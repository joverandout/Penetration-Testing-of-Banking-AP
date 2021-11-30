package wondough.tests;

import java.sql.*;
import java.util.*;
import java.net.*;
import java.io.*;

import wondough.*;
import static wondough.SessionUtil.*;

public class test6 {

	public void test() {
        String result = checkGetUserReturnstheCorrectID(); 
        System.out.println();
        System.out.print("Vulnerability 6:\t\t");
        if(result.equals("PASSED")) System.out.println((char)27 + "[32m" + result);
        else  System.out.println((char)27 + "[31m" + result);
        System.out.print((char)27 + "[30m"); 
        System.out.println("\tAll users login as 0");
    }

    public String checkGetUserReturnstheCorrectID(){		
        try{
            //connect to the database
            DbConnection connection = new DbConnection("wondough.db");
            SecurityConfiguration securityConfiguration = Program.getInstance().getSecurityConfiguration();
            Connection dbCon = DriverManager.getConnection("jdbc:sqlite:" + "wondough.db");

            WondoughUser testUser6 = connection.getUser("hacker@wondoughbank.com");
            //get the hacker user uasing get user

            String query = "SELECT id FROM users WHERE username='hacker@wondoughbank.com' LIMIT 1;";
            Statement stmt = dbCon.createStatement();
            //create a statement to get the id from the database
            int correctid = -1;

            ResultSet rs = stmt.executeQuery(query);
            while(rs.next()){
                correctid = rs.getInt(1);
            }
            stmt.close();
            rs.close();

            //check ids match and they are both 1
            if(testUser6.getID() == correctid && correctid == 1) return "PASSED";
            else return "FAILED";
        }
        catch (SQLException e){
            System.out.println(e.toString());
            return "FAILED";
        }
    }
}
