package com.code.aon.ui.marketing.event;

import static com.code.aon.ui.marketing.controller.IMarketingConstants.CAMPAIGN_CONTROLLER_NAME;
import static com.code.aon.ui.marketing.controller.IMarketingConstants.SHOW_PHONE_ACTION_MEDIA_TYPE;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.marketing.enumeration.ActionMediaType;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class PhoneActionFilterListener extends ControllerAdapter {

	@Override
	public void beforeModelInitialized(ControllerEvent event)
			throws ControllerListenerException {
		if (! AonUtil.isBeanValue(CAMPAIGN_CONTROLLER_NAME, SHOW_PHONE_ACTION_MEDIA_TYPE) ) {
			IController controller = event.getController();
			try {
				String alias = controller.getFieldName(IEntityAlias.MARKETING_ACTION_MEDIA_TYPE);
				Expression expression = ExpressionUtilities.getNotEqualExpression(alias, ActionMediaType.PHONE);
				controller.getCriteria().addExpression(expression);
			} catch (ManagerBeanException e) {
				throw new ControllerListenerException( e.getMessage(), e );
			}			
		}
	}
	
}