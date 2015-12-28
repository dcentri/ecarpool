package com.dicentrix.ecarpool.parcours;

import android.app.AlarmManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.dicentrix.ecarpool.R;
import com.dicentrix.ecarpool.access.ConnectionActivity;
import com.dicentrix.ecarpool.main.Dashboard;
import com.dicentrix.ecarpool.util.Message;
import com.dicentrix.ecarpool.util.WEB;

import java.util.List;

/**
 * Created by Akash on 9/27/2015.
 */
public class NotificationFragment extends ListFragment{

    public static String MSG = "msg";
    String[] notifs;
    List<Message> msgs;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        View rootView = inflater.inflate(R.layout.parcourslist_fragement, container, false);
        new GetNotifications().execute((Void) null);
        return rootView;
    }
    public void displayNotifs(){
        final ArrayAdapter adapter = new ArrayAdapter(getActivity(),
                android.R.layout.simple_list_item_1, notifs);
        this.getView().findViewById(R.id.txtNoItems).setVisibility(View.INVISIBLE);
        setListAdapter(adapter);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start your app main activity
                new GetNotifications().execute((Void) null);
            }
        }, Dashboard.INTERVAL_ALARM);
    }
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Intent i = new Intent(getActivity(), DetailMessageActivity.class);
        i.putExtra(MSG, msgs.get(position).remoteId);
        this.startActivity(i);

    }
    private class GetNotifications extends AsyncTask<Void, Void, Void> {
        Exception m_Exp;

        // Méthode exécutée SYNChrone avant l'exécution de la tâche asynchrone.
        @Override
        protected void onPreExecute() {
        }

        // Méthode exécutée ASYNChrone: la tâche en tant que telle.
        @Override
        protected Void doInBackground(Void... unused) {

            try {
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
                String login = prefs.getString(ConnectionActivity.LOGIN, "");
                msgs = WEB.getUserMessages(login);
            } catch (Exception e) {
                m_Exp = e;
            }
            return null;
        }

        private String[] getNotifLable(List<Message> msg){
            String[] res = new String[msg.size()];
            for(int i =0; i < msg.size(); i++){
                res[i] = msg.get(i).msg;
            }
            return res;
        }
        // Méthode exécutée SYNChrone après l'exécution de la tâche asynchrone.
        // Elle reçoit en paramètre la valeur de retour de "doInBackground".
        @Override
        protected void onPostExecute(Void unused) {

            if (m_Exp == null) {
                if(msgs != null && msgs.size() > 0){
                    notifs = getNotifLable(msgs);
                    displayNotifs();
                }
            } else {
                Toast.makeText(getActivity(), getString(R.string.err_invalidConnection), Toast.LENGTH_SHORT).show();
            }
        }
    }
}
