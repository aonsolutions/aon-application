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
	
	{
			// first of all load JDBC drivers
	    try {
			Class.forName("org.gjt.mm.mysql.Driver");
		    Class.forName("com.transtools.jdbc.CtsqlJdbcDriver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	private String ctsqlURL;
	private String ctsqlUser;
	private String ctsqlPasswd;
	private String mysqlURL;
	private String mysqlUser;
	private String mysqlPasswd;
	private boolean dryRun;
	
	private Date fromDate;
	

	public Ctsql2Mysql(String args [] ) {
		parseArgs(args);
	}
	
	protected String getCtsqlURL() {
		return ctsqlURL;
	}

	protected boolean parseArgs(String[] args ) {
    	
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
    	OptionBuilder.withDescription(  "trapasar los datos a partir de esta fecha M/d/Y" );
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
    	
    	CommandLineParser parser = new PosixParser();   

    	HelpFormatter helpFormatter = new HelpFormatter();

		try {
			 CommandLine line= parser.parse( options, args );

			if ( line.hasOption(helpOption.getOpt()) ) {
	        	helpFormatter.printHelp(HelpFormatter.DEFAULT_SYNTAX_PREFIX, options, true);
	        	return false;
	        }

            ctsqlURL = line.getOptionValue(ctsqlURLOption.getOpt());
            ctsqlUser = line.getOptionValue(ctsqlUserOption.getOpt(), "ctl");
            ctsqlPasswd = line.getOptionValue(ctsqlPasswdOption.getOpt(), "ctl");
            
            mysqlURL = line.getOptionValue(mysqlURLOption.getOpt());
            mysqlUser = line.getOptionValue(mysqlUserOption.getOpt(),"dbuser");
            mysqlPasswd = line.getOptionValue(mysqlPasswdOption.getOpt(),"serubd2000");
            
            dryRun=  line.hasOption(dryRunOption.getOpt());
			
            String fromString = line.getOptionValue(fromDateOption.getOpt());
            if ( fromString != null ) {
	        	fromDate = DateFormat.getDateInstance(DateFormat.SHORT).parse(fromString);
	        }

		} catch (Exception e) {
        	helpFormatter.printHelp(HelpFormatter.DEFAULT_SYNTAX_PREFIX, options, true);
		}

		return true;
	}
	
	protected Connection getCtsqlConnection() throws SQLException {
		return DriverManager.getConnection(ctsqlURL, ctsqlUser, ctsqlPasswd);
	}
	
	protected Connection getMysqlConnection() throws SQLException {
		return DriverManager.getConnection(mysqlURL, mysqlUser, mysqlPasswd);
	}

	protected void transfer() throws ClassNotFoundException, SQLException, java.text.ParseException {
		if ( ctsqlURL == null ){
			return;
		}

        Connection ctsqlConnection = null;
        Connection mysqlConnection = null;
        
        try {
	        ctsqlConnection = getCtsqlConnection();  
	        	
	        mysqlConnection =  getMysqlConnection();
	
	        mysqlConnection.setAutoCommit(false);
	        
	        MysqlDB mysqlWriter = new MysqlDB(mysqlConnection);
	        
	        mysqlWriter.setFromDate(fromDate);
	        
	        CtsqlDB ctsqlReader = new CtsqlDB(ctsqlConnection);
	        mysqlWriter.writeAll(ctsqlReader);
	        
	        if ( !dryRun ) {
	        	mysqlConnection.commit(); 
	        }
        }
        finally {
        	if ( ctsqlConnection != null )
        		ctsqlConnection.close();
	        if ( mysqlConnection != null )
	        	mysqlConnection.close();
        }
	}
	
	public static void main( String[] args ) throws SQLException, ClassNotFoundException, java.text.ParseException
    {
		new Ctsql2Mysql(args).transfer();
    }

	public boolean isDryRun() {
		return dryRun;
	}

	
}

