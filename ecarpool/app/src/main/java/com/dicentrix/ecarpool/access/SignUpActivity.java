package com.dicentrix.ecarpool.access;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.dicentrix.ecarpool.R;
import com.dicentrix.ecarpool.main.Dashboard;

/**
 * Created by Akash on 9/26/2015.
 */
public class SignUpActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_activity);
    }

    public void validateSignUp(View view){
        Intent i = new Intent(this, Dashboard.class);
        startActivity(i);
    }
}
