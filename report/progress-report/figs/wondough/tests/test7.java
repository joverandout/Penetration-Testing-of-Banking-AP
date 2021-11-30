package wondough.tests;

import java.sql.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

import javax.swing.plaf.nimbus.State;

import java.net.*;
import java.io.*;

import wondough.*;
import static wondough.SessionUtil.*;

public class test7 {

	public void test() {
        String result = checkdifferentsalts(); 
        System.out.println();
        System.out.print("Vulnerability 7:\t\t");
        if(result.equals("PASSED")) System.out.println((char)27 + "[32m" + result);
        else  System.out.println((char)27 + "[31m" + result);
        System.out.print((char)27 + "[30m"); 
        System.out.println("\tSalt generation isn't random");
    }

    public String checkdifferentsalts(){		
        try{
            DbConnection connection = new DbConnection("wondough.db");
            SecurityConfiguration securityConfiguration = Program.getInstance().getSecurityConfiguration();
            //fetch the secuirty configuration
            //declare a 100,000 length array
            String[] salts = new String[100000];

            for (int i = 0; i < 100000; i++) {
                //generate a salt for all of those values
                salts[i] = securityConfiguration.generateSalt();
            }

            //check if they are distinct by calling the distinct function
            if(areDistinct(salts)) return "PASSED";
            else return "FAILED";
        }
        catch (SQLException e){
            System.out.println(e.toString());
            return "FAILED";
        }
    }


    public static boolean areDistinct(String[] salts) 
    { 
        // Put array into HashSet 
        Set<String> s = new HashSet<String>(Arrays.asList(salts)); 
  
        // If all elements are the same HashSet will be same size as array. 
        return (s.size() == salts.length); 
    } 
}

