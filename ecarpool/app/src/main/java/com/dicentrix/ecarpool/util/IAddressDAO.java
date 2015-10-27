package com.dicentrix.ecarpool.util;


/**
 * Created by Akash on 10/19/2015.
 */
public interface IAddressDAO extends IDAO{

    public int create(Address user);

    public  Address getById(int id);

    public void update(Address address);

    public void delete(int id);
}
