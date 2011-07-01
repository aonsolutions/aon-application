package com.esferalia.aon.ui.payroll.utils;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.event.AbortProcessingException;

import org.apache.commons.lang.time.DateFormatUtils;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.company.Enterprise;
import com.code.aon.person.Person;
import com.code.aon.ql.Criteria;
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.payroll.ContractData;
import com.esferalia.aon.payroll.EnterpriseCCC;
import com.esferalia.aon.payroll.dao.IPayrollAlias;
import com.esferalia.aon.payroll.enumeration.CCCType;
import com.esferalia.aon.payroll.enumeration.ContractCode;
import com.esferalia.aon.payroll.enumeration.ContractVariables;
import com.esferalia.aon.ui.payroll.controller.wizard.ContrataParams;
import com.esferalia.aon.ui.payroll.utils.contractMojo.CIFNIFTYPE;
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
import com.esferalia.aon.ui.payroll.utils.contractMojo.DATOSANEXOCONTRATORELEVOTYPE;
import com.esferalia.aon.ui.payroll.utils.contractMojo.DATOSCOMUNICACOPIABASICATYPE;
import com.esferalia.aon.ui.payroll.utils.contractMojo.DATOSCONTRATOEXTRANJEROTYPE;
import com.esferalia.aon.ui.payroll.utils.contractMojo.DATOSCONTRATOTIEMPOPARCIALTYPE;
import com.esferalia.aon.ui.payroll.utils.contractMojo.DATOSEMPRESATYPE;
import com.esferalia.aon.ui.payroll.utils.contractMojo.DATOSETCOTYPE;
import com.esferalia.aon.ui.payroll.utils.contractMojo.DATOSETTTYPE;
import com.esferalia.aon.ui.payroll.utils.contractMojo.DATOSGENERALESCONTRATOTYPE;
import com.esferalia.aon.ui.payroll.utils.contractMojo.DATOSMEDIDASFOMENTOTYPE;
import com.esferalia.aon.ui.payroll.utils.contractMojo.DATOSREDUCCIONRDL12011TYPE;
import com.esferalia.aon.ui.payroll.utils.contractMojo.DATOSTRABAJADORTYPE;
import com.esferalia.aon.ui.payroll.utils.contractMojo.DATOSUSOLIBREEMPRESATYPE;
import com.esferalia.aon.ui.payroll.utils.contractMojo.NOMBREAPELLIDOSTYPE;
import com.esferalia.aon.ui.payroll.utils.contractMojo.ObjectFactory;

public class ContractXmlWriter {
	
