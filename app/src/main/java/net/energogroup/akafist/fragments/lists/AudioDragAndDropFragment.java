package net.energogroup.akafist.fragments.lists;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.energogroup.akafist.R;
import net.energogroup.akafist.databinding.FragmentAudioDragAndDropBinding;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AudioDragAndDropFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AudioDragAndDropFragment extends Fragment {
    private FragmentAudioDragAndDropBinding binding;

    public AudioDragAndDropFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment AudioDragAndDropFragment.
     */
    public static AudioDragAndDropFragment newInstance() {
        AudioDragAndDropFragment fragment = new AudioDragAndDropFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentAudioDragAndDropBinding.inflate(getLayoutInflater(), container, false);

        return binding.getRoot();
    }
}