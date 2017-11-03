package com.esferalia.aon.file.payroll.contract.pdf.enterpriseCertificate;

import java.io.IOException;
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
import com.esferalia.aon.sepe.api.certificados.certificadoEmpresa.CertificadoEmpresa;
import com.lowagie.text.pdf.PdfReader;



public abstract class AbstractEnterpriseCertificate implements IContractPdfDocument<CertificadoEmpresa> {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	public final static String ENTERPRISE_CERTIFICATE_PATH = "com/esferalia/aon/file/payroll/contract/enterpriseCertificate/";

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
		return ENTERPRISE_CERTIFICATE_PATH;
	}
	
	@Override
	public byte[] buildPdf(boolean readOnly) {
		try {
			return getHandler().buildPdf(new PdfReader(getEnterpriseCertificateUrl(documentName+".pdf")), readOnly);
		} catch (IOException e) {
			return null;
		}
	}

	public void loadPdfFields(ContractAttachment contractPdfDraft) {
		getHandler().loadPdfFields(contractPdfDraft);
	}
	
	protected URL getEnterpriseCertificateUrl(String file) throws IOException {
		ClassLoader cl = Thread.currentThread().getContextClassLoader();
		URL[] urls = Classpath.search(cl, ENTERPRISE_CERTIFICATE_PATH, file);
		return urls[0];
	}
	
	protected void readPdfFields(PdfReader reader) throws IOException{
		readPdfFields();
	}
	
	protected void readPdfFields() throws IOException{
		getHandler().readPdfFields(new PdfReader(getEnterpriseCertificateUrl(documentName+".pdf")));
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
	
	public enum EnterpriseCertificateField implements IStringEnum {
		
		ENTERPRISE_DIR_STAFF_NAME("Texto1-cer"),
		ENTERPRISE_DIR_STAFF_CHARGE("Texto3-cer"),
		
		// ENTERPRISE
		ENTERPRISE_NAME("Texto3-ce"),
		ENTERPRISE_REGIME_CODE("Texto4-ce"),
		ENTERPRISE_REGIME_NAME("Texto5-ce"),
		ENTERPRISE_CCC("Texto6-ce"),
		ENTERPRISE_SOCIAL_ADDRESS("Texto-domi-social-ce"),
		ENTERPRISE_CITY("Texto-ce"),
		ENTERPRISE_ZIP("6-ce"),
		ENTERPRISE_PROVINCE("g-ce"),
		ENTERPRISE_CNAE_CODE("Texto7-ce"),
		ENTERPRISE_CNAE_NAME("Texto8-ce"),
		ENTERPRISE_WORKPLACE_ADDRESS("l2-ce"),
		
		// EMPLOYEE
		EMPLOYEE_FULLNAME("i-ce"),
		EMPLOYEE_DOCUMENT("Texto5b-ce"),
		EMPLOYEE_SS_NUMBER("Textocodigo-ce"),
		EMPLOYEE_QUOTE_GROUP("9-ce"),
		EMPLOYEE_CONTRACT_TIPE("m-ce"),
		EMPLOYEE_DURATION("Texto11-ce"),
		
		EMPLOYEE_TP_PERIOD1_TYPE("Texto9-ce"),
		EMPLOYEE_TP_PERIOD1_DAYS("Texto10-ce"),
		EMPLOYEE_TP_PERIOD1_FROM("Texto12-ce"),
		EMPLOYEE_TP_PERIOD1_TO("Texto13-ce"),
		
		EMPLOYEE_TP_PERIOD2_TYPE("Texto9a-ce"),
		EMPLOYEE_TP_PERIOD2_DAYS("Texto10a-ce"),
		EMPLOYEE_TP_PERIOD2_FROM("Texto12a-ce"),
		EMPLOYEE_TP_PERIOD2_TO("Texto13a-ce"),
		
		EMPLOYEE_TP_PERIOD3_TYPE("Texto9b-ce"),
		EMPLOYEE_TP_PERIOD3_DAYS("Texto10b-ce"),
		EMPLOYEE_TP_PERIOD3_FROM("Texto12b-ce"),
		EMPLOYEE_TP_PERIOD3_TO("Texto13b-ce"),
		
		EMPLOYEE_TP_PERIOD4_TYPE("Texto9c-ce"),
		EMPLOYEE_TP_PERIOD4_DAYS("Texto10c-ce"),
		EMPLOYEE_TP_PERIOD4_FROM("Texto12c-ce"),
		EMPLOYEE_TP_PERIOD4_TO("Texto13c-ce"),
		
		EMPLOYEE_PROFESSION_CODE("Texto14b-ce"),
		EMPLOYEE_PROFESSION_NAME("ñ-ce"),
		EMPLOYEE_PROFESSION_NAME_MORE("n-ce"),
		EMPLOYEE_PUBLIC_CHARGE("Texto14-ce"),
		EMPLOYEE_PUBLIC_CHARGE_DURATION("Texto15-ce"),
		EMPLOYEE_START_DATE("f1-ce"),
		EMPLOYEE_SUSPENSION_CODE("Texto16-ce"),
		EMPLOYEE_SUSPENSION_NAME("Texto17-ce"),
		EMPLOYEE_SUSPENSION_NAME_MORE("Texto18-ce"),
		EMPLOYEE_END_DATE("Texto19-ce"),
		EMPLOYEE_SUSPENDION_END_DATE("Texto20-ce"),
		EMPLOYEE_ERE_NUMBER("Texto21-ce"),
		EMPLOYEE_TIME_REDUCTION_PERCENT("Texto22-ce"),
		EMPLOYEE_CARE_PERCENT("Texto23-ce"),
		EMPLOYEE_SALARY_TRAMITATION_DAY_NUMBER("DÍAS-ce"),
		EMPLOYEE_SALARY_TRAMITATION_FROM("10'-ce"),
		EMPLOYEE_SALARY_TRAMITATION_TO("10''-ce"),
		
		// COTIZACIONES - CONT. COMUNES Y DESEMPLEO
		ROW1_YEAR("90-ce"),
		ROW1_MONTH("80-ce"),
		ROW1_DAYS("70-ce"),
		ROW1_BASE_COMMON_CONTINGENCIES("60-ce"),
		ROW1_BASE_UNEMPLOYMENT("50-ce"),
		ROW1_REMARKS("a1-ce"),

		ROW2_YEAR("91-ce"),
		ROW2_MONTH("81-ce"),
		ROW2_DAYS("71-ce"),
		ROW2_BASE_COMMON_CONTINGENCIES("61-ce"),
		ROW2_BASE_UNEMPLOYMENT("51-ce"),
		ROW2_REMARKS("a2-ce"),
		
		ROW3_YEAR("92-ce"),
		ROW3_MONTH("82-ce"),
		ROW3_DAYS("72-ce"),
		ROW3_BASE_COMMON_CONTINGENCIES("62-ce"),
		ROW3_BASE_UNEMPLOYMENT("52-ce"),
		ROW3_REMARKS("a3-ce"),
		
		ROW4_YEAR("93-ce"),
		ROW4_MONTH("83-ce"),
		ROW4_DAYS("73-ce"),
		ROW4_BASE_COMMON_CONTINGENCIES("63-ce"),
		ROW4_BASE_UNEMPLOYMENT("53-ce"),
		ROW4_REMARKS("a4-ce"),
		
		ROW5_YEAR("94-ce"),
		ROW5_MONTH("84-ce"),
		ROW5_DAYS("74-ce"),
		ROW5_BASE_COMMON_CONTINGENCIES("64-ce"),
		ROW5_BASE_UNEMPLOYMENT("54-ce"),
		ROW5_REMARKS("a5-ce"),
		
		ROW6_YEAR("95-ce"),
		ROW6_MONTH("85-ce"),
		ROW6_DAYS("75-ce"),
		ROW6_BASE_COMMON_CONTINGENCIES("65-ce"),
		ROW6_BASE_UNEMPLOYMENT("55-ce"),
		ROW6_REMARKS("a6-ce"),
		
		ROW7_YEAR("96-ce"),
		ROW7_MONTH("86-ce"),
		ROW7_DAYS("76-ce"),
		ROW7_BASE_COMMON_CONTINGENCIES("66-ce"),
		ROW7_BASE_UNEMPLOYMENT("56-ce"),
		ROW7_REMARKS("a7-ce"),
		
		ROW8_YEAR("97-ce"),
		ROW8_MONTH("87-ce"),
		ROW8_DAYS("77-ce"),
		ROW8_BASE_COMMON_CONTINGENCIES("67-ce"),
		ROW8_BASE_UNEMPLOYMENT("57-ce"),
		ROW8_REMARKS("a8-ce"),
		
		HOLIDAY_DAYS("78-ce"),
		HOLIDAY_BASE_COMMON_CONTINGENCIES("68-ce"),
		HOLIDAY_BASE_UNEMPLOYMENT("58-ce"),
		HOLIDAY_REMARKS("a9-ce"),

		TOTAL_DAYS("Texto35-ce"),
		TOTAL_BASE_COMMON_CONTINGENCIES("Texto36-ce"),
		TOTAL_BASE_UNEMPLOYMENT("Texto37-ce"),
		
		// FOOTER
		SIGN_CITY("CIUDAD-ce"),
		SIGN_DAY("DIA-ce"),
		SIGN_MONTH("MES-ce"),
		SIGN_YEAR("AÑO-ce"),
		SIGNATURE("textofirma"),
		
		;
		
		private String value;
		
		private EnterpriseCertificateField(String value) {
			this.value = value;
		}
		
		@Override
		public String getValue() {
			return value;
		}
		
	}

	public enum EnterpriseCertificateSeaField implements IStringEnum {
		
		ENTERPRISE_DIR_STAFF_NAME("Texto1-ce"),
		ENTERPRISE_DIR_STAFF_CHARGE("Texto2-ce"),
		
		// ENTERPRISE
		ENTERPRISE_NAME("Texto4-cer"),
//		ENTERPRISE_REGIME_CODE(""),
//		ENTERPRISE_REGIME_NAME(""),
		ENTERPRISE_CCC("1-cer"),
		ENTERPRISE_SOCIAL_ADDRESS("j-cer"),
		ENTERPRISE_CITY("f-cer"),
		ENTERPRISE_ZIP("6-cer"),
		ENTERPRISE_PROVINCE("g-cer"),
		ENTERPRISE_CNAE_CODE("Texto7-cer"),
		ENTERPRISE_CNAE_NAME("Texto8-cer"),
		ENTERPRISE_WORKPLACE_ADDRESS("l2-cer"),
		
		// EMPLOYEE
		EMPLOYEE_FULLNAME("i-cer"),
		EMPLOYEE_DOCUMENT("Texto5b-cer"),
		EMPLOYEE_SS_NUMBER("1a-cer"),
//		EMPLOYEE_QUOTE_GROUP(""),
		EMPLOYEE_CONTRACT_TIPE("m-cer"),
		EMPLOYEE_DURATION("Texto11-cer"),
		
		EMPLOYEE_TP_PERIOD1_TYPE("Texto9-cer"),
		EMPLOYEE_TP_PERIOD1_DAYS("Texto10-cer"),
		EMPLOYEE_TP_PERIOD1_FROM("Texto12-cer"),
		EMPLOYEE_TP_PERIOD1_TO("Texto13-cer"),
		
		EMPLOYEE_TP_PERIOD2_TYPE("Texto9a-cer"),
		EMPLOYEE_TP_PERIOD2_DAYS("Texto10a-cer"),
		EMPLOYEE_TP_PERIOD2_FROM("Texto12a-cer"),
		EMPLOYEE_TP_PERIOD2_TO("Texto13a-cer"),
		
		EMPLOYEE_TP_PERIOD3_TYPE("Texto9b-cer"),
		EMPLOYEE_TP_PERIOD3_DAYS("Texto10b-cer"),
		EMPLOYEE_TP_PERIOD3_FROM("Texto12b-cer"),
		EMPLOYEE_TP_PERIOD3_TO("Texto13b-cer"),
		
		EMPLOYEE_TP_PERIOD4_TYPE("Texto9c-cer"),
		EMPLOYEE_TP_PERIOD4_DAYS("Texto10c-cer"),
		EMPLOYEE_TP_PERIOD4_FROM("Texto12c-cer"),
		EMPLOYEE_TP_PERIOD4_TO("Texto13c-cer"),
		
		EMPLOYEE_PROFESSION_CODE("Texto14b"),
		EMPLOYEE_PROFESSION_NAME("ñ-cer"),
		EMPLOYEE_PROFESSION_NAME_MORE("n-cer"),
		EMPLOYEE_PUBLIC_CHARGE("Texto14-cer"),
		EMPLOYEE_PUBLIC_CHARGE_DURATION("Texto15y-cer"),
		EMPLOYEE_START_DATE("f1-cer"),
		EMPLOYEE_SUSPENSION_CODE("Texto16-cer"),
		EMPLOYEE_SUSPENSION_NAME("Texto17-cer"),
		EMPLOYEE_SUSPENSION_NAME_MORE("Texto18-cer"),
		EMPLOYEE_END_DATE("Texto19-cer"),
		EMPLOYEE_SUSPENDION_END_DATE("Texto20-cer"),
		EMPLOYEE_ERE_NUMBER("Texto21-cer"),
		EMPLOYEE_TIME_REDUCTION_PERCENT("Texto22-cer"),
		EMPLOYEE_CARE_PERCENT("Texto23-cer"),
		EMPLOYEE_SALARY_TRAMITATION_DAY_NUMBER("DÍAS-cer"),
		EMPLOYEE_SALARY_TRAMITATION_FROM("10'-cer"),
		EMPLOYEE_SALARY_TRAMITATION_TO("10''-cer"),
		
		// COTIZACIONES - CONT. COMUNES Y DESEMPLEO
		ROW1_YEAR("90-cer"),
		ROW1_MONTH("80-cer"),
		ROW1_QUOTE_GROUP("9-cer"),
		ROW1_QUOTE_DAYS("Texto15-cer"),
		ROW1_JOURNAL_DAYS("Textob15-cer"),
		ROW1_BASE_UNEMPLOYMENT("Textoc15-cer"),
		ROW1_REMARKS("a1-cer"),

		ROW2_YEAR("90a-cer"),
		ROW2_MONTH("80a-cer"),
		ROW2_QUOTE_GROUP("9a-cer"),
		ROW2_QUOTE_DAYS("Texto15a-cer"),
		ROW2_JOURNAL_DAYS("Textob15a-cer"),
		ROW2_BASE_UNEMPLOYMENT("Textoc15a-cer"),
		ROW2_REMARKS("a1a-cer"),

		ROW3_YEAR("90b-cer"),
		ROW3_MONTH("80b-cer"),
		ROW3_QUOTE_GROUP("9b-cer"),
		ROW3_QUOTE_DAYS("Texto15b-cer"),
		ROW3_JOURNAL_DAYS("Textob15b-cer"),
		ROW3_BASE_UNEMPLOYMENT("Textoc15b-cer"),
		ROW3_REMARKS("a1b-cer"),

		ROW4_YEAR("90c-cer"),
		ROW4_MONTH("80c-cer"),
		ROW4_QUOTE_GROUP("9c-cer"),
		ROW4_QUOTE_DAYS("Texto15c-cer"),
		ROW4_JOURNAL_DAYS("Textob15c-cer"),
		ROW4_BASE_UNEMPLOYMENT("Textoc15c-cer"),
		ROW4_REMARKS("a1c-cer"),

		ROW5_YEAR("90d-cer"),
		ROW5_MONTH("80d-cer"),
		ROW5_QUOTE_GROUP("9d-cer"),
		ROW5_QUOTE_DAYS("Texto15d-cer"),
		ROW5_JOURNAL_DAYS("Textob15d-cer"),
		ROW5_BASE_UNEMPLOYMENT("Textoc15d-cer"),
		ROW5_REMARKS("a1d-cer"),

		ROW6_YEAR("90e-cer"),
		ROW6_MONTH("80e-cer"),
		ROW6_QUOTE_GROUP("9e-cer"),
		ROW6_QUOTE_DAYS("Texto15e-cer"),
		ROW6_JOURNAL_DAYS("Textob15e-cer"),
		ROW6_BASE_UNEMPLOYMENT("Textoc15e-cer"),
		ROW6_REMARKS("a1e-cer"),

		ROW7_YEAR("90f-cer"),
		ROW7_MONTH("80f-cer"),
		ROW7_QUOTE_GROUP("9f-cer"),
		ROW7_QUOTE_DAYS("Texto15f-cer"),
		ROW7_JOURNAL_DAYS("Textob15f-cer"),
		ROW7_BASE_UNEMPLOYMENT("Textoc15f-cer"),
		ROW7_REMARKS("a1f-cer"),

		ROW8_YEAR("90g-cer"),
		ROW8_MONTH("80g-cer"),
		ROW8_QUOTE_GROUP("9g-cer"),
		ROW8_QUOTE_DAYS("Texto15g-cer"),
		ROW8_JOURNAL_DAYS("Textob15g-cer"),
		ROW8_BASE_UNEMPLOYMENT("Textoc15g-cer"),
		ROW8_REMARKS("a1g-cer"),
		
		
		HOLIDAY_QUOTE_GROUP("9h-cer"),
		HOLIDAY_QUOTE_DAYS("Texto15h-cer"),
		HOLIDAY_JOURNAL_DAYS("Textob15h-cer"),
		HOLIDAY_BASE_UNEMPLOYMENT("Textoc15h-cer"),
		HOLIDAY_REMARKS("a1h-cer"),
	
		TOTAL_QUOTE_DAYS("Texto15i-cer"),
		TOTAL_JOURNAL_DAYS("Textob15i-cer"),
		TOTAL_BASE_UNEMPLOYMENT("Textoc15i-cer"),
		TOTAL_REMARKS("a1i-cer"),
		
		// FOOTER
		SIGN_CITY("CIUDAD-cer"),
		SIGN_DAY("DIA-cer"),
		SIGN_MONTH("MES-cer"),
		SIGN_YEAR("AÑO-cer"),
		SIGNATURE("CIU-cer"),
		
		;
		
		private String value;
		
		private EnterpriseCertificateSeaField(String value) {
			this.value = value;
		}
		
		@Override
		public String getValue() {
			return value;
		}
		
	}
	
}
	
	