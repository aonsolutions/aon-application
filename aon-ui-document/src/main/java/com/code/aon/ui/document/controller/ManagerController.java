package com.code.aon.ui.document.controller;


import static com.code.aon.document.BasicAlfresco.SERVER_ADMIN_PASSWORD;
import static com.code.aon.document.BasicAlfresco.SERVER_ADMIN_USER;
import static com.code.aon.ui.common.ICommonConstants.LOGGED_USER_CONTROLLER_NAME;
import static com.code.aon.ui.company.controller.ICompanyConstants.ENTERPRISE_CONTROLLER_NAME;
import static com.code.aon.ui.registry.controller.IRegistryConstants.DOCUMENT_MANAGER_CONTROLLER_NAME;
import static com.code.aon.ui.webmail.controller.IWebMailConstants.BEAN_MAIL_ACCOUNT_DB;
import static com.code.aon.ui.webmail.controller.IWebMailConstants.BEAN_MAIL_CONFIG;
import static com.code.aon.ui.webmail.controller.IWebMailConstants.BEAN_SIGNATURE_DB;

import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import javax.faces.event.AbortProcessingException;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.sql.DAOException;
import com.code.aon.company.Company;
import com.code.aon.company.Enterprise;
import com.code.aon.config.User;
import com.code.aon.document.AlfrescoGroup;
import com.code.aon.document.AlfrescoUserManager;
import com.code.aon.document.BasicAlfresco;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.common.controller.LoggedUser;
import com.code.aon.ui.company.controller.EnterpriseController;
import com.code.aon.ui.config.util.UserUtils;
import com.code.aon.ui.document.event.EnterpriseProjectListener;
import com.code.aon.ui.form.event.IControllerListener;
import com.code.aon.ui.registry.controller.DocumentManager;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.ui.webmail.controller.MailAccountDBController;
import com.code.aon.ui.webmail.controller.MailConfigController;
import com.code.aon.ui.webmail.controller.SignatureDBController;
import com.esferalia.aon.entity.IEntityAlias;

public class ManagerController implements IEnterpriseController {
	
	private final static Logger LOGGER = LoggerFactory.getLogger(ManagerController.class);
	
	public static final String CONTROLLER_NAME = "manager";

	private String alfrescoUser;
	
	private String alfrescoPassword;
	
	private Enterprise enterprise;
	
	private Enterprise parentEnterprise;
	
	private IControllerListener projectListener;
	
	private String homeTemplate = "/homepage.xhtml";
	
	private AlfrescoUserManager userManager;
	
	private boolean administrator;
	
	private List<SelectItem> userScopes;
	
	public ManagerController() {
		this.parentEnterprise = resolveParentEnterprise();
		User user = UserUtils.getInstance().getLoggedUser();		
		initAlfrescoUser( user );
		this.enterprise = resolveEnterprise( user );
		if ( this.enterprise == null ) {
			this.enterprise = this.parentEnterprise;
		}
		if ( isMainEnterprise() ) {
			initWebmail();
		} else {
			initEnterprise();
		}
		try {
			this.userManager = new AlfrescoUserManager(getAlfrescoUser(), getAlfrescoPassword());
			this.administrator = this.userManager.isAlfrescoAdministrator(getAlfrescoUser());			
			loadUserScopes();
		} catch (DAOException e) {
			LOGGER.error( e.getMessage(), e );
		}
		this.projectListener = new EnterpriseProjectListener(this, ! isMainEnterprise());
		if ( AonUtil.isSkipLdap() ) {
			LoggedUser lu = (LoggedUser) AonUtil.getRegisteredBean(LOGGED_USER_CONTROLLER_NAME);
			lu.setCompanyName(enterprise.getRegistry().getFullName());
			DocumentManager dm = (DocumentManager) AonUtil.getRegisteredBean(DOCUMENT_MANAGER_CONTROLLER_NAME);
			dm.setShow(false);
		}
	}
	
