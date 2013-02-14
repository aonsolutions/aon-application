package com.code.aon.ui.product.controller;

import static com.code.aon.ui.product.controller.IItemConstants.PRODUCT_CATEGORY;
import static com.code.aon.ui.product.controller.IItemConstants.SHOW_CATEGORY_GROUP;

import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.util.AonUtil;

public class ProductCategoryController extends BasicController {

	private boolean showPosInfo;

	public boolean isShowPosInfo() {
		return showPosInfo;
	}

	public void setShowPosInfo(boolean showPosInfo) {
		this.showPosInfo = showPosInfo;
	}
	
	public boolean isShowCategoryGroup() {
		if (! showPosInfo ) {
			return AonUtil.isBeanValue(PRODUCT_CATEGORY, SHOW_CATEGORY_GROUP);
		}
		return true;
	}
}