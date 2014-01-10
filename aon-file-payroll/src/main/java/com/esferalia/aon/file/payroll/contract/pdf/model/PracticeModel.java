package com.esferalia.aon.file.payroll.contract.pdf.model;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.ResourceBundle;

import org.apache.commons.lang.StringUtils;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.IResourceable;
import com.code.aon.geozone.GeoZone;
import com.code.aon.registry.RegistryAddress;
import com.esferalia.aon.file.payroll.contract.pdf.UnsupportedContractDocumentException;
import com.esferalia.aon.file.payroll.contract.pdf.model.AbstractContractModel.ModelOption;
import com.esferalia.aon.file.payroll.contrata.ContrataContratoParams;
import com.esferalia.aon.file.payroll.contrata.IContrataParams;
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.payroll.ContractInfo.ContractVariable;
import com.esferalia.aon.payroll.enumeration.ContractCode;
import com.lowagie.text.pdf.PdfReader;



public class PracticeModel extends AbstractContractModel {
	
	public final static String MODEL_NAME = "Practicas";
	
	public PracticeModel(){
		super.documentName = MODEL_NAME;
	}
	
	@Override
	public void loadPdfFields(ContractCode code, Contract contract, IContrataParams contrataParams) throws UnsupportedContractDocumentException{
		
		// TODO
		try {
			setReader(new PdfReader(getContractModelUrl(documentName+".pdf")));
			String range = "1-3";
			ModelOption modelOption = ModelOption.valueOf(getContractInfoMap(contract).get(ContractVariable.CONTRACT_MODEL_OPTION.getValue()));
			range += ","+modelOption.getPageNumber();
			getReader().selectPages(range);
			readPdfFields();
			
			ContrataContratoParams contrata = (ContrataContratoParams) contrataParams;
			
			super.loadPdfCommonFields(contract, contrataParams);
			
			
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
//	public enum PracticeModelOption implements IContractOption {
//		
//		/**
//		 * PRÁCTICAS ( ORDINARIO ). (pag. 4)
//		 */
//		OPT1(ContractCode.C420, ContractCode.C520),
//		/**
//		 * DE TRABAJADORES EN SITUACIÓN DE EXCLUSIÓN SOCIAL, VÍCTIMAS DE VIOLENCIA DE GÉNERO, DOMESTICA O VÍCTIMA DE TERRORISMO .(pag.5)
//		 */
//		OPT2(ContractCode.C450, ContractCode.C550),
//		/**
//		 * DE TRABAJADORES MAYORES DE 52 AÑOS BENEFICIARIOS DE LOS SUBSIDIOS POR DESEMPLEO (pag.6)
//		 */
//		OPT3(ContractCode.C420),
//		/**
//		 * DE PERSONAS CON DISCAPACIDAD EN CENTROS ESPECIALES DE EMPLEO. (pag.7)
//		 */
//		OPT4(ContractCode.C420, ContractCode.C520),
//		/**
//		 * DE TRABAJOS DE INTERES SOCIAL/FOMENTO DE EMPLEO AGRARIO. (pag.8)
//		 */
//		OPT5(ContractCode.C420, ContractCode.C520),
//		;
//		
//		private ContractCode[] codes;
//		
//		private PracticeModelOption(ContractCode... codes) {
//			this.codes = codes;
//		}
//		
//		@Override
//		public ContractCode[] getCodes() {
//			return codes;
//		}
//		
//		@Override
//		public String getPdfModel() {
//			return PracticeModel.MODEL_NAME;
//		}
//		
//		public Integer getOptionPage(){
//			return this.ordinal()+4;
//		}
//		
//		/** Message key prefix. */
//		private static final String MSG_KEY_PREFIX = "aon_enum_contract_model_practice_";
//		
//		@Override
//		public String getName(Locale locale) {
//		    ResourceBundle bundle = ResourceBundle.getBundle(BASE_NAME, locale); 
//			return bundle.getString(MSG_KEY_PREFIX + toString());
//		}
//
//	}
	
	public enum PracticeCommonFieldName implements IContractFieldName{
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
		
		private PracticeCommonFieldName(String value, boolean overridable) {
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
	
	