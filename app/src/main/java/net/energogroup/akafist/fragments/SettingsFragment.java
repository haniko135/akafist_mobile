package net.energogroup.akafist.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.FragmentKt;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.energogroup.akafist.MainActivity;
import net.energogroup.akafist.R;
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
    private final SettingsRecyclerAdapter settingsRecyclerAdapter = new SettingsRecyclerAdapter();
    private final List<String> settingsNames = new ArrayList<>();
    private final List<Object> settingsLinks = new ArrayList<>();

    /**
     * Required class constructor
     */
    public SettingsFragment() { }

    /**
     * This method is responsible for creating the settings fragment class
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
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(getResources().getString(R.string.settings_name));
        settingsNames.add("Изменить аккаунт");
        settingsLinks.add(R.id.action_settingsFragment_to_loginFragment);
        settingsNames.add("Размер шрифта молитв");
        settingsLinks.add("userTextSizeDialog");
        settingsNames.add("Связь с разработчиком");
        settingsLinks.add("contactDeveloper");
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
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        settingsBinding = FragmentSettingsBinding.inflate(inflater, container, false);

        SharedPreferences appPref = getActivity().getSharedPreferences(MainActivity.APP_PREFERENCES, Context.MODE_PRIVATE);
        String userName = appPref.getString("app_pref_username", "guest");
        settingsBinding.userName.setText(userName);

        settingsBinding.settingsRV.setLayoutManager(new LinearLayoutManager(getContext()));
        settingsRecyclerAdapter.setData(settingsNames, settingsLinks);
        settingsRecyclerAdapter.setFragment(this);
        settingsBinding.settingsRV.setAdapter(settingsRecyclerAdapter);

        settingsBinding.userImage.setOnClickListener(view -> FragmentKt.findNavController(this).navigate(R.id.action_settingsFragment_to_accountFragment));

        return settingsBinding.getRoot();
    }
}