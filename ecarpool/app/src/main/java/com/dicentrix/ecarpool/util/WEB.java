package com.dicentrix.ecarpool.util;

/**
 * Created by Akash on 12/16/2015.
 */
public class WEB {
    public static String URL = "ecarpool-1137.appspot.com";
    public static String CON = "/connexion";
    public static String PARCOURS= "/parcours";
    public static String TRAJETS= "/trajets";
    public static String MESSAGE= "/trajets";
    public static String CREATE_USER(String login){
        return "/user/"+ login;
    }

    public static String MODIF_USER(String login){
        return "/user/"+ login;
    }

    public static String GET_USER(String login){
        return "/user/"+ login;
    }

    public static String GET_USER_PARCOURS(String login){
        return "/user/"+ login +"/parcours";
    }

    public static String GET_USER_TRAJETS(String login){
        return "/user/"+ login +"/trajets";
    }

    public static String GET_TRAJET(String idTrajet){
        return "/trajets/"+ idTrajet ;
    }

    public static String GET_PARCOURS(String idParcours){
        return "/parcours/"+ idParcours ;
    }

    public static String GET_ADRESSE(String idAdresse){
        return "/address/"+ idAdresse ;
    }

}
