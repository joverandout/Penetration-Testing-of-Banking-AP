package wondough.tests;

import java.sql.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

import javax.swing.plaf.nimbus.State;

import java.net.*;
import java.io.*;

import wondough.*;
import static wondough.SessionUtil.*;

public class test9 {

	public void test() {
        String result = checktokensexpireHaveDifferentExpiryDatesAndAreDifferent(); 
        System.out.println();
        System.out.print("Vulnerability 9:\t\t");
        if(result.equals("PASSED")) System.out.println((char)27 + "[32m" + result);
        else  System.out.println((char)27 + "[31m" + result);
        System.out.print((char)27 + "[30m"); 
        System.out.println("\tTokens expire and are different");
    }

    public String checktokensexpireHaveDifferentExpiryDatesAndAreDifferent(){		
        try{
            //connect to the database
            DbConnection connection = new DbConnection("wondough.db");
            SecurityConfiguration securityConfiguration = Program.getInstance().getSecurityConfiguration();

            //create a new user
            WondoughUser testuser1 = new WondoughUser(3, "test9User@wondoughbank.com");
            testuser1.setSalt(securityConfiguration.generateSalt());
            testuser1.setHashedPassword(securityConfiguration.pbkdf2("password", testuser1.getSalt()));
            testuser1.setIterations(securityConfiguration.getIterations());
            testuser1.setKeySize(securityConfiguration.getKeySize());

            WondoughApp app = connection.createApp(testuser1);
            //add their tokens to the database using createapp

            if(app.getAccessToken() == app.getRequestToken()) return "FAILED";

            //fetch the tokens from the database
            Connection dbCon = DriverManager.getConnection("jdbc:sqlite:" + "wondough.db");
            String query = "SELECT * FROM authorised_apps WHERE accessToken = '" + app.getAccessToken() + "'";
            Statement stmt = dbCon.createStatement();
            long expiryDate = 0;

            ResultSet rs = stmt.executeQuery(query);
            while(rs.next()){
                expiryDate = rs.getLong(4); //get the tokens
            }

            //create the times which the expiry times must be within
            Timestamp acc = new Timestamp(System.currentTimeMillis());
            acc.setTime(acc.getTime() + TimeUnit.MINUTES.toMillis(28));
            long acc1 = acc.getTime();
            acc.setTime(acc.getTime() + TimeUnit.MINUTES.toMillis(5));
            long acc2 = acc.getTime();
            //check that they are NOT within the boundaries and if so fail the test
            if(!(expiryDate > acc1 && expiryDate < acc2)) return "FAILED";

            //get the second token
            query = "SELECT * FROM authorised_apps WHERE requestToken = '" + app.getRequestToken() + "'";
            stmt = dbCon.createStatement();

            rs = stmt.executeQuery(query);
            while(rs.next()){
                expiryDate = rs.getLong(4);
            }
            
            //produce the second tokens boundaries for its expiry
            acc = new Timestamp(System.currentTimeMillis());
            acc.setTime(acc.getTime() + TimeUnit.MINUTES.toMillis(58));
            acc1 = acc.getTime();
            acc.setTime(acc.getTime() + TimeUnit.MINUTES.toMillis(5));
            acc2 = acc.getTime();
            //if NOT within those boundaries fail
            if(!(expiryDate > acc1 && expiryDate < acc2)) return "FAILED";
            
            try{ //clean up the database by removing the tokens - very important since they grant access
                Connection connectionToDelete = DriverManager.getConnection("jdbc:sqlite:" + "wondough.db");
                String query2 = "DELETE FROM users WHERE username='test9User@wondoughbank.com'";
                Statement stmt2 = connectionToDelete.createStatement();
                stmt2.executeUpdate(query2);
                Connection connectionToDelete1 = DriverManager.getConnection("jdbc:sqlite:" + "wondough.db");
                String query21 = "DELETE FROM authorised_apps WHERE user = 3";
                Statement stmt21 = connectionToDelete1.createStatement();
                stmt21.executeUpdate(query21);
                return "PASSED";
            }
            catch(SQLException e){
                System.out.println(e.toString());
                return "FAILED";
            }

        }
        catch (SQLException e){
            System.out.println(e.toString());
            return "FAILED";
        }
    }
}

