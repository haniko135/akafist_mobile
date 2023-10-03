package net.energogroup.akafist.recyclers;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.FragmentKt;
import androidx.recyclerview.widget.RecyclerView;

import net.energogroup.akafist.R;
import net.energogroup.akafist.fragments.SkypesFragment;
import net.energogroup.akafist.models.SkypesConfs;

import java.util.List;

/**
 * The RecyclerView adapter class for the {@link SkypesFragment} class
 * @author Nastya Izotina
 * @version 1.0.0
 */
public class SkypesRecyclerAdapter extends RecyclerView.Adapter<SkypesRecyclerAdapter.SkypesViewHolder>{
    private List<SkypesConfs> skypesConfs;
    private Fragment fragment;

    /**
     * List's constructor
     * @param skypesConfs - list of skype conferences
     * @param fragment - SkypesFragment context
     */
    public SkypesRecyclerAdapter(List<SkypesConfs> skypesConfs, SkypesFragment fragment) {
        this.skypesConfs = skypesConfs;
        this.fragment = fragment;
    }

    /**
     * Creates ViewHolder for skypes
     * @param parent   The ViewGroup into which the new View will be added after it is bound to
     *                 an adapter position.
     * @param viewType The view type of the new View.
     * @return
     */
    @NonNull
    @Override
    public SkypesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.prayers_list, parent, false);
        return new SkypesViewHolder(itemView);
    }

    /**
     * This method is responsible for the logic that occurs in each element of the Recycler View
     * @param holder List Item
     * @param position Position in the list
     */
    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull SkypesViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.skypesListItem.setText(skypesConfs.get(position).getName());
        if(skypesConfs.get(position).isUrl()) {
            holder.skypesListItem.setOnClickListener(view -> {
                holder.skypesListItem.setBackgroundColor(R.color.white);
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(skypesConfs.get(position).getUrl()));
                view.getContext().startActivity(intent);
            });
        }else {
            holder.skypesListItem.setOnClickListener(view -> {
                holder.skypesListItem.setBackgroundColor(R.color.white);
                Bundle bundle = new Bundle();
                bundle.putString("nameTitle", skypesConfs.get(position).getName());
                bundle.putInt("urlId", skypesConfs.get(position).getId());
                FragmentKt.findNavController(fragment).navigate(R.id.action_skypesFragment_to_skypesBlocksFragment, bundle);
            });
        }
    }

    @Override
    public int getItemCount() {
        return skypesConfs.size();
    }


    /**
     * The internal class responsible for the correct display of the RecyclerView element
     */
    static class SkypesViewHolder extends RecyclerView.ViewHolder{

        public TextView skypesListItem;

        public SkypesViewHolder(@NonNull View itemView) {
            super(itemView);
            this.skypesListItem = itemView.findViewById(R.id.prayers_list_item);
        }
    }
}
