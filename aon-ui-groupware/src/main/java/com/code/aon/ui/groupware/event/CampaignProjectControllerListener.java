package com.code.aon.ui.groupware.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.AonVersion;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.groupware.controller.CampaignProjectController;
import com.esferalia.aon.entity.IEntityAlias;

public class CampaignProjectControllerListener extends ControllerAdapter {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private final static Logger LOGGER = LoggerFactory.getLogger(CampaignProjectControllerListener.class); 

    @Override
    public void beforeModelInitialized(ControllerEvent event) throws ControllerListenerException {
        CampaignProjectController controller = (CampaignProjectController)event.getController();
        try {
            controller.setMainCriteria(controller.getCriteria().getExpression());

            Criteria criteria = controller.getCriteria();
            criteria.addOrder(controller.getManagerBean().getFieldName(IEntityAlias.CAMPAIGN_PROJECT_PROJECT_REGISTRY_NAME));
            criteria.addOrder(controller.getManagerBean().getFieldName(IEntityAlias.CAMPAIGN_PROJECT_PROJECT_REGISTRY_DOCUMENT));
            criteria.addOrder(controller.getManagerBean().getFieldName(IEntityAlias.CAMPAIGN_PROJECT_PROJECT_NAME));
            controller.setCriteria(criteria);
            controller.setExtendedModel(null);
            controller.setProcessDetailList(null);
            controller.setProcessCount(null);
        } catch (ManagerBeanException e) {
            LOGGER.error("Error obtaining criteria for CampaignProject model.", e);
            throw new ControllerListenerException(e);
        }
    }

}