package com.code.aon.ui.cms.tree;

import com.code.aon.ui.cms.controller.GalleryController;
import com.code.aon.ui.cms.util.ControllerUtil;
import com.code.aon.ui.util.AonUtil;

public class FileSystemBean {

    public synchronized FileSystemNode[] getImageRoots() {
    	FileSystemNode[] srcRoots = new FileSystemNode[1];
    	srcRoots[0] = new FileSystemNode(ControllerUtil.getImagesPath(),this);
        return srcRoots;
    }

	public void setSelected(FileSystemNode fileSystemNode) {
		GalleryController controller = (GalleryController)AonUtil.getRegisteredBean("gallery"); 
		controller.setCurrentPath(fileSystemNode.getPath());
		controller.chargeImageList();
	}

}