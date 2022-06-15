package com.example.vaadbaitv3;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class register extends AppCompatActivity implements   AdapterView.OnItemSelectedListener , View.OnClickListener {
    EditText Full_name, email, pass, passcheck, phone, num_address;
    Spinner cityy, streett;
    Button submit_register;
    RadioButton btnn1, btnn2, btnn3;
    ArrayList<String> samplee = new ArrayList<>();
    ArrayList<String> sampleev2 = new ArrayList<>();
    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    ProgressDialog p;
    DatabaseReference myref, myref1;
    boolean flag;
    int type_guest;
    Thread thread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        phone = findViewById(R.id.phone);
        pass = findViewById(R.id.pass);
        email = findViewById(R.id.email);
        cityy = findViewById(R.id.cityy);
        Full_name = findViewById(R.id.Full_name);
        streett = findViewById(R.id.streett);
        num_address = findViewById(R.id.num_address);
        btnn1 = findViewById(R.id.btnn1);
        btnn2 = findViewById(R.id.btnn2);
        btnn3 = findViewById(R.id.btnn3);
        btnn3.setOnClickListener(this);
        btnn2.setOnClickListener(this);
        btnn1.setOnClickListener(this);
        readCityData();
        ArrayAdapter<String> newadapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, samplee);
        cityy.setAdapter(newadapter);
        cityy.setOnItemSelectedListener(this);
        streett.setOnItemSelectedListener(this);
        submit_register = findViewById(R.id.submit_Register);
        submit_register.setOnClickListener(this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        myref = firebaseDatabase.getReference("Address/");
        myref1 = firebaseDatabase.getReference("Address/");

    }


    private List<CityData> CityDataread = new ArrayList<>();

    private void readCityData() {
        InputStream is = getResources().openRawResource(R.raw.data);
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(is, Charset.forName("windows-1255"))
        );
        String line = "";
        try {
            reader.readLine();
            while ((line = reader.readLine()) != null) {
                Log.d("register", "Line:" + line);
                String[] tokens = line.split(",");
                CityData sample = new CityData();
                sample.setCity_Name(tokens[1].trim());
                sample.setId_Street(tokens[2]);
                sample.setStreet_name(tokens[3]);
                CityDataread.add(sample);
                if (!samplee.contains(tokens[1]))
                    samplee.add(tokens[1]);
                Log.d("register", "Just created:" + sample);

            }
        } catch (IOException e) {
            Log.wtf("register", "Error while reading data file" + line, e);
            e.printStackTrace();
        }
    }

    private void showSteerts(String city) {

        sampleev2 = new ArrayList<>();
        for (int i = 0; i < CityDataread.size() && CityDataread.get(i).getCity_Name().compareTo(city.trim()) <= 0; i++) {
            if (CityDataread.get(i).getCity_Name().equals(city.trim())) {

                sampleev2.add(CityDataread.get(i).getStreet_name());
            }

        }
        ArrayAdapter<String> newadapterv2 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, sampleev2);
        streett.setAdapter(newadapterv2);
    }


    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        if (adapterView == cityy) {
            String item = samplee.get(i);

            if (i != 0)
                showSteerts(item);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    public void back(View view) {

        startActivity(new Intent(register.this, login.class));
    }

    public void createUser() {

        // if (isVaildate()) {


            myref1.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    try {
                        for (DataSnapshot singleSnapshot : snapshot.getChildren()) {
                            if (singleSnapshot.child("city2").getValue().toString().equals(cityy.getSelectedItem().toString()) &&
                                    singleSnapshot.child("street2").getValue().toString().equals(streett.getSelectedItem().toString()) &&
                                    singleSnapshot.child("num_address2").getValue().toString().equals(num_address.getText().toString())) {
                                if (singleSnapshot.child("type_guest").getValue().toString().equals(type_guest)) {
                                    flag = false;
                                    throw new Exception("קיים בכתובת זו מנהל וועד");

                                }
                            }
                        }
                    } catch (Exception e) {
                        Toast.makeText(register.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            p = new ProgressDialog(this);
            p.setMessage("בתהליך רישום....");
            p.show();
            thread = new Thread() {
                public void run() {
                    try {
                        synchronized (register.this) {
                            firebaseAuth.createUserWithEmailAndPassword(email.getText().toString(), pass.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        try {
                                            if (!flag) {
                               /* myref = firebaseDatabase.getReference("Address/"+cityy.getSelectedItem().toString()+"/"+
                                        streett.getSelectedItem().toString()+"/"+
                                        num_address.getText().toString()+"/Users").push();
*/
                                                                                                                                                                         myref = firebaseDatabase.getReference("Users").child(email.getText().toString().replace(".", " "));

                                                                                                                                                                         DefaultUser u = new DefaultUser(Full_name.getText().toString()
                                                                                                                                                                                 , email.getText().toString()
                                                                                                                                                                                 , pass.getText().toString()
                                                                                                                                                                                 , phone.getText().toString()
                                                                                                                                                                                 , cityy.getSelectedItem().toString()
                                                                                                                                                                                 , streett.getSelectedItem().toString()
                                                                                                                                                                                 , num_address.getText().toString(), "0", type_guest, myref.getKey());
                                                                                                                                                                         myref.setValue(u);
                                                                                                                                                                         ArrayList<String> usersInAddress = new ArrayList<>();
                                                                                                                                                                         myref = firebaseDatabase.getReference("Address").child(cityy.getSelectedItem().toString()).child(
                                                                                                                                                                                 streett.getSelectedItem().toString()).child( num_address.getText().toString()).child("users");
                                                                                                                                                                         myref.addValueEventListener(new ValueEventListener() {
                                                                                                                                                                             @Override
                                                                                                                                                                             public void onDataChange(@NonNull DataSnapshot snapshot) {

                                                                                                                                                                                 for (DataSnapshot ds : snapshot.getChildren()) {
                                                                                                                                                                                    // for (String s : (ArrayList<String>)ds.getValue(ArrayList.class))
                                                                                                                                                                                     usersInAddress.add(ds.getValue(String.class));

                                                                                                                                                                                 }
                                                                                                                                                                                 usersInAddress.add(email.getText().toString().replace(".", " "));
                                                                                                                                                                                 Toast.makeText(register.this, ""+usersInAddress.size(), Toast.LENGTH_LONG).show();
                                                                                                                                                                                 myref = firebaseDatabase.getReference("Address").child(cityy.getSelectedItem().toString()).child(
                                                                                                                                                                                         streett.getSelectedItem().toString()).child( num_address.getText().toString());
                                                                                                                                                                                 HashMap<String, Object> map = new HashMap<>();
                                                                                                                                                                                 map.put("users", usersInAddress);
                                                                                                                                                                                 myref.updateChildren(map);
                                                                                                                                                                             }

                                                                                                                                                                             @Override
                                                                                                                                                                             public void onCancelled(@NonNull DatabaseError error) {

                                                                                                                                                                             }
                                                                                                                                                                         });



                                                                                                                                                                         /*  .child(email.getText().toString().replace("."," "));*/
                                                                                                                                                                         /* myref.setValue(email.getText().toString().replace("."," "));*/
                                                                                                                                                                     }
                                                                                                                                                                 } catch (Exception e) {
                                                                                                                                                                     Toast.makeText(register.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                                                                                                                                                 }
                                                                                                                                                                 p.dismiss();
                                                                                                                                                                 Toast.makeText(register.this, "הרשמה בוצעה בהצלחה", Toast.LENGTH_LONG).show();
                                                                                                                                                                 startActivity(new Intent(register.this, login.class));
                                                                                                                                                             }
                                                                                                                                                         }
                                                                                                                                                     }
                            ).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(register.this, "הרשמה לא  בוצעה בהצלחה", Toast.LENGTH_LONG).show();
                                    Toast.makeText(register.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            });
                            //}
                     /*    catch(Exception e){
                            Toast.makeText(register.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }*/

                            register.this.wait(1000);

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                }

                            });

                        } } catch (InterruptedException e) { }
                    }



        };


        thread.start();
                }







    @Override
    public void onClick(View view) {
        if (btnn1 == view) {
            type_guest = 1;
        } else if (btnn2 == view) {
            type_guest = 2;
        } else if (btnn3 == view) {
            type_guest = 3;
        }
        if (view == submit_register) {
            if (pass.getText().toString().length() < 6) {
                pass.setError("הסיסמא צריכה להיות לפחות 6 תווים");
                pass.setFocusable(true);
            } else if (num_address == null) {
                num_address.setError("חובה להקליד מספר בניין");
                pass.setFocusable(true);

            } else if ((Full_name.toString().contains("@") ||
                    Full_name.toString().contains("!") ||
                    Full_name.toString().contains("$") ||
                    Full_name.toString().contains("%") ||
                    Full_name.toString().contains("^") ||
                    Full_name.toString().contains("&") ||
                    Full_name.toString().contains("*") ||
                    Full_name.toString().contains(")") ||
                    Full_name.toString().contains("(") ||
                    Full_name.toString().contains("_") ||
                    Full_name.toString().contains("-") ||
                    Full_name.toString().contains("+") ||
                    Full_name.toString().contains("="))) {
                email.setError("לא ניתן להכניס תווים אלה במייל");
                email.setFocusable(true);
                 }
        } else {

            createUser();
        }
    }
                                       }





/* windows-1255 */