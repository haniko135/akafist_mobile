package net.energogroup.akafist.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.energogroup.akafist.AkafistApplication;
import net.energogroup.akafist.R;
import net.energogroup.akafist.databinding.FragmentMenuOnlineTempleBinding;
import net.energogroup.akafist.recyclers.MenuOnlineTempleAdapter;
import net.energogroup.akafist.viewmodel.MenuOnlineTempleViewModel;

import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MenuOnlineTempleFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MenuOnlineTempleFragment extends Fragment {

    private static final String TAG = "MENU_ONLINE_TEMPLE_FRAGMENT";

    private MenuOnlineTempleViewModel menuOnlineTempleVM;
    private final MenuOnlineTempleAdapter menuOnlineTempleAdapter = new MenuOnlineTempleAdapter();
    private FragmentMenuOnlineTempleBinding binding;
    private String mode;

    public MenuOnlineTempleFragment() {}

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment MenuOnlineTempleFragment.
     */
    public static MenuOnlineTempleFragment newInstance() {
        return new MenuOnlineTempleFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null){
            mode  = getArguments().getString("mode");
        }

        menuOnlineTempleVM = new ViewModelProvider(this).get(MenuOnlineTempleViewModel.class);
        menuOnlineTempleVM.initialize(((AkafistApplication)getActivity().getApplication()).prAPI, getContext(), mode);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentMenuOnlineTempleBinding.inflate(inflater, container, false);

        if (((AppCompatActivity)getActivity()).getSupportActionBar() != null){
            String title  = Objects.equals(mode,  "menuOnlineTemple") ?
                    getResources().getString(R.string.menu_online_temple_title)  :
                    getResources().getString(R.string.menu_church_title);
            ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(title);
        }

        String menuDescribe = Objects.equals(mode, "menuOnlineTemple") ?
                getResources().getString(R.string.menu_online_temple_desc) :
                getResources().getString(R.string.menu_church_desc);
        binding.menuOnlineTempleTV.setText(menuDescribe);

        binding.menuOnlineTempleRV.setLayoutManager(new LinearLayoutManager(getContext()));
        menuOnlineTempleAdapter.setFragment(this);


        binding.menuOnlineTempleSwipe.setOnRefreshListener(() -> {
            if(Objects.equals(mode,  "menuChurch")) {
                binding.menuOnlineTempleSwipe.setRefreshing(true);
                if (menuOnlineTempleVM.getOnlineTempleList().size() == 1) {
                    menuOnlineTempleVM.getJson(((AkafistApplication)getActivity().getApplication()).prAPI);
                    menuOnlineTempleVM.getOnlineTempleListMLD().observe(
                            getViewLifecycleOwner(),
                            onlineTempleList -> {
                                menuOnlineTempleAdapter.setData(onlineTempleList);
                            }
                    );
                }
                binding.menuOnlineTempleSwipe.setRefreshing(false);
            }
        });

        menuOnlineTempleVM.getOnlineTempleListMLD().observe(getViewLifecycleOwner(),onlineTempleList -> {
            menuOnlineTempleAdapter.setData(onlineTempleList);
            binding.menuOnlineTempleRV.setAdapter(menuOnlineTempleAdapter);
        });

        return binding.getRoot();
    }
}