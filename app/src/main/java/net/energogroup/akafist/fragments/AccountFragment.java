package net.energogroup.akafist.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import net.energogroup.akafist.MainActivity;
import net.energogroup.akafist.R;
import net.energogroup.akafist.databinding.FragmentAccountBinding;
import net.energogroup.akafist.recyclers.SettingsRecyclerAdapter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AccountFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AccountFragment extends Fragment {

    private FragmentAccountBinding accountBinding;
    private final List<String> pagesList = new ArrayList<>();
    private final List<Object> pagesLinks = new ArrayList<>();
    private final SettingsRecyclerAdapter settingsRecyclerAdapter = new SettingsRecyclerAdapter();

    private EditText accountBirthday;
    private EditText accountNameday;
    private final TextWatcher textWatcherBirthday = new TextWatcher() {
        private String current = "";
        private final String ddmmyyyy = "ДДММГГГГ";
        private final Calendar cal = Calendar.getInstance();

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (!s.toString().equals(current)) {
                String clean = s.toString().replaceAll("[^\\d.]|\\.", "");
                String cleanC = current.replaceAll("[^\\d.]|\\.", "");

                int cl = clean.length();
                int sel = cl;
                for (int i = 2; i <= cl && i < 6; i += 2) {
                    sel++;
                }
                if (clean.equals(cleanC)) sel--;

                if (clean.length() < 8){
                    clean = clean + ddmmyyyy.substring(clean.length());
                }else{
                    int day  = Integer.parseInt(clean.substring(0,2));
                    int mon  = Integer.parseInt(clean.substring(2,4));
                    int year = Integer.parseInt(clean.substring(4,8));

                    mon = mon < 1 ? 1 : Math.min(mon, 12);
                    cal.set(Calendar.MONTH, mon-1);
                    year = (year<1900)?1900: Math.min(year, 2100);
                    cal.set(Calendar.YEAR, year);

                    day = Math.min(day, cal.getActualMaximum(Calendar.DATE));
                    clean = String.format(Locale.getDefault(),"%02d%02d%02d",day, mon, year);
                }

                clean = String.format("%s.%s.%s", clean.substring(0, 2),
                        clean.substring(2, 4),
                        clean.substring(4, 8));

                sel = Math.max(sel, 0);
                current = clean;
                accountBirthday.setText(current);
                accountBirthday.setSelection(Math.min(sel, current.length()));
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    private final TextWatcher textWatcherNameday = new TextWatcher() {
        private String current = "";
        private final String ddmmyyyy = "ДДММ";
        private final Calendar cal = Calendar.getInstance();

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (!s.toString().equals(current)) {
                String clean = s.toString().replaceAll("[^\\d.]|\\.", "");
                String cleanC = current.replaceAll("[^\\d.]|\\.", "");

                int cl = clean.length();
                int sel = cl;
                for (int i = 2; i <= cl && i < 6; i += 2) {
                    sel++;
                }
                //Fix for pressing delete next to a forward slash
                if (clean.equals(cleanC)) sel--;

                if (clean.length() < 8){
                    clean = clean + ddmmyyyy.substring(clean.length());
                }else{
                    int day  = Integer.parseInt(clean.substring(0,2));
                    int mon  = Integer.parseInt(clean.substring(2,4));

                    mon = mon < 1 ? 1 : Math.min(mon, 12);
                    cal.set(Calendar.MONTH, mon-1);

                    day = Math.min(day, cal.getActualMaximum(Calendar.DATE));
                    clean = String.format(Locale.getDefault(),"%02d%02d",day, mon);
                }

                clean = String.format("%s.%s", clean.substring(0, 2),
                        clean.substring(2, 4));

                sel = Math.max(sel, 0);
                current = clean;
                accountNameday.setText(current);
                accountNameday.setSelection(Math.min(sel, current.length()));
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    /**
     * Required empty public constructor
     */
    public AccountFragment() { }

    public static AccountFragment newInstance() {
        return new AccountFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getActivity() != null) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Профиль");
        }
        pagesList.add("Избранное");
        pagesLinks.add(R.id.action_accountFragment_to_starredFragment);
        pagesList.add("Молитвенное правило");
        pagesLinks.add(R.id.action_accountFragment_to_prayerRuleFragment);
        pagesList.add("Мой календарь");
        pagesLinks.add(R.id.action_accountFragment_to_accountCalendarFragment);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        accountBinding = FragmentAccountBinding.inflate(inflater, container, false);

        SharedPreferences appPref = getActivity().getSharedPreferences(MainActivity.APP_PREFERENCES, Context.MODE_PRIVATE);
        String userName = appPref.getString("app_pref_username", "guest");
        accountBinding.userName.setText(userName.replaceAll(" ", "\n"));

        accountBirthday = accountBinding.accountBirthday;
        accountBirthday.addTextChangedListener(textWatcherBirthday);
        accountBinding.accountEditBirthday.setOnClickListener(view -> {
            if(!accountBirthday.getText().toString().isEmpty()){
                SharedPreferences.Editor editor = appPref.edit();
                editor.putString("app_pref_birthday", accountBirthday.getText().toString());
                editor.apply();
            }
        });

        accountNameday = accountBinding.accountNameday;
        accountNameday.addTextChangedListener(textWatcherNameday);
        accountBinding.accountEditNameday.setOnClickListener(view -> {
            if(!accountNameday.getText().toString().isEmpty()){
                SharedPreferences.Editor editor = appPref.edit();
                editor.putString("app_pref_nameday", accountNameday.getText().toString());
                editor.apply();
            }
        });

        if(appPref.contains("app_pref_nameday")){
            accountNameday.setHint(appPref.getString("app_pref_nameday", "01.01"));
        }
        if(appPref.contains("app_pref_birthday")){
            accountBirthday.setHint(appPref.getString("app_pref_birthday", "01.01.2000"));
        }

        /*accountBinding.starred.setOnClickListener(view ->
            FragmentKt.findNavController(this).navigate()
        );
        accountBinding.molitvaPravilo.setOnClickListener(view ->
            FragmentKt.findNavController(this).navigate()
        );
        accountBinding.calendarAccount.setOnClickListener(view ->
            FragmentKt.findNavController(this).navigate()
        );*/

        accountBinding.accountRV.setLayoutManager(new LinearLayoutManager(getContext()));
        settingsRecyclerAdapter.setData(pagesList, pagesLinks);
        settingsRecyclerAdapter.setFragment(this);
        accountBinding.accountRV.setAdapter(settingsRecyclerAdapter);

        return accountBinding.getRoot();
    }
}