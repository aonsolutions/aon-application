package com.code.aon.google.apis.drive;

import static com.code.aon.google.apis.DatabaseSync.getDomains;
import static com.code.aon.google.apis.DriveUtils.sync2;
import static org.apache.commons.cli.HelpFormatter.DEFAULT_SYNTAX_PREFIX;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyStoreException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import javax.naming.NamingException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.google.apis.DatabaseSync;
import com.code.aon.google.apis.DriveData;
import com.code.aon.google.apis.DriveUtils;
import com.code.aon.google.apis.FileInfo;
import com.code.aon.google.apis.jooq.DBConsults;
import com.code.aon.pool.AonConnectionException;
import com.code.aon.registry.enumeration.RegistryAttachmentType;
import com.google.api.services.drive.Drive;

public class SynchronizeFiles {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(SynchronizeFiles.class.getName());

	static int numero = 0;

	public static void synchronizeSF() throws IOException, SQLException,
			AonConnectionException, KeyStoreException,
			GeneralSecurityException, NamingException {
		Map<String, String> domains = getDomains();
		// obtiene todos los dominios de la BD

		for (String key : domains.keySet()) {
			// recorre todos los dominios de la BD
			synchronizeSF(key);
		}
	}

	public static void synchronizeSF(String domain) throws SQLException,
			AonConnectionException, IOException, KeyStoreException,
			GeneralSecurityException, NamingException {
		// DriveData dd=DatabaseSync.getDomainFiles(domain);
		// get iattachs, get contract attachs,.... y añadir a DriveData
		DriveData dd = getAttachsSF(domain);

		// long max= 100000;

		// if (max >= dd.getGservice().getSize()){

		// Rattach
		if (dd.getGservice().getClientId() != null && dd.getAttachs() != null
				&& dd.getAttachs().size() > 0) {
			Drive drive = null;
			try {
				drive = DriveUtils.serviceInitialize(dd.getGservice());
			} catch (IOException e) {
				LOGGER.error("I/O Error connecting to Drive: {}, {}. {}",
						domain, dd.getGservice().getEmailAddress(),
						e.getMessage());
				throw e;
			} catch (GeneralSecurityException e) {
				LOGGER.error("Security Error connecting to Drive: {}, {}. {}",
						domain, dd.getGservice().getEmailAddress(),
						e.getMessage());
				throw e;
			}
			LOGGER.info("Connected to Drive: {}, {}.", domain, dd.getGservice()
					.getEmailAddress());

			for (int j = 0; j < dd.getAttachs().size(); j++) {
				FileInfo attach = dd.getAttachs().get(j);
				if (attach.getAonType().equals("registry")) {
					byte data[] = DatabaseSync.getFileData(attach.getFileId(),
							domain);

					Vector<String> emails = DatabaseSync.getEmails(
							attach.getFileId(), domain);
					Vector<String> pemails = DatabaseSync.getPersonEmails(
							attach.getFileId(), domain);
					emails.addAll(pemails);
					attach.setEmails(emails);
					attach.setData(data);
				} else if (attach.getAonType().equals("contract")) {
					attach = DBConsults.getDataContractAttach(domain, attach);
					attach = DBConsults.getEmailsContractAttach(domain, attach);
				} else if (attach.getAonType().equals("item")) {
					attach = DBConsults.getDataIattach(domain, attach);
				} else if (attach.getAonType().equals("invoice")) {
					attach = DBConsults.getDataInvoiceAttach(domain, attach);
					attach = DBConsults.getEmailsInvoiceAttach(domain, attach);
				} else if (attach.getAonType().equals("offer")) {
					attach = DBConsults.getDataOfferAttach(domain, attach);
				} else if (attach.getAonType().equals("payroll")) {
					attach = DBConsults.getDataPayrollAttach(domain, attach);
				} else if (attach.getAonType().equals("project")) {
					attach = DBConsults.getDataProjectAttach(domain, attach);
					attach = DBConsults.getEmailsProjectAttach(domain, attach);
				} else if (attach.getAonType().equals("sepe")) {
					attach = DBConsults.getDataSepeAttach(domain, attach);
				}

				if (sync2(drive, attach, domain)) {
					numero++;
				}

				attach.setData(null);

				if (numero >= num)
					return;

			}
		} else if (dd.getGservice().getClientId() == null)
			LOGGER.info("No service account found for domain: '{}'", domain);
		else if (dd.getAttachs() == null || dd.getAttachs().size() == 0)
			LOGGER.info("No documents/files found for domain: '{}'", domain);

	}

