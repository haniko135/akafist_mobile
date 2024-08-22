package net.energogroup.akafist.recyclers;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.FragmentKt;
import androidx.recyclerview.widget.RecyclerView;

import net.energogroup.akafist.R;
import net.energogroup.akafist.fragments.Home;
import net.energogroup.akafist.models.HomeBlocksModel;

import java.util.ArrayList;
import java.util.List;

/**
 * The class of the RecyclerView adapter on the "Home" page
 * @author Nastya Izotina
 * @version 1.0.0
 */
public class HomeRecyclerAdapter extends RecyclerView.Adapter<HomeRecyclerAdapter.HomeViewHolder> {
    private final List<HomeBlocksModel> homeBlocksModels = new ArrayList<>();
    private Fragment fragment;

    public HomeRecyclerAdapter( ){ }

    public void setData(List<HomeBlocksModel> homeBlocksModelsT){
        homeBlocksModels.clear();
        homeBlocksModels.addAll(homeBlocksModelsT);
    }

    public void setFragment(Home fragment){
        this.fragment = fragment;
    }

    @NonNull
    @Override
    public HomeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_blocks_list, parent, false);
        return new HomeViewHolder(itemView);
    }

    /**
     * This method is responsible for the logic that occurs in each element of the Recycler View
     * @param holder List item
     * @param position Position in the list
     */
    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull HomeViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.homeBlockListTitle.setText(homeBlocksModels.get(position).getDateTxt());

        //link assignment
        if (homeBlocksModels.get(position).getDate().equals("skypeConfs")){
            homeBlocksModels.get(position).setLinks(R.id.action_home2_to_skypesFragment);
            holder.homeBlockListImage.setImageResource(R.drawable.home_skype_img);
        }
        if (homeBlocksModels.get(position).getDate().equals("menuOnlineTemple")){
            homeBlocksModels.get(position).setLinks(R.id.action_home2_to_menuOnlineTempleFragment);
            holder.homeBlockListImage.setImageResource(R.drawable.home_online_temple_img);
        }
        if (homeBlocksModels.get(position).getDate().equals("links")){
            String title = homeBlocksModels.get(position).getDateTxt()+" "+homeBlocksModels.get(position).getName();
            holder.homeBlockListTitle.setText(title);
            homeBlocksModels.get(position).setLinks(R.id.action_home2_to_linksFragment);
            holder.homeBlockListImage.setImageResource(R.drawable.home_links_img);
        }
        if (homeBlocksModels.get(position).getDate().equals("menuChurch")){
            homeBlocksModels.get(position).setLinks(R.id.action_home2_to_menuOnlineTempleFragment);
            holder.homeBlockListImage.setImageResource(R.drawable.home_menuchurch_img);
        }
        if (homeBlocksModels.get(position).getDate().equals("notes")){
            homeBlocksModels.get(position).setLinks(R.string.link_notes);
            holder.homeBlockListImage.setImageResource(R.drawable.home_notes_img);
        }
        if (homeBlocksModels.get(position).getDate().equals("talks")){
            homeBlocksModels.get(position).setLinks(R.string.link_talks);
            holder.homeBlockListImage.setImageResource(R.drawable.home_talks_img);
        }

        //click on links
        if (homeBlocksModels.get(position).getDate().equals("menuOnlineTemple")
            || homeBlocksModels.get(position).getDate().equals("menuChurch")) {
            holder.homeBlockListImage.setOnClickListener(view -> {
                Bundle bundle = new Bundle();
                bundle.putString("mode", homeBlocksModels.get(position).getDate());
                FragmentKt.findNavController(fragment).navigate(homeBlocksModels.get(position).getLinks(),bundle);
            });
            holder.homeBlockListForward.setOnClickListener(view -> {
                Bundle bundle = new Bundle();
                bundle.putString("mode", homeBlocksModels.get(position).getDate());
                FragmentKt.findNavController(fragment).navigate(homeBlocksModels.get(position).getLinks(),bundle);
            });
        } else if (homeBlocksModels.get(position).getDate().equals("notes")
                || homeBlocksModels.get(position).getDate().equals("talks")) {
            holder.homeBlockListImage.setOnClickListener(view -> {
                Intent toSite = new Intent(Intent.ACTION_VIEW, Uri.parse(fragment.getResources().getString(homeBlocksModels.get(position).getLinks())));
                fragment.getContext().startActivity(toSite);
            });
            holder.homeBlockListForward.setOnClickListener(view -> {
                Intent toSite = new Intent(Intent.ACTION_VIEW, Uri.parse(fragment.getResources().getString(homeBlocksModels.get(position).getLinks())));
                fragment.getContext().startActivity(toSite);
            });
        } else {
            holder.homeBlockListImage.setOnClickListener(view  -> {
                Bundle bundle = new Bundle();
                bundle.putString("date", homeBlocksModels.get(position).getDate());
                bundle.putString("dateTxt", homeBlocksModels.get(position).getDateTxt());
                FragmentKt.findNavController(fragment).navigate(homeBlocksModels.get(position).getLinks(), bundle);
            });
            holder.homeBlockListForward.setOnClickListener(view  ->  {
                Bundle bundle = new Bundle();
                bundle.putString("date", homeBlocksModels.get(position).getDate());
                bundle.putString("dateTxt", homeBlocksModels.get(position).getDateTxt());
                FragmentKt.findNavController(fragment).navigate(homeBlocksModels.get(position).getLinks(), bundle);
            });
        }
    }

    @Override
    public int getItemCount() {
        return homeBlocksModels.size();
    }

    /**
     * The internal class responsible for the correct display of the RecyclerView element
     */
    static class HomeViewHolder extends RecyclerView.ViewHolder{

        private final CardView homeBlockListView;
        private final ImageButton homeBlockListImage;
        private final TextView homeBlockListTitle;
        private final ImageButton homeBlockListForward;

        public HomeViewHolder(@NonNull View itemView) {
            super(itemView);
            this.homeBlockListView = itemView.findViewById(R.id.home_block_list_view);
            this.homeBlockListImage = itemView.findViewById(R.id.home_block_list_image);
            this.homeBlockListTitle = itemView.findViewById(R.id.home_block_list_title);
            this.homeBlockListForward = itemView.findViewById(R.id.home_block_list_forward);
        }
    }
}
