package com.code.aon.ui.project.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.StringUtils;

import com.code.aon.campaign.ActivityProcess;
import com.code.aon.campaign.Campaign;
import com.code.aon.campaign.CampaignDossier;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.User;
import com.code.aon.config.UserWorkGroup;
import com.code.aon.config.dao.IConfigAlias;
import com.code.aon.customer.Customer;
import com.code.aon.project.Activity;
import com.code.aon.project.Dossier;
import com.code.aon.project.PeriodicalTask;
import com.code.aon.project.Task;
import com.code.aon.project.dao.IProjectAlias;
import com.code.aon.project.enumeration.DossierStatus;
import com.code.aon.project.enumeration.TaskSource;
import com.code.aon.project.enumeration.TaskStatus;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.Order;
import com.code.aon.ql.OrderByList;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.config.util.UserUtils;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.project.util.CampaignTaskManager;
import com.code.aon.ui.util.AonUtil;

public class TaskController extends BasicController implements ITaskController {

	private static final Logger LOGGER = Logger.getLogger(TaskController.class.getName());

	private static final String PERIOD_TASK_CONTROLLER_NAME = "periodTask";

	private static final String ASCENDING = "asc";
	private static final String DESCENDING = "desc";

	public static String TASK_ID_ALIAS = null;
	public static String CUSTOMER_ALIAS = null;
	public static String TASK_START_DATE_ALIAS = null;
	public static String TASK_END_DATE_ALIAS = null;
	public static String TASK_DUE_DATE_ALIAS = null;
	public static String DESCRIPTION_ALIAS = null;
	public static String WORKGROUP_ALIAS = null;
	public static String USER_ALIAS = null;
	public static String USER_NAME_ALIAS = null;
	public static String DOSSIER_ALIAS = null;
	public static String DOSSIER_NUMBER_ALIAS = null;
	public static String ACTIVITY_ALIAS = null;
	public static String STATUS_ALIAS = null;
	public static String PRIORITY_ALIAS = null;
	public static String PERCENT_ALIAS = null;

	static {
		try {
			IManagerBean taskBean = BeanManager.getManagerBean(Task.class);
			TASK_ID_ALIAS = taskBean.getFieldName(IProjectAlias.TASK_ID);
			DESCRIPTION_ALIAS = taskBean.getFieldName(IProjectAlias.TASK_DESCRIPTION);
			CUSTOMER_ALIAS = taskBean.getFieldName(IProjectAlias.TASK_CUSTOMER_ID);
			TASK_START_DATE_ALIAS = taskBean.getFieldName(IProjectAlias.TASK_START_DATE);
			TASK_END_DATE_ALIAS = taskBean.getFieldName(IProjectAlias.TASK_END_DATE);
			TASK_DUE_DATE_ALIAS = taskBean.getFieldName(IProjectAlias.TASK_DUE_DATE);
			WORKGROUP_ALIAS = taskBean.getFieldName(IProjectAlias.TASK_WORK_GROUP_ID);
			USER_ALIAS = taskBean.getFieldName(IProjectAlias.TASK_USER_ID);
			USER_NAME_ALIAS = taskBean.getFieldName(IProjectAlias.TASK_USER_NAME);
			DOSSIER_ALIAS = taskBean.getFieldName(IProjectAlias.TASK_DOSSIER_ID);
			DOSSIER_NUMBER_ALIAS = taskBean.getFieldName(IProjectAlias.TASK_DOSSIER_NUMBER);
			ACTIVITY_ALIAS = taskBean.getFieldName(IProjectAlias.TASK_ACTIVITY_ID);
			STATUS_ALIAS = taskBean.getFieldName(IProjectAlias.TASK_STATUS);
			PRIORITY_ALIAS = taskBean.getFieldName(IProjectAlias.TASK_PRIORITY);
			PERCENT_ALIAS = taskBean.getFieldName(IProjectAlias.TASK_PERCENT);
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Error obtining field alias", e);
		}
	}

	private String description;
	private Integer workgroup;
	private Integer user;
	private Date startDateFrom;
	private Date startDateTo;
	private Date endDateFrom;
	private Date endDateTo;
	private Date dueDateFrom;
	private Date dueDateTo;
	private Customer customer;
	private Integer dossierId;
	private Integer activityId;
	private boolean statusPending = true;
	private boolean statusInProgress = true;
	private boolean statusFinished = false;
	private boolean statusDeleted = false;

	private User loggedUser;

	private String orderColumn;
	private boolean orderAscending;

	private boolean richEditor;
	
