package com.code.aon.ui.cms.velocity.attribute;

import java.util.ArrayList;

import com.code.aon.cms.DirectAccessGroupDetail;
import com.code.aon.ui.cms.velocity.DirectAccessGenerator;

public class DirectAccessGroupHandler {

	private String label;
	
	private ArrayList<DirectAccessHandler> list;
	
	public DirectAccessGroupHandler (DirectAccessGroupDetail group) {
		label = group.getLabel();
		list = DirectAccessGenerator.getDirectAccessList(group);
	}


	public String getLabel() {
		return label;
	}


	public ArrayList<DirectAccessHandler> getList() {
		return list;
	}

}
