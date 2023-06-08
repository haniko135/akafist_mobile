package net.energogroup.akafist.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.FragmentKt;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import net.energogroup.akafist.MainActivity;
import net.energogroup.akafist.R;
import net.energogroup.akafist.databinding.FragmentLoginBinding;
import net.energogroup.akafist.dialogs.DialogLogin;
import net.energogroup.akafist.viewmodel.LoginViewModel;

import java.util.Random;
import java.util.UUID;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginFragment extends Fragment {

    public FragmentLoginBinding loginBinding;
    private LoginViewModel authService;
    private String name, email;
    private SharedPreferences appPref;

    public LoginFragment() { }

    public static LoginFragment newInstance() {
        return new LoginFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("Login", "OnCreate");
        if(getActivity() != null) {
            appPref = getActivity().getSharedPreferences(MainActivity.APP_PREFERENCES, Context.MODE_PRIVATE);

            SharedPreferences.Editor editor = appPref.edit();
            editor.apply();
            if (appPref.getBoolean("app_pref_firstlogin", true)) {
                editor.putBoolean("app_pref_firstlogin", false);
                editor.putBoolean("app_pref_first_login_snack", true);
                editor.apply();
                guestFunc(editor);
                FragmentKt.findNavController(this).navigate(R.id.action_loginFragment_to_home2);
            } else {
                if (appPref.contains("app_pref_username") && appPref.contains("app_pref_email")) {
                    editor.putBoolean("app_pref_first_login_snack", true);
                    editor.apply();
                    FragmentKt.findNavController(this).navigate(R.id.action_loginFragment_to_home2);
                }
            }
            ViewModelProvider provider = new ViewModelProvider(this);
            authService = provider.get(LoginViewModel.class);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.e("Login", "OnCreateView");
        loginBinding = FragmentLoginBinding.inflate(inflater, container, false);

        if (!appPref.getBoolean("app_pref_firstlogin", true) && !appPref.contains("app_pref_username") && !appPref.contains("app_pref_email")) {
            DialogLogin dialogLogin = new DialogLogin(authService, loginBinding, appPref, this);
            dialogLogin.show(requireActivity().getSupportFragmentManager(), "userAlertLogin");
        }

        authService.getAuthorizationURL().observe(getViewLifecycleOwner(), s -> {
            loginBinding.webViewLog.loadUrl(s);
            Log.e("URL_LOGIN", s);
        });

        authService.getNameMLD().observe(getViewLifecycleOwner(), s -> {
            name = authService.getNameMLD().getValue();
            email = authService.getEmailMLD().getValue();

            SharedPreferences.Editor editor = appPref.edit();

            editor.putString("app_pref_username", name);
            editor.putString("app_pref_email", email);
            editor.apply();

            FragmentKt.findNavController(this).navigate(R.id.action_loginFragment_to_home2);
        });

        return loginBinding.getRoot();
    }

    public void guestFunc(SharedPreferences.Editor editor){
        Random random = new Random();
        int rand1 = random.nextInt(100);
        int rand2 = random.nextInt(1000);
        int rand3 = random.nextInt(7548);

        editor.putString("app_pref_username", "Guest_" + rand1+"_"+rand2+"_"+rand3);
        editor.putString("app_pref_email", rand1+"_"+rand2+"_"+rand3+"@guest");
        editor.apply();
    }
}