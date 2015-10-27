package com.dicentrix.ecarpool.parcours;


import com.dicentrix.ecarpool.util.IDAO;

/**
 * Created by Akash on 10/26/2015.
 */
public interface IParcoursDAO extends IDAO {

    public int create(Parcours p);

    public Parcours getById(int id);

    public void update(Parcours p);

    public void delete(Parcours p);
}