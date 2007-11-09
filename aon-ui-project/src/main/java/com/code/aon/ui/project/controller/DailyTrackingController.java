package com.code.aon.ui.project.controller;

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

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.sql.DAOException;
import com.code.aon.config.UserWorkGroup;
import com.code.aon.config.dao.IConfigAlias;
import com.code.aon.customer.Customer;
import com.code.aon.customer.dao.ICustomerAlias;
import com.code.aon.customer.enumeration.CustomerStatus;
import com.code.aon.project.Activity;
import com.code.aon.project.DailyTracking;
import com.code.aon.project.Dossier;
import com.code.aon.project.Task;
import com.code.aon.project.dao.IProjectAlias;
import com.code.aon.project.enumeration.DossierStatus;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.config.util.UserUtils;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.PageDataModel;
import com.code.aon.ui.menu.jsf.MenuEvent;
import com.code.aon.ui.report.OutputFormat;
import com.code.aon.ui.report.ReportException;
import com.code.aon.ui.report.controller.ReportManager;
import com.code.aon.ui.util.AonUtil;

public class DailyTrackingController extends BasicController {

    private static final Logger LOGGER = Logger.getLogger(DailyTrackingController.class.getName());

	private static final String DAILY_TRACKING_CUSTOMER_STATUS = "DailyTracking.customer.status";
    
    private List<SelectItem> dossiers = new LinkedList<SelectItem>();
    private List<SelectItem> activities = new LinkedList<SelectItem>();
    private List<SelectItem> users = new LinkedList<SelectItem>();
    
    private DataModel finishedTaskModel;
    
    private boolean reportMode;
    
    private Integer customerId;
    
    private CustomerStatus customerStatus;
    
    private String customerName;
    
    private Integer workgroupId;
    
    private Integer userId;
    
    private Integer dossierId;
    
    private Integer activityId;
    
    private Date fromDate;
    
    private Date toDate;

    private Integer jobTypeId;
    
    private OutputFormat outputFormat;
    
    
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

	public DataModel getFinishedTaskModel() {
		return finishedTaskModel;
	}

	public void setFinishedTaskModel(DataModel finishedTaskModel) {
		this.finishedTaskModel = finishedTaskModel;
	}
	
	public boolean isReportMode() {
		return reportMode;
	}

	public void setReportMode(boolean reportMode) {
		this.reportMode = reportMode;
	}

	public Integer getCustomerId() {
		return customerId;
	}

	public void setCustomerId(Integer customerId) {
		this.customerId = customerId;
	}

	public CustomerStatus getCustomerStatus() {
		return customerStatus;
	}

