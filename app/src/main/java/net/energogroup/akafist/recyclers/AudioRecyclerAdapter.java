package net.energogroup.akafist.recyclers;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.media.MediaPlayer;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import net.energogroup.akafist.MainActivity;
import net.energogroup.akafist.R;
import net.energogroup.akafist.fragments.LinksFragment;
import net.energogroup.akafist.models.LinksModel;
import net.energogroup.akafist.service.PlayAudios;

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

    private MediaPlayer mediaPlayer;
    public PlayAudios playAudios;
    private String urlForLink;
    private final String urlPattern = "https://getfile.dokpub.com/yandex/get/";

    private LinksFragment fragment;
    private List<LinksModel> audios;
    private List<String> audiosDown;
    private ProgressDialog progressDialog;
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

    public AudioRecyclerAdapter(List<LinksModel> audios, List<String> audiosDownNames, LinksFragment fragment){
        this.fragment = fragment;
        this.audios = audios;
        this.audiosDown = audiosDownNames;
    }

    public AudioRecyclerAdapter(List<LinksModel> audios, LinksFragment fragment){
        this.fragment = fragment;
        this.audios = audios;
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
                    String finalPath = fragment.getFinalPath()+"/";
                    String fileName = audios.get(position).getName() + ".mp3";
                    Log.e("FINAL_PATH", finalPath+fileName);
                    try {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            Files.delete(Paths.get(finalPath+fileName));
                            fragment.preNotification("Файл удален. Обновите страницу");
                        }else {
                            File file = new File(finalPath+fileName);
                            if (file.delete()) fragment.preNotification("Файл удален");
                            else fragment.preNotification("Произошла ошибка при удалении файла. Попробуйте ещё раз");
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
            progressDialog = new ProgressDialog(fragment.getContext());
            checkPlaying();
            urlForLink = audios.get(position).getUrl();
            fragment.urlForLink = urlForLink;
            fragment.fileName = audios.get(position).getName();
            MainActivity.networkConnection.observe(fragment.getViewLifecycleOwner(), isChecked->{
                recIsChecked = isChecked;
                if (isChecked){
                    progressDialog.setMessage("Загружается...");
                    progressDialog.show();
                    fragment.binding.downloadLinkButton.setVisibility(View.VISIBLE);
                }else {
                    fragment.binding.downloadLinkButton.setVisibility(View.GONE);
                }
            });

            if(recIsChecked){
                if (playAudios != null) {
                    playAudios.destroyPlayAudios();
                }

                playAudios = new PlayAudios(urlPattern + urlForLink + "?alt=media", fragment.getContext(),
                        fragment.getView(), audios.get(position));
                mediaPlayer = playAudios.getMediaPlayer();
                playAudios.playAndStop();
                progressDialog.cancel();
            }else {
                if (playAudios != null) {
                    playAudios.destroyPlayAudios();
                }
                playAudios = new PlayAudios(urlForLink, fragment.getContext(),
                        fragment.getView(), audios.get(position));
                mediaPlayer = playAudios.getMediaPlayer();
                playAudios.playAndStop();
                progressDialog.cancel();
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
        if (mediaPlayer != null)
            if(mediaPlayer.isPlaying()) {
                PlayAudios.runnable = null;
                mediaPlayer.stop();
            }
    }
}
