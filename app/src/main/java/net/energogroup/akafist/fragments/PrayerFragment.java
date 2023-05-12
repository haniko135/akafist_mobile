package net.energogroup.akafist.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.FragmentKt;

import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.energogroup.akafist.MainActivity;
import net.energogroup.akafist.R;

import net.energogroup.akafist.databinding.FragmentPrayerBinding;
import net.energogroup.akafist.viewmodel.PrayerViewModel;

/**
 * Класс фрагмента молитв
 * @author Nastya
 * @version 1.0.0
 */
public class PrayerFragment extends Fragment {

    private float textSize;
    private String prevMenu;
    private int prayerId;
    private PrayerViewModel prayerViewModel;
    private SharedPreferences appPref;
    FragmentPrayerBinding binding;

    /**
     * Обязательный конструктор класса
     */
    public PrayerFragment() { }

    /**
     * Этот метод отвечает за создание класса фрагмента молитв
     * @return PrayerFragment
     */
    public static PrayerFragment newInstance() {
        return new PrayerFragment();
    }

    /**
     * Этот метод подготавливает активность к работе фрагмента
     * @param savedInstanceState Bundle
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appPref = requireActivity().getSharedPreferences(MainActivity.APP_PREFERENCES, Context.MODE_PRIVATE);
        if(getArguments() != null){
            prevMenu = getArguments().getString("prevMenu");
            prayerId = getArguments().getInt("prayerId");
        }
        ViewModelProvider provider = new ViewModelProvider(this);
        prayerViewModel = provider.get(PrayerViewModel.class);
        prayerViewModel.getJson(prevMenu, prayerId);
    }

    /**
     * Этот метод определяет поля класса фрагмента, после завершения его создания
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
     * Этот метод создаёт фрагмент с учетом определённых
     * в {@link PrayerFragment#onCreate(Bundle)} полей
     * @param inflater LayoutInflater
     * @param container ViewGroup
     * @param savedInstanceState Bundle
     * @return View
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if(getArguments() != null){
            prevMenu = getArguments().getString("prevMenu");
            prayerId = getArguments().getInt("largeText");
            /*if(getArguments().get("textSize")!=null){
                textSize = getArguments().getFloat("textSize");
            } else{
                textSize = getResources().getDimension(R.dimen.text_prayer);
            }*/
            if(appPref.contains("app_pref_text_size")){
                textSize = appPref.getFloat("app_pref_text_size", getResources().getDimension(R.dimen.text_prayer));
            } else{
                textSize = getResources().getDimension(R.dimen.text_prayer);
            }
        }

        //название молитвы в ToolBar
        prayerViewModel.getPrayersModelsMutableLiveData().observe(getViewLifecycleOwner(), prayersModels -> ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(prayersModels.getNamePrayer()));

        //Первоначальные настройки фрагмента
        binding = FragmentPrayerBinding.inflate(getLayoutInflater());
        binding.textPrayer.setTextSize(convertToPx());
        binding.textPrayer.setMovementMethod(new ScrollingMovementMethod());
        prayerViewModel.getPrayersModelsMutableLiveData().observe(getViewLifecycleOwner(), prayersModels -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                binding.textPrayer.setText(Html.fromHtml(prayersModels.getTextPrayer(), Html.FROM_HTML_MODE_COMPACT));
            } else{
                binding.textPrayer.setText(Html.fromHtml(prayersModels.getTextPrayer()));
            }
        });

        //то, что не совсем хорошо сработало
        binding.prayerOptions.getMenu().getItem(0).setChecked(false);

        //нижнее меню
        binding.prayerOptions.setOnItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.zoom_out:
                    textSize--;
                    saveTextSize(textSize);
                    binding.textPrayer.setTextSize(convertToPx());
                    Log.i("PRAYER", Float.toString(textSize));
                    return true;
                case R.id.to_menu:
                    Bundle bundle1 = new Bundle();
                    bundle1.putString("date",prevMenu);
                    FragmentKt.findNavController(getParentFragment()).navigate(R.id.action_prayerFragment_to_churchFragment, bundle1);
                    return true;
                case R.id.zoom_in:
                    textSize++;
                    saveTextSize(textSize);
                    binding.textPrayer.setTextSize(convertToPx());
                    Log.i("PRAYER", Float.toString(textSize));
                    return true;
                case R.id.next_prayer:
                    if(prayerViewModel.getPrayersModel().getNext() == 0){
                        Bundle bundle3 = new Bundle();
                        bundle3.putString("date",prevMenu);
                        FragmentKt.findNavController(getParentFragment()).navigate(R.id.action_prayerFragment_to_churchFragment, bundle3);
                        return true;
                    } else {
                        Bundle bundle4 = new Bundle();
                        bundle4.putString("prevMenu", prevMenu);
                        bundle4.putInt("prayerId", prayerViewModel.getPrayersModel().getNext());
                        FragmentKt.findNavController(getParentFragment()).navigate(R.id.action_prayerFragment_self, bundle4);
                        return true;
                    }
                case R.id.prev_prayer:
                    if(prayerViewModel.getPrayersModel().getPrev() == 0){
                        Bundle bundle5 = new Bundle();
                        bundle5.putString("date",prevMenu);
                        FragmentKt.findNavController(getParentFragment()).navigate(R.id.action_prayerFragment_to_churchFragment, bundle5);
                        return true;
                    }else {
                        Bundle bundle2 = new Bundle();
                        bundle2.putString("prevMenu", prevMenu);
                        bundle2.putInt("prayerId", prayerViewModel.getPrayersModel().getPrev());
                        FragmentKt.findNavController(getParentFragment()).navigate(R.id.action_prayerFragment_self, bundle2);
                        return true;
                    }
            }
            return false;
        });

        return binding.getRoot();
    }

    /**
     * Этот метод конвертирует размер в пиксели
     * @return float
     */
    private float convertToPx(){
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, textSize, getContext().getResources().getDisplayMetrics());
    }

    public void saveTextSize(float textSize){
        SharedPreferences.Editor editor = appPref.edit();
        editor.putFloat("app_pref_text_size", textSize);
        editor.apply();
    }
}