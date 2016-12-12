package com.esferalia.aon.file.payroll.contract.pdf.model;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;

import com.code.aon.AonVersion;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.geozone.GeoZone;
import com.code.aon.registry.RegistryAddress;
import com.esferalia.aon.file.payroll.contract.pdf.ModelOption;
import com.esferalia.aon.file.payroll.contract.pdf.PdfFieldTemporary;
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
import com.esferalia.aon.payroll.util.PayrollUtils;
import com.lowagie.text.pdf.PdfReader;



public class TemporaryModel extends AbstractContractModel {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	public final static String MODEL_NAME = "Temporal";
	
	public TemporaryModel(Contract contract){
		super.contract = contract;
		super.documentName = MODEL_NAME;
	}
	
	@Override
	public void loadPdfFieldValues(ContractCode code, Contract contract, List<IContrataParams> contrataParams) throws UnsupportedContractDocumentException{
		
		try {
			PdfReader reader = new PdfReader(getContractModelUrl(documentName+".pdf"));
			readPdfFields(reader);
			
			ContrataContratoParams contrata = null;
			if(contrataParams!=null && contrataParams.size()>0){
				contrata = (ContrataContratoParams) contrataParams.get(0);
			}
			SimpleDateFormat dateFormatter = new SimpleDateFormat();
			
			/* 
			 * Contract enterprise fields
			 */
			setPdfFieldValue(PdfFieldTemporary.ENTERPRISE_CIF.getValue(),contract.getWorkPlace().getEnterprise().getRegistry().getDocument());
			if(StringUtils.isNotBlank(getContractInfoMap(contract).get(PdfFieldTemporary.ENTERPRISE_DIR_STAFF_NAME.toString()))){
				setPdfFieldValue(PdfFieldTemporary.ENTERPRISE_DIR_STAFF_NAME.getValue(), getContractInfoMap(contract).get(PdfFieldTemporary.ENTERPRISE_DIR_STAFF_NAME.toString()));
			}
			if(StringUtils.isNotBlank(getContractInfoMap(contract).get(PdfFieldTemporary.ENTERPRISE_DIR_STAFF_NIF.toString()))){
				setPdfFieldValue(PdfFieldTemporary.ENTERPRISE_DIR_STAFF_NIF.getValue(), getContractInfoMap(contract).get(PdfFieldTemporary.ENTERPRISE_DIR_STAFF_NIF.toString()));
			}
			if(StringUtils.isNotBlank(getContractInfoMap(contract).get(PdfFieldTemporary.ENTERPRISE_DIR_STAFF_CHARGE.toString()))){
				setPdfFieldValue(PdfFieldTemporary.ENTERPRISE_DIR_STAFF_CHARGE.getValue(), getContractInfoMap(contract).get(PdfFieldTemporary.ENTERPRISE_DIR_STAFF_CHARGE.toString()));
			}
			setPdfFieldValue(PdfFieldTemporary.ENTERPRISE_NAME.getValue(),contract.getWorkPlace().getEnterprise().getRegistry().getFullName());
			setPdfFieldValue(PdfFieldTemporary.ENTERPRISE_ADDRESS.getValue(),contract.getWorkPlace().getEnterprise().getRegistry().getDefaultAddress().getFullAddress());
			try {	
				setPdfFieldValue(PdfFieldTemporary.ENTERPRISE_COUNTRY.getValue(),contract.getWorkPlace().getEnterprise().getRegistry().getNationality().getName(getLocale()));
				setPdfFieldValue(PdfFieldTemporary.ENTERPRISE_COUNTRY_CODE1.getValue(),String.valueOf(contract.getWorkPlace().getEnterprise().getRegistry().getNationality().getIsoNum()).substring(0,1));
				setPdfFieldValue(PdfFieldTemporary.ENTERPRISE_COUNTRY_CODE2.getValue(),String.valueOf(contract.getWorkPlace().getEnterprise().getRegistry().getNationality().getIsoNum()).substring(1,2));
				setPdfFieldValue(PdfFieldTemporary.ENTERPRISE_COUNTRY_CODE3.getValue(),String.valueOf(contract.getWorkPlace().getEnterprise().getRegistry().getNationality().getIsoNum()).substring(2,3));
			} catch (StringIndexOutOfBoundsException aie) {
				// do nothing
			} catch (NullPointerException npe) {
				// do nothing
			}
			try {	
				RegistryAddress address = contract.getWorkPlace().getEnterprise().getRegistry().getDefaultAddress();
				if(StringUtils.isNotBlank(address.getMunicipalityCode())){
					ResourceBundle bundle = ResourceBundle.getBundle(MUNICIPALITIES_BUNDLE_BASE_NAME);
					setPdfFieldValue(PdfFieldTemporary.ENTERPRISE_MUNICIPALITY.getValue(),bundle.getString(address.getMunicipalityCode()));
					setPdfFieldValue(PdfFieldTemporary.ENTERPRISE_MUNICIPALITY_CODE1.getValue(),address.getMunicipalityCode().substring(0, 1));
					setPdfFieldValue(PdfFieldTemporary.ENTERPRISE_MUNICIPALITY_CODE2.getValue(),address.getMunicipalityCode().substring(1, 2));
					setPdfFieldValue(PdfFieldTemporary.ENTERPRISE_MUNICIPALITY_CODE3.getValue(),address.getMunicipalityCode().substring(2, 3));
					setPdfFieldValue(PdfFieldTemporary.ENTERPRISE_MUNICIPALITY_CODE4.getValue(),address.getMunicipalityCode().substring(3, 4));
					setPdfFieldValue(PdfFieldTemporary.ENTERPRISE_MUNICIPALITY_CODE5.getValue(),address.getMunicipalityCode().substring(4, 5));
				}
			} catch (StringIndexOutOfBoundsException aie) {
				// do nothing
			} catch (NullPointerException npe) {
				// do nothing
			}
			try {	
				setPdfFieldValue(PdfFieldTemporary.ENTERPRISE_ZIP1.getValue(),contract.getWorkPlace().getEnterprise().getRegistry().getDefaultAddress().getZip().substring(0, 1));
				setPdfFieldValue(PdfFieldTemporary.ENTERPRISE_ZIP2.getValue(),contract.getWorkPlace().getEnterprise().getRegistry().getDefaultAddress().getZip().substring(1, 2));
				setPdfFieldValue(PdfFieldTemporary.ENTERPRISE_ZIP3.getValue(),contract.getWorkPlace().getEnterprise().getRegistry().getDefaultAddress().getZip().substring(2, 3));
				setPdfFieldValue(PdfFieldTemporary.ENTERPRISE_ZIP4.getValue(),contract.getWorkPlace().getEnterprise().getRegistry().getDefaultAddress().getZip().substring(3, 4));
				setPdfFieldValue(PdfFieldTemporary.ENTERPRISE_ZIP5.getValue(),contract.getWorkPlace().getEnterprise().getRegistry().getDefaultAddress().getZip().substring(4, 5));
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
						setPdfFieldValue(PdfFieldTemporary.CCC_REG1.getValue(),quoteRegime.substring(0, 1));
						setPdfFieldValue(PdfFieldTemporary.CCC_REG2.getValue(),quoteRegime.substring(1, 2));
						setPdfFieldValue(PdfFieldTemporary.CCC_REG3.getValue(),quoteRegime.substring(2, 3));
						setPdfFieldValue(PdfFieldTemporary.CCC_REG4.getValue(),quoteRegime.substring(3, 4));
					}
				}
				if(contract.getEnterpriseCCC().getCcc().length()==11){
					setPdfFieldValue(PdfFieldTemporary.CCC_PROV1.getValue(),contract.getEnterpriseCCC().getCcc().substring(0, 1));
					setPdfFieldValue(PdfFieldTemporary.CCC_PROV2.getValue(),contract.getEnterpriseCCC().getCcc().substring(1, 2));
					setPdfFieldValue(PdfFieldTemporary.CCC_NISS.getValue(),contract.getEnterpriseCCC().getCcc().substring(2, 9));
					setPdfFieldValue(PdfFieldTemporary.CCC_CONTROL_DIGIT1.getValue(),contract.getEnterpriseCCC().getCcc().substring(9, 10));
					setPdfFieldValue(PdfFieldTemporary.CCC_CONTROL_DIGIT2.getValue(),contract.getEnterpriseCCC().getCcc().substring(10, 11));
				} else {
					setPdfFieldValue(PdfFieldTemporary.CCC_NISS.getValue(),contract.getEnterpriseCCC().getCcc());
				}
				setPdfFieldValue(PdfFieldTemporary.CCC_ACTIVITY.getValue(),contract.getEnterpriseCCC().getActivity().getDescription());
				setPdfFieldValue(PdfFieldTemporary.CCC_ACTIVITY_CODE1.getValue(),contract.getEnterpriseCCC().getActivity().getCnae2009().getCode().substring(0, 1));
				setPdfFieldValue(PdfFieldTemporary.CCC_ACTIVITY_CODE2.getValue(),contract.getEnterpriseCCC().getActivity().getCnae2009().getCode().substring(1, 2));
			}
			/* 
			 * Contract workplace fields
			 */
			try {
				GeoZone country = obtainCountry(contract.getWorkPlace().getAddress().getGeozone());
				setPdfFieldValue(PdfFieldTemporary.WORKPLACE_COUNTRY.getValue(),country.getName());
				setPdfFieldValue(PdfFieldTemporary.WORKPLACE_COUNTRY_CODE1.getValue(),String.valueOf(obtainCountryByValue(country).getIsoNum()).substring(0,1));
				setPdfFieldValue(PdfFieldTemporary.WORKPLACE_COUNTRY_CODE2.getValue(),String.valueOf(obtainCountryByValue(country).getIsoNum()).substring(1,2));
				setPdfFieldValue(PdfFieldTemporary.WORKPLACE_COUNTRY_CODE3.getValue(),String.valueOf(obtainCountryByValue(country).getIsoNum()).substring(2,3));
			} catch (StringIndexOutOfBoundsException aie) {
				// do nothing
			} catch (NullPointerException npe) {
				// do nothing
			}
			try {
				RegistryAddress address = contract.getWorkPlace().getAddress();
				if(StringUtils.isNotBlank(address.getMunicipalityCode())){
					ResourceBundle bundle = ResourceBundle.getBundle(MUNICIPALITIES_BUNDLE_BASE_NAME);
					setPdfFieldValue(PdfFieldTemporary.WORKPLACE_MUNICIPALITY.getValue(),bundle.getString(address.getMunicipalityCode()));
					setPdfFieldValue(PdfFieldTemporary.WORKPLACE_MUNICIPALITY_CODE1.getValue(),address.getMunicipalityCode().substring(0, 1));
					setPdfFieldValue(PdfFieldTemporary.WORKPLACE_MUNICIPALITY_CODE2.getValue(),address.getMunicipalityCode().substring(1, 2));
					setPdfFieldValue(PdfFieldTemporary.WORKPLACE_MUNICIPALITY_CODE3.getValue(),address.getMunicipalityCode().substring(2, 3));
					setPdfFieldValue(PdfFieldTemporary.WORKPLACE_MUNICIPALITY_CODE4.getValue(),address.getMunicipalityCode().substring(3, 4));
					setPdfFieldValue(PdfFieldTemporary.WORKPLACE_MUNICIPALITY_CODE5.getValue(),address.getMunicipalityCode().substring(4, 5));
				}
			} catch (StringIndexOutOfBoundsException aie) {
				// do nothing
			} catch (NullPointerException npe) {
				// do nothing
			}
			/*
			 * Contract employee fields
			 */
			setPdfFieldValue(PdfFieldTemporary.EMPLOYEE_NAME.getValue(),contract.getPerson().getFullName());
			setPdfFieldValue(PdfFieldTemporary.EMPLOYEE_NIF.getValue(),contract.getPerson().getRegistry().getDocument());
			if(contract.getPerson().getBirthDate()!=null){
				SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
				setPdfFieldValue(PdfFieldTemporary.EMPLOYEE_BIRTH_DATE.getValue(),formatter.format(contract.getPerson().getBirthDate()));
			}
			setPdfFieldValue(PdfFieldTemporary.EMPLOYEE_NSS.getValue(),contract.getPerson().getSocialSecurityNumber());
			if(contrata!=null && contrata.getNivelFormativo()!=null){
				setPdfFieldValue(PdfFieldTemporary.EMPLOYEE_FORMATION_LEVEL.getValue(),contrata.getNivelFormativo().getDescription());
				setPdfFieldValue(PdfFieldTemporary.EMPLOYEE_FORMATION_CODE1.getValue(),contrata.getNivelFormativo().getCode().substring(0, 1));
				setPdfFieldValue(PdfFieldTemporary.EMPLOYEE_FORMATION_CODE2.getValue(),contrata.getNivelFormativo().getCode().substring(1, 2));
			}
			try {
				setPdfFieldValue(PdfFieldTemporary.EMPLOYEE_COUNTRY.getValue(),String.valueOf(contract.getPerson().getRegistry().getNationality().getName(getLocale())));
				setPdfFieldValue(PdfFieldTemporary.EMPLOYEE_COUNTRY_CODE1.getValue(),String.valueOf(contract.getPerson().getRegistry().getNationality().getIsoNum()).substring(0,1));
				setPdfFieldValue(PdfFieldTemporary.EMPLOYEE_COUNTRY_CODE2.getValue(),String.valueOf(contract.getPerson().getRegistry().getNationality().getIsoNum()).substring(1,2));
				setPdfFieldValue(PdfFieldTemporary.EMPLOYEE_COUNTRY_CODE3.getValue(),String.valueOf(contract.getPerson().getRegistry().getNationality().getIsoNum()).substring(2,3));
			} catch (StringIndexOutOfBoundsException aie) {
				// do nothing
			} catch (NullPointerException npe) {
				// do nothing
			}
			try {
				RegistryAddress address = contract.getPerson().getRegistry().getDefaultAddress();
				if(StringUtils.isNotBlank(address.getMunicipalityCode())){
					ResourceBundle bundle = ResourceBundle.getBundle(MUNICIPALITIES_BUNDLE_BASE_NAME);
					setPdfFieldValue(PdfFieldTemporary.EMPLOYEE_ADDRESS_MUNICIPALITY.getValue(),bundle.getString(address.getMunicipalityCode()));
					setPdfFieldValue(PdfFieldTemporary.EMPLOYEE_ADDRESS_MUNICIPALITY_CODE1.getValue(),address.getMunicipalityCode().substring(0, 1));
					setPdfFieldValue(PdfFieldTemporary.EMPLOYEE_ADDRESS_MUNICIPALITY_CODE2.getValue(),address.getMunicipalityCode().substring(1, 2));
					setPdfFieldValue(PdfFieldTemporary.EMPLOYEE_ADDRESS_MUNICIPALITY_CODE3.getValue(),address.getMunicipalityCode().substring(2, 3));
					setPdfFieldValue(PdfFieldTemporary.EMPLOYEE_ADDRESS_MUNICIPALITY_CODE4.getValue(),address.getMunicipalityCode().substring(3, 4));
					setPdfFieldValue(PdfFieldTemporary.EMPLOYEE_ADDRESS_MUNICIPALITY_CODE5.getValue(),address.getMunicipalityCode().substring(4, 5));
				}
			} catch (StringIndexOutOfBoundsException aie) {
				// do nothing
			} catch (NullPointerException npe) {
				// do nothing
			}
			try {
				GeoZone country = obtainCountry(contract.getPerson().getRegistry().getDefaultAddress().getGeozone());
				setPdfFieldValue(PdfFieldTemporary.EMPLOYEE_ADDRESS_COUNTRY.getValue(),country.getName());
				setPdfFieldValue(PdfFieldTemporary.EMPLOYEE_ADDRESS_COUNTRY_CODE1.getValue(),String.valueOf(obtainCountryByValue(country).getIsoNum()).substring(0,1));
				setPdfFieldValue(PdfFieldTemporary.EMPLOYEE_ADDRESS_COUNTRY_CODE2.getValue(),String.valueOf(obtainCountryByValue(country).getIsoNum()).substring(1,2));
				setPdfFieldValue(PdfFieldTemporary.EMPLOYEE_ADDRESS_COUNTRY_CODE3.getValue(),String.valueOf(obtainCountryByValue(country).getIsoNum()).substring(2,3));
			} catch (StringIndexOutOfBoundsException aie) {
				// do nothing
			} catch (NullPointerException npe) {
				// do nothing
			}
			try {
				if(StringUtils.isNotBlank(getContractInfoMap(contract).get(PdfFieldTemporary.LEGAL_REPRESENTATIVE_NAME.toString()))){
					setPdfFieldValue(PdfFieldTemporary.LEGAL_REPRESENTATIVE_NAME.getValue(), getContractInfoMap(contract).get(PdfFieldTemporary.LEGAL_REPRESENTATIVE_NAME.toString()));
				}
				if(StringUtils.isNotBlank(getContractInfoMap(contract).get(PdfFieldTemporary.LEGAL_REPRESENTATIVE_NIF.toString()))){
					setPdfFieldValue(PdfFieldTemporary.LEGAL_REPRESENTATIVE_NIF.getValue(), getContractInfoMap(contract).get(PdfFieldTemporary.LEGAL_REPRESENTATIVE_NIF.toString()));
				}
				if(StringUtils.isNotBlank(getContractInfoMap(contract).get(PdfFieldTemporary.LEGAL_REPRESENTATIVE_CHARGE.toString()))){
					setPdfFieldValue(PdfFieldTemporary.LEGAL_REPRESENTATIVE_CHARGE.getValue(), getContractInfoMap(contract).get(PdfFieldTemporary.LEGAL_REPRESENTATIVE_CHARGE.toString()));
				}
			} catch (StringIndexOutOfBoundsException aie) {
				// do nothing
			} catch (NullPointerException npe) {
				// do nothing
			}
			
			
			/*
			 * Contract page 1
			 */
			if(contrata!=null && contrata.getCno()!=null){
				setPdfFieldValue(PdfFieldTemporary.PROFESSION.getValue(), contrata.getCno().getTitle());
			}
			if(contract.getAgreementLevelCategory()!=null && contract.getAgreementLevelCategory().getId()!=null){
				setPdfFieldValue(PdfFieldTemporary.CATEGORY.getValue(), contract.getAgreementLevelCategory().getDescription());
			} else {
				setPdfFieldValue(PdfFieldTemporary.CATEGORY.getValue(), contract.getCategoryDescription());
			}
			
