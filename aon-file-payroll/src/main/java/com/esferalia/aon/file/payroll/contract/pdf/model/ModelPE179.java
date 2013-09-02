package com.esferalia.aon.file.payroll.contract.pdf.model;

import java.io.IOException;

import com.code.aon.common.ManagerBeanException;
import com.esferalia.aon.file.payroll.contract.pdf.UnsupportedContractDocumentException;
import com.esferalia.aon.file.payroll.contrata.IContrataParams;
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.payroll.enumeration.ContractCode;
import com.lowagie.text.pdf.PdfReader;



public class ModelPE179 extends AbstractContractModel {
	
	/*
	 * Contract page 1
	 */
	final static String PE179_TC2_410 = "tiempocompleto";
	final static String PE179_TC2_510 = "tiempoparcial";
	final static String PE179_LEGAL_REPRESENTATIVE_NAME = "nomreptra";
	final static String PE179_LEGAL_REPRESENTATIVE_NIF = "dnireptra";
	final static String PE179_LEGAL_REPRESENTATIVE_CHARGE = "calireptra";
	final static String PE179_INTERIM_CAUSE_MATERNITY = "sel_causinter1";
	final static String PE179_INTERIM_CAUSE_PATERNITY = "sel_causinter2";
	final static String PE179_INTERIM_CAUSE_ADOPTION = "sel_causinter3";
	final static String PE179_INTERIM_CAUSE_PREADOPTIVE_PLACEMENT = "sel_causinter34";
	final static String PE179_INTERIM_CAUSE_RISK_DURING_PREGNANCY = "sel_causinter5";
	final static String PE179_INTERIM_CAUSE_RISK_DURING_LACTATION = "sel_causinter6";
	
