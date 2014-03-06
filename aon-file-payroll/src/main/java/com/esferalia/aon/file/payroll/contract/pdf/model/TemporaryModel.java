package com.esferalia.aon.file.payroll.contract.pdf.model;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ResourceBundle;

import org.apache.commons.lang.StringUtils;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.geozone.GeoZone;
import com.code.aon.registry.RegistryAddress;
import com.code.aon.registry.RegistryDirStaff;
import com.esferalia.aon.file.payroll.contract.pdf.ModelOption;
import com.esferalia.aon.file.payroll.contract.pdf.TemporaryCommonField;
import com.esferalia.aon.file.payroll.contract.pdf.TemporaryOptionField;
import com.esferalia.aon.file.payroll.contract.pdf.UnsupportedContractDocumentException;
import com.esferalia.aon.file.payroll.contrata.ContrataContratoParams;
import com.esferalia.aon.file.payroll.contrata.IContrataParams;
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.payroll.ContractInfo.ContractVariable;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.payroll.enumeration.ContractCode;
import com.esferalia.aon.payroll.enumeration.contrata.TEIINTER;
import com.esferalia.aon.payroll.enumeration.contrata.TEQPTIEM;
import com.esferalia.aon.payroll.enumeration.contrata.TETPGMEM;
import com.lowagie.text.pdf.PdfReader;



public class TemporaryModel extends AbstractContractModel {
	
	public final static String MODEL_NAME = "Temporal";
	
	public TemporaryModel(){
		super.documentName = MODEL_NAME;
	}
	
