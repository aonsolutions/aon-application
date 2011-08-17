package com.esferalia.aon.ui.payroll.event.contract;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.form.LinesController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.esferalia.aon.payroll.Mod145;

public class Mod145DescendientsControllerListener extends ControllerAdapter{
	
	private static final Logger LOGGER = LoggerFactory.getLogger(Mod145DescendientsControllerListener.class.getName());
	
	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		LinesController controller = (LinesController) this.getController();
		try {
			if(controller.getRowCount()==0){
				Mod145 m = (Mod145) controller.getMasterController().getTo();
				m.setDescendientCount(null);
				controller.getManagerBean().update(m);
			}
		} catch (ManagerBeanException e) {
			String msg = "Error al actualizar el modelo 145";
			LOGGER.error(msg);
		}
	}
		
}
