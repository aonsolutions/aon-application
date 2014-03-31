package com.esferalia.aon.file.payroll.contrata;

import java.io.Serializable;
import java.util.Date;

import com.code.aon.AonVersion;
import com.code.aon.person.Person;
import com.esferalia.aon.payroll.CNO;
import com.esferalia.aon.payroll.enumeration.ContractCode;
import com.esferalia.aon.payroll.enumeration.contrata.TEJINDIS;
import com.esferalia.aon.payroll.enumeration.contrata.TEOCOLDE;
import com.esferalia.aon.payroll.enumeration.contrata.TEQPTIEM;
import com.esferalia.aon.payroll.enumeration.contrata.TERFIRCB;


public class ContrataTransformacionesParams implements IContrataParams, Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private ContractCode transformCode;
	private Date fechaInicio;
	private Date fechaTerminoReal;
	private String indicadorDiscontinuidad;
	
	
	public String getIndicadorDiscontinuidad() {
		return indicadorDiscontinuidad;
	}

	public void setIndicadorDiscontinuidad(String indicadorDiscontinuidad) {
		this.indicadorDiscontinuidad = indicadorDiscontinuidad;
	}

	public Date getFechaInicio() {
		return fechaInicio;
	}

	public void setFechaInicio(Date fechaInicio) {
		this.fechaInicio = fechaInicio;
	}

	public Date getFechaTerminoReal() {
		return fechaTerminoReal;
	}

	public void setFechaTerminoReal(Date fechaTerminoReal) {
		this.fechaTerminoReal = fechaTerminoReal;
	}

	public ContractCode getTransformCode() {
		return transformCode;
	}

	public void setTransformCode(ContractCode transformCode) {
		this.transformCode = transformCode;
	}

	
	
//	/* 
//	 * generales de contrato 
//	 */
//	private TBONVFOR nivelFormativo;
//	private boolean collectiveAgreement;
//	private Double timeUnit;
//	private String profession;
	private CNO cno;
//	private TETPGMEM codigoProgramaEmpleo;
//	private String signaturePlace;
//	private Date signatureDate;
//	/* 
//	 * datos de contrata 
//	 */
	private String usoLibreEmpresa;
//	private String timeType;
//	private boolean theoryTraining;
//	private String ageGroup;
//	private Double partialRetirement;
//	private boolean nonDateActivity;
//	private boolean periodicalDiscontinuous;
//	
//	private String employerType;
//	private String investigationJobType;
//	private boolean investigationJobRD;
//	private String localCorporation;
//	private String actuation;
//	private Double financialYear;
//
	private boolean indCosteDespido;
	private TEOCOLDE codigoColectivoDespido;
//
//	// DATOS_GENERALESCONTRATOTYPE
//	private String offer;
	private TEJINDIS indDiscapacidad;
//	private TELCOLBO colectivoBonificacion;
//	private Boolean indEmpleadAutonomo;
//	private THYDISLE otrasLegislaciones;
//	private String campaignGeozone;
//	private String campaign;
//	private String campaignYear;
//	
//	// DATOSETCOTYPE 
//	private TESCETCO codigoEtCoTe;
//	
//	// DATOSCOMUNICACOPIABASICATYPE
	private TERFIRCB tipoFirmaCopiaBasica;
	private String textoCopiaBasica;
//
//	// DATOSETTTYPE
//	private String ettCif;
//	private String ettName;
//	private boolean ettContractTemplate;
//	private boolean ettForeignEnterprise;
//	
//	// CONTRATO RELEVO
//	private TEYTRELE tipoTrabajadorRelevo;
	private Person reliefPerson;
//	
//	// DATOSCONTRATOEXTRANJEROTYPE
//	private String employmentCharacter;
//	private String anexEmploymentYear;
//	
//	///////////////////////////////////////////////////
//	// checks datos especificos contrato
//	///////////////////////////////////////////////////
//	private boolean employmentProgramData;
//	private boolean ettData;
	private boolean reliefData;
