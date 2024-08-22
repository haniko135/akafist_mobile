package net.energogroup.akafist.recyclers;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.FragmentKt;
import androidx.recyclerview.widget.RecyclerView;

import net.energogroup.akafist.MainActivity;
import net.energogroup.akafist.R;
import net.energogroup.akafist.dialogs.DialogTextSize;
import net.energogroup.akafist.fragments.SettingsFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * RecyclerView adapter class for the {@link SettingsFragment} class
 */
public class SettingsRecyclerAdapter extends RecyclerView.Adapter<SettingsRecyclerAdapter.SettingsViewHolder> {

    private final List<String> textMenu = new ArrayList<>();
    private final List<Object> textMenuLinks = new ArrayList<>();
    private Fragment fragment;
    private SharedPreferences preferences;

    /**
     * Constructor method
     */
    public SettingsRecyclerAdapter() {}

    public void setData(List<String> textMenuTemp, List<Object> textMenuLinksTemp){
        textMenu.clear();
        textMenu.addAll(textMenuTemp);

        textMenuLinks.clear();
        textMenuLinks.addAll(textMenuLinksTemp);

        notifyDataSetChanged();
    }

    public void setFragment(Fragment fragment) {
        this.fragment = fragment;
    }

    @NonNull
    @Override
    public SettingsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.menu_blocks_list, parent,false);
        return new SettingsViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull SettingsViewHolder holder, int position) {
        holder.getTextMenu().setText(textMenu.get(position));

        //if(position == 0){
            if(textMenuLinks.get(position) instanceof Integer){
                holder.getTextMenu().setOnClickListener(v -> {
                    preferences = fragment.getActivity().getSharedPreferences(MainActivity.APP_PREFERENCES, Context.MODE_PRIVATE);
                    preferences.edit().remove("app_pref_username").remove("app_pref_email").apply();
                    FragmentKt.findNavController(fragment).navigate((Integer) textMenuLinks.get(position));
                });
            } else if (textMenuLinks.get(position) instanceof String) {
                holder.getTextMenu().setOnClickListener(v -> {
                    DialogTextSize textSize = new DialogTextSize();
                    textSize.show(fragment.requireActivity().getSupportFragmentManager(), textMenuLinks.get(position).toString());
                });
            }
    }

    @Override
    public int getItemCount() {
        return textMenu.size();
    }

    static class SettingsViewHolder extends RecyclerView.ViewHolder{

        private final TextView textMenu;

        public TextView getTextMenu() { return textMenu; }

        public SettingsViewHolder(@NonNull View itemView) {
            super(itemView);
            this.textMenu = itemView.findViewById(R.id.menu_list_block);
        }
    }
}
