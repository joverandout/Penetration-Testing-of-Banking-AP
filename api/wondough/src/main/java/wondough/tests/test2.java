package wondough.tests;

import java.sql.*;

import wondough.*;
import static wondough.SessionUtil.*;

public class test2 {

	public void test() {
        System.out.println();
        String result = checkUserCantTransferMoreThanBalance(); 

        System.out.print("Vulnerability 2:\t\t");
        if(result.equals("PASSED")) System.out.println((char)27 + "[32m" + result);
        else  System.out.println((char)27 + "[31m" + result);
        System.out.print((char)27 + "[30m"); 
    }

    public String checkUserCantTransferMoreThanBalance(){
        return "FAILED";
    }
}

//REGEX-BASED STRNG SAITISATION

