package net.energogroup.akafist.recyclers;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import net.energogroup.akafist.MainActivity;
import net.energogroup.akafist.R;
import net.energogroup.akafist.fragments.LinksFragment;
import net.energogroup.akafist.fragments.PlayerFragment;
import net.energogroup.akafist.models.LinksModel;
import net.energogroup.akafist.viewmodel.PlayerViewModel;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * Класс адаптера RecyclerView с аудиофайлами
 * @author Nastya Izotina
 * @version 1.0.0
 */
public class AudioRecyclerAdapter extends RecyclerView.Adapter<AudioRecyclerAdapter.AudioViewHolder> implements Serializable {

    private String urlForLink, filePath;
    private final String urlPattern = "https://getfile.dokpub.com/yandex/get/";
    private LinksFragment fragment;
    private PlayerViewModel playerViewModel;
    private List<LinksModel> audios;
    private List<String> audiosDown;
    private MainActivity mainActivity;
    boolean recIsChecked;

    /**
     * Этот метод обновляет листы скачанных аудио и онлайн-аудио
     * @param audios Список онлайн-аудио
     * @param audiosDownNames Список скачанных аудио
     */
    public void setList(List<LinksModel> audios, List<String> audiosDownNames){
        this.audios = audios;
        this.audiosDown = audiosDownNames;
    }

    public AudioRecyclerAdapter(List<LinksModel> audios, List<String> audiosDownNames, LinksFragment fragment, String filePath){
        this.fragment = fragment;
        this.audios = audios;
        this.audiosDown = audiosDownNames;
        this.filePath = filePath;
        playerViewModel = new ViewModelProvider(fragment.getActivity()).get(PlayerViewModel.class);
    }

    public AudioRecyclerAdapter(List<LinksModel> audios, LinksFragment fragment){
        this.fragment = fragment;
        this.audios = audios;
        playerViewModel = new ViewModelProvider(fragment.getActivity()).get(PlayerViewModel.class);
    }

    @NonNull
    @Override
    public AudioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.audios_list, parent, false);
        return new AudioViewHolder(itemView);
    }


    /**
     * Этот метод отвечает за логику, происходящую в каждом элементе RecyclerView
     * @param holder Элемент списка
     * @param position Позиция в списке
     */
    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull AudioViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.audiosListItem.setText(audios.get(position).getName());

        //проверка на наличие в списках скачанных
        if (audiosDown !=null && audiosDown.size() != 0) {
            if (audiosDown.contains(audios.get(position).getName())) {
                Log.e("Download", "here");
                Log.e("Name", audios.get(position).getName());
                holder.audioListItemDown.setVisibility(View.VISIBLE);
                holder.audioListItemDel.setVisibility(View.VISIBLE);
                //удаление файла
                holder.audioListItemDel.setOnClickListener(v -> {
                    String finalPath = filePath+"/";
                    String fileName = audios.get(position).getName() + ".mp3";
                    Log.e("FINAL_PATH", finalPath+fileName);
                    try {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            Files.delete(Paths.get(finalPath+fileName));
                            playerViewModel.preNotification("Файл удален. Обновите страницу", fragment.getContext());
                        }else {
                            File file = new File(finalPath+fileName);
                            if (file.delete()) playerViewModel.preNotification("Файл удален", fragment.getContext());
                            else playerViewModel.preNotification("Произошла ошибка при удалении файла. Попробуйте ещё раз", fragment.getContext());
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.e("ERROR_DELETE", e.getLocalizedMessage());
                    }
                });
            }
        }

        //обработка нажатия на элемент списка
        holder.audiosListItem.setOnClickListener(view -> {
            checkPlaying();
            urlForLink = audios.get(position).getUrl();
            playerViewModel.setUrlForLink(urlForLink);
            playerViewModel.setFileName(audios.get(position).getName());
            playerViewModel.setFilePath(filePath);
            MainActivity.networkConnection.observe(fragment.getViewLifecycleOwner(), isChecked->{
                recIsChecked = isChecked;
                /*if (isChecked){

                }else {
                    //fragment.binding.downloadLinkButton.setVisibility(View.GONE);
                }*/
            });

            if(recIsChecked){
                if(playerViewModel.getCurrMediaPlayer().getValue()!=null){
                    playerViewModel.getCurrMediaPlayer().getValue().stop();
                }

                playerViewModel.setWorkMode("audioPrayers");
                playerViewModel.setUrlForAudio(urlPattern + urlForLink + "?alt=media");
                playerViewModel.setLinksModel(audios.get(position));

                mainActivity = (MainActivity) fragment.getActivity();
                mainActivity.binding.mainLayout.playerContainer.setVisibility(View.VISIBLE);

            }else {
                if(playerViewModel.getCurrMediaPlayer().getValue()!=null){
                    playerViewModel.getCurrMediaPlayer().getValue().stop();
                }

                playerViewModel.setWorkMode("audioPrayers");
                playerViewModel.setUrlForAudio(urlForLink);
                playerViewModel.setLinksModel(audios.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return audios.size();
    }

    /**
     * Внутренний класс, отвечающий за правильной отображение элемента RecyclerView
     */
    static class AudioViewHolder extends RecyclerView.ViewHolder{
        public TextView audiosListItem;
        public ImageView audioListItemDown;
        public ImageButton audioListItemDel;

        public AudioViewHolder(@NonNull View itemView) {
            super(itemView);
            this.audiosListItem = itemView.findViewById(R.id.audio_list_item);
            this.audioListItemDown = itemView.findViewById(R.id.audio_list_item_down);
            this.audioListItemDel = itemView.findViewById(R.id.audio_list_item_del);
        }
    }

    /**
     * Этот метод проверяет, играет ли предыдущий аудиофайл
     */
    private void checkPlaying(){
        playerViewModel.getCurrMediaPlayer().observe(fragment.getViewLifecycleOwner(), mediaPlayer1 -> {
            if (mediaPlayer1 != null)
                if(mediaPlayer1.isPlaying()) {
                    PlayerFragment.runnable = null;
                    mediaPlayer1.stop();
                    playerViewModel.setCurrMediaPlayer(mediaPlayer1);
                }
        });

    }
}
