package com.code.aon.ui.commercial.event;

import java.util.LinkedList;
import java.util.List;

import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

import com.code.aon.commercial.Offer;
import com.code.aon.commercial.dao.ICommercialAlias;
import com.code.aon.commercial.enumeration.OfferType;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.commercial.controller.OfferController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

/**
 * Listener Added to the OfferController.
 * 
 * @author Esferalia. David Uriarte - 14-sept-2009
 * @since 1.0
 */
public class InternetOfferControllerListener extends ControllerAdapter {

	/* (non-Javadoc)
	 * @see com.code.aon.ui.form.event.ControllerAdapter#beforeModelInitialized(com.code.aon.ui.form.event.ControllerEvent)
	 */
	@Override
	public void beforeModelInitialized(ControllerEvent event)
			throws ControllerListenerException {
		// TODO Auto-generated method stub
				
		super.beforeModelInitialized(event);	
		IManagerBean beanOffer;
		
		try {
			((OfferController)this.getController()).clearCriteria();
			beanOffer = BeanManager.getManagerBean(Offer.class);
			String type = beanOffer.getFieldName(ICommercialAlias.OFFER_TYPE);
			Criteria criteria = new Criteria();			
			criteria.addEqualExpression(type,OfferType.INTERNET);
			((OfferController)this.getController()).setCriteria(criteria);
			
			
			
		} catch (ManagerBeanException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
				
	}

	@Override
	public void afterBeanCreated(ControllerEvent event)
			throws ControllerListenerException {
		// TODO Auto-generated method stub
		super.afterBeanCreated(event);
		((Offer)((OfferController)this.getController()).getTo()).setType(OfferType.STANDARD);////CAMBIAR POR OFFER TYPE INTENET
	}

}