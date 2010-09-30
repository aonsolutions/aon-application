package com.esferalia.aon.payroll.ctsql2mysql;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;

/********************************************************************
* Copyright (c) 2010, esferalia NETWORKS S.A
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

public class CtsqlDB extends AbstractCtsqlDB{
	
	
	public CtsqlDB( Connection ctsqlConnection) {
		super(ctsqlConnection);
	}
	
	
	
	public static void main(String[] args) throws ClassNotFoundException, ParseErrorException, MethodInvocationException, ResourceNotFoundException, SQLException, IOException {
		
		// create the command line parser
    	CommandLineParser parser = new PosixParser();   
    	
    	// create the Options
    	Options options = new Options();

    	OptionBuilder.isRequired(false);
    	OptionBuilder.hasArg(false);
    	OptionBuilder.withDescription("imprime esta ayuda.");
    	Option helpOption = OptionBuilder.create( "help" );

    	
    	OptionBuilder.isRequired(false);
    	OptionBuilder.hasArg(true);
    	OptionBuilder.withArgName( "URL" );
    	OptionBuilder.withType(String.class);
    	OptionBuilder.withDescription(  "cadena de conexión." );
    	Option ctsqlURLOption = OptionBuilder.create( "url" );


    	OptionBuilder.isRequired(false);
    	OptionBuilder.hasArg(true);
    	OptionBuilder.withArgName( "name" );
    	OptionBuilder.withType(String.class);
    	OptionBuilder.withDescription(  "usuario para conectarse." );
    	Option ctsqlUserOption = OptionBuilder.create( "user" );

    	OptionBuilder.isRequired(false);
    	OptionBuilder.hasArg(true);
    	OptionBuilder.withArgName( "name" );
    	OptionBuilder.withType(String.class);
    	OptionBuilder.withDescription(  "clave para conectarse." );
    	Option ctsqlPasswdOption = OptionBuilder.create( "passwd" );
    	

    	options.addOption(helpOption);
    	options.addOption(ctsqlURLOption);
    	options.addOption(ctsqlUserOption);
    	options.addOption(ctsqlPasswdOption);
    	
    	
    	HelpFormatter helpFormatter = new HelpFormatter();
    	
    	try {
    		// first of all load JDBC driver
            Class.forName("com.transtools.jdbc.CtsqlJdbcDriver");

            // parse the command line arguments
            CommandLine line = parser.parse( options, args );
            
            if ( line.hasOption(helpOption.getOpt()) )
            	helpFormatter.printHelp(HelpFormatter.DEFAULT_SYNTAX_PREFIX, options, true);
            
            String url = line.getOptionValue(ctsqlURLOption.getOpt(), 
            		"jdbc:ctsql://192.168.2.100:1101/empre055;DBPATH=/usr/share/ctsql/data;RTRIMCHAR=true");
            String user = line.getOptionValue(ctsqlUserOption.getOpt(), "ctl");
            String passwd = line.getOptionValue(ctsqlPasswdOption.getOpt(), "ctl");
            
            String ctsqlDBArgs [] = {
            		"-url", url,
            		"-user", user,
            		"-passwd", passwd,
            		"-out" , "src/main/java/com/esferalia/aon/payroll/ctsql2mysql/AbstractCtsqlDB.java", 
            		"-template" , "src/main/java/com/esferalia/aon/payroll/ctsql2mysql/templates/CtsqlDB.java.vm" 
            };
            

            DBContext.main(ctsqlDBArgs);

            String ctsqlDBVisitorArgs [] = {
            		"-url", url,
            		"-user", user,
            		"-passwd", passwd,
            		"-out" , "src/main/java/com/esferalia/aon/payroll/ctsql2mysql/CtsqlDBVisitor.java", 
            		"-template" , "src/main/java/com/esferalia/aon/payroll/ctsql2mysql/templates/CtsqlDBVisitor.java.vm" 
            };
    		
            DBContext.main(ctsqlDBVisitorArgs);

            String defCtsqlDBVisitorArgs [] = {
            		"-url", url,
            		"-user", user,
            		"-passwd", passwd,
            		"-out" , "src/main/java/com/esferalia/aon/payroll/ctsql2mysql/DefaultCtsqlDBVisitor.java", 
            		"-template" , "src/main/java/com/esferalia/aon/payroll/ctsql2mysql/templates/DefaultCtsqlDBVisitor.java.vm" 
            };

            DBContext.main(defCtsqlDBVisitorArgs);
            
//            String stateCtsqlDBVisitorArgs [] = {
//            		"-url", url,
//            		"-user", user,
//            		"-passwd", passwd,
//            		"-out" , "src/main/java/com/esferalia/aon/payroll/ctsql2mysql/StateCtsqlDBVisitor.java", 
//            		"-template" , "src/main/java/com/esferalia/aon/payroll/ctsql2mysql/templates/StateCtsqlDBVisitor.java.vm" 
//            };
//
//            DBContext.main(stateCtsqlDBVisitorArgs);
    	}
        catch( ParseException exp ) {
            // oops, something went wrong
            System.err.println( "Error : " + exp.getMessage() );
        	helpFormatter.printHelp(HelpFormatter.DEFAULT_SYNTAX_PREFIX, options, true);
        } 

	}
	
	
}
