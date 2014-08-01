package com.code.aon.ui.tas.controller;

import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import com.code.aon.commercial.Target;
import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.tas.ProjectTas;
import com.code.aon.tas.TasItem;
import com.code.aon.tas.enumeration.ProjectStatus;
import com.code.aon.ui.common.components.LookupChangeEvent;
import com.code.aon.ui.config.controller.ConfigCollectionsController;
import com.code.aon.ui.config.controller.ConfigConstants;
import com.code.aon.ui.config.controller.HeaderObjectController;
import com.code.aon.ui.stat.controller.ProjectStatEngineController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class ProjectTasController extends HeaderObjectController implements ITasConstants {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	public void targetData(LookupChangeEvent event) throws ManagerBeanException {
		ProjectTas projectTas = ((ProjectTas)getTo());
		if (event.getNewValue() != null && !event.getNewValue().equals("")) {
			Target target = (Target)event.getNewValue();
			projectTas.setTarget(target);

			if (projectTas.getTasItem() == null || projectTas.getTasItem().getId() == null) {
				ProjectTas previousProjectTas = obtainPreviousProjectTas(IEntityAlias.PROJECT_TAS_TARGET_ID, target.getId());
				if (previousProjectTas != null) {
					projectTas.setTasItem(previousProjectTas.getTasItem());
				}
			}
		}
	}

	public void tasItemData(LookupChangeEvent event) throws ManagerBeanException {
		ProjectTas projectTas = ((ProjectTas)getTo());
		if (event.getNewValue() != null && !event.getNewValue().equals("")) {
			TasItem tasItem = (TasItem)event.getNewValue();
			projectTas.setTasItem(tasItem);

			if (projectTas.getTarget() == null || projectTas.getTarget().getId() == null) {
				ProjectTas previousProjectTas = obtainPreviousProjectTas(IEntityAlias.PROJECT_TAS_TAS_ITEM_ID, tasItem.getId());
				if (previousProjectTas != null) {
					projectTas.setTarget(previousProjectTas.getTarget());
				}
			}
		}
	}

	private ProjectTas obtainPreviousProjectTas(String alias, Integer value) throws ManagerBeanException {
		IManagerBean projectTasBean = BeanManager.getManagerBean(ProjectTas.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(projectTasBean.getFieldName(alias), value);
		criteria.addOrder(projectTasBean.getFieldName(IEntityAlias.PROJECT_TAS_PROJECT_DATE), false);
		criteria.addOrder(projectTasBean.getFieldName(IEntityAlias.PROJECT_TAS_SERIES), false);
		criteria.addOrder(projectTasBean.getFieldName(IEntityAlias.PROJECT_TAS_NUMBER), false);
		for (ITransferObject ito : projectTasBean.getList(criteria)) {
			return (ProjectTas)ito;
		}
		return null;
	}

	public boolean isFinished() {
		ProjectTas projectTas = ((ProjectTas)getTo());
		return (projectTas != null && projectTas.getStatus() == ProjectStatus.CLOSED);
	}

	public void onProjectHistory(ActionEvent event) {
		ProjectStatEngineController statController =(ProjectStatEngineController)AonUtil.getRegisteredBean(PROJECT_STAT_CONTROLLER_NAME);
		statController.setProject(((ProjectTas)this.getTo()).getProject());
		statController.setBackAction(PROJECT_TAS_FORM_NAME);
		statController.initializeProjectData();
	}

	@Override
	public List<SelectItem> getSeriesCodes() throws ManagerBeanException {
		ConfigCollectionsController ccc = (ConfigCollectionsController) AonUtil.getRegisteredBean(ConfigConstants.CONFIG_COLLECTIONS);
		return ccc.getTasSeriesIds();
	}
	
}
