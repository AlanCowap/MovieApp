package com.example.android.app.moviesapp;

import android.content.Context;
import android.widget.Toast;

import java.util.concurrent.atomic.AtomicInteger;


/**
 * Created by Laurence on 21/05/2017.
 *
 */

class Generator {
    //TODO AWESOME this is a nice pattern for using Toasts throughout an App
    //Logging STATIC VARIABLES
    static final String LOG_ENTERING = "ENTERING ";
    static final String LOG_EXITING = "EXITING ";
    static final String SPACE_CHAR = " ";

    /// Creates the unique integer required to identify app and loader in manager
    private static final AtomicInteger sCurrentUniqueId = new AtomicInteger(0);

    static int getNewUniqueLoaderId(){
        return sCurrentUniqueId.getAndIncrement();
    }

    private static Toast mToast;

    //  suppress the toast at onCreate
    // initialised as false
    static boolean mShowToast = false;

    public Generator(){
        super();
        mShowToast = false;
    }

    static void ClearToast(){
        if(mToast!=null){
            mToast.cancel();
        }
    }

    // call method to generate a toast message
    static void GenerateToastMessage(Context context, String message){
        // delete the toast if it exists, so we don't get a backlog
        Generator.ClearToast();
        // generate the toast
        mToast = Toast.makeText(context, message, Toast.LENGTH_LONG);
        mToast.show();
    }

}