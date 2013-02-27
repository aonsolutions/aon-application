package com.code.aon.ui.product.controller;

import javax.faces.event.ActionEvent;

import org.apache.commons.lang.StringUtils;

import com.code.aon.product.ProductCategory;
import com.code.aon.ui.form.BasicController;

public class ProductCategoryController extends BasicController {

	private boolean showPosInfo;

	public boolean isShowPosInfo() {
		return showPosInfo;
	}

	public void setShowPosInfo(boolean showPosInfo) {
		this.showPosInfo = showPosInfo;
	}

	@Override
	public void onAccept(ActionEvent event) {
		ProductCategory pc = (ProductCategory) getTo();
		if ( !StringUtils.isEmpty(pc.getDetail2()) && StringUtils.isEmpty(pc.getDetail()) ) {
			pc.setDetail(pc.getDetail2());
			pc.setDetail2(null);
		}
		super.onAccept(event);
	}
	
}