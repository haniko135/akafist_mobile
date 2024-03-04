package net.energogroup.akafist.fragments.lists;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.energogroup.akafist.MainActivity;
import net.energogroup.akafist.databinding.FragmentStarredListBinding;
import net.energogroup.akafist.db.StarredDTO;
import net.energogroup.akafist.models.LinksModel;
import net.energogroup.akafist.models.StarredModel;
import net.energogroup.akafist.recyclers.AudioRecyclerAdapter;
import net.energogroup.akafist.recyclers.StarredRecyclerAdapter;
import net.energogroup.akafist.viewmodel.LinksViewModel;
import net.energogroup.akafist.viewmodel.StarredViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link StarredListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StarredListFragment extends Fragment {

    private FragmentStarredListBinding starredListBinding;
    private StarredViewModel starredViewModel;
    private MainActivity mainActivity;
    private SQLiteDatabase db;
    private StarredRecyclerAdapter starredRecyclerAdapter;
    public StarredListFragment() { }

    public static StarredListFragment newInstance() {
        return new StarredListFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = (MainActivity) getActivity();
        db = mainActivity.getDbHelper().getReadableDatabase();
        starredViewModel = new ViewModelProvider(this).get(StarredViewModel.class);
        starredViewModel.getStarred(db);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        starredListBinding = FragmentStarredListBinding.inflate(getLayoutInflater(), container, false);
        starredListBinding.starredTextListRv.setLayoutManager(new LinearLayoutManager(getContext()));

        starredViewModel.getTextPrayers().observe(getViewLifecycleOwner(), servicesModel -> {
            starredRecyclerAdapter = new StarredRecyclerAdapter(mainActivity, this, servicesModel);
            starredListBinding.starredTextListRv.setAdapter(starredRecyclerAdapter);
        });

        return starredListBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        db.close();
    }
}