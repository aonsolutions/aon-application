/**
 *
 */
package com.esferalia.aon.maven.plugin;

/********************************************************************
* Copyright (c) 2011, esferalia NETWORKS S.A
*
* The copyright of the computer program herein is the property
* of esferalia NETWORKS.
*********************************************************************
* The program may be used and/or copied only with the written
* permission of esferalia NETWORKS, or in accordance with the
* terms and conditions stipulated in the agreement contract
* under which the program has been supplied.
*********************************************************************
*/


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.Properties;
import java.util.TimeZone;


import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectHelper;

import net.aonsolutions.core.dbutils.AonSQLException;
import com.code.aon.master.VersionManager;

/**
 * @author rtrepiana
 */
@Mojo(name = "create-db", defaultPhase = LifecyclePhase.GENERATE_SOURCES, threadSafe = true)
public class DBCreate extends AbstractMojo {

	private static final Object LOCK = new Object();

    // ----------------------------------------------------------------------
    // Mojo parameters
    // ----------------------------------------------------------------------

    /**
     * Specifies the variable name in template's context for tables.
     */
    @Parameter(defaultValue = "tables", readonly = true)
    protected String tablesVariable;

    /**
     * Specifies the variable name in template's context for targetPackage.
     */
    @Parameter(defaultValue = "package", readonly = true)
    protected String packageVariable;

    /**
     * Specifies the directory containing template files.
     */
    @Parameter(defaultValue = "com/esferalia/aon/master/vm/", readonly = true)
    protected String sourceDirectory;

    /**
     * The Maven Project Object
     */
    @Parameter(defaultValue = "${project}", readonly = true)
    protected MavenProject project;

    /**
     * The maven project's helper.
     */
    @Component
    private MavenProjectHelper projectHelper;

    // ----------------------------------------------------------------------
    // aon parameters
    // ----------------------------------------------------------------------

    /**
     * Host to connect to.
     */
    @Parameter(defaultValue = "127.0.0.1")
    private String dbHost ;

    /**
     * Port number to use for connection.
     */
    @Parameter(defaultValue = "3306")
    private String dbPort;

    /**
     */
    @Parameter(defaultValue = "aon-master")
    private String dbName ;

    /**
     * User for login.
     */
    @Parameter(defaultValue = "dbuser")
    private String dbUser ;

    /**
     * Password to use when connecting to server.
     */
    @Parameter(defaultValue = "serubd2000")
    private String dbPasswd ;

		/**
     * TimeZone to use when connecting to server.
     */
    @Parameter(defaultValue = "Europe/Madrid")
    private String dbTimeZone ;

		/**
     *  Establishing SSL connection.
     */
    @Parameter(defaultValue = "false")
    private String dbUseSSL ;

    @Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		synchronized( LOCK) {
	    	validateParameters();

	    	Connection connection = null;
	        try {

	        	connection = getConnection(null);
		        dropDataBase( connection , dbName );
		        VersionManager versionManager = new VersionManager();
		    	getLog().info("Creating database: " + dbName);
		        versionManager.createDatabase(connection, dbName);
		        connection.close();
				connection = getConnection(dbName);
		        versionManager.uptodateDatabase(connection);
		    	getLog().info("Created!");
			} catch ( SQLException e ) {
		    	getLog().error(e.getMessage());
				throw new MojoExecutionException( e.getMessage(), e );
			} catch ( AonSQLException e ) {
		    	getLog().error(e.getMessage());
				throw new MojoExecutionException( e.getMessage(), e );
			} finally {
				if ( connection != null ) {
					try {
						connection.close();
					} catch (SQLException e) {
					}
				}
			}
		}
	}

    /**
     * Drops database ..
     */
    private void dropDataBase(Connection connection, String dbName)
    	throws SQLException {
    	Statement stmt  = null;
    	try  {
	    	stmt = connection.createStatement();
	    	String sentence = "DROP DATABASE `" + dbName + "`";
	    	getLog().info("Trying to drop database: " + sentence);
	    	stmt.execute(sentence);
    	}catch (SQLException e) {
	    	getLog().info("Database not dropped: " + e.getMessage());
		}
    	finally{
    		if ( stmt != null )
    			stmt.close();
    	}
    }

    /**
     * dbtables is required
     *
     * @throws MojoExecutionException
     */
    private void validateParameters() throws MojoExecutionException {

    }

    private Connection getConnection(String dbName) throws MojoExecutionException {
    	try {
    		// first of all load JDBC driver
    		Class.forName("com.mysql.jdbc.Driver");
    	}catch (ClassNotFoundException e) {
    		throw new MojoExecutionException( e.getMessage(), e );
		}
    	String url = String.format("jdbc:mysql://%s:%s/%s", dbHost, dbPort, dbName != null ? dbName : "");
    	getLog().info("Database creation URL:" + url);
    	try {
			Properties properties = new Properties();
			properties.setProperty("user", dbUser);
			properties.setProperty("password", dbPasswd);
			properties.setProperty("useSSL", dbUseSSL);
			properties.setProperty("allowPublicKeyRetrieval", "true");
			properties.setProperty("serverTimezone", dbTimeZone);
			Connection c = DriverManager.getConnection(url, properties);
	    	getLog().info("Connected!");
	    	return c;
		} catch (SQLException e) {
	    	getLog().error(e.getMessage());
    		throw new MojoExecutionException( e.getMessage(), e );
		}

    }


}
