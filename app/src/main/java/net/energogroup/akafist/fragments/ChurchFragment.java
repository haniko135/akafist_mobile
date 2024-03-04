package net.energogroup.akafist.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.energogroup.akafist.MainActivity;
import net.energogroup.akafist.databinding.FragmentChurchBinding;
import net.energogroup.akafist.viewmodel.ChurchViewModel;
import net.energogroup.akafist.recyclers.ServicesRecyclerAdapter;
import net.energogroup.akafist.recyclers.TypesRecyclerAdapter;

import java.util.stream.Collectors;

/**
 * The class of the fragment that displays the list of prayers
 * @author Nastya Izotina
 * @version 1.0.0
 */
public class ChurchFragment extends Fragment {

    private String date, dateTxt, name;
    public static ServicesRecyclerAdapter servicesRecyclerAdapter;
    public FragmentChurchBinding churchBinding;
    private ChurchViewModel churchViewModel;


    /**
     * Required class constructor
     */
    public ChurchFragment() { }

    /**
     * This method is responsible for creating a fragment class that outputs a list of prayers
     * @return A new instance of the Church Fragment class
     */
    public static ChurchFragment newInstance() {
        return new ChurchFragment();
    }

    /**
     * This method prepares the activity for the fragment operation
     * @param savedInstanceState Bundle
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            date = getArguments().getString("date");
        }
        ViewModelProvider provider = new ViewModelProvider(this);
        churchViewModel = provider.get(ChurchViewModel.class);
        if((AppCompatActivity)getActivity() != null){
            ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(dateTxt);
            churchViewModel.getJson(date, getContext());
        }
    }

    /**
     * This method creates a fragment taking into account certain
     * fields in {@link ChurchFragment#onCreate(Bundle)}
     * @param inflater LayoutInflater
     * @param container ViewGroup
     * @param savedInstanceState Bundle
     * @return View
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        churchBinding = FragmentChurchBinding.inflate(inflater, container, false);

        if(getActivity().getApplicationContext() != null){
            MainActivity.networkConnection.observe(getViewLifecycleOwner(), aBoolean -> {
                if(aBoolean){
                    churchBinding.noInternet.setVisibility(View.INVISIBLE);
                    churchViewModel.getLiveDataTxt().observe(getViewLifecycleOwner(), s -> {
                        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(s);
                        churchBinding.churchDateTxt.setText(s);
                    });
                    churchViewModel.getLiveNameTxt().observe(getViewLifecycleOwner(), s -> churchBinding.churchName.setText(s));

                    churchBinding.upRvChurch.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
                    churchViewModel.getMutableTypesList().observe(getViewLifecycleOwner(), typesModels -> churchBinding.upRvChurch.setAdapter(new TypesRecyclerAdapter(typesModels, this)));

                    churchBinding.downRvChurch.setLayoutManager(new LinearLayoutManager(getContext()));

                    //фильтр по текущему нажатому Id
                    churchViewModel.getCurId().observe(getViewLifecycleOwner(), integer -> churchViewModel.getMutableServicesList().observe(getViewLifecycleOwner(), servicesModels -> {
                        servicesRecyclerAdapter = new ServicesRecyclerAdapter(servicesModels.stream().filter(servicesModel ->
                                servicesModel.getType() == integer
                        ).collect(Collectors.toList()), this);
                        churchBinding.downRvChurch.setAdapter(servicesRecyclerAdapter);
                        //servicesRecyclerAdapter.setFragment(this);
                    }));
                }else {
                    churchBinding.noInternet.setVisibility(View.VISIBLE);
                }
            });
        }

        return churchBinding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        MainActivity mainActivity = (MainActivity) getActivity();
        mainActivity.getDbHelper().close();
    }
}