package net.energogroup.akafist.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

/**
 * Класс, проверяющий интренет-соединение
 * @author Nastya Izotina
 * @version 1.0.0
 */
public class NetworkConnection extends LiveData<Boolean> {
    private Context context;
    private ConnectivityManager connectivityManager;

    /**
     * Конструктор класса с учётом текущего контекста
     * @param context Context
     */
    public NetworkConnection(Context context){
        this.context = context;
        connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    private NetworkCallback networkCallback = new NetworkCallback();

    /**
     * Этот метод провереяет интернет0сеодинение в режиме реального времени
     */
    private void updateNetworkConnection(){
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null ) {
            postValue(networkInfo.isConnected());
            Log.d("NETWORK_CHECK", String.valueOf(connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork()).getLinkDownstreamBandwidthKbps()));
        }
        else postValue(false);
    }

    /**
     * @return Проверка ответа сети
     */
    private NetworkCallback connectivityManagerCallback(){
        networkCallback = new NetworkCallback();
        return networkCallback;
    }

    /**
     * Этот метод проверяет сеть при наличии хотя бы одного подписчика
     */
    @Override
    protected void onActive() {
        super.onActive();
        updateNetworkConnection();
        connectivityManager.registerDefaultNetworkCallback(connectivityManagerCallback());

    }

    /**
     * Этот метод отключает проверку сети при отсутствии подписчиков
     */
    @Override
    protected void onInactive() {
        super.onInactive();
        //connectivityManager.unregisterNetworkCallback(connectivityManagerCallback());
    }

    /**
     * Внутренний класс, который переопределяет класс {@link ConnectivityManager.NetworkCallback}
     */
    class NetworkCallback extends ConnectivityManager.NetworkCallback{
        @Override
        public void onLost(@NonNull Network network) {
            super.onLost(network);
            postValue(false);
        }

        @Override
        public void onAvailable(@NonNull Network network) {
            super.onAvailable(network);
            postValue(true);
        }
    }

    /**
     * Внутренний класс, который переопределяет класс {@link BroadcastReceiver}
     */
    class NetworkReciever extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            updateNetworkConnection();
        }
    }
}
