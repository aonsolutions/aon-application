package com.code.aon.ui.cms.event;

import com.code.aon.cms.Banner;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.cms.controller.BannerController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class BannerControllerListener extends ControllerAdapter {
	
	@Override
	public void afterBeanRemoved(ControllerEvent event)
			throws ControllerListenerException {
		BannerController controller = (BannerController) event.getController();
		try {
			controller.orderedControllerSupport.reorderObjects(controller);
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e);
		}
	}
	
	@Override
	public void afterModelInitialized(ControllerEvent event)
			throws ControllerListenerException {
		BannerController controller = (BannerController)event.getController();
		controller.orderedControllerSupport.addListenerSupport(controller);
	}
	
	@Override
	public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
		Banner banner = (Banner)event.getController().getTo();
		banner.setActive(true);
	}

	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		BannerController lc = (BannerController)event.getController();
		Banner b = (Banner)event.getController().getTo();
		b.setBannerCategory(lc.getCurrentBannerCategory());
		b.setPosition(lc.orderedControllerSupport.getLastPosition(lc));
	}
	
}