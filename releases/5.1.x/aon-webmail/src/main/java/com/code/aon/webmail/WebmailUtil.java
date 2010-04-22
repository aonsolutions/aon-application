package com.code.aon.webmail;

import java.util.List;

import javax.naming.Name;

import com.code.aon.common.BasicManagerBean;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.dao.ldap.LdapDAO;
import com.code.aon.ldap.NameResolver;
import com.code.aon.ql.Criteria;
import com.code.aon.webmail.dao.IWebMailAlias;

public class WebmailUtil {

	public static LdapDAO getMailAccountDAO( String domain, String user ) {
		LdapDAO dao = new LdapDAO(MailAccount.class);
		Name baseDN = NameResolver.getUserAccountsDN(domain, user);
		dao.setBaseDN( baseDN );
		return dao;
	}
	
	public static LdapDAO getSignatureDAO( String domain, String user ) {
		LdapDAO dao = new LdapDAO(Signature.class);
		Name baseDN = NameResolver.getUserSignaturesDN(domain, user);
		dao.setBaseDN( baseDN );
		return dao;
	}

    public static MailAccount getDefaultAccount( String domain, String user ) throws ManagerBeanException {
    	LdapDAO dao = getMailAccountDAO(domain, user);
		IManagerBean beanAccount = new BasicManagerBean(dao);
		Criteria criteriaAccount = new Criteria();
		criteriaAccount.addEqualExpression(beanAccount.getFieldName(IWebMailAlias.MAIL_ACCOUNT_NAME), MailAccount.DEFAULT_MAIL_ACCOUNT_NAME);
		List<ITransferObject> list = beanAccount.getList(criteriaAccount);
		if (! list.isEmpty() ) {
			MailAccount mailAccount = (MailAccount) list.get(0);
			return mailAccount;
		}
		return null;
    }

	
}
