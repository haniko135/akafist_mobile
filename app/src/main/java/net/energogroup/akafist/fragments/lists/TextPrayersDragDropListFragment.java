package net.energogroup.akafist.fragments.lists;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.energogroup.akafist.MainActivity;
import net.energogroup.akafist.R;
import net.energogroup.akafist.databinding.FragmentTextPrayersDragDropListBinding;
import net.energogroup.akafist.recyclers.DragAndDropAdapter;
import net.energogroup.akafist.viewmodel.StarredViewModel;

import java.io.Console;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TextPrayersDragDropListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TextPrayersDragDropListFragment extends Fragment implements StartDragListner  {

    private FragmentTextPrayersDragDropListBinding dragDropListBinding;
    private DragAndDropAdapter dropAdapter;
    private ItemTouchHelper touchHelper;
    private StarredViewModel viewModel;
    private SQLiteDatabase db;

    public TextPrayersDragDropListFragment() { }

    public static TextPrayersDragDropListFragment newInstance() {
        return new TextPrayersDragDropListFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainActivity mainActivity = (MainActivity) getActivity();
        db = mainActivity.getDbHelper().getReadableDatabase();
        viewModel = new ViewModelProvider(this).get(StarredViewModel.class);
        viewModel.getStarred(db);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        dragDropListBinding = FragmentTextPrayersDragDropListBinding.inflate(getLayoutInflater(), container, false);

        viewModel.getTextPrayers().observe(getViewLifecycleOwner(), prayers ->{
            prayers.forEach(item->Log.e("DRAG-AND-DROP", item.getName()));
            dropAdapter = new DragAndDropAdapter(this,prayers, this);

            ItemTouchHelper.Callback callback = new ItemMoveCallback(dropAdapter);
            touchHelper  = new ItemTouchHelper(callback);
            touchHelper.attachToRecyclerView(dragDropListBinding.textPrayersDragAndDropList);

            dragDropListBinding.textPrayersDragAndDropList.setLayoutManager(new LinearLayoutManager(getContext()));
            dragDropListBinding.textPrayersDragAndDropList.setAdapter(dropAdapter);
        });

        return dragDropListBinding.getRoot();
    }

    @Override
    public void requestDrag(RecyclerView.ViewHolder viewHolder) {
        touchHelper.startDrag(viewHolder);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        db.close();
    }
}