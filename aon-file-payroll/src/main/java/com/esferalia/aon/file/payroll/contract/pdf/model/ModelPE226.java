package com.esferalia.aon.file.payroll.contract.pdf.model;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
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
import com.lowagie.text.pdf.PdfReader;



public class ModelPE226 extends AbstractContractModel {
	
	public final static String MODEL_NAME = "PE226";
	
	public ModelPE226(){
		super.documentName = MODEL_NAME;
	}
	
	@Override
	public void loadPdfFieldValues(ContractCode code, Contract contract, IContrataParams contrataParams) throws UnsupportedContractDocumentException{
		// TODO

		ContrataContratoParams params = (ContrataContratoParams) contrataParams;
		
		try {
			PdfReader reader = new PdfReader(getContractModelUrl(documentName+".pdf"));
			
			readPdfFields(reader);
			
			if(code == ContractCode.C421){
				if( isQuoteBonus(contract)!=null ){
					if( isQuoteBonus(contract) ){
						getPdfFieldsMap().get(PE226FieldName.QUOTE_BONUS.getValue()).setValue("true");
					} else if( !isQuoteBonus(contract) ){
						getPdfFieldsMap().get(PE226FieldName.QUOTE_NO_BONUS.getValue()).setValue("true");
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
				getPdfFieldsMap().get(PE226FieldName.ENTERPRISE_COUNTRY1.getValue()).setValue(contract.getWorkPlace().getEnterprise().getRegistry().getNationality().getName(getLocale()));
			} catch (StringIndexOutOfBoundsException aie) {
				// do nothing
			} catch (NullPointerException npe) {
				// do nothing
			}
			
			try {
				RegistryAddress address = contract.getWorkPlace().getEnterprise().getRegistry().getDefaultAddress();
				ResourceBundle bundle = ResourceBundle.getBundle("com.esferalia.aon.payroll.i18n.municipalities");
				getPdfFieldsMap().get(PE226FieldName.ENTERPRISE_MUNICIPALITY1.getValue()).setValue(bundle.getString(address.getMunicipalityCode()));
			} catch (NullPointerException npe) {
				// do nothing
			}
			
			try {
				GeoZone country = obtainCountry(contract.getWorkPlace().getAddress().getGeozone());
				getPdfFieldsMap().get(PE226FieldName.WORKPLACE_COUNTRY1.getValue()).setValue(country.getName());
			} catch (StringIndexOutOfBoundsException aie) {
				// do nothing
			} catch (NullPointerException npe) {
				// do nothing
			}
			try {
				RegistryAddress address = contract.getWorkPlace().getAddress();
				ResourceBundle bundle = ResourceBundle.getBundle("com.esferalia.aon.payroll.i18n.municipalities");
				getPdfFieldsMap().get(PE226FieldName.WORKPLACE_MUNICIPALITY1.getValue()).setValue(bundle.getString(address.getMunicipalityCode()));
			} catch (NullPointerException npe) {
				// do nothing
			}
			try {
				RegistryAddress address = contract.getPerson().getRegistry().getDefaultAddress();
				ResourceBundle bundle = ResourceBundle.getBundle("com.esferalia.aon.payroll.i18n.municipalities");
				getPdfFieldsMap().get(PE226FieldName.EMPLOYEE_ADDRESS_MUNICIPALITY1.getValue()).setValue(bundle.getString(address.getMunicipalityCode()));
			} catch (NullPointerException npe) {
				// do nothing
			}
			try {
				GeoZone country = obtainCountry(contract.getPerson().getRegistry().getDefaultAddress().getGeozone());
				getPdfFieldsMap().get(PE226FieldName.EMPLOYEE_ADDRESS_COUNTRY1.getValue()).setValue(country.getName());
			} catch (StringIndexOutOfBoundsException aie) {
				// do nothing
			} catch (NullPointerException npe) {
				// do nothing
			}
			/*
			 * FIXME: FIELDS OVERRIDES
			 */
			
			
			getPdfFieldsMap().get(PE226FieldName.REPRESENTATIVE_NAME.getValue()).setValue("");
			getPdfFieldsMap().get(PE226FieldName.REPRESENTATIVE_DOCUMENT.getValue()).setValue("");
			getPdfFieldsMap().get(PE226FieldName.REPRESENTATIVE_FUNCTION.getValue()).setValue("");
			if( isQuoteBonus(contract)!=null ){
				if( isQuoteBonus(contract) ){
					getPdfFieldsMap().get(PE226FieldName.QUOTE_BONUS_YES.getValue()).setValue("true");
				} else if( !isQuoteBonus(contract) ){
					getPdfFieldsMap().get(PE226FieldName.QUOTE_BONUS_NO.getValue()).setValue("true");
				}
			}
			getPdfFieldsMap().get(PE226FieldName.EMPLOYEE_OPT1.getValue()).setValue("");
			getPdfFieldsMap().get(PE226FieldName.EMPLOYEE_OPT2.getValue()).setValue("");
			getPdfFieldsMap().get(PE226FieldName.EMPLOYEE_OPT3.getValue()).setValue("");
			
			Map<String, String>  map = getContractDataMap(contract);
			TrainingCourse trainingCourse = obtainTrainingCourse(map.get(ContractVariable.TRAINING_COURSE.getValue()));
			
			if(trainingCourse!=null){
				getPdfFieldsMap().get(PE226FieldName.EMPLOYEE_PROFFESION.getValue()).setValue(trainingCourse.getOccupationName());
				getPdfFieldsMap().get(PE226FieldName.EMPLOYEE_CATEGORY.getValue()).setValue(trainingCourse.getOccupationName());
				
				String cno = trainingCourse.getCNO().getCode();
				if( !StringUtils.isEmpty(cno) ){
					getPdfFieldsMap().get(PE226FieldName.CNO1.getValue()).setValue(cno.substring(0, 1));
					getPdfFieldsMap().get(PE226FieldName.CNO2.getValue()).setValue(cno.substring(1, 2));
					getPdfFieldsMap().get(PE226FieldName.CNO3.getValue()).setValue(cno.substring(2, 3));
					getPdfFieldsMap().get(PE226FieldName.CNO4.getValue()).setValue(cno.substring(3, 4));
				}
			}
			
			getPdfFieldsMap().get(PE226FieldName.WORKPLACE_ADDRESS.getValue()).setValue(contract.getWorkPlace().getAddress().getFullAddress()+", "+contract.getWorkPlace().getAddress().getGeozone().getName());
			
			/*
			 * PAGE 2
			 */
			if(contrataParams!=null){
				SimpleDateFormat dateFormatter = new SimpleDateFormat();
				if(params.getHorasJornada()!=null){
					getPdfFieldsMap().get(PE226FieldName.YEAR_1_JOURNAL.getValue()).setValue(String.valueOf(Integer.parseInt(params.getHorasJornada())));
					getPdfFieldsMap().get(PE226FieldName.YEAR_2_3_JOURNAL.getValue()).setValue(""); 
					if(params.getHorasFormacion()!=null){
						Integer horasJornada = Integer.parseInt(params.getHorasJornada());
						Integer horasFormacion = Integer.parseInt(params.getHorasFormacion());
						getPdfFieldsMap().get(PE226FieldName.TOTAL_HOURS.getValue()).setValue(String.valueOf(horasJornada - horasFormacion));
						if(horasJornada!=null && horasJornada!=0){
							getPdfFieldsMap().get(PE226FieldName.JOURNAL_PERCENT.getValue()).setValue(String.valueOf(100-(horasFormacion*100/horasJornada)));
						}
					}
				}
				
				PayrollWorkPlace pw = obtainPayrollWorkPlace(contract.getWorkPlace());
				if(pw!=null && pw.getAgreement()!=null){
					getPdfFieldsMap().get(PE226FieldName.COLLECTIVE_AGREEMENT.getValue()).setValue(pw.getAgreement().getDescription());
				}				
				getPdfFieldsMap().get(PE226FieldName.JOURNAL_HORUS.getValue()).setValue(map.get(ContractVariable.WORK_SCHEDULE.getValue()));
				Integer durationInMonths = getMonthsBetweenDates(contract.getStartDate(), contract.getEndDate());
				getPdfFieldsMap().get(PE226FieldName.CONTRACT_DURATION.getValue()).setValue(durationInMonths!=null?durationInMonths+" meses":"");
				dateFormatter.applyPattern("dd/MM/yyyy");
				getPdfFieldsMap().get(PE226FieldName.START_DATE.getValue()).setValue(dateFormatter.format(contract.getStartDate()));
				if(contract.getEndDate()!=null){
					getPdfFieldsMap().get(PE226FieldName.END_DATE.getValue()).setValue(dateFormatter.format(contract.getEndDate()));
				}
				getPdfFieldsMap().get(PE226FieldName.TEST_PERIOD.getValue()).setValue("Según convenio");
				getPdfFieldsMap().get(PE226FieldName.SALARY.getValue()).setValue("Según convenio");
				getPdfFieldsMap().get(PE226FieldName.SALARY_PERIOD.getValue()).setValue("mensuales");
				getPdfFieldsMap().get(PE226FieldName.VACATIONS.getValue()).setValue("Según convenio");
				if( params.getPorcentajeReduccion()!=null){
					if( params.getPorcentajeReduccion().equals("75") ){
						getPdfFieldsMap().get(PE226FieldName.REDUCCTION_75.getValue()).setValue("true");
					} else if( params.getPorcentajeReduccion().equals("100") ){
						getPdfFieldsMap().get(PE226FieldName.REDUCTION_100.getValue()).setValue("true");
					}
				}
				if(pw!=null && pw.getAgreement()!=null){
					getPdfFieldsMap().get(PE226FieldName.COLLECTIVE_AGREEMENT1.getValue()).setValue(pw.getAgreement().getDescription());
					getPdfFieldsMap().get(PE226FieldName.COLLECTIVE_AGREEMENT2.getValue()).setValue("");
				}
				getPdfFieldsMap().get(PE226FieldName.MORE_CLAUSE.getValue()).setValue("");

				getPdfFieldsMap().get(PE226FieldName.SEPE_TOWN1.getValue()).setValue(contract.getWorkPlace().getAddress().getCity());
				getPdfFieldsMap().get(PE226FieldName.SEPE_TOWN2.getValue()).setValue(contract.getWorkPlace().getAddress().getCity());
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
	
	/*
	 * INNER CLASSES
	 */
	public enum PE226FieldName implements IContractFieldName{
		
		/*
		 * Contract page 1
		 */
		QUOTE_BONUS("si_bonif_cuota",Boolean.FALSE),
		QUOTE_NO_BONUS("no_bonif_cuota",Boolean.FALSE),
		REPRESENTATIVE_NAME("nomreptra",Boolean.FALSE),
		REPRESENTATIVE_DOCUMENT("dnireptra",Boolean.FALSE),
		REPRESENTATIVE_FUNCTION("calireptra",Boolean.FALSE),
		QUOTE_BONUS_YES("verificacion123",Boolean.FALSE),
		QUOTE_BONUS_NO("verificacion1234",Boolean.FALSE),
		EMPLOYEE_OPT1("entre16y30",Boolean.FALSE),
		EMPLOYEE_OPT2("trabdiscap",Boolean.FALSE),
		EMPLOYEE_OPT3("alumproy",Boolean.FALSE),
		EMPLOYEE_PROFFESION("profetraba",Boolean.FALSE),
		EMPLOYEE_CATEGORY("catetraba",Boolean.FALSE),
		CNO1("cno1",Boolean.FALSE),
		CNO2("cno2",Boolean.FALSE),
		CNO3("cno3",Boolean.FALSE),
		CNO4("cno4",Boolean.FALSE),
		WORKPLACE_ADDRESS("calletrab",Boolean.FALSE),
		
		/*
		 * Contract page 2
		 */
		YEAR_1_JOURNAL("año1",Boolean.FALSE),
		YEAR_2_3_JOURNAL("año2y3",Boolean.FALSE),
		TOTAL_HOURS("trabefec",Boolean.FALSE),
		JOURNAL_PERCENT("porcforma",Boolean.FALSE),
		COLLECTIVE_AGREEMENT("convcole",Boolean.FALSE),
		COLLECTIVE_AGREEMENT1("convcole1",Boolean.FALSE),
		JOURNAL_HORUS("jornhoraefec",Boolean.FALSE),
		CONTRACT_DURATION("totaldura",Boolean.FALSE),
		START_DATE("fechaini",Boolean.FALSE),
		END_DATE("fechafin",Boolean.FALSE),
		TEST_PERIOD("periodoprue",Boolean.FALSE),
		SALARY("retribu",Boolean.FALSE),
		SALARY_PERIOD("perioretri",Boolean.FALSE),
		VACATIONS("vacaciones",Boolean.FALSE),
		REDUCCTION_75("porc_75_250",Boolean.FALSE),
		REDUCTION_100("porc_100_249",Boolean.FALSE),
		COLLECTIVE_AGREEMENT2("convcole2",Boolean.FALSE),
		SEPE_TOWN1("munisepe1",Boolean.FALSE),
		SEPE_TOWN2("munisepe2",Boolean.FALSE),
		SIGN_TOWN("munifirma",Boolean.FALSE),
		MORE_CLAUSE("clausadici",Boolean.FALSE),
		SIGN_DAY("diafirma",Boolean.FALSE),
		SIGN_MONTH("mesfirma",Boolean.FALSE),
		SIGN_YEAR("añofirma",Boolean.FALSE),
		
		
		/*
		 * fileds with different labels
		 */
		ENTERPRISE_COUNTRY1("Texto1pais1",Boolean.FALSE),
		ENTERPRISE_MUNICIPALITY1("Texto2muni1",Boolean.FALSE),
		WORKPLACE_COUNTRY1("Texto3pais2",Boolean.FALSE),
		WORKPLACE_MUNICIPALITY1("Texto4muni2",Boolean.FALSE),
		EMPLOYEE_ADDRESS_MUNICIPALITY1("Texto6muni3",Boolean.FALSE),
		EMPLOYEE_ADDRESS_COUNTRY1("Texto7pais3",Boolean.FALSE),
		
		;
		
		private String value;
		private boolean overridable;
		
		private PE226FieldName(String value, boolean overridable) {
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
	
	