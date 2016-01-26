package com.esferalia.aon.file.payroll.contract.pdf.model;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;

import com.code.aon.AonVersion;
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
import com.esferalia.aon.file.payroll.contract.pdf.ModelOption;
import com.esferalia.aon.file.payroll.contract.pdf.PdfFieldLearning;
import com.esferalia.aon.file.payroll.contract.pdf.UnsupportedContractDocumentException;
import com.esferalia.aon.file.payroll.contrata.ContrataContratoParams;
import com.esferalia.aon.file.payroll.contrata.IContrataParams;
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.payroll.ContractInfo;
import com.esferalia.aon.payroll.ContractInfo.ContractVariable;
import com.esferalia.aon.payroll.PayrollWorkPlace;
import com.esferalia.aon.payroll.TrainingCourse;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.payroll.enumeration.ContractCode;
import com.esferalia.aon.payroll.enumeration.TrainingModality;
import com.esferalia.aon.payroll.util.PayrollUtils;
import com.lowagie.text.pdf.PdfReader;



public class LearningModel extends AbstractContractModel {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	public final static String MODEL_NAME = "Formacion";
	
	public LearningModel(Contract contract){
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
			setPdfFieldValue(PdfFieldLearning.ENTERPRISE_CIF.getValue(),contract.getWorkPlace().getEnterprise().getRegistry().getDocument());
			if(StringUtils.isNotBlank(getContractInfoMap(contract).get(PdfFieldLearning.ENTERPRISE_DIR_STAFF_NAME.toString()))){
				setPdfFieldValue(PdfFieldLearning.ENTERPRISE_DIR_STAFF_NAME.getValue(), getContractInfoMap(contract).get(PdfFieldLearning.ENTERPRISE_DIR_STAFF_NAME.toString()));
			}
			if(StringUtils.isNotBlank(getContractInfoMap(contract).get(PdfFieldLearning.ENTERPRISE_DIR_STAFF_NIF.toString()))){
				setPdfFieldValue(PdfFieldLearning.ENTERPRISE_DIR_STAFF_NIF.getValue(), getContractInfoMap(contract).get(PdfFieldLearning.ENTERPRISE_DIR_STAFF_NIF.toString()));
			}
			if(StringUtils.isNotBlank(getContractInfoMap(contract).get(PdfFieldLearning.ENTERPRISE_DIR_STAFF_CHARGE.toString()))){
				setPdfFieldValue(PdfFieldLearning.ENTERPRISE_DIR_STAFF_CHARGE.getValue(), getContractInfoMap(contract).get(PdfFieldLearning.ENTERPRISE_DIR_STAFF_CHARGE.toString()));
			}
			setPdfFieldValue(PdfFieldLearning.ENTERPRISE_NAME.getValue(),contract.getWorkPlace().getEnterprise().getRegistry().getFullName());
			setPdfFieldValue(PdfFieldLearning.ENTERPRISE_ADDRESS.getValue(),contract.getWorkPlace().getEnterprise().getRegistry().getDefaultAddress().getFullAddress());
			try {	
				setPdfFieldValue(PdfFieldLearning.ENTERPRISE_COUNTRY.getValue(),contract.getWorkPlace().getEnterprise().getRegistry().getNationality().getName(getLocale()));
				setPdfFieldValue(PdfFieldLearning.ENTERPRISE_COUNTRY_CODE1.getValue(),String.valueOf(contract.getWorkPlace().getEnterprise().getRegistry().getNationality().getIsoNum()).substring(0,1));
				setPdfFieldValue(PdfFieldLearning.ENTERPRISE_COUNTRY_CODE2.getValue(),String.valueOf(contract.getWorkPlace().getEnterprise().getRegistry().getNationality().getIsoNum()).substring(1,2));
				setPdfFieldValue(PdfFieldLearning.ENTERPRISE_COUNTRY_CODE3.getValue(),String.valueOf(contract.getWorkPlace().getEnterprise().getRegistry().getNationality().getIsoNum()).substring(2,3));
			} catch (StringIndexOutOfBoundsException aie) {
				// do nothing
			} catch (NullPointerException npe) {
				// do nothing
			}
			try {	
				RegistryAddress address = contract.getWorkPlace().getEnterprise().getRegistry().getDefaultAddress();
				if(StringUtils.isNotBlank(address.getMunicipalityCode())){
					ResourceBundle bundle = ResourceBundle.getBundle(MUNICIPALITIES_BUNDLE_BASE_NAME);
					setPdfFieldValue(PdfFieldLearning.ENTERPRISE_MUNICIPALITY.getValue(),bundle.getString(address.getMunicipalityCode()));
					setPdfFieldValue(PdfFieldLearning.ENTERPRISE_MUNICIPALITY_CODE1.getValue(),address.getMunicipalityCode().substring(0, 1));
					setPdfFieldValue(PdfFieldLearning.ENTERPRISE_MUNICIPALITY_CODE2.getValue(),address.getMunicipalityCode().substring(1, 2));
					setPdfFieldValue(PdfFieldLearning.ENTERPRISE_MUNICIPALITY_CODE3.getValue(),address.getMunicipalityCode().substring(2, 3));
					setPdfFieldValue(PdfFieldLearning.ENTERPRISE_MUNICIPALITY_CODE4.getValue(),address.getMunicipalityCode().substring(3, 4));
					setPdfFieldValue(PdfFieldLearning.ENTERPRISE_MUNICIPALITY_CODE5.getValue(),address.getMunicipalityCode().substring(4, 5));
				}
			} catch (StringIndexOutOfBoundsException aie) {
				// do nothing
			} catch (NullPointerException npe) {
				// do nothing
			}
			try {	
				setPdfFieldValue(PdfFieldLearning.ENTERPRISE_ZIP1.getValue(),contract.getWorkPlace().getEnterprise().getRegistry().getDefaultAddress().getZip().substring(0, 1));
				setPdfFieldValue(PdfFieldLearning.ENTERPRISE_ZIP2.getValue(),contract.getWorkPlace().getEnterprise().getRegistry().getDefaultAddress().getZip().substring(1, 2));
				setPdfFieldValue(PdfFieldLearning.ENTERPRISE_ZIP3.getValue(),contract.getWorkPlace().getEnterprise().getRegistry().getDefaultAddress().getZip().substring(2, 3));
				setPdfFieldValue(PdfFieldLearning.ENTERPRISE_ZIP4.getValue(),contract.getWorkPlace().getEnterprise().getRegistry().getDefaultAddress().getZip().substring(3, 4));
				setPdfFieldValue(PdfFieldLearning.ENTERPRISE_ZIP5.getValue(),contract.getWorkPlace().getEnterprise().getRegistry().getDefaultAddress().getZip().substring(4, 5));
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
						setPdfFieldValue(PdfFieldLearning.CCC_REG1.getValue(),quoteRegime.substring(0, 1));
						setPdfFieldValue(PdfFieldLearning.CCC_REG2.getValue(),quoteRegime.substring(1, 2));
						setPdfFieldValue(PdfFieldLearning.CCC_REG3.getValue(),quoteRegime.substring(2, 3));
						setPdfFieldValue(PdfFieldLearning.CCC_REG4.getValue(),quoteRegime.substring(3, 4));
					}
				}
				if(contract.getEnterpriseCCC().getCcc().length()==11){
					setPdfFieldValue(PdfFieldLearning.CCC_PROV1.getValue(),contract.getEnterpriseCCC().getCcc().substring(0, 1));
					setPdfFieldValue(PdfFieldLearning.CCC_PROV2.getValue(),contract.getEnterpriseCCC().getCcc().substring(1, 2));
					setPdfFieldValue(PdfFieldLearning.CCC_NISS.getValue(),contract.getEnterpriseCCC().getCcc().substring(2, 9));
					setPdfFieldValue(PdfFieldLearning.CCC_CONTROL_DIGIT1.getValue(),contract.getEnterpriseCCC().getCcc().substring(9, 10));
					setPdfFieldValue(PdfFieldLearning.CCC_CONTROL_DIGIT2.getValue(),contract.getEnterpriseCCC().getCcc().substring(10, 11));
				} else {
					setPdfFieldValue(PdfFieldLearning.CCC_NISS.getValue(),contract.getEnterpriseCCC().getCcc());
				}
				setPdfFieldValue(PdfFieldLearning.CCC_ACTIVITY.getValue(),contract.getEnterpriseCCC().getActivity().getDescription());
				setPdfFieldValue(PdfFieldLearning.CCC_ACTIVITY_CODE1.getValue(),contract.getEnterpriseCCC().getActivity().getCnae2009().getCode().substring(0, 1));
				setPdfFieldValue(PdfFieldLearning.CCC_ACTIVITY_CODE2.getValue(),contract.getEnterpriseCCC().getActivity().getCnae2009().getCode().substring(1, 2));
			}
			/* 
			 * Contract workplace fields
			 */
			try {
				GeoZone country = obtainCountry(contract.getWorkPlace().getAddress().getGeozone());
				setPdfFieldValue(PdfFieldLearning.WORKPLACE_COUNTRY.getValue(),country.getName());
				setPdfFieldValue(PdfFieldLearning.WORKPLACE_COUNTRY_CODE1.getValue(),String.valueOf(obtainCountryByValue(country).getIsoNum()).substring(0,1));
				setPdfFieldValue(PdfFieldLearning.WORKPLACE_COUNTRY_CODE2.getValue(),String.valueOf(obtainCountryByValue(country).getIsoNum()).substring(1,2));
				setPdfFieldValue(PdfFieldLearning.WORKPLACE_COUNTRY_CODE3.getValue(),String.valueOf(obtainCountryByValue(country).getIsoNum()).substring(2,3));
			} catch (StringIndexOutOfBoundsException aie) {
				// do nothing
			} catch (NullPointerException npe) {
				// do nothing
			}
			try {
				RegistryAddress address = contract.getWorkPlace().getAddress();
				if(StringUtils.isNotBlank(address.getMunicipalityCode())){
					ResourceBundle bundle = ResourceBundle.getBundle(MUNICIPALITIES_BUNDLE_BASE_NAME);
					setPdfFieldValue(PdfFieldLearning.WORKPLACE_MUNICIPALITY.getValue(),bundle.getString(address.getMunicipalityCode()));
					setPdfFieldValue(PdfFieldLearning.WORKPLACE_MUNICIPALITY_CODE1.getValue(),address.getMunicipalityCode().substring(0, 1));
					setPdfFieldValue(PdfFieldLearning.WORKPLACE_MUNICIPALITY_CODE2.getValue(),address.getMunicipalityCode().substring(1, 2));
					setPdfFieldValue(PdfFieldLearning.WORKPLACE_MUNICIPALITY_CODE3.getValue(),address.getMunicipalityCode().substring(2, 3));
					setPdfFieldValue(PdfFieldLearning.WORKPLACE_MUNICIPALITY_CODE4.getValue(),address.getMunicipalityCode().substring(3, 4));
					setPdfFieldValue(PdfFieldLearning.WORKPLACE_MUNICIPALITY_CODE5.getValue(),address.getMunicipalityCode().substring(4, 5));
				}
			} catch (StringIndexOutOfBoundsException aie) {
				// do nothing
			} catch (NullPointerException npe) {
				// do nothing
			}
			/*
			 * Contract employee fields
			 */
			setPdfFieldValue(PdfFieldLearning.EMPLOYEE_NAME.getValue(),contract.getPerson().getFullName());
			setPdfFieldValue(PdfFieldLearning.EMPLOYEE_NIF.getValue(),contract.getPerson().getRegistry().getDocument());
			if(contract.getPerson().getBirthDate()!=null){
				SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
				setPdfFieldValue(PdfFieldLearning.EMPLOYEE_BIRTH_DATE.getValue(),formatter.format(contract.getPerson().getBirthDate()));
			}
			setPdfFieldValue(PdfFieldLearning.EMPLOYEE_NSS.getValue(),contract.getPerson().getSocialSecurityNumber());
			if(contrata!=null && contrata.getNivelFormativo()!=null){
				setPdfFieldValue(PdfFieldLearning.EMPLOYEE_FORMATION_LEVEL.getValue(),contrata.getNivelFormativo().getDescription());
				setPdfFieldValue(PdfFieldLearning.EMPLOYEE_FORMATION_CODE1.getValue(),contrata.getNivelFormativo().getCode().substring(0, 1));
				setPdfFieldValue(PdfFieldLearning.EMPLOYEE_FORMATION_CODE2.getValue(),contrata.getNivelFormativo().getCode().substring(1, 2));
			}
			try {
				setPdfFieldValue(PdfFieldLearning.EMPLOYEE_COUNTRY.getValue(),String.valueOf(contract.getPerson().getRegistry().getNationality().getName(getLocale())));
				setPdfFieldValue(PdfFieldLearning.EMPLOYEE_COUNTRY_CODE1.getValue(),String.valueOf(contract.getPerson().getRegistry().getNationality().getIsoNum()).substring(0,1));
				setPdfFieldValue(PdfFieldLearning.EMPLOYEE_COUNTRY_CODE2.getValue(),String.valueOf(contract.getPerson().getRegistry().getNationality().getIsoNum()).substring(1,2));
				setPdfFieldValue(PdfFieldLearning.EMPLOYEE_COUNTRY_CODE3.getValue(),String.valueOf(contract.getPerson().getRegistry().getNationality().getIsoNum()).substring(2,3));
			} catch (StringIndexOutOfBoundsException aie) {
				// do nothing
			} catch (NullPointerException npe) {
				// do nothing
			}
			try {
				RegistryAddress address = contract.getPerson().getRegistry().getDefaultAddress();
				if(StringUtils.isNotBlank(address.getMunicipalityCode())){
					ResourceBundle bundle = ResourceBundle.getBundle(MUNICIPALITIES_BUNDLE_BASE_NAME);
					setPdfFieldValue(PdfFieldLearning.EMPLOYEE_ADDRESS_MUNICIPALITY.getValue(),bundle.getString(address.getMunicipalityCode()));
					setPdfFieldValue(PdfFieldLearning.EMPLOYEE_ADDRESS_MUNICIPALITY_CODE1.getValue(),address.getMunicipalityCode().substring(0, 1));
					setPdfFieldValue(PdfFieldLearning.EMPLOYEE_ADDRESS_MUNICIPALITY_CODE2.getValue(),address.getMunicipalityCode().substring(1, 2));
					setPdfFieldValue(PdfFieldLearning.EMPLOYEE_ADDRESS_MUNICIPALITY_CODE3.getValue(),address.getMunicipalityCode().substring(2, 3));
					setPdfFieldValue(PdfFieldLearning.EMPLOYEE_ADDRESS_MUNICIPALITY_CODE4.getValue(),address.getMunicipalityCode().substring(3, 4));
					setPdfFieldValue(PdfFieldLearning.EMPLOYEE_ADDRESS_MUNICIPALITY_CODE5.getValue(),address.getMunicipalityCode().substring(4, 5));
				}
			} catch (StringIndexOutOfBoundsException aie) {
				// do nothing
			} catch (NullPointerException npe) {
				// do nothing
			}
			try {
				GeoZone country = obtainCountry(contract.getPerson().getRegistry().getDefaultAddress().getGeozone());
				setPdfFieldValue(PdfFieldLearning.EMPLOYEE_ADDRESS_COUNTRY.getValue(),country.getName());
				setPdfFieldValue(PdfFieldLearning.EMPLOYEE_ADDRESS_COUNTRY_CODE1.getValue(),String.valueOf(obtainCountryByValue(country).getIsoNum()).substring(0,1));
				setPdfFieldValue(PdfFieldLearning.EMPLOYEE_ADDRESS_COUNTRY_CODE2.getValue(),String.valueOf(obtainCountryByValue(country).getIsoNum()).substring(1,2));
				setPdfFieldValue(PdfFieldLearning.EMPLOYEE_ADDRESS_COUNTRY_CODE3.getValue(),String.valueOf(obtainCountryByValue(country).getIsoNum()).substring(2,3));
			} catch (StringIndexOutOfBoundsException aie) {
				// do nothing
			} catch (NullPointerException npe) {
				// do nothing
			}
			try {
				if(StringUtils.isNotBlank(getContractInfoMap(contract).get(PdfFieldLearning.LEGAL_REPRESENTATIVE_NAME.toString()))){
					setPdfFieldValue(PdfFieldLearning.LEGAL_REPRESENTATIVE_NAME.getValue(), getContractInfoMap(contract).get(PdfFieldLearning.LEGAL_REPRESENTATIVE_NAME.toString()));
				}
				if(StringUtils.isNotBlank(getContractInfoMap(contract).get(PdfFieldLearning.LEGAL_REPRESENTATIVE_NIF.toString()))){
					setPdfFieldValue(PdfFieldLearning.LEGAL_REPRESENTATIVE_NIF.getValue(), getContractInfoMap(contract).get(PdfFieldLearning.LEGAL_REPRESENTATIVE_NIF.toString()));
				}
				if(StringUtils.isNotBlank(getContractInfoMap(contract).get(PdfFieldLearning.LEGAL_REPRESENTATIVE_CHARGE.toString()))){
					setPdfFieldValue(PdfFieldLearning.LEGAL_REPRESENTATIVE_CHARGE.getValue(), getContractInfoMap(contract).get(PdfFieldLearning.LEGAL_REPRESENTATIVE_CHARGE.toString()));
				}
			} catch (StringIndexOutOfBoundsException aie) {
				// do nothing
			} catch (NullPointerException npe) {
				// do nothing
			}
			
			
