package com.esferalia.aon.ui.payroll.file;

import java.io.IOException;

import javax.xml.bind.JAXBException;

import org.apache.commons.lang.StringUtils;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.person.Person;
import com.esferalia.aon.payroll.CNO;
import com.esferalia.aon.payroll.enumeration.BasicCopySignatureType;
import com.esferalia.aon.payroll.enumeration.DisabilityCode;
import com.esferalia.aon.payroll.enumeration.DismissalCollective;
import com.esferalia.aon.payroll.enumeration.EducationalLevel;
import com.esferalia.aon.payroll.enumeration.EmployeeType;
import com.esferalia.aon.payroll.enumeration.EmploymentProgram;
import com.esferalia.aon.payroll.enumeration.InterimCause;
import com.esferalia.aon.payroll.enumeration.OtherLaws;
import com.esferalia.aon.payroll.enumeration.ResearchEmployee;
import com.esferalia.aon.payroll.enumeration.ResearchEmployer;
import com.esferalia.aon.payroll.enumeration.SchoolWorkshop;
import com.esferalia.aon.payroll.enumeration.WorkingDayType;
import com.esferalia.aon.ui.payroll.controller.wizard.ContrataParams;
import com.esferalia.aon.ui.payroll.utils.contractMojo.CONTRATO100TYPE;
import com.esferalia.aon.ui.payroll.utils.contractMojo.CONTRATO130TYPE;
import com.esferalia.aon.ui.payroll.utils.contractMojo.CONTRATO150TYPE;
import com.esferalia.aon.ui.payroll.utils.contractMojo.CONTRATO200TYPE;
import com.esferalia.aon.ui.payroll.utils.contractMojo.CONTRATO230TYPE;
import com.esferalia.aon.ui.payroll.utils.contractMojo.CONTRATO250TYPE;
import com.esferalia.aon.ui.payroll.utils.contractMojo.CONTRATO300TYPE;
import com.esferalia.aon.ui.payroll.utils.contractMojo.CONTRATO330TYPE;
import com.esferalia.aon.ui.payroll.utils.contractMojo.CONTRATO350TYPE;
import com.esferalia.aon.ui.payroll.utils.contractMojo.CONTRATO401TYPE;
import com.esferalia.aon.ui.payroll.utils.contractMojo.CONTRATO402TYPE;
import com.esferalia.aon.ui.payroll.utils.contractMojo.CONTRATO403TYPE;
import com.esferalia.aon.ui.payroll.utils.contractMojo.CONTRATO410TYPE;
import com.esferalia.aon.ui.payroll.utils.contractMojo.CONTRATO420TYPE;
import com.esferalia.aon.ui.payroll.utils.contractMojo.CONTRATO421TYPE;
import com.esferalia.aon.ui.payroll.utils.contractMojo.CONTRATO430TYPE;
import com.esferalia.aon.ui.payroll.utils.contractMojo.CONTRATO441TYPE;
import com.esferalia.aon.ui.payroll.utils.contractMojo.CONTRATO450TYPE;
import com.esferalia.aon.ui.payroll.utils.contractMojo.CONTRATO452TYPE;
import com.esferalia.aon.ui.payroll.utils.contractMojo.CONTRATO501TYPE;
import com.esferalia.aon.ui.payroll.utils.contractMojo.CONTRATO502TYPE;
import com.esferalia.aon.ui.payroll.utils.contractMojo.CONTRATO503TYPE;
import com.esferalia.aon.ui.payroll.utils.contractMojo.CONTRATO510TYPE;
import com.esferalia.aon.ui.payroll.utils.contractMojo.CONTRATO520TYPE;
import com.esferalia.aon.ui.payroll.utils.contractMojo.CONTRATO530TYPE;
import com.esferalia.aon.ui.payroll.utils.contractMojo.CONTRATO540TYPE;
import com.esferalia.aon.ui.payroll.utils.contractMojo.CONTRATO541TYPE;
import com.esferalia.aon.ui.payroll.utils.contractMojo.CONTRATO550TYPE;
import com.esferalia.aon.ui.payroll.utils.contractMojo.CONTRATO552TYPE;
import com.esferalia.aon.ui.payroll.utils.contractMojo.CONTRATO970TYPE;
import com.esferalia.aon.ui.payroll.utils.contractMojo.CONTRATO980TYPE;
import com.esferalia.aon.ui.payroll.utils.contractMojo.CONTRATO990TYPE;
import com.esferalia.aon.ui.payroll.utils.contractMojo.CONTRATOS;
import com.esferalia.aon.ui.payroll.utils.contractMojo.DATOSANEXOCONTRATORELEVOTYPE;
import com.esferalia.aon.ui.payroll.utils.contractMojo.DATOSBONIFICACIONTYPE;
import com.esferalia.aon.ui.payroll.utils.contractMojo.DATOSCOMUNICACOPIABASICATYPE;
import com.esferalia.aon.ui.payroll.utils.contractMojo.DATOSCONTRATOEXTRANJEROTYPE;
import com.esferalia.aon.ui.payroll.utils.contractMojo.DATOSCONTRATOINSERCIONTYPE;
import com.esferalia.aon.ui.payroll.utils.contractMojo.DATOSCONTRATOINTERINIDADTYPE;
import com.esferalia.aon.ui.payroll.utils.contractMojo.DATOSCONTRATOINVESTIGACIONTYPE;
import com.esferalia.aon.ui.payroll.utils.contractMojo.DATOSCONTRATOPRACTICASTYPE;
import com.esferalia.aon.ui.payroll.utils.contractMojo.DATOSCONTRATOTIEMPOPARCIALTYPE;
import com.esferalia.aon.ui.payroll.utils.contractMojo.DATOSCOPIABASICATYPE;
import com.esferalia.aon.ui.payroll.utils.contractMojo.DATOSEMPRESAINSERCIONTYPE;
import com.esferalia.aon.ui.payroll.utils.contractMojo.DATOSETCOTYPE;
import com.esferalia.aon.ui.payroll.utils.contractMojo.DATOSETTTYPE;
import com.esferalia.aon.ui.payroll.utils.contractMojo.DATOSEXCLUSIONSOCIALTYPE;
import com.esferalia.aon.ui.payroll.utils.contractMojo.DATOSGENERALESCONTRATOTYPE;
import com.esferalia.aon.ui.payroll.utils.contractMojo.DATOSMEDIDASFOMENTOTYPE;
import com.esferalia.aon.ui.payroll.utils.contractMojo.DATOSPROGEMPLEOPUBLICOTYPE;
import com.esferalia.aon.ui.payroll.utils.contractMojo.DATOSREDUCCIONRDL12011TYPE;
import com.esferalia.aon.ui.payroll.utils.contractMojo.DATOSTRABAJADORTYPE;
import com.esferalia.aon.ui.payroll.utils.contractMojo.DATOSUSOLIBREEMPRESATYPE;


