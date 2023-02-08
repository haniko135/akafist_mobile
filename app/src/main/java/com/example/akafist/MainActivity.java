package com.example.akafist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;


import android.annotation.SuppressLint;
import android.content.res.Configuration;
import android.os.Bundle;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;


import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.akafist.databinding.ActivityMainBinding;

import java.io.File;


public class MainActivity extends AppCompatActivity {

    public ActivityMainBinding binding;
    public static String secToken;
    public static boolean isChecked = false;
    public static RequestQueue mRequestQueue;
    NavController navController;
    public Toolbar supToolBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        super.onCreate(savedInstanceState);
        super.onPostResume();
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        supToolBar = findViewById(R.id.supToolBar);
        setSupportActionBar(supToolBar);
        supToolBar.inflateMenu(R.menu.nav_menu);
        supToolBar.setTitle("Помощник чтеца");

        NavHostFragment navHostFragment =
                (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_fragment);

        navController = navHostFragment.getNavController();
        navController.setGraph(R.navigation.routes);


        AkafistApplication akafistApplication = (AkafistApplication)getApplication();
        akafistApplication.globalIsChecked = isChecked;
        secToken = akafistApplication.secToken;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.nav_menu, menu);
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.homeFragment:
                navController.navigate(R.id.action_global_home2);
                return true;
            case R.id.menuFragment:
                navController.navigate(R.id.action_global_menu);
                return true;
            case R.id.quitApp:
                MainActivity.this.finish();
                System.exit(0);
        }
        return true;
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE){
            Log.i("ORIENTATION", "Landscape");
        }else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            Log.i("ORIENTATION", "Portrait");
        }
    }

    @Override
    protected void onDestroy() {
        String fileSystem = getFilesDir().getPath();
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
                Log.i("CLEAN", file.getName());
            }
        }
        return  deleteFile.delete();
    }
}