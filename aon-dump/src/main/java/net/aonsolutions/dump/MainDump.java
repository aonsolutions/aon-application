package net.aonsolutions.dump;

import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Optional;
import java.util.Properties;
import java.util.TimeZone;
import java.util.stream.Stream;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.Record3;
import org.jooq.Result;
import org.jooq.SQLDialect;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.conf.ParamType;
import org.jooq.conf.RenderKeywordCase;
import org.jooq.conf.RenderQuotedNames;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

public class MainDump {
	
	public static void main(String[] args) throws SQLException, SecurityException, IOException {
		
		Option hostNameOpt = Option.builder()
				.hasArg()
				.argName("name")
				.required()
				.longOpt("host")
				.desc("Host name which we want to connect to.")
				.build();

		Option portOpt = Option.builder()
				.hasArg()
				.argName("name")
				.longOpt("port")
				.desc("Port to connect to host.")
				.type(Integer.class)
				.build();

		Option dataBaseOpt = Option.builder()
				.hasArg()
				.argName("name")
				.required()
				.longOpt("database")
				.desc("DataBase we are going to use.")
				.build();

		Option userOpt = Option.builder()
				.hasArg()
				.argName("name")
				.required()
				.longOpt("user")
				.desc("User for connecting to server.")
				.build();

		Option passwordOpt = Option.builder()
				.hasArg()
				.argName("name")
				.required()
				.longOpt("password")
				.desc("Password for connecting to server.")
				.build();

		Option domainOpt = Option.builder()
				.hasArg()
//				.required()
				.argName("name")
				.longOpt("domain")
				.desc("Domain's name for dump.")
				.build();

		Option listDomains = Option.builder()
				.longOpt("list")
				.desc("List all the domains you have access.")
				.build();

		Option helpOpt = Option.builder()
				.longOpt("help")
				.desc("Shows help for entrys arguments.")
				.build();
		
		Options options = new Options()
				.addOption(hostNameOpt)
				.addOption(portOpt)
				.addOption(dataBaseOpt)
				.addOption(userOpt)
				.addOption(passwordOpt)
				.addOption(domainOpt)
				.addOption(listDomains)
				.addOption(helpOpt);

		// Parser create
		CommandLineParser parser = new DefaultParser();

		try {

			// Parse the command line arguments
			CommandLine cmd = parser.parse(options, args);
			
			// Get all the information we have gotten from the comand line
			String hostName = cmd.getOptionValue(hostNameOpt.getLongOpt());
			String user = cmd.getOptionValue(userOpt.getLongOpt());
			String password = cmd.getOptionValue(passwordOpt.getLongOpt());
			String database = cmd.getOptionValue(dataBaseOpt.getLongOpt());
			String port = cmd.hasOption(portOpt.getLongOpt()) ? cmd.getOptionValue(portOpt.getLongOpt()) : "3306";
			String domain = cmd.getOptionValue(domainOpt.getLongOpt());

			String url = "jdbc:mysql://" + hostName + ":" + port + "/" + database;
			
			// DSLContext creation
			DSLContext dslContext = createDSLContext(user, password, url);

			if (cmd.hasOption(helpOpt.getLongOpt())) {
				// Automatically generate the help statement
				HelpFormatter formatter = new HelpFormatter();
				formatter.printHelp("aon-dump", options);
				return;
			}
			
			if (cmd.hasOption(listDomains.getLongOpt())){
				listDomains(dslContext, System.err);
				return;
			}
			
			dumpDomain(dslContext, database, domain);
			

		} catch (ParseException e) {

			// Oops, something went wrong
			System.err.println("Parsing failed.  Reason: " + e.getMessage());

			// Automatically generate the help statement
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("aon-dump", options);
		}
	}

	private static DSLContext createDSLContext(String user, String password, String url) throws SQLException {
		// Create a connection to our DataBase
		Properties properties = new Properties();
		properties.setProperty("user", user);
		properties.setProperty("password", password);
		properties.setProperty("serverTimezone", TimeZone.getDefault().getID());
		Connection connection = DriverManager.getConnection(url, properties);

		// Establish settings
		Settings settings = new Settings();
		settings.setRenderSchema(false);

		//settings.setRenderFormatted(true);
		settings.setRenderQuotedNames(RenderQuotedNames.EXPLICIT_DEFAULT_QUOTED);
		settings.setRenderKeywordCase(RenderKeywordCase.UPPER);

		settings.setParamType(ParamType.INLINED);

		// Establish context
		return DSL.using(connection, SQLDialect.MARIADB, settings);
	}

	private static void listDomains(DSLContext dslContext, PrintStream err) {
		Result<Record3<Integer, String, String>> domainsResult = dslContext
				.select(DOMAIN.ID, DOMAIN.NAME, DOMAIN.DESCRIPTION).from(DOMAIN).fetch();
		
		domainsResult.forEach(d -> err.println(d.getValue(DOMAIN.NAME)));
		
	}
	
	private static void dumpDomain(DSLContext dslContext, String database, String domainName) throws IOException {
		Optional<Schema> schema = dslContext.meta().getSchemas().stream()
				.filter(s -> s.getName().equals(database)).findFirst();
		
		if(schema.isPresent()) {
			System.out.println("PRESENT -> database : " + database + ", domainName : " + domainName);
			
			// Table with domain field
			Stream<Table<?>> domainTables = schema.get().getTables().stream()
					.filter(t -> t.field("domain") != null);
			
			// Get domainId
			Record domainRecord = dslContext.select().from(DOMAIN).where(DOMAIN.NAME.eq(domainName)).fetchOne();	
			Integer domainId = domainRecord.get(DOMAIN.ID);
			Integer parentDomainId = domainRecord.get(DOMAIN.PARENT);
			
			System.out.println("domainId : " + domainId + ", parentDomainId : " + parentDomainId);
			
			// Get inserts of domainTables where domain is domainId
			BufferedWriter writer = new BufferedWriter(new FileWriter("/Users/sergio/Desktop/AON/InsertDomain.sql"));
			writer.write("SET FOREIGN_KEY_CHECKS=0;\n");
			
			dslContext.selectFrom(DOMAIN).where(DOMAIN.ID.eq(parentDomainId)).fetch().formatInsert(writer, DOMAIN);
			dslContext.selectFrom(DOMAIN).where(DOMAIN.ID.eq(domainId)).fetch().formatInsert(writer, DOMAIN);
			
			domainTables.forEach(t -> {
				if(null != parentDomainId) dslContext.select().from(DSL.table(t.getName())).where(((Field<Integer>) t.field("domain")).eq(parentDomainId)).fetch().formatInsert(writer, t);
				dslContext.select().from(DSL.table(t.getName())).where(((Field<Integer>) t.field("domain")).eq(domainId)).fetch().formatInsert(writer, t);
			});
			writer.write("SET FOREIGN_KEY_CHECKS=1;");
			writer.close();
			
			System.out.println("PRESENT END!");
		} else
			System.err.println("NOT PRESENT -> database : " + database + ", domainName : " + domainName);
	}
	
}
