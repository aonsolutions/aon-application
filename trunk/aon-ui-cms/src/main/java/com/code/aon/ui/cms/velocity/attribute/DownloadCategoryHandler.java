package com.code.aon.ui.cms.velocity.attribute;

import java.util.ArrayList;

import com.code.aon.cms.DownloadCategoryDetail;
import com.code.aon.cms.enumeration.Templates;
import com.code.aon.ui.cms.velocity.DownloadsGenerator;

public class DownloadCategoryHandler {

	private String label;
	
	private String url;
	
	private ArrayList<DownloadHandler> list;
	
	public DownloadCategoryHandler (DownloadCategoryDetail group) {
		label = group.getLabel();
		String link = Templates.DOWNLOADS.getHtmlName();
		link = link.replaceAll("%NAME%", group.getDownloadCategory().getAlias());
		url = link;
		list = DownloadsGenerator.getDownloadsList(group);
	}


	public String getLabel() {
		return label;
	}


	public ArrayList<DownloadHandler> getList() {
		return list;
	}

	public String getUrl() {
		return url;
	}

}