	public void setCustomerStatus(CustomerStatus customerStatus) {
		this.customerStatus = customerStatus;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public Integer getWorkgroupId() {
		return workgroupId;
	}

	public void setWorkgroupId(Integer workgroupId) {
		this.workgroupId = workgroupId;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
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

	public Integer getJobTypeId() {
		return jobTypeId;
	}

	public void setJobTypeId(Integer jobTypeId) {
		this.jobTypeId = jobTypeId;
	}

	public Date getFromDate() {
		return fromDate;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public Date getToDate() {
		return toDate;
	}

	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}

	public OutputFormat getOutputFormat() {
		return outputFormat;
	}

	public void setOutputFormat(OutputFormat outputFormat) {
		this.outputFormat = outputFormat;
	}

	public void onReset(MenuEvent event) {
        super.onReset(event);
    }
	
    public void customerChange(ValueChangeEvent event) {
        if (event.getNewValue() != null && !"".equals(event.getNewValue())) {
            loadDossiers(new Integer(event.getNewValue().toString()));
            loadCustomer(new Integer(event.getNewValue().toString()));
        } else {
            dossiers = new LinkedList<SelectItem>();
        }
        activities = new LinkedList<SelectItem>();
    }
    
    public void workgroupChanged(ValueChangeEvent event) {
    	if(event.getNewValue() != null && !event.getNewValue().equals("")){
    		loadUsers(new Integer(event.getNewValue().toString()));
    	}else{
    		users = new LinkedList<SelectItem>();
    	}
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

    @SuppressWarnings("unchecked")
	private void loadCustomer(Integer id) {
		try {
			IManagerBean customerBean = BeanManager.getManagerBean(Customer.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(customerBean.getFieldName(ICustomerAlias.CUSTOMER_ID), id);
			Iterator iter = customerBean.getList(criteria).iterator();
			if(iter.hasNext()){
				Customer customer = (Customer)iter.next();
				((DailyTracking)this.getTo()).setCustomer(customer);
			}
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error loading customer with id= " + id, e);
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
    
    @SuppressWarnings("unchecked")
    public void loadUsers(Integer workgroupId){
    	users = new LinkedList<SelectItem>();
    	try {
			IManagerBean userWorkGroupBean = BeanManager.getManagerBean(UserWorkGroup.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(userWorkGroupBean.getFieldName(IConfigAlias.USER_WORK_GROUP_WORK_GROUP_ID), workgroupId);
			Iterator iter = userWorkGroupBean.getList(criteria).iterator();
			while(iter.hasNext()){
				UserWorkGroup userWorkGroup = (UserWorkGroup)iter.next();
				SelectItem item = new SelectItem(userWorkGroup.getUser().getId(), userWorkGroup.getUser().getName());
				users.add(item);
			}
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error loading users related with workgroup with id= " + workgroupId, e);
		}
    }
    
    @SuppressWarnings({"unused","unchecked"})
    public void onLoadFinishedTasks(ActionEvent event){
    	try {
			IManagerBean taskBean = BeanManager.getManagerBean(Task.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(taskBean.getFieldName(IProjectAlias.TASK_USER_ID), UserUtils.getLoggedUser().getId());
			criteria.addEqualExpression(taskBean.getFieldName(IProjectAlias.TASK_END_DATE), new Date());
			finishedTaskModel = new PageDataModel(null,20);
			List taskList = taskBean.getList(criteria, 0, 20);
			finishedTaskModel.setWrappedData(taskList);
			((PageDataModel)finishedTaskModel).resize(taskList.size());
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error loading finished tasks", e);
		}
    }
    
    @SuppressWarnings("unused")
    public void onImportTask(ActionEvent event){
    	Task task = (Task)getFinishedTaskModel().getRowData();
    	DailyTracking tracking = (DailyTracking)this.getTo();
    	tracking.setTrackingDate(new Date());
    	if (task.getDossier() != null) {
    		tracking.setCustomer(task.getDossier().getCustomer());
    		tracking.setDossier(task.getDossier());
    		tracking.setActivity(task.getActivity()==null?new Activity():task.getActivity());
    		loadDossiers(task.getDossier().getCustomer().getId());
    		loadActivities(task.getDossier().getId());
    	}
    }
    
    public void addTrackingDateFromExpression(ValueChangeEvent event){
        if(event.getNewValue() != null) {
            try {
                IManagerBean dailyTrackingBean = BeanManager.getManagerBean(DailyTracking.class);
                getCriteria().addGreaterThanOrEqualExpression(dailyTrackingBean.getFieldName(IProjectAlias.DAILY_TRACKING_TRACKING_DATE), event.getNewValue());
            } catch (ManagerBeanException e) {
            	LOGGER.log(Level.SEVERE, "Error adding FROM date expression", e);
            }
        }
    }
    
    public void addTrackingDateToExpression(ValueChangeEvent event){
        if(event.getNewValue() != null){
            try {
            	IManagerBean dailyTrackingBean = BeanManager.getManagerBean(DailyTracking.class);
                getCriteria().addLessThanOrEqualExpression(dailyTrackingBean.getFieldName(IProjectAlias.DAILY_TRACKING_TRACKING_DATE), event.getNewValue());
            } catch (ManagerBeanException e) {
                LOGGER.log(Level.SEVERE, "Error adding TO date expression", e);
            }
        }
    }

    public void addEqualExpression(ValueChangeEvent event) throws ManagerBeanException, ExpressionException {
        if (event.getNewValue() != null && !"".equals(event.getNewValue())) {
            Object value = event.getNewValue();
            Criteria criteria = getCriteria();
            criteria.addExpression(getFieldName(event.getComponent().getId()), value.toString());
            setCriteria(criteria);
        }
    }
    
    public void addCustomerExpression(ValueChangeEvent event) {
    	if(event.getNewValue() != null && !"".equals(event.getNewValue().toString().trim())){
			try {
				IManagerBean dailyTrackingBean = BeanManager.getManagerBean(DailyTracking.class);
	    		getCriteria().addEqualExpression(dailyTrackingBean.getFieldName(IProjectAlias.DAILY_TRACKING_CUSTOMER_ID), new Integer(event.getNewValue().toString()));
			} catch (ManagerBeanException e) {
				LOGGER.log(Level.SEVERE, "Error adding customer Expression", e);
			}
    	}
    }
    
    public void reloadDossiers(ValueChangeEvent event){
    	if (event.getNewValue() != null && !"".equals(event.getNewValue())) {
            loadDossiers(new Integer(event.getNewValue().toString()));
        } else {
            dossiers = new LinkedList<SelectItem>();
        }
        activities = new LinkedList<SelectItem>();
    }

    @SuppressWarnings("unused")
    public void onResetReportSearch(MenuEvent event){
    	setReportMode(true);
    	initializeSearchParameters();
    }
	
    @SuppressWarnings("unused")
    public void disableReportMode(MenuEvent event){
    	setReportMode(false);
    }
    
    private void initializeSearchParameters() {
		setCustomerId(null);
		setCustomerName("");
		setWorkgroupId(null);
		setUserId(null);
		setDossierId(null);
		setActivityId(null);
		setJobTypeId(null);
		setFromDate(null);
		setToDate(null);
		setCustomerStatus(CustomerStatus.ACTIVE);
	}

    private Criteria createCriteria() throws ManagerBeanException {
		Criteria criteria = new Criteria();
		IManagerBean bean = getManagerBean();
		criteria.addEqualExpression(DAILY_TRACKING_CUSTOMER_STATUS, getCustomerStatus());
		if(getCustomerId() != null){
			criteria.addEqualExpression(bean.getFieldName(IProjectAlias.DAILY_TRACKING_CUSTOMER_ID), getCustomerId());
		}
		if(getDossierId() != null){
			criteria.addEqualExpression(bean.getFieldName(IProjectAlias.DAILY_TRACKING_DOSSIER_ID), getDossierId());
		}
		if(getActivityId() != null){
			criteria.addEqualExpression(bean.getFieldName(IProjectAlias.DAILY_TRACKING_ACTIVITY_ID), getActivityId());
		}
		if(getJobTypeId() != null){
			criteria.addEqualExpression(bean.getFieldName(IProjectAlias.DAILY_TRACKING_JOB_TYPE_ID), getJobTypeId());
		}
		if(getFromDate() != null){
			criteria.addGreaterThanOrEqualExpression(bean.getFieldName(IProjectAlias.DAILY_TRACKING_TRACKING_DATE), getFromDate());
		}
		if(getToDate() != null){
			criteria.addLessThanOrEqualExpression(bean.getFieldName(IProjectAlias.DAILY_TRACKING_TRACKING_DATE), getToDate());
		}
		if(getWorkgroupId() != null && getUserId() == null){
			Expression expression = obtainWorkGroupExpression(getWorkgroupId());
			if(expression != null){
				criteria.addExpression(expression);
			}
		}
		if(getUserId() != null){
			criteria.addEqualExpression(bean.getFieldName(IProjectAlias.DAILY_TRACKING_USER_ID), getUserId());
		}
		return criteria;
	}

    @SuppressWarnings("unchecked")
	private Expression obtainWorkGroupExpression(Integer workgroupId) {
		try {
			IManagerBean userWorkGroupBean = BeanManager.getManagerBean(UserWorkGroup.class);
			IManagerBean dailyTrackingBean = BeanManager.getManagerBean(DailyTracking.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(userWorkGroupBean.getFieldName(IConfigAlias.USER_WORK_GROUP_WORK_GROUP_ID), workgroupId);
			Iterator iter = userWorkGroupBean.getList(criteria).iterator();
			Expression exp = null;
			while(iter.hasNext()){
				UserWorkGroup userWorkGroup = (UserWorkGroup)iter.next();
				ExpressionUtilities.getOrExpression(exp, ExpressionUtilities.getEqualExpression(dailyTrackingBean.getFieldName(IProjectAlias.DAILY_TRACKING_USER_ID), userWorkGroup.getUser().getId()));
			}
			return exp;
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error adding workgroup Expression", e);
		}
		return null;
	}

	public String onReportByCustomer() throws ReportException, DAOException, ManagerBeanException{
    	IManagerBean dailyTrackingBean = BeanManager.getManagerBean(DailyTracking.class);
    	createCriteria().addOrder(dailyTrackingBean.getFieldName(IProjectAlias.DAILY_TRACKING_CUSTOMER_ID));
    	ReportManager manager = (ReportManager)AonUtil.getRegisteredBean("report");
        manager.setReportKey("dailyTrackingByCustomer");
        manager.setOutputFormat((getOutputFormat()== null?OutputFormat.PDF:getOutputFormat()));
        String outcome = manager.onExecute();
        return outcome;
    }
    
	public String onReportByUser() throws ReportException, DAOException, ManagerBeanException{
    	IManagerBean dailyTrackingBean = BeanManager.getManagerBean(DailyTracking.class);
    	createCriteria().addOrder(dailyTrackingBean.getFieldName(IProjectAlias.DAILY_TRACKING_USER_ID));
    	ReportManager manager = (ReportManager)AonUtil.getRegisteredBean("report");
        manager.setReportKey("dailyTrackingByUser");
        manager.setOutputFormat((getOutputFormat()== null?OutputFormat.PDF:getOutputFormat()));
        String outcome = manager.onExecute();
        return outcome;
    }
}