package net.energogroup.akafist;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;

import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import net.energogroup.akafist.databinding.ActivityMainBinding;
import net.energogroup.akafist.service.NetworkConnection;
import net.energogroup.akafist.service.notification.NotificationForPlay;
import net.energogroup.akafist.viewmodel.OnlineTempleViewModel;

/**
 * Класс главной активности
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
    public static String secToken;
    public static boolean isChecked = false;
    public static String APP_PREFERENCES = "app_pref";
    public static final String CHANNEL_ID = "downloadNote";
    public static RequestQueue mRequestQueue;
    public static NetworkConnection networkConnection;
    NavController navController;
    public Toolbar supToolBar;

    public static BottomSheetBehavior playerBehavior;

    public ActivityMainBinding getBinding() {
        return binding;
    }

    /**
     * Этот метод инициализирует главную активность приложения
     * @param savedInstanceState Bundle - текцщее состояние приложения
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
        globals();
        enablePermissions();
        enableNotifications();
    }

    /**
     * Этот метод отключает ночную тему
     */
    public void unableNightTheme(){
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
    }

    /**
     * Этот метод создаёт верхнюю панель
     */
    public void enableSupToolBar(){
        supToolBar = findViewById(R.id.supToolBar);
        setSupportActionBar(supToolBar);
        supToolBar.inflateMenu(R.menu.nav_menu);
        supToolBar.setTitle("Помощник чтеца");
    }

    /**
     * Этот метод создаёт навигацию между фрагментами
     */
    public void enableNavigation(){
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_fragment);
        navController = navHostFragment.getNavController();
        navController.setGraph(R.navigation.routes);
    }

    /**
     * Этот метод создаёт отслеживание состояния сети
     */
    public void enableNetwork(){
        if(getApplicationContext() != null) {
            networkConnection = new NetworkConnection(getApplicationContext());
        }
    }

    /**
     * Этот метод инициализирует глобальные переменные
     */
    public void globals(){
        AkafistApplication akafistApplication = (AkafistApplication)getApplication();
        akafistApplication.globalIsChecked = isChecked;
        secToken = akafistApplication.secToken;
    }

    /**
     * Этот метод спрашивает разрешения у пользователя
     */
    public void enablePermissions(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
            verifyNotificationPerm();
        }
        if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.R){
            verifyStoragePerm();
        }
    }

    public void enablePlayer(){
        binding.mainLayout.playerContainer.setVisibility(View.GONE);
    }

    /**
     * Этот метод создаёт каналы уведомлений в приложении
     */
    public void enableNotifications(){
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
     * Этот метод создаёт меню в Toolbar
     * @param menu Menu - главное меню
     * @return Статус созданного меню
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.nav_menu, menu);
        return true;
    }

    /**
     * Этот метод присваивает действия к элементам меню
     * @param item MenuItem - элемент меню
     * @return Статус присвоенных дейстыий к меню
     */
    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.homeFragment){
            navController.navigate(R.id.action_global_home2);
            return true;
        } else if (item.getItemId() == R.id.menuFragment) {
            navController.navigate(R.id.action_global_menu);
            return true;
        } else if (item.getItemId() == R.id.settingsFrag) {
            navController.navigate(R.id.action_global_settingsFragment);
            return true;
        } else if (item.getItemId() == R.id.quitApp) {
            MainActivity.this.finish();
            System.exit(0);
        }
        return true;
    }

    /**
     * Этот метод реагирует на смену конфигурации
     * @param newConfig
     */
    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE){
            Log.i("ORIENTATION", "Landscape");
        }else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            Log.i("ORIENTATION", "Portrait");
        }
    }

    /**
     * Этот метод уничтожает активность
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void verifyNotificationPerm(){
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                ActivityCompat.requestPermissions(this, PERMISSION_NOTIFICATION, 2);
            }
        }
    }
    private void verifyStoragePerm() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, PERMISSION_STORAGE, 1);
        }
    }
}