package net.energogroup.akafist.recyclers;

import android.content.Intent;
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

import net.energogroup.akafist.R;
import net.energogroup.akafist.models.psaltir.PsaltirKafismaModel;

import java.util.List;

public class PsaltirRecyclerAdapter extends RecyclerView.Adapter<PsaltirRecyclerAdapter.PsaltirViewHolder> {

    private final List<PsaltirKafismaModel> psaltirKafismaModels;
    private final Fragment fr;
    private final int blockId;

    public PsaltirRecyclerAdapter(List<PsaltirKafismaModel> psaltirKafismaModels, Fragment fr, int block_id) {
        this.psaltirKafismaModels = psaltirKafismaModels;
        this.fr = fr;
        this.blockId = block_id;
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
            if(psaltirKafismaModels.get(position).getDesc().startsWith("https://")){
                Intent toSite = new Intent(Intent.ACTION_VIEW, Uri.parse(psaltirKafismaModels.get(position).getDesc()));
                fr.getContext().startActivity(toSite);
            }else {
                Bundle bundle = new Bundle();
                bundle.putInt("blockId", blockId);
                bundle.putInt("prayerId", psaltirKafismaModels.get(position).getId());
                bundle.putString("mode", "psaltir_read");
                FragmentKt.findNavController(fr).navigate(R.id.action_psaltirFragment_to_prayerFragment, bundle);
            }
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
