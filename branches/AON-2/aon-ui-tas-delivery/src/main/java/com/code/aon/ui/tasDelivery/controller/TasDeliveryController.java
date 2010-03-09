package com.code.aon.ui.tasDelivery.controller;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.tasDelivery.TasDelivery;
import com.code.aon.tasDelivery.dao.ITasDeliveryAlias;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.ui.warehouse.controller.DeliveryController;
import com.code.aon.warehouse.Delivery;
import com.code.aon.warehouse.dao.IWarehouseAlias;

/**
 * Controller used to manage <code>TasDelivery</code> class.
 */
public class TasDeliveryController extends BasicController {

	/** The LOGGER. */
	private static final Logger LOGGER = Logger.getLogger(TasDeliveryController.class.getName());

	/** DeliveryController name. */
	private static final String DELIVERY_CONTROLLER_NAME = "delivery";

	/* (non-Javadoc)
	 * @see com.code.aon.ui.form.BasicController#getCollection()
	 */
	@Override
	public Collection getCollection() {
		DeliveryController deliveryController = (DeliveryController)AonUtil.getController(DELIVERY_CONTROLLER_NAME);
		Delivery delivery = (Delivery) deliveryController.getTo();
		try {
			IManagerBean tasDeliveryBean = BeanManager.getManagerBean(TasDelivery.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(tasDeliveryBean.getFieldName(ITasDeliveryAlias.TAS_DELIVERY_DELIVERY_ID),
					delivery.getId());
			Iterator iter = tasDeliveryBean.getList(criteria).iterator();
			if (iter.hasNext()) {
				List<TasDelivery> l = new LinkedList<TasDelivery>();
				l.add((TasDelivery) iter.next());
				return l;
			}
			TasDelivery tasDelivery = new TasDelivery();
			tasDelivery.setDelivery(obtainDelivery(delivery.getId()));
			List<TasDelivery> l = new LinkedList<TasDelivery>();
			l.add(tasDelivery);
			return l;
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error obtaining collection for delivery with id=" + delivery.getId(), e);
		}
		return super.getCollection();
	}

	/**
	 * Obtains the delivery with id equals to parameter <code>deliveryId</code>.
	 * 
	 * @param deliveryId the delivery id
	 * 
	 * @return the delivery
	 */
	private Delivery obtainDelivery(Integer deliveryId) {
		try {
			IManagerBean deliveryBean = BeanManager.getManagerBean(Delivery.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(deliveryBean.getFieldName(IWarehouseAlias.DELIVERY_ID), deliveryId);
			Iterator iter = deliveryBean.getList(criteria).iterator();
			if (iter.hasNext()) {
				return (Delivery) iter.next();
			}
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error obtaining delivery with id=" + deliveryId, e);
		}
		return null;
	}
}