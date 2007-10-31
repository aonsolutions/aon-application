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
import javax.faces.model.DataModel;
import javax.faces.model.SelectItem;

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
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.config.util.UserUtils;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.menu.jsf.MenuEvent;
import com.code.aon.ui.project.util.CampaignTaskManager;
import com.code.aon.ui.util.AonUtil;

public class TaskController extends BasicController implements ITaskController {

    private static final Logger LOGGER = Logger.getLogger(TaskController.class.getName());

	private static final String PERIOD_TASK_CONTROLLER_NAME = "periodTask";

    private Expression myStatusExpression;

    private Customer customer;
    private List<SelectItem> dossiers = new LinkedList<SelectItem>();
    private List<SelectItem> activities = new LinkedList<SelectItem>();

    private List<SelectItem> users = new LinkedList<SelectItem>();

    private ArrayList<Task> checks = new ArrayList<Task>();
    

    public Expression getMyStatusExpression() {
        return myStatusExpression;
    }

    public void setMyStatusExpression(Expression myStatusExpression) {
        this.myStatusExpression = myStatusExpression;
    }

    public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public List<SelectItem> getDossiers() {
        return dossiers;
    }

    public void setDossiers(List<SelectItem> dossiers) {
        this.dossiers = dossiers;
    }

    public List<SelectItem> getActivities() {
        return activities;
    }

    public void setActivities(List<SelectItem> activities) {
        this.activities = activities;
    }

    public List<SelectItem> getUsers() {
        return users;
    }

    public void setUsers(List<SelectItem> users) {
        this.users = users;
    }

    public void resetChecks() {
        checks = new ArrayList<Task>();
    }

    public boolean getRowChecked() {
        Task to = (Task)model.getRowData();
        return checks.contains(to);
    }

    public void setRowChecked(boolean rowChecked) {
        if (rowChecked) {
            Task to = (Task)model.getRowData();
            if (!checks.contains(to)) {
                checks.add(to);
            }
        } else {
            Task to = (Task)model.getRowData();
            if (checks.contains(to)) {
                checks.remove(to);
            }
        }
    }
    
    public void rowSelected(ValueChangeEvent event){
        if(event.getNewValue() != null){
            setRowChecked(((Boolean)event.getNewValue()).booleanValue());
        }
    }

	@SuppressWarnings("unused")
    public void onSearch(MenuEvent event) {
    	obtainTaskInbox();
    }
    
    @SuppressWarnings("unused")
    public void onTaskInbox(ActionEvent event){
    	obtainTaskInbox();
    }
    
    private void obtainTaskInbox(){
    	try{
            Expression expr1 = ExpressionUtilities.getEqualExpression(getFieldName(IProjectAlias.TASK_STATUS), TaskStatus.PENDING);
            Expression expr2 = ExpressionUtilities.getEqualExpression(getFieldName(IProjectAlias.TASK_STATUS), TaskStatus.IN_PROGRESS);
            setMyStatusExpression(ExpressionUtilities.getOrExpression(expr1, expr2));

            setCriteria(new Criteria());
            super.onSearch(null);
        } catch (ManagerBeanException e) {
            LOGGER.log(Level.SEVERE, "Error initializing Task Model", e);
        }
    }
    
    public DataModel getInboxModel(){
    	obtainTaskInbox();
    	return this.model;
    }

    @Override
    public void onEditSearch(ActionEvent event) {
        setMyStatusExpression(null);
        super.onEditSearch(event);
    }
    
    public void addStartDateFromExpression(ValueChangeEvent event){
        if(event.getNewValue() != null) {
            try {
                getCriteria().addGreaterThanOrEqualExpression(getFieldName(IProjectAlias.TASK_START_DATE), event.getNewValue());
            } catch (ManagerBeanException e) {
                LOGGER.log(Level.SEVERE, "Error adding FROM start date expression", e);
            }
        }
    }
    
    public void addStartDateToExpression(ValueChangeEvent event){
        if(event.getNewValue() != null) {
            try {
                getCriteria().addLessThanOrEqualExpression(getFieldName(IProjectAlias.TASK_START_DATE), event.getNewValue());
            } catch (ManagerBeanException e) {
                LOGGER.log(Level.SEVERE, "Error adding TO start date expression", e);
            }
        }
    }

