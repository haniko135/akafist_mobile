package net.energogroup.akafist.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayoutMediator;

import net.energogroup.akafist.databinding.FragmentPrayerRuleBinding;
import net.energogroup.akafist.viewpagers.PrayerRuleAdapter;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PrayerRuleFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PrayerRuleFragment extends Fragment {

    private FragmentPrayerRuleBinding prayerRuleBinding;

    public PrayerRuleFragment() { }

    public static PrayerRuleFragment newInstance() {
        return new PrayerRuleFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        prayerRuleBinding = FragmentPrayerRuleBinding.inflate(getLayoutInflater(), container, false);

        prayerRuleBinding.prayerRuleVp.setAdapter(new PrayerRuleAdapter(getActivity()));

        new TabLayoutMediator(prayerRuleBinding.prayerRuleTabs, prayerRuleBinding.prayerRuleVp,
                (tab, position) -> {
                    if (position == 0) {
                        tab.setText("Молитвы");
                    }else {
                        tab.setText("Аудио-молитвы");
                    }
                }).attach();

        return prayerRuleBinding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}