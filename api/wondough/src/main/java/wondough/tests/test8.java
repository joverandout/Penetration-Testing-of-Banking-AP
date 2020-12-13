package wondough.tests;

import java.sql.*;

import wondough.*;
import static wondough.SessionUtil.*;

public class test8 {

	public void test() {
        String result = checkUserCantTransferMoreThanBalance(); 
        System.out.println();
        System.out.print("Vulnerability 8:\t\t");
        if(result.equals("PASSED")) System.out.println((char)27 + "[32m" + result);
        else  System.out.println((char)27 + "[31m" + result);
        System.out.print((char)27 + "[30m"); 
        System.out.println("\tPassword change");
    }

    public String checkUserCantTransferMoreThanBalance(){		
        try{
            DbConnection connection = new DbConnection("wondough.db");
            SecurityConfiguration securityConfiguration = Program.getInstance().getSecurityConfiguration();

            WondoughUser testuser1 = new WondoughUser(3, "test8User@wondoughbank.com");
            testuser1.setSalt(securityConfiguration.generateSalt());
            testuser1.setHashedPassword(securityConfiguration.pbkdf2("password", testuser1.getSalt()));
            testuser1.setIterations(securityConfiguration.getIterations());
            testuser1.setKeySize(securityConfiguration.getKeySize());
            
            if((connection.createUser(testuser1)) == false) return "FAILED";

            try{
                if(connection.changePassword(testuser1.getUsername(), testuser1.getHashedPassword(), "password")) return "FAILED";
                connection.changePassword(testuser1.getUsername(), testuser1.getHashedPassword(), "password2");
                String hashed1 = "a", hashed2 = "b";
                hashed1 = testuser1.getHashedPassword();
                Connection connectionToGet = DriverManager.getConnection("jdbc:sqlite:" + "wondough.db");
                String query1 = "SELECT password FROM users WHERE username='test8User@wondoughbank.com'";
                Statement stmt1 = connectionToGet.createStatement();
                ResultSet rs1 = stmt1.executeQuery(query1);
                while(rs1.next()){
                    hashed2 = rs1.getString(1);
                }

                stmt1.close();
                rs1.close();

                Connection connectionToDelete = DriverManager.getConnection("jdbc:sqlite:" + "wondough.db");
                String query = "DELETE FROM users WHERE username='test8User@wondoughbank.com'";
                Statement stmt = connectionToDelete.createStatement();
                stmt.executeUpdate(query);

                if(hashed1.equals(hashed2)) return "FFAILED";
                
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


