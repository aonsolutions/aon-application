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
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectHelper;

import net.aonsolutions.core.dbutils.AonSQLException;
import com.code.aon.master.VersionManager;

/**
 * @author rtrepiana
 *
 * @goal 	create-db
 * @phase	generate-sources
 * @requiresDependencyResolution
 * @threadSafe
 *
 */
public class DBCreate extends AbstractMojo {

	private static final Object LOCK = new Object();

    // ----------------------------------------------------------------------
    // Mojo parameters
    // ----------------------------------------------------------------------

    /**
     * Specifies the variable name in template's context for tables.
     *
     * @parameter default-value="tables"
     * @readonly
     */
    protected String tablesVariable;

    /**
     * Specifies the variable name in template's context for targetPackage.
     *
     * @parameter default-value="package"
     * @readonly
     */
    protected String packageVariable;

    /**
     * Specifies the directory containing template files.
     *
     * @parameter default-value="com/esferalia/aon/master/vm/"
     * @readonly
     */
    protected String sourceDirectory;

    /**
     * The Maven Project Object
     *
     * @parameter expression="${project}"
     * @readonly
     */
    protected MavenProject project;

    /**
     * The maven project's helper.
     *
     * @component role="org.apache.maven.project.MavenProjectHelper"
     * @readonly
     */
    private MavenProjectHelper projectHelper;

    // ----------------------------------------------------------------------
    // aon parameters
    // ----------------------------------------------------------------------

    /**
     * Host to connect to.
     *
     * @parameter default-value="127.0.0.1"
     */
    private String dbHost ;

    /**
     * Port number to use for connection.
     *
     * @parameter default-value="3306"
     */
    private String dbPort;

    /**
     *
     * @parameter default-value="aon-master"
     */
    private String dbName ;

    /**
     * User for login.
     *
     * @parameter default-value="dbuser"
     */
    private String dbUser ;

    /**
     * Password to use when connecting to server.
     *
     * @parameter default-value="serubd2000"
     */
    private String dbPasswd ;

		/**
     * TimeZone to use when connecting to server.
     *
     * @parameter default-value="Europe/Madrid"
     */
    private String dbTimeZone ;

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
