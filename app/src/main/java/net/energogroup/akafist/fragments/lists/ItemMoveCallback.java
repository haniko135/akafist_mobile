package net.energogroup.akafist.fragments.lists;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import net.energogroup.akafist.recyclers.DragAndDropAdapter;

public class ItemMoveCallback extends ItemTouchHelper.Callback {

    private ItemTouchHelperContract mAdapter;

    public ItemMoveCallback(ItemTouchHelperContract mAdapters) {
        mAdapter = mAdapters;
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return false;
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return false;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

    }

    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        return makeMovementFlags(dragFlags, 0);
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        mAdapter.onRowMoved(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        return true;
    }

    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
        if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
            if (viewHolder instanceof DragAndDropAdapter.DragAndDropViewHolder) {
                DragAndDropAdapter.DragAndDropViewHolder myViewHolder=
                        (DragAndDropAdapter.DragAndDropViewHolder) viewHolder;
                mAdapter.onRowSelected(myViewHolder);
            }

        }

        super.onSelectedChanged(viewHolder, actionState);
    }

    @Override
    public void clearView(RecyclerView recyclerView,
                          RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);

        if (viewHolder instanceof DragAndDropAdapter.DragAndDropViewHolder) {
            DragAndDropAdapter.DragAndDropViewHolder myViewHolder=
                    (DragAndDropAdapter.DragAndDropViewHolder) viewHolder;
            mAdapter.onRowClear(myViewHolder);
        }
    }

    public interface ItemTouchHelperContract {

        void onRowMoved(int fromPosition, int toPosition);
        void onRowSelected(DragAndDropAdapter.DragAndDropViewHolder myViewHolder);
        void onRowClear(DragAndDropAdapter.DragAndDropViewHolder myViewHolder);
    }

}
