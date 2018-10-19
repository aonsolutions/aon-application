package com.esferalia.aon.payroll.ctsql2mysql;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;


import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

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

public class CtsqlDB extends AbstractCtsqlDB
{
	
	public CtsqlDB( Connection ctsqlConnection) {
		super(ctsqlConnection);
	}
	
	
	
	public static void main(String[] args) throws ClassNotFoundException, SQLException , IOException {
		
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
            		"jdbc:ctsql://127.0.0.1:1101/empre055;DBPATH=/home/ctl/data;RTRIMCHAR=true");
//            String url = line.getOptionValue(ctsqlURLOption.getOpt(), 
//            		"jdbc:ctsql://194.30.98.127:1101/empre800;DBPATH=/usr/share/ctsql/data;RTRIMCHAR=true");
            String user = line.getOptionValue(ctsqlUserOption.getOpt(), "ctl");
            String passwd = line.getOptionValue(ctsqlPasswdOption.getOpt(), "ctl");
            
            Connection connection =  DriverManager.getConnection(url, user, passwd);
    		System.out.println( "Success :  Connection "  );
    		DatabaseMetaData dbMetaData = connection.getMetaData(); 
    		System.out.println( "Success :  DatabaseMetaData "  );
    		DBContext dbContext = new DBContext(dbMetaData);

    		System.out.println( "Success :  DBContext "  );
    		
            Writer out = new FileWriter("src/main/java/com/esferalia/aon/payroll/ctsql2mysql/AbstractCtsqlDB.java");
            Reader in =  new FileReader("src/main/java/com/esferalia/aon/payroll/ctsql2mysql/templates/CtsqlDB.java.vm");
    		DBContext.evaluate(dbContext, out, "DBContext", in);
    		in.close();
    		out.close();
    		
    		System.out.println( "Success :  AbstractCtsqlDB.java "  );

            out = new FileWriter("src/main/java/com/esferalia/aon/payroll/ctsql2mysql/CtsqlDBVisitor.java");
            in =  new FileReader("src/main/java/com/esferalia/aon/payroll/ctsql2mysql/templates/CtsqlDBVisitor.java.vm");
            DBContext.evaluate(dbContext, out, "DBContext", in);
    		in.close();
    		out.close();

    		System.out.println( "Success :  CtsqlDBVisitor.java "  );

            out = new FileWriter("src/main/java/com/esferalia/aon/payroll/ctsql2mysql/DefaultCtsqlDBVisitor.java");
            in =  new FileReader("src/main/java/com/esferalia/aon/payroll/ctsql2mysql/templates/DefaultCtsqlDBVisitor.java.vm");
            DBContext.evaluate(dbContext, out, "DBContext", in);
    		in.close();
    		out.close();
            
    		System.out.println( "Success :  DefaultCtsqlDBVisitor.java "  );
    	}
        catch( ParseException exp ) {
            // oops, something went wrong
            System.err.println( "Error : " + exp.getMessage() );
        	helpFormatter.printHelp(HelpFormatter.DEFAULT_SYNTAX_PREFIX, options, true);
        } 

	}
	
	
}
