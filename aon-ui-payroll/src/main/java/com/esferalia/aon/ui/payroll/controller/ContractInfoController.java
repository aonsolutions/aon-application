package com.esferalia.aon.ui.payroll.controller;

import java.util.LinkedList;
import java.util.List;

import javax.faces.event.ActionEvent;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.file.payroll.contract.pdf.model.AbstractContractModel.IContractFieldName;
import com.esferalia.aon.file.payroll.contract.pdf.model.AbstractContractModel.ModelOption;
import com.esferalia.aon.file.payroll.contract.pdf.model.IndefiniteModel;
import com.esferalia.aon.file.payroll.contract.pdf.model.LearningModel;
import com.esferalia.aon.file.payroll.contract.pdf.model.PracticeModel;
import com.esferalia.aon.file.payroll.contract.pdf.model.TemporaryModel;
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.payroll.ContractInfo;
import com.esferalia.aon.payroll.ContractInfo.ContractVariable;
import com.esferalia.aon.payroll.enumeration.ContractModel;
import com.esferalia.aon.ui.payroll.controller.contract.ContractController;
import com.esferalia.aon.ui.payroll.utils.ContractUtils;

public class ContractInfoController extends BasicController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ContractInfoController.class);

	private static final String CONTRACT_FIELDS_CONTROLLER = "contractDocumentInfo";

	private boolean commonInfo;
	
	private ContractModel contractModel;
	
	private List<ContractField> contractFieldList;
	
	public List<ContractField> getContractFieldList() {
		return contractFieldList;
	}
	public void setContractFieldList(List<ContractField> contractFieldList) {
		this.contractFieldList = contractFieldList;
	}

	
	public boolean isCommonInfo() {
		return commonInfo;
	}
	public void setCommonInfo(boolean commonInfo) {
		this.commonInfo = commonInfo;
	}
	public ContractModel getContractModel() {
		return contractModel;
	}
	public void setContractModel(ContractModel contractModel) {
		this.contractModel = contractModel;
	}
	
	@Override
	public void initializeModel() {
		try {
			if(isCommonInfo()){
				this.getCriteria().addNullExpression("ContractInfo.contract");
			} else {
				Contract contract = (Contract) FormUtil.getController(IPayrollConstants.CONTRACT_CONTROLLER).getTo();
				this.getCriteria().addEqualExpression(this.getFieldName(IEntityAlias.CONTRACT_INFO_CONTRACT_ID), contract.getId());
				if(this.getBeanName().equals(CONTRACT_FIELDS_CONTROLLER)){
					this.getCriteria().addInExpression(this.getFieldName(IEntityAlias.CONTRACT_INFO_NAME), getOverridableFieldNames(contract));
				}
			}
		} catch (ManagerBeanException e) {
			LOGGER.error(">>>> Unable to clear criteria! ",e);
			addMessage(e.getMessage());
		}
		super.initializeModel();
	}

	private List<String> getOverridableFieldNames(Contract contract){
		List<String> list = new LinkedList<String>();
		for(IContractFieldName fn: getOverridableFields(contract)){
			if(fn.isOverridable()){
				list.add(fn.toString());
			}
		}
		if(list.isEmpty()){
			list.add("");
		}
		return list;
	}
	
	private List<IContractFieldName> getOverridableFields(Contract contract){
		List<IContractFieldName> list = new LinkedList<IContractFieldName>();
		String contractModelOption = ContractUtils.getInstance().getInfoCurrentValue(contract, ContractVariable.CONTRACT_MODEL_OPTION.getValue());
		if(contract!=null && StringUtils.isNotBlank(contractModelOption)){
			String contractModel = ModelOption.valueOf(contractModelOption).getPdfModel();
			IContractFieldName[] fields = null;
			if(IndefiniteModel.MODEL_NAME.equals(contractModel)){
				fields = (IContractFieldName[]) ArrayUtils.addAll(fields, (IContractFieldName[]) IndefiniteModel.IndefiniteCommonFieldName.values());
//				fields = (IContractFieldName[]) ArrayUtils.addAll(fields, (IContractFieldName[]) IndefiniteModel.IndefiniteOptionFieldName.values());
			} else if(TemporaryModel.MODEL_NAME.equals(contractModel)){
				fields = (IContractFieldName[]) ArrayUtils.addAll(fields, (IContractFieldName[]) TemporaryModel.TemporaryCommonFieldName.values());
//				fields = (IContractFieldName[]) ArrayUtils.addAll(fields, (IContractFieldName[]) TemporaryModel.TemporaryOptionFieldName.values());
			} else if(LearningModel.MODEL_NAME.equals(contractModel)){
				fields = (IContractFieldName[]) ArrayUtils.addAll(fields, (IContractFieldName[]) LearningModel.LearningCommonFieldName.values());
//				fields = (IContractFieldName[]) ArrayUtils.addAll(fields, (IContractFieldName[]) LearningModel.LearningOptionFieldName.values());
			} else if(PracticeModel.MODEL_NAME.equals(contractModel)){
				fields = (IContractFieldName[]) ArrayUtils.addAll(fields, (IContractFieldName[]) PracticeModel.PracticeCommonFieldName.values());
//				fields = (IContractFieldName[]) ArrayUtils.addAll(fields, (IContractFieldName[]) PracticeModel.PracticeOptionFieldName.values());
			}
			if(fields != null){
				for(IContractFieldName field: fields){
					if(field.isOverridable()){
						list.add(field);
					}
				}
			}
		}
		return list;
	}
	
	/*
	 * Contract especific field functions
	 */
	
	public ContractInfo obtainContractField(Contract contract, IContractFieldName fieldName){
		ContractInfo contractInfo  = null;
		try {
			Criteria criteria = new Criteria(); 
			criteria.addEqualExpression(this.getFieldName(IEntityAlias.CONTRACT_INFO_CONTRACT_ID), contract.getId());
			if(this.getBeanName().equals(CONTRACT_FIELDS_CONTROLLER)){
				criteria.addInExpression(this.getFieldName(IEntityAlias.CONTRACT_INFO_NAME), getOverridableFieldNames(contract));
			}
			criteria.addEqualExpression(this.getFieldName(IEntityAlias.CONTRACT_INFO_NAME), fieldName.toString());
			List<ITransferObject> list = this.getManagerBean().getList(criteria);
			if(!list.isEmpty()){
				contractInfo = (ContractInfo) list.get(0); 
				if(StringUtils.isNotBlank(contractInfo.getExpression())){
					contractInfo.setExpression(contractInfo.getExpression().replace("\"", ""));
				}
			}
		} catch (ManagerBeanException e) {
			LOGGER.error(">>>> Unable to load contract document fields from contract_info ",e);
			addMessage(e.getMessage());
		}
		if(contractInfo == null){
			contractInfo = new ContractInfo();
			contractInfo.setContract(contract);
			contractInfo.setStartDate(contract.getStartDate());
			contractInfo.setEndDate(contract.getEndDate());
			contractInfo.setName(fieldName.toString());
		}
		return contractInfo;
	}
	
	public void onLoadContractFields(ActionEvent event){
		contractFieldList = new LinkedList<ContractField>();
		Contract contract = (Contract) FormUtil.getController(IPayrollConstants.CONTRACT_CONTROLLER).getTo();
		for(IContractFieldName fn: getOverridableFields(contract)){
			ContractField field = new ContractField();
			field.setContractInfo(obtainContractField(contract, fn));
			contractFieldList.add(field);
		}
	}
	
	public void saveContractFields(){
		try {
			if(contractFieldList!=null){
				for(ContractField field: contractFieldList){
					this.getManagerBean().insertOrUpdate(field.getContractInfo());
				}
			}
		} catch (ManagerBeanException e) {
			LOGGER.error("No se han podido guardar los datos correctamente.",e);
			AonUtil.addErrorMessage("No se han podido guardar los datos correctamente.");
			AonUtil.addErrorMessage(e.getMessage());
		}
	}
	
	public void onSaveContractModel(ActionEvent event) throws ManagerBeanException{
		IManagerBean bean = BeanManager.getManagerBean(ContractInfo.class);
		ContractController controller = (ContractController) FormUtil.getController(IPayrollConstants.CONTRACT_CONTROLLER);
		Contract contract = (Contract) controller.getTo();
		try {
			if(controller.getParams().getContractModelOption()!=null){
				ContractInfo info = new ContractInfo();
				info.setContract(contract);
				info.setStartDate(contract.getStartDate());
				info.setEndDate(contract.getEndDate());
				info.setName( ContractVariable.CONTRACT_MODEL_OPTION.getValue() );
				info.setExpression("\"" + controller.getParams().getContractModelOption() + "\"");
				bean.insert(info);
			}
		} catch (ManagerBeanException e) {
			String msg = "Error al grabar el modelo del contrato. (" +e.getMessage() + ")";
			AonUtil.addErrorMessage(msg);
		}
	}
	
	/*
	 * INNER CLASSES
	 */
	public class ContractField {
		private ContractInfo contractInfo;
		public String getName() {
			String label = null;
			try {
				label = AonUtil.getMessage("payroll_contract_document_"+this.contractInfo.getName());
			} catch (Exception e) {
				label = this.contractInfo.getName();
			}
			return label;
		}
		public ContractInfo getContractInfo() {
			return contractInfo;
		}
		public void setContractInfo(ContractInfo contractInfo) {
			this.contractInfo = contractInfo;
		}
		public String getExpression(){
			return this.contractInfo.getExpression();
		}
		public void setExpression(String expression){
			this.contractInfo.setExpression(expression);
		}
	}
	
}
