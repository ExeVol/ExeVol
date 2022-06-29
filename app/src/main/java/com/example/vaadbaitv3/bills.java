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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class bills extends AppCompatActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawerLayout;
    private EndDrawerToggle drawerToggle ;
    String storage;
    ImageView profile;
    Uri uri;
    String picname,email;
    ImageView upload;
    Uri imageuri = null;
    androidx.appcompat.widget.Toolbar toolbar;
    NavigationView navigationView;
    SharedPreferences sp;
    SharedPreferences.Editor editor;
    StorageReference storageReference;
    ListView billsList;
    SharedPreferences address;
    ImageView btn_upload;
    ArrayList<String> listOfPdf;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bills);
        address = getSharedPreferences("address", 0);
        drawerLayout = findViewById(R.id.drawerLayout);
        toolbar = (androidx.appcompat.widget.Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        billsList=findViewById(R.id.mList);

        billsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String url_bill= (String) adapterView.getItemAtPosition(i).toString();
                storageReference = FirebaseStorage.getInstance().getReference("documents/" +
                        address.getString("city2","").trim()+"/"+
                        address.getString("street2","").trim()+"/"+
                        address.getString("num_address2",""));
                storageReference = storageReference.child(url_bill);
                AlertDialog.Builder alert = new AlertDialog.Builder(bills.this);

                alert.setMessage("חשבונית מוצגת")
                        .setPositiveButton("הצג חשבונית", new DialogInterface.OnClickListener()                 {

                            public void onClick(DialogInterface dialog, int which) {
                                storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        Intent intent = new Intent(android.content.Intent.ACTION_VIEW);
                                        intent.setDataAndType( uri,"application/pdf");
                                        try {
                                            startActivity(Intent.createChooser(intent, "Choose an Application:"));
                                        } catch (Exception e) {
                                            Toast.makeText(bills.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                        }
                                    }

                                });

                               /* pdfView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                                pdfView.fromAsset((billsList.getSelectedItem().toString()));
                                pdfView.loadPages();
                                alert.setView(pdfView);*/

                            }
                        }).setNegativeButton("חזרה", null);

                AlertDialog alert1 = alert.create();
                alert1.show();

            }
        });
        navigationView=findViewById(R.id.navigationView);
        navigationView.inflateMenu(R.menu.side_menu);
        View headerView=  navigationView.inflateHeaderView(R.layout.headerfile);
        profile=headerView.findViewById(R.id.profile_header);
        profile.setOnClickListener(this);
        sp = getSharedPreferences("save", 0);
        editor = sp.edit();
        btn_upload=findViewById(R.id.uploadpdf);
        btn_upload.setOnClickListener(this);
        TextView name=headerView.findViewById(R.id.menu_name);
        name.setText("ברוך הבא, "+sp.getString("name",""));
        storage=sp.getString("storage","");
        downLoadBills();

        if(storage.equals("0")){
            profile.setImageResource(R.drawable.update_profile);
        }
        else{
            storageReference = FirebaseStorage.getInstance().getReference("image/" + email.replace('.', ' '));
            storageReference = storageReference.child(storage);
            storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Picasso.with(bills.this).load(uri).into(profile);
                }
            });
        }
        navigationView.setNavigationItemSelectedListener(this);
        drawerToggle = new EndDrawerToggle(drawerLayout, toolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(drawerToggle);
        btn_upload.setVisibility(View.INVISIBLE);
        if (sp.getString("type_guest", "").equals("3")||sp.getString("type_guest", "").equals("2"))
            btn_upload.setVisibility(View.VISIBLE);

        /*-----------image-upload-----------------*/
        upload = findViewById(R.id.uploadpdf);

        // After Clicking on this we will be
        // redirected to choose pdf
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

                // We will be redirected to choose pdf
                galleryIntent.setType("application/pdf");
                startActivityForResult(galleryIntent, 1);
            }
        });
        listOfPdf = new ArrayList<>();


    }
    ProgressDialog dialog;






    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {

            // Here we are initialising the progress dialog box
            dialog = new ProgressDialog(this);
            dialog.setMessage("Uploading");

            // this will show message uploading
            // while pdf is uploading
            dialog.show();
            imageuri = data.getData();
            final String timestamp = "" + System.currentTimeMillis();
            StorageReference storageReference =  FirebaseStorage.getInstance().getReference("documents/"
                    +address.getString("city2","").trim()+"/"
                    +address.getString("street2","").trim()+"/"
                    + address.getString("num_address2","").trim());
            final String messagePushID = timestamp;
            Toast.makeText(bills.this, imageuri.toString(), Toast.LENGTH_SHORT).show();

            // Here we are uploading the pdf in firebase storage with the name of current time
            final StorageReference filepath = storageReference.child(imageuri.getLastPathSegment() + "." + "pdf");
            Toast.makeText(bills.this, filepath.getName(), Toast.LENGTH_SHORT).show();
            filepath.putFile(imageuri).continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return filepath.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        // After uploading is done it progress
                        // dialog box will be dismissed
                        dialog.dismiss();
                        Uri uri = task.getResult();
                        String myurl;
                        myurl = uri.toString();
                        Toast.makeText(bills.this, "Uploaded Successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        dialog.dismiss();
                        Toast.makeText(bills.this, "UploadedFailed", Toast.LENGTH_SHORT).show();
                    }
                }
            });}
    }



    @Override
    public void onClick(View view) {



    }
 public void downLoadBills(){

     StorageReference listRef =   FirebaseStorage.getInstance().getReference("documents/"
             +address.getString("city2","").trim()+"/"
             +address.getString("street2","").trim()+"/"
             + address.getString("num_address2","").trim());

     listRef.listAll()
             .addOnSuccessListener(new OnSuccessListener<ListResult>() {
                 @Override
                 public void onSuccess(ListResult listResult) {


                     for (StorageReference item : listResult.getItems()) {
                         listOfPdf.add(item.getName().toString());
                     }
                     ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(bills.this, R.layout.listview,R.id.textViewLiad,listOfPdf);
                     billsList.setAdapter(arrayAdapter);


                     Toast.makeText(bills.this, listOfPdf.toString(),Toast.LENGTH_LONG).show();


                 }

             })
             .addOnFailureListener(new OnFailureListener() {
                 @Override
                 public void onFailure(@NonNull Exception e) {
                     // Uh-oh, an error occurred!
                 }
             });




 }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id=item.getItemId();
        if (id == R.id.settings) {
            Intent intent = new Intent(bills.this, Settings.class);
            startActivity(intent);
            finish();
        }
        else if(id==R.id.home_page){
            Intent intent = new Intent(bills.this, HomePage.class);
            startActivity(intent);
            finish();
        }
        else if(id==R.id.disconnect){
            Intent intent = new Intent(bills.this, login.class);
            startActivity(intent);
            finish();
        }
        else if(id==R.id.bills_building){
            Intent intent = new Intent(bills.this, bills.class);
            startActivity(intent);
            finish();
        }
        else if(id==R.id.votes){
            Intent intent = new Intent(bills.this, votes.class);
            startActivity(intent);
            finish();
        }
        else if(id==R.id.reports){
            Intent intent = new Intent(bills.this, reports.class);
            startActivity(intent);
            finish();
        }
        else if(id==R.id.wallet_building){
            Intent intent = new Intent(bills.this, walletpage.class);
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
        AlertDialog.Builder alert = new AlertDialog.Builder(bills.this);
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












