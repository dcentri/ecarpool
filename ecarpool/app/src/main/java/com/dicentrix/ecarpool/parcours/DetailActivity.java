package com.dicentrix.ecarpool.parcours;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.dicentrix.ecarpool.R;
import com.dicentrix.ecarpool.access.ConnectionActivity;
import com.dicentrix.ecarpool.misc.ParcoursSearchActivity;
import com.dicentrix.ecarpool.misc.ParcoursSearchListFragment;
import com.dicentrix.ecarpool.user.IUserDAO;
import com.dicentrix.ecarpool.user.User;
import com.dicentrix.ecarpool.user.UserDAO;
import com.dicentrix.ecarpool.util.Address;
import com.dicentrix.ecarpool.util.AddressDAO;
import com.dicentrix.ecarpool.util.IAddressDAO;
import com.dicentrix.ecarpool.util.JsonParser;
import com.dicentrix.ecarpool.util.SearchAddress;
import com.dicentrix.ecarpool.util.WEB;
import com.google.android.gms.maps.model.LatLng;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.json.JSONObject;

import java.math.RoundingMode;
import java.net.URI;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by Akash on 11/25/2015.
 */
public class DetailActivity extends FragmentActivity {

    private final String TAG = this.getClass().getSimpleName();
    private HttpClient m_ClientHttp = new DefaultHttpClient();
    Parcours p = new Parcours();
    Trajet t = new Trajet();
    float km;
    IUserDAO db = new UserDAO(this);
    IAddressDAO dbAdresse = new AddressDAO(this);
    User user;
    String elementId;
    String element;
    boolean isDriver;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_activity);
        Intent intent = getIntent();
        element = intent.getStringExtra(ParcoursSearchListFragment.ELEMENTYPE);
        elementId = intent.getStringExtra(ParcoursSearchListFragment.ID);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        user = db.getById(prefs.getInt(ConnectionActivity.USER_ID, 0));
        isDriver = user.getType() == User.UserType.DRIVER;
    }

    public  void setFields(){
        try{
            if(t != null){
                ((TextView) this.findViewById(R.id.txtDeparture)).setText(t.getDepart().toString());
                ((TextView) this.findViewById(R.id.txtArrival)).setText(t.getDestination().toString());
                DecimalFormat df = new DecimalFormat("#.####");
                df.setRoundingMode(RoundingMode.CEILING);
                ((TextView) this.findViewById(R.id.txtKM)).setText(df.format(km));
                ((TextView) this.findViewById(R.id.txtDriver)).setText(user.getFirstName() +", "+ user.getLastName());
                if(!isDriver){
                    ((TextView)this.findViewById(R.id.lblUserType)).setText(R.string.lbl_passenger);
                    ((EditText)this.findViewById(R.id.txtNbPlaces)).setVisibility(View.INVISIBLE);
                    ((EditText)this.findViewById(R.id.txtPrice)).setVisibility(View.INVISIBLE);
                }
            }
        }catch (Exception e){}
    }

    public void chooseElement(View v) {
        Intent i;
        i = new Intent(this, DetailActivity.class);
        startActivityForResult(i, 1);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Button btn = null;
        SearchAddress result = null;
        if(resultCode == Activity.RESULT_OK){
            result = new SearchAddress(data.getStringExtra("place_id"), data.getStringExtra("description"));
            Log.d("Youpi !!!! :::: Got it ", result.place_id + result.description);
            new SendRequestTask().execute((Void) null);
        }
        if (resultCode == Activity.RESULT_CANCELED) {
            //Write your code if there's no result
        }
        switch (requestCode)
        {
            case 1:
                btn = (Button) this.findViewById(R.id.btnDeparture);
                break;
        }
        if(btn != null && result != null)
            btn.setText(result.description);
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
                URI uri;
                if(element == "TRAJET")
                    uri = new URI("https", WEB.URL, WEB.GET_TRAJET(elementId), null, null);
                else
                    uri = new URI("https", WEB.URL, WEB.GET_PARCOURS(elementId), null, null);
                HttpPost requetePost = new HttpPost(uri);

                //String body = m_ClientHttp.execute(requeteGet, new BasicResponseHandler());
                //Log.i(TAG, "Reçu (PUT) : " + body);

                JSONObject obj = JsonParser.serialiseTrajet(t);
                requetePost.setEntity(new StringEntity(obj.toString(),HTTP.UTF_8));
                requetePost.addHeader("Content-Type", "application/json");

                String body = m_ClientHttp.execute(requetePost, new BasicResponseHandler());
                Trajet tmpTraj = JsonParser.deserialiseTrajet(new JSONObject(body));
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
            setProgressBarIndeterminateVisibility(false);

            if (m_Exp == null) {
                Toast.makeText(DetailActivity.this, getString(R.string.succ_trajet_creation), Toast.LENGTH_LONG).show();
                // Rechargement de la liste des personnes.
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // This method will be executed once the timer is over
                        // Start your app main activity
                        Intent i = new Intent(DetailActivity.this, ParcoursActivity.class);
                        startActivity(i);
                        // close this activity
                        finish();
                    }
                }, Toast.LENGTH_LONG + 50);
            } else {
                Log.e(TAG, "Erreur lors de l'ajout ou de la modification de la personne (PUT)", m_Exp);
                Toast.makeText(DetailActivity.this, getString(R.string.err_invalidConnection), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class SendRequestTask extends AsyncTask<Void, Void, Void> {
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
                URI uri = new URI("https", WEB.URL, WEB.PARCOURS, null, null);
                HttpPost requetePost = new HttpPost(uri);

                //String body = m_ClientHttp.execute(requeteGet, new BasicResponseHandler());
                //Log.i(TAG, "Reçu (PUT) : " + body);

                JSONObject obj = JsonParser.serialiseParcours(p);
                requetePost.setEntity(new StringEntity(obj.toString(),HTTP.UTF_8));
                requetePost.addHeader("Content-Type", "application/json");

                String body = m_ClientHttp.execute(requetePost, new BasicResponseHandler());
                Parcours tmpParc = JsonParser.deserialiseParcours(new JSONObject(body));
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
            setProgressBarIndeterminateVisibility(false);

            if (m_Exp == null) {
                Toast.makeText(DetailActivity.this, getString(R.string.succ_parcours_creation), Toast.LENGTH_LONG).show();
                // Rechargement de la liste des personnes.
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // This method will be executed once the timer is over
                        // Start your app main activity
                        Intent i = new Intent(DetailActivity.this, ParcoursActivity.class);
                        startActivity(i);
                        // close this activity
                        finish();
                    }
                }, Toast.LENGTH_LONG + 50);
            } else {
                Log.e(TAG, "Erreur lors de l'ajout ou de la modification de la personne (PUT)", m_Exp);
                Toast.makeText(DetailActivity.this, getString(R.string.err_invalidConnection), Toast.LENGTH_SHORT).show();
            }
        }
    }
}
