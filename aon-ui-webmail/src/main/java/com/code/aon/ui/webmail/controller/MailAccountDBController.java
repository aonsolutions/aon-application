package com.code.aon.ui.webmail.controller;

import static com.code.aon.ui.webmail.controller.IWebMailConstants.BEAN_WEBMAIL;
import static com.code.aon.ui.webmail.controller.IWebMailConstants.BUNDLE_NAME;
import static com.code.aon.ui.webmail.controller.IWebMailConstants.MAIL_ACCOUNT_DUPLICATED;
import static com.code.aon.ui.webmail.controller.IWebMailConstants.SHOW_DOMAIN_MAIL_ACCOUNTS_PROPERTY;

import java.util.LinkedList;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
import com.code.aon.webmail.db.MailAccount;
import com.esferalia.aon.entity.IEntityAlias;

public class MailAccountDBController extends MailDBController implements IMailAccountController {

	private final static Logger LOGGER = LoggerFactory.getLogger(MailAccountDBController.class);
	
	@Override
	protected String getDuplicatedMessage( String name ) {
		return AonUtil.getMessage(BUNDLE_NAME, MAIL_ACCOUNT_DUPLICATED, name);
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
		List<SelectItem> accounts = new LinkedList<SelectItem>();
		try {		
    		Criteria criteria = new Criteria();
    		User user = UserUtils.getInstance().getLoggedUser();
			Expression userExp = ExpressionUtilities.getEqualExpression("MailAccount.user<id", user.getId());
			if ( AonUtil.isBeanValue(BEAN_WEBMAIL, SHOW_DOMAIN_MAIL_ACCOUNTS_PROPERTY) ) {
				Expression exp = getExpression(null);
				userExp = ExpressionUtilities.getOrExpression(userExp, exp); 					
			}
			criteria.addExpression(userExp);
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
	
}
