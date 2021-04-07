package com.example.travelbuddyv2.networkManager;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkRequest;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;

public class NetworkObserver {

    Context context;
    public static boolean isNetworkConnected = false;
    public String tag = "NETWORK_OBSERVER";

    public NetworkObserver(Context context) {
        this.context = context;
    }

    public void registerNetworkCallback() {
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkRequest.Builder builder = new NetworkRequest.Builder();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                connectivityManager.registerDefaultNetworkCallback(new ConnectivityManager.NetworkCallback(){
                    @Override
                    public void onAvailable(@NonNull Network network) {
                        isNetworkConnected = true;
                        Log.d(tag, String.valueOf(true));
                    }

                    @Override
                    public void onLost(@NonNull Network network) {
                        isNetworkConnected = false;
                        Log.d(tag, String.valueOf(false));
                    }
                });
            }
            isNetworkConnected = false;
        } catch (Exception e) {
            isNetworkConnected = false;
        }
    }





}
