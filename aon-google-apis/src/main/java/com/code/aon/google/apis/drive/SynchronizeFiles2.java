package com.code.aon.google.apis.drive;

import static org.apache.commons.cli.HelpFormatter.DEFAULT_SYNTAX_PREFIX;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Vector;

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
import com.code.aon.google.apis.jooq.DBSync;
import net.aonsolutions.core.pool.AonConnectionException;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.DomainGserviceaccount;
import com.esferalia.aon.occam.api.model.attachment.AttachType;
import com.esferalia.aon.occam.api.model.attachment.ContractAttachmentType;
import com.esferalia.aon.occam.api.model.attachment.InvoiceAttachmentType;
import com.esferalia.aon.occam.api.model.attachment.ItemAttachmentType;
import com.esferalia.aon.occam.api.model.attachment.PayrollBatchAttachmentType;
import com.esferalia.aon.occam.api.model.attachment.ProjectAttachmentType;
import com.esferalia.aon.occam.api.model.attachment.RegistryAttachmentType;
import com.esferalia.aon.occam.api.model.attachment.SepeBatchAttachmentType;
import com.esferalia.aon.occam.api.model.security.User;
import com.google.api.services.drive.Drive;

public class SynchronizeFiles2 {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(SynchronizeFiles2.class.getName());

	static int numero = 0;
	
	private static Drive getDriveConnection(String domainName, Integer domainId){
		DomainGserviceaccount g = AON.getDomainGserviceaccount(domainName, domainId, "");
		Drive drive = null;
		if(g.getClientId() == null){
			LOGGER.info("No service account found ");
		}else{
			drive = DriveUtils.serviceInitialize(g);
			LOGGER.info("Connected to Drive: {}", g.getEmailAddress());
		}
		return drive;
	}
	
