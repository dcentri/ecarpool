package com.dicentrix.ecarpool.util;

import com.dicentrix.ecarpool.access.Connection;
import com.dicentrix.ecarpool.parcours.Parcours;
import com.dicentrix.ecarpool.parcours.Trajet;
import com.dicentrix.ecarpool.user.User;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Akash on 12/16/2015.
 */
public class WEB {
    public static String URL = "ecarpool-1137.appspot.com";
    public static String CON = "/connexion";
    public static String PARCOURS= "/parcours";
    public static String TRAJETS= "/trajets";
    public static String MESSAGE= "/message";
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

    public static String GET_TRAJET_PARCOURS(String idTrajet){
        return "/trajet/"+ idTrajet +"/parcours";
    }

    public static String GET_USER_MESSAGES(String login){
        return "/user/"+ login +"/message";
    }

    public static String GET_MESSAGE(String id){
        return "/message/"+ id ;
    }

    public static String DELETE_MESSAGE(String login , String id){
        return "/user/"+ login +"/message/"+ id ;
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

    public static String DRIVER_REQUEST(String login, String parcoursId, String trajetId){
        return "/user/"+ login +"/parcours/"+parcoursId + "/trajet/"+trajetId ;
    }

    public static String PASSENGER_REQUEST(String login, String trajetId, String parcoursId){
        return "/user/"+ login + "/trajet/" + trajetId + "/parcours/" + parcoursId  ;
    }

    public static String GET_ADRESSE(String idAdresse){
        return "/address/"+ idAdresse ;
    }

    /**
     * Retourne l'adresse trouvé sur le serveur
     * @param id : Identifiant de l'adresse sur le serveur
     * @return Objet Adress trouvé sur le serveur. Si l'adresse n'est pas trouvé, une exception est lancé.
     * @throws Exception
     */
    public static Address getAddresse(String id)throws Exception{
        HttpClient m_ClientHttp = new DefaultHttpClient();
        try {

            URI uri = new URI("https", WEB.URL, WEB.GET_ADRESSE(id), null, null);
            HttpGet requeteGet = new HttpGet(uri);
            requeteGet.addHeader("Content-Type", "application/json");

            String body = m_ClientHttp.execute(requeteGet, new BasicResponseHandler());
            Address tempAdresse = JsonParser.deserialiseAddresse(new JSONObject(body));

            return tempAdresse;
        }catch (Exception e){
            throw e;
        }
    }
    public static User signIn(Connection con)throws Exception{
        HttpClient m_ClientHttp = new DefaultHttpClient();
        try {
            User temp;
            URI uri = new URI("https", WEB.URL, WEB.CON, null, null);
            HttpPost requetePost = new HttpPost(uri);

            //String body = m_ClientHttp.execute(requeteGet, new BasicResponseHandler());
            //Log.i(TAG, "Reçu (PUT) : " + body);

            JSONObject obj = JsonParser.serialiseCon(con);
            requetePost.setEntity(new StringEntity(obj.toString(), HTTP.UTF_8));
            requetePost.addHeader("Content-Type", "application/json");

            String body = m_ClientHttp.execute(requetePost, new BasicResponseHandler());
            temp= JsonParser.deseriliserUser(new JSONObject(body));
            return temp;
        }catch (Exception e){
            throw e;
        }
    }
    /**
     * Retrouve un objet trajet complèt avec l'utilisateur et les objets adresses et l'autheur du trajet
     * @param trajetId : L'identifiant du trajet sur le serveur.
     * @return Objet trajet complète trouvé sur le serveur.
     */
    public static Trajet getCompleteTrajet(String trajetId)throws Exception{
        try{
            Trajet temp = getTrajetWithAddresses(trajetId);
            temp.setAuthor(getUser(temp.idAuthor));
            return temp;
        }catch (Exception e){
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    /**
     * Récupère le trajet avec tous ses adresses.
     * @param trajetId : L'identifiant du trajet à récupérer.
     * @return : Trajet trouvé.
     */
    public static Trajet getTrajetWithAddresses(String trajetId){
        try {
            Trajet temp = getTrajet(trajetId);
            temp = WEB.fillTrajetAdresses(temp);
            return temp;
        }catch (Exception e){
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    /**
     * Retourne un trajet comme représenté sur le réseau
     * @param trajetId : L'identifiant du trajet sur le serveur
     * @return : L'objet trajet trouvé. S'il est pas trouvé une exception est lancé.
     * @throws Exception
     */
    public static Trajet getTrajet(String trajetId)throws Exception{
        try{
            Trajet temp;
            HttpClient m_ClientHttp = new DefaultHttpClient();
            URI uri = new URI("https", WEB.URL, WEB.GET_TRAJET(trajetId), null, null);
            HttpGet requeteGet = new HttpGet(uri);
            requeteGet.addHeader("Content-Type", "application/json");

            String body = m_ClientHttp.execute(requeteGet, new BasicResponseHandler());

            temp = JsonParser.deserialiseTrajet(new JSONObject(body));

            return temp;
        }catch (Exception e){
            throw e;
        }
    }

    /**
     * Retourne tous les trajets comme représenté sur le réseau
     * @return : Une liste des trajets trouvées. S'il est pas trouvé une exception est lancé.
     * @throws Exception
     */
    public static List<Trajet> getAllTrajets()throws Exception{
        try{
            List<Trajet> trajs;
            HttpClient m_ClientHttp = new DefaultHttpClient();
            URI uri = new URI("https", WEB.URL, WEB.TRAJETS, null, null);
            HttpGet requeteGet = new HttpGet(uri);
            requeteGet.addHeader("Content-Type", "application/json");

            String body = m_ClientHttp.execute(requeteGet, new BasicResponseHandler());
            trajs = JsonParser.deserialiseTrajetsList(new JSONArray(body));
            return trajs;
        }catch (Exception e){
            throw e;
        }
    }
    /**
     * Retourne un parcours comme représenté sur le réseau
     * @param userLogin : L'identifiant de l'utilisateur pour le quelle on cherche les trajets
     * @return : Les objets trajets trouvés. S'il ne sont pas trouvés, une exception est lancé.
     * @throws Exception : Lancé en cas d'erreur de connexion ou de conversion d'objet json.
     */
    public static List<Trajet> getUser_sTrajets(String userLogin)throws Exception{
        try{
            List<Trajet> trajs;
            HttpClient m_ClientHttp = new DefaultHttpClient();
            URI uri = new URI("https", WEB.URL, WEB.GET_USER_TRAJETS(userLogin), null, null);
            HttpGet requeteGet = new HttpGet(uri);
            requeteGet.addHeader("Content-Type", "application/json");

            String body = m_ClientHttp.execute(requeteGet, new BasicResponseHandler());
            trajs = JsonParser.deserialiseTrajetsList(new JSONArray(body));
            return trajs;
        }catch (Exception e){
            throw e;
        }
    }
    /**
     * Retourne un parcours comme représenté sur le réseau
     * @param userLogin : L'identifiant de l'utilisateur pour le quelle on cherche les trajets
     * @return : Les objets trajets trouvés. S'il ne sont pas trouvés, une exception est lancé.
     * @throws Exception : Lancé en cas d'erreur de connexion ou de conversion d'objet json.
     */
    public static List<Trajet> getUser_sUsableTrajets(String userLogin)throws Exception{
        try{
            List<Trajet> users =  batchFillTrajetAdresses(getUser_sTrajets(userLogin));
            List<Trajet> res = new ArrayList<>();
            for(Trajet t : users){
                if(!t.booked)
                    res.add(t);
            }
            return res;
        }catch (Exception e){
            throw e;
        }
    }
    /**
     * Retourne un parcours comme représenté sur le réseau
     * @param parcoursId : L'identifiant du parcours sur le serveur
     * @return : L'objet parcours trouvé. S'il n'est pas trouvé, une exception est lancé.
     * @throws Exception : Lancé en cas d'erreur de connexion ou de conversion d'objet json.
     */
    public static Parcours getParcours(String parcoursId)throws Exception{
        try{
            Parcours temp;
            HttpClient m_ClientHttp = new DefaultHttpClient();
            URI uri = new URI("https", WEB.URL,WEB.GET_PARCOURS(parcoursId), null, null);
            HttpGet requeteGet = new HttpGet(uri);
            requeteGet.addHeader("Content-Type", "application/json");

            String body = m_ClientHttp.execute(requeteGet, new BasicResponseHandler());

            temp = JsonParser.deserialiseParcours(new JSONObject(body));

            return temp;
        }catch (Exception e){
            throw e;
        }
    }

    /**
     * Retourne un parcours comme représenté sur le réseau qui a le trajet avec l'identifiant spécifié
     * @param trajetId : L'identifiant du trajet sur le serveur
     * @return : L'objet parcours trouvé. S'il n'est pas trouvé, une exception est lancé.
     * @throws Exception : Lancé en cas d'erreur de connexion ou de conversion d'objet json.
     */
    public static Parcours getTrajetParcours(String trajetId)throws Exception{
        try{
            Parcours temp;
            HttpClient m_ClientHttp = new DefaultHttpClient();
            URI uri = new URI("https", WEB.URL,WEB.GET_TRAJET_PARCOURS(trajetId), null, null);
            HttpGet requeteGet = new HttpGet(uri);
            requeteGet.addHeader("Content-Type", "application/json");

            String body = m_ClientHttp.execute(requeteGet, new BasicResponseHandler());

            temp = null;
            JSONArray res = new JSONArray(body);
            if(res.length() > 0){
                temp = JsonParser.deserialiseParcours(((JSONObject) res.get(0)));
            }
            return temp;
        }catch (Exception e){
            throw e;
        }
    }
    public static Map<String, String> driverTrajetParcoursRequest(String login, String parcoursId, String trajetId)throws Exception{
        try{
            HttpClient m_ClientHttp = new DefaultHttpClient();
            URI uri = new URI("https", WEB.URL, WEB.DRIVER_REQUEST(login, parcoursId, trajetId), null, null);
            HttpPut requetePut = new HttpPut(uri);
            String body = m_ClientHttp.execute(requetePut, new BasicResponseHandler());
            return JsonParser.deserialiseStatus(new JSONObject(body));
        }catch (Exception e){
            throw e;
        }
    }
    public static Map<String, String> passengerParcoursTrajetRequest(String login, String parcoursId, String trajetId)throws Exception{
        try{
            HttpClient m_ClientHttp = new DefaultHttpClient();
            URI uri = new URI("https", WEB.URL, WEB.PASSENGER_REQUEST(login, trajetId, parcoursId), null, null);
            HttpPut requetePut = new HttpPut(uri);
            String body = m_ClientHttp.execute(requetePut, new BasicResponseHandler());
            return JsonParser.deserialiseStatus(new JSONObject(body));
        }catch (Exception e){
            throw e;
        }
    }

    /**
     * Retourne un parcours comme représenté sur le réseau
     * @param userLogin : L'identifiant de l'utilisateur pour le quelle on cherche les parcours
     * @return : L'objet parcours trouvé. S'il n'est pas trouvé, une exception est lancé.
     * @throws Exception : Lancé en cas d'erreur de connexion ou de conversion d'objet json.
     */
    public static List<Parcours> getUserParcours(String userLogin)throws Exception{
        try{
            List<Parcours> parcs= WEB.getAllParcours();
            List<Parcours> res = new ArrayList<Parcours>();
            for(Parcours p : parcs){
                if(p.driverId.equals(userLogin)){
                    res.add(p);
                }
            }
            return res;

        }catch (Exception e){
            throw e;
        }
    }

    /**
     * Retourne un parcours comme représenté sur le réseau
     * @param userLogin : L'identifiant de l'utilisateur pour le quelle on cherche les parcours
     * @return : L'objet parcours trouvé. S'il n'est pas trouvé, une exception est lancé.
     * @throws Exception : Lancé en cas d'erreur de connexion ou de conversion d'objet json.
     */
    public static List<Parcours> getUserParcoursWithTrajets(String userLogin)throws Exception{
        try{
            List<Parcours> parcs= WEB.getUserParcours(userLogin);
            parcs =  WEB.batchSetParcoursTrajets(parcs);
            return  parcs;
        }catch (Exception e){
            throw e;
        }
    }

    public static Parcours setParcoursTrajets(Parcours p){
        try {
            for(String trajet : p.remoteTrajetsIds){
                p.addTrajet(getCompleteTrajet(trajet));
            }
            return p;
        }catch (Exception e){
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    public static List<Parcours> batchSetParcoursTrajets(List<Parcours> list){
        try {
            for(Parcours p : list){
                setParcoursTrajets(p);
            }
            return list;
        }catch (Exception e){
            throw new IllegalArgumentException(e.getMessage());
        }
    }
    /**
     * Retourne un parcours comme représenté sur le réseau
     * @return : Les objets parcours trouvé. S'il n'est sont pas trouvés, une exception est lancé.
     * @throws Exception : Lancé en cas d'erreur de connexion ou de conversion d'objet json.
     */
    public static List<Parcours> getAllParcours()throws Exception{
        try{
            List<Parcours> parcs;
            HttpClient m_ClientHttp = new DefaultHttpClient();
            URI uri = new URI("https", WEB.URL, WEB.PARCOURS, null, null);
            HttpGet requeteGet = new HttpGet(uri);
            requeteGet.addHeader("Content-Type", "application/json");

            String body = m_ClientHttp.execute(requeteGet, new BasicResponseHandler());
            parcs = JsonParser.deserialiseParcoursList(new JSONArray(body));
            return parcs;
        }catch (Exception e){
            throw e;
        }
    }
    public static List<Parcours> getAllParcoursWithTrajets()throws Exception{
        try{
            List<Parcours> parcs= WEB.getAllParcours();
            parcs =  WEB.batchSetParcoursTrajets(parcs);
            return  parcs;
        }catch (Exception e){
            throw e;
        }
    }
    /**
     * Remplir les références d'adresse sur le serveur avec les objets complets.
     * @param trajetToFill : Le trajet qui ne contient que des référence d'adresses.
     * @return : Objet Trajet contenant les référence d'adresses et les objets d'adresses.
     * @throws Exception : Lancé en cas d'erreur de connexion ou autre.
     */
    public static Trajet fillTrajetAdresses(Trajet trajetToFill)throws Exception{
        try{
            trajetToFill.setDepart(WEB.getAddresse(trajetToFill.remoteDepartureAdresse));
            trajetToFill.setDestination(WEB.getAddresse(trajetToFill.remoteArrivalAdresse));
            return trajetToFill;
        }catch (Exception e){
            throw new IllegalArgumentException(e.getMessage());
        }
    }
    /**
     * Remplir les reference d'adresse sur le serveur avec les objets complets.
     * @param trajetsToFill : La liste des trajets qui ne contient que des référence d'adresses.
     * @return : Liste d'objets Trajet contenant les référence d'adresses et les objets d'adresses.
     * @throws Exception : Lancé en cas d'erreur de connexion ou autre.
     */
    public static List<Trajet> batchFillTrajetAdresses(List<Trajet> trajetsToFill)throws Exception{
        try{
            for(Trajet t : trajetsToFill){
                fillTrajetAdresses(t);
            }
            return trajetsToFill;
        }catch (Exception e){
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    /**
     * Retourne un utilisateur comme représenté sur le serveur
     * @param userId : Login de l'utilisateur
     * @return : Utilisateur s'il est trouvé. si non une exception est lancé.
     * @throws Exception
     */
    public static User getUser(String userId)throws Exception{
        HttpClient m_ClientHttp = new DefaultHttpClient();
        try {
            User temp;
            URI uri = new URI("https",WEB.URL , WEB.GET_USER(userId), null, null);
            HttpGet requeteGet = new HttpGet(uri);
            requeteGet.addHeader("Content-Type", "application/json");

            String body = m_ClientHttp.execute(requeteGet, new BasicResponseHandler());
            temp = JsonParser.deseriliserUser(new JSONObject(body));
            return temp;
        }catch (Exception e){
            throw e;
        }
    }

    public static List<Message> getUserMessages(String login){
        HttpClient m_ClientHttp = new DefaultHttpClient();
        List<Message> temp = new ArrayList<>();
        try {

            URI uri = new URI("https",WEB.URL , WEB.GET_USER_MESSAGES(login), null, null);
            HttpGet requeteGet = new HttpGet(uri);
            requeteGet.addHeader("Content-Type", "application/json");

            String body = m_ClientHttp.execute(requeteGet, new BasicResponseHandler());
            temp = JsonParser.deserialiseMessage(new JSONArray(body));
            return temp;
        }catch (Exception e){
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    public static Message getMessage(String login, String id){
        Message temp = new Message();
        try {
            List<Message> all = getUserMessages(login);
            for(Message m : all){
                if(m.remoteId.equals(id))
                    return m;
            }
            return temp;
        }catch (Exception e){
            throw new IllegalArgumentException(e.getMessage());
        }
    }
    public static void deleteMessage(String login, String id){
        HttpClient m_ClientHttp = new DefaultHttpClient();
        try {

            URI uri = new URI("https",WEB.URL , WEB.DELETE_MESSAGE(login, id), null, null);
            HttpDelete requeteDelete = new HttpDelete(uri);
            requeteDelete.addHeader("Content-Type", "application/json");

            String body = m_ClientHttp.execute(requeteDelete, new BasicResponseHandler());
        }catch (Exception e){
            throw new IllegalArgumentException(e.getMessage());
        }
    }

}
