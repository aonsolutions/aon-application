package com.esferalia.aon.web.employee.controller;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.faces.event.AbortProcessingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.util.BasicPrincipal;
import com.code.aon.company.EnterpriseUser;
import com.code.aon.jaas.auth.AuthPrincipal;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.common.ICommonConstants;
import com.code.aon.ui.common.controller.LoggedUser;
import com.code.aon.ui.company.controller.EnterpriseController;
import com.code.aon.ui.company.controller.ICompanyConstants;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.ui.calendar.controller.CalendarController;
import com.esferalia.aon.ui.calendar.controller.ICalendarConstants;
import com.esferalia.aon.ui.payroll.controller.IPayrollConstants;
import com.esferalia.aon.ui.payroll.controller.salary.SalaryController;
import com.esferalia.aon.ui.payroll.controller.salary.draft.SalaryDraftController;

public class ManagerController implements IPayrollConstants {
	
	private final static Logger LOGGER = LoggerFactory.getLogger(ManagerController.class);
	
	public static final String CONTROLLER_NAME = "manager";

	private AuthPrincipal principal;
	
	private EnterpriseUser loggedUser;
	
	private String homeTemplate;
	
	public ManagerController() {
		this.principal = BasicPrincipal.getAuthPrincipal();
		this.loggedUser = resolveUser();
		LoggedUser lu = (LoggedUser) AonUtil.getRegisteredBean(ICommonConstants.LOGGED_USER_CONTROLLER_NAME);
		if ( isEnterprise() ) {
			initEnterprise();
		} else {
			initWorker();
			lu.setLoggedUserName(loggedUser.getRegistry().getFullName());
		}
		lu.setCompanyName(loggedUser.getEnterprise().getRegistry().getFullName());
	}
	
	public AuthPrincipal getPrincipal() {
		return principal;
	}

	public EnterpriseUser getLoggedUser() {
		return loggedUser;
	}
	
	public boolean isEnterprise() {
		return this.loggedUser.getRegistry() == null;
	}
	
	private EnterpriseUser resolveUser() {
		EnterpriseUser user = null;
		try {
            IManagerBean bean = BeanManager.getManagerBean(EnterpriseUser.class);
            if ( getPrincipal().getUserId() != null ) {
            	user = (EnterpriseUser) bean.get(getPrincipal().getUserId());
            } else {
                Criteria criteria = new Criteria();
                criteria.addEqualExpression( bean.getFieldName("EnterpriseUser_login"), getPrincipal().getShortName() );
                if ( getPrincipal().getDomainId() != null ) {
                	criteria.addEqualExpression( bean.getFieldName("EnterpriseUser_domain"), getPrincipal().getDomainId() );
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
        	LOGGER.error( "Error obtaining the USER related with the logged user: " + getPrincipal(), e);
        }
        return user;		
	}
	
	public String getHomeTemplate() {
		return homeTemplate;
	}
	
	private void initEnterpriseTree() {
		EnterpriseController controller = (EnterpriseController) AonUtil.getRegisteredBean(ICompanyConstants.ENTERPRISE_CONTROLLER_NAME);
		controller.onTreeViewSelect(null);
		try {
			controller.select(null, this.loggedUser.getEnterprise());
		} catch (ManagerBeanException e) {
			LOGGER.error(">>>> initEnterpriseTree exception ",e);
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		}				
	}

	private void initSalaryDraft() {
		SalaryDraftController controller = (SalaryDraftController) AonUtil.getRegisteredBean(IPayrollConstants.SALARY_DRAFT_CONTROLLER);
		List<Expression> initExpressions = new LinkedList<Expression>();
		try {
			String enterpriseId = controller.getFieldName(IEntityAlias.CONTRACT_WORK_PLACE_ENTERPRISE_ID);
			Expression expr = ExpressionUtilities.getEqualExpression(enterpriseId, this.loggedUser.getEnterprise().getId());
			initExpressions.add(expr);
			controller.setInitExpressions(initExpressions);
		} catch (ManagerBeanException e) {
			LOGGER.error(">>>> initSalaryDraft exception ",e);
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		}
	}
	
	private void initEnterprise() {
		//this.homeTemplate = "/com/esferalia/aon/ui/payroll/facelet/enterpriseTree/formTree.xhtml";
		this.homeTemplate = "/com/esferalia/aon/ui/employee/facelet/enterpriseTree/formTree.xhtml";
		initEnterpriseTree();
		initSalaryDraft();
	}
	
	private Contract getContract() {
		try {		
			IManagerBean bean = BeanManager.getManagerBean(Contract.class);		
			Criteria criteria = new Criteria();
			String endDate = bean.getFieldName(IEntityAlias.CONTRACT_END_DATE);
			Expression expr1 = ExpressionUtilities.getGreaterThanOrEqualExpression(endDate, new Date());
			Expression expr2 = ExpressionUtilities.getNullExpression(endDate);
			criteria.addExpression(ExpressionUtilities.getOrExpression(expr1, expr2));
			String personId = bean.getFieldName(IEntityAlias.CONTRACT_PERSON_ID);
			criteria.addEqualExpression(personId, this.loggedUser.getRegistry().getId());
			String enterpriseId = bean.getFieldName(IEntityAlias.CONTRACT_WORK_PLACE_ENTERPRISE_ID);
			criteria.addEqualExpression(enterpriseId, this.loggedUser.getEnterprise().getId());
			List<ITransferObject> list = bean.getList(criteria);
			if (! list.isEmpty() ) {
				return (Contract) list.get(0);
			}			
		} catch (ManagerBeanException e) {
			LOGGER.error(">>>> getContract exception ",e);
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		}					
		return null;
	}
	
	private void initWorkerSalaries( Contract contract ) {
		SalaryController controller = (SalaryController) AonUtil.getRegisteredBean(IPayrollConstants.SALARY_CONTROLLER);
		try {		
			Criteria criteria = controller.getCriteria();
			String contractId = controller.getFieldName(IEntityAlias.SALARY_CONTRACT_ID);
			criteria.addEqualExpression( contractId, contract.getId() );			
			controller.onSearch(null);
		} catch (ManagerBeanException e) {
			LOGGER.error(">>>> initWorkerSalaries exception ",e);
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		}			
	}

	private void initWorkerCalendar( Contract contract ) {
		CalendarController controller = (CalendarController) AonUtil.getRegisteredBean(ICalendarConstants.CALENDAR_CONTROLLER_NAME);
		// TODO cargar el id de calendar, no el de contract
//		controller.setSource(CalendarSource.CONTRACT);
//		controller.setSourceId( contract.getId() );
		controller.onInitialize(null);
		// TODO ¿¿porque esta esto aqui??
//		CalendarHolidayDataController chdc = (CalendarHolidayDataController) AonUtil.getRegisteredBean(CALENDAR_HOLIDAY_DATA_CONTROLLER_NAME);
//		chdc.getHolidayDataModels();
	}
	
	private void initWorker() {
		//this.homeTemplate = "/com/esferalia/aon/ui/payroll/facelet/salary/list.xhtml";
		this.homeTemplate = "/com/esferalia/aon/ui/employee/facelet/salary/list.xhtml";
		Contract contract = getContract();
		initWorkerSalaries( contract );
		initWorkerCalendar( contract );
		AonUtil.setBeanValue(SALARY_CONTROLLER, SHOW_PERSON_COLUMN, Boolean.FALSE);
	}
	
}
