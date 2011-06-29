package com.esferalia.aon.ui.payroll.controller.contract;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import javax.faces.model.SelectItem;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.payroll.ContractData;
import com.esferalia.aon.payroll.dao.IPayrollAlias;
import com.esferalia.aon.ui.payroll.controller.IPayrollConstants;

public abstract class ContractDetailAbstractController extends BasicController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ContractDetailAbstractController.class.getName());
	
	private List<SelectItem> concepts;
	private boolean modalPanelVisible;
	private ContractData data;
	private DataModel variablesModel;
	private DataModel undefinedVariablesModel;
	private boolean searchCurrent;
	
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
	public ContractData getData() {
		return data;
	}
	public void setData(ContractData data) {
		this.data = data;
	}
	public DataModel getVariablesModel() {
		return variablesModel;
	}
	public void setVariablesModel(DataModel variablesModel) {
		this.variablesModel = variablesModel;
	}
	public DataModel getUndefinedVariablesModel() {
		return undefinedVariablesModel;
	}
	public void setUndefinedVariablesModel(DataModel undefinedVariablesModel) {
		this.undefinedVariablesModel = undefinedVariablesModel;
	}

	public List<?> expressionContext(Object suggest) {
		try {
			String filter = (String) suggest;
			List<String> list = new LinkedList<String>();
			IController master = FormUtil.getController(IPayrollConstants.CONTRACT_CONTROLLER);
			Contract contract = (Contract) master.getTo();
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
			return list;
		} catch (ManagerBeanException e) {
			return null;
		} 
	}
	public void onEdit(ActionEvent event) {
		super.onSelect(event);
		reset(true);
		initializeVariables(event);
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
	protected abstract void completeCiteria();
	
	
	//**********************************************
	// VARIABLES
	//**********************************************
	
	protected abstract void initializeVariables(ActionEvent event);
	
	protected List<ITransferObject> existingContractData(String name, Contract contract) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(ContractData.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IPayrollAlias.CONTRACT_DATA_CONTRACT_ID), contract.getId());
		criteria.addEqualExpression(bean.getFieldName(IPayrollAlias.CONTRACT_DATA_NAME), name);
		return bean.getList(criteria);
	}
	
	public void onResetVariable(ActionEvent event) {
		setData(new ContractData());
		IController master = FormUtil.getController(IPayrollConstants.CONTRACT_CONTROLLER);
		getData().setContract((Contract) master.getTo());
	}
	public void onSelectVariable(ActionEvent event) {
		setData((ContractData) getVariablesModel().getRowData());
	}
	public void onSaveVariable(ActionEvent event) {
		try {
			IManagerBean bean = BeanManager.getManagerBean(ContractData.class);
			bean.insertOrUpdate(getData());
		} catch (ManagerBeanException e) {
			String msg = "Imposible guardar la variable del contrato (" + e.getMessage() +")";
			LOGGER.error(msg);
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg,e);
		}
		setData(null);
		initializeVariables(event);
	}
	public void onCancelVariable(ActionEvent event) {
		setData(null);
	}
	public void onRemoveVariable(ActionEvent event) {
		try {
			IManagerBean bean = BeanManager.getManagerBean(ContractData.class);
			bean.remove(getData());
		} catch (ManagerBeanException e) {
			String msg = "Imposible borrar la variable del contrato (" + e.getMessage() +")";
			LOGGER.error(msg);
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg,e);
		}
		setData(null);
		initializeVariables(event);
	}
	public void onAddUndefinedVariable(ActionEvent event) {
		setData((ContractData) getUndefinedVariablesModel().getRowData());
	}
	@SuppressWarnings("unchecked")
	public List<SelectItem> getNewVariableList(){
		List<SelectItem> list = new LinkedList<SelectItem>();
		if(getVariablesModel()!=null){
			for(ContractData data: (List<ContractData>)getVariablesModel().getWrappedData()){
				String name = data.getName();
				SelectItem item = new SelectItem(name, name);
				list.add(item);
			}
		}
		if(getUndefinedVariablesModel()!=null){
			for(ContractData data: (List<ContractData>)getUndefinedVariablesModel().getWrappedData()){
				String name = data.getName();
				SelectItem item = new SelectItem(name, name);
				list.add(item);
			}
		}
		return list;
	}
	
}
