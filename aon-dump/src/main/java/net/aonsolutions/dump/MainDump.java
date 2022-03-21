package net.aonsolutions.dump;

import static com.esferalia.aon.jooq.tables.ApplicationUser.APPLICATION_USER;
import static com.esferalia.aon.jooq.tables.ApplicationUserProfile.APPLICATION_USER_PROFILE;
import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static com.esferalia.aon.jooq.tables.DomainApplication.DOMAIN_APPLICATION;
import static com.esferalia.aon.jooq.tables.Profile.PROFILE;
import static com.esferalia.aon.jooq.tables.Scope.SCOPE;
import static com.esferalia.aon.jooq.tables.User.USER;
import static com.esferalia.aon.jooq.tables.UserAppRole.USER_APP_ROLE;
import static com.esferalia.aon.jooq.tables.UserScope.USER_SCOPE;
import static com.esferalia.aon.jooq.tables.UserWorkgroup.USER_WORKGROUP;
import static com.esferalia.aon.jooq.tables.Workgroup.WORKGROUP;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.TimeZone;
import java.util.stream.Collectors;
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
import org.jooq.Record1;
import org.jooq.Record3;
import org.jooq.Result;
import org.jooq.SQLDialect;
import org.jooq.Schema;
import org.jooq.SelectConditionStep;
import org.jooq.Table;
import org.jooq.conf.ParamType;
import org.jooq.conf.RenderKeywordCase;
import org.jooq.conf.RenderQuotedNames;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

public class MainDump {
	
	private final static String BREAKLINE = ";\n";
	
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
				.argName("name")
				.longOpt("domain")
				.desc("Domain's name for dump.")
				.build();
		
		Option nameOpt = Option.builder()
				.hasArg()
				.argName("name")
				.longOpt("name")
				.desc("New Domain's output name for dump.")
				.build();
		
		Option systemOpt = Option.builder()
				.longOpt("system")
				.desc("Get system data for domain dump.")
				.build();
		
		Option unifyDomainsOpt = Option.builder()
				.longOpt("unify")
				.desc("Unify parent and child Domains for dump.")
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
				.addOption(unifyDomainsOpt)
				.addOption(domainOpt)
				.addOption(nameOpt)
				.addOption(systemOpt)
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
			
			if (cmd.hasOption(helpOpt.getLongOpt())) {
				// Automatically generate the help statement
				HelpFormatter formatter = new HelpFormatter();
				formatter.printHelp("aon-dump", options);
				return;
			}
			
			boolean listDomain = cmd.hasOption(listDomains.getLongOpt());
			boolean unifyDomains = cmd.hasOption(unifyDomainsOpt.getLongOpt());
			boolean system = cmd.hasOption(systemOpt.getLongOpt());
			String newDomainName = cmd.getOptionValue(nameOpt.getLongOpt());
			
