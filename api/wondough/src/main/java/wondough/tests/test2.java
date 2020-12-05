package wondough.tests;

import java.sql.*;

import wondough.*;
import static wondough.SessionUtil.*;

public class test2 {

	public void test() {
        String result = checkUserCantTransferMoreThanBalance(); 
        System.out.println();
        System.out.print("Vulnerability 2:\t\t");
        if(result.equals("PASSED")) System.out.println((char)27 + "[32m" + result);
        else  System.out.println((char)27 + "[31m" + result);
        System.out.print((char)27 + "[30m"); 
        System.out.println("\tSending more than balance");
    }

    public String checkUserCantTransferMoreThanBalance(){
        return "FAILED";
    }
}

//REGEX-BASED STRNG SAITISATION

