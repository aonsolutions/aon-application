package com.esferalia.aon.file.payroll.contract.pdf.model;

import java.io.IOException;

import com.code.aon.common.ManagerBeanException;
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.payroll.enumeration.ContractCode;
import com.lowagie.text.pdf.PdfReader;



public class ModelPE179 extends AbstractContractModel {
	
	/*
	 * Contract page 1
	 */
	final static String PE179_TC2_410 = "tiempocompleto";
	final static String PE179_TC2_510 = "tiempoparcial";
//	final static String PE179_ = "nomreptra";
//	final static String PE179_ = "dnireptra";
//	final static String PE179_ = "calireptra";
//	final static String PE179_ = "sel_causinter1";
//	final static String PE179_ = "sel_causinter2";
//	final static String PE179_ = "sel_causinter3";
//	final static String PE179_ = "sel_causinter34";
//	final static String PE179_ = "sel_causinter5";
//	final static String PE179_ = "sel_causinter6";
	
	/*
	 * Contract page 2
	 */
//	profetraba
//	catetraba
//	calletrab
//	ubicact2
//	sel_jorn1 
//	horasjorna1
//	horainicio
//	horafin
//	sel_jorn2
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
//	activsust
//	duractcicli
//	activcicli
//	totaldura
//	fechaini
//	fechafin
//	peridoprue
//	retribu
//	perioretri1
//	perioretri
//	concepsala
//	concepsala1
//	vacaciones
//	nomtrasust
//	dnitrasust
//	oecomusus
//	fecharegsus
//	idcontsus
//	puestodesem
//	convcole
//	convcole2
//	munifirma
//	clausadici
//	diafirma
//	mesfirma
//	añofirma

	public final static String MODEL_NAME = "PE179";
	
	public ModelPE179(){
		super.modelName = MODEL_NAME;
	}
	
	@Override
	public void loadPdfFields(ContractCode code, Contract contract) throws UnsupportedContractModelException{
		// TODO
		try {
			PdfReader reader = new PdfReader(getContractModelUrl(modelName+".pdf"));
			
			readPdfFields(reader);
			
			if(code == ContractCode.C410){
				getPdfFieldsMap().get(PE179_TC2_410).setValue("true");
			} else if(code == ContractCode.C510){
				getPdfFieldsMap().get(PE179_TC2_510).setValue("true");
			} else {
				throw new UnsupportedContractModelException("El modelo de contrato seleccionado es incorrecto");
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
	
	