package com.esferalia.aon.ui.payroll.controller.contract;

import java.util.LinkedList;
import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.IResourceable;
import com.code.aon.common.enumeration.IStringEnum;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.payroll.ContractData;
import com.esferalia.aon.payroll.dao.IPayrollAlias;
import com.esferalia.aon.payroll.enumeration.CNO;
import com.esferalia.aon.payroll.enumeration.ContractCode;
import com.esferalia.aon.payroll.enumeration.ContractVariables;
import com.esferalia.aon.payroll.enumeration.QuoteGroup;
import com.esferalia.aon.payroll.enumeration.VariableType;
import com.esferalia.aon.ui.payroll.controller.IPayrollConstants;
import com.esferalia.aon.ui.payroll.controller.PayrollVariablesCollectionsController;

public abstract class VariablesAbstractController extends BasicController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(VariablesAbstractController.class.getName());
	
	private ContractData data;
	private DataModel variablesModel;
	private DataModel undefinedVariablesModel;
	
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
	
	protected List<ITransferObject> existingContractData(String name, Contract contract) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(ContractData.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IPayrollAlias.CONTRACT_DATA_CONTRACT_ID), contract.getId());
		criteria.addEqualExpression(bean.getFieldName(IPayrollAlias.CONTRACT_DATA_NAME), name);
		return bean.getList(criteria);
	}
	
	public ContractData getRowVariable(){
		if(getVariablesModel().getRowIndex()>=0){
			return (ContractData) getVariablesModel().getRowData();
		}
		return null;
	}
	
	public void onResetVariable(ActionEvent event) {
		setData(new ContractData());
		IController master = FormUtil.getController(IPayrollConstants.CONTRACT_CONTROLLER);
		getData().setContract((Contract) master.getTo());
	}
	public void onSelectVariable(ActionEvent event) {
		setData((ContractData) getVariablesModel().getRowData());
		handleEditorExpression(getData().getExpression());
	}
	
	public void onSaveVariable(ActionEvent event) {
		handleDataExpression();
		try {
			IManagerBean bean = BeanManager.getManagerBean(ContractData.class);
			bean.insertOrUpdate(getData());
		} catch (ManagerBeanException e) {
			String msg = "Imposible guardar la variable del contrato (" + e.getMessage() +")";
			LOGGER.error(msg);
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg,e);
		}
		initEditor();
		initializeVariables(event);
	}
	public void onCancelVariable(ActionEvent event) {
		initEditor();
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
		initEditor();
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
	
	protected abstract void initializeVariables(ActionEvent event);
	
	
	//******************************************************
	// VARIABLEs EDITOR
	//******************************************************
	private boolean modalHelperPanelVisible;
	
	public boolean isModalHelperPanelVisible() {
		return modalHelperPanelVisible;
	}
	public void setModalHelperPanelVisible(boolean modalHelperPanelVisible) {
		this.modalHelperPanelVisible = modalHelperPanelVisible;
	}
	public Object getExpression() {
		return getData()!=null?getObjectExpression(getData().getExpression()):null;
	}
	public void setExpression(Object expression) {
		getData().setExpression(getStringExpression(expression));
	} 
	
	public List<?> getVariablesCollection() {
		PayrollVariablesCollectionsController c = new PayrollVariablesCollectionsController();
		if(getData().getVariable()==ContractVariables.CNO){
			return c.getCnoList();
		}else if(getData().getVariable()==ContractVariables.TC2){
			return c.getTc2List();
		}else if(getData().getVariable()==ContractVariables.CATEGORY){
			return c.getCategoryList();
		}else if(getData().getVariable()==ContractVariables.QUOTE_GROUP){
			 return c.getQuoteGroupList();
		}
		return null;
	}
	
	private String getStringExpression(Object expression) {
		if (expression instanceof Enum<?>) {
			if (expression instanceof IResourceable) {
//				Enum<?> v = (Enum<?>) expression;
//				return v.toString();
				if (expression instanceof IStringEnum) {
					IStringEnum v = (IStringEnum) expression;
					return "\""+v.getValue()+"\"";
				} else {
					Enum<?> v = (Enum<?>) expression;
					return "\""+v.toString()+"\"";
				}
			}
		}
		return expression.toString();
	}
	
	private Object getObjectExpression(String expression) {
		if( StringUtils.startsWith(expression, "\"") && StringUtils.endsWith(expression, "\"")){
			expression = expression.substring(1, expression.length()-1);
		}
		return expression;
	}
	
	private CNO cno;
	private ContractCode contractCode;
	private QuoteGroup quoteGroup;
	private DataModel variableHelperModel;

	public CNO getCno() {
		return cno;
	}
	public void setCno(CNO cno) {
		this.cno = cno;
	}
	public ContractCode getContractCode() {
		return contractCode;
	}
	public void setContractCode(ContractCode contractCode) {
		this.contractCode = contractCode;
	}
	public QuoteGroup getQuoteGroup() {
		return quoteGroup;
	}
	public void setQuoteGroup(QuoteGroup quoteGroup) {
		this.quoteGroup = quoteGroup;
	}
	
	public DataModel getVariableHelperModel() {
		if(variableHelperModel == null){
			variableHelperModel = new ListDataModel(getVariableHelpList());
		}
		return variableHelperModel;
	}
	public void setVariableHelperModel(DataModel variableHelperModel) {
		this.variableHelperModel = variableHelperModel;
	}
	private List<String> getVariableHelpList() {
		List<String> list = new LinkedList<String>();
		for(ContractVariables v: ContractVariables.values()){
			list.add(v.getName());
		}
		return list;
	}
	public void onShowVariableHelp(ActionEvent event) {
		setModalHelperPanelVisible(true);
	}
	public void onCancelVariableHelp(ActionEvent event) {
		setModalHelperPanelVisible(false);
	}
	public void onSelectHelpVariable(ActionEvent event) {
		onCancelVariableHelp(event);
		getData().setName((String) getVariableHelperModel().getRowData());
	}
	
	private void initEditor(){
		setData(null);
		setCno(null);
		setQuoteGroup(null);
		setContractCode(null);
	}
	private void handleEditorExpression(String expression) {
		if( StringUtils.startsWith(expression, "\"") && StringUtils.endsWith(expression, "\"")){
			expression = expression.substring(1, expression.length()-1);
		}
		if(getData().getVariable()==ContractVariables.CNO){
			setCno(CNO.getCnoByValue(expression));
		}else if(getData().getVariable()==ContractVariables.TC2){
			setContractCode(ContractCode.getContractCodeByValue(expression));
		}else if(getData().getVariable()==ContractVariables.QUOTE_GROUP){
			setQuoteGroup(QuoteGroup.getQuoteGroupByValue(expression));
		}
		
	}
	private void handleDataExpression() {
		if(getData().getVariable()==ContractVariables.CNO){
			getData().setExpression("\""+String.valueOf(getCno().ordinal())+"\"");
		}else if(getData().getVariable()==ContractVariables.TC2){
			getData().setExpression("\""+getContractCode().getValue()+"\"");
		}else if(getData().getVariable()==ContractVariables.QUOTE_GROUP){
			getData().setExpression("\""+getQuoteGroup().getValue()+"\"");
		}
	}
	
	public class Variable2{
		private ContractData data;
		private VariableType type;
		public ContractData getData() {
			return data;
		}
		public void setData(ContractData data) {
			this.data = data;
		}
		public VariableType getType() {
			return type;
		}
		public void setType(VariableType type) {
			this.type = type;
		}
	}
	
	
}
