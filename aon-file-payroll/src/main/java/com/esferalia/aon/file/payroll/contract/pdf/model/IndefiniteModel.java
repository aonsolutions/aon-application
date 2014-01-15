package com.esferalia.aon.file.payroll.contract.pdf.model;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ResourceBundle;

import org.apache.commons.lang.StringUtils;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.geozone.GeoZone;
import com.code.aon.registry.RegistryAddress;
import com.code.aon.registry.RegistryDirStaff;
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
	public void loadPdfFields(ContractCode code, Contract contract, IContrataParams contrataParams) throws UnsupportedContractDocumentException{
		
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
			setPdfFieldValue(IndefiniteCommonFieldName.ENTERPRISE_CIF.getValue(),contract.getWorkPlace().getEnterprise().getRegistry().getDocument());
			RegistryDirStaff rDirStaff = obtainRegistryDirStaff(contract); 
			try {
				setPdfFieldValue(IndefiniteCommonFieldName.ENTERPRISE_DIR_STAFF_NAME.getValue(),rDirStaff.getName());
				setPdfFieldValue(IndefiniteCommonFieldName.ENTERPRISE_DIR_STAFF_NIF.getValue(),rDirStaff.getDocument());
				String rDirStaddCharge = null;
				if ( rDirStaff.isShareHolder() ){
					rDirStaddCharge = "Socio";
				} else if ( rDirStaff.isRepresentative() ){
					rDirStaddCharge = "Apoderado";
				} else if( rDirStaff.isDirector() ){
					rDirStaddCharge = "Administrador";
				} else if ( rDirStaff.isRepresentativeLabor() ){
					rDirStaddCharge = "Representante laboral";
				}
				setPdfFieldValue(IndefiniteCommonFieldName.ENTERPRISE_DIR_STAFF_CHARGE.getValue(),rDirStaddCharge);
			} catch (NullPointerException npe) {
				// do nothing
			}
			setPdfFieldValue(IndefiniteCommonFieldName.ENTERPRISE_NAME.getValue(),contract.getWorkPlace().getEnterprise().getRegistry().getFullName());
			setPdfFieldValue(IndefiniteCommonFieldName.ENTERPRISE_ADDRESS.getValue(),contract.getWorkPlace().getEnterprise().getRegistry().getDefaultAddress().getFullAddress());
			try {	
				setPdfFieldValue(IndefiniteCommonFieldName.ENTERPRISE_COUNTRY.getValue(),contract.getWorkPlace().getEnterprise().getRegistry().getNationality().getName(getLocale()));
				setPdfFieldValue(IndefiniteCommonFieldName.ENTERPRISE_COUNTRY_CODE1.getValue(),String.valueOf(contract.getWorkPlace().getEnterprise().getRegistry().getNationality().getIsoNum()).substring(0,1));
				setPdfFieldValue(IndefiniteCommonFieldName.ENTERPRISE_COUNTRY_CODE2.getValue(),String.valueOf(contract.getWorkPlace().getEnterprise().getRegistry().getNationality().getIsoNum()).substring(1,2));
				setPdfFieldValue(IndefiniteCommonFieldName.ENTERPRISE_COUNTRY_CODE3.getValue(),String.valueOf(contract.getWorkPlace().getEnterprise().getRegistry().getNationality().getIsoNum()).substring(2,3));
			} catch (StringIndexOutOfBoundsException aie) {
				// do nothing
			} catch (NullPointerException npe) {
				// do nothing
			}
			try {	
				RegistryAddress address = contract.getWorkPlace().getEnterprise().getRegistry().getDefaultAddress();
				ResourceBundle bundle = ResourceBundle.getBundle("com.esferalia.aon.payroll.i18n.municipalities");
				setPdfFieldValue(IndefiniteCommonFieldName.ENTERPRISE_MUNICIPALITY.getValue(),bundle.getString(address.getMunicipalityCode()));
				setPdfFieldValue(IndefiniteCommonFieldName.ENTERPRISE_MUNICIPALITY_CODE1.getValue(),address.getMunicipalityCode().substring(0, 1));
				setPdfFieldValue(IndefiniteCommonFieldName.ENTERPRISE_MUNICIPALITY_CODE2.getValue(),address.getMunicipalityCode().substring(1, 2));
				setPdfFieldValue(IndefiniteCommonFieldName.ENTERPRISE_MUNICIPALITY_CODE3.getValue(),address.getMunicipalityCode().substring(2, 3));
				setPdfFieldValue(IndefiniteCommonFieldName.ENTERPRISE_MUNICIPALITY_CODE4.getValue(),address.getMunicipalityCode().substring(3, 4));
				setPdfFieldValue(IndefiniteCommonFieldName.ENTERPRISE_MUNICIPALITY_CODE5.getValue(),address.getMunicipalityCode().substring(4, 5));
			} catch (StringIndexOutOfBoundsException aie) {
				// do nothing
			} catch (NullPointerException npe) {
				// do nothing
			}
			try {	
				setPdfFieldValue(IndefiniteCommonFieldName.ENTERPRISE_ZIP1.getValue(),contract.getWorkPlace().getEnterprise().getRegistry().getDefaultAddress().getZip().substring(0, 1));
				setPdfFieldValue(IndefiniteCommonFieldName.ENTERPRISE_ZIP2.getValue(),contract.getWorkPlace().getEnterprise().getRegistry().getDefaultAddress().getZip().substring(1, 2));
				setPdfFieldValue(IndefiniteCommonFieldName.ENTERPRISE_ZIP3.getValue(),contract.getWorkPlace().getEnterprise().getRegistry().getDefaultAddress().getZip().substring(2, 3));
				setPdfFieldValue(IndefiniteCommonFieldName.ENTERPRISE_ZIP4.getValue(),contract.getWorkPlace().getEnterprise().getRegistry().getDefaultAddress().getZip().substring(3, 4));
				setPdfFieldValue(IndefiniteCommonFieldName.ENTERPRISE_ZIP5.getValue(),contract.getWorkPlace().getEnterprise().getRegistry().getDefaultAddress().getZip().substring(4, 5));
			} catch (StringIndexOutOfBoundsException aie) {
				// do nothing
			} catch (NullPointerException npe) {
				// do nothing
			}
			/* 
			 * Contract ccc fields
			 */
			if(contract.getEnterpriseCCC()!=null){
				setPdfFieldValue(IndefiniteCommonFieldName.CCC_REG1.getValue(),contract.getEnterpriseCCC().getActivity().getQuoteRegimeCode().substring(0, 1));
				setPdfFieldValue(IndefiniteCommonFieldName.CCC_REG2.getValue(),contract.getEnterpriseCCC().getActivity().getQuoteRegimeCode().substring(1, 2));
				setPdfFieldValue(IndefiniteCommonFieldName.CCC_REG3.getValue(),contract.getEnterpriseCCC().getActivity().getQuoteRegimeCode().substring(2, 3));
				setPdfFieldValue(IndefiniteCommonFieldName.CCC_REG4.getValue(),contract.getEnterpriseCCC().getActivity().getQuoteRegimeCode().substring(3, 4));
				if(contract.getEnterpriseCCC().getCcc().length()==11){
					setPdfFieldValue(IndefiniteCommonFieldName.CCC_PROV1.getValue(),contract.getEnterpriseCCC().getCcc().substring(0, 1));
					setPdfFieldValue(IndefiniteCommonFieldName.CCC_PROV2.getValue(),contract.getEnterpriseCCC().getCcc().substring(1, 2));
					setPdfFieldValue(IndefiniteCommonFieldName.CCC_NISS.getValue(),contract.getEnterpriseCCC().getCcc().substring(2, 9));
					setPdfFieldValue(IndefiniteCommonFieldName.CCC_CONTROL_DIGIT1.getValue(),contract.getEnterpriseCCC().getCcc().substring(9, 10));
					setPdfFieldValue(IndefiniteCommonFieldName.CCC_CONTROL_DIGIT2.getValue(),contract.getEnterpriseCCC().getCcc().substring(10, 11));
				} else {
					setPdfFieldValue(IndefiniteCommonFieldName.CCC_NISS.getValue(),contract.getEnterpriseCCC().getCcc());
				}
				setPdfFieldValue(IndefiniteCommonFieldName.CCC_ACTIVITY.getValue(),contract.getEnterpriseCCC().getActivity().getDescription());
				setPdfFieldValue(IndefiniteCommonFieldName.CCC_ACTIVITY_CODE1.getValue(),contract.getEnterpriseCCC().getActivity().getCnae2009().getCode().substring(0, 1));
				setPdfFieldValue(IndefiniteCommonFieldName.CCC_ACTIVITY_CODE2.getValue(),contract.getEnterpriseCCC().getActivity().getCnae2009().getCode().substring(1, 2));
			}
			/* 
			 * Contract workplace fields
			 */
			try {
				GeoZone country = obtainCountry(contract.getWorkPlace().getAddress().getGeozone());
				setPdfFieldValue(IndefiniteCommonFieldName.WORKPLACE_COUNTRY.getValue(),country.getName());
				setPdfFieldValue(IndefiniteCommonFieldName.WORKPLACE_COUNTRY_CODE1.getValue(),String.valueOf(obtainCountryByValue(country).getIsoNum()).substring(0,1));
				setPdfFieldValue(IndefiniteCommonFieldName.WORKPLACE_COUNTRY_CODE2.getValue(),String.valueOf(obtainCountryByValue(country).getIsoNum()).substring(1,2));
				setPdfFieldValue(IndefiniteCommonFieldName.WORKPLACE_COUNTRY_CODE3.getValue(),String.valueOf(obtainCountryByValue(country).getIsoNum()).substring(2,3));
			} catch (StringIndexOutOfBoundsException aie) {
				// do nothing
			} catch (NullPointerException npe) {
				// do nothing
			}
			try {
				RegistryAddress address = contract.getWorkPlace().getAddress();
				ResourceBundle bundle = ResourceBundle.getBundle("com.esferalia.aon.payroll.i18n.municipalities");
				setPdfFieldValue(IndefiniteCommonFieldName.WORKPLACE_MUNICIPALITY.getValue(),bundle.getString(address.getMunicipalityCode()));
				setPdfFieldValue(IndefiniteCommonFieldName.WORKPLACE_MUNICIPALITY_CODE1.getValue(),address.getMunicipalityCode().substring(0, 1));
				setPdfFieldValue(IndefiniteCommonFieldName.WORKPLACE_MUNICIPALITY_CODE2.getValue(),address.getMunicipalityCode().substring(1, 2));
				setPdfFieldValue(IndefiniteCommonFieldName.WORKPLACE_MUNICIPALITY_CODE3.getValue(),address.getMunicipalityCode().substring(2, 3));
				setPdfFieldValue(IndefiniteCommonFieldName.WORKPLACE_MUNICIPALITY_CODE4.getValue(),address.getMunicipalityCode().substring(3, 4));
				setPdfFieldValue(IndefiniteCommonFieldName.WORKPLACE_MUNICIPALITY_CODE5.getValue(),address.getMunicipalityCode().substring(4, 5));
			} catch (StringIndexOutOfBoundsException aie) {
				// do nothing
			} catch (NullPointerException npe) {
				// do nothing
			}
			/*
			 * Contract employee fields
			 */
			setPdfFieldValue(IndefiniteCommonFieldName.EMPLOYEE_NAME.getValue(),contract.getPerson().getFullName());
			setPdfFieldValue(IndefiniteCommonFieldName.EMPLOYEE_NIF.getValue(),contract.getPerson().getRegistry().getDocument());
			if(contract.getPerson().getBirthDate()!=null){
				SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
				setPdfFieldValue(IndefiniteCommonFieldName.EMPLOYEE_BIRTH_DATE.getValue(),formatter.format(contract.getPerson().getBirthDate()));
			}
			setPdfFieldValue(IndefiniteCommonFieldName.EMPLOYEE_NSS.getValue(),contract.getPerson().getSocialSecurityNumber());
			if(contrata!=null && contrata.getNivelFormativo()!=null){
				setPdfFieldValue(IndefiniteCommonFieldName.EMPLOYEE_FORMATION_LEVEL.getValue(),contrata.getNivelFormativo().getDescription());
				setPdfFieldValue(IndefiniteCommonFieldName.EMPLOYEE_FORMATION_CODE1.getValue(),contrata.getNivelFormativo().getCode().substring(0, 1));
				setPdfFieldValue(IndefiniteCommonFieldName.EMPLOYEE_FORMATION_CODE2.getValue(),contrata.getNivelFormativo().getCode().substring(1, 2));
			}
			try {
				setPdfFieldValue(IndefiniteCommonFieldName.EMPLOYEE_COUNTRY.getValue(),String.valueOf(contract.getPerson().getRegistry().getNationality().getName(getLocale())));
				setPdfFieldValue(IndefiniteCommonFieldName.EMPLOYEE_COUNTRY_CODE1.getValue(),String.valueOf(contract.getPerson().getRegistry().getNationality().getIsoNum()).substring(0,1));
				setPdfFieldValue(IndefiniteCommonFieldName.EMPLOYEE_COUNTRY_CODE2.getValue(),String.valueOf(contract.getPerson().getRegistry().getNationality().getIsoNum()).substring(1,2));
				setPdfFieldValue(IndefiniteCommonFieldName.EMPLOYEE_COUNTRY_CODE3.getValue(),String.valueOf(contract.getPerson().getRegistry().getNationality().getIsoNum()).substring(2,3));
			} catch (StringIndexOutOfBoundsException aie) {
				// do nothing
			} catch (NullPointerException npe) {
				// do nothing
			}
			try {
				RegistryAddress address = contract.getPerson().getRegistry().getDefaultAddress();
				ResourceBundle bundle = ResourceBundle.getBundle("com.esferalia.aon.payroll.i18n.municipalities");
				setPdfFieldValue(IndefiniteCommonFieldName.EMPLOYEE_ADDRESS_MUNICIPALITY.getValue(),bundle.getString(address.getMunicipalityCode()));
				setPdfFieldValue(IndefiniteCommonFieldName.EMPLOYEE_ADDRESS_MUNICIPALITY_CODE1.getValue(),address.getMunicipalityCode().substring(0, 1));
				setPdfFieldValue(IndefiniteCommonFieldName.EMPLOYEE_ADDRESS_MUNICIPALITY_CODE2.getValue(),address.getMunicipalityCode().substring(1, 2));
				setPdfFieldValue(IndefiniteCommonFieldName.EMPLOYEE_ADDRESS_MUNICIPALITY_CODE3.getValue(),address.getMunicipalityCode().substring(2, 3));
				setPdfFieldValue(IndefiniteCommonFieldName.EMPLOYEE_ADDRESS_MUNICIPALITY_CODE4.getValue(),address.getMunicipalityCode().substring(3, 4));
				setPdfFieldValue(IndefiniteCommonFieldName.EMPLOYEE_ADDRESS_MUNICIPALITY_CODE5.getValue(),address.getMunicipalityCode().substring(4, 5));
			} catch (StringIndexOutOfBoundsException aie) {
				// do nothing
			} catch (NullPointerException npe) {
				// do nothing
			}
			try {
				GeoZone country = obtainCountry(contract.getPerson().getRegistry().getDefaultAddress().getGeozone());
				setPdfFieldValue(IndefiniteCommonFieldName.EMPLOYEE_ADDRESS_COUNTRY.getValue(),country.getName());
				setPdfFieldValue(IndefiniteCommonFieldName.EMPLOYEE_ADDRESS_COUNTRY_CODE1.getValue(),String.valueOf(obtainCountryByValue(country).getIsoNum()).substring(0,1));
				setPdfFieldValue(IndefiniteCommonFieldName.EMPLOYEE_ADDRESS_COUNTRY_CODE2.getValue(),String.valueOf(obtainCountryByValue(country).getIsoNum()).substring(1,2));
				setPdfFieldValue(IndefiniteCommonFieldName.EMPLOYEE_ADDRESS_COUNTRY_CODE3.getValue(),String.valueOf(obtainCountryByValue(country).getIsoNum()).substring(2,3));
			} catch (StringIndexOutOfBoundsException aie) {
				// do nothing
			} catch (NullPointerException npe) {
				// do nothing
			}
			try {
				setPdfFieldValue(IndefiniteCommonFieldName.LEGAL_REPRESENTATIVE_NAME.getValue(),"");
				setPdfFieldValue(IndefiniteCommonFieldName.LEGAL_REPRESENTATIVE_NIF.getValue(),"");
				setPdfFieldValue(IndefiniteCommonFieldName.LEGAL_REPRESENTATIVE_CHARGE.getValue(),"");
			} catch (StringIndexOutOfBoundsException aie) {
				// do nothing
			} catch (NullPointerException npe) {
				// do nothing
			}
			
			/*
			 * Contract page 1
			 */
			setPdfFieldValue(IndefiniteCommonFieldName.PROFESSION.getValue(), contrata.getCno().getTitle());
			setPdfFieldValue(IndefiniteCommonFieldName.CATEGORY.getValue(), contract.getCategoryDescription());
			setPdfFieldValue(IndefiniteCommonFieldName.WORKPLACE_FULL_ADDRESS.getValue(), null);
			setPdfFieldValue(IndefiniteCommonFieldName.WORKPLACE_FULL_ADDRESS_MORE.getValue(), contract.getWorkPlace().getAddress().getFullAddress()+", "+contract.getWorkPlace().getAddress().getLocation());
			// setPdfFieldValue(IndefiniteCommonFieldName.DISTANCE_WORKING.getValue(), null);
			// setPdfFieldValue(IndefiniteCommonFieldName.DISTANCE_WORKING_ADDRESS.getValue(), null);
			
			// setPdfFieldValue(IndefiniteCommonFieldName.DISCONTONUOUS_WORK_DESCRIPTION.getValue(), null);
			// setPdfFieldValue(IndefiniteCommonFieldName.DISCONTONUOUS_WORK_ACTIVITY.getValue(), null);
			// setPdfFieldValue(IndefiniteCommonFieldName.DISCONTONUOUS_WORK_DURATION.getValue(), null);
			// setPdfFieldValue(IndefiniteCommonFieldName.DISCONTONUOUS_WORK_ESTIMATED_DURATION.getValue(), null);
			// setPdfFieldValue(IndefiniteCommonFieldName.DISCONTONUOUS_WORK_AGREEMENT_COLLECTIVE.getValue(), null);
			// setPdfFieldValue(IndefiniteCommonFieldName.DISCONTONUOUS_WORK_ESTIMATED_JOURNAL_HOURS.getValue(), null);
			// setPdfFieldValue(IndefiniteCommonFieldName.DISCONTONUOUS_WORK_ESTIMATED_JOURNAL_PERIOD.getValue(), null);
			// setPdfFieldValue(IndefiniteCommonFieldName.DISCONTONUOUS_WORK_ESTIMATED_SCHEDULE.getValue(), null);
			// setPdfFieldValue(IndefiniteCommonFieldName.DISCONTINUOUS_AGREEMENT_COLLECTIVE_YES.getValue(), null);
			// setPdfFieldValue(IndefiniteCommonFieldName.DISCONTINUOUS_AGREEMENT_COLLECTIVE_NO.getValue(), null);
			// setPdfFieldValue(IndefiniteCommonFieldName.**UNKNOWN**.getValue(), null);
			
			if(code == ContractCode.C100){
				setPdfFieldValue(IndefiniteCommonFieldName.FULL_TIME.getValue(), "true");
				if(StringUtils.isNotBlank(getContractInfoMap(contract).get(IndefiniteCommonFieldName.FULL_TIME_WEEK_HOURS.toString()))){
					setPdfFieldValue(IndefiniteCommonFieldName.FULL_TIME_WEEK_HOURS.getValue(), getContractInfoMap(contract).get(IndefiniteCommonFieldName.FULL_TIME_WEEK_HOURS.toString()));
				}
				if(StringUtils.isNotBlank(getContractInfoMap(contract).get(IndefiniteCommonFieldName.FULL_TIME_START_TIME.toString()))){
					setPdfFieldValue(IndefiniteCommonFieldName.FULL_TIME_START_TIME.getValue(), getContractInfoMap(contract).get(IndefiniteCommonFieldName.FULL_TIME_START_TIME.toString()));
				}
				if(StringUtils.isNotBlank(getContractInfoMap(contract).get(IndefiniteCommonFieldName.FULL_TIME_END_TIME.toString()))){
					setPdfFieldValue(IndefiniteCommonFieldName.FULL_TIME_END_TIME.getValue(), getContractInfoMap(contract).get(IndefiniteCommonFieldName.FULL_TIME_END_TIME.toString()));
				}
			} else if(code == ContractCode.C200){
				setPdfFieldValue(IndefiniteCommonFieldName.PARTIALLY_TIME.getValue(), "true");
				setPdfFieldValue(IndefiniteCommonFieldName.PARTIALLY_TIME_HOURS.getValue(), String.valueOf(Integer.parseInt(contrata.getHorasJornada())));
				if(contrata.getTipoJornada()==TEQPTIEM.TEQPTIEM_D){
					setPdfFieldValue(IndefiniteCommonFieldName.PARTIALLY_TIME_DAYLY.getValue(), "true");
				} else if(contrata.getTipoJornada()==TEQPTIEM.TEQPTIEM_S){
					setPdfFieldValue(IndefiniteCommonFieldName.PARTIALLY_TIME_WEEKLY.getValue(), "true");
				} else if(contrata.getTipoJornada()==TEQPTIEM.TEQPTIEM_M){
					setPdfFieldValue(IndefiniteCommonFieldName.PARTIALLY_TIME_MONTHLY.getValue(), "true");
				} else if(contrata.getTipoJornada()==TEQPTIEM.TEQPTIEM_A){
					setPdfFieldValue(IndefiniteCommonFieldName.PARTIALLY_TIME_YEARLY.getValue(), "true");
				}
				// setPdfFieldValue(IndefiniteCommonFieldName.HOURS.getValue(), "");
				// setPdfFieldValue(IndefiniteCommonFieldName.COMPLEMENTARY_HOURS_YES.getValue(), "");
				// setPdfFieldValue(IndefiniteCommonFieldName.COMPLEMENTARY_HOURS_NO.getValue(), "");
			}
			
			/*
			 * Contract page 2
			 */
			dateFormatter.applyPattern("dd/MM/yyyy");
			setPdfFieldValue(IndefiniteCommonFieldName.START_DATE.getValue(), dateFormatter.format(contract.getStartDate()));
			
			if(StringUtils.isNotBlank(getContractInfoMap(contract).get(IndefiniteCommonFieldName.TRIAL_DURATION.toString()))){
				setPdfFieldValue(IndefiniteCommonFieldName.TRIAL_DURATION.getValue(), getContractInfoMap(contract).get(IndefiniteCommonFieldName.TRIAL_DURATION.toString()));
			}
			
			if(contrata.isReliefData()){
				setPdfFieldValue(IndefiniteCommonFieldName.RELIEF_CONTRACT_YES.getValue(), "true");
			} else {
				setPdfFieldValue(IndefiniteCommonFieldName.RELIEF_CONTRACT_NO.getValue(), "true");
			}
			// setPdfFieldValue(IndefiniteCommonFieldName.RELIEF_CONTRACT_UNEMPLOYED_IN_SEPE_MUNICIPALITY1.getValue(), "");
			// setPdfFieldValue(IndefiniteCommonFieldName.RELIEF_CONTRACT_UNEMPLOYED_IN_SEPE_MUNICIPALITY2.getValue(), "");
			// setPdfFieldValue(IndefiniteCommonFieldName.RELIEF_CONTRACT_PARTIALLY_CONTRACT_SEPE_MUNICIPALITY.getValue(), "");
			// setPdfFieldValue(IndefiniteCommonFieldName.RELIEF_CONTRACT_PARTIALLY_CONTRACT_NUMBER.getValue(), "");
			// setPdfFieldValue(IndefiniteCommonFieldName.RELIEF_CONTRACT_PARTIALLY_CONTRACT_DATE.getValue(), "");
			
			if(StringUtils.isNotBlank(getContractInfoMap(contract).get(IndefiniteCommonFieldName.SALARY_AMOUNT.toString()))){
				setPdfFieldValue(IndefiniteCommonFieldName.SALARY_AMOUNT.getValue(), getContractInfoMap(contract).get(IndefiniteCommonFieldName.SALARY_AMOUNT.toString()));
			}
			if(StringUtils.isNotBlank(getContractInfoMap(contract).get(IndefiniteCommonFieldName.SALARY_PERIOD.toString()))){
				setPdfFieldValue(IndefiniteCommonFieldName.SALARY_PERIOD.getValue(), getContractInfoMap(contract).get(IndefiniteCommonFieldName.SALARY_PERIOD.toString()));
			}
			if(StringUtils.isNotBlank(getContractInfoMap(contract).get(IndefiniteCommonFieldName.SALARY_CONCEPT.toString()))){
				setPdfFieldValue(IndefiniteCommonFieldName.SALARY_CONCEPT.getValue(), getContractInfoMap(contract).get(IndefiniteCommonFieldName.SALARY_CONCEPT.toString()));
			}
			if(StringUtils.isNotBlank(getContractInfoMap(contract).get(IndefiniteCommonFieldName.HOLIDAYS.toString()))){
				setPdfFieldValue(IndefiniteCommonFieldName.HOLIDAYS.getValue(), getContractInfoMap(contract).get(IndefiniteCommonFieldName.HOLIDAYS.toString()));
			}
			if(contract.getAgreementLevelCategory()!=null && contract.getAgreementLevelCategory().getId()!=null){
				setPdfFieldValue(IndefiniteCommonFieldName.AGREEMENT_COLLECTIVE.getValue(), contract.getAgreementLevelCategory().getLevel().getAgreement().getDescription());
			}
			if(StringUtils.isNotBlank(getContractInfoMap(contract).get(IndefiniteCommonFieldName.SEPE_MUNICIPALITY.toString()))){
				setPdfFieldValue(IndefiniteCommonFieldName.SEPE_MUNICIPALITY.getValue(), getContractInfoMap(contract).get(IndefiniteCommonFieldName.SEPE_MUNICIPALITY.toString()));
			}
			
			
			/*
			 *  OPTIONS PAGE
			 */
			modelOption = ModelOption.valueOf(getContractInfoMap(contract).get(ContractVariable.CONTRACT_MODEL_OPTION.getValue()));
			
			if(modelOption == ModelOption.INDEFINITE_OPT1){
				setPdfFieldValue(IndefiniteOptionFieldName.MAIN_OPT1_CHECK.getValue(),"true");
				setPdfFieldValue(IndefiniteOptionFieldName.OPT1_OPTION_CHECK.getValue(),"true");
				if(code == ContractCode.C100){
					setPdfFieldValue(IndefiniteOptionFieldName.OPT1_TC2_100.getValue(),"true");
				} else if(code == ContractCode.C200){
					setPdfFieldValue(IndefiniteOptionFieldName.OPT1_TC2_200.getValue(),"true");
				} else if(code == ContractCode.C300){
					setPdfFieldValue(IndefiniteOptionFieldName.OPT1_TC2_300.getValue(),"true");
				}
			} else if(modelOption == ModelOption.INDEFINITE_OPT5){
				setPdfFieldValue(IndefiniteOptionFieldName.MAIN_OPT5_CHECK.getValue(),"true");
				setPdfFieldValue(IndefiniteOptionFieldName.OPT5_OPTION_CHECK.getValue(),"true");
				if(code == ContractCode.C100){
					setPdfFieldValue(IndefiniteOptionFieldName.OPT5_FULL_TIME.getValue(),"true");
					setPdfFieldValue(IndefiniteOptionFieldName.OPT5_TC2_100.getValue(),"true");
				} else if(code == ContractCode.C150){
					setPdfFieldValue(IndefiniteOptionFieldName.OPT5_FULL_TIME.getValue(),"true");
					setPdfFieldValue(IndefiniteOptionFieldName.OPT5_TC2_150.getValue(),"true");
				} else if(code == ContractCode.C200){
					setPdfFieldValue(IndefiniteOptionFieldName.OPT5_PARTIALLY_TIME.getValue(),"true");
					setPdfFieldValue(IndefiniteOptionFieldName.OPT5_TC2_200.getValue(),"true");
				} else if(code == ContractCode.C250){
					setPdfFieldValue(IndefiniteOptionFieldName.OPT5_PARTIALLY_TIME.getValue(),"true");
					setPdfFieldValue(IndefiniteOptionFieldName.OPT5_TC2_250.getValue(),"true");
				} else if(code == ContractCode.C300){
					setPdfFieldValue(IndefiniteOptionFieldName.OPT5_DISCONTINUOUS_TIME.getValue(),"true");
					setPdfFieldValue(IndefiniteOptionFieldName.OPT5_TC2_300.getValue(),"true");
				} else if(code == ContractCode.C350){
					setPdfFieldValue(IndefiniteOptionFieldName.OPT5_DISCONTINUOUS_TIME.getValue(),"true");
					setPdfFieldValue(IndefiniteOptionFieldName.OPT5_TC2_350.getValue(),"true");
				}
				// TODO: complete this
				setPdfFieldValue(IndefiniteOptionFieldName.OPT5_BONUS_ART4_RDL3_2012_YES.getValue(),"");
				setPdfFieldValue(IndefiniteOptionFieldName.OPT5_BONUS_ART4_RDL3_2012_NO.getValue(),"");
				setPdfFieldValue(IndefiniteOptionFieldName.OPT5_REGISTERED_IN_EMPLOYMENT_OFFICE.getValue(),"");
				setPdfFieldValue(IndefiniteOptionFieldName.OPT5_UNEMPLOYED_BT_16_30.getValue(),"");
				setPdfFieldValue(IndefiniteOptionFieldName.OPT5_UNEMPLOYED_BT_16_30_JUNIOR.getValue(),"");
				setPdfFieldValue(IndefiniteOptionFieldName.OPT5_UNEMPLOYED_BT_16_30_FEMALE.getValue(),"");
				setPdfFieldValue(IndefiniteOptionFieldName.OPT5_UNEMPLOYED_GT_45.getValue(),"");
				setPdfFieldValue(IndefiniteOptionFieldName.OPT5_UNEMPLOYED_GT_45_MALE.getValue(),"");
				setPdfFieldValue(IndefiniteOptionFieldName.OPT5_UNEMPLOYED_GT_45_FEMALE.getValue(),"");
				setPdfFieldValue(IndefiniteOptionFieldName.OPT5_UNEMPLOYED_WITH_3_MONTH_BENEFIT.getValue(),"");
				setPdfFieldValue(IndefiniteOptionFieldName.OPT5_FIRST_EMPLOYEE_AND_LT_30.getValue(),"");
			} else if(modelOption == ModelOption.INDEFINITE_OPT6){
				setPdfFieldValue(IndefiniteOptionFieldName.MAIN_OPT6_CHECK.getValue(),"true");
				setPdfFieldValue(IndefiniteOptionFieldName.OPT6_OPTION_CHECK.getValue(),"true");
				if(code == ContractCode.C100){
					setPdfFieldValue(IndefiniteOptionFieldName.OPT6_TC2_100.getValue(),"true");
				} else if(code == ContractCode.C200){
					setPdfFieldValue(IndefiniteOptionFieldName.OPT6_TC2_200.getValue(),"true");
				}
				// TODO: complete this
				setPdfFieldValue(IndefiniteOptionFieldName.OPT6_LT_30_EMPLOYEE.getValue(),"");
				setPdfFieldValue(IndefiniteOptionFieldName.OPT6_LT_35_EMPLOYEE_AND_HANDICAP_GTE_33.getValue(),"");
				setPdfFieldValue(IndefiniteOptionFieldName.OPT6_AGREEMENT_COLLECTIVE1.getValue(),"");
				setPdfFieldValue(IndefiniteOptionFieldName.OPT6_AGREEMENT_COLLECTIVE2.getValue(),"");
			} else if(modelOption == ModelOption.INDEFINITE_OPT7){
				setPdfFieldValue(IndefiniteOptionFieldName.MAIN_OPT7_CHECK.getValue(),"true");
				setPdfFieldValue(IndefiniteOptionFieldName.OPT7_OPTION_CHECK.getValue(),"true");
				if(code == ContractCode.C100){
					setPdfFieldValue(IndefiniteOptionFieldName.OPT7_TC2_100.getValue(),"true");
				} else if(code == ContractCode.C200){
					setPdfFieldValue(IndefiniteOptionFieldName.OPT7_TC2_200.getValue(),"true");
				} else if(code == ContractCode.C300){
					setPdfFieldValue(IndefiniteOptionFieldName.OPT7_TC2_300.getValue(),"true");
				}
				// TODO: complete this
				setPdfFieldValue(IndefiniteOptionFieldName.OPT7_UNEMPLOYED_DURING_12_MONTH.getValue(),"");
				setPdfFieldValue(IndefiniteOptionFieldName.OPT7_PROFFESIONAL_RECUALIFICATION.getValue(),"");
				setPdfFieldValue(IndefiniteOptionFieldName.OPT7_AGREEMENT_COLLECTIVE1.getValue(),"");
				setPdfFieldValue(IndefiniteOptionFieldName.OPT7_AGREEMENT_COLLECTIVE2.getValue(),"");
			} else if(modelOption == ModelOption.INDEFINITE_OPT15){
				setPdfFieldValue(IndefiniteOptionFieldName.MAIN_OPT15_CHECK.getValue(),"true");
				setPdfFieldValue(IndefiniteOptionFieldName.OPT15_OPTION_CHECK.getValue(),"true");
				if(code == ContractCode.C100){
					setPdfFieldValue(IndefiniteOptionFieldName.OPT15_TC2_100.getValue(),"true");
				} else if(code == ContractCode.C200){
					setPdfFieldValue(IndefiniteOptionFieldName.OPT15_TC2_200.getValue(),"true");
				}
				// TODO: complete this
				setPdfFieldValue(IndefiniteOptionFieldName.OPT15_ONSITE_HOURS_YES.getValue(),"");
				setPdfFieldValue(IndefiniteOptionFieldName.OPT15_ONSITE_HOURS_NO.getValue(),"");
				setPdfFieldValue(IndefiniteOptionFieldName.OPT15_ONSITE_WEEK_HOURS.getValue(),"");
				setPdfFieldValue(IndefiniteOptionFieldName.OPT15_ONSITE_HOURS_DISTRIBUTION.getValue(),"");
				setPdfFieldValue(IndefiniteOptionFieldName.OPT15_SALARY_OPT1.getValue(),"");
				setPdfFieldValue(IndefiniteOptionFieldName.OPT15_SALARY_OPT2.getValue(),"");
				setPdfFieldValue(IndefiniteOptionFieldName.OPT15_SALARY_OPT3.getValue(),"");
				setPdfFieldValue(IndefiniteOptionFieldName.OPT15_OVERNIGHT_YES.getValue(),"");
				setPdfFieldValue(IndefiniteOptionFieldName.OPT15_OVERNIGHT_NO.getValue(),"");
				setPdfFieldValue(IndefiniteOptionFieldName.OPT15_OVERNIGHT_WEEK_DAYS.getValue(),"");
			} else if(modelOption == ModelOption.INDEFINITE_OPT17){
				setPdfFieldValue(IndefiniteOptionFieldName.MAIN_OPT17_CHECK.getValue(),"true");
				setPdfFieldValue(IndefiniteOptionFieldName.OPT17_OPTION_CHECK.getValue(),"true");
				String subsidized = getContractDataMap(contract).get(ContextVariable.SUBSIDIZED.getName());
				if(code == ContractCode.C109 || code == ContractCode.C139 | code == ContractCode.C189){
					if(code == ContractCode.C139){
						setPdfFieldValue(IndefiniteOptionFieldName.OPT17_FULL_TIME.getValue(),"true");
						setPdfFieldValue(IndefiniteOptionFieldName.OPT17_TC2_139.getValue(),"true");
					} else if(code == ContractCode.C109){
						setPdfFieldValue(IndefiniteOptionFieldName.OPT17_FULL_TIME.getValue(),"true");
						setPdfFieldValue(IndefiniteOptionFieldName.OPT17_TC2_109.getValue(),"true");
					} else if(code == ContractCode.C189){
						setPdfFieldValue(IndefiniteOptionFieldName.OPT17_FULL_TIME.getValue(),"true");
						setPdfFieldValue(IndefiniteOptionFieldName.OPT17_TC2_189.getValue(),"true");
					} 
					if( Boolean.parseBoolean(subsidized) ){
						setPdfFieldValue(IndefiniteOptionFieldName.OPT17_FULL_TIME_QUOTE_BONUS_YES.getValue(),"true");
					} else {
						setPdfFieldValue(IndefiniteOptionFieldName.OPT17_FULL_TIME_QUOTE_BONUS_NO.getValue(),"true");
					}
				} else if(code == ContractCode.C209 || code == ContractCode.C239 || code == ContractCode.C289){
					if(code == ContractCode.C239){
						setPdfFieldValue(IndefiniteOptionFieldName.OPT17_PARTIALLY_TIME.getValue(),"true");
						setPdfFieldValue(IndefiniteOptionFieldName.OPT17_TC2_239.getValue(),"true");
					} else if(code == ContractCode.C209){
						setPdfFieldValue(IndefiniteOptionFieldName.OPT17_PARTIALLY_TIME.getValue(),"true");
						setPdfFieldValue(IndefiniteOptionFieldName.OPT17_TC2_209.getValue(),"true");
					} else if(code == ContractCode.C289){
						setPdfFieldValue(IndefiniteOptionFieldName.OPT17_PARTIALLY_TIME.getValue(),"true");
						setPdfFieldValue(IndefiniteOptionFieldName.OPT17_TC2_289.getValue(),"true");
					} 
					if( Boolean.parseBoolean(subsidized) ){
						setPdfFieldValue(IndefiniteOptionFieldName.OPT17_PARTIALLY_TIME_QUOTE_BONUS_YES.getValue(),"true");
					} else {
						setPdfFieldValue(IndefiniteOptionFieldName.OPT17_PARTIALLY_TIME_QUOTE_BONUS_NO.getValue(),"true");
					}
				} else if(code == ContractCode.C309 || code == ContractCode.C339 || code == ContractCode.C389){
					if(code == ContractCode.C339){
						setPdfFieldValue(IndefiniteOptionFieldName.OPT17_DISCONTINUOUS_TIME.getValue(),"true");
						setPdfFieldValue(IndefiniteOptionFieldName.OPT17_TC2_339.getValue(),"true");
					} else if(code == ContractCode.C309){
						setPdfFieldValue(IndefiniteOptionFieldName.OPT17_DISCONTINUOUS_TIME.getValue(),"true");
						setPdfFieldValue(IndefiniteOptionFieldName.OPT17_TC2_309.getValue(),"true");
					} else if(code == ContractCode.C389){
						setPdfFieldValue(IndefiniteOptionFieldName.OPT17_DISCONTINUOUS_TIME.getValue(),"true");
						setPdfFieldValue(IndefiniteOptionFieldName.OPT17_TC2_389.getValue(),"true");
					}
					if( Boolean.parseBoolean(subsidized) ){
						setPdfFieldValue(IndefiniteOptionFieldName.OPT17_DISCONTINUOUS_TIME_QUOTE_BONUS_YES.getValue(),"true");
					} else {
						setPdfFieldValue(IndefiniteOptionFieldName.OPT17_DISCONTINUOUS_TIME_QUOTE_BONUS_NO.getValue(),"true");
					}
				}
				// TODO: complete this
				setPdfFieldValue(IndefiniteOptionFieldName.OPT17_SEPE_MUNICIPALITY.getValue(),"");
				setPdfFieldValue(IndefiniteOptionFieldName.OPT17_TRANSFORM_DATE.getValue(),"");
				setPdfFieldValue(IndefiniteOptionFieldName.OPT17_IS_FULL_TIME.getValue(),"");
				setPdfFieldValue(IndefiniteOptionFieldName.OPT17_IS_FULL_TIME_DISCONTINUOUS.getValue(),"");
				setPdfFieldValue(IndefiniteOptionFieldName.OPT17_SOURCE_CONTRACT.getValue(),"");
				setPdfFieldValue(IndefiniteOptionFieldName.OPT17_SOURCE_CONTRACT_START_DATE.getValue(),"");
				setPdfFieldValue(IndefiniteOptionFieldName.OPT17_SOURCE_CONTRACT_SEPE_MUNICIPALITY.getValue(),"");
				setPdfFieldValue(IndefiniteOptionFieldName.OPT17_SOURCE_CONTRACT_SEPE_DATE.getValue(),"");
				setPdfFieldValue(IndefiniteOptionFieldName.OPT17_SOURCE_CONTRACT_SEPE_ID.getValue(),"");
			}
			
		} catch (IOException e) {
			// do nothing
		} catch (ManagerBeanException e) {
			// do nothing
		}
	}

	/*
	 * INNER CLASSES
	 */
	public enum IndefiniteCommonFieldName implements IContractFieldName{
		
		/* 
		 * Contract enterprise fields
		 */
		ENTERPRISE_CIF("Texto3",Boolean.FALSE),
		ENTERPRISE_DIR_STAFF_NAME("Texto4",Boolean.FALSE),
		ENTERPRISE_DIR_STAFF_NIF("Texto5",Boolean.FALSE),
		ENTERPRISE_DIR_STAFF_CHARGE("Texto6",Boolean.FALSE),
		ENTERPRISE_NAME("Texto7",Boolean.FALSE),
		ENTERPRISE_ADDRESS("Texto8",Boolean.FALSE),
		ENTERPRISE_COUNTRY("Texto9",Boolean.FALSE),
		ENTERPRISE_COUNTRY_CODE1("Texto10",Boolean.FALSE),
		ENTERPRISE_COUNTRY_CODE2("Texto11",Boolean.FALSE),
		ENTERPRISE_COUNTRY_CODE3("Texto12",Boolean.FALSE),
		ENTERPRISE_MUNICIPALITY("Texto13",Boolean.FALSE),
		ENTERPRISE_MUNICIPALITY_CODE1("Texto14",Boolean.FALSE),
		ENTERPRISE_MUNICIPALITY_CODE2("Texto15",Boolean.FALSE),
		ENTERPRISE_MUNICIPALITY_CODE3("Texto16",Boolean.FALSE),
		ENTERPRISE_MUNICIPALITY_CODE4("Texto17",Boolean.FALSE),
		ENTERPRISE_MUNICIPALITY_CODE5("Texto18",Boolean.FALSE),
		ENTERPRISE_ZIP1("Texto19",Boolean.FALSE),
		ENTERPRISE_ZIP2("Texto20",Boolean.FALSE),
		ENTERPRISE_ZIP3("Texto21",Boolean.FALSE),
		ENTERPRISE_ZIP4("Texto22",Boolean.FALSE),
		ENTERPRISE_ZIP5("Texto23",Boolean.FALSE),
		
		/* 
		 * Contract ccc fields
		 */
		CCC_REG1("Texto24",Boolean.FALSE),
		CCC_REG2("Texto25",Boolean.FALSE),
		CCC_REG3("Texto26",Boolean.FALSE),
		CCC_REG4("Texto27",Boolean.FALSE),
		CCC_PROV1("Texto28",Boolean.FALSE),
		CCC_PROV2("Texto29",Boolean.FALSE),
		CCC_NISS("Texto30",Boolean.FALSE),
		CCC_CONTROL_DIGIT1("Texto31",Boolean.FALSE),
		CCC_CONTROL_DIGIT2("Texto32",Boolean.FALSE),
		CCC_ACTIVITY("Texto33",Boolean.FALSE),
		CCC_ACTIVITY_CODE1("Texto34",Boolean.FALSE),
		CCC_ACTIVITY_CODE2("Texto35",Boolean.FALSE),
		
		/* 
		 * Contract workplace fields
		 */
		WORKPLACE_COUNTRY("Texto36",Boolean.FALSE),
		WORKPLACE_COUNTRY_CODE1("Texto37",Boolean.FALSE),
		WORKPLACE_COUNTRY_CODE2("Texto38",Boolean.FALSE),
		WORKPLACE_COUNTRY_CODE3("Texto39",Boolean.FALSE),
		WORKPLACE_MUNICIPALITY("Texto40",Boolean.FALSE),
		WORKPLACE_MUNICIPALITY_CODE1("Texto41",Boolean.FALSE),
		WORKPLACE_MUNICIPALITY_CODE2("Texto42",Boolean.FALSE),
		WORKPLACE_MUNICIPALITY_CODE3("Texto43",Boolean.FALSE),
		WORKPLACE_MUNICIPALITY_CODE4("Texto44",Boolean.FALSE),
		WORKPLACE_MUNICIPALITY_CODE5("Texto45",Boolean.FALSE),
		
		/*
		 * Contract employee fields
		 */
		EMPLOYEE_NAME("Texto46",Boolean.FALSE),
		EMPLOYEE_NIF("Texto47",Boolean.FALSE),
		EMPLOYEE_BIRTH_DATE("Texto48",Boolean.FALSE),
		EMPLOYEE_NSS("Texto49",Boolean.FALSE),
		EMPLOYEE_FORMATION_LEVEL("Texto50",Boolean.FALSE),
		EMPLOYEE_FORMATION_CODE1("Texto51",Boolean.FALSE),
		EMPLOYEE_FORMATION_CODE2("Texto52",Boolean.FALSE),
		EMPLOYEE_COUNTRY("Texto53",Boolean.FALSE),
		EMPLOYEE_COUNTRY_CODE1("Texto54",Boolean.FALSE),
		EMPLOYEE_COUNTRY_CODE2("Texto55",Boolean.FALSE),
		EMPLOYEE_COUNTRY_CODE3("Texto56",Boolean.FALSE),
		EMPLOYEE_ADDRESS_MUNICIPALITY("Texto57",Boolean.FALSE),
		EMPLOYEE_ADDRESS_MUNICIPALITY_CODE1("Texto58",Boolean.FALSE),
		EMPLOYEE_ADDRESS_MUNICIPALITY_CODE2("Texto59",Boolean.FALSE),
		EMPLOYEE_ADDRESS_MUNICIPALITY_CODE3("Texto60",Boolean.FALSE),
		EMPLOYEE_ADDRESS_MUNICIPALITY_CODE4("Texto61",Boolean.FALSE),
		EMPLOYEE_ADDRESS_MUNICIPALITY_CODE5("Texto62",Boolean.FALSE),
		EMPLOYEE_ADDRESS_COUNTRY("Texto63",Boolean.FALSE),
		EMPLOYEE_ADDRESS_COUNTRY_CODE1("Texto64",Boolean.FALSE),
		EMPLOYEE_ADDRESS_COUNTRY_CODE2("Texto65",Boolean.FALSE),
		EMPLOYEE_ADDRESS_COUNTRY_CODE3("Texto66",Boolean.FALSE),

		/*
		 * LEGAL REPRESENTATION
		 */
		LEGAL_REPRESENTATIVE_NAME("Texto67",Boolean.FALSE),
		LEGAL_REPRESENTATIVE_NIF("Texto68",Boolean.FALSE),
		LEGAL_REPRESENTATIVE_CHARGE("Texto69",Boolean.FALSE),
		
		/*
		 * Contract page 1
		 */
		PROFESSION("Texto71",Boolean.FALSE),
		CATEGORY("Texto72",Boolean.FALSE),
		// FUNCTIONS
		WORKPLACE_FULL_ADDRESS("Texto75",Boolean.FALSE),
		WORKPLACE_FULL_ADDRESS_MORE("Texto73",Boolean.FALSE),
		// DISTANCE_WORKING
		// DISTANCE_WORKING_ADDRESS
		
		// DISCONTONUOUS_WORK_DESCRIPTION
		// DISCONTONUOUS_WORK_ACTIVITY
		// DISCONTONUOUS_WORK_DURATION
		// DISCONTONUOUS_WORK_ESTIMATED_DURATION
		// DISCONTONUOUS_WORK_AGREEMENT_COLLECTIVE
		// DISCONTONUOUS_WORK_ESTIMATED_JOURNAL_HOURS
		// DISCONTONUOUS_WORK_ESTIMATED_JOURNAL_PERIOD
		// DISCONTONUOUS_WORK_ESTIMATED_SCHEDULE
		// DISCONTINUOUS_AGREEMENT_COLLECTIVE_YES
		// DISCONTINUOUS_AGREEMENT_COLLECTIVE_NO
		// **UNKNOWN**
		
		FULL_TIME("Casilla de verificación82",Boolean.FALSE),
		FULL_TIME_WEEK_HOURS("Texto8696",Boolean.TRUE),
		FULL_TIME_START_TIME("Texto8620",Boolean.TRUE),
		FULL_TIME_END_TIME("Texto86",Boolean.TRUE),
		PARTIALLY_TIME("Casilla de verificación83",Boolean.FALSE),
		PARTIALLY_TIME_HOURS("Texto86ññññ",Boolean.FALSE),
		PARTIALLY_TIME_DAYLY("Casilla de verificación84879",Boolean.FALSE),
		PARTIALLY_TIME_WEEKLY("Casilla de verificación84236",Boolean.FALSE),
		PARTIALLY_TIME_MONTHLY("Casilla de verificación84opi",Boolean.FALSE),
		PARTIALLY_TIME_YEARLY("Casilla de verificación85",Boolean.FALSE),
		// HOURS
		// COMPLEMENTARY_HOURS_YES
		// COMPLEMENTARY_HOURS_NO
		
		/*
		 * Contract page 2
		 */
		START_DATE("Texto92",Boolean.FALSE),
		TRIAL_DURATION("Text9107",Boolean.TRUE),

		SALARY_AMOUNT("Texto1",Boolean.TRUE),
		SALARY_PERIOD("Texto2",Boolean.TRUE),
		SALARY_CONCEPT("Texto70",Boolean.TRUE),

		HOLIDAYS("Texto91005500",Boolean.TRUE),

		AGREEMENT_COLLECTIVE("Texto82",Boolean.FALSE),

		RELIEF_CONTRACT_YES("Casilla de verificación8423",Boolean.FALSE),
		RELIEF_CONTRACT_NO("Casilla de verificación84mk",Boolean.FALSE),
		// RELIEF_CONTRACT_UNEMPLOYED_IN_SEPE_MUNICIPALITY1
		// RELIEF_CONTRACT_UNEMPLOYED_IN_SEPE_MUNICIPALITY2
		// RELIEF_CONTRACT_PARTIALLY_CONTRACT_SEPE_MUNICIPALITY
		// RELIEF_CONTRACT_PARTIALLY_CONTRACT_NUMBER
		// RELIEF_CONTRACT_PARTIALLY_CONTRACT_DATE
		
		SEPE_MUNICIPALITY("oecomu",Boolean.TRUE),
		
		;
		
		private String value;
		private boolean overridable;
		
		private IndefiniteCommonFieldName(String value, boolean overridable) {
			this.value = value;
			this.overridable = overridable;
		}
		
		@Override
		public boolean isOverridable(){
			return overridable;
		}
		@Override
		public String getValue() {
			return value;
		}
	}
	
	public enum IndefiniteOptionFieldName implements IContractFieldName, IContractOptionField {
		
		/* Contract PAGE 3 */
		MAIN_OPT1_CHECK("Casilla de verificación871",Boolean.FALSE),
		MAIN_OPT2_CHECK("Casilla de verificación872",Boolean.FALSE),
		MAIN_OPT3_CHECK("Casilla de verificación873",Boolean.FALSE),
		MAIN_OPT4_CHECK("Casilla de verificación874",Boolean.FALSE),
		MAIN_OPT5_CHECK("Casilla de verificación875",Boolean.FALSE),
		MAIN_OPT6_CHECK("Casilla de verificación876",Boolean.FALSE),
		MAIN_OPT7_CHECK("Casilla de verificación877",Boolean.FALSE),
		MAIN_OPT8_CHECK("Casilla de verificación878",Boolean.FALSE),
		MAIN_OPT9_CHECK("Casilla de verificación879",Boolean.FALSE),
		MAIN_OPT10_CHECK("Casilla de verificación8710",Boolean.FALSE),
		MAIN_OPT11_CHECK("Casilla de verificación8711",Boolean.FALSE),
		MAIN_OPT12_CHECK("Casilla de verificación8712",Boolean.FALSE),
		MAIN_OPT13_CHECK("Casilla de verificación8713",Boolean.FALSE),
		MAIN_OPT14_CHECK("Casilla de verificación8714",Boolean.FALSE),
		MAIN_OPT15_CHECK("Casilla de verificación8715",Boolean.FALSE),
		MAIN_OPT16_CHECK("Casilla de verificación7",Boolean.FALSE),
		MAIN_OPT17_CHECK("Casilla de verificación14ddddddddd",Boolean.FALSE),
		
	
		
//		INDEFINIDO ORDINARIO (pag. 4)
		OPT1_OPTION_CHECK("Casilla de verificación8766",Boolean.FALSE),
		OPT1_TC2_100("Casilla de verificación8oooo",Boolean.FALSE),
		OPT1_TC2_200("Casilla de verificación87333",Boolean.FALSE),
		OPT1_TC2_300("Casilla de verificación872369",Boolean.FALSE),
//		DE PERSONAS CON DISCAPACIDAD (pag. 5)
		OPT2_OPTION_CHECK("Casilla de verificación48",Boolean.FALSE),
//		DE PERSONAS CON DISCAPACIDAD EN CENTROS ESPECIALES DE EMPLEO (pag.6)
		OPT3_OPTION_CHECK("Casilla de verificación48",Boolean.FALSE),
//		DE PERSONAS CON DISCAPACIDAD PROCEDENTES DE ENCLAVES LABORALES (pag.7)
		OPT4_OPTION_CHECK("Casilla de verificación48",Boolean.FALSE),
//		DE APOYO A LOS EMPRENDEDORES (pag.8)
		OPT5_OPTION_CHECK("Casilla de verificación84opi369612369",Boolean.FALSE),
		OPT5_FULL_TIME("Casilla de verificación8",Boolean.FALSE),
		OPT5_TC2_100("Casilla de verificación11",Boolean.FALSE),
		OPT5_TC2_150("Casilla de verificación12",Boolean.FALSE),
		OPT5_PARTIALLY_TIME("Casilla de verificación9",Boolean.FALSE),
		OPT5_TC2_200("Casilla de verificación84opi36ñpoi",Boolean.FALSE),
		OPT5_TC2_250("Casilla de verificación84opi3632548",Boolean.FALSE),
		OPT5_DISCONTINUOUS_TIME("Casilla de verificación10",Boolean.FALSE),
		OPT5_TC2_300("Casilla de verificación84opi895",Boolean.FALSE),
		OPT5_TC2_350("Casilla de verificación84opi3692369666",Boolean.FALSE),
		OPT5_BONUS_ART4_RDL3_2012_YES("Casilla de verificación84opi3692668889",Boolean.FALSE),
		OPT5_BONUS_ART4_RDL3_2012_NO("Casilla de verificación84opi698",Boolean.FALSE),
		OPT5_REGISTERED_IN_EMPLOYMENT_OFFICE("Casilla de verificación84opi3pñoium",Boolean.FALSE),
		OPT5_UNEMPLOYED_BT_16_30("Casilla de verificación84opi36998756",Boolean.FALSE),
		OPT5_UNEMPLOYED_BT_16_30_JUNIOR("Casilla de verificación84opi3698715",Boolean.FALSE),
		OPT5_UNEMPLOYED_BT_16_30_FEMALE("Casilla de verificación84opi301258963185",Boolean.FALSE),
		OPT5_UNEMPLOYED_GT_45("Casilla de verificación84opi3987562489",Boolean.FALSE),
		OPT5_UNEMPLOYED_GT_45_MALE("Casilla de verificación84opi3698888887",Boolean.FALSE),
		OPT5_UNEMPLOYED_GT_45_FEMALE("Casilla de verificación84opi3669875658",Boolean.FALSE),
		OPT5_UNEMPLOYED_WITH_3_MONTH_BENEFIT("Casilla de verificación84opi99634582",Boolean.FALSE),
		OPT5_FIRST_EMPLOYEE_AND_LT_30("Casilla de verificación84opi36926897",Boolean.FALSE),
//		DE UN JÓVEN POR MICROEMPRESAS Y EMPRESARIOS AUTÓNOMOS (pag.9)
		OPT6_OPTION_CHECK("Casilla de verificación84opi987",Boolean.FALSE),
		OPT6_TC2_100("Casilla de verificación84opi37523",Boolean.FALSE),
		OPT6_TC2_200("Casilla de verificación84opi3692664",Boolean.FALSE),
		OPT6_LT_30_EMPLOYEE("Casilla de verificación84opi369569",Boolean.FALSE),
		OPT6_LT_35_EMPLOYEE_AND_HANDICAP_GTE_33("Casilla de verificación84opi3663781",Boolean.FALSE),
		OPT6_AGREEMENT_COLLECTIVE1("Texto101",Boolean.FALSE),
		OPT6_AGREEMENT_COLLECTIVE2("Texto102",Boolean.FALSE),
//		DE NUEVO PROYECTO DE EMPRENDIMIENTO JOVEN (pag.10)
		OPT7_OPTION_CHECK("Casilla de verificación84opi36302",Boolean.FALSE),
		OPT7_TC2_100("Casilla de verificación84opi3692874",Boolean.FALSE),
		OPT7_TC2_200("Casilla de verificación84opi369236",Boolean.FALSE),
		OPT7_TC2_300("Casilla de verificación84opi36921234",Boolean.FALSE),
		OPT7_UNEMPLOYED_DURING_12_MONTH("129266666",Boolean.FALSE),
		OPT7_PROFFESIONAL_RECUALIFICATION("Casilla de verificación84opi3692623",Boolean.FALSE),
		OPT7_AGREEMENT_COLLECTIVE1("Texto103",Boolean.FALSE),
		OPT7_AGREEMENT_COLLECTIVE2("Texto104",Boolean.FALSE),
//		A TIEMPO PARCIAL CON VINCULACIÓN FORMATIVA (pag.11)
		OPT8_OPTION_CHECK("Casilla de verificación48",Boolean.FALSE),
//		DE TRABAJADORES EN SITUACIÓN DE EXCLUSIÓN SOCIAL, VÍCTIMAS DE VIOLENCIA DE GÉNERO, DOMESTICA O VÍCTIMAS DE TERRORISMO (pag.12)
		OPT9_OPTION_CHECK("Casilla de verificación48",Boolean.FALSE),
//		DE EXCLUIDOS EN EMPRESAS DE INSERCIÓN (pag.13)
		OPT10_OPTION_CHECK("Casilla de verificación48",Boolean.FALSE),
//		DE MAYORES DE 52 AÑOS BENEFICIARIOS DE SUBSIDIOS POR DESEMPLEO (pag.14)
		OPT11_OPTION_CHECK("Casilla de verificación48",Boolean.FALSE),
//		PROCENTE DE PRIMER EMPLEO JOVEN DE ETT. (pag.15)
		OPT12_OPTION_CHECK("Casilla de verificación48",Boolean.FALSE),
//		PROCEDENTE DE UN CONTRATO PARA LA FORMACIÓN Y EL APRENDIZAJE DE ETT (pag.16)
		OPT13_OPTION_CHECK("Casilla de verificación48",Boolean.FALSE),
//		PROCEDENTE DE UN CONTRATO EN PRÁCTICAS DE ETT. ( pág 17)
		OPT14_OPTION_CHECK("Casilla de verificación48",Boolean.FALSE),
//		DEL SERVICIO DEL HOGAR FAMILIAR (pag.18)
		OPT15_OPTION_CHECK("Casilla de verificación112",Boolean.FALSE),
		OPT15_TC2_100("Casilla de verificación180",Boolean.FALSE),
		OPT15_TC2_200("Casilla de verificación181",Boolean.FALSE),
		OPT15_ONSITE_HOURS_YES("Casilla de verificación178",Boolean.FALSE),
		OPT15_ONSITE_HOURS_NO("Casilla de verificación179",Boolean.FALSE),
		OPT15_ONSITE_WEEK_HOURS("Texto187",Boolean.FALSE),
		OPT15_ONSITE_HOURS_DISTRIBUTION("Texto110",Boolean.FALSE),
		OPT15_SALARY_OPT1("Casilla de verificación182",Boolean.FALSE),
		OPT15_SALARY_OPT2("Casilla de verificación183",Boolean.FALSE),
		OPT15_SALARY_OPT3("Casilla de verificación184",Boolean.FALSE),
		OPT15_OVERNIGHT_YES("Casilla de verificación185",Boolean.FALSE),
		OPT15_OVERNIGHT_NO("Casilla de verificación186",Boolean.FALSE),
		OPT15_OVERNIGHT_WEEK_DAYS("Texto188",Boolean.FALSE),
//		OTRAS SITUACIONES (pág19)
		OPT16_OPTION_CHECK("Casilla de verificación48",Boolean.FALSE),
//		CONVERSIÓN DE CONTRATO TEMPORAL EN CONTRATO INDEFINIDO (pag.20)
		OPT17_OPTION_CHECK("Casilla de verificación48",Boolean.FALSE),
		OPT17_FULL_TIME("Casilla de verificación190",Boolean.FALSE),
		OPT17_TC2_139("Casilla de verificación193",Boolean.FALSE),
		OPT17_TC2_109("Casilla de verificación1931",Boolean.FALSE),
		OPT17_TC2_189("Casilla de verificación19321",Boolean.FALSE),
		OPT17_FULL_TIME_QUOTE_BONUS_YES("Casilla de verificación3",Boolean.FALSE),
		OPT17_FULL_TIME_QUOTE_BONUS_NO("Casilla de verificación4",Boolean.FALSE),
		OPT17_PARTIALLY_TIME("Casilla de verificación191",Boolean.FALSE),
		OPT17_TC2_239("Casilla de verificación1933",Boolean.FALSE),
		OPT17_TC2_209("Casilla de verificación1946",Boolean.FALSE),
		OPT17_TC2_289("Casilla de verificación19569",Boolean.FALSE),
		OPT17_PARTIALLY_TIME_QUOTE_BONUS_YES("Casilla de verificación5",Boolean.FALSE),
		OPT17_PARTIALLY_TIME_QUOTE_BONUS_NO("Casilla de verificación6",Boolean.FALSE),
		OPT17_DISCONTINUOUS_TIME("Casilla de verificación192",Boolean.FALSE),
		OPT17_TC2_339("Casilla de verificación19398",Boolean.FALSE),
		OPT17_TC2_309("Casilla de verificación1937892",Boolean.FALSE),
		OPT17_TC2_389("Casilla de verificación19371",Boolean.FALSE),
		OPT17_DISCONTINUOUS_TIME_QUOTE_BONUS_YES("Casilla de verificación1",Boolean.FALSE),
		OPT17_DISCONTINUOUS_TIME_QUOTE_BONUS_NO("Casilla de verificación2",Boolean.FALSE),
		OPT17_SEPE_MUNICIPALITY("Texto197",Boolean.FALSE),
		OPT17_TRANSFORM_DATE("Texto198",Boolean.FALSE),
		OPT17_IS_FULL_TIME("Casilla de verificación1938888",Boolean.FALSE),
		OPT17_IS_FULL_TIME_DISCONTINUOUS("Casilla de verificación19333332",Boolean.FALSE),
		OPT17_SOURCE_CONTRACT("Texto199",Boolean.FALSE),
		OPT17_SOURCE_CONTRACT_START_DATE("Texto200",Boolean.FALSE),
		OPT17_SOURCE_CONTRACT_SEPE_MUNICIPALITY("Texto202",Boolean.FALSE),
		OPT17_SOURCE_CONTRACT_SEPE_DATE("Texto203",Boolean.FALSE),
		OPT17_SOURCE_CONTRACT_SEPE_ID("Texto204",Boolean.FALSE),
		;
		
		
		private String value;
		private boolean overridable;
		
		private IndefiniteOptionFieldName(String value, boolean overridable) {
			this.value = value;
			this.overridable = overridable;
		}
		
		@Override
		public boolean isOverridable(){
			return overridable;
		}
		@Override
		public String getValue() {
			return value;
		}
		
	}
	
}
	
	