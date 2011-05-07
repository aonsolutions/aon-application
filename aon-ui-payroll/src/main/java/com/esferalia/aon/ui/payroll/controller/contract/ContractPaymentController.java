package com.esferalia.aon.ui.payroll.controller.contract;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.StringUtils;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.payroll.ContractPayment;
import com.esferalia.aon.payroll.PaymentConcept;
import com.esferalia.aon.payroll.dao.IPayrollAlias;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.calculator.ISalaryCalculatorContext;
import com.esferalia.aon.salary.expression.ExpressionException;
import com.esferalia.aon.salary.expression.ITimedObject;
import com.esferalia.aon.ui.payroll.controller.IPayrollConstants;

public class ContractPaymentController extends ContractDetailAbstractController {

	@Override
	public void onSave(ActionEvent event) {
		IController master = FormUtil.getController("contract");
		Contract contract = (Contract) master.getTo();
		ContractPayment cd  = (ContractPayment) getTo();
		cd.setContract(contract);
		super.onSave(event);
	}
	
	@Override
	protected void initialiceConcepts() {
		setConcepts(new LinkedList<SelectItem>());
		try {
			ContractPayment cp = (ContractPayment) getTo();
			IManagerBean bean = BeanManager.getManagerBean(PaymentConcept.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(IPayrollAlias.PAYMENT_CONCEPT_TYPE), cp.getType());
			criteria.addOrder(bean.getFieldName(IPayrollAlias.PAYMENT_CONCEPT_CODE));
			List<ITransferObject> list = bean.getList(criteria);
			for (ITransferObject to: list) {
				PaymentConcept pc = (PaymentConcept) to;
				getConcepts().add(new SelectItem(pc, pc.getCode() + " - "+pc.getDescription()));
			}
		} catch (ManagerBeanException e) {
			// Se devuelve la lista vacia.
		} 
	}
	
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
		
//		SQLContractSalaryCalculatorContext context = new
		IController master = FormUtil.getController("contract");
		Contract contract = (Contract) master.getTo();
		ContractPayment cp = (ContractPayment) getTo();
		try {
			Date startDate = cp.getStartDate();
			Date endDate = cp.getEndDate()!=null?cp.getEndDate():null;
			if(endDate==null){
				Calendar cal = new GregorianCalendar();
				cal.setTime(new Date());
				cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
//				endDate = new Date(startDate.getTime());
				endDate = cal.getTime();
			}
			ISalaryCalculatorContext ctx = contract.getSalaryCalculatorContext(startDate, endDate, new Date());
			List<ITimedObject<Object>> list = ctx.getExpressionContext().eval(cp.getExpression(), startDate, endDate);//.addExpression(cp.getExpression(), cp.getStartDate(),cp.getEndDate());
			ctx.getExpressionContext().getExpressionVariables();
			for(ITimedObject<Object> o: list){
				o.getClass();
			}
		} catch (SalaryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExpressionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public void onEdit(ActionEvent event) {
		super.onEdit(event);
		initializeVariables(event);
	}
	
	@Override
	public void onReset(ActionEvent event) {
		super.onReset(event);
		initializeVariables(event);
	}

}
