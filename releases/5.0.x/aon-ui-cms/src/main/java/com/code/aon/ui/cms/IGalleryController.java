package com.code.aon.ui.cms;

import java.io.FilenameFilter;

import com.code.aon.ui.cms.util.FolderNodeUserObject;

public interface IGalleryController {

	public FolderNodeUserObject getSelected();
	
	public void setSelected(FolderNodeUserObject selected);

	public String revoverFilesPath();
	
	public FilenameFilter getFilenameFilter();
}
