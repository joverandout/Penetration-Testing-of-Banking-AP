package wondough.tests;

import java.sql.*;

import wondough.*;
import static wondough.SessionUtil.*;

public class test1 {

	public void test() {
        System.out.println();
        System.out.println("TESTS:\n");

        String result = runSQLInjection();
        System.out.print("Vulnerability 1:\t\t");
        if(result.equals("PASSED")) System.out.println((char)27 + "[32m" + result);
        else  System.out.println((char)27 + "[31m" + result);
        System.out.print((char)27 + "[30m");
        System.out.println("\tSQL Injection");
	}

	private String runSQLInjection() {
        String[] testSQLInjections = {"blank' OR 1=1;--", "blank' OR 'x'='x';--", "blank' OR '*;--", "blank' OR TRUE;--"};
        //These are the 4 sql injection attacks used to gain access
		DbConnection db = Program.getInstance().getDbConnection(); //connect to the database
        try {
            for (int i = 0; i <= 3; i++) { //execute each of the attacks
                //if a user is returned then the fix has not worked and the bug is still active
				if (db.getUser(testSQLInjections[i]) instanceof WondoughUser) return "FAILED";
			}
        } catch (SQLException e) {
            return "FAILED" + e.toString();
        }
		return "PASSED";
    }
}
