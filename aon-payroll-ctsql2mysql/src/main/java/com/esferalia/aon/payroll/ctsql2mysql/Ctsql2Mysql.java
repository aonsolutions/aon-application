package com.esferalia.aon.payroll.ctsql2mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

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


/**
 * Ctsql2Mysql
 *
 */
public class Ctsql2Mysql 
{
	

	public static void main( String[] args ) throws SQLException, ClassNotFoundException, java.text.ParseException
    {
		
		// create the command line parser
    	CommandLineParser parser = new PosixParser();   
    	
    	// create the Options
    	Options options = new Options();

    	OptionBuilder.isRequired(false);
    	OptionBuilder.hasArg(false);
    	OptionBuilder.withDescription("imprime esta ayuda.");
    	Option helpOption = OptionBuilder.create( "help" );

    	OptionBuilder.isRequired(false);
    	OptionBuilder.hasArg(false);
    	OptionBuilder.withDescription("No inserta nada en la base de datos. Para chequear que las modificaciones, las operaciones sql (por pantalla) funcionan como se esparaba.");
    	Option dryRunOption = OptionBuilder.create( "dryrun" );
    	
    	OptionBuilder.isRequired(true);
    	OptionBuilder.hasArg(true);
    	OptionBuilder.withArgName( "URL" );
    	OptionBuilder.withType(String.class);
    	OptionBuilder.withDescription(  "cadena de conxión ctsql." );
    	Option ctsqlURLOption = OptionBuilder.create( "ctsqlurl" );

    	OptionBuilder.isRequired(true);
    	OptionBuilder.hasArg(true);
    	OptionBuilder.withArgName( "URL" );
    	OptionBuilder.withType(String.class);
    	OptionBuilder.withDescription(  "cadena de conxión mysql" );
    	Option mysqlURLOption = OptionBuilder.create( "mysqlurl" );

    	OptionBuilder.isRequired(false);
    	OptionBuilder.hasArg(true);
    	OptionBuilder.withArgName( "name" );
    	OptionBuilder.withType(String.class);
    	OptionBuilder.withDescription(  "usuario para conectarse a ctsql, si no es 'ctl'" );
    	Option ctsqlUserOption = OptionBuilder.create( "ctsqluser" );

    	OptionBuilder.isRequired(false);
    	OptionBuilder.hasArg(true);
    	OptionBuilder.withArgName( "name" );
    	OptionBuilder.withType(String.class);
    	OptionBuilder.withDescription(  "clave para conectarse a ctsql." );
    	Option ctsqlPasswdOption = OptionBuilder.create( "ctsqlpasswd" );
    	
    	OptionBuilder.isRequired(false);
    	OptionBuilder.hasArg(true);
    	OptionBuilder.withArgName( "name" );
    	OptionBuilder.withType(String.class);
    	OptionBuilder.withDescription(  "usuario para conectarse a mysql, si no es 'dbuser'" );
    	Option mysqlUserOption = OptionBuilder.create( "mysqluser" );

    	OptionBuilder.isRequired(false);
    	OptionBuilder.hasArg(true);
    	OptionBuilder.withArgName( "name" );
    	OptionBuilder.withType(String.class);
    	OptionBuilder.withDescription(  "clave para conectarse a mysql." );
    	Option mysqlPasswdOption = OptionBuilder.create( "mysqlpasswd" );
    	
    	OptionBuilder.isRequired(false);
    	OptionBuilder.hasArg(true);
    	OptionBuilder.withArgName( "date" );
    	OptionBuilder.withType(String.class);
    	OptionBuilder.withDescription(  "trapasar los datos a partir de esta fecha" );
    	Option fromDateOption = OptionBuilder.create( "from" );

    	options.addOption(helpOption);
    	options.addOption(dryRunOption);
    	options.addOption(ctsqlURLOption);
    	options.addOption(mysqlURLOption);
    	options.addOption(ctsqlUserOption);
    	options.addOption(mysqlUserOption);
    	options.addOption(ctsqlPasswdOption);
    	options.addOption(mysqlPasswdOption);
    	options.addOption(fromDateOption);
    	
    	
    	HelpFormatter helpFormatter = new HelpFormatter();
    	

    	try {
    		// first of all load JDBC drivers
            Class.forName("org.gjt.mm.mysql.Driver");
            Class.forName("com.transtools.jdbc.CtsqlJdbcDriver");

            // parse the command line arguments
            CommandLine line = parser.parse( options, args );
            
            if ( line.hasOption(helpOption.getOpt()) )
            	helpFormatter.printHelp(HelpFormatter.DEFAULT_SYNTAX_PREFIX, options, true);
            
            String ctsqlURL = line.getOptionValue(ctsqlURLOption.getOpt());
            String ctsqlUser = line.getOptionValue(ctsqlUserOption.getOpt(), "ctl");
            String ctsqlPasswd = line.getOptionValue(ctsqlPasswdOption.getOpt(), "ctl");
            Connection ctsqlConnection =  
            	DriverManager.getConnection(ctsqlURL, ctsqlUser, ctsqlPasswd);
            
            String mysqlURL = line.getOptionValue(mysqlURLOption.getOpt());
            String mysqlUser = line.getOptionValue(mysqlUserOption.getOpt(), "dbuser");
            String mysqlPasswd = line.getOptionValue(mysqlPasswdOption.getOpt(),"serubd2000");
            Connection mysqlConnection =  
            	DriverManager.getConnection(mysqlURL, mysqlUser, mysqlPasswd);
            
            boolean dryRun=  line.hasOption(dryRunOption.getOpt());
            
            
            mysqlConnection.setAutoCommit(false);
            
            MysqlDB mysqlWriter = new MysqlDB(mysqlConnection);
          
            String fromString = line.getOptionValue(fromDateOption.getOpt());
            if ( fromString != null ) {
            	
            	Date fromDate = DateFormat.getDateInstance(DateFormat.SHORT).parse(fromString);
            	mysqlWriter.setFromDate(fromDate);
            }
            
            CtsqlDB ctsqlReader = new CtsqlDB(ctsqlConnection);
            mysqlWriter.writeAll(ctsqlReader);
            
            if ( !dryRun ) {
            	mysqlConnection.commit(); 
            }
            
            mysqlConnection.close();
            ctsqlConnection.close();
    	
    	}
        catch( ParseException exp ) {
            // oops, something went wrong
            System.err.println( "Error : " + exp.getMessage() );
        	helpFormatter.printHelp(HelpFormatter.DEFAULT_SYNTAX_PREFIX, options, true);
        } 
    }
	

}

