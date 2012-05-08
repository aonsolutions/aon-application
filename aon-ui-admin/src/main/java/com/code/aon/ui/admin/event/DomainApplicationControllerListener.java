package com.code.aon.ui.admin.event;

import static com.code.aon.ui.admin.controller.IAdminConstants.ADMIN_CONTROLLER_NAME;
import static com.code.aon.ui.admin.controller.IAdminConstants.APPLICATION_PROFILE_CONTROLLER_NAME;
import static com.code.aon.ui.admin.controller.IAdminConstants.DOMAIN_APPLICATION_PROFILE_CONTROLLER_NAME;

import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.Domain;
import com.code.aon.config.DomainApplication;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.admin.controller.AdminMainController;
import com.code.aon.ui.admin.controller.ApplicationProfileController;
import com.code.aon.ui.admin.controller.DomainApplicationController;
import com.code.aon.ui.admin.controller.DomainController;
import com.code.aon.ui.admin.controller.IAdminConstants;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class DomainApplicationControllerListener extends ControllerAdapter {

	private final static Logger LOGGER = LoggerFactory.getLogger(DomainApplicationControllerListener.class);
	
	private AdminMainController getAdmin() {
		return (AdminMainController) AonUtil.getRegisteredBean(ADMIN_CONTROLLER_NAME);
	}
	
	@Override
	public void afterBeanCreated(ControllerEvent event)
			throws ControllerListenerException {
		getAdmin().resetTermsOfServiceAccepted();	
	}

	@Override
	public void afterBeanAdded(ControllerEvent event)
			throws ControllerListenerException {
		DomainApplicationController dac = (DomainApplicationController) event.getController();
		DomainApplication application = dac.getDomainApplication();
		getAdmin().getLogger().domainApplicationAddded(application);
	}	
	
	@Override
	public void afterBeanRemoved(ControllerEvent event)
			throws ControllerListenerException {
		DomainApplicationController dac = (DomainApplicationController) event.getController();
		DomainApplication application = dac.getDomainApplication();
		getAdmin().getLogger().domainApplicationRemoved(application);
	}

	@Override
	public void afterBeanSelected(ControllerEvent event)
			throws ControllerListenerException {
		DomainApplication da = (DomainApplication) event.getController().getTo();		
		DomainController dc = (DomainController) AonUtil.getRegisteredBean(IAdminConstants.DOMAIN_CONTROLLER_NAME);
		ApplicationProfileController dp = (ApplicationProfileController) AonUtil.getRegisteredBean(DOMAIN_APPLICATION_PROFILE_CONTROLLER_NAME);
		IController profile = FormUtil.getController(APPLICATION_PROFILE_CONTROLLER_NAME);
		try {
			profile.clearCriteria();
			String application = profile.getFieldName(IEntityAlias.PROFILE_APPLICATION_ID);
			profile.getCriteria().addEqualExpression(application, da.getApplication().getId());
			
			List<Expression> initExpressions = new LinkedList<Expression>();
			String domainAlias = profile.getFieldName(IEntityAlias.PROFILE_DOMAIN_ID);
			Expression expr1 = ExpressionUtilities.getEqualExpression(domainAlias, dc.getDomain().getId());
			Domain parent = dc.getDomain().getParent();
			if ( (parent != null) && (parent.getId() != null) ) {
				Expression expr2 = ExpressionUtilities.getEqualExpression(domainAlias, parent.getId());
				initExpressions.add( ExpressionUtilities.getOrExpression(expr1, expr2) );				
			} else {
				initExpressions.add(expr1);
			}
			dp.setInitExpressions(initExpressions);
		} catch (ManagerBeanException e) {
			LOGGER.error(e.getMessage(), e);
			throw new ControllerListenerException( e.getMessage(), e );
		}
	}	
	
}