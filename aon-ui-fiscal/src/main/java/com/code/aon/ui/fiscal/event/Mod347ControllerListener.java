package com.code.aon.ui.fiscal.event;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.fiscal.Mod347;
import com.code.aon.fiscal.enumeration.Mod347Status;
import com.code.aon.fiscal.mod347.Mod347Manager;
import com.code.aon.fiscal.mod347.Mod347Parameters;
import com.code.aon.ui.fiscal.controller.mod347.Mod347Controller;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;

public class Mod347ControllerListener extends ControllerAdapter {

	@Override
	public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
		Mod347Controller c = (Mod347Controller) event.getController();

		Mod347Parameters params = new Mod347Parameters(AonUtil.getDomainName());
		params.initialize();
		c.setParams(params);
		
		c.setFileOutput(null);
		Mod347 mod347= (Mod347) c.getTo();
		mod347.setAdministration(c.getFiscalParams().getDefaultAdministration());
		mod347.setStatus( Mod347Status.PENDING);
		mod347.setSecurityLevel(SecurityLevel.OFFICIAL);
		mod347.setMinimumAmount(Mod347.MINIMUM_AMOUNT);
		mod347.setGenerateLines(true);
	}
	
	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		Mod347Controller c = (Mod347Controller) event.getController();
		Mod347 mod347= (Mod347) c.getTo();
		if (mod347.isGenerateLines() && mod347.getMinimumAmount() < 0) {
			throw new ControllerListenerException("El importe mínimo ha de ser un número positivo.");
		}
	}


	@Override
	public void afterBeanAdded(ControllerEvent event) throws ControllerListenerException {
		try {
			Mod347Controller c = (Mod347Controller) event.getController();
			Mod347Parameters params = c.getParams();
			c.setFileOutput(null);
			Mod347 mod347= (Mod347) c.getTo();
			if (mod347.isGenerateLines()) {
				Mod347Manager manager = new Mod347Manager();
				params.setMod347(mod347);
				manager.generateDetails(params);
			}
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException("Imposible generar la declaración: " + e.getMessage(),e);
		}
	}

	@Override
	public void afterBeanSelected(ControllerEvent event) throws ControllerListenerException {
		Mod347Controller c = (Mod347Controller) event.getController();
		c.setFileOutput(null);
	}

	@Override
	public void afterBeanCanceled(ControllerEvent event) throws ControllerListenerException {
		Mod347Controller c = (Mod347Controller) event.getController();
		c.setFileOutput(null);
	}

	@Override
	public void afterBeanRemoved(ControllerEvent event) throws ControllerListenerException {
		Mod347Controller c = (Mod347Controller) event.getController();
		c.setFileOutput(null);
	}

	@Override
	public void afterBeanReset(ControllerEvent event) throws ControllerListenerException {
		Mod347Controller c = (Mod347Controller) event.getController();
		c.setFileOutput(null);
	}

	@Override
	public void afterBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		Mod347Controller c = (Mod347Controller) event.getController();
		c.setFileOutput(null);
	}
}
