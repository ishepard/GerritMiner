package org.davidespadini.gerrit;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.google.gerrit.extensions.restapi.RestApiException;

public class Runner 
{
	// EXAMPLE
	public static final String DatabaseURL = "jdbc:mysql://..../gerritProject";
	public static final String gerritURL = "https://.....";
	public static final String databaseName = "gerritProject";

    public static void main( String[] args ) throws RestApiException, SQLException
    {
    	if (args.length != 2){
    		System.out.println("Usage: java -jar <jar_name> <startpoint> <endpoint>");
    		System.exit(-1);
    	}
    	// ID of the first review
    	int startpoint = Integer.parseInt(args[1]);
    	
    	// ID of the last review
    	int endpoint = Integer.parseInt(args[2]);

    	GerritMiner gm = new GerritMiner(connectDatabase(DatabaseURL), databaseName, gerritURL);
    	gm.start(startpoint, endpoint);
    }

    public static Connection connectDatabase(String connectionUrl){
    	String url = connectionUrl;
    	String username = "xxx";
    	String password = "xxx";

    	System.out.println("Connecting database...");

    	try {
    		Connection connection = DriverManager.getConnection(url, username, password);
    	    System.out.println("Database connected!");
    	    return connection;
    	} catch (SQLException e) {
    	    throw new IllegalStateException("Cannot connect the database!", e);
    	}
    }
}
