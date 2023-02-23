package com.example.akafist.fragments;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.akafist.databinding.FragmentMenuBinding;
import com.example.akafist.recyclers.MenuRecyclerAdapter;
import com.example.akafist.viewmodel.MenuViewModel;

/**
 * Класс фрагмента "Меню"
 * @author Nastya Izotina
 * @version 1.0.0
 */
public class Menu extends Fragment {
    private MenuViewModel menuViewModel;
    public FragmentMenuBinding menuBinding;

    /**
     * Обязательный конструктор класса
     */
    public Menu() { }

    /**
     * Этот метод отвечает за создание класса фрагмента "Меню"
     * @return Menu
     */
    public static Menu newInstance() {
        return new Menu();
    }

    /**
     * Этот метод подготавливает активность к работе фрагмента
     * @param savedInstanceState Bundle
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ViewModelProvider provider = new ViewModelProvider(this);
        menuViewModel = provider.get(MenuViewModel.class);

        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Меню");

        menuViewModel.firstSet();
        menuViewModel.getJson("menu");
    }

    /**
     * Этот метод создаёт фрагмент с учетом определённых
     * в {@link Menu#onCreate(Bundle)} полей
     * @param inflater LayoutInflater
     * @param container ViewGroup
     * @param savedInstanceState bundle
     * @return View
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        menuBinding = FragmentMenuBinding.inflate(getLayoutInflater());

        Menu fr = this;
        menuBinding.menuList.setLayoutManager(new LinearLayoutManager(getContext()));
        menuViewModel.getMutableLiveData().observe(getViewLifecycleOwner(), homeBlocksModels -> menuBinding.menuList.setAdapter(new MenuRecyclerAdapter(homeBlocksModels,fr)));

        menuBinding.menuSwipe.setOnRefreshListener(() -> {
            menuBinding.menuSwipe.setRefreshing(true);
            if(menuViewModel.getBlocksModelList().size() == 7) {
                menuViewModel.getJson("menu");
                menuViewModel.getMutableLiveData().observe(getViewLifecycleOwner(), homeBlocksModels -> menuBinding.menuList.setAdapter(new MenuRecyclerAdapter(homeBlocksModels,fr)));
            }
            menuBinding.menuSwipe.setRefreshing(false);
        });

        return menuBinding.getRoot();
    }

}