//			String subsidized = getContractDataMap(contract).get(ContextVariable.SUBSIDIZED.getName());
//			if( subsidized!=null ){
//				if( Boolean.parseBoolean(subsidized) ){
//					setPdfFieldValue(PdfFieldLearning.QUOTE_BONUS_YES.getValue(),"true");
//				} else if( !Boolean.parseBoolean(subsidized) ){
//					setPdfFieldValue(PdfFieldLearning.QUOTE_BONUS_NO.getValue(),"true");
//				}
//			}
			String quoteBonus = getContractInfoMap(contract).get(PdfFieldLearning.QUOTE_BONUS.toString());
			if(StringUtils.isNotBlank(quoteBonus)){
				if(PdfFieldLearning.QUOTE_BONUS_YES.toString().equals(quoteBonus)){
					setPdfFieldValue(PdfFieldLearning.QUOTE_BONUS_YES.getValue(),"true");
				} else if(PdfFieldLearning.QUOTE_BONUS_NO.toString().equals(quoteBonus)){
					setPdfFieldValue(PdfFieldLearning.QUOTE_BONUS_NO.getValue(),"true");
				}
			}
			
			String employeeOpt = getContractInfoMap(contract).get(PdfFieldLearning.EMPLOYEE_OPT.toString());
			if(StringUtils.isNotBlank(employeeOpt)){
				if(PdfFieldLearning.EMPLOYEE_OPT1.toString().equals(employeeOpt)){
					setPdfFieldValue(PdfFieldLearning.EMPLOYEE_OPT1.getValue(),"true");
				} else if(PdfFieldLearning.EMPLOYEE_OPT2.toString().equals(employeeOpt)){
					setPdfFieldValue(PdfFieldLearning.EMPLOYEE_OPT2.getValue(),"true");
				} else if(PdfFieldLearning.EMPLOYEE_OPT3.toString().equals(employeeOpt)){
					setPdfFieldValue(PdfFieldLearning.EMPLOYEE_OPT3.getValue(),"true");
				} else if(PdfFieldLearning.EMPLOYEE_OPT4.toString().equals(employeeOpt)){
					setPdfFieldValue(PdfFieldLearning.EMPLOYEE_OPT4.getValue(),"true");
				}
			}
			
			TrainingCourse trainingCourse = obtainTrainingCourse(getContractInfoMap(contract).get(ContractVariable.TRAINING_COURSE.getValue()));
			if(trainingCourse!=null){
				setPdfFieldValue(PdfFieldLearning.ACTIVITY_EMPLOYEE_PROFFESION.getValue(),trainingCourse.getOccupationName());
				setPdfFieldValue(PdfFieldLearning.ACTIVITY_EMPLOYEE_CATEGORY.getValue(),trainingCourse.getOccupationName());
				
				String cno = trainingCourse.getCNO().getCode();
				if( !StringUtils.isEmpty(cno) ){
					setPdfFieldValue(PdfFieldLearning.CNO1.getValue(),cno.substring(0, 1));
					setPdfFieldValue(PdfFieldLearning.CNO2.getValue(),cno.substring(1, 2));
					setPdfFieldValue(PdfFieldLearning.CNO3.getValue(),cno.substring(2, 3));
					setPdfFieldValue(PdfFieldLearning.CNO4.getValue(),cno.substring(3, 4));
				}
			} else {
				if(contrata!=null && contrata.getCno()!=null ){
					setPdfFieldValue(PdfFieldLearning.ACTIVITY_EMPLOYEE_PROFFESION.getValue(), contrata.getCno().getTitle());
					setPdfFieldValue(PdfFieldLearning.ACTIVITY_EMPLOYEE_CATEGORY.getValue(), contract.getCategoryDescription());
					String cno = contrata.getCno().getCode();
					if( !StringUtils.isEmpty(cno) ){
						setPdfFieldValue(PdfFieldLearning.CNO1.getValue(),cno.substring(0, 1));
						setPdfFieldValue(PdfFieldLearning.CNO2.getValue(),cno.substring(1, 2));
						setPdfFieldValue(PdfFieldLearning.CNO3.getValue(),cno.substring(2, 3));
						setPdfFieldValue(PdfFieldLearning.CNO4.getValue(),cno.substring(3, 4));
					}
				}
			}
			
			String wpFullAddress = contract.getWorkPlace().getAddress().getFullAddress();
			String wpGeozoneName = contract.getWorkPlace().getAddress().getGeozone().getName();
			setPdfFieldValue(PdfFieldLearning.ACTIVITY_WORKPLACE_ADDRESS2.getValue(), wpFullAddress + ", " + wpGeozoneName);
			
			/*
			 * Contract page 2
			 */
			setPdfFieldValue(PdfFieldLearning.CONTRACT_EMPLOYEE_PROFFESION.getValue(), contrata.getCno().getTitle());
			setPdfFieldValue(PdfFieldLearning.CONTRACT_EMPLOYEE_CATEGORY.getValue(), contract.getCategoryDescription());
			if(StringUtils.isNotBlank(getContractInfoMap(contract).get(PdfFieldLearning.CONTRACT_WORKPLACE_ADDRESS.toString()))){
				setPdfFieldValue(PdfFieldLearning.CONTRACT_WORKPLACE_ADDRESS.getValue(), getContractInfoMap(contract).get(PdfFieldLearning.CONTRACT_WORKPLACE_ADDRESS.toString()));
			}
			
			if(StringUtils.isNotBlank(getContractInfoMap(contract).get(PdfFieldLearning.FORMATION_TEACHER.toString()))){
				setPdfFieldValue(PdfFieldLearning.FORMATION_TEACHER.getValue(), getContractInfoMap(contract).get(PdfFieldLearning.FORMATION_TEACHER.toString()));
			}
			if(StringUtils.isNotBlank(getContractInfoMap(contract).get(PdfFieldLearning.FORMATION_TEACHER_QUALIFICATION.toString()))){
				setPdfFieldValue(PdfFieldLearning.FORMATION_TEACHER_QUALIFICATION.getValue(), getContractInfoMap(contract).get(PdfFieldLearning.FORMATION_TEACHER_QUALIFICATION.toString()));
			}
			
			if(contrata!=null){
				if(contrata.getHorasJornada()!=null){
					setPdfFieldValue(PdfFieldLearning.JOURNAL_HOURS.getValue(), String.valueOf(Integer.parseInt(contrata.getHorasJornada())));
					if(contrata.getHorasFormacion()!=null){
						Integer horasJornada = Integer.parseInt(contrata.getHorasJornada());
						Integer horasFormacion = Integer.parseInt(contrata.getHorasFormacion());
						setPdfFieldValue(PdfFieldLearning.TOTAL_HOURS.getValue(), String.valueOf(horasFormacion));
						if(horasJornada!=null && horasJornada!=0){
							setPdfFieldValue(PdfFieldLearning.JOURNAL_PERCENT.getValue(), String.valueOf(100-(horasFormacion*100/horasJornada)));
						}
					}
				}
			}
			
			PayrollWorkPlace pw = obtainPayrollWorkPlace(contract.getWorkPlace());
			if(pw!=null && pw.getAgreement()!=null){
				setPdfFieldValue(PdfFieldLearning.JOURNAL_COLLECTIVE_AGREEMENT.getValue(), pw.getAgreement().getDescription());
			}
			
			if(StringUtils.isNotBlank(getContractInfoMap(contract).get(PdfFieldLearning.HORARIO_LABORAL.toString()))){
				setPdfFieldValue(PdfFieldLearning.HORARIO_LABORAL.getValue(),getContractInfoMap(contract).get(ContractVariable.WORK_SCHEDULE.getValue()));
			}
			if(StringUtils.isNotBlank(getContractInfoMap(contract).get(PdfFieldLearning.HORARIO_LECTIVO.toString()))){
				setPdfFieldValue(PdfFieldLearning.HORARIO_LECTIVO2.getValue(),getContractInfoMap(contract).get(ContractVariable.TRAINING_SCHEDULE.getValue()));
			}

			Integer durationInMonths = getMonthsBetweenDates(contract.getStartDate(), contract.getEndDate());
			setPdfFieldValue(PdfFieldLearning.CONTRACT_DURATION.getValue(), durationInMonths!=null?durationInMonths+" meses":"");
			dateFormatter.applyPattern("dd/MM/yyyy");
			setPdfFieldValue(PdfFieldLearning.START_DATE.getValue(), dateFormatter.format(contract.getStartDate()));
			if(contract.getEndDate()!=null){
				setPdfFieldValue(PdfFieldLearning.END_DATE.getValue(), dateFormatter.format(contract.getEndDate()));
			}
			
			
			if(StringUtils.isNotBlank(getContractInfoMap(contract).get(PdfFieldLearning.TRIAL_DURATION.toString()))){
				setPdfFieldValue(PdfFieldLearning.TRIAL_DURATION.getValue(),getContractInfoMap(contract).get(PdfFieldLearning.TRIAL_DURATION.toString()));
			}
			String trialDurationIncrease = getContractInfoMap(contract).get(PdfFieldLearning.TRIAL_DURATION_INCREASE.toString());
			if(StringUtils.isNotBlank(trialDurationIncrease) && new Boolean(trialDurationIncrease)){
				setPdfFieldValue(PdfFieldLearning.TRIAL_DURATION_INCREASE.getValue(), "true");
			}
			
			if(StringUtils.isNotBlank(getContractInfoMap(contract).get(PdfFieldLearning.SALARY_AMOUNT.toString()))){
				setPdfFieldValue(PdfFieldLearning.SALARY_AMOUNT.getValue(),getContractInfoMap(contract).get(PdfFieldLearning.SALARY_AMOUNT.toString()));
			}
			if(StringUtils.isNotBlank(getContractInfoMap(contract).get(PdfFieldLearning.SALARY_PERIOD.toString()))){
				setPdfFieldValue(PdfFieldLearning.SALARY_PERIOD.getValue(),getContractInfoMap(contract).get(PdfFieldLearning.SALARY_PERIOD.toString()));
			}
			if(StringUtils.isNotBlank(getContractInfoMap(contract).get(PdfFieldLearning.HOLIDAYS.toString()))){
				setPdfFieldValue(PdfFieldLearning.HOLIDAYS.getValue(),getContractInfoMap(contract).get(PdfFieldLearning.HOLIDAYS.toString()));
			}
			
			String annex1check = getContractInfoMap(contract).get(PdfFieldLearning.ANNEX_I_CHECK.toString());
			if(StringUtils.isNotBlank(annex1check) && new Boolean(annex1check)){
				setPdfFieldValue(PdfFieldLearning.ANNEX_I_CHECK.getValue(), "true");
			}
			String annex2check = getContractInfoMap(contract).get(PdfFieldLearning.ANNEX_II_CHECK.toString());
			if(StringUtils.isNotBlank(annex2check) && new Boolean(annex2check)){
				setPdfFieldValue(PdfFieldLearning.ANNEX_II_CHECK.getValue(), "true");
			}
			
			if(pw!=null && pw.getAgreement()!=null){
				setPdfFieldValue(PdfFieldLearning.COLLECTIVE_AGREEMENT.getValue(), pw.getAgreement().getDescription());
			}
			
			setPdfFieldValue(PdfFieldLearning.SEPE_TOWN_FOR_CONTRACT_START.getValue(), contract.getWorkPlace().getAddress().getCity());
			setPdfFieldValue(PdfFieldLearning.SEPE_TOWN_FOR_CONTRACT_END.getValue(), contract.getWorkPlace().getAddress().getCity());
			
			
			setPdfFieldValue(PdfFieldLearning.SIGN_TOWN.getValue(),contract.getWorkPlace().getAddress().getCity());
			dateFormatter.applyPattern("dd");
			setPdfFieldValue(PdfFieldLearning.SIGN_DAY.getValue(),dateFormatter.format(contract.getStartDate()));
			dateFormatter.applyPattern("MMMM");
			setPdfFieldValue(PdfFieldLearning.SIGN_MONTH.getValue(),dateFormatter.format(contract.getStartDate()));
			dateFormatter.applyPattern("yy");
			setPdfFieldValue(PdfFieldLearning.SIGN_YEAR.getValue(),dateFormatter.format(contract.getStartDate()));
			
			
			/*
			 *  OPTIONS PAGE
			 */
			ModelOption modelOption = ModelOption.valueOf(getContractInfoMap(contract).get(ContractVariable.CONTRACT_MODEL_OPTION.getValue()));
			
			if(modelOption == ModelOption.LEARNING_OPT1){
				setPdfFieldValue(PdfFieldLearning.MAIN_OPT1_CHECK.getValue(),"true");
				setPdfFieldValue(PdfFieldLearning.OPT1_OPTION_CHECK.getValue(),"true");
				if(StringUtils.isNotBlank(quoteBonus)){
					if(PdfFieldLearning.QUOTE_BONUS_YES.toString().equals(quoteBonus)){
						setPdfFieldValue(PdfFieldLearning.OPT1_QUOTE_BONUS.getValue(),"true");
					} else if(PdfFieldLearning.QUOTE_BONUS_NO.toString().equals(quoteBonus)){
						setPdfFieldValue(PdfFieldLearning.OPT1_QUOTE_NO_BONUS.getValue(),"true");
					}
				}
			} else if(modelOption == ModelOption.LEARNING_OPT2){
				setPdfFieldValue(PdfFieldLearning.MAIN_OPT2_CHECK.getValue(),"true");
				// TODO: complete this option
			} else if(modelOption == ModelOption.LEARNING_OPT3){
				setPdfFieldValue(PdfFieldLearning.MAIN_OPT3_CHECK.getValue(),"true");
				// TODO: complete this option
			} else if(modelOption == ModelOption.LEARNING_OPT4){
				setPdfFieldValue(PdfFieldLearning.MAIN_OPT4_CHECK.getValue(),"true");
				// TODO: complete this option
			}
			
			// ANEX I fields: because its length is defined forward
			// TODO: complete this option
			
			// ANEX II fields: because its length is defined forward
