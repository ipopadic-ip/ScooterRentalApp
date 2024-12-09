package com.example.luxdrive_1;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class AddScooterActivity extends AppCompatActivity {
    private EditText priceInput;
//    private EditText classInput;
    private Spinner classSpinner;
    private EditText speedInput;
    private Button addButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_scooter);

        // Initialize UI components
        priceInput = findViewById(R.id.price_input);
//        classInput = findViewById(R.id.class_input);
        classSpinner = findViewById(R.id.class_spinner);
        speedInput = findViewById(R.id.speed_input);
        addButton = findViewById(R.id.add_button);

        // Populate Spinner with scooter classes
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.scooter_classes, // Reference to string array
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        classSpinner.setAdapter(adapter);

        // Handle Add button click
        addButton.setOnClickListener(v -> {
            String price = priceInput.getText().toString().trim();
            String scooterClass = classSpinner.getSelectedItem().toString();
//            String scooterClass = classInput.getText().toString().trim();
            String maxSpeed = speedInput.getText().toString().trim();

            if (price.isEmpty() || scooterClass.isEmpty() || maxSpeed.isEmpty()) {
                Toast.makeText(AddScooterActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            } else {
                // Handle adding the scooter (e.g., save to database)
//                Toast.makeText(AddScooterActivity.this, "Scooter Added!", Toast.LENGTH_SHORT).show();
//                finish();
                addScooterToDatabase(price, scooterClass, maxSpeed);
            }
        });
    }

    private void addScooterToDatabase(String price, String scooterClass, String maxSpeed) {
        String url = "http://x.x.x.x:5001/api/scooters"; // Replace with your API URL
        RequestQueue queue = Volley.newRequestQueue(this);

        JSONObject scooterData = new JSONObject();
        try {
            scooterData.put("PricePerMin", Integer.parseInt(price));
            scooterData.put("ScooterType", scooterClass);
            scooterData.put("MaxSpeed", Integer.parseInt(maxSpeed));
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to create JSON object", Toast.LENGTH_SHORT).show();
            return;
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, scooterData,
                response -> {
                    Toast.makeText(AddScooterActivity.this, "Scooter added successfully!", Toast.LENGTH_SHORT).show();
                    finish(); // Return to the previous screen
                },
                error -> {
                    String errorMessage = error.getMessage() != null ? error.getMessage() : "Unknown error";
                    Toast.makeText(AddScooterActivity.this, "Failed to add scooter: " + errorMessage, Toast.LENGTH_SHORT).show();
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

}