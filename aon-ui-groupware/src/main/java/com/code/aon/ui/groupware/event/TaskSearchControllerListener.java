package com.code.aon.ui.groupware.event;

import java.util.LinkedList;
import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import com.code.aon.common.BeanManager;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.company.util.CompanyUtil;
import com.code.aon.config.WorkGroup;
import com.code.aon.groupware.TaskHolder;
import com.code.aon.groupware.dao.IGroupwareAlias;
import com.code.aon.groupware.enumeration.Priority;
import com.code.aon.groupware.enumeration.TaskStatus;
import com.code.aon.groupware.enumeration.TaskSource;
import com.code.aon.project.ActivityType;
import com.code.aon.project.Project;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.registry.Registry;
import com.code.aon.ui.common.components.LookupChangeEvent;
import com.code.aon.ui.config.util.UserUtils;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.event.ControllerSearchListener;
import com.code.aon.ui.groupware.GroupwareUtils;
import com.code.aon.ui.groupware.controller.GroupWareCollectionsController;
import com.code.aon.ui.groupware.controller.IGroupWareConstants;
import com.code.aon.ui.groupware.controller.TaskController;
import com.code.aon.ui.project.controller.IProjectConstants;
import com.code.aon.ui.project.controller.ProjectCollectionsController;
import com.code.aon.ui.util.AonUtil;

public class TaskSearchControllerListener extends ControllerSearchListener {

	private Registry registry;
	private TaskHolder taskHolder;
	private WorkGroup workGroup;
	private Project project;
	private ActivityType activityType;
	private boolean statusPending;
	private boolean statusInProgress;
	private boolean statusFinished;
	private boolean statusDeleted;
	private boolean processTask;
	private boolean highPriority;
	private boolean normalPriority;
	private boolean lowPriority;
	private boolean nonePriority;
	
	private  List<SelectItem> projects;
	private  List<SelectItem> activityTypes;

	private GroupwareUtils groupwareUtils;
	
	public GroupwareUtils getGroupwareUtils() {
		if (groupwareUtils == null) {
			groupwareUtils = new GroupwareUtils();
		}
		return groupwareUtils;
	}

	public Registry getRegistry() {
		return registry;
	}
	public void setRegistry(Registry registry) {
		this.registry = registry;
	}

	public boolean isStatusPending() {
		return statusPending;
	}
	public void setStatusPending(boolean statusPending) {
		this.statusPending = statusPending;
	}

	public boolean isStatusInProgress() {
		return statusInProgress;
	}
	public void setStatusInProgress(boolean statusInProgress) {
		this.statusInProgress = statusInProgress;
	}

	public boolean isStatusFinished() {
		return statusFinished;
	}
	public void setStatusFinished(boolean statusFinished) {
		this.statusFinished = statusFinished;
	}

	public boolean isStatusDeleted() {
		return statusDeleted;
	}
	public void setStatusDeleted(boolean statusDeleted) {
		this.statusDeleted = statusDeleted;
	}
	
	public TaskHolder getTaskHolder() {
		return taskHolder;
	}
	public void setTaskHolder(TaskHolder taskHolder) {
		this.taskHolder = taskHolder;
	}

	public WorkGroup getWorkGroup() {
		return workGroup;
	}
	public void setWorkGroup(WorkGroup workGroup) {
		this.workGroup = workGroup;
	}
	
	public boolean isProcessTask() {
		return processTask;
	}
	public void setProcessTask(boolean processTask) {
		this.processTask = processTask;
	}
	
	public ActivityType getActivityType() {
		return activityType;
	}
	public void setActivityType(ActivityType activityType) {
		this.activityType = activityType;
	}
	
	public Project getProject() {
		return project;
	}
	public void setProject(Project project) {
		this.project = project;
	}
	
	public boolean isHighPriority() {
		return highPriority;
	}
	public void setHighPriority(boolean highPriority) {
		this.highPriority = highPriority;
	}

	public boolean isNormalPriority() {
		return normalPriority;
	}
	public void setNormalPriority(boolean normalPriority) {
		this.normalPriority = normalPriority;
	}

	public boolean isLowPriority() {
		return lowPriority;
	}
	public void setLowPriority(boolean lowPriority) {
		this.lowPriority = lowPriority;
	}

