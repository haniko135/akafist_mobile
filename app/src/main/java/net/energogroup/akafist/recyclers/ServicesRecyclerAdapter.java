package net.energogroup.akafist.recyclers;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.FragmentKt;
import androidx.recyclerview.widget.RecyclerView;

import net.energogroup.akafist.MainActivity;
import net.energogroup.akafist.R;
import net.energogroup.akafist.db.StarredDTO;
import net.energogroup.akafist.models.ServicesModel;

import net.energogroup.akafist.fragments.ChurchFragment;

import java.util.List;

/**
 * RecyclerView adapter class for the {@link ChurchFragment} class
 * @author Nastya Izotina
 * @version 1.0.0
 */
public class ServicesRecyclerAdapter extends RecyclerView.Adapter<ServicesRecyclerAdapter.ServicesViewHolder> {
    private final List<ServicesModel> servicesModels;
    private Fragment fragment;
    private final MainActivity mainActivity;
    private final SQLiteDatabase db;


    public ServicesRecyclerAdapter(List<ServicesModel> servicesModels, Fragment fragment) {
        this.servicesModels = servicesModels;
        this.fragment = fragment;

        mainActivity = (MainActivity) fragment.getActivity();
        db = mainActivity.getDbHelper().getWritableDatabase();
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
            bundle.putString("mode", "prayer_read");
            FragmentKt.findNavController(fragment).navigate(R.id.action_churchFragment_to_prayerFragment, bundle);
        });

        Cursor cursorPrayer = db.rawQuery("SELECT * FROM " + StarredDTO.TABLE_NAME + " WHERE "
                + StarredDTO.COLUMN_NAME_OBJECT_URL + "='" +
                servicesModels.get(position).getDate()+"/"+servicesModels.get(position).getName()+"/"+servicesModels.get(position).getId()+"'",
                null);
        if(cursorPrayer.moveToFirst()){
            holder.getServiceListItemStarBorder().setVisibility(View.GONE);
            holder.getServiceListItemStar().setVisibility(View.VISIBLE);
        }else {
            holder.getServiceListItemStar().setVisibility(View.GONE);
            holder.getServiceListItemStarBorder().setVisibility(View.VISIBLE);
        }
        cursorPrayer.close();

        holder.getServiceListItemStarBorder().setOnClickListener(view -> {
            ContentValues contentValues = new ContentValues();
            contentValues.put(StarredDTO.COLUMN_NAME_OBJECT_URL, servicesModels.get(position).getDate()
                    +"/"+servicesModels.get(position).getName()+"/"+servicesModels.get(position).getId());
            contentValues.put(StarredDTO.COLUMN_NAME_OBJECT_TYPE, "text-prayers");
            contentValues.put(StarredDTO.COLUMN_NAME_ID, Math.round(Math.random()*10000));

            db.insert(StarredDTO.TABLE_NAME,null, contentValues);

            holder.serviceListItemStarBorder.setVisibility(View.GONE);
            holder.serviceListItemStar.setVisibility(View.VISIBLE);
        });

        holder.getServiceListItemStar().setOnClickListener(view -> {
            String[] selectionArgs = { servicesModels.get(position).getDate()+"/"
                    +servicesModels.get(position).getName()+"/"+servicesModels.get(position).getId() };
            db.delete(StarredDTO.TABLE_NAME,StarredDTO.COLUMN_NAME_OBJECT_URL + " LIKE ?",
                    selectionArgs);

            holder.serviceListItemStar.setVisibility(View.GONE);
            holder.serviceListItemStarBorder.setVisibility(View.VISIBLE);
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
        private final TextView serviceListItem;
        private final ImageButton serviceListItemStarBorder;
        private final ImageButton serviceListItemStar;
        public TextView getServiceListItem() {
            return serviceListItem;
        }

        public ImageButton getServiceListItemStarBorder() {
            return serviceListItemStarBorder;
        }

        public ImageButton getServiceListItemStar() {
            return serviceListItemStar;
        }

        public ServicesViewHolder(@NonNull View itemView) {
            super(itemView);
            this.serviceListItem = itemView.findViewById(R.id.prayers_list_item);
            this.serviceListItemStarBorder = itemView.findViewById(R.id.prayers_list_item_star_border);
            this.serviceListItemStar = itemView.findViewById(R.id.prayers_list_item_star);
        }
    }
}
