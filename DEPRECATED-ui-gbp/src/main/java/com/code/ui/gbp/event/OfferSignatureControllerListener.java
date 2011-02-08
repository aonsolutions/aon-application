package com.code.ui.gbp.event;

import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.event.AbortProcessingException;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;
import com.code.gbp.Offer;
import com.code.gbp.OfferSignature;
import com.code.gbp.Requirement;
import com.code.gbp.dao.IGBPAlias;
import com.code.gbp.enumeration.DocumentType;
import com.code.gbp.enumeration.OfferStatus;
import com.code.ui.gbp.constants.GBPConstants;

public class OfferSignatureControllerListener extends ControllerAdapter {
	
	private static final Logger LOGGER = Logger.getLogger(OfferSignatureControllerListener.class.getName());
	
	private static final String OFFER_CONTROLLER_NAME = "offer";

	@Override
	public void beforeModelInitialized(ControllerEvent event) throws ControllerListenerException {
		try {
			event.getController().getCriteria().addOrder(event.getController().getFieldName(IGBPAlias.OFFER_SIGNATURE_SIGNATURE_DATE), false);
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e);
		}
	}

	@Override
	public void afterBeanAdded(ControllerEvent event) throws ControllerListenerException {
		BasicController offerController = (BasicController)AonUtil.getController(OFFER_CONTROLLER_NAME);
		Offer offer = (Offer)offerController.getTo();
		Requirement requirement = obtainRequirement(offer);
		if(requirement != null){
			if(enoughSignatures(requirement, offer)){
				offer.setStatus(OfferStatus.ACCEPTED);
				offerController.accept(null);
			}
		}else{
			AonUtil.addErrorMessage("Unable to obtain the requermient to apply");
		}
	}

	@SuppressWarnings("unchecked")
	private Requirement obtainRequirement(Offer offer) {
		try {
			IManagerBean requirementBean = BeanManager.getManagerBean(Requirement.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(requirementBean.getFieldName(IGBPAlias.REQUIREMENT_DOCUMENT_TYPE), DocumentType.OFFER);
			criteria.addLessThanOrEqualExpression(requirementBean.getFieldName(IGBPAlias.REQUIREMENT_APPLICATION_DATE), offer.getOfferDate());
			criteria.addLessThanOrEqualExpression(requirementBean.getFieldName(IGBPAlias.REQUIREMENT_AMOUNT), offer.getPrice());
			criteria.addOrder(requirementBean.getFieldName(IGBPAlias.REQUIREMENT_AMOUNT), false);
			criteria.addOrder(requirementBean.getFieldName(IGBPAlias.REQUIREMENT_APPLICATION_DATE), false);
			Iterator iter = requirementBean.getList(criteria, 0, 1).iterator();
			if(iter.hasNext()){
				return (Requirement)iter.next();
			}
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
			AonUtil.addErrorMessage("Unable to obtain the requermient to apply");
			throw new AbortProcessingException(e);
		}
		return null;
	}
	
	private boolean enoughSignatures(Requirement requirement, Offer offer){
		try {
			IManagerBean offerSignatureBean = BeanManager.getManagerBean(OfferSignature.class);
			Criteria criteria = new Criteria();
			Expression offerExp = ExpressionUtilities.getEqualExpression(offerSignatureBean.getFieldName(IGBPAlias.OFFER_SIGNATURE_OFFER_ID), offer.getId());
			Expression managerExp = ExpressionUtilities.getEqualExpression(offerSignatureBean.getFieldName(IGBPAlias.OFFER_SIGNATURE_ROLE), GBPConstants.GBP_MANAGER_ROLE_NAME); 
			Expression supervisorExp = ExpressionUtilities.getEqualExpression(offerSignatureBean.getFieldName(IGBPAlias.OFFER_SIGNATURE_ROLE), GBPConstants.GBP_SUPERVISOR_ROLE_NAME); 
			criteria.addExpression(ExpressionUtilities.getAndExpression(offerExp, managerExp));
			if(offerSignatureBean.getCount(criteria) >= requirement.getManagerNumber()){
				criteria = new Criteria();
				criteria.addExpression(ExpressionUtilities.getAndExpression(offerExp, supervisorExp));
				if(offerSignatureBean.getCount(criteria) >= requirement.getSupervisorNumber()){
					return true;
				}
			}
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
			AonUtil.addErrorMessage("Error validating signature requeriments");
			throw new AbortProcessingException(e);
		}
		return false;
	}
}