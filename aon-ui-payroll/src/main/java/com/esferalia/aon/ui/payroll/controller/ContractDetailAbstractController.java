package com.esferalia.aon.ui.payroll.controller;

import java.util.LinkedList;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.IController;
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.expression.ExpressionContext;
import com.esferalia.aon.salary.expression.IExpression;

public abstract class ContractDetailAbstractController extends BasicController {
	
	private List<SelectItem> concepts;
	private boolean modalPanelVisible;
	
	public boolean isModalPanelVisible() {
		return modalPanelVisible;
	}
	public void setModalPanelVisible(boolean modalPanelVisible) {
		this.modalPanelVisible = modalPanelVisible;
	}

	public List<?> expressionContext(Object suggest) {
		try {
			List<IExpression> list = new LinkedList<IExpression>();
			IController master = FormUtil.getController("contract");
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

	@Override
	public void onCancel(ActionEvent event) {
		super.onCancel(event);
		reset(false);
	}

	@Override
	public void onRemove(ActionEvent event) {
		super.onRemove(event);
		reset(false);
	}
	
	@Override
	public void onReset(ActionEvent event) {
		super.onReset(event);
		reset(true);
	}
	
	public void reset(boolean panelVisible) {
		setModalPanelVisible(panelVisible);
		setConcepts(null);
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

	protected abstract void initialiceConcepts();
}
