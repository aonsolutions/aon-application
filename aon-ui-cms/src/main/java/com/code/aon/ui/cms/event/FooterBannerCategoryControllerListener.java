package com.code.aon.ui.cms.event;

import com.code.aon.cms.FooterBannerCategory;
import com.code.aon.ui.cms.controller.FooterBannerCategoryController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class FooterBannerCategoryControllerListener extends ControllerAdapter {

	
	@Override
	public void afterBeanCreated(ControllerEvent event)
			throws ControllerListenerException {
		FooterBannerCategoryController c = (FooterBannerCategoryController)event.getController();
		FooterBannerCategory fbc = (FooterBannerCategory)event.getController().getTo();
		fbc.setFooter(c.getCurrentFooter());
	}
	
}
