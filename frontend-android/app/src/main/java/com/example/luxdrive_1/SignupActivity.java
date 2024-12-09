package com.example.luxdrive_1;

import android.content.Intent;
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

import org.json.JSONObject;

public class SignupActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        EditText email = findViewById(R.id.emailSignUp);
        EditText password = findViewById(R.id.passwordSignUp);
        Button signUpButton = findViewById(R.id.signUpButton);

        signUpButton.setOnClickListener(v -> {
            String emailText = email.getText().toString();
            String passwordText = password.getText().toString();

            if (emailText.isEmpty() || passwordText.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            } else {
                signUp(emailText,passwordText);
//                // Handle sign-up logic here
//                Toast.makeText(this, "Sign Up Successful", Toast.LENGTH_SHORT).show();
//                // Redirect to login
//                startActivity(new Intent(SignupActivity.this, LoginActivity.class));
            }
        });
    }
    private void signUp(String emailText, String passwordText) {
        // URL of your backend API endpoint
        String url = "http://x.x.x.x:5001/api/users/register"; // Change this to your actual server URL

        // Create a new request queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        // Create JSON object for the request body
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("Email", emailText);
            requestBody.put("Password", passwordText);
            requestBody.put("UserType", "Regular");
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Create a JSON object request
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                url,
                requestBody,
                response -> {
                    // Parse the server response
                    try {
                        String successMessage = "Sign Up Successful"; // Default message
                        if (response.has("message")) {
                            successMessage = response.getString("message");
                        }
                        Toast.makeText(SignupActivity.this, successMessage, Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(SignupActivity.this, LoginActivity.class));
                        finish(); // Close the signup activity
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(SignupActivity.this, "Sign Up Successful", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(SignupActivity.this, LoginActivity.class));
                        finish();
                    }
                },
                error -> {
                    // Handle error response
                    String errorMessage = "An error occurred. Please try again later.";
                    if (error.networkResponse != null && error.networkResponse.data != null) {
                        try {
                            // Extract the error message from the server response
                            String responseBody = new String(error.networkResponse.data, "UTF-8");
                            JSONObject data = new JSONObject(responseBody);
                            if (data.has("message")) {
                                errorMessage = data.getString("message");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    Toast.makeText(SignupActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                }
        );

        // Add the request to the request queue
        requestQueue.add(jsonObjectRequest);
    }
}