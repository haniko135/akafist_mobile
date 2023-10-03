package net.energogroup.akafist.recyclers;

import android.annotation.SuppressLint;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
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
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * RecyclerView adapter class with audio files
 * @author Nastya Izotina
 * @version 1.0.0
 */
public class AudioRecyclerAdapter extends RecyclerView.Adapter<AudioRecyclerAdapter.AudioViewHolder> implements Serializable {

    private String urlForLink, filePath;
    private static final int URL_PATTERN_ID = R.string.listenPattern;
    private static final int DELETE_REPEAT_ID = R.string.deleteRepeat;
    private static final int DELETE_FILE_ID = R.string.deleteFile;
    private static final String DEV_TAG = "AudioRecyclerAdapter";
    private LinksFragment fragment;
    private PlayerViewModel playerViewModel;
    private List<LinksModel> audios;
    private List<String> audiosDown;
    private MainActivity mainActivity;
    boolean recIsChecked;

    /**
     * This method updates the lists of downloaded audio and online audio
     * @param audios Список онлайн-аудио
     * @param audiosDownNames Список скачанных аудио
     */
    public void setList(List<LinksModel> audios, List<String> audiosDownNames){
        this.audios = audios;
        this.audiosDown = audiosDownNames;
    }

    /**
     * Constructor for list of audios that downloaded and online
     * @param audios List of audios
     * @param audiosDownNames List of names of audios
     * @param fragment Current fragment
     * @param filePath Path to downloaded file
     */
    public AudioRecyclerAdapter(List<LinksModel> audios, List<String> audiosDownNames, LinksFragment fragment, String filePath){
        this.fragment = fragment;
        this.audios = audios;
        this.audiosDown = audiosDownNames;
        this.filePath = filePath;
        playerViewModel = new ViewModelProvider(fragment.getActivity()).get(PlayerViewModel.class);
    }

    /**
     * Constuctor for list of downloaded audios
     * @param audios List of audios
     * @param fragment
     */
    public AudioRecyclerAdapter(List<LinksModel> audios, LinksFragment fragment){
        this.fragment = fragment;
        this.audios = audios;
        playerViewModel = new ViewModelProvider(fragment.getActivity()).get(PlayerViewModel.class);
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
    public AudioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.audios_list, parent, false);
        return new AudioViewHolder(itemView);
    }


    /**
     * This method is responsible for the logic occurring in each element of the Recycler View
     * @param holder List item
     * @param position Position in the list
     */
    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull AudioViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.audiosListItem.setText(audios.get(position).getName());
        Animation blinkAnimation = AnimationUtils.loadAnimation(fragment.getContext().getApplicationContext(), R.anim.blink);
        holder.audiosListItem.startAnimation(blinkAnimation);

        //checking for the presence in tje list of downloaded files
        if (audiosDown !=null && audiosDown.size() != 0) {
            if (audiosDown.contains(audios.get(position).getName())) {
                Log.e("Download", "here");
                Log.e("Name", audios.get(position).getName());
                holder.isDownload = true;
                holder.audioListItemDown.setVisibility(View.VISIBLE);
                holder.audioListItemDel.setVisibility(View.VISIBLE);

                //delete file
                holder.audioListItemDel.setOnClickListener(v -> {
                    String finalPath = filePath + "/";
                    String fileName = audios.get(position).getName() + ".mp3";
                    Log.e("FINAL_PATH", finalPath + fileName);
                    try {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            Files.delete(Paths.get(finalPath + fileName));
                            MainActivity.generateNotification(DELETE_REPEAT_ID, fragment.getContext());
                        } else {
                            File file = new File(finalPath + fileName);
                            if (file.delete()) {
                                MainActivity.generateNotification(DELETE_FILE_ID, fragment.getContext());
                            } else {
                                MainActivity.generateNotification(DELETE_REPEAT_ID, fragment.getContext());
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.e("ERROR_DELETE", e.getLocalizedMessage());
                    }
                });
            }
        }

        //processing of clicking on a list item
        holder.audiosListItem.setOnClickListener(view -> {
            holder.progressBar.setVisibility(View.VISIBLE);
            playerViewModel.setIsProgressBarActive(true);
            Log.e(DEV_TAG, String.valueOf(view.getId()));
            Log.e(DEV_TAG, String.valueOf(holder.audiosListItem.getId()));

        });

        playerViewModel.getIsProgressBarActive().observe(fragment.getViewLifecycleOwner(), aBoolean -> {
            if(aBoolean){
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
                    playerViewModel.setLinksModel(audios.get(position));

                    String urlPattern = fragment.getResources().getString(URL_PATTERN_ID);

                    //start audio from the phone's memory if it is downloaded
                    if(!holder.isDownload) {
                        playerViewModel.setUrlForAudio(urlPattern + urlForLink + "?alt=media");
                        playerViewModel.setDownload(false);
                    }
                    else{
                        playerViewModel.setUrlForAudio(filePath+"/"+audios.get(position).getName()+".mp3");
                        playerViewModel.setDownload(holder.isDownload);
                    }

                    mainActivity = (MainActivity) fragment.getActivity();
                    mainActivity.binding.mainLayout.playerContainer.setVisibility(View.VISIBLE);
                    playerViewModel.setIsProgressBarActive(false);
                    holder.progressBar.setVisibility(View.INVISIBLE);
                }else {
                    if(playerViewModel.getCurrMediaPlayer().getValue()!=null){
                        playerViewModel.getCurrMediaPlayer().getValue().stop();
                    }

                    playerViewModel.setWorkMode("audioPrayers");
                    playerViewModel.setUrlForAudio(urlForLink);
                    playerViewModel.setLinksModel(audios.get(position));

                    mainActivity = (MainActivity) fragment.getActivity();
                    mainActivity.binding.mainLayout.playerContainer.setVisibility(View.VISIBLE);
                    playerViewModel.setIsProgressBarActive(false);
                    holder.progressBar.setVisibility(View.INVISIBLE);
                }
            }
        });


    }

    @Override
    public int getItemCount() {
        return audios.size();
    }

    /**
     * The internal class responsible for the display of the RecyclerView element
     */
    static class AudioViewHolder extends RecyclerView.ViewHolder{
        public TextView audiosListItem;
        public ImageView audioListItemDown;
        public ImageButton audioListItemDel;
        public ProgressBar progressBar;

        public Boolean isDownload = false;

        public AudioViewHolder(@NonNull View itemView) {
            super(itemView);
            this.audiosListItem = itemView.findViewById(R.id.audio_list_item);
            this.audioListItemDown = itemView.findViewById(R.id.audio_list_item_down);
            this.audioListItemDel = itemView.findViewById(R.id.audio_list_item_del);
            this.progressBar = itemView.findViewById(R.id.audio_progress_bar);
        }
    }

    /**
     * This method checks if the previous audio file is playing
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
