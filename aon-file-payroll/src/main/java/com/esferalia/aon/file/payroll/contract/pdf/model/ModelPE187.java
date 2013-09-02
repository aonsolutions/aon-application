package com.esferalia.aon.file.payroll.contract.pdf.model;

import java.io.IOException;

import com.code.aon.common.ManagerBeanException;
import com.esferalia.aon.file.payroll.contract.pdf.UnsupportedContractDocumentException;
import com.esferalia.aon.file.payroll.contrata.IContrataParams;
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.payroll.enumeration.ContractCode;
import com.lowagie.text.pdf.PdfReader;



public class ModelPE187 extends AbstractContractModel {
	
	/*
	 * Contract page 1
	 */
	final static String PE187_TC2_430_BONI = "tiempocompletoboni";
	final static String PE187_TC2_530_BONI = "tiempoparcialboni";
	final static String PE187_TC2_430 = "tiempocompleto";
	final static String PE187_TC2_530 = "tiempoparcial";
	final static String PE187_LEGAL_REPRESENTATIVE_NAME = "nomreptra";
	final static String PE187_LEGAL_REPRESENTATIVE_NIF = "dnireptra";
	final static String PE187_LEGAL_REPRESENTATIVE_CHARGE = "calireptra";
	final static String PE187_HANDICAPPED_PERSON = "discapa_si";
	final static String PE187_HANDICAP_CERTIFICATE_ISSUED_BY = "organcerti2";
	final static String PE187_RDL_43_2006_BENEFITS = "acogebene_si";
	
	/*
	 * Contract page 2
	 */
	final static String PE187_PROFESSION = "profetraba";
	final static String PE187_CATEGORY = "catetraba";
	final static String PE187_WORKPLACE_FULL_ADDRESS = "calletrab";
	final static String PE187_WORKPLACE_FULL_ADDRESS_MORE = "calleloca1";
	final static String PE187_FULL_TIME = "sel_tpjorn1";
	final static String PE187_FULL_TIME_WEEK_HOURS = "horasjorna1";
	final static String PE187_FULL_TIME_FROM_TIME = "horainicio";
	final static String PE187_FULL_TIME_TO_TIME = "horafin";	
	final static String PE187_PARTIALLY_TIME = "sel_tpjorn2";
	final static String PE187_PARTIALLY_TIME_HOURS = "horasjorna2";
	final static String PE187_PARTIALLY_TIME_DAYLY = "sel_tpjorn_dia";
	final static String PE187_PARTIALLY_TIME_WEEKLY = "sel_tpjorn_sem";
	final static String PE187_PARTIALLY_TIME_MONTHLY = "sel_tpjorn_mes";
	final static String PE187_PARTIALLY_TIME_YEARLY = "sel_tpjorn_año";
	final static String PE187_COMPARABLE_FULL_TIME = "sel_tpjorn21";
	final static String PE187_AGREEMENT_COLLECTIVE_FULL_TIME = "sel_tpjorn22";
	final static String PE187_LEGAL_MAX = "sel_tpjorn23";
	final static String PE187_JOB_TIME_DISTRIBUTION1 = "horatraba1";
	final static String PE187_JOB_TIME_DISTRIBUTION2 = "horatraba2";
	final static String PE187_TOTAL_DURATION = "totaldura";
	final static String PE187_START_DATE = "fechaini";
	final static String PE187_END_DATE = "fechafin";
	final static String PE187_TRIAL_DURATION = "peridoprue";
	final static String PE187_SALARY_AMOUNT = "retribu";
	final static String PE187_SALARY_PERIOD = "perioretri";
	final static String PE187_SALARY_CONCEPT = "concepsala";
	final static String PE187_HOLIDAYS = "vacaciones";
	final static String PE187_AGREEMENT_COLLECTIVE = "c1";
	final static String PE187_AGREEMENT_COLLECTIVE_MORE = "convcole";
	final static String PE187_SEPE_MUNICIPALITY = "oecomu";
	final static String PE187_SEPE_MUNICIPALITY_MORE = "eo2";
	final static String PE187_ADDITIONAL_CLAUSES = "clausadici";
	
	
	public final static String MODEL_NAME = "PE187";
	
	public ModelPE187(){
		super.documentName = MODEL_NAME;
	}
	
