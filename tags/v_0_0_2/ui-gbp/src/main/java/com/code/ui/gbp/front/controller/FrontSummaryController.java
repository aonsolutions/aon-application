package com.code.ui.gbp.front.controller;

import java.util.LinkedList;
import java.util.List;

import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.util.AonUtil;
import com.code.gbp.Offer;
import com.code.gbp.ProFormaInvoice;
import com.code.gbp.dao.IGBPAlias;
import com.code.gbp.enumeration.OfferStatus;
import com.code.gbp.enumeration.ProFormaInvoiceStatus;
import com.code.ui.gbp.front.util.FrontSummaryTo;
import com.code.ui.gbp.front.util.FrontUtil;

public class FrontSummaryController {

	public DataModel getOfferSumaryModel(){
		List<FrontSummaryTo> offerSummaryList = new LinkedList<FrontSummaryTo>();
		DataModel offerSummaryModel = null;
		try {
			FrontSummaryTo summaryTo =null;
			for(OfferStatus status : OfferStatus.values()){
				summaryTo = new FrontSummaryTo();
				summaryTo.setStatus(status);
				summaryTo.setCount(obtainOfferCount(status));
				offerSummaryList.add(summaryTo);
			}
		} catch (ManagerBeanException e) {
			AonUtil.addErrorMessage(e.getMessage());
		}
		offerSummaryModel = new ListDataModel(offerSummaryList);
		return offerSummaryModel;
	}

	public DataModel getProFormaSumaryModel(){
		List<FrontSummaryTo> proFormaSummaryList = new LinkedList<FrontSummaryTo>();
		DataModel proFormaSummaryModel = null;
		try {
			FrontSummaryTo summaryTo =null;
			for(ProFormaInvoiceStatus status : ProFormaInvoiceStatus.values()){
				summaryTo = new FrontSummaryTo();
				summaryTo.setStatus(status);
				summaryTo.setCount(obtainProFormaCount(status));
				proFormaSummaryList.add(summaryTo);
			}
		} catch (ManagerBeanException e) {
			AonUtil.addErrorMessage(e.getMessage());
		}
		proFormaSummaryModel = new ListDataModel(proFormaSummaryList);
		return proFormaSummaryModel;
	}
	
	private int obtainOfferCount(OfferStatus status) throws ManagerBeanException {
		IManagerBean offerBean = BeanManager.getManagerBean(Offer.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(offerBean.getFieldName(IGBPAlias.OFFER_STATUS), status);
		criteria.addEqualExpression(offerBean.getFieldName(IGBPAlias.OFFER_SUPPLIER_ID), FrontUtil.getCurrentSupplier().getId());
		return offerBean.getCount(criteria);
	}
	
	private int obtainProFormaCount(ProFormaInvoiceStatus status) throws ManagerBeanException {
		IManagerBean proFormaBean = BeanManager.getManagerBean(ProFormaInvoice.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(proFormaBean.getFieldName(IGBPAlias.PRO_FORMA_INVOICE_STATUS), status);
		criteria.addEqualExpression(proFormaBean.getFieldName(IGBPAlias.PRO_FORMA_INVOICE_SUPPLIER_ID), FrontUtil.getCurrentSupplier().getId());
		return proFormaBean.getCount(criteria);
	}
}