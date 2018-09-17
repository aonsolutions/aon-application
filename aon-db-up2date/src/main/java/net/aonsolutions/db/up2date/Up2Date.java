package net.aonsolutions.db.up2date;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import net.aonsolutions.db.up2date.payroll.FundUpvUpdateII;

public class Up2Date {
	
	

    private static Update [] UPDATES  = {
    		//IRPF2018UPDATE,
    		//AGREEMENTUPDATE,
    		//BASES2018UPDATE,
    		//ISSUE1760UPDATE,
    		//REMOVE2HIDEUPDATE,
    		//HOMEBASESUPDATE,
    		//TRAININGBASES2018UPDATE,
    		//HOMEPERCENTAGEUPDATE
    		//FELLOWSBASES2018UPDATE,
    		//SYSTEMPAYMENTREADONLYUPDATE
    		//BASES2018UPDATEII
    		//PRESTITCOMMONDISEASEATLACKINSERT,
    		//GEROA_INSERT
    		//UNEMPLOYMENT
    		//TRAININGBASES2018FIX,
    		//FELLOWBASES2018FIX,
    		FundUpvUpdateII.FUNDUPV_UPDATE_II
    		
    }; 
	
	
	// ------------------------------------------------------------------------
	
	


    @SuppressWarnings("static-access")
    public static void main(String[] args) {
    	 
    	
		 Option hostOption = OptionBuilder
		     .hasArg()
			 .isRequired()
		     .withLongOpt("host")
		     .withArgName("name")
		     .withDescription("Connect to host.")
		     .create("h");
		 Option portOption = OptionBuilder
		     .hasArg()
		     .withLongOpt("port")
		     .withArgName("name")
		     .withDescription("Port number to use for connection, default (3306).")
		     .create("P");
		 Option userOption = OptionBuilder
		     .hasArg()
			 .isRequired()
		     .withLongOpt("user")
		     .withArgName("name")
		     .withDescription("User for login if not current user.")
		     .create("u");
		 Option passwordOption = OptionBuilder
		     .hasArg()
			 .isRequired()
		     .withLongOpt("password")
		     .withArgName("name")
		     .withDescription("Password to use when connecting to server.")
		     .create("p");
		 Option helpOption = OptionBuilder
				 .withLongOpt("help")
		         .withDescription("Display this help and exit.")
		         .create("?");
		 
		 Options options = new Options();
		 options.addOption(helpOption);
		 options.addOption(hostOption);
		 options.addOption(portOption);
		 options.addOption(userOption);
		 options.addOption(passwordOption);
		 
		 Statement statement = null;
		 ResultSet databasesRs = null;
		 Connection connection = null;

		 CommandLineParser parser = new GnuParser();
		 try {
			Class.forName("com.mysql.jdbc.Driver");
			CommandLine commandLine = parser.parse(options, args);
			
			String host = commandLine.getOptionValue(hostOption.getLongOpt());
			String port = commandLine.getOptionValue(portOption.getLongOpt(), "3306");
			String user = commandLine.getOptionValue(userOption.getLongOpt());
			String password = commandLine.getOptionValue(passwordOption.getLongOpt());
			
			String url = String.format("jdbc:mysql://%s:%s/information_schema", host, port);
			
			connection = DriverManager.getConnection(url, user, password);
			
			statement = connection.createStatement();
			databasesRs = statement.executeQuery("SELECT `TABLE_SCHEMA` FROM `TABLES` WHERE `TABLE_NAME`='registry'");
			List<String> databases = new ArrayList<String>();
			while ( databasesRs.next() )
				databases.add(databasesRs.getString(1));
			
			for ( String database : databases ) {
				
				System.out.print(String.format("Updating database  `%s`" ,database  ));
				
				statement.executeQuery(String.format("USE `%s`", database));
				
				for ( Update update : UPDATES ) {
					try {
						update.upgrade(connection);
						System.out.println("Success." );
					} catch ( Throwable t) {
						System.out.println("Error: " + t.getLocalizedMessage());
					}
				}

				
			}
			
		} catch (ParseException e) {
			// oops, somthing went wrong
			System.out.println("Error: " + e.getLocalizedMessage());
			new HelpFormatter().printHelp(Up2Date.class.getSimpleName(), options);
		} catch (SQLException e) { 
			System.out.println("Error: " + e.getLocalizedMessage());
		} catch (ClassNotFoundException e) {
			System.out.println("Error: " + e.getLocalizedMessage());
		} finally {
			try {
				if ( databasesRs != null )
					databasesRs.close();
				if ( statement != null )
					statement.close();
				if ( connection != null )
					connection.close();
			} catch ( SQLException e ) {
				System.err.println("Oops, something went wrong, " + e.getLocalizedMessage());
			}
		}
	     
	}

}
