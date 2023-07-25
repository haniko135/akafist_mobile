package net.energogroup.akafist.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.energogroup.akafist.databinding.FragmentSettingsBinding;
import net.energogroup.akafist.recyclers.SettingsRecyclerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * The class for global application settings
 * @author Nastya Izotina
 * @version 1.0.1
 */
public class SettingsFragment extends Fragment {

    public FragmentSettingsBinding settingsBinding;
    private List<String> settingsNames = new ArrayList<>();

    /**
     * Required class constructor
     */
    public SettingsFragment() { }

    /**
     * This method is responsible for creating the settings fragment class
     * @return
     */
    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }

    /**
     * This method creates fragment in context of activity
     * @param savedInstanceState If the fragment is being re-created from
     * a previous saved state, this is the state.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        settingsNames.add("Изменить аккаунт");
        settingsNames.add("Размер шрифта молитв");
    }

    /**
     * This method creates fragment itself
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        settingsBinding = FragmentSettingsBinding.inflate(inflater, container, false);

        settingsBinding.settingsRV.setLayoutManager(new LinearLayoutManager(getContext()));
        settingsBinding.settingsRV.setAdapter(new SettingsRecyclerAdapter(settingsNames, this));

        return settingsBinding.getRoot();
    }
}