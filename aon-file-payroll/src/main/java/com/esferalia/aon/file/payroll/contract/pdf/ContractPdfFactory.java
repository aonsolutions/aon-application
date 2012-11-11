package com.esferalia.aon.file.payroll.contract.pdf;

import com.esferalia.aon.file.payroll.contract.pdf.model.IContractPdfModel;
import com.esferalia.aon.file.payroll.contract.pdf.model.ModelPE151;
import com.esferalia.aon.file.payroll.contract.pdf.model.ModelPE170;
import com.esferalia.aon.file.payroll.contract.pdf.model.ModelPE176;
import com.esferalia.aon.file.payroll.contract.pdf.model.ModelPE177;
import com.esferalia.aon.file.payroll.contract.pdf.model.ModelPE179;
import com.esferalia.aon.file.payroll.contract.pdf.model.ModelPE183;
import com.esferalia.aon.file.payroll.contract.pdf.model.ModelPE226;


public class ContractPdfFactory {
	
	
//	private final static String PE170_MODEL = "PE170";
//	private final static String PE176_MODEL = "PE176";
//	private final static String PE177_MODEL = "PE177";
//	private final static String PE179_MODEL = "PE179";
//	private final static String PE183_MODEL = "PE183";
//	private final static String PE226_MODEL = "PE226";
	
	public IContractPdfModel createContractModel(String model) {
		if (model.equals(ModelPE151.MODEL_NAME)) {
			return new ModelPE151();
		} else if (model.equals(ModelPE170.MODEL_NAME)) {
			return new ModelPE170();
		} else if (model.equals(ModelPE176.MODEL_NAME)) {
			return new ModelPE176();
		} else if (model.equals(ModelPE177.MODEL_NAME)) {
			return new ModelPE177();
		} else if (model.equals(ModelPE179.MODEL_NAME)) {
			return new ModelPE179();
		} else if (model.equals(ModelPE183.MODEL_NAME)) {
			return new ModelPE183();
		} else if (model.equals(ModelPE226.MODEL_NAME)) {
			return new ModelPE226();
		}
		return null;
	}
	
}
	
	