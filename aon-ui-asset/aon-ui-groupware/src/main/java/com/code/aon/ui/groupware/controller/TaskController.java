package com.code.aon.ui.groupware.controller;

import static com.code.aon.ui.common.ICommonMessages.GROUPWARE_TASK_FINALIZATION_PANEL;

import java.io.IOException;
import java.sql.Types;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.groupware.ProcessDetailTransition;
import com.code.aon.groupware.Task;
import com.code.aon.groupware.TaskHolder;
import com.code.aon.groupware.TaskHolderWorkgroup;
import com.code.aon.groupware.enumeration.TaskSource;
import com.code.aon.groupware.task.TaskManager;
import com.code.aon.jaas.auth.AuthPrincipal;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.Registry;
import com.code.aon.report.ReportException;
import com.code.aon.report.poi.ExcelReportExporter;
import com.code.aon.report.poi.IReportExporter;
import com.code.aon.report.poi.ReportColumnMetadata;
import com.code.aon.report.poi.ReportMetadata;
import com.code.aon.ui.common.components.LookupChangeEvent;
import com.code.aon.ui.config.controller.ConfigCollectionsController;
import com.code.aon.ui.config.controller.ConfigConstants;
import com.code.aon.ui.config.controller.DomainSwitcher;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.project.controller.IProjectConstants;
import com.code.aon.ui.project.controller.ProjectCollectionsController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.watson.util.AonStringUtils;

public class TaskController extends BasicController {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private final static Logger LOGGER = LoggerFactory.getLogger(TaskController.class);

	private boolean showAuditInfoWindow;
	private TaskHolder currentTaskHolder;
	
	private TaskManager taskManager;
	private boolean allMembers;
	private boolean monitor;
	private boolean finishPanelVisible = false;
	private List<SelectItem> transitions;
	private ProcessDetailTransition transitionSelected;
	private  List<SelectItem> projects;
	private  List<SelectItem> activityTypes;

	public boolean isShowAuditInfoWindow() {
		return showAuditInfoWindow;
	}

	public void setShowAuditInfoWindow(boolean showAuditInfoWindow) {
		this.showAuditInfoWindow = showAuditInfoWindow;
	}	
	
	public boolean isParentDomain() {
		DomainSwitcher ds = (DomainSwitcher) AonUtil.getRegisteredBean( ConfigConstants.DOMAIN_SWITCHER );
		return( ds != null && !ds.isChildDomain() && ds.isDomainManagementAvailable());
	}

	public boolean isMonitor() {
		return monitor;
	}
	public void setMonitor(boolean monitor) {
		this.monitor = monitor;
	}
	
	public boolean isAllMembers() {
		return allMembers;
	}
	public void setAllMembers(boolean allMembers) {
		this.allMembers = allMembers;
	}

	public boolean isFinishPanelVisible() {
		return finishPanelVisible;
	}
	public void setFinishPanelVisible(boolean finishPanelVisible) {
		this.finishPanelVisible = finishPanelVisible;
	}

	public List<SelectItem> getProjects() throws ManagerBeanException {
		if (projects == null) {
			projects = new LinkedList<SelectItem>();
		}
		return projects;
	}

	public List<SelectItem> getTransitions() {
		try {
			if (transitions == null) {
				initializeTransitions();
			}
		} catch (ManagerBeanException e) {
			String msg = "No se puede mostrar la lista de transiciones disponibles. " + e.getMessage();
			LOGGER.error(msg);
			AonUtil.addErrorMessage(msg);
		}
		return transitions;
	}
	public void setTransitions(List<SelectItem> transitions) {
		this.transitions = transitions;
	}
	
	public ProcessDetailTransition getTransitionSelected() {
		return transitionSelected;
	}
	public void setTransitionSelected(ProcessDetailTransition transitionSelected) {
		this.transitionSelected = transitionSelected;
	}
	
