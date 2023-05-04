package com.esferalia.aon.gwt.payroll.jooq;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang.time.DateFormatUtils;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.util.CommonUtil;
import com.esferalia.aon.gwt.payroll.shared.ContractSpecificData;
import com.esferalia.aon.gwt.payroll.shared.EmployeeContractInfo;
import com.esferalia.aon.payroll.enumeration.ContractCode;
import com.esferalia.aon.sepe.api.contract.model.IContratoType;
import com.esferalia.aon.sepe.api.contrata.contratos.CIFNIFTYPE;
import com.esferalia.aon.sepe.api.contrata.contratos.CONTRATO100TYPE;
import com.esferalia.aon.sepe.api.contrata.contratos.CONTRATO130TYPE;
import com.esferalia.aon.sepe.api.contrata.contratos.CONTRATO150TYPE;
import com.esferalia.aon.sepe.api.contrata.contratos.CONTRATO200TYPE;
import com.esferalia.aon.sepe.api.contrata.contratos.CONTRATO230TYPE;
import com.esferalia.aon.sepe.api.contrata.contratos.CONTRATO250TYPE;
import com.esferalia.aon.sepe.api.contrata.contratos.CONTRATO300TYPE;
import com.esferalia.aon.sepe.api.contrata.contratos.CONTRATO330TYPE;
import com.esferalia.aon.sepe.api.contrata.contratos.CONTRATO350TYPE;
import com.esferalia.aon.sepe.api.contrata.contratos.CONTRATO401TYPE;
import com.esferalia.aon.sepe.api.contrata.contratos.CONTRATO402TYPE;
import com.esferalia.aon.sepe.api.contrata.contratos.CONTRATO403TYPE;
import com.esferalia.aon.sepe.api.contrata.contratos.CONTRATO410TYPE;
import com.esferalia.aon.sepe.api.contrata.contratos.CONTRATO420TYPE;
import com.esferalia.aon.sepe.api.contrata.contratos.CONTRATO421TYPE;
import com.esferalia.aon.sepe.api.contrata.contratos.CONTRATO430TYPE;
import com.esferalia.aon.sepe.api.contrata.contratos.CONTRATO441TYPE;
import com.esferalia.aon.sepe.api.contrata.contratos.CONTRATO450TYPE;
import com.esferalia.aon.sepe.api.contrata.contratos.CONTRATO452TYPE;
import com.esferalia.aon.sepe.api.contrata.contratos.CONTRATO501TYPE;
import com.esferalia.aon.sepe.api.contrata.contratos.CONTRATO502TYPE;
import com.esferalia.aon.sepe.api.contrata.contratos.CONTRATO503TYPE;
import com.esferalia.aon.sepe.api.contrata.contratos.CONTRATO510TYPE;
import com.esferalia.aon.sepe.api.contrata.contratos.CONTRATO520TYPE;
import com.esferalia.aon.sepe.api.contrata.contratos.CONTRATO530TYPE;
import com.esferalia.aon.sepe.api.contrata.contratos.CONTRATO540TYPE;
import com.esferalia.aon.sepe.api.contrata.contratos.CONTRATO541TYPE;
import com.esferalia.aon.sepe.api.contrata.contratos.CONTRATO550TYPE;
import com.esferalia.aon.sepe.api.contrata.contratos.CONTRATO552TYPE;
import com.esferalia.aon.sepe.api.contrata.contratos.CONTRATO970TYPE;
import com.esferalia.aon.sepe.api.contrata.contratos.CONTRATO980TYPE;
import com.esferalia.aon.sepe.api.contrata.contratos.CONTRATO990TYPE;
import com.esferalia.aon.sepe.api.contrata.contratos.DATOSANEXOCONTRATORELEVOTYPE;
import com.esferalia.aon.sepe.api.contrata.contratos.DATOSBONIFICACIONTYPE;
import com.esferalia.aon.sepe.api.contrata.contratos.DATOSCOMUNICACOPIABASICATYPE;
import com.esferalia.aon.sepe.api.contrata.contratos.DATOSCONTRATOEXTRANJEROTYPE;
import com.esferalia.aon.sepe.api.contrata.contratos.DATOSCONTRATOINSERCIONTYPE;
import com.esferalia.aon.sepe.api.contrata.contratos.DATOSCONTRATOINTERINIDADTYPE;
import com.esferalia.aon.sepe.api.contrata.contratos.DATOSCONTRATOINVESTIGACIONTYPE;
import com.esferalia.aon.sepe.api.contrata.contratos.DATOSCONTRATOPRACTICASTYPE;
import com.esferalia.aon.sepe.api.contrata.contratos.DATOSCONTRATOTIEMPOPARCIALTYPE;
import com.esferalia.aon.sepe.api.contrata.contratos.DATOSCOPIABASICATYPE;
import com.esferalia.aon.sepe.api.contrata.contratos.DATOSEMPRESAINSERCIONTYPE;
import com.esferalia.aon.sepe.api.contrata.contratos.DATOSEMPRESATYPE;
import com.esferalia.aon.sepe.api.contrata.contratos.DATOSETCOTYPE;
import com.esferalia.aon.sepe.api.contrata.contratos.DATOSETTTYPE;
import com.esferalia.aon.sepe.api.contrata.contratos.DATOSEXCLUSIONSOCIALTYPE;
import com.esferalia.aon.sepe.api.contrata.contratos.DATOSGENERALESCONTRATOTYPE;
import com.esferalia.aon.sepe.api.contrata.contratos.DATOSMEDIDASFOMENTOTYPE;
import com.esferalia.aon.sepe.api.contrata.contratos.DATOSPROGEMPLEOPUBLICOTYPE;
import com.esferalia.aon.sepe.api.contrata.contratos.DATOSREDUCCIONFORMACIONTYPE;
import com.esferalia.aon.sepe.api.contrata.contratos.DATOSREDUCCIONRDL12011TYPE;
import com.esferalia.aon.sepe.api.contrata.contratos.DATOSTRABAJADORTYPE;
import com.esferalia.aon.sepe.api.contrata.contratos.DATOSUSOLIBREEMPRESATYPE;
import com.esferalia.aon.sepe.api.contrata.contratos.NOMBREAPELLIDOSTYPE;
import com.esferalia.aon.ui.sepe.utils.SEPEUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class JooqContrata {
	
	public static void completeContratosParams(Object o, ContractSpecificData contractSpecificData) throws JAXBException, IOException {
		if (o instanceof CONTRATO100TYPE) {
			readContract100((CONTRATO100TYPE)o, contractSpecificData);
		} else if (o instanceof CONTRATO130TYPE) {
			readContract130((CONTRATO130TYPE)o, contractSpecificData);
		} else if (o instanceof CONTRATO150TYPE) {
			readContract150((CONTRATO150TYPE)o, contractSpecificData);
		} else if (o instanceof CONTRATO200TYPE) {
			readContract200((CONTRATO200TYPE)o, contractSpecificData);
		} else if (o instanceof CONTRATO230TYPE) {
			readContract230((CONTRATO230TYPE)o, contractSpecificData);
		} else if (o instanceof CONTRATO250TYPE) {
			readContract250((CONTRATO250TYPE)o, contractSpecificData);
		} else if (o instanceof CONTRATO300TYPE) {
			readContract300((CONTRATO300TYPE)o, contractSpecificData);
		} else if (o instanceof CONTRATO330TYPE) {
			readContract330((CONTRATO330TYPE)o, contractSpecificData);
		} else if (o instanceof CONTRATO350TYPE) {
			readContract350((CONTRATO350TYPE)o, contractSpecificData);
		} else if (o instanceof CONTRATO401TYPE) {
			readContract401((CONTRATO401TYPE)o, contractSpecificData);
		} else if (o instanceof CONTRATO402TYPE) {
			readContract402((CONTRATO402TYPE)o, contractSpecificData);
		} else if (o instanceof CONTRATO403TYPE) {
			readContract403((CONTRATO403TYPE)o, contractSpecificData);
		} else if (o instanceof CONTRATO410TYPE) {
			readContract410((CONTRATO410TYPE)o, contractSpecificData);
		} else if (o instanceof CONTRATO420TYPE) {
			readContract420((CONTRATO420TYPE)o, contractSpecificData);
		} else if (o instanceof CONTRATO421TYPE) {
			readContract421((CONTRATO421TYPE)o, contractSpecificData);
		} else if (o instanceof CONTRATO430TYPE) {
			readContract430((CONTRATO430TYPE)o, contractSpecificData);
		} else if (o instanceof CONTRATO441TYPE) {
			readContract441((CONTRATO441TYPE)o, contractSpecificData);
		} else if (o instanceof CONTRATO450TYPE) {
			readContract450((CONTRATO450TYPE)o, contractSpecificData);
		} else if (o instanceof CONTRATO452TYPE) {
			readContract452((CONTRATO452TYPE)o, contractSpecificData);
		} else if (o instanceof CONTRATO501TYPE) {
			readContract501((CONTRATO501TYPE)o, contractSpecificData);
		} else if (o instanceof CONTRATO502TYPE) {
			readContract502((CONTRATO502TYPE)o, contractSpecificData);
		} else if (o instanceof CONTRATO503TYPE) {
			readContract503((CONTRATO503TYPE)o, contractSpecificData);
		} else if (o instanceof CONTRATO510TYPE) {
			readContract510((CONTRATO510TYPE)o, contractSpecificData);
		} else if (o instanceof CONTRATO520TYPE) {
			readContract520((CONTRATO520TYPE)o, contractSpecificData);
		} else if (o instanceof CONTRATO530TYPE) {
			readContract530((CONTRATO530TYPE)o, contractSpecificData);
		} else if (o instanceof CONTRATO540TYPE) {
			readContract540((CONTRATO540TYPE)o, contractSpecificData);
		} else if (o instanceof CONTRATO541TYPE) {
			readContract541((CONTRATO541TYPE)o, contractSpecificData);
		} else if (o instanceof CONTRATO550TYPE) {
			readContract550((CONTRATO550TYPE)o, contractSpecificData);
		} else if (o instanceof CONTRATO552TYPE) {
			readContract552((CONTRATO552TYPE)o, contractSpecificData);
		} else if (o instanceof CONTRATO970TYPE) {
			readContract970((CONTRATO970TYPE)o, contractSpecificData);
		} else if (o instanceof CONTRATO980TYPE) {
			readContract980((CONTRATO980TYPE)o, contractSpecificData);
		} else if (o instanceof CONTRATO990TYPE) {
			readContract990((CONTRATO990TYPE)o, contractSpecificData);
		}
	}
	
	private static void readContract100(CONTRATO100TYPE o, ContractSpecificData contractSpecificData) {
		completeDatosGeneralesContrato(o.getDATOSGENERALESCONTRATO(), contractSpecificData);
		completeDatosMedidasFomento(o.getDATOSMEDIDASFOMENTO(), contractSpecificData);
		completeDatosAnexoContratoRelevo(o.getDATOSANEXOCONTRATORELEVO(), contractSpecificData);
		completeDatosEtCote(o.getDATOSETCOTE(), contractSpecificData);
		completeDatosEtt(o.getDATOSETT(), contractSpecificData);
		completeDatosContratoExtranjero(o.getDATOSCONTRATOEXTRANJERO(), contractSpecificData);
		completeDatosComunicacionCopiaBasica(o.getDATOSCOMUNICACOPIABASICA(), contractSpecificData);
		completeDatosUsoLibreEmpresa(o.getDATOSUSOLIBREEMPRESA(), contractSpecificData);
	}
	
	private static void readContract130(CONTRATO130TYPE o, ContractSpecificData contractSpecificData) {
		completeDatosGeneralesContrato(o.getDATOSGENERALESCONTRATO(), contractSpecificData);
	    completeDatosBonificacion(o.getDATOSBONIFICACION(), contractSpecificData);
	    completeDatosMedidasFomento(o.getDATOSMEDIDASFOMENTO(), contractSpecificData);
	    completeDatosAnexoContratoRelevo(o.getDATOSANEXOCONTRATORELEVO(), contractSpecificData);
	    completeDatosEtCote(o.getDATOSETCOTE(), contractSpecificData);
	    completeDatosEtt(o.getDATOSETT(), contractSpecificData); 
	    completeDatosComunicacionCopiaBasica(o.getDATOSCOMUNICACOPIABASICA(), contractSpecificData);
	    completeDatosUsoLibreEmpresa(o.getDATOSUSOLIBREEMPRESA(), contractSpecificData);
	}
	
	private static void readContract150(CONTRATO150TYPE o, ContractSpecificData contractSpecificData) {
		completeDatosGeneralesContrato(o.getDATOSGENERALESCONTRATO(), contractSpecificData);
		completeDatosBonificacion(o.getDATOSBONIFICACION(), contractSpecificData);
		completeDatosMedidasFomento(o.getDATOSMEDIDASFOMENTO(), contractSpecificData);
		completeDatosAnexoContratoRelevo(o.getDATOSANEXOCONTRATORELEVO(), contractSpecificData);
		completeDatosEtCote(o.getDATOSETCOTE(), contractSpecificData);
		completeDatosEtt(o.getDATOSETT(), contractSpecificData);
		completeDatosEmpresaInsercion(o.getDATOSEMPRESAINSERCION(), contractSpecificData);
		completeDatosComunicacionCopiaBasica(o.getDATOSCOMUNICACOPIABASICA(), contractSpecificData);
		completeDatosUsoLibreEmpresa(o.getDATOSUSOLIBREEMPRESA(), contractSpecificData);
	}
	
	private static void readContract200(CONTRATO200TYPE o, ContractSpecificData contractSpecificData) {
		completeDatosGeneralesContrato(o.getDATOSGENERALESCONTRATO(), contractSpecificData);
		completeDatosMedidasFomento(o.getDATOSMEDIDASFOMENTO(), contractSpecificData);
		completeDatosAnexoContratoRelevo(o.getDATOSANEXOCONTRATORELEVO(), contractSpecificData);
		completeDatosEtCote(o.getDATOSETCOTE(), contractSpecificData);
		completeDatosEtt(o.getDATOSETT(), contractSpecificData);
		completeDatosContratoExtranjero(o.getDATOSCONTRATOEXTRANJERO(), contractSpecificData);
		completeDatosComunicacionCopiaBasica(o.getDATOSCOMUNICACOPIABASICA(), contractSpecificData);
		completeDatosUsoLibreEmpresa(o.getDATOSUSOLIBREEMPRESA(), contractSpecificData);
		completeDatosReduccionRdl2011(o.getDATOSREDUCCIONRDL12011(), contractSpecificData);
		completeDatosContratoTiempoParcial(o.getDATOSCONTRATOTIEMPOPARCIAL(), contractSpecificData);
	}
	
	private static void readContract230(CONTRATO230TYPE o, ContractSpecificData contractSpecificData) {
		completeDatosGeneralesContrato(o.getDATOSGENERALESCONTRATO(), contractSpecificData);
		completeDatosContratoTiempoParcial(o.getDATOSCONTRATOTIEMPOPARCIAL(), contractSpecificData);
		completeDatosBonificacion(o.getDATOSBONIFICACION(), contractSpecificData);
		completeDatosMedidasFomento(o.getDATOSMEDIDASFOMENTO(), contractSpecificData);
		completeDatosAnexoContratoRelevo(o.getDATOSANEXOCONTRATORELEVO(), contractSpecificData);
		completeDatosEtCote(o.getDATOSETCOTE(), contractSpecificData);
		completeDatosEtt(o.getDATOSETT(), contractSpecificData);
		completeDatosReduccionRdl2011(o.getDATOSREDUCCIONRDL12011(), contractSpecificData);
		completeDatosComunicacionCopiaBasica(o.getDATOSCOMUNICACOPIABASICA(), contractSpecificData);
		completeDatosUsoLibreEmpresa(o.getDATOSUSOLIBREEMPRESA(), contractSpecificData);
	}
	
	private static void readContract250(CONTRATO250TYPE o, ContractSpecificData contractSpecificData) {
		completeDatosGeneralesContrato(o.getDATOSGENERALESCONTRATO(), contractSpecificData);
		completeDatosContratoTiempoParcial(o.getDATOSCONTRATOTIEMPOPARCIAL(), contractSpecificData);
		completeDatosBonificacion(o.getDATOSBONIFICACION(), contractSpecificData);
		completeDatosMedidasFomento(o.getDATOSMEDIDASFOMENTO(), contractSpecificData);
		completeDatosAnexoContratoRelevo(o.getDATOSANEXOCONTRATORELEVO(), contractSpecificData);
		completeDatosEtCote(o.getDATOSETCOTE(), contractSpecificData);
		completeDatosEtt(o.getDATOSETT(), contractSpecificData);
		completeDatosEmpresaInsercion(o.getDATOSEMPRESAINSERCION(), contractSpecificData);
		completeDatosReduccionRdl2011(o.getDATOSREDUCCIONRDL12011(), contractSpecificData);
		completeDatosComunicacionCopiaBasica(o.getDATOSCOMUNICACOPIABASICA(), contractSpecificData);
		completeDatosUsoLibreEmpresa(o.getDATOSUSOLIBREEMPRESA(), contractSpecificData);
	}
	
	private static void readContract300(CONTRATO300TYPE o, ContractSpecificData contractSpecificData) {
		completeDatosGeneralesContrato(o.getDATOSGENERALESCONTRATO(), contractSpecificData);
	    completeDatosContratoTiempoParcial(o.getDATOSCONTRATOTIEMPOPARCIAL(), contractSpecificData);
	    completeDatosMedidasFomento(o.getDATOSMEDIDASFOMENTO(), contractSpecificData);
	    completeDatosAnexoContratoRelevo(o.getDATOSANEXOCONTRATORELEVO(), contractSpecificData);
	    completeDatosEtCote(o.getDATOSETCOTE(), contractSpecificData);
	    completeDatosEtt(o.getDATOSETT(), contractSpecificData); 
	    completeDatosReduccionRdl2011(o.getDATOSREDUCCIONRDL12011(), contractSpecificData);
	    completeDatosComunicacionCopiaBasica(o.getDATOSCOMUNICACOPIABASICA(), contractSpecificData);
    	completeDatosUsoLibreEmpresa(o.getDATOSUSOLIBREEMPRESA(), contractSpecificData);
	}
	
	private static void readContract330(CONTRATO330TYPE o, ContractSpecificData contractSpecificData) {
		completeDatosGeneralesContrato(o.getDATOSGENERALESCONTRATO(), contractSpecificData);
		completeDatosContratoTiempoParcial(o.getDATOSCONTRATOTIEMPOPARCIAL(), contractSpecificData);
		completeDatosBonificacion(o.getDATOSBONIFICACION(), contractSpecificData);
		completeDatosMedidasFomento(o.getDATOSMEDIDASFOMENTO(), contractSpecificData);
		completeDatosAnexoContratoRelevo(o.getDATOSANEXOCONTRATORELEVO(), contractSpecificData);
		completeDatosEtCote(o.getDATOSETCOTE(), contractSpecificData);
		completeDatosEtt(o.getDATOSETT(), contractSpecificData); 
		completeDatosReduccionRdl2011(o.getDATOSREDUCCIONRDL12011(), contractSpecificData);
		completeDatosComunicacionCopiaBasica(o.getDATOSCOMUNICACOPIABASICA(), contractSpecificData);
		completeDatosUsoLibreEmpresa(o.getDATOSUSOLIBREEMPRESA(), contractSpecificData);
	}
	
	private static void readContract350(CONTRATO350TYPE o, ContractSpecificData contractSpecificData) {
		completeDatosGeneralesContrato(o.getDATOSGENERALESCONTRATO(), contractSpecificData);
		completeDatosContratoTiempoParcial(o.getDATOSCONTRATOTIEMPOPARCIAL(), contractSpecificData);
		completeDatosBonificacion(o.getDATOSBONIFICACION(), contractSpecificData);
		completeDatosMedidasFomento(o.getDATOSMEDIDASFOMENTO(), contractSpecificData);
		completeDatosAnexoContratoRelevo(o.getDATOSANEXOCONTRATORELEVO(), contractSpecificData);
		completeDatosEtCote(o.getDATOSETCOTE(), contractSpecificData);
		completeDatosEtt(o.getDATOSETT(), contractSpecificData); 
		completeDatosEmpresaInsercion(o.getDATOSEMPRESAINSERCION(), contractSpecificData);
		completeDatosReduccionRdl2011(o.getDATOSREDUCCIONRDL12011(), contractSpecificData);
		completeDatosComunicacionCopiaBasica(o.getDATOSCOMUNICACOPIABASICA(), contractSpecificData);
		completeDatosUsoLibreEmpresa(o.getDATOSUSOLIBREEMPRESA(), contractSpecificData);
	}
	
	private static void readContract401(CONTRATO401TYPE o, ContractSpecificData contractSpecificData) {
		completeDatosGeneralesContrato(o.getDATOSGENERALESCONTRATO(), contractSpecificData);
		completeDatosEtCote(o.getDATOSETCOTE(), contractSpecificData);
		completeDatosContratoInvestigacion(o.getDATOSCONTRATOINVESTIGACION(), contractSpecificData);
		completeDatosProgramaEmpleoPublico(o.getPROGEMPLEOPUBLICO(), contractSpecificData);
		completeDatosEtt(o.getDATOSETT(), contractSpecificData); 
		completeDatosContratoExtranjero(o.getDATOSCONTRATOEXTRANJERO(), contractSpecificData);
		completeDatosComunicacionCopiaBasica(o.getDATOSCOMUNICACOPIABASICA(), contractSpecificData);
		completeDatosUsoLibreEmpresa(o.getDATOSUSOLIBREEMPRESA(), contractSpecificData);
	}
	
	private static void readContract402(CONTRATO402TYPE o, ContractSpecificData contractSpecificData) {
		completeDatosGeneralesContrato(o.getDATOSGENERALESCONTRATO(), contractSpecificData);
		completeDatosEtCote(o.getDATOSETCOTE(), contractSpecificData);
		completeDatosCopiaBasica(o.getDATOSCOPIABASICA(), contractSpecificData);
		completeDatosProgramaEmpleoPublico(o.getPROGEMPLEOPUBLICO(), contractSpecificData);
		completeDatosEtt(o.getDATOSETT(), contractSpecificData); 
		completeDatosContratoExtranjero(o.getDATOSCONTRATOEXTRANJERO(), contractSpecificData);
		completeDatosComunicacionCopiaBasica(o.getDATOSCOMUNICACOPIABASICA(), contractSpecificData);
		completeDatosUsoLibreEmpresa(o.getDATOSUSOLIBREEMPRESA(), contractSpecificData);
	}
	
	private static void readContract403(CONTRATO403TYPE o, ContractSpecificData contractSpecificData) {
		completeDatosGeneralesContrato(o.getDATOSGENERALESCONTRATO(), contractSpecificData);
		completeDatosProgramaEmpleoPublico(o.getPROGEMPLEOPUBLICO(), contractSpecificData);
		completeDatosContratoInsercion(o.getDATOSCONTRATOINSERCION(), contractSpecificData);
		completeDatosEtt(o.getDATOSETT(), contractSpecificData); 
		completeDatosComunicacionCopiaBasica(o.getDATOSCOMUNICACOPIABASICA(), contractSpecificData);
		completeDatosUsoLibreEmpresa(o.getDATOSUSOLIBREEMPRESA(), contractSpecificData);
	}
	
	private static void readContract410(CONTRATO410TYPE o, ContractSpecificData contractSpecificData) {
		completeDatosGeneralesContrato(o.getDATOSGENERALESCONTRATO(), contractSpecificData);
		completeDatosContratoInterinidad(o.getDATOSCONTRATOINTERINIDAD(), contractSpecificData);
		completeDatosEtCote(o.getDATOSETCOTE(), contractSpecificData);
		completeDatosProgramaEmpleoPublico(o.getPROGEMPLEOPUBLICO(), contractSpecificData);
		completeDatosEtt(o.getDATOSETT(), contractSpecificData); 
		completeDatosComunicacionCopiaBasica(o.getDATOSCOMUNICACOPIABASICA(), contractSpecificData);
		completeDatosUsoLibreEmpresa(o.getDATOSUSOLIBREEMPRESA(), contractSpecificData);
	}
	
	private static void readContract420(CONTRATO420TYPE o, ContractSpecificData contractSpecificData) {
		completeDatosGeneralesContrato(o.getDATOSGENERALESCONTRATO(), contractSpecificData);
		completeDatosContratoPracticas(o.getDATOSCONTRATOPRACTICAS(), contractSpecificData);
		completeDatosEtCote(o.getDATOSETCOTE(), contractSpecificData);
		completeDatosContratoInvestigacion(o.getDATOSCONTRATOINVESTIGACION(), contractSpecificData);
		completeDatosProgramaEmpleoPublico(o.getPROGEMPLEOPUBLICO(), contractSpecificData);
		completeDatosEtt(o.getDATOSETT(), contractSpecificData); 
		completeDatosComunicacionCopiaBasica(o.getDATOSCOMUNICACOPIABASICA(), contractSpecificData);
		completeDatosUsoLibreEmpresa(o.getDATOSUSOLIBREEMPRESA(), contractSpecificData);
	}
	
	private static void readContract421(CONTRATO421TYPE o, ContractSpecificData contractSpecificData) {
		completeDatosGeneralesContrato(o.getDATOSGENERALESCONTRATO(), contractSpecificData);
		completeDatosContratoTiempoParcial(o.getDATOSCONTRATOTIEMPOPARCIAL(), contractSpecificData);
		completeDatosEtCote(o.getDATOSETCOTE(), contractSpecificData);
		completeDatosProgramaEmpleoPublico(o.getPROGEMPLEOPUBLICO(), contractSpecificData);
		completeDatosEtt(o.getDATOSETT(), contractSpecificData); 
		completeDatosReduccionFormacion(o.getDATOSREDUCCIONFORMACION(), contractSpecificData); 
		completeDatosComunicacionCopiaBasica(o.getDATOSCOMUNICACOPIABASICA(), contractSpecificData);
		completeDatosUsoLibreEmpresa(o.getDATOSUSOLIBREEMPRESA(), contractSpecificData);
	}
	
	private static void readContract430(CONTRATO430TYPE o, ContractSpecificData contractSpecificData) {
		completeDatosGeneralesContrato(o.getDATOSGENERALESCONTRATO(), contractSpecificData);
		completeDatosBonificacion(o.getDATOSBONIFICACION(), contractSpecificData);
		completeDatosEtCote(o.getDATOSETCOTE(), contractSpecificData);
		completeDatosProgramaEmpleoPublico(o.getPROGEMPLEOPUBLICO(), contractSpecificData);
		completeDatosEtt(o.getDATOSETT(), contractSpecificData); 
		completeDatosComunicacionCopiaBasica(o.getDATOSCOMUNICACOPIABASICA(), contractSpecificData);
		completeDatosUsoLibreEmpresa(o.getDATOSUSOLIBREEMPRESA(), contractSpecificData);
	}
	
	private static void readContract441(CONTRATO441TYPE o, ContractSpecificData contractSpecificData) {
		completeDatosGeneralesContrato(o.getDATOSGENERALESCONTRATO(), contractSpecificData);
		completeDatosAnexoContratoRelevo(o.getDATOSANEXOCONTRATORELEVO(), contractSpecificData);
		completeDatosEtCote(o.getDATOSETCOTE(), contractSpecificData);
		completeDatosEtt(o.getDATOSETT(), contractSpecificData); 
		completeDatosComunicacionCopiaBasica(o.getDATOSCOMUNICACOPIABASICA(), contractSpecificData);
		completeDatosUsoLibreEmpresa(o.getDATOSUSOLIBREEMPRESA(), contractSpecificData);
	}
	
	private static void readContract450(CONTRATO450TYPE o, ContractSpecificData contractSpecificData) {
		completeDatosGeneralesContrato(o.getDATOSGENERALESCONTRATO(), contractSpecificData);
		completeDatosExclusionSocial(o.getDATOSEXCLUSIONSOCIAL(), contractSpecificData);
		completeDatosBonificacion(o.getDATOSBONIFICACION(), contractSpecificData);
		completeDatosContratoPracticas(o.getDATOSCONTRATOPRACTICAS(), contractSpecificData);
		completeDatosContratoTiempoParcial(o.getDATOSCONTRATOTIEMPOPARCIAL(), contractSpecificData);
		completeDatosContratoInterinidad(o.getDATOSCONTRATOINTERINIDAD(), contractSpecificData);
		completeDatosEtCote(o.getDATOSETCOTE(), contractSpecificData);
		completeDatosCopiaBasica(o.getDATOSCOPIABASICA(), contractSpecificData);
		completeDatosProgramaEmpleoPublico(o.getPROGEMPLEOPUBLICO(), contractSpecificData);
		completeDatosEtt(o.getDATOSETT(), contractSpecificData); 
		completeDatosEmpresaInsercion(o.getDATOSEMPRESAINSERCION(), contractSpecificData);
		completeDatosComunicacionCopiaBasica(o.getDATOSCOMUNICACOPIABASICA(), contractSpecificData);
		completeDatosUsoLibreEmpresa(o.getDATOSUSOLIBREEMPRESA(), contractSpecificData);
	}
	
	private static void readContract452(CONTRATO452TYPE o, ContractSpecificData contractSpecificData) {
		completeDatosGeneralesContrato(o.getDATOSGENERALESCONTRATO(), contractSpecificData);
		completeDatosBonificacion(o.getDATOSBONIFICACION(), contractSpecificData);
		completeDatosEtCote(o.getDATOSETCOTE(), contractSpecificData);
		completeDatosCopiaBasica(o.getDATOSCOPIABASICA(), contractSpecificData);
		completeDatosProgramaEmpleoPublico(o.getPROGEMPLEOPUBLICO(), contractSpecificData);
		completeDatosEtt(o.getDATOSETT(), contractSpecificData); 
		completeDatosEmpresaInsercion(o.getDATOSEMPRESAINSERCION(), contractSpecificData);
		completeDatosComunicacionCopiaBasica(o.getDATOSCOMUNICACOPIABASICA(), contractSpecificData);
		completeDatosUsoLibreEmpresa(o.getDATOSUSOLIBREEMPRESA(), contractSpecificData);
	}
	
	private static void readContract501(CONTRATO501TYPE o, ContractSpecificData contractSpecificData) {
		completeDatosGeneralesContrato(o.getDATOSGENERALESCONTRATO(), contractSpecificData);
		completeDatosContratoTiempoParcial(o.getDATOSCONTRATOTIEMPOPARCIAL(), contractSpecificData);
		completeDatosEtCote(o.getDATOSETCOTE(), contractSpecificData);
		completeDatosContratoInvestigacion(o.getDATOSCONTRATOINVESTIGACION(), contractSpecificData);
		completeDatosProgramaEmpleoPublico(o.getPROGEMPLEOPUBLICO(), contractSpecificData);
		completeDatosEtt(o.getDATOSETT(), contractSpecificData); 
		completeDatosContratoExtranjero(o.getDATOSCONTRATOEXTRANJERO(), contractSpecificData);
		completeDatosReduccionRdl2011(o.getDATOSREDUCCIONRDL12011(), contractSpecificData);
		completeDatosComunicacionCopiaBasica(o.getDATOSCOMUNICACOPIABASICA(), contractSpecificData);
		completeDatosUsoLibreEmpresa(o.getDATOSUSOLIBREEMPRESA(), contractSpecificData);
	}
	
	private static void readContract502(CONTRATO502TYPE o, ContractSpecificData contractSpecificData) {
		completeDatosGeneralesContrato(o.getDATOSGENERALESCONTRATO(), contractSpecificData);
		completeDatosContratoTiempoParcial(o.getDATOSCONTRATOTIEMPOPARCIAL(), contractSpecificData);
		completeDatosEtCote(o.getDATOSETCOTE(), contractSpecificData);
		completeDatosProgramaEmpleoPublico(o.getPROGEMPLEOPUBLICO(), contractSpecificData);
		completeDatosEtt(o.getDATOSETT(), contractSpecificData); 
		completeDatosContratoExtranjero(o.getDATOSCONTRATOEXTRANJERO(), contractSpecificData);
		completeDatosReduccionRdl2011(o.getDATOSREDUCCIONRDL12011(), contractSpecificData);
		completeDatosComunicacionCopiaBasica(o.getDATOSCOMUNICACOPIABASICA(), contractSpecificData);
		completeDatosUsoLibreEmpresa(o.getDATOSUSOLIBREEMPRESA(), contractSpecificData);
	}
	
	private static void readContract503(CONTRATO503TYPE o, ContractSpecificData contractSpecificData) {
		completeDatosGeneralesContrato(o.getDATOSGENERALESCONTRATO(), contractSpecificData);
		completeDatosContratoTiempoParcial(o.getDATOSCONTRATOTIEMPOPARCIAL(), contractSpecificData);
		completeDatosProgramaEmpleoPublico(o.getPROGEMPLEOPUBLICO(), contractSpecificData);
		completeDatosContratoInsercion(o.getDATOSCONTRATOINSERCION(), contractSpecificData);
		completeDatosEtt(o.getDATOSETT(), contractSpecificData); 
		completeDatosComunicacionCopiaBasica(o.getDATOSCOMUNICACOPIABASICA(), contractSpecificData);
		completeDatosUsoLibreEmpresa(o.getDATOSUSOLIBREEMPRESA(), contractSpecificData);
	}
	
	private static void readContract510(CONTRATO510TYPE o, ContractSpecificData contractSpecificData) {
		completeDatosGeneralesContrato(o.getDATOSGENERALESCONTRATO(), contractSpecificData);
		completeDatosContratoTiempoParcial(o.getDATOSCONTRATOTIEMPOPARCIAL(), contractSpecificData);
		completeDatosContratoInterinidad(o.getDATOSCONTRATOINTERINIDAD(), contractSpecificData);
		completeDatosEtCote(o.getDATOSETCOTE(), contractSpecificData);
		completeDatosProgramaEmpleoPublico(o.getPROGEMPLEOPUBLICO(), contractSpecificData);
		completeDatosEtt(o.getDATOSETT(), contractSpecificData); 
		completeDatosComunicacionCopiaBasica(o.getDATOSCOMUNICACOPIABASICA(), contractSpecificData);
		completeDatosUsoLibreEmpresa(o.getDATOSUSOLIBREEMPRESA(), contractSpecificData);
	}
	
	private static void readContract520(CONTRATO520TYPE o, ContractSpecificData contractSpecificData) {
		completeDatosGeneralesContrato(o.getDATOSGENERALESCONTRATO(), contractSpecificData);
		completeDatosContratoPracticas(o.getDATOSCONTRATOPRACTICAS(), contractSpecificData);
		completeDatosContratoTiempoParcial(o.getDATOSCONTRATOTIEMPOPARCIAL(), contractSpecificData);
		completeDatosEtCote(o.getDATOSETCOTE(), contractSpecificData);
		completeDatosContratoInvestigacion(o.getDATOSCONTRATOINVESTIGACION(), contractSpecificData);
		completeDatosProgramaEmpleoPublico(o.getPROGEMPLEOPUBLICO(), contractSpecificData);
		completeDatosEtt(o.getDATOSETT(), contractSpecificData); 
		completeDatosReduccionRdl2011(o.getDATOSREDUCCIONRDL12011(), contractSpecificData);
		completeDatosComunicacionCopiaBasica(o.getDATOSCOMUNICACOPIABASICA(), contractSpecificData);
		completeDatosUsoLibreEmpresa(o.getDATOSUSOLIBREEMPRESA(), contractSpecificData);
	}
	
	private static void readContract530(CONTRATO530TYPE o, ContractSpecificData contractSpecificData) {
		completeDatosGeneralesContrato(o.getDATOSGENERALESCONTRATO(), contractSpecificData);
		completeDatosBonificacion(o.getDATOSBONIFICACION(), contractSpecificData);
		completeDatosContratoTiempoParcial(o.getDATOSCONTRATOTIEMPOPARCIAL(), contractSpecificData);
		completeDatosEtCote(o.getDATOSETCOTE(), contractSpecificData);
		completeDatosProgramaEmpleoPublico(o.getPROGEMPLEOPUBLICO(), contractSpecificData);
		completeDatosEtt(o.getDATOSETT(), contractSpecificData); 
		completeDatosReduccionRdl2011(o.getDATOSREDUCCIONRDL12011(), contractSpecificData);
		completeDatosComunicacionCopiaBasica(o.getDATOSCOMUNICACOPIABASICA(), contractSpecificData);
		completeDatosUsoLibreEmpresa(o.getDATOSUSOLIBREEMPRESA(), contractSpecificData);
	}
	
	private static void readContract540(CONTRATO540TYPE o, ContractSpecificData contractSpecificData) {
		completeDatosGeneralesContrato(o.getDATOSGENERALESCONTRATO(), contractSpecificData);
		completeDatosContratoTiempoParcial(o.getDATOSCONTRATOTIEMPOPARCIAL(), contractSpecificData);
		completeDatosEtCote(o.getDATOSETCOTE(), contractSpecificData);
		completeDatosEtt(o.getDATOSETT(), contractSpecificData); 
		completeDatosComunicacionCopiaBasica(o.getDATOSCOMUNICACOPIABASICA(), contractSpecificData);
		completeDatosUsoLibreEmpresa(o.getDATOSUSOLIBREEMPRESA(), contractSpecificData);
	}
	
	private static void readContract541(CONTRATO541TYPE o, ContractSpecificData contractSpecificData) {
		completeDatosGeneralesContrato(o.getDATOSGENERALESCONTRATO(), contractSpecificData);
		completeDatosContratoTiempoParcial(o.getDATOSCONTRATOTIEMPOPARCIAL(), contractSpecificData);
		completeDatosAnexoContratoRelevo(o.getDATOSANEXOCONTRATORELEVO(), contractSpecificData);
		completeDatosEtCote(o.getDATOSETCOTE(), contractSpecificData);
		completeDatosEtt(o.getDATOSETT(), contractSpecificData); 
		completeDatosComunicacionCopiaBasica(o.getDATOSCOMUNICACOPIABASICA(), contractSpecificData);
		completeDatosUsoLibreEmpresa(o.getDATOSUSOLIBREEMPRESA(), contractSpecificData);
	}
	
	private static void readContract550(CONTRATO550TYPE o, ContractSpecificData contractSpecificData) {
		completeDatosGeneralesContrato(o.getDATOSGENERALESCONTRATO(), contractSpecificData);
		completeDatosExclusionSocial(o.getDATOSEXCLUSIONSOCIAL(), contractSpecificData);
		completeDatosBonificacion(o.getDATOSBONIFICACION(), contractSpecificData);
		completeDatosContratoPracticas(o.getDATOSCONTRATOPRACTICAS(), contractSpecificData);
		completeDatosContratoTiempoParcial(o.getDATOSCONTRATOTIEMPOPARCIAL(), contractSpecificData);
		completeDatosContratoInterinidad(o.getDATOSCONTRATOINTERINIDAD(), contractSpecificData);
		completeDatosEtCote(o.getDATOSETCOTE(), contractSpecificData);
		completeDatosCopiaBasica(o.getDATOSCOPIABASICA(), contractSpecificData);
		completeDatosProgramaEmpleoPublico(o.getPROGEMPLEOPUBLICO(), contractSpecificData);
		completeDatosEtt(o.getDATOSETT(), contractSpecificData); 
		completeDatosEmpresaInsercion(o.getDATOSEMPRESAINSERCION(), contractSpecificData);
		completeDatosReduccionRdl2011(o.getDATOSREDUCCIONRDL12011(), contractSpecificData);
		completeDatosComunicacionCopiaBasica(o.getDATOSCOMUNICACOPIABASICA(), contractSpecificData);
		completeDatosUsoLibreEmpresa(o.getDATOSUSOLIBREEMPRESA(), contractSpecificData);
	}
	
	private static void readContract552(CONTRATO552TYPE o, ContractSpecificData contractSpecificData) {
		completeDatosGeneralesContrato(o.getDATOSGENERALESCONTRATO(), contractSpecificData);
		completeDatosBonificacion(o.getDATOSBONIFICACION(), contractSpecificData);
		completeDatosContratoTiempoParcial(o.getDATOSCONTRATOTIEMPOPARCIAL(), contractSpecificData);
		completeDatosEtCote(o.getDATOSETCOTE(), contractSpecificData);
		completeDatosCopiaBasica(o.getDATOSCOPIABASICA(), contractSpecificData);
		completeDatosProgramaEmpleoPublico(o.getPROGEMPLEOPUBLICO(), contractSpecificData);
		completeDatosEtt(o.getDATOSETT(), contractSpecificData);
		completeDatosEmpresaInsercion(o.getDATOSEMPRESAINSERCION(), contractSpecificData);
		completeDatosComunicacionCopiaBasica(o.getDATOSCOMUNICACOPIABASICA(), contractSpecificData);
		completeDatosUsoLibreEmpresa(o.getDATOSUSOLIBREEMPRESA(), contractSpecificData);
	}
	
	private static void readContract970(CONTRATO970TYPE o, ContractSpecificData contractSpecificData) {
		completeDatosGeneralesContrato(o.getDATOSGENERALESCONTRATO(), contractSpecificData);
		completeDatosEtCote(o.getDATOSETCOTE(), contractSpecificData);
		completeDatosProgramaEmpleoPublico(o.getPROGEMPLEOPUBLICO(), contractSpecificData);
		completeDatosEtt(o.getDATOSETT(), contractSpecificData);
		completeDatosComunicacionCopiaBasica(o.getDATOSCOMUNICACOPIABASICA(), contractSpecificData);
		completeDatosUsoLibreEmpresa(o.getDATOSUSOLIBREEMPRESA(), contractSpecificData);
	}
	
	private static void readContract980(CONTRATO980TYPE o, ContractSpecificData contractSpecificData) {
		completeDatosGeneralesContrato(o.getDATOSGENERALESCONTRATO(), contractSpecificData);
		completeDatosEtCote(o.getDATOSETCOTE(), contractSpecificData);
		completeDatosEtt(o.getDATOSETT(), contractSpecificData);
		completeDatosComunicacionCopiaBasica(o.getDATOSCOMUNICACOPIABASICA(), contractSpecificData);
		completeDatosUsoLibreEmpresa(o.getDATOSUSOLIBREEMPRESA(), contractSpecificData);
	}
	
	private static void readContract990(CONTRATO990TYPE o, ContractSpecificData contractSpecificData) {
		completeDatosGeneralesContrato(o.getDATOSGENERALESCONTRATO(), contractSpecificData);
		completeDatosEtCote(o.getDATOSETCOTE(), contractSpecificData);
		completeDatosCopiaBasica(o.getDATOSCOPIABASICA(), contractSpecificData);
		completeDatosProgramaEmpleoPublico(o.getPROGEMPLEOPUBLICO(), contractSpecificData);
		completeDatosEtt(o.getDATOSETT(), contractSpecificData);
		completeDatosComunicacionCopiaBasica(o.getDATOSCOMUNICACOPIABASICA(), contractSpecificData);
		completeDatosUsoLibreEmpresa(o.getDATOSUSOLIBREEMPRESA(), contractSpecificData);
	}
	
	private static void completeDatosGeneralesContrato(DATOSGENERALESCONTRATOTYPE datos, ContractSpecificData contractSpecificData) {
		if(datos != null){
			// TODO: ¿Convenio Colectivo? -> Disabled currently
//			if(datos.getINDCONVENIOCOLECTIVO()!=null){
//				params.setCollectiveAgreement(datos.getINDCONVENIOCOLECTIVO().equals("S")?true:false);
//			}
			
			if(datos.getNIVELFORMATIVO()!=null){
				contractSpecificData.setFormativeLevel(datos.getNIVELFORMATIVO());
			}
			
			if(datos.getINDDISCAPACIDAD()!=null){
				contractSpecificData.setDisability(datos.getINDDISCAPACIDAD());
				contractSpecificData.setDisabilityB(true);
			}
			
			if(datos.getCODIGOOCUPACION()!=null){
				contractSpecificData.setCno(datos.getCODIGOOCUPACION().trim());
			}
			
			if(datos.getIDOFERTA()!=null){
				contractSpecificData.setOffer(datos.getIDOFERTA());
				contractSpecificData.setOfferWorkData(true);
			}
			
			if(datos.getCODIGOPROGRAMAEMPLEO()!=null){
				contractSpecificData.setWorkProgramData(true);
				contractSpecificData.setWorkProgram(datos.getCODIGOPROGRAMAEMPLEO());
			}
			
			if(datos.getOTRASLEGISLACIONES()!=null){
				contractSpecificData.setOtherLegislations(datos.getOTRASLEGISLACIONES());
				contractSpecificData.setOlderThan52(true);
			}
			
			if(datos.getDATOSCAMPAÑAS()!=null){
				contractSpecificData.setCpCampaign(datos.getDATOSCAMPAÑAS().substring(0, 2));
				contractSpecificData.setCodeCampaign(datos.getDATOSCAMPAÑAS().substring(2, 5));
				contractSpecificData.setYearCampaign(datos.getDATOSCAMPAÑAS().substring(datos.getDATOSCAMPAÑAS().length()-4, datos.getDATOSCAMPAÑAS().length()));
				contractSpecificData.setCampaigns(true);
			}
			
			// Plan de Transformación, Recuperación y Resiliencia
			if(datos.getPROYECTOEMPLEOFORMACION() != null)
				contractSpecificData.setPlanRecovery(AonStringUtils.equalsIgnoreCase(datos.getPROYECTOEMPLEOFORMACION(), "S") ? true : false);
			
		}
	}

	private static void completeDatosMedidasFomento(DATOSMEDIDASFOMENTOTYPE datos, ContractSpecificData contractSpecificData) {
		if(datos != null){
			contractSpecificData.setPromotionMeasures(true);
			contractSpecificData.setPromotionPermanentHiring(datos.getINDCOSTEDESPIDO().equals("1")?true:false);
//			if(datos.getCODIGOCOLECTIVODESPIDO()!=null){
//				params.setCodigoColectivoDespido(TEOCOLDE.getEnumByValue(datos.getCODIGOCOLECTIVODESPIDO()));
//			}
		}
	}
	
	private static void completeDatosAnexoContratoRelevo(DATOSANEXOCONTRATORELEVOTYPE datos, ContractSpecificData contractSpecificData) {
		if(datos != null){
			contractSpecificData.setContractRelief(true);
			if(datos.getTIPOTRABAJADOR()!=null){
				contractSpecificData.setReliefEmployee(datos.getTIPOTRABAJADOR());
			}
			contractSpecificData.setRetirementName(datos.getNOMBREAPELLIDOS().getNOMBRE());
			contractSpecificData.setRetirementSurname(datos.getNOMBREAPELLIDOS().getPRIMERAPELLIDO());
			contractSpecificData.setRetirementSurname2(datos.getNOMBREAPELLIDOS().getSEGUNDOAPELLIDO());
		}
	}
	
	
	private static void completeDatosEtCote(DATOSETCOTYPE datos, ContractSpecificData contractSpecificData) {
		if(datos != null){
			contractSpecificData.setWorkshopSchoolB(true);
			contractSpecificData.setWorkshopSchool(datos.getCODIGOETCOTE());
		}
	}
	
	private static void completeDatosEtt(DATOSETTTYPE datos, ContractSpecificData contractSpecificData) {
		if(datos != null){
			contractSpecificData.setNif(null != datos.getCIFNIFEMPRESAUSUARIA() ? datos.getCIFNIFEMPRESAUSUARIA().getCIFNIF() : null);
			contractSpecificData.setSocialReason(datos.getRAZONSOCIALEMPRESAUSUARIA());
			contractSpecificData.setContractTemplate(!AonStringUtils.isBlank(datos.getINDCTOPLANTILLA()) && datos.getINDCTOPLANTILLA().equals("S"));
			contractSpecificData.setForeignEnterprise(!AonStringUtils.isBlank(datos.getINDEMPRESAEXTRANJERA()) && datos.getINDEMPRESAEXTRANJERA().equals("S"));
		
			contractSpecificData.setTemporalWorkEnterprise(null != datos.getCIFNIFEMPRESAUSUARIA() || !AonStringUtils.isBlank(datos.getINDCTOPLANTILLA()) || !AonStringUtils.isBlank(datos.getINDEMPRESAEXTRANJERA()) || !AonStringUtils.isBlank(datos.getRAZONSOCIALEMPRESAUSUARIA()));
		}
	}
	
	private static void completeDatosContratoExtranjero(DATOSCONTRATOEXTRANJEROTYPE datos, ContractSpecificData contractSpecificData) {
		if(datos != null){
			contractSpecificData.setAnnexedB(true);
			contractSpecificData.setSourceYear(datos.getAÑOCONTINGENTE());
			if(!AonStringUtils.isBlank(datos.getINDCARACTEROFERTA())) {
				if(datos.getINDCARACTEROFERTA().equals("E")) {
					contractSpecificData.setAnnexed(true);
				} else {
					contractSpecificData.setAnnexed(false);
				}
			} else {
				contractSpecificData.setAnnexed(false);
			}
		}
	}
	
	private static void completeDatosComunicacionCopiaBasica(DATOSCOMUNICACOPIABASICATYPE datos, ContractSpecificData contractSpecificData) {
		if(datos != null){
			contractSpecificData.setSignBasicCopy(datos.getTIPOFIRMA());
			contractSpecificData.setBasicCopy(datos.getTEXTOCOPIABASICA());
		}
	}

	private static void completeDatosUsoLibreEmpresa(DATOSUSOLIBREEMPRESATYPE datos, ContractSpecificData contractSpecificData) {
		if(datos != null){
			contractSpecificData.setUseEnterpriseFree(datos.getUSOLIBREEMPRESA());
		}
	}
	
	private static void completeDatosBonificacion(DATOSBONIFICACIONTYPE datos, ContractSpecificData contractSpecificData) {
		if(datos != null){
			if( datos.getCODIGOCOLECTIVOBONIF()!=null ) {
				String collectiveBonus = datos.getCODIGOCOLECTIVOBONIF();
				if(isBonus(collectiveBonus)) {
					contractSpecificData.setBonusType(datos.getCODIGOCOLECTIVOBONIF());
					contractSpecificData.setBonus(true);
					contractSpecificData.setDisabilityB(false);
				} else {
					contractSpecificData.setBonusColective(datos.getCODIGOCOLECTIVOBONIF());
					contractSpecificData.setBonus(false);
					contractSpecificData.setDisabilityB(true);
				}
				
			}

			if(datos.getINDICEMPLEADAUTONOMO()!=null){
				if(datos.getINDICEMPLEADAUTONOMO().equals("1")){
					contractSpecificData.setFreelanceEmployeer(true);
				} else if(datos.getINDICEMPLEADAUTONOMO().equals("2")){
					contractSpecificData.setFreelanceEmployeer(false);
				}
			}
			
			contractSpecificData.setEntrepreneurSupport(datos.getINDICEMPLEADAUTONOMO()!=null);
			
		}
	}
	
	private static boolean isBonus(String collectiveBonus) {
		ArrayList<String> list = new ArrayList<>(List.of("016", "017", "018", "019", "020", "021", "040", "041", "060", "088", "089", "163", "172", "175", "186", "187", "188", "193"));
		return list.contains(collectiveBonus);
	}
	
	private static void completeDatosEmpresaInsercion(DATOSEMPRESAINSERCIONTYPE dato, ContractSpecificData contractSpecificData) {
		// TODO
	}
	
	private static void completeDatosReduccionRdl2011(DATOSREDUCCIONRDL12011TYPE datos, ContractSpecificData contractSpecificData) {
		if(datos != null){
			contractSpecificData.setQuoteReductions(true);
			if(datos.getCODIGOCOLECTIVOREDUCCION()!=null)
				contractSpecificData.setReductionColective(datos.getCODIGOCOLECTIVOREDUCCION());
			

			if(NumberUtils.isNumber(datos.getPORCENTAJEJORNADAREDUCCION())){
				contractSpecificData.setJourneyPercent((Double.parseDouble(datos.getPORCENTAJEJORNADAREDUCCION())/100)+"");
			}
			if(AonStringUtils.isNotBlank(datos.getPORCENTAJEREDUCCION())){
				contractSpecificData.setQuoteReduction(datos.getPORCENTAJEREDUCCION().equals("75"));
			}
		}
	}
	
	private static void completeDatosContratoTiempoParcial(DATOSCONTRATOTIEMPOPARCIALTYPE datos, ContractSpecificData contractSpecificData) {
		if(datos != null){
//			params.setActividadSinFechaCierta(datos.getACTIVIDADSINFECHACIERTA());
//			params.setColectivoEdad(THPCOLFO.getEnumByValue(datos.getCOLECTIVOEDAD()));
			if(datos.getFIJODISCONTINUOPERIODICO()!=null){
				contractSpecificData.setRepeatFD(datos.getFIJODISCONTINUOPERIODICO().equals("S"));
			}
//			params.setHorasAnualesTiempoCompleto(datos.getHORASANUALESTIEMPOCOMPLETO());
			contractSpecificData.setAgreementHours(getHoras(datos.getHORASCONVENIO()));
			contractSpecificData.setAgreementMinutes(getMinutos(datos.getHORASCONVENIO()));
			contractSpecificData.setFormationHours(getHoras(datos.getHORASFORMACION()));
			contractSpecificData.setFormationMinutes(getMinutos(datos.getHORASFORMACION()));
			contractSpecificData.setJourneyDurationHours(getHoras(datos.getHORASJORNADA()));
			contractSpecificData.setJourneyDurationMinutes(getMinutos(datos.getHORASJORNADA()));
			contractSpecificData.setTeoricFormation(null == datos.getINDICFORMACIONTEORICA() ? false : datos.getINDICFORMACIONTEORICA().equals("S"));
			contractSpecificData.setRetirementPercent(datos.getPORCENTAJEJUBILACIONPARCIAL());
//			params.setPorcJornadaPactada(datos.getPORCJORNADAPACTADA());
			contractSpecificData.setJourneyType(datos.getTIPOJORNADA());
		}
	}
	
	private static void completeDatosContratoInvestigacion(DATOSCONTRATOINVESTIGACIONTYPE datos, ContractSpecificData contractSpecificData) {
		if(datos != null){
			contractSpecificData.setInvest(true);
			contractSpecificData.setEmployee(datos.getINDTRABAJADOR());
			contractSpecificData.setEmployer(datos.getINDEMPLEADOR());
			contractSpecificData.setResearcher(datos.getINDRD632006()!=null && datos.getINDRD632006().equals("S"));
		}
	}
	
	private static void completeDatosProgramaEmpleoPublico(DATOSPROGEMPLEOPUBLICOTYPE datos,  ContractSpecificData contractSpecificData) {
		//TODO
		if(datos != null){
//			params.setEmploymentProgramData(true);
//			datos.setACTUACION("");
//			datos.setCORPORACIONLOCAL("");
//			datos.setEJERCICIOPRESUPUESTARIO("");
//			datos.setGRUPOCOTIZACIONCORPORACIONLOCAL("");
		}
	}
	
	private static void completeDatosCopiaBasica(DATOSCOPIABASICATYPE datos, ContractSpecificData contractSpecificData) {
		// TODO
	}
	
	private static void completeDatosContratoInsercion(DATOSCONTRATOINSERCIONTYPE datos, ContractSpecificData contractSpecificData) {
		// TODO
	}
	
	private static void completeDatosContratoInterinidad(DATOSCONTRATOINTERINIDADTYPE datos, ContractSpecificData contractSpecificData) {
		if(datos != null){
			contractSpecificData.setIsInterimCause(true);
			contractSpecificData.setInterimCause(datos.getCAUSAINTERINIDAD());
		}
	}
	private static void completeDatosContratoPracticas(DATOSCONTRATOPRACTICASTYPE datos, ContractSpecificData contractSpecificData) {
		if(datos != null){
			contractSpecificData.setAcademicTitulation(datos.getTITULACIONACADEMICA());
			if(!AonStringUtils.isEmpty(datos.getINDCERTIFPROFESIONALIDAD())){
				contractSpecificData.setProfesionality(datos.getINDCERTIFPROFESIONALIDAD().equals("S"));
			}
		}
	}
	
	private static void completeDatosReduccionFormacion(DATOSREDUCCIONFORMACIONTYPE datos, ContractSpecificData contractSpecificData) {
		if(datos != null){
			contractSpecificData.setQuoteReductions(true);
			contractSpecificData.setReductionColective(datos.getCODIGOCOLECTIVOREDUCCIONFORMACION());
			contractSpecificData.setJourneyPercent(datos.getPORCENTAJEREDUCCIONFORMACION());
//			params.setReductionData(true);
//			params.setCodigoColectivoReduccion(TQOCOLRE.getEnumByValue(datos.getCODIGOCOLECTIVOREDUCCIONFORMACION()));
//			params.setPorcentajeReduccion(datos.getPORCENTAJEREDUCCIONFORMACION());
		}
	}
	
	private static void completeDatosExclusionSocial(DATOSEXCLUSIONSOCIALTYPE datos, ContractSpecificData contractSpecificData) {
		// TODO
	}
	
	private static String getHoras(String duracion){
		return AonStringUtils.isBlank(duracion)?null:duracion.substring(0,4);
	}
	private static String getMinutos(String duracion){
		return AonStringUtils.isBlank(duracion)?null:duracion.substring(4,6);
	}
	
	public static IContratoType createContratoModel(String code) throws IllegalArgumentException {
		try {
			com.esferalia.aon.sepe.api.contrata.contratos.ObjectFactory factory = new com.esferalia.aon.sepe.api.contrata.contratos.ObjectFactory();
			if (code.equals(ContractCode.C100.getValue())) {
				return factory.createCONTRATO100TYPE();
			} else if (code.equals(ContractCode.C109.getValue())) {
				return factory.createCONTRATO100TYPE();
			} else if (code.equals(ContractCode.C130.getValue())) {
				return factory.createCONTRATO130TYPE();
			} else if (code.equals(ContractCode.C139.getValue())) {
				return factory.createCONTRATO130TYPE();
			} else if (code.equals(ContractCode.C150.getValue())) {
				return factory.createCONTRATO150TYPE();
			} else if (code.equals(ContractCode.C189.getValue())) {
				return factory.createCONTRATO100TYPE();
			} else if (code.equals(ContractCode.C200.getValue())) {
				return factory.createCONTRATO200TYPE();
			} else if (code.equals(ContractCode.C209.getValue())) {
				return factory.createCONTRATO200TYPE();
			} else if (code.equals(ContractCode.C230.getValue())) {
				return factory.createCONTRATO230TYPE();
			} else if (code.equals(ContractCode.C250.getValue())) {
				return factory.createCONTRATO250TYPE();
			} else if (code.equals(ContractCode.C289.getValue())) {
				return factory.createCONTRATO200TYPE();
			} else if (code.equals(ContractCode.C300.getValue())) {
				return factory.createCONTRATO300TYPE();
			} else if (code.equals(ContractCode.C330.getValue())) {
				return factory.createCONTRATO330TYPE();
			} else if (code.equals(ContractCode.C339.getValue())) {
				return factory.createCONTRATO330TYPE();
			} else if (code.equals(ContractCode.C350.getValue())) {
				return factory.createCONTRATO350TYPE();
			} else if (code.equals(ContractCode.C389.getValue())) {
				return factory.createCONTRATO300TYPE();
			} else if (code.equals(ContractCode.C401.getValue())) {
				return factory.createCONTRATO401TYPE();
			} else if (code.equals(ContractCode.C402.getValue())) {
				return factory.createCONTRATO402TYPE();
			} else if (code.equals(ContractCode.C403.getValue())) {
				return factory.createCONTRATO403TYPE();
			} else if (code.equals(ContractCode.C410.getValue())) {
				return factory.createCONTRATO410TYPE();
			} else if (code.equals(ContractCode.C420.getValue())) {
				return factory.createCONTRATO420TYPE();
			} else if (code.equals(ContractCode.C421.getValue())) {
				return factory.createCONTRATO421TYPE();
			} else if (code.equals(ContractCode.C430.getValue())) {
				return factory.createCONTRATO430TYPE();
			} else if (code.equals(ContractCode.C441.getValue())) {
				return factory.createCONTRATO441TYPE();
			} else if (code.equals(ContractCode.C450.getValue())) {
				return factory.createCONTRATO450TYPE();
			} else if (code.equals(ContractCode.C452.getValue())) {
				return factory.createCONTRATO452TYPE();
			} else if (code.equals(ContractCode.C501.getValue())) {
				return factory.createCONTRATO501TYPE();
			} else if (code.equals(ContractCode.C502.getValue())) {
				return factory.createCONTRATO502TYPE();
			} else if (code.equals(ContractCode.C503.getValue())) {
				return factory.createCONTRATO503TYPE();
			} else if (code.equals(ContractCode.C510.getValue())) {
				return factory.createCONTRATO510TYPE();
			} else if (code.equals(ContractCode.C520.getValue())) {
				return factory.createCONTRATO520TYPE();
			} else if (code.equals(ContractCode.C530.getValue())) {
				return factory.createCONTRATO530TYPE();
			} else if (code.equals(ContractCode.C540.getValue())) {
				return factory.createCONTRATO540TYPE();
			} else if (code.equals(ContractCode.C541.getValue())) {
				return factory.createCONTRATO541TYPE();
			} else if (code.equals(ContractCode.C550.getValue())) {
				return factory.createCONTRATO550TYPE();
			} else if (code.equals(ContractCode.C552.getValue())) {
				return factory.createCONTRATO552TYPE();
			} else if (code.equals(ContractCode.C970.getValue())) {
				return factory.createCONTRATO970TYPE();
			} else if (code.equals(ContractCode.C980.getValue())) {
				return factory.createCONTRATO980TYPE();
			} else if (code.equals(ContractCode.C990.getValue())) {
				return factory.createCONTRATO990TYPE();
			}
			return null;
		} catch (Exception e) {
			throw new IllegalArgumentException(e.getMessage());
		}
	}

	public static IContratoType createCONTRATOS(EmployeeContractInfo employeeContractInfo) throws IllegalArgumentException {
		String tc2 = employeeContractInfo.getContractInfo().getContractType();
		try {
			if(AonStringUtils.isBlank(tc2))
				return null;
			IContratoType contratoType = createContratoModel(tc2);
		
			writeContratosMainData(contratoType, employeeContractInfo);
			
			if (tc2.equals(ContractCode.C100.getValue())) {
				contratoType = createContract100(contratoType, employeeContractInfo);
			} else if (tc2.equals(ContractCode.C109.getValue())) {
				contratoType = createContract100(contratoType, employeeContractInfo);
			} else if (tc2.equals(ContractCode.C130.getValue())) {
				contratoType = createContract130(contratoType, employeeContractInfo);
			} else if (tc2.equals(ContractCode.C139.getValue())) {
				contratoType = createContract130(contratoType, employeeContractInfo);
			} else if (tc2.equals(ContractCode.C150.getValue())) {
				contratoType = createContract150(contratoType, employeeContractInfo);
			} else if (tc2.equals(ContractCode.C189.getValue())) {
				contratoType = createContract189(contratoType, employeeContractInfo);
			} else if (tc2.equals(ContractCode.C200.getValue())) {
				contratoType = createContract200(contratoType, employeeContractInfo);
			} else if (tc2.equals(ContractCode.C209.getValue())) {
				contratoType = createContract200(contratoType, employeeContractInfo);
			} else if (tc2.equals(ContractCode.C230.getValue())) {
				contratoType = createContract230(contratoType, employeeContractInfo);
			} else if (tc2.equals(ContractCode.C239.getValue())) {
				contratoType = createContract230(contratoType, employeeContractInfo);
			} else if (tc2.equals(ContractCode.C250.getValue())) {
				contratoType = createContract250(contratoType, employeeContractInfo);
			} else if (tc2.equals(ContractCode.C289.getValue())) {
				contratoType = createContract289(contratoType, employeeContractInfo);
			} else if (tc2.equals(ContractCode.C300.getValue())) {
				contratoType = createContract300(contratoType, employeeContractInfo);
			} else if (tc2.equals(ContractCode.C309.getValue())) {
				contratoType = createContract300(contratoType, employeeContractInfo);
			} else if (tc2.equals(ContractCode.C330.getValue())) {
				contratoType = createContract330(contratoType, employeeContractInfo);
			} else if (tc2.equals(ContractCode.C339.getValue())) {
				contratoType = createContract330(contratoType, employeeContractInfo);
			} else if (tc2.equals(ContractCode.C350.getValue())) {
				contratoType = createContract350(contratoType, employeeContractInfo);
			} else if (tc2.equals(ContractCode.C389.getValue())) {
				contratoType = createContract300(contratoType, employeeContractInfo);
			} else if (tc2.equals(ContractCode.C401.getValue())) {
				contratoType = createContract401(contratoType, employeeContractInfo);
			} else if (tc2.equals(ContractCode.C402.getValue())) {
				contratoType = createContract402(contratoType, employeeContractInfo);
			} else if (tc2.equals(ContractCode.C403.getValue())) {
				contratoType = createContract403(contratoType, employeeContractInfo);
			} else if (tc2.equals(ContractCode.C410.getValue())) {
				contratoType = createContract410(contratoType, employeeContractInfo);
			} else if (tc2.equals(ContractCode.C420.getValue())) {
				contratoType = createContract420(contratoType, employeeContractInfo);
			} else if (tc2.equals(ContractCode.C421.getValue())) {
				contratoType = createContract421(contratoType, employeeContractInfo);
			} else if (tc2.equals(ContractCode.C430.getValue())) {
				contratoType = createContract430(contratoType, employeeContractInfo);
			} else if (tc2.equals(ContractCode.C441.getValue())) {
				contratoType = createContract441(contratoType, employeeContractInfo);
			} else if (tc2.equals(ContractCode.C450.getValue())) {
				contratoType = createContract450(contratoType, employeeContractInfo);
			} else if (tc2.equals(ContractCode.C452.getValue())) {
				contratoType = createContract452(contratoType, employeeContractInfo);
			} else if (tc2.equals(ContractCode.C501.getValue())) {
				contratoType = createContract501(contratoType, employeeContractInfo);
			} else if (tc2.equals(ContractCode.C502.getValue())) {
				contratoType = createContract502(contratoType, employeeContractInfo);
			} else if (tc2.equals(ContractCode.C503.getValue())) {
				contratoType = createContract503(contratoType, employeeContractInfo);
			} else if (tc2.equals(ContractCode.C510.getValue())) {
				contratoType = createContract510(contratoType, employeeContractInfo);
			} else if (tc2.equals(ContractCode.C520.getValue())) {
				contratoType = createContract520(contratoType, employeeContractInfo);
			} else if (tc2.equals(ContractCode.C530.getValue())) {
				contratoType = createContract530(contratoType, employeeContractInfo);
			} else if (tc2.equals(ContractCode.C540.getValue())) {
				contratoType = createContract540(contratoType, employeeContractInfo);
			} else if (tc2.equals(ContractCode.C541.getValue())) {
				contratoType = createContract541(contratoType, employeeContractInfo);
			} else if (tc2.equals(ContractCode.C550.getValue())) {
				contratoType = createContract550(contratoType, employeeContractInfo);
			} else if (tc2.equals(ContractCode.C552.getValue())) {
				contratoType = createContract552(contratoType, employeeContractInfo);
			} else if (tc2.equals(ContractCode.C970.getValue())) {
				contratoType = createContract970(contratoType, employeeContractInfo);
			} else if (tc2.equals(ContractCode.C980.getValue())) {
				contratoType = createContract980(contratoType, employeeContractInfo);
			} else if (tc2.equals(ContractCode.C990.getValue())) {
				contratoType = createContract990(contratoType, employeeContractInfo);
			}
			
			return contratoType;
		} catch (Exception e) {
			if(AonStringUtils.isNotBlank(tc2) && AonStringUtils.equals(tc2.substring(tc2.length() - 1), "9"))
				return null;
			throw new IllegalArgumentException(e.getMessage());
		}
	}
	
	private static void writeContratosMainData(IContratoType contratos, EmployeeContractInfo employeeContractInfo) throws IllegalArgumentException{
		try {
			contratos.setDATOSEMPRESA(createDatosEmpresa(employeeContractInfo));
			contratos.setDATOSTRABAJADOR(createDatosTrabajador(employeeContractInfo));
			contratos.setDATOSGENERALESCONTRATO(createDatosGeneralesContrato(employeeContractInfo)); 
			if(employeeContractInfo.getContractSpecificData().getTemporalWorkEnterprise()){
				contratos.setDATOSETT(createDatosEtt(employeeContractInfo));
			}
			contratos.setDATOSCOMUNICACOPIABASICA(createDatosComunicacionCopiaBasica(employeeContractInfo)); 
			contratos.setDATOSUSOLIBREEMPRESA(createDatosUsoLibreEmpresa(employeeContractInfo));
		} catch (Exception e) {
			throw new IllegalArgumentException(e.getMessage());
		}
	}
	
	private static DATOSTRABAJADORTYPE createDatosTrabajador(EmployeeContractInfo employeeContractInfo) throws ManagerBeanException {
		DATOSTRABAJADORTYPE datos = new DATOSTRABAJADORTYPE(); 
		if(null != employeeContractInfo.getEmployeeInfo().getBirthdate()){
			datos.setFECHANACIMIENTO(getFormatedDate(employeeContractInfo.getEmployeeInfo().getBirthdate()));
		} else {
//			AonUtil.addErrorMessage("El trabajador no tiene definida la fecha de nacimiento.");
		}
		if(AonStringUtils.isEmpty(employeeContractInfo.getEmployeeInfo().getDocument())){
//			AonUtil.addErrorMessage("El trabajador no tiene definido el nmero de documento.");
		} else {
			// FIXME
			/*
			"D";"D.N.I"
			"E";"NUMERO IDENTIFICATIVO EXTRANJERO"
			"U";"CIUDADANOS DE LA UE/EEE SIN NIE"
			"W";"CIUD.QUE NO PERTENECEN A UE/EEE.SIN NIE"
			 */
			if(employeeContractInfo.getEmployeeInfo().getDocumentType() == (byte) 0){
				datos.setIDENTIFICADORPFISICA("D"+employeeContractInfo.getEmployeeInfo().getDocument());
			} else if(employeeContractInfo.getEmployeeInfo().getDocumentType() == (byte) 1){
				datos.setIDENTIFICADORPFISICA("E"+employeeContractInfo.getEmployeeInfo().getDocument());
			}
		}
		datos.setNACIONALIDAD("724");
		datos.setNOMBREAPELLIDOS(createNombreApellidos(employeeContractInfo));
		if(AonStringUtils.isEmpty(employeeContractInfo.getEmployeeInfo().getSsNumber())){
//			AonUtil.addErrorMessage("El trabajador no tiene definido el nmero de seguridad social.");
		} else {
			datos.setNUMEROSEGURIDADSOCIAL(employeeContractInfo.getEmployeeInfo().getSsNumber());
		}
		if(employeeContractInfo.getEmployeeInfo().getAddressProvinces()!=null){
			if(employeeContractInfo.getEmployeeInfo().getAddressProvinces()!=null){
				datos.setPAISRESIDENCIA("724");
			}
			if(employeeContractInfo.getEmployeeInfo().getAddressProvinces()!=null){
				datos.setMUNICIPIORESIDENCIA(employeeContractInfo.getEmployeeInfo().getAddressCity());
			} else {
//				AonUtil.addErrorMessage("El trabajador no tiene definido el municipio de residencia.");
			}
		} else {
//			AonUtil.addErrorMessage("El trabajador no tiene definida la direccin.");
		}
		if(employeeContractInfo.getEmployeeInfo().getGender()==null){
//			AonUtil.addErrorMessage("El sexo del trabajador es desconocido.");
		} else {
			datos.setSEXO(employeeContractInfo.getEmployeeInfo().getGender() == (byte)1 ? "0" : "1");
		}
		return datos;
	}
	
	private static NOMBREAPELLIDOSTYPE createNombreApellidos(EmployeeContractInfo employeeContractInfo) {
		NOMBREAPELLIDOSTYPE datos = new NOMBREAPELLIDOSTYPE();
		
		String name = AonStringUtils.trimToEmpty(employeeContractInfo.getEmployeeInfo().getName());
		String firstSurname = AonStringUtils.trimToEmpty(employeeContractInfo.getEmployeeInfo().getSurName());
		String secondSurname = AonStringUtils.trimToEmpty(employeeContractInfo.getEmployeeInfo().getSecondSurName());
		
		name = name.length()>15?name.substring(0, 15):name;
		firstSurname = firstSurname.length()>20?firstSurname.substring(0, 20):firstSurname;
		secondSurname = secondSurname.length()>20?secondSurname.substring(0, 20):secondSurname;
		
		datos.setNOMBRE(name);
		datos.setPRIMERAPELLIDO(firstSurname);
		datos.setSEGUNDOAPELLIDO(secondSurname);
		
		return datos;
	}
	
	private static DATOSGENERALESCONTRATOTYPE createDatosGeneralesContrato(EmployeeContractInfo employeeContractInfo) throws IllegalArgumentException {
		try {
			String tc2 = employeeContractInfo.getContractInfo().getContractType();
			DATOSGENERALESCONTRATOTYPE datos = new DATOSGENERALESCONTRATOTYPE();
			datos.setFECHAINICIO(getFormatedDate(employeeContractInfo.getContractInfo().getStartDate()));
			if(null != employeeContractInfo.getContractInfo().getEndDate())
				datos.setFECHATERMINO(getFormatedDate(employeeContractInfo.getContractInfo().getEndDate()));
			
			// TODO: mirar esto el dia de mañana para ver si es necesario
//			if( employeeContractInfo.getContractInfo().getEndDate()==null 
//					&& (tc2.equals("402") || tc2.equals("502") || tc2.equals("430") 
//					|| tc2.equals("530") || tc2.equals("420") || tc2.equals("520")  
//					|| tc2.equals("421") || tc2.equals("441") || tc2.equals("541") 
//					|| tc2.equals("452") || tc2.equals("552") || tc2.equals("970") ) ){
//				throw new IllegalArgumentException("La fecha final es necesaria para este tipo de contrato");
//	//			AonUtil.addErrorMessage("La fecha final es necesaria para este tipo de contrato.");
//			} else {
//				datos.setFECHATERMINO(getFormatedDate(employeeContractInfo.getContractInfo().getEndDate()));
//			}
	
	//		if(employeeContractInfo.getContractInfo().getEndDate()!=null){
	//			Integer durationInMonths = getMonthsBetweenDates(employeeContractInfo.getContractInfo().getStartDate(), employeeContractInfo.getContractInfo().getEndDate());
	//			if( ((tc2.equals("402") || tc2.equals("502")) && durationInMonths>=6 && durationInMonths<=12) 
	//					|| (tc2.equals("421") && durationInMonths>=24 && durationInMonths<=36) 
	//					|| ((tc2.equals("401") || tc2.equals("501") || tc2.equals("450") || tc2.equals("550")) && durationInMonths>=36 && durationInMonths<=48)  
	//					){
	//				datos.setINDCONVENIOCOLECTIVO(params.isCollectiveAgreement()?"S":"N");
	//			}
	//		}
			
			ContractSpecificData contractSpecificData = employeeContractInfo.getContractSpecificData();
			
			datos.setNIVELFORMATIVO(contractSpecificData.getFormativeLevel() != null ? contractSpecificData.getFormativeLevel() : null);
			if(contractSpecificData.getDisabilityB()){
				datos.setINDDISCAPACIDAD(contractSpecificData.getDisability() != null ? contractSpecificData.getDisability() : null);
			}
			
			String cno = contractSpecificData.getCno();
			if(AonStringUtils.isEmpty(cno)){
	//			AonUtil.addErrorMessage("El trabajador no tiene definido el cdigo de ocupacion (CNO).");
			} else {
				datos.setCODIGOOCUPACION(completeLength(cno, 8,  " ", true));
			}
			
			if(contractSpecificData.getOfferWorkData()){
				datos.setIDOFERTA(contractSpecificData.getOffer());
			}
			
			if(contractSpecificData.getWorkProgramData()){
				datos.setCODIGOPROGRAMAEMPLEO(contractSpecificData.getWorkProgram() != null ? contractSpecificData.getWorkProgram() : null);
			}
			
			datos.setNACIONALIDADCT("724");
			
			datos.setMUNICIPIOCT(employeeContractInfo.getContractInfo().getWorkplaceZIP());
			
			if(contractSpecificData.getOlderThan52()){
				datos.setOTRASLEGISLACIONES(contractSpecificData.getOtherLegislations());
			}
			
			//TODO: MIRAR ESTO PARA 430
	//		if( tc2.equals("430") || ( tc2.equals("530") && datos!=null && datos.getINDDISCAPACIDAD()!=null && !datos.getINDDISCAPACIDAD().equals("C") ) ){
	//			String subsidized = SEPEUtils.getInstance().getContractDataMap(employeeContractInfo.getContractInfo().getContractId()).get(ContextVariable.SUBSIDIZED.getName());
	//			datos.setTEMPORALMINUSVBONIFICADO(Boolean.parseBoolean(subsidized)?"S":"N");
	//		}
			
	//		Calendar formationStart = Calendar.getInstance();
	//		formationStart.set(2010, 5, 18);
	//		Calendar formationEnd = Calendar.getInstance();
	//		formationEnd.set(2011, 7, 30);
	//		if(tc2.equals("421") 
	//				&& getContract().getStartDate().after(formationStart.getTime()) 
	//				&& getContract().getStartDate().before(formationEnd.getTime())){
	//			String subsidized = SEPEUtils.getInstance().getContractDataMap(getContract()).get(ContextVariable.SUBSIDIZED.getName());
	//			datos.setFORMACIONBONIFICADO(Boolean.parseBoolean(subsidized)?"S":"N");
	//		}
			
			if(contractSpecificData.getCampaigns()){
				datos.setDATOSCAMPAÑAS(contractSpecificData.getCpCampaign()+contractSpecificData.getCodeCampaign()+contractSpecificData.getYearCampaign());
			}
	
			if(tc2.equals("401") || tc2.equals("501") || tc2.equals("450") || tc2.equals("550")){
				datos.setINDEMPRESAAAPPUNIVERSIDAD("N");
			}
			
			// Plan de Transformación, Recuperación y Resiliencia
			if(tc2.equals("420"))
				datos.setPROYECTOEMPLEOFORMACION(contractSpecificData.getPlanRecovery() ? "S" : "N");
					
			return datos;
		} catch (Exception e) {
			throw new IllegalArgumentException(e.getMessage());
		}
	}
	
	private static DATOSETTTYPE createDatosEtt(EmployeeContractInfo employeeContractInfo) {
		DATOSETTTYPE datos = new DATOSETTTYPE();
		if(employeeContractInfo.getContractSpecificData().getTemporalWorkEnterprise()) {
			datos.setCIFNIFEMPRESAUSUARIA(employeeContractInfo.getContractSpecificData().getNif()!=null?createCifNif(employeeContractInfo.getContractSpecificData().getNif()):null);
			datos.setRAZONSOCIALEMPRESAUSUARIA(employeeContractInfo.getContractSpecificData().getSocialReason());
			Boolean contractTemplate = employeeContractInfo.getContractSpecificData().getContractTemplate();
			datos.setINDCTOPLANTILLA(null != contractTemplate && contractTemplate ? "S" : null);
			datos.setINDEMPRESAEXTRANJERA(null != contractTemplate && contractTemplate ? "S" : null);
		}
		return datos;
	}

	private static DATOSCOMUNICACOPIABASICATYPE createDatosComunicacionCopiaBasica(EmployeeContractInfo employeeContractInfo) {
		DATOSCOMUNICACOPIABASICATYPE datos = new DATOSCOMUNICACOPIABASICATYPE();
		datos.setDOMICCENTROTRABAJO(employeeContractInfo.getContractInfo().getWorkplaceFullAddress());
		datos.setTEXTOCOPIABASICA(employeeContractInfo.getContractSpecificData().getBasicCopy());
		datos.setTIPOFIRMA(employeeContractInfo.getContractSpecificData().getSignBasicCopy()!=null?employeeContractInfo.getContractSpecificData().getSignBasicCopy():null);
		return datos;
	}
	
	private static DATOSUSOLIBREEMPRESATYPE createDatosUsoLibreEmpresa(EmployeeContractInfo employeeContractInfo) {
		if(AonStringUtils.isNotBlank(employeeContractInfo.getContractSpecificData().getUseEnterpriseFree())){
			DATOSUSOLIBREEMPRESATYPE datos = new DATOSUSOLIBREEMPRESATYPE();
			datos.setUSOLIBREEMPRESA(employeeContractInfo.getContractSpecificData().getUseEnterpriseFree());
			return datos;
		}
		return null;
	}
	
	private static CIFNIFTYPE createCifNif(String cif) {
		CIFNIFTYPE datos = new CIFNIFTYPE();
		datos.setCIFNIF(cif);
		return datos;
	}
	
	private static String getFormatedDate(Date date){
		String pattern = "yyyyMMdd";
		if(date!=null){
			return DateFormatUtils.format(date, pattern);
		}
		return null;
	}
	
	
	private static DATOSEMPRESATYPE createDatosEmpresa(EmployeeContractInfo employeeContractInfo) throws ManagerBeanException {
		DATOSEMPRESATYPE datos = new DATOSEMPRESATYPE();
		datos.setCIFNIFEMPRESA(createCifNif(employeeContractInfo.getContractInfo().getEnterpriseCIF()));
		datos.setCODIGOCUENTACOTIZACION(employeeContractInfo.getContractInfo().getCompleteCCC());
		return datos;
	}
	
	private static CONTRATO100TYPE createContract100(IContratoType contratoType, EmployeeContractInfo employeeContractInfo) throws ManagerBeanException{
		CONTRATO100TYPE c = (CONTRATO100TYPE) contratoType;
		c.setDATOSMEDIDASFOMENTO(createDatosMedidasFomento(employeeContractInfo));
		if(employeeContractInfo.getContractSpecificData().getAnnexedB()){
			c.setDATOSANEXOCONTRATORELEVO(createDatosAnexoContratoRelevo(employeeContractInfo));
		}
		if(employeeContractInfo.getContractSpecificData().getWorkshopSchoolB()){
			c.setDATOSETCOTE(createDatosEtCote(employeeContractInfo));
		}
		c.setDATOSCONTRATOEXTRANJERO(createDatosContratoExtranjero(employeeContractInfo));
		return c;
	}
	
	private static DATOSCONTRATOEXTRANJEROTYPE createDatosContratoExtranjero(EmployeeContractInfo employeeContractInfo) throws IllegalArgumentException {
		try {
			if(employeeContractInfo.getContractSpecificData().getAnnexedB()){
				DATOSCONTRATOEXTRANJEROTYPE datos = new DATOSCONTRATOEXTRANJEROTYPE();
				datos.setINDCARACTEROFERTA(employeeContractInfo.getContractSpecificData().getAnnexed() ? "E" : "T");
				datos.setAÑOCONTINGENTE(employeeContractInfo.getContractSpecificData().getSourceYear());
				return datos;
			}
			return null;
		} catch (Exception e) {
			throw new IllegalArgumentException(e.getMessage());
		}
	}
	
	private static DATOSMEDIDASFOMENTOTYPE createDatosMedidasFomento(EmployeeContractInfo employeeContractInfo) {
		if(employeeContractInfo.getContractSpecificData().getPromotionMeasures()){
			DATOSMEDIDASFOMENTOTYPE datos = new DATOSMEDIDASFOMENTOTYPE();
			datos.setINDCOSTEDESPIDO(employeeContractInfo.getContractSpecificData().getPromotionPermanentHiring()?"1":"2");
//			if(params.isIndCosteDespido()){
//				datos.setCODIGOCOLECTIVODESPIDO(params.getCodigoColectivoDespido()!=null?params.getCodigoColectivoDespido().getCode():null);
//			} else {
//				datos.setCODIGOCOLECTIVODESPIDO(null);
//			}
			return datos;
		}
		return null;
	}
	
	private static DATOSANEXOCONTRATORELEVOTYPE createDatosAnexoContratoRelevo(EmployeeContractInfo employeeContractInfo) {
		if(employeeContractInfo.getContractSpecificData().getContractRelief()){
			DATOSANEXOCONTRATORELEVOTYPE datos = new DATOSANEXOCONTRATORELEVOTYPE();
			datos.setTIPOTRABAJADOR(employeeContractInfo.getContractSpecificData().getReliefEmployee());
			datos.setNOMBREAPELLIDOS(createNombreApellidosRelief(employeeContractInfo));
			return datos;
		}
		return null;
	}
	
	private static NOMBREAPELLIDOSTYPE createNombreApellidosRelief(EmployeeContractInfo employeeContractInfo) {
		NOMBREAPELLIDOSTYPE datos = new NOMBREAPELLIDOSTYPE();
		String name = AonStringUtils.trimToEmpty(employeeContractInfo.getContractSpecificData().getRetirementName());
		String firstSurname = AonStringUtils.trimToEmpty(employeeContractInfo.getContractSpecificData().getRetirementSurname());
		String secondSurname = AonStringUtils.trimToEmpty(employeeContractInfo.getContractSpecificData().getRetirementSurname2());
		
		name = name.length()>15?name.substring(0, 15):name;
		firstSurname = firstSurname.length()>20?firstSurname.substring(0, 20):firstSurname;
		secondSurname = secondSurname.length()>20?secondSurname.substring(0, 20):secondSurname;
		
		datos.setNOMBRE(name);
		datos.setPRIMERAPELLIDO(firstSurname);
		datos.setSEGUNDOAPELLIDO(secondSurname);
		return datos;
	}
	
	private static DATOSETCOTYPE createDatosEtCote(EmployeeContractInfo employeeContractInfo) throws IllegalArgumentException {
		try {
			if(employeeContractInfo.getContractSpecificData().getWorkshopSchoolB()){
				DATOSETCOTYPE datos = new DATOSETCOTYPE();
				datos.setCODIGOETCOTE(employeeContractInfo.getContractSpecificData().getWorkshopSchool());
				return datos;
			}
			return null;
		} catch (Exception e) {
			throw new IllegalArgumentException(e.getMessage());
		}
	}
	
	private static CONTRATO130TYPE createContract130(IContratoType contratoType, EmployeeContractInfo employeeContractInfo) throws ManagerBeanException{
		CONTRATO130TYPE c = (CONTRATO130TYPE) contratoType;
	    c.setDATOSBONIFICACION(createDatosBonificacion(employeeContractInfo));
	    c.setDATOSMEDIDASFOMENTO(createDatosMedidasFomento(employeeContractInfo));
	    c.setDATOSANEXOCONTRATORELEVO(createDatosAnexoContratoRelevo(employeeContractInfo));
	    c.setDATOSETCOTE(createDatosEtCote(employeeContractInfo));
	    return c;
	}
	
	private static DATOSBONIFICACIONTYPE createDatosBonificacion(EmployeeContractInfo employeeContractInfo) {
		if(employeeContractInfo.getContractSpecificData().getEntrepreneurSupport() || employeeContractInfo.getContractSpecificData().getDisabilityB()
				 || employeeContractInfo.getContractSpecificData().getBonus()){
			DATOSBONIFICACIONTYPE datos = new DATOSBONIFICACIONTYPE();
//			if( params.getIndDiscapacidad()!=TEJINDIS.TEJINDIS_C ){
			
				if(AonStringUtils.isNotBlank(employeeContractInfo.getContractSpecificData().getBonusColective())){
					datos.setCODIGOCOLECTIVOBONIF(employeeContractInfo.getContractSpecificData().getBonusColective());
				}
				
				if(AonStringUtils.isNotBlank(employeeContractInfo.getContractSpecificData().getBonusType())){
					datos.setCODIGOCOLECTIVOBONIF(employeeContractInfo.getContractSpecificData().getBonusType());
				}
//			}
			
			String tc2 = employeeContractInfo.getContractInfo().getContractType();
			if( tc2.equals("150") || tc2.equals("250") || tc2.equals("350") ){
				if(employeeContractInfo.getContractSpecificData().getFreelanceEmployeer()!=null){
					datos.setINDICEMPLEADAUTONOMO(employeeContractInfo.getContractSpecificData().getFreelanceEmployeer()?"1":"2");
				}
			}
			return datos;
		}
		return null;
	}
	
	private static CONTRATO150TYPE createContract150(IContratoType contratoType, EmployeeContractInfo employeeContractInfo) throws ManagerBeanException{
		CONTRATO150TYPE c = (CONTRATO150TYPE) contratoType;
		c.setDATOSBONIFICACION(createDatosBonificacion(employeeContractInfo));
		c.setDATOSMEDIDASFOMENTO(createDatosMedidasFomento(employeeContractInfo));
		c.setDATOSANEXOCONTRATORELEVO(createDatosAnexoContratoRelevo(employeeContractInfo));
		c.setDATOSETCOTE(createDatosEtCote(employeeContractInfo));
		c.setDATOSEMPRESAINSERCION(createDatosEmpresaInsercion(employeeContractInfo));
		return c;
	}
	
	private static CONTRATO100TYPE createContract189(IContratoType contratoType, EmployeeContractInfo employeeContractInfo) throws ManagerBeanException{
		CONTRATO100TYPE c = (CONTRATO100TYPE) contratoType;
		c.setDATOSMEDIDASFOMENTO(createDatosMedidasFomento(employeeContractInfo));
		if(employeeContractInfo.getContractSpecificData().getAnnexedB()){
			c.setDATOSANEXOCONTRATORELEVO(createDatosAnexoContratoRelevo(employeeContractInfo));
		}
		if(employeeContractInfo.getContractSpecificData().getWorkshopSchoolB()){
			c.setDATOSETCOTE(createDatosEtCote(employeeContractInfo));
		}
		c.setDATOSCONTRATOEXTRANJERO(createDatosContratoExtranjero(employeeContractInfo));
		return c;
	}
	
	private static DATOSEMPRESAINSERCIONTYPE createDatosEmpresaInsercion(EmployeeContractInfo employeeContractInfo) {
//		DATOSEMPRESAINSERCIONTYPE datos = new DATOSEMPRESAINSERCIONTYPE();
//		String tc2 = getContractDataMap(getContract()).get(ContextVariable.TC2.getName());
//		if( tc2.equals("452") || tc2.equals("552") ){
//			datos.setINDEMPRESAINSERCION("S");
//			datos.setINDEMPRESAINSERCION("N");
//			if(contract_duration between 6 and 12 motnh){
//				datos.setINDDURACINFERIOR("S");
//				datos.setINDDURACINFERIOR("N");
//			}
//		}
//		return datos;
		return null;
	}
	
	private static CONTRATO200TYPE createContract200(IContratoType contratoType, EmployeeContractInfo employeeContractInfo) throws ManagerBeanException{
		CONTRATO200TYPE c = (CONTRATO200TYPE) contratoType;
		c.setDATOSMEDIDASFOMENTO(createDatosMedidasFomento(employeeContractInfo));
		c.setDATOSANEXOCONTRATORELEVO(createDatosAnexoContratoRelevo(employeeContractInfo));
		c.setDATOSETCOTE(createDatosEtCote(employeeContractInfo));
		c.setDATOSCONTRATOEXTRANJERO(createDatosContratoExtranjero(employeeContractInfo));
		c.setDATOSREDUCCIONRDL12011(createDatosReduccionRdl2011(employeeContractInfo));
		c.setDATOSCONTRATOTIEMPOPARCIAL(createDatosContratoTiempoParcial(employeeContractInfo));
		return c;
	}
	
	private static DATOSCONTRATOTIEMPOPARCIALTYPE createDatosContratoTiempoParcial(EmployeeContractInfo employeeContractInfo) {
		// TODO
		String tc2 = employeeContractInfo.getContractInfo().getContractType();
		DATOSCONTRATOTIEMPOPARCIALTYPE datos = new DATOSCONTRATOTIEMPOPARCIALTYPE();
//		datos.setACTIVIDADSINFECHACIERTA(params.getActividadSinFechaCierta());
//		datos.setCOLECTIVOEDAD(params.getColectivoEdad()!=null?params.getColectivoEdad().getCode():null);
		if( tc2.equals("200") || tc2.equals("230") || tc2.equals("250") ){
			datos.setFIJODISCONTINUOPERIODICO(employeeContractInfo.getContractSpecificData().getRepeatFD()!=null && employeeContractInfo.getContractSpecificData().getRepeatFD()?"S":"N");
		}
//		datos.setHORASANUALESTIEMPOCOMPLETO(params.getHorasAnualesTiempoCompleto());
		
		String duracionconvenio = AonStringUtils.leftPad(AonStringUtils.isBlank(employeeContractInfo.getContractSpecificData().getAgreementHours()) ? "" : employeeContractInfo.getContractSpecificData().getAgreementHours(), 4, '0')+AonStringUtils.leftPad(AonStringUtils.isBlank(employeeContractInfo.getContractSpecificData().getAgreementMinutes()) ? "" : employeeContractInfo.getContractSpecificData().getAgreementMinutes(), 2, '0');
		String duracionformacion = AonStringUtils.leftPad(AonStringUtils.isBlank(employeeContractInfo.getContractSpecificData().getFormationHours()) ? "" : employeeContractInfo.getContractSpecificData().getFormationHours(), 4, '0')+AonStringUtils.leftPad(AonStringUtils.isBlank(employeeContractInfo.getContractSpecificData().getFormationMinutes()) ? "" : employeeContractInfo.getContractSpecificData().getFormationMinutes(), 2, '0');
		String duracionjornada = AonStringUtils.leftPad(AonStringUtils.isBlank(employeeContractInfo.getContractSpecificData().getJourneyDurationHours()) ? "" : employeeContractInfo.getContractSpecificData().getJourneyDurationHours(), 4, '0')+AonStringUtils.leftPad(AonStringUtils.isBlank(employeeContractInfo.getContractSpecificData().getJourneyDurationMinutes()) ? "" : employeeContractInfo.getContractSpecificData().getJourneyDurationMinutes(), 2, '0');
		
		datos.setHORASCONVENIO(duracionconvenio.isEmpty()?null:completeLength(duracionconvenio, 6, "0", false));
		datos.setHORASFORMACION(duracionformacion.isEmpty()?null:completeLength(duracionformacion, 6, "0", false));
		datos.setHORASJORNADA(duracionjornada.isEmpty()?null:completeLength(duracionjornada, 6, "0", false));
		
		if( tc2.equals("421") && duracionformacion.isEmpty() ){
			datos.setINDICFORMACIONTEORICA(employeeContractInfo.getContractSpecificData().getTeoricFormation()?"S":"N");
		}
		
		datos.setPORCENTAJEJUBILACIONPARCIAL(employeeContractInfo.getContractSpecificData().getRetirementPercent());
//		datos.setPORCJORNADAPACTADA(employeeContractInfo.getContractSpecificData().getJourneyPercent());
		datos.setTIPOJORNADA(employeeContractInfo.getContractSpecificData().getJourneyType());
		return datos;
	}
	
	private static String completeLength(String value, Integer length, String appendValue, boolean rightAppend) {
		if(value==null)return null;
		StringBuilder builder = new StringBuilder("");
		if(rightAppend){
			builder.append(value);
		}
		for(int i=value.length(); i<length; i++){
			builder.append(appendValue);
		}
		if(!rightAppend){
			builder.append(value);
		}
		return builder.toString();
	}
	
	private static DATOSREDUCCIONRDL12011TYPE createDatosReduccionRdl2011(EmployeeContractInfo employeeContractInfo) {
		DATOSREDUCCIONRDL12011TYPE datos = new DATOSREDUCCIONRDL12011TYPE();
		if(employeeContractInfo.getContractSpecificData().getQuoteReductions()){
			if(employeeContractInfo.getContractSpecificData().getReductionColective()!=null){
				datos.setCODIGOCOLECTIVOREDUCCION(employeeContractInfo.getContractSpecificData().getReductionColective());
			}
			datos.setPORCENTAJEREDUCCION(employeeContractInfo.getContractSpecificData().getQuoteReduction() ? "75" : "100");
			if(employeeContractInfo.getContractSpecificData().getJourneyPercent()!=null){
				datos.setPORCENTAJEJORNADAREDUCCION(completeLength(fixPercent(employeeContractInfo.getContractSpecificData().getJourneyPercent()), 4, "0", true));
			}
			return datos;
		}
		return null;
	}
	
	private static String fixPercent(String journeyPercent) {
		return journeyPercent.split("\\.")[0];
	}

	private static CONTRATO230TYPE createContract230(IContratoType contratoType, EmployeeContractInfo employeeContractInfo) throws ManagerBeanException{
		CONTRATO230TYPE c = (CONTRATO230TYPE) contratoType;
		c.setDATOSCONTRATOTIEMPOPARCIAL(createDatosContratoTiempoParcial(employeeContractInfo));
		c.setDATOSBONIFICACION(createDatosBonificacion(employeeContractInfo));
		c.setDATOSMEDIDASFOMENTO(createDatosMedidasFomento(employeeContractInfo));
		c.setDATOSANEXOCONTRATORELEVO(createDatosAnexoContratoRelevo(employeeContractInfo));
		c.setDATOSETCOTE(createDatosEtCote(employeeContractInfo));
		c.setDATOSREDUCCIONRDL12011(createDatosReduccionRdl2011(employeeContractInfo));
		return c;
	}
	private static CONTRATO250TYPE createContract250(IContratoType contratoType, EmployeeContractInfo employeeContractInfo) throws ManagerBeanException{
		CONTRATO250TYPE c = (CONTRATO250TYPE) contratoType;
		c.setDATOSCONTRATOTIEMPOPARCIAL(createDatosContratoTiempoParcial(employeeContractInfo));
		c.setDATOSBONIFICACION(createDatosBonificacion(employeeContractInfo));
		c.setDATOSMEDIDASFOMENTO(createDatosMedidasFomento(employeeContractInfo));
		c.setDATOSANEXOCONTRATORELEVO(createDatosAnexoContratoRelevo(employeeContractInfo));
		c.setDATOSETCOTE(createDatosEtCote(employeeContractInfo));
		c.setDATOSEMPRESAINSERCION(createDatosEmpresaInsercion(employeeContractInfo));
		c.setDATOSREDUCCIONRDL12011(createDatosReduccionRdl2011(employeeContractInfo));
		return c;
	}
	
	private static CONTRATO200TYPE createContract289(IContratoType contratoType, EmployeeContractInfo employeeContractInfo) throws ManagerBeanException{
		CONTRATO200TYPE c = (CONTRATO200TYPE) contratoType;
		c.setDATOSMEDIDASFOMENTO(createDatosMedidasFomento(employeeContractInfo));
		c.setDATOSANEXOCONTRATORELEVO(createDatosAnexoContratoRelevo(employeeContractInfo));
		c.setDATOSETCOTE(createDatosEtCote(employeeContractInfo));
		c.setDATOSCONTRATOEXTRANJERO(createDatosContratoExtranjero(employeeContractInfo));
		c.setDATOSREDUCCIONRDL12011(createDatosReduccionRdl2011(employeeContractInfo));
		c.setDATOSCONTRATOTIEMPOPARCIAL(createDatosContratoTiempoParcial(employeeContractInfo));
		return c;
	}
	
	private static CONTRATO300TYPE createContract300(IContratoType contratoType, EmployeeContractInfo employeeContractInfo) throws ManagerBeanException{
		CONTRATO300TYPE c = (CONTRATO300TYPE) contratoType;
	    c.setDATOSCONTRATOTIEMPOPARCIAL(createDatosContratoTiempoParcial(employeeContractInfo));
	    c.setDATOSMEDIDASFOMENTO(createDatosMedidasFomento(employeeContractInfo));
	    c.setDATOSANEXOCONTRATORELEVO(createDatosAnexoContratoRelevo(employeeContractInfo));
	    c.setDATOSETCOTE(createDatosEtCote(employeeContractInfo));
	    c.setDATOSREDUCCIONRDL12011(createDatosReduccionRdl2011(employeeContractInfo));
	    return c;
	}
	private static CONTRATO330TYPE createContract330(IContratoType contratoType, EmployeeContractInfo employeeContractInfo) throws ManagerBeanException{
		CONTRATO330TYPE c = (CONTRATO330TYPE) contratoType;
		c.setDATOSCONTRATOTIEMPOPARCIAL(createDatosContratoTiempoParcial(employeeContractInfo));
		c.setDATOSBONIFICACION(createDatosBonificacion(employeeContractInfo));
		c.setDATOSMEDIDASFOMENTO(createDatosMedidasFomento(employeeContractInfo));
		c.setDATOSANEXOCONTRATORELEVO(createDatosAnexoContratoRelevo(employeeContractInfo));
		c.setDATOSETCOTE(createDatosEtCote(employeeContractInfo));
		c.setDATOSREDUCCIONRDL12011(createDatosReduccionRdl2011(employeeContractInfo));
		return c;
	}
	private static CONTRATO350TYPE createContract350(IContratoType contratoType, EmployeeContractInfo employeeContractInfo) throws ManagerBeanException{
		CONTRATO350TYPE c = (CONTRATO350TYPE) contratoType;
		c.setDATOSCONTRATOTIEMPOPARCIAL(createDatosContratoTiempoParcial(employeeContractInfo));
		c.setDATOSBONIFICACION(createDatosBonificacion(employeeContractInfo));
		c.setDATOSMEDIDASFOMENTO(createDatosMedidasFomento(employeeContractInfo));
		c.setDATOSANEXOCONTRATORELEVO(createDatosAnexoContratoRelevo(employeeContractInfo));
		c.setDATOSETCOTE(createDatosEtCote(employeeContractInfo));
		c.setDATOSEMPRESAINSERCION(createDatosEmpresaInsercion(employeeContractInfo));
		c.setDATOSREDUCCIONRDL12011(createDatosReduccionRdl2011(employeeContractInfo));
		return c;
	}
	private static CONTRATO401TYPE createContract401(IContratoType contratoType, EmployeeContractInfo employeeContractInfo) throws ManagerBeanException{
		CONTRATO401TYPE c = (CONTRATO401TYPE) contratoType;
		c.setDATOSETCOTE(createDatosEtCote(employeeContractInfo));
		c.setDATOSCONTRATOINVESTIGACION(createDatosContratoInvestigacion(employeeContractInfo));
		c.setPROGEMPLEOPUBLICO(createDatosProgramaEmpleoPublico(employeeContractInfo));
		c.setDATOSCONTRATOEXTRANJERO(createDatosContratoExtranjero(employeeContractInfo));
		return c;
	}
	
	private static DATOSCONTRATOINVESTIGACIONTYPE createDatosContratoInvestigacion(EmployeeContractInfo employeeContractInfo) {
		if(employeeContractInfo.getContractSpecificData().getInvest()){
			DATOSCONTRATOINVESTIGACIONTYPE datos = new  DATOSCONTRATOINVESTIGACIONTYPE();
			if(employeeContractInfo.getContractSpecificData().getEmployer()!=null){
				datos.setINDEMPLEADOR(employeeContractInfo.getContractSpecificData().getEmployer());
			}
			if(employeeContractInfo.getContractSpecificData().getEmployee()!=null){
				datos.setINDTRABAJADOR(employeeContractInfo.getContractSpecificData().getEmployee());
			}
			datos.setINDRD632006(employeeContractInfo.getContractSpecificData().getResearcher()?"S":null);
			return datos;
		}
		return null;
	}
	
	private static CONTRATO402TYPE createContract402(IContratoType contratoType, EmployeeContractInfo employeeContractInfo) throws IllegalArgumentException{
		try {
			CONTRATO402TYPE c = (CONTRATO402TYPE) contratoType;
//			c.setDATOSETCOTE(createDatosEtCote(employeeContractInfo));
//			c.setDATOSCOPIABASICA(createDatosCopiaBasica(employeeContractInfo));
//			c.setPROGEMPLEOPUBLICO(createDatosProgramaEmpleoPublico(employeeContractInfo));
//			c.setDATOSCONTRATOEXTRANJERO(createDatosContratoExtranjero(employeeContractInfo));
			return c;
		} catch (Exception e) {
			throw new IllegalArgumentException(e.getMessage());
		}
	}
	
	private static DATOSPROGEMPLEOPUBLICOTYPE createDatosProgramaEmpleoPublico(EmployeeContractInfo employeeContractInfo) throws IllegalArgumentException {
		try {
			DATOSPROGEMPLEOPUBLICOTYPE datos = new DATOSPROGEMPLEOPUBLICOTYPE();
			datos.setACTUACION("");
			datos.setCORPORACIONLOCAL("");
			datos.setEJERCICIOPRESUPUESTARIO("");
			datos.setGRUPOCOTIZACIONCORPORACIONLOCAL("");
			return datos;
		} catch (Exception e) {
			throw new IllegalArgumentException(e.getMessage());
		}
	}
	
	private static DATOSCOPIABASICATYPE createDatosCopiaBasica(EmployeeContractInfo employeeContractInfo) throws IllegalArgumentException {
		try {
			DATOSCOPIABASICATYPE datos = null;
			SEPEUtils utils = SEPEUtils.getInstance();
			String tc2 = employeeContractInfo.getContractInfo().getContractType();
			if( tc2.equals("402") || tc2.equals("990") ){
				if( tc2.equals("990") ){
					datos = datos==null?new DATOSCOPIABASICATYPE():datos;
					datos.setINDCONTRATOALTADIRECCION("S");
				}
				Date start = utils.getDateWithResettedHours(employeeContractInfo.getContractInfo().getStartDate(),true);
				Date end = utils.getDateWithResettedHours(employeeContractInfo.getContractInfo().getEndDate(), false);
				if( tc2.equals("402") && end!=null && CommonUtil.getDaysBetweenDates(start, end) <= 28){
					datos = datos==null?new DATOSCOPIABASICATYPE():datos;
					datos.setINDCONTRATOESCRITO("S");
				}
			}
			return datos;
		} catch (Exception e) {
			throw new IllegalArgumentException(e.getMessage());
		}
	}
	
	private static CONTRATO403TYPE createContract403(IContratoType contratoType, EmployeeContractInfo employeeContractInfo) throws ManagerBeanException{
		CONTRATO403TYPE c = (CONTRATO403TYPE) contratoType;
		c.setPROGEMPLEOPUBLICO(createDatosProgramaEmpleoPublico(employeeContractInfo));
		c.setDATOSCONTRATOINSERCION(createDatosContratoInsercion(employeeContractInfo));
		return c;
	}
	
	private static DATOSCONTRATOINSERCIONTYPE createDatosContratoInsercion(EmployeeContractInfo employeeContractInfo) {
		// TODO
		DATOSCONTRATOINSERCIONTYPE datos = new DATOSCONTRATOINSERCIONTYPE();
		datos.setGRUPOCOTIZACIONSEGSOCIAL("");
		return datos;
	}
	
	private static CONTRATO410TYPE createContract410(IContratoType contratoType, EmployeeContractInfo employeeContractInfo) throws ManagerBeanException{
		CONTRATO410TYPE c = (CONTRATO410TYPE) contratoType;
		c.setDATOSCONTRATOINTERINIDAD(createDatosContratoInterinidad(employeeContractInfo));
		c.setDATOSETCOTE(createDatosEtCote(employeeContractInfo));
		c.setPROGEMPLEOPUBLICO(createDatosProgramaEmpleoPublico(employeeContractInfo));
		return c;
	}
	
	private static DATOSCONTRATOINTERINIDADTYPE createDatosContratoInterinidad(EmployeeContractInfo employeeContractInfo) {
		if(employeeContractInfo.getContractSpecificData().getIsInterimCause()){
			DATOSCONTRATOINTERINIDADTYPE datos = new DATOSCONTRATOINTERINIDADTYPE();
			if(employeeContractInfo.getContractSpecificData().getInterimCause()!=null){
				datos.setCAUSAINTERINIDAD(employeeContractInfo.getContractSpecificData().getInterimCause());
			}
			return datos;
		}
		return null;
	}
	
	private static CONTRATO420TYPE createContract420(IContratoType contratoType, EmployeeContractInfo employeeContractInfo) throws ManagerBeanException{
		CONTRATO420TYPE c = (CONTRATO420TYPE) contratoType;
		c.setDATOSCONTRATOPRACTICAS(createDatosContratoPracticas(employeeContractInfo));
		c.setDATOSETCOTE(createDatosEtCote(employeeContractInfo));
		c.setDATOSCONTRATOINVESTIGACION(createDatosContratoInvestigacion(employeeContractInfo));
		c.setPROGEMPLEOPUBLICO(createDatosProgramaEmpleoPublico(employeeContractInfo));
		return c;
	}
	
	private static DATOSCONTRATOPRACTICASTYPE createDatosContratoPracticas(EmployeeContractInfo employeeContractInfo) {
		DATOSCONTRATOPRACTICASTYPE datos = new DATOSCONTRATOPRACTICASTYPE();
		if(null != employeeContractInfo.getContractSpecificData().getAcademicTitulation() && AonStringUtils.isNotBlank(employeeContractInfo.getContractSpecificData().getAcademicTitulation()))
			datos.setTITULACIONACADEMICA(employeeContractInfo.getContractSpecificData().getAcademicTitulation());
		if(employeeContractInfo.getContractSpecificData().getProfesionality()!=null){
			datos.setINDCERTIFPROFESIONALIDAD(employeeContractInfo.getContractSpecificData().getProfesionality()?"S":"N");
		}
		return datos;
	}
	
	private static CONTRATO421TYPE createContract421(IContratoType contratoType, EmployeeContractInfo employeeContractInfo) throws ManagerBeanException{
		CONTRATO421TYPE c = (CONTRATO421TYPE) contratoType;
		c.setDATOSCONTRATOTIEMPOPARCIAL(createDatosContratoTiempoParcial(employeeContractInfo));
		c.setDATOSETCOTE(createDatosEtCote(employeeContractInfo));
		c.setPROGEMPLEOPUBLICO(createDatosProgramaEmpleoPublico(employeeContractInfo));
		c.setDATOSETT(createDatosEtt(employeeContractInfo));
		c.setDATOSREDUCCIONFORMACION(createDatosReduccionFormacion(employeeContractInfo));
		return c;
	}
	
	private static DATOSREDUCCIONFORMACIONTYPE createDatosReduccionFormacion(EmployeeContractInfo employeeContractInfo) {
		DATOSREDUCCIONFORMACIONTYPE datos = new DATOSREDUCCIONFORMACIONTYPE();
		if(employeeContractInfo.getContractSpecificData().getQuoteReductions()){
			datos.setCODIGOCOLECTIVOREDUCCIONFORMACION(employeeContractInfo.getContractSpecificData().getReductionColective());
			datos.setPORCENTAJEREDUCCIONFORMACION(employeeContractInfo.getContractSpecificData().getQuoteReduction()?"75":"100");
			return datos;
		}
		return null;
	}
	
	private static CONTRATO430TYPE createContract430(IContratoType contratoType, EmployeeContractInfo employeeContractInfo) throws ManagerBeanException{
		CONTRATO430TYPE c = (CONTRATO430TYPE) contratoType;
		c.setDATOSBONIFICACION(createDatosBonificacion(employeeContractInfo));
		c.setDATOSETCOTE(createDatosEtCote(employeeContractInfo));
		c.setPROGEMPLEOPUBLICO(createDatosProgramaEmpleoPublico(employeeContractInfo));
		return c;
	}
	private static CONTRATO441TYPE createContract441(IContratoType contratoType, EmployeeContractInfo employeeContractInfo) throws ManagerBeanException{
		CONTRATO441TYPE c = (CONTRATO441TYPE) contratoType;
		c.setDATOSANEXOCONTRATORELEVO(createDatosAnexoContratoRelevo(employeeContractInfo));
		c.setDATOSETCOTE(createDatosEtCote(employeeContractInfo));
		return c;
	}
	private static CONTRATO450TYPE createContract450(IContratoType contratoType, EmployeeContractInfo employeeContractInfo) throws ManagerBeanException{
		CONTRATO450TYPE c = (CONTRATO450TYPE) contratoType;
		c.setDATOSEXCLUSIONSOCIAL(createDatosExclusionSocial());
		c.setDATOSBONIFICACION(createDatosBonificacion(employeeContractInfo));
		c.setDATOSCONTRATOPRACTICAS(createDatosContratoPracticas(employeeContractInfo));
		c.setDATOSCONTRATOTIEMPOPARCIAL(createDatosContratoTiempoParcial(employeeContractInfo));
		c.setDATOSCONTRATOINTERINIDAD(createDatosContratoInterinidad(employeeContractInfo));
		c.setDATOSETCOTE(createDatosEtCote(employeeContractInfo));
		c.setDATOSCOPIABASICA(createDatosCopiaBasica(employeeContractInfo));
		c.setPROGEMPLEOPUBLICO(createDatosProgramaEmpleoPublico(employeeContractInfo));
		c.setDATOSEMPRESAINSERCION(createDatosEmpresaInsercion(employeeContractInfo));
		return c;
	}
	
	private static DATOSEXCLUSIONSOCIALTYPE createDatosExclusionSocial() {
		// TODO revisar la forma de escoger el codigo TC2 a la hora de crear un unevo contrato
		DATOSEXCLUSIONSOCIALTYPE datos = new DATOSEXCLUSIONSOCIALTYPE();
		datos.setMODALIDADEXCLUSION("000");
		return datos;
	}
	
	private static CONTRATO452TYPE createContract452(IContratoType contratoType, EmployeeContractInfo employeeContractInfo) throws ManagerBeanException{
		CONTRATO452TYPE c = (CONTRATO452TYPE) contratoType;
		c.setDATOSBONIFICACION(createDatosBonificacion(employeeContractInfo));
		c.setDATOSETCOTE(createDatosEtCote(employeeContractInfo));
		c.setDATOSCOPIABASICA(createDatosCopiaBasica(employeeContractInfo));
		c.setPROGEMPLEOPUBLICO(createDatosProgramaEmpleoPublico(employeeContractInfo));
		c.setDATOSEMPRESAINSERCION(createDatosEmpresaInsercion(employeeContractInfo));
		return c;
	}
	private static CONTRATO501TYPE createContract501(IContratoType contratoType, EmployeeContractInfo employeeContractInfo) throws ManagerBeanException{
		CONTRATO501TYPE c = (CONTRATO501TYPE) contratoType;
		c.setDATOSCONTRATOTIEMPOPARCIAL(createDatosContratoTiempoParcial(employeeContractInfo));
		c.setDATOSETCOTE(createDatosEtCote(employeeContractInfo));
		c.setDATOSCONTRATOINVESTIGACION(createDatosContratoInvestigacion(employeeContractInfo));
		c.setPROGEMPLEOPUBLICO(createDatosProgramaEmpleoPublico(employeeContractInfo));
		c.setDATOSCONTRATOEXTRANJERO(createDatosContratoExtranjero(employeeContractInfo));
		c.setDATOSREDUCCIONRDL12011(createDatosReduccionRdl2011(employeeContractInfo));
		return c;
	}
	private static CONTRATO502TYPE createContract502(IContratoType contratoType, EmployeeContractInfo employeeContractInfo) throws ManagerBeanException{
		CONTRATO502TYPE c = (CONTRATO502TYPE) contratoType;
		c.setDATOSCONTRATOTIEMPOPARCIAL(createDatosContratoTiempoParcial(employeeContractInfo));
		c.setDATOSETCOTE(createDatosEtCote(employeeContractInfo));
		c.setPROGEMPLEOPUBLICO(createDatosProgramaEmpleoPublico(employeeContractInfo));
		c.setDATOSCONTRATOEXTRANJERO(createDatosContratoExtranjero(employeeContractInfo));
		c.setDATOSREDUCCIONRDL12011(createDatosReduccionRdl2011(employeeContractInfo));
		return c;
	}
	private static CONTRATO503TYPE createContract503(IContratoType contratoType, EmployeeContractInfo employeeContractInfo) throws ManagerBeanException{
		CONTRATO503TYPE c = (CONTRATO503TYPE) contratoType;
		c.setDATOSCONTRATOTIEMPOPARCIAL(createDatosContratoTiempoParcial(employeeContractInfo));
		c.setPROGEMPLEOPUBLICO(createDatosProgramaEmpleoPublico(employeeContractInfo));
		c.setDATOSCONTRATOINSERCION(createDatosContratoInsercion(employeeContractInfo));
		return c;
	}
	private static CONTRATO510TYPE createContract510(IContratoType contratoType, EmployeeContractInfo employeeContractInfo) throws ManagerBeanException{
		CONTRATO510TYPE c = (CONTRATO510TYPE) contratoType;
		c.setDATOSCONTRATOTIEMPOPARCIAL(createDatosContratoTiempoParcial(employeeContractInfo));
		c.setDATOSCONTRATOINTERINIDAD(createDatosContratoInterinidad(employeeContractInfo));
		c.setDATOSETCOTE(createDatosEtCote(employeeContractInfo));
		c.setPROGEMPLEOPUBLICO(createDatosProgramaEmpleoPublico(employeeContractInfo));
		return c;
	}
	private static CONTRATO520TYPE createContract520(IContratoType contratoType, EmployeeContractInfo employeeContractInfo) throws ManagerBeanException{
		CONTRATO520TYPE c = (CONTRATO520TYPE) contratoType;
		c.setDATOSCONTRATOPRACTICAS(createDatosContratoPracticas(employeeContractInfo));
		c.setDATOSCONTRATOTIEMPOPARCIAL(createDatosContratoTiempoParcial(employeeContractInfo));
		c.setDATOSETCOTE(createDatosEtCote(employeeContractInfo));
		c.setDATOSCONTRATOINVESTIGACION(createDatosContratoInvestigacion(employeeContractInfo));
		c.setPROGEMPLEOPUBLICO(createDatosProgramaEmpleoPublico(employeeContractInfo));
		c.setDATOSREDUCCIONRDL12011(createDatosReduccionRdl2011(employeeContractInfo));
		return c;
	}
	private static CONTRATO530TYPE createContract530(IContratoType contratoType, EmployeeContractInfo employeeContractInfo) throws ManagerBeanException{
		CONTRATO530TYPE c = (CONTRATO530TYPE) contratoType;
		c.setDATOSBONIFICACION(createDatosBonificacion(employeeContractInfo));
		c.setDATOSCONTRATOTIEMPOPARCIAL(createDatosContratoTiempoParcial(employeeContractInfo));
		c.setDATOSETCOTE(createDatosEtCote(employeeContractInfo));
		c.setPROGEMPLEOPUBLICO(createDatosProgramaEmpleoPublico(employeeContractInfo));
		c.setDATOSREDUCCIONRDL12011(createDatosReduccionRdl2011(employeeContractInfo));
		return c;
	}
	private static CONTRATO540TYPE createContract540(IContratoType contratoType, EmployeeContractInfo employeeContractInfo) throws ManagerBeanException{
		CONTRATO540TYPE c = (CONTRATO540TYPE) contratoType;
		c.setDATOSCONTRATOTIEMPOPARCIAL(createDatosContratoTiempoParcial(employeeContractInfo));
		c.setDATOSETCOTE(createDatosEtCote(employeeContractInfo));
		return c;
	}
	private static CONTRATO541TYPE createContract541(IContratoType contratoType, EmployeeContractInfo employeeContractInfo) throws ManagerBeanException{
		CONTRATO541TYPE c = (CONTRATO541TYPE) contratoType;
		c.setDATOSCONTRATOTIEMPOPARCIAL(createDatosContratoTiempoParcial(employeeContractInfo));
		c.setDATOSANEXOCONTRATORELEVO(createDatosAnexoContratoRelevo(employeeContractInfo));
		c.setDATOSETCOTE(createDatosEtCote(employeeContractInfo));
		return c;
	}
	private static CONTRATO550TYPE createContract550(IContratoType contratoType, EmployeeContractInfo employeeContractInfo) throws ManagerBeanException{
		CONTRATO550TYPE c = (CONTRATO550TYPE) contratoType;
		c.setDATOSEXCLUSIONSOCIAL(createDatosExclusionSocial());
		c.setDATOSBONIFICACION(createDatosBonificacion(employeeContractInfo));
		c.setDATOSCONTRATOPRACTICAS(createDatosContratoPracticas(employeeContractInfo));
		c.setDATOSCONTRATOTIEMPOPARCIAL(createDatosContratoTiempoParcial(employeeContractInfo));
		c.setDATOSCONTRATOINTERINIDAD(createDatosContratoInterinidad(employeeContractInfo));
		c.setDATOSETCOTE(createDatosEtCote(employeeContractInfo));
		c.setDATOSCOPIABASICA(createDatosCopiaBasica(employeeContractInfo));
		c.setPROGEMPLEOPUBLICO(createDatosProgramaEmpleoPublico(employeeContractInfo));
		c.setDATOSEMPRESAINSERCION(createDatosEmpresaInsercion(employeeContractInfo));
		c.setDATOSREDUCCIONRDL12011(createDatosReduccionRdl2011(employeeContractInfo));
		return c;
	}
	private static CONTRATO552TYPE createContract552(IContratoType contratoType, EmployeeContractInfo employeeContractInfo) throws ManagerBeanException{
		CONTRATO552TYPE c = (CONTRATO552TYPE) contratoType;
		c.setDATOSBONIFICACION(createDatosBonificacion(employeeContractInfo));
		c.setDATOSCONTRATOTIEMPOPARCIAL(createDatosContratoTiempoParcial(employeeContractInfo));
		c.setDATOSETCOTE(createDatosEtCote(employeeContractInfo));
		c.setDATOSCOPIABASICA(createDatosCopiaBasica(employeeContractInfo));
		c.setPROGEMPLEOPUBLICO(createDatosProgramaEmpleoPublico(employeeContractInfo));
		c.setDATOSEMPRESAINSERCION(createDatosEmpresaInsercion(employeeContractInfo));
		return c;
	}
	private static CONTRATO970TYPE createContract970(IContratoType contratoType, EmployeeContractInfo employeeContractInfo) throws ManagerBeanException{
		CONTRATO970TYPE c = (CONTRATO970TYPE) contratoType;
		c.setDATOSETCOTE(createDatosEtCote(employeeContractInfo));
		c.setPROGEMPLEOPUBLICO(createDatosProgramaEmpleoPublico(employeeContractInfo));
		return c;
	}
	private static CONTRATO980TYPE createContract980(IContratoType contratoType, EmployeeContractInfo employeeContractInfo) throws ManagerBeanException{
		CONTRATO980TYPE c = (CONTRATO980TYPE) contratoType;
		c.setDATOSETCOTE(createDatosEtCote(employeeContractInfo));
		return c;
	}
	private static CONTRATO990TYPE createContract990(IContratoType contratoType, EmployeeContractInfo employeeContractInfo) throws ManagerBeanException{
		CONTRATO990TYPE c = (CONTRATO990TYPE) contratoType;
		c.setDATOSETCOTE(createDatosEtCote(employeeContractInfo));
		c.setDATOSCOPIABASICA(createDatosCopiaBasica(employeeContractInfo));
		c.setPROGEMPLEOPUBLICO(createDatosProgramaEmpleoPublico(employeeContractInfo));
		return c;
	}
	
}
