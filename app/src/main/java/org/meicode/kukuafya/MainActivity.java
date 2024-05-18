package org.meicode.kukuafya;

import android.drm.DrmStore;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;







import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;


import com.google.android.material.navigation.NavigationView;

import org.meicode.kukuafya.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    ActivityMainBinding binding;
    DrawerLayout drawerLayout;

    FragmentManager fragmentManager;

    ImageView menu;


    Toolbar toolbar;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_nav, R.string.close_nav);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.navigation_drawer);
        binding.navigationDrawer.setNavigationItemSelectedListener(this);



        replaceFragment(new HomeFragment());
        binding.bottomNavigationView.setBackground(null);

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {

            Fragment fragment;
            if (item.getItemId() == R.id.home) {
                fragment = new HomeFragment();
            } else if (item.getItemId() == R.id.detect) {
                fragment = new DetectFragment();
            } else if (item.getItemId() == R.id.subscribe) {
                fragment = new SubscribeFragment();
            } else if (item.getItemId() == R.id.community) {
                fragment = new CommunityFragment();

            } else {
                fragment = new HomeFragment();

            }
            replaceFragment(fragment);
            return true;
        });


    }



    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }


    public static void openDrawer(DrawerLayout drawerrLayout) {
        drawerrLayout.openDrawer(GravityCompat.START);
    }

    public void closeDrawer(DrawerLayout drawerLayout) {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.getOnBackPressedDispatcher();
        }


    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        Fragment fragment;
        int itemId = menuItem.getItemId();
        if (itemId == R.id.Import) {
            fragment = new ImportFragment();

            if(fragment != null) {
                replaceFragment(fragment);
            }

        } else if (itemId == R.id.gallery) {
            fragment = new GalleryFragment();

            if(fragment != null) {
                replaceFragment(fragment);
            }

        } else if (itemId == R.id.about) {
            fragment = new AboutFragment();

            if(fragment != null) {
                replaceFragment(fragment);
            }
        } else if (itemId == R.id.log_out) {
            fragment = new LogoutFragment();

            if(fragment != null) {
                replaceFragment(fragment);
            }
        }


        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}