	public boolean isNonePriority() {
		return nonePriority;
	}
	public void setNonePriority(boolean nonePriority) {
		this.nonePriority = nonePriority;
	}

	public List<SelectItem> getTaskHolderWorkgroups() throws ManagerBeanException {
		GroupWareCollectionsController gcc = (GroupWareCollectionsController) 
			AonUtil.getRegisteredBean( IGroupWareConstants.GROUPWARE_COLLECTIONS_CONTROLLER_NAME);
		return gcc.getTaskHolderWorkgroups(getWorkGroup());
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
				setProject(null);
				loadProjects(null);
			} else {
				loadProjects(registry.getId());
			}
			Integer projectTypeId = null;
			if (getProject() != null && getProject().getProjectType() != null) {
				projectTypeId = getProject().getProjectType().getId();	
			}
			loadActivityTypes(projectTypeId);
		} catch (ManagerBeanException e) {
			String msg = "Error al inicializar la lista de proyectos.";
			throw new AbortProcessingException(msg,e);
		}
	}

	public void onChangeProject(ActionEvent event) {
		try {
			Registry registry = null;
			if (getProject() != null) {
				registry = getProject().getRegistry();
			} else {
				registry = (Registry) BeanManager.getManagerBean(Registry.class).createNewTo();
			}
			setRegistry(registry);
			LookupChangeEvent e = new LookupChangeEvent(event.getComponent(), registry);
			onRegistryChanged(e);
			Integer projectTypeId = null;
			if (getProject() != null && getProject().getProjectType() != null) {
				projectTypeId = getProject().getProjectType().getId();	
			}
			loadActivityTypes(projectTypeId);
		} catch (ManagerBeanException e) {
			String msg = "Error al inicializar el proyecto.";
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
	
	
	@Override
	protected void init() throws ManagerBeanException {
		super.init();
		setRegistry((Registry)BeanManager.getManagerBean(Registry.class).createNewTo());
		setTaskHolder(null);
		setWorkGroup(null);
		setProject(null);
		setActivityType(null);
		setStatusPending(true);
		setStatusInProgress(true);
		setStatusFinished(false);
		setStatusDeleted(false);
		setHighPriority(true);
		setNormalPriority(true);
		setLowPriority(true);
		setNonePriority(true);
		setProcessTask(false);
		loadProjects(null);
		loadActivityTypes(null);
	}

	@Override
	protected void completeCriteria(Criteria criteria) throws ManagerBeanException, ExpressionException {
		super.completeCriteria( criteria );
		TaskController controller = (TaskController) FormUtil.getController(IGroupWareConstants.TASK_CONTROLLER_NAME);
		CompanyUtil companyUtil = new CompanyUtil(); 
		controller .getCriteria().addEqualExpression(getFieldName(IGroupwareAlias.TASK_ENTERPRISE_ID), companyUtil.getActiveEnterprise().getId() );
		if(!controller.isMonitor()){
			TaskHolder taskHolder = getGroupwareUtils().getCurrentTaskHolder();
			String taskHolderAlias = getFieldName(IGroupwareAlias.TASK_TASK_HOLDER_ID);
			Expression userExpr = ExpressionUtilities.getEqualExpression(taskHolderAlias, taskHolder.getId() );
			Expression workGroupExpr = UserUtils.obtainUserWorkGroupsExpr(taskHolder.getUser(), getFieldName(IGroupwareAlias.TASK_WORK_GROUP_ID));
			Expression groupExpr = ExpressionUtilities.getNullExpression(taskHolderAlias);
			workGroupExpr = ExpressionUtilities.getAndExpression(workGroupExpr, groupExpr);
			criteria.addExpression(ExpressionUtilities.getOrExpression(userExpr, workGroupExpr));
		} else {
			if (getWorkGroup() != null) {
				criteria.addEqualExpression( getFieldName(IGroupwareAlias.TASK_WORK_GROUP_ID), getWorkGroup().getId() ); 	
			}
			if (getTaskHolder() != null) {
				criteria.addEqualExpression( getFieldName(IGroupwareAlias.TASK_TASK_HOLDER_ID), getTaskHolder().getId() ); 	
			}
		}
		if ((getRegistry() != null) && (getRegistry().getId() != null)) {
			criteria.addEqualExpression(getFieldName(IGroupwareAlias.TASK_REGISTRY_ID), getRegistry().getId());
		}		
		if ((getProject() != null) && (getProject().getId() != null)) {
			criteria.addEqualExpression(getFieldName(IGroupwareAlias.TASK_PROJECT_ID), getProject().getId());
		}		
		if ((getActivityType() != null) && (getActivityType().getId() != null)) {
			criteria.addEqualExpression(getFieldName(IGroupwareAlias.TASK_ACTIVITY_TYPE_ID), getActivityType().getId());
		}		
		
		if (isProcessTask()) {
			criteria.addEqualExpression( getFieldName(IGroupwareAlias.TASK_SOURCE), TaskSource.PROCESS );
		}
		loadStatusCriteria(criteria);
		loadPriorityCriteria(criteria);
	}

	private void loadStatusCriteria(Criteria criteria) throws ManagerBeanException {
		if (isStatusDeleted() || isStatusFinished() || isStatusInProgress() || isStatusPending()) {
			String statusAlias = getFieldName(IGroupwareAlias.TASK_STATUS);
			// Hay que realizar una expression OR con los valores
			// seleccionados. Como hay
			// cuatro valores de status creamos un array con esas
			// dimensiones y asignamos
			// las expresiones correspondientes al array.
			Expression[] exps = { null, null, null, null };
			int count = 0;
			int inCaseCount1 = -1;
			if (isStatusDeleted()) {
				exps[0] = ExpressionUtilities.getEqualExpression(statusAlias,TaskStatus.DELETED);
				count++;
				inCaseCount1 = 0;
			}
			if (isStatusFinished()) {
				exps[1] = ExpressionUtilities.getEqualExpression(statusAlias,TaskStatus.FINISHED);
				count++;
				inCaseCount1 = 1;
			}
			if (isStatusInProgress()) {
				exps[2] = ExpressionUtilities.getEqualExpression(statusAlias,TaskStatus.IN_PROGRESS);
				count++;
				inCaseCount1 = 2;
			}
			if (isStatusPending()) {
				exps[3] = ExpressionUtilities.getEqualExpression(statusAlias,TaskStatus.PENDING);
				count++;
				inCaseCount1 = 3;
			}
			addOrExpression(criteria,exps,count,inCaseCount1);
		}
	}
	
	private void loadPriorityCriteria(Criteria criteria) throws ManagerBeanException {
		if (isHighPriority() || isLowPriority() || isNormalPriority() || isNonePriority()) {
			String priorityAlias = getFieldName(IGroupwareAlias.TASK_PRIORITY);
			Expression[] exps = { null, null, null, null };
			int count = 0;
			int inCaseCount1 = -1;
			if (isHighPriority()) {
				exps[0] = ExpressionUtilities.getEqualExpression(priorityAlias,Priority.HIGH);
				count++;
				inCaseCount1 = 0;
			}
			if (isLowPriority()) {
				exps[1] = ExpressionUtilities.getEqualExpression(priorityAlias,Priority.LOW);
				count++;
				inCaseCount1 = 1;
			}
			if (isNormalPriority()) {
				exps[2] = ExpressionUtilities.getEqualExpression(priorityAlias,Priority.NORMAL);
				count++;
				inCaseCount1 = 2;
			}
			if (isNonePriority()) {
				exps[3] = ExpressionUtilities.getEqualExpression(priorityAlias,Priority.NONE);
				count++;
				inCaseCount1 = 3;
			}
			addOrExpression(criteria,exps,count,inCaseCount1);
		}
	}

	private void addOrExpression(Criteria criteria, Expression[] exps,int count, int inCaseCount1) {
		Expression expToAdd = null;
		if (count == 1) {
			expToAdd = exps[inCaseCount1];
		} else {
			// Si count > 1 hay que hacer una OR Expression
			boolean ready = false;
			for (int i = 0; i < exps.length; i++) {
				if (exps[i] != null) {
					if (!ready) {
						expToAdd = exps[i];
						ready = true;
					} else {
						expToAdd = ExpressionUtilities.getOrExpression(expToAdd, exps[i]);
					}
				}
			}
		}
		criteria.addExpression(expToAdd);
	}	
	
}