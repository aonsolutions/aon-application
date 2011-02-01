package com.code.aon.webmail;

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
		dao.setBaseDN( baseDN );
		return dao;
	}

    public static MailAccount getDefaultAccount( String domain, String user ) throws ManagerBeanException {
    	return getDefaultAccount(domain, user, false);
    }

    public static MailAccount getDefaultAccount( String domain, String user, boolean first ) throws ManagerBeanException {
    	LdapDAO dao = getMailAccountDAO(domain, user);
		IManagerBean beanAccount = new BasicManagerBean(dao);
		Criteria criteriaAccount = new Criteria();
		criteriaAccount.addEqualExpression(beanAccount.getFieldName(IWebMailAlias.MAIL_ACCOUNT_NAME), MailAccount.DEFAULT_MAIL_ACCOUNT_NAME);
		List<ITransferObject> list = beanAccount.getList(criteriaAccount);
		if (! list.isEmpty() ) {
			return (MailAccount) list.get(0);
		}
		if ( first ) {
			Criteria criteria = new Criteria();
			criteria.addOrder( beanAccount.getFieldName(IWebMailAlias.MAIL_ACCOUNT_NAME) );
			List<ITransferObject> fullList = beanAccount.getList(criteria);
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
