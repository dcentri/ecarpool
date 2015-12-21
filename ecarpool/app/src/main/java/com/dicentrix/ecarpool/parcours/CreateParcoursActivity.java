package com.dicentrix.ecarpool.parcours;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.dicentrix.ecarpool.R;
import com.dicentrix.ecarpool.misc.SearchActivity;
import com.dicentrix.ecarpool.util.JsonParser;
import com.dicentrix.ecarpool.util.SearchAddress;
import com.dicentrix.ecarpool.util.WEB;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.net.URI;

/**
 * Created by Akash on 9/26/2015.
 */
public class CreateParcoursActivity extends FragmentActivity implements OnMapReadyCallback {
    public static String DEP_PLACE = "departurePlaceId";
    public static String ARR_PLACE = "arrivalPlaceId";
    private HttpClient m_ClientHttp = new DefaultHttpClient();
    private final String TAG = this.getClass().getSimpleName();
    private GoogleMap mMap;
    private int requestId;
    SearchAddress departure;
    SearchAddress arrival;
    private static final LatLng GARNEAU = new LatLng(46.792028, -71.264176);

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.createroute_activity);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        updateMap();
    }
    private void updateMap(){
        LatLng markerDepPlace = new LatLng(0,0);
        LatLng markerArrivPlace = new LatLng(0,0);
        mMap.clear();
        try{
            if(arrival != null || departure != null ){
                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                if (departure != null){
                    if(departure.fullDetails != null){
                        markerDepPlace =JsonParser.deseriliaseLatLong(new JSONObject(departure.fullDetails));
                        Marker departureM = mMap.addMarker(new MarkerOptions().position(markerDepPlace).title(departure.description));
                        builder.include(departureM.getPosition());
                        //mMap.addMarker(new MarkerOptions().position(JsonParser.deseriliserLatLong(new JSONObject(departure.fullDetails))).title("Garneau"));
                    }
                }


                if (arrival != null){
                    if(arrival.fullDetails != null){
                        markerArrivPlace = JsonParser.deseriliaseLatLong(new JSONObject(arrival.fullDetails));
                        Marker arrivalM = mMap.addMarker(new MarkerOptions().position(markerArrivPlace).title(arrival.description));
                        builder.include(arrivalM.getPosition());
                    }
                }
                if(arrival != null && departure != null ){
                    Polyline line = mMap.addPolyline(new PolylineOptions()
                            .add(markerDepPlace, markerArrivPlace)
                            .width(5)
                            .color(Color.RED));
                }

                LatLngBounds bounds = builder.build();
                int padding = 15; // offset from edges of the map in pixels
                CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                mMap.animateCamera(cu);
            }else{
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(GARNEAU, 15));
            }
        }
        catch (Exception e){
        }
    }
    private void demandeAdress(int requestCode){
        Intent i;
        i = new Intent(this, SearchActivity.class);
        startActivityForResult(i, requestCode);
    }
    public void departure(View view){
        requestId = 1;
        demandeAdress(requestId);
    }
    public void arrival(View view){
        requestId = 2;
        demandeAdress(requestId);
    }
    public void validateDepartArrival(View view){
        if(departure == null || arrival == null){
            Toast.makeText(this, getString(R.string.lbl_invalideAddressDepartureArrival), Toast.LENGTH_LONG ).show();
        }
        else{
            continueForm();
        }
    }
    private void continueForm(){
        Intent i = new Intent(this, CreateParcourForm2Activity.class);
        i.putExtra(DEP_PLACE, departure.fullDetails);
        i.putExtra(ARR_PLACE, arrival.fullDetails);
        startActivity(i);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Button btn = null;
        SearchAddress result = null;
        if(resultCode == Activity.RESULT_OK){
            result = new SearchAddress(data.getStringExtra("place_id"), data.getStringExtra("description"));
            Log.d("Youpi !!!! :::: Got it ", result.place_id + result.description);
            new GetGeoLatLongTask().execute((Void) null);
        }
        if (resultCode == Activity.RESULT_CANCELED) {
            //Write your code if there's no result
        }
        switch (requestCode)
        {
            case 1:
                btn = (Button) this.findViewById(R.id.btnDeparture);
                departure = result;
                break;
            case 2:
                btn = (Button) this.findViewById(R.id.btnArrival);
                arrival = result;
                break;
        }
        if(btn != null && result != null)
            btn.setText(result.description);
    }

    private class GetGeoLatLongTask extends AsyncTask<Void, Void, Void> {
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
                URI uri = new URI("https", "maps.googleapis.com", "/maps/api/geocode/json", "address="+ (requestId == 1 ?departure.description: arrival.description) + "&key=" +getString(R.string.google_maps_key_server), null);
                HttpGet requeteGet = new HttpGet(uri);

                //String body = m_ClientHttp.execute(requeteGet, new BasicResponseHandler());
                //Log.i(TAG, "Reçu (PUT) : " + body);
                requeteGet.addHeader("Content-Type", "application/json");
                requeteGet.addHeader("Accept-Charset", "UTF-8");

                String body = m_ClientHttp.execute(requeteGet, new BasicResponseHandler());
                JSONObject fullAddresse = new JSONObject(body);
                if((!fullAddresse.has("errorMessage")) && ((String)fullAddresse.get("status")).equals("OK") ){
                    if(requestId == 1){
                        departure.fullDetails = new String(body);
                    }else if (requestId == 2){
                        arrival.fullDetails = new String(body);
                    }
                }
                Log.i(TAG, "Put terminé avec succès");
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
                updateMap();
            } else {
                Log.e(TAG, getString(R.string.err_com), m_Exp);
                Toast.makeText(CreateParcoursActivity.this, getString(R.string.err_com), Toast.LENGTH_SHORT).show();
            }
        }
    }
}
