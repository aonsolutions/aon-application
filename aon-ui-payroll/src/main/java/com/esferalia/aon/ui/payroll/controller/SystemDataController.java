package com.esferalia.aon.ui.payroll.controller;

import java.util.Date;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import org.apache.commons.lang.StringUtils;

import com.code.aon.AonVersion;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;


public class SystemDataController extends BasicController implements IVariableFilter {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private VariableFilter variableFilter;
	
	public VariableFilter getVariableFilter() {
		try {
			if(variableFilter == null){
				variableFilter = new VariableFilter(this.getModel(), null);
			}
		} catch (Exception e) {
			String msg = "Error al iniciar el filtro de datos.";
			AonUtil.addErrorMessage(msg);
			variableFilter = new VariableFilter(null, null);
		}
		return variableFilter;
	}

	public void setVariableFilter(VariableFilter variableFilter) {
		this.variableFilter = variableFilter;
	}
	
	@Override
	public void reloadData(ActionEvent event) {
		try {
			clearCriteria();
			updateFilterCriteria();
		} catch (ManagerBeanException e) {
			String msg = "Error al aplicar el filtro.";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg, e);
		}
		super.onSearch(event);
	}
	
	private void updateFilterCriteria() throws ManagerBeanException{
		if(getVariableFilter().isSearchCurrentVariables()){
			Expression expr1 = ExpressionUtilities.getGreaterThanOrEqualExpression(this.getFieldName(IEntityAlias.SYSTEM_DATA_END_DATE), new Date());
			Expression expr2 = ExpressionUtilities.getNullExpression(this.getFieldName(IEntityAlias.SYSTEM_DATA_END_DATE));
			getCriteria().addExpression(ExpressionUtilities.getOrExpression(expr1, expr2));						
		} else {
			if(getVariableFilter().getInactiveDate()!=null){
				Expression expr1 = ExpressionUtilities.getGreaterThanOrEqualExpression(this.getFieldName(IEntityAlias.SYSTEM_DATA_END_DATE), getVariableFilter().getInactiveDate());
				Expression expr2 = ExpressionUtilities.getNullExpression(this.getFieldName(IEntityAlias.SYSTEM_DATA_END_DATE));
				getCriteria().addExpression(ExpressionUtilities.getOrExpression(expr1, expr2));						
			}
		}
		if(!StringUtils.isEmpty(getVariableFilter().getSelectedVariableFilter())){
			getCriteria().addEqualExpression(this.getFieldName(IEntityAlias.SYSTEM_DATA_NAME), getVariableFilter().getSelectedVariableFilter());
		}
	}
	
	@Override
	public void onSearch(ActionEvent event) {
		try {
			updateFilterCriteria();
		} catch (ManagerBeanException e) {
			String msg = "Error en el filtro de datos.";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg, e);
		}
		super.onSearch(event);
	}
	
}