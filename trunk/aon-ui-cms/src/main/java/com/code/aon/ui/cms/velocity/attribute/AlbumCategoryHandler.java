package com.code.aon.ui.cms.velocity.attribute;

import java.util.ArrayList;

import com.code.aon.cms.AlbumCategoryDetail;

public class AlbumCategoryHandler {

	private String label;
	
	private ArrayList<AlbumHandler> list;

	public AlbumCategoryHandler (AlbumCategoryDetail detail, ArrayList<AlbumHandler> list) {
		this.label = detail.getLabel();
		this.list = list;
	}

	public String getLabel() {
		return label;
	}

	public ArrayList<AlbumHandler> getList() {
		return list;
	}
	
	
}
