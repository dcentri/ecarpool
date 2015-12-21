package com.dicentrix.ecarpool.util;

import android.widget.Toast;

import com.dicentrix.ecarpool.access.Connection;
import com.dicentrix.ecarpool.parcours.FrequenceTrajet;
import com.dicentrix.ecarpool.parcours.Parcours;
import com.dicentrix.ecarpool.parcours.Trajet;
import com.dicentrix.ecarpool.user.User;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.plus.model.people.Person;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Akash on 12/17/2015.
 */
public class JsonParser {

    public static JSONObject serialiseUser(User usr){
        try{
            JSONObject jUsr = new JSONObject();
            jUsr.put("login", usr.getLogin());
            jUsr.put("password", usr.getPassword());
            jUsr.put("firstName", usr.getFirstName());
            jUsr.put("lastName", usr.getLastName());
            jUsr.put("userType", usr.getType());
            jUsr.put("gender", String.valueOf(usr.getGender()));
            jUsr.put("phone", usr.getPhone());
            jUsr.put("email", usr.getEmail());
            jUsr.put("phone", usr.getPhone());
            jUsr.put("userAddress", serialiseAddresse(usr.getAddress()));
            return jUsr;
        }
        catch (JSONException e){
            System.out.println(e.getMessage());
            return null;
        }
    }
    public static User deseriliserUser(JSONObject jUsr){
        try{
            User usr = new User();
            usr.setLogin(jUsr.getString("login"));
            usr.setPassword(jUsr.getString("password"));
            usr.setFirstName(jUsr.getString("firstName"));
            usr.setLastName(jUsr.getString("lastName"));
            usr.setType(jUsr.getString("userType").equals("DRIVER") ? User.UserType.DRIVER : User.UserType.PASSENGER);
            usr.setGender(jUsr.getString("gender").trim().charAt(0));
            usr.setPhone(jUsr.getString("phone"));
            usr.setEmail(jUsr.getString("email"));
            usr.remoteAddress = jUsr.getString("idAddress");
            JSONArray array = jUsr.getJSONArray("listDemandesParcours");
            usr.remoteDemandeParcours = new String[array.length()];
            for (int i = 0; i < array.length(); i++)
                usr.remoteDemandeParcours[i] = array.getString(i);
            array = jUsr.getJSONArray("listeDemandesTrajet");
            usr.reomoteDemandeTrajet = new String[array.length()];
            for (int i = 0; i < array.length(); i++)
                usr.reomoteDemandeTrajet[i] = array.getString(i);
            return usr;
        }
        catch (JSONException e){
            System.out.println(e.getMessage());
            return null;
        }
    }
    public static JSONObject serialiseParcours(Parcours parcours){
        try{
            JSONObject jPar = new JSONObject();

            jPar.put("nbPlaces", parcours.getNbPlaces());
            jPar.put("price", parcours.getPrice());
            DecimalFormat df = new DecimalFormat("#.####");
            df.setRoundingMode(RoundingMode.CEILING);
            jPar.put("km", Double.parseDouble(df.format(parcours.getKm())));
            jPar.put("trajetDefault", serialiseTrajet(parcours.getDefaultTrajet()));
            return jPar;
        }
        catch (JSONException e){
            System.out.println(e.getMessage());
            return null;
        }
    }

    public  static List<Parcours> deserialiseParcoursList(JSONArray listParcs){
        try{
            List<Parcours> resList = new ArrayList<Parcours>();
            for (int i = 0; i < listParcs.length(); i++) {
                resList.add(deserialiseParcours((JSONObject)listParcs.get(i)));
            }
            return resList;
        }catch (Exception e){
            return null;
        }
    }
    public  static List<Trajet> deserialiseTrajetsList(JSONArray listTrajs){
        try{
            List<Trajet> resList = new ArrayList<Trajet>();
            for (int i = 0; i < listTrajs.length(); i++) {
                resList.add(deserialiseTrajet((JSONObject)listTrajs.get(i)));
            }
            return resList;
        }catch (Exception e){
            return null;
        }
    }

    public static Parcours deserialiseParcours(JSONObject parc){
        try{
            Parcours p = new Parcours();
            p.setNbPlaces(parc.getInt("nbPlaces"));
            p.remoteId = parc.getString("id");
            p.driverId = parc.getString("driver");
            p.setKm((float) parc.getDouble("km"));
            JSONArray trajets = parc.getJSONArray("trajets");
            String[] trajetsTemps = new String[trajets.length()];
            for(int i = 0 ; i < trajets.length(); i++){
                trajetsTemps[i] = String.valueOf(trajets.get(i));
            }
            p.remoteTrajetsIds = trajetsTemps;
            return p;
        }
        catch (JSONException e){
            System.out.println(e.getMessage());
            return null;
        }
    }
    public static JSONObject serialiseTrajet(Trajet traj){
        try{
            JSONObject jPar = new JSONObject();
            jPar.put("id", traj.remoteId == null ? "" : traj.remoteId);
            jPar.put("idAuthor",traj.getAuthor().getLogin());
            SimpleDateFormat format = new SimpleDateFormat("MMM dd yyyy hh:mmaaa");
            jPar.put("departureDateTime", format.format(traj.getDepartDateTime()));
            jPar.put("arrivalDateTime", format.format(traj.getDepartDateTime()));
            jPar.put("frequency", traj.getFrequence().getName());
            jPar.put("departureAddress", serialiseAddresse(traj.getDepart()));
            jPar.put("arrivalAddress", serialiseAddresse(traj.getDestination()));
            return jPar;
        }
        catch (JSONException e){
            System.out.println(e.getMessage());
            return null;
        }
    }

