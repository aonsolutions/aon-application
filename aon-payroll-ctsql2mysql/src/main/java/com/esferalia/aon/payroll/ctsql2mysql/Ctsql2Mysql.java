package com.esferalia.aon.payroll.ctsql2mysql;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;
import org.apache.commons.codec.binary.Base64;

import net.aonsolutions.core.dbutils.AonSQLException;
import com.code.aon.master.VersionManager;

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

/**
 * Ctsql2Mysql
 * 
 */
public class Ctsql2Mysql {

	{
		// first of all load JDBC drivers
		try {
			Class.forName("org.gjt.mm.mysql.Driver");
			Class.forName("com.transtools.jdbc.CtsqlJdbcDriver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

	}

	private String ctsqlURL;
	private String ctsqlUser;
	private String ctsqlPasswd;
	private String mysqlURL;
	private String mysqlUser;
	private String mysqlPasswd;
	private boolean dryRun;
	private boolean merge;
	private boolean disabled;
	private String domainName;
	private String domainUser;
	private String domainPasswd;
	private boolean checkFVisionado;
	private String tables[];
	private short delay;
	private String commands[];

	private String passwdHash;
	private Date fromDate;
	private File imagesDir;
	private List<String> cifs;

	public Ctsql2Mysql(String args[]) {
		parseArgs(args);
	}

	protected File getImagesDir() {
		return imagesDir;
	}

	protected boolean parseArgs(String[] args) {

		Options options = new Options();

		OptionBuilder.isRequired(false);
		OptionBuilder.hasArg(false);
		OptionBuilder.withDescription("imprime esta ayuda.");
		Option helpOption = OptionBuilder.create("help");

		OptionBuilder.isRequired(false);
		OptionBuilder.hasArg(false);
		OptionBuilder
				.withDescription("No inserta nada en la base de datos. Para chequear que las modificaciones, las operaciones sql (por pantalla) funcionan como se esparaba.");
		Option dryRunOption = OptionBuilder.create("dryrun");

		OptionBuilder.isRequired(false);
		OptionBuilder.hasArg(false);
		OptionBuilder.withDescription("Traspasar los clientes inactivos.");
		Option disabledOption = OptionBuilder.create("inactivo");

		OptionBuilder.isRequired(false);
		OptionBuilder.hasArg(false);
		OptionBuilder.withDescription("Chequear fechas de visionado.");
		Option checkFVisionadoOption = OptionBuilder.create("visionado");

		OptionBuilder.isRequired(true);
		OptionBuilder.hasArg(true);
		OptionBuilder.withArgName("URL");
		OptionBuilder.withType(String.class);
		OptionBuilder.withDescription("cadena de conxiÃ³n ctsql.");
		Option ctsqlURLOption = OptionBuilder.create("ctsqlurl");

		OptionBuilder.isRequired(true);
		OptionBuilder.hasArg(true);
		OptionBuilder.withArgName("URL");
		OptionBuilder.withType(String.class);
		OptionBuilder.withDescription("cadena de conxiÃ³n mysql");
		Option mysqlURLOption = OptionBuilder.create("mysqlurl");

		OptionBuilder.isRequired(false);
		OptionBuilder.hasArg(true);
		OptionBuilder.withArgName("name");
		OptionBuilder.withType(String.class);
		OptionBuilder
				.withDescription("usuario para conectarse a ctsql, si no es 'ctl'");
		Option ctsqlUserOption = OptionBuilder.create("ctsqluser");

		OptionBuilder.isRequired(false);
		OptionBuilder.hasArg(true);
		OptionBuilder.withArgName("name");
		OptionBuilder.withType(String.class);
		OptionBuilder.withDescription("clave para conectarse a ctsql.");
		Option ctsqlPasswdOption = OptionBuilder.create("ctsqlpasswd");

		OptionBuilder.isRequired(false);
		OptionBuilder.hasArg(true);
		OptionBuilder.withArgName("name");
		OptionBuilder.withType(String.class);
		OptionBuilder
				.withDescription("usuario para conectarse a mysql, si no es 'dbuser'");
		Option mysqlUserOption = OptionBuilder.create("mysqluser");

		OptionBuilder.isRequired(false);
		OptionBuilder.hasArg(true);
		OptionBuilder.withArgName("name");
		OptionBuilder.withType(String.class);
		OptionBuilder.withDescription("clave para conectarse a mysql.");
		Option mysqlPasswdOption = OptionBuilder.create("mysqlpasswd");

		OptionBuilder.isRequired(false);
		OptionBuilder.hasArg(true);
		OptionBuilder.withArgName("date");
		OptionBuilder.withType(String.class);
		OptionBuilder
				.withDescription("trapasar los datos a partir de esta fecha M/d/Y");
		Option fromDateOption = OptionBuilder.create("from");

		OptionBuilder.isRequired(false);
		OptionBuilder.hasArg(true);
		OptionBuilder.withArgName("dir");
		OptionBuilder.withType(String.class);
		OptionBuilder
				.withDescription("ruta del directorio de imagenes ( logos y firmas )");
		Option imagesDirOption = OptionBuilder.create("images");

		OptionBuilder.isRequired(false);
		OptionBuilder.hasArgs();
		OptionBuilder.withArgName("cifs");
		OptionBuilder.withValueSeparator(',');
		OptionBuilder.withType(String.class);
		OptionBuilder.withDescription("traspasar únicamente estas empresas");
		Option enterprisesOption = OptionBuilder.create("enterprises");

		OptionBuilder.isRequired(false);
		OptionBuilder.hasArg(true);
		OptionBuilder.withArgName("clave");
		OptionBuilder.withType(String.class);
		OptionBuilder.withDescription("clave genérica para todos los usuarios");
		Option passwdOption = OptionBuilder.create("passwd");

		OptionBuilder.isRequired(true);
		OptionBuilder.hasArg(true);
		OptionBuilder.withArgName("dominio");
		OptionBuilder.withType(String.class);
		OptionBuilder.withDescription("dominio");
		Option domainOption = OptionBuilder.create("domain");

		OptionBuilder.isRequired(false);
		OptionBuilder.hasArg(true);
		OptionBuilder.withArgName("user");
		OptionBuilder.withType(String.class);
		OptionBuilder.withDescription("Parent domain admin user");
		Option domainUserOption = OptionBuilder.create("domainUser");

		OptionBuilder.isRequired(false);
		OptionBuilder.hasArg(true);
		OptionBuilder.withArgName("clave");
		OptionBuilder.withType(String.class);
		OptionBuilder.withDescription("Parent domain admin password");
		Option domainPasswdOption = OptionBuilder.create("domainPasswd");

		OptionBuilder.isRequired(false);
		OptionBuilder.hasArgs();
		OptionBuilder.withArgName("table");
		OptionBuilder.withType(String.class);
		OptionBuilder.withValueSeparator((char) 0);
		OptionBuilder
				.withDescription("Tablas de ctsql que lanzan la sincronización ( sólo para el demonio ). ");
		Option tablesOption = OptionBuilder.create("table");

		OptionBuilder.isRequired(false);
		OptionBuilder.hasArg(true);
		OptionBuilder.withArgName("delay");
		OptionBuilder.withType(String.class);
		OptionBuilder.withDescription("");
		Option delayOption = OptionBuilder.create("delay");

		OptionBuilder.isRequired(false);
		OptionBuilder.hasArgs();
		OptionBuilder.withArgName("command");
		OptionBuilder.withType(String.class);
		OptionBuilder.withValueSeparator((char) 0);
		OptionBuilder
				.withDescription("Comandos despues de la sincronización ( sólo para el demonio ). ");
		Option commandsOption = OptionBuilder.create("command");

		OptionBuilder.isRequired(false);
		OptionBuilder.hasArg(false);
		OptionBuilder
				.withDescription("Añadir a la base de datos existente. No borrar la base de datos.");
		Option mergeOption = OptionBuilder.create("merge");

		options.addOption(helpOption);
		options.addOption(dryRunOption);
		options.addOption(disabledOption);
		options.addOption(ctsqlURLOption);
		options.addOption(mysqlURLOption);
		options.addOption(ctsqlUserOption);
		options.addOption(mysqlUserOption);
		options.addOption(ctsqlPasswdOption);
		options.addOption(mysqlPasswdOption);
		options.addOption(fromDateOption);
		options.addOption(passwdOption);
		options.addOption(domainOption);
		options.addOption(imagesDirOption);
		options.addOption(enterprisesOption);
		options.addOption(domainUserOption);
		options.addOption(domainPasswdOption);
		options.addOption(checkFVisionadoOption);
		options.addOption(tablesOption);
		options.addOption(delayOption);
		options.addOption(commandsOption);
		options.addOption(mergeOption);

		CommandLineParser parser = new PosixParser();

		HelpFormatter helpFormatter = new HelpFormatter();

		try {

			CommandLine line = parser.parse(options, args);

			if (line.hasOption(helpOption.getOpt())) {
				helpFormatter.printHelp(HelpFormatter.DEFAULT_SYNTAX_PREFIX,
						options, true);
				return false;
			}

			ctsqlURL = line.getOptionValue(ctsqlURLOption.getOpt());
			ctsqlUser = line.getOptionValue(ctsqlUserOption.getOpt(), "ctl");
			ctsqlPasswd = line
					.getOptionValue(ctsqlPasswdOption.getOpt(), "ctl");

			mysqlURL = line.getOptionValue(mysqlURLOption.getOpt());
			mysqlUser = line.getOptionValue(mysqlUserOption.getOpt(), "dbuser");
			mysqlPasswd = line.getOptionValue(mysqlPasswdOption.getOpt(),
					"serubd2000");

			dryRun = line.hasOption(dryRunOption.getOpt());

			disabled = line.hasOption(disabledOption.getOpt());
			checkFVisionado = line.hasOption(checkFVisionadoOption.getOpt());

			domainName = line.getOptionValue(domainOption.getOpt());
			domainUser = line.getOptionValue(domainUserOption.getOpt(),
					"toledo");
			domainPasswd = line.getOptionValue(domainPasswdOption.getOpt(),
					"t0l3d0");

			String fromString = line.getOptionValue(fromDateOption.getOpt());
			if (fromString != null) {
				fromDate = DateFormat.getDateInstance(DateFormat.SHORT).parse(
						fromString);
			}

			String passwd = line.getOptionValue(passwdOption.getOpt());
			if (passwd != null) {
				try {
					MessageDigest digest = MessageDigest.getInstance("SHA-1");
					digest.update(passwd.getBytes("UTF-8"));
					byte raw[] = digest.digest();
					passwdHash = new String(Base64.encodeBase64(raw), "UTF-8"); // step
																				// 5
				} catch (NoSuchAlgorithmException e) {
				}
			}

			String imagesDirPath = line
					.getOptionValue(imagesDirOption.getOpt());
			if (imagesDirPath != null) {
				imagesDir = new File(imagesDirPath);
			}

			String enterprises[] = line.getOptionValues(enterprisesOption
					.getOpt());
			if (enterprises != null) {
				cifs = Arrays.asList(enterprises);
			}

			tables = line.getOptionValues(tablesOption.getOpt());

			String delayString = line
					.getOptionValue(delayOption.getOpt(), "15");
			delay = Short.parseShort(delayString);

			commands = line.getOptionValues(commandsOption.getOpt());

			merge = line.hasOption(mergeOption.getOpt());

		} catch (Exception e) {
			helpFormatter.printHelp(HelpFormatter.DEFAULT_SYNTAX_PREFIX,
					options, true);
		}

		return true;
	}

	protected Integer newDomain(MysqlDB mysqlDB) throws IOException,
			InterruptedException, SQLException, AonSQLException {

		return mysqlDB.newConsultancyDomain(domainName, domainUser,
				domainPasswd, null /* TODO : scope ? */);
	}

	public boolean drop() {
		return !merge;
	}

	public String[] getCommands() {
		return commands;
	}

	public short getDelay() {
		return delay;
	}

	public String[] getTables() {
		return tables;
	}

	public String getCtsqlURL() {
		return ctsqlURL;
	}

	protected Connection getCtsqlConnection() throws SQLException {
		return DriverManager.getConnection(ctsqlURL, ctsqlUser, ctsqlPasswd);
	}

	protected Connection getMysqlConnection() throws SQLException {
		return DriverManager.getConnection(mysqlURL, mysqlUser, mysqlPasswd);
	}

	protected void dropDatabase() throws SQLException {
		Pattern pattern = Pattern.compile("(jdbc[^/]+//[^/]+)/([^/?]+)");

		Matcher matcher = pattern.matcher(mysqlURL);
		matcher.matches();
		String mysqlServerURL = matcher.group(1);
		String dbName = matcher.group(2);

		Statement stmt = null;
		Connection connection = null;
		try {
			connection = DriverManager.getConnection(mysqlServerURL, mysqlUser,
					mysqlPasswd);
			stmt = connection.createStatement();
			MysqlDB.info("ctsql2mysql : DROP DATABASE `{}`", dbName);
			stmt.execute("DROP DATABASE `" + dbName + "`");

		} finally {
			if (stmt != null)
				stmt.close();
			if (connection != null)
				connection.close();
		}

	}

	protected Connection getMysqlConnectionEx() throws SQLException,
			AonSQLException, IOException {
		try {
			return DriverManager
					.getConnection(mysqlURL, mysqlUser, mysqlPasswd);
		} catch (SQLException e) {

			Pattern pattern = Pattern.compile("(jdbc[^/]+//[^/]+)/([^/?]+)");

			Matcher matcher = pattern.matcher(mysqlURL);
			matcher.matches();
			String mysqlServerURL = matcher.group(1);
			String dbName = matcher.group(2);

			Connection connection = DriverManager.getConnection(mysqlServerURL,
					mysqlUser, mysqlPasswd);

			VersionManager versionManager = new VersionManager();
			versionManager.createDatabase(connection, dbName);
			versionManager.uptodateDatabase(connection);

			return connection;
		}
	}

	protected void transfer() throws ClassNotFoundException, SQLException,
			java.text.ParseException, AonSQLException, IOException,
			InterruptedException {
		if (ctsqlURL == null) {
			return;
		}

		Connection ctsqlConnection = null;
		Connection mysqlConnection = null;

		try {
			ctsqlConnection = getCtsqlConnection();

			mysqlConnection = getMysqlConnectionEx();
			mysqlConnection.setAutoCommit(false);

			MysqlDB mysqlWriter = new MysqlDB(mysqlConnection);

			mysqlWriter.setCifs(cifs);
			mysqlWriter.setDisable(disabled);
			mysqlWriter.setFromDate(fromDate);
			mysqlWriter.setImagesDir(imagesDir);
			mysqlWriter.setPasswdHash(passwdHash);
			mysqlWriter.setDomainName(domainName);
			mysqlWriter.setCheckFVisionado(checkFVisionado);

			Integer domain = newDomain(mysqlWriter);
			mysqlWriter.setDefaultDomain(domain);
			MysqlDB.info("ctsql2mysql[{}] : Default domain {}", domain,
					domainName);

			/*
			 * Integer registry = mysqlWriter.insertEnterprise( domain, null,
			 * //TODO: Consultancy document number. Country.ES, null, //TODO:
			 * Consultancy name. Country.ES, null, //TODO: Consultancy alias.
			 * null, DefaultMysqlDB.enum2short(CustomerStatus.ACTIVE));
			 * 
			 * // TODO: Company ... related entries, like 'logo'
			 * mysqlWriter.insertCompany(registry, domain, false, false, false,
			 * false);
			 */

			CtsqlDB ctsqlReader = new CtsqlDB(ctsqlConnection);
			mysqlWriter.write(ctsqlReader);

			if (!dryRun) {
				mysqlConnection.commit();
			}
		} finally {
			if (ctsqlConnection != null)
				ctsqlConnection.close();
			if (mysqlConnection != null)
				mysqlConnection.close();
		}
	}

	public void exec() throws IOException {
		if (commands == null)
			return;
		for (String cmd : commands)
			exec(cmd);
	}

	public void exec(String command) throws IOException {

		MysqlDB.info("ctsql2mysql : command {}", command);

		Runtime runtime = Runtime.getRuntime();
		String cmd[] = { "/bin/sh", "-c", command };
		Process process = runtime.exec(cmd);

		InputStream out = process.getInputStream();
		new InputStream2Output(out, System.out).run();

		InputStream err = process.getErrorStream();
		new InputStream2Output(err, System.err).run();
	}

	public static void main(String[] args) throws SQLException,
			ClassNotFoundException, java.text.ParseException, AonSQLException,
			IOException, InterruptedException {
		Ctsql2Mysql ctsql2Mysql = new Ctsql2Mysql(args);
		
		if (ctsql2Mysql.drop()) {
			try {
				ctsql2Mysql.dropDatabase();
			} catch (SQLException e) {
			}
		}
		ctsql2Mysql.transfer();
		ctsql2Mysql.exec();
	}

	public boolean isDryRun() {
		return dryRun;
	}

	private static URL getCreateScript() {
		String name = "com/esferalia/aon/payroll/ctsql2mysql/create.database.sql";
		return getScript(name);
	}

	private static URL getScript(String name) {
		ClassLoader cl = Thread.currentThread().getContextClassLoader();
		return cl.getResource(name);
	}

	static class InputStream2Output implements Runnable {

		private InputStream in;
		private OutputStream out;

		public InputStream2Output(InputStream in, OutputStream out) {
			this.in = in;
			this.out = out;
		}

		@Override
		public void run() {
			try {
				int read = -1;
				byte b[] = new byte[256];
				while ((read = in.read(b)) != -1)
					out.write(b, 0, read);
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}

}
