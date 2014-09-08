package com.code.aon.google.apis.drive;

import static com.code.aon.google.apis.DatabaseSync.getDomains;

import java.io.IOException;
import java.io.InputStream;
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
import org.apache.commons.lang.StringUtils;

import com.code.aon.google.apis.DatabaseSync;
import com.code.aon.google.apis.DriveData;
import com.code.aon.google.apis.DriveUtils;
import com.code.aon.google.apis.FileInfo;
import com.code.aon.google.apis.DriveUtils.CheckSum;
import com.code.aon.google.apis.jooq.DBConsults;
import com.code.aon.pool.AonConnectionException;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;

public class SynchronizeFiles {
	static int numero = 0;
	public static void synchronizeSF() throws IOException, SQLException, AonConnectionException, KeyStoreException, GeneralSecurityException, NamingException {
		Map<String, String> domains=getDomains();//obtiene todos los dominios de la BD
		
		for (String key : domains.keySet()) { // recorre todos los dominios de la BD	
			synchronizeSF(key);
		}
	}
	
	public static void synchronizeSF(String domain) throws SQLException, AonConnectionException, IOException, KeyStoreException, GeneralSecurityException, NamingException{
		//DriveData dd=DatabaseSync.getDomainFiles(domain);
		// get iattachs, get contract attachs,.... y añadir a DriveData
		DriveData dd = getAttachsSF(domain);

		//long max= 100000;

		//if (max >= dd.getGservice().getSize()){
		
		
			// Rattach
			if(dd.getGservice().getClientId()!=null && dd.getAttachs()!=null && dd.getAttachs().size()>0){
				Drive drive = DriveUtils.serviceInitialize(dd.getGservice());
				for(int j=0;j<dd.getAttachs().size();j++){
					FileInfo attach=  dd.getAttachs().get(j);

					if(attach.getAonType().equals("registry")) {
						InputStream i =DatabaseSync.getFileData(attach.getFileId(),domain);
						Vector<String> emails= DatabaseSync.getEmails(attach.getFileId(),domain);
						Vector<String> pemails=DatabaseSync.getPersonEmails(attach.getFileId(),domain);
						emails.addAll(pemails);
						attach.setEmails(emails);
						attach.setData(i);	
					}
					else if( attach.getAonType().equals("contract")){  
						attach=DBConsults.getDataContractAttach(domain, attach);
						attach=DBConsults.getEmailsContractAttach(domain, attach);
					}
					else if(attach.getAonType().equals( "item")){
						attach=DBConsults.getDataIattach(domain, attach);
					}
					else if(attach.getAonType().equals("invoice")){  
						attach=DBConsults.getDataInvoiceAttach(domain, attach);
						attach=DBConsults.getEmailsInvoiceAttach(domain, attach);
					}
					else if(attach.getAonType().equals("offer")){
						attach=DBConsults.getDataOfferAttach(domain, attach);
					}
					else if(attach.getAonType().equals("payroll")){
						attach=DBConsults.getDataPayrollAttach(domain, attach);
					}
					else if(attach.getAonType().equals("project")){
						attach= DBConsults.getDataProjectAttach(domain, attach);
						attach=DBConsults.getEmailsProjectAttach(domain, attach);
					}
					else if(attach.getAonType().equals("sepe")){  
						attach=DBConsults.getDataSepeAttach(domain, attach);	
					}

					if (num == -1)
						DriveUtils.sync2(drive,attach,domain);
					else sync2SF(drive, attach, domain);
					//long size = DriveUtils.totalSize(domain);
					//DBConsults.setDriveSize(domain, size, dd.getGservice().getDomain());
				}		
		}
	}
	
	public static void sync2SF(Drive drive,FileInfo fileInfo,String domain) throws SQLException, AonConnectionException, IOException, NamingException, KeyStoreException, GeneralSecurityException{
		
		if(numero<num && (fileInfo.getAonType().equals("project") || fileInfo.getAonType().equals("offer") || DriveUtils.checkTypes(fileInfo.getType()))){
						
			if(fileInfo.getDriveId()==null){
				DriveUtils.viewFile(fileInfo);
				File file = DriveUtils.principal(drive, domain, fileInfo);
				numero++;
				if(file!=null){
					fileInfo.setDriveId(file.getId());
					DriveUtils.setDriveId(fileInfo,domain);
					
				}
			}
			else{
				
				File fileAux = DriveUtils.getFile(fileInfo.getDriveId());
								
				if(fileInfo.getData()!= null && !fileAux.getMd5Checksum().equals(CheckSum.getMD5Checksum(fileInfo.getData()))){
					
					File file= DriveUtils.updateFile(fileInfo);
					if(file!=null){
						fileInfo.setDriveId(file.getId());
						DriveUtils.setDriveId(fileInfo,domain);
					}
				}
				//else updateDateSync(drive,fileInfo);

			}
		}
		/*
		else{
			if(fileInfo.getDriveId()!=null) updateDateSync(drive,fileInfo);	
		}*/
		
	}
	
