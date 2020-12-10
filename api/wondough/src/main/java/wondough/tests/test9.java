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
            DbConnection connection = new DbConnection("wondough.db");
            SecurityConfiguration securityConfiguration = Program.getInstance().getSecurityConfiguration();

            WondoughUser testuser1 = new WondoughUser(1, "test9User@wondoughbank.com");
            testuser1.setSalt(securityConfiguration.generateSalt());
            testuser1.setHashedPassword(securityConfiguration.pbkdf2("password", testuser1.getSalt()));
            testuser1.setIterations(securityConfiguration.getIterations());
            testuser1.setKeySize(securityConfiguration.getKeySize());

            WondoughApp app = connection.createApp(testuser1);

            if(app.getAccessToken() == app.getRequestToken()) return "FAILED";

            Connection dbCon = DriverManager.getConnection("jdbc:sqlite:" + "wondough.db");
            String query = "SELECT * FROM authorised_apps WHERE accessToken = '" + app.getAccessToken() + "'";
            Statement stmt = dbCon.createStatement();
            long expiryDate = 0;

            ResultSet rs = stmt.executeQuery(query);
            while(rs.next()){
                expiryDate = rs.getLong(4);
            }

            Timestamp acc = new Timestamp(System.currentTimeMillis());
            acc.setTime(acc.getTime() + TimeUnit.MINUTES.toMillis(28));
            long acc1 = acc.getTime();
            acc.setTime(acc.getTime() + TimeUnit.MINUTES.toMillis(5));
            long acc2 = acc.getTime();
            if(!(expiryDate > acc1 && expiryDate < acc2)) return "FAILED";

            query = "SELECT * FROM authorised_apps WHERE requestToken = '" + app.getRequestToken() + "'";
            stmt = dbCon.createStatement();

            rs = stmt.executeQuery(query);
            while(rs.next()){
                expiryDate = rs.getLong(4);
            }

            acc = new Timestamp(System.currentTimeMillis());
            acc.setTime(acc.getTime() + TimeUnit.MINUTES.toMillis(58));
            acc1 = acc.getTime();
            acc.setTime(acc.getTime() + TimeUnit.MINUTES.toMillis(5));
            acc2 = acc.getTime();
            if(!(expiryDate > acc1 && expiryDate < acc2)) return "FAILED";
            
            try{
                Connection connectionToDelete = DriverManager.getConnection("jdbc:sqlite:" + "wondough.db");
                String query2 = "DELETE FROM users WHERE username='test9User@wondoughbank.com'";
                Statement stmt2 = connectionToDelete.createStatement();
                stmt2.executeUpdate(query2);
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

//REGEX-BASED STRNG SAITISATION

