package net.energogroup.akafist.fragments;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.energogroup.akafist.R;

import net.energogroup.akafist.databinding.FragmentHomeBinding;
import net.energogroup.akafist.recyclers.HomeRecyclerAdapter;
import net.energogroup.akafist.viewmodel.MenuViewModel;

/**
 * Класс фрагмента "Главная"
 * @author Nastya Izotina
 * @version 1.0.0
 */
public class Home extends Fragment {

    private MenuViewModel menuViewModel;
    public FragmentHomeBinding homeBinding;
    AppCompatActivity fragActivity;

    /**
     * Обязательный конструктор класса
     */
    public Home() {
    }

    /**
     * Этот метод отвечает за создание класса фрагмента "Главная"
     * @return Home
     */
    public static Home newInstance() {
        return new Home();
    }

    /**
     * Этот метод подготавливает активность к работе фрагмента
     * @param savedInstanceState Bundle
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if((AppCompatActivity)getActivity() != null) {
            if (((AppCompatActivity)getActivity()).getSupportActionBar() != null){
                fragActivity = (AppCompatActivity)getActivity();
                ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(getResources().getString(R.string.home_title));
            }
            ViewModelProvider provider = new ViewModelProvider(this);
            menuViewModel = provider.get(MenuViewModel.class);
            menuViewModel.firstSet();
            menuViewModel.getJson("home");
        }

    }

    /**
     * Этот метод создаёт фрагмент с учетом определённых
     * в {@link Home#onCreate(Bundle)} полей
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
                menuViewModel.getJson("home");
                menuViewModel.getMutableLiveData().observe(getViewLifecycleOwner(), homeBlocksModels -> homeBinding.homeRv.setAdapter(new HomeRecyclerAdapter(homeBlocksModels, fr)));
            }
            homeBinding.homeSwipe.setRefreshing(false);
        });

        return homeBinding.getRoot();
    }
}