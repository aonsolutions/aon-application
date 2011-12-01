package com.esferalia.aon.ui.payroll.controller.wizard;

import java.util.Date;

import com.code.aon.person.Person;
import com.esferalia.aon.payroll.CNO;
import com.esferalia.aon.payroll.enumeration.BasicCopySignatureType;
import com.esferalia.aon.payroll.enumeration.CollectiveReductionCode;
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

public class ContrataParams {
	
	/* 
	 * generales de contrato 
	 */
	private EducationalLevel educationalLevel;
	private boolean collectiveAgreement;
	private Double timeUnit;
	private String profession;
	private CNO cno;
	private EmploymentProgram employmentProgram;
	private String signaturePlace;
	private Date signatureDate;
	private String townCode;
	/* 
	 * datos de contrata 
	 */
//	private TimeType timeType;
	private String enterpriseFreeUse;
	private String timeType;
	private boolean theoryTraining;
//	private AgeGroup ageGroup;
	private String ageGroup;
	private Double partialRetirement;
	private boolean nonDateActivity;
	private boolean periodicalDiscontinuous;
	
//	private EmployerType employerType;
	private String employerType;
//	private InvestigationJobType investigationJobType;
	private String investigationJobType;
	private boolean investigationJobRD;
//	private LocalCorporation localCorporation;
	private String localCorporation;
//	private Actuation actuation;
	private String actuation;
	private Double financialYear;

	private boolean permanentContractDevelopment;
	private DismissalCollective dismissalCollective;

	// DATOS_GENERALESCONTRATOTYPE
	private String offer;
	private DisabilityCode disabilityCode;
	private OtherLaws otherLaws;
	private String campaignGeozone;
	private String campaign;
	private String campaignYear;
	
	// DATOSETCOTYPE 
	private SchoolWorkshop schoolWorkshop;
	
	// DATOSCOMUNICACOPIABASICATYPE
	private BasicCopySignatureType basicCopySignatureType;
	private String basicCopyComments;

	// DATOSETTTYPE
	private String ettCif;
	private String ettName;
	private boolean ettContractTemplate;
	private boolean ettForeignEnterprise;
	
	// CONTRATO RELEVO
	private EmployeeType reliefEmployeeType;
	private Person reliefPerson;
	
	// DATOSCONTRATOEXTRANJEROTYPE
	private String employmentCharacter;
	private String anexEmploymentYear;
	
	///////////////////////////////////////////////////
	// checks datos especificos contrato
	///////////////////////////////////////////////////
	private boolean employmentProgramData;
	private boolean ettData;
	private boolean reliefData;
	private boolean offerData;
	private boolean schoolWorkshopData;
	private boolean disabilityData;
	private boolean olderThan52Data;
	private boolean annexData;
	private boolean canpaignData;
	private boolean interimData;
	private boolean researchData;
	private boolean reductionData;
	
	
	public boolean isEmploymentProgramData() {
		return employmentProgramData;
	}
	public void setEmploymentProgramData(boolean employmentProgramData) {
		this.employmentProgramData = employmentProgramData;
	}
	public boolean isEttData() {
		return ettData;
	}
	public void setEttData(boolean ettData) {
		this.ettData = ettData;
	}
	public boolean isReliefData() {
		return reliefData;
	}
	public void setReliefData(boolean reliefData) {
		this.reliefData = reliefData;
	}
	public boolean isOfferData() {
		return offerData;
	}
	public void setOfferData(boolean offerData) {
		this.offerData = offerData;
	}
	public boolean isSchoolWorkshopData() {
		return schoolWorkshopData;
	}
	public void setSchoolWorkshopData(boolean schoolWorkshopData) {
		this.schoolWorkshopData = schoolWorkshopData;
	}
	public boolean isDisabilityData() {
		return disabilityData;
	}
	public void setDisabilityData(boolean disabilityData) {
		this.disabilityData = disabilityData;
	}
	public boolean isOlderThan52Data() {
		return olderThan52Data;
	}
	public void setOlderThan52Data(boolean olderThan52Data) {
		this.olderThan52Data = olderThan52Data;
	}
	public boolean isAnnexData() {
		return annexData;
	}
	public void setAnnexData(boolean annexData) {
		this.annexData = annexData;
	}
	public boolean isCanpaignData() {
		return canpaignData;
	}
	public void setCanpaignData(boolean canpaignData) {
		this.canpaignData = canpaignData;
	}
	public boolean isInterimData() {
		return interimData;
	}
	public void setInterimData(boolean interimData) {
		this.interimData = interimData;
	}
	public boolean isResearchData() {
		return researchData;
	}
	public void setResearchData(boolean researchData) {
		this.researchData = researchData;
	}
	public boolean isReductionData() {
		return reductionData;
	}
	public void setReductionData(boolean reductionData) {
		this.reductionData = reductionData;
	}
	
