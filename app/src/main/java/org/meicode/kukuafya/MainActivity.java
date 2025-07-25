package org.meicode.kukuafya;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import org.meicode.kukuafya.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    ActivityMainBinding binding;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    FragmentManager fragmentManager;
    Handler handler;
    Runnable timeoutRunnable;

    private Toolbar toolbar;

    private static final String PREFS_NAME = "UserPrefs";
    private static final String LAST_ACTIVE_TIME = "lastActiveTime";
    private static final long TIMEOUT_DURATION = 30 * 1000;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_drawer);
        View BottomAppBar = findViewById(R.id.bottomAppBar);

        //toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //floating action bar
        FloatingActionButton fab = findViewById(R.id.fab);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, binding.toolbar, R.string.open_nav, R.string.close_nav);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        fragmentManager = getSupportFragmentManager();
        handler = new Handler();

      //  Opening and closing of bottom nav bar when initializing input
        final View rootView = findViewById(android.R.id.content);
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            int screenHeight = rootView.getRootView().getHeight();
            int keypadHeight = screenHeight - rootView.getHeight();
            boolean isOpen = keypadHeight > screenHeight * 0.15;

            BottomNavigationView bottomNav = findViewById(R.id.bottomNavigationView);
            bottomNav.setVisibility(isOpen ? View.GONE : View.VISIBLE);
        });




        // Inflate the navigation header layout
        View headerView = navigationView.getHeaderView(0);
        TextView usernameTextView = headerView.findViewById(R.id.nav_header_title);
        TextView userEmailTextView = headerView.findViewById(R.id.nav_header_subtitle);

        // Retrieve user information from SharedPreferences
        SharedPreferences sharedPref = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String userName = sharedPref.getString("USER_NAME", "Username");
        String userEmail = sharedPref.getString("USER_EMAIL", "email@example.com");

        // Update UI with user information
        usernameTextView.setText(userName);
        userEmailTextView.setText(userEmail);



        // Handle click event
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Perform undo last action
                undoLastAction();
            }
        });


        // Handle long press event
        fab.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                // Navigate to the HomeFragment
                replaceFragment(new HomeFragment());
                return true; // Indicates that the long click event is consumed
            }
        });



        // Set up the timeout check
        timeoutRunnable = () -> {
            long lastActiveTime = getLastActiveTime();
            long currentTime = System.currentTimeMillis();
            long elapsedTime = currentTime - lastActiveTime;

            if (elapsedTime > TIMEOUT_DURATION) {
                // Show password prompt or login screen
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                finish(); // Finish the MainActivity to prevent returning to it without re-authentication
            }
        };

        // Start the timeout check after the specified duration
        resetTimeout();

        // Set the last active time when the app is created
        setLastActiveTime(System.currentTimeMillis());

        // Replace the fragment with the initial one
        replaceFragment(new HomeFragment());
        binding.bottomNavigationView.setBackground(null);

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment fragment;
            if (item.getItemId() == R.id.home) {
                fragment = new HomeFragment();
            } else if (item.getItemId() == R.id.detect) {
                fragment = new DetectFragment();
            } else if (item.getItemId() == R.id.treatment) {
                fragment = new TreatmentFragment();
            } else if (item.getItemId() == R.id.community) {
                fragment = new CommunityFragment();
            } else {
                fragment = new HomeFragment();
            }
            replaceFragment(fragment);
            return true;
        });
    }

    private void undoLastAction() {
    }


    @Override
    protected void onPause() {
        super.onPause();
        // Save the current time when the app goes into background
        setLastActiveTime(System.currentTimeMillis());
        // Remove the timeout check when the app goes into background
        handler.removeCallbacks(timeoutRunnable);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Start the timeout check again when the app is resumed
        resetTimeout();
    }

    @Override
    public void onUserInteraction() {
        super.onUserInteraction();
        // Reset the timeout timer on user interaction
        resetTimeout();
    }

    private void replaceFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }

    private void setLastActiveTime(long timeInMillis) {
        SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
        editor.putLong(LAST_ACTIVE_TIME, timeInMillis);
        editor.apply();
    }

    private long getLastActiveTime() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        return prefs.getLong(LAST_ACTIVE_TIME, 0); // Default is 0
    }

    private void resetTimeout() {
        // Save the current time as the last active time
        setLastActiveTime(System.currentTimeMillis());
        // Remove any previous callbacks
        handler.removeCallbacks(timeoutRunnable);
        // Post the timeout runnable with the specified duration
        handler.postDelayed(timeoutRunnable, TIMEOUT_DURATION);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        Fragment fragment;
        int itemId = menuItem.getItemId();
        if (itemId == R.id.Import) {
            fragment = new ImportFragment();
        } else if (itemId == R.id.gallery) {
            fragment = new GalleryFragment();
        } else if (itemId == R.id.about) {
            fragment = new AboutFragment();
        } else if (itemId == R.id.log_out) {
            fragment = new LogoutFragment();
        } else {
            fragment = new HomeFragment();
        }

        if (fragment != null) {
            replaceFragment(fragment);
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    ///hide toolbar

    public void hideToolbar() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
    }

    public void showToolbar() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().show();
        }
    }

    // Hide and show bottom app bar methods
    public void hideBottomAppBar() {
        BottomAppBar bottomAppBar = findViewById(R.id.bottomAppBar);
        if (bottomAppBar != null && bottomAppBar.getVisibility() == View.VISIBLE) {
            bottomAppBar.setVisibility(View.GONE);
        }
    }

    public void showBottomAppBar() {
        BottomAppBar bottomAppBar = findViewById(R.id.bottomAppBar);
        if (bottomAppBar != null && bottomAppBar.getVisibility() != View.VISIBLE) {
            bottomAppBar.setVisibility(View.VISIBLE);
        }
    }


    // Hide and show floating action button methods
    public void hideFloatingActionButton() {
        FloatingActionButton fab = findViewById(R.id.fab);
        if (fab != null && fab.getVisibility() == View.VISIBLE) {
            fab.setVisibility(View.GONE);
        }
    }

    public void showFloatingActionButton() {
        FloatingActionButton fab = findViewById(R.id.fab);
        if (fab != null && fab.getVisibility() != View.VISIBLE) {
            fab.setVisibility(View.VISIBLE);
        }
    }



}