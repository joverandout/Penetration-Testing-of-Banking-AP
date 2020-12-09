package wondough;

import org.apache.commons.lang.StringEscapeUtils;

import jdk.nashorn.api.tree.StatementTree;

import java.sql.*;
import java.util.Calendar;
import java.time.LocalDate; // import the LocalDate class
import java.time.LocalDateTime;
import java.time.Instant;
import java.util.concurrent.TimeUnit;

/**
* Represents a connection to the not-quite-as-volatile database.
* @author  The Intern
* @version 0.1
*/
public class DbConnection {
    /** The database connection to use. */
    private Connection connection;

    /**
    * Initialises a new database connection.
    * @param filename The name of the SQLite database file.
    */
    public DbConnection(String filename) throws SQLException {
        // construct the connection string
        String url = "jdbc:sqlite:" + filename;

        // connect to the database
        this.connection = DriverManager.getConnection(url);
    }

    /**
    * Retrieves the next User ID to use.
    */
    private int largestUserID() throws SQLException {
        Statement stmt = null;
        String query = "SELECT id FROM users ORDER BY id DESC LIMIT 1;";

        try {
            stmt = this.connection.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            if(rs.next()) {
                return rs.getInt("id") + 1;
            }
        } catch (SQLException e ) {
            throw e;
        } finally {
            if (stmt != null) { stmt.close(); }
        }

        return 0;
    }

    /**
    * Retrieves the next request token ID to use.
    */
    private int largestRequestToken() throws SQLException {
        Statement stmt = null;
        String query = "SELECT requestToken FROM authorised_apps ORDER BY requestToken DESC LIMIT 1;";

        try {
            stmt = this.connection.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            if(rs.next()) {
                return rs.getInt("requestToken") + 1;
            }
        } catch (SQLException e ) {
            throw e;
        } finally {
            if (stmt != null) { stmt.close(); }
        }

        return 0;
    }

    /**
    * Retrieves the next access token ID to use.
    */
    private int largestAccessToken() throws SQLException {
        Statement stmt = null;
        String query = "SELECT accessToken FROM authorised_apps ORDER BY accessToken DESC LIMIT 1;";

        try {
            stmt = this.connection.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            if(rs.next()) {
                return rs.getInt("accessToken") + 1;
            }
        } catch (SQLException e ) {
            throw e;
        } finally {
            if (stmt != null) { stmt.close(); }
        }

        return 0;
    }

    /**
    * Inserts the specified user account into the database. This method
    * assumes that the ID of the user is not set to anything.
    * @param user The user account to insert.
    */
    public boolean createUser(WondoughUser user) throws SQLException {
        //we need to check if a username already exists:

        if(getUser(user.getUsername()) != null){
            // System.out.println("User already exists");
            return false;
        }

        // get the next available ID for this user
        int id = this.largestUserID();

        // create a prepared statement to insert the user account
        // into the database
        Statement stmt = null;
        String query = "INSERT INTO users (id,username,password,salt,iterations,keySize) VALUES (" + id + ", '" + user.getUsername() + "' , '" + user.getHashedPassword() + "' , '" + user.getSalt() + "' ," + user.getIterations() + "," + user.getKeySize() + ");";

        // try to insert the user into the database
        try {
            stmt = this.connection.createStatement();
            stmt.executeUpdate(query);
            return true;
        } catch (SQLException e ) {
            throw e;
        } finally {
            if (stmt != null) { stmt.close(); }
        }
    }

    /**
    * Looks up a user by their username.
    * @param username The username to lookup.
    */
    public WondoughUser getUser(String username) throws SQLException {
        PreparedStatement stmt = null;
        String query = "SELECT * FROM users WHERE username=? LIMIT 1;";

        try {
            //the use of prepared statements mean the sql query is loaded
            //before the username is added to it preventing sql injection
            //on login
            stmt = this.connection.prepareStatement(query);
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if(rs.next()) {
                WondoughUser user = new WondoughUser(rs.getInt("id"), rs.getString("username"));
                user.setHashedPassword(rs.getString("password"));
                user.setSalt(rs.getString("salt"));
                user.setIterations(rs.getInt("iterations"));
                user.setKeySize(rs.getInt("keySize"));
                return user;
            }
        } catch (SQLException e ) {
            // return null;
            throw e;
        } finally {
            if (stmt != null) { stmt.close(); }
        }

        return null;
    }

