package com.code.aon.ui.composition.event;

import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.code.aon.commercial.OfferDetail;
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

public class CompositionOfferDetailListener extends ControllerAdapter {
	
    /** The LOGGER. */
	private static final Logger LOGGER = Logger.getLogger(CompositionOfferDetailListener.class.getName());
	
    /**
     * Before bean added. Sets composition price equals zero in offer, if composition price isPriceInDetails.
     * 
     * @param event
     * @throws ControllerListenerException
     */
	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		Composition composition = obtainComposition(((OfferDetail)event.getController().getTo()).getItem());
		if(composition != null && composition.isPriceInDetails()){
			((OfferDetail)event.getController().getTo()).setPrice(0.0);
		}
	}
	
    /**
     * After bean added. Inserts composition details in offer.
     * 
     * @param event
     * @throws ControllerListenerException
     */
	@Override
	public void afterBeanAdded(ControllerEvent event) throws ControllerListenerException {
		OfferDetail offerDetail = duplicateOfferDetail((OfferDetail)event.getController().getTo());
		try {
			Composition composition = obtainComposition(offerDetail.getItem());
			if(composition != null){
				IManagerBean compositionDetailBean = BeanManager.getManagerBean(CompositionDetail.class);
				Criteria criteria = new Criteria();
				criteria.addEqualExpression(compositionDetailBean.getFieldName(ICompositionAlias.COMPOSITION_DETAIL_COMPOSITION_ID), composition.getId());
				Iterator iter = compositionDetailBean.getList(criteria).iterator();
				while(iter.hasNext()){
					CompositionDetail compositionDetail = (CompositionDetail)iter.next();
					((OfferDetail)event.getController().getTo()).setItem(compositionDetail.getItem());
					((OfferDetail)event.getController().getTo()).setDescription(compositionDetail.getDescription());
					((OfferDetail)event.getController().getTo()).setDiscountExpression(new DiscountExpression("0.0"));
					((OfferDetail)event.getController().getTo()).setQuantity(offerDetail.getQuantity() * compositionDetail.getQuantity());
					if(composition.isPriceInDetails()){
						((OfferDetail)event.getController().getTo()).setPrice(offerDetail.getQuantity() * compositionDetail.getPrice());
					}else{
						((OfferDetail)event.getController().getTo()).setPrice(0.0);
					}
                    event.getController().onAccept(null);
                    event.getController().onReset(null);
				}
			}
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error before bean added for offerDetail with id= " + offerDetail.getId(), e);
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
	private OfferDetail duplicateOfferDetail(OfferDetail detail) {
		OfferDetail offerDetail = new OfferDetail();
		offerDetail.setId(detail.getId());
		offerDetail.setDiscountExpression(detail.getDiscountExpression());
		offerDetail.setItem(detail.getItem());
		offerDetail.setOffer(detail.getOffer());
		offerDetail.setPrice(detail.getPrice());
		offerDetail.setQuantity(detail.getQuantity());
		offerDetail.setStatus(detail.getStatus());
		return offerDetail;
	}
}
