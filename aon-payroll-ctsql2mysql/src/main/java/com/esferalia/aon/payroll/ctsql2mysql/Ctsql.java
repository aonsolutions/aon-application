package com.esferalia.aon.payroll.ctsql2mysql;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import java.util.TimeZone;
import java.util.Properties;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

import com.esferalia.aon.watson.util.AonStringUtils;

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

public class Ctsql extends AbstractCtsqlDB
{

	public Ctsql( Connection ctsqlConnection) {
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

    	OptionBuilder.isRequired(true);
    	OptionBuilder.hasArg(true);
    	OptionBuilder.withArgName( "query" );
    	OptionBuilder.withType(String.class);
    	OptionBuilder.withDescription(  "select para ejecutarse." );
    	Option queryOption = OptionBuilder.create( "query" );

    	options.addOption(helpOption);
    	options.addOption(ctsqlURLOption);
    	options.addOption(ctsqlUserOption);
    	options.addOption(ctsqlPasswdOption);
    	options.addOption(queryOption);


    	HelpFormatter helpFormatter = new HelpFormatter();

    	try {
    		// first of all load JDBC driver
            Class.forName("com.transtools.jdbc.CtsqlJdbcDriver");

            // parse the command line arguments
            CommandLine line = parser.parse( options, args );

            if ( line.hasOption(helpOption.getOpt()) )
            	helpFormatter.printHelp(HelpFormatter.DEFAULT_SYNTAX_PREFIX, options, true);

            String url = line.getOptionValue(ctsqlURLOption.getOpt(),
            		"jdbc:ctsql://127.0.0.1:1101/empre600;DBPATH=/home/ctl/data;RTRIMCHAR=true");
//            String url = line.getOptionValue(ctsqlURLOption.getOpt(),
//            		"jdbc:ctsql://194.30.98.127:1101/empre800;DBPATH=/usr/share/ctsql/data;RTRIMCHAR=true");
            String user = line.getOptionValue(ctsqlUserOption.getOpt(), "ctl");
            String passwd = line.getOptionValue(ctsqlPasswdOption.getOpt(), "ctl");

            Connection connection =  DriverManager.getConnection(url, user, passwd);
            System.out.println( "Success :  DefaultCtsqlDBVisitor.java "  );

            String query = line.getOptionValue(queryOption.getOpt(), "query");

            ResultSet rs =  connection.createStatement().executeQuery(query);

            ResultSetMetaData rsmd = rs.getMetaData();
            int columnsNumber = rsmd.getColumnCount();
            int lengths [] = new int [columnsNumber+1];
            for (int i = 1; i <= columnsNumber; i++) {
                if (i > 1) System.out.print(" | ");
                String name = rsmd.getColumnName(i);
                System.out.print(name);
                lengths[i] = Math.max(rsmd.getColumnDisplaySize(i), name.length());
                System.out.print(AonStringUtils.repeat(' ', lengths[i] - name.length() ));
            }
            System.out.println();
            while (rs.next()) {
                for (int i = 1; i <= columnsNumber; i++) {
                    if (i > 1) System.out.print(" | ");
                    String str = rs.getString(i);
                    if ( str == null )
                    	str = "";
                    System.out.print(str);
                    System.out.print(AonStringUtils.repeat(' ', lengths[i] - str.length() ));
                }
                System.out.println("");
            }
    	}
        catch( ParseException exp ) {
            // oops, something went wrong
            System.err.println( "Error : " + exp.getMessage() );
        	helpFormatter.printHelp(HelpFormatter.DEFAULT_SYNTAX_PREFIX, options, true);
        }

	}


}
