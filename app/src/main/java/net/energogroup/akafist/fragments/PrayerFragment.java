package net.energogroup.akafist.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.FragmentKt;

import android.text.Html;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.energogroup.akafist.MainActivity;
import net.energogroup.akafist.R;

import net.energogroup.akafist.databinding.FragmentPrayerBinding;
import net.energogroup.akafist.models.PrayersModels;
import net.energogroup.akafist.viewmodel.PrayerViewModel;
import net.energogroup.akafist.viewmodel.StarredViewModel;

import java.util.Objects;

/**
 * Prayer Fragment Class
 * @author Nastya
 * @version 1.0.0
 */
public class PrayerFragment extends Fragment {

    private float textSize;
    private String prevMenu, mode;
    private int prayerId;
    private PrayerViewModel prayerViewModel;
    private StarredViewModel starredViewModel;
    private SharedPreferences appPref;
    private SQLiteDatabase db;
    FragmentPrayerBinding binding;

    /**
     * Required class constructor
     */
    public PrayerFragment() { }

    /**
     * This method is responsible for creating the prayer fragment class
     * @return PrayerFragment
     */
    public static PrayerFragment newInstance() {
        return new PrayerFragment();
    }

    /**
     * This method prepares the activity for the fragment operation
     * @param savedInstanceState Bundle
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appPref = requireActivity().getSharedPreferences(MainActivity.APP_PREFERENCES, Context.MODE_PRIVATE);
        if(getArguments() != null){
            prevMenu = getArguments().getString("prevMenu");
            prayerId = getArguments().getInt("prayerId");
            mode = getArguments().getString("mode");
        }
        MainActivity mainActivity = (MainActivity) getActivity();
        db = mainActivity.getDbHelper().getReadableDatabase();

        ViewModelProvider provider = new ViewModelProvider(this);
        starredViewModel = provider.get(StarredViewModel.class);
        prayerViewModel = provider.get(PrayerViewModel.class);
        if(Objects.equals(mode,"prayer_read")) prayerViewModel.getJson(prevMenu, prayerId, getContext());
        else if (Objects.equals(mode, "prayer_rule")) {
            prayerViewModel.setPrayersModelsMutableLiveData(starredViewModel.getPrayerModelsCollectionItem(prayerId, db));
        }
    }

    /**
     * This method defines the fields of the fragment class, after its creation is completed
     * @param view View
     * @param savedInstanceState Bundle
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(getArguments() != null){
            prevMenu = getArguments().getString("prevMenu");
            prayerId = getArguments().getInt("prayerId");
        }
    }

    /**
     * This method creates a fragment taking into account certain
     * fields in {@link PrayerFragment#onCreate(Bundle)}
     * @param inflater LayoutInflater
     * @param container ViewGroup
     * @param savedInstanceState Bundle
     * @return View
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if(getArguments() != null){
            prevMenu = getArguments().getString("prevMenu");
            prayerId = getArguments().getInt("largeText");
            if(appPref.contains("app_pref_text_size")){
                textSize = appPref.getFloat("app_pref_text_size", getResources().getDimension(R.dimen.text_prayer));
            } else{
                textSize = getResources().getDimension(R.dimen.text_prayer);
            }
        }

        //the name of the prayer in the ToolBar
        prayerViewModel.getPrayersModelsMutableLiveData().observe(getViewLifecycleOwner(), prayersModels -> ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(prayersModels.getNamePrayer()));

        //Initial Fragment settings
        binding = FragmentPrayerBinding.inflate(getLayoutInflater());
        binding.textPrayer.setTextSize(convertToPx());
        //binding.textPrayer.setMovementMethod(new ScrollingMovementMethod());
        prayerViewModel.getPrayersModelsMutableLiveData().observe(getViewLifecycleOwner(), prayersModels -> binding.textPrayer.setText(Html.fromHtml(prayersModels.getTextPrayer(), Html.FROM_HTML_MODE_COMPACT)));

        //something that didn't work out very well
        binding.prayerOptions.getMenu().getItem(0).setChecked(false);

        prayerViewModel.getPrayersModelsMutableLiveData().observe(getViewLifecycleOwner(), prayersModels -> Log.e("PRAYER_FRAG", prayersModels.getNext()+" - "+prayersModels.getPrev()));

        prayerViewModel.getPrayersModelsMutableLiveData().observe(getViewLifecycleOwner(), prayersModels -> {
            //bottom menu
            binding.prayerOptions.setOnItemSelectedListener(item -> {
                if (item.getItemId() == R.id.zoom_out){
                    textSize--;
                    saveTextSize(textSize);
                    binding.textPrayer.setTextSize(convertToPx());
                    Log.i("PRAYER", Float.toString(textSize));
                    return true;
                } else if (item.getItemId() == R.id.to_menu) {
                    Bundle bundle1 = new Bundle();
                    bundle1.putString("date",prevMenu);
                    FragmentKt.findNavController(getParentFragment()).navigate(R.id.action_prayerFragment_to_churchFragment, bundle1);
                    return true;
                } else if (item.getItemId() == R.id.zoom_in) {
                    textSize++;
                    saveTextSize(textSize);
                    binding.textPrayer.setTextSize(convertToPx());
                    Log.i("PRAYER", Float.toString(textSize));
                    return true;
                } else if (item.getItemId() == R.id.next_prayer) {
                    if(prayersModels.getNext() == 0){           //тут переписать на mutablelivedata
                        Bundle bundle3 = new Bundle();
                        bundle3.putString("date",prevMenu);
                        FragmentKt.findNavController(getParentFragment()).navigate(R.id.action_prayerFragment_to_churchFragment, bundle3);
                        return true;
                    } else {
                        Bundle bundle4 = new Bundle();
                        bundle4.putString("prevMenu", prevMenu);
                        bundle4.putInt("prayerId", prayersModels.getNext());
                        bundle4.putString("mode", mode);
                        FragmentKt.findNavController(getParentFragment()).navigate(R.id.action_prayerFragment_self, bundle4);
                        return true;
                    }
                } else if (item.getItemId() == R.id.prev_prayer) {
                    if(prayersModels.getPrev() == 0){           //тут переписать на mutablelivedata
                        Bundle bundle5 = new Bundle();
                        bundle5.putString("date",prevMenu);
                        FragmentKt.findNavController(getParentFragment()).navigate(R.id.action_prayerFragment_to_churchFragment, bundle5);
                        return true;
                    }else {
                        Bundle bundle2 = new Bundle();
                        bundle2.putString("prevMenu", prevMenu);
                        bundle2.putInt("prayerId", prayersModels.getPrev());
                        bundle2.putString("mode", mode);
                        FragmentKt.findNavController(getParentFragment()).navigate(R.id.action_prayerFragment_self, bundle2);
                        return true;
                    }
                }
                return false;
            });
        });

        return binding.getRoot();
    }

    /**
     * This method converts the size to pixels
     * @return float
     */
    private float convertToPx(){
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, textSize, getContext().getResources().getDisplayMetrics());
    }

    /**
     * This method saves text size in SharedPreferences
     * @param textSize actual text size
     */
    public void saveTextSize(float textSize){
        SharedPreferences.Editor editor = appPref.edit();
        editor.putFloat("app_pref_text_size", textSize);
        editor.apply();
    }
}