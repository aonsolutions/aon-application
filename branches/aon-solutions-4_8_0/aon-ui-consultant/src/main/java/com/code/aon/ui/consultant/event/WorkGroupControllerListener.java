package com.code.aon.ui.consultant.event;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.code.aon.campaign.ProcessDetail;
import com.code.aon.campaign.dao.ICampaignAlias;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.WorkGroup;
import com.code.aon.config.enumeration.WorkGroupStatus;
import com.code.aon.project.Activity;
import com.code.aon.project.Task;
import com.code.aon.project.dao.IProjectAlias;
import com.code.aon.project.enumeration.TaskStatus;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.form.listener.LinesControllerListener;

public class WorkGroupControllerListener extends LinesControllerListener {
	
	private static final Logger LOGGER = Logger.getLogger(WorkGroupControllerListener.class.getName());

	@Override
	public void beforeBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		WorkGroup workGroup = (WorkGroup)event.getController().getTo();
		if(workGroup.getStatus().equals(WorkGroupStatus.INACTIVE)){
			checkTasks(workGroup);
			checkActivities(workGroup);
			checkProcessDetails(workGroup);
		}
		super.beforeBeanUpdated(event);
	}
	
	private void checkTasks(WorkGroup workGroup) throws ControllerListenerException {
		try {
			IManagerBean taskBean = BeanManager.getManagerBean(Task.class);
			Criteria criteria = new Criteria();
			Expression groupExpr = ExpressionUtilities.getEqualExpression(taskBean.getFieldName(IProjectAlias.TASK_WORK_GROUP_ID), workGroup.getId());
			Expression pendingExpr = ExpressionUtilities.getEqualExpression(taskBean.getFieldName(IProjectAlias.TASK_STATUS), TaskStatus.PENDING);
			Expression inProgressExpr = ExpressionUtilities.getEqualExpression(taskBean.getFieldName(IProjectAlias.TASK_STATUS), TaskStatus.IN_PROGRESS);
			Expression orExp = ExpressionUtilities.getOrExpression(pendingExpr, inProgressExpr);
			criteria.addExpression(ExpressionUtilities.getAndExpression(groupExpr, orExp));
			int tasks = taskBean.getCount(criteria);
			if(tasks > 0){
				workGroup.setStatus(WorkGroupStatus.ACTIVE);
				throw new ControllerListenerException("Unable to set as INACTIVE workGroup. " + tasks + " tasks related." );
			}
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error checking userWorkGroups workGroup id= " + workGroup.getId(), e);
		}
	}
	
	private void checkActivities(WorkGroup workGroup) throws ControllerListenerException {
		try {
			IManagerBean activityBean = BeanManager.getManagerBean(Activity.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(activityBean.getFieldName(IProjectAlias.ACTIVITY_WORKGROUP_ID), workGroup.getId());
			int activities = activityBean.getCount(criteria);
			if(activities > 0){
				workGroup.setStatus(WorkGroupStatus.ACTIVE);
				throw new ControllerListenerException("Unable to set as INACTIVE activities. " + activities + " activities related." );
			}
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error checking activities workGroup id= " + workGroup.getId(), e);
		}
	}
	
	private void checkProcessDetails(WorkGroup workGroup) throws ControllerListenerException {
		try {
			IManagerBean processDetailBean = BeanManager.getManagerBean(ProcessDetail.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(processDetailBean.getFieldName(ICampaignAlias.PROCESS_DETAIL_WORKGROUP_ID), workGroup.getId());
			int processDetails = processDetailBean.getCount(criteria);
			if(processDetails > 0){
				workGroup.setStatus(WorkGroupStatus.ACTIVE);
				throw new ControllerListenerException("Unable to set as INACTIVE processDetail. " + processDetails + " processDetails related." );
			}
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error checking processDetails workGroup id= " + workGroup.getId(), e);
		}
	}
}