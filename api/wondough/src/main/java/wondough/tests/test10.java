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

            for (int i = 0; i < whitelists.length; i++) {
                urls[i] = new URL("http://localhost:" + Spark.port() + "/auth?app=1&target=" + whitelists[i]);
            }
            urls[4] = new URL("http://localhost:" + Spark.port() + "/auth?app=1&target=http://localhost:1464/oauth");

            for (int j = 0; j < urls.length; j++) {
                HttpURLConnection webconnection = (HttpURLConnection)urls[j].openConnection();
                int status = webconnection.getResponseCode();
                System.out.println(status);
            }

            return null;
        }
        catch(IOException e){
            return e.toString();
        }
    }
}

