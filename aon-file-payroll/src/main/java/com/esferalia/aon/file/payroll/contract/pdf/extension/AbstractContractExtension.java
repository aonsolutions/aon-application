package com.esferalia.aon.file.payroll.contract.pdf.extension;

import java.io.IOException;
import java.net.URL;
import java.util.Collection;
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
import com.esferalia.aon.file.payroll.contract.pdf.PdfModelHandler;
import com.esferalia.aon.file.payroll.contrata.IContrataParams;
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.payroll.ContractAttachment;
import com.lowagie.text.pdf.PdfReader;



public abstract class AbstractContractExtension implements IContractPdfDocument {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	/* 
	 * Contract enterprise fields
	 */
	final String ENTERPRISE_CIF = "cif";
	final String ENTERPRISE_DIR_STAFF_NAME = "nomrepr";
	final String ENTERPRISE_DIR_STAFF_NIF = "dnirep";
	final String ENTERPRISE_DIR_STAFF_CHARGE = "cargorep";
	final String ENTERPRISE_NAME = "razsoc";
	final String ENTERPRISE_ADDRESS = "domsocialem";
	final String ENTERPRISE_COUNTRY = "Texto1pais1";
	final String ENTERPRISE_COUNTRY_CODE1 = "codpaisem1";
	final String ENTERPRISE_COUNTRY_CODE2 = "codpaisem2";
	final String ENTERPRISE_COUNTRY_CODE3 = "codpaisem3";
	final String ENTERPRISE_MUNICIPALITY = "Texto2muni1";
	final String ENTERPRISE_MUNICIPALITY_CODE1 = "codmuniem1";
	final String ENTERPRISE_MUNICIPALITY_CODE2 = "codmuniem2";
	final String ENTERPRISE_MUNICIPALITY_CODE3 = "codmuniem3";
	final String ENTERPRISE_MUNICIPALITY_CODE4 = "codmuniem4";
	final String ENTERPRISE_MUNICIPALITY_CODE5 = "codmuniem5";
	final String ENTERPRISE_ZIP1 = "codpostem1";
	final String ENTERPRISE_ZIP2 = "codpostem2";
	final String ENTERPRISE_ZIP3 = "codpostem3";
	final String ENTERPRISE_ZIP4 = "codpostem4";
	final String ENTERPRISE_ZIP5 = "codpostem5";
	
	/* 
	 * Contract ccc fields
	 */
	final String CCC_REG1 = "regimen1";
	final String CCC_REG2 = "regimen2";
	final String CCC_REG3 = "regimen3";
	final String CCC_REG4 = "regimen4";
	final String CCC_PROV1 = "provniss1";
	final String CCC_PROV2 = "provniss2";
	final String CCC_NISS = "numniss";
	final String CCC_CONTROL_DIGIT1 = "digcon1";
	final String CCC_CONTROL_DIGIT2 = "digcon2";
	final String CCC_ACTIVITY = "litacteco";
	final String CCC_ACTIVITY_CODE1 = "codacteco1";
	final String CCC_ACTIVITY_CODE2 = "codacteco2";
	
	/* 
	 * Contract workplace fields
	 */
	final String WORKPLACE_COUNTRY = "Texto3pais3";
	final String WORKPLACE_COUNTRY_CODE1 = "codpaisct1";
	final String WORKPLACE_COUNTRY_CODE2 = "codpaisct2";
	final String WORKPLACE_COUNTRY_CODE3 = "codpaisct3";
	final String WORKPLACE_MUNICIPALITY = "Texto4muni3";
	final String WORKPLACE_MUNICIPALITY_CODE1 = "codmunict1";
	final String WORKPLACE_MUNICIPALITY_CODE2 = "codmunict2";
	final String WORKPLACE_MUNICIPALITY_CODE3 = "codmunict3";
	final String WORKPLACE_MUNICIPALITY_CODE4 = "codmunict4";
	final String WORKPLACE_MUNICIPALITY_CODE5 = "codmunict5";
	
