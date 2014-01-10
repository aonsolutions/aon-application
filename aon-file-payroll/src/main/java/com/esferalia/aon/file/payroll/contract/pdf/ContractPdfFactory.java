package com.esferalia.aon.file.payroll.contract.pdf;

import javax.swing.text.html.HTMLDocument.HTMLReader.PreAction;

import com.esferalia.aon.file.payroll.contract.pdf.annex.ModelPE229;
import com.esferalia.aon.file.payroll.contract.pdf.annex.ModelPE230;
import com.esferalia.aon.file.payroll.contract.pdf.basicCopy.BasicCopy;
import com.esferalia.aon.file.payroll.contract.pdf.clauses.Clauses;
import com.esferalia.aon.file.payroll.contract.pdf.extension.Extension;
import com.esferalia.aon.file.payroll.contract.pdf.model.IndefiniteModel;
import com.esferalia.aon.file.payroll.contract.pdf.model.LearningModel;
import com.esferalia.aon.file.payroll.contract.pdf.model.ModelPE151;
import com.esferalia.aon.file.payroll.contract.pdf.model.ModelPE170;
import com.esferalia.aon.file.payroll.contract.pdf.model.ModelPE176;
import com.esferalia.aon.file.payroll.contract.pdf.model.ModelPE177;
import com.esferalia.aon.file.payroll.contract.pdf.model.ModelPE179;
import com.esferalia.aon.file.payroll.contract.pdf.model.ModelPE183;
import com.esferalia.aon.file.payroll.contract.pdf.model.ModelPE187;
import com.esferalia.aon.file.payroll.contract.pdf.model.ModelPE226;
import com.esferalia.aon.file.payroll.contract.pdf.model.PracticeModel;
import com.esferalia.aon.file.payroll.contract.pdf.model.TemporaryModel;


public class ContractPdfFactory {
	
	
	public IContractPdfDocument createContractDocument(String document) {
		// CONTRACT DOCUMENT
//		if (document.equals(ModelPE151.MODEL_NAME)) {
//			return new ModelPE151();
//		} else if (document.equals(ModelPE170.MODEL_NAME)) {
//			return new ModelPE170();
//		} else if (document.equals(ModelPE176.MODEL_NAME)) {
//			return new ModelPE176();
//		} else if (document.equals(ModelPE177.MODEL_NAME)) {
//			return new ModelPE177();
//		} else if (document.equals(ModelPE179.MODEL_NAME)) {
//			return new ModelPE179();
//		} else if (document.equals(ModelPE183.MODEL_NAME)) {
//			return new ModelPE183();
//		} else if (document.equals(ModelPE187.MODEL_NAME)) {
//			return new ModelPE187();
//		} else if (document.equals(ModelPE226.MODEL_NAME)) {
//			return new ModelPE226();
//		}
		if (document.equals(LearningModel.MODEL_NAME)) {
			return new LearningModel();
		} else if (document.equals(PracticeModel.MODEL_NAME)) {
			return new PracticeModel();
		} else if (document.equals(TemporaryModel.MODEL_NAME)) {
			return new TemporaryModel();
		} else if (document.equals(IndefiniteModel.MODEL_NAME)) {
			return new IndefiniteModel();
		}
		
		
		
		
		
		// BASIC COPY DOCUMENT
		if (document.equals(BasicCopy.BASIC_COPY_NAME)) {
			return new BasicCopy();
		} 
		// ANNEX DOCUMENT
		if (document.equals(ModelPE229.MODEL_NAME)) {
			return new ModelPE229();
		} else if (document.equals(ModelPE230.MODEL_NAME)) {
				return new ModelPE230();
		}
		// EXTENSION DOCUMENT
		if (document.equals(Extension.EXTENSION_NAME)) {
			return new Extension();
		} 
		// CLAUSES DOCUMENT
		if (document.equals(Clauses.CLAUSES_NAME)) {
			return new Clauses();
		} 
		return null;
	}
	
}
	
	