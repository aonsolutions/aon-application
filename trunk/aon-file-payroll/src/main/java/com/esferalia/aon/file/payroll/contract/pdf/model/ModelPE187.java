package com.esferalia.aon.file.payroll.contract.pdf.model;

import java.io.IOException;

import com.code.aon.common.ManagerBeanException;
import com.esferalia.aon.file.payroll.contract.pdf.UnsupportedContractDocumentException;
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.payroll.enumeration.ContractCode;
import com.lowagie.text.pdf.PdfReader;



public class ModelPE187 extends AbstractContractModel {
	
	/*
	 * Contract page 1
	 */
	final static String PE187_TC2_430_BONI = "tiempocompletoboni";
	final static String PE187_TC2_530_BONI = "tiempoparcialboni";
	final static String PE187_TC2_430 = "tiempocompleto";
	final static String PE187_TC2_530 = "tiempoparcial";
//	final static String PE187_ = "nomreptra";
//	final static String PE187_ = "dnireptra";
//	final static String PE187_ = "calireptra";
//	final static String PE187_ = "discapa_si";
//	final static String PE187_ = "organcerti2";
//	final static String PE187_ = "acogebene_si";
	
	/*
	 * Contract page 2
	 */
//	final static String PE187_ = "profetraba";
//	final static String PE187_ = "catetraba";
//	final static String PE187_ = "calletrab";
//	final static String PE187_ = "calleloca1";
//	final static String PE187_ = "sel_tpjorn1";
//	final static String PE187_ = "horasjorna1";
//	final static String PE187_ = "fechainicio";
//	final static String PE187_ = "fechafin";
//	final static String PE187_ = "sel_tpjorn2";
//	final static String PE187_ = "horasjorna2";
//	final static String PE187_ = "sel_tpjorn_dia";
//	final static String PE187_ = "sel_tpjorn_sem";
//	final static String PE187_ = "sel_tpjorn_mes";
//	final static String PE187_ = "sel_tpjorn_año";
//	final static String PE187_ = "sel_tpjorn21";
//	final static String PE187_ = "sel_tpjorn22";
//	final static String PE187_ = "sel_tpjorn23";
//	final static String PE187_ = "horatraba1";
//	final static String PE187_ = "horatraba2";
//	final static String PE187_ = "totaldura";
//	final static String PE187_ = "fechaini";
//	final static String PE187_ = "fechafin";
//	final static String PE187_ = "peridoprue";
//	final static String PE187_ = "retribu";
//	final static String PE187_ = "perioretri";
//	final static String PE187_ = "concepsala";
//	final static String PE187_ = "vacaciones";
//	final static String PE187_ = "c1";
//	final static String PE187_ = "convcole";
//	final static String PE187_ = "oecomu";
//	final static String PE187_ = "eo2";
//	final static String PE187_ = "clausadici";
//	final static String PE187_ = "munifirma";
//	final static String PE187_ = "diafirma";
//	final static String PE187_ = "mesfirma";
//	final static String PE187_ = "añofirma";
	
	public final static String MODEL_NAME = "PE187";
	
	public ModelPE187(){
		super.documentName = MODEL_NAME;
	}
	
	@Override
	public void loadPdfFields(ContractCode code, Contract contract) throws UnsupportedContractDocumentException{
		// TODO
		try {
			PdfReader reader = new PdfReader(getContractModelUrl(documentName+".pdf"));
			readPdfFields(reader);
			
			boolean bonif = false;
			if(code == ContractCode.C430){
				if(bonif){
					getPdfFieldsMap().get(PE187_TC2_430_BONI).setValue("true");
				} else {
					getPdfFieldsMap().get(PE187_TC2_430).setValue("true");
				}
			} else if(code == ContractCode.C530){
				if(bonif){
					getPdfFieldsMap().get(PE187_TC2_530_BONI).setValue("true");
				} else {
					getPdfFieldsMap().get(PE187_TC2_530).setValue("true");
				}
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
	
	