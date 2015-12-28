package com.dicentrix.ecarpool.access;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.dicentrix.ecarpool.R;
import com.dicentrix.ecarpool.main.Dashboard;
import com.dicentrix.ecarpool.user.IUserDAO;
import com.dicentrix.ecarpool.user.User;
import com.dicentrix.ecarpool.user.UserDAO;
import com.dicentrix.ecarpool.user.UserTypeActivity;
import com.dicentrix.ecarpool.util.Address;
import com.dicentrix.ecarpool.util.JsonParser;
import com.dicentrix.ecarpool.util.WEB;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.json.JSONObject;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Akash on 2015-09-20.
 */
public class ConnectionActivity extends Activity {

    IUserDAO db;
    public final static String USER_ID = "userId";
    public final static String LOGIN = "userLogin";
    public final static String PSWD = "pswd";
    private final String TAG = this.getClass().getSimpleName();
    private HttpClient m_ClientHttp = new DefaultHttpClient();
    public User m_usr;
    public Connection m_con ;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = new UserDAO(this);
        setContentView(R.layout.connection_activity);
    }

    /**
     * Valide la saisie à l'écran d'une connexion au système
     * @param view: Vue de connexion contenant les champs de saisie du login et du mot de passe.
     */
    public void validateConnection(View view){
        Connection con = new Connection();
        con.setLogin(((EditText) this.findViewById(R.id.txtLogin)).getText().toString());
        con.setPassword(((EditText) this.findViewById(R.id.txtPassword)).getText().toString());

        if (!(con.getLogin().equals("") || con.getPassword().equals("")))
        {
            m_con = con;
            new CreateConnectionTask().execute((Void) null );
        }
        else
            Toast.makeText(this,R.string.err_invalidCredentials, Toast.LENGTH_LONG).show();
    }

    //Valide la connexion au serveur
    public void validateServerConnection(Connection con){
        setCredentials(PreferenceManager.getDefaultSharedPreferences(ConnectionActivity.this).edit(), con, db.getByLogin(con.getLogin()).getId());
        startDash();
    }

    /**
     * Valide la connexion dans la base de données
     * @param con : Objet de connexion
     * @return Vrai si la connexion est valide et faux dans le cas contraire.
     */
    public boolean validatedbConnection(Connection con){
        if(con.getLogin().equals("") || con.getPassword().equals(""))
            return false;
        else{
            User dbUser = db.getByLogin(con.getLogin());
            return ( dbUser != null  && dbUser.getPassword().equals(con.getPassword()));
        }
    }

    /**
     * Ouvre la vue d'inscription.
     */
    public void startSignUp(View view){
        Intent i = new Intent(this, SignUpActivity.class);
        startActivity(i);
    }

    public static void setCredentials(SharedPreferences.Editor editor, Connection con, int id){
        editor.clear();
        editor.putInt(USER_ID, id);
        editor.putString(LOGIN, con.getLogin());
        editor.putString(PSWD, con.getPassword());
        editor.commit();
    }

    public void startDash( ){
        Intent i = new Intent(this, Dashboard.class);
        startActivity(i);
        finish();
    }

    private class CreateConnectionTask extends AsyncTask<Void, Void, Void> {
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
                m_usr = WEB.signIn(m_con);
                User tempUser = db.getByLogin(m_usr.getLogin());
                if(tempUser != null){
                    m_usr = tempUser;
                }else{
                    m_usr.setAddress(WEB.getAddresse(m_usr.remoteAddress));
                    m_usr.setId(db.create(m_usr));
                }
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
                // Rechargement de la liste des personnes.
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(ConnectionActivity.this).edit();
                setCredentials(editor, m_con, m_usr.getId());

                Intent intent = new Intent(ConnectionActivity.this, Dashboard.class);
                ConnectionActivity.this.startActivity(intent);
            } else {
                Log.e(TAG, "Erreur lors de l'ajout ou de la modification de la personne (PUT)", m_Exp);
                Toast.makeText(ConnectionActivity.this, getString(R.string.err_invalidCredentials), Toast.LENGTH_SHORT).show();
            }
        }
    }
}
