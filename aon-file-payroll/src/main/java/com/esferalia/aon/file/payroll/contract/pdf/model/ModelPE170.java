package com.esferalia.aon.file.payroll.contract.pdf.model;

import java.io.IOException;

import com.code.aon.common.ManagerBeanException;
import com.esferalia.aon.file.payroll.contract.pdf.UnsupportedContractDocumentException;
import com.esferalia.aon.file.payroll.contrata.IContrataParams;
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.payroll.enumeration.ContractCode;
import com.lowagie.text.pdf.PdfReader;



public class ModelPE170 extends AbstractContractModel {
	
	/*
	 * Contract page 1
	 */
	final static String PE170_TC2_100 = "tipocontrato_100";
	final static String PE170_TC2_200 = "tipocontrato_200";
	final static String PE170_LEGAL_REPRESENTATIVE_NAME = "nomreptra";
	final static String PE170_LEGAL_REPRESENTATIVE_NIF = "dnireptra";
	final static String PE170_LEGAL_REPRESENTATIVE_CHARGE = "calireptra";
	final static String PE170_PROFESSION = "profetraba";
	final static String PE170_CATEGORY = "catetraba";
	final static String PE170_WORKPLACE_FULL_ADDRESS = "ubicact";
	final static String PE170_WORKPLACE_FULL_ADDRESS_MORE = "calletrab";
	final static String PE170_FULL_TIME = "sel_jorn1";
	final static String PE170_FULL_TIME_WEEK_HOURS = "horasjorna1";
	final static String PE170_FULL_TIME_FROM_TIME = "horainicio";
	final static String PE170_FULL_TIME_TO_TIME = "horafin";
	final static String PE170_PARTIALLY_TIME = "sel_jorn2";
	final static String PE170_PARTIALLY_TIME_HOURS = "horasjorna2";
	final static String PE170_PARTIALLY_TIME_DAYLY = "tipojorntp1";
	final static String PE170_PARTIALLY_TIME_WEEKLY = "tipojorntp2";
	final static String PE170_PARTIALLY_TIME_MONTHLY = "tipojorntp3";
	final static String PE170_PARTIALLY_TIME_YEARLY = "tipojorntp4";
	final static String PE170_COMPARABLE_FULL_TIME = "sel_jorn21";
	final static String PE170_AGREEMENT_COLLECTIVE_FULL_TIME = "sel_jorn22";
	final static String PE170_LEGAL_MAX = "sel_jorn23";
	final static String PE170_LEGAL_MAX_HOURS = "horas4";

	/*
	 * Contract page 2
	 */
	final static String PE170_JOB_TIME_DISTRIBUTION1 = "horatraba1";
	final static String PE170_JOB_TIME_DISTRIBUTION2 = "horatraba2";
	final static String PE170_PARTIALLY_TIME_DISCOTINUOUS_YES = "sel_adj1";
	final static String PE170_PARTIALLY_TIME_DISCOTINUOUS_NO = "sel_adj2";
	final static String PE170_PARTIALLY_TIME_COMPLEMENTARY_HOURS_YES = "sel_adjhhcc1";
	final static String PE170_PARTIALLY_TIME_COMPLEMENTARY_HOURS_NO = "sel_adjhhcc2";
	final static String PE170_START_DATE = "fechaini";
	final static String PE170_TRIAL_DURATION = "peridoprue";
	final static String PE170_RELIEF_CONTRACT_YES = "sel_ctorel1";
	final static String PE170_RELIEF_CONTRACT_NO = "sel_ctorel2";
	final static String PE170_SALARY_AMOUNT = "retribu";
	final static String PE170_SALARY_PERIOD = "perioretri";
	final static String PE170_SALARY_CONCEPT = "concepsala";
	final static String PE170_HOLIDAYS = "vacaciones";
	final static String PE170_AGREEMENT_COLLECTIVE = "convcole";
	final static String PE170_SEPE_MUNICIPALITY = "oecomu";
	final static String PE170_ADDITIONAL_CLAUSES = "clausadici";
	
	
	public final static String MODEL_NAME = "PE170";
	
	
	public ModelPE170(){
		super.documentName = MODEL_NAME;
	}
	
