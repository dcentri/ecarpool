package com.dicentrix.ecarpool.parcours;


import com.dicentrix.ecarpool.user.User;
import com.dicentrix.ecarpool.util.Address;

import java.util.Date;

/**
 * Created by Akash on 9/26/2015.
 */
public class Trajet {


    private int id;
    public String remoteId;
    private User author;
    public String idAuthor;
    private Address depart;
    public String remoteDepartureAdresse;
    private Address destination;
    public String remoteArrivalAdresse;
    private Date departDateTime;
    private Date arrivalDateTime;
    private FrequenceTrajet frequence;

    public  Trajet(){}
    public  Trajet(User author, Address depart, Address destination, Date departDateTime, FrequenceTrajet frequence ){
        this.author = author;
        this.depart = depart;
        this.destination = destination;
        this.departDateTime = departDateTime;
        this.frequence = frequence;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getArrivalDateTime() {
        return arrivalDateTime;
    }

    public void setArrivalDateTime(Date arrivalDateTime) {
        this.arrivalDateTime = arrivalDateTime;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public Address getDepart() {
        return depart;
    }

    public void setDepart(Address depart) {
        this.depart = depart;
    }

    public Date getDepartDateTime() {
        return departDateTime;
    }

    public void setDepartDateTime(Date departDateTime) {
        this.departDateTime = departDateTime;
    }

    public Address getDestination() {
        return destination;
    }

    public void setDestination(Address destination) {
        this.destination = destination;
    }

    public FrequenceTrajet getFrequence() {
        return frequence;
    }

    public void setFrequence(FrequenceTrajet frequence) {
        this.frequence = frequence;
    }
}
