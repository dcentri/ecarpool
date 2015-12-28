package com.dicentrix.ecarpool.parcours;

/**
 * Created by Akash on 12/28/2015.
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.Toast;

/*
 * BroadcastReceiver pour le déclenchement de l'alarme.
 */
public class AlarmReceiver extends BroadcastReceiver {
    public AlarmReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // Récupération du compteur du nombre de déclenchements de l'alarme.
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        int triggerCount = prefs.getInt("trigger_count", 0);

        Toast.makeText(context, "Hello" + triggerCount, Toast.LENGTH_SHORT).show();

        // Appel du service qui gère le déclenchement de l'alarme.
        // Note : Il est essentiel de faire ceci lorsque le traitement
        // à effectuer est lourd car l'exécution de "onReceive" doit être rapide.
        // De plus, il ne faut pas partir une tâche asynchrone ici.
        Intent intentService = new Intent(context, AlarmService.class);
        context.startService(intentService);
    }
}