	private List<SelectItem> dossiers;
    private List<SelectItem> allDossiers;
    private List<SelectItem> activities;

	private List<SelectItem> users;

	private ArrayList<Task> checks = new ArrayList<Task>();

	private boolean monitor;

	public List<SelectItem> getAvailableFormDossiers() {
		Task t = (Task) getTo();
		if (t.getDossier().getCustomer() != null && t.getDossier().getCustomer().getId() != null) {
			return getDossiers();
		} 
		return getAllDossiers();
    }

	public List<SelectItem> getAvailableSearchDossiers() {
		if (getCustomer() != null && getCustomer().getId() != null) {
			return getDossiers();
		} 
		return getAllDossiers();
    }

	public List<SelectItem> getAllDossiers() {
		if (allDossiers == null) {
			loadAllDossiers();
		}
		return allDossiers;
	}

	@SuppressWarnings("unchecked")
	public void loadAllDossiers() {
        allDossiers = new LinkedList<SelectItem>();
        try {
            IManagerBean managerBean = BeanManager.getManagerBean(Dossier.class);
            Criteria criteria = new Criteria();
            criteria.addEqualExpression(managerBean.getFieldName(IProjectAlias.DOSSIER_STATUS), DossierStatus.ACTIVE);
            criteria.addOrder(managerBean.getFieldName(IProjectAlias.DOSSIER_NUMBER));
            criteria.addOrder(managerBean.getFieldName(IProjectAlias.DOSSIER_CUSTOMER_ID));
            Iterator iterator = managerBean.getList(criteria).iterator();
            while (iterator.hasNext()) {
                Dossier dossier = (Dossier)iterator.next();
                StringBuilder sb = new StringBuilder(dossier.getNumber()); 
                sb.append(" (");
                sb.append( dossier.getCustomer().getRegistry().getAlias() );
                sb.append(")");
                SelectItem item = new SelectItem(dossier.getId(), sb.toString() );
                allDossiers.add(item);
            }
        } catch (ManagerBeanException e) {
        	LOGGER.log(Level.SEVERE, "Error loading all dossiers!", e);
        }
    }

	public List<SelectItem> getDossiers() {
		return dossiers;
	}

	public void setDossiers(List<SelectItem> dossiers) {
		this.dossiers = dossiers;
	}

	public List<SelectItem> getActivities() {
		if (activities == null) {
			setActivities(new LinkedList<SelectItem>());
		}
		return activities;
	}

	public void setActivities(List<SelectItem> activities) {
		this.activities = activities;
	}

	public List<SelectItem> getUsers() {
		if (users == null) {
			loadUsers(null);
		}
		return users;
	}

	public void setUsers(List<SelectItem> users) {
		this.users = users;
	}

	public User getLoggedUser() {
		if (loggedUser == null) {
			loggedUser = UserUtils.getInstance().getLoggedUser();
		}
		return loggedUser;
	}

	public void setLoggedUser(User loggedUser) {
		this.loggedUser = loggedUser;
	}

	public void resetChecks() {
		checks = new ArrayList<Task>();
	}

	public boolean getRowChecked() {
		Task to = (Task) model.getRowData();
		return checks.contains(to);
	}

	public void setRowChecked(boolean rowChecked) {
		if (rowChecked) {
			Task to = (Task) model.getRowData();
			if (!checks.contains(to)) {
				checks.add(to);
			}
		} else {
			Task to = (Task) model.getRowData();
			if (checks.contains(to)) {
				checks.remove(to);
			}
		}
	}

	public void rowSelected(ValueChangeEvent event) {
		if (event.getNewValue() != null) {
			setRowChecked(((Boolean) event.getNewValue()).booleanValue());
		}
	}

	@Override
	public void onEditSearch(ActionEvent event) {
		setCustomer(null);
		setStartDateFrom(null);
		setStartDateTo(null);
		setEndDateFrom(null);
		setEndDateTo(null);
		setDueDateFrom(null);
		setDueDateTo(null);
		setDescription(null);
		setWorkgroup(null);
		setUser(null);
		setDossierId(null);
		setActivityId(null);
		initializeStatusFilter();
		setUsers(null);
		setDossiers(null);
		setActivities(null);
		super.onEditSearch(event);
	}

	private void initializeStatusFilter() {
		setStatusPending(true);
		setStatusInProgress(true);
		setStatusFinished(false);
		setStatusDeleted(false);
	}

	public void onRefresh(ActionEvent event) {
		super.onSearch(event);
	}

	public boolean isFreeTask() {
		return isFreeTask((Task) this.getTo());
	}

