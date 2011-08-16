package com.esferalia.aon.ui.payroll.controller.salary;

import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.AonException;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.dao.sql.DAOException;
import com.code.aon.common.enumeration.Month;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.config.ApplicationParameter;
import com.code.aon.config.dao.IConfigAlias;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.payroll.ContractPayment;
import com.esferalia.aon.payroll.PaymentConcept;
import com.esferalia.aon.payroll.Salary;
import com.esferalia.aon.payroll.calculator.ContractSalaryCalculatorContext;
import com.esferalia.aon.payroll.dao.IPayrollAlias;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.calculator.ISalaryCalculatorContext;
import com.esferalia.aon.salary.enumeration.PaymentType;
import com.esferalia.aon.salary.enumeration.SalaryType;
import com.esferalia.aon.ui.payroll.controller.IPayrollConstants;
import com.esferalia.aon.ui.payroll.controller.PayrollAppParamsController;
import com.esferalia.aon.ui.payroll.controller.contract.ContractController;
import com.esferalia.aon.ui.payroll.controller.salary.draft.SalaryDraftController;

public class SettleController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(SettleController.class);
	
	private SettleParams params;
	
	private PaymentConcept noticeDayConcept;
	private PaymentConcept vacationConcept;
	private PaymentConcept compensationConcept;
	
	private Salary settle;
	
	public PaymentConcept getNoticeDayConcept() {
		return noticeDayConcept;
	}
	public void setNoticeDayConcept(PaymentConcept noticeDayConcept) {
		this.noticeDayConcept = noticeDayConcept;
	}
	public PaymentConcept getVacationConcept() {
		return vacationConcept;
	}
	public void setVacationConcept(PaymentConcept vacationConcept) {
		this.vacationConcept = vacationConcept;
	}
	public PaymentConcept getCompensationConcept() {
		return compensationConcept;
	}
	public void setCompensationConcept(PaymentConcept compensationConcept) {
		this.compensationConcept = compensationConcept;
	}
	public SettleParams getParams() {
		return params;
	}
	public void setParams(SettleParams params) {
		this.params = params;
	}
	
	public void onSelectContract(ActionEvent event){
		ContractController controller = (ContractController) FormUtil.getController(IPayrollConstants.CONTRACT_CONTROLLER);
		controller.onSelect(event);
		setParams(new SettleParams());
		getParams().setContract((Contract) controller.getTo());
		getParams().setSeniorityDate(((Contract) controller.getTo()).getSeniorityDate());
		initializeParams();
		initializeConcepts();
	}
	
	public void onGenerate(ActionEvent event){
		if (settle == null) {
			finalizeContract();
			boolean mustBeginTransaction = HibernateUtil.mustBeginTransaction();
			boolean mustCloseSession = HibernateUtil.mustCloseSession();
			String sessionName = HibernateUtil.getSessionFactoryName();
			try {
				try {
					HibernateUtil.setBeginTransaction(false);
					HibernateUtil.setCloseSession(false);
					HibernateUtil.beginTransaction(sessionName);
					// BEGIN operaciones de la transaccion
					saveSettlePayments();
					generateSettle();
					// FIN operaciones de la transaccion
					HibernateUtil.getSession(sessionName).flush();
					HibernateUtil.commitTransaction(sessionName);
				} catch (Exception e) {
					String msg = e.getMessage();
					try {
						HibernateUtil.rollbackTransaction(sessionName);
					} catch (DAOException daoe) {
						msg = "Unable to rollback transaction! (" + msg + ")";
					}
					AonUtil.addErrorMessage(msg);
					throw new AbortProcessingException(e);
				} finally {
					HibernateUtil.closeSession(sessionName);
				}
			} finally {
				HibernateUtil.setCloseSession(mustCloseSession);
				HibernateUtil.setBeginTransaction(mustBeginTransaction);
			}
		}
	}
	
	public void onSelectSettleDraft(ActionEvent event){
		try {
			SalaryDraftController controller = (SalaryDraftController) FormUtil.getController(IPayrollConstants.SALARY_DRAFT_CONTROLLER);
			controller.setSalaryType(SalaryType.SETTLE);
			controller.select(event, getParams().getContract().getId());
		} catch (ManagerBeanException e) {
			LOGGER.error(">>>> onSelectSettleDraft ",e);
			throw new AbortProcessingException(e.getMessage(), e);
		}
	}
	
	private void initializeParams() {
		// TODO inicializar los parametros obteniendo los datos del calculador
		// salario diario, importe dia vacacion, etc.
	}
	
	private void initializeConcepts() {
		try {
			IManagerBean dataBean = BeanManager.getManagerBean(ApplicationParameter.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(dataBean.getFieldName(IConfigAlias.APPLICATION_PARAMETER_NAME), PayrollAppParamsController.SETTLE_CONCEPT);
			List<ITransferObject> list = dataBean.getList(criteria);
			if(!list.isEmpty()){
				ApplicationParameter ap = (ApplicationParameter) list.get(0);
				if(ap.getValue()!=null && !ap.getValue().isEmpty()){
					IManagerBean bean = BeanManager.getManagerBean(PaymentConcept.class);
					criteria = new Criteria();
					criteria.addEqualExpression(bean.getFieldName(IPayrollAlias.PAYMENT_CONCEPT_ID), Integer.parseInt(ap.getValue()));
					PaymentConcept pc = (PaymentConcept) bean.getList(criteria).get(0);
					setNoticeDayConcept(pc);
					setVacationConcept(pc);
					setCompensationConcept(pc);
				}
			}
		} catch (ManagerBeanException e) {
			LOGGER.error(">>>> getSalaryTemplate ",e);
			throw new AbortProcessingException(e.getMessage(), e);
		}
	}
	
	private void saveSettlePayments() throws ManagerBeanException {
		String bundleMsg = ResourceBundle.getBundle(IPayrollConstants.BUNDLE_BASE_NAME).getString(IPayrollConstants.PAYROLL_SETTLE_NOTICE_DAY_AMOUNT);
		saveSettlePayment(getNoticeDayConcept(), getParams().getNoticeDayAmount(), bundleMsg);
		bundleMsg = ResourceBundle.getBundle(IPayrollConstants.BUNDLE_BASE_NAME).getString(IPayrollConstants.PAYROLL_SETTLE_VACATION_AMOUNT);
		saveSettlePayment(getVacationConcept(), getParams().getVacationAmount(), bundleMsg);
		bundleMsg = ResourceBundle.getBundle(IPayrollConstants.BUNDLE_BASE_NAME).getString(IPayrollConstants.PAYROLL_SETTLE_COMPENSATION);
		saveSettlePayment(getCompensationConcept(), getParams().getCompensation(), bundleMsg);
	}
	
	private void saveSettlePayment(PaymentConcept pc, Double amount, String description) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(ContractPayment.class);
		ContractPayment payment = new ContractPayment();
		payment.setContract(getParams().getContract());
		payment.setSalaryType(SalaryType.SETTLE);
		payment.setPaymentConcept(pc);
		payment.setStartDate(getParams().getSuspensionDate());
		payment.setEndDate(getParams().getSuspensionDate());
		payment.setType(PaymentType.COMPENSATION_OR_PREPAID_EXPENSES);
		payment.setExpression(amount.toString());
//		payment.setIrpfExpression("0.00");
//		payment.setQuoteExpression("0.00");
		payment.setDescription(description);
		bean.insert(payment);
	}
		
	private void generateSettle() throws ManagerBeanException {
		try {
			ContractController controller = (ContractController) FormUtil.getController(IPayrollConstants.CONTRACT_CONTROLLER);
			Contract contract = (Contract) controller.getTo();
			//Date startDate = getParams().getSuspensionDate(); 
			Date endDate = getParams().getSuspensionDate();
			//Date issueDate = getParams().getSuspensionDate(); 
			int year = CommonUtil.getYear(endDate);
			int month = CommonUtil.getMonth(endDate);
			ISalaryCalculatorContext ctx = 
				contract.getSalaryCalculatorContext(year, Month.values()[month], SalaryType.SETTLE);
			settle = (Salary) ctx.getSalaryProxy().getSalary();
			IManagerBean bean = BeanManager.getManagerBean(Salary.class);
			settle.setNonEstructuralOvertimeBase(0.0);
			bean.insert((ITransferObject) settle);
		}catch (AonException e) {
			String msg = "Error en el calculo del finiquito";
			AonUtil.addErrorMessage(msg);
			LOGGER.error(msg);
		} 
		setSettle(null);
	}
	
	public void setSettle(Salary settle) {
		this.settle = settle;
	}
	
	private void finalizeContract(){
		ContractController controller = (ContractController) FormUtil.getController(IPayrollConstants.CONTRACT_CONTROLLER);
		Contract contract = (Contract) controller.getTo();
		contract.setEndDate(getParams().getSuspensionDate());
		controller.accept(null);
		controller.getBeanName();
	}
}
