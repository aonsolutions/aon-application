package com.code.aon.ui.tasCommercial.event;

import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.code.aon.commercial.Offer;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.tas.SupportOrder;
import com.code.aon.tas.dao.ITASAlias;
import com.code.aon.tasCommercial.TasOffer;
import com.code.aon.tasCommercial.dao.ITasCommercialAlias;
import com.code.aon.ui.commercial.controller.OfferController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

/**
 * Listener Added to the OfferController.
 */
public class TasOfferListener extends ControllerAdapter {
	
	/** The LOGGER. */
	private static final Logger LOGGER = Logger.getLogger(TasOfferListener.class.getName());

	/**
	 * After bean added. If the current offer has a support order related, adds a <code>TasOffer</code>
	 * 
	 * @param event the event
	 */
	@Override
	@SuppressWarnings("unchecked")
	public void afterBeanAdded(ControllerEvent event) {
		OfferController offerController = (OfferController)event.getController();
		try {
			IManagerBean supportOrderBean = BeanManager.getManagerBean(SupportOrder.class);
			IManagerBean tasOfferBean = BeanManager.getManagerBean(TasOffer.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(supportOrderBean.getFieldName(ITASAlias.SUPPORT_ORDER_ID), offerController.getSupportOrderId());
			Iterator iter = supportOrderBean.getList(criteria).iterator();
			if(iter.hasNext()){
				SupportOrder supportOrder = (SupportOrder)iter.next();
				TasOffer tasOffer = new TasOffer();
				tasOffer.setSupportOrder(supportOrder);
				tasOffer.setOffer((Offer)offerController.getTo());
				tasOfferBean.insert(tasOffer);
			}
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error inserting TasOffer for offer with id= " + ((Offer)offerController.getTo()).getId(), e);
		}
	}

	/**
	 * After bean updated. Updates the related <code>TasOffer</code> if necessary
	 * 
	 * @param event the event
	 * 
	 * @throws ControllerListenerException the controller listener exception
	 */
	@Override
	@SuppressWarnings("unchecked")
	public void afterBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		OfferController offerController = (OfferController)event.getController();
		Offer offer = (Offer)offerController.getTo();
		try {
			IManagerBean tasOffBean = BeanManager.getManagerBean(TasOffer.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(tasOffBean.getFieldName(ITasCommercialAlias.TAS_OFFER_OFFER_ID), offer.getId());
			Iterator iter = tasOffBean.getList(criteria).iterator();
			if(iter.hasNext()){
				TasOffer tasOffer = (TasOffer)iter.next();
				IManagerBean supportOrderBean = BeanManager.getManagerBean(SupportOrder.class);
				criteria = new Criteria();
				criteria.addEqualExpression(supportOrderBean.getFieldName(ITASAlias.SUPPORT_ORDER_ID), offerController.getSupportOrderId());
				iter = supportOrderBean.getList(criteria).iterator();
				if(iter.hasNext()){
					SupportOrder supportOrder = (SupportOrder)iter.next();
					tasOffer.setSupportOrder(supportOrder);
					tasOffBean.update(tasOffer);
				}
			}
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error updating TasOffer for offer with id= " + offer.getId(), e);
		}
	}

	/**
	 * Before bean removed. Removes the related <code>TasOffer</code> if necessary
	 * 
	 * @param event the event
	 * 
	 * @throws ControllerListenerException the controller listener exception
	 */
	@Override
	@SuppressWarnings("unchecked")
	public void beforeBeanRemoved(ControllerEvent event) throws ControllerListenerException {
		Offer offer = (Offer)((OfferController)event.getController()).getTo();
		try {
			IManagerBean tasOfferBean = BeanManager.getManagerBean(TasOffer.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(tasOfferBean.getFieldName(ITasCommercialAlias.TAS_OFFER_OFFER_ID), offer.getId());
			Iterator iter = tasOfferBean.getList(criteria).iterator();
			if(iter.hasNext()){
				tasOfferBean.remove((TasOffer)iter.next());
			}
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error removing TasOffer for offer with id= " + offer.getId(), e);
		}
	}

	/**
	 * After bean selected. Gets the the SupportOrder id from the related <code>TasOffer</code>
	 * 
	 * @param event the event
	 * 
	 * @throws ControllerListenerException the controller listener exception
	 */
	@Override
	@SuppressWarnings("unchecked")
	public void afterBeanSelected(ControllerEvent event) throws ControllerListenerException {
		OfferController offerController = (OfferController)event.getController();
		Offer offer = (Offer)((OfferController)event.getController()).getTo();
		try {
			IManagerBean tasOfferBean = BeanManager.getManagerBean(TasOffer.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(tasOfferBean.getFieldName(ITasCommercialAlias.TAS_OFFER_OFFER_ID), offer.getId());
			Iterator iter = tasOfferBean.getList(criteria).iterator();
			if(iter.hasNext()){
				TasOffer tasOffer = (TasOffer)iter.next();
				offerController.setSupportOrderId(tasOffer.getSupportOrder().getId());
			}
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error after select offer with id= " + offer.getId(), e);
		}
	}
}