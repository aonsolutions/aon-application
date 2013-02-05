package com.esferalia.aon.ui.payroll.file;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.company.Enterprise;
import com.code.aon.person.Person;
import com.code.aon.person.enumeration.Gender;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.enumeration.DocumentType;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.file.payroll.contract.model.IContratoType;
import com.esferalia.aon.file.payroll.contrata.model.contratos.CIFNIFTYPE;
import com.esferalia.aon.file.payroll.contrata.model.contratos.CONTRATO100TYPE;
import com.esferalia.aon.file.payroll.contrata.model.contratos.CONTRATO130TYPE;
import com.esferalia.aon.file.payroll.contrata.model.contratos.CONTRATO150TYPE;
import com.esferalia.aon.file.payroll.contrata.model.contratos.CONTRATO200TYPE;
import com.esferalia.aon.file.payroll.contrata.model.contratos.CONTRATO230TYPE;
import com.esferalia.aon.file.payroll.contrata.model.contratos.CONTRATO250TYPE;
import com.esferalia.aon.file.payroll.contrata.model.contratos.CONTRATO300TYPE;
import com.esferalia.aon.file.payroll.contrata.model.contratos.CONTRATO330TYPE;
import com.esferalia.aon.file.payroll.contrata.model.contratos.CONTRATO350TYPE;
import com.esferalia.aon.file.payroll.contrata.model.contratos.CONTRATO401TYPE;
import com.esferalia.aon.file.payroll.contrata.model.contratos.CONTRATO402TYPE;
import com.esferalia.aon.file.payroll.contrata.model.contratos.CONTRATO403TYPE;
import com.esferalia.aon.file.payroll.contrata.model.contratos.CONTRATO410TYPE;
import com.esferalia.aon.file.payroll.contrata.model.contratos.CONTRATO420TYPE;
import com.esferalia.aon.file.payroll.contrata.model.contratos.CONTRATO421TYPE;
import com.esferalia.aon.file.payroll.contrata.model.contratos.CONTRATO430TYPE;
import com.esferalia.aon.file.payroll.contrata.model.contratos.CONTRATO441TYPE;
import com.esferalia.aon.file.payroll.contrata.model.contratos.CONTRATO450TYPE;
import com.esferalia.aon.file.payroll.contrata.model.contratos.CONTRATO452TYPE;
import com.esferalia.aon.file.payroll.contrata.model.contratos.CONTRATO501TYPE;
import com.esferalia.aon.file.payroll.contrata.model.contratos.CONTRATO502TYPE;
import com.esferalia.aon.file.payroll.contrata.model.contratos.CONTRATO503TYPE;
import com.esferalia.aon.file.payroll.contrata.model.contratos.CONTRATO510TYPE;
import com.esferalia.aon.file.payroll.contrata.model.contratos.CONTRATO520TYPE;
import com.esferalia.aon.file.payroll.contrata.model.contratos.CONTRATO530TYPE;
import com.esferalia.aon.file.payroll.contrata.model.contratos.CONTRATO540TYPE;
import com.esferalia.aon.file.payroll.contrata.model.contratos.CONTRATO541TYPE;
import com.esferalia.aon.file.payroll.contrata.model.contratos.CONTRATO550TYPE;
import com.esferalia.aon.file.payroll.contrata.model.contratos.CONTRATO552TYPE;
import com.esferalia.aon.file.payroll.contrata.model.contratos.CONTRATO970TYPE;
import com.esferalia.aon.file.payroll.contrata.model.contratos.CONTRATO980TYPE;
import com.esferalia.aon.file.payroll.contrata.model.contratos.CONTRATO990TYPE;
import com.esferalia.aon.file.payroll.contrata.model.contratos.DATOSANEXOCONTRATORELEVOTYPE;
import com.esferalia.aon.file.payroll.contrata.model.contratos.DATOSBONIFICACIONTYPE;
import com.esferalia.aon.file.payroll.contrata.model.contratos.DATOSCOMUNICACOPIABASICATYPE;
import com.esferalia.aon.file.payroll.contrata.model.contratos.DATOSCONTRATOEXTRANJEROTYPE;
import com.esferalia.aon.file.payroll.contrata.model.contratos.DATOSCONTRATOINSERCIONTYPE;
import com.esferalia.aon.file.payroll.contrata.model.contratos.DATOSCONTRATOINTERINIDADTYPE;
import com.esferalia.aon.file.payroll.contrata.model.contratos.DATOSCONTRATOINVESTIGACIONTYPE;
import com.esferalia.aon.file.payroll.contrata.model.contratos.DATOSCONTRATOPRACTICASTYPE;
import com.esferalia.aon.file.payroll.contrata.model.contratos.DATOSCONTRATOTIEMPOPARCIALTYPE;
import com.esferalia.aon.file.payroll.contrata.model.contratos.DATOSCOPIABASICATYPE;
import com.esferalia.aon.file.payroll.contrata.model.contratos.DATOSEMPRESAINSERCIONTYPE;
import com.esferalia.aon.file.payroll.contrata.model.contratos.DATOSEMPRESATYPE;
import com.esferalia.aon.file.payroll.contrata.model.contratos.DATOSETCOTYPE;
import com.esferalia.aon.file.payroll.contrata.model.contratos.DATOSETTTYPE;
import com.esferalia.aon.file.payroll.contrata.model.contratos.DATOSEXCLUSIONSOCIALTYPE;
import com.esferalia.aon.file.payroll.contrata.model.contratos.DATOSGENERALESCONTRATOTYPE;
import com.esferalia.aon.file.payroll.contrata.model.contratos.DATOSMEDIDASFOMENTOTYPE;
import com.esferalia.aon.file.payroll.contrata.model.contratos.DATOSPROGEMPLEOPUBLICOTYPE;
import com.esferalia.aon.file.payroll.contrata.model.contratos.DATOSREDUCCIONRDL12011TYPE;
import com.esferalia.aon.file.payroll.contrata.model.contratos.DATOSTRABAJADORTYPE;
import com.esferalia.aon.file.payroll.contrata.model.contratos.DATOSUSOLIBREEMPRESATYPE;
import com.esferalia.aon.file.payroll.contrata.model.contratos.NOMBREAPELLIDOSTYPE;
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.payroll.EnterpriseCCC;
import com.esferalia.aon.payroll.enumeration.CCCType;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.payroll.enumeration.ContractCode;
import com.esferalia.aon.ui.payroll.utils.PayrollUtils;

public class ContrataContratosWriter {
	
	private final String ZERO_VALUE = "0";
	
	private com.esferalia.aon.file.payroll.contrata.model.contratos.ObjectFactory contratoFactory = new com.esferalia.aon.file.payroll.contrata.model.contratos.ObjectFactory();
	
	public IContratoType createFile(IContratoType contratoType, ContrataParams params) throws ManagerBeanException{
		writeContratosMainData(contratoType, params);
		String code = getContractDataMap(params.getContract()).get(ContextVariable.TC2.getName());
		if (code.equals(ContractCode.C100.getValue())) {
			return createContract100(contratoType, params);
		} else if (code.equals(ContractCode.C130.getValue())) {
			return createContract130(contratoType, params);
		} else if (code.equals(ContractCode.C150.getValue())) {
			return createContract150(contratoType, params);
		} else if (code.equals(ContractCode.C200.getValue())) {
			return createContract200(contratoType, params);
		} else if (code.equals(ContractCode.C230.getValue())) {
			return createContract230(contratoType, params);
		} else if (code.equals(ContractCode.C250.getValue())) {
			return createContract250(contratoType, params);
		} else if (code.equals(ContractCode.C300.getValue())) {
			return createContract300(contratoType, params);
		} else if (code.equals(ContractCode.C330.getValue())) {
			return createContract330(contratoType, params);
		} else if (code.equals(ContractCode.C350.getValue())) {
			return createContract350(contratoType, params);
		} else if (code.equals(ContractCode.C401.getValue())) {
			return createContract401(contratoType, params);
		} else if (code.equals(ContractCode.C402.getValue())) {
			return createContract402(contratoType, params);
		} else if (code.equals(ContractCode.C403.getValue())) {
			return createContract403(contratoType, params);
		} else if (code.equals(ContractCode.C410.getValue())) {
			return createContract410(contratoType, params);
		} else if (code.equals(ContractCode.C420.getValue())) {
			return createContract420(contratoType, params);
		} else if (code.equals(ContractCode.C421.getValue())) {
			return createContract421(contratoType, params);
		} else if (code.equals(ContractCode.C430.getValue())) {
			return createContract430(contratoType, params);
		} else if (code.equals(ContractCode.C441.getValue())) {
			return createContract441(contratoType, params);
		} else if (code.equals(ContractCode.C450.getValue())) {
			return createContract450(contratoType, params);
		} else if (code.equals(ContractCode.C452.getValue())) {
			return createContract452(contratoType, params);
		} else if (code.equals(ContractCode.C501.getValue())) {
			return createContract501(contratoType, params);
		} else if (code.equals(ContractCode.C502.getValue())) {
			return createContract502(contratoType, params);
		} else if (code.equals(ContractCode.C503.getValue())) {
			return createContract503(contratoType, params);
		} else if (code.equals(ContractCode.C510.getValue())) {
			return createContract510(contratoType, params);
		} else if (code.equals(ContractCode.C520.getValue())) {
			return createContract520(contratoType, params);
		} else if (code.equals(ContractCode.C530.getValue())) {
			return createContract530(contratoType, params);
		} else if (code.equals(ContractCode.C540.getValue())) {
			return createContract540(contratoType, params);
		} else if (code.equals(ContractCode.C541.getValue())) {
			return createContract541(contratoType, params);
		} else if (code.equals(ContractCode.C550.getValue())) {
			return createContract550(contratoType, params);
		} else if (code.equals(ContractCode.C552.getValue())) {
			return createContract552(contratoType, params);
		} else if (code.equals(ContractCode.C970.getValue())) {
			return createContract970(contratoType, params);
		} else if (code.equals(ContractCode.C980.getValue())) {
			return createContract980(contratoType, params);
		} else if (code.equals(ContractCode.C990.getValue())) {
			return createContract990(contratoType, params);
		}
		return null;
	}
	
	private void writeContratosMainData(IContratoType contract, ContrataParams params) throws ManagerBeanException{
		contract.setDATOSEMPRESA(createDatosEmpresa(params));
		contract.setDATOSTRABAJADOR(createDatosTrabajador(params));
		contract.setDATOSGENERALESCONTRATO(createDatosGeneralesContrato(params)); 
		if(params.isEttData()){
			contract.setDATOSETT(createDatosEtt(params));
		}
		contract.setDATOSCOMUNICACOPIABASICA(createDatosComunicacionCopiaBasica(params)); 
		contract.setDATOSUSOLIBREEMPRESA(createDatosUsoLibreEmpresa(params));
	}
	
