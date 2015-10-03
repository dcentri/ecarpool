package com.dicentrix.ecarpool.access;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.dicentrix.ecarpool.R;
import com.dicentrix.ecarpool.user.UserTypeActivity;

/**
 * Created by Akash on 2015-09-20.
 */
public class ConnectionActivity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.connection_activity);
    }

    public void validateConnection(View view){
        Intent i = new Intent(this, UserTypeActivity.class);
        startActivity(i);
    }

    public void signUp(View view){
        Intent i = new Intent(this, SignUpActivity.class);
        startActivity(i);
    }
}
