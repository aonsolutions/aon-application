package com.esferalia.aon.file.payroll.contract.pdf.model;

import java.io.IOException;

import com.code.aon.common.ManagerBeanException;
import com.esferalia.aon.file.payroll.contract.pdf.UnsupportedContractDocumentException;
import com.esferalia.aon.file.payroll.contrata.IContrataParams;
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.payroll.enumeration.ContractCode;
import com.lowagie.text.pdf.PdfReader;



public class ModelPE176 extends AbstractContractModel {
	
	/*
	 * Contract page 1
	 */
	final static String PE176_TC2_420 = "tiempocompleto";
	final static String PE176_TC2_520 = "tiempoparcial";
	final static String PE176_LEGAL_REPRESENTATIVE_NAME = "nomreptra";
	final static String PE176_LEGAL_REPRESENTATIVE_NIF = "dnireptra";
	final static String PE176_LEGAL_REPRESENTATIVE_CHARGE = "calireptra";
	final static String PE176_PROFFESION_TITLE = "titulprac";
	final static String PE176_PROFFESION_TITLE_DATE = "fechatit";
	final static String PE176_HANDICAP_PERSON1 = "ubicact";
	final static String PE176_HANDICAP_PERSON2 = "Texto2";
	final static String PE176_RDL_63_2006_RESEARCH_FORMATION = "tiempoc";
	final static String PE176_PROFESSION = "profetraba";
	final static String PE176_CATEGORY = "catetraba";
	final static String PE176_WORKPLACE_FULL_ADDRESS = "ubitrab";
	final static String PE176_WORKPLACE_FULL_ADDRESS_MORE = "calletrab";
	
	/*
	 * Contract page 2
	 */
	final static String PE176_FULL_TIME = "sel_jorn1";
	final static String PE176_FULL_TIME_WEEK_HOURS = "horasjorna1";
	final static String PE176_FULL_TIME_FROM_TIME = "horainicio";
	final static String PE176_FULL_TIME_TO_TIME = "horafin";
	final static String PE176_PARTIALLY_TIME = "sel_jorn2";
	final static String PE176_PARTIALLY_TIME_HOURS = "horasjorna2";
	final static String PE176_PARTIALLY_TIME_DAYLY = "tipojorntp1";
	final static String PE176_PARTIALLY_TIME_WEEKLY = "tipojorntp2";
	final static String PE176_PARTIALLY_TIME_MONTHLY = "tipojorntp3";
	final static String PE176_PARTIALLY_TIME_YEARLY = "tipojorntp4";
	final static String PE176_COMPARABLE_FULL_TIME = "Casilla de verificación161";
	final static String PE176_AGREEMENT_COLLECTIVE_FULL_TIME = "Casilla de verificación171";
	final static String PE176_LEGAL_MAX = "Casilla de verificación181";
	final static String PE176_JOB_TIME_DISTRIBUTION1 = "horatraba1";
	final static String PE176_JOB_TIME_DISTRIBUTION2 = "horatraba2";
	final static String PE176_TOTAL_DURATION = "totaldura";
	final static String PE176_START_DATE = "fechaini";
	final static String PE176_END_DATE = "fechafin";
	final static String PE176_TRIAL_DURATION = "peridoprue";
	final static String PE176_SALARY_AMOUNT = "retribu";
	final static String PE176_SALARY_PERIOD = "perioretri";
	final static String PE176_SALARY_CONCEPT = "concepsala";
	final static String PE176_HOLIDAYS = "vacaciones";
	final static String PE176_AGREEMENT_COLLECTIVE = "convcole";
	final static String PE176_SEPE_MUNICIPALITY = "oecomu";
	final static String PE176_ADDITIONAL_CLAUSES = "clausadici";
	
	public final static String MODEL_NAME = "PE176";
	
	public ModelPE176(){
		super.documentName = MODEL_NAME;
	}
	
