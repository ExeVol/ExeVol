package com.example.vaadbaitv3;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class HomePage extends AppCompatActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener, OnFailureListener {
    private DrawerLayout drawerLayout;
    private EndDrawerToggle drawerToggle ;
    String storage;
    ImageView profile;
    ImageView bt1,bt2,bt3;
    androidx.appcompat.widget.Toolbar toolbar;
    NavigationView navigationView;
    SharedPreferences sp;
    SharedPreferences.Editor editor;
    StorageReference storageReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        bt1=findViewById(R.id.imagebutton1);
        bt2=findViewById(R.id.imagebutton2);
        bt3=findViewById(R.id.imagebutton3);
        bt1.setOnClickListener(this);
        bt2.setOnClickListener(this);
        bt3.setOnClickListener(this);
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
        TextView name=headerView.findViewById(R.id.menu_name);
        name.setText("ברוך הבא, "+sp.getString("name",""));
        storage=sp.getString("storage","");
        if(storage.equals("0")){
            profile.setImageResource(R.drawable.profile);
        }
        else{
            storageReference = FirebaseStorage.getInstance().getReference("image/" + sp.getString("email","").replace('.', ' '));
            storageReference = storageReference.child(storage);
            storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Picasso.with(HomePage.this).load(uri).into(profile);
                }
            });
        }
        navigationView.setNavigationItemSelectedListener(this);
        drawerToggle = new EndDrawerToggle(drawerLayout, toolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(drawerToggle);

    }

    @Override
    public void onClick(View view) {
        if(bt1==view){
            Intent intent = new Intent(HomePage.this, reports.class);
            startActivity(intent);
        }
        if(bt2==view){
            Intent intent = new Intent(HomePage.this,walletpage.class);
            startActivity(intent);
        }
        if(bt3==view){
            Intent intent = new Intent(HomePage.this, Chat.class);
            startActivity(intent);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id=item.getItemId();
        if (id == R.id.settings) {
            Intent intent = new Intent(HomePage.this, Settings.class);
            startActivity(intent);
            finish();
        }
        else if(id==R.id.home_page){
            Intent intent = new Intent(HomePage.this, HomePage.class);
            startActivity(intent);
            finish();
        }
        else if(id==R.id.disconnect){
            showPopup();
        }
        else if(id==R.id.bills_building){
            Intent intent = new Intent(HomePage.this, bills.class);
            startActivity(intent);
            finish();
        }
        else if(id==R.id.votes){
            Intent intent = new Intent(HomePage.this, votes.class);
            startActivity(intent);
            finish();
        }
        else if(id==R.id.reports){
            Intent intent = new Intent(HomePage.this, reports.class);
            startActivity(intent);
            finish();
        }
        else if(id==R.id.wallet_building){
            Intent intent = new Intent(HomePage.this, walletpage.class);
            startActivity(intent);
            finish();
        }
        else if(id==R.id.chat_building){
            Intent intent = new Intent(HomePage.this, Chat.class);
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

    public void onFailure(@NonNull Exception e) {
        Toast.makeText(HomePage.this, e.getMessage(), Toast.LENGTH_LONG).show();
    }
    private void showPopup() {
        AlertDialog.Builder alert = new AlertDialog.Builder(HomePage.this);
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