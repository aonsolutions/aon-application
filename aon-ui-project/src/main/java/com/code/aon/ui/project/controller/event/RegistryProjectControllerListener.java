package com.code.aon.ui.project.controller.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.project.Project;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.project.controller.IProjectConstants;
import com.esferalia.aon.entity.IEntityAlias;

public class RegistryProjectControllerListener extends ControllerAdapter {
	
	private final static Logger LOGGER = LoggerFactory.getLogger(RegistryProjectControllerListener.class);
	
	private String backAction;
	
	
	public String getBackAction() {
		return backAction;
	}
	public void setBackAction(String backAction) {
		this.backAction = backAction;
	}

	@Override
	public void afterBeanSelected(ControllerEvent event) throws ControllerListenerException {
		try {
			Project project = (Project) event.getController().getTo();
			BasicController c = (BasicController) FormUtil.getController(IProjectConstants.PROJECT_CONTROLLER_NAME);
			c.onEditSearch(null);
			c.getCriteria().addEqualExpression(c.getFieldName(IEntityAlias.PROJECT_ID), project.getId());
			c.onSearch(null);
			c.onSelect(null);
			c.setBackAction( getBackAction() );
		} catch (ManagerBeanException e) {
			String msg = "No se pudo seleccionar el proyecto.";
			LOGGER.error(msg,e);
			throw new ControllerListenerException(msg,e);
		}
	}
	
}