package com.code.aon.ui.cms.tree;

import com.code.aon.ui.cms.controller.GalleryController;
import com.code.aon.ui.cms.util.ControllerUtil;
import com.code.aon.ui.util.AonUtil;

public class FileSystemBean {

    private FileSystemNode[] srcRoots = null;

    public synchronized FileSystemNode[] getImageRoots() {
    	srcRoots = new FileSystemNode(ControllerUtil.getImagesPath(),this).getNodes();
        return srcRoots;
    }

	public void setSelected(FileSystemNode fileSystemNode) {
		GalleryController controller = (GalleryController)AonUtil.getRegisteredBean("gallery"); 
		controller.setCurrentPath(fileSystemNode.getPath());
		controller.chargeImageList();
	}

}