package com.code.aon.ui.cms.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.richfaces.component.UITree;
import org.richfaces.component.html.HtmlTree;
import org.richfaces.event.NodeSelectedEvent;
import org.richfaces.event.UploadEvent;
import org.richfaces.model.ListRowKey;
import org.richfaces.model.UploadItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.cms.Image;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.cms.IGalleryController;
import com.code.aon.ui.cms.tree.FileSystemNode;
import com.code.aon.ui.cms.util.ControllerUtil;
import com.code.aon.ui.cms.util.ZipUtil;
import com.code.aon.ui.form.BasicController;

public abstract class GalleryController extends BasicController implements IGalleryController {

	private final static Logger LOGGER = LoggerFactory.getLogger(GalleryController.class);
	
    private static Comparator<Image> COMPARATOR = new Comparator<Image>() {
    	
    	public int compare(Image img1, Image img2) {
    		return img1.getName().compareToIgnoreCase(img2.getName());
    	}    	
    	
	};	
	
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
		Collections.sort(list, COMPARATOR);
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

	public DataModel getModel() {
		if (model == null) {
			chargeImageList();
		}
		return model;
	}
	
	public int getColumns() {
		if ( getModel() != null ) {
			return (getModel().getRowCount() > 6) ? 6 : getModel().getRowCount();
		}
		return 0;
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
		String ext = FilenameUtils.getExtension(fileName);
		if ( StringUtils.equalsIgnoreCase(ext, "zip") ) {
			File folder = new File( currentPath, FilenameUtils.getBaseName(fileName));
			ZipUtil.uncompressZipData(item.getData(), folder);
		} else {
			File file = new File( currentPath, fileName );
			try {
				FileUtils.writeByteArrayToFile(file, item.getData());
				chargeImageList();
			} catch (Throwable th) {
				LOGGER.error(th.getMessage(), th);
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
		String[] list = currentPath.list();
		return ArrayUtils.isEmpty(list);
	}
	
	public String getPreviewCurrentUrl() {
		String url = ControllerUtil.getPreviewURL() + ControllerUtil.IMAGES_PATH + getCurrentRelativePath();
		return url;
	}
	
	public static void main(String[] args) {
		String fileName = "fsadfs$$$·33a6756745._-gdfsg%%%";
		fileName = fileName.replaceAll("[^A-Za-z0-9._-]+", "");
		LOGGER.info(fileName);
	}

	public void nodeSelected(NodeSelectedEvent event) {
		HtmlTree tree = (HtmlTree) event.getComponent();
		if ( tree.isRowAvailable() ) {
			FileSystemNode fsn = (FileSystemNode) tree.getRowData();
			setCurrentPath( fsn.getPath() );
			chargeImageList();
		}
	}
	
    public FileSystemNode[] getRoots() {
    	FileSystemNode[] srcRoots = new FileSystemNode[1];
    	srcRoots[0] = new FileSystemNode( recoverFilesPath() );
        return srcRoots;
    }
	
	public Boolean adviseNodeOpened(UITree tree) {
        Object key = tree.getRowKey();
        ListRowKey<FileSystemNode> treeRowKey = (ListRowKey<FileSystemNode>) key;
        if (treeRowKey == null || treeRowKey.depth() <= 1) {
            return Boolean.TRUE;
        }		
		return null;
	}	

	public Boolean adviseNodeSelected(UITree tree) {
        Object key = tree.getRowKey();
        ListRowKey<FileSystemNode> treeRowKey = (ListRowKey<FileSystemNode>) key;
        if ( tree.isRowAvailable() ) {
        	FileSystemNode node = (FileSystemNode) tree.getRowData();
        	return ObjectUtils.equals(currentPath, node.getPath());
		}
		return null;
	}	

}