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



public class ModelPE170 extends AbstractContractModel {

	
	public final static String MODEL_NAME = "PE170";
	
	
	public ModelPE170(){
		super.documentName = MODEL_NAME;
	}
	
	@Override
	public void loadPdfFieldValues(ContractCode code, Contract contract, IContrataParams contrataParams) throws UnsupportedContractDocumentException{
		// TODO 
		
		try {
			PdfReader reader = new PdfReader(getContractModelUrl(documentName+".pdf"));
			
			readPdfFields(reader);
			
			if(code == ContractCode.C100){
				getPdfFieldsMap().get(PE170FieldName.TC2_100.getValue()).setValue("true");
			} else if(code == ContractCode.C200){
				getPdfFieldsMap().get(PE170FieldName.TC2_200.getValue()).setValue("true");
			} else {
				throw new UnsupportedContractDocumentException("El modelo de contrato seleccionado es incorrecto");
			}
			
			ContrataContratoParams contrata = (ContrataContratoParams) contrataParams;
			
			super.loadPdfCommonFields(contract, contrataParams);
			
			SimpleDateFormat dateFormatter = new SimpleDateFormat();
			/*
			 * Contract page 1
			 */
			getPdfFieldsMap().get(PE170FieldName.LEGAL_REPRESENTATIVE_NAME.getValue()).setValue(null);
			getPdfFieldsMap().get(PE170FieldName.LEGAL_REPRESENTATIVE_NIF.getValue()).setValue(null);
			getPdfFieldsMap().get(PE170FieldName.LEGAL_REPRESENTATIVE_CHARGE.getValue()).setValue(null);
			
			getPdfFieldsMap().get(PE170FieldName.PROFESSION.getValue()).setValue(contrata.getCno().getTitle());
			getPdfFieldsMap().get(PE170FieldName.CATEGORY.getValue()).setValue(contract.getCategoryDescription());
			getPdfFieldsMap().get(PE170FieldName.WORKPLACE_FULL_ADDRESS.getValue()).setValue(null);
			getPdfFieldsMap().get(PE170FieldName.WORKPLACE_FULL_ADDRESS_MORE.getValue()).setValue(contract.getWorkPlace().getAddress().getFullAddress()+", "+contract.getWorkPlace().getAddress().getLocation());
			
			if(code == ContractCode.C100){
				getPdfFieldsMap().get(PE170FieldName.FULL_TIME.getValue()).setValue("true");
				if(StringUtils.isNotBlank(getContractInfoMap(contract).get(PE170FieldName.FULL_TIME_WEEK_HOURS.toString()))){
					getPdfFieldsMap().get(PE170FieldName.FULL_TIME_WEEK_HOURS.getValue()).setValue(getContractInfoMap(contract).get(PE170FieldName.FULL_TIME_WEEK_HOURS.toString()));
				}
				if(StringUtils.isNotBlank(getContractInfoMap(contract).get(PE170FieldName.FULL_TIME_START_TIME.toString()))){
					getPdfFieldsMap().get(PE170FieldName.FULL_TIME_START_TIME.getValue()).setValue(getContractInfoMap(contract).get(PE170FieldName.FULL_TIME_START_TIME.toString()));
				}
				if(StringUtils.isNotBlank(getContractInfoMap(contract).get(PE170FieldName.FULL_TIME_END_TIME.toString()))){
					getPdfFieldsMap().get(PE170FieldName.FULL_TIME_END_TIME.getValue()).setValue(getContractInfoMap(contract).get(PE170FieldName.FULL_TIME_END_TIME.toString()));
				}
			} else if(code == ContractCode.C200){
				getPdfFieldsMap().get(PE170FieldName.PARTIALLY_TIME.getValue()).setValue("true");
				getPdfFieldsMap().get(PE170FieldName.PARTIALLY_TIME_HOURS.getValue()).setValue(String.valueOf(Integer.parseInt(contrata.getHorasJornada())));
				if(contrata.getTipoJornada()==TEQPTIEM.TEQPTIEM_D){
					getPdfFieldsMap().get(PE170FieldName.PARTIALLY_TIME_DAYLY.getValue()).setValue("true");
				} else if(contrata.getTipoJornada()==TEQPTIEM.TEQPTIEM_S){
					getPdfFieldsMap().get(PE170FieldName.PARTIALLY_TIME_WEEKLY.getValue()).setValue("true");
				} else if(contrata.getTipoJornada()==TEQPTIEM.TEQPTIEM_M){
					getPdfFieldsMap().get(PE170FieldName.PARTIALLY_TIME_MONTHLY.getValue()).setValue("true");
				} else if(contrata.getTipoJornada()==TEQPTIEM.TEQPTIEM_A){
					getPdfFieldsMap().get(PE170FieldName.PARTIALLY_TIME_YEARLY.getValue()).setValue("true");
				}

				// TODO: what is needed for these partially time options?
				getPdfFieldsMap().get(PE170FieldName.COMPARABLE_FULL_TIME.getValue()).setValue(null);
				getPdfFieldsMap().get(PE170FieldName.AGREEMENT_COLLECTIVE_FULL_TIME.getValue()).setValue(null);
				getPdfFieldsMap().get(PE170FieldName.LEGAL_MAX.getValue()).setValue(null);
				getPdfFieldsMap().get(PE170FieldName.LEGAL_MAX_HOURS.getValue()).setValue(null);
			}
			

			/*
			 * Contract page 2
			 */
			getPdfFieldsMap().get(PE170FieldName.JOB_TIME_DISTRIBUTION1.getValue()).setValue(null);
			if(StringUtils.isNotBlank(getContractInfoMap(contract).get(PE170FieldName.JOB_TIME_DISTRIBUTION2.toString()))){
				getPdfFieldsMap().get(PE170FieldName.JOB_TIME_DISTRIBUTION2.getValue()).setValue(getContractInfoMap(contract).get(PE170FieldName.JOB_TIME_DISTRIBUTION2.toString()));
			}
			getPdfFieldsMap().get(PE170FieldName.PARTIALLY_TIME_DISCOTINUOUS_YES.getValue()).setValue(null);
			getPdfFieldsMap().get(PE170FieldName.PARTIALLY_TIME_DISCOTINUOUS_NO.getValue()).setValue(null);
			getPdfFieldsMap().get(PE170FieldName.PARTIALLY_TIME_COMPLEMENTARY_HOURS_YES.getValue()).setValue(null);
			getPdfFieldsMap().get(PE170FieldName.PARTIALLY_TIME_COMPLEMENTARY_HOURS_NO.getValue()).setValue(null);
			dateFormatter.applyPattern("dd/MM/yyyy");
			getPdfFieldsMap().get(PE170FieldName.START_DATE.getValue()).setValue(dateFormatter.format(contract.getStartDate()));
			
			if(StringUtils.isNotBlank(getContractInfoMap(contract).get(PE170FieldName.TRIAL_DURATION.toString()))){
				getPdfFieldsMap().get(PE170FieldName.TRIAL_DURATION.getValue()).setValue(getContractInfoMap(contract).get(PE170FieldName.TRIAL_DURATION.toString()));
			}
			
			if(contrata.isReliefData()){
				getPdfFieldsMap().get(PE170FieldName.RELIEF_CONTRACT_YES.getValue()).setValue("true");
			} else {
				getPdfFieldsMap().get(PE170FieldName.RELIEF_CONTRACT_NO.getValue()).setValue("true");
			}
			
			if(StringUtils.isNotBlank(getContractInfoMap(contract).get(PE170FieldName.SALARY_AMOUNT.toString()))){
				getPdfFieldsMap().get(PE170FieldName.SALARY_AMOUNT.getValue()).setValue(getContractInfoMap(contract).get(PE170FieldName.SALARY_AMOUNT.toString()));
			}
			if(StringUtils.isNotBlank(getContractInfoMap(contract).get(PE170FieldName.SALARY_PERIOD.toString()))){
				getPdfFieldsMap().get( PE170FieldName.SALARY_PERIOD.getValue()).setValue(getContractInfoMap(contract).get(PE170FieldName.SALARY_PERIOD.toString()));
			}
			if(StringUtils.isNotBlank(getContractInfoMap(contract).get(PE170FieldName.SALARY_CONCEPT.toString()))){
				getPdfFieldsMap().get(PE170FieldName.SALARY_CONCEPT.getValue()).setValue(getContractInfoMap(contract).get(PE170FieldName.SALARY_CONCEPT.toString()));
			}
			if(StringUtils.isNotBlank(getContractInfoMap(contract).get(PE170FieldName.HOLIDAYS.toString()))){
				getPdfFieldsMap().get(PE170FieldName.HOLIDAYS.getValue()).setValue(getContractInfoMap(contract).get(PE170FieldName.HOLIDAYS.toString()));
			}
			if(contract.getAgreementLevelCategory()!=null && contract.getAgreementLevelCategory().getId()!=null){
				getPdfFieldsMap().get(PE170FieldName.AGREEMENT_COLLECTIVE.getValue()).setValue(contract.getAgreementLevelCategory().getLevel().getAgreement().getDescription());
			}
			if(StringUtils.isNotBlank(getContractInfoMap(contract).get(PE170FieldName.SEPE_MUNICIPALITY.toString()))){
				getPdfFieldsMap().get(PE170FieldName.SEPE_MUNICIPALITY.getValue()).setValue(getContractInfoMap(contract).get(PE170FieldName.SEPE_MUNICIPALITY.toString()));
			}
			getPdfFieldsMap().get(PE170FieldName.ADDITIONAL_CLAUSES.getValue()).setValue(null);
			
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
	public enum PE170FieldName implements IContractFieldName{
		/*
		 * Contract page 1
		 */
		TC2_100("tipocontrato_100",Boolean.FALSE),
		TC2_200("tipocontrato_200",Boolean.FALSE),
		LEGAL_REPRESENTATIVE_NAME("nomreptra",Boolean.FALSE),
		LEGAL_REPRESENTATIVE_NIF("dnireptra",Boolean.FALSE),
		LEGAL_REPRESENTATIVE_CHARGE("calireptra",Boolean.FALSE),
		PROFESSION("profetraba",Boolean.FALSE),
		CATEGORY("catetraba",Boolean.FALSE),
		WORKPLACE_FULL_ADDRESS("ubicact",Boolean.FALSE),
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
		LEGAL_MAX_HOURS("horas4",Boolean.FALSE),

		/*
		 * Contract page 2
		 */
		JOB_TIME_DISTRIBUTION1("horatraba1",Boolean.FALSE),
		JOB_TIME_DISTRIBUTION2("horatraba2",Boolean.TRUE),
		PARTIALLY_TIME_DISCOTINUOUS_YES("sel_adj1",Boolean.FALSE),
		PARTIALLY_TIME_DISCOTINUOUS_NO("sel_adj2",Boolean.FALSE),
		PARTIALLY_TIME_COMPLEMENTARY_HOURS_YES("sel_adjhhcc1",Boolean.FALSE),
		PARTIALLY_TIME_COMPLEMENTARY_HOURS_NO("sel_adjhhcc2",Boolean.FALSE),
		START_DATE("fechaini",Boolean.FALSE),
		TRIAL_DURATION("peridoprue",Boolean.TRUE),
		RELIEF_CONTRACT_YES("sel_ctorel1",Boolean.FALSE),
		RELIEF_CONTRACT_NO("sel_ctorel2",Boolean.FALSE),
		SALARY_AMOUNT("retribu",Boolean.TRUE),
		SALARY_PERIOD("perioretri",Boolean.TRUE),
		SALARY_CONCEPT("concepsala",Boolean.TRUE),
		HOLIDAYS("vacaciones",Boolean.TRUE),
		AGREEMENT_COLLECTIVE("convcole",Boolean.FALSE),
		SEPE_MUNICIPALITY("oecomu",Boolean.TRUE),
		ADDITIONAL_CLAUSES("clausadici",Boolean.FALSE),
		;
		
		private String value;
		private boolean overridable;
		
		private PE170FieldName(String value, boolean overridable) {
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
	
	