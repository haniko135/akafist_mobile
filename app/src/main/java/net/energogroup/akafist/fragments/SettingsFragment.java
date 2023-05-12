package net.energogroup.akafist.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.energogroup.akafist.R;
import net.energogroup.akafist.databinding.FragmentSettingsBinding;
import net.energogroup.akafist.recyclers.SettingsRecyclerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingsFragment extends Fragment {

    public FragmentSettingsBinding settingsBinding;
    private List<String> settingsNames = new ArrayList<>();

    public SettingsFragment() { }

    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        settingsNames.add("Изменить аккаунт");
        settingsNames.add("Размер шрифта");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        settingsBinding = FragmentSettingsBinding.inflate(inflater, container, false);

        settingsBinding.settingsRV.setLayoutManager(new LinearLayoutManager(getContext()));
        settingsBinding.settingsRV.setAdapter(new SettingsRecyclerAdapter(settingsNames, this));

        return settingsBinding.getRoot();
    }
}