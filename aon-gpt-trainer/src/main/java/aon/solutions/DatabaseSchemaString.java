package aon.solutions;

import java.io.LineNumberReader;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;
import java.util.TimeZone;
import java.util.stream.Collectors;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class DatabaseSchemaString {
    
    // [ 
    // {"table_name": salary, "column_names": [ "id", "domain", "employee_name" ...]} 
    // ...
    // ]
    public static void main(String[] args) throws SQLException {

	Option hostOption = Option.builder("h").hasArg().required().longOpt("host").argName("name")
		.desc("Connect to host.").build();

	Option portOption = Option.builder("P").hasArg().longOpt("port").argName("name")
		.desc("Port number to use for connection, default (3306).").build();

	Option userOption = Option.builder("u").hasArg().required().longOpt("user").argName("name")
		.desc("User for login if not current user.").build();

	Option passwordOption = Option.builder("p").hasArg().required().longOpt("password").argName("name")
		.desc("Password to use when connecting to server.").build();

	Option databaseOption = Option.builder("d").hasArg().required().longOpt("database").argName("name")
		.desc("Database to use").build();

	Option tableOption = Option.builder("t").hasArgs().required().longOpt("table").argName("pattern")
		.desc("A table name pattern; must match the table name as it is stored in the database").build();

	Option helpOption = Option.builder("?").longOpt("help").desc("Display this help and exit.").build();

	Options options = new Options();
	options.addOption(helpOption);
	options.addOption(hostOption);
	options.addOption(portOption);
	options.addOption(userOption);
	options.addOption(passwordOption);
	options.addOption(databaseOption);
	options.addOption(tableOption);

	CommandLine commandLine = null;

	try {
	    CommandLineParser parser = new DefaultParser();
	    commandLine = parser.parse(options, args);
	} catch (ParseException e) {
	    // oops, something went wrong
	    System.out.println("Error: " + e.getLocalizedMessage());
	    new HelpFormatter().printHelp(DatabaseSchemaString.class.getSimpleName(), options);
	    return;
	}

	String dbHost = commandLine.getOptionValue(hostOption.getLongOpt());
	String pdbPrt = commandLine.getOptionValue(portOption.getLongOpt(), "3306");
	String dbUser = commandLine.getOptionValue(userOption.getLongOpt());
	String dbPassword = commandLine.getOptionValue(passwordOption.getLongOpt());
	String dbName = commandLine.getOptionValue(databaseOption.getLongOpt());
	String[] tableNamePattern = commandLine.getOptionValues(tableOption.getLongOpt());

	Properties properties = new Properties();
	properties.setProperty("user", dbUser);
	properties.setProperty("password", dbPassword);
	properties.setProperty("useSSL", "false");
	properties.setProperty("serverTimezone", TimeZone.getDefault().getID());
	String url = String.format("jdbc:mysql://%s:%s/%s", dbHost, pdbPrt, dbName);

	    try (Connection connection = DriverManager.getConnection(url, properties)) {

	    //SQL query extracting info to answer the user's question. Given the following SQL tables, your job is to write queries given a user’s request.
	    //The query should be returned in plain text, not in JSON . Then look at the results of the query and return the answer."
	    	
	    System.out.print("SQL query extracting info to answer the user's question. Given the following SQL tables, your job is to write queries given a user’s request.");	
	  
		Statement st = connection.createStatement();
		for(String tableNamePatt : tableNamePattern) {
		ResultSet rs = st.executeQuery("SHOW CREATE TABLE" + " " +  tableNamePatt);
		if(rs.next()) {
			
			
			 
			try ( StringReader reader = new StringReader(rs.getString(2))) {
				LineNumberReader lineReader = new LineNumberReader(reader);
					lineReader.lines().forEach(System.out::print);
			}
			
			System.out.print(";"); 
		}
		rs.close();
		}
		st.close();

	}	
	    System.out.print("The query should be returned in plain text, not in JSON . Then look at the results of the query and return the answer.");	
	    
    }
    
    
    private static Collection<String> getColumnNames(Connection connection, String dbName, String tableName, String columnNamePattern) throws SQLException {
    	DatabaseMetaData metadata = connection.getMetaData();
    	try(ResultSet rs = metadata.getColumns(dbName, null, tableName, columnNamePattern )){
    		List<String> columnNames = new ArrayList<>();
    		while(rs.next()) {
    			columnNames.add(rs.getString("COLUMN_NAME"));
    		}
    		return columnNames;
    	}
       }


    private static Collection<String> getTableNames(Connection connection, String dbName, String tableNamePattern) throws SQLException {
	DatabaseMetaData metadata = connection.getMetaData();
	try ( ResultSet rs = metadata.getTables(dbName, null, tableNamePattern, new String [] {"TABLE"}) ) {
        	List<String> tableNames = new ArrayList<>(); 
        	while ( rs.next() ) {
        	    tableNames.add(rs.getString("TABLE_NAME"));
        	}
        	return tableNames;
	}
    }
    

}
