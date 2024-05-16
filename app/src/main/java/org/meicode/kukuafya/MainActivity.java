package org.meicode.kukuafya;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import org.meicode.kukuafya.MainActivity;
import org.meicode.kukuafya.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {


     ActivityMainBinding binding ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(bindng.getRoot);

       replaceFragment(new HomeFragment());
        binding.bottomNavigationView.setBackground(null);

        binding.buttomNavigationView.setOnItemSelectedListener(item ->{

            switch (item.getItemId()) {
                case R.id.home:
                    replaceFragment(new HomeFragment());
                    break;
            }

            switch (item.getItemId()) {
                case R.id.detect:
                    replaceFragment(new DetectFragment());
                    break;
            }

            switch (item.getItemId()) {
                case R.id.subscribe:
                    replaceFragment(new SubscribeFragment());
                    break;
            }

            switch (item.getItemId()) {
                case R.id.community:
                    replaceFragment(new CommunityFragment());
                    break;
            }

            return true;
        });

    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }

}