	@Override
	public void loadPdfFields(ContractCode code, Contract contract, IContrataParams contrataParams) throws UnsupportedContractDocumentException{
		// TODO 
		
		try {
			PdfReader reader = new PdfReader(getContractModelUrl(documentName+".pdf"));
			
			readPdfFields(reader);
			
			if(code == ContractCode.C100){
				getPdfFieldsMap().get(PE170_TC2_100).setValue("true");
			} else if(code == ContractCode.C200){
				getPdfFieldsMap().get(PE170_TC2_200).setValue("true");
			} else {
				throw new UnsupportedContractDocumentException("El modelo de contrato seleccionado es incorrecto");
			}
			
			super.loadPdfCommonFields(contract, contrataParams);
			
			/*
			 * Contract page 1
			 */
			getPdfFieldsMap().get(PE170_LEGAL_REPRESENTATIVE_NAME).setValue(null);
			getPdfFieldsMap().get(PE170_LEGAL_REPRESENTATIVE_NIF).setValue(null);
			getPdfFieldsMap().get(PE170_LEGAL_REPRESENTATIVE_CHARGE).setValue(null);
			getPdfFieldsMap().get(PE170_PROFESSION).setValue(null);
			getPdfFieldsMap().get(PE170_CATEGORY).setValue(null);
			getPdfFieldsMap().get(PE170_WORKPLACE_FULL_ADDRESS).setValue(null);
			getPdfFieldsMap().get(PE170_WORKPLACE_FULL_ADDRESS_MORE).setValue(null);
			getPdfFieldsMap().get(PE170_FULL_TIME).setValue(null);
			getPdfFieldsMap().get(PE170_FULL_TIME_WEEK_HOURS).setValue(null);
			getPdfFieldsMap().get(PE170_FULL_TIME_FROM_TIME).setValue(null);
			getPdfFieldsMap().get(PE170_FULL_TIME_TO_TIME).setValue(null);
			getPdfFieldsMap().get(PE170_PARTIALLY_TIME).setValue(null);
			getPdfFieldsMap().get(PE170_PARTIALLY_TIME_HOURS).setValue(null);
			getPdfFieldsMap().get(PE170_PARTIALLY_TIME_DAYLY).setValue(null);
			getPdfFieldsMap().get(PE170_PARTIALLY_TIME_WEEKLY).setValue(null);
			getPdfFieldsMap().get(PE170_PARTIALLY_TIME_MONTHLY).setValue(null);
			getPdfFieldsMap().get(PE170_PARTIALLY_TIME_YEARLY).setValue(null);
			getPdfFieldsMap().get(PE170_COMPARABLE_FULL_TIME).setValue(null);
			getPdfFieldsMap().get(PE170_AGREEMENT_COLLECTIVE_FULL_TIME).setValue(null);
			getPdfFieldsMap().get(PE170_LEGAL_MAX).setValue(null);
			getPdfFieldsMap().get(PE170_LEGAL_MAX_HOURS).setValue(null);

			/*
			 * Contract page 2
			 */
			getPdfFieldsMap().get(PE170_JOB_TIME_DISTRIBUTION1).setValue(null);
			getPdfFieldsMap().get(PE170_JOB_TIME_DISTRIBUTION2).setValue(null);
			getPdfFieldsMap().get(PE170_PARTIALLY_TIME_DISCOTINUOUS_YES).setValue(null);
			getPdfFieldsMap().get(PE170_PARTIALLY_TIME_DISCOTINUOUS_NO).setValue(null);
			getPdfFieldsMap().get(PE170_PARTIALLY_TIME_COMPLEMENTARY_HOURS_YES).setValue(null);
			getPdfFieldsMap().get(PE170_PARTIALLY_TIME_COMPLEMENTARY_HOURS_NO).setValue(null);
			getPdfFieldsMap().get(PE170_START_DATE).setValue(null);
			getPdfFieldsMap().get(PE170_TRIAL_DURATION).setValue(null);
			getPdfFieldsMap().get(PE170_RELIEF_CONTRACT_YES).setValue(null);
			getPdfFieldsMap().get(PE170_RELIEF_CONTRACT_NO).setValue(null);
			getPdfFieldsMap().get(PE170_SALARY_AMOUNT).setValue(null);
			getPdfFieldsMap().get(PE170_SALARY_PERIOD).setValue(null);
			getPdfFieldsMap().get(PE170_SALARY_CONCEPT).setValue(null);
			getPdfFieldsMap().get(PE170_HOLIDAYS).setValue(null);
			getPdfFieldsMap().get(PE170_AGREEMENT_COLLECTIVE).setValue(null);
			getPdfFieldsMap().get(PE170_SEPE_MUNICIPALITY).setValue(null);
			getPdfFieldsMap().get(PE170_ADDITIONAL_CLAUSES).setValue(null);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ManagerBeanException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
	
	