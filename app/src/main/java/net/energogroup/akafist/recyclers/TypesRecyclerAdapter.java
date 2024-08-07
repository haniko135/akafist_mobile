package net.energogroup.akafist.recyclers;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import net.energogroup.akafist.viewmodel.ChurchViewModel;
import net.energogroup.akafist.R;
import net.energogroup.akafist.models.TypesModel;

import net.energogroup.akafist.fragments.ChurchFragment;

import java.util.List;

/**
 * The RecyclerView adapter class for the {@link ChurchFragment} class
 * @author Nastya Izotina
 * @version 1.0.0
 */
public class TypesRecyclerAdapter extends RecyclerView.Adapter<TypesRecyclerAdapter.TypesViewHolder>{
    private final List<TypesModel> typesModels;
    private ChurchViewModel churchViewModel;
    private TypesViewHolder prevViewHolder;
    Fragment fragment;

    /**
     * Adapter's constructor
     * @param typesModels list of types
     * @param fragment ChurchFragment context
     */
    public TypesRecyclerAdapter(List<TypesModel> typesModels, Fragment fragment) {
        this.typesModels = typesModels;
        this.fragment = fragment;
    }

    /**
     * Creates ViewHolder for audios
     * @param parent   The ViewGroup into which the new View will be added after it is bound to
     *                 an adapter position.
     * @param viewType The view type of the new View.
     * @return
     */
    @NonNull
    @Override
    public TypesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_horizontal_list, parent, false);
        return new TypesViewHolder(itemView);
    }

    /**
     * This method is responsible for the logic that occurs in each element of the Recycler View
     * @param holder List Item
     * @param position Position in the list
     */
    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull TypesViewHolder holder, int position) {
        ViewModelProvider provider = new ViewModelProvider(fragment);
        churchViewModel = provider.get(ChurchViewModel.class);

        if(prevViewHolder == null && position == 0) {
            churchViewModel.setCurId(typesModels.get(position).getId());
            holder.getHorizontalItem().setTextColor(Color.parseColor("#000000"));
            prevViewHolder = holder;
        }

        holder.getHorizontalItem().setText(typesModels.get(position).getName());
        holder.getHorizontalItem().setOnClickListener(view -> {
            churchViewModel.setCurId(typesModels.get(position).getId());
            prevViewHolder.getHorizontalItem().setTextColor(Color.parseColor("#9A9A9A"));
            holder.getHorizontalItem().setTextColor(Color.parseColor("#000000"));
            prevViewHolder = holder;
            Log.e("AdapterId", String.valueOf(typesModels.get(position).getId()));
            Log.e("AdapterId", String.valueOf(R.color.borderBottom));
            Log.e("AdapterId", String.valueOf(prevViewHolder.horizontalItem.getCurrentTextColor()));
        });
    }

    @Override
    public int getItemCount() {
        return typesModels.size();
    }


    /**
     * The internal class responsible for the correct display of the RecyclerView element
     */
    static class TypesViewHolder extends RecyclerView.ViewHolder{

        private final TextView horizontalItem;

        public TypesViewHolder(@NonNull View itemView) {
            super(itemView);
            this.horizontalItem = itemView.findViewById(R.id.horizontal_item);
        }

        public TextView getHorizontalItem() {
            return horizontalItem;
        }
    }
}
