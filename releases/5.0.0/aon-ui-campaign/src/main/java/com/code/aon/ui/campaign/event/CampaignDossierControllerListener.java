package com.code.aon.ui.campaign.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.campaign.dao.ICampaignAlias;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.campaign.controller.CampaignDossierController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class CampaignDossierControllerListener extends ControllerAdapter {

	private final static Logger LOGGER = LoggerFactory
			.getLogger(CampaignDossierControllerListener.class); 

    @Override
    public void beforeModelInitialized(ControllerEvent event) throws ControllerListenerException {
        CampaignDossierController controller = (CampaignDossierController)event.getController();
        try {
            controller.setMainCriteria(controller.getCriteria().getExpression());

            Criteria criteria = controller.getCriteria();
            criteria.addOrder(controller.getManagerBean().getFieldName(ICampaignAlias.CAMPAIGN_DOSSIER_CUSTOMER_NAME));
            criteria.addOrder(controller.getManagerBean().getFieldName(ICampaignAlias.CAMPAIGN_DOSSIER_CUSTOMER_SURNAME));
            criteria.addOrder(controller.getManagerBean().getFieldName(ICampaignAlias.CAMPAIGN_DOSSIER_CUSTOMER_DOCUMENT));
            criteria.addOrder(controller.getManagerBean().getFieldName(ICampaignAlias.CAMPAIGN_DOSSIER_DOSSIER_NUMBER));
            controller.setCriteria(criteria);
            controller.setExtendedModel(null);
            controller.setProcessDetailList(null);
            controller.setProcessCount(null);
        } catch (ManagerBeanException e) {
            LOGGER.error("Error obtaining criteria for CampaignDossier model.", e);
        }
    }

}