	public static DriveData getAttachsSF(String domain) throws SQLException, AonConnectionException{
		HashMap<String, String> map = new HashMap<String, String>();
		for (String s : b) {
			map.put(s, s);
		}
		
		DriveData dd= new DriveData();
		
		dd.setDomain(domain);
		// de momento solo se sincroniza con Rattach
		//REGISTRY ATTACH
		if(map.containsKey("registry") || b[0].equals("all"))dd=DatabaseSync.getDomainFiles(domain);
			
		//CONTRACT ATTACH
		else if(map.containsKey("contract") || b[0].equals("all")) dd.setAttachs(DBConsults.getContractAttach(domain, dd.getAttachs()));
		
		//ITEM ATTACH
		else if(map.containsKey("item") || b[0].equals("all")) dd.setAttachs(DBConsults.getIattach(domain, dd.getAttachs()));
		
		//INVOICE ATTACH
		else if(map.containsKey("invoice") || b[0].equals("all")) dd.setAttachs(DBConsults.getInvoiceAttach(domain, dd.getAttachs()));
		
		//OFFER ATTACH
		else if(map.containsKey("offer") || b[0].equals("all")) dd.setAttachs(DBConsults.getOfferAttach(domain, dd.getAttachs()));
		
		//PAYROLL ATTACH
		else if(map.containsKey("payroll") || b[0].equals("all")) dd.setAttachs(DBConsults.getPayrollAttach(domain, dd.getAttachs()));
		
		//PROJECT ATTACH
		else if(map.containsKey("project") || b[0].equals("all")) dd.setAttachs(DBConsults.getProjectAttach(domain, dd.getAttachs()));
		
		//SEPE ATTACH
		else if(map.containsKey("sepe") || b[0].equals("all")) dd.setAttachs(DBConsults.getSepeAttach(domain, dd.getAttachs()));
		
		return dd;
	}
	
	public static void main(String[] args) throws KeyStoreException, IOException, SQLException, AonConnectionException, GeneralSecurityException, NamingException {
		parse(args);
		DriveUtils.types=types;
		DriveUtils.domains=domains;
		
		if (domains==null || domains.length==0 || domains[0].equals("TODOS")){
			/*if (b[0].equals("all"))
				DriveUtils.synchronize();
			else*/
			synchronizeSF();
		}
		else{
			for (String string : domains) {
				synchronizeSF(string);
			}
		}
	}

	
	private static String types[]={};
	private static String domains[];
	private static String action = "all";
	private static String values[] ;
	private static String out = "normally";
	private static String[] b = {"all"};
	private static Integer num = -1;
	private static void parse(String  args []) {
		CommandLineParser parser = new PosixParser();
		HelpFormatter helpFormatter = new HelpFormatter();
		
		Options options = new Options();
		
		OptionBuilder.isRequired(false);
		OptionBuilder.hasArg(false);
		OptionBuilder.withDescription("imprime esta ayuda.");
		Option helpOption = OptionBuilder.create("help");
		
		OptionBuilder.isRequired(false);
		OptionBuilder.hasArg(true);
		OptionBuilder.withDescription("Tipo de archivo que se quiere tratar. Ej: LOGO,SIGNATURE,DOCUMENT,... ");
		OptionBuilder.withValueSeparator(',');
		Option typeOption = OptionBuilder.create("t");
		
		OptionBuilder.isRequired(false);
		OptionBuilder.hasArg(true);
		OptionBuilder.withDescription("Dominio al que se le quiere aplicar la acción. Ej: xxx.net,... ");
		OptionBuilder.withValueSeparator(',');		
		Option domainOption = OptionBuilder.create("d");
		
		OptionBuilder.isRequired(false);
		OptionBuilder.hasArg(true);
		OptionBuilder.withDescription("Tipo de acción que se va aplicar a la búsqueda o al borrado. Ej: -a title (buscar por título)");
		OptionBuilder.withValueSeparator(',');		
		Option actionOption = OptionBuilder.create("a");
		
		OptionBuilder.isRequired(false);
		OptionBuilder.hasArg(true);
		OptionBuilder.withDescription("Valor que acompaña al tipo de acción a aplicar. Ej: -a title -v hola (Buscar archivos que contenga 'hola' en el título. ");
		OptionBuilder.withValueSeparator(',');		
		Option valueOption = OptionBuilder.create("v");
		
		OptionBuilder.isRequired(false);
		OptionBuilder.hasArg(true);
		OptionBuilder.withDescription("Tipo de salida al aplicar el comando. Ej: normally");
		OptionBuilder.withValueSeparator(',');		
		Option outOption = OptionBuilder.create("o");
		
		OptionBuilder.isRequired(false);
		OptionBuilder.hasArg(true);
		OptionBuilder.withDescription("Tablas attach de la base de datos. Ej: invoice, registry,...");
		OptionBuilder.withValueSeparator(',');		
		Option bOption = OptionBuilder.create("b");

		OptionBuilder.isRequired(false);
		OptionBuilder.hasArg(true);
		OptionBuilder.withDescription("Número de archivos a sincronizar con Google Drive.");
		OptionBuilder.withValueSeparator(',');		
		Option numOption = OptionBuilder.create("n");
		
		options.addOption(helpOption);
		options.addOption(outOption);
		options.addOption(domainOption);
		options.addOption(typeOption);
		options.addOption(valueOption);
		options.addOption(actionOption);
		options.addOption(bOption);
		options.addOption(numOption);

		
		try {
			CommandLine line = parser.parse(options, args);
			
			String[] typesaux  = line.getOptionValues("t");
			if(typesaux!=null){ types = typesaux;}
			String[] domainsaux = line.getOptionValues("d");
			if(domainsaux!=null){ domains = domainsaux;}
			String actionaux = line.getOptionValue("a");
			if(actionaux!=null){ action = actionaux;}
			String[] valuesaux= line.getOptionValues("v");
			if(valuesaux!=null){ values = valuesaux;}
			String outaux = line.getOptionValue("o");
			if(outaux!=null) out = outaux; 
			String[] baux = line.getOptionValues("b");
			if(baux!=null) b = baux;
			String numaux = line.getOptionValue("n");
			if(numaux!=null) num = Integer.parseInt(numaux);
		} catch (ParseException e) {
			helpFormatter.printHelp(HelpFormatter.DEFAULT_SYNTAX_PREFIX,
					options, true);
		}
	}
}
