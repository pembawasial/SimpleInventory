package com.dintaaditya.simpleinventory;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.dintaaditya.simpleinventory.Model.ItemLog;
import com.dintaaditya.simpleinventory.Model.Shipment;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

import javax.annotation.Nullable;

public class ShipmentActivity extends AppCompatActivity {
    TextView tvName, tvSKU, tvStock;
    ImageView imgItem;
    EditText edtReceiver, edtQuantity;
    String SKU;
    DocumentReference itemDetail, shipmentRef;
    Spinner spinnerProvince, spinnerCity, spinnerPostCode;
    Button btnSendItem;
    LinearLayout progressBar;
    FirebaseFirestore firestore;
    int stock;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shipment);

        SKU = getIntent().getStringExtra("SKU");

        tvName = findViewById(R.id.tv_name);
        tvSKU = findViewById(R.id.tv_SKU);
        tvStock = findViewById(R.id.tv_stock);
        edtReceiver = findViewById(R.id.edt_receiver);
        edtQuantity = findViewById(R.id.edt_quantity);
        imgItem = findViewById(R.id.img_item);
        btnSendItem = findViewById(R.id.btn_send_item);
        spinnerProvince = findViewById(R.id.spinner_province);
        spinnerCity = findViewById(R.id.spinner_city);
        spinnerPostCode = findViewById(R.id.spinner_post_code);
        progressBar = findViewById(R.id.progress_bar);

        firestore = FirebaseFirestore.getInstance();
        itemDetail = firestore.document("Item/" + SKU);
    }


    @Override
    protected void onStart() {
        super.onStart();
        getProvince();
        progressBar.setVisibility(View.VISIBLE);

        btnSendItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                saveShipmentData();
            }
        });


        itemDetail.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                String name = documentSnapshot.getString("name");
                String image = documentSnapshot.getString("image");
                stock = documentSnapshot.getLong("stock").intValue();

                tvSKU.setText("SKU: " + SKU);
                tvName.setText(name);
                tvStock.setText("Stock available: " + stock);
                Glide.with(ShipmentActivity.this).load(image).apply(RequestOptions.circleCropTransform()).into(imgItem);
            }
        });


    }

    private void saveShipmentData() {
        String receiver_name = edtReceiver.getText().toString().trim();
        final int quantity = Integer.parseInt(edtQuantity.getText().toString().trim());
        String addressProvince = spinnerProvince.getSelectedItem().toString();
        String addressCity = spinnerCity.getSelectedItem().toString();
        String addressPostCode = spinnerPostCode.getSelectedItem().toString();
        String address = addressProvince + ", " + addressCity + ", " + addressPostCode;
        Shipment newShipment = new Shipment(SKU, address, receiver_name, quantity);
        final int previous_stock = stock;
        if (quantity < stock) {
            firestore.collection("Shipment").add(newShipment)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            //change stock
                            final int last_stock = stock - quantity;
                            shipmentRef = firestore.document("Item/" + SKU);
                            shipmentRef.update("stock", last_stock)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            //new log item
                                            ItemLog newLog = new ItemLog(previous_stock, quantity, last_stock, "Outcoming");
                                            firestore.collection("Item/" + SKU + "/Log").add(newLog)
                                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                        @Override
                                                        public void onSuccess(DocumentReference documentReference) {
                                                            Toast.makeText(ShipmentActivity.this, "New Log Added Successfully", Toast.LENGTH_SHORT).show();
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Toast.makeText(ShipmentActivity.this, "Failed to add Log" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                            progressBar.setVisibility(View.GONE);
                                            onBackPressed();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(ShipmentActivity.this, "Failed change stock!!," + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ShipmentActivity.this, "Failed to insert data!!, " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(this, "insufficient Stock !!", Toast.LENGTH_LONG).show();
        }
    }

    private void getProvince() {
        final ArrayList provinceArray = new ArrayList<>();
        final ArrayList provinceKeysArray = new ArrayList<>();

        final String provinceUrl = "https://kodepos-2d475.firebaseio.com/list_propinsi.json?print=pretty";
        StringRequest provinseRequest = new StringRequest(Request.Method.GET, provinceUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject object = new JSONObject(response);
                    Iterator<String> keys = object.keys();
                    while (keys.hasNext()) {
                        String keyValue = keys.next();
                        String province = object.getString(keyValue);

                        provinceKeysArray.add(keyValue);
                        provinceArray.add(province);
                    }
                    spinnerProvince.setAdapter(new ArrayAdapter<String>(ShipmentActivity.this, android.R.layout.simple_list_item_1, provinceArray));
                    spinnerProvince.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            int pos = position;
                            String cityIndex = String.valueOf(provinceKeysArray.get(pos));
                            getCity(cityIndex);
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });

                } catch (JSONException e) {
                    Toast.makeText(ShipmentActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ShipmentActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        Volley.newRequestQueue(this).add(provinseRequest);
    }

    private void getCity(String index) {
        final ArrayList cityArray = new ArrayList<>();
        final ArrayList cityKeysArray = new ArrayList<>();
        final String cityUrl = "https://kodepos-2d475.firebaseio.com/list_kotakab/" + index + ".json?print=pretty";
        StringRequest cityRequest = new StringRequest(Request.Method.GET, cityUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject object = new JSONObject(response);
                    Iterator<String> keys = object.keys();
                    while (keys.hasNext()) {
                        String keyValue = keys.next();
                        String province = object.getString(keyValue);

                        cityKeysArray.add(keyValue);
                        cityArray.add(province);
                    }
                    spinnerCity.setAdapter(new ArrayAdapter<String>(ShipmentActivity.this, android.R.layout.simple_list_item_1, cityArray));
                    spinnerCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            int pos = position;
                            String postCodeIndex = String.valueOf(cityKeysArray.get(pos));
                            getPostCode(postCodeIndex);
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });
                } catch (JSONException e) {
                    Toast.makeText(ShipmentActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ShipmentActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        Volley.newRequestQueue(this).add(cityRequest);
    }

    private void getPostCode(String index) {
        final ArrayList postCodeArray = new ArrayList<>();
        final String postCodeUrl = "https://kodepos-2d475.firebaseio.com/kota_kab/" + index + ".json?print=pretty";
        StringRequest postCodeRequest = new StringRequest(Request.Method.GET, postCodeUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray itemArray = new JSONArray(response);
                    for (int i = 0; i < itemArray.length(); i++) {
                        JSONObject object = itemArray.getJSONObject(i);
                        String kecamatan = object.getString("kecamatan");
                        String kelurahan = object.getString("kelurahan");
                        String postCode = object.getString("kodepos");
                        postCodeArray.add(kecamatan + ", " + kelurahan + " (" + postCode + ")");
                    }
                    spinnerPostCode.setAdapter(new ArrayAdapter<String>(ShipmentActivity.this, android.R.layout.simple_list_item_1, postCodeArray));
                    progressBar.setVisibility(View.GONE);
                } catch (JSONException e) {
                    Toast.makeText(ShipmentActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ShipmentActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        Volley.newRequestQueue(this).add(postCodeRequest);
    }


}