//	private boolean offerData;
//	private boolean schoolWorkshopData;
//	private boolean disabilityData;
//	private boolean apoyoEmprendedoresData;
//	private boolean olderThan52Data;
//	private boolean annexData;
//	private boolean canpaignData;
//	private boolean interimData;
//	private boolean researchData;
//	private boolean reductionData;
//	private boolean medidasFomentoData;
//
//	private boolean showEmploymentProgramData;
//	private boolean showEttData;
	private boolean showReliefData;
//	private boolean showOfferData;
//	private boolean showSchoolWorkshopData;
//	private boolean showDisabilityData;
//	private boolean showApoyoEmprendedoresData;
//	private boolean showOlderThan52Data;
//	private boolean showAnnexData;
//	private boolean showCanpaignData;
//	private boolean showInterimData;
//	private boolean showResearchData;
//	private boolean showReductionData;
//	private boolean showMedidasFomentoData;
//	
//	
//	
//	public boolean isShowEmploymentProgramData() {
//		return showEmploymentProgramData;
//	}
//	public boolean isMedidasFomentoData() {
//		return medidasFomentoData;
//	}
//	public void setMedidasFomentoData(boolean medidasFomentoData) {
//		this.medidasFomentoData = medidasFomentoData;
//	}
//	public boolean isShowMedidasFomentoData() {
//		return showMedidasFomentoData;
//	}
//	public void setShowMedidasFomentoData(boolean showMedidasFomentoData) {
//		this.showMedidasFomentoData = showMedidasFomentoData;
//	}
//	public void setShowEmploymentProgramData(boolean showEmploymentProgramData) {
//		this.showEmploymentProgramData = showEmploymentProgramData;
//	}
//	public boolean isShowEttData() {
//		return showEttData;
//	}
//	public void setShowEttData(boolean showEttData) {
//		this.showEttData = showEttData;
//	}
	public boolean isShowReliefData() {
		return showReliefData;
	}
	public void setShowReliefData(boolean showReliefData) {
		this.showReliefData = showReliefData;
	}
//	public boolean isShowOfferData() {
//		return showOfferData;
//	}
//	public void setShowOfferData(boolean showOfferData) {
//		this.showOfferData = showOfferData;
//	}
//	public boolean isShowSchoolWorkshopData() {
//		return showSchoolWorkshopData;
//	}
//	public void setShowSchoolWorkshopData(boolean showSchoolWorkshopData) {
//		this.showSchoolWorkshopData = showSchoolWorkshopData;
//	}
//	public boolean isShowDisabilityData() {
//		return showDisabilityData;
//	}
//	public void setShowDisabilityData(boolean showDisabilityData) {
//		this.showDisabilityData = showDisabilityData;
//	}
//	public boolean isShowApoyoEmprendedoresData() {
//		return showApoyoEmprendedoresData;
//	}
//	public void setShowApoyoEmprendedoresData(boolean showApoyoEmprendedoresData) {
//		this.showApoyoEmprendedoresData = showApoyoEmprendedoresData;
//	}
//	public boolean isShowOlderThan52Data() {
//		return showOlderThan52Data;
//	}
//	public void setShowOlderThan52Data(boolean showOlderThan52Data) {
//		this.showOlderThan52Data = showOlderThan52Data;
//	}
//	public boolean isShowAnnexData() {
//		return showAnnexData;
//	}
//	public void setShowAnnexData(boolean showAnnexData) {
//		this.showAnnexData = showAnnexData;
//	}
//	public boolean isShowCanpaignData() {
//		return showCanpaignData;
//	}
//	public void setShowCanpaignData(boolean showCanpaignData) {
//		this.showCanpaignData = showCanpaignData;
//	}
//	public boolean isShowInterimData() {
//		return showInterimData;
//	}
//	public void setShowInterimData(boolean showInterimData) {
//		this.showInterimData = showInterimData;
//	}
//	public boolean isShowResearchData() {
//		return showResearchData;
//	}
//	public void setShowResearchData(boolean showResearchData) {
//		this.showResearchData = showResearchData;
//	}
//	public boolean isShowReductionData() {
//		return showReductionData;
//	}
//	public void setShowReductionData(boolean showReductionData) {
//		this.showReductionData = showReductionData;
//	}
//	public boolean isEmploymentProgramData() {
//		return employmentProgramData;
//	}
//	public void setEmploymentProgramData(boolean employmentProgramData) {
//		this.employmentProgramData = employmentProgramData;
//	}
//	public boolean isEttData() {
//		return ettData;
//	}
//	public void setEttData(boolean ettData) {
//		this.ettData = ettData;
//	}
	public boolean isReliefData() {
		return reliefData;
	}
	public void setReliefData(boolean reliefData) {
		this.reliefData = reliefData;
	}
