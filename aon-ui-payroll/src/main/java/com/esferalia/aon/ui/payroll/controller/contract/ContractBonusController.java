package com.esferalia.aon.ui.payroll.controller.contract;

import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

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
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.payroll.BonusConcept;
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.payroll.ContractBonus;
import com.esferalia.aon.payroll.ContractData;
import com.esferalia.aon.payroll.ContractPayment;
import com.esferalia.aon.payroll.calculator.ContractSalaryCalculatorContext;
import com.esferalia.aon.payroll.dao.IPayrollAlias;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.expression.ExpressionContext;

public class ContractBonusController extends ContractDetailAbstractController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ContractBonusController.class.getName());
	
	private DataModel variablesModel;
	private ContractData contractData;
	private DataModel undefinedVariablesModel;
	
	
	public DataModel getUndefinedVariablesModel() {
		return undefinedVariablesModel;
	}
	public void setUndefinedVariablesModel(DataModel undefinedVariablesModel) {
		this.undefinedVariablesModel = undefinedVariablesModel;
	}
	public DataModel getVariablesModel() {
		return variablesModel;
	}
	public ContractData getContractData() {
		return contractData;
	}
	public void setContractData(ContractData contractData) {
		this.contractData = contractData;
	}

	@Override
	public void onSave(ActionEvent event) {
		IController master = FormUtil.getController("contract");
		Contract contract = (Contract) master.getTo();
		ContractBonus cd  = (ContractBonus) getTo();
		cd.setContract(contract);
		super.onSave(event);
	}
	
	@Override
	protected void initialiceConcepts() {
		setConcepts(new LinkedList<SelectItem>());
		try {
//			ContractBonus cb = (ContractBonus) getTo();
			IManagerBean bean = BeanManager.getManagerBean(BonusConcept.class);
			Criteria criteria = new Criteria();
//			criteria.addEqualExpression(bean.getFieldName(IPayrollAlias.PAYMENT_CONCEPT_TYPE), cb.getType());
			criteria.addOrder(bean.getFieldName(IPayrollAlias.BONUS_CONCEPT_DESCRIPTION));
			List<ITransferObject> list = bean.getList(criteria);
			for (ITransferObject to: list) {
				BonusConcept bc = (BonusConcept) to;
				getConcepts().add(new SelectItem(bc, bc.getDescription()));
			}
		} catch (ManagerBeanException e) {
			// Se devuelve la lista vacia.
		} 
	}
	
	public void onBonusConceptChange(ActionEvent event) {
		ContractPayment cp = (ContractPayment) getTo();
		if (cp.getType() != null && StringUtils.isEmpty(cp.getDescription())) {
			cp.setDescription( cp.getPaymentConcept().getDescription() );
		}
	}
	
	@Override
	public void onEdit(ActionEvent event) {
		super.onEdit(event);
		initializeVariables(event);
	}
	
	
	//**********************************************
	// VARIABLES
	//**********************************************
	
	private void initializeVariables(ActionEvent event) {
		ContractBonus bonus = (ContractBonus)this.getTo();
		Contract contract = bonus.getContract();
//		IController master = FormUtil.getController("contract");
//		Contract contract = (Contract) master.getTo();
		ContractSalaryCalculatorContext ctx;
		List<ContractData> dataList;
		try {
			ctx = (ContractSalaryCalculatorContext) contract.getSalaryCalculatorContext(new Date(), new Date(), new Date());
			variablesModel = null;
			undefinedVariablesModel = null;
			if(bonus.getExpression()!=null || bonus.getBonusConcept().getExpression()!=null){
				dataList = new LinkedList<ContractData>();
				Set<String> vl = ExpressionContext.getVariables(bonus.getExpression()==null?bonus.getBonusConcept().getExpression():bonus.getExpression());
				List<ContractData> undefined = new LinkedList<ContractData>();
				if(!vl.isEmpty()){
					for(String s: vl){
						List<ITransferObject> list = existingContractData(s, contract);
						if(!list.isEmpty()){
							for(ITransferObject to: list){
								dataList.add((ContractData) to);
							}
						} else {
							Calendar startCal = Calendar.getInstance();
							Calendar endCal = Calendar.getInstance();
							startCal.set(Calendar.DAY_OF_MONTH, startCal.getActualMinimum(Calendar.DAY_OF_MONTH));
							endCal.set(Calendar.DAY_OF_MONTH, endCal.getActualMaximum(Calendar.DAY_OF_MONTH));
							ContractData data = new ContractData();
							data.setContract(contract);
							data.setName(s);
							data.setStartDate(startCal.getTime());
							data.setEndDate(endCal.getTime());
							Object o = ctx.getExpressionContext().getVariable(s, startCal.getTime(), endCal.getTime(), Object.class);
							if(o==null){
								undefined.add(data);
							} else {
								data.setExpression(o.toString());
								dataList.add(data);
							}
						}
					}
				}
				variablesModel = new ListDataModel(dataList);
				if(!undefined.isEmpty()){
					undefinedVariablesModel = new ListDataModel(undefined);
				}
			}
		} catch (SalaryException e) {
			String msg = "Imposible cargar las variables del contrato (" + e.getMessage() +")";
			LOGGER.error(msg);
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg,e);
		} catch (ManagerBeanException e) {
			String msg = "Imposible cargar las variables del contrato (" + e.getMessage() +")";
			LOGGER.error(msg);
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg,e);
		}
	}
	
	private List<ITransferObject> existingContractData(String name, Contract contract) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(ContractData.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IPayrollAlias.CONTRACT_DATA_CONTRACT_ID), contract.getId());
		criteria.addEqualExpression(bean.getFieldName(IPayrollAlias.CONTRACT_DATA_NAME), name);
		return bean.getList(criteria);
	}
	
	public void onResetVariable(ActionEvent event) {
		setContractData(new ContractData());
		getContractData().setContract(((ContractBonus)this.getTo()).getContract());
	}
	public void onSelectVariable(ActionEvent event) {
		setContractData((ContractData) getVariablesModel().getRowData());
	}
	public void onSaveVariable(ActionEvent event) {
		try {
			IManagerBean bean = BeanManager.getManagerBean(ContractData.class);
			bean.insertOrUpdate(getContractData());
		} catch (ManagerBeanException e) {
			String msg = "Imposible guardar la variable del contrato (" + e.getMessage() +")";
			LOGGER.error(msg);
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg,e);
		}
		setContractData(null);
		initializeVariables(event);
	}
	public void onCancelVariable(ActionEvent event) {
		setContractData(null);
	}
	public void onRemoveVariable(ActionEvent event) {
		try {
			IManagerBean bean = BeanManager.getManagerBean(ContractData.class);
			bean.remove(getContractData());
		} catch (ManagerBeanException e) {
			String msg = "Imposible borrar la variable del contrato (" + e.getMessage() +")";
			LOGGER.error(msg);
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg,e);
		}
		setContractData(null);
		initializeVariables(event);
	}
	public void onAddUndefinedVariable(ActionEvent event) {
		setContractData((ContractData) getUndefinedVariablesModel().getRowData());
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
