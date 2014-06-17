package com.esferalia.aon.file.payroll.contract.pdf;

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.esferalia.aon.file.payroll.contrata.IContrataParams;
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.payroll.ContractAttachment;
import com.esferalia.aon.payroll.enumeration.ContractCode;


public interface IContractPdfDocument {
	
	String MUNICIPALITIES_BUNDLE_BASE_NAME = "com.code.aon.common.i18n.municipalities";
	
	public byte[] buildPdf(boolean readOnly);

	public void loadPdfFields(ContractAttachment contractPdfDraft);
	
	public void loadPdfFieldValues(ContractCode code, Contract contract, List<IContrataParams> contrataParams) throws UnsupportedContractDocumentException;
	
	public Collection<ContractPdfField> getPdfFields();
	
	public Double getDocumentWidth();

	public Double getDocumentHeight();

	public Integer getNumberOfDocumentPages();
	
	public String getDocumentPath();

	public void setLocale(Locale locale);

	public Map<String, ContractPdfField> getPdfFieldsMap();
	
}
	
	