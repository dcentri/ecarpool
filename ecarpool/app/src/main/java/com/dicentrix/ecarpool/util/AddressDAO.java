package com.dicentrix.ecarpool.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

/**
 * Created by Akash on 10/19/2015.
 */
public class AddressDAO extends DAO implements IAddressDAO{

    public static final String CIVIC_NO = "civic_no";
    public static final int IDX_CIVIC_NO = 1;
    public static final String STRT_NAME = "address";
    public static final int IDX_STRT_NAME= 2;
    public static final String APPARTEMENT = "appartement";
    public static final int IDX_APPARTEMENT = 3;
    public static final String POSTAL_CODE = "postal_code";
    public static final int IDX_POSTAL_CODE = 4;
    public static final String LONG_COORD = "long_coord";
    public static final int IDX_LONG_COORD = 5;
    public static final String LAT_COORD = "lat_coord";
    public static final int IDX_LAT_COORD = 6;

    public AddressDAO(Context context){
        super(context);
    }

    public int create(Address address)
    {
        open();
        int result = (int) db.insert(Database.ADDRESS_TBL, null, row(address));
        close();
        return result;
    }

    public  Address getById(int id){
        Address result = null;
        open();
        Cursor c = db.query(
                Database.ADDRESS_TBL, null, ID + "=" + id, null, null, null, null);
        c.moveToFirst();
        if (!c.isAfterLast()) {
             result = mapObject(c);
        }
        close();
        return result;
    }

    public void update(Address address){
        open();
        db.update(Database.ADDRESS_TBL, row(address), ID + "=" + address.getId(), null);
        close();
    }

    public void delete(int id){
        open();
        db.delete(Database.ADDRESS_TBL, ID + "=" + id, null);
        close();
    }

    private ContentValues row(Address address)
    {
        ContentValues row = new ContentValues();
        row.put(CIVIC_NO, address.getCivicNo());
        row.put(STRT_NAME, address.getRouteName());
        row.put(APPARTEMENT, address.getAppart());
        row.put(POSTAL_CODE, address.getPostalCode());
        row.put(LONG_COORD, address.getLongCoord());
        row.put(LAT_COORD, address.getLatCoord());
        return row;
    }

    private Address mapObject(Cursor c)
    {
        Address ad = new Address();
        ad.setId(c.getInt(IDX_ID));
        ad.setCivicNo(c.getString(IDX_CIVIC_NO));
        ad.setPostalCode(c.getString(IDX_POSTAL_CODE));
        ad.setAppart(c.getString(IDX_APPARTEMENT));
        ad.setRouteName(c.getString(IDX_STRT_NAME));
        ad.setLatCoord(c.getString(IDX_LAT_COORD));
        ad.setLongCoord(c.getString(IDX_LONG_COORD));

        return ad;
    }
}
