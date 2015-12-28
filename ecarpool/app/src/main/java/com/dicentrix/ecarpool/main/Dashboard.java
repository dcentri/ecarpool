package com.dicentrix.ecarpool.main;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dicentrix.ecarpool.R;
import com.dicentrix.ecarpool.access.ConnectionActivity;
import com.dicentrix.ecarpool.misc.HelpActivity;
import com.dicentrix.ecarpool.misc.ParcoursSearchActivity;
import com.dicentrix.ecarpool.parcours.AlarmReceiver;
import com.dicentrix.ecarpool.parcours.BootReceiver;
import com.dicentrix.ecarpool.parcours.CreateParcoursActivity;
import com.dicentrix.ecarpool.parcours.ParcoursActivity;
import com.dicentrix.ecarpool.user.IUserDAO;
import com.dicentrix.ecarpool.user.ProfilActivity;
import com.dicentrix.ecarpool.user.User;
import com.dicentrix.ecarpool.user.UserDAO;
import com.dicentrix.ecarpool.util.AddressDAO;
import com.dicentrix.ecarpool.util.IAddressDAO;

public class Dashboard extends Activity {
    public static int INTERVAL_ALARM = 7000;

    // Identifiant pour l'intention en suspens de l'alarme.
    public static final int ID_ALARM = 12345;
    // Le gestionnaire d'alarme d'Android.
    private AlarmManager alarmMgr;
    // L'intention lorsque l'alarme se déclenche.
    private Intent alarmIntent;

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
        this.alarmIntent = new Intent(this, AlarmReceiver.class);

        // Récupération du gestionnaire d'alarme.
        this.alarmMgr = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        // On récupère le pending intent pour vérifier si l'alarme est active; ceci permet de gérer l'état des boutons de l'interface.
        if (PendingIntent.getBroadcast(this, ID_ALARM, this.alarmIntent, PendingIntent.FLAG_NO_CREATE) != null) {
            // L'alarme est déjà active.
            //Toast.makeText(this, this.getString(R.string.msg_alarm_on), Toast.LENGTH_LONG).show();
        } else {
            // L'alarme est inactive.
            //activerAlarme();
        }
    }
    /*
         * Permet d'activer l'alarme.
         */
    public void activerAlarme() {

        // Création d'une nouvelle intention en suspens.
        PendingIntent alarmPendingIntent = PendingIntent.getBroadcast(this, ID_ALARM, this.alarmIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);

        // Activation de l'alarme : donc l'intention en suspens est envoyée à l'alarm manager
        // Privilégiez l'utilisation de la méthode "setInexactRepeating" au lieu de "setRepeating".
        this.alarmMgr.setRepeating(AlarmManager.ELAPSED_REALTIME, 0, INTERVAL_ALARM, alarmPendingIntent);

        Toast.makeText(this, this.getString(R.string.msg_alarm_on), Toast.LENGTH_SHORT).show();

        // Activation du BroadcastReceiver pour le démarrage (boot) de l'appareil (BootReceiver).
        // Ici, on intéragit avec un récepteur STATIQUE (dans le manifest)
        // D'où la nécessité d'utiliser le package manager pour activer/désactiver.
        ComponentName receiver = new ComponentName(this, BootReceiver.class);
        PackageManager pm = this.getPackageManager();
        pm.setComponentEnabledSetting(
                receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);
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
    public void list(View view){
        intent = new Intent(this, ParcoursActivity.class);
        startActivity(intent);
    }
}
