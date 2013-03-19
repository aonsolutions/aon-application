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
import com.code.aon.ql.Criteria;
import com.code.aon.registry.enumeration.DocumentType;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.file.payroll.contract.model.ITransformacionType;
import com.esferalia.aon.file.payroll.contract.generated.transformaciones.CIFNIFTYPE;
import com.esferalia.aon.file.payroll.contract.generated.transformaciones.DATOSADICIONALESTRANSFORMACIONTYPE;
import com.esferalia.aon.file.payroll.contract.generated.transformaciones.DATOSANEXOCONTRATORELEVOTYPE;
import com.esferalia.aon.file.payroll.contract.generated.transformaciones.DATOSBONIFICACIONTYPE;
import com.esferalia.aon.file.payroll.contract.generated.transformaciones.DATOSCOMUNICACOPIABASICATYPE;
import com.esferalia.aon.file.payroll.contract.generated.transformaciones.DATOSCONTRATOTIEMPOPARCIALTYPE;
import com.esferalia.aon.file.payroll.contract.generated.transformaciones.DATOSCONTRATOTYPE;
import com.esferalia.aon.file.payroll.contract.generated.transformaciones.DATOSEMPRESATYPE;
import com.esferalia.aon.file.payroll.contract.generated.transformaciones.DATOSGENERALESTRANSFORMACIONTYPE;
import com.esferalia.aon.file.payroll.contract.generated.transformaciones.DATOSMEDIDASFOMENTOTYPE;
import com.esferalia.aon.file.payroll.contract.generated.transformaciones.DATOSUSOLIBREEMPRESATYPE;
import com.esferalia.aon.file.payroll.contract.generated.transformaciones.NOMBREAPELLIDOSTYPE;
import com.esferalia.aon.file.payroll.contract.generated.transformaciones.TRANSFORMACION109TYPE;
import com.esferalia.aon.file.payroll.contract.generated.transformaciones.TRANSFORMACION139TYPE;
import com.esferalia.aon.file.payroll.contract.generated.transformaciones.TRANSFORMACION189TYPE;
import com.esferalia.aon.file.payroll.contract.generated.transformaciones.TRANSFORMACION209TYPE;
import com.esferalia.aon.file.payroll.contract.generated.transformaciones.TRANSFORMACION239TYPE;
import com.esferalia.aon.file.payroll.contract.generated.transformaciones.TRANSFORMACION289TYPE;
import com.esferalia.aon.file.payroll.contract.generated.transformaciones.TRANSFORMACION309TYPE;
import com.esferalia.aon.file.payroll.contract.generated.transformaciones.TRANSFORMACION389TYPE;
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.payroll.EnterpriseCCC;
import com.esferalia.aon.payroll.enumeration.CCCType;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.payroll.enumeration.ContractCode;
import com.esferalia.aon.ui.payroll.utils.PayrollUtils;

public class ContrataTransformacionesWriter {
	
	private final String ZERO_VALUE = "0";
	
	private com.esferalia.aon.file.payroll.contract.generated.transformaciones.ObjectFactory factory = new com.esferalia.aon.file.payroll.contract.generated.transformaciones.ObjectFactory();;
	
