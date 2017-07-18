package com.example.android.app.moviesapp;

import android.content.Context;
import android.widget.Toast;

import java.util.concurrent.atomic.AtomicInteger;

class Generator {
    //Logging STATIC VARIABLES
    static final String LOG_ENTERING = "ENTERING ";
    static final String LOG_EXITING = "EXITING ";
    static final String SPACE_CHAR = " ";
    /// Creates the unique integer required to identify app and loader in manager
    private static final AtomicInteger sCurrentUniqueId = new AtomicInteger(0);
    //  suppress toast at onCreate
    // initialised as false
    static boolean mShowToast = false;
    private static Toast mToast;

    public Generator(){
        super();
        mShowToast = false;
    }

    static int getNewUniqueLoaderId() {

        return sCurrentUniqueId.get();
    }

    static void clearToast(){
        if(mToast!=null){
            mToast.cancel();
        }
    }

    // call method to generate a toast message
    static void generateToastMessage(Context context, String message){
        // delete the toast if it exists, so we don't get a backlog
        Generator.clearToast();
        // generate the toast
        mToast = Toast.makeText(context, message, Toast.LENGTH_LONG);
        mToast.show();
    }
}
