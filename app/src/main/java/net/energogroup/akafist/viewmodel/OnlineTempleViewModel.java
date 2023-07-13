package net.energogroup.akafist.viewmodel;

import android.media.MediaPlayer;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import net.energogroup.akafist.fragments.OnlineTempleFragment;

/**
 * Класс, содержащий логику обработки данных
 * {@link OnlineTempleFragment} и запуска плеера
 * @author Nastya Izotina
 * @version 1.0.0
 */
public class OnlineTempleViewModel extends ViewModel {
    private static MediaPlayer mediaPlayer = new MediaPlayer();
    public static boolean playPause;
    public static boolean initStage = true;

    private MutableLiveData<String> urlSound = new MutableLiveData<>();

    public static MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }

    public MutableLiveData<String> getUrlSound() {
        return urlSound;
    }

    public void setUrlSound(String urlSound) {
        this.urlSound.setValue(urlSound);
    }
}
