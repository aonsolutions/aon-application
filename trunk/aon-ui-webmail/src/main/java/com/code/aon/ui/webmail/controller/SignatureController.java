package com.code.aon.ui.webmail.controller;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.StringUtils;

import com.code.aon.bridge.plugin.Utils;
import com.code.aon.common.BasicManagerBean;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.sql.DAOException;
import com.code.aon.dao.ldap.LdapDAO;
import com.code.aon.jaas.auth.AuthPrincipal;
import com.code.aon.ldap.AonDN;
import com.code.aon.ldap.DistinguishedName;
import com.code.aon.ui.form.GridController;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.ui.webmail.bean.AonConstants;
import com.code.aon.webmail.Signature;

public class SignatureController extends GridController {

	private static final String SIGNATURE_DUPLICATED = "aon_webmail_signature_duplicated";

	private static final Logger LOGGER = Logger.getLogger(SignatureController.class.getName());

	private LdapDAO dao;
	
	private BasicManagerBean ldapManagerBean;

	public LdapDAO getDAO( AuthPrincipal principal ) {
		LdapDAO dao = new LdapDAO(Signature.class);
		DistinguishedName baseDN = AonDN.getUserSignaturesDN(principal.getDomain(), principal.getShortName());
		LOGGER.info( "Signature DAO DN:" + baseDN );
		dao.setBaseDN( baseDN.toString() );
		return dao;
	}
	
	@Override
	public IManagerBean getManagerBean() throws ManagerBeanException {
		if (this.ldapManagerBean == null) {
			this.dao = getDAO(Utils.getAuthPrincipal());
			this.ldapManagerBean = new BasicManagerBean(dao);
		}
		return this.ldapManagerBean;
	}	
	
	private void addMessageExpression( String messageId ) {
		Locale locale = AonUtil.getCurrentLocale();
		ResourceBundle bundle = ResourceBundle.getBundle(AonConstants.RESOURCE_BUNDLE, locale);
		addMessage( bundle.getString(messageId) );
	}
	
	@Override
	public void accept(ActionEvent event) {		
		String oldId = (String) this.savedToId;
		try {
			String currentId = this.dao.calculateDN(getTo());
			if ( isNew() ) {
				if ( dao.exists(currentId) ) {
					addMessageExpression(SIGNATURE_DUPLICATED);
		            return;
				}			
			} else {
				if (! StringUtils.equals(oldId, currentId) ) {
					if ( dao.exists(currentId) ) {
						addMessageExpression(SIGNATURE_DUPLICATED);
						return;
					}			
					getManagerBean().setId( getTo(), oldId );
					getManagerBean().remove( getTo() );
					getManagerBean().setId( getTo(), null );
					setNew(true);
				}
			}
		} catch (ManagerBeanException e) {
	        LOGGER.severe(">>>> accept " + e.getMessage());
	        addMessage(e.getMessage());
	        throw new AbortProcessingException(e.getMessage(), e);	        
		} catch (DAOException e) {
	        LOGGER.severe(">>>> accept " + e.getMessage());
	        addMessage(e.getMessage());
	        throw new AbortProcessingException(e.getMessage(), e);	        
		}				
		super.accept(event);
	}

	@SuppressWarnings("unchecked")
	public List<SelectItem> getSignatures() throws ManagerBeanException {
		List<SelectItem> series = new LinkedList<SelectItem>();
		Iterator iter = getManagerBean().getList(getCriteria()).iterator();
		while(iter.hasNext()){
			Signature signature = (Signature)iter.next();
			SelectItem item = new SelectItem(signature.getId(),signature.getName());
			series.add(item);
		}
		return series;
	}
	
}
