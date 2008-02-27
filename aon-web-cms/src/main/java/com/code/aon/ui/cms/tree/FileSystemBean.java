package com.code.aon.ui.cms.tree;

import com.code.aon.ui.cms.controller.GalleryController;
import com.code.aon.ui.cms.util.ControllerUtil;
import com.code.aon.ui.util.AonUtil;

public class FileSystemBean {

	private String current;
	
    public synchronized FileSystemNode[] getImageRoots() {
    	current = "gallery";
    	FileSystemNode[] srcRoots = new FileSystemNode[1];
    	srcRoots[0] = new FileSystemNode(ControllerUtil.getImagesPath(),this);
        return srcRoots;
    }

    public synchronized FileSystemNode[] getDocumentRoots() {
    	current = "document";
    	FileSystemNode[] srcRoots = new FileSystemNode[1];
    	srcRoots[0] = new FileSystemNode(ControllerUtil.getDocumentsPath(),this);
        return srcRoots;
    }

	public void setSelected(FileSystemNode fileSystemNode) {
		GalleryController controller = (GalleryController)AonUtil.getRegisteredBean(current); 
		controller.setCurrentPath(fileSystemNode.getPath());
		controller.chargeImageList();
	}

}