	public boolean isFreeListTask() {
		try {
			return isFreeTask((Task) this.getModel().getRowData());
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error getting Task Model", e);
		}
		return false;
	}

	public boolean isFreeTask(Task task) {
		Criteria criteria = new Criteria();
		try {
			criteria.addEqualExpression(getFieldName(IProjectAlias.TASK_ID), task.getId());
			criteria.addNotNullExpression(getFieldName(IProjectAlias.TASK_USER_ID));
			return (getManagerBean().getList(criteria).size() == 0);
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error obtaining task with id= " + task.getId(), e);
		}
		return false;
	}

	public void customerChange(ValueChangeEvent event) {
		if (event.getNewValue() != null && !"".equals(event.getNewValue())) {
			loadDossiers(new Integer(event.getNewValue().toString()));
		} else {
			setDossiers(null);
		}
	}

	public void customerPojoChange(ValueChangeEvent event) {
		if (event.getNewValue() != null) {
			Customer customer = (Customer) event.getNewValue();
			loadDossiers(customer.getId());
			setDossierId(null);
		} else {
			setDossiers(null);
		}
	}

	@SuppressWarnings("unchecked")
	public void loadDossiers(Integer customerId) {
		setDossiers(new LinkedList<SelectItem>());
		try {
			IManagerBean managerBean = BeanManager.getManagerBean(Dossier.class);
			Criteria criteria = new Criteria();
			if (customerId != null) {
				criteria.addEqualExpression(managerBean
						.getFieldName(IProjectAlias.DOSSIER_CUSTOMER_ID), customerId);
			}
			criteria.addEqualExpression(managerBean.getFieldName(IProjectAlias.DOSSIER_STATUS),
					DossierStatus.ACTIVE);
			Iterator iterator = managerBean.getList(criteria).iterator();
			while (iterator.hasNext()) {
				Dossier dossier = (Dossier) iterator.next();
				StringBuilder sb = new StringBuilder(dossier.getNumber());
				if (customerId == null) {
					sb.append(" (");
					sb.append(dossier.getCustomer().getRegistry().getAlias());
					sb.append(")");
				}
				SelectItem item = new SelectItem(dossier.getId(), sb.toString());
				dossiers.add(item);
			}
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error loading dossiers related with customer with id= "
					+ customerId.toString(), e);
		}
	}

	public void dossierChange(ValueChangeEvent event) {
		try {
			if (event.getNewValue() != null && !"".equals(event.getNewValue())) {
				IManagerBean bean = BeanManager.getManagerBean(Dossier.class);
				Dossier d = (Dossier) bean.get((Integer) event.getNewValue());
				if (d != null) {
					((Task)getTo()).setDossier(d);
					loadDossiers(d.getCustomer().getId());
				}
				loadActivities(new Integer(event.getNewValue().toString()));
			} else {
				setActivities(new LinkedList<SelectItem>());
			}
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error loading dossier", e);
		}
	}

	public void dossierSearchChange(ValueChangeEvent event) {
		try {
			if (event.getNewValue() != null && !"".equals(event.getNewValue())) {
				IManagerBean bean = BeanManager.getManagerBean(Dossier.class);
				Integer id = (Integer) event.getNewValue();
				Dossier d = (Dossier) bean.get(id);
				if (d != null) {
					setCustomer(d.getCustomer());
					loadDossiers(d.getCustomer().getId());
				}
				loadActivities(new Integer(event.getNewValue().toString()));
			} else {
				setActivities(new LinkedList<SelectItem>());
			}
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error loading dossier", e);
		}
	}

	@SuppressWarnings("unchecked")
	public void loadActivities(Integer dossierId) {
		setActivities( new LinkedList<SelectItem>());
		try {
			IManagerBean managerBean = BeanManager.getManagerBean(Activity.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(
					managerBean.getFieldName(IProjectAlias.ACTIVITY_DOSSIER_ID), dossierId);
			Iterator iterator = managerBean.getList(criteria).iterator();
			while (iterator.hasNext()) {
				Activity activity = (Activity) iterator.next();
				SelectItem item = new SelectItem(activity.getId(), activity.getActivityType()
						.getDescription());
				activities.add(item);
			}
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error loading activities related with dossier with id= "
					+ dossierId.toString(), e);
		}
	}

	public void workGroupChange(ValueChangeEvent event) {
		if (event.getNewValue() != null && !"".equals(event.getNewValue())) {
			loadUsers(new Integer(event.getNewValue().toString()));
		} else {
			users = new LinkedList<SelectItem>();
		}
	}

	@SuppressWarnings("unchecked")
	public void loadUsers(Integer workGroupId) {
		users = new LinkedList<SelectItem>();
		try {
			Criteria criteria = new Criteria();
			IManagerBean managerBean;
			if (workGroupId != null) {
				managerBean = BeanManager.getManagerBean(UserWorkGroup.class);
				criteria.addEqualExpression(managerBean
						.getFieldName(IConfigAlias.USER_WORK_GROUP_WORK_GROUP_ID), workGroupId);
				criteria.addOrder(managerBean.getFieldName(IConfigAlias.USER_WORK_GROUP_USER_NAME));
			} else {
				managerBean = BeanManager.getManagerBean(User.class);
				criteria.addOrder(managerBean.getFieldName(IConfigAlias.USER_NAME));
			}

			Iterator iterator = managerBean.getList(criteria).iterator();
			while (iterator.hasNext()) {
				User user;
				if (workGroupId != null) {
					UserWorkGroup userWorkGroup = (UserWorkGroup) iterator.next();
					user = userWorkGroup.getUser();
				} else {
					user = (User) iterator.next();
				}
				SelectItem item = new SelectItem(user.getId(), user.getName());
				users.add(item);
			}
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error loading users of workgroup with id= "
					+ workGroupId.toString(), e);
		}
	}

	@SuppressWarnings("unused")
	public Campaign getTaskCampaign() {
		Task task = (Task) this.getTo();
		if (task.getSource().equals(TaskSource.AON_CONSULTANT)) {
			try {
				ActivityProcess activityProcess = CampaignTaskManager
						.getCurrentActivityProcess(task);
				if (activityProcess != null) {
					return activityProcess.getCampaign();
				}
			} catch (ManagerBeanException e) {
				LOGGER.log(Level.SEVERE, "Error getting campaign from task with id= "
						+ task.getId(), e);
			}
		}
		return null;
	}

	@SuppressWarnings("unused")
	public String getPreviousTaskDescription() {
		Task task = (Task) this.getTo();
		if (task.getSource().equals(TaskSource.AON_CONSULTANT)) {
			Task previousTask = getPreviousTask(task);
			return (previousTask != null) ? previousTask.getDescription() : null;
		}
		return null;
	}

	@SuppressWarnings("unused")
	public String getPreviousTaskEmployee() {
		Task task = (Task) this.getTo();
		if (task.getSource().equals(TaskSource.AON_CONSULTANT)) {
			Task previousTask = getPreviousTask(task);
			User previousUser = (previousTask != null) ? previousTask.getUser() : null;
			if (previousUser != null) {
				return (previousUser.getName());
			}
		}
		return null;
	}

	private Task getPreviousTask(Task task) {
		if (task.getSource().equals(TaskSource.AON_CONSULTANT)) {
			try {
				return CampaignTaskManager.getPreviousTask(task);
			} catch (ManagerBeanException e) {
				LOGGER.log(Level.SEVERE, "Error getting previous task from task with id= "
						+ task.getId(), e);
			}
		}
		return null;
	}

	@SuppressWarnings("unused")
	public void onRemoveTask(ActionEvent event) {
		Task task = (Task) this.getTo();
		removeTask(task);
		if (!isMyTask(task)) {
			addMessage("No se puede Borrar la Tarea. Ha sido asumida por otro Usuario.");
		}
	}

	private void removeTask(Task task) {
		if (isFreeTask(task) || isMyTask(task)) {
			task.setEndDate(new Date());
			task.setStatus(TaskStatus.DELETED);
			task.setUser(getLoggedUser());
			updateTask(task);

			if (task.getSource().equals(TaskSource.AON_CONSULTANT)) {
				finishTaskAlarm(task);
			}
		}
	}

	private void finishTaskAlarm(Task task) {
		try {
			CampaignTaskManager.finishTaskAlarm(task);
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error finishing alarm associated to task with id= "
					+ task.getId(), e);
		}
	}

	@SuppressWarnings("unused")
	public void onAssumeTask(ActionEvent event) {
		Task task = (Task) this.getTo();
		assumeTask(task);
		if (!isMyTask(task)) {
			addMessage("No se puede Asumir la Tarea. Ha sido asumida por otro Usuario.");
		}
	}

	@SuppressWarnings("unused")
	public void onAssumeSelected(ActionEvent event) {
		boolean message = false;
		Iterator<Task> iter = checks.iterator();
		while (iter.hasNext()) {
			Task task = iter.next();
			assumeTask(task);
			if (!message && !isMyTask(task)) {
				addMessage("Existen Tareas que no se han podido asumir por estar asumidas por otros Usuarios.");
				message = true;
			}
		}
		resetChecks();
		onRefresh(event);
	}

	private void assumeTask(Task task) {
		if (isFreeTask(task)) {
			task.setUser(getLoggedUser());
			updateTask(task);
		}
	}

	@SuppressWarnings("unused")
	public void onReleaseTask(ActionEvent event) {
		Task task = (Task) this.getTo();
		releaseTask(task);
	}

	@SuppressWarnings("unused")
	public void onReleaseSelected(ActionEvent event) {
		boolean message = false;
		Iterator<Task> iter = checks.iterator();
		while (iter.hasNext()) {
			Task task = iter.next();
			task = releaseTask(task);
			if (!message && !isFreeTask(task)) {
				addMessage("Existen Tareas que no se han podido liberar por estar asumidas por otros Usuarios.");
				message = true;
			}
		}
		resetChecks();
		onRefresh(event);
	}

	private Task releaseTask(Task task) {
		if (isMyTask(task)) {
			task.setUser(null);
			task = updateTask(task);
		}
		return task;
	}

	private void createNextTask(Task task) {
		try {
			ActivityProcess activityProcess = CampaignTaskManager.getCurrentActivityProcess(task);
			if (activityProcess != null) {
				CampaignDossier campaignDossier = new CampaignDossier();
				campaignDossier.setCampaign(activityProcess.getCampaign());
				campaignDossier.setDossier(task.getDossier());
				CampaignTaskManager.addCampaignTask(campaignDossier, activityProcess
						.getProcessDetail().getPosition() + 1);
			}
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error creating next task for task with id= " + task.getId(),
					e);
		}
	}

	@SuppressWarnings("unchecked")
	private void createNextPeriodicalTask(Task task) {
		try {
			IManagerBean periodTaskBean = BeanManager.getManagerBean(PeriodicalTask.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(periodTaskBean
					.getFieldName(IProjectAlias.PERIODICAL_TASK_TASK_ID), task.getId());
			Iterator iter = periodTaskBean.getList(criteria).iterator();
			if (iter.hasNext()) {
				PeriodicalTask periodTask = (PeriodicalTask) iter.next();
				if (periodTask.getNextDate().before(periodTask.getEndDate())) {
					PeriodicalTaskController periodTaskController = (PeriodicalTaskController) AonUtil
							.getController(PERIOD_TASK_CONTROLLER_NAME);
					Task newTask = creteNewPeriodicalTask(periodTask, task);
					periodTask.setTask(newTask);
					periodTask.setNextDate(periodTaskController.addPeriodToDate(periodTask, newTask
							.getStartDate()));
					periodTaskBean.update(periodTask);
				}
			}
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error creating next periodical task for task with id= "
					+ task.getId(), e);
		}
	}

	private Task creteNewPeriodicalTask(PeriodicalTask periodTask, Task task)
			throws ManagerBeanException {
		Task newTask = new Task();
		newTask.setActivity(task.getActivity());
		newTask.setComments(task.getComments());
		newTask.setDescription(task.getDescription());
		newTask.setDossier(task.getDossier());
		newTask.setPercent(0);
		newTask.setPriority(task.getPriority());
		newTask.setSender(task.getSender());
		newTask.setSource(task.getSource());
		newTask.setStartDate(periodTask.getNextDate());
		newTask.setStatus(TaskStatus.PENDING);
		newTask.setUser(null);
		newTask.setWorkGroup(task.getWorkGroup());
		ensureTask(newTask);
		PeriodicalTaskController periodTaskController = (PeriodicalTaskController) AonUtil
				.getController(PERIOD_TASK_CONTROLLER_NAME);
		newTask.setDueDate(periodTaskController.addPeriodToDate(periodTask, task.getDueDate()));
		IManagerBean taskBean = BeanManager.getManagerBean(Task.class);
		return (Task) taskBean.insert(newTask);
	}

	private void ensureTask(Task task) {
		if (task.getDossier() != null && task.getDossier().getId() == null) {
			task.setDossier(null);
		}
		if (task.getActivity() != null && task.getActivity().getId() == null) {
			task.setActivity(null);
		}
		if (task.getUser() != null && task.getUser().getId() == null) {
			task.setUser(null);
		}
	}

	@SuppressWarnings("unused")
	public void onListStartTask(ActionEvent event) {
		Task task = (Task) model.getRowData();
		startTask(task);
	}

	@SuppressWarnings("unused")
	public void onStartTask(ActionEvent event) {
		Task task = (Task) this.getTo();
		startTask(task);
	}

	private void startTask(Task task) {
		if (isMyTask(task)) {
			task.setStatus(TaskStatus.IN_PROGRESS);
			updateTask(task);
		}
	}

	@SuppressWarnings("unused")
	public void onListStopTask(ActionEvent event) {
		Task task = (Task) model.getRowData();
		stopTask(task);
	}

	@SuppressWarnings("unused")
	public void onStopTask(ActionEvent event) {
		Task task = (Task) this.getTo();
		stopTask(task);
	}

	private void stopTask(Task task) {
		if (isMyTask(task)) {
			task.setStatus(TaskStatus.PENDING);
			updateTask(task);
		}
	}

	@SuppressWarnings("unused")
	public void onListReopenTask(ActionEvent event) {
		Task task = (Task) model.getRowData();
		reopenTask(task);
	}

	@SuppressWarnings("unused")
	public void onReopenTask(ActionEvent event) {
		Task task = (Task) this.getTo();
		reopenTask(task);
	}

	private void reopenTask(Task task) {
		if (isMyTask(task)) {
			task.setStatus(TaskStatus.PENDING);
			updateTask(task);
		}
	}

	public boolean isMonitor() {
		return monitor;
	}

	public void setMonitor(boolean monitor) {
		this.monitor = monitor;
	}

	public void onSwicthMonitor(ActionEvent event) throws ManagerBeanException {
		onRefresh(event);
	}

	public void completeCriteria() {
		try {
			if (!StringUtils.isEmpty(getDescription())) {
				getCriteria().addExpression(DESCRIPTION_ALIAS, getDescription());
			}
			if (getStartDateFrom() != null) {
				getCriteria().addGreaterThanOrEqualExpression(TASK_START_DATE_ALIAS,
						getStartDateFrom());
			}
			if (getStartDateTo() != null) {
				getCriteria().addLessThanOrEqualExpression(TASK_START_DATE_ALIAS, getStartDateTo());
			}
			if (getEndDateFrom() != null) {
				getCriteria()
						.addGreaterThanOrEqualExpression(TASK_END_DATE_ALIAS, getEndDateFrom());
			}
			if (getEndDateTo() != null) {
				getCriteria().addLessThanOrEqualExpression(TASK_END_DATE_ALIAS, getEndDateTo());
			}
			if (getDueDateFrom() != null) {
				getCriteria()
						.addGreaterThanOrEqualExpression(TASK_DUE_DATE_ALIAS, getDueDateFrom());
			}
			if (getEndDateTo() != null) {
				getCriteria().addLessThanOrEqualExpression(TASK_DUE_DATE_ALIAS, getDueDateTo());
			}
			if (getWorkgroup() != null) {
				getCriteria().addEqualExpression(WORKGROUP_ALIAS, getWorkgroup());
			}
			if (getUser() != null) {
				getCriteria().addEqualExpression(USER_ALIAS, getUser());
			}

			if (getDossierId() != null) {
				getCriteria().addEqualExpression(DOSSIER_ALIAS, getDossierId());
			} else {
				if (getCustomer() != null && getCustomer().getId() != null) {
					getCriteria().addEqualExpression(CUSTOMER_ALIAS, customer.getId());
				}
			}
			
			if (getActivityId() != null) {
				getCriteria().addEqualExpression(ACTIVITY_ALIAS, getActivityId());
			}
			if (isStatusDeleted() || isStatusFinished() || isStatusInProgress()
					|| isStatusPending()) {
				// Hay que realizar una expression OR con los valores
				// seleccionados. Como hay
				// cuatro valores de status creamos un array con esas
				// dimensiones y asignamos
				// las expresiones correspondientes al array.
				Expression[] exps = { null, null, null, null };
				int count = 0;
				int inCaseCount1 = -1;
				if (isStatusDeleted()) {
					exps[0] = ExpressionUtilities.getEqualExpression(STATUS_ALIAS,
							TaskStatus.DELETED);
					count++;
					inCaseCount1 = 0;
				}
				if (isStatusFinished()) {
					exps[1] = ExpressionUtilities.getEqualExpression(STATUS_ALIAS,
							TaskStatus.FINISHED);
					count++;
					inCaseCount1 = 1;
				}
				if (isStatusInProgress()) {
					exps[2] = ExpressionUtilities.getEqualExpression(STATUS_ALIAS,
							TaskStatus.IN_PROGRESS);
					count++;
					inCaseCount1 = 2;
				}
				if (isStatusPending()) {
					exps[3] = ExpressionUtilities.getEqualExpression(STATUS_ALIAS,
							TaskStatus.PENDING);
					count++;
					inCaseCount1 = 3;
				}
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
				getCriteria().addExpression(expToAdd);
			}
			addOrder();
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error adding custom expression", e);
		} catch (ExpressionException e) {
			LOGGER.log(Level.SEVERE, "Error adding custom expression", e);
		}
	}

	private void addOrder() throws ManagerBeanException {
		OrderByList list = new OrderByList();
		if (getOrderColumn() == null) {
			list
					.addOrder(new Order(ExpressionUtilities.getIdentifierExpression(USER_ALIAS),
							false));
			list.addOrder(new Order(ExpressionUtilities.getIdentifierExpression(PRIORITY_ALIAS),
					false));
			list.addOrder(new Order(ExpressionUtilities.getIdentifierExpression(STATUS_ALIAS),
					false));
			list.addOrder(new Order(ExpressionUtilities
					.getIdentifierExpression(TASK_DUE_DATE_ALIAS), false));
		} else {
			list.addOrder(new Order(ExpressionUtilities.getIdentifierExpression(getOrderColumn()),
					isOrderAscending()));
		}
		getCriteria().setOrderByList(list);

	}

	// -------------------------------------------------
	// Getters y setters para los campos de la búsqueda.
	// -------------------------------------------------

	public Date getStartDateFrom() {
		return startDateFrom;
	}

	public void setStartDateFrom(Date startDateFrom) {
		this.startDateFrom = startDateFrom;
	}

	public Date getStartDateTo() {
		return startDateTo;
	}

	public void setStartDateTo(Date startDateTo) {
		this.startDateTo = startDateTo;
	}

	public Date getEndDateFrom() {
		return endDateFrom;
	}

	public void setEndDateFrom(Date endDateFrom) {
		this.endDateFrom = endDateFrom;
	}

	public Date getEndDateTo() {
		return endDateTo;
	}

	public void setEndDateTo(Date endDateTo) {
		this.endDateTo = endDateTo;
	}

	public Date getDueDateFrom() {
		return dueDateFrom;
	}

	public void setDueDateFrom(Date dueDateFrom) {
		this.dueDateFrom = dueDateFrom;
	}

	public Date getDueDateTo() {
		return dueDateTo;
	}

	public void setDueDateTo(Date dueDateTo) {
		this.dueDateTo = dueDateTo;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Integer getWorkgroup() {
		return workgroup;
	}

	public void setWorkgroup(Integer workgroup) {
		this.workgroup = workgroup;
	}

	public Integer getUser() {
		return user;
	}

	public void setUser(Integer user) {
		this.user = user;
	}

	public Integer getDossierId() {
		return dossierId;
	}

	public void setDossierId(Integer dossierId) {
		this.dossierId = dossierId;
	}

	public Integer getActivityId() {
		return activityId;
	}

	public void setActivityId(Integer activityId) {
		this.activityId = activityId;
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

	// -------------------------------------------------
	// FIN Getters y setters para los campos de la búsqueda.
	// -------------------------------------------------

	// -------------------------------------------------
	// Operaciones que se realizan sobre una tarea
	// -------------------------------------------------
	public boolean isMyTask() {
		return isMyTask((Task) this.getTo());
	}

	public boolean isMyListTask() {
		try {
			return isMyTask((Task) this.getModel().getRowData());
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error getting Task Model", e);
		}
		return false;
	}

	public boolean isMyTask(Task task) {
		return (task.getUser() == null) ? false : getLoggedUser().getId().equals(
				task.getUser().getId());
	}

	@SuppressWarnings("unused")
	public void onFinishSelected(ActionEvent event) {
		boolean message = false;
		Iterator<Task> iter = checks.iterator();
		while (iter.hasNext()) {
			Task task = iter.next();
			finishTask(task);
			if (!message && !isMyTask(task)) {
				addMessage("Existen Tareas que no se han podido finalizar por estar asumidas por otros Usuarios.");
				message = true;
			}
		}
		resetChecks();
		onRefresh(event);
	}

	@SuppressWarnings("unused")
	public void onFinishTask(ActionEvent event) {
		Task task = (Task) this.getTo();
		finishTask(task);
		onRefresh(event);
	}

	private void finishTask(Task task) {
		if (isMyTask(task) || isFreeTask(task)) {
			task.setEndDate(new Date());
			task.setStatus(TaskStatus.FINISHED);
			task.setUser(getLoggedUser());
			updateTask(task);

			if (task.getSource().equals(TaskSource.AON_CONSULTANT)) {
				finishTaskAlarm(task);
				createNextTask(task);
			} else {
				if (task.getSource().equals(TaskSource.PERIODICAL)) {
					createNextPeriodicalTask(task);
				}
			}
		} else {
			addMessage("No se puede Finalizar la Tarea. Ha sido asumida por otro Usuario.");
		}
	}

	private Task updateTask(Task task) {
		try {
			restoreNullSubPOJOs(task);
			task = (Task) getManagerBean().update(task);
			getManagerBean().initializePOJO(task);
			return task;
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error updating task with id= " + task.getId(), e);
		}
		return null;
	}

	// -------------------------------------------------
	// FIN Operaciones que se realizan sobre una tarea
	// -------------------------------------------------

	public boolean isAssumeSelectedDisabled() {
		for (Task task : checks) {
			if (!isFreeTask(task)) {
				return true;
			}
		}
		return (checks.size() == 0);
	}

	public boolean isReleaseSelectedDisabled() {
		for (Task task : checks) {
			if (!isMyTask(task) || task.isInProgress()) {
				return true;
			}
		}
		return (checks.size() == 0);
	}

	public boolean isFinishSelectedDisabled() {
		for (Task task : checks) {
			if (!(isMyTask(task) || isFreeTask(task))) {
				return true;
			}
		}
		return (checks.size() == 0);
	}

	public String getOrderColumn() {
		return orderColumn;
	}

	public void setOrderColumn(String orderColumn) {
		if (orderColumn.equals(this.orderColumn)) {
			setOrderAscending(!orderAscending);
		} else {
			setOrderAscending(orderColumn != PRIORITY_ALIAS && orderColumn != USER_ALIAS);
		}
		this.orderColumn = orderColumn;
	}

	public boolean isOrderAscending() {
		return orderAscending;
	}

	public void setOrderAscending(boolean orderAscending) {
		this.orderAscending = orderAscending;
	}

	public void sortByDescription(ActionEvent event) {
		setOrderColumn(DESCRIPTION_ALIAS);
		onRefresh(event);
	}

	public boolean isSortedByDescription() {
		return (DESCRIPTION_ALIAS == getOrderColumn());
	}

	public void sortByPriority(ActionEvent event) {
		setOrderColumn(PRIORITY_ALIAS);
		onRefresh(event);
	}

	public boolean isSortedByPriority() {
		return (PRIORITY_ALIAS == getOrderColumn());
	}

	public void sortByStatus(ActionEvent event) {
		setOrderColumn(STATUS_ALIAS);
		onRefresh(event);
	}

	public boolean isSortedByStatus() {
		return (STATUS_ALIAS == getOrderColumn());
	}

	public void sortByUser(ActionEvent event) {
		setOrderColumn(USER_NAME_ALIAS);
		onRefresh(event);
	}

	public boolean isSortedByUser() {
		return (USER_NAME_ALIAS == getOrderColumn());
	}

	public void sortByWorkGroup(ActionEvent event) {
		setOrderColumn(WORKGROUP_ALIAS);
		onRefresh(event);
	}

	public boolean isSortedByWorkGroup() {
		return (WORKGROUP_ALIAS == getOrderColumn());
	}

	public void sortByDossier(ActionEvent event) {
		setOrderColumn(DOSSIER_NUMBER_ALIAS);
		onRefresh(event);
	}

	public boolean isSortedByDossier() {
		return (DOSSIER_NUMBER_ALIAS == getOrderColumn());
	}

	public void sortByStartDate(ActionEvent event) {
		setOrderColumn(TASK_START_DATE_ALIAS);
		onRefresh(event);
	}

	public boolean isSortedByStartDate() {
		return (TASK_START_DATE_ALIAS == getOrderColumn());
	}

	public void sortByDueDate(ActionEvent event) {
		setOrderColumn(TASK_DUE_DATE_ALIAS);
		onRefresh(event);
	}

	public boolean isSortedByDueDate() {
		return (TASK_DUE_DATE_ALIAS == getOrderColumn());
	}

	public void sortByPercent(ActionEvent event) {
		setOrderColumn(PERCENT_ALIAS);
		onRefresh(event);
	}

	public boolean isSortedByPercent() {
		return (PERCENT_ALIAS == getOrderColumn());
	}

	public String getOrderAscendingLiteral() {
		return isOrderAscending() ? ASCENDING : DESCENDING;
	}

	public boolean isRichEditor() {
		return richEditor;
	}

	public void setRichEditor(boolean richEditor) {
		this.richEditor = richEditor;
	}
}