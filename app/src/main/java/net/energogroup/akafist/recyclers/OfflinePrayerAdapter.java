package net.energogroup.akafist.recyclers;

import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.FragmentKt;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;


import net.energogroup.akafist.MainActivity;
import net.energogroup.akafist.R;
import net.energogroup.akafist.models.PrayersModels;
import net.energogroup.akafist.models.ServicesModel;
import net.energogroup.akafist.service.background.DownloadPrayer;

import java.util.ArrayList;
import java.util.List;


public class OfflinePrayerAdapter extends RecyclerView.Adapter<OfflinePrayerAdapter.OfflinePrayerViewHolder> {

    private List<ServicesModel> prayers = new ArrayList<>();
    private Fragment fragment;
    private MainActivity mainActivity;

    public OfflinePrayerAdapter() { }

    public void setPrayers(List<ServicesModel> prayersTemp){
        prayers.clear();
        prayers.addAll(prayersTemp);
    }

    public void setFragment(Fragment fragment) {
        this.fragment = fragment;
    }

    public void setMainActivity(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Override
    public OfflinePrayerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.prayers_list, parent, false);
        return new OfflinePrayerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(OfflinePrayerViewHolder holder, int position) {
        holder.bind(prayers.get(position));
        holder.prayerListItem.setOnClickListener(view -> {
            Bundle bundle = new Bundle();
            bundle.putString("prevMenu", prayers.get(position).getDate());
            bundle.putInt("prayerId", prayers.get(position).getId());
            FragmentKt.findNavController(fragment).navigate(R.id.action_offlinePrayerFragment_to_prayerFragment, bundle);
        });

        holder.prayerListItemDownloaded.setOnClickListener(view -> {
            DownloadPrayer downloadPrayer = new DownloadPrayer();
            downloadPrayer.init(mainActivity);
            downloadPrayer.deletePrayer(prayers.get(position).getDate(), prayers.get(position).getName());

            holder.switchDownloadStatus(false);

            deletePrayer(prayers.get(position));
        });
    }

    @Override
    public int getItemCount() {
        return prayers.size();
    }

    public void deletePrayer(ServicesModel prayer){
        int position = prayers.indexOf(prayer);
        prayers.remove(prayer);
        notifyItemRemoved(position);
    }

    public static class OfflinePrayerViewHolder extends RecyclerView.ViewHolder {
        public final TextView prayerListItem;
        public final ImageButton prayerListItemStarBorder;
        public final ImageButton prayerListItemStar;
        public final ImageButton prayerListItemDownload;
        public final ImageButton prayerListItemDownloaded;

        public OfflinePrayerViewHolder(View itemView) {
            super(itemView);
            this.prayerListItem = itemView.findViewById(R.id.prayers_list_item);
            this.prayerListItemStarBorder = itemView.findViewById(R.id.prayers_list_item_star_border);
            this.prayerListItemStar = itemView.findViewById(R.id.prayers_list_item_star);
            this.prayerListItemDownload = itemView.findViewById(R.id.prayers_list_item_download);
            this.prayerListItemDownloaded = itemView.findViewById(R.id.prayers_list_item_downloaded);
        }

        public void bind(ServicesModel prayer) {
            this.prayerListItem.setText(prayer.getName().split("/")[1]);
            this.prayerListItemStarBorder.setVisibility(View.GONE);
            this.prayerListItemStar.setVisibility(View.GONE);
            this.prayerListItemDownload.setVisibility(View.GONE);
            this.prayerListItemDownloaded.setVisibility(View.VISIBLE);
        }

        public void switchDownloadStatus(boolean status){
            // status = true - prayer was downloaded in device
            // status = false - prayer was deleted or not downloaded yet

            if (status){
                prayerListItemDownload.setVisibility(View.GONE);
                prayerListItemDownloaded.setVisibility(View.VISIBLE);
            }else {
                prayerListItemDownloaded.setVisibility(View.GONE);
                prayerListItemDownload.setVisibility(View.VISIBLE);
            }
        }
    }
}