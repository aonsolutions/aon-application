package com.code.aon.ui.marketing.event;

import static com.code.aon.ui.marketing.controller.IMarketingConstants.CAMPAIGN_ACTION_TARGET_CONTROLLER_NAME;

import com.code.aon.common.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.marketing.MarketingAction;
import com.code.aon.marketing.MarketingCampaign;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.marketing.controller.CampaignActionTargetController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class CampaignControllerListener extends ControllerAdapter {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	@Override
	public void beforeBeanRemoved(ControllerEvent event) throws ControllerListenerException {
		MarketingCampaign campaign = (MarketingCampaign) event.getController().getTo();
		try {
			deleteActions( campaign );
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException( e.getMessage(), e );
		}
	}
	
	private void deleteActions( MarketingCampaign campaign ) throws ManagerBeanException {
		CampaignActionTargetController catc = (CampaignActionTargetController) AonUtil.getRegisteredBean(CAMPAIGN_ACTION_TARGET_CONTROLLER_NAME);
		IManagerBean bean = BeanManager.getManagerBean(MarketingAction.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.MARKETING_ACTION_CAMPAIGN_ID), campaign.getId());
		for( ITransferObject to : bean.getList(criteria) ) {
			MarketingAction action = (MarketingAction) to;
			catc.deleteActionTargets(action);
			bean.remove(action);
		}
	}
	
}
