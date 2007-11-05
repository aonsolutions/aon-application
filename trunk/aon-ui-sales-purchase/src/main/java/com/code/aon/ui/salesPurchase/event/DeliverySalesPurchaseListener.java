package com.code.aon.ui.salesPurchase.event;

import java.util.Iterator;
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
import com.code.aon.warehouse.Delivery;
import com.code.aon.warehouse.DeliveryDetail;
import com.code.aon.warehouse.dao.IWarehouseAlias;

/**
 * Listener added to DeliveryController.
 */
public class DeliverySalesPurchaseListener extends ControllerAdapter{

	/** The LOGGER. */
	private static final Logger LOGGER = Logger.getLogger(DeliverySalesPurchaseListener.class.getName());
	
	/**
	 * Before bean removed. Removes SalesPurchase related with the current Delivery
	 * 
	 * @param event the event
	 * 
	 * @throws ControllerListenerException the controller listener exception
	 */
	@Override
	@SuppressWarnings("unchecked")
	public void beforeBeanRemoved(ControllerEvent event) throws ControllerListenerException {
		Delivery delivery = (Delivery)event.getController().getTo(); 
		try {
			IManagerBean deliveryDetailBean = BeanManager.getManagerBean(DeliveryDetail.class);
			IManagerBean salesPurchaseBean = BeanManager.getManagerBean(SalesPurchase.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(deliveryDetailBean.getFieldName(IWarehouseAlias.DELIVERY_DETAIL_DELIVERY_ID), delivery.getId());
			Iterator deliveryDetailIter = deliveryDetailBean.getList(criteria).iterator();
			while(deliveryDetailIter.hasNext()){
				DeliveryDetail deliveryDetail = (DeliveryDetail)deliveryDetailIter.next();
				criteria = new Criteria();
				criteria.addEqualExpression(salesPurchaseBean.getFieldName(ISalesPurchaseAlias.SALES_PURCHASE_SALES_DETAIL_ID), deliveryDetail.getSalesDetail().getId());
				Iterator salesPurchaseIter = salesPurchaseBean.getList(criteria).iterator();
				if(salesPurchaseIter.hasNext()){
					salesPurchaseBean.remove((SalesPurchase)salesPurchaseIter.next());
				}
			}
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error before removing delivery with id=" + delivery.getId(), e);
		}
	}
}