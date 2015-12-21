package com.dicentrix.ecarpool.parcours;

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
import com.dicentrix.ecarpool.user.IUserDAO;
import com.dicentrix.ecarpool.user.User;
import com.dicentrix.ecarpool.user.UserDAO;
import com.dicentrix.ecarpool.util.Address;
import com.dicentrix.ecarpool.util.AddressDAO;
import com.dicentrix.ecarpool.util.IAddressDAO;
import com.dicentrix.ecarpool.util.JsonParser;
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
public class CreateParcourForm2Activity extends FragmentActivity implements TimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener{

    private final String TAG = this.getClass().getSimpleName();
    private HttpClient m_ClientHttp = new DefaultHttpClient();
    private Address arrivalAddre;
    private Address departureAddre;
    public static Calendar departureDate;
    Parcours p = new Parcours();
    Trajet t = new Trajet();
    float km;
    IUserDAO db = new UserDAO(this);
    IAddressDAO dbAdresse = new AddressDAO(this);
    User user;
    boolean isDriver;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.createroute_f2_activity);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        user = db.getById(prefs.getInt(ConnectionActivity.USER_ID, 0));
        isDriver = user.getType() == User.UserType.DRIVER;
        updateDate();
        updateTime();
        try{
            arrivalAddre = JsonParser.deserialiseGoogleAddresse(new JSONObject(this.getIntent().getStringExtra(CreateParcoursActivity.ARR_PLACE)));
            departureAddre = JsonParser.deserialiseGoogleAddresse(new JSONObject(this.getIntent().getStringExtra(CreateParcoursActivity.DEP_PLACE)));
            ((TextView) this.findViewById(R.id.txtDeparture)).setText(departureAddre.toString());
            ((TextView) this.findViewById(R.id.txtArrival)).setText(arrivalAddre.toString());
            km = getDistance(departureAddre, arrivalAddre);
            DecimalFormat df = new DecimalFormat("#.####");
            df.setRoundingMode(RoundingMode.CEILING);
            ((TextView) this.findViewById(R.id.txtKM)).setText(df.format(km));
            ((TextView) this.findViewById(R.id.txtDriver)).setText(user.getFirstName() +", "+ user.getLastName());
            if(!isDriver){
                ((TextView)this.findViewById(R.id.lblUserType)).setText(R.string.lbl_passenger);
                ((EditText)this.findViewById(R.id.txtNbPlaces)).setVisibility(View.INVISIBLE);
                ((EditText)this.findViewById(R.id.txtPrice)).setVisibility(View.INVISIBLE);
            }
        }catch (Exception e){}
    }
    private float getDistance(Address departure, Address arrival){
        LatLng departLatLng = new LatLng(Double.parseDouble(departure.getLatCoord()), Double.parseDouble(departure.getLongCoord()));
        LatLng arrivalLatLng = new LatLng(Double.parseDouble(arrival.getLatCoord()), Double.parseDouble(arrival.getLongCoord()));
        return distFrom((float)departLatLng.latitude,(float) departLatLng.longitude, (float)arrivalLatLng.latitude,(float)arrivalLatLng.longitude);
    }

    public static float distFrom(float lat1, float lng1, float lat2, float lng2) {
        double earthRadius = 6371; //en KM
        double dLat = Math.toRadians(lat2-lat1);
        double dLng = Math.toRadians(lng2-lng1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLng/2) * Math.sin(dLng/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        float dist = (float) (earthRadius * c);

        return dist;
    }
    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getFragmentManager(), "datePicker");


    }
    public void updateDate(){
        if(departureDate != null){
            Date time = departureDate.getTime();
            SimpleDateFormat frmt = new SimpleDateFormat("EEE, MMM d, ''yy");
            ((Button)this.findViewById(R.id.btnDate)).setText(frmt.format(time));
        }
    }
    public void showTimePickerDialog(View v) {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getFragmentManager(), "timePicker");

    }
    public void updateTime(){
        if(departureDate != null){
            Date time = departureDate.getTime();
            SimpleDateFormat frmt = new SimpleDateFormat("HH:mm");
            ((Button) this.findViewById(R.id.btnTime)).setText(frmt.format(time));
        }
    }
    public void createParcours(View v) {
        EditText txtnbPlaces = (EditText)this.findViewById(R.id.txtNbPlaces);
        EditText txtprice = (EditText)this.findViewById(R.id.txtPrice);
        String frequecy =( (RadioButton) this.findViewById(R.id.rbWeekly)).isChecked()?"WEEKLY": "ONCE";
        Float price = tryParseFloat(txtprice.getText().toString());
        int places = tryParseInt(txtnbPlaces.getText().toString());
        if(departureDate == null){
            Toast.makeText(this, getString(R.string.err_no_date_time), Toast.LENGTH_LONG).show();
        }else if(price == null && isDriver){
            Toast.makeText(this, getString(R.string.err_no_price), Toast.LENGTH_LONG).show();
        }else if(places  < 2 && isDriver){
            Toast.makeText(this, getString(R.string.err_invalide_places), Toast.LENGTH_LONG).show();
        }else{
            if(arrivalAddre != null && departureAddre != null && user != null){
                Trajet trj = new Trajet(user,departureAddre, arrivalAddre, departureDate.getTime(), new FrequenceTrajet(frequecy));
                if(isDriver){
                    p = new Parcours(places, price, km, trj);
                    new CreateParcoursTask().execute((Void) null);
                }else {
                    t = trj;
                    new CreateTrajetTask().execute((Void)null);
                }
            }
            else
                Toast.makeText(this, getString(R.string.err_parcours_creation_failure), Toast.LENGTH_LONG).show();
        }
    }

    private Float tryParseFloat(String flt){
        try{
            return Float.parseFloat(flt);
        }catch (Exception e){
            return null;
        }
    }
    private int tryParseInt(String number){
        try{
            return Integer.parseInt(number);
        }catch (Exception e){
            return  -1;
        }
    }
    public void onDateSet(DatePicker view, int year, int month, int day) {
        // Set the date chosen by the user
        if(departureDate == null)
            departureDate = new GregorianCalendar();
        departureDate.set(Calendar.YEAR, year);
        departureDate.set(Calendar.MONTH, month);
        departureDate.set(Calendar.DAY_OF_MONTH, day);
        updateDate();
    }

    public static class DatePickerFragment extends DialogFragment
             {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), (CreateParcourForm2Activity)getActivity(), year, month, day);
        }
    }

    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        // Do something with the time chosen by the user
        if(departureDate == null)
            departureDate = new GregorianCalendar();
        departureDate.set(Calendar.HOUR_OF_DAY, hourOfDay);
        departureDate.set(Calendar.MINUTE, minute);
        updateTime();
    }

    public static class TimePickerFragment extends DialogFragment
             {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), (CreateParcourForm2Activity)getActivity(), hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }
    }

    private class CreateTrajetTask extends AsyncTask<Void, Void, Void> {
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
                URI uri = new URI("https", WEB.URL, WEB.TRAJETS, null, null);
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
                Toast.makeText(CreateParcourForm2Activity.this, getString(R.string.succ_trajet_creation), Toast.LENGTH_LONG).show();
                // Rechargement de la liste des personnes.
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // This method will be executed once the timer is over
                        // Start your app main activity
                        Intent i = new Intent(CreateParcourForm2Activity.this, ParcoursActivity.class);
                        startActivity(i);
                        // close this activity
                        finish();
                    }
                }, Toast.LENGTH_LONG + 50);
            } else {
                Log.e(TAG, "Erreur lors de l'ajout ou de la modification de la personne (PUT)", m_Exp);
                Toast.makeText(CreateParcourForm2Activity.this, getString(R.string.err_invalidConnection), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class CreateParcoursTask extends AsyncTask<Void, Void, Void> {
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
                Toast.makeText(CreateParcourForm2Activity.this, getString(R.string.succ_parcours_creation), Toast.LENGTH_LONG).show();
                // Rechargement de la liste des personnes.
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // This method will be executed once the timer is over
                        // Start your app main activity
                        Intent i = new Intent(CreateParcourForm2Activity.this, ParcoursActivity.class);
                        startActivity(i);
                        // close this activity
                        finish();
                    }
                }, Toast.LENGTH_LONG + 50);
            } else {
                Log.e(TAG, "Erreur lors de l'ajout ou de la modification de la personne (PUT)", m_Exp);
                Toast.makeText(CreateParcourForm2Activity.this, getString(R.string.err_invalidConnection), Toast.LENGTH_SHORT).show();
            }
        }
    }
}
