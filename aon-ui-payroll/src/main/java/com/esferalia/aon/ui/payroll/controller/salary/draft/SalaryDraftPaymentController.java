package com.esferalia.aon.ui.payroll.controller.salary.draft;

import javax.faces.event.ActionEvent;

import org.apache.commons.lang.StringUtils;

import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.payroll.AgreementLevelPayment;
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.payroll.ContractPayment;
import com.esferalia.aon.payroll.calculator.IContractPayment;
import com.esferalia.aon.payroll.dao.IPayrollAlias;
import com.esferalia.aon.salary.expression.ExpressionScope;
import com.esferalia.aon.ui.payroll.controller.IPayrollConstants;

public class SalaryDraftPaymentController extends BasicController{
	
	private boolean modalPanelVisible;
	
	public boolean isModalPanelVisible() {
		return modalPanelVisible;
	}
	public void setModalPanelVisible(boolean modalPanelVisible) {
		this.modalPanelVisible = modalPanelVisible;
	}
	
//	protected void initialiceConcepts() {
//		setConcepts(new LinkedList<SelectItem>());
//		try {
//			ContractPayment cp = (ContractPayment) getTo();
//			IManagerBean bean = BeanManager.getManagerBean(PaymentConcept.class);
//			Criteria criteria = new Criteria();
//			criteria.addEqualExpression(bean.getFieldName(IPayrollAlias.PAYMENT_CONCEPT_TYPE), cp.getType());
//			criteria.addOrder(bean.getFieldName(IPayrollAlias.PAYMENT_CONCEPT_CODE));
//			List<ITransferObject> list = bean.getList(criteria);
//			for (ITransferObject to: list) {
//				PaymentConcept pc = (PaymentConcept) to;
//				getConcepts().add(new SelectItem(pc, pc.getCode() + " - "+pc.getDescription()));
//			}
//		} catch (ManagerBeanException e) {
//			// Se devuelve la lista vacia.
//		} 
//	}
	
	public void onPaymentConceptChange(ActionEvent event) {
		ContractPayment cp = (ContractPayment) getTo();
		if (cp.getType() != null && StringUtils.isEmpty(cp.getDescription())) {
			cp.setDescription( cp.getPaymentConcept().getDescription() );
		}
	}
	
	private void initializeVariables(ActionEvent event) {
		// TODO a la espera de obtener del contexto del contrato las
		// variables asociadas a un payment en concreto
		BasicController controller = (BasicController) AonUtil.getRegisteredBean(IPayrollConstants.CONTRACT_DATA_CONTROLLER);
		try {
			// TODO ainadir al criteria el id de las variables de este payment
			controller.getCriteria().addNullExpression(controller.getFieldName(IPayrollAlias.CONTRACT_DATA_ID));
			controller.onSearch(event);
		} catch (ManagerBeanException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
////			SQLContractSalaryCalculatorContext context = new
//		IController master = FormUtil.getController(IPayrollConstants.SALARY_DRAFT_CONTROLLER);
//		Contract contract = (Contract) master.getTo();
//		ContractPayment cp = (ContractPayment) getTo();
//		try {
//			Date startDate = cp.getStartDate();
//			Date endDate = cp.getEndDate()!=null?cp.getEndDate():null;
//			if(endDate==null){
//				Calendar cal = new GregorianCalendar();
//				cal.setTime(new Date());
//				cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
////					endDate = new Date(startDate.getTime());
//				endDate = cal.getTime();
//			}
//			ISalaryCalculatorContext ctx = contract.getSalaryCalculatorContext(startDate, endDate, new Date());
//			List<ITimedObject<Object>> list = ctx.getExpressionContext().eval(cp.getExpression(), startDate, endDate);//.addExpression(cp.getExpression(), cp.getStartDate(),cp.getEndDate());
//			ctx.getExpressionContext().getExpressionVariables();
//			for(ITimedObject<Object> o: list){
//				o.getClass();
//			}
//		} catch (SalaryException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (ExpressionException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}
	
	public void onEdit(ActionEvent event) {
//		super.onSelect(event);
		reset(true);
		setSelectedPayment(event);
		initializeVariables(event);
	}

	public void onSave(ActionEvent event) {
//		IController master = FormUtil.getController(IPayrollConstants.SALARY_DRAFT_CONTROLLER);
//		Contract contract = (Contract) master.getTo();
//		ContractPayment cd  = (ContractPayment) getTo();
//		cd.setContract(contract);
		ContractPayment cp = (ContractPayment) this.getTo();
		if(cp.getDescription().isEmpty()){
			cp.setDescription(null);
		}
		super.onAccept(event);
		reset(false);
		SalaryDraftController master = (SalaryDraftController) FormUtil.getController(IPayrollConstants.SALARY_DRAFT_CONTROLLER);
		master.setPaymentsModel(null);
	}

	public void onCancel(ActionEvent event) {
		super.onCancel(event);
		reset(false);
	}

	public void onRemove(ActionEvent event) {
		super.onRemove(event);
		reset(false);
		SalaryDraftController master = (SalaryDraftController) FormUtil.getController(IPayrollConstants.SALARY_DRAFT_CONTROLLER);
		master.setPaymentsModel(null);
	}
	
	public void onReset(ActionEvent event) {
		super.onReset(event);
		reset(true);
		initializeVariables(event);
	}
	
	public void reset(boolean panelVisible) {
		setModalPanelVisible(panelVisible);
//		setConcepts(null);
	}
	
	private void setSelectedPayment(ActionEvent event){
		SalaryDraftController controller = (SalaryDraftController) AonUtil.getRegisteredBean(IPayrollConstants.SALARY_DRAFT_CONTROLLER);
		IContractPayment payment = (IContractPayment) controller.getPaymentsModel().getRowData();
		if(payment.getScope()==ExpressionScope.CONTRACT){
			this.setTo((ContractPayment) payment);
		} else if(payment.getScope()==ExpressionScope.AGREEMENT){
			super.onReset(event);
			ContractPayment cp = (ContractPayment) this.getTo();
			AgreementLevelPayment alp = (AgreementLevelPayment) payment;
			cp.setContract((Contract) controller.getTo()); 
			cp.setType(alp.getType()); 
			cp.setPaymentConcept(alp.getPaymentConcept()); 
			cp.setDescription(alp.getDescription()); 
			cp.setExpression(alp.getExpression()); 
			cp.setIrpfExpression(alp.getIrpfExpression()); 
			cp.setQuoteExpression(alp.getQuoteExpression()); 
			cp.setStartDate(alp.getStartDate()); 
			cp.setEndDate(alp.getEndDate()); 
			cp.setMonth(alp.getMonth()); 
			cp.setDescriptionDecorable(alp.isDescriptionDecorable());
			cp.setSalaryType(alp.getSalaryType());
		}
	}
	
	
}
