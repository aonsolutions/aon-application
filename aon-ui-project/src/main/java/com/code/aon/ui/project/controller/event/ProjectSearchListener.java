package com.code.aon.ui.project.controller.event;

import com.code.aon.common.BeanManager;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.project.ProjectType;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.registry.Registry;
import com.code.aon.ui.form.event.ControllerSearchListener;
import com.esferalia.aon.entity.IEntityAlias;

public class ProjectSearchListener extends ControllerSearchListener {
	
	private Registry registry;
    private ProjectType projectType;
	
	public Registry getRegistry() {
		return registry;
	}
	public void setRegistry(Registry registry) {
		this.registry = registry;
	}
	
	public ProjectType getProjectType() {
		return projectType;
	}
	public void setProjectType(ProjectType projectType) {
		this.projectType = projectType;
	}
	
	@Override
	protected void init() throws ManagerBeanException {
		setRegistry((Registry)BeanManager.getManagerBean(Registry.class).createNewTo());
		setProjectType((ProjectType)BeanManager.getManagerBean(ProjectType.class).createNewTo());
	}
	
	@Override
	protected void completeCriteria(Criteria criteria) throws ManagerBeanException, ExpressionException {
		if ((getRegistry() != null) && (getRegistry().getId() != null)) {
			criteria.addEqualExpression(getFieldName(IEntityAlias.PROJECT_REGISTRY_ID), getRegistry().getId());
		}		
		if ((getProjectType() != null) && (getProjectType().getId() != null)) {
			criteria.addEqualExpression(getFieldName(IEntityAlias.PROJECT_PROJECT_TYPE_ID), getProjectType().getId());
		}		
	}	

}