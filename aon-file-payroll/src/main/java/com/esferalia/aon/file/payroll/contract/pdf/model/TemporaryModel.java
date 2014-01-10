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
import com.esferalia.aon.payroll.enumeration.ContractCode;
import com.esferalia.aon.payroll.enumeration.contrata.TEQPTIEM;
import com.lowagie.text.pdf.PdfReader;



public class TemporaryModel extends AbstractContractModel {
	
	public final static String MODEL_NAME = "Temporal";
	
	public TemporaryModel(){
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
			setPdfFieldValue(TemporaryCommonFieldName.ENTERPRISE_CIF.getValue(),contract.getWorkPlace().getEnterprise().getRegistry().getDocument());
			RegistryDirStaff rDirStaff = obtainRegistryDirStaff(contract); 
			try {
				setPdfFieldValue(TemporaryCommonFieldName.ENTERPRISE_DIR_STAFF_NAME.getValue(),rDirStaff.getName());
				setPdfFieldValue(TemporaryCommonFieldName.ENTERPRISE_DIR_STAFF_NIF.getValue(),rDirStaff.getDocument());
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
				setPdfFieldValue(TemporaryCommonFieldName.ENTERPRISE_DIR_STAFF_CHARGE.getValue(),rDirStaddCharge);
			} catch (NullPointerException npe) {
				// do nothing
			}
			setPdfFieldValue(TemporaryCommonFieldName.ENTERPRISE_NAME.getValue(),contract.getWorkPlace().getEnterprise().getRegistry().getFullName());
			setPdfFieldValue(TemporaryCommonFieldName.ENTERPRISE_ADDRESS.getValue(),contract.getWorkPlace().getEnterprise().getRegistry().getDefaultAddress().getFullAddress());
			try {	
				setPdfFieldValue(TemporaryCommonFieldName.ENTERPRISE_COUNTRY.getValue(),contract.getWorkPlace().getEnterprise().getRegistry().getNationality().getName(getLocale()));
				setPdfFieldValue(TemporaryCommonFieldName.ENTERPRISE_COUNTRY_CODE1.getValue(),String.valueOf(contract.getWorkPlace().getEnterprise().getRegistry().getNationality().getIsoNum()).substring(0,1));
				setPdfFieldValue(TemporaryCommonFieldName.ENTERPRISE_COUNTRY_CODE2.getValue(),String.valueOf(contract.getWorkPlace().getEnterprise().getRegistry().getNationality().getIsoNum()).substring(1,2));
				setPdfFieldValue(TemporaryCommonFieldName.ENTERPRISE_COUNTRY_CODE3.getValue(),String.valueOf(contract.getWorkPlace().getEnterprise().getRegistry().getNationality().getIsoNum()).substring(2,3));
			} catch (StringIndexOutOfBoundsException aie) {
				// do nothing
			} catch (NullPointerException npe) {
				// do nothing
			}
			try {	
				RegistryAddress address = contract.getWorkPlace().getEnterprise().getRegistry().getDefaultAddress();
				ResourceBundle bundle = ResourceBundle.getBundle("com.esferalia.aon.payroll.i18n.municipalities");
				setPdfFieldValue(TemporaryCommonFieldName.ENTERPRISE_MUNICIPALITY.getValue(),bundle.getString(address.getMunicipalityCode()));
				setPdfFieldValue(TemporaryCommonFieldName.ENTERPRISE_MUNICIPALITY_CODE1.getValue(),address.getMunicipalityCode().substring(0, 1));
				setPdfFieldValue(TemporaryCommonFieldName.ENTERPRISE_MUNICIPALITY_CODE2.getValue(),address.getMunicipalityCode().substring(1, 2));
				setPdfFieldValue(TemporaryCommonFieldName.ENTERPRISE_MUNICIPALITY_CODE3.getValue(),address.getMunicipalityCode().substring(2, 3));
				setPdfFieldValue(TemporaryCommonFieldName.ENTERPRISE_MUNICIPALITY_CODE4.getValue(),address.getMunicipalityCode().substring(3, 4));
				setPdfFieldValue(TemporaryCommonFieldName.ENTERPRISE_MUNICIPALITY_CODE5.getValue(),address.getMunicipalityCode().substring(4, 5));
			} catch (StringIndexOutOfBoundsException aie) {
				// do nothing
			} catch (NullPointerException npe) {
				// do nothing
			}
			try {	
				setPdfFieldValue(TemporaryCommonFieldName.ENTERPRISE_ZIP1.getValue(),contract.getWorkPlace().getEnterprise().getRegistry().getDefaultAddress().getZip().substring(0, 1));
				setPdfFieldValue(TemporaryCommonFieldName.ENTERPRISE_ZIP2.getValue(),contract.getWorkPlace().getEnterprise().getRegistry().getDefaultAddress().getZip().substring(1, 2));
				setPdfFieldValue(TemporaryCommonFieldName.ENTERPRISE_ZIP3.getValue(),contract.getWorkPlace().getEnterprise().getRegistry().getDefaultAddress().getZip().substring(2, 3));
				setPdfFieldValue(TemporaryCommonFieldName.ENTERPRISE_ZIP4.getValue(),contract.getWorkPlace().getEnterprise().getRegistry().getDefaultAddress().getZip().substring(3, 4));
				setPdfFieldValue(TemporaryCommonFieldName.ENTERPRISE_ZIP5.getValue(),contract.getWorkPlace().getEnterprise().getRegistry().getDefaultAddress().getZip().substring(4, 5));
			} catch (StringIndexOutOfBoundsException aie) {
				// do nothing
			} catch (NullPointerException npe) {
				// do nothing
			}
			/* 
			 * Contract ccc fields
			 */
			if(contract.getEnterpriseCCC()!=null){
				setPdfFieldValue(TemporaryCommonFieldName.CCC_REG1.getValue(),contract.getEnterpriseCCC().getActivity().getQuoteRegimeCode().substring(0, 1));
				setPdfFieldValue(TemporaryCommonFieldName.CCC_REG2.getValue(),contract.getEnterpriseCCC().getActivity().getQuoteRegimeCode().substring(1, 2));
				setPdfFieldValue(TemporaryCommonFieldName.CCC_REG3.getValue(),contract.getEnterpriseCCC().getActivity().getQuoteRegimeCode().substring(2, 3));
				setPdfFieldValue(TemporaryCommonFieldName.CCC_REG4.getValue(),contract.getEnterpriseCCC().getActivity().getQuoteRegimeCode().substring(3, 4));
				if(contract.getEnterpriseCCC().getCcc().length()==11){
					setPdfFieldValue(TemporaryCommonFieldName.CCC_PROV1.getValue(),contract.getEnterpriseCCC().getCcc().substring(0, 1));
					setPdfFieldValue(TemporaryCommonFieldName.CCC_PROV2.getValue(),contract.getEnterpriseCCC().getCcc().substring(1, 2));
					setPdfFieldValue(TemporaryCommonFieldName.CCC_NISS.getValue(),contract.getEnterpriseCCC().getCcc().substring(2, 9));
					setPdfFieldValue(TemporaryCommonFieldName.CCC_CONTROL_DIGIT1.getValue(),contract.getEnterpriseCCC().getCcc().substring(9, 10));
					setPdfFieldValue(TemporaryCommonFieldName.CCC_CONTROL_DIGIT2.getValue(),contract.getEnterpriseCCC().getCcc().substring(10, 11));
				} else {
					setPdfFieldValue(TemporaryCommonFieldName.CCC_NISS.getValue(),contract.getEnterpriseCCC().getCcc());
				}
				setPdfFieldValue(TemporaryCommonFieldName.CCC_ACTIVITY.getValue(),contract.getEnterpriseCCC().getActivity().getDescription());
				setPdfFieldValue(TemporaryCommonFieldName.CCC_ACTIVITY_CODE1.getValue(),contract.getEnterpriseCCC().getActivity().getCnae2009().getCode().substring(0, 1));
				setPdfFieldValue(TemporaryCommonFieldName.CCC_ACTIVITY_CODE2.getValue(),contract.getEnterpriseCCC().getActivity().getCnae2009().getCode().substring(1, 2));
			}
			/* 
			 * Contract workplace fields
			 */
			try {
				GeoZone country = obtainCountry(contract.getWorkPlace().getAddress().getGeozone());
				setPdfFieldValue(TemporaryCommonFieldName.WORKPLACE_COUNTRY.getValue(),country.getName());
				setPdfFieldValue(TemporaryCommonFieldName.WORKPLACE_COUNTRY_CODE1.getValue(),String.valueOf(obtainCountryByValue(country).getIsoNum()).substring(0,1));
				setPdfFieldValue(TemporaryCommonFieldName.WORKPLACE_COUNTRY_CODE2.getValue(),String.valueOf(obtainCountryByValue(country).getIsoNum()).substring(1,2));
				setPdfFieldValue(TemporaryCommonFieldName.WORKPLACE_COUNTRY_CODE3.getValue(),String.valueOf(obtainCountryByValue(country).getIsoNum()).substring(2,3));
			} catch (StringIndexOutOfBoundsException aie) {
				// do nothing
			} catch (NullPointerException npe) {
				// do nothing
			}
			try {
				RegistryAddress address = contract.getWorkPlace().getAddress();
				ResourceBundle bundle = ResourceBundle.getBundle("com.esferalia.aon.payroll.i18n.municipalities");
				setPdfFieldValue(TemporaryCommonFieldName.WORKPLACE_MUNICIPALITY.getValue(),bundle.getString(address.getMunicipalityCode()));
				setPdfFieldValue(TemporaryCommonFieldName.WORKPLACE_MUNICIPALITY_CODE1.getValue(),address.getMunicipalityCode().substring(0, 1));
				setPdfFieldValue(TemporaryCommonFieldName.WORKPLACE_MUNICIPALITY_CODE2.getValue(),address.getMunicipalityCode().substring(1, 2));
				setPdfFieldValue(TemporaryCommonFieldName.WORKPLACE_MUNICIPALITY_CODE3.getValue(),address.getMunicipalityCode().substring(2, 3));
				setPdfFieldValue(TemporaryCommonFieldName.WORKPLACE_MUNICIPALITY_CODE4.getValue(),address.getMunicipalityCode().substring(3, 4));
				setPdfFieldValue(TemporaryCommonFieldName.WORKPLACE_MUNICIPALITY_CODE5.getValue(),address.getMunicipalityCode().substring(4, 5));
			} catch (StringIndexOutOfBoundsException aie) {
				// do nothing
			} catch (NullPointerException npe) {
				// do nothing
			}
			/*
			 * Contract employee fields
			 */
			setPdfFieldValue(TemporaryCommonFieldName.EMPLOYEE_NAME.getValue(),contract.getPerson().getFullName());
			setPdfFieldValue(TemporaryCommonFieldName.EMPLOYEE_NIF.getValue(),contract.getPerson().getRegistry().getDocument());
			if(contract.getPerson().getBirthDate()!=null){
				SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
				setPdfFieldValue(TemporaryCommonFieldName.EMPLOYEE_BIRTH_DATE.getValue(),formatter.format(contract.getPerson().getBirthDate()));
			}
			setPdfFieldValue(TemporaryCommonFieldName.EMPLOYEE_NSS.getValue(),contract.getPerson().getSocialSecurityNumber());
			if(contrata!=null && contrata.getNivelFormativo()!=null){
				setPdfFieldValue(TemporaryCommonFieldName.EMPLOYEE_FORMATION_LEVEL.getValue(),contrata.getNivelFormativo().getDescription());
				setPdfFieldValue(TemporaryCommonFieldName.EMPLOYEE_FORMATION_CODE1.getValue(),contrata.getNivelFormativo().getCode().substring(0, 1));
				setPdfFieldValue(TemporaryCommonFieldName.EMPLOYEE_FORMATION_CODE2.getValue(),contrata.getNivelFormativo().getCode().substring(1, 2));
			}
			try {
				setPdfFieldValue(TemporaryCommonFieldName.EMPLOYEE_COUNTRY.getValue(),String.valueOf(contract.getPerson().getRegistry().getNationality().getName(getLocale())));
				setPdfFieldValue(TemporaryCommonFieldName.EMPLOYEE_COUNTRY_CODE1.getValue(),String.valueOf(contract.getPerson().getRegistry().getNationality().getIsoNum()).substring(0,1));
				setPdfFieldValue(TemporaryCommonFieldName.EMPLOYEE_COUNTRY_CODE2.getValue(),String.valueOf(contract.getPerson().getRegistry().getNationality().getIsoNum()).substring(1,2));
				setPdfFieldValue(TemporaryCommonFieldName.EMPLOYEE_COUNTRY_CODE3.getValue(),String.valueOf(contract.getPerson().getRegistry().getNationality().getIsoNum()).substring(2,3));
			} catch (StringIndexOutOfBoundsException aie) {
				// do nothing
			} catch (NullPointerException npe) {
				// do nothing
			}
			try {
				RegistryAddress address = contract.getPerson().getRegistry().getDefaultAddress();
				ResourceBundle bundle = ResourceBundle.getBundle("com.esferalia.aon.payroll.i18n.municipalities");
				setPdfFieldValue(TemporaryCommonFieldName.EMPLOYEE_ADDRESS_MUNICIPALITY.getValue(),bundle.getString(address.getMunicipalityCode()));
				setPdfFieldValue(TemporaryCommonFieldName.EMPLOYEE_ADDRESS_MUNICIPALITY_CODE1.getValue(),address.getMunicipalityCode().substring(0, 1));
				setPdfFieldValue(TemporaryCommonFieldName.EMPLOYEE_ADDRESS_MUNICIPALITY_CODE2.getValue(),address.getMunicipalityCode().substring(1, 2));
				setPdfFieldValue(TemporaryCommonFieldName.EMPLOYEE_ADDRESS_MUNICIPALITY_CODE3.getValue(),address.getMunicipalityCode().substring(2, 3));
				setPdfFieldValue(TemporaryCommonFieldName.EMPLOYEE_ADDRESS_MUNICIPALITY_CODE4.getValue(),address.getMunicipalityCode().substring(3, 4));
				setPdfFieldValue(TemporaryCommonFieldName.EMPLOYEE_ADDRESS_MUNICIPALITY_CODE5.getValue(),address.getMunicipalityCode().substring(4, 5));
			} catch (StringIndexOutOfBoundsException aie) {
				// do nothing
			} catch (NullPointerException npe) {
				// do nothing
			}
			try {
				GeoZone country = obtainCountry(contract.getPerson().getRegistry().getDefaultAddress().getGeozone());
				setPdfFieldValue(TemporaryCommonFieldName.EMPLOYEE_ADDRESS_COUNTRY.getValue(),country.getName());
				setPdfFieldValue(TemporaryCommonFieldName.EMPLOYEE_ADDRESS_COUNTRY_CODE1.getValue(),String.valueOf(obtainCountryByValue(country).getIsoNum()).substring(0,1));
				setPdfFieldValue(TemporaryCommonFieldName.EMPLOYEE_ADDRESS_COUNTRY_CODE2.getValue(),String.valueOf(obtainCountryByValue(country).getIsoNum()).substring(1,2));
				setPdfFieldValue(TemporaryCommonFieldName.EMPLOYEE_ADDRESS_COUNTRY_CODE3.getValue(),String.valueOf(obtainCountryByValue(country).getIsoNum()).substring(2,3));
			} catch (StringIndexOutOfBoundsException aie) {
				// do nothing
			} catch (NullPointerException npe) {
				// do nothing
			}
			try {
				setPdfFieldValue(TemporaryCommonFieldName.LEGAL_REPRESENTATIVE_NAME.getValue(),"");
				setPdfFieldValue(TemporaryCommonFieldName.LEGAL_REPRESENTATIVE_NIF.getValue(),"");
				setPdfFieldValue(TemporaryCommonFieldName.LEGAL_REPRESENTATIVE_CHARGE.getValue(),"");
			} catch (StringIndexOutOfBoundsException aie) {
				// do nothing
			} catch (NullPointerException npe) {
				// do nothing
			}
			
			
			/*
			 * Contract page 1
			 */
			setPdfFieldValue(TemporaryCommonFieldName.PROFESSION.getValue(), contrata.getCno().getTitle());
			setPdfFieldValue(TemporaryCommonFieldName.CATEGORY.getValue(), contract.getCategoryDescription());
			setPdfFieldValue(TemporaryCommonFieldName.WORKPLACE_FULL_ADDRESS_MORE.getValue(), contract.getWorkPlace().getAddress().getFullAddress()+", "+contract.getWorkPlace().getAddress().getLocation());
			
			if(code == ContractCode.C401 || code == ContractCode.C402 || code == ContractCode.C410){
				setPdfFieldValue(TemporaryCommonFieldName.FULL_TIME.getValue(), "true");
				if(StringUtils.isNotBlank(getContractInfoMap(contract).get(TemporaryCommonFieldName.FULL_TIME_WEEK_HOURS.toString()))){
					setPdfFieldValue(TemporaryCommonFieldName.FULL_TIME_WEEK_HOURS.getValue(), getContractInfoMap(contract).get(TemporaryCommonFieldName.FULL_TIME_WEEK_HOURS.toString()));
				}
				if(StringUtils.isNotBlank(getContractInfoMap(contract).get(TemporaryCommonFieldName.FULL_TIME_START_TIME.toString()))){
					setPdfFieldValue(TemporaryCommonFieldName.FULL_TIME_START_TIME.getValue(), getContractInfoMap(contract).get(TemporaryCommonFieldName.FULL_TIME_START_TIME.toString()));
				}
				if(StringUtils.isNotBlank(getContractInfoMap(contract).get(TemporaryCommonFieldName.FULL_TIME_END_TIME.toString()))){
					setPdfFieldValue(TemporaryCommonFieldName.FULL_TIME_END_TIME.getValue(), getContractInfoMap(contract).get(TemporaryCommonFieldName.FULL_TIME_END_TIME.toString()));
				}
			} else if(code == ContractCode.C501 || code == ContractCode.C502 | code == ContractCode.C510 || code == ContractCode.C540){
				setPdfFieldValue(TemporaryCommonFieldName.PARTIALLY_TIME.getValue(), "true");
				setPdfFieldValue(TemporaryCommonFieldName.PARTIALLY_TIME_HOURS.getValue(), String.valueOf(Integer.parseInt(contrata.getHorasJornada())));
				if(contrata.getTipoJornada()==TEQPTIEM.TEQPTIEM_D){
					setPdfFieldValue(TemporaryCommonFieldName.PARTIALLY_TIME_DAYLY.getValue(), "true");
				} else if(contrata.getTipoJornada()==TEQPTIEM.TEQPTIEM_S){
					setPdfFieldValue(TemporaryCommonFieldName.PARTIALLY_TIME_WEEKLY.getValue(), "true");
				} else if(contrata.getTipoJornada()==TEQPTIEM.TEQPTIEM_M){
					setPdfFieldValue(TemporaryCommonFieldName.PARTIALLY_TIME_MONTHLY.getValue(), "true");
				} else if(contrata.getTipoJornada()==TEQPTIEM.TEQPTIEM_A){
					setPdfFieldValue(TemporaryCommonFieldName.PARTIALLY_TIME_YEARLY.getValue(), "true");
				}
			}
			
			dateFormatter.applyPattern("dd/MM/yyyy");
			setPdfFieldValue(TemporaryCommonFieldName.START_DATE.getValue(), dateFormatter.format(contract.getStartDate()));
			if(contract.getEndDate()!=null){
				setPdfFieldValue(TemporaryCommonFieldName.END_DATE.getValue(), dateFormatter.format(contract.getEndDate()));
			}
			
			if(StringUtils.isNotBlank(getContractInfoMap(contract).get(TemporaryCommonFieldName.TRIAL_DURATION.toString()))){
				setPdfFieldValue(TemporaryCommonFieldName.TRIAL_DURATION.getValue(), getContractInfoMap(contract).get(TemporaryCommonFieldName.TRIAL_DURATION.toString()));
			}
			setPdfFieldValue(TemporaryCommonFieldName.GRATER_DURATION_AGREEMENT_COLLECTIVE.getValue(), null);
			
			if(StringUtils.isNotBlank(getContractInfoMap(contract).get(TemporaryCommonFieldName.SALARY_AMOUNT.toString()))){
				setPdfFieldValue(TemporaryCommonFieldName.SALARY_AMOUNT.getValue(), getContractInfoMap(contract).get(TemporaryCommonFieldName.SALARY_AMOUNT.toString()));
			}
			if(StringUtils.isNotBlank(getContractInfoMap(contract).get(TemporaryCommonFieldName.SALARY_PERIOD.toString()))){
				setPdfFieldValue(TemporaryCommonFieldName.SALARY_PERIOD.getValue(), getContractInfoMap(contract).get(TemporaryCommonFieldName.SALARY_PERIOD.toString()));
			}
			if(StringUtils.isNotBlank(getContractInfoMap(contract).get(TemporaryCommonFieldName.SALARY_CONCEPT.toString()))){
				setPdfFieldValue(TemporaryCommonFieldName.SALARY_CONCEPT.getValue(), getContractInfoMap(contract).get(TemporaryCommonFieldName.SALARY_CONCEPT.toString()));
			}
			
			
			/*
			 * Contract page 2
			 */
			if(StringUtils.isNotBlank(getContractInfoMap(contract).get(TemporaryCommonFieldName.HOLIDAYS.toString()))){
				setPdfFieldValue(TemporaryCommonFieldName.HOLIDAYS.getValue(), getContractInfoMap(contract).get(TemporaryCommonFieldName.HOLIDAYS.toString()));
			}
			if(StringUtils.isNotBlank(getContractInfoMap(contract).get(TemporaryCommonFieldName.SEPE_MUNICIPALITY.toString()))){
				setPdfFieldValue(TemporaryCommonFieldName.SEPE_MUNICIPALITY.getValue(), getContractInfoMap(contract).get(TemporaryCommonFieldName.SEPE_MUNICIPALITY.toString()));
			}
			
			/*
			 *  OPTIONS PAGE
			 */
			modelOption = ModelOption.valueOf(getContractInfoMap(contract).get(ContractVariable.CONTRACT_MODEL_OPTION.getValue()));
			
			if(modelOption == ModelOption.TEMPORARY_OPT1){
				setPdfFieldValue(TemporaryOptionFieldName.MAIN_OPT1_CHECK.getValue(),"true");
				setPdfFieldValue(TemporaryOptionFieldName.OPT1_OPTION_CHECK.getValue(),"true");
				if(code == ContractCode.C401){
					setPdfFieldValue(TemporaryOptionFieldName.OPT1_TC2_401.getValue(),"true");
				} else if(code == ContractCode.C501){
					setPdfFieldValue(TemporaryOptionFieldName.OPT1_TC2_501.getValue(),"true");
				}
				// TODO: complete this
				setPdfFieldValue(TemporaryOptionFieldName.OPT1_WORK_DESCRIPTION1.getValue(),"");
				setPdfFieldValue(TemporaryOptionFieldName.OPT1_WORK_DESCRIPTION2.getValue(),"");
			} else if(modelOption == ModelOption.TEMPORARY_OPT2){
				setPdfFieldValue(TemporaryOptionFieldName.MAIN_OPT2_CHECK.getValue(),"true");
				setPdfFieldValue(TemporaryOptionFieldName.OPT2_OPTION_CHECK.getValue(),"true");
				if(code == ContractCode.C402){
					setPdfFieldValue(TemporaryOptionFieldName.OPT2_TC2_402.getValue(),"true");
				} else if(code == ContractCode.C502){
					setPdfFieldValue(TemporaryOptionFieldName.OPT2_TC2_502.getValue(),"true");
				}
				// TODO: complete this
				setPdfFieldValue(TemporaryOptionFieldName.OPT2_WORK_DESCRIPTION1.getValue(),"");
				setPdfFieldValue(TemporaryOptionFieldName.OPT2_WORK_DESCRIPTION2.getValue(),"");
			} else if(modelOption == ModelOption.TEMPORARY_OPT12){
				setPdfFieldValue(TemporaryOptionFieldName.MAIN_OPT12_CHECK.getValue(),"true");
				setPdfFieldValue(TemporaryOptionFieldName.OPT12_OPTION_CHECK.getValue(),"true");
				if(code == ContractCode.C401 || code == ContractCode.C410){
					setPdfFieldValue(TemporaryOptionFieldName.OPT12_FULL_TIME.getValue(),"true");
					if(code == ContractCode.C401){
						setPdfFieldValue(TemporaryOptionFieldName.OPT12_TC2_401.getValue(),"true");
					} else if(code == ContractCode.C410){
						setPdfFieldValue(TemporaryOptionFieldName.OPT12_TC2_410.getValue(),"true");
					}
				} else if(code == ContractCode.C501 || code == ContractCode.C510){
					setPdfFieldValue(TemporaryOptionFieldName.OPT12_PARTIALLY_TIME.getValue(),"true");
					if(code == ContractCode.C501){
						setPdfFieldValue(TemporaryOptionFieldName.OPT12_TC2_501.getValue(),"true");
					} else if(code == ContractCode.C510){
						setPdfFieldValue(TemporaryOptionFieldName.OPT12_TC2_510.getValue(),"true");
					}
				}
				// TODO: complete this
				setPdfFieldValue(TemporaryOptionFieldName.OPT12_ONSITE_HOURS_YES.getValue(),"");
				setPdfFieldValue(TemporaryOptionFieldName.OPT12_ONSITE_HOURS_NO.getValue(),"");
				setPdfFieldValue(TemporaryOptionFieldName.OPT12_ONSITE_WEEK_HOURS.getValue(),"");
				setPdfFieldValue(TemporaryOptionFieldName.OPT12_ONSITE_HOURS_DISTRIBUTION.getValue(),"");
				setPdfFieldValue(TemporaryOptionFieldName.OPT12_SALARY_OPT1.getValue(),"");
				setPdfFieldValue(TemporaryOptionFieldName.OPT12_SALARY_OPT2.getValue(),"");
				setPdfFieldValue(TemporaryOptionFieldName.OPT12_SALARY_OPT3.getValue(),"");
				setPdfFieldValue(TemporaryOptionFieldName.OPT12_OVERNIGHT_YES.getValue(),"");
				setPdfFieldValue(TemporaryOptionFieldName.OPT12_OVERNIGHT_NO.getValue(),"");
				setPdfFieldValue(TemporaryOptionFieldName.OPT12_OVERNIGHT_WEEK_DAYS.getValue(),"");
			}
			// TODO: complete other options
			
		} catch (IOException e) {
			// do nothing
		} catch (ManagerBeanException e) {
			// do nothing
		}
	}

	/*
	 * INNER CLASSES
	 */
	
	public enum TemporaryCommonFieldName implements IContractFieldName{
		/* 
		 * Contract enterprise fields
		 */
		ENTERPRISE_CIF("Textoasddfgghhjjkhfgf",Boolean.FALSE),
		ENTERPRISE_DIR_STAFF_NAME("Texto2",Boolean.FALSE),
		ENTERPRISE_DIR_STAFF_NIF("Texto3",Boolean.FALSE),
		ENTERPRISE_DIR_STAFF_CHARGE("Texto4",Boolean.FALSE),
		ENTERPRISE_NAME("Texto5",Boolean.FALSE),
		ENTERPRISE_ADDRESS("Texto6",Boolean.FALSE),
		ENTERPRISE_COUNTRY("Texto7",Boolean.FALSE),
		ENTERPRISE_COUNTRY_CODE1("Cifra1",Boolean.FALSE),
		ENTERPRISE_COUNTRY_CODE2("Cifra2",Boolean.FALSE),
		ENTERPRISE_COUNTRY_CODE3("Cifra3",Boolean.FALSE),
		ENTERPRISE_MUNICIPALITY("Texto8",Boolean.FALSE),
		ENTERPRISE_MUNICIPALITY_CODE1("Cifra4",Boolean.FALSE),
		ENTERPRISE_MUNICIPALITY_CODE2("Cifra5",Boolean.FALSE),
		ENTERPRISE_MUNICIPALITY_CODE3("Cifra6",Boolean.FALSE),
		ENTERPRISE_MUNICIPALITY_CODE4("Cifra7",Boolean.FALSE),
		ENTERPRISE_MUNICIPALITY_CODE5("Cifra8",Boolean.FALSE),
		ENTERPRISE_ZIP1("Cifra9",Boolean.FALSE),
		ENTERPRISE_ZIP2("Cifra10",Boolean.FALSE),
		ENTERPRISE_ZIP3("Cifra11",Boolean.FALSE),
		ENTERPRISE_ZIP4("Cifra12",Boolean.FALSE),
		ENTERPRISE_ZIP5("Cifra13",Boolean.FALSE),
		
		/* 
		 * Contract ccc fields
		 */
		CCC_REG1("Cifra14",Boolean.FALSE),
		CCC_REG2("Cifra15",Boolean.FALSE),
		CCC_REG3("Cifra16",Boolean.FALSE),
		CCC_REG4("Cifra17",Boolean.FALSE),
		CCC_PROV1("Cifra18",Boolean.FALSE),
		CCC_PROV2("Cifra19",Boolean.FALSE),
		CCC_NISS("Texto9",Boolean.FALSE),
		CCC_CONTROL_DIGIT1("Cifra20",Boolean.FALSE),
		CCC_CONTROL_DIGIT2("Cifra21",Boolean.FALSE),
		CCC_ACTIVITY("Texto10",Boolean.FALSE),
		CCC_ACTIVITY_CODE1("Cifra22",Boolean.FALSE),
		CCC_ACTIVITY_CODE2("Cifra23",Boolean.FALSE),
		
		/* 
		 * Contract workplace fields
		 */
		WORKPLACE_COUNTRY("Texto11",Boolean.FALSE),
		WORKPLACE_COUNTRY_CODE1("Cifra24",Boolean.FALSE),
		WORKPLACE_COUNTRY_CODE2("Cifra25",Boolean.FALSE),
		WORKPLACE_COUNTRY_CODE3("Cifra26",Boolean.FALSE),
		WORKPLACE_MUNICIPALITY("Texto12",Boolean.FALSE),
		WORKPLACE_MUNICIPALITY_CODE1("Cifra27",Boolean.FALSE),
		WORKPLACE_MUNICIPALITY_CODE2("Cifra28",Boolean.FALSE),
		WORKPLACE_MUNICIPALITY_CODE3("Cifra29",Boolean.FALSE),
		WORKPLACE_MUNICIPALITY_CODE4("Cifra30",Boolean.FALSE),
		WORKPLACE_MUNICIPALITY_CODE5("Cifra31",Boolean.FALSE),
		
		/*
		 * Contract employee fields
		 */
		EMPLOYEE_NAME("Texto13",Boolean.FALSE),
		EMPLOYEE_NIF("Texto14",Boolean.FALSE),
		EMPLOYEE_BIRTH_DATE("Texto15",Boolean.FALSE),
		EMPLOYEE_NSS("Texto16",Boolean.FALSE),
		EMPLOYEE_FORMATION_LEVEL("Texto17",Boolean.FALSE),
		EMPLOYEE_FORMATION_CODE1("Cifra32",Boolean.FALSE),
		EMPLOYEE_FORMATION_CODE2("Cifra33",Boolean.FALSE),
		EMPLOYEE_COUNTRY("Texto18",Boolean.FALSE),
		EMPLOYEE_COUNTRY_CODE1("Cifra34",Boolean.FALSE),
		EMPLOYEE_COUNTRY_CODE2("Cifra35",Boolean.FALSE),
		EMPLOYEE_COUNTRY_CODE3("Cifra36",Boolean.FALSE),
		EMPLOYEE_ADDRESS_MUNICIPALITY("Texto19",Boolean.FALSE),
		EMPLOYEE_ADDRESS_MUNICIPALITY_CODE1("Cifra37",Boolean.FALSE),
		EMPLOYEE_ADDRESS_MUNICIPALITY_CODE2("Cifra38",Boolean.FALSE),
		EMPLOYEE_ADDRESS_MUNICIPALITY_CODE3("Cifra39",Boolean.FALSE),
		EMPLOYEE_ADDRESS_MUNICIPALITY_CODE4("Cifra40",Boolean.FALSE),
		EMPLOYEE_ADDRESS_MUNICIPALITY_CODE5("Cifra41",Boolean.FALSE),
		EMPLOYEE_ADDRESS_COUNTRY("Texto20",Boolean.FALSE),
		EMPLOYEE_ADDRESS_COUNTRY_CODE1("Cifra42",Boolean.FALSE),
		EMPLOYEE_ADDRESS_COUNTRY_CODE2("Cifra43",Boolean.FALSE),
		EMPLOYEE_ADDRESS_COUNTRY_CODE3("Cifra44",Boolean.FALSE),
		
		/*
		 * LEGAL REPRESENTATION
		 */
		LEGAL_REPRESENTATIVE_NAME("Renglon1",Boolean.FALSE),
		LEGAL_REPRESENTATIVE_NIF("Renglon2",Boolean.FALSE),
		LEGAL_REPRESENTATIVE_CHARGE("Renglon3",Boolean.FALSE),
		
		
		/*
		 * Contract page 1
		 */
		PROFESSION("Texto1",Boolean.FALSE),
		CATEGORY("Texto21",Boolean.FALSE),
		WORKPLACE_FULL_ADDRESS("Texto23",Boolean.FALSE),
		WORKPLACE_FULL_ADDRESS_MORE("Texto24",Boolean.FALSE),
		FULL_TIME("Casilla de verificación27",Boolean.FALSE),
		FULL_TIME_WEEK_HOURS("Texto28",Boolean.TRUE),
		FULL_TIME_START_TIME("Texto29",Boolean.TRUE),
		FULL_TIME_END_TIME("Texto30",Boolean.TRUE),
		PARTIALLY_TIME("Casilla de verificación31",Boolean.FALSE),
		PARTIALLY_TIME_HOURS("Texto32",Boolean.FALSE),
		PARTIALLY_TIME_DAYLY("Casilla de verificación33",Boolean.FALSE),
		PARTIALLY_TIME_WEEKLY("Casilla de verificación34",Boolean.FALSE),
		PARTIALLY_TIME_MONTHLY("Casilla de verificación35",Boolean.FALSE),
		PARTIALLY_TIME_YEARLY("Casilla de verificación36",Boolean.FALSE),
		START_DATE("Texto37",Boolean.FALSE),
		END_DATE("Texto38",Boolean.FALSE),
		TRIAL_DURATION("Texto39",Boolean.TRUE),
		GRATER_DURATION_AGREEMENT_COLLECTIVE("Casilla de verificación40",Boolean.FALSE),
		SALARY_AMOUNT("Texto42",Boolean.TRUE),
		SALARY_PERIOD("Texto43",Boolean.FALSE),
		SALARY_CONCEPT("Texto44",Boolean.TRUE),
		
		/*
		 * Contract page 2
		 */
		HOLIDAYS("Texto45",Boolean.TRUE),
		SEPE_MUNICIPALITY("Texto46",Boolean.TRUE),
		
		;
		
		private String value;
		private boolean overridable;
		
		private TemporaryCommonFieldName(String value, boolean overridable) {
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
	
	public enum TemporaryOptionFieldName implements IContractFieldName, IContractOptionField {

		/* Contract PAGE 3 */
		MAIN_OPT1_CHECK("Casilla de verificación47",Boolean.FALSE),
		MAIN_OPT2_CHECK("Casilla de verificación47q",Boolean.FALSE),
		MAIN_OPT3_CHECK("Casilla de verificación47w",Boolean.FALSE),
		MAIN_OPT4_CHECK("Casilla de verificación47e",Boolean.FALSE),
		MAIN_OPT5_CHECK("Casilla de verificación47r",Boolean.FALSE),
		MAIN_OPT6_CHECK("Casilla de verificación47t",Boolean.FALSE),
		MAIN_OPT7_CHECK("Casilla de verificación47y",Boolean.FALSE),
		MAIN_OPT8_CHECK("Casilla de verificación47u",Boolean.FALSE),
		MAIN_OPT9_CHECK("Casilla de verificación47i",Boolean.FALSE),
		MAIN_OPT10_CHECK("Casilla de verificación47o",Boolean.FALSE),
		MAIN_OPT11_CHECK("Casilla de verificación47p",Boolean.FALSE),
		MAIN_OPT12_CHECK("Casilla de verificación47a",Boolean.FALSE),
		MAIN_OPT13_CHECK("Casilla de verificación47s",Boolean.FALSE),
		MAIN_OPT14_CHECK("Casilla de verificación47d",Boolean.FALSE),
		MAIN_OPT15_CHECK("Casilla de verificación47f",Boolean.FALSE),
		MAIN_OPT16_CHECK("Casilla de verificación47g",Boolean.FALSE),
		MAIN_OPT17_CHECK("Casilla de verificación47h",Boolean.FALSE),
		MAIN_OPT18_CHECK("Casilla de verificación47j",Boolean.FALSE),
		
//		OBRA O SERVICIO DETERMINADO. ( pág.4 )
		OPT1_OPTION_CHECK("Casilla de verificación48",Boolean.FALSE),
		OPT1_TC2_401("Casilla de verificación481",Boolean.FALSE),
		OPT1_TC2_501("Casilla de verificación482",Boolean.FALSE),
		OPT1_WORK_DESCRIPTION1("Texto49",Boolean.FALSE),
		OPT1_WORK_DESCRIPTION2("Texto50",Boolean.FALSE),
//		EVENTUAL POR CIRCUNSTANCIAS DE LA PRODUCCIÓN. (pág.5 )
		OPT2_OPTION_CHECK("Casilla de verificación51",Boolean.FALSE),
		OPT2_TC2_402("Casilla de verificación51a",Boolean.FALSE),
		OPT2_TC2_502("Casilla de verificación51s",Boolean.FALSE),
		OPT2_WORK_DESCRIPTION1("Texto52",Boolean.FALSE),
		OPT2_WORK_DESCRIPTION2("Texto53",Boolean.FALSE),
//		INTERINIDAD. ( pág.6 )
		OPT3_OPTION_CHECK("",Boolean.FALSE),
//		PRIMER EMPLEO JOVEN. ( pág.7 )
		OPT4_OPTION_CHECK("",Boolean.FALSE),
//		DE TRABAJADORES EN SITUACIÓN DE EXCLUSIÓN SOCIAL, VÍCTIMAS DE VIOLENCIA DE GÉNERO, DOMÉSTICA O VÍCTIMA DE TERRORISMO. ( pág.8 )
		OPT5_OPTION_CHECK("",Boolean.FALSE),
//		DE TRABAJADORES EN SITUACIÓN DE EXCLUSIÓN SOCIAL POR EMPRESA DE INSERCIÓN. ( pág.9 )
		OPT6_OPTION_CHECK("",Boolean.FALSE),
//		DE TRABAJADORES MAYORES DE 52 AÑOS BENEFICIARIOS DE LOS SUBSIDIOS POR DESEMPLEO. ( pág.10 )
		OPT7_OPTION_CHECK("",Boolean.FALSE),
//		SITUACIÓN DE JUBILACIÓN PARCIAL. ( pág.11 )
		OPT8_OPTION_CHECK("",Boolean.FALSE),
//		RELEVO. ( pág.12 )
		OPT9_OPTION_CHECK("",Boolean.FALSE),
//		A TIEMPO PARCIAL CON VINCULACIÓN FORMATIVA. ( pág.13 )
		OPT10_OPTION_CHECK("",Boolean.FALSE),
//		DE TRABAJOS DE INTERÉS SOCIAL/FOMENTO DE EMPLEO AGRARIO. ( pág.14 )
		OPT11_OPTION_CHECK("",Boolean.FALSE),
//		DE TRABAJADORES DEL SERVICIO DEL HOGAR FAMILIAR. (pág.15 )
		OPT12_OPTION_CHECK("Casilla de verificación4",Boolean.FALSE),
		OPT12_FULL_TIME("Casilla de verificación12",Boolean.FALSE),
		OPT12_TC2_401("Casilla de verificación32q",Boolean.FALSE),
		OPT12_TC2_410("Casilla de verificación32",Boolean.FALSE),
		OPT12_PARTIALLY_TIME("Casilla de verificación13",Boolean.FALSE),
		OPT12_TC2_501("Casilla de verificación32e",Boolean.FALSE),
		OPT12_TC2_510("Casilla de verificación32w",Boolean.FALSE),
		OPT12_ONSITE_HOURS_YES("Casilla de verificación32r",Boolean.FALSE),
		OPT12_ONSITE_HOURS_NO("Casilla de verificación32t",Boolean.FALSE),
		OPT12_ONSITE_WEEK_HOURS("Texto33",Boolean.FALSE),
		OPT12_ONSITE_HOURS_DISTRIBUTION("Texto34",Boolean.FALSE),
		OPT12_SALARY_OPT1("Casilla de verificación32y",Boolean.TRUE),
		OPT12_SALARY_OPT2("Casilla de verificación32u",Boolean.TRUE),
		OPT12_SALARY_OPT3("Casilla de verificación32i",Boolean.TRUE),
		OPT12_OVERNIGHT_YES("Casilla de verificación32o",Boolean.TRUE),
		OPT12_OVERNIGHT_NO("Casilla de verificación32p",Boolean.TRUE),
		OPT12_OVERNIGHT_WEEK_DAYS("Texto35",Boolean.TRUE),		
//		DE PERSONAS CON DISCAPACIDAD. (pág.16 )
		OPT13_OPTION_CHECK("",Boolean.FALSE),
//		DE PERSONAS CON DISCAPACIDAD EN CENTROS ESPECIALES DE EMPLEO. (pág.17 )
		OPT14_OPTION_CHECK("",Boolean.FALSE),
//		DE INVESTIGADORES. ( pág.18 )
		OPT15_OPTION_CHECK("",Boolean.FALSE),
//		DE TRABAJADOES/AS PENADOS EN INSTITUCIONES PENITENCIARIAS. (pág.19 )
		OPT16_OPTION_CHECK("",Boolean.FALSE),
//		DE MENORES Y JÓVENES EN CENTROS DE MENORES. ( SOMETIDOS A MEDIDADAS DE INTERNAMIENTO PREVISTAS EN LA LEY ORGÁNICA 5/2000 DE 21 DE ENERO ). ( pág.20 )
		OPT17_OPTION_CHECK("",Boolean.FALSE),
//		OTRAS SITUACIONES. ( pág.21 )
		OPT18_OPTION_CHECK("",Boolean.FALSE),
		
		;
		
		private String value;
		private boolean overridable;
		
		private TemporaryOptionFieldName(String value, boolean overridable) {
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
	
	