package com.dicentrix.ecarpool.user;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.EditText;
import android.widget.TextView;

import com.dicentrix.ecarpool.R;
import com.dicentrix.ecarpool.access.ConnectionActivity;
import com.dicentrix.ecarpool.util.AddressDAO;
import com.dicentrix.ecarpool.util.IAddressDAO;

/**
 * Created by Akash on 9/26/2015.
 */
public class ProfilActivity extends Activity {

    IUserDAO db = new UserDAO(this);
    IAddressDAO dbAdresse = new AddressDAO(this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profil_activity);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        User user = db.getById(prefs.getInt(ConnectionActivity.USER_ID, 0));
        populateZones(user);
    }

    public void populateZones(User u){
        ((TextView) this.findViewById(R.id.lblUserFirstName)).setText(u.getFirstName());
        ((TextView) this.findViewById(R.id.lblUserName)).setText(u.getLastName());
        ((TextView) this.findViewById(R.id.lblUserTelephone)).setText(u.getPhone());
        ((TextView) this.findViewById(R.id.lblUserEmail)).setText(u.getEmail());
        if(u.getGender() == 'F')
            ((TextView) this.findViewById(R.id.lblGender)).setText(R.string.lbl_female);

        ((TextView) this.findViewById(R.id.lblUserCivicNo)).setText(u.getAddress().getCivicNo());
        ((TextView) this.findViewById(R.id.lblUserRoute)).setText(u.getAddress().getRouteName());
        ((TextView) this.findViewById(R.id.lblUserAppartment)).setText(u.getAddress().getAppart());
        ((TextView) this.findViewById(R.id.lblUserPostalCode)).setText(u.getAddress().getPostalCode());
    }

}