    /**
    * Looks up whether an app exists and returns the display name of the
    * application if successful.
    * @param id The ID of the application.
    */
    public String lookupApp(int id) throws SQLException {
        PreparedStatement stmt = null;
        String query = "SELECT name FROM apps WHERE appid=? LIMIT 1;";

        try {
            stmt = this.connection.prepareStatement(query);
            stmt.setInt(1, id);

            ResultSet rs = stmt.executeQuery();

            if(rs.next()) {
                return rs.getString("name");
            }
        } catch (SQLException e ) {
            throw e;
        } finally {
            if (stmt != null) { stmt.close(); }
        }

        return null;
    }

    public void removeOldTokens(){
        try{
            long ut1 = System.currentTimeMillis();
            System.out.println(ut1);
            String query3 = "DELETE FROM authorised_apps WHERE expiryDate < "+ ut1 + ";";
            System.out.println(query3);
            Statement stmt2 = this.connection.createStatement();
            int rs = stmt2.executeUpdate(query3);
            System.out.println("DELETE");
        }
        catch (SQLException e){
            System.out.println(e.toString());
        }
    }


    /**
    * Authorises a new application to perform actions on behalf
    * of the specified user.
    * @param user The user for whom the app should be registered.
    */
    public WondoughApp createApp(WondoughUser user) throws SQLException {
        PreparedStatement stmt = null;
        String query = "INSERT INTO authorised_apps (user,requestToken,accessToken,expiryDate) VALUES (?,?,?,?);";

        try {
            System.out.println(user.getID());
            System.out.println(user.getUsername());

            WondoughApp app = new WondoughApp(user.getID());
            app.setRequestToken(Program.getInstance().getSecurityConfiguration().generateSalt());
            app.setAccessToken(Program.getInstance().getSecurityConfiguration().generateSalt());

            stmt = this.connection.prepareStatement(query);
            stmt.setInt(1, user.getID());
            stmt.setString(2, null);
            stmt.setString(3, app.getAccessToken());
            Timestamp now = new Timestamp(System.currentTimeMillis());
            now.setTime(now.getTime() + TimeUnit.MINUTES.toMillis(30));
            stmt.setTimestamp(4, now);
            stmt.executeUpdate();

            stmt = this.connection.prepareStatement(query);
            stmt.setInt(1, user.getID());
            stmt.setString(2, app.getRequestToken());
            stmt.setString(3, null);
            now = new Timestamp(System.currentTimeMillis());
            now.setTime(now.getTime() + TimeUnit.DAYS.toMillis(120));
            stmt.setTimestamp(4, now);
            stmt.executeUpdate();

            stmt.close();
           
            return app;
        } catch (SQLException e ) {
            throw e;
        } finally {
            if (stmt != null) { stmt.close(); }
        }        
    }

    /**
    * Exchanges a request token for an access token.
    * @param requestToken The request token to exchange.
    */
    public String exchangeToken(String requestToken) throws SQLException {
        SecurityConfiguration config = Program.getInstance().getSecurityConfiguration();
        Statement stmt = null;
        String query = "SELECT requestToken, accessToken FROM authorised_apps;";

        try {
            stmt = this.connection.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            while(rs.next()) {
                String token = config.md5(rs.getString("requestToken"));

                if(token.equals(requestToken)) {
                    return config.md5(rs.getString("accessToken"));
                }
            }
        } catch (SQLException e ) {
            throw e;
        } finally {
            if (stmt != null) { stmt.close(); }
        }

        return null;
    }

