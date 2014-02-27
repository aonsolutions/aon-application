package com.code.aon.ui.fiscal.event;

import com.code.aon.fiscal.Mod347Detail;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class Mod347ControllerDetailAssetListener  extends ControllerAdapter {

	@Override
	public void beforeBeanCreated(ControllerEvent event)
			throws ControllerListenerException {
		IController c = event.getController();
		Mod347Detail  detail = (Mod347Detail) c.getTo();
		detail.setAssetLocation("1");
	}
	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		IController c = event.getController();
		Mod347Detail  detail = (Mod347Detail) c.getTo();
		detail.setSheet("I");
	}
	
}
