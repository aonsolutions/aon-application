package com.esferalia.aon.ui.payroll.controller;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import com.code.aon.common.enumeration.Month;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.ui.form.LinesController;
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.expression.ExpressionContext;
import com.esferalia.aon.salary.expression.IExpression;

public abstract class SalaryDraftLinesController extends LinesController {
	
	private List<SelectItem> concepts;
	private boolean modalPanelVisible;
	private SelectItem currentMonth;
	
	public boolean isModalPanelVisible() {
		return modalPanelVisible;
	}
	public void setModalPanelVisible(boolean modalPanelVisible) {
		this.modalPanelVisible = modalPanelVisible;
	}

	public List<?> expressionContext(Object suggest) {
		try {
			List<IExpression> list = new LinkedList<IExpression>();
			SalaryDraftController master = (SalaryDraftController) getMasterController();
			Contract contract = (Contract) master.getTo();
			ExpressionContext ec = contract.getSalaryCalculatorContext().getExpressionContext();
			String filter = (String) suggest;
			for (IExpression exp:ec.getValues() ){
				if (exp.getName().startsWith(filter)) {
					list.add(exp);		
				}
			}
			return list;
		} catch (SalaryException e) {
			return null;
		}
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

	public void onRemove(ActionEvent event) {
		super.onRemove(event);
		reset(false);
	}

	public void reset(boolean panelVisible) {
		setModalPanelVisible(panelVisible);
		setConcepts(null);
		setCurrentMonth(null);
	}
	
	public void onTypeChange(ActionEvent event) {
		setConcepts(null);
	}
	
	public List<SelectItem> getConcepts() {
		if (concepts == null) {
			initialiceConcepts();
		}
		return concepts;
		
	}
	public void setConcepts(List<SelectItem> concepts) {
		this.concepts = concepts;
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

	protected abstract void initialiceConcepts();
}
