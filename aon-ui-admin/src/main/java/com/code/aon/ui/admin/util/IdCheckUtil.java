package com.code.aon.ui.admin.util;

import static com.code.aon.ui.admin.controller.IAdminConstants.BUNDLE_NAME;
import static com.code.aon.ui.config.controller.ConfigConstants.DOMAIN_SWITCHER;

import javax.faces.application.FacesMessage;
import javax.faces.validator.ValidatorException;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.config.controller.DomainSwitcher;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.util.AonUtil;

public class IdCheckUtil {

	private final static Logger LOGGER = LoggerFactory.getLogger(IdCheckUtil.class);

	private IController controller;
	
	private String alias;
	
	private String domainAlias;
	
	private String duplicateMessage;
	
	private String oldValue;
	
	public IdCheckUtil( IController controller, String alias, String duplicateMessage ) {
		this.controller = controller;
		this.alias = alias;
		this.duplicateMessage = duplicateMessage;
	}

	public String getDomainAlias() {
		return domainAlias;
	}

	public void setDomainAlias(String domainAlias) {
		this.domainAlias = domainAlias;
	}

	public String getOldValue() {
		return oldValue;
	}

	public void setOldValue(String oldValue) {
		this.oldValue = oldValue;
	}

	public void idCheck( String newValue ) {
		try {
			if ( isDuplicated(newValue) ) {
				throw new ValidatorException(new FacesMessage(getDuplicatedMessage(newValue)));			
			}
		} catch (ManagerBeanException e) {
			LOGGER.error( e.getMessage(), e );
		}
	}		
	
	private boolean exists( String newValue ) throws ManagerBeanException {
		Criteria criteria = new Criteria();
		if ( domainAlias != null ) {
			criteria.setSkipDomainFilter(true);
			String field = controller.getFieldName(domainAlias);
			Expression expr1 = ExpressionUtilities.getEqualExpression(field, DomainManager.getCurrentDomain());
			if (! DomainManager.isParentDomain() ) {
				DomainSwitcher ds = (DomainSwitcher) AonUtil.getRegisteredBean(DOMAIN_SWITCHER);
				Expression expr2 = ExpressionUtilities.getEqualExpression(field, ds.getParentDomain());
				expr1 = ExpressionUtilities.getOrExpression(expr1, expr2);
			}			
			criteria.addExpression(expr1);
		}
		criteria.addEqualExpression(controller.getFieldName(alias), newValue);
		return controller.getManagerBean().getCount(criteria) > 0;
	}
	
	private boolean isDuplicated( String newValue ) throws ManagerBeanException {
		boolean skipCheck = false;
		if ( oldValue != null ) {
			skipCheck = StringUtils.equals(oldValue, newValue);
		}
		if (! skipCheck ) {
			return exists(newValue);
		}
		return false;
	}		
	
	private String getDuplicatedMessage( String newValue ) {
		return AonUtil.getMessage(BUNDLE_NAME, duplicateMessage, newValue);
	}
	
}
