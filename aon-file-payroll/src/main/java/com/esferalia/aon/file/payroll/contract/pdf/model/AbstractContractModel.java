package com.esferalia.aon.file.payroll.contract.pdf.model;

import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.code.aon.AonVersion;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.Country;
import com.code.aon.common.util.Classpath;
import com.code.aon.geozone.GeoZone;
import com.code.aon.registry.Registry;
import com.code.aon.registry.RegistryDirStaff;
import com.esferalia.aon.file.payroll.contract.pdf.ContractPdfField;
import com.esferalia.aon.file.payroll.contract.pdf.IContractPdfDocument;
import com.esferalia.aon.file.payroll.contract.pdf.ModelOption;
import com.esferalia.aon.file.payroll.contract.pdf.PdfModelHandler;
import com.esferalia.aon.file.payroll.contrata.IContrataParams;
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.payroll.ContractAttachment;
import com.esferalia.aon.payroll.ContractInfo.ContractVariable;
import com.lowagie.text.pdf.PdfReader;


public abstract class AbstractContractModel implements IContractPdfDocument {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	public final static String CONTRACT_DOCUMENT_PATH = "com/esferalia/aon/file/payroll/contract/modelPdf/";
	public final static String CONTRACT_CLAUSES_PATH = "com/esferalia/aon/file/payroll/contract/clausesPdf/";
	
	protected Contract contract;
	
	private PdfModelHandler handler;
	
	protected String documentName;
	
	private Locale locale;
	
	public Locale getLocale() {
		return locale;
	}

	@Override
	public void setLocale(Locale locale) {
		this.locale = locale;
	}

	public Collection<ContractPdfField> getPdfFields() {
		return getPdfFieldsMap().values();
	}
	
	@Override
	public Map<String, ContractPdfField> getPdfFieldsMap() {
		return getHandler().getPdfFieldsMap();
	}
	
	public PdfModelHandler getHandler() {
		if(handler==null){
			handler = new PdfModelHandler();
		}
		return handler;
	}

	@Override
	public Double getDocumentWidth() {
		return getHandler().getDocumentWidth();
	}
	
	public void setDocumentWidth(Double documentWidth) {
		getHandler().setDocumentWidth(documentWidth);
	}

	@Override
	public Double getDocumentHeight() {
		return getHandler().getDocumentHeight();
	}

	public void setDocumentHeight(Double documentHeight) {
		getHandler().setDocumentHeight(documentHeight);
	}

	@Override
	public Integer getNumberOfDocumentPages() {
		return getHandler().getNumberOfDocumentPages();
	}
	
	public void setNumberOfDocumentPages(Integer numberOfDocumentPages) {
		getHandler().setNumberOfDocumentPages(numberOfDocumentPages);
	}
	
	@Override
	public String getDocumentPath(){
		// CONTRACT DOCUMENT
		if (documentName.equals(LearningModel.MODEL_NAME) || documentName.equals(PracticeModel.MODEL_NAME)
			 || documentName.equals(TemporaryModel.MODEL_NAME) || documentName.equals(IndefiniteModel.MODEL_NAME)) {
			return CONTRACT_DOCUMENT_PATH;
		}		
		// CLAUSES DOCUMENT
		if (documentName.equals(ClausulasModel.MODEL_NAME)) {
			return CONTRACT_CLAUSES_PATH;
		} 
		return null;
	}
	
	@Override
	public byte[] buildPdf(boolean readOnly) {
		try {
			PdfReader reader = new PdfReader(getContractModelUrl(documentName+".pdf"));
			String range = "1-3";
			ModelOption modelOption = ModelOption.valueOf(getContractInfoMap(contract).get(ContractVariable.CONTRACT_MODEL_OPTION.getValue()));
			range += ","+modelOption.getPageNumber();
			reader.selectPages(range);
			return getHandler().buildPdf(reader, readOnly);
		} catch (IOException e) {
			return null;
		}
	}

	public void loadPdfFields(ContractAttachment contractPdfDraft) {
		getHandler().loadPdfFields(contractPdfDraft);
	}
	
	protected URL getContractModelUrl(String file) throws IOException {
		ClassLoader cl = Thread.currentThread().getContextClassLoader();
		URL[] urls = Classpath.search(cl, getDocumentPath(), file);
		return urls[0];
	}
	
	protected void readPdfFields(PdfReader reader) throws IOException{
		readPdfFields();
	}
	protected void readPdfFields() throws IOException{
		PdfReader reader = new PdfReader(getContractModelUrl(documentName+".pdf"));
		String range = "1-3";
		ModelOption modelOption = ModelOption.valueOf(getContractInfoMap(contract).get(ContractVariable.CONTRACT_MODEL_OPTION.getValue()));
		range += ","+modelOption.getPageNumber();
		reader.selectPages(range);
		getHandler().readPdfFields(reader);
	}
	
	protected void setPdfFieldValue(String name, String value){
		getHandler().setPdfFieldValue(name, value);
	}
	
	public void loadPdfCommonFields(Contract contract, List<IContrataParams> contrataParams) throws ManagerBeanException{
		
	}
	
	protected RegistryDirStaff obtainRegistryDirStaff(Registry registry) throws ManagerBeanException {
		return getHandler().obtainRegistryDirStaff(registry);
	}
	
	protected GeoZone obtainCountry(GeoZone geoZone) throws ManagerBeanException {
		return getHandler().obtainCountry(geoZone);
	}

	protected Country obtainCountryByValue(GeoZone country) {
		return getHandler().obtainCountryByValue(country);
	}
	
	public Map<String, String> getContractDataMap(Contract contract) {
		return getHandler().getContractDataMap(contract);
	}

	public Map<String, String> getContractDataMap(Contract contract, Date startDate, Date endDate, boolean current) {
		return getHandler().getContractDataMap(contract, startDate, endDate, current);
	}
	
	public Map<String, String> getContractInfoMap(Contract contract) {
		return getHandler().getContractInfoMap(contract);
	}
	
	
}
	
	