	public static DriveData getAttachsSF(String domain) throws SQLException,
			AonConnectionException {
		HashMap<String, String> map = new HashMap<String, String>();
		for (String s : types) {
			try {
				Enum.valueOf(RegistryAttachmentType.class, s);
				map.put("registry", "registry");
			} catch (IllegalArgumentException e) {
				map.put(s, s);
			}
		}

		DriveData dd = new DriveData();
		dd.setDomain(domain);
		dd.setGservice(DatabaseSync.getServiceAccount(domain));
		dd.setAttachs(new Vector<FileInfo>());

		// REGISTRY ATTACHins
		if (map.containsKey("registry")) {
			dd = DatabaseSync.getDomainFiles(domain);
		}

		// CONTRACT ATTACH
		if (map.containsKey("contract")) {
			dd.setAttachs(DBConsults.getContractAttach(domain, dd.getAttachs()));
		}

		// ITEM ATTACH
		if (map.containsKey("item")) {
			dd.setAttachs(DBConsults.getIattach(domain, dd.getAttachs()));
		}

		// INVOICE ATTACH
		if (map.containsKey("invoice")) {
			dd.setAttachs(DBConsults.getInvoiceAttach(domain, dd.getAttachs()));
		}

		// OFFER ATTACH
		if (map.containsKey("offer")) {
			dd.setAttachs(DBConsults.getOfferAttach(domain, dd.getAttachs()));
		}

		// PAYROLL ATTACH
		if (map.containsKey("payroll")) {
			dd.setAttachs(DBConsults.getPayrollAttach(domain, dd.getAttachs()));
		}

		// PROJECT ATTACH
		if (map.containsKey("project")) {
			dd.setAttachs(DBConsults.getProjectAttach(domain, dd.getAttachs()));
		}

		// SEPE ATTACH
		if (map.containsKey("sepe"))
			dd.setAttachs(DBConsults.getSepeAttach(domain, dd.getAttachs()));

		return dd;
	}

	public static void main(String[] args) throws IOException, SQLException,
			AonConnectionException, GeneralSecurityException, NamingException {

		if (!parse(args))
			return;

		DriveUtils.types = types;
		DriveUtils.domains = domains;
		DriveUtils.dryRun = dryRun;

		if (domains == null || domains.length == 0 || domains[0].equals("ALL")) {
			synchronizeSF();
		} else {
			for (String string : domains) {
				synchronizeSF(string);
			}
		}
	}

	private static String types[];
	private static String domains[];
	private static String action;
	private static String values[];
	private static String out;
	private static Integer num;
	private static boolean dryRun;

	private static boolean parse(String args[]) {

		CommandLineParser parser = new PosixParser();
		HelpFormatter helpFormatter = new HelpFormatter();

		Options options = new Options();

		OptionBuilder.isRequired(false);
		OptionBuilder.hasArg(false);
		OptionBuilder.withLongOpt("help");
		OptionBuilder.withDescription("print this help.");
		Option helpOption = OptionBuilder.create('h');

		OptionBuilder.isRequired(false);
		OptionBuilder.hasArg(true);
		OptionBuilder
				.withDescription("specify a type list, consisting of file types separated by commas, e.g., \"LOGO,SIGNATURE,DOCUMENT\"");
		OptionBuilder.withValueSeparator(',');
		OptionBuilder.withLongOpt("types");
		Option typeOption = OptionBuilder.create('t');

		OptionBuilder.isRequired(false);
		OptionBuilder.hasArg(true);
		OptionBuilder
				.withDescription("specify a domain list, consisting of domain names separated by commas, e.g., \"aon.esferalia.net,sig.aonsolutions.org\"");
		OptionBuilder.withValueSeparator(',');
		OptionBuilder.withLongOpt("domains");
		Option domainOption = OptionBuilder.create('d');

		OptionBuilder.isRequired(false);
		OptionBuilder.hasArg(true);
		OptionBuilder
				.withDescription("action, e.g., \"title\" (search by title)");
		OptionBuilder.withLongOpt("action");
		Option actionOption = OptionBuilder.create('a');

		OptionBuilder.isRequired(false);
		OptionBuilder.hasArg(true);
		OptionBuilder
				.withDescription("specify a value list, consisting of values separated by commas, e.g., \"hello,world\" (search documents that contains 'hello' or 'world')");
		OptionBuilder.withValueSeparator(',');
		OptionBuilder.withLongOpt("values");
		Option valueOption = OptionBuilder.create('v');

		OptionBuilder.isRequired(false);
		OptionBuilder.hasArg(false);
		OptionBuilder
				.withDescription("perform a trial run with no changes made");
		OptionBuilder.withLongOpt("dry-run");
		Option dryOption = OptionBuilder.create('n');

		OptionBuilder.isRequired(false);
		OptionBuilder.hasArg(true);
		OptionBuilder.withDescription("synchronize only N documents");
		OptionBuilder.withLongOpt("count");
		Option countOption = OptionBuilder.create('c');

		options.addOption(helpOption);
		options.addOption(domainOption);
		options.addOption(typeOption);
		options.addOption(valueOption);
		options.addOption(actionOption);
		options.addOption(dryOption);
		options.addOption(countOption);

		try {
			CommandLine line = parser.parse(options, args);

			if (line.hasOption(helpOption.getOpt())) {
				helpFormatter.printHelp(DEFAULT_SYNTAX_PREFIX, options, true);
				return false;
			}
			out = "normally";

			num = Integer.valueOf(line.getOptionValue(countOption.getOpt(),
					String.valueOf(Integer.MAX_VALUE)));

			dryRun = line.hasOption(dryOption.getOpt());
			if (dryRun)
				LOGGER.info("DryRun ON: Perform a trial run with no changes made.");

			types = line.getOptionValues(typeOption.getOpt());
			if (types == null)
				types = new String[] {};

			domains = line.getOptionValues(domainOption.getOpt());
			if (domains == null)
				domains = new String[] {};

			values = line.getOptionValues(valueOption.getOpt());
			if (values == null)
				values = new String[] {};

			action = line.getOptionValue(actionOption.getOpt(), "all");


		} catch (ParseException e) {
			System.out.print(e.getMessage());
			helpFormatter.printHelp(DEFAULT_SYNTAX_PREFIX, options, true);
			return false;
		}
		return true;
	}
}
