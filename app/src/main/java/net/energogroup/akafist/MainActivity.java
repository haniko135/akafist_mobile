package net.energogroup.akafist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;


import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;


import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import net.energogroup.akafist.R;

import net.energogroup.akafist.databinding.ActivityMainBinding;
import net.energogroup.akafist.service.NetworkConnection;

/**
 * Класс главной активности
 * @author Nastya Izotina
 * @version 1.0.0
 */
public class MainActivity extends AppCompatActivity {

    public ActivityMainBinding binding;
    public static String secToken;
    public static boolean isChecked = false;
    public static final String CHANNEL_ID = "downloadNote";
    public static RequestQueue mRequestQueue;
    public static NetworkConnection networkConnection;
    NavController navController;
    public Toolbar supToolBar;

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

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        supToolBar = findViewById(R.id.supToolBar);
        setSupportActionBar(supToolBar);
        supToolBar.inflateMenu(R.menu.nav_menu);
        supToolBar.setTitle("Помощник чтеца");

        NavHostFragment navHostFragment =
                (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_fragment);

        navController = navHostFragment.getNavController();
        navController.setGraph(R.navigation.routes);

        if(getApplicationContext() != null) {
            networkConnection = new NetworkConnection(getApplicationContext());
        }

        AkafistApplication akafistApplication = (AkafistApplication)getApplication();
        akafistApplication.globalIsChecked = isChecked;
        secToken = akafistApplication.secToken;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String description = "Для загрузки уведомления пользователя";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_ID, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
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
}