package com.esferalia.aon.file.payroll.contract.pdf.model;

import java.util.Collection;

import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.payroll.ContractAttachment;
import com.esferalia.aon.payroll.enumeration.ContractCode;


public interface IContractPdfModel {
	
	
	public byte[] buildPdf();

	public void loadPdfFields(ContractCode code, Contract contract) throws UnsupportedContractModelException;
	
	public void loadPdfFields(ContractAttachment contractPdfDraft);
	
	public Collection<ContractPdfField> getPdfFields();
	
	public Double getContractWidth();

	public Double getContractHeight();

	public Integer getNumberOfContractPages();
	
}
	
	