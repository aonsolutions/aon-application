package com.esferalia.aon.file.payroll.contract.pdf.model;

import java.io.IOException;

import com.code.aon.common.ManagerBeanException;
import com.esferalia.aon.file.payroll.contract.pdf.UnsupportedContractDocumentException;
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.payroll.enumeration.ContractCode;
import com.lowagie.text.pdf.PdfReader;



public class ModelPE176 extends AbstractContractModel {
	
	/*
	 * Contract page 1
	 */
	final static String PE176_TC2_420 = "tiempocompleto";
	final static String PE176_TC2_520 = "tiempoparcial";
//	nomreptra
//	dnireptra
//	calireptra
//	titulprac
//	fechatit
//	ubicact
//	Texto2
//	tiempoc
//	profetraba
//	catetraba
//	ubitrab
//	calletrab
	
	/*
	 * Contract page 2
	 */
//	sel_jorn1
//	horasjorna1
//	horainicio
//	sel_jorn2
//	horasjorna2
//	tipojorntp1
//	tipojorntp2
//	tipojorntp3
//	tipojorntp4
//	Casilla de verificación161
//	Casilla de verificación171
//	Casilla de verificación181
//	horatraba1
//	horatraba2
//	totaldura
//	fechaini
//	fechafin
//	peridoprue
//	retribu
//	perioretri
//	concepsala
//	vacaciones
//	convcole
//	oecomu
//	clausadici
//	munifirma
//	diafirma
//	mesfirma
//	añofirma

	public final static String MODEL_NAME = "PE176";
	
	public ModelPE176(){
		super.documentName = MODEL_NAME;
	}
	
	@Override
	public void loadPdfFields(ContractCode code, Contract contract) throws UnsupportedContractDocumentException{
		// TODO
		try {
			PdfReader reader = new PdfReader(getContractModelUrl(documentName+".pdf"));
			
			readPdfFields(reader);
			
			if(code == ContractCode.C420){
				getPdfFieldsMap().get(PE176_TC2_420).setValue("true");
			} else if(code == ContractCode.C520){
				getPdfFieldsMap().get(PE176_TC2_520).setValue("true");
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
	
	