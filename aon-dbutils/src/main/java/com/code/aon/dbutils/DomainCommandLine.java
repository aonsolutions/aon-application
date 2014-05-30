package com.code.aon.dbutils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.ColumnListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DomainCommandLine {

	private static final String DRIVER_CLASS_ARGUMENT = "driverClass";

	private static final String FILE_ARGUMENT = "file";

	private static final String DOMAIN_ARGUMENT = "domain";

	private static final String PASSWORD_ARGUMENT = "password";

	private static final String USER_ARGUMENT = "user";

	private static final String URL_ARGUMENT = "url";

	private final static Logger LOGGER = LoggerFactory.getLogger(DomainCommandLine.class);
	
	private Options options;
	
	private CommandLine line;
	
	public DomainCommandLine( boolean addFileArgument ) {
		options = new Options();
		
		Option userOption = OptionBuilder.withDescription( "user of database connection" )
				.withArgName( "login" ).hasArg().create(USER_ARGUMENT);
		userOption.setRequired(true);		
		options.addOption(userOption);

		Option passwordOption = OptionBuilder.withDescription( "password of database connection" )
				.withArgName( PASSWORD_ARGUMENT ).hasArg().create(PASSWORD_ARGUMENT);
		passwordOption.setRequired(true);
		options.addOption(passwordOption);

		Option urlOption = OptionBuilder.withDescription( "url, example: jdbc:mysql://localhost:3306/pro-aonsolutions-net" )
				.withArgName( "jdbcUrl" ).hasArg().create(URL_ARGUMENT);
		urlOption.setRequired(true);
		options.addOption(urlOption);

		Option domainOption = OptionBuilder.withDescription( "domain name to backup" )
				.withArgName( "domainName" ).hasArg().create(DOMAIN_ARGUMENT);
		options.addOption(domainOption);

		Option driverClassOption = OptionBuilder.withDescription( "jdbc driver class" )
				.withArgName( "jdbcDriver" ).hasArg().create(DRIVER_CLASS_ARGUMENT);
		options.addOption(driverClassOption);

		if ( addFileArgument ) {
			Option fileOption = OptionBuilder.withDescription( "output sql file in ISO-8859-1" )
					.withArgName( "sqlFile" ).hasArg().create(FILE_ARGUMENT);
			fileOption.setRequired(true);
			options.addOption(fileOption);					
		}
	}

	public void parse( String className, String[] arguments ) {
		BasicParser parser = new BasicParser();
		
		try {
			line = parser.parse(options, arguments);
		} catch (ParseException e) {
			LOGGER.error( "Parsing failed. Reason: " + e.getMessage() );
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp( className, options, true );
			System.exit(-1);
		}	

		String driver = "com.mysql.jdbc.Driver";
		if ( line.hasOption(DRIVER_CLASS_ARGUMENT) ) {
			driver = line.getOptionValue(DRIVER_CLASS_ARGUMENT);
		}
		if (! DbUtils.loadDriver(driver) ) {
			LOGGER.error( "Error loading driver: {}", driver );
			System.exit(-1);
		}		
	}
	
	private String getUrl() {
		return line.getOptionValue(URL_ARGUMENT);
	}
	
	private String getUser() {
		return line.getOptionValue(USER_ARGUMENT);
	}

	private String getPassword() {
		return line.getOptionValue(PASSWORD_ARGUMENT);
	}

	private boolean hasDomain() {
		return line.hasOption(DOMAIN_ARGUMENT);
	}
	
	private String getDomain() {
		return line.getOptionValue(DOMAIN_ARGUMENT);
	}

	public String getFile() {
		return line.getOptionValue(FILE_ARGUMENT);
	}
	
	public Connection getConnection() throws SQLException {
		return DriverManager.getConnection(getUrl(), getUser(), getPassword());
	}

	private Integer getDomainId( Connection connection, String domainName ) {
		QueryRunner run = new QueryRunner();
		try {
			ResultSetHandler<Integer> h = new ScalarHandler<Integer>();
			return run.query( connection, "SELECT id FROM domain WHERE name =?", h, domainName); 
		} catch (Throwable e) {
			LOGGER.error(e.getMessage(), e);
		}		
		return null;
	}
	
	private List<Integer> getDomainIds( Connection connection ) {
		QueryRunner run = new QueryRunner();
		try {
			ResultSetHandler<List<Integer>> h = new ColumnListHandler<Integer>();
			return run.query( connection, "SELECT id FROM domain", h );
		} catch (Throwable e) {
			LOGGER.error(e.getMessage(), e);
		}		
		return null;	
	}	
	
	public Integer[] getDomains( Connection connection ) {
		Integer[] domains = null;
		if ( hasDomain() ) {
			String domainName = getDomain();
			Integer domainId = getDomainId(connection, domainName);
			if ( domainId == null ) {
				LOGGER.error("Domain {} not found", domainName);
			} else {
				domains = new Integer[]{domainId};	
			}
		} else {
			List<Integer> list = getDomainIds(connection);
			if (! list.isEmpty() ) {
				domains = list.toArray(new Integer[list.size()]);
			}
		}		
		return domains;
	}
	
}