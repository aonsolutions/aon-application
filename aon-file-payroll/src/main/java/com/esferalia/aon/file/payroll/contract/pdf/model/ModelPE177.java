package com.esferalia.aon.file.payroll.contract.pdf.model;

import java.io.IOException;
import java.text.SimpleDateFormat;

import org.apache.commons.lang.StringUtils;

import com.code.aon.common.ManagerBeanException;
import com.esferalia.aon.file.payroll.contract.pdf.UnsupportedContractDocumentException;
import com.esferalia.aon.file.payroll.contrata.ContrataContratoParams;
import com.esferalia.aon.file.payroll.contrata.IContrataParams;
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.payroll.enumeration.ContractCode;
import com.esferalia.aon.payroll.enumeration.contrata.TEQPTIEM;
import com.lowagie.text.pdf.PdfReader;



public class ModelPE177 extends AbstractContractModel {
	
	public final static String MODEL_NAME = "PE177";
	
	public ModelPE177(){
		super.documentName = MODEL_NAME;
	}
	
	@Override
	public void loadPdfFieldValues(ContractCode code, Contract contract, IContrataParams contrataParams) throws UnsupportedContractDocumentException{
		// TODO
		try {
			PdfReader reader = new PdfReader(getContractModelUrl(documentName+".pdf"));
			
			readPdfFields(reader);
			
			if(code == ContractCode.C401){
				getPdfFieldsMap().get(PE177FieldName.FULL_TIME_HEADER.getValue()).setValue("true");
				getPdfFieldsMap().get(PE177FieldName.TC2_401.getValue()).setValue("true");
			} else if(code == ContractCode.C402){
				getPdfFieldsMap().get(PE177FieldName.FULL_TIME_HEADER.getValue()).setValue("true");
				getPdfFieldsMap().get(PE177FieldName.TC2_402.getValue()).setValue("true");
			} else if(code == ContractCode.C410){
				getPdfFieldsMap().get(PE177FieldName.FULL_TIME_HEADER.getValue()).setValue("true");
				getPdfFieldsMap().get(PE177FieldName.TC2_410.getValue()).setValue("true");
			} else if(code == ContractCode.C501){
				getPdfFieldsMap().get(PE177FieldName.PARTIALLY_TIME_HEADER.getValue()).setValue("true");
				getPdfFieldsMap().get(PE177FieldName.TC2_501.getValue()).setValue("true");
			} else if(code == ContractCode.C502){
				getPdfFieldsMap().get(PE177FieldName.PARTIALLY_TIME_HEADER.getValue()).setValue("true");
				getPdfFieldsMap().get(PE177FieldName.TC2_502.getValue()).setValue("true");
			} else if(code == ContractCode.C510){
				getPdfFieldsMap().get(PE177FieldName.PARTIALLY_TIME_HEADER.getValue()).setValue("true");
				getPdfFieldsMap().get(PE177FieldName.TC2_510.getValue()).setValue("true");
			} else if(code == ContractCode.C540){
				getPdfFieldsMap().get(PE177FieldName.PARTIALLY_TIME_HEADER.getValue()).setValue("true");
				getPdfFieldsMap().get(PE177FieldName.TC2_540.getValue()).setValue("true");
			} else {
				throw new UnsupportedContractDocumentException("El modelo de contrato seleccionado es incorrecto");
			}
			
			ContrataContratoParams contrata = (ContrataContratoParams) contrataParams;
			
			super.loadPdfCommonFields(contract, contrataParams);
			
			SimpleDateFormat dateFormatter = new SimpleDateFormat();
			
			/*
			 * Contract page 1
			 */
			getPdfFieldsMap().get(PE177FieldName.LEGAL_REPRESENTATIVE_NAME.getValue()).setValue(null);
			getPdfFieldsMap().get(PE177FieldName.LEGAL_REPRESENTATIVE_NIF.getValue()).setValue(null);
			getPdfFieldsMap().get(PE177FieldName.LEGAL_REPRESENTATIVE_CHARGE.getValue()).setValue(null);
			
			getPdfFieldsMap().get(PE177FieldName.PROFESSION.getValue()).setValue(contrata.getCno().getTitle());
			getPdfFieldsMap().get(PE177FieldName.CATEGORY.getValue()).setValue(contract.getCategoryDescription());
			getPdfFieldsMap().get(PE177FieldName.WORKPLACE_FULL_ADDRESS_MORE.getValue()).setValue(contract.getWorkPlace().getAddress().getFullAddress()+", "+contract.getWorkPlace().getAddress().getLocation());
			
			if(code == ContractCode.C401 || code == ContractCode.C402 || code == ContractCode.C410){
				getPdfFieldsMap().get(PE177FieldName.FULL_TIME.getValue()).setValue("true");
				if(StringUtils.isNotBlank(getContractInfoMap(contract).get(PE177FieldName.FULL_TIME_WEEK_HOURS.toString()))){
					getPdfFieldsMap().get( PE177FieldName.FULL_TIME_WEEK_HOURS.getValue()).setValue(getContractInfoMap(contract).get(PE177FieldName.FULL_TIME_WEEK_HOURS.toString()));
				}
				if(StringUtils.isNotBlank(getContractInfoMap(contract).get(PE177FieldName.FULL_TIME_START_TIME.toString()))){
					getPdfFieldsMap().get( PE177FieldName.FULL_TIME_START_TIME.getValue()).setValue(getContractInfoMap(contract).get(PE177FieldName.FULL_TIME_START_TIME.toString()));
				}
				if(StringUtils.isNotBlank(getContractInfoMap(contract).get(PE177FieldName.FULL_TIME_END_TIME.toString()))){
					getPdfFieldsMap().get( PE177FieldName.FULL_TIME_END_TIME.getValue()).setValue(getContractInfoMap(contract).get(PE177FieldName.FULL_TIME_END_TIME.toString()));
				}
			} else if(code == ContractCode.C501 || code == ContractCode.C502 | code == ContractCode.C510 || code == ContractCode.C540){
				getPdfFieldsMap().get(PE177FieldName.PARTIALLY_TIME.getValue()).setValue("true");
				getPdfFieldsMap().get(PE177FieldName.PARTIALLY_TIME_HOURS.getValue()).setValue(String.valueOf(Integer.parseInt(contrata.getHorasJornada())));
				if(contrata.getTipoJornada()==TEQPTIEM.TEQPTIEM_D){
					getPdfFieldsMap().get( PE177FieldName.PARTIALLY_TIME_DAYLY.getValue()).setValue("true");
				} else if(contrata.getTipoJornada()==TEQPTIEM.TEQPTIEM_S){
					getPdfFieldsMap().get( PE177FieldName.PARTIALLY_TIME_WEEKLY.getValue()).setValue("true");
				} else if(contrata.getTipoJornada()==TEQPTIEM.TEQPTIEM_M){
					getPdfFieldsMap().get( PE177FieldName.PARTIALLY_TIME_MONTHLY.getValue()).setValue("true");
				} else if(contrata.getTipoJornada()==TEQPTIEM.TEQPTIEM_A){
					getPdfFieldsMap().get( PE177FieldName.PARTIALLY_TIME_YEARLY.getValue()).setValue("true");
				}
				
				// TODO: what is needed for these partially time options?
				getPdfFieldsMap().get(PE177FieldName.COMPARABLE_FULL_TIME.getValue()).setValue(null);
				getPdfFieldsMap().get(PE177FieldName.AGREEMENT_COLLECTIVE_FULL_TIME.getValue()).setValue(null);
				getPdfFieldsMap().get(PE177FieldName.LEGAL_MAX.getValue()).setValue(null);
			}

			
			getPdfFieldsMap().get(PE177FieldName.JOB_TIME_DISTRIBUTION1.getValue()).setValue(null);
			if(StringUtils.isNotBlank(getContractInfoMap(contract).get(PE177FieldName.JOB_TIME_DISTRIBUTION2.toString()))){
				getPdfFieldsMap().get(PE177FieldName.JOB_TIME_DISTRIBUTION2.getValue()).setValue(getContractInfoMap(contract).get(PE177FieldName.JOB_TIME_DISTRIBUTION2.toString()));
			}
			
			dateFormatter.applyPattern("dd/MM/yyyy");
			getPdfFieldsMap().get(PE177FieldName.START_DATE.getValue()).setValue(dateFormatter.format(contract.getStartDate()));
			if(contract.getEndDate()!=null){
				getPdfFieldsMap().get(PE177FieldName.END_DATE.getValue()).setValue(dateFormatter.format(contract.getEndDate()));
			}
			
			if(StringUtils.isNotBlank(getContractInfoMap(contract).get(PE177FieldName.TRIAL_DURATION.toString()))){
				getPdfFieldsMap().get( PE177FieldName.TRIAL_DURATION.getValue()).setValue(getContractInfoMap(contract).get(PE177FieldName.TRIAL_DURATION.toString()));
			}
			getPdfFieldsMap().get(PE177FieldName.GRATER_DURATION_AGREEMENT_COLLECTIVE.getValue()).setValue(null);
			
			/*
			 * Contract page 2
			 */
			if(StringUtils.isNotBlank(getContractInfoMap(contract).get(PE177FieldName.SALARY_AMOUNT.toString()))){
				getPdfFieldsMap().get( PE177FieldName.SALARY_AMOUNT.getValue()).setValue(getContractInfoMap(contract).get(PE177FieldName.SALARY_AMOUNT.toString()));
			}
			if(StringUtils.isNotBlank(getContractInfoMap(contract).get(PE177FieldName.SALARY_PERIOD.toString()))){
				getPdfFieldsMap().get( PE177FieldName.SALARY_PERIOD.getValue()).setValue(getContractInfoMap(contract).get(PE177FieldName.SALARY_PERIOD.toString()));
			}
			if(StringUtils.isNotBlank(getContractInfoMap(contract).get(PE177FieldName.SALARY_CONCEPT.toString()))){
				getPdfFieldsMap().get( PE177FieldName.SALARY_CONCEPT.getValue()).setValue(getContractInfoMap(contract).get(PE177FieldName.SALARY_CONCEPT.toString()));
			}
			if(StringUtils.isNotBlank(getContractInfoMap(contract).get(PE177FieldName.HOLIDAYS.toString()))){
				getPdfFieldsMap().get( PE177FieldName.HOLIDAYS.getValue()).setValue(getContractInfoMap(contract).get(PE177FieldName.HOLIDAYS.toString()));
			}

			
			getPdfFieldsMap().get(PE177FieldName.CAUSE1.getValue()).setValue(null);
			getPdfFieldsMap().get(PE177FieldName.CAUSE1_DESCRIPTION.getValue()).setValue(null);
			getPdfFieldsMap().get(PE177FieldName.CAUSE2.getValue()).setValue(null);
			getPdfFieldsMap().get(PE177FieldName.CAUSE2_DESCRIPTION1.getValue()).setValue(null);
			getPdfFieldsMap().get(PE177FieldName.CAUSE2_DESCRIPTION2.getValue()).setValue(null);
			getPdfFieldsMap().get(PE177FieldName.CAUSE3.getValue()).setValue(null);
			getPdfFieldsMap().get(PE177FieldName.CAUSE3_REPLACED_EMPLOYEE_NAME.getValue()).setValue(null);
			getPdfFieldsMap().get(PE177FieldName.CAUSE3_1.getValue()).setValue(null);
			getPdfFieldsMap().get(PE177FieldName.CAUSE3_2.getValue()).setValue(null);
			getPdfFieldsMap().get(PE177FieldName.CAUSE3_3.getValue()).setValue(null);
			getPdfFieldsMap().get(PE177FieldName.CAUSE3_4.getValue()).setValue(null);
			getPdfFieldsMap().get(PE177FieldName.CAUSE3_5.getValue()).setValue(null);
			getPdfFieldsMap().get(PE177FieldName.CAUSE3_6.getValue()).setValue(null);
			getPdfFieldsMap().get(PE177FieldName.CAUSE3_7.getValue()).setValue(null);
			getPdfFieldsMap().get(PE177FieldName.CAUSE3_7_A.getValue()).setValue(null);
			getPdfFieldsMap().get(PE177FieldName.CAUSE3_7_B.getValue()).setValue(null);
			getPdfFieldsMap().get(PE177FieldName.CAUSE3_OCCUPATION.getValue()).setValue(null);
			getPdfFieldsMap().get(PE177FieldName.CAUSE4.getValue()).setValue(null);
			getPdfFieldsMap().get(PE177FieldName.CAUSE4_SALARY_REDUCTION.getValue()).setValue(null);
			
			
			if(contract.getAgreementLevelCategory()!=null && contract.getAgreementLevelCategory().getId()!=null){
				getPdfFieldsMap().get( PE177FieldName.AGREEMENT_COLLECTIVE.getValue()).setValue(contract.getAgreementLevelCategory().getLevel().getAgreement().getDescription());
			}
			if(StringUtils.isNotBlank(getContractInfoMap(contract).get(PE177FieldName.SEPE_MUNICIPALITY.toString()))){
				getPdfFieldsMap().get( PE177FieldName.SEPE_MUNICIPALITY.getValue()).setValue(getContractInfoMap(contract).get(PE177FieldName.SEPE_MUNICIPALITY.toString()));
			}
			
			getPdfFieldsMap().get(PE177FieldName.ADDITIONAL_CLAUSES.getValue()).setValue(null);
			
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
	public enum PE177FieldName implements IContractFieldName{
	
		/*
		 * Contract page 1
		 */
		
		FULL_TIME_HEADER("tiempocompleto",Boolean.FALSE),
		PARTIALLY_TIME_HEADER("tiempoparcial",Boolean.FALSE),
		
		TC2_401("tipocontrato_401",Boolean.FALSE),
		TC2_402("tipocontrato_402",Boolean.FALSE),
		TC2_410("tipocontrato_410",Boolean.FALSE),
		TC2_501("tipocontrato_501",Boolean.FALSE),
		TC2_502("tipocontrato_502",Boolean.FALSE),
		TC2_510("tipocontrato_510",Boolean.FALSE),
		TC2_540("tipocontrato_540",Boolean.FALSE),
		LEGAL_REPRESENTATIVE_NAME("nomreptra",Boolean.FALSE),
		LEGAL_REPRESENTATIVE_NIF("dnireptra",Boolean.FALSE),
		LEGAL_REPRESENTATIVE_CHARGE("calireptra",Boolean.FALSE),
		PROFESSION("profetraba",Boolean.FALSE),
		CATEGORY("catetraba",Boolean.FALSE),
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
		START_DATE("fechaini",Boolean.FALSE),
		END_DATE("fechafin",Boolean.FALSE),
		TRIAL_DURATION("peridoprue",Boolean.TRUE),
		GRATER_DURATION_AGREEMENT_COLLECTIVE("in_ccolec",Boolean.FALSE),
		
		/*
		 * Contract page 2
		 */
		SALARY_AMOUNT("retribu",Boolean.TRUE),
		SALARY_PERIOD("perioretri",Boolean.TRUE),
		SALARY_CONCEPT("concepsala",Boolean.TRUE),
		HOLIDAYS("vacaciones",Boolean.TRUE),
		CAUSE1("in_causa1",Boolean.FALSE),
		CAUSE1_DESCRIPTION("causaobra",Boolean.FALSE),
		CAUSE2("in_causa2",Boolean.FALSE),
		CAUSE2_DESCRIPTION1("causaobra1",Boolean.FALSE),
		CAUSE2_DESCRIPTION2("causaobra2",Boolean.FALSE),
		CAUSE3("in_causa3",Boolean.FALSE),
		CAUSE3_REPLACED_EMPLOYEE_NAME("nomtrasus",Boolean.FALSE),
		CAUSE3_1("in_causa31",Boolean.FALSE),
		CAUSE3_2("in_causa32",Boolean.FALSE),
		CAUSE3_3("in_causa33",Boolean.FALSE),
		CAUSE3_4("in_causa34",Boolean.FALSE),
		CAUSE3_5("in_causa35",Boolean.FALSE),
		CAUSE3_6("in_causa36",Boolean.FALSE),
		CAUSE3_7("violengen",Boolean.FALSE),
		CAUSE3_7_A("violengenA",Boolean.FALSE),
		CAUSE3_7_B("violengenB",Boolean.FALSE),
		CAUSE3_OCCUPATION("puestotra",Boolean.FALSE),
		CAUSE4("reducjorn",Boolean.FALSE),
		CAUSE4_SALARY_REDUCTION("reducsalario",Boolean.FALSE),
		AGREEMENT_COLLECTIVE("convcole",Boolean.FALSE),
		SEPE_MUNICIPALITY("oecomu",Boolean.TRUE),
		ADDITIONAL_CLAUSES("clausadici",Boolean.FALSE),
		;
		
		private String value;
		private boolean overridable;
		
		private PE177FieldName(String value, boolean overridable) {
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
	
	