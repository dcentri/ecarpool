package com.dicentrix.ecarpool.parcours;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.dicentrix.ecarpool.user.IUserDAO;
import com.dicentrix.ecarpool.user.UserDAO;
import com.dicentrix.ecarpool.util.AddressDAO;
import com.dicentrix.ecarpool.util.DAO;
import com.dicentrix.ecarpool.util.Database;
import com.dicentrix.ecarpool.util.IAddressDAO;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Akash on 10/27/2015.
 */
public class TrajetDAO extends DAO implements ITrajetDAO{
    public static final String FROM = "from_address";
    public static final int IDX_FROM = 1;
    public static final String TO = "to_address";
    public static final int IDX_TO = 2;
    public static final String DEPARTURE_DATE = "departure_dateTime";
    public static final int IDX_DEPARTURE_DATE = 3;
    public static final String ARRIVAL_DATETIME = "arrival_dateTime";
    public static final int IDX_ARRIVAL_DATETIME = 4;
    public static final String FREQUENCY = "freq";
    public static final int IDX_FREQUENCY = 5;
    public static final String AUTHOR = "author";
    public static final int IDX_AUTHOR = 6;

    //Table de jonction d'un parcours et le trajet
    public static final String ROUTE_ID = "route_id";
    public static final String TRAJET_ID = "trajet_id";

    private IAddressDAO dbAdress ;
    private IUserDAO dbUser;
    public TrajetDAO(Context context){
        super(context);
        dbAdress = new AddressDAO(context);
        dbUser = new UserDAO(context);
    }

    @Override
    public int create(Trajet trajet) {

        open();
        int id = (int) db.insert(Database.TRAJET_TBL , null, row(trajet));
        close();
        return id;
    }

    @Override
    public void delete(Trajet trajet) {

        open();
        db.delete(Database.ADDRESS_TBL, ID + "=" + trajet.getId(), null);
        close();
    }

    @Override
    public Trajet getById(int id) {
        open();
        Trajet result = null;
        Cursor c = db.query(
                Database.TRAJET_TBL, null, ID + "=" + id, null, null, null, null);
        c.moveToFirst();
        if (!c.isAfterLast())
            result = mapObject(c);
        close();
        return result;
    }

    public ArrayList<Trajet> getAllTrajetParcours(int idParcours){
        open();
        ArrayList<Trajet> result = new ArrayList<Trajet>();
        Cursor c = db.query(
                Database.ROUTETRAJET_TBL, null, ROUTE_ID + "=" + idParcours, null, null, null, null);
        c.moveToFirst();
        while (!c.isAfterLast())
            result.add(getById(c.getInt(1)));
        close();
        return result;
    }
    @Override
    public void update(Trajet trajet) {
        open();
        db.update(Database.TRAJET_TBL, row(trajet), ID + "=" + trajet.getId(), null);
        close();
    }

    public ArrayList<FrequenceTrajet> getAllFrequency(){
        open();
        ArrayList<FrequenceTrajet> allFreq = new ArrayList<>();
        Cursor c = db.query(
                Database.FREQUENCE_TBL, null, null, null, null, null, null);
        c.moveToFirst();
        while (!c.isAfterLast())
            allFreq.add(mapFrequencyObject(c));
        close();
        return  allFreq;
    }

    public FrequenceTrajet getFrequencyById(int id){
        open();
        FrequenceTrajet result = null;
        Cursor c = db.query(
                Database.FREQUENCE_TBL, null, ID + "=" + id, null, null, null, null);
        c.moveToFirst();
        if (!c.isAfterLast())
            result = mapFrequencyObject(c);
        close();
        return result;
    }

    private ContentValues row(Trajet trajet)
    {
        ContentValues row = new ContentValues();
        row.put(FROM, trajet.getDepart().getId() );
        row.put(TO, trajet.getDestination().getId());
        row.put(DEPARTURE_DATE, trajet.getDepartDateTime().toString());
        row.put(ARRIVAL_DATETIME, trajet.getArrivalDateTime().toString());
        row.put(FREQUENCY, trajet.getFrequence().getId());
        row.put(AUTHOR, trajet.getAuthor().getId());
        return row;
    }

    private Trajet mapObject(Cursor c)
    {
        Trajet trajet = new Trajet();

        trajet.setId(c.getInt(IDX_ID));
        trajet.setArrivalDateTime(new Date(c.getString(IDX_ARRIVAL_DATETIME)));
        trajet.setDepartDateTime(new Date(c.getString(IDX_DEPARTURE_DATE)));
        trajet.setDepart(dbAdress.getById(c.getInt(IDX_FROM)));
        trajet.setDestination(dbAdress.getById(c.getInt(IDX_TO)));
        trajet.setAuthor(dbUser.getById(c.getInt(IDX_AUTHOR)));
        trajet.setFrequence(getFrequencyById(c.getInt(IDX_FREQUENCY)));
        return trajet;
    }

    public FrequenceTrajet mapFrequencyObject(Cursor c){
        FrequenceTrajet freq = new FrequenceTrajet();
        freq.setId(c.getInt(0));
        freq.setName(c.getString(1));
        return freq;
    }


}
