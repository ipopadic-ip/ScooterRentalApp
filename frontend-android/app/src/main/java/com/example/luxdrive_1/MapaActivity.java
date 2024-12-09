package com.example.luxdrive_1;

import android.content.Intent;
import android.provider.MediaStore;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

public class MapaActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapa);

        drawerLayout = findViewById(R.id.drawer_layout);
        LinearLayout navigationDrawer = findViewById(R.id.navigation_drawer);

        // Get user type from intent (with default fallback)
        String userType = getIntent().getStringExtra("userType");
        if (userType == null) userType = "user"; // Fallback to user if missing

        // Menu button to open/close drawer
        ImageButton menuButton = findViewById(R.id.menu_button);
        menuButton.setOnClickListener(v -> {
            if (drawerLayout.isDrawerOpen(navigationDrawer)) {
                drawerLayout.closeDrawer(navigationDrawer);
            } else {
                drawerLayout.openDrawer(navigationDrawer);
            }
        });

        // SCAN button setup
        Button scanButton = findViewById(R.id.scan_button);
        scanButton.setOnClickListener(v -> openCamera());

        // Update navigation drawer dynamically
        setupNavigationDrawer(navigationDrawer, userType);
    }

    private void setupNavigationDrawer(LinearLayout navigationDrawer, String userType) {
        // Clear any previous menu items
        navigationDrawer.removeAllViews();

        if ("admin".equals(userType.toLowerCase())) {
            addMenuItem(navigationDrawer, "Map", true, null);
            addMenuItem(navigationDrawer, "Scooters", false, v -> {
                startActivity(new Intent(MapaActivity.this, ScooterActivity.class));
            });
            addMenuItem(navigationDrawer, "Add New", false, v -> {
                startActivity(new Intent(MapaActivity.this, AddScooterActivity.class));
            });
            addMenuItem(navigationDrawer, "Settings", false, null);
        } else {
            addMenuItem(navigationDrawer, "Map", true, null);
            addMenuItem(navigationDrawer, "Log Out", false, v -> {
                startActivity(new Intent(MapaActivity.this, MainActivity.class));
            });
        }
    }

    private void addMenuItem(LinearLayout parent, String title, boolean isSelected, View.OnClickListener onClickListener) {
        LinearLayout menuItem = new LinearLayout(this);
        menuItem.setOrientation(LinearLayout.HORIZONTAL);
        menuItem.setPadding(16, 16, 16, 16);
        menuItem.setBackgroundColor(isSelected ? getResources().getColor(R.color.selected_item_color) : Color.WHITE);

        TextView textView = new TextView(this);
        textView.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
        textView.setText(title);
        textView.setTextSize(18);
        textView.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
        textView.setTextColor(Color.BLACK);

        ImageView arrowIcon = new ImageView(this);
        arrowIcon.setLayoutParams(new LinearLayout.LayoutParams(48, 48));
        arrowIcon.setImageResource(R.drawable.ic_arrow);
        arrowIcon.setScaleType(ImageView.ScaleType.CENTER);

        menuItem.addView(textView);
        menuItem.addView(arrowIcon);
        parent.addView(menuItem);

        // Set click listener if provided
        if (onClickListener != null) {
            menuItem.setOnClickListener(onClickListener);
        }
    }

    private void openCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(cameraIntent);
        }
    }


}

