package com.esferalia.aon.web.employee.controller;

import java.util.List;

import javax.faces.event.AbortProcessingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.company.EnterpriseUser;
import com.code.aon.jaas.auth.AuthPrincipal;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.common.ICommonConstants;
import com.code.aon.ui.common.controller.LoggedUser;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.ui.payroll.controller.IPayrollConstants;

public class ManagerController implements IPayrollConstants {
	
	private static final String WORKER_TEMPLATE = "/com/esferalia/aon/ui/employee/facelet/salary/list.xhtml";

	private static final String ENTERPRISE_TEMPLATE = "/com/esferalia/aon/ui/employee/facelet/enterpriseTree/formTree.xhtml";

	private static final Logger LOGGER = LoggerFactory.getLogger(ManagerController.class);
	
	public static final String CONTROLLER_NAME = "manager";
	
	private EnterpriseUser loggedUser;
	
	public ManagerController() {
		this.loggedUser = resolveUser();
		LoggedUser lu = (LoggedUser) AonUtil.getRegisteredBean(ICommonConstants.LOGGED_USER_CONTROLLER_NAME);
		if ( isWorker() ) {
			lu.setLoggedUserName(loggedUser.getRegistry().getFullName());
		}
		lu.setCompanyName(loggedUser.getEnterprise().getRegistry().getFullName());
	}

	public EnterpriseUser getLoggedUser() {
		return loggedUser;
	}
	
	private boolean isWorker() {
		return this.loggedUser.getRegistry() != null;
	}
	
	private EnterpriseUser resolveUser() {
		EnterpriseUser user = null;
		AuthPrincipal principal = AonUtil.getAuthPrincipal();
		try {
            IManagerBean bean = BeanManager.getManagerBean(EnterpriseUser.class);
            if ( principal.getUserId() != null ) {
            	user = (EnterpriseUser) bean.get(principal.getUserId());
            } else {
                Criteria criteria = new Criteria();
                criteria.addEqualExpression( bean.getFieldName("EnterpriseUser_login"), principal.getShortName() );
                if ( principal.getDomainId() != null ) {
                	criteria.addEqualExpression( bean.getFieldName("EnterpriseUser_domain"), principal.getDomainId() );
                }
                List<ITransferObject> list = bean.getList(criteria);
                if (! list.isEmpty() ) {
                    user = (EnterpriseUser) list.get(0);
                }
            }
            if ( user == null ) {
                String message = "El usuario no existe";
            	LOGGER.error(message);
    			AonUtil.addErrorMessage(message);
    			throw new AbortProcessingException(message);
            }
        } catch (ManagerBeanException e) {
        	LOGGER.error( "Error obtaining the USER related with the logged user: " + principal, e);
        }
        return user;		
	}
	
	public String getHomeTemplate() {
		return isWorker() ? WORKER_TEMPLATE : ENTERPRISE_TEMPLATE;
	}
	
}
