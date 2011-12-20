package com.esferalia.aon.ui.payroll.controller.contract;

import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.ListDataModel;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.payroll.Agreement;
import com.esferalia.aon.payroll.AgreementData;
import com.esferalia.aon.payroll.IVariableData;
import com.esferalia.aon.payroll.SystemData;
import com.esferalia.aon.payroll.dao.IPayrollAlias;
import com.esferalia.aon.salary.expression.IExpression;

public class AgreementVariablesHandler extends AbstractVariableHandler{

	private static final Logger LOGGER = LoggerFactory.getLogger(AgreementVariablesHandler.class.getName());

	public AgreementVariablesHandler(IController controller) {
		super(controller);
	}

	@Override
	public void initializeVariables(ActionEvent event) {
		setVariablesModel(null);
		try {
			Agreement agreement = ((Agreement) getController().getTo());
			setUndefinedVariablesModel(null);
			IManagerBean bean = BeanManager.getManagerBean(AgreementData.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(IPayrollAlias.AGREEMENT_DATA_AGREEMENT_ID), agreement.getId());
			criteria.addOrder(bean.getFieldName(IPayrollAlias.AGREEMENT_DATA_NAME));
			if(isSearchCurrentVariables()){
				Expression expr1 = ExpressionUtilities.getGreaterThanOrEqualExpression(bean.getFieldName(IPayrollAlias.AGREEMENT_DATA_END_DATE), new Date());
				Expression expr2 = ExpressionUtilities.getNullExpression(bean.getFieldName(IPayrollAlias.AGREEMENT_DATA_END_DATE));
				criteria.addExpression(ExpressionUtilities.getOrExpression(expr1, expr2));						
			} else {
				if(getInactiveDate()!=null){
					Expression expr1 = ExpressionUtilities.getGreaterThanOrEqualExpression(bean.getFieldName(IPayrollAlias.AGREEMENT_DATA_END_DATE), getInactiveDate());
					Expression expr2 = ExpressionUtilities.getNullExpression(bean.getFieldName(IPayrollAlias.AGREEMENT_DATA_END_DATE));
					criteria.addExpression(ExpressionUtilities.getOrExpression(expr1, expr2));						
				}
			}
			if(!StringUtils.isEmpty(getVariableFilter())){
				criteria.addEqualExpression(bean.getFieldName(IPayrollAlias.AGREEMENT_DATA_NAME), getVariableFilter());
			}
			List<IVariableData> dataList = null;
			dataList = new LinkedList<IVariableData>();
			List<ITransferObject> list = bean.getList(criteria);
			if(!list.isEmpty()){
				for(ITransferObject to: list){
					AbstractVariableData data = new AbstractVariableData();
					data.setVariableData((IVariableData) to);
					dataList.add(data);
				}
			}
			setVariablesModel(new ListDataModel(dataList));
		} catch (ManagerBeanException e) {
			String msg = "Imposible cargar las variables del convenio (" + e.getMessage() +")";
			LOGGER.error(msg);
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg,e);
		}
	}
	
	@Override
	public List<?> expressionContext(Object suggest) {
		// CONTRACT variables
		try {
			String filter = (String) suggest;
			List<String> list = new LinkedList<String>();
			Agreement agreement = (Agreement) getController().getTo();
			IManagerBean bean = BeanManager.getManagerBean(AgreementData.class);
			Criteria criteria = new Criteria();  
			String alias = bean.getFieldName(IPayrollAlias.AGREEMENT_DATA_AGREEMENT_ID);
			criteria.addEqualExpression(alias, agreement.getId());
			alias = bean.getFieldName(IPayrollAlias.AGREEMENT_DATA_END_DATE);
			Expression expr1 = ExpressionUtilities.getGreaterThanOrEqualExpression(alias, new Date());
			Expression expr2 = ExpressionUtilities.getNullExpression(alias);
			criteria.addExpression(ExpressionUtilities.getOrExpression(expr1, expr2));			
			criteria.addOrder(alias, false);
			alias = bean.getFieldName(IPayrollAlias.AGREEMENT_DATA_NAME);
			criteria.addOrder(alias);
			for(ITransferObject to: bean.getList(criteria)){
				AgreementData data = (AgreementData) to;
				if (data.getName().contains(filter.toUpperCase())) {
					list.add(data.getName());		
				}
			}
			// system variables
			for(SimpleVariable data: (Collection<? extends SimpleVariable>) getVariableHelperModel().getWrappedData()){
				if (data.getName().contains(filter.toUpperCase())) {
					list.add(data.getName());		
				}
			}
			// system data variables
			bean = BeanManager.getManagerBean(SystemData.class);
			criteria = new Criteria();
			
			//TODO ¿Utilizar las fechas del pojo activo?
			Date date = new Date();
			
			alias = bean.getFieldName(IPayrollAlias.SYSTEM_DATA_END_DATE);
			Expression ex1 = ExpressionUtilities.getNullExpression(alias);
			Expression ex2 = ExpressionUtilities.getGreaterThanOrEqualExpression(alias,date);
			criteria.addOrExpression( ExpressionUtilities.getOrExpression(ex1, ex2));
			for (ITransferObject to: bean.getList(criteria)) {
				SystemData data = (SystemData) to;
				if (data.getName().contains(filter.toUpperCase())) {
					list.add(data.getName());		
				}
			}
			return list;
		} catch (ManagerBeanException e) {
			LOGGER.error("error on expressionContext");
			return null;
		} 
	}
	
	@Override
	public IManagerBean getVariableManagerBean() throws ManagerBeanException {
		return BeanManager.getManagerBean(AgreementData.class);
	}
	@Override
	public void resetVariable() {
		setData(new AbstractVariableData());
		getData().setVariableData(new AgreementData());
		((AgreementData)getData().getVariableData()).setAgreement((Agreement) getController().getTo());
	}

}
