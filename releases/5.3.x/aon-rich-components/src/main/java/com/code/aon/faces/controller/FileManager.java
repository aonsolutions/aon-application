package com.code.aon.faces.controller;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.validator.ValidatorException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.SystemUtils;
import org.richfaces.event.UploadEvent;
import org.richfaces.model.UploadItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.common.util.ZipUtil;
import com.code.aon.ui.common.io.AonFile;
import com.code.aon.ui.util.AonUtil;

public class FileManager {
	
	private static final String FILE_MANAGER_FORM = "fileManager_form";
	
	private static final String BUNDLE_NAME = "richBundle";

	private static final Logger LOGGER = LoggerFactory.getLogger(FileManager.class);
	
	private String beanName;
	
	private int pageLimit;
	
	private DataModel model;
	
	private boolean _new;
	
	private String nextAction;
	
	private AonFile aonFile;
	
	private String fileValue;
	
	private String folderName;
	
	private String zipName;
	
	private File currentFile;
	
	private File currentDirectory;
	
	private File startDirectory;
	
	private boolean inRename;
	
	private boolean uploadZip;
	
	public FileManager() {
		this.pageLimit = 20;
	}
	
	public String getBeanName() {
		return beanName;
	}

	public void setBeanName(String beanName) {
		this.beanName = beanName;
	}

	public int getPageLimit() {
		return pageLimit;
	}

	public void setPageLimit(int pageLimit) {
		this.pageLimit = pageLimit;
	}

	public DataModel getModel() {
		return model;
	}

	public void setModel(DataModel model) {
		this.model = model;
	}
	
	public File getCurrentDirectory() {
		return currentDirectory;
	}

	public void setCurrentDirectory(File currentDirectory) {
		this.currentDirectory = currentDirectory;
	}
	
	public File getStartDirectory() {
		return startDirectory;
	}

	public void setStartDirectory(File startDirectory) {
		this.startDirectory = startDirectory;
	}

	public AonFile getAonFile() {
		return aonFile;
	}

	public void setAonFile(AonFile aonFile) {
		this.aonFile = aonFile;
	}
	
	public String getFileValue() {
		return fileValue;
	}

	public void setFileValue(String fileValue) {
		this.fileValue = fileValue;
	}

	public boolean isImage() {
		MimeType type = this.aonFile.getMimeType();
		return (type != null) && type.getName().startsWith("image/"); 
	}

	public String getFolderName() {
		return folderName;
	}

	public void setFolderName(String folderName) {
		this.folderName = folderName;
	}
	
	public String getZipName() {
		return zipName;
	}

	public void setZipName(String zipName) {
		this.zipName = zipName;
	}

	public boolean isUploadZip() {
		return uploadZip;
	}

	public void setUploadZip(boolean uploadZip) {
		this.uploadZip = uploadZip;
	}
	
	public boolean isInRename() {
		return inRename;
	}

	public void setInRename(boolean inRename) {
		this.inRename = inRename;
	}

	private File getDefaultDirectory() {
		File directory = new File( "/home" );
		if ( directory.exists() && directory.canRead() ) {
			return directory;
		}
		return SystemUtils.getUserHome();
	}
	
	private void loadModel( File directory ) {
		List<File> list = new ArrayList<File>();
		List<File> files = new ArrayList<File>();
		File[] fileArray = directory.listFiles();
		if (! ArrayUtils.isEmpty(fileArray) ) {
			for( File file : fileArray ) {
				if ( file.isDirectory() ) {
					list.add( file );
				} else {
					files.add( file );
				}
			}
			Collections.sort(list);
			Collections.sort(files);
			list.addAll( files );			
		}
		List<FileWrapper> fws = new ArrayList<FileWrapper>(list.size());
		for( File file : list ) {
			fws.add( new FileWrapper(file) );
		}
		this.model = new ListDataModel( fws );
	}

	public void onInit( ActionEvent event ) {
		if ( this.startDirectory != null ) {
			setCurrentDirectory( this.startDirectory );
		} else if ( getCurrentDirectory() == null ) {
			setCurrentDirectory( getDefaultDirectory() );
		}
		loadModel( getCurrentDirectory() );
	}
	