	public EducationalLevel getEducationalLevel() {
		return educationalLevel;
	}
	public void setEducationalLevel(EducationalLevel educationalLevel) {
		this.educationalLevel = educationalLevel;
	}
	public DisabilityCode getDisabilityCode() {
		return disabilityCode;
	}
	public void setDisabilityCode(DisabilityCode disabilityCode) {
		this.disabilityCode = disabilityCode;
	}
	public boolean isCollectiveAgreement() {
		return collectiveAgreement;
	}
	public void setCollectiveAgreement(boolean collectiveAgreement) {
		this.collectiveAgreement = collectiveAgreement;
	}
	public Double getTimeUnit() {
		return timeUnit;
	}
	public void setTimeUnit(Double timeUnit) {
		this.timeUnit = timeUnit;
	}
	public String getProfession() {
		return profession;
	}
	public void setProfession(String profession) {
		this.profession = profession;
	}
	public String getOffer() {
		return offer;
	}
	public void setOffer(String offer) {
		this.offer = offer;
	}
	
	public String getFullCampaign() {
		return campaignGeozone+campaign+campaignYear;
	}
	public String getCampaignGeozone(){
		return campaignGeozone;
	}
	public void setCampaignGeozone(String campaignGeozone){
		this.campaignGeozone = campaignGeozone;
	}
	public String getCampaign() {
		return campaign;
	}
	public void setCampaign(String campaign) {
		this.campaign = campaign;
	}
	public String getCampaignYear(){
		return campaignYear;
	}
	public void setCampaignYear(String campaignYear){
		this.campaignYear = campaignYear;
	}
	
	public CNO getCno() {
		if(cno==null){
			cno = new CNO();
		}
		return cno;
	}
	public void setCno(CNO cno) {
		this.cno = cno;
	}
	public EmploymentProgram getEmploymentProgram() {
		return employmentProgram;
	}
	public void setEmploymentProgram(EmploymentProgram employmentProgram) {
		this.employmentProgram = employmentProgram;
	}
	public OtherLaws getOtherLaws() {
		return otherLaws;
	}
	public void setOtherLaws(OtherLaws otherLaws) {
		this.otherLaws = otherLaws;
	}
	public String getSignaturePlace() {
		return signaturePlace;
	}
	public void setSignaturePlace(String signaturePlace) {
		this.signaturePlace = signaturePlace;
	}
	public Date getSignatureDate() {
		return signatureDate;
	}
	public void setSignatureDate(Date signatureDate) {
		this.signatureDate = signatureDate;
	}
	public String getTownCode() {
		return townCode;
	}
	public void setTownCode(String townCode) {
		this.townCode = townCode;
	}
	public EmployeeType getReliefEmployeeType() {
		return reliefEmployeeType;
	}
	public void setReliefEmployeeType(EmployeeType reliefEmployeeType) {
		this.reliefEmployeeType = reliefEmployeeType;
	}
	public Person getReliefPerson() {
		if(reliefPerson==null){
			reliefPerson = new Person();
		}
		return reliefPerson;
	}
	public void setReliefPerson(Person reliefPerson) {
		this.reliefPerson = reliefPerson;
	}
	
