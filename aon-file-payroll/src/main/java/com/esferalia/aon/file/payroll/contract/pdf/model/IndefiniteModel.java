package com.esferalia.aon.file.payroll.contract.pdf.model;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ResourceBundle;

import org.apache.commons.lang.StringUtils;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.geozone.GeoZone;
import com.code.aon.registry.RegistryAddress;
import com.code.aon.registry.RegistryDirStaff;
import com.esferalia.aon.file.payroll.contract.pdf.PdfFieldIndefinite;
import com.esferalia.aon.file.payroll.contract.pdf.ModelOption;
import com.esferalia.aon.file.payroll.contract.pdf.UnsupportedContractDocumentException;
import com.esferalia.aon.file.payroll.contrata.ContrataContratoParams;
import com.esferalia.aon.file.payroll.contrata.IContrataParams;
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.payroll.ContractInfo.ContractVariable;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.payroll.enumeration.ContractCode;
import com.esferalia.aon.payroll.enumeration.contrata.TEQPTIEM;
import com.lowagie.text.pdf.PdfReader;



public class IndefiniteModel extends AbstractContractModel {
	
	public final static String MODEL_NAME = "Indefinido";
	
	public IndefiniteModel(){
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
			setPdfFieldValue(PdfFieldIndefinite.ENTERPRISE_CIF.getValue(),contract.getWorkPlace().getEnterprise().getRegistry().getDocument());
			RegistryDirStaff rDirStaff = obtainRegistryDirStaff(contract.getWorkPlace().getEnterprise().getRegistry()); 
			try {
				setPdfFieldValue(PdfFieldIndefinite.ENTERPRISE_DIR_STAFF_NAME.getValue(),rDirStaff.getName());
				setPdfFieldValue(PdfFieldIndefinite.ENTERPRISE_DIR_STAFF_NIF.getValue(),rDirStaff.getDocument());
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
				setPdfFieldValue(PdfFieldIndefinite.ENTERPRISE_DIR_STAFF_CHARGE.getValue(),rDirStaddCharge);
			} catch (NullPointerException npe) {
				// do nothing
			}
			setPdfFieldValue(PdfFieldIndefinite.ENTERPRISE_NAME.getValue(),contract.getWorkPlace().getEnterprise().getRegistry().getFullName());
			setPdfFieldValue(PdfFieldIndefinite.ENTERPRISE_ADDRESS.getValue(),contract.getWorkPlace().getEnterprise().getRegistry().getDefaultAddress().getFullAddress());
			try {	
				setPdfFieldValue(PdfFieldIndefinite.ENTERPRISE_COUNTRY.getValue(),contract.getWorkPlace().getEnterprise().getRegistry().getNationality().getName(getLocale()));
				setPdfFieldValue(PdfFieldIndefinite.ENTERPRISE_COUNTRY_CODE1.getValue(),String.valueOf(contract.getWorkPlace().getEnterprise().getRegistry().getNationality().getIsoNum()).substring(0,1));
				setPdfFieldValue(PdfFieldIndefinite.ENTERPRISE_COUNTRY_CODE2.getValue(),String.valueOf(contract.getWorkPlace().getEnterprise().getRegistry().getNationality().getIsoNum()).substring(1,2));
				setPdfFieldValue(PdfFieldIndefinite.ENTERPRISE_COUNTRY_CODE3.getValue(),String.valueOf(contract.getWorkPlace().getEnterprise().getRegistry().getNationality().getIsoNum()).substring(2,3));
			} catch (StringIndexOutOfBoundsException aie) {
				// do nothing
			} catch (NullPointerException npe) {
				// do nothing
			}
			try {	
				RegistryAddress address = contract.getWorkPlace().getEnterprise().getRegistry().getDefaultAddress();
				ResourceBundle bundle = ResourceBundle.getBundle(MUNICIPALITIES_BUNDLE_BASE_NAME);
				setPdfFieldValue(PdfFieldIndefinite.ENTERPRISE_MUNICIPALITY.getValue(),bundle.getString(address.getMunicipalityCode()));
				setPdfFieldValue(PdfFieldIndefinite.ENTERPRISE_MUNICIPALITY_CODE1.getValue(),address.getMunicipalityCode().substring(0, 1));
				setPdfFieldValue(PdfFieldIndefinite.ENTERPRISE_MUNICIPALITY_CODE2.getValue(),address.getMunicipalityCode().substring(1, 2));
				setPdfFieldValue(PdfFieldIndefinite.ENTERPRISE_MUNICIPALITY_CODE3.getValue(),address.getMunicipalityCode().substring(2, 3));
				setPdfFieldValue(PdfFieldIndefinite.ENTERPRISE_MUNICIPALITY_CODE4.getValue(),address.getMunicipalityCode().substring(3, 4));
				setPdfFieldValue(PdfFieldIndefinite.ENTERPRISE_MUNICIPALITY_CODE5.getValue(),address.getMunicipalityCode().substring(4, 5));
			} catch (StringIndexOutOfBoundsException aie) {
				// do nothing
			} catch (NullPointerException npe) {
				// do nothing
			}
			try {	
				setPdfFieldValue(PdfFieldIndefinite.ENTERPRISE_ZIP1.getValue(),contract.getWorkPlace().getEnterprise().getRegistry().getDefaultAddress().getZip().substring(0, 1));
				setPdfFieldValue(PdfFieldIndefinite.ENTERPRISE_ZIP2.getValue(),contract.getWorkPlace().getEnterprise().getRegistry().getDefaultAddress().getZip().substring(1, 2));
				setPdfFieldValue(PdfFieldIndefinite.ENTERPRISE_ZIP3.getValue(),contract.getWorkPlace().getEnterprise().getRegistry().getDefaultAddress().getZip().substring(2, 3));
				setPdfFieldValue(PdfFieldIndefinite.ENTERPRISE_ZIP4.getValue(),contract.getWorkPlace().getEnterprise().getRegistry().getDefaultAddress().getZip().substring(3, 4));
				setPdfFieldValue(PdfFieldIndefinite.ENTERPRISE_ZIP5.getValue(),contract.getWorkPlace().getEnterprise().getRegistry().getDefaultAddress().getZip().substring(4, 5));
			} catch (StringIndexOutOfBoundsException aie) {
				// do nothing
			} catch (NullPointerException npe) {
				// do nothing
			}
			/* 
			 * Contract ccc fields
			 */
			if(contract.getEnterpriseCCC()!=null){
				setPdfFieldValue(PdfFieldIndefinite.CCC_REG1.getValue(),contract.getEnterpriseCCC().getActivity().getQuoteRegimeCode().substring(0, 1));
				setPdfFieldValue(PdfFieldIndefinite.CCC_REG2.getValue(),contract.getEnterpriseCCC().getActivity().getQuoteRegimeCode().substring(1, 2));
				setPdfFieldValue(PdfFieldIndefinite.CCC_REG3.getValue(),contract.getEnterpriseCCC().getActivity().getQuoteRegimeCode().substring(2, 3));
				setPdfFieldValue(PdfFieldIndefinite.CCC_REG4.getValue(),contract.getEnterpriseCCC().getActivity().getQuoteRegimeCode().substring(3, 4));
				if(contract.getEnterpriseCCC().getCcc().length()==11){
					setPdfFieldValue(PdfFieldIndefinite.CCC_PROV1.getValue(),contract.getEnterpriseCCC().getCcc().substring(0, 1));
					setPdfFieldValue(PdfFieldIndefinite.CCC_PROV2.getValue(),contract.getEnterpriseCCC().getCcc().substring(1, 2));
					setPdfFieldValue(PdfFieldIndefinite.CCC_NISS.getValue(),contract.getEnterpriseCCC().getCcc().substring(2, 9));
					setPdfFieldValue(PdfFieldIndefinite.CCC_CONTROL_DIGIT1.getValue(),contract.getEnterpriseCCC().getCcc().substring(9, 10));
					setPdfFieldValue(PdfFieldIndefinite.CCC_CONTROL_DIGIT2.getValue(),contract.getEnterpriseCCC().getCcc().substring(10, 11));
				} else {
					setPdfFieldValue(PdfFieldIndefinite.CCC_NISS.getValue(),contract.getEnterpriseCCC().getCcc());
				}
				setPdfFieldValue(PdfFieldIndefinite.CCC_ACTIVITY.getValue(),contract.getEnterpriseCCC().getActivity().getDescription());
				setPdfFieldValue(PdfFieldIndefinite.CCC_ACTIVITY_CODE1.getValue(),contract.getEnterpriseCCC().getActivity().getCnae2009().getCode().substring(0, 1));
				setPdfFieldValue(PdfFieldIndefinite.CCC_ACTIVITY_CODE2.getValue(),contract.getEnterpriseCCC().getActivity().getCnae2009().getCode().substring(1, 2));
			}
			/* 
			 * Contract workplace fields
			 */
			try {
				GeoZone country = obtainCountry(contract.getWorkPlace().getAddress().getGeozone());
				setPdfFieldValue(PdfFieldIndefinite.WORKPLACE_COUNTRY.getValue(),country.getName());
				setPdfFieldValue(PdfFieldIndefinite.WORKPLACE_COUNTRY_CODE1.getValue(),String.valueOf(obtainCountryByValue(country).getIsoNum()).substring(0,1));
				setPdfFieldValue(PdfFieldIndefinite.WORKPLACE_COUNTRY_CODE2.getValue(),String.valueOf(obtainCountryByValue(country).getIsoNum()).substring(1,2));
				setPdfFieldValue(PdfFieldIndefinite.WORKPLACE_COUNTRY_CODE3.getValue(),String.valueOf(obtainCountryByValue(country).getIsoNum()).substring(2,3));
			} catch (StringIndexOutOfBoundsException aie) {
				// do nothing
			} catch (NullPointerException npe) {
				// do nothing
			}
			try {
				RegistryAddress address = contract.getWorkPlace().getAddress();
				ResourceBundle bundle = ResourceBundle.getBundle(MUNICIPALITIES_BUNDLE_BASE_NAME);
				setPdfFieldValue(PdfFieldIndefinite.WORKPLACE_MUNICIPALITY.getValue(),bundle.getString(address.getMunicipalityCode()));
				setPdfFieldValue(PdfFieldIndefinite.WORKPLACE_MUNICIPALITY_CODE1.getValue(),address.getMunicipalityCode().substring(0, 1));
				setPdfFieldValue(PdfFieldIndefinite.WORKPLACE_MUNICIPALITY_CODE2.getValue(),address.getMunicipalityCode().substring(1, 2));
				setPdfFieldValue(PdfFieldIndefinite.WORKPLACE_MUNICIPALITY_CODE3.getValue(),address.getMunicipalityCode().substring(2, 3));
				setPdfFieldValue(PdfFieldIndefinite.WORKPLACE_MUNICIPALITY_CODE4.getValue(),address.getMunicipalityCode().substring(3, 4));
				setPdfFieldValue(PdfFieldIndefinite.WORKPLACE_MUNICIPALITY_CODE5.getValue(),address.getMunicipalityCode().substring(4, 5));
			} catch (StringIndexOutOfBoundsException aie) {
				// do nothing
			} catch (NullPointerException npe) {
				// do nothing
			}
			/*
			 * Contract employee fields
			 */
			setPdfFieldValue(PdfFieldIndefinite.EMPLOYEE_NAME.getValue(),contract.getPerson().getFullName());
			setPdfFieldValue(PdfFieldIndefinite.EMPLOYEE_NIF.getValue(),contract.getPerson().getRegistry().getDocument());
			if(contract.getPerson().getBirthDate()!=null){
				SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
				setPdfFieldValue(PdfFieldIndefinite.EMPLOYEE_BIRTH_DATE.getValue(),formatter.format(contract.getPerson().getBirthDate()));
			}
			setPdfFieldValue(PdfFieldIndefinite.EMPLOYEE_NSS.getValue(),contract.getPerson().getSocialSecurityNumber());
			if(contrata!=null && contrata.getNivelFormativo()!=null){
				setPdfFieldValue(PdfFieldIndefinite.EMPLOYEE_FORMATION_LEVEL.getValue(),contrata.getNivelFormativo().getDescription());
				setPdfFieldValue(PdfFieldIndefinite.EMPLOYEE_FORMATION_CODE1.getValue(),contrata.getNivelFormativo().getCode().substring(0, 1));
				setPdfFieldValue(PdfFieldIndefinite.EMPLOYEE_FORMATION_CODE2.getValue(),contrata.getNivelFormativo().getCode().substring(1, 2));
			}
			try {
				setPdfFieldValue(PdfFieldIndefinite.EMPLOYEE_COUNTRY.getValue(),String.valueOf(contract.getPerson().getRegistry().getNationality().getName(getLocale())));
				setPdfFieldValue(PdfFieldIndefinite.EMPLOYEE_COUNTRY_CODE1.getValue(),String.valueOf(contract.getPerson().getRegistry().getNationality().getIsoNum()).substring(0,1));
				setPdfFieldValue(PdfFieldIndefinite.EMPLOYEE_COUNTRY_CODE2.getValue(),String.valueOf(contract.getPerson().getRegistry().getNationality().getIsoNum()).substring(1,2));
				setPdfFieldValue(PdfFieldIndefinite.EMPLOYEE_COUNTRY_CODE3.getValue(),String.valueOf(contract.getPerson().getRegistry().getNationality().getIsoNum()).substring(2,3));
			} catch (StringIndexOutOfBoundsException aie) {
				// do nothing
			} catch (NullPointerException npe) {
				// do nothing
			}
			try {
				RegistryAddress address = contract.getPerson().getRegistry().getDefaultAddress();
				ResourceBundle bundle = ResourceBundle.getBundle(MUNICIPALITIES_BUNDLE_BASE_NAME);
				setPdfFieldValue(PdfFieldIndefinite.EMPLOYEE_ADDRESS_MUNICIPALITY.getValue(),bundle.getString(address.getMunicipalityCode()));
				setPdfFieldValue(PdfFieldIndefinite.EMPLOYEE_ADDRESS_MUNICIPALITY_CODE1.getValue(),address.getMunicipalityCode().substring(0, 1));
				setPdfFieldValue(PdfFieldIndefinite.EMPLOYEE_ADDRESS_MUNICIPALITY_CODE2.getValue(),address.getMunicipalityCode().substring(1, 2));
				setPdfFieldValue(PdfFieldIndefinite.EMPLOYEE_ADDRESS_MUNICIPALITY_CODE3.getValue(),address.getMunicipalityCode().substring(2, 3));
				setPdfFieldValue(PdfFieldIndefinite.EMPLOYEE_ADDRESS_MUNICIPALITY_CODE4.getValue(),address.getMunicipalityCode().substring(3, 4));
				setPdfFieldValue(PdfFieldIndefinite.EMPLOYEE_ADDRESS_MUNICIPALITY_CODE5.getValue(),address.getMunicipalityCode().substring(4, 5));
			} catch (StringIndexOutOfBoundsException aie) {
				// do nothing
			} catch (NullPointerException npe) {
				// do nothing
			}
			try {
				GeoZone country = obtainCountry(contract.getPerson().getRegistry().getDefaultAddress().getGeozone());
				setPdfFieldValue(PdfFieldIndefinite.EMPLOYEE_ADDRESS_COUNTRY.getValue(),country.getName());
				setPdfFieldValue(PdfFieldIndefinite.EMPLOYEE_ADDRESS_COUNTRY_CODE1.getValue(),String.valueOf(obtainCountryByValue(country).getIsoNum()).substring(0,1));
				setPdfFieldValue(PdfFieldIndefinite.EMPLOYEE_ADDRESS_COUNTRY_CODE2.getValue(),String.valueOf(obtainCountryByValue(country).getIsoNum()).substring(1,2));
				setPdfFieldValue(PdfFieldIndefinite.EMPLOYEE_ADDRESS_COUNTRY_CODE3.getValue(),String.valueOf(obtainCountryByValue(country).getIsoNum()).substring(2,3));
			} catch (StringIndexOutOfBoundsException aie) {
				// do nothing
			} catch (NullPointerException npe) {
				// do nothing
			}
			try {
				setPdfFieldValue(PdfFieldIndefinite.LEGAL_REPRESENTATIVE_NAME.getValue(),"");
				setPdfFieldValue(PdfFieldIndefinite.LEGAL_REPRESENTATIVE_NIF.getValue(),"");
				setPdfFieldValue(PdfFieldIndefinite.LEGAL_REPRESENTATIVE_CHARGE.getValue(),"");
			} catch (StringIndexOutOfBoundsException aie) {
				// do nothing
			} catch (NullPointerException npe) {
				// do nothing
			}
			
			/*
			 * Contract page 1
			 */
			if(contrata!=null){
				setPdfFieldValue(PdfFieldIndefinite.PROFESSION.getValue(), contrata.getCno().getTitle());
			}
			setPdfFieldValue(PdfFieldIndefinite.CATEGORY.getValue(), contract.getCategoryDescription());
			setPdfFieldValue(PdfFieldIndefinite.WORKPLACE_FULL_ADDRESS.getValue(), null);
			setPdfFieldValue(PdfFieldIndefinite.WORKPLACE_FULL_ADDRESS_MORE.getValue(), contract.getWorkPlace().getAddress().getFullAddress()+", "+contract.getWorkPlace().getAddress().getLocation());
			
			if(StringUtils.isNotBlank(getContractInfoMap(contract).get(PdfFieldIndefinite.EMPLOYEE_CONTRACT_DISTANCE.toString()))){
				setPdfFieldValue(PdfFieldIndefinite.EMPLOYEE_CONTRACT_DISTANCE.getValue(), getContractInfoMap(contract).get(PdfFieldIndefinite.EMPLOYEE_CONTRACT_DISTANCE.toString()));
			}
			if(StringUtils.isNotBlank(getContractInfoMap(contract).get(PdfFieldIndefinite.EMPLOYEE_CONTRACT_DIST_ADDR.toString()))){
				setPdfFieldValue(PdfFieldIndefinite.EMPLOYEE_CONTRACT_DIST_ADDR.getValue(), getContractInfoMap(contract).get(PdfFieldIndefinite.EMPLOYEE_CONTRACT_DIST_ADDR.toString()));
			}
			
			// setPdfFieldValue(PdfFieldIndefinite.DISCONTONUOUS_WORK_DESCRIPTION.getValue(), null);
			// setPdfFieldValue(PdfFieldIndefinite.DISCONTONUOUS_WORK_ACTIVITY.getValue(), null);
			// setPdfFieldValue(PdfFieldIndefinite.DISCONTONUOUS_WORK_DURATION.getValue(), null);
			// setPdfFieldValue(PdfFieldIndefinite.DISCONTONUOUS_WORK_ESTIMATED_DURATION.getValue(), null);
			// setPdfFieldValue(PdfFieldIndefinite.DISCONTONUOUS_WORK_AGREEMENT_COLLECTIVE.getValue(), null);
			// setPdfFieldValue(PdfFieldIndefinite.DISCONTONUOUS_WORK_ESTIMATED_JOURNAL_HOURS.getValue(), null);
			// setPdfFieldValue(PdfFieldIndefinite.DISCONTONUOUS_WORK_ESTIMATED_JOURNAL_PERIOD.getValue(), null);
			// setPdfFieldValue(PdfFieldIndefinite.DISCONTONUOUS_WORK_ESTIMATED_SCHEDULE.getValue(), null);
			// setPdfFieldValue(PdfFieldIndefinite.DISCONTINUOUS_AGREEMENT_COLLECTIVE_YES.getValue(), null);
			// setPdfFieldValue(PdfFieldIndefinite.DISCONTINUOUS_AGREEMENT_COLLECTIVE_NO.getValue(), null);
			// setPdfFieldValue(PdfFieldIndefinite.**UNKNOWN**.getValue(), null);
			
			if(code.getValue().startsWith("1") || code.getValue().startsWith("4")){
				setPdfFieldValue(PdfFieldIndefinite.FULL_TIME.getValue(), "true");
				if(StringUtils.isNotBlank(getContractInfoMap(contract).get(PdfFieldIndefinite.FULL_TIME_WEEK_HOURS.toString()))){
					setPdfFieldValue(PdfFieldIndefinite.FULL_TIME_WEEK_HOURS.getValue(), getContractInfoMap(contract).get(PdfFieldIndefinite.FULL_TIME_WEEK_HOURS.toString()));
				}
				if(StringUtils.isNotBlank(getContractInfoMap(contract).get(PdfFieldIndefinite.FULL_TIME_START_TIME.toString()))){
					setPdfFieldValue(PdfFieldIndefinite.FULL_TIME_START_TIME.getValue(), getContractInfoMap(contract).get(PdfFieldIndefinite.FULL_TIME_START_TIME.toString()));
				}
				if(StringUtils.isNotBlank(getContractInfoMap(contract).get(PdfFieldIndefinite.FULL_TIME_END_TIME.toString()))){
					setPdfFieldValue(PdfFieldIndefinite.FULL_TIME_END_TIME.getValue(), getContractInfoMap(contract).get(PdfFieldIndefinite.FULL_TIME_END_TIME.toString()));
				}
			} else if(code.getValue().startsWith("2") || code.getValue().startsWith("5")){
				setPdfFieldValue(PdfFieldIndefinite.PARTIALLY_TIME.getValue(), "true");
				
				
				if(StringUtils.isNotBlank(getContractDataMap(contract).get(ContextVariable.WEEK_HOURS.toString()))){
					setPdfFieldValue(PdfFieldIndefinite.PARTIALLY_TIME_HOURS.getValue(), getContractDataMap(contract).get(ContextVariable.WEEK_HOURS.toString()));
					setPdfFieldValue(PdfFieldIndefinite.PARTIALLY_TIME_WEEKLY.getValue(), "true");
				} else if(contrata!=null){
					if(contrata.getHorasJornada()!=null){
						setPdfFieldValue(PdfFieldIndefinite.PARTIALLY_TIME_HOURS.getValue(), String.valueOf(Integer.parseInt(contrata.getHorasJornada())));
					}
					if(contrata.getTipoJornada()==TEQPTIEM.TEQPTIEM_D){
						setPdfFieldValue(PdfFieldIndefinite.PARTIALLY_TIME_DAYLY.getValue(), "true");
					} else if(contrata.getTipoJornada()==TEQPTIEM.TEQPTIEM_S){
						setPdfFieldValue(PdfFieldIndefinite.PARTIALLY_TIME_WEEKLY.getValue(), "true");
					} else if(contrata.getTipoJornada()==TEQPTIEM.TEQPTIEM_M){
						setPdfFieldValue(PdfFieldIndefinite.PARTIALLY_TIME_MONTHLY.getValue(), "true");
					} else if(contrata.getTipoJornada()==TEQPTIEM.TEQPTIEM_A){
						setPdfFieldValue(PdfFieldIndefinite.PARTIALLY_TIME_YEARLY.getValue(), "true");
					}
				}
				if(StringUtils.isNotBlank(getContractInfoMap(contract).get(PdfFieldIndefinite.DEFAULT_JOURNAL_HOURS.toString()))){
					setPdfFieldValue(PdfFieldIndefinite.DEFAULT_JOURNAL_HOURS.getValue(), getContractInfoMap(contract).get(PdfFieldIndefinite.DEFAULT_JOURNAL_HOURS.toString()));
				}
				
				String key = getContractInfoMap(contract).get(PdfFieldIndefinite.COMPLEMENTARY_HOURS.toString());
				if(StringUtils.isNotBlank(key)){
					if(PdfFieldIndefinite.COMPLEMENTARY_HOURS_YES.toString().equals(key)){
						setPdfFieldValue(PdfFieldIndefinite.COMPLEMENTARY_HOURS_YES.getValue(), "true");
					} else if(PdfFieldIndefinite.COMPLEMENTARY_HOURS_NO.toString().equals(key)){
						setPdfFieldValue(PdfFieldIndefinite.COMPLEMENTARY_HOURS_NO.getValue(), "true");
					}
				}
				
			}
			
			/*
			 * Contract page 2
			 */
			dateFormatter.applyPattern("dd/MM/yyyy");
			setPdfFieldValue(PdfFieldIndefinite.START_DATE.getValue(), dateFormatter.format(contract.getStartDate()));
			
			if(StringUtils.isNotBlank(getContractInfoMap(contract).get(PdfFieldIndefinite.TRIAL_DURATION.toString()))){
				setPdfFieldValue(PdfFieldIndefinite.TRIAL_DURATION.getValue(), getContractInfoMap(contract).get(PdfFieldIndefinite.TRIAL_DURATION.toString()));
			}
			
			if(contrata!=null && contrata.isReliefData()){
				setPdfFieldValue(PdfFieldIndefinite.RELIEF_CONTRACT_YES.getValue(), "true");
			} else {
				setPdfFieldValue(PdfFieldIndefinite.RELIEF_CONTRACT_NO.getValue(), "true");
			}
			// setPdfFieldValue(PdfFieldIndefinite.RELIEF_CONTRACT_UNEMPLOYED_IN_SEPE_MUNICIPALITY1.getValue(), "");
			// setPdfFieldValue(PdfFieldIndefinite.RELIEF_CONTRACT_UNEMPLOYED_IN_SEPE_MUNICIPALITY2.getValue(), "");
			// setPdfFieldValue(PdfFieldIndefinite.RELIEF_CONTRACT_PARTIALLY_CONTRACT_SEPE_MUNICIPALITY.getValue(), "");
			// setPdfFieldValue(PdfFieldIndefinite.RELIEF_CONTRACT_PARTIALLY_CONTRACT_NUMBER.getValue(), "");
			// setPdfFieldValue(PdfFieldIndefinite.RELIEF_CONTRACT_PARTIALLY_CONTRACT_DATE.getValue(), "");
			
			if(StringUtils.isNotBlank(getContractInfoMap(contract).get(PdfFieldIndefinite.SALARY_AMOUNT.toString()))){
				setPdfFieldValue(PdfFieldIndefinite.SALARY_AMOUNT.getValue(), getContractInfoMap(contract).get(PdfFieldIndefinite.SALARY_AMOUNT.toString()));
			}
			if(StringUtils.isNotBlank(getContractInfoMap(contract).get(PdfFieldIndefinite.SALARY_PERIOD.toString()))){
				setPdfFieldValue(PdfFieldIndefinite.SALARY_PERIOD.getValue(), getContractInfoMap(contract).get(PdfFieldIndefinite.SALARY_PERIOD.toString()));
			}
			if(StringUtils.isNotBlank(getContractInfoMap(contract).get(PdfFieldIndefinite.SALARY_CONCEPT.toString()))){
				setPdfFieldValue(PdfFieldIndefinite.SALARY_CONCEPT.getValue(), getContractInfoMap(contract).get(PdfFieldIndefinite.SALARY_CONCEPT.toString()));
			}
			if(StringUtils.isNotBlank(getContractInfoMap(contract).get(PdfFieldIndefinite.HOLIDAYS.toString()))){
				setPdfFieldValue(PdfFieldIndefinite.HOLIDAYS.getValue(), getContractInfoMap(contract).get(PdfFieldIndefinite.HOLIDAYS.toString()));
			}
			if(contract.getAgreementLevelCategory()!=null && contract.getAgreementLevelCategory().getId()!=null){
				setPdfFieldValue(PdfFieldIndefinite.AGREEMENT_COLLECTIVE.getValue(), contract.getAgreementLevelCategory().getLevel().getAgreement().getDescription());
			}
			if(StringUtils.isNotBlank(getContractInfoMap(contract).get(PdfFieldIndefinite.SEPE_MUNICIPALITY.toString()))){
				setPdfFieldValue(PdfFieldIndefinite.SEPE_MUNICIPALITY.getValue(), getContractInfoMap(contract).get(PdfFieldIndefinite.SEPE_MUNICIPALITY.toString()));
			}
			
			
			/*
			 *  OPTIONS PAGE
			 */
			modelOption = ModelOption.valueOf(getContractInfoMap(contract).get(ContractVariable.CONTRACT_MODEL_OPTION.getValue()));
			
			if(modelOption == ModelOption.INDEFINITE_OPT1){
				setPdfFieldValue(PdfFieldIndefinite.MAIN_OPT1_CHECK.getValue(),"true");
				setPdfFieldValue(PdfFieldIndefinite.OPT1_OPTION_CHECK.getValue(),"true");
				if(code == ContractCode.C100){
					setPdfFieldValue(PdfFieldIndefinite.OPT1_TC2_100.getValue(),"true");
				} else if(code == ContractCode.C200){
					setPdfFieldValue(PdfFieldIndefinite.OPT1_TC2_200.getValue(),"true");
				} else if(code == ContractCode.C300){
					setPdfFieldValue(PdfFieldIndefinite.OPT1_TC2_300.getValue(),"true");
				}
			} else if(modelOption == ModelOption.INDEFINITE_OPT5){
				setPdfFieldValue(PdfFieldIndefinite.MAIN_OPT5_CHECK.getValue(),"true");
				setPdfFieldValue(PdfFieldIndefinite.OPT5_OPTION_CHECK.getValue(),"true");
				if(code == ContractCode.C100){
					setPdfFieldValue(PdfFieldIndefinite.OPT5_FULL_TIME.getValue(),"true");
					setPdfFieldValue(PdfFieldIndefinite.OPT5_TC2_100.getValue(),"true");
				} else if(code == ContractCode.C150){
					setPdfFieldValue(PdfFieldIndefinite.OPT5_FULL_TIME.getValue(),"true");
					setPdfFieldValue(PdfFieldIndefinite.OPT5_TC2_150.getValue(),"true");
				} else if(code == ContractCode.C200){
					setPdfFieldValue(PdfFieldIndefinite.OPT5_PARTIALLY_TIME.getValue(),"true");
					setPdfFieldValue(PdfFieldIndefinite.OPT5_TC2_200.getValue(),"true");
				} else if(code == ContractCode.C250){
					setPdfFieldValue(PdfFieldIndefinite.OPT5_PARTIALLY_TIME.getValue(),"true");
					setPdfFieldValue(PdfFieldIndefinite.OPT5_TC2_250.getValue(),"true");
				} else if(code == ContractCode.C300){
					setPdfFieldValue(PdfFieldIndefinite.OPT5_DISCONTINUOUS_TIME.getValue(),"true");
					setPdfFieldValue(PdfFieldIndefinite.OPT5_TC2_300.getValue(),"true");
				} else if(code == ContractCode.C350){
					setPdfFieldValue(PdfFieldIndefinite.OPT5_DISCONTINUOUS_TIME.getValue(),"true");
					setPdfFieldValue(PdfFieldIndefinite.OPT5_TC2_350.getValue(),"true");
				}
				
				String key = getContractInfoMap(contract).get(PdfFieldIndefinite.OPT5_BONUS_ART4_RDL3_2012.toString());
				if(StringUtils.isNotBlank(key)){
					if(PdfFieldIndefinite.OPT5_BONUS_ART4_RDL3_2012_YES.toString().equals(key)){
						setPdfFieldValue(PdfFieldIndefinite.OPT5_BONUS_ART4_RDL3_2012_YES.getValue(), "true");
					} else if(PdfFieldIndefinite.OPT5_BONUS_ART4_RDL3_2012_NO.toString().equals(key)){
						setPdfFieldValue(PdfFieldIndefinite.OPT5_BONUS_ART4_RDL3_2012_NO.getValue(), "true");
					}
				}
				
				if(StringUtils.isNotBlank(getContractInfoMap(contract).get(PdfFieldIndefinite.OPT5_UNEMPLOYED_BT_16_30.toString()))
						|| StringUtils.isNotBlank(getContractInfoMap(contract).get(PdfFieldIndefinite.OPT5_UNEMPLOYED_GT_45.toString())) ){
					setPdfFieldValue(PdfFieldIndefinite.OPT5_REGISTERED_IN_EMPLOYMENT_OFFICE.getValue(),"true");

					key = getContractInfoMap(contract).get(PdfFieldIndefinite.OPT5_UNEMPLOYED_BT_16_30.toString());
					if(StringUtils.isNotBlank(key)){
						setPdfFieldValue(PdfFieldIndefinite.OPT5_UNEMPLOYED_BT_16_30_CHECK.getValue(),"true");
						if(PdfFieldIndefinite.OPT5_UNEMPLOYED_BT_16_30_JUNIOR.toString().equals(key)){
							setPdfFieldValue(PdfFieldIndefinite.OPT5_UNEMPLOYED_BT_16_30_JUNIOR.getValue(), "true");
						} else if(PdfFieldIndefinite.OPT5_UNEMPLOYED_BT_16_30_FEMALE.toString().equals(key)){
							setPdfFieldValue(PdfFieldIndefinite.OPT5_UNEMPLOYED_BT_16_30_FEMALE.getValue(), "true");
						}
					}

					key = getContractInfoMap(contract).get(PdfFieldIndefinite.OPT5_UNEMPLOYED_GT_45.toString());
					if(StringUtils.isNotBlank(key)){
						setPdfFieldValue(PdfFieldIndefinite.OPT5_UNEMPLOYED_GT_45_CHECK.getValue(),"true");
						if(PdfFieldIndefinite.OPT5_UNEMPLOYED_GT_45_MALE.toString().equals(key)){
							setPdfFieldValue(PdfFieldIndefinite.OPT5_UNEMPLOYED_GT_45_MALE.getValue(), "true");
						} else if(PdfFieldIndefinite.OPT5_UNEMPLOYED_GT_45_FEMALE.toString().equals(key)){
							setPdfFieldValue(PdfFieldIndefinite.OPT5_UNEMPLOYED_GT_45_FEMALE.getValue(), "true");
						}
					}
				}
				
				if(StringUtils.isNotBlank(getContractInfoMap(contract).get(PdfFieldIndefinite.OPT5_UNEMPL_3_MONTH_BENEFIT.toString()))){
					setPdfFieldValue(PdfFieldIndefinite.OPT5_UNEMPL_3_MONTH_BENEFIT.getValue(),"true");
				}
				if(StringUtils.isNotBlank(getContractInfoMap(contract).get(PdfFieldIndefinite.OPT5_FIRST_EMPLOYEE_AND_LT_30.toString()))){
					setPdfFieldValue(PdfFieldIndefinite.OPT5_FIRST_EMPLOYEE_AND_LT_30.getValue(),"true");
				}
			} else if(modelOption == ModelOption.INDEFINITE_OPT6){
				setPdfFieldValue(PdfFieldIndefinite.MAIN_OPT6_CHECK.getValue(),"true");
				setPdfFieldValue(PdfFieldIndefinite.OPT6_OPTION_CHECK.getValue(),"true");
				if(code == ContractCode.C100){
					setPdfFieldValue(PdfFieldIndefinite.OPT6_TC2_100.getValue(),"true");
				} else if(code == ContractCode.C200){
					setPdfFieldValue(PdfFieldIndefinite.OPT6_TC2_200.getValue(),"true");
				}
				// TODO: complete this
				setPdfFieldValue(PdfFieldIndefinite.OPT6_LT_30_EMPLOYEE.getValue(),"");
				setPdfFieldValue(PdfFieldIndefinite.OPT6_LT_35_EMPLOYEE_AND_HANDICAP_GTE_33.getValue(),"");
				setPdfFieldValue(PdfFieldIndefinite.OPT6_AGREEMENT_COLLECTIVE1.getValue(),"");
				setPdfFieldValue(PdfFieldIndefinite.OPT6_AGREEMENT_COLLECTIVE2.getValue(),"");
			} else if(modelOption == ModelOption.INDEFINITE_OPT7){
				setPdfFieldValue(PdfFieldIndefinite.MAIN_OPT7_CHECK.getValue(),"true");
				setPdfFieldValue(PdfFieldIndefinite.OPT7_OPTION_CHECK.getValue(),"true");
				if(code == ContractCode.C100){
					setPdfFieldValue(PdfFieldIndefinite.OPT7_TC2_100.getValue(),"true");
				} else if(code == ContractCode.C200){
					setPdfFieldValue(PdfFieldIndefinite.OPT7_TC2_200.getValue(),"true");
				} else if(code == ContractCode.C300){
					setPdfFieldValue(PdfFieldIndefinite.OPT7_TC2_300.getValue(),"true");
				}
				// TODO: complete this
				setPdfFieldValue(PdfFieldIndefinite.OPT7_UNEMPLOYED_DURING_12_MONTH.getValue(),"");
				setPdfFieldValue(PdfFieldIndefinite.OPT7_PROFFESIONAL_RECUALIFICATION.getValue(),"");
				setPdfFieldValue(PdfFieldIndefinite.OPT7_AGREEMENT_COLLECTIVE1.getValue(),"");
				setPdfFieldValue(PdfFieldIndefinite.OPT7_AGREEMENT_COLLECTIVE2.getValue(),"");
			} else if(modelOption == ModelOption.INDEFINITE_OPT15){
				setPdfFieldValue(PdfFieldIndefinite.MAIN_OPT15_CHECK.getValue(),"true");
				setPdfFieldValue(PdfFieldIndefinite.OPT15_OPTION_CHECK.getValue(),"true");
				if(code == ContractCode.C100){
					setPdfFieldValue(PdfFieldIndefinite.OPT15_TC2_100.getValue(),"true");
				} else if(code == ContractCode.C200){
					setPdfFieldValue(PdfFieldIndefinite.OPT15_TC2_200.getValue(),"true");
				}
				// TODO: complete this
				setPdfFieldValue(PdfFieldIndefinite.OPT15_ONSITE_HOURS_YES.getValue(),"");
				setPdfFieldValue(PdfFieldIndefinite.OPT15_ONSITE_HOURS_NO.getValue(),"");
				setPdfFieldValue(PdfFieldIndefinite.OPT15_ONSITE_WEEK_HOURS.getValue(),"");
				setPdfFieldValue(PdfFieldIndefinite.OPT15_ONSITE_HOURS_DISTRIBUTION.getValue(),"");
				setPdfFieldValue(PdfFieldIndefinite.OPT15_SALARY_OPT1.getValue(),"");
				setPdfFieldValue(PdfFieldIndefinite.OPT15_SALARY_OPT2.getValue(),"");
				setPdfFieldValue(PdfFieldIndefinite.OPT15_SALARY_OPT3.getValue(),"");
				setPdfFieldValue(PdfFieldIndefinite.OPT15_OVERNIGHT_YES.getValue(),"");
				setPdfFieldValue(PdfFieldIndefinite.OPT15_OVERNIGHT_NO.getValue(),"");
				setPdfFieldValue(PdfFieldIndefinite.OPT15_OVERNIGHT_WEEK_DAYS.getValue(),"");
			} else if(modelOption == ModelOption.INDEFINITE_OPT17){
				setPdfFieldValue(PdfFieldIndefinite.MAIN_OPT17_CHECK.getValue(),"true");
				setPdfFieldValue(PdfFieldIndefinite.OPT17_OPTION_CHECK.getValue(),"true");
				String subsidized = getContractDataMap(contract).get(ContextVariable.SUBSIDIZED.getName());
				if(code == ContractCode.C109 || code == ContractCode.C139 | code == ContractCode.C189){
					if(code == ContractCode.C139){
						setPdfFieldValue(PdfFieldIndefinite.OPT17_FULL_TIME.getValue(),"true");
						setPdfFieldValue(PdfFieldIndefinite.OPT17_TC2_139.getValue(),"true");
					} else if(code == ContractCode.C109){
						setPdfFieldValue(PdfFieldIndefinite.OPT17_FULL_TIME.getValue(),"true");
						setPdfFieldValue(PdfFieldIndefinite.OPT17_TC2_109.getValue(),"true");
					} else if(code == ContractCode.C189){
						setPdfFieldValue(PdfFieldIndefinite.OPT17_FULL_TIME.getValue(),"true");
						setPdfFieldValue(PdfFieldIndefinite.OPT17_TC2_189.getValue(),"true");
					} 
					if( Boolean.parseBoolean(subsidized) ){
						setPdfFieldValue(PdfFieldIndefinite.OPT17_FULL_TIME_QUOTE_BONUS_YES.getValue(),"true");
					} else {
						setPdfFieldValue(PdfFieldIndefinite.OPT17_FULL_TIME_QUOTE_BONUS_NO.getValue(),"true");
					}
				} else if(code == ContractCode.C209 || code == ContractCode.C239 || code == ContractCode.C289){
					if(code == ContractCode.C239){
						setPdfFieldValue(PdfFieldIndefinite.OPT17_PARTIALLY_TIME.getValue(),"true");
						setPdfFieldValue(PdfFieldIndefinite.OPT17_TC2_239.getValue(),"true");
					} else if(code == ContractCode.C209){
						setPdfFieldValue(PdfFieldIndefinite.OPT17_PARTIALLY_TIME.getValue(),"true");
						setPdfFieldValue(PdfFieldIndefinite.OPT17_TC2_209.getValue(),"true");
					} else if(code == ContractCode.C289){
						setPdfFieldValue(PdfFieldIndefinite.OPT17_PARTIALLY_TIME.getValue(),"true");
						setPdfFieldValue(PdfFieldIndefinite.OPT17_TC2_289.getValue(),"true");
					} 
					if( Boolean.parseBoolean(subsidized) ){
						setPdfFieldValue(PdfFieldIndefinite.OPT17_PARTIALLY_TIME_QUOTE_BONUS_YES.getValue(),"true");
					} else {
						setPdfFieldValue(PdfFieldIndefinite.OPT17_PARTIALLY_TIME_QUOTE_BONUS_NO.getValue(),"true");
					}
				} else if(code == ContractCode.C309 || code == ContractCode.C339 || code == ContractCode.C389){
					if(code == ContractCode.C339){
						setPdfFieldValue(PdfFieldIndefinite.OPT17_DISCONTINUOUS_TIME.getValue(),"true");
						setPdfFieldValue(PdfFieldIndefinite.OPT17_TC2_339.getValue(),"true");
					} else if(code == ContractCode.C309){
						setPdfFieldValue(PdfFieldIndefinite.OPT17_DISCONTINUOUS_TIME.getValue(),"true");
						setPdfFieldValue(PdfFieldIndefinite.OPT17_TC2_309.getValue(),"true");
					} else if(code == ContractCode.C389){
						setPdfFieldValue(PdfFieldIndefinite.OPT17_DISCONTINUOUS_TIME.getValue(),"true");
						setPdfFieldValue(PdfFieldIndefinite.OPT17_TC2_389.getValue(),"true");
					}
					if( Boolean.parseBoolean(subsidized) ){
						setPdfFieldValue(PdfFieldIndefinite.OPT17_DISCONTINUOUS_TIME_QUOTE_BONUS_YES.getValue(),"true");
					} else {
						setPdfFieldValue(PdfFieldIndefinite.OPT17_DISCONTINUOUS_TIME_QUOTE_BONUS_NO.getValue(),"true");
					}
				}
				
				
				if(StringUtils.isNotBlank(getContractInfoMap(contract).get(PdfFieldIndefinite.SEPE_MUNICIPALITY.toString()))){
					setPdfFieldValue(PdfFieldIndefinite.OPT17_SEPE_MUNICIPALITY.getValue(), getContractInfoMap(contract).get(PdfFieldIndefinite.SEPE_MUNICIPALITY.toString()));
				}				
				// TODO: complete this
				setPdfFieldValue(PdfFieldIndefinite.OPT17_TRANSFORM_DATE.getValue(),"");
				
				
//				String key = getContractInfoMap(contract).get(PdfFieldIndefinite.OPT17_TRANSFORMATION_TO.toString());
//				if(StringUtils.isNotBlank(key)){
//					if(PdfFieldIndefinite.OPT17_IS_FULL_TIME.toString().equals(key)){
//						setPdfFieldValue(PdfFieldIndefinite.OPT17_IS_FULL_TIME.getValue(), "true");
//					} else if(PdfFieldIndefinite.OPT17_IS_FULL_TIME_DISCONTINUOUS.toString().equals(key)){
//						setPdfFieldValue(PdfFieldIndefinite.OPT17_IS_FULL_TIME_DISCONTINUOUS.getValue(), "true");
//					}
//				}
				if(code == ContractCode.C309 || code == ContractCode.C339 || code == ContractCode.C389){
					setPdfFieldValue(PdfFieldIndefinite.OPT17_IS_FULL_TIME.getValue(), "true");
				} else {
					setPdfFieldValue(PdfFieldIndefinite.OPT17_IS_FULL_TIME_DISCONTINUOUS.getValue(), "true");
				}
				
				String contractCode = getContractDataMap(contract, null, null, false).get(ContextVariable.TC2.getName());
				if(StringUtils.isNotBlank(contractCode)){
					setPdfFieldValue(PdfFieldIndefinite.OPT17_SRC_CONTRACT.getValue(),ContractCode.getContractCodeByValue(contractCode).getName(getLocale()));
				}
				
				dateFormatter.applyPattern("dd/MM/yyyy");
				setPdfFieldValue(PdfFieldIndefinite.OPT17_SRC_CONTRACT_START_DATE.getValue(), dateFormatter.format(contract.getStartDate()));
				
				if(StringUtils.isNotBlank(getContractInfoMap(contract).get(PdfFieldIndefinite.OPT17_SRC_CONTRACT_SEPE_MUNIC.toString()))){
					setPdfFieldValue(PdfFieldIndefinite.OPT17_SRC_CONTRACT_SEPE_MUNIC.getValue(), getContractInfoMap(contract).get(PdfFieldIndefinite.OPT17_SRC_CONTRACT_SEPE_MUNIC.toString()));
				}
				
				dateFormatter.applyPattern("dd/MM/yyyy");
				setPdfFieldValue(PdfFieldIndefinite.OPT17_SRC_CONTRACT_SEPE_DATE.getValue(), dateFormatter.format(contract.getStartDate()));
				
				if(StringUtils.isNotBlank(getContractInfoMap(contract).get(ContractVariable.SEPE_CONTRACT_ID.getValue()))){
					setPdfFieldValue(PdfFieldIndefinite.OPT17_SRC_CONTRACT_SEPE_ID.getValue(),getContractInfoMap(contract).get(ContractVariable.SEPE_CONTRACT_ID.getValue()));
				}
			}
			
		} catch (IOException e) {
			// do nothing
		} catch (ManagerBeanException e) {
			// do nothing
		}
	}
	
}
	
	