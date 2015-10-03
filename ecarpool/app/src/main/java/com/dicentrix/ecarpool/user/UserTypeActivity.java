package com.dicentrix.ecarpool.user;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.dicentrix.ecarpool.R;
import com.dicentrix.ecarpool.main.Dashboard;

/**
 * Created by Akash on 9/26/2015.
 */
public class UserTypeActivity extends Activity {
    private char userType;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fate_activity);
    }

    public void setPassenger(View v){
        this.userType = 'P';
        getConnection();
    }

    public void setDriver(View v){
        this.userType = 'C';
        getConnection();
    }
    private void getConnection(){
        Intent i = new Intent(this, Dashboard.class);

        startActivity(i);
    }

}
