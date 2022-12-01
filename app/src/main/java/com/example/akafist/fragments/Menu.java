package com.example.akafist.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.fragment.FragmentKt;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.akafist.R;
import com.example.akafist.databinding.FragmentMenuBinding;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Menu#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Menu extends Fragment {

    public Menu() {
        // Required empty public constructor
    }

    public static Menu newInstance() {
        Menu fragment = new Menu();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Меню");
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_menu, container, false);
        // Inflate the layout for this fragment
        view.findViewById(R.id.skype_confs).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentKt.findNavController(getParentFragment()).navigate(R.id.action_menu_to_skypesFragment);
            }
        });
        view.findViewById(R.id.online_Michael).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(getContext(), "online_Michael", Toast.LENGTH_SHORT).show();
                String urlToMichael =  "http://radio.zakonbozhiy.ru:8000/live.mp3";
                Bundle bundle = new Bundle();
                bundle.putString("urlToSound", urlToMichael);
                FragmentKt.findNavController(getParentFragment()).navigate(R.id.onlineTempleFragment, bundle);
            }
        });
        view.findViewById(R.id.online_Varvara).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "online_Varvara", Toast.LENGTH_SHORT).show();
                String urlToVarvara =  "http://radio.zakonbozhiy.ru:8010/kem.mp3";
                Bundle bundle = new Bundle();
                bundle.putString("urlToSound", urlToVarvara);
                FragmentKt.findNavController(getParentFragment()).navigate(R.id.onlineTempleFragment, bundle);
            }
        });
        view.findViewById(R.id.links).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "links", Toast.LENGTH_SHORT).show();
            }
        });
        view.findViewById(R.id.day_Michael).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "day_Michael", Toast.LENGTH_SHORT).show();
            }
        });
        view.findViewById(R.id.everyday_title).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentKt.findNavController(getParentFragment()).navigate(R.id.action_menu_to_everydayFragment);
            }
        });
        view.findViewById(R.id.morn_and_even_title).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(getContext(), "morn_and_even_title", Toast.LENGTH_SHORT).show();
                FragmentKt.findNavController(getParentFragment()).navigate(R.id.action_menu_to_mornAndEvenMolitvyFragment);
            }
        });
        view.findViewById(R.id.psaltir_title).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "psaltir_title", Toast.LENGTH_SHORT).show();
            }
        });
        view.findViewById(R.id.molitvy_title).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "molitvy_title", Toast.LENGTH_SHORT).show();
            }
        });

        return view;//inflater.inflate(R.layout.fragment_menu, container, false);
    }
}