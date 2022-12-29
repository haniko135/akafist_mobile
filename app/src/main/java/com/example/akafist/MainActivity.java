package com.example.akafist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;


import android.annotation.SuppressLint;
import android.os.Bundle;

import android.util.Log;
import android.view.MenuItem;
import android.view.View;


import com.example.akafist.databinding.ActivityMainBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.io.File;
import java.nio.file.Path;


public class MainActivity extends AppCompatActivity {

    public ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.onPostResume();
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        NavController navController;
        NavHostFragment navHostFragment =
                (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_fragment);

        navController = navHostFragment.getNavController();


        navController.setGraph(R.navigation.routes);

        //binding.bNav.setOnItemSelectedListener(obItList);
        binding.bNav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.homeFragment:
                        navController.navigate(R.id.action_global_home2);
                        return true;
                    case R.id.menuFragment:
                        navController.navigate(R.id.action_global_menu);
                        return true;
                }
                return true;
            }
        });

    }

    @Override
    protected void onDestroy() {
        String fileSystem = getCacheDir().getPath();
        File androidStorage = new File(fileSystem);
        boolean res = cleanTemps(androidStorage);
        if(res)
            Log.i("CLEAN", "Directory deleted");
        else
            Log.i("CLEAN", "Directory still exists");
        super.onDestroy();
    }


    public boolean cleanTemps(File deleteFile){
        File[] allFiles = deleteFile.listFiles();
        if (allFiles != null){
            for (File file : allFiles) {
                cleanTemps(file);
            }
        }
        return  deleteFile.delete();
    }
}