	public TaskHolder getCurrentTaskHolder() {
		if (currentTaskHolder == null) {
			try {
		        AuthPrincipal principal = AonUtil.getAuthPrincipal();
		        if( principal.getUserId() == null){
		        	throw new IllegalStateException("No es posible encontrar el usuario actual");
		        }
				IManagerBean bean = BeanManager.getManagerBean(TaskHolder.class);
				Criteria criteria = new Criteria();
				criteria.addEqualExpression(bean.getFieldName(IEntityAlias.TASK_HOLDER_USER_ID), principal.getUserId());
				if (isParentDomain()) {
					criteria.setSkipDomainFilter(true);
				}
				List<ITransferObject> list = bean.getList(criteria);
				if (list != null && list.size() > 0) {
					currentTaskHolder = (TaskHolder) list.get(0);
				} 
			} catch (ManagerBeanException e) {
				// currentTaskHolder remains null.
			}
		}
		return currentTaskHolder;
	}
	
	public TaskManager getTaskManager() {
		if (taskManager == null) {
			taskManager = new TaskManager();
		}
		return taskManager;
	}

	public void onSwicthMonitor(ActionEvent event) {
		onLaunch(event);
	}
	public void onLaunch(ActionEvent event) {
		onEditSearch(event);
		onSearch(event);
	}
	public void onFilter(ActionEvent event) throws ManagerBeanException {
		setCriteria(new Criteria());
		onSearch(event);
	}
	
