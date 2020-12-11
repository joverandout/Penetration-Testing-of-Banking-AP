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
        try{
            URL[] urls = new URL[5];
            String[] whitelists = AuthController.getSafeSites();

            for (int i = 0; i < urls.length; i++) {
                if(i < 4) urls[i] = new URL("http://localhost:" + Spark.port() + "/auth?intern@wondoughbank.com&password=password&app=1&target=" + whitelists[i]);
                else urls[i] =  new URL("http://localhost:" + Spark.port() + "/auth?intern@wondoughbank.com&password=password&app=1&target=http://amazon.co.uk");
            }
            for (int j = 0; j < urls.length; j++) {
                try{
                    HttpURLConnection webconnection = (HttpURLConnection)urls[j].openConnection();
                    int status = webconnection.getResponseCode();
                    System.out.println(status);
                    webconnection.disconnect();
                }
                catch(NumberFormatException e){
                    System.out.println("FAILED");
                }

            }

            return "FAILED";
        }
        catch(IOException e){
            return "ERROR"+e.toString();
        }
    }
}

