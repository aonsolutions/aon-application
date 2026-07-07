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


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.StringTokenizer;
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
import org.apache.velocity.app.Velocity;
import org.codehaus.plexus.util.StringUtils;

import net.aonsolutions.core.dbutils.AonSQLException;
import com.code.aon.master.VersionManager;

/**
 * @author rtrepiana
 */
@Mojo(name = "generate-db", defaultPhase = LifecyclePhase.GENERATE_SOURCES, threadSafe = true)
public class DBMojo extends AbstractMojo {

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

    /**
     * Comma separated list of database tables to generate files for. <br/>
     */
    @Parameter(property = "dbtables")
    protected String dbtables;


    /**
     * Specifies the java package for generated files.
     */
    @Parameter(defaultValue = "com.esferalia.aon.master.sql")
    private String targetPackage ;

    /**
     * Specifies the destination directory where should generate files. <br/>
     */
    @Parameter(defaultValue = "${project.build.directory}/generated-sources/aon-master")
    protected File outputDirectory;

    /**
     * Comma separated template file names present in the <code>sourceDirectory</code>
     * directory. <br/>
     */
    @Parameter(property = "templates", defaultValue = "AbstractSQL.java.vm,SQLReader.java.vm,SQLWriter.java.vm,SQLConstants.java.vm")
    protected String templates;



    @Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		synchronized( LOCK) {
	    	validateParameters();

	    	Connection connection = null;
	        try {
	        	VersionManager versionManager = new VersionManager();
	        	if ("aon-master".equals(dbName)) {
	        		getLog().info("Using default database: " + dbName);
		        	connection = getConnection(null);
		        	getLog().info("Ensure database structure. Dropping ... ");
			        dropDataBase( connection , dbName );
			        getLog().info("Ensure database structure. Creating ... ");
			        versionManager.createDatabase(connection, dbName);
			        connection.close();
			        connection = getConnection(dbName);
	        	} else {
	        		try {
	        			connection = getConnection(dbName);
	        		} catch ( SQLException e ) {
	        			getLog().info("Can not connect to " + dbName);
	        			connection = getConnection(null);
				        getLog().info("Creating ... " + dbName);
				        versionManager.createDatabase(connection, dbName);
				        connection.close();
				        connection = getConnection(dbName);
	        		}
	        	}

	        	getLog().info("Ensure database structure. Updating... ");
		        versionManager.uptodateDatabase(connection);

		        DatabaseMetaData dbMetaData = connection.getMetaData();
				DBContext dbContext = new DBContext(dbMetaData);

				dbContext.put(packageVariable, targetPackage);
				dbContext.put(tablesVariable, dbContext.getTables(getTables()) );

				String templateArr [] = getTemplates();
				for (String  template : templateArr) {

			    	Writer writer = null;
			    	Reader reader = null;

			    	try {
						reader = getAsReader(template);
						writer = new FileWriter(getOuputFile(template));
						Velocity.evaluate(dbContext, writer, "DBContext", reader);
		        	}
		        	finally {
		        		if ( reader != null )
		        			reader.close();
		        		if ( writer != null )
		        			writer.close();
		        	}
				}

		        if ( project != null )
		        {
		            project.addCompileSourceRoot( outputDirectory.getAbsolutePath() );
		        }

			} catch ( IOException e ) {
				throw new MojoExecutionException( e.getMessage(), e );
			} catch ( SQLException e ) {
				throw new MojoExecutionException( e.getMessage(), e );
			} catch ( AonSQLException e ) {
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

    private String [] getTables() {
    	return toArray(dbtables);
    }

    private String [] getTemplates() {
    	return toArray(templates);
    }

    private Reader getAsReader(String template) {
    	ClassLoader cl = DBMojo.class.getClassLoader();
    	InputStream in = cl.getResourceAsStream(sourceDirectory + template );
    	return new InputStreamReader(in);
    }

    private File getOuputFile(String template) {
    	String targetDir = targetPackage.replace('.', '/');
    	File packageDir = new File(outputDirectory, targetDir);
    	packageDir.mkdirs();
    	int endIndex = template.lastIndexOf('.');
    	String javaFileName = template.substring(0, endIndex );
    	return new File(packageDir, javaFileName);
    }

    private String [] toArray(String commaList ) {
    	ArrayList<String> strings = new ArrayList<String>();
    	StringTokenizer tk = new StringTokenizer(commaList, ",");
    	while (tk.hasMoreTokens() ) {
    		strings.add(tk.nextToken() );
    	}
    	return strings.toArray(new String []{});
    }


    /**
     * Drops database ..
     */
    private void dropDataBase(Connection connection, String dbName)
    	throws SQLException {
    	Statement stmt  = null;
    	try  {
	    	stmt = connection.createStatement();
	    	stmt.execute("DROP DATABASE `" + dbName + "`");
    	}catch (SQLException e) {
    		// If database not exist, better, continue.
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
    private void validateParameters()
        throws MojoExecutionException
    {
        if ( ( StringUtils.isEmpty( dbtables ) )  )
        {
            StringBuffer msg = new StringBuffer();
            msg.append( "aon plugin parameters are invalid/missing." ).append( '\n' );
            msg.append( "Inside the definition for plugin 'aon-maven-plugin' specify the following:" ).append( '\n' );
            msg.append( '\n' );
            msg.append( "<configuration>" ).append( '\n' );
            msg.append( "  <dbtables>VALUE</dbtables>" ).append( '\n' );
            msg.append( "</configuration>" ).append( '\n' );

            throw new MojoExecutionException( msg.toString() );
        }
    }

    private Connection getConnection(String dbName)
        throws MojoExecutionException, SQLException {

    	try {
    		// first of all load JDBC driver
    		Class.forName("com.mysql.jdbc.Driver");
    	}catch (ClassNotFoundException e) {
    		throw new MojoExecutionException( e.getMessage(), e );
		}
    	String url = String.format("jdbc:mysql://%s:%s/%s",
    			dbHost, dbPort, dbName != null ? dbName : "");

		Properties properties = new Properties();
		properties.setProperty("user", dbUser);
		properties.setProperty("password", dbPasswd);
		properties.setProperty("useSSL", dbUseSSL);
		properties.setProperty("serverTimezone", dbTimeZone);
		return DriverManager.getConnection(url, properties);

    }


}
