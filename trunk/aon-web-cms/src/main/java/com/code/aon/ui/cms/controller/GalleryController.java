package com.code.aon.ui.cms.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.richfaces.event.UploadEvent;
import org.richfaces.model.UploadItem;

import com.code.aon.cms.Image;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.cms.IGalleryController;
import com.code.aon.ui.cms.util.ControllerUtil;
import com.code.aon.ui.cms.util.ImageComparator;
import com.code.aon.ui.cms.util.ZipUtil;
import com.code.aon.ui.form.BasicController;

public abstract class GalleryController extends BasicController implements IGalleryController {

	private static final Logger LOGGER = Logger.getLogger(GalleryController.class.getName());
	
	private boolean showImageWindow;
	
	private boolean showThumbnailImageWindow;
	
	private File currentPath = recoverFilesPath();

	private String getRelativePath(String path) {
		String base = recoverFilesPath().getAbsolutePath(); 
		if (path.startsWith(base)) {
			if (path.length() == base.length()) return "/";
			else return path.substring(base.length()).replace('\\', '/');
		}
		return "";
	}

	public void chargeImageList() {
		ArrayList<Image> list = new ArrayList<Image>();
		if (!currentPath.exists()) currentPath.mkdirs();
		File files[] = currentPath.listFiles(getFilenameFilter());
		for (int i=0; i < files.length; i++) {
			File file = files[i];
			if (!file.isDirectory()) {
				Image img = new Image();
				img.setName(file.getName());
				img.setFile(file);
				img.setRelativePath(getRelativePath(file.getAbsolutePath()));
				list.add(img);
			}
		}
		Collections.sort(list, new ImageComparator());
		model = new ListDataModel(list);
		list = null;
	}

	public String getCurrentRelativePath() {
		return getRelativePath(currentPath.getAbsolutePath());
	}
	
	public File getCurrentPath() {
		return currentPath;
	}

	public void setCurrentPath(File currentPath) {
		this.currentPath = currentPath;
	}

	public DataModel getModel() throws ManagerBeanException {
		if (model == null) {
			chargeImageList();
		}
		return model;
	}

	public void onDeleteFile(ActionEvent event) throws ManagerBeanException {
		File file = ((Image)getModel().getRowData()).getFile();
		file.delete();
		chargeImageList();
	}

	public void fileUploaded(UploadEvent event) {
		UploadItem item = event.getUploadItem();
		String upload_name = item.getFileName();
		upload_name = upload_name.replace('\\', '/');
		if (upload_name.lastIndexOf('/')!=-1)
			upload_name = upload_name.substring(upload_name.lastIndexOf('/'));
		upload_name = upload_name.replaceAll("[^A-Za-z0-9._-]+", "");
		String fileName = File.separator+upload_name;
		//Miramos si es un fichero zip, en ese caso creamos un directorio y descomprimimos ahi los archivos...
		String ext = fileName.substring(fileName.indexOf(".") + 1);
		if (ext != null && ext.trim().toLowerCase().equals("zip")) {
			ZipUtil.uncompressZipData(item.getData(), currentPath+File.separator+fileName.substring(0, fileName.indexOf(".")));
		}
		else {
			File file = new File( currentPath+File.separator+fileName);
			FileOutputStream outputStream = null; 
			try{
				byte[] data = item.getData();
		        outputStream = new FileOutputStream(file);
		        outputStream.write(data);
				chargeImageList();
			} catch (Throwable th) {
				LOGGER.log(Level.SEVERE, th.getMessage(), th);
			} finally {
		        IOUtils.closeQuietly(outputStream);
			}
		}
	}

	private String folderName;
	
	public String getFolderName() {
		return folderName;
	}

	public void setFolderName(String folderName) {
		this.folderName = folderName;
	}

	public void createFolder( ActionEvent event ) throws IOException {
		if (! StringUtils.isEmpty(folderName) ){
			File file = new File( currentPath, folderName );
			if (!file.exists()){
				file.mkdir();
			}
			setCurrentPath( file );
			chargeImageList();			
			folderName = null;
		}
	}

	public void deleteFolder( ActionEvent event ) throws IOException {
		FileUtils.deleteDirectory(currentPath);
		currentPath = recoverFilesPath();
		chargeImageList();
	}

	public boolean isEmptyDir(){
		if (currentPath.listFiles().length==0)
			return true;
		return false;
	}
	
	public String getPreviewCurrentUrl() {
		String url = ControllerUtil.getPreviewURL() + "/" + ControllerUtil.IMAGES_PATH + getCurrentRelativePath();
		return url;
	}
	
	public static void main(String[] args) {
		String fileName = "fsadfs$$$·33a6756745._-gdfsg%%%";
		fileName = fileName.replaceAll("[^A-Za-z0-9._-]+", "");
		LOGGER.info(fileName);
	}

	public boolean isShowImageWindow() {
		return showImageWindow;
	}

	public void setShowImageWindow(boolean showImageWindow) {
		this.showImageWindow = showImageWindow;
	}

	public boolean isShowThumbnailImageWindow() {
		return showThumbnailImageWindow;
	}

	public void setShowThumbnailImageWindow(boolean showThumbnailImageWindow) {
		this.showThumbnailImageWindow = showThumbnailImageWindow;
	}
	
}
