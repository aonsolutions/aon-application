package com.code.aon.ui.project.controller;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.faces.model.SelectItemGroup;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.groupware.enumeration.Priority;
import com.code.aon.project.ActivityType;
import com.code.aon.project.DossierType;
import com.code.aon.project.JobType;
import com.code.aon.project.dao.IProjectAlias;
import com.code.aon.project.enumeration.DossierStatus;
import com.code.aon.project.enumeration.TaskPeriod;
import com.code.aon.project.enumeration.TaskStatus;
import com.code.aon.ql.Criteria;

public class ProjectCollectionsController {

	@SuppressWarnings("unchecked")
	public List<SelectItem> getDossierTypes() throws ManagerBeanException {
		List<SelectItem> dossierTypeList = new LinkedList<SelectItem>();
		IManagerBean dossierTypeBean = BeanManager.getManagerBean(DossierType.class);
		Criteria criteria = new Criteria();
		criteria.addOrder(dossierTypeBean.getFieldName(IProjectAlias.DOSSIER_TYPE_DESCRIPTION));
		Iterator iter = dossierTypeBean.getList(criteria).iterator();
		while(iter.hasNext()){
			DossierType type = (DossierType)iter.next();
			SelectItem item = new SelectItem(type.getId(), type.getDescription());
			dossierTypeList.add(item);
		}
		return dossierTypeList;
	}

    public List<SelectItem> getDossierStatus() {
		Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
		LinkedList<SelectItem> dossierStatusList = new LinkedList<SelectItem>();
		DossierStatus[] dossierStatuses = DossierStatus.values();
		for (int i = 0; i < dossierStatuses.length; i++) {
			DossierStatus status = dossierStatuses[i];
			String name = status.getName(locale);
			SelectItem item = new SelectItem(status, name);
            dossierStatusList.add(item);
		}
		return dossierStatusList;
	}

    @SuppressWarnings("unchecked")
    public List<SelectItem> getActivityTypes() throws ManagerBeanException {
        List<SelectItem> activityTypeList = new LinkedList<SelectItem>();
        IManagerBean dossierTypeBean = BeanManager.getManagerBean(DossierType.class);
        Criteria criteria = new Criteria();
        criteria.addOrder(dossierTypeBean.getFieldName(IProjectAlias.DOSSIER_TYPE_DESCRIPTION));
        Iterator iter = dossierTypeBean.getList(criteria).iterator();
        while(iter.hasNext()){
            DossierType dossierType = (DossierType)iter.next();
            SelectItemGroup activityType = new SelectItemGroup(dossierType.getDescription());
            activityType.setDisabled(true);
            activityType.setSelectItems(obtainActivityTypes(dossierType));
            activityTypeList.add(activityType);
        }
        return activityTypeList;
    }

    @SuppressWarnings("unchecked")
    private SelectItem[] obtainActivityTypes(DossierType dossierType) throws ManagerBeanException {
        List<SelectItem> activityTypes = new LinkedList<SelectItem>();
        IManagerBean activityTypeBean = BeanManager.getManagerBean(ActivityType.class);
        Criteria criteria = new Criteria();
        criteria.addEqualExpression(activityTypeBean.getFieldName(IProjectAlias.ACTIVITY_TYPE_DOSSIER_TYPE_ID), dossierType.getId());
        criteria.addOrder(activityTypeBean.getFieldName(IProjectAlias.ACTIVITY_TYPE_DESCRIPTION));
        Iterator iter = activityTypeBean.getList(criteria).iterator();
        while(iter.hasNext()){
            ActivityType activityType = (ActivityType)iter.next();
            SelectItem item = new SelectItem(activityType.getId(), activityType.getDescription());
            activityTypes.add(item);
        }
        return activityTypes.toArray(new SelectItem[activityTypes.size()]);
    }

    @SuppressWarnings("unchecked")
	public List<SelectItem> getJobTypes() throws ManagerBeanException {
		List<SelectItem> jobTypeList = new LinkedList<SelectItem>();
		IManagerBean jobTypeBean = BeanManager.getManagerBean(JobType.class);
		Criteria criteria = new Criteria();
		criteria.addOrder(jobTypeBean.getFieldName(IProjectAlias.JOB_TYPE_DESCRIPTION));
		Iterator iter = jobTypeBean.getList(criteria).iterator();
		while(iter.hasNext()){
			JobType type = (JobType)iter.next();
			SelectItem item = new SelectItem(type.getId(), type.getDescription());
			jobTypeList.add(item);
		}
		return jobTypeList;
	}

    public List<SelectItem> getTaskPriorities() {
        Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
        LinkedList<SelectItem> taskPriorityList = new LinkedList<SelectItem>();
        Priority[] taskPriorities = Priority.values();
        for (int i = 0; i < taskPriorities.length; i++) {
            Priority taskPriority = taskPriorities[i];
            String name = taskPriority.getName(locale);
            SelectItem item = new SelectItem(taskPriority, name);
            taskPriorityList.add(item);
        }
        return taskPriorityList;
    }
    
    public List<SelectItem> getTaskStatus() {
        Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
        LinkedList<SelectItem> taskStatusList = new LinkedList<SelectItem>();
        TaskStatus[] taskStatuses = TaskStatus.values();
        for (int i = 0; i < taskStatuses.length; i++) {
            TaskStatus taskStatus = taskStatuses[i];
            String name = taskStatus.getName(locale);
            SelectItem item = new SelectItem(taskStatus, name);
            taskStatusList.add(item);
        }
        return taskStatusList;
    }
    
    public List<SelectItem> getTaskPeriods() {
        Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
        LinkedList<SelectItem> taskPeriodList = new LinkedList<SelectItem>();
        TaskPeriod[] taskPeriods = TaskPeriod.values();
        for (int i = 0; i < taskPeriods.length; i++) {
            TaskPeriod taskPeriod = taskPeriods[i];
            String name = taskPeriod.getName(locale);
            SelectItem item = new SelectItem(taskPeriod, name);
            taskPeriodList.add(item);
        }
        return taskPeriodList;
    }
}