//	public boolean isOfferData() {
//		return offerData;
//	}
//	public void setOfferData(boolean offerData) {
//		this.offerData = offerData;
//	}
//	public boolean isSchoolWorkshopData() {
//		return schoolWorkshopData;
//	}
//	public void setSchoolWorkshopData(boolean schoolWorkshopData) {
//		this.schoolWorkshopData = schoolWorkshopData;
//	}
//	public boolean isDisabilityData() {
//		return disabilityData;
//	}
//	public void setDisabilityData(boolean disabilityData) {
//		this.disabilityData = disabilityData;
//	}
//	public boolean isApoyoEmprendedoresData() {
//		return apoyoEmprendedoresData;
//	}
//	public void setApoyoEmprendedoresData(boolean apoyoEmprendedoresData) {
//		this.apoyoEmprendedoresData = apoyoEmprendedoresData;
//	}
//	public boolean isOlderThan52Data() {
//		return olderThan52Data;
//	}
//	public void setOlderThan52Data(boolean olderThan52Data) {
//		this.olderThan52Data = olderThan52Data;
//	}
//	public boolean isAnnexData() {
//		return annexData;
//	}
//	public void setAnnexData(boolean annexData) {
//		this.annexData = annexData;
//	}
//	public boolean isCanpaignData() {
//		return canpaignData;
//	}
//	public void setCanpaignData(boolean canpaignData) {
//		this.canpaignData = canpaignData;
//	}
//	public boolean isInterimData() {
//		return interimData;
//	}
//	public void setInterimData(boolean interimData) {
//		this.interimData = interimData;
//	}
//	public boolean isResearchData() {
//		return researchData;
//	}
//	public void setResearchData(boolean researchData) {
//		this.researchData = researchData;
//	}
//	public boolean isReductionData() {
//		return reductionData;
//	}
//	public void setReductionData(boolean reductionData) {
//		this.reductionData = reductionData;
//	}
//	public TBONVFOR getNivelFormativo() {
//		return nivelFormativo;
//	}
//	public void setNivelFormativo(TBONVFOR nivelFormativo) {
//		this.nivelFormativo = nivelFormativo;
//	}
	public TEJINDIS getIndDiscapacidad() {
		return indDiscapacidad;
	}
	public void setIndDiscapacidad(TEJINDIS indDiscapacidad) {
		this.indDiscapacidad = indDiscapacidad;
	}