    public void addEndDateFromExpression(ValueChangeEvent event){
        if(event.getNewValue() != null) {
            try {
                getCriteria().addGreaterThanOrEqualExpression(getFieldName(IProjectAlias.TASK_END_DATE), event.getNewValue());
            } catch (ManagerBeanException e) {
                LOGGER.log(Level.SEVERE, "Error adding FROM end date expression", e);
            }
        }
    }
    
    public void addEndDateToExpression(ValueChangeEvent event){
        if(event.getNewValue() != null) {
            try {
                getCriteria().addLessThanOrEqualExpression(getFieldName(IProjectAlias.TASK_END_DATE), event.getNewValue());
            } catch (ManagerBeanException e) {
                LOGGER.log(Level.SEVERE, "Error adding TO end date expression", e);
            }
        }
    }

    public void addDueDateFromExpression(ValueChangeEvent event){
        if(event.getNewValue() != null) {
            try {
                getCriteria().addGreaterThanOrEqualExpression(getFieldName(IProjectAlias.TASK_DUE_DATE), event.getNewValue());
            } catch (ManagerBeanException e) {
                LOGGER.log(Level.SEVERE, "Error adding FROM due date expression", e);
            }
        }
    }
    
    public void addDueDateToExpression(ValueChangeEvent event){
        if(event.getNewValue() != null) {
            try {
                getCriteria().addLessThanOrEqualExpression(getFieldName(IProjectAlias.TASK_DUE_DATE), event.getNewValue());
            } catch (ManagerBeanException e) {
                LOGGER.log(Level.SEVERE, "Error adding TO due date expression", e);
            }
        }
    }

    public void addCustomerExpression(ValueChangeEvent event) {
        if(event.getNewValue() != null && !event.getNewValue().equals("")) {
            try {
                getCriteria().addEqualExpression(getFieldName(IProjectAlias.TASK_CUSTOMER_ID), new Integer(event.getNewValue().toString()));
            } catch (ManagerBeanException e) {
                LOGGER.log(Level.SEVERE, "Error adding customer expression", e);
            }
        }
    }

    public void addStatusExpression(ValueChangeEvent event){
        setMyStatusExpression(null);
        if(event.getNewValue() != null) {
            try {
                setMyStatusExpression(ExpressionUtilities.getEqualExpression(getFieldName(IProjectAlias.TASK_STATUS), event.getNewValue()));
            } catch (ManagerBeanException e) {
                LOGGER.log(Level.SEVERE, "Error adding status expression", e);
            }
        }
    }

    public boolean isPending() {
        return isPending((Task)this.getTo());
    }

    public boolean isListPending() {
        try {
            return isPending((Task)this.getModel().getRowData());
        } catch (ManagerBeanException e) {
            LOGGER.log(Level.SEVERE, "Error getting Task Model", e);
        }
        return false;
    }

    public boolean isPending(Task task) {
        return task.getStatus().equals(TaskStatus.PENDING);
    }

    public boolean isInProgress() {
        return isInProgress((Task)this.getTo());
    }

    public boolean isListInProgress() {
        try {
            return isInProgress((Task)this.getModel().getRowData());
        } catch (ManagerBeanException e) {
            LOGGER.log(Level.SEVERE, "Error getting Task Model", e);
        }
        return false;
    }

    public boolean isInProgress(Task task) {
        return task.getStatus().equals(TaskStatus.IN_PROGRESS);
    }

    public boolean isFinished() {
        return ((Task)this.getTo()).getStatus().equals(TaskStatus.FINISHED);
    }

    public boolean isDeleted() {
        return ((Task)this.getTo()).getStatus().equals(TaskStatus.DELETED);
    }

    public boolean isSourceAssigned() {
        return ((Task)this.getTo()).getSource().equals(TaskSource.ASSIGNED);
    }

    public boolean isSourceCampaign() {
        return ((Task)this.getTo()).getSource().equals(TaskSource.AON_CONSULTANT);
    }

