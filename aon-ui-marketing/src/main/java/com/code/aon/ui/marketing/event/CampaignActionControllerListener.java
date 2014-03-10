package com.code.aon.ui.marketing.event;

import static com.code.aon.ui.marketing.controller.IMarketingConstants.CAMPAIGN_ACTION_TARGET_CONTROLLER_NAME;

import com.code.aon.common.AonVersion;
import com.code.aon.marketing.MarketingAction;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.marketing.controller.CampaignActionTargetController;
import com.code.aon.ui.util.AonUtil;

public class CampaignActionControllerListener extends ControllerAdapter {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	@Override
	public void beforeBeanRemoved(ControllerEvent event) throws ControllerListenerException {
		MarketingAction action = (MarketingAction) event.getController().getTo();
		CampaignActionTargetController catc = (CampaignActionTargetController) AonUtil.getRegisteredBean(CAMPAIGN_ACTION_TARGET_CONTROLLER_NAME);
		catc.deleteActionTargets(action);
	}
	
}
