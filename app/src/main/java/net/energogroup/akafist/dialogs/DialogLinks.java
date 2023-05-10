package net.energogroup.akafist.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import net.energogroup.akafist.R;
import net.energogroup.akafist.fragments.LinksFragment;

public class DialogLinks extends DialogFragment {

    SharedPreferences.Editor editor;

    public DialogLinks() { }

    public DialogLinks(SharedPreferences.Editor editor){
        this.editor = editor;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Согласие на скачивание")
                .setMessage(getActivity().getResources().getString(R.string.text_for_agreement))
                .setPositiveButton(getActivity().getResources().getString(R.string.agree_agreement), (dialog, which) -> {
                    LinksFragment.isChecked = true;
                    editor.putBoolean("app_pref_checked", true);
                    editor.apply();
                    dialog.cancel();
                })
                .setNegativeButton(getActivity().getResources().getString(R.string.disagree_agrrement), (dialog, which) -> {
                    LinksFragment.isChecked = false;
                    editor.putBoolean("app_pref_checked", false);
                    editor.apply();
                    dialog.cancel();
                });

        return builder.create();
    }
}
