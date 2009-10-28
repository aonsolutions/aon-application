package com.code.aon.ui.campaign.controller;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.event.ActionEvent;

import com.code.aon.campaign.ActivityProcess;
import com.code.aon.campaign.dao.ICampaignAlias;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.FormUtil;

public class ActivityProcessController extends BasicController {

    private static final Logger LOGGER = Logger.getLogger(ActivityProcessController.class.getName());
    
    private CampaignDossierExtended cde;
    
    public CampaignDossierExtended getCampaignDossierExtended() {
		return cde;
	}

	public void setCampaignDossierExtended(CampaignDossierExtended cde) {
		this.cde = cde;
	}

	public void onSearchTasks(ActionEvent event) {
        try {
	    	CampaignDossierController cdc = (CampaignDossierController) FormUtil.getController("campaignDossier");
	        CampaignDossierExtended cde = (CampaignDossierExtended) cdc.getExtendedModel().getRowData();
	        setCampaignDossierExtended(cde);
	        
	        Integer campaignId = cde.getCampaignDossier().getCampaign().getId();
	        Integer dossierId = cde.getCampaignDossier().getDossier().getId();
	        
            IManagerBean activityProcessBean = BeanManager.getManagerBean(ActivityProcess.class);
            Criteria criteria = new Criteria();
            criteria.addEqualExpression(activityProcessBean.getFieldName(ICampaignAlias.ACTIVITY_PROCESS_CAMPAIGN_ID), campaignId);
            criteria.addEqualExpression(activityProcessBean.getFieldName(ICampaignAlias.ACTIVITY_PROCESS_TASK_DOSSIER_ID), dossierId);
            criteria.addOrder(activityProcessBean.getFieldName(ICampaignAlias.ACTIVITY_PROCESS_TASK_ID));
            setCriteria(criteria);
            onSearch(null);
        } catch (ManagerBeanException e) {
            LOGGER.log(Level.SEVERE, "Error obtaining campaign tasks", e);
        }
    }

}