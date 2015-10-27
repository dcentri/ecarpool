package com.dicentrix.ecarpool.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Akash on 10/4/2015.
 */
public class DAO implements IDAO{

    public static final String ID = "_id";
    public static final int IDX_ID= 0;

    protected Database ddl;
    protected SQLiteDatabase db;

    public DAO(Context context) {
        ddl = new Database(context);
    }

    /**
     * Ouverture de la connexion à la BD.
     */
    public void open() {
        db = this.ddl.getWritableDatabase();
    }

    /**
     * Fermeture de la connexion à la BD.
     */
    public void close() {
        db.close();
    }

}
