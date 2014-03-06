package com.code.aon.ui.groupware.controller;

import javax.faces.event.ActionEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.AonVersion;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.FormUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class ProcessTaskController extends BasicController {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private final static Logger LOGGER = LoggerFactory.getLogger(ProcessTaskController.class);
	
    private CampaignProjectExtended cde;
    
    public CampaignProjectExtended getCampaignProjectExtended() {
		return cde;
	}

	public void setCampaignProjectExtended(CampaignProjectExtended cde) {
		this.cde = cde;
	}

	public void onSearchTasks(ActionEvent event) {
        try {
	    	CampaignProjectController cdc = (CampaignProjectController) FormUtil.getController(IGroupWareConstants.CAMPAIGN_PROJECT_CONTROLLER_NAME);
	        CampaignProjectExtended cde = (CampaignProjectExtended) cdc.getExtendedModel().getRowData();
	        setCampaignProjectExtended(cde);
	        
	        Integer campaignId = cde.getCampaignProject().getCampaign().getId();
	        Integer projectId = cde.getCampaignProject().getProject().getId();

            Criteria criteria = new Criteria();
            criteria.addEqualExpression(getFieldName(IEntityAlias.PROCESS_TASK_CAMPAIGN_ID), campaignId);
            criteria.addEqualExpression(getFieldName(IEntityAlias.PROCESS_TASK_TASK_PROJECT_ID), projectId);
//            criteria.addOrder(getFieldName(IGroupwareAlias.PROCESS_TASK_TASK_ID));
            setCriteria(criteria);
            onSearch(null);
        } catch (ManagerBeanException e) {
            LOGGER.error("Error obtaining campaign tasks", e);
        }
    }

}