	/*
	 * Contract page 2
	 */
	final static String PE179_PROFESSION = "profetraba";
	final static String PE179_CATEGORY = "catetraba";
	final static String PE179_WORKPLACE_FULL_ADDRESS = "ubicact2";
	final static String PE179_WORKPLACE_FULL_ADDRESS_MORE = "calletrab";
	final static String PE179_FULL_TIME = "sel_jorn1"; 
	final static String PE179_FULL_TIME_WEEK_HOURS = "horasjorna1";
	final static String PE179_FULL_TIME_FROM_TIME = "horainicio";
	final static String PE179_FULL_TIME_TO_TIME = "horafin";
	final static String PE179_PARTIALLY_TIME = "sel_jorn2";
	final static String PE179_PARTIALLY_TIME_HOURS = "horasjorna2";
	final static String PE179_PARTIALLY_TIME_DAYLY = "tipojorntp1";
	final static String PE179_PARTIALLY_TIME_WEEKLY = "tipojorntp2";
	final static String PE179_PARTIALLY_TIME_MONTHLY = "tipojorntp3";
	final static String PE179_PARTIALLY_TIME_YEARLY = "tipojorntp4";
	final static String PE179_COMPARABLE_FULL_TIME = "sel_jorn21";
	final static String PE179_AGREEMENT_COLLECTIVE_FULL_TIME = "sel_jorn22";
	final static String PE179_LEGAL_MAX = "sel_jorn23";
	final static String PE179_JOB_TIME_DISTRIBUTION1 = "horatraba1";
	final static String PE179_JOB_TIME_DISTRIBUTION2 = "horatraba2";
	final static String PE179_ACTIVITY = "activsust";
	final static String PE179_ACTIVITY_CYCLIC = "duractcicli";
	final static String PE179_ACTIVITY_CYCLIC_DURATION = "activcicli";
	final static String PE176_TOTAL_DURATION = "totaldura";
	final static String PE176_START_DATE = "fechaini";
	final static String PE176_END_DATE = "fechafin";
	final static String PE179_TRIAL_DURATION = "peridoprue";
	final static String PE179_SALARY_AMOUNT = "retribu";
	final static String PE179_SALARY_PERIOD1 = "perioretri1";
	final static String PE179_SALARY_PERIOD = "perioretri";
	final static String PE179_SALARY_CONCEPT = "concepsala";
	final static String PE179_SALARY_CONCEPT1 = "concepsala1";
	final static String PE179_HOLIDAYS = "vacaciones";
	final static String PE179_REPLACED_EMPLOYEE_NAME = "nomtrasust";
	final static String PE179_REPLACED_EMPLOYEE_NIF = "dnitrasus";
	final static String PE179_SEPE_MUNICIPALITY = "oecomusus";
	final static String PE179_REPLACEMENT_DATE = "fecharegsus";
	final static String PE179_REPLACED_CONTRACT_ID = "idcontsus";
	final static String PE179_OCCUPATION = "puestodesem";
	final static String PE179_AGREEMENT_COLLECTIVE = "convcole";
	final static String PE179_AGREEMENT_COLLECTIVE2 = "convcole2";
	final static String PE179_ADDITIONAL_CLAUSES = "clausadici";
	
	
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
				getPdfFieldsMap().get(PE179_TC2_410).setValue("true");
			} else if(code == ContractCode.C510){
				getPdfFieldsMap().get(PE179_TC2_510).setValue("true");
			} else {
				throw new UnsupportedContractDocumentException("El modelo de contrato seleccionado es incorrecto");
			}
			
			super.loadPdfCommonFields(contract, contrataParams);
			
			
			/*
			 * Contract page 1
			 */
			getPdfFieldsMap().get(PE179_LEGAL_REPRESENTATIVE_NAME).setValue(null);
			getPdfFieldsMap().get(PE179_LEGAL_REPRESENTATIVE_NIF).setValue(null);
			getPdfFieldsMap().get(PE179_LEGAL_REPRESENTATIVE_CHARGE).setValue(null);
			getPdfFieldsMap().get(PE179_INTERIM_CAUSE_MATERNITY).setValue(null);
			getPdfFieldsMap().get(PE179_INTERIM_CAUSE_PATERNITY).setValue(null);
			getPdfFieldsMap().get(PE179_INTERIM_CAUSE_ADOPTION).setValue(null);
			getPdfFieldsMap().get(PE179_INTERIM_CAUSE_PREADOPTIVE_PLACEMENT).setValue(null);
			getPdfFieldsMap().get(PE179_INTERIM_CAUSE_RISK_DURING_PREGNANCY).setValue(null);
			getPdfFieldsMap().get(PE179_INTERIM_CAUSE_RISK_DURING_LACTATION).setValue(null);
			
			/*
			 * Contract page 2
			 */
			getPdfFieldsMap().get(PE179_PROFESSION).setValue(null);
			getPdfFieldsMap().get(PE179_CATEGORY).setValue(null);
			getPdfFieldsMap().get(PE179_WORKPLACE_FULL_ADDRESS).setValue(null);
			getPdfFieldsMap().get(PE179_WORKPLACE_FULL_ADDRESS_MORE).setValue(null);
			getPdfFieldsMap().get(PE179_FULL_TIME).setValue(null);
			getPdfFieldsMap().get(PE179_FULL_TIME_WEEK_HOURS).setValue(null);
			getPdfFieldsMap().get(PE179_FULL_TIME_FROM_TIME).setValue(null);
			getPdfFieldsMap().get(PE179_FULL_TIME_TO_TIME).setValue(null);
			getPdfFieldsMap().get(PE179_PARTIALLY_TIME).setValue(null);
			getPdfFieldsMap().get(PE179_PARTIALLY_TIME_HOURS).setValue(null);
			getPdfFieldsMap().get(PE179_PARTIALLY_TIME_DAYLY).setValue(null);
			getPdfFieldsMap().get(PE179_PARTIALLY_TIME_WEEKLY).setValue(null);
			getPdfFieldsMap().get(PE179_PARTIALLY_TIME_MONTHLY).setValue(null);
			getPdfFieldsMap().get(PE179_PARTIALLY_TIME_YEARLY).setValue(null);
			getPdfFieldsMap().get(PE179_COMPARABLE_FULL_TIME).setValue(null);
			getPdfFieldsMap().get(PE179_AGREEMENT_COLLECTIVE_FULL_TIME).setValue(null);
			getPdfFieldsMap().get(PE179_LEGAL_MAX).setValue(null);
			getPdfFieldsMap().get(PE179_JOB_TIME_DISTRIBUTION1).setValue(null);
			getPdfFieldsMap().get(PE179_JOB_TIME_DISTRIBUTION2).setValue(null);
			getPdfFieldsMap().get(PE179_ACTIVITY).setValue(null);
			getPdfFieldsMap().get(PE179_ACTIVITY_CYCLIC).setValue(null);
			getPdfFieldsMap().get(PE179_ACTIVITY_CYCLIC_DURATION).setValue(null);
			getPdfFieldsMap().get(PE176_TOTAL_DURATION).setValue(null);
			getPdfFieldsMap().get(PE176_START_DATE).setValue(null);
			getPdfFieldsMap().get(PE176_END_DATE).setValue(null);
			getPdfFieldsMap().get(PE179_TRIAL_DURATION).setValue(null);
			getPdfFieldsMap().get(PE179_SALARY_AMOUNT).setValue(null);
			getPdfFieldsMap().get(PE179_SALARY_PERIOD1).setValue(null);
			getPdfFieldsMap().get(PE179_SALARY_PERIOD).setValue(null);
			getPdfFieldsMap().get(PE179_SALARY_CONCEPT).setValue(null);
			getPdfFieldsMap().get(PE179_SALARY_CONCEPT1).setValue(null);
			getPdfFieldsMap().get(PE179_HOLIDAYS).setValue(null);
			getPdfFieldsMap().get(PE179_REPLACED_EMPLOYEE_NAME).setValue(null);
			getPdfFieldsMap().get(PE179_REPLACED_EMPLOYEE_NIF).setValue(null);
			getPdfFieldsMap().get(PE179_SEPE_MUNICIPALITY).setValue(null);
			getPdfFieldsMap().get(PE179_REPLACEMENT_DATE).setValue(null);
			getPdfFieldsMap().get(PE179_REPLACED_CONTRACT_ID).setValue(null);
			getPdfFieldsMap().get(PE179_OCCUPATION).setValue(null);
			getPdfFieldsMap().get(PE179_AGREEMENT_COLLECTIVE).setValue(null);
			getPdfFieldsMap().get(PE179_AGREEMENT_COLLECTIVE2).setValue(null);
			getPdfFieldsMap().get(PE179_ADDITIONAL_CLAUSES).setValue(null);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ManagerBeanException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
	
	