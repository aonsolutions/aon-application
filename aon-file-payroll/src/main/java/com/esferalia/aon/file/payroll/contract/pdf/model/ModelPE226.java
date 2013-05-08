package com.esferalia.aon.file.payroll.contract.pdf.model;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

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
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.file.payroll.contract.pdf.UnsupportedContractDocumentException;
import com.esferalia.aon.file.payroll.contrata.ContrataParams;
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.payroll.ContractData;
import com.esferalia.aon.payroll.PayrollWorkPlace;
import com.esferalia.aon.payroll.TrainingCourse;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.payroll.enumeration.ContractCode;
import com.lowagie.text.pdf.PdfReader;



public class ModelPE226 extends AbstractContractModel {
	
	/*
	 * Contract page 1
	 */
	final static String PE226_QUOTE_BONUS = "si_bonif_cuota";
	final static String PE226_QUOTE_NO_BONUS = "no_bonif_cuota";
	final static String PE226_REPRESENTATIVE_NAME = "nomreptra";
	final static String PE226_REPRESENTATIVE_DOCUMENT = "dnireptra";
	final static String PE226_REPRESENTATIVE_FUNCTION = "calireptra";
	final static String PE226_QUOTE_BONUS_YES = "verificacion123";
	final static String PE226_QUOTE_BONUS_NO = "verificacion1234";
	final static String PE226_EMPLOYEE_OPT1 = "entre16y30";
	final static String PE226_EMPLOYEE_OPT2 = "trabdiscap";
	final static String PE226_EMPLOYEE_OPT3 = "alumproy";
	final static String PE226_EMPLOYEE_PROFFESION = "profetraba";
	final static String PE226_EMPLOYEE_CATEGORY = "catetraba";
	final static String PE226_CNO1 = "cno1";
	final static String PE226_CNO2 = "cno2";
	final static String PE226_CNO3 = "cno3";
	final static String PE226_CNO4 = "cno4";
	final static String PE226_WORKPLACE_ADDRESS = "calletrab";
	
	/*
	 * Contract page 2
	 */
	final static String PE226_YEAR_1_JOURNAL = "año1";
	final static String PE226_YEAR_2_3_JOURNAL = "año2y3";
	final static String PE226_TOTAL_HOURS = "trabefec";
	final static String PE226_JOURNAL_PERCENT = "porcforma";
	final static String PE226_COLLECTIVE_AGREEMENT = "convcole";
	final static String PE226_COLLECTIVE_AGREEMENT1 = "convcole1";
	final static String PE226_JOURNAL_HORUS = "jornhoraefec";
	final static String PE226_CONTRACT_DURATION = "totaldura";
	final static String PE226_START_DATE = "fechaini";
	final static String PE226_END_DATE = "fechafin";
	final static String PE226_TEST_PERIOD = "periodoprue";
	final static String PE226_SALARY = "retribu";
	final static String PE226_SALARY_PERIOD = "perioretri";
	final static String PE226_VACATIONS = "vacaciones";
	final static String PE226_REDUCCTION_75 = "porc_75_250";
	final static String PE226_REDUCTION_100 = "porc_100_249";
	final static String PE226_COLLECTIVE_AGREEMENT2 = "convcole2";
	final static String PE226_SEPE_TOWN1 = "munisepe1";
	final static String PE226_SEPE_TOWN2 = "munisepe2";
	final static String PE226_SIGN_TOWN = "munifirma";
	final static String PE226_MORE_CLAUSE = "clausadici";
	final static String PE226_SING_DAY = "diafirma";
	final static String PE226_SIGN_MONTH = "mesfirma";
	final static String PE226_SIGN_YEAR = "añofirma";
	
	/*
	 * fileds with different labels
	 */
	final String ENTERPRISE_COUNTRY1 = "Texto1pais1";
	final String ENTERPRISE_TOWN1 = "Texto2muni1";
	final String WORKPLACE_COUNTRY1 = "Texto3pais2";
	final String WORKPLACE_TOWN1 = "Texto4muni2";
	final String EMPLOYEE_ADDRESS_TOWN1 = "Texto6muni3";
	final String EMPLOYEE_ADDRESS_COUNTRY1 = "Texto7pais3";
	
	public final static String MODEL_NAME = "PE226";
	
	public ModelPE226(){
		super.documentName = MODEL_NAME;
	}
	
