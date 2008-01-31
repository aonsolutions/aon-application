package com.code.aon.ui.cms.velocity.attribute;

import java.util.ArrayList;

import com.code.aon.cms.DownloadCategoryDetail;
import com.code.aon.ui.cms.velocity.DownloadsGenerator;

public class DownloadCategoryHandler {

	private String label;
	
	private ArrayList<DownloadHandler> list;
	
	public DownloadCategoryHandler (DownloadCategoryDetail group) {
		label = group.getLabel();
		list = DownloadsGenerator.getDownloadsList(group);
	}


	public String getLabel() {
		return label;
	}


	public ArrayList<DownloadHandler> getList() {
		return list;
	}

}
