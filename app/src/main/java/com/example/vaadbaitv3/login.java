package com.example.vaadbaitv3;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class login extends AppCompatActivity implements View.OnClickListener {
    EditText email_login,password_login;
    Button login_button,forgot_pass;
    String email, password, key,name="",storage,type_guest;
    Thread thread;

    FirebaseAuth Auth = FirebaseAuth.getInstance();
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference;
    private FirebaseAuth mAuth;
    SharedPreferences address;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        email_login=findViewById(R.id.email_login);
        password_login=findViewById(R.id.password_login);
        login_button=findViewById(R.id.login_button);
        forgot_pass=findViewById(R.id.forgotpass);
        login_button.setOnClickListener(this);
        mAuth = FirebaseAuth.getInstance();
        forgot_pass.setOnClickListener(this);
        forgot_pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRecoverPasswordDialog();
            }
        });
    }
    ProgressDialog loadingBar;

    private void showRecoverPasswordDialog() {
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("שחזר סיסמא");
        LinearLayout linearLayout=new LinearLayout(this);
        final EditText emailet= new EditText(this);

        // write the email using which you registered
        emailet.setText("הכנס מייל");
        emailet.setMinEms(16);
        emailet.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        linearLayout.addView(emailet);
        linearLayout.setPadding(10,10,10,10);
        builder.setView(linearLayout);

        // Click on Recover and a email will be sent to your registered email id
        builder.setPositiveButton("Recover", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String email=emailet.getText().toString().trim();
                beginRecovery(email);
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    private void beginRecovery(String email) {
        loadingBar=new ProgressDialog(this);
        loadingBar.setMessage("Sending Email....");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();

        // calling sendPasswordResetEmail
        // open your email and write the new
        // password and then you can login
        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                loadingBar.dismiss();
                if(task.isSuccessful())
                {
                    // if isSuccessful then done message will be shown
                    // and you can change the password
                    Toast.makeText(login.this,"Done sent",Toast.LENGTH_LONG).show();
                }
                else {
                    Toast.makeText(login.this,"Error Occured",Toast.LENGTH_LONG).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                loadingBar.dismiss();
                Toast.makeText(login.this,"Error Failed",Toast.LENGTH_LONG).show();
            }
        });
    }


    public void pageregister(View view) {
        startActivity(new Intent(login.this , register.class));

    }

    @Override
    public void onClick(View view) {
        if(view==login_button){
            if (email_login.getText().toString().contains("@gmail.com"))
            {password=password_login.getText().toString();
            email=email_login.getText().toString();
            databaseReference=firebaseDatabase.getReference("Users");
            thread=new Thread() {
                public void run() {
                    try { synchronized (login.this)
                    {
                databaseReference.get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                    @Override
                    public void onSuccess(DataSnapshot dataSnapshot) {
                        for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                          if (singleSnapshot.getKey().equals(email.replace(".", " "))){
                           DefaultUser u = singleSnapshot.getValue(DefaultUser.class);
                                name=u.getName();
                                key=u.getKey();
                                storage=u.getStorage();
                                type_guest=Integer.valueOf(u.getType_guest()).toString();
                                if (type_guest.equals("3") || type_guest.equals("2")) {
                                    address = getSharedPreferences("address", 0);
                                    SharedPreferences.Editor editor = address.edit();
                                    editor.putString("city2",u.getCity2());
                                    editor.putString("street2",u.getStreet2());
                                    editor.putString("num_address2",u.getNum_address2());
                                    editor.commit();

                                }
                           }
                        }
                    }
                });

                login.this.wait(1000);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(login.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()) {

                                    SharedPreferences sp;
                                    sp = getSharedPreferences("save", 0);
                                    SharedPreferences.Editor editor = sp.edit();
                                    Toast.makeText(login.this, " אתה מחובר"+" "+name, Toast.LENGTH_LONG).show();
                                    editor.putString("email", email.toString());
                                    editor.putString("pass", password.toString());
                                    editor.putString("name", name.toString());
                                    editor.putString("storage", storage.toString());
                                    editor.putString("key", key.toString());
                                    editor.putString("type_guest", type_guest.toString());
                                    editor.commit();
                                    Intent intent = new Intent(login.this, HomePage.class);
                                    startActivity(intent);
                                    finish();

                                }

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(login.this, e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                });

            }
            }catch (InterruptedException e) { }


            }
            };


            thread.start();}
            else{
                Toast.makeText(login.this, "שים לב שאתה מכניס כתובת מייל מלאה!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}