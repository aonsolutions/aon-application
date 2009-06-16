package com.code.aon.ui.project.controller;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.UserWorkGroup;
import com.code.aon.config.dao.IConfigAlias;
import com.code.aon.customer.Customer;
import com.code.aon.project.Activity;
import com.code.aon.project.Dossier;
import com.code.aon.project.PeriodicalTask;
import com.code.aon.project.dao.IProjectAlias;
import com.code.aon.project.enumeration.DossierStatus;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.BasicController;

public class PeriodicalTaskController extends BasicController {
	
	private static final Logger LOGGER = Logger.getLogger(PeriodicalTaskController.class.getName());
	
    private List<SelectItem> dossiers = new LinkedList<SelectItem>();
    private List<SelectItem> activities = new LinkedList<SelectItem>();
    private List<SelectItem> users = new LinkedList<SelectItem>();

    
    private Date nextDateFrom;
    private Date nextDateTo;

    public List<SelectItem> getDossiers() {
        return dossiers;
    }

    public void setDossiers(List<SelectItem> dossiers) {
        this.dossiers = dossiers;
    }

    public List<SelectItem> getActivities() {
        return activities;
    }
    
    @Override
    public void onEditSearch(ActionEvent event) {
    	super.onEditSearch(event);
    	setNextDateFrom(null);
    	setNextDateTo(null);
    }

    public void setActivities(List<SelectItem> activities) {
        this.activities = activities;
    }
	
	public void customerChange(ValueChangeEvent event) {
        if (event.getNewValue() != null && !"".equals(event.getNewValue())) {
            loadDossiers(new Integer(event.getNewValue().toString()));
        } else {
            dossiers = new LinkedList<SelectItem>();
        }
    }
	
    public void customerPojoChange(ValueChangeEvent event) {
        if ( event.getNewValue() != null ) {
        	Customer customer = (Customer) event.getNewValue();
            loadDossiers( customer.getId() );
        } else {
            dossiers = new LinkedList<SelectItem>();
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
    public void loadDossiers(Integer customerId) {
        dossiers = new LinkedList<SelectItem>();
        activities = new LinkedList<SelectItem>();        
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
    
	public void addNextDateFromExpression(ValueChangeEvent event){
        if(event.getNewValue() != null && getNextDateFrom() != null) {
            try {
                getCriteria().addGreaterThanOrEqualExpression(getFieldName(IProjectAlias.PERIODICAL_TASK_NEXT_DATE), event.getNewValue());
            } catch (ManagerBeanException e) {
                LOGGER.log(Level.SEVERE, "Error adding FROM next date expression", e);
            }
        }
    }
    
    public void addNextDateToExpression(ValueChangeEvent event){
        if(event.getNewValue() != null && getNextDateTo() != null) {
            try {
                getCriteria().addLessThanOrEqualExpression(getFieldName(IProjectAlias.PERIODICAL_TASK_NEXT_DATE), event.getNewValue());
            } catch (ManagerBeanException e) {
                LOGGER.log(Level.SEVERE, "Error adding TO next date expression", e);
            }
        }
    }
    
    public Date addPeriodToDate(PeriodicalTask periodTask, Date date) {
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		calendar.add(periodTask.getPeriod().getField(), periodTask.getQuantity() * periodTask.getPeriod().getValue());
		return calendar.getTime();
	}

	public Date getNextDateFrom() {
		return nextDateFrom;
	}

	public void setNextDateFrom(Date nextDateFrom) {
		this.nextDateFrom = nextDateFrom;
	}

	public Date getNextDateTo() {
		return nextDateTo;
	}

	public void setNextDateTo(Date nextDateTo) {
		this.nextDateTo = nextDateTo;
	}
    public List<SelectItem> getUsers() {
		return users;
	}

	public void setUsers(List<SelectItem> users) {
		this.users = users;
	}
	
    public void workgroupChanged(ValueChangeEvent event) {
    	if(event.getNewValue() != null && !event.getNewValue().equals("")){
    		loadUsers(new Integer(event.getNewValue().toString()));
    	}else{
    		users = new LinkedList<SelectItem>();
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
}