public class ContractXmlReader {
	
	private ContrataParams params;
	
	public void completeContrataParams(CONTRATOS contratos, ContrataParams params) throws JAXBException, IOException {
		this.params = params;
		
		Object o = contratos.getCONTRATO100AndCONTRATO130AndCONTRATO150().get(0);
		if (o instanceof CONTRATO100TYPE) {
			readContract100((CONTRATO100TYPE)o);
		} else if (o instanceof CONTRATO130TYPE) {
			readContract130((CONTRATO130TYPE)o);
		} else if (o instanceof CONTRATO150TYPE) {
			readContract150((CONTRATO150TYPE)o);
		} else if (o instanceof CONTRATO200TYPE) {
			readContract200((CONTRATO200TYPE)o);
		} else if (o instanceof CONTRATO230TYPE) {
			readContract230((CONTRATO230TYPE)o);
		} else if (o instanceof CONTRATO250TYPE) {
			readContract250((CONTRATO250TYPE)o);
		} else if (o instanceof CONTRATO300TYPE) {
			readContract300((CONTRATO300TYPE)o);
		} else if (o instanceof CONTRATO330TYPE) {
			readContract330((CONTRATO330TYPE)o);
		} else if (o instanceof CONTRATO350TYPE) {
			readContract350((CONTRATO350TYPE)o);
		} else if (o instanceof CONTRATO401TYPE) {
			readContract401((CONTRATO401TYPE)o);
		} else if (o instanceof CONTRATO402TYPE) {
			readContract402((CONTRATO402TYPE)o);
		} else if (o instanceof CONTRATO403TYPE) {
			readContract403((CONTRATO403TYPE)o);
		} else if (o instanceof CONTRATO410TYPE) {
			readContract410((CONTRATO410TYPE)o);
		} else if (o instanceof CONTRATO420TYPE) {
			readContract420((CONTRATO420TYPE)o);
		} else if (o instanceof CONTRATO421TYPE) {
			readContract421((CONTRATO421TYPE)o);
		} else if (o instanceof CONTRATO430TYPE) {
			readContract430((CONTRATO430TYPE)o);
		} else if (o instanceof CONTRATO441TYPE) {
			readContract441((CONTRATO441TYPE)o);
		} else if (o instanceof CONTRATO450TYPE) {
			readContract450((CONTRATO450TYPE)o);
		} else if (o instanceof CONTRATO452TYPE) {
			readContract452((CONTRATO452TYPE)o);
		} else if (o instanceof CONTRATO501TYPE) {
			readContract501((CONTRATO501TYPE)o);
		} else if (o instanceof CONTRATO502TYPE) {
			readContract502((CONTRATO502TYPE)o);
		} else if (o instanceof CONTRATO503TYPE) {
			readContract503((CONTRATO503TYPE)o);
		} else if (o instanceof CONTRATO510TYPE) {
			readContract510((CONTRATO510TYPE)o);
		} else if (o instanceof CONTRATO520TYPE) {
			readContract520((CONTRATO520TYPE)o);
		} else if (o instanceof CONTRATO530TYPE) {
			readContract530((CONTRATO530TYPE)o);
		} else if (o instanceof CONTRATO540TYPE) {
			readContract540((CONTRATO540TYPE)o);
		} else if (o instanceof CONTRATO541TYPE) {
			readContract541((CONTRATO541TYPE)o);
		} else if (o instanceof CONTRATO550TYPE) {
			readContract550((CONTRATO550TYPE)o);
		} else if (o instanceof CONTRATO552TYPE) {
			readContract552((CONTRATO552TYPE)o);
		} else if (o instanceof CONTRATO970TYPE) {
			readContract970((CONTRATO970TYPE)o);
		} else if (o instanceof CONTRATO980TYPE) {
			readContract980((CONTRATO980TYPE)o);
		} else if (o instanceof CONTRATO990TYPE) {
			readContract990((CONTRATO990TYPE)o);
		}
		
		
		
		
	}
	
	private CNO getCno(String value){
		try {
			IManagerBean bean = BeanManager.getManagerBean(CNO.class);
			return (CNO) bean.get(Integer.parseInt(value.substring(0, 4)));
		} catch (ManagerBeanException e) {
			// NADA
		}
		return null;
	}
	
