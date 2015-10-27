package com.dicentrix.ecarpool.parcours;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.dicentrix.ecarpool.user.User;
import com.dicentrix.ecarpool.util.DAO;
import com.dicentrix.ecarpool.util.Database;

/**
 * Created by Akash on 10/26/2015.
 */
public class ParcoursDAO extends DAO implements IParcoursDAO {

    public static final String DEFAULT_TRAJET = "default_trajet";
    public static final int IDX_DEFAULT_TRAJET = 1;
    public static final String NB_SPOTS = "nb_spots";
    public static final int IDX_NB_SPOTS = 2;
    public static final String PRICE = "price";
    public static final int IDX_PRICE = 3;
    public static final String KM = "km";
    public static final int IDX_KM = 4;

    private ITrajetDAO dbTrajet;
    public ParcoursDAO(Context context){
        super(context);
        dbTrajet = new TrajetDAO(context);
    }

    @Override
    public int create(Parcours p) {
        open();
        int id = (int) db.insert(Database.USER_TBL , null, row(p));
        close();
        return id;
    }

    @Override
    public void delete(Parcours p) {

        open();
        db.update(Database.ROUTE_TBL, row(p), ID + "=" + p.getId(), null);
        close();
    }

    @Override
    public Parcours getById(int id) {
        open();
        Parcours result = null;
        Cursor c = db.query(
                Database.ROUTE_TBL, null, ID + "=" + id, null, null, null, null);
        c.moveToFirst();
        if (!c.isAfterLast())
            result = mapObject(c);
        close();
        return result;
    }

    @Override
    public void update(Parcours p) {
        open();
        db.update(Database.ROUTE_TBL, row(p), ID + "=" + p.getId(), null);
        close();
    }

    private ContentValues row(Parcours p)
    {
        ContentValues row = new ContentValues();
        row.put(DEFAULT_TRAJET, p.getDefaultTrajet().getId() );
        row.put(NB_SPOTS, p.getNbPlaces());
        row.put(PRICE, p.getPrice());
        row.put(KM, p.getKm());
        return row;
    }

    private Parcours mapObject(Cursor c)
    {
        Parcours p = new Parcours();

        p.setId(c.getInt(IDX_ID));
        p.setDefaultTrajet(dbTrajet.getById(c.getInt(IDX_DEFAULT_TRAJET)));
        p.setKm(c.getFloat(IDX_KM));
        p.setPrice(c.getFloat(IDX_PRICE));
        p.setNbPlaces(c.getInt(IDX_NB_SPOTS));
        return p;
    }
}
