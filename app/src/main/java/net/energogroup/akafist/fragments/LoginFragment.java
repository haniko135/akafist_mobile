package net.energogroup.akafist.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.FragmentKt;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;

import net.energogroup.akafist.MainActivity;
import net.energogroup.akafist.R;
import net.energogroup.akafist.databinding.FragmentLoginBinding;
import net.energogroup.akafist.dialogs.DialogLogin;
import net.energogroup.akafist.viewmodel.LoginViewModel;

import java.util.Random;

/**
 * Authorization class
 * @author Nastya Izotina
 * @version 1.0.1
 */
public class LoginFragment extends Fragment {

    public FragmentLoginBinding loginBinding;
    private LoginViewModel authService;
    private String name, email;
    private SharedPreferences appPref;

    /**
     * Required class constructor
     */
    public LoginFragment() { }

    /**
     * This method is responsible for creating the "Login" fragment class
     * @return LoginFragment
     */
    public static LoginFragment newInstance() {
        return new LoginFragment();
    }

    /**
     * This method creates a fragment
     * @param savedInstanceState If the fragment is being re-created from
     * a previous saved state, this is the state.
     */
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

    /**
     * This method creates the fragment itself
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        loginBinding = FragmentLoginBinding.inflate(inflater, container, false);

        if (!appPref.getBoolean("app_pref_firstlogin", true) && !appPref.contains("app_pref_username") && !appPref.contains("app_pref_email")) {
            DialogLogin dialogLogin = new DialogLogin(authService, loginBinding, appPref, this);
            dialogLogin.show(requireActivity().getSupportFragmentManager(), "userAlertLogin");
        }

        authService.getAuthorizationURL().observe(getViewLifecycleOwner(), s -> {
            loginBinding.webViewLog.loadUrl(s);
            loginBinding.webViewLog.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        });

        authService.getNameMLD().observe(getViewLifecycleOwner(), s -> {
            name = s;
            authService.getEmailMLD().observe(getViewLifecycleOwner(), s1 -> {
                email = s1;

                SharedPreferences.Editor editor = appPref.edit();

                editor.putString("app_pref_username", name);
                editor.putString("app_pref_email", email);
                editor.apply();

                FragmentKt.findNavController(this).navigate(R.id.action_loginFragment_to_home2);
            });
        });

        return loginBinding.getRoot();
    }

    /**
     * This method assigns a name to a user with guest rights
     * @param editor
     */
    public void guestFunc(SharedPreferences.Editor editor){
        Random random = new Random();
        int rand1 = random.nextInt(100);
        int rand2 = random.nextInt(1000);
        int rand3 = random.nextInt(7548);

        editor.putString("app_pref_username", "Гость_" + rand1+"_"+rand2+"_"+rand3);
        editor.putString("app_pref_email", rand1+"_"+rand2+"_"+rand3+"@guest");
        editor.apply();
    }
}