package com.esferalia.aon.file.payroll.contract.pdf.clauses;

import java.io.IOException;

import com.code.aon.common.ManagerBeanException;
import com.esferalia.aon.file.payroll.contract.pdf.UnsupportedContractDocumentException;
import com.esferalia.aon.file.payroll.contrata.ContrataParams;
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.payroll.enumeration.ContractCode;
import com.lowagie.text.pdf.PdfReader;



public class Clauses extends AbstractContractClauses {
	
	public final static String CLAUSES_NAME = "AdditionalClauses";
	
	public Clauses(){
		super.documentName = CLAUSES_NAME;
	}
	
	@Override
	public void loadPdfFields(ContractCode code, Contract contract, ContrataParams contrataParams) throws UnsupportedContractDocumentException{
		// TODO
		
		try {
			PdfReader reader = new PdfReader(getContractClausesUrl(documentName+".pdf"));
			
			readPdfFields(reader);
			
			super.loadPdfCommonFields(contract);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ManagerBeanException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
	
	