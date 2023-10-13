package net.energogroup.akafist.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.energogroup.akafist.MainActivity;
import net.energogroup.akafist.databinding.FragmentMenuBinding;
import net.energogroup.akafist.recyclers.MenuRecyclerAdapter;
import net.energogroup.akafist.viewmodel.MenuViewModel;

/**
 * The class of the "Menu" fragment
 * @author Nastya Izotina
 * @version 1.0.0
 */
public class Menu extends Fragment {
    private MenuViewModel menuViewModel;
    public FragmentMenuBinding menuBinding;

    /**
     * Required class constructor
     */
    public Menu() { }

    /**
     * This method is responsible for creating the "Menu" fragment class
     * @return Menu
     */
    public static Menu newInstance() {
        return new Menu();
    }

    /**
     * This method prepares the activity for the fragment operation
     * @param savedInstanceState Bundle
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ViewModelProvider provider = new ViewModelProvider(this);
        menuViewModel = provider.get(MenuViewModel.class);

        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Меню");

        SharedPreferences appPref = getActivity().getSharedPreferences(MainActivity.APP_PREFERENCES, Context.MODE_PRIVATE);
        String userName = appPref.getString("app_pref_username", "guest");
        if(userName.startsWith("Guest_")){
            menuViewModel.firstSet("guest", getContext());
        }else {
            menuViewModel.firstSet("energogroup", getContext());
        }
        menuViewModel.getJson("menu", getContext());
    }

    /**
     * This method creates a fragment taking into account certain
     * fields in {@link Menu#onCreate(Bundle)}
     * @param inflater LayoutInflater
     * @param container ViewGroup
     * @param savedInstanceState bundle
     * @return View
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        menuBinding = FragmentMenuBinding.inflate(getLayoutInflater());

        Menu fr = this;
        menuBinding.menuList.setLayoutManager(new LinearLayoutManager(getContext()));
        menuViewModel.getMutableLiveData().observe(getViewLifecycleOwner(), homeBlocksModels -> menuBinding.menuList.setAdapter(new MenuRecyclerAdapter(homeBlocksModels,fr)));

        menuBinding.menuSwipe.setOnRefreshListener(() -> {
            menuBinding.menuSwipe.setRefreshing(true);
            if(menuViewModel.getBlocksModelList().size() == 7) {
                menuViewModel.getJson("menu", getContext());
                menuViewModel.getMutableLiveData().observe(getViewLifecycleOwner(), homeBlocksModels -> menuBinding.menuList.setAdapter(new MenuRecyclerAdapter(homeBlocksModels,fr)));
            }
            menuBinding.menuSwipe.setRefreshing(false);
        });

        return menuBinding.getRoot();
    }

}