//	public TELCOLBO getColectivoBonificacion() {
//		return colectivoBonificacion;
//	}
//	public void setColectivoBonificacion(TELCOLBO colectivoBonificacion) {
//		this.colectivoBonificacion = colectivoBonificacion;
//	}
//	public Boolean getIndEmpleadAutonomo() {
//		return indEmpleadAutonomo;
//	}
//	public void setIndEmpleadAutonomo(Boolean indEmpleadAutonomo) {
//		this.indEmpleadAutonomo = indEmpleadAutonomo;
//	}
//	public boolean isCollectiveAgreement() {
//		return collectiveAgreement;
//	}
//	public void setCollectiveAgreement(boolean collectiveAgreement) {
//		this.collectiveAgreement = collectiveAgreement;
//	}
//	public Double getTimeUnit() {
//		return timeUnit;
//	}
//	public void setTimeUnit(Double timeUnit) {
//		this.timeUnit = timeUnit;
//	}
//	public String getProfession() {
//		return profession;
//	}
//	public void setProfession(String profession) {
//		this.profession = profession;
//	}
//	public String getOffer() {
//		return offer;
//	}
//	public void setOffer(String offer) {
//		this.offer = offer;
//	}
//	
//	public String getFullCampaign() {
//		return campaignGeozone+campaign+campaignYear;
//	}
//	public String getCampaignGeozone(){
//		return campaignGeozone;
//	}
//	public void setCampaignGeozone(String campaignGeozone){
//		this.campaignGeozone = campaignGeozone;
//	}
//	public String getCampaign() {
//		return campaign;
//	}
//	public void setCampaign(String campaign) {
//		this.campaign = campaign;
//	}
//	public String getCampaignYear(){
//		return campaignYear;
//	}
//	public void setCampaignYear(String campaignYear){
//		this.campaignYear = campaignYear;
//	}
//	
	public CNO getCno() {
		if(cno==null){
			cno = new CNO();
		}
		return cno;
	}
	public void setCno(CNO cno) {
		this.cno = cno;
	}
//	public TETPGMEM getCodigoProgramaEmpleo() {
//		return codigoProgramaEmpleo;
//	}
//	public void setCodigoProgramaEmpleo(TETPGMEM codigoProgramaEmpleo) {
//		this.codigoProgramaEmpleo = codigoProgramaEmpleo;
//	}
//	public THYDISLE getOtrasLegislaciones() {
//		return otrasLegislaciones;
//	}
//	public void setOtrasLegislaciones(THYDISLE otrasLegislaciones) {
//		this.otrasLegislaciones = otrasLegislaciones;
//	}
//	public String getSignaturePlace() {
//		return signaturePlace;
//	}
//	public void setSignaturePlace(String signaturePlace) {
//		this.signaturePlace = signaturePlace;
//	}
//	public Date getSignatureDate() {
//		return signatureDate;
//	}
//	public void setSignatureDate(Date signatureDate) {
//		this.signatureDate = signatureDate;
//	}
//	public TEYTRELE getTipoTrabajadorRelevo() {
//		return tipoTrabajadorRelevo;
//	}
//	public void setTipoTrabajadorRelevo(TEYTRELE tipoTrabajadorRelevo) {
//		this.tipoTrabajadorRelevo = tipoTrabajadorRelevo;
//	}
	public Person getReliefPerson() {
		if(reliefPerson==null){
			reliefPerson = new Person();
		}
		return reliefPerson;
	}
	public void setReliefPerson(Person reliefPerson) {
		this.reliefPerson = reliefPerson;
	}
//	
//	public String getEmploymentCharacter() {
//		return employmentCharacter;
//	}
//	public void setEmploymentCharacter(String employmentCharacter) {
//		this.employmentCharacter = employmentCharacter;
//	}
//	public String getAnexEmploymentYear() {
//		return anexEmploymentYear;
//	}
//	public void setAnexEmploymentYear(String anexEmploymentYear) {
//		this.anexEmploymentYear = anexEmploymentYear;
//	}
	public TEOCOLDE getCodigoColectivoDespido() {
		return codigoColectivoDespido;
	}
	public void setCodigoColectivoDespido(TEOCOLDE codigoColectivoDespido) {
		this.codigoColectivoDespido = codigoColectivoDespido;
	}
	public boolean isIndCosteDespido() {
		return indCosteDespido;
	}
	public void setIndCosteDespido(boolean indCosteDespido) {
		this.indCosteDespido = indCosteDespido;
	}
	public String getUsoLibreEmpresa() {
		return usoLibreEmpresa;
	}
	public void setUsoLibreEmpresa(String usoLibreEmpresa) {
		this.usoLibreEmpresa = usoLibreEmpresa;
	}
