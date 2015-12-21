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
import android.widget.RadioGroup;
import android.widget.Toast;

import com.dicentrix.ecarpool.R;
import com.dicentrix.ecarpool.user.IUserDAO;
import com.dicentrix.ecarpool.user.User;
import com.dicentrix.ecarpool.user.UserDAO;
import com.dicentrix.ecarpool.user.UserTypeActivity;
import com.dicentrix.ecarpool.util.Address;
import com.dicentrix.ecarpool.util.AddressDAO;
import com.dicentrix.ecarpool.util.IAddressDAO;
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
public class SignUpActivity extends Activity {
    private final String TAG = this.getClass().getSimpleName();
    public static final String USER_ID = "userId";
    public static final String USER_LOGIN = "userLogin";
    IUserDAO db;
    IAddressDAO dbAdresse;
    public User m_usr;
    public Address m_addrs;
    private HttpClient m_ClientHttp = new DefaultHttpClient();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = new UserDAO(this);
        dbAdresse = new AddressDAO(this);
        setContentView(R.layout.signup_activity);
    }

    public void validateSignUp(View view){

        SignUp newUser = new SignUp();
        newUser.setLogin(((EditText) this.findViewById(R.id.txtLogin)).getText().toString());
        newUser.setGender(R.id.rbFemale == ((RadioGroup) this.findViewById(R.id.Sex)).getCheckedRadioButtonId()
                ? 'F' : 'M');

        newUser.setFirstName(((EditText) this.findViewById(R.id.txtFirstName)).getText().toString());
        newUser.setLastName(((EditText) this.findViewById(R.id.txtName)).getText().toString());
        newUser.setPhone(((EditText) this.findViewById(R.id.txtTel)).getText().toString());
        newUser.setEmail(((EditText) this.findViewById(R.id.txtEmail)).getText().toString());
        newUser.setPassword(((EditText) this.findViewById(R.id.txtPassword)).getText().toString());
        newUser.setConfirmPassword(((EditText) this.findViewById(R.id.txtConfirmPassword)).getText().toString());

        Address newAddress = new Address();

        newAddress.setCivicNo(((EditText) this.findViewById(R.id.txtCivicNo)).getText().toString());
        newAddress.setRouteName(((EditText) this.findViewById(R.id.txtAddress)).getText().toString());
        newAddress.setAppart(((EditText) this.findViewById(R.id.txtAppartNo)).getText().toString());
        newAddress.setPostalCode(((EditText) this.findViewById(R.id.txtPostalCode)).getText().toString());
        m_usr = newUser;
        m_usr.setType(User.UserType.PASSENGER);
        m_usr.setAddress(newAddress);
        m_addrs = newAddress;

        if(newUser.isValid()){
            if(loginExists(newUser.getLogin()))
                Toast.makeText(this, R.string.err_signUpInvalidLogin, Toast.LENGTH_LONG).show();
            else
            {
                new CreateUserTask().execute((Void) null);
            }
        }
        else
            Toast.makeText(this, newUser.getPassword().equals(newUser.getConfirmPassword()) ?
                    R.string.err_cannotBeEmpty
                    : R.string.err_passwordMismatch, Toast.LENGTH_LONG).show();

    }

    public boolean loginExists(String login){
        return db.getByLogin(login) != null;
    }


    private class CreateUserTask extends AsyncTask<Void, Void, Void> {
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
                URI uri = new URI("https", WEB.URL, WEB.GET_USER(m_usr.getLogin()), null, null);
                HttpPut requetePost = new HttpPut(uri);

                //String body = m_ClientHttp.execute(requeteGet, new BasicResponseHandler());
                //Log.i(TAG, "Reçu (PUT) : " + body);

                JSONObject obj = JsonParser.serialiseUser(m_usr);
                requetePost.setEntity(new StringEntity(obj.toString()));
                requetePost.addHeader("Content-Type", "application/json");

                String body = m_ClientHttp.execute(requetePost, new BasicResponseHandler());
                m_usr= JsonParser.deseriliserUser(new JSONObject(body));
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
                m_usr.setAddress(m_addrs);
                m_usr.getAddress().setId(dbAdresse.create(m_usr.getAddress()));

                int id = db.create(m_usr);
                ConnectionActivity.setCredentials(PreferenceManager.getDefaultSharedPreferences
                        (SignUpActivity.this).edit(), new Connection(m_usr.getLogin(),m_usr.getPassword()), id);

                Intent i = new Intent(SignUpActivity.this, UserTypeActivity.class);
                i.putExtra(USER_ID, id);
                i.putExtra(USER_LOGIN, m_usr.getLogin());
                startActivity(i);
                finish();

                // Rechargement de la liste des personnes.
//                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(SignUpActivity.this).edit();
//                editor.clear();
                // Pour ajouter ou supprimer des préférences en code.
//                editor.putString("surnom", m_usr.get());
//                editor.putString("motPasse", m_usr.getMotPasse());
//                editor.putString("adresseCourriel", m_utilisateur.getCourriel());
                // editor.remove(key);
//                editor.commit();
//
//                Intent intent = new Intent(SignUpActivity.this, UserTypeActivity.class);
//                SignUpActivity.this.startActivity(intent);
            } else {
                Log.e(TAG, getString(R.string.err_server_inscription), m_Exp);
                Toast.makeText(SignUpActivity.this, getString(R.string.err_com), Toast.LENGTH_SHORT).show();
            }
        }
    }
}
