package com.code.aon.ui.webmail.controller;

import static com.code.aon.ldap.IAonObjectClasses.DOMAIN;
import static com.code.aon.ldap.IAonObjectClasses.ORGANIZATIONAL_UNIT;
import static com.code.aon.ldap.IAonObjectClasses.USER;
import static com.code.aon.ldap.NameResolver.DOMAINS;
import static com.code.aon.ui.webmail.controller.IWebMailConstants.BEAN_MAIL_ACCOUNT;
import static com.code.aon.ui.webmail.controller.IWebMailConstants.BUNDLE_NAME;
import static com.code.aon.ui.webmail.controller.IWebMailConstants.SIGNATURE_DUPLICATED;
import static com.code.aon.ui.webmail.controller.IWebMailConstants.SIGNATURE_USED;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.faces.convert.Converter;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import javax.naming.Name;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.util.BasicPrincipal;
import com.code.aon.dao.ldap.ILdapTransferObject;
import com.code.aon.jaas.auth.AuthPrincipal;
import com.code.aon.ldap.NameResolver;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.ui.webmail.converter.LdapTransferObjectConverter;
import com.code.aon.webmail.MailAccount;
import com.code.aon.webmail.Signature;
import com.code.aon.webmail.dao.IWebMailAlias;

public class SignatureController extends LdapBasicController implements ISignatureController {

	private final static Logger LOGGER = LoggerFactory.getLogger(SignatureController.class);

	private Converter converter;
	
	protected String getDuplicatedMessage( String name ) {
		return AonUtil.getMessage(BUNDLE_NAME, SIGNATURE_DUPLICATED, name);
	}	

	@Override
	public boolean updateBaseDN(Name parent) {
		boolean updated = false;
		String container = NameResolver.getValue(parent, 1);
		if ( StringUtils.equals(container, DOMAINS) ) {
			String domain = NameResolver.getFirstValue(parent);
			updated = updateBaseDN( domain );
		} else {
			String user = NameResolver.getFirstValue(parent);
			String domain = NameResolver.getValue(parent, 2);
			updated = updateBaseDN(domain, user);
		}		
		return updated;
	}
	
	private boolean updateBaseDN( String domain )  {
		Name domainDN = NameResolver.getDomainDN(domain);
		if ( getLdapDAO().exists(domainDN, DOMAIN) ) { 
			Name baseDN = NameResolver.getDomainSignaturesDN(domain);
			if (! getLdapDAO().exists(baseDN, ORGANIZATIONAL_UNIT) ) {
				getLdapDAO().addOrganizationUnit(baseDN);
			}
			getLdapDAO().setBaseDN( baseDN );
			return true;
		}
		LOGGER.warn( "LDAP entry not found: {}", domainDN );	
		return false;			
	}	
	
	private boolean updateBaseDN( String domain, String user )  {
		Name userDN = NameResolver.getUserDN(domain, user);
		if ( getLdapDAO().exists(userDN, USER) ) { 
			Name baseDN = NameResolver.getUserSignaturesDN(domain, user);
			if (! getLdapDAO().exists(baseDN, ORGANIZATIONAL_UNIT) ) {
				getLdapDAO().addOrganizationUnit(baseDN);
			}
			getLdapDAO().setBaseDN( baseDN );
			return true;
		}
		LOGGER.warn( "LDAP entry not found: {}", userDN );	
		return false;			
	}			
	
	@Override
	protected void initDAO() {
		AuthPrincipal auth = BasicPrincipal.getAuthPrincipal();
		updateBaseDN(auth.getDomain(), auth.getShortName());
	}

	@Override
	public Converter getConverter() {
		if ( converter == null ) {
			this.converter = new LdapTransferObjectConverter(this);			
		}
		return converter;
	}

	@Override
	public List<SelectItem> getSignatures() {
		List<SelectItem> signatures = new LinkedList<SelectItem>();
		try {		
			Iterator<ITransferObject> iter = getManagerBean().getList(getCriteria()).iterator();
			while(iter.hasNext()){
				Signature signature = (Signature)iter.next();
				SelectItem item = new SelectItem(signature, signature.getName());
				signatures.add(item);
			}
		} catch (ManagerBeanException e) {
            LOGGER.error(">>>> getSignatures", e);
		}		
		return signatures;
	}

	@SuppressWarnings("unchecked")
	private List<MailAccount> getReferences( Name id ) {
		List<MailAccount> list = null;
		try {
			IManagerBean mailAccountBean = FormUtil.getController(BEAN_MAIL_ACCOUNT).getManagerBean();
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(mailAccountBean.getFieldName(IWebMailAlias.MAIL_ACCOUNT_SIGNATURE_ID), id);
			list = (List) mailAccountBean.getList(criteria);
		} catch (ManagerBeanException e) {
            LOGGER.error(">>>> getReferences", e);
		}		
		return list;
	}

	@Override
	protected void afterIdChanged( Name id, ILdapTransferObject to ) {
		try {
			IManagerBean mailAccountBean = FormUtil.getController(BEAN_MAIL_ACCOUNT).getManagerBean();
			for( MailAccount mailAccount : getReferences(id) ) {
				mailAccount.setSignature( (Signature) to );
				mailAccountBean.update( mailAccount );
			}
		} catch (ManagerBeanException e) {
			LOGGER.error(">>>> updateReferences", e);
		}					
	}
	
	private boolean checkRemovable( Signature signature ) {
		List<MailAccount> list = getReferences( signature.getId() );
		if ( (list!=null) && (!list.isEmpty()) ) {
			AonUtil.addErrorMessageFromBundle( BUNDLE_NAME, SIGNATURE_USED, signature.getName() );
			return false;
		}
		return true;
	}
	
	@Override
	public void onRemove(ActionEvent event) {
		if ( checkRemovable((Signature) getTo() ) ) {
			super.onRemove(event);			
		}
	}
	
}
