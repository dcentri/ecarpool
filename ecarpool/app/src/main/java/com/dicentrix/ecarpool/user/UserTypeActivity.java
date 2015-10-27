package com.dicentrix.ecarpool.user;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.dicentrix.ecarpool.R;
import com.dicentrix.ecarpool.access.ConnectionActivity;
import com.dicentrix.ecarpool.access.SignUpActivity;
import com.dicentrix.ecarpool.main.Dashboard;

/**
 * Created by Akash on 9/26/2015.
 */
public class UserTypeActivity extends Activity {
    User user;
    IUserDAO db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fate_activity);
        int id = this.getIntent().getIntExtra(SignUpActivity.USER_ID, 0);
        if (id == 0)
            startConnection();
        else
        {
            db = new UserDAO(this);
            user = db.getById(id);
        }
    }

    public void setPassenger(View v){
        if(user != null)
        {
            user.setType(User.UserType.PASSENGER);
            db.update(user);
        }
        startDash();
    }

    public void setDriver(View v){
        if(user != null)
        {
            user.setType(User.UserType.DRIVER);
            db.update(user);
        }

        startDash();
    }
    private void startDash(){
        Intent i = new Intent(this, Dashboard.class);
        startActivity(i);
    }
    private void startConnection(){
        Intent i = new Intent(this, ConnectionActivity.class);
        startActivity(i);
        finish();
    }

}
