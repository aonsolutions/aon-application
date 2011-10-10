package com.code.aon.ui.project.controller.event;

import com.code.aon.common.BeanManager;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.project.ProjectType;
import com.code.aon.project.dao.IProjectAlias;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.registry.Registry;
import com.code.aon.ui.form.event.ControllerSearchListener;

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
		super.init();
		setRegistry((Registry)BeanManager.getManagerBean(Registry.class).createNewTo());
		setProjectType((ProjectType)BeanManager.getManagerBean(ProjectType.class).createNewTo());
	}
	
	@Override
	protected void completeCriteria(Criteria criteria) throws ManagerBeanException, ExpressionException {
		super.completeCriteria( criteria );
		if ((getRegistry() != null) && (getRegistry().getId() != null)) {
			criteria.addEqualExpression(getFieldName(IProjectAlias.PROJECT_REGISTRY_ID), getRegistry().getId());
		}		
		if ((getProjectType() != null) && (getProjectType().getId() != null)) {
			criteria.addEqualExpression(getFieldName(IProjectAlias.PROJECT_PROJECT_TYPE_ID), getProjectType().getId());
		}		
	}	

}