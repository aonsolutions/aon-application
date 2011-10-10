package com.code.aon.ui.tas.event;

import org.apache.commons.lang.ArrayUtils;

import com.code.aon.commercial.Target;
import com.code.aon.common.BeanManager;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.groupware.TaskHolder;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.tas.TasItem;
import com.code.aon.tas.dao.ITASAlias;
import com.code.aon.tas.enumeration.ProjectStatus;
import com.code.aon.ui.form.event.ControllerSearchListener;

public class ProjectTasSearchListener extends ControllerSearchListener {

	private Target target;
	private TasItem tasItem;
	private TaskHolder taskHolder;
	private ProjectStatus[] projectStatuses;
		
	public Target getTarget() {
		return target;
	}

	public void setTarget(Target target) {
		this.target = target;
	}

	public TasItem getTasItem() {
		return tasItem;
	}

	public void setTasItem(TasItem tasItem) {
		this.tasItem = tasItem;
	}

	public TaskHolder getTaskHolder() {
		return taskHolder;
	}

	public void setTaskHolder(TaskHolder taskHolder) {
		this.taskHolder = taskHolder;
	}

	public ProjectStatus[] getProjectStatuses() {
		return projectStatuses;
	}

	public void setProjectStatuses(ProjectStatus[] projectStatuses) {
		this.projectStatuses = projectStatuses;
	}
	
	@Override
	protected void init() throws ManagerBeanException {
		setTarget((Target)BeanManager.getManagerBean(Target.class).createNewTo());
		setTasItem((TasItem)BeanManager.getManagerBean(TasItem.class).createNewTo());
		setTaskHolder((TaskHolder)BeanManager.getManagerBean(TaskHolder.class).createNewTo());
		ProjectStatus[] defaultProjectStatus = {ProjectStatus.PENDING};
		setProjectStatuses(defaultProjectStatus);
	}
	
	@Override
	protected void completeCriteria(Criteria criteria) throws ManagerBeanException, ExpressionException {
		if ((getTarget() != null) && (getTarget().getId() != null)) {
			criteria.addEqualExpression(getFieldName(ITASAlias.PROJECT_TAS_TARGET_ID), getTarget().getId());			
		}
		if ((getTasItem() != null) && (getTasItem().getId() != null)) {
			criteria.addEqualExpression(getFieldName(ITASAlias.PROJECT_TAS_TAS_ITEM_ID), getTasItem().getId());			
		}
		if ((getTaskHolder() != null) && (getTaskHolder().getId() != null)) {
			criteria.addEqualExpression(getFieldName(ITASAlias.PROJECT_TAS_TASK_HOLDER_ID), getTaskHolder().getId());			
		}
		if (!ArrayUtils.isEmpty(getProjectStatuses())) {
			addEnumToCriteria(criteria, getController().resolveAlias(ITASAlias.PROJECT_TAS_STATUS), getProjectStatuses());
		}
	}

}