			if(StringUtils.isNotBlank(getContractInfoMap(contract).get(PdfFieldTemporary.FUNCTIONS.toString()))){
				setPdfFieldValue(PdfFieldTemporary.FUNCTIONS.getValue(), getContractInfoMap(contract).get(PdfFieldTemporary.FUNCTIONS.toString()));
			}
			
			setPdfFieldValue(PdfFieldTemporary.WORKPLACE_FULL_ADDRESS_MORE.getValue(), contract.getWorkPlace().getAddress().getFullAddress()+", "+contract.getWorkPlace().getAddress().getLocation());
			if(StringUtils.isNotBlank(getContractInfoMap(contract).get(PdfFieldTemporary.EMPLOYEE_CONTRACT_DISTANCE.toString()))){
				setPdfFieldValue(PdfFieldTemporary.EMPLOYEE_CONTRACT_DISTANCE.getValue(), getContractInfoMap(contract).get(PdfFieldTemporary.EMPLOYEE_CONTRACT_DISTANCE.toString()));
			}
			if(StringUtils.isNotBlank(getContractInfoMap(contract).get(PdfFieldTemporary.EMPLOYEE_CONTRACT_DIST_ADDR.toString()))){
				setPdfFieldValue(PdfFieldTemporary.EMPLOYEE_CONTRACT_DIST_ADDR.getValue(), getContractInfoMap(contract).get(PdfFieldTemporary.EMPLOYEE_CONTRACT_DIST_ADDR.toString()));
			}
			
			
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
				setPdfFieldValue(PdfFieldTemporary.FULL_TIME.getValue(), "true");
				if(StringUtils.isNotBlank(getContractInfoMap(contract).get(PdfFieldTemporary.FULL_TIME_WEEK_HOURS.toString()))){
					setPdfFieldValue(PdfFieldTemporary.FULL_TIME_WEEK_HOURS.getValue(), getContractInfoMap(contract).get(PdfFieldTemporary.FULL_TIME_WEEK_HOURS.toString()));
				}
				if(StringUtils.isNotBlank(getContractInfoMap(contract).get(PdfFieldTemporary.FULL_TIME_START_TIME.toString()))){
					setPdfFieldValue(PdfFieldTemporary.FULL_TIME_START_TIME.getValue(), getContractInfoMap(contract).get(PdfFieldTemporary.FULL_TIME_START_TIME.toString()));
				}
				if(StringUtils.isNotBlank(getContractInfoMap(contract).get(PdfFieldTemporary.FULL_TIME_END_TIME.toString()))){
					setPdfFieldValue(PdfFieldTemporary.FULL_TIME_END_TIME.getValue(), getContractInfoMap(contract).get(PdfFieldTemporary.FULL_TIME_END_TIME.toString()));
				}
			} else if(code.getValue().startsWith("2") || code.getValue().startsWith("5")){
				setPdfFieldValue(PdfFieldTemporary.PARTIALLY_TIME.getValue(), "true");
				if(weekHours>0){
					setPdfFieldValue(PdfFieldTemporary.PARTIALLY_TIME_HOURS.getValue(), String.valueOf(weekHours));
					setPdfFieldValue(PdfFieldTemporary.PARTIALLY_TIME_WEEKLY.getValue(), "true");
				} else if(contrata!=null){
					if(contrata.getHorasJornada()!=null){
						setPdfFieldValue(PdfFieldTemporary.PARTIALLY_TIME_HOURS.getValue(), String.valueOf(Integer.parseInt(contrata.getHorasJornada())));
					}
					if(contrata.getTipoJornada()==TEQPTIEM.TEQPTIEM_D){
						setPdfFieldValue(PdfFieldTemporary.PARTIALLY_TIME_DAYLY.getValue(), "true");
					} else if(contrata.getTipoJornada()==TEQPTIEM.TEQPTIEM_S){
						setPdfFieldValue(PdfFieldTemporary.PARTIALLY_TIME_WEEKLY.getValue(), "true");
					} else if(contrata.getTipoJornada()==TEQPTIEM.TEQPTIEM_M){
						setPdfFieldValue(PdfFieldTemporary.PARTIALLY_TIME_MONTHLY.getValue(), "true");
					} else if(contrata.getTipoJornada()==TEQPTIEM.TEQPTIEM_A){
						setPdfFieldValue(PdfFieldTemporary.PARTIALLY_TIME_YEARLY.getValue(), "true");
					}
				}
				if(StringUtils.isNotBlank(getContractInfoMap(contract).get(PdfFieldTemporary.PARTIALLY_TIME_JOB_LOWER_THAN.toString()))){
					setPdfFieldValue(PdfFieldTemporary.PARTIALLY_TIME_JOB_LOWER_THAN.getValue(), getContractInfoMap(contract).get(PdfFieldTemporary.PARTIALLY_TIME_JOB_LOWER_THAN.toString()));
				}
				if(StringUtils.isNotBlank(getContractInfoMap(contract).get(PdfFieldTemporary.PARTIALLY_TIME_JOB_DISTRIB.toString()))){
					setPdfFieldValue(PdfFieldTemporary.PARTIALLY_TIME_JOB_DISTRIB.getValue(), getContractInfoMap(contract).get(PdfFieldTemporary.PARTIALLY_TIME_JOB_DISTRIB.toString()));
				}
			}
			
			dateFormatter.applyPattern("dd/MM/yyyy");
			setPdfFieldValue(PdfFieldTemporary.START_DATE.getValue(), dateFormatter.format(contract.getStartDate()));
			String[] optionalEndDateCodes = {"401", "501", "410", "510", "403", "503", "540", "980", "990"};
			if(contract.getEndDate()!=null){
				setPdfFieldValue(PdfFieldTemporary.END_DATE.getValue(), dateFormatter.format(contract.getEndDate()));
			} else if(ArrayUtils.contains(optionalEndDateCodes, code.getValue())) {
				if(StringUtils.isNotBlank(getContractInfoMap(contract).get(PdfFieldTemporary.END_DATE_TEXT.toString()))){
					setPdfFieldValue(PdfFieldTemporary.END_DATE.getValue(), getContractInfoMap(contract).get(PdfFieldTemporary.END_DATE_TEXT.toString()));
				}
			}
			
			if(StringUtils.isNotBlank(getContractInfoMap(contract).get(PdfFieldTemporary.TRIAL_DURATION.toString()))){
				setPdfFieldValue(PdfFieldTemporary.TRIAL_DURATION.getValue(), getContractInfoMap(contract).get(PdfFieldTemporary.TRIAL_DURATION.toString()));
			}
			if(StringUtils.isNotBlank(getContractInfoMap(contract).get(PdfFieldTemporary.GREATER_DURATION_AGREEMENT_COL.toString()))){
				setPdfFieldValue(PdfFieldTemporary.GREATER_DURATION_AGREEMENT_COL.getValue(), getContractInfoMap(contract).get(PdfFieldTemporary.GREATER_DURATION_AGREEMENT_COL.toString()));
			}
			
			if(StringUtils.isNotBlank(getContractInfoMap(contract).get(PdfFieldTemporary.SALARY_AMOUNT.toString()))){
				setPdfFieldValue(PdfFieldTemporary.SALARY_AMOUNT.getValue(), getContractInfoMap(contract).get(PdfFieldTemporary.SALARY_AMOUNT.toString()));
			}
			if(StringUtils.isNotBlank(getContractInfoMap(contract).get(PdfFieldTemporary.SALARY_PERIOD.toString()))){
				setPdfFieldValue(PdfFieldTemporary.SALARY_PERIOD.getValue(), getContractInfoMap(contract).get(PdfFieldTemporary.SALARY_PERIOD.toString()));
			}
			if(StringUtils.isNotBlank(getContractInfoMap(contract).get(PdfFieldTemporary.SALARY_CONCEPT.toString()))){
				setPdfFieldValue(PdfFieldTemporary.SALARY_CONCEPT.getValue(), getContractInfoMap(contract).get(PdfFieldTemporary.SALARY_CONCEPT.toString()));
			}
			
			
			/*
			 * Contract page 2
			 */
			if(StringUtils.isNotBlank(getContractInfoMap(contract).get(PdfFieldTemporary.HOLIDAYS.toString()))){
				setPdfFieldValue(PdfFieldTemporary.HOLIDAYS.getValue(), getContractInfoMap(contract).get(PdfFieldTemporary.HOLIDAYS.toString()));
			}
			if(StringUtils.isNotBlank(getContractInfoMap(contract).get(PdfFieldTemporary.SEPE_MUNICIPALITY.toString()))){
				setPdfFieldValue(PdfFieldTemporary.SEPE_MUNICIPALITY.getValue(), getContractInfoMap(contract).get(PdfFieldTemporary.SEPE_MUNICIPALITY.toString()));
			}
			
			/*
			 *  OPTIONS PAGE
			 */
			ModelOption modelOption = ModelOption.valueOf(getContractInfoMap(contract).get(ContractVariable.CONTRACT_MODEL_OPTION.getValue()));
			
			if(modelOption == ModelOption.TEMPORARY_OPT1){
				setPdfFieldValue(PdfFieldTemporary.MAIN_OPT1_CHECK.getValue(),"true");
				setPdfFieldValue(PdfFieldTemporary.OPT1_OPTION_CHECK.getValue(),"true");
				if(code == ContractCode.C401){
					setPdfFieldValue(PdfFieldTemporary.OPT1_TC2_401.getValue(),"true");
				} else if(code == ContractCode.C501){
					setPdfFieldValue(PdfFieldTemporary.OPT1_TC2_501.getValue(),"true");
				}
				if(StringUtils.isNotBlank(getContractInfoMap(contract).get(PdfFieldTemporary.OPT1_WORK_DESCRIPTION1.toString()))){
					setPdfFieldValue(PdfFieldTemporary.OPT1_WORK_DESCRIPTION1.getValue(), getContractInfoMap(contract).get(PdfFieldTemporary.OPT1_WORK_DESCRIPTION1.toString()));
				}
				if(StringUtils.isNotBlank(getContractInfoMap(contract).get(PdfFieldTemporary.OPT1_WORK_DESCRIPTION2.toString()))){
					setPdfFieldValue(PdfFieldTemporary.OPT1_WORK_DESCRIPTION2.getValue(), getContractInfoMap(contract).get(PdfFieldTemporary.OPT1_WORK_DESCRIPTION2.toString()));
				}
			} else if(modelOption == ModelOption.TEMPORARY_OPT2){
				setPdfFieldValue(PdfFieldTemporary.MAIN_OPT2_CHECK.getValue(),"true");
				setPdfFieldValue(PdfFieldTemporary.OPT2_OPTION_CHECK.getValue(),"true");
				if(code == ContractCode.C402){
					setPdfFieldValue(PdfFieldTemporary.OPT2_TC2_402.getValue(),"true");
				} else if(code == ContractCode.C502){
					setPdfFieldValue(PdfFieldTemporary.OPT2_TC2_502.getValue(),"true");
				}
				if(StringUtils.isNotBlank(getContractInfoMap(contract).get(PdfFieldTemporary.OPT2_WORK_DESCRIPTION1.toString()))){
					setPdfFieldValue(PdfFieldTemporary.OPT2_WORK_DESCRIPTION1.getValue(), getContractInfoMap(contract).get(PdfFieldTemporary.OPT2_WORK_DESCRIPTION1.toString()));
				}
				if(StringUtils.isNotBlank(getContractInfoMap(contract).get(PdfFieldTemporary.OPT2_WORK_DESCRIPTION2.toString()))){
					setPdfFieldValue(PdfFieldTemporary.OPT2_WORK_DESCRIPTION2.getValue(), getContractInfoMap(contract).get(PdfFieldTemporary.OPT2_WORK_DESCRIPTION2.toString()));
				}
			} else if(modelOption == ModelOption.TEMPORARY_OPT3){
				setPdfFieldValue(PdfFieldTemporary.MAIN_OPT3_CHECK.getValue(),"true");
				setPdfFieldValue(PdfFieldTemporary.OPT3_OPTION_CHECK.getValue(),"true");
				if(code == ContractCode.C410){
					setPdfFieldValue(PdfFieldTemporary.OPT3_TC2_410.getValue(),"true");
				} else if(code == ContractCode.C510){
					setPdfFieldValue(PdfFieldTemporary.OPT3_TC2_510.getValue(),"true");
				}
				if(StringUtils.isNotBlank(getContractInfoMap(contract).get(PdfFieldTemporary.OPT3_REPLACED_WORKER_NAME.toString()))){
					setPdfFieldValue(PdfFieldTemporary.OPT3_REPLACED_WORKER_NAME.getValue(), getContractInfoMap(contract).get(PdfFieldTemporary.OPT3_REPLACED_WORKER_NAME.toString()));
				}
				
				if(contrata!=null){					
					if(contrata.getCausaInterinidad()==TEIINTER.TEIINTER_A){
//						TEIINTER_A( "A", "TRABAJADOR CON DERECHO RESERVA DE PUESTO", "19800315", "0" ),
						setPdfFieldValue(PdfFieldTemporary.OPT3_CAUSE1.getValue(),"true");
					}else if(contrata.getCausaInterinidad()==TEIINTER.TEIINTER_B){
//						TEIINTER_B( "B", "TRABAJADOR POR MATERNIDAD SIN BONIFICACION DE CUOTAS", "19800315", "0" ),
						setPdfFieldValue(PdfFieldTemporary.OPT3_CAUSE2.getValue(),"true");
					}else if(contrata.getCausaInterinidad()==TEIINTER.TEIINTER_C){
//						TEIINTER_C( "C", "EXCEDENCIA CUIDADO HIJO PERCEPTOR PRESTA", "19950501", "19991106" ),
					}else if(contrata.getCausaInterinidad()==TEIINTER.TEIINTER_D){
//						TEIINTER_D( "D", "EXCEDENCIA CUIDADO HIJO NO PERCEP.PRESTA", "19950501", "19970516" ),
					}else if(contrata.getCausaInterinidad()==TEIINTER.TEIINTER_E){
//						TEIINTER_E( "E", "TRABAJADOR PROCESO DE SELECCION/PROMOCION", "19800315", "0" ),
						setPdfFieldValue(PdfFieldTemporary.OPT3_CAUSE4.getValue(),"true");
					}else if(contrata.getCausaInterinidad()==TEIINTER.TEIINTER_F){
//						TEIINTER_F( "F", "MATERNIDAD CON BONIFICACION DE CUOTAS", "19980906", "0" ),
						setPdfFieldValue(PdfFieldTemporary.OPT3_CAUSE1_BONUS.getValue(),"true");
					}else if(contrata.getCausaInterinidad()==TEIINTER.TEIINTER_G){
//						TEIINTER_G( "G", "ADOPCION", "19980906", "0" ),
						setPdfFieldValue(PdfFieldTemporary.OPT3_CAUSE3_BONUS.getValue(),"true");
					}else if(contrata.getCausaInterinidad()==TEIINTER.TEIINTER_H){
//						TEIINTER_H( "H", "ACOGIMIENTO", "19980906", "0" ),
						setPdfFieldValue(PdfFieldTemporary.OPT3_CAUSE4_BONUS.getValue(),"true");
					}else if(contrata.getCausaInterinidad()==TEIINTER.TEIINTER_I){
//						TEIINTER_I( "I", "RIESGO DURANTE EMBARAZO", "19991107", "0" ),
						setPdfFieldValue(PdfFieldTemporary.OPT3_CAUSE5_BONUS.getValue(),"true");
					}else if(contrata.getCausaInterinidad()==TEIINTER.TEIINTER_J){
//						TEIINTER_J( "J", "TRABAJ.EN FORMACION POR PERCEPTOR PRESTA", "20020526", "0" ),
						setPdfFieldValue(PdfFieldTemporary.OPT3_CAUSE5.getValue(),"true");
					}else if(contrata.getCausaInterinidad()==TEIINTER.TEIINTER_K){
//						TEIINTER_K( "K", "MINUSVALIDOS DESEMPLEADOS POR MINUSV.INCAP.TEMP", "20021214", "0" ),
						setPdfFieldValue(PdfFieldTemporary.OPT3_CAUSE6.getValue(),"true");
					}else if(contrata.getCausaInterinidad()==TEIINTER.TEIINTER_L){
//						TEIINTER_L( "L", "EXCEDENCIA CUIDADO FAMILIAR PERCEP.PREST", "19991107", "0" ),
						setPdfFieldValue(PdfFieldTemporary.OPT3_CAUSE3.getValue(),"true");
					}else if(contrata.getCausaInterinidad()==TEIINTER.TEIINTER_M){
//						TEIINTER_M( "M", "SUSTITUCIÓN VÍCTIMAS VIOLENCIA DE GÉNERO", "20050128", "0" ),
						setPdfFieldValue(PdfFieldTemporary.OPT3_CAUSE7.getValue(),"true");
//						setPdfFieldValue(PdfFieldTemporary.OPT3_CAUSE7_OPT1.getValue(),"true");
//						setPdfFieldValue(PdfFieldTemporary.OPT3_CAUSE7_OPT2.getValue(),"true");
					}else if(contrata.getCausaInterinidad()==TEIINTER.TEIINTER_N){
//						TEIINTER_N( "N", "PATERNIDAD", "20070324", "0" ),
						setPdfFieldValue(PdfFieldTemporary.OPT3_CAUSE2_BONUS.getValue(),"true");
					}else if(contrata.getCausaInterinidad()==TEIINTER.TEIINTER_O){
//						TEIINTER_O( "O", "RIESGO DURANTE LA LACTANCIA NATURAL", "20070324", "0" ),
						setPdfFieldValue(PdfFieldTemporary.OPT3_CAUSE6_BONUS.getValue(),"true");
					}
				}
			} else if(modelOption == ModelOption.TEMPORARY_OPT4){
				setPdfFieldValue(PdfFieldTemporary.MAIN_OPT4_CHECK.getValue(),"true");
				setPdfFieldValue(PdfFieldTemporary.OPT4_OPTION_CHECK.getValue(),"true");
				// TODO
			} else if(modelOption == ModelOption.TEMPORARY_OPT5){
				setPdfFieldValue(PdfFieldTemporary.MAIN_OPT5_CHECK.getValue(),"true");
				setPdfFieldValue(PdfFieldTemporary.OPT5_OPTION_CHECK.getValue(),"true");
				// TODO
			} else if(modelOption == ModelOption.TEMPORARY_OPT6){
				setPdfFieldValue(PdfFieldTemporary.MAIN_OPT6_CHECK.getValue(),"true");
				setPdfFieldValue(PdfFieldTemporary.OPT6_OPTION_CHECK.getValue(),"true");
				// TODO
			} else if(modelOption == ModelOption.TEMPORARY_OPT7){
				setPdfFieldValue(PdfFieldTemporary.MAIN_OPT7_CHECK.getValue(),"true");
				setPdfFieldValue(PdfFieldTemporary.OPT7_OPTION_CHECK.getValue(),"true");
				// TODO
			} else if(modelOption == ModelOption.TEMPORARY_OPT8){
				setPdfFieldValue(PdfFieldTemporary.MAIN_OPT8_CHECK.getValue(),"true");
				setPdfFieldValue(PdfFieldTemporary.OPT8_OPTION_CHECK.getValue(),"true");
				if(contrata!=null){
					setPdfFieldValue(PdfFieldTemporary.OPT8_REDUCTION_PERCENT.getValue(),contrata.getPorcentajeJubilacionParcial()+"%");
				}
			} else if(modelOption == ModelOption.TEMPORARY_OPT9){
				setPdfFieldValue(PdfFieldTemporary.MAIN_OPT9_CHECK.getValue(),"true");
				setPdfFieldValue(PdfFieldTemporary.OPT9_OPTION_CHECK.getValue(),"true");
				// TODO
			} else if(modelOption == ModelOption.TEMPORARY_OPT10){
				setPdfFieldValue(PdfFieldTemporary.MAIN_OPT10_CHECK.getValue(),"true");
				setPdfFieldValue(PdfFieldTemporary.OPT10_OPTION_CHECK.getValue(),"true");
				if(code == ContractCode.C501){
					setPdfFieldValue(PdfFieldTemporary.OPT10_TC2_501.getValue(),"true");
				} else if(code == ContractCode.C502){
					setPdfFieldValue(PdfFieldTemporary.OPT10_TC2_502.getValue(),"true");
				}
				String key = getContractInfoMap(contract).get(PdfFieldTemporary.OPT10_REQUIREMENTS_OPT.toString());
				if(StringUtils.isNotBlank(key)){
					if(PdfFieldTemporary.OPT10_REQUIREMENTS_OPT1.toString().equals(key)){
						setPdfFieldValue(PdfFieldTemporary.OPT10_REQUIREMENTS_OPT1.getValue(), "true");
					} else if(PdfFieldTemporary.OPT10_REQUIREMENTS_OPT2.toString().equals(key)){
						setPdfFieldValue(PdfFieldTemporary.OPT10_REQUIREMENTS_OPT2.getValue(), "true");
					} else if(PdfFieldTemporary.OPT10_REQUIREMENTS_OPT3.toString().equals(key)){
						setPdfFieldValue(PdfFieldTemporary.OPT10_REQUIREMENTS_OPT3.getValue(), "true");
					} else if(PdfFieldTemporary.OPT10_REQUIREMENTS_OPT4.toString().equals(key)){
						setPdfFieldValue(PdfFieldTemporary.OPT10_REQUIREMENTS_OPT4.getValue(), "true");
					}
				}
				key = getContractInfoMap(contract).get(PdfFieldTemporary.OPT10_FORMATION_OPT.toString());
				if(StringUtils.isNotBlank(key)){
					if(PdfFieldTemporary.OPT10_FORMATION_OPT1.toString().equals(key)){
						setPdfFieldValue(PdfFieldTemporary.OPT10_FORMATION_OPT1.getValue(), "true");
					} else if(PdfFieldTemporary.OPT10_FORMATION_OPT2.toString().equals(key)){
						setPdfFieldValue(PdfFieldTemporary.OPT10_FORMATION_OPT2.getValue(), "true");
					}
				}
				key = getContractInfoMap(contract).get(PdfFieldTemporary.OPT10_FORMATION_TYPE_OPT.toString());
				if(StringUtils.isNotBlank(key)){
					if(PdfFieldTemporary.OPT10_FORMATION_TYPE_OPT1.toString().equals(key)){
						setPdfFieldValue(PdfFieldTemporary.OPT10_FORMATION_TYPE_OPT1.getValue(), "true");
						setPdfFieldValue(PdfFieldTemporary.OPT10_FORMATION_TYPE_OPT1_TEXT.getValue(), 
								getContractInfoMap(contract).get(PdfFieldTemporary.OPT10_FORMATION_TYPE_OPT1_TEXT.toString()));
					} else if(PdfFieldTemporary.OPT10_FORMATION_TYPE_OPT2.toString().equals(key)){
						setPdfFieldValue(PdfFieldTemporary.OPT10_FORMATION_TYPE_OPT2.getValue(), "true");
						setPdfFieldValue(PdfFieldTemporary.OPT10_FORMATION_TYPE_OPT2_TEXT.getValue(), 
								getContractInfoMap(contract).get(PdfFieldTemporary.OPT10_FORMATION_TYPE_OPT2_TEXT.toString()));
					}
				}				
				setPdfFieldValue(PdfFieldTemporary.OPT10_REDUCTION_OPT1.getValue(), "true");
			} else if(modelOption == ModelOption.TEMPORARY_OPT11){
				setPdfFieldValue(PdfFieldTemporary.MAIN_OPT11_CHECK.getValue(),"true");
				setPdfFieldValue(PdfFieldTemporary.OPT11_OPTION_CHECK.getValue(),"true");
				if(code == ContractCode.C401){
					setPdfFieldValue(PdfFieldTemporary.OPT11_FULL_TIME.getValue(),"true");
					setPdfFieldValue(PdfFieldTemporary.OPT11_TC2_401.getValue(),"true");
				} else if(code == ContractCode.C402){
					setPdfFieldValue(PdfFieldTemporary.OPT11_FULL_TIME.getValue(),"true");
					setPdfFieldValue(PdfFieldTemporary.OPT11_TC2_402.getValue(),"true");
				} else if(code == ContractCode.C410){
					setPdfFieldValue(PdfFieldTemporary.OPT11_FULL_TIME.getValue(),"true");
					setPdfFieldValue(PdfFieldTemporary.OPT11_TC2_410.getValue(),"true");
				} else if(code == ContractCode.C450){
					setPdfFieldValue(PdfFieldTemporary.OPT11_FULL_TIME.getValue(),"true");
					setPdfFieldValue(PdfFieldTemporary.OPT11_TC2_450.getValue(),"true");
				} else if(code == ContractCode.C990){
					setPdfFieldValue(PdfFieldTemporary.OPT11_FULL_TIME.getValue(),"true");
					setPdfFieldValue(PdfFieldTemporary.OPT11_TC2_990_FULL_TIME.getValue(),"true");
				} else if(code == ContractCode.C501){
					setPdfFieldValue(PdfFieldTemporary.OPT11_PARTIALLY_TIME.getValue(),"true");
					setPdfFieldValue(PdfFieldTemporary.OPT11_TC2_501.getValue(),"true");
				} else if(code == ContractCode.C502){
					setPdfFieldValue(PdfFieldTemporary.OPT11_PARTIALLY_TIME.getValue(),"true");
					setPdfFieldValue(PdfFieldTemporary.OPT11_TC2_502.getValue(),"true");
				} else if(code == ContractCode.C510){
					setPdfFieldValue(PdfFieldTemporary.OPT11_PARTIALLY_TIME.getValue(),"true");
					setPdfFieldValue(PdfFieldTemporary.OPT11_TC2_510.getValue(),"true");
				} else if(code == ContractCode.C550){
					setPdfFieldValue(PdfFieldTemporary.OPT11_PARTIALLY_TIME.getValue(),"true");
					setPdfFieldValue(PdfFieldTemporary.OPT11_TC2_550.getValue(),"true");
				} else if(code == ContractCode.C990){
					setPdfFieldValue(PdfFieldTemporary.OPT11_PARTIALLY_TIME.getValue(),"true");
					setPdfFieldValue(PdfFieldTemporary.OPT11_TC2_990_PARTIALLY_TIME.getValue(),"true");
				}				
				if(contrata!=null){
					if(contrata.getCodigoProgramaEmpleo() == TETPGMEM.TETPGMEM_01){
//						TETPGMEM_01( "01", "FOMENTO EMPLEO AGRARIO", null, null ),
						setPdfFieldValue(PdfFieldTemporary.OPT11_AGRICULTURAL_PROMOTION_CHECK.getValue(),"true");
					} else if(contrata.getCodigoProgramaEmpleo() == TETPGMEM.TETPGMEM_02){
//						TETPGMEM_02( "02", "INSERCIÓN CORPORACIÓN LOCAL", null, null ),
						setPdfFieldValue(PdfFieldTemporary.OPT11_AGRICULTURAL_PROMOTION_CHECK.getValue(),"true");
						setPdfFieldValue(PdfFieldTemporary.OPT11_EMPLOYER_LOCAL_CORPORATION_CHECK.getValue(),"true");
					} else if(contrata.getCodigoProgramaEmpleo() == TETPGMEM.TETPGMEM_03){
//						TETPGMEM_03( "03", "INSERCIÓN (ÓRGANOS ADMINISTRAC. ESTADO)", null, null ),
						setPdfFieldValue(PdfFieldTemporary.OPT11_AGRICULTURAL_PROMOTION_CHECK.getValue(),"true");
						setPdfFieldValue(PdfFieldTemporary.OPT11_EMPLOYER_STATE_ADMINISTRATION_CHECK.getValue(),"true");
					} else if(contrata.getCodigoProgramaEmpleo() == TETPGMEM.TETPGMEM_04){
//						TETPGMEM_04( "04", "INSERCIÓN (COMUNIDAD AUTÓNOMA)", null, null ),
						setPdfFieldValue(PdfFieldTemporary.OPT11_AGRICULTURAL_PROMOTION_CHECK.getValue(),"true");
						setPdfFieldValue(PdfFieldTemporary.OPT11_EMPLOYER_COMMUNITY_CHECK.getValue(),"true");
					} else if(contrata.getCodigoProgramaEmpleo() == TETPGMEM.TETPGMEM_05){
//						TETPGMEM_05( "05", "INSERCIÓN (ENTIDAD SIN ANIMO DE LUCRO)", null, null ),
						setPdfFieldValue(PdfFieldTemporary.OPT11_AGRICULTURAL_PROMOTION_CHECK.getValue(),"true");
						setPdfFieldValue(PdfFieldTemporary.OPT11_EMPLOYER_NONPROFIT_ENTITY_CHECK.getValue(),"true");
					} else if(contrata.getCodigoProgramaEmpleo() == TETPGMEM.TETPGMEM_06){
//						TETPGMEM_06( "06", "INSERCIÓN (UNIVERSIDAD)", null, null ),
						setPdfFieldValue(PdfFieldTemporary.OPT11_AGRICULTURAL_PROMOTION_CHECK.getValue(),"true");
						setPdfFieldValue(PdfFieldTemporary.OPT11_EMPLOYER_UNIVERSITY_CHECK.getValue(),"true");
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
						setPdfFieldValue(PdfFieldTemporary.OPT11_SOCIAL_INTERES_CHECK.getValue(),"true");
						setPdfFieldValue(PdfFieldTemporary.OPT11_EMPLOYER_LOCAL_CORPORATION_CHECK.getValue(),"true");
					} else if(contrata.getCodigoProgramaEmpleo() == TETPGMEM.TETPGMEM_13){
//						TETPGMEM_13( "13", "INTERES SOCIAL (ORGANOS AD.ESTADO O CCAA)", null, null ),
						setPdfFieldValue(PdfFieldTemporary.OPT11_SOCIAL_INTERES_CHECK.getValue(),"true");
						setPdfFieldValue(PdfFieldTemporary.OPT11_EMPLOYER_STATE_ADMINISTRATION_CHECK.getValue(),"true");
					} else if(contrata.getCodigoProgramaEmpleo() == TETPGMEM.TETPGMEM_14){
//						TETPGMEM_14( "14", "INTERES SOCIAL (COMUNIDAD AUTONOMA)", null, null ),
						setPdfFieldValue(PdfFieldTemporary.OPT11_SOCIAL_INTERES_CHECK.getValue(),"true");
						setPdfFieldValue(PdfFieldTemporary.OPT11_EMPLOYER_COMMUNITY_CHECK.getValue(),"true");
					} else if(contrata.getCodigoProgramaEmpleo() == TETPGMEM.TETPGMEM_15){
//						TETPGMEM_15( "15", "INTERES SOCIAL (ENTIDAD SIN ANIMO DE LUCRO)", null, null ),
						setPdfFieldValue(PdfFieldTemporary.OPT11_SOCIAL_INTERES_CHECK.getValue(),"true");
						setPdfFieldValue(PdfFieldTemporary.OPT11_EMPLOYER_NONPROFIT_ENTITY_CHECK.getValue(),"true");
					} else if(contrata.getCodigoProgramaEmpleo() == TETPGMEM.TETPGMEM_16){
//						TETPGMEM_16( "16", "INTERES SOCIAL (UNIVERSIDAD)", null, null ),
						setPdfFieldValue(PdfFieldTemporary.OPT11_SOCIAL_INTERES_CHECK.getValue(),"true");
						setPdfFieldValue(PdfFieldTemporary.OPT11_EMPLOYER_UNIVERSITY_CHECK.getValue(),"true");
					}
				}
				if(contract.getAgreementLevelCategory()!=null && contract.getAgreementLevelCategory().getLevel()!=null
					 && contract.getAgreementLevelCategory().getLevel().getAgreement()!=null){
					setPdfFieldValue(PdfFieldTemporary.OPT11_COLLECTIVE_AGREEMENT.getValue(),contract.getAgreementLevelCategory().getLevel().getAgreement().getDescription());
				}
			} else if(modelOption == ModelOption.TEMPORARY_OPT12){
				setPdfFieldValue(PdfFieldTemporary.MAIN_OPT12_CHECK.getValue(),"true");
				setPdfFieldValue(PdfFieldTemporary.OPT12_OPTION_CHECK.getValue(),"true");
				if(code == ContractCode.C401){
					setPdfFieldValue(PdfFieldTemporary.OPT12_FULL_TIME.getValue(),"true");
					setPdfFieldValue(PdfFieldTemporary.OPT12_TC2_401.getValue(),"true");
				} else if(code == ContractCode.C410){
					setPdfFieldValue(PdfFieldTemporary.OPT12_FULL_TIME.getValue(),"true");
					setPdfFieldValue(PdfFieldTemporary.OPT12_TC2_410.getValue(),"true");
				} else if(code == ContractCode.C501){
					setPdfFieldValue(PdfFieldTemporary.OPT12_PARTIALLY_TIME.getValue(),"true");
					setPdfFieldValue(PdfFieldTemporary.OPT12_TC2_501.getValue(),"true");
				} else if(code == ContractCode.C510){
					setPdfFieldValue(PdfFieldTemporary.OPT12_PARTIALLY_TIME.getValue(),"true");
					setPdfFieldValue(PdfFieldTemporary.OPT12_TC2_510.getValue(),"true");
				}
				String key = getContractInfoMap(contract).get(PdfFieldTemporary.OPT12_ONSITE_HOURS.toString());
				if(StringUtils.isNotBlank(key)){
					if(PdfFieldTemporary.OPT12_ONSITE_HOURS_YES.toString().equals(key)){
						setPdfFieldValue(PdfFieldTemporary.OPT12_ONSITE_HOURS_YES.getValue(), "true");
					} else if(PdfFieldTemporary.OPT12_ONSITE_HOURS_NO.toString().equals(key)){
						setPdfFieldValue(PdfFieldTemporary.OPT12_ONSITE_HOURS_NO.getValue(), "true");
					}
				}
				if(StringUtils.isNotBlank(getContractInfoMap(contract).get(PdfFieldTemporary.OPT12_ONSITE_WEEK_HOURS.toString()))){
					setPdfFieldValue(PdfFieldTemporary.OPT12_ONSITE_WEEK_HOURS.getValue(), getContractInfoMap(contract).get(PdfFieldTemporary.OPT12_ONSITE_WEEK_HOURS.toString()));
				}
				if(StringUtils.isNotBlank(getContractInfoMap(contract).get(PdfFieldTemporary.OPT12_ONSITE_HOURS_DISTRIB.toString()))){
					setPdfFieldValue(PdfFieldTemporary.OPT12_ONSITE_HOURS_DISTRIB.getValue(), getContractInfoMap(contract).get(PdfFieldTemporary.OPT12_ONSITE_HOURS_DISTRIB.toString()));
				}
				key = getContractInfoMap(contract).get(PdfFieldTemporary.OPT12_SALARY_OPT.toString());
				if(StringUtils.isNotBlank(key)){
					if(PdfFieldTemporary.OPT12_SALARY_OPT1.toString().equals(key)){
						setPdfFieldValue(PdfFieldTemporary.OPT12_SALARY_OPT1.getValue(), "true");
					} else if(PdfFieldTemporary.OPT12_SALARY_OPT2.toString().equals(key)){
						setPdfFieldValue(PdfFieldTemporary.OPT12_SALARY_OPT2.getValue(), "true");
					} else if(PdfFieldTemporary.OPT12_SALARY_OPT3.toString().equals(key)){
						setPdfFieldValue(PdfFieldTemporary.OPT12_SALARY_OPT3.getValue(), "true");
					}
				}
				key = getContractInfoMap(contract).get(PdfFieldTemporary.OPT12_OVERNIGHT.toString());
				if(StringUtils.isNotBlank(key)){
					if(PdfFieldTemporary.OPT12_OVERNIGHT_YES.toString().equals(key)){
						setPdfFieldValue(PdfFieldTemporary.OPT12_OVERNIGHT_YES.getValue(), "true");
					} else if(PdfFieldTemporary.OPT12_OVERNIGHT_NO.toString().equals(key)){
						setPdfFieldValue(PdfFieldTemporary.OPT12_OVERNIGHT_NO.getValue(), "true");
					}
				}
				if(StringUtils.isNotBlank(getContractInfoMap(contract).get(PdfFieldTemporary.OPT12_OVERNIGHT_WEEK_DAYS.toString()))){
					setPdfFieldValue(PdfFieldTemporary.OPT12_OVERNIGHT_WEEK_DAYS.getValue(), getContractInfoMap(contract).get(PdfFieldTemporary.OPT12_OVERNIGHT_WEEK_DAYS.toString()));
				}
			} else if(modelOption == ModelOption.TEMPORARY_OPT13){
				setPdfFieldValue(PdfFieldTemporary.MAIN_OPT13_CHECK.getValue(),"true");
				setPdfFieldValue(PdfFieldTemporary.OPT13_OPTION_CHECK.getValue(),"true");
				Boolean isBonused = contrata.getColectivoBonificacion()!=null && contrata.getColectivoBonificacion().getCode()!=null;
				if(code == ContractCode.C430){
					setPdfFieldValue(PdfFieldTemporary.OPT13_FULL_TIME.getValue(),"true");
					if( isBonused ){
						setPdfFieldValue(PdfFieldTemporary.OPT13_TC2_430_BONUS.getValue(),"true");
					} else {
						setPdfFieldValue(PdfFieldTemporary.OPT13_TC2_430_NO_BONUS.getValue(),"true");
					}
				} else if(code == ContractCode.C530){
					setPdfFieldValue(PdfFieldTemporary.OPT13_PARTIALLY_TIME.getValue(),"true");
					if( isBonused ){
						setPdfFieldValue(PdfFieldTemporary.OPT13_TC2_530_BONUS.getValue(),"true");
					} else {
						setPdfFieldValue(PdfFieldTemporary.OPT13_TC2_530_NO_BONUS.getValue(),"true");
					}
				}
				if(StringUtils.isNotBlank(getContractInfoMap(contract).get(PdfFieldTemporary.OPT13_DISABILITY_ISSUED_BY.toString()))){
					setPdfFieldValue(PdfFieldTemporary.OPT13_DISABILITY_ISSUED_BY.getValue(), getContractInfoMap(contract).get(PdfFieldTemporary.OPT13_DISABILITY_ISSUED_BY.toString()));
				}
				String key = getContractInfoMap(contract).get(PdfFieldTemporary.OPT13_DISABILITY.toString());
				if(StringUtils.isNotBlank(key)){
					if(PdfFieldTemporary.OPT13_DISABILITY_MAN_LT_45.toString().equals(key)){
						setPdfFieldValue(PdfFieldTemporary.OPT13_DISABILITY_MAN_LT_45.getValue(), "true");
					} else if(PdfFieldTemporary.OPT13_DISABILITY_MAN_GT_45.toString().equals(key)){
						setPdfFieldValue(PdfFieldTemporary.OPT13_DISABILITY_MAN_GT_45.getValue(), "true");
					} else if(PdfFieldTemporary.OPT13_DISABILITY_WOMAN_LT_45.toString().equals(key)){
						setPdfFieldValue(PdfFieldTemporary.OPT13_DISABILITY_WOMAN_LT_45.getValue(), "true");
					} else if(PdfFieldTemporary.OPT13_DISABILITY_WOMAN_GT_45.toString().equals(key)){
						setPdfFieldValue(PdfFieldTemporary.OPT13_DISABILITY_WOMAN_GT_45.getValue(), "true");
					}
				}
				key = getContractInfoMap(contract).get(PdfFieldTemporary.OPT13_SEVERE_DISABILITY.toString());
				if(StringUtils.isNotBlank(key)){
					if(PdfFieldTemporary.OPT13_SEVERE_DISABILITY_MAN_LT_45.toString().equals(key)){
						setPdfFieldValue(PdfFieldTemporary.OPT13_SEVERE_DISABILITY_MAN_LT_45.getValue(), "true");
					} else if(PdfFieldTemporary.OPT13_SEVERE_DISABILITY_MAN_GT_45.toString().equals(key)){
						setPdfFieldValue(PdfFieldTemporary.OPT13_SEVERE_DISABILITY_MAN_GT_45.getValue(), "true");
					} else if(PdfFieldTemporary.OPT13_SEVERE_DISABILITY_WOMAN_LT_45.toString().equals(key)){
						setPdfFieldValue(PdfFieldTemporary.OPT13_SEVERE_DISABILITY_WOMAN_LT_45.getValue(), "true");
					} else if(PdfFieldTemporary.OPT13_SEVERE_DISABILITY_WOMAN_GT_45.toString().equals(key)){
						setPdfFieldValue(PdfFieldTemporary.OPT13_SEVERE_DISABILITY_WOMAN_GT_45.getValue(), "true");
					}
				}
			} else if(modelOption == ModelOption.TEMPORARY_OPT14){
				setPdfFieldValue(PdfFieldTemporary.MAIN_OPT14_CHECK.getValue(),"true");
				setPdfFieldValue(PdfFieldTemporary.OPT14_OPTION_CHECK.getValue(),"true");
				if(code == ContractCode.C401){
					setPdfFieldValue(PdfFieldTemporary.OPT14_FULL_TIME.getValue(),"true");
					setPdfFieldValue(PdfFieldTemporary.OPT14_TC2_401.getValue(),"true");
				} else if(code == ContractCode.C402){
					setPdfFieldValue(PdfFieldTemporary.OPT14_FULL_TIME.getValue(),"true");
					setPdfFieldValue(PdfFieldTemporary.OPT14_TC2_402.getValue(),"true");
				} else if(code == ContractCode.C410){
					setPdfFieldValue(PdfFieldTemporary.OPT14_FULL_TIME.getValue(),"true");
					setPdfFieldValue(PdfFieldTemporary.OPT14_TC2_410.getValue(),"true");
				} else if(code == ContractCode.C430){
					setPdfFieldValue(PdfFieldTemporary.OPT14_FULL_TIME.getValue(),"true");
					setPdfFieldValue(PdfFieldTemporary.OPT14_TC2_430.getValue(),"true");
				} else if(code == ContractCode.C441){
					setPdfFieldValue(PdfFieldTemporary.OPT14_FULL_TIME.getValue(),"true");
					setPdfFieldValue(PdfFieldTemporary.OPT14_TC2_441.getValue(),"true");
				} else if(code == ContractCode.C990){
					setPdfFieldValue(PdfFieldTemporary.OPT14_FULL_TIME.getValue(),"true");
					setPdfFieldValue(PdfFieldTemporary.OPT14_TC2_990_FULL_TIME.getValue(),"true");
				} else if(code == ContractCode.C501){
					setPdfFieldValue(PdfFieldTemporary.OPT14_PARTIALLY_TIME.getValue(),"true");
					setPdfFieldValue(PdfFieldTemporary.OPT14_TC2_501.getValue(),"true");
				} else if(code == ContractCode.C502){
					setPdfFieldValue(PdfFieldTemporary.OPT14_PARTIALLY_TIME.getValue(),"true");
					setPdfFieldValue(PdfFieldTemporary.OPT14_TC2_502.getValue(),"true");
				} else if(code == ContractCode.C510){
					setPdfFieldValue(PdfFieldTemporary.OPT14_PARTIALLY_TIME.getValue(),"true");
					setPdfFieldValue(PdfFieldTemporary.OPT14_TC2_510.getValue(),"true");
				} else if(code == ContractCode.C530){
					setPdfFieldValue(PdfFieldTemporary.OPT14_PARTIALLY_TIME.getValue(),"true");
					setPdfFieldValue(PdfFieldTemporary.OPT14_TC2_530.getValue(),"true");
				} else if(code == ContractCode.C540){
					setPdfFieldValue(PdfFieldTemporary.OPT14_PARTIALLY_TIME.getValue(),"true");
					setPdfFieldValue(PdfFieldTemporary.OPT14_TC2_540.getValue(),"true");
				} else if(code == ContractCode.C541){
					setPdfFieldValue(PdfFieldTemporary.OPT14_PARTIALLY_TIME.getValue(),"true");
					setPdfFieldValue(PdfFieldTemporary.OPT14_TC2_541.getValue(),"true");
				} else if(code == ContractCode.C990){
					setPdfFieldValue(PdfFieldTemporary.OPT14_PARTIALLY_TIME.getValue(),"true");
					setPdfFieldValue(PdfFieldTemporary.OPT14_TC2_990_PARTIALLY_TIME.getValue(),"true");
				}
				if(StringUtils.isNotBlank(getContractInfoMap(contract).get(PdfFieldTemporary.OPT14_TRIAL_PERIOD.toString()))){
					setPdfFieldValue(PdfFieldTemporary.OPT14_TRIAL_PERIOD.getValue(), getContractInfoMap(contract).get(PdfFieldTemporary.OPT14_TRIAL_PERIOD.toString()));
				}
				if(StringUtils.isNotBlank(getContractInfoMap(contract).get(PdfFieldTemporary.OPT14_TRIAL_TERMS.toString()))){
					setPdfFieldValue(PdfFieldTemporary.OPT14_TRIAL_TERMS.getValue(), getContractInfoMap(contract).get(PdfFieldTemporary.OPT14_TRIAL_TERMS.toString()));
				}
				if(StringUtils.isNotBlank(getContractInfoMap(contract).get(PdfFieldTemporary.OPT14_PROFESSION.toString()))){
					setPdfFieldValue(PdfFieldTemporary.OPT14_PROFESSION.getValue(), getContractInfoMap(contract).get(PdfFieldTemporary.OPT14_PROFESSION.toString()));
				}
				if(StringUtils.isNotBlank(getContractInfoMap(contract).get(PdfFieldTemporary.OPT14_DISTANCE_ADJUSTMENT.toString()))){
					setPdfFieldValue(PdfFieldTemporary.OPT14_DISTANCE_ADJUSTMENT.getValue(), getContractInfoMap(contract).get(PdfFieldTemporary.OPT14_DISTANCE_ADJUSTMENT.toString()));
				}
				if(StringUtils.isNotBlank(getContractInfoMap(contract).get(PdfFieldTemporary.OPT14_DISTANCE_ADJUSTMENT_MORE.toString()))){
					setPdfFieldValue(PdfFieldTemporary.OPT14_DISTANCE_ADJUSTMENT_MORE.getValue(), getContractInfoMap(contract).get(PdfFieldTemporary.OPT14_DISTANCE_ADJUSTMENT_MORE.toString()));
				}
				if(contract.getAgreementLevelCategory()!=null && contract.getAgreementLevelCategory().getLevel()!=null
					 && contract.getAgreementLevelCategory().getLevel().getAgreement()!=null){
					setPdfFieldValue(PdfFieldTemporary.OPT14_COLLECTIVE_AGREEMENT.getValue(),contract.getAgreementLevelCategory().getLevel().getAgreement().getDescription());
				}
			} else if(modelOption == ModelOption.TEMPORARY_OPT15){
				setPdfFieldValue(PdfFieldTemporary.MAIN_OPT15_CHECK.getValue(),"true");
				setPdfFieldValue(PdfFieldTemporary.OPT15_OPTION_CHECK.getValue(),"true");
				// TODO
			} else if(modelOption == ModelOption.TEMPORARY_OPT16){
				setPdfFieldValue(PdfFieldTemporary.MAIN_OPT16_CHECK.getValue(),"true");
				setPdfFieldValue(PdfFieldTemporary.OPT16_OPTION_CHECK.getValue(),"true");
				// TODO
			} else if(modelOption == ModelOption.TEMPORARY_OPT17){
				setPdfFieldValue(PdfFieldTemporary.MAIN_OPT17_CHECK.getValue(),"true");
				setPdfFieldValue(PdfFieldTemporary.OPT17_OPTION_CHECK.getValue(),"true");
				// TODO
			} else if(modelOption == ModelOption.TEMPORARY_OPT18){
				setPdfFieldValue(PdfFieldTemporary.MAIN_OPT18_CHECK.getValue(),"true");
				setPdfFieldValue(PdfFieldTemporary.OPT18_OPTION_CHECK.getValue(),"true");
				// TODO
			}
			
		} catch (IOException e) {
			// do nothing
		} catch (ManagerBeanException e) {
			// do nothing
		}
	}

	
}
	
	