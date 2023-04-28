package net.energogroup.akafist;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class StartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.R) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

            setContentView(R.layout.activity_start);

            new Handler().postDelayed(() -> {
                Intent i = new Intent(StartActivity.this, MainActivity.class);
                Log.e("YOU_ARE_LOH", "Loh");
                i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(i);
                finish();
            }, 2000);
        }else {
            Intent i = new Intent(StartActivity.this, MainActivity.class);
            Log.e("YOU_ARE_LOH", "Loh");
            i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(i);
            finish();
        }
    }
}
