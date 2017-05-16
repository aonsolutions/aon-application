package com.code.aon.google.apis.drive;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.Collections;
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

import com.code.aon.google.apis.DriveUtils;
import com.code.aon.google.apis.jooq.DBConsults;
import com.code.aon.google.apis.jooq.DBSync;
import com.code.aon.pool.AonConnectionException;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.DomainGserviceaccount;
import com.esferalia.aon.occam.api.model.attachment.AttachType;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.watson.server.io.AonIOUtils;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.google.api.services.drive.model.Property;

public class DeleteFiles {
	
	private static void deleteDriveIds(File f, Domain domain, Integer id){
		String aonType = null;
		if(f.getProperties() != null){
			for (Property property : f.getProperties()) {
				if(property.getKey().equals("aontype"))
					aonType = property.getValue();
			}
			
			if (aonType.equals("registry")) DBConsults.deleteDriveIdRegistryAttach(domain, f.getId(), id);
			if (aonType.equals("contract")) DBConsults.deleteDriveIdContractAttach(domain, f.getId(), id);
			if (aonType.equals("item")) DBConsults.deleteDriveIdIattach(domain, f.getId(), id);
			if (aonType.equals("invoice")) DBConsults.deleteDriveIdInvoiceAttach(domain, f.getId(), id);
			if (aonType.equals("offer")) DBConsults.deleteDriveIdOfferAttach(domain, f.getId(), id);
			if (aonType.equals("payroll")) DBConsults.deleteDriveIdPayrollAttach(domain, f.getId(), id);
			if (aonType.equals("project")) DBConsults.deleteDriveIdProjectAttach(domain, f.getId(), id);
			if (aonType.equals("sepe")) DBConsults.deleteDriveIdSepeAttach(domain, f.getId(), id);
		}
	}
	
	private static void deleteFileBD(Domain domain, File f, Integer id){
		String aonType = null;
		
		if(f.getProperties() != null){
			for (Property property : f.getProperties()) {
				if(property.getKey().equals("aontype"))
					aonType = property.getValue();
			}
			if (aonType.equals("registry")){
				String driveid = DBConsults.getAttachDriveId(domain, getUser(), AttachType.REGISTRY, id);
				if(driveid.equals(f.getId())){
					DBConsults.deleteRegistryAttachTags(domain, getUser(), id);
					DBConsults.deleteFileAttach(domain, getUser(), AttachType.REGISTRY, id);
				}
			}
			if (aonType.equals("contract")) {
				String driveid = DBConsults.getAttachDriveId(domain, getUser(), AttachType.CONTRACT, id);
				if(driveid.equals(f.getId())){
					DBConsults.deleteFileAttach(domain, getUser(), AttachType.CONTRACT, id);
				}
			}
			if (aonType.equals("item")) {
				String driveid = DBConsults.getAttachDriveId(domain, getUser(), AttachType.ITEM, id);
				if(driveid.equals(f.getId())){
					DBConsults.deleteFileAttach(domain, getUser(), AttachType.ITEM, id);
				}
			}
			if (aonType.equals("invoice")) {
				String driveid = DBConsults.getAttachDriveId(domain, getUser(), AttachType.INVOICE, id);
				if(driveid.equals(f.getId())){
					DBConsults.deleteFileAttach(domain, getUser(), AttachType.INVOICE, id);
				}
			}
			if (aonType.equals("offer")) {
				String driveid = DBConsults.getAttachDriveId(domain, getUser(), AttachType.OFFER, id);
				if(driveid.equals(f.getId())){
					DBConsults.deleteFileAttach(domain, getUser(), AttachType.OFFER, id);
				}
			}
			if (aonType.equals("payroll")) {
				String driveid = DBConsults.getAttachDriveId(domain, getUser(), AttachType.PAYROLL, id);
				if(driveid.equals(f.getId())){
					DBConsults.deleteFileAttach(domain, getUser(), AttachType.PAYROLL, id);
				}
			}
			if (aonType.equals("project")) {
				String driveid = DBConsults.getAttachDriveId(domain, getUser(), AttachType.PROJECT, id);
				if(driveid.equals(f.getId())){
					DBConsults.deleteFileAttach(domain, getUser(), AttachType.PROJECT, id);
				}
			}
			if (aonType.equals("sepe")) {
				String driveid = DBConsults.getAttachDriveId(domain, getUser(), AttachType.SEPE, id);
				if(driveid.equals(f.getId())){
					DBConsults.deleteFileAttach(domain, getUser(), AttachType.SEPE, id);
				}
			}
		}
	}
		
