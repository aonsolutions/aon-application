package com.code.aon.ui.project.controller;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import javax.faces.model.SelectItem;

import org.apache.commons.lang.ObjectUtils;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.project.ActivityType;
import com.code.aon.project.Project;
import com.code.aon.project.ProjectType;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.esferalia.aon.entity.IEntityAlias;

public class ProjectCollectionsController implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	public List<SelectItem> getProjects(Integer registryId) throws ManagerBeanException {
		List<SelectItem> projects = new LinkedList<SelectItem>();
		if (registryId != null) {
			IManagerBean bean = BeanManager.getManagerBean(Project.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.PROJECT_REGISTRY_ID), registryId);
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.PROJECT_ACTIVE), true);
			criteria.addOrder(bean.getFieldName(IEntityAlias.PROJECT_NAME));
			List<ITransferObject> list = bean.getList(criteria);
			for (ITransferObject to:list) {
				Project project = (Project) to;
				SelectItem item = new SelectItem(project, project.getName());
				projects.add(item);
			}
		}
		return projects;
	}

	public int getProjectsCount(Integer registryId) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(Project.class);
		Criteria criteria = new Criteria();
		if (registryId != null) {
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.PROJECT_REGISTRY_ID), registryId);
		}
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.PROJECT_ACTIVE), true);
		return bean.getCount(criteria);
	}

	public boolean isActiveProjects() throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(Project.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.PROJECT_ACTIVE), true);
		return bean.getList(criteria, 0, 1) != null;
	}

	public List<SelectItem> getActivityTypes(Integer projectTypeId) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(ActivityType.class);
		Criteria criteria = new Criteria();
		Expression expr1 = ExpressionUtilities.getNullExpression(bean.getFieldName(IEntityAlias.ACTIVITY_TYPE_PROJECT_TYPE_ID)); 
		if (projectTypeId != null) {
			Expression expr2 = ExpressionUtilities.getEqualExpression(bean.getFieldName(IEntityAlias.ACTIVITY_TYPE_PROJECT_TYPE_ID), projectTypeId);
			criteria.addOrExpression(ExpressionUtilities.getOrExpression(expr1, expr2));
		} else {
			criteria.addExpression(expr1);
		}
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.ACTIVITY_TYPE_ACTIVE), true);
		criteria.addOrder(bean.getFieldName(IEntityAlias.ACTIVITY_TYPE_PROJECT_TYPE_ID));
		criteria.addOrder(bean.getFieldName(IEntityAlias.ACTIVITY_TYPE_DESCRIPTION));
		List<ITransferObject> list = bean.getList(criteria);
		Integer oldId = null;
		boolean first = true;
		List<SelectItem> activityTypes = new LinkedList<SelectItem>();
		for (ITransferObject to:list) {
			ActivityType activityType = (ActivityType) to;
			Integer id = activityType.getProjectType() == null?null: activityType.getProjectType().getId();
			if (!ObjectUtils.equals(oldId,id)) {
				if (!first) {
					activityTypes.add(new SelectItem(null," -----"," -----",true));	
				}
				oldId = id;
			}
			SelectItem item = new SelectItem(activityType, activityType.getDescription());
			activityTypes.add(item);
			first = false;
		}
		return activityTypes;
	}

	public List<SelectItem> getActivityTypes() throws ManagerBeanException {
		List<SelectItem> activityTypes = new LinkedList<SelectItem>();
		IManagerBean bean = BeanManager.getManagerBean(ActivityType.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.ACTIVITY_TYPE_ACTIVE), true);
		criteria.addOrder(bean.getFieldName(IEntityAlias.ACTIVITY_TYPE_DESCRIPTION));
		List<ITransferObject> list = bean.getList(criteria);
		for (ITransferObject to:list) {
			ActivityType activityType = (ActivityType) to;
			String description = activityType.getDescription() + " (" + activityType.getId() + ")";
			if (activityType.getProjectType() != null) {
				description = activityType.getDescription() + " (" + activityType.getProjectType().getDescription() + ")";
			}
			SelectItem item = new SelectItem(activityType, description);
			activityTypes.add(item);
		}
		return activityTypes;
	}

	public List<SelectItem> getProjectTypes( ) throws ManagerBeanException {
		List<SelectItem> projectTypes = new LinkedList<SelectItem>();
		IManagerBean bean = BeanManager.getManagerBean(ProjectType.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.PROJECT_TYPE_ACTIVE),true);
		criteria.addOrder(bean.getFieldName(IEntityAlias.PROJECT_TYPE_DESCRIPTION));
		List<ITransferObject> list = bean.getList(criteria);
		for (ITransferObject to:list) {
			ProjectType type = (ProjectType) to;
			SelectItem item = new SelectItem(type, type.getDescription());
			projectTypes.add(item);
		}
		return projectTypes;
	}
	
}