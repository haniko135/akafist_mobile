package net.energogroup.akafist.recyclers;

import android.annotation.SuppressLint;
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
 * Класс адаптера RecyclerView для класса {@link ChurchFragment}
 * @author Nastya Izotina
 * @version 1.0.0
 */
public class TypesRecyclerAdapter extends RecyclerView.Adapter<TypesRecyclerAdapter.TypesViewHolder>{
    private List<TypesModel> typesModels;
    private ChurchViewModel churchViewModel;
    private TypesViewHolder prevViewHolder;
    Fragment fragment;

    public TypesRecyclerAdapter(List<TypesModel> typesModels, Fragment fragment) {
        this.typesModels = typesModels;
        this.fragment = fragment;
    }

    @NonNull
    @Override
    public TypesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_horizontal_list, parent, false);
        return new TypesViewHolder(itemView);
    }

    /**
     * Этот метод отвечает за логику, происходящую в каждом элементе RecyclerView
     * @param holder Элемент списка
     * @param position Позиция в списке
     */
    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull TypesViewHolder holder, int position) {
        ViewModelProvider provider = new ViewModelProvider(fragment);
        churchViewModel = provider.get(ChurchViewModel.class);

        if(prevViewHolder == null && position == 0) {
            churchViewModel.setCurId(typesModels.get(position).getId());
            holder.getHorizontalLine().setVisibility(View.VISIBLE);
            prevViewHolder = holder;
        }

        holder.getHorizontalItem().setText(typesModels.get(position).getName());
        holder.getHorizontalItem().setOnClickListener(view -> {
            churchViewModel.setCurId(typesModels.get(position).getId());
            prevViewHolder.getHorizontalLine().setVisibility(View.INVISIBLE);
            holder.getHorizontalLine().setVisibility(View.VISIBLE);
            prevViewHolder = holder;
            Log.e("AdapterId", String.valueOf(typesModels.get(position).getId()));
        });
    }

    @Override
    public int getItemCount() {
        return typesModels.size();
    }


    /**
     * Внутренний класс, отвечающий за правильной отображение элемента RecyclerView
     */
    static class TypesViewHolder extends RecyclerView.ViewHolder{

        private final TextView horizontalItem;
        private final TextView horizontalLine;

        public TypesViewHolder(@NonNull View itemView) {
            super(itemView);
            this.horizontalItem = itemView.findViewById(R.id.horizontal_item);
            this.horizontalLine = itemView.findViewById(R.id.horizontal_line);
        }

        public TextView getHorizontalItem() {
            return horizontalItem;
        }
        public TextView getHorizontalLine() { return horizontalLine; }
    }
}
