package com.code.aon.ui.registry.controller.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.AonVersion;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.esferalia.aon.entity.IEntityAlias;

public class DomainLoookupListener extends ControllerAdapter {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private final static Logger LOGGER = LoggerFactory.getLogger(DomainLoookupListener.class);
	
	@Override
	public void beforeModelInitialized(ControllerEvent event) throws ControllerListenerException {
		Integer domainId = DomainManager.getCurrentDomain();
		try {
			IController controller = event.getController();
			Criteria criteria = controller.getCriteria();
			Expression expr1 = ExpressionUtilities.getEqualExpression(controller.getFieldName(IEntityAlias.DOMAIN_ID), domainId);
			Expression expr2 = ExpressionUtilities.getEqualExpression("Domain.parent<id", domainId);
			criteria.addExpression(ExpressionUtilities.getOrExpression(expr1, expr2));
		} catch (ManagerBeanException e) {
			LOGGER.error(e.getMessage(), e);
			throw new ControllerListenerException( e.getMessage(), e );
		} 
	}
	
}
