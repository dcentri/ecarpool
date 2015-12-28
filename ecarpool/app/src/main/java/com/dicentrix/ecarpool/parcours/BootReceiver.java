package com.dicentrix.ecarpool.parcours;

/**
 * Created by Akash on 12/28/2015.
 */

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.dicentrix.ecarpool.main.Dashboard;

/*
 * BroadcastReceiver pour le démarrage (boot) de l'appareil.
 */
public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {

            // Activation de l'alarme.
            Intent alarmIntent = new Intent(context, AlarmReceiver.class);
            PendingIntent alarmPendingIntent = PendingIntent.getBroadcast(
                    context,
                    Dashboard.ID_ALARM,
                    alarmIntent,
                    PendingIntent.FLAG_CANCEL_CURRENT);

            AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

            alarmMgr.setRepeating(
                    AlarmManager.ELAPSED_REALTIME,
                    0,
                    Dashboard.INTERVAL_ALARM,
                    alarmPendingIntent);
        }
    }
}
