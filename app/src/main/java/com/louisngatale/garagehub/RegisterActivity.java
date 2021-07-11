package com.louisngatale.garagehub;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.louisngatale.garagehub.adapters.PicturePreviewRecyclerAdapter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "Register";
    Button register,login,add_images;
    EditText fullName,company,email,password, phone, description;
    FirebaseAuth mAuth;
    FirebaseFirestore mDb;
    String fNameVal, companyVal, emailVal, pwdVal,descriptionVal, phoneVal;
    ProgressBar loading;
    RecyclerView imagePreviewRecView;
    PicturePreviewRecyclerAdapter previewAdapter;
    private static final int REQUEST_IMAGE_CAPTURE = 102,
            EXTERNAL_STORAGE_PERMISSION_CODE = 103,
            PICK_IMAGE = 104;
    String currentPhotoPath;
    private ProgressDialog mProgress;
    ArrayList<String> images;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        mDb = FirebaseFirestore.getInstance();

        fullName = findViewById(R.id.full_name_value);
        company = findViewById(R.id.company_value);
        email = findViewById(R.id.email_value);
        password = findViewById(R.id.password_value);
        register = findViewById(R.id.register);
        login = findViewById(R.id.login);
        phone = findViewById(R.id.company_phone_value);
        description = findViewById(R.id.details_value);
        loading = findViewById(R.id.loading);
        add_images = findViewById(R.id.add_images);

        login.setOnClickListener(v -> {
            Intent login = new Intent(RegisterActivity.this,LoginActivity.class);
            startActivity(login);
            finish();
        });

        register.setOnClickListener(v -> {
            loading.setVisibility(View.VISIBLE);
            fNameVal = fullName.getText().toString();
            companyVal = company.getText().toString();
            emailVal = email.getText().toString();
            pwdVal = password.getText().toString();
            phoneVal = phone.getText().toString();
            descriptionVal = description.getText().toString();

            mAuth.createUserWithEmailAndPassword(emailVal,pwdVal)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        String uid = task.getResult().getUser().getUid();

                        createProfile(uid, emailVal);
                    }else {
                        loading.setVisibility(View.GONE);
                        Toast.makeText(this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
        });

        imagePreviewRecView = findViewById(R.id.image_views);

        previewAdapter = new PicturePreviewRecyclerAdapter(this);

        imagePreviewRecView.setAdapter(previewAdapter);
        imagePreviewRecView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));

        add_images.setOnClickListener(this);

//        takePhoto.setOnClickListener(this);
    }

    /**
     * @param requestCode Request code for Intent
     * @param resultCode Result code form Intent
     * @param data Result data from Intent
     * @Author: Eng. Louis Ngatale
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && data != null) {
            switch (requestCode) {
                case REQUEST_IMAGE_CAPTURE:
                    if (resultCode == RESULT_OK) {
                        addImage(data, "Camera");
                    }
                    break;
                case PICK_IMAGE:
                    if (resultCode == RESULT_OK) {
                        addImage(data, "Picker");
                    }
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + requestCode);
            }
        }else {
            Toast.makeText(this, "Error obtaining image!", Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.add_images:
                addImages();
                break;
            default:
                break;
        }
    }

    private void addImages() {
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");

        startActivityForResult(Intent.createChooser(galleryIntent,"SELECT IMAGE"),PICK_IMAGE);
    }


    @SuppressLint("QueryPermissionsNeeded")
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        } catch (ActivityNotFoundException e) {
            // display error state to the user
        }
    }


    private void addImage(@Nullable Intent data, String type) {
//        TODO: Toggle empty image placeholder
        switch (type) {
            case "Camera":
//                Bundle extras = Objects.requireNonNull(data).getExtras();
                Bitmap imageBitmap = (Bitmap) data.getExtras().get("data");

                // CALL THIS METHOD TO GET THE URI FROM THE BITMAP
                Uri imageUri = getImageUri(this.getApplicationContext(), imageBitmap);

                previewAdapter.getImages().add(imageUri);

                previewAdapter.notifyDataSetChanged();

//                Get actual Path
                break;
            case "Picker":
                Uri selectedImg = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(Objects.requireNonNull(this).getContentResolver(),selectedImg);
                    Uri test = MediaStore.Images.Media.getContentUri(String.valueOf(selectedImg));
                    if (bitmap!= null) {
                        previewAdapter.getImages().add(selectedImg);
                        previewAdapter.notifyDataSetChanged();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + type);
        }
    }

    //    TODO:Handle camera intent error
    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    private void upload_image_documents(String documentId){
        StorageReference mImageStorage = FirebaseStorage.getInstance().getReference();
        DocumentReference mDbRef = mDb.collection("users").document(documentId);

        CompletableFuture.supplyAsync(() -> {
            // Upload Images in asynchronous mode
            images.forEach(img -> {
                StorageReference filepath = mImageStorage.child("Documents").child(createImageName() + ".jpg");
                filepath
                    .putFile(Uri.parse(img))
                    .addOnSuccessListener(taskSnapshot -> filepath
                        .getDownloadUrl()
                        .addOnSuccessListener(uri -> {
                            String downloadUri = uri.toString();
                            mDbRef.update("documentImages", FieldValue.arrayUnion(downloadUri)).addOnCompleteListener(task -> {
                            });
                        }).addOnFailureListener(e -> Log.d(TAG, "onFailure: " + e)));
            });
            return null;
        })
        .thenAccept(value -> Log.d(TAG, "uploadImages: Done"));
    }
    public String createImageName(){
        UUID uuid = UUID.randomUUID();
        @SuppressLint("SimpleDateFormat")
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        return "HOUSE_" + timeStamp+uuid;
    }

    private void createProfile(String uid, String email) {
        HashMap<String,String> companies = new HashMap<>();
        images = new ArrayList<>();
        companies.put("owner",uid);
        companies.put("company",companyVal);
        companies.put("description",descriptionVal);
        companies.put("phone",phoneVal);
        previewAdapter.getImages()
                .forEach(image -> images.add(String.valueOf(image)));
        ArrayList<String> emptyImages = new ArrayList<>();

        // Create a companies document to store general company data
        mDb.collection("companies")
            .add(companies)
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()){
                    String id = task.getResult().getId();
                    HashMap<String,String> user = new HashMap<>();
                    user.put("email",email);
                    user.put("uid",uid);
                    user.put("company",companyVal);
                    user.put("companyId",id);
                    user.put("Full Name",fNameVal);

                    // Create a users document to store user data for quick retrieval
                    mDb.collection("users")
                        .document(uid)
                        .set(user)
                        .addOnCompleteListener(task1 -> {
                            loading.setVisibility(View.GONE);
                            if (task1.isSuccessful()){
                                // Upload images if not empty
                                if (!images.isEmpty()){
                                    upload_image_documents(uid);
                                }
                                Intent dashboard = new Intent(RegisterActivity.this,DashboardActivity.class);
                                startActivity(dashboard);
                                finish();
                            }else {
                                Toast.makeText(this, task1.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                }
            });
    }
}