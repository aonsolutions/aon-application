package com.esferalia.aon.sepe.api.contract.model;

import com.esferalia.aon.sepe.api.contrata.transformaciones.DATOSEMPRESATYPE;
import com.esferalia.aon.sepe.api.contrata.transformaciones.DATOSCONTRATOTYPE;
import com.esferalia.aon.sepe.api.contrata.transformaciones.DATOSGENERALESTRANSFORMACIONTYPE;
import com.esferalia.aon.sepe.api.contrata.transformaciones.DATOSADICIONALESTRANSFORMACIONTYPE;
import com.esferalia.aon.sepe.api.contrata.transformaciones.DATOSANEXOCONTRATORELEVOTYPE;
import com.esferalia.aon.sepe.api.contrata.transformaciones.DATOSCOMUNICACOPIABASICATYPE;
import com.esferalia.aon.sepe.api.contrata.transformaciones.DATOSUSOLIBREEMPRESATYPE;

public interface ITransformacionType {

	public DATOSEMPRESATYPE getDATOSEMPRESA();
	public DATOSCONTRATOTYPE getDATOSCONTRATO();
	public DATOSGENERALESTRANSFORMACIONTYPE getDATOSGENERALESTRANSFORMACION();
	public DATOSADICIONALESTRANSFORMACIONTYPE getDATOSADICIONALESTRANSFORMACION();
	public DATOSANEXOCONTRATORELEVOTYPE getDATOSANEXOCONTRATORELEVO();
	public DATOSCOMUNICACOPIABASICATYPE getDATOSCOMUNICACOPIABASICA();
	public DATOSUSOLIBREEMPRESATYPE getDATOSUSOLIBREEMPRESA();

	public void setDATOSEMPRESA(DATOSEMPRESATYPE value);
	public void setDATOSCONTRATO(DATOSCONTRATOTYPE value);
	public void setDATOSGENERALESTRANSFORMACION(
			DATOSGENERALESTRANSFORMACIONTYPE value);
	public void setDATOSADICIONALESTRANSFORMACION(
			DATOSADICIONALESTRANSFORMACIONTYPE value);
	public void setDATOSANEXOCONTRATORELEVO(DATOSANEXOCONTRATORELEVOTYPE value);
	public void setDATOSCOMUNICACOPIABASICA(DATOSCOMUNICACOPIABASICATYPE value);
	public void setDATOSUSOLIBREEMPRESA(DATOSUSOLIBREEMPRESATYPE value);

}
