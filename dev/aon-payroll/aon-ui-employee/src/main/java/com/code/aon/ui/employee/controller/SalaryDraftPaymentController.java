package com.code.aon.ui.employee.controller;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.StringUtils;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.Month;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.employee.Contract;
import com.code.aon.employee.ContractPayment;
import com.code.aon.employee.PaymentConcept;
import com.code.aon.employee.dao.IEmployeeAlias;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.LinesController;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.expression.ExpressionContext;
import com.esferalia.aon.salary.expression.IExpression;

public class SalaryDraftPaymentController extends LinesController {

	private boolean modalPanelVisible;
	private List<SelectItem> paymentConcepts;
	private SelectItem currentMonth;
	
	public boolean isModalPanelVisible() {
		return modalPanelVisible;
	}
	public void setModalPanelVisible(boolean modalPanelVisible) {
		this.modalPanelVisible = modalPanelVisible;
	}
	
	public List<SelectItem> getPaymentConcepts() {
		if (paymentConcepts == null) {
			setPaymentConcepts(new LinkedList<SelectItem>());
			try {
				ContractPayment cp = (ContractPayment) getTo();
				IManagerBean bean = BeanManager.getManagerBean(PaymentConcept.class);
				Criteria criteria = new Criteria();
				criteria.addEqualExpression(bean.getFieldName(IEmployeeAlias.PAYMENT_CONCEPT_TYPE), cp.getType());
				criteria.addOrder(bean.getFieldName(IEmployeeAlias.PAYMENT_CONCEPT_CODE));
				List<ITransferObject> list = bean.getList(criteria);
				for (ITransferObject to: list) {
					PaymentConcept pc = (PaymentConcept) to;
					paymentConcepts.add(new SelectItem(pc, pc.getCode() + "-"+pc.getDescription()));
				}
			} catch (ManagerBeanException e) {
				// Se devuelve la lista vacia.
			} 
		}
		return paymentConcepts;
	}
	public void setPaymentConcepts(List<SelectItem> paymentConcepts) {
		this.paymentConcepts = paymentConcepts;
	}

	public SelectItem getCurrentMonth() {
		if (currentMonth == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			SalaryDraftController master = (SalaryDraftController) getMasterController();
			Month month = Month.getMonthByValue(CommonUtil.getMonth( master.getIssueDate()));
			setCurrentMonth( new SelectItem(month,month.getName(locale)));
		}
		return currentMonth; 
	}
	public void setCurrentMonth(SelectItem currentMonth) {
		this.currentMonth = currentMonth;
	}

	public void onEditNew(ActionEvent event) {
		super.onReset(event);
		SalaryDraftController master = (SalaryDraftController) getMasterController();
		ContractPayment cp = (ContractPayment) getTo();
		cp.setStartDate( master.getStartDate() );
		cp.setEndDate( master.getEndDate() );
		Month month = Month.getMonthByValue(CommonUtil.getMonth( master.getIssueDate()));
		cp.setMonth( month ); 
		reset(true);
	}

	public void onEdit(ActionEvent event) {
		super.onSelect(event);
		reset(true);
	}
	
	public void onSave(ActionEvent event) {
		super.onAccept(event);
		reset(false);
	}

	public void onCancel(ActionEvent event) {
		super.onCancel(event);
		reset(false);
	}

	private void reset(boolean panelVisible) {
		setModalPanelVisible(panelVisible);
		setPaymentConcepts(null);
		setCurrentMonth(null);
	}
	
	public void onTypeChange(ActionEvent event) {
		setPaymentConcepts(null);
	}
	
	public void onPaymentConceptChange(ActionEvent event) {
		ContractPayment cp = (ContractPayment) getTo();
		if (cp.getType() != null && StringUtils.isEmpty(cp.getDescription())) {
			cp.setDescription( cp.getPaymentConcept().getDescription() );
		}
	}
	
	public List<?> expressionContext(Object suggest) {
		try {
			List<IExpression> list = new LinkedList<IExpression>();
			SalaryDraftController master = (SalaryDraftController) getMasterController();
			Contract contract = (Contract) master.getTo();
			ExpressionContext ec = contract.getSalaryCalculatorContext().getExpressionContext();
			list.addAll(ec.getValues());
			// TODO filtrar el contenido de la lista en base a suggest.
			return list;
		} catch (SalaryException e) {
			return null;
		}
	}
}