	public File getFile() {
		File file = null;
		if ( getModel().isRowAvailable() ) {
			file = ((FileWrapper) getModel().getRowData()).getWrappedObject();
		}
		return file;		
	}

	public String getSelectAction() {
		return this.nextAction;
	}

	private AonFile getAonFile( File file ) {
		AonFile aonFile = new AonFile();
		aonFile.setFileName(file.getName());
		try {
			byte[] data = FileUtils.readFileToByteArray(file);
			aonFile.setData(data);
			MimeType type = AttachmentUtil.getMimeType(aonFile);
			aonFile.setMimeType(type);
		} catch (IOException e) {
			LOGGER.error("change to file " + file, e);
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		}		
		return aonFile;
	}
	
	private void changeCurrentFile( File file ) {
		this.currentFile = file;
		this.aonFile = getAonFile(file);
		if (! isImage() ) {
			this.fileValue = new String(this.aonFile.getData());
		}
	}
	
	public void onSelect( ActionEvent event ) {
		File file = getFile();
		if ( (file != null) ) {
			this.nextAction = null;
			if ( file.isDirectory() ) {
				setCurrentDirectory( file );
				loadModel( getCurrentDirectory() );
			} else {
				changeCurrentFile(file);
				this.nextAction = FILE_MANAGER_FORM;
			}
		}		
	}

	public void onCurrentRemove( ActionEvent event ) {
		File file = getFile();
		if ( file != null ) {
			FileUtils.deleteQuietly(file);
			loadModel( getCurrentDirectory() );
		}		
	}

	public void onRemove( ActionEvent event ) {
		if ( this.currentFile != null ) {
			FileUtils.deleteQuietly(this.currentFile);
			loadModel( getCurrentDirectory() );
			reset();
		}		
	}
	
	public void onGoParent( ActionEvent event ) {
		File parent = getCurrentDirectory().getParentFile();
		if ( (parent != null) && parent.canRead() ) {
			setCurrentDirectory(parent);
			loadModel(getCurrentDirectory());
		}
	}

	public void onGoRoot( ActionEvent event ) {
		String parent = getCurrentDirectory().getParent();
		File root = null;
		if ( this.startDirectory != null ) {
			root = this.startDirectory;
		} else {
			root = new File( FilenameUtils.getPrefix(parent) );	
		}
		if ( root.canRead() ) {
			setCurrentDirectory(root);
			loadModel(getCurrentDirectory());
		}
	}
	
	public boolean isNew() {
		return this._new;
	}
	
	public void setNew( boolean value ) {
		this._new = true;
	}
	
	public void accept( ActionEvent event ) {
		File file = this.currentFile;
		if (! StringUtils.equals(this.aonFile.getFileName(), file.getName()) ) {
			FileUtils.deleteQuietly(file);
			file = new File( file.getParentFile(), this.aonFile.getFileName() );
		}
		try {
			FileUtils.writeStringToFile(file, this.fileValue);
		} catch (IOException e) {
			LOGGER.error("writting to file " + file, e);
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		}
	}
	
	public void onCancel( ActionEvent event ) {
		reset();
	}

	public void onRefresh( ActionEvent event ) {
		loadModel( getCurrentDirectory() );
		reset();
	}
	
	private void reset() {
		this.currentFile = null;
		this.inRename = false;
		setAonFile(null);
		setFileValue(null);		
		setFolderName(null);
	}

	public void createImageContent(OutputStream out, Object data) throws IOException {
		if (! ArrayUtils.isEmpty(this.aonFile.getData()) ) {
			out.write( this.aonFile.getData() );
		}
	}
	
    public void downloadAttachment( ActionEvent event ) throws NumberFormatException, ManagerBeanException {
        File file = getFile();
        AonFile aonFile = getAonFile(file);
        AttachmentUtil.downloadAttachment(aonFile.getFileName(),aonFile.getMimeType(),aonFile.getData());    	
    }	
    
	public void onCreateFolder( ActionEvent event ) {
		File newFolder = new File( getCurrentDirectory(), this.folderName );
		try {
			FileUtils.forceMkdir(newFolder);
		} catch (IOException e) {
			LOGGER.error("mkdir " + newFolder, e);
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		}
		loadModel( getCurrentDirectory() );
		reset();
	}

