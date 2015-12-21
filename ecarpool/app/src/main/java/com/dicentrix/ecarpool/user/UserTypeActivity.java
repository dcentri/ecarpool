package com.dicentrix.ecarpool.user;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.dicentrix.ecarpool.R;
import com.dicentrix.ecarpool.access.ConnectionActivity;
import com.dicentrix.ecarpool.access.SignUpActivity;
import com.dicentrix.ecarpool.main.Dashboard;
import com.dicentrix.ecarpool.util.JsonParser;
import com.dicentrix.ecarpool.util.WEB;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.net.URI;

/**
 * Created by Akash on 9/26/2015.
 */
public class UserTypeActivity extends Activity {
    User user;
    IUserDAO db;
    private HttpClient m_ClientHttp = new DefaultHttpClient();
    private final String TAG = this.getClass().getSimpleName();
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
            new UpdateUserTypeTask().execute((Void) null);
        }
        else{
            Toast.makeText(this, getString(R.string.err_com), Toast.LENGTH_SHORT);
        }

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

    private class UpdateUserTypeTask extends AsyncTask<Void, Void, Void> {
        Exception m_Exp;

        // Méthode exécutée SYNChrone avant l'exécution de la tâche asynchrone.
        @Override
        protected void onPreExecute() {
            setProgressBarIndeterminateVisibility(true);
        }

        // Méthode exécutée ASYNChrone: la tâche en tant que telle.
        @Override
        protected Void doInBackground(Void... unused) {

            try {
                URI uri = new URI("https", WEB.URL, WEB.GET_USER(user.getLogin()), null, null);
                HttpPut requetePost = new HttpPut(uri);

                //String body = m_ClientHttp.execute(requeteGet, new BasicResponseHandler());
                //Log.i(TAG, "Reçu (PUT) : " + body);

                JSONObject obj = JsonParser.serialiseUser(user);
                requetePost.setEntity(new StringEntity(obj.toString()));
                requetePost.addHeader("Content-Type", "application/json");

                m_ClientHttp.execute(requetePost, new BasicResponseHandler());
                Log.i(TAG, "Put terminé avec succès");
            } catch (Exception e) {
                m_Exp = e;
            }
            return null;
        }

        // Méthode exécutée SYNChrone après l'exécution de la tâche asynchrone.
        // Elle reçoit en paramètre la valeur de retour de "doInBackground".
        @Override
        protected void onPostExecute(Void unused) {
            setProgressBarIndeterminateVisibility(false);

            if (m_Exp == null) {

                db.update(user);
                startDash();
                finish();
            } else {
                Log.e(TAG, getString(R.string.err_server_inscription), m_Exp);
                Toast.makeText(UserTypeActivity.this, getString(R.string.err_com), Toast.LENGTH_SHORT).show();
            }
        }
    }

}