	@Override
	public void loadPdfFields(ContractCode code, Contract contract, IContrataParams contrataParams) throws UnsupportedContractDocumentException{
		// TODO
		try {
			PdfReader reader = new PdfReader(getContractModelUrl(documentName+".pdf"));
			
			readPdfFields(reader);
			
			if(code == ContractCode.C420){
				getPdfFieldsMap().get(PE176_TC2_420).setValue("true");
			} else if(code == ContractCode.C520){
				getPdfFieldsMap().get(PE176_TC2_520).setValue("true");
			} else {
				throw new UnsupportedContractDocumentException("El modelo de contrato seleccionado es incorrecto");
			}
			
			super.loadPdfCommonFields(contract, contrataParams);
			
			/*
			 * Contract page 1
			 */
			getPdfFieldsMap().get( PE176_LEGAL_REPRESENTATIVE_NAME).setValue(null);
			getPdfFieldsMap().get( PE176_LEGAL_REPRESENTATIVE_NIF).setValue(null);
			getPdfFieldsMap().get( PE176_LEGAL_REPRESENTATIVE_CHARGE).setValue(null);
			getPdfFieldsMap().get( PE176_PROFFESION_TITLE).setValue(null);
			getPdfFieldsMap().get( PE176_PROFFESION_TITLE_DATE).setValue(null);
			getPdfFieldsMap().get( PE176_HANDICAP_PERSON1).setValue(null);
			getPdfFieldsMap().get( PE176_HANDICAP_PERSON2).setValue(null);
			getPdfFieldsMap().get( PE176_RDL_63_2006_RESEARCH_FORMATION).setValue(null);
			getPdfFieldsMap().get( PE176_PROFESSION).setValue(null);
			getPdfFieldsMap().get( PE176_CATEGORY).setValue(null);
			getPdfFieldsMap().get( PE176_WORKPLACE_FULL_ADDRESS).setValue(null);
			getPdfFieldsMap().get( PE176_WORKPLACE_FULL_ADDRESS_MORE).setValue(null);
			
			/*
			 * Contract page 2
			 */
			getPdfFieldsMap().get( PE176_FULL_TIME).setValue(null);
			getPdfFieldsMap().get( PE176_FULL_TIME_WEEK_HOURS).setValue(null);
			getPdfFieldsMap().get( PE176_FULL_TIME_FROM_TIME).setValue(null);
			getPdfFieldsMap().get( PE176_FULL_TIME_TO_TIME).setValue(null);
			getPdfFieldsMap().get( PE176_PARTIALLY_TIME).setValue(null);
			getPdfFieldsMap().get( PE176_PARTIALLY_TIME_HOURS).setValue(null);
			getPdfFieldsMap().get( PE176_PARTIALLY_TIME_DAYLY).setValue(null);
			getPdfFieldsMap().get( PE176_PARTIALLY_TIME_WEEKLY).setValue(null);
			getPdfFieldsMap().get( PE176_PARTIALLY_TIME_MONTHLY).setValue(null);
			getPdfFieldsMap().get( PE176_PARTIALLY_TIME_YEARLY).setValue(null);
			getPdfFieldsMap().get( PE176_COMPARABLE_FULL_TIME).setValue(null);
			getPdfFieldsMap().get( PE176_AGREEMENT_COLLECTIVE_FULL_TIME).setValue(null);
			getPdfFieldsMap().get( PE176_LEGAL_MAX).setValue(null);
			getPdfFieldsMap().get( PE176_JOB_TIME_DISTRIBUTION1).setValue(null);
			getPdfFieldsMap().get( PE176_JOB_TIME_DISTRIBUTION2).setValue(null);
			getPdfFieldsMap().get( PE176_TOTAL_DURATION).setValue(null);
			getPdfFieldsMap().get( PE176_START_DATE).setValue(null);
			getPdfFieldsMap().get( PE176_END_DATE).setValue(null);
			getPdfFieldsMap().get( PE176_TRIAL_DURATION).setValue(null);
			getPdfFieldsMap().get( PE176_SALARY_AMOUNT).setValue(null);
			getPdfFieldsMap().get( PE176_SALARY_PERIOD).setValue(null);
			getPdfFieldsMap().get( PE176_SALARY_CONCEPT).setValue(null);
			getPdfFieldsMap().get( PE176_HOLIDAYS).setValue(null);
			getPdfFieldsMap().get( PE176_AGREEMENT_COLLECTIVE).setValue(null);
			getPdfFieldsMap().get( PE176_SEPE_MUNICIPALITY).setValue(null);
			getPdfFieldsMap().get( PE176_ADDITIONAL_CLAUSES).setValue(null);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ManagerBeanException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
}
	
	