	/*
	 * Contract employee fields
	 */
	final String EMPLOYEE_NAME = "nomtrab";
	final String EMPLOYEE_NIF = "dnitra";
	final String EMPLOYEE_BIRTH_DATE = "fechanac";
	final String EMPLOYEE_NSS = "numafinss";
	final String EMPLOYEE_FORMATION_LEVEL = "nivel_formativo";
	final String EMPLOYEE_FORMATION_CODE1 = "codnivaca1";
	final String EMPLOYEE_FORMATION_CODE2 = "codnivaca2";
	final String EMPLOYEE_COUNTRY = "Texto5nacion1";
	final String EMPLOYEE_COUNTRY_CODE1 = "codnactra1";
	final String EMPLOYEE_COUNTRY_CODE2 = "codnactra2";
	final String EMPLOYEE_COUNTRY_CODE3 = "codnactra3";
	final String EMPLOYEE_ADDRESS_MUNICIPALITY = "Texto6domi20";
	final String EMPLOYEE_ADDRESS_MUNICIPALITY_CODE1 = "codmunitra1";
	final String EMPLOYEE_ADDRESS_MUNICIPALITY_CODE2 = "codmunitra2";
	final String EMPLOYEE_ADDRESS_MUNICIPALITY_CODE3 = "codmunitra3";
	final String EMPLOYEE_ADDRESS_MUNICIPALITY_CODE4 = "codmunitra4";
	final String EMPLOYEE_ADDRESS_MUNICIPALITY_CODE5 = "codmunitra5";
	final String EMPLOYEE_ADDRESS_COUNTRY = "Texto7domi21";
	final String EMPLOYEE_ADDRESS_COUNTRY_CODE1 = "codpaisdomtr1";
	final String EMPLOYEE_ADDRESS_COUNTRY_CODE2 = "codpaisdomtr2";
	final String EMPLOYEE_ADDRESS_COUNTRY_CODE3 = "codpaisdomtr3";
	
	public final static String CONTRACT_EXTENSION_PATH = "com/esferalia/aon/file/payroll/contract/extensionPdf/";
	
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
		return CONTRACT_EXTENSION_PATH;
	}
	
	@Override
	public byte[] buildPdf(boolean readOnly) {
		try {
			return getHandler().buildPdf(new PdfReader(getContractExtensionUrl(documentName+".pdf")), readOnly);
		} catch (IOException e) {
			return null;
		}
	}

	public void loadPdfFields(ContractAttachment contractPdfDraft) {
		getHandler().loadPdfFields(contractPdfDraft);
	}
	
	protected URL getContractExtensionUrl(String file) throws IOException {
		ClassLoader cl = Thread.currentThread().getContextClassLoader();
		URL[] urls = Classpath.search(cl, CONTRACT_EXTENSION_PATH, file);
		return urls[0];
	}
	
	protected void readPdfFields(PdfReader reader) throws IOException {
		readPdfFields();
	}
	
	protected void readPdfFields() throws IOException{
		getHandler().readPdfFields(new PdfReader(getContractExtensionUrl(documentName+".pdf")));
	}
	
	protected void setPdfFieldValue(String name, String value){
		try {
			getPdfFieldsMap().get(name).setValue(value);
		} catch (NullPointerException npe) {
			// do nothing
		}
	}
	
	public void loadPdfCommonFields(Contract contract, List<IContrataParams> contrataParams) throws ManagerBeanException{
		
	}
	
	private RegistryDirStaff obtainRegistryDirStaff(Registry registry) throws ManagerBeanException {
		return getHandler().obtainRegistryDirStaff(registry);
	}
	
	protected GeoZone obtainCountry(GeoZone geoZone) throws ManagerBeanException {
		return getHandler().obtainCountry(geoZone);
	}

	protected Country obtainCountryByValue(GeoZone country) {
		return getHandler().obtainCountryByValue(country);
	}
	

	
}
	
	