package com.code.aon.ui.admin.util;

import static com.code.aon.ui.config.controller.ConfigConstants.DOMAIN_SWITCHER;

import java.io.Serializable;

import javax.faces.application.FacesMessage;
import javax.faces.validator.ValidatorException;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.config.User;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.common.ICommonMessages;
import com.code.aon.ui.config.controller.DomainSwitcher;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class UserIdCheckUtil implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private final static Logger LOGGER = LoggerFactory.getLogger(UserIdCheckUtil.class);

	private IManagerBean bean;
	
	private String alias;
	
	private String domainAlias;
	
	private String oldValue;
	
	public UserIdCheckUtil() {
		try {
			this.bean = BeanManager.getManagerBean(User.class);
			this.alias = bean.getFieldName(IEntityAlias.USER_LOGIN);
			this.domainAlias = bean.getFieldName(IEntityAlias.USER_DOMAIN);
		} catch (ManagerBeanException e) {
			LOGGER.error(e.getMessage(), e);
		}
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
			Expression expr1 = ExpressionUtilities.getEqualExpression(domainAlias, DomainManager.getCurrentDomain());
			DomainSwitcher ds = (DomainSwitcher) AonUtil.getRegisteredBean(DOMAIN_SWITCHER);
			if (! ds.isParentDomain() ) {
				Expression expr2 = ExpressionUtilities.getEqualExpression(domainAlias, ds.getParentDomain());
				expr1 = ExpressionUtilities.getOrExpression(expr1, expr2);
			}			
			criteria.addExpression(expr1);
		}
		criteria.addEqualExpression(alias, newValue);
		return bean.getCount(criteria) > 0;
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
		return AonUtil.getMessage(ICommonMessages.USER_DUPLICATED, newValue);
	}
	
}
