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
import net.energogroup.akafist.databinding.FragmentOfflinePrayerBinding;
import net.energogroup.akafist.recyclers.OfflinePrayerAdapter;
import net.energogroup.akafist.viewmodel.OfflinePrayerViewModel;

/**
 * A fragment representing a list of Items.
 */
public class OfflinePrayerFragment extends Fragment {

    private static final String TAG = "OFFLINE_PRAYER_FRAGMENT";
    private FragmentOfflinePrayerBinding binding;
    private final OfflinePrayerAdapter adapter = new OfflinePrayerAdapter();
    private OfflinePrayerViewModel offlinePrayerViewModel;

    public static OfflinePrayerFragment newInstance() {
        return new OfflinePrayerFragment();
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public OfflinePrayerFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        offlinePrayerViewModel = new ViewModelProvider(this).get(OfflinePrayerViewModel.class);
        if (getActivity() != null) {
            ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Скачанные молитвы");
            offlinePrayerViewModel.getDownloadedPrayers(this);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentOfflinePrayerBinding.inflate(inflater, container, false);

        binding.offlinePrayerRv.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter.setFragment(this);
        adapter.setMainActivity((MainActivity) getActivity());
        offlinePrayerViewModel.getPrayersDB().observe(getViewLifecycleOwner(), prayers -> {
            if(prayers!= null)
                adapter.setPrayers(prayers);
            binding.offlinePrayerRv.setAdapter(adapter);
        });

        return binding.getRoot();
    }
}