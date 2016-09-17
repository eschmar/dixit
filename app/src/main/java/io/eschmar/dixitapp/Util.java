package io.eschmar.dixitapp;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.Manifest;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by eschmar on 17/09/16.
 */

public class Util {
    private static final String LOG_TAG = "DixitAppUtil";
    public static final String APP_NAME = "Dixit";

    public static void requestPermission(Activity activity, String[] permissions) {
        List<String> newPermissions = new ArrayList<String>();

        for (String p: permissions) {
            if (ContextCompat.checkSelfPermission(activity, p) != PackageManager.PERMISSION_GRANTED) {
                newPermissions.add(p);
            }
        }

        if (newPermissions.toArray().length < 1) {
            return;
        }

        ActivityCompat.requestPermissions(activity, newPermissions.toArray(new String[0]), 0);
    }

    public static void createAppFolder() {
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            Log.d(LOG_TAG, "No SDCARD found.");
        } else {
            File directory = new File(Environment.getExternalStorageDirectory() + File.separator + APP_NAME);
            directory.mkdirs();
        }
    }
}
