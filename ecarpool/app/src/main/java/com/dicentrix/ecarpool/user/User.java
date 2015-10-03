package com.dicentrix.ecarpool.user;

/**
 * Created by Akash on 9/26/2015.
 */
public class User {
    private int id;
    private String userName;
    private String firstName;
    private char userType;
    private char sex;
    private String email;
    private String phone;
    private Adresse adresse;

    public User() {
    }

    public User(Adresse adresse, String email, String firstName, int id, String phone, char sex, String userName, char userType) {
        this.adresse = adresse;
        this.email = email;
        this.firstName = firstName;
        this.id = id;
        this.phone = phone;
        this.sex = sex;
        this.userName = userName;
        this.userType = userType;
    }

    public Adresse getAdresse() {
        return adresse;
    }

    public void setAdresse(Adresse adresse) {
        this.adresse = adresse;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public char getSex() {
        return sex;
    }

    public void setSex(char sex) {
        this.sex = sex;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public char getUserType() {
        return userType;
    }

    public void setUserType(char userType) {
        this.userType = userType;
    }

    public class Adresse{
        private String civicNo;
        private String route;
        private String appart;
        private String postalCode;
        private String adresse;

        public Adresse() {
        }

        public Adresse(String adresse, String appart, String civicNo, String postalCode, String route) {
            this.adresse = adresse;
            this.appart = appart;
            this.civicNo = civicNo;
            this.postalCode = postalCode;
            this.route = route;
        }

        public String getAdresse() {
            return adresse;
        }

        public void setAdresse(String adresse) {
            this.adresse = adresse;
        }

        public String getAppart() {
            return appart;
        }

        public void setAppart(String appart) {
            this.appart = appart;
        }

        public String getCivicNo() {
            return civicNo;
        }

        public void setCivicNo(String civicNo) {
            this.civicNo = civicNo;
        }

        public String getPostalCode() {
            return postalCode;
        }

        public void setPostalCode(String postalCode) {
            this.postalCode = postalCode;
        }

        public String getRoute() {
            return route;
        }

        public void setRoute(String route) {
            this.route = route;
        }
    }
}
