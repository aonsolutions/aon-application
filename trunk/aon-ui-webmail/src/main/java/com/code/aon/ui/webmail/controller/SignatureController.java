package com.code.aon.ui.webmail.controller;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import javax.faces.model.SelectItem;

import org.apache.commons.lang.StringUtils;

import com.code.aon.common.BasicManagerBean;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.IDAO;
import com.code.aon.common.dao.sql.DAOException;
import com.code.aon.dao.ldap.LdapDAO;
import com.code.aon.jaas.auth.AuthPrincipal;
import com.code.aon.ldap.AonDN;
import com.code.aon.ldap.DistinguishedName;
import com.code.aon.ui.form.GridController;
import com.code.aon.webmail.Signature;

public class SignatureController extends GridController {

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
			this.dao = getDAO(LoginController.getPrincipal());
			this.ldapManagerBean = new BasicManagerBean(dao);
		}
		return this.ldapManagerBean;
	}	
	
	@Override
	protected void accept() {
		if (! isNew() ) {
			String oldId = (String) this.savedToId;
			try {
				String currentId = this.dao.calculateDN( getTo() );
				if (! StringUtils.equals(oldId, currentId) ) {
					getManagerBean().setId( getTo(), oldId );
					getManagerBean().remove( getTo() );
					getManagerBean().setId( getTo(), null );
					setNew(true);
				}
			} catch (ManagerBeanException e) {
				LOGGER.severe( e.getMessage() );
			} catch (DAOException e) {
				LOGGER.severe( e.getMessage() );
			}				
		}
		super.accept();
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
