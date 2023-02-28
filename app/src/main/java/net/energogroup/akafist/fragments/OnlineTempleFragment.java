package net.energogroup.akafist.fragments;

import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.energogroup.akafist.MainActivity;
import net.energogroup.akafist.databinding.FragmentOnlineTempleBinding;
import net.energogroup.akafist.service.NetworkConnection;
import net.energogroup.akafist.viewmodel.OnlineTempleViewModel;

/**
 * Класс фрагмента с прямым эфиром
 * @author Nastya Izotina
 * @version 1.0.0
 */
public class OnlineTempleFragment extends Fragment {

    public FragmentOnlineTempleBinding onlineTempleBinding;
    private String urlSound;
    private NetworkConnection networkConnection;
    private OnlineTempleViewModel onlineTempleViewModel;

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
            networkConnection.observe(getViewLifecycleOwner(), isChecked->{
                if (isChecked){
                    onlineTempleBinding.noInternet2.setVisibility(View.INVISIBLE);
                    urlSound = getArguments().getString("urlToSound");
                    Log.d("ONLINE_TEMPLE_ERROR", urlSound);
                    String soundTitle = getArguments().getString("soundTitle");
                    ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(soundTitle);
                    onlineTempleBinding.stopPlayButton.setOnClickListener(view1 -> {
                        onlineTempleViewModel.play(getLayoutInflater(), getView(), urlSound);
                        String transName = soundTitle.substring(0,10);
                        transName += " богослужения общины ";
                        transName += soundTitle.substring(11, soundTitle.length());
                        onlineTempleBinding.transName.setText(transName);
                    });
                }else {
                    onlineTempleBinding.noInternet2.setVisibility(View.VISIBLE);
                }
            });
        }

        return onlineTempleBinding.getRoot();
    }

    /**
     * Этот метод уничтожает фрагмент
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        onlineTempleViewModel.checkPlaying();
        OnlineTempleViewModel.initStage = true;
        OnlineTempleViewModel.playPause = false;
        OnlineTempleViewModel.getMediaPlayer().reset();
    }
}