	private static final String ZERO_VALUE = "0";
	private String code;
	private ObjectFactory factory;
	private Contract contract;
	private ContrataParams params;
	private String fileName;
	
	
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public ContrataParams getParams() {
		return params;
	}
	public void setParams(ContrataParams params) {
		this.params = params;
	}
	public Contract getContract() {
		return contract;
	}
	public void setContract(Contract contract) {
		this.contract = contract;
	}
	public String getCode() {
		if(code==null){
			code = getContractDataMap().get(ContractVariables.TC2.getName());
		}
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public ObjectFactory getFactory() {
		if(factory==null){
			factory = new ObjectFactory();
		}
		return factory;
	}
	public void setFactory(ObjectFactory factory) {
		this.factory = factory;
	}
	public Object execute() throws ManagerBeanException{
		if(getContract()==null){
			String msg = "El contrato no se ha cargado correctamente.";
			throw new AbortProcessingException(msg);
		}
		setFileName(getFormatedDate(new Date()));
		if (getCode().equals(ContractCode.C100.getValue())) {
			return createContract100(getContract(), getParams());
		} else if (getCode().equals(ContractCode.C130.getValue())) {
			return createContract130();
		} else if (getCode().equals(ContractCode.C150.getValue())) {
			return createContract150();
		} else if (getCode().equals(ContractCode.C200.getValue())) {
			return createContract200(getContract(), getParams());
		} else if (getCode().equals(ContractCode.C230.getValue())) {
			return createContract230();
		} else if (getCode().equals(ContractCode.C250.getValue())) {
			return createContract250();
		} else if (getCode().equals(ContractCode.C300.getValue())) {
			return createContract300();
		} else if (getCode().equals(ContractCode.C330.getValue())) {
			return createContract330();
		} else if (getCode().equals(ContractCode.C350.getValue())) {
			return createContract350();
		} else if (getCode().equals(ContractCode.C401.getValue())) {
			return createContract401();
		} else if (getCode().equals(ContractCode.C402.getValue())) {
			return createContract402();
		} else if (getCode().equals(ContractCode.C403.getValue())) {
			return createContract403();
		} else if (getCode().equals(ContractCode.C410.getValue())) {
			return createContract410();
		} else if (getCode().equals(ContractCode.C420.getValue())) {
			return createContract420();
		} else if (getCode().equals(ContractCode.C421.getValue())) {
			return createContract421();
		} else if (getCode().equals(ContractCode.C430.getValue())) {
			return createContract430();
		} else if (getCode().equals(ContractCode.C441.getValue())) {
			return createContract441();
		} else if (getCode().equals(ContractCode.C450.getValue())) {
			return createContract450();
		} else if (getCode().equals(ContractCode.C452.getValue())) {
			return createContract452();
		} else if (getCode().equals(ContractCode.C501.getValue())) {
			return createContract501();
		} else if (getCode().equals(ContractCode.C502.getValue())) {
			return createContract502();
		} else if (getCode().equals(ContractCode.C503.getValue())) {
			return createContract503();
		} else if (getCode().equals(ContractCode.C510.getValue())) {
			return createContract510();
		} else if (getCode().equals(ContractCode.C520.getValue())) {
			return createContract520();
		} else if (getCode().equals(ContractCode.C530.getValue())) {
			return createContract530();
		} else if (getCode().equals(ContractCode.C540.getValue())) {
			return createContract540();
		} else if (getCode().equals(ContractCode.C541.getValue())) {
			return createContract541();
		} else if (getCode().equals(ContractCode.C550.getValue())) {
			return createContract550();
		} else if (getCode().equals(ContractCode.C552.getValue())) {
			return createContract552();
		} else if (getCode().equals(ContractCode.C990.getValue())) {
			// TODO no existe ContractCode.C970
			return createContract970();
		} else if (getCode().equals(ContractCode.C990.getValue())) {
			// TODO no existe ContractCode.C980
			return createContract980();
		} else if (getCode().equals(ContractCode.C990.getValue())) {
			return createContract990();
		}
		return null;
	}
	
	private CONTRATO100TYPE createContract100(Contract contract, ContrataParams contrataParams) throws ManagerBeanException{
		CONTRATO100TYPE c = getFactory().createCONTRATO100TYPE();
		c.setDATOSEMPRESA(createDatosEmpresa(contract.getWorkPlace().getEnterprise()));
		c.setDATOSTRABAJADOR(createDatosTrabajador(contract.getPerson()));
		c.setDATOSGENERALESCONTRATO(createDatosGeneralesContrato(contract, contrataParams));
		c.setDATOSMEDIDASFOMENTO(createDatosMedidasFomento(contrataParams));
		if(contrataParams.isAnnexData()){
			c.setDATOSANEXOCONTRATORELEVO(createDatosAnexoContratoRelevo(contrataParams));
		}
		if(contrataParams.isSchoolWorkshopData()){
			c.setDATOSETCOTE(createDatosEtCote(contrataParams));
		}
		if(contrataParams.isEttData()){
			c.setDATOSETT(createDatosEtt(contrataParams));
		}
		c.setDATOSCONTRATOEXTRANJERO(createDatosContratoExtranjero());
		c.setDATOSCOMUNICACOPIABASICA(createDatosComunicacionCopiaBasica(contract, contrataParams));
		c.setDATOSUSOLIBREEMPRESA(createDatosUsoLibreEmpresa());
		return c;
	}
	private CONTRATO130TYPE createContract130(){
		return null;
	}
	private CONTRATO150TYPE createContract150(){
		return null;
	}
	private CONTRATO200TYPE createContract200(Contract contract, ContrataParams contrataParams) throws ManagerBeanException{
		CONTRATO200TYPE c = getFactory().createCONTRATO200TYPE();
		c.setDATOSEMPRESA(createDatosEmpresa(contract.getWorkPlace().getEnterprise()));
		c.setDATOSTRABAJADOR(createDatosTrabajador(contract.getPerson()));
		c.setDATOSGENERALESCONTRATO(createDatosGeneralesContrato(contract, contrataParams));
		c.setDATOSMEDIDASFOMENTO(createDatosMedidasFomento(contrataParams));
		c.setDATOSANEXOCONTRATORELEVO(createDatosAnexoContratoRelevo(contrataParams));
		c.setDATOSETCOTE(createDatosEtCote(contrataParams));
		c.setDATOSETT(createDatosEtt(contrataParams));
		c.setDATOSCONTRATOEXTRANJERO(createDatosContratoExtranjero());
		c.setDATOSCOMUNICACOPIABASICA(createDatosComunicacionCopiaBasica(contract, contrataParams));
		c.setDATOSUSOLIBREEMPRESA(createDatosUsoLibreEmpresa());
		c.setDATOSREDUCCIONRDL12011(createDatosReduccionRdl2011());
		c.setDATOSCONTRATOTIEMPOPARCIAL(createDatosContratoTiempoParcial(contrataParams));
		return c;
	}
	private CONTRATO230TYPE createContract230(){
		return null;
	}
	private CONTRATO250TYPE createContract250(){
		return null;
	}
	private CONTRATO300TYPE createContract300(){
		return null;
	}
	private CONTRATO330TYPE createContract330(){
		return null;
	}
	private CONTRATO350TYPE createContract350(){
		return null;
	}
	private CONTRATO401TYPE createContract401(){
		return null;
	}
	private CONTRATO402TYPE createContract402(){
		return null;
	}
	private CONTRATO403TYPE createContract403(){
		return null;
	}
	private CONTRATO410TYPE createContract410(){
		return null;
	}
	private CONTRATO420TYPE createContract420(){
		return null;
	}
	private CONTRATO421TYPE createContract421(){
		return null;
	}
	private CONTRATO430TYPE createContract430(){
		return null;
	}
	private CONTRATO441TYPE createContract441(){
		return null;
	}
	private CONTRATO450TYPE createContract450(){
		return null;
	}
	private CONTRATO452TYPE createContract452(){
		return null;
	}
	private CONTRATO501TYPE createContract501(){
		return null;
	}
	private CONTRATO502TYPE createContract502(){
		return null;
	}
	private CONTRATO503TYPE createContract503(){
		return null;
	}
	private CONTRATO510TYPE createContract510(){
		return null;
	}
	private CONTRATO520TYPE createContract520(){
		return null;
	}
	private CONTRATO530TYPE createContract530(){
		return null;
	}
	private CONTRATO540TYPE createContract540(){
		return null;
	}
	private CONTRATO541TYPE createContract541(){
		return null;
	}
	private CONTRATO550TYPE createContract550(){
		return null;
	}
	private CONTRATO552TYPE createContract552(){
		return null;
	}
	private CONTRATO970TYPE createContract970(){
		return null;
	}
	private CONTRATO980TYPE createContract980(){
		return null;
	}
	private CONTRATO990TYPE createContract990(){
		return null;
	}

	private DATOSEMPRESATYPE createDatosEmpresa(Enterprise enterprise) throws ManagerBeanException {
		DATOSEMPRESATYPE datos = getFactory().createDATOSEMPRESATYPE();
		datos.setCIFNIFEMPRESA(createCifNifEmpresa(enterprise.getRegistry().getDocument()));
		datos.setCODIGOCUENTACOTIZACION(getEnterpriseCCC(enterprise));
		return datos;
	}

	private CIFNIFTYPE createCifNifEmpresa(String document) {
		CIFNIFTYPE datos = getFactory().createCIFNIFTYPE();
		datos.setCIFNIF(document);
		return datos;
	}

	private DATOSTRABAJADORTYPE createDatosTrabajador(Person person) throws ManagerBeanException {
		DATOSTRABAJADORTYPE datos = getFactory().createDATOSTRABAJADORTYPE(); 
		datos.setFECHANACIMIENTO(getFormatedDate(person.getBirthDate()));
		datos.setIDENTIFICADORPFISICA(person.getRegistry().getDocument());
		datos.setMUNICIPIORESIDENCIA(person.getRegistry().getDefaultAddress().getGeozone().getName());
		datos.setNACIONALIDAD(person.getRegistry().getNationality().toString());
		datos.setNOMBREAPELLIDOS(createNombreApellidos(person));
		datos.setNUMEROSEGURIDADSOCIAL(person.getSocialSecurityNumber());
		datos.setPAISRESIDENCIA(completeLength(person.getRegistry().getDefaultAddress().getRegistry().getNationality().getIsoNum(),3,ZERO_VALUE,false));
		datos.setSEXO(person.getGender().toString());
		return datos;
	}

	private NOMBREAPELLIDOSTYPE createNombreApellidos(Person person) {
		NOMBREAPELLIDOSTYPE datos = getFactory().createNOMBREAPELLIDOSTYPE();
		datos.setNOMBRE(person!=null?person.getName():null);
		datos.setPRIMERAPELLIDO(person!=null?person.getFirstSurname():null);
		datos.setSEGUNDOAPELLIDO(person!=null?person.getSecondSurname():null);
		return datos;
	}

	private DATOSGENERALESCONTRATOTYPE createDatosGeneralesContrato(Contract contract, ContrataParams contrataParams) throws ManagerBeanException {
		// TODO
		DATOSGENERALESCONTRATOTYPE datos = getFactory().createDATOSGENERALESCONTRATOTYPE();
		datos.setFECHAINICIO(getFormatedDate(contract.getStartDate()));
		datos.setFECHATERMINO(getFormatedDate(contract.getEndDate()));
//		Indicador de convenio colectivo.   
//		Obligatorio para : 
//			- contratos de códigos 402 y 502 cuando su duración está entre 6 y 12 meses.   
//			- contratos de código 421 cuando su duración está entre 24 y 36 meses  
//			- contratos de código 401, 501, 450 con modalidad 401 y 550 con modalidad 501 iniciados a partir del 18/06/2010 
//				cuando su duración está entre 36 y 48 meses.    
//		Refleja la existencia ("S") o no existencia ("N") de un convenio colectivo que autorice estas duraciones. 
//		Para el resto de contratos que no se encuentran en uno de los casos anteriores, este elemento no debe aparecer en el fichero a enviar.
		datos.setINDCONVENIOCOLECTIVO(contrataParams.isCollectiveAgreement()?"S":null);
		datos.setNIVELFORMATIVO(contrataParams.getEducationalLevel()!=null?contrataParams.getEducationalLevel().getValue():null);
//		Indicador de discapacidad.  
//		Obligatorio con "S" para contratos de minusválidos.  
//		Obligatorio con "C" para contratos de minusválidos en centros especiales de empleo. 
//		Obligatorio con "E", "F" o "G" para contratos de minusválidos de enclaves laborales. 
//		Sus posibles valores se encuentran codificados en la tabla TEJINDIS.txt de la Ayuda XML 
		if(contrataParams.isDisabilityData()){
			datos.setINDDISCAPACIDAD(contrataParams.getDisabilityCode()!=null?contrataParams.getDisabilityCode().getValue():null);
		}
		datos.setCODIGOOCUPACION(completeLength(contrataParams.getCno().getCode(), 8, ZERO_VALUE, true));
		if(contrataParams.isOfferData()){
			datos.setIDOFERTA(completeLength(contrataParams.getOffer(), 17, ZERO_VALUE, false));
		}
//		Código del programa de empleo.  
//		Obligatorio para los contratos de inserción (códigos 403 y 503).  
//		Sus posibles valores se encuentran codificados en la tabla TETPGMEM.txt de la Ayuda XML 
		datos.setCODIGOPROGRAMAEMPLEO(contrataParams.getEmploymentProgram()!=null?contrataParams.getEmploymentProgram().getValue():null);
		datos.setNACIONALIDADCT(completeLength(contract.getWorkPlace().getAddress().getRegistry().getNationality().getIsoNum(),3,ZERO_VALUE,false));
//		falta por implementar: tabla con los municipios
		datos.setMUNICIPIOCT(contract.getWorkPlace().getAddress().getGeozone().getName());
		if(contrataParams.isOlderThan52Data()){
			datos.setOTRASLEGISLACIONES(contrataParams.getOtherLaws()!=null?contrataParams.getOtherLaws().getValue():null);
		}
//		contrato temporal para personas con discapacidad bonificado 
//		Obligatorio (S/N) para los códigos 430 y 530 salvo que sean minusválidos en Centros Especiales de Empleo (IND_DISCAPACIDAD=C) . 
//		Indica si el contrato temporal para personas con discapacidad es bonificado o no. 
//		datos.setTEMPORALMINUSVBONIFICADO("");
//		contrato de formación bonificado
//		Obligatorio (S/N) para los códigos 421 y 450 con modalidad 421 iniciados entre el 18/06/2010 y el 31/12/2011. 
//		Indica si el contrato de formación es bonificado o no. 
//		datos.setFORMACIONBONIFICADO("");
		if(contrataParams.isCanpaignData()){
			datos.setDATOSCAMPAÑAS(contrataParams.getCampaign());
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
	
	private DATOSMEDIDASFOMENTOTYPE createDatosMedidasFomento(ContrataParams contrataParams) {
		// TODO
		DATOSMEDIDASFOMENTOTYPE datos = getFactory().createDATOSMEDIDASFOMENTOTYPE();
//		Indicador de acogida a la ley de fomento de la contratación indefinida.  
//		Refleja si el contrato se acoge a la ley de fomento(1) o no se acoge(2).
		datos.setINDCOSTEDESPIDO(contrataParams.isPermanentContractDevelopment()?"1":"2");
//		Código del colectivo de fomento de la contratación indefinida. 
//		Sólo se rellena en el caso de que el IND_COSTE_DESPIDO sea 1 y NO sea un contrato 
//		de Centros Especiales de Empleo (IND_DISCAPACIDAD=C) ó un contrato de Minusválidos (130, 230 y 330) . 
//		Sus posibles valores se encuentran codificados en la tabla TEOCOLDE.txt de la Ayuda XML 
		if(contrataParams.isPermanentContractDevelopment()){
			datos.setCODIGOCOLECTIVODESPIDO(contrataParams.getDismissalCollective()!=null?contrataParams.getDismissalCollective().getValue():null);
		} else {
			datos.setCODIGOCOLECTIVODESPIDO(null);
		}
		return datos;
	}

	private DATOSANEXOCONTRATORELEVOTYPE createDatosAnexoContratoRelevo(ContrataParams contrataParams) {
		DATOSANEXOCONTRATORELEVOTYPE datos = getFactory().createDATOSANEXOCONTRATORELEVOTYPE();
		datos.setTIPOTRABAJADOR(contrataParams.getReliefEmployeeType()!=null?contrataParams.getReliefEmployeeType().getValue():null);
		datos.setNOMBREAPELLIDOS(createNombreApellidos(contrataParams.getReliefPerson()));
		return datos;
	}

	private DATOSETCOTYPE createDatosEtCote(ContrataParams contrataParams) {
		// TODO
		DATOSETCOTYPE datos = getFactory().createDATOSETCOTYPE();
//		Código de escuela taller, casas de oficio y talleres de empleo. 
//		Sus posibles valores se encuentran codificados en la tabla TESCETCO.txt de la Ayuda XML 
//		- Ultima versión - Tablas de códigos.   
//		Para los contratos con códigos 100, 130, 150, 200, 230, 250, 300, 330, 350, 441, 540, 541 y 980 
//		los códigos de escuela taller válidos sólo son los acabados en 2 (T02, O02 y E02).
		datos.setCODIGOETCOTE(contrataParams.getSchoolWorkshop()!=null?contrataParams.getSchoolWorkshop().getValue():null);
		return datos;
	}

	private DATOSETTTYPE createDatosEtt(ContrataParams contrataParams) {
		DATOSETTTYPE datos = getFactory().createDATOSETTTYPE();
		datos.setCIFNIFEMPRESAUSUARIA(createCifNif(contrataParams.getEttCif()));
		datos.setINDCTOPLANTILLA(contrataParams.isEttContractTemplate()?"S":null);
		datos.setINDEMPRESAEXTRANJERA(contrataParams.isEttForeignEnterprise()?contrataParams.getEttName():null);
		datos.setRAZONSOCIALEMPRESAUSUARIA(contrataParams.getEttName());
		return datos;
	}

	private CIFNIFTYPE createCifNif(String cif) {
		CIFNIFTYPE datos = getFactory().createCIFNIFTYPE();
		datos.setCIFNIF(cif);
		return datos;
	}

	private DATOSCONTRATOEXTRANJEROTYPE createDatosContratoExtranjero() {
		// TODO
		DATOSCONTRATOEXTRANJEROTYPE datos = getFactory().createDATOSCONTRATOEXTRANJEROTYPE();
//		Indicador del carácter de la oferta.  
//		Refleja si la oferta de empleo tiene un carácter ESTABLE (E) ó por el contrario es de carácter TEMPORAL (T). 
		datos.setAÑOCONTINGENTE(null);
//		Año del contingente. Será el mismo o el año siguiente al de la fecha de inicio del contrato.
		datos.setINDCARACTEROFERTA(null);
		return datos;
	}

	private DATOSCOMUNICACOPIABASICATYPE createDatosComunicacionCopiaBasica(Contract contract, ContrataParams contrataParams) {
		DATOSCOMUNICACOPIABASICATYPE datos = getFactory().createDATOSCOMUNICACOPIABASICATYPE();
		datos.setDOMICCENTROTRABAJO(contract.getWorkPlace().getAddress().getFullAddress());
		datos.setTEXTOCOPIABASICA(contrataParams.getBasicCopyComments());
		datos.setTIPOFIRMA(contrataParams.getBasicCopySignatureType()!=null?contrataParams.getBasicCopySignatureType().getValue():null);
		return datos;
	}

	private DATOSUSOLIBREEMPRESATYPE createDatosUsoLibreEmpresa() {
		DATOSUSOLIBREEMPRESATYPE datos = getFactory().createDATOSUSOLIBREEMPRESATYPE();
		datos.setUSOLIBREEMPRESA(null);
		return datos;
	}
	
	private DATOSCONTRATOTIEMPOPARCIALTYPE createDatosContratoTiempoParcial(ContrataParams contrataParams) {
		// TODO
		DATOSCONTRATOTIEMPOPARCIALTYPE datos = getFactory().createDATOSCONTRATOTIEMPOPARCIALTYPE();
		datos.setACTIVIDADSINFECHACIERTA("");
		datos.setCOLECTIVOEDAD(contrataParams.getAgeGroup());
		datos.setFIJODISCONTINUOPERIODICO("");
		datos.setHORASANUALESTIEMPOCOMPLETO("");
		datos.setHORASCONVENIO("");
		datos.setHORASFORMACION("");
		datos.setHORASJORNADA("");
		datos.setINDICFORMACIONTEORICA("");
		datos.setPORCENTAJEJUBILACIONPARCIAL("");
		datos.setPORCJORNADAPACTADA("");
		datos.setTIPOJORNADA("");
		return datos;
	}
	private DATOSREDUCCIONRDL12011TYPE createDatosReduccionRdl2011() {
		// TODO
		DATOSREDUCCIONRDL12011TYPE datos = getFactory().createDATOSREDUCCIONRDL12011TYPE();
		datos.setCODIGOCOLECTIVOREDUCCION("");
		datos.setPORCENTAJEJORNADAREDUCCION("");
		datos.setPORCENTAJEREDUCCION("");
		return datos;
	}
	
	/*
	 * AUXILIARES
	 */
	private Map<String, String> contractDataMap;
	
	protected Map<String, String> getContractDataMap() {
		if(contractDataMap==null){
			contractDataMap = new HashMap<String, String>();
			try {
				IManagerBean bean = BeanManager.getManagerBean(ContractData.class);
				Criteria criteria = new Criteria();
				criteria.addEqualExpression(bean.getFieldName(IPayrollAlias.CONTRACT_DATA_CONTRACT_ID), getContract().getId());
				criteria.addNullExpression(bean.getFieldName(IPayrollAlias.CONTRACT_DATA_END_DATE));
				for(ITransferObject to: bean.getList(criteria)){
					ContractData data = (ContractData) to;
					contractDataMap.put(data.getName(), data.getExpression().replace('"', ' ').trim());
				}
			} catch (ManagerBeanException e) {
				// NADA, que siga generando el fichero
			}
		}
		return contractDataMap;
	}
	
	private String getEnterpriseCCC(Enterprise enterprise) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(EnterpriseCCC.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IPayrollAlias.ENTERPRISE_CCC_ACTIVITY_ENTERPRISE_ID), enterprise.getId());
		criteria.addEqualExpression(bean.getFieldName(IPayrollAlias.ENTERPRISE_CCC_TYPE), CCCType.PRINCIPAL);
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

