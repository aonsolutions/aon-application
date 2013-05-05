package com.esferalia.aon.file.payroll.contract.pdf.basicCopy;

import java.io.IOException;

import com.code.aon.common.ManagerBeanException;
import com.esferalia.aon.file.payroll.contract.pdf.UnsupportedContractDocumentException;
import com.esferalia.aon.file.payroll.contrata.ContrataParams;
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.payroll.enumeration.ContractCode;
import com.lowagie.text.pdf.PdfReader;



public class BasicCopy extends AbstractContractBasicCopy {
	
	public final static String BASIC_COPY_NAME = "ContractBasicCopy";
	
	public BasicCopy(){
		super.documentName = BASIC_COPY_NAME;
	}
	
	@Override
	public void loadPdfFields(ContractCode code, Contract contract, ContrataParams contrataParams) throws UnsupportedContractDocumentException{
		// TODO
		
		try {
			PdfReader reader = new PdfReader(getContractBasicCopyUrl(documentName+".pdf"));
			
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
	
	