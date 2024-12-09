package com.example.luxdrive_1;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ScooterActivity extends AppCompatActivity {

    private EditText searchInput;
    private LinearLayout scootersList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scooter);

        // Initialize UI components
        searchInput = findViewById(R.id.search_input);
        scootersList = findViewById(R.id.scooters_list);

        // Fetch scooters from the server
        fetchScooters();

        // Search functionality
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterScooters(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void fetchScooters() {
        String url = "http://x.x.x.x:5001/api/scooters";
        RequestQueue queue = Volley.newRequestQueue(this);

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    scootersList.removeAllViews(); // Clear previous list
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject scooter = response.getJSONObject(i);
                            // Check if fields exist before accessing
                            if (scooter.has("scooterId") && scooter.has("scooterType")) {
                                int id = scooter.getInt("scooterId");
                                String type = scooter.getString("scooterType");

                                addScooterToView(id, type);
                            } else {
                                // Log and skip invalid entries
                                Toast.makeText(ScooterActivity.this,"Invalid scooter data at index " + i,Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(ScooterActivity.this, "Error parsing scooter data", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                error -> {
                    String errorMessage = getDetailedErrorMessage(error);
                    Toast.makeText(ScooterActivity.this, "Failed to fetch scooters: " + errorMessage, Toast.LENGTH_LONG).show();
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                SharedPreferences preferences = getSharedPreferences("auth", MODE_PRIVATE);
                String token = preferences.getString("jwt_token", null);

                if (token != null) {
                    headers.put("Authorization", "Bearer " + token);
                }
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        queue.add(request);
    }

    private void addScooterToView(int id, String type) {
        // Inflate the scooter item layout
        LinearLayout scooterItem = (LinearLayout) getLayoutInflater().inflate(R.layout.scooter_item, null);

        // Set scooter details
        TextView scooterId = scooterItem.findViewById(R.id.scooter_code);
        scooterId.setText("Scooter: " + id + " - " + type);

        // Handle delete click
        ImageView deleteIcon = scooterItem.findViewById(R.id.delete_icon);
        deleteIcon.setOnClickListener(v -> deleteScooter(id, scooterItem));

        // Add to the list
        scootersList.addView(scooterItem);
    }

    private String getDetailedErrorMessage(VolleyError error) {
        if (error.networkResponse != null && error.networkResponse.data != null) {
            try {
                String responseBody = new String(error.networkResponse.data, "utf-8");
                JSONObject json = new JSONObject(responseBody);
                return json.optString("message", "Unknown server error");
            } catch (Exception e) {
                return "Failed to parse error response";
            }
        }
        return error.getMessage() != null ? error.getMessage() : "Unknown error";
    }

    private void deleteScooter(int id, LinearLayout scooterItem) {
        String url = "http://x.x.x.x:5001/api/scooters/" + id;
        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest request = new StringRequest(Request.Method.DELETE, url,
                response -> {
                    scootersList.removeView(scooterItem);
                    Toast.makeText(ScooterActivity.this, "Scooter deleted successfully", Toast.LENGTH_SHORT).show();
                },
                error -> Toast.makeText(ScooterActivity.this, "Failed to delete scooter: " + error.getMessage(), Toast.LENGTH_SHORT).show()
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                SharedPreferences preferences = getSharedPreferences("auth", MODE_PRIVATE);
                String token = preferences.getString("jwt_token", null);

                if (token != null) {
                    headers.put("Authorization", "Bearer " + token);
                }
                return headers;
            }
        };

        queue.add(request);
    }

    private void filterScooters(String query) {
        for (int i = 0; i < scootersList.getChildCount(); i++) {
            LinearLayout scooterItem = (LinearLayout) scootersList.getChildAt(i);
            TextView scooterCode = scooterItem.findViewById(R.id.scooter_code);

            if (scooterCode.getText().toString().toLowerCase().contains(query.toLowerCase())) {
                scooterItem.setVisibility(View.VISIBLE);
            } else {
                scooterItem.setVisibility(View.GONE);
            }
        }
    }
}

