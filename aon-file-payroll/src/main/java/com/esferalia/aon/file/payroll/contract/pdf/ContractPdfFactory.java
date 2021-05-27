package com.esferalia.aon.file.payroll.contract.pdf;

import com.esferalia.aon.file.payroll.contract.pdf.annex.ModelPE229;
import com.esferalia.aon.file.payroll.contract.pdf.annex.ModelPE230;
import com.esferalia.aon.file.payroll.contract.pdf.basicCopy.BasicCopy;
import com.esferalia.aon.file.payroll.contract.pdf.enterpriseCertificate.EnterpriseCertificate;
import com.esferalia.aon.file.payroll.contract.pdf.enterpriseCertificate.EnterpriseCertificateSea;
import com.esferalia.aon.file.payroll.contract.pdf.extension.Extension;
import com.esferalia.aon.file.payroll.contract.pdf.internship.InternshipModel;
import com.esferalia.aon.file.payroll.contract.pdf.model.ClausulasModel;
import com.esferalia.aon.file.payroll.contract.pdf.model.IndefiniteModel;
import com.esferalia.aon.file.payroll.contract.pdf.model.LearningModel;
import com.esferalia.aon.file.payroll.contract.pdf.model.PracticeModel;
import com.esferalia.aon.file.payroll.contract.pdf.model.TemporaryModel;
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.payroll.enumeration.CCCType;


public class ContractPdfFactory<E> {
	
	
	public IContractPdfDocument<?> createContractDocument(Contract contract, String document) {
		// CONTRACT DOCUMENT
		if (document.equals(LearningModel.MODEL_NAME)) {
			return new LearningModel(contract);
		} else if (document.equals(PracticeModel.MODEL_NAME)) {
			return new PracticeModel(contract);
		} else if (document.equals(TemporaryModel.MODEL_NAME)) {
			return new TemporaryModel(contract);
		} else if (document.equals(IndefiniteModel.MODEL_NAME)) {
			return new IndefiniteModel(contract);
		} else if (document.equals(InternshipModel.MODEL_NAME)) {
			return new InternshipModel(contract);
		}		
		// CLAUSES DOCUMENT
		if (document.equals(ClausulasModel.MODEL_NAME)) {
			return new ClausulasModel(contract);
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
		// ENTERPRISE CERTIFICATE DOCUMENT
		if (document.equals(EnterpriseCertificate.ENTERPRISE_CERTIFICATE_NAME)) {
			if(contract.getEnterpriseCCC().getType()!=CCCType.AGRICULTURAL){
				return new EnterpriseCertificate();
			} else {
				return new EnterpriseCertificateSea();
			}
		} 

		return null;
	}
	
}
	
	