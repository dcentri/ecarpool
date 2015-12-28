package com.dicentrix.ecarpool.util;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import com.dicentrix.ecarpool.R;
import com.dicentrix.ecarpool.parcours.DetailActivity;
import com.dicentrix.ecarpool.parcours.Parcours;
import com.dicentrix.ecarpool.parcours.Trajet;
import com.dicentrix.ecarpool.user.User;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Akash on 12/25/2015.
 */
public class ChooseElementFragment extends DialogFragment {
    public String element;
    public User user;
    public Parcours p = new Parcours();
    public List<Parcours> listeP;
    public Trajet t;
    public List<Trajet> listeT;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Where we track the selected items
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        String title = element.equals("TRAJET") ? getString(R.string.lbl_my_route_choice) : getString(R.string.lbl_my_trajet_choice);
        //SimpleAdapter adapter = FragmentHelper.getListAdapter( element.equals("TRAJET") ? FragmentHelper.ParcoursListe(listeP): FragmentHelper.TrajetsListe(listeT), getActivity());
        ArrayList<Map<String, String>> curr = element.equals("TRAJET") ? FragmentHelper.ParcoursListe(listeP) : FragmentHelper.TrajetsListe(listeT);
        if(curr.size() < 1){
            title =  element.equals("TRAJET") ? getString(R.string.lbl_no_item_parcours) :getString(R.string.lbl_no_item_trajet) ;
        }
        final ArrayAdapter<Map<String, String>> adapter = new ChoiceAdapter(getActivity(),R.layout.choice_list,0,curr);
        // Set the dialog title
        builder.setTitle(title)
                // Specify the list array, the items to be selected by default (null for none),
                // and the listener through which to receive callbacks when items are selected
                .setSingleChoiceItems(adapter, 0, null)
                        // Set the action buttons
                .setPositiveButton(R.string.lbl_request, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Envoyer la requête
                        //new SendRequestTask().execute((Void) null);
                        int pos = ((ChoiceAdapter) adapter).mSelectedPosition;
                        ((DetailActivity)getActivity()).doPositiveClick(pos);
                    }
                })
                .setNegativeButton(R.string.lbl_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //L'utilisateur a annulé la requête
                    }
                });


        return builder.create();
    }
}