//	public String getTimeType() {
//		return timeType;
//	}
//	public void setTimeType(String timeType) {
//		this.timeType = timeType;
//	}
//	public boolean isTheoryTraining() {
//		return theoryTraining;
//	}
//	public void setTheoryTraining(boolean theoryTraining) {
//		this.theoryTraining = theoryTraining;
//	}
//	public String getAgeGroup() {
//		return ageGroup;
//	}
//	public void setAgeGroup(String ageGroup) {
//		this.ageGroup = ageGroup;
//	}
//	public Double getPartialRetirement() {
//		return partialRetirement;
//	}
//	public void setPartialRetirement(Double partialRetirement) {
//		this.partialRetirement = partialRetirement;
//	}
//	public boolean isNonDateActivity() {
//		return nonDateActivity;
//	}
//	public void setNonDateActivity(boolean nonDateActivity) {
//		this.nonDateActivity = nonDateActivity;
//	}
//	public boolean isPeriodicalDiscontinuous() {
//		return periodicalDiscontinuous;
//	}
//	public void setPeriodicalDiscontinuous(boolean periodicalDiscontinuous) {
//		this.periodicalDiscontinuous = periodicalDiscontinuous;
//	}
//	public TESCETCO getCodigoEtCoTe() {
//		return codigoEtCoTe;
//	}
//	public void setCodigoEtCoTe(TESCETCO codigoEtCoTe) {
//		this.codigoEtCoTe = codigoEtCoTe;
//	}
//	public String getEmployerType() {
//		return employerType;
//	}
//	public void setEmployerType(String employerType) {
//		this.employerType = employerType;
//	}
//	public String getInvestigationJobType() {
//		return investigationJobType;
//	}
//	public void setInvestigationJobType(String investigationJobType) {
//		this.investigationJobType = investigationJobType;
//	}
//	public boolean isInvestigationJobRD() {
//		return investigationJobRD;
//	}
//	public void setInvestigationJobRD(boolean investigationJobRD) {
//		this.investigationJobRD = investigationJobRD;
//	}
//	public String getLocalCorporation() {
//		return localCorporation;
//	}
//	public void setLocalCorporation(String localCorporation) {
//		this.localCorporation = localCorporation;
//	}
//	public String getActuation() {
//		return actuation;
//	}
//	public void setActuation(String actuation) {
//		this.actuation = actuation;
//	}
//	public Double getFinancialYear() {
//		return financialYear;
//	}
//	public void setFinancialYear(Double financialYear) {
//		this.financialYear = financialYear;
//	}
	public TERFIRCB getTipoFirmaCopiaBasica() {
		return tipoFirmaCopiaBasica;
	}
	public void setTipoFirmaCopiaBasica(TERFIRCB tipoFirmaCopiaBasica) {
		this.tipoFirmaCopiaBasica = tipoFirmaCopiaBasica;
	}
	public String getTextoCopiaBasica() {
		return textoCopiaBasica;
	}
	public void setTextoCopiaBasica(String textoCopiaBasica) {
		this.textoCopiaBasica = textoCopiaBasica;
	}
//	public String getEttCif() {
//		return ettCif;
//	}
//	public void setEttCif(String ettCif) {
//		this.ettCif = ettCif;
//	}
//	public String getEttName() {
//		return ettName;
//	}
//	public void setEttName(String ettName) {
//		this.ettName = ettName;
//	}
//	public boolean isEttContractTemplate() {
//		return ettContractTemplate;
//	}
//	public void setEttContractTemplate(boolean ettContractTemplate) {
//		this.ettContractTemplate = ettContractTemplate;
//	}
//	public boolean isEttForeignEnterprise() {
//		return ettForeignEnterprise;
//	}
//	public void setEttForeignEnterprise(boolean ettForeignEnterprise) {
//		this.ettForeignEnterprise = ettForeignEnterprise;
//	}
//	
//	
//	////////////////////////////////////////////////////////////////
//	////////////////////////////////////////////////////////////////
//	////////////////////////////////////////////////////////////////
//	////////////////////////////////////////////////////////////////
//	
//	// DATOSCONTRATOINTERINIDADTYPE
//	private TEIINTER causaInterinidad;
//	
//	// DATOSREDUCCIONRDL12011TYPE
//	private TQOCOLRE codigoColectivoReduccion;
//	private String porcentajeReduccion;
//	private Double porcentajeJornadaReduccion;
//	
//	// DATOSPROGEMPLEOPUBLICOTYPE
//	private String corporacionLocal;
//	private String actuacion;
//	private String ejercicioPresupuestario;
//	private String grupoCotizacionCorporacionLocal;
//	
//	// DATOSCONTRATOTIEMPOPARCIALTYPE
	private TEQPTIEM tipoJornada;
	private String horasJornada;
	private String minutosJornada;
	private String horasConvenio;
	private String minutosConvenio;
