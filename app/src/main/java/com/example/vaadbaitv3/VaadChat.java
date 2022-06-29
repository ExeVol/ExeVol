package com.example.vaadbaitv3;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class VaadChat extends AppCompatActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawerLayout;
    private EndDrawerToggle drawerToggle ;
    String storage;
    ImageView profile;
    String email;
    androidx.appcompat.widget.Toolbar toolbar;
    NavigationView navigationView;
    SharedPreferences sp;
    SharedPreferences address;
    SharedPreferences.Editor editor;
    StorageReference storageReference;
    ProgressDialog p;
    EditText ed_message;
    FirebaseAuth firebaseAuth;
    msg m;
    Button send1;


    ArrayAdapter<String> adapter;
    ArrayList<String> list;
    ArrayList<msg> listpost;
    ArrayAdapter<msg> lpadapter;
    ListView myListView;
    DatabaseReference databaseReference;
    FirebaseDatabase firebaseDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vaad_chat);
        drawerLayout = findViewById(R.id.drawerLayout);
        toolbar = (androidx.appcompat.widget.Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        navigationView=findViewById(R.id.navigationView);
        navigationView.inflateMenu(R.menu.side_menu);
        View headerView=  navigationView.inflateHeaderView(R.layout.headerfile);
        profile=headerView.findViewById(R.id.profile_header);
        profile.setOnClickListener(this);
        sp = getSharedPreferences("save", 0);
        address=getSharedPreferences("address",0);
        editor = sp.edit();
        send1=findViewById(R.id.send1);
        send1.setOnClickListener(this);
        ed_message=findViewById(R.id.ed_message);
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
                    Picasso.with(VaadChat.this).load(uri).into(profile);
                }
            });
        }
        navigationView.setNavigationItemSelectedListener(this);
        drawerToggle = new EndDrawerToggle(drawerLayout, toolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(drawerToggle);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Address/" +
                address.getString("city2","").trim()+"/"+
                address.getString("street2","").trim()+"/"+
                address.getString("num_address2","")).child("msg");


        m = new msg();

        list= new ArrayList<>();
        listpost= new ArrayList<>();
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list);
        myListView= (ListView) findViewById(R.id.listviewformessage1);
        lpadapter = new ArrayAdapter<msg>(this, android.R.layout.simple_list_item_1, listpost);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    m = ds.getValue(msg.class);
                    if (m.getName_sender().equals(sp.getString("name", "")))
                        list.add("from you: " + m.getText());
                    else
                        list.add("from " + m.getName_sender() + ": " + m.getText());

                    listpost.add(m);

                }
                myListView.setAdapter(adapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }



    public void createMSG() {
        if (isValidate()) {
            p = new ProgressDialog(this);
            p.setMessage("שולח..");
            p.show();
            //לשם חיבור לבסיס הנתונים
            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
            //הצבעה לתת ענף של משתמשים ("טבלה")

            DatabaseReference myref1 = firebaseDatabase.getReference("Address/"+address.getString("city2","")+"/"
                    + address.getString("street2","")+"/"+
                    address.getString("num_address2","")).child("msg");
            m=new msg(sp.getString("name",""), "vaad", ed_message.getText().toString());
            Toast.makeText(VaadChat.this, "נשלח", Toast.LENGTH_SHORT).show();
            myref1.push().setValue(m);
            this.recreate();

        }
    }

    public boolean isValidate() {
        if (ed_message.getText().toString().length() == 0) {
            ed_message.setError("אי אפשר לשלוח הודעות ריקות");
           ed_message.setFocusable(true);
            return false;
        }
        if (firebaseAuth.getInstance().getCurrentUser()==null)
            return false;
        return true;
    }

    @Override
    public void onClick(View view) {
            if (view==send1){
            createMSG();
            }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id=item.getItemId();
        if (id == R.id.settings) {
            Intent intent = new Intent(VaadChat.this, Settings.class);
            startActivity(intent);
            finish();
        }
        else if(id==R.id.home_page){
            Intent intent = new Intent(VaadChat.this, HomePage.class);
            startActivity(intent);
            finish();
        }
        else if(id==R.id.disconnect){
            showPopup();
        }
        else if(id==R.id.bills_building){
            Intent intent = new Intent(VaadChat.this, bills.class);
            startActivity(intent);
            finish();
        }
        else if(id==R.id.votes){
            Intent intent = new Intent(VaadChat.this, votes.class);
            startActivity(intent);
            finish();
        }
        else if(id==R.id.reports){
            Intent intent = new Intent(VaadChat.this, reports.class);
            startActivity(intent);
            finish();
        }
        else if(id==R.id.chat_building){
            Intent intent = new Intent(VaadChat.this, Chat.class);
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
        AlertDialog.Builder alert = new AlertDialog.Builder(VaadChat.this);
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