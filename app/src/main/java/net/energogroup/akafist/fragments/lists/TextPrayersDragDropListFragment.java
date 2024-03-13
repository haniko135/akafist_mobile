package net.energogroup.akafist.fragments.lists;

import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
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

import com.google.android.material.snackbar.Snackbar;

import net.energogroup.akafist.MainActivity;
import net.energogroup.akafist.R;
import net.energogroup.akafist.databinding.FragmentTextPrayersDragDropListBinding;
import net.energogroup.akafist.models.ServicesModel;
import net.energogroup.akafist.recyclers.DragAndDropAdapter;
import net.energogroup.akafist.recyclers.ServicesRecyclerAdapter;
import net.energogroup.akafist.viewmodel.StarredViewModel;

import java.io.Console;
import java.util.List;

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

    //true = список избранных уже есть в бд как PrayersModel, false = список избранных ещё как StarredModel
    private boolean mode;

    public StarredViewModel getViewModel() {
        return viewModel;
    }

    public boolean isMode() { return mode; }

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
        mode = viewModel.getPrayerRuleArray(db);
        if(!mode) {
            viewModel.getStarred(db);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        dragDropListBinding = FragmentTextPrayersDragDropListBinding.inflate(getLayoutInflater(), container, false);

        if(!mode) {
            viewModel.getTextPrayers().observe(getViewLifecycleOwner(), prayers -> {
                dropAdapter = new DragAndDropAdapter(this, prayers, this, db);

                ItemTouchHelper.Callback callback = new ItemMoveCallback(dropAdapter);
                touchHelper = new ItemTouchHelper(callback);

                touchHelper.attachToRecyclerView(dragDropListBinding.textPrayersDragAndDropList);

                dragDropListBinding.textPrayersDragAndDropList.setLayoutManager(new LinearLayoutManager(getContext()));
                dragDropListBinding.textPrayersDragAndDropList.setAdapter(dropAdapter);
            });
        }else {
            viewModel.getPrayerRules().observe(getViewLifecycleOwner(), prayerRule->{
                dropAdapter = new DragAndDropAdapter(this, prayerRule, this, db);

                ItemSwipeCallback itemMoveCallback = new ItemSwipeCallback(getContext()){
                    @Override
                    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                        super.onSwiped(viewHolder, direction);
                        final int position = viewHolder.getAdapterPosition();
                        final ServicesModel item = dropAdapter.getTextPrayers().get(position);

                        dropAdapter.removeItem(position);


                        Snackbar snackbar = Snackbar
                                .make(dragDropListBinding.getRoot(), "Молитва удалена. ", Snackbar.LENGTH_LONG);
                        snackbar.setAction("ОТМЕНИТЬ", view -> {
                            dropAdapter.restoreItem(item, position);
                            //recyclerView.scrollToPosition(position);
                        });

                        snackbar.setActionTextColor(Color.YELLOW);
                        snackbar.show();
                    }
                };

                ItemTouchHelper touchHelper2 = new ItemTouchHelper(itemMoveCallback);

                ItemTouchHelper.Callback callback = new ItemMoveCallback(dropAdapter);
                touchHelper = new ItemTouchHelper(callback);

                touchHelper.attachToRecyclerView(dragDropListBinding.textPrayersDragAndDropList);
                touchHelper2.attachToRecyclerView(dragDropListBinding.textPrayersDragAndDropList);

                dragDropListBinding.textPrayersDragAndDropList.setLayoutManager(new LinearLayoutManager(getContext()));
                dragDropListBinding.textPrayersDragAndDropList.setAdapter(dropAdapter);
            });
        }

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