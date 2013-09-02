package com.esferalia.aon.file.payroll.contract.pdf.model;

import java.io.IOException;

import com.code.aon.common.ManagerBeanException;
import com.esferalia.aon.file.payroll.contract.pdf.UnsupportedContractDocumentException;
import com.esferalia.aon.file.payroll.contrata.IContrataParams;
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.payroll.enumeration.ContractCode;
import com.lowagie.text.pdf.PdfReader;



public class ModelPE177 extends AbstractContractModel {
	
	/*
	 * Contract page 1
	 */
	final static String PE177_FULL_TIME = "sel_jorn1";
	final static String PE177_PARTIAL_TIME = "sel_jorn2";
	final static String PE177_TC2_401 = "tipocontrato_401";
	final static String PE177_TC2_402 = "tipocontrato_402";
	final static String PE177_TC2_410 = "tipocontrato_410";
	final static String PE177_TC2_501 = "tipocontrato_501";
	final static String PE177_TC2_502 = "tipocontrato_502";
	final static String PE177_TC2_510 = "tipocontrato_510";
	final static String PE177_TC2_540 = "tipocontrato_540";
	final static String PE177_LEGAL_REPRESENTATIVE_NAME = "nomreptra";
	final static String PE177_LEGAL_REPRESENTATIVE_NIF = "dnireptra";
	final static String PE177_LEGAL_REPRESENTATIVE_CHARGE = "calireptra";
	final static String PE177_PROFESSION = "profetraba";
	final static String PE177_CATEGORY = "catetraba";
	final static String PE177_WORKPLACE_FULL_ADDRESS_MORE = "calletrab";
	final static String PE177_FULL_TIME_WEEK_HOURS = "horasjorna1";
	final static String PE177_FULL_TIME_FROM_TIME = "horainicio";
	final static String PE177_FULL_TIME_TO_TIME = "horafin";
	final static String PE177_PARTIALLY_TIME_HOURS = "horasjorna2";
	final static String PE177_PARTIALLY_TIME_DAYLY = "tipojorntp1";
	final static String PE177_PARTIALLY_TIME_WEEKLY = "tipojorntp2";
	final static String PE177_PARTIALLY_TIME_MONTHLY = "tipojorntp3";
	final static String PE177_PARTIALLY_TIME_YEARLY = "tipojorntp4";
	final static String PE177_COMPARABLE_FULL_TIME = "sel_jorn21";
	final static String PE177_AGREEMENT_COLLECTIVE_FULL_TIME = "sel_jorn22";
	final static String PE177_LEGAL_MAX = "sel_jorn23";
	final static String PE177_JOB_TIME_DISTRIBUTION1 = "horatraba1";
	final static String PE177_JOB_TIME_DISTRIBUTION2 = "horatraba2";
	final static String PE177_START_DATE = "fechaini";
	final static String PE177_END_DATE = "fechafin";
	final static String PE177_TRIAL_DURATION = "peridoprue";
	final static String PE177_GRATER_DURATION_AGREEMENT_COLLECTIVE = "in_ccolec";
	
