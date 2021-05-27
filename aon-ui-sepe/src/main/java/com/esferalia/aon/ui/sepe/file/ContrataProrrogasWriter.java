package com.esferalia.aon.ui.sepe.file;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.person.Person;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.enumeration.DocumentType;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.file.payroll.contrata.ContrataProrrogaParams;
import com.esferalia.aon.file.payroll.contrata.IContrataParams;
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.payroll.ContractInfo;
import com.esferalia.aon.payroll.ContractInfo.ContractVariable;
import com.esferalia.aon.payroll.EnterpriseCCC;
import com.esferalia.aon.payroll.enumeration.SSRegimeType;
import com.esferalia.aon.payroll.enumeration.contrata.TCHRGCOT;
import com.esferalia.aon.sepe.api.contrata.prorrogas.CIFNIFTYPE;
import com.esferalia.aon.sepe.api.contrata.prorrogas.DATOSADICIONALESPRORROGATYPE;
import com.esferalia.aon.sepe.api.contrata.prorrogas.DATOSCONTRATOTYPE;
import com.esferalia.aon.sepe.api.contrata.prorrogas.DATOSEMPRESATYPE;
import com.esferalia.aon.sepe.api.contrata.prorrogas.DATOSGENERALESPRORROGATYPE;
import com.esferalia.aon.sepe.api.contrata.prorrogas.DATOSUSOLIBREEMPRESATYPE;
import com.esferalia.aon.sepe.api.contrata.prorrogas.ObjectFactory;
import com.esferalia.aon.sepe.api.contrata.prorrogas.PRORROGATIPOTYPE;

public class ContrataProrrogasWriter implements IContrataWriter {
	
	final String CONTRATA_PRORROGAS_MODEL_PATH = "com.esferalia.aon.sepe.api.contrata.prorrogas";
	
	private Contract contract;

	private ObjectFactory factory = new ObjectFactory();
	
	
	public ContrataProrrogasWriter(Contract contract) {
		this.contract = contract;
	}

	public ObjectFactory getFactory(){
		return factory;
	}
	
	@Override
	public Contract getContract(){
		return contract;
	}
	
	@Override
	public File createFile(IContrataParams params) throws ManagerBeanException, IOException {
		// TODO 
		return null;
	}
	
	public PRORROGATIPOTYPE createProrroga(PRORROGATIPOTYPE type2, ContrataProrrogaParams params) throws ManagerBeanException{
		PRORROGATIPOTYPE type = (PRORROGATIPOTYPE) type2;
		type.setDATOSEMPRESA(createDatosEmpresa(params));
		type.setDATOSCONTRATO(createDatosContrato(params));
		type.setDATOSGENERALESPRORROGA(createDatosGeneralesProrroga(params));
		type.setDATOSADICIONALESPRORROGA(createDatosAdicionalesProrroga(params));
		type.setDATOSUSOLIBREEMPRESA(createDatosUsoLibreEmpresa(params));
		return type;
	}
	
