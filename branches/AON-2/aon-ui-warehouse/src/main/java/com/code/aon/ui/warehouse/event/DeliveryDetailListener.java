package com.code.aon.ui.warehouse.event;

import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.sales.Sales;
import com.code.aon.sales.SalesDetail;
import com.code.aon.sales.dao.ISalesAlias;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.ui.warehouse.controller.DeliveryDetailController;
import com.code.aon.warehouse.Delivery;
import com.code.aon.warehouse.DeliveryDetail;
import com.code.aon.warehouse.dao.IWarehouseAlias;

/**
 * DeliveryDetail listener 
 * 
 * @author Consulting & Development.
 * @since 1.0
 *
 */
public class DeliveryDetailListener extends ControllerAdapter {
	
	/**
	 * The class logger
	 */
	private final Logger LOGGER = Logger.getLogger(DeliveryDetailListener.class.getName());
	
	/**
	 * The name of the DeliveryDetailController
	 */
	private static final String DELIVERY_DETAIL_CONTROLLER_NAME = "deliveryDetail";
	
	/**
	 * Searches the Sales instead of the DeliveryDetail to assign it to the controller  
	 * 
	 * @see com.code.aon.ui.form.event.ControllerAdapter#afterBeanSelected(com.code.aon.ui.form.event.ControllerEvent)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public void afterBeanSelected(ControllerEvent event) throws ControllerListenerException {
		Delivery delivery = (Delivery)event.getController().getTo();
		Criteria criteria = new Criteria();
		try {
			IManagerBean deliveryDetailBean = BeanManager.getManagerBean(DeliveryDetail.class);
			criteria.addEqualExpression(deliveryDetailBean.getFieldName(IWarehouseAlias.DELIVERY_DETAIL_DELIVERY_ID),delivery.getId());
			Iterator iter = deliveryDetailBean.getList(criteria).iterator();
			Sales sales = null;
			/*if(iter.hasNext()){
				sales = ((DeliveryDetail)iter.next()).getSalesDetail().getSales();
			}*/
			DeliveryDetailController deliveryDetailController = (DeliveryDetailController)AonUtil.getController(DELIVERY_DETAIL_CONTROLLER_NAME);
			deliveryDetailController.setSales(sales);
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE,"Error obtaining sales for delivery with id: " + delivery.getId(),e);
		}
	}
	
	/**
	 * Removes all SalesDetail and Sales linked to this DeliveryDetail
	 * 
	 * @see com.code.aon.ui.form.event.ControllerAdapter#afterBeanRemoved(com.code.aon.ui.form.event.ControllerEvent)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public void afterBeanRemoved(ControllerEvent event) throws ControllerListenerException {
		DeliveryDetailController deliveryDetailController = (DeliveryDetailController)AonUtil.getController(DELIVERY_DETAIL_CONTROLLER_NAME);
		Sales sales = deliveryDetailController.getSales();
		if(sales != null){
			try {
				Criteria criteria = new Criteria();
				IManagerBean salesDetailBean = BeanManager.getManagerBean(SalesDetail.class);
				criteria.addEqualExpression(salesDetailBean.getFieldName(ISalesAlias.SALES_DETAIL_SALES_ID),sales.getId());
				Iterator iter = salesDetailBean.getList(criteria).iterator();
				while(iter.hasNext()){
					salesDetailBean.remove((SalesDetail)iter.next());
				}
				IManagerBean salesBean = BeanManager.getManagerBean(Sales.class);
				salesBean.remove(sales);
				deliveryDetailController.setSales(null);
			} catch (ManagerBeanException e) {
				LOGGER.log(Level.SEVERE,"Error deleting sales for delivery with id: " + ((Delivery)event.getController().getTo()).getId(),e);
			}
		}
	}
}
