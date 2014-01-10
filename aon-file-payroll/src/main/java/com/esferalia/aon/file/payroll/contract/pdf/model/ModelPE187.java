package com.esferalia.aon.file.payroll.contract.pdf.model;

import java.io.IOException;

import com.code.aon.common.ManagerBeanException;
import com.esferalia.aon.file.payroll.contract.pdf.UnsupportedContractDocumentException;
import com.esferalia.aon.file.payroll.contrata.IContrataParams;
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.payroll.enumeration.ContractCode;
import com.lowagie.text.pdf.PdfReader;



public class ModelPE187 extends AbstractContractModel {
	
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
					getPdfFieldsMap().get(PE187FieldName.TC2_430_BONI.getValue()).setValue("true");
				} else {
					getPdfFieldsMap().get(PE187FieldName.TC2_430.getValue()).setValue("true");
				}
			} else if(code == ContractCode.C530){
				if(bonif){
					getPdfFieldsMap().get(PE187FieldName.TC2_530_BONI.getValue()).setValue("true");
				} else {
					getPdfFieldsMap().get(PE187FieldName.TC2_530.getValue()).setValue("true");
				}
			} else {
				throw new UnsupportedContractDocumentException("El modelo de contrato seleccionado es incorrecto");
			}
			
			super.loadPdfCommonFields(contract, contrataParams);
			
			/*
			 * Contract page 1
			 */
			getPdfFieldsMap().get(PE187FieldName.LEGAL_REPRESENTATIVE_NAME.getValue()).setValue(null);
			getPdfFieldsMap().get(PE187FieldName.LEGAL_REPRESENTATIVE_NIF.getValue()).setValue(null);
			getPdfFieldsMap().get(PE187FieldName.LEGAL_REPRESENTATIVE_CHARGE.getValue()).setValue(null);
			getPdfFieldsMap().get(PE187FieldName.HANDICAPPED_PERSON.getValue()).setValue(null);
			getPdfFieldsMap().get(PE187FieldName.HANDICAP_CERTIFICATE_ISSUED_BY.getValue()).setValue(null);
			getPdfFieldsMap().get(PE187FieldName.RDL_43_2006_BENEFITS.getValue()).setValue(null);
			
			/*
			 * Contract page 2
			 */
			getPdfFieldsMap().get(PE187FieldName.PROFESSION.getValue()).setValue(null);
			getPdfFieldsMap().get(PE187FieldName.CATEGORY.getValue()).setValue(null);
			getPdfFieldsMap().get(PE187FieldName.WORKPLACE_FULL_ADDRESS.getValue()).setValue(null);
			getPdfFieldsMap().get(PE187FieldName.WORKPLACE_FULL_ADDRESS_MORE.getValue()).setValue(null);
			getPdfFieldsMap().get(PE187FieldName.FULL_TIME.getValue()).setValue(null);
			getPdfFieldsMap().get(PE187FieldName.FULL_TIME_WEEK_HOURS.getValue()).setValue(null);
			getPdfFieldsMap().get(PE187FieldName.FULL_TIME_FROM_TIME.getValue()).setValue(null);
			getPdfFieldsMap().get(PE187FieldName.FULL_TIME_TO_TIME.getValue()).setValue(null);	
			getPdfFieldsMap().get(PE187FieldName.PARTIALLY_TIME.getValue()).setValue(null);
			getPdfFieldsMap().get(PE187FieldName.PARTIALLY_TIME_HOURS.getValue()).setValue(null);
			getPdfFieldsMap().get(PE187FieldName.PARTIALLY_TIME_DAYLY.getValue()).setValue(null);
			getPdfFieldsMap().get(PE187FieldName.PARTIALLY_TIME_WEEKLY.getValue()).setValue(null);
			getPdfFieldsMap().get(PE187FieldName.PARTIALLY_TIME_MONTHLY.getValue()).setValue(null);
			getPdfFieldsMap().get(PE187FieldName.PARTIALLY_TIME_YEARLY.getValue()).setValue(null);
			getPdfFieldsMap().get(PE187FieldName.COMPARABLE_FULL_TIME.getValue()).setValue(null);
			getPdfFieldsMap().get(PE187FieldName.AGREEMENT_COLLECTIVE_FULL_TIME.getValue()).setValue(null);
			getPdfFieldsMap().get(PE187FieldName.LEGAL_MAX.getValue()).setValue(null);
			getPdfFieldsMap().get(PE187FieldName.JOB_TIME_DISTRIBUTION1.getValue()).setValue(null);
			getPdfFieldsMap().get(PE187FieldName.JOB_TIME_DISTRIBUTION2.getValue()).setValue(null);
			getPdfFieldsMap().get(PE187FieldName.TOTAL_DURATION.getValue()).setValue(null);
			getPdfFieldsMap().get(PE187FieldName.START_DATE.getValue()).setValue(null);
			getPdfFieldsMap().get(PE187FieldName.END_DATE.getValue()).setValue(null);
			getPdfFieldsMap().get(PE187FieldName.TRIAL_DURATION.getValue()).setValue(null);
			getPdfFieldsMap().get(PE187FieldName.SALARY_AMOUNT.getValue()).setValue(null);
			getPdfFieldsMap().get(PE187FieldName.SALARY_PERIOD.getValue()).setValue(null);
			getPdfFieldsMap().get(PE187FieldName.SALARY_CONCEPT.getValue()).setValue(null);
			getPdfFieldsMap().get(PE187FieldName.HOLIDAYS.getValue()).setValue(null);
			getPdfFieldsMap().get(PE187FieldName.AGREEMENT_COLLECTIVE.getValue()).setValue(null);
			getPdfFieldsMap().get(PE187FieldName.AGREEMENT_COLLECTIVE_MORE.getValue()).setValue(null);
			getPdfFieldsMap().get(PE187FieldName.SEPE_MUNICIPALITY.getValue()).setValue(null);
			getPdfFieldsMap().get(PE187FieldName.SEPE_MUNICIPALITY_MORE.getValue()).setValue(null);
			getPdfFieldsMap().get(PE187FieldName.ADDITIONAL_CLAUSES.getValue()).setValue(null);
			
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
	public enum PE187FieldName implements IContractFieldName{
		
		/*
		 * Contract page 1
		 */
		TC2_430_BONI("tiempocompletoboni",Boolean.FALSE),
		TC2_530_BONI("tiempoparcialboni",Boolean.FALSE),
		TC2_430("tiempocompleto",Boolean.FALSE),
		TC2_530("tiempoparcial",Boolean.FALSE),
		LEGAL_REPRESENTATIVE_NAME("nomreptra",Boolean.FALSE),
		LEGAL_REPRESENTATIVE_NIF("dnireptra",Boolean.FALSE),
		LEGAL_REPRESENTATIVE_CHARGE("calireptra",Boolean.FALSE),
		HANDICAPPED_PERSON("discapa_si",Boolean.FALSE),
		HANDICAP_CERTIFICATE_ISSUED_BY("organcerti2",Boolean.FALSE),
		RDL_43_2006_BENEFITS("acogebene_si",Boolean.FALSE),
		
		/*
		 * Contract page 2
		 */
		PROFESSION("profetraba",Boolean.FALSE),
		CATEGORY("catetraba",Boolean.FALSE),
		WORKPLACE_FULL_ADDRESS("calletrab",Boolean.FALSE),
		WORKPLACE_FULL_ADDRESS_MORE("calleloca1",Boolean.FALSE),
		FULL_TIME("sel_tpjorn1",Boolean.FALSE),
		FULL_TIME_WEEK_HOURS("horasjorna1",Boolean.FALSE),
		FULL_TIME_FROM_TIME("horainicio",Boolean.FALSE),
		FULL_TIME_TO_TIME("horafin",Boolean.FALSE),	
		PARTIALLY_TIME("sel_tpjorn2",Boolean.FALSE),
		PARTIALLY_TIME_HOURS("horasjorna2",Boolean.FALSE),
		PARTIALLY_TIME_DAYLY("sel_tpjorn_dia",Boolean.FALSE),
		PARTIALLY_TIME_WEEKLY("sel_tpjorn_sem",Boolean.FALSE),
		PARTIALLY_TIME_MONTHLY("sel_tpjorn_mes",Boolean.FALSE),
		PARTIALLY_TIME_YEARLY("sel_tpjorn_año",Boolean.FALSE),
		COMPARABLE_FULL_TIME("sel_tpjorn21",Boolean.FALSE),
		AGREEMENT_COLLECTIVE_FULL_TIME("sel_tpjorn22",Boolean.FALSE),
		LEGAL_MAX("sel_tpjorn23",Boolean.FALSE),
		JOB_TIME_DISTRIBUTION1("horatraba1",Boolean.FALSE),
		JOB_TIME_DISTRIBUTION2("horatraba2",Boolean.FALSE),
		TOTAL_DURATION("totaldura",Boolean.FALSE),
		START_DATE("fechaini",Boolean.FALSE),
		END_DATE("fechafin",Boolean.FALSE),
		TRIAL_DURATION("peridoprue",Boolean.FALSE),
		SALARY_AMOUNT("retribu",Boolean.FALSE),
		SALARY_PERIOD("perioretri",Boolean.FALSE),
		SALARY_CONCEPT("concepsala",Boolean.FALSE),
		HOLIDAYS("vacaciones",Boolean.FALSE),
		AGREEMENT_COLLECTIVE("c1",Boolean.FALSE),
		AGREEMENT_COLLECTIVE_MORE("convcole",Boolean.FALSE),
		SEPE_MUNICIPALITY("oecomu",Boolean.FALSE),
		SEPE_MUNICIPALITY_MORE("eo2",Boolean.FALSE),
		ADDITIONAL_CLAUSES("clausadici",Boolean.FALSE),
		
		;
		
		private String value;
		private boolean overridable;
		
		private PE187FieldName(String value, boolean overridable) {
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
	
	