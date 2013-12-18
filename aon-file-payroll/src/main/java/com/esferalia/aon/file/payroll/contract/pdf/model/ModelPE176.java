package com.esferalia.aon.file.payroll.contract.pdf.model;

import java.io.IOException;

import com.code.aon.common.ManagerBeanException;
import com.esferalia.aon.file.payroll.contract.pdf.UnsupportedContractDocumentException;
import com.esferalia.aon.file.payroll.contrata.IContrataParams;
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.payroll.enumeration.ContractCode;
import com.lowagie.text.pdf.PdfReader;



public class ModelPE176 extends AbstractContractModel {
	
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
				getPdfFieldsMap().get(PE176FieldName.TC2_420.getValue()).setValue("true");
			} else if(code == ContractCode.C520){
				getPdfFieldsMap().get(PE176FieldName.TC2_520.getValue()).setValue("true");
			} else {
				throw new UnsupportedContractDocumentException("El modelo de contrato seleccionado es incorrecto");
			}
			
			super.loadPdfCommonFields(contract, contrataParams);
			
			/*
			 * Contract page 1
			 */
			getPdfFieldsMap().get( PE176FieldName.LEGAL_REPRESENTATIVE_NAME.getValue()).setValue(null);
			getPdfFieldsMap().get( PE176FieldName.LEGAL_REPRESENTATIVE_NIF.getValue()).setValue(null);
			getPdfFieldsMap().get( PE176FieldName.LEGAL_REPRESENTATIVE_CHARGE.getValue()).setValue(null);
			getPdfFieldsMap().get( PE176FieldName.PROFFESION_TITLE.getValue()).setValue(null);
			getPdfFieldsMap().get( PE176FieldName.PROFFESION_TITLE_DATE.getValue()).setValue(null);
			getPdfFieldsMap().get( PE176FieldName.HANDICAP_PERSON1.getValue()).setValue(null);
			getPdfFieldsMap().get( PE176FieldName.HANDICAP_PERSON2.getValue()).setValue(null);
			getPdfFieldsMap().get( PE176FieldName.RDL_63_2006_RESEARCH_FORMATION.getValue()).setValue(null);
			getPdfFieldsMap().get( PE176FieldName.PROFESSION.getValue()).setValue(null);
			getPdfFieldsMap().get( PE176FieldName.CATEGORY.getValue()).setValue(null);
			getPdfFieldsMap().get( PE176FieldName.WORKPLACE_FULL_ADDRESS.getValue()).setValue(null);
			getPdfFieldsMap().get( PE176FieldName.WORKPLACE_FULL_ADDRESS_MORE.getValue()).setValue(null);
			
			/*
			 * Contract page 2
			 */
			getPdfFieldsMap().get( PE176FieldName.FULL_TIME.getValue()).setValue(null);
			getPdfFieldsMap().get( PE176FieldName.FULL_TIME_WEEK_HOURS.getValue()).setValue(null);
			getPdfFieldsMap().get( PE176FieldName.FULL_TIME_FROM_TIME.getValue()).setValue(null);
			getPdfFieldsMap().get( PE176FieldName.FULL_TIME_TO_TIME.getValue()).setValue(null);
			getPdfFieldsMap().get( PE176FieldName.PARTIALLY_TIME.getValue()).setValue(null);
			getPdfFieldsMap().get( PE176FieldName.PARTIALLY_TIME_HOURS.getValue()).setValue(null);
			getPdfFieldsMap().get( PE176FieldName.PARTIALLY_TIME_DAYLY.getValue()).setValue(null);
			getPdfFieldsMap().get( PE176FieldName.PARTIALLY_TIME_WEEKLY.getValue()).setValue(null);
			getPdfFieldsMap().get( PE176FieldName.PARTIALLY_TIME_MONTHLY.getValue()).setValue(null);
			getPdfFieldsMap().get( PE176FieldName.PARTIALLY_TIME_YEARLY.getValue()).setValue(null);
			getPdfFieldsMap().get( PE176FieldName.COMPARABLE_FULL_TIME.getValue()).setValue(null);
			getPdfFieldsMap().get( PE176FieldName.AGREEMENT_COLLECTIVE_FULL_TIME.getValue()).setValue(null);
			getPdfFieldsMap().get( PE176FieldName.LEGAL_MAX.getValue()).setValue(null);
			getPdfFieldsMap().get( PE176FieldName.JOB_TIME_DISTRIBUTION1.getValue()).setValue(null);
			getPdfFieldsMap().get( PE176FieldName.JOB_TIME_DISTRIBUTION2.getValue()).setValue(null);
			getPdfFieldsMap().get( PE176FieldName.TOTAL_DURATION.getValue()).setValue(null);
			getPdfFieldsMap().get( PE176FieldName.START_DATE.getValue()).setValue(null);
			getPdfFieldsMap().get( PE176FieldName.END_DATE.getValue()).setValue(null);
			getPdfFieldsMap().get( PE176FieldName.TRIAL_DURATION.getValue()).setValue(null);
			getPdfFieldsMap().get( PE176FieldName.SALARY_AMOUNT.getValue()).setValue(null);
			getPdfFieldsMap().get( PE176FieldName.SALARY_PERIOD.getValue()).setValue(null);
			getPdfFieldsMap().get( PE176FieldName.SALARY_CONCEPT.getValue()).setValue(null);
			getPdfFieldsMap().get( PE176FieldName.HOLIDAYS.getValue()).setValue(null);
			getPdfFieldsMap().get( PE176FieldName.AGREEMENT_COLLECTIVE.getValue()).setValue(null);
			getPdfFieldsMap().get( PE176FieldName.SEPE_MUNICIPALITY.getValue()).setValue(null);
			getPdfFieldsMap().get( PE176FieldName.ADDITIONAL_CLAUSES.getValue()).setValue(null);
			
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
	public enum PE176FieldName implements IContractFieldName{
		/*
		 * Contract page 1
		 */
		TC2_420("tiempocompleto",Boolean.FALSE),
		TC2_520("tiempoparcial",Boolean.FALSE),
		LEGAL_REPRESENTATIVE_NAME("nomreptra",Boolean.FALSE),
		LEGAL_REPRESENTATIVE_NIF("dnireptra",Boolean.FALSE),
		LEGAL_REPRESENTATIVE_CHARGE("calireptra",Boolean.FALSE),
		PROFFESION_TITLE("titulprac",Boolean.FALSE),
		PROFFESION_TITLE_DATE("fechatit",Boolean.FALSE),
		HANDICAP_PERSON1("ubicact",Boolean.FALSE),
		HANDICAP_PERSON2("Texto2",Boolean.FALSE),
		RDL_63_2006_RESEARCH_FORMATION("tiempoc",Boolean.FALSE),
		PROFESSION("profetraba",Boolean.FALSE),
		CATEGORY("catetraba",Boolean.FALSE),
		WORKPLACE_FULL_ADDRESS("ubitrab",Boolean.FALSE),
		WORKPLACE_FULL_ADDRESS_MORE("calletrab",Boolean.FALSE),
		
		/*
		 * Contract page 2
		 */
		FULL_TIME("sel_jorn1",Boolean.FALSE),
		FULL_TIME_WEEK_HOURS("horasjorna1",Boolean.TRUE),
		FULL_TIME_FROM_TIME("horainicio",Boolean.TRUE),
		FULL_TIME_TO_TIME("horafin",Boolean.TRUE),
		PARTIALLY_TIME("sel_jorn2",Boolean.FALSE),
		PARTIALLY_TIME_HOURS("horasjorna2",Boolean.FALSE),
		PARTIALLY_TIME_DAYLY("tipojorntp1",Boolean.FALSE),
		PARTIALLY_TIME_WEEKLY("tipojorntp2",Boolean.FALSE),
		PARTIALLY_TIME_MONTHLY("tipojorntp3",Boolean.FALSE),
		PARTIALLY_TIME_YEARLY("tipojorntp4",Boolean.FALSE),
		COMPARABLE_FULL_TIME("Casilla de verificación161",Boolean.FALSE),
		AGREEMENT_COLLECTIVE_FULL_TIME("Casilla de verificación171",Boolean.FALSE),
		LEGAL_MAX("Casilla de verificación181",Boolean.FALSE),
		JOB_TIME_DISTRIBUTION1("horatraba1",Boolean.FALSE),
		JOB_TIME_DISTRIBUTION2("horatraba2",Boolean.FALSE),
		TOTAL_DURATION("totaldura",Boolean.FALSE),
		START_DATE("fechaini",Boolean.FALSE),
		END_DATE("fechafin",Boolean.FALSE),
		TRIAL_DURATION("peridoprue",Boolean.TRUE),
		SALARY_AMOUNT("retribu",Boolean.TRUE),
		SALARY_PERIOD("perioretri",Boolean.FALSE),
		SALARY_CONCEPT("concepsala",Boolean.TRUE),
		HOLIDAYS("vacaciones",Boolean.FALSE),
		AGREEMENT_COLLECTIVE("convcole",Boolean.FALSE),
		SEPE_MUNICIPALITY("oecomu",Boolean.TRUE),
		ADDITIONAL_CLAUSES("clausadici",Boolean.FALSE),
		;
		
		private String value;
		private boolean overridable;
		
		private PE176FieldName(String value, boolean overridable) {
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
	
	