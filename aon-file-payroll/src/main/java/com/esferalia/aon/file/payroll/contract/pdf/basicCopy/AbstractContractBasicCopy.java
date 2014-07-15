package com.esferalia.aon.file.payroll.contract.pdf.basicCopy;

import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.util.Collection;
import java.util.Locale;
import java.util.Map;

import com.code.aon.AonVersion;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.IStringEnum;
import com.code.aon.common.util.Classpath;
import com.code.aon.registry.Registry;
import com.code.aon.registry.RegistryDirStaff;
import com.esferalia.aon.file.payroll.contract.pdf.ContractPdfField;
import com.esferalia.aon.file.payroll.contract.pdf.IContractPdfDocument;
import com.esferalia.aon.file.payroll.contract.pdf.PdfModelHandler;
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.payroll.ContractAttachment;
import com.lowagie.text.pdf.PdfReader;



public abstract class AbstractContractBasicCopy implements IContractPdfDocument {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	public final static String CONTRACT_BASIC_COPY_PATH = "com/esferalia/aon/file/payroll/contract/basicCopyPdf/";

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
	
	public PdfReader getReader() {
		return getHandler().getReader();
	}

	public void setReader(PdfReader reader) {
		getHandler().setReader(reader);
	}
	
	@Override
	public String getDocumentPath(){
		return CONTRACT_BASIC_COPY_PATH;
	}
	
	@Override
	public byte[] buildPdf(boolean readOnly) {
		return getHandler().buildPdf(readOnly);
	}

	public void loadPdfFields(ContractAttachment contractPdfDraft) {
		getHandler().loadPdfFields(contractPdfDraft);
	}
	
	protected URL getContractBasicCopyUrl(String file) throws IOException {
		ClassLoader cl = Thread.currentThread().getContextClassLoader();
		URL[] urls = Classpath.search(cl, CONTRACT_BASIC_COPY_PATH, file);
		return urls[0];
	}
	
	protected void readPdfFields(PdfReader reader) throws IOException{
		readPdfFields();
	}
	
	protected void readPdfFields() throws IOException{
		getHandler().readPdfFields();
	}
	
	protected void setPdfFieldValue(String name, String value){
		getHandler().setPdfFieldValue(name, value);
	}
	
	public void loadPdfCommonFields(Contract contract) throws ManagerBeanException{
	}
	
	protected RegistryDirStaff obtainRegistryDirStaff(Registry registry) throws ManagerBeanException {
		return getHandler().obtainRegistryDirStaff(registry);
	}
	
	public Map<String, String> getContractDataMap(Contract contract) {
		return getHandler().getContractDataMap(contract);
	}
	
	public Map<String, String> getContractInfoMap(Contract contract) {
		return getHandler().getContractInfoMap(contract);
	}
	
	public enum BasicCopyField implements IStringEnum {
		
		/* 
		 * Enterprise fields
		 */
		ENTERPRISE_CIF("cif"),
		ENTERPRISE_NAME("razsoc"),
		ENTERPRISE_ADDRESS("domsocialem"),
		ENTERPRISE_MUNICIPALITY_CODE1("codmuniem1"),
		ENTERPRISE_MUNICIPALITY_CODE2("codmuniem2"),
		ENTERPRISE_MUNICIPALITY_CODE3("codmuniem3"),
		ENTERPRISE_MUNICIPALITY_CODE4("codmuniem4"),
		ENTERPRISE_MUNICIPALITY_CODE5("codmuniem5"),
		ENTERPRISE_DIR_STAFF_NAME("nomrepr"),
		
		/*
		 * Employee fields
		 */
		EMPLOYEE_NAME("nomtrab"),
		
		/*
		 * Contract fields
		 */
		CONTRACT_START_DATE("fechaini"),
		CONTRACT_TYPE("cto"),
		CONTRACT_TOTAL_DURATION("totaldura1"),
		CONTRACT_CATEGORY("catetraba"),
		CONTRACT_JOURNAL_HOURS_1("horasjorna1"),
		CONTRACT_JOURNAL_HOURS_2("horasjorna2"),
		CONTRACT_JOURNAL("jornada"),
		SALARY_AMOUNT("retribu"),
		SALARY_AMOUNT_EURO("euros"),
		SALARY_PERIOD("perioretri"),
		HOLIDAYS("vacaciones"),
		
		CONTRACT_SIGN_TOWN("munifirma"),
		CONTRACT_SING_DAY("diafirma"),
		CONTRACT_SIGN_MONTH("mesfirma"),
		CONTRACT_SIGN_YEAR("añofirma"),
		;
		
		private String value;
		
		private BasicCopyField(String value) {
			this.value = value;
		}
		
		@Override
		public String getValue() {
			return value;
		}
		
	}
	
}
	
	