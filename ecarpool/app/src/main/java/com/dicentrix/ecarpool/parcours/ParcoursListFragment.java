package com.dicentrix.ecarpool.parcours;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Created by Akash on 9/30/2015.
 */
public class ParcoursListFragment extends ListFragment {

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ArrayList<String> myString = new ArrayList<String>();

        String[] values = new String[] { "Beauport", "Duberger", "Parc industrielle",
                "Cegep Garneau", "Place Laurier", "Place Fleure de lys" };
        final ArrayAdapter adapter = new ArrayAdapter(getActivity(),
                android.R.layout.simple_list_item_1, values);
        setListAdapter(adapter);
    }

    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Intent i = new Intent(getActivity(), MapActivity.class);
        this.startActivity(i);

    }

}