	private CONTRATO100TYPE createContract100(IContratoType contratoType, ContrataParams contrataParams) throws ManagerBeanException{
		CONTRATO100TYPE c = (CONTRATO100TYPE) contratoType;
		c.setDATOSMEDIDASFOMENTO(createDatosMedidasFomento(contrataParams));
		if(contrataParams.isAnnexData()){
			c.setDATOSANEXOCONTRATORELEVO(createDatosAnexoContratoRelevo(contrataParams));
		}
		if(contrataParams.isSchoolWorkshopData()){
			c.setDATOSETCOTE(createDatosEtCote(contrataParams));
		}
		c.setDATOSCONTRATOEXTRANJERO(createDatosContratoExtranjero(contrataParams));
		return c;
	}
	private CONTRATO130TYPE createContract130(IContratoType contratoType, ContrataParams contrataParams) throws ManagerBeanException{
		CONTRATO130TYPE c = (CONTRATO130TYPE) contratoType;
	    c.setDATOSBONIFICACION(createDatosBonificacion(contrataParams));
	    c.setDATOSMEDIDASFOMENTO(createDatosMedidasFomento(contrataParams));
	    c.setDATOSANEXOCONTRATORELEVO(createDatosAnexoContratoRelevo(contrataParams));
	    c.setDATOSETCOTE(createDatosEtCote(contrataParams));
	    return c;
	}
	private CONTRATO150TYPE createContract150(IContratoType contratoType, ContrataParams contrataParams) throws ManagerBeanException{
		CONTRATO150TYPE c = (CONTRATO150TYPE) contratoType;
		c.setDATOSBONIFICACION(createDatosBonificacion(contrataParams));
		c.setDATOSMEDIDASFOMENTO(createDatosMedidasFomento(contrataParams));
		c.setDATOSANEXOCONTRATORELEVO(createDatosAnexoContratoRelevo(contrataParams));
		c.setDATOSETCOTE(createDatosEtCote(contrataParams));
		c.setDATOSEMPRESAINSERCION(createDatosEmpresaInsercion());
		return c;
	}
	private CONTRATO200TYPE createContract200(IContratoType contratoType, ContrataParams contrataParams) throws ManagerBeanException{
		CONTRATO200TYPE c = (CONTRATO200TYPE) contratoType;
		c.setDATOSMEDIDASFOMENTO(createDatosMedidasFomento(contrataParams));
		c.setDATOSANEXOCONTRATORELEVO(createDatosAnexoContratoRelevo(contrataParams));
		c.setDATOSETCOTE(createDatosEtCote(contrataParams));
		c.setDATOSCONTRATOEXTRANJERO(createDatosContratoExtranjero(contrataParams));
		c.setDATOSREDUCCIONRDL12011(createDatosReduccionRdl2011(contrataParams));
		c.setDATOSCONTRATOTIEMPOPARCIAL(createDatosContratoTiempoParcial(contrataParams));
		return c;
	}
	private CONTRATO230TYPE createContract230(IContratoType contratoType, ContrataParams contrataParams) throws ManagerBeanException{
		CONTRATO230TYPE c = (CONTRATO230TYPE) contratoType;
		c.setDATOSCONTRATOTIEMPOPARCIAL(createDatosContratoTiempoParcial(contrataParams));
		c.setDATOSBONIFICACION(createDatosBonificacion(contrataParams));
		c.setDATOSMEDIDASFOMENTO(createDatosMedidasFomento(contrataParams));
		c.setDATOSANEXOCONTRATORELEVO(createDatosAnexoContratoRelevo(contrataParams));
		c.setDATOSETCOTE(createDatosEtCote(contrataParams));
		c.setDATOSREDUCCIONRDL12011(createDatosReduccionRdl2011(contrataParams));
		return c;
	}
	private CONTRATO250TYPE createContract250(IContratoType contratoType, ContrataParams contrataParams) throws ManagerBeanException{
		CONTRATO250TYPE c = (CONTRATO250TYPE) contratoType;
		c.setDATOSCONTRATOTIEMPOPARCIAL(createDatosContratoTiempoParcial(contrataParams));
		c.setDATOSBONIFICACION(createDatosBonificacion(contrataParams));
		c.setDATOSMEDIDASFOMENTO(createDatosMedidasFomento(contrataParams));
		c.setDATOSANEXOCONTRATORELEVO(createDatosAnexoContratoRelevo(contrataParams));
		c.setDATOSETCOTE(createDatosEtCote(contrataParams));
		c.setDATOSEMPRESAINSERCION(createDatosEmpresaInsercion());
		c.setDATOSREDUCCIONRDL12011(createDatosReduccionRdl2011(contrataParams));
		return c;
	}
	private CONTRATO300TYPE createContract300(IContratoType contratoType, ContrataParams contrataParams) throws ManagerBeanException{
		CONTRATO300TYPE c = (CONTRATO300TYPE) contratoType;
	    c.setDATOSCONTRATOTIEMPOPARCIAL(createDatosContratoTiempoParcial(contrataParams));
	    c.setDATOSMEDIDASFOMENTO(createDatosMedidasFomento(contrataParams));
	    c.setDATOSANEXOCONTRATORELEVO(createDatosAnexoContratoRelevo(contrataParams));
	    c.setDATOSETCOTE(createDatosEtCote(contrataParams));
	    c.setDATOSREDUCCIONRDL12011(createDatosReduccionRdl2011(contrataParams));
	    return c;
	}
	private CONTRATO330TYPE createContract330(IContratoType contratoType, ContrataParams contrataParams) throws ManagerBeanException{
		CONTRATO330TYPE c = (CONTRATO330TYPE) contratoType;
		c.setDATOSCONTRATOTIEMPOPARCIAL(createDatosContratoTiempoParcial(contrataParams));
		c.setDATOSBONIFICACION(createDatosBonificacion(contrataParams));
		c.setDATOSMEDIDASFOMENTO(createDatosMedidasFomento(contrataParams));
		c.setDATOSANEXOCONTRATORELEVO(createDatosAnexoContratoRelevo(contrataParams));
		c.setDATOSETCOTE(createDatosEtCote(contrataParams));
		c.setDATOSREDUCCIONRDL12011(createDatosReduccionRdl2011(contrataParams));
		return c;
	}
	private CONTRATO350TYPE createContract350(IContratoType contratoType, ContrataParams contrataParams) throws ManagerBeanException{
		CONTRATO350TYPE c = (CONTRATO350TYPE) contratoType;
		c.setDATOSCONTRATOTIEMPOPARCIAL(createDatosContratoTiempoParcial(contrataParams));
		c.setDATOSBONIFICACION(createDatosBonificacion(contrataParams));
		c.setDATOSMEDIDASFOMENTO(createDatosMedidasFomento(contrataParams));
		c.setDATOSANEXOCONTRATORELEVO(createDatosAnexoContratoRelevo(contrataParams));
		c.setDATOSETCOTE(createDatosEtCote(contrataParams));
		c.setDATOSEMPRESAINSERCION(createDatosEmpresaInsercion());
		c.setDATOSREDUCCIONRDL12011(createDatosReduccionRdl2011(contrataParams));
		return c;
	}
	private CONTRATO401TYPE createContract401(IContratoType contratoType, ContrataParams contrataParams) throws ManagerBeanException{
		CONTRATO401TYPE c = (CONTRATO401TYPE) contratoType;
		c.setDATOSETCOTE(createDatosEtCote(contrataParams));
		c.setDATOSCONTRATOINVESTIGACION(createDatosContratoInvestigacion(contrataParams));
		c.setPROGEMPLEOPUBLICO(createDatosProgramaEmpleoPublico(contrataParams));
		c.setDATOSCONTRATOEXTRANJERO(createDatosContratoExtranjero(contrataParams));
		return c;
	}
	private CONTRATO402TYPE createContract402(IContratoType contratoType, ContrataParams contrataParams) throws ManagerBeanException{
		CONTRATO402TYPE c = (CONTRATO402TYPE) contratoType;
		c.setDATOSETCOTE(createDatosEtCote(contrataParams));
		c.setDATOSCOPIABASICA(createDatosCopiaBasica(contrataParams));
		c.setPROGEMPLEOPUBLICO(createDatosProgramaEmpleoPublico(contrataParams));
		c.setDATOSCONTRATOEXTRANJERO(createDatosContratoExtranjero(contrataParams));
		return c;
	}
	private CONTRATO403TYPE createContract403(IContratoType contratoType, ContrataParams contrataParams) throws ManagerBeanException{
		CONTRATO403TYPE c = (CONTRATO403TYPE) contratoType;
		c.setPROGEMPLEOPUBLICO(createDatosProgramaEmpleoPublico(contrataParams));
		c.setDATOSCONTRATOINSERCION(createDatosContratoInsercion(contrataParams));
		return c;
	}
	private CONTRATO410TYPE createContract410(IContratoType contratoType, ContrataParams contrataParams) throws ManagerBeanException{
		CONTRATO410TYPE c = (CONTRATO410TYPE) contratoType;
		c.setDATOSCONTRATOINTERINIDAD(createDatosContratoInterinidad(contrataParams));
		c.setDATOSETCOTE(createDatosEtCote(contrataParams));
		c.setPROGEMPLEOPUBLICO(createDatosProgramaEmpleoPublico(contrataParams));
		return c;
	}
	private CONTRATO420TYPE createContract420(IContratoType contratoType, ContrataParams contrataParams) throws ManagerBeanException{
		CONTRATO420TYPE c = (CONTRATO420TYPE) contratoType;
		c.setDATOSCONTRATOPRACTICAS(createDatosContratoPracticas(contrataParams));
		c.setDATOSETCOTE(createDatosEtCote(contrataParams));
		c.setDATOSCONTRATOINVESTIGACION(createDatosContratoInvestigacion(contrataParams));
		c.setPROGEMPLEOPUBLICO(createDatosProgramaEmpleoPublico(contrataParams));
		return c;
	}
	private CONTRATO421TYPE createContract421(IContratoType contratoType, ContrataParams contrataParams) throws ManagerBeanException{
		CONTRATO421TYPE c = (CONTRATO421TYPE) contratoType;
		c.setDATOSCONTRATOTIEMPOPARCIAL(createDatosContratoTiempoParcial(contrataParams));
		c.setDATOSETCOTE(createDatosEtCote(contrataParams));
		c.setPROGEMPLEOPUBLICO(createDatosProgramaEmpleoPublico(contrataParams));
		return c;
	}
	private CONTRATO430TYPE createContract430(IContratoType contratoType, ContrataParams contrataParams) throws ManagerBeanException{
		CONTRATO430TYPE c = (CONTRATO430TYPE) contratoType;
		c.setDATOSBONIFICACION(createDatosBonificacion(contrataParams));
		c.setDATOSETCOTE(createDatosEtCote(contrataParams));
		c.setPROGEMPLEOPUBLICO(createDatosProgramaEmpleoPublico(contrataParams));
		return c;
	}
	private CONTRATO441TYPE createContract441(IContratoType contratoType, ContrataParams contrataParams) throws ManagerBeanException{
		CONTRATO441TYPE c = (CONTRATO441TYPE) contratoType;
		c.setDATOSANEXOCONTRATORELEVO(createDatosAnexoContratoRelevo(contrataParams));
		c.setDATOSETCOTE(createDatosEtCote(contrataParams));
		return c;
	}
	private CONTRATO450TYPE createContract450(IContratoType contratoType, ContrataParams contrataParams) throws ManagerBeanException{
		CONTRATO450TYPE c = (CONTRATO450TYPE) contratoType;
		c.setDATOSEXCLUSIONSOCIAL(createDatosExclusionSocial());
		c.setDATOSBONIFICACION(createDatosBonificacion(contrataParams));
		c.setDATOSCONTRATOPRACTICAS(createDatosContratoPracticas(contrataParams));
		c.setDATOSCONTRATOTIEMPOPARCIAL(createDatosContratoTiempoParcial(contrataParams));
		c.setDATOSCONTRATOINTERINIDAD(createDatosContratoInterinidad(contrataParams));
		c.setDATOSETCOTE(createDatosEtCote(contrataParams));
		c.setDATOSCOPIABASICA(createDatosCopiaBasica(contrataParams));
		c.setPROGEMPLEOPUBLICO(createDatosProgramaEmpleoPublico(contrataParams));
		c.setDATOSEMPRESAINSERCION(createDatosEmpresaInsercion());
		return c;
	}
	private CONTRATO452TYPE createContract452(IContratoType contratoType, ContrataParams contrataParams) throws ManagerBeanException{
		CONTRATO452TYPE c = (CONTRATO452TYPE) contratoType;
		c.setDATOSBONIFICACION(createDatosBonificacion(contrataParams));
		c.setDATOSETCOTE(createDatosEtCote(contrataParams));
		c.setDATOSCOPIABASICA(createDatosCopiaBasica(contrataParams));
		c.setPROGEMPLEOPUBLICO(createDatosProgramaEmpleoPublico(contrataParams));
		c.setDATOSEMPRESAINSERCION(createDatosEmpresaInsercion());
		return c;
	}
	private CONTRATO501TYPE createContract501(IContratoType contratoType, ContrataParams contrataParams) throws ManagerBeanException{
		CONTRATO501TYPE c = (CONTRATO501TYPE) contratoType;
		c.setDATOSCONTRATOTIEMPOPARCIAL(createDatosContratoTiempoParcial(contrataParams));
		c.setDATOSETCOTE(createDatosEtCote(contrataParams));
		c.setDATOSCONTRATOINVESTIGACION(createDatosContratoInvestigacion(contrataParams));
		c.setPROGEMPLEOPUBLICO(createDatosProgramaEmpleoPublico(contrataParams));
		c.setDATOSCONTRATOEXTRANJERO(createDatosContratoExtranjero(contrataParams));
		c.setDATOSREDUCCIONRDL12011(createDatosReduccionRdl2011(contrataParams));
		return c;
	}
	private CONTRATO502TYPE createContract502(IContratoType contratoType, ContrataParams contrataParams) throws ManagerBeanException{
		CONTRATO502TYPE c = (CONTRATO502TYPE) contratoType;
		c.setDATOSCONTRATOTIEMPOPARCIAL(createDatosContratoTiempoParcial(contrataParams));
		c.setDATOSETCOTE(createDatosEtCote(contrataParams));
		c.setPROGEMPLEOPUBLICO(createDatosProgramaEmpleoPublico(contrataParams));
		c.setDATOSCONTRATOEXTRANJERO(createDatosContratoExtranjero(contrataParams));
		c.setDATOSREDUCCIONRDL12011(createDatosReduccionRdl2011(contrataParams));
		return c;
	}
	private CONTRATO503TYPE createContract503(IContratoType contratoType, ContrataParams contrataParams) throws ManagerBeanException{
		CONTRATO503TYPE c = (CONTRATO503TYPE) contratoType;
		c.setDATOSCONTRATOTIEMPOPARCIAL(createDatosContratoTiempoParcial(contrataParams));
		c.setPROGEMPLEOPUBLICO(createDatosProgramaEmpleoPublico(contrataParams));
		c.setDATOSCONTRATOINSERCION(createDatosContratoInsercion(contrataParams));
		return c;
	}
	private CONTRATO510TYPE createContract510(IContratoType contratoType, ContrataParams contrataParams) throws ManagerBeanException{
		CONTRATO510TYPE c = (CONTRATO510TYPE) contratoType;
		c.setDATOSCONTRATOTIEMPOPARCIAL(createDatosContratoTiempoParcial(contrataParams));
		c.setDATOSCONTRATOINTERINIDAD(createDatosContratoInterinidad(contrataParams));
		c.setDATOSETCOTE(createDatosEtCote(contrataParams));
		c.setPROGEMPLEOPUBLICO(createDatosProgramaEmpleoPublico(contrataParams));
		return c;
	}
	private CONTRATO520TYPE createContract520(IContratoType contratoType, ContrataParams contrataParams) throws ManagerBeanException{
		CONTRATO520TYPE c = (CONTRATO520TYPE) contratoType;
		c.setDATOSCONTRATOPRACTICAS(createDatosContratoPracticas(contrataParams));
		c.setDATOSCONTRATOTIEMPOPARCIAL(createDatosContratoTiempoParcial(contrataParams));
		c.setDATOSETCOTE(createDatosEtCote(contrataParams));
		c.setDATOSCONTRATOINVESTIGACION(createDatosContratoInvestigacion(contrataParams));
		c.setPROGEMPLEOPUBLICO(createDatosProgramaEmpleoPublico(contrataParams));
		c.setDATOSREDUCCIONRDL12011(createDatosReduccionRdl2011(contrataParams));
		return c;
	}
	private CONTRATO530TYPE createContract530(IContratoType contratoType, ContrataParams contrataParams) throws ManagerBeanException{
		CONTRATO530TYPE c = (CONTRATO530TYPE) contratoType;
		c.setDATOSBONIFICACION(createDatosBonificacion(contrataParams));
		c.setDATOSCONTRATOTIEMPOPARCIAL(createDatosContratoTiempoParcial(contrataParams));
		c.setDATOSETCOTE(createDatosEtCote(contrataParams));
		c.setPROGEMPLEOPUBLICO(createDatosProgramaEmpleoPublico(contrataParams));
		c.setDATOSREDUCCIONRDL12011(createDatosReduccionRdl2011(contrataParams));
		return c;
	}
	private CONTRATO540TYPE createContract540(IContratoType contratoType, ContrataParams contrataParams) throws ManagerBeanException{
		CONTRATO540TYPE c = (CONTRATO540TYPE) contratoType;
		c.setDATOSCONTRATOTIEMPOPARCIAL(createDatosContratoTiempoParcial(contrataParams));
		c.setDATOSETCOTE(createDatosEtCote(contrataParams));
		return c;
	}
	private CONTRATO541TYPE createContract541(IContratoType contratoType, ContrataParams contrataParams) throws ManagerBeanException{
		CONTRATO541TYPE c = (CONTRATO541TYPE) contratoType;
		c.setDATOSCONTRATOTIEMPOPARCIAL(createDatosContratoTiempoParcial(contrataParams));
		c.setDATOSANEXOCONTRATORELEVO(createDatosAnexoContratoRelevo(contrataParams));
		c.setDATOSETCOTE(createDatosEtCote(contrataParams));
		return c;
	}
	private CONTRATO550TYPE createContract550(IContratoType contratoType, ContrataParams contrataParams) throws ManagerBeanException{
		CONTRATO550TYPE c = (CONTRATO550TYPE) contratoType;
		c.setDATOSEXCLUSIONSOCIAL(createDatosExclusionSocial());
		c.setDATOSBONIFICACION(createDatosBonificacion(contrataParams));
		c.setDATOSCONTRATOPRACTICAS(createDatosContratoPracticas(contrataParams));
		c.setDATOSCONTRATOTIEMPOPARCIAL(createDatosContratoTiempoParcial(contrataParams));
		c.setDATOSCONTRATOINTERINIDAD(createDatosContratoInterinidad(contrataParams));
		c.setDATOSETCOTE(createDatosEtCote(contrataParams));
		c.setDATOSCOPIABASICA(createDatosCopiaBasica(contrataParams));
		c.setPROGEMPLEOPUBLICO(createDatosProgramaEmpleoPublico(contrataParams));
		c.setDATOSEMPRESAINSERCION(createDatosEmpresaInsercion());
		c.setDATOSREDUCCIONRDL12011(createDatosReduccionRdl2011(contrataParams));
		return c;
	}
	private CONTRATO552TYPE createContract552(IContratoType contratoType, ContrataParams contrataParams) throws ManagerBeanException{
		CONTRATO552TYPE c = (CONTRATO552TYPE) contratoType;
		c.setDATOSBONIFICACION(createDatosBonificacion(contrataParams));
		c.setDATOSCONTRATOTIEMPOPARCIAL(createDatosContratoTiempoParcial(contrataParams));
		c.setDATOSETCOTE(createDatosEtCote(contrataParams));
		c.setDATOSCOPIABASICA(createDatosCopiaBasica(contrataParams));
		c.setPROGEMPLEOPUBLICO(createDatosProgramaEmpleoPublico(contrataParams));
		c.setDATOSEMPRESAINSERCION(createDatosEmpresaInsercion());
		return c;
	}
	private CONTRATO970TYPE createContract970(IContratoType contratoType, ContrataParams contrataParams) throws ManagerBeanException{
		CONTRATO970TYPE c = (CONTRATO970TYPE) contratoType;
		c.setDATOSETCOTE(createDatosEtCote(contrataParams));
		c.setPROGEMPLEOPUBLICO(createDatosProgramaEmpleoPublico(contrataParams));
		return c;
	}
	private CONTRATO980TYPE createContract980(IContratoType contratoType, ContrataParams contrataParams) throws ManagerBeanException{
		CONTRATO980TYPE c = (CONTRATO980TYPE) contratoType;
		c.setDATOSETCOTE(createDatosEtCote(contrataParams));
		return c;
	}
	private CONTRATO990TYPE createContract990(IContratoType contratoType, ContrataParams contrataParams) throws ManagerBeanException{
		CONTRATO990TYPE c = (CONTRATO990TYPE) contratoType;
		c.setDATOSETCOTE(createDatosEtCote(contrataParams));
		c.setDATOSCOPIABASICA(createDatosCopiaBasica(contrataParams));
		c.setPROGEMPLEOPUBLICO(createDatosProgramaEmpleoPublico(contrataParams));
		return c;
	}
	private DATOSEMPRESATYPE createDatosEmpresa(ContrataParams params) throws ManagerBeanException {
		DATOSEMPRESATYPE datos = contratoFactory.createDATOSEMPRESATYPE();
		datos.setCIFNIFEMPRESA(createCifNif(params.getContract().getWorkPlace().getEnterprise().getRegistry().getDocument()));
		datos.setCODIGOCUENTACOTIZACION(completeLength(getEnterpriseCCC(params.getContract().getWorkPlace().getEnterprise()),15,"0",false));
		return datos;
	}

