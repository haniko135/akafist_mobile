package net.energogroup.akafist.fragments;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.media3.common.MediaItem;
import androidx.media3.common.Player;
import androidx.media3.common.Timeline;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.datasource.DefaultHttpDataSource;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.exoplayer.hls.HlsDataSourceFactory;
import androidx.media3.exoplayer.hls.HlsManifest;
import androidx.media3.exoplayer.hls.HlsMediaSource;
import androidx.media3.exoplayer.source.MediaSource;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import net.energogroup.akafist.databinding.FragmentOnlineTempleBinding;
import net.energogroup.akafist.service.NetworkConnection;
import net.energogroup.akafist.viewmodel.OnlineTempleViewModel;

public class OnlineTempleFragment extends Fragment {

    public FragmentOnlineTempleBinding onlineTempleBinding;
    private String urlSound;
    private NetworkConnection networkConnection;
    private OnlineTempleViewModel onlineTempleViewModel;

    private boolean playPauseState = true;
    private ExoPlayer player = null;
    private MediaItem mediaItem;

    /**
     * Обязательный конструктор класса
     */
    public OnlineTempleFragment() { }

    /**
     * Этот метод отвечает за создание класса фрагмента с прямым эфиром
     * @return OnlineTempleFragment
     */
    public static OnlineTempleFragment newInstance() {
        return new OnlineTempleFragment();
    }

    /**
     * Этот метод подготавливает активность к работе фрагмента
     * @param savedInstanceState Bundle
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewModelProvider provider = new ViewModelProvider(this);
        onlineTempleViewModel = provider.get(OnlineTempleViewModel.class);
        if(((AppCompatActivity)getActivity()).getSupportActionBar() != null) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Трансляция общины");
            networkConnection = new NetworkConnection(getContext().getApplicationContext());
        }
    }

    /**
     * Этот метод создаёт фрагмент
     * @param inflater LayoutInflater
     * @param container ViewGroup
     * @param savedInstanceState Bundle
     * @return View
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        onlineTempleBinding = FragmentOnlineTempleBinding.inflate(getLayoutInflater());

        if (getArguments() != null && getActivity().getApplicationContext() != null) {
            networkConnection.observe(getViewLifecycleOwner(), isChecked -> {
                if (isChecked) {
                    onlineTempleBinding.noInternet2.setVisibility(View.INVISIBLE);
                    urlSound = getArguments().getString("urlToSound");
                    onlineTempleViewModel.setUrlSound(urlSound);
                    Log.d("ONLINE_TEMPLE_ERROR", urlSound);
                    String soundTitle = getArguments().getString("soundTitle");
                    ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(soundTitle);

                    if (player == null) {
                        initializePlayer(urlSound, getContext());
                    }
                } else {
                    onlineTempleBinding.noInternet2.setVisibility(View.VISIBLE);
                }
            });
        }

        return onlineTempleBinding.getRoot();
    }


    public void initializePlayer(String s, Context context){
        player = new ExoPlayer.Builder(context).build();
        onlineTempleBinding.exoPlayer.setPlayer(player);
        mediaItem = new MediaItem.Builder()
                .setUri(Uri.parse(s))
                .setLiveConfiguration(new MediaItem.LiveConfiguration.Builder()
                        .setMaxPlaybackSpeed(1.02f)
                        .build())
                .build();
        player.setMediaItem(mediaItem);
        player.setPlayWhenReady(playPauseState);
        player.prepare();
    }

    public void releasePlayer(){
        Log.e("ONLINE_TEMPLE_ERROR", "RELEASE PLAYER");
        player.setPlayWhenReady(!playPauseState);
        playPauseState = player.getPlayWhenReady();
        player.stop();
        player.seekTo(0);
        mediaItem = null;
        player.release();
        player = null;
    }


    @Override
    public void onPause() {
        super.onPause();
        if(player != null){
            Log.e("ONLINE_TEMPLE_ERROR", "ON PAUSE");
        }
    }

    /**
     * Этот метод уничтожает фрагмент
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(player != null){
            Log.e("ONLINE_TEMPLE_ERROR", "ON DESTROY");
            releasePlayer();
        }
    }
}