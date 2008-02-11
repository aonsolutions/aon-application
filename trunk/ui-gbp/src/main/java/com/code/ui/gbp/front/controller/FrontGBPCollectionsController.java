package com.code.ui.gbp.front.controller;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.faces.model.SelectItem;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.gbp.CampaignSupplier;
import com.code.gbp.Supplier;
import com.code.gbp.dao.IGBPAlias;
import com.code.gbp.enumeration.CampaignStatus;
import com.code.ui.gbp.front.util.FrontUtil;

public class FrontGBPCollectionsController {

	@SuppressWarnings("unchecked")
	public List<SelectItem> getPendingCampaigns() throws ManagerBeanException{
		List<SelectItem> pendingCampaigns = new LinkedList<SelectItem>();
		IManagerBean campaignSupplierBean = BeanManager.getManagerBean(CampaignSupplier.class);
		Supplier currentSupplier = FrontUtil.getCurrentSupplier();
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(campaignSupplierBean.getFieldName(IGBPAlias.CAMPAIGN_SUPPLIER_CAMPAIGN_STATUS), CampaignStatus.ACTIVE);
		criteria.addEqualExpression(campaignSupplierBean.getFieldName(IGBPAlias.CAMPAIGN_SUPPLIER_SUPPLIER_ID), currentSupplier.getId());
		Iterator iter = campaignSupplierBean.getList(criteria).iterator();
		while(iter.hasNext()){
			CampaignSupplier campaignSupplier = (CampaignSupplier)iter.next();
			SelectItem item = new SelectItem(campaignSupplier.getCampaign().getId(), campaignSupplier.getCampaign().getCode().toString());
			pendingCampaigns.add(item);
		}
		return pendingCampaigns;
	}
}