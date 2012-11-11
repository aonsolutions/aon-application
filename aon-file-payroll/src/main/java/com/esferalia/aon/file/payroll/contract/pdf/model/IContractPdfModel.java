package com.esferalia.aon.file.payroll.contract.pdf.model;

import java.util.Collection;
import java.util.List;

import com.esferalia.aon.file.payroll.contract.model.DATOSEMPRESATYPE;
import com.esferalia.aon.file.payroll.contract.model.DATOSGENERALESCONTRATOTYPE;
import com.esferalia.aon.file.payroll.contract.model.DATOSTRABAJADORTYPE;
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.payroll.ContractAttachment;
import com.esferalia.aon.payroll.enumeration.ContractCode;
import com.lowagie.text.pdf.PdfReader;



public interface IContractPdfModel {
	
	
	public byte[] buildPdf();

	public void loadPdfFields(ContractCode code, Contract contract) throws UnsupportedContractModelException;
	
	public void loadPdfFields(ContractAttachment contractPdfDraft);
	
//	public void writePdfFields(PdfReader reader);
	public Collection<ContractPdfField> getPdfFields();
	
	public Double getContractWidth();

	public Double getContractHeight();

	
}
	
	