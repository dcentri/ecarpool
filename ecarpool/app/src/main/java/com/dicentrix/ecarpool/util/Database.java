package com.dicentrix.ecarpool.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.dicentrix.ecarpool.user.UserDAO;

import java.util.HashMap;

/**
 * Created by Akash on 10/3/2015.
 */
public class Database extends SQLiteOpenHelper {

    private SQLiteDatabase db;
    private HashMap<String, String[]> values;

    public Database(Context context) {
        super(context, "ecarpool.sqlite", null, DB_VERSION);
    }

    private final static int DB_VERSION = 1;

    //Database tables
    public final static String USER_TBL = "tblUser";
    public final static String TRAJET_TBL = "tblTrajet";
    public final static String FREQUENCE_TBL = "tblFrequence";
    public final static String ROUTE_TBL = "tblRoute";
    public final static String ROUTETRAJET_TBL = "tblRouteTrajet";
    public final static String ADDRESS_TBL = "tblAddress";

    @Override
    public void onCreate(SQLiteDatabase database)
    {
        this.db = database;
        String [] tables = tables();
        HashMap<String, String> columns = columns();

        for(int i = 0; i < tables.length; i ++)
        {
            createTable(tables[i], columns.get(tables[i]));
        }
        populateDb();
    }

    private void populateDb()
    {
        values = new HashMap<>();
        values.put(FREQUENCE_TBL, new String[]{"1, 'weekday'", "2, 'weekend'","3, 'everyday'", "4, 'once'", "5, 'monthly'"});
        populateTable(FREQUENCE_TBL);

    }

    private void populateTable(String tbl )
    {
        String[] tempValues = values.get(tbl);
        for(int i = 0; i < tempValues.length; i++)
        {
            insert(tbl, tempValues[i]);
        }
    }

    /**
     * Insertion des données dans la base de données
     * @param tbl : Table dans laquelle les données seront inserées
     * @param values : Valeurs à inserer dans la base de données
     */
    private void insert(String tbl, String values)
    {
        db.execSQL("INSERT INTO " + tbl + " VALUES " + "(" + values + ")");
    }

    /**
     * Listes de tous les tables de la bd selon l'ordre du moins dépandant au plus dépandant
     * @return : Array de tous les tables de la bd
     */
    private String[] tables()
    {
        return new String[]{
                ADDRESS_TBL,
                FREQUENCE_TBL,
                USER_TBL,
                TRAJET_TBL,
                ROUTE_TBL,
                ROUTETRAJET_TBL};
    }

    /**
     * Dictionnaire de colonnes (Champs) de tous les tables avec la clé comme le nom de la table
     * @return Dictionnaire de tous les colonnes de tous les tables de la base de données
     */
    private HashMap<String, String> columns( )
    {
        HashMap<String, String> values = new HashMap<String, String>();
        values.put(ADDRESS_TBL, AddressDAO.CIVIC_NO + " TEXT, " + AddressDAO.STRT_NAME + " TEXT, " + AddressDAO.APPARTEMENT
                + " TEXT, " + AddressDAO.POSTAL_CODE + " TEXT, " + AddressDAO.LONG_COORD + " TEXT, " + AddressDAO.LAT_COORD + " TEXT");

        values.put(FREQUENCE_TBL, "freq_type TEXT");

        values.put(USER_TBL, UserDAO.LOGIN + " TEXT, " + UserDAO.LAST_NAME + " TEXT, " + UserDAO.FIRST_NAME + " TEXT, " + UserDAO.TEL + " TEXT, " + UserDAO.EMAIL + " TEXT, " +
                UserDAO.PASSWORD + " TEXT, " + UserDAO.ACTIVE_SPACE + " TEXT, " + UserDAO.GENDER + " TEXT, " + UserDAO.ADDRESS + " INTEGER," +
                "FOREIGN KEY (" + UserDAO.ADDRESS + ") REFERENCES " + ADDRESS_TBL + "(" + UserDAO.ID + ")");

        values.put(TRAJET_TBL, "from_address INTEGER, to_address INTEGER, departure_dateTime TEXT, " +
                "arrival_dateTime TEXT, freq INTEGER, author INTEGER, " +
                "FOREIGN KEY (from_address) REFERENCES " + ADDRESS_TBL + "(_id), " +
                "FOREIGN KEY (to_address) REFERENCES " + ADDRESS_TBL + "(_id), " +
                "FOREIGN KEY (freq) REFERENCES " + FREQUENCE_TBL + "(_id), " +
                "FOREIGN KEY (author) REFERENCES " + USER_TBL + "(_id)");

        values.put(ROUTE_TBL, "default_trajet INTEGER, nb_spots INTEGER, price REAL, km REAL, " +
                "FOREIGN KEY (default_trajet) REFERENCES " + TRAJET_TBL + "(_id)");

        values.put(ROUTETRAJET_TBL, "route_id INTEGER, trajet_id INTEGER, " +
                "FOREIGN KEY (route_id) REFERENCES " + ROUTE_TBL + "(_id), " +
                "FOREIGN KEY (trajet_id) REFERENCES " + TRAJET_TBL + "(_id)");

        return values;
    }

    /**
     * Créer une table dans la base de données sqlite avec un id autoincrement
     * @param tbl : Nom de la table à créer
     * @param values : Colonnes (champs) et types (sqlite pure) de la tables separer par virgules
     */
    private void createTable(String tbl, String values)
    {
        db.execSQL(tableSqlString(tbl, values));
    }

    /**
     * Supprimer une table de la base de données sqlite
     * @param tbl : table à supprimer
     */
    private void deleteTable(String tbl)
    {
        db.execSQL("DROP TABLE IF EXISTS " + tbl);
    }

    /**
     * Créer une chaîne de charactères pour une table sql avec un _id autoincrement
     * @param tbl : Nom de la table
     * @param values : Colonnes (champs) de la table
     * @return Chaîne de caractères sql représentant la table
     */
    private String tableSqlString(String tbl, String values)
    {
        return "CREATE TABLE " + tbl +
                " (_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                values + ")";
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion,
                           int newVersion)
    {
        this.db = database;
        String[] tables = tables();
        for(int i = tables.length; i > 0 ; i--)
        {
            deleteTable(tables[i-1]);
        }
        this.onCreate(db);
    }
}
