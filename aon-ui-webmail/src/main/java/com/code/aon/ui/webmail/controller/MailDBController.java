package com.code.aon.ui.webmail.controller;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.validator.ValidatorException;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.User;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.common.converter.MappedTransferObjectConverter;
import com.code.aon.ui.form.BasicController;

public abstract class MailDBController extends BasicController {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private final static Logger LOGGER = LoggerFactory.getLogger(MailDBController.class);
	
	private transient Converter converter;
	
	private User user;
	
	protected abstract String getDuplicatedMessage( String name );
	
	protected abstract String getUserAlias() throws ManagerBeanException;
	
	protected abstract String getNameAlias() throws ManagerBeanException;
	
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Converter getConverter() {
		if ( converter == null ) {
			this.converter = new MappedTransferObjectConverter();
		}
		return converter;
	}	
	
	public void setConverter(Converter converter) {
		this.converter = converter;
	}

	public void updateUser( User user ) throws ManagerBeanException {
		if ( user!=null && user.getId()!=null ) {
			setUser(user);
		} else {
			setUser(null);
		}
		resetCriteria();
	}
	
	private void resetCriteria() throws ManagerBeanException {
		clearCriteria();
		completeCriteria( getCriteria() );
	}
	
	public void completeCriteria( Criteria criteria ) throws ManagerBeanException {
		criteria.addExpression(getExpression(user));
	}
	
	public Expression getExpression( User user ) throws ManagerBeanException {
		if ( user != null ) {
			return ExpressionUtilities.getEqualExpression(getUserAlias(), user.getId());	
		}
		String alias = StringUtils.removeEnd(getUserAlias(), ".id");
		return ExpressionUtilities.getNullExpression(alias);
	}
	
	public void idCheck(FacesContext context, UIComponent component, Object value) {
		idCheck( value.toString() );
	}	
	
	private boolean exists( String name ) throws ManagerBeanException {
		Criteria criteria = new Criteria();
		completeCriteria( criteria );
		criteria.addEqualExpression(getNameAlias(), name);		
		return getManagerBean().getCount(criteria) > 0;
	}
	
	protected abstract String getToName();
	
	private boolean isDuplicated( String name ) throws ManagerBeanException {
		boolean skipCheck = false;
		if (! isNevv() ) {
			skipCheck = StringUtils.equals(name, getToName());
		}
		if (! skipCheck ) {
			return exists(name);
		}
		return false;
	}		
	
	private void idCheck( String id ) {
		try {
			if ( isDuplicated(id) ) {
				throw new ValidatorException(new FacesMessage(getDuplicatedMessage(id)));			
			}
		} catch (ManagerBeanException e) {
			LOGGER.error( e.getMessage(), e );
		}
	}		
	
}
