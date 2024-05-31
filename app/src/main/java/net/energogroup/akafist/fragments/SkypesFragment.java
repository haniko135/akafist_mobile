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

import net.energogroup.akafist.MainActivity;
import net.energogroup.akafist.databinding.FragmentSkypesBinding;
import net.energogroup.akafist.recyclers.SkypesRecyclerAdapter;
import net.energogroup.akafist.viewmodel.SkypeViewModel;

/**
 * Fragment class with a list of online conferences
 * @author Nastya Izotina
 * @version 1.0.0
 */
public class SkypesFragment extends Fragment {

    private SkypeViewModel skypeViewModel;
    public FragmentSkypesBinding skypesBinding;

    /**
     * Required class constructor
     */
    public SkypesFragment() { }

    /**
     * This method creates the SkypesFragment fragment class
     * @return A new instance of the SkypesFragment class
     */
    public static SkypesFragment newInstance() {
        return new SkypesFragment();
    }

    /**
     * This method prepares the activity for the fragment operation taking into account
     * the stored data
     * @param savedInstanceState Bundle - Сохранённые данные фрагмента
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getActivity() != null) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Конференции по группам");
            ViewModelProvider provider = new ViewModelProvider(this);
            skypeViewModel = provider.get(SkypeViewModel.class);
            skypeViewModel.getJsonSkype(getContext());
        }
    }

    /**
     * This method creates a fragment taking into account certain
     * fields in {@link SkypesFragment#onCreate(Bundle)}
     * @param inflater LayoutInflater
     * @param container ViewGroup
     * @param savedInstanceState Bundle - Saved fragment state
     * @return View - Displays a fragment on the screen
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        skypesBinding = FragmentSkypesBinding.inflate(getLayoutInflater());

        if(getActivity() != null) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Конференции по группам");

            if(getActivity().getApplicationContext() != null){
                MainActivity.networkConnection.observe(getViewLifecycleOwner(), isChecked->{
                    if(isChecked){
                        skypesBinding.noInternet3.setVisibility(View.INVISIBLE);
                        skypesBinding.skypesList.setLayoutManager(new LinearLayoutManager(getContext()));
                        skypeViewModel.getSkypesMutableLiveData().observe(getViewLifecycleOwner(),
                                skypesConfs -> skypesBinding.skypesList.setAdapter(new SkypesRecyclerAdapter(skypesConfs, this)));

                        skypesBinding.confsList.setLayoutManager(new LinearLayoutManager(getContext()));
                        skypeViewModel.getConfsMutableLiveData().observe(getViewLifecycleOwner(),
                                skypesConfs -> skypesBinding.confsList.setAdapter(new SkypesRecyclerAdapter(skypesConfs ,this)));
                    } else {
                        skypesBinding.noInternet3.setVisibility(View.VISIBLE);
                    }
                });
            }
        }

        return skypesBinding.getRoot();
    }
}