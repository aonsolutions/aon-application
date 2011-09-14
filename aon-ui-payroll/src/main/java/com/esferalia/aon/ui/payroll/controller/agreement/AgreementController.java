package com.esferalia.aon.ui.payroll.controller.agreement;


import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

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
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.payroll.Agreement;
import com.esferalia.aon.payroll.AgreementData;
import com.esferalia.aon.payroll.AgreementLevel;
import com.esferalia.aon.payroll.AgreementLevelData;
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.payroll.ContractData;
import com.esferalia.aon.payroll.SystemData;
import com.esferalia.aon.payroll.dao.IPayrollAlias;
import com.esferalia.aon.payroll.enumeration.ContractVariables;
import com.esferalia.aon.payroll.enumeration.InactiveLastPeriod;
import com.esferalia.aon.ui.payroll.controller.IPayrollConstants;
import com.esferalia.aon.ui.payroll.controller.contract.VariablesAbstractController;

public class AgreementController extends VariablesAbstractController {

	private static final Logger LOGGER = LoggerFactory.getLogger(AgreementController.class.getName());
		
	private boolean modalPanelVisible;
	
	private Date inactiveDate;
	private InactiveLastPeriod inactiveLastPeriod;
	private boolean searchCurrent;
	
	public boolean isSearchCurrent() {
		return searchCurrent;
	}
	public void setSearchCurrent(boolean searchCurrent) {
		this.searchCurrent = searchCurrent;
	}
	
	public InactiveLastPeriod getInactiveLastPeriod() {
		return inactiveLastPeriod;
	}
	public void setInactiveLastPeriod(InactiveLastPeriod inactiveLastPeriod) {
		this.inactiveLastPeriod = inactiveLastPeriod;
	}

	public Date getInactiveDate() {
		return inactiveDate;
	}
	public void setInactiveDate(Date inactiveDate) {
		this.inactiveDate = inactiveDate;
	}
	
	public boolean isModalPanelVisible() {
		return modalPanelVisible;
	}
	public void setModalPanelVisible(boolean modalPanelVisible) {
		this.modalPanelVisible = modalPanelVisible;
	}
	
	
	public void onShowVariables( ActionEvent event ) {
//		try {
//			Agreement to = (Agreement) getTo();
//			IController c = FormUtil.getController(IPayrollConstants.CONTRACT_DATA_CONTROLLER);
//			c.onEditSearch(event);
//			c.getCriteria().addEqualExpression(c.getFieldName(IPayrollAlias.CONTRACT_DATA_CONTRACT_ID), to.getId());
//			c.onSearch(event);
//			this.initializeVariables(event);
//		} catch (ManagerBeanException e) {
//			String msg = "Imposible mostrar las variables del contrato (" + e.getMessage() +")";
//			LOGGER.error(msg);
//			AonUtil.addErrorMessage(msg);
//			throw new AbortProcessingException(msg,e);
//		}						
		this.initializeVariables(event);
	}
	
