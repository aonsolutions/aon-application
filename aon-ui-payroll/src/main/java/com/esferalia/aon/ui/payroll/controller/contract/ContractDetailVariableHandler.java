package com.esferalia.aon.ui.payroll.controller.contract;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.faces.event.ActionEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.IController;
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.payroll.ContractData;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.ui.payroll.controller.IPayrollConstants;

public abstract class ContractDetailVariableHandler extends AbstractVariableHandler {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ContractDetailVariableHandler.class.getName());
	
	public ContractDetailVariableHandler(IController controller) {
		super(controller);
	}

	@Override
	public List<?> expressionContext(Object suggest) {
		try {
			String filter = (String) suggest;
			List<String> list = new LinkedList<String>();
			IController master = FormUtil.getController(IPayrollConstants.CONTRACT_CONTROLLER);
			Contract contract = (Contract) master.getTo();
			IManagerBean bean = BeanManager.getManagerBean(ContractData.class);
			Criteria criteria = new Criteria();  
			String alias = bean.getFieldName(IEntityAlias.CONTRACT_DATA_CONTRACT_ID);
			criteria.addEqualExpression(alias, contract.getId());
			alias = bean.getFieldName(IEntityAlias.CONTRACT_DATA_END_DATE);
			Expression expr1 = ExpressionUtilities.getGreaterThanOrEqualExpression(alias, new Date());
			Expression expr2 = ExpressionUtilities.getNullExpression(alias);
			criteria.addExpression(ExpressionUtilities.getOrExpression(expr1, expr2));			
			criteria.addOrder(alias, false);
			alias = bean.getFieldName(IEntityAlias.CONTRACT_DATA_NAME);
			criteria.addOrder(alias);
			for(ITransferObject to: bean.getList(criteria)){
				ContractData data = (ContractData) to;
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
	
	public List<ITransferObject> existingContractData(String name, Contract contract) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(ContractData.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_DATA_CONTRACT_ID), contract.getId());
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_DATA_NAME), name);
		return bean.getList(criteria);
	}
	
	@Override
	public IManagerBean getVariableManagerBean() throws ManagerBeanException {
		return BeanManager.getManagerBean(ContractData.class);
	}
	
	@Override
	public abstract void resetVariable() ;
	
	@Override
	public abstract void initializeVariables(ActionEvent event) ;
	
}
