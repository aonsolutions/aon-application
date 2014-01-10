package com.esferalia.aon.file.payroll.contract.pdf.model;

import java.io.IOException;
import java.text.SimpleDateFormat;

import org.apache.commons.lang.StringUtils;

import com.code.aon.common.ManagerBeanException;
import com.esferalia.aon.file.payroll.contract.pdf.UnsupportedContractDocumentException;
import com.esferalia.aon.file.payroll.contract.pdf.model.AbstractContractModel.FieldName;
import com.esferalia.aon.file.payroll.contrata.ContrataContratoParams;
import com.esferalia.aon.file.payroll.contrata.ContrataTransformacionesParams;
import com.esferalia.aon.file.payroll.contrata.IContrataParams;
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.payroll.enumeration.ContractCode;
import com.esferalia.aon.payroll.enumeration.contrata.TEQPTIEM;
import com.lowagie.text.pdf.PdfReader;



public class ModelPE183 extends AbstractContractModel {
	
	public final static String MODEL_NAME = "PE183";
	
	public ModelPE183(){
		super.documentName = MODEL_NAME;
	}
	
	@Override
	public void loadPdfFields(ContractCode code, Contract contract, IContrataParams contrataParams) throws UnsupportedContractDocumentException{
		// TODO
		try {
			PdfReader reader = new PdfReader(getContractModelUrl(documentName+".pdf"));
			
			readPdfFields(reader);
			
			if(code == ContractCode.C109){
				getPdfFieldsMap().get(PE183FieldName.TC2_BONI.getValue()).setValue("true");
				getPdfFieldsMap().get(PE183FieldName.TC2_109.getValue()).setValue("true");
			} else if(code == ContractCode.C209){
				getPdfFieldsMap().get(PE183FieldName.TC2_BONI.getValue()).setValue("true");
				getPdfFieldsMap().get(PE183FieldName.TC2_209.getValue()).setValue("true");
			} else if(code == ContractCode.C189){
				getPdfFieldsMap().get(PE183FieldName.TC2_NO_BONI.getValue()).setValue("true");
				getPdfFieldsMap().get(PE183FieldName.TC2_189.getValue()).setValue("true");
			} else if(code == ContractCode.C289){
				getPdfFieldsMap().get(PE183FieldName.TC2_NO_BONI.getValue()).setValue("true");
				getPdfFieldsMap().get(PE183FieldName.TC2_289.getValue()).setValue("true");
			} else {
				throw new UnsupportedContractDocumentException("El modelo de contrato seleccionado es incorrecto");
			}
			
			ContrataTransformacionesParams contrata = (ContrataTransformacionesParams) contrataParams;
			super.loadPdfCommonFields(contract, contrataParams);
			SimpleDateFormat dateFormatter = new SimpleDateFormat();
			
			getPdfFieldsMap().get(PE183FieldName.LEGAL_REPRESENTATIVE_NAME.getValue()).setValue(null);
			getPdfFieldsMap().get(PE183FieldName.LEGAL_REPRESENTATIVE_NIF.getValue()).setValue(null);
			getPdfFieldsMap().get(PE183FieldName.LEGAL_REPRESENTATIVE_CHARGE.getValue()).setValue(null);
			
			
			if(StringUtils.isNotBlank(getContractInfoMap(contract).get(PE183FieldName.SEPE_MUNICIPALITY.toString()))){
				getPdfFieldsMap().get(PE183FieldName.SEPE_MUNICIPALITY.getValue()).setValue(getContractInfoMap(contract).get(PE183FieldName.SEPE_MUNICIPALITY.toString()));
			}
			
			getPdfFieldsMap().get(PE183FieldName.TRANSFORM_DATE.getValue()).setValue(null);
			getPdfFieldsMap().get(PE183FieldName.SOURCE_CONTRACT_FULL_TIME.getValue()).setValue(null);
			getPdfFieldsMap().get(PE183FieldName.SOURCE_CONTRACT_PARTIALLY_TIME.getValue()).setValue(null);
			getPdfFieldsMap().get(PE183FieldName.SOURCE_CONTRACT_START_DATE.getValue()).setValue(null);
			getPdfFieldsMap().get(PE183FieldName.SOURCE_CONTRACT_SEPE_MUNICIPALITY.getValue()).setValue(null);

			dateFormatter.applyPattern("dd/MM/yyyy");
			getPdfFieldsMap().get(PE183FieldName.CONTRACT_START_DATE.getValue()).setValue(dateFormatter.format(contract.getStartDate()));
			
			
			getPdfFieldsMap().get(PE183FieldName.SOURCE_CONTRACT_SEPE_ID.getValue()).setValue(null);
			getPdfFieldsMap().get(PE183FieldName.BONUS_RDL_3_2012.getValue()).setValue(null);
			
			
			if(code == ContractCode.C109 || code == ContractCode.C189){
				getPdfFieldsMap().get(PE183FieldName.FULL_TIME.getValue()).setValue("true");
				if(StringUtils.isNotBlank(getContractInfoMap(contract).get(PE183FieldName.FULL_TIME_WEEK_HOURS.toString()))){
					getPdfFieldsMap().get( PE183FieldName.FULL_TIME_WEEK_HOURS.getValue()).setValue(getContractInfoMap(contract).get(PE183FieldName.FULL_TIME_WEEK_HOURS.toString()));
				}
				if(StringUtils.isNotBlank(getContractInfoMap(contract).get(PE183FieldName.FULL_TIME_START_TIME.toString()))){
					getPdfFieldsMap().get( PE183FieldName.FULL_TIME_START_TIME.getValue()).setValue(getContractInfoMap(contract).get(PE183FieldName.FULL_TIME_START_TIME.toString()));
				}
				if(StringUtils.isNotBlank(getContractInfoMap(contract).get(PE183FieldName.FULL_TIME_END_TIME.toString()))){
					getPdfFieldsMap().get( PE183FieldName.FULL_TIME_END_TIME.getValue()).setValue(getContractInfoMap(contract).get(PE183FieldName.FULL_TIME_END_TIME.toString()));
				}
			} else if(code == ContractCode.C209 || code == ContractCode.C289){
				getPdfFieldsMap().get(PE183FieldName.PARTIALLY_TIME.getValue()).setValue("true");
				getPdfFieldsMap().get(PE183FieldName.PARTIALLY_TIME_HOURS.getValue()).setValue(String.valueOf(Integer.parseInt(contrata.getHorasJornada())));
				if(contrata.getTipoJornada()==TEQPTIEM.TEQPTIEM_D){
					getPdfFieldsMap().get( PE183FieldName.PARTIALLY_TIME_DAYLY.getValue()).setValue("true");
				} else if(contrata.getTipoJornada()==TEQPTIEM.TEQPTIEM_S){
					getPdfFieldsMap().get( PE183FieldName.PARTIALLY_TIME_WEEKLY.getValue()).setValue("true");
				} else if(contrata.getTipoJornada()==TEQPTIEM.TEQPTIEM_M){
					getPdfFieldsMap().get( PE183FieldName.PARTIALLY_TIME_MONTHLY.getValue()).setValue("true");
				} else if(contrata.getTipoJornada()==TEQPTIEM.TEQPTIEM_A){
					getPdfFieldsMap().get( PE183FieldName.PARTIALLY_TIME_YEARLY.getValue()).setValue("true");
				}
				
				// TODO: what is needed for these partially time options?
				getPdfFieldsMap().get(PE183FieldName.COMPARABLE_FULL_TIME.getValue()).setValue(null);
				getPdfFieldsMap().get(PE183FieldName.AGREEMENT_COLLECTIVE_FULL_TIME.getValue()).setValue(null);
				getPdfFieldsMap().get(PE183FieldName.LEGAL_MAX.getValue()).setValue(null);
				getPdfFieldsMap().get(PE183FieldName.LEGAL_MAX_HOURS.getValue()).setValue(null);
			}
			
			
			
			getPdfFieldsMap().get(PE183FieldName.ADDITIONAL_HOURS_AGREEMENT_YES.getValue()).setValue(null);
			getPdfFieldsMap().get(PE183FieldName.ADDITIONAL_HOURS_AGREEMENT_NO.getValue()).setValue(null);
			
			/*
			 * Contract page 2
			 */
			getPdfFieldsMap().get(PE183FieldName.WORKPLACE_FULL_ADDRESS_MORE.getValue()).setValue(contract.getWorkPlace().getAddress().getFullAddress()+", "+contract.getWorkPlace().getAddress().getLocation());
			getPdfFieldsMap().get(PE183FieldName.WORKPLACE_FULL_ADDRESS_MORE2.getValue()).setValue(null);
			
			getPdfFieldsMap().get(PE183FieldName.JOB_TIME_DISTRIBUTION1.getValue()).setValue(null);
			if(StringUtils.isNotBlank(getContractInfoMap(contract).get(PE183FieldName.JOB_TIME_DISTRIBUTION2.toString()))){
				getPdfFieldsMap().get(PE183FieldName.JOB_TIME_DISTRIBUTION2.getValue()).setValue(getContractInfoMap(contract).get(PE183FieldName.JOB_TIME_DISTRIBUTION2.toString()));
			}
			
//			getPdfFieldsMap().get(PE183FieldName.PARTIALLY_TIME_DISCOTINUOUS_YES.getValue()).setValue(null);
//			getPdfFieldsMap().get(PE183FieldName.PARTIALLY_TIME_DISCOTINUOUS_NO.getValue()).setValue(null);
			
			getPdfFieldsMap().get(PE183FieldName.TRANSFORM_START_DATE.getValue()).setValue(null);
			if(StringUtils.isNotBlank(getContractInfoMap(contract).get(PE183FieldName.SALARY_AMOUNT.toString()))){
				getPdfFieldsMap().get(PE183FieldName.SALARY_AMOUNT.getValue()).setValue(getContractInfoMap(contract).get(PE183FieldName.SALARY_AMOUNT.toString()));
			}
			if(StringUtils.isNotBlank(getContractInfoMap(contract).get(PE183FieldName.SALARY_PERIOD.toString()))){
				getPdfFieldsMap().get( PE183FieldName.SALARY_PERIOD.getValue()).setValue(getContractInfoMap(contract).get(PE183FieldName.SALARY_PERIOD.toString()));
			}
			if(StringUtils.isNotBlank(getContractInfoMap(contract).get(PE183FieldName.SALARY_CONCEPT.toString()))){
				getPdfFieldsMap().get(PE183FieldName.SALARY_CONCEPT.getValue()).setValue(getContractInfoMap(contract).get(PE183FieldName.SALARY_CONCEPT.toString()));
			}
			if(StringUtils.isNotBlank(getContractInfoMap(contract).get(PE183FieldName.HOLIDAYS.toString()))){
				getPdfFieldsMap().get(PE183FieldName.HOLIDAYS.getValue()).setValue(getContractInfoMap(contract).get(PE183FieldName.HOLIDAYS.toString()));
			}
			
//			if(contrata.isReliefData()){
//				getPdfFieldsMap().get(PE183FieldName.RELIEF_CONTRACT_YES.getValue()).setValue("true");
//			} else {
//				getPdfFieldsMap().get(PE183FieldName.RELIEF_CONTRACT_NO.getValue()).setValue("true");
//			}
			
			getPdfFieldsMap().get(PE183FieldName.BT_16_AND_30_MAN.getValue()).setValue(null);
			getPdfFieldsMap().get(PE183FieldName.BT_16_AND_30_WOMAN.getValue()).setValue(null);
			getPdfFieldsMap().get(PE183FieldName.GT_45_MAN.getValue()).setValue(null);
			getPdfFieldsMap().get(PE183FieldName.GT_45_WOMAN.getValue()).setValue(null);
			
			if(StringUtils.isNotBlank(getContractDataMap(contract).get(ContextVariable.TC2.getName()))){
				
			}
			
			getPdfFieldsMap().get(PE183FieldName.IS_TRAINING_SOURCE_CONTRACT.getValue()).setValue(null);
			
			if(contract.getAgreementLevelCategory()!=null && contract.getAgreementLevelCategory().getId()!=null){
				getPdfFieldsMap().get(PE183FieldName.AGREEMENT_COLLECTIVE.getValue()).setValue(contract.getAgreementLevelCategory().getLevel().getAgreement().getDescription());
			}
			
			getPdfFieldsMap().get(PE183FieldName.SIGN_TOWN.getValue()).setValue(contract.getWorkPlace().getAddress().getCity());
			
			getPdfFieldsMap().get(PE183FieldName.ADDITIONAL_CLAUSES.getValue()).setValue(null);
			
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
	public enum PE183FieldName implements IContractFieldName{
		
		/*
		 * Contract page 1
		 */
		TC2_BONI("bonificado",Boolean.FALSE),
		TC2_109("tipocontrato_109",Boolean.FALSE),
		TC2_209("tipocontrato_209",Boolean.FALSE),
		TC2_NO_BONI("nobonificado",Boolean.FALSE),
		TC2_189("tipocontrato_189",Boolean.FALSE),
		TC2_289("tipocontrato_289",Boolean.FALSE),
		LEGAL_REPRESENTATIVE_NAME("nomreptra",Boolean.FALSE),
		LEGAL_REPRESENTATIVE_NIF("dnireptra",Boolean.FALSE),
		LEGAL_REPRESENTATIVE_CHARGE("calireptra",Boolean.FALSE),
		
		SEPE_MUNICIPALITY("nombsepe",Boolean.TRUE),
		TRANSFORM_DATE("fechaconver",Boolean.FALSE),
		SOURCE_CONTRACT_FULL_TIME("sel_jorn1", Boolean.FALSE),
		SOURCE_CONTRACT_PARTIALLY_TIME("sel_jorn2",Boolean.FALSE),
		SOURCE_CONTRACT_START_DATE("fechacelecv",Boolean.FALSE),
		SOURCE_CONTRACT_SEPE_MUNICIPALITY("oecomconv",Boolean.TRUE),
		CONTRACT_START_DATE("fecharegcv",Boolean.FALSE),
		SOURCE_CONTRACT_SEPE_ID("idcontconv",Boolean.FALSE),
		BONUS_RDL_3_2012("Casilla de verificación1",Boolean.TRUE),
		
		FULL_TIME("tipocontrato_tc", Boolean.FALSE),
		FULL_TIME_WEEK_HOURS("horasjorna1",Boolean.FALSE),
		FULL_TIME_START_TIME("horainicio",Boolean.FALSE),
		FULL_TIME_END_TIME("horafin",Boolean.FALSE),	
		PARTIALLY_TIME("tipocontrato_tp",Boolean.FALSE),
		PARTIALLY_TIME_HOURS("horasjorna2",Boolean.FALSE),
		PARTIALLY_TIME_DAYLY("tipojorntp1",Boolean.FALSE),
		PARTIALLY_TIME_WEEKLY("tipojorntp2",Boolean.FALSE),
		PARTIALLY_TIME_MONTHLY("tipojorntp3",Boolean.FALSE),
		PARTIALLY_TIME_YEARLY("tipojorntp4",Boolean.FALSE),
		COMPARABLE_FULL_TIME("sel_jorn21",Boolean.FALSE),
		AGREEMENT_COLLECTIVE_FULL_TIME("sel_jorn22",Boolean.FALSE),
		LEGAL_MAX("sel_jorn23",Boolean.FALSE),
		LEGAL_MAX_HOURS("horasjornadaTP",Boolean.FALSE),
		ADDITIONAL_HOURS_AGREEMENT_YES("sel_horcom1",Boolean.FALSE),
		ADDITIONAL_HOURS_AGREEMENT_NO("sel_horcom2",Boolean.FALSE),
		
		/*
		 * Contract page 2
		 */
		WORKPLACE_FULL_ADDRESS_MORE("calletrab",Boolean.FALSE),
		WORKPLACE_FULL_ADDRESS_MORE2("Texto3",Boolean.FALSE),
		JOB_TIME_DISTRIBUTION1("distraba2",Boolean.FALSE),
		JOB_TIME_DISTRIBUTION2("distraba1",Boolean.TRUE),
		PARTIALLY_TIME_DISCOTINUOUS_YES("set_ctorel55",Boolean.FALSE),
		PARTIALLY_TIME_DISCOTINUOUS_NO("set_ctorel555",Boolean.FALSE),
		TRANSFORM_START_DATE("fechaini",Boolean.FALSE),
		SALARY_AMOUNT("retribu",Boolean.TRUE),
		SALARY_PERIOD("perioretri",Boolean.TRUE),
		SALARY_CONCEPT("concepsala",Boolean.TRUE),
		HOLIDAYS("vacaciones",Boolean.TRUE),
		RELIEF_CONTRACT_YES("set_ctorel1",Boolean.FALSE),
		RELIEF_CONTRACT_NO("set_ctorel2",Boolean.FALSE),

		BT_16_AND_30_MAN("Casilla de verificación584",Boolean.TRUE),
		BT_16_AND_30_WOMAN("Casilla de verificación596",Boolean.TRUE),
		GT_45_MAN("casilla 789541",Boolean.TRUE),
		GT_45_WOMAN("casilla 84461",Boolean.TRUE),
		
		IS_TRAINING_SOURCE_CONTRACT("casilla 85469",Boolean.FALSE),
		AGREEMENT_COLLECTIVE("convcole",Boolean.FALSE),
		SIGN_TOWN("munifirma2",Boolean.FALSE),
		ADDITIONAL_CLAUSES("clausadici",Boolean.FALSE),
		;
		
		private String value;
		private boolean overridable;
		
		private PE183FieldName(String value, boolean overridable) {
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
	
	