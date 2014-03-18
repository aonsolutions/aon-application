package com.code.aon.ui.warehouse.event;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.Projection;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.warehouse.controller.IncomeController;
import com.code.aon.ui.warehouse.controller.IncomeDetailController;
import com.code.aon.warehouse.Income;
import com.code.aon.warehouse.IncomeDetail;
import com.esferalia.aon.entity.IEntityAlias;

public class IncomeDetailControllerListener extends ControllerAdapter {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	@Override
	public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
		IncomeDetailController controller = (IncomeDetailController)event.getController();
		IncomeDetail incomeDetail = (IncomeDetail)controller.getTo();
		Income income = (Income)controller.getMasterController().getTo();

		controller.setLongDescription(false);
		try {
			incomeDetail.setProject((income.getProject() != null && income.getProject().getId() != null) ? income.getProject() : null);
			incomeDetail.setLine(calculateNextLine((Income)controller.getMasterController().getTo()));
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage(), e);
		}
	}

	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		IncomeDetailController incomeDetailController = (IncomeDetailController)event.getController();
		IncomeDetail incomeDetail = (IncomeDetail)incomeDetailController.getTo();
		incomeDetail.setWarehouse(((IncomeController)incomeDetailController.getMasterController()).getWarehouse());
	}

	@Override
	public void beforeBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		IncomeDetailController incomeDetailController = (IncomeDetailController)event.getController();
		IncomeDetail incomeDetail = (IncomeDetail)incomeDetailController.getTo();
		incomeDetail.setWarehouse(((IncomeController)incomeDetailController.getMasterController()).getWarehouse());
	}

	@Override
	public void afterBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		event.getController().initializeModel();
	}

	@Override
	public void afterBeanSelected(ControllerEvent event) throws ControllerListenerException {
		IncomeDetailController controller = (IncomeDetailController)event.getController();
		IncomeDetail incomeDetail = (IncomeDetail)controller.getTo();

		controller.setLongDescription((incomeDetail.getDescription().length() > 64) ? true : false);
	}

	private	Integer calculateNextLine(Income income) throws ManagerBeanException {
		IManagerBean incomeDetailBean = BeanManager.getManagerBean(IncomeDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(incomeDetailBean.getFieldName(IEntityAlias.INCOME_DETAIL_INCOME_ID), income.getId());
		Projection projection = Projection.max(incomeDetailBean.getFieldName(IEntityAlias.INCOME_DETAIL_LINE));
		Object value = incomeDetailBean.getUniqueResult(projection, criteria);
		return (value != null) ? ((Integer)value) + 1 : 1;
	}

}