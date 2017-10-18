package net.aonsolutions.aon.google.apis.drive;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.DomainGserviceaccount;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.attachment.AttachType;
import com.esferalia.aon.occam.api.model.attachment.ProjectAttachmentType;
import com.esferalia.aon.occam.api.model.attachment.RegistryAttachmentType;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.watson.server.io.AonFileUtils;
import com.google.api.client.util.DateTime;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;

public class AonDrive extends DriveUtils{
	
	private static final Logger LOGGER  = Logger.getLogger(AonDrive.class.getName());

	public static AonDrive getInstace() {
		return new AonDrive();
	}
	
	public File createFolder(Drive drive, String name, File parent) {
		if(!parent.getMimeType().equals("application/vnd.google-apps.folder")){
			LOGGER.log(Level.SEVERE, name + "- The specified parent is not a folder.");
			return null;
		}
		else{
			File folder = new File();
			folder.setParents(Collections.singletonList(parent.getId()));
			folder.setName(name);
			folder.setMimeType("application/vnd.google-apps.folder");
			return createFile(drive, folder);
		}	
	}
	
	public File createFile(Drive drive, Attach attach, File parent) {
		if(!parent.getMimeType().equals("application/vnd.google-apps.folder")){
			return null;
		}
		else{
			File file = new File().setShared(true)
					.setName(attach.getDescription())
					.setMimeType(attach.getMimeType().getName())
					.setModifiedTime(new DateTime(new Date()))
					.setParents(Collections.singletonList(parent.getId()))
					.setProperties(getProperties(drive, attach));
			
			return createFile(drive, file, attach.getData());
		}	
	}

	private HashMap<String, String> getProperties(Drive drive, Attach attach) {
		HashMap<String, String> map = new HashMap<String, String>();
		
		if(attach.getCategory() != null) // NAME OF CATEGORY
			map.put("category", AON.getCategory(attach.getDomain().getName(),
				attach.getDomain().getId(), "", attach.getCategory()).getName());

		if(attach.getType() != null) // NUMBER OF ENUM
			map.put("type", attach.getType().toString());
		
		if(attach.getDomain() != null && attach.getDomain().getName() != null) // DOMAIN NAME
			map.put("domain", attach.getDomain().getName());

		if(attach.getAttachType() != null) // ATTACH TYPE NAME -- tendria que ser number of enum?
			map.put("aontype", attach.getAttachType().getName());
		
		if(attach.getId() != null) // ID OF ATTACH IN BD
			map.put("fileId", attach.getId().toString());
		
		if(attach.getAttachModule() != null) // MODULE OF ATTACH IN BD (registryId, itemId,...)
			map.put("aonmodule", attach.getAttachModule().toString());

		if(attach.getDate() != null)  // ATTACH DATE
			map.put("date", attach.getDate().toString());

		return map;
	}
	
	public File principal(Drive drive, Attach attach) {
		File root = getFile(drive, "root");
		
		FileList domainFolders = SearchFiles.searchFilesTitleEqualAndMimetype(drive, attach.getDomain().getName());
		File domainFolder = domainFolders.getFiles().size()>0 ? domainFolders.getFiles().get(0)
				: createFolder(drive, attach.getDomain().getName(), root);
	
		
		FileList typeFolders = SearchFiles.searchFilesTitleAndParent(drive, attach.getAttachType().getName(), domainFolder.getId());
		File typeFolder = typeFolders.getFiles().size()>0 ? typeFolders.getFiles().get(0)
				:createFolder(drive, attach.getAttachType().getName(), domainFolder);
				
		if(domainFolder != null && typeFolder != null){
			return createFile(drive, attach, typeFolder);
		}
		return new File();
	}
	
	public Boolean paysheet(DomainGserviceaccount d, Attach attach, Vector<String> emails) {
		Drive drive = serviceInitialize(d);
		File file = new File();
		String[] keys = {"domain", "aontype", "aonmodule", "date"};
		String[] properties = {attach.getDomain().getName(), attach.getAttachType().getName()
				, attach.getAttachModule() != null ? attach.getAttachModule().toString() : "" 
				, attach.getDate() != null ? attach.getDate().toString() : ""};
		FileList fileList = SearchFiles.searchFilesAppProperties(drive, keys, properties);
		if(fileList.getFiles().size() > 0){
			file = fileList.getFiles().get(0);
			if (file.getMd5Checksum() == null || !file.getMd5Checksum().equals(AonFileUtils.getMD5Checksum(attach.getData())))
				file = updateFile(drive, file, attach.getData());
		} else 	file = principal(drive, attach);
		return setPermissions(drive, file.getId(), emails);
	}

