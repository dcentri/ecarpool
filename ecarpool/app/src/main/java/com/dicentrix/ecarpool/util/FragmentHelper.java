package com.dicentrix.ecarpool.util;

import android.app.Activity;
import android.widget.SimpleAdapter;

import com.dicentrix.ecarpool.parcours.Parcours;
import com.dicentrix.ecarpool.parcours.Trajet;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Akash on 12/25/2015.
 */
public class FragmentHelper {
    public static SimpleAdapter getListAdapter(ArrayList<Map<String, String>> list, Activity currentActivity){
        String[] de = {"Heure", "Depart"};
        int[] a = {android.R.id.text1, android.R.id.text2};
        SimpleAdapter adapter = new SimpleAdapter(currentActivity, list, android.R.layout.simple_list_item_2, de, a);
        return adapter;
    }
    public static ArrayList<Map<String, String>> TrajetsListe(List<Trajet> allTrajets)
    {
        ArrayList<Map<String, String>> res = new ArrayList<Map<String, String>>();
        for (Trajet t : allTrajets) {
            HashMap<String, String> item = new HashMap<String, String>();
            item.put("Heure", getDate(t.getDepartDateTime()));
            item.put("Depart", "Départ : " + t.getDepart().toString());
            res.add(item);
        }
        return res;
    }
    public static ArrayList<Map<String, String>> ParcoursListe(List<Parcours> allParcours)
    {
        HashMap<String, String> item;
        ArrayList<Map<String, String>> res = new ArrayList<Map<String, String>>();
        for (Parcours p : allParcours) {
            item = new HashMap<String, String>();
            item.put("Heure", getDate(p.getDefaultTrajet().getDepartDateTime()));
            item.put("Depart", "Départ : " + p.getDefaultTrajet().getDepart().toString());
            res.add(item);
        }
        return res;
    }

    public static String getDate(Date date){
        return new SimpleDateFormat("kk:mm EEE, MMM d, ''yy").format(date);
    }
}
