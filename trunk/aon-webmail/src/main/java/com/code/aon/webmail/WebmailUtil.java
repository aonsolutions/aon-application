package com.code.aon.webmail;

import static com.code.aon.ldap.IAonObjectClasses.ORGANIZATIONAL_UNIT;
import static com.code.aon.webmail.MailAccount.DEFAULT_MAIL_ACCOUNT_NAME;
import static com.code.aon.webmail.dao.IWebMailAlias.MAIL_ACCOUNT_NAME;

import java.util.List;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.Part;
import javax.mail.internet.MimeBodyPart;
import javax.naming.Name;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.ArrayUtils;

import com.code.aon.common.BasicManagerBean;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.util.AonFile;
import com.code.aon.dao.ldap.LdapDAO;
import com.code.aon.ldap.NameResolver;
import com.code.aon.ql.Criteria;
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
	
    public static MailAccount getDefaultAccount( String domain, String user ) throws ManagerBeanException {
    	return getDefaultAccount(domain, user, false);
    }

    public static MailAccount getDefaultAccount( String domain, String user, boolean first ) throws ManagerBeanException {
    	LdapDAO dao = getMailAccountDAO(domain, user);
    	if ( dao != null ) {
    		return getDefaultAccount(dao, first);
    	}
		return null;
    }    
   
    public static MailAccount getDefaultAccount( String domain ) throws ManagerBeanException {
    	return getDefaultAccount(domain, false);
    }

    public static MailAccount getDefaultAccount( String domain, boolean first ) throws ManagerBeanException {
    	LdapDAO dao = getMailAccountDAO(domain);
    	if ( dao != null ) {
    		return getDefaultAccount(dao, first);
    	}
		return null;
    }        
    
    private static MailAccount getDefaultAccount( LdapDAO dao, boolean first ) throws ManagerBeanException {
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

    public static BodyPart getBodyPart( AonFile af ) throws MessagingException {
    	MimeBodyPart bodyPart = new MimeBodyPart();
    	FileDataSource fds = new AonFileDataSource(af);
		String name = FilenameUtils.getName(af.getFileName());
    	bodyPart.setFileName( name );
    	bodyPart.setDataHandler(new DataHandler(fds));
    	return bodyPart;	
    }

    public static String getContentId( Part part ) throws MessagingException {
    	String[] contentId = part.getHeader("Content-ID");
    	if (! ArrayUtils.isEmpty(contentId) ) {
    		return contentId[0];
    	}
    	return null;
    }
    
}

