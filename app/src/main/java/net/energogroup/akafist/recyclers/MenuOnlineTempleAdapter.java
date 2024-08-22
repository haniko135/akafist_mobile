package net.energogroup.akafist.recyclers;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.FragmentKt;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import net.energogroup.akafist.R;
import net.energogroup.akafist.models.HomeBlocksModel;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MenuOnlineTempleAdapter extends RecyclerView.Adapter<MenuOnlineTempleAdapter.MenuOnlineTempleVH> {

    private Fragment fr;
    private final List<HomeBlocksModel> onlineTempleList = new ArrayList<>();

    public MenuOnlineTempleAdapter() { }

    public void setData(List<HomeBlocksModel> onlineTempleListTemp){
        onlineTempleList.clear();
        onlineTempleList.addAll(onlineTempleListTemp);

        notifyDataSetChanged();
    }

    public void setFragment(Fragment fr){
        this.fr = fr;
    }

    @NonNull
    @Override
    public MenuOnlineTempleVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.menu_church_list, parent, false);
        return new MenuOnlineTempleVH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MenuOnlineTempleVH holder, int position) {
        //getting tomorrow's date
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        Date tom = calendar.getTime();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", new Locale("ru"));
        String tomorrow = dateFormat.format(tom);

        String titleString = onlineTempleList.get(position).getDateTxt()+" "+onlineTempleList.get(position).getName();
        holder.title.setText(titleString);

        if(onlineTempleList.get(position).getDate() != null) {
            if (onlineTempleList.get(position).getDate().equals("onlineMichael")) {
                setItem(onlineTempleList.get(position),
                        R.string.link_Michael,
                        "Трансляция арх. Михаила",
                        "online_temple_michael",
                        holder);
            }
            if (onlineTempleList.get(position).getDate().equals("onlinePokrovaPls")) {
                setItem(onlineTempleList.get(position),
                        R.string.link_Pokrova_Pls,
                        "Трансляция Покрова Пр. Богородицы",
                        "online_temple_pokrova",
                        holder);
            }
            if (onlineTempleList.get(position).getDate().equals("onlineVarvara")) {
                setItem(onlineTempleList.get(position),
                        R.string.link_Varvara,
                        "Трансляция св. Варвары",
                        "online_temple_varvara",
                        holder);
            }
            if (onlineTempleList.get(position).getDate().equals("molitvyOfflain")){
                setItem(onlineTempleList.get(position),
                        R.id.action_menuOnlineTempleFragment_to_linksFragment,
                        null, "ic_baseline_arrow_right",
                        holder);
                holder.image.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            }
            if (onlineTempleList.get(position).getDate().equals("prayersDownload")){
                setItem(onlineTempleList.get(position),
                        R.id.action_menuOnlineTempleFragment_to_offlinePrayerFragment,
                        null, "ic_baseline_arrow_right",
                        holder);
                holder.image.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            }
            if (onlineTempleList.get(position).getDate().equals("now")){
                holder.title.setText(onlineTempleList.get(position).getDateTxt());
                setItem(onlineTempleList.get(position),
                        R.id.action_menuOnlineTempleFragment_to_churchFragment,
                        null, "ic_baseline_arrow_right",
                        holder);
                holder.image.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            }
            if (onlineTempleList.get(position).getDate().equals(tomorrow) ||
                    onlineTempleList.get(position).getDate().equals("everyday") ||
                    onlineTempleList.get(position).getDate().equals("needs"))
            {
                setItem(onlineTempleList.get(position),
                        R.id.action_menuOnlineTempleFragment_to_churchFragment,
                        null, "ic_baseline_arrow_right",
                        holder);
                holder.image.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            }
        }else{
            holder.title.setText(onlineTempleList.get(position).getName());
            if(onlineTempleList.get(position).getName().startsWith("Псалтирь")){
                setItem(onlineTempleList.get(position),
                        R.id.action_menuOnlineTempleFragment_to_psaltirFragment,
                        null, "ic_baseline_arrow_right",
                        holder);
                holder.image.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            }
        }

        if(onlineTempleList.get(position).getDate() != null) {
            if (onlineTempleList.get(position).getDate().equals("onlineMichael")
                    || onlineTempleList.get(position).getDate().equals("onlineVarvara")
                    || onlineTempleList.get(position).getDate().equals("onlinePokrovaPls")) {
                holder.block.setOnClickListener(view -> {
                    Bundle bundle = new Bundle();
                    bundle.putString("urlToSound", fr.getResources().getString(onlineTempleList.get(position).getLinks()));
                    bundle.putString("soundTitle", onlineTempleList.get(position).getAdditions());
                    FragmentKt.findNavController(fr).navigate(R.id.onlineTempleFragment, bundle);
                });
            } else {
                holder.block.setOnClickListener(view -> {
                    Bundle bundle = new Bundle();
                    bundle.putString("date", onlineTempleList.get(position).getDate());
                    bundle.putString("dateTxt", onlineTempleList.get(position).getDateTxt());
                    FragmentKt.findNavController(fr).navigate(onlineTempleList.get(position).getLinks(), bundle);
                });
            }
        }else if (onlineTempleList.get(position).getName().startsWith("Псалтирь")){
            holder.block.setOnClickListener(view -> {
                Bundle bundle = new Bundle();
                bundle.putInt("id", onlineTempleList.get(position).getId());
                FragmentKt.findNavController(fr).navigate(onlineTempleList.get(position).getLinks(), bundle);
            });
        }
    }

    @Override
    public int getItemCount() {
        return onlineTempleList.size();
    }

    public int getImage(String imageName) {
        return fr.getResources().getIdentifier(imageName, "drawable", fr.getActivity().getPackageName());
    }

    public void setItem(HomeBlocksModel homeBlock, int link, String additions, String imageName, MenuOnlineTempleVH vh){
        homeBlock.setLinks(link);
        homeBlock.setAdditions(additions);
        Glide.with(fr.getContext())
                .load(getImage(imageName))
                .centerInside()
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .into(vh.image);
    }

    static class MenuOnlineTempleVH extends RecyclerView.ViewHolder{
        public final TextView title;
        public final ImageView image;
        public final LinearLayout block;

        public MenuOnlineTempleVH(View itemView) {
            super(itemView);
            this.title = itemView.findViewById(R.id.menu_church_list_title);
            this.image = itemView.findViewById(R.id.menu_church_list_image);
            this.block  = itemView.findViewById(R.id.menu_church_list_block);
        }
    }
}
