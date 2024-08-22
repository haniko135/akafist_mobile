package net.energogroup.akafist.recyclers;

import android.annotation.SuppressLint;
import android.graphics.Color;
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

import java.util.ArrayList;
import java.util.List;

/**
 * The RecyclerView adapter class for the {@link ChurchFragment} class
 * @author Nastya Izotina
 * @version 1.0.0
 */
public class TypesRecyclerAdapter extends RecyclerView.Adapter<TypesRecyclerAdapter.TypesViewHolder>{
    private List<TypesModel> typesModels = new ArrayList<>();
    private ChurchViewModel churchViewModel;
    private TypesViewHolder prevViewHolder;
    private Fragment fragment;

    /**
     * The Adapter constructor
     */
    public TypesRecyclerAdapter() { }

    public static TypesRecyclerAdapter.Builder newBuilder() {
        return new TypesRecyclerAdapter().new Builder();
    }

    /**
     * Creates ViewHolder for audios
     * @param parent   The ViewGroup into which the new View will be added after it is bound to
     *                 an adapter position.
     * @param viewType The view type of the new View.
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

    public class Builder{
        private Builder(){ }


        public Builder setData(List<TypesModel> typesModels) {
            TypesRecyclerAdapter.this.typesModels = typesModels;
            return this;
        }

        public Builder setFragment(Fragment fragment) {
            TypesRecyclerAdapter.this.fragment = fragment;
            return this;
        }

        public Builder setChurchViewModel() {
            TypesRecyclerAdapter.this.churchViewModel = new ViewModelProvider(TypesRecyclerAdapter.this.fragment).get(ChurchViewModel.class);
            return this;
        }

        public TypesRecyclerAdapter build() {
            return TypesRecyclerAdapter.this;
        }
    }
}