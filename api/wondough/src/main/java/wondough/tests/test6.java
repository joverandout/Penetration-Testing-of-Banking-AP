package wondough.tests;

import java.sql.*;
import java.util.*;
import java.net.*;
import java.io.*;

import wondough.*;
import static wondough.SessionUtil.*;

public class test6 {

	public void test() {
        String result = checkUserCantTransferMoreThanBalance(); 
        System.out.println();
        System.out.print("Vulnerability 6:\t\t");
        if(result.equals("PASSED")) System.out.println((char)27 + "[32m" + result);
        else  System.out.println((char)27 + "[31m" + result);
        System.out.print((char)27 + "[30m"); 
        System.out.println("\tInsufficient iterations");
    }

    public String checkUserCantTransferMoreThanBalance(){		
        try {
            // initialise the HTTP request
            URL url = new URL("http://localhost:8000/auth");
            HttpURLConnection con = (HttpURLConnection)url.openConnection();
            con.setRequestMethod("POST");

            // write the request token to the request body
            con.setDoOutput(true);
            DataOutputStream out = new DataOutputStream(con.getOutputStream());
            out.writeBytes("username=intern@wondoughbank.com&password=password&appname=1&target=http://localhost:8080/oauth&g-recaptcha-response=1");
            out.flush();
            out.close();

            // get the response status code and check that it's OK
            int status = con.getResponseCode();
			// close the HTTP connection
            con.disconnect();

			// if the request succeeded, fail the test
            if(status == 302) return "failed";
            else return "passed";
        }
        catch(IOException ex) {
            return "failed" + ex.toString();
        }
    }
}

//REGEX-BASED STRNG SAITISATION

