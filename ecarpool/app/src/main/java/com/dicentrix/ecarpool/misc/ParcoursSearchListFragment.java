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
import android.widget.Toast;

import com.dicentrix.ecarpool.R;
import com.dicentrix.ecarpool.access.ConnectionActivity;
import com.dicentrix.ecarpool.parcours.DetailActivity;
import com.dicentrix.ecarpool.parcours.Parcours;
import com.dicentrix.ecarpool.parcours.Trajet;
import com.dicentrix.ecarpool.user.IUserDAO;
import com.dicentrix.ecarpool.user.User;
import com.dicentrix.ecarpool.user.UserDAO;
import com.dicentrix.ecarpool.util.AddressDAO;
import com.dicentrix.ecarpool.util.FragmentHelper;
import com.dicentrix.ecarpool.util.IAddressDAO;
import com.dicentrix.ecarpool.util.WEB;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Akash on 9/30/2015.
 */
public class ParcoursSearchListFragment extends ListFragment {
    public static String ELEMENTYPE = "Element";
    public static String ID = "ID";
    private final String TAG = this.getClass().getSimpleName();
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
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.parcourslist_fragement, container, false);
        rootView.findViewById(R.id.txtNoItems).setVisibility(View.GONE);
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
            actionBar.getTabAt(0).setText(R.string.lbl_published_routes);
        }
        if(trajs != null || parcs != null){
            updateList();
        }else{
            if(isDriver){
                trajs = new ArrayList<>();
                new GetTrajetsTask().execute((Void)null);
            }else
            {
                parcs = new ArrayList<>();
                new GetParcoursTask().execute((Void) null);
            }
        }

        return rootView;
    }

    public void updateList(){
        if(parcs != null && parcs.size() > 0) {
            setListAdapter(FragmentHelper.getListAdapter(FragmentHelper.ParcoursListe(parcs), getActivity()));
        }else if(trajs != null && trajs.size() > 0){
            setListAdapter(FragmentHelper.getListAdapter(FragmentHelper.TrajetsListe(trajs), getActivity()));
        }else {
            getView().findViewById(R.id.txtNoItems).setVisibility(View.VISIBLE);
        }
    }

    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Intent i = new Intent(getActivity(), DetailActivity.class);
        if(isDriver){
            i.putExtra(ELEMENTYPE, "TRAJET");
            i.putExtra(ID, trajs.get(position).remoteId);
        }else {
            i.putExtra(ELEMENTYPE, "PARCOURS");
            i.putExtra(ID, parcs.get(position).remoteId);
        }
        this.startActivity(i);

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
                //Trouver tous les parcours
                trajs = WEB.batchFillTrajetAdresses(WEB.getAllTrajets());
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
                parcs = WEB.getAllParcoursWithTrajets();
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
}
