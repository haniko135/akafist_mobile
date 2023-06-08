package net.energogroup.akafist.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.FragmentKt;

import net.energogroup.akafist.R;
import net.energogroup.akafist.fragments.LinksFragment;

public class DialogLinks extends DialogFragment {

    private SharedPreferences.Editor editor;
    private Fragment fr;

    public DialogLinks() { }

    public DialogLinks(SharedPreferences.Editor editor, LinksFragment frag){
        this.editor = editor;
        this.fr = frag;
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
                    FragmentKt.findNavController(fr).navigate(R.id.action_linksFragment_to_home2);
                });

        return builder.create();
    }
}
