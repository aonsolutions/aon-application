package com.code.aon.google.apis.drive;

import static org.apache.commons.cli.HelpFormatter.DEFAULT_SYNTAX_PREFIX;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyStoreException;
import java.sql.SQLException;
import java.util.Arrays;
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
import com.code.aon.google.apis.jooq.DomainGserviceaccount;
import com.esferalia.aon.google.sql.AbstractSQL.Domain;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.About;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.google.api.services.drive.model.ParentReference;
import com.google.api.services.drive.model.Property;

public class SortFiles {
	
	private static String domain;

	private static File createFolder(Drive drive, String title, String parent) throws IOException{
		File folder = new File();
		folder.setParents(Arrays.asList(new ParentReference().setId(parent)));
		folder.setTitle(title);
		folder.setMimeType("application/vnd.google-apps.folder");
		folder = drive.files().insert(folder).execute();
		return folder;
	}

	public static void main(String[] args) throws KeyStoreException,
			IOException, GeneralSecurityException, SQLException {
		parse(args);
		if (!domain.equals("")) {
			Domain d = DBConsults.getDomain(domain);
			DomainGserviceaccount g = DBConsults.getServiceAccount(domain,
					d.getId());
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
				attachs.addAll(DBDrive.getDriveContractAttach(domain, d.getId()));
				attachs.addAll(DBDrive.getDriveIAttach(domain, d.getId()));
				attachs.addAll(DBDrive.getDriveInvoiceAttach(domain, d.getId()));
				attachs.addAll(DBDrive.getDriveOfferAttach(domain, d.getId()));
				attachs.addAll(DBDrive.getDrivePayrollAttach(domain, d.getId()));
				attachs.addAll(DBDrive.getDriveProjectAttach(domain, d.getId()));
				attachs.addAll(DBDrive.getDriveRAttach(domain, d.getId()));
				attachs.addAll(DBDrive.getDriveSepeAttach(domain, d.getId()));
				
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

					File file = DriveUtils.getFile(drive,
							fileInfo.getDriveId(), fileInfo.getFileId());
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


		try {
			CommandLine line = parser.parse(options, args);

			if (line.hasOption(helpOption.getOpt())) {
				helpFormatter.printHelp(DEFAULT_SYNTAX_PREFIX, options, true);
				return false;
			}

			domain = line.getOptionValue(domainOption.getOpt(), "");

			


		} catch (ParseException e) {
			System.out.print(e.getMessage());
			helpFormatter.printHelp(DEFAULT_SYNTAX_PREFIX, options, true);
			return false;
		}
		return true;
	}
}
