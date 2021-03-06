package com.example.vaadbaitv3;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class votes extends AppCompatActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawerLayout;
    private EndDrawerToggle drawerToggle ;
    String storage;
    ImageView profile;
    String email;
    androidx.appcompat.widget.Toolbar toolbar;
    NavigationView navigationView;
    SharedPreferences sp;
    SharedPreferences.Editor editor;
    StorageReference storageReference;
    ImageButton survey_create,show_serveys;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_votes);
        drawerLayout = findViewById(R.id.drawerLayout);
        toolbar = (androidx.appcompat.widget.Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        navigationView=findViewById(R.id.navigationView);
        navigationView.inflateMenu(R.menu.side_menu);
        View headerView=  navigationView.inflateHeaderView(R.layout.headerfile);
        profile=headerView.findViewById(R.id.profile_header);
        profile.setOnClickListener(this);
        survey_create=findViewById(R.id.create_survey);
        show_serveys=findViewById(R.id.show_surveys);
        survey_create.setOnClickListener(this);
        show_serveys.setOnClickListener(this);
        sp = getSharedPreferences("save", 0);
        editor = sp.edit();
        TextView name=headerView.findViewById(R.id.menu_name);
        name.setText("???????? ??????, "+sp.getString("name",""));
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
                    Picasso.with(votes.this).load(uri).into(profile);
                }
            });
        }
        navigationView.setNavigationItemSelectedListener(this);
        drawerToggle = new EndDrawerToggle(drawerLayout, toolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(drawerToggle);



        survey_create.setVisibility(View.INVISIBLE);
        if (sp.getString("type_guest", "").equals("3")||sp.getString("type_guest", "").equals("2"))
            survey_create.setVisibility(View.VISIBLE);

    }

    @Override
    public void onClick(View view) {
        if(survey_create==view){
            Intent intent = new Intent(votes.this, SurveyCreatePage.class);
            startActivity(intent);
            finish();
        }
        if(show_serveys==view){
            Intent intent = new Intent(votes.this, ShowSurveyPage.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id=item.getItemId();
        if (id == R.id.settings) {
            Intent intent = new Intent(votes.this, Settings.class);
            startActivity(intent);
            finish();
        }
        else if(id==R.id.home_page){
            Intent intent = new Intent(votes.this, HomePage.class);
            startActivity(intent);
            finish();
        }
        else if(id==R.id.disconnect){
            showPopup();
        }
        else if(id==R.id.bills_building){
            Intent intent = new Intent(votes.this, bills.class);
            startActivity(intent);
            finish();
        }
        else if(id==R.id.votes){
            Intent intent = new Intent(votes.this, votes.class);
            startActivity(intent);
            finish();
        }
        else if(id==R.id.reports){
            Intent intent = new Intent(votes.this, reports.class);
            startActivity(intent);
            finish();
        }
        else if(id==R.id.wallet_building){
            Intent intent = new Intent(votes.this, walletpage.class);
            startActivity(intent);
            finish();
        }
        else if(id==R.id.chat_building){
            Intent intent = new Intent(votes.this, Chat.class);
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
        AlertDialog.Builder alert = new AlertDialog.Builder(votes.this);
        alert.setMessage("?????? ???????? ???????? ???????? ?????????????")
                .setPositiveButton("??????????????", new DialogInterface.OnClickListener()                 {

                    public void onClick(DialogInterface dialog, int which) {

                        logout(); // Last step. Logout function

                    }
                }).setNegativeButton("??????", null);

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