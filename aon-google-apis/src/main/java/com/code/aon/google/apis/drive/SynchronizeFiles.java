package com.code.aon.google.apis.drive;

import static org.apache.commons.cli.HelpFormatter.DEFAULT_SYNTAX_PREFIX;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

import com.code.aon.google.apis.jooq.DBConsults;
import com.code.aon.google.apis.jooq.DBDrive;
import com.code.aon.google.apis.jooq.DBSync;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.DomainGserviceaccount;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.attachment.AttachType;
import com.esferalia.aon.occam.api.model.attachment.ContractAttachmentType;
import com.esferalia.aon.occam.api.model.attachment.DataAttachType;
import com.esferalia.aon.occam.api.model.attachment.InvoiceAttachmentType;
import com.esferalia.aon.occam.api.model.attachment.ItemAttachmentType;
import com.esferalia.aon.occam.api.model.attachment.PayrollBatchAttachmentType;
import com.esferalia.aon.occam.api.model.attachment.ProjectAttachmentType;
import com.esferalia.aon.occam.api.model.attachment.RegistryAttachmentType;
import com.esferalia.aon.occam.api.model.attachment.SepeBatchAttachmentType;
import com.esferalia.aon.occam.api.model.security.User;
import com.google.api.services.drive.Drive;

import net.aonsolutions.aon.google.apis.drive.AonDrive;

public class SynchronizeFiles {

	private static final Logger LOGGER  = Logger.getLogger(SynchronizeFiles.class.getName());

	static int numero = 0;
	
	private static Drive getDriveConnection(String domainName, Integer domainId){
		DomainGserviceaccount g = AON.getDomainGserviceaccount(domainName, domainId, "rpm");
		Drive drive = null;
		if(g.getClientId() == null)
			LOGGER.log(Level.INFO, "No service account found ");
		else{
			drive = AonDrive.getInstace().serviceInitialize(g);
			LOGGER.log(Level.INFO, "Connected to Drive: " + g.getEmailAddress());
		}
		return drive;
	}
	
	private static void synchronizeSF(Domain domain, Drive drive) {
		LOGGER.log(Level.INFO, "domain: "+ domain.getName());
		
		HashMap<String, String> map = new HashMap<String, String>();
		for (String s : types) map.put(s, s);
		
		if (map.containsKey("registry")) sync(drive, domain, AttachType.REGISTRY, RegistryAttachmentType.drive());
		if (map.containsKey("contract")) sync(drive, domain, AttachType.CONTRACT, ContractAttachmentType.drive());
		if (map.containsKey("item")) sync(drive, domain, AttachType.ITEM, ItemAttachmentType.drive());
		if (map.containsKey("invoice")) sync(drive, domain, AttachType.INVOICE, InvoiceAttachmentType.drive()); 
		if (map.containsKey("offer")) sync(drive, domain, AttachType.OFFER, new Byte[]{}); 	
		if (map.containsKey("payroll")) sync(drive, domain, AttachType.PAYROLL, PayrollBatchAttachmentType.drive());
		if (map.containsKey("project")) sync(drive, domain, AttachType.PROJECT, ProjectAttachmentType.drive());
		if (map.containsKey("sepe")) sync(drive, domain, AttachType.SEPE, SepeBatchAttachmentType.drive());
		if (map.containsKey("data")) sync(drive, domain, AttachType.DATA, DataAttachType.drive());
		
		if(numero == 0) LOGGER.log(Level.INFO, "No documents/files found for domain: '"+domain.getName()+"'");
	}

	public static void sync(Drive drive, Domain domain, AttachType attachType, Byte[] types){
		AonDrive aonDrive = AonDrive.getInstace();
		Integer perPage = 10;
		Integer page = 0;
		while(perPage == 10){
			Stream<Attach> v = DBDrive.getAttachStreamLimit(domain, getUser(), attachType, types, page, perPage);
			perPage = new Long(v.count()).intValue();
			page++;
			v.forEach(attach -> {
				aonDrive.sync(drive, getUser(), attach, dryRun);
				numero++;
				if (numero >= num) return;
			});
			if (numero >= num) return;
		}
	}
	
	public static void main(String[] args){
		if (!parse(args))
			return;
		if(types[0].equals("all")){
			String t[] = new String[9];
			t[0] = "registry";
			t[1] = "contract";
			t[2] = "item";
			t[3] = "invoice";
			t[4] = "offer";
			t[5] = "payroll";
			t[6] = "project";
			t[7] = "sepe";
			t[8] = "data";
			types = t;
		}

		Map<String, Integer> domainMap = DBSync.getDomainMap();
		if (domains == null || domains.length == 0 || domains[0].equals("ALL")) {
			DBSync.getSchemas().stream().forEach(schema ->{
				String domainName = DBSync.getSchemaFirstDomain(schema);
				if(domainName != null && !domainName.equals("")){
					Drive drive = getDriveConnection(domainName, domainMap.get(domainName));
					if(drive != null){
						LinkedList<Domain> list = DBConsults.getDriveDomainList(domainName, domainMap.get(domainName));
						Collections.sort(list, (Domain s1, Domain s2) -> s1.getName().compareTo(s2.getName()));
						list.stream().forEach(domain -> synchronizeSF(domain, drive));
					}
				}
			});
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
