package net.energogroup.akafist.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.FragmentKt;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.snackbar.Snackbar;

import net.energogroup.akafist.MainActivity;
import net.energogroup.akafist.R;

import net.energogroup.akafist.databinding.FragmentHomeBinding;
import net.energogroup.akafist.recyclers.HomeRecyclerAdapter;
import net.energogroup.akafist.viewmodel.MenuViewModel;

/**
 * Main Page class
 * @author Nastya Izotina
 * @version 1.0.0
 */
public class Home extends Fragment {

    private MenuViewModel menuViewModel;
    public FragmentHomeBinding homeBinding;
    private SharedPreferences appPref;
    private String userName;
    private boolean isFirstTime = false;
    AppCompatActivity fragActivity;

    /**
     * Required class constructor
     */
    public Home() { }

    /**
     * This method is responsible for creating the "Home" fragment class
     * @return Home
     */
    public static Home newInstance() {
        return new Home();
    }

    /**
     * This method prepares the activity for the fragment operation
     * @param savedInstanceState Bundle
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("Home", "OnCreate");
        if(getActivity() != null) {
            initializeHome();
            onBackPressed();
        }

    }

    /**
     * This method creates a fragment taking into account certain
     * fields in {@link Home#onCreate(Bundle)}
     * @param inflater LayoutInflater
     * @param container ViewGroup
     * @param savedInstanceState Bundle
     * @return View
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (fragActivity != null){
            fragActivity.getSupportActionBar().setTitle(getResources().getString(R.string.home_title));
        }

        homeBinding = FragmentHomeBinding.inflate(getLayoutInflater());

        Home fr = this;
        homeBinding.homeRv.setLayoutManager(new LinearLayoutManager(getContext()));
        menuViewModel.getMutableLiveData().observe(getViewLifecycleOwner(), homeBlocksModels -> homeBinding.homeRv.setAdapter(new HomeRecyclerAdapter(homeBlocksModels, fr)));

        homeBinding.homeSwipe.setOnRefreshListener(() -> {
            homeBinding.homeSwipe.setRefreshing(true);
            if(menuViewModel.getBlocksModelList().size() == 7) {
                menuViewModel.getJson("home", getContext());
                menuViewModel.getMutableLiveData().observe(getViewLifecycleOwner(), homeBlocksModels -> homeBinding.homeRv.setAdapter(new HomeRecyclerAdapter(homeBlocksModels, fr)));
            }
            homeBinding.homeSwipe.setRefreshing(false);
        });

        SharedPreferences.Editor editor = appPref.edit();
        editor.apply();
        if (appPref.getBoolean("app_pref_first_login_snack", true)) {
            Snackbar.make(homeBinding.getRoot(), "Вы вошли как " + userName, Snackbar.LENGTH_LONG)
                    .setAction("Изменить", v -> {
                        appPref.edit().remove("app_pref_username").remove("app_pref_email").apply();
                        FragmentKt.findNavController(this).navigate(R.id.action_home2_to_loginFragment);
                    }).setActionTextColor(getContext().getColor(R.color.block_main))
                    .setBackgroundTint(getContext().getColor(R.color.white))
                    .setTextColor(getContext().getColor(R.color.black)).show();
            editor.putBoolean("app_pref_first_login_snack", false);
            editor.apply();
        }

        return homeBinding.getRoot();
    }

    /**
     * This method initializes the main parameters of the home page
     * and is using {@link Home#onCreate(Bundle)}
     * @return
     */
    private void initializeHome(){
        if (((AppCompatActivity)getActivity()).getSupportActionBar() != null){
            fragActivity = (AppCompatActivity)getActivity();
            ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(getResources().getString(R.string.home_title));
        }

        ViewModelProvider provider = new ViewModelProvider(this);
        menuViewModel = provider.get(MenuViewModel.class);

        appPref = getActivity().getSharedPreferences(MainActivity.APP_PREFERENCES, Context.MODE_PRIVATE);
        userName = appPref.getString("app_pref_username", "guest");
        Log.e("YOU_ARE_LOH", userName);
        if(userName.startsWith("Guest_")){
            menuViewModel.firstSet("guest", getContext());
        }else {
            menuViewModel.firstSet("energogroup", getContext());
        }

        menuViewModel.getJson("home", getContext());
    }

    /**
     * Processing clicks on the "Back" button in the bottom panel
     * @return
     */
    private void onBackPressed(){
        requireActivity().getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                requireActivity().finish();
                System.exit(0);
            }
        });
    }
}