package com.esferalia.aon.ui.payroll.controller;

import java.util.LinkedList;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.form.BasicController;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.file.payroll.contract.pdf.model.AbstractContractModel;
import com.esferalia.aon.file.payroll.contract.pdf.model.AbstractContractModel.IContractFieldName;
import com.esferalia.aon.file.payroll.contract.pdf.model.ModelPE151;
import com.esferalia.aon.file.payroll.contract.pdf.model.ModelPE170;
import com.esferalia.aon.file.payroll.contract.pdf.model.ModelPE176;
import com.esferalia.aon.file.payroll.contract.pdf.model.ModelPE177;
import com.esferalia.aon.file.payroll.contract.pdf.model.ModelPE179;
import com.esferalia.aon.file.payroll.contract.pdf.model.ModelPE183;
import com.esferalia.aon.file.payroll.contract.pdf.model.ModelPE187;
import com.esferalia.aon.file.payroll.contract.pdf.model.ModelPE226;
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.payroll.ContractInfo;
import com.esferalia.aon.payroll.enumeration.ContractModel;

public class ContractInfoController extends BasicController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ContractInfoController.class);

	private static final String CONTRACT_FIELDS_CONTROLLER = "contractDocumentInfo";

	private boolean commonInfo;
	
	private Contract contract;
	
	private ContractModel contractModel;

	
	public boolean isCommonInfo() {
		return commonInfo;
	}
	public void setCommonInfo(boolean commonInfo) {
		this.commonInfo = commonInfo;
	}
	public Contract getContract() {
		return contract;
	}
	public void setContract(Contract contract) {
		this.contract = contract;
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
			}
			if(getContract()!=null && getContract().getId()!=null){
				this.getCriteria().addEqualExpression(this.getFieldName(IEntityAlias.CONTRACT_INFO_CONTRACT_ID), getContract().getId());
				if(this.getBeanName().equals(CONTRACT_FIELDS_CONTROLLER)){
					this.getCriteria().addInExpression(this.getFieldName(IEntityAlias.CONTRACT_INFO_NAME), getOverridableFieldNames());
				}
			}
		} catch (ManagerBeanException e) {
			LOGGER.error(">>>> Unable to clear criteria! ",e);
			addMessage(e.getMessage());
		}
		super.initializeModel();
	}
	
	@Override
	public void accept(ActionEvent event) {
		if(isCommonInfo()){
			((ContractInfo)this.getTo()).setContract(null);
		}
		if(getContract()!=null && getContract().getId()!=null){
			((ContractInfo)this.getTo()).setContract(getContract());
		}
		super.accept(event);
	}
	
	
	public List<SelectItem> getFieldNames(){
		List<SelectItem> list = new LinkedList<SelectItem>();
		for(IContractFieldName fn: getOverridableFields()){
			if(fn.isOverridable()){
				SelectItem item = new SelectItem(fn, fn.toString());
				list.add(item);
			}
		}
		return list;
	}

	public List<String> getOverridableFieldNames(){
		List<String> list = new LinkedList<String>();
		for(IContractFieldName fn: getOverridableFields()){
			if(fn.isOverridable()){
				list.add(fn.toString());
			}
		}
		if(list.isEmpty()){
			list.add("");
		}
		return list;
	}
	public List<IContractFieldName> getOverridableFields(){
		List<IContractFieldName> list = new LinkedList<IContractFieldName>();
		if(getContract()!=null && getContract().getModel()!=null){
			setContractModel(getContract().getModel());
			IContractFieldName[] fields = null;
			if(getContractModel().toString().equals(ModelPE151.MODEL_NAME)){
				fields = ModelPE151.PE151FieldName.values();
			} else if(getContractModel().toString().equals(ModelPE170.MODEL_NAME)){
				fields = ModelPE170.PE170FieldName.values();
			} else if(getContractModel().toString().equals(ModelPE176.MODEL_NAME)){
//				fields = ModelPE176.PE170FieldName.values();
			} else if(getContractModel().toString().equals(ModelPE177.MODEL_NAME)){
//				fields = ModelPE177.PE170FieldName.values();
			} else if(getContractModel().toString().equals(ModelPE179.MODEL_NAME)){
//				fields = ModelPE179.PE170FieldName.values();
			} else if(getContractModel().toString().equals(ModelPE183.MODEL_NAME)){
//				fields = ModelPE183.PE170FieldName.values();
			} else if(getContractModel().toString().equals(ModelPE187.MODEL_NAME)){
//				fields = ModelPE187.PE170FieldName.values();
			} else if(getContractModel().toString().equals(ModelPE226.MODEL_NAME)){
//				fields = ModelPE226.PE170FieldName.values();
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
	 * CONTRACT DEFAULT FIELD VALUES
	 */
//	public String getContractField() {
//		try {
//			if(this.getModel().isRowAvailable()){
//				
//			}
//		} catch (ManagerBeanException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		return null;
//	}
//	public void setContractField(String contractField) {
//		
//	}
//	public List<SelectItem> getContractModelFields() {
//		List<SelectItem> list = new LinkedList<SelectItem>();
//		if(getContractModel()!=null){			
//			for(IContractFieldName field: getOverridableFields()){
//				if(field.isOverridable()){
//					SelectItem item = new SelectItem(field, field.getValue());
//					list.add(item);
//				}
//			}
//		}
//		return list;
//	}	

	
	
	
}