	public String getEmploymentCharacter() {
		return employmentCharacter;
	}
	public void setEmploymentCharacter(String employmentCharacter) {
		this.employmentCharacter = employmentCharacter;
	}
	public String getAnexEmploymentYear() {
		return anexEmploymentYear;
	}
	public void setAnexEmploymentYear(String anexEmploymentYear) {
		this.anexEmploymentYear = anexEmploymentYear;
	}
	public DismissalCollective getDismissalCollective() {
		return dismissalCollective;
	}
	public void setDismissalCollective(DismissalCollective dismissalCollective) {
		this.dismissalCollective = dismissalCollective;
	}
	public boolean isPermanentContractDevelopment() {
		return permanentContractDevelopment;
	}
	public void setPermanentContractDevelopment(boolean permanentContractDevelopment) {
		this.permanentContractDevelopment = permanentContractDevelopment;
	}
	public String getEnterpriseFreeUse() {
		return enterpriseFreeUse;
	}
	public void setEnterpriseFreeUse(String enterpriseFreeUse) {
		this.enterpriseFreeUse = enterpriseFreeUse;
	}
	public String getTimeType() {
		return timeType;
	}
	public void setTimeType(String timeType) {
		this.timeType = timeType;
	}
	public boolean isTheoryTraining() {
		return theoryTraining;
	}
	public void setTheoryTraining(boolean theoryTraining) {
		this.theoryTraining = theoryTraining;
	}
	public String getAgeGroup() {
		return ageGroup;
	}
	public void setAgeGroup(String ageGroup) {
		this.ageGroup = ageGroup;
	}
	public Double getPartialRetirement() {
		return partialRetirement;
	}
	public void setPartialRetirement(Double partialRetirement) {
		this.partialRetirement = partialRetirement;
	}
	public boolean isNonDateActivity() {
		return nonDateActivity;
	}
	public void setNonDateActivity(boolean nonDateActivity) {
		this.nonDateActivity = nonDateActivity;
	}
	public boolean isPeriodicalDiscontinuous() {
		return periodicalDiscontinuous;
	}
	public void setPeriodicalDiscontinuous(boolean periodicalDiscontinuous) {
		this.periodicalDiscontinuous = periodicalDiscontinuous;
	}
	public SchoolWorkshop getSchoolWorkshop() {
		return schoolWorkshop;
	}
	public void setSchoolWorkshop(SchoolWorkshop schoolWorkshop) {
		this.schoolWorkshop = schoolWorkshop;
	}
	public String getEmployerType() {
		return employerType;
	}
	public void setEmployerType(String employerType) {
		this.employerType = employerType;
	}
	public String getInvestigationJobType() {
		return investigationJobType;
	}
	public void setInvestigationJobType(String investigationJobType) {
		this.investigationJobType = investigationJobType;
	}
	public boolean isInvestigationJobRD() {
		return investigationJobRD;
	}
	public void setInvestigationJobRD(boolean investigationJobRD) {
		this.investigationJobRD = investigationJobRD;
	}
	public String getLocalCorporation() {
		return localCorporation;
	}
	public void setLocalCorporation(String localCorporation) {
		this.localCorporation = localCorporation;
	}
	public String getActuation() {
		return actuation;
	}
	public void setActuation(String actuation) {
		this.actuation = actuation;
	}
	public Double getFinancialYear() {
		return financialYear;
	}
	public void setFinancialYear(Double financialYear) {
		this.financialYear = financialYear;
	}
	public BasicCopySignatureType getBasicCopySignatureType() {
		return basicCopySignatureType;
	}
	public void setBasicCopySignatureType(BasicCopySignatureType basicCopySignatureType) {
		this.basicCopySignatureType = basicCopySignatureType;
	}
	public String getBasicCopyComments() {
		return basicCopyComments;
	}
	public void setBasicCopyComments(String basicCopyComments) {
		this.basicCopyComments = basicCopyComments;
	}
	public String getEttCif() {
		return ettCif;
	}
	public void setEttCif(String ettCif) {
		this.ettCif = ettCif;
	}
	public String getEttName() {
		return ettName;
	}
	public void setEttName(String ettName) {
		this.ettName = ettName;
	}
	public boolean isEttContractTemplate() {
		return ettContractTemplate;
	}
	public void setEttContractTemplate(boolean ettContractTemplate) {
		this.ettContractTemplate = ettContractTemplate;
	}
	public boolean isEttForeignEnterprise() {
		return ettForeignEnterprise;
	}
	public void setEttForeignEnterprise(boolean ettForeignEnterprise) {
		this.ettForeignEnterprise = ettForeignEnterprise;
	}
	
	
	////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////
	
