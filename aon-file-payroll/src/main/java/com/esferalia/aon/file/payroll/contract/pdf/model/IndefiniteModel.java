package com.esferalia.aon.file.payroll.contract.pdf.model;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;

import com.code.aon.AonVersion;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.geozone.GeoZone;
import com.code.aon.registry.RegistryAddress;
import com.esferalia.aon.file.payroll.contract.pdf.ModelOption;
import com.esferalia.aon.file.payroll.contract.pdf.PdfFieldIndefinite;
import com.esferalia.aon.file.payroll.contract.pdf.UnsupportedContractDocumentException;
import com.esferalia.aon.file.payroll.contrata.ContrataContratoParams;
import com.esferalia.aon.file.payroll.contrata.ContrataTransformacionesParams;
import com.esferalia.aon.file.payroll.contrata.IContrataParams;
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.payroll.ContractInfo.ContractVariable;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.payroll.enumeration.ContractCode;
import com.esferalia.aon.payroll.enumeration.contrata.TEQPTIEM;
import com.esferalia.aon.payroll.util.PayrollUtils;
import com.lowagie.text.pdf.PdfReader;



public class IndefiniteModel extends AbstractContractModel {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	public final static String MODEL_NAME = "Indefinido";
	
	public IndefiniteModel(Contract contract){
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
			setPdfFieldValue(PdfFieldIndefinite.ENTERPRISE_CIF.getValue(),contract.getWorkPlace().getEnterprise().getRegistry().getDocument());
			
			if(StringUtils.isNotBlank(getContractInfoMap(contract).get(PdfFieldIndefinite.ENTERPRISE_DIR_STAFF_NAME.toString()))){
				setPdfFieldValue(PdfFieldIndefinite.ENTERPRISE_DIR_STAFF_NAME.getValue(), getContractInfoMap(contract).get(PdfFieldIndefinite.ENTERPRISE_DIR_STAFF_NAME.toString()));
			}
			if(StringUtils.isNotBlank(getContractInfoMap(contract).get(PdfFieldIndefinite.ENTERPRISE_DIR_STAFF_NIF.toString()))){
				setPdfFieldValue(PdfFieldIndefinite.ENTERPRISE_DIR_STAFF_NIF.getValue(), getContractInfoMap(contract).get(PdfFieldIndefinite.ENTERPRISE_DIR_STAFF_NIF.toString()));
			}
			if(StringUtils.isNotBlank(getContractInfoMap(contract).get(PdfFieldIndefinite.ENTERPRISE_DIR_STAFF_CHARGE.toString()))){
				setPdfFieldValue(PdfFieldIndefinite.ENTERPRISE_DIR_STAFF_CHARGE.getValue(), getContractInfoMap(contract).get(PdfFieldIndefinite.ENTERPRISE_DIR_STAFF_CHARGE.toString()));
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
				if(StringUtils.isNotBlank(address.getMunicipalityCode())){
					ResourceBundle bundle = ResourceBundle.getBundle(MUNICIPALITIES_BUNDLE_BASE_NAME);
					setPdfFieldValue(PdfFieldIndefinite.ENTERPRISE_MUNICIPALITY.getValue(),bundle.getString(address.getMunicipalityCode()));
					setPdfFieldValue(PdfFieldIndefinite.ENTERPRISE_MUNICIPALITY_CODE1.getValue(),address.getMunicipalityCode().substring(0, 1));
					setPdfFieldValue(PdfFieldIndefinite.ENTERPRISE_MUNICIPALITY_CODE2.getValue(),address.getMunicipalityCode().substring(1, 2));
					setPdfFieldValue(PdfFieldIndefinite.ENTERPRISE_MUNICIPALITY_CODE3.getValue(),address.getMunicipalityCode().substring(2, 3));
					setPdfFieldValue(PdfFieldIndefinite.ENTERPRISE_MUNICIPALITY_CODE4.getValue(),address.getMunicipalityCode().substring(3, 4));
					setPdfFieldValue(PdfFieldIndefinite.ENTERPRISE_MUNICIPALITY_CODE5.getValue(),address.getMunicipalityCode().substring(4, 5));
				}
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
				if(PayrollUtils.getInstance().getRegimeCode(contract.getEnterpriseCCC())!=null){
					String quoteRegime = PayrollUtils.getInstance().getRegimeCode(contract.getEnterpriseCCC());
					if(StringUtils.isNotBlank(quoteRegime) && quoteRegime.length()>=4){
						setPdfFieldValue(PdfFieldIndefinite.CCC_REG1.getValue(),quoteRegime.substring(0, 1));
						setPdfFieldValue(PdfFieldIndefinite.CCC_REG2.getValue(),quoteRegime.substring(1, 2));
						setPdfFieldValue(PdfFieldIndefinite.CCC_REG3.getValue(),quoteRegime.substring(2, 3));
						setPdfFieldValue(PdfFieldIndefinite.CCC_REG4.getValue(),quoteRegime.substring(3, 4));
					}
				}					
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
				if(StringUtils.isNotBlank(address.getMunicipalityCode())){
					ResourceBundle bundle = ResourceBundle.getBundle(MUNICIPALITIES_BUNDLE_BASE_NAME);
					setPdfFieldValue(PdfFieldIndefinite.WORKPLACE_MUNICIPALITY.getValue(),bundle.getString(address.getMunicipalityCode()));
					setPdfFieldValue(PdfFieldIndefinite.WORKPLACE_MUNICIPALITY_CODE1.getValue(),address.getMunicipalityCode().substring(0, 1));
					setPdfFieldValue(PdfFieldIndefinite.WORKPLACE_MUNICIPALITY_CODE2.getValue(),address.getMunicipalityCode().substring(1, 2));
					setPdfFieldValue(PdfFieldIndefinite.WORKPLACE_MUNICIPALITY_CODE3.getValue(),address.getMunicipalityCode().substring(2, 3));
					setPdfFieldValue(PdfFieldIndefinite.WORKPLACE_MUNICIPALITY_CODE4.getValue(),address.getMunicipalityCode().substring(3, 4));
					setPdfFieldValue(PdfFieldIndefinite.WORKPLACE_MUNICIPALITY_CODE5.getValue(),address.getMunicipalityCode().substring(4, 5));
				}
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
				if(StringUtils.isNotBlank(address.getMunicipalityCode())){
					ResourceBundle bundle = ResourceBundle.getBundle(MUNICIPALITIES_BUNDLE_BASE_NAME);
					setPdfFieldValue(PdfFieldIndefinite.EMPLOYEE_ADDRESS_MUNICIPALITY.getValue(),bundle.getString(address.getMunicipalityCode()));
					setPdfFieldValue(PdfFieldIndefinite.EMPLOYEE_ADDRESS_MUNICIPALITY_CODE1.getValue(),address.getMunicipalityCode().substring(0, 1));
					setPdfFieldValue(PdfFieldIndefinite.EMPLOYEE_ADDRESS_MUNICIPALITY_CODE2.getValue(),address.getMunicipalityCode().substring(1, 2));
					setPdfFieldValue(PdfFieldIndefinite.EMPLOYEE_ADDRESS_MUNICIPALITY_CODE3.getValue(),address.getMunicipalityCode().substring(2, 3));
					setPdfFieldValue(PdfFieldIndefinite.EMPLOYEE_ADDRESS_MUNICIPALITY_CODE4.getValue(),address.getMunicipalityCode().substring(3, 4));
					setPdfFieldValue(PdfFieldIndefinite.EMPLOYEE_ADDRESS_MUNICIPALITY_CODE5.getValue(),address.getMunicipalityCode().substring(4, 5));
				}
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
				if(StringUtils.isNotBlank(getContractInfoMap(contract).get(PdfFieldIndefinite.LEGAL_REPRESENTATIVE_NAME.toString()))){
					setPdfFieldValue(PdfFieldIndefinite.LEGAL_REPRESENTATIVE_NAME.getValue(), getContractInfoMap(contract).get(PdfFieldIndefinite.LEGAL_REPRESENTATIVE_NAME.toString()));
				}
				if(StringUtils.isNotBlank(getContractInfoMap(contract).get(PdfFieldIndefinite.LEGAL_REPRESENTATIVE_NIF.toString()))){
					setPdfFieldValue(PdfFieldIndefinite.LEGAL_REPRESENTATIVE_NIF.getValue(), getContractInfoMap(contract).get(PdfFieldIndefinite.LEGAL_REPRESENTATIVE_NIF.toString()));
				}
				if(StringUtils.isNotBlank(getContractInfoMap(contract).get(PdfFieldIndefinite.LEGAL_REPRESENTATIVE_CHARGE.toString()))){
					setPdfFieldValue(PdfFieldIndefinite.LEGAL_REPRESENTATIVE_CHARGE.getValue(), getContractInfoMap(contract).get(PdfFieldIndefinite.LEGAL_REPRESENTATIVE_CHARGE.toString()));
				}
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
			
//			if(contract.getAgreementLevelCategory()!=null && contract.getAgreementLevelCategory().getId()!=null){
//				setPdfFieldValue(PdfFieldIndefinite.CATEGORY.getValue(), contract.getAgreementLevelCategory().getDescription());
//			} else {
//				setPdfFieldValue(PdfFieldIndefinite.CATEGORY.getValue(), contract.getCategoryDescription());
//			}
			
			if(StringUtils.isNotBlank(getContractInfoMap(contract).get(PdfFieldIndefinite.FUNCTIONS.toString()))){
				setPdfFieldValue(PdfFieldIndefinite.FUNCTIONS.getValue(), getContractInfoMap(contract).get(PdfFieldIndefinite.FUNCTIONS.toString()));
			}
			
			setPdfFieldValue(PdfFieldIndefinite.WORKPLACE_FULL_ADDRESS.getValue(), null);
			setPdfFieldValue(PdfFieldIndefinite.WORKPLACE_FULL_ADDRESS_MORE.getValue(), contract.getWorkPlace().getAddress().getFullAddress()+", "+contract.getWorkPlace().getAddress().getLocation());
			
			if(StringUtils.isNotBlank(getContractInfoMap(contract).get(PdfFieldIndefinite.EMPLOYEE_CONTRACT_DISTANCE.toString()))){
				setPdfFieldValue(PdfFieldIndefinite.EMPLOYEE_CONTRACT_DISTANCE.getValue(), getContractInfoMap(contract).get(PdfFieldIndefinite.EMPLOYEE_CONTRACT_DISTANCE.toString()));
			}
			if(StringUtils.isNotBlank(getContractInfoMap(contract).get(PdfFieldIndefinite.EMPLOYEE_CONTRACT_DIST_ADDR.toString()))){
				setPdfFieldValue(PdfFieldIndefinite.EMPLOYEE_CONTRACT_DIST_ADDR.getValue(), getContractInfoMap(contract).get(PdfFieldIndefinite.EMPLOYEE_CONTRACT_DIST_ADDR.toString()));
			}
			
			boolean isFullTimeDiscontinuous = false;
			if(code == ContractCode.C300 || code == ContractCode.C309 || code == ContractCode.C330 
					|| code == ContractCode.C339 || code == ContractCode.C350 || code == ContractCode.C389){
				if(StringUtils.isNotBlank(getContractInfoMap(contract).get(PdfFieldIndefinite.DISC_WORK_DESCRIPTION.toString()))){
					setPdfFieldValue(PdfFieldIndefinite.DISC_WORK_DESCRIPTION.getValue(), getContractInfoMap(contract).get(PdfFieldIndefinite.DISC_WORK_DESCRIPTION.toString()));
				}
				if(StringUtils.isNotBlank(getContractInfoMap(contract).get(PdfFieldIndefinite.DISC_WORK_ACTIVITY.toString()))){
					setPdfFieldValue(PdfFieldIndefinite.DISC_WORK_ACTIVITY.getValue(), getContractInfoMap(contract).get(PdfFieldIndefinite.DISC_WORK_ACTIVITY.toString()));
				}
				if(StringUtils.isNotBlank(getContractInfoMap(contract).get(PdfFieldIndefinite.DISC_WORK_DURATION.toString()))){
					setPdfFieldValue(PdfFieldIndefinite.DISC_WORK_DURATION.getValue(), getContractInfoMap(contract).get(PdfFieldIndefinite.DISC_WORK_DURATION.toString()));
				}
				if(StringUtils.isNotBlank(getContractInfoMap(contract).get(PdfFieldIndefinite.DISC_WORK_ESTIMATED_DURATION.toString()))){
					setPdfFieldValue(PdfFieldIndefinite.DISC_WORK_ESTIMATED_DURATION.getValue(), getContractInfoMap(contract).get(PdfFieldIndefinite.DISC_WORK_ESTIMATED_DURATION.toString()));
				}
				
				if(contract.getAgreementLevel()!=null){
					setPdfFieldValue(PdfFieldIndefinite.DISC_WORK_AGREEMENT_COLLECTIVE.getValue(), contract.getAgreementLevel().getAgreement().getDescription());
				}
				
//				if(contract.getAgreementLevelCategory()!=null && contract.getAgreementLevelCategory().getId()!=null){
//					setPdfFieldValue(PdfFieldIndefinite.DISC_WORK_AGREEMENT_COLLECTIVE.getValue(), contract.getAgreementLevelCategory().getLevel().getAgreement().getDescription());
//				}
				
				if(StringUtils.isNotBlank(getContractInfoMap(contract).get(PdfFieldIndefinite.DISC_WORK_ESTIM_JOURNAL_HOURS.toString()))){
					setPdfFieldValue(PdfFieldIndefinite.DISC_WORK_ESTIM_JOURNAL_HOURS.getValue(), getContractInfoMap(contract).get(PdfFieldIndefinite.DISC_WORK_ESTIM_JOURNAL_HOURS.toString()));
				}
				if(StringUtils.isNotBlank(getContractInfoMap(contract).get(PdfFieldIndefinite.DISC_WORK_ESTIM_JOURNAL_PERIOD.toString()))){
					setPdfFieldValue(PdfFieldIndefinite.DISC_WORK_ESTIM_JOURNAL_PERIOD.getValue(), getContractInfoMap(contract).get(PdfFieldIndefinite.DISC_WORK_ESTIM_JOURNAL_PERIOD.toString()));
				}
				if(StringUtils.isNotBlank(getContractInfoMap(contract).get(PdfFieldIndefinite.DISC_WORK_ESTIM_SCHEDULE.toString()))){
					setPdfFieldValue(PdfFieldIndefinite.DISC_WORK_ESTIM_SCHEDULE.getValue(), getContractInfoMap(contract).get(PdfFieldIndefinite.DISC_WORK_ESTIM_SCHEDULE.toString()));
				}
				
				String discontinuousWorkTime = getContractInfoMap(contract).get(PdfFieldIndefinite.DISC_AGREEMENT_COLLECTIVE.toString());
				boolean discontinuousPartTime = false;
				boolean discontinuousFullTime = false;
				if(StringUtils.isNotBlank(discontinuousWorkTime)){
					if(PdfFieldIndefinite.DISC_AGREEMENT_COLLECTIVE_YES.toString().equals(discontinuousWorkTime)){
						discontinuousPartTime = true;
					} else if(PdfFieldIndefinite.DISC_AGREEMENT_COLLECTIVE_NO.toString().equals(discontinuousWorkTime)){
						discontinuousFullTime = true;
					}
				}
				if(discontinuousPartTime){
					setPdfFieldValue(PdfFieldIndefinite.DISC_AGREEMENT_COLLECTIVE_YES.getValue(), "true");
				} else if(discontinuousFullTime){
					setPdfFieldValue(PdfFieldIndefinite.DISC_AGREEMENT_COLLECTIVE_NO.getValue(), "true");
				}
				isFullTimeDiscontinuous = code.getValue().startsWith("3") && discontinuousFullTime;
				
				// setPdfFieldValue(PdfFieldIndefinite.**UNKNOWN**.getValue(), null);
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
				weekHours = CommonUtil.round(weekHours);
			}
			
			
			if(code.getValue().startsWith("1") || code.getValue().startsWith("4") || isFullTimeDiscontinuous){
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
			} else if(code.getValue().startsWith("2") || code.getValue().startsWith("5") || !isFullTimeDiscontinuous){
				setPdfFieldValue(PdfFieldIndefinite.PARTIALLY_TIME.getValue(), "true");
				String weekJournalHours = getContractInfoMap(contract).get(PdfFieldIndefinite.PARTIALLY_TIME_HOURS.toString());
				if(weekJournalHours!=null){
					setPdfFieldValue(PdfFieldIndefinite.PARTIALLY_TIME_HOURS.getValue(), String.valueOf(weekJournalHours));
					setPdfFieldValue(PdfFieldIndefinite.PARTIALLY_TIME_WEEKLY.getValue(), "true");
				} else if(contrata!=null){
					if(contrata.getHorasJornada()!=null){
						Integer hours = Integer.parseInt(contrata.getHorasJornada());
						Integer mins = Integer.parseInt(contrata.getMinutosJornada());
						setPdfFieldValue(PdfFieldIndefinite.PARTIALLY_TIME_HOURS.getValue(), String.valueOf(hours + CommonUtil.round(new Double(mins)*1/60)));
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
				} else {
					setPdfFieldValue(PdfFieldIndefinite.PARTIALLY_TIME_HOURS.getValue(), String.valueOf(weekHours));
					setPdfFieldValue(PdfFieldIndefinite.PARTIALLY_TIME_WEEKLY.getValue(), "true");
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
			if(contract.getAgreementLevel()!=null){
				setPdfFieldValue(PdfFieldIndefinite.AGREEMENT_COLLECTIVE.getValue(), contract.getAgreementLevel().getAgreement().getDescription());
			}
//			if(contract.getAgreementLevelCategory()!=null && contract.getAgreementLevelCategory().getId()!=null){
//				setPdfFieldValue(PdfFieldIndefinite.AGREEMENT_COLLECTIVE.getValue(), contract.getAgreementLevelCategory().getLevel().getAgreement().getDescription());
//			}
			if(StringUtils.isNotBlank(getContractInfoMap(contract).get(PdfFieldIndefinite.SEPE_MUNICIPALITY.toString()))){
				setPdfFieldValue(PdfFieldIndefinite.SEPE_MUNICIPALITY.getValue(), getContractInfoMap(contract).get(PdfFieldIndefinite.SEPE_MUNICIPALITY.toString()));
			}
			
			
			/*
			 *  OPTIONS PAGE
			 */
			ModelOption modelOption = ModelOption.valueOf(getContractInfoMap(contract).get(ContractVariable.CONTRACT_MODEL_OPTION.getValue()));
			
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
			} else if(modelOption == ModelOption.INDEFINITE_OPT2){
				setPdfFieldValue(PdfFieldIndefinite.MAIN_OPT2_CHECK.getValue(),"true");
				setPdfFieldValue(PdfFieldIndefinite.OPT2_OPTION_CHECK.getValue(),"true");
				if(code == ContractCode.C130){
					setPdfFieldValue(PdfFieldIndefinite.OPT2_TC2_130.getValue(),"true");
				} else if(code == ContractCode.C230){
					setPdfFieldValue(PdfFieldIndefinite.OPT2_TC2_230.getValue(),"true");
				} else if(code == ContractCode.C330){
					setPdfFieldValue(PdfFieldIndefinite.OPT2_TC2_330.getValue(),"true");
				}
				String key = getContractInfoMap(contract).get(PdfFieldIndefinite.OPT2_SEPE_MUNICIPALITY.toString());
				if(StringUtils.isNotBlank(key)){
					setPdfFieldValue(PdfFieldIndefinite.OPT2_SEPE_MUNICIPALITY.getValue(), getContractInfoMap(contract).get(PdfFieldIndefinite.OPT2_SEPE_MUNICIPALITY.toString()));
				}
				key = getContractInfoMap(contract).get(PdfFieldIndefinite.OPT2_DISABILITY_NO_SEVERE.toString());
				if(StringUtils.isNotBlank(key)){
					if(PdfFieldIndefinite.OPT2_DISABILITY_NO_SEVERE_MAN_LT_45.toString().equals(key)){
						setPdfFieldValue(PdfFieldIndefinite.OPT2_DISABILITY_NO_SEVERE_MAN_LT_45.getValue(), "true");
					} else if(PdfFieldIndefinite.OPT2_DISABILITY_NO_SEVERE_MAN_GT_45.toString().equals(key)){
						setPdfFieldValue(PdfFieldIndefinite.OPT2_DISABILITY_NO_SEVERE_MAN_GT_45.getValue(), "true");
					} else if(PdfFieldIndefinite.OPT2_DISABILITY_NO_SEVERE_WOMAN_LT_45.toString().equals(key)){
						setPdfFieldValue(PdfFieldIndefinite.OPT2_DISABILITY_NO_SEVERE_WOMAN_LT_45.getValue(), "true");
					} else if(PdfFieldIndefinite.OPT2_DISABILITY_NO_SEVERE_WOMAN_GT_45.toString().equals(key)){
						setPdfFieldValue(PdfFieldIndefinite.OPT2_DISABILITY_NO_SEVERE_WOMAN_GT_45.getValue(), "true");
					}
				}
				key = getContractInfoMap(contract).get(PdfFieldIndefinite.OPT2_DISABILITY_SEVERE.toString());
				if(StringUtils.isNotBlank(key)){
					if(PdfFieldIndefinite.OPT2_DISABILITY_SEVERE_MAN_LT_45.toString().equals(key)){
						setPdfFieldValue(PdfFieldIndefinite.OPT2_DISABILITY_SEVERE_MAN_LT_45.getValue(), "true");
					} else if(PdfFieldIndefinite.OPT2_DISABILITY_SEVERE_MAN_GT_45.toString().equals(key)){
						setPdfFieldValue(PdfFieldIndefinite.OPT2_DISABILITY_SEVERE_MAN_GT_45.getValue(), "true");
					} else if(PdfFieldIndefinite.OPT2_DISABILITY_SEVERE_WOMAN_LT_45.toString().equals(key)){
						setPdfFieldValue(PdfFieldIndefinite.OPT2_DISABILITY_SEVERE_WOMAN_LT_45.getValue(), "true");
					} else if(PdfFieldIndefinite.OPT2_DISABILITY_SEVERE_WOMAN_GT_45.toString().equals(key)){
						setPdfFieldValue(PdfFieldIndefinite.OPT2_DISABILITY_SEVERE_WOMAN_GT_45.getValue(), "true");
					}
				}
				if(StringUtils.isNotBlank(getContractInfoMap(contract).get(PdfFieldIndefinite.OPT2_REDUCTION.toString()))){
					setPdfFieldValue(PdfFieldIndefinite.OPT2_REDUCTION.getValue(), getContractInfoMap(contract).get(PdfFieldIndefinite.OPT2_REDUCTION.toString()));
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
				String key = getContractInfoMap(contract).get(PdfFieldIndefinite.OPT6_AGE.toString());
				if(StringUtils.isNotBlank(key)){
					if(PdfFieldIndefinite.OPT6_LT_30_EMPLOYEE.toString().equals(key)){
						setPdfFieldValue(PdfFieldIndefinite.OPT6_LT_30_EMPLOYEE.getValue(), "true");
					} else if(PdfFieldIndefinite.OPT6_LT_35_EMPLOYEE_AND_HANDICAP_GTE_33.toString().equals(key)){
						setPdfFieldValue(PdfFieldIndefinite.OPT6_LT_35_EMPLOYEE_AND_HANDICAP_GTE_33.getValue(), "true");
					}
				}
				if(StringUtils.isNotBlank(getContractInfoMap(contract).get(PdfFieldIndefinite.OPT6_AGREEMENT_COLLECTIVE1.toString()))){
					setPdfFieldValue(PdfFieldIndefinite.OPT6_AGREEMENT_COLLECTIVE1.getValue(), getContractInfoMap(contract).get(PdfFieldIndefinite.OPT6_AGREEMENT_COLLECTIVE1.toString()));
				}
				if(StringUtils.isNotBlank(getContractInfoMap(contract).get(PdfFieldIndefinite.OPT6_AGREEMENT_COLLECTIVE2.toString()))){
					setPdfFieldValue(PdfFieldIndefinite.OPT6_AGREEMENT_COLLECTIVE2.getValue(), getContractInfoMap(contract).get(PdfFieldIndefinite.OPT6_AGREEMENT_COLLECTIVE2.toString()));
				}
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
				String key = getContractInfoMap(contract).get(PdfFieldIndefinite.OPT15_ONSITE_HOURS.toString());
				if(StringUtils.isNotBlank(key)){
					if(PdfFieldIndefinite.OPT15_ONSITE_HOURS_YES.toString().equals(key)){
						setPdfFieldValue(PdfFieldIndefinite.OPT15_ONSITE_HOURS_YES.getValue(), "true");
					} else if(PdfFieldIndefinite.OPT15_ONSITE_HOURS_NO.toString().equals(key)){
						setPdfFieldValue(PdfFieldIndefinite.OPT15_ONSITE_HOURS_NO.getValue(), "true");
					}
				}
				if(StringUtils.isNotBlank(getContractInfoMap(contract).get(PdfFieldIndefinite.OPT15_ONSITE_WEEK_HOURS.toString()))){
					setPdfFieldValue(PdfFieldIndefinite.OPT15_ONSITE_WEEK_HOURS.getValue(), getContractInfoMap(contract).get(PdfFieldIndefinite.OPT15_ONSITE_WEEK_HOURS.toString()));
				}
				if(StringUtils.isNotBlank(getContractInfoMap(contract).get(PdfFieldIndefinite.OPT15_ONSITE_HOURS_DISTRIBUTION.toString()))){
					setPdfFieldValue(PdfFieldIndefinite.OPT15_ONSITE_HOURS_DISTRIBUTION.getValue(), getContractInfoMap(contract).get(PdfFieldIndefinite.OPT15_ONSITE_HOURS_DISTRIBUTION.toString()));
				}
				key = getContractInfoMap(contract).get(PdfFieldIndefinite.OPT15_SALARY.toString());
				if(StringUtils.isNotBlank(key)){
					if(PdfFieldIndefinite.OPT15_SALARY_OPT1.toString().equals(key)){
						setPdfFieldValue(PdfFieldIndefinite.OPT15_SALARY_OPT1.getValue(), "true");
					} else if(PdfFieldIndefinite.OPT15_SALARY_OPT2.toString().equals(key)){
						setPdfFieldValue(PdfFieldIndefinite.OPT15_SALARY_OPT2.getValue(), "true");
					} else if(PdfFieldIndefinite.OPT15_SALARY_OPT3.toString().equals(key)){
						setPdfFieldValue(PdfFieldIndefinite.OPT15_SALARY_OPT3.getValue(), "true");
					}
				}
				key = getContractInfoMap(contract).get(PdfFieldIndefinite.OPT15_OVERNIGHT.toString());
				if(StringUtils.isNotBlank(key)){
					if(PdfFieldIndefinite.OPT15_OVERNIGHT_YES.toString().equals(key)){
						setPdfFieldValue(PdfFieldIndefinite.OPT15_OVERNIGHT_YES.getValue(), "true");
					} else if(PdfFieldIndefinite.OPT15_OVERNIGHT_NO.toString().equals(key)){
						setPdfFieldValue(PdfFieldIndefinite.OPT15_OVERNIGHT_NO.getValue(), "true");
					}
				}
				if(StringUtils.isNotBlank(getContractInfoMap(contract).get(PdfFieldIndefinite.OPT15_OVERNIGHT_WEEK_DAYS.toString()))){
					setPdfFieldValue(PdfFieldIndefinite.OPT15_OVERNIGHT_WEEK_DAYS.getValue(), getContractInfoMap(contract).get(PdfFieldIndefinite.OPT15_OVERNIGHT_WEEK_DAYS.toString()));
				}
			} else if(modelOption == ModelOption.INDEFINITE_OPT17){
				setPdfFieldValue(PdfFieldIndefinite.MAIN_OPT17_CHECK.getValue(),"true");
				setPdfFieldValue(PdfFieldIndefinite.OPT17_OPTION_CHECK.getValue(),"true");
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
					String key = getContractInfoMap(contract).get(PdfFieldIndefinite.OPT17_FULL_TIME_QUOTE_BONUS.toString());
					if(StringUtils.isNotBlank(key)){
						if(PdfFieldIndefinite.OPT17_FULL_TIME_QUOTE_BONUS_YES.toString().equals(key)){
							setPdfFieldValue(PdfFieldIndefinite.OPT17_FULL_TIME_QUOTE_BONUS_YES.getValue(), "true");
						} else if(PdfFieldIndefinite.OPT17_FULL_TIME_QUOTE_BONUS_NO.toString().equals(key)){
							setPdfFieldValue(PdfFieldIndefinite.OPT17_FULL_TIME_QUOTE_BONUS_NO.getValue(), "true");
						}
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
					String key = getContractInfoMap(contract).get(PdfFieldIndefinite.OPT17_PARTIALLY_TIME_QUOTE_BONUS.toString());
					if(StringUtils.isNotBlank(key)){
						if(PdfFieldIndefinite.OPT17_PARTIALLY_TIME_QUOTE_BONUS_YES.toString().equals(key)){
							setPdfFieldValue(PdfFieldIndefinite.OPT17_PARTIALLY_TIME_QUOTE_BONUS_YES.getValue(), "true");
						} else if(PdfFieldIndefinite.OPT17_PARTIALLY_TIME_QUOTE_BONUS_NO.toString().equals(key)){
							setPdfFieldValue(PdfFieldIndefinite.OPT17_PARTIALLY_TIME_QUOTE_BONUS_NO.getValue(), "true");
						}
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
					String key = getContractInfoMap(contract).get(PdfFieldIndefinite.OPT17_DISCONT_TIME_QUOTE_BONUS.toString());
					if(StringUtils.isNotBlank(key)){
						if(PdfFieldIndefinite.OPT17_DISCONT_TIME_QUOTE_BONUS_YES.toString().equals(key)){
							setPdfFieldValue(PdfFieldIndefinite.OPT17_DISCONT_TIME_QUOTE_BONUS_YES.getValue(), "true");
						} else if(PdfFieldIndefinite.OPT17_DISCONT_TIME_QUOTE_BONUS_NO.toString().equals(key)){
							setPdfFieldValue(PdfFieldIndefinite.OPT17_DISCONT_TIME_QUOTE_BONUS_NO.getValue(), "true");
						}
					}
				}
				
				
				if(StringUtils.isNotBlank(getContractInfoMap(contract).get(PdfFieldIndefinite.SEPE_MUNICIPALITY.toString()))){
					setPdfFieldValue(PdfFieldIndefinite.OPT17_SEPE_MUNICIPALITY.getValue(), getContractInfoMap(contract).get(PdfFieldIndefinite.SEPE_MUNICIPALITY.toString()));
				}				
				// TODO: transform date
				ContrataTransformacionesParams transformParams = null;
				for(IContrataParams params: contrataParams){
					if(params instanceof ContrataTransformacionesParams){
						transformParams = (ContrataTransformacionesParams) params;
					}
				}
				if(transformParams.getFechaInicio()!=null){
				}
				setPdfFieldValue(PdfFieldIndefinite.OPT17_TRANSFORM_DATE.getValue(),dateFormatter.format(contract.getStartDate()));
				
				
//				String key = getContractInfoMap(contract).get(PdfFieldIndefinite.OPT17_TRANSFORMATION_TO.toString());
//				if(StringUtils.isNotBlank(key)){
//					if(PdfFieldIndefinite.OPT17_IS_FULL_TIME.toString().equals(key)){
//						setPdfFieldValue(PdfFieldIndefinite.OPT17_IS_FULL_TIME.getValue(), "true");
//					} else if(PdfFieldIndefinite.OPT17_IS_FULL_TIME_DISCONTINUOUS.toString().equals(key)){
//						setPdfFieldValue(PdfFieldIndefinite.OPT17_IS_FULL_TIME_DISCONTINUOUS.getValue(), "true");
//					}
//				}
				if(code == ContractCode.C309 || code == ContractCode.C339 || code == ContractCode.C389){
					setPdfFieldValue(PdfFieldIndefinite.OPT17_IS_FULL_TIME_DISCONTINUOUS.getValue(), "true");
				} else {
					setPdfFieldValue(PdfFieldIndefinite.OPT17_IS_FULL_TIME.getValue(), "true");
				}
				
				// TODO source contract code
//				String contractCode = getContractDataMap(contract, null, null, false).get(ContextVariable.TC2.getName());
//				ContractCode sourceContractCode = transformParams.getSourceContractCode();
//				if(sourceContractCode!=null){
//					setPdfFieldValue(PdfFieldIndefinite.OPT17_SRC_CONTRACT.getValue(),sourceContractCode.getName(getLocale()));
//				}
				setPdfFieldValue(PdfFieldIndefinite.OPT17_SRC_CONTRACT.getValue(),"TEMPORAL");
				
				dateFormatter.applyPattern("dd/MM/yyyy");
				setPdfFieldValue(PdfFieldIndefinite.OPT17_SRC_CONTRACT_START_DATE.getValue(), dateFormatter.format(contract.getSeniorityDate()));
				
				if(StringUtils.isNotBlank(getContractInfoMap(contract).get(PdfFieldIndefinite.OPT17_SRC_CONTRACT_SEPE_MUNIC.toString()))){
					setPdfFieldValue(PdfFieldIndefinite.OPT17_SRC_CONTRACT_SEPE_MUNIC.getValue(), getContractInfoMap(contract).get(PdfFieldIndefinite.OPT17_SRC_CONTRACT_SEPE_MUNIC.toString()));
				}
				
				dateFormatter.applyPattern("dd/MM/yyyy");
				setPdfFieldValue(PdfFieldIndefinite.OPT17_SRC_CONTRACT_SEPE_DATE.getValue(), dateFormatter.format(contract.getSeniorityDate()));
				
				// TODO source contract sepe id
				if(StringUtils.isNotBlank(transformParams.getSourceContractSepeId())){
					setPdfFieldValue(PdfFieldIndefinite.OPT17_SRC_CONTRACT_SEPE_ID.getValue(),transformParams.getSourceContractSepeId());
				} else 
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
	
	