//			TrainingCourse trainingCourse = obtainTrainingCourse(map.get(ContractVariable.TRAINING_COURSE.getValue()));
			
			if(StringUtils.isNotBlank(annex2check) && new Boolean(annex2check) && trainingCourse != null){
				// HEADER FIELDS
				if(trainingCourse.isProfessionalCertificate()){
					setPdfFieldValue(PdfFieldLearning.ANNEXII_CP_YES.getValue(),"true");
				} else {
					setPdfFieldValue(PdfFieldLearning.ANNEXII_CP_NO.getValue(),"true");
				}
				if(trainingCourse.isFpTitle()){
					setPdfFieldValue(PdfFieldLearning.ANNEXII_FP_YES.getValue(),"true");
				} else {
					setPdfFieldValue(PdfFieldLearning.ANNEXII_FP_NO.getValue(),"true");
				}
				if(trainingCourse.isCenterAvailable()){
					setPdfFieldValue(PdfFieldLearning.ANNEXII_CENTER_AVAILABLE_YES.getValue(),"true");
				} else {
					setPdfFieldValue(PdfFieldLearning.ANNEXII_CENTER_AVAILABLE_NO.getValue(),"true");
				}
				
				setPdfFieldValue(PdfFieldLearning.ANNEXII_CP_NAME.getValue(),trainingCourse.getCertificationName());
				setPdfFieldValue(PdfFieldLearning.ANNEXII_FP_TITLE.getValue(),trainingCourse.getFpTitleName());
			}
			
			// ENTERPRISE FIELDS
			setPdfFieldValue(PdfFieldLearning.ANNEXII_ENTERPRISE_NAME.getValue(),contract.getWorkPlace().getEnterprise().getRegistry().getFullName());
			setPdfFieldValue(PdfFieldLearning.ANNEXII_ENTERPRISE_CIF.getValue(),contract.getWorkPlace().getEnterprise().getRegistry().getDocument());
			RegistryDirStaff enterpriseDirStaff = obtainRegistryDirStaff(contract.getWorkPlace().getEnterprise().getRegistry()); 
			try {
				setPdfFieldValue(PdfFieldLearning.ANNEXII_ENTERPRISE_DIR_STAFF_NAME.getValue(),enterpriseDirStaff.getName());
				setPdfFieldValue(PdfFieldLearning.ANNEXII_ENTERPRISE_DIR_STAFF_NIF.getValue(),enterpriseDirStaff.getDocument());
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
				setPdfFieldValue(PdfFieldLearning.ANNEXII_ENTERPRISE_DIR_STAFF_CHARGE.getValue(),rDirStaddCharge);
			} catch (NullPointerException npe) {
				// do nothing
			}
			
			String phone = contract.getWorkPlace().getEnterprise().getRegistry().getPhone().getValue();
			try {
				phone = phone.replaceAll("[^0-9]", "");
				setPdfFieldValue(PdfFieldLearning.ANNEXII_ENTERPRISE_PHONE_1.getValue(),phone.substring(0, 1));
				setPdfFieldValue(PdfFieldLearning.ANNEXII_ENTERPRISE_PHONE_2.getValue(),phone.substring(1, 2));
				setPdfFieldValue(PdfFieldLearning.ANNEXII_ENTERPRISE_PHONE_3.getValue(),phone.substring(2, 3));
				setPdfFieldValue(PdfFieldLearning.ANNEXII_ENTERPRISE_PHONE_4.getValue(),phone.substring(3, 4));
				setPdfFieldValue(PdfFieldLearning.ANNEXII_ENTERPRISE_PHONE_5.getValue(),phone.substring(4, 5));
				setPdfFieldValue(PdfFieldLearning.ANNEXII_ENTERPRISE_PHONE_6.getValue(),phone.substring(5, 6));
				setPdfFieldValue(PdfFieldLearning.ANNEXII_ENTERPRISE_PHONE_7.getValue(),phone.substring(6, 7));
				setPdfFieldValue(PdfFieldLearning.ANNEXII_ENTERPRISE_PHONE_8.getValue(),phone.substring(7, 8));
				setPdfFieldValue(PdfFieldLearning.ANNEXII_ENTERPRISE_PHONE_9.getValue(),phone.substring(8, 9));
			} catch (StringIndexOutOfBoundsException aie) {
				// do nothing
			} catch (NullPointerException npe) {
				// do nothing
			}
			String email = contract.getWorkPlace().getEnterprise().getRegistry().getEmail().getValue();
			setPdfFieldValue(PdfFieldLearning.ANNEXII_ENTERPRISE_EMAIL.getValue(),email);
			
			// EMPLOYEE FIELDS
			setPdfFieldValue(PdfFieldLearning.ANNEXII_EMPLOYEE_NAME.getValue(),contract.getPerson().getFullName());
			setPdfFieldValue(PdfFieldLearning.ANNEXII_EMPLOYEE_NIF.getValue(),contract.getPerson().getRegistry().getDocument());
			
			// CONTRACT FIELDS
			try {
				setPdfFieldValue(PdfFieldLearning.ANNEXII_CONTRACT_ID_NUMBER_1.getValue(),getContractInfoMap(contract).get(ContractVariable.SEPE_CONTRACT_ID.getValue()).substring(0, 1));
				setPdfFieldValue(PdfFieldLearning.ANNEXII_CONTRACT_ID_NUMBER_2.getValue(),getContractInfoMap(contract).get(ContractVariable.SEPE_CONTRACT_ID.getValue()).substring(1, 2));
				setPdfFieldValue(PdfFieldLearning.ANNEXII_CONTRACT_ID_NUMBER_3.getValue(),getContractInfoMap(contract).get(ContractVariable.SEPE_CONTRACT_ID.getValue()).substring(2, 3));
				setPdfFieldValue(PdfFieldLearning.ANNEXII_CONTRACT_ID_NUMBER_4.getValue(),getContractInfoMap(contract).get(ContractVariable.SEPE_CONTRACT_ID.getValue()).substring(3, 4));
				setPdfFieldValue(PdfFieldLearning.ANNEXII_CONTRACT_ID_NUMBER_5.getValue(),getContractInfoMap(contract).get(ContractVariable.SEPE_CONTRACT_ID.getValue()).substring(4, 5));
				setPdfFieldValue(PdfFieldLearning.ANNEXII_CONTRACT_ID_NUMBER_6.getValue(),getContractInfoMap(contract).get(ContractVariable.SEPE_CONTRACT_ID.getValue()).substring(5, 6));
				setPdfFieldValue(PdfFieldLearning.ANNEXII_CONTRACT_ID_NUMBER_7.getValue(),getContractInfoMap(contract).get(ContractVariable.SEPE_CONTRACT_ID.getValue()).substring(6, 7));
				setPdfFieldValue(PdfFieldLearning.ANNEXII_CONTRACT_ID_NUMBER_8.getValue(),getContractInfoMap(contract).get(ContractVariable.SEPE_CONTRACT_ID.getValue()).substring(7, 8));
				setPdfFieldValue(PdfFieldLearning.ANNEXII_CONTRACT_ID_NUMBER_9.getValue(),getContractInfoMap(contract).get(ContractVariable.SEPE_CONTRACT_ID.getValue()).substring(8, 9));
				setPdfFieldValue(PdfFieldLearning.ANNEXII_CONTRACT_ID_NUMBER_10.getValue(),getContractInfoMap(contract).get(ContractVariable.SEPE_CONTRACT_ID.getValue()).substring(9, 10));
				setPdfFieldValue(PdfFieldLearning.ANNEXII_CONTRACT_ID_NUMBER_11.getValue(),getContractInfoMap(contract).get(ContractVariable.SEPE_CONTRACT_ID.getValue()).substring(10, 11));
				setPdfFieldValue(PdfFieldLearning.ANNEXII_CONTRACT_ID_NUMBER_12.getValue(),getContractInfoMap(contract).get(ContractVariable.SEPE_CONTRACT_ID.getValue()).substring(11, 12));
				setPdfFieldValue(PdfFieldLearning.ANNEXII_CONTRACT_ID_NUMBER_13.getValue(),getContractInfoMap(contract).get(ContractVariable.SEPE_CONTRACT_ID.getValue()).substring(12, 13));
				setPdfFieldValue(PdfFieldLearning.ANNEXII_CONTRACT_ID_NUMBER_14.getValue(),getContractInfoMap(contract).get(ContractVariable.SEPE_CONTRACT_ID.getValue()).substring(13, 14));
				setPdfFieldValue(PdfFieldLearning.ANNEXII_CONTRACT_ID_NUMBER_15.getValue(),getContractInfoMap(contract).get(ContractVariable.SEPE_CONTRACT_ID.getValue()).substring(14, 15));
				setPdfFieldValue(PdfFieldLearning.ANNEXII_CONTRACT_ID_NUMBER_16.getValue(),getContractInfoMap(contract).get(ContractVariable.SEPE_CONTRACT_ID.getValue()).substring(15, 16));
			} catch (StringIndexOutOfBoundsException aie) {
				// do nothing
			} catch (NullPointerException npe) {
				// do nothing
			}
			
			SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
			if(contract.getStartDate()!=null){
				setPdfFieldValue(PdfFieldLearning.ANNEXII_CONTRACT_START_DATE.getValue(),formatter.format(contract.getStartDate()));
			}
			if(contract.getEndDate()!=null){
				setPdfFieldValue(PdfFieldLearning.ANNEXII_CONTRACT_END_DATE.getValue(),formatter.format(contract.getEndDate()));
			}
			if(trainingCourse != null){
				setPdfFieldValue(PdfFieldLearning.ANNEXII_CONTRACT_OCCUPATION.getValue(),trainingCourse.getOccupationName());
				String cno = getContractDataMap(contract).get(ContextVariable.CNO.getName());
				if( !StringUtils.isEmpty(cno) ){
					setPdfFieldValue(PdfFieldLearning.ANNEXII_CONTRACT_CNO_1.getValue(),cno.substring(0, 1));
					setPdfFieldValue(PdfFieldLearning.ANNEXII_CONTRACT_CNO_2.getValue(),cno.substring(1, 2));
					setPdfFieldValue(PdfFieldLearning.ANNEXII_CONTRACT_CNO_3.getValue(),cno.substring(2, 3));
					setPdfFieldValue(PdfFieldLearning.ANNEXII_CONTRACT_CNO_4.getValue(),cno.substring(3, 4));
					setPdfFieldValue(PdfFieldLearning.ANNEXII_CONTRACT_CNO_5.getValue(),"");
					setPdfFieldValue(PdfFieldLearning.ANNEXII_CONTRACT_CNO_6.getValue(),"");
					setPdfFieldValue(PdfFieldLearning.ANNEXII_CONTRACT_CNO_7.getValue(),"");
					setPdfFieldValue(PdfFieldLearning.ANNEXII_CONTRACT_CNO_8.getValue(),"");
				}
			}
			
			
			// TRAINING CENTER FIELDS
			RegistryDirStaff trainingCenterDirStaff = null;
			if(trainingCourse != null){
				setPdfFieldValue(PdfFieldLearning.ANNEXII_TRAINING_CENTER_CODE.getValue(),trainingCourse.getTrainingCenter().getCode());
				trainingCenterDirStaff = obtainRegistryDirStaff(trainingCourse.getTrainingCenter().getRegistry());
				try {
					if(trainingCenterDirStaff != null){
						setPdfFieldValue(PdfFieldLearning.ANNEXII_TRAINING_CENTER_DIR_STAFF_NAME.getValue(),trainingCenterDirStaff.getName());
						setPdfFieldValue(PdfFieldLearning.ANNEXII_TRAINING_CENTER_DIR_STAFF_NIF.getValue(),trainingCenterDirStaff.getDocument());
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
						setPdfFieldValue(PdfFieldLearning.ANNEXII_TRAINING_CENTER_DIR_STAFF_CHARGE.getValue(),rDirStaddCharge);
					}
				} catch (NullPointerException npe) {
					// do nothing
				}
				
				try {	
					setPdfFieldValue(PdfFieldLearning.ANNEXII_TRAINING_CENTER_NAME.getValue(),trainingCourse.getTrainingCenter().getRegistry().getFullName());
				} catch (NullPointerException npe) {
					// do nothing
				}
				try {	
					setPdfFieldValue(PdfFieldLearning.ANNEXII_TRAINING_CENTER_NIF.getValue(),trainingCourse.getTrainingCenter().getRegistry().getDocument());
				} catch (NullPointerException npe) {
					// do nothing
				}
				try {	
					setPdfFieldValue(PdfFieldLearning.ANNEXII_TRAINING_CENTER_ADDRESS.getValue(),trainingCourse.getTrainingCenter().getRegistry().getDefaultAddress().getFullAddress());
				} catch (NullPointerException npe) {
					// do nothing
				}
				try {	
					setPdfFieldValue(PdfFieldLearning.ANNEXII_TRAINING_CENTER_ZIP_1.getValue(),trainingCourse.getTrainingCenter().getRegistry().getDefaultAddress().getZip().substring(0, 1));
					setPdfFieldValue(PdfFieldLearning.ANNEXII_TRAINING_CENTER_ZIP_2.getValue(),trainingCourse.getTrainingCenter().getRegistry().getDefaultAddress().getZip().substring(1, 2));
					setPdfFieldValue(PdfFieldLearning.ANNEXII_TRAINING_CENTER_ZIP_3.getValue(),trainingCourse.getTrainingCenter().getRegistry().getDefaultAddress().getZip().substring(2, 3));
					setPdfFieldValue(PdfFieldLearning.ANNEXII_TRAINING_CENTER_ZIP_4.getValue(),trainingCourse.getTrainingCenter().getRegistry().getDefaultAddress().getZip().substring(3, 4));
					setPdfFieldValue(PdfFieldLearning.ANNEXII_TRAINING_CENTER_ZIP_5.getValue(),trainingCourse.getTrainingCenter().getRegistry().getDefaultAddress().getZip().substring(4, 5));
				} catch (StringIndexOutOfBoundsException aie) {
					// do nothing
				} catch (NullPointerException npe) {
					// do nothing
				}
				try {	
					setPdfFieldValue(PdfFieldLearning.ANNEXII_TRAINING_CENTER_TOWN.getValue(),trainingCourse.getTrainingCenter().getRegistry().getDefaultAddress().getCity());
				} catch (NullPointerException npe) {
					// do nothing
				}
				try {	
					setPdfFieldValue(PdfFieldLearning.ANNEXII_TRAINING_CENTER_PROVINCE.getValue(),trainingCourse.getTrainingCenter().getRegistry().getDefaultAddress().getGeozone().getName());
				} catch (NullPointerException npe) {
					// do nothing
				}
				
				try {
					String centerPhone = trainingCourse.getTrainingCenter().getRegistry().getPhone().getValue();
					centerPhone = centerPhone.replaceAll("[^0-9]", "");
					setPdfFieldValue(PdfFieldLearning.ANNEXII_TRAINING_CENTER_PHONE_1.getValue(),centerPhone.substring(0, 1));
					setPdfFieldValue(PdfFieldLearning.ANNEXII_TRAINING_CENTER_PHONE_2.getValue(),centerPhone.substring(1, 2));
					setPdfFieldValue(PdfFieldLearning.ANNEXII_TRAINING_CENTER_PHONE_3.getValue(),centerPhone.substring(2, 3));
					setPdfFieldValue(PdfFieldLearning.ANNEXII_TRAINING_CENTER_PHONE_4.getValue(),centerPhone.substring(3, 4));
					setPdfFieldValue(PdfFieldLearning.ANNEXII_TRAINING_CENTER_PHONE_5.getValue(),centerPhone.substring(4, 5));
					setPdfFieldValue(PdfFieldLearning.ANNEXII_TRAINING_CENTER_PHONE_6.getValue(),centerPhone.substring(5, 6));
					setPdfFieldValue(PdfFieldLearning.ANNEXII_TRAINING_CENTER_PHONE_7.getValue(),centerPhone.substring(6, 7));
					setPdfFieldValue(PdfFieldLearning.ANNEXII_TRAINING_CENTER_PHONE_8.getValue(),centerPhone.substring(7, 8));
					setPdfFieldValue(PdfFieldLearning.ANNEXII_TRAINING_CENTER_PHONE_9.getValue(),centerPhone.substring(8, 9));
				} catch (StringIndexOutOfBoundsException aie) {
					// do nothing
				} catch (NullPointerException npe) {
					// do nothing
				}
				
				try {	
					String centerEmail = trainingCourse.getTrainingCenter().getRegistry().getEmail().getValue();
					setPdfFieldValue(PdfFieldLearning.ANNEXII_TRAINING_CENTER_EMAIL.getValue(),centerEmail);
				} catch (NullPointerException npe) {
					// do nothing
				}
				
				// TRAINING COURSE FIELDS
				if(trainingCourse.getModality()==TrainingModality.CLASSROOM){
					setPdfFieldValue(PdfFieldLearning.ANNEXII_TRAINING_COURSE_CLASSROOM.getValue(),"true");
				} else if(trainingCourse.getModality()==TrainingModality.DISTANCE){
					setPdfFieldValue(PdfFieldLearning.ANNEXII_TRAINING_COURSE_DISTANCE.getValue(),"true");
				} else if(trainingCourse.getModality()==TrainingModality.PHONE){
					setPdfFieldValue(PdfFieldLearning.ANNEXII_TRAINING_COURSE_PHONE_LEARNING.getValue(),"true");
				} else if(trainingCourse.getModality()==TrainingModality.MIX){
					setPdfFieldValue(PdfFieldLearning.ANNEXII_TRAINING_COURSE_MIXED.getValue(),"true");
				}
				ContractInfo trainingCourseInfo = obtainContractInfo(contract, ContractVariable.TRAINING_COURSE.getValue());
				if(trainingCourseInfo!=null && trainingCourseInfo.getStartDate()!=null){
					setPdfFieldValue(PdfFieldLearning.ANNEXII_TRAINING_COURSE_START_DATE.getValue(),formatter.format(trainingCourseInfo.getStartDate()));
				}
				if(trainingCourseInfo!=null && trainingCourseInfo.getEndDate()!=null){
					setPdfFieldValue(PdfFieldLearning.ANNEXII_TRAINING_COURSE_END_DATE.getValue(),formatter.format(trainingCourseInfo.getEndDate()));
				}
			}
			setPdfFieldValue(PdfFieldLearning.ANNEXII_TRAINING_COURSE_SCHEDULE.getValue(),getContractDataMap(contract).get(ContractVariable.TRAINING_SCHEDULE.getValue()));
			if(contrataParams!=null){
				
				if(durationInMonths!=null){
					
					Integer horasFormacion = (durationInMonths<6)?(258):(516);
					
					setPdfFieldValue(PdfFieldLearning.ANNEXII_TRAINING_COURSE_FIRST_YEAR_MAIN_HOURS.getValue(),String.valueOf(horasFormacion));
				}
			}
			setPdfFieldValue(PdfFieldLearning.ANNEXII_TRAINING_COURSE_FIRST_YEAR_COMPLEMENTARY_HOURS.getValue(),"");
			setPdfFieldValue(PdfFieldLearning.ANNEXII_TRAINING_COURSE_NEXT_YEAR_MAIN_HOURS.getValue(),"");
			setPdfFieldValue(PdfFieldLearning.ANNEXII_TRAINING_COURSE_NEXT_YEAR_COMPLEMENTARY_HOURS.getValue(),"");

			// FOORTER FIELDS
			setPdfFieldValue(PdfFieldLearning.ANNEXII_SIGNATURE_PLACE.getValue(),contract.getWorkPlace().getAddress().getCity());
			setPdfFieldValue(PdfFieldLearning.ANNEXII_SIGNATURE_DAY.getValue(),String.valueOf(CommonUtil.getDay(new Date())));
			
			formatter.applyPattern("MMMM");
			setPdfFieldValue(PdfFieldLearning.ANNEXII_SIGNATURE_MONTH.getValue(),formatter.format(new Date()) );
			formatter.applyPattern("yy");
			setPdfFieldValue(PdfFieldLearning.ANNEXII_SIGNATURE_YEAR.getValue(),formatter.format(new Date()));
			setPdfFieldValue(PdfFieldLearning.ANNEXII_SIGNATURE_EMPLOYEE.getValue(),contract.getPerson().getFullName());
			setPdfFieldValue(PdfFieldLearning.ANNEXII_SIGNATURE_EMPLOYEE_DIR_STAFF.getValue(),"");
			try {	
				setPdfFieldValue(PdfFieldLearning.ANNEXII_SIGNATURE_ENTERPRISE_DIR_STAFF.getValue(),enterpriseDirStaff.getName());
			} catch (NullPointerException npe) {
				// do nothing
			}
			try {	
				setPdfFieldValue(PdfFieldLearning.ANNEXII_SIGNATURE_TRAINING_CENTER_DIR_STAFF.getValue(),trainingCenterDirStaff.getName());
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
	
	private ContractInfo obtainContractInfo(Contract contract, String name) {
		try {
			IManagerBean bean = BeanManager.getManagerBean(ContractInfo.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_INFO_CONTRACT_ID), contract.getId() );
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_INFO_NAME), name );
			List<ITransferObject> list = bean.getList(criteria);
			if( !list.isEmpty() ){
				return (ContractInfo) list.get(0);
			}
		} catch (ManagerBeanException e) {
			// do nothing ...
		}
		return null;
	}
	
}
	
	