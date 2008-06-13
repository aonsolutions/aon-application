package com.code.ui.gbp.front.controller;

import java.util.Date;
import java.util.Iterator;

import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.util.AonUtil;
import com.code.gbp.Campaign;
import com.code.gbp.Offer;
import com.code.gbp.ProFormaInvoice;
import com.code.gbp.dao.IGBPAlias;
import com.code.gbp.enumeration.OfferStatus;
import com.code.gbp.enumeration.ProFormaInvoiceStatus;

public class FrontOfferController extends BasicController {
	
	private static final String FRONT_PRO_FORMA_CONTROLLER_NAME = "frontProForma";

	public boolean isPending() {
		return ((Offer)this.getTo()).getStatus().equals(OfferStatus.PENDING);
	}

	public boolean isDiscarded() {
		return ((Offer)this.getTo()).getStatus().equals(OfferStatus.DISCARDED);
	}
	
	public boolean isAccepted(){
		return ((Offer)this.getTo()).getStatus().equals(OfferStatus.ACCEPTED);
	}

	public void onDiscard(ActionEvent event){
		((Offer)this.getTo()).setStatus(OfferStatus.DISCARDED);
		this.onAccept(event);
	}

	public void onInitialModel(ActionEvent event) throws ManagerBeanException{
		clearCriteria();
		Expression notDiscardedExp = ExpressionUtilities.getNotEqualExpression(getFieldName(IGBPAlias.OFFER_STATUS), OfferStatus.DISCARDED);
		getCriteria().addExpression(notDiscardedExp);
		Expression notTransferedExp = ExpressionUtilities.getNotEqualExpression(getFieldName(IGBPAlias.OFFER_STATUS), OfferStatus.TRANSFERED);
		getCriteria().addExpression(notTransferedExp);
		this.onSearch(event);
	}

	public void onTransfer(ActionEvent event){
		Offer offer = (Offer)this.getTo();
		FrontProFormaInvoiceController proFormaController = (FrontProFormaInvoiceController)AonUtil.getController(FRONT_PRO_FORMA_CONTROLLER_NAME);
		proFormaController.onReset(event);
		ProFormaInvoice invoice = (ProFormaInvoice)proFormaController.getTo();
		invoice.setAccountContact(offer.getAccountContact());
		invoice.setAmount(offer.getPrice());
		invoice.setCampaign(offer.getCampaign());
		invoice.setCostType(offer.getCostType());
		invoice.setInvoiceDate(new Date());
		invoice.setNumber("");
		invoice.setOffice(offer.getOffice());
		invoice.setPaymentTerm(offer.getPaymentTerm());
		invoice.setStatus(ProFormaInvoiceStatus.PENDING);
		invoice.setSupplier(offer.getSupplier());
		invoice.setOffer(offer);
		proFormaController.accept(event);
		proFormaController.setModel(null);
		offer.setStatus(OfferStatus.TRANSFERED);
		this.onAccept(event);
	}
	
	@SuppressWarnings("unchecked")
	public void campaignChanged(ValueChangeEvent event) throws ManagerBeanException{
		if(event.getNewValue() != null){
			IManagerBean campaignBean = BeanManager.getManagerBean(Campaign.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(campaignBean.getFieldName(IGBPAlias.CAMPAIGN_ID), event.getNewValue());
			Iterator iter = campaignBean.getList(criteria, 0, 1).iterator();
			if(iter.hasNext()){
				((Offer)getTo()).setCampaign((Campaign)iter.next());
			}
		}
	}
	
	public void addStatusExpression(ValueChangeEvent event) throws ManagerBeanException{
		if(event.getNewValue() != null){
			getCriteria().addEqualExpression(getFieldName(IGBPAlias.OFFER_STATUS), event.getNewValue());
		}
	}
	
	public void addFromDateExpression(ValueChangeEvent event) throws ManagerBeanException{
		if(event.getNewValue() != null){
			getCriteria().addGreaterThanOrEqualExpression(getFieldName(IGBPAlias.OFFER_OFFER_DATE), event.getNewValue());
		}
	}

	public void addToDateExpression(ValueChangeEvent event) throws ManagerBeanException{
		if(event.getNewValue() != null){
			getCriteria().addLessThanOrEqualExpression(getFieldName(IGBPAlias.OFFER_OFFER_DATE), event.getNewValue());
		}
	}
}