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
import com.esferalia.aon.payroll.AgreementLevel;
import com.esferalia.aon.payroll.AgreementLevelData;
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.payroll.ContractData;
import com.esferalia.aon.payroll.SystemData;
import com.esferalia.aon.payroll.dao.IPayrollAlias;

public class ContractVariableHandler extends AbstractVariableController {
	
	public ContractVariableHandler(IController controller) {
		super(controller);
	}
	
	
	@Override
	protected void initializeVariables(ActionEvent event) {
		try {
			Contract contract = ((Contract)getController().getTo());
			setVariablesModel(null);
			setUndefinedVariablesModel(null);
			IManagerBean bean = BeanManager.getManagerBean(ContractData.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(IPayrollAlias.CONTRACT_DATA_CONTRACT_ID), contract.getId());
			criteria.addOrder(bean.getFieldName(IPayrollAlias.CONTRACT_DATA_NAME));
			if(isSearchCurrentVariables()){
				Expression expr1 = ExpressionUtilities.getGreaterThanOrEqualExpression(bean.getFieldName(IPayrollAlias.CONTRACT_DATA_END_DATE), new Date());
				Expression expr2 = ExpressionUtilities.getNullExpression(bean.getFieldName(IPayrollAlias.CONTRACT_DATA_END_DATE));
				criteria.addExpression(ExpressionUtilities.getOrExpression(expr1, expr2));						
			} else {
				if(getInactiveDate()!=null){
					Expression expr1 = ExpressionUtilities.getGreaterThanOrEqualExpression(bean.getFieldName(IPayrollAlias.CONTRACT_DATA_END_DATE), getInactiveDate());
					Expression expr2 = ExpressionUtilities.getNullExpression(bean.getFieldName(IPayrollAlias.CONTRACT_DATA_END_DATE));
					criteria.addExpression(ExpressionUtilities.getOrExpression(expr1, expr2));						
				}
			}
			if(!StringUtils.isEmpty(getVariableFilter())){
				criteria.addEqualExpression(bean.getFieldName(IPayrollAlias.CONTRACT_DATA_NAME), getVariableFilter());
			}
			List<ContractData> dataList = null;
			dataList = new LinkedList<ContractData>();
			List<ITransferObject> list = bean.getList(criteria);
			if(!list.isEmpty()){
				for(ITransferObject to: list){
					dataList.add((ContractData) to);
				}
			}
			bean = BeanManager.getManagerBean(AgreementLevelData.class);
			String label = bean.getFieldName(IPayrollAlias.AGREEMENT_LEVEL_DATA_LEVEL_ID);
			if(contract.getAgreementLevelCategory()!=null && contract.getAgreementLevelCategory().getId()!=null){
				AgreementLevel level = contract.getAgreementLevelCategory().getLevel();
				criteria = new Criteria();
				criteria.addEqualExpression(label, level.getId());
				if(!StringUtils.isEmpty(getVariableFilter())){
					criteria.addEqualExpression(bean.getFieldName(IPayrollAlias.AGREEMENT_LEVEL_DATA_NAME), getVariableFilter());
				}
				criteria.addOrder(bean.getFieldName(IPayrollAlias.AGREEMENT_LEVEL_DATA_NAME));
				list = bean.getList(criteria);
				if(!list.isEmpty()){
					for(ITransferObject to: list){
						AgreementLevelData d = (AgreementLevelData) to;
						if(!existVariable(d, dataList)){
							ContractData data = new ContractData();
							data.setContract((Contract) getController().getTo());
							data.setName(d.getName());
							data.setStartDate(d.getStartDate());
							data.setEndDate(d.getEndDate());
							data.setExpression(d.getExpression());
							dataList.add(data);
						}
					}
				}
			}
			setVariablesModel(new ListDataModel(dataList));
		} catch (ManagerBeanException e) {
			String msg = "Imposible cargar las variables del contrato (" + e.getMessage() +")";
			LOGGER.error(msg);
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg,e);
		}
	}
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ContractVariableHandler.class.getName());
	@Override
	public List<?> expressionContext(Object suggest) {
		// CONTRACT variables
		try {
			String filter = (String) suggest;
			List<String> list = new LinkedList<String>();
			Contract contract = (Contract) getController().getTo();
			IManagerBean bean = BeanManager.getManagerBean(ContractData.class);
			Criteria criteria = new Criteria();  
			String alias = bean.getFieldName(IPayrollAlias.CONTRACT_DATA_CONTRACT_ID);
			criteria.addEqualExpression(alias, contract.getId());
			alias = bean.getFieldName(IPayrollAlias.CONTRACT_DATA_END_DATE);
			Expression expr1 = ExpressionUtilities.getGreaterThanOrEqualExpression(alias, new Date());
			Expression expr2 = ExpressionUtilities.getNullExpression(alias);
			criteria.addExpression(ExpressionUtilities.getOrExpression(expr1, expr2));			
			criteria.addOrder(alias, false);
			alias = bean.getFieldName(IPayrollAlias.CONTRACT_DATA_NAME);
			criteria.addOrder(alias);
			for(ITransferObject to: bean.getList(criteria)){
				ContractData data = (ContractData) to;
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
	protected IManagerBean getVariableManagerBean() throws ManagerBeanException {
		return BeanManager.getManagerBean(ContractData.class);
	}
	@Override
	protected void resetVariable() {
		setData(new ContractData());
		((ContractData)getData()).setContract((Contract) getController().getTo());
	}
	
	private boolean existVariable(AgreementLevelData d, List<ContractData> dataList) {
		for(ContractData data: dataList){
			if(data.getName().equals(d.getName())){
				return true;
			}
		}
		return false;
	}
	
}
