package net.energogroup.akafist.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.MutableLiveData;

import net.energogroup.akafist.MainActivity;
import net.energogroup.akafist.R;

public class DialogTextSize extends DialogFragment {

    private SharedPreferences appPref;
    private MutableLiveData<Float> liveTextSize = new MutableLiveData<>();
    private float textSizeNow;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        appPref = requireActivity().getSharedPreferences(MainActivity.APP_PREFERENCES, Context.MODE_PRIVATE);
        View textSizeDialog = getLayoutInflater().inflate(R.layout.fragment_text_size, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        AlertDialog alertDialog = builder.create();

        ImageButton zoomInDialog = textSizeDialog.findViewById(R.id.zoomInDialog);
        ImageButton zoomOutDialog = textSizeDialog.findViewById(R.id.zoomOutDialog);
        TextView fontSizeDialog = textSizeDialog.findViewById(R.id.fontSizeDialog);

        if(appPref.contains("app_pref_text_size")){
            textSizeNow = appPref.getFloat("app_pref_text_size", getActivity().getResources().getDimension(R.dimen.text_prayer));
        }else {
            textSizeNow = requireActivity().getResources().getDimension(R.dimen.text_prayer);
        }
        liveTextSize.setValue(textSizeNow);

        liveTextSize.observe(this, aFloat -> {
            fontSizeDialog.setText(String.valueOf(aFloat));
        });

        zoomOutDialog.setOnClickListener(v -> {
            textSizeNow--;
            liveTextSize.setValue(textSizeNow);
            SharedPreferences.Editor editor = appPref.edit();
            saveTextSize(editor);
        });

        zoomInDialog.setOnClickListener(v -> {
            textSizeNow++;
            liveTextSize.setValue(textSizeNow);
            SharedPreferences.Editor editor = appPref.edit();
            saveTextSize(editor);
        });

        alertDialog.setView(textSizeDialog);
        return alertDialog;
    }

    public void saveTextSize(SharedPreferences.Editor editor){
        editor.putFloat("app_pref_text_size", textSizeNow);
        editor.apply();
    }
}