	@Override
	public void loadPdfFields(ContractCode code, Contract contract, ContrataParams contrataParams) throws UnsupportedContractDocumentException{
		// TODO
		try {
			PdfReader reader = new PdfReader(getContractModelUrl(documentName+".pdf"));
			
			readPdfFields(reader);
			
			if(code == ContractCode.C421){
				if( isQuoteBonus(contract)!=null ){
					if( isQuoteBonus(contract) ){
						getPdfFieldsMap().get(PE226_QUOTE_BONUS).setValue("true");
					} else if( !isQuoteBonus(contract) ){
						getPdfFieldsMap().get(PE226_QUOTE_NO_BONUS).setValue("true");
					}
				}
			} else {
				throw new UnsupportedContractDocumentException("El modelo de contrato seleccionado es incorrecto");
			}
			
			super.loadPdfCommonFields(contract, contrataParams);
			
			/*
			 * FIXME: FIELDS OVERRIDES
			 * same fileds named with different labels
			 * must normalize pdf files of contract models 
			 */
			try {	
				getPdfFieldsMap().get(ENTERPRISE_COUNTRY1).setValue(contract.getWorkPlace().getEnterprise().getRegistry().getNationality().getName(getLocale()));
			} catch (StringIndexOutOfBoundsException aie) {
				// do nothing
			} catch (NullPointerException npe) {
				// do nothing
			}
			getPdfFieldsMap().get(ENTERPRISE_TOWN1).setValue(contract.getWorkPlace().getEnterprise().getRegistry().getDefaultAddress().getCity());
			try {
				GeoZone country = obtainCountry(contract.getWorkPlace().getAddress().getGeozone());
				getPdfFieldsMap().get(WORKPLACE_COUNTRY1).setValue(country.getName());
			} catch (StringIndexOutOfBoundsException aie) {
				// do nothing
			} catch (NullPointerException npe) {
				// do nothing
			}
			getPdfFieldsMap().get(EMPLOYEE_ADDRESS_TOWN1).setValue(contract.getPerson().getRegistry().getDefaultAddress()!=null?contract.getPerson().getRegistry().getDefaultAddress().getCity():null);
			try {
				GeoZone country = obtainCountry(contract.getPerson().getRegistry().getDefaultAddress().getGeozone());
				getPdfFieldsMap().get(EMPLOYEE_ADDRESS_COUNTRY1).setValue(country.getName());
			} catch (StringIndexOutOfBoundsException aie) {
				// do nothing
			} catch (NullPointerException npe) {
				// do nothing
			}
			/*
			 * FIXME: FIELDS OVERRIDES
			 */
			
			
			getPdfFieldsMap().get(PE226_REPRESENTATIVE_NAME).setValue("");
			getPdfFieldsMap().get(PE226_REPRESENTATIVE_DOCUMENT).setValue("");
			getPdfFieldsMap().get(PE226_REPRESENTATIVE_FUNCTION).setValue("");
			if( isQuoteBonus(contract)!=null ){
				if( isQuoteBonus(contract) ){
					getPdfFieldsMap().get(PE226_QUOTE_BONUS_YES).setValue("true");
				} else if( !isQuoteBonus(contract) ){
					getPdfFieldsMap().get(PE226_QUOTE_BONUS_NO).setValue("true");
				}
			}
			getPdfFieldsMap().get(PE226_EMPLOYEE_OPT1).setValue("");
			getPdfFieldsMap().get(PE226_EMPLOYEE_OPT2).setValue("");
			getPdfFieldsMap().get(PE226_EMPLOYEE_OPT3).setValue("");
			
			Map<String, String>  map = getContractDataMap(contract);
			TrainingCourse trainingCourse = obtainTrainingCourse(map.get(ContextVariable.TRAINING_COURSE.getName()));
			
			if(trainingCourse!=null){
				getPdfFieldsMap().get(PE226_EMPLOYEE_PROFFESION).setValue(trainingCourse.getOccupationName());
				getPdfFieldsMap().get(PE226_EMPLOYEE_CATEGORY).setValue(trainingCourse.getOccupationName());
				
				String cno = trainingCourse.getCNO().getCode();
				if( !StringUtils.isEmpty(cno) ){
					getPdfFieldsMap().get(PE226_CNO1).setValue(cno.substring(0, 1));
					getPdfFieldsMap().get(PE226_CNO2).setValue(cno.substring(1, 2));
					getPdfFieldsMap().get(PE226_CNO3).setValue(cno.substring(2, 3));
					getPdfFieldsMap().get(PE226_CNO4).setValue(cno.substring(3, 4));
				}
			}
			
			getPdfFieldsMap().get(PE226_WORKPLACE_ADDRESS).setValue(contract.getWorkPlace().getAddress().getFullAddress()+", "+contract.getWorkPlace().getAddress().getGeozone().getName());
			
			/*
			 * PAGE 2
			 */
			if(contrataParams!=null){
				SimpleDateFormat dateFormatter = new SimpleDateFormat();
				getPdfFieldsMap().get(PE226_YEAR_1_JOURNAL).setValue(String.valueOf(Integer.parseInt(contrataParams.getHorasJornada())));
				getPdfFieldsMap().get(PE226_YEAR_2_3_JOURNAL).setValue(""); 
				
				Integer horasJornada = Integer.parseInt(contrataParams.getHorasJornada());
				Integer horasFormacion = Integer.parseInt(contrataParams.getHorasFormacion());
				getPdfFieldsMap().get(PE226_TOTAL_HOURS).setValue(String.valueOf(horasJornada - horasFormacion));
				getPdfFieldsMap().get(PE226_JOURNAL_PERCENT).setValue(String.valueOf(100-(horasFormacion*100/horasJornada)));
				
				PayrollWorkPlace pw = obtainPayrollWorkPlace(contract.getWorkPlace());
				if(pw!=null && pw.getAgreement()!=null){
					getPdfFieldsMap().get(PE226_COLLECTIVE_AGREEMENT).setValue(pw.getAgreement().getDescription());
				}				
				getPdfFieldsMap().get(PE226_JOURNAL_HORUS).setValue("");
				Integer durationInMonths = getMonthsBetweenDates(contract.getStartDate(), contract.getEndDate());
				getPdfFieldsMap().get(PE226_CONTRACT_DURATION).setValue(durationInMonths!=null?durationInMonths+" meses":"");
				dateFormatter.applyPattern("dd/MM/yyyy");
				getPdfFieldsMap().get(PE226_START_DATE).setValue(dateFormatter.format(contract.getStartDate()));
				if(contract.getEndDate()!=null){
					getPdfFieldsMap().get(PE226_END_DATE).setValue(dateFormatter.format(contract.getEndDate()));
				}
				getPdfFieldsMap().get(PE226_TEST_PERIOD).setValue("Según convenio");
				getPdfFieldsMap().get(PE226_SALARY).setValue("Según convenio");
				getPdfFieldsMap().get(PE226_SALARY_PERIOD).setValue("mensuales");
				getPdfFieldsMap().get(PE226_VACATIONS).setValue("Según convenio");
				if( contrataParams.getPorcentajeReduccion()!=null){
					if( contrataParams.getPorcentajeReduccion().equals("75") ){
						getPdfFieldsMap().get(PE226_REDUCCTION_75).setValue("true");
					} else if( contrataParams.getPorcentajeReduccion().equals("100") ){
						getPdfFieldsMap().get(PE226_REDUCTION_100).setValue("true");
					}
				}
				if(pw!=null && pw.getAgreement()!=null){
					getPdfFieldsMap().get(PE226_COLLECTIVE_AGREEMENT1).setValue(pw.getAgreement().getDescription());
					getPdfFieldsMap().get(PE226_COLLECTIVE_AGREEMENT2).setValue("");
				}
				getPdfFieldsMap().get(PE226_MORE_CLAUSE).setValue("");

				getPdfFieldsMap().get(PE226_SEPE_TOWN1).setValue(contract.getWorkPlace().getAddress().getCity());
				getPdfFieldsMap().get(PE226_SEPE_TOWN2).setValue(contract.getWorkPlace().getAddress().getCity());
				getPdfFieldsMap().get(PE226_SIGN_TOWN).setValue(contract.getWorkPlace().getAddress().getCity());
				dateFormatter.applyPattern("dd");
				getPdfFieldsMap().get(PE226_SING_DAY).setValue(dateFormatter.format(contract.getStartDate()));
				dateFormatter.applyPattern("MMMM");
				getPdfFieldsMap().get(PE226_SIGN_MONTH).setValue(dateFormatter.format(contract.getStartDate()));
				dateFormatter.applyPattern("yy");
				getPdfFieldsMap().get(PE226_SIGN_YEAR).setValue(dateFormatter.format(contract.getStartDate()));
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ManagerBeanException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private Boolean isQuoteBonus(Contract contract) {
		String subsidized = getContractDataMap(contract).get(ContextVariable.SUBSIDIZED.getName());
		return Boolean.parseBoolean(subsidized);
	}
	
	public Map<String, String> getContractDataMap(Contract contract) {
		Map<String, String> map = new HashMap<String, String>();
		try {
			IManagerBean bean = BeanManager.getManagerBean(ContractData.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_DATA_CONTRACT_ID), contract.getId());
			criteria.addGreaterThanOrEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_DATA_START_DATE), contract.getStartDate());
			if(contract.getEndDate()!=null){
				criteria.addLessThanOrEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_DATA_END_DATE), contract.getEndDate());
			} else {
				criteria.addNullExpression(bean.getFieldName(IEntityAlias.CONTRACT_DATA_END_DATE));
			}
			for(ITransferObject to: bean.getList(criteria)){
				ContractData data = (ContractData) to;
				map.put(data.getName(), data.getExpression().replace('"', ' ').trim());
			}
		} catch (ManagerBeanException e) {
			// NADA, se devuelve un mapa vacio
			return map;
		}
		return map;
	}
	
	private Integer getMonthsBetweenDates(Date startDate, Date endDate) {
		if(startDate!=null && endDate!=null){
			return (int) ((CommonUtil.getDaysBetweenDates(startDate, endDate, true))/30);
		}
		return null;
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
	
}
	
	