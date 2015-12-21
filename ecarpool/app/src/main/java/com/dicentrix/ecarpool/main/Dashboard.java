package com.dicentrix.ecarpool.main;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.dicentrix.ecarpool.R;
import com.dicentrix.ecarpool.access.ConnectionActivity;
import com.dicentrix.ecarpool.misc.HelpActivity;
import com.dicentrix.ecarpool.misc.ParcoursSearchActivity;
import com.dicentrix.ecarpool.misc.SearchActivity;
import com.dicentrix.ecarpool.parcours.CreateParcoursActivity;
import com.dicentrix.ecarpool.parcours.MapsActivity;
import com.dicentrix.ecarpool.parcours.ParcoursActivity;
import com.dicentrix.ecarpool.user.IUserDAO;
import com.dicentrix.ecarpool.user.ProfilActivity;
import com.dicentrix.ecarpool.user.User;
import com.dicentrix.ecarpool.user.UserDAO;
import com.dicentrix.ecarpool.util.AddressDAO;
import com.dicentrix.ecarpool.util.IAddressDAO;

public class Dashboard extends Activity {

    User user;
    IUserDAO db = new UserDAO(this);
    IAddressDAO dbAdresse = new AddressDAO(this);
    Intent intent;
    boolean isDriver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        user = db.getById(prefs.getInt(ConnectionActivity.USER_ID, 0));
        setContentView(R.layout.home_activity);
        isDriver = user.getType() == User.UserType.DRIVER;
        if(!isDriver){
            ((ImageView)this.findViewById(R.id.icUserType)).setImageResource(R.drawable.passeng);
            ((TextView) this.findViewById(R.id.lblUserType)).setText(R.string.lbl_passenger);
            ((TextView)this.findViewById(R.id.txtNew)).setText(R.string.lbl_newTrajet);
            ((TextView)this.findViewById(R.id.txtMine)).setText(R.string.lbl_my_trajet);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_dashboard, menu);
        return true;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        getMenuInflater().inflate(R.menu.menu_dashboard, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch(item.getItemId())
        {
            case R.id.fate:
                help();
                return true;
            default:
                Log.w("MainActivity", "Menu inconnu : " + item.getTitle());
        }

        return super.onOptionsItemSelected(item);
    }
    public void help(){
        intent = new Intent(this, HelpActivity.class);
        startActivity(intent);
    }
    public void search(View view){
        intent = new Intent(this, ParcoursSearchActivity.class);
        startActivity(intent);
    }
    public void profil(View view){
        intent = new Intent(this, ProfilActivity.class);
        startActivity(intent);
    }
    public void create(View view){
        intent = new Intent(this, CreateParcoursActivity.class);
        startActivity(intent);
    }
    public void startMap(){
        intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
    }
    public void list(View view){
        intent = new Intent(this, ParcoursActivity.class);
        startActivity(intent);
    }
}