	public void onStartTaskFromList(ActionEvent event) {
		try {
			Task task = (Task) model.getRowData();
			getTaskManager().startTask(getCurrentTaskHolder(), task );
			onSearch(event);
		} catch (ManagerBeanException e) {
			String msg = "No se puede empezar la tarea. " + e.getMessage();
			LOGGER.error(msg);
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
	}
	public void onStartTask(ActionEvent event) {
		try {
			Task task = (Task) getTo();
			task = getTaskManager().startTask(getCurrentTaskHolder(), task );
			setTo(task);
		} catch (ManagerBeanException e) {
			String msg = "No se puede empezar la tarea. " + e.getMessage();
			LOGGER.error(msg);
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
	}
	
	public void onStopTaskFromList(ActionEvent event) {
		try {
			Task task = (Task) model.getRowData();
			getTaskManager().stopTask(getCurrentTaskHolder(), task );
			onSearch(event);
		} catch (ManagerBeanException e) {
			String msg = "No se puede parar la tarea. " + e.getMessage();
			LOGGER.error(msg);
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
	}
	public void onStopTask(ActionEvent event) {
		try {
			Task task = (Task) getTo();
			task = getTaskManager().stopTask(getCurrentTaskHolder(), task );
			setTo(task);
		} catch (ManagerBeanException e) {
			String msg = "No se puede parar la tarea. " + e.getMessage();
			LOGGER.error(msg);
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
	}
	public void onReopenTaskFromList(ActionEvent event) {
		try {
			Task task = (Task) model.getRowData();
			getTaskManager().reopenTask(getCurrentTaskHolder(), task );
			onSearch(event);
		} catch (ManagerBeanException e) {
			String msg = "No se puede abrir la tarea. " + e.getMessage();
			LOGGER.error(msg);
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
	}
	public void onReopenTask(ActionEvent event) {
		try {
			Task task = (Task) getTo();
			task = getTaskManager().reopenTask(getCurrentTaskHolder(), task );
			setTo(task);
		} catch (ManagerBeanException e) {
			String msg = "No se puede abrir la tarea. " + e.getMessage();
			LOGGER.error(msg);
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
	}

	public void onAssumeTaskFromList(ActionEvent event) {
		try {
			Task task = (Task) model.getRowData();
			getTaskManager().assumeTask(getCurrentTaskHolder(), task );
			onSearch(event);
		} catch (ManagerBeanException e) {
			String msg = e.getMessage();
			LOGGER.error(msg, e);
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
	}
	
	public void onAssumeTask(ActionEvent event) {
		try {
			Task task = (Task) getTo();
			task = getTaskManager().assumeTask(getCurrentTaskHolder(), task );
			setTo(task);
		} catch (ManagerBeanException e) {
			String msg = e.getMessage();
			LOGGER.error(msg, e);
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
	}
	
	public void onReleaseTaskFromList(ActionEvent event) {
		try {
			Task task = (Task) model.getRowData();
			getTaskManager().releaseTask(getCurrentTaskHolder(), task );
			onSearch(event);
		} catch (ManagerBeanException e) {
			String msg = e.getMessage();
			LOGGER.error(msg, e);
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
	}

	public void onReleaseTask(ActionEvent event) {
		try {
			Task task = (Task) getTo();
			task = getTaskManager().releaseTask(getCurrentTaskHolder(), task );
			setTo(task);
		} catch (ManagerBeanException e) {
			String msg = e.getMessage();
			LOGGER.error(msg, e);
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
	}

	public void onFinishTaskFromList(ActionEvent event) {
		onSelect(event);
		onFinishTask(event);
		if (!isFinishPanelVisible()) {
			onSearch(event);
		}
	}
	public void onFinishTask(ActionEvent event) {
		try {
			Task task = (Task) getTo();
			setTransitions(null);
			setTransitionSelected( new ProcessDetailTransition() );
			setFinishPanelVisible(false);
			if (task.isSourceProcess() && getTaskManager().hasTransitions(task)) {
				setFinishPanelVisible(true);
			} else {
				finishTask(task);
			}
		} catch (ManagerBeanException e) {
			String msg = e.getMessage();
			LOGGER.error(msg, e);
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg,e);
		}
	}
	
	public void onFinishTransitionTask(ActionEvent event) {
		try {
			Task task = (Task) this.getTo();
			if (getTransitionSelected() != null && getTransitionSelected().getId() == null) {
				// FinalizaciÃ³n sin incidencias.
				setTransitionSelected(null);
			}
			finishTask(task);
			setTransitions(null);
			setFinishPanelVisible(false);
			onSearch(null);
		} catch (ManagerBeanException e) {
			LOGGER.error(e.getMessage(), e);
			addMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage());
		}
	}
	
	private Task finishTask(Task task) throws ManagerBeanException {
		getManagerBean().restoreNullSubPOJOs(task);
		task = getTaskManager().finishTask(getCurrentTaskHolder(), task, getTransitionSelected() );
		getManagerBean().initializePOJO(task);
		return task;
	}

	public boolean isMyTaskFromList() {
		try {
			return ((Task) this.getModel().getRowData()).isMine(getCurrentTaskHolder());
		} catch (ManagerBeanException e) {
			LOGGER.error("Imposible identificar de quien es la tarea.", e);
		}
		return false;
	}
	public boolean isMyTask() {
		return ((Task) getTo()).isMine(getCurrentTaskHolder());
	}
	
	private void initializeTransitions() throws ManagerBeanException {
		Task task = (Task) getTo();
		transitions = new LinkedList<SelectItem>();
		if (task != null) {
			transitions.add(new SelectItem(new ProcessDetailTransition(), AonUtil.getMessage(GROUPWARE_TASK_FINALIZATION_PANEL)));				
			for (ProcessDetailTransition pdt : getTaskManager().getTransitions(task)) {
				String description = pdt.getProcessTransitionType().getDescription();
				SelectItem item = new SelectItem(pdt, description);
				transitions.add(item);				
			}
		}
	}
	public List<SelectItem> getWorkgroups() {
		try {
			TaskHolder taskHolder = ((Task) getTo()).getTaskHolder();
			if (taskHolder == null || taskHolder.getId() == null) {
				ConfigCollectionsController ccc = (ConfigCollectionsController) 
					AonUtil.getRegisteredBean( IGroupWareConstants.CONFIG_COLLECTIONS_CONTROLLER_NAME);
				return ccc.getWorkgroups();	
			} else {
				GroupWareCollectionsController gcc = (GroupWareCollectionsController) 
					AonUtil.getRegisteredBean( IGroupWareConstants.GROUPWARE_COLLECTIONS_CONTROLLER_NAME);
				return gcc.getTaskHolderWorkgroups(taskHolder);
			}
		} catch (ManagerBeanException e) {
			LOGGER.error("Imposible mostar la lista de grupos de usuarios");
			addMessage(e.getMessage());
			return new LinkedList<SelectItem>();
		}
	}
	public List<SelectItem> getTaskHolderWorkgroups() {
		try {
			GroupWareCollectionsController gcc = (GroupWareCollectionsController) 
			AonUtil.getRegisteredBean( IGroupWareConstants.GROUPWARE_COLLECTIONS_CONTROLLER_NAME);
			return gcc.getTaskHolderWorkgroups(((Task) getTo()).getWorkGroup());
		} catch (ManagerBeanException e) {
			LOGGER.error("Imposible mostar la lista de usuarios");
			addMessage(e.getMessage());
			return new LinkedList<SelectItem>();
		}
	}

	public void onRegistryChanged(LookupChangeEvent event) {
		try {
			Task task = (Task) getTo();
			Registry registry = (Registry) event.getNewValue();
			if (registry == null || registry.getId() == null) {
				task.setProject(null);
				loadProjects(null);
			} else {
				loadProjects(registry.getId());
			}
			Integer projectTypeId = null;
			if (task.getProject() != null && task.getProject().getProjectType() != null) {
				projectTypeId = task.getProject().getProjectType().getId();	
			}
			loadActivityTypes(projectTypeId);
		} catch (ManagerBeanException e) {
			String msg = "Error al inicializar la lista de proyectos.";
			LOGGER.error(msg, e);
			throw new AbortProcessingException(msg,e);
		}
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
	
	public void onChangeProject(ActionEvent event) {
		try {
			Task task = (Task) getTo();
			Registry registry = null;
			if (task.getProject() != null) {
				registry = task.getProject().getRegistry();
			} else {
				registry = (Registry) BeanManager.getManagerBean(Registry.class).createNewTo();
			}
			task.setRegistry(registry);
			LookupChangeEvent e = new LookupChangeEvent(event.getComponent(), registry);
			onRegistryChanged(e);
			Integer projectTypeId = null;
			if (task.getProject() != null && task.getProject().getProjectType() != null) {
				projectTypeId = task.getProject().getProjectType().getId();	
			}
			loadActivityTypes(projectTypeId);
		} catch (ManagerBeanException e) {
			String msg = "Error al inicializar registry.";
			LOGGER.error(msg, e);
			throw new AbortProcessingException(msg,e);
		}
	}

	public void saveTask(ActionEvent event) {
		try {
			if (!isAllMembers()) {
				super.accept(event);
			} else {
				Task task = (Task) getTo();
				prepareForInsert(task);
				forAllMembers(task);
				onSearch(event);
			}
		} catch (ManagerBeanException e) {
			String msg = "Error al grabar la tarea." + e.getMessage();
			LOGGER.error(msg, e);
			throw new AbortProcessingException(msg,e);
		}
	}

	private void forAllMembers(Task task) {
		try {
			Criteria criteria = new Criteria();
			IManagerBean managerBean = BeanManager.getManagerBean(TaskHolderWorkgroup.class);
			criteria.addEqualExpression(managerBean.getFieldName(IEntityAlias.TASK_HOLDER_WORKGROUP_WORK_GROUP_ID), task.getWorkGroup().getId());
			criteria.addEqualExpression(managerBean.getFieldName(IEntityAlias.TASK_HOLDER_WORKGROUP_TASK_HOLDER_ACTIVE), true);
			List<ITransferObject> list = managerBean.getList(criteria);
			int i = 0;
			for (ITransferObject to: list) {
				TaskHolderWorkgroup uw = (TaskHolderWorkgroup) to;
				task.setId(null);
				task.setTaskHolder(uw.getTaskHolder());
				setTo(task);
				getManagerBean().insert(task);
				i++;
			}
			AonUtil.addInfoMessage("" + i + " tarea(s) creadas.");
		} catch (ManagerBeanException e) {
			String msg = "Error al crear la tarea para los usuarios. [" + e.getMessage() + "]";
			LOGGER.error(msg, e);
			addMessage(msg);
			throw new AbortProcessingException(msg);
		}
	}

	private void prepareForInsert(Task task) throws ManagerBeanException {
		if (!isMyTask()) {
			task.setSource(TaskSource.ASSIGNED);
			task.setSender(getCurrentTaskHolder());
		}
		Date startDate = task.getStartDate();
		Date dueDate = task.getDueDate();
		if (dueDate.compareTo(startDate) < 0) {
			String msg = "Fecha Inicio no puede ser posterior a Fecha Vencimiento.";
			LOGGER.error(msg);
			addMessage(msg);
			throw new AbortProcessingException(msg);
		}
	}

	public void allMembersChanged(ActionEvent event) {
		Task t = (Task) getTo();
		if (isAllMembers()) {
			t.setTaskHolder(new TaskHolder());
		}
	}
	
	public String onExcel() {
		try {
			FacesContext context = FacesContext.getCurrentInstance();
			Locale locale = AonUtil.getCurrentLocale();
			HttpServletResponse response = (HttpServletResponse) context.getExternalContext().getResponse();
			String fileName = "BandejaTareas";
			response.setContentType(MimeType.MIME_MS_EXCEL_2007.getName());
			response.setHeader("Content-disposition", "attachment; filename=\"" + fileName + ".xls\";");
			ServletOutputStream out = response.getOutputStream();
			IManagerBean bean = BeanManager.getManagerBean(Task.class);

			ExcelReportExporter exporter = new ExcelReportExporter();
			exporter.startExport(IReportExporter.DEFAULT_NAME);
			ReportMetadata metadata = getMetadata();
			exporter.exportHeader(metadata);
			int rows = 0;
			List<ITransferObject> list = bean.getList(getCriteria());
			for (ITransferObject to : list) {
				Task  am = (Task) to;
				exporter.startLine();
				int i = 0;
				String taskHolder = "";
				if (am.getTaskHolder() != null) {
					if (am.getTaskHolder().getUser() != null) {
						taskHolder = am.getTaskHolder().getUser().getName();
					} else if (am.getTaskHolder().getRegistry() != null) {
						taskHolder = am.getTaskHolder().getRegistry().getName();
					}
				}
				String sender = "";
				if (am.getSender() != null) {
					if (am.getSender().getUser() != null) {
						sender = am.getSender().getUser().getName();
					} else if (am.getSender().getRegistry() != null) {
						sender = am.getSender().getRegistry().getName();
					}
				}
				String comments = AonStringUtils.defaultIfBlank( am.getComments() );
				if ( AonStringUtils.isNotBlank(comments) && AonStringUtils.isNotBlank(am.getProcessComments())) {
					comments = comments + ". ";
				}
				if ( AonStringUtils.isNotBlank(am.getProcessComments())) {
					comments = comments + am.getProcessComments();
				}
				exporter.exportColumn(metadata.getColumns().get((i++)), am.getId() );
				exporter.exportColumn(metadata.getColumns().get((i++)), am.getDescription() );
				exporter.exportColumn(metadata.getColumns().get((i++)), am.getStartDate());
				exporter.exportColumn(metadata.getColumns().get((i++)), am.getEndDate());
				exporter.exportColumn(metadata.getColumns().get((i++)), am.getDueDate());
				exporter.exportColumn(metadata.getColumns().get((i++)), am.getPriority()==null?"":am.getPriority().getName(locale));
				exporter.exportColumn(metadata.getColumns().get((i++)), am.getStatus()==null?"":am.getStatus().getName(locale));
				exporter.exportColumn(metadata.getColumns().get((i++)), am.getRegistry()==null?"":am.getRegistry().getFullName());
				exporter.exportColumn(metadata.getColumns().get((i++)), am.getProject()==null?"":am.getProject().getName());
				exporter.exportColumn(metadata.getColumns().get((i++)), am.getActivityType()==null?"":am.getActivityType().getDescription());
				
				
				exporter.exportColumn(metadata.getColumns().get((i++)), sender);
				exporter.exportColumn(metadata.getColumns().get((i++)), am.getWorkGroup()==null?"":am.getWorkGroup().getDescription());
				exporter.exportColumn(metadata.getColumns().get((i++)), taskHolder);
				exporter.exportColumn(metadata.getColumns().get((i++)), comments );
				rows++;
				if (rows > 65533) {
					// 
					// AVOID FORMAT LIMITATION & EXCEPTION
					//	java.lang.IllegalArgumentException: Invalid row number (65536) outside allowable range (0..65535)
					//		at org.apache.poi.hssf.usermodel.HSSFRow.setRowNum(HSSFRow.java:252)
					//		at org.apache.poi.hssf.usermodel.HSSFRow.<init>(HSSFRow.java:86)
					exporter.startLine();
					exporter.exportColumn(metadata.getColumns().get((1)), "NO SE HA EXPORTADO TODOS LOS DATOS");
					break;
				}
			}
			exporter.endExport(out);
			out.flush();
			response.flushBuffer();
			context.responseComplete();
		} catch (IOException e) {
			LOGGER.error(e.getMessage(), e);
			e.printStackTrace();
		} catch (ManagerBeanException e) {
			LOGGER.error(e.getMessage(), e);
			e.printStackTrace();
		} catch (ReportException e) {
			LOGGER.error(e.getMessage(), e);
			e.printStackTrace();
		}
		return null;
	}

	private static final ReportColumnMetadata[] COLUMN_LABELS = new ReportColumnMetadata[] {
			new ReportColumnMetadata("NUME", Types.INTEGER, "Número", 10),
			new ReportColumnMetadata("DESC", Types.VARCHAR, "Descripción.", 50),
			new ReportColumnMetadata("START_DATE", Types.DATE, "Fec. Inicio.", 12),
			new ReportColumnMetadata("END_DATE", Types.DATE, "Fec. Fin.", 12),
			new ReportColumnMetadata("DUE_DATE", Types.DATE, "Fec. Vto.", 12),
			new ReportColumnMetadata("PRIORITY", Types.VARCHAR, "Prioridad", 10),
			new ReportColumnMetadata("STATUS", Types.VARCHAR, "Estado", 10),
			new ReportColumnMetadata("REGISTRY", Types.VARCHAR, "Exp. Asignado a", 40),
			new ReportColumnMetadata("PROJECT", Types.VARCHAR, "Exp. Nombre", 30),
			new ReportColumnMetadata("ACTIVITY", Types.VARCHAR, "Exp. Tipo Act.", 20),
			new ReportColumnMetadata("SENDER", Types.VARCHAR, "Remitente.", 25),

			new ReportColumnMetadata("GROUP", Types.VARCHAR, "Grupo", 25),
			new ReportColumnMetadata("USER", Types.VARCHAR, "Usuario.", 25),
			
			new ReportColumnMetadata("COMMENTS", Types.VARCHAR, "Comentarios.", 50),
			
//			new ReportColumnMetadata("CUSER", Types.VARCHAR, "Usr. Creac.", 15),
//			new ReportColumnMetadata("CDATE", Types.TIMESTAMP, "Usr. Creac.", 15),
//			new ReportColumnMetadata("MUSER", Types.VARCHAR, "Usr. Modif.", 15),
//			new ReportColumnMetadata("MDATE", Types.TIMESTAMP, "Usr. Modif.", 15),
	};
	
	private ReportMetadata getMetadata() throws ReportException {

		ReportMetadata metadata = new ReportMetadata();
		for (ReportColumnMetadata rcm : COLUMN_LABELS) {
			metadata.getColumns().add(rcm);
		}
		return metadata;
	}
	






}



