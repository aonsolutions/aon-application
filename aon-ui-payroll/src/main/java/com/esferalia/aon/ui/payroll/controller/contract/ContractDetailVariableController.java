package com.esferalia.aon.ui.payroll.controller.contract;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.enumeration.Month;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.IController;
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.payroll.calculator.ContractSalaryCalculatorContext;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.enumeration.SalaryType;
import com.esferalia.aon.salary.expression.ExpressionException;
import com.esferalia.aon.salary.expression.ITimedObject;
import com.esferalia.aon.ui.payroll.controller.IPayrollConstants;

public abstract class ContractDetailVariableController extends BasicController implements IVariablesHandler {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ContractDetailVariableController.class.getName());
	
	private List<SelectItem> concepts;
	private boolean modalPanelVisible;
	private boolean searchCurrent;
	private Date inactiveDate;
	
	public abstract AbstractVariableHandler getHandler() ;
	public abstract SalaryType getSalaryType() ;
	public abstract String getExpression();
	
	public Date getInactiveDate() {
		return inactiveDate;
	}
	public void setInactiveDate(Date inactiveDate) {
		this.inactiveDate = inactiveDate;
	}
	public boolean isSearchCurrent() {
		return searchCurrent;
	}
	public void setSearchCurrent(boolean searchCurrent) {
		this.searchCurrent = searchCurrent;
	}
	public boolean isModalPanelVisible() {
		return modalPanelVisible;
	}
	public void setModalPanelVisible(boolean modalPanelVisible) {
		this.modalPanelVisible = modalPanelVisible;
	}

	public void onEdit(ActionEvent event) {
		super.onSelect(event);
		reset(true);
		getHandler().initializeVariables(event);
		onReloadExpression(event);
	}
	
	public void onSave(ActionEvent event) {
		super.onAccept(event);
		reset(false);
	}
	
	@Override
	public void onSearch(ActionEvent event) {
		completeCiteria();
		super.onSearch(event);
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
		getHandler().setVariablesModel(null);
	}
	
	public void reset(boolean panelVisible) {
		setModalPanelVisible(panelVisible);
		setConcepts(null);
		setMonth(Month.getMonthByValue(CommonUtil.getMonth(new Date())));
		setYear( CommonUtil.getYear(new Date()));
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
	protected abstract void completeCiteria();
	
	public Contract getContract(){
		IController master = FormUtil.getController(IPayrollConstants.CONTRACT_CONTROLLER);
		return (Contract) master.getTo();
	}
	
	private Month month;
	private Integer year;
	private Double result;

	public Month getMonth() {
		if(month==null){
			month = Month.getMonthByValue(CommonUtil.getMonth(new Date()));
		}
		return month;
	}
	public void setMonth(Month month) {
		this.month = month;
	}
	public Integer getYear() {
		if(month==null){
			year = CommonUtil.getYear(new Date());
		}
		return year;
	}
	public void setYear(Integer year) {
		this.year = year;
	}
	public Double getResult() {
		return result;
	}
	public void setResult(Double result) {
		this.result = result;
	}
	
	public void onReloadExpression(ActionEvent event){
		try {
			Calendar startCal = Calendar.getInstance();
			Calendar endCal = Calendar.getInstance();
			startCal.set(Calendar.HOUR_OF_DAY, 0);
			startCal.set(Calendar.YEAR, year);
			startCal.set(Calendar.MONTH, month.ordinal());
			startCal.set(Calendar.DAY_OF_MONTH, startCal.getActualMinimum(Calendar.DAY_OF_MONTH));
			endCal.set(Calendar.HOUR_OF_DAY, 0);
			endCal.set(Calendar.YEAR, year);
			endCal.set(Calendar.MONTH, month.ordinal());
			endCal.set(Calendar.DAY_OF_MONTH, endCal.getActualMaximum(Calendar.DAY_OF_MONTH));
			ContractSalaryCalculatorContext ctx = (ContractSalaryCalculatorContext) getContract().getSalaryCalculatorContext(year, month, getSalaryType());
			
			List<ITimedObject<Object>> list = ctx.getExpressionContext().eval(getExpression(), startCal.getTime(), endCal.getTime());
			if(list.isEmpty() || list.get(0).getValue()==null){
				result = null;
			} else {
				result = new Double(list.get(0).getValue().toString());
			}
		} catch (SalaryException e) {
			result = null;
			getHandler().setVariablesModel(null);
			getHandler().setUndefinedVariablesModel(null);
			LOGGER.error("error evaluating expression.");
		} catch (ExpressionException e) {
			result = null;
			getHandler().setVariablesModel(null);
			getHandler().setUndefinedVariablesModel(null);
			LOGGER.error("error evaluating expression.");
		}
		initializeVariables(event);
	}
	
	
}
