package com.code.aon.faces.controller;

import static com.code.aon.ui.common.ICommonMessages.FILE_DUPLICATED_NAME;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileFilter;
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

import com.code.aon.common.AonVersion;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.common.util.AonFile;
import com.code.aon.common.util.ZipUtil;
import com.code.aon.ui.common.serialize.SerializableListDataModel;
import com.code.aon.ui.form.DataScrollerState;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.ui.util.DownloadUtil;

public class FileManager extends DataScrollerState implements IRichConstants {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(FileManager.class);
	
	private boolean _new;
	
	private String backActionListener;
	
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
	
	private FileFilter fileFilter;
	
	private String searchName;
	
	private String searchContent;
	
	public String getBackActionListener() {
		return backActionListener;
	}

	public void setBackActionListener(String expression) {
		this.backActionListener = expression;
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

	public String getSearchName() {
		return searchName;
	}

	public void setSearchName(String searchName) {
		this.searchName = searchName;
	}

	public String getSearchContent() {
		return searchContent;
	}

	public void setSearchContent(String searchContent) {
		this.searchContent = searchContent;
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
		File[] fileArray = directory.listFiles(this.fileFilter);
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
		setModel(new SerializableListDataModel(fws));
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
		if ( getDirectModel().isRowAvailable() ) {
			file = ((FileWrapper) getDirectModel().getRowData()).getWrappedObject();
		}			
		return file;		
	}

	public String getSelectAction() {
		return this.nextAction;
	}

	private AonFile getAonFile( File file ) {
		AonFile aonFile = new AonFile();
		aonFile.setFile(file);
		aonFile.setMimeType(aonFile.resolveMimeType());
		return aonFile;
	}
	
	private void changeCurrentFile( File file ) {
		this.currentFile = file;
		this.aonFile = getAonFile(file);
		if (! isImage() ) {
			try {
				byte[] data = this.aonFile.getOrReadData();
				this.fileValue = new String(data);
			} catch (IOException e) {
				LOGGER.error( e.getMessage(), e );
			}
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
		this.fileFilter = null;
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
		byte[] buffer = this.aonFile.getOrReadData();
		if (! ArrayUtils.isEmpty(buffer) ) {
			out.write(buffer);
		}
	}
	
    public void downloadAttachment( ActionEvent event ) throws IOException {
        File file = getFile();
        AonFile af = getAonFile(file);
        InputStream in = af.openStream();
        DownloadUtil.downloadAttachment(af.getFileName(), af.getMimeType(), in, af.getSize());
        in.close();
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
					FacesMessage message = new FacesMessage(AonUtil.getMessage(FILE_DUPLICATED_NAME));
					message.setSeverity(FacesMessage.SEVERITY_ERROR);
					throw new ValidatorException( message );
				}
			}
		}
	}

	public void onEditSearch( ActionEvent event ) {
		setSearchName(null);
		setSearchContent(null);
		this.fileFilter = null;
	}	

	public void onSearch( ActionEvent event ) {
		if (! (StringUtils.isEmpty(searchName) && StringUtils.isEmpty(searchContent)) ) {
			this.fileFilter = new FileFilter() {
				
				@Override
				public boolean accept(File file) {
					boolean ok = true;
					if (! StringUtils.isEmpty(searchName) ) {
						ok = StringUtils.containsIgnoreCase(file.getName(), searchName);
					}
					if ( ok ) {
						if (! StringUtils.isEmpty(searchContent) ) {
							if ( file.isFile() && file.canRead() ) {
								try {
									String content = FileUtils.readFileToString(file);
									ok = StringUtils.containsIgnoreCase(content, searchContent);
								} catch (IOException e) {
									LOGGER.error( e.getMessage(), e );
								}								
							} else {
								ok = false;
							}
						}
					}
					return ok;
				}
				
			};
		}
		loadModel( getCurrentDirectory() );
		reset();
	}

}
