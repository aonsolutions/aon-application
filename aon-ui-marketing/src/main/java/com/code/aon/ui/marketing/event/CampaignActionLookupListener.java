package com.code.aon.ui.marketing.event;

import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang.time.DateUtils;

import com.code.aon.AonVersion;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.config.util.UserUtils;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.esferalia.aon.entity.IEntityAlias;

public class CampaignActionLookupListener extends ControllerAdapter {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	@Override
	public void beforeModelInitialized(ControllerEvent event)
			throws ControllerListenerException {
		try {
			IController controller = event.getController();
			Criteria criteria = getController().getCriteria();
			String endDate = controller.getFieldName(IEntityAlias.MARKETING_ACTION_END_DATE);
			Date date = DateUtils.truncate(new Date(), Calendar.DATE);
			date = DateUtils.addDays(date, 1);
			Expression expr1 = ExpressionUtilities.getNullExpression(endDate);
			Expression expr2 = ExpressionUtilities.getGreaterThanOrEqualExpression(endDate, date);
			criteria.addExpression( ExpressionUtilities.getOrExpression(expr1, expr2) );
			String scope = controller.getFieldName(IEntityAlias.MARKETING_ACTION_CAMPAIGN_SCOPE_ID);
			UserUtils.getInstance().addNullableScopeExpression(criteria, scope);
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException( e.getMessage(), e );
		}
	}
}
