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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class Settings extends AppCompatActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawerLayout;
    private EndDrawerToggle drawerToggle ;

    ImageView profile,profile_update;
    Uri uri;
    String picname,email;
    TextView tv_user,tv_address;
    ImageButton logout_button;
    androidx.appcompat.widget.Toolbar toolbar;
    NavigationView navigationView;
    SharedPreferences sp;
    SharedPreferences Address;
    SharedPreferences.Editor editor;
    StorageReference storageReference;
    ImageButton save_pic;
    String current_url;
    DatabaseReference databaseReference;
    FirebaseDatabase firebaseDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        drawerLayout = findViewById(R.id.drawerLayout);
        toolbar = (androidx.appcompat.widget.Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        navigationView=findViewById(R.id.navigationView);
        navigationView.inflateMenu(R.menu.side_menu);
        View headerView=  navigationView.inflateHeaderView(R.layout.headerfile);
        profile=headerView.findViewById(R.id.profile_header);
        profile_update=findViewById(R.id.profile_update);
        profile_update.setOnClickListener(this);
        sp = getSharedPreferences("save", 0);
        Address=getSharedPreferences("address",0);
        editor = sp.edit();
        tv_user=findViewById(R.id.tv_user);
        tv_address=findViewById(R.id.tv_address);
        logout_button=findViewById(R.id.logout_button);
        logout_button.setOnClickListener(this);
        TextView name=headerView.findViewById(R.id.menu_name);
        name.setText("???????? ??????, "+sp.getString("name",""));
        navigationView.setNavigationItemSelectedListener(this);
        drawerToggle = new EndDrawerToggle(drawerLayout, toolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(drawerToggle);
        tv_user.setText("???? ??????????:"+" "+sp.getString("name",""));
        tv_address.setText("?????????? ??????????:"+" "+Address.getString("city2","").trim()+
                "-"+Address.getString("street2","").trim()+
                "-"+Address.getString("num_address2","").trim());
        email=sp.getString("email","");




    }


    @Override
    public void onClick(View view) {
        if(view==logout_button){
            showPopup();
        }
        if(profile_update==view){
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
            profile.setImageURI(uri);
            picname = System.currentTimeMillis() + "." + getFileExtension(uri);
            storageReference = FirebaseStorage.getInstance().getReference("image/" + email.replace('.', ' '));
            storageReference = storageReference.child(picname);
            storageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                profile_update.setImageURI(uri);
                current_url=storageReference.child(String.valueOf(uri)).getDownloadUrl().toString();
                databaseReference.child(email).child("profile_name").setValue(picname);

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });
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
            Intent intent = new Intent(Settings.this, Settings.class);
            startActivity(intent);
            finish();
        }
        else if(id==R.id.home_page){
            Intent intent = new Intent(Settings.this, HomePage.class);
            startActivity(intent);
            finish();
        }
        else if(id==R.id.disconnect){
            showPopup();
        }
        else if(id==R.id.bills_building){
            Intent intent = new Intent(Settings.this, bills.class);
            startActivity(intent);
            finish();
        }
        else if(id==R.id.votes){
            Intent intent = new Intent(Settings.this, votes.class);
            startActivity(intent);
            finish();
        }
        else if(id==R.id.reports){
            Intent intent = new Intent(Settings.this, reports.class);
            startActivity(intent);
            finish();
        }
        else if(id==R.id.chat_building){
            Intent intent = new Intent(Settings.this, Chat.class);
            startActivity(intent);
            finish();
        }
        else if(id==R.id.wallet_building){
            Intent intent = new Intent(Settings.this, walletpage.class);
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
        AlertDialog.Builder alert = new AlertDialog.Builder(Settings.this);
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