package net.energogroup.akafist.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuHost;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.FragmentKt;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
    private final HomeRecyclerAdapter homeAdapter = new HomeRecyclerAdapter();
    private final boolean isFirstTime = false;
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
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (fragActivity != null){
            fragActivity.getSupportActionBar().setTitle(getResources().getString(R.string.home_title));
        }

        homeBinding = FragmentHomeBinding.inflate(getLayoutInflater());

        homeBinding.homeRv.setLayoutManager(new LinearLayoutManager(getContext()));
        homeAdapter.setFragment(this);
        menuViewModel.getMutableLiveData().observe(
                getViewLifecycleOwner(),
                homeBlocksModels ->{
                    homeAdapter.setData(homeBlocksModels);
                    homeBinding.homeRv.setAdapter(homeAdapter);
                }
        );

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

    @Override
    public void onDestroy() {
        super.onDestroy();
        appPref.edit().putBoolean("app_pref_player", false).apply();
    }

    /**
     * This method initializes the main parameters of the home page
     * and is using {@link Home#onCreate(Bundle)}
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
        if(userName.startsWith("Гость_")){
            menuViewModel.firstSet("guest", getContext());
        }else {
            menuViewModel.firstSet("energogroup", getContext());
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MenuHost menuHost = requireActivity();
        menuHost.addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menu.removeItem(R.id.menuContinueRead);
                menu.removeItem(R.id.menuContinueMyRead);
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                return false;
            }
        }, getViewLifecycleOwner(), Lifecycle.State.RESUMED);
    }
}