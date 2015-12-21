package com.dicentrix.ecarpool.misc;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.dicentrix.ecarpool.R;
import com.dicentrix.ecarpool.util.SearchAddress;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.AutocompletePredictionBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;


public class GetElementActivity extends Activity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.get_element_activity);
        EditText searchBox = (EditText) this.findViewById(R.id.search);
        ListView results = (ListView) this.findViewById(R.id.addressList);

        results.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                Intent returnIntent = new Intent();
                SearchAddress result = (SearchAddress) parent.getItemAtPosition(position);
                returnIntent.putExtra("place_id",result.place_id);
                returnIntent.putExtra("description",result.description);
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
        });

        if(searchBox.getText().length() > 0)
            findSuggestions(searchBox.getText().toString());
        searchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0 && s.length() %3 == 0){
                    findSuggestions(s.toString());
                }
            }
        });
        // Get the intent, verify the action and get the query
        Intent intent = getIntent();
        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    public void updateResultList(SearchAddress[] items){
        final ArrayAdapter adapter = new ArrayAdapter(this,
                android.R.layout.simple_list_item_1, items);
        ListView list = (ListView)this.findViewById(R.id.addressList);
        list.setAdapter(adapter);
    }

    public void findSuggestions(String queryString){
        LatLngBounds mBounds = new LatLngBounds(new LatLng(46.56169309108066, -71.73728942871094), new LatLng(47.16544237771324, -71.30470275878906));

        Places.GeoDataApi.getAutocompletePredictions(mGoogleApiClient, queryString,
                        mBounds, null).setResultCallback(new ResultCallback<AutocompletePredictionBuffer>() {
                    @Override
                    public void onResult(AutocompletePredictionBuffer autocompletePredictions) {
                        SearchAddress[] items = new SearchAddress[autocompletePredictions.getCount()];
                        for(int i =0; i < autocompletePredictions.getCount(); i++)
                        {
                            items[i] = new SearchAddress(autocompletePredictions.get(i));
                        }
                        updateResultList(items);
                        Log.d("Elements", String.valueOf(autocompletePredictions.getCount()));
                        autocompletePredictions.release();
                    }
                });
    }
    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}