	// DATOSCONTRATOINTERINIDADTYPE
	private InterimCause causaInterinidad;
	
	// DATOSREDUCCIONRDL12011TYPE
	private CollectiveReductionCode codigocolectivoreduccion;
	private String porcentajereduccion;
	private Double porcentajejornadareduccion;
	
	// DATOSPROGEMPLEOPUBLICOTYPE
	private String corporacionlocal;
	private String actuacion;
	private String ejerciciopresupuestario;
	private String grupocotizacioncorporacionlocal;
	
	// DATOSCONTRATOTIEMPOPARCIALTYPE
	private WorkingDayType tipojornada;
	private String horasjornada;
	private String minutosjornada;
	private String horasconvenio;
	private String minutosconvenio;
	private String horasformacion;
	private String minutosformacion;
	private String indicformacionteorica;
	private String colectivoedad;
	private String porcentajejubilacionparcial;
	private String actividadsinfechacierta;
	private Boolean fijodiscontinuoperiodico;
	private String porcjornadapactada;
	private String horasanualestiempocompleto;
	
	// DATOSCONTRATOINVESTIGACIONTYPE
	private ResearchEmployer indempleador;
	private ResearchEmployee indtrabajador;
	private Boolean indrd632006;
	
	// DATOSCONTRATOPRACTICASTYPE 
	private String titulacionacademica;
	private Boolean indcertifprofesionalidad;
	
	
	public InterimCause getCausaInterinidad() {
		return causaInterinidad;
	}
	public void setCausaInterinidad(InterimCause causaInterinidad) {
		this.causaInterinidad = causaInterinidad;
	}

