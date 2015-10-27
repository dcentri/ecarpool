package com.dicentrix.ecarpool.access;

import android.content.res.Resources;

import com.dicentrix.ecarpool.R;
import com.dicentrix.ecarpool.user.User;

/**
 * Created by Akash on 10/19/2015.
 */
public class SignUp extends User {

    public String error;

    private String confirmPassword;

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public boolean isValid()
    {
        if(hasEmptyValues())
        {
            return false;
        }else if (!getPassword().equals(getConfirmPassword())){
            return false;
        }else
            return true;
    }

    private boolean hasEmptyValues(){
        return !(isNotEmpty(getLogin()) &&
                isNotEmpty(getFirstName())&&
                isNotEmpty(getLastName())&&
                isNotEmpty(getEmail())&&
                isNotEmpty(getPassword())&&
                isNotEmpty(getConfirmPassword()));
    }
    private boolean isNotEmpty(String testString)
    {
        return testString != null && !testString.equals("");
    }
}