	private DATOSTRABAJADORTYPE createDatosTrabajador(ContrataParams params) throws ManagerBeanException {
		Person person = params.getContract().getPerson();
		DATOSTRABAJADORTYPE datos = contratoFactory.createDATOSTRABAJADORTYPE(); 
		if(person.getBirthDate()!=null){
			datos.setFECHANACIMIENTO(getFormatedDate(person.getBirthDate()));
		} else {
			AonUtil.addErrorMessage("El trabajador no tiene definida la fecha de nacimiento.");
		}
		/*
		"D";"D.N.I"
		"E";"NUMERO IDENTIFICATIVO EXTRANJERO"
		"U";"CIUDADANOS DE LA UE/EEE SIN NIE"
		"W";"CIUD.QUE NO PERTENECEN A UE/EEE.SIN NIE"
		*/
		if(StringUtils.isEmpty(person.getRegistry().getDocument())){
			AonUtil.addErrorMessage("El trabajador no tiene definido el número de documento..");
		} else {
			if(person.getRegistry().getDocumentType()==DocumentType.NIF){
				datos.setIDENTIFICADORPFISICA("D"+person.getRegistry().getDocument());
			} else if(person.getRegistry().getDocumentType()==DocumentType.NIE){
				datos.setIDENTIFICADORPFISICA("E"+person.getRegistry().getDocument());
			}
		}
		datos.setMUNICIPIORESIDENCIA(params.getTownCode());
		datos.setNACIONALIDAD(completeLength(person.getRegistry().getNationality().getIsoNum(),3,ZERO_VALUE,false));
		datos.setNOMBREAPELLIDOS(createNombreApellidos(person));
		if(StringUtils.isEmpty(person.getSocialSecurityNumber())){
			AonUtil.addErrorMessage("El trabajador no tiene definido el número de seguridad social.");
		} else {
			datos.setNUMEROSEGURIDADSOCIAL(person.getSocialSecurityNumber());
		}
		if(person.getRegistry().getDefaultAddress()!=null){
			datos.setPAISRESIDENCIA(completeLength(person.getRegistry().getDefaultAddress().getRegistry().getNationality().getIsoNum(),3,ZERO_VALUE,false));
		} else {
			AonUtil.addErrorMessage("El trabajador no tiene definida la dirección.");
		}
		/*
		"1";"HOMBRE"
		"2";"MUJER"
		 */
		datos.setSEXO(person.getGender()==Gender.MALE?"1":"2");
		return datos;
	}

