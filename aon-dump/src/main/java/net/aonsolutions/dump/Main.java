package net.aonsolutions.dump;

import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.sql.SQLException;
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

import com.esferalia.aon.jooq.tables.Domain;

public class Main {
	
	private static AonDump aonDump;
	
	public static void main(String[] args) throws SQLException, ClassNotFoundException, NoSuchFieldException,
			SecurityException, IOException {

		/**
		 * CONTROL DE ERRORES DE PARAMETROS DE ENTRADA
		 */
		Option hostNameOpt = OptionBuilder
				.withArgName("name")
				.hasArg()
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

		Option nameDomainOpt = OptionBuilder
				.withArgName("name")
				.hasArg()
				.isRequired(true)
				.withLongOpt("domainDump")
				.withDescription("Domains' name for dump.")
				.create();

		Option newNameDomainOpt = OptionBuilder
				.withArgName("name")
				.hasArg()
				.isRequired(true)
				.withLongOpt("newNameDomain")
				.withDescription("Select how to rename domain. For example: \"Copy_of_{domain}\".")
				.create();

		Option renameNIF = OptionBuilder
				.withArgName("name")
				.hasArg()
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

		Option helpOpt = OptionBuilder
				.withLongOpt("help")
				.withDescription("Shows help for entrys arguments.")
				.create();

		Options options = new Options().addOption(hostNameOpt).addOption(portOpt).addOption(dataBaseOpt)
				.addOption(userOpt).addOption(passwordOpt).addOption(nameDomainOpt).addOption(newNameDomainOpt)
				.addOption(renameNIF).addOption(executeOpt).addOption(zipOpt).addOption(sqlOpt)
				.addOption(eraseUsers).addOption(renameLogin).addOption(renamePass).addOption(commentsOpt)
				.addOption(standAloneOpt).addOption(helpOpt);

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

			String domain = cmd.getOptionValue(nameDomainOpt.getLongOpt());
			String newDomain = cmd.getOptionValue(newNameDomainOpt.getLongOpt());

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
			
			PrintStream out = null;
			ZipOutputStream zos = null;
			Integer idDomain = aonDump.dslContext.select(DOMAIN.ID).from(DOMAIN).where((DOMAIN.NAME).equal(domain)).fetchOne().value1();
			
			if (cmd.hasOption(zipOpt.getLongOpt())){
				File file = new File(zipName+".zip");
				FileOutputStream fos = new FileOutputStream(file);
				zos = new ZipOutputStream(fos);
				zos.putNextEntry(new ZipEntry("sql_Java"));
				out  = new PrintStream(zos, true, "UTF-8");
		
			}else if (cmd.hasOption(sqlOpt.getLongOpt())){
				File file = new File(sqlName+".sql");
				out = new PrintStream(file, "UTF-8");
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
			
			if (cmd.hasOption(standAloneOpt.getLongOpt()))
				cb = new DomainParentCallBackDump(cb, idDomain);
			else
				cb = new DomainSiblingCallBackDump(cb);
			
			cb = new IndexUniqueCallBackDump(cb);
			cb = new ModifyDataCallBack(cb, newDomain, "domain", "name");
			cb = new TaskProcessCallBack(cb, System.out, aonDump, 0, 0); //MIRAR ESTO
			
			if (cmd.hasOption(renameNIF.getLongOpt()))
				cb = new ModifyDataCallBack(cb, nif, "registry", "document");
			
			cb = new DownloadCallBackDump(cb, false);

			if (cmd.hasOption(commentsOpt.getLongOpt()))
				cb = new CommentsPrintCallbackDump(out, cb);
			
			if (cmd.hasOption(eraseUsers.getLongOpt()))
				cb = new EraseUser(cb, aonDump.dslContext, pass, login);

			aonDump.findDomainInTables(aonDump.connection, aonDump.dslContext, cb, hostName, database, domain);
			
			// Close our file
			if (zos != null)
				zos.closeEntry();
			
			out.close();
			
			System.out.println("FIN DEL PROGRAMA, TODO OKKKKK");

		} catch (ParseException e) {

			// Oops, something went wrong
			System.err.println("Parsing failed.  Reason: " + e.getMessage());

			// Automatically generate the help statement
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("aon-dump", options);
		}
	}
}
