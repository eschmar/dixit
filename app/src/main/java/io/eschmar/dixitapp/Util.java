package io.eschmar.dixitapp;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.Manifest;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by eschmar on 17/09/16.
 */

public class Util {
    public static void requestPermission(Activity activity, String[] permissions) {
        List<String> newPermissions = new ArrayList<String>();

        for (String p: permissions) {
            if (ContextCompat.checkSelfPermission(activity, p) != PackageManager.PERMISSION_GRANTED) {
                newPermissions.add(p);
            }
        }

        ActivityCompat.requestPermissions(activity, newPermissions.toArray(new String[0]), 0);
    }
}
