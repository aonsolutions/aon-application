package com.esferalia.aon.file.payroll.contract.pdf.model;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ResourceBundle;

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
	
	
	/*
	 * Contract page 1
	 */
	final static String PE151_TC2_100 = "tipocontrato_100";
	final static String PE151_TC2_150 = "tipocontrato_150";
	final static String PE151_LEGAL_REPRESENTATIVE_NAME = "Texto65";
	final static String PE151_LEGAL_REPRESENTATIVE_NIF = "Texto66";
	final static String PE151_LEGAL_REPRESENTATIVE_CHARGE = "Texto67";
	final static String PE151_ART4_RDL_3_2012_YES = "Casilla de verificación78464";
	final static String PE151_ART4_RDL_3_2012_NO = "Casilla de verificación71016430";
	final static String PE151_PROFESSION = "profetraba";
	final static String PE151_CATEGORY = "catetraba";
	final static String PE151_FUNCTION = "funciontraba";
	final static String PE151_WORKPLACE_FULL_ADDRESS = "calletrab";
	final static String PE151_CONTRACT_START_DATE = "fechaini";
	final static String PE151_JOURNAL_HOURS = "horasjorna1";
	final static String PE151_START_TIME = "horainicio";
	final static String PE151_END_TIME = "horafin";
	
	/*
	 * Contract page 2
	 */
	final static String PE151_SALARY = "retribu";
	final static String PE151_SALARY_PERIOD = "perioretri";
	final static String PE151_SALARY_CONCEPT = "concepsala";
	final static String PE151_HOLIDAYS = "vacaciones";
	final static String PE151_RELIEF_YES = "Casilla de verificación7";
	final static String PE151_RELIEF_NO = "Casilla de verificación8";
	final static String PE151_ART4_RDL_3_2012_BT_16_30_UNEMPLOYED = "Casilla de verificación11";
	final static String PE151_ART4_RDL_3_2012_BT_16_30_YOUNG = "Casilla de verificación9";
	final static String PE151_ART4_RDL_3_2012_BT_16_30_WOMAN = "Casilla de verificación10";
	final static String PE151_ART4_RDL_3_2012_GT_45_UNEMPLOYED = "Casilla de verificación13";
	final static String PE151_ART4_RDL_3_2012_GT_45 = "Casilla de verificación12";
	final static String PE151_ART4_RDL_3_2012_GT_45_WOMAN = "Casilla de verificación14";
	final static String PE151_UNEMPLOYED_WITH_3_BENEFIT = "Casilla de verificación15";
	final static String PE151_FIRST_EMPLOYEE_LT_30 = "Casilla de verificación16";
	final static String PE151_AGREEMENT_COLLECTIVE = "convcole";
	final static String PE151_SEPE_MUNICIPALITY = "oecomu";
	final static String PE151_ADDITIONAL_CLAUSES = "T25";
	
	// OVERRIDES FIELDS
	final String ENTERPRISE_COUNTRY1 = "Texto1pas1";
	final String ENTERPRISE_MUNICIPALITY1 = "Texto2mun1";
	final String WORKPLACE_COUNTRY1 = "Texto34";
	final String WORKPLACE_MUNICIPALITY1 = "Texto38";
	final String EMPLOYEE_COUNTRY1 = "Texto51";
	final String EMPLOYEE_ADDRESS_MUNICIPALITY1 = "Texto55";
	final String EMPLOYEE_ADDRESS_COUNTRY1 = "Texto61";
	
	public final static String MODEL_NAME = "PE151";
	
	public ModelPE151(){
		super.documentName = MODEL_NAME;
	}
	
	@Override
	public void loadPdfFields(ContractCode code, Contract contract, IContrataParams contrataParams) throws UnsupportedContractDocumentException{
		ContrataContratoParams params = (ContrataContratoParams) contrataParams;
		
		// TODO
		
		try {
			PdfReader reader = new PdfReader(getContractModelUrl(documentName+".pdf"));
			
			readPdfFields(reader);
			
			if(code == ContractCode.C100){
				getPdfFieldsMap().get(PE151_TC2_100).setValue("true");
			} else if(code == ContractCode.C150){
				getPdfFieldsMap().get(PE151_TC2_150).setValue("true");
			} else {
				throw new UnsupportedContractDocumentException("El modelo de contrato seleccionado es incorrecto");
			}
			
			super.loadPdfCommonFields(contract, contrataParams);
			
			/*
			 * FIXME: FIELDS OVERRIDES
			 * same fileds named with different labels
			 * must normalize pdf files of contract models 
			 */
			try {	
				getPdfFieldsMap().get(ENTERPRISE_COUNTRY1).setValue(contract.getWorkPlace().getEnterprise().getRegistry().getNationality().getName(getLocale()));
			} catch (StringIndexOutOfBoundsException aie) {
				// do nothing
			} catch (NullPointerException npe) {
				// do nothing
			}
			
			try {
				RegistryAddress address = contract.getWorkPlace().getEnterprise().getRegistry().getDefaultAddress();
				ResourceBundle bundle = ResourceBundle.getBundle("com.esferalia.aon.payroll.i18n.municipalities");
				getPdfFieldsMap().get(ENTERPRISE_MUNICIPALITY1).setValue(bundle.getString(address.getMunicipalityCode()));
			} catch (NullPointerException npe) {
				// do nothing
			}
			
			try {
				GeoZone country = obtainCountry(contract.getWorkPlace().getAddress().getGeozone());
				getPdfFieldsMap().get(WORKPLACE_COUNTRY1).setValue(country.getName());
			} catch (StringIndexOutOfBoundsException aie) {
				// do nothing
			} catch (NullPointerException npe) {
				// do nothing
			}
			try {
				RegistryAddress address = contract.getWorkPlace().getAddress();
				ResourceBundle bundle = ResourceBundle.getBundle("com.esferalia.aon.payroll.i18n.municipalities");
				getPdfFieldsMap().get(WORKPLACE_MUNICIPALITY1).setValue(bundle.getString(address.getMunicipalityCode()));
			} catch (NullPointerException npe) {
				// do nothing
			}
			try {
				getPdfFieldsMap().get(EMPLOYEE_COUNTRY1).setValue(String.valueOf(contract.getPerson().getRegistry().getNationality().getName(getLocale())));
			} catch (NullPointerException npe) {
				// do nothing
			}
			try {
				RegistryAddress address = contract.getPerson().getRegistry().getDefaultAddress();
				ResourceBundle bundle = ResourceBundle.getBundle("com.esferalia.aon.payroll.i18n.municipalities");
				getPdfFieldsMap().get(EMPLOYEE_ADDRESS_MUNICIPALITY1).setValue(bundle.getString(address.getMunicipalityCode()));
			} catch (NullPointerException npe) {
				// do nothing
			}
			try {
				GeoZone country = obtainCountry(contract.getPerson().getRegistry().getDefaultAddress().getGeozone());
				getPdfFieldsMap().get(EMPLOYEE_ADDRESS_COUNTRY1).setValue(country.getName());
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
			getPdfFieldsMap().get(PE151_LEGAL_REPRESENTATIVE_NAME).setValue(null);
			getPdfFieldsMap().get(PE151_LEGAL_REPRESENTATIVE_NIF).setValue(null);
			getPdfFieldsMap().get(PE151_LEGAL_REPRESENTATIVE_CHARGE).setValue(null);
			getPdfFieldsMap().get(PE151_ART4_RDL_3_2012_YES).setValue(null);
			getPdfFieldsMap().get(PE151_ART4_RDL_3_2012_NO).setValue(null);
			getPdfFieldsMap().get(PE151_PROFESSION).setValue(null);
			getPdfFieldsMap().get(PE151_CATEGORY).setValue(null);
			getPdfFieldsMap().get(PE151_FUNCTION).setValue(null);
			getPdfFieldsMap().get(PE151_WORKPLACE_FULL_ADDRESS).setValue(contract.getWorkPlace().getAddress().getFullAddress() + ", " + contract.getWorkPlace().getAddress().getCity());
			dateFormatter.applyPattern("dd/MM/yyyy");
			getPdfFieldsMap().get(PE151_CONTRACT_START_DATE).setValue(dateFormatter.format(contract.getStartDate()));
			getPdfFieldsMap().get(PE151_JOURNAL_HOURS).setValue(null);
			getPdfFieldsMap().get(PE151_START_TIME).setValue(null);
			getPdfFieldsMap().get(PE151_END_TIME).setValue(null);
			
			/*
			 * Contract page 2
			 */
			getPdfFieldsMap().get(PE151_SALARY).setValue("Según convenio");
			getPdfFieldsMap().get(PE151_SALARY_PERIOD).setValue(null);
			getPdfFieldsMap().get(PE151_SALARY_CONCEPT).setValue(null);
			getPdfFieldsMap().get(PE151_HOLIDAYS).setValue(null);
			if(params!=null && params.isReliefData()){
				getPdfFieldsMap().get(PE151_RELIEF_YES).setValue("true");
			} else {
				getPdfFieldsMap().get(PE151_RELIEF_NO).setValue("true");
			}
			getPdfFieldsMap().get(PE151_ART4_RDL_3_2012_BT_16_30_UNEMPLOYED).setValue(null);
			getPdfFieldsMap().get(PE151_ART4_RDL_3_2012_BT_16_30_YOUNG).setValue(null);
			getPdfFieldsMap().get(PE151_ART4_RDL_3_2012_BT_16_30_WOMAN).setValue(null);
			getPdfFieldsMap().get(PE151_ART4_RDL_3_2012_GT_45_UNEMPLOYED).setValue(null);
			getPdfFieldsMap().get(PE151_ART4_RDL_3_2012_GT_45).setValue(null);
			getPdfFieldsMap().get(PE151_ART4_RDL_3_2012_GT_45_WOMAN).setValue(null);
			getPdfFieldsMap().get(PE151_UNEMPLOYED_WITH_3_BENEFIT).setValue(null);
			getPdfFieldsMap().get(PE151_FIRST_EMPLOYEE_LT_30).setValue(null);
			getPdfFieldsMap().get(PE151_AGREEMENT_COLLECTIVE).setValue(null);
			getPdfFieldsMap().get(PE151_SEPE_MUNICIPALITY).setValue(null);
			getPdfFieldsMap().get(PE151_ADDITIONAL_CLAUSES).setValue(null);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ManagerBeanException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
}
	
	