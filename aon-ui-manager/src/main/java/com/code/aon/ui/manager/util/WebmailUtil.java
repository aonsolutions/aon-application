package com.code.aon.ui.manager.util;

import static com.code.aon.ldap.IAonObjectClasses.ORGANIZATIONAL_UNIT;
import static com.code.aon.webmail.IMailAccount.DEFAULT_MAIL_ACCOUNT_NAME;
import static com.code.aon.webmail.dao.IWebMailAlias.MAIL_ACCOUNT_NAME;

import java.util.List;

import javax.naming.Name;

import com.code.aon.common.BasicManagerBean;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.dao.ldap.LdapDAO;
import com.code.aon.ldap.NameResolver;
import com.code.aon.manager.MailAccount;
import com.code.aon.ql.Criteria;
import com.code.aon.webmail.IMailAccount;
import com.code.aon.webmail.dao.IWebMailAlias;

public class WebmailUtil {

	public static LdapDAO getMailAccountDAO( String domain, String user ) {
		LdapDAO dao = new LdapDAO(MailAccount.class);
		Name baseDN = NameResolver.getUserAccountsDN(domain, user);
		if ( dao.exists(baseDN, ORGANIZATIONAL_UNIT)) {
			dao.setBaseDN( baseDN );
			return dao;			
		}
		return null;
	}

	public static LdapDAO getMailAccountDAO( String domain ) {
		LdapDAO dao = new LdapDAO(MailAccount.class);
		Name baseDN = NameResolver.getDomainAccountsDN(domain);
		if ( dao.exists(baseDN, ORGANIZATIONAL_UNIT)) {
			dao.setBaseDN( baseDN );
			return dao;			
		}
		return null;
	}
	
    public static IMailAccount getDefaultAccount( String domain, String user ) throws ManagerBeanException {
    	return getDefaultAccount(domain, user, false);
    }

    public static IMailAccount getDefaultAccount( String domain, String user, boolean first ) throws ManagerBeanException {
    	LdapDAO dao = getMailAccountDAO(domain, user);
    	if ( dao != null ) {
    		return getDefaultAccount(dao, first);
    	}
		return null;
    }    
   
    public static IMailAccount getDefaultAccount( String domain ) throws ManagerBeanException {
    	return getDefaultAccount(domain, false);
    }

    public static IMailAccount getDefaultAccount( String domain, boolean first ) throws ManagerBeanException {
    	LdapDAO dao = getMailAccountDAO(domain);
    	if ( dao != null ) {
    		return getDefaultAccount(dao, first);
    	}
		return null;
    }        
    
    private static IMailAccount getDefaultAccount( LdapDAO dao, boolean first ) throws ManagerBeanException {
		IManagerBean bean = new BasicManagerBean(dao);
		Criteria defaultCriteria = new Criteria();
		defaultCriteria.addEqualExpression(bean.getFieldName(IWebMailAlias.MAIL_ACCOUNT_DEFAULT_ACCOUNT), Boolean.TRUE);
		List<ITransferObject> defautList = bean.getList(defaultCriteria);
		if (! defautList.isEmpty() ) {
			return (MailAccount) defautList.get(0);
		}
		Criteria systemCriteria = new Criteria();
		systemCriteria.addEqualExpression(bean.getFieldName(MAIL_ACCOUNT_NAME), DEFAULT_MAIL_ACCOUNT_NAME);
		List<ITransferObject> list = bean.getList(systemCriteria);
		if (! list.isEmpty() ) {
			MailAccount account = (MailAccount) list.get(0);
			account.setDefaultAccount(true);
			bean.update(account);
			return account;
		}
		if ( first ) {
			Criteria criteria = new Criteria();
			criteria.addOrder( bean.getFieldName(MAIL_ACCOUNT_NAME) );
			List<ITransferObject> fullList = bean.getList(criteria);
			if (! fullList.isEmpty() ) {
				return (MailAccount) fullList.get(0);
			}
		}
		return null;
    }      
    
    public static IMailAccount getMailAccount( String domain, String user, boolean checkDomainAccounts ) throws ManagerBeanException {
		IMailAccount mailAccount = WebmailUtil.getDefaultAccount(domain, user, true);
		if ( (mailAccount == null) && checkDomainAccounts ) {
			mailAccount = WebmailUtil.getDefaultAccount(domain, true);
		}
		return mailAccount;
    }
    
}

