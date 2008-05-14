package com.code.aon.ui.salesPurchase.event;

import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.salesPurchase.SalesPurchase;
import com.code.aon.salesPurchase.dao.ISalesPurchaseAlias;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.warehouse.Income;
import com.code.aon.warehouse.IncomeDetail;
import com.code.aon.warehouse.dao.IWarehouseAlias;

/**
 * Listener added to the IncomeController.
 */
public class IncomeSalesPurchaseListener extends ControllerAdapter {

	/** The LOGGER. */
	private static final Logger LOGGER = Logger.getLogger(IncomeSalesPurchaseListener.class.getName()); 

	/**
	 * Before bean removed. Throws an Exception if any of IncomeDetails is linked with a deliveryDetail
	 * 
	 * @param event the event
	 * 
	 * @throws ControllerListenerException the controller listener exception
	 */
	@Override
	@SuppressWarnings("unchecked")
	public void beforeBeanRemoved(ControllerEvent event) throws ControllerListenerException {
		Income income = (Income)event.getController().getTo();
		try {
			IManagerBean incomeDetailBean = BeanManager.getManagerBean(IncomeDetail.class);
			IManagerBean salesPurchaseBean = BeanManager.getManagerBean(SalesPurchase.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(incomeDetailBean.getFieldName(IWarehouseAlias.INCOME_DETAIL_INCOME_ID), income.getId());
			Iterator iter = incomeDetailBean.getList(criteria).iterator();
			criteria = new Criteria();
			if(iter.hasNext()){
				while(iter.hasNext()){
					IncomeDetail incomeDetail = (IncomeDetail)iter.next();
					criteria.addOrExpression(salesPurchaseBean.getFieldName(ISalesPurchaseAlias.SALES_PURCHASE_PURCHASE_DETAIL_ID), incomeDetail.getPurchaseDetail().getId().toString());
				}
				if(salesPurchaseBean.getCount(criteria)>0){
					throw new ControllerListenerException("IncomeDetail linked with a Delivery");
				}
			}
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error before remove Income with id=" + income.getId(), e);
		} catch (ExpressionException e) {
			LOGGER.log(Level.SEVERE, "Error before remove Income with id=" + income.getId(), e);
		}
	}
}
