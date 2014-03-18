package com.esferalia.aon.ui.payroll.controller;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.file.payroll.contract.pdf.IContractFieldName;
import com.esferalia.aon.file.payroll.contract.pdf.IndefiniteCommonField;
import com.esferalia.aon.file.payroll.contract.pdf.IndefiniteOptionField;
import com.esferalia.aon.file.payroll.contract.pdf.LearningCommonField;
import com.esferalia.aon.file.payroll.contract.pdf.LearningOptionField;
import com.esferalia.aon.file.payroll.contract.pdf.ModelOption;
import com.esferalia.aon.file.payroll.contract.pdf.PracticeCommonField;
import com.esferalia.aon.file.payroll.contract.pdf.PracticeOptionField;
import com.esferalia.aon.file.payroll.contract.pdf.TemporaryCommonField;
import com.esferalia.aon.file.payroll.contract.pdf.TemporaryOptionField;
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
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ContractInfoController.class);

	private static final String CONTRACT_FIELDS_CONTROLLER = "contractDocumentInfo";

	private boolean commonInfo;
	
	private ContractModel contractModel;
	
	private List<ContractField> contractFieldList;
	
	private List<String> overridableFieldList;
	
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
		overridableFieldList = null;
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
		if(overridableFieldList == null){
			overridableFieldList = new LinkedList<String>();
			for(IContractFieldName fn: getOverridableFields(contract)){
				if(fn.isOverridable()){
					overridableFieldList.add(fn.toString());
				}
			}
			if(overridableFieldList.isEmpty()){
				overridableFieldList.add("");
			}
		}
		return overridableFieldList;
	}
	
	private List<IContractFieldName> getOverridableFields(Contract contract){
		List<IContractFieldName> list = new LinkedList<IContractFieldName>();
		String contractModelOption = ContractUtils.getInstance().getInfoCurrentValue(contract, ContractVariable.CONTRACT_MODEL_OPTION.getValue());
		if(contract!=null && StringUtils.isNotBlank(contractModelOption)){
			String option = contractModelOption.substring(contractModelOption.lastIndexOf("_")+1, contractModelOption.length()); 
			String contractModel = ModelOption.valueOf(contractModelOption).getPdfModel();
			if(IndefiniteModel.MODEL_NAME.equals(contractModel)){
				addAllCommonFields(list, IndefiniteCommonField.values());
				addAllOptionFields(list, IndefiniteOptionField.values(), option);
			} else if(TemporaryModel.MODEL_NAME.equals(contractModel)){
				addAllCommonFields(list, TemporaryCommonField.values());
				addAllOptionFields(list, TemporaryOptionField.values(), option);
			} else if(LearningModel.MODEL_NAME.equals(contractModel)){
				addAllCommonFields(list, LearningCommonField.values());
				addAllOptionFields(list, LearningOptionField.values(), option);
			} else if(PracticeModel.MODEL_NAME.equals(contractModel)){
				addAllCommonFields(list, PracticeCommonField.values());
				addAllOptionFields(list, PracticeOptionField.values(), option);
			}
		}
		return list;
	}
	private void addAllCommonFields(List<IContractFieldName> list , IContractFieldName[] fieldNameValues){
		for(IContractFieldName field: fieldNameValues){
			if(field.isOverridable()){
				if(field.toString().length()>32){
					LOGGER.error(" ######## ContractInfo ########  COMMON FIELD NAME TOO LONG -> " + field.toString());
				} else {
					list.add(field);
				}
			}
		}
	}
	private void addAllOptionFields(List<IContractFieldName> list , IContractFieldName[] fieldNameValues, String option){
		for(IContractFieldName field: fieldNameValues){
			if(field.toString().substring(0, field.toString().indexOf("_")).equals(option) && field.isOverridable()){
				if(field.toString().length()>32){
					LOGGER.error(" ######## ContractInfo ########  OPTION FIELD NAME TOO LONG -> " + field.toString());
				} else {
					list.add(field);
				}
			}
		}
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
		overridableFieldList = null;
		contractFieldList = new LinkedList<ContractField>();
		Contract contract = (Contract) FormUtil.getController(IPayrollConstants.CONTRACT_CONTROLLER).getTo();
		for(IContractFieldName fn: getOverridableFields(contract)){
			ContractField field = new ContractField();
			field.setContractInfo(obtainContractField(contract, fn));
			field.setFieldName(fn);
			contractFieldList.add(field);
		}
	}
	
	public void saveContractFields(){
		try {
			if(contractFieldList!=null){
				for(ContractField field: contractFieldList){
					if(StringUtils.isNotBlank(field.getContractInfo().getExpression())
							&& !StringUtils.equalsIgnoreCase(field.getContractInfo().getExpression(), "false")){
						this.getManagerBean().insertOrUpdate(field.getContractInfo());
					} else if(field.getContractInfo().getId()!=null){
						this.getManagerBean().remove(field.getContractInfo());
					}
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
	public static class ContractField implements Serializable {
		
		private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
		
		private final String MESSAGE_PREFIX = "payroll_contract_document_";
		private ContractInfo contractInfo;
		private IContractFieldName fieldName;
		public String getName() {
			return getMessage(this.contractInfo.getName());
		}
		public ContractInfo getContractInfo() {
			return contractInfo;
		}
		public void setContractInfo(ContractInfo contractInfo) {
			this.contractInfo = contractInfo;
		}
		public IContractFieldName getFieldName() {
			return fieldName;
		}
		public void setFieldName(IContractFieldName fieldName) {
			this.fieldName = fieldName;
		}
		public String getExpression(){
			return this.contractInfo.getExpression();
		}
		public void setExpression(String expression){
			this.contractInfo.setExpression(expression);
		}
		public boolean isCheck(){
			return fieldName!=null && fieldName.isCheck();
		}
		public boolean isComposite(){
			return fieldName!=null && fieldName.getCompositeValues()!=null && fieldName.getCompositeValues().length>0;
		}
		public List<SelectItem> getCompositeList(){
			List<SelectItem> list = new LinkedList<SelectItem>();
			for(IContractFieldName field: fieldName.getCompositeValues()){
				list.add(new SelectItem(field, getMessage(field.toString())));
			}
			return list;
		}
		public String getMessage(String key){
			try {
				return  AonUtil.getMessage(MESSAGE_PREFIX + key);
			} catch (Exception e) {
				LOGGER.error(" ######## ContractInfo ########  NO MESSAGE WAS DEFINED FOR FIELD NAME -> " + key);
				return key;
			}
		}
	}
	
}
