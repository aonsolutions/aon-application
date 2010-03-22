package com.code.aon.ui.cms.tree;

import java.io.File;

import com.code.aon.ui.cms.controller.GalleryController;
import com.code.aon.ui.cms.controller.ICMSConstants;
import com.code.aon.ui.cms.util.ControllerUtil;
import com.code.aon.ui.util.AonUtil;

public class FileSystemBean implements ICMSConstants {

	private String current;
	
    public synchronized FileSystemNode[] getImageRoots() {
    	current = GALLERY;
    	FileSystemNode[] srcRoots = new FileSystemNode[1];
    	srcRoots[0] = new FileSystemNode(ControllerUtil.getImagesPath(),this);
        return srcRoots;
    }

    public synchronized FileSystemNode[] getDocumentRoots() {
    	current = DOCUMENT;
    	FileSystemNode[] srcRoots = new FileSystemNode[1];
    	srcRoots[0] = new FileSystemNode(ControllerUtil.getDocumentsPath(),this);
        return srcRoots;
    }

	public void setSelected(File path) {
		GalleryController controller = (GalleryController)AonUtil.getRegisteredBean(current); 
		controller.setCurrentPath(path);
		controller.chargeImageList();
	}

}