	public ITransformacionType createFile(ITransformacionType transformacionType, ContrataParams params) throws ManagerBeanException{
		String code = getContractDataMap(params.getContract()).get(ContextVariable.TC2.getName());
		if (code.equals(ContractCode.C109.getValue())) {
			return createTransformacion109(transformacionType, params);
		} else if (code.equals(ContractCode.C139.getValue())) {
			return createTransformacion139(transformacionType, params);
		} else if (code.equals(ContractCode.C189.getValue())) {
			return createTransformacion189(transformacionType, params);
		} else if (code.equals(ContractCode.C209.getValue())) {
			return createTransformacion209(transformacionType, params);
		} else if (code.equals(ContractCode.C239.getValue())) {
			return createTransformacion239(transformacionType, params);
		} else if (code.equals(ContractCode.C289.getValue())) {
			return createTransformacion289(transformacionType, params);
		} else if (code.equals(ContractCode.C309.getValue())) {
			return createTransformacion309(transformacionType, params);
//		} else if (code.equals(ContractCode.C339.getValue())) {
//			return createTransformacionContract339(transformacionType, params);
		} else if (code.equals(ContractCode.C389.getValue())) {
			return createTransformacion389(transformacionType, params);
		}
		return null;
	}
	
	
	private ITransformacionType createTransformacion109(ITransformacionType transformacionType, ContrataParams params) throws ManagerBeanException{
		TRANSFORMACION109TYPE type = (TRANSFORMACION109TYPE) transformacionType;
		type.setDATOSEMPRESA(createDatosEmpresa(params));
		type.setDATOSCONTRATO(createDatosContrato(params));
		type.setDATOSGENERALESTRANSFORMACION(createDatosGeneralesTransformacion(params));
		type.setDATOSMEDIDASFOMENTO(createDatosMedidasFomento(params));
		type.setDATOSBONIFICACION(createDatosBonificacion(null));
		type.setDATOSADICIONALESTRANSFORMACION(createDatosAdicionalesTransformacion(params));
		type.setDATOSANEXOCONTRATORELEVO(createDatosAnexoContratoRelevo(params));
		type.setDATOSCOMUNICACOPIABASICA(createDatosComunicacionCopiaBasica(params));
		type.setDATOSUSOLIBREEMPRESA(createDatosUsoLibreEmpresa(params));
		return type;
	}
	private ITransformacionType createTransformacion139(ITransformacionType transformacionType, ContrataParams params) throws ManagerBeanException{
		TRANSFORMACION139TYPE type = (TRANSFORMACION139TYPE) transformacionType;
		type.setDATOSEMPRESA(createDatosEmpresa(params));
		type.setDATOSCONTRATO(createDatosContrato(params));
		type.setDATOSGENERALESTRANSFORMACION(createDatosGeneralesTransformacion(params));
		type.setDATOSMEDIDASFOMENTO(createDatosMedidasFomento(params));
		type.setDATOSBONIFICACION(createDatosBonificacion(params));
		type.setDATOSADICIONALESTRANSFORMACION(createDatosAdicionalesTransformacion(params));
		type.setDATOSANEXOCONTRATORELEVO(createDatosAnexoContratoRelevo(params));
		type.setDATOSCOMUNICACOPIABASICA(createDatosComunicacionCopiaBasica(params));
		type.setDATOSUSOLIBREEMPRESA(createDatosUsoLibreEmpresa(params));
		return type;
	}
	private ITransformacionType createTransformacion189(ITransformacionType transformacionType, ContrataParams params) throws ManagerBeanException{
		TRANSFORMACION189TYPE type = (TRANSFORMACION189TYPE) transformacionType;
		type.setDATOSADICIONALESTRANSFORMACION(createDatosAdicionalesTransformacion(params));
		type.setDATOSANEXOCONTRATORELEVO(createDatosAnexoContratoRelevo(params));
		type.setDATOSCOMUNICACOPIABASICA(createDatosComunicacionCopiaBasica(params));
		type.setDATOSCONTRATO(createDatosContrato(params));
		type.setDATOSEMPRESA(createDatosEmpresa(params));
		type.setDATOSGENERALESTRANSFORMACION(createDatosGeneralesTransformacion(params));
		type.setDATOSMEDIDASFOMENTO(createDatosMedidasFomento(params));
		type.setDATOSUSOLIBREEMPRESA(createDatosUsoLibreEmpresa(params));
		return type;
	}
	private ITransformacionType createTransformacion209(ITransformacionType transformacionType, ContrataParams params) throws ManagerBeanException{
		TRANSFORMACION209TYPE type = (TRANSFORMACION209TYPE) transformacionType;
		type.setDATOSEMPRESA(createDatosEmpresa(params));
		type.setDATOSCONTRATO(createDatosContrato(params));
		type.setDATOSGENERALESTRANSFORMACION(createDatosGeneralesTransformacion(params));
		type.setDATOSCONTRATOTIEMPOPARCIAL(createDatosContratoTiempoParcial(params));
		type.setDATOSMEDIDASFOMENTO(createDatosMedidasFomento(params));
		type.setDATOSBONIFICACION(createDatosBonificacion(params));
		type.setDATOSADICIONALESTRANSFORMACION(createDatosAdicionalesTransformacion(params));
		type.setDATOSANEXOCONTRATORELEVO(createDatosAnexoContratoRelevo(params));
		type.setDATOSCOMUNICACOPIABASICA(createDatosComunicacionCopiaBasica(params));
		type.setDATOSUSOLIBREEMPRESA(createDatosUsoLibreEmpresa(params));
		return type;
	}
	private ITransformacionType createTransformacion239(ITransformacionType transformacionType, ContrataParams params) throws ManagerBeanException{
		TRANSFORMACION239TYPE type = (TRANSFORMACION239TYPE) transformacionType;
		type.setDATOSEMPRESA(createDatosEmpresa(params));
		type.setDATOSCONTRATO(createDatosContrato(params));
		type.setDATOSGENERALESTRANSFORMACION(createDatosGeneralesTransformacion(params));
		type.setDATOSCONTRATOTIEMPOPARCIAL(createDatosContratoTiempoParcial(params));
		type.setDATOSMEDIDASFOMENTO(createDatosMedidasFomento(params));
		type.setDATOSBONIFICACION(createDatosBonificacion(params));
		type.setDATOSADICIONALESTRANSFORMACION(createDatosAdicionalesTransformacion(params));
		type.setDATOSANEXOCONTRATORELEVO(createDatosAnexoContratoRelevo(params));
		type.setDATOSCOMUNICACOPIABASICA(createDatosComunicacionCopiaBasica(params));
		type.setDATOSUSOLIBREEMPRESA(createDatosUsoLibreEmpresa(params));
		return type;
	}
	private ITransformacionType createTransformacion289(ITransformacionType transformacionType, ContrataParams params) throws ManagerBeanException{
		TRANSFORMACION289TYPE type = (TRANSFORMACION289TYPE) transformacionType;
		type.setDATOSEMPRESA(createDatosEmpresa(params));
		type.setDATOSCONTRATO(createDatosContrato(params));
		type.setDATOSGENERALESTRANSFORMACION(createDatosGeneralesTransformacion(params));
		type.setDATOSCONTRATOTIEMPOPARCIAL(createDatosContratoTiempoParcial(params));
		type.setDATOSMEDIDASFOMENTO(createDatosMedidasFomento(params));
		type.setDATOSADICIONALESTRANSFORMACION(createDatosAdicionalesTransformacion(params));
		type.setDATOSANEXOCONTRATORELEVO(createDatosAnexoContratoRelevo(params));
		type.setDATOSCOMUNICACOPIABASICA(createDatosComunicacionCopiaBasica(params));
		type.setDATOSUSOLIBREEMPRESA(createDatosUsoLibreEmpresa(params));
		return null;
	}
	private ITransformacionType createTransformacion309(ITransformacionType transformacionType, ContrataParams params) throws ManagerBeanException{
		TRANSFORMACION309TYPE type = (TRANSFORMACION309TYPE) transformacionType;
		type.setDATOSEMPRESA(createDatosEmpresa(params));
		type.setDATOSCONTRATO(createDatosContrato(params));
		type.setDATOSGENERALESTRANSFORMACION(createDatosGeneralesTransformacion(params));
		type.setDATOSCONTRATOTIEMPOPARCIAL(createDatosContratoTiempoParcial(params));
		type.setDATOSMEDIDASFOMENTO(createDatosMedidasFomento(params));
		type.setDATOSBONIFICACION(createDatosBonificacion(params));
		type.setDATOSADICIONALESTRANSFORMACION(createDatosAdicionalesTransformacion(params));
		type.setDATOSANEXOCONTRATORELEVO(createDatosAnexoContratoRelevo(params));
		type.setDATOSCOMUNICACOPIABASICA(createDatosComunicacionCopiaBasica(params));
		type.setDATOSUSOLIBREEMPRESA(createDatosUsoLibreEmpresa(params));
		return type;
	}
//	private ITransformacionType createTransformacion339(ITransformacionType transformacionType, ContrataParams params){
//		TRANSFORMACION339TYPE type = (TRANSFORMACION339TYPE) transformacionType;
//		
//		return null;
//	}
	private ITransformacionType createTransformacion389(ITransformacionType transformacionType, ContrataParams params) throws ManagerBeanException{
		TRANSFORMACION389TYPE type = (TRANSFORMACION389TYPE) transformacionType;
		type.setDATOSEMPRESA(createDatosEmpresa(params));
		type.setDATOSCONTRATO(createDatosContrato(params));
		type.setDATOSGENERALESTRANSFORMACION(createDatosGeneralesTransformacion(params));
		type.setDATOSCONTRATOTIEMPOPARCIAL(createDatosContratoTiempoParcial(params));
		type.setDATOSMEDIDASFOMENTO(createDatosMedidasFomento(params));
		type.setDATOSADICIONALESTRANSFORMACION(createDatosAdicionalesTransformacion(params));
		type.setDATOSANEXOCONTRATORELEVO(createDatosAnexoContratoRelevo(params));
		type.setDATOSCOMUNICACOPIABASICA(createDatosComunicacionCopiaBasica(params));
		type.setDATOSUSOLIBREEMPRESA(createDatosUsoLibreEmpresa(params));
		return type;
	}
	
	
	
