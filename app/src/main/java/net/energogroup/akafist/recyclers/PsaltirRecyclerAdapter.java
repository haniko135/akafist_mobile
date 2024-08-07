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
import net.energogroup.akafist.db.StarredDTO;
import net.energogroup.akafist.models.psaltir.PsaltirKafismaModel;

import java.util.List;

public class PsaltirRecyclerAdapter extends RecyclerView.Adapter<PsaltirRecyclerAdapter.PsaltirViewHolder> {

    private final List<PsaltirKafismaModel> psaltirKafismaModels;
    private final Fragment fr;
    private final int blockId;
    private MainActivity mainActivity;
    private final SQLiteDatabase db;

    public PsaltirRecyclerAdapter(List<PsaltirKafismaModel> psaltirKafismaModels, Fragment fr, int block_id) {
        this.psaltirKafismaModels = psaltirKafismaModels;
        this.fr = fr;
        this.blockId = block_id;

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


        Cursor cursorPrayer = db.rawQuery("SELECT * FROM " + StarredDTO.TABLE_NAME + " WHERE "
                        + StarredDTO.COLUMN_NAME_OBJECT_URL + "='" +
                        "psaltir/"+psaltirKafismaModels.get(position).getName()+"/"+psaltirKafismaModels.get(position).getId()+"'",
                null);
        if(cursorPrayer.moveToFirst()){
            holder.getPsaltirListItemStarBorder().setVisibility(View.GONE);
            holder.getPsaltirListItemStar().setVisibility(View.VISIBLE);
        }else {
            holder.getPsaltirListItemStar().setVisibility(View.GONE);
            holder.getPsaltirListItemStarBorder().setVisibility(View.VISIBLE);
        }
        cursorPrayer.close();



        holder.getPsaltirListItemStarBorder().setOnClickListener(view -> {
            ContentValues contentValues = new ContentValues();
            contentValues.put(StarredDTO.COLUMN_NAME_OBJECT_URL, "psaltir/"+psaltirKafismaModels.get(position).getName()+"/"+psaltirKafismaModels.get(position).getId());
            contentValues.put(StarredDTO.COLUMN_NAME_OBJECT_TYPE, "text-prayers");
            contentValues.put(StarredDTO.COLUMN_NAME_ID, Math.round(Math.random()*10000));

            db.insert(StarredDTO.TABLE_NAME,null, contentValues);

            holder.psaltirListItemStarBorder.setVisibility(View.GONE);
            holder.psaltirListItemStar.setVisibility(View.VISIBLE);
        });

        holder.getPsaltirListItemStar().setOnClickListener(view -> {
            String[] selectionArgs = { "psaltir/"+psaltirKafismaModels.get(position).getName()+"/"+psaltirKafismaModels.get(position).getId() };
            db.delete(StarredDTO.TABLE_NAME,StarredDTO.COLUMN_NAME_OBJECT_URL + " LIKE ?",
                    selectionArgs);

            holder.psaltirListItemStar.setVisibility(View.GONE);
            holder.psaltirListItemStarBorder.setVisibility(View.VISIBLE);
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

        public TextView getPsaltirListItem() { return psaltirListItem; }

        public ImageButton getPsaltirListItemStarBorder() { return psaltirListItemStarBorder; }

        public ImageButton getPsaltirListItemStar() { return psaltirListItemStar; }

        public PsaltirViewHolder(@NonNull View itemView) {
            super(itemView);
            this.psaltirListItem = itemView.findViewById(R.id.prayers_list_item);
            this.psaltirListItemStarBorder = itemView.findViewById(R.id.prayers_list_item_star_border);
            this.psaltirListItemStar = itemView.findViewById(R.id.prayers_list_item_star);
        }
    }
}
