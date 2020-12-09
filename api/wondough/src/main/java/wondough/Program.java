package wondough;

import static spark.Spark.awaitInitialization;
import static spark.Spark.get;
import static spark.Spark.port;
import static spark.Spark.post;
import static spark.Spark.staticFiles;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import spark.Spark;
import wondough.controllers.APIController;
import wondough.controllers.AuthController;
import wondough.tests.test1;
import wondough.tests.test2;
import wondough.tests.test3;
import wondough.tests.test4;
import wondough.tests.test5;
import wondough.tests.test6;





/**
* This class contains the main entry point for the application.
* @author  The Intern
* @version 0.1
*/
public class Program {
    private static final Logger LOG = LoggerFactory.getLogger(Program.class);
    
    /** Stores the singleton instance of this class. */
    private static Program program;

    /** Stores the security configuration for this application. */
    private SecurityConfiguration securityConfiguration;

    /** Stores the database connection. */
    private DbConnection connection;

    /** Gets the singleton instance of this class. */
    public static Program getInstance() {
        return program;
    }

    /** Gets the security configuration for this program. */
    public SecurityConfiguration getSecurityConfiguration() {
        return this.securityConfiguration;
    }

    /** Gets the database connection for this program. */
    public DbConnection getDbConnection() {
        return this.connection;
    }

    /** Explicitly mark constructor as private so no instances of this
    * class can be created elsewhere. */
    private Program() {

    }

    /**
    * The main entry point for the application.
    * @param args The command-line arguments supplied by the OS.
    */
    public static void main(String[] args) {
        // initialise and run the program
        program = new Program();
        program.run();
    }

    /**
     * Stores the port number of the server in a file so that the other process
     * knows which port to connect to.
     * @param port The port number to store.
     */
    private void savePort(int port) {
        try {
            String home = System.getProperty("user.home");
            File file = Paths.get(home, ".wondough-api").toFile();
            
            // the file should not exist 
            if(file.exists()) {
                LOG.warn(
                    "{} already exists, the server may not have shut down properly or may still be running", 
                    file.getAbsolutePath()
                );

                file.delete();
            }

            // write the port number to the file
            file.createNewFile();

            FileWriter writer = new FileWriter(file);
            writer.write(Integer.toString(port));
            writer.close();

            // delete the file on exit
            file.deleteOnExit();
        } catch (IOException e) {
            LOG.error("Unable to store port in file: {}", e.getMessage());
        }
    }

    /**
    * Runs the program.
    */
    private void run() {
        try
        {
            // load the security configuration from a file
            this.securityConfiguration =
                SecurityConfiguration.fromFile("security.json");

            // initialise the database connection
            this.connection = new DbConnection("wondough.db");

            // WondoughUser intern = new WondoughUser(1, "test44User@wondoughbank.com");
            // intern.setSalt(this.securityConfiguration.generateSalt());
            // intern.setHashedPassword(this.securityConfiguration.pbkdf2("password", intern.getSalt()));
            // intern.setIterations(this.securityConfiguration.getIterations());
            // intern.setKeySize(this.securityConfiguration.getKeySize());
            // connection.createUser(intern);

            // WondoughUser intern2 = new WondoughUser(1, "test44User@wondoughbank.com");
            // intern2.setSalt(this.securityConfiguration.generateSalt());
            // intern2.setHashedPassword(this.securityConfiguration.pbkdf2("password2", intern.getSalt()));
            // intern2.setIterations(this.securityConfiguration.getIterations());
            // intern2.setKeySize(this.securityConfiguration.getKeySize());
            // connection.createUser(intern2);


            // pick an arbitrary port
            port(0);

            // tell the Spark framework where to find static files
            staticFiles.location("/static");

            // map routes to controllers
            get("/auth", AuthController.serveAuthPage);
            post("/auth", AuthController.handleAuth);
            post("/exchange", AuthController.handleExchange);

            get("/transactions", "application/json",
                APIController.getTransactions, new JSONTransformer());
            post("/transactions/new", "application/json",
                APIController.postTransaction, new JSONTransformer());

            // wait for the server to start
            awaitInitialization();

            // get the port we are running on
            int port = Spark.port();

            // save the port number to a file so that it can be retrieved
            // by the other process
            this.savePort(port);

            // print something useful to stdout to tell users that the server
            // started successfully
            System.out.printf("\n\nServer started successfully and is running on port %d\n", port);

            test1 tests1 = new test1();
            tests1.test();

            test2 tests2 = new test2();
            tests2.test();

            test3 tests3 = new test3();
            tests3.test();

            test4 tests4 = new test4();
            tests4.test();

            test5 tests5 = new test5();
            tests5.test();

            // test6 tests6 = new test6();
            // tests6.test();
        }
        catch(Exception ex) {
            System.out.println(ex.toString());
        }
    }
}
