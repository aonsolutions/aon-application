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
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Properties;

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
 * @goal 	create-hibernate-configuration-file
 * @phase	initialize
 * @requiresDependencyResolution
 * @threadSafe
 *  
 */
public class HibernateConfigurationFileForReveng extends AbstractMojo {
	
	private static final Object LOCK = new Object();
	
    // ----------------------------------------------------------------------
    // Mojo parameters
    // ----------------------------------------------------------------------

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
     *  
     * @parameter 
     */
    private String destDir;

    /**
     *  
     * @parameter 
     */
    private List<String> properties;

    
    @Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		synchronized( LOCK) {
	        try {
	        	File file = new File(this.destDir);
	        	file.getParentFile().mkdirs();
	        	PrintWriter writer =  new PrintWriter(file);
	        	for (String property : properties) {
	        		writer.println(property);	
	        	}
	        	writer.flush();
	        	writer.close();
	        } catch (IOException e) {
		    	getLog().error(e.getMessage());
				throw new MojoExecutionException( e.getMessage(), e );
			}
		}
	}
}
