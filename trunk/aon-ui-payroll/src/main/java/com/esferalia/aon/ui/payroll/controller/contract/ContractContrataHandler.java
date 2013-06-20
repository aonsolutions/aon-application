package com.esferalia.aon.ui.payroll.controller.contract;

import java.util.Calendar;

import org.apache.commons.lang.StringUtils;

import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.file.payroll.contrata.ContrataParams;
import com.esferalia.aon.payroll.contrata.enumeration.TBONVFOR;
import com.esferalia.aon.payroll.contrata.enumeration.TEQPTIEM;
import com.esferalia.aon.payroll.enumeration.ContractCode;


public class ContractContrataHandler {
	
	private ContrataParams params;
	private ContractCode contractCode;
	
	public ContrataParams getParams() {
		if(params==null){
			params = new ContrataParams();
		}
		return params;
	}

	public void setParams(ContrataParams params) {
		this.params = params;
	}

	public ContractCode getContractCode() {
		return contractCode;
	}

	public void setContractCode(ContractCode contractCode) {
		this.contractCode = contractCode;
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
//				|| getContractCode() == ContractCode.C339
				|| getContractCode() == ContractCode.C309
				|| getContractCode() == ContractCode.C289
				|| getContractCode() == ContractCode.C239
				|| getContractCode() == ContractCode.C209;
	}
	public boolean isAvailableTipoJornada() {
		if( getContractCode()==ContractCode.C300 || getContractCode()==ContractCode.C330 || getContractCode()==ContractCode.C350){
			return false;
		}
		return true;
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
		if(getParams().getContract().getPerson().getAge()!=null){
			return getContractCode()==ContractCode.C421 && getParams().getContract().getPerson().getAge()>=21;
		} else {
			String msg = "El trabajador no tiene definida la fecha de nacimiento";
			AonUtil.addErrorMessage(msg);
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
		return getParams().getContract().getStartDate().after(cal.getTime()) 
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
		return getParams().getContract().getStartDate().after(cal.getTime()) 
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
	
	
	
	public void buildDataStructure(){
		
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
	
}