    /**
    * Validates whether the specified string is a valid access token and returns
    * the unique ID of the user it belongs to.
    * @param accessToken The access token to validate.
    */
    public Integer isValidAccessToken(String accessToken) throws SQLException {
        SecurityConfiguration config = Program.getInstance().getSecurityConfiguration();
        Statement stmt = null;
        String query = "SELECT user, accessToken FROM authorised_apps;";

        try {
            stmt = this.connection.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            while(rs.next()) {
                String token = config.md5(rs.getString("accessToken"));

                if(token.equals(accessToken)) {
                    return rs.getInt("user");
                }
            }
        } catch (SQLException e ) {
            throw e;
        } finally {
            if (stmt != null) { stmt.close(); }
        }

        return null;
    }

    /**
    * Looks up a user by their username and returns their unique ID.
    * @param username The username to lookup.
    */
    public Integer findUserByName(String username) throws SQLException {
        PreparedStatement stmt = null;
        String query = "SELECT id FROM users WHERE username=? LIMIT 1;";

        try {
            stmt = this.connection.prepareStatement(query);
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if(rs.next()) {
                return rs.getInt("id");
            }
        } catch (SQLException e ) {
            throw e;
        } finally {
            if (stmt != null) { stmt.close(); }
        }

        return null;
    }

    /**
    * Creates a new transaction.
    * @param user The ID of the user sending the money.
    * @param recipient The ID of the recipient of the money.
    * @param description The description of the transaction.
    * @param amount The amount that is being transferred.
    */
    public boolean createTransaction(int user, int recipient, String description, float amount) throws SQLException {
        // don't allow users to send negative amounts
        if(amount < 0) {
            return false;
        }

        //Below is the fix for being able to transfer more money than available
        if(amount > getTransactions(user).getAccountBalance()){
            return false;
        }

        //Below is the fix for preventing javascript injection
        //By html encoding the description it prevents any script being interpreted as a script
        //As such the issue is fixed
        String encodedVersionToStopJSInjection = StringEscapeUtils.escapeHtml(description);

        PreparedStatement creditStmt = null;
        PreparedStatement debitStmt = null;
        String creditQuery = "INSERT INTO transactions (uid,value,description) VALUES (?,?,?)";
        String debitQuery = "INSERT INTO transactions (uid,value,description) VALUES (?,?,?)";

        try {
            creditStmt = this.connection.prepareStatement(creditQuery);
            debitStmt = this.connection.prepareStatement(debitQuery);

            debitStmt.setInt(1, user);
            debitStmt.setFloat(2, -amount);
            debitStmt.setString(3, encodedVersionToStopJSInjection);

            debitStmt.executeUpdate();

            creditStmt.setInt(1, recipient);
            creditStmt.setFloat(2, amount);
            creditStmt.setString(3, encodedVersionToStopJSInjection);

            creditStmt.executeUpdate();

            return true;
        } catch (SQLException e ) {
            throw e;
        } finally {
            if (creditStmt != null) { creditStmt.close(); }
            if (debitStmt != null) { debitStmt.close(); }
        }
    }

    /**
    * Gets all transactions for a user.
    * @param user The unique ID of the user to look up transactions for.
    */
    public Transactions getTransactions(int user) throws SQLException {
        PreparedStatement stmt = null;
        String query = "SELECT * FROM transactions WHERE uid=? ORDER BY tid DESC;";

        try {
            stmt = this.connection.prepareStatement(query);
            stmt.setInt(1, user);
            ResultSet rs = stmt.executeQuery();

            Transactions result = new Transactions();
            float total = 0.0f;

            while(rs.next()) {
                Transaction t = new Transaction(rs.getInt("tid"));
                t.setAmount(rs.getFloat("value"));
                t.setDescription(rs.getString("description"));
                result.addTransaction(t);

                total += t.getAmount();
            }

            result.setAccountBalance(total);

            return result;
        } catch (SQLException e ) {
            throw e;
        } finally {
            if (stmt != null) { stmt.close(); }
        }
    }

    /**
    * Closes the database connection.
    */
    public void close() throws SQLException {
        this.connection.close();
    }
}
