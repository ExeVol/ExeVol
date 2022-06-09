package com.example.vaadbaitv3;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class reports extends AppCompatActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawerLayout;
    private EndDrawerToggle drawerToggle ;
    String storage;
    ImageView profile;
    Uri uri;
    String picname,email;

    androidx.appcompat.widget.Toolbar toolbar;
    NavigationView navigationView;
    SharedPreferences sp;
    SharedPreferences.Editor editor;
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    StorageReference storageReference;
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
        sp = getSharedPreferences("save", 0);
        editor = sp.edit();
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
    }

    @Override
    public void onClick(View view) {

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
            Intent intent = new Intent(reports.this, login.class);
            startActivity(intent);
            finish();
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
}