package net.energogroup.akafist.service;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import net.energogroup.akafist.R;
import net.energogroup.akafist.models.LinksModel;
import net.energogroup.akafist.recyclers.AudioRecyclerAdapter;

import java.io.Serializable;
import java.util.Objects;

/**
 * Класс для воспроизведения аудиофайлов
 * @author Nastya Izotina
 * @version 1.0.0
 */
public class PlayAudios implements Serializable {

    private MediaPlayer mediaPlayer;
    private final SeekBar seekBar;
    private final ImageButton playStopButton;
    private final TextView seekBarHint;
    private final Handler handler = new Handler();
    private final View view;
    private boolean isPrepared = false;
    private LinksModel linksModelPlay = new LinksModel();
    public static Runnable runnable;

    /**
     * @return Возвращает текущее состояние MediaPlayer
     */
    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }

    public void setLinksModelPlay(LinksModel linksModelPlay) {
        this.linksModelPlay.setId(linksModelPlay.getId());
        this.linksModelPlay.setName(linksModelPlay.getName());
        this.linksModelPlay.setUrl(linksModelPlay.getUrl());
        this.linksModelPlay.setImage(linksModelPlay.getImage());
    }

    /**
     * Конструктор класса PlayAudios, где объявляются основные параметры медиаплеера и сопуствующих
     * элементов. Есть в классе {@link AudioRecyclerAdapter}
     * @param name String - Ссылка на аудиофайл
     * @param context Context
     * @param view View
     */
    @SuppressLint("ClickableViewAccessibility")
    public PlayAudios(String name, Context context, View view, LinksModel linksModel){
        this.view = view;
        setLinksModelPlay(linksModel);

        this.mediaPlayer = MediaPlayer.create(context, Uri.parse(name));
        mediaPlayer.setVolume(0.5f, 0.5f);
        mediaPlayer.setLooping(false);

        view.findViewById(R.id.durationPlayer).setVisibility(View.VISIBLE);
        view.findViewById(R.id.playBarPlayer).setVisibility(View.VISIBLE);
        seekBarHint = view.findViewById(R.id.seekBarDurTime);
        TextView seekBarMax = view.findViewById(R.id.seekBarMaxTime);
        seekBarMax.setVisibility(View.VISIBLE);
        seekBarHint.setVisibility(View.VISIBLE);
        playStopButton = view.findViewById(R.id.imageButtonPlay);
        TextView textPlayer = view.findViewById(R.id.text_player);

        CharSequence text = linksModel.getName();
        textPlayer.setVisibility(View.VISIBLE);
        textPlayer.setText(text);

        seekBar = view.findViewById(R.id.durationBarMolitvy);
        seekBar.setMax(mediaPlayer.getDuration());
        seekBarMax.setText(formatDur(seekBar.getMax()));
        seekBar.setProgress(0);
        seekBarHint.setText(formatDur(mediaPlayer.getCurrentPosition()));

        //анимация движущегося ползунка
        runnable = () -> {
            seekBarHint.setText(formatDur(mediaPlayer.getCurrentPosition()));
            seekBar.setProgress(mediaPlayer.getCurrentPosition());
            handler.postDelayed(runnable, 1000);
        };
        handler.postDelayed(runnable, 0);


        //контроль ползунка
        seekBar.setOnTouchListener((v, event) -> {
            if(mediaPlayer.isPlaying()){
                SeekBar sb = (SeekBar)v;
                mediaPlayer.seekTo(sb.getProgress());
            }
            return false;
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if(i > 0 && !mediaPlayer.isPlaying()){
                    mediaPlayer.seekTo(seekBar.getProgress());
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    mediaPlayer.seekTo(seekBar.getProgress());
                }
            }
        });

        isPrepared = true;
        view.getContext().registerReceiver(broadcastReceiver, new IntentFilter("AUDIOS"));
        view.getContext().startService(new Intent(view.getContext(), OnClearFromRecentService.class));
    }

    /**
     * Этот метод форматирует время
     * @param i - секунда
     * @return Возвращает отформатированное время
     */
    public String formatDur(int i){
        int x = (int) Math.ceil(i / 1000f);
        String fin;

        if (x < 10)
            fin = "0:0" + x;
        else if(x > 10 && x < 60)
            fin = "0:" + x;
        else {
            int min = x / 60, sec = x % 60;
            if (min < 10)
                if(sec < 10)
                    fin = "0"+ min + ":0" + sec;
                else
                    fin = "0"+ min + ":" + sec;
            else
                if(sec < 10)
                    fin = min + ":0" + sec;
                else
                    fin = min + ":" + sec;

        }
        return fin;
    }

    /**
     * Этот метод контролирует кнопку воспроизведения в плеере
     */
    public void playAndStop(){
        if (!mediaPlayer.isPlaying()) {
            if(isPrepared){
                NotificationForPlay.createNotification(view.getContext(), linksModelPlay, android.R.drawable.ic_media_pause);
                isPrepared = false;
            }
            NotificationForPlay.createNotification(view.getContext(), linksModelPlay, android.R.drawable.ic_media_pause);
            playStopButton.setImageResource(android.R.drawable.ic_media_pause);
            mediaPlayer.start();
            Log.d("AUDIO_RECYCLER", "Started in");
        }else {
            NotificationForPlay.createNotification(view.getContext(), linksModelPlay, android.R.drawable.ic_media_play);
            playStopButton.setImageResource(android.R.drawable.ic_media_play);
            mediaPlayer.pause();
            Log.d("AUDIO_RECYCLER", "Paused in");
        }
    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getExtras().getString("actionName");

            if (Objects.equals(action, NotificationForPlay.ACTION_PLAY)){
                if (mediaPlayer.isPlaying()){
                    playAndStop();
                    //NotificationForPlay.createNotification(view.getContext(), linksModelPlay, android.R.drawable.ic_media_play);
                }else {
                    playAndStop();
                    //NotificationForPlay.createNotification(view.getContext(), linksModelPlay, android.R.drawable.ic_media_pause);
                }
            }
        }
    };

    /**
     * Этот метод уничтожает проигрывание аудиофайла
     */
    public void destroyPlayAudios() {
        mediaPlayer.stop();
        view.getContext().unregisterReceiver(broadcastReceiver);
    }

}
