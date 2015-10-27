package com.dicentrix.ecarpool.user;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.dicentrix.ecarpool.util.AddressDAO;
import com.dicentrix.ecarpool.util.DAO;
import com.dicentrix.ecarpool.util.Database;

/**
 * Created by Akash on 10/4/2015.
 */
public class UserDAO extends DAO implements IUserDAO {

    public static final String LOGIN = "login";
    public static final int IDX_LOGIN = 1;
    public static final String LAST_NAME = "last_name";
    public static final int IDX_LAST_NAME = 2;
    public static final String FIRST_NAME = "first_name";
    public static final int IDX_FIRST_NAME = 3;
    public static final String TEL = "tel";
    public static final int IDX_TEL = 4;
    public static final String EMAIL = "email";
    public static final int IDX_EMAIL = 5;
    public static final String PASSWORD = "password";
    public static final int IDX_PASSWORD = 6;
    public static final String ACTIVE_SPACE = "active_space";
    public static final int IDX_ACTIVE_SPACE = 7;
    public static final String GENDER = "gender";
    public static final int IDX_GENDER = 8;
    public static final String ADDRESS = "address";
    public static final int IDX_ADDRESS = 9;

    private AddressDAO dbAddress;

    public UserDAO(Context context){
        super(context);
        this.dbAddress = new AddressDAO(context);

    }

    @Override
    public int create(User user) {

        open();
        int id = (int) db.insert(Database.USER_TBL , null, row(user));
        close();
        return id;
    }

    @Override
    public void delete(User user) {

        open();

        close();
    }

    @Override
    public User getById(int id) {
        open();
        User result = null;
        Cursor c = db.query(
                Database.USER_TBL, null, ID + "=" + id, null, null, null, null);
        c.moveToFirst();
        if (!c.isAfterLast())
            result = mapObject(c);
        close();
        return result;
    }

    @Override
    public User getByLogin(String login) {
        String where = LOGIN + "=\"" + login + "\"";
        User result = null;
        open();
        try{
            Cursor c = db.query(
                    Database.USER_TBL, null, where, null, null, null, null);
            c.moveToFirst();
            if (!c.isAfterLast())
                result = mapObject(c);
        }catch (Exception e)
        {
            System.out.println("Error occured: "+ e.getMessage());
        }
        close();
        return result;
    }

    @Override
    public void update(User user) {
        open();
        db.update(Database.USER_TBL, row(user), ID + "=" + user.getId(), null);
        close();
    }

    private ContentValues row(User user)
    {
        ContentValues row = new ContentValues();
        row.put(LOGIN, user.getLogin());
        row.put(LAST_NAME, user.getLastName());
        row.put(FIRST_NAME, user.getFirstName());
        row.put(TEL, user.getPhone());
        row.put(EMAIL, user.getEmail());
        row.put(PASSWORD, user.getPassword());
        row.put(GENDER, String.valueOf(user.getGender()));
        row.put(ADDRESS, user.getAddress().getId());
        return row;
    }

    private User mapObject(Cursor c)
    {
        User user = new User();

        user.setId(c.getInt(IDX_ID));
        user.setAddress(dbAddress.getById(c.getInt(IDX_ADDRESS)));
        user.setPassword(c.getString(IDX_PASSWORD));
        user.setEmail(c.getString(IDX_EMAIL));
        user.setFirstName(c.getString(IDX_FIRST_NAME));
        user.setLastName(c.getString(IDX_LAST_NAME));
        user.setGender(c.getString(IDX_GENDER).charAt(0));
        user.setLogin(c.getString(IDX_LOGIN));
        user.setPhone(c.getString(IDX_TEL));
        String type = c.getString(IDX_ACTIVE_SPACE);
        user.setType(type == null ? User.UserType.PASSENGER : User.UserType.valueOf(type));

        return user;
    }
}