	private static void synchronizeSF(Domain domain, Drive drive) throws  IOException, GeneralSecurityException{
		LOGGER.info("domain: {}", domain.getName());
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
			Integer perPage = 10;
			Integer page = 1;
			while(perPage == 10){
				Vector<FileInfo> v = DBDrive.getAttachLimit(domain, getUser(), AttachType.REGISTRY, RegistryAttachmentType.drive(), page, perPage);
				if(v.size() != 0) sync(drive, domain, v);
				if (numero >= num) return;
				perPage = v.size();
				page++;
			}
		}
		if (map.containsKey("contract")){
			Integer perPage = 10;
			Integer page = 1;
			while(perPage == 10){
				Vector<FileInfo> v = DBDrive.getAttachLimit(domain, getUser(), AttachType.CONTRACT, ContractAttachmentType.drive(), page, perPage);
				if(v.size() != 0) sync(drive, domain, v);
				if (numero >= num) return;
				perPage = v.size();
				page++;
			}
		}
		if (map.containsKey("item")){
			Integer perPage = 10;
			Integer page = 1;
			while(perPage == 10){
				Vector<FileInfo> v = DBDrive.getAttachLimit(domain, getUser(), AttachType.ITEM, ItemAttachmentType.drive(), page, perPage);
				if(v.size() != 0) sync(drive, domain, v);
				if (numero >= num) return;
				perPage = v.size();
				page++;
			}
		}
		if (map.containsKey("invoice")){
			Integer perPage = 10;
			Integer page = 1;
			while(perPage == 10){
				Vector<FileInfo> v = DBDrive.getAttachLimit(domain, getUser(), AttachType.INVOICE, InvoiceAttachmentType.drive(),page, perPage);
				if(v.size() != 0) sync(drive, domain, v);
				if (numero >= num) return;
				perPage = v.size();
				page++;
			}
		}
		if (map.containsKey("offer")){
			Integer perPage = 10;
			Integer page = 1;
			while(perPage == 10){
				Vector<FileInfo> v = DBDrive.getAttachLimit(domain, getUser(), AttachType.OFFER, new Byte[]{}, page, perPage);
				if(v.size() != 0) sync(drive, domain, v);
				if (numero >= num) return;
				perPage = v.size();
				page++;
			}
		}	
		if (map.containsKey("payroll")){
			Integer perPage = 10;
			Integer page = 1;
			while(perPage == 10){
				Vector<FileInfo> v = DBDrive.getAttachLimit(domain, getUser(), AttachType.PAYROLL, PayrollBatchAttachmentType.drive(), page, perPage);
				if(v.size() != 0) sync(drive, domain, v);
				if (numero >= num) return;
				perPage = v.size();
				page++;
			}
		}
		if (map.containsKey("project")){
			Integer perPage = 10;
			Integer page = 1;
			while(perPage == 10){
				Vector<FileInfo> v = DBDrive.getAttachLimit(domain, getUser(), AttachType.PROJECT, ProjectAttachmentType.drive(), page, perPage);
				if(v.size() != 0) sync(drive, domain, v);
				if (numero >= num) return;
				perPage = v.size();
				page++;
			}
		}
		if (map.containsKey("sepe")){
			Integer perPage = 10;
			Integer page = 1;
			while(perPage == 10){
				Vector<FileInfo> v = DBDrive.getAttachLimit(domain, getUser(), AttachType.SEPE, SepeBatchAttachmentType.drive(), page, perPage);
				if(v.size() != 0) sync(drive, domain, v);
				if (numero >= num) return;
				perPage = v.size();
				page++;
			}
		}
		if(numero == 0){
			LOGGER.info("No documents/files found for domain: '{}'", domain.getName());
		}
	}

	public static void sync(Drive drive, Domain domain, Vector<FileInfo> vector){
		vector.stream().forEach(f->{
			try {
				if (DriveUtils.sync2(drive, domain, getUser(), f)) {
					numero++;
				}
				if (numero >= num) return;
			} catch (Exception e) {e.printStackTrace();}
		});
	}
	
	public static void main(String[] args) throws AonConnectionException, IOException, GeneralSecurityException{
		if (!parse(args))
			return;
		if(types[0].equals("all")){
			String t[] = new String[8];
			t[0] = "registry";
			t[1] = "contract";
			t[2] = "item";
			t[3] = "invoice";
			t[4] = "offer";
			t[5] = "payroll";
			t[6] = "project";
			t[7] = "sepe";
			types = t;
		}
		
		DriveUtils.types = types;
		DriveUtils.domains = domains;
		DriveUtils.dryRun = dryRun;
		
		Map<String, Integer> domainMap = DBSync.getDomainMap();
		if (domains == null || domains.length == 0 || domains[0].equals("ALL")) {
			for (String schema : DBSync.getSchemas()) {
				String domainName = DBSync.getSchemaFirstDomain(schema);
				if(domainName != null && !domainName.equals("")){
					Drive drive = getDriveConnection(domainName, domainMap.get(domainName));
					if(drive != null){
						LinkedList<Domain> list = DBConsults.getDriveDomainList(domainName, domainMap.get(domainName));
						Collections.sort(list, (Domain s1, Domain s2) -> s1.getName().compareTo(s2.getName()));
						for (Domain domain : list){
							synchronizeSF(domain, drive);
						}
					}
				}
			}
		} else {
			for (String domainName : domains) {
				Drive drive = getDriveConnection(domainName, domainMap.get(domainName));
				if(drive != null){
					Domain domain = AON.getDomain(domainName, domainMap.get(domainName), getLogin()); 
					synchronizeSF(domain, drive);
				}
			}
		}
	}

	private static String types[];
	private static String domains[];
	private static Integer num;
	private static boolean dryRun;
	private static String login; // username
	
	public static String getLogin(){
		return login;
	}
	
	public static User getUser(){
		return new User().setLogin(getLogin());
	}
	
	private static boolean parse(String args[]) {

		CommandLineParser parser = new PosixParser();
		HelpFormatter helpFormatter = new HelpFormatter();

		Options options = new Options();

		OptionBuilder.isRequired(true);
		OptionBuilder.hasArg(true);
		OptionBuilder.withDescription("Username of application");
		OptionBuilder.withLongOpt("username");
		Option loginOption = OptionBuilder.create('u');
		
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

		options.addOption(loginOption);
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
			
			login = line.getOptionValue(loginOption.getOpt());
			
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
