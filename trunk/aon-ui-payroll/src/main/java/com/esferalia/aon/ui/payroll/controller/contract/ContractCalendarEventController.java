package com.esferalia.aon.ui.payroll.controller.contract;

import java.util.LinkedList;
import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.form.LinesController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.payroll.enumeration.ContextVariable;

public class ContractCalendarEventController extends LinesController {
	
	private boolean forceBackAction;
	
	private String forcedBackAction;
	
	public boolean isForceBackAction() {
		return forceBackAction;
	}

	public void setForceBackAction(boolean forceBackAction) {
		this.forceBackAction = forceBackAction;
	}
	
	public String getForcedBackAction() {
		return forcedBackAction;
	}

	public void setForcedBackAction(String forcedBackAction) {
		this.forcedBackAction = forcedBackAction;
	}

	public List<SelectItem> getCalendarEvents(){
		List<SelectItem> list = new LinkedList<SelectItem>();
		SelectItem item = new SelectItem(ContextVariable.HOLIDAYS.toString(), ContextVariable.HOLIDAYS.toString());
		list.add(item);
		item = new SelectItem(ContextVariable.SPECIAL_DAYS.toString(), ContextVariable.SPECIAL_DAYS.toString());
		list.add(item);
		item = new SelectItem(ContextVariable.SPECIAL_DAYS.toString(), ContextVariable.NO_HOLIDAYS.toString());
		list.add(item);
		return list;
	}
	
	@Override
	public void initializeModel() {
		try {
			String alias = this.getManagerBean().getFieldName(IEntityAlias.CONTRACT_DATA_NAME);
			Expression exp1 = ExpressionUtilities.getEqualExpression(alias, ContextVariable.HOLIDAYS.toString());
			Expression exp2 = ExpressionUtilities.getEqualExpression(alias, ContextVariable.SPECIAL_DAYS.toString());
			Expression exp3 = ExpressionUtilities.getEqualExpression(alias, ContextVariable.NO_HOLIDAYS.toString());
			Expression exp = ExpressionUtilities.getOrExpression(exp1, exp2);
			exp = ExpressionUtilities.getOrExpression(exp, exp3);
			getCriteria().addExpression( exp );
			super.initializeModel();
		} catch (ManagerBeanException e) {
			String message = "Hubo un error al cargar las indidencias de contrato.";
			AonUtil.addErrorMessage(message);
			throw new AbortProcessingException(message);
		}
	}
	
	@Override
	public void onReset(ActionEvent arg0) {
		if(isForceBackAction()){
			setForcedBackAction(getBackAction());
		}
		super.onReset(arg0);
		if(isForceBackAction()){
			setBackAction(getForcedBackAction());
		}
	}
	
	@Override
	public void onSelect(ActionEvent event) {
		if(isForceBackAction()){
			setForcedBackAction(getBackAction());
		}
		super.onSelect(event);
		if(isForceBackAction()){
			setBackAction(getForcedBackAction());
		}
	}
	
	
}
