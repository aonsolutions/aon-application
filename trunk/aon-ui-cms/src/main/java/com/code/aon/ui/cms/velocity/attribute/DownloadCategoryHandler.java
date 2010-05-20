package com.code.aon.ui.cms.velocity.attribute;

import java.util.ArrayList;

import com.code.aon.cms.DownloadCategoryDetail;
import com.code.aon.cms.enumeration.Templates;

public class DownloadCategoryHandler {

	private String alias;

	private String label;
	
	private String url;
	
	private ArrayList<DownloadHandler> list;
	
	public DownloadCategoryHandler (DownloadCategoryDetail group, ArrayList<DownloadHandler> list) {
		this.alias = group.getDownloadCategory().getAlias();
		this.label = group.getLabel();
		String link = Templates.DOWNLOADS.getHtmlName();
		link = link.replaceAll("%NAME%", group.getDownloadCategory().getAlias());
		this.url = link;
		this.list = list;
	}

	public String getAlias() {
		return alias;
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
