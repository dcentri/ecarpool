package com.dicentrix.ecarpool.util;

import com.google.android.gms.location.places.AutocompletePrediction;

/**
 * Created by Akash on 11/25/2015.
 */
public class SearchAddress {
    public String place_id;
    public String description;
    public String fullDetails;

    public SearchAddress(String place_id, String description){
        this.place_id = place_id;
        this.description = description;
    }
    public SearchAddress(AutocompletePrediction place){
        place_id = place.getPlaceId();
        description = place.getDescription();
    }

    @Override
    public String toString(){
        return description;
    }
}
