package com.code.aon.ui.cms.util;

import com.code.aon.ui.cms.IGalleryController;
import com.code.aon.ui.cms.controller.GalleryController;
import com.icesoft.faces.component.tree.IceUserObject;

import javax.faces.event.ActionEvent;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 * The UrlNodeUserObject object is responsible for storing extra data
 * for a url.  The url along with text is bound to a ice:commanLink object which
 * will launch a new browser window pointed to the url.
 */
public class FolderNodeUserObject extends IceUserObject {

    private String path;
    
    private String relativePath;

    private IGalleryController gallery;

    public FolderNodeUserObject(DefaultMutableTreeNode wrapper, IGalleryController gallery) {
        super(wrapper);
        this.gallery = gallery;
        setLeafIcon("/images/cms/tree/folderopen.gif");
        setBranchContractedIcon("/images/cms/tree/folderclosed.gif");
        setBranchExpandedIcon("/images/cms/tree/folderopen.gif");
    }
    
    public void onSelectFolder(ActionEvent action){
    	gallery.setSelected(this);
    }

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

    public boolean isSelected() {
    	if (this.equals(gallery.getSelected())) return true;
        return false;
    }

	public void setRelativePath(String relativePath) {
		this.relativePath = relativePath;
	}

	public String getRelativePath() {
		return relativePath;
	}
    
}