    public boolean isMyTask() {
        return isMyTask((Task)this.getTo());
    }

    public boolean isMyListTask() {
        try {
            return isMyTask((Task)this.getModel().getRowData());
        } catch (ManagerBeanException e) {
            LOGGER.log(Level.SEVERE, "Error getting Task Model", e);
        }
        return false;
    }

    public boolean isMyTask(Task task) {
        return (task.getUser() == null) ? false : UserUtils.getLoggedUser().getId().equals(task.getUser().getId());
    }

    public boolean isFreeTask() {
        return isFreeTask((Task)this.getTo());
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
            dossiers = new LinkedList<SelectItem>();
        }
        activities = new LinkedList<SelectItem>();
    }

    @SuppressWarnings("unchecked")
    public void loadDossiers(Integer customerId) {
        dossiers = new LinkedList<SelectItem>();
        try {
            IManagerBean managerBean = BeanManager.getManagerBean(Dossier.class);
            Criteria criteria = new Criteria();
            criteria.addEqualExpression(managerBean.getFieldName(IProjectAlias.DOSSIER_CUSTOMER_ID), customerId);
            criteria.addEqualExpression(managerBean.getFieldName(IProjectAlias.DOSSIER_STATUS), DossierStatus.ACTIVE);
            Iterator iterator = managerBean.getList(criteria).iterator();
            while (iterator.hasNext()) {
                Dossier dossier = (Dossier)iterator.next();
                SelectItem item = new SelectItem(dossier.getId(), dossier.getNumber());
                dossiers.add(item);
            }
        } catch (ManagerBeanException e) {
            LOGGER.log(Level.SEVERE, "Error loading dossiers related with customer with id= " + customerId.toString(), e);
        }
    }

    public void dossierChange(ValueChangeEvent event) {
        if (event.getNewValue() != null && !"".equals(event.getNewValue())) {
            loadActivities(new Integer(event.getNewValue().toString()));
        } else {
            activities = new LinkedList<SelectItem>();
        }
    }

    @SuppressWarnings("unchecked")
    public void loadActivities(Integer dossierId) {
        activities = new LinkedList<SelectItem>();
        try {
            IManagerBean managerBean = BeanManager.getManagerBean(Activity.class);
            Criteria criteria = new Criteria();
            criteria.addEqualExpression(managerBean.getFieldName(IProjectAlias.ACTIVITY_DOSSIER_ID), dossierId);
            Iterator iterator = managerBean.getList(criteria).iterator();
            while (iterator.hasNext()) {
                Activity activity = (Activity)iterator.next();
                SelectItem item = new SelectItem(activity.getId(), activity.getActivityType().getDescription());
                activities.add(item);
            }
        } catch (ManagerBeanException e) {
            LOGGER.log(Level.SEVERE, "Error loading activities related with dossier with id= " + dossierId.toString(), e);
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
            IManagerBean managerBean = BeanManager.getManagerBean(UserWorkGroup.class);
            Criteria criteria = new Criteria();
            criteria.addEqualExpression(managerBean.getFieldName(IConfigAlias.USER_WORK_GROUP_WORK_GROUP_ID), workGroupId);
            criteria.addOrder(managerBean.getFieldName(IConfigAlias.USER_WORK_GROUP_USER_NAME));
            Iterator iterator = managerBean.getList(criteria).iterator();
            while (iterator.hasNext()) {
            	UserWorkGroup userWorkGroup = (UserWorkGroup)iterator.next();
               	SelectItem item = new SelectItem(userWorkGroup.getUser().getId(), userWorkGroup.getUser().getName());
                users.add(item);                	
            }
        } catch (ManagerBeanException e) {
            LOGGER.log(Level.SEVERE, "Error loading users of workgroup with id= " + workGroupId.toString(), e);
        }
    }

	@SuppressWarnings("unused")
    public Campaign getTaskCampaign() {
        Task task = (Task)this.getTo();
        if (task.getSource().equals(TaskSource.AON_CONSULTANT)) {
            try {
                ActivityProcess activityProcess = CampaignTaskManager.getCurrentActivityProcess(task);
                if (activityProcess != null) {
                    return activityProcess.getCampaign();
                }
            } catch (ManagerBeanException e) {
                LOGGER.log(Level.SEVERE, "Error getting campaign from task with id= " + task.getId(), e);
            }
        }
        return null;
    }

    @SuppressWarnings("unused")
    public String getPreviousTaskDescription() {
        Task task = (Task)this.getTo();
        if (task.getSource().equals(TaskSource.AON_CONSULTANT)) {
            Task previousTask = getPreviousTask(task);
            return (previousTask != null) ? previousTask.getDescription() : null;
        }
        return null;
    }

    @SuppressWarnings("unused")
    public String getPreviousTaskEmployee() {
        Task task = (Task)this.getTo();
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
                LOGGER.log(Level.SEVERE, "Error getting previous task from task with id= " + task.getId(), e);
            }
        }
        return null;
    }

    @SuppressWarnings("unused")
    public void onRemoveTask(ActionEvent event) {
        Task task = (Task)this.getTo();
        removeTask(task);
        if (!isMyTask(task)) {
            addMessage("No se puede Borrar la Tarea. Ha sido asumida por otro Usuario.");
        }
    }

    private void removeTask(Task task) {
        if (isFreeTask(task) || isMyTask(task)) {
            task.setEndDate(new Date());
            task.setStatus(TaskStatus.DELETED);
            task.setUser(UserUtils.getLoggedUser());
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
            LOGGER.log(Level.SEVERE, "Error finishing alarm associated to task with id= " + task.getId(), e);
        }
    }

    @SuppressWarnings("unused")
    public void onAssumeTask(ActionEvent event) {
        Task task = (Task)this.getTo();
        assumeTask(task);
        if (!isMyTask(task)) {
            addMessage("No se puede Asumir la Tarea. Ha sido asumida por otro Usuario.");
        }
    }

    @SuppressWarnings("unused")
    public void onAssumeSelected(ActionEvent event) {
        boolean message = false;
        Iterator<Task> iter = checks.iterator();
        while(iter.hasNext()){
            Task task = iter.next();
            assumeTask(task);
            if (!message && !isMyTask(task)) {
                addMessage("Existen Tareas que no se han podido asumir por estar asumidas por otros Usuarios.");
                message = true;
            }
        }
        resetChecks();
    }

    private void assumeTask(Task task) {
        if (isFreeTask(task)) {
            task.setUser(UserUtils.getLoggedUser());
            updateTask(task);
        }
    }

    @SuppressWarnings("unused")
    public void onReleaseTask(ActionEvent event) {
        Task task = (Task)this.getTo();
        releaseTask(task);
    }

    @SuppressWarnings("unused")
    public void onReleaseSelected(ActionEvent event) {
        boolean message = false;
        Iterator<Task> iter = checks.iterator();
        while(iter.hasNext()){
            Task task = iter.next();
            releaseTask(task);
            if (!message && !isFreeTask(task)) {
                addMessage("Existen Tareas que no se han podido liberar por estar asumidas por otros Usuarios.");
                message = true;
            }
        }
        resetChecks();
    }

    private void releaseTask(Task task) {
        if (isMyTask(task)) {
            task.setUser(null);
            updateTask(task);
        }
    }

    @SuppressWarnings("unused")
    public void onFinishTask(ActionEvent event) {
        Task task = (Task)this.getTo();
        finishTask(task);
        if (!isMyTask(task)) {
            addMessage("No se puede Finalizar la Tarea. Ha sido asumida por otro Usuario.");
        }
    }

    @SuppressWarnings("unused")
    public void onFinishSelected(ActionEvent event) {
        boolean message = false;
        Iterator<Task> iter = checks.iterator();
        while(iter.hasNext()){
            Task task = iter.next();
            finishTask(task);
            if (!message && !isMyTask(task)) {
                addMessage("Existen Tareas que no se han podido finalizar por estar asumidas por otros Usuarios.");
                message = true;
            }
        }
        resetChecks();
    }

    private void finishTask(Task task) {
        if (isFreeTask(task) || isMyTask(task)) {
            task.setEndDate(new Date());
            task.setStatus(TaskStatus.FINISHED);
            task.setUser(UserUtils.getLoggedUser());
            updateTask(task);

            if (task.getSource().equals(TaskSource.AON_CONSULTANT)) {
                finishTaskAlarm(task);
                createNextTask(task);
            } else {
            	if(task.getSource().equals(TaskSource.PERIODICAL)){
            		createNextPeriodicalTask(task);
            	}
            }
        }
    }

	private void createNextTask(Task task) {
		try {
		    ActivityProcess activityProcess = CampaignTaskManager.getCurrentActivityProcess(task);
            if (activityProcess != null) {
                CampaignDossier campaignDossier = new CampaignDossier();
                campaignDossier.setCampaign(activityProcess.getCampaign());
                campaignDossier.setDossier(task.getDossier());
                CampaignTaskManager.addCampaignTask(campaignDossier, activityProcess.getProcessDetail().getPosition() + 1);
            }
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error creating next task for task with id= " + task.getId(), e);
		}
    }
	
	@SuppressWarnings("unchecked")
    private void createNextPeriodicalTask(Task task) {
		try {
			IManagerBean periodTaskBean = BeanManager.getManagerBean(PeriodicalTask.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(periodTaskBean.getFieldName(IProjectAlias.PERIODICAL_TASK_TASK_ID), task.getId());
			Iterator iter = periodTaskBean.getList(criteria).iterator();
			if(iter.hasNext()){
				PeriodicalTask periodTask = (PeriodicalTask)iter.next();
				if(periodTask.getNextDate().before(periodTask.getEndDate())){
					PeriodicalTaskController periodTaskController = (PeriodicalTaskController)AonUtil.getController(PERIOD_TASK_CONTROLLER_NAME);
					Task newTask = creteNewPeriodicalTask(periodTask, task);
					periodTask.setTask(newTask);
					periodTask.setNextDate(periodTaskController.addPeriodToDate(periodTask, newTask.getStartDate()));
					periodTaskBean.update(periodTask);
				}
			}
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error creating next periodical task for task with id= " + task.getId(), e);
		}
	}

    private Task creteNewPeriodicalTask(PeriodicalTask periodTask, Task task) throws ManagerBeanException {
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
    	PeriodicalTaskController periodTaskController = (PeriodicalTaskController)AonUtil.getController(PERIOD_TASK_CONTROLLER_NAME);
    	newTask.setDueDate(periodTaskController.addPeriodToDate(periodTask, task.getDueDate()));
    	IManagerBean taskBean = BeanManager.getManagerBean(Task.class);
		return (Task)taskBean.insert(newTask);
	}

	@SuppressWarnings("unused")
    public void onStartTask(ActionEvent event) {
        Task task = (Task)this.getTo();
        startTask(task);
    }

    private void startTask(Task task) {
        if (isMyTask(task)) {
            task.setStatus(TaskStatus.IN_PROGRESS);
            updateTask(task);
        }
    }

    @SuppressWarnings("unused")
    public void onStopTask(ActionEvent event) {
        Task task = (Task)this.getTo();
        stopTask(task);
    }

    private void stopTask(Task task) {
        if (isMyTask(task)) {
            task.setStatus(TaskStatus.PENDING);
            updateTask(task);
        }
    }

    @SuppressWarnings("unused")
    public void onReopenTask(ActionEvent event) {
        Task task = (Task)this.getTo();
        reopenTask(task);
    }

    private void reopenTask(Task task) {
        if (isMyTask(task)) {
            task.setStatus(TaskStatus.PENDING);
            updateTask(task);
        }
    }

    private Task updateTask(Task task) {
        try {
            restoreNullSubPOJOs(task);
            task = (Task)getManagerBean().update(task);
            getManagerBean().initializePOJO(task);
            return task;
        } catch (ManagerBeanException e) {
            LOGGER.log(Level.SEVERE, "Error updating task with id= " + task.getId(), e);
        }
        return null;
    }

}