package com.code.aon.ui.document.controller;


import static com.code.aon.ui.company.controller.ICompanyConstants.ENTERPRISE_CONTROLLER_NAME;
import static com.code.aon.ui.registry.controller.IRegistryConstants.DOCUMENT_MANAGER_CONTROLLER_NAME;
import static com.code.aon.ui.webmail.controller.IWebMailConstants.BEAN_MAIL_ACCOUNT;
import static com.code.aon.ui.webmail.controller.IWebMailConstants.BEAN_SIGNATURE;

import java.security.Principal;
import java.util.LinkedList;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.naming.Name;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.bridge.session.LoggedUser;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.company.Enterprise;
import com.code.aon.company.EnterpriseUser;
import com.code.aon.jaas.auth.AuthPrincipal;
import com.code.aon.ldap.NameResolver;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.company.controller.EnterpriseController;
import com.code.aon.ui.document.event.EnterpriseProjectListener;
import com.code.aon.ui.form.event.IControllerListener;
import com.code.aon.ui.registry.controller.DocumentManager;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.ui.webmail.controller.LdapBasicController;
import com.esferalia.aon.entity.IEntityAlias;

public class ManagerController implements IEnterpriseController {
	
	private final static Logger LOGGER = LoggerFactory.getLogger(ManagerController.class);
	
	public static final String CONTROLLER_NAME = "manager";

	private AuthPrincipal principal;
	
	private EnterpriseUser loggedUser;
	
	private IControllerListener projectListener;
	
	private String homeTemplate = "/homepage.xhtml";
	
	public ManagerController() {
		this.principal = resolvePrincipal();
		this.loggedUser = resolveUser();
		if ( isMainEnterprise() ) {
			initWebmail(this.principal.getDomain());
		} else {
			initEnterprise();
		}
		this.projectListener = new EnterpriseProjectListener(this, ! isMainEnterprise());		
		LoggedUser lu = (LoggedUser) AonUtil.getRegisteredBean(LoggedUser.LOGGED_USER);
		lu.setCompanyName(loggedUser.getEnterprise().getRegistry().getFullName());
		DocumentManager dm = (DocumentManager) AonUtil.getRegisteredBean(DOCUMENT_MANAGER_CONTROLLER_NAME);
		dm.setShow(false);
	}
	
	public AuthPrincipal getPrincipal() {
		return principal;
	}

	public EnterpriseUser getLoggedUser() {
		return loggedUser;
	}

	private AuthPrincipal resolvePrincipal() {
		AuthPrincipal user = null;
		Principal principal = FacesContext.getCurrentInstance().getExternalContext().getUserPrincipal();
		if ( principal instanceof AuthPrincipal ) {
			user = (AuthPrincipal) principal;
		} else {
			user = new AuthPrincipal( principal.getName() );
		}
		return user;
	}
	
	private EnterpriseUser resolveUser() {
		try {
            IManagerBean bean = BeanManager.getManagerBean(EnterpriseUser.class);
            Criteria criteria = new Criteria();
            criteria.addEqualExpression( bean.getFieldName("EnterpriseUser_login"), getPrincipal().getShortName() );
            List<ITransferObject> list = bean.getList(criteria);
            if (! list.isEmpty() ) {
                return (EnterpriseUser) list.get(0);
            } else {
            	String message = "El usuario no existe";
            	LOGGER.error(message);
    			AonUtil.addErrorMessage(message);
    			throw new AbortProcessingException(message);
            }
        } catch (ManagerBeanException e) {
        	LOGGER.error( "Error obtaining the USER related with the logged user: " + getPrincipal(), e);
        }
        return null;		
	}
	
	public String getHomeTemplate() {
		return homeTemplate;
	}
	
	private void initEnterprise() {
		EnterpriseController controller = (EnterpriseController) AonUtil.getRegisteredBean(ENTERPRISE_CONTROLLER_NAME);
		List<Expression> initExpressions = new LinkedList<Expression>();
		try {
			String enterpriseId = controller.getFieldName(IEntityAlias.ENTERPRISE_ID);
			Expression expr = ExpressionUtilities.getEqualExpression(enterpriseId, this.loggedUser.getEnterprise().getId());
			initExpressions.add(expr);
			controller.setInitExpressions(initExpressions);
		} catch (ManagerBeanException e) {
			LOGGER.error(">>>> initEnterprise exception ",e);
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		}
	}	
		
	public boolean isMainEnterprise() {
		return (this.loggedUser.getRegistry() == null);
	}

	@Override
	public Enterprise getEnterprise() {
		return this.loggedUser.getEnterprise();
	}

	public IControllerListener getProjectListener() {
		return projectListener;
	}
	
	public void initWebmail( String domain) {
		LdapBasicController mailAccount = (LdapBasicController) AonUtil.getRegisteredBean(BEAN_MAIL_ACCOUNT);
		Name dn = NameResolver.getDomainDN(domain);
		mailAccount.updateBaseDN(dn);
		mailAccount.onSearch(null);
		LdapBasicController signature = (LdapBasicController) AonUtil.getRegisteredBean(BEAN_SIGNATURE);
		signature.updateBaseDN(dn);
		signature.onSearch(null);	
	}
	
}