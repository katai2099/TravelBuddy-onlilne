package com.example.travelbuddyv2;

import org.junit.Before;
import org.junit.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.*;

public class registerActivityUnitTest {

    private String getLocalPartOfEmail(String email){
        String userName_Regex = "^[\\w-\\.]+";
        Pattern pattern = Pattern.compile(userName_Regex);
        Matcher matcher = pattern.matcher(email);
        if(matcher.find()){
            return matcher.group(0);
        }
        return "";
    }

    private boolean isPasswordStrongEnough(String password) {
        String password_regex = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#&()–[{}]:;',?/*~$^+=<>]).{8,20}$";
        Pattern pattern = Pattern.compile(password_regex);
        Matcher matcher = pattern.matcher(password);
        return matcher.matches();
    }

    public boolean isEmailCorrect(String email) {
        String email_regex = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";
        Pattern pattern = Pattern.compile(email_regex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
    @Test
    public void checkIsEmailCorrect(){
        boolean actual=isEmailCorrect("kataix2@gmail.com");
        assertTrue(actual);
        boolean checkFalse=isEmailCorrect("k__t@x.c");
        assertFalse(checkFalse);
    }
    @Test
    public void checkIsPasswordStrongEnough(){
        boolean actual=isPasswordStrongEnough("RandomPassword123@");
        assertTrue(actual);
        boolean checkFalse = isPasswordStrongEnough("random123@");
        assertFalse(checkFalse);
    }
    @Test
    public void getLocalPartOfEmail(){
        String actual=getLocalPartOfEmail("kataix2@gmail.com");
        assertEquals("kataix2",actual);
    }

}