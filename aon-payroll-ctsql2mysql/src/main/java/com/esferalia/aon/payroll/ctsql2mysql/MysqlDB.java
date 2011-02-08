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
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;


import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

import com.code.aon.common.enumeration.Country;
import com.code.aon.company.enumeration.CCCType;
import com.code.aon.company.enumeration.EnterpriseActivityType;
import com.code.aon.customer.enumeration.CustomerStatus;
import com.code.aon.employee.enumeration.ContractStatus;
import com.code.aon.geozone.GeoZone;
import com.code.aon.person.enumeration.Gender;
import com.code.aon.person.enumeration.MaritalStatus;
import com.code.aon.registry.enumeration.AddressType;
import com.code.aon.registry.enumeration.DocumentType;
import com.esferalia.aon.salary.enumeration.DeductionType;
import com.esferalia.aon.salary.enumeration.PaymentType;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Cliente;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Convenio;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Delegacion;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Domicilio;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Empract;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Emprccc;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Emprdom;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Empresa;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Emprnif;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Emprper;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Nomdto;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Nomdtoex;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Nomina;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Nominadev;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Nominaex;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Pais;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Percep;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Persona;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Tipdoc;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Trabajo;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Trabdto;

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
		MyAgreement myAgreement = 
			new MyAgreement(this); 
		MyEnterprise myEnterprise = 
			new MyEnterprise(this, 
					myAgreement );
		MyContract myContract= 
			new MyContract(this, 
					myEnterprise, 
					myPerson,
					fromDate);
		MyHoliday myHoliday =
			new MyHoliday(this, 
						"Aplicación Total");
		MyCalendar myCalendar = 
			new MyCalendar(this, 
					myHoliday, 
					myEnterprise);
		
		ctsqlReader.visitPais(this);
		ctsqlReader.visitTipdoc(this);
		ctsqlReader.visit(myHoliday);
		
		ctsqlReader.visit(myPerson);
		ctsqlReader.visit(myAgreement);
		ctsqlReader.visit(myEnterprise);
		ctsqlReader.visit(myCalendar);
		ctsqlReader.visit(myContract);

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
            
            
    	}
        catch( ParseException exp ) {
            // oops, something went wrong
            System.err.println( "Error : " + exp.getMessage() );
        	helpFormatter.printHelp(HelpFormatter.DEFAULT_SYNTAX_PREFIX, options, true);
        } 

	}


}
