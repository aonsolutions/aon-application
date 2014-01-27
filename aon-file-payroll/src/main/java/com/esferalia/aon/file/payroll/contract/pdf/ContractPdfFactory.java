package com.esferalia.aon.file.payroll.contract.pdf;

import com.esferalia.aon.file.payroll.contract.pdf.annex.ModelPE229;
import com.esferalia.aon.file.payroll.contract.pdf.annex.ModelPE230;
import com.esferalia.aon.file.payroll.contract.pdf.basicCopy.BasicCopy;
import com.esferalia.aon.file.payroll.contract.pdf.extension.Extension;
import com.esferalia.aon.file.payroll.contract.pdf.model.ClausulasModel;
import com.esferalia.aon.file.payroll.contract.pdf.model.IndefiniteModel;
import com.esferalia.aon.file.payroll.contract.pdf.model.LearningModel;
import com.esferalia.aon.file.payroll.contract.pdf.model.PracticeModel;
import com.esferalia.aon.file.payroll.contract.pdf.model.TemporaryModel;


public class ContractPdfFactory {
	
	
	public IContractPdfDocument createContractDocument(String document) {
		// CONTRACT DOCUMENT
		if (document.equals(LearningModel.MODEL_NAME)) {
			return new LearningModel();
		} else if (document.equals(PracticeModel.MODEL_NAME)) {
			return new PracticeModel();
		} else if (document.equals(TemporaryModel.MODEL_NAME)) {
			return new TemporaryModel();
		} else if (document.equals(IndefiniteModel.MODEL_NAME)) {
			return new IndefiniteModel();
		}		
		// CLAUSES DOCUMENT
		if (document.equals(ClausulasModel.MODEL_NAME)) {
			return new ClausulasModel();
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

		return null;
	}
	
}
	
	