    public static Trajet deserialiseTrajet(JSONObject jTraj){
        try{
            Trajet traj = new Trajet();
            traj.remoteId = jTraj.getString("id");
            traj.idAuthor = jTraj.getString("idAuthor");
            traj.setDepartDateTime(dateTryParse(jTraj.getString("departureDateTime")));
            traj.setArrivalDateTime(dateTryParse(jTraj.getString("arrivalDateTime")));
            traj.setFrequence(new FrequenceTrajet(jTraj.getString("frequency")));
            traj.remoteDepartureAdresse = jTraj.getString("departureAddress");
            traj.remoteArrivalAdresse = jTraj.getString("arrivalAddress");
            return traj;
        }
        catch (JSONException e){
            System.out.println(e.getMessage());
            return null;
        }
    }

    private static Date dateTryParse(String date){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        try {
            return format.parse(date);
        }catch (ParseException e){
            return new Date();
        }
    }

    public static JSONObject serialiseCon(Connection con){
        try{
            JSONObject jCon = new JSONObject();
            jCon.put("login",con.getLogin());
            jCon.put("password", con.getPassword());
            return jCon;
        }
        catch (JSONException e){
            System.out.println(e.getMessage());
            return null;
        }
    }
    public static LatLng deseriliaseLatLong(JSONObject jFullDetails){
        try{
            JSONArray results = (JSONArray)jFullDetails.get("results");
            JSONObject geometry = (JSONObject)((JSONObject)results.get(0)).get("geometry");
            JSONObject location = (JSONObject)geometry.get("location");
            return new LatLng((Double)location.get("lat"), ((Double) location.get("lng")));
        }
        catch (JSONException e){
            System.out.println(e.getMessage());
            return null;
        }
        catch (Exception e){
            System.out.println(e.getMessage());
            return null;
        }
    }

    public static Address deserialiseGoogleAddresse(JSONObject addresse){
        Address res = new Address();
        try {
            JSONArray results = (JSONArray)addresse.get("results");
            JSONArray address_components = (JSONArray)((JSONObject)results.get(0)).get("address_components");
            JSONArray types;
            for(int i = 0; i < address_components.length(); i++){
                types = (JSONArray)((JSONObject)address_components.get(i)).get("types");
                if(ArrayContains("street_number", types)){
                    res.setCivicNo((String)((JSONObject)address_components.get(i)).get("long_name"));
                }else if(ArrayContains("route", types)){
                    res.setRouteName((String)((JSONObject)address_components.get(i)).get("long_name"));
                }else if(ArrayContains("locality", types)){
                    res.setRouteName(res.getRouteName() + ", " + (String)((JSONObject)address_components.get(i)).get("long_name"));
                }else if(ArrayContains("administrative_area_level_1", types)){
                    res.setRouteName(res.getRouteName() + ", " + (String)((JSONObject)address_components.get(i)).get("short_name"));
                }else if(ArrayContains("country", types)){
                    res.setRouteName(res.getRouteName() + ", " + (String) ((JSONObject) address_components.get(i)).get("short_name"));
                }else if(ArrayContains("postal_code", types)){
                    res.setPostalCode((String) ((JSONObject) address_components.get(i)).get("short_name"));
                }
            }
            LatLng coords = deseriliaseLatLong(addresse);
            if(coords != null){
                res.setLatCoord(String.valueOf(coords.latitude));
                res.setLongCoord(String.valueOf(coords.longitude));
            }
            return  res;
        }
        catch (Exception e){
            return  res;
        }

    }
    private static boolean ArrayContains(String evalString, JSONArray testArray){
        try {
            for(int i =0; i< testArray.length(); i++){
                if(evalString.equals((String)testArray.get(i)))
                    return true;
            }
            return  false;
        }catch (Exception e){
            return false;
        }

    }
    public static JSONObject serialiseAddresse(Address addrs)
    {
        try{
            JSONObject jAddrs = new JSONObject();
            jAddrs.put("id", addrs.remoteId == null? "": addrs.remoteId);
            jAddrs.put("civicNo", addrs.getCivicNo());
            jAddrs.put("routeName", addrs.getRouteName());
            jAddrs.put("postalCode", addrs.getPostalCode());
            jAddrs.put("appartNo", addrs.getAppart() == null ? "": addrs.getAppart());
            jAddrs.put("long", addrs.getLongCoord()== null ? "0":addrs.getLongCoord());
            jAddrs.put("lat", addrs.getLatCoord()== null ? "0":addrs.getLatCoord());
            return  jAddrs;
        }
        catch (JSONException ex){
            System.out.println(ex.getMessage());
            return null;
        }
    }
    public static Address deserialiseAddresse(JSONObject addresse){
        Address res = new Address();
        try {
            res.setLongCoord(addresse.getString("id"));
            res.setCivicNo(addresse.getString("civicNo"));
            res.setPostalCode(addresse.getString("postalCode"));
            res.setRouteName(addresse.getString("routeName"));
            res.setAppart(addresse.getString("appartNo"));
            res.setLongCoord(addresse.getString("long"));
            res.setLatCoord(addresse.getString("lat"));
            return res;
        }catch (Exception e){
            return  res;
        }
    }

}
