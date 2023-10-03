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
 * Class for checcking internet connection
 * @author Nastya Izotina
 * @version 1.0.0
 */
public class NetworkConnection extends LiveData<Boolean> {
    private Context context;
    private ConnectivityManager connectivityManager;

    /**
     * The constructor of the class taking into account the current context
     * @param context Context
     */
    public NetworkConnection(Context context){
        this.context = context;
        connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    private NetworkCallback networkCallback = new NetworkCallback();

    /**
     * This method checks the internet connection in real time
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
     * @return Checking the network response
     */
    private NetworkCallback connectivityManagerCallback(){
        networkCallback = new NetworkCallback();
        return networkCallback;
    }

    /**
     * This method checks the network if there is at least one subscriber
     */
    @Override
    protected void onActive() {
        super.onActive();
        updateNetworkConnection();
        connectivityManager.registerDefaultNetworkCallback(connectivityManagerCallback());

    }

    /**
     * This method disables network verification if there are no subscribers
     */
    @Override
    protected void onInactive() {
        super.onInactive();
        //connectivityManager.unregisterNetworkCallback(connectivityManagerCallback());
    }

    /**
     * An internal class that overrides {@link ConnectivityManager.NetworkCallback}
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
     * An internal class that overrides {@link BroadcastReceiver}
     */
    class NetworkReciever extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            updateNetworkConnection();
        }
    }
}
