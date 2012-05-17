package com.code.aon.ui.webmail.controller;

import static com.code.aon.ui.webmail.controller.IWebMailConstants.BUNDLE_NAME;
import static com.code.aon.ui.webmail.controller.IWebMailConstants.INVALID_NAME;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.validator.ValidatorException;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.User;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.common.converter.MappedTransferObjectConverter;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.util.AonUtil;

public abstract class MailDBController extends BasicController {

	private final static Logger LOGGER = LoggerFactory.getLogger(MailDBController.class);
	
	private Converter converter;
	
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
		if ( (user != null) && (user.getId() != null) ) {
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
		if ( user != null ) {
			criteria.addEqualExpression(getUserAlias(), user.getId());	
		} else {
			String alias = StringUtils.removeEnd(getUserAlias(), ".id");
			criteria.addNullExpression(alias);
		}		
	}
	
	public void idCheck(FacesContext context, UIComponent component, Object value) {
		idCheck( value.toString() );
	}	
	
	private boolean isValidName( String name ) {
		return name.matches("\\p{Alpha}[\\w\\.\\-]*");
	}
	
	private String getInvalidMessage( String name ) {
		return AonUtil.getMessage(BUNDLE_NAME, INVALID_NAME, name);
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
		if (! isNew() ) {
			skipCheck = StringUtils.equals(name, getToName());
		}
		if (! skipCheck ) {
			return exists(name);
		}
		return false;
	}		
	
	private void idCheck( String id ) {
		if (! isValidName(id) ) {
			throw new ValidatorException(new FacesMessage(getInvalidMessage(id)));
		}
		try {
			if ( isDuplicated(id) ) {
				throw new ValidatorException(new FacesMessage(getDuplicatedMessage(id)));			
			}
		} catch (ManagerBeanException e) {
			LOGGER.error( e.getMessage(), e );
		}
	}		
	
}
