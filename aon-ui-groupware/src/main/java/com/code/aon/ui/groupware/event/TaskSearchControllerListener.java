package com.code.aon.ui.groupware.event;


import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.company.util.CompanyUtil;
import com.code.aon.config.WorkGroup;
import com.code.aon.groupware.Process;
import com.code.aon.groupware.ProcessDetail;
import com.code.aon.groupware.TaskHolder;
import com.code.aon.groupware.TaskHolderWorkgroup;
import com.code.aon.groupware.enumeration.Priority;
import com.code.aon.groupware.enumeration.TaskSource;
import com.code.aon.groupware.enumeration.TaskStatus;
import com.code.aon.project.ActivityType;
import com.code.aon.project.Project;
import com.code.aon.project.ProjectType;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.registry.Registry;
import com.code.aon.ui.common.components.LookupChangeEvent;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.event.ControllerSearchListener;
import com.code.aon.ui.groupware.GroupwareUtils;
import com.code.aon.ui.groupware.controller.GroupWareCollectionsController;
import com.code.aon.ui.groupware.controller.IGroupWareConstants;
import com.code.aon.ui.groupware.controller.TaskController;
import com.code.aon.ui.project.controller.IProjectConstants;
import com.code.aon.ui.project.controller.ProjectCollectionsController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class TaskSearchControllerListener extends ControllerSearchListener {

	private Date fromDueDate;
	private Date toDueDate;
	private Registry registry;
	private TaskHolder taskHolder;
	private WorkGroup workGroup;
	private Project project;
	private ProjectType projectType;
	private ActivityType activityType;
	private Process process;
	private ProcessDetail processDetail;
	private boolean statusPending;
	private boolean statusInProgress;
	private boolean statusFinished;
	private boolean statusDeleted;
	private boolean processTask;
	private boolean highPriority;
	private boolean normalPriority;
	private boolean lowPriority;
	private boolean nonePriority;
	
	private enum When {
		BEFORE,
		TODAY,
		WEEK,
		TWO_WEEKS,
		MONTH,
		ALWAYS;
	}
	private When when;
	
	private  List<SelectItem> projects;
	private  List<SelectItem> activityTypes;

	private GroupwareUtils groupwareUtils;
	
	public TaskSearchControllerListener() {
		try {
			init();
		} catch (ManagerBeanException e) {
			// Nothing
		}
	}
	
	public GroupwareUtils getGroupwareUtils() {
		if (groupwareUtils == null) {
			groupwareUtils = new GroupwareUtils();
		}
		return groupwareUtils;
	}
	
	public Date getFromDueDate() {
		return fromDueDate;
	}
	public void setFromDueDate(Date fromDueDate) {
		this.fromDueDate = fromDueDate;
	}

	public Date getToDueDate() {
		return toDueDate;
	}
	public void setToDueDate(Date toDueDate) {
		this.toDueDate = toDueDate;
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
	
	public ProjectType getProjectType() {
		return projectType;
	}
	public void setProjectType(ProjectType projectType) {
		this.projectType = projectType;
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

	public When getWhen() {
		return when;
	}
	public void setWhen(When when) {
		this.when = when;
	}

	public boolean isWhenEqualsBefore() {
		return (getWhen() == When.BEFORE);
	}
	public boolean isWhenEqualsWeek() {
		return (getWhen() == When.WEEK);
	}
	public boolean isWhenEqualsTwoWeeks() {
		return (getWhen() == When.TWO_WEEKS);
	}
	public boolean isWhenEqualsMonth() {
		return (getWhen() == When.MONTH);
	}
	public boolean isWhenEqualsToday() {
		return (getWhen() == When.TODAY);
	}
	public boolean isWhenEqualsAlways() {
		return (getWhen() == When.ALWAYS);
	}

	public Process getProcess() {
		return process;
	}
	public void setProcess(Process process) {
		this.process = process;
	}

	public ProcessDetail getProcessDetail() {
		return processDetail;
	}
	public void setProcessDetail(ProcessDetail processDetail) {
		this.processDetail = processDetail;
	}

	public List<SelectItem> getTaskHolderWorkgroups() throws ManagerBeanException {
		GroupWareCollectionsController gcc = (GroupWareCollectionsController) 
			AonUtil.getRegisteredBean( IGroupWareConstants.GROUPWARE_COLLECTIONS_CONTROLLER_NAME);
		return gcc.getTaskHolderWorkgroups(getWorkGroup());
	}

	public List<SelectItem> getProcessDetails() throws ManagerBeanException {
		if (getProcess() != null && getProcess().getId() != null) {
			GroupWareCollectionsController gcc = (GroupWareCollectionsController) 
			AonUtil.getRegisteredBean( IGroupWareConstants.GROUPWARE_COLLECTIONS_CONTROLLER_NAME);
			return gcc.getProcessDetails(getProcess());
		} 
		return new LinkedList<SelectItem>();
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
		setFromDueDate(null);
		setToDueDate(null);
		setRegistry((Registry)BeanManager.getManagerBean(Registry.class).createNewTo());
		setTaskHolder(null);
		setWorkGroup(null);
		setProject(null);
		setProcess(null);
		setProcessDetail(null);
		setActivityType(null);
		setProjectType(null);
		setStatusPending(true);
		setStatusInProgress(true);
		setStatusFinished(false);
		setStatusDeleted(false);
		setHighPriority(true);
		setNormalPriority(true);
		setLowPriority(true);
		setNonePriority(true);
		setProcessTask(false);
		setWhen(null);
		loadProjects(null);
		loadActivityTypes(null);
	}

	@Override
	protected void completeCriteria(Criteria criteria) throws ManagerBeanException, ExpressionException {
		super.completeCriteria( criteria );
		TaskController controller = (TaskController) FormUtil.getController(IGroupWareConstants.TASK_CONTROLLER_NAME);
		if(!controller.isMonitor()){
			TaskHolder taskHolder = getGroupwareUtils().getCurrentTaskHolder();
			String taskHolderAlias = getFieldName(IEntityAlias.TASK_TASK_HOLDER_ID);
			Expression userExpr = ExpressionUtilities.getEqualExpression(taskHolderAlias, taskHolder.getId() );
			Expression workGroupExpr = obtainTaskHolderWorkGroupsExpression(taskHolder, getFieldName(IEntityAlias.TASK_WORK_GROUP_ID));
			Expression groupExpr = ExpressionUtilities.getNullExpression(taskHolderAlias);
			workGroupExpr = ExpressionUtilities.getAndExpression(workGroupExpr, groupExpr);
			criteria.addExpression(ExpressionUtilities.getOrExpression(userExpr, workGroupExpr));
		} else {
			if (getWorkGroup() != null) {
				criteria.addEqualExpression( getFieldName(IEntityAlias.TASK_WORK_GROUP_ID), getWorkGroup().getId() ); 	
			}
			if (getTaskHolder() != null) {
				criteria.addEqualExpression( getFieldName(IEntityAlias.TASK_TASK_HOLDER_ID), getTaskHolder().getId() ); 	
			}
		}
		if (isWhenEqualsWeek()) {
			Date[] range = CommonUtil.getWeekDateRange(new Date());
			criteria.addGreaterThanOrEqualExpression(getFieldName(IEntityAlias.TASK_DUE_DATE), range[0]);
			criteria.addLessThanOrEqualExpression(getFieldName(IEntityAlias.TASK_DUE_DATE), range[1]);
		} else if (isWhenEqualsTwoWeeks()) {
			Date[] range = CommonUtil.getTwoWeekDateRange(new Date());
			criteria.addGreaterThanOrEqualExpression(getFieldName(IEntityAlias.TASK_DUE_DATE), range[0]);
			criteria.addLessThanOrEqualExpression(getFieldName(IEntityAlias.TASK_DUE_DATE), range[1]);
		} else if (isWhenEqualsMonth()) {
			Date today = new Date();
			criteria.addGreaterThanOrEqualExpression(getFieldName(IEntityAlias.TASK_DUE_DATE), CommonUtil.getMonthFirstDay(today));
			criteria.addLessThanOrEqualExpression(getFieldName(IEntityAlias.TASK_DUE_DATE), CommonUtil.getMonthLastDay(today));
		} else if (isWhenEqualsToday()) {
			criteria.addEqualExpression(getFieldName(IEntityAlias.TASK_DUE_DATE), new Date());
		} else if (isWhenEqualsBefore()) {
			criteria.addLessThanExpression(getFieldName(IEntityAlias.TASK_DUE_DATE), new Date());
		} else {
			if (getFromDueDate() != null) {
				criteria.addGreaterThanOrEqualExpression(getFieldName(IEntityAlias.TASK_DUE_DATE), getFromDueDate());
			}
			if (getToDueDate() != null) {
				criteria.addLessThanOrEqualExpression(getFieldName(IEntityAlias.TASK_DUE_DATE), getToDueDate());
			}
		}
		if ((getRegistry() != null) && (getRegistry().getId() != null)) {
			criteria.addEqualExpression(getFieldName(IEntityAlias.TASK_REGISTRY_ID), getRegistry().getId());
		}		
		if ((getProject() != null) && (getProject().getId() != null)) {
			criteria.addEqualExpression(getFieldName(IEntityAlias.TASK_PROJECT_ID), getProject().getId());
		}		
		if ((getActivityType() != null) && (getActivityType().getId() != null)) {
			criteria.addEqualExpression(getFieldName(IEntityAlias.TASK_ACTIVITY_TYPE_ID), getActivityType().getId());
		}		
		if ((getProjectType() != null) && (getProjectType().getId() != null)) {
			criteria.addEqualExpression(getFieldName(IEntityAlias.TASK_PROJECT_PROJECT_TYPE_ID), getProjectType().getId());
		}		
		if (isProcessTask()) {
			criteria.addEqualExpression( getFieldName(IEntityAlias.TASK_SOURCE), TaskSource.PROCESS );
		}
		if ((getProcess() != null) && (getProcess().getId() != null)) {
			criteria.addEqualExpression("Task.processTask.processDetail.process.id", getProcess().getId());
		}		
		if ((getProcessDetail() != null) && (getProcessDetail().getId() != null)) {
			criteria.addEqualExpression("Task.processTask.processDetail.id", getProcessDetail().getId());
		}		
		loadStatusCriteria(criteria);
		loadPriorityCriteria(criteria);
	}

    private Expression obtainTaskHolderWorkGroupsExpression(TaskHolder taskHolder, String alias) throws ManagerBeanException {
        Expression expression = null;
        IManagerBean bean = BeanManager.getManagerBean(TaskHolderWorkgroup.class);
        Criteria criteria = new Criteria();
        criteria.addEqualExpression(bean.getFieldName(IEntityAlias.TASK_HOLDER_WORKGROUP_TASK_HOLDER_ID), taskHolder.getId());
        List<ITransferObject> list = bean.getList(criteria);
        for (ITransferObject to: list ) {
        	TaskHolderWorkgroup thwg = (TaskHolderWorkgroup) to;
            expression = ExpressionUtilities.getOrExpression(expression, ExpressionUtilities.getEqualExpression(alias, thwg.getWorkGroup().getId()));
        }
        return expression;
    }

	private void loadStatusCriteria(Criteria criteria) throws ManagerBeanException {
		if (isStatusDeleted() || isStatusFinished() || isStatusInProgress() || isStatusPending()) {
			String statusAlias = getFieldName(IEntityAlias.TASK_STATUS);
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
			String priorityAlias = getFieldName(IEntityAlias.TASK_PRIORITY);
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
	
	public void onFilterAll(ActionEvent event) throws ManagerBeanException {
		TaskController c = (TaskController) AonUtil.getRegisteredBean(IGroupWareConstants.TASK_CONTROLLER_NAME);
		setWhen(When.ALWAYS);
		c.onFilter(event);
	}
	public void onFilterWeek(ActionEvent event) throws ManagerBeanException {
		TaskController c = (TaskController) AonUtil.getRegisteredBean(IGroupWareConstants.TASK_CONTROLLER_NAME);
		setWhen(When.WEEK);
		c.onFilter(event);
	}
	public void onFilterTwoWeeks(ActionEvent event) throws ManagerBeanException {
		TaskController c = (TaskController) AonUtil.getRegisteredBean(IGroupWareConstants.TASK_CONTROLLER_NAME);
		setWhen(When.TWO_WEEKS);
		c.onFilter(event);
	}
	public void onFilterMonth(ActionEvent event) throws ManagerBeanException {
		TaskController c = (TaskController) AonUtil.getRegisteredBean(IGroupWareConstants.TASK_CONTROLLER_NAME);
		setWhen(When.MONTH);
		c.onFilter(event);
	}
	public void onFilterToday(ActionEvent event) throws ManagerBeanException {
		TaskController c = (TaskController) AonUtil.getRegisteredBean(IGroupWareConstants.TASK_CONTROLLER_NAME);
		setWhen(When.TODAY);
		c.onFilter(event);
	}
	public void onFilterBefore(ActionEvent event) throws ManagerBeanException {
		TaskController c = (TaskController) AonUtil.getRegisteredBean(IGroupWareConstants.TASK_CONTROLLER_NAME);
		setWhen(When.BEFORE);
		c.onFilter(event);
	}
	
}
