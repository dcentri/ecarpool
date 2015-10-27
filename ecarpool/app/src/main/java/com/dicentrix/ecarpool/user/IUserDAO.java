package com.dicentrix.ecarpool.user;

import com.dicentrix.ecarpool.util.IDAO;


/**
 * Created by Akash on 10/4/2015.
 */
public interface IUserDAO extends IDAO{

    public int create(User user);

    public  User getById(int id);

    public User getByLogin(String login);

    public void update(User user);

    public void delete(User user);
}
