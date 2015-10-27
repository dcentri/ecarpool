package com.dicentrix.ecarpool.access;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.dicentrix.ecarpool.R;
import com.dicentrix.ecarpool.user.IUserDAO;
import com.dicentrix.ecarpool.user.UserDAO;
import com.dicentrix.ecarpool.user.UserTypeActivity;
import com.dicentrix.ecarpool.util.Address;
import com.dicentrix.ecarpool.util.AddressDAO;
import com.dicentrix.ecarpool.util.IAddressDAO;

/**
 * Created by Akash on 9/26/2015.
 */
public class SignUpActivity extends Activity {

    public static final String USER_ID = "userId";
    IUserDAO db;
    IAddressDAO dbAdresse;


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

        if(newUser.isValid()){
            if(loginExists(newUser.getLogin()))
                Toast.makeText(this, R.string.err_signUpInvalidLogin, Toast.LENGTH_LONG).show();
            else
            {

                newAddress.setId(dbAdresse.create(newAddress));
                newUser.setAddress(newAddress);

                int id = db.create(newUser);

                ConnectionActivity.setCredentials(PreferenceManager.getDefaultSharedPreferences
                        (SignUpActivity.this).edit(), new Connection(newUser.getLogin(),newUser.getPassword()), id);

                Intent i = new Intent(this, UserTypeActivity.class);
                i.putExtra(USER_ID, id);
                startActivity(i);
                finish();
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
}
