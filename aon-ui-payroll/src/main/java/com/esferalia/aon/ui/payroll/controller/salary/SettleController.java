package com.esferalia.aon.ui.payroll.controller.salary;

import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.Month;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.config.ApplicationParameter;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.payroll.ContractData;
import com.esferalia.aon.payroll.ContractPayment;
import com.esferalia.aon.payroll.PaymentConcept;
import com.esferalia.aon.payroll.Salary;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.enumeration.PaymentType;
import com.esferalia.aon.salary.enumeration.SalaryType;
import com.esferalia.aon.ui.payroll.controller.IPayrollConstants;
import com.esferalia.aon.ui.payroll.controller.PayrollAppParamsController;
import com.esferalia.aon.ui.payroll.controller.contract.ContractController;
import com.esferalia.aon.ui.payroll.controller.salary.draft.SalaryDraftController;
import com.esferalia.aon.ui.payroll.utils.PayrollUtils;

public class SettleController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(SettleController.class);
	
	private SettleParams params;
	
	private PaymentConcept noticeDayConcept;
	private PaymentConcept vacationConcept;
	private PaymentConcept compensationConcept;
	
	private Salary settle;
	
	private String backAction;
	
	public String getBackAction() {
		return backAction;
	}
	public void setBackAction(String backAction) {
		this.backAction = backAction;
	}
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
	public void setSettle(Salary settle) {
		this.settle = settle;
	}
	
	public void onSelectContract(ActionEvent event){
		ContractController controller = (ContractController) FormUtil.getController(IPayrollConstants.CONTRACT_CONTROLLER);
		if(controller.getTo()==null || ((Contract) controller.getTo()).getId()==null){
			controller.onSelect(event);
			setBackAction(null);
		}
		initializeParams();
		initializeConcepts();
	}
	
	public void onGenerate(ActionEvent event){
		if (settle == null) {
			try {
				saveSettlePayments();
				generateSettle();
				finalizeContract();
			} catch (ManagerBeanException e) {
				LOGGER.error(">>>> onGenerate ",e);
				throw new AbortProcessingException(e.getMessage(), e);
			} catch (SalaryException e) {
				LOGGER.error(">>>> onGenerate ",e);
				throw new AbortProcessingException(e.getMessage(), e);
			}
		}
	}
	
	public void onSelectSettleDraft(ActionEvent event){
		try {
			SalaryDraftController controller = (SalaryDraftController) FormUtil.getController(IPayrollConstants.SALARY_DRAFT_CONTROLLER);
			controller.setSalaryType(SalaryType.SETTLE);
			controller.setYear(CommonUtil.getYear(getParams().getSuspensionDate()));
			controller.setMonth(Month.getMonthByValue(CommonUtil.getMonth(getParams().getSuspensionDate())));
			controller.select(event, getParams().getContract().getId());
		} catch (ManagerBeanException e) {
			LOGGER.error(">>>> onSelectSettleDraft ",e);
			throw new AbortProcessingException(e.getMessage(), e);
		}
	}
	
	private void initializeParams() {
		ContractController controller = (ContractController) FormUtil.getController(IPayrollConstants.CONTRACT_CONTROLLER);
		setParams(new SettleParams((Contract) controller.getTo()));
	}
	
	private void initializeConcepts() {
		try {
			IManagerBean dataBean = BeanManager.getManagerBean(ApplicationParameter.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(dataBean.getFieldName(IEntityAlias.APPLICATION_PARAMETER_NAME), PayrollAppParamsController.SETTLE_VACATION_CONCEPT);
			criteria.addOrExpression(dataBean.getFieldName(IEntityAlias.APPLICATION_PARAMETER_NAME), PayrollAppParamsController.SETTLE_NOTICE_DAY_CONCEPT);
			criteria.addOrExpression(dataBean.getFieldName(IEntityAlias.APPLICATION_PARAMETER_NAME), PayrollAppParamsController.SETTLE_COMPENSATION_CONCEPT);
			List<ITransferObject> list = dataBean.getList(criteria);
			if(!list.isEmpty()){
				for(ITransferObject to: list){
					ApplicationParameter ap = (ApplicationParameter) to;
					if(ap.getValue()!=null && !ap.getValue().isEmpty()){
						IManagerBean bean = BeanManager.getManagerBean(PaymentConcept.class);
						criteria = new Criteria();
						criteria.addEqualExpression(bean.getFieldName(IEntityAlias.PAYMENT_CONCEPT_ID), Integer.parseInt(ap.getValue()));
						PaymentConcept pc = (PaymentConcept) bean.getList(criteria).get(0);
						if(ap.getName().equals(PayrollAppParamsController.SETTLE_VACATION_CONCEPT)){
							setVacationConcept(pc);
						} else if(ap.getName().equals(PayrollAppParamsController.SETTLE_NOTICE_DAY_CONCEPT)){
							setNoticeDayConcept(pc);
						} else if(ap.getName().equals(PayrollAppParamsController.SETTLE_COMPENSATION_CONCEPT)){
							setCompensationConcept(pc);
						}
					} else {
						String msg = "No se han definido los conceptos de finiquito.";
						LOGGER.error(">>>> initializeConcepts ",msg);
						AonUtil.addErrorMessage(msg);
						throw new AbortProcessingException(msg);
					}
				}
			} else {
				String msg = "No se han definido los conceptos de finiquito.";
				LOGGER.error(">>>> initializeConcepts ",msg);
				AonUtil.addErrorMessage(msg);
				throw new AbortProcessingException(msg);
			}
		} catch (ManagerBeanException e) {
			LOGGER.error(">>>> initializeConcepts ",e);
			throw new AbortProcessingException(e.getMessage(), e);
		} catch (ExpressionException e) {
			LOGGER.error(">>>> initializeConcepts ",e);
			throw new AbortProcessingException(e.getMessage(), e);
		}
	}
	
	private void saveSettlePayments() throws ManagerBeanException {
		String bundleMsg = ResourceBundle.getBundle(IPayrollConstants.BUNDLE_BASE_NAME).getString(IPayrollConstants.PAYROLL_SETTLE_NOTICE_DAY_AMOUNT);
		saveSettlePayment(getNoticeDayConcept(), getParams().getNoticeAmount(), bundleMsg);
		bundleMsg = ResourceBundle.getBundle(IPayrollConstants.BUNDLE_BASE_NAME).getString(IPayrollConstants.PAYROLL_SETTLE_VACATION_AMOUNT);
		saveSettlePayment(getVacationConcept(), getParams().getVacationAmount(), bundleMsg);
		bundleMsg = ResourceBundle.getBundle(IPayrollConstants.BUNDLE_BASE_NAME).getString(IPayrollConstants.PAYROLL_SETTLE_COMPENSATION);
		saveSettlePayment(getCompensationConcept(), getParams().getCompensation(), bundleMsg);
	}
	
	private void saveSettlePayment(PaymentConcept pc, Double amount, String description) throws ManagerBeanException {
		if(amount!=0){
			IManagerBean bean = BeanManager.getManagerBean(ContractPayment.class);
			ContractPayment payment = new ContractPayment();
			payment.setContract(getParams().getContract());
			payment.setSalaryType(SalaryType.SETTLE);
			payment.setPaymentConcept(pc);
			payment.setStartDate(CommonUtil.getMonthFirstDay(getParams().getSuspensionDate()));
			payment.setEndDate(getParams().getSuspensionDate());
			payment.setType(PaymentType.COMPENSATION_OR_PREPAID_EXPENSES);
			payment.setExpression("IMPORTE_"+pc.getCode());
//			payment.setIrpfExpression("0.00");
//			payment.setQuoteExpression("0.00");
			payment.setDescription(description);
			bean.insert(payment);
			savePaymentVariable(pc.getCode(), amount);
		}
	}
		
	private void savePaymentVariable(String name, Double amount) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(ContractData.class);
		ContractData data = new ContractData();
		data.setContract(getParams().getContract());
		data.setStartDate(CommonUtil.getMonthFirstDay(getParams().getSuspensionDate()));
		data.setEndDate(getParams().getSuspensionDate());
		data.setName("IMPORTE_"+name);
		data.setExpression(amount.toString());
		bean.insert(data);
	}
	
	private void generateSettle() throws SalaryException {
		try {
			Contract contract = getParams().getContract();
			Date endDate = getParams().getSuspensionDate();
			
			PayrollUtils utils = new PayrollUtils();
			settle = (Salary) utils.calculateSalary(contract, endDate);
			settle.setNonEstructuralOvertimeBase(0.0);
			settle.setTotalIrpf(0.0);
			settle.setContract(contract);
			settle.getSalaryPayments().clear();
			settle.getSalaryDeductions().clear();
			settle.getSalaryBonus().clear();
			settle.getSalaryCosts().clear();
			settle.getSalaryEmbargos().clear();
			
			IManagerBean bean = BeanManager.getManagerBean(Salary.class);
			bean.insert((ITransferObject) settle);
		}catch (ManagerBeanException e) {
			String msg = "Error en el calculo del finiquito";
			AonUtil.addErrorMessage(msg);
			LOGGER.error(msg);
		} 
		setSettle(null);
	}
	
	
	private void finalizeContract(){
		ContractController controller = (ContractController) FormUtil.getController(IPayrollConstants.CONTRACT_CONTROLLER);
		Contract contract = (Contract) controller.getTo();
		contract.setEndDate(getParams().getSuspensionDate());
		controller.accept(null);
		controller.getBeanName();
	}
	
}
