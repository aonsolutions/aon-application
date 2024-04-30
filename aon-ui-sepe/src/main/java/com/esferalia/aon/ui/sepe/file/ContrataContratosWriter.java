package com.esferalia.aon.ui.sepe.file;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.Country;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.person.Person;
import com.code.aon.person.enumeration.Gender;
import com.code.aon.registry.enumeration.DocumentType;
import com.esferalia.aon.file.payroll.contrata.ContrataContratoParams;
import com.esferalia.aon.file.payroll.contrata.IContrataParams;
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.payroll.ContractData;
import com.esferalia.aon.payroll.EnterpriseCCC;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.payroll.enumeration.ContractCode;
import com.esferalia.aon.payroll.enumeration.SSRegimeType;
import com.esferalia.aon.payroll.enumeration.contrata.TCHRGCOT;
import com.esferalia.aon.payroll.enumeration.contrata.TEJINDIS;
import com.esferalia.aon.payroll.enumeration.contrata.TEQPTIEM;
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
import com.esferalia.aon.sepe.api.contrata.contratos.DATOSCONTRATOSUSTITUCIONTYPE;
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
import com.esferalia.aon.sepe.api.contrata.contratos.ObjectFactory;
import com.esferalia.aon.ui.sepe.utils.SEPEUtils;

