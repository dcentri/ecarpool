package com.dicentrix.ecarpool.access;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
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

        if (validateConnection(con))
        {
            setCredentials(PreferenceManager.getDefaultSharedPreferences(ConnectionActivity.this).edit(), con, db.getByLogin(con.getLogin()).getId());
            startDash();
        }
        else
            Toast.makeText(this,R.string.err_invalidConnection, Toast.LENGTH_LONG).show();
    }

    /**
     * Valide la connexion dans la base de données
     * @param con : Objet de connexion
     * @return Vrai si la connexion est valide et faux dans le cas contraire.
     */
    public boolean validateConnection(Connection con){
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

    public void startUserType(){
        Intent i = new Intent(this, UserTypeActivity.class);
        startActivity(i);
        finish();
    }
}
