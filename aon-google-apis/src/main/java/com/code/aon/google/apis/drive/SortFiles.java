package com.code.aon.google.apis.drive;

import static org.apache.commons.cli.HelpFormatter.DEFAULT_SYNTAX_PREFIX;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Arrays;
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
import com.code.aon.google.apis.FileInfo;
import com.code.aon.google.apis.jooq.DBConsults;
import com.code.aon.google.apis.jooq.DBDrive;
import com.code.aon.google.apis.jooq.DBSync;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.DomainGserviceaccount;
import com.esferalia.aon.occam.api.model.attachment.AttachType;
import com.esferalia.aon.occam.api.model.security.User;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.About;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.google.api.services.drive.model.ParentReference;
import com.google.api.services.drive.model.Property;

public class SortFiles {
	
	private static String domain;
	private static String login;
	
	public static String getLogin(){
		return login;
	}

	public static User getUser(){
		return new User().setLogin(getLogin());
	}
	
	private static File createFolder(Drive drive, String title, String parent) throws IOException{
		File folder = new File();
		folder.setParents(Arrays.asList(new ParentReference().setId(parent)));
		folder.setTitle(title);
		folder.setMimeType("application/vnd.google-apps.folder");
		folder = drive.files().insert(folder).execute();
		return folder;
	}
	
	public static void main(String[] args) throws IOException, GeneralSecurityException {
		parse(args);
		
		if (!domain.equals("")) {
			Map<String, Integer> domainMap = DBSync.getDomainMap();
			Domain domainAux = AON.getDomain(domain, domainMap.get(domain), getUser().getLogin());
			DomainGserviceaccount g = DBConsults.getServiceAccount(domainAux, getUser());
			Drive drive = DriveUtils.serviceInitialize(g);

			FileList domainFolders = SearchFiles.searchFilesTitleEqual(drive,
					domain);
			File domainFolder;
			if (domainFolders.getItems().size() > 0) {
				domainFolder = domainFolders.getItems().get(0);
			} else {
				About about = drive.about().get().execute();
				String rootId = about.getRootFolderId();
				domainFolder = createFolder(drive, domain, rootId);

				Vector<FileInfo> attachs = new Vector<FileInfo>();
				attachs.addAll(DBDrive.getDriveAttach(domainAux, getUser(), AttachType.CONTRACT));
				attachs.addAll(DBDrive.getDriveAttach(domainAux, getUser(), AttachType.ITEM));
				attachs.addAll(DBDrive.getDriveAttach(domainAux, getUser(), AttachType.INVOICE));
				attachs.addAll(DBDrive.getDriveAttach(domainAux, getUser(), AttachType.OFFER));
				attachs.addAll(DBDrive.getDriveAttach(domainAux, getUser(), AttachType.PAYROLL));
				attachs.addAll(DBDrive.getDriveAttach(domainAux, getUser(), AttachType.PROJECT));
				attachs.addAll(DBDrive.getDriveAttach(domainAux, getUser(), AttachType.REGISTRY));
				attachs.addAll(DBDrive.getDriveAttach(domainAux, getUser(), AttachType.SEPE));
				
				for (FileInfo fileInfo : attachs) {

					FileList typeFolders = SearchFiles
							.searchFilesTitleAndParent(drive,
									fileInfo.getAonType(), domainFolder.getId());
					File typeFolder;
					if (typeFolders.getItems().size() > 0) {
						typeFolder = typeFolders.getItems().get(0);
					} else
						typeFolder = createFolder(drive, fileInfo.getAonType(),
								domainFolder.getId());

					File file = DriveUtils.getFile(drive, domainAux, getUser(), fileInfo);
					for (ParentReference parent : file.getParents()) {
						drive.parents().delete(file.getId(), parent.getId())
								.execute();
					}
					drive.parents()
							.insert(file.getId(),
									new ParentReference().setId(typeFolder
											.getId())).execute();
					Property property = new Property();
					property.setKey("domain");
					property.setValue(domain);
					drive.properties().update(file.getId(), "domain",property);
				}
			}
		}
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
		
		OptionBuilder.isRequired(true);
		OptionBuilder.hasArg(true);
		OptionBuilder
				.withDescription("name of domain.");
		OptionBuilder.withLongOpt("d");
		Option domainOption = OptionBuilder.create('d');

		options.addOption(helpOption);				
		options.addOption(domainOption);
		options.addOption(loginOption);

		try {
			CommandLine line = parser.parse(options, args);

			if (line.hasOption(helpOption.getOpt())) {
				helpFormatter.printHelp(DEFAULT_SYNTAX_PREFIX, options, true);
				return false;
			}

			domain = line.getOptionValue(domainOption.getOpt(), "");

			login = line.getOptionValue(loginOption.getOpt());


		} catch (ParseException e) {
			System.out.print(e.getMessage());
			helpFormatter.printHelp(DEFAULT_SYNTAX_PREFIX, options, true);
			return false;
		}
		return true;
	}
}
