package com.linsen.h5.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetWorkUtil {
	/**
	 * 判断是否有网络连接
	 * 
	 * @return
	 */

	public static Boolean isNetworkConnected(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = connectivityManager.getActiveNetworkInfo();

		Boolean conn;
		if (ni != null && ni.isConnectedOrConnecting())
			conn = true;
		else
			conn = false;
		return conn;
	}

}
