package net.energogroup.akafist.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayoutMediator;

import net.energogroup.akafist.databinding.FragmentStarredBinding;
import net.energogroup.akafist.viewpagers.StarredCollectionAdapter;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link StarredFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StarredFragment extends Fragment {

    public FragmentStarredBinding starredBinding;

    public StarredFragment() {}

    public static StarredFragment newInstance() {
        return new StarredFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if((AppCompatActivity)getActivity() != null) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Избранное");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        starredBinding = FragmentStarredBinding.inflate(getLayoutInflater(), container, false);

        starredBinding.starredPager.setAdapter(new StarredCollectionAdapter(getActivity()));

        new TabLayoutMediator(starredBinding.starredTabs, starredBinding.starredPager,
                (tab, position) -> {
                    if(position == 0){
                        tab.setText("Молитвы");
                    }else if(position == 1){
                        tab.setText("Аудио-молитвы");
                    }else if(position == 2){
                        tab.setText("Беседы");
                    }
                }).attach();

        return starredBinding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}