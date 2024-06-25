package net.energogroup.akafist.recyclers;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.FragmentKt;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import net.energogroup.akafist.R;
import net.energogroup.akafist.db.StarredDTO;
import net.energogroup.akafist.fragments.lists.ItemMoveCallback;
import net.energogroup.akafist.fragments.lists.ItemSwipeCallback;
import net.energogroup.akafist.fragments.lists.StartDragListner;
import net.energogroup.akafist.fragments.lists.TextPrayersDragDropListFragment;
import net.energogroup.akafist.models.ServicesModel;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class DragAndDropAdapter extends RecyclerView.Adapter<DragAndDropAdapter.DragAndDropViewHolder>
        implements ItemMoveCallback.ItemTouchHelperContract, ItemSwipeCallback.ItemSwipeContract{

    private final StartDragListner mStartDragListener;
    private final List<ServicesModel> textPrayers;
    private final TextPrayersDragDropListFragment fragment;
    private final SQLiteDatabase db;

    public List<ServicesModel> getTextPrayers() {
        return textPrayers;
    }

    public DragAndDropAdapter(StartDragListner mStartDragListener, List<ServicesModel> textPrayers, TextPrayersDragDropListFragment frag, SQLiteDatabase db) {
        this.mStartDragListener = mStartDragListener;
        this.textPrayers = textPrayers;
        this.fragment = frag;
        this.db = db;
    }

    @NonNull
    @Override
    public DragAndDropViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.drag_and_drop_list, parent, false);
        return new DragAndDropViewHolder(itemView);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull DragAndDropViewHolder holder, int position) {
        holder.dragAndDropTitle.setText(textPrayers.get(position).getName());
        holder.dragAnDropTouch.setOnTouchListener((view, motionEvent) -> {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                mStartDragListener.requestDrag(holder);
            }
            return false;
        });
        holder.dragAndDropTitle.setOnClickListener(view -> {
            if(fragment.isMode())  {
                int curPosition = position;
                fragment.getViewModel()
                        .convertToPrayersModels(fragment.getContext(), db)
                        .observe(fragment.getViewLifecycleOwner(), prayersModels -> {
                                if(prayersModels.size() == fragment.getViewModel().getPrayerRules().getValue().size()) {

                                    Gson gson = new Gson();
                                    String jsonString = gson.toJson(prayersModels);

                                    ContentValues contentValues = new ContentValues();
                                    contentValues.put(StarredDTO.COLUMN_NAME_OBJECT_URL, jsonString);
                                    contentValues.put(StarredDTO.COLUMN_NAME_OBJECT_TYPE, "prayer_rule_col");
                                    contentValues.put(StarredDTO.COLUMN_NAME_ID, Math.round(Math.random() * 1000));
                                    Log.w("STARRED", contentValues.toString());
                                    db.insert(StarredDTO.TABLE_NAME, null, contentValues);
                                    //isConverted.setValue(true);

                                    NavController navController = Navigation.findNavController(fragment.requireActivity(), R.id.nav_fragment);
                                    NavDestination currentDestination = navController.getCurrentDestination();

                                    if (currentDestination != null && currentDestination.getId() != R.id.prayerFragment) {
                                        Bundle bundle = new Bundle();
                                        bundle.putString("prevMenu", textPrayers.get(curPosition).getDate());
                                        bundle.putInt("prayerId", textPrayers.get(curPosition).getId());
                                        bundle.putString("mode", "prayer_rule");

                                        FragmentKt.findNavController(fragment).navigate(R.id.action_prayerRuleFragment_to_prayerFragment, bundle);
                                    }
                                } else if (prayersModels.size() > fragment.getViewModel().getPrayerRules().getValue().size()) {
                                    prayersModels.clear();
                                }
                });
            } else {
                Bundle bundle = new Bundle();
                bundle.putString("prevMenu", textPrayers.get(position).getDate());
                bundle.putInt("prayerId", textPrayers.get(position).getId());
                if(!Objects.equals(textPrayers.get(position).getDate(), "psaltir")) {
                    bundle.putString("mode", "prayer_read");
                }else {
                    bundle.putInt("blockId", 1);
                    bundle.putString("mode", "psaltir_read");
                }
                Log.e("DRAG_DROP", bundle.toString());
                FragmentKt.findNavController(fragment).navigate(R.id.action_prayerRuleFragment_to_prayerFragment, bundle);
            }
            //тут указание на фрагмент молитвенного правила
        });
    }

    @Override
    public int getItemCount() {
        return textPrayers.size();
    }

    @Override
    public void onRowMoved(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(textPrayers, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(textPrayers, i, i - 1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onRowSelected(DragAndDropAdapter.DragAndDropViewHolder myViewHolder) {
        myViewHolder.rowView.setBackgroundColor(R.color.block_main_selected);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onRowClear(DragAndDropAdapter.DragAndDropViewHolder myViewHolder) {
        myViewHolder.rowView.setBackground(fragment.getActivity().getDrawable(R.drawable.button_template));
        fragment.getViewModel().savePrayerRuleArray(textPrayers, db);
    }

    @Override
    public void removeItem(int position) {
        textPrayers.remove(position);
        notifyItemRemoved(position);
    }
    @Override
    public void restoreItem(ServicesModel item, int position) {
        textPrayers.add(position, item);
        notifyItemInserted(position);
    }

    public static class DragAndDropViewHolder extends RecyclerView.ViewHolder{

        private final TextView dragAndDropTitle;
        private final ImageButton dragAnDropTouch;
        View rowView;

        public DragAndDropViewHolder(@NonNull View itemView) {
            super(itemView);
            this.dragAndDropTitle = itemView.findViewById(R.id.drag_and_drop_text);
            this.dragAnDropTouch = itemView.findViewById(R.id.drag_and_drop_touch);
            this.rowView = itemView;
        }
    }
}
