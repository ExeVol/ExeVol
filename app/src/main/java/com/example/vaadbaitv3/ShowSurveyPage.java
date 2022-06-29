package com.example.vaadbaitv3;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ShowSurveyPage extends AppCompatActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawerLayout;
    private EndDrawerToggle drawerToggle ;
    androidx.appcompat.widget.Toolbar toolbar;
    NavigationView navigationView;
    String storage;
    ImageView profile;
    String email;
    SharedPreferences sp;
    SharedPreferences.Editor editor;
    StorageReference storageReference;
    ListView survey_list;
    FirebaseDatabase firebaseDatabase ;
    DatabaseReference databaseReference;
    SharedPreferences address;
   ArrayList<String> listSeker;
    int baad_sum=0,neged_sum=0;
    String explain_alert;
    Button back_to;

   @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_survey_page);
        drawerLayout = findViewById(R.id.drawerLayout);
        toolbar = (androidx.appcompat.widget.Toolbar) findViewById(R.id.toolbar);

        firebaseDatabase = FirebaseDatabase.getInstance();
        setSupportActionBar(toolbar);
        navigationView = findViewById(R.id.navigationView);
        navigationView.inflateMenu(R.menu.side_menu);
        View headerView = navigationView.inflateHeaderView(R.layout.headerfile);
        profile = headerView.findViewById(R.id.profile_header);
        profile.setOnClickListener(this);
        sp = getSharedPreferences("save", 0);
        editor = sp.edit();
        TextView name = headerView.findViewById(R.id.menu_name);
        name.setText("ברוך הבא, " + sp.getString("name", ""));
        storage = sp.getString("storage", "");
        if (storage.equals("0")) {
            profile.setImageResource(R.drawable.profile);
        } else {
            storageReference = FirebaseStorage.getInstance().getReference("image/" + email.replace('.', ' '));
            storageReference = storageReference.child(storage);
            storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Picasso.with(ShowSurveyPage.this).load(uri).into(profile);
                }
            });
        }
        back_to=findViewById(R.id.back_to);
        back_to.setOnClickListener(this);
        navigationView.setNavigationItemSelectedListener(this);
        drawerToggle = new EndDrawerToggle(drawerLayout, toolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(drawerToggle);
        address = getSharedPreferences("address", 0);
        survey_list=findViewById(R.id.survey_list);
       listSeker = new ArrayList<>();


       databaseReference = firebaseDatabase.getReference("Address/" +
               address.getString("city2","").trim()+"/"+
               address.getString("street2","").trim()+"/"+
               address.getString("num_address2","")).child("seker");
       databaseReference.get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
           @Override
           public void onSuccess(DataSnapshot dataSnapshot) {
               for (DataSnapshot ds: dataSnapshot.getChildren()) {
                   listSeker.add(ds.child("nosee").getValue().toString());
                   baad_sum= Integer.parseInt(ds.child("baad").getValue().toString());
                   neged_sum= Integer.parseInt((ds.child("neged").getValue().toString()));
                   explain_alert=ds.child("explain").getValue().toString();
               }
               ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(ShowSurveyPage.this, R.layout.listview,R.id.textViewLiad,listSeker);
               survey_list.setAdapter(arrayAdapter);

        }


    });
        survey_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            String subject= (String) adapterView.getItemAtPosition(i).toString();
            AlertDialog.Builder alert = new AlertDialog.Builder(ShowSurveyPage.this);
            alert.setMessage("נושא הסקר :" +"   "+subject+
                     "       "+" פירוט:"+"           "+explain_alert+
                    "                                         "+"בעד:"+baad_sum+","+" נגד: "+neged_sum)


                    .setPositiveButton("בעד", new DialogInterface.OnClickListener()                 {
                        public void onClick(DialogInterface dialog, int which) {
                            baad_sum++;
                            databaseReference.child(String.valueOf(i)).child("baad").setValue(baad_sum);

                        }
                    }).setNegativeButton("נגד", new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface dialogInterface, int j) {
                    neged_sum++;
                    databaseReference.child(String.valueOf(i)).child("neged").setValue(neged_sum);
                }
            }).setNeutralButton("חזור", new DialogInterface.OnClickListener() {
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
        if (back_to==view){
            Intent intent = new Intent(ShowSurveyPage.this, votes.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id=item.getItemId();
        if (id == R.id.settings) {
            Intent intent = new Intent(ShowSurveyPage.this, Settings.class);
            startActivity(intent);
            finish();
        }
        else if(id==R.id.home_page){
            Intent intent = new Intent(ShowSurveyPage.this, HomePage.class);
            startActivity(intent);
            finish();
        }
        else if(id==R.id.disconnect){
            showPopup();
        }
        else if(id==R.id.bills_building){
            Intent intent = new Intent(ShowSurveyPage.this, bills.class);
            startActivity(intent);
            finish();
        }
        else if(id==R.id.votes){
            Intent intent = new Intent(ShowSurveyPage.this, votes.class);
            startActivity(intent);
            finish();
        }
        else if(id==R.id.reports){
            Intent intent = new Intent(ShowSurveyPage.this, reports.class);
            startActivity(intent);
            finish();
        }
        else if(id==R.id.wallet_building){
            Intent intent = new Intent(ShowSurveyPage.this, walletpage.class);
            startActivity(intent);
            finish();
        }
        else if(id==R.id.chat_building){
            Intent intent = new Intent(ShowSurveyPage.this, Chat.class);
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
        AlertDialog.Builder alert = new AlertDialog.Builder(ShowSurveyPage.this);
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