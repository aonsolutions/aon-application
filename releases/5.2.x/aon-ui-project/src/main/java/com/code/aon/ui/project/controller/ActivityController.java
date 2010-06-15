package com.code.aon.ui.project.controller;

import java.util.Iterator;

import javax.faces.event.ValueChangeEvent;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.WorkGroup;
import com.code.aon.config.dao.IConfigAlias;
import com.code.aon.project.Activity;
import com.code.aon.project.ActivityType;
import com.code.aon.project.dao.IProjectAlias;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.LinesController;

public class ActivityController extends LinesController {
	
	@SuppressWarnings("unchecked")
	public void onActivityTypeChanged(ValueChangeEvent event) throws ManagerBeanException{
		if(event.getNewValue() != null){
			IManagerBean activityTypeBean = BeanManager.getManagerBean(ActivityType.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(activityTypeBean.getFieldName(IProjectAlias.ACTIVITY_TYPE_ID), event.getNewValue());
			Iterator iter = activityTypeBean.getList(criteria).iterator();
			if(iter.hasNext()){
				ActivityType activityType = (ActivityType)iter.next();
				((Activity)this.getTo()).setActivityType(activityType);
			}
		}
	}

	@SuppressWarnings("unchecked")
	public void onWorkGroupChanged(ValueChangeEvent event) throws ManagerBeanException{
		if(event.getNewValue() != null){
			IManagerBean workGroupBean = BeanManager.getManagerBean(WorkGroup.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(workGroupBean.getFieldName(IConfigAlias.WORK_GROUP_ID), event.getNewValue());
			Iterator iter = workGroupBean.getList(criteria).iterator();
			if(iter.hasNext()){
				WorkGroup workGroup = (WorkGroup)iter.next();
				((Activity)this.getTo()).setWorkgroup(workGroup);
			}
		}
	}
}
