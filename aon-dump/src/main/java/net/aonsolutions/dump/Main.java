package net.aonsolutions.dump;

import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.InsertSetMoreStep;
import org.jooq.Record;
import org.jooq.Record3;
import org.jooq.Result;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.impl.DSL;

public class Main {
	
	private static AonDump aonDump;
	
	@SuppressWarnings("static-access")
	public static void main(String[] args) throws SQLException, ClassNotFoundException, NoSuchFieldException,
			SecurityException, IOException {
		
		Option hostNameOpt = OptionBuilder
				.hasArg()
				.withArgName("name")
				.isRequired(true)
				.withLongOpt("host")
				.withDescription("Host name which we want to connect to.")
				.create();

		Option portOpt = OptionBuilder
				.withArgName("name")
				.hasArg()
				.withLongOpt("port")
				.withDescription("Port to connect to host.")
				.withType(Integer.class)
				.create();

		Option dataBaseOpt = OptionBuilder
				.withArgName("name")
				.hasArg()
				.isRequired(true)
				.withLongOpt("database")
				.withDescription("DataBase we are going to use.")
				.create();

		Option userOpt = OptionBuilder
				.withArgName("name")
				.hasArg()
				.isRequired(true)
				.withLongOpt("user")
				.withDescription("User for connecting to server.")
				.create();

		Option passwordOpt = OptionBuilder
				.withArgName("name")
				.hasArg()
				.isRequired(true)
				.withLongOpt("password")
				.withDescription("Password for connecting to server.")
				.create();

		Option domainOpt = OptionBuilder
				.hasArg()
				.isRequired(true)
				.withArgName("name")
				.withLongOpt("domain")
				.withDescription("Domain's name for dump.")
				.create();

		Option nameOpt = OptionBuilder
				.hasArg()
				.isRequired(true)
				.withArgName("name")
				.withLongOpt("name")
				.withDescription("Select how to rename domain. For example: \"Copy_of_{domain}\".")
				.create();

		Option renameNIF = OptionBuilder
				.hasArg()
				.withArgName("name")
				.withLongOpt("nif")
				.withDescription("Select how to raname nif. For example: \"Copy_of_{nif}\".")
				.create();

		Option executeOpt = OptionBuilder
				.withLongOpt("execute")
				.withDescription("If you want to execute the sql archive, instead downloading.")
				.create();
		
		Option zipOpt = OptionBuilder
				.withArgName("name")
				.hasArg()
				.withLongOpt("zip")
				.withDescription("Zip the downloaded file. --zip path/nameFile (without extension)")
				.create();
		
		Option sqlOpt = OptionBuilder
				.withArgName("name")
				.hasArg()
				.withLongOpt("sql")
				.withDescription("Makes a sql --sql path/nameFile (without extension).")
				.create();
		
		Option eraseUsers = OptionBuilder
				.withLongOpt("userErase")
				.withDescription("Erase all the users of the Database. Requiered --name & --login")
				.create();

		Option renameLogin = OptionBuilder
				.withArgName("name")
				.hasArg()
				.withLongOpt("login")
				.withDescription("Create a new password for users")
				.create();

		Option renamePass = OptionBuilder
				.withArgName("name")
				.hasArg()
				.withLongOpt("pass")
				.withDescription("Create a new password for users")
				.create();

		Option commentsOpt = OptionBuilder
				.withLongOpt("comments")
				.withDescription("Add comments to the SQL file. Required --sql or --zip")
				.create();
		
		Option standAloneOpt = OptionBuilder
				.withLongOpt("alone")
				.withDescription("Download domain Stand Alone version. Default option is Sibling")
				.create();
		
		Option recursiveOpt = OptionBuilder
				.withLongOpt("recursive")
				.withDescription("Dump domains recursively.")
				.create('r');

		Option includeOpt = OptionBuilder
				.hasArg()
				.withArgName("name")
				.withLongOpt("include")
				.withDescription("Include only following domain.")
				.create('i');

		Option listDomains = OptionBuilder
				.withLongOpt("list")
				.withDescription("List all the domains you have access.")
				.create();

		Option helpOpt = OptionBuilder
				.withLongOpt("help")
				.withDescription("Shows help for entrys arguments.")
				.create();

		Option verboseOpt = OptionBuilder
				.withLongOpt("verbose")
				.withDescription("Print info about the various stages.")
				.create('v');
		
		//@formatter:off
		Options options = new Options()
				.addOption(hostNameOpt)
				.addOption(portOpt)
				.addOption(dataBaseOpt)
				.addOption(userOpt)
				.addOption(passwordOpt)
				.addOption(domainOpt)
				.addOption(nameOpt)
				.addOption(renameNIF)
				.addOption(executeOpt)
				.addOption(zipOpt)
				.addOption(sqlOpt)
				.addOption(eraseUsers)
				.addOption(renameLogin)
				.addOption(renamePass)
				.addOption(commentsOpt)
				.addOption(standAloneOpt)
				.addOption(listDomains)
				.addOption(recursiveOpt)
				.addOption(includeOpt)
				.addOption(helpOpt);
		//@formatter:on

		// Parser create
		CommandLineParser parser = new GnuParser();

		try {

			// Parse the command line arguments
			CommandLine cmd = parser.parse(options, args);

			if (cmd.hasOption(helpOpt.getLongOpt())) {
				// Automatically generate the help statement
				HelpFormatter formatter = new HelpFormatter();
				formatter.printHelp("aon-dump", options);
				return;
				
			}

			// Get all the information we have gotten from the comand line
			String hostName = cmd.getOptionValue(hostNameOpt.getLongOpt());
			String user = cmd.getOptionValue(userOpt.getLongOpt());
			String password = cmd.getOptionValue(passwordOpt.getLongOpt());
			String database = cmd.getOptionValue(dataBaseOpt.getLongOpt());
			String port = cmd.hasOption(portOpt.getLongOpt()) ? cmd.getOptionValue(portOpt.getLongOpt()) : "3306";

			String url = "jdbc:mysql://" + hostName + ":" + port + "/" + database;

			String domain = cmd.getOptionValue(domainOpt.getLongOpt());
			String name = cmd.getOptionValue(nameOpt.getLongOpt());

			String nif = cmd.getOptionValue(renameNIF.getLongOpt());

			String pass = cmd.getOptionValue(renamePass.getLongOpt());
			String login = cmd.getOptionValue(renameLogin.getLongOpt());
			
			String zipName = cmd.getOptionValue(zipOpt.getLongOpt());
			String sqlName = "";
			
			if (cmd.hasOption(sqlOpt.getLongOpt()))
				sqlName = cmd.getOptionValue(sqlOpt.getLongOpt());
			else
				sqlName = "/tmp/dumpSQL";
			
			// Create an AonDump object in order to start our library
			aonDump = new AonDump(url, user, password);
			
			Integer idDomain = 0;
			ZipOutputStream zos = null;
			PrintStream out = System.out;
			
			if (cmd.hasOption(listDomains.getLongOpt())){
				listDomains(aonDump.dslContext, System.err);
				return;
			}
			
			if (cmd.hasOption(zipOpt.getLongOpt())){
				File file = new File(zipName+".zip");
				FileOutputStream fos = new FileOutputStream(file);
				zos = new ZipOutputStream(fos);
				zos.putNextEntry(new ZipEntry("sql_Java"));
				out  = new PrintStream(zos, true, "UTF-8");
				idDomain = aonDump.dslContext.select(DOMAIN.ID).from(DOMAIN).where((DOMAIN.NAME).equal(domain)).fetchOne().value1();
		
			}else if (cmd.hasOption(sqlOpt.getLongOpt())){
				File file = new File(sqlName+".sql");
				out = new PrintStream(file, "UTF-8");
				idDomain = aonDump.dslContext.select(DOMAIN.ID).from(DOMAIN).where((DOMAIN.NAME).equal(domain)).fetchOne().value1();
			}

			//Creamos los Callbacks
			CallbackDump cb;
			
			if (cmd.hasOption(executeOpt.getLongOpt()))
				cb = new CallbackDumpExecute(aonDump.dslContext);
			else
				cb = new CallbackDumpPrint(out);

			cb = new ErrorReferenceCallBackDump(cb);
			
			if (cmd.hasOption(standAloneOpt.getLongOpt()))
				cb = new ParentCallbackDump(cb);
			else
				cb = new SiblingCallBackDump(cb);
			
			cb = new DomainZeroCallbackDump(cb);
			cb = new DomainNullCallbackDump(cb);
			
			if (cmd.hasOption(standAloneOpt.getLongOpt()))
				cb = new DomainParentCallBackDump(cb, idDomain);
			else
				cb = new DomainSiblingCallBackDump(cb);
			
			cb = new IndexUniqueCallBackDump(cb);
			cb = new ModifyDataCallBack(cb, name, "domain", "name");
			
			if (cmd.hasOption(verboseOpt.getLongOpt())) //TODO:
				cb = new ConsoleInformationCallBack(cb, System.out, aonDump, 0, 0); 
			
			if (cmd.hasOption(renameNIF.getLongOpt()))
				cb = new ModifyDataCallBack(cb, nif, "registry", "document");
			
			cb = new DownloadCallBackDump(cb, false);

			if (cmd.hasOption(commentsOpt.getLongOpt()))
				cb = new CommentsPrintCallbackDump(out, cb);
			
			if (cmd.hasOption(eraseUsers.getLongOpt()))
				cb = new EraseUser(cb, aonDump.dslContext, pass, login);

			IdsMap parentIdsMap = aonDump.findDomainInTables(aonDump.connection, aonDump.dslContext, cb, hostName, database, domain);
			
			cb = new AbstractChaimCallbackDump(cb) {
				@Override
				public void header(Schema schema, String hostName, Map<Table<?>, Integer> domainTables,
						DSLContext dslContext, int id, IdsMap idsMap) {
					super.header(schema, hostName, domainTables, dslContext, id, idsMap);
				}
				
				@Override
				public Field<Integer> onErrFk(DSLContext dslContext, Record r, ForeignKey<?, ?> fk, AonDump aondump,
						IdsMap idsMap, CallbackDump cb, List<Table<?>> ciclica, List<?> references, Condition where) {
					
					Table<?> tableReference = fk.getKey().getTable();
					String fieldNameId = fk.getKey().getFields().get(0).getName();
					String tableReferenceName = fieldNameId.equals("id") ? tableReference.getName() : fieldNameId;
					try {
						Field<Integer> parentOrder = parentIdsMap.getOrder(tableReferenceName,
								((Integer) r.getValue(fk.getFields().get(0), Integer.class)));
						if ( parentOrder != null )
							return parentOrder;
					} catch ( Exception e ) {
						
					}
					
					return super.onErrFk(dslContext, r, fk, aondump, idsMap, cb, ciclica, references, where);
				}
				
			};
			
			if ( cmd.hasOption(recursiveOpt.getLongOpt())) {
				for( String d : cmd.getOptionValues(includeOpt.getLongOpt()))
						aonDump.findDomainInTables(aonDump.connection, aonDump.dslContext, cb, hostName, database, d + "-" + domain);
			}
			
			
			// Close our file
			if (zos != null)
				zos.closeEntry();
			
			out.close();
			

		} catch (ParseException e) {

			// Oops, something went wrong
			System.err.println("Parsing failed.  Reason: " + e.getMessage());

			// Automatically generate the help statement
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("aon-dump", options);
		}
	}

	private static void listDomains(DSLContext dslContext, PrintStream err) {
		Result<Record3<Integer, String, String>> domainsResult = dslContext
				.select(DOMAIN.ID, DOMAIN.NAME, DOMAIN.DESCRIPTION).from(DOMAIN).fetch();
		
		domainsResult.forEach(d -> err.println(d.getValue(DOMAIN.NAME)));
		
	}

	private static String[] getChildDomains(DSLContext dslContext, String parent) {

		return  
		dslContext
		.select()
		.from(DOMAIN)
		.where(DOMAIN.PARENT.in(DSL.select(DOMAIN.ID).from(DOMAIN).where(DOMAIN.NAME.eq(parent))))
		.fetch(DOMAIN.NAME)
		.toArray(new String[]{});
		
		
	}
}
