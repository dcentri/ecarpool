package com.dicentrix.ecarpool.misc;


import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.dicentrix.ecarpool.R;
import com.dicentrix.ecarpool.access.ConnectionActivity;
import com.dicentrix.ecarpool.parcours.DetailActivity;
import com.dicentrix.ecarpool.parcours.MapsActivity;
import com.dicentrix.ecarpool.parcours.Parcours;
import com.dicentrix.ecarpool.parcours.Trajet;
import com.dicentrix.ecarpool.user.IUserDAO;
import com.dicentrix.ecarpool.user.User;
import com.dicentrix.ecarpool.user.UserDAO;
import com.dicentrix.ecarpool.util.Address;
import com.dicentrix.ecarpool.util.AddressDAO;
import com.dicentrix.ecarpool.util.IAddressDAO;
import com.dicentrix.ecarpool.util.JsonParser;
import com.dicentrix.ecarpool.util.WEB;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Akash on 9/30/2015.
 */
public class ParcoursSearchListFragment extends ListFragment {
    public static String ELEMENTYPE = "Element";
    public static String ID = "ID";
    private final String TAG = this.getClass().getSimpleName();
    private HttpClient m_ClientHttp = new DefaultHttpClient();
    List<Parcours> parcs;
    List<Trajet> trajs;
    IUserDAO db;
    IAddressDAO dbAdresse;
    User user;
    boolean isDriver;
    private ActionBar actionBar;
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        db = new UserDAO(activity);
        db = new UserDAO(activity);
        dbAdresse =new AddressDAO(activity);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        int userId = prefs.getInt(ConnectionActivity.USER_ID, 0);
        user = db.getById(userId);
        isDriver = user.getType() == User.UserType.DRIVER;
        if(!isDriver){
            actionBar = activity.getActionBar();
            actionBar.getTabAt(0).setText(R.string.lbl_published_routes);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if(isDriver){
            trajs = new ArrayList<Trajet>();
            new GetTrajetsTask().execute((Void)null);
        }else
        {
            parcs = new ArrayList<Parcours>();
            new GetParcoursTask().execute((Void) null);
        }

    }
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.parcourslist_fragement, container, false);
        ((TextView)rootView.findViewById(R.id.txtNoItems)).setVisibility(View.INVISIBLE);
        return rootView;
    }

    public void updateList(){
        if(parcs != null && parcs.size() > 0) {
            ArrayList<Map<String, String>> list = ParcoursListe();
            String[] de = {"Heure", "Depart"};
            int[] a = {android.R.id.text1, android.R.id.text2};
            SimpleAdapter adapter = new SimpleAdapter(getActivity(), list, android.R.layout.simple_list_item_2, de, a);
            setListAdapter(adapter);
        }else if(trajs != null && trajs.size() > 0){
            ArrayList<Map<String, String>> list = TrajetsListe();
            String[] de = {"Heure", "Depart"};
            int[] a = {android.R.id.text1 , android.R.id.text2};
            SimpleAdapter adapter = new SimpleAdapter( getActivity(), list , android.R.layout.simple_list_item_2 , de, a );
            setListAdapter(adapter);
        }else {
            ((TextView)getView().findViewById(R.id.txtNoItems)).setVisibility(View.VISIBLE);
        }
    }
    public ArrayList<Map<String, String>> TrajetsListe()
    {
        ArrayList<Map<String, String>> res = new ArrayList<Map<String, String>>();
        for (Trajet t : trajs) {
            HashMap<String, String> item = new HashMap<String, String>();
            item.put("Heure", getDate(t.getDepartDateTime()));
            item.put("Depart", "Départ : " + t.getDepart().toString());
            res.add(item);
        }
        return res;
    }

    public ArrayList<Map<String, String>> ParcoursListe()
    {
        ArrayList<Map<String, String>> res = new ArrayList<Map<String, String>>();
        for (Parcours p : parcs) {
            HashMap<String, String> item = new HashMap<String, String>();
            item.put("Heure", getDate(p.getDefaultTrajet().getDepartDateTime()));
            item.put("Depart", "Départ : " + p.getDefaultTrajet().getDepart().toString());
            res.add(item);
        }
        return res;
    }
    private String getDate(Date date){
        return new SimpleDateFormat("kk:mm EEE, MMM d, ''yy").format(date);
    }
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        /*Intent i = new Intent(getActivity(), DetailActivity.class);
        if(isDriver){
            i.putExtra(ELEMENTYPE, "TRAJET");
            i.putExtra(ID, trajs.get(position).remoteId);
        }else {
            i.putExtra(ELEMENTYPE, "PARCOURS");
            i.putExtra(ID, parcs.get(position).remoteId);
        }
        this.startActivity(i);*/

    }

    private class GetTrajetsTask extends AsyncTask<Void, Void, Void> {
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
                //Trouver tous les parcours
                URI uri = new URI("https", WEB.URL, WEB.TRAJETS, null, null);
                HttpGet requeteGet = new HttpGet(uri);
                requeteGet.addHeader("Content-Type", "application/json");

                String body = m_ClientHttp.execute(requeteGet, new BasicResponseHandler());
                trajs = JsonParser.deserialiseTrajetsList(new JSONArray(body));
                trajs = cleanTrajets(trajs, login);
                //Informations de parcours
                for(Trajet t : trajs){
                    t.setDepart(getAddresse(t.remoteDepartureAdresse));
                    t.setDestination(getAddresse(t.remoteArrivalAdresse));
                }
                Log.i(TAG, "Get des parcours terminé avec succès");
            } catch (Exception e) {
                m_Exp = e;
            }
            return null;
        }
        private  List<Trajet> cleanTrajets(List<Trajet> trajets, String login){
            List<Trajet> res = new ArrayList<Trajet>();
            for(Trajet t : trajs){
                if(t.idAuthor !=  login){
                    res.add(t);
                }
            }
            return res;
        }
        private Address getAddresse(String id){
            try {

                URI uri = new URI("https", WEB.URL, WEB.GET_ADRESSE(id), null, null);
                HttpGet requeteGet = new HttpGet(uri);
                requeteGet.addHeader("Content-Type", "application/json");

                String body = m_ClientHttp.execute(requeteGet, new BasicResponseHandler());
                Address tempAdresse = JsonParser.deserialiseAddresse(new JSONObject(body));

                return tempAdresse;
            }catch (Exception e){
                throw new IllegalArgumentException();
            }
        }
        // Méthode exécutée SYNChrone après l'exécution de la tâche asynchrone.
        // Elle reçoit en paramètre la valeur de retour de "doInBackground".
        @Override
        protected void onPostExecute(Void unused) {

            if (m_Exp == null) {
                // Rechargement de la liste des parcours
                updateList();
            } else {
                Log.e(TAG, "Erreur lors de l'ajout ou de la modification de la personne (PUT)", m_Exp);
                Toast.makeText(getActivity(), getString(R.string.err_invalidConnection), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class GetParcoursTask extends AsyncTask<Void, Void, Void> {
        Exception m_Exp;

        // Méthode exécutée SYNChrone avant l'exécution de la tâche asynchrone.
        @Override
        protected void onPreExecute() {
        }

        // Méthode exécutée ASYNChrone: la tâche en tant que telle.
        @Override
        protected Void doInBackground(Void... unused) {

            try {
                //Trouver tous les parcours
                URI uri = new URI("https", WEB.URL, WEB.PARCOURS, null, null);
                HttpGet requeteGet = new HttpGet(uri);
                requeteGet.addHeader("Content-Type", "application/json");

                String body = m_ClientHttp.execute(requeteGet, new BasicResponseHandler());
                parcs = JsonParser.deserialiseParcoursList(new JSONArray(body));
                parcs = cleanParcours(parcs);
                //Informations de parcours
                for(Parcours p : parcs){
                    for(String trajet : p.remoteTrajetsIds){
                        uri = new URI("https", WEB.URL, WEB.GET_TRAJET(trajet), null, null);
                        requeteGet = new HttpGet(uri);
                        requeteGet.addHeader("Content-Type", "application/json");

                        body = m_ClientHttp.execute(requeteGet, new BasicResponseHandler());
                        Trajet tempTraj = JsonParser.deserialiseTrajet(new JSONObject(body));
                        tempTraj.setDepart(getAddresse(tempTraj.remoteDepartureAdresse));
                        tempTraj.setDestination(getAddresse(tempTraj.remoteArrivalAdresse));
                        p.addTrajet(tempTraj);
                    }
                }
                Log.i(TAG, "Get des parcours terminé avec succès");
            } catch (Exception e) {
                m_Exp = e;
            }
            return null;
        }

        private  List<Parcours> cleanParcours(List<Parcours> parcours){
            List<Parcours> res = new ArrayList<Parcours>();
            for(Parcours p : parcours){
                if(p.remoteTrajetsIds.length < p.getNbPlaces()){
                    res.add(p);
                }
            }
            return res;
        }

        private Address getAddresse(String id){
            try {

                URI uri = new URI("https", WEB.URL, WEB.GET_ADRESSE(id), null, null);
                HttpGet requeteGet = new HttpGet(uri);
                requeteGet.addHeader("Content-Type", "application/json");

                String body = m_ClientHttp.execute(requeteGet, new BasicResponseHandler());
                Address tempAdresse = JsonParser.deserialiseAddresse(new JSONObject(body));

                return tempAdresse;
            }catch (Exception e){
                throw new IllegalArgumentException();
            }
        }
        // Méthode exécutée SYNChrone après l'exécution de la tâche asynchrone.
        // Elle reçoit en paramètre la valeur de retour de "doInBackground".
        @Override
        protected void onPostExecute(Void unused) {

            if (m_Exp == null) {
                // Rechargement de la liste des parcours
                updateList();
            } else {
                Log.e(TAG, "Erreur lors de l'ajout ou de la modification de la personne (PUT)", m_Exp);
                Toast.makeText(getActivity(), getString(R.string.err_invalidConnection), Toast.LENGTH_SHORT).show();
            }
        }
    }
}
