package com.tes.imdb;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.util.Log;

import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.enums.SnackbarType;
import com.nispok.snackbar.listeners.ActionClickListener;



public class Constants {

    public static class BaseDir {
        public static final String BaseUrl = "http://www.omdbapi.com/";
    }
    public static void largeLog(String paramString1, String paramString2)
    {
        if (paramString2.length() > 4000)
        {
            Log.d(paramString1, paramString2.substring(0, 4000));
            largeLog(paramString1, paramString2.substring(4000));
            return;
        }
        Log.d(paramString1, paramString2);
    }

    public static void showSnackbarConnection(Context ctx) {
        Snackbar.with(ctx) // context
                .type(SnackbarType.MULTI_LINE)
                .duration(Snackbar.SnackbarDuration.LENGTH_INDEFINITE)
                .text(ctx.getResources().getString(R.string.fail))
                .actionLabel("OK")
                .actionListener(new ActionClickListener() {
                    @Override
                    public void onActionClicked(Snackbar snackbar) {
                        snackbar.dismiss();
                    }
                }).show((Activity)ctx);
        return;
    }
    public static boolean isOnline(Context ctx) {
        try
        {
            ConnectivityManager cm = (ConnectivityManager)ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
            return cm.getActiveNetworkInfo().isConnectedOrConnecting();
        }
        catch (Exception e)
        {
            return false;
        }
    }

}
