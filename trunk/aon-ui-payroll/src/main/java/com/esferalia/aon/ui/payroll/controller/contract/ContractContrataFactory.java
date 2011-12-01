package com.esferalia.aon.ui.payroll.controller.contract;

import java.util.Calendar;

import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.payroll.enumeration.ContractCode;
import com.esferalia.aon.ui.payroll.controller.wizard.ContrataParams;


public class ContractContrataFactory {
	
	private ContrataParams params;
	private Contract contract;
	private ContractCode contractCode;
	
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
	public Boolean getShowWorkingDayType() {
		return getContractCode()!=ContractCode.C300||getContractCode()!=ContractCode.C330||getContractCode()!=ContractCode.C350;
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
	public Boolean getShowWorkingDayDuration() {
		return getContractCode()==ContractCode.C300||getContractCode()==ContractCode.C330||getContractCode()==ContractCode.C350;
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
	public Boolean getShowAgreementDuration() {
		return getContractCode()==ContractCode.C200||getContractCode()==ContractCode.C230||getContractCode()==ContractCode.C250||
		getContractCode()==ContractCode.C300||getContractCode()==ContractCode.C330||getContractCode()==ContractCode.C350;
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
	public Boolean getShowFormationDuration() {
		return getContractCode()==ContractCode.C421;
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
	public Boolean getShowFormationReceived() {
		return getContractCode()==ContractCode.C421;
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
	public Boolean getShowAgeCollective() {
		return getContractCode()==ContractCode.C421;
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
	public Boolean getShowRetirementPercent() {
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
	public Boolean getShowPeriodicallyDiscontinuous() {
		Calendar cal = Calendar.getInstance();
		cal.set(2006, 6, 1);
		return getContract().getStartDate().after(cal.getTime()) && 
		(getContractCode()==ContractCode.C200||getContractCode()==ContractCode.C230||getContractCode()==ContractCode.C250);
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
	public Boolean getShowAcademicTitulation() {
		return true;
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
	public Boolean getShowProfessionalCertificate() {
		Calendar cal = Calendar.getInstance();
		cal.set(2010, 6, 18);
		return getContract().getStartDate().after(cal.getTime()) && 
		(getContractCode()==ContractCode.C420||getContractCode()==ContractCode.C520||
				getContractCode()==ContractCode.C450||getContractCode()==ContractCode.C550);
	}
	
	////////////////////////////////////////
	// checks datos especificos contrato
	////////////////////////////////////////
	public Boolean getShowEmploymentProgramData(){
		return true;
	}
	public Boolean getShowEttData(){
		return true;
	}
	public Boolean getShowReliefData(){
		return true;	
	}
	public Boolean getShowOfferData(){
		return true;
	}
	public Boolean getShowWorkshopData(){
		return true;
	}
	public Boolean getShowDisabilityData(){
		return true;
	}
	public Boolean getShowOlderThan52Data(){
		return true;
	}
	public Boolean getShowAnnexData(){
		return true;
	}
	public Boolean getShowCanpaignData(){
		return true;
	}
	public Boolean getShowInterimData(){
		return true;
	}
	public Boolean getShowResearchData(){
		return true;
	}
	public Boolean getShowReductionData(){
		return true;
	}
	

	
	
	public void buildDataStructure(){
		
	}
	
	
}