	/*
	 * Contract page 2
	 */
	final static String PE177_SALARY_AMOUNT = "retribu";
	final static String PE177_SALARY_PERIOD = "perioretri";
	final static String PE177_SALARY_CONCEPT = "concepsala";
	final static String PE177_HOLIDAYS = "vacaciones";
	final static String PE177_CAUSE1 = "in_causa1";
	final static String PE177_CAUSE1_DESCRIPTION = "causaobra";
	final static String PE177_CAUSE2 = "in_causa2";
	final static String PE177_CAUSE2_DESCRIPTION1 = "causaobra1";
	final static String PE177_CAUSE2_DESCRIPTION2 = "causaobra2";
	final static String PE177_CAUSE3 = "in_causa3";
	final static String PE177_CAUSE3_REPLACED_EMPLOYEE_NAME = "nomtrasus";
	final static String PE177_CAUSE3_1 = "in_causa31";
	final static String PE177_CAUSE3_2 = "in_causa32";
	final static String PE177_CAUSE3_3 = "in_causa33";
	final static String PE177_CAUSE3_4 = "in_causa34";
	final static String PE177_CAUSE3_5 = "in_causa35";
	final static String PE177_CAUSE3_6 = "in_causa36";
	final static String PE177_CAUSE3_7 = "violengen";
	final static String PE177_CAUSE3_7_A = "violengenA";
	final static String PE177_CAUSE3_7_B = "violengenB";
	final static String PE177_CAUSE3_OCCUPATION = "puestotra";
	final static String PE177_CAUSE4 = "reducjorn";
	final static String PE177_CAUSE4_SALARY_REDUCTION = "reducsalario";
	final static String PE177_AGREEMENT_COLLECTIVE = "convcole";
	final static String PE177_SEPE_MUNICIPALITY = "oecomu";
	final static String PE177_ADDITIONAL_CLAUSES = "clausadici";
	
	
	public final static String MODEL_NAME = "PE177";
	
	public ModelPE177(){
		super.documentName = MODEL_NAME;
	}
	
