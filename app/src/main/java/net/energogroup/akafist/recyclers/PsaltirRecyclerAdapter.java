package net.energogroup.akafist.recyclers;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
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
import net.energogroup.akafist.models.psaltir.PsaltirKafismaModel;
import net.energogroup.akafist.service.background.DownloadPrayer;

import java.util.ArrayList;
import java.util.List;

public class PsaltirRecyclerAdapter extends RecyclerView.Adapter<PsaltirRecyclerAdapter.PsaltirViewHolder> {

    private final List<PsaltirKafismaModel> psaltirKafismaModels = new ArrayList<>();
    private Fragment fr;
    private int blockId;
    private MainActivity mainActivity;
    private SQLiteDatabase db;

    public PsaltirRecyclerAdapter() { }

    public void setData(List<PsaltirKafismaModel> psaltirKafismaModelsL){
        psaltirKafismaModels.clear();
        psaltirKafismaModels.addAll(psaltirKafismaModelsL);
    }

    public void setFragment(Fragment fr){
        this.fr = fr;
    }

    public void setBlockId(int blockId){
        this.blockId = blockId;
    }

    public void init(){
        mainActivity = (MainActivity) fr.getActivity();
        db = mainActivity.getDbHelper().getWritableDatabase();
    }

    @NonNull
    @Override
    public PsaltirViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.prayers_list, parent, false);
        return new PsaltirViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull PsaltirViewHolder holder, int position) {
        Cursor cursorPrayer = db.rawQuery("SELECT * FROM " + StarredDTO.TABLE_NAME + " WHERE "
                        + StarredDTO.COLUMN_NAME_OBJECT_URL + "='" +
                        "psaltir/"+psaltirKafismaModels.get(position).getName()+"/"+psaltirKafismaModels.get(position).getId()+"'",
                null);
        holder.switchStarredStatus(cursorPrayer.moveToFirst());
        cursorPrayer.close();

        String selectName = blockId + "/" + psaltirKafismaModels.get(position).getName();
        Cursor cursorPrayerDB = db.rawQuery("SELECT * FROM " + PrayersDTO.TABLE_NAME + " WHERE "
                + PrayersDTO.COLUMN_NAME_NAME + "='" + selectName +"'", null);
        holder.switchDownloadStatus(cursorPrayerDB.moveToFirst());
        cursorPrayerDB.close();


        holder.getPsaltirListItem().setText(psaltirKafismaModels.get(position).getName());
        holder.getPsaltirListItem().setOnClickListener(view -> {
            if(psaltirKafismaModels.get(position).getDesc() != null) {
                if (psaltirKafismaModels.get(position).getDesc().startsWith("https://")) {
                    Intent toSite = new Intent(Intent.ACTION_VIEW, Uri.parse(psaltirKafismaModels.get(position).getDesc()));
                    fr.getContext().startActivity(toSite);
                }
                else {
                    Bundle bundle = new Bundle();
                    bundle.putInt("blockId", blockId);
                    bundle.putInt("prayerId", psaltirKafismaModels.get(position).getId());
                    bundle.putString("mode", "psaltir_read");
                    FragmentKt.findNavController(fr).navigate(R.id.action_psaltirFragment_to_prayerFragment, bundle);
                }
            }else {
                Bundle bundle = new Bundle();
                bundle.putInt("blockId", blockId);
                bundle.putInt("prayerId", psaltirKafismaModels.get(position).getId());
                bundle.putString("mode", "psaltir_read");
                FragmentKt.findNavController(fr).navigate(R.id.action_psaltirFragment_to_prayerFragment, bundle);
            }
        });


        holder.getPsaltirListItemStarBorder().setOnClickListener(view -> {
            ContentValues contentValues = new ContentValues();
            contentValues.put(StarredDTO.COLUMN_NAME_OBJECT_URL, "psaltir/"+psaltirKafismaModels.get(position).getName()+"/"+psaltirKafismaModels.get(position).getId());
            contentValues.put(StarredDTO.COLUMN_NAME_OBJECT_TYPE, "text-prayers");
            contentValues.put(StarredDTO.COLUMN_NAME_ID, Math.round(Math.random()*10000));

            db.insert(StarredDTO.TABLE_NAME,null, contentValues);

            holder.switchStarredStatus(true);
        });

        holder.getPsaltirListItemStar().setOnClickListener(view -> {
            String[] selectionArgs = { "psaltir/"+psaltirKafismaModels.get(position).getName()+"/"+psaltirKafismaModels.get(position).getId() };
            db.delete(StarredDTO.TABLE_NAME,StarredDTO.COLUMN_NAME_OBJECT_URL + " LIKE ?",
                    selectionArgs);

            holder.switchStarredStatus(false);
        });


        holder.getPsaltirListItemDownload().setOnClickListener(view -> {
            DownloadPrayer downloadPrayer = new DownloadPrayer();
            downloadPrayer.setData(String.valueOf(blockId), psaltirKafismaModels.get(position).getId());
            downloadPrayer.init(mainActivity);
            downloadPrayer.downloadPsaltir();

            holder.switchDownloadStatus(true);
        });

        holder.getPsaltirListItemDownloaded().setOnClickListener(view -> {
            DownloadPrayer downloadPrayer = new DownloadPrayer();
            downloadPrayer.init(mainActivity);
            downloadPrayer.deletePrayer(String.valueOf(blockId), psaltirKafismaModels.get(position).getName());

            holder.switchDownloadStatus(false);
        });
    }

    @Override
    public int getItemCount() {
        return psaltirKafismaModels.size();
    }

    static class PsaltirViewHolder extends RecyclerView.ViewHolder{
        private final TextView psaltirListItem;
        private final ImageButton psaltirListItemStarBorder;
        private final ImageButton psaltirListItemStar;
        private final ImageButton psaltirListItemDownload;
        private final ImageButton psaltirListItemDownloaded;

        public TextView getPsaltirListItem() { return psaltirListItem; }

        public ImageButton getPsaltirListItemStarBorder() { return psaltirListItemStarBorder; }

        public ImageButton getPsaltirListItemStar() { return psaltirListItemStar; }

        public ImageButton getPsaltirListItemDownload() {
            return psaltirListItemDownload;
        }

        public ImageButton getPsaltirListItemDownloaded() {
            return psaltirListItemDownloaded;
        }

        public PsaltirViewHolder(@NonNull View itemView) {
            super(itemView);
            this.psaltirListItem = itemView.findViewById(R.id.prayers_list_item);
            this.psaltirListItemStarBorder = itemView.findViewById(R.id.prayers_list_item_star_border);
            this.psaltirListItemStar = itemView.findViewById(R.id.prayers_list_item_star);
            this.psaltirListItemDownload = itemView.findViewById(R.id.prayers_list_item_download);
            this.psaltirListItemDownloaded = itemView.findViewById(R.id.prayers_list_item_downloaded);
        }

        public void switchDownloadStatus(boolean status){
            // status = true - prayer was downloaded in device
            // status = false - prayer was deleted or not downloaded yet

            if(status){
                psaltirListItemDownload.setVisibility(View.GONE);
                psaltirListItemDownloaded.setVisibility(View.VISIBLE);
            }else {
                psaltirListItemDownloaded.setVisibility(View.GONE);
                psaltirListItemDownload.setVisibility(View.VISIBLE);
            }
        }

        public void switchStarredStatus(boolean status){
            // status = true - prayer was starred
            // status = false - prayer was unstarred

            if(status){
                psaltirListItemStarBorder.setVisibility(View.GONE);
                psaltirListItemStar.setVisibility(View.VISIBLE);
            }else {
                psaltirListItemStar.setVisibility(View.GONE);
                psaltirListItemStarBorder.setVisibility(View.VISIBLE);
            }
        }
    }
}
