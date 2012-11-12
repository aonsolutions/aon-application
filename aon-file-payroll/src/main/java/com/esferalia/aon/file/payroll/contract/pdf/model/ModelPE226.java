package com.esferalia.aon.file.payroll.contract.pdf.model;

import com.esferalia.aon.file.payroll.contract.model.DATOSEMPRESATYPE;
import com.esferalia.aon.file.payroll.contract.model.DATOSGENERALESCONTRATOTYPE;
import com.esferalia.aon.file.payroll.contract.model.DATOSTRABAJADORTYPE;
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.payroll.enumeration.ContractCode;



public class ModelPE226 extends AbstractContractModel {
	
	/*
	 * Contract page 1
	 */
	final static String PE151_TC2_100 = "tipocontrato_100";
	final static String PE151_TC2_150 = "tipocontrato_150";
//	final static String PE151_ = "Texto65";
//	final static String PE151_ = "Texto66";
//	final static String PE151_ = "Texto67";
//	final static String PE151_ = "Casilla de verificación78464";
//	final static String PE151_ = "Casilla de verificación71016430";
//	final static String PE151_ = "profetraba";
//	final static String PE151_ = "catetraba";
//	final static String PE151_ = "funciontraba";
//	final static String PE151_ = "calletrab";
//	final static String PE151_ = "fechaini";
//	final static String PE151_ = "horasjorna1";
//	final static String PE151_ = "horainicio";
//	final static String PE151_ = "horafin";
	
	/*
	 * Contract page 2
	 */
	final static String PE151_SALARY = "retribu";
//	final static String PE151_ = "perioretri";
//	final static String PE151_ = "concepsala";
//	final static String PE151_ = "vacaciones";
//	final static String PE151_ = "Casilla de verificación7";
//	final static String PE151_ = "Casilla de verificación8";
//	final static String PE151_ = "Casilla de verificación11";
//	final static String PE151_ = "Casilla de verificación9";
//	final static String PE151_ = "Casilla de verificación10";
//	final static String PE151_ = "Casilla de verificación13";
//	final static String PE151_ = "Casilla de verificación12";
//	final static String PE151_ = "Casilla de verificación14";
//	final static String PE151_ = "Casilla de verificación15";
//	final static String PE151_ = "Casilla de verificación16";
//	final static String PE151_ = "convcole";
//	final static String PE151_ = "oecomu";
//	final static String PE151_ = "T25";
//	final static String PE151_ = "munifirma";
//	final static String PE151_ = "diafirma";
//	final static String PE151_ = "mesfirma";
//	final static String PE151_ = "añofirma";

	public final static String MODEL_NAME = "PE226";
	
	public ModelPE226(){
		super.modelName = MODEL_NAME;
	}
	
	@Override
	public void loadPdfFields(ContractCode code, Contract contract) throws UnsupportedContractModelException{
		// TODO
		
	}
	
}
	
	