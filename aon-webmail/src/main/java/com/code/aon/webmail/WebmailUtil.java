package com.code.aon.webmail;

import java.util.Iterator;

import com.code.aon.common.BasicManagerBean;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.dao.ldap.LdapDAO;
import com.code.aon.ldap.AonDN;
import com.code.aon.ldap.DistinguishedName;
import com.code.aon.ql.Criteria;
import com.code.aon.webmail.dao.IWebMailAlias;

public class WebmailUtil {

	public static LdapDAO getMailAccountDAO( String domain, String user ) {
		LdapDAO dao = new LdapDAO(MailAccount.class);
		DistinguishedName baseDN = AonDN.getUserAccountsDN(domain, user);
		dao.setBaseDN( baseDN.toString() );
		return dao;
	}
	
	public static LdapDAO getSignatureDAO( String domain, String user ) {
		LdapDAO dao = new LdapDAO(Signature.class);
		DistinguishedName baseDN = AonDN.getUserSignaturesDN(domain, user);
		dao.setBaseDN( baseDN.toString() );
		return dao;
	}

    public static MailAccount getDefaultAccount( String domain, String user ) throws ManagerBeanException {
    	LdapDAO dao = getMailAccountDAO(domain, user);
		IManagerBean beanAccount = new BasicManagerBean(dao);
		Criteria criteriaAccount = new Criteria();
		criteriaAccount.addEqualExpression(beanAccount.getFieldName(IWebMailAlias.MAIL_ACCOUNT_NAME), MailAccount.DEFAULT_MAIL_ACCOUNT_NAME);
		Iterator<ITransferObject> iterAccount = beanAccount.getList(criteriaAccount).iterator();
		if (iterAccount.hasNext()){
			MailAccount mailAccount = (MailAccount)iterAccount.next();
			return mailAccount;
		}
		return null;
    }

	
}
