package com.esferalia.aon.file.payroll.contract.pdf;

import java.util.Collection;

import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.payroll.ContractAttachment;
import com.esferalia.aon.payroll.enumeration.ContractCode;


public interface IContractPdfDocument {
	
	public byte[] buildPdf();

	public void loadPdfFields(ContractCode code, Contract contract) throws UnsupportedContractDocumentException;
	
	public void loadPdfFields(ContractAttachment contractPdfDraft);
	
	public Collection<ContractPdfField> getPdfFields();
	
	public Double getDocumentWidth();

	public Double getDocumentHeight();

	public Integer getNumberOfDocumentPages();
	
	public String getDocumentPath();
	
}
	
	