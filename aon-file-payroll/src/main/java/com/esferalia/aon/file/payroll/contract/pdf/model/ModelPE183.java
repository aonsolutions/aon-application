package com.esferalia.aon.file.payroll.contract.pdf.model;

import java.io.IOException;

import com.code.aon.common.ManagerBeanException;
import com.esferalia.aon.file.payroll.contract.pdf.UnsupportedContractDocumentException;
import com.esferalia.aon.file.payroll.contrata.IContrataParams;
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.payroll.enumeration.ContractCode;
import com.lowagie.text.pdf.PdfReader;



public class ModelPE183 extends AbstractContractModel {
	
	/*
	 * Contract page 1
	 */
	final static String PE183_TC2_BONI = "bonificado";
	final static String PE183_TC2_109 = "tipocontrato_109";
	final static String PE183_TC2_209 = "tipocontrato_209";
	final static String PE183_TC2_NO_BONI = "nobonificado";
	final static String PE183_TC2_189 = "tipocontrato_189";
	final static String PE183_TC2_289 = "tipocontrato_289";
	final static String PE183_LEGAL_REPRESENTATIVE_NAME = "nomreptra";
	final static String PE183_LEGAL_REPRESENTATIVE_NIF = "dnireptra";
	final static String PE183_LEGAL_REPRESENTATIVE_CHARGE = "calireptra";
	
	
//	nombsepe
//	fechaconver
//	sel_jorn1
//	sel_jorn2
//	fechacelecv
//	oecomconv
//	fecharegcv
//	idcontconv
//	Casilla de verificación1
//	tipocontrato
//	horasjorna1
//	horainicio
//	horafin
//	horasjorna2
//	tipojorntp1
//	tipojorntp2
//	tipojorntp3
//	tipojorntp4
//	sel_jorn21
//	sel_jorn22
//	sel_jorn23
//	horasjornadaTP
//	sel_horcom1
//	sel_horcom2
	
	/*
	 * Contract page 2
	 */
//	calletrab
//	Texto3
//	distraba2
//	distraba1
//	set_ctorel55
//	set_ctorel555
	final static String PE183_START_DATE = "fechaini";
	final static String PE183_SALARY_AMOUNT = "retribu";
	final static String PE183_SALARY_PERIOD = "perioretri";
	final static String PE183_SALARY_CONCEPT = "concepsala";
	final static String PE183_HOLIDAYS = "vacaciones";
//	set_ctorel1
//	set_ctorel2
//	Casilla de verificación584
//	Casilla de verificación596
//	casilla 789541
//	casilla 84461
//	casilla 85469
	final static String PE183_AGREEMENT_COLLECTIVE = "convcole";
//	munifirma2
	final static String PE183_ADDITIONAL_CLAUSES = "clausadici";
	
	
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
				getPdfFieldsMap().get(PE183_TC2_BONI).setValue("true");
				getPdfFieldsMap().get(PE183_TC2_109).setValue("true");
			} else if(code == ContractCode.C209){
				getPdfFieldsMap().get(PE183_TC2_BONI).setValue("true");
				getPdfFieldsMap().get(PE183_TC2_209).setValue("true");
			} else if(code == ContractCode.C189){
				getPdfFieldsMap().get(PE183_TC2_NO_BONI).setValue("true");
				getPdfFieldsMap().get(PE183_TC2_189).setValue("true");
			} else if(code == ContractCode.C289){
				getPdfFieldsMap().get(PE183_TC2_NO_BONI).setValue("true");
				getPdfFieldsMap().get(PE183_TC2_289).setValue("true");
			} else {
				throw new UnsupportedContractDocumentException("El modelo de contrato seleccionado es incorrecto");
			}
			
			super.loadPdfCommonFields(contract, contrataParams);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ManagerBeanException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
	
	