	private NOMBREAPELLIDOSTYPE createNombreApellidos(Person person) {
		NOMBREAPELLIDOSTYPE datos = contratoFactory.createNOMBREAPELLIDOSTYPE();
		datos.setNOMBRE(person!=null?person.getName():null);
		datos.setPRIMERAPELLIDO(person!=null?person.getFirstSurname():null);
		datos.setSEGUNDOAPELLIDO(person!=null?person.getSecondSurname():null);
		return datos;
	}

	private DATOSGENERALESCONTRATOTYPE createDatosGeneralesContrato(ContrataParams params) throws ManagerBeanException {
		DATOSGENERALESCONTRATOTYPE datos = contratoFactory.createDATOSGENERALESCONTRATOTYPE();
		datos.setFECHAINICIO(getFormatedDate(params.getContract().getStartDate()));
		datos.setFECHATERMINO(getFormatedDate(params.getContract().getEndDate()));
//		Indicador de convenio colectivo.   
//		Obligatorio para : 
//			- contratos de códigos 402 y 502 cuando su duración está entre 6 y 12 meses.   
//			- contratos de código 421 cuando su duración está entre 24 y 36 meses  
//			- contratos de código 401, 501, 450 con modalidad 401 y 550 con modalidad 501 iniciados a partir del 18/06/2010 
//				cuando su duración está entre 36 y 48 meses.    
//		Refleja la existencia ("S") o no existencia ("N") de un convenio colectivo que autorice estas duraciones. 
//		Para el resto de contratos que no se encuentran en uno de los casos anteriores, este elemento no debe aparecer en el fichero a enviar.
		datos.setINDCONVENIOCOLECTIVO(params.isCollectiveAgreement()?"S":null);
//		"11";"ESTUDIOS PRIMARIOS INCOMPLETOS"
//		"22";"PRIMERA ETAPA DE EDUCACIÓN SECUNDARIA SIN TÍTULO DE GRADUADO ESCOLAR O EQUIVALENTE"              
//		"23";"PRIMERA ETAPA DE EDUCACIÓN SECUNDARIA CON TÍTULO DE GRADUADO ESCOLAR O EQUIVALENTE"             
//		"32";"ENSEÑANZAS DE BACHILLERATO"
//		"33";"ENSEÑANZAS DE GRADO MEDIO DE FORMACIÓN PROFESIONAL ESPECÍFICA, ARTES PLÁSTICAS, DISEÑO Y DEPORTIVAS"                                            
//		"51";"ENSEÑANZAS DE GRADO SUPERIOR DE FORMACIÓN PROFESIONAL ESPECÍFICA Y EQUIVALENTE, ARTES PLÁSTICAS, DISEÑO Y DEPORTIVAS"                          
//		"54";"ENSEÑANZAS UNIVERSITARIAS DE PRIMER CICLO Y EQUIVALENTES O PERSONAS QUE HAN APROBADO 3 CURSOS COMPLETOS DE UNA LICENCIATURA O CRÉDITOS EQUIVALENTES (DIPLOMADOS)"                                
//		"55";"ENSEÑANZAS UNIVERSITARIAS DE SEGUNDO CICLO Y EQUIVALENTES (LICENCIADOS)"
//		"59";"ENSEÑANZAS UNIVERSITARIAS DE GRADO"
//		"60";"ENSEÑANZAS UNIVERSITARIAS DE MÁSTER"
//		"61";"DOCTORADO UNIVERSITARIO"
//		"80";"SIN ESTUDIOS"
//		datos.setNIVELFORMATIVO(contrataParams.getEducationalLevel()!=null?contrataParams.getEducationalLevel().getValue():null);
		datos.setNIVELFORMATIVO("11");
//		Indicador de discapacidad.  
//		Obligatorio con "S" para contratos de minusválidos.  
//		Obligatorio con "C" para contratos de minusválidos en centros especiales de empleo. 
//		Obligatorio con "E", "F" o "G" para contratos de minusválidos de enclaves laborales. 
//		Sus posibles valores se encuentran codificados en la tabla TEJINDIS.txt de la Ayuda XML 
		if(params.isDisabilityData()){
			datos.setINDDISCAPACIDAD(params.getDisabilityCode()!=null?params.getDisabilityCode().getValue():null);
		}
//		datos.setCODIGOOCUPACION(completeLength(contrataParams.getCno().getCode(), 8, ZERO_VALUE, true));
		String cno = getContractDataMap(params.getContract()).get(ContextVariable.CNO.getName());
		if(StringUtils.isEmpty(cno)){
			AonUtil.addErrorMessage("El trabajador no tiene definido el código de ocupacion (CNO).");
		} else {
			datos.setCODIGOOCUPACION(completeLength(cno, 8, ZERO_VALUE, true));
		}
		if(params.isOfferData()){
			datos.setIDOFERTA(completeLength(params.getOffer(), 17, ZERO_VALUE, false));
		}
//		Código del programa de empleo.  
//		Obligatorio para los contratos de inserción (códigos 403 y 503).  
//		Sus posibles valores se encuentran codificados en la tabla TETPGMEM.txt de la Ayuda XML 
		datos.setCODIGOPROGRAMAEMPLEO(params.getEmploymentProgram()!=null?params.getEmploymentProgram().getValue():null);
		datos.setNACIONALIDADCT(completeLength(params.getContract().getWorkPlace().getAddress().getRegistry().getNationality().getIsoNum(),3,ZERO_VALUE,false));
//		falta por implementar: tabla con los municipios
		datos.setMUNICIPIOCT(completeLength(params.getContract().getWorkPlace().getAddress().getGeozone().getCode(),5,ZERO_VALUE,false));
		if(params.isOlderThan52Data()){
			datos.setOTRASLEGISLACIONES(params.getOtherLaws()!=null?params.getOtherLaws().getValue():null);
		}
//		contrato temporal para personas con discapacidad bonificado 
//		Obligatorio (S/N) para los códigos 430 y 530 salvo que sean minusválidos en Centros Especiales de Empleo (IND_DISCAPACIDAD=C) . 
//		Indica si el contrato temporal para personas con discapacidad es bonificado o no. 
//		datos.setTEMPORALMINUSVBONIFICADO("");
//		contrato de formación bonificado
//		Obligatorio (S/N) para los códigos 421 y 450 con modalidad 421 iniciados entre el 18/06/2010 y el 31/12/2011. 
//		Indica si el contrato de formación es bonificado o no. 
//		datos.setFORMACIONBONIFICADO("");
		if(params.isCanpaignData()){
			datos.setDATOSCAMPAÑAS(params.getFullCampaign());
		}
//		Indicador de empresa. 
//		Obligatorio (S/N) para los códigos 401, 501, 450 con modalidad 401 y 
//		550 con modalidad 501 iniciados a partir de 19/09/2010 cuando no cumpla con la duración válida 
//		y tampoco esté acogido a convenio colectivo que justifique esta duración. 
//		Indica si el contrato se realiza (S) por la Administración Pública , Organismo Público vinculado o Universidad , 
//		o no es una empresa de estos tipos (N). 
//		datos.setINDEMPRESAAAPPUNIVERSIDAD("");
		return datos;
	}
	/**
	 * <xsd:complexType name="DATOS_MEDIDASFOMENTOTYPE">
		<xsd:annotation>
			<xsd:documentation xml:lang="es">Datos de medidas de fomento de la contratación indefinida.</xsd:documentation>
		</xsd:annotation>
		<xsd:sequence>
			<xsd:element name="IND_COSTE_DESPIDO">
				<xsd:annotation>
					<xsd:documentation xml:lang="es">Indicador de acogida a la ley de fomento de la contratación indefinida.  
					Refleja si el contrato se acoge a la ley de fomento(1) o no se acoge(2).</xsd:documentation>
				</xsd:annotation>
				<xsd:simpleType>
					<xsd:restriction base="xsd:string">
						<xsd:pattern value="[12]"/>
					</xsd:restriction>
				</xsd:simpleType>
			</xsd:element>
			<xsd:element name="CODIGO_COLECTIVO_DESPIDO" minOccurs="0">
				<xsd:annotation>
					<xsd:documentation xml:lang="es">Código del colectivo de fomento de la contratación indefinida. 
					Sólo se rellena en el caso de que el IND_COSTE_DESPIDO sea 1 y NO sea un contrato de 
					Centros Especiales de Empleo (IND_DISCAPACIDAD=C) ó un contrato de Minusválidos (130, 230 y 330) . 
					Sus posibles valores se encuentran codificados en la tabla TEOCOLDE.txt 
					de la Ayuda XML - Ultima versión - Tablas de códigos.  </xsd:documentation>
				</xsd:annotation>
				<xsd:simpleType>
					<xsd:restriction base="xsd:string">
						<xsd:pattern value="\d{2}"/>
					</xsd:restriction>
				</xsd:simpleType>
			</xsd:element>
		</xsd:sequence>
	</xsd:complexType>
	 * 
	 * @param contrataParams
	 * @return
	 */
	private DATOSMEDIDASFOMENTOTYPE createDatosMedidasFomento(ContrataParams contrataParams) {
		// TODO 
		DATOSMEDIDASFOMENTOTYPE datos = contratoFactory.createDATOSMEDIDASFOMENTOTYPE();
		datos.setINDCOSTEDESPIDO(contrataParams.isPermanentContractDevelopment()?"1":"2");
		if(contrataParams.isPermanentContractDevelopment()){
			datos.setCODIGOCOLECTIVODESPIDO(contrataParams.getDismissalCollective()!=null?contrataParams.getDismissalCollective().getValue():null);
		} else {
			datos.setCODIGOCOLECTIVODESPIDO(null);
		}
		return datos;
	}
	/**
	 * <xsd:complexType name="DATOS_ANEXOCONTRATORELEVOTYPE">
		<xsd:annotation>
			<xsd:documentation xml:lang="es">Datos anexos de los contratos de relevo.</xsd:documentation>
		</xsd:annotation>
		<xsd:sequence>
			<xsd:element name="TIPO_TRABAJADOR">
				<xsd:annotation>
					<xsd:documentation xml:lang="es">Tipo de trabajador de relevo. 
					Sus posibles valores se encuentran codificados en la tabla TEYTRELE.txt 
					de la Ayuda XML - Ultima versión - Tablas de códigos.</xsd:documentation>
				</xsd:annotation>
				<xsd:simpleType>
					<xsd:restriction base="xsd:string">
						<xsd:pattern value="\d{1}"/>
					</xsd:restriction>
				</xsd:simpleType>
			</xsd:element>
			<xsd:element name="NOMBRE_APELLIDOS" type="NOMBREAPELLIDOSTYPE">
				<xsd:annotation>
					<xsd:documentation xml:lang="es">Nombre y apellidos del trabajador de relevo.</xsd:documentation>
				</xsd:annotation>
			</xsd:element>
		</xsd:sequence>
	</xsd:complexType>
	 * 
	 * @param contrataParams
	 * @return
	 */
	private DATOSANEXOCONTRATORELEVOTYPE createDatosAnexoContratoRelevo(ContrataParams params) {
		if(params.isReliefData()){
			DATOSANEXOCONTRATORELEVOTYPE datos = contratoFactory.createDATOSANEXOCONTRATORELEVOTYPE();
			datos.setTIPOTRABAJADOR(params.getReliefEmployeeType().getValue());
			datos.setNOMBREAPELLIDOS(createNombreApellidos(params.getReliefPerson()));
			return datos;
		}
		return null;
	}
	/**
	 * <xsd:complexType name="DATOS_ET_CO_TYPE">
		<xsd:annotation>
			<xsd:documentation xml:lang="es">Datos de la escuela taller</xsd:documentation>
		</xsd:annotation>
		<xsd:sequence>
			<xsd:element name="CODIGO_ET_CO_TE">
				<xsd:annotation>
					<xsd:documentation xml:lang="es">Código de escuela taller, casas de oficio y talleres de empleo. 
					Sus posibles valores se encuentran codificados en la tabla TESCETCO.txt 
					de la Ayuda XML - Ultima versión - Tablas de códigos.   
					Para los contratos con códigos 100, 130, 150, 200, 230, 250, 300, 330, 350, 441, 540, 541 y 980 
					los códigos de escuela taller válidos sólo son los acabados en 2 (T02, O02 y E02).</xsd:documentation>
				</xsd:annotation>
				<xsd:simpleType>
					<xsd:restriction base="xsd:string">
						<xsd:length value="3"/>
					</xsd:restriction>
				</xsd:simpleType>
			</xsd:element>
		</xsd:sequence>
	</xsd:complexType>
	 * 
	 * @param contrataParams
	 * @return
	 */
	private DATOSETCOTYPE createDatosEtCote(ContrataParams params) {
		if(params.isSchoolWorkshopData()){
			DATOSETCOTYPE datos = contratoFactory.createDATOSETCOTYPE();
			datos.setCODIGOETCOTE(params.getSchoolWorkshop().getValue());
			return datos;
		}
		return null;
	}
	/**
	 * <xsd:complexType name="DATOS_ETT_TYPE">
		<xsd:annotation>
			<xsd:documentation xml:lang="es">Datos de contratos de ETT.</xsd:documentation>
		</xsd:annotation>
		<xsd:sequence>
			<xsd:element name="CIF_NIF_EMPRESA_USUARIA" type="CIFNIFTYPE" minOccurs="0">
				<xsd:annotation>
					<xsd:documentation xml:lang="es">CIF/ NIF de la empresa usuaria del contrato.  
					Dato obligatorio en el caso de ser un contrato de puesta a disposición, salvo 
					en el caso de que la empresa usuaria sea una empresa extranjera.</xsd:documentation>
				</xsd:annotation>
			</xsd:element>
			<xsd:element name="RAZON_SOCIAL_EMPRESA_USUARIA" minOccurs="0">
				<xsd:annotation>
					<xsd:documentation xml:lang="es">Razón social de la empresa usuaria del contrato. 
					Dato obligatorio en el caso de ser un contrato de puesta a disposición.</xsd:documentation>
				</xsd:annotation>
				<xsd:simpleType>
					<xsd:restriction base="xsd:string">
						<xsd:maxLength value="55"/>
					</xsd:restriction>
				</xsd:simpleType>
			</xsd:element>
			<xsd:element name="IND_CTO_PLANTILLA" minOccurs="0">
				<xsd:annotation>
					<xsd:documentation xml:lang="es">Indicador de contrato de plantilla.   
					Refleja si el contrato es de la plantilla de la propia ETT ("S") y no es un contrato 
					de puesta a disposición (de una empresa usuaria). En este caso, en que el contrato es de la propia plantilla 
					de la ETT no se deberá indicar ningún dato adicional. </xsd:documentation>
				</xsd:annotation>
				<xsd:simpleType>
					<xsd:restriction base="xsd:string">
						<xsd:pattern value="[S\s]"/>
					</xsd:restriction>
				</xsd:simpleType>
			</xsd:element>
			<xsd:element name="IND_EMPRESA_EXTRANJERA" minOccurs="0">
				<xsd:annotation>
					<xsd:documentation xml:lang="es">Indicador de empresa extranjera.   
					Refleja si el contrato es de puesta a disposición y la empresa usuaria tiene su centro de trabajo en el extranjero, 
					en este caso el único dato a aportar será el de la razón social de la empresa usuaria .</xsd:documentation>
				</xsd:annotation>
				<xsd:simpleType>
					<xsd:restriction base="xsd:string">
						<xsd:pattern value="[S\s]"/>
					</xsd:restriction>
				</xsd:simpleType>
			</xsd:element>
		</xsd:sequence>
	</xsd:complexType>
	 * 
	 * @param contrataParams
	 * @return
	 */
	private DATOSETTTYPE createDatosEtt(ContrataParams params) {
		if(params.isEttData()){
			DATOSETTTYPE datos = contratoFactory.createDATOSETTTYPE();
			datos.setCIFNIFEMPRESAUSUARIA(params.getEttCif()!=null?createCifNif(params.getEttCif()):null);
			datos.setRAZONSOCIALEMPRESAUSUARIA(params.getEttName());
			datos.setINDCTOPLANTILLA(params.isEttContractTemplate()?"S":null);
			datos.setINDEMPRESAEXTRANJERA(params.isEttForeignEnterprise()?"S":null);
			return datos;
		}
		return null;
	}

