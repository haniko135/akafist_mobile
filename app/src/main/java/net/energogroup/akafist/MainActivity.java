package net.energogroup.akafist;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import net.energogroup.akafist.databinding.ActivityMainBinding;
import net.energogroup.akafist.db.DBHelper;
import net.energogroup.akafist.fragments.PlayerFragment;
import net.energogroup.akafist.service.NetworkConnection;
import net.energogroup.akafist.service.notification.NotificationForPlay;

/**
 * Main activity class
 * @author Nastya Izotina
 * @version 1.0.0
 */
public class MainActivity extends AppCompatActivity {

    @RequiresApi(api = Build.VERSION_CODES.R)
    private final String[] PERMISSION_NOTIFICATION = {
            Manifest.permission.POST_NOTIFICATIONS
    };

    private final String[] PERMISSION_STORAGE = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.MANAGE_EXTERNAL_STORAGE
    };

    public ActivityMainBinding binding;
    public static final int SEC_TOKEN = R.string.secToken;
    public static final int AZBYKA_TOKEN = R.string.azbykaToken;
    public static final int API_PATH = R.string.apiPath;
    public static final int AZBYKA_API_PATH = R.string.azbykaApiPath;
    public static final int APP_VER = R.string.app_ver;


    public static String APP_PREFERENCES = "app_pref";
    public static final String CHANNEL_ID = "downloadNote";
    public static RequestQueue mRequestQueue;
    public static NetworkConnection networkConnection;
    NavController navController;
    public Toolbar supToolBar;
    private DBHelper dbHelper;

    public ActivityMainBinding getBinding() {
        return binding;
    }

    public DBHelper getDbHelper(){
        return dbHelper;
    }

    /**
     * This method initializes the main activity of the application
     * @param savedInstanceState Bundle - current state of the application
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        super.onCreate(savedInstanceState);
        super.onPostResume();
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        unableNightTheme();
        enableSupToolBar();
        enableNavigation();
        enableNetwork();
        enablePlayer();
        enablePermissions();
        enableNotifications();
        dbHelper = new DBHelper(this);
    }

    /**
     * This method disables the night theme
     */
    public void unableNightTheme() {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
    }

    /**
     * Этот метод создаёт верхнюю панель
     */
    public void enableSupToolBar() {
        supToolBar = findViewById(R.id.supToolBar);
        setSupportActionBar(supToolBar);
        supToolBar.inflateMenu(R.menu.nav_menu);
        supToolBar.setTitle("Помощник чтеца");
    }

    /**
     * This method creates the top panel
     */
    public void enableNavigation() {
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_fragment);
        navController = navHostFragment.getNavController();
        navController.setGraph(R.navigation.routes);
    }

    /**
     * This method creates network status tracking
     */
    public void enableNetwork() {
        if (getApplicationContext() != null) {
            networkConnection = new NetworkConnection(getApplicationContext());
        }
    }

    /**
     * This method requests permissions from the user
     */
    public void enablePermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            verifyNotificationPerm();
        }
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.R) {
            verifyStoragePerm();
        }
        verifyBatteryPerm();
    }

    /**
     * This method enables {@link PlayerFragment#PlayerFragment()}
     */
    public void enablePlayer() {
        binding.mainLayout.playerContainer.setVisibility(View.GONE);
        SharedPreferences appPref = getSharedPreferences(MainActivity.APP_PREFERENCES, Context.MODE_PRIVATE);
        appPref.edit().putBoolean("app_pref_player", false).apply();
    }

    /**
     * This method creates notification channels in the application
     */
    public void enableNotifications() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String description = "Для загрузки уведомления пользователя";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_ID, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);

            NotificationChannel channel2 = new NotificationChannel(NotificationForPlay.CHANNEL_ID,
                    "Для отображения музыки", NotificationManager.IMPORTANCE_LOW);
            notificationManager.createNotificationChannel(channel2);
        }
    }

    /**
     * This method creates a menu in the Toolbar
     * @param menu Menu - main menu
     * @return Status of the created menu
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.nav_menu, menu);
        return true;
    }

    /**
     * This method assigns actions to menu items
     * @param item MenuItem - menu item
     * @return Status of assigned actions to the menu
     */
    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.homeFragment) {
            navController.navigate(R.id.action_global_home2);
            return true;
        } else if (item.getItemId() == R.id.settingsFrag) {
            navController.navigate(R.id.action_global_settingsFragment);
            return true;
        } else if (item.getItemId() == R.id.accountFrag) {
            navController.navigate(R.id.action_global_accountFragment);
            return true;
        } else if (item.getItemId() == R.id.calendFrag) {
            navController.navigate(R.id.action_global_calendarFragment);
            return true;
        } else if (item.getItemId() == R.id.quitApp) {
            MainActivity.this.finish();
            System.exit(0);
        }
        return true;
    }

    /**
     * This method responds to a configuration change
     * @param newConfig
     */
    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Log.i("ORIENTATION", "Landscape");
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            Log.i("ORIENTATION", "Portrait");
        }
    }

    /**
     * This method destroys the activity
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * This method enables notification permissions if they are not enabled
     */
    private void verifyNotificationPerm() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                ActivityCompat.requestPermissions(this, PERMISSION_NOTIFICATION, 2);
            }
        }
    }

    /**
     * This method enables storage permissions if they are not enabled
     */
    private void verifyStoragePerm() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, PERMISSION_STORAGE, 1);
        }
    }

    private void verifyBatteryPerm(){
        Intent intent = new Intent();
        String packageName = getPackageName();
        PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
        if (!pm.isIgnoringBatteryOptimizations(packageName)) {
            intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
            intent.setData(Uri.parse("package:" + packageName));
            startActivity(intent);
        }
    }

    /**
     * This method generate notification
     */
    public static void generateNotification(int textId, Context context) {
        String text = context.getResources().getString(textId);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, MainActivity.CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentTitle(context.getString(R.string.app_name))
                .setContentText(text)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(context);
        int NOTIFICATION_ID = 101;
        try {
            managerCompat.notify(NOTIFICATION_ID, builder.build());
        } catch (SecurityException e){
            Log.e("NOTIFICATION ERROR", e.getLocalizedMessage());
        }
    }
}