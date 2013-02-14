package com.code.aon.ui.asset.event;

import com.code.aon.ui.asset.controller.ActivityLinesController;
import com.code.aon.ui.asset.controller.IAssetConstants;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class AssetControllerListener extends ControllerAdapter {

	//private static final Logger LOGGER = Logger.getLogger(AssetControllerListener.class.getName());

	@Override
	public void beforeBeanSelected(ControllerEvent event)
			throws ControllerListenerException {
		((ActivityLinesController)FormUtil.getController(IAssetConstants.ACTIVITY_LINES_CONTROLLER_NAME)).initializeDates();
	}
}
