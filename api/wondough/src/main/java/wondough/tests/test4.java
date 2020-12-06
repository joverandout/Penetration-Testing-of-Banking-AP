package wondough.tests;

import java.sql.*;

import wondough.*;
import static wondough.SessionUtil.*;

public class test4 {

	public void test() {
        String result = checkUserCantTransferMoreThanBalance(); 
        System.out.println();
        System.out.print("Vulnerability 4:\t\t");
        if(result.equals("PASSED")) System.out.println((char)27 + "[32m" + result);
        else  System.out.println((char)27 + "[31m" + result);
        System.out.print((char)27 + "[30m"); 
        System.out.println("\tDuplicate usernames");
    }

    public String checkUserCantTransferMoreThanBalance(){		
        try{
            DbConnection connection = new DbConnection("wondough.db");
            SecurityConfiguration securityConfiguration = Program.getInstance().getSecurityConfiguration();

            WondoughUser testuser1 = new WondoughUser(1, "test3User@wondoughbank.com");
            testuser1.setSalt(securityConfiguration.generateSalt());
            testuser1.setHashedPassword(securityConfiguration.pbkdf2("password", testuser1.getSalt()));
            testuser1.setIterations(securityConfiguration.getIterations());
            testuser1.setKeySize(securityConfiguration.getKeySize());
            
            if((connection.createUser(testuser1)) == false) return "FAILED";

            WondoughUser testuser2 = new WondoughUser(1, "test3User@wondoughbank.com");
            testuser2.setSalt(securityConfiguration.generateSalt());
            testuser2.setHashedPassword(securityConfiguration.pbkdf2("password", testuser1.getSalt()));
            testuser2.setIterations(securityConfiguration.getIterations());
            testuser2.setKeySize(securityConfiguration.getKeySize());

            if((connection.createUser(testuser1)) == true) return "FAILED";

            try{
                Connection connectionToDelete = DriverManager.getConnection("jdbc:sqlite:" + "wondough.db");
                String query = "DELETE FROM users WHERE username='test3User@wondoughbank.com'";
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

