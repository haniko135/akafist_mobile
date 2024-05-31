package net.energogroup.akafist.recyclers;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.FragmentKt;
import androidx.recyclerview.widget.RecyclerView;

import net.energogroup.akafist.R;
import net.energogroup.akafist.fragments.Home;
import net.energogroup.akafist.models.HomeBlocksModel;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * The class of the RecyclerView adapter on the "Home" page
 * @author Nastya Izotina
 * @version 1.0.0
 */
public class HomeRecyclerAdapter extends RecyclerView.Adapter<HomeRecyclerAdapter.HomeViewHolder> {
    private final List<HomeBlocksModel> homeBlocksModels;
    private final Fragment fragment;

    public HomeRecyclerAdapter(List<HomeBlocksModel> homeBlocksModels, Home fragment) {
        this.homeBlocksModels = homeBlocksModels;
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
        //getting tomorrow's date
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        Date tom = calendar.getTime();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", new Locale("ru"));
        String tomorrow = dateFormat.format(tom);

        holder.getHomeBlockTextUp().setText(homeBlocksModels.get(position).getDateTxt());
        holder.getHomeBlockTextDown().setText(homeBlocksModels.get(position).getName());

        if(homeBlocksModels.get(position).getDate() != null){
            holder.getHomeBlockTextUp().setText(homeBlocksModels.get(position).getDateTxt());
            holder.getHomeBlockTextDown().setText(homeBlocksModels.get(position).getName());
            //link assignment
            if (homeBlocksModels.get(position).getDate().equals("skypeConfs")){
                homeBlocksModels.get(position).setLinks(R.id.action_home2_to_skypesFragment);
            }
            if (homeBlocksModels.get(position).getDate().equals("onlineMichael")){
                homeBlocksModels.get(position).setLinks(R.string.link_Michael);
                homeBlocksModels.get(position).setAdditions("Трансляция арх. Михаила");
            }
            if (homeBlocksModels.get(position).getDate().equals("onlinePokrovaPls")){
                homeBlocksModels.get(position).setLinks(R.string.link_Pokrova_Pls);
                homeBlocksModels.get(position).setAdditions("Трансляция Покрова Пр. Богородицы");
            }
            if (homeBlocksModels.get(position).getDate().equals("onlineVarvara")){
                homeBlocksModels.get(position).setLinks(R.string.link_Varvara);
                homeBlocksModels.get(position).setAdditions("Трансляция св. Варвары");
            }
            if (homeBlocksModels.get(position).getDate().equals("molitvyOfflain")){
                homeBlocksModels.get(position).setLinks(R.id.action_home2_to_linksFragment);
            }
            if (homeBlocksModels.get(position).getDate().equals("links")){
                homeBlocksModels.get(position).setLinks(R.id.action_home2_to_linksFragment);
            }
            if (homeBlocksModels.get(position).getDate().equals("notes")){
                homeBlocksModels.get(position).setLinks(R.string.link_notes);
            }
            if (homeBlocksModels.get(position).getDate().equals("talks")){
                homeBlocksModels.get(position).setLinks(R.string.link_talks);
            }
            if (homeBlocksModels.get(position).getDate().equals("now")){
                homeBlocksModels.get(position).setLinks(R.id.action_home2_to_churchFragment);
            }
            if (homeBlocksModels.get(position).getDate().equals(tomorrow)){
                homeBlocksModels.get(position).setLinks(R.id.action_home2_to_churchFragment);
            }
            if (homeBlocksModels.get(position).getDate().equals("everyday")){
                homeBlocksModels.get(position).setLinks(R.id.action_home2_to_churchFragment);
            }
            if(homeBlocksModels.get(position).getDate().equals("needs")){
                homeBlocksModels.get(position).setLinks(R.id.action_home2_to_churchFragment);
            }
        }
        else{
            holder.getHomeBlockTextUp().setVisibility(View.GONE);
            holder.getHomeBlockTextDown().setText(homeBlocksModels.get(position).getName());
            holder.getHomeBlockline().setVisibility(View.GONE);
            if(homeBlocksModels.get(position).getName().startsWith("Псалтирь")){
                homeBlocksModels.get(position).setLinks(R.id.action_home2_to_psaltirFragment);
            }
        }

        if(homeBlocksModels.get(position).getDate() != null) {
            //click on links
            if (homeBlocksModels.get(position).getDate().equals("onlineMichael")
                    || homeBlocksModels.get(position).getDate().equals("onlineVarvara")
                    || homeBlocksModels.get(position).getDate().equals("onlinePokrovaPls")) {
                holder.getHomeBlockLinear().setOnClickListener(view -> {
                    Bundle bundle = new Bundle();
                    bundle.putString("urlToSound", fragment.getResources().getString(homeBlocksModels.get(position).getLinks()));
                    bundle.putString("soundTitle", homeBlocksModels.get(position).getAdditions());
                    FragmentKt.findNavController(fragment).navigate(R.id.onlineTempleFragment, bundle);
                });
            } else if (homeBlocksModels.get(position).getDate().equals("notes")
                    || homeBlocksModels.get(position).getDate().equals("talks")) {
                holder.getHomeBlockLinear().setOnClickListener(view -> {
                    Intent toSite = new Intent(Intent.ACTION_VIEW, Uri.parse(fragment.getResources().getString(homeBlocksModels.get(position).getLinks())));
                    fragment.getContext().startActivity(toSite);
                });
            } else {
                holder.getHomeBlockLinear().setOnClickListener(view -> {
                    Bundle bundle = new Bundle();
                    bundle.putString("date", homeBlocksModels.get(position).getDate());
                    bundle.putString("dateTxt", homeBlocksModels.get(position).getDateTxt());
                    FragmentKt.findNavController(fragment).navigate(homeBlocksModels.get(position).getLinks(), bundle);
                });
            }
        } else if (homeBlocksModels.get(position).getName().startsWith("Псалтирь")){
            holder.getHomeBlockLinear().setOnClickListener(view -> {
                Bundle bundle = new Bundle();
                bundle.putInt("id", homeBlocksModels.get(position).getId());
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

        private final LinearLayout homeBlockLinear;
        private final TextView homeBlockTextUp;
        private final TextView homeBlockTextDown;
        private final TextView homeBlockline;

        public HomeViewHolder(@NonNull View itemView) {
            super(itemView);
            this.homeBlockLinear = itemView.findViewById(R.id.home_block_list);
            this.homeBlockTextUp = itemView.findViewById(R.id.home_block_text_up);
            this.homeBlockTextDown = itemView.findViewById(R.id.home_block_text_down);
            this.homeBlockline = itemView.findViewById(R.id.home_block_line);
        }

        public LinearLayout getHomeBlockLinear() {
            return homeBlockLinear;
        }

        public TextView getHomeBlockTextUp() {
            return homeBlockTextUp;
        }

        public TextView getHomeBlockTextDown() {
            return homeBlockTextDown;
        }

        public TextView getHomeBlockline() { return homeBlockline; }
    }
}
