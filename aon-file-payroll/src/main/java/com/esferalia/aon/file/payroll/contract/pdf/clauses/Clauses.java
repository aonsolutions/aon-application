package com.esferalia.aon.file.payroll.contract.pdf.clauses;

import java.io.IOException;
import java.text.SimpleDateFormat;

import com.code.aon.common.ManagerBeanException;
import com.esferalia.aon.file.payroll.contract.pdf.UnsupportedContractDocumentException;
import com.esferalia.aon.file.payroll.contrata.IContrataParams;
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.payroll.enumeration.ContractCode;
import com.lowagie.text.pdf.PdfReader;



public class Clauses extends AbstractContractClauses {
	
	public final static String CLAUSES_NAME = "AdditionalClauses";
	
	public Clauses(){
		super.documentName = CLAUSES_NAME;
	}
	
	public void loadPdfFields(ContractCode code, Contract contract, IContrataParams contrataParams) throws UnsupportedContractDocumentException{
		// TODO
		
		try {
			PdfReader reader = new PdfReader(getContractClausesUrl(documentName+".pdf"));
			
			readPdfFields(reader);
			
			super.loadPdfCommonFields(contract);
			
			getPdfFieldsMap().get(CLAUSES_ENTERPRISE_NAME).setValue(contract.getWorkPlace().getEnterprise().getRegistry().getFullName());
			getPdfFieldsMap().get(CLAUSES_EMPLOYEE_NAME).setValue(contract.getPerson().getFullName());
			getPdfFieldsMap().get(CLAUSES_SIGN_TOWN).setValue(contract.getWorkPlace().getAddress().getCity());
			SimpleDateFormat dateFormatter = new SimpleDateFormat();
			dateFormatter.applyPattern("dd");
			getPdfFieldsMap().get(CLAUSES_SIGN_DAY).setValue(dateFormatter.format(contract.getStartDate()));
			dateFormatter.applyPattern("MMMM");
			getPdfFieldsMap().get(CLAUSES_SIGN_MONTH).setValue(dateFormatter.format(contract.getStartDate()));
			dateFormatter.applyPattern("yy");
			getPdfFieldsMap().get(CLAUSES_SIGN_YEAR).setValue(dateFormatter.format(contract.getStartDate()));
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ManagerBeanException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
	
	