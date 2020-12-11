package wondough.controllers;

import java.util.*;
import java.net.*;
import java.security.Timestamp;
import java.sql.SQLException;

import spark.*;
import wondough.*;
import static wondough.SessionUtil.*;


public class AuthController {
    private static String[] safeSites = {"http://localhost:1464/oauth"};
    //sites that are considered safe. I have edited the sample-client such that it no longer runs on a random port but instead http://localhost:1464/oauth

    public static String[] getSafeSites(){
        return safeSites;
    }

    /** Serve the auth page (GET request) */
    public static Route serveAuthPage = (Request request, Response response) -> {
        Map<String, Object> model = new HashMap<>();
        
        String name = Program.getInstance().getDbConnection().lookupApp(Integer.parseInt(request.queryParams("app")));

        if(name == null) {
            response.status(400);
            return "Invalid appid.";
        }

        model.put("appname", name);
        model.put("target", request.queryParams("target"));

        System.out.println(model);

        return ViewUtil.render(request, model, "/velocity/auth.vm");
    };

    public static Route handleExchange = (Request request, Response response) -> {
        // retrieve the request token from the request
        Program.getInstance().getDbConnection().removeOldTokens();
        String token = request.queryParams("token");


        String accessToken = Program.getInstance().getDbConnection().exchangeToken(token);

        if(accessToken == null) {
            response.status(400);
            return "Invalid request token.";
        }
        else {
            return accessToken;
        }
    };

   
    public static Route handleAuth = (Request request, Response response) -> {
        Program.getInstance().getDbConnection().removeOldTokens();


        Map<String, Object> model = new HashMap<>();
        model.put("target", request.queryParams("target"));
        model.put("appname", request.queryParams("appname"));

        // retrieve the username and password from the request
        String username = request.queryParams("username");
        String password = request.queryParams("password");

        // make sure the username and password aren't empty
        if (username.isEmpty() || password.isEmpty()) {
            model.put("error", "Empty username or password!");
            return ViewUtil.render(request, model, "/velocity/auth.vm");
        }

        // try to find the user in the database
        WondoughUser user = null;

        try {
            user = Program.getInstance().getDbConnection().getUser(username);

            if(user == null) {
                model.put("error", "No such user!");
                return ViewUtil.render(request, model, "/velocity/auth.vm");
            }
        }
        catch(SQLException ex) {
            model.put("error", ex.toString());
            return ViewUtil.render(request, model, "/velocity/auth.vm");
        }

        // retrieve the global security configuration
        SecurityConfiguration config = Program.getInstance().getSecurityConfiguration();

        // hash the plain text password supplied by the client using the
        // security configuration for this particular user
        String hashedPassword = config.pbkdf2(password, user.getSalt(), user.getIterations(), user.getKeySize());

        // check that the hashed passwords match
        if(!user.getHashedPassword().equals(hashedPassword)) {
            model.put("error", "Incorrect password!");
            return ViewUtil.render(request, model, "/velocity/auth.vm");
        }

        // check that the user's configuration is up-to-date;
        // if not, re-hash the password
        if(user.getIterations() != config.getIterations() ||
            user.getKeySize() != config.getKeySize()) {

        }

        // authorise an app
        WondoughApp app = null;

        try {
            // create an authorisation for this user
            app = Program.getInstance().getDbConnection().createApp(user);

            if(app == null) {
                model.put("error", "Couldn't authorise application!");
                return ViewUtil.render(request, model, "/velocity/auth.vm");
            }
        }
        catch(SQLException ex) {
            model.put("error", ex.toString());
            return ViewUtil.render(request, model, "/velocity/auth.vm");
        }

        // redirect the user somewhere, if this was requested
        if (getQueryLoginRedirect(request) != null) {
            // redirect to the target URL and append the token;
            // the token is hashed for security so that its
            // value cannot be read

            // System.out.println("QUERY LOGIN REDIRECT-> " + getQueryLoginRedirect(request));
            if(trustedURL(getQueryLoginRedirect(request))){
                response.redirect(
                getQueryLoginRedirect(request) +
                "?token=" + URLEncoder.encode(config.md5(app.getRequestToken()), "UTF-8")
                );
            }
            else{
                model.put("error", getQueryLoginRedirect(request) + " isn't a trusted website");
                return ViewUtil.render(request, model, "/velocity/auth.vm");
            }
            
        }

        return ViewUtil.render(request, model, "/velocity/auth.vm");
    };

    public static boolean trustedURL(String url){
        for (int i = 0; i < safeSites.length; i++) {
            if(url.equals(safeSites[i])) return true;
        }
        return false;
    }

}
