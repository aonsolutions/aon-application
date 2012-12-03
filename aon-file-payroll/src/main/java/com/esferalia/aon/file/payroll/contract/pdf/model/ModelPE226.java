package com.esferalia.aon.file.payroll.contract.pdf.model;

import java.io.IOException;

import com.code.aon.common.ManagerBeanException;
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.payroll.enumeration.ContractCode;
import com.lowagie.text.pdf.PdfReader;



public class ModelPE226 extends AbstractContractModel {
	
	/*
	 * Contract page 1
	 */
	final static String PE226_QUOTE_BONUS = "si_bonif_cuota";
	final static String PE226_QUOTE_NO_BONUS = "no_bonif_cuota";
//	nomreptra
//	dnireptra
//	calireptra
//	verificacion123
//	verificacion1234
//	entre16y30
//	trabdiscap
//	alumproy
//	profetraba
//	catetraba
//	cno1
//	cno2
//	cno3
//	cno4

	/*
	 * Contract page 2
	 */
//	año1
//	año2y3
//	trabefec
//	porcforma
//	convcole
//	jornhoraefec
//	totaldura
//	fechaini
//	fechafin
//	periodoprue
//	retribu
//	perioretri
//	vacaciones
//	porc_75_250
//	porc_100_249
//	convcole2
//	munifirma
//	clausadici
//	diafirma
//	mesfirma
//	añofirma
	
	public final static String MODEL_NAME = "PE226";
	
	public ModelPE226(){
		super.modelName = MODEL_NAME;
	}
	
	@Override
	public void loadPdfFields(ContractCode code, Contract contract) throws UnsupportedContractModelException{
		// TODO
		try {
			PdfReader reader = new PdfReader(getContractModelUrl(modelName+".pdf"));
			
			readPdfFields(reader);
			
			if(code == ContractCode.C421){
				if( isQuoteBonus(contract) ){
					getPdfFieldsMap().get(PE226_QUOTE_BONUS).setValue("true");
				} else {
					getPdfFieldsMap().get(PE226_QUOTE_NO_BONUS).setValue("true");
				}
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

	private boolean isQuoteBonus(Contract contract) {
		// TODO 
		return true;
	}
	
}
	
	