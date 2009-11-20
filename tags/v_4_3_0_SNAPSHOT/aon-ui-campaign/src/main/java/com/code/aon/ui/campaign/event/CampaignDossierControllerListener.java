package com.code.aon.ui.campaign.event;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.code.aon.campaign.dao.ICampaignAlias;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.campaign.controller.CampaignDossierController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class CampaignDossierControllerListener extends ControllerAdapter {

    private static final Logger LOGGER = Logger.getLogger(CampaignDossierControllerListener.class.getName()); 

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
        } catch (ManagerBeanException e) {
            LOGGER.log(Level.SEVERE, "Error obtaining criteria for CampaignDossier model.", e);
        }
    }

}