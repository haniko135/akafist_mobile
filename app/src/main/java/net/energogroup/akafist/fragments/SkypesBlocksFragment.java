package net.energogroup.akafist.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.energogroup.akafist.MainActivity;
import net.energogroup.akafist.databinding.FragmentSkypesBlocksBinding;
import net.energogroup.akafist.recyclers.SkypesGridRecyclerAdapter;
import net.energogroup.akafist.viewmodel.SkypeViewModel;

/**
 * A class that displays a list of links to conferences
 * @author Nastya Izotina
 * @version 1.0.0
 */
public class SkypesBlocksFragment extends Fragment {

    private SkypeViewModel skypeViewModel;
    private String nameTitle;
    private int urlId;

    public FragmentSkypesBlocksBinding skypesBlocksBinding;

    /**
     * Required class constructor
     */
    public SkypesBlocksFragment() { }

    /**
     * This method creates the SkypesBlocksFragment fragment class
     * @return New instance of the SkypesBlocksFragment class
     */
    public static SkypesBlocksFragment newInstance() {
        return new SkypesBlocksFragment();
    }

    /**
     * This method prepares the activity for the fragment operation taking into account
     * saved data
     * @param savedInstanceState Bundle - Saved fragment data
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            nameTitle = getArguments().getString("nameTitle");
            urlId = getArguments().getInt("urlId");
        }
        if((AppCompatActivity)getActivity() != null){
            ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(nameTitle);
            ViewModelProvider provider = new ViewModelProvider(this);
            skypeViewModel = provider.get(SkypeViewModel.class);
            skypeViewModel.getJsonSkypeBlock(urlId, getContext());
        }
    }

    /**
     * This method creates a fragment taking into account certain
     * fields in {@link SkypesBlocksFragment#onCreate(Bundle)}
     * @param inflater LayoutInflater
     * @param container ViewGroup
     * @param savedInstanceState Bundle - Saved fragment state
     * @return View - Displays a fragment on the screen
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        skypesBlocksBinding = FragmentSkypesBlocksBinding.inflate(inflater, container, false);

        if (getActivity().getApplicationContext() != null){
            MainActivity.networkConnection.observe(getViewLifecycleOwner(), isAvailable->{
                if(isAvailable){
                    skypesBlocksBinding.noInternet4.setVisibility(View.INVISIBLE);
                    skypesBlocksBinding.groupBlocks.setLayoutManager(new GridLayoutManager(getContext(), 2));
                    skypeViewModel.getConfsMutableLiveData().observe(getViewLifecycleOwner(),
                            view -> skypesBlocksBinding.groupBlocks.setAdapter(new SkypesGridRecyclerAdapter(view)));
                } else {
                    skypesBlocksBinding.noInternet4.setVisibility(View.VISIBLE);
                }
            });
        }
        
        return skypesBlocksBinding.getRoot();
    }
}