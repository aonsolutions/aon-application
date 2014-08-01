package com.code.aon.ui.admin.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.Domain;
import com.code.aon.config.DomainApplication;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.admin.controller.ApplicationProfileController;
import com.code.aon.ui.admin.controller.DomainController;
import com.code.aon.ui.admin.controller.IAdminConstants;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.form.listener.LinesControllerListener;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class DomainApplicationProfileLinesListener extends LinesControllerListener {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private final static Logger LOGGER = LoggerFactory.getLogger(DomainApplicationProfileLinesListener.class);

	@Override
	protected void updateDetailCriteria(IController master, boolean reset)
			throws ControllerListenerException {
		ApplicationProfileController dp = (ApplicationProfileController) getDetailController();
		DomainController dc = (DomainController) AonUtil.getRegisteredBean(IAdminConstants.DOMAIN_CONTROLLER_NAME);
		DomainApplication da = (DomainApplication) master.getTo();
		try {
			dp.clearCriteria();
			String appAlias = dp.getFieldName(IEntityAlias.PROFILE_APPLICATION_ID);
			dp.getCriteria().addEqualExpression(appAlias, da.getApplication().getId());
			String domainAlias = dp.getFieldName(IEntityAlias.PROFILE_DOMAIN_ID);
			Expression expr1 = ExpressionUtilities.getEqualExpression(domainAlias, da.getDomain());
			Domain parent = dc.getParentDomain();
			if ( parent != null ) {
				Expression expr2 = ExpressionUtilities.getEqualExpression(domainAlias, parent.getId());
				dp.getCriteria().addExpression( ExpressionUtilities.getOrExpression(expr1, expr2) );				
			} else {
				dp.getCriteria().addExpression(expr1);
			}
		} catch (ManagerBeanException e) {
			LOGGER.error(e.getMessage(), e);
			throw new ControllerListenerException( e.getMessage(), e );
		} 
	}

}
