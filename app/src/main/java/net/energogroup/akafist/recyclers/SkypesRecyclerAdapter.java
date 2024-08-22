package net.energogroup.akafist.recyclers;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.FragmentKt;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import net.energogroup.akafist.R;
import net.energogroup.akafist.fragments.SkypesFragment;
import net.energogroup.akafist.models.SkypesConfs;

import java.util.ArrayList;
import java.util.List;

/**
 * The RecyclerView adapter class for the {@link SkypesFragment} class
 * @author Nastya Izotina
 * @version 1.0.0
 */
public class SkypesRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private static final int TYPE_ZERO = 0;
    private static final int TYPE_ONE = 1;

    private final List<SkypesConfs> skypesConfs = new ArrayList<>();
    private Fragment fragment;
    private int viewType;

    /**
     * List's constructor
     */
    public SkypesRecyclerAdapter() { }

    public void setFragment(Fragment fragment){
        this.fragment = fragment;
    }

    public void setData(List<SkypesConfs> skypesConfsTemp){
        skypesConfs.clear();
        skypesConfs.addAll(skypesConfsTemp);

        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if (skypesConfs.get(position).isUrl()){
            return TYPE_ZERO;
        }else   {
            return TYPE_ONE;
        }
    }

    /**
     * Creates ViewHolder for skypes
     * @param parent   The ViewGroup into which the new View will be added after it is bound to
     *                 an adapter position.
     * @param viewType The view type of the new View.
     */
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (viewType)  {
            case TYPE_ZERO:
                View itemView1 = inflater.inflate(R.layout.home_blocks_list, parent, false);
                return new SkypesViewUrlHolder(itemView1);
            case TYPE_ONE:
                View itemView2 = inflater.inflate(R.layout.menu_church_list, parent, false);
                return new SkypesViewHolder(itemView2);
            default:
                throw new IllegalArgumentException("Invalid view type");
        }
    }

    /**
     * This method is responsible for the logic that occurs in each element of the Recycler View
     * @param holder List Item
     * @param position Position in the list
     */
    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        switch (holder.getItemViewType())  {
            case TYPE_ZERO:
                SkypesViewUrlHolder skypeUrlVH = (SkypesViewUrlHolder) holder;
                skypeUrlVH.skypeBlockListTitle.setText(skypesConfs.get(position).getName());
                Glide.with(fragment.getContext())
                        .load(getImage("skype_url_img"))
                        .centerInside()
                        .diskCacheStrategy(DiskCacheStrategy.DATA)
                        .into(skypeUrlVH.skypeBlockListImage);
                skypeUrlVH.skypeBlockListImage.setOnClickListener(view -> {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(skypesConfs.get(position).getUrl()));
                    view.getContext().startActivity(intent);
                });
                break;
            case TYPE_ONE:
                SkypesViewHolder skypesVH = (SkypesViewHolder) holder;
                skypesVH.title.setText(skypesConfs.get(position).getName());
                Glide.with(fragment.getContext())
                        .load(getImage("ic_baseline_arrow_right"))
                        .centerInside()
                        .diskCacheStrategy(DiskCacheStrategy.DATA)
                        .into(skypesVH.image);
                skypesVH.image.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                skypesVH.image.setOnClickListener(view -> {
                    Bundle bundle = new Bundle();
                    bundle.putString("nameTitle", skypesConfs.get(position).getName());
                    bundle.putInt("urlId", skypesConfs.get(position).getId());
                    FragmentKt.findNavController(fragment).navigate(R.id.action_skypesFragment_to_skypesBlocksFragment, bundle);
                });
                skypesVH.block.setOnClickListener(view -> {
                    Bundle bundle = new Bundle();
                    bundle.putString("nameTitle", skypesConfs.get(position).getName());
                    bundle.putInt("urlId", skypesConfs.get(position).getId());
                    FragmentKt.findNavController(fragment).navigate(R.id.action_skypesFragment_to_skypesBlocksFragment, bundle);
                });
                break;
        }

    }

    @Override
    public int getItemCount() {
        return skypesConfs.size();
    }

    public int getImage(String imageName) {
        return fragment.getResources().getIdentifier(imageName, "drawable", fragment.getActivity().getPackageName());
    }

    static class SkypesViewUrlHolder extends RecyclerView.ViewHolder{
        public final CardView skypeBlockListView;
        public final ImageButton skypeBlockListImage;
        public final TextView skypeBlockListTitle;
        public final ImageButton skypeBlockListForward;

        public SkypesViewUrlHolder(@NonNull View itemView) {
            super(itemView);
            this.skypeBlockListView = itemView.findViewById(R.id.home_block_list_view);
            this.skypeBlockListImage = itemView.findViewById(R.id.home_block_list_image);
            this.skypeBlockListTitle = itemView.findViewById(R.id.home_block_list_title);
            this.skypeBlockListForward = itemView.findViewById(R.id.home_block_list_forward);
        }
    }

    /**
     * The internal class responsible for the correct display of the RecyclerView element
     */
    static class SkypesViewHolder extends RecyclerView.ViewHolder{

        public final TextView title;
        public final ImageView image;
        public final LinearLayout block;

        public SkypesViewHolder(View itemView) {
            super(itemView);
            this.title = itemView.findViewById(R.id.menu_church_list_title);
            this.image = itemView.findViewById(R.id.menu_church_list_image);
            this.block  = itemView.findViewById(R.id.menu_church_list_block);
        }
    }
}
