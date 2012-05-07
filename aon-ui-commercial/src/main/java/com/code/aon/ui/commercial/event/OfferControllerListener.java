package com.code.aon.ui.commercial.event;

import com.code.aon.commercial.CommercialTerm;
import com.code.aon.commercial.Offer;
import com.code.aon.commercial.OfferTerm;
import com.code.aon.commercial.enumeration.OfferStatus;
import com.code.aon.commercial.enumeration.OfferType;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.commercial.controller.ICommercialConstants;
import com.code.aon.ui.commercial.controller.OfferController;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.esferalia.aon.entity.IEntityAlias;

public class OfferControllerListener extends ControllerAdapter implements ICommercialConstants {

	@Override
	public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
		OfferController controller = (OfferController)event.getController();
		((Offer)controller.getTo()).setSecurityLevel(SecurityLevel.OFFICIAL);
		((Offer)controller.getTo()).setStatus(OfferStatus.PENDING);
		((Offer)controller.getTo()).setType(OfferType.NORMAL);
		controller.setAddresses(null);
		controller.setProjects(null);
		controller.setDefaultPayMethod(null);
		controller.resetOfferPayMethod();
	}

	@Override
	public void afterBeanSelected(ControllerEvent event) throws ControllerListenerException {
		OfferController controller = (OfferController)event.getController();
		try {
			controller.loadAddresses(((Offer)controller.getTo()).getTarget().getRegistry().getId());
			controller.loadProjects(((Offer)controller.getTo()).getTarget().getRegistry().getId());
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
			criteria.addEqualExpression(commercialTermBean.getFieldName(IEntityAlias.COMMERCIAL_TERM_GENERAL), true);
			criteria.addOrder(commercialTermBean.getFieldName(IEntityAlias.COMMERCIAL_TERM_LINE));
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

			IController offerDetailController = FormUtil.getController(OFFER_DETAIL_CONTROLLER_NAME);
			offerDetailController.onReset(null);
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e);
		}
	}

}