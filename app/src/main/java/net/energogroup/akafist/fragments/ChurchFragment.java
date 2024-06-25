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
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import net.energogroup.akafist.MainActivity;
import net.energogroup.akafist.R;
import net.energogroup.akafist.databinding.FragmentChurchBinding;
import net.energogroup.akafist.viewmodel.ChurchViewModel;
import net.energogroup.akafist.recyclers.ServicesRecyclerAdapter;
import net.energogroup.akafist.recyclers.TypesRecyclerAdapter;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * The class of the fragment that displays the list of prayers
 * @author Nastya Izotina
 * @version 1.0.0
 */
public class ChurchFragment extends Fragment {

    private static final String TAG = "CHURCH_FRAGMENT";
    public static final String LAST_PRAYER_PREF_NAME = "app_pref_prayer_scroll_pos_";
    private String date, dateTxt, name;
    public static ServicesRecyclerAdapter servicesRecyclerAdapter;
    public FragmentChurchBinding churchBinding;
    private ChurchViewModel churchViewModel;
    private SharedPreferences appPref;
    private String lastViewedPrayerKey;


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
        if(getActivity() != null){
            ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(dateTxt);
            churchViewModel.getJson(date, getContext());
        }

        appPref = requireActivity().getSharedPreferences(MainActivity.APP_PREFERENCES,Context.MODE_PRIVATE);
        getLastViewedPrayer();
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

                    //filter by current pressed ID
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
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if(lastViewedPrayerKey != null) {
            MenuHost menuHost = requireActivity();
            menuHost.addMenuProvider(new MenuProvider() {
                @Override
                public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                    if(menu.findItem(R.id.menuContinueMyRead) != null)
                        menu.removeItem(R.id.menuContinueMyRead);
                    MenuItem menuItem = menu.add(Menu.NONE, R.id.menuContinueRead, Menu.NONE, "Продолжить чтение");
                    menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
                }

                @Override
                public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                    if (menuItem.getItemId() == R.id.menuContinueRead) {
                        continueRead();
                        return true;
                    }
                    return false;
                }
            }, getViewLifecycleOwner(), Lifecycle.State.RESUMED);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        MainActivity mainActivity = (MainActivity) getActivity();
        mainActivity.getDbHelper().close();
    }

    public void getLastViewedPrayer(){
        Map<String, ?> allEntries = appPref.getAll();

        String lastSavedKey = null;
        long lastTimestamp = 0;

        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            String key = entry.getKey();
            if (key.startsWith(LAST_PRAYER_PREF_NAME) && key.endsWith("_timestamp")) {
                long timestamp = (Long) entry.getValue();
                if (timestamp > lastTimestamp) {
                    lastTimestamp = timestamp;
                    lastSavedKey = key.substring(0, key.length() - "_timestamp".length());
                }
            }
        }

        lastViewedPrayerKey = lastSavedKey;
    }


    private void continueRead(){
        String dataForBundle = lastViewedPrayerKey.substring(LAST_PRAYER_PREF_NAME.length());
        String prevMenuData = dataForBundle.split("/")[0];
        String prayerIdData = dataForBundle.split("/")[1];

        Bundle bundle = new Bundle();
        bundle.putString("prevMenu", prevMenuData);
        bundle.putInt("prayerId", Integer.parseInt(prayerIdData));
        bundle.putString("mode", "prayer_read");
        FragmentKt.findNavController(this).navigate(R.id.action_churchFragment_to_prayerFragment, bundle);
    }
}