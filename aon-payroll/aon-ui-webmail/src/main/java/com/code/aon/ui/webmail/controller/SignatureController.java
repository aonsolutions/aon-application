package com.code.aon.ui.webmail.controller;

import static com.code.aon.ldap.IAonObjectClasses.ORGANIZATIONAL_UNIT;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import javax.naming.Name;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.bridge.plugin.Utils;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.dao.ldap.ILdapTransferObject;
import com.code.aon.jaas.auth.AuthPrincipal;
import com.code.aon.ldap.NameResolver;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.webmail.MailAccount;
import com.code.aon.webmail.Signature;
import com.code.aon.webmail.dao.IWebMailAlias;

public class SignatureController extends LdapBasicController implements IWebMailConstants {

	private final static Logger LOGGER = LoggerFactory.getLogger(SignatureController.class);

	private List<SelectItem> signatures;
	
	private boolean richTextEnabled = true;
	
	public boolean isRichTextEnabled() {
		return richTextEnabled;
	}

	public void setRichTextEnabled(boolean richTextEnabled) {
		this.richTextEnabled = richTextEnabled;
	}
	
	protected String getDuplicatedMessage( String name ) {
		return AonUtil.getMessage(BUNDLE_NAME, SIGNATURE_DUPLICATED, name);
	}	

	@Override
	public void updateBaseDN(Name parent) {
		String user = NameResolver.getFirstValue(parent);
		String domain = NameResolver.getValue(parent, 2);
		updateBaseDN(domain, user);
	}
	
	@Override
	protected void initDAO() {
		AuthPrincipal auth = Utils.getAuthPrincipal();
		updateBaseDN(auth.getDomain(), auth.getShortName());
	}
	

	private void updateBaseDN( String domain, String user )  {
		Name baseDN = NameResolver.getUserSignaturesDN(domain, user);
		if ( getLdapDAO().exists(baseDN, ORGANIZATIONAL_UNIT) ) {
			getLdapDAO().setBaseDN( baseDN );	
		} else {
			baseDN = NameResolver.getDomainSignaturesDN(domain);
			if ( getLdapDAO().exists(baseDN, ORGANIZATIONAL_UNIT) ) {
				getLdapDAO().setBaseDN( baseDN );
			}
		}		
	}	

	public List<SelectItem> getSignatures() {
		return signatures;
	}

	@SuppressWarnings("unchecked")
	public void updateSignatureList() throws ManagerBeanException {
		this.signatures = new LinkedList<SelectItem>();
		Iterator iter = getManagerBean().getList(getCriteria()).iterator();
		while(iter.hasNext()){
			Signature signature = (Signature)iter.next();
			SelectItem item = new SelectItem(signature.getId(),signature.getName());
			this.signatures.add(item);
		}
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
