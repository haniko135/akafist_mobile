package net.energogroup.akafist.service;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.ListenableWorker;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import net.energogroup.akafist.viewmodel.OnlineTempleViewModel;

import java.io.IOException;

/**
 * Класс, воспроизводящий онлайн-трансляцию
 * @author Nastya Izotina
 * @version 1.0.0
 */
public class OnlineTemplePlayer extends Worker {

    /**
     * Конструктор, наследуемый от {@link Worker}
     * @param context Context
     * @param workerParams WorkerParams
     */
    public OnlineTemplePlayer(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    /**
     * Этот метод прекращает работу ProcessDialog и начинает трансляцию
     */
    protected void onPostExecute() {
        if (OnlineTempleViewModel.getProgressDialog().isShowing()){
            OnlineTempleViewModel.getProgressDialog().cancel();
        }
        OnlineTempleViewModel.getMediaPlayer().start();
        OnlineTempleViewModel.initStage = false;
    }

    /**
     * Этот метод подготавливаает плеер к проигрыванию эфира и производит первычное подключение
     * к источнику эфира
     * @return Результат работы по подключению
     */
    @NonNull
    @Override
    public Result doWork() {
        ListenableWorker.Result result;
        try {
            OnlineTempleViewModel.getMediaPlayer().setDataSource(getInputData().getString("URL_SOUND"));
            OnlineTempleViewModel.getMediaPlayer().setOnCompletionListener(mediaPlayer -> {
                OnlineTempleViewModel.initStage = true;
                OnlineTempleViewModel.playPause = false;
                OnlineTempleViewModel.getMediaPlayer().stop();
                OnlineTempleViewModel.getMediaPlayer().reset();
            });
            OnlineTempleViewModel.getMediaPlayer().prepare();
            result = ListenableWorker.Result.success();
        } catch (IOException e) {
            Log.e("ONLINE_TEMPLE_ERROR",e.getLocalizedMessage());
            result = ListenableWorker.Result.failure();
        }
        onPostExecute();
        return result;
    }
}
