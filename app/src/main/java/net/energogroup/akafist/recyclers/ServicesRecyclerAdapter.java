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
import net.energogroup.akafist.db.PrayersDTO;
import net.energogroup.akafist.db.StarredDTO;
import net.energogroup.akafist.models.ServicesModel;

import net.energogroup.akafist.fragments.ChurchFragment;
import net.energogroup.akafist.service.background.DownloadPrayer;

import java.util.ArrayList;
import java.util.List;

/**
 * RecyclerView adapter class for the {@link ChurchFragment} class
 * @author Nastya Izotina
 * @version 1.0.0
 */
public class ServicesRecyclerAdapter extends RecyclerView.Adapter<ServicesRecyclerAdapter.ServicesViewHolder> {
    private final List<ServicesModel> servicesModels = new ArrayList<>();
    private Fragment fragment;
    private MainActivity mainActivity;
    private SQLiteDatabase db;


    /**
     * This method initializes the adapter
     */
    public void init(){
        mainActivity = (MainActivity) fragment.getActivity();
        db = mainActivity.getDbHelper().getWritableDatabase();
    }

    /**
     * This method assigns the data to the adapter
     */
    public void setData(List<ServicesModel> servicesModelsL){
        servicesModels.clear();
        servicesModels.addAll(servicesModelsL);
    }

    /**
     * This method assigns the current fragment
     */
    public void setFragment(Fragment fragment) {
        this.fragment = fragment;
    }

    /**
     * This method sets the current viewholder
     * @param parent   The ViewGroup into which the new View will be added after it is bound to
     *                 an adapter position.
     * @param viewType The view type of the new View.
     */
    @NonNull
    @Override
    public ServicesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.prayers_list, parent, false);
        return new ServicesViewHolder(itemView);
    }

    /**
     * This method binds the data to the view
     * @param holder   The ViewHolder which should be updated to represent the contents of the
     *                 item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull ServicesViewHolder holder, int position) {
        String selectParam = servicesModels.get(position).getDate()+"/"+servicesModels.get(position).getName()+"/"+servicesModels.get(position).getId();
        Cursor cursorPrayer = db.rawQuery("SELECT * FROM " + StarredDTO.TABLE_NAME + " WHERE "
                        + StarredDTO.COLUMN_NAME_OBJECT_URL + "='" + selectParam+"'", null);
        holder.switchStarredStatus(cursorPrayer.moveToFirst());
        cursorPrayer.close();


        String selectName = servicesModels.get(position).getDate()+"/"+servicesModels.get(position).getName();
        Cursor cursorPrayerDB = db.rawQuery("SELECT * FROM " + PrayersDTO.TABLE_NAME + " WHERE "
                + PrayersDTO.COLUMN_NAME_NAME + "='" + selectName +"'", null);
        holder.switchDownloadStatus(cursorPrayerDB.moveToFirst());
        cursorPrayerDB.close();


        holder.getServiceListItem().setText(servicesModels.get(position).getName());
        holder.getServiceListItem().setOnClickListener(view -> {
            holder.getServiceListItem().setBackgroundColor(R.color.white);
            Bundle bundle = new Bundle();
            bundle.putString("prevMenu", servicesModels.get(position).getDate());
            bundle.putInt("prayerId", servicesModels.get(position).getId());
            bundle.putString("mode", "prayer_read");
            FragmentKt.findNavController(fragment).navigate(R.id.action_churchFragment_to_prayerFragment, bundle);
        });


        holder.getServiceListItemStarBorder().setOnClickListener(view -> {
            ContentValues contentValues = new ContentValues();
            contentValues.put(StarredDTO.COLUMN_NAME_OBJECT_URL, servicesModels.get(position).getDate()
                    +"/"+servicesModels.get(position).getName()+"/"+servicesModels.get(position).getId());
            contentValues.put(StarredDTO.COLUMN_NAME_OBJECT_TYPE, "text-prayers");
            contentValues.put(StarredDTO.COLUMN_NAME_ID, Math.round(Math.random()*10000));

            db.insert(StarredDTO.TABLE_NAME,null, contentValues);

            holder.switchStarredStatus(true);
            notifyItemChanged(position);
        });

        holder.getServiceListItemStar().setOnClickListener(view -> {
            String[] selectionArgs = { servicesModels.get(position).getDate()+"/"
                    +servicesModels.get(position).getName()+"/"+servicesModels.get(position).getId() };
            db.delete(StarredDTO.TABLE_NAME,StarredDTO.COLUMN_NAME_OBJECT_URL + " LIKE ?",
                    selectionArgs);

            holder.switchStarredStatus(false);
            notifyItemChanged(position);
        });


        //downloading prayer
        holder.getServiceListItemDownload().setOnClickListener(view -> {
            DownloadPrayer downloadPrayer = new DownloadPrayer();
            downloadPrayer.setData(servicesModels.get(position).getDate(),servicesModels.get(position).getId());
            downloadPrayer.init(mainActivity);
            downloadPrayer.downloadPrayer();

            holder.switchDownloadStatus(true);
            notifyItemChanged(position);
        });

        holder.getServiceListItemDownloaded().setOnClickListener(view -> {
            DownloadPrayer downloadPrayer = new DownloadPrayer();
            downloadPrayer.init(mainActivity);
            downloadPrayer.deletePrayer(servicesModels.get(position).getDate(), servicesModels.get(position).getName());

            holder.switchDownloadStatus(false);
            notifyItemChanged(position);
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
        private final ImageButton serviceListItemDownload;
        private final ImageButton serviceListItemDownloaded;

        public TextView getServiceListItem() {
            return serviceListItem;
        }

        public ImageButton getServiceListItemStarBorder() {
            return serviceListItemStarBorder;
        }

        public ImageButton getServiceListItemStar() {
            return serviceListItemStar;
        }

        public ImageButton getServiceListItemDownload() {
            return serviceListItemDownload;
        }

        public ImageButton getServiceListItemDownloaded() {
            return serviceListItemDownloaded;
        }

        public ServicesViewHolder(@NonNull View itemView) {
            super(itemView);
            this.serviceListItem = itemView.findViewById(R.id.prayers_list_item);
            this.serviceListItemStarBorder = itemView.findViewById(R.id.prayers_list_item_star_border);
            this.serviceListItemStar = itemView.findViewById(R.id.prayers_list_item_star);
            this.serviceListItemDownload = itemView.findViewById(R.id.prayers_list_item_download);
            this.serviceListItemDownloaded = itemView.findViewById(R.id.prayers_list_item_downloaded);
        }


        public void switchDownloadStatus(boolean status){
            // status = true - prayer was downloaded in device
            // status = false - prayer was deleted or not downloaded yet

            if (status){
                serviceListItemDownload.setVisibility(View.GONE);
                serviceListItemDownloaded.setVisibility(View.VISIBLE);
            }else {
                serviceListItemDownloaded.setVisibility(View.GONE);
                serviceListItemDownload.setVisibility(View.VISIBLE);
            }
        }

        public void switchStarredStatus(boolean status){
            // status = true - prayer was starred
            // status = false - prayer was unstarred

            if(status){
                serviceListItemStarBorder.setVisibility(View.GONE);
                serviceListItemStar.setVisibility(View.VISIBLE);
            }else {
                serviceListItemStar.setVisibility(View.GONE);
                serviceListItemStarBorder.setVisibility(View.VISIBLE);
            }
        }
    }
}
