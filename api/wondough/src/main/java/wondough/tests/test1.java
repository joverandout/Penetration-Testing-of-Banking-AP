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
	}

	private String runSQLInjection() {
        String[] testSQLInjections = {"blank' OR 1=1;--", "blank' OR 'x'='x';--", "blank' OR '*;--", "blank' OR TRUE;--"};
		DbConnection db = Program.getInstance().getDbConnection();
        try {
			for (int i = 0; i <= 3; i++) {
				if (db.getUser(testSQLInjections[i]) instanceof WondoughUser) return "FAILED";
			}
        } catch (SQLException e) {
            return "FAILED" + e.toString();
        }
		return "PASSED";
    }
}
