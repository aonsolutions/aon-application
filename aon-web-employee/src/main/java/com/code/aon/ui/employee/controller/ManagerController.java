package com.code.aon.ui.employee.controller;

import java.security.Principal;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.company.EnterpriseUser;
import com.code.aon.employee.dao.IEmployeeAlias;
import com.code.aon.jaas.auth.AuthPrincipal;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.company.controller.EnterpriseController;
import com.code.aon.ui.company.controller.ICompanyConstants;
import com.code.aon.ui.util.AonUtil;

public class ManagerController implements IEmployeeConstants {
	
	private final static Logger LOGGER = LoggerFactory.getLogger(ManagerController.class);

	private AuthPrincipal principal;
	
	private EnterpriseUser loggedUser;
	
	private String homeTemplate;
	
	public ManagerController() {
		this.principal = resolvePrincipal();
		this.loggedUser = resolveUser();		
		if ( this.loggedUser.getRegistry() == null ) {
			initEnterprise();
		} else {
			initWorker();
		}
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
		this.homeTemplate = "/com/code/aon/ui/company/facelet/enterprise/formTree.xhtml";
		EnterpriseController controller = (EnterpriseController) AonUtil.getRegisteredBean(ICompanyConstants.ENTERPRISE_CONTROLLER_NAME);
		controller.onTreeViewSelect(null);
		try {
			controller.select(null, this.loggedUser.getEnterprise());
		} catch (ManagerBeanException e) {
			LOGGER.error(">>>> initEnterprise exception ",e);
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		}		
	}

	private void initWorker() {
		this.homeTemplate = "/com/code/aon/ui/employee/facelet/salary/list.xhtml";
		SalaryController controller = (SalaryController) AonUtil.getRegisteredBean(IEmployeeConstants.SALARY_CONTROLLER);
		try {		
			Criteria criteria = controller.getCriteria();
			String personAlias = controller.getFieldName(IEmployeeAlias.SALARY_CONTRACT_PERSON_ID);
			criteria.addEqualExpression( personAlias, loggedUser.getRegistry().getId() );			
			String enterpriseAlias = controller.getFieldName(IEmployeeAlias.SALARY_CONTRACT_WORK_PLACE_ENTERPRISE_ID);
			criteria.addEqualExpression( enterpriseAlias, loggedUser.getEnterprise().getId() );			
			controller.onSearch(null);
		} catch (ManagerBeanException e) {
			LOGGER.error(">>>> initWorker exception ",e);
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		}		
	}
	
}
