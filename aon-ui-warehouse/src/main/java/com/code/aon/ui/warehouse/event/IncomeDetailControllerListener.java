package com.code.aon.ui.warehouse.event;

import static com.code.aon.ui.common.ICommonMessages.ITEM_SERIALIZABLE_WILDCARD_ERROR;

import javax.faces.event.AbortProcessingException;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.Projection;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;
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
			incomeDetail.setWarehouse(((IncomeController)controller.getMasterController()).getWarehouse());
			incomeDetail.getIncome().setWorkPlace(income.getWorkPlace());
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage(), e);
		}
	}

	@Override
	public void afterBeanSelected(ControllerEvent event) throws ControllerListenerException {
		IncomeDetailController controller = (IncomeDetailController)event.getController();
		IncomeDetail incomeDetail = (IncomeDetail)controller.getTo();

		controller.setLongDescription((incomeDetail.getDescription().length() > 64) ? true : false);
	}

	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		IncomeDetailController controller = (IncomeDetailController)event.getController();
		IncomeDetail incomeDetail = (IncomeDetail)controller.getTo();
		incomeDetail.setWarehouse(((IncomeController)controller.getMasterController()).getWarehouse());
		try {
			checkSerializableWildCard(incomeDetail);
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage(), e);
		}
	}

	@Override
	public void beforeBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		IncomeDetailController controller = (IncomeDetailController)event.getController();
		IncomeDetail incomeDetail = (IncomeDetail)controller.getTo();
		incomeDetail.setWarehouse(((IncomeController)controller.getMasterController()).getWarehouse());
		try {
			checkSerializableWildCard(incomeDetail);
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage(), e);
		}
	}

	@Override
	public void afterBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		event.getController().initializeModel();
	}

	private	Integer calculateNextLine(Income income) throws ManagerBeanException {
		IManagerBean incomeDetailBean = BeanManager.getManagerBean(IncomeDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(incomeDetailBean.getFieldName(IEntityAlias.INCOME_DETAIL_INCOME_ID), income.getId());
		Projection projection = Projection.max(incomeDetailBean.getFieldName(IEntityAlias.INCOME_DETAIL_LINE));
		Object value = incomeDetailBean.getUniqueResult(projection, criteria);
		return (value != null) ? ((Integer)value) + 1 : 1;
	}

	private void checkSerializableWildCard(IncomeDetail incomeDetail) throws ManagerBeanException {
		if (incomeDetail.getItem() != null && incomeDetail.getItem().getProduct().isSerializable() && incomeDetail.getItem().isWildCard()) {
			throw new AbortProcessingException(AonUtil.getMessage(ITEM_SERIALIZABLE_WILDCARD_ERROR));
		}
	}

}