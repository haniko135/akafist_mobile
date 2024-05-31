package net.energogroup.akafist.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.energogroup.akafist.databinding.FragmentPsaltirBinding;
import net.energogroup.akafist.recyclers.PsaltirRecyclerAdapter;
import net.energogroup.akafist.viewmodel.PsaltirViewModel;

import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PsaltirFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PsaltirFragment extends Fragment {

    public static final String TAG = "PSALTIR_FRAGMENT";
    private PsaltirViewModel viewModel;
    private int id;
    public FragmentPsaltirBinding psaltirBinding;

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
        if (getArguments() != null) {
            id = getArguments().getInt("id");
        }
        viewModel = new ViewModelProvider(this).get(PsaltirViewModel.class);
        if(getActivity() != null) {
            ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Псалтирь");
            viewModel.getJson(getContext(), id);
        }
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
            Log.e(TAG, String.valueOf(psaltirModel.getDesc() != null));
            if(Objects.equals(psaltirModel.getDesc(), null)){
                psaltirBinding.psaltirDesc.setVisibility(View.VISIBLE);
                psaltirBinding.psaltirDesc.setText(psaltirModel.getDesc());
            }else {
                psaltirBinding.psaltirDesc.setVisibility(View.GONE);
            }

            if (!psaltirModel.getPsaltirKafismas().isEmpty()){
                psaltirBinding.psaltirRV.setLayoutManager(new LinearLayoutManager(getContext()));
                psaltirBinding.psaltirRV.setAdapter(new PsaltirRecyclerAdapter(psaltirModel.getPsaltirKafismas(), fr, id));
            }
        });

        return psaltirBinding.getRoot();
    }
}