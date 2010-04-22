package com.code.aon.ui.cms.event;

import com.code.aon.cms.SportCareerPath;
import com.code.aon.ui.cms.controller.SportCareerPathController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class SportCareerPathControllerListener extends ControllerAdapter {

	@Override
	public void afterBeanCreated(ControllerEvent event)
			throws ControllerListenerException {
		SportCareerPathController c = (SportCareerPathController)event.getController();
		SportCareerPath sportCareerPath = (SportCareerPath) c.getTo();
		sportCareerPath.setSportPlayer(c.getSportPlayer());
	}

}
