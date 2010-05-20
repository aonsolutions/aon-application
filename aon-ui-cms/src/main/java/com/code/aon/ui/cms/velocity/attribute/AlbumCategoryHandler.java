package com.code.aon.ui.cms.velocity.attribute;

import java.util.ArrayList;

import com.code.aon.cms.AlbumCategoryDetail;
import com.code.aon.cms.enumeration.Templates;

public class AlbumCategoryHandler {

	private String alias;
	
	private String label;
	
	private String url;
	
	private ArrayList<AlbumHandler> list;

	public AlbumCategoryHandler (AlbumCategoryDetail detail, ArrayList<AlbumHandler> list) {
		this.alias = detail.getAlbumCategory().getAlias();
		this.label = detail.getLabel();
		this.list = list;
		this.url = Templates.ALBUM_CATEGORY.getHtmlName();
		this.url = this.url.replaceAll("%NAME%", this.alias);
	}

	public String getLabel() {
		return label;
	}

	public ArrayList<AlbumHandler> getList() {
		return list;
	}

	public String getUrl() {
		return url;
	}

	public String getAlias() {
		return alias;
	}
	
	
}
