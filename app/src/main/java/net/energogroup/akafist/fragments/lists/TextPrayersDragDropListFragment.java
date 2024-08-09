package net.energogroup.akafist.fragments.lists;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MenuHost;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.FragmentKt;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.snackbar.Snackbar;

import net.energogroup.akafist.AkafistApplication;
import net.energogroup.akafist.MainActivity;
import net.energogroup.akafist.R;
import net.energogroup.akafist.api.PrAPI;
import net.energogroup.akafist.databinding.FragmentTextPrayersDragDropListBinding;
import net.energogroup.akafist.models.ServicesModel;
import net.energogroup.akafist.recyclers.DragAndDropAdapter;
import net.energogroup.akafist.viewmodel.StarredViewModel;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TextPrayersDragDropListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TextPrayersDragDropListFragment extends Fragment implements StartDragListner  {

    private static final String TAG = "TEXT_PRAYERS_DRAG_DROP_LIST_FRAGMENT";
    public static final String LAST_PRAYER_RULE_PREF_NAME = "app_pref_prayerrule_scroll_pos_";
    private PrAPI prAPI;
    private FragmentTextPrayersDragDropListBinding dragDropListBinding;
    private DragAndDropAdapter dropAdapter;
    private ItemTouchHelper touchHelper;
    private StarredViewModel viewModel;
    private SQLiteDatabase db;
    private String lastViewedPrayerRuleKey;
    private SharedPreferences appPref;
    private List<ServicesModel> textPrayers;




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
        appPref = requireActivity().getSharedPreferences(MainActivity.APP_PREFERENCES, Context.MODE_PRIVATE);
        lastViewedPrayerRuleKey = getLastViewedPrayerInRule();

        MainActivity mainActivity = (MainActivity) getActivity();
        db = mainActivity.getDbHelper().getReadableDatabase();
        viewModel = new ViewModelProvider(this).get(StarredViewModel.class);
        mode = viewModel.getPrayerRuleArray(db);
        prAPI = ((AkafistApplication)getActivity().getApplication()).prAPI;
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
                textPrayers = prayers;
                dropAdapter = new DragAndDropAdapter(this, prayers, this, db, prAPI);

                ItemTouchHelper.Callback callback = new ItemMoveCallback(dropAdapter);
                touchHelper = new ItemTouchHelper(callback);

                touchHelper.attachToRecyclerView(dragDropListBinding.textPrayersDragAndDropList);

                dragDropListBinding.textPrayersDragAndDropList.setLayoutManager(new LinearLayoutManager(getContext()));
                dragDropListBinding.textPrayersDragAndDropList.setAdapter(dropAdapter);
            });
        }else {
            viewModel.getPrayerRules().observe(getViewLifecycleOwner(), prayerRule->{
                textPrayers = prayerRule;
                dropAdapter = new DragAndDropAdapter(this, prayerRule, this, db, prAPI);

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
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if(lastViewedPrayerRuleKey != null) {
            Log.w(TAG, lastViewedPrayerRuleKey);
            MenuHost menuHost = requireActivity();
            menuHost.addMenuProvider(new MenuProvider() {
                @Override
                public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                    if (menu.findItem(R.id.menuContinueRead) != null)
                        menu.removeItem(R.id.menuContinueRead);
                    if (menu.findItem(R.id.menuContinueMyRead) == null){
                        MenuItem menuItem = menu.add(Menu.NONE, R.id.menuContinueMyRead, Menu.NONE, "Продолжить чтение");
                        menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
                    }
                }

                @Override
                public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                    if (menuItem.getItemId() == R.id.menuContinueMyRead) {
                        continueRead();
                        return true;
                    }
                    return false;
                }
            }, getViewLifecycleOwner(), Lifecycle.State.RESUMED);
        }
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

    public String getLastViewedPrayerInRule(){
        Map<String, ?> allEntries = appPref.getAll();

        String lastSavedKey = null;
        long lastTimestamp = 0;

        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            String key = entry.getKey();
            if (key.startsWith(LAST_PRAYER_RULE_PREF_NAME) && key.endsWith("_timestamp")) {
                long timestamp = (Long) entry.getValue();
                if (timestamp > lastTimestamp) {
                    lastTimestamp = timestamp;
                    lastSavedKey = key.substring(0, key.length() - "_timestamp".length());
                }
            }
        }

        return lastSavedKey;
    }


    private void continueRead(){
        String dataForBundle = lastViewedPrayerRuleKey.substring(LAST_PRAYER_RULE_PREF_NAME.length());
        String prevMenuData = dataForBundle.split("/")[0];
        String prayerIdData = dataForBundle.split("/")[1];

        if(mode){

            viewModel.convertToPrayersModels(getContext(), db, prAPI)
                    .observe(getViewLifecycleOwner(), prayersModels -> {
                    if(prayersModels.size() == viewModel.getPrayerRules().getValue().size()) {
                        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_fragment);
                        NavDestination currentDestination = navController.getCurrentDestination();

                        if (currentDestination != null && currentDestination.getId() != R.id.prayerFragment) {
                            Bundle bundle = new Bundle();
                            bundle.putString("prevMenu", prevMenuData);
                            bundle.putInt("prayerId", Integer.parseInt(prayerIdData));
                            bundle.putString("mode", "prayer_rule");
                            FragmentKt.findNavController(this).navigate(R.id.action_prayerRuleFragment_to_prayerFragment, bundle);
                        }
                    }
            });
        }else {
            Bundle bundle = new Bundle();
            bundle.putString("prevMenu", prevMenuData);
            bundle.putInt("prayerId", Integer.parseInt(prayerIdData));
            if(!Objects.equals(prevMenuData, "psaltir")) {
                bundle.putString("mode", "prayer_read");
            }else {
                bundle.putInt("blockId", 1);
                bundle.putString("mode", "psaltir_read");
            }
            FragmentKt.findNavController(this).navigate(R.id.action_prayerRuleFragment_to_prayerFragment, bundle);
        }
    }
}