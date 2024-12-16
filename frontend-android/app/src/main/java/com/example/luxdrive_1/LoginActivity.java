package com.example.luxdrive_1;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;


import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        EditText email = findViewById(R.id.emailLogIn);
        EditText password = findViewById(R.id.passwordLogIn);
        Button logInButton = findViewById(R.id.logInButton);

        logInButton.setOnClickListener(v -> {
            String emailText = email.getText().toString();
            String passwordText = password.getText().toString();

            if (emailText.isEmpty() || passwordText.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            } else {
                loginUser(emailText, passwordText);
            }
        });
    }

    private void loginUser(String email, String password) {
        String url = "http://x.x.x.x:5001/api/users/login"; // Replace with your local server IP
        RequestQueue queue = Volley.newRequestQueue(this);

        JSONObject loginData = new JSONObject();
        try {
            loginData.put("Email", email);
            loginData.put("Password", password);
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error creating request data", Toast.LENGTH_SHORT).show();
            return;
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, loginData,
                response -> {
                    try {
                        if (response.has("message") && response.has("userType") && response.has("token")) {
                            String message = response.getString("message");
                            String userType = response.getString("userType");
                            String token = response.getString("token");

                            // Store the token securely
                            SharedPreferences preferences = getSharedPreferences("auth", MODE_PRIVATE);
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putString("jwt_token", token);
                            editor.apply();

                            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();

                            // Redirect based on user type
                            Intent intent = new Intent(LoginActivity.this, MapaActivity.class);
                            intent.putExtra("userType", userType.toLowerCase());
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(this, "Unexpected response format", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Error parsing response", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    String errorMessage;
                    if (error.networkResponse != null && error.networkResponse.data != null) {
                        errorMessage = new String(error.networkResponse.data);
                    } else {
                        errorMessage = error.getMessage() != null ? error.getMessage() : "Unknown error";
                    }
                    Toast.makeText(this, "Login Failed: " + errorMessage, Toast.LENGTH_SHORT).show();
                }
        );
        queue.add(jsonObjectRequest);
    }

    //If I would use AuthUtils for request
//    private void loginUser(String email, String password) {
//        String url = "http://x.x.x.x:5001/api/users/login"; // Replace with your local server IP
//
//        JSONObject loginData = new JSONObject();
//        try {
//            loginData.put("Email", email);
//            loginData.put("Password", password);
//        } catch (JSONException e) {
//            e.printStackTrace();
//            Toast.makeText(this, "Error creating request data", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        // Use AuthUtils to make the request
//        AuthUtils.makeAuthenticatedRequest(this, url, Request.Method.POST, loginData,
//                response -> {
//                    try {
//                        if (response.has("message") && response.has("userType") && response.has("token")) {
//                            String message = response.getString("message");
//                            String userType = response.getString("userType");
//                            String token = response.getString("token");
//
//                            // Store the token securely
//                            SharedPreferences preferences = getSharedPreferences("auth", MODE_PRIVATE);
//                            SharedPreferences.Editor editor = preferences.edit();
//                            editor.putString("jwt_token", token);
//                            editor.apply();
//
//                            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
//
//                            // Redirect based on user type
//                            Intent intent = new Intent(LoginActivity.this, MapaActivity.class);
//                            intent.putExtra("userType", userType.toLowerCase());
//                            startActivity(intent);
//                            finish();
//                        } else {
//                            Toast.makeText(this, "Unexpected response format", Toast.LENGTH_SHORT).show();
//                        }
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                        Toast.makeText(this, "Error parsing response", Toast.LENGTH_SHORT).show();
//                    }
//                },
//                error -> {
//                    String errorMessage;
//                    if (error.networkResponse != null && error.networkResponse.data != null) {
//                        errorMessage = new String(error.networkResponse.data);
//                    } else {
//                        errorMessage = error.getMessage() != null ? error.getMessage() : "Unknown error";
//                    }
//                    Toast.makeText(this, "Login Failed: " + errorMessage, Toast.LENGTH_SHORT).show();
//                }
//        );
//    }

}
