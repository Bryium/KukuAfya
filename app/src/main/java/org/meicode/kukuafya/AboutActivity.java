package org.meicode.kukuafya;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class AboutActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_about);

        // Optional: Set title on the ActionBar
        getSupportActionBar().setTitle("About");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Show back button
    }

    // Handle back button
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