	private void readContract100(CONTRATO100TYPE o) {
		completeDatosTrabajador(o.getDATOSTRABAJADOR());
		completeDatosGeneralesContrato(o.getDATOSGENERALESCONTRATO());
		completeDatosMedidasFomento(o.getDATOSMEDIDASFOMENTO());
		completeDatosAnexoContratoRelevo(o.getDATOSANEXOCONTRATORELEVO());
		completeDatosEtCote(o.getDATOSETCOTE());
		completeDatosEtt(o.getDATOSETT());
		completeDatosContratoExtranjero(o.getDATOSCONTRATOEXTRANJERO());
		completeDatosComunicacionCopiaBasica(o.getDATOSCOMUNICACOPIABASICA());
		completeDatosUsoLibreEmpresa(o.getDATOSUSOLIBREEMPRESA());
	}
	private void readContract130(CONTRATO130TYPE o) {
		completeDatosTrabajador(o.getDATOSTRABAJADOR());
		completeDatosGeneralesContrato(o.getDATOSGENERALESCONTRATO());
	    completeDatosBonificacion(o.getDATOSBONIFICACION());
	    completeDatosMedidasFomento(o.getDATOSMEDIDASFOMENTO());
	    completeDatosAnexoContratoRelevo(o.getDATOSANEXOCONTRATORELEVO());
	    completeDatosEtCote(o.getDATOSETCOTE());
	    completeDatosEtt(o.getDATOSETT()); 
	    completeDatosComunicacionCopiaBasica(o.getDATOSCOMUNICACOPIABASICA());
	    completeDatosUsoLibreEmpresa(o.getDATOSUSOLIBREEMPRESA());
	}
	private void readContract150(CONTRATO150TYPE o) {
		completeDatosTrabajador(o.getDATOSTRABAJADOR());
		completeDatosGeneralesContrato(o.getDATOSGENERALESCONTRATO());
		completeDatosBonificacion(o.getDATOSBONIFICACION());
		completeDatosMedidasFomento(o.getDATOSMEDIDASFOMENTO());
		completeDatosAnexoContratoRelevo(o.getDATOSANEXOCONTRATORELEVO());
		completeDatosEtCote(o.getDATOSETCOTE());
		completeDatosEtt(o.getDATOSETT());
		completeDatosEmpresaInsercion(o.getDATOSEMPRESAINSERCION());
		completeDatosComunicacionCopiaBasica(o.getDATOSCOMUNICACOPIABASICA());
		completeDatosUsoLibreEmpresa(o.getDATOSUSOLIBREEMPRESA());
	}
	private void readContract200(CONTRATO200TYPE o) {
		completeDatosTrabajador(o.getDATOSTRABAJADOR());
		completeDatosGeneralesContrato(o.getDATOSGENERALESCONTRATO());
		completeDatosMedidasFomento(o.getDATOSMEDIDASFOMENTO());
		completeDatosAnexoContratoRelevo(o.getDATOSANEXOCONTRATORELEVO());
		completeDatosEtCote(o.getDATOSETCOTE());
		completeDatosEtt(o.getDATOSETT());
		completeDatosContratoExtranjero(o.getDATOSCONTRATOEXTRANJERO());
		completeDatosComunicacionCopiaBasica(o.getDATOSCOMUNICACOPIABASICA());
		completeDatosUsoLibreEmpresa(o.getDATOSUSOLIBREEMPRESA());
		completeDatosReduccionRdl2011(o.getDATOSREDUCCIONRDL12011());
		completeDatosContratoTiempoParcial(o.getDATOSCONTRATOTIEMPOPARCIAL());
	}
	private void readContract230(CONTRATO230TYPE o) {
		completeDatosTrabajador(o.getDATOSTRABAJADOR());
		completeDatosGeneralesContrato(o.getDATOSGENERALESCONTRATO());
		completeDatosContratoTiempoParcial(o.getDATOSCONTRATOTIEMPOPARCIAL());
		completeDatosBonificacion(o.getDATOSBONIFICACION());
		completeDatosMedidasFomento(o.getDATOSMEDIDASFOMENTO());
		completeDatosAnexoContratoRelevo(o.getDATOSANEXOCONTRATORELEVO());
		completeDatosEtCote(o.getDATOSETCOTE());
		completeDatosEtt(o.getDATOSETT());
		completeDatosReduccionRdl2011(o.getDATOSREDUCCIONRDL12011());
		completeDatosComunicacionCopiaBasica(o.getDATOSCOMUNICACOPIABASICA());
		completeDatosUsoLibreEmpresa(o.getDATOSUSOLIBREEMPRESA());
	}
	private void readContract250(CONTRATO250TYPE o) {
		completeDatosTrabajador(o.getDATOSTRABAJADOR());
		completeDatosGeneralesContrato(o.getDATOSGENERALESCONTRATO());
		completeDatosContratoTiempoParcial(o.getDATOSCONTRATOTIEMPOPARCIAL());
		completeDatosBonificacion(o.getDATOSBONIFICACION());
		completeDatosMedidasFomento(o.getDATOSMEDIDASFOMENTO());
		completeDatosAnexoContratoRelevo(o.getDATOSANEXOCONTRATORELEVO());
		completeDatosEtCote(o.getDATOSETCOTE());
		completeDatosEtt(o.getDATOSETT());
		completeDatosEmpresaInsercion(o.getDATOSEMPRESAINSERCION());
		completeDatosReduccionRdl2011(o.getDATOSREDUCCIONRDL12011());
		completeDatosComunicacionCopiaBasica(o.getDATOSCOMUNICACOPIABASICA());
		completeDatosUsoLibreEmpresa(o.getDATOSUSOLIBREEMPRESA());
	}
	private void readContract300(CONTRATO300TYPE o) {
		completeDatosTrabajador(o.getDATOSTRABAJADOR());
		completeDatosGeneralesContrato(o.getDATOSGENERALESCONTRATO());
	    completeDatosContratoTiempoParcial(o.getDATOSCONTRATOTIEMPOPARCIAL());
	    completeDatosMedidasFomento(o.getDATOSMEDIDASFOMENTO());
	    completeDatosAnexoContratoRelevo(o.getDATOSANEXOCONTRATORELEVO());
	    completeDatosEtCote(o.getDATOSETCOTE());
	    completeDatosEtt(o.getDATOSETT()); 
	    completeDatosReduccionRdl2011(o.getDATOSREDUCCIONRDL12011());
	    completeDatosComunicacionCopiaBasica(o.getDATOSCOMUNICACOPIABASICA());
    	completeDatosUsoLibreEmpresa(o.getDATOSUSOLIBREEMPRESA());
	}
	private void readContract330(CONTRATO330TYPE o) {
		completeDatosTrabajador(o.getDATOSTRABAJADOR());
		completeDatosGeneralesContrato(o.getDATOSGENERALESCONTRATO());
		completeDatosContratoTiempoParcial(o.getDATOSCONTRATOTIEMPOPARCIAL());
		completeDatosBonificacion(o.getDATOSBONIFICACION());
		completeDatosMedidasFomento(o.getDATOSMEDIDASFOMENTO());
		completeDatosAnexoContratoRelevo(o.getDATOSANEXOCONTRATORELEVO());
		completeDatosEtCote(o.getDATOSETCOTE());
		completeDatosEtt(o.getDATOSETT()); 
		completeDatosReduccionRdl2011(o.getDATOSREDUCCIONRDL12011());
		completeDatosComunicacionCopiaBasica(o.getDATOSCOMUNICACOPIABASICA());
		completeDatosUsoLibreEmpresa(o.getDATOSUSOLIBREEMPRESA());
	}
	private void readContract350(CONTRATO350TYPE o) {
		completeDatosTrabajador(o.getDATOSTRABAJADOR());
		completeDatosGeneralesContrato(o.getDATOSGENERALESCONTRATO());
		completeDatosContratoTiempoParcial(o.getDATOSCONTRATOTIEMPOPARCIAL());
		completeDatosBonificacion(o.getDATOSBONIFICACION());
		completeDatosMedidasFomento(o.getDATOSMEDIDASFOMENTO());
		completeDatosAnexoContratoRelevo(o.getDATOSANEXOCONTRATORELEVO());
		completeDatosEtCote(o.getDATOSETCOTE());
		completeDatosEtt(o.getDATOSETT()); 
		completeDatosEmpresaInsercion(o.getDATOSEMPRESAINSERCION());
		completeDatosReduccionRdl2011(o.getDATOSREDUCCIONRDL12011());
		completeDatosComunicacionCopiaBasica(o.getDATOSCOMUNICACOPIABASICA());
		completeDatosUsoLibreEmpresa(o.getDATOSUSOLIBREEMPRESA());
	}
	private void readContract401(CONTRATO401TYPE o) {
		completeDatosTrabajador(o.getDATOSTRABAJADOR());
		completeDatosGeneralesContrato(o.getDATOSGENERALESCONTRATO());
		completeDatosEtCote(o.getDATOSETCOTE());
		completeDatosContratoInvestigacion(o.getDATOSCONTRATOINVESTIGACION());
		completeDatosProgramaEmpleoPublico(o.getPROGEMPLEOPUBLICO());
		completeDatosEtt(o.getDATOSETT()); 
		completeDatosContratoExtranjero(o.getDATOSCONTRATOEXTRANJERO());
		completeDatosComunicacionCopiaBasica(o.getDATOSCOMUNICACOPIABASICA());
		completeDatosUsoLibreEmpresa(o.getDATOSUSOLIBREEMPRESA());
	}
	private void readContract402(CONTRATO402TYPE o) {
		completeDatosTrabajador(o.getDATOSTRABAJADOR());
		completeDatosGeneralesContrato(o.getDATOSGENERALESCONTRATO());
		completeDatosEtCote(o.getDATOSETCOTE());
		completeDatosCopiaBasica(o.getDATOSCOPIABASICA());
		completeDatosProgramaEmpleoPublico(o.getPROGEMPLEOPUBLICO());
		completeDatosEtt(o.getDATOSETT()); 
		completeDatosContratoExtranjero(o.getDATOSCONTRATOEXTRANJERO());
		completeDatosComunicacionCopiaBasica(o.getDATOSCOMUNICACOPIABASICA());
		completeDatosUsoLibreEmpresa(o.getDATOSUSOLIBREEMPRESA());
	}
	private void readContract403(CONTRATO403TYPE o) {
		completeDatosTrabajador(o.getDATOSTRABAJADOR());
		completeDatosGeneralesContrato(o.getDATOSGENERALESCONTRATO());
		completeDatosProgramaEmpleoPublico(o.getPROGEMPLEOPUBLICO());
		completeDatosContratoInsercion(o.getDATOSCONTRATOINSERCION());
		completeDatosEtt(o.getDATOSETT()); 
		completeDatosComunicacionCopiaBasica(o.getDATOSCOMUNICACOPIABASICA());
		completeDatosUsoLibreEmpresa(o.getDATOSUSOLIBREEMPRESA());
	}
	private void readContract410(CONTRATO410TYPE o) {
		completeDatosTrabajador(o.getDATOSTRABAJADOR());
		completeDatosGeneralesContrato(o.getDATOSGENERALESCONTRATO());
		completeDatosContratoInterinidad(o.getDATOSCONTRATOINTERINIDAD());
		completeDatosEtCote(o.getDATOSETCOTE());
		completeDatosProgramaEmpleoPublico(o.getPROGEMPLEOPUBLICO());
		completeDatosEtt(o.getDATOSETT()); 
		completeDatosComunicacionCopiaBasica(o.getDATOSCOMUNICACOPIABASICA());
		completeDatosUsoLibreEmpresa(o.getDATOSUSOLIBREEMPRESA());
	}
	private void readContract420(CONTRATO420TYPE o) {
		completeDatosTrabajador(o.getDATOSTRABAJADOR());
		completeDatosGeneralesContrato(o.getDATOSGENERALESCONTRATO());
		completeDatosContratoPracticas(o.getDATOSCONTRATOPRACTICAS());
		completeDatosEtCote(o.getDATOSETCOTE());
		completeDatosContratoInvestigacion(o.getDATOSCONTRATOINVESTIGACION());
		completeDatosProgramaEmpleoPublico(o.getPROGEMPLEOPUBLICO());
		completeDatosEtt(o.getDATOSETT()); 
		completeDatosComunicacionCopiaBasica(o.getDATOSCOMUNICACOPIABASICA());
		completeDatosUsoLibreEmpresa(o.getDATOSUSOLIBREEMPRESA());
	}
	private void readContract421(CONTRATO421TYPE o) {
		completeDatosTrabajador(o.getDATOSTRABAJADOR());
		completeDatosGeneralesContrato(o.getDATOSGENERALESCONTRATO());
		completeDatosContratoTiempoParcial(o.getDATOSCONTRATOTIEMPOPARCIAL());
		completeDatosEtCote(o.getDATOSETCOTE());
		completeDatosProgramaEmpleoPublico(o.getPROGEMPLEOPUBLICO());
		completeDatosEtt(o.getDATOSETT()); 
		completeDatosComunicacionCopiaBasica(o.getDATOSCOMUNICACOPIABASICA());
		completeDatosUsoLibreEmpresa(o.getDATOSUSOLIBREEMPRESA());
	}
	private void readContract430(CONTRATO430TYPE o) {
		completeDatosTrabajador(o.getDATOSTRABAJADOR());
		completeDatosGeneralesContrato(o.getDATOSGENERALESCONTRATO());
		completeDatosBonificacion(o.getDATOSBONIFICACION());
		completeDatosEtCote(o.getDATOSETCOTE());
		completeDatosProgramaEmpleoPublico(o.getPROGEMPLEOPUBLICO());
		completeDatosEtt(o.getDATOSETT()); 
		completeDatosComunicacionCopiaBasica(o.getDATOSCOMUNICACOPIABASICA());
		completeDatosUsoLibreEmpresa(o.getDATOSUSOLIBREEMPRESA());
	}
	private void readContract441(CONTRATO441TYPE o) {
		completeDatosTrabajador(o.getDATOSTRABAJADOR());
		completeDatosGeneralesContrato(o.getDATOSGENERALESCONTRATO());
		completeDatosAnexoContratoRelevo(o.getDATOSANEXOCONTRATORELEVO());
		completeDatosEtCote(o.getDATOSETCOTE());
		completeDatosEtt(o.getDATOSETT()); 
		completeDatosComunicacionCopiaBasica(o.getDATOSCOMUNICACOPIABASICA());
		completeDatosUsoLibreEmpresa(o.getDATOSUSOLIBREEMPRESA());
	}
	private void readContract450(CONTRATO450TYPE o) {
		completeDatosTrabajador(o.getDATOSTRABAJADOR());
		completeDatosGeneralesContrato(o.getDATOSGENERALESCONTRATO());
		completeDatosExclusionSocial(o.getDATOSEXCLUSIONSOCIAL());
		completeDatosBonificacion(o.getDATOSBONIFICACION());
		completeDatosContratoPracticas(o.getDATOSCONTRATOPRACTICAS());
		completeDatosContratoTiempoParcial(o.getDATOSCONTRATOTIEMPOPARCIAL());
		completeDatosContratoInterinidad(o.getDATOSCONTRATOINTERINIDAD());
		completeDatosEtCote(o.getDATOSETCOTE());
		completeDatosCopiaBasica(o.getDATOSCOPIABASICA());
		completeDatosProgramaEmpleoPublico(o.getPROGEMPLEOPUBLICO());
		completeDatosEtt(o.getDATOSETT()); 
		completeDatosEmpresaInsercion(o.getDATOSEMPRESAINSERCION());
		completeDatosComunicacionCopiaBasica(o.getDATOSCOMUNICACOPIABASICA());
		completeDatosUsoLibreEmpresa(o.getDATOSUSOLIBREEMPRESA());
	}
	private void readContract452(CONTRATO452TYPE o) {
		completeDatosTrabajador(o.getDATOSTRABAJADOR());
		completeDatosGeneralesContrato(o.getDATOSGENERALESCONTRATO());
		completeDatosBonificacion(o.getDATOSBONIFICACION());
		completeDatosEtCote(o.getDATOSETCOTE());
		completeDatosCopiaBasica(o.getDATOSCOPIABASICA());
		completeDatosProgramaEmpleoPublico(o.getPROGEMPLEOPUBLICO());
		completeDatosEtt(o.getDATOSETT()); 
		completeDatosEmpresaInsercion(o.getDATOSEMPRESAINSERCION());
		completeDatosComunicacionCopiaBasica(o.getDATOSCOMUNICACOPIABASICA());
		completeDatosUsoLibreEmpresa(o.getDATOSUSOLIBREEMPRESA());
	}
	private void readContract501(CONTRATO501TYPE o) {
		completeDatosTrabajador(o.getDATOSTRABAJADOR());
		completeDatosGeneralesContrato(o.getDATOSGENERALESCONTRATO());
		completeDatosContratoTiempoParcial(o.getDATOSCONTRATOTIEMPOPARCIAL());
		completeDatosEtCote(o.getDATOSETCOTE());
		completeDatosContratoInvestigacion(o.getDATOSCONTRATOINVESTIGACION());
		completeDatosProgramaEmpleoPublico(o.getPROGEMPLEOPUBLICO());
		completeDatosEtt(o.getDATOSETT()); 
		completeDatosContratoExtranjero(o.getDATOSCONTRATOEXTRANJERO());
		completeDatosReduccionRdl2011(o.getDATOSREDUCCIONRDL12011());
		completeDatosComunicacionCopiaBasica(o.getDATOSCOMUNICACOPIABASICA());
		completeDatosUsoLibreEmpresa(o.getDATOSUSOLIBREEMPRESA());
	}
	private void readContract502(CONTRATO502TYPE o) {
		completeDatosTrabajador(o.getDATOSTRABAJADOR());
		completeDatosGeneralesContrato(o.getDATOSGENERALESCONTRATO());
		completeDatosContratoTiempoParcial(o.getDATOSCONTRATOTIEMPOPARCIAL());
		completeDatosEtCote(o.getDATOSETCOTE());
		completeDatosProgramaEmpleoPublico(o.getPROGEMPLEOPUBLICO());
		completeDatosEtt(o.getDATOSETT()); 
		completeDatosContratoExtranjero(o.getDATOSCONTRATOEXTRANJERO());
		completeDatosReduccionRdl2011(o.getDATOSREDUCCIONRDL12011());
		completeDatosComunicacionCopiaBasica(o.getDATOSCOMUNICACOPIABASICA());
		completeDatosUsoLibreEmpresa(o.getDATOSUSOLIBREEMPRESA());
	}
	private void readContract503(CONTRATO503TYPE o) {
		completeDatosTrabajador(o.getDATOSTRABAJADOR());
		completeDatosGeneralesContrato(o.getDATOSGENERALESCONTRATO());
		completeDatosContratoTiempoParcial(o.getDATOSCONTRATOTIEMPOPARCIAL());
		completeDatosProgramaEmpleoPublico(o.getPROGEMPLEOPUBLICO());
		completeDatosContratoInsercion(o.getDATOSCONTRATOINSERCION());
		completeDatosEtt(o.getDATOSETT()); 
		completeDatosComunicacionCopiaBasica(o.getDATOSCOMUNICACOPIABASICA());
		completeDatosUsoLibreEmpresa(o.getDATOSUSOLIBREEMPRESA());
	}
	private void readContract510(CONTRATO510TYPE o) {
		completeDatosTrabajador(o.getDATOSTRABAJADOR());
		completeDatosGeneralesContrato(o.getDATOSGENERALESCONTRATO());
		completeDatosContratoTiempoParcial(o.getDATOSCONTRATOTIEMPOPARCIAL());
		completeDatosContratoInterinidad(o.getDATOSCONTRATOINTERINIDAD());
		completeDatosEtCote(o.getDATOSETCOTE());
		completeDatosProgramaEmpleoPublico(o.getPROGEMPLEOPUBLICO());
		completeDatosEtt(o.getDATOSETT()); 
		completeDatosComunicacionCopiaBasica(o.getDATOSCOMUNICACOPIABASICA());
		completeDatosUsoLibreEmpresa(o.getDATOSUSOLIBREEMPRESA());
	}
	private void readContract520(CONTRATO520TYPE o) {
		completeDatosTrabajador(o.getDATOSTRABAJADOR());
		completeDatosGeneralesContrato(o.getDATOSGENERALESCONTRATO());
		completeDatosContratoPracticas(o.getDATOSCONTRATOPRACTICAS());
		completeDatosContratoTiempoParcial(o.getDATOSCONTRATOTIEMPOPARCIAL());
		completeDatosEtCote(o.getDATOSETCOTE());
		completeDatosContratoInvestigacion(o.getDATOSCONTRATOINVESTIGACION());
		completeDatosProgramaEmpleoPublico(o.getPROGEMPLEOPUBLICO());
		completeDatosEtt(o.getDATOSETT()); 
		completeDatosReduccionRdl2011(o.getDATOSREDUCCIONRDL12011());
		completeDatosComunicacionCopiaBasica(o.getDATOSCOMUNICACOPIABASICA());
		completeDatosUsoLibreEmpresa(o.getDATOSUSOLIBREEMPRESA());
	}
	private void readContract530(CONTRATO530TYPE o) {
		completeDatosTrabajador(o.getDATOSTRABAJADOR());
		completeDatosGeneralesContrato(o.getDATOSGENERALESCONTRATO());
		completeDatosBonificacion(o.getDATOSBONIFICACION());
		completeDatosContratoTiempoParcial(o.getDATOSCONTRATOTIEMPOPARCIAL());
		completeDatosEtCote(o.getDATOSETCOTE());
		completeDatosProgramaEmpleoPublico(o.getPROGEMPLEOPUBLICO());
		completeDatosEtt(o.getDATOSETT()); 
		completeDatosReduccionRdl2011(o.getDATOSREDUCCIONRDL12011());
		completeDatosComunicacionCopiaBasica(o.getDATOSCOMUNICACOPIABASICA());
		completeDatosUsoLibreEmpresa(o.getDATOSUSOLIBREEMPRESA());
	}
	private void readContract540(CONTRATO540TYPE o) {
		completeDatosTrabajador(o.getDATOSTRABAJADOR());
		completeDatosGeneralesContrato(o.getDATOSGENERALESCONTRATO());
		completeDatosContratoTiempoParcial(o.getDATOSCONTRATOTIEMPOPARCIAL());
		completeDatosEtCote(o.getDATOSETCOTE());
		completeDatosEtt(o.getDATOSETT()); 
		completeDatosComunicacionCopiaBasica(o.getDATOSCOMUNICACOPIABASICA());
		completeDatosUsoLibreEmpresa(o.getDATOSUSOLIBREEMPRESA());
	}
	private void readContract541(CONTRATO541TYPE o) {
		completeDatosTrabajador(o.getDATOSTRABAJADOR());
		completeDatosGeneralesContrato(o.getDATOSGENERALESCONTRATO());
		completeDatosContratoTiempoParcial(o.getDATOSCONTRATOTIEMPOPARCIAL());
		completeDatosAnexoContratoRelevo(o.getDATOSANEXOCONTRATORELEVO());
		completeDatosEtCote(o.getDATOSETCOTE());
		completeDatosEtt(o.getDATOSETT()); 
		completeDatosComunicacionCopiaBasica(o.getDATOSCOMUNICACOPIABASICA());
		completeDatosUsoLibreEmpresa(o.getDATOSUSOLIBREEMPRESA());
	}
	private void readContract550(CONTRATO550TYPE o) {
		completeDatosTrabajador(o.getDATOSTRABAJADOR());
		completeDatosGeneralesContrato(o.getDATOSGENERALESCONTRATO());
		completeDatosExclusionSocial(o.getDATOSEXCLUSIONSOCIAL());
		completeDatosBonificacion(o.getDATOSBONIFICACION());
		completeDatosContratoPracticas(o.getDATOSCONTRATOPRACTICAS());
		completeDatosContratoTiempoParcial(o.getDATOSCONTRATOTIEMPOPARCIAL());
		completeDatosContratoInterinidad(o.getDATOSCONTRATOINTERINIDAD());
		completeDatosEtCote(o.getDATOSETCOTE());
		completeDatosCopiaBasica(o.getDATOSCOPIABASICA());
		completeDatosProgramaEmpleoPublico(o.getPROGEMPLEOPUBLICO());
		completeDatosEtt(o.getDATOSETT()); 
		completeDatosEmpresaInsercion(o.getDATOSEMPRESAINSERCION());
		completeDatosReduccionRdl2011(o.getDATOSREDUCCIONRDL12011());
		completeDatosComunicacionCopiaBasica(o.getDATOSCOMUNICACOPIABASICA());
		completeDatosUsoLibreEmpresa(o.getDATOSUSOLIBREEMPRESA());
	}
	private void readContract552(CONTRATO552TYPE o) {
		completeDatosTrabajador(o.getDATOSTRABAJADOR());
		completeDatosGeneralesContrato(o.getDATOSGENERALESCONTRATO());
		completeDatosBonificacion(o.getDATOSBONIFICACION());
		completeDatosContratoTiempoParcial(o.getDATOSCONTRATOTIEMPOPARCIAL());
		completeDatosEtCote(o.getDATOSETCOTE());
		completeDatosCopiaBasica(o.getDATOSCOPIABASICA());
		completeDatosProgramaEmpleoPublico(o.getPROGEMPLEOPUBLICO());
		completeDatosEtt(o.getDATOSETT());
		completeDatosEmpresaInsercion(o.getDATOSEMPRESAINSERCION());
		completeDatosComunicacionCopiaBasica(o.getDATOSCOMUNICACOPIABASICA());
		completeDatosUsoLibreEmpresa(o.getDATOSUSOLIBREEMPRESA());
	}
	private void readContract970(CONTRATO970TYPE o) {
		completeDatosTrabajador(o.getDATOSTRABAJADOR());
		completeDatosGeneralesContrato(o.getDATOSGENERALESCONTRATO());
		completeDatosEtCote(o.getDATOSETCOTE());
		completeDatosProgramaEmpleoPublico(o.getPROGEMPLEOPUBLICO());
		completeDatosEtt(o.getDATOSETT());
		completeDatosComunicacionCopiaBasica(o.getDATOSCOMUNICACOPIABASICA());
		completeDatosUsoLibreEmpresa(o.getDATOSUSOLIBREEMPRESA());
	}
	private void readContract980(CONTRATO980TYPE o) {
		completeDatosTrabajador(o.getDATOSTRABAJADOR());
		completeDatosGeneralesContrato(o.getDATOSGENERALESCONTRATO());
		completeDatosEtCote(o.getDATOSETCOTE());
		completeDatosEtt(o.getDATOSETT());
		completeDatosComunicacionCopiaBasica(o.getDATOSCOMUNICACOPIABASICA());
		completeDatosUsoLibreEmpresa(o.getDATOSUSOLIBREEMPRESA());
	}
	private void readContract990(CONTRATO990TYPE o) {
		completeDatosTrabajador(o.getDATOSTRABAJADOR());
		completeDatosGeneralesContrato(o.getDATOSGENERALESCONTRATO());
		completeDatosEtCote(o.getDATOSETCOTE());
		completeDatosCopiaBasica(o.getDATOSCOPIABASICA());
		completeDatosProgramaEmpleoPublico(o.getPROGEMPLEOPUBLICO());
		completeDatosEtt(o.getDATOSETT());
		completeDatosComunicacionCopiaBasica(o.getDATOSCOMUNICACOPIABASICA());
		completeDatosUsoLibreEmpresa(o.getDATOSUSOLIBREEMPRESA());
	}
	
	
	private void completeDatosTrabajador(DATOSTRABAJADORTYPE datos) {
		if(datos != null){
			params.setTownCode(datos.getMUNICIPIORESIDENCIA());
		}
	}
	