	private CIFNIFTYPE createCifNif(String cif) {
		CIFNIFTYPE datos = contratoFactory.createCIFNIFTYPE();
		datos.setCIFNIF(cif);
		return datos;
	}
	/**
	 * <xsd:complexType name="DATOS_CONTRATO_EXTRANJEROTYPE">
		<xsd:annotation>
			<xsd:documentation xml:lang="es">Datos del contingente de extranjeros no comunitarios (iniciados a partir del 01-01-2008).</xsd:documentation>
		</xsd:annotation>
		<xsd:sequence>
			<xsd:element name="IND_CARACTER_OFERTA">
				<xsd:annotation>
					<xsd:documentation xml:lang="es">Indicador del carácter de la oferta.  
					Refleja si la oferta de empleo tiene un carácter ESTABLE (E) ó por el contrario es de carácter TEMPORAL (T). 
					</xsd:documentation>
				</xsd:annotation>
				<xsd:simpleType>
					<xsd:restriction base="xsd:string">
						<xsd:pattern value="[ET\s]"/>
					</xsd:restriction>
				</xsd:simpleType>
			</xsd:element>
			<xsd:element name="AÑO_CONTINGENTE">
				<xsd:annotation>
					<xsd:documentation xml:lang="es">Año del contingente. 
					Será el mismo o el año siguiente al de la fecha de inicio del contrato.</xsd:documentation>
				</xsd:annotation>
				<xsd:simpleType>
					<xsd:restriction base="xsd:string">
						<xsd:pattern value="\d{4}"/>
					</xsd:restriction>
				</xsd:simpleType>
			</xsd:element>
		</xsd:sequence>
	</xsd:complexType>
	 * 
	 * @return
	 */
	private DATOSCONTRATOEXTRANJEROTYPE createDatosContratoExtranjero(ContrataParams params) {
		if(params.isAnnexData()){
			DATOSCONTRATOEXTRANJEROTYPE datos = contratoFactory.createDATOSCONTRATOEXTRANJEROTYPE();
			datos.setAÑOCONTINGENTE(params.getAnexEmploymentYear());
			datos.setINDCARACTEROFERTA(params.getEmploymentCharacter());
			return datos;
		}
		return null;
	}
	/**
	 * <xsd:complexType name="DATOS_COMUNICA_COPIA_BASICATYPE">
		<xsd:annotation>
			<xsd:documentation xml:lang="es">Datos de la comunicación de la copia básica del contrato.</xsd:documentation>
		</xsd:annotation>
		<xsd:sequence>
			<xsd:element name="TIPO_FIRMA">
				<xsd:annotation>
					<xsd:documentation xml:lang="es">Tipo de firma de la copia básica. 
					Sus posibles valores se encuentran codificados en la tabla TERFIRCB.txt 
					de la Ayuda XML - Ultima versión - Tablas de códigos.</xsd:documentation>
				</xsd:annotation>
				<xsd:simpleType>
					<xsd:restriction base="xsd:string">
						<xsd:pattern value="\d{1}"/>
					</xsd:restriction>
				</xsd:simpleType>
			</xsd:element>
			<xsd:element name="TEXTO_COPIABASICA" minOccurs="0">
				<xsd:annotation>
					<xsd:documentation xml:lang="es">Texto de la copia básica. Claúsulas y/o condiciones 
					que no vienen reflejados en la comunicación del contrato.</xsd:documentation>
				</xsd:annotation>
				<xsd:simpleType>
					<xsd:restriction base="xsd:string">
						<xsd:maxLength value="750"/>
					</xsd:restriction>
				</xsd:simpleType>
			</xsd:element>
			<xsd:element name="DOMIC_CENTRO_TRABAJO" minOccurs="0">
				<xsd:annotation>
					<xsd:documentation xml:lang="es">Domicilio del centro de trabajo.</xsd:documentation>
				</xsd:annotation>
				<xsd:simpleType>
					<xsd:restriction base="xsd:string">
						<xsd:maxLength value="150"/>
					</xsd:restriction>
				</xsd:simpleType>
			</xsd:element>
		</xsd:sequence>
	</xsd:complexType>
	 * 
	 * @param contract
	 * @param contrataParams
	 * @return
	 */
	private DATOSCOMUNICACOPIABASICATYPE createDatosComunicacionCopiaBasica(ContrataParams params) {
		if(params.isEttData()){
			DATOSCOMUNICACOPIABASICATYPE datos = contratoFactory.createDATOSCOMUNICACOPIABASICATYPE();
			datos.setDOMICCENTROTRABAJO(params.getContract().getWorkPlace().getAddress().getFullAddress());
			datos.setTEXTOCOPIABASICA(params.getBasicCopyComments());
			datos.setTIPOFIRMA(params.getBasicCopySignatureType()!=null?params.getBasicCopySignatureType().getValue():null);
			return datos;
		}
		return null;
	}
	/**
	 * 
	 * @param contrataParams
	 * @return
	 */
	private DATOSUSOLIBREEMPRESATYPE createDatosUsoLibreEmpresa(ContrataParams params) {
		if(params.getEnterpriseFreeUse()!=null){
			DATOSUSOLIBREEMPRESATYPE datos = contratoFactory.createDATOSUSOLIBREEMPRESATYPE();
			datos.setUSOLIBREEMPRESA(params.getEnterpriseFreeUse());
			return datos;
		}
		return null;
	}
	/**
	 * <xsd:complexType name="DATOS_CONTRATOTIEMPOPARCIALTYPE">
		<xsd:annotation>
			<xsd:documentation xml:lang="es">Datos de los contratos a tiempo parcial. Distribución de la jornada de trabajo.</xsd:documentation>
		</xsd:annotation>
		<xsd:sequence>
			<xsd:element name="TIPO_JORNADA">
				<xsd:annotation>
					<xsd:documentation xml:lang="es">Período de tiempo. Sus posibles valores se encuentran codificados 
					en la tabla TEQPTIEM.txt de la Ayuda XML - Ultima versión - Tablas de códigos. 
					En el caso de  contratos fijos discontinuos (300, 330 y 350) el tipo de jornada será siempre ANUAL (A). 
					</xsd:documentation>
				</xsd:annotation>
				<xsd:simpleType>
					<xsd:restriction base="xsd:string">
						<xsd:length value="1"/>
					</xsd:restriction>
				</xsd:simpleType>
			</xsd:element>
			<xsd:element name="HORAS_JORNADA" minOccurs="0">
				<xsd:annotation>
					<xsd:documentation xml:lang="es">Número de horas por jornada. Formato: HHHHMM (Horas(4)Minutos(2)). 
					Obligatorias para todos los contratos a tiempo parcial menos para los fijos discontinuos (300, 330 y 350) 
					que podran no llevarlas dependiendo del valor de ACTIVIDAD_SIN_FECHACIERTA.</xsd:documentation>
				</xsd:annotation>
				<xsd:simpleType>
					<xsd:restriction base="xsd:string">
						<xsd:pattern value="\d{6}"/>
					</xsd:restriction>
				</xsd:simpleType>
			</xsd:element>
			<xsd:element name="HORAS_CONVENIO" minOccurs="0">
				<xsd:annotation>
					<xsd:documentation xml:lang="es">Número de horas por convenio. Formato: HHHHMM (Horas(4)Minutos(2)). 
					Sólo vendrá cumplimentado, y de manera opcional, para los contratos de códigos 200, 250, 230, 300, 350, 330.
					</xsd:documentation>
				</xsd:annotation>
				<xsd:simpleType>
					<xsd:restriction base="xsd:string">
						<xsd:pattern value="\d{6}"/>
					</xsd:restriction>
				</xsd:simpleType>
			</xsd:element>
			<xsd:element name="HORAS_FORMACION" minOccurs="0">
				<xsd:annotation>
					<xsd:documentation xml:lang="es">Número de horas de formación. Formato: HHHHMM (Horas(4)Minutos(2)). 
					Obligatorias para los contratos de código 421 (salvo que el elemento INDIC_FORMACION_TEORICA sea "S"). </xsd:documentation>
				</xsd:annotation>
				<xsd:simpleType>
					<xsd:restriction base="xsd:string">
						<xsd:pattern value="\d{6}"/>
					</xsd:restriction>
				</xsd:simpleType>
			</xsd:element>
			<xsd:element name="INDIC_FORMACION_TEORICA" minOccurs="0">
				<xsd:annotation>
					<xsd:documentation xml:lang="es">Indicador de formación teórica recibida. 
					Obligatorio para los contratos de código 421 si no vienen especificadas las horas de formación. 
					Los posibles valores que puede tomar son "S" (SI) ó "N" (NO) para indicar si ya había sido recibida 
					con anterioridad la formación o no. </xsd:documentation>
				</xsd:annotation>
				<xsd:simpleType>
					<xsd:restriction base="xsd:string">
						<xsd:pattern value="[SN\s]"/>
					</xsd:restriction>
				</xsd:simpleType>
			</xsd:element>
			<xsd:element name="COLECTIVO_EDAD" minOccurs="0">
				<xsd:annotation>
					<xsd:documentation xml:lang="es">Colectivo de edad en formación. Obligatorio para los contratos de código 421 
					cuando el trabajador tiene una edad mayor o igual a 21 años  ( para los contratos de código 421 iniciados 
					entre el 18/06/2010 y el 31/12/2011 esta edad pasa a ser de 24 años). Sus posibles valores se encuentran 
					codificados en la tabla THPCOLFO.txt de la Ayuda XML - Ultima versión - Tablas de códigos. </xsd:documentation>
				</xsd:annotation>
				<xsd:simpleType>
					<xsd:restriction base="xsd:string">
						<xsd:pattern value="\d{2}"/>
					</xsd:restriction>
				</xsd:simpleType>
			</xsd:element>
			<xsd:element name="PORCENTAJE_JUBILACION_PARCIAL" minOccurs="0">
				<xsd:annotation>
					<xsd:documentation xml:lang="es">Porcentaje sobre la jornada en contratos de jubilación parcial. 
					Obligatorio para los contratos de código 540. Su valor debe estar comprendido entre el 25% y el 85%. 
					Su formato pasa a ser EEDD (03-2009), siendo las dos primeras posiciones la parte entera del porcentaje 
					y las dos últimas la parte decimal (ej. 25% será enviado como 2500). </xsd:documentation>
				</xsd:annotation>
				<xsd:simpleType>
					<xsd:restriction base="xsd:string">
						<xsd:pattern value="\d{4}"/>
					</xsd:restriction>
				</xsd:simpleType>
			</xsd:element>
			<xsd:element name="ACTIVIDAD_SIN_FECHACIERTA" minOccurs="0">
				<xsd:annotation>
					<xsd:documentation xml:lang="es">Indicador de si el período de actividad no tiene una fecha concreta. 
					Obligatorio para los contratos fijos discontinuos (300,330 y 350) si no vienen especificadas las HORAS_JORNADA. 
					El único valor posible que puede tomar es "S" (SI) para indicar que es una actividad sin fecha conocida. 
					</xsd:documentation>
				</xsd:annotation>
				<xsd:simpleType>
					<xsd:restriction base="xsd:string">
						<xsd:pattern value="[S\s]"/>
					</xsd:restriction>
				</xsd:simpleType>
			</xsd:element>
			<xsd:element name="FIJODISCONTINUO_PERIODICO" minOccurs="0">
				<xsd:annotation>
					<xsd:documentation xml:lang="es">Indicador de trabajo fijo discontinuo o periódico. 
					Obligatorio para los contratos de código 200, 230 ó 250 iniciados a partir del 01/07/2006. 
					Los posibles valores que puede tomar son "S" (SI) ó "N" (NO) para indicar si el contrato indefinido 
					a tiempo parcial corresponde a la realización de trabajos fijos discontinuos o periódicos que se repiten 
					en fechas ciertas dentro del volumen normal de la actividad de la empresa. </xsd:documentation>
				</xsd:annotation>
				<xsd:simpleType>
					<xsd:restriction base="xsd:string">
						<xsd:pattern value="[SN\s]"/>
					</xsd:restriction>
				</xsd:simpleType>
			</xsd:element>
			<xsd:element name="PORC_JORNADA_PACTADA" minOccurs="0">
				<xsd:annotation>
					<xsd:documentation xml:lang="es">Porcentaje de jornada pactada a realizar. 
					Obligatorio para los contratos de código 250 ó 350 iniciados a partir del 08/03/2009 que se acojan 
					a los colectivos de bonificación de beneficiarios de prestaciones (124,125 o 126). Su formato es EEDD, 
					siendo las dos primeras posiciones la parte entera del porcentaje y las dos últimas la parte decimal 
					(ej. 25% será enviado como 2500). </xsd:documentation>
				</xsd:annotation>
				<xsd:simpleType>
					<xsd:restriction base="xsd:string">
						<xsd:pattern value="\d{4}"/>
					</xsd:restriction>
				</xsd:simpleType>
			</xsd:element>
			<xsd:element name="HORAS_ANUALES_TIEMPO_COMPLETO" minOccurs="0">
				<xsd:annotation>
					<xsd:documentation xml:lang="es">Número de horas anuales que tendría la jornada a tiempo completo. 
					Obligatorias para los contratos de código 250 ó 350 iniciados a partir del 08/03/2009 que se acojan 
					a los colectivos de bonificación de beneficiarios de prestaciones (124,125 o 126).  
					Su formato es HHHHMM (Horas(4)Minutos(2)).</xsd:documentation>
				</xsd:annotation>
				<xsd:simpleType>
					<xsd:restriction base="xsd:string">
						<xsd:pattern value="\d{6}"/>
					</xsd:restriction>
				</xsd:simpleType>
			</xsd:element>
		</xsd:sequence>
	</xsd:complexType>
	 * 
	 * @param contrataParams
	 * @return
	 */
	private DATOSCONTRATOTIEMPOPARCIALTYPE createDatosContratoTiempoParcial(ContrataParams params) {
		// TODO
		DATOSCONTRATOTIEMPOPARCIALTYPE datos = contratoFactory.createDATOSCONTRATOTIEMPOPARCIALTYPE();
		datos.setACTIVIDADSINFECHACIERTA(params.getActividadsinfechacierta());
		datos.setCOLECTIVOEDAD(params.getColectivoedad());
		datos.setFIJODISCONTINUOPERIODICO(params.getFijodiscontinuoperiodico()?"S":"N");
		datos.setHORASANUALESTIEMPOCOMPLETO(params.getHorasanualestiempocompleto());
		datos.setHORASCONVENIO(params.getDuracionconvenio().isEmpty()?null:completeLength(params.getDuracionconvenio(), 6, "0", false));
		datos.setHORASFORMACION(params.getDuracionformacion().isEmpty()?null:completeLength(params.getDuracionformacion(), 6, "0", false));
		datos.setHORASJORNADA(params.getDuracionjornada().isEmpty()?null:completeLength(params.getDuracionjornada(), 6, "0", false));
		datos.setINDICFORMACIONTEORICA(params.getIndicformacionteorica());
		datos.setPORCENTAJEJUBILACIONPARCIAL(params.getPorcentajejubilacionparcial());
		datos.setPORCJORNADAPACTADA(params.getPorcjornadapactada());
		datos.setTIPOJORNADA(params.getTipojornada().getValue());
		return datos;
	}
	/**
	 * <xsd:complexType name="DATOS_REDUCCION_RDL_1_2011TYPE">
		<xsd:annotation>
			<xsd:documentation xml:lang="es">Datos de reducción de cuotas por contratación a tiempo parcial (RDL 1/2011). Opcional para los contratos a tiempo parcial (salvo los de interinidad, jubilación parcial, relevo e inserción con los que no es compatible) iniciados a partir del 13/02/2011.  Tampoco es compatible con contratos de extranjeros en origen , ni con los de centros especiales de empleo.</xsd:documentation>
		</xsd:annotation>
		<xsd:sequence>
			<xsd:element name="CODIGO_COLECTIVO_REDUCCION">
				<xsd:annotation>
					<xsd:documentation xml:lang="es">
					Colectivo de reducción. 
					Sus posibles valores se encuentran codificados en la tabla TQOCOLRE.txt de la Ayuda XML - Ultima versión - Tablas de códigos.
					</xsd:documentation>
				</xsd:annotation>
				<xsd:simpleType>
					<xsd:restriction base="xsd:string">
						<xsd:pattern value="\d{2}"/>
					</xsd:restriction>
				</xsd:simpleType>
			</xsd:element>
			<xsd:element name="PORCENTAJE_REDUCCION">
				<xsd:annotation>
					<xsd:documentation xml:lang="es">
					Porcentaje de reducción de la cuota. 
					Solo admite 2 valores : 75% para empresas con más de 250 trabajadores y 100% para empresas con menos de 250 trabajadores. 
					</xsd:documentation>
				</xsd:annotation>
				<xsd:simpleType>
					<xsd:restriction base="xsd:string">
						<xsd:minLength value="2"/>
						<xsd:maxLength value="3"/>
						<xsd:pattern value="([0-9])+"/>
					</xsd:restriction>
				</xsd:simpleType>
			</xsd:element>
			<xsd:element name="PORCENTAJE_JORNADA_REDUCCION">
				<xsd:annotation>
					<xsd:documentation xml:lang="es">
					Porcentaje de jornada del contrato con reducción de cuotas. 
					Su valor debe estar comprendido entre el 50% y el 75% ambos inclusive. Su formato es EEDD, 
					siendo las dos primeras posiciones la parte entera del porcentaje y las dos últimas la parte decimal 
					(ej. 50% será enviado como 5000). 
					</xsd:documentation>
				</xsd:annotation>
				<xsd:simpleType>
					<xsd:restriction base="xsd:string">
						<xsd:pattern value="\d{4}"/>
					</xsd:restriction>
				</xsd:simpleType>
			</xsd:element>
		</xsd:sequence>
	</xsd:complexType>
	 * 
	 * @return
	 */
	private DATOSREDUCCIONRDL12011TYPE createDatosReduccionRdl2011(ContrataParams params) {
		// TODO
		DATOSREDUCCIONRDL12011TYPE datos = contratoFactory.createDATOSREDUCCIONRDL12011TYPE();
//		datos.setCODIGOCOLECTIVOREDUCCION(params.getCODIGOCOLECTIVOREDUCCION());
//		datos.setPORCENTAJEREDUCCION(params.getPORCENTAJEREDUCCION());
//		datos.setPORCENTAJEJORNADAREDUCCION(params.getPORCENTAJEJORNADAREDUCCION());
//		return datos;
		return null;
	}
	/**
	 * <xsd:complexType name="DATOS_BONIFICACIONTYPE">
		<xsd:annotation>
			<xsd:documentation xml:lang="es">Datos de bonificación en la cotización a la Seguridad Social.</xsd:documentation>
		</xsd:annotation>
		<xsd:sequence>
			<xsd:element name="CODIGO_COLECTIVO_BONIF" minOccurs="0">
				<xsd:annotation>
					<xsd:documentation xml:lang="es">
					Código del colectivo de bonificación. 
					No es necesario especificar su valor en contratos de Centros Especiales de Empleo (IND_DISCAPACIDAD=C).  
					Sus posibles valores se encuentran codificados en la tabla TELCOLBO.txt de la Ayuda XML - Ultima versión - Tablas de códigos.
					</xsd:documentation>
				</xsd:annotation>
				<xsd:simpleType>
					<xsd:restriction base="xsd:string">
						<xsd:minLength value="2"/>
						<xsd:maxLength value="3"/>
						<xsd:pattern value="([0-9])+"/>
					</xsd:restriction>
				</xsd:simpleType>
			</xsd:element>
			<xsd:element name="INDIC_EMPLEAD_AUTONOMO" minOccurs="0">
				<xsd:annotation>
					<xsd:documentation xml:lang="es">
					Indicador de empleador autónomo.   
					Obligatorio para contratos de código 150, 250 y 350 iniciados antes del 01/07/2006.  
					Refleja si el empleador que contrata es autónomo(1) o no lo es(2).
					</xsd:documentation>
				</xsd:annotation>
				<xsd:simpleType>
					<xsd:restriction base="xsd:string">
						<xsd:pattern value="[12]"/>
					</xsd:restriction>
				</xsd:simpleType>
			</xsd:element>
		</xsd:sequence>
	</xsd:complexType>
	 * 
	 * @param contrataParams
	 * @return
	 */
	private DATOSBONIFICACIONTYPE createDatosBonificacion(ContrataParams contrataParams) {
		// TODO
		DATOSBONIFICACIONTYPE datos = new DATOSBONIFICACIONTYPE();
		datos.setCODIGOCOLECTIVOBONIF("00");
		datos.setINDICEMPLEADAUTONOMO("1");
		return datos;
	}
	/**
	 * <xsd:complexType name="DATOS_EMPRESA_INSERCIONTYPE">
		<xsd:annotation>
			<xsd:documentation xml:lang="es">Datos de contratos de empresas de inserción. </xsd:documentation>
		</xsd:annotation>
		<xsd:sequence>
			<xsd:element name="IND_EMPRESA_INSERCION">
				<xsd:annotation>
					<xsd:documentation xml:lang="es">
					Indicador del tipo de empresa.  
					Refleja si la empresa que contrata es de inserción (S) ó no (N) 
					( dato obligatorio para contratos de código 452 , 552 y para los de exclusion social iniciados a partir del 13-01-2008). 
					</xsd:documentation>
				</xsd:annotation>
				<xsd:simpleType>
					<xsd:restriction base="xsd:string">
						<xsd:pattern value="[SN\s]"/>
					</xsd:restriction>
				</xsd:simpleType>
			</xsd:element>
			<xsd:element name="IND_DURAC_INFERIOR" minOccurs="0">
				<xsd:annotation>
					<xsd:documentation xml:lang="es">
					Indicador de duración inferior. 
					Obligatorio para contratos de códigos 452 y 552 cuando su duración está entre 6 y 12 meses.  
					Refleja si la duración inferior del contrato está aconsejada (S) ó no (N) por los Servicios Sociales Públicos 
					para el seguimiento del proceso de inserción. 
					</xsd:documentation>
				</xsd:annotation>
				<xsd:simpleType>
					<xsd:restriction base="xsd:string">
						<xsd:pattern value="[SN\s]"/>
					</xsd:restriction>
				</xsd:simpleType>
			</xsd:element>
		</xsd:sequence>
	</xsd:complexType>
	 * 
	 * @return
	 */
	private DATOSEMPRESAINSERCIONTYPE createDatosEmpresaInsercion() {
		// TODO
		DATOSEMPRESAINSERCIONTYPE datos = new DATOSEMPRESAINSERCIONTYPE();
		datos.setINDEMPRESAINSERCION("N");
		datos.setINDDURACINFERIOR(null);
		return datos;
	}
	/**
	 * <xsd:complexType name="DATOS_COPIABASICATYPE">
		<xsd:annotation>
			<xsd:documentation xml:lang="es">Datos de evaluación para la obligatoriedad de la copia básica</xsd:documentation>
		</xsd:annotation>
		<xsd:sequence>
			<xsd:element name="IND_CONTRATO_ALTA_DIRECCION" minOccurs="0">
				<xsd:annotation>
					<xsd:documentation xml:lang="es">
						Indicador de contrato de alta dirección.   
						Obligatorio para contratos de código 990 cuando el contrato SI es escrito.   
						Refleja si el contrato es de alta dirección ("S") o no lo es ("N") para evaluar la obligatoriedad 
						de la copia básica, si es de alta dirección no es obligatoria, si no es de alta dirección sí es obligatoria.
					</xsd:documentation>
				</xsd:annotation>
				<xsd:simpleType>
					<xsd:restriction base="xsd:string">
						<xsd:pattern value="[SN\s]"/>
					</xsd:restriction>
				</xsd:simpleType>
			</xsd:element>
			<xsd:element name="IND_CONTRATO_ESCRITO">
				<xsd:annotation>
					<xsd:documentation xml:lang="es">Indicador de contrato escrito.   
					Obligatorio para contratos de código 990 y 402 cuando su duración es menor ó igual a 28 días.  
					Refleja si el contrato es escrito ("S") o no lo es ("N") para evaluar la obligatoriedad de la copia básica, 
					si no es escrito no es obligatoria, si es escrito sí es obligatoria para los 402 (para los 990 dependerá 
					del elemento  IND_CONTRATO_ALTA_DIRECCION).
				</xsd:documentation>
				</xsd:annotation>
				<xsd:simpleType>
					<xsd:restriction base="xsd:string">
						<xsd:pattern value="[SN\s]"/>
					</xsd:restriction>
				</xsd:simpleType>
			</xsd:element>
		</xsd:sequence>
	</xsd:complexType>
	 * 
	 * @param contract
	 * @return
	 */
	private DATOSCOPIABASICATYPE createDatosCopiaBasica(ContrataParams params) {
		// TODO
		DATOSCOPIABASICATYPE datos = new DATOSCOPIABASICATYPE();
		datos.setINDCONTRATOALTADIRECCION("");
		datos.setINDCONTRATOESCRITO("");
		return datos;
	}
	/**
	 * <xsd:complexType name="DATOS_PROGEMPLEOPUBLICOTYPE">
		<xsd:annotation>
			<xsd:documentation xml:lang="es">Datos de la obra o servicio en Corporaciones Locales.</xsd:documentation>
		</xsd:annotation>
		<xsd:sequence>
			<xsd:element name="CORPORACION_LOCAL" minOccurs="0">
				<xsd:annotation>
					<xsd:documentation xml:lang="es">
						Entidad colaboradora, código de corporación local.  
						Obligatorio para los contratos que en el elemento CODIGOPROGRAMAEMPLEO hayan especificado 
						el valor 2 (inserción en corporación local) sea cual sea su fecha de inicio 
						ó los valores 1(fomento de empleo agrario) y 12 (interes social en corporacion local) 
						si los contratos se inician con anterioridad al año 2005. 
						No debe especificarse valor alguno para este elemento en ningún otro caso. 
						Sus posibles valores se encuentran codificados en la tabla TEUECCLL.txt d
						e la Ayuda XML - Ultima versión - Tablas de códigos.
					</xsd:documentation>
				</xsd:annotation>
				<xsd:simpleType>
					<xsd:restriction base="xsd:string">
						<xsd:pattern value="\d{1}"/>
					</xsd:restriction>
				</xsd:simpleType>
			</xsd:element>
			<xsd:element name="ACTUACION" minOccurs="0">
				<xsd:annotation>
					<xsd:documentation xml:lang="es">
						Código de actuación.  
						Obligatorio para los contratos que en el elemento CODIGOPROGRAMAEMPLEO hayan especificado 
						el valor 2 (inserción en corporación local) sea cual sea su fecha de inicio 
						ó los valores 1(fomento de empleo agrario) y 12 (interes social en corporacion local) 
						si los contratos se inician con anterioridad al año 2005.  No debe especificarse valor alguno 
						para este elemento en ningún otro caso. La 1ª posición de este código de 3 posiciones,  
						podrá tener un valor que se encuentra codificado en la tabla TEVACTCL.txt 
						de la Ayuda XML - Ultima versión - Tablas de códigos.  
						Las 2 posiciones restantes corresponderán a un número secuencial de la actuación expresada 
						en la posición anterior.
					</xsd:documentation>
				</xsd:annotation>
				<xsd:simpleType>
					<xsd:restriction base="xsd:string">
						<xsd:length value="3"/>
					</xsd:restriction>
				</xsd:simpleType>
			</xsd:element>
			<xsd:element name="EJERCICIO_PRESUPUESTARIO" minOccurs="0">
				<xsd:annotation>
					<xsd:documentation xml:lang="es">
						Año del ejercicio presupuestario al que es imputado.  
						Obligatorio para los contratos que en el elemento CODIGOPROGRAMAEMPLEO hayan especificado 
						el valor 2 (inserción en corporación local) sea cual sea su fecha de inicio 
						ó los valores 1(fomento de empleo agrario) y 12 (interes social en corporacion local) 
						si los contratos se inician con anterioridad al año 2005. No debe especificarse valor alguno 
						para este elemento en ningún otro caso.  Este elemento deberá contener un año que coincidirá 
						con el año de mecanización del contrato ó con el año inmediatamente anterior.
					</xsd:documentation>
				</xsd:annotation>
				<xsd:simpleType>
					<xsd:restriction base="xsd:string">
						<xsd:pattern value="\d{4}"/>
					</xsd:restriction>
				</xsd:simpleType>
			</xsd:element>
			<xsd:element name="GRUPOCOTIZACION_CORPORACIONLOCAL" minOccurs="0">
				<xsd:annotation>
					<xsd:documentation xml:lang="es">
						Grupo de cotización a la Seguridad Social en Corporaciones Locales.  
						Obligatorio para los contratos que en el elemento CODIGOPROGRAMAEMPLEO hayan especificado 
						el valor 13, 14, 15 ó 16.  Sus posibles valores se encuentran codificados en la tabla TFGGRCOT.txt 
						de la Ayuda XML - Ultima versión - Tablas de códigos.
					</xsd:documentation>
				</xsd:annotation>
				<xsd:simpleType>
					<xsd:restriction base="xsd:string">
						<xsd:pattern value="\d{2}"/>
					</xsd:restriction>
				</xsd:simpleType>
			</xsd:element>
		</xsd:sequence>
	</xsd:complexType>
	 * 
	 * @param contract
	 * @return
	 */
	private DATOSPROGEMPLEOPUBLICOTYPE createDatosProgramaEmpleoPublico(ContrataParams params) {
		// TODO
		if(params.isEmploymentProgramData()){
			DATOSPROGEMPLEOPUBLICOTYPE datos = new DATOSPROGEMPLEOPUBLICOTYPE();
			datos.setACTUACION("");
			datos.setCORPORACIONLOCAL("");
			datos.setEJERCICIOPRESUPUESTARIO("");
			datos.setGRUPOCOTIZACIONCORPORACIONLOCAL("");
			return datos;
		}
		return null;
	}
	/**
	 * <xsd:complexType name="DATOS_CONTRATOINVESTIGACIONTYPE">
		<xsd:annotation>
			<xsd:documentation xml:lang="es">Datos de los contratos de investigación.</xsd:documentation>
		</xsd:annotation>
		<xsd:sequence>
			<xsd:element name="IND_EMPLEADOR" minOccurs="0">
				<xsd:annotation>
					<xsd:documentation xml:lang="es">
						Tipo de empleador que contrata para los acogidos a la Ley 12/2001. 
						Sus posibles valores se encuentran codificados en la tabla TEWEINVE.txt 
						de la Ayuda XML - Ultima versión - Tablas de códigos.
					</xsd:documentation>
				</xsd:annotation>
				<xsd:simpleType>
					<xsd:restriction base="xsd:string">
						<xsd:pattern value="\d{1}"/>
					</xsd:restriction>
				</xsd:simpleType>
			</xsd:element>
			<xsd:element name="IND_TRABAJADOR" minOccurs="0">
				<xsd:annotation>
					<xsd:documentation xml:lang="es">
						Tipo de trabajador contratado. 
						Sus posibles valores se encuentran codificados en la tabla TEXTINVE.txt 
						de la Ayuda XML - Ultima versión - Tablas de códigos.
					</xsd:documentation>
				</xsd:annotation>
				<xsd:simpleType>
					<xsd:restriction base="xsd:string">
						<xsd:pattern value="\d{1}"/>
					</xsd:restriction>
				</xsd:simpleType>
			</xsd:element>
			<xsd:element name="IND_RD63_2006" fixed="S" minOccurs="0">
				<xsd:annotation>
					<xsd:documentation>
						Indicador contrato de investigador formación para los acogidos al Real Decreto 63/2006 . 
						No se rellena junto a los otros valores y solo admite el valor 'S'.
					</xsd:documentation>
				</xsd:annotation>
				<xsd:simpleType>
					<xsd:restriction base="xsd:string"/>
				</xsd:simpleType>
			</xsd:element>
		</xsd:sequence>
	</xsd:complexType>
	 * 
	 * @return
	 */
	private DATOSCONTRATOINVESTIGACIONTYPE createDatosContratoInvestigacion(ContrataParams params) {
		DATOSCONTRATOINVESTIGACIONTYPE datos = new  DATOSCONTRATOINVESTIGACIONTYPE();
		datos.setINDEMPLEADOR(params.getIndempleador().getValue());
		datos.setINDTRABAJADOR(params.getIndtrabajador().getValue());
		datos.setINDRD632006(params.getIndrd632006()?"S":null);
		return datos;
	}
	/**
	 * <xsd:complexType name="DATOS_CONTRATOINSERCIONTYPE">
		<xsd:annotation>
			<xsd:documentation xml:lang="es">Datos de los contratos de inserción.</xsd:documentation>
		</xsd:annotation>
		<xsd:sequence>
			<xsd:element name="GRUPOCOTIZACIONSEGSOCIAL">
				<xsd:annotation>
					<xsd:documentation xml:lang="es">
						Grupo de cotización a la Seguridad Social.  
						Sus posibles valores se encuentran codificados en la tabla TFGGRCOT.txt 
						de la Ayuda XML - Ultima versión - Tablas de códigos.
					</xsd:documentation>
				</xsd:annotation>
				<xsd:simpleType>
					<xsd:restriction base="xsd:string">
						<xsd:pattern value="\d{2}"/>
					</xsd:restriction>
				</xsd:simpleType>
			</xsd:element>
		</xsd:sequence>
	</xsd:complexType>
	 * 
	 * @param contract
	 * @return
	 */
	private DATOSCONTRATOINSERCIONTYPE createDatosContratoInsercion(ContrataParams contrataParams) {
		// TODO
		DATOSCONTRATOINSERCIONTYPE datos = new DATOSCONTRATOINSERCIONTYPE();
		datos.setGRUPOCOTIZACIONSEGSOCIAL("");
		return datos;
	}
	/**
	 * <xsd:complexType name="DATOS_CONTRATOINTERINIDADTYPE">
		<xsd:annotation>
			<xsd:documentation xml:lang="es">Datos de los contratos de interinidad.</xsd:documentation>
		</xsd:annotation>
		<xsd:sequence>
			<xsd:element name="CAUSA_INTERINIDAD">
				<xsd:annotation>
					<xsd:documentation xml:lang="es">
						Código de la causa objeto de la interinidad. Sus posibles valores se encuentran codificados 
						en la tabla TEIINTER.txt de la Ayuda XML - Ultima versión - Tablas de códigos.
					</xsd:documentation>
				</xsd:annotation>
				<xsd:simpleType>
					<xsd:restriction base="xsd:string">
						<xsd:length value="1"/>
					</xsd:restriction>
				</xsd:simpleType>
			</xsd:element>
		</xsd:sequence>
	</xsd:complexType>
	 * 
	 * @return
	 */
	private DATOSCONTRATOINTERINIDADTYPE createDatosContratoInterinidad(ContrataParams params) {
		if(params.isInterimData()){
			DATOSCONTRATOINTERINIDADTYPE datos = new DATOSCONTRATOINTERINIDADTYPE();
			datos.setCAUSAINTERINIDAD(params.getCausaInterinidad().getValue());
			return datos;
		}
		return null;
	}
	/**
	 * 
	 <xsd:complexType name="DATOSCONTRATOPRACTICASTYPE">
		<xsd:annotation>
			<xsd:documentation xml:lang="es">Datos de los contratos de prácticas.</xsd:documentation>
		</xsd:annotation>
		<xsd:sequence>
			<xsd:element name="TITULACION_ACADEMICA">
				<xsd:annotation>
					<xsd:documentation xml:lang="es">
						Código de titulación académica. No es necesario especificar su valor en los contratos de prácticas, 
						cuando el Nivel Formativo del trabajador sea 60. Sus posibles valores se encuentran codificados 
						en la tabla THITIACA.txt de la Ayuda XML - Ultima versión - Tablas de códigos.  
						La codificación tabulada de este elemento corresponde a códigos de 12 posiciones, 
						hasta ahora se debían enviar códigos de 4 posiciones con 8 ceros por la izquierda hasta completar las 12, 
						a partir de ahora los códigos ya son de 12 posiciones por lo que no habrá que rellenar con ceros.
					</xsd:documentation>
				</xsd:annotation>
				<xsd:simpleType>
					<xsd:restriction base="xsd:string">
						<xsd:pattern value="\d{12}"/>
					</xsd:restriction>
				</xsd:simpleType>
			</xsd:element>
			<xsd:element name="IND_CERTIF_PROFESIONALIDAD" minOccurs="0">
				<xsd:annotation>
					<xsd:documentation xml:lang="es">
						Obligatorio (S/N) para los códigos 420, 520, 450 con modalidad 420 
						y 550 con modalidad 520 iniciados a partir del 18/06/2010 cuando el Nivel Formativo del trabajador 
						no sea uno de los siguientes códigos: 33, 51, 54, 55, 59 o 60. 
						Indica si el trabajador tiene o no un certificado de profesionalidad. 
					</xsd:documentation>
				</xsd:annotation>
				<xsd:simpleType>
					<xsd:restriction base="xsd:string">
						<xsd:pattern value="[SN\s]"/>
					</xsd:restriction>
				</xsd:simpleType>
			</xsd:element>
		</xsd:sequence>
	</xsd:complexType>
	 * 
	 * @return
	 */
	private DATOSCONTRATOPRACTICASTYPE createDatosContratoPracticas(ContrataParams params) {
		// TODO
		DATOSCONTRATOPRACTICASTYPE datos = new DATOSCONTRATOPRACTICASTYPE();
		datos.setTITULACIONACADEMICA("");
		datos.setINDCERTIFPROFESIONALIDAD("");
		return datos;
	}
	/**
	 * Modalidad del contrato de exclusión social. 
	 * Las modalidades de contratación admitidas serán los códigos reseñados a continuación, 
	 * dependiendo de que el contrato de exclusión sea a tiempo parcial ó a tiempo completo, 
	 * así para los contratos de exclusión a tiempo completo (450) la modalidad podrá ser : 401, 402, 410, 420, 421 y 990;  
	 * y para los contratos de exclusión a tiempo parcial (550) la modalidad podrá ser : 501, 502, 510, 520 y 990.
	 * 
	 * @param contrataParams
	 * @return
	 */
	private DATOSEXCLUSIONSOCIALTYPE createDatosExclusionSocial() {
		// TODO revisar la forma de escoger el codigo TC2 a la hora de crear un unevo contrato
		DATOSEXCLUSIONSOCIALTYPE datos = new DATOSEXCLUSIONSOCIALTYPE();
		datos.setMODALIDADEXCLUSION("000");
		return datos;
	}

	
	/* ***************************************
	 * ***************************************
	 * AUXILIARES
	 * ***************************************
	 * ***************************************
	 */
	private Map<String, String> contractDataMap;
	
	protected Map<String, String> getContractDataMap(Contract contract) {
		if(contractDataMap==null){
			PayrollUtils utils = new PayrollUtils();
			contractDataMap = utils.getContractDataMap(contract);
		}
		return contractDataMap;
	}
	protected Map<String, String> getContractDataMap() {
		return contractDataMap;
	}
	
	private String getEnterpriseCCC(Enterprise enterprise) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(EnterpriseCCC.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.ENTERPRISE_CCC_ACTIVITY_ENTERPRISE_ID), enterprise.getId());
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.ENTERPRISE_CCC_TYPE), CCCType.PRINCIPAL);
		List<ITransferObject> list = bean.getList(criteria);
		if(!list.isEmpty()){
			return ((EnterpriseCCC)list.get(0)).getCcc();
		}
		return null;
	}
	
	private String getFormatedDate(Date date){
		String pattern = "yyyyMMdd";
		if(date!=null){
			return DateFormatUtils.format(date, pattern);
		}
		return null;
	}
	
	private String completeLength(Integer value, Integer length, String appendValue, boolean rightAppend) {
		return completeLength(value.toString(), length, appendValue, rightAppend);
	}
	
	private String completeLength(String value, Integer length, String appendValue, boolean rightAppend) {
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
	

		
}

