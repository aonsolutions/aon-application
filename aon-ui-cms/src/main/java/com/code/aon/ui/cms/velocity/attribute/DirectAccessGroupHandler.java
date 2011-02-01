package com.code.aon.ui.cms.velocity.attribute;

import java.util.ArrayList;

import com.code.aon.cms.DirectAccessGroupDetail;

public class DirectAccessGroupHandler {

	private int groupId;
	
	private String label;
	
	private ArrayList<DirectAccessHandler> list;
	
	public DirectAccessGroupHandler (DirectAccessGroupDetail group, ArrayList<DirectAccessHandler> list) {
		this.groupId = group.getDirectAccessGroup().getId();
		this.label = group.getLabel();
		this.list = list;
	}


	public String getLabel() {
		return label;
	}


	public ArrayList<DirectAccessHandler> getList() {
		return list;
	}


}
