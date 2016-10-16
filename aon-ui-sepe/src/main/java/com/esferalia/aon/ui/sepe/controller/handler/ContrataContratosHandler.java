package com.esferalia.aon.ui.sepe.controller.handler;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.TreeSet;
import java.util.stream.Collectors;

import javax.faces.model.SelectItem;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import com.code.aon.AonVersion;
import com.code.aon.common.IAttachment;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.esferalia.aon.file.payroll.contrata.ContrataContratoParams;
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.payroll.ContractAttachment;
import com.esferalia.aon.payroll.ContractData;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.payroll.enumeration.ContractCode;
import com.esferalia.aon.payroll.enumeration.contrata.TBONVFOR;
import com.esferalia.aon.payroll.enumeration.contrata.TEJINDIS;
import com.esferalia.aon.payroll.enumeration.contrata.TEQPTIEM;
import com.esferalia.aon.ui.sepe.controller.ISepeConstants;
import com.esferalia.aon.ui.sepe.file.ContrataReader;
import com.esferalia.aon.ui.sepe.utils.SEPEUtils;


public class ContrataContratosHandler implements IContrataHandler, Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private Contract contract;
	
	private ContrataContratoParams params;

	private ContractCode contractCode;

	
	@Override
	public Contract getContract() {
		return contract;
	}
	
	@Override
	public ContrataContratoParams getParams() {
		if(params==null){
			params = new ContrataContratoParams();
		}
		return params;
	}

	@Override
	public ContractCode getContractCode() {
		return contractCode;
	}
	
	@Override
	public boolean isCommunicationAvailable(){
		if(getContractCode()!=null){
			return ArrayUtils.contains(ISepeConstants.AVAILABLE_CONTRACT_CODE_COMMUNICATION, getContractCode().getValue());
		}
		return false;
	}
	
	@Override
	public void initialize(Contract contract){
		SEPEUtils utils = SEPEUtils.getInstance();
		this.contract = contract;
		try {
			List<ITransferObject> list = utils.getContractData(contract, null, null, ContextVariable.TC2.getName(), false);
			list = list.stream().map(to -> ((ContractData)to))
					.filter(cd -> cd.getExpression().matches(".\\d\\d[^9]."))
					.sorted((cd1, cd2) -> cd1.getStartDate().compareTo(cd2.getStartDate()))
					.collect(Collectors.toList());
			if(list!=null && !list.isEmpty()){
				String code = ((ContractData)list.get(list.size()-1)).getExpression();
				code  = code.replaceAll("\"", "");
				this.contractCode = ContractCode.getContractCodeByValue( code );
			}
		} catch (ManagerBeanException e) {
			// do nothing ...
		}
	}
	
	@Override
	public void loadContrataData(IAttachment attach) throws ManagerBeanException, IOException{
		if(attach==null){
			attach = new ContractAttachment();
		} else {
			ContrataReader reader = new ContrataReader();
			this.params = (ContrataContratoParams) reader.readFile( new ByteArrayInputStream(attach.getData()) );
		}
		afterDataLoading();
	}
	
	private void afterDataLoading() {
		if(getShowCausaInterinidadPanel()){
			getParams().setInterimData(true);
		}
	}

	public List<SelectItem> getQualificationsNames(){
		String BASE_NAME = "com.esferalia.aon.payroll.i18n.qualifications";
		ResourceBundle bundle = ResourceBundle.getBundle(BASE_NAME);
		List<SelectItem> qualifications = new LinkedList<SelectItem>();
		if(getParams().getNivelFormativo()!=null){
			TreeSet<String> tree = new TreeSet<String>(bundle.keySet());
			for(String key: tree){
				if(key.startsWith(getParams().getNivelFormativo().getCode())){
					String name = bundle.getString(key);
					SelectItem item = new SelectItem(key, name);
					qualifications.add(item);
				}
			}
		}
		return qualifications;
	}

	////////////////////////////////////////
	// fields otros datos contrato
	////////////////////////////////////////
	
	/**
	 * <xsd:element name="NIVEL_FORMATIVO">
			<xsd:annotation>
				<xsd:documentation xml:lang="es">Código del nivel formativo.  
				Sus posibles valores se encuentran codificados en la tabla TBONVFOR.txt de la Ayuda XML - Ultima versión - Tablas de códigos.</xsd:documentation>
			</xsd:annotation>
			<xsd:simpleType>
				<xsd:restriction base="xsd:string">
					<xsd:pattern value="\d{2}"/>
				</xsd:restriction>
			</xsd:simpleType>
		</xsd:element>
	 */
	public Boolean getShowNivelFormativo() {
		return isContratoType();
	}
	
	/**
	 * <xsd:element name="TIPO_JORNADA">
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
	 */
	public Boolean getShowTipoJornada() {
		if( getContractCode()==ContractCode.C300 || getContractCode()==ContractCode.C330 || getContractCode()==ContractCode.C350){
			getParams().setTipoJornada(TEQPTIEM.TEQPTIEM_A);
		}
		return 
				// contratos
				getContractCode() == ContractCode.C200
				|| getContractCode() == ContractCode.C230
				|| getContractCode() == ContractCode.C250
				|| getContractCode() == ContractCode.C300
				|| getContractCode() == ContractCode.C330
				|| getContractCode() == ContractCode.C350
				|| getContractCode() == ContractCode.C421
				|| getContractCode() == ContractCode.C450
				|| getContractCode() == ContractCode.C501
				|| getContractCode() == ContractCode.C502
				|| getContractCode() == ContractCode.C503
				|| getContractCode() == ContractCode.C510
				|| getContractCode() == ContractCode.C520
				|| getContractCode() == ContractCode.C530
				|| getContractCode() == ContractCode.C540
				|| getContractCode() == ContractCode.C541
				|| getContractCode() == ContractCode.C550
				|| getContractCode() == ContractCode.C552
				// transformaciones
				|| getContractCode() == ContractCode.C389
				|| getContractCode() == ContractCode.C339
				|| getContractCode() == ContractCode.C309
				|| getContractCode() == ContractCode.C289
				|| getContractCode() == ContractCode.C239
				|| getContractCode() == ContractCode.C209;
	}
	public boolean isAvailableTipoJornada() {
		return getContractCode()!=ContractCode.C300 && getContractCode()!=ContractCode.C330 && getContractCode()!=ContractCode.C350;
	}

	/**
	 * <xsd:element name="HORAS_JORNADA" minOccurs="0">
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
	 * @return
	 */
	public Boolean getShowHorasMinutosJornada() {
		if( isPartialTimeContract(getContractCode()) ){
			return ( getContractCode() != ContractCode.C300 && getContractCode() != ContractCode.C330 && getContractCode() != ContractCode.C350 )
					|| ( getParams().getActividadSinFechaCierta()==null || !getParams().getActividadSinFechaCierta().equals("S") );
		}
		return false;
	}

	/**
	 * <xsd:element name="HORAS_CONVENIO" minOccurs="0">
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
	 * @return
	 */
	public Boolean getShowHorasMinutosConvenio() {
		return getContractCode()==ContractCode.C200 || getContractCode()==ContractCode.C230
				|| getContractCode()==ContractCode.C250 || getContractCode()==ContractCode.C300
				|| getContractCode()==ContractCode.C330 || getContractCode()==ContractCode.C350;
	}

	/**
	 * <xsd:element name="HORAS_FORMACION" minOccurs="0">
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
	 * @return
	 */
	public Boolean getShowHorasMinutosFormacion() {
		return getContractCode()==ContractCode.C421 ;
	}

	/**
	 * <xsd:element name="INDIC_FORMACION_TEORICA" minOccurs="0">
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
	 * @return
	 */
	public Boolean getShowIndicFormacionTeorica() {
		return getContractCode()==ContractCode.C421 
				&& StringUtils.isBlank(getParams().getHorasFormacion()) && StringUtils.isBlank(getParams().getMinutosFormacion());
	}
	
	/**
	 * <xsd:element name="COLECTIVO_EDAD" minOccurs="0">
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
	 * @return
	 */
	public Boolean getShowColectivoEdad() {
		if(getContract().getPerson().getAge()!=null){
			return getContractCode()==ContractCode.C421 && getContract().getPerson().getAge()>=21;
		}
		return false;
	}
	
	/**
	 * <xsd:element name="PORCENTAJE_JUBILACION_PARCIAL" minOccurs="0">
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
	 * @return
	 */
	public Boolean getShowPorcentajeJubilacionParcial() {
		return getContractCode()==ContractCode.C540;
	}
	
	/**
	 * <xsd:element name="FIJODISCONTINUO_PERIODICO" minOccurs="0">
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
	 * @return
	 */
	public Boolean getShowFijoDiscontinuoPeriodico() {
		Calendar cal = Calendar.getInstance();
		cal.set(2006, 6, 1);
		return getContract().getStartDate().after(cal.getTime()) 
				&& ( getContractCode()==ContractCode.C200 || getContractCode()==ContractCode.C230
				|| getContractCode()==ContractCode.C250 );
	}
	
	/**
	<xsd:element name="TITULACION_ACADEMICA">
		<xsd:annotation>
			<xsd:documentation xml:lang="es">Código de titulación académica. 
			No es necesario especificar su valor en los contratos de prácticas, 
			cuando el Nivel Formativo del trabajador sea 60. 
			Sus posibles valores se encuentran codificados en la tabla THITIACA.txt 
			de la Ayuda XML - Ultima versión - Tablas de códigos.  
			La codificación tabulada de este elemento corresponde a códigos de 12 posiciones, 
			hasta ahora se debían enviar códigos de 4 posiciones con 8 ceros por la izquierda hasta completar las 12, 
			a partir de ahora los códigos ya son de 12 posiciones por lo que no habrá que rellenar con ceros.</xsd:documentation>
		</xsd:annotation>
		<xsd:simpleType>
			<xsd:restriction base="xsd:string">
				<xsd:pattern value="\d{12}"/>
			</xsd:restriction>
		</xsd:simpleType>
	</xsd:element>
	 * @return
	 */
	public Boolean getShowTitulacionAcademica() {
		return getContractCode()==ContractCode.C420 || getContractCode()==ContractCode.C520;
	}
	public boolean isTitulacionAcademicaRequired() {
		return getParams().getNivelFormativo()!=null && getParams().getNivelFormativo()!=TBONVFOR.TBONVFOR_60;
	}
	
	/**
	<xsd:element name="IND_CERTIF_PROFESIONALIDAD" minOccurs="0">
		<xsd:annotation>
			<xsd:documentation xml:lang="es">
			Obligatorio (S/N) para los códigos 420, 520, 450 con modalidad 420 
			y 550 con modalidad 520 iniciados a partir del 18/06/2010 
			cuando el Nivel Formativo del trabajador no sea uno de los siguientes códigos: 33, 51, 54, 55, 59 o 60. 
			Indica si el trabajador tiene o no un certificado de profesionalidad. </xsd:documentation>
		</xsd:annotation>
		<xsd:simpleType>
			<xsd:restriction base="xsd:string">
				<xsd:pattern value="[SN\s]"/>
			</xsd:restriction>
		</xsd:simpleType>
	</xsd:element>
	 * @return
	 */
	public Boolean getShowCertificadoProfesionalidad() {
		Calendar cal = Calendar.getInstance();
		cal.set(2010, 6, 18);
		return getContract().getStartDate().after(cal.getTime()) 
				&& ( getContractCode()==ContractCode.C420 || getContractCode()==ContractCode.C520
				|| getContractCode()==ContractCode.C450 || getContractCode()==ContractCode.C550 );
	}
	public boolean isCertificadoProfesionalidadRequired() {
		return getParams().getNivelFormativo() != null
				&& getParams().getNivelFormativo() != TBONVFOR.TBONVFOR_33
				&& getParams().getNivelFormativo() != TBONVFOR.TBONVFOR_51
				&& getParams().getNivelFormativo() != TBONVFOR.TBONVFOR_54
				&& getParams().getNivelFormativo() != TBONVFOR.TBONVFOR_55
				&& getParams().getNivelFormativo() != TBONVFOR.TBONVFOR_59
				&& getParams().getNivelFormativo() != TBONVFOR.TBONVFOR_60;
	}
	
	public Boolean isTranscormacionType() {
		return getContractCode() == ContractCode.C109
				|| getContractCode() == ContractCode.C139
				|| getContractCode() == ContractCode.C189
				|| getContractCode() == ContractCode.C209
				|| getContractCode() == ContractCode.C239
				|| getContractCode() == ContractCode.C289
				|| getContractCode() == ContractCode.C309
//				|| getContractCode() == ContractCode.C339
				|| getContractCode() == ContractCode.C389;
	}
	public Boolean isContratoType() {
		return getContractCode() == ContractCode.C100
				|| getContractCode() == ContractCode.C130
				|| getContractCode() == ContractCode.C150
				|| getContractCode() == ContractCode.C200
				|| getContractCode() == ContractCode.C230
				|| getContractCode() == ContractCode.C250
				|| getContractCode() == ContractCode.C300
				|| getContractCode() == ContractCode.C330
				|| getContractCode() == ContractCode.C350
				|| getContractCode() == ContractCode.C401
				|| getContractCode() == ContractCode.C402
				|| getContractCode() == ContractCode.C403
				|| getContractCode() == ContractCode.C410
				|| getContractCode() == ContractCode.C420
				|| getContractCode() == ContractCode.C421
				|| getContractCode() == ContractCode.C430
				|| getContractCode() == ContractCode.C441
				|| getContractCode() == ContractCode.C450
				|| getContractCode() == ContractCode.C452
				|| getContractCode() == ContractCode.C501
				|| getContractCode() == ContractCode.C502
				|| getContractCode() == ContractCode.C503
				|| getContractCode() == ContractCode.C510
				|| getContractCode() == ContractCode.C520
				|| getContractCode() == ContractCode.C530
				|| getContractCode() == ContractCode.C540
				|| getContractCode() == ContractCode.C541
				|| getContractCode() == ContractCode.C550
				|| getContractCode() == ContractCode.C552
				|| getContractCode() == ContractCode.C970
				|| getContractCode() == ContractCode.C980
				|| getContractCode() == ContractCode.C990;
	}
	
	////////////////////////////////////////
	// checks datos especificos contrato
	////////////////////////////////////////
	public Boolean getShowDatosProgramaEmpleoPanel(){
		return getContractCode()==ContractCode.C401 || getContractCode()==ContractCode.C402 || getContractCode()==ContractCode.C403
				|| getContractCode()==ContractCode.C410 || getContractCode()==ContractCode.C420 || getContractCode()==ContractCode.C421
				|| getContractCode()==ContractCode.C430 || getContractCode()==ContractCode.C450 || getContractCode()==ContractCode.C452
				|| getContractCode()==ContractCode.C501 || getContractCode()==ContractCode.C502 || getContractCode()==ContractCode.C503
				|| getContractCode()==ContractCode.C510 || getContractCode()==ContractCode.C520 || getContractCode()==ContractCode.C530
				|| getContractCode()==ContractCode.C550 || getContractCode()==ContractCode.C552 || getContractCode()==ContractCode.C970
				|| getContractCode()==ContractCode.C990;
	}
	public Boolean getShowEttPanel(){
		return getContractCode()==ContractCode.C100 || getContractCode()==ContractCode.C130 || getContractCode()==ContractCode.C150 
				|| getContractCode()==ContractCode.C200 || getContractCode()==ContractCode.C230 || getContractCode()==ContractCode.C250
				|| getContractCode()==ContractCode.C300 || getContractCode()==ContractCode.C330 || getContractCode()==ContractCode.C350
				|| getContractCode()==ContractCode.C401 || getContractCode()==ContractCode.C402 || getContractCode()==ContractCode.C403
				|| getContractCode()==ContractCode.C410 || getContractCode()==ContractCode.C420 || getContractCode()==ContractCode.C421
				|| getContractCode()==ContractCode.C430 || getContractCode()==ContractCode.C441 || getContractCode()==ContractCode.C450
				|| getContractCode()==ContractCode.C452 || getContractCode()==ContractCode.C501 || getContractCode()==ContractCode.C502
				|| getContractCode()==ContractCode.C503 || getContractCode()==ContractCode.C510 || getContractCode()==ContractCode.C520
				|| getContractCode()==ContractCode.C530 || getContractCode()==ContractCode.C540 || getContractCode()==ContractCode.C541
				|| getContractCode()==ContractCode.C550 || getContractCode()==ContractCode.C552 || getContractCode()==ContractCode.C970
				|| getContractCode()==ContractCode.C980 || getContractCode()==ContractCode.C990;
	}
	public Boolean getShowContratoRelevoPanel(){
		return getContractCode()==ContractCode.C100 || getContractCode()==ContractCode.C130 || getContractCode()==ContractCode.C150 
				|| getContractCode()==ContractCode.C200 || getContractCode()==ContractCode.C230 || getContractCode()==ContractCode.C250
				|| getContractCode()==ContractCode.C300 || getContractCode()==ContractCode.C330 || getContractCode()==ContractCode.C350
				|| getContractCode()==ContractCode.C441 || getContractCode()==ContractCode.C541;
	}
	public Boolean getShowEscuelasTallerPanel(){
		return getContractCode()==ContractCode.C100 || getContractCode()==ContractCode.C130 || getContractCode()==ContractCode.C150 
				|| getContractCode()==ContractCode.C200 || getContractCode()==ContractCode.C230 || getContractCode()==ContractCode.C250
				|| getContractCode()==ContractCode.C300 || getContractCode()==ContractCode.C330 || getContractCode()==ContractCode.C350
				|| getContractCode()==ContractCode.C401 || getContractCode()==ContractCode.C410 || getContractCode()==ContractCode.C420
				|| getContractCode()==ContractCode.C421 || getContractCode()==ContractCode.C430 || getContractCode()==ContractCode.C441
				|| getContractCode()==ContractCode.C450 || getContractCode()==ContractCode.C452 || getContractCode()==ContractCode.C501
				|| getContractCode()==ContractCode.C502 || getContractCode()==ContractCode.C510 || getContractCode()==ContractCode.C520
				|| getContractCode()==ContractCode.C530 || getContractCode()==ContractCode.C540 || getContractCode()==ContractCode.C541
				|| getContractCode()==ContractCode.C550 || getContractCode()==ContractCode.C970 || getContractCode()==ContractCode.C980
				|| getContractCode()==ContractCode.C990;
	}
	public Boolean getShowDatosOfertaTrabajoPanel(){
		// Datos comunes a todos los contratos
		return true;
	}
	public Boolean getShowDiscapacidadPanel(){
		// Datos comunes a todos los contratos
		return true;
	}
	public Boolean getShowApoyoEmprendedoresPanel(){
		// TODO
		return getContractCode()==ContractCode.C150 || getContractCode()==ContractCode.C430 || getContractCode()==ContractCode.C530;
	}
	public Boolean getShowMayor52Panel(){
		// Datos comunes a todos los contratos
		return true;
	}
	public Boolean getShowAnexoGestionColectivaPanel(){
		// TODO
		return true;
	}
	public Boolean getShowCampainasPanel(){
		// Datos comunes a todos los contratos
		return true;
	}
	public Boolean getShowCausaInterinidadPanel(){
		return getContractCode()==ContractCode.C410 || getContractCode()==ContractCode.C450 || getContractCode()==ContractCode.C510
				|| getContractCode()==ContractCode.C550;
	}
	public Boolean getShowInvestigacionPanel(){
		return getContractCode()==ContractCode.C401 || getContractCode()==ContractCode.C420 || getContractCode()==ContractCode.C501;
	}
	public Boolean getShowReduccionCuotasPanel(){
		return getContractCode()==ContractCode.C200 || getContractCode()==ContractCode.C230 || getContractCode()==ContractCode.C250
				|| getContractCode()==ContractCode.C300 || getContractCode()==ContractCode.C330 || getContractCode()==ContractCode.C350
				|| getContractCode()==ContractCode.C421 || getContractCode()==ContractCode.C450 || getContractCode()==ContractCode.C501
				|| getContractCode()==ContractCode.C502 || getContractCode()==ContractCode.C520 || getContractCode()==ContractCode.C530
				|| getContractCode()==ContractCode.C550;
	}
	public Boolean getShowMedidasFomentoPanel(){
		return getContractCode()==ContractCode.C100 || getContractCode()==ContractCode.C130
				|| getContractCode()==ContractCode.C150 || getContractCode()==ContractCode.C200
				|| getContractCode()==ContractCode.C230 || getContractCode()==ContractCode.C250
				|| getContractCode()==ContractCode.C300 || getContractCode()==ContractCode.C330
				|| getContractCode()==ContractCode.C350;
	}
	public Boolean getShowContratoExtranjeroPanel(){
		// TODO: create data input panel in view
		return getContractCode()==ContractCode.C100 || getContractCode()==ContractCode.C200
				|| getContractCode()==ContractCode.C401 || getContractCode()==ContractCode.C402
				|| getContractCode()==ContractCode.C501 || getContractCode()==ContractCode.C502;
	}
	public Boolean getShowContratoEmprendedoresPanel(){
		// TODO: create data input panel in view
		return getContractCode()==ContractCode.C100 || getContractCode()==ContractCode.C150
				|| getContractCode()==ContractCode.C300 || getContractCode()==ContractCode.C350;
	}
	public Boolean getShowDatosBonificacionPanel(){
		// TODO: create data input panel in view
		return getContractCode()==ContractCode.C130 || getContractCode()==ContractCode.C150
				|| getContractCode()==ContractCode.C230 || getContractCode()==ContractCode.C250
				|| getContractCode()==ContractCode.C330 || getContractCode()==ContractCode.C350
				|| getContractCode()==ContractCode.C430 || getContractCode()==ContractCode.C450
				|| getContractCode()==ContractCode.C452 || getContractCode()==ContractCode.C530
				|| getContractCode()==ContractCode.C550 || getContractCode()==ContractCode.C552;
	}
	public Boolean getShowEmpresaInsercionPanel(){
		// TODO: create data input panel in view
		return getContractCode()==ContractCode.C150 || getContractCode()==ContractCode.C250
				|| getContractCode()==ContractCode.C350 || getContractCode()==ContractCode.C450
				|| getContractCode()==ContractCode.C452 || getContractCode()==ContractCode.C550
				|| getContractCode()==ContractCode.C552;
	}
	public Boolean getShowDatosCopiaBasicaPanel(){
		// TODO: create data input panel in view
		return getContractCode()==ContractCode.C402 || getContractCode()==ContractCode.C450
				|| getContractCode()==ContractCode.C452 || getContractCode()==ContractCode.C550
				|| getContractCode()==ContractCode.C552 || getContractCode()==ContractCode.C990;
	}
	public Boolean getShowContratoInsercionPanel(){
		// TODO: create data input panel in view
		return getContractCode()==ContractCode.C403 || getContractCode()==ContractCode.C502;
	}
	public Boolean getShowDatosContratoPracticasPanel(){
		// TODO: create data input panel in view
		return getContractCode()==ContractCode.C420 || getContractCode()==ContractCode.C450
				|| getContractCode()==ContractCode.C520 || getContractCode()==ContractCode.C550;
	}
	public Boolean getShowDatosExclusionSocialPanel(){
		// TODO: create data input panel in view
		return getContractCode()==ContractCode.C450 || getContractCode()==ContractCode.C550;
	}
	
	private boolean isPartialTimeContract(ContractCode contractCode) {
		return getContractCode()==ContractCode.C200 || getContractCode()==ContractCode.C230
				|| getContractCode()==ContractCode.C250 || getContractCode()==ContractCode.C300
				|| getContractCode()==ContractCode.C330 || getContractCode()==ContractCode.C350
				|| getContractCode()==ContractCode.C421 || getContractCode()==ContractCode.C450
				|| getContractCode()==ContractCode.C501 || getContractCode()==ContractCode.C502
				|| getContractCode()==ContractCode.C503 || getContractCode()==ContractCode.C510
				|| getContractCode()==ContractCode.C520 || getContractCode()==ContractCode.C530
				|| getContractCode()==ContractCode.C540 || getContractCode()==ContractCode.C541
				|| getContractCode()==ContractCode.C550 || getContractCode()==ContractCode.C552;
	}
	
	////////////////////////////////////////
	// checks datos especificos contrato
	////////////////////////////////////////
	public Boolean getShowColectivoBonificacion(){
		return getParams().getIndDiscapacidad() != TEJINDIS.TEJINDIS_C;
	}
	/**
	 * <xsd:documentation xml:lang="es">
		Indicador de empleador autónomo.   
		Obligatorio para contratos de código 150, 250 y 350 iniciados antes del 01/07/2006.  
		Refleja si el empleador que contrata es autónomo(1) o no lo es(2).
		</xsd:documentation>
	 * @return
	 */
	public Boolean getShowIndEmpleadAutonomo(){
		return getContractCode()==ContractCode.C150 || getContractCode()==ContractCode.C250 || getContractCode()==ContractCode.C350;
	}
	
}
