package net.energogroup.akafist.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.FragmentKt;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import net.energogroup.akafist.MainActivity;
import net.energogroup.akafist.R;
import net.energogroup.akafist.databinding.FragmentAccountBinding;

import java.util.Calendar;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AccountFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AccountFragment extends Fragment {

    private FragmentAccountBinding accountBinding;
    private EditText accountBirthday;
    private EditText accountNameday;
    private TextWatcher textWatcherBirthday = new TextWatcher() {
        private String current = "";
        private String ddmmyyyy = "ДДММГГГГ";
        private Calendar cal = Calendar.getInstance();

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
                    //This part makes sure that when we finish entering numbers
                    //the date is correct, fixing it otherwise
                    int day  = Integer.parseInt(clean.substring(0,2));
                    int mon  = Integer.parseInt(clean.substring(2,4));
                    int year = Integer.parseInt(clean.substring(4,8));

                    mon = mon < 1 ? 1 : mon > 12 ? 12 : mon;
                    cal.set(Calendar.MONTH, mon-1);
                    year = (year<1900)?1900:(year>2100)?2100:year;
                    cal.set(Calendar.YEAR, year);
                    // ^ first set year for the line below to work correctly
                    //with leap years - otherwise, date e.g. 29/02/2012
                    //would be automatically corrected to 28/02/2012

                    day = (day > cal.getActualMaximum(Calendar.DATE))? cal.getActualMaximum(Calendar.DATE):day;
                    clean = String.format("%02d%02d%02d",day, mon, year);
                }

                clean = String.format("%s.%s.%s", clean.substring(0, 2),
                        clean.substring(2, 4),
                        clean.substring(4, 8));

                sel = sel < 0 ? 0 : sel;
                current = clean;
                accountBirthday.setText(current);
                accountBirthday.setSelection(sel < current.length() ? sel : current.length());
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    private TextWatcher textWatcherNameday = new TextWatcher() {
        private String current = "";
        private String ddmmyyyy = "ДДММ";
        private Calendar cal = Calendar.getInstance();

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

                    mon = mon < 1 ? 1 : mon > 12 ? 12 : mon;
                    cal.set(Calendar.MONTH, mon-1);

                    day = (day > cal.getActualMaximum(Calendar.DATE))? cal.getActualMaximum(Calendar.DATE):day;
                    clean = String.format("%02d%02d",day, mon);
                }

                clean = String.format("%s.%s", clean.substring(0, 2),
                        clean.substring(2, 4));

                sel = sel < 0 ? 0 : sel;
                current = clean;
                accountNameday.setText(current);
                accountNameday.setSelection(sel < current.length() ? sel : current.length());
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
        if((AppCompatActivity)getActivity() != null) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Профиль");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
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

        accountBinding.starred.setOnClickListener(view ->
            FragmentKt.findNavController(this).navigate(R.id.action_accountFragment_to_starredFragment)
        );
        accountBinding.molitvaPravilo.setOnClickListener(view ->
            FragmentKt.findNavController(this).navigate(R.id.action_accountFragment_to_prayerRuleFragment)
        );
        accountBinding.calendarAccount.setOnClickListener(view ->
            FragmentKt.findNavController(this).navigate(R.id.action_accountFragment_to_accountCalendarFragment)
        );

        return accountBinding.getRoot();
    }
}