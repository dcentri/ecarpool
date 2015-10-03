package com.dicentrix.ecarpool.parcours;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

/**
 * Created by Akash on 9/27/2015.
 */
public class NotificationFragment extends ListFragment{

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ArrayList<String> myString = new ArrayList<String>();

        String[] values = new String[] { "Demande pour beauport accépté", "Demande pour place Laurier refusé",
                "Demande pour Fleure de lys accépté" };
        final ArrayAdapter adapter = new ArrayAdapter(getActivity(),
                android.R.layout.simple_list_item_1, values);
        setListAdapter(adapter);
    }
}
