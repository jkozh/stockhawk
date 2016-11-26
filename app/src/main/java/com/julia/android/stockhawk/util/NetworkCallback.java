package com.julia.android.stockhawk.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.util.Log;
import android.widget.Toast;

public class NetworkCallback extends ConnectivityManager.NetworkCallback {

    private Context mContext;

    public NetworkCallback(Context context) {
        mContext = context;
    }

    public Context getContext() {
        return mContext;
    }

    @Override
    public void onAvailable(Network network) {
        super.onAvailable(network);
        Log.v("NetworkCallback", "NetworkCallback.onAvailable() with network " + network.toString());
    }

    @Override
    public void onLost(Network network) {
        super.onLost(network);
        Log.v("NetworkCallback", "NetworkCallback.onLost() with network " + network.toString());
        Toast.makeText(getContext(), "Network Unavailable", Toast.LENGTH_SHORT).show();
    }

}
