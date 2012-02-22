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
package com.esferalia.aon.payroll.ctsql2mysql;


import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

/**
 * 
 */
public class MysqlDB extends DefaultMysqlDB{
	
	
	// --------------------------------------------------------------
	private Date		fromDate = null;		
	
	
	

	// --------------------------------------------------------------
	
	public MysqlDB(Connection mysqlConnection) throws SQLException {
		super(mysqlConnection);
	}
	
	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public void writeAll(CtsqlDB ctsqlReader) throws SQLException {
		start();

		MyPerson myPerson = 
			new MyPerson(this);
		MyConcept myConcept = 
			new MyConcept(this);
		MyAgreement myAgreement = 
			new MyAgreement(this, 
					myConcept, fromDate); 
		MyHoliday myHoliday =
			new MyHoliday(this, 
						"Aplicación Total");
		MyCalendar myCalendar = 
			new MyCalendar(this, 
					myHoliday);
		MyEnterprise myEnterprise = 
			new MyEnterprise(this, 
					myAgreement,
					myCalendar);
		MyContract myContract= 
			new MyContract(this, 
					myEnterprise, 
					myPerson,
					myConcept,
					myAgreement,
					myCalendar,
					fromDate);
		MyFsProfRetention myFsProfRetention = 
			new MyFsProfRetention(this, 
					myEnterprise, 
					fromDate);
		
		ctsqlReader.visitPais(this);
		ctsqlReader.visitTipdoc(this);
		ctsqlReader.visit(myHoliday);
		
		ctsqlReader.visit(myPerson);
		ctsqlReader.visit(myConcept);
		ctsqlReader.visit(myAgreement);
		ctsqlReader.visit(myCalendar);
		ctsqlReader.visit(myEnterprise);
		ctsqlReader.visit(myContract);
		ctsqlReader.visit(myFsProfRetention);
		
		finish();
		//ctsqlReader.visit(new MyTraverse());
	}
	
	
	
	public static void main(String[] args) throws ClassNotFoundException, SQLException, IOException {
		
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
    	OptionBuilder.withDescription(  "cadena de conexiÃ³n." );
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
    		
    		// first of all load JDBC drivers
            Class.forName("org.gjt.mm.mysql.Driver");

            // parse the command line arguments
            CommandLine line = parser.parse( options, args );
            
            if ( line.hasOption(helpOption.getOpt()) )
            	helpFormatter.printHelp(HelpFormatter.DEFAULT_SYNTAX_PREFIX, options, true);
            
            String url = line.getOptionValue(ctsqlURLOption.getOpt(), 
            		"jdbc:mysql://127.0.0.1:3306/payroll-esferalia-org");
            String user = line.getOptionValue(ctsqlUserOption.getOpt(), "dbuser");
            String passwd = line.getOptionValue(ctsqlPasswdOption.getOpt(), "serubd2000");
            
            String mysqlDBArgs [] = {
            		"-url", url,
            		"-user", user,
            		"-passwd", passwd,
            		"-out" , "src/main/java/com/esferalia/aon/payroll/ctsql2mysql/AbstractMysqlDB.java" ,
            		"-template" , "src/main/java/com/esferalia/aon/payroll/ctsql2mysql/templates/MysqlDB.java.vm" 
            };
            DBContext.main(mysqlDBArgs);
            
            String mysqlDomainDBArgs [] = {
            		"-url", url,
            		"-user", user,
            		"-passwd", passwd,
            		"-out" , "src/main/java/com/esferalia/aon/payroll/ctsql2mysql/AbstractDomainMysqlDB.java" ,
            		"-template" , "src/main/java/com/esferalia/aon/payroll/ctsql2mysql/templates/MysqlDomainDB.java.vm" 
            };
            DBContext.main(mysqlDomainDBArgs);
            
    	}
        catch( ParseException exp ) {
            // oops, something went wrong
            System.err.println( "Error : " + exp.getMessage() );
        	helpFormatter.printHelp(HelpFormatter.DEFAULT_SYNTAX_PREFIX, options, true);
        } 

	}


}
