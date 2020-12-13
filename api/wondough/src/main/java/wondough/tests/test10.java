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
        String result = "FAILED";
        try{
            result = checkwhitelist(); 
        }
        catch (Exception e){
            result = "FAILED";
        }
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

            URL url = new URL("http://localhost:"+ Spark.port() + "/auth?");
            HttpURLConnection con = (HttpURLConnection)url.openConnection();
            con.setRequestMethod("POST");
            con.setDoOutput(true);
            DataOutputStream bytes = new DataOutputStream(con.getOutputStream());
            bytes.writeBytes("username=intern@wondoughbank.com&password=password&appname=1&target=https://www.bbc.co.uk");
            bytes.flush();
            bytes.close();

            int status = con.getResponseCode();
            con.disconnect();
            if(status == 302) return "FAILED"; //302 means the site has redirected to bbc.co.uk which is not a safe site so the test has failed
        }
        catch(IOException ex) {
            return "FAILED";
        }

        try {
            String[] whitelists = AuthController.getSafeSites();

            URL url = new URL("http://localhost:"+ Spark.port() + "/auth?");
            HttpURLConnection con = (HttpURLConnection)url.openConnection();
            con.setRequestMethod("POST");
            con.setDoOutput(true);
            DataOutputStream bytes = new DataOutputStream(con.getOutputStream());
            bytes.writeBytes("username=intern@wondoughbank.com&password=password&appname=1&target=https://http://localhost:1464/oauth");
            bytes.flush();
            bytes.close();

            int status = con.getResponseCode();
            con.disconnect();

            if(status != 200) return "FAILED";
        }
        catch(IOException ex) {
            return "FAILED";
        }

        return "PASSED";
    }
}
