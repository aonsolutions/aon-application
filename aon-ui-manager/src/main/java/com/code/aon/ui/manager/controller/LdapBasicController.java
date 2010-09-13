package com.code.aon.ui.manager.controller;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.validator.ValidatorException;
import javax.naming.Name;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.BasicManagerBean;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.sql.DAOException;
import com.code.aon.dao.ldap.LdapDAO;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.util.AonUtil;

public abstract class LdapBasicController extends BasicController implements IManagerConstants {
	
	private final static Logger LOGGER = LoggerFactory.getLogger(LdapBasicController.class);
	
	private LdapDAO ldapDAO;
	
	private BasicManagerBean ldapManagerBean;
	
	public abstract void updateBaseDN( Name parent );
	
	public LdapDAO getLdapDAO() {
		return ldapDAO;
	}

	@Override
	@SuppressWarnings("unchecked")	
	public IManagerBean getManagerBean() throws ManagerBeanException {
		if ( this.ldapManagerBean == null ) {
			Class<? extends ITransferObject> pojoClass;
			try {
				pojoClass = (Class<? extends ITransferObject>) Class.forName( getPojo() );
				this.ldapDAO = new LdapDAO( pojoClass );		
				this.ldapManagerBean = new BasicManagerBean(this.ldapDAO);			
			} catch (ClassNotFoundException e) {
				LOGGER.error( e.getMessage(), e );
			}
		}
		return this.ldapManagerBean;
	}
	
	public boolean isDuplicated( String name ) throws DAOException {
		Name currentId = getLdapDAO().calculateDN(name);
		boolean skipCheck = false;
		if (! isNew() ) {
			skipCheck = currentId.equals( this.savedToId );
		}
		if (! skipCheck ) {
			return getLdapDAO().exists(currentId);
		}
		return false;
	}		
	
	public boolean isValidName( String name ) {
		return name.matches("[a-zA-Z][a-zA-Z0-9_-]*");
	}
	
	public void idCheck(FacesContext context, UIComponent component, Object value) {
		String name = value.toString();
		if (! isValidName(name) ) {
			String summary = AonUtil.getMessage(BUNDLE_NAME, INVALID_NAME, name);
			throw new ValidatorException( new FacesMessage(summary) );
		}
		try {
			if ( isDuplicated(name) ) {
				String summary = AonUtil.getMessage(BUNDLE_NAME, ID_DUPLICATED, name);
				throw new ValidatorException( new FacesMessage(summary) );			
			}
		} catch (DAOException e) {
			LOGGER.error( e.getMessage(), e );
		}
	}	
	
	@Override
	public void accept(ActionEvent event) {		
		try {
			ITransferObject to = getTo();
			Name currentId = getLdapDAO().calculateDN(to);
			if (! isNew() ) {
				Name oldId = (Name) this.savedToId;
				if (! oldId.equals(currentId) ) {
					if ( getLdapDAO().exists(currentId) ) {
						AonUtil.addErrorMessageFromBundle(BUNDLE_NAME, ID_DUPLICATED );
						return;
					}			
					getManagerBean().setId( to, oldId );
					getManagerBean().remove( to );
					getManagerBean().setId( to, null );
					setNew(true);
				}
			}
		} catch (ManagerBeanException e) {
	        LOGGER.error(">>>> accept", e );
	        addMessage(e.getMessage());
	        throw new AbortProcessingException(e.getMessage(), e);	        
		} catch (DAOException e) {
	        LOGGER.error(">>>> accept ", e );
	        addMessage(e.getMessage());
	        throw new AbortProcessingException(e.getMessage(), e);	        
		}				
		super.accept(event);
	}	
	
}
