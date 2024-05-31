package net.energogroup.akafist.fragments.lists;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.energogroup.akafist.databinding.FragmentAudioDragAndDropBinding;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AudioDragAndDropFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AudioDragAndDropFragment extends Fragment {
    private FragmentAudioDragAndDropBinding binding;

    // Required empty public constructor
    public AudioDragAndDropFragment() { }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment AudioDragAndDropFragment.
     */
    public static AudioDragAndDropFragment newInstance() {
        return new AudioDragAndDropFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentAudioDragAndDropBinding.inflate(getLayoutInflater(), container, false);

        return binding.getRoot();
    }
}