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
import net.energogroup.akafist.dialogs.DialogContactDeveloper;
import net.energogroup.akafist.dialogs.DialogTextSize;
import net.energogroup.akafist.fragments.SettingsFragment;

import java.util.List;

/**
 * RecyclerView adapter class for the {@link SettingsFragment} class
 */
public class SettingsRecyclerAdapter extends RecyclerView.Adapter<SettingsRecyclerAdapter.SettingsViewHolder> {

    private List<String> textMenu;
    private Fragment fragment;
    private SharedPreferences preferences;

    /**
     * Constructor method
     * @param textMenu Strings of current menu
     * @param fragment Settings fragment
     */
    public SettingsRecyclerAdapter(List<String> textMenu, SettingsFragment fragment) {
        this.textMenu = textMenu;
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

        if(position == 0){
            holder.getTextMenu().setOnClickListener(v -> {
                preferences = fragment.getActivity().getSharedPreferences(MainActivity.APP_PREFERENCES, Context.MODE_PRIVATE);
                preferences.edit().remove("app_pref_username").remove("app_pref_email").apply();
                FragmentKt.findNavController(fragment).navigate(R.id.action_settingsFragment_to_loginFragment);
            });
        } else if (position == 1){
            holder.getTextMenu().setOnClickListener(v -> {
                DialogTextSize textSize = new DialogTextSize();
                textSize.show(fragment.requireActivity().getSupportFragmentManager(), "userTextSizeDialog");
            });
        } else if (position == 2) {
            holder.getTextMenu().setOnClickListener(v -> {
                DialogContactDeveloper contactDeveloper = new DialogContactDeveloper();
                contactDeveloper.show(fragment.requireActivity().getSupportFragmentManager(), "contactDeveloper");
            });
        }

        /*if(position == 0){
            holder.getTextMenu().setOnClickListener(v -> {
                DialogTextSize textSize = new DialogTextSize();
                textSize.show(fragment.requireActivity().getSupportFragmentManager(), "userTextSizeDialog");
            });
        }*/
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
