package com.esferalia.aon.file.payroll.contract.pdf.model;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;

import com.code.aon.AonVersion;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.geozone.GeoZone;
import com.code.aon.registry.RegistryAddress;
import com.esferalia.aon.file.payroll.contract.pdf.ModelOption;
import com.esferalia.aon.file.payroll.contract.pdf.PdfFieldPractice;
import com.esferalia.aon.file.payroll.contract.pdf.UnsupportedContractDocumentException;
import com.esferalia.aon.file.payroll.contrata.ContrataContratoParams;
import com.esferalia.aon.file.payroll.contrata.IContrataParams;
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.payroll.ContractInfo.ContractVariable;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.payroll.enumeration.ContractCode;
import com.esferalia.aon.payroll.enumeration.contrata.TEQPTIEM;
import com.esferalia.aon.payroll.util.PayrollUtils;
import com.lowagie.text.pdf.PdfReader;



public class PracticeModel extends AbstractContractModel {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	public final static String MODEL_NAME = "Practicas";
	
	public PracticeModel(Contract contract){
		super.contract = contract;
		super.documentName = MODEL_NAME;
	}
	
	@Override
	public void loadPdfFieldValues(ContractCode code, Contract contract, List<IContrataParams> contrataParams) throws UnsupportedContractDocumentException{
		
		try {
			PdfReader reader = new PdfReader(getContractModelUrl(documentName+".pdf"));
			readPdfFields(reader);
			
			SimpleDateFormat dateFormatter = new SimpleDateFormat();
			ContrataContratoParams contrata = null;
			if(contrataParams!=null && contrataParams.size()>0){
				contrata = (ContrataContratoParams) contrataParams.get(0);
			}
			
			/* 
			 * Contract enterprise fields
			 */
			setPdfFieldValue(PdfFieldPractice.ENTERPRISE_CIF.getValue(),contract.getWorkPlace().getEnterprise().getRegistry().getDocument());
			if(StringUtils.isNotBlank(getContractInfoMap(contract).get(PdfFieldPractice.ENTERPRISE_DIR_STAFF_NAME.toString()))){
				setPdfFieldValue(PdfFieldPractice.ENTERPRISE_DIR_STAFF_NAME.getValue(), getContractInfoMap(contract).get(PdfFieldPractice.ENTERPRISE_DIR_STAFF_NAME.toString()));
			}
			if(StringUtils.isNotBlank(getContractInfoMap(contract).get(PdfFieldPractice.ENTERPRISE_DIR_STAFF_NIF.toString()))){
				setPdfFieldValue(PdfFieldPractice.ENTERPRISE_DIR_STAFF_NIF.getValue(), getContractInfoMap(contract).get(PdfFieldPractice.ENTERPRISE_DIR_STAFF_NIF.toString()));
			}
			if(StringUtils.isNotBlank(getContractInfoMap(contract).get(PdfFieldPractice.ENTERPRISE_DIR_STAFF_CHARGE.toString()))){
				setPdfFieldValue(PdfFieldPractice.ENTERPRISE_DIR_STAFF_CHARGE.getValue(), getContractInfoMap(contract).get(PdfFieldPractice.ENTERPRISE_DIR_STAFF_CHARGE.toString()));
			}
			setPdfFieldValue(PdfFieldPractice.ENTERPRISE_NAME.getValue(),contract.getWorkPlace().getEnterprise().getRegistry().getFullName());
			setPdfFieldValue(PdfFieldPractice.ENTERPRISE_ADDRESS.getValue(),contract.getWorkPlace().getEnterprise().getRegistry().getDefaultAddress().getFullAddress());
			try {	
				setPdfFieldValue(PdfFieldPractice.ENTERPRISE_COUNTRY.getValue(),contract.getWorkPlace().getEnterprise().getRegistry().getNationality().getName(getLocale()));
				setPdfFieldValue(PdfFieldPractice.ENTERPRISE_COUNTRY_CODE1.getValue(),String.valueOf(contract.getWorkPlace().getEnterprise().getRegistry().getNationality().getIsoNum()).substring(0,1));
				setPdfFieldValue(PdfFieldPractice.ENTERPRISE_COUNTRY_CODE2.getValue(),String.valueOf(contract.getWorkPlace().getEnterprise().getRegistry().getNationality().getIsoNum()).substring(1,2));
				setPdfFieldValue(PdfFieldPractice.ENTERPRISE_COUNTRY_CODE3.getValue(),String.valueOf(contract.getWorkPlace().getEnterprise().getRegistry().getNationality().getIsoNum()).substring(2,3));
			} catch (StringIndexOutOfBoundsException aie) {
				// do nothing
			} catch (NullPointerException npe) {
				// do nothing
			}
			try {	
				RegistryAddress address = contract.getWorkPlace().getEnterprise().getRegistry().getDefaultAddress();
				if(StringUtils.isNotBlank(address.getMunicipalityCode())){
					ResourceBundle bundle = ResourceBundle.getBundle(MUNICIPALITIES_BUNDLE_BASE_NAME);
					setPdfFieldValue(PdfFieldPractice.ENTERPRISE_MUNICIPALITY.getValue(),bundle.getString(address.getMunicipalityCode()));
					setPdfFieldValue(PdfFieldPractice.ENTERPRISE_MUNICIPALITY_CODE1.getValue(),address.getMunicipalityCode().substring(0, 1));
					setPdfFieldValue(PdfFieldPractice.ENTERPRISE_MUNICIPALITY_CODE2.getValue(),address.getMunicipalityCode().substring(1, 2));
					setPdfFieldValue(PdfFieldPractice.ENTERPRISE_MUNICIPALITY_CODE3.getValue(),address.getMunicipalityCode().substring(2, 3));
					setPdfFieldValue(PdfFieldPractice.ENTERPRISE_MUNICIPALITY_CODE4.getValue(),address.getMunicipalityCode().substring(3, 4));
					setPdfFieldValue(PdfFieldPractice.ENTERPRISE_MUNICIPALITY_CODE5.getValue(),address.getMunicipalityCode().substring(4, 5));
				}
			} catch (StringIndexOutOfBoundsException aie) {
				// do nothing
			} catch (NullPointerException npe) {
				// do nothing
			}
			try {	
				setPdfFieldValue(PdfFieldPractice.ENTERPRISE_ZIP1.getValue(),contract.getWorkPlace().getEnterprise().getRegistry().getDefaultAddress().getZip().substring(0, 1));
				setPdfFieldValue(PdfFieldPractice.ENTERPRISE_ZIP2.getValue(),contract.getWorkPlace().getEnterprise().getRegistry().getDefaultAddress().getZip().substring(1, 2));
				setPdfFieldValue(PdfFieldPractice.ENTERPRISE_ZIP3.getValue(),contract.getWorkPlace().getEnterprise().getRegistry().getDefaultAddress().getZip().substring(2, 3));
				setPdfFieldValue(PdfFieldPractice.ENTERPRISE_ZIP4.getValue(),contract.getWorkPlace().getEnterprise().getRegistry().getDefaultAddress().getZip().substring(3, 4));
				setPdfFieldValue(PdfFieldPractice.ENTERPRISE_ZIP5.getValue(),contract.getWorkPlace().getEnterprise().getRegistry().getDefaultAddress().getZip().substring(4, 5));
			} catch (StringIndexOutOfBoundsException aie) {
				// do nothing
			} catch (NullPointerException npe) {
				// do nothing
			}
			/* 
			 * Contract ccc fields
			 */
			if(contract.getEnterpriseCCC()!=null){
				if(PayrollUtils.getInstance().getRegimeCode(contract.getEnterpriseCCC())!=null){
					String quoteRegime = PayrollUtils.getInstance().getRegimeCode(contract.getEnterpriseCCC());
					if(StringUtils.isNotBlank(quoteRegime) && quoteRegime.length()>=4){
						setPdfFieldValue(PdfFieldPractice.CCC_REG1.getValue(),quoteRegime.substring(0, 1));
						setPdfFieldValue(PdfFieldPractice.CCC_REG2.getValue(),quoteRegime.substring(1, 2));
						setPdfFieldValue(PdfFieldPractice.CCC_REG3.getValue(),quoteRegime.substring(2, 3));
						setPdfFieldValue(PdfFieldPractice.CCC_REG4.getValue(),quoteRegime.substring(3, 4));
					}
				}
				if(contract.getEnterpriseCCC().getCcc().length()==11){
					setPdfFieldValue(PdfFieldPractice.CCC_PROV1.getValue(),contract.getEnterpriseCCC().getCcc().substring(0, 1));
					setPdfFieldValue(PdfFieldPractice.CCC_PROV2.getValue(),contract.getEnterpriseCCC().getCcc().substring(1, 2));
					setPdfFieldValue(PdfFieldPractice.CCC_NISS.getValue(),contract.getEnterpriseCCC().getCcc().substring(2, 9));
					setPdfFieldValue(PdfFieldPractice.CCC_CONTROL_DIGIT1.getValue(),contract.getEnterpriseCCC().getCcc().substring(9, 10));
					setPdfFieldValue(PdfFieldPractice.CCC_CONTROL_DIGIT2.getValue(),contract.getEnterpriseCCC().getCcc().substring(10, 11));
				} else {
					setPdfFieldValue(PdfFieldPractice.CCC_NISS.getValue(),contract.getEnterpriseCCC().getCcc());
				}
				setPdfFieldValue(PdfFieldPractice.CCC_ACTIVITY.getValue(),contract.getEnterpriseCCC().getActivity().getDescription());
				setPdfFieldValue(PdfFieldPractice.CCC_ACTIVITY_CODE1.getValue(),contract.getEnterpriseCCC().getActivity().getCnae2009().getCode().substring(0, 1));
				setPdfFieldValue(PdfFieldPractice.CCC_ACTIVITY_CODE2.getValue(),contract.getEnterpriseCCC().getActivity().getCnae2009().getCode().substring(1, 2));
			}
			/* 
			 * Contract workplace fields
			 */
			try {
				GeoZone country = obtainCountry(contract.getWorkPlace().getAddress().getGeozone());
				setPdfFieldValue(PdfFieldPractice.WORKPLACE_COUNTRY.getValue(),country.getName());
				setPdfFieldValue(PdfFieldPractice.WORKPLACE_COUNTRY_CODE1.getValue(),String.valueOf(obtainCountryByValue(country).getIsoNum()).substring(0,1));
				setPdfFieldValue(PdfFieldPractice.WORKPLACE_COUNTRY_CODE2.getValue(),String.valueOf(obtainCountryByValue(country).getIsoNum()).substring(1,2));
				setPdfFieldValue(PdfFieldPractice.WORKPLACE_COUNTRY_CODE3.getValue(),String.valueOf(obtainCountryByValue(country).getIsoNum()).substring(2,3));
			} catch (StringIndexOutOfBoundsException aie) {
				// do nothing
			} catch (NullPointerException npe) {
				// do nothing
			}
			try {
				RegistryAddress address = contract.getWorkPlace().getAddress();
				if(StringUtils.isNotBlank(address.getMunicipalityCode())){
					ResourceBundle bundle = ResourceBundle.getBundle(MUNICIPALITIES_BUNDLE_BASE_NAME);
					setPdfFieldValue(PdfFieldPractice.WORKPLACE_MUNICIPALITY.getValue(),bundle.getString(address.getMunicipalityCode()));
					setPdfFieldValue(PdfFieldPractice.WORKPLACE_MUNICIPALITY_CODE1.getValue(),address.getMunicipalityCode().substring(0, 1));
					setPdfFieldValue(PdfFieldPractice.WORKPLACE_MUNICIPALITY_CODE2.getValue(),address.getMunicipalityCode().substring(1, 2));
					setPdfFieldValue(PdfFieldPractice.WORKPLACE_MUNICIPALITY_CODE3.getValue(),address.getMunicipalityCode().substring(2, 3));
					setPdfFieldValue(PdfFieldPractice.WORKPLACE_MUNICIPALITY_CODE4.getValue(),address.getMunicipalityCode().substring(3, 4));
					setPdfFieldValue(PdfFieldPractice.WORKPLACE_MUNICIPALITY_CODE5.getValue(),address.getMunicipalityCode().substring(4, 5));
				}
			} catch (StringIndexOutOfBoundsException aie) {
				// do nothing
			} catch (NullPointerException npe) {
				// do nothing
			}
			/*
			 * Contract employee fields
			 */
			setPdfFieldValue(PdfFieldPractice.EMPLOYEE_NAME.getValue(),contract.getPerson().getFullName());
			setPdfFieldValue(PdfFieldPractice.EMPLOYEE_NIF.getValue(),contract.getPerson().getRegistry().getDocument());
			if(contract.getPerson().getBirthDate()!=null){
				SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
				setPdfFieldValue(PdfFieldPractice.EMPLOYEE_BIRTH_DATE.getValue(),formatter.format(contract.getPerson().getBirthDate()));
			}
			setPdfFieldValue(PdfFieldPractice.EMPLOYEE_NSS.getValue(),contract.getPerson().getSocialSecurityNumber());
			if(contrata!=null && contrata.getNivelFormativo()!=null){
				setPdfFieldValue(PdfFieldPractice.EMPLOYEE_FORMATION_LEVEL.getValue(),contrata.getNivelFormativo().getDescription());
				setPdfFieldValue(PdfFieldPractice.EMPLOYEE_FORMATION_CODE1.getValue(),contrata.getNivelFormativo().getCode().substring(0, 1));
				setPdfFieldValue(PdfFieldPractice.EMPLOYEE_FORMATION_CODE2.getValue(),contrata.getNivelFormativo().getCode().substring(1, 2));
			}
			try {
				setPdfFieldValue(PdfFieldPractice.EMPLOYEE_COUNTRY.getValue(),String.valueOf(contract.getPerson().getRegistry().getNationality().getName(getLocale())));
				setPdfFieldValue(PdfFieldPractice.EMPLOYEE_COUNTRY_CODE1.getValue(),String.valueOf(contract.getPerson().getRegistry().getNationality().getIsoNum()).substring(0,1));
				setPdfFieldValue(PdfFieldPractice.EMPLOYEE_COUNTRY_CODE2.getValue(),String.valueOf(contract.getPerson().getRegistry().getNationality().getIsoNum()).substring(1,2));
				setPdfFieldValue(PdfFieldPractice.EMPLOYEE_COUNTRY_CODE3.getValue(),String.valueOf(contract.getPerson().getRegistry().getNationality().getIsoNum()).substring(2,3));
			} catch (StringIndexOutOfBoundsException aie) {
				// do nothing
			} catch (NullPointerException npe) {
				// do nothing
			}
			try {
				RegistryAddress address = contract.getPerson().getRegistry().getDefaultAddress();
				if(StringUtils.isNotBlank(address.getMunicipalityCode())){
					ResourceBundle bundle = ResourceBundle.getBundle(MUNICIPALITIES_BUNDLE_BASE_NAME);
					setPdfFieldValue(PdfFieldPractice.EMPLOYEE_ADDRESS_MUNICIPALITY.getValue(),bundle.getString(address.getMunicipalityCode()));
					setPdfFieldValue(PdfFieldPractice.EMPLOYEE_ADDRESS_MUNICIPALITY_CODE1.getValue(),address.getMunicipalityCode().substring(0, 1));
					setPdfFieldValue(PdfFieldPractice.EMPLOYEE_ADDRESS_MUNICIPALITY_CODE2.getValue(),address.getMunicipalityCode().substring(1, 2));
					setPdfFieldValue(PdfFieldPractice.EMPLOYEE_ADDRESS_MUNICIPALITY_CODE3.getValue(),address.getMunicipalityCode().substring(2, 3));
					setPdfFieldValue(PdfFieldPractice.EMPLOYEE_ADDRESS_MUNICIPALITY_CODE4.getValue(),address.getMunicipalityCode().substring(3, 4));
					setPdfFieldValue(PdfFieldPractice.EMPLOYEE_ADDRESS_MUNICIPALITY_CODE5.getValue(),address.getMunicipalityCode().substring(4, 5));
				}
			} catch (StringIndexOutOfBoundsException aie) {
				// do nothing
			} catch (NullPointerException npe) {
				// do nothing
			}
			try {
				GeoZone country = obtainCountry(contract.getPerson().getRegistry().getDefaultAddress().getGeozone());
				setPdfFieldValue(PdfFieldPractice.EMPLOYEE_ADDRESS_COUNTRY.getValue(),country.getName());
				setPdfFieldValue(PdfFieldPractice.EMPLOYEE_ADDRESS_COUNTRY_CODE1.getValue(),String.valueOf(obtainCountryByValue(country).getIsoNum()).substring(0,1));
				setPdfFieldValue(PdfFieldPractice.EMPLOYEE_ADDRESS_COUNTRY_CODE2.getValue(),String.valueOf(obtainCountryByValue(country).getIsoNum()).substring(1,2));
				setPdfFieldValue(PdfFieldPractice.EMPLOYEE_ADDRESS_COUNTRY_CODE3.getValue(),String.valueOf(obtainCountryByValue(country).getIsoNum()).substring(2,3));
			} catch (StringIndexOutOfBoundsException aie) {
				// do nothing
			} catch (NullPointerException npe) {
				// do nothing
			}
			try {
				if(StringUtils.isNotBlank(getContractInfoMap(contract).get(PdfFieldPractice.LEGAL_REPRESENTATIVE_NAME.toString()))){
					setPdfFieldValue(PdfFieldPractice.LEGAL_REPRESENTATIVE_NAME.getValue(), getContractInfoMap(contract).get(PdfFieldPractice.LEGAL_REPRESENTATIVE_NAME.toString()));
				}
				if(StringUtils.isNotBlank(getContractInfoMap(contract).get(PdfFieldPractice.LEGAL_REPRESENTATIVE_NIF.toString()))){
					setPdfFieldValue(PdfFieldPractice.LEGAL_REPRESENTATIVE_NIF.getValue(), getContractInfoMap(contract).get(PdfFieldPractice.LEGAL_REPRESENTATIVE_NIF.toString()));
				}
				if(StringUtils.isNotBlank(getContractInfoMap(contract).get(PdfFieldPractice.LEGAL_REPRESENTATIVE_CHARGE.toString()))){
					setPdfFieldValue(PdfFieldPractice.LEGAL_REPRESENTATIVE_CHARGE.getValue(), getContractInfoMap(contract).get(PdfFieldPractice.LEGAL_REPRESENTATIVE_CHARGE.toString()));
				}
			} catch (StringIndexOutOfBoundsException aie) {
				// do nothing
			} catch (NullPointerException npe) {
				// do nothing
			}
			
			/*
			 * # Contract page 1
			 */
			if(StringUtils.isNotBlank(getContractInfoMap(contract).get(PdfFieldPractice.PROFESSIONAL_CERT.toString()))){
				setPdfFieldValue(PdfFieldPractice.PROFESSIONAL_CERT.getValue(), getContractInfoMap(contract).get(PdfFieldPractice.PROFESSIONAL_CERT.toString()));
			}
			if(StringUtils.isNotBlank(getContractInfoMap(contract).get(PdfFieldPractice.PROFESSIONAL_CERT_OBTAIN_DATE.toString()))){
				setPdfFieldValue(PdfFieldPractice.PROFESSIONAL_CERT_OBTAIN_DATE.getValue(), getContractInfoMap(contract).get(PdfFieldPractice.PROFESSIONAL_CERT_OBTAIN_DATE.toString()));
			}
			if(StringUtils.isNotBlank(getContractInfoMap(contract).get(PdfFieldPractice.DISABILITY_ISSUE_ENTITY.toString()))){
				setPdfFieldValue(PdfFieldPractice.DISABILITY_ISSUE_ENTITY.getValue(), getContractInfoMap(contract).get(PdfFieldPractice.DISABILITY_ISSUE_ENTITY.toString()));
			}
			if(StringUtils.isNotBlank(getContractInfoMap(contract).get(PdfFieldPractice.DISABILITY_ISSUE_ENTITY_MORE.toString()))){
				setPdfFieldValue(PdfFieldPractice.DISABILITY_ISSUE_ENTITY_MORE.getValue(), getContractInfoMap(contract).get(PdfFieldPractice.DISABILITY_ISSUE_ENTITY_MORE.toString()));
			}
			
			String key = getContractInfoMap(contract).get(PdfFieldPractice.FIRST_CONTRACT.toString());
			if(StringUtils.isNotBlank(key)){
				if(PdfFieldPractice.FIRST_CONTRACT_LT_30.toString().equals(key)){
					setPdfFieldValue(PdfFieldPractice.FIRST_CONTRACT_LT_30.getValue(), "true");
				} else if(PdfFieldPractice.FIRST_CONTRACT_LT_35.toString().equals(key)){
					setPdfFieldValue(PdfFieldPractice.FIRST_CONTRACT_LT_35.getValue(), "true");
				} else if(PdfFieldPractice.FIRST_CONTRACT_LT_30_RD1543_2011.toString().equals(key)){
					setPdfFieldValue(PdfFieldPractice.FIRST_CONTRACT_LT_30_RD1543_2011.getValue(), "true");
				}
			}
			
			
			if(contrata!=null){
				setPdfFieldValue(PdfFieldPractice.PROFESSION.getValue(), contrata.getCno().getTitle());
			}
			if(contract.getAgreementLevelCategory()!=null && contract.getAgreementLevelCategory().getId()!=null){
				setPdfFieldValue(PdfFieldPractice.CATEGORY.getValue(), contract.getAgreementLevelCategory().getDescription());
			} else {
				setPdfFieldValue(PdfFieldPractice.CATEGORY.getValue(), contract.getCategoryDescription());
			}
			
			setPdfFieldValue(PdfFieldPractice.WORKPLACE_FULL_ADDRESS.getValue(), null);
			setPdfFieldValue(PdfFieldPractice.WORKPLACE_FULL_ADDRESS_MORE.getValue(), contract.getWorkPlace().getAddress().getFullAddress()+", "+contract.getWorkPlace().getAddress().getLocation());

			/*
			 * # Contract page 2
			 */
			
			Map<String, String> map = getContractDataMap(contract);
			
			String monday = map.get(ContextVariable.MONDAY_HOURS.toString());
			String tuesday = map.get(ContextVariable.TUESDAY_HOURS.toString());
			String thursday = map.get(ContextVariable.THURSDAY_HOURS.toString());
			String wednesday = map.get(ContextVariable.WEDNESDAY_HOURS.toString());
			String friday = map.get(ContextVariable.FRIDAY_HOURS.toString());
			String saturday = map.get(ContextVariable.SATURDAY_HOURS.toString());
			String sunday = map.get(ContextVariable.SUNDAY_HOURS.toString());
			double weekHours = 0.0;
			if(StringUtils.isNotBlank(monday) || StringUtils.isNotBlank(tuesday) || StringUtils.isNotBlank(thursday) 
					|| StringUtils.isNotBlank(wednesday) || StringUtils.isNotBlank(friday) 
					|| StringUtils.isNotBlank(saturday) || StringUtils.isNotBlank(sunday)){
				weekHours += NumberUtils.isNumber(monday)?new Double(monday):0.0;
				weekHours += NumberUtils.isNumber(tuesday)?new Double(tuesday):0.0;
				weekHours += NumberUtils.isNumber(thursday)?new Double(thursday):0.0;
				weekHours += NumberUtils.isNumber(wednesday)?new Double(wednesday):0.0;
				weekHours += NumberUtils.isNumber(friday)?new Double(friday):0.0;
				weekHours += NumberUtils.isNumber(saturday)?new Double(saturday):0.0;
				weekHours += NumberUtils.isNumber(sunday)?new Double(sunday):0.0;
			}
			
			if(code.getValue().startsWith("1") || code.getValue().startsWith("4")){
				setPdfFieldValue(PdfFieldPractice.FULL_TIME.getValue(), "true");
				if(StringUtils.isNotBlank(getContractInfoMap(contract).get(PdfFieldPractice.FULL_TIME_WEEK_HOURS.toString()))){
					setPdfFieldValue(PdfFieldPractice.FULL_TIME_WEEK_HOURS.getValue(), getContractInfoMap(contract).get(PdfFieldPractice.FULL_TIME_WEEK_HOURS.toString()));
				}
				if(StringUtils.isNotBlank(getContractInfoMap(contract).get(PdfFieldPractice.FULL_TIME_START_TIME.toString()))){
					setPdfFieldValue(PdfFieldPractice.FULL_TIME_START_TIME.getValue(), getContractInfoMap(contract).get(PdfFieldPractice.FULL_TIME_START_TIME.toString()));
				}
				if(StringUtils.isNotBlank(getContractInfoMap(contract).get(PdfFieldPractice.FULL_TIME_END_TIME.toString()))){
					setPdfFieldValue(PdfFieldPractice.FULL_TIME_END_TIME.getValue(), getContractInfoMap(contract).get(PdfFieldPractice.FULL_TIME_END_TIME.toString()));
				}
			} else if(code.getValue().startsWith("2") || code.getValue().startsWith("5")){
				setPdfFieldValue(PdfFieldPractice.PARTIALLY_TIME.getValue(), "true");
				if(weekHours>0){
					setPdfFieldValue(PdfFieldPractice.PARTIALLY_TIME_HOURS.getValue(), String.valueOf(weekHours));
					setPdfFieldValue(PdfFieldPractice.PARTIALLY_TIME_WEEKLY.getValue(), "true");
				} else if(contrata!=null){
					if(contrata.getHorasJornada()!=null){
						setPdfFieldValue(PdfFieldPractice.PARTIALLY_TIME_HOURS.getValue(), String.valueOf(Integer.parseInt(contrata.getHorasJornada())));
					}
					if(contrata.getTipoJornada()==TEQPTIEM.TEQPTIEM_D){
						setPdfFieldValue(PdfFieldPractice.PARTIALLY_TIME_DAYLY.getValue(), "true");
					} else if(contrata.getTipoJornada()==TEQPTIEM.TEQPTIEM_S){
						setPdfFieldValue(PdfFieldPractice.PARTIALLY_TIME_WEEKLY.getValue(), "true");
					} else if(contrata.getTipoJornada()==TEQPTIEM.TEQPTIEM_M){
						setPdfFieldValue(PdfFieldPractice.PARTIALLY_TIME_MONTHLY.getValue(), "true");
					} else if(contrata.getTipoJornada()==TEQPTIEM.TEQPTIEM_A){
						setPdfFieldValue(PdfFieldPractice.PARTIALLY_TIME_YEARLY.getValue(), "true");
					}
				}
				
			}
			
			if(StringUtils.isNotBlank(getContractInfoMap(contract).get(PdfFieldPractice.JOB_TIME_DISTRIBUTION.toString()))){
				setPdfFieldValue(PdfFieldPractice.JOB_TIME_DISTRIBUTION.getValue(), getContractInfoMap(contract).get(PdfFieldPractice.JOB_TIME_DISTRIBUTION.toString()));
			}
			if(StringUtils.isNotBlank(getContractInfoMap(contract).get(PdfFieldPractice.JOB_TIME_DISTRIBUTION2.toString()))){
				setPdfFieldValue(PdfFieldPractice.JOB_TIME_DISTRIBUTION2.getValue(), getContractInfoMap(contract).get(PdfFieldPractice.JOB_TIME_DISTRIBUTION2.toString()));
			}
			
			Integer durationInMonths = getMonthsBetweenDates(contract.getStartDate(), contract.getEndDate());
			setPdfFieldValue(PdfFieldPractice.CONTRACT_DURATION.getValue(), durationInMonths!=null?durationInMonths+" meses":"");
			
			dateFormatter.applyPattern("dd/MM/yyyy");
			setPdfFieldValue(PdfFieldPractice.CONTRACT_START_DATE.getValue(), dateFormatter.format(contract.getStartDate()));
			
			String[] optionalEndDateCodes = {"401", "501", "410", "510", "403", "503", "540", "980", "990"};
			if(contract.getEndDate()!=null){
				setPdfFieldValue(PdfFieldPractice.CONTRACT_END_DATE.getValue(), dateFormatter.format(contract.getEndDate()));
			} else if(ArrayUtils.contains(optionalEndDateCodes, code.getValue())) {
				setPdfFieldValue(PdfFieldPractice.CONTRACT_END_DATE.getValue(), "fin de obra");
			}
			if(StringUtils.isNotBlank(getContractInfoMap(contract).get(PdfFieldPractice.TRIAL_DURATION.toString()))){
				setPdfFieldValue(PdfFieldPractice.TRIAL_DURATION.getValue(), getContractInfoMap(contract).get(PdfFieldPractice.TRIAL_DURATION.toString()));
			}
			if(StringUtils.isNotBlank(getContractInfoMap(contract).get(PdfFieldPractice.SALARY_AMOUNT.toString()))){
				setPdfFieldValue(PdfFieldPractice.SALARY_AMOUNT.getValue(), getContractInfoMap(contract).get(PdfFieldPractice.SALARY_AMOUNT.toString()));
			}
			if(StringUtils.isNotBlank(getContractInfoMap(contract).get(PdfFieldPractice.SALARY_PERIOD.toString()))){
				setPdfFieldValue(PdfFieldPractice.SALARY_PERIOD.getValue(), getContractInfoMap(contract).get(PdfFieldPractice.SALARY_PERIOD.toString()));
			}
			if(StringUtils.isNotBlank(getContractInfoMap(contract).get(PdfFieldPractice.SALARY_CONCEPT.toString()))){
				setPdfFieldValue(PdfFieldPractice.SALARY_CONCEPT.getValue(), getContractInfoMap(contract).get(PdfFieldPractice.SALARY_CONCEPT.toString()));
			}
			if(StringUtils.isNotBlank(getContractInfoMap(contract).get(PdfFieldPractice.HOLIDAYS.toString()))){
				setPdfFieldValue(PdfFieldPractice.HOLIDAYS.getValue(), getContractInfoMap(contract).get(PdfFieldPractice.HOLIDAYS.toString()));
			}
			if(contract.getAgreementLevelCategory()!=null && contract.getAgreementLevelCategory().getId()!=null){
				setPdfFieldValue(PdfFieldPractice.AGREEMENT_COLLECTIVE.getValue(), contract.getAgreementLevelCategory().getLevel().getAgreement().getDescription());
			}
			setPdfFieldValue(PdfFieldPractice.AGREEMENT_COLLECTIVE_MORE.getValue(), null);
			if(StringUtils.isNotBlank(getContractInfoMap(contract).get(PdfFieldPractice.SEPE_START_COMMUNICATION.toString()))){
				setPdfFieldValue(PdfFieldPractice.SEPE_START_COMMUNICATION.getValue(), getContractInfoMap(contract).get(PdfFieldPractice.SEPE_START_COMMUNICATION.toString()));
			}
			if(StringUtils.isNotBlank(getContractInfoMap(contract).get(PdfFieldPractice.SEPE_END_COMMUNICATION.toString()))){
				setPdfFieldValue(PdfFieldPractice.SEPE_END_COMMUNICATION.getValue(), getContractInfoMap(contract).get(PdfFieldPractice.SEPE_END_COMMUNICATION.toString()));
			}
			
			
			/*
			 *  OPTIONS PAGE
			 */
			ModelOption modelOption = ModelOption.valueOf(getContractInfoMap(contract).get(ContractVariable.CONTRACT_MODEL_OPTION.getValue()));
			
			if(modelOption == ModelOption.PRACTICE_OPT1){
				setPdfFieldValue(PdfFieldPractice.MAIN_OPT1_CHECK.getValue(),"true");
				setPdfFieldValue(PdfFieldPractice.OPT1_OPTION_CHECK.getValue(),"true");
				if(code == ContractCode.C420){
					setPdfFieldValue(PdfFieldPractice.OPT1_TC2_420.getValue(),"true");
				} else if(code == ContractCode.C520){
					setPdfFieldValue(PdfFieldPractice.OPT1_TC2_520.getValue(),"true");
				}
			} else if(modelOption == ModelOption.PRACTICE_OPT2){
				setPdfFieldValue(PdfFieldPractice.MAIN_OPT2_CHECK.getValue(),"true");
				setPdfFieldValue(PdfFieldPractice.OPT2_OPTION_CHECK.getValue(),"true");
				if(code == ContractCode.C450){
					setPdfFieldValue(PdfFieldPractice.OPT2_TC2_450.getValue(),"true");
				} else if(code == ContractCode.C550){
					setPdfFieldValue(PdfFieldPractice.OPT2_TC2_550.getValue(),"true");
				}
				// TODO
				setPdfFieldValue(PdfFieldPractice.OPT2_A.getValue(),null);
				setPdfFieldValue(PdfFieldPractice.OPT2_A_SOCIAL_SERVICES_FROM.getValue(),null);
				setPdfFieldValue(PdfFieldPractice.OPT2_A_A.getValue(),null);
				setPdfFieldValue(PdfFieldPractice.OPT2_A_B.getValue(),null);
				setPdfFieldValue(PdfFieldPractice.OPT2_A_C.getValue(),null);
				setPdfFieldValue(PdfFieldPractice.OPT2_A_D.getValue(),null);
				setPdfFieldValue(PdfFieldPractice.OPT2_A_E.getValue(),null);
				setPdfFieldValue(PdfFieldPractice.OPT2_A_F.getValue(),null);
				setPdfFieldValue(PdfFieldPractice.OPT2_A_G.getValue(),null);
				setPdfFieldValue(PdfFieldPractice.OPT2_A_H.getValue(),null);
				setPdfFieldValue(PdfFieldPractice.OPT2_B.getValue(),null);
				setPdfFieldValue(PdfFieldPractice.OPT2_B_OFFICIAL_ENTITY.getValue(),null);
				setPdfFieldValue(PdfFieldPractice.OPT2_B_LO_1_2004.getValue(),null);
				setPdfFieldValue(PdfFieldPractice.OPT2_B__LAW_27_2003.getValue(),null);
				setPdfFieldValue(PdfFieldPractice.OPT2_C.getValue(),null);
			} else if(modelOption == ModelOption.PRACTICE_OPT3){
				setPdfFieldValue(PdfFieldPractice.MAIN_OPT3_CHECK.getValue(),"true");
				setPdfFieldValue(PdfFieldPractice.OPT3_OPTION_CHECK.getValue(),"true");
				if(code == ContractCode.C420){
					setPdfFieldValue(PdfFieldPractice.OPT3_TC2_420.getValue(),"true");
				}
				String unemployment = getContractInfoMap(contract).get(PdfFieldPractice.OPT3_UNEMPLOYMENT.toString());
				if(StringUtils.isNotBlank(unemployment)){
					if(PdfFieldPractice.OPT3_UNEMPLOYMENT_AGRARIAN_REGIME.toString().equals(unemployment)){
						setPdfFieldValue(PdfFieldPractice.OPT3_UNEMPLOYMENT_AGRARIAN_REGIME.getValue(),"true");
					} else if(PdfFieldPractice.OPT3_UNEMPLOYMENT_ART_215.toString().equals(unemployment)){
						setPdfFieldValue(PdfFieldPractice.OPT3_UNEMPLOYMENT_ART_215.getValue(),"true");
					}
				}
			} else if(modelOption == ModelOption.PRACTICE_OPT4){
				setPdfFieldValue(PdfFieldPractice.MAIN_OPT4_CHECK.getValue(),"true");
				setPdfFieldValue(PdfFieldPractice.OPT4_OPTION_CHECK.getValue(),"true");
				if(code == ContractCode.C420){
					setPdfFieldValue(PdfFieldPractice.OPT4_TC2_420.getValue(),"true");
				} else if(code == ContractCode.C520){
					setPdfFieldValue(PdfFieldPractice.OPT4_TC2_520.getValue(),"true");
				}
				if(StringUtils.isNotBlank(getContractInfoMap(contract).get(PdfFieldPractice.OPT4_TRIAL_DURATION.toString()))){
					setPdfFieldValue(PdfFieldPractice.OPT4_TRIAL_DURATION.getValue(),getContractInfoMap(contract).get(PdfFieldPractice.OPT4_TRIAL_DURATION.toString()));
				}
				if(StringUtils.isNotBlank(getContractInfoMap(contract).get(PdfFieldPractice.OPT4_TRIAL_DURATION_CONDITIONS.toString()))){
					setPdfFieldValue(PdfFieldPractice.OPT4_TRIAL_DURATION_CONDITIONS.getValue(),getContractInfoMap(contract).get(PdfFieldPractice.OPT4_TRIAL_DURATION_CONDITIONS.toString()));
				}
				if(StringUtils.isNotBlank(getContractInfoMap(contract).get(PdfFieldPractice.OPT4_WORK_PLACE_ADAPTATIONS.toString()))){
					setPdfFieldValue(PdfFieldPractice.OPT4_WORK_PLACE_ADAPTATIONS.getValue(),getContractInfoMap(contract).get(PdfFieldPractice.OPT4_WORK_PLACE_ADAPTATIONS.toString()));
				}
				if(StringUtils.isNotBlank(getContractInfoMap(contract).get(PdfFieldPractice.OPT4_STAFF_ADJUSTMENT.toString()))){
					setPdfFieldValue(PdfFieldPractice.OPT4_STAFF_ADJUSTMENT.getValue(),getContractInfoMap(contract).get(PdfFieldPractice.OPT4_STAFF_ADJUSTMENT.toString()));
				}
				if(StringUtils.isNotBlank(getContractInfoMap(contract).get(PdfFieldPractice.OPT4_STAFF_ADJUSTMENT_MORE.toString()))){
					setPdfFieldValue(PdfFieldPractice.OPT4_STAFF_ADJUSTMENT_MORE.getValue(),getContractInfoMap(contract).get(PdfFieldPractice.OPT4_STAFF_ADJUSTMENT_MORE.toString()));
				}
				if(contract.getAgreementLevelCategory()!=null && contract.getAgreementLevelCategory().getId()!=null){
					setPdfFieldValue(PdfFieldPractice.OPT4_AGREEMENT_COLLECTIVE.getValue(), contract.getAgreementLevelCategory().getLevel().getAgreement().getDescription());
				}
			} else if(modelOption == ModelOption.PRACTICE_OPT5){
				setPdfFieldValue(PdfFieldPractice.MAIN_OPT5_CHECK.getValue(),"true");
				setPdfFieldValue(PdfFieldPractice.OPT5_OPTION_CHECK.getValue(),"true");
				if(code == ContractCode.C420){
					setPdfFieldValue(PdfFieldPractice.OPT5_TC2_420.getValue(),"true");
				} else if(code == ContractCode.C520){
					setPdfFieldValue(PdfFieldPractice.OPT5_TC2_520.getValue(),"true");
				}
				String motivation = getContractInfoMap(contract).get(PdfFieldPractice.OPT5_EMPLOYER.toString());
				if(StringUtils.isNotBlank(motivation)){
					if(PdfFieldPractice.OPT5_MOTIVATION_SOCIAL_INTEREST.toString().equals(motivation)){
						setPdfFieldValue(PdfFieldPractice.OPT5_MOTIVATION_SOCIAL_INTEREST.getValue(),"true");
					} else if(PdfFieldPractice.OPT5_MOTIVATION_AGRARIAN_PROMOTION.toString().equals(motivation)){
						setPdfFieldValue(PdfFieldPractice.OPT5_MOTIVATION_AGRARIAN_PROMOTION.getValue(),"true");
					}
				}
				String employer = getContractInfoMap(contract).get(PdfFieldPractice.OPT5_EMPLOYER.toString());
				if(StringUtils.isNotBlank(employer)){
					if(PdfFieldPractice.OPT5_EMPLOYER_LOCAL_CORPORATION.toString().equals(employer)){
						setPdfFieldValue(PdfFieldPractice.OPT5_EMPLOYER_LOCAL_CORPORATION.getValue(),"true");
					} else if(PdfFieldPractice.OPT5_EMPLOYER_GENERAL_ADMINISTRATION.toString().equals(employer)){
						setPdfFieldValue(PdfFieldPractice.OPT5_EMPLOYER_GENERAL_ADMINISTRATION.getValue(),"true");
					} else if(PdfFieldPractice.OPT5_EMPLOYER_AUTON_COMMUNITY.toString().equals(employer)){
						setPdfFieldValue(PdfFieldPractice.OPT5_EMPLOYER_AUTON_COMMUNITY.getValue(),"true");
					} else if(PdfFieldPractice.OPT5_EMPLOYER_NONPROFIT_ENTITY.toString().equals(employer)){
						setPdfFieldValue(PdfFieldPractice.OPT5_EMPLOYER_NONPROFIT_ENTITY.getValue(),"true");
					} else if(PdfFieldPractice.OPT5_EMPLOYER_UNIVERSITY.toString().equals(employer)){
						setPdfFieldValue(PdfFieldPractice.OPT5_EMPLOYER_UNIVERSITY.getValue(),"true");
					}
				}
				if(contract.getAgreementLevelCategory()!=null && contract.getAgreementLevelCategory().getId()!=null){
					setPdfFieldValue(PdfFieldPractice.OPT5_AGREEMENT_COLLECTIVE.getValue(), contract.getAgreementLevelCategory().getLevel().getAgreement().getDescription());
				}
			}
		} catch (IOException e) {
			// do nothing
		} catch (ManagerBeanException e) {
			// do nothing
		}
	}
	
	private Integer getMonthsBetweenDates(Date startDate, Date endDate) {
		if(startDate!=null && endDate!=null){
			return (int) ((CommonUtil.getDaysBetweenDates(startDate, endDate, true))/30);
		}
		return null;
	}

}
	
	