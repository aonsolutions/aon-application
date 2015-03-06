package com.code.aon.google.apis.drive;

import static com.code.aon.google.apis.DatabaseSync.getDomains;
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

import com.code.aon.google.apis.DriveUtils;
import com.code.aon.google.apis.FileInfo;
import com.code.aon.google.apis.jooq.DBConsults;
import com.code.aon.google.apis.jooq.DBDrive;
import com.code.aon.google.apis.jooq.DomainGserviceaccount;
import com.code.aon.pool.AonConnectionException;
import com.code.aon.registry.enumeration.RegistryAttachmentType;
import com.esferalia.aon.google.sql.AbstractSQL.Domain;
import com.google.api.services.drive.Drive;

public class SynchronizeFiles2 {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(SynchronizeFiles2.class.getName());

	static int numero = 0;

	private static void synchronizeSF(String domain) throws SQLException, IOException, GeneralSecurityException{
		Domain d = DBConsults.getDomain(domain);
		DomainGserviceaccount g = DBConsults.getServiceAccount(domain, d.getId());
		if(g.getClientId() == null)
			LOGGER.info("No service account found for domain: '{}'", domain);
		else{
			Drive drive = null;
			try {
				drive = DriveUtils.serviceInitialize(g);
			} catch (IOException e1) {
				LOGGER.error("I/O Error connecting to Drive: {}",
						e1.getMessage());
				throw e1;
			} catch (GeneralSecurityException e1) {
				LOGGER.error("Security Error connecting to Drive: {}",
						e1.getMessage());
				throw e1;			
			}
			LOGGER.info("Connected to Drive: {}, {}.", domain,g.getEmailAddress());
			
			HashMap<String, String> map = new HashMap<String, String>();
			Vector<RegistryAttachmentType> rats = new Vector<RegistryAttachmentType>();
			for (String s : types) {
				try {
					RegistryAttachmentType rat = Enum.valueOf(RegistryAttachmentType.class, s);
					rats.add(rat);
					map.put("registry", "registry");
				} catch (IllegalArgumentException e) {
					map.put(s, s);
				}
			}
		
			if (map.containsKey("registry")) {
				Integer size = 10;
				while(size == 10){
					Vector<FileInfo> v = DBDrive.getRAttachLimit(d.getName(), d.getId(),rats);
					System.out.println(v.size());
					sync(drive, d.getName(), v);
					if (numero >= num) return;
					size = v.size();
				}
			}
			if (map.containsKey("contract")){
				Integer size = 10;
				while(size == 10){
					Vector<FileInfo> v = DBDrive.getContractAttachLimit(d.getName(), d.getId());
					sync(drive, d.getName(), v);
					if (numero >= num) return;
					size = v.size();
				}
			}
			if (map.containsKey("item")){
				Integer size = 10;
				while(size == 10){
					Vector<FileInfo> v = DBDrive.getIAttachLimit(d.getName(), d.getId());
					sync(drive, d.getName(), v);
					if (numero >= num) return;
					size = v.size();
				}
			}
			if (map.containsKey("invoice")){
				Integer size = 10;
				while(size == 10){
					Vector<FileInfo> v = DBDrive.getInvoiceAttachLimit(d.getName(), d.getId());
					sync(drive, d.getName(), v);
					if (numero >= num) return;
					size = v.size();
				}
			}
			if (map.containsKey("offer")){
				Integer size = 10;
				while(size == 10){
					Vector<FileInfo> v = DBDrive.getOfferAttachLimit(d.getName(), d.getId());
					sync(drive, d.getName(), v);
					if (numero >= num) return;
					size = v.size();
				}
			}	
			if (map.containsKey("payroll")){
				Integer size = 10;
				while(size == 10){
					Vector<FileInfo> v = DBDrive.getPayrollAttachLimit(d.getName(), d.getId());
					sync(drive, d.getName(), v);
					if (numero >= num) return;
					size = v.size();
				}
			}
			if (map.containsKey("project")){
				Integer size = 10;
				while(size == 10){
					Vector<FileInfo> v = DBDrive.getProjectAttachLimit(d.getName(), d.getId());
					sync(drive, d.getName(), v);
					if (numero >= num) return;
					size = v.size();
				}
			}
			if (map.containsKey("sepe")){
				Integer size = 10;
				while(size == 10){
					Vector<FileInfo> v = DBDrive.getSepeAttachLimit(d.getName(), d.getId());
					sync(drive, d.getName(), v);
					if (numero >= num) return;
					size = v.size();
				}
			}
			if(numero == 0){
				LOGGER.info("No documents/files found for domain: '{}'", domain);
			}
		}
	}

	public static void sync(Drive drive,String domain,Vector<FileInfo> vector){
		vector.stream().forEach(f->{
			try {
				if (DriveUtils.sync2(drive, f, domain)) {
					numero++;
				}
				if (numero >= num) return;
			} catch (Exception e) {e.printStackTrace();}
		});
	}
	
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
	
	public static void main(String[] args) throws IOException, SQLException,
			AonConnectionException, GeneralSecurityException, NamingException {

		if (!parse(args))
			return;
		if(types[0].equals("all")){
			types[0] = "registry";
			types[1] = "contract";
			types[2] = "item";
			types[3] = "invoice";
			types[4] = "offer";
			types[5] = "payroll";
			types[6] = "project";
			types[7] = "sepe";
		}
		
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
		options.addOption(dryOption);
		options.addOption(countOption);

		try {
			CommandLine line = parser.parse(options, args);

			if (line.hasOption(helpOption.getOpt())) {
				helpFormatter.printHelp(DEFAULT_SYNTAX_PREFIX, options, true);
				return false;
			}

			num = Integer.valueOf(line.getOptionValue(countOption.getOpt(),
					String.valueOf(Integer.MAX_VALUE)));

			dryRun = line.hasOption(dryOption.getOpt());
			if (dryRun)
				LOGGER.info("DryRun ON: Perform a trial run with no changes made.");

			types = line.getOptionValues(typeOption.getOpt());
			if (types == null)
				types = new String[] {"all"};

			domains = line.getOptionValues(domainOption.getOpt());
			if (domains == null)
				domains = new String[] {};

		} catch (ParseException e) {
			System.out.print(e.getMessage());
			helpFormatter.printHelp(DEFAULT_SYNTAX_PREFIX, options, true);
			return false;
		}
		return true;
	}
}
