package com.code.aon.ui.salesPurchase.event;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.salesPurchase.SalesPurchase;
import com.code.aon.salesPurchase.dao.ISalesPurchaseAlias;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.warehouse.IncomeDetail;

/**
 * Listener added to the IncomeDetailController.
 */
public class IncomeDetailSalesPurchaseListener extends ControllerAdapter {
	
	/** The LOGGER. */
	private static final Logger LOGGER = Logger.getLogger(IncomeDetailSalesPurchaseListener.class.getName()); 

	/**
	 * Before bean removed. Throws the exception if the current IncomeDetail is linked with a deliveryDetail
	 * 
	 * @param event the event
	 * 
	 * @throws ControllerListenerException the controller listener exception
	 */
	@Override
	public void beforeBeanRemoved(ControllerEvent event) throws ControllerListenerException {
		IncomeDetail incomeDetail = (IncomeDetail)event.getController().getTo();
		try {
			IManagerBean salesPurchaseBean = BeanManager.getManagerBean(SalesPurchase.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(salesPurchaseBean.getFieldName(ISalesPurchaseAlias.SALES_PURCHASE_PURCHASE_DETAIL_ID), incomeDetail.getPurchaseDetail().getId());
			if(salesPurchaseBean.getCount(criteria)>0){
				throw new ControllerListenerException("incomeDetail linked with a deliveryDetail");
			}
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error before remove incomeDetail with id=" + incomeDetail.getId(), e);
		}
	}
}
