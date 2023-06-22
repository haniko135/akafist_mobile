package net.energogroup.akafist.fragments;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import net.energogroup.akafist.MainActivity;
import net.energogroup.akafist.R;
import net.energogroup.akafist.databinding.FragmentPlayerBinding;
import net.energogroup.akafist.service.NotificationForPlay;
import net.energogroup.akafist.viewmodel.PlayerViewModel;

import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PlayerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PlayerFragment extends Fragment {

    private MediaPlayer mediaPlayer;
    private final Handler handler = new Handler();
    private boolean isPrepared = false;
    private String mode;
    private AppCompatActivity fragActivity;
    private MainActivity mainActivity;

    private PlayerViewModel playerViewModel;
    public static Runnable runnable;
    private FragmentPlayerBinding playerBinding;

    public PlayerFragment() { }

    public static PlayerFragment newInstance() {
        return new PlayerFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("PLAYER_FRAGMENT", "ON_CREATE");
        playerViewModel = new ViewModelProvider(this).get(PlayerViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        playerBinding = FragmentPlayerBinding.inflate(inflater, container, false);
        fragActivity = new AppCompatActivity();
        mainActivity = new MainActivity();

        playerViewModel.getWorkMode().observeForever(s -> {
            try {
                if (mediaPlayer == null && s != null) {
                    Log.e("PLAYER_FRAGMENT", "after 1 check");

                    if (mode.startsWith("audioPrayers")) {
                        String name = playerViewModel.getUrlForAudio().getValue();
                        this.mediaPlayer = MediaPlayer.create(fragActivity, Uri.parse(name));
                        mediaPlayer.setVolume(0.5f, 0.5f);
                        mediaPlayer.setLooping(false);
                        Log.e("PLAYER_FRAGMENT", "after 2 check");
                        isPrepared = true;
                    }

                } else if (mediaPlayer != null && s != null) {
                    if (!mediaPlayer.isPlaying()) {
                        if (isPrepared) {
                            //NotificationForPlay.createNotification(fragActivity, linksModelPlay, android.R.drawable.ic_media_pause);
                            isPrepared = false;
                        }
                        //NotificationForPlay.createNotification(fragActivity, linksModelPlay, android.R.drawable.ic_media_pause);
                        playerBinding.imageButtonPlay.setImageResource(android.R.drawable.ic_media_pause);
                        mainActivity.binding.player.imageButtonPlay.setImageResource(android.R.drawable.ic_media_pause);
                        mediaPlayer.start();
                        Log.d("AUDIO_RECYCLER", "Started in");
                    } else {
                        //NotificationForPlay.createNotification(fragActivity, linksModelPlay, android.R.drawable.ic_media_play);
                        playerBinding.imageButtonPlay.setImageResource(android.R.drawable.ic_media_play);
                        mainActivity.binding.player.imageButtonPlay.setImageResource(android.R.drawable.ic_media_play);
                        mediaPlayer.pause();
                        Log.d("AUDIO_RECYCLER", "Paused in");
                    }
                }

                initSeekBar();
                initButtonClicks();
                playerBinding.textPlayer.setText(getArguments().getString("prayerName"));
                mainActivity.binding.player.textPlayer.setText(getArguments().getString("prayerName"));


                playerBinding.durationBarMolitvy.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                        if (i > 0 && !mediaPlayer.isPlaying()) {
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
            } catch (Exception e){

            }
        });
        return playerBinding.getRoot();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initSeekBar(){
        Log.e("PLAYER_FRAGMENT", "seekbar");
        playerBinding.durationBarMolitvy.setMax(mediaPlayer.getDuration());
        playerBinding.seekBarMaxTime.setText(playerBinding.durationBarMolitvy.getMax());

        playerBinding.durationBarMolitvy.setProgress(0);
        playerBinding.seekBarDurTime.setText(formatDur(mediaPlayer.getCurrentPosition()));


        //анимация движущегося ползунка
        runnable = () -> {
            playerBinding.seekBarDurTime.setText(formatDur(mediaPlayer.getCurrentPosition()));
            playerBinding.durationBarMolitvy.setProgress(mediaPlayer.getCurrentPosition());
            handler.postDelayed(runnable, 1000);
        };
        handler.postDelayed(runnable, 0);

        //контроль ползунка
        playerBinding.durationBarMolitvy.setOnTouchListener((v, event) -> {
            if (mediaPlayer.isPlaying()) {
                SeekBar sb = (SeekBar) v;
                mediaPlayer.seekTo(sb.getProgress());
            }
            return false;
        });
    }


    private void initButtonClicks(){
        Log.e("PLAYER_FRAGMENT", "init buttons");
        playerBinding.imageButtonPlay.setOnClickListener(view -> {
            playAndStop();
        });
        playerBinding.downloadLinkButton.setOnClickListener(view -> {

        });
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

    private void playAndStop(){
        if (!mediaPlayer.isPlaying()) {
            if(isPrepared){
                //NotificationForPlay.createNotification(view.getContext(), linksModelPlay, android.R.drawable.ic_media_pause);
                isPrepared = false;
            }
            //NotificationForPlay.createNotification(view.getContext(), linksModelPlay, android.R.drawable.ic_media_pause);
            playerBinding.imageButtonPlay.setImageResource(android.R.drawable.ic_media_pause);
            mediaPlayer.start();
            Log.d("AUDIO_RECYCLER", "Started in");
        }else {
            //NotificationForPlay.createNotification(view.getContext(), linksModelPlay, android.R.drawable.ic_media_play);
            playerBinding.imageButtonPlay.setImageResource(android.R.drawable.ic_media_play);
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        mediaPlayer.stop();
        //view.getContext().unregisterReceiver(broadcastReceiver);
    }
}