	@Override
	public void onSaveVariable(ActionEvent event) {
		handleDataExpression();
		try {
			IManagerBean bean = BeanManager.getManagerBean(AgreementData.class);
			AgreementData d = new AgreementData();
			d.setAgreement((Agreement) getTo());
			d.setStartDate(getData().getStartDate());
			d.setEndDate(getData().getEndDate());
			d.setName(getData().getName());
			d.setExpression(getData().getExpression());
			bean.insertOrUpdate(d);
		} catch (ManagerBeanException e) {
			String msg = "Imposible guardar la variable del convenio (" + e.getMessage() +")";
			LOGGER.error(msg);
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg,e);
		}
		initEditor();
		initializeVariables(event);
	}
	
	private void initEditor(){
		setData(null);
		setCno(null);
		setQuoteGroup(null);
		setContractCode(null);
	}
	
	private void handleDataExpression() {
		if(getData().getVariable()==ContractVariables.CNO){
			getData().setExpression("\""+String.valueOf(getCno().ordinal())+"\"");
		}else if(getData().getVariable()==ContractVariables.TC2){
			getData().setExpression("\""+getContractCode().getValue()+"\"");
		}else if(getData().getVariable()==ContractVariables.CATEGORY){
			;
		}else if(getData().getVariable()==ContractVariables.QUOTE_GROUP){
			getData().setExpression("\""+getQuoteGroup().getValue()+"\"");
		}else if(getData().getVariable()==ContractVariables.OCCUPATION){
			getData().setExpression("\""+getOccupationType().getValue()+"\"");
		}else if(getData().getVariable()==ContractVariables.QUOTE_IT){
			;
		}
	}
	
	@Override
	protected void initializeVariables(ActionEvent event) {
		setVariablesModel(null);
		try {
			Agreement agreement = ((Agreement)getTo());
			setUndefinedVariablesModel(null);
			IManagerBean bean = BeanManager.getManagerBean(AgreementData.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(IPayrollAlias.AGREEMENT_DATA_AGREEMENT_ID), agreement.getId());
			criteria.addOrder(bean.getFieldName(IPayrollAlias.AGREEMENT_DATA_NAME));
			if(isSearchCurrent()){
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
			List<AgreementData> dataList = null;
			dataList = new LinkedList<AgreementData>();
			List<ITransferObject> list = bean.getList(criteria);
			if(!list.isEmpty()){
				for(ITransferObject to: list){
					dataList.add((AgreementData) to);
				}
			}
//			bean = BeanManager.getManagerBean(AgreementLevelData.class);
//			String label = bean.getFieldName(IPayrollAlias.AGREEMENT_LEVEL_DATA_LEVEL_ID);
//			if(contract.getAgreementLevelCategory()!=null && contract.getAgreementLevelCategory().getId()!=null){
//				AgreementLevel level = contract.getAgreementLevelCategory().getLevel();
//				criteria = new Criteria();
//				criteria.addEqualExpression(label, level.getId());
//				criteria.addOrder(bean.getFieldName(IPayrollAlias.AGREEMENT_LEVEL_DATA_NAME));
//				list = bean.getList(criteria);
//				if(!list.isEmpty()){
//					for(ITransferObject to: list){
//						AgreementLevelData d = (AgreementLevelData) to;
//						if(!existVariable(d, dataList)){
//							ContractData data = new ContractData();
//							data.setContract((Contract) this.getTo());
//							data.setName(d.getName());
//							data.setStartDate(d.getStartDate());
//							data.setEndDate(d.getEndDate());
//							data.setExpression(d.getExpression());
//							dataList.add(data);
//						}
//					}
//				}
//			}
			setVariablesModel(new ListDataModel(dataList));
		} catch (ManagerBeanException e) {
			String msg = "Imposible cargar las variables del contrato (" + e.getMessage() +")";
			LOGGER.error(msg);
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg,e);
		}
	}
	private boolean existVariable(AgreementLevelData d, List<ContractData> dataList) {
		for(ContractData data: dataList){
			if(data.getName().equals(d.getName())){
				return true;
			}
		}
		return false;
	}
	
	public void onChangeLastPeriod( ActionEvent event ) {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DAY_OF_MONTH, 1);
		if(getInactiveLastPeriod()==InactiveLastPeriod.LAST_MONTH){
			cal.add(Calendar.MONTH, -1);
		} else if(getInactiveLastPeriod()==InactiveLastPeriod.LAST_QUARTER){
			cal.add(Calendar.MONTH, -3);
		} else if(getInactiveLastPeriod()==InactiveLastPeriod.LAST_SEMESTER){
			cal.add(Calendar.MONTH, -6);
		} else if(getInactiveLastPeriod()==InactiveLastPeriod.LAST_YEAR){
			cal.add(Calendar.YEAR, -1);
		} else if(getInactiveLastPeriod()==InactiveLastPeriod.ALL){
			cal = null;
		}
		setInactiveDate(cal!=null?cal.getTime():null);
	}
	
	public void onChangeInactiveDate( ActionEvent event ) {
		if(getInactiveDate()==null && getInactiveLastPeriod()!=InactiveLastPeriod.ALL){
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.MONTH, -1);
			setInactiveDate(cal!=null?cal.getTime():null);
		}
	}
	
	public void reloadData( ActionEvent event ) {
		initializeVariables(event);
	}
	
	public List<?> expressionContext(Object suggest) {
		// CONTRACT variables
		try {
			String filter = (String) suggest;
			List<String> list = new LinkedList<String>();
			Contract contract = (Contract) this.getTo();
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
	
	private DataModel variablesModel;
	
	public DataModel getVariablesModel() {
		return variablesModel;
	}
	public void setVariablesModel(DataModel variablesModel) {
		this.variablesModel = variablesModel;
	}
	
}
