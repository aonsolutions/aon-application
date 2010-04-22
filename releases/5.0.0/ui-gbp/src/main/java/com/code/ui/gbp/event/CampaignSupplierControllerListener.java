package com.code.ui.gbp.event;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.event.AbortProcessingException;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;
import com.code.gbp.CampaignSupplier;
import com.code.gbp.dao.IGBPAlias;

public class CampaignSupplierControllerListener extends ControllerAdapter {
	
	private static final Logger LOGGER = Logger.getLogger(CampaignSupplierControllerListener.class.getName());

	@Override
	public void beforeModelInitialized(ControllerEvent event) throws ControllerListenerException {
		try {
			event.getController().getCriteria().addOrder(event.getController().getFieldName(IGBPAlias.CAMPAIGN_SUPPLIER_SUPPLIER_NAME));
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e);
		}
	}
	
	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		try {
			CampaignSupplier campaignSupplier = (CampaignSupplier)event.getController().getTo();
			if(existingSupplier(campaignSupplier)){
				LOGGER.log(Level.SEVERE, "Supplier " + campaignSupplier.getSupplier().getName() + " already exists for current campaign", "Supplier " + campaignSupplier.getSupplier().getName() + " already exists for current campaign");
				AonUtil.addErrorMessage("Supplier " + campaignSupplier.getSupplier().getName() + " already exists for current campaign");
				throw new AbortProcessingException("Supplier " + campaignSupplier.getSupplier().getName() + " already exists for current campaign");
			}
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e);
		}
	}

	@Override
	public void beforeBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		try {
			CampaignSupplier campaignSupplier = (CampaignSupplier)event.getController().getTo();
			if(existingSupplier(campaignSupplier)){
				LOGGER.log(Level.SEVERE, "Supplier " + campaignSupplier.getSupplier().getName() + " already exists for current campaign", "Supplier " + campaignSupplier.getSupplier().getName() + " already exists for current campaign");
				AonUtil.addErrorMessage("Supplier " + campaignSupplier.getSupplier().getName() + " already exists for current campaign");
				throw new AbortProcessingException("Supplier " + campaignSupplier.getSupplier().getName() + " already exists for current campaign");
			}
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e);
		}
	}

	private boolean existingSupplier(CampaignSupplier campaignSupplier) throws ManagerBeanException{
		IManagerBean campaignSupplierBean = BeanManager.getManagerBean(CampaignSupplier.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(campaignSupplierBean.getFieldName(IGBPAlias.CAMPAIGN_SUPPLIER_SUPPLIER_ID), campaignSupplier.getSupplier().getId());
		criteria.addEqualExpression(campaignSupplierBean.getFieldName(IGBPAlias.CAMPAIGN_SUPPLIER_CAMPAIGN_ID), campaignSupplier.getCampaign().getId());
		return (campaignSupplierBean.getCount(criteria) > 0);
	} 
}