	public void sync(Drive drive, User user, Attach attach, Boolean dryRun){		
		if (!checkTypes(attach)) {
			if(attach.getDriveId() == null){
				if (dryRun) LOGGER.log(Level.INFO, "Dry Run "+attach.getDescription()+" : Not at Drive. It will be created & uploaded.");
				File file = principal(drive, attach);			
				if(file.getId() != null){
					attach.setDriveId(file.getId());
					updateDriveId(attach);
					deleteData(attach);
					LOGGER.log(Level.INFO,"'"+attach.getDescription()+"': Not at Drive. It was created & uploaded ["+file.getId()+"].");
				} else LOGGER.log(Level.SEVERE,"Parent of file is null");
			} else {
				if (attach.getData() == null) LOGGER.log(Level.FINE, "Skip '"+ attach.getDescription() +"': No new data.");
				File file = getFile(drive, attach.getDriveId());
				if (file.getMd5Checksum() == null || !file.getMd5Checksum().equals(
						AonFileUtils.getMD5Checksum(attach.getData()))) {
					if (dryRun)	LOGGER.log(Level.INFO,"Dry Run '"+ attach.getDescription() +"': Changed. It will be synchronized/uploaded.");
					file = updateFile(drive, file, attach.getData());
					if (file.getId() != null) {
						deleteData(attach);	
						LOGGER.log(Level.INFO,"'"+attach.getDescription()+"': Changed. It was synchronized/uploaded ["+file.getId()+"].");
					}
					LOGGER.log(Level.WARNING,"'"+ attach.getDescription() +"': Changed. It was NOT synchronized/uploaded");
				} else LOGGER.log(Level.FINE,"Skip '"+attach.getDescription()+"': New data it's the same that at drive ( MD5s are the same ).");
			}
		} else LOGGER.log(Level.WARNING,"Skip '"+attach.getDescription()+"': won't be synchronized.");
	}
	
	public static Boolean checkTypes(Attach attach) {
		return (AttachType.REGISTRY.equals(attach.getAttachType())
				&& (attach.getType() == RegistryAttachmentType.LOGO.value()
					|| attach.getType() == RegistryAttachmentType.AON_TEMPLATES.value() 
					|| attach.getType() == RegistryAttachmentType.D2_DEPOSIT.value()
					|| attach.getType() == RegistryAttachmentType.DOMAIN_BOOK_HISTORY.value() // Historial en configuración
					|| attach.getType() == RegistryAttachmentType.DOMAIN_INSERT_HISTORY.value()
					|| attach.getType() == RegistryAttachmentType.DOMAIN_REMOVE_HISTORY.value()
					|| attach.getType() == RegistryAttachmentType.CRETA_RESPUESTA.value() 
					|| attach.getType() == RegistryAttachmentType.CRETA_TRABAJADORES_Y_TRAMOS.value() 
					|| attach.getType() == RegistryAttachmentType.ECOMMERCE_PRODUCT_TEMPLATES.value()
					|| attach.getType() == RegistryAttachmentType.INVOICE_FOOTER_TEXT.value()
					|| attach.getType() == RegistryAttachmentType.REPORT_BACKGROUND.value()
				)) 
			|| (AttachType.PROJECT.equals(attach.getAttachType())
				&& (attach.getType() == ProjectAttachmentType.CONEXFLOW.value()
					|| attach.getType() == ProjectAttachmentType.PAYSLIP.value() 
				));
	}
	
	public void updateDriveId(Attach attach){
		AON.updateAttachDriveId(attach.getDomain().getName(), attach.getDomain().getId(),
				"", attach.getId(), attach.getDriveId(), attach.getAttachType());
	}
	
	public static void deleteData(Attach attach){
		AON.updateAttachData(attach.getDomain().getName(), attach.getDomain().getId(),
				"",attach.setData(null));		
	}
	
}