	private void completeDatosGeneralesContrato(DATOSGENERALESCONTRATOTYPE datos) {
		if(datos != null){
			if(datos.getINDCONVENIOCOLECTIVO()!=null){
				params.setCollectiveAgreement(datos.getINDCONVENIOCOLECTIVO().equals("S")?true:false);
			}
			if(datos.getNIVELFORMATIVO()!=null){
				params.setEducationalLevel(EducationalLevel.valueOf("EL"+datos.getNIVELFORMATIVO()));
			}
			if(datos.getINDDISCAPACIDAD()!=null){
				params.setDisabilityCode(DisabilityCode.valueOf("DC_"+datos.getINDDISCAPACIDAD()));
				params.setDisabilityData(true);
			}
			if(datos.getCODIGOOCUPACION()!=null){
				params.setCno(getCno(datos.getCODIGOOCUPACION()));
			}
			if(datos.getIDOFERTA()!=null){
				params.setOffer(datos.getIDOFERTA());
				params.setOfferData(true);
			}
			if(datos.getCODIGOPROGRAMAEMPLEO()!=null){
				params.setEmploymentProgram(EmploymentProgram.valueOf("EP"+datos.getCODIGOPROGRAMAEMPLEO()));
				params.setEmploymentProgramData(true);
			}
			if(datos.getOTRASLEGISLACIONES()!=null){
				params.setOtherLaws(OtherLaws.valueOf("OL"+datos.getOTRASLEGISLACIONES()));
				params.setOlderThan52Data(true);
			}
			if(datos.getDATOSCAMPAÑAS()!=null){
				params.setCampaignGeozone(datos.getDATOSCAMPAÑAS().substring(0, 2));
				params.setCampaign(datos.getDATOSCAMPAÑAS().substring(2, 5));
				params.setCampaignYear(datos.getDATOSCAMPAÑAS().substring(datos.getDATOSCAMPAÑAS().length()-4, datos.getDATOSCAMPAÑAS().length()));
				params.setCanpaignData(true);
			}
		}
	}
	private void completeDatosMedidasFomento(DATOSMEDIDASFOMENTOTYPE datos) {
		if(datos != null){
			params.setPermanentContractDevelopment(datos.getINDCOSTEDESPIDO().equals("1")?true:false);
			if(datos.getCODIGOCOLECTIVODESPIDO()!=null){
				params.setDismissalCollective(DismissalCollective.valueOf("DC"+datos.getCODIGOCOLECTIVODESPIDO()));
			}
		}
	}
	private void completeDatosAnexoContratoRelevo(DATOSANEXOCONTRATORELEVOTYPE datos) {
		if(datos != null){
			params.setReliefData(true);
			if(datos.getTIPOTRABAJADOR()!=null){
				params.setReliefEmployeeType(EmployeeType.valueOf("ET"+datos.getTIPOTRABAJADOR()));
			}
			Person person = new Person();
			person.setName(datos.getNOMBREAPELLIDOS().getNOMBRE());
			person.setFirstSurname(datos.getNOMBREAPELLIDOS().getPRIMERAPELLIDO());
			person.setSecondSurname(datos.getNOMBREAPELLIDOS().getSEGUNDOAPELLIDO());
			params.setReliefPerson(person);
		}
	}
	private void completeDatosEtCote(DATOSETCOTYPE datos) {
		if(datos != null){
			params.setSchoolWorkshopData(true);
			params.setSchoolWorkshop(SchoolWorkshop.valueOf("SW_"+datos.getCODIGOETCOTE()));
		}
	}
	private void completeDatosEtt(DATOSETTTYPE datos) {
		if(datos != null){
			params.setEttData(true);
			params.setEttCif(datos.getCIFNIFEMPRESAUSUARIA().getCIFNIF());
			params.setEttName(datos.getRAZONSOCIALEMPRESAUSUARIA());
			params.setEttContractTemplate(!StringUtils.isBlank(datos.getINDCTOPLANTILLA()) && datos.getINDCTOPLANTILLA().equals("S"));
			params.setEttForeignEnterprise(!StringUtils.isBlank(datos.getINDEMPRESAEXTRANJERA()) && datos.getINDEMPRESAEXTRANJERA().equals("S"));
		}
	}
	private void completeDatosContratoExtranjero(DATOSCONTRATOEXTRANJEROTYPE datos) {
		if(datos != null){
			params.setAnnexData(true);
			params.setAnexEmploymentYear(datos.getAÑOCONTINGENTE());
			params.setEmploymentCharacter(datos.getINDCARACTEROFERTA());
		}
	}
	private void completeDatosComunicacionCopiaBasica(DATOSCOMUNICACOPIABASICATYPE datos) {
		if(datos != null){
			params.setEttData(true);
			params.setBasicCopyComments(datos.getTEXTOCOPIABASICA());
			params.setBasicCopySignatureType(BasicCopySignatureType.valueOf("BCST"+datos.getTIPOFIRMA()));
		}
	}

