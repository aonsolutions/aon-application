package com.code.aon.ui.marketing.event;

import java.util.Map;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.marketing.dao.IMarketingAlias;
import com.code.aon.marketing.enumeration.ActionMediaType;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.common.controller.ConfigurationController;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.marketing.controller.IMarketingConstants;
import com.code.aon.ui.util.AonUtil;

public class CampaignActionLookupListener extends ControllerAdapter implements IMarketingConstants {

	@Override
	public void beforeModelInitialized(ControllerEvent event)
			throws ControllerListenerException {
		ConfigurationController cc = AonUtil.getConfigurationController();
		if ( cc.getBean() != null ) {
			Map<String,Object> map = cc.getBean().get(CAMPAIGN_CONTROLLER_NAME);
			if ( map != null ) {
				Object value = map.get(SHOW_PHONE_ACTION_MEDIA_TYPE);
				if ( (value != null) && !((Boolean)value).booleanValue() ) {
					IController controller = event.getController();
					try {
						String alias = controller.getFieldName(IMarketingAlias.MARKETING_ACTION_MEDIA_TYPE);
						Expression expression = ExpressionUtilities.getNotEqualExpression(alias, ActionMediaType.PHONE);
						controller.getCriteria().addExpression(expression);
					} catch (ManagerBeanException e) {
						throw new ControllerListenerException( e.getMessage(), e );
					}
				}
			}			
		}

	}
	
}