package com.dicentrix.ecarpool.parcours;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.dicentrix.ecarpool.R;
import com.dicentrix.ecarpool.access.ConnectionActivity;
import com.dicentrix.ecarpool.misc.ParcoursSearchListFragment;
import com.dicentrix.ecarpool.user.IUserDAO;
import com.dicentrix.ecarpool.user.User;
import com.dicentrix.ecarpool.user.UserDAO;
import com.dicentrix.ecarpool.util.ChooseElementFragment;
import com.dicentrix.ecarpool.util.FragmentHelper;
import com.dicentrix.ecarpool.util.WEB;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;

/**
 * Created by Akash on 11/25/2015.
 */
public class DetailParcoursActivity extends FragmentActivity{

    public static String LOOP = "loop";
    private final String TAG = this.getClass().getSimpleName();
    Parcours p = new Parcours();
    IUserDAO db = new UserDAO(this);
    User user;
    String parcoursId;
    String login;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details_parcours_activity);
        Intent intent = getIntent();
        parcoursId = intent.getStringExtra(ParcoursListFragment.PARCOURS_DETAIL);
        login = intent.getStringExtra(ParcoursListFragment.LOGIN);
        new GetParcoursTask().execute((Void) null);
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
                ListView trajList = ((ListView)this.findViewById(R.id.list));
                trajList.setAdapter(FragmentHelper.getListAdapter(FragmentHelper.TrajetsListe(p.getTrajets()), this));
                trajList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        startTrajetDetails(position);
                    }
                });
            }
        }catch (Exception e){}
    }

    public void startTrajetDetails(int position){
        Intent i = new Intent(this, DetailTrajetActivity.class);
        i.putExtra(ParcoursListFragment.TRAJET_DETAIL, p.getTrajets().get(position).remoteId);
        i.putExtra(ParcoursListFragment.LOGIN, login);
        i.putExtra(this.LOOP, "true");
        this.startActivity(i);
    }
    private class GetParcoursTask extends AsyncTask<Void, Void, Void> {
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
                if(parcoursId != null)
                    p = WEB.setParcoursTrajets(WEB.getParcours(parcoursId));
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
                Toast.makeText(DetailParcoursActivity.this, getString(R.string.err_invalidConnection), Toast.LENGTH_SHORT).show();
            }
        }
    }
}
