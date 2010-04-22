package com.code.aon.ui.warehouse.event;

import java.util.Iterator;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.warehouse.controller.IncomeController;
import com.code.aon.warehouse.Income;
import com.code.aon.warehouse.IncomeDetail;
import com.code.aon.warehouse.Warehouse;
import com.code.aon.warehouse.dao.IWarehouseAlias;
import com.code.aon.warehouse.enumeration.IncomeStatus;

public class IncomeControllerListener extends ControllerAdapter {
	
	@Override
	public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
		IncomeController controller = (IncomeController)event.getController();
		((Income)controller.getTo()).setSecurityLevel(SecurityLevel.OFFICIAL);
		((Income)controller.getTo()).setStatus(IncomeStatus.PENDING);
		controller.setAddresses(null);
		controller.setWarehouse(null);
		controller.setDefaultPayMethod(null);
		controller.resetIncomePayMethod();
	}

	@Override
	public void afterBeanSelected(ControllerEvent event) throws ControllerListenerException {
		IncomeController controller = (IncomeController)event.getController();
		try {
			controller.loadAddresses(((Income)controller.getTo()).getSupplier().getRegistry().getId());
	        controller.setWarehouse(obtainWarehouseId((Income)controller.getTo()));
			controller.loadDefaultPayMethod(((Income)controller.getTo()).getSupplier().getRegistry().getId(), true);
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage());
		}
	}
	
	@SuppressWarnings("unchecked")
	private Warehouse obtainWarehouseId(Income income) throws ControllerListenerException {
		try {
			IManagerBean incomeDetailBean = BeanManager.getManagerBean(IncomeDetail.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(incomeDetailBean.getFieldName(IWarehouseAlias.INCOME_DETAIL_INCOME_ID), income.getId());
			Iterator iterator = incomeDetailBean.getList(criteria).iterator();
			if (iterator.hasNext()) {
				return ((IncomeDetail)iterator.next()).getWarehouse();
			} else {
				IManagerBean warehouseBean = BeanManager.getManagerBean(Warehouse.class);
				criteria = new Criteria();
				criteria.addOrder(warehouseBean.getFieldName(IWarehouseAlias.WAREHOUSE_NAME));
				Iterator<?> iter = warehouseBean.getList(criteria).iterator();
				if (iter.hasNext()) {
					return (Warehouse)iter.next();
				}
			}
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage());
		}
		return null;
	}

}