//	private String horasFormacion;
//	private String minutosFormacion;
//	private String indicFormacionTeorica;
//	private THPCOLFO colectivoEdad;
//	private String porcentajeJubilacionParcial;
	private String actividadSinFechaCierta;
	private Boolean fijoDiscontinuoPeriodico;
//	private String porcJornadaPactada;
//	private String horasAnualesTiempoCompleto;
//	
//	// DATOSCONTRATOINVESTIGACIONTYPE
//	private TEWEINVE indEmpleador;
//	private TEXTINVE indTrabajador;
//	private Boolean indRd632006;
//	
//	// DATOSCONTRATOPRACTICASTYPE 
//	private String titulacionAcademica;
//	private Boolean indCertifProfesionalidad;
//	
//	
//	public TEIINTER getCausaInterinidad() {
//		return causaInterinidad;
//	}
//	public void setCausaInterinidad(TEIINTER causaInterinidad) {
//		this.causaInterinidad = causaInterinidad;
//	}
//	public TQOCOLRE getCodigoColectivoReduccion() {
//		return codigoColectivoReduccion;
//	}
//	public void setCodigoColectivoReduccion(TQOCOLRE codigoColectivoReduccion) {
//		this.codigoColectivoReduccion = codigoColectivoReduccion;
//	}
//	public String getPorcentajeReduccion() {
//		return porcentajeReduccion;
//	}
//	public void setPorcentajeReduccion(String porcentajeReduccion) {
//		this.porcentajeReduccion = porcentajeReduccion;
//	}
//	public Double getPorcentajeJornadaReduccion() {
//		return porcentajeJornadaReduccion;
//	}
//	public void setPorcentajeJornadaReduccion(Double porcentajeJornadaReduccion) {
//		this.porcentajeJornadaReduccion = porcentajeJornadaReduccion;
//	}
//	public String getCorporacionLocal() {
//		return corporacionLocal;
//	}
//	public void setCorporacionLocal(String corporacionLocal) {
//		this.corporacionLocal = corporacionLocal;
//	}
//	public String getActuacion() {
//		return actuacion;
//	}
//	public void setActuacion(String actuacion) {
//		this.actuacion = actuacion;
//	}
//	public String getEjercicioPresupuestario() {
//		return ejercicioPresupuestario;
//	}
//	public void setEjercicioPresupuestario(String ejercicioPresupuestario) {
//		this.ejercicioPresupuestario = ejercicioPresupuestario;
//	}
//	public String getGrupoCotizacionCorporacionLocal() {
//		return grupoCotizacionCorporacionLocal;
//	}
//	public void setGrupoCotizacionCorporacionLocal(
//			String grupoCotizacionCorporacionLocal) {
//		this.grupoCotizacionCorporacionLocal = grupoCotizacionCorporacionLocal;
//	}
	public TEQPTIEM getTipoJornada() {
		return tipoJornada;
	}
	public void setTipoJornada(TEQPTIEM tipoJornada) {
		this.tipoJornada = tipoJornada;
	}
	public String getHorasJornada() {
		return horasJornada;
	}
	public void setHorasJornada(String horasJornada) {
		this.horasJornada = horasJornada;
	}
	public String getMinutosJornada() {
		return minutosJornada;
	}
	public void setMinutosJornada(String minutosJornada) {
		this.minutosJornada = minutosJornada;
	}
	public String getHorasConvenio() {
		return horasConvenio;
	}
	public void setHorasConvenio(String horasConvenio) {
		this.horasConvenio = horasConvenio;
	}
	public String getMinutosConvenio() {
		return minutosConvenio;
	}
	public void setMinutosConvenio(String minutosConvenio) {
		this.minutosConvenio = minutosConvenio;
	}
