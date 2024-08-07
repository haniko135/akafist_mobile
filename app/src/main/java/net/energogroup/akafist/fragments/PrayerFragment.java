package net.energogroup.akafist.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Observable;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.text.HtmlCompat;
import androidx.core.view.MenuHost;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.FragmentKt;

import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.energogroup.akafist.AkafistApplication;
import net.energogroup.akafist.MainActivity;
import net.energogroup.akafist.R;

import net.energogroup.akafist.api.PrAPI;
import net.energogroup.akafist.databinding.FragmentPrayerBinding;
import net.energogroup.akafist.service.SavePrayerData;
import net.energogroup.akafist.viewmodel.PrayerViewModel;
import net.energogroup.akafist.viewmodel.StarredViewModel;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Prayer Fragment Class
 * @author Nastya
 * @version 1.0.0
 */
public class PrayerFragment extends Fragment implements SavePrayerData {

    private final String TAG = "PRAYER_FRAGMENT";
    private PrAPI prAPI;
    private float textSize;
    private String prevMenu, mode;
    private int prayerId, blockId;
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
            Log.d(TAG, getArguments().toString());
            prevMenu = getArguments().getString("prevMenu");
            prayerId = getArguments().getInt("prayerId");
            blockId = getArguments().getInt("blockId");
            mode = getArguments().getString("mode");
        }
        prAPI = ((AkafistApplication)getActivity().getApplication()).prAPI;

        MainActivity mainActivity = (MainActivity) getActivity();
        db = mainActivity.getDbHelper().getWritableDatabase();

        ViewModelProvider provider = new ViewModelProvider(this);
        starredViewModel = provider.get(StarredViewModel.class);
        prayerViewModel = provider.get(PrayerViewModel.class);
        if(Objects.equals(mode,"prayer_read")) {
            prayerViewModel.getJson(prAPI, prevMenu, String.valueOf(prayerId));
        }
        else if (Objects.equals(mode,"psaltir_read")){
            //prayerViewModel.getJsonPsaltir(blockId, prayerId, getContext());
            prayerViewModel.getJsonPsaltir(prAPI, blockId, prayerId);
        }
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
            blockId = getArguments().getInt("blockId");
            prayerId = getArguments().getInt("prayerId");

            MenuHost menuHost = requireActivity();
            menuHost.addMenuProvider(new MenuProvider() {
                @Override
                public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                    menu.removeItem(R.id.menuContinueRead);
                    menu.removeItem(R.id.menuContinueMyRead);
                }

                @Override
                public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                    return false;
                }
            }, getViewLifecycleOwner(), Lifecycle.State.RESUMED);
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
            blockId = getArguments().getInt("blockId");
            prayerId = getArguments().getInt("largeText");
            if(appPref.contains("app_pref_text_size")){
                textSize = appPref.getFloat("app_pref_text_size", getResources().getDimension(R.dimen.text_prayer));
            } else{
                textSize = getResources().getDimension(R.dimen.text_prayer);
            }
        }

        //Initial Fragment settings
        binding = FragmentPrayerBinding.inflate(getLayoutInflater());

        prayerViewModel.getIsRendered().observe(getViewLifecycleOwner(), aBoolean -> {
            if(!aBoolean){
                binding.prayerLoad.setVisibility(View.VISIBLE);
            }else {
                binding.prayerLoad.setVisibility(View.GONE);

                //the name of the prayer in the ToolBar
                prayerViewModel.getPrayersModelsMutableLiveData()
                        .observe(getViewLifecycleOwner(),
                                prayersModels -> ((AppCompatActivity)getActivity())
                                        .getSupportActionBar()
                                        .setTitle(prayersModels.getName())
                        );


                prayerViewModel.getPrayerTextArrMLD()
                        .observe(getViewLifecycleOwner(), prayerTextArr -> {
                            prayerTextArr.forEach(a->{
                                TextView textView = new TextView(getContext());
                                textView.setTextSize(convertToPx());
                                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                                    Typeface textFont = getResources().getFont(R.font.hirmos_ucs);
                                    textView.setTypeface(textFont);
                                }

                                if(a.contains("<details>")){
                                    Pattern pattern = Pattern.compile( "<summary>(.*?)</summary>" );
                                    Matcher m = pattern.matcher(a);
                                    if(m.find()) {
                                        textView.setText(m.group(1));
                                        textView.setId(R.id.textPrayerDetailsId);
                                        textView.setTextColor(Color.DKGRAY);

                                        String s = a.split(m.group(1))[1];
                                        TextView other = new TextView(getContext());
                                        other.setText(HtmlCompat.fromHtml(s, HtmlCompat.FROM_HTML_MODE_COMPACT));
                                        other.setId(R.id.textPrayerDetailsOtherId);
                                        other.setTextSize(convertToPx());
                                        other.setTextColor(Color.BLACK);
                                        other.setVisibility(View.GONE);
                                        binding.textPrayer.addView(textView);
                                        binding.textPrayer.addView(other);

                                        textView.setOnClickListener(view -> {
                                            if(other.getVisibility() == View.VISIBLE){
                                                other.setVisibility(View.GONE);
                                            }else {
                                                other.setVisibility(View.VISIBLE);
                                            }
                                        });
                                    }
                                }else {
                                    textView.setTextColor(Color.BLACK);
                                    textView.setText(HtmlCompat.fromHtml(a, HtmlCompat.FROM_HTML_MODE_COMPACT));
                                    binding.textPrayer.addView(textView);
                                }
                            });

                            prayerViewModel.setIsRenderedTextArr(true);
                        });



                //something that didn't work out very well
                binding.prayerOptions.getMenu().getItem(0).setChecked(false);

                prayerViewModel.getPrayersModelsMutableLiveData().observe(getViewLifecycleOwner(), prayersModels -> {
                    //bottom menu
                    binding.prayerOptions.setOnItemSelectedListener(item -> {
                        if (item.getItemId() == R.id.zoom_out){
                            textSize--;
                            saveTextSize(textSize);
                            for (int i = 0; i < binding.textPrayer.getChildCount(); i++) {
                                View view = binding.textPrayer.getChildAt(i);
                                if(view instanceof TextView){
                                    ((TextView) view).setTextSize(convertToPx());
                                }
                            }
                            return true;
                        } else if (item.getItemId() == R.id.to_menu) {
                            Bundle bundle1 = new Bundle();
                            if (prevMenu != null){
                                bundle1.putString("date", prevMenu);
                                FragmentKt.findNavController(getParentFragment()).navigate(R.id.action_prayerFragment_to_churchFragment, bundle1);
                            }else {
                                bundle1.putInt("id", getArguments().getInt("blockId"));
                                FragmentKt.findNavController(getParentFragment()).navigate(R.id.action_prayerFragment_to_psaltirFragment, bundle1);
                            }
                            return true;
                        } else if (item.getItemId() == R.id.zoom_in) {
                            textSize++;
                            saveTextSize(textSize);
                            for (int i = 0; i < binding.textPrayer.getChildCount(); i++) {
                                View view = binding.textPrayer.getChildAt(i);
                                if(view instanceof TextView){
                                    ((TextView) view).setTextSize(convertToPx());
                                }
                            }
                            return true;
                        } else if (item.getItemId() == R.id.next_prayer) {
                            if(prayersModels.getNext() == 0){
                                Bundle bundle3 = new Bundle();
                                if (prevMenu != null) {
                                    bundle3.putString("date", prevMenu);
                                    FragmentKt.findNavController(getParentFragment()).navigate(R.id.action_prayerFragment_to_churchFragment, bundle3);
                                } else {
                                    bundle3.putInt("id", getArguments().getInt("blockId"));
                                    FragmentKt.findNavController(getParentFragment()).navigate(R.id.action_prayerFragment_to_psaltirFragment, bundle3);
                                }
                                return true;
                            } else {
                                Bundle bundle4 = new Bundle();
                                if (prevMenu != null) {
                                    bundle4.putString("prevMenu", prevMenu);
                                    bundle4.putInt("prayerId", prayersModels.getNext());
                                    bundle4.putString("mode", mode);
                                    FragmentKt.findNavController(getParentFragment()).navigate(R.id.action_prayerFragment_self, bundle4);
                                } else {
                                    bundle4.putInt("blockId", getArguments().getInt("blockId"));
                                    bundle4.putInt("prayerId", prayersModels.getNext());
                                    bundle4.putString("mode", "psaltir_read");
                                    FragmentKt.findNavController(getParentFragment()).navigate(R.id.action_prayerFragment_self, bundle4);
                                }
                                return true;
                            }
                        } else if (item.getItemId() == R.id.prev_prayer) {
                            if(prayersModels.getPrev() == 0){
                                Bundle bundle5 = new Bundle();
                                if (prevMenu != null) {
                                    bundle5.putString("date", prevMenu);
                                    FragmentKt.findNavController(getParentFragment()).navigate(R.id.action_prayerFragment_to_churchFragment, bundle5);
                                }else {
                                    bundle5.putInt("id", getArguments().getInt("blockId"));
                                    FragmentKt.findNavController(getParentFragment()).navigate(R.id.action_prayerFragment_to_psaltirFragment, bundle5);
                                }
                                return true;
                            }else {
                                Bundle bundle2 = new Bundle();
                                if (prevMenu != null) {
                                    bundle2.putString("prevMenu", prevMenu);
                                    bundle2.putInt("prayerId", prayersModels.getPrev());
                                    bundle2.putString("mode", mode);
                                    FragmentKt.findNavController(getParentFragment()).navigate(R.id.action_prayerFragment_self, bundle2);
                                } else {
                                    bundle2.putInt("blockId", getArguments().getInt("blockId"));
                                    bundle2.putInt("prayerId", prayersModels.getPrev());
                                    bundle2.putString("mode", "psaltir_read");
                                    FragmentKt.findNavController(getParentFragment()).navigate(R.id.action_prayerFragment_self, bundle2);
                                }
                                return true;
                            }
                        }
                        return false;
                    });
                });
            }
        });

        return binding.getRoot();
    }

    @Override
    public void onPause() {
        super.onPause();
        saveData();
    }

    @Override
    public void onResume() {
        super.onResume();
        int scrollPosition;
        if(Objects.equals(mode,"prayer_read")) {
            scrollPosition = appPref.getInt("app_pref_prayer_scroll_pos_" + prevMenu + "/" + prayerId, 0);
            Log.d(TAG, "saved data: app_pref_prayer_scroll_pos_   "+scrollPosition);
        } else if (Objects.equals(mode,"psaltir_read")) {
            scrollPosition = appPref.getInt("app_pref_psaltir_scroll_pos_" + blockId + "/" + prayerId, 0);
            Log.d(TAG, "saved data: app_pref_psaltir_scroll_pos_   "+scrollPosition);
        } else if (Objects.equals(mode, "prayer_rule")) {
            scrollPosition = appPref.getInt("app_pref_prayerrule_scroll_pos_" + prevMenu + "/" + prayerId, 0);
            Log.d(TAG, "saved data: app_pref_prayerrule_scroll_pos_   "+scrollPosition);
        } else {
            scrollPosition = 0;
        }

        prayerViewModel.getIsRenderedTextArr().observe(getViewLifecycleOwner(), aBoolean -> {
            if(aBoolean){
                binding.scrollPrayer.post(() -> binding.scrollPrayer.scrollTo(0, scrollPosition));
            }
        });
    }

    @Override
    public void saveData() {
        SharedPreferences.Editor editor = appPref.edit();
        int windowHeight = binding.textPrayer.getHeight() - binding.scrollPrayer.getScrollY();
        int scrollPosition = windowHeight > binding.scrollPrayer.getHeight() ? binding.scrollPrayer.getScrollY() : 0;

        if(Objects.equals(mode,"prayer_read")) {
            editor.putInt("app_pref_prayer_scroll_pos_" + prevMenu + "/" + prayerId, scrollPosition);
            editor.putLong("app_pref_prayer_scroll_pos_" + prevMenu + "/" + prayerId+"_timestamp", System.currentTimeMillis());
        } else if (Objects.equals(mode,"psaltir_read")) {
            editor.putInt("app_pref_psaltir_scroll_pos_" + blockId + "/" + prayerId, scrollPosition);
            editor.putLong("app_pref_psaltir_scroll_pos_" + blockId + "/" + prayerId+"_timestamp", System.currentTimeMillis());
        } else if (Objects.equals(mode, "prayer_rule")) {
            editor.putInt("app_pref_prayerrule_scroll_pos_" + prevMenu + "/" + prayerId, scrollPosition);
            editor.putLong("app_pref_prayerrule_scroll_pos_" + prevMenu + "/" + prayerId+"_timestamp", System.currentTimeMillis());
        }
        boolean success = editor.commit();
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