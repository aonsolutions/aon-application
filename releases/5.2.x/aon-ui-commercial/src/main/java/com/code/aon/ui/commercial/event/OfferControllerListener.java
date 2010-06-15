package com.code.aon.ui.commercial.event;

import com.code.aon.commercial.CommercialTerm;
import com.code.aon.commercial.Offer;
import com.code.aon.commercial.OfferTerm;
import com.code.aon.commercial.dao.ICommercialAlias;
import com.code.aon.commercial.enumeration.OfferStatus;
import com.code.aon.commercial.enumeration.OfferType;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.commercial.controller.OfferController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

/**
 * Listener Added to the OfferController.
 * 
 * @author Consulting & Development. Joseba Urkiri - 6-sept-2006
 * @since 1.0
 */
public class OfferControllerListener extends ControllerAdapter {

	@Override
	public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
		OfferController controller = (OfferController)event.getController();
		((Offer)controller.getTo()).setSecurityLevel(SecurityLevel.OFFICIAL);
		((Offer)controller.getTo()).setStatus(OfferStatus.PENDING);
		((Offer)controller.getTo()).setType(OfferType.NORMAL);
		controller.setAddresses(null);
		controller.setDefaultPayMethod(null);
		controller.resetOfferPayMethod();
	}

	@Override
	public void afterBeanSelected(ControllerEvent event) throws ControllerListenerException {
		OfferController controller = (OfferController)event.getController();
		try {
			controller.loadAddresses(((Offer)controller.getTo()).getTarget().getRegistry().getId());
			controller.loadDefaultPayMethod(((Offer)controller.getTo()).getTarget().getRegistry().getId(), true);
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage());
		}
	}

	@Override
	public void afterBeanAdded(ControllerEvent event) throws ControllerListenerException {
		Offer offer = (Offer)event.getController().getTo();
		try {
			IManagerBean offerTermBean = BeanManager.getManagerBean(OfferTerm.class);
			IManagerBean commercialTermBean = BeanManager.getManagerBean(CommercialTerm.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(commercialTermBean.getFieldName(ICommercialAlias.COMMERCIAL_TERM_GENERAL), true);
			criteria.addOrder(commercialTermBean.getFieldName(ICommercialAlias.COMMERCIAL_TERM_LINE));
			int line = 0;
			for (ITransferObject to : commercialTermBean.getList(criteria)) {
				CommercialTerm commercialTerm = (CommercialTerm)to;

				OfferTerm offerTerm = new OfferTerm();
				offerTerm.setOffer(offer);
				offerTerm.setLine(++line);
				offerTerm.setName(commercialTerm.getName());
				offerTerm.setDescription(commercialTerm.getDescription());
				offerTerm.setGeneral(commercialTerm.isGeneral());
				offerTermBean.insert(offerTerm);
			}
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e);
		}
	}

}