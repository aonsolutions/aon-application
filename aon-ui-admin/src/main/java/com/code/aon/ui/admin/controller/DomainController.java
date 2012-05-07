package com.code.aon.ui.admin.controller;

import static com.code.aon.ui.admin.controller.IAdminConstants.ADMIN_CONTROLLER_NAME;
import static com.code.aon.ui.webmail.controller.IWebMailConstants.BEAN_CONTACT_DB;
import static com.code.aon.ui.webmail.controller.IWebMailConstants.BEAN_MAIL_ACCOUNT_DB;
import static com.code.aon.ui.webmail.controller.IWebMailConstants.BEAN_SIGNATURE_DB;

import java.util.LinkedList;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.bridge.jmx.mbean.IConsoleAdmin;
import com.code.aon.bridge.plugin.UserManager;
import com.code.aon.bridge.plugin.Utils;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.Domain;
import com.code.aon.config.User;
import com.code.aon.config.enumeration.DomainType;
import com.code.aon.jaas.auth.AuthPrincipal;
import com.code.aon.jaas.deployment.DeploymentException;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.admin.UserType;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.ui.webmail.controller.ContactDBController;
import com.code.aon.ui.webmail.controller.MailAccountDBController;
import com.code.aon.ui.webmail.controller.SignatureDBController;
import com.esferalia.aon.entity.IEntityAlias;

public class DomainController extends BasicController {

	private final static Logger LOGGER = LoggerFactory.getLogger(DomainController.class);
	
	public final static int DEFAULT_MAX_DOCUMENT_SIZE = 1;
	
	public final static int DEFAULT_MAX_TOTAL_DOCUMENT_SIZE = 100;	
	
	private List<SelectItem> parentDomains;
	
	private boolean showCompanyWindow;
	
	private String selectedTab;
	
	private AdminMainController getAdmin() {
		return (AdminMainController) AonUtil.getRegisteredBean(ADMIN_CONTROLLER_NAME);
	}

	public String getSelectedTab() {
		return selectedTab;
	}

	public void setSelectedTab(String selectedTab) {
		this.selectedTab = selectedTab;
	}	
	
	public Domain getDomain() {
		return (Domain) getTo();
	}	

	public boolean isDeletable() {
		if (! ObjectUtils.equals(getDomain(), getAdmin().getCurrentDomain())  ) {
			return getAdmin().getUserType() != UserType.NORMAL;
		}
		return false;
	}
	
	public boolean isChildDomain() {
		DomainType type = getDomain().getType();
		return (type == null) || (type == DomainType.ENTERPRISE);
	}

	public boolean isShowDomainSubDomainSuffix() {
		if (! isChildDomain()  ) {
			if ( getDomain().isDomainManagement() || getAdmin().isSysAdmin() ) {
				return true;
			}
		}
		return false;
	}
	
	public boolean isShowCompanyWindow() {
		return showCompanyWindow;
	}

	public void setShowCompanyWindow(boolean showCompanyWindow) {
		this.showCompanyWindow = showCompanyWindow;
	}	

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void updateParentDomains() {
		this.parentDomains = new LinkedList<SelectItem>();
		try {
			Criteria criteria = new Criteria();
			String domainManagement = getFieldName(IEntityAlias.DOMAIN_DOMAIN_MANAGEMENT);
			Expression expr1 = ExpressionUtilities.getEqualExpression(domainManagement, Boolean.TRUE);
			String type = getFieldName(IEntityAlias.DOMAIN_TYPE);
			Expression expr2 = ExpressionUtilities.getEqualExpression(type, DomainType.CONSULTANCY);
			criteria.addExpression(ExpressionUtilities.getOrExpression(expr1, expr2));
			List<Domain> list = (List) getManagerBean().getList(criteria);
			for (Domain domain : list) {
				SelectItem item = new SelectItem(domain, domain.getName() );
				this.parentDomains.add(item);
			}
		} catch (ManagerBeanException e) {
			LOGGER.error(e.getMessage(), e);
		}
	}	
		
	public List<SelectItem> getAllParentDomains() {
		return parentDomains;
	}

	public List<SelectItem> getParentDomains() {
		List<SelectItem> list = new LinkedList<SelectItem>();
		for( SelectItem item : parentDomains ) {
			if (! getDomain().equals(item.getValue()) ) {
				list.add(item);
			}
		}
		return list;
	}

	public void onBackToDomain( ActionEvent event ) {
		try {
			initWebmail( null );
		} catch (ManagerBeanException e) {
			LOGGER.error(e.getMessage(), e);
		}
	}
	
	public void initWebmail( User user ) throws ManagerBeanException {
		SignatureDBController signature = (SignatureDBController) AonUtil.getRegisteredBean(BEAN_SIGNATURE_DB);
		signature.updateUser(user);
		signature.initializeModel();

		MailAccountDBController account = (MailAccountDBController) AonUtil.getRegisteredBean(BEAN_MAIL_ACCOUNT_DB);
		account.updateUser(user);
		account.initializeModel();		

		ContactDBController contact = (ContactDBController) AonUtil.getRegisteredBean(BEAN_CONTACT_DB);
		contact.updateUser(user);
		contact.initializeModel();		
	}

	public void flushAuthenticationCache( User user ) {
		try {
			IConsoleAdmin console = Utils.getSecurityConsole();
			AuthPrincipal principal = new AuthPrincipal( user.getLogin() + "@" + getDomain().getName() );			
			console.flushAuthenticationCache(UserManager.LDAP_SECURITY_DOMAIN, principal);
		} catch (DeploymentException e) {
			LOGGER.error( "Error flushing authenticaction cache for " + user, e );
		}
	}
	
}