	/**
	<xsd:complexType name="DATOS_EMPRESATYPE">
		<xsd:annotation>
			<xsd:documentation xml:lang="es">Datos de la empresa que prorroga</xsd:documentation>
		</xsd:annotation>
		<xsd:sequence>
			<xsd:annotation>
				<xsd:documentation xml:lang="es">CIF/ NIF de la empresa que prorroga</xsd:documentation>
			</xsd:annotation>
			<xsd:element name="CIF_NIF_EMPRESA" type="CIFNIFTYPE"/>
			<xsd:element name="CCC">
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
	 * 
	 */
	private DATOSEMPRESATYPE createDatosEmpresa(ContrataProrrogaParams params) throws ManagerBeanException {
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
			datos.setCCC(quoteRegime+ccc.getCcc());
		}
		return datos;
	}
	
	/**
	<xsd:complexType name="DATOS_CONTRATOTYPE">
		<xsd:annotation>
			<xsd:documentation xml:lang="es">Datos del contrato prorrogado</xsd:documentation>
		</xsd:annotation>
		<xsd:sequence>
			<xsd:element name="CLAVE_CONTRATO" type="CLAVECONTRATOTYPE" minOccurs="0">
				<xsd:annotation>
					<xsd:documentation xml:lang="es">Identificador del contrato a prorrogar. 
					Este campo configura una de las 2 opciones por las que se puede identificar el contrato que se va a prorrogar.  
					En el caso de elegir esta opción, el elemento es obligatorio, y no se deben rellenar el resto de campos de DATOS_CONTRATO.</xsd:documentation>
				</xsd:annotation>
			</xsd:element>
			<xsd:element name="IDENTIFICADORPFISICA" minOccurs="0">
				<xsd:annotation>
					<xsd:documentation xml:lang="es">Identificador de la persona física del contrato a prorrogar.  
					Este campo junto con la FECHA_INICIO_CTO configura la segunda opción por las que se puede identificar el contrato que se va a prorrogar.  
					En el caso de elegir esta opción, el elemento es obligatorio, y no se debe rellenar el campo CLAVE_CONTRATO.  
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
			<xsd:element name="FECHA_INICIO_CTO" type="FECHATYPE" minOccurs="0">
				<xsd:annotation>
					<xsd:documentation xml:lang="es">Fecha de inicio del contrato a prorrogar.  
					Este campo junto con el IDENTIFICADORPFISICA configura la segunda opción por las que se puede identificar el contrato que se va a prorrogar.  
					En el caso de elegir esta opción, el elemento es obligatorio, y no se debe rellenar el campo CLAVE_CONTRATO.</xsd:documentation>
				</xsd:annotation>
			</xsd:element>
		</xsd:sequence>
	</xsd:complexType>
	 * 
	 */
	private DATOSCONTRATOTYPE createDatosContrato(ContrataProrrogaParams params) {
		DATOSCONTRATOTYPE datos = factory.createDATOSCONTRATOTYPE();
		
		if(StringUtils.isNotBlank(params.getClaveContrato())){
			datos.setCLAVECONTRATO(params.getClaveContrato());
		} else {
			try {
				IManagerBean bean = BeanManager.getManagerBean(ContractInfo.class);
				Criteria criteria = new Criteria();
				criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_INFO_CONTRACT_ID), getContract().getId());
				criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_INFO_NAME), ContractVariable.SEPE_CONTRACT_ID.getValue());
				criteria.addOrder(bean.getFieldName(IEntityAlias.CONTRACT_INFO_START_DATE));
				List<ITransferObject> list = bean.getList(criteria);
				if(!list.isEmpty()){
					ContractInfo info = (ContractInfo) list.get(0);
					datos.setCLAVECONTRATO(info.getExpression().replaceAll("\"", ""));
				}
			} catch (ManagerBeanException e) {
				// nada
			}
			if(datos.getCLAVECONTRATO()==null){
				Person person = getContract().getPerson();
				if(StringUtils.isEmpty(person.getRegistry().getDocument())){
					AonUtil.addErrorMessage("El trabajador no tiene definido el número de documento..");
				} else {
					if(person.getRegistry().getDocumentType()==DocumentType.NIF){
						datos.setIDENTIFICADORPFISICA("D"+person.getRegistry().getDocument());
					} else if(person.getRegistry().getDocumentType()==DocumentType.NIE){
						datos.setIDENTIFICADORPFISICA("E"+person.getRegistry().getDocument());
					}
				}
				datos.setFECHAINICIOCTO(getFormatedDate(getContract().getStartDate()));
			}
		}
		
		return datos;
	}
	
	/**
	<xsd:complexType name="DATOS_GENERALESPRORROGATYPE">
		<xsd:annotation>
			<xsd:documentation xml:lang="es">Datos propios de la prórroga</xsd:documentation>
		</xsd:annotation>
		<xsd:sequence>
			<xsd:element name="FECHA_INICIO" type="FECHATYPE">
				<xsd:annotation>
					<xsd:documentation xml:lang="es">Fecha de inicio de la prórroga.</xsd:documentation>
				</xsd:annotation>
			</xsd:element>
			<xsd:element name="FECHA_FIN" type="FECHATYPE">
				<xsd:annotation>
					<xsd:documentation xml:lang="es">Fecha de término de la prórroga.</xsd:documentation>
				</xsd:annotation>
			</xsd:element>
			<xsd:element name="INDICADOR_CONV_COL" minOccurs="0">
				<xsd:annotation>
					<xsd:documentation xml:lang="es">Indicador de convenio colectivo.   Obligatorio para prorrogar : 
					- contratos de códigos 402 y 502 cuando su duración está entre 6 y 12 meses.   
					- contratos de código 421 cuando su duración está entre 24 y 36 meses.  
					- contratos de código 401, 501, 450 con modalidad 401 y 550 con modalidad 501 iniciados a partir del 18/06/2010 
							cuando su duración está entre 36 y 48 meses.    
					Refleja la existencia ("S") o no existencia ("N") de un convenio colectivo que autorice estas duraciones.</xsd:documentation>
				</xsd:annotation>
				<xsd:simpleType>
					<xsd:restriction base="xsd:string">
						<xsd:pattern value="[SN\s]"/>
					</xsd:restriction>
				</xsd:simpleType>
			</xsd:element>
			<xsd:element name="INDICADOR_DISCONTINUIDAD" minOccurs="0">
				<xsd:annotation>
					<xsd:documentation xml:lang="es">Indicador de discontinuidad.  
					El único valor admitido "I" (ILT) indicará que se ha producido una Invalidez transitoria que justifica la discontinuidad existente 
					entre la fecha de inicio de la prórroga que se está comunicando y la fecha de término de la prórroga/contrato anterior.</xsd:documentation>
				</xsd:annotation>
				<xsd:simpleType>
					<xsd:restriction base="xsd:string">
						<xsd:pattern value="[I\s]"/>
					</xsd:restriction>
				</xsd:simpleType>
			</xsd:element>
			<xsd:element name="IND_EMPRESA_AAPP_UNIVERSIDAD" minOccurs="0">
				<xsd:annotation>
					<xsd:documentation xml:lang="es">Indicador de empresa. 
					Obligatorio (S/N) para los códigos 401, 501, 450 con modalidad 401 y 550 con modalidad 501 iniciados a partir de 19/09/2010 
					cuando no cumpla con la duración válida y tampoco esté acogido a convenio colectivo que justifique esta duración. 
					Indica si la prórroga se realiza (S) por la Administración Pública , Organismo Público vinculado o Universidad , 
					o no es una empresa de estos tipos (N). </xsd:documentation>
				</xsd:annotation>
				<xsd:simpleType>
					<xsd:restriction base="xsd:string">
						<xsd:pattern value="[SN\s]"/>
					</xsd:restriction>
				</xsd:simpleType>
			</xsd:element>
			<xsd:element name="IND_PERIODO_AUTORIZA_DURACION" minOccurs="0">
				<xsd:annotation>
					<xsd:documentation xml:lang="es">Indicador de autorización de la duración. 
					Obligatorio (S/N) para los códigos 420, 421, 450 con modalidad 420, 450 con modalidad 421, 520 y 550 con modalidad 520 iniciados 
					a partir de 19/09/2010 cuando no cumpla con la duración válida y tampoco esté acogido a convenio colectivo que justifique esta duración. 
					Indica si han existido períodos de incapacidad , maternidad, adopción , riesgo o paternidad que justifiquen la superación 
					de la duración máxima (S) , o no (N). </xsd:documentation>
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
	 */
	private DATOSGENERALESPRORROGATYPE createDatosGeneralesProrroga(ContrataProrrogaParams params) {
		DATOSGENERALESPRORROGATYPE datos = factory.createDATOSGENERALESPRORROGATYPE();
		datos.setFECHAINICIO(getFormatedDate(params.getFechaInicio()));
		datos.setFECHAFIN(getFormatedDate(params.getFechaFin()));
		if(params.getIndicadorConvCol()!=null){
			datos.setINDICADORCONVCOL(params.getIndicadorConvCol()?"S":"N");
		}
		if(params.getIndicadorDiscontinuidad()!=null){
			datos.setINDICADORDISCONTINUIDAD(params.getIndicadorDiscontinuidad()?"I":null);
		}
		if(params.getIndEmpresaAappUniversidad()!=null){
			datos.setINDEMPRESAAAPPUNIVERSIDAD(params.getIndEmpresaAappUniversidad()?"S":"N");
		}
		if(params.getIndPeriodoAutorizaDuracion()!=null){
			datos.setINDPERIODOAUTORIZADURACION(params.getIndPeriodoAutorizaDuracion()?"S":"N");
		}
	    return datos;
	}
	
	/**
	<xsd:complexType name="DATOS_ADICIONALESPRORROGATYPE">
		<xsd:annotation>
			<xsd:documentation xml:lang="es">Datos adicionales de la prórroga</xsd:documentation>
		</xsd:annotation>
		<xsd:sequence>
			<xsd:element name="HORAS_FORMACION" minOccurs="0">
				<xsd:annotation>
					<xsd:documentation xml:lang="es">Número de horas de formación. Formato: HHHHMM (Horas(4)Minutos(2)). 
					Opcionales para las prórrogas de contratos de código 421 (Formación), para el resto no deben ser cumplimentadas. 
					En el caso de no ser especificadas en las prórrogas de los contratos de Formación,  le son asignadas a la prórroga las que tuviera el contrato que está prorrogando. </xsd:documentation>
				</xsd:annotation>
				<xsd:simpleType>
					<xsd:restriction base="xsd:string">
						<xsd:pattern value="\d{6}"/>
					</xsd:restriction>
				</xsd:simpleType>
			</xsd:element>
			<xsd:element name="IND_DURAC_INFERIOR" minOccurs="0">
				<xsd:annotation>
					<xsd:documentation xml:lang="es">Indicador de duración inferior. Obligatorio para contratos de códigos 452 y 552 cuando 
					su duración está entre 6 y 12 meses.  Refleja si la duración inferior del contrato está aconsejada (S) ó no (N) por 
					los Servicios Sociales Públicos para el seguimiento del proceso de inserción. </xsd:documentation>
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
	 */
	private DATOSADICIONALESPRORROGATYPE createDatosAdicionalesProrroga(ContrataProrrogaParams params) {
		DATOSADICIONALESPRORROGATYPE datos = null;
		String duracionformacion = (params.getHorasFormacion()==null?"":completeLength(params.getHorasFormacion(), 4, "0", false))+(params.getMinutosFormacion()==null?"":completeLength(params.getMinutosFormacion(), 2, "0", false));
		if(StringUtils.isNotBlank(duracionformacion)){
			datos = factory.createDATOSADICIONALESPRORROGATYPE();
			datos.setHORASFORMACION(duracionformacion.isEmpty()?null:completeLength(duracionformacion, 6, "0", false));
		}
		if(params.getIndDuracInferior()!=null){
			datos = datos==null?factory.createDATOSADICIONALESPRORROGATYPE():datos;
			datos.setINDDURACINFERIOR(params.getIndDuracInferior()?"S":"N");
		}
		return datos;
	}
	
	/**
	<xsd:complexType name="DATOS_USOLIBRE_EMPRESATYPE">
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
	 * 
	 * @param params
	 * @return
	 */
	private DATOSUSOLIBREEMPRESATYPE createDatosUsoLibreEmpresa(ContrataProrrogaParams params) {
		if(StringUtils.isNotBlank(params.getUsoLibreEmpresa())){
			DATOSUSOLIBREEMPRESATYPE datos = factory.createDATOSUSOLIBREEMPRESATYPE();
			datos.setUSOLIBREEMPRESA(params.getUsoLibreEmpresa());
			return datos;
		}
		return null;
	}
	
	private CIFNIFTYPE createCifNif(String cif) {
		CIFNIFTYPE datos = factory.createCIFNIFTYPE();
		datos.setCIFNIF(cif);
		return datos;
	}
	
	
	/* ***************************************
	 * ***************************************
	 * AUXILIARES
	 * ***************************************
	 * ***************************************
	 */
	
	private String getFormatedDate(Date date){
		String pattern = "yyyyMMdd";
		if(date!=null){
			return DateFormatUtils.format(date, pattern);
		}
		return null;
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