	private static void insertBlobs(byte[] data, String driveId, Domain domain, Integer id, String aonType){
		if(aonType.equals("registry")) DBConsults.updateAttachData(domain, getUser(), AttachType.REGISTRY, id, data);
		if(aonType.equals("contract")) DBConsults.updateAttachData(domain, getUser(), AttachType.CONTRACT, id, data);
		if(aonType.equals("invoice")) DBConsults.updateAttachData(domain, getUser(), AttachType.INVOICE, id, data);
		if(aonType.equals("item")) DBConsults.updateAttachData(domain, getUser(), AttachType.ITEM, id, data);
		if(aonType.equals("offer")) DBConsults.updateAttachData(domain, getUser(), AttachType.OFFER, id, data);
		if(aonType.equals("payroll")) DBConsults.updateAttachData(domain, getUser(), AttachType.PAYROLL, id, data);
		if(aonType.equals("project")) DBConsults.updateAttachData(domain, getUser(), AttachType.PROJECT, id, data);
		if(aonType.equals("sepe")) DBConsults.updateAttachData(domain, getUser(), AttachType.SEPE, id, data);
	}

	public static void deleteFile(Drive drive, File f, Domain domain) throws IOException{
		Integer idAux = -1;
		String aonType = null;
		if(f.getProperties() != null){
			for (Property property : f.getProperties()) {
				if(property.getKey().equals("fileId")){
					String fileId = property.getValue();
					idAux = Integer.parseInt(fileId);
				}
				if(property.getKey().equals("aontype")){
					System.out.println(property.getValue());
					aonType = property.getValue();
				}
			}
		}
	 
		if(idAux != -1 && aonType != null){
			if(remove.equals("force")){
				if(!f.getMimeType().equals("application/vnd.google-apps.folder")){
					deleteFileBD(domain, f, idAux);
				}
				drive.files().delete(f.getId()).execute();
			}
			else{
				if(!f.getMimeType().equals("application/vnd.google-apps.folder")){
					InputStream data = DriveUtils.downloadFile(drive, f);
					insertBlobs(AonIOUtils.toByteArray(data), f.getId(), domain, idAux, aonType);
				}
				drive.files().delete(f.getId()).execute();
		
				if(!f.getMimeType().equals("application/vnd.google-apps.folder")) 
					deleteDriveIds(f, domain, idAux);
			}
			View.delete(f);
		}
		else View.error5();
	}

	public static void deleteFilesId(Drive drive, Domain domain) throws IOException {

		if (values.length == 0) {
			View.error3();
		} else {
			for (String fileId : values) {
				File f = SearchFiles.searchFile(drive, fileId);
				deleteFile(drive, f, domain);
			}
		}

	}
	
	public static void deleteFileId(Domain domain, Integer id) throws IOException {
		DomainGserviceaccount g = DBConsults.getServiceAccount(domain, getUser());
		if (g.getClientId() != null) {
			Drive drive = DriveUtils.serviceInitialize(g);
			FileList fl = SearchFiles.searchFilesProperties(drive, "fileId", id.toString());
			File f = fl.getItems().get(0);
			deleteFile(drive, f, domain);
		}
	}

	public static void deleteFilesAll(Drive drive, Domain domain) throws IOException {
		SearchFiles.types = types;
		FileList fl = SearchFiles.searchFilesAllAndTypes(drive, domain.getName());
		if (fl.getItems() != null){
			for (File f : fl.getItems()) {
				deleteFile(drive, f, domain);
			}
		}
	}

	public static void act(Domain domain) throws IOException, GeneralSecurityException, AonConnectionException {
		DomainGserviceaccount g = DBConsults.getServiceAccount(domain, getUser());
		if (g.getClientId() != null) {
			Drive drive = DriveUtils.serviceInitialize(g);
			View.domain(domain.getName());
			if (action.equals("all")) {
				deleteFilesAll(drive, domain);
			} else if (action.equals("id")) {
				deleteFilesId(drive, domain);
			} else {
				View.error2();
			}
			
		}
	}

