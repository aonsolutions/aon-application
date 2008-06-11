package com.code.ui.gbp.front.controller;

import java.util.Iterator;

import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
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

	public void onDiscard(ActionEvent event){
		((ProFormaInvoice)this.getTo()).setStatus(ProFormaInvoiceStatus.DISCARDED);
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
				((ProFormaInvoice)getTo()).setCampaign((Campaign)iter.next());
			}
		}
	}
}