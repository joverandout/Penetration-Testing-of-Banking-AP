package wondough.tests;

import java.sql.*;

import wondough.*;
import static wondough.SessionUtil.*;

public class test5 {

	public void test() {
        String result = checkUserCantTransferMoreThanBalance(); 
        System.out.println();
        System.out.print("Vulnerability 5:\t\t");
        if(result.equals("PASSED")) System.out.println((char)27 + "[32m" + result);
        else  System.out.println((char)27 + "[31m" + result);
        System.out.print((char)27 + "[30m"); 
        System.out.println("\tInsufficient iterations");
    }

    public String checkUserCantTransferMoreThanBalance(){		
        try{
            DbConnection connection = new DbConnection("wondough.db");
            SecurityConfiguration securityConfiguration = Program.getInstance().getSecurityConfiguration();

            WondoughUser testuser1 = new WondoughUser(1, "test5User@wondoughbank.com");
            testuser1.setSalt(securityConfiguration.generateSalt());
            testuser1.setHashedPassword(securityConfiguration.pbkdf2("password", testuser1.getSalt()));
            testuser1.setIterations(securityConfiguration.getIterations());
            testuser1.setKeySize(securityConfiguration.getKeySize());
            
            if(testuser1.getIterations() == 1) return "FAILED";
            if(testuser1.getIterations() != 10000) return "FAILED";

            try{
                Connection connectionToDelete = DriverManager.getConnection("jdbc:sqlite:" + "wondough.db");
                String query = "DELETE FROM users WHERE username='test5User@wondoughbank.com'";
                Statement stmt = connectionToDelete.createStatement();
                stmt.executeUpdate(query);
                return "PASSED";
            }
            catch(SQLException e){
                System.out.println(e.toString());
                return "FAILED";
            }
        }
        catch (SQLException e){
            System.out.println(e.toString());
            return "FAILED";
        }

    }
}

//REGEX-BASED STRNG SAITISATION

