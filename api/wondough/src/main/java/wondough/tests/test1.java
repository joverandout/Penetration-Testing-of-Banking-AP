package wondough;

import java.sql.*;

public class test1 {

	public void test() {
		System.out.println(this.runSQLInjection());
	}

	private String runSQLInjection() {
        String[] testSQLInjections = {"blank' OR 1=1;--", "blank' OR 'x'='x';--", "blank' OR '*;--", "blank' OR TRUE;--"};
		DbConnection db = Program.getInstance().getDbConnection();
        try {
			for (int i = 0; i <= 4; i++) {
				if (db.getUser(testSQLInjections[i]) instanceof WondoughUser) return "FAILED";
			}
        } catch (SQLException e) {
            return "FAILED" + e.toString();
        }
		return "PASSED";
    }
}