//	public String getHorasFormacion() {
//		return horasFormacion;
//	}
//	public void setHorasFormacion(String horasFormacion) {
//		this.horasFormacion = horasFormacion;
//	}
//	public String getMinutosFormacion() {
//		return minutosFormacion;
//	}
//	public void setMinutosFormacion(String minutosFormacion) {
//		this.minutosFormacion = minutosFormacion;
//	}
//	public String getIndicFormacionTeorica() {
//		return indicFormacionTeorica;
//	}
//	public void setIndicFormacionTeorica(String indicFormacionTeorica) {
//		this.indicFormacionTeorica = indicFormacionTeorica;
//	}
//	public THPCOLFO getColectivoEdad() {
//		return colectivoEdad;
//	}
//	public void setColectivoEdad(THPCOLFO colectivoEdad) {
//		this.colectivoEdad = colectivoEdad;
//	}
//	public String getPorcentajeJubilacionParcial() {
//		return porcentajeJubilacionParcial;
//	}
//	public void setPorcentajeJubilacionParcial(String porcentajeJubilacionParcial) {
//		this.porcentajeJubilacionParcial = porcentajeJubilacionParcial;
//	}
	public String getActividadSinFechaCierta() {
		return actividadSinFechaCierta;
	}
	public void setActividadSinFechaCierta(String actividadSinFechaCierta) {
		this.actividadSinFechaCierta = actividadSinFechaCierta;
	}
	public Boolean getFijoDiscontinuoPeriodico() {
		return fijoDiscontinuoPeriodico;
	}
	public void setFijoDiscontinuoPeriodico(Boolean fijoDiscontinuoPeriodico) {
		this.fijoDiscontinuoPeriodico = fijoDiscontinuoPeriodico;
	}
//	public String getPorcJornadaPactada() {
//		return porcJornadaPactada;
//	}
//	public void setPorcJornadaPactada(String porcJornadaPactada) {
//		this.porcJornadaPactada = porcJornadaPactada;
//	}
//	public String getHorasAnualesTiempoCompleto() {
//		return horasAnualesTiempoCompleto;
//	}
//	public void setHorasAnualesTiempoCompleto(String horasAnualesTiempoCompleto) {
//		this.horasAnualesTiempoCompleto = horasAnualesTiempoCompleto;
//	}
//	public Boolean getIndRd632006() {
//		return indRd632006;
//	}
//	public TEWEINVE getIndEmpleador() {
//		return indEmpleador;
//	}
//	public void setIndEmpleador(TEWEINVE indEmpleador) {
//		this.indEmpleador = indEmpleador;
//	}
//	public TEXTINVE getIndTrabajador() {
//		return indTrabajador;
//	}
//	public void setIndTrabajador(TEXTINVE indTrabajador) {
//		this.indTrabajador = indTrabajador;
//	}
//	public void setIndRd632006(Boolean indRd632006) {
//		this.indRd632006 = indRd632006;
//	}
//	public String getTitulacionAcademica() {
//		return titulacionAcademica;
//	}
//	public void setTitulacionAcademica(String titulacionAcademica) {
//		this.titulacionAcademica = titulacionAcademica;
//	}
//	public Boolean getIndCertifProfesionalidad() {
//		return indCertifProfesionalidad;
//	}
//	public void setIndCertifProfesionalidad(Boolean indCertifProfesionalidad) {
//		this.indCertifProfesionalidad = indCertifProfesionalidad;
//	}
	
	
}
