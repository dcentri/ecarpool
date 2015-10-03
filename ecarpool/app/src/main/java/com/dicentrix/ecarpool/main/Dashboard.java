package com.dicentrix.ecarpool.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.dicentrix.ecarpool.R;
import com.dicentrix.ecarpool.misc.SearchActivity;
import com.dicentrix.ecarpool.parcours.CreateParcoursActivity;
import com.dicentrix.ecarpool.parcours.ParcoursActivity;
import com.dicentrix.ecarpool.user.ProfilActivity;

public class Dashboard extends Activity {

    Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity);
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

                return true;
            default:
                Log.w("MainActivity", "Menu inconnu : " + item.getTitle());
        }

        return super.onOptionsItemSelected(item);
    }

    public void search(View view){
        intent = new Intent(this, SearchActivity.class);
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
