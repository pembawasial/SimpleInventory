package com.dintaaditya.simpleinventory;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.dintaaditya.simpleinventory.Model.Item;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.soundcloud.android.crop.Crop;

import java.io.File;

public class InputActivity extends AppCompatActivity implements View.OnClickListener {
    EditText edtName, edtStock;
    Button btnSave, btnGetPic;
    ImageButton btnPlus, btnMinus;
    ImageView imgItem;
    Uri imageFile;
    FirebaseFirestore firestore;
    StorageReference storageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input);

        edtName = findViewById(R.id.edt_name);
        edtStock = findViewById(R.id.edt_stock);
        btnSave = findViewById(R.id.btn_save);
        btnGetPic = findViewById(R.id.btn_get_picture);
        btnPlus = findViewById(R.id.btn_plus);
        btnMinus = findViewById(R.id.btn_minus);
        imgItem = findViewById(R.id.img_item);

        btnGetPic.setOnClickListener(this);
        btnPlus.setOnClickListener(this);
        btnMinus.setOnClickListener(this);
        btnSave.setOnClickListener(this);

        firestore = FirebaseFirestore.getInstance();
        storageRef = FirebaseStorage.getInstance().getReference();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_get_picture:
                getPicture();
                break;
            case R.id.btn_plus:
                plusStock();
                break;
            case R.id.btn_minus:
                minStock();
                break;
            case R.id.btn_save:
                saveData();
                break;
        }
    }

    private void getPicture() {
        Crop.pickImage(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent result) {
        if (requestCode == Crop.REQUEST_PICK && resultCode == RESULT_OK) {
            beginCrop(result.getData());
        } else if (requestCode == Crop.REQUEST_CROP) {
            handleCrop(resultCode, result);
        }
    }

    private void beginCrop(Uri data) {
        Uri destination = Uri.fromFile(new File(getCacheDir(), "cropped"));
        Crop.of(data, destination).asSquare().start(this);
    }

    private void handleCrop(int resultCode, Intent result) {
        imageFile = Crop.getOutput(result);
        if (resultCode == RESULT_OK) {
            Glide.with(getApplicationContext()).load(imageFile).diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).apply(RequestOptions.circleCropTransform()).into(imgItem);
        } else if (resultCode == Crop.RESULT_ERROR) {
            Toast.makeText(this, Crop.getError(result).getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void saveData() {
        String name = edtName.getText().toString().trim();
        int stock = Integer.parseInt(edtStock.getText().toString());
        Item newItem = new Item(name, "null", stock);
        firestore.collection("Item").add(newItem)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(final DocumentReference documentReference) {
                        storageRef.child(documentReference.getId()).putFile(imageFile)
                                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        storageRef.child(documentReference.getId()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {
                                                String image = uri.toString();
                                                firestore.document("Item/" + documentReference.getId()).update("image", image);
                                                Toast.makeText(InputActivity.this, "Data Inserted Successfully", Toast.LENGTH_LONG).show();
                                                startActivity(new Intent(InputActivity.this, ItemActivity.class));
                                                finish();
                                            }
                                        });
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getApplicationContext(), "Failed to upload image!!, " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(InputActivity.this, "Failed to insert data!!, " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }


    private void plusStock() {
    }

    private void minStock() {
    }
}
