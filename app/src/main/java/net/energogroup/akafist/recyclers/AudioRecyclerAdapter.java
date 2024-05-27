package net.energogroup.akafist.recyclers;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import net.energogroup.akafist.MainActivity;
import net.energogroup.akafist.R;
import net.energogroup.akafist.db.StarredDTO;
import net.energogroup.akafist.fragments.LinksFragment;
import net.energogroup.akafist.fragments.PlayerFragment;
import net.energogroup.akafist.models.LinksModel;
import net.energogroup.akafist.viewmodel.LinksViewModel;
import net.energogroup.akafist.viewmodel.PlayerViewModel;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

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
    private Fragment fragment;
    private PlayerViewModel playerViewModel;
    private LinksViewModel linksViewModel;
    private SQLiteDatabase db;
    private List<LinksModel> audios;
    private List<String> audiosDown;
    private MainActivity mainActivity;
    private String date;
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
    public AudioRecyclerAdapter(List<LinksModel> audios, List<String> audiosDownNames,
                                Fragment fragment, String filePath, String date){
        this.fragment = fragment;
        this.audios = audios;
        this.audiosDown = audiosDownNames;
        this.filePath = filePath;
        this.date = date;
        playerViewModel = new ViewModelProvider(fragment.getActivity()).get(PlayerViewModel.class);
        linksViewModel = new ViewModelProvider(fragment.getActivity()).get(LinksViewModel.class);

        mainActivity = (MainActivity) fragment.getActivity();
        db = mainActivity.getDbHelper().getWritableDatabase();
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

        Cursor cursorPrayer = db.rawQuery("SELECT * FROM " + StarredDTO.TABLE_NAME + " WHERE "
                + StarredDTO.COLUMN_NAME_OBJECT_URL + "='" + audios.get(position).getUrl()+"'", null);
        if(cursorPrayer.moveToFirst()){
            holder.audioListItemStarBorder.setVisibility(View.GONE);
            holder.audioListItemStar.setVisibility(View.VISIBLE);
        }else {
            holder.audioListItemStar.setVisibility(View.GONE);
            holder.audioListItemStarBorder.setVisibility(View.VISIBLE);
        }
        cursorPrayer.close();

        holder.audioListItemStarBorder.setOnClickListener(view -> {
            ContentValues contentValues = new ContentValues();
            contentValues.put(StarredDTO.COLUMN_NAME_OBJECT_URL, audios.get(position).getUrl());
            contentValues.put(StarredDTO.COLUMN_NAME_OBJECT_TYPE, date);
            contentValues.put(StarredDTO.COLUMN_NAME_ID, Math.round(Math.random()*1000));

            db.insert(StarredDTO.TABLE_NAME,null, contentValues);

            holder.audioListItemStarBorder.setVisibility(View.GONE);
            holder.audioListItemStar.setVisibility(View.VISIBLE);
        });

        holder.audioListItemStar.setOnClickListener(view -> {
            String[] selectionArgs = { audios.get(position).getUrl() };

            db.delete(StarredDTO.TABLE_NAME,StarredDTO.COLUMN_NAME_OBJECT_URL + " LIKE ?",
                    selectionArgs);

            holder.audioListItemStar.setVisibility(View.GONE);
            holder.audioListItemStarBorder.setVisibility(View.VISIBLE);
        });

        //checking for the presence in the list of downloaded files
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
            //holder.progressBar.setVisibility(View.VISIBLE);
            Log.e(DEV_TAG, String.valueOf(holder.audiosListItem.getId()));

            Log.e("AUDIO_RECYCLER", audios.get(position).getName()+": "+audios.get(position).getUrl());

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
                //holder.progressBar.setVisibility(View.INVISIBLE);
            }else {
                if(playerViewModel.getCurrMediaPlayer().getValue()!=null){
                    playerViewModel.getCurrMediaPlayer().getValue().stop();
                }

                playerViewModel.setWorkMode("audioPrayers");
                playerViewModel.setUrlForAudio(urlForLink);
                playerViewModel.setLinksModel(audios.get(position));

                mainActivity = (MainActivity) fragment.getActivity();
                mainActivity.binding.mainLayout.playerContainer.setVisibility(View.VISIBLE);
                //holder.progressBar.setVisibility(View.INVISIBLE);
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
        public ImageButton audioListItemStar;
        public ImageButton audioListItemStarBorder;
        public ProgressBar progressBar;
        public Boolean isDownload = false;

        public AudioViewHolder(@NonNull View itemView) {
            super(itemView);
            this.audiosListItem = itemView.findViewById(R.id.audio_list_item);
            this.audioListItemDown = itemView.findViewById(R.id.audio_list_item_down);
            this.audioListItemDel = itemView.findViewById(R.id.audio_list_item_del);
            this.audioListItemStar = itemView.findViewById(R.id.audio_list_item_star);
            this.audioListItemStarBorder = itemView.findViewById(R.id.audio_list_item_star_border);
            //this.progressBar = itemView.findViewById(R.id.audio_progress_bar);
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
