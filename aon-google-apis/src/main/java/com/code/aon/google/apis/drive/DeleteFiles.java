package com.code.aon.google.apis.drive;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.KeyStoreException;
import java.sql.SQLException;
import java.util.Hashtable;
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

import com.code.aon.google.apis.DatabaseSync;
import com.code.aon.google.apis.DriveUtils;
import com.code.aon.google.apis.Utils;
import com.code.aon.google.apis.jooq.DBConsults;
import com.code.aon.pool.AonConnectionException;
import com.esferalia.aon.google.sql.AbstractSQL.DomainGserviceaccount;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.google.api.services.drive.model.Property;

public class DeleteFiles {

	private static void deleteDriveIds(File f, String domain)
			throws SQLException, AonConnectionException {
		String aonType = null;
		if(f.getProperties() != null){
			for (Property property : f.getProperties()) {
				if(property.getKey().equals("aontype"))
					aonType = property.getValue();
			}
			if (aonType.equals("registry")) DatabaseSync.delDriveId(f.getId(), domain, id);
			if (aonType.equals("contract")) DBConsults.deleteDriveIdContractAttach(domain, f.getId(), id);
			if (aonType.equals("item")) DBConsults.deleteDriveIdIattach(domain, f.getId(), id);
			if (aonType.equals("invoice")) DBConsults.deleteDriveIdInvoiceAttach(domain, f.getId(), id);
			if (aonType.equals("offer")) DBConsults.deleteDriveIdOfferAttach(domain, f.getId(), id);
			if (aonType.equals("payroll")) DBConsults.deleteDriveIdPayrollAttach(domain, f.getId(), id);
			if (aonType.equals("project")) DBConsults.deleteDriveIdProjectAttach(domain, f.getId(), id);
			if (aonType.equals("sepe")) DBConsults.deleteDriveIdSepeAttach(domain, f.getId(), id);
		}
	}
	
	private static void deleteFileBD(File f, String domain)
			throws SQLException, AonConnectionException {
		String aonType = null;
		if(f.getProperties() != null){
			for (Property property : f.getProperties()) {
				if(property.getKey().equals("aontype"))
					aonType = property.getValue();
			}
			if (aonType.equals("registry")){
				DBConsults.deleteRAttachTags(domain, f.getId(), id);
				DBConsults.deleteFileRAttach(domain, f.getId(), id);
			}
			if (aonType.equals("contract")) DBConsults.deleteFileContractAttach(domain, f.getId(), id);
			if (aonType.equals("item")) DBConsults.deleteFileIAttach(domain, f.getId(), id);
			if (aonType.equals("invoice")) DBConsults.deleteFileInvoiceAttach(domain, f.getId(), id);
			if (aonType.equals("offer")) DBConsults.deleteFileOfferAttach(domain, f.getId(), id);
			if (aonType.equals("payroll")) DBConsults.deleteFilePayrollAttach(domain, f.getId(), id);
			if (aonType.equals("project")) DBConsults.deleteFileProjectAttach(domain, f.getId(), id);
			if (aonType.equals("sepe")) DBConsults.deleteFileSepeAttach(domain, f.getId(), id);
		}
	}
		
	private static void insertBlobs(byte[] data, String driveId, String domain)
			throws SQLException, AonConnectionException {
		DBConsults.insertBlobRAttach(data, domain, driveId);
		DBConsults.insertBlobContractAttach(data, domain, driveId);
		DBConsults.insertBlobIAttach(data, domain, driveId);
		DBConsults.insertBlobInvoiceAttach(data, domain, driveId);
		DBConsults.insertBlobOfferAttach(data, domain, driveId);
		DBConsults.insertBlobPayrollAttach(data, domain, driveId);
		DBConsults.insertBlobProjectAttach(data, domain, driveId);
		DBConsults.insertBlobSepeAttach(data, domain, driveId);
	}

	public static void deleteFile(Drive drive, File f, String domain)
			throws IOException, SQLException, AonConnectionException {
		if(remove.equals("force")){
			if(!f.getMimeType().equals("application/vnd.google-apps.folder")){
				deleteFileBD(f, domain);
			}
			drive.files().delete(f.getId()).execute();
		}
		else{
			if(!f.getMimeType().equals("application/vnd.google-apps.folder")){
				InputStream data = DriveUtils.downloadFile(drive, f);
				insertBlobs(Utils.InputStreamToByte(data), f.getId(), domain);
			}
			drive.files().delete(f.getId()).execute();
		
			if(!f.getMimeType().equals("application/vnd.google-apps.folder")) deleteDriveIds(f, domain);
			
		}
		View.delete(f);
	}

