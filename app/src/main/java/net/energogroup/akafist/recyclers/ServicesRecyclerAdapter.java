package net.energogroup.akafist.recyclers;

import android.annotation.SuppressLint;
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
import net.energogroup.akafist.models.ServicesModel;

import net.energogroup.akafist.fragments.ChurchFragment;

import java.util.List;

/**
 * RecyclerView adapter class for the {@link ChurchFragment} class
 * @author Nastya Izotina
 * @version 1.0.0
 */
public class ServicesRecyclerAdapter extends RecyclerView.Adapter<ServicesRecyclerAdapter.ServicesViewHolder> {
    private List<ServicesModel> servicesModels;
    private Fragment fragment;


    public ServicesRecyclerAdapter(List<ServicesModel> servicesModels) {
        this.servicesModels = servicesModels;
    }

    /**
     * This method assigns the current fragment
     * @param fragment
     */
    public void setFragment(Fragment fragment) {
        this.fragment = fragment;
    }

    @NonNull
    @Override
    public ServicesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.prayers_list, parent, false);
        return new ServicesViewHolder(itemView);
    }

    /**
     * Этот метод отвечает за логику, происходящую в каждом элементе RecyclerView
     * @param holder list item
     * @param position Position in the list
     */
    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull ServicesViewHolder holder, int position) {
        holder.getServiceListItem().setText(servicesModels.get(position).getName());
        holder.getServiceListItem().setOnClickListener(view -> {
            holder.getServiceListItem().setBackgroundColor(R.color.white);
            Bundle bundle = new Bundle();
            bundle.putString("prevMenu", servicesModels.get(position).getDate());
            bundle.putInt("prayerId", servicesModels.get(position).getId());
            FragmentKt.findNavController(fragment).navigate(R.id.action_churchFragment_to_prayerFragment, bundle);
        });
    }

    @Override
    public int getItemCount() {
        return servicesModels.size();
    }

    /**
     * The internal class responsible for the correct display of the RecyclerView element
     */
    static class ServicesViewHolder extends RecyclerView.ViewHolder{
        private TextView serviceListItem;

        public TextView getServiceListItem() {
            return serviceListItem;
        }

        public ServicesViewHolder(@NonNull View itemView) {
            super(itemView);
            this.serviceListItem = itemView.findViewById(R.id.prayers_list_item);
        }
    }
}
