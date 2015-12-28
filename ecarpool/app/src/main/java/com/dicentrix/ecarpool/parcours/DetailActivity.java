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
import com.dicentrix.ecarpool.util.WEB;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;

/**
 * Created by Akash on 11/25/2015.
 */
public class DetailActivity extends FragmentActivity{

    private final String TAG = this.getClass().getSimpleName();
    Parcours p = new Parcours();
    List<Parcours> listeP;
    public Parcours tMatch;
    public Trajet pMatch;
    Trajet t = new Trajet();
    List<Trajet> listeT;
    IUserDAO db = new UserDAO(this);
    User user;
    String elementId;
    String element;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_activity);
        Intent intent = getIntent();
        element = intent.getStringExtra(ParcoursSearchListFragment.ELEMENTYPE);
        elementId = intent.getStringExtra(ParcoursSearchListFragment.ID);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        user = db.getById(prefs.getInt(ConnectionActivity.USER_ID, 0));
        new GetElementTask().execute((Void)null);

    }

    public  void setFields(){
        try{
            if(t != null){
                ((TextView) this.findViewById(R.id.txtDeparture)).setText(t.getDepart().toString());
                ((TextView) this.findViewById(R.id.txtArrival)).setText(t.getDestination().toString());
                DecimalFormat df = new DecimalFormat("#.####");
                df.setRoundingMode(RoundingMode.CEILING);
                ((TextView) this.findViewById(R.id.txtKM)).setText(df.format(t.getDepart().getDistanceTo(t.getDestination())));
                ((TextView) this.findViewById(R.id.txtDriver)).setText(t.getAuthor().toString());
                ((TextView) this.findViewById(R.id.txtPhone)).setText(t.getAuthor().getPhone());
                ((TextView) this.findViewById(R.id.txtEmail)).setText(t.getAuthor().getEmail());
                ((TextView) this.findViewById(R.id.txtFrequency)).setText(t.getFrequence().getName());
                ((TextView) this.findViewById(R.id.txtDate)).setText(t.getDepartDateTime().toString());
                if(user.getType() == User.UserType.DRIVER){
                    ((TextView)this.findViewById(R.id.lblUserType)).setText(R.string.lbl_passenger);
                    this.findViewById(R.id.lblElementPrice).setVisibility(View.INVISIBLE);
                    this.findViewById(R.id.lblPlaces).setVisibility(View.INVISIBLE);
                }else{
                    ((TextView)this.findViewById(R.id.txtElementPrice)).setText(String.valueOf(p.getPrice()));
                    ((TextView)this.findViewById(R.id.txtPlaces)).setText(String.valueOf(p.getNbPlaces()));
                    ((Button) this.findViewById(R.id.btnSendRequest)).setText(R.string.lbl_request_parc);
                }
            }
        }catch (Exception e){}
    }

    public void RequestElement(View v) {
        new GetPersonalList().execute((Void) null);
    }

    public void displayChooseList(){
        ChooseElementFragment newFragment = new ChooseElementFragment();
        newFragment.element = element;
        newFragment.listeP = listeP;
        newFragment.listeT = listeT;
        newFragment.user = user;
        newFragment.p = p;
        newFragment.t = t;
        newFragment.show(this.getFragmentManager(), "Element Choisie");
    }
    public void doPositiveClick(int pos) {
        if (pos != -1) {
            if(element.equals("TRAJET")){
                tMatch = listeP.get(pos);
            }else
                pMatch = listeT.get(pos);

            new SendRequestTask().execute((Void) null);
        } else {
            Toast.makeText(this, getString(R.string.err_no_choice), Toast.LENGTH_LONG).show();
        }
        Log.i("FragmentAlertDialog", "Positive click!");
    }

    public void doNegativeClick() {
        // Do stuff here.
        Log.i("FragmentAlertDialog", "Negative click!");
    }
    public void requestFinished(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
        finish();
    }

    private class GetElementTask extends AsyncTask<Void, Void, Void> {
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
                if(element.equals("TRAJET"))
                    t = WEB.getCompleteTrajet(elementId);
                else
                    p = WEB.setParcoursTrajets(WEB.getParcours(elementId));

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
                if(p != null && p.getTrajets() != null)
                    t = p.getDefaultTrajet();
                setFields();
            } else {
                Log.e(TAG, "Erreur lors de l'ajout ou de la modification de la personne (PUT)", m_Exp);
                Toast.makeText(DetailActivity.this, getString(R.string.err_invalidConnection), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class GetPersonalList extends AsyncTask<Void, Void, Void> {
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
                if(element.equals("TRAJET"))
                    listeP = WEB.getUserParcoursWithTrajets(user.getLogin());
                else
                    listeT = WEB.batchFillTrajetAdresses(WEB.getUser_sUsableTrajets(user.getLogin()));

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
                displayChooseList();
            } else {
                Log.e(TAG, "Erreur lors de l'ajout ou de la modification de la personne (PUT)", m_Exp);
                Toast.makeText(DetailActivity.this, getString(R.string.err_invalidConnection), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class SendRequestTask extends AsyncTask<Void, Void, Void> {
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
                if(element.equals("TRAJET"))
                    res = WEB.driverTrajetParcoursRequest(user.getLogin(), tMatch.remoteId, t.remoteId );
                else
                    res = WEB.passengerParcoursTrajetRequest(user.getLogin(),p.remoteId, pMatch.remoteId);
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
                String msg = "";
                if(res.containsKey("erreur"))
                    msg = res.get("erreur");
                else if (res.containsKey("succes"))
                    msg = res.get("succes");
                requestFinished(msg);
            } else {
                Log.e(TAG, "Erreur lors de l'ajout ou de la modification de la personne (PUT)", m_Exp);
            }
        }
    }
}
