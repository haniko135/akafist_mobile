package net.energogroup.akafist.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.FragmentKt;

import com.google.android.material.snackbar.Snackbar;

import net.energogroup.akafist.R;
import net.energogroup.akafist.databinding.FragmentLoginBinding;
import net.energogroup.akafist.fragments.LoginFragment;
import net.energogroup.akafist.viewmodel.LoginViewModel;

import java.util.Random;

public class DialogLogin extends DialogFragment {

    private LoginViewModel authService;
    private FragmentLoginBinding loginBinding;
    private SharedPreferences appPref;
    private Fragment fragment;

    public DialogLogin(LoginViewModel authService, FragmentLoginBinding loginBinding,
                       SharedPreferences appPref, LoginFragment fragment){
        this.authService = authService;
        this.loginBinding = loginBinding;
        this.appPref = appPref;
        this.fragment = fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(requireActivity().getResources().getString(R.string.login_quest))
                .setPositiveButton("Да", (dialog, which) -> {
                    authService.getFirst();
                    authService.getIsHostUnavailable().observe(fragment, aBoolean -> {
                        if(!aBoolean){
                            loginBinding.webViewLog.getSettings().setJavaScriptEnabled(true);
                            loginBinding.webViewLog.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
                            loginBinding.webViewLog.setWebViewClient(new WebViewClient(){
                                public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request){
                                    if (request.getUrl().toString().startsWith("https://dev-knowledge-api.energogroup.org/auth/login#code"))
                                    {
                                        String code = request.getUrl().toString().substring(58);
                                        authService.getSecond(code);
                                        return true;
                                    } else {
                                        Log.e("URL_YES", "Clown");
                                        return false;
                                    }
                                }
                            });
                            dialog.cancel();
                        } else {
                            SharedPreferences.Editor editor = appPref.edit();
                            editor.putBoolean("app_pref_first_login_snack", true);
                            editor.apply();

                            Random random = new Random();
                            int rand1 = random.nextInt(100);
                            int rand2 = random.nextInt(1000);
                            int rand3 = random.nextInt(7548);

                            editor.putString("app_pref_username", "Guest_" + rand1+"_"+rand2+"_"+rand3);
                            editor.putString("app_pref_email", rand1+"_"+rand2+"_"+rand3+"@guest");
                            editor.apply();

                            dialog.cancel();
                            FragmentKt.findNavController(fragment).navigate(R.id.action_loginFragment_to_home2);
                        }
                    });

                })
                .setNegativeButton("Нет", (dialog, which) -> {
                    SharedPreferences.Editor editor = appPref.edit();
                    editor.putBoolean("app_pref_first_login_snack", true);
                    editor.apply();

                    Random random = new Random();
                    int rand1 = random.nextInt(100);
                    int rand2 = random.nextInt(1000);
                    int rand3 = random.nextInt(7548);

                    editor.putString("app_pref_username", "Guest_" + rand1+"_"+rand2+"_"+rand3);
                    editor.putString("app_pref_email", rand1+"_"+rand2+"_"+rand3+"@guest");
                    editor.apply();

                    FragmentKt.findNavController(fragment).navigate(R.id.action_loginFragment_to_home2);
                    dialog.cancel();
                });
        return builder.create();
    }
}