			dump(user, password, url, database, domain, listDomain, unifyDomains, system, newDomainName);
			
		} catch (ParseException e) {

			// Oops, something went wrong
			System.err.println("Parsing failed.  Reason: " + e.getMessage());

			// Automatically generate the help statement
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("aon-dump", options);
		}
	}

	/**
	 * 
	 * @param user
	 * @param password
	 * @param url
	 * @param database
	 * @param domain
	 * @param listDomain : true only returns a list of aviable domains
	 * @param unifyDomains
	 * @param newDomainName : new output domain name
	 * @param system : true gets system data
	 * @throws SQLException
	 * @throws IOException
	 */
	
	public static void dump(String user, String password, String url, String database, String domain, boolean listDomain, boolean unifyDomains, boolean system, String newDomainName) throws SQLException, IOException {
		// DSLContext creation
		DSLContext dslContext = createDSLContext(user, password, url);
		
		if (listDomain){
			listDomains(dslContext, System.err);
			return;
		}
		
		dumpDomain(dslContext, database, domain, unifyDomains, system, newDomainName);
	}

	// Create DslContext
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

	// List aviable domains
	private static void listDomains(DSLContext dslContext, PrintStream err) {
		Result<Record3<Integer, String, String>> domainsResult = dslContext
				.select(DOMAIN.ID, DOMAIN.NAME, DOMAIN.DESCRIPTION).from(DOMAIN).fetch();
		
		domainsResult.forEach(d -> err.println(d.getValue(DOMAIN.NAME)));
		
	}
	
	// List parent tables to be omited if unify
	private static List<String> getOmitTables() {
		// TODO: estas son las tablas que he tenido que omitir para que funcione el dump, 
		// pero igual se podrian quitar mas tablas que no haran falta por parte del padre (como por ejemplo: workplace y todos los datos de laboral
		// que no sean de configuracion), de hecho no estoy seguro pero creo que valdría con descargarnos solo las tablas que tengas algun tipo de info
		// que afecte a la configuracion del hijo.. (no se, como idea solo)
		List<String> tables = new ArrayList<>();
		tables.add("company");
		tables.add("domain_application");
		tables.add("app_param");
		tables.add("workgroup");
		tables.add("enterprise");
		return tables;
	}
	
	// --------------------------------------------------------------------------------------------------------------------------------------------
	// DUMP METHOD
	// --------------------------------------------------------------------------------------------------------------------------------------------
	
	private static void dumpDomain(DSLContext dslContext, String database, String domainName, boolean unifyDomains, boolean system, String newDomainName) throws IOException {
		List<String> parentTablesNotDownload = getOmitTables();
		
		Optional<Schema> schema = dslContext.meta().getSchemas().stream()
				.filter(s -> s.getName().equals(database)).findFirst();
		
		if(schema.isPresent()) {
			
			FileWriter file = new FileWriter("/Users/sergio/Desktop/AON/InsertDomain.sql");
			BufferedWriter writer = new BufferedWriter(file);
			
			disableForeignKeys(writer);
			
			if(system) getSystemData(dslContext, schema, writer);
			
			Stream<Table<?>> domainTables = getDomainTables(schema, unifyDomains);
			
			// Domain record
			Record domainRecord = dslContext.select().from(DOMAIN).where(DOMAIN.NAME.eq(domainName)).fetchOne();	
			Integer domainId = domainRecord.get(DOMAIN.ID);
			Integer parentDomainId = domainRecord.get(DOMAIN.PARENT);
			
			// Start inserts
			getParentDomainInsert(dslContext, parentDomainId, unifyDomains, newDomainName, writer);
			getDomainInsert(dslContext, domainId, unifyDomains, newDomainName, writer);
			
			domainTables.forEach(t -> {
				Field<?>[] fields = unifyDomainFields(unifyDomains, t.fields(), domainId);
				Field<Integer> domainField = (Field<Integer>) t.field("domain");
				
				if(null != parentDomainId && ((unifyDomains && !parentTablesNotDownload.contains(t.getName())) || !unifyDomains))
					dslContext.select(fields).from(DSL.table(t.getName())).where(domainField.eq(parentDomainId)).fetch().formatInsert(writer, t);
				
				dslContext.select().from(DSL.table(t.getName())).where(domainField.eq(domainId)).fetch().formatInsert(writer, t);
			});
			
			if(unifyDomains) createDefaultUser(dslContext, domainId, parentDomainId, writer);
			// End inserts
			
			enableForeignKeys(writer);

			writer.close();
			
			System.out.println("Dump end");
			
		} else
			System.err.println("NOT PRESENT -> database : " + database + ", domainName : " + domainName);
	}
	
	// --------------------------------------------------------------------------------------------------------------------------------------------
	// DUMP AUXILIAR METHODS
	// --------------------------------------------------------------------------------------------------------------------------------------------
	
	// Get domain tables
	private static Stream<Table<?>> getDomainTables(Optional<Schema> schema, boolean unifyDomains) {
		return schema.isPresent() && unifyDomains ?  
				
				schema.get().getTables().stream()
					.filter(t -> t.field("domain") != null)
					.filter(t -> !t.getName().contains("user")) :
					
				schema.get().getTables().stream()
					.filter(t -> t.field("domain") != null);
	}

	// Unify domain field
	private static Field<?>[] unifyDomainFields(boolean unifyDomains, Field<?>[] fields, Integer domainId) {
		if(!unifyDomains) return fields;
		for(int i=0; i<fields.length; i++) {
			if(fields[i].getName().equals("domain"))
				fields[i] = DSL.field(domainId.toString(), Integer.class).as(DSL.name(fields[i].getName()));
		}
		return fields;
	}
	
	// Get system data tables
	private static void getSystemData(DSLContext dslContext, Optional<Schema> schema, BufferedWriter writer) {
		Stream<Table<?>> allTables = schema.get().getTables().stream();
		List<Table<?>> allTablesList = allTables.collect(Collectors.toList());
		for(Table<?> t : allTablesList) {
			if(t.getName().equals("domain") || t.getName().equals("app_param")) continue;
			
			if(t.field("domain") == null)
				dslContext.select().from(DSL.table(t.getName())).fetch().formatInsert(writer, t);
			else {
				Field<Integer> domainField = (Field<Integer>) t.field("domain");
				dslContext.select().from(DSL.table(t.getName()))
					.where(domainField.isNull().or(domainField.eq(0)))
					.fetch().formatInsert(writer, t);
			}	
		}
	}
	
	// Parent insert
	private static void getParentDomainInsert(DSLContext dslContext, Integer parentDomainId, boolean unifyDomains, String newDomainName, BufferedWriter writer) {
		if(!unifyDomains) {
			if(null == newDomainName) dslContext.selectFrom(DOMAIN).where(DOMAIN.ID.eq(parentDomainId)).fetch().formatInsert(writer, DOMAIN);
			else {
				Field<?>[] domainFields = DOMAIN.fields();
				
				for(int i=0; i<domainFields.length; i++) {
					if(domainFields[i].getName().equals("name"))
						domainFields[i] = DSL.concat(newDomainName).as(DSL.name(domainFields[i].getName()));
				}
				
				dslContext.select(domainFields).from(DOMAIN).where(DOMAIN.ID.eq(parentDomainId)).fetch().formatInsert(writer, DOMAIN);
			}
		}
	}

	// Domain insert
	private static void getDomainInsert(DSLContext dslContext, Integer domainId, boolean unifyDomains, String newDomainName, BufferedWriter writer) {
		Field<?>[] domainFields = DOMAIN.fields();
		
		for(int i=0; i<domainFields.length; i++) {
			if(domainFields[i].getName().equals("parent") && unifyDomains)
				domainFields[i] = DSL.castNull(domainFields[i]).as(DSL.name(domainFields[i].getName()));
			if(domainFields[i].getName().equals("name"))
				domainFields[i] = unifyDomains ? DSL.concat(newDomainName).as(DSL.name(domainFields[i].getName()))
						: DSL.concat("child-" + newDomainName).as(DSL.name(domainFields[i].getName()));
		}
		
		dslContext.select(domainFields).from(DOMAIN).where(DOMAIN.ID.eq(domainId)).fetch().formatInsert(writer, DOMAIN);
	}
	
	// Disable FK
	private static void disableForeignKeys(BufferedWriter writer) throws IOException {
		writer.write("SET FOREIGN_KEY_CHECKS=0" + BREAKLINE);
	}
	
	// Enable FK
	private static void enableForeignKeys(BufferedWriter writer) throws IOException {
		writer.write("SET FOREIGN_KEY_CHECKS=1" + BREAKLINE);
	}

	// Create default user for unify option
	private static void createDefaultUser(DSLContext dslContext, Integer domainId, Integer parentDomain, BufferedWriter writer) throws IOException {
		
		String userInsertSql = dslContext.insertInto(USER).set(USER.DOMAIN, domainId).set(USER.NAME, "Usuario Dump").set(USER.LOGIN, "dump").set(USER.ACTIVE, (byte)1).set(USER.PASSWORD, "qJrqE4/N/gtQH3kEwl49Itwy1JA=").getSQL();
		SelectConditionStep<Record1<Integer>> userIdSelect = dslContext.select(USER.ID).from(USER).where(USER.DOMAIN.eq(domainId)).and(USER.LOGIN.eq("dump"));
		
		SelectConditionStep<Record1<Integer>> domainApplicationSelect = dslContext.select(DOMAIN_APPLICATION.ID).from(DOMAIN_APPLICATION).where(DOMAIN_APPLICATION.DOMAIN.eq(domainId));
		String applicationUserInsertSql = dslContext.insertInto(APPLICATION_USER).set(APPLICATION_USER.DOMAIN, domainId).set(APPLICATION_USER.USER_ID, userIdSelect).set(APPLICATION_USER.DOMAIN_APPLICATION, domainApplicationSelect).getSQL();
		SelectConditionStep<Record1<Integer>> applicationUserSelect = dslContext.select(APPLICATION_USER.ID).from(APPLICATION_USER).where(APPLICATION_USER.DOMAIN.eq(domainId)).and(APPLICATION_USER.USER_ID.eq(userIdSelect));

		List<Integer> profileSelect = dslContext.select(PROFILE.ID).from(PROFILE).where(PROFILE.NAME.eq("Administrador")).orderBy(PROFILE.ID.desc()).fetch(PROFILE.ID);
		String applicationUserProfileInsertSql = dslContext.insertInto(APPLICATION_USER_PROFILE).set(APPLICATION_USER_PROFILE.DOMAIN, domainId).set(APPLICATION_USER_PROFILE.APPLICATION_USER, applicationUserSelect).set(APPLICATION_USER_PROFILE.PROFILE, profileSelect.get(0)).getSQL();		
		
		// TODO: No he sabido como hacer esto con una insert a partir de una select, por que no he conseguido que me funcionase
		List<Integer> domainScopeIds = dslContext.select(SCOPE.ID).from(SCOPE).where(SCOPE.DOMAIN.eq(domainId).or(SCOPE.DOMAIN.eq(parentDomain))).fetch(SCOPE.ID);
		List<String> userScopesInsertSql = new ArrayList<>();
		domainScopeIds.forEach(scopeId -> {
			String userScopeInsertSql = dslContext.insertInto(USER_SCOPE).set(USER_SCOPE.DOMAIN, domainId).set(USER_SCOPE.USER_ID, userIdSelect).set(USER_SCOPE.SCOPE, scopeId).getSQL();
			userScopesInsertSql.add(userScopeInsertSql);
		});
		
		SelectConditionStep<Record1<Integer>> workgroupSelect = dslContext.select(WORKGROUP.ID).from(WORKGROUP).where(WORKGROUP.DOMAIN.eq(domainId));
		String userWorkgroupInsertSql = dslContext.insertInto(USER_WORKGROUP).set(USER_WORKGROUP.DOMAIN, domainId).set(USER_WORKGROUP.USER_ID, userIdSelect).set(USER_WORKGROUP.WORKGROUP, workgroupSelect).getSQL();
		
		String userAppRoleInsertSql = dslContext.insertInto(USER_APP_ROLE).set(USER_APP_ROLE.DOMAIN, domainId).set(USER_APP_ROLE.APP, (byte)-1).set(USER_APP_ROLE.USER_ID, userIdSelect).set(USER_APP_ROLE.ROLE, (byte)0).getSQL();
		
		writer.write(userInsertSql + BREAKLINE);
		writer.write(applicationUserInsertSql + BREAKLINE);
		writer.write(applicationUserProfileInsertSql + BREAKLINE);
		userScopesInsertSql.forEach(userScopeInsertSql -> {
			try {
				writer.write(userScopeInsertSql + BREAKLINE);
			} catch (IOException e) {}
		});
		writer.write(userWorkgroupInsertSql + BREAKLINE);
		writer.write(userAppRoleInsertSql + BREAKLINE);
	}
	
}
