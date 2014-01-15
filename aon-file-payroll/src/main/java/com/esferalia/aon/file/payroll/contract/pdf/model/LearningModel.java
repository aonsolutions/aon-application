package com.esferalia.aon.file.payroll.contract.pdf.model;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.company.WorkPlace;
import com.code.aon.geozone.GeoZone;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.RegistryAddress;
import com.code.aon.registry.RegistryDirStaff;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.file.payroll.contract.pdf.UnsupportedContractDocumentException;
import com.esferalia.aon.file.payroll.contrata.ContrataContratoParams;
import com.esferalia.aon.file.payroll.contrata.IContrataParams;
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.payroll.ContractData;
import com.esferalia.aon.payroll.ContractInfo.ContractVariable;
import com.esferalia.aon.payroll.PayrollWorkPlace;
import com.esferalia.aon.payroll.TrainingCourse;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.payroll.enumeration.ContractCode;
import com.esferalia.aon.payroll.enumeration.TrainingModality;
import com.lowagie.text.pdf.PdfReader;



public class LearningModel extends AbstractContractModel {
	
	public final static String MODEL_NAME = "Formacion";
	
	public LearningModel(){
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
			
			// print all field keys of the pdf document
//			for(String key: getPdfFieldsMap().keySet()){
//				System.out.println(key);
//			}
			
			/* 
			 * Contract enterprise fields
			 */
			setPdfFieldValue(LearningCommonFieldName.ENTERPRISE_CIF.getValue(),contract.getWorkPlace().getEnterprise().getRegistry().getDocument());
			RegistryDirStaff rDirStaff = obtainRegistryDirStaff(contract); 
			try {
				setPdfFieldValue(LearningCommonFieldName.ENTERPRISE_DIR_STAFF_NAME.getValue(),rDirStaff.getName());
				setPdfFieldValue(LearningCommonFieldName.ENTERPRISE_DIR_STAFF_NIF.getValue(),rDirStaff.getDocument());
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
				setPdfFieldValue(LearningCommonFieldName.ENTERPRISE_DIR_STAFF_CHARGE.getValue(),rDirStaddCharge);
			} catch (NullPointerException npe) {
				// do nothing
			}
			setPdfFieldValue(LearningCommonFieldName.ENTERPRISE_NAME.getValue(),contract.getWorkPlace().getEnterprise().getRegistry().getFullName());
			setPdfFieldValue(LearningCommonFieldName.ENTERPRISE_ADDRESS.getValue(),contract.getWorkPlace().getEnterprise().getRegistry().getDefaultAddress().getFullAddress());
			try {	
				setPdfFieldValue(LearningCommonFieldName.ENTERPRISE_COUNTRY.getValue(),contract.getWorkPlace().getEnterprise().getRegistry().getNationality().getName(getLocale()));
				setPdfFieldValue(LearningCommonFieldName.ENTERPRISE_COUNTRY_CODE1.getValue(),String.valueOf(contract.getWorkPlace().getEnterprise().getRegistry().getNationality().getIsoNum()).substring(0,1));
				setPdfFieldValue(LearningCommonFieldName.ENTERPRISE_COUNTRY_CODE2.getValue(),String.valueOf(contract.getWorkPlace().getEnterprise().getRegistry().getNationality().getIsoNum()).substring(1,2));
				setPdfFieldValue(LearningCommonFieldName.ENTERPRISE_COUNTRY_CODE3.getValue(),String.valueOf(contract.getWorkPlace().getEnterprise().getRegistry().getNationality().getIsoNum()).substring(2,3));
			} catch (StringIndexOutOfBoundsException aie) {
				// do nothing
			} catch (NullPointerException npe) {
				// do nothing
			}
			try {	
				RegistryAddress address = contract.getWorkPlace().getEnterprise().getRegistry().getDefaultAddress();
				ResourceBundle bundle = ResourceBundle.getBundle("com.esferalia.aon.payroll.i18n.municipalities");
				setPdfFieldValue(LearningCommonFieldName.ENTERPRISE_MUNICIPALITY.getValue(),bundle.getString(address.getMunicipalityCode()));
				setPdfFieldValue(LearningCommonFieldName.ENTERPRISE_MUNICIPALITY_CODE1.getValue(),address.getMunicipalityCode().substring(0, 1));
				setPdfFieldValue(LearningCommonFieldName.ENTERPRISE_MUNICIPALITY_CODE2.getValue(),address.getMunicipalityCode().substring(1, 2));
				setPdfFieldValue(LearningCommonFieldName.ENTERPRISE_MUNICIPALITY_CODE3.getValue(),address.getMunicipalityCode().substring(2, 3));
				setPdfFieldValue(LearningCommonFieldName.ENTERPRISE_MUNICIPALITY_CODE4.getValue(),address.getMunicipalityCode().substring(3, 4));
				setPdfFieldValue(LearningCommonFieldName.ENTERPRISE_MUNICIPALITY_CODE5.getValue(),address.getMunicipalityCode().substring(4, 5));
			} catch (StringIndexOutOfBoundsException aie) {
				// do nothing
			} catch (NullPointerException npe) {
				// do nothing
			}
			try {	
				setPdfFieldValue(LearningCommonFieldName.ENTERPRISE_ZIP1.getValue(),contract.getWorkPlace().getEnterprise().getRegistry().getDefaultAddress().getZip().substring(0, 1));
				setPdfFieldValue(LearningCommonFieldName.ENTERPRISE_ZIP2.getValue(),contract.getWorkPlace().getEnterprise().getRegistry().getDefaultAddress().getZip().substring(1, 2));
				setPdfFieldValue(LearningCommonFieldName.ENTERPRISE_ZIP3.getValue(),contract.getWorkPlace().getEnterprise().getRegistry().getDefaultAddress().getZip().substring(2, 3));
				setPdfFieldValue(LearningCommonFieldName.ENTERPRISE_ZIP4.getValue(),contract.getWorkPlace().getEnterprise().getRegistry().getDefaultAddress().getZip().substring(3, 4));
				setPdfFieldValue(LearningCommonFieldName.ENTERPRISE_ZIP5.getValue(),contract.getWorkPlace().getEnterprise().getRegistry().getDefaultAddress().getZip().substring(4, 5));
			} catch (StringIndexOutOfBoundsException aie) {
				// do nothing
			} catch (NullPointerException npe) {
				// do nothing
			}
			/* 
			 * Contract ccc fields
			 */
			if(contract.getEnterpriseCCC()!=null){
				setPdfFieldValue(LearningCommonFieldName.CCC_REG1.getValue(),contract.getEnterpriseCCC().getActivity().getQuoteRegimeCode().substring(0, 1));
				setPdfFieldValue(LearningCommonFieldName.CCC_REG2.getValue(),contract.getEnterpriseCCC().getActivity().getQuoteRegimeCode().substring(1, 2));
				setPdfFieldValue(LearningCommonFieldName.CCC_REG3.getValue(),contract.getEnterpriseCCC().getActivity().getQuoteRegimeCode().substring(2, 3));
				setPdfFieldValue(LearningCommonFieldName.CCC_REG4.getValue(),contract.getEnterpriseCCC().getActivity().getQuoteRegimeCode().substring(3, 4));
				if(contract.getEnterpriseCCC().getCcc().length()==11){
					setPdfFieldValue(LearningCommonFieldName.CCC_PROV1.getValue(),contract.getEnterpriseCCC().getCcc().substring(0, 1));
					setPdfFieldValue(LearningCommonFieldName.CCC_PROV2.getValue(),contract.getEnterpriseCCC().getCcc().substring(1, 2));
					setPdfFieldValue(LearningCommonFieldName.CCC_NISS.getValue(),contract.getEnterpriseCCC().getCcc().substring(2, 9));
					setPdfFieldValue(LearningCommonFieldName.CCC_CONTROL_DIGIT1.getValue(),contract.getEnterpriseCCC().getCcc().substring(9, 10));
					setPdfFieldValue(LearningCommonFieldName.CCC_CONTROL_DIGIT2.getValue(),contract.getEnterpriseCCC().getCcc().substring(10, 11));
				} else {
					setPdfFieldValue(LearningCommonFieldName.CCC_NISS.getValue(),contract.getEnterpriseCCC().getCcc());
				}
				setPdfFieldValue(LearningCommonFieldName.CCC_ACTIVITY.getValue(),contract.getEnterpriseCCC().getActivity().getDescription());
				setPdfFieldValue(LearningCommonFieldName.CCC_ACTIVITY_CODE1.getValue(),contract.getEnterpriseCCC().getActivity().getCnae2009().getCode().substring(0, 1));
				setPdfFieldValue(LearningCommonFieldName.CCC_ACTIVITY_CODE2.getValue(),contract.getEnterpriseCCC().getActivity().getCnae2009().getCode().substring(1, 2));
			}
			/* 
			 * Contract workplace fields
			 */
			try {
				GeoZone country = obtainCountry(contract.getWorkPlace().getAddress().getGeozone());
				setPdfFieldValue(LearningCommonFieldName.WORKPLACE_COUNTRY.getValue(),country.getName());
				setPdfFieldValue(LearningCommonFieldName.WORKPLACE_COUNTRY_CODE1.getValue(),String.valueOf(obtainCountryByValue(country).getIsoNum()).substring(0,1));
				setPdfFieldValue(LearningCommonFieldName.WORKPLACE_COUNTRY_CODE2.getValue(),String.valueOf(obtainCountryByValue(country).getIsoNum()).substring(1,2));
				setPdfFieldValue(LearningCommonFieldName.WORKPLACE_COUNTRY_CODE3.getValue(),String.valueOf(obtainCountryByValue(country).getIsoNum()).substring(2,3));
			} catch (StringIndexOutOfBoundsException aie) {
				// do nothing
			} catch (NullPointerException npe) {
				// do nothing
			}
			try {
				RegistryAddress address = contract.getWorkPlace().getAddress();
				ResourceBundle bundle = ResourceBundle.getBundle("com.esferalia.aon.payroll.i18n.municipalities");
				setPdfFieldValue(LearningCommonFieldName.WORKPLACE_MUNICIPALITY.getValue(),bundle.getString(address.getMunicipalityCode()));
				setPdfFieldValue(LearningCommonFieldName.WORKPLACE_MUNICIPALITY_CODE1.getValue(),address.getMunicipalityCode().substring(0, 1));
				setPdfFieldValue(LearningCommonFieldName.WORKPLACE_MUNICIPALITY_CODE2.getValue(),address.getMunicipalityCode().substring(1, 2));
				setPdfFieldValue(LearningCommonFieldName.WORKPLACE_MUNICIPALITY_CODE3.getValue(),address.getMunicipalityCode().substring(2, 3));
				setPdfFieldValue(LearningCommonFieldName.WORKPLACE_MUNICIPALITY_CODE4.getValue(),address.getMunicipalityCode().substring(3, 4));
				setPdfFieldValue(LearningCommonFieldName.WORKPLACE_MUNICIPALITY_CODE5.getValue(),address.getMunicipalityCode().substring(4, 5));
			} catch (StringIndexOutOfBoundsException aie) {
				// do nothing
			} catch (NullPointerException npe) {
				// do nothing
			}
			/*
			 * Contract employee fields
			 */
			setPdfFieldValue(LearningCommonFieldName.EMPLOYEE_NAME.getValue(),contract.getPerson().getFullName());
			setPdfFieldValue(LearningCommonFieldName.EMPLOYEE_NIF.getValue(),contract.getPerson().getRegistry().getDocument());
			if(contract.getPerson().getBirthDate()!=null){
				SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
				setPdfFieldValue(LearningCommonFieldName.EMPLOYEE_BIRTH_DATE.getValue(),formatter.format(contract.getPerson().getBirthDate()));
			}
			setPdfFieldValue(LearningCommonFieldName.EMPLOYEE_NSS.getValue(),contract.getPerson().getSocialSecurityNumber());
			if(contrata!=null && contrata.getNivelFormativo()!=null){
				setPdfFieldValue(LearningCommonFieldName.EMPLOYEE_FORMATION_LEVEL.getValue(),contrata.getNivelFormativo().getDescription());
				setPdfFieldValue(LearningCommonFieldName.EMPLOYEE_FORMATION_CODE1.getValue(),contrata.getNivelFormativo().getCode().substring(0, 1));
				setPdfFieldValue(LearningCommonFieldName.EMPLOYEE_FORMATION_CODE2.getValue(),contrata.getNivelFormativo().getCode().substring(1, 2));
			}
			try {
				setPdfFieldValue(LearningCommonFieldName.EMPLOYEE_COUNTRY.getValue(),String.valueOf(contract.getPerson().getRegistry().getNationality().getName(getLocale())));
				setPdfFieldValue(LearningCommonFieldName.EMPLOYEE_COUNTRY_CODE1.getValue(),String.valueOf(contract.getPerson().getRegistry().getNationality().getIsoNum()).substring(0,1));
				setPdfFieldValue(LearningCommonFieldName.EMPLOYEE_COUNTRY_CODE2.getValue(),String.valueOf(contract.getPerson().getRegistry().getNationality().getIsoNum()).substring(1,2));
				setPdfFieldValue(LearningCommonFieldName.EMPLOYEE_COUNTRY_CODE3.getValue(),String.valueOf(contract.getPerson().getRegistry().getNationality().getIsoNum()).substring(2,3));
			} catch (StringIndexOutOfBoundsException aie) {
				// do nothing
			} catch (NullPointerException npe) {
				// do nothing
			}
			try {
				RegistryAddress address = contract.getPerson().getRegistry().getDefaultAddress();
				ResourceBundle bundle = ResourceBundle.getBundle("com.esferalia.aon.payroll.i18n.municipalities");
				setPdfFieldValue(LearningCommonFieldName.EMPLOYEE_ADDRESS_MUNICIPALITY.getValue(),bundle.getString(address.getMunicipalityCode()));
				setPdfFieldValue(LearningCommonFieldName.EMPLOYEE_ADDRESS_MUNICIPALITY_CODE1.getValue(),address.getMunicipalityCode().substring(0, 1));
				setPdfFieldValue(LearningCommonFieldName.EMPLOYEE_ADDRESS_MUNICIPALITY_CODE2.getValue(),address.getMunicipalityCode().substring(1, 2));
				setPdfFieldValue(LearningCommonFieldName.EMPLOYEE_ADDRESS_MUNICIPALITY_CODE3.getValue(),address.getMunicipalityCode().substring(2, 3));
				setPdfFieldValue(LearningCommonFieldName.EMPLOYEE_ADDRESS_MUNICIPALITY_CODE4.getValue(),address.getMunicipalityCode().substring(3, 4));
				setPdfFieldValue(LearningCommonFieldName.EMPLOYEE_ADDRESS_MUNICIPALITY_CODE5.getValue(),address.getMunicipalityCode().substring(4, 5));
			} catch (StringIndexOutOfBoundsException aie) {
				// do nothing
			} catch (NullPointerException npe) {
				// do nothing
			}
			try {
				GeoZone country = obtainCountry(contract.getPerson().getRegistry().getDefaultAddress().getGeozone());
				setPdfFieldValue(LearningCommonFieldName.EMPLOYEE_ADDRESS_COUNTRY.getValue(),country.getName());
				setPdfFieldValue(LearningCommonFieldName.EMPLOYEE_ADDRESS_COUNTRY_CODE1.getValue(),String.valueOf(obtainCountryByValue(country).getIsoNum()).substring(0,1));
				setPdfFieldValue(LearningCommonFieldName.EMPLOYEE_ADDRESS_COUNTRY_CODE2.getValue(),String.valueOf(obtainCountryByValue(country).getIsoNum()).substring(1,2));
				setPdfFieldValue(LearningCommonFieldName.EMPLOYEE_ADDRESS_COUNTRY_CODE3.getValue(),String.valueOf(obtainCountryByValue(country).getIsoNum()).substring(2,3));
			} catch (StringIndexOutOfBoundsException aie) {
				// do nothing
			} catch (NullPointerException npe) {
				// do nothing
			}
			try {
				setPdfFieldValue(LearningCommonFieldName.LEGAL_REPRESENTATIVE_NAME.getValue(),"");
				setPdfFieldValue(LearningCommonFieldName.LEGAL_REPRESENTATIVE_NIF.getValue(),"");
				setPdfFieldValue(LearningCommonFieldName.LEGAL_REPRESENTATIVE_CHARGE.getValue(),"");
			} catch (StringIndexOutOfBoundsException aie) {
				// do nothing
			} catch (NullPointerException npe) {
				// do nothing
			}
			
			
			String subsidized = getContractDataMap(contract).get(ContextVariable.SUBSIDIZED.getName());
			if( subsidized!=null ){
				if( Boolean.parseBoolean(subsidized) ){
					setPdfFieldValue(LearningCommonFieldName.QUOTE_BONUS_YES.getValue(),"true");
				} else if( !Boolean.parseBoolean(subsidized) ){
					setPdfFieldValue(LearningCommonFieldName.QUOTE_BONUS_NO.getValue(),"true");
				}
			}
			setPdfFieldValue(LearningCommonFieldName.EMPLOYEE_OPT1.getValue(),"");
			setPdfFieldValue(LearningCommonFieldName.EMPLOYEE_OPT2.getValue(),"");
			setPdfFieldValue(LearningCommonFieldName.EMPLOYEE_OPT3.getValue(),"");
			
			TrainingCourse trainingCourse = obtainTrainingCourse(getContractDataMap(contract).get(ContractVariable.TRAINING_COURSE.getValue()));
			if(trainingCourse!=null){
				setPdfFieldValue(LearningCommonFieldName.ACTIVITY_EMPLOYEE_PROFFESION.getValue(),trainingCourse.getOccupationName());
				setPdfFieldValue(LearningCommonFieldName.ACTIVITY_EMPLOYEE_CATEGORY.getValue(),trainingCourse.getOccupationName());
				
				String cno = trainingCourse.getCNO().getCode();
				if( !StringUtils.isEmpty(cno) ){
					setPdfFieldValue(LearningCommonFieldName.CNO1.getValue(),cno.substring(0, 1));
					setPdfFieldValue(LearningCommonFieldName.CNO2.getValue(),cno.substring(1, 2));
					setPdfFieldValue(LearningCommonFieldName.CNO3.getValue(),cno.substring(2, 3));
					setPdfFieldValue(LearningCommonFieldName.CNO4.getValue(),cno.substring(3, 4));
				}
			}
			
			String wpFullAddress = contract.getWorkPlace().getAddress().getFullAddress();
			String wpGeozoneName = contract.getWorkPlace().getAddress().getGeozone().getName();
			setPdfFieldValue(LearningCommonFieldName.ACTIVITY_WORKPLACE_ADDRESS2.getValue(), wpFullAddress + ", " + wpGeozoneName);
			
			/*
			 * Contract page 2
			 */
			// TODO
//			CONTRACT_WORKPLACE_ADDRESS("Texto30",Boolean.FALSE),
//			CONTRACT_EMPLOYEE_PROFFESION("Texto31",Boolean.FALSE),
//			CONTRACT_EMPLOYEE_CATEGORY("Texto32",Boolean.FALSE),
//			FORMATION_TEACHER("Texto33",Boolean.FALSE),
//			FORMATION_TEACHER_QUALIFICATION("Texto34",Boolean.FALSE),
			
			if(contrata!=null){
				if(contrata.getHorasJornada()!=null){
					setPdfFieldValue(LearningCommonFieldName.JOURNAL_HOURS.getValue(), String.valueOf(Integer.parseInt(contrata.getHorasJornada())));
					if(contrata.getHorasFormacion()!=null){
						Integer horasJornada = Integer.parseInt(contrata.getHorasJornada());
						Integer horasFormacion = Integer.parseInt(contrata.getHorasFormacion());
						setPdfFieldValue(LearningCommonFieldName.TOTAL_HOURS.getValue(), String.valueOf(horasFormacion));
						if(horasJornada!=null && horasJornada!=0){
							setPdfFieldValue(LearningCommonFieldName.JOURNAL_PERCENT.getValue(), String.valueOf(100-(horasFormacion*100/horasJornada)));
						}
					}
				}
			}
			
			PayrollWorkPlace pw = obtainPayrollWorkPlace(contract.getWorkPlace());
			if(pw!=null && pw.getAgreement()!=null){
				setPdfFieldValue(LearningCommonFieldName.JOURNAL_COLLECTIVE_AGREEMENT.getValue(), pw.getAgreement().getDescription());
			}
			
			if(StringUtils.isNotBlank(getContractInfoMap(contract).get(LearningCommonFieldName.HORARIO_LABORAL.toString()))){
				setPdfFieldValue(LearningCommonFieldName.HORARIO_LABORAL.getValue(),getContractInfoMap(contract).get(ContractVariable.WORK_SCHEDULE.getValue()));
			}
			if(StringUtils.isNotBlank(getContractInfoMap(contract).get(LearningCommonFieldName.HORARIO_LECTIVO.toString()))){
				setPdfFieldValue(LearningCommonFieldName.HORARIO_LECTIVO2.getValue(),getContractInfoMap(contract).get(ContractVariable.TRAINING_SCHEDULE.getValue()));
			}

			Integer durationInMonths = getMonthsBetweenDates(contract.getStartDate(), contract.getEndDate());
			setPdfFieldValue(LearningCommonFieldName.CONTRACT_DURATION.getValue(), durationInMonths!=null?durationInMonths+" meses":"");
			dateFormatter.applyPattern("dd/MM/yyyy");
			setPdfFieldValue(LearningCommonFieldName.START_DATE.getValue(), dateFormatter.format(contract.getStartDate()));
			if(contract.getEndDate()!=null){
				setPdfFieldValue(LearningCommonFieldName.END_DATE.getValue(), dateFormatter.format(contract.getEndDate()));
			}
			setPdfFieldValue(LearningCommonFieldName.TRIAL_DURATION.getValue(), "Según convenio");
			setPdfFieldValue(LearningCommonFieldName.TRIAL_DURATION_INCREASE.getValue(), null);
			
			setPdfFieldValue(LearningCommonFieldName.SALARY_AMOUNT.getValue(), "Según convenio");
			setPdfFieldValue(LearningCommonFieldName.SALARY_PERIOD.getValue(), "mensuales");
			setPdfFieldValue(LearningCommonFieldName.HOLIDAYS.getValue(), "Según convenio");
			
			setPdfFieldValue(LearningCommonFieldName.ANNEX_I_CHECK.getValue(), null);
			setPdfFieldValue(LearningCommonFieldName.ANNEX_II_CHECK.getValue(), "true");
			
			if(pw!=null && pw.getAgreement()!=null){
				setPdfFieldValue(LearningCommonFieldName.COLLECTIVE_AGREEMENT.getValue(), pw.getAgreement().getDescription());
			}
			
			setPdfFieldValue(LearningCommonFieldName.SEPE_TOWN_FOR_CONTRACT_START.getValue(), contract.getWorkPlace().getAddress().getCity());
			setPdfFieldValue(LearningCommonFieldName.SEPE_TOWN_FOR_CONTRACT_END.getValue(), contract.getWorkPlace().getAddress().getCity());
			
			
			setPdfFieldValue(LearningCommonFieldName.SIGN_TOWN.getValue(),contract.getWorkPlace().getAddress().getCity());
			dateFormatter.applyPattern("dd");
			setPdfFieldValue(LearningCommonFieldName.SIGN_DAY.getValue(),dateFormatter.format(contract.getStartDate()));
			dateFormatter.applyPattern("MMMM");
			setPdfFieldValue(LearningCommonFieldName.SIGN_MONTH.getValue(),dateFormatter.format(contract.getStartDate()));
			dateFormatter.applyPattern("yy");
			setPdfFieldValue(LearningCommonFieldName.SIGN_YEAR.getValue(),dateFormatter.format(contract.getStartDate()));
			
			
			/*
			 *  OPTIONS PAGE
			 */
			modelOption = ModelOption.valueOf(getContractInfoMap(contract).get(ContractVariable.CONTRACT_MODEL_OPTION.getValue()));
			
			if(modelOption == ModelOption.LEARNING_OPT1){
				setPdfFieldValue(LearningOptionFieldName.MAIN_OPT1_CHECK.getValue(),"true");
				setPdfFieldValue(LearningOptionFieldName.OPT1_OPTION_CHECK.getValue(),"true");
				if( subsidized!=null ){
					if( Boolean.parseBoolean(subsidized) ){
						setPdfFieldValue(LearningOptionFieldName.OPT1_QUOTE_BONUS.getValue(),"true");
					} else if( !Boolean.parseBoolean(subsidized) ){
						setPdfFieldValue(LearningOptionFieldName.OPT1_QUOTE_NO_BONUS.getValue(),"true");
					}
				}
			} else if(modelOption == ModelOption.LEARNING_OPT2){
				setPdfFieldValue(LearningOptionFieldName.MAIN_OPT2_CHECK.getValue(),"true");
				// TODO: complete this option
			} else if(modelOption == ModelOption.LEARNING_OPT3){
				setPdfFieldValue(LearningOptionFieldName.MAIN_OPT3_CHECK.getValue(),"true");
				// TODO: complete this option
			} else if(modelOption == ModelOption.LEARNING_OPT4){
				setPdfFieldValue(LearningOptionFieldName.MAIN_OPT4_CHECK.getValue(),"true");
				// TODO: complete this option
			}
			
			// ANEX I fields: because its length is defined forward
			// TODO: complete this option
			
			// ANEX II fields: because its length is defined forward
//			TrainingCourse trainingCourse = obtainTrainingCourse(map.get(ContractVariable.TRAINING_COURSE.getValue()));
			
			if(trainingCourse != null){
				// HEADER FIELDS
				if(trainingCourse.isProfessionalCertificate()){
					setPdfFieldValue(LearningAnnex2.CP_YES.getValue(),"true");
				} else {
					setPdfFieldValue(LearningAnnex2.CP_NO.getValue(),"true");
				}
				if(trainingCourse.isFpTitle()){
					setPdfFieldValue(LearningAnnex2.FP_YES.getValue(),"true");
				} else {
					setPdfFieldValue(LearningAnnex2.FP_NO.getValue(),"true");
				}
				if(trainingCourse.isCenterAvailable()){
					setPdfFieldValue(LearningAnnex2.CENTER_AVAILABLE_YES.getValue(),"true");
				} else {
					setPdfFieldValue(LearningAnnex2.CENTER_AVAILABLE_NO.getValue(),"true");
				}
				
				setPdfFieldValue(LearningAnnex2.CP_NAME.getValue(),trainingCourse.getCertificationName());
				setPdfFieldValue(LearningAnnex2.FP_TITLE.getValue(),trainingCourse.getFpTitleName());
			}
			
			// ENTERPRISE FIELDS
			setPdfFieldValue(LearningAnnex2.ENTERPRISE_NAME.getValue(),contract.getWorkPlace().getEnterprise().getRegistry().getFullName());
			setPdfFieldValue(LearningAnnex2.ENTERPRISE_CIF.getValue(),contract.getWorkPlace().getEnterprise().getRegistry().getDocument());
			RegistryDirStaff enterpriseDirStaff = obtainRegistryDirStaff(contract.getWorkPlace().getEnterprise().getRegistry()); 
			try {
				setPdfFieldValue(LearningAnnex2.ENTERPRISE_DIR_STAFF_NAME.getValue(),enterpriseDirStaff.getName());
				setPdfFieldValue(LearningAnnex2.ENTERPRISE_DIR_STAFF_NIF.getValue(),enterpriseDirStaff.getDocument());
				String rDirStaddCharge = null;
				if ( enterpriseDirStaff.isShareHolder() ){
					rDirStaddCharge = "Socio";
				} else if ( enterpriseDirStaff.isRepresentative() ){
					rDirStaddCharge = "Apoderado";
				} else if( enterpriseDirStaff.isDirector() ){
					rDirStaddCharge = "Administrador";
				} else if ( enterpriseDirStaff.isRepresentativeLabor() ){
					rDirStaddCharge = "Representante laboral";
				}
				setPdfFieldValue(LearningAnnex2.ENTERPRISE_DIR_STAFF_CHARGE.getValue(),rDirStaddCharge);
			} catch (NullPointerException npe) {
				// do nothing
			}
			
			String phone = contract.getWorkPlace().getEnterprise().getRegistry().getPhone().getValue();
			try {
				phone = phone.replaceAll("[^0-9]", "");
				setPdfFieldValue(LearningAnnex2.ENTERPRISE_PHONE_1.getValue(),phone.substring(0, 1));
				setPdfFieldValue(LearningAnnex2.ENTERPRISE_PHONE_2.getValue(),phone.substring(1, 2));
				setPdfFieldValue(LearningAnnex2.ENTERPRISE_PHONE_3.getValue(),phone.substring(2, 3));
				setPdfFieldValue(LearningAnnex2.ENTERPRISE_PHONE_4.getValue(),phone.substring(3, 4));
				setPdfFieldValue(LearningAnnex2.ENTERPRISE_PHONE_5.getValue(),phone.substring(4, 5));
				setPdfFieldValue(LearningAnnex2.ENTERPRISE_PHONE_6.getValue(),phone.substring(5, 6));
				setPdfFieldValue(LearningAnnex2.ENTERPRISE_PHONE_7.getValue(),phone.substring(6, 7));
				setPdfFieldValue(LearningAnnex2.ENTERPRISE_PHONE_8.getValue(),phone.substring(7, 8));
				setPdfFieldValue(LearningAnnex2.ENTERPRISE_PHONE_9.getValue(),phone.substring(8, 9));
			} catch (StringIndexOutOfBoundsException aie) {
				// do nothing
			} catch (NullPointerException npe) {
				// do nothing
			}
			String email = contract.getWorkPlace().getEnterprise().getRegistry().getEmail().getValue();
			setPdfFieldValue(LearningAnnex2.ENTERPRISE_EMAIL.getValue(),email);
			
			// EMPLOYEE FIELDS
			setPdfFieldValue(LearningAnnex2.EMPLOYEE_NAME.getValue(),contract.getPerson().getFullName());
			setPdfFieldValue(LearningAnnex2.EMPLOYEE_NIF.getValue(),contract.getPerson().getRegistry().getDocument());
			
			// CONTRACT FIELDS
			try {
				setPdfFieldValue(LearningAnnex2.CONTRACT_ID_NUMBER_1.getValue(),getContractInfoMap(contract).get(ContractVariable.SEPE_CONTRACT_ID.getValue()).substring(0, 1));
				setPdfFieldValue(LearningAnnex2.CONTRACT_ID_NUMBER_2.getValue(),getContractInfoMap(contract).get(ContractVariable.SEPE_CONTRACT_ID.getValue()).substring(1, 2));
				setPdfFieldValue(LearningAnnex2.CONTRACT_ID_NUMBER_3.getValue(),getContractInfoMap(contract).get(ContractVariable.SEPE_CONTRACT_ID.getValue()).substring(2, 3));
				setPdfFieldValue(LearningAnnex2.CONTRACT_ID_NUMBER_4.getValue(),getContractInfoMap(contract).get(ContractVariable.SEPE_CONTRACT_ID.getValue()).substring(3, 4));
				setPdfFieldValue(LearningAnnex2.CONTRACT_ID_NUMBER_5.getValue(),getContractInfoMap(contract).get(ContractVariable.SEPE_CONTRACT_ID.getValue()).substring(4, 5));
				setPdfFieldValue(LearningAnnex2.CONTRACT_ID_NUMBER_6.getValue(),getContractInfoMap(contract).get(ContractVariable.SEPE_CONTRACT_ID.getValue()).substring(5, 6));
				setPdfFieldValue(LearningAnnex2.CONTRACT_ID_NUMBER_7.getValue(),getContractInfoMap(contract).get(ContractVariable.SEPE_CONTRACT_ID.getValue()).substring(6, 7));
				setPdfFieldValue(LearningAnnex2.CONTRACT_ID_NUMBER_8.getValue(),getContractInfoMap(contract).get(ContractVariable.SEPE_CONTRACT_ID.getValue()).substring(7, 8));
				setPdfFieldValue(LearningAnnex2.CONTRACT_ID_NUMBER_9.getValue(),getContractInfoMap(contract).get(ContractVariable.SEPE_CONTRACT_ID.getValue()).substring(8, 9));
				setPdfFieldValue(LearningAnnex2.CONTRACT_ID_NUMBER_10.getValue(),getContractInfoMap(contract).get(ContractVariable.SEPE_CONTRACT_ID.getValue()).substring(9, 10));
				setPdfFieldValue(LearningAnnex2.CONTRACT_ID_NUMBER_11.getValue(),getContractInfoMap(contract).get(ContractVariable.SEPE_CONTRACT_ID.getValue()).substring(10, 11));
				setPdfFieldValue(LearningAnnex2.CONTRACT_ID_NUMBER_12.getValue(),getContractInfoMap(contract).get(ContractVariable.SEPE_CONTRACT_ID.getValue()).substring(11, 12));
				setPdfFieldValue(LearningAnnex2.CONTRACT_ID_NUMBER_13.getValue(),getContractInfoMap(contract).get(ContractVariable.SEPE_CONTRACT_ID.getValue()).substring(12, 13));
				setPdfFieldValue(LearningAnnex2.CONTRACT_ID_NUMBER_14.getValue(),getContractInfoMap(contract).get(ContractVariable.SEPE_CONTRACT_ID.getValue()).substring(13, 14));
				setPdfFieldValue(LearningAnnex2.CONTRACT_ID_NUMBER_15.getValue(),getContractInfoMap(contract).get(ContractVariable.SEPE_CONTRACT_ID.getValue()).substring(14, 15));
				setPdfFieldValue(LearningAnnex2.CONTRACT_ID_NUMBER_16.getValue(),getContractInfoMap(contract).get(ContractVariable.SEPE_CONTRACT_ID.getValue()).substring(15, 16));
			} catch (StringIndexOutOfBoundsException aie) {
				// do nothing
			} catch (NullPointerException npe) {
				// do nothing
			}
			
			SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
			if(contract.getStartDate()!=null){
				setPdfFieldValue(LearningAnnex2.CONTRACT_START_DATE.getValue(),formatter.format(contract.getStartDate()));
			}
			if(contract.getEndDate()!=null){
				setPdfFieldValue(LearningAnnex2.CONTRACT_END_DATE.getValue(),formatter.format(contract.getEndDate()));
			}
			if(trainingCourse != null){
				setPdfFieldValue(LearningAnnex2.CONTRACT_OCCUPATION.getValue(),trainingCourse.getOccupationName());
				String cno = getContractDataMap(contract).get(ContextVariable.CNO.getName());
				if( !StringUtils.isEmpty(cno) ){
					setPdfFieldValue(LearningAnnex2.CONTRACT_CNO_1.getValue(),cno.substring(0, 1));
					setPdfFieldValue(LearningAnnex2.CONTRACT_CNO_2.getValue(),cno.substring(1, 2));
					setPdfFieldValue(LearningAnnex2.CONTRACT_CNO_3.getValue(),cno.substring(2, 3));
					setPdfFieldValue(LearningAnnex2.CONTRACT_CNO_4.getValue(),cno.substring(3, 4));
					setPdfFieldValue(LearningAnnex2.CONTRACT_CNO_5.getValue(),"");
					setPdfFieldValue(LearningAnnex2.CONTRACT_CNO_6.getValue(),"");
					setPdfFieldValue(LearningAnnex2.CONTRACT_CNO_7.getValue(),"");
					setPdfFieldValue(LearningAnnex2.CONTRACT_CNO_8.getValue(),"");
				}
			}
			
			
			// TRAINING CENTER FIELDS
			RegistryDirStaff trainingCenterDirStaff = null;
			if(trainingCourse != null){
				setPdfFieldValue(LearningAnnex2.TRAINING_CENTER_CODE.getValue(),trainingCourse.getTrainingCenter().getCode());
				trainingCenterDirStaff = obtainRegistryDirStaff(trainingCourse.getTrainingCenter().getRegistry());
				try {
					if(trainingCenterDirStaff != null){
						setPdfFieldValue(LearningAnnex2.TRAINING_CENTER_DIR_STAFF_NAME.getValue(),trainingCenterDirStaff.getName());
						setPdfFieldValue(LearningAnnex2.TRAINING_CENTER_DIR_STAFF_NIF.getValue(),trainingCenterDirStaff.getDocument());
						String rDirStaddCharge = null;
						if ( enterpriseDirStaff.isShareHolder() ){
							rDirStaddCharge = "Socio";
						} else if ( enterpriseDirStaff.isRepresentative() ){
							rDirStaddCharge = "Apoderado";
						} else if( enterpriseDirStaff.isDirector() ){
							rDirStaddCharge = "Administrador";
						} else if ( enterpriseDirStaff.isRepresentativeLabor() ){
							rDirStaddCharge = "Representante laboral";
						}
						setPdfFieldValue(LearningAnnex2.TRAINING_CENTER_DIR_STAFF_CHARGE.getValue(),rDirStaddCharge);
					}
				} catch (NullPointerException npe) {
					// do nothing
				}
				
				try {	
					setPdfFieldValue(LearningAnnex2.TRAINING_CENTER_NAME.getValue(),trainingCourse.getTrainingCenter().getRegistry().getFullName());
				} catch (NullPointerException npe) {
					// do nothing
				}
				try {	
					setPdfFieldValue(LearningAnnex2.TRAINING_CENTER_NIF.getValue(),trainingCourse.getTrainingCenter().getRegistry().getDocument());
				} catch (NullPointerException npe) {
					// do nothing
				}
				try {	
					setPdfFieldValue(LearningAnnex2.TRAINING_CENTER_ADDRESS.getValue(),trainingCourse.getTrainingCenter().getRegistry().getDefaultAddress().getFullAddress());
				} catch (NullPointerException npe) {
					// do nothing
				}
				try {	
					setPdfFieldValue(LearningAnnex2.TRAINING_CENTER_ZIP_1.getValue(),trainingCourse.getTrainingCenter().getRegistry().getDefaultAddress().getZip().substring(0, 1));
					setPdfFieldValue(LearningAnnex2.TRAINING_CENTER_ZIP_2.getValue(),trainingCourse.getTrainingCenter().getRegistry().getDefaultAddress().getZip().substring(1, 2));
					setPdfFieldValue(LearningAnnex2.TRAINING_CENTER_ZIP_3.getValue(),trainingCourse.getTrainingCenter().getRegistry().getDefaultAddress().getZip().substring(2, 3));
					setPdfFieldValue(LearningAnnex2.TRAINING_CENTER_ZIP_4.getValue(),trainingCourse.getTrainingCenter().getRegistry().getDefaultAddress().getZip().substring(3, 4));
					setPdfFieldValue(LearningAnnex2.TRAINING_CENTER_ZIP_5.getValue(),trainingCourse.getTrainingCenter().getRegistry().getDefaultAddress().getZip().substring(4, 5));
				} catch (StringIndexOutOfBoundsException aie) {
					// do nothing
				} catch (NullPointerException npe) {
					// do nothing
				}
				try {	
					setPdfFieldValue(LearningAnnex2.TRAINING_CENTER_TOWN.getValue(),trainingCourse.getTrainingCenter().getRegistry().getDefaultAddress().getCity());
				} catch (NullPointerException npe) {
					// do nothing
				}
				try {	
					setPdfFieldValue(LearningAnnex2.TRAINING_CENTER_PROVINCE.getValue(),trainingCourse.getTrainingCenter().getRegistry().getDefaultAddress().getGeozone().getName());
				} catch (NullPointerException npe) {
					// do nothing
				}
				
				try {
					String centerPhone = trainingCourse.getTrainingCenter().getRegistry().getPhone().getValue();
					centerPhone = centerPhone.replaceAll("[^0-9]", "");
					setPdfFieldValue(LearningAnnex2.TRAINING_CENTER_PHONE_1.getValue(),centerPhone.substring(0, 1));
					setPdfFieldValue(LearningAnnex2.TRAINING_CENTER_PHONE_2.getValue(),centerPhone.substring(1, 2));
					setPdfFieldValue(LearningAnnex2.TRAINING_CENTER_PHONE_3.getValue(),centerPhone.substring(2, 3));
					setPdfFieldValue(LearningAnnex2.TRAINING_CENTER_PHONE_4.getValue(),centerPhone.substring(3, 4));
					setPdfFieldValue(LearningAnnex2.TRAINING_CENTER_PHONE_5.getValue(),centerPhone.substring(4, 5));
					setPdfFieldValue(LearningAnnex2.TRAINING_CENTER_PHONE_6.getValue(),centerPhone.substring(5, 6));
					setPdfFieldValue(LearningAnnex2.TRAINING_CENTER_PHONE_7.getValue(),centerPhone.substring(6, 7));
					setPdfFieldValue(LearningAnnex2.TRAINING_CENTER_PHONE_8.getValue(),centerPhone.substring(7, 8));
					setPdfFieldValue(LearningAnnex2.TRAINING_CENTER_PHONE_9.getValue(),centerPhone.substring(8, 9));
				} catch (StringIndexOutOfBoundsException aie) {
					// do nothing
				} catch (NullPointerException npe) {
					// do nothing
				}
				
				try {	
					String centerEmail = trainingCourse.getTrainingCenter().getRegistry().getEmail().getValue();
					setPdfFieldValue(LearningAnnex2.TRAINING_CENTER_EMAIL.getValue(),centerEmail);
				} catch (NullPointerException npe) {
					// do nothing
				}
				
				// TRAINING COURSE FIELDS
				if(trainingCourse.getModality()==TrainingModality.CLASSROOM){
					setPdfFieldValue(LearningAnnex2.TRAINING_COURSE_CLASSROOM.getValue(),"true");
				} else if(trainingCourse.getModality()==TrainingModality.DISTANCE){
					setPdfFieldValue(LearningAnnex2.TRAINING_COURSE_DISTANCE.getValue(),"true");
				} else if(trainingCourse.getModality()==TrainingModality.PHONE){
					setPdfFieldValue(LearningAnnex2.TRAINING_COURSE_PHONE_LEARNING.getValue(),"true");
				} else if(trainingCourse.getModality()==TrainingModality.MIX){
					setPdfFieldValue(LearningAnnex2.TRAINING_COURSE_MIXED.getValue(),"true");
				}
				ContractData trainingCourseData = obtainContractData(contract, ContractVariable.TRAINING_COURSE.getValue());
				if(trainingCourseData.getStartDate()!=null){
					setPdfFieldValue(LearningAnnex2.TRAINING_COURSE_START_DATE.getValue(),formatter.format(trainingCourseData.getStartDate()));
				}
				if(trainingCourseData.getEndDate()!=null){
					setPdfFieldValue(LearningAnnex2.TRAINING_COURSE_END_DATE.getValue(),formatter.format(trainingCourseData.getEndDate()));
				}
			}
			setPdfFieldValue(LearningAnnex2.TRAINING_COURSE_SCHEDULE.getValue(),getContractDataMap(contract).get(ContractVariable.TRAINING_SCHEDULE.getValue()));
			if(contrataParams!=null){
				
//				Integer durationInMonths = getMonthsBetweenDates(contract.getStartDate(), contract.getEndDate());
				
				if(durationInMonths!=null){
					
					Integer horasFormacion = (durationInMonths<6)?(258):(516);
					
					setPdfFieldValue(LearningAnnex2.TRAINING_COURSE_FIRST_YEAR_MAIN_HOURS.getValue(),String.valueOf(horasFormacion));
				}
			}
			setPdfFieldValue(LearningAnnex2.TRAINING_COURSE_FIRST_YEAR_COMPLEMENTARY_HOURS.getValue(),"");
			setPdfFieldValue(LearningAnnex2.TRAINING_COURSE_NEXT_YEAR_MAIN_HOURS.getValue(),"");
			setPdfFieldValue(LearningAnnex2.TRAINING_COURSE_NEXT_YEAR_COMPLEMENTARY_HOURS.getValue(),"");

			// FOORTER FIELDS
			setPdfFieldValue(LearningAnnex2.SIGNATURE_PLACE.getValue(),contract.getWorkPlace().getAddress().getCity());
			setPdfFieldValue(LearningAnnex2.SIGNATURE_DAY.getValue(),String.valueOf(CommonUtil.getDay(new Date())));
			
			formatter.applyPattern("MMMM");
			setPdfFieldValue(LearningAnnex2.SIGNATURE_MONTH.getValue(),formatter.format(new Date()) );
			formatter.applyPattern("yy");
			setPdfFieldValue(LearningAnnex2.SIGNATURE_YEAR.getValue(),formatter.format(new Date()));
			setPdfFieldValue(LearningAnnex2.SIGNATURE_EMPLOYEE.getValue(),contract.getPerson().getFullName());
			setPdfFieldValue(LearningAnnex2.SIGNATURE_EMPLOYEE_DIR_STAFF.getValue(),"");
			try {	
				setPdfFieldValue(LearningAnnex2.SIGNATURE_ENTERPRISE_DIR_STAFF.getValue(),enterpriseDirStaff.getName());
			} catch (NullPointerException npe) {
				// do nothing
			}
			try {	
				setPdfFieldValue(LearningAnnex2.SIGNATURE_TRAINING_CENTER_DIR_STAFF.getValue(),trainingCenterDirStaff.getName());
			} catch (NullPointerException npe) {
				// do nothing
			}
		} catch (IOException e) {
			// do nothing
		} catch (ManagerBeanException e) {
			// do nothing
		}
	}
	
	private TrainingCourse obtainTrainingCourse(String value) {
		if(value!=null && NumberUtils.isNumber(value)){
			try {
				IManagerBean bean = BeanManager.getManagerBean(TrainingCourse.class);
				Criteria criteria = new Criteria();
				criteria.addEqualExpression(bean.getFieldName(IEntityAlias.TRAINING_COURSE_ID), Integer.parseInt(value) );
				Iterator<ITransferObject> it = bean.getList(criteria).iterator();
				while( it.hasNext() ){
					return (TrainingCourse) it.next();
				}
			} catch (ManagerBeanException e) {
				// do nothing ...
			}
		}
		return null;
	}
	
	private PayrollWorkPlace obtainPayrollWorkPlace(WorkPlace workPlace) {
		try {
			IManagerBean bean = BeanManager.getManagerBean(PayrollWorkPlace.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.PAYROLL_WORK_PLACE_WORK_PLACE_ID), workPlace.getId() );
			Iterator<ITransferObject> it = bean.getList(criteria).iterator();
			while( it.hasNext() ){
				return (PayrollWorkPlace) it.next();
			}
		} catch (ManagerBeanException e) {
			// do nothing ...
		}
		return null;
	}
	
	private Integer getMonthsBetweenDates(Date startDate, Date endDate) {
		if(startDate!=null && endDate!=null){
			return (int) ((CommonUtil.getDaysBetweenDates(startDate, endDate, true))/30);
		}
		return null;
	}
	
	private ContractData obtainContractData(Contract contract, String name) {
		try {
			IManagerBean bean = BeanManager.getManagerBean(ContractData.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_DATA_CONTRACT_ID), contract.getId() );
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_DATA_NAME), name );
			List<ITransferObject> list = bean.getList(criteria);
			if( !list.isEmpty() ){
				return (ContractData) list.get(0);
			}
		} catch (ManagerBeanException e) {
			// do nothing ...
		}
		return null;
	}

	/*
	 * INNER CLASSES
	 */
	public enum LearningCommonFieldName implements IContractFieldName{
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
		QUOTE_BONUS_YES("Casilla de verificación1",Boolean.FALSE),
		QUOTE_BONUS_NO("Casilla de verificación2",Boolean.FALSE),
		EMPLOYEE_OPT1("Casilla de verificación3",Boolean.FALSE),
		EMPLOYEE_OPT2("Casilla de verificación4",Boolean.FALSE),
		EMPLOYEE_OPT3("Casilla de verificación5",Boolean.FALSE),
		EMPLOYEE_OPT4("Casilla de verificación6",Boolean.FALSE),
		
		ACTIVITY_EMPLOYEE_PROFFESION("Texto21",Boolean.FALSE),
		CNO1("Texto26",Boolean.FALSE),
		CNO2("Texto27",Boolean.FALSE),
		CNO3("Texto28",Boolean.FALSE),
		CNO4("Texto29",Boolean.FALSE),
		ACTIVITY_EMPLOYEE_CATEGORY("Texto22",Boolean.FALSE),
		ACTIVITY_WORKPLACE_ADDRESS1("Texto23",Boolean.FALSE),
		ACTIVITY_WORKPLACE_ADDRESS2("Texto24",Boolean.FALSE),
		
		/*
		 * Contract page 2
		 */
		CONTRACT_WORKPLACE_ADDRESS("Texto30",Boolean.FALSE),
		CONTRACT_EMPLOYEE_PROFFESION("Texto31",Boolean.FALSE),
		CONTRACT_EMPLOYEE_CATEGORY("Texto32",Boolean.FALSE),
		FORMATION_TEACHER("Texto33",Boolean.FALSE),
		FORMATION_TEACHER_QUALIFICATION("Texto34",Boolean.FALSE),
		
		JOURNAL_HOURS("Texto35",Boolean.FALSE),
		TOTAL_HOURS("Texto36",Boolean.FALSE),
		JOURNAL_PERCENT("Texto37",Boolean.FALSE),
		JOURNAL_COLLECTIVE_AGREEMENT("Texto38",Boolean.FALSE),
//		EFFECTIVE_JOURNAL_SCHEDULE("Texto39",Boolean.TRUE),
		HORARIO_LABORAL("Texto39",Boolean.TRUE),
//		FORMATION_JOURNAL_SCHEDULE("Texto40",Boolean.TRUE),
		HORARIO_LECTIVO("Texto40",Boolean.TRUE),
//		FORMATION_JOURNAL_SCHEDULE2("Texto41",Boolean.FALSE),
		HORARIO_LECTIVO2("Texto41",Boolean.FALSE),
		
		CONTRACT_DURATION("Texto42",Boolean.FALSE),
		START_DATE("Texto43",Boolean.FALSE),
		END_DATE("Texto44",Boolean.FALSE),
		TRIAL_DURATION("Texto45",Boolean.TRUE),
		TRIAL_DURATION_INCREASE("Casilla de verificación46",Boolean.FALSE),
		
		SALARY_AMOUNT("Texto47",Boolean.TRUE),
		SALARY_PERIOD("Texto48",Boolean.TRUE),
		
		HOLIDAYS("Texto49",Boolean.TRUE),
		
		ANNEX_I_CHECK("Casilla de verificación59",Boolean.FALSE),
		ANNEX_II_CHECK("Casilla de verificación60",Boolean.FALSE),
		
		COLLECTIVE_AGREEMENT("Texto54g",Boolean.FALSE),
		
		SEPE_TOWN_FOR_CONTRACT_START("Texto51",Boolean.FALSE),
		SEPE_TOWN_FOR_CONTRACT_END("Texto52",Boolean.FALSE),
		
		// SPECIFIC fields: because its length, it is defined forward
		
		// ANEX I fields: because its length, it is defined forward
		
		// ANEX II fields: because its length, it is defined forward
		
		SIGN_TOWN("Texto74",Boolean.FALSE),
		SIGN_DAY("Texto75",Boolean.FALSE),
		SIGN_MONTH("Texto76",Boolean.FALSE),
		SIGN_YEAR("Texto77",Boolean.FALSE),
		
		;
		
		private String value;
		private boolean overridable;
		
		private LearningCommonFieldName(String value, boolean overridable) {
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
	
	public enum LearningOptionFieldName implements IContractFieldName, IContractOptionField {
		
		/* Contract PAGE 3 */
		MAIN_OPT1_CHECK("Casilla de verificación53",Boolean.FALSE),
		MAIN_OPT2_CHECK("Casilla de verificación54",Boolean.FALSE),
		MAIN_OPT3_CHECK("Casilla de verificación55",Boolean.FALSE),
		MAIN_OPT4_CHECK("Casilla de verificación56",Boolean.FALSE),
		
//		FORMACIÓN Y APRENDIZAJE ( ORDINARIO ). ( pág.4 )
		OPT1_OPTION_CHECK("Casilla de verificación7",Boolean.FALSE),
		OPT1_QUOTE_BONUS("Casilla de verificación8",Boolean.FALSE),
		OPT1_QUOTE_NO_BONUS("Casilla de verificación9",Boolean.FALSE),	
//		DE TRABAJADORES EN SITUACIÓN DE EXCLUSIÓN SOCIAL, VÍCTIMAS DE VIOLENCIA DE GÉNERO, DOMÉSTICA O VÍCTIMA DE TERRORISMO . ( pág.5 )
		OPT2_OPTION_CHECK("Casilla de verificación10",Boolean.FALSE),
		// TODO: complete this option
//		DE PERSONAS CON DISCAPACIDAD EN CENTROS ESPECIALES DE EMPLEO. ( pág.6 )
		OPT3_OPTION_CHECK("Casilla de verificación57",Boolean.FALSE),
		// TODO: complete this option
//		DE TRABAJOS DE INTERÉS SOCIAL/FOMENTO DE EMPLEO AGRARIO. ( pág.7 )
		OPT4_OPTION_CHECK("Casilla de verificación64",Boolean.FALSE),
		// TODO: complete this option
		;
		
		private String value;
		private boolean overridable;
		
		private LearningOptionFieldName(String value, boolean overridable) {
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
	
	public enum LearningAnnex1 {
		
	}
	
	public enum LearningAnnex2 implements IContractFieldName, IContractOptionField {
		
		// HEADER FIELDS
		CP_NO("c1", Boolean.FALSE),
		CP_YES("c2", Boolean.FALSE),
		FP_NO("c4", Boolean.FALSE),
		FP_YES("c5", Boolean.FALSE),
		CENTER_AVAILABLE_NO("c7", Boolean.FALSE),
		CENTER_AVAILABLE_YES("c8", Boolean.FALSE),

		CP_NAME("c3", Boolean.FALSE),
		FP_TITLE("c6", Boolean.FALSE),
		
		// ENTERPRISE FIELDS
		ENTERPRISE_NAME("c9", Boolean.FALSE),
		ENTERPRISE_CIF("c10", Boolean.FALSE),
		
		ENTERPRISE_DIR_STAFF_NAME("c11", Boolean.FALSE),
		ENTERPRISE_DIR_STAFF_NIF("c12", Boolean.FALSE),
		ENTERPRISE_DIR_STAFF_CHARGE("c13", Boolean.FALSE),
		
		ENTERPRISE_PHONE_1("c14", Boolean.FALSE),
		ENTERPRISE_PHONE_2("c15", Boolean.FALSE),
		ENTERPRISE_PHONE_3("c16", Boolean.FALSE),
		ENTERPRISE_PHONE_4("c17", Boolean.FALSE),
		ENTERPRISE_PHONE_5("c18", Boolean.FALSE),
		ENTERPRISE_PHONE_6("c19", Boolean.FALSE),
		ENTERPRISE_PHONE_7("c20", Boolean.FALSE),
		ENTERPRISE_PHONE_8("c21", Boolean.FALSE),
		ENTERPRISE_PHONE_9("c22", Boolean.FALSE),
		ENTERPRISE_EMAIL("c23", Boolean.FALSE),
		
		// EMPLOYEE FIELDS
		EMPLOYEE_NAME("c24", Boolean.FALSE),
		EMPLOYEE_NIF("c25", Boolean.FALSE),

		// CONTRACT FIELDS
		CONTRACT_ID_NUMBER_1("c26", Boolean.FALSE),
		CONTRACT_ID_NUMBER_2("c27", Boolean.FALSE),
		CONTRACT_ID_NUMBER_3("c28", Boolean.FALSE),
		CONTRACT_ID_NUMBER_4("c29", Boolean.FALSE),
		CONTRACT_ID_NUMBER_5("c30", Boolean.FALSE),
		CONTRACT_ID_NUMBER_6("c31", Boolean.FALSE),
		CONTRACT_ID_NUMBER_7("c32", Boolean.FALSE),
		CONTRACT_ID_NUMBER_8("c33", Boolean.FALSE),
		CONTRACT_ID_NUMBER_9("c34", Boolean.FALSE),
		CONTRACT_ID_NUMBER_10("c35", Boolean.FALSE),
		CONTRACT_ID_NUMBER_11("c36", Boolean.FALSE),
		CONTRACT_ID_NUMBER_12("c37", Boolean.FALSE),
		CONTRACT_ID_NUMBER_13("c38", Boolean.FALSE),
		CONTRACT_ID_NUMBER_14("c39", Boolean.FALSE),
		CONTRACT_ID_NUMBER_15("c40", Boolean.FALSE),
		CONTRACT_ID_NUMBER_16("c41", Boolean.FALSE),
		
		CONTRACT_START_DATE("c42", Boolean.FALSE),
		CONTRACT_END_DATE("c43", Boolean.FALSE),
		CONTRACT_OCCUPATION("c44", Boolean.FALSE),
		CONTRACT_CNO_1("c45", Boolean.FALSE),
		CONTRACT_CNO_2("c46", Boolean.FALSE),
		CONTRACT_CNO_3("c47", Boolean.FALSE),
		CONTRACT_CNO_4("c48", Boolean.FALSE),
		CONTRACT_CNO_5("c49", Boolean.FALSE),
		CONTRACT_CNO_6("c50", Boolean.FALSE),
		CONTRACT_CNO_7("c51", Boolean.FALSE),
		CONTRACT_CNO_8("c52", Boolean.FALSE),
		
		// TRAINING CENTER FIELDS
		TRAINING_CENTER_CODE("c53", Boolean.FALSE),
		TRAINING_CENTER_DIR_STAFF_NAME("c54", Boolean.FALSE),
		TRAINING_CENTER_DIR_STAFF_NIF("c55", Boolean.FALSE),
		TRAINING_CENTER_DIR_STAFF_CHARGE("c56", Boolean.FALSE),
		TRAINING_CENTER_NAME("c57", Boolean.FALSE),
		TRAINING_CENTER_NIF("c58", Boolean.FALSE),
		TRAINING_CENTER_ADDRESS("c59", Boolean.FALSE),
		TRAINING_CENTER_ZIP_1("c60", Boolean.FALSE),
		TRAINING_CENTER_ZIP_2("c61", Boolean.FALSE),
		TRAINING_CENTER_ZIP_3("c62", Boolean.FALSE),
		TRAINING_CENTER_ZIP_4("c63", Boolean.FALSE),
		TRAINING_CENTER_ZIP_5("c64", Boolean.FALSE),
		TRAINING_CENTER_TOWN("c65", Boolean.FALSE),
		TRAINING_CENTER_PROVINCE("c66", Boolean.FALSE),
		TRAINING_CENTER_PHONE_1("c67", Boolean.FALSE),
		TRAINING_CENTER_PHONE_2("c68", Boolean.FALSE),
		TRAINING_CENTER_PHONE_3("c69", Boolean.FALSE),
		TRAINING_CENTER_PHONE_4("c70", Boolean.FALSE),
		TRAINING_CENTER_PHONE_5("c71", Boolean.FALSE),
		TRAINING_CENTER_PHONE_6("c72", Boolean.FALSE),
		TRAINING_CENTER_PHONE_7("c73", Boolean.FALSE),
		TRAINING_CENTER_PHONE_8("c74", Boolean.FALSE),
		TRAINING_CENTER_PHONE_9("c75", Boolean.FALSE),
		TRAINING_CENTER_EMAIL("c76", Boolean.FALSE),
		
		// TRAINING COURSE FIELDS
		TRAINING_COURSE_NAME("c77", Boolean.FALSE),
		TRAINING_COURSE_CODE("c78", Boolean.FALSE),
		TRAINING_COURSE_CLASSROOM("c79", Boolean.FALSE),
		TRAINING_COURSE_DISTANCE("c80", Boolean.FALSE),
		TRAINING_COURSE_PHONE_LEARNING("c81", Boolean.FALSE),
		TRAINING_COURSE_MIXED("c80", Boolean.FALSE),
		TRAINING_COURSE_START_DATE("c83", Boolean.FALSE),
		TRAINING_COURSE_END_DATE("c84", Boolean.FALSE),
		TRAINING_COURSE_SCHEDULE("c85", Boolean.FALSE),
		TRAINING_COURSE_FIRST_YEAR_MAIN_HOURS("c86", Boolean.FALSE),
		TRAINING_COURSE_FIRST_YEAR_COMPLEMENTARY_HOURS("c87", Boolean.FALSE),
		TRAINING_COURSE_NEXT_YEAR_MAIN_HOURS("c88", Boolean.FALSE),
		TRAINING_COURSE_NEXT_YEAR_COMPLEMENTARY_HOURS("c89", Boolean.FALSE),

		// FOORTER FIELDS
		SIGNATURE_PLACE("c90", Boolean.FALSE),
		SIGNATURE_DAY("c91", Boolean.FALSE),
		SIGNATURE_MONTH("c92", Boolean.FALSE),
		SIGNATURE_YEAR("c93", Boolean.FALSE),
		SIGNATURE_EMPLOYEE("c94", Boolean.FALSE),
		SIGNATURE_EMPLOYEE_DIR_STAFF("c95", Boolean.FALSE),
		SIGNATURE_ENTERPRISE_DIR_STAFF("c96", Boolean.FALSE),
		SIGNATURE_TRAINING_CENTER_DIR_STAFF("c97", Boolean.FALSE),
		;
		
		private String value;
		private boolean overridable;
		
		private LearningAnnex2(String value, boolean overridable) {
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
	
	