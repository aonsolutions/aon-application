package com.esferalia.aon.file.payroll.contract.pdf.model;

import java.util.Collection;

import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.payroll.ContractAttachment;
import com.esferalia.aon.payroll.enumeration.ContractCode;


public interface IContractPdfModel {
	
	/* 
	 * Contract enterprise fields
	 */
	final String ENTERPRISE_CIF = "cif";
	final String ENTERPRISE_DIR_STAFF_NAME = "nomrepr";
	final String ENTERPRISE_DIR_STAFF_NIF = "dnirep";
	final String ENTERPRISE_DIR_STAFF_CHARGE = "cargorep";
	final String ENTERPRISE_NAME = "razsoc";
	final String ENTERPRISE_ADDRESS = "domsocialem";
	final String ENTERPRISE_COUNTRY = "Texto1pais";
	final String ENTERPRISE_COUNTRY_CODE1 = "codpaisem1";
	final String ENTERPRISE_COUNTRY_CODE2 = "codpaisem2";
	final String ENTERPRISE_COUNTRY_CODE3 = "codpaisem3";
	final String ENTERPRISE_TOWN = "Texto3mun";
	final String ENTERPRISE_TOWN_CODE1 = "codmuniem1";
	final String ENTERPRISE_TOWN_CODE2 = "codmuniem2";
	final String ENTERPRISE_TOWN_CODE3 = "codmuniem3";
	final String ENTERPRISE_TOWN_CODE4 = "codmuniem4";
	final String ENTERPRISE_TOWN_CODE5 = "codmuniem5";
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
	final String WORKPLACE_COUNTRY = "Texto4pais";
	final String WORKPLACE_COUNTRY_CODE1 = "codpaisct1";
	final String WORKPLACE_COUNTRY_CODE2 = "codpaisct2";
	final String WORKPLACE_COUNTRY_CODE3 = "codpaisct3";
	final String WORKPLACE_TOWN = "Texto5municipio";
	final String WORKPLACE_TOWN_CODE1 = "codmunict1";
	final String WORKPLACE_TOWN_CODE2 = "codmunict2";
	final String WORKPLACE_TOWN_CODE3 = "codmunict3";
	final String WORKPLACE_TOWN_CODE4 = "codmunict4";
	final String WORKPLACE_TOWN_CODE5 = "codmunict5";
	
	/*
	 * Contract employee fields
	 */
	final String EMPLOYEE_NAME = "nomtrab";
	final String EMPLOYEE_NIF = "dnitra";
	final String EMPLOYEE_BIRTH_DATE = "fechanac";
	final String EMPLOYEE_NSS = "numafinss";
	final String EMPLOYEE_FORMATION_CODE1 = "codnivaca1";
	final String EMPLOYEE_FORMATION_CODE2 = "codnivaca2";
	final String EMPLOYEE_COUNTRY_CODE1 = "codnactra1";
	final String EMPLOYEE_COUNTRY_CODE2 = "codnactra2";
	final String EMPLOYEE_COUNTRY_CODE3 = "codnactra3";
	final String EMPLOYEE_ADDRESS_TOWN = "Texto6mun";
	final String EMPLOYEE_ADDRESS_TOWN_CODE1 = "codmunitra1";
	final String EMPLOYEE_ADDRESS_TOWN_CODE2 = "codmunitra2";
	final String EMPLOYEE_ADDRESS_TOWN_CODE3 = "codmunitra3";
	final String EMPLOYEE_ADDRESS_TOWN_CODE4 = "codmunitra4";
	final String EMPLOYEE_ADDRESS_TOWN_CODE5 = "codmunitra5";
	final String EMPLOYEE_ADDRESS_COUNTRY = "Texto7padom";
	final String EMPLOYEE_ADDRESS_COUNTRY_CODE1 = "codpaisdomtr1";
	final String EMPLOYEE_ADDRESS_COUNTRY_CODE2 = "codpaisdomtr2";
	final String EMPLOYEE_ADDRESS_COUNTRY_CODE3 = "codpaisdomtr3";
	
	
	final String MODELS_PATH = "com/esferalia/aon/file/payroll/contract/pdf/";
	
	
	public byte[] buildPdf();

	public void loadPdfFields(ContractCode code, Contract contract) throws UnsupportedContractModelException;
	
	public void loadPdfFields(ContractAttachment contractPdfDraft);
	
	public Collection<ContractPdfField> getPdfFields();
	
	public Double getContractWidth();

	public Double getContractHeight();

	public Integer getNumberOfContractPages();
	
}
	
	