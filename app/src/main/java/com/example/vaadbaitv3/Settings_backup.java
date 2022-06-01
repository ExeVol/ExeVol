package com.example.vaadbaitv3;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class Settings_backup extends AppCompatActivity implements View.OnClickListener {
    ImageView profile;
    Button back;
    Uri uri;
    String picname,email;
    SharedPreferences sp;
    SharedPreferences.Editor editor;
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    StorageReference storageReference;
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        back=findViewById(R.id.back);
       // profile=findViewById(R.id.profile);//
        profile.setOnClickListener(this);
        sp = getSharedPreferences("save", 0);
        editor = sp.edit();
        email=sp.getString("email","");
    }
    @Override
    public void onClick(View view) {
        if(profile==view){
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), 0);
        }
        if ((view==back)){
            startActivity(new Intent(Settings_backup.this, HomePage.class));
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
}