	public static void deleteFilesId(Drive drive, String domain)
			throws IOException, SQLException, AonConnectionException {

		if (values.length == 0) {
			View.error3();
		} else {
			for (String fileId : values) {
				File f = SearchFiles.searchFile(drive, fileId);
				deleteFile(drive, f, domain);
			}
		}

	}

	public static void deleteFilesAll(Drive drive, String domain)
			throws IOException, SQLException, AonConnectionException {
		SearchFiles.types = types;
		FileList fl = SearchFiles.searchFilesAllAndTypes(drive);
		if (fl.getItems() != null){
			for (File f : fl.getItems()) {
				deleteFile(drive, f, domain);
			}
		}
	}

	public static void act(String domain) throws KeyStoreException,
			IOException, GeneralSecurityException, SQLException,
			AonConnectionException {
		DomainGserviceaccount d = DatabaseSync.getServiceAccount(domain);
		if (d.getClientId() != null) {
			Drive drive = DriveUtils.serviceInitialize(d);
			View.domain(domain);

			if (action.equals("all")) {
				deleteFilesAll(drive, domain);
			} else if (action.equals("id")) {
				deleteFilesId(drive, domain);
			} else {
				View.error2();
			}
		}
	}

	public static void main(String[] args) throws SQLException,
			KeyStoreException, IOException, GeneralSecurityException,
			AonConnectionException {
		parse(args);
		System.out.println(domains[0]);
		if (domains[0].equals("all")) {
			Map<String, String> domains1=DatabaseSync.getDomains();
			Hashtable<String,String> schemas = new Hashtable<String, String>();
			
			for (String key : domains1.keySet()) { // recorre todos los dominios de la BD	
				if (!SearchFiles.esta(schemas,domains1.get(key))){
					schemas.put(domains1.get(key), key);
				}
			}
			Vector<String> domains2 = new Vector<String>();
			for (String sch : schemas.keySet()){
				domains2.addAll(DBConsults.getParentName(schemas.get(sch)));
			}
			
			for (String key : domains2) { // recorre todos los dominios de la BD	
				act(key);
			}
		} else {
			for (String domain : domains) {
				act(domain);
			}
		}

	}

	private static String types[] = { "all" };
	private static String domains[] = { "all" };
	private static String action = "all";
	private static String values[];
	private static String out = "normally";
	private static String remove = "normally";
	private static Integer id;
 
	private static void parse(String args[]) {
		CommandLineParser parser = new PosixParser();
		HelpFormatter helpFormatter = new HelpFormatter();

		Options options = new Options();

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
				.withDescription("Tipo de salida al aplicar el comando. Ej: normally");
		OptionBuilder.withValueSeparator(',');
		Option outOption = OptionBuilder.create("o");
		
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

		options.addOption(helpOption);
		options.addOption(outOption);
		options.addOption(domainOption);
		options.addOption(typeOption);
		options.addOption(valueOption);
		options.addOption(actionOption);
		options.addOption(removeOption);
		options.addOption(idOption);

		try {
			CommandLine line = parser.parse(options, args);
					
			String[] typesaux = line.getOptionValues("t");
			if (typesaux != null) {
				types = typesaux;
			}
			String[] domainsaux = line.getOptionValues("d");
			System.out.println(domainsaux);
			if (domainsaux != null) {
				domains = domainsaux;
			}
			String actionaux = line.getOptionValue("a");
			if (actionaux != null) {
				action = actionaux;
			}
			String[] valuesaux = line.getOptionValues("v");
			if (valuesaux != null) {
				values = valuesaux;
			}
			String outaux = line.getOptionValue("o");
			if (outaux != null)
				out = outaux;
			
			String removeaux = line.getOptionValue("r");
			if (removeaux != null)
				remove = removeaux;
			
			String idaux = line.getOptionValue("id");
			if (idaux != null)
				id = Integer.parseInt(idaux);
			
		} catch (ParseException e) {
			helpFormatter.printHelp(HelpFormatter.DEFAULT_SYNTAX_PREFIX,
					options, true);
		}
	}
}
