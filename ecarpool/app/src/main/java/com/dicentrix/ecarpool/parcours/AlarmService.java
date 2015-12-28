package com.dicentrix.ecarpool.parcours;

/**
 * Created by Akash on 12/28/2015.
 */

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.NotificationCompat;

import com.dicentrix.ecarpool.R;
import com.dicentrix.ecarpool.access.ConnectionActivity;
import com.dicentrix.ecarpool.util.Message;
import com.dicentrix.ecarpool.util.WEB;

import java.util.ArrayList;
import java.util.List;

/*
 * Service appelé lorsque l'alarme est déclenchée.
 * Note: Ce type de service simple permet d'exécuter une tâche asynchrone
 * dans la méthode "onHandleIntent"; ce qui est très pratique pour exécuter
 * une tâche lourde, comme le chargement de données sur Internet, sans avoir
 * besoin de se soucier de la gestion d'un "Thread".  Le service sera automatiquement
 * arrêté après l'exécution de la méthode "onHandleIntent".
 */
public class AlarmService extends IntentService {

    public static String NOTIFICATIONS = "notificationsManager";
    public AlarmService() {
        super("AlarmService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Exception m_Exp = null;
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this.getBaseContext());
        List<Message> msg = null;
        // On peut effectuer des tâches lourdes ici.
        try {
            String login = prefs.getString(ConnectionActivity.LOGIN, "");
            if(!login.equals("")){
                msg = WEB.getUserMessages(login);
            }
        } catch (Exception e) {
            m_Exp = e;
        }
        if(m_Exp != null){

        }else {
            if(msg != null && msg.size() > 0){
                SharedPreferences.Editor editor = prefs.edit();
                String notif = prefs.getString(NOTIFICATIONS, "");
                if(!notif.equals("")){
                    String temp = notif;
                    String[] newNotif = getIdArray(msg);
                    notif = getCSV(newNotif);
                    String[] alerts = getNewNotifId(temp.split(","), newNotif);
                    if(alerts.length > 0){
                        for (int i = 0; i< alerts.length; i++){
                            Notify(msg.get(Integer.parseInt(alerts[i])),this.getBaseContext() );
                        }
                    }
                }
                editor.putString(NOTIFICATIONS, notif);
                editor.commit();
            }
        }

    }

    public void Notify(Message m, Context cont){
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
        mBuilder.setSmallIcon(R.drawable.ecarpool_logo);
        mBuilder.setContentTitle(m.msg);
        mBuilder.setContentText(getString(R.string.new_request));

        Intent resultIntent = new Intent(this, ParcoursActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(cont);
        stackBuilder.addParentStack(ParcoursActivity.class);

        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

// notificationID allows you to update the notification later on.
        mNotificationManager.notify(Integer.parseInt(m.remoteId), mBuilder.build());
    }

    public String[] getIdArray(List<Message> msgs){
        String[] res = new String[msgs.size()];
        for(int i = 0; i < msgs.size(); i++){
            res[i] = msgs.get(i).remoteId;
        }
        return res;
    }
    public String getCSV(String[] list){
        String res = "";
        for(int i = 0; i < list.length; i++){
            res += list[i] + ",";
        }
        return res;
    }

    public String[] getNewNotifId(String[] old, String[] newNotif){
        String res = "";
        int i = 0;
        int k = 0;
        boolean found = false;
        while(i < newNotif.length){
            while (k < old.length && !found){
                if(newNotif[i].equals(old[k]))
                    found = true;
            }
            if(!found){
                res += String.valueOf(i) +",";
            }
        }
        return res.split(",");
    }
}
