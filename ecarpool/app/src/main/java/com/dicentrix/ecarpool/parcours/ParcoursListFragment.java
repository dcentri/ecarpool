package com.dicentrix.ecarpool.parcours;


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
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.dicentrix.ecarpool.R;
import com.dicentrix.ecarpool.access.ConnectionActivity;
import com.dicentrix.ecarpool.user.IUserDAO;
import com.dicentrix.ecarpool.user.User;
import com.dicentrix.ecarpool.user.UserDAO;
import com.dicentrix.ecarpool.util.Address;
import com.dicentrix.ecarpool.util.AddressDAO;
import com.dicentrix.ecarpool.util.FragmentHelper;
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
public class ParcoursListFragment extends ListFragment {
    private final String TAG = this.getClass().getSimpleName();
    public static String PARCOURS_DETAIL = "idParcours";
    public static String TRAJET_DETAIL = "idTrajet";
    public static String LOGIN = "userLogin";
    private HttpClient m_ClientHttp = new DefaultHttpClient();
    List<Parcours> parcs;
    List<Trajet> trajs;
    IUserDAO db;
    IAddressDAO dbAdresse;
    User user;
    private ActionBar actionBar;
    boolean isDriver;
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(user == null){
            db = new UserDAO(getActivity());
            dbAdresse =new AddressDAO(getActivity());
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
            int userId = prefs.getInt(ConnectionActivity.USER_ID, 0);
            user = db.getById(userId);
        }
        isDriver = user.getType() == User.UserType.DRIVER;
        if(!isDriver){
            actionBar = getActivity().getActionBar();
            actionBar.getTabAt(0).setText(R.string.lbl_my_trajet);
        }
        if(trajs != null || parcs != null){
            updateList();
        }else
        {
            if(isDriver){
                parcs = new ArrayList<>();
                new GetParcoursTask().execute((Void)null);
            }else
            {
                trajs = new ArrayList<>();
                new GetTrajetsTask().execute((Void)null);
            }
        }
        View rootView = inflater.inflate(R.layout.parcourslist_fragement, container, false);
        (rootView.findViewById(R.id.txtNoItems)).setVisibility(View.GONE);
        return rootView;
    }

    public void updateList(){
        if(parcs != null  && parcs.size() > 0){
            setListAdapter(FragmentHelper.getListAdapter(FragmentHelper.ParcoursListe(parcs), getActivity()));
        }else if(trajs != null && trajs.size() > 0 ) {
            setListAdapter(FragmentHelper.getListAdapter(FragmentHelper.TrajetsListe(trajs), getActivity()));
        }
        else {
            (getView().findViewById(R.id.txtNoItems)).setVisibility(View.GONE);
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
    private String getDate(Date date){
        return new SimpleDateFormat("kk:mm EEE, MMM d, ''yy").format(date);
    }
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        if(user.getType() == User.UserType.DRIVER){
            Intent i = new Intent(getActivity(), DetailParcoursActivity.class);
            i.putExtra(PARCOURS_DETAIL, parcs.get(position).remoteId);
            i.putExtra(LOGIN, user.getLogin());
            this.startActivity(i);
        }else{
            Intent i = new Intent(getActivity(), DetailTrajetActivity.class);
            i.putExtra(TRAJET_DETAIL,trajs.get(position).remoteId);
            i.putExtra(LOGIN, user.getLogin());
            this.startActivity(i);
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
                if(user != null)
                    parcs = WEB.getUserParcoursWithTrajets(user.getLogin());
                Log.i(TAG, "Get des parcours terminé avec succès");
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
                // Rechargement de la liste des parcours
                updateList();
            } else {
                Log.e(TAG, "Erreur lors de l'ajout ou de la modification de la personne (PUT)", m_Exp);
                Toast.makeText(getActivity(), getString(R.string.err_invalidConnection), Toast.LENGTH_SHORT).show();
            }
        }
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
                URI uri = new URI("https", WEB.URL, WEB.GET_USER_TRAJETS(login), null, null);
                HttpGet requeteGet = new HttpGet(uri);
                requeteGet.addHeader("Content-Type", "application/json");

                String body = m_ClientHttp.execute(requeteGet, new BasicResponseHandler());
                trajs = JsonParser.deserialiseTrajetsList(new JSONArray(body));

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
