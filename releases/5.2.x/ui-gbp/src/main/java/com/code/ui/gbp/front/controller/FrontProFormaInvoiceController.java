package com.code.ui.gbp.front.controller;

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
import com.code.gbp.Campaign;
import com.code.gbp.ProFormaInvoice;
import com.code.gbp.dao.IGBPAlias;
import com.code.gbp.enumeration.ProFormaInvoiceStatus;

public class FrontProFormaInvoiceController extends BasicController {

	public boolean isPending() {
		return ((ProFormaInvoice)this.getTo()).getStatus().equals(ProFormaInvoiceStatus.PENDING);
	}

	public boolean isDiscarded() {
		return ((ProFormaInvoice)this.getTo()).getStatus().equals(ProFormaInvoiceStatus.DISCARDED);
	}

	public boolean isAccepted() {
		return ((ProFormaInvoice)this.getTo()).getStatus().equals(ProFormaInvoiceStatus.ACCEPTED);
	}

	public void onDiscard(ActionEvent event){
		((ProFormaInvoice)this.getTo()).setStatus(ProFormaInvoiceStatus.DISCARDED);
		this.onAccept(event);
	}

	public void onInitialModel(ActionEvent event) throws ManagerBeanException{
		clearCriteria();
		Expression notDiscardedExp = ExpressionUtilities.getNotEqualExpression(getFieldName(IGBPAlias.PRO_FORMA_INVOICE_STATUS), ProFormaInvoiceStatus.DISCARDED);
		getCriteria().addExpression(notDiscardedExp);
		this.onSearch(event);
	}

	@SuppressWarnings("unchecked")
	public void campaignChanged(ValueChangeEvent event) throws ManagerBeanException{
		if(event.getNewValue() != null){
			IManagerBean campaignBean = BeanManager.getManagerBean(Campaign.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(campaignBean.getFieldName(IGBPAlias.CAMPAIGN_ID), event.getNewValue());
			Iterator iter = campaignBean.getList(criteria, 0, 1).iterator();
			if(iter.hasNext()){
				((ProFormaInvoice)getTo()).setCampaign((Campaign)iter.next());
			}
		}
	}
	
	public void addStatusExpression(ValueChangeEvent event) throws ManagerBeanException{
		if(event.getNewValue() != null){
			getCriteria().addEqualExpression(getFieldName(IGBPAlias.PRO_FORMA_INVOICE_STATUS), event.getNewValue());
		}
	}
	
	public void addFromDateExpression(ValueChangeEvent event) throws ManagerBeanException{
		if(event.getNewValue() != null){
			getCriteria().addGreaterThanOrEqualExpression(getFieldName(IGBPAlias.PRO_FORMA_INVOICE_INVOICE_DATE), event.getNewValue());
		}
	}

	public void addToDateExpression(ValueChangeEvent event) throws ManagerBeanException{
		if(event.getNewValue() != null){
			getCriteria().addLessThanOrEqualExpression(getFieldName(IGBPAlias.PRO_FORMA_INVOICE_INVOICE_DATE), event.getNewValue());
		}
	}
}