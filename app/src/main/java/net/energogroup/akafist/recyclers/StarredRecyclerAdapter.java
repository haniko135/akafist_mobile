package net.energogroup.akafist.recyclers;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
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

import java.util.List;

public class StarredRecyclerAdapter extends RecyclerView.Adapter<StarredRecyclerAdapter.StarredViewHolder> {

    private MainActivity mainActivity;
    private Fragment fragment;
    private SQLiteDatabase db;
    private List<ServicesModel> prayersModels;


    public StarredRecyclerAdapter(MainActivity mainActivity, Fragment fragment, List<ServicesModel> prayersModels) {
        this.mainActivity = mainActivity;
        this.fragment = fragment;
        this.prayersModels = prayersModels;

        this.mainActivity = (MainActivity) this.fragment.getActivity();
        db = mainActivity.getDbHelper().getWritableDatabase();
    }

    @NonNull
    @Override
    public StarredViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.prayers_list, parent, false);
        return new StarredViewHolder(itemView);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull StarredViewHolder holder, int position) {
        holder.prayersListItem.setText(prayersModels.get(position).getName());

        Cursor cursorPrayer = db.rawQuery("SELECT * FROM " + StarredDTO.TABLE_NAME + " WHERE "
                        + StarredDTO.COLUMN_NAME_OBJECT_URL + "='" +
                        prayersModels.get(position).getDate()+"/"+prayersModels.get(position).getName()
                        +"/"+prayersModels.get(position).getId()+"'",
                null);
        if(cursorPrayer.moveToFirst()){
            holder.prayersListItemStarBorder.setVisibility(View.GONE);
            holder.prayersListItemStar.setVisibility(View.VISIBLE);
        }else {
            holder.prayersListItemStar.setVisibility(View.GONE);
            holder.prayersListItemStarBorder.setVisibility(View.VISIBLE);
        }
        cursorPrayer.close();

        holder.prayersListItemStarBorder.setOnClickListener(view -> {
            ContentValues contentValues = new ContentValues();
            contentValues.put(StarredDTO.COLUMN_NAME_OBJECT_URL, prayersModels.get(position).getDate()
                    +"/"+prayersModels.get(position).getName()+"/"+prayersModels.get(position).getId());
            contentValues.put(StarredDTO.COLUMN_NAME_OBJECT_TYPE, "text-prayers");
            contentValues.put(StarredDTO.COLUMN_NAME_ID, Math.round(Math.random()*10000));

            db.insert(StarredDTO.TABLE_NAME,null, contentValues);

            holder.prayersListItemStarBorder.setVisibility(View.GONE);
            holder.prayersListItemStar.setVisibility(View.VISIBLE);
        });

        holder.prayersListItemStar.setOnClickListener(view -> {
            String[] selectionArgs = { prayersModels.get(position).getDate()+"/"
                    +prayersModels.get(position).getName()+"/"+prayersModels.get(position).getId() };
            db.delete(StarredDTO.TABLE_NAME,StarredDTO.COLUMN_NAME_OBJECT_URL + " LIKE ?",
                    selectionArgs);

            holder.prayersListItemStar.setVisibility(View.GONE);
            holder.prayersListItemStarBorder.setVisibility(View.VISIBLE);
        });

        holder.prayersListItem.setOnClickListener(view -> {
            holder.prayersListItem.setBackgroundColor(R.color.white);
            Bundle bundle = new Bundle();
            bundle.putString("prevMenu", prayersModels.get(position).getDate());
            bundle.putInt("prayerId", prayersModels.get(position).getId());
            FragmentKt.findNavController(fragment).navigate(R.id.action_starredFragment_to_prayerFragment, bundle);
        });
    }

    @Override
    public int getItemCount() {
        return prayersModels.size();
    }

    static class StarredViewHolder extends RecyclerView.ViewHolder{
        public TextView prayersListItem;
        public ImageButton prayersListItemStarBorder;
        public ImageButton prayersListItemStar;
        public StarredViewHolder(@NonNull View itemView) {
            super(itemView);
            this.prayersListItem = itemView.findViewById(R.id.prayers_list_item);
            this.prayersListItemStarBorder = itemView.findViewById(R.id.prayers_list_item_star_border);
            this.prayersListItemStar = itemView.findViewById(R.id.prayers_list_item_star);
        }
    }
}