	@Override
	public void loadPdfFields(ContractCode code, Contract contract, IContrataParams contrataParams) throws UnsupportedContractDocumentException{
		// TODO
		try {
			PdfReader reader = new PdfReader(getContractModelUrl(documentName+".pdf"));
			
			readPdfFields(reader);
			
			if(code == ContractCode.C401){
				getPdfFieldsMap().get(PE177_FULL_TIME).setValue("true");
				getPdfFieldsMap().get(PE177_TC2_401).setValue("true");
			} else if(code == ContractCode.C402){
				getPdfFieldsMap().get(PE177_FULL_TIME).setValue("true");
				getPdfFieldsMap().get(PE177_TC2_402).setValue("true");
			} else if(code == ContractCode.C410){
				getPdfFieldsMap().get(PE177_FULL_TIME).setValue("true");
				getPdfFieldsMap().get(PE177_TC2_410).setValue("true");
			} else if(code == ContractCode.C501){
				getPdfFieldsMap().get(PE177_PARTIAL_TIME).setValue("true");
				getPdfFieldsMap().get(PE177_TC2_501).setValue("true");
			} else if(code == ContractCode.C502){
				getPdfFieldsMap().get(PE177_PARTIAL_TIME).setValue("true");
				getPdfFieldsMap().get(PE177_TC2_502).setValue("true");
			} else if(code == ContractCode.C510){
				getPdfFieldsMap().get(PE177_PARTIAL_TIME).setValue("true");
				getPdfFieldsMap().get(PE177_TC2_510).setValue("true");
			} else if(code == ContractCode.C540){
				getPdfFieldsMap().get(PE177_PARTIAL_TIME).setValue("true");
				getPdfFieldsMap().get(PE177_TC2_540).setValue("true");
			} else {
				throw new UnsupportedContractDocumentException("El modelo de contrato seleccionado es incorrecto");
			}
			
			super.loadPdfCommonFields(contract, contrataParams);
			
			
			
			/*
			 * Contract page 1
			 */
			getPdfFieldsMap().get(PE177_LEGAL_REPRESENTATIVE_NAME).setValue(null);
			getPdfFieldsMap().get(PE177_LEGAL_REPRESENTATIVE_NIF).setValue(null);
			getPdfFieldsMap().get(PE177_LEGAL_REPRESENTATIVE_CHARGE).setValue(null);
			getPdfFieldsMap().get(PE177_PROFESSION).setValue(null);
			getPdfFieldsMap().get(PE177_CATEGORY).setValue(null);
			getPdfFieldsMap().get(PE177_WORKPLACE_FULL_ADDRESS_MORE).setValue(null);
			getPdfFieldsMap().get(PE177_FULL_TIME_WEEK_HOURS).setValue(null);
			getPdfFieldsMap().get(PE177_FULL_TIME_FROM_TIME).setValue(null);
			getPdfFieldsMap().get(PE177_FULL_TIME_TO_TIME).setValue(null);
			getPdfFieldsMap().get(PE177_PARTIALLY_TIME_HOURS).setValue(null);
			getPdfFieldsMap().get(PE177_PARTIALLY_TIME_DAYLY).setValue(null);
			getPdfFieldsMap().get(PE177_PARTIALLY_TIME_WEEKLY).setValue(null);
			getPdfFieldsMap().get(PE177_PARTIALLY_TIME_MONTHLY).setValue(null);
			getPdfFieldsMap().get(PE177_PARTIALLY_TIME_YEARLY).setValue(null);
			getPdfFieldsMap().get(PE177_COMPARABLE_FULL_TIME).setValue(null);
			getPdfFieldsMap().get(PE177_AGREEMENT_COLLECTIVE_FULL_TIME).setValue(null);
			getPdfFieldsMap().get(PE177_LEGAL_MAX).setValue(null);
			getPdfFieldsMap().get(PE177_JOB_TIME_DISTRIBUTION1).setValue(null);
			getPdfFieldsMap().get(PE177_JOB_TIME_DISTRIBUTION2).setValue(null);
			getPdfFieldsMap().get(PE177_START_DATE).setValue(null);
			getPdfFieldsMap().get(PE177_END_DATE).setValue(null);
			getPdfFieldsMap().get(PE177_TRIAL_DURATION).setValue(null);
			getPdfFieldsMap().get(PE177_GRATER_DURATION_AGREEMENT_COLLECTIVE).setValue(null);
			
			/*
			 * Contract page 2
			 */
			getPdfFieldsMap().get(PE177_SALARY_AMOUNT).setValue(null);
			getPdfFieldsMap().get(PE177_SALARY_PERIOD).setValue(null);
			getPdfFieldsMap().get(PE177_SALARY_CONCEPT).setValue(null);
			getPdfFieldsMap().get(PE177_HOLIDAYS).setValue(null);
			getPdfFieldsMap().get(PE177_CAUSE1).setValue(null);
			getPdfFieldsMap().get(PE177_CAUSE1_DESCRIPTION).setValue(null);
			getPdfFieldsMap().get(PE177_CAUSE2).setValue(null);
			getPdfFieldsMap().get(PE177_CAUSE2_DESCRIPTION1).setValue(null);
			getPdfFieldsMap().get(PE177_CAUSE2_DESCRIPTION2).setValue(null);
			getPdfFieldsMap().get(PE177_CAUSE3).setValue(null);
			getPdfFieldsMap().get(PE177_CAUSE3_REPLACED_EMPLOYEE_NAME).setValue(null);
			getPdfFieldsMap().get(PE177_CAUSE3_1).setValue(null);
			getPdfFieldsMap().get(PE177_CAUSE3_2).setValue(null);
			getPdfFieldsMap().get(PE177_CAUSE3_3).setValue(null);
			getPdfFieldsMap().get(PE177_CAUSE3_4).setValue(null);
			getPdfFieldsMap().get(PE177_CAUSE3_5).setValue(null);
			getPdfFieldsMap().get(PE177_CAUSE3_6).setValue(null);
			getPdfFieldsMap().get(PE177_CAUSE3_7).setValue(null);
			getPdfFieldsMap().get(PE177_CAUSE3_7_A).setValue(null);
			getPdfFieldsMap().get(PE177_CAUSE3_7_B).setValue(null);
			getPdfFieldsMap().get(PE177_CAUSE3_OCCUPATION).setValue(null);
			getPdfFieldsMap().get(PE177_CAUSE4).setValue(null);
			getPdfFieldsMap().get(PE177_CAUSE4_SALARY_REDUCTION).setValue(null);
			getPdfFieldsMap().get(PE177_AGREEMENT_COLLECTIVE).setValue(null);
			getPdfFieldsMap().get(PE177_SEPE_MUNICIPALITY).setValue(null);
			getPdfFieldsMap().get(PE177_ADDITIONAL_CLAUSES).setValue(null);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ManagerBeanException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
	
	