	private void completeDatosUsoLibreEmpresa(DATOSUSOLIBREEMPRESATYPE datos) {
		if(datos != null){
			params.setEnterpriseFreeUse(datos.getUSOLIBREEMPRESA());
		}
	}
	
	private void completeDatosContratoTiempoParcial(DATOSCONTRATOTIEMPOPARCIALTYPE datos) {
		if(datos != null){
			params.setActividadsinfechacierta(datos.getACTIVIDADSINFECHACIERTA());
			params.setColectivoedad(datos.getCOLECTIVOEDAD());
			params.setFijodiscontinuoperiodico(datos.getFIJODISCONTINUOPERIODICO().equals("S"));
			params.setHorasanualestiempocompleto(datos.getHORASANUALESTIEMPOCOMPLETO());
			params.setHorasconvenio(getHoras(datos.getHORASCONVENIO()));
			params.setMinutosconvenio(getMinutos(datos.getHORASCONVENIO()));
			params.setHorasformacion(getHoras(datos.getHORASFORMACION()));
			params.setMinutosformacion(getMinutos(datos.getHORASFORMACION()));
			params.setHorasjornada(getHoras(datos.getHORASJORNADA()));
			params.setMinutosjornada(getMinutos(datos.getHORASJORNADA()));
			params.setIndicformacionteorica(datos.getINDICFORMACIONTEORICA());
			params.setPorcentajejubilacionparcial(datos.getPORCENTAJEJUBILACIONPARCIAL());
			params.setPorcjornadapactada(datos.getPORCJORNADAPACTADA());
			params.setTipojornada(WorkingDayType.enumByValue(datos.getTIPOJORNADA()));
		}
	}
	private String getHoras(String duracion){
		return StringUtils.isBlank(duracion)?null:duracion.substring(0,4);
	}
	private String getMinutos(String duracion){
		return StringUtils.isBlank(duracion)?null:duracion.substring(4,6);
	}
	private void completeDatosReduccionRdl2011(DATOSREDUCCIONRDL12011TYPE datos) {
		// TODO
	}
	private void completeDatosBonificacion(DATOSBONIFICACIONTYPE datos) {
		// TODO
	}
	private void completeDatosEmpresaInsercion(DATOSEMPRESAINSERCIONTYPE dato) {
		// TODO
	}
	private void completeDatosCopiaBasica(DATOSCOPIABASICATYPE datos) {
		// TODO
	}
	private void completeDatosProgramaEmpleoPublico(DATOSPROGEMPLEOPUBLICOTYPE datos) {
		// TODO
		if(datos != null){
			params.setEmploymentProgramData(true);
//			datos.setACTUACION("");
//			datos.setCORPORACIONLOCAL("");
//			datos.setEJERCICIOPRESUPUESTARIO("");
//			datos.setGRUPOCOTIZACIONCORPORACIONLOCAL("");
		}
	}
	private void completeDatosContratoInvestigacion(DATOSCONTRATOINVESTIGACIONTYPE datos) {
		if(datos != null){
			params.setIndempleador(ResearchEmployer.enumByValue(datos.getINDEMPLEADOR()));
			params.setIndtrabajador(ResearchEmployee.enumByValue(datos.getINDTRABAJADOR()));
			params.setIndrd632006(datos.getINDRD632006().equals("S"));
		}
	}
	private void completeDatosContratoInsercion(DATOSCONTRATOINSERCIONTYPE datos) {
		// TODO
	}
	private void completeDatosContratoInterinidad(DATOSCONTRATOINTERINIDADTYPE datos) {
		if(datos != null){
			params.setInterimData(true);
			params.setCausaInterinidad(InterimCause.valueOf(datos.getCAUSAINTERINIDAD()));
		}
	}
	private void completeDatosContratoPracticas(DATOSCONTRATOPRACTICASTYPE datos) {
		// TODO
	}
	private void completeDatosExclusionSocial(DATOSEXCLUSIONSOCIALTYPE datos) {
		// TODO
	}
	
	
	
}

