package com.dicentrix.ecarpool.parcours;

import com.dicentrix.ecarpool.user.User;

import java.util.ArrayList;

/**
 * Created by Akash on 9/26/2015.
 */
public class Parcours {

    private int id;
    public String remoteId;
    public String driverId;
    private int nbPlaces;
    private float price;
    private float km;
    private Trajet defaultTrajet;
    private ArrayList<Trajet> trajets;
    public String[] remoteTrajetsIds;

    public Parcours(){}
    public Parcours(int nbPlaces, float price, float km, Trajet defaultTrajet){
        this.nbPlaces = nbPlaces;
        this.price = price;
        this.km = km;
        this.defaultTrajet = defaultTrajet;
    }

    public Trajet getDefaultTrajet() {
        return defaultTrajet;
    }

    public void setDefaultTrajet(Trajet defaultTrajet) {
        this.defaultTrajet = defaultTrajet;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public float getKm() {
        return km;
    }

    public void setKm(float km) {
        this.km = km;
    }

    public int getNbPlaces() {
        return nbPlaces;
    }

    public void setNbPlaces(int nbPlaces) {
        this.nbPlaces = nbPlaces;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public ArrayList<Trajet> getTrajets() {
        return trajets;
    }

    public void setTrajets(ArrayList<Trajet> trajets) {
        this.trajets = trajets;
    }

    public void addTrajet(Trajet trajet) {
        if(trajet.idAuthor.equals(driverId)){
            this.defaultTrajet = trajet;
        }else{
            if(this.trajets == null)
                this.trajets = new ArrayList<Trajet>();
            this.trajets.add(trajet);
        }

    }
}