	/**
	 * <xsd:complexType name="DATOS_ANEXOCONTRATORELEVOTYPE">
		<xsd:annotation>
			<xsd:documentation xml:lang="es">Datos anexos de los contratos de relevo.</xsd:documentation>
		</xsd:annotation>
		<xsd:sequence>
			<xsd:element name="NOMBRE_APELLIDOS" type="NOMBREAPELLIDOSTYPE">
				<xsd:annotation>
					<xsd:documentation xml:lang="es">Nombre y apellidos del trabajador de relevo.</xsd:documentation>
				</xsd:annotation>
			</xsd:element>
		</xsd:sequence>
	</xsd:complexType>
	 * @param params
	 * @return
	 */
	private DATOSANEXOCONTRATORELEVOTYPE createDatosAnexoContratoRelevo(ContrataParams params) {
		if(params.isReliefData()){
			DATOSANEXOCONTRATORELEVOTYPE datos = factory.createDATOSANEXOCONTRATORELEVOTYPE();
			datos.setNOMBREAPELLIDOS(createNombreApellidos(params.getReliefPerson()));
			return datos;
		}
		return null;
	}
	
	private NOMBREAPELLIDOSTYPE createNombreApellidos(Person person) {
		NOMBREAPELLIDOSTYPE datos = factory.createNOMBREAPELLIDOSTYPE();
		datos.setNOMBRE(person!=null?person.getName():null);
		datos.setPRIMERAPELLIDO(person!=null?person.getFirstSurname():null);
		datos.setSEGUNDOAPELLIDO(person!=null?person.getSecondSurname():null);
		return datos;
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
					Sus posibles valores se encuentran codificados en la tabla TERFIRCB.txt de la Ayuda XML 
					- Ultima versión - Tablas de códigos.</xsd:documentation>
				</xsd:annotation>
				<xsd:simpleType>
					<xsd:restriction base="xsd:string">
						<xsd:pattern value="\d{1}"/>
					</xsd:restriction>
				</xsd:simpleType>
			</xsd:element>
			<xsd:element name="TEXTO_COPIABASICA" minOccurs="0">
				<xsd:annotation>
					<xsd:documentation xml:lang="es">Texto de la copia básica. 
					Claúsulas y/o condiciones que no vienen reflejados en la comunicación del contrato.</xsd:documentation>
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
	 * @param params
	 * @return
	 */
	private DATOSCOMUNICACOPIABASICATYPE createDatosComunicacionCopiaBasica(ContrataParams params) {
		if(params.isEttData()){
			DATOSCOMUNICACOPIABASICATYPE datos = factory.createDATOSCOMUNICACOPIABASICATYPE();
			datos.setDOMICCENTROTRABAJO(params.getContract().getWorkPlace().getAddress().getFullAddress());
			datos.setTEXTOCOPIABASICA(params.getTextoCopiaBasica());
			datos.setTIPOFIRMA(params.getTipoFirmaCopiaBasica()!=null?params.getTipoFirmaCopiaBasica().getValue():null);
			return datos;
		}
		return null;
	}
	
	/**
	 * <xsd:complexType name="DATOS_EMPRESATYPE">
		<xsd:annotation>
			<xsd:documentation xml:lang="es">Datos de la empresa que realiza la transformación</xsd:documentation>
		</xsd:annotation>
		<xsd:sequence>
			<xsd:element name="CIF_NIF_EMPRESA" type="CIFNIFTYPE">
				<xsd:annotation>
					<xsd:documentation xml:lang="es">CIF/ NIF de la empresa que realiza la transformación</xsd:documentation>
				</xsd:annotation>
			</xsd:element>
			<xsd:element name="CODIGO_CUENTA_COTIZACION">
				<xsd:annotation>
					<xsd:documentation xml:lang="es">Código de la cuenta de cotización de la empresa que realiza la transformación.  
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
	private DATOSEMPRESATYPE createDatosEmpresa(ContrataParams params) throws ManagerBeanException {
		DATOSEMPRESATYPE datos = factory.createDATOSEMPRESATYPE();
		datos.setCIFNIFEMPRESA(createCifNif(params.getContract().getWorkPlace().getEnterprise().getRegistry().getDocument()));
		datos.setCODIGOCUENTACOTIZACION(completeLength(getEnterpriseCCC(params.getContract().getWorkPlace().getEnterprise()),15,"0",false));
		return datos;
	}
	
	private CIFNIFTYPE createCifNif(String cif) {
		CIFNIFTYPE datos = factory.createCIFNIFTYPE();
		datos.setCIFNIF(cif);
		return datos;
	}
	
	/**
	 * <xsd:complexType name="DATOS_MEDIDASFOMENTOTYPE">
		<xsd:annotation>
			<xsd:documentation xml:lang="es">Datos de medidas de fomento de la contratación indefinida. 
			A partir del 12/02/2012 queda derogada esta D.A. por lo que no deberá enviarse este dato para las transformaciones iniciadas 
			a partir de esa fecha.</xsd:documentation>
		</xsd:annotation>
		<xsd:sequence>
			<xsd:element name="IND_COSTE_DESPIDO">
				<xsd:annotation>
					<xsd:documentation xml:lang="es">Indicador de acogida a la ley de fomento de la contratación indefinida.  
					Refleja si la transformación se acoge a la ley de fomento(1) o no se acoge(2).</xsd:documentation>
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
					Sólo se rellena para las transformaciones en las que el IND_COSTE_DESPIDO sea 1. 
					Sus posibles valores se encuentran codificados en la tabla TEOCOLDE.txt de la Ayuda XML 
					- Ultima versión - Tablas de códigos.  </xsd:documentation>
				</xsd:annotation>
				<xsd:simpleType>
					<xsd:restriction base="xsd:string">
						<xsd:pattern value="\d{2}"/>
					</xsd:restriction>
				</xsd:simpleType>
			</xsd:element>
		</xsd:sequence>
	</xsd:complexType>
	 * @param contrataParams
	 * @return
	 */
	private DATOSMEDIDASFOMENTOTYPE createDatosMedidasFomento(ContrataParams contrataParams) {
		// TODO 
		DATOSMEDIDASFOMENTOTYPE datos = factory.createDATOSMEDIDASFOMENTOTYPE();
		datos.setINDCOSTEDESPIDO(contrataParams.isPermanentContractDevelopment()?"1":"2");
		if(contrataParams.isPermanentContractDevelopment()){
			datos.setCODIGOCOLECTIVODESPIDO(contrataParams.getCodigoColectivoDespido()!=null?contrataParams.getCodigoColectivoDespido().getValue():null);
		} else {
			datos.setCODIGOCOLECTIVODESPIDO(null);
		}
		return datos;
	}
	
	/**
	 * <xsd:complexType name="DATOS_USOLIBRE_EMPRESATYPE">
		<xsd:annotation>
			<xsd:documentation xml:lang="es">Datos para uso de la empresa. 
			No se efectúa ninguna validación ni modificación sobre estos datos.</xsd:documentation>
		</xsd:annotation>
		<xsd:sequence>
			<xsd:element name="USOLIBRE_EMPRESA">
				<xsd:simpleType>
					<xsd:restriction base="xsd:string">
						<xsd:maxLength value="30"/>
					</xsd:restriction>
				</xsd:simpleType>
			</xsd:element>
		</xsd:sequence>
	</xsd:complexType>
	 * @param params
	 * @return
	 */
	private DATOSUSOLIBREEMPRESATYPE createDatosUsoLibreEmpresa(ContrataParams params) {
		if(params.getUsoLibreEmpresa()!=null){
			DATOSUSOLIBREEMPRESATYPE datos = factory.createDATOSUSOLIBREEMPRESATYPE();
			datos.setUSOLIBREEMPRESA(params.getUsoLibreEmpresa());
			return datos;
		}
		return null;
	}
	
	/**
	 * <xsd:complexType name="DATOS_GENERALES_TRANSFORMACIONTYPE">
		<xsd:annotation>
			<xsd:documentation xml:lang="es">Datos generales de la transformación</xsd:documentation>
		</xsd:annotation>
		<xsd:sequence>
			<xsd:element name="FECHA_INICIO" type="FECHATYPE">
				<xsd:annotation>
					<xsd:documentation xml:lang="es">Fecha de inicio de la transformación.</xsd:documentation>
				</xsd:annotation>
			</xsd:element>
			<xsd:element name="INDICADOR_DISCONTINUIDAD" minOccurs="0">
				<xsd:annotation>
					<xsd:documentation xml:lang="es">Indicador de discontinuidad. 
					Los únicos valores admitidos son:  "I" (ILT- Invalidez laboral transitoria) y "P"(Prórroga tácita) 
					que justificarán la discontinuidad existente entre la fecha de inicio de la transformación que se está 
					comunicando y la fecha de término de la prórroga/contrato anterior a la transformación.</xsd:documentation>
				</xsd:annotation>
				<xsd:simpleType>
					<xsd:restriction base="xsd:string">
						<xsd:pattern value="[IP\s]"/>
					</xsd:restriction>
				</xsd:simpleType>
			</xsd:element>
			<xsd:element name="FECHA_TERMINO_REAL" type="FECHATYPE" minOccurs="0">
				<xsd:annotation>
					<xsd:documentation xml:lang="es">Fecha de término real del contrato transformado. 
					Obligatoria en caso de existencia de prórroga tácita.</xsd:documentation>
				</xsd:annotation>
			</xsd:element>
			<xsd:element name="CODIGO_OCUPACION">
				<xsd:annotation>
					<xsd:documentation xml:lang="es">Código de ocupación del puesto de trabajo.  
					Sus posibles valores se encuentran codificados en la tabla TAICLAOC.txt de la Ayuda XML 
					- Ultima versión - Tablas de códigos. La codificación tabulada de este elemento corresponde a códigos de 4 posiciones, 
					el elemento está definido para admitir un código de 8 posiciones que es la codificación con la que hemos 
					trabajado anteriormente, por tanto y para no cambiar la longitud, el elemento se deberá enviar con 4 blancos 
					por la derecha hasta completar las 8 posiciones.</xsd:documentation>
				</xsd:annotation>
				<xsd:simpleType>
					<xsd:restriction base="xsd:string">
						<xsd:length value="8"/>
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
		</xsd:sequence>
	</xsd:complexType>
	 * @return
	 */
	private DATOSGENERALESTRANSFORMACIONTYPE createDatosGeneralesTransformacion(ContrataParams params) {
		DATOSGENERALESTRANSFORMACIONTYPE datos = factory.createDATOSGENERALESTRANSFORMACIONTYPE();
		datos.setFECHAINICIO(getFormatedDate(params.getContract().getStartDate()));
		// TODO
//		"I" (ILT- Invalidez laboral transitoria) 
//		"P"(Prórroga tácita)
//		datos.setINDICADORDISCONTINUIDAD(true?"I":"P");
		if(datos.getINDICADORDISCONTINUIDAD()!=null && datos.getINDICADORDISCONTINUIDAD().equals("P")){
			// TODO obtain previous contract endDate
			datos.setFECHATERMINOREAL(null);
		}
		String cno = getContractDataMap(params.getContract()).get(ContextVariable.CNO.getName());
		if(StringUtils.isEmpty(cno)){
			AonUtil.addErrorMessage("El trabajador no tiene definido el código de ocupacion (CNO).");
		} else {
			datos.setCODIGOOCUPACION(completeLength(cno, 8, ZERO_VALUE, true));
		}
		datos.setNACIONALIDADCT(completeLength(params.getContract().getWorkPlace().getAddress().getRegistry().getNationality().getIsoNum(),3,ZERO_VALUE,false));
		datos.setMUNICIPIOCT(completeLength(params.getContract().getWorkPlace().getAddress().getGeozone().getCode(),5,ZERO_VALUE,false));
	    return datos;
	}
	
	/**
	 * <xsd:complexType name="DATOS_CONTRATOTYPE">
		<xsd:annotation>
			<xsd:documentation xml:lang="es">Datos del contrato transformado</xsd:documentation>
		</xsd:annotation>
		<xsd:sequence>
			<xsd:element name="CLAVE_CONTRATO" type="CLAVECONTRATOTYPE" minOccurs="0">
				<xsd:annotation>
					<xsd:documentation xml:lang="es">Identificador del contrato a transformar. 
					Este campo configura una de las 2 opciones por las que se puede identificar el contrato que se va a transformar.  
					En el caso de elegir esta opción, el elemento es obligatorio, y no se deben rellenar el resto 
					de campos de DATOS_CONTRATO.</xsd:documentation>
				</xsd:annotation>
			</xsd:element>
			<xsd:element name="IDENTIFICADORPFISICA" minOccurs="0">
				<xsd:annotation>
					<xsd:documentation xml:lang="es">Identificador de la persona física del contrato a transformar.  
					Este campo junto con la FECHA_INICIO_CTO configura la segunda opción por las que se puede identificar el contrato 
					que se va a transformar.  En el caso de elegir esta opción, el elemento es obligatorio, y no se debe rellenar 
					el campo CLAVE_CONTRATO.  Los tipos de documento admitidos (1ª letra del identificador) se encuentran codificados 
					en la tabla STDIDETC.txt de la Ayuda XML - Ultima versión - Tablas de códigos. </xsd:documentation>
				</xsd:annotation>
				<xsd:simpleType>
					<xsd:restriction base="xsd:string">
						<xsd:maxLength value="12"/>
						<xsd:pattern value="[DEUW][0-9XYZ ]+\d{7}[A-Z]"/>
					</xsd:restriction>
				</xsd:simpleType>
			</xsd:element>
			<xsd:element name="FECHA_INICIO_CTO" type="FECHATYPE" minOccurs="0">
				<xsd:annotation>
					<xsd:documentation xml:lang="es">Fecha de inicio del contrato a transformar.  
					Este campo junto con el IDENTIFICADORPFISICA configura la segunda opción por las que se puede identificar el contrato 
					que se va a transformar.  En el caso de elegir esta opción, el elemento es obligatorio, y no se debe rellenar 
					el campo CLAVE_CONTRATO.</xsd:documentation>
				</xsd:annotation>
			</xsd:element>
		</xsd:sequence>
	</xsd:complexType>
	 * @return
	 */
	private DATOSCONTRATOTYPE createDatosContrato(ContrataParams params) {
		DATOSCONTRATOTYPE datos = factory.createDATOSCONTRATOTYPE();
		datos.setCLAVECONTRATO(null);
		if(datos.getCLAVECONTRATO()==null){
			Person person = params.getContract().getPerson();
			if(StringUtils.isEmpty(person.getRegistry().getDocument())){
				AonUtil.addErrorMessage("El trabajador no tiene definido el número de documento..");
			} else {
				if(person.getRegistry().getDocumentType()==DocumentType.NIF){
					datos.setIDENTIFICADORPFISICA("D"+person.getRegistry().getDocument());
				} else if(person.getRegistry().getDocumentType()==DocumentType.NIE){
					datos.setIDENTIFICADORPFISICA("E"+person.getRegistry().getDocument());
				}
			}
			datos.setFECHAINICIOCTO(null);
		}
		return datos;
	}

	/**
	 * <xsd:complexType name="DATOS_ADICIONALES_TRANSFORMACIONTYPE">
			<xsd:annotation>
				<xsd:documentation xml:lang="es">Datos adicionales de la transformación</xsd:documentation>
			</xsd:annotation>
			<xsd:sequence>
				<xsd:element name="IND_DISCAPACIDAD" minOccurs="0">
					<xsd:annotation>
						<xsd:documentation xml:lang="es">Indicador de discapacidad.  
						Obligatorio con "S" para transformaciones de contratos de minusválidos.  
						Obligatorio con "C" para transformaciones de contratos de minusválidos en centros especiales de empleo. 
						Sus posibles valores se encuentran codificados en la tabla TEJINDIS.txt de la Ayuda XML 
						- Ultima versión - Tablas de códigos.   </xsd:documentation>
					</xsd:annotation>
					<xsd:simpleType>
						<xsd:restriction base="xsd:string">
							<xsd:pattern value="[SC\s]"/>
						</xsd:restriction>
					</xsd:simpleType>
				</xsd:element>
				<xsd:element name="CODIGO_COLECTIVO_REDUCCION" minOccurs="0">
					<xsd:annotation>
						<xsd:documentation xml:lang="es">Colectivo de reducción. 
						Sólo vendrá cumplimentado, y de manera opcional, para las transformaciones de contratos de formación (421) a códigos 189, 289 y 389. 
						Sus posibles valores se encuentran codificados en la tabla TQOCOLRE.txt de la Ayuda XML 
						- Ultima versión - Tablas de códigos. </xsd:documentation>
					</xsd:annotation>
					<xsd:simpleType>
						<xsd:restriction base="xsd:string">
							<xsd:pattern value="\d{2}"/>
						</xsd:restriction>
					</xsd:simpleType>
				</xsd:element>
			</xsd:sequence>
		</xsd:complexType>
		
	 * @param params
	 * @return
	 */
	private DATOSADICIONALESTRANSFORMACIONTYPE createDatosAdicionalesTransformacion(ContrataParams params) {
		DATOSADICIONALESTRANSFORMACIONTYPE datos = factory.createDATOSADICIONALESTRANSFORMACIONTYPE();
		datos.setINDDISCAPACIDAD(params.getIndDiscapacidad()!=null?params.getIndDiscapacidad().getValue():null);
		// TODO
		datos.setCODIGOCOLECTIVOREDUCCION(null);
		return datos;
	}
	
	/**
	 * <xsd:complexType name="DATOS_BONIFICACIONTYPE">
		<xsd:annotation>
			<xsd:documentation xml:lang="es">Datos de bonificación de la transformación.</xsd:documentation>
		</xsd:annotation>
		<xsd:sequence>
			<xsd:element name="ACOGIDO_MATERNIDAD_EXCEDENCIA" minOccurs="0">
				<xsd:annotation>
					<xsd:documentation xml:lang="es">Indicador de acogida a los colectivos de bonificación de "Maternidad o excedencia".  
					El único valor posible que puede tomar es "S" (SI) para indicar que se acoge a dicho colectivo.</xsd:documentation>
				</xsd:annotation>
				<xsd:simpleType>
					<xsd:restriction base="xsd:string">
						<xsd:pattern value="[S\s]"/>
					</xsd:restriction>
				</xsd:simpleType>
			</xsd:element>
			<xsd:element name="COLECTIVO_DISCAPACITADOS" minOccurs="0">
				<xsd:annotation>
					<xsd:documentation xml:lang="es">Código del colectivo de bonificación para discapacitados. 
					Obligatorio para los transformaciones de código 139, 239  y 339 iniciadas a partir del 31/12/2006.  
					Sus posibles valores se encuentran codificados en la tabla TELCOLBO.txt de la Ayuda XML 
					- Tablas de códigos (códigos 70 a 77).</xsd:documentation>
				</xsd:annotation>
				<xsd:simpleType>
					<xsd:restriction base="xsd:string">
						<xsd:minLength value="2"/>
						<xsd:maxLength value="3"/>
						<xsd:pattern value="([0-9])+"/>
					</xsd:restriction>
				</xsd:simpleType>
			</xsd:element>
			<xsd:element name="CODIGO_COLECTIVO_BONIF" minOccurs="0">
				<xsd:annotation>
					<xsd:documentation xml:lang="es">Código del colectivo de bonificación. 
					Obligatorio para los transformaciones de códigos 109, 209 y 309 iniciadas a partir del 18/06/2010.  
					Sus posibles valores se encuentran codificados en la tabla TELCOLBO.txt de la Ayuda XML - Tablas de códigos.</xsd:documentation>
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
	 * @param contrataParams
	 * @return
	 */
	private DATOSBONIFICACIONTYPE createDatosBonificacion(ContrataParams contrataParams) {
		// TODO
		DATOSBONIFICACIONTYPE datos = factory.createDATOSBONIFICACIONTYPE();
		datos.setCODIGOCOLECTIVOBONIF("00");
		datos.setACOGIDOMATERNIDADEXCEDENCIA(null);
		datos.setCOLECTIVODISCAPACITADOS(null);
		return datos;
	}
	
	/**
	 * <xsd:complexType name="DATOS_CONTRATOTIEMPOPARCIALTYPE">
		<xsd:annotation>
			<xsd:documentation xml:lang="es">Datos de las transformaciones a tiempo parcial. Distribución de la jornada de trabajo.</xsd:documentation>
		</xsd:annotation>
		<xsd:sequence>
			<xsd:element name="TIPO_JORNADA">
				<xsd:annotation>
					<xsd:documentation xml:lang="es">Período de tiempo. 
					Sus posibles valores se encuentran codificados en la tabla TEQPTIEM.txt de la Ayuda XML 
					- Ultima versión - Tablas de códigos. En el caso de  transformaciones a fijos discontinuos (309, 339 y 389) 
					el tipo de jornada será siempre ANUAL (A). </xsd:documentation>
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
					Obligatorias para todas las transformaciones a tiempo parcial menos para las transformaciones a fijos discontinuos (309, 339 y 389) 
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
					<xsd:documentation xml:lang="es">Número de horas por convenio. Formato: HHHHMM (Horas(4)Minutos(2)). </xsd:documentation>
				</xsd:annotation>
				<xsd:simpleType>
					<xsd:restriction base="xsd:string">
						<xsd:pattern value="\d{6}"/>
					</xsd:restriction>
				</xsd:simpleType>
			</xsd:element>
			<xsd:element name="ACTIVIDAD_SIN_FECHACIERTA" minOccurs="0">
				<xsd:annotation>
					<xsd:documentation xml:lang="es">Indicador de si el período de actividad no tiene una fecha concreta. 
					Obligatorio para las transformaciones a fijos discontinuos (309, 339 y 389) si no vienen especificadas las HORAS_JORNADA. 
					El único valor posible que puede tomar es "S" (SI) para indicar que es una actividad sin fecha conocida. </xsd:documentation>
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
					Obligatorio para los transformaciones de código 209, 239 ó 289 iniciadas a partir del 01/07/2006. 
					Los posibles valores que puede tomar son "S" (SI) ó "N" (NO) para indicar si  la transformación a tiempo parcial corresponde 
					a la realización de trabajos fijos discontinuos o periódicos que se repiten en fechas ciertas dentro del volumen normal 
					de la actividad de la empresa. </xsd:documentation>
				</xsd:annotation>
				<xsd:simpleType>
					<xsd:restriction base="xsd:string">
						<xsd:pattern value="[SN\s]"/>
					</xsd:restriction>
				</xsd:simpleType>
			</xsd:element>
		</xsd:sequence>
	</xsd:complexType>
	 * @param params
	 * @return
	 */
	private DATOSCONTRATOTIEMPOPARCIALTYPE createDatosContratoTiempoParcial(ContrataParams params) {
		// TODO
		DATOSCONTRATOTIEMPOPARCIALTYPE datos = factory.createDATOSCONTRATOTIEMPOPARCIALTYPE();		
		datos.setTIPOJORNADA(params.getTipoJornada().getValue());
		String duracionconvenio = (params.getHorasConvenio()==null?"":completeLength(params.getHorasConvenio(), 4, "0", false))+(params.getMinutosConvenio()==null?"":completeLength(params.getMinutosConvenio(), 2, "0", false));
		String duracionjornada = (params.getHorasJornada()==null?"":completeLength(params.getHorasJornada(), 4, "0", false))+(params.getMinutosJornada()==null?"":completeLength(params.getMinutosJornada(), 2, "0", false));
	    datos.setHORASJORNADA(duracionjornada.isEmpty()?null:completeLength(duracionjornada, 6, "0", false));
	    datos.setHORASCONVENIO(duracionconvenio.isEmpty()?null:completeLength(duracionconvenio, 6, "0", false));
	    datos.setACTIVIDADSINFECHACIERTA(params.getActividadSinFechaCierta());
	    datos.setFIJODISCONTINUOPERIODICO(params.getFijoDiscontinuoPeriodico()?"S":"N");
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

