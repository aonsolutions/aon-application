package com.esferalia.aon.file.payroll.contract.pdf.model;

import java.io.IOException;

import com.code.aon.common.ManagerBeanException;
import com.esferalia.aon.file.payroll.contract.pdf.UnsupportedContractDocumentException;
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.payroll.enumeration.ContractCode;
import com.lowagie.text.pdf.PdfReader;



public class ModelPE177 extends AbstractContractModel {
	
	/*
	 * Contract page 1
	 */
	final static String PE177_FULL_TIME = "sel_jorn1";
	final static String PE177_PARTIAL_TIME = "sel_jorn2";
	final static String PE177_TC2_401 = "tipocontrato_401";
	final static String PE177_TC2_402 = "tipocontrato_402";
	final static String PE177_TC2_410 = "tipocontrato_410";
	final static String PE177_TC2_501 = "tipocontrato_501";
	final static String PE177_TC2_502 = "tipocontrato_502";
	final static String PE177_TC2_510 = "tipocontrato_510";
	final static String PE177_TC2_540 = "tipocontrato_540";
//	nomreptra
//	dnireptra
//	calireptra
//	profetraba
//	catetraba
//	calletrab
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
//	horatraba1
//	horatraba2
//	fechaini
//	fechafin
//	peridoprue
//	in_ccolec
	
	/*
	 * Contract page 2
	 */
//	retribu
//	perioretri
//	concepsala
//	vacaciones
//	in_causa1
//	causaobra
//	in_causa2
//	causaobra1
//	causaobra2
//	in_causa3
//	nomtrasus
//	in_causa31
//	in_causa32
//	in_causa33
//	in_causa34
//	in_causa35
//	in_causa36
//	violengen
//	violengenA
//	violengenB
//	puestotra
//	reducjorn
//	reducsalario
//	convcole
//	oecomu
//	clausadici
//	munifirma
//	diafirma
//	mesfirma
//	añofirma
	
	public final static String MODEL_NAME = "PE177";
	
	public ModelPE177(){
		super.documentName = MODEL_NAME;
	}
	
	@Override
	public void loadPdfFields(ContractCode code, Contract contract) throws UnsupportedContractDocumentException{
		// TODO
		try {
			PdfReader reader = new PdfReader(getContractModelUrl(documentName+".pdf"));
			
			readPdfFields(reader);
			
			if(code == ContractCode.C401){
				getPdfFieldsMap().get(PE177_FULL_TIME).setValue("true");
				getPdfFieldsMap().get(PE177_TC2_401).setValue("true");
			} else if(code == ContractCode.C402){
				getPdfFieldsMap().get(PE177_FULL_TIME).setValue("true");
				getPdfFieldsMap().get(PE177_TC2_402).setValue("true");
			} else if(code == ContractCode.C410){
				getPdfFieldsMap().get(PE177_FULL_TIME).setValue("true");
				getPdfFieldsMap().get(PE177_TC2_410).setValue("true");
			} else if(code == ContractCode.C501){
				getPdfFieldsMap().get(PE177_PARTIAL_TIME).setValue("true");
				getPdfFieldsMap().get(PE177_TC2_501).setValue("true");
			} else if(code == ContractCode.C502){
				getPdfFieldsMap().get(PE177_PARTIAL_TIME).setValue("true");
				getPdfFieldsMap().get(PE177_TC2_502).setValue("true");
			} else if(code == ContractCode.C510){
				getPdfFieldsMap().get(PE177_PARTIAL_TIME).setValue("true");
				getPdfFieldsMap().get(PE177_TC2_510).setValue("true");
			} else if(code == ContractCode.C540){
				getPdfFieldsMap().get(PE177_PARTIAL_TIME).setValue("true");
				getPdfFieldsMap().get(PE177_TC2_540).setValue("true");
			} else {
				throw new UnsupportedContractDocumentException("El modelo de contrato seleccionado es incorrecto");
			}
			
			super.loadPdfCommonFields(contract);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ManagerBeanException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
	
	