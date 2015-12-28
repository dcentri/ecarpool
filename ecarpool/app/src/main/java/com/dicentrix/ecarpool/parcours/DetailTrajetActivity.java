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
import com.dicentrix.ecarpool.misc.ParcoursSearchActivity;
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
public class DetailTrajetActivity extends FragmentActivity{

    private final String TAG = this.getClass().getSimpleName();
    public Parcours tMatch;
    Trajet t = new Trajet();
    IUserDAO db = new UserDAO(this);
    User user;
    String trajetId;
    String login;
    String loop;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_trajet_activity);
        Intent intent = getIntent();
        trajetId = intent.getStringExtra(ParcoursListFragment.TRAJET_DETAIL);
        login = intent.getStringExtra(ParcoursListFragment.LOGIN);
        if(intent.hasExtra(DetailParcoursActivity.LOOP))
            loop = intent.getStringExtra(DetailParcoursActivity.LOOP);
        new GetTrajetTask().execute((Void) null);

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
                if(loop == null){
                    if(t.booked){
                        ((Button) this.findViewById(R.id.btnSendRequest)).setText(R.string.lbl_parc_consult);
                        new GetTrajetParcoursTask().execute((Void) null);
                    }
                }else {
                    ((Button) this.findViewById(R.id.btnSendRequest)).setText(R.string.lbl_back);
                }

            }
        }catch (Exception e){}
    }

    public void requestAction(View v){
        if(loop == null){
            if(t.booked){
                if(tMatch != null){
                    Intent i = new Intent(this, DetailParcoursActivity.class);
                    i.putExtra(ParcoursListFragment.PARCOURS_DETAIL, tMatch.remoteId);
                    i.putExtra(ParcoursListFragment.LOGIN, login);
                    this.startActivity(i);
                }else{
                    Toast.makeText(this, R.string.err_com, Toast.LENGTH_LONG).show();
                }
            }else{
                search();
            }
        }else {
            finish();
        }
    }
    public void search(){
        Intent intent = new Intent(this, ParcoursSearchActivity.class);
        startActivity(intent);
    }
    private class GetTrajetTask extends AsyncTask<Void, Void, Void> {
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
                if(trajetId != null)
                    t = WEB.getCompleteTrajet(trajetId);
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
                Toast.makeText(DetailTrajetActivity.this, getString(R.string.err_invalidConnection), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class GetTrajetParcoursTask extends AsyncTask<Void, Void, Void> {
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
                if(trajetId != null)
                    tMatch = WEB.getTrajetParcours(trajetId);
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
                Toast.makeText(DetailTrajetActivity.this, getString(R.string.err_invalidConnection), Toast.LENGTH_SHORT).show();
            }
        }
    }
}
