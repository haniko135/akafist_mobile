package net.energogroup.akafist.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuHost;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.FragmentKt;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import net.energogroup.akafist.AkafistApplication;
import net.energogroup.akafist.MainActivity;
import net.energogroup.akafist.R;
import net.energogroup.akafist.databinding.FragmentPsaltirBinding;
import net.energogroup.akafist.recyclers.PsaltirRecyclerAdapter;
import net.energogroup.akafist.viewmodel.PsaltirViewModel;

import java.util.Map;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PsaltirFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PsaltirFragment extends Fragment {

    public static final String TAG = "PSALTIR_FRAGMENT";
    private static final String LAST_PSALTIR_PREF_NAME = "app_pref_psaltir_scroll_pos_";
    private PsaltirViewModel viewModel;
    private int id;
    private String lastViewedPsaltirKey;
    public FragmentPsaltirBinding psaltirBinding;
    private LinearLayoutManager psaltirLM;
    private SharedPreferences appPref;

    public PsaltirFragment() { }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment PsaltirFragment.
     */
    public static PsaltirFragment newInstance() {
        return new PsaltirFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appPref = requireActivity().getSharedPreferences(MainActivity.APP_PREFERENCES,Context.MODE_PRIVATE);

        if (getArguments() != null) {
            id = getArguments().getInt("id");
        }
        viewModel = new ViewModelProvider(this).get(PsaltirViewModel.class);
        if(getActivity() != null) {
            ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Псалтирь");
            viewModel.getJson(((AkafistApplication) getActivity().getApplication()).prAPI, id);
        }

        getLastViewedPsaltir();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(getActivity() != null) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Псалтирь");
        }

        psaltirBinding = FragmentPsaltirBinding.inflate(inflater, container, false);
        Fragment fr = this;

        viewModel.getPsaltirModelMLD().observe(getViewLifecycleOwner(), psaltirModel -> {
            psaltirBinding.psaltirName.setText(psaltirModel.getName());
            if(Objects.equals(psaltirModel.getDesc(), null)){
                psaltirBinding.psaltirDesc.setVisibility(View.VISIBLE);
                psaltirBinding.psaltirDesc.setText(psaltirModel.getDesc());
            }else {
                psaltirBinding.psaltirDesc.setVisibility(View.GONE);
            }

            if (!psaltirModel.getKafismas().isEmpty()){
                psaltirLM = new LinearLayoutManager(getContext());
                psaltirBinding.psaltirRV.setLayoutManager(psaltirLM);

                PsaltirRecyclerAdapter psaltirAdapter = new PsaltirRecyclerAdapter();
                psaltirAdapter.setData(psaltirModel.getKafismas());
                psaltirAdapter.setBlockId(id);
                psaltirAdapter.setFragment(fr);
                psaltirAdapter.init();

                psaltirBinding.psaltirRV.setAdapter(psaltirAdapter);
            }
        });

        return psaltirBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if(lastViewedPsaltirKey != null){
            MenuHost menuHost = requireActivity();

            menuHost.addMenuProvider(new MenuProvider() {
                @Override
                public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                    MenuItem menuItem = menu.add(Menu.NONE, R.id.menuContinueRead, Menu.NONE, "Продолжить чтение");
                    menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
                }

                @Override
                public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                    if (menuItem.getItemId() == R.id.menuContinueRead){
                        continueRead();
                        return true;
                    }
                    return false;
                }
            }, getViewLifecycleOwner(), Lifecycle.State.RESUMED);
        }
    }

    public void getLastViewedPsaltir(){
        Map<String, ?> allEntries = appPref.getAll();

        String lastSavedKey = null;
        long lastTimestamp = 0;

        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            String key = entry.getKey();
            if (key.startsWith(LAST_PSALTIR_PREF_NAME) && key.endsWith("_timestamp")) {
                long timestamp = (Long) entry.getValue();
                if (timestamp > lastTimestamp) {
                    lastTimestamp = timestamp;
                    lastSavedKey = key.substring(0, key.length() - "_timestamp".length());
                }
            }
        }

        lastViewedPsaltirKey = lastSavedKey;
    }

    private void continueRead(){
        String dataForBundle = lastViewedPsaltirKey.substring(LAST_PSALTIR_PREF_NAME.length());
        String blockIdData = dataForBundle.split("/")[0];
        String prayerIdData = dataForBundle.split("/")[1];

        Bundle bundle = new Bundle();
        bundle.putInt("blockId", Integer.parseInt(blockIdData));
        bundle.putInt("prayerId", Integer.parseInt(prayerIdData));
        bundle.putString("mode", "psaltir_read");
        FragmentKt.findNavController(this).navigate(R.id.action_psaltirFragment_to_prayerFragment, bundle);
    }
}