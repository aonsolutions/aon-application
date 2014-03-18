package com.code.aon.ui.fiscal.event;

import com.code.aon.AonVersion;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.fiscal.Mod349;
import com.code.aon.fiscal.enumeration.Mod349Status;
import com.code.aon.fiscal.mod349.Mod349Manager;
import com.code.aon.fiscal.mod349.Mod349Parameters;
import com.code.aon.ui.fiscal.controller.mod349.Mod349Controller;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;

public class Mod349ControllerListener extends ControllerAdapter {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	@Override
	public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
		Mod349Controller c = (Mod349Controller) event.getController();

		Mod349Parameters params = new Mod349Parameters(AonUtil.getDomainName());
		params.initialize();
		c.setParams(params);
		
		c.setFileOutput(null);
		Mod349 mod349= (Mod349) c.getTo();
		mod349.setAdministration(c.getFiscalParams().getDefaultAdministration());
		mod349.setStatus( Mod349Status.PENDING);
		mod349.setSecurityLevel(SecurityLevel.OFFICIAL);
		mod349.setGenerateLines(true);
	}
	
	@Override
	public void afterBeanAdded(ControllerEvent event) throws ControllerListenerException {
		try {
			Mod349Controller c = (Mod349Controller) event.getController();
			Mod349Parameters params = c.getParams();
			c.setFileOutput(null);
			Mod349 mod349= (Mod349) c.getTo();
			if (mod349.isGenerateLines()) {
				Mod349Manager manager = new Mod349Manager();
				params.setMod349(mod349);
				manager.generateDetails(params);
			}
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException("Imposible generar la declaración",e);
		}
	}

	@Override
	public void afterBeanSelected(ControllerEvent event) throws ControllerListenerException {
		Mod349Controller c = (Mod349Controller) event.getController();
		c.setFileOutput(null);
	}

	@Override
	public void afterBeanCanceled(ControllerEvent event) throws ControllerListenerException {
		Mod349Controller c = (Mod349Controller) event.getController();
		c.setFileOutput(null);
	}

	@Override
	public void afterBeanRemoved(ControllerEvent event) throws ControllerListenerException {
		Mod349Controller c = (Mod349Controller) event.getController();
		c.setFileOutput(null);
	}

	@Override
	public void afterBeanReset(ControllerEvent event) throws ControllerListenerException {
		Mod349Controller c = (Mod349Controller) event.getController();
		c.setFileOutput(null);
	}

	@Override
	public void afterBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		Mod349Controller c = (Mod349Controller) event.getController();
		c.setFileOutput(null);
	}
}
