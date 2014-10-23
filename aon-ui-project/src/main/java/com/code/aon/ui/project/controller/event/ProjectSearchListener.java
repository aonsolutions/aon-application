package com.code.aon.ui.project.controller.event;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.project.ProjectType;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.registry.Registry;
import com.code.aon.ui.form.event.ControllerSearchListener;
import com.esferalia.aon.entity.IEntityAlias;

public class ProjectSearchListener extends ControllerSearchListener {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private Registry registry;
    private ProjectType projectType;
    private boolean active;
	
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

	public boolean isActive() {
		return active;
	}
	public void setActive(boolean active) {
		this.active = active;
	}

	@Override
	protected void init() throws ManagerBeanException {
		setRegistry((Registry)BeanManager.getManagerBean(Registry.class).createNewTo());
		setProjectType((ProjectType)BeanManager.getManagerBean(ProjectType.class).createNewTo());
		setActive(true);
	}
	
	@Override
	protected void completeCriteria(Criteria criteria) throws ManagerBeanException, ExpressionException {
		if ((getRegistry() != null) && (getRegistry().getId() != null)) {
			criteria.addEqualExpression(getFieldName(IEntityAlias.PROJECT_REGISTRY_ID), getRegistry().getId());
		}
		if ((getProjectType() != null) && (getProjectType().getId() != null)) {
			criteria.addEqualExpression(getFieldName(IEntityAlias.PROJECT_PROJECT_TYPE_ID), getProjectType().getId());
		}
		criteria.addEqualExpression(getFieldName(IEntityAlias.PROJECT_ACTIVE), Boolean.valueOf(active));
	}	

}