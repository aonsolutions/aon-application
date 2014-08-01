package com.code.aon.ui.groupware.report;

import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.ICollectionProvider;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.groupware.TaskHolder;
import com.code.aon.groupware.enumeration.DailyTrackingReportType;
import com.code.aon.groupware.report.dailyTracking.DailyTrackingReportEngine;
import com.code.aon.groupware.report.dailyTracking.DailyTrackingReportParams;
import com.code.aon.registry.Registry;
import com.code.aon.report.OutputFormat;
import com.code.aon.ui.common.components.LookupChangeEvent;
import com.code.aon.ui.groupware.GroupwareUtils;
import com.code.aon.ui.groupware.controller.GroupWareCollectionsController;
import com.code.aon.ui.groupware.controller.IGroupWareConstants;
import com.code.aon.ui.project.controller.IProjectConstants;
import com.code.aon.ui.project.controller.ProjectCollectionsController;
import com.code.aon.ui.report.controller.ReportManager;
import com.code.aon.ui.util.AonUtil;

public class DailyTrackingReportController implements ICollectionProvider, Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private final static Logger LOGGER = LoggerFactory.getLogger(DailyTrackingReportController.class);
	
	private DailyTrackingReportParams params;
	private boolean monitor;
	private  List<SelectItem> projects;
	private  List<SelectItem> activityTypes;
	
	private GroupwareUtils groupwareUtils;
	
	public GroupwareUtils getGroupwareUtils() {
		if (groupwareUtils == null) {
			groupwareUtils = new GroupwareUtils();
		}
		return groupwareUtils;
	}

	public DailyTrackingReportParams getParams() {
		return params;
	}
	public void setParams(DailyTrackingReportParams params) {
		this.params = params;
	}

	public boolean isMonitor() {
		return monitor;
	}
	public void setMonitor(boolean monitor) {
		this.monitor = monitor;
	}

	public void onResetReportSearch(ActionEvent event) {
		try {
			if (!isMonitor()) {
				TaskHolder taskHolder = getGroupwareUtils().getCurrentTaskHolder();
				if (taskHolder == null) {
					String msg = "No existe un operario vinculado a la cuenta de acceso. Cree un operario y vincule la cuenta de acceso.";
					AonUtil.addErrorMessage(msg);
					throw new AbortProcessingException(msg);
				}
			}
			
			setParams( new DailyTrackingReportParams() );
			getParams().setRegistry( (Registry) BeanManager.getManagerBean(Registry.class).createNewTo());
			loadProjects(null);
			loadActivityTypes(null);
		} catch (ManagerBeanException e) {
			String msg = "Error al inicializar los parámetros para el informe.";
			LOGGER.error(msg, e);
			throw new AbortProcessingException(msg,e);
		}
	}
	
	private void loadProjects(Integer registryId) throws ManagerBeanException {
		ProjectCollectionsController pcc = (ProjectCollectionsController) 
		AonUtil.getRegisteredBean( IProjectConstants.PROJECT_COLLECTIONS_CONTROLLER_NAME );
		projects = pcc.getProjects( registryId);
	}

	private void loadActivityTypes(Integer projectTypeId) throws ManagerBeanException {
		ProjectCollectionsController pcc = (ProjectCollectionsController) 
			AonUtil.getRegisteredBean( IProjectConstants.PROJECT_COLLECTIONS_CONTROLLER_NAME );
		if (projectTypeId != null) {
			activityTypes = pcc.getActivityTypes( projectTypeId );
		} else {
			activityTypes = new LinkedList<SelectItem>();	
		}
	}

	public void onRegistryChanged(LookupChangeEvent event) {
		try {
			Registry registry = (Registry) event.getNewValue();
			if (registry == null || registry.getId() == null) {
				getParams().setProject(null);
				loadProjects(null);
			} else {
				loadProjects(registry.getId());
			}
			Integer projectTypeId = null;
			if (getParams().getProject() != null && getParams().getProject().getProjectType() != null) {
				projectTypeId = getParams().getProject().getProjectType().getId();	
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
			if (getParams().getProject() != null) {
				registry = getParams().getProject().getRegistry();
			} else {
				registry = (Registry) BeanManager.getManagerBean(Registry.class).createNewTo();
			}
			getParams().setRegistry(registry);
			LookupChangeEvent e = new LookupChangeEvent(event.getComponent(), registry);
			onRegistryChanged(e);
			Integer projectTypeId = null;
			if (getParams().getProject() != null && getParams().getProject().getProjectType() != null) {
				projectTypeId = getParams().getProject().getProjectType().getId();	
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

	public List<SelectItem> getActivityTypes() throws ManagerBeanException {
		return activityTypes;
	}
	
	public List<SelectItem> getTaskHolderWorkgroups() throws ManagerBeanException {
		GroupWareCollectionsController gcc = (GroupWareCollectionsController) 
			AonUtil.getRegisteredBean( IGroupWareConstants.GROUPWARE_COLLECTIONS_CONTROLLER_NAME);
		return gcc.getTaskHolderWorkgroups(getParams().getWorkGroup());
	}

	public Collection<?> getCollection(boolean forceRefresh) throws ManagerBeanException {
		return getCollection();
	}

	public Collection<?> getCollection() {
		DailyTrackingReportEngine engine = new DailyTrackingReportEngine();
		return engine.getReportCollection(getParams());
	}


	private OutputFormat getOutputFormat() {
		if (DailyTrackingReportType.REPORT == getParams().getReportType()) {
			return OutputFormat.XLS;
		}
		return OutputFormat.PDF;
	}

	public String onReport() {
		try {
			if (!isMonitor()) {
					getParams().setTaskHolder( getGroupwareUtils().getCurrentTaskHolder() );
			}
			ReportManager manager = (ReportManager) AonUtil.getRegisteredBean("report");
			manager.setReportKey(getParams().getReportType().getReportKey());
			manager.setOutputFormat(getOutputFormat() == null ? OutputFormat.PDF : getOutputFormat());
			String outcome = manager.onExecute();
			return outcome;
		} catch (Throwable e) {
			String msg = "Error al ejecutar el listado. " + e.getMessage();
			LOGGER.error(msg, e);
			throw new AbortProcessingException(msg,e);
		}			
	}

}
