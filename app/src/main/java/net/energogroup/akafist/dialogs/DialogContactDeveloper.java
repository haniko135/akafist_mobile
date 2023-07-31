package net.energogroup.akafist.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.snackbar.Snackbar;

import net.energogroup.akafist.R;

public class DialogContactDeveloper extends DialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View contactDeveloperDialog = getLayoutInflater().inflate(R.layout.fragment_contact_developer, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        AlertDialog alertDialog = builder.create();

        EditText subjectET = contactDeveloperDialog.findViewById(R.id.subjectET);
        EditText contentET = contactDeveloperDialog.findViewById(R.id.contentET);
        Button sendButton = contactDeveloperDialog.findViewById(R.id.sendDeveloper);

        sendButton.setOnClickListener(view -> {
            String subjectText = subjectET.getText().toString();
            String contentText = contentET.getText().toString();
            if(subjectText.isEmpty() || contentText.isEmpty()){
                Snackbar.make(contactDeveloperDialog,
                                getString(R.string.userAttention), Snackbar.LENGTH_SHORT)
                        .setBackgroundTint(getContext().getColor(R.color.white))
                        .setTextColor(getContext().getColor(R.color.black)).show();
            }else {
                String[] emailData = {subjectText, contentText};
                sendEmail(emailData);
            }
        });

        alertDialog.setView(contactDeveloperDialog);
        return alertDialog;
    }

    public void sendEmail(String[] emailData){
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{getString(R.string.developEmail)});
        intent.putExtra(Intent.EXTRA_SUBJECT, emailData[0]);
        intent.putExtra(Intent.EXTRA_TEXT, emailData[1]);
        intent.setType("message/rfc822");
        startActivity(Intent.createChooser(intent, getString(R.string.intentTitle)));
        dismiss();
    }
}
