package com.example.vaadbaitv3;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

public class SurveyCreatePage extends AppCompatActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawerLayout;
    private EndDrawerToggle drawerToggle ;
    androidx.appcompat.widget.Toolbar toolbar;
    NavigationView navigationView;
    String storage;
    ImageView profile;
    String email;
    EditText article_ed,explain_ed;
    SharedPreferences sp;
    SharedPreferences.Editor editor;
    StorageReference storageReference;
    ImageButton survey_create;
    ArrayList<seker> listSeker;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey_create_page);
        drawerLayout = findViewById(R.id.drawerLayout);
        toolbar = (androidx.appcompat.widget.Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        navigationView=findViewById(R.id.navigationView);
        navigationView.inflateMenu(R.menu.side_menu);
        View headerView=  navigationView.inflateHeaderView(R.layout.headerfile);
        profile=headerView.findViewById(R.id.profile_header);
        profile.setOnClickListener(this);
        sp = getSharedPreferences("save", 0);
        editor = sp.edit();
        listSeker = new ArrayList<>();
        TextView name=headerView.findViewById(R.id.menu_name);
        survey_create=findViewById(R.id.create_survey_bt);
        article_ed=findViewById(R.id.enter_article);
        explain_ed=findViewById(R.id.enter_explain);
        survey_create.setOnClickListener(this);
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
                    Picasso.with(SurveyCreatePage.this).load(uri).into(profile);
                }
            });
        }
        navigationView.setNavigationItemSelectedListener(this);
        drawerToggle = new EndDrawerToggle(drawerLayout, toolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(drawerToggle);
    }

    @Override
    public void onClick(View view) {
       Thread thread=new Thread() {
            public void run() { try {synchronized (SurveyCreatePage.this) {
                SharedPreferences address = getSharedPreferences("address", 0);
                DatabaseReference address_seker= FirebaseDatabase.getInstance().getReference("Address/"
                        + (address.getString("city2", "").trim())
                        + "/" + (address.getString("street2", "").trim()) +
                        "/" + (address.getString("num_address2", ""))).child("seker");
                address_seker.get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {

                    @Override
                public void onSuccess(DataSnapshot dataSnapshot) {
                    for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {

                        seker temp = singleSnapshot.getValue(seker.class);
                        listSeker.add(temp);

                    }
                        seker s = new seker(article_ed.getText().toString(),explain_ed.getText().toString(),0,0);

                        listSeker.add(s) ;
                        HashMap<String, Object> map = new HashMap<>();
                        map.put("seker", listSeker);
                        DatabaseReference address_seker= FirebaseDatabase.getInstance().getReference("Address/"
                                + (address.getString("city2", "").trim())
                                + "/" + (address.getString("street2", "").trim()) +
                                "/" + (address.getString("num_address2", "")));
                        address_seker.updateChildren(map);
                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(SurveyCreatePage.this, "fail", Toast.LENGTH_LONG).show();
                    }
                });

                address_seker.get().addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(SurveyCreatePage.this, "fail", Toast.LENGTH_LONG).show();
                    }
                });

            }} catch (Exception e) {
                e.printStackTrace();
            }

            }
        };
        thread.start();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id=item.getItemId();
        if (id == R.id.settings) {
            Intent intent = new Intent(SurveyCreatePage.this, Settings.class);
            startActivity(intent);
            finish();
        }
        else if(id==R.id.home_page){
            Intent intent = new Intent(SurveyCreatePage.this, HomePage.class);
            startActivity(intent);
            finish();
        }
        else if(id==R.id.disconnect){
            showPopup();
        }
        else if(id==R.id.bills_building){
            Intent intent = new Intent(SurveyCreatePage.this, bills.class);
            startActivity(intent);
            finish();
        }
        else if(id==R.id.votes){
            Intent intent = new Intent(SurveyCreatePage.this, votes.class);
            startActivity(intent);
            finish();
        }
        else if(id==R.id.reports){
            Intent intent = new Intent(SurveyCreatePage.this, reports.class);
            startActivity(intent);
            finish();
        }
        else if(id==R.id.wallet_building){
            Intent intent = new Intent(SurveyCreatePage.this, walletpage.class);
            startActivity(intent);
            finish();
        }
        else if(id==R.id.chat_building){
            Intent intent = new Intent(SurveyCreatePage.this, Chat.class);
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
        AlertDialog.Builder alert = new AlertDialog.Builder(SurveyCreatePage.this);
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
        startActivity(new Intent(this, login.class));
        finish();
    }
}