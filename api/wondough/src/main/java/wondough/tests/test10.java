package wondough.tests;


import static spark.Spark.awaitInitialization;
import static spark.Spark.get;
import static spark.Spark.port;
import static spark.Spark.post;
import static spark.Spark.staticFiles;

import spark.Spark;

import java.sql.*;
import java.util.*;
import java.net.*;
import java.io.*;
import wondough.*;
import wondough.controllers.AuthController;

import static wondough.SessionUtil.*;

public class test10 {

	public void test() {
        String result = checkwhitelist(); 
        System.out.println();
        System.out.print("Vulnerability 10:\t\t");
        if(result.equals("PASSED")) System.out.println((char)27 + "[32m" + result);
        else  System.out.println((char)27 + "[31m" + result);
        System.out.print((char)27 + "[30m"); 
        System.out.println("\tSites aren't whitelisted");
    }

    public String checkwhitelist(){	
        try {
            String[] whitelists = AuthController.getSafeSites();

            
            URL url = new URL("http://localhost:8000/auth?");
            HttpURLConnection con = (HttpURLConnection)url.openConnection();
            con.setRequestMethod("POST");

            con.setDoOutput(true);
            DataOutputStream out = new DataOutputStream(con.getOutputStream());
            out.writeBytes("username=intern@wondoughbank.com&password=password&appname=1&target=https://www.google.com");
            out.flush();
            out.close();

            int status = con.getResponseCode();
            con.disconnect();

            System.out.println(status);

            if(status == 302) return "failed";
            else return "passed";
        }
        catch(IOException ex) {
            return "FAILED" + ex.toString();
        }
    }
}
