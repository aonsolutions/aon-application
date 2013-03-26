package com.esferalia.aon.file.payroll.contract.model;

import com.esferalia.aon.file.payroll.contract.generated.contratos.DATOSCOMUNICACOPIABASICATYPE;
import com.esferalia.aon.file.payroll.contract.generated.contratos.DATOSEMPRESATYPE;
import com.esferalia.aon.file.payroll.contract.generated.contratos.DATOSETTTYPE;
import com.esferalia.aon.file.payroll.contract.generated.contratos.DATOSGENERALESCONTRATOTYPE;
import com.esferalia.aon.file.payroll.contract.generated.contratos.DATOSTRABAJADORTYPE;
import com.esferalia.aon.file.payroll.contract.generated.contratos.DATOSUSOLIBREEMPRESATYPE;

public interface IContratoType {

	public DATOSEMPRESATYPE getDATOSEMPRESA();
	public DATOSTRABAJADORTYPE getDATOSTRABAJADOR();
	public DATOSGENERALESCONTRATOTYPE getDATOSGENERALESCONTRATO();
	public DATOSETTTYPE getDATOSETT();
	public DATOSCOMUNICACOPIABASICATYPE getDATOSCOMUNICACOPIABASICA();
	public DATOSUSOLIBREEMPRESATYPE getDATOSUSOLIBREEMPRESA();
	
	public void setDATOSEMPRESA(DATOSEMPRESATYPE value);
	public void setDATOSTRABAJADOR(DATOSTRABAJADORTYPE value);
	public void setDATOSGENERALESCONTRATO(DATOSGENERALESCONTRATOTYPE value); 
	public void setDATOSETT(DATOSETTTYPE value); 
	public void setDATOSCOMUNICACOPIABASICA(DATOSCOMUNICACOPIABASICATYPE value); 
	public void setDATOSUSOLIBREEMPRESA(DATOSUSOLIBREEMPRESATYPE value); 
   
}
