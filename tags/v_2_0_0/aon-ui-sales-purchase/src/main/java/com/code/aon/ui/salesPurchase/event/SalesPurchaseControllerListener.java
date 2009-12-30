package com.code.aon.ui.salesPurchase.event;

import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.salesPurchase.SalesPurchase;
import com.code.aon.ui.form.PageDataModel;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.salesPurchase.controller.SalesPurchaseController;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.ui.warehouse.controller.IncomeController;
import com.code.aon.ui.warehouse.controller.IncomeDetailController;
import com.code.aon.warehouse.Income;
import com.code.aon.warehouse.IncomeDetail;
import com.code.aon.warehouse.dao.IWarehouseAlias;

/**
 * Listener added to the SalesPurchaseController.
 */
public class SalesPurchaseControllerListener extends ControllerAdapter {
	
	/** The LOGGER. */
	private static final Logger LOGGER = Logger.getLogger(SalesPurchaseController.class.getName());
	
	/** IncomeDetail Controller name. */
	private static final String INCOME_DETAIL_CONTROLLER_NAME = "incomeDetail";
	
	/** IncomeController name. */
	private static final String INCOME_CONTROLLER_NAME = "income";

	/**
	 * After bean removed. Removes all the <code>IncomeDetails</code> and the <code>Income</code> related 
	 * with the current <code>SalesPurchase</code>
	 * 
	 * @param event the event
	 * 
	 * @throws ControllerListenerException the controller listener exception
	 */
	@Override
	@SuppressWarnings("unchecked")
	public void afterBeanRemoved(ControllerEvent event) throws ControllerListenerException {
		SalesPurchase salesPurchase = (SalesPurchase)event.getController().getTo();
		try {
			IManagerBean incomeDetailBean = BeanManager.getManagerBean(IncomeDetail.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(incomeDetailBean.getFieldName(IWarehouseAlias.INCOME_DETAIL_PURCHASE_DETAIL_ID), salesPurchase.getPurchaseDetail().getId());
			Iterator iter = incomeDetailBean.getList(criteria).iterator();
			if(iter.hasNext()){
				IncomeDetail incomeDetail = (IncomeDetail)iter.next();
				if(getIncomeDetailCount(incomeDetail)>1){
					IncomeDetailController incomeDetailController = (IncomeDetailController)AonUtil.getController(INCOME_DETAIL_CONTROLLER_NAME);
					incomeDetailController.onReset(null);
					((IncomeDetail)incomeDetailController.getTo()).setId(incomeDetail.getId());
					((IncomeDetail)incomeDetailController.getTo()).setPurchaseDetail(salesPurchase.getPurchaseDetail());
					((IncomeDetail)incomeDetailController.getTo()).setIncome(incomeDetail.getIncome());
					incomeDetailController.onRemove(null);
				}else{
					IncomeController incomeController = (IncomeController)AonUtil.getController(INCOME_CONTROLLER_NAME);
					incomeController.onReset(null);
					((Income)incomeController.getTo()).setId(incomeDetail.getIncome().getId());
					((Income)incomeController.getTo()).setIncomeStatus(incomeDetail.getIncome().getIncomeStatus());
					IncomeDetailController incomeDetailController = (IncomeDetailController)AonUtil.getController(INCOME_DETAIL_CONTROLLER_NAME);
					criteria = new Criteria();
					criteria.addEqualExpression(incomeDetailBean.getFieldName(IWarehouseAlias.INCOME_DETAIL_INCOME_ID), incomeDetail.getIncome().getId());
					incomeDetailController.setCriteria(criteria);
					incomeDetailController.setModel(new PageDataModel(incomeDetailController,incomeDetailBean.getCount(criteria),20));
					incomeDetailController.setPurchase(incomeDetail.getPurchaseDetail().getPurchase());
					incomeController.onRemove(null);
				}
			}
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error deleting purchaseDetail with id= " + salesPurchase.getPurchaseDetail().getId(), e);
		}
		
	}
	
	/**
	 * Gets the number of incomeDetails related with the Income of the parameter <code>incomeDetail</code>
	 * 
	 * @param incomeDetail the income detail
	 * 
	 * @return the income detail count
	 * 
	 * @throws ManagerBeanException the manager bean exception
	 */
	private int getIncomeDetailCount(IncomeDetail incomeDetail) throws ManagerBeanException {
		IManagerBean incomeDetailBean = BeanManager.getManagerBean(IncomeDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(incomeDetailBean.getFieldName(IWarehouseAlias.INCOME_DETAIL_INCOME_ID), incomeDetail.getIncome().getId());
		return incomeDetailBean.getCount(criteria);
	}
}
