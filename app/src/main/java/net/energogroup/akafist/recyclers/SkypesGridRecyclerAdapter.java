package net.energogroup.akafist.recyclers;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import net.energogroup.akafist.R;
import net.energogroup.akafist.models.SkypesConfs;

import net.energogroup.akafist.fragments.SkypesBlocksFragment;

import java.util.List;

/**
 * The RecyclerView adapter class for the {@link SkypesBlocksFragment} class
 * @author Nastya Izotina
 * @version 1.0.0
 */
public class SkypesGridRecyclerAdapter extends RecyclerView.Adapter<SkypesGridRecyclerAdapter.SkypesGridViewHolder> {

    private List<SkypesConfs> skypesConfs;

    public SkypesGridRecyclerAdapter(List<SkypesConfs> skypesConfs) {
        this.skypesConfs = skypesConfs;
    }

    @NonNull
    @Override
    public SkypesGridViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.prayers_list_grid, parent, false);
        return new SkypesGridViewHolder(itemView);
    }

    /**
     * This method is responsible for the logic that occurs in each element of the Recycler View
     * @param holder List Item
     * @param position Position in the list
     */
    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull SkypesGridViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.getSkypesListItem().setText(skypesConfs.get(position).getName());
        holder.getSkypesListItem().setOnClickListener(view -> {
            holder.getSkypesListItem().setBackgroundColor(R.color.white);
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(skypesConfs.get(position).getUrl()));
            view.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return skypesConfs.size();
    }

    /**
     * The internal class responsible for the correct display of the RecyclerView element
     */
    static class SkypesGridViewHolder extends RecyclerView.ViewHolder{
        private TextView skypesListItem;

        public SkypesGridViewHolder(@NonNull View itemView) {
            super(itemView);
            this.skypesListItem = itemView.findViewById(R.id.prayers_list_item_grid);
        }

        public TextView getSkypesListItem() {
            return skypesListItem;
        }
    }
}