	public boolean isAlfrescoReady() {
		return this.userManager != null;
	}

	public boolean isAlfrescoManagementEnabled() {
		return AonUtil.isSkipLdap();
	}
	
	public boolean isAdministrator() {
		return administrator;
	}

	public AlfrescoUserManager getUserManager() {
		return userManager;
	}
	
	public String getAlfrescoUser() {
		return alfrescoUser;
	}

	public String getAlfrescoPassword() {
		return alfrescoPassword;
	}
	
	private Enterprise resolveEnterprise( User user ) {
		try {
            IManagerBean bean = BeanManager.getManagerBean(Enterprise.class);
            return (Enterprise) bean.get(user.getEnterprise());
        } catch (ManagerBeanException e) {
        	LOGGER.error( "Error obtaining enterpise of the logged user: " + user, e);
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
			Expression expr = ExpressionUtilities.getEqualExpression(enterpriseId, this.enterprise.getId());
			initExpressions.add(expr);
			controller.setInitExpressions(initExpressions);
		} catch (ManagerBeanException e) {
			LOGGER.error(">>>> initEnterprise exception ",e);
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		}
	}	
		
	public boolean isMainEnterprise() {
		return ObjectUtils.equals(this.enterprise, this.parentEnterprise);
	}

	@Override
	public Enterprise getEnterprise() {
		return this.enterprise;
	}

	public IControllerListener getProjectListener() {
		return projectListener;
	}
	
	public void initWebmail() {
		if ( AonUtil.isSkipLdap() ) {
			SignatureDBController signature = (SignatureDBController) AonUtil.getRegisteredBean(BEAN_SIGNATURE_DB);
			MailAccountDBController account = (MailAccountDBController) AonUtil.getRegisteredBean(BEAN_MAIL_ACCOUNT_DB);
			MailConfigController mailConfig = (MailConfigController) AonUtil.getRegisteredBean(BEAN_MAIL_CONFIG);
			try {
				signature.updateUser(null);
				mailConfig.setSignature(signature);
				account.updateUser(null);
				mailConfig.setMailAccount(account);
			} catch (ManagerBeanException e) {
				LOGGER.error(">>>> initWebmail exception ",e);
				AonUtil.addErrorMessage(e.getMessage());
				throw new AbortProcessingException(e.getMessage(), e);
			}			
		}
	}

	public Enterprise resolveParentEnterprise() {
		try {
			IManagerBean companyBean = BeanManager.getManagerBean(Company.class);
			List<ITransferObject> tos = companyBean.getList(null);
			if (! tos.isEmpty() ) {
				Company company = (Company) tos.get(0);
				IManagerBean enterpriseBean = BeanManager.getManagerBean(Enterprise.class);
				return (Enterprise) enterpriseBean.get(company.getId());
			}
		} catch (ManagerBeanException e) {
			LOGGER.error(">>>> resolveParentEnterprise",e);
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		}
		return null;
	}

	public void initAlfrescoUser( User user ) {
		if ( AonUtil.isSkipLdap() ) {
			this.alfrescoUser = user.getLogin();
			this.alfrescoPassword = user.getPassword();
		} else {
			Properties properties = BasicAlfresco.getAlfrescoProperties();
			this.alfrescoUser = properties.getProperty(SERVER_ADMIN_USER, "admin");
			this.alfrescoPassword = properties.getProperty(SERVER_ADMIN_PASSWORD, "admin");
		}
	}	
	
	public Enterprise getParentEnterprise() {
		return parentEnterprise;
	}

	public List<SelectItem> getUserScopes() {
		return userScopes;
	}

	private void loadUserScopes() throws DAOException {
		this.userScopes = new LinkedList<SelectItem>();
		for( String scope : getUserManager().getUserScopes() ) {
			SelectItem item = new SelectItem(new AlfrescoGroup(scope), scope);
			this.userScopes.add(item);
		}
	}
	
	public boolean isShowScopes() {
		return ! this.userScopes.isEmpty();
	}
	
}