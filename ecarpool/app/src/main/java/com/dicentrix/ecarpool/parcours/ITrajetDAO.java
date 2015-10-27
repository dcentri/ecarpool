package com.dicentrix.ecarpool.parcours;

import com.dicentrix.ecarpool.util.IDAO;

import java.util.ArrayList;

/**
 * Created by Akash on 10/27/2015.
 */
public interface ITrajetDAO extends IDAO {

    public int create(Trajet trajet);

    public Trajet getById(int id);

    public ArrayList<Trajet> getAllTrajetParcours(int idParcours);

    public void update(Trajet trajet);

    public void delete(Trajet trajet);

    public ArrayList<FrequenceTrajet> getAllFrequency();

    public FrequenceTrajet getFrequencyById(int id);

}
