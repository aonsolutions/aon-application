/**
 * 
 */
package com.code.aon.master.maven.plugin;


import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.velocity.app.Velocity;

import com.code.aon.master.VersionManager;

/**
 * @author rtrepiana
 *
 * @goal 	DB
 * @phase	generate-sources	
 * @requiresDependencyResolution 
 */
public class DBMojo extends AbstractMojo implements FilenameFilter{
	
	
    /**
     * @parameter default-value="jdbc:mysql://volga:3306"
     */
    private String url = "jdbc:mysql://volga:3306";
	
    /**
     * @parameter default-value="aon-master"
     */
    private String dbName = "aon-master";
    /**
     * @parameter default-value="dbuser"
     */
    private String user = "dbuser";

    /**
     * @parameter default-value="serubd2000"
     */
    private String password = "serubd2000";
    
    /**
     * @parameter default-value="src/main/resources/com/code/aon/master/vm" 
     */
    private File vmDir = new File("src/main/resources/com/code/aon/master/vm");
    
    /**
     * @parameter default-value="src/main/java/com/code/aon/master/db"
     */
    private File javaOut = new File ( "src/main/java/com/code/aon/master/db" ) ;



    @Override
	public void execute() throws MojoExecutionException, MojoFailureException {
    	Writer out = null;
    	Reader in = null;
    	Connection connection = null;
    	
		// first of all load JDBC driver
        try {
			Class.forName("org.gjt.mm.mysql.Driver");
	        
			connection = 
	        	DriverManager.getConnection(url, user, password);
	        dropDataBase( connection , dbName );
	        VersionManager versionManager = new VersionManager();
	        versionManager.createDatabase(connection, dbName);

	        connection.close();
			connection = 
	        	DriverManager.getConnection(url+"/"+ dbName, user, password);
	        versionManager.uptodateDatabase(connection);
	        
	        DatabaseMetaData dbMetaData = connection.getMetaData(); 
			DBContext dbContext = new DBContext(dbMetaData);
			
			File vms [] = vmDir.listFiles(this);
			
	        for (File  vm : vms) {
	        	try {
					in = new FileReader(vm);
					out = new FileWriter(getJavaPath(vm));
					Velocity.evaluate(dbContext, out, "DBContext", in);
	        	}
	        	finally {
	        		if ( in != null ) 
	        			in.close();
	        		if ( out != null ) 
	        			out.close();
	        	}
			}
	        
			
		} catch ( SQLException e ) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch ( Throwable e ) {
			e.printStackTrace();
		} 
		
	}
    
	@Override
	public boolean accept(File dir, String name) {
		return name.endsWith(".vm");
	}

	private File getJavaPath(File vm ) {
    	String vmName = vm.getName();
    	String javaName = vmName.substring(0, vmName.lastIndexOf('.'));
    	return new File ( javaOut, javaName );
    }
    
    private void dropDataBase(Connection connection, String dbName) 
    throws SQLException {
    	Statement stmt  = null;
    	try  {
	    	stmt = connection.createStatement();
	    	stmt.execute("DROP DATABASE `" + dbName + "`");
    	}catch (SQLException e) {
		}
    	finally{
    		if ( stmt != null )
    			stmt.close();
    	}
    }
    
    public static void main(String[] args) throws MojoExecutionException, MojoFailureException{
		DBMojo dbMojo = new DBMojo();
		dbMojo.execute();
    }


}
