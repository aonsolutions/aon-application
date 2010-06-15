package com.code.aon.faces.controller;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.SystemUtils;
import org.richfaces.event.UploadEvent;
import org.richfaces.model.UploadItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.ui.common.io.AonFile;
import com.code.aon.ui.util.AonUtil;

public class FileManager {
	
	private static final String FILE_MANAGER_FORM = "fileManager_form";

	private static final Logger LOGGER = LoggerFactory.getLogger(FileManager.class);

	private static final DecimalFormat BYTES_FORMAT = new DecimalFormat("0 bytes");
	
	private static final DecimalFormat KB_FORMAT = new DecimalFormat("0.## KB");
	
	private static final DecimalFormat MB_FORMAT = new DecimalFormat("0.## MB");
	
	private static final DecimalFormat GB_FORMAT = new DecimalFormat("0.## GB");
	
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
		this.model = new ListDataModel( list );
	}

	public void onInit( ActionEvent event ) {
		if ( getCurrentDirectory() == null ) {
			setCurrentDirectory( getDefaultDirectory() );
		}
		loadModel( getCurrentDirectory() );
	}
	
	private File getFile() {
		if ( getModel().isRowAvailable() ) {
			return (File) getModel().getRowData();
		}
		return null;		
	}
	
	public Date getLastModified() {
		File file = getFile();
		if ( file != null ) {
			return new Date( file.lastModified() );
		}
		return null;
	}

	public static String getDisplaySize( long value ) {
		String result = "";
		double size = value;
		if ( size != -1 ) {
			if ( size < FileUtils.ONE_KB ) {
				result = BYTES_FORMAT.format(size);
			} else if ( size < FileUtils.ONE_MB ) {
				result = KB_FORMAT.format(size / FileUtils.ONE_KB);
			} else if ( size < FileUtils.ONE_GB ) {
				result = MB_FORMAT.format(size / FileUtils.ONE_MB);
			} else {
				result = GB_FORMAT.format(size / FileUtils.ONE_GB);
			}
		}
		return result;		
	}
	
	public String getLength() {
		File file = getFile();
		if ( (file != null) && (! file.isDirectory()) ) {
			return getDisplaySize( file.length() );
		}
		return null;
	}	

	public String getPermissions() {
		File file = getFile();
		if ( file != null ) {
			StringBuffer sb = new StringBuffer();
			if ( file.canRead() ) {
				sb.append( "R" );
			}
			if ( file.canWrite() ) {
				sb.append( "W" );
			}
			if ( file.canExecute() ) {
				sb.append( "X" );
			}
			return sb.toString();
		}
		return null;
	}	

	public String getStyleClass() {
		File file = getFile();
		if ( file != null ) {
			if ( file.isDirectory() ) {
				return "aon-icon-folder";
			}
		}
		return "aon-icon-csv";
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
		File root = new File( FilenameUtils.getPrefix(parent) );
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
        File file = (File) getModel().getRowData();
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

	public void fileUploaded(UploadEvent event) {
		UploadItem item = event.getUploadItem();
		File file = new File( getCurrentDirectory(), item.getFileName() );
		try {
			if ( item.isTempFile() ) {
				FileUtils.copyFile( item.getFile(), file );
			} else {
				FileUtils.writeByteArrayToFile(file, item.getData());
			}
			loadModel( getCurrentDirectory() );
		} catch (IOException e) {
			LOGGER.error("file upload " + file, e);
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		}
	}	
	
	private String getRelativePath( File path, File file ) {
		String fullPath = FilenameUtils.normalizeNoEndSeparator(file.getAbsolutePath());
		String basePath = FilenameUtils.normalizeNoEndSeparator(path.getAbsolutePath());
		return StringUtils.substring(fullPath, basePath.length());
	}	
	
	private void addDirectory( ZipOutputStream out, File zipFile, File directory ) throws IOException {
		for( File file : directory.listFiles() ) {
			if ( file.canRead() ) {
				if ( file.isDirectory() ) {
					addDirectory(out, zipFile, file);
				} else if ( file.isFile() && (!zipFile.equals(file)) ) {
					InputStream in = new BufferedInputStream(new FileInputStream(file));
					String name = getRelativePath(zipFile.getParentFile(), file);
					out.putNextEntry(new ZipEntry(name));
					IOUtils.copy(in, out);
					out.closeEntry();
					IOUtils.closeQuietly(in);
				}
			}
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
			OutputStream os = new BufferedOutputStream(new FileOutputStream(zipFile));
			ZipOutputStream out = new ZipOutputStream(os);
			if ( getCurrentDirectory().canRead() ) {
				addDirectory(out, zipFile, getCurrentDirectory());	
			}
		    IOUtils.closeQuietly(out);
		} catch (IOException e) {
			LOGGER.error("createZip " + zipFile, e);
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		}
		loadModel( getCurrentDirectory() );
		reset();
	}	
	
}
