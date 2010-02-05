package com.code.aon.ui.webmail.controller;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import javax.naming.Name;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.bridge.plugin.Utils;
import com.code.aon.common.BasicManagerBean;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.sql.DAOException;
import com.code.aon.dao.ldap.LdapDAO;
import com.code.aon.jaas.auth.AuthPrincipal;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.GridController;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.ui.webmail.bean.WebMailConstants;
import com.code.aon.webmail.MailAccount;
import com.code.aon.webmail.Signature;
import com.code.aon.webmail.WebmailUtil;
import com.code.aon.webmail.bean.BundleConstants;
import com.code.aon.webmail.dao.IWebMailAlias;

public class SignatureController extends GridController {

	private static final String SIGNATURE_DUPLICATED = "webmail_signature_duplicated";

	private final static Logger LOGGER = LoggerFactory.getLogger(SignatureController.class);

	private LdapDAO dao;
	
	private BasicManagerBean ldapManagerBean;
	
	private List<SelectItem> signatures;
	
	@Override
	public IManagerBean getManagerBean() throws ManagerBeanException {
		if (this.ldapManagerBean == null) {
			AuthPrincipal auth = Utils.getAuthPrincipal();
			this.dao = WebmailUtil.getSignatureDAO(auth.getDomain(), auth.getShortName());
			this.ldapManagerBean = new BasicManagerBean(dao);
		}
		return this.ldapManagerBean;
	}	
	
	private void addMessageExpression( String messageId ) {
		Locale locale = AonUtil.getCurrentLocale();
		ResourceBundle bundle = ResourceBundle.getBundle(BundleConstants.RESOURCE_BUNDLE, locale);
		addMessage( bundle.getString(messageId) );
	}
	
	@Override
	public void accept(ActionEvent event) {		
		boolean renamed = false;
		Name oldId = null;
		try {
			Name currentId = this.dao.calculateDN(getTo());
			if ( isNew() ) {
				if ( dao.exists(currentId) ) {
					addMessageExpression(SIGNATURE_DUPLICATED);
		            return;
				}			
			} else {
				oldId = (Name) this.savedToId;				
				if (! oldId.equals(currentId) ) {
					if ( dao.exists(currentId) ) {
						addMessageExpression(SIGNATURE_DUPLICATED);
						return;
					}			
					getManagerBean().setId( getTo(), oldId );
					getManagerBean().remove( getTo() );
					getManagerBean().setId( getTo(), null );
					setNew(true);
					renamed = true;
				}
			}
		} catch (ManagerBeanException e) {
	        LOGGER.error(">>>> accept", e);
	        addMessage(e.getMessage());
	        throw new AbortProcessingException(e.getMessage(), e);	        
		} catch (DAOException e) {
			LOGGER.error(">>>> accept", e);
	        addMessage(e.getMessage());
	        throw new AbortProcessingException(e.getMessage(), e);	        
		}				
		super.accept(event);
		if ( renamed ) {
			updateReferences( oldId, (Signature) getTo() );
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
			IManagerBean mailAccountBean = FormUtil.getController(WebMailConstants.BEAN_MAIL_ACCOUNT).getManagerBean();
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(mailAccountBean.getFieldName(IWebMailAlias.MAIL_ACCOUNT_SIGNATURE_ID), id);
			list = (List) mailAccountBean.getList(criteria);
		} catch (ManagerBeanException e) {
            LOGGER.error(">>>> getReferences", e);
		}		
		return list;
	}

	private void updateReferences( Name id, Signature signature ) {
		try {
			IManagerBean mailAccountBean = FormUtil.getController(WebMailConstants.BEAN_MAIL_ACCOUNT).getManagerBean();
			for( MailAccount mailAccount : getReferences(id) ) {
				mailAccount.setSignature( signature );
				mailAccountBean.update( mailAccount );
			}
		} catch (ManagerBeanException e) {
			LOGGER.error(">>>> updateReferences", e);
		}					
	}
	
	private boolean checkRemovable( Signature signature ) {
		List<MailAccount> list = getReferences( signature.getId() );
		if ( (list!=null) && (!list.isEmpty()) ) {
			AonUtil.addErrorMessage( "La Firma " + signature.getName() + " no se puede borrar porque esta siendo utlizada." );
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

	@Override
	public void onRemoveSelected(ActionEvent event) {
		boolean remove = true;
		for (ITransferObject to: getCheckList()) {
			remove = checkRemovable(( Signature) to );
		}
		if ( remove ) {
			super.onRemoveSelected(event);	
		}
	}
	
}
