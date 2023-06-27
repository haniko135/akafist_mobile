package net.energogroup.akafist.viewmodel;

import android.app.ProgressDialog;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.SeekBar;

import androidx.lifecycle.ViewModel;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import net.energogroup.akafist.R;
import net.energogroup.akafist.fragments.OnlineTempleFragment;
import net.energogroup.akafist.service.background.OnlineTemplePlayer;

/**
 * Класс, содержащий логику обработки данных
 * {@link OnlineTempleFragment} и запуска плеера
 * @author Nastya Izotina
 * @version 1.0.0
 */
public class OnlineTempleViewModel extends ViewModel {
    private static MediaPlayer mediaPlayer = new MediaPlayer();
    private static AudioManager audioManager;
    public static boolean playPause;
    public static boolean initStage = true;
    private static SeekBar volumeBar;
    private static ProgressDialog progressDialog;
    private ImageButton playStopButton;

    public static MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }

    public static ProgressDialog getProgressDialog() {
        return progressDialog;
    }

    public static SeekBar getVolumeBar() {
        return volumeBar;
    }

    public static AudioManager getAudioManager() {
        return audioManager;
    }

    /**
     * Этот метод отвечает за инициализацию плеера
     * в методе {@link OnlineTempleFragment#onCreateView(LayoutInflater, ViewGroup, Bundle)}
     * @param inflater LayoutInflater
     * @param view View
     * @param urlSound String - ссылка на эфир
     */
    public void play(LayoutInflater inflater, View view, String urlSound){
        progressDialog = new ProgressDialog(inflater.getContext());
        audioManager = (AudioManager) inflater.getContext().getSystemService(inflater.getContext().AUDIO_SERVICE);
        int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int curValue = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);

        volumeBar = view.findViewById(R.id.volumeBar);
        volumeBar.setMax(maxVolume);
        volumeBar.setProgress(curValue);
        volumeBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, i, AudioManager.FLAG_SHOW_UI);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        });

        playStopButton = view.findViewById(R.id.stopPlayButton);
        if(!playPause){
            playStopButton.setImageResource(android.R.drawable.ic_media_pause);
            if (initStage){
                progressDialog.setMessage("Загружается...");
                progressDialog.show();

                //начало подгрузки эфира из интернета в фоновом потоке
                Data data = new Data.Builder().putString("URL_SOUND", urlSound).build();
                OneTimeWorkRequest workRequest = new OneTimeWorkRequest.Builder(OnlineTemplePlayer.class)
                        .setInputData(data).build();
                WorkManager.getInstance(inflater.getContext()).enqueue(workRequest);
            }else {
                if (!mediaPlayer.isPlaying()){
                    mediaPlayer.start();
                }
            }
            playPause = true;
        } else{
            playStopButton.setImageResource(android.R.drawable.ic_media_play);
            if (mediaPlayer.isPlaying()){
                mediaPlayer.pause();
            }
            playPause = false;
        }
    }

    /**
     * Этот метод проверяет, играет ли предыдущий трек
     */
    public void checkPlaying(){
        if(mediaPlayer != null) {
            mediaPlayer.stop();
        }
    }
}