	public CollectiveReductionCode getCodigocolectivoreduccion() {
		return codigocolectivoreduccion;
	}
	public void setCodigocolectivoreduccion(
			CollectiveReductionCode codigocolectivoreduccion) {
		this.codigocolectivoreduccion = codigocolectivoreduccion;
	}
	public String getPorcentajereduccion() {
		return porcentajereduccion;
	}
	public void setPorcentajereduccion(String porcentajereduccion) {
		this.porcentajereduccion = porcentajereduccion;
	}
	public Double getPorcentajejornadareduccion() {
		return porcentajejornadareduccion;
	}
	public void setPorcentajejornadareduccion(Double porcentajejornadareduccion) {
		this.porcentajejornadareduccion = porcentajejornadareduccion;
	}
	public String getCorporacionlocal() {
		return corporacionlocal;
	}
	public void setCorporacionlocal(String corporacionlocal) {
		this.corporacionlocal = corporacionlocal;
	}
	public String getActuacion() {
		return actuacion;
	}
	public void setActuacion(String actuacion) {
		this.actuacion = actuacion;
	}
	public String getEjerciciopresupuestario() {
		return ejerciciopresupuestario;
	}
	public void setEjerciciopresupuestario(String ejerciciopresupuestario) {
		this.ejerciciopresupuestario = ejerciciopresupuestario;
	}
	public String getGrupocotizacioncorporacionlocal() {
		return grupocotizacioncorporacionlocal;
	}
	public void setGrupocotizacioncorporacionlocal(
			String grupocotizacioncorporacionlocal) {
		this.grupocotizacioncorporacionlocal = grupocotizacioncorporacionlocal;
	}
	public WorkingDayType getTipojornada() {
		return tipojornada;
	}
	public void setTipojornada(WorkingDayType tipojornada) {
		this.tipojornada = tipojornada;
	}
	public String getMinutosjornada() {
		return minutosjornada;
	}
	public void setMinutosjornada(String minutosjornada) {
		this.minutosjornada = minutosjornada;
	}
	public String getMinutosconvenio() {
		return minutosconvenio;
	}
	public String getDuracionconvenio() {
		return (horasconvenio==null?"":completeLength(horasconvenio, 4, "0", false))+(minutosconvenio==null?"":completeLength(minutosconvenio, 2, "0", false));
	}
	public String getDuracionjornada() {
		return (horasjornada==null?"":completeLength(horasjornada, 4, "0", false))+(minutosjornada==null?"":completeLength(minutosjornada, 2, "0", false));
	}
	public String getDuracionformacion() {
		return (horasformacion==null?"":completeLength(horasformacion, 4, "0", false))+(minutosformacion==null?"":completeLength(minutosformacion, 2, "0", false));
	}
	public String getMinutosformacion() {
		return minutosformacion;
	}
	public void setMinutosconvenio(String minutosconvenio) {
		this.minutosconvenio = minutosconvenio;
	}
	public void setMinutosformacion(String minutosformacion) {
		this.minutosformacion = minutosformacion;
	}
	public String getHorasjornada() {
		return horasjornada;
	}
	public void setHorasjornada(String horasjornada) {
		this.horasjornada = horasjornada;
	}
	public String getHorasconvenio() {
		return horasconvenio;
	}
	public void setHorasconvenio(String horasconvenio) {
		this.horasconvenio = horasconvenio;
	}
	public String getHorasformacion() {
		return horasformacion;
	}
	public void setHorasformacion(String horasformacion) {
		this.horasformacion = horasformacion;
	}
	public String getIndicformacionteorica() {
		return indicformacionteorica;
	}
	public void setIndicformacionteorica(String indicformacionteorica) {
		this.indicformacionteorica = indicformacionteorica;
	}
	public String getColectivoedad() {
		return colectivoedad;
	}
	public void setColectivoedad(String colectivoedad) {
		this.colectivoedad = colectivoedad;
	}
	public String getPorcentajejubilacionparcial() {
		return porcentajejubilacionparcial;
	}
	public void setPorcentajejubilacionparcial(String porcentajejubilacionparcial) {
		this.porcentajejubilacionparcial = porcentajejubilacionparcial;
	}
	public String getActividadsinfechacierta() {
		return actividadsinfechacierta;
	}
	public void setActividadsinfechacierta(String actividadsinfechacierta) {
		this.actividadsinfechacierta = actividadsinfechacierta;
	}
	public Boolean getFijodiscontinuoperiodico() {
		return fijodiscontinuoperiodico;
	}
	public void setFijodiscontinuoperiodico(Boolean fijodiscontinuoperiodico) {
		this.fijodiscontinuoperiodico = fijodiscontinuoperiodico;
	}
	public String getPorcjornadapactada() {
		return porcjornadapactada;
	}
	public void setPorcjornadapactada(String porcjornadapactada) {
		this.porcjornadapactada = porcjornadapactada;
	}
	public String getHorasanualestiempocompleto() {
		return horasanualestiempocompleto;
	}
	public void setHorasanualestiempocompleto(String horasanualestiempocompleto) {
		this.horasanualestiempocompleto = horasanualestiempocompleto;
	}
	public ResearchEmployer getIndempleador() {
		return indempleador;
	}
	public void setIndempleador(ResearchEmployer indempleador) {
		this.indempleador = indempleador;
	}
	public ResearchEmployee getIndtrabajador() {
		return indtrabajador;
	}
	public void setIndtrabajador(ResearchEmployee indtrabajador) {
		this.indtrabajador = indtrabajador;
	}
	public Boolean getIndrd632006() {
		return indrd632006;
	}
	public void setIndrd632006(Boolean indrd632006) {
		this.indrd632006 = indrd632006;
	}
	public String getTitulacionacademica() {
		return titulacionacademica;
	}
	public void setTitulacionacademica(String titulacionacademica) {
		this.titulacionacademica = titulacionacademica;
	}
	public Boolean getIndcertifprofesionalidad() {
		return indcertifprofesionalidad;
	}
	public void setIndcertifprofesionalidad(Boolean indcertifprofesionalidad) {
		this.indcertifprofesionalidad = indcertifprofesionalidad;
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
