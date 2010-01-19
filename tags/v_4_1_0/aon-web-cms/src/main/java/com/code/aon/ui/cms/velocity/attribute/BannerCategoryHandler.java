package com.code.aon.ui.cms.velocity.attribute;

import java.util.ArrayList;

import com.code.aon.cms.BannerCategoryDetail;
import com.code.aon.ui.cms.velocity.BannerGenerator;

public class BannerCategoryHandler {

	private String label;
	
	private ArrayList<BannerHandler> list;
	
	public BannerCategoryHandler (BannerCategoryDetail group) {
		label = group.getLabel();
		list = BannerGenerator.getBannerList(group);
	}


	public String getLabel() {
		return label;
	}


	public ArrayList<BannerHandler> getList() {
		return list;
	}

}
