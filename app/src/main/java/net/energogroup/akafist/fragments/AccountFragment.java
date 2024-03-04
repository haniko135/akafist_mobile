package net.energogroup.akafist.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.FragmentKt;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.energogroup.akafist.MainActivity;
import net.energogroup.akafist.R;
import net.energogroup.akafist.databinding.FragmentAccountBinding;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AccountFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AccountFragment extends Fragment {

    private FragmentAccountBinding accountBinding;

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
        accountBinding.userName.setText(userName);

        accountBinding.starred.setOnClickListener(view ->
            FragmentKt.findNavController(this).navigate(R.id.action_accountFragment_to_starredFragment)
        );

        return accountBinding.getRoot();
    }
}