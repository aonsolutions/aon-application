package com.code.aon.ui.composition.event;

import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.composition.Composition;
import com.code.aon.composition.CompositionDetail;
import com.code.aon.composition.dao.ICompositionAlias;
import com.code.aon.product.Item;
import com.code.aon.product.util.DiscountExpression;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.warehouse.controller.DeliveryDetailController;
import com.code.aon.warehouse.DeliveryDetail;

public class CompositionDeliveryDetailListener extends ControllerAdapter {
	
    /** The LOGGER. */
	private static final Logger LOGGER = Logger.getLogger(CompositionDeliveryDetailListener.class.getName());
	
    /**
     * Before bean added. Sets composition price equals zero in delivery, if composition price isPriceInDetails.
     * 
     * @param event
     * @throws ControllerListenerException
     */
	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		DeliveryDetailController deliveryDetailController = (DeliveryDetailController)event.getController();
		if(!deliveryDetailController.isImportingOffer()){
			Composition composition = obtainComposition(((DeliveryDetail)event.getController().getTo()).getItem());
			if(composition != null && composition.isPriceInDetails()){
				((DeliveryDetail)event.getController().getTo()).setPrice(0.0);
			}
		}
	}
	
    /**
     * After bean added. Inserts composition details in delivery.
     * 
     * @param event
     * @throws ControllerListenerException
     */
	@Override
	public void afterBeanAdded(ControllerEvent event) throws ControllerListenerException {
		DeliveryDetailController deliveryDetailController = (DeliveryDetailController)event.getController();
		if(!deliveryDetailController.isImportingOffer()){
			DeliveryDetail deliveryDetail = duplicateDeliveryDetail((DeliveryDetail)deliveryDetailController.getTo());
			try {
				Composition composition = obtainComposition(deliveryDetail.getItem());
				if(composition != null){
					IManagerBean compositionDetailBean = BeanManager.getManagerBean(CompositionDetail.class);
					Criteria criteria = new Criteria();
					criteria.addEqualExpression(compositionDetailBean.getFieldName(ICompositionAlias.COMPOSITION_DETAIL_COMPOSITION_ID), composition.getId());
					Iterator iter = compositionDetailBean.getList(criteria).iterator();
					while(iter.hasNext()){
						CompositionDetail compositionDetail = (CompositionDetail)iter.next();
						((DeliveryDetail)event.getController().getTo()).setItem(compositionDetail.getItem());
                        ((DeliveryDetail)event.getController().getTo()).setDescription(compositionDetail.getDescription());
						((DeliveryDetail)event.getController().getTo()).setDiscountExpression(new DiscountExpression("0.0"));
						((DeliveryDetail)event.getController().getTo()).setQuantity(deliveryDetail.getQuantity() * compositionDetail.getQuantity());
						if(composition.isPriceInDetails()){
							((DeliveryDetail)event.getController().getTo()).setPrice(deliveryDetail.getQuantity() * compositionDetail.getPrice());
						}else{
							((DeliveryDetail)event.getController().getTo()).setPrice(0.0);
						}
						event.getController().onAccept(null);
                        event.getController().onReset(null);
					}
				}
			} catch (ManagerBeanException e) {
				LOGGER.log(Level.SEVERE, "Error before bean added for deliveryDetail with id= " + deliveryDetail.getId(), e);
			}
		}
	}
	
	/**
     * Obtains a composition using the given item.
     * 
	 * @param item
	 * @return Composition
	 */
	private Composition obtainComposition(Item item) {
		try {
			IManagerBean compositionBean = BeanManager.getManagerBean(Composition.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(compositionBean.getFieldName(ICompositionAlias.COMPOSITION_ITEM), item.getId());
			Iterator iter = compositionBean.getList(criteria).iterator();
			if(iter.hasNext()){
				return (Composition)iter.next();
			}
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error obtaining composition for item with id=" + item.getId(), e);
		}
		return null;
	}

	/** Duplicates delivery detail.
	 * @param detail
	 * @return DeliveryDetail
	 */
	private DeliveryDetail duplicateDeliveryDetail(DeliveryDetail detail) {
		DeliveryDetail deliveryDetail = new DeliveryDetail();
		deliveryDetail.setId(detail.getId());
		deliveryDetail.setDiscountExpression(detail.getDiscountExpression());
		deliveryDetail.setItem(detail.getItem());
		deliveryDetail.setDelivery(detail.getDelivery());
		deliveryDetail.setPrice(detail.getPrice());
		deliveryDetail.setQuantity(detail.getQuantity());
		return deliveryDetail;
	}
}