	@Override
	public void loadPdfFields(ContractCode code, Contract contract, IContrataParams contrataParams) throws UnsupportedContractDocumentException{
		// TODO
		try {
			PdfReader reader = new PdfReader(getContractModelUrl(documentName+".pdf"));
			readPdfFields(reader);
			
			boolean bonif = false;
			if(code == ContractCode.C430){
				if(bonif){
					getPdfFieldsMap().get(PE187_TC2_430_BONI).setValue("true");
				} else {
					getPdfFieldsMap().get(PE187_TC2_430).setValue("true");
				}
			} else if(code == ContractCode.C530){
				if(bonif){
					getPdfFieldsMap().get(PE187_TC2_530_BONI).setValue("true");
				} else {
					getPdfFieldsMap().get(PE187_TC2_530).setValue("true");
				}
			} else {
				throw new UnsupportedContractDocumentException("El modelo de contrato seleccionado es incorrecto");
			}
			
			super.loadPdfCommonFields(contract, contrataParams);
			
			/*
			 * Contract page 1
			 */
			getPdfFieldsMap().get(PE187_LEGAL_REPRESENTATIVE_NAME).setValue(null);
			getPdfFieldsMap().get(PE187_LEGAL_REPRESENTATIVE_NIF).setValue(null);
			getPdfFieldsMap().get(PE187_LEGAL_REPRESENTATIVE_CHARGE).setValue(null);
			getPdfFieldsMap().get(PE187_HANDICAPPED_PERSON).setValue(null);
			getPdfFieldsMap().get(PE187_HANDICAP_CERTIFICATE_ISSUED_BY).setValue(null);
			getPdfFieldsMap().get(PE187_RDL_43_2006_BENEFITS).setValue(null);
			
			/*
			 * Contract page 2
			 */
			getPdfFieldsMap().get(PE187_PROFESSION).setValue(null);
			getPdfFieldsMap().get(PE187_CATEGORY).setValue(null);
			getPdfFieldsMap().get(PE187_WORKPLACE_FULL_ADDRESS).setValue(null);
			getPdfFieldsMap().get(PE187_WORKPLACE_FULL_ADDRESS_MORE).setValue(null);
			getPdfFieldsMap().get(PE187_FULL_TIME).setValue(null);
			getPdfFieldsMap().get(PE187_FULL_TIME_WEEK_HOURS).setValue(null);
			getPdfFieldsMap().get(PE187_FULL_TIME_FROM_TIME).setValue(null);
			getPdfFieldsMap().get(PE187_FULL_TIME_TO_TIME).setValue(null);	
			getPdfFieldsMap().get(PE187_PARTIALLY_TIME).setValue(null);
			getPdfFieldsMap().get(PE187_PARTIALLY_TIME_HOURS).setValue(null);
			getPdfFieldsMap().get(PE187_PARTIALLY_TIME_DAYLY).setValue(null);
			getPdfFieldsMap().get(PE187_PARTIALLY_TIME_WEEKLY).setValue(null);
			getPdfFieldsMap().get(PE187_PARTIALLY_TIME_MONTHLY).setValue(null);
			getPdfFieldsMap().get(PE187_PARTIALLY_TIME_YEARLY).setValue(null);
			getPdfFieldsMap().get(PE187_COMPARABLE_FULL_TIME).setValue(null);
			getPdfFieldsMap().get(PE187_AGREEMENT_COLLECTIVE_FULL_TIME).setValue(null);
			getPdfFieldsMap().get(PE187_LEGAL_MAX).setValue(null);
			getPdfFieldsMap().get(PE187_JOB_TIME_DISTRIBUTION1).setValue(null);
			getPdfFieldsMap().get(PE187_JOB_TIME_DISTRIBUTION2).setValue(null);
			getPdfFieldsMap().get(PE187_TOTAL_DURATION).setValue(null);
			getPdfFieldsMap().get(PE187_START_DATE).setValue(null);
			getPdfFieldsMap().get(PE187_END_DATE).setValue(null);
			getPdfFieldsMap().get(PE187_TRIAL_DURATION).setValue(null);
			getPdfFieldsMap().get(PE187_SALARY_AMOUNT).setValue(null);
			getPdfFieldsMap().get(PE187_SALARY_PERIOD).setValue(null);
			getPdfFieldsMap().get(PE187_SALARY_CONCEPT).setValue(null);
			getPdfFieldsMap().get(PE187_HOLIDAYS).setValue(null);
			getPdfFieldsMap().get(PE187_AGREEMENT_COLLECTIVE).setValue(null);
			getPdfFieldsMap().get(PE187_AGREEMENT_COLLECTIVE_MORE).setValue(null);
			getPdfFieldsMap().get(PE187_SEPE_MUNICIPALITY).setValue(null);
			getPdfFieldsMap().get(PE187_SEPE_MUNICIPALITY_MORE).setValue(null);
			getPdfFieldsMap().get(PE187_ADDITIONAL_CLAUSES).setValue(null);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ManagerBeanException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
	
	