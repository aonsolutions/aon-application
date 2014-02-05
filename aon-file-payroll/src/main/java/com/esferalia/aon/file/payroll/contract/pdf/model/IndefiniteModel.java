package com.esferalia.aon.file.payroll.contract.pdf.model;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ResourceBundle;

import org.apache.commons.lang.StringUtils;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.geozone.GeoZone;
import com.code.aon.registry.RegistryAddress;
import com.code.aon.registry.RegistryDirStaff;
import com.esferalia.aon.file.payroll.contract.pdf.IndefiniteCommonField;
import com.esferalia.aon.file.payroll.contract.pdf.IndefiniteOptionField;
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
			setPdfFieldValue(IndefiniteCommonField.ENTERPRISE_CIF.getValue(),contract.getWorkPlace().getEnterprise().getRegistry().getDocument());
			RegistryDirStaff rDirStaff = obtainRegistryDirStaff(contract.getWorkPlace().getEnterprise().getRegistry()); 
			try {
				setPdfFieldValue(IndefiniteCommonField.ENTERPRISE_DIR_STAFF_NAME.getValue(),rDirStaff.getName());
				setPdfFieldValue(IndefiniteCommonField.ENTERPRISE_DIR_STAFF_NIF.getValue(),rDirStaff.getDocument());
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
				setPdfFieldValue(IndefiniteCommonField.ENTERPRISE_DIR_STAFF_CHARGE.getValue(),rDirStaddCharge);
			} catch (NullPointerException npe) {
				// do nothing
			}
			setPdfFieldValue(IndefiniteCommonField.ENTERPRISE_NAME.getValue(),contract.getWorkPlace().getEnterprise().getRegistry().getFullName());
			setPdfFieldValue(IndefiniteCommonField.ENTERPRISE_ADDRESS.getValue(),contract.getWorkPlace().getEnterprise().getRegistry().getDefaultAddress().getFullAddress());
			try {	
				setPdfFieldValue(IndefiniteCommonField.ENTERPRISE_COUNTRY.getValue(),contract.getWorkPlace().getEnterprise().getRegistry().getNationality().getName(getLocale()));
				setPdfFieldValue(IndefiniteCommonField.ENTERPRISE_COUNTRY_CODE1.getValue(),String.valueOf(contract.getWorkPlace().getEnterprise().getRegistry().getNationality().getIsoNum()).substring(0,1));
				setPdfFieldValue(IndefiniteCommonField.ENTERPRISE_COUNTRY_CODE2.getValue(),String.valueOf(contract.getWorkPlace().getEnterprise().getRegistry().getNationality().getIsoNum()).substring(1,2));
				setPdfFieldValue(IndefiniteCommonField.ENTERPRISE_COUNTRY_CODE3.getValue(),String.valueOf(contract.getWorkPlace().getEnterprise().getRegistry().getNationality().getIsoNum()).substring(2,3));
			} catch (StringIndexOutOfBoundsException aie) {
				// do nothing
			} catch (NullPointerException npe) {
				// do nothing
			}
			try {	
				RegistryAddress address = contract.getWorkPlace().getEnterprise().getRegistry().getDefaultAddress();
				ResourceBundle bundle = ResourceBundle.getBundle("com.esferalia.aon.payroll.i18n.municipalities");
				setPdfFieldValue(IndefiniteCommonField.ENTERPRISE_MUNICIPALITY.getValue(),bundle.getString(address.getMunicipalityCode()));
				setPdfFieldValue(IndefiniteCommonField.ENTERPRISE_MUNICIPALITY_CODE1.getValue(),address.getMunicipalityCode().substring(0, 1));
				setPdfFieldValue(IndefiniteCommonField.ENTERPRISE_MUNICIPALITY_CODE2.getValue(),address.getMunicipalityCode().substring(1, 2));
				setPdfFieldValue(IndefiniteCommonField.ENTERPRISE_MUNICIPALITY_CODE3.getValue(),address.getMunicipalityCode().substring(2, 3));
				setPdfFieldValue(IndefiniteCommonField.ENTERPRISE_MUNICIPALITY_CODE4.getValue(),address.getMunicipalityCode().substring(3, 4));
				setPdfFieldValue(IndefiniteCommonField.ENTERPRISE_MUNICIPALITY_CODE5.getValue(),address.getMunicipalityCode().substring(4, 5));
			} catch (StringIndexOutOfBoundsException aie) {
				// do nothing
			} catch (NullPointerException npe) {
				// do nothing
			}
			try {	
				setPdfFieldValue(IndefiniteCommonField.ENTERPRISE_ZIP1.getValue(),contract.getWorkPlace().getEnterprise().getRegistry().getDefaultAddress().getZip().substring(0, 1));
				setPdfFieldValue(IndefiniteCommonField.ENTERPRISE_ZIP2.getValue(),contract.getWorkPlace().getEnterprise().getRegistry().getDefaultAddress().getZip().substring(1, 2));
				setPdfFieldValue(IndefiniteCommonField.ENTERPRISE_ZIP3.getValue(),contract.getWorkPlace().getEnterprise().getRegistry().getDefaultAddress().getZip().substring(2, 3));
				setPdfFieldValue(IndefiniteCommonField.ENTERPRISE_ZIP4.getValue(),contract.getWorkPlace().getEnterprise().getRegistry().getDefaultAddress().getZip().substring(3, 4));
				setPdfFieldValue(IndefiniteCommonField.ENTERPRISE_ZIP5.getValue(),contract.getWorkPlace().getEnterprise().getRegistry().getDefaultAddress().getZip().substring(4, 5));
			} catch (StringIndexOutOfBoundsException aie) {
				// do nothing
			} catch (NullPointerException npe) {
				// do nothing
			}
			/* 
			 * Contract ccc fields
			 */
			if(contract.getEnterpriseCCC()!=null){
				setPdfFieldValue(IndefiniteCommonField.CCC_REG1.getValue(),contract.getEnterpriseCCC().getActivity().getQuoteRegimeCode().substring(0, 1));
				setPdfFieldValue(IndefiniteCommonField.CCC_REG2.getValue(),contract.getEnterpriseCCC().getActivity().getQuoteRegimeCode().substring(1, 2));
				setPdfFieldValue(IndefiniteCommonField.CCC_REG3.getValue(),contract.getEnterpriseCCC().getActivity().getQuoteRegimeCode().substring(2, 3));
				setPdfFieldValue(IndefiniteCommonField.CCC_REG4.getValue(),contract.getEnterpriseCCC().getActivity().getQuoteRegimeCode().substring(3, 4));
				if(contract.getEnterpriseCCC().getCcc().length()==11){
					setPdfFieldValue(IndefiniteCommonField.CCC_PROV1.getValue(),contract.getEnterpriseCCC().getCcc().substring(0, 1));
					setPdfFieldValue(IndefiniteCommonField.CCC_PROV2.getValue(),contract.getEnterpriseCCC().getCcc().substring(1, 2));
					setPdfFieldValue(IndefiniteCommonField.CCC_NISS.getValue(),contract.getEnterpriseCCC().getCcc().substring(2, 9));
					setPdfFieldValue(IndefiniteCommonField.CCC_CONTROL_DIGIT1.getValue(),contract.getEnterpriseCCC().getCcc().substring(9, 10));
					setPdfFieldValue(IndefiniteCommonField.CCC_CONTROL_DIGIT2.getValue(),contract.getEnterpriseCCC().getCcc().substring(10, 11));
				} else {
					setPdfFieldValue(IndefiniteCommonField.CCC_NISS.getValue(),contract.getEnterpriseCCC().getCcc());
				}
				setPdfFieldValue(IndefiniteCommonField.CCC_ACTIVITY.getValue(),contract.getEnterpriseCCC().getActivity().getDescription());
				setPdfFieldValue(IndefiniteCommonField.CCC_ACTIVITY_CODE1.getValue(),contract.getEnterpriseCCC().getActivity().getCnae2009().getCode().substring(0, 1));
				setPdfFieldValue(IndefiniteCommonField.CCC_ACTIVITY_CODE2.getValue(),contract.getEnterpriseCCC().getActivity().getCnae2009().getCode().substring(1, 2));
			}
			/* 
			 * Contract workplace fields
			 */
			try {
				GeoZone country = obtainCountry(contract.getWorkPlace().getAddress().getGeozone());
				setPdfFieldValue(IndefiniteCommonField.WORKPLACE_COUNTRY.getValue(),country.getName());
				setPdfFieldValue(IndefiniteCommonField.WORKPLACE_COUNTRY_CODE1.getValue(),String.valueOf(obtainCountryByValue(country).getIsoNum()).substring(0,1));
				setPdfFieldValue(IndefiniteCommonField.WORKPLACE_COUNTRY_CODE2.getValue(),String.valueOf(obtainCountryByValue(country).getIsoNum()).substring(1,2));
				setPdfFieldValue(IndefiniteCommonField.WORKPLACE_COUNTRY_CODE3.getValue(),String.valueOf(obtainCountryByValue(country).getIsoNum()).substring(2,3));
			} catch (StringIndexOutOfBoundsException aie) {
				// do nothing
			} catch (NullPointerException npe) {
				// do nothing
			}
			try {
				RegistryAddress address = contract.getWorkPlace().getAddress();
				ResourceBundle bundle = ResourceBundle.getBundle("com.esferalia.aon.payroll.i18n.municipalities");
				setPdfFieldValue(IndefiniteCommonField.WORKPLACE_MUNICIPALITY.getValue(),bundle.getString(address.getMunicipalityCode()));
				setPdfFieldValue(IndefiniteCommonField.WORKPLACE_MUNICIPALITY_CODE1.getValue(),address.getMunicipalityCode().substring(0, 1));
				setPdfFieldValue(IndefiniteCommonField.WORKPLACE_MUNICIPALITY_CODE2.getValue(),address.getMunicipalityCode().substring(1, 2));
				setPdfFieldValue(IndefiniteCommonField.WORKPLACE_MUNICIPALITY_CODE3.getValue(),address.getMunicipalityCode().substring(2, 3));
				setPdfFieldValue(IndefiniteCommonField.WORKPLACE_MUNICIPALITY_CODE4.getValue(),address.getMunicipalityCode().substring(3, 4));
				setPdfFieldValue(IndefiniteCommonField.WORKPLACE_MUNICIPALITY_CODE5.getValue(),address.getMunicipalityCode().substring(4, 5));
			} catch (StringIndexOutOfBoundsException aie) {
				// do nothing
			} catch (NullPointerException npe) {
				// do nothing
			}
			/*
			 * Contract employee fields
			 */
			setPdfFieldValue(IndefiniteCommonField.EMPLOYEE_NAME.getValue(),contract.getPerson().getFullName());
			setPdfFieldValue(IndefiniteCommonField.EMPLOYEE_NIF.getValue(),contract.getPerson().getRegistry().getDocument());
			if(contract.getPerson().getBirthDate()!=null){
				SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
				setPdfFieldValue(IndefiniteCommonField.EMPLOYEE_BIRTH_DATE.getValue(),formatter.format(contract.getPerson().getBirthDate()));
			}
			setPdfFieldValue(IndefiniteCommonField.EMPLOYEE_NSS.getValue(),contract.getPerson().getSocialSecurityNumber());
			if(contrata!=null && contrata.getNivelFormativo()!=null){
				setPdfFieldValue(IndefiniteCommonField.EMPLOYEE_FORMATION_LEVEL.getValue(),contrata.getNivelFormativo().getDescription());
				setPdfFieldValue(IndefiniteCommonField.EMPLOYEE_FORMATION_CODE1.getValue(),contrata.getNivelFormativo().getCode().substring(0, 1));
				setPdfFieldValue(IndefiniteCommonField.EMPLOYEE_FORMATION_CODE2.getValue(),contrata.getNivelFormativo().getCode().substring(1, 2));
			}
			try {
				setPdfFieldValue(IndefiniteCommonField.EMPLOYEE_COUNTRY.getValue(),String.valueOf(contract.getPerson().getRegistry().getNationality().getName(getLocale())));
				setPdfFieldValue(IndefiniteCommonField.EMPLOYEE_COUNTRY_CODE1.getValue(),String.valueOf(contract.getPerson().getRegistry().getNationality().getIsoNum()).substring(0,1));
				setPdfFieldValue(IndefiniteCommonField.EMPLOYEE_COUNTRY_CODE2.getValue(),String.valueOf(contract.getPerson().getRegistry().getNationality().getIsoNum()).substring(1,2));
				setPdfFieldValue(IndefiniteCommonField.EMPLOYEE_COUNTRY_CODE3.getValue(),String.valueOf(contract.getPerson().getRegistry().getNationality().getIsoNum()).substring(2,3));
			} catch (StringIndexOutOfBoundsException aie) {
				// do nothing
			} catch (NullPointerException npe) {
				// do nothing
			}
			try {
				RegistryAddress address = contract.getPerson().getRegistry().getDefaultAddress();
				ResourceBundle bundle = ResourceBundle.getBundle("com.esferalia.aon.payroll.i18n.municipalities");
				setPdfFieldValue(IndefiniteCommonField.EMPLOYEE_ADDRESS_MUNICIPALITY.getValue(),bundle.getString(address.getMunicipalityCode()));
				setPdfFieldValue(IndefiniteCommonField.EMPLOYEE_ADDRESS_MUNICIPALITY_CODE1.getValue(),address.getMunicipalityCode().substring(0, 1));
				setPdfFieldValue(IndefiniteCommonField.EMPLOYEE_ADDRESS_MUNICIPALITY_CODE2.getValue(),address.getMunicipalityCode().substring(1, 2));
				setPdfFieldValue(IndefiniteCommonField.EMPLOYEE_ADDRESS_MUNICIPALITY_CODE3.getValue(),address.getMunicipalityCode().substring(2, 3));
				setPdfFieldValue(IndefiniteCommonField.EMPLOYEE_ADDRESS_MUNICIPALITY_CODE4.getValue(),address.getMunicipalityCode().substring(3, 4));
				setPdfFieldValue(IndefiniteCommonField.EMPLOYEE_ADDRESS_MUNICIPALITY_CODE5.getValue(),address.getMunicipalityCode().substring(4, 5));
			} catch (StringIndexOutOfBoundsException aie) {
				// do nothing
			} catch (NullPointerException npe) {
				// do nothing
			}
			try {
				GeoZone country = obtainCountry(contract.getPerson().getRegistry().getDefaultAddress().getGeozone());
				setPdfFieldValue(IndefiniteCommonField.EMPLOYEE_ADDRESS_COUNTRY.getValue(),country.getName());
				setPdfFieldValue(IndefiniteCommonField.EMPLOYEE_ADDRESS_COUNTRY_CODE1.getValue(),String.valueOf(obtainCountryByValue(country).getIsoNum()).substring(0,1));
				setPdfFieldValue(IndefiniteCommonField.EMPLOYEE_ADDRESS_COUNTRY_CODE2.getValue(),String.valueOf(obtainCountryByValue(country).getIsoNum()).substring(1,2));
				setPdfFieldValue(IndefiniteCommonField.EMPLOYEE_ADDRESS_COUNTRY_CODE3.getValue(),String.valueOf(obtainCountryByValue(country).getIsoNum()).substring(2,3));
			} catch (StringIndexOutOfBoundsException aie) {
				// do nothing
			} catch (NullPointerException npe) {
				// do nothing
			}
			try {
				setPdfFieldValue(IndefiniteCommonField.LEGAL_REPRESENTATIVE_NAME.getValue(),"");
				setPdfFieldValue(IndefiniteCommonField.LEGAL_REPRESENTATIVE_NIF.getValue(),"");
				setPdfFieldValue(IndefiniteCommonField.LEGAL_REPRESENTATIVE_CHARGE.getValue(),"");
			} catch (StringIndexOutOfBoundsException aie) {
				// do nothing
			} catch (NullPointerException npe) {
				// do nothing
			}
			
			/*
			 * Contract page 1
			 */
			if(contrata!=null){
				setPdfFieldValue(IndefiniteCommonField.PROFESSION.getValue(), contrata.getCno().getTitle());
			}
			setPdfFieldValue(IndefiniteCommonField.CATEGORY.getValue(), contract.getCategoryDescription());
			setPdfFieldValue(IndefiniteCommonField.WORKPLACE_FULL_ADDRESS.getValue(), null);
			setPdfFieldValue(IndefiniteCommonField.WORKPLACE_FULL_ADDRESS_MORE.getValue(), contract.getWorkPlace().getAddress().getFullAddress()+", "+contract.getWorkPlace().getAddress().getLocation());
			
			if(StringUtils.isNotBlank(getContractInfoMap(contract).get(IndefiniteCommonField.EMPLOYEE_CONTRACT_DISTANCE.toString()))){
				setPdfFieldValue(IndefiniteCommonField.EMPLOYEE_CONTRACT_DISTANCE.getValue(), getContractInfoMap(contract).get(IndefiniteCommonField.EMPLOYEE_CONTRACT_DISTANCE.toString()));
			}
			if(StringUtils.isNotBlank(getContractInfoMap(contract).get(IndefiniteCommonField.EMPLOYEE_CONTRACT_DISTANCE_ADDR.toString()))){
				setPdfFieldValue(IndefiniteCommonField.EMPLOYEE_CONTRACT_DISTANCE_ADDR.getValue(), getContractInfoMap(contract).get(IndefiniteCommonField.EMPLOYEE_CONTRACT_DISTANCE_ADDR.toString()));
			}
			
			// setPdfFieldValue(IndefiniteCommonField.DISCONTONUOUS_WORK_DESCRIPTION.getValue(), null);
			// setPdfFieldValue(IndefiniteCommonField.DISCONTONUOUS_WORK_ACTIVITY.getValue(), null);
			// setPdfFieldValue(IndefiniteCommonField.DISCONTONUOUS_WORK_DURATION.getValue(), null);
			// setPdfFieldValue(IndefiniteCommonField.DISCONTONUOUS_WORK_ESTIMATED_DURATION.getValue(), null);
			// setPdfFieldValue(IndefiniteCommonField.DISCONTONUOUS_WORK_AGREEMENT_COLLECTIVE.getValue(), null);
			// setPdfFieldValue(IndefiniteCommonField.DISCONTONUOUS_WORK_ESTIMATED_JOURNAL_HOURS.getValue(), null);
			// setPdfFieldValue(IndefiniteCommonField.DISCONTONUOUS_WORK_ESTIMATED_JOURNAL_PERIOD.getValue(), null);
			// setPdfFieldValue(IndefiniteCommonField.DISCONTONUOUS_WORK_ESTIMATED_SCHEDULE.getValue(), null);
			// setPdfFieldValue(IndefiniteCommonField.DISCONTINUOUS_AGREEMENT_COLLECTIVE_YES.getValue(), null);
			// setPdfFieldValue(IndefiniteCommonField.DISCONTINUOUS_AGREEMENT_COLLECTIVE_NO.getValue(), null);
			// setPdfFieldValue(IndefiniteCommonField.**UNKNOWN**.getValue(), null);
			
			if(code.getValue().startsWith("1") || code.getValue().startsWith("4")){
				setPdfFieldValue(IndefiniteCommonField.FULL_TIME.getValue(), "true");
				if(StringUtils.isNotBlank(getContractInfoMap(contract).get(IndefiniteCommonField.FULL_TIME_WEEK_HOURS.toString()))){
					setPdfFieldValue(IndefiniteCommonField.FULL_TIME_WEEK_HOURS.getValue(), getContractInfoMap(contract).get(IndefiniteCommonField.FULL_TIME_WEEK_HOURS.toString()));
				}
				if(StringUtils.isNotBlank(getContractInfoMap(contract).get(IndefiniteCommonField.FULL_TIME_START_TIME.toString()))){
					setPdfFieldValue(IndefiniteCommonField.FULL_TIME_START_TIME.getValue(), getContractInfoMap(contract).get(IndefiniteCommonField.FULL_TIME_START_TIME.toString()));
				}
				if(StringUtils.isNotBlank(getContractInfoMap(contract).get(IndefiniteCommonField.FULL_TIME_END_TIME.toString()))){
					setPdfFieldValue(IndefiniteCommonField.FULL_TIME_END_TIME.getValue(), getContractInfoMap(contract).get(IndefiniteCommonField.FULL_TIME_END_TIME.toString()));
				}
			} else if(code.getValue().startsWith("2") || code.getValue().startsWith("5")){
				setPdfFieldValue(IndefiniteCommonField.PARTIALLY_TIME.getValue(), "true");
				
				
				if(StringUtils.isNotBlank(getContractDataMap(contract).get(ContextVariable.WEEK_HOURS.toString()))){
					setPdfFieldValue(IndefiniteCommonField.PARTIALLY_TIME_HOURS.getValue(), getContractDataMap(contract).get(ContextVariable.WEEK_HOURS.toString()));
					setPdfFieldValue(IndefiniteCommonField.PARTIALLY_TIME_WEEKLY.getValue(), "true");
				} else if(contrata!=null){
					if(contrata.getHorasJornada()!=null){
						setPdfFieldValue(IndefiniteCommonField.PARTIALLY_TIME_HOURS.getValue(), String.valueOf(Integer.parseInt(contrata.getHorasJornada())));
					}
					if(contrata.getTipoJornada()==TEQPTIEM.TEQPTIEM_D){
						setPdfFieldValue(IndefiniteCommonField.PARTIALLY_TIME_DAYLY.getValue(), "true");
					} else if(contrata.getTipoJornada()==TEQPTIEM.TEQPTIEM_S){
						setPdfFieldValue(IndefiniteCommonField.PARTIALLY_TIME_WEEKLY.getValue(), "true");
					} else if(contrata.getTipoJornada()==TEQPTIEM.TEQPTIEM_M){
						setPdfFieldValue(IndefiniteCommonField.PARTIALLY_TIME_MONTHLY.getValue(), "true");
					} else if(contrata.getTipoJornada()==TEQPTIEM.TEQPTIEM_A){
						setPdfFieldValue(IndefiniteCommonField.PARTIALLY_TIME_YEARLY.getValue(), "true");
					}
				}
				if(StringUtils.isNotBlank(getContractInfoMap(contract).get(IndefiniteCommonField.DEFAULT_JOURNAL_HOURS.toString()))){
					setPdfFieldValue(IndefiniteCommonField.DEFAULT_JOURNAL_HOURS.getValue(), getContractInfoMap(contract).get(IndefiniteCommonField.DEFAULT_JOURNAL_HOURS.toString()));
				}
				
				String key = getContractInfoMap(contract).get(IndefiniteCommonField.COMPLEMENTARY_HOURS.toString());
				if(StringUtils.isNotBlank(key)){
					if(IndefiniteCommonField.COMPLEMENTARY_HOURS_YES.toString().equals(key)){
						setPdfFieldValue(IndefiniteCommonField.COMPLEMENTARY_HOURS_YES.getValue(), "true");
					} else if(IndefiniteCommonField.COMPLEMENTARY_HOURS_NO.toString().equals(key)){
						setPdfFieldValue(IndefiniteCommonField.COMPLEMENTARY_HOURS_NO.getValue(), "true");
					}
				}
				
			}
			
			/*
			 * Contract page 2
			 */
			dateFormatter.applyPattern("dd/MM/yyyy");
			setPdfFieldValue(IndefiniteCommonField.START_DATE.getValue(), dateFormatter.format(contract.getStartDate()));
			
			if(StringUtils.isNotBlank(getContractInfoMap(contract).get(IndefiniteCommonField.TRIAL_DURATION.toString()))){
				setPdfFieldValue(IndefiniteCommonField.TRIAL_DURATION.getValue(), getContractInfoMap(contract).get(IndefiniteCommonField.TRIAL_DURATION.toString()));
			}
			
			if(contrata!=null && contrata.isReliefData()){
				setPdfFieldValue(IndefiniteCommonField.RELIEF_CONTRACT_YES.getValue(), "true");
			} else {
				setPdfFieldValue(IndefiniteCommonField.RELIEF_CONTRACT_NO.getValue(), "true");
			}
			// setPdfFieldValue(IndefiniteCommonField.RELIEF_CONTRACT_UNEMPLOYED_IN_SEPE_MUNICIPALITY1.getValue(), "");
			// setPdfFieldValue(IndefiniteCommonField.RELIEF_CONTRACT_UNEMPLOYED_IN_SEPE_MUNICIPALITY2.getValue(), "");
			// setPdfFieldValue(IndefiniteCommonField.RELIEF_CONTRACT_PARTIALLY_CONTRACT_SEPE_MUNICIPALITY.getValue(), "");
			// setPdfFieldValue(IndefiniteCommonField.RELIEF_CONTRACT_PARTIALLY_CONTRACT_NUMBER.getValue(), "");
			// setPdfFieldValue(IndefiniteCommonField.RELIEF_CONTRACT_PARTIALLY_CONTRACT_DATE.getValue(), "");
			
			if(StringUtils.isNotBlank(getContractInfoMap(contract).get(IndefiniteCommonField.SALARY_AMOUNT.toString()))){
				setPdfFieldValue(IndefiniteCommonField.SALARY_AMOUNT.getValue(), getContractInfoMap(contract).get(IndefiniteCommonField.SALARY_AMOUNT.toString()));
			}
			if(StringUtils.isNotBlank(getContractInfoMap(contract).get(IndefiniteCommonField.SALARY_PERIOD.toString()))){
				setPdfFieldValue(IndefiniteCommonField.SALARY_PERIOD.getValue(), getContractInfoMap(contract).get(IndefiniteCommonField.SALARY_PERIOD.toString()));
			}
			if(StringUtils.isNotBlank(getContractInfoMap(contract).get(IndefiniteCommonField.SALARY_CONCEPT.toString()))){
				setPdfFieldValue(IndefiniteCommonField.SALARY_CONCEPT.getValue(), getContractInfoMap(contract).get(IndefiniteCommonField.SALARY_CONCEPT.toString()));
			}
			if(StringUtils.isNotBlank(getContractInfoMap(contract).get(IndefiniteCommonField.HOLIDAYS.toString()))){
				setPdfFieldValue(IndefiniteCommonField.HOLIDAYS.getValue(), getContractInfoMap(contract).get(IndefiniteCommonField.HOLIDAYS.toString()));
			}
			if(contract.getAgreementLevelCategory()!=null && contract.getAgreementLevelCategory().getId()!=null){
				setPdfFieldValue(IndefiniteCommonField.AGREEMENT_COLLECTIVE.getValue(), contract.getAgreementLevelCategory().getLevel().getAgreement().getDescription());
			}
			if(StringUtils.isNotBlank(getContractInfoMap(contract).get(IndefiniteCommonField.SEPE_MUNICIPALITY.toString()))){
				setPdfFieldValue(IndefiniteCommonField.SEPE_MUNICIPALITY.getValue(), getContractInfoMap(contract).get(IndefiniteCommonField.SEPE_MUNICIPALITY.toString()));
			}
			
			
			/*
			 *  OPTIONS PAGE
			 */
			modelOption = ModelOption.valueOf(getContractInfoMap(contract).get(ContractVariable.CONTRACT_MODEL_OPTION.getValue()));
			
			if(modelOption == ModelOption.INDEFINITE_OPT1){
				setPdfFieldValue(IndefiniteOptionField.MAIN_OPT1_CHECK.getValue(),"true");
				setPdfFieldValue(IndefiniteOptionField.OPT1_OPTION_CHECK.getValue(),"true");
				if(code == ContractCode.C100){
					setPdfFieldValue(IndefiniteOptionField.OPT1_TC2_100.getValue(),"true");
				} else if(code == ContractCode.C200){
					setPdfFieldValue(IndefiniteOptionField.OPT1_TC2_200.getValue(),"true");
				} else if(code == ContractCode.C300){
					setPdfFieldValue(IndefiniteOptionField.OPT1_TC2_300.getValue(),"true");
				}
			} else if(modelOption == ModelOption.INDEFINITE_OPT5){
				setPdfFieldValue(IndefiniteOptionField.MAIN_OPT5_CHECK.getValue(),"true");
				setPdfFieldValue(IndefiniteOptionField.OPT5_OPTION_CHECK.getValue(),"true");
				if(code == ContractCode.C100){
					setPdfFieldValue(IndefiniteOptionField.OPT5_FULL_TIME.getValue(),"true");
					setPdfFieldValue(IndefiniteOptionField.OPT5_TC2_100.getValue(),"true");
				} else if(code == ContractCode.C150){
					setPdfFieldValue(IndefiniteOptionField.OPT5_FULL_TIME.getValue(),"true");
					setPdfFieldValue(IndefiniteOptionField.OPT5_TC2_150.getValue(),"true");
				} else if(code == ContractCode.C200){
					setPdfFieldValue(IndefiniteOptionField.OPT5_PARTIALLY_TIME.getValue(),"true");
					setPdfFieldValue(IndefiniteOptionField.OPT5_TC2_200.getValue(),"true");
				} else if(code == ContractCode.C250){
					setPdfFieldValue(IndefiniteOptionField.OPT5_PARTIALLY_TIME.getValue(),"true");
					setPdfFieldValue(IndefiniteOptionField.OPT5_TC2_250.getValue(),"true");
				} else if(code == ContractCode.C300){
					setPdfFieldValue(IndefiniteOptionField.OPT5_DISCONTINUOUS_TIME.getValue(),"true");
					setPdfFieldValue(IndefiniteOptionField.OPT5_TC2_300.getValue(),"true");
				} else if(code == ContractCode.C350){
					setPdfFieldValue(IndefiniteOptionField.OPT5_DISCONTINUOUS_TIME.getValue(),"true");
					setPdfFieldValue(IndefiniteOptionField.OPT5_TC2_350.getValue(),"true");
				}
				
				String key = getContractInfoMap(contract).get(IndefiniteOptionField.OPT5_BONUS_ART4_RDL3_2012.toString());
				if(StringUtils.isNotBlank(key)){
					if(IndefiniteOptionField.OPT5_BONUS_ART4_RDL3_2012_YES.toString().equals(key)){
						setPdfFieldValue(IndefiniteOptionField.OPT5_BONUS_ART4_RDL3_2012_YES.getValue(), "true");
					} else if(IndefiniteOptionField.OPT5_BONUS_ART4_RDL3_2012_NO.toString().equals(key)){
						setPdfFieldValue(IndefiniteOptionField.OPT5_BONUS_ART4_RDL3_2012_NO.getValue(), "true");
					}
				}
				
				if(StringUtils.isNotBlank(getContractInfoMap(contract).get(IndefiniteOptionField.OPT5_UNEMPLOYED_BT_16_30.toString()))
						|| StringUtils.isNotBlank(getContractInfoMap(contract).get(IndefiniteOptionField.OPT5_UNEMPLOYED_GT_45.toString())) ){
					setPdfFieldValue(IndefiniteOptionField.OPT5_REGISTERED_IN_EMPLOYMENT_OFFICE.getValue(),"true");

					key = getContractInfoMap(contract).get(IndefiniteOptionField.OPT5_UNEMPLOYED_BT_16_30.toString());
					if(StringUtils.isNotBlank(key)){
						setPdfFieldValue(IndefiniteOptionField.OPT5_UNEMPLOYED_BT_16_30_CHECK.getValue(),"true");
						if(IndefiniteOptionField.OPT5_UNEMPLOYED_BT_16_30_JUNIOR.toString().equals(key)){
							setPdfFieldValue(IndefiniteOptionField.OPT5_UNEMPLOYED_BT_16_30_JUNIOR.getValue(), "true");
						} else if(IndefiniteOptionField.OPT5_UNEMPLOYED_BT_16_30_FEMALE.toString().equals(key)){
							setPdfFieldValue(IndefiniteOptionField.OPT5_UNEMPLOYED_BT_16_30_FEMALE.getValue(), "true");
						}
					}

					key = getContractInfoMap(contract).get(IndefiniteOptionField.OPT5_UNEMPLOYED_GT_45.toString());
					if(StringUtils.isNotBlank(key)){
						setPdfFieldValue(IndefiniteOptionField.OPT5_UNEMPLOYED_GT_45_CHECK.getValue(),"true");
						if(IndefiniteOptionField.OPT5_UNEMPLOYED_GT_45_MALE.toString().equals(key)){
							setPdfFieldValue(IndefiniteOptionField.OPT5_UNEMPLOYED_GT_45_MALE.getValue(), "true");
						} else if(IndefiniteOptionField.OPT5_UNEMPLOYED_GT_45_FEMALE.toString().equals(key)){
							setPdfFieldValue(IndefiniteOptionField.OPT5_UNEMPLOYED_GT_45_FEMALE.getValue(), "true");
						}
					}
				}
				
				if(StringUtils.isNotBlank(getContractInfoMap(contract).get(IndefiniteOptionField.OPT5_UNEMPL_WITH_3_MONTH_BENEFIT.toString()))){
					setPdfFieldValue(IndefiniteOptionField.OPT5_UNEMPL_WITH_3_MONTH_BENEFIT.getValue(),"true");
				}
				if(StringUtils.isNotBlank(getContractInfoMap(contract).get(IndefiniteOptionField.OPT5_FIRST_EMPLOYEE_AND_LT_30.toString()))){
					setPdfFieldValue(IndefiniteOptionField.OPT5_FIRST_EMPLOYEE_AND_LT_30.getValue(),"true");
				}
			} else if(modelOption == ModelOption.INDEFINITE_OPT6){
				setPdfFieldValue(IndefiniteOptionField.MAIN_OPT6_CHECK.getValue(),"true");
				setPdfFieldValue(IndefiniteOptionField.OPT6_OPTION_CHECK.getValue(),"true");
				if(code == ContractCode.C100){
					setPdfFieldValue(IndefiniteOptionField.OPT6_TC2_100.getValue(),"true");
				} else if(code == ContractCode.C200){
					setPdfFieldValue(IndefiniteOptionField.OPT6_TC2_200.getValue(),"true");
				}
				// TODO: complete this
				setPdfFieldValue(IndefiniteOptionField.OPT6_LT_30_EMPLOYEE.getValue(),"");
				setPdfFieldValue(IndefiniteOptionField.OPT6_LT_35_EMPLOYEE_AND_HANDICAP_GTE_33.getValue(),"");
				setPdfFieldValue(IndefiniteOptionField.OPT6_AGREEMENT_COLLECTIVE1.getValue(),"");
				setPdfFieldValue(IndefiniteOptionField.OPT6_AGREEMENT_COLLECTIVE2.getValue(),"");
			} else if(modelOption == ModelOption.INDEFINITE_OPT7){
				setPdfFieldValue(IndefiniteOptionField.MAIN_OPT7_CHECK.getValue(),"true");
				setPdfFieldValue(IndefiniteOptionField.OPT7_OPTION_CHECK.getValue(),"true");
				if(code == ContractCode.C100){
					setPdfFieldValue(IndefiniteOptionField.OPT7_TC2_100.getValue(),"true");
				} else if(code == ContractCode.C200){
					setPdfFieldValue(IndefiniteOptionField.OPT7_TC2_200.getValue(),"true");
				} else if(code == ContractCode.C300){
					setPdfFieldValue(IndefiniteOptionField.OPT7_TC2_300.getValue(),"true");
				}
				// TODO: complete this
				setPdfFieldValue(IndefiniteOptionField.OPT7_UNEMPLOYED_DURING_12_MONTH.getValue(),"");
				setPdfFieldValue(IndefiniteOptionField.OPT7_PROFFESIONAL_RECUALIFICATION.getValue(),"");
				setPdfFieldValue(IndefiniteOptionField.OPT7_AGREEMENT_COLLECTIVE1.getValue(),"");
				setPdfFieldValue(IndefiniteOptionField.OPT7_AGREEMENT_COLLECTIVE2.getValue(),"");
			} else if(modelOption == ModelOption.INDEFINITE_OPT15){
				setPdfFieldValue(IndefiniteOptionField.MAIN_OPT15_CHECK.getValue(),"true");
				setPdfFieldValue(IndefiniteOptionField.OPT15_OPTION_CHECK.getValue(),"true");
				if(code == ContractCode.C100){
					setPdfFieldValue(IndefiniteOptionField.OPT15_TC2_100.getValue(),"true");
				} else if(code == ContractCode.C200){
					setPdfFieldValue(IndefiniteOptionField.OPT15_TC2_200.getValue(),"true");
				}
				// TODO: complete this
				setPdfFieldValue(IndefiniteOptionField.OPT15_ONSITE_HOURS_YES.getValue(),"");
				setPdfFieldValue(IndefiniteOptionField.OPT15_ONSITE_HOURS_NO.getValue(),"");
				setPdfFieldValue(IndefiniteOptionField.OPT15_ONSITE_WEEK_HOURS.getValue(),"");
				setPdfFieldValue(IndefiniteOptionField.OPT15_ONSITE_HOURS_DISTRIBUTION.getValue(),"");
				setPdfFieldValue(IndefiniteOptionField.OPT15_SALARY_OPT1.getValue(),"");
				setPdfFieldValue(IndefiniteOptionField.OPT15_SALARY_OPT2.getValue(),"");
				setPdfFieldValue(IndefiniteOptionField.OPT15_SALARY_OPT3.getValue(),"");
				setPdfFieldValue(IndefiniteOptionField.OPT15_OVERNIGHT_YES.getValue(),"");
				setPdfFieldValue(IndefiniteOptionField.OPT15_OVERNIGHT_NO.getValue(),"");
				setPdfFieldValue(IndefiniteOptionField.OPT15_OVERNIGHT_WEEK_DAYS.getValue(),"");
			} else if(modelOption == ModelOption.INDEFINITE_OPT17){
				setPdfFieldValue(IndefiniteOptionField.MAIN_OPT17_CHECK.getValue(),"true");
				setPdfFieldValue(IndefiniteOptionField.OPT17_OPTION_CHECK.getValue(),"true");
				String subsidized = getContractDataMap(contract).get(ContextVariable.SUBSIDIZED.getName());
				if(code == ContractCode.C109 || code == ContractCode.C139 | code == ContractCode.C189){
					if(code == ContractCode.C139){
						setPdfFieldValue(IndefiniteOptionField.OPT17_FULL_TIME.getValue(),"true");
						setPdfFieldValue(IndefiniteOptionField.OPT17_TC2_139.getValue(),"true");
					} else if(code == ContractCode.C109){
						setPdfFieldValue(IndefiniteOptionField.OPT17_FULL_TIME.getValue(),"true");
						setPdfFieldValue(IndefiniteOptionField.OPT17_TC2_109.getValue(),"true");
					} else if(code == ContractCode.C189){
						setPdfFieldValue(IndefiniteOptionField.OPT17_FULL_TIME.getValue(),"true");
						setPdfFieldValue(IndefiniteOptionField.OPT17_TC2_189.getValue(),"true");
					} 
					if( Boolean.parseBoolean(subsidized) ){
						setPdfFieldValue(IndefiniteOptionField.OPT17_FULL_TIME_QUOTE_BONUS_YES.getValue(),"true");
					} else {
						setPdfFieldValue(IndefiniteOptionField.OPT17_FULL_TIME_QUOTE_BONUS_NO.getValue(),"true");
					}
				} else if(code == ContractCode.C209 || code == ContractCode.C239 || code == ContractCode.C289){
					if(code == ContractCode.C239){
						setPdfFieldValue(IndefiniteOptionField.OPT17_PARTIALLY_TIME.getValue(),"true");
						setPdfFieldValue(IndefiniteOptionField.OPT17_TC2_239.getValue(),"true");
					} else if(code == ContractCode.C209){
						setPdfFieldValue(IndefiniteOptionField.OPT17_PARTIALLY_TIME.getValue(),"true");
						setPdfFieldValue(IndefiniteOptionField.OPT17_TC2_209.getValue(),"true");
					} else if(code == ContractCode.C289){
						setPdfFieldValue(IndefiniteOptionField.OPT17_PARTIALLY_TIME.getValue(),"true");
						setPdfFieldValue(IndefiniteOptionField.OPT17_TC2_289.getValue(),"true");
					} 
					if( Boolean.parseBoolean(subsidized) ){
						setPdfFieldValue(IndefiniteOptionField.OPT17_PARTIALLY_TIME_QUOTE_BONUS_YES.getValue(),"true");
					} else {
						setPdfFieldValue(IndefiniteOptionField.OPT17_PARTIALLY_TIME_QUOTE_BONUS_NO.getValue(),"true");
					}
				} else if(code == ContractCode.C309 || code == ContractCode.C339 || code == ContractCode.C389){
					if(code == ContractCode.C339){
						setPdfFieldValue(IndefiniteOptionField.OPT17_DISCONTINUOUS_TIME.getValue(),"true");
						setPdfFieldValue(IndefiniteOptionField.OPT17_TC2_339.getValue(),"true");
					} else if(code == ContractCode.C309){
						setPdfFieldValue(IndefiniteOptionField.OPT17_DISCONTINUOUS_TIME.getValue(),"true");
						setPdfFieldValue(IndefiniteOptionField.OPT17_TC2_309.getValue(),"true");
					} else if(code == ContractCode.C389){
						setPdfFieldValue(IndefiniteOptionField.OPT17_DISCONTINUOUS_TIME.getValue(),"true");
						setPdfFieldValue(IndefiniteOptionField.OPT17_TC2_389.getValue(),"true");
					}
					if( Boolean.parseBoolean(subsidized) ){
						setPdfFieldValue(IndefiniteOptionField.OPT17_DISCONTINUOUS_TIME_QUOTE_BONUS_YES.getValue(),"true");
					} else {
						setPdfFieldValue(IndefiniteOptionField.OPT17_DISCONTINUOUS_TIME_QUOTE_BONUS_NO.getValue(),"true");
					}
				}
				// TODO: complete this
				setPdfFieldValue(IndefiniteOptionField.OPT17_SEPE_MUNICIPALITY.getValue(),"");
				setPdfFieldValue(IndefiniteOptionField.OPT17_TRANSFORM_DATE.getValue(),"");
				setPdfFieldValue(IndefiniteOptionField.OPT17_IS_FULL_TIME.getValue(),"");
				setPdfFieldValue(IndefiniteOptionField.OPT17_IS_FULL_TIME_DISCONTINUOUS.getValue(),"");
				setPdfFieldValue(IndefiniteOptionField.OPT17_SOURCE_CONTRACT.getValue(),"");
				setPdfFieldValue(IndefiniteOptionField.OPT17_SOURCE_CONTRACT_START_DATE.getValue(),"");
				setPdfFieldValue(IndefiniteOptionField.OPT17_SOURCE_CONTRACT_SEPE_MUNICIPALITY.getValue(),"");
				setPdfFieldValue(IndefiniteOptionField.OPT17_SOURCE_CONTRACT_SEPE_DATE.getValue(),"");
				setPdfFieldValue(IndefiniteOptionField.OPT17_SOURCE_CONTRACT_SEPE_ID.getValue(),"");
			}
			
		} catch (IOException e) {
			// do nothing
		} catch (ManagerBeanException e) {
			// do nothing
		}
	}
	
}
	
	