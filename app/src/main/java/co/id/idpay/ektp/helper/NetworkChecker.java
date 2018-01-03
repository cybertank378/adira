package co.id.idpay.ektp.helper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.content.LocalBroadcastManager;

/**
 * Created      : Rahman on 8/28/2017.
 * Project      : EKTP.
 * ================================
 * Package      : com.esimtek.helper.
 * Copyright    : idpay.com 2017.
 */
public class NetworkChecker extends BroadcastReceiver {
    public static final String TAG = NetworkChecker.class.getSimpleName();

    public static final String NETWORK_AVAILABLE_ACTION = "com.esimtek.helper.NetworkChecker";
    public static final String IS_NETWORK_AVAILABLE = "isNetworkAvailable";

    public static boolean isNetworkAvailable(Context context) {
        int[] networkTypes = {ConnectivityManager.TYPE_MOBILE, ConnectivityManager.TYPE_WIFI};
        try {
            ConnectivityManager connectivityManager =(ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            for (int networkType : networkTypes) {
                NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
                if (activeNetworkInfo != null && activeNetworkInfo.getType() == networkType)
                    return true;
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        Intent networkStateIntent = new Intent(NETWORK_AVAILABLE_ACTION);
        networkStateIntent.putExtra(IS_NETWORK_AVAILABLE,  isNetworkAvailable(context));
        LocalBroadcastManager.getInstance(context).sendBroadcast(networkStateIntent);

    }
}