	@Override
	public void loadPdfFieldValues(ContractCode code, Contract contract, IContrataParams contrataParams) throws UnsupportedContractDocumentException{
		
		try {
			setReader(new PdfReader(getContractModelUrl(documentName+".pdf")));
			String range = "1-3";
			ModelOption modelOption = ModelOption.valueOf(getContractInfoMap(contract).get(ContractVariable.CONTRACT_MODEL_OPTION.getValue()));
			range += ","+modelOption.getPageNumber();
			getReader().selectPages(range);
			readPdfFields();
			
			ContrataContratoParams contrata = (ContrataContratoParams) contrataParams;
			SimpleDateFormat dateFormatter = new SimpleDateFormat();
			
			/* 
			 * Contract enterprise fields
			 */
			setPdfFieldValue(TemporaryCommonField.ENTERPRISE_CIF.getValue(),contract.getWorkPlace().getEnterprise().getRegistry().getDocument());
			RegistryDirStaff rDirStaff = obtainRegistryDirStaff(contract.getWorkPlace().getEnterprise().getRegistry()); 
			try {
				setPdfFieldValue(TemporaryCommonField.ENTERPRISE_DIR_STAFF_NAME.getValue(),rDirStaff.getName());
				setPdfFieldValue(TemporaryCommonField.ENTERPRISE_DIR_STAFF_NIF.getValue(),rDirStaff.getDocument());
				String rDirStaddCharge = null;
				if ( rDirStaff.isShareHolder() ){
					rDirStaddCharge = "Socio";
				} else if ( rDirStaff.isRepresentative() ){
					rDirStaddCharge = "Apoderado";
				} else if( rDirStaff.isDirector() ){
					rDirStaddCharge = "Administrador";
				} else if ( rDirStaff.isRepresentativeLabor() ){
					rDirStaddCharge = "Repr. laboral";
				}
				setPdfFieldValue(TemporaryCommonField.ENTERPRISE_DIR_STAFF_CHARGE.getValue(),rDirStaddCharge);
			} catch (NullPointerException npe) {
				// do nothing
			}
			setPdfFieldValue(TemporaryCommonField.ENTERPRISE_NAME.getValue(),contract.getWorkPlace().getEnterprise().getRegistry().getFullName());
			setPdfFieldValue(TemporaryCommonField.ENTERPRISE_ADDRESS.getValue(),contract.getWorkPlace().getEnterprise().getRegistry().getDefaultAddress().getFullAddress());
			try {	
				setPdfFieldValue(TemporaryCommonField.ENTERPRISE_COUNTRY.getValue(),contract.getWorkPlace().getEnterprise().getRegistry().getNationality().getName(getLocale()));
				setPdfFieldValue(TemporaryCommonField.ENTERPRISE_COUNTRY_CODE1.getValue(),String.valueOf(contract.getWorkPlace().getEnterprise().getRegistry().getNationality().getIsoNum()).substring(0,1));
				setPdfFieldValue(TemporaryCommonField.ENTERPRISE_COUNTRY_CODE2.getValue(),String.valueOf(contract.getWorkPlace().getEnterprise().getRegistry().getNationality().getIsoNum()).substring(1,2));
				setPdfFieldValue(TemporaryCommonField.ENTERPRISE_COUNTRY_CODE3.getValue(),String.valueOf(contract.getWorkPlace().getEnterprise().getRegistry().getNationality().getIsoNum()).substring(2,3));
			} catch (StringIndexOutOfBoundsException aie) {
				// do nothing
			} catch (NullPointerException npe) {
				// do nothing
			}
			try {	
				RegistryAddress address = contract.getWorkPlace().getEnterprise().getRegistry().getDefaultAddress();
				ResourceBundle bundle = ResourceBundle.getBundle(MUNICIPALITIES_BUNDLE_BASE_NAME);
				setPdfFieldValue(TemporaryCommonField.ENTERPRISE_MUNICIPALITY.getValue(),bundle.getString(address.getMunicipalityCode()));
				setPdfFieldValue(TemporaryCommonField.ENTERPRISE_MUNICIPALITY_CODE1.getValue(),address.getMunicipalityCode().substring(0, 1));
				setPdfFieldValue(TemporaryCommonField.ENTERPRISE_MUNICIPALITY_CODE2.getValue(),address.getMunicipalityCode().substring(1, 2));
				setPdfFieldValue(TemporaryCommonField.ENTERPRISE_MUNICIPALITY_CODE3.getValue(),address.getMunicipalityCode().substring(2, 3));
				setPdfFieldValue(TemporaryCommonField.ENTERPRISE_MUNICIPALITY_CODE4.getValue(),address.getMunicipalityCode().substring(3, 4));
				setPdfFieldValue(TemporaryCommonField.ENTERPRISE_MUNICIPALITY_CODE5.getValue(),address.getMunicipalityCode().substring(4, 5));
			} catch (StringIndexOutOfBoundsException aie) {
				// do nothing
			} catch (NullPointerException npe) {
				// do nothing
			}
			try {	
				setPdfFieldValue(TemporaryCommonField.ENTERPRISE_ZIP1.getValue(),contract.getWorkPlace().getEnterprise().getRegistry().getDefaultAddress().getZip().substring(0, 1));
				setPdfFieldValue(TemporaryCommonField.ENTERPRISE_ZIP2.getValue(),contract.getWorkPlace().getEnterprise().getRegistry().getDefaultAddress().getZip().substring(1, 2));
				setPdfFieldValue(TemporaryCommonField.ENTERPRISE_ZIP3.getValue(),contract.getWorkPlace().getEnterprise().getRegistry().getDefaultAddress().getZip().substring(2, 3));
				setPdfFieldValue(TemporaryCommonField.ENTERPRISE_ZIP4.getValue(),contract.getWorkPlace().getEnterprise().getRegistry().getDefaultAddress().getZip().substring(3, 4));
				setPdfFieldValue(TemporaryCommonField.ENTERPRISE_ZIP5.getValue(),contract.getWorkPlace().getEnterprise().getRegistry().getDefaultAddress().getZip().substring(4, 5));
			} catch (StringIndexOutOfBoundsException aie) {
				// do nothing
			} catch (NullPointerException npe) {
				// do nothing
			}
			/* 
			 * Contract ccc fields
			 */
			if(contract.getEnterpriseCCC()!=null){
				setPdfFieldValue(TemporaryCommonField.CCC_REG1.getValue(),contract.getEnterpriseCCC().getActivity().getQuoteRegimeCode().substring(0, 1));
				setPdfFieldValue(TemporaryCommonField.CCC_REG2.getValue(),contract.getEnterpriseCCC().getActivity().getQuoteRegimeCode().substring(1, 2));
				setPdfFieldValue(TemporaryCommonField.CCC_REG3.getValue(),contract.getEnterpriseCCC().getActivity().getQuoteRegimeCode().substring(2, 3));
				setPdfFieldValue(TemporaryCommonField.CCC_REG4.getValue(),contract.getEnterpriseCCC().getActivity().getQuoteRegimeCode().substring(3, 4));
				if(contract.getEnterpriseCCC().getCcc().length()==11){
					setPdfFieldValue(TemporaryCommonField.CCC_PROV1.getValue(),contract.getEnterpriseCCC().getCcc().substring(0, 1));
					setPdfFieldValue(TemporaryCommonField.CCC_PROV2.getValue(),contract.getEnterpriseCCC().getCcc().substring(1, 2));
					setPdfFieldValue(TemporaryCommonField.CCC_NISS.getValue(),contract.getEnterpriseCCC().getCcc().substring(2, 9));
					setPdfFieldValue(TemporaryCommonField.CCC_CONTROL_DIGIT1.getValue(),contract.getEnterpriseCCC().getCcc().substring(9, 10));
					setPdfFieldValue(TemporaryCommonField.CCC_CONTROL_DIGIT2.getValue(),contract.getEnterpriseCCC().getCcc().substring(10, 11));
				} else {
					setPdfFieldValue(TemporaryCommonField.CCC_NISS.getValue(),contract.getEnterpriseCCC().getCcc());
				}
				setPdfFieldValue(TemporaryCommonField.CCC_ACTIVITY.getValue(),contract.getEnterpriseCCC().getActivity().getDescription());
				setPdfFieldValue(TemporaryCommonField.CCC_ACTIVITY_CODE1.getValue(),contract.getEnterpriseCCC().getActivity().getCnae2009().getCode().substring(0, 1));
				setPdfFieldValue(TemporaryCommonField.CCC_ACTIVITY_CODE2.getValue(),contract.getEnterpriseCCC().getActivity().getCnae2009().getCode().substring(1, 2));
			}
			/* 
			 * Contract workplace fields
			 */
			try {
				GeoZone country = obtainCountry(contract.getWorkPlace().getAddress().getGeozone());
				setPdfFieldValue(TemporaryCommonField.WORKPLACE_COUNTRY.getValue(),country.getName());
				setPdfFieldValue(TemporaryCommonField.WORKPLACE_COUNTRY_CODE1.getValue(),String.valueOf(obtainCountryByValue(country).getIsoNum()).substring(0,1));
				setPdfFieldValue(TemporaryCommonField.WORKPLACE_COUNTRY_CODE2.getValue(),String.valueOf(obtainCountryByValue(country).getIsoNum()).substring(1,2));
				setPdfFieldValue(TemporaryCommonField.WORKPLACE_COUNTRY_CODE3.getValue(),String.valueOf(obtainCountryByValue(country).getIsoNum()).substring(2,3));
			} catch (StringIndexOutOfBoundsException aie) {
				// do nothing
			} catch (NullPointerException npe) {
				// do nothing
			}
			try {
				RegistryAddress address = contract.getWorkPlace().getAddress();
				ResourceBundle bundle = ResourceBundle.getBundle(MUNICIPALITIES_BUNDLE_BASE_NAME);
				setPdfFieldValue(TemporaryCommonField.WORKPLACE_MUNICIPALITY.getValue(),bundle.getString(address.getMunicipalityCode()));
				setPdfFieldValue(TemporaryCommonField.WORKPLACE_MUNICIPALITY_CODE1.getValue(),address.getMunicipalityCode().substring(0, 1));
				setPdfFieldValue(TemporaryCommonField.WORKPLACE_MUNICIPALITY_CODE2.getValue(),address.getMunicipalityCode().substring(1, 2));
				setPdfFieldValue(TemporaryCommonField.WORKPLACE_MUNICIPALITY_CODE3.getValue(),address.getMunicipalityCode().substring(2, 3));
				setPdfFieldValue(TemporaryCommonField.WORKPLACE_MUNICIPALITY_CODE4.getValue(),address.getMunicipalityCode().substring(3, 4));
				setPdfFieldValue(TemporaryCommonField.WORKPLACE_MUNICIPALITY_CODE5.getValue(),address.getMunicipalityCode().substring(4, 5));
			} catch (StringIndexOutOfBoundsException aie) {
				// do nothing
			} catch (NullPointerException npe) {
				// do nothing
			}
			/*
			 * Contract employee fields
			 */
			setPdfFieldValue(TemporaryCommonField.EMPLOYEE_NAME.getValue(),contract.getPerson().getFullName());
			setPdfFieldValue(TemporaryCommonField.EMPLOYEE_NIF.getValue(),contract.getPerson().getRegistry().getDocument());
			if(contract.getPerson().getBirthDate()!=null){
				SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
				setPdfFieldValue(TemporaryCommonField.EMPLOYEE_BIRTH_DATE.getValue(),formatter.format(contract.getPerson().getBirthDate()));
			}
			setPdfFieldValue(TemporaryCommonField.EMPLOYEE_NSS.getValue(),contract.getPerson().getSocialSecurityNumber());
			if(contrata!=null && contrata.getNivelFormativo()!=null){
				setPdfFieldValue(TemporaryCommonField.EMPLOYEE_FORMATION_LEVEL.getValue(),contrata.getNivelFormativo().getDescription());
				setPdfFieldValue(TemporaryCommonField.EMPLOYEE_FORMATION_CODE1.getValue(),contrata.getNivelFormativo().getCode().substring(0, 1));
				setPdfFieldValue(TemporaryCommonField.EMPLOYEE_FORMATION_CODE2.getValue(),contrata.getNivelFormativo().getCode().substring(1, 2));
			}
			try {
				setPdfFieldValue(TemporaryCommonField.EMPLOYEE_COUNTRY.getValue(),String.valueOf(contract.getPerson().getRegistry().getNationality().getName(getLocale())));
				setPdfFieldValue(TemporaryCommonField.EMPLOYEE_COUNTRY_CODE1.getValue(),String.valueOf(contract.getPerson().getRegistry().getNationality().getIsoNum()).substring(0,1));
				setPdfFieldValue(TemporaryCommonField.EMPLOYEE_COUNTRY_CODE2.getValue(),String.valueOf(contract.getPerson().getRegistry().getNationality().getIsoNum()).substring(1,2));
				setPdfFieldValue(TemporaryCommonField.EMPLOYEE_COUNTRY_CODE3.getValue(),String.valueOf(contract.getPerson().getRegistry().getNationality().getIsoNum()).substring(2,3));
			} catch (StringIndexOutOfBoundsException aie) {
				// do nothing
			} catch (NullPointerException npe) {
				// do nothing
			}
			try {
				RegistryAddress address = contract.getPerson().getRegistry().getDefaultAddress();
				ResourceBundle bundle = ResourceBundle.getBundle(MUNICIPALITIES_BUNDLE_BASE_NAME);
				setPdfFieldValue(TemporaryCommonField.EMPLOYEE_ADDRESS_MUNICIPALITY.getValue(),bundle.getString(address.getMunicipalityCode()));
				setPdfFieldValue(TemporaryCommonField.EMPLOYEE_ADDRESS_MUNICIPALITY_CODE1.getValue(),address.getMunicipalityCode().substring(0, 1));
				setPdfFieldValue(TemporaryCommonField.EMPLOYEE_ADDRESS_MUNICIPALITY_CODE2.getValue(),address.getMunicipalityCode().substring(1, 2));
				setPdfFieldValue(TemporaryCommonField.EMPLOYEE_ADDRESS_MUNICIPALITY_CODE3.getValue(),address.getMunicipalityCode().substring(2, 3));
				setPdfFieldValue(TemporaryCommonField.EMPLOYEE_ADDRESS_MUNICIPALITY_CODE4.getValue(),address.getMunicipalityCode().substring(3, 4));
				setPdfFieldValue(TemporaryCommonField.EMPLOYEE_ADDRESS_MUNICIPALITY_CODE5.getValue(),address.getMunicipalityCode().substring(4, 5));
			} catch (StringIndexOutOfBoundsException aie) {
				// do nothing
			} catch (NullPointerException npe) {
				// do nothing
			}
			try {
				GeoZone country = obtainCountry(contract.getPerson().getRegistry().getDefaultAddress().getGeozone());
				setPdfFieldValue(TemporaryCommonField.EMPLOYEE_ADDRESS_COUNTRY.getValue(),country.getName());
				setPdfFieldValue(TemporaryCommonField.EMPLOYEE_ADDRESS_COUNTRY_CODE1.getValue(),String.valueOf(obtainCountryByValue(country).getIsoNum()).substring(0,1));
				setPdfFieldValue(TemporaryCommonField.EMPLOYEE_ADDRESS_COUNTRY_CODE2.getValue(),String.valueOf(obtainCountryByValue(country).getIsoNum()).substring(1,2));
				setPdfFieldValue(TemporaryCommonField.EMPLOYEE_ADDRESS_COUNTRY_CODE3.getValue(),String.valueOf(obtainCountryByValue(country).getIsoNum()).substring(2,3));
			} catch (StringIndexOutOfBoundsException aie) {
				// do nothing
			} catch (NullPointerException npe) {
				// do nothing
			}
			try {
				setPdfFieldValue(TemporaryCommonField.LEGAL_REPRESENTATIVE_NAME.getValue(),"");
				setPdfFieldValue(TemporaryCommonField.LEGAL_REPRESENTATIVE_NIF.getValue(),"");
				setPdfFieldValue(TemporaryCommonField.LEGAL_REPRESENTATIVE_CHARGE.getValue(),"");
			} catch (StringIndexOutOfBoundsException aie) {
				// do nothing
			} catch (NullPointerException npe) {
				// do nothing
			}
			
			
			/*
			 * Contract page 1
			 */
			if(contrata!=null){
				setPdfFieldValue(TemporaryCommonField.PROFESSION.getValue(), contrata.getCno().getTitle());
			}
			setPdfFieldValue(TemporaryCommonField.CATEGORY.getValue(), contract.getCategoryDescription());
			if(StringUtils.isNotBlank(getContractInfoMap(contract).get(TemporaryCommonField.FUNCTIONS.toString()))){
				setPdfFieldValue(TemporaryCommonField.FUNCTIONS.getValue(), getContractInfoMap(contract).get(TemporaryCommonField.FUNCTIONS.toString()));
			}
			
			setPdfFieldValue(TemporaryCommonField.WORKPLACE_FULL_ADDRESS_MORE.getValue(), contract.getWorkPlace().getAddress().getFullAddress()+", "+contract.getWorkPlace().getAddress().getLocation());
			if(StringUtils.isNotBlank(getContractInfoMap(contract).get(TemporaryCommonField.EMPLOYEE_CONTRACT_DISTANCE.toString()))){
				setPdfFieldValue(TemporaryCommonField.EMPLOYEE_CONTRACT_DISTANCE.getValue(), getContractInfoMap(contract).get(TemporaryCommonField.EMPLOYEE_CONTRACT_DISTANCE.toString()));
			}
			if(StringUtils.isNotBlank(getContractInfoMap(contract).get(TemporaryCommonField.EMPLOYEE_CONTRACT_DISTANCE_ADDR.toString()))){
				setPdfFieldValue(TemporaryCommonField.EMPLOYEE_CONTRACT_DISTANCE_ADDR.getValue(), getContractInfoMap(contract).get(TemporaryCommonField.EMPLOYEE_CONTRACT_DISTANCE_ADDR.toString()));
			}
			
			if(code.getValue().startsWith("1") || code.getValue().startsWith("4")){
				setPdfFieldValue(TemporaryCommonField.FULL_TIME.getValue(), "true");
				if(StringUtils.isNotBlank(getContractInfoMap(contract).get(TemporaryCommonField.FULL_TIME_WEEK_HOURS.toString()))){
					setPdfFieldValue(TemporaryCommonField.FULL_TIME_WEEK_HOURS.getValue(), getContractInfoMap(contract).get(TemporaryCommonField.FULL_TIME_WEEK_HOURS.toString()));
				}
				if(StringUtils.isNotBlank(getContractInfoMap(contract).get(TemporaryCommonField.FULL_TIME_START_TIME.toString()))){
					setPdfFieldValue(TemporaryCommonField.FULL_TIME_START_TIME.getValue(), getContractInfoMap(contract).get(TemporaryCommonField.FULL_TIME_START_TIME.toString()));
				}
				if(StringUtils.isNotBlank(getContractInfoMap(contract).get(TemporaryCommonField.FULL_TIME_END_TIME.toString()))){
					setPdfFieldValue(TemporaryCommonField.FULL_TIME_END_TIME.getValue(), getContractInfoMap(contract).get(TemporaryCommonField.FULL_TIME_END_TIME.toString()));
				}
			} else if(code.getValue().startsWith("2") || code.getValue().startsWith("5")){
				setPdfFieldValue(TemporaryCommonField.PARTIALLY_TIME.getValue(), "true");
				if(StringUtils.isNotBlank(getContractDataMap(contract).get(ContextVariable.WEEK_HOURS.toString()))){
					setPdfFieldValue(TemporaryCommonField.PARTIALLY_TIME_HOURS.getValue(), getContractDataMap(contract).get(ContextVariable.WEEK_HOURS.toString()));
					setPdfFieldValue(TemporaryCommonField.PARTIALLY_TIME_WEEKLY.getValue(), "true");
				} else if(contrata!=null){
					if(contrata.getHorasJornada()!=null){
						setPdfFieldValue(TemporaryCommonField.PARTIALLY_TIME_HOURS.getValue(), String.valueOf(Integer.parseInt(contrata.getHorasJornada())));
					}
					if(contrata.getTipoJornada()==TEQPTIEM.TEQPTIEM_D){
						setPdfFieldValue(TemporaryCommonField.PARTIALLY_TIME_DAYLY.getValue(), "true");
					} else if(contrata.getTipoJornada()==TEQPTIEM.TEQPTIEM_S){
						setPdfFieldValue(TemporaryCommonField.PARTIALLY_TIME_WEEKLY.getValue(), "true");
					} else if(contrata.getTipoJornada()==TEQPTIEM.TEQPTIEM_M){
						setPdfFieldValue(TemporaryCommonField.PARTIALLY_TIME_MONTHLY.getValue(), "true");
					} else if(contrata.getTipoJornada()==TEQPTIEM.TEQPTIEM_A){
						setPdfFieldValue(TemporaryCommonField.PARTIALLY_TIME_YEARLY.getValue(), "true");
					}
				}
				if(StringUtils.isNotBlank(getContractInfoMap(contract).get(TemporaryCommonField.PARTIALLY_TIME_JOB_LOWER_THAN.toString()))){
					setPdfFieldValue(TemporaryCommonField.PARTIALLY_TIME_JOB_LOWER_THAN.getValue(), getContractInfoMap(contract).get(TemporaryCommonField.PARTIALLY_TIME_JOB_LOWER_THAN.toString()));
				}
				if(StringUtils.isNotBlank(getContractInfoMap(contract).get(TemporaryCommonField.PARTIALLY_TIME_JOB_DISTRIBUTION.toString()))){
					setPdfFieldValue(TemporaryCommonField.PARTIALLY_TIME_JOB_DISTRIBUTION.getValue(), getContractInfoMap(contract).get(TemporaryCommonField.PARTIALLY_TIME_JOB_DISTRIBUTION.toString()));
				}
			}
			
			dateFormatter.applyPattern("dd/MM/yyyy");
			setPdfFieldValue(TemporaryCommonField.START_DATE.getValue(), dateFormatter.format(contract.getStartDate()));
			if(contract.getEndDate()!=null){
				setPdfFieldValue(TemporaryCommonField.END_DATE.getValue(), dateFormatter.format(contract.getEndDate()));
			}
			
			if(StringUtils.isNotBlank(getContractInfoMap(contract).get(TemporaryCommonField.TRIAL_DURATION.toString()))){
				setPdfFieldValue(TemporaryCommonField.TRIAL_DURATION.getValue(), getContractInfoMap(contract).get(TemporaryCommonField.TRIAL_DURATION.toString()));
			}
			if(StringUtils.isNotBlank(getContractInfoMap(contract).get(TemporaryCommonField.GREATER_DURATION_AGREEMENT_COL.toString()))){
				setPdfFieldValue(TemporaryCommonField.GREATER_DURATION_AGREEMENT_COL.getValue(), getContractInfoMap(contract).get(TemporaryCommonField.GREATER_DURATION_AGREEMENT_COL.toString()));
			}
			
			if(StringUtils.isNotBlank(getContractInfoMap(contract).get(TemporaryCommonField.SALARY_AMOUNT.toString()))){
				setPdfFieldValue(TemporaryCommonField.SALARY_AMOUNT.getValue(), getContractInfoMap(contract).get(TemporaryCommonField.SALARY_AMOUNT.toString()));
			}
			if(StringUtils.isNotBlank(getContractInfoMap(contract).get(TemporaryCommonField.SALARY_PERIOD.toString()))){
				setPdfFieldValue(TemporaryCommonField.SALARY_PERIOD.getValue(), getContractInfoMap(contract).get(TemporaryCommonField.SALARY_PERIOD.toString()));
			}
			if(StringUtils.isNotBlank(getContractInfoMap(contract).get(TemporaryCommonField.SALARY_CONCEPT.toString()))){
				setPdfFieldValue(TemporaryCommonField.SALARY_CONCEPT.getValue(), getContractInfoMap(contract).get(TemporaryCommonField.SALARY_CONCEPT.toString()));
			}
			
			
			/*
			 * Contract page 2
			 */
			if(StringUtils.isNotBlank(getContractInfoMap(contract).get(TemporaryCommonField.HOLIDAYS.toString()))){
				setPdfFieldValue(TemporaryCommonField.HOLIDAYS.getValue(), getContractInfoMap(contract).get(TemporaryCommonField.HOLIDAYS.toString()));
			}
			if(StringUtils.isNotBlank(getContractInfoMap(contract).get(TemporaryCommonField.SEPE_MUNICIPALITY.toString()))){
				setPdfFieldValue(TemporaryCommonField.SEPE_MUNICIPALITY.getValue(), getContractInfoMap(contract).get(TemporaryCommonField.SEPE_MUNICIPALITY.toString()));
			}
			
			/*
			 *  OPTIONS PAGE
			 */
			modelOption = ModelOption.valueOf(getContractInfoMap(contract).get(ContractVariable.CONTRACT_MODEL_OPTION.getValue()));
			
			if(modelOption == ModelOption.TEMPORARY_OPT1){
				setPdfFieldValue(TemporaryOptionField.MAIN_OPT1_CHECK.getValue(),"true");
				setPdfFieldValue(TemporaryOptionField.OPT1_OPTION_CHECK.getValue(),"true");
				if(code == ContractCode.C401){
					setPdfFieldValue(TemporaryOptionField.OPT1_TC2_401.getValue(),"true");
				} else if(code == ContractCode.C501){
					setPdfFieldValue(TemporaryOptionField.OPT1_TC2_501.getValue(),"true");
				}
				if(StringUtils.isNotBlank(getContractInfoMap(contract).get(TemporaryOptionField.OPT1_WORK_DESCRIPTION1.toString()))){
					setPdfFieldValue(TemporaryOptionField.OPT1_WORK_DESCRIPTION1.getValue(), getContractInfoMap(contract).get(TemporaryOptionField.OPT1_WORK_DESCRIPTION1.toString()));
				}
				if(StringUtils.isNotBlank(getContractInfoMap(contract).get(TemporaryOptionField.OPT1_WORK_DESCRIPTION2.toString()))){
					setPdfFieldValue(TemporaryOptionField.OPT1_WORK_DESCRIPTION2.getValue(), getContractInfoMap(contract).get(TemporaryOptionField.OPT1_WORK_DESCRIPTION2.toString()));
				}
			} else if(modelOption == ModelOption.TEMPORARY_OPT2){
				setPdfFieldValue(TemporaryOptionField.MAIN_OPT2_CHECK.getValue(),"true");
				setPdfFieldValue(TemporaryOptionField.OPT2_OPTION_CHECK.getValue(),"true");
				if(code == ContractCode.C402){
					setPdfFieldValue(TemporaryOptionField.OPT2_TC2_402.getValue(),"true");
				} else if(code == ContractCode.C502){
					setPdfFieldValue(TemporaryOptionField.OPT2_TC2_502.getValue(),"true");
				}
				if(StringUtils.isNotBlank(getContractInfoMap(contract).get(TemporaryOptionField.OPT2_WORK_DESCRIPTION1.toString()))){
					setPdfFieldValue(TemporaryOptionField.OPT2_WORK_DESCRIPTION1.getValue(), getContractInfoMap(contract).get(TemporaryOptionField.OPT2_WORK_DESCRIPTION1.toString()));
				}
				if(StringUtils.isNotBlank(getContractInfoMap(contract).get(TemporaryOptionField.OPT2_WORK_DESCRIPTION2.toString()))){
					setPdfFieldValue(TemporaryOptionField.OPT2_WORK_DESCRIPTION2.getValue(), getContractInfoMap(contract).get(TemporaryOptionField.OPT2_WORK_DESCRIPTION2.toString()));
				}
			} else if(modelOption == ModelOption.TEMPORARY_OPT3){
				setPdfFieldValue(TemporaryOptionField.MAIN_OPT3_CHECK.getValue(),"true");
				setPdfFieldValue(TemporaryOptionField.OPT3_OPTION_CHECK.getValue(),"true");
				if(code == ContractCode.C410){
					setPdfFieldValue(TemporaryOptionField.OPT3_TC2_410.getValue(),"true");
				} else if(code == ContractCode.C510){
					setPdfFieldValue(TemporaryOptionField.OPT3_TC2_510.getValue(),"true");
				}
				if(StringUtils.isNotBlank(getContractInfoMap(contract).get(TemporaryOptionField.OPT3_REPLACED_WORKER_NAME.toString()))){
					setPdfFieldValue(TemporaryOptionField.OPT3_REPLACED_WORKER_NAME.getValue(), getContractInfoMap(contract).get(TemporaryOptionField.OPT3_REPLACED_WORKER_NAME.toString()));
				}
				
				if(contrata!=null){					
					if(contrata.getCausaInterinidad()==TEIINTER.TEIINTER_A){
//						TEIINTER_A( "A", "TRABAJADOR CON DERECHO RESERVA DE PUESTO", "19800315", "0" ),
						setPdfFieldValue(TemporaryOptionField.OPT3_CAUSE1.getValue(),"true");
					}else if(contrata.getCausaInterinidad()==TEIINTER.TEIINTER_B){
//						TEIINTER_B( "B", "TRABAJADOR POR MATERNIDAD SIN BONIFICACION DE CUOTAS", "19800315", "0" ),
						setPdfFieldValue(TemporaryOptionField.OPT3_CAUSE2.getValue(),"true");
					}else if(contrata.getCausaInterinidad()==TEIINTER.TEIINTER_C){
//						TEIINTER_C( "C", "EXCEDENCIA CUIDADO HIJO PERCEPTOR PRESTA", "19950501", "19991106" ),
					}else if(contrata.getCausaInterinidad()==TEIINTER.TEIINTER_D){
//						TEIINTER_D( "D", "EXCEDENCIA CUIDADO HIJO NO PERCEP.PRESTA", "19950501", "19970516" ),
					}else if(contrata.getCausaInterinidad()==TEIINTER.TEIINTER_E){
//						TEIINTER_E( "E", "TRABAJADOR PROCESO DE SELECCION/PROMOCION", "19800315", "0" ),
						setPdfFieldValue(TemporaryOptionField.OPT3_CAUSE4.getValue(),"true");
					}else if(contrata.getCausaInterinidad()==TEIINTER.TEIINTER_F){
//						TEIINTER_F( "F", "MATERNIDAD CON BONIFICACION DE CUOTAS", "19980906", "0" ),
						setPdfFieldValue(TemporaryOptionField.OPT3_CAUSE1_BONUS.getValue(),"true");
					}else if(contrata.getCausaInterinidad()==TEIINTER.TEIINTER_G){
//						TEIINTER_G( "G", "ADOPCION", "19980906", "0" ),
						setPdfFieldValue(TemporaryOptionField.OPT3_CAUSE3_BONUS.getValue(),"true");
					}else if(contrata.getCausaInterinidad()==TEIINTER.TEIINTER_H){
//						TEIINTER_H( "H", "ACOGIMIENTO", "19980906", "0" ),
						setPdfFieldValue(TemporaryOptionField.OPT3_CAUSE4_BONUS.getValue(),"true");
					}else if(contrata.getCausaInterinidad()==TEIINTER.TEIINTER_I){
//						TEIINTER_I( "I", "RIESGO DURANTE EMBARAZO", "19991107", "0" ),
						setPdfFieldValue(TemporaryOptionField.OPT3_CAUSE5_BONUS.getValue(),"true");
					}else if(contrata.getCausaInterinidad()==TEIINTER.TEIINTER_J){
//						TEIINTER_J( "J", "TRABAJ.EN FORMACION POR PERCEPTOR PRESTA", "20020526", "0" ),
						setPdfFieldValue(TemporaryOptionField.OPT3_CAUSE5.getValue(),"true");
					}else if(contrata.getCausaInterinidad()==TEIINTER.TEIINTER_K){
//						TEIINTER_K( "K", "MINUSVALIDOS DESEMPLEADOS POR MINUSV.INCAP.TEMP", "20021214", "0" ),
						setPdfFieldValue(TemporaryOptionField.OPT3_CAUSE6.getValue(),"true");
					}else if(contrata.getCausaInterinidad()==TEIINTER.TEIINTER_L){
//						TEIINTER_L( "L", "EXCEDENCIA CUIDADO FAMILIAR PERCEP.PREST", "19991107", "0" ),
						setPdfFieldValue(TemporaryOptionField.OPT3_CAUSE3.getValue(),"true");
					}else if(contrata.getCausaInterinidad()==TEIINTER.TEIINTER_M){
//						TEIINTER_M( "M", "SUSTITUCIÓN VÍCTIMAS VIOLENCIA DE GÉNERO", "20050128", "0" ),
						setPdfFieldValue(TemporaryOptionField.OPT3_CAUSE7.getValue(),"true");
//						setPdfFieldValue(TemporaryOptionField.OPT3_CAUSE7_OPT1.getValue(),"true");
//						setPdfFieldValue(TemporaryOptionField.OPT3_CAUSE7_OPT2.getValue(),"true");
					}else if(contrata.getCausaInterinidad()==TEIINTER.TEIINTER_N){
//						TEIINTER_N( "N", "PATERNIDAD", "20070324", "0" ),
						setPdfFieldValue(TemporaryOptionField.OPT3_CAUSE2_BONUS.getValue(),"true");
					}else if(contrata.getCausaInterinidad()==TEIINTER.TEIINTER_O){
//						TEIINTER_O( "O", "RIESGO DURANTE LA LACTANCIA NATURAL", "20070324", "0" ),
						setPdfFieldValue(TemporaryOptionField.OPT3_CAUSE6_BONUS.getValue(),"true");
					}
				}
			} else if(modelOption == ModelOption.TEMPORARY_OPT4){
				setPdfFieldValue(TemporaryOptionField.MAIN_OPT4_CHECK.getValue(),"true");
				setPdfFieldValue(TemporaryOptionField.OPT4_OPTION_CHECK.getValue(),"true");
			} else if(modelOption == ModelOption.TEMPORARY_OPT5){
				setPdfFieldValue(TemporaryOptionField.MAIN_OPT5_CHECK.getValue(),"true");
				setPdfFieldValue(TemporaryOptionField.OPT5_OPTION_CHECK.getValue(),"true");
			} else if(modelOption == ModelOption.TEMPORARY_OPT6){
				setPdfFieldValue(TemporaryOptionField.MAIN_OPT6_CHECK.getValue(),"true");
				setPdfFieldValue(TemporaryOptionField.OPT6_OPTION_CHECK.getValue(),"true");
			} else if(modelOption == ModelOption.TEMPORARY_OPT7){
				setPdfFieldValue(TemporaryOptionField.MAIN_OPT7_CHECK.getValue(),"true");
				setPdfFieldValue(TemporaryOptionField.OPT7_OPTION_CHECK.getValue(),"true");
			} else if(modelOption == ModelOption.TEMPORARY_OPT8){
				setPdfFieldValue(TemporaryOptionField.MAIN_OPT8_CHECK.getValue(),"true");
				setPdfFieldValue(TemporaryOptionField.OPT8_OPTION_CHECK.getValue(),"true");
				if(contrata!=null){
					setPdfFieldValue(TemporaryOptionField.OPT8_REDUCTION_PERCENT.getValue(),contrata.getPorcentajeJubilacionParcial()+"%");
				}
			} else if(modelOption == ModelOption.TEMPORARY_OPT9){
				setPdfFieldValue(TemporaryOptionField.MAIN_OPT9_CHECK.getValue(),"true");
				setPdfFieldValue(TemporaryOptionField.OPT9_OPTION_CHECK.getValue(),"true");
			} else if(modelOption == ModelOption.TEMPORARY_OPT10){
				setPdfFieldValue(TemporaryOptionField.MAIN_OPT10_CHECK.getValue(),"true");
				setPdfFieldValue(TemporaryOptionField.OPT10_OPTION_CHECK.getValue(),"true");
			} else if(modelOption == ModelOption.TEMPORARY_OPT11){
				setPdfFieldValue(TemporaryOptionField.MAIN_OPT11_CHECK.getValue(),"true");
				setPdfFieldValue(TemporaryOptionField.OPT11_OPTION_CHECK.getValue(),"true");
				if(code == ContractCode.C401){
					setPdfFieldValue(TemporaryOptionField.OPT11_FULL_TIME.getValue(),"true");
					setPdfFieldValue(TemporaryOptionField.OPT11_TC2_401.getValue(),"true");
				} else if(code == ContractCode.C402){
					setPdfFieldValue(TemporaryOptionField.OPT11_FULL_TIME.getValue(),"true");
					setPdfFieldValue(TemporaryOptionField.OPT11_TC2_402.getValue(),"true");
				} else if(code == ContractCode.C410){
					setPdfFieldValue(TemporaryOptionField.OPT11_FULL_TIME.getValue(),"true");
					setPdfFieldValue(TemporaryOptionField.OPT11_TC2_410.getValue(),"true");
				} else if(code == ContractCode.C450){
					setPdfFieldValue(TemporaryOptionField.OPT11_FULL_TIME.getValue(),"true");
					setPdfFieldValue(TemporaryOptionField.OPT11_TC2_450.getValue(),"true");
				} else if(code == ContractCode.C990){
					setPdfFieldValue(TemporaryOptionField.OPT11_FULL_TIME.getValue(),"true");
					setPdfFieldValue(TemporaryOptionField.OPT11_TC2_990_FULL_TIME.getValue(),"true");
				} else if(code == ContractCode.C501){
					setPdfFieldValue(TemporaryOptionField.OPT11_PARTIALLY_TIME.getValue(),"true");
					setPdfFieldValue(TemporaryOptionField.OPT11_TC2_501.getValue(),"true");
				} else if(code == ContractCode.C502){
					setPdfFieldValue(TemporaryOptionField.OPT11_PARTIALLY_TIME.getValue(),"true");
					setPdfFieldValue(TemporaryOptionField.OPT11_TC2_502.getValue(),"true");
				} else if(code == ContractCode.C510){
					setPdfFieldValue(TemporaryOptionField.OPT11_PARTIALLY_TIME.getValue(),"true");
					setPdfFieldValue(TemporaryOptionField.OPT11_TC2_510.getValue(),"true");
				} else if(code == ContractCode.C550){
					setPdfFieldValue(TemporaryOptionField.OPT11_PARTIALLY_TIME.getValue(),"true");
					setPdfFieldValue(TemporaryOptionField.OPT11_TC2_550.getValue(),"true");
				} else if(code == ContractCode.C990){
					setPdfFieldValue(TemporaryOptionField.OPT11_PARTIALLY_TIME.getValue(),"true");
					setPdfFieldValue(TemporaryOptionField.OPT11_TC2_990_PARTIALLY_TIME.getValue(),"true");
				}				
				if(contrata!=null){
					if(contrata.getCodigoProgramaEmpleo() == TETPGMEM.TETPGMEM_01){
//						TETPGMEM_01( "01", "FOMENTO EMPLEO AGRARIO", null, null ),
						setPdfFieldValue(TemporaryOptionField.OPT11_AGRICULTURAL_PROMOTION_CHECK.getValue(),"true");
					} else if(contrata.getCodigoProgramaEmpleo() == TETPGMEM.TETPGMEM_02){
//						TETPGMEM_02( "02", "INSERCIÓN CORPORACIÓN LOCAL", null, null ),
						setPdfFieldValue(TemporaryOptionField.OPT11_AGRICULTURAL_PROMOTION_CHECK.getValue(),"true");
						setPdfFieldValue(TemporaryOptionField.OPT11_EMPLOYER_LOCAL_CORPORATION_CHECK.getValue(),"true");
					} else if(contrata.getCodigoProgramaEmpleo() == TETPGMEM.TETPGMEM_03){
//						TETPGMEM_03( "03", "INSERCIÓN (ÓRGANOS ADMINISTRAC. ESTADO)", null, null ),
						setPdfFieldValue(TemporaryOptionField.OPT11_AGRICULTURAL_PROMOTION_CHECK.getValue(),"true");
						setPdfFieldValue(TemporaryOptionField.OPT11_EMPLOYER_STATE_ADMINISTRATION_CHECK.getValue(),"true");
					} else if(contrata.getCodigoProgramaEmpleo() == TETPGMEM.TETPGMEM_04){
//						TETPGMEM_04( "04", "INSERCIÓN (COMUNIDAD AUTÓNOMA)", null, null ),
						setPdfFieldValue(TemporaryOptionField.OPT11_AGRICULTURAL_PROMOTION_CHECK.getValue(),"true");
						setPdfFieldValue(TemporaryOptionField.OPT11_EMPLOYER_COMMUNITY_CHECK.getValue(),"true");
					} else if(contrata.getCodigoProgramaEmpleo() == TETPGMEM.TETPGMEM_05){
//						TETPGMEM_05( "05", "INSERCIÓN (ENTIDAD SIN ANIMO DE LUCRO)", null, null ),
						setPdfFieldValue(TemporaryOptionField.OPT11_AGRICULTURAL_PROMOTION_CHECK.getValue(),"true");
						setPdfFieldValue(TemporaryOptionField.OPT11_EMPLOYER_NONPROFIT_ENTITY_CHECK.getValue(),"true");
					} else if(contrata.getCodigoProgramaEmpleo() == TETPGMEM.TETPGMEM_06){
//						TETPGMEM_06( "06", "INSERCIÓN (UNIVERSIDAD)", null, null ),
						setPdfFieldValue(TemporaryOptionField.OPT11_AGRICULTURAL_PROMOTION_CHECK.getValue(),"true");
						setPdfFieldValue(TemporaryOptionField.OPT11_EMPLOYER_UNIVERSITY_CHECK.getValue(),"true");
					} else if(contrata.getCodigoProgramaEmpleo() == TETPGMEM.TETPGMEM_07){
//						TETPGMEM_07( "07", "SUBSIDIO AGRARIO(ORGANISMOS INVERSORES)", null, null ),
					} else if(contrata.getCodigoProgramaEmpleo() == TETPGMEM.TETPGMEM_08){
//						TETPGMEM_08( "08", "AGENTES DE EMPLEO Y DESARROLLO LOCAL", null, null ),
					} else if(contrata.getCodigoProgramaEmpleo() == TETPGMEM.TETPGMEM_09){
//						TETPGMEM_09( "09", "ESTUDIOS Y CAMPAÑAS", null, null ),
					} else if(contrata.getCodigoProgramaEmpleo() == TETPGMEM.TETPGMEM_10){
//						TETPGMEM_10( "10", "PROGRAMA DE EMPLEO I+E", null, null ),
					} else if(contrata.getCodigoProgramaEmpleo() == TETPGMEM.TETPGMEM_12){
//						TETPGMEM_12( "12", "INTERES SOCIAL (CORPORACION LOCAL)", null, null ),
						setPdfFieldValue(TemporaryOptionField.OPT11_SOCIAL_INTERES_CHECK.getValue(),"true");
						setPdfFieldValue(TemporaryOptionField.OPT11_EMPLOYER_LOCAL_CORPORATION_CHECK.getValue(),"true");
					} else if(contrata.getCodigoProgramaEmpleo() == TETPGMEM.TETPGMEM_13){
//						TETPGMEM_13( "13", "INTERES SOCIAL (ORGANOS AD.ESTADO O CCAA)", null, null ),
						setPdfFieldValue(TemporaryOptionField.OPT11_SOCIAL_INTERES_CHECK.getValue(),"true");
						setPdfFieldValue(TemporaryOptionField.OPT11_EMPLOYER_STATE_ADMINISTRATION_CHECK.getValue(),"true");
					} else if(contrata.getCodigoProgramaEmpleo() == TETPGMEM.TETPGMEM_14){
//						TETPGMEM_14( "14", "INTERES SOCIAL (COMUNIDAD AUTONOMA)", null, null ),
						setPdfFieldValue(TemporaryOptionField.OPT11_SOCIAL_INTERES_CHECK.getValue(),"true");
						setPdfFieldValue(TemporaryOptionField.OPT11_EMPLOYER_COMMUNITY_CHECK.getValue(),"true");
					} else if(contrata.getCodigoProgramaEmpleo() == TETPGMEM.TETPGMEM_15){
//						TETPGMEM_15( "15", "INTERES SOCIAL (ENTIDAD SIN ANIMO DE LUCRO)", null, null ),
						setPdfFieldValue(TemporaryOptionField.OPT11_SOCIAL_INTERES_CHECK.getValue(),"true");
						setPdfFieldValue(TemporaryOptionField.OPT11_EMPLOYER_NONPROFIT_ENTITY_CHECK.getValue(),"true");
					} else if(contrata.getCodigoProgramaEmpleo() == TETPGMEM.TETPGMEM_16){
//						TETPGMEM_16( "16", "INTERES SOCIAL (UNIVERSIDAD)", null, null ),
						setPdfFieldValue(TemporaryOptionField.OPT11_SOCIAL_INTERES_CHECK.getValue(),"true");
						setPdfFieldValue(TemporaryOptionField.OPT11_EMPLOYER_UNIVERSITY_CHECK.getValue(),"true");
					}
				}
				if(contract.getAgreementLevelCategory()!=null && contract.getAgreementLevelCategory().getLevel()!=null
					 && contract.getAgreementLevelCategory().getLevel().getAgreement()!=null){
					setPdfFieldValue(TemporaryOptionField.OPT11_COLLECTIVE_AGREEMENT.getValue(),contract.getAgreementLevelCategory().getLevel().getAgreement().getDescription());
				}
			} else if(modelOption == ModelOption.TEMPORARY_OPT12){
				setPdfFieldValue(TemporaryOptionField.MAIN_OPT12_CHECK.getValue(),"true");
				setPdfFieldValue(TemporaryOptionField.OPT12_OPTION_CHECK.getValue(),"true");
				if(code == ContractCode.C401){
					setPdfFieldValue(TemporaryOptionField.OPT12_FULL_TIME.getValue(),"true");
					setPdfFieldValue(TemporaryOptionField.OPT12_TC2_401.getValue(),"true");
				} else if(code == ContractCode.C410){
					setPdfFieldValue(TemporaryOptionField.OPT12_FULL_TIME.getValue(),"true");
					setPdfFieldValue(TemporaryOptionField.OPT12_TC2_410.getValue(),"true");
				} else if(code == ContractCode.C501){
					setPdfFieldValue(TemporaryOptionField.OPT12_PARTIALLY_TIME.getValue(),"true");
					setPdfFieldValue(TemporaryOptionField.OPT12_TC2_501.getValue(),"true");
				} else if(code == ContractCode.C510){
					setPdfFieldValue(TemporaryOptionField.OPT12_PARTIALLY_TIME.getValue(),"true");
					setPdfFieldValue(TemporaryOptionField.OPT12_TC2_510.getValue(),"true");
				}
				String key = getContractInfoMap(contract).get(TemporaryOptionField.OPT12_ONSITE_HOURS.toString());
				if(StringUtils.isNotBlank(key)){
					if(TemporaryOptionField.OPT12_ONSITE_HOURS_YES.toString().equals(key)){
						setPdfFieldValue(TemporaryOptionField.OPT12_ONSITE_HOURS_YES.getValue(), "true");
					} else if(TemporaryOptionField.OPT12_ONSITE_HOURS_NO.toString().equals(key)){
						setPdfFieldValue(TemporaryOptionField.OPT12_ONSITE_HOURS_NO.getValue(), "true");
					}
				}
				if(StringUtils.isNotBlank(getContractInfoMap(contract).get(TemporaryOptionField.OPT12_ONSITE_WEEK_HOURS.toString()))){
					setPdfFieldValue(TemporaryOptionField.OPT12_ONSITE_WEEK_HOURS.getValue(), getContractInfoMap(contract).get(TemporaryOptionField.OPT12_ONSITE_WEEK_HOURS.toString()));
				}
				if(StringUtils.isNotBlank(getContractInfoMap(contract).get(TemporaryOptionField.OPT12_ONSITE_HOURS_DISTRIBUTION.toString()))){
					setPdfFieldValue(TemporaryOptionField.OPT12_ONSITE_HOURS_DISTRIBUTION.getValue(), getContractInfoMap(contract).get(TemporaryOptionField.OPT12_ONSITE_HOURS_DISTRIBUTION.toString()));
				}
				key = getContractInfoMap(contract).get(TemporaryOptionField.OPT12_SALARY_OPT.toString());
				if(StringUtils.isNotBlank(key)){
					if(TemporaryOptionField.OPT12_SALARY_OPT1.toString().equals(key)){
						setPdfFieldValue(TemporaryOptionField.OPT12_SALARY_OPT1.getValue(), "true");
					} else if(TemporaryOptionField.OPT12_SALARY_OPT2.toString().equals(key)){
						setPdfFieldValue(TemporaryOptionField.OPT12_SALARY_OPT2.getValue(), "true");
					} else if(TemporaryOptionField.OPT12_SALARY_OPT3.toString().equals(key)){
						setPdfFieldValue(TemporaryOptionField.OPT12_SALARY_OPT3.getValue(), "true");
					}
				}
				key = getContractInfoMap(contract).get(TemporaryOptionField.OPT12_OVERNIGHT.toString());
				if(StringUtils.isNotBlank(key)){
					if(TemporaryOptionField.OPT12_OVERNIGHT_YES.toString().equals(key)){
						setPdfFieldValue(TemporaryOptionField.OPT12_OVERNIGHT_YES.getValue(), "true");
					} else if(TemporaryOptionField.OPT12_OVERNIGHT_NO.toString().equals(key)){
						setPdfFieldValue(TemporaryOptionField.OPT12_OVERNIGHT_NO.getValue(), "true");
					}
				}
				if(StringUtils.isNotBlank(getContractInfoMap(contract).get(TemporaryOptionField.OPT12_OVERNIGHT_WEEK_DAYS.toString()))){
					setPdfFieldValue(TemporaryOptionField.OPT12_OVERNIGHT_WEEK_DAYS.getValue(), getContractInfoMap(contract).get(TemporaryOptionField.OPT12_OVERNIGHT_WEEK_DAYS.toString()));
				}
			} else if(modelOption == ModelOption.TEMPORARY_OPT13){
				setPdfFieldValue(TemporaryOptionField.MAIN_OPT13_CHECK.getValue(),"true");
				setPdfFieldValue(TemporaryOptionField.OPT13_OPTION_CHECK.getValue(),"true");
			} else if(modelOption == ModelOption.TEMPORARY_OPT14){
				setPdfFieldValue(TemporaryOptionField.MAIN_OPT14_CHECK.getValue(),"true");
				setPdfFieldValue(TemporaryOptionField.OPT14_OPTION_CHECK.getValue(),"true");
				if(code == ContractCode.C401){
					setPdfFieldValue(TemporaryOptionField.OPT14_FULL_TIME.getValue(),"true");
					setPdfFieldValue(TemporaryOptionField.OPT14_TC2_401.getValue(),"true");
				} else if(code == ContractCode.C402){
					setPdfFieldValue(TemporaryOptionField.OPT14_FULL_TIME.getValue(),"true");
					setPdfFieldValue(TemporaryOptionField.OPT14_TC2_402.getValue(),"true");
				} else if(code == ContractCode.C410){
					setPdfFieldValue(TemporaryOptionField.OPT14_FULL_TIME.getValue(),"true");
					setPdfFieldValue(TemporaryOptionField.OPT14_TC2_410.getValue(),"true");
				} else if(code == ContractCode.C430){
					setPdfFieldValue(TemporaryOptionField.OPT14_FULL_TIME.getValue(),"true");
					setPdfFieldValue(TemporaryOptionField.OPT14_TC2_430.getValue(),"true");
				} else if(code == ContractCode.C441){
					setPdfFieldValue(TemporaryOptionField.OPT14_FULL_TIME.getValue(),"true");
					setPdfFieldValue(TemporaryOptionField.OPT14_TC2_441.getValue(),"true");
				} else if(code == ContractCode.C990){
					setPdfFieldValue(TemporaryOptionField.OPT14_FULL_TIME.getValue(),"true");
					setPdfFieldValue(TemporaryOptionField.OPT14_TC2_990_FULL_TIME.getValue(),"true");
				} else if(code == ContractCode.C501){
					setPdfFieldValue(TemporaryOptionField.OPT14_PARTIALLY_TIME.getValue(),"true");
					setPdfFieldValue(TemporaryOptionField.OPT14_TC2_501.getValue(),"true");
				} else if(code == ContractCode.C502){
					setPdfFieldValue(TemporaryOptionField.OPT14_PARTIALLY_TIME.getValue(),"true");
					setPdfFieldValue(TemporaryOptionField.OPT14_TC2_502.getValue(),"true");
				} else if(code == ContractCode.C510){
					setPdfFieldValue(TemporaryOptionField.OPT14_PARTIALLY_TIME.getValue(),"true");
					setPdfFieldValue(TemporaryOptionField.OPT14_TC2_510.getValue(),"true");
				} else if(code == ContractCode.C530){
					setPdfFieldValue(TemporaryOptionField.OPT14_PARTIALLY_TIME.getValue(),"true");
					setPdfFieldValue(TemporaryOptionField.OPT14_TC2_530.getValue(),"true");
				} else if(code == ContractCode.C540){
					setPdfFieldValue(TemporaryOptionField.OPT14_PARTIALLY_TIME.getValue(),"true");
					setPdfFieldValue(TemporaryOptionField.OPT14_TC2_540.getValue(),"true");
				} else if(code == ContractCode.C541){
					setPdfFieldValue(TemporaryOptionField.OPT14_PARTIALLY_TIME.getValue(),"true");
					setPdfFieldValue(TemporaryOptionField.OPT14_TC2_541.getValue(),"true");
				} else if(code == ContractCode.C990){
					setPdfFieldValue(TemporaryOptionField.OPT14_PARTIALLY_TIME.getValue(),"true");
					setPdfFieldValue(TemporaryOptionField.OPT14_TC2_990_PARTIALLY_TIME.getValue(),"true");
				}
				if(StringUtils.isNotBlank(getContractInfoMap(contract).get(TemporaryOptionField.OPT14_TRIAL_PERIOD.toString()))){
					setPdfFieldValue(TemporaryOptionField.OPT14_TRIAL_PERIOD.getValue(), getContractInfoMap(contract).get(TemporaryOptionField.OPT14_TRIAL_PERIOD.toString()));
				}
				if(StringUtils.isNotBlank(getContractInfoMap(contract).get(TemporaryOptionField.OPT14_TRIAL_TERMS.toString()))){
					setPdfFieldValue(TemporaryOptionField.OPT14_TRIAL_TERMS.getValue(), getContractInfoMap(contract).get(TemporaryOptionField.OPT14_TRIAL_TERMS.toString()));
				}
				if(StringUtils.isNotBlank(getContractInfoMap(contract).get(TemporaryOptionField.OPT14_PROFESSION.toString()))){
					setPdfFieldValue(TemporaryOptionField.OPT14_PROFESSION.getValue(), getContractInfoMap(contract).get(TemporaryOptionField.OPT14_PROFESSION.toString()));
				}
				if(StringUtils.isNotBlank(getContractInfoMap(contract).get(TemporaryOptionField.OPT14_DISTANCE_ADJUSTMENT.toString()))){
					setPdfFieldValue(TemporaryOptionField.OPT14_DISTANCE_ADJUSTMENT.getValue(), getContractInfoMap(contract).get(TemporaryOptionField.OPT14_DISTANCE_ADJUSTMENT.toString()));
				}
				if(StringUtils.isNotBlank(getContractInfoMap(contract).get(TemporaryOptionField.OPT14_DISTANCE_ADJUSTMENT_MORE.toString()))){
					setPdfFieldValue(TemporaryOptionField.OPT14_DISTANCE_ADJUSTMENT_MORE.getValue(), getContractInfoMap(contract).get(TemporaryOptionField.OPT14_DISTANCE_ADJUSTMENT_MORE.toString()));
				}
				if(contract.getAgreementLevelCategory()!=null && contract.getAgreementLevelCategory().getLevel()!=null
					 && contract.getAgreementLevelCategory().getLevel().getAgreement()!=null){
					setPdfFieldValue(TemporaryOptionField.OPT14_COLLECTIVE_AGREEMENT.getValue(),contract.getAgreementLevelCategory().getLevel().getAgreement().getDescription());
				}
			} else if(modelOption == ModelOption.TEMPORARY_OPT15){
				setPdfFieldValue(TemporaryOptionField.MAIN_OPT15_CHECK.getValue(),"true");
				setPdfFieldValue(TemporaryOptionField.OPT15_OPTION_CHECK.getValue(),"true");
			} else if(modelOption == ModelOption.TEMPORARY_OPT16){
				setPdfFieldValue(TemporaryOptionField.MAIN_OPT16_CHECK.getValue(),"true");
				setPdfFieldValue(TemporaryOptionField.OPT16_OPTION_CHECK.getValue(),"true");
			} else if(modelOption == ModelOption.TEMPORARY_OPT17){
				setPdfFieldValue(TemporaryOptionField.MAIN_OPT17_CHECK.getValue(),"true");
				setPdfFieldValue(TemporaryOptionField.OPT17_OPTION_CHECK.getValue(),"true");
			} else if(modelOption == ModelOption.TEMPORARY_OPT18){
				setPdfFieldValue(TemporaryOptionField.MAIN_OPT18_CHECK.getValue(),"true");
				setPdfFieldValue(TemporaryOptionField.OPT18_OPTION_CHECK.getValue(),"true");
			}
			
		} catch (IOException e) {
			// do nothing
		} catch (ManagerBeanException e) {
			// do nothing
		}
	}

	
}
	
	