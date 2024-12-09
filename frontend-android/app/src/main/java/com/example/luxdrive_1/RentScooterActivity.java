package com.example.luxdrive_1;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class RentScooterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_rent_scooter);
        // Find Views
        EditText editTextTime = findViewById(R.id.editTextTime);
        Button rentButton = findViewById(R.id.rentButton);

        // Button Click Listener
        rentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String hours = editTextTime.getText().toString().trim();

                // Check for valid input
                if (hours.isEmpty()) {
                    Toast.makeText(RentScooterActivity.this, "Please enter rental time in hours", Toast.LENGTH_SHORT).show();
                } else {
                    int rentalTime = Integer.parseInt(hours);

                    if (rentalTime <= 0) {
                        Toast.makeText(RentScooterActivity.this, "Enter a valid rental time!", Toast.LENGTH_SHORT).show();
                    } else {
                        // Proceed with renting logic
                        Toast.makeText(RentScooterActivity.this, "Scooter rented for " + rentalTime + " hours.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
}