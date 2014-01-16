package com.esferalia.aon.file.payroll.contract.pdf.model;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ResourceBundle;

import org.apache.commons.lang.StringUtils;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.geozone.GeoZone;
import com.code.aon.registry.RegistryAddress;
import com.esferalia.aon.file.payroll.contract.pdf.UnsupportedContractDocumentException;
import com.esferalia.aon.file.payroll.contrata.ContrataContratoParams;
import com.esferalia.aon.file.payroll.contrata.IContrataParams;
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.payroll.enumeration.ContractCode;
import com.lowagie.text.pdf.PdfReader;



public class ModelPE151 extends AbstractContractModel {
	
	public final static String MODEL_NAME = "PE151";
	
	public ModelPE151(){
		super.documentName = MODEL_NAME;
	}
	
	@Override
	public void loadPdfFieldValues(ContractCode code, Contract contract, IContrataParams contrataParams) throws UnsupportedContractDocumentException{
		ContrataContratoParams params = (ContrataContratoParams) contrataParams;
		
		// TODO
		
		try {
			PdfReader reader = new PdfReader(getContractModelUrl(documentName+".pdf"));
			
			readPdfFields(reader);
			
			if(code == ContractCode.C100){
				getPdfFieldsMap().get(PE151FieldName.TC2_100.getValue()).setValue("true");
			} else if(code == ContractCode.C150){
				getPdfFieldsMap().get(PE151FieldName.TC2_150.getValue()).setValue("true");
			} else {
				throw new UnsupportedContractDocumentException("El modelo de contrato seleccionado es incorrecto");
			}
			
			ContrataContratoParams contrata = (ContrataContratoParams) contrataParams;
			
			super.loadPdfCommonFields(contract, contrataParams);
			
			/*
			 * FIXME: FIELDS OVERRIDES
			 * exists same fileds named with different labels
			 * it is necessary to normalize pdf files of contract models 
			 */
			try {	
				getPdfFieldsMap().get(PE151FieldName.ENTERPRISE_COUNTRY1.getValue()).setValue(contract.getWorkPlace().getEnterprise().getRegistry().getNationality().getName(getLocale()));
			} catch (StringIndexOutOfBoundsException aie) {
				// do nothing
			} catch (NullPointerException npe) {
				// do nothing
			}
			
			try {
				RegistryAddress address = contract.getWorkPlace().getEnterprise().getRegistry().getDefaultAddress();
				ResourceBundle bundle = ResourceBundle.getBundle("com.esferalia.aon.payroll.i18n.municipalities");
				getPdfFieldsMap().get(PE151FieldName.ENTERPRISE_MUNICIPALITY1.getValue()).setValue(bundle.getString(address.getMunicipalityCode()));
			} catch (NullPointerException npe) {
				// do nothing
			}
			
			try {
				GeoZone country = obtainCountry(contract.getWorkPlace().getAddress().getGeozone());
				getPdfFieldsMap().get(PE151FieldName.WORKPLACE_COUNTRY1.getValue()).setValue(country.getName());
			} catch (StringIndexOutOfBoundsException aie) {
				// do nothing
			} catch (NullPointerException npe) {
				// do nothing
			}
			try {
				RegistryAddress address = contract.getWorkPlace().getAddress();
				ResourceBundle bundle = ResourceBundle.getBundle("com.esferalia.aon.payroll.i18n.municipalities");
				getPdfFieldsMap().get(PE151FieldName.WORKPLACE_MUNICIPALITY1.getValue()).setValue(bundle.getString(address.getMunicipalityCode()));
			} catch (NullPointerException npe) {
				// do nothing
			}
			try {
				getPdfFieldsMap().get(PE151FieldName.EMPLOYEE_COUNTRY1.getValue()).setValue(String.valueOf(contract.getPerson().getRegistry().getNationality().getName(getLocale())));
			} catch (NullPointerException npe) {
				// do nothing
			}
			try {
				RegistryAddress address = contract.getPerson().getRegistry().getDefaultAddress();
				ResourceBundle bundle = ResourceBundle.getBundle("com.esferalia.aon.payroll.i18n.municipalities");
				getPdfFieldsMap().get(PE151FieldName.EMPLOYEE_ADDRESS_MUNICIPALITY1.getValue()).setValue(bundle.getString(address.getMunicipalityCode()));
			} catch (NullPointerException npe) {
				// do nothing
			}
			try {
				GeoZone country = obtainCountry(contract.getPerson().getRegistry().getDefaultAddress().getGeozone());
				getPdfFieldsMap().get(PE151FieldName.EMPLOYEE_ADDRESS_COUNTRY1.getValue()).setValue(country.getName());
			} catch (StringIndexOutOfBoundsException aie) {
				// do nothing
			} catch (NullPointerException npe) {
				// do nothing
			}
			/*
			 * FIXME: FIELDS OVERRIDES
			 */
			
			SimpleDateFormat dateFormatter = new SimpleDateFormat();
			/*
			 * Contract page 1
			 */
			getPdfFieldsMap().get(PE151FieldName.LEGAL_REPRESENTATIVE_NAME.getValue()).setValue(null);
			getPdfFieldsMap().get(PE151FieldName.LEGAL_REPRESENTATIVE_NIF.getValue()).setValue(null);
			getPdfFieldsMap().get(PE151FieldName.LEGAL_REPRESENTATIVE_CHARGE.getValue()).setValue(null);
			
			// TODO: what must be done with this?
			getPdfFieldsMap().get(PE151FieldName.ART4_RDL_3_2012_YES.getValue()).setValue(null);
			getPdfFieldsMap().get(PE151FieldName.ART4_RDL_3_2012_NO.getValue()).setValue(null);
			
			
			getPdfFieldsMap().get(PE151FieldName.PROFESSION.getValue()).setValue(contrata.getCno().getTitle());
			getPdfFieldsMap().get(PE151FieldName.CATEGORY.getValue()).setValue(contract.getCategoryDescription());
			getPdfFieldsMap().get(PE151FieldName.FUNCTION.getValue()).setValue(null);
			getPdfFieldsMap().get(PE151FieldName.WORKPLACE_FULL_ADDRESS.getValue()).setValue(contract.getWorkPlace().getAddress().getFullAddress() + ", " + contract.getWorkPlace().getAddress().getLocation());
			
			dateFormatter.applyPattern("dd/MM/yyyy");
			getPdfFieldsMap().get(PE151FieldName.CONTRACT_START_DATE.getValue()).setValue(dateFormatter.format(contract.getStartDate()));
			
			if(StringUtils.isNotBlank(getContractInfoMap(contract).get(PE151FieldName.WEEK_HOURS.toString()))){
				getPdfFieldsMap().get(PE151FieldName.WEEK_HOURS.getValue()).setValue(getContractInfoMap(contract).get(PE151FieldName.WEEK_HOURS.toString()));
			}
			if(StringUtils.isNotBlank(getContractInfoMap(contract).get(PE151FieldName.START_TIME.toString()))){
				getPdfFieldsMap().get(PE151FieldName.START_TIME.getValue()).setValue(getContractInfoMap(contract).get(PE151FieldName.START_TIME.toString()));
			}
			if(StringUtils.isNotBlank(getContractInfoMap(contract).get(PE151FieldName.END_TIME.toString()))){
				getPdfFieldsMap().get(PE151FieldName.END_TIME.getValue()).setValue(getContractInfoMap(contract).get(PE151FieldName.END_TIME.toString()));
			}
			
			/*
			 * Contract page 2
			 */
			
			if(StringUtils.isNotBlank(getContractInfoMap(contract).get(PE151FieldName.SALARY_AMOUNT.toString()))){
				getPdfFieldsMap().get(PE151FieldName.SALARY_AMOUNT.getValue()).setValue(getContractInfoMap(contract).get(PE151FieldName.SALARY_AMOUNT.toString()));
			} else {
				getPdfFieldsMap().get(PE151FieldName.SALARY_AMOUNT.getValue()).setValue(null);
			}
			getPdfFieldsMap().get(PE151FieldName.SALARY_PERIOD.getValue()).setValue("mensuales");
			if(StringUtils.isNotBlank(getContractInfoMap(contract).get(PE151FieldName.SALARY_CONCEPT.toString()))){
				getPdfFieldsMap().get(PE151FieldName.SALARY_CONCEPT.getValue()).setValue(getContractInfoMap(contract).get(PE151FieldName.SALARY_CONCEPT.toString()));
			} else {
				getPdfFieldsMap().get(PE151FieldName.SALARY_CONCEPT.getValue()).setValue(null);
			}
			if(StringUtils.isNotBlank(getContractInfoMap(contract).get(PE151FieldName.HOLIDAYS.toString()))){
				getPdfFieldsMap().get(PE151FieldName.HOLIDAYS.getValue()).setValue(getContractInfoMap(contract).get(PE151FieldName.HOLIDAYS.toString()));
			} else {
				getPdfFieldsMap().get(PE151FieldName.HOLIDAYS.getValue()).setValue(null);
			}
			if(params!=null && params.isReliefData()){
				getPdfFieldsMap().get(PE151FieldName.RELIEF_CONTRACT_YES.getValue()).setValue("true");
			} else {
				getPdfFieldsMap().get(PE151FieldName.RELIEF_CONTRACT_NO.getValue()).setValue("true");
			}
		
			// TODO: what must be done with this?
			getPdfFieldsMap().get(PE151FieldName.ART4_RDL_3_2012_BT_16_30_UNEMPLOYED.getValue()).setValue(null);
			getPdfFieldsMap().get(PE151FieldName.ART4_RDL_3_2012_BT_16_30_YOUNG.getValue()).setValue(null);
			getPdfFieldsMap().get(PE151FieldName.ART4_RDL_3_2012_BT_16_30_WOMAN.getValue()).setValue(null);
			getPdfFieldsMap().get(PE151FieldName.ART4_RDL_3_2012_GT_45_UNEMPLOYED.getValue()).setValue(null);
			getPdfFieldsMap().get(PE151FieldName.ART4_RDL_3_2012_GT_45.getValue()).setValue(null);
			getPdfFieldsMap().get(PE151FieldName.ART4_RDL_3_2012_GT_45_WOMAN.getValue()).setValue(null);
			getPdfFieldsMap().get(PE151FieldName.UNEMPLOYED_WITH_3_BENEFIT.getValue()).setValue(null);
			getPdfFieldsMap().get(PE151FieldName.FIRST_EMPLOYEE_LT_30.getValue()).setValue(null);
			
			if(contract.getAgreementLevelCategory()!=null && contract.getAgreementLevelCategory().getId()!=null){
				getPdfFieldsMap().get(PE151FieldName.AGREEMENT_COLLECTIVE.getValue()).setValue(contract.getAgreementLevelCategory().getLevel().getAgreement().getDescription());
			} else {
				getPdfFieldsMap().get(PE151FieldName.AGREEMENT_COLLECTIVE.getValue()).setValue(null);
			}
			if(StringUtils.isNotBlank(getContractInfoMap(contract).get(PE151FieldName.SEPE_MUNICIPALITY.toString()))){
				getPdfFieldsMap().get(PE151FieldName.SEPE_MUNICIPALITY.getValue()).setValue(getContractInfoMap(contract).get(PE151FieldName.SEPE_MUNICIPALITY.toString()));
			} else {
				getPdfFieldsMap().get(PE151FieldName.SEPE_MUNICIPALITY.getValue()).setValue(null);
			}
			getPdfFieldsMap().get(PE151FieldName.ADDITIONAL_CLAUSES.getValue()).setValue(null);
			
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
	public enum PE151FieldName implements IContractFieldName{
		/*
		 * Contract page 1
		 */
		TC2_100("tipocontrato_100",Boolean.FALSE),
		TC2_150("tipocontrato_150",Boolean.FALSE),
		LEGAL_REPRESENTATIVE_NAME("Texto65",Boolean.FALSE),
		LEGAL_REPRESENTATIVE_NIF("Texto66",Boolean.FALSE),
		LEGAL_REPRESENTATIVE_CHARGE("Texto67",Boolean.FALSE),
		ART4_RDL_3_2012_YES("Casilla de verificación78464",Boolean.FALSE),
		ART4_RDL_3_2012_NO("Casilla de verificación71016430",Boolean.FALSE),
		PROFESSION("profetraba",Boolean.FALSE),
		CATEGORY("catetraba",Boolean.FALSE),
		FUNCTION("funciontraba",Boolean.FALSE),
		WORKPLACE_FULL_ADDRESS("calletrab",Boolean.FALSE),
		CONTRACT_START_DATE("fechaini",Boolean.FALSE),
		WEEK_HOURS("horasjorna1",Boolean.TRUE),
		START_TIME("horainicio",Boolean.TRUE),
		END_TIME("horafin",Boolean.TRUE),
		
		/*
		 * Contract page 2
		 */
		SALARY_AMOUNT("retribu",Boolean.TRUE),
		SALARY_PERIOD("perioretri",Boolean.FALSE),
		SALARY_CONCEPT("concepsala",Boolean.TRUE),
		HOLIDAYS("vacaciones",Boolean.TRUE),
		RELIEF_CONTRACT_YES("Casilla de verificación7",Boolean.FALSE),
		RELIEF_CONTRACT_NO("Casilla de verificación8",Boolean.FALSE),
		ART4_RDL_3_2012_BT_16_30_UNEMPLOYED("Casilla de verificación11",Boolean.FALSE),
		ART4_RDL_3_2012_BT_16_30_YOUNG("Casilla de verificación9",Boolean.FALSE),
		ART4_RDL_3_2012_BT_16_30_WOMAN("Casilla de verificación10",Boolean.FALSE),
		ART4_RDL_3_2012_GT_45_UNEMPLOYED("Casilla de verificación13",Boolean.FALSE),
		ART4_RDL_3_2012_GT_45("Casilla de verificación12",Boolean.FALSE),
		ART4_RDL_3_2012_GT_45_WOMAN("Casilla de verificación14",Boolean.FALSE),
		UNEMPLOYED_WITH_3_BENEFIT("Casilla de verificación15",Boolean.FALSE),
		FIRST_EMPLOYEE_LT_30("Casilla de verificación16",Boolean.FALSE),
		AGREEMENT_COLLECTIVE("convcole",Boolean.FALSE),
		SEPE_MUNICIPALITY("oecomu",Boolean.TRUE),
		ADDITIONAL_CLAUSES("T25",Boolean.FALSE),
		
		// OVERRIDES FIELDS
		ENTERPRISE_COUNTRY1("Texto1pas1",Boolean.FALSE),
		ENTERPRISE_MUNICIPALITY1("Texto2mun1",Boolean.FALSE),
		WORKPLACE_COUNTRY1("Texto34",Boolean.FALSE),
		WORKPLACE_MUNICIPALITY1("Texto38",Boolean.FALSE),
		EMPLOYEE_COUNTRY1("Texto51",Boolean.FALSE),
		EMPLOYEE_ADDRESS_MUNICIPALITY1("Texto55",Boolean.FALSE),
		EMPLOYEE_ADDRESS_COUNTRY1("Texto61",Boolean.FALSE),
		;
		
		private String value;
		private boolean overridable;
		
		private PE151FieldName(String value, boolean overridable) {
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
	
	