public class ContrataContratosWriter implements IContrataWriter{
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ContrataContratosWriter.class.getName());
	
	final String CONTRATA_CONTRATOS_MODEL_PATH = "com.esferalia.aon.sepe.api.contrata.contratos";
	
	private final String ZERO_VALUE = "0";
	private final String BLANK_1 = " ";
	
	private Contract contract;

	private ObjectFactory factory = new ObjectFactory();
	
	public ContrataContratosWriter(Contract contract) {
		this.contract = contract;
	}

	public ObjectFactory getFactory(){
		return factory;
	}
	
	public Contract getContract(){
		return contract;
	}
	
	@Override
	public File createFile(IContrataParams params) throws ManagerBeanException, IOException {
		// TODO
		return null;
	}
	
	public IContratoType createFile(IContratoType contratoType, ContrataContratoParams params) throws ManagerBeanException{
		SEPEUtils utils = SEPEUtils.getInstance();
		String code = "";
		try {
			List<ITransferObject> list = utils.getContractData(contract, null, null, ContextVariable.TC2.getName(), false);
			list = list.stream().map(to -> ((ContractData)to))
					.filter(cd -> cd.getExpression().matches(".\\d\\d[^9]."))
					.sorted((cd1, cd2) -> cd1.getStartDate().compareTo(cd2.getStartDate()))
					.collect(Collectors.toList());
			if(list!=null && list.size()>0){
				code = ((ContractData)list.get(list.size()-1)).getExpression();
				code = code.replaceAll("\"", "");
			}
		} catch (ManagerBeanException e) {
			// do nothing ...
		}
		
		if(contratoType!=null){
			writeContratosMainData(contratoType, params);
		}
		
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
	
	private void writeContratosMainData(IContratoType contract, ContrataContratoParams params) throws ManagerBeanException{
		contract.setDATOSEMPRESA(createDatosEmpresa(params));
		contract.setDATOSTRABAJADOR(createDatosTrabajador(params));
		contract.setDATOSGENERALESCONTRATO(createDatosGeneralesContrato(params)); 
		if(params.isEttData()){
			contract.setDATOSETT(createDatosEtt(params));
		}
		contract.setDATOSCOMUNICACOPIABASICA(createDatosComunicacionCopiaBasica(params)); 
		contract.setDATOSUSOLIBREEMPRESA(createDatosUsoLibreEmpresa(params));
	}
	
	private CONTRATO100TYPE createContract100(IContratoType contratoType, ContrataContratoParams ContrataContratoParams) throws ManagerBeanException{
		CONTRATO100TYPE c = (CONTRATO100TYPE) contratoType;
		c.setDATOSMEDIDASFOMENTO(createDatosMedidasFomento(ContrataContratoParams));
		if(ContrataContratoParams.isAnnexData()){
			c.setDATOSANEXOCONTRATORELEVO(createDatosAnexoContratoRelevo(ContrataContratoParams));
		}
		if(ContrataContratoParams.isSchoolWorkshopData()){
			c.setDATOSETCOTE(createDatosEtCote(ContrataContratoParams));
		}
		c.setDATOSCONTRATOEXTRANJERO(createDatosContratoExtranjero(ContrataContratoParams));
		return c;
	}
	private CONTRATO130TYPE createContract130(IContratoType contratoType, ContrataContratoParams ContrataContratoParams) throws ManagerBeanException{
		CONTRATO130TYPE c = (CONTRATO130TYPE) contratoType;
	    c.setDATOSBONIFICACION(createDatosBonificacion(ContrataContratoParams));
	    c.setDATOSMEDIDASFOMENTO(createDatosMedidasFomento(ContrataContratoParams));
	    c.setDATOSANEXOCONTRATORELEVO(createDatosAnexoContratoRelevo(ContrataContratoParams));
	    c.setDATOSETCOTE(createDatosEtCote(ContrataContratoParams));
	    return c;
	}
	private CONTRATO150TYPE createContract150(IContratoType contratoType, ContrataContratoParams ContrataContratoParams) throws ManagerBeanException{
		CONTRATO150TYPE c = (CONTRATO150TYPE) contratoType;
		c.setDATOSBONIFICACION(createDatosBonificacion(ContrataContratoParams));
		c.setDATOSMEDIDASFOMENTO(createDatosMedidasFomento(ContrataContratoParams));
		c.setDATOSANEXOCONTRATORELEVO(createDatosAnexoContratoRelevo(ContrataContratoParams));
		c.setDATOSETCOTE(createDatosEtCote(ContrataContratoParams));
		c.setDATOSEMPRESAINSERCION(createDatosEmpresaInsercion(ContrataContratoParams));
		return c;
	}
	private CONTRATO200TYPE createContract200(IContratoType contratoType, ContrataContratoParams ContrataContratoParams) throws ManagerBeanException{
		CONTRATO200TYPE c = (CONTRATO200TYPE) contratoType;
		c.setDATOSMEDIDASFOMENTO(createDatosMedidasFomento(ContrataContratoParams));
		c.setDATOSANEXOCONTRATORELEVO(createDatosAnexoContratoRelevo(ContrataContratoParams));
		c.setDATOSETCOTE(createDatosEtCote(ContrataContratoParams));
		c.setDATOSCONTRATOEXTRANJERO(createDatosContratoExtranjero(ContrataContratoParams));
		c.setDATOSREDUCCIONRDL12011(createDatosReduccionRdl2011(ContrataContratoParams));
		c.setDATOSCONTRATOTIEMPOPARCIAL(createDatosContratoTiempoParcial(ContrataContratoParams));
		return c;
	}
	private CONTRATO230TYPE createContract230(IContratoType contratoType, ContrataContratoParams ContrataContratoParams) throws ManagerBeanException{
		CONTRATO230TYPE c = (CONTRATO230TYPE) contratoType;
		c.setDATOSCONTRATOTIEMPOPARCIAL(createDatosContratoTiempoParcial(ContrataContratoParams));
		c.setDATOSBONIFICACION(createDatosBonificacion(ContrataContratoParams));
		c.setDATOSMEDIDASFOMENTO(createDatosMedidasFomento(ContrataContratoParams));
		c.setDATOSANEXOCONTRATORELEVO(createDatosAnexoContratoRelevo(ContrataContratoParams));
		c.setDATOSETCOTE(createDatosEtCote(ContrataContratoParams));
		c.setDATOSREDUCCIONRDL12011(createDatosReduccionRdl2011(ContrataContratoParams));
		return c;
	}
	private CONTRATO250TYPE createContract250(IContratoType contratoType, ContrataContratoParams ContrataContratoParams) throws ManagerBeanException{
		CONTRATO250TYPE c = (CONTRATO250TYPE) contratoType;
		c.setDATOSCONTRATOTIEMPOPARCIAL(createDatosContratoTiempoParcial(ContrataContratoParams));
		c.setDATOSBONIFICACION(createDatosBonificacion(ContrataContratoParams));
		c.setDATOSMEDIDASFOMENTO(createDatosMedidasFomento(ContrataContratoParams));
		c.setDATOSANEXOCONTRATORELEVO(createDatosAnexoContratoRelevo(ContrataContratoParams));
		c.setDATOSETCOTE(createDatosEtCote(ContrataContratoParams));
		c.setDATOSEMPRESAINSERCION(createDatosEmpresaInsercion(ContrataContratoParams));
		c.setDATOSREDUCCIONRDL12011(createDatosReduccionRdl2011(ContrataContratoParams));
		return c;
	}
	private CONTRATO300TYPE createContract300(IContratoType contratoType, ContrataContratoParams ContrataContratoParams) throws ManagerBeanException{
		CONTRATO300TYPE c = (CONTRATO300TYPE) contratoType;
	    c.setDATOSCONTRATOTIEMPOPARCIAL(createDatosContratoTiempoParcial(ContrataContratoParams));
	    c.setDATOSMEDIDASFOMENTO(createDatosMedidasFomento(ContrataContratoParams));
	    c.setDATOSANEXOCONTRATORELEVO(createDatosAnexoContratoRelevo(ContrataContratoParams));
	    c.setDATOSETCOTE(createDatosEtCote(ContrataContratoParams));
	    c.setDATOSREDUCCIONRDL12011(createDatosReduccionRdl2011(ContrataContratoParams));
	    return c;
	}
	private CONTRATO330TYPE createContract330(IContratoType contratoType, ContrataContratoParams ContrataContratoParams) throws ManagerBeanException{
		CONTRATO330TYPE c = (CONTRATO330TYPE) contratoType;
		c.setDATOSCONTRATOTIEMPOPARCIAL(createDatosContratoTiempoParcial(ContrataContratoParams));
		c.setDATOSBONIFICACION(createDatosBonificacion(ContrataContratoParams));
		c.setDATOSMEDIDASFOMENTO(createDatosMedidasFomento(ContrataContratoParams));
		c.setDATOSANEXOCONTRATORELEVO(createDatosAnexoContratoRelevo(ContrataContratoParams));
		c.setDATOSETCOTE(createDatosEtCote(ContrataContratoParams));
		c.setDATOSREDUCCIONRDL12011(createDatosReduccionRdl2011(ContrataContratoParams));
		return c;
	}
	private CONTRATO350TYPE createContract350(IContratoType contratoType, ContrataContratoParams ContrataContratoParams) throws ManagerBeanException{
		CONTRATO350TYPE c = (CONTRATO350TYPE) contratoType;
		c.setDATOSCONTRATOTIEMPOPARCIAL(createDatosContratoTiempoParcial(ContrataContratoParams));
		c.setDATOSBONIFICACION(createDatosBonificacion(ContrataContratoParams));
		c.setDATOSMEDIDASFOMENTO(createDatosMedidasFomento(ContrataContratoParams));
		c.setDATOSANEXOCONTRATORELEVO(createDatosAnexoContratoRelevo(ContrataContratoParams));
		c.setDATOSETCOTE(createDatosEtCote(ContrataContratoParams));
		c.setDATOSEMPRESAINSERCION(createDatosEmpresaInsercion(ContrataContratoParams));
		c.setDATOSREDUCCIONRDL12011(createDatosReduccionRdl2011(ContrataContratoParams));
		return c;
	}
	private CONTRATO401TYPE createContract401(IContratoType contratoType, ContrataContratoParams ContrataContratoParams) throws ManagerBeanException{
		CONTRATO401TYPE c = (CONTRATO401TYPE) contratoType;
		c.setDATOSETCOTE(createDatosEtCote(ContrataContratoParams));
		c.setDATOSCONTRATOINVESTIGACION(createDatosContratoInvestigacion(ContrataContratoParams));
		c.setPROGEMPLEOPUBLICO(createDatosProgramaEmpleoPublico(ContrataContratoParams));
		c.setDATOSCONTRATOEXTRANJERO(createDatosContratoExtranjero(ContrataContratoParams));
		return c;
	}
	private CONTRATO402TYPE createContract402(IContratoType contratoType, ContrataContratoParams ContrataContratoParams) throws ManagerBeanException{
		CONTRATO402TYPE c = (CONTRATO402TYPE) contratoType;
		c.setDATOSETCOTE(createDatosEtCote(ContrataContratoParams));
		c.setDATOSCOPIABASICA(createDatosCopiaBasica(ContrataContratoParams));
		c.setPROGEMPLEOPUBLICO(createDatosProgramaEmpleoPublico(ContrataContratoParams));
		c.setDATOSCONTRATOEXTRANJERO(createDatosContratoExtranjero(ContrataContratoParams));
		return c;
	}
	private CONTRATO403TYPE createContract403(IContratoType contratoType, ContrataContratoParams ContrataContratoParams) throws ManagerBeanException{
		CONTRATO403TYPE c = (CONTRATO403TYPE) contratoType;
		c.setPROGEMPLEOPUBLICO(createDatosProgramaEmpleoPublico(ContrataContratoParams));
		c.setDATOSCONTRATOINSERCION(createDatosContratoInsercion(ContrataContratoParams));
		return c;
	}
	private CONTRATO410TYPE createContract410(IContratoType contratoType, ContrataContratoParams ContrataContratoParams) throws ManagerBeanException{
		CONTRATO410TYPE c = (CONTRATO410TYPE) contratoType;
		c.setDATOSCONTRATOSUSTITUCION(createDatosContratoSustitucion(ContrataContratoParams));
		c.setDATOSETCOTE(createDatosEtCote(ContrataContratoParams));
		c.setPROGEMPLEOPUBLICO(createDatosProgramaEmpleoPublico(ContrataContratoParams));
		return c;
	}
	private CONTRATO420TYPE createContract420(IContratoType contratoType, ContrataContratoParams ContrataContratoParams) throws ManagerBeanException{
		CONTRATO420TYPE c = (CONTRATO420TYPE) contratoType;
		c.setDATOSCONTRATOPRACTICAS(createDatosContratoPracticas(ContrataContratoParams));
		c.setDATOSETCOTE(createDatosEtCote(ContrataContratoParams));
		c.setDATOSCONTRATOINVESTIGACION(createDatosContratoInvestigacion(ContrataContratoParams));
		c.setPROGEMPLEOPUBLICO(createDatosProgramaEmpleoPublico(ContrataContratoParams));
		return c;
	}
	private CONTRATO421TYPE createContract421(IContratoType contratoType, ContrataContratoParams ContrataContratoParams) throws ManagerBeanException{
		CONTRATO421TYPE c = (CONTRATO421TYPE) contratoType;
		c.setDATOSCONTRATOTIEMPOPARCIAL(createDatosContratoTiempoParcial(ContrataContratoParams));
		c.setDATOSETCOTE(createDatosEtCote(ContrataContratoParams));
		c.setPROGEMPLEOPUBLICO(createDatosProgramaEmpleoPublico(ContrataContratoParams));
		c.setDATOSETT(createDatosEtt(ContrataContratoParams));
		c.setDATOSREDUCCIONFORMACION(createDatosReduccionFormacion(ContrataContratoParams));
		return c;
	}
	private CONTRATO430TYPE createContract430(IContratoType contratoType, ContrataContratoParams ContrataContratoParams) throws ManagerBeanException{
		CONTRATO430TYPE c = (CONTRATO430TYPE) contratoType;
		c.setDATOSBONIFICACION(createDatosBonificacion(ContrataContratoParams));
		c.setDATOSETCOTE(createDatosEtCote(ContrataContratoParams));
		c.setPROGEMPLEOPUBLICO(createDatosProgramaEmpleoPublico(ContrataContratoParams));
		return c;
	}
	private CONTRATO441TYPE createContract441(IContratoType contratoType, ContrataContratoParams ContrataContratoParams) throws ManagerBeanException{
		CONTRATO441TYPE c = (CONTRATO441TYPE) contratoType;
		c.setDATOSANEXOCONTRATORELEVO(createDatosAnexoContratoRelevo(ContrataContratoParams));
		c.setDATOSETCOTE(createDatosEtCote(ContrataContratoParams));
		return c;
	}
	private CONTRATO450TYPE createContract450(IContratoType contratoType, ContrataContratoParams ContrataContratoParams) throws ManagerBeanException{
		CONTRATO450TYPE c = (CONTRATO450TYPE) contratoType;
		c.setDATOSEXCLUSIONSOCIAL(createDatosExclusionSocial());
		c.setDATOSBONIFICACION(createDatosBonificacion(ContrataContratoParams));
		c.setDATOSCONTRATOPRACTICAS(createDatosContratoPracticas(ContrataContratoParams));
		c.setDATOSCONTRATOTIEMPOPARCIAL(createDatosContratoTiempoParcial(ContrataContratoParams));
		c.setDATOSCONTRATOSUSTITUCION(createDatosContratoSustitucion(ContrataContratoParams));
		c.setDATOSETCOTE(createDatosEtCote(ContrataContratoParams));
		c.setDATOSCOPIABASICA(createDatosCopiaBasica(ContrataContratoParams));
		c.setPROGEMPLEOPUBLICO(createDatosProgramaEmpleoPublico(ContrataContratoParams));
		c.setDATOSEMPRESAINSERCION(createDatosEmpresaInsercion(ContrataContratoParams));
		return c;
	}
	private CONTRATO452TYPE createContract452(IContratoType contratoType, ContrataContratoParams ContrataContratoParams) throws ManagerBeanException{
		CONTRATO452TYPE c = (CONTRATO452TYPE) contratoType;
		c.setDATOSBONIFICACION(createDatosBonificacion(ContrataContratoParams));
		c.setDATOSETCOTE(createDatosEtCote(ContrataContratoParams));
		c.setDATOSCOPIABASICA(createDatosCopiaBasica(ContrataContratoParams));
		c.setPROGEMPLEOPUBLICO(createDatosProgramaEmpleoPublico(ContrataContratoParams));
		c.setDATOSEMPRESAINSERCION(createDatosEmpresaInsercion(ContrataContratoParams));
		return c;
	}
	private CONTRATO501TYPE createContract501(IContratoType contratoType, ContrataContratoParams ContrataContratoParams) throws ManagerBeanException{
		CONTRATO501TYPE c = (CONTRATO501TYPE) contratoType;
		c.setDATOSCONTRATOTIEMPOPARCIAL(createDatosContratoTiempoParcial(ContrataContratoParams));
		c.setDATOSETCOTE(createDatosEtCote(ContrataContratoParams));
		c.setDATOSCONTRATOINVESTIGACION(createDatosContratoInvestigacion(ContrataContratoParams));
		c.setPROGEMPLEOPUBLICO(createDatosProgramaEmpleoPublico(ContrataContratoParams));
		c.setDATOSCONTRATOEXTRANJERO(createDatosContratoExtranjero(ContrataContratoParams));
		c.setDATOSREDUCCIONRDL12011(createDatosReduccionRdl2011(ContrataContratoParams));
		return c;
	}
	private CONTRATO502TYPE createContract502(IContratoType contratoType, ContrataContratoParams ContrataContratoParams) throws ManagerBeanException{
		CONTRATO502TYPE c = (CONTRATO502TYPE) contratoType;
		c.setDATOSCONTRATOTIEMPOPARCIAL(createDatosContratoTiempoParcial(ContrataContratoParams));
		c.setDATOSETCOTE(createDatosEtCote(ContrataContratoParams));
		c.setPROGEMPLEOPUBLICO(createDatosProgramaEmpleoPublico(ContrataContratoParams));
		c.setDATOSCONTRATOEXTRANJERO(createDatosContratoExtranjero(ContrataContratoParams));
		c.setDATOSREDUCCIONRDL12011(createDatosReduccionRdl2011(ContrataContratoParams));
		return c;
	}
	private CONTRATO503TYPE createContract503(IContratoType contratoType, ContrataContratoParams ContrataContratoParams) throws ManagerBeanException{
		CONTRATO503TYPE c = (CONTRATO503TYPE) contratoType;
		c.setDATOSCONTRATOTIEMPOPARCIAL(createDatosContratoTiempoParcial(ContrataContratoParams));
		c.setPROGEMPLEOPUBLICO(createDatosProgramaEmpleoPublico(ContrataContratoParams));
		c.setDATOSCONTRATOINSERCION(createDatosContratoInsercion(ContrataContratoParams));
		return c;
	}
	private CONTRATO510TYPE createContract510(IContratoType contratoType, ContrataContratoParams ContrataContratoParams) throws ManagerBeanException{
		CONTRATO510TYPE c = (CONTRATO510TYPE) contratoType;
		c.setDATOSCONTRATOTIEMPOPARCIAL(createDatosContratoTiempoParcial(ContrataContratoParams));
		c.setDATOSCONTRATOSUSTITUCION(createDatosContratoSustitucion(ContrataContratoParams));
		c.setDATOSETCOTE(createDatosEtCote(ContrataContratoParams));
		c.setPROGEMPLEOPUBLICO(createDatosProgramaEmpleoPublico(ContrataContratoParams));
		return c;
	}
	private CONTRATO520TYPE createContract520(IContratoType contratoType, ContrataContratoParams ContrataContratoParams) throws ManagerBeanException{
		CONTRATO520TYPE c = (CONTRATO520TYPE) contratoType;
		c.setDATOSCONTRATOPRACTICAS(createDatosContratoPracticas(ContrataContratoParams));
		c.setDATOSCONTRATOTIEMPOPARCIAL(createDatosContratoTiempoParcial(ContrataContratoParams));
		c.setDATOSETCOTE(createDatosEtCote(ContrataContratoParams));
		c.setDATOSCONTRATOINVESTIGACION(createDatosContratoInvestigacion(ContrataContratoParams));
		c.setPROGEMPLEOPUBLICO(createDatosProgramaEmpleoPublico(ContrataContratoParams));
		c.setDATOSREDUCCIONRDL12011(createDatosReduccionRdl2011(ContrataContratoParams));
		return c;
	}
	private CONTRATO530TYPE createContract530(IContratoType contratoType, ContrataContratoParams ContrataContratoParams) throws ManagerBeanException{
		CONTRATO530TYPE c = (CONTRATO530TYPE) contratoType;
		c.setDATOSBONIFICACION(createDatosBonificacion(ContrataContratoParams));
		c.setDATOSCONTRATOTIEMPOPARCIAL(createDatosContratoTiempoParcial(ContrataContratoParams));
		c.setDATOSETCOTE(createDatosEtCote(ContrataContratoParams));
		c.setPROGEMPLEOPUBLICO(createDatosProgramaEmpleoPublico(ContrataContratoParams));
		c.setDATOSREDUCCIONRDL12011(createDatosReduccionRdl2011(ContrataContratoParams));
		return c;
	}
	private CONTRATO540TYPE createContract540(IContratoType contratoType, ContrataContratoParams ContrataContratoParams) throws ManagerBeanException{
		CONTRATO540TYPE c = (CONTRATO540TYPE) contratoType;
		c.setDATOSCONTRATOTIEMPOPARCIAL(createDatosContratoTiempoParcial(ContrataContratoParams));
		c.setDATOSETCOTE(createDatosEtCote(ContrataContratoParams));
		return c;
	}
	private CONTRATO541TYPE createContract541(IContratoType contratoType, ContrataContratoParams ContrataContratoParams) throws ManagerBeanException{
		CONTRATO541TYPE c = (CONTRATO541TYPE) contratoType;
		c.setDATOSCONTRATOTIEMPOPARCIAL(createDatosContratoTiempoParcial(ContrataContratoParams));
		c.setDATOSANEXOCONTRATORELEVO(createDatosAnexoContratoRelevo(ContrataContratoParams));
		c.setDATOSETCOTE(createDatosEtCote(ContrataContratoParams));
		return c;
	}
	private CONTRATO550TYPE createContract550(IContratoType contratoType, ContrataContratoParams ContrataContratoParams) throws ManagerBeanException{
		CONTRATO550TYPE c = (CONTRATO550TYPE) contratoType;
		c.setDATOSEXCLUSIONSOCIAL(createDatosExclusionSocial());
		c.setDATOSBONIFICACION(createDatosBonificacion(ContrataContratoParams));
		c.setDATOSCONTRATOPRACTICAS(createDatosContratoPracticas(ContrataContratoParams));
		c.setDATOSCONTRATOTIEMPOPARCIAL(createDatosContratoTiempoParcial(ContrataContratoParams));
		c.setDATOSCONTRATOSUSTITUCION(createDatosContratoSustitucion(ContrataContratoParams));
		c.setDATOSETCOTE(createDatosEtCote(ContrataContratoParams));
		c.setDATOSCOPIABASICA(createDatosCopiaBasica(ContrataContratoParams));
		c.setPROGEMPLEOPUBLICO(createDatosProgramaEmpleoPublico(ContrataContratoParams));
		c.setDATOSEMPRESAINSERCION(createDatosEmpresaInsercion(ContrataContratoParams));
		c.setDATOSREDUCCIONRDL12011(createDatosReduccionRdl2011(ContrataContratoParams));
		return c;
	}
	private CONTRATO552TYPE createContract552(IContratoType contratoType, ContrataContratoParams ContrataContratoParams) throws ManagerBeanException{
		CONTRATO552TYPE c = (CONTRATO552TYPE) contratoType;
		c.setDATOSBONIFICACION(createDatosBonificacion(ContrataContratoParams));
		c.setDATOSCONTRATOTIEMPOPARCIAL(createDatosContratoTiempoParcial(ContrataContratoParams));
		c.setDATOSETCOTE(createDatosEtCote(ContrataContratoParams));
		c.setDATOSCOPIABASICA(createDatosCopiaBasica(ContrataContratoParams));
		c.setPROGEMPLEOPUBLICO(createDatosProgramaEmpleoPublico(ContrataContratoParams));
		c.setDATOSEMPRESAINSERCION(createDatosEmpresaInsercion(ContrataContratoParams));
		return c;
	}
	private CONTRATO970TYPE createContract970(IContratoType contratoType, ContrataContratoParams ContrataContratoParams) throws ManagerBeanException{
		CONTRATO970TYPE c = (CONTRATO970TYPE) contratoType;
		c.setDATOSETCOTE(createDatosEtCote(ContrataContratoParams));
		c.setPROGEMPLEOPUBLICO(createDatosProgramaEmpleoPublico(ContrataContratoParams));
		return c;
	}
	private CONTRATO980TYPE createContract980(IContratoType contratoType, ContrataContratoParams ContrataContratoParams) throws ManagerBeanException{
		CONTRATO980TYPE c = (CONTRATO980TYPE) contratoType;
		c.setDATOSETCOTE(createDatosEtCote(ContrataContratoParams));
		return c;
	}
	private CONTRATO990TYPE createContract990(IContratoType contratoType, ContrataContratoParams ContrataContratoParams) throws ManagerBeanException{
		CONTRATO990TYPE c = (CONTRATO990TYPE) contratoType;
		c.setDATOSETCOTE(createDatosEtCote(ContrataContratoParams));
		c.setDATOSCOPIABASICA(createDatosCopiaBasica(ContrataContratoParams));
		c.setPROGEMPLEOPUBLICO(createDatosProgramaEmpleoPublico(ContrataContratoParams));
		return c;
	}
	
	/**
	 * <xsd:complexType name="DATOS_EMPRESATYPE">
		<xsd:annotation>
			<xsd:documentation xml:lang="es">Datos de la empresa que contrata</xsd:documentation>
		</xsd:annotation>
		<xsd:sequence>
			<xsd:element name="CIF_NIF_EMPRESA" type="CIFNIFTYPE">
				<xsd:annotation>
					<xsd:documentation xml:lang="es">CIF/ NIF de la empresa que contrata</xsd:documentation>
				</xsd:annotation>
			</xsd:element>
			<xsd:element name="CODIGO_CUENTA_COTIZACION">
				<xsd:annotation>
					<xsd:documentation xml:lang="es">Código de la cuenta de cotización de la empresa.  
					Su composición corresponde a la unión de los datos de :  
					régimen de cotización(4)-provincia(2)-número de cuenta de cotización(7)-dígito de control(2). 
					Los posibles valores que puede tomar el régimen de cotización se encuentran codificados en la tabla TCHRGCOT.txt de la Ayuda XML 
					- Ultima versión - Tablas de códigos.</xsd:documentation>
				</xsd:annotation>
				<xsd:simpleType>
					<xsd:restriction base="xsd:string">
						<xsd:pattern value="\d{15}"/>
					</xsd:restriction>
				</xsd:simpleType>
			</xsd:element>
		</xsd:sequence>
	</xsd:complexType>
	 * @param params
	 * @return
	 * @throws ManagerBeanException
	 */
	private DATOSEMPRESATYPE createDatosEmpresa(ContrataContratoParams params) throws ManagerBeanException {
		DATOSEMPRESATYPE datos = factory.createDATOSEMPRESATYPE();
		datos.setCIFNIFEMPRESA(createCifNif(getContract().getWorkPlace().getEnterprise().getRegistry().getDocument()));
		EnterpriseCCC ccc = getContract().getEnterpriseCCC();
		// TODO: research about ccc quote regime
		String quoteRegime = "0000";
		if(getContract().getEnterpriseCCC().getActivity().getType()==SSRegimeType.GENERAL){
			quoteRegime = TCHRGCOT.TCHRGCOT_0111.getCode();
		} else if(getContract().getEnterpriseCCC().getActivity().getType()==SSRegimeType.AGRICULTURAL){
			quoteRegime = TCHRGCOT.TCHRGCOT_0613.getCode();
		} else if(getContract().getEnterpriseCCC().getActivity().getType()==SSRegimeType.ARTIST){
			quoteRegime = TCHRGCOT.TCHRGCOT_0112.getCode();
		} else if(getContract().getEnterpriseCCC().getActivity().getType()==SSRegimeType.COAL_MINING){
			quoteRegime = TCHRGCOT.TCHRGCOT_0911.getCode();
		} else if(getContract().getEnterpriseCCC().getActivity().getType()==SSRegimeType.DOMESTIC_EMPLOYEES){
			quoteRegime = TCHRGCOT.TCHRGCOT_0138.getCode();
		} else if(getContract().getEnterpriseCCC().getActivity().getType()==SSRegimeType.SEA_WORKERS){
			quoteRegime = TCHRGCOT.TCHRGCOT_0800.getCode();
		} else if(getContract().getEnterpriseCCC().getActivity().getType()==SSRegimeType.SELF_EMPLOYED){
			quoteRegime = TCHRGCOT.TCHRGCOT_0721.getCode();
		} else if(getContract().getEnterpriseCCC().getActivity().getType()==SSRegimeType.STUDENT_INSURANCE){
			quoteRegime = TCHRGCOT.TCHRGCOT_1911.getCode();
		}
		if(ccc!=null){
			datos.setCODIGOCUENTACOTIZACION(quoteRegime+ccc.getCcc());
		}
		return datos;
	}

	/**
	 * <xsd:complexType name="DATOS_TRABAJADORTYPE">
		<xsd:annotation>
			<xsd:documentation xml:lang="es">Datos del trabajador contratado</xsd:documentation>
		</xsd:annotation>
		<xsd:sequence>
			<xsd:element name="IDENTIFICADORPFISICA">
				<xsd:annotation>
					<xsd:documentation xml:lang="es">Identificador de la persona física.  
					Los tipos de documento admitidos (1ª letra del identificador) se encuentran codificados en la tabla STDIDETC.txt de la Ayuda XML 
					- Ultima versión - Tablas de códigos. </xsd:documentation>
				</xsd:annotation>
				<xsd:simpleType>
					<xsd:restriction base="xsd:string">
						<xsd:maxLength value="12"/>
						<xsd:pattern value="[DEUW][0-9XYZ ]+\d{7}[A-Z]"/>
					</xsd:restriction>
				</xsd:simpleType>
			</xsd:element>
			<xsd:element name="NOMBRE_APELLIDOS" type="NOMBREAPELLIDOSTYPE">
				<xsd:annotation>
					<xsd:documentation xml:lang="es">Nombre y apellidos del trabajador contratado</xsd:documentation>
				</xsd:annotation>
			</xsd:element>
			<xsd:element name="SEXO">
				<xsd:annotation>
					<xsd:documentation xml:lang="es">Sexo del trabajador contratado. 
					Sus posibles valores se encuentran codificados en la tabla TCMCSEXO.txt de la Ayuda XML 
					- Ultima versión - Tablas de códigos.</xsd:documentation>
				</xsd:annotation>
				<xsd:simpleType>
					<xsd:restriction base="xsd:string">
						<xsd:pattern value="\d{1}"/>
					</xsd:restriction>
				</xsd:simpleType>
			</xsd:element>
			<xsd:element name="FECHA_NACIMIENTO" type="FECHATYPE">
				<xsd:annotation>
					<xsd:documentation xml:lang="es">Fecha de nacimiento del trabajador contratado</xsd:documentation>
				</xsd:annotation>
			</xsd:element>
			<xsd:element name="NACIONALIDAD" type="PAISTYPE">
				<xsd:annotation>
					<xsd:documentation xml:lang="es">Nacionalidad del trabajador contratado</xsd:documentation>
				</xsd:annotation>
			</xsd:element>
			<xsd:element name="MUNICIPIO_RESIDENCIA" type="MUNICIPIOTYPE" minOccurs="0">
				<xsd:annotation>
					<xsd:documentation xml:lang="es">Domicilio de residencia del trabajador contratado. 
					Obligatorio cuando el PAIS_RESIDENCIA sea 724 (España).</xsd:documentation>
				</xsd:annotation>
			</xsd:element>
			<xsd:element name="PAIS_RESIDENCIA" type="PAISTYPE">
				<xsd:annotation>
					<xsd:documentation xml:lang="es">País de residencia del trabajador contratado</xsd:documentation>
				</xsd:annotation>
			</xsd:element>
			<xsd:element name="NUMERO_SEGURIDAD_SOCIAL" minOccurs="0">
				<xsd:annotation>
					<xsd:documentation xml:lang="es">Número de afiliación a la Seguridad Social del trabajador contratado.  
					Su composición corresponde a la unión de los datos de :  provincia(2)-número (8)-dígito de control(2).  
					Obligatorio cuando la 1ª letra del IDENTIFICADORPFISICA NO sea una D ó una E (tipo de documento NO sea un DNI ó un NIE).</xsd:documentation>
				</xsd:annotation>
				<xsd:simpleType>
					<xsd:restriction base="xsd:string">
						<xsd:pattern value="\d{12}"/>
					</xsd:restriction>
				</xsd:simpleType>
			</xsd:element>
		</xsd:sequence>
	</xsd:complexType>
	 * @param params
	 * @return
	 * @throws ManagerBeanException
	 */
	private DATOSTRABAJADORTYPE createDatosTrabajador(ContrataContratoParams params) throws ManagerBeanException {
		Person person = getContract().getPerson();
		DATOSTRABAJADORTYPE datos = factory.createDATOSTRABAJADORTYPE(); 
		if(person.getBirthDate()!=null){
			datos.setFECHANACIMIENTO(getFormatedDate(person.getBirthDate()));
		} else {
//			AonUtil.addErrorMessage("El trabajador no tiene definida la fecha de nacimiento.");
		}
		if(StringUtils.isEmpty(person.getRegistry().getDocument())){
//			AonUtil.addErrorMessage("El trabajador no tiene definido el número de documento.");
		} else {
			// FIXME
			/*
			"D";"D.N.I"
			"E";"NUMERO IDENTIFICATIVO EXTRANJERO"
			"U";"CIUDADANOS DE LA UE/EEE SIN NIE"
			"W";"CIUD.QUE NO PERTENECEN A UE/EEE.SIN NIE"
			 */
			if(person.getRegistry().getDocumentType()==DocumentType.NIF){
				datos.setIDENTIFICADORPFISICA("D"+person.getRegistry().getDocument());
			} else if(person.getRegistry().getDocumentType()==DocumentType.NIE){
				datos.setIDENTIFICADORPFISICA("E"+person.getRegistry().getDocument());
			}
		}
		datos.setNACIONALIDAD(completeLength(person.getRegistry().getNationality().getIsoNum(),3,ZERO_VALUE,false));
		datos.setNOMBREAPELLIDOS(createNombreApellidos(person));
		if(StringUtils.isEmpty(person.getSocialSecurityNumber())){
//			AonUtil.addErrorMessage("El trabajador no tiene definido el número de seguridad social.");
		} else {
			datos.setNUMEROSEGURIDADSOCIAL(person.getSocialSecurityNumber());
		}
		if(person.getRegistry().getDefaultAddress()!=null){
			if(person.getRegistry().getDefaultAddress().getGeozone()!=null){
				datos.setPAISRESIDENCIA(completeLength(Country.valueOf(person.getRegistry().getDefaultAddress().getGeozone().getGeoZoneCountry().getCode()).getIsoNum(),3,ZERO_VALUE,false));
			}
			if(person.getRegistry().getDefaultAddress().getMunicipalityCode()!=null){
				datos.setMUNICIPIORESIDENCIA(person.getRegistry().getDefaultAddress().getMunicipalityCode());
			} else {
//				AonUtil.addErrorMessage("El trabajador no tiene definido el municipio de residencia.");
			}
		} else {
//			AonUtil.addErrorMessage("El trabajador no tiene definida la dirección.");
		}
		if(person.getGender()==null || person.getGender()==Gender.UNKNOWN){
//			AonUtil.addErrorMessage("El sexo del trabajador es desconocido.");
		} else {
			datos.setSEXO(person.getGender()==Gender.MALE?"1":"2");
		}
		return datos;
	}

	/**
	 * <xsd:complexType name="NOMBREAPELLIDOSTYPE">
		<xsd:annotation>
			<xsd:documentation xml:lang="es">Nombre y apellidos.</xsd:documentation>
		</xsd:annotation>
		<xsd:sequence>
			<xsd:element name="NOMBRE">
				<xsd:simpleType>
					<xsd:restriction base="xsd:string">
						<xsd:maxLength value="15"/>
					</xsd:restriction>
				</xsd:simpleType>
			</xsd:element>
			<xsd:element name="PRIMER_APELLIDO">
				<xsd:simpleType>
					<xsd:restriction base="xsd:string">
						<xsd:maxLength value="20"/>
					</xsd:restriction>
				</xsd:simpleType>
			</xsd:element>
			<xsd:element name="SEGUNDO_APELLIDO" minOccurs="0">
				<xsd:annotation>
					<xsd:documentation xml:lang="es"> Obligatorio cuando la 1ª letra del IDENTIFICADORPFISICA sea una D (tipo de documento sea un DNI).</xsd:documentation>
				</xsd:annotation>
				<xsd:simpleType>
					<xsd:restriction base="xsd:string">
						<xsd:maxLength value="20"/>
					</xsd:restriction>
				</xsd:simpleType>
			</xsd:element>
		</xsd:sequence>
	</xsd:complexType>
	 * @param person
	 * @return
	 */
	private NOMBREAPELLIDOSTYPE createNombreApellidos(Person person) {
		NOMBREAPELLIDOSTYPE datos = factory.createNOMBREAPELLIDOSTYPE();
		if(person!=null){
			String name = StringUtils.trimToEmpty(person.getName());
			String firstSurname = StringUtils.trimToEmpty(person.getFirstSurname());
			String secondSurname = StringUtils.trimToEmpty(person.getSecondSurname());
			
			name = name.length()>15?name.substring(0, 15):name;
			firstSurname = firstSurname.length()>20?firstSurname.substring(0, 20):firstSurname;
			secondSurname = secondSurname.length()>20?secondSurname.substring(0, 20):secondSurname;
			
			datos.setNOMBRE(name);
			datos.setPRIMERAPELLIDO(firstSurname);
			datos.setSEGUNDOAPELLIDO(secondSurname);
		}
		return datos;
	}

	/**
	 * <xsd:complexType name="DATOS_GENERALESCONTRATOTYPE">
		<xsd:annotation>
			<xsd:documentation xml:lang="es">Datos propios del contrato</xsd:documentation>
		</xsd:annotation>
		<xsd:sequence>
			<xsd:element name="FECHA_INICIO" type="FECHATYPE">
				<xsd:annotation>
					<xsd:documentation xml:lang="es">Fecha de inicio del contrato.</xsd:documentation>
				</xsd:annotation>
			</xsd:element>
			<xsd:element name="FECHA_TERMINO" type="FECHATYPE" minOccurs="0">
				<xsd:annotation>
					<xsd:documentation xml:lang="es">Fecha de término del contrato.  
					Obligatoria para los contratos de códigos: 402, 502, 430, 530, 420, 520, 421, 441, 541, 452, 552, 970.    
					Opcional para los contratos de códigos :  401, 501, 410, 510, 403, 503, 540, 980, 990.</xsd:documentation>
				</xsd:annotation>
			</xsd:element>
			<xsd:element name="IND_CONVENIO_COLECTIVO" minOccurs="0">
				<xsd:annotation>
					<xsd:documentation xml:lang="es">Indicador de convenio colectivo.   
					Obligatorio para : - contratos de códigos 402 y 502 cuando su duración está entre 6 y 12 meses.   
					- contratos de código 421 cuando su duración está entre 6 y 12 meses  
					- contratos de código 401, 501, 450 con modalidad 401 y 550 con modalidad 501 iniciados a partir del 18/06/2010 cuando 
					su duración está entre 36 y 48 meses.    
					Refleja la existencia ("S") o no existencia ("N") de un convenio colectivo que autorice estas duraciones. 
					Para el resto de contratos que no se encuentran en uno de los casos anteriores, este elemento no debe aparecer 
					en el fichero a enviar.</xsd:documentation>
				</xsd:annotation>
				<xsd:simpleType>
					<xsd:restriction base="xsd:string">
						<xsd:pattern value="[SN\s]"/>
					</xsd:restriction>
				</xsd:simpleType>
			</xsd:element>
			<xsd:element name="NIVEL_FORMATIVO">
				<xsd:annotation>
					<xsd:documentation xml:lang="es">Código del nivel formativo.  
					Sus posibles valores se encuentran codificados en la tabla TBONVFOR.txt de la Ayuda XML 
					- Ultima versión - Tablas de códigos.</xsd:documentation>
				</xsd:annotation>
				<xsd:simpleType>
					<xsd:restriction base="xsd:string">
						<xsd:pattern value="\d{2}"/>
					</xsd:restriction>
				</xsd:simpleType>
			</xsd:element>
			<xsd:element name="IND_DISCAPACIDAD" minOccurs="0">
				<xsd:annotation>
					<xsd:documentation xml:lang="es">Indicador de discapacidad.  
					Obligatorio con "S" para contratos de minusválidos.  Obligatorio con "C" para contratos de minusválidos en centros 
					especiales de empleo. Obligatorio con "E", "F" o "G" para contratos de minusválidos de enclaves laborales. 
					Sus posibles valores se encuentran codificados en la tabla TEJINDIS.txt de la Ayuda XML 
					- Ultima versión - Tablas de códigos.</xsd:documentation>
				</xsd:annotation>
				<xsd:simpleType>
					<xsd:restriction base="xsd:string">
						<xsd:pattern value="[SCEFG\s]"/>
					</xsd:restriction>
				</xsd:simpleType>
			</xsd:element>
			<xsd:element name="CODIGO_OCUPACION">
				<xsd:annotation>
					<xsd:documentation xml:lang="es">Código de ocupación del puesto de trabajo.  
					Sus posibles valores se encuentran codificados en la tabla TAICLAOC.txt de la Ayuda XML 
					- Ultima versión - Tablas de códigos. La codificación tabulada de este elemento corresponde a códigos de 4 posiciones, 
					el elemento está definido para admitir un código de 8 posiciones que es la codificación con la que hemos trabajado 
					anteriormente, por tanto y para no cambiar la longitud, el elemento se deberá enviar con 4 blancos por la derecha hasta 
					completar las 8 posiciones.</xsd:documentation>
				</xsd:annotation>
				<xsd:simpleType>
					<xsd:restriction base="xsd:string">
						<xsd:length value="8"/>
					</xsd:restriction>
				</xsd:simpleType>
			</xsd:element>
			<xsd:element name="ID_OFERTA" minOccurs="0">
				<xsd:annotation>
					<xsd:documentation xml:lang="es">Identificador de la oferta. 
					Su composición corresponde a la unión de los datos de :  CC.AA.(2)-Año(4)-número secuencial(6). El elemento está definido 
					para admitir 17 posiciones dado su anterior formato, por tanto y para no cambiar la longitud, el elemento deberá ser enviado 
					con 5 ceros por la izquierda que completarán las 17 posiciones.</xsd:documentation>
				</xsd:annotation>
				<xsd:simpleType>
					<xsd:restriction base="xsd:string">
						<xsd:pattern value="\d{17}"/>
					</xsd:restriction>
				</xsd:simpleType>
			</xsd:element>
			<xsd:element name="CODIGOPROGRAMAEMPLEO" minOccurs="0">
				<xsd:annotation>
					<xsd:documentation xml:lang="es">Código del programa de empleo.  
					Obligatorio para los contratos de inserción (códigos 403 y 503).  Sus posibles valores se encuentran codificados 
					en la tabla TETPGMEM.txt de la Ayuda XML - Ultima versión - Tablas de códigos.</xsd:documentation>
				</xsd:annotation>
				<xsd:simpleType>
					<xsd:restriction base="xsd:string">
						<xsd:pattern value="\d{2}"/>
					</xsd:restriction>
				</xsd:simpleType>
			</xsd:element>
			<xsd:element name="NACIONALIDAD_CT" type="PAISTYPE">
				<xsd:annotation>
					<xsd:documentation xml:lang="es">Código de nacionalidad del centro de trabajo.</xsd:documentation>
				</xsd:annotation>
			</xsd:element>
			<xsd:element name="MUNICIPIO_CT" type="MUNICIPIOTYPE" minOccurs="0">
				<xsd:annotation>
					<xsd:documentation xml:lang="es">Código del municipio del centro de trabajo. 
					Obligatorio cuando la NACIONALIDAD_CT sea 724 (España).</xsd:documentation>
				</xsd:annotation>
			</xsd:element>
			<xsd:element name="OTRAS_LEGISLACIONES" minOccurs="0">
				<xsd:annotation>
					<xsd:documentation xml:lang="es">Otras disposiciones legales para el fomento del empleo (Contratos para mayores de 52 años). 
					Sus posibles valores se encuentran codificados en la tabla THYDISLE.txt de la Ayuda XML 
					- Ultima versión - Tablas de códigos.</xsd:documentation>
				</xsd:annotation>
				<xsd:simpleType>
					<xsd:restriction base="xsd:string">
						<xsd:pattern value="\d{3}"/>
					</xsd:restriction>
				</xsd:simpleType>
			</xsd:element>
			<xsd:element name="TEMPORAL_MINUSV_BONIFICADO" minOccurs="0">
				<xsd:annotation>
					<xsd:documentation xml:lang="es">Obligatorio (S/N) para los códigos 430 y 530 salvo que sean minusválidos 
					en Centros Especiales de Empleo (IND_DISCAPACIDAD=C) . Indica si el contrato temporal para personas con discapacidad 
					es bonificado o no. </xsd:documentation>
				</xsd:annotation>
				<xsd:simpleType>
					<xsd:restriction base="xsd:string">
						<xsd:pattern value="[SN\s]"/>
					</xsd:restriction>
				</xsd:simpleType>
			</xsd:element>
			<xsd:element name="FORMACION_BONIFICADO" minOccurs="0">
				<xsd:annotation>
					<xsd:documentation xml:lang="es">Obligatorio (S/N) para los códigos 421 y 450 con modalidad 421 iniciados 
					entre el 18/06/2010 y el 30/08/2011. Indica si el contrato de formación es bonificado o no. </xsd:documentation>
				</xsd:annotation>
				<xsd:simpleType>
					<xsd:restriction base="xsd:string">
						<xsd:pattern value="[SN\s]"/>
					</xsd:restriction>
				</xsd:simpleType>
			</xsd:element>
			<xsd:element name="DATOS_CAMPAÑAS" minOccurs="0">
				<xsd:annotation>
					<xsd:documentation xml:lang="es">Código de los datos de campaña. Su composición corresponde a la unión de los datos de :  
					CC.AA.(2)-Campo libre(3)-Año(4).</xsd:documentation>
				</xsd:annotation>
				<xsd:simpleType>
					<xsd:restriction base="xsd:string">
						<xsd:pattern value="\d{2}.{3}\d{4}"/>
					</xsd:restriction>
				</xsd:simpleType>
			</xsd:element>
			<xsd:element name="IND_EMPRESA_AAPP_UNIVERSIDAD" minOccurs="0">
				<xsd:annotation>
					<xsd:documentation xml:lang="es">Indicador de empresa. Obligatorio (S/N) para los códigos 401, 501, 450 con modalidad 401 
					y 550 con modalidad 501 iniciados a partir de 19/09/2010 cuando no cumpla con la duración válida y tampoco esté acogido 
					a convenio colectivo que justifique esta duración. Indica si el contrato se realiza (S) por la Administración Pública , 
					Organismo Público vinculado o Universidad , o no es una empresa de estos tipos (N). </xsd:documentation>
				</xsd:annotation>
				<xsd:simpleType>
					<xsd:restriction base="xsd:string">
						<xsd:pattern value="[SN\s]"/>
					</xsd:restriction>
				</xsd:simpleType>
			</xsd:element>
			<xsd:element name="REGULARIZACION_RDL_5_2011" minOccurs="0">
				<xsd:annotation>
					<xsd:documentation xml:lang="es">Indicador de si el contrato se acoge al proceso de regularización establecido en 
					el RDL 5/2011 (ctos. iniciados entre el 07/05/2011 y el 31/07/2011). El único valor posible que puede tomar es "S" (SI) para indicar 
					que se acoge a dicho proceso. </xsd:documentation>
				</xsd:annotation>
				<xsd:simpleType>
					<xsd:restriction base="xsd:string">
						<xsd:pattern value="[S\s]"/>
					</xsd:restriction>
				</xsd:simpleType>
			</xsd:element>
			<xsd:element name="ACOGIDO_LEGISLACION_ANTERIOR" minOccurs="0">
				<xsd:annotation>
					<xsd:documentation xml:lang="es">Indicador de acogida a la legislación anterior. 
					Obligatorio para los códigos 421 y 450 con modalidad 421 de Escuela Taller o de empleo-formación promovido por las Comunidades Autónomas, 
					iniciados a partir de el 31/08/2011. El único valor posible que puede tomar es "S" (SI) para indicar que se acoge a la legislación 
					anterior y no a los nuevos RDL. </xsd:documentation>
				</xsd:annotation>
				<xsd:simpleType>
					<xsd:restriction base="xsd:string">
						<xsd:pattern value="[S\s]"/>
					</xsd:restriction>
				</xsd:simpleType>
			</xsd:element>
			<xsd:element name="PROYECTO_EMPLEO_FORMACION" minOccurs="0">
				<xsd:annotation>
					<xsd:documentation xml:lang="es">Indicador de si el contrato es de un proyecto de empleo-formación promovido por 
					las Comunidades Autónomas. Opcional y sólo para los códigos 421 y 450 con modalidad 421 . El único valor posible que puede 
					tomar es "S" (SI) para indicar que es promovido. </xsd:documentation>
				</xsd:annotation>
				<xsd:simpleType>
					<xsd:restriction base="xsd:string">
						<xsd:pattern value="[S\s]"/>
					</xsd:restriction>
				</xsd:simpleType>
			</xsd:element>
		</xsd:sequence>
	</xsd:complexType>
	 * @param params
	 * @return
	 * @throws ManagerBeanException
	 */
	private DATOSGENERALESCONTRATOTYPE createDatosGeneralesContrato(ContrataContratoParams params) throws ManagerBeanException {
		String tc2 = SEPEUtils.getInstance().getContractDataMap(getContract()).get(ContextVariable.TC2.getName());
		DATOSGENERALESCONTRATOTYPE datos = factory.createDATOSGENERALESCONTRATOTYPE();
		datos.setFECHAINICIO(getFormatedDate(getContract().getStartDate()));
		if( getContract().getEndDate()==null 
				&& (tc2.equals("402") || tc2.equals("502") || tc2.equals("430") 
				|| tc2.equals("530") || tc2.equals("420") || tc2.equals("520")  
				|| tc2.equals("421") || tc2.equals("441") || tc2.equals("541") 
				|| tc2.equals("452") || tc2.equals("552") || tc2.equals("970") ) ){
//			AonUtil.addErrorMessage("La fecha final es necesaria para este tipo de contrato.");
		} else {
			datos.setFECHATERMINO(getFormatedDate(getContract().getEndDate()));
		}
//		Indicador de convenio colectivo.   
//		Obligatorio para : 
//			- contratos de códigos 402 y 502 cuando su duración está entre 6 y 12 meses.   
//			- contratos de código 421 cuando su duración está entre 24 y 36 meses  
//			- contratos de código 401, 501, 450 con modalidad 401 y 550 con modalidad 501 iniciados a partir del 18/06/2010 
//				cuando su duración está entre 36 y 48 meses.    
//		Refleja la existencia ("S") o no existencia ("N") de un convenio colectivo que autorice estas duraciones. 
//		Para el resto de contratos que no se encuentran en uno de los casos anteriores, este elemento no debe aparecer en el fichero a enviar.
		if(getContract().getEndDate()!=null){
			Integer durationInMonths = getMonthsBetweenDates(getContract().getStartDate(), getContract().getEndDate());
			if( ((tc2.equals("402") || tc2.equals("502")) && durationInMonths>=6 && durationInMonths<=12) 
					|| (tc2.equals("421") && durationInMonths>=24 && durationInMonths<=36) 
					|| ((tc2.equals("401") || tc2.equals("501") || tc2.equals("450") || tc2.equals("550")) && durationInMonths>=36 && durationInMonths<=48)  
					){
				datos.setINDCONVENIOCOLECTIVO(params.isCollectiveAgreement()?"S":"N");
			}
		}
		datos.setNIVELFORMATIVO(params.getNivelFormativo()!=null?params.getNivelFormativo().getCode():null);
		if(params.isDisabilityData()){
			datos.setINDDISCAPACIDAD(params.getIndDiscapacidad()!=null?params.getIndDiscapacidad().getCode():null);
		}
		String cno = SEPEUtils.getInstance().getContractDataMap(getContract()).get(ContextVariable.CNO.getName());
		if(StringUtils.isEmpty(cno)){
//			AonUtil.addErrorMessage("El trabajador no tiene definido el código de ocupacion (CNO).");
		} else {
			datos.setCODIGOOCUPACION(completeLength(cno, 8,  BLANK_1, true));
		}
		if(params.isOfferData()){
			datos.setIDOFERTA(completeLength(params.getOffer(), 17, ZERO_VALUE, false));
		}
		if( params.isEmploymentProgramData()){
			datos.setCODIGOPROGRAMAEMPLEO(params.getCodigoProgramaEmpleo()!=null?params.getCodigoProgramaEmpleo().getCode():null);
		}
		datos.setNACIONALIDADCT(completeLength(getContract().getWorkPlace().getAddress().getRegistry().getNationality().getIsoNum(),3,ZERO_VALUE,false));
		datos.setMUNICIPIOCT(getContract().getWorkPlace().getAddress().getMunicipalityCode());
		if(params.isOlderThan52Data()){
			datos.setOTRASLEGISLACIONES(params.getOtrasLegislaciones()!=null?params.getOtrasLegislaciones().getCode():null);
		}
		if( tc2.equals("430") || ( tc2.equals("530") && datos!=null && datos.getINDDISCAPACIDAD()!=null && !datos.getINDDISCAPACIDAD().equals("C") ) ){
			String subsidized = SEPEUtils.getInstance().getContractDataMap(getContract()).get(ContextVariable.SUBSIDIZED.getName());
			datos.setTEMPORALMINUSVBONIFICADO(Boolean.parseBoolean(subsidized)?"S":"N");
		}
		
		Calendar formationStart = Calendar.getInstance();
		formationStart.set(2010, 5, 18);
		Calendar formationEnd = Calendar.getInstance();
		formationEnd.set(2011, 7, 30);
		if(tc2.equals("421") 
				&& getContract().getStartDate().after(formationStart.getTime()) 
				&& getContract().getStartDate().before(formationEnd.getTime())){
			String subsidized = SEPEUtils.getInstance().getContractDataMap(getContract()).get(ContextVariable.SUBSIDIZED.getName());
			datos.setFORMACIONBONIFICADO(Boolean.parseBoolean(subsidized)?"S":"N");
		}
		if(params.isCanpaignData()){
			datos.setDATOSCAMPAÑAS(params.getFullCampaign());
		}
//		Indicador de empresa. 
//		Obligatorio (S/N) para los códigos 401, 501, 450 con modalidad 401 y 
//		550 con modalidad 501 iniciados a partir de 19/09/2010 cuando no cumpla con la duración válida 
//		y tampoco esté acogido a convenio colectivo que justifique esta duración. 
//		Indica si el contrato se realiza (S) por la Administración Pública , Organismo Público vinculado o Universidad , 
//		o no es una empresa de estos tipos (N). 
		// FIXME
		if(tc2.equals("401") || tc2.equals("501") || tc2.equals("450") || tc2.equals("550")){
			datos.setINDEMPRESAAAPPUNIVERSIDAD("N");
		}
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
	 * @param ContrataContratoParams
	 * @return
	 */
	private DATOSMEDIDASFOMENTOTYPE createDatosMedidasFomento(ContrataContratoParams params) {
		if(params.isMedidasFomentoData()){
			DATOSMEDIDASFOMENTOTYPE datos = factory.createDATOSMEDIDASFOMENTOTYPE();
			datos.setINDCOSTEDESPIDO(params.isIndCosteDespido()?"1":"2");
			if(params.isIndCosteDespido()){
				datos.setCODIGOCOLECTIVODESPIDO(params.getCodigoColectivoDespido()!=null?params.getCodigoColectivoDespido().getCode():null);
			} else {
				datos.setCODIGOCOLECTIVODESPIDO(null);
			}
			return datos;
		}
		return null;
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
	 * @param ContrataContratoParams
	 * @return
	 */
	private DATOSANEXOCONTRATORELEVOTYPE createDatosAnexoContratoRelevo(ContrataContratoParams params) {
		if(params.isReliefData()){
			DATOSANEXOCONTRATORELEVOTYPE datos = factory.createDATOSANEXOCONTRATORELEVOTYPE();
			datos.setTIPOTRABAJADOR(params.getTipoTrabajadorRelevo().getCode());
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
	 * @param ContrataContratoParams
	 * @return
	 */
	private DATOSETCOTYPE createDatosEtCote(ContrataContratoParams params) {
		if(params.isSchoolWorkshopData()){
			DATOSETCOTYPE datos = factory.createDATOSETCOTYPE();
			datos.setCODIGOETCOTE(params.getCodigoEtCoTe().getCode());
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
	 * @param ContrataContratoParams
	 * @return
	 */
	private DATOSETTTYPE createDatosEtt(ContrataContratoParams params) {
		if(params.isEttData()){
			DATOSETTTYPE datos = factory.createDATOSETTTYPE();
			datos.setCIFNIFEMPRESAUSUARIA(params.getEttCif()!=null?createCifNif(params.getEttCif()):null);
			datos.setRAZONSOCIALEMPRESAUSUARIA(params.getEttName());
			datos.setINDCTOPLANTILLA(params.isEttContractTemplate()?"S":null);
			datos.setINDEMPRESAEXTRANJERA(params.isEttForeignEnterprise()?"S":null);
			return datos;
		}
		return null;
	}

	private CIFNIFTYPE createCifNif(String cif) {
		CIFNIFTYPE datos = factory.createCIFNIFTYPE();
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
	private DATOSCONTRATOEXTRANJEROTYPE createDatosContratoExtranjero(ContrataContratoParams params) {
		if(params.isAnnexData()){
			DATOSCONTRATOEXTRANJEROTYPE datos = factory.createDATOSCONTRATOEXTRANJEROTYPE();
			datos.setINDCARACTEROFERTA(params.getEmploymentCharacter());
			datos.setAÑOCONTINGENTE(params.getAnexEmploymentYear());
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
	 * @param ContrataContratoParams
	 * @return
	 */
	private DATOSCOMUNICACOPIABASICATYPE createDatosComunicacionCopiaBasica(ContrataContratoParams params) {
		DATOSCOMUNICACOPIABASICATYPE datos = factory.createDATOSCOMUNICACOPIABASICATYPE();
		datos.setDOMICCENTROTRABAJO(getContract().getWorkPlace().getAddress().getFullAddress());
		datos.setTEXTOCOPIABASICA(params.getTextoCopiaBasica());
		datos.setTIPOFIRMA(params.getTipoFirmaCopiaBasica()!=null?params.getTipoFirmaCopiaBasica().getCode():null);
		return datos;
	}
	/**
	 * 
	 * @param ContrataContratoParams
	 * @return
	 */
	private DATOSUSOLIBREEMPRESATYPE createDatosUsoLibreEmpresa(ContrataContratoParams params) {
		if(StringUtils.isNotBlank(params.getUsoLibreEmpresa())){
			DATOSUSOLIBREEMPRESATYPE datos = factory.createDATOSUSOLIBREEMPRESATYPE();
			datos.setUSOLIBREEMPRESA(params.getUsoLibreEmpresa());
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
	 * @param ContrataContratoParams
	 * @return
	 */
	private DATOSCONTRATOTIEMPOPARCIALTYPE createDatosContratoTiempoParcial(ContrataContratoParams params) {
		// TODO
		String tc2 = SEPEUtils.getInstance().getContractDataMap(getContract()).get(ContextVariable.TC2.getName());
		DATOSCONTRATOTIEMPOPARCIALTYPE datos = factory.createDATOSCONTRATOTIEMPOPARCIALTYPE();
		datos.setACTIVIDADSINFECHACIERTA(params.getActividadSinFechaCierta());
		datos.setCOLECTIVOEDAD(params.getColectivoEdad()!=null?params.getColectivoEdad().getCode():null);
		//if( tc2.equals("200") || tc2.equals("230") || tc2.equals("250") ){
		//	datos.setFIJODISCONTINUOPERIODICO(params.getFijoDiscontinuoPeriodico()!=null && params.getFijoDiscontinuoPeriodico()?"S":"N");
		//}
		datos.setHORASANUALESTIEMPOCOMPLETO(params.getHorasAnualesTiempoCompleto());
		String duracionconvenio = (params.getHorasConvenio()==null?"":completeLength(params.getHorasConvenio(), 4, "0", false))+(params.getMinutosConvenio()==null?"":completeLength(params.getMinutosConvenio(), 2, "0", false));
		String duracionformacion = (params.getHorasFormacion()==null?"":completeLength(params.getHorasFormacion(), 4, "0", false))+(params.getMinutosFormacion()==null?"":completeLength(params.getMinutosFormacion(), 2, "0", false));
		
		
		Map<String, String> map = SEPEUtils.getInstance().getContractDataMap(getContract());
		
		String monday = map.get(ContextVariable.MONDAY_HOURS.toString());
		String tuesday = map.get(ContextVariable.TUESDAY_HOURS.toString());
		String thursday = map.get(ContextVariable.THURSDAY_HOURS.toString());
		String wednesday = map.get(ContextVariable.WEDNESDAY_HOURS.toString());
		String friday = map.get(ContextVariable.FRIDAY_HOURS.toString());
		String saturday = map.get(ContextVariable.SATURDAY_HOURS.toString());
		String sunday = map.get(ContextVariable.SUNDAY_HOURS.toString());
		double weekHours = 0.0;
		if(StringUtils.isNotBlank(monday) || StringUtils.isNotBlank(tuesday) || StringUtils.isNotBlank(thursday) 
				|| StringUtils.isNotBlank(wednesday) || StringUtils.isNotBlank(friday) 
				|| StringUtils.isNotBlank(saturday) || StringUtils.isNotBlank(sunday)){
			weekHours += NumberUtils.isNumber(monday)?new Double(monday):0.0;
			weekHours += NumberUtils.isNumber(tuesday)?new Double(tuesday):0.0;
			weekHours += NumberUtils.isNumber(thursday)?new Double(thursday):0.0;
			weekHours += NumberUtils.isNumber(wednesday)?new Double(wednesday):0.0;
			weekHours += NumberUtils.isNumber(friday)?new Double(friday):0.0;
			weekHours += NumberUtils.isNumber(saturday)?new Double(saturday):0.0;
			weekHours += NumberUtils.isNumber(sunday)?new Double(sunday):0.0;
		}
		
		String duracionjornada = String.valueOf(weekHours);
		duracionjornada = (duracionjornada==null?"":completeLength(getHours(duracionjornada), 4, "0", false)+(completeLength(getMinutes(duracionjornada), 2, "0", false)));
		if(StringUtils.isBlank(duracionjornada) || Integer.parseInt(duracionjornada)<=0){
			duracionjornada = (params.getHorasJornada()==null?"":completeLength(params.getHorasJornada(), 4, "0", false))+(params.getMinutosJornada()==null?"":completeLength(params.getMinutosJornada(), 2, "0", false));
		}
		datos.setHORASCONVENIO(duracionconvenio.isEmpty()?null:completeLength(duracionconvenio, 6, "0", false));
		datos.setHORASFORMACION(duracionformacion.isEmpty()?null:completeLength(duracionformacion, 6, "0", false));
		datos.setHORASJORNADA(duracionjornada.isEmpty()?null:completeLength(duracionjornada, 6, "0", false));
		
		if( tc2.equals("421") && duracionformacion.isEmpty() ){
			datos.setINDICFORMACIONTEORICA(params.getIndicFormacionTeorica());
		}
		
		datos.setPORCENTAJEJUBILACIONPARCIAL(params.getPorcentajeJubilacionParcial());
		datos.setPORCJORNADAPACTADA(params.getPorcJornadaPactada());
		datos.setTIPOJORNADA(params.getTipoJornada()!=null?params.getTipoJornada().getCode():TEQPTIEM.TEQPTIEM_S.getCode());
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
	private DATOSREDUCCIONRDL12011TYPE createDatosReduccionRdl2011(ContrataContratoParams params) {
		DATOSREDUCCIONRDL12011TYPE datos = factory.createDATOSREDUCCIONRDL12011TYPE();
		if(params.isReductionData()){
			if(params.getCodigoColectivoReduccion()!=null){
				datos.setCODIGOCOLECTIVOREDUCCION(params.getCodigoColectivoReduccion().getCode());
			}
			datos.setPORCENTAJEREDUCCION(params.getPorcentajeReduccion());
			if(params.getPorcentajeJornadaReduccion()!=null){
				int value = (int)(CommonUtil.round(params.getPorcentajeJornadaReduccion())*100);
				datos.setPORCENTAJEJORNADAREDUCCION(String.valueOf(value));
			}
			return datos;
		}
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
	 * @param ContrataContratoParams
	 * @return
	 */
	private DATOSBONIFICACIONTYPE createDatosBonificacion(ContrataContratoParams params) {
		if(params.isApoyoEmprendedoresData()){
			DATOSBONIFICACIONTYPE datos = new DATOSBONIFICACIONTYPE();
			if( params.getIndDiscapacidad()!=TEJINDIS.TEJINDIS_C ){
				if(params.getColectivoBonificacion()!=null){
					datos.setCODIGOCOLECTIVOBONIF(params.getColectivoBonificacion().getCode());
				}
			}
			String tc2 = SEPEUtils.getInstance().getContractDataMap(getContract()).get(ContextVariable.TC2.getName());
			if( tc2.equals("150") || tc2.equals("250") || tc2.equals("350") ){
				if(params.getIndEmpleadAutonomo()!=null){
					datos.setINDICEMPLEADAUTONOMO(params.getIndEmpleadAutonomo()?"1":"2");
				}
			}
			return datos;
		}
		return null;
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
	private DATOSEMPRESAINSERCIONTYPE createDatosEmpresaInsercion(ContrataContratoParams params) {
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
	private DATOSCOPIABASICATYPE createDatosCopiaBasica(ContrataContratoParams params) {
		// TODO: consultar en pantalla si el contrato es escrito
		DATOSCOPIABASICATYPE datos = null;
		SEPEUtils utils = SEPEUtils.getInstance();
		String tc2 = utils.getContractDataMap(getContract()).get(ContextVariable.TC2.getName());
		if( tc2.equals("402") || tc2.equals("990") ){
			if( tc2.equals("990") ){
				datos = datos==null?new DATOSCOPIABASICATYPE():datos;
				datos.setINDCONTRATOALTADIRECCION("S");
			}
			Date start = utils.getDateWithResettedHours(contract.getStartDate(),true);
			Date end = utils.getDateWithResettedHours(contract.getEndDate(), false);
			if( tc2.equals("402") && end!=null && CommonUtil.getDaysBetweenDates(start, end) <= 28){
				datos = datos==null?new DATOSCOPIABASICATYPE():datos;
				datos.setINDCONTRATOESCRITO("S");
			}
		}
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
	private DATOSPROGEMPLEOPUBLICOTYPE createDatosProgramaEmpleoPublico(ContrataContratoParams params) {
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
	private DATOSCONTRATOINVESTIGACIONTYPE createDatosContratoInvestigacion(ContrataContratoParams params) {
		if(params.isResearchData()){
			DATOSCONTRATOINVESTIGACIONTYPE datos = new  DATOSCONTRATOINVESTIGACIONTYPE();
			if(params.getIndEmpleador()!=null){
				datos.setINDEMPLEADOR(params.getIndEmpleador().getCode());
			}
			if(params.getIndTrabajador()!=null){
				datos.setINDTRABAJADOR(params.getIndTrabajador().getCode());
			}
			datos.setINDRD632006(params.getIndRd632006()?"S":null);
			return datos;
		}
		return null;
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
	private DATOSCONTRATOINSERCIONTYPE createDatosContratoInsercion(ContrataContratoParams ContrataContratoParams) {
		// TODO
		DATOSCONTRATOINSERCIONTYPE datos = new DATOSCONTRATOINSERCIONTYPE();
		datos.setGRUPOCOTIZACIONSEGSOCIAL("");
		return datos;
	}
	/**
	 * <xsd:complexType name="DATOS_CONTRATOSUSTITUCIONTYPE">
		<xsd:annotation>
			<xsd:documentation xml:lang="es">Datos de los contratos de interinidad.</xsd:documentation>
		</xsd:annotation>
		<xsd:sequence>
			<xsd:element name="CAUSA_SUSTITUCION">
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
	private DATOSCONTRATOSUSTITUCIONTYPE createDatosContratoSustitucion(ContrataContratoParams params) {
		if(params.isInterimData()){
			DATOSCONTRATOSUSTITUCIONTYPE datos = new DATOSCONTRATOSUSTITUCIONTYPE();
			if(params.getCausaInterinidad()!=null){
				datos.setCAUSASUSTITUCION(params.getCausaInterinidad().getCode());
			}
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
	private DATOSCONTRATOPRACTICASTYPE createDatosContratoPracticas(ContrataContratoParams params) {
		DATOSCONTRATOPRACTICASTYPE datos = new DATOSCONTRATOPRACTICASTYPE();
		datos.setTITULACIONACADEMICA(params.getTitulacionAcademica());
		if(params.getIndCertifProfesionalidad()!=null){
			datos.setINDCERTIFPROFESIONALIDAD(params.getIndCertifProfesionalidad()?"S":"N");
		}
		return datos;
	}
	/**
	 * Modalidad del contrato de exclusión social. 
	 * Las modalidades de contratación admitidas serán los códigos reseñados a continuación, 
	 * dependiendo de que el contrato de exclusión sea a tiempo parcial ó a tiempo completo, 
	 * así para los contratos de exclusión a tiempo completo (450) la modalidad podrá ser : 401, 402, 410, 420, 421 y 990;  
	 * y para los contratos de exclusión a tiempo parcial (550) la modalidad podrá ser : 501, 502, 510, 520 y 990.
	 * 
	 * @param ContrataContratoParams
	 * @return
	 */
	private DATOSEXCLUSIONSOCIALTYPE createDatosExclusionSocial() {
		// TODO revisar la forma de escoger el codigo TC2 a la hora de crear un unevo contrato
		DATOSEXCLUSIONSOCIALTYPE datos = new DATOSEXCLUSIONSOCIALTYPE();
		datos.setMODALIDADEXCLUSION("000");
		return datos;
	}
	
	/**
	 * <xsd:complexType name="DATOS_REDUCCION_FORMACIONTYPE">
		<xsd:annotation>
			<xsd:documentation xml:lang="es">Datos de reducción de cuotas para los contratos de formación. Opcional para los contratos de formación iniciados a partir del 31/08/2011.</xsd:documentation>
		</xsd:annotation>
		<xsd:sequence>
			<xsd:element name="CODIGO_COLECTIVO_REDUCCION_FORMACION">
				<xsd:annotation>
					<xsd:documentation xml:lang="es">Colectivo de reducción. 
					Sus posibles valores se encuentran codificados en la tabla TQOCOLRE.txt de la Ayuda XML - Ultima versión - Tablas de códigos. </xsd:documentation>
				</xsd:annotation>
				<xsd:simpleType>
					<xsd:restriction base="xsd:string">
						<xsd:pattern value="\d{2}"/>
					</xsd:restriction>
				</xsd:simpleType>
			</xsd:element>
			<xsd:element name="PORCENTAJE_REDUCCION_FORMACION">
				<xsd:annotation>
					<xsd:documentation xml:lang="es">Porcentaje de reducción de la cuota. 
					Solo admite 2 valores : 75% para empresas con más de 250 trabajadores y 100% para empresas con menos de 250 trabajadores. </xsd:documentation>
				</xsd:annotation>
				<xsd:simpleType>
					<xsd:restriction base="xsd:string">
						<xsd:minLength value="2"/>
						<xsd:maxLength value="3"/>
						<xsd:pattern value="([0-9])+"/>
					</xsd:restriction>
				</xsd:simpleType>
			</xsd:element>
		</xsd:sequence>
	</xsd:complexType>
	 * @param ContrataContratoParams
	 * @return
	 */
	private DATOSREDUCCIONFORMACIONTYPE createDatosReduccionFormacion(ContrataContratoParams ContrataContratoParams) {
		DATOSREDUCCIONFORMACIONTYPE datos = new DATOSREDUCCIONFORMACIONTYPE();
		if(ContrataContratoParams.isReductionData()){
			datos.setCODIGOCOLECTIVOREDUCCIONFORMACION(ContrataContratoParams.getCodigoColectivoReduccion()!=null?ContrataContratoParams.getCodigoColectivoReduccion().getCode():null);
			datos.setPORCENTAJEREDUCCIONFORMACION(ContrataContratoParams.getPorcentajeReduccion());
			return datos;
		}
		return null;
	}

	
	/* ***************************************
	 * ***************************************
	 * AUXILIARES
	 * ***************************************
	 * ***************************************
	 */

	private Integer getHours(String value){
		try{
			return (int)Double.parseDouble(value);
		} catch (Exception e) {
			LOGGER.error("Error al obtener las horas del valor");
			LOGGER.error(e.getMessage());
			return null;
		}
	}
	
	private Integer getMinutes(String value){
		try {
			Double _value = Double.parseDouble(value);
			Double fraction = _value - (int)(CommonUtil.round(_value, 2));
			return (int)(60*fraction);
		} catch (Exception e) {
			LOGGER.error("Error al obtener los minutos del valor");
			LOGGER.error(e.getMessage());
			return null;
		}
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
	
	private Integer getMonthsBetweenDates(Date startDate, Date endDate) {
		if(startDate!=null && endDate!=null){
			return (int) ((CommonUtil.getDaysBetweenDates(startDate, endDate, true))/30);
		}
		return null;
	}
	

		
}