	public static void main(String[] args) throws  IOException, GeneralSecurityException, AonConnectionException {
		parse(args);
		Map<String, Integer> domainMap = DBSync.getDomainMap();
		
		if(id != -1){
			Map<String, String> domains1=DBSync.getDomains();
			Vector<String> v = new Vector<String>(domains1.keySet());
			Domain domain = AON.getDomain(v.get(0), domainMap.get(v.get(0)), getUser().getLogin());
			deleteFileId(domain,id);
		}
		else if (domains[0].equals("all")) {
			Map<String, String> domains = DBSync.getDomains();
			
			// Ordena los dominios por orden alfabetico.
			LinkedList<String> list = new LinkedList<String>(domains.keySet());
			Collections.sort(list, (String s1, String s2) -> s1.compareTo(s2));
			
			// Recorre todos los dominios de la BD.
			for (String domainName : list){
				Domain domain = AON.getDomain(domainName, domainMap.get(domainName), getLogin());
				act(domain);
			}
		} else {
			for (String domainName : domains) {
				Domain domain = AON.getDomain(domainName, domainMap.get(domainName), getLogin());
				act(domain);
			}
		}

	}

	private static String types[] = { "all" };
	private static String domains[] = { "all" };
	private static String action = "all";
	private static String values[];
	private static String remove = "normally";
	private static Integer id = -1;
	private static String login;
	
	public static String getLogin(){
		return login;
	}
	
	public static User getUser(){
		return new User().setLogin(getLogin());
	}
 
	private static void parse(String args[]) {
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
		OptionBuilder.withDescription("imprime esta ayuda.");
		Option helpOption = OptionBuilder.create("help");

		OptionBuilder.isRequired(false);
		OptionBuilder.hasArg(true);
		OptionBuilder
				.withDescription("Tipo de archivo que se quiere tratar. Ej: LOGO,SIGNATURE,DOCUMENT,... ");
		OptionBuilder.withValueSeparator(',');
		Option typeOption = OptionBuilder.create("t");

		OptionBuilder.isRequired(false);
		OptionBuilder.hasArg(true);
		OptionBuilder
				.withDescription("Dominio al que se le quiere aplicar la acción. Ej: xxx.net,... ");
		OptionBuilder.withValueSeparator(',');
		Option domainOption = OptionBuilder.create("d");

		OptionBuilder.isRequired(false);
		OptionBuilder.hasArg(true);
		OptionBuilder
				.withDescription("Tipo de acción que se va aplicar a la búsqueda o al borrado. Ej: -a title (buscar por título)");
		OptionBuilder.withValueSeparator(',');
		Option actionOption = OptionBuilder.create("a");

		OptionBuilder.isRequired(false);
		OptionBuilder.hasArg(true);
		OptionBuilder
				.withDescription("Valor que acompaña al tipo de acción a aplicar. Ej: -a title -v hola (Buscar archivos que contenga 'hola' en el título. ");
		OptionBuilder.withValueSeparator(',');
		Option valueOption = OptionBuilder.create("v");
		
		OptionBuilder.isRequired(false);
		OptionBuilder.hasArg(true);
		OptionBuilder
				.withDescription("Borrado");
		OptionBuilder.withValueSeparator(',');
		Option removeOption = OptionBuilder.create("r");

		OptionBuilder.isRequired(false);
		OptionBuilder.hasArg(true);
		OptionBuilder
				.withDescription("id bd");
		OptionBuilder.withValueSeparator(',');
		Option idOption = OptionBuilder.create("id");

		options.addOption(loginOption);
		options.addOption(helpOption);
		options.addOption(domainOption);
		options.addOption(typeOption);
		options.addOption(valueOption);
		options.addOption(actionOption);
		options.addOption(removeOption);
		options.addOption(idOption);

		try {
			CommandLine line = parser.parse(options, args);
			String[] typesaux = line.getOptionValues(typeOption.getOpt());
			if (typesaux != null) {
				types = typesaux;
			}
			String[] domainsaux = line.getOptionValues(domainOption.getOpt());
			System.out.println(domainsaux);
			if (domainsaux != null) {
				domains = domainsaux;
			}
			String actionaux = line.getOptionValue(actionOption.getOpt());
			if (actionaux != null) {
				action = actionaux;
			}
			String[] valuesaux = line.getOptionValues(valueOption.getOpt());
			if (valuesaux != null) {
				values = valuesaux;
			}
			
			String removeaux = line.getOptionValue(removeOption.getOpt());
			if (removeaux != null)
				remove = removeaux;
			
			String idaux = line.getOptionValue(idOption.getOpt());
			if (idaux != null)
				id = Integer.parseInt(idaux);
			
			login = line.getOptionValue(loginOption.getOpt());
			
		} catch (ParseException e) {
			System.out.println(e);
			helpFormatter.printHelp(HelpFormatter.DEFAULT_SYNTAX_PREFIX,
					options, true);
		}
	}
}
