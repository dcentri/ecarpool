package com.dicentrix.ecarpool.parcours;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.dicentrix.ecarpool.R;
import com.dicentrix.ecarpool.access.ConnectionActivity;
import com.dicentrix.ecarpool.misc.ParcoursSearchListFragment;
import com.dicentrix.ecarpool.user.IUserDAO;
import com.dicentrix.ecarpool.user.User;
import com.dicentrix.ecarpool.user.UserDAO;
import com.dicentrix.ecarpool.util.ChooseElementFragment;
import com.dicentrix.ecarpool.util.Message;
import com.dicentrix.ecarpool.util.WEB;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;

/**
 * Created by Akash on 11/25/2015.
 */
public class DetailMessageActivity extends FragmentActivity{

    private final String TAG = this.getClass().getSimpleName();
    Parcours p = new Parcours();
    Trajet t = new Trajet();
    IUserDAO db = new UserDAO(this);
    User user;
    Message msg;
    String msgId;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_message_activity);
        Intent intent = getIntent();
        msgId = intent.getStringExtra(NotificationFragment.MSG);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        user = db.getById(prefs.getInt(ConnectionActivity.USER_ID, 0));
        new GetMessageTask().execute((Void)null);

    }

    public  void setFields(){
        try{
            if(p != null){
                ((TextView) this.findViewById(R.id.txtDeparture)).setText(p.getDefaultTrajet().getDepart().toString());
                ((TextView) this.findViewById(R.id.txtArrival)).setText(p.getDefaultTrajet().getDestination().toString());
                DecimalFormat df = new DecimalFormat("#.####");
                df.setRoundingMode(RoundingMode.CEILING);
                ((TextView) this.findViewById(R.id.txtKM)).setText(df.format(p.getDefaultTrajet().getDepart().getDistanceTo(p.getDefaultTrajet().getDestination())));
                ((TextView) this.findViewById(R.id.txtDriver)).setText(p.getDefaultTrajet().getAuthor().toString());
                ((TextView) this.findViewById(R.id.txtPhone)).setText(p.getDefaultTrajet().getAuthor().getPhone());
                ((TextView) this.findViewById(R.id.txtEmail)).setText(p.getDefaultTrajet().getAuthor().getEmail());
                ((TextView) this.findViewById(R.id.txtFrequency)).setText(p.getDefaultTrajet().getFrequence().getName());
                ((TextView) this.findViewById(R.id.txtDate)).setText(p.getDefaultTrajet().getDepartDateTime().toString());
                ((TextView)this.findViewById(R.id.txtElementPrice)).setText(String.valueOf(p.getPrice()));
                ((TextView)this.findViewById(R.id.txtPlaces)).setText(String.valueOf(p.getNbPlaces()));
            }
            if(t != null){
                ((TextView) this.findViewById(R.id.txtPassengerDeparture)).setText(t.getDepart().toString());
                ((TextView) this.findViewById(R.id.txtPassengerArrival)).setText(t.getDestination().toString());
                DecimalFormat df = new DecimalFormat("#.####");
                df.setRoundingMode(RoundingMode.CEILING);
                ((TextView) this.findViewById(R.id.txtKM)).setText(df.format(t.getDepart().getDistanceTo(t.getDestination())));
                ((TextView) this.findViewById(R.id.txtPassenger)).setText(t.getAuthor().toString());
                ((TextView) this.findViewById(R.id.txtPassengerPhone)).setText(t.getAuthor().getPhone());
                ((TextView) this.findViewById(R.id.txtPassengerEmail)).setText(t.getAuthor().getEmail());
                ((TextView) this.findViewById(R.id.txtPassengerDate)).setText(t.getDepartDateTime().toString());
            }
        }catch (Exception e){}
    }

    public void Accept(View v) {
        new AcceptRequestTask().execute((Void) null);
    }
    public void Delete(View v) {
        new DeleteRequestTask().execute((Void) null);
    }
    public void end(){
        this.finish();
    }

    private class GetMessageTask extends AsyncTask<Void, Void, Void> {
        Exception m_Exp;

        // Méthode exécutée SYNChrone avant l'exécution de la tâche asynchrone.
        @Override
        protected void onPreExecute() {
            setProgressBarIndeterminateVisibility(true);
        }

        // Méthode exécutée ASYNChrone: la tâche en tant que telle.
        @Override
        protected Void doInBackground(Void... unused) {

            try {
                if(msgId != null){
                    msg = WEB.getMessage(user.getLogin(), msgId);
                    if(msg != null){
                        p = WEB.setParcoursTrajets(WEB.getParcours(msg.refParcours));
                        t = WEB.getCompleteTrajet(msg.refTrajet);
                    }else
                    {
                        throw new IllegalArgumentException("Le message n'a pas été trouvé");
                    }
                }
                Log.i(TAG, "Parcours reçu avec succès");
            } catch (Exception e) {
                m_Exp = e;
            }
            return null;
        }

        // Méthode exécutée SYNChrone après l'exécution de la tâche asynchrone.
        // Elle reçoit en paramètre la valeur de retour de "doInBackground".
        @Override
        protected void onPostExecute(Void unused) {
            setProgressBarIndeterminateVisibility(false);

            if (m_Exp == null) {
                setFields();
            } else {
                Log.e(TAG, "Erreur lors de l'ajout ou de la modification de la personne (PUT)", m_Exp);
                Toast.makeText(DetailMessageActivity.this, getString(R.string.err_invalidConnection), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class AcceptRequestTask extends AsyncTask<Void, Void, Void> {
        Exception m_Exp;

        // Méthode exécutée SYNChrone avant l'exécution de la tâche asynchrone.
        @Override
        protected void onPreExecute() {
            setProgressBarIndeterminateVisibility(true);
        }

        // Méthode exécutée ASYNChrone: la tâche en tant que telle.
        @Override
        protected Void doInBackground(Void... unused) {

            try {
                Map<String, String> status;
                if(user.getType() == User.UserType.DRIVER){
                    status = WEB.driverTrajetParcoursRequest(user.getLogin(), p.remoteId, t.remoteId);
                }else{
                    status = WEB.passengerParcoursTrajetRequest(user.getLogin(), p.remoteId, t.remoteId);
                }
                WEB.deleteMessage(user.getLogin(), msg.remoteId);
                Log.i(TAG, "Parcours reçu avec succès");
            } catch (Exception e) {
                m_Exp = e;
            }
            return null;
        }

        // Méthode exécutée SYNChrone après l'exécution de la tâche asynchrone.
        // Elle reçoit en paramètre la valeur de retour de "doInBackground".
        @Override
        protected void onPostExecute(Void unused) {
            setProgressBarIndeterminateVisibility(false);

            if (m_Exp == null) {
                Toast.makeText(DetailMessageActivity.this, getString(R.string.succ_request), Toast.LENGTH_LONG).show();
                end();
            } else {
                Log.e(TAG, "Erreur lors de l'ajout ou de la modification de la personne (PUT)", m_Exp);
                Toast.makeText(DetailMessageActivity.this, getString(R.string.err_invalidConnection), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class DeleteRequestTask extends AsyncTask<Void, Void, Void> {
        Exception m_Exp;
        Map<String,String> res;
        // Méthode exécutée SYNChrone avant l'exécution de la tâche asynchrone.
        @Override
        protected void onPreExecute() {

        }

        // Méthode exécutée ASYNChrone: la tâche en tant que telle.
        @Override
        protected Void doInBackground(Void... unused) {

            try {
                WEB.deleteMessage(user.getLogin(), msg.remoteId);
                Log.i(TAG, "Création de parcour terminé avec succès");
            } catch (Exception e) {
                m_Exp = e;
            }
            return null;
        }

        // Méthode exécutée SYNChrone après l'exécution de la tâche asynchrone.
        // Elle reçoit en paramètre la valeur de retour de "doInBackground".
        @Override
        protected void onPostExecute(Void unused) {

            if (m_Exp == null) {
                Toast.makeText(DetailMessageActivity.this, getString(R.string.succ_request), Toast.LENGTH_LONG).show();
                end();
            } else {
                Log.e(TAG, "Erreur lors de l'ajout ou de la modification de la personne (PUT)", m_Exp);
            }
        }
    }
}
