package com.example.vaadbaitv3;

import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

public class reports extends AppCompatActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawerLayout;
    private EndDrawerToggle drawerToggle ;
    String storage;
    ImageView profile;
    String email;
    ImageButton upload_report;
    ListView report_list;
    androidx.appcompat.widget.Toolbar toolbar;
    NavigationView navigationView;
    SharedPreferences sp;
    SharedPreferences.Editor editor;
    SharedPreferences address;
    StorageReference storageReference;
    ArrayList <full_report> listReports;
    ArrayList<String> list_view_reports;
    EditText enter_article_report,enter_explain_report;
    ImageView upload_camera;
    Uri uri;
    String picname;
    DatabaseReference databaseReference;
    FirebaseDatabase firebaseDatabase;
    String explain_alert_reports;
    StorageReference firebaseStorage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reports);
        drawerLayout = findViewById(R.id.drawerLayout);
        toolbar = (androidx.appcompat.widget.Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        navigationView=findViewById(R.id.navigationView);
        navigationView.inflateMenu(R.menu.side_menu);
        View headerView=  navigationView.inflateHeaderView(R.layout.headerfile);
        profile=headerView.findViewById(R.id.profile_header);
        profile.setOnClickListener(this);

        address = getSharedPreferences("address", 0);
        enter_article_report=findViewById(R.id.enter_article_report);
        enter_explain_report=findViewById(R.id.enter_explain_report);
        upload_camera=findViewById(R.id.upload_camera);
        upload_camera.setOnClickListener(this);
        listReports = new ArrayList<>();
        upload_report=findViewById(R.id.upload_reports);
        upload_report.setOnClickListener(this);
        report_list=findViewById(R.id.list_of_reports);
        firebaseDatabase = FirebaseDatabase.getInstance();
        list_view_reports = new ArrayList<>();
        databaseReference = firebaseDatabase.getReference("Address/" +
                address.getString("city2","").trim()+"/"+
                address.getString("street2","").trim()+"/"+
                address.getString("num_address2","")).child("reports");


           databaseReference.get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
               @Override
               public void onSuccess(DataSnapshot dataSnapshot) {
                   for (DataSnapshot ds : dataSnapshot.getChildren()) {
                       list_view_reports.add(ds.child("report_n").getValue().toString());
                       explain_alert_reports=ds.child("details").getValue().toString();
                       listReports.add( ds.getValue(full_report.class));

                   }
                   ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(reports.this, R.layout.listview, R.id.textViewLiad, list_view_reports);
                   report_list.setAdapter(arrayAdapter);

               }


           });


        sp = getSharedPreferences("save", 0);
        email=sp.getString("email","");
        TextView name=headerView.findViewById(R.id.menu_name);
        name.setText("ברוך הבא, "+sp.getString("name",""));
        storage=sp.getString("storage","");
        if(storage.equals("0")){
            profile.setImageResource(R.drawable.profile);
        }
        else{
            storageReference = FirebaseStorage.getInstance().getReference("image/" + email.replace('.', ' '));
            storageReference = storageReference.child(storage);
            storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Picasso.with(reports.this).load(uri).into(profile);
                }
            });
        }
        navigationView.setNavigationItemSelectedListener(this);
        drawerToggle = new EndDrawerToggle(drawerLayout, toolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(drawerToggle);

        report_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String subject= (String) adapterView.getItemAtPosition(i).toString();
                String uri = listReports.get(i).getUri();
                firebaseStorage = FirebaseStorage.getInstance().getReference("image/" + email.replace('.', ' '))
                        .child(uri);
                AlertDialog.Builder alert = new AlertDialog.Builder(reports.this);

                alert.setMessage("נושא הדיווח :" +"   "+subject+ "       "+" פירוט:"+"                     "+explain_alert_reports)
                        .setPositiveButton("הצג תמונת דיווח", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                firebaseStorage.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        Intent intent = new Intent(android.content.Intent.ACTION_VIEW);
                                        intent.setDataAndType( uri,"image/*");
                                        try {
                                            startActivity(Intent.createChooser(intent, "Choose an Application:"));
                                        } catch (Exception e) {
                                            Toast.makeText(reports.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                        }
                                    }

                                });
                            }
                        })
                        .setNegativeButton("חזור", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        alert.setOnCancelListener(new DialogInterface.OnCancelListener()
                        {
                            @Override
                            public void onCancel(DialogInterface dialog)
                            {


                                dialog.dismiss();

                            }
                        });
                    }
                });





                AlertDialog alert1 = alert.create();
                alert1.show();

            }
        });



    }

    @Override
    public void onClick(View view) {
        if (upload_report==view){
            Thread thread = new Thread() {
                public void run() {
                    try {
                        synchronized (reports.this) {
                            SharedPreferences address = getSharedPreferences("address", 0);
                            DatabaseReference address_report = FirebaseDatabase.getInstance().getReference("Address/"
                                    + (address.getString("city2", "").trim())
                                    + "/" + (address.getString("street2", "").trim()) +
                                    "/" + (address.getString("num_address2", ""))).child("reports");
                            storageReference = FirebaseStorage.getInstance().getReference("image/" + email.replace('.', ' '));
                            storageReference = storageReference.child(picname);
                            storageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                    // databaseReference.child(email).child("profile_name").setValue(picname);

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                }
                            });

                            address_report.get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {

                                @Override
                                public void onSuccess(DataSnapshot dataSnapshot) {
                                    for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                                        full_report s = singleSnapshot.getValue(full_report.class);
                                        listReports.add(s);

                                    }
                                    full_report s = new full_report(picname, enter_article_report.getText().toString(),enter_explain_report.getText().toString());

                                    listReports.add(s);
                                    HashMap<String, Object> map = new HashMap<>();
                                    map.put("reports", listReports);
                                    DatabaseReference address_report = FirebaseDatabase.getInstance().getReference("Address/"
                                            + (address.getString("city2", "").trim())
                                            + "/" + (address.getString("street2", "").trim()) +
                                            "/" + (address.getString("num_address2", "")));
                                    address_report.updateChildren(map);
                                }
                            })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(reports.this, "fail", Toast.LENGTH_LONG).show();
                                        }
                                    });

                            address_report.get().addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(reports.this, "fail", Toast.LENGTH_LONG).show();
                                }
                            });

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            };
            thread.start();
        }
        if(upload_camera==view){
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), 0);
        }


    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null && requestCode == 0) {

            uri = data.getData();
            picname = System.currentTimeMillis() + "." + getFileExtension(uri);
            upload_camera.setImageURI(uri);
        }

    }
    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id=item.getItemId();
        if (id == R.id.settings) {
            Intent intent = new Intent(reports.this, Settings.class);
            startActivity(intent);
            finish();
        }
        else if(id==R.id.home_page){
            Intent intent = new Intent(reports.this, HomePage.class);
            startActivity(intent);
            finish();
        }
        else if(id==R.id.disconnect){
            showPopup();
        }
        else if(id==R.id.bills_building){
            Intent intent = new Intent(reports.this, bills.class);
            startActivity(intent);
            finish();
        }
        else if(id==R.id.votes){
            Intent intent = new Intent(reports.this, votes.class);
            startActivity(intent);
            finish();
        }
        else if(id==R.id.reports){
            Intent intent = new Intent(reports.this, reports.class);
            startActivity(intent);
            finish();
        }
        else if(id==R.id.chat_building){
            Intent intent = new Intent(reports.this, Chat.class);
            startActivity(intent);
            finish();
        }
        else if(id==R.id.wallet_building){
            Intent intent = new Intent(reports.this, walletpage.class);
            startActivity(intent);
            finish();
        }
        return false;
    }
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }
    private void showPopup() {
        AlertDialog.Builder alert = new AlertDialog.Builder(reports.this);
        alert.setMessage("אתה בטוח שאתה רוצה להתנתק?")
                .setPositiveButton("התנתקות", new DialogInterface.OnClickListener()                 {

                    public void onClick(DialogInterface dialog, int which) {

                        logout(); // Last step. Logout function

                    }
                }).setNegativeButton("בטל", null);

        AlertDialog alert1 = alert.create();
        alert1.show();
    }

    private void logout() {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signOut();
        startActivity(new Intent(this, login.class));
        finish();
    }
}