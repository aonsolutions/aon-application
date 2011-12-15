package com.code.aon.ui.webmail.controller;

import static com.code.aon.ui.webmail.controller.IWebMailConstants.BUNDLE_NAME;
import static com.code.aon.ui.webmail.controller.IWebMailConstants.ID_DUPLICATED;
import static com.code.aon.ui.webmail.controller.IWebMailConstants.ID_USED;
import static com.code.aon.ui.webmail.controller.IWebMailConstants.INVALID_NAME;

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
import com.code.aon.dao.ldap.ILdapTransferObject;
import com.code.aon.dao.ldap.LdapDAO;
import com.code.aon.ldap.NameResolver;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.util.AonUtil;

public abstract class LdapBasicController extends BasicController {
	
	private final static Logger LOGGER = LoggerFactory.getLogger(LdapBasicController.class);
	
	private LdapDAO ldapDAO;
	
	private BasicManagerBean ldapManagerBean;
	
	public boolean updateBaseDN( Name parent ) {
		return true;
	}
	
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
				initDAO();
				this.ldapManagerBean = new BasicManagerBean(this.ldapDAO);			
			} catch (ClassNotFoundException e) {
				LOGGER.error( e.getMessage(), e );
			}
		}
		return this.ldapManagerBean;
	}
	
	protected void initDAO() {
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
		return name.matches("\\p{Alpha}[\\w\\.\\-]*");
	}
	
	protected String getInvalidMessage( String name ) {
		return AonUtil.getMessage(BUNDLE_NAME, INVALID_NAME, name);
	}

	protected String getDuplicatedMessage( String name ) {
		return AonUtil.getMessage(BUNDLE_NAME, ID_DUPLICATED, name);
	}
	
	public void idCheck(FacesContext context, UIComponent component, Object value) {
		idCheck( value.toString() );
	}	
	
	protected void idCheck( String id ) {
		if (! isValidName(id) ) {
			throw new ValidatorException(new FacesMessage(getInvalidMessage(id)));
		}
		try {
			if ( isDuplicated(id) ) {
				throw new ValidatorException(new FacesMessage(getDuplicatedMessage(id)));			
			}
		} catch (DAOException e) {
			LOGGER.error( e.getMessage(), e );
		}
	}		
	
	@Override
	public void accept(ActionEvent event) {		
		boolean idChanged = false;
		Name oldId = null;
		try {
			ITransferObject to = getTo();
			Name currentId = getLdapDAO().calculateDN(to);
			if (! isNew() ) {
				oldId = (Name) this.savedToId;
				if (! oldId.equals(currentId) ) {
					if ( getLdapDAO().exists(currentId) ) {
						String name = NameResolver.getFirstValue(currentId);
						AonUtil.addErrorMessage( getDuplicatedMessage(name) );
						return;
					}			
					getManagerBean().setId( to, oldId );
					getManagerBean().remove( to );
					getManagerBean().setId( to, null );
					setNew(true);
					idChanged = true;
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
		if ( idChanged ) {
			afterIdChanged( oldId, (ILdapTransferObject) getTo() );
		}
	}	
	
	@Override
	public void onRemove(ActionEvent event) {
		try {
			ILdapTransferObject to = (ILdapTransferObject) getTo();
			if ( isUsed(to) ) {
				AonUtil.addErrorMessageFromBundle(BUNDLE_NAME, ID_USED );
				return;
			}
		} catch (Throwable e) {
	        LOGGER.error(">>>> onRemove", e );
	        addMessage(e.getMessage());
	        throw new AbortProcessingException(e.getMessage(), e);	        
		}			
		super.onRemove(event);
	}	
	
	protected boolean isUsed( ILdapTransferObject to ) throws ManagerBeanException {
		return false;
	}	
	
	protected void afterIdChanged( Name oldId, ILdapTransferObject to ) {
	}
	
}