	public void onFileUploaded(UploadEvent event) {
		UploadItem item = event.getUploadItem();
		File file = new File( getCurrentDirectory(), item.getFileName() );
		try {
			if ( isUploadZip() ) {
				zipUploaded(item);
			} else {
				fileUploaded(item);
			}
			if ( item.isTempFile() ) {
				FileUtils.deleteQuietly( item.getFile() );				
			}
			loadModel( getCurrentDirectory() );
		} catch (IOException e) {
			LOGGER.error("file upload " + file, e);
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		}
	}	

	public void fileUploaded(UploadItem item) throws IOException {
		File file = new File( getCurrentDirectory(), item.getFileName() );
		if ( item.isTempFile() ) {
			FileUtils.copyFile( item.getFile(), file );
		} else {
			FileUtils.writeByteArrayToFile(file, item.getData());
		}
	}	

	public void zipUploaded(UploadItem item) throws IOException {
		String fileName = item.getFileName();
		String ext = FilenameUtils.getExtension(fileName);
		if ( StringUtils.equalsIgnoreCase(ext, MimeType.MIME_ZIP.getExtension()) ) {
			InputStream in = null;
			if ( item.isTempFile() ) {
				in = new FileInputStream( item.getFile() );
			} else {
				in = new ByteArrayInputStream( item.getData() );
			}			
			ZipUtil.uncompressZipData(in, this.currentDirectory);
			IOUtils.closeQuietly(in);
		}
	}	
	
	public void onShowZipWindow( ActionEvent event ) {
		this.zipName = null;
		String extension = "." + MimeType.MIME_ZIP.getExtension();
		String base = getCurrentDirectory().getName();
		File file = new File( getCurrentDirectory(), base + extension );
		if ( file.exists() ) {
			try {
				File tempFile = File.createTempFile( base, extension, getCurrentDirectory() );
				this.zipName = tempFile.getName();
				FileUtils.deleteQuietly(tempFile);
			} catch (IOException e) {
				LOGGER.error( "Error calculating zip file name", e);
			}
		} else {
			this.zipName = file.getName();
		}
	}
	
	public void onCreateZip( ActionEvent event ) {
		String extension = FilenameUtils.getExtension(this.zipName);
		if ( StringUtils.isEmpty(extension) ) {
			this.zipName = this.zipName + "." + MimeType.MIME_ZIP.getExtension();
		}
		File zipFile = new File( getCurrentDirectory(), this.zipName );
		if ( zipFile.exists() ) {
			AonUtil.addErrorMessage( "File already exists: " + zipFile );
		}
		try {
			if ( getCurrentDirectory().canRead() ) {
				ZipUtil.createZip(zipFile, getCurrentDirectory());
			}
		} catch (IOException e) {
			LOGGER.error("createZip " + zipFile, e);
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		}
		loadModel( getCurrentDirectory() );
		reset();
	}	
	
	public boolean isInRoot() {
		return ObjectUtils.equals(getCurrentDirectory(), getStartDirectory());
	}

	public String getCurrentPath() {
		if ( this.startDirectory != null ) {
			if ( isInRoot() ) {
				return File.separator;
			}
			return ZipUtil.getRelativePath(this.startDirectory, getCurrentDirectory());
		}
		return getCurrentDirectory().toString();
	}

	public void onCleanCurrentFolder( ActionEvent event ) {
		try {
			FileUtils.cleanDirectory(this.currentDirectory);
		} catch (IOException e) {
			LOGGER.error("cleanCurrentFolder " + this.currentDirectory, e);
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		}
		loadModel( getCurrentDirectory() );
		reset();
	}	

	@SuppressWarnings("unchecked")
	public void fileNameCheck(FacesContext context, UIComponent component, Object value) throws ManagerBeanException {
		String name = value.toString();
		File file = getFile();
		if (! StringUtils.equals(file.getName(), name) ) {
			List<FileWrapper> list = (List<FileWrapper>) getModel().getWrappedData(); 
			for( FileWrapper fw : list ) {
				if ( fw.getWrappedObject().getName().equals(name) ) {
					FacesMessage message = new FacesMessage(AonUtil.getMessage(BUNDLE_NAME, "rich_file_duplicated_name"));
					message.setSeverity(FacesMessage.SEVERITY_ERROR);
					throw new ValidatorException( message );
				}
			}
		}
	}
	
}
