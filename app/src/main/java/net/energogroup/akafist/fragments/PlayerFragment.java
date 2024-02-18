package net.energogroup.akafist.fragments;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import net.energogroup.akafist.MainActivity;
import net.energogroup.akafist.R;
import net.energogroup.akafist.databinding.FragmentPlayerBinding;
import net.energogroup.akafist.service.notification.NotificationForPlay;
import net.energogroup.akafist.service.notification.OnClearFromRecentService;
import net.energogroup.akafist.viewmodel.PlayerViewModel;

import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PlayerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PlayerFragment extends Fragment {

    private MediaPlayer mediaPlayer;
    private SharedPreferences appPref;
    private final Handler handler = new Handler();
    private boolean isPrepared = false;
    private PlayerViewModel playerViewModel;
    private static final int START_DOWNLOAD_ID = R.string.startDownload;
    private static final String DEV_LOG = "PlayerFragment";
    public static Runnable runnable;
    private FragmentPlayerBinding playerBinding;


    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getExtras().getString("actionName");

            if (Objects.equals(action, NotificationForPlay.ACTION_PLAY)){
                if (mediaPlayer.isPlaying()){
                    playAndStop();
                    NotificationForPlay.createNotification(getActivity(),
                            playerViewModel.getLinksModel().getValue(),
                            android.R.drawable.ic_media_play);
                }else {
                    playAndStop();
                    NotificationForPlay.createNotification(getActivity(),
                            playerViewModel.getLinksModel().getValue(),
                            android.R.drawable.ic_media_pause);
                }
            }
        }
    };

    public PlayerFragment() { }

    public static PlayerFragment newInstance() {
        return new PlayerFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("PLAYER_FRAGMENT", "ON_CREATE");
        playerViewModel = new ViewModelProvider(getActivity()).get(PlayerViewModel.class);
        playerViewModel.setWorkMode("background");
        appPref = getActivity().getSharedPreferences(MainActivity.APP_PREFERENCES, Context.MODE_PRIVATE);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        playerBinding = FragmentPlayerBinding.inflate(inflater, container, false);

        playerViewModel.getWorkMode().observe(getViewLifecycleOwner(), s -> {
            playerViewModel.setIsInitialized(false);
            try {
                if (mediaPlayer == null && s != null) {
                    if (s.startsWith("audioPrayers")) {
                        Log.e(DEV_LOG, "inPlayer");
                        playerViewModel.getUrlForAudio().observe(getViewLifecycleOwner(), s1 -> {
                            String name = playerViewModel.getUrlForAudio().getValue();
                            Log.e(DEV_LOG, name);
                            this.mediaPlayer = MediaPlayer.create(getActivity(), Uri.parse(name));
                            mediaPlayer.setVolume(0.5f, 0.5f);
                            mediaPlayer.setLooping(false);
                            isPrepared = true;
                            playerViewModel.setCurrMediaPlayer(mediaPlayer);
                        });
                    }

                } else if (mediaPlayer != null) {
                    playerViewModel.getLinksModel().observe(getViewLifecycleOwner(), linksModel -> {
                        if (!mediaPlayer.isPlaying()) {
                            NotificationForPlay.createNotification(getActivity(),
                                    playerViewModel.getLinksModel().getValue(),
                                    android.R.drawable.ic_media_pause);
                            playerBinding.imageButtonPlay.
                                    setImageResource(R.drawable.baseline_play_arrow_24);
                            mediaPlayer.start();
                        } else {
                            NotificationForPlay.createNotification(getActivity(),
                                    playerViewModel.getLinksModel().getValue(),
                                    android.R.drawable.ic_media_play);
                            playerBinding.imageButtonPlay.
                                    setImageResource(R.drawable.baseline_pause_24);
                            mediaPlayer.pause();
                        }
                    });
                }

                playerViewModel.getUrlForAudio().observe(getViewLifecycleOwner(), s1 -> {
                    if(!s.startsWith("background") && mediaPlayer != null) {
                        initSeekBar();
                        initButtonClicks(container);
                        playerViewModel.getLinksModel().observe(getViewLifecycleOwner(), linksModel -> playerBinding.textPlayer.setText(linksModel.getName()));

                        playerViewModel.getIsDownload().observe(getViewLifecycleOwner(), aBoolean -> {
                            if (aBoolean){
                                playerBinding.downloadLinkButton.setVisibility(View.INVISIBLE);
                            }else {
                                playerBinding.downloadLinkButton.setVisibility(View.VISIBLE);
                            }
                        });


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

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            getActivity().registerReceiver(broadcastReceiver, new IntentFilter("AUDIOS"), Context.RECEIVER_EXPORTED);
                        }else {
                            getActivity().registerReceiver(broadcastReceiver, new IntentFilter("AUDIOS"));
                        }
                        getActivity().startService(new Intent(getContext(), OnClearFromRecentService.class));
                        playerViewModel.setIsInitialized(true);
                    }
                });

            } catch (Exception e){
                Log.e("PLAYER_FRAGMENT", "ERROR_E:"+e.getLocalizedMessage());
            }
        });
        return playerBinding.getRoot();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initSeekBar(){
        Log.e("PLAYER_FRAGMENT", "seekbar");
        playerBinding.durationBarMolitvy.setMax(mediaPlayer.getDuration());
        playerBinding.seekBarMaxTime.setText(formatDur(playerBinding.durationBarMolitvy.getMax()));

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


    public void initButtonClicks(ViewGroup container){
        appPref.edit().putBoolean("app_pref_player", true).apply();

        Log.e("PLAYER_FRAGMENT", "init buttons");
        playerBinding.imageButtonPlay.setOnClickListener(view -> playAndStop());
        playerBinding.downloadLinkButton.setOnClickListener(view -> {
            MainActivity.generateNotification(START_DOWNLOAD_ID, getContext());
            playerViewModel.getLinkDownload(getLayoutInflater(), container);
        });
        playerBinding.exitAudioPlay.setOnClickListener(view -> {
            destroy();
            playerViewModel.setWorkMode("background");
            MainActivity mainActivity = (MainActivity) getActivity();
            mainActivity.binding.mainLayout.playerContainer.setVisibility(View.GONE);
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
                NotificationForPlay.createNotification(getActivity(),
                        playerViewModel.getLinksModel().getValue(),
                        android.R.drawable.ic_media_pause);
                isPrepared = false;
            }
            NotificationForPlay.createNotification(getActivity(),
                    playerViewModel.getLinksModel().getValue(),
                    android.R.drawable.ic_media_pause);
            playerBinding.imageButtonPlay.setImageResource(R.drawable.baseline_pause_24);
            mediaPlayer.start();
        }else {
            NotificationForPlay.createNotification(getActivity(),
                    playerViewModel.getLinksModel().getValue(),
                    android.R.drawable.ic_media_play);
            playerBinding.imageButtonPlay.setImageResource(R.drawable.baseline_play_arrow_24);
            mediaPlayer.pause();
        }
    }

    public void destroy(){
        mediaPlayer.stop();
        getActivity().unregisterReceiver(broadcastReceiver);
        appPref.edit().putBoolean("app_pref_player", false).apply();
        Log.e("PLAYER_PREF", String.valueOf(appPref.getBoolean("app_pref_player", false)));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null && broadcastReceiver != null) {
            mediaPlayer.stop();
            getActivity().unregisterReceiver(broadcastReceiver);
        }
    }
}