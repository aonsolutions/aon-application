package com.code.aon.ui.webmail.controller;

import static com.code.aon.ui.common.ICommonMessages.MAIL_ACCOUNT_DUPLICATED;

import java.util.LinkedList;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.config.User;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.config.util.UserUtils;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.webmail.IMailAccount;
import com.code.aon.webmail.bean.IMailConstants;
import com.code.aon.webmail.db.MailAccount;
import com.code.aon.webmail.enumeration.ConnectionSecurity;
import com.code.aon.webmail.enumeration.MailAccountType;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.esferalia.aon.watson.util.Pair;

import software.amazon.awssdk.services.sesv2.model.GetEmailIdentityResponse;
import solutions.aon.aws.ses.SES;

public class MailAccountDBController extends MailDBController implements IMailAccountController {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private final static Logger LOGGER = LoggerFactory.getLogger(MailAccountDBController.class);
	
	
	private Pair<String,GetEmailIdentityResponse> emailIdentity;
	
	@Override
	protected String getDuplicatedMessage( String name ) {
		return AonUtil.getMessage(MAIL_ACCOUNT_DUPLICATED, name);
	}		
	
	@Override
	protected String getToName() {
		IMailAccount account = (IMailAccount) getTo();
		return account.getName();
	}

	@Override
	public void onReset(ActionEvent event) {
		super.onReset(event);
		MailAccount account = (MailAccount) getTo();
		account.setUser(getUser());
	}

	@Override
	public List<SelectItem> getMailAccounts() {
		return getMailAccounts(true);
	}

	public List<SelectItem> getMailAccounts( boolean filterType ) {
		List<SelectItem> accounts = new LinkedList<SelectItem>();
		try {		
    		Criteria criteria = new Criteria();
    		User user = UserUtils.getInstance().getLoggedUser();
			Expression userExp = ExpressionUtilities.getEqualExpression("MailAccount.user<id", user.getId());
			userExp = ExpressionUtilities.getOrExpression(userExp, getExpression(null)); 					
			criteria.addExpression(userExp);
			if ( filterType ) {
				criteria.addEqualExpression(getFieldName(IEntityAlias.MAIL_ACCOUNT_TYPE), MailAccountType.USER);	
			}
			if ( DomainManager.isParentDomainUserInChildDomain() ) {
	    		criteria.setSkipDomainFilter(true);
				String domainId = getFieldName(IEntityAlias.MAIL_ACCOUNT_DOMAIN);
				Expression exp1 = ExpressionUtilities.getEqualExpression(domainId, DomainManager.getCurrentDomain());
				Expression exp2 = ExpressionUtilities.getEqualExpression(domainId, user.getDomain());
				criteria.addExpression(ExpressionUtilities.getOrExpression(exp1, exp2));
			}		
			criteria.addOrder(getFieldName(IEntityAlias.MAIL_ACCOUNT_NAME));
			for( ITransferObject to : getManagerBean().getList(criteria) ) {
				MailAccount account = (MailAccount) to;
				SelectItem item = new SelectItem(account, account.getName());
				accounts.add(item);				
			}
		} catch (ManagerBeanException e) {
            LOGGER.error(">>>> getMailAccounts", e);
		}		
		return accounts;
	}
	
	@Override
	protected String getUserAlias() throws ManagerBeanException {
		return getFieldName( IEntityAlias.MAIL_ACCOUNT_USER_ID );
	}

	@Override
	protected String getNameAlias() throws ManagerBeanException {
		return getFieldName( IEntityAlias.MAIL_ACCOUNT_NAME );
	}

	public void onProtocolChanged( ActionEvent event ) {
		MailAccount mailAccount = (MailAccount) getTo();
		if (! isProtocolDefinied() ) {
			mailAccount.setIncomingHost(null);
			mailAccount.setIncomingPort(0);
			mailAccount.setIncomingSecurity(ConnectionSecurity.NONE);
		} else if ( mailAccount.getIncomingPort() == 0 ) {
			if ( mailAccount.isIMAP() ) {
				mailAccount.setIncomingPort(IMailConstants.DEFAULT_IMAP_PORT);
			} else {
				mailAccount.setIncomingPort(IMailConstants.DEFAULT_POP3_PORT);
			}
		}
		if (! mailAccount.isIMAP() ) {
			mailAccount.setDraftFolder(null);
			mailAccount.setSentFolder(null);
			mailAccount.setTrashFolder(null);
			mailAccount.setSpamFolder(null);
		}
	}
	boolean protocolAon;
	public void onProtocolAonChanged( ActionEvent event ) {
		MailAccount mailAccount = (MailAccount) getTo();
		mailAccount.setProtocol(protocolAon ? "aon" : null);
		mailAccount.setReplyToMail(protocolAon ? mailAccount.getEmail() : null);
	}
	
	boolean includeBcc;
	public void onIncludeBccChanged( ActionEvent event ) {
		MailAccount mailAccount = (MailAccount) getTo();
		mailAccount.setReplyToMail(includeBcc ? mailAccount.getEmail() : null);
	}

	public boolean isProtocolAon() {
		MailAccount mailAccount = (MailAccount) getTo();
		return isProtocolDefinied() && "aon".equalsIgnoreCase(mailAccount.getProtocol());
	}
	
	public boolean isProtocolAon(MailAccount mailAccount) {
		return isProtocolDefinied(mailAccount) && "aon".equalsIgnoreCase(mailAccount.getProtocol());
	}

	public void setProtocolAon(boolean aon) {
		protocolAon = aon;
		MailAccount mailAccount = (MailAccount) getTo();
		mailAccount.setProtocol(aon ? "aon" : null);
	}
	
	public boolean isIncludeBcc() {
		MailAccount mailAccount = (MailAccount) getTo();
		return mailAccount.getReplyToMail() != null;
	}
	
	public void setIncludeBcc(boolean bcc) {
		includeBcc = bcc;
		MailAccount mailAccount = (MailAccount) getTo();
		mailAccount.setReplyToMail(includeBcc ? mailAccount.getEmail() : null);
	}
	
	public boolean isProtocolDefinied() {
		MailAccount mailAccount = (MailAccount) getTo();
		return !StringUtils.isEmpty(mailAccount.getProtocol());
	}
	
	public boolean isProtocolDefinied(MailAccount mailAccount) {
		return !StringUtils.isEmpty(mailAccount.getProtocol());
	}

	public void updateEmailIdentity() {
		MailAccount mailAccount = (MailAccount) getTo();
		if ( mailAccount == null ) {
			this.emailIdentity  = null;
		} else {
			updateEmailIdentity(mailAccount.getEmail());
		}
	}
	
	public void updateEmailIdentity(String email) {
		if ( AonStringUtils.isBlank(email) ) {
			this.emailIdentity = null;
		} else if ( this.emailIdentity == null || !AonStringUtils.equalsIgnoreCase(emailIdentity.getKey(), email )){
			this.emailIdentity = new Pair<>( email, SES.getEmailIdentity(email));
		}
	}

	public boolean isVerifiedForSendingStatus() {
		updateEmailIdentity();
		return emailIdentity != null &&  emailIdentity.getValue() != null && emailIdentity.getValue().verifiedForSendingStatus();
	}
	
	public boolean isVerifiedForSendingStatus(String email) {
		updateEmailIdentity(email);
		return emailIdentity != null &&  emailIdentity.getValue() != null && emailIdentity.getValue().verifiedForSendingStatus();
	}
	
	public boolean isVerifiedForSendingStatus(MailAccount mailAccount) {
		return isVerifiedForSendingStatus(mailAccount.getEmail());
	}
}
