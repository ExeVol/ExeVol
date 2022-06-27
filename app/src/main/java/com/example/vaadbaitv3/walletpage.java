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
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class walletpage extends AppCompatActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawerLayout;
    private EndDrawerToggle drawerToggle ;
    String storage;
    ImageView profile;
    String email;
    String amount_amount;
    Thread thread;
    TextView tv_money;
    ImageButton add_money,remove_money;
    EditText add_money_ed,remove_money_ed;
    int sum_cash=0;
    androidx.appcompat.widget.Toolbar toolbar;
    NavigationView navigationView;
    SharedPreferences sp;
    SharedPreferences.Editor editor;
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);
        drawerLayout = findViewById(R.id.drawerLayout);
        tv_money = findViewById(R.id.tv_money);
        add_money = findViewById(R.id.add_button);
        add_money_ed = findViewById(R.id.add_money_ed);
        remove_money = findViewById(R.id.remove_button);
        remove_money_ed = findViewById(R.id.remove_money_ed);
        remove_money.setOnClickListener(this);
        add_money.setOnClickListener(this);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
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
                    Picasso.with(walletpage.this).load(uri).into(profile);
                }
            });
        }
        navigationView.setNavigationItemSelectedListener(this);
        drawerToggle = new EndDrawerToggle(drawerLayout, toolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(drawerToggle);


        add_money.setVisibility(View.INVISIBLE);
        add_money_ed.setVisibility(View.INVISIBLE);
        remove_money.setVisibility(View.INVISIBLE);
        remove_money_ed.setVisibility(View.INVISIBLE);
        if (sp.getString("type_guest", "").equals("3") || sp.getString("type_guest", "").equals("2")) {
            add_money.setVisibility(View.VISIBLE);
            add_money_ed.setVisibility(View.VISIBLE);
            remove_money.setVisibility(View.VISIBLE);
            remove_money_ed.setVisibility(View.VISIBLE);
        }


        amount_money();
    }

    public void amount_money(){
        thread=new Thread() {
            public void run() { try {synchronized (walletpage.this) {
                SharedPreferences address = getSharedPreferences("address", 0);
                DatabaseReference address_amount = firebaseDatabase.getReference("Address/"
                        + (address.getString("city2", "").trim())
                        + "/" + (address.getString("street2", "").trim()) +
                        "/" + (address.getString("num_address2", "")));
                address_amount.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            if (ds.getKey().equals("amount")){
                            amount_amount = ds.getValue().toString();
                            sum_cash=Integer.parseInt(amount_amount);
                            tv_money.setText(amount_amount);}


                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                Toast.makeText(walletpage.this, amount_amount, Toast.LENGTH_LONG).show();
            }} catch (Exception e) {
                    e.printStackTrace();
                }

            }
        };
        thread.start();
    }

    @Override
    public void onClick(View view) {
    if(view==add_money){
        String value=add_money_ed.getText().toString();
        int desiredValue= Integer.parseInt(value);
        sum_cash+=desiredValue;
        tv_money.setText(String.valueOf(sum_cash));
        SharedPreferences Address=getSharedPreferences("address", 0);
        FirebaseDatabase database=FirebaseDatabase.getInstance();
        DatabaseReference databaseReference=database.getReference("Address/"+Address.getString("city2","")+"/"+
                Address.getString("street2","")+"/"+
                Address.getString("num_address2","")).child("amount");

        databaseReference.setValue(sum_cash);
        add_money_ed.setText("");

    }

    if(view==remove_money){
        String value=remove_money_ed.getText().toString();
        int desiredValue= Integer.parseInt(value);
        sum_cash-=desiredValue;
        tv_money.setText(String.valueOf(sum_cash));
        SharedPreferences Address=getSharedPreferences("address", 0);
        FirebaseDatabase database=FirebaseDatabase.getInstance();
        DatabaseReference databaseReference=database.getReference("Address/"+Address.getString("city2","")+"/"+
                Address.getString("street2","")+"/"+
                Address.getString("num_address2","")).child("amount");
        databaseReference.setValue(sum_cash);
        remove_money_ed.setText("");
    }
}

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id=item.getItemId();
        if (id == R.id.settings) {
            Intent intent = new Intent(walletpage.this, Settings.class);
            startActivity(intent);
            finish();
        }
        else if(id==R.id.home_page){
            Intent intent = new Intent(walletpage.this, HomePage.class);
            startActivity(intent);
            finish();
        }
        else if(id==R.id.disconnect){
            showPopup();
        }
        else if(id==R.id.bills_building){
            Intent intent = new Intent(walletpage.this, bills.class);
            startActivity(intent);
            finish();
        }
        else if(id==R.id.votes){
            Intent intent = new Intent(walletpage.this, votes.class);
            startActivity(intent);
            finish();
        }
        else if(id==R.id.reports){
            Intent intent = new Intent(walletpage.this, reports.class);
            startActivity(intent);
            finish();
        }
        else if(id==R.id.chat_building){
            Intent intent = new Intent(walletpage.this, Chat.class);
            startActivity(intent);
            finish();
        }
        else if(id==R.id.wallet_building){
            Intent intent = new Intent(walletpage.this, walletpage.class);
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
        AlertDialog.Builder alert = new AlertDialog.Builder(walletpage.this);
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