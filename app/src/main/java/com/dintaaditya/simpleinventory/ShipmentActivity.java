package com.dintaaditya.simpleinventory;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
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
    String SKU;
    DocumentReference itemDetail;
    Spinner spinnerProvince, spinnerCity, spinnerPostCode;
    Button btnSendItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shipment);

        SKU = getIntent().getStringExtra("SKU");

        tvName = findViewById(R.id.tv_name);
        tvSKU = findViewById(R.id.tv_SKU);
        tvStock = findViewById(R.id.tv_stock);
        imgItem = findViewById(R.id.img_item);
        btnSendItem = findViewById(R.id.btn_send_item);
        spinnerProvince = findViewById(R.id.spinner_province);
        spinnerCity = findViewById(R.id.spinner_city);
        spinnerPostCode = findViewById(R.id.spinner_post_code);

        btnSendItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                getProvince();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        getProvince();
//        getCity();
//        getPostCode();
        itemDetail = FirebaseFirestore.getInstance().document("Item/" + SKU);
        itemDetail.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                String name = documentSnapshot.getString("name");
                String image = documentSnapshot.getString("image");
                Integer stock = documentSnapshot.getLong("stock").intValue();

                tvSKU.setText("SKU: " + SKU);
                tvName.setText(name);
                tvStock.setText("Stock available: " + stock);
                Glide.with(ShipmentActivity.this).load(image).apply(RequestOptions.circleCropTransform()).into(imgItem);
            }
        });


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
