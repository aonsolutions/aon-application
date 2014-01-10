package com.esferalia.aon.file.payroll.contract.pdf.model;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

import org.apache.commons.lang.StringUtils;

import com.code.aon.common.ManagerBeanException;
import com.esferalia.aon.file.payroll.contract.pdf.UnsupportedContractDocumentException;
import com.esferalia.aon.file.payroll.contrata.ContrataContratoParams;
import com.esferalia.aon.file.payroll.contrata.IContrataParams;
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.payroll.enumeration.ContractCode;
import com.esferalia.aon.payroll.enumeration.contrata.TEQPTIEM;
import com.lowagie.text.pdf.PdfReader;



public class ModelPE179 extends AbstractContractModel {
	
	public final static String MODEL_NAME = "PE179";
	
	public ModelPE179(){
		super.documentName = MODEL_NAME;
	}
	
	@Override
	public void loadPdfFields(ContractCode code, Contract contract, IContrataParams contrataParams) throws UnsupportedContractDocumentException{
		// TODO
		try {
			PdfReader reader = new PdfReader(getContractModelUrl(documentName+".pdf"));
			
			readPdfFields(reader);
			
			if(code == ContractCode.C410){
				getPdfFieldsMap().get(PE179FieldName.TC2_410.getValue()).setValue("true");
			} else if(code == ContractCode.C510){
				getPdfFieldsMap().get(PE179FieldName.TC2_510.getValue()).setValue("true");
			} else {
				throw new UnsupportedContractDocumentException("El modelo de contrato seleccionado es incorrecto");
			}
			
			ContrataContratoParams contrata = (ContrataContratoParams) contrataParams;
			
			super.loadPdfCommonFields(contract, contrataParams);
			
			SimpleDateFormat dateFormatter = new SimpleDateFormat();
			
			/*
			 * Contract page 1
			 */
			getPdfFieldsMap().get(PE179FieldName.LEGAL_REPRESENTATIVE_NAME.getValue()).setValue(null);
			getPdfFieldsMap().get(PE179FieldName.LEGAL_REPRESENTATIVE_NIF.getValue()).setValue(null);
			getPdfFieldsMap().get(PE179FieldName.LEGAL_REPRESENTATIVE_CHARGE.getValue()).setValue(null);
			
			getPdfFieldsMap().get(PE179FieldName.INTERIM_CAUSE_MATERNITY.getValue()).setValue(null);
			getPdfFieldsMap().get(PE179FieldName.INTERIM_CAUSE_PATERNITY.getValue()).setValue(null);
			getPdfFieldsMap().get(PE179FieldName.INTERIM_CAUSE_ADOPTION.getValue()).setValue(null);
			getPdfFieldsMap().get(PE179FieldName.INTERIM_CAUSE_PREADOPTIVE_PLACEMENT.getValue()).setValue(null);
			getPdfFieldsMap().get(PE179FieldName.INTERIM_CAUSE_RISK_DURING_PREGNANCY.getValue()).setValue(null);
			getPdfFieldsMap().get(PE179FieldName.INTERIM_CAUSE_RISK_DURING_LACTATION.getValue()).setValue(null);
			
			/*
			 * Contract page 2
			 */
			getPdfFieldsMap().get(PE179FieldName.PROFESSION.getValue()).setValue(contrata.getCno().getTitle());
			getPdfFieldsMap().get(PE179FieldName.CATEGORY.getValue()).setValue(contract.getCategoryDescription());
			getPdfFieldsMap().get(PE179FieldName.WORKPLACE_FULL_ADDRESS.getValue()).setValue(null);
			getPdfFieldsMap().get(PE179FieldName.WORKPLACE_FULL_ADDRESS_MORE.getValue()).setValue(contract.getWorkPlace().getAddress().getFullAddress()+", "+contract.getWorkPlace().getAddress().getLocation());
			
			if(code == ContractCode.C410){
				getPdfFieldsMap().get(PE179FieldName.FULL_TIME.getValue()).setValue("true");
				if(StringUtils.isNotBlank(getContractInfoMap(contract).get(PE179FieldName.FULL_TIME_WEEK_HOURS.toString()))){
					getPdfFieldsMap().get( PE179FieldName.FULL_TIME_WEEK_HOURS.getValue()).setValue(getContractInfoMap(contract).get(PE179FieldName.FULL_TIME_WEEK_HOURS.toString()));
				}
				if(StringUtils.isNotBlank(getContractInfoMap(contract).get(PE179FieldName.FULL_TIME_START_TIME.toString()))){
					getPdfFieldsMap().get( PE179FieldName.FULL_TIME_START_TIME.getValue()).setValue(getContractInfoMap(contract).get(PE179FieldName.FULL_TIME_START_TIME.toString()));
				}
				if(StringUtils.isNotBlank(getContractInfoMap(contract).get(PE179FieldName.FULL_TIME_END_TIME.toString()))){
					getPdfFieldsMap().get( PE179FieldName.FULL_TIME_END_TIME.getValue()).setValue(getContractInfoMap(contract).get(PE179FieldName.FULL_TIME_END_TIME.toString()));
				}
			} else if(code == ContractCode.C510){
				getPdfFieldsMap().get(PE179FieldName.PARTIALLY_TIME.getValue()).setValue("true");
				getPdfFieldsMap().get(PE179FieldName.PARTIALLY_TIME_HOURS.getValue()).setValue(String.valueOf(Integer.parseInt(contrata.getHorasJornada())));
				if(contrata.getTipoJornada()==TEQPTIEM.TEQPTIEM_D){
					getPdfFieldsMap().get( PE179FieldName.PARTIALLY_TIME_DAYLY.getValue()).setValue("true");
				} else if(contrata.getTipoJornada()==TEQPTIEM.TEQPTIEM_S){
					getPdfFieldsMap().get( PE179FieldName.PARTIALLY_TIME_WEEKLY.getValue()).setValue("true");
				} else if(contrata.getTipoJornada()==TEQPTIEM.TEQPTIEM_M){
					getPdfFieldsMap().get( PE179FieldName.PARTIALLY_TIME_MONTHLY.getValue()).setValue("true");
				} else if(contrata.getTipoJornada()==TEQPTIEM.TEQPTIEM_A){
					getPdfFieldsMap().get( PE179FieldName.PARTIALLY_TIME_YEARLY.getValue()).setValue("true");
				}
				
				// TODO: what is needed for these partially time options?
				getPdfFieldsMap().get(PE179FieldName.COMPARABLE_FULL_TIME.getValue()).setValue(null);
				getPdfFieldsMap().get(PE179FieldName.AGREEMENT_COLLECTIVE_FULL_TIME.getValue()).setValue(null);
				getPdfFieldsMap().get(PE179FieldName.LEGAL_MAX.getValue()).setValue(null);
			}
			
			getPdfFieldsMap().get(PE179FieldName.JOB_TIME_DISTRIBUTION1.getValue()).setValue(null);
			if(StringUtils.isNotBlank(getContractInfoMap(contract).get(PE179FieldName.JOB_TIME_DISTRIBUTION2.toString()))){
				getPdfFieldsMap().get(PE179FieldName.JOB_TIME_DISTRIBUTION2.getValue()).setValue(getContractInfoMap(contract).get(PE179FieldName.JOB_TIME_DISTRIBUTION2.toString()));
			}
			
			getPdfFieldsMap().get(PE179FieldName.ACTIVITY.getValue()).setValue(null);
			getPdfFieldsMap().get(PE179FieldName.ACTIVITY_CYCLIC.getValue()).setValue(null);
			getPdfFieldsMap().get(PE179FieldName.ACTIVITY_CYCLIC_DURATION.getValue()).setValue(null);
			
			
			Calendar startCalendar = new GregorianCalendar();
			startCalendar.setTime(contract.getStartDate());
			Calendar endCalendar = new GregorianCalendar();
			endCalendar.setTime(contract.getEndDate());
			int diffYear = endCalendar.get(Calendar.YEAR) - startCalendar.get(Calendar.YEAR);
			int diffMonth = diffYear * 12 + endCalendar.get(Calendar.MONTH) - startCalendar.get(Calendar.MONTH);
			getPdfFieldsMap().get( PE179FieldName.TOTAL_DURATION.getValue()).setValue(String.valueOf(diffMonth + " meses"));
			
			dateFormatter.applyPattern("dd/MM/yyyy");
			getPdfFieldsMap().get(PE179FieldName.START_DATE.getValue()).setValue(dateFormatter.format(contract.getStartDate()));
			if(contract.getEndDate()!=null){
				getPdfFieldsMap().get(PE179FieldName.END_DATE.getValue()).setValue(dateFormatter.format(contract.getEndDate()));
			}
			if(StringUtils.isNotBlank(getContractInfoMap(contract).get(PE179FieldName.TRIAL_DURATION.toString()))){
				getPdfFieldsMap().get( PE179FieldName.TRIAL_DURATION.getValue()).setValue(getContractInfoMap(contract).get(PE179FieldName.TRIAL_DURATION.toString()));
			}
			
			if(StringUtils.isNotBlank(getContractInfoMap(contract).get(PE179FieldName.SALARY_AMOUNT.toString()))){
				getPdfFieldsMap().get( PE179FieldName.SALARY_AMOUNT.getValue()).setValue(getContractInfoMap(contract).get(PE179FieldName.SALARY_AMOUNT.toString()));
			}
			if(StringUtils.isNotBlank(getContractInfoMap(contract).get(PE179FieldName.SALARY_PERIOD.toString()))){
				getPdfFieldsMap().get( PE179FieldName.SALARY_PERIOD.getValue()).setValue(getContractInfoMap(contract).get(PE179FieldName.SALARY_PERIOD.toString()));
			}
			getPdfFieldsMap().get(PE179FieldName.SALARY_PERIOD1.getValue()).setValue(null);
			if(StringUtils.isNotBlank(getContractInfoMap(contract).get(PE179FieldName.SALARY_CONCEPT.toString()))){
				getPdfFieldsMap().get( PE179FieldName.SALARY_CONCEPT.getValue()).setValue(getContractInfoMap(contract).get(PE179FieldName.SALARY_CONCEPT.toString()));
			}
			getPdfFieldsMap().get(PE179FieldName.SALARY_CONCEPT1.getValue()).setValue(null);
			if(StringUtils.isNotBlank(getContractInfoMap(contract).get(PE179FieldName.HOLIDAYS.toString()))){
				getPdfFieldsMap().get( PE179FieldName.HOLIDAYS.getValue()).setValue(getContractInfoMap(contract).get(PE179FieldName.HOLIDAYS.toString()));
			}
			
			getPdfFieldsMap().get(PE179FieldName.REPLACED_EMPLOYEE_NAME.getValue()).setValue(null);
			getPdfFieldsMap().get(PE179FieldName.REPLACED_EMPLOYEE_NIF.getValue()).setValue(null);
			
			if(StringUtils.isNotBlank(getContractInfoMap(contract).get(PE179FieldName.SEPE_MUNICIPALITY.toString()))){
				getPdfFieldsMap().get( PE179FieldName.SEPE_MUNICIPALITY.getValue()).setValue(getContractInfoMap(contract).get(PE179FieldName.SEPE_MUNICIPALITY.toString()));
			}
			
			getPdfFieldsMap().get(PE179FieldName.REPLACEMENT_DATE.getValue()).setValue(null);
			getPdfFieldsMap().get(PE179FieldName.REPLACED_CONTRACT_ID.getValue()).setValue(null);
			getPdfFieldsMap().get(PE179FieldName.OCCUPATION.getValue()).setValue(null);
			
			if(contract.getAgreementLevelCategory()!=null && contract.getAgreementLevelCategory().getId()!=null){
				getPdfFieldsMap().get( PE179FieldName.AGREEMENT_COLLECTIVE.getValue()).setValue(contract.getAgreementLevelCategory().getLevel().getAgreement().getDescription());
			}
			getPdfFieldsMap().get(PE179FieldName.AGREEMENT_COLLECTIVE2.getValue()).setValue(null);

			getPdfFieldsMap().get(PE179FieldName.ADDITIONAL_CLAUSES.getValue()).setValue(null);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ManagerBeanException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/*
	 * INNER CLASSES
	 */
	public enum PE179FieldName implements IContractFieldName{
		
		/*
		 * Contract page 1
		 */
		TC2_410("tiempocompleto",Boolean.FALSE),
		TC2_510("tiempoparcial",Boolean.FALSE),
		LEGAL_REPRESENTATIVE_NAME("nomreptra",Boolean.FALSE),
		LEGAL_REPRESENTATIVE_NIF("dnireptra",Boolean.FALSE),
		LEGAL_REPRESENTATIVE_CHARGE("calireptra",Boolean.FALSE),
		INTERIM_CAUSE_MATERNITY("sel_causinter1",Boolean.FALSE),
		INTERIM_CAUSE_PATERNITY("sel_causinter2",Boolean.FALSE),
		INTERIM_CAUSE_ADOPTION("sel_causinter3",Boolean.FALSE),
		INTERIM_CAUSE_PREADOPTIVE_PLACEMENT("sel_causinter34",Boolean.FALSE),
		INTERIM_CAUSE_RISK_DURING_PREGNANCY("sel_causinter5",Boolean.FALSE),
		INTERIM_CAUSE_RISK_DURING_LACTATION("sel_causinter6",Boolean.FALSE),
		
		/*
		 * Contract page 2
		 */
		PROFESSION("profetraba",Boolean.FALSE),
		CATEGORY("catetraba",Boolean.FALSE),
		WORKPLACE_FULL_ADDRESS("ubicact2",Boolean.FALSE),
		WORKPLACE_FULL_ADDRESS_MORE("calletrab",Boolean.FALSE),
		FULL_TIME("sel_jorn1",Boolean.FALSE), 
		FULL_TIME_WEEK_HOURS("horasjorna1",Boolean.TRUE),
		FULL_TIME_START_TIME("horainicio",Boolean.TRUE),
		FULL_TIME_END_TIME("horafin",Boolean.TRUE),
		PARTIALLY_TIME("sel_jorn2",Boolean.FALSE),
		PARTIALLY_TIME_HOURS("horasjorna2",Boolean.FALSE),
		PARTIALLY_TIME_DAYLY("tipojorntp1",Boolean.FALSE),
		PARTIALLY_TIME_WEEKLY("tipojorntp2",Boolean.FALSE),
		PARTIALLY_TIME_MONTHLY("tipojorntp3",Boolean.FALSE),
		PARTIALLY_TIME_YEARLY("tipojorntp4",Boolean.FALSE),
		COMPARABLE_FULL_TIME("sel_jorn21",Boolean.FALSE),
		AGREEMENT_COLLECTIVE_FULL_TIME("sel_jorn22",Boolean.FALSE),
		LEGAL_MAX("sel_jorn23",Boolean.FALSE),
		JOB_TIME_DISTRIBUTION1("horatraba1",Boolean.FALSE),
		JOB_TIME_DISTRIBUTION2("horatraba2",Boolean.TRUE),
		ACTIVITY("activsust",Boolean.FALSE),
		ACTIVITY_CYCLIC("duractcicli",Boolean.FALSE),
		ACTIVITY_CYCLIC_DURATION("activcicli",Boolean.FALSE),
		TOTAL_DURATION("totaldura",Boolean.FALSE),
		START_DATE("fechaini",Boolean.FALSE),
		END_DATE("fechafin",Boolean.FALSE),
		TRIAL_DURATION("peridoprue",Boolean.TRUE),
		SALARY_AMOUNT("retribu",Boolean.TRUE),
		SALARY_PERIOD1("perioretri1",Boolean.FALSE),
		SALARY_PERIOD("perioretri",Boolean.TRUE),
		SALARY_CONCEPT("concepsala",Boolean.TRUE),
		SALARY_CONCEPT1("concepsala1",Boolean.FALSE),
		HOLIDAYS("vacaciones",Boolean.TRUE),
		REPLACED_EMPLOYEE_NAME("nomtrasust",Boolean.FALSE),
		REPLACED_EMPLOYEE_NIF("dnitrasus",Boolean.FALSE),
		SEPE_MUNICIPALITY("oecomusus",Boolean.TRUE),
		REPLACEMENT_DATE("fecharegsus",Boolean.FALSE),
		REPLACED_CONTRACT_ID("idcontsus",Boolean.FALSE),
		OCCUPATION("puestodesem",Boolean.FALSE),
		AGREEMENT_COLLECTIVE("convcole",Boolean.FALSE),
		AGREEMENT_COLLECTIVE2("convcole2",Boolean.FALSE),
		ADDITIONAL_CLAUSES("clausadici",Boolean.FALSE),
		;
		
		private String value;
		private boolean overridable;
		
		private PE179FieldName(String value, boolean overridable) {
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
	
	