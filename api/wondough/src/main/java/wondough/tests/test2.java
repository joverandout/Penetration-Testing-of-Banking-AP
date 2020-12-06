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
        Connection connection;
        try{ //connect to the database
        String url = "jdbc:sqlite:" + "wondough.db";
        connection = DriverManager.getConnection(url);
        }
        catch(SQLException e){
            System.out.println("Failed to even connect: "+ e.toString());
            return"FAILED";
        }

        //add 100 pounds to the testuser
		PreparedStatement creditStmt = null;
        String creditQuery = "INSERT INTO transactions (uid,value,description) VALUES (?,?,?)";

		try {
            creditStmt = connection.prepareStatement(creditQuery);

            creditStmt.setInt(1, 2);
            creditStmt.setFloat(2, (float) 100.0);
            creditStmt.setString(3, "test");

            creditStmt.executeUpdate();

            if (creditStmt != null) { creditStmt.close(); }
        } catch (SQLException e) {
            System.out.println("FAILED" + e.toString());
			return "FAILED";
        }

        DbConnection db = Program.getInstance().getDbConnection();
		try {
			if (db.createTransaction(2, 2, "test", (float) db.getTransactions(2).getAccountBalance()+1) == true) {
				return "FAILED"; 
			} else {
				try {
                    if (db.createTransaction(2, 2, "test", (float) db.getTransactions(2).getAccountBalance()) == true) {
                        try{
                            Connection connectionToDelete = DriverManager.getConnection("jdbc:sqlite:" + "wondough.db");
                            String query = "DELETE FROM transactions WHERE description='test'";
                            Statement stmt = connectionToDelete.createStatement();
                            stmt.executeUpdate(query);
                            return "PASSED";
                        }
                        catch(SQLException e){
                            System.out.println(e.toString());
                            return "FAILED";
                        }          
                    } else {
                        return "FAILED"; 
                    }
                } catch (SQLException e) {
                    return "FAILED" + e.toString();
                }
			}
        }
        catch (SQLException e) {
			return "FAILED" + e.toString();
		}
    }
}

//REGEX-BASED STRNG SAITISATION

