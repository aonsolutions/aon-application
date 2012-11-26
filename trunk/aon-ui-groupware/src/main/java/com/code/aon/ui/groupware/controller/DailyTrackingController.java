package com.code.aon.ui.groupware.controller;

import java.util.LinkedList;
import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.BeanManager;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.groupware.DailyTracking;
import com.code.aon.registry.Registry;
import com.code.aon.ui.common.components.LookupChangeEvent;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.project.controller.IProjectConstants;
import com.code.aon.ui.project.controller.ProjectCollectionsController;
import com.code.aon.ui.util.AonUtil;

public class DailyTrackingController extends BasicController {

	
	private final static Logger LOGGER = LoggerFactory.getLogger(DailyTrackingController.class);

	private boolean monitor;
	private  List<SelectItem> projects;
	private  List<SelectItem> activityTypes;

	public boolean isMonitor() {
		return monitor;
	}
	public void setMonitor(boolean monitor) {
		this.monitor = monitor;
	}
	public void onSwicthMonitor(ActionEvent event) {
		onRefresh(event);
	}

	public void onRefresh(ActionEvent event) {
		super.onSearch(event);
	}

	public void onRegistryChanged(LookupChangeEvent event) {
		try {
			Registry registry = (Registry) event.getNewValue();
			if (registry == null || registry.getId() == null) {
				getCurrent().setProject(null);
				loadProjects(null);
			} else {
				loadProjects(registry.getId());
			}
			Integer projectTypeId = null;
			if (getCurrent() != null && getCurrent().getProject() != null && getCurrent().getProject().getProjectType() != null) {
				projectTypeId = getCurrent().getProject().getProjectType().getId();	
			}
			loadActivityTypes(projectTypeId);
		} catch (ManagerBeanException e) {
			String msg = "Error al inicializar la lista de proyectos.";
			LOGGER.error(msg, e);
			throw new AbortProcessingException(msg,e);
		}
	}
	
	public void onChangeProject(ActionEvent event) {
		try {
			Registry registry = null;
			if (getCurrent().getProject() != null) {
				registry = getCurrent().getProject().getRegistry();
			} else {
				registry = (Registry) BeanManager.getManagerBean(Registry.class).createNewTo();
			}
			getCurrent().setRegistry(registry);
			LookupChangeEvent e = new LookupChangeEvent(event.getComponent(), registry);
			onRegistryChanged(e);
			Integer projectTypeId = null;
			if (getCurrent() != null && getCurrent().getProject() != null && getCurrent().getProject().getProjectType() != null) {
				projectTypeId = getCurrent().getProject().getProjectType().getId();	
			}
			loadActivityTypes(projectTypeId);
		} catch (ManagerBeanException e) {
			String msg = "Error al inicializar registry.";
			LOGGER.error(msg, e);
			throw new AbortProcessingException(msg,e);
		}
	}
	
	public List<SelectItem> getProjects() throws ManagerBeanException {
		if (projects == null) {
			projects = new LinkedList<SelectItem>();
		}
		return projects;
	}

	public void loadProjects(Integer registryId) throws ManagerBeanException {
		ProjectCollectionsController pcc = (ProjectCollectionsController) 
		AonUtil.getRegisteredBean( IProjectConstants.PROJECT_COLLECTIONS_CONTROLLER_NAME );
		projects = pcc.getProjects( registryId);
	}
	
	public List<SelectItem> getActivityTypes() throws ManagerBeanException {
		return activityTypes;
	}
	public void loadActivityTypes(Integer projectTypeId) throws ManagerBeanException {
		ProjectCollectionsController pcc = (ProjectCollectionsController) 
			AonUtil.getRegisteredBean( IProjectConstants.PROJECT_COLLECTIONS_CONTROLLER_NAME );
		if (projectTypeId != null) {
			activityTypes = pcc.getActivityTypes( projectTypeId );
		} else {
			activityTypes = new LinkedList<SelectItem>();	
		}
	}
	
	private DailyTracking getCurrent() {
		return (DailyTracking) getTo();
	}
		
}