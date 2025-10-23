package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomCard;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomCheckBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDateBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomListBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomSuggestBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomTextArea;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomTextBox;
import com.esferalia.aon.gwt.payroll.shared.AcademicTitulation;
import com.esferalia.aon.gwt.payroll.shared.CNO;
import com.esferalia.aon.gwt.payroll.shared.EmployeeContractInfo;
import com.esferalia.aon.gwt.payroll.shared.FormativeLevel;
import com.esferalia.aon.occam.api.model.type.ContractType;
import com.esferalia.aon.occam.api.model.type.ContractType.ContractTypeRecord;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.Widget;

public abstract class ContractSpecificDataWidget extends FlowPanel {

	private AonCustomCard cnoCard; // Codigo Nacional de Ocupacion

	private AonCustomSuggestBox cnoSB = new AonCustomSuggestBox("CNO");

	private AonCustomCard comunicationCard; // Comunicacion Sepe

	private AonCustomTextBox ideTB = new AonCustomTextBox("IDE");
	private AonCustomDateBox comunicationDateBx = new AonCustomDateBox("Fecha Comunicaci\u00f3n");

	private AonCustomCard comunicationTransformCard; // Comunicacion Transformacion Sepe

	private AonCustomTextBox ideTransformTB = new AonCustomTextBox("IDE Transformaci\u00f3n");
	private AonCustomDateBox comunicationTransformDateBx = new AonCustomDateBox("Fecha Comunicaci\u00f3n Transformaci\u00f3n");

	private AonCustomCard comunicationExtensionCard; // Comunicacion Prorroga Sepe

	private AonCustomListBox extensionsLB = new AonCustomListBox("Pr\u00f3rrogas");
	private AonCustomTextBox ideExtensionTB = new AonCustomTextBox("IDE Pr\u00f3rroga");
	private AonCustomDateBox comunicationExtensionDateBx = new AonCustomDateBox("Fecha Comunicaci\u00f3n Pr\u00f3rroga");

	private AonCustomCard otherDataCard; // Otros datos contrato

	private AonCustomDateBox calendarFormativeStartDate = new AonCustomDateBox("Calendario formativo (F. Ini)");
	private AonCustomDateBox calendarFormativeEndDate = new AonCustomDateBox("Calendario formativo (F. Fin)");
	private AonCustomListBox formativeLevelLB = new AonCustomListBox("Nivel formativo");
	private AonCustomListBox academicTitulationLB = new AonCustomListBox("Titulaci\u00f3n acad\u00e9mica");
	private AonCustomCheckBox profesionalityCB = new AonCustomCheckBox("Certificado de profesionalidad");
	private AonCustomTextBox legalRepresentativeTB = new AonCustomTextBox("Representante Legal");
	private AonCustomListBox signBasicCopyLB = new AonCustomListBox("Tipo firma copia b\u00e1sica");
	private AonCustomTextArea basicCopyTA = new AonCustomTextArea("Texto copia b\u00e1sica");
	private AonCustomTextBox useEnterpriseFreeTB = new AonCustomTextBox("Uso libre empresa");
	private AonCustomTextBox agreementHoursTB = new AonCustomTextBox("Horas convenio");
	private AonCustomTextBox agreementMinutesTB = new AonCustomTextBox("Minutos convenio");
	private AonCustomCheckBox repeatFDCB = new AonCustomCheckBox("¿Realiza trabajos fijos discontinuos o peri\u00f3dicos que se repiten en fechas ciertas?");
	private AonCustomListBox journeyTypeLB = new AonCustomListBox("Tipo de jornada");
	private AonCustomTextBox journeyDurationHoursTB = new AonCustomTextBox("Horas jornada");
	private AonCustomTextBox journeyDurationMinutesTB = new AonCustomTextBox("Minutos jornada");
	private AonCustomListBox teoricFormationLB = new AonCustomListBox("Formaci\u00f3n te\u00f3rica recibida");
	private AonCustomTextBox formationHoursTB = new AonCustomTextBox("Horas formaci\u00f3n");
	private AonCustomTextBox formationMinutesTB = new AonCustomTextBox("Minutos formaci\u00f3n");
	private AonCustomTextBox retirementPercentTB = new AonCustomTextBox("Porcentaje jubilaci\u00f3n (%)");
	private AonCustomCheckBox discCB = new AonCustomCheckBox("Indicador discontinuidad");
	private AonCustomListBox discReasonLB = new AonCustomListBox("Tipo discontinuidad");
	private AonCustomCheckBox trueDateCB = new AonCustomCheckBox("Periodo actividad sin fecha cierta");
	private AonCustomCheckBox planRecoveryCB = new AonCustomCheckBox("Acoge el Plan de Transformaci\u00f3n, Recuperaci\u00f3n y Resiliencia");
	private AonCustomCheckBox writenContractCB = new AonCustomCheckBox("Indicador contrato escrito");

	private AonCustomCard specificDataCard; // Datos especificos

	private AonCustomCheckBox workProgramDataCB = new AonCustomCheckBox("Datos de Programa de Empleo");
	private AonCustomCheckBox temporalWorkEnterpriseCB = new AonCustomCheckBox("Empresa de Trabajo Temporal");
	private AonCustomCheckBox contractReliefCB = new AonCustomCheckBox("Contrato Relevo");
	private AonCustomCheckBox offerWorkDataCB = new AonCustomCheckBox("Datos de la oferta de Trabajo");
	private AonCustomCheckBox workshopSchoolCB = new AonCustomCheckBox("Escuelas Taller");
	private AonCustomCheckBox disabilityCB = new AonCustomCheckBox("Discapacidad");
	private AonCustomCheckBox older52CB = new AonCustomCheckBox("Mayores de 52 a\u00f1os (Ley 45/2002)");
	private AonCustomCheckBox annexedCB = new AonCustomCheckBox("Anexo gesti\u00f3n colect. de contrataciones en origen");
	private AonCustomCheckBox campaignsCB = new AonCustomCheckBox("Campa\u00f1as");
	private AonCustomCheckBox investCB = new AonCustomCheckBox("Investigaci\u00f3n");
	private AonCustomCheckBox interimCauseCB = new AonCustomCheckBox("Causa de interinidad");
	private AonCustomCheckBox entrepreneurSupportCB = new AonCustomCheckBox("Apoyo emprendedores");
	private AonCustomCheckBox promotionMeasuresCB = new AonCustomCheckBox("Medidas fomento");
	private AonCustomCheckBox quoteReductionsCB = new AonCustomCheckBox("Reducci\u00f3n de cuotas");
	private AonCustomCheckBox bonusCB = new AonCustomCheckBox("Bonificaci\u00f3n");

	private AonCustomCard workProgramDataCard; // Datos Programa de Empleo

	private AonCustomListBox workProgramLB = new AonCustomListBox("Programa empleo");

	private AonCustomCard temporalWorkEnterpriseDataCard; // Empresa de Trabajo Temporal

	private AonCustomTextBox nifTB = new AonCustomTextBox("NIF");
	private AonCustomTextBox socialReasonTB = new AonCustomTextBox("Nombre/Raz\u00f3n Social");
	private AonCustomCheckBox contractTemplateCB = new AonCustomCheckBox("Contrato plantilla");
	private AonCustomCheckBox foreignEnterpriseCB = new AonCustomCheckBox("Empresa extranjera");

	private AonCustomCard contractReliefDataCard; // Contrato Relevo

	private AonCustomListBox reliefEmployeeLB = new AonCustomListBox("Tipo de trabajador relevo");
	private AonCustomTextBox retirementNameTB = new AonCustomTextBox("Trabajador jubilaci\u00f3n parcial (Nombre)");
	private AonCustomTextBox retirementSurnameTB = new AonCustomTextBox("Trabajador jubilaci\u00f3n parcial (1er Apellido)");
	private AonCustomTextBox retirementSurname2TB = new AonCustomTextBox("Trabajador jubilaci\u00f3n parcial (2\u00ba Apellido)");

	private AonCustomCard offerWorkDataCard; // Datos de la oferta de Trabajo

	private AonCustomTextBox offerTB = new AonCustomTextBox("Oferta");

	private AonCustomCard workshopSchoolDataCard; // Escuelas Taller

	private AonCustomListBox workshopSchoolLB = new AonCustomListBox("Escuelas taller");

	private AonCustomCard disabilityDataCard; // Discapacidad

	private AonCustomListBox disabilityLB = new AonCustomListBox("Indicador de discapacidad");
	private AonCustomListBox bonusColectiveDisabilityLB = new AonCustomListBox("Colectivo bonificaci\u00f3n");

	private AonCustomCard older52DataCard; // Mayores de 52 años (Ley 45/2002)

	private AonCustomListBox older52LB = new AonCustomListBox("Mayores de 52 a\u00f1os");

	private AonCustomCard annexedDataCard; // Anexo gestion colect. de contrataciones en origen

	private AonCustomListBox annexedLB = new AonCustomListBox("Oferta de empleo de car\u00e1cter");
	private AonCustomTextBox sourceYearTB = new AonCustomTextBox("A\u00f1o de la contrataci\u00f3n en origen");

	private AonCustomCard campaignsDataCard; // Campañas

	private AonCustomTextBox cpCampaignTB = new AonCustomTextBox("Comunidad aut\u00f3noma");
	private AonCustomTextBox codeCampaignTB = new AonCustomTextBox("C\u00f3digo campa\u00f1a");
	private AonCustomTextBox yearCampaignTB = new AonCustomTextBox("A\u00f1o campa\u00f1a");

	private AonCustomCard investDataCard; // Investigacion

	private AonCustomListBox employerLB = new AonCustomListBox("Empleador Contratante");
	private AonCustomListBox employeeLB = new AonCustomListBox("Trabajador Contratado");
	private AonCustomCheckBox researcherCB = new AonCustomCheckBox("Contrato de Investigador en Formaci\u00f3n (Real Decreto 63/2006)");

	private AonCustomCard interimCauseDataCard; // Causa de interinidad

	private AonCustomListBox interimCauseLB = new AonCustomListBox("Causa de interinidad");

	private AonCustomCard entrepreneurSupportDataCard; // Apoyo emprendedores

	private AonCustomListBox bonusColectiveLB = new AonCustomListBox("Colectivo bonificaci\u00f3n");
	private AonCustomCheckBox freelanceEmployeerCB = new AonCustomCheckBox("El empleador que contrata es aut\u00f3nomo");

	private AonCustomCard promotionMeasuresDataCard; // Medidas fomento

	private AonCustomCheckBox promotionPermanentHiringCB = new AonCustomCheckBox("Acogida a la ley de fomento de la contrataci\u00f3n indefinida");

	private AonCustomCard quoteReductionsDataCard; // Reduccion de cuotas

	private AonCustomListBox reductionColectiveLB = new AonCustomListBox("Colectivo de reducci\u00f3n");
	private AonCustomListBox quoteReductionLB = new AonCustomListBox("Porcentaje de reducci\u00f3n de cuotas");
	private AonCustomTextBox journeyPercentTB = new AonCustomTextBox("Porcentaje de jornada (% Min:50.00 - Max:75.00)");

	private AonCustomCard bonusDataCard; // Bonificación

	private AonCustomListBox bonusLB = new AonCustomListBox("Colectivo de bonificaci\u00f3n");

	// ------------------------------------------------------ Constructor
	// ---------------------------------------------------------

	private DomainEnterprisesServiceAsync impl = DomainEnterprisesServiceAsync.newInstance();
	private DomainEmployeesServiceAsync implEmployee = DomainEmployeesServiceAsync.newInstance();

	private com.esferalia.aon.gwt.payroll.shared.ContractSpecificData contractSpecificData;
	private EmployeeContractInfo contractEmployeeInfo;

	private Map<String, CNO> cnoMap;

	private boolean isTransform;
	private boolean isExtension;

	private FormativeLevel formativeLevel = new FormativeLevel();

	protected ContractSpecificDataWidget() {
		addStyleName(AON.CSS.aonItemFlex());
		addStyleName(AON.CSS.aonFlexColumn());
		getElement().getStyle().setProperty("gap", "1rem");
		getElement().getStyle().setProperty("margin", "1rem 0");

		cnoMap = new HashMap<>();
	}

	public void setEmployeeContractInfo(EmployeeContractInfo contractEmployeeInfo, Consumer<Void> finish) {
		this.contractEmployeeInfo = contractEmployeeInfo;
		this.isExtension = contractEmployeeInfo.getContractInfo().isHasExtension();
		checkTransformContract();

		reloadSepeData(finish);
	}

	private void checkTransformContract() {
		Integer contractTypeCode = AonStringUtils.isBlank(contractEmployeeInfo.getContractInfo().getContractType())
				? null
				: Integer.parseInt(contractEmployeeInfo.getContractInfo().getContractType());
		if (null == contractTypeCode)
			this.isTransform = false;
		else {
			ContractType contractType = new ContractType();
			ContractTypeRecord contractTypeRecord = contractType.getContractType(contractTypeCode);
			this.isTransform = null == contractTypeRecord ? false : contractTypeRecord.isTransform();
		}
	}

	private void reloadSepeData(Consumer<Void> finis) {
		showLoadingMessage("Cargando datos Sepe del contrato...");
		getCnos(cnos -> {
			getContractSpecificData(contractSpecificDataIn -> {
				contractSpecificData = contractSpecificDataIn;

				fillContractSpecificData();
				initializeView();

				hideMessagePanel();
				finis.accept(null);
			}, f -> showErrorMessage("Datos Sepe", f.getMessage()));
		});
	}

	private void fillContractSpecificData() {
		clear();

		FlowPanel table = createFlexPanel();
		table.getElement().getStyle().setProperty("justify-content", "center");
		table.getElement().getStyle().setProperty("gap", "1rem");
		
		createCnoCard();
		createUpdateSepeInfo();
		
		table.add(cnoCard);
		table.add(comunicationCard);
		add(table);

		if (Boolean.TRUE.equals(isTransform) || !AonStringUtils.isBlank(contractSpecificData.getTransformIde()))
			createUpdateTransformSepeInfo();

		if (Boolean.TRUE.equals(isExtension) || !contractSpecificData.getExtensions().isEmpty())
			createUpdateExtensionSepeInfo();

		createOtherDataCard();
		createSpecificDataCard();

		createWorkProgramDataCard();
		createTemporalWorkEnterpriseDataCard();
		createContractReliefDataCard();
		createOfferWorkDataCard();
		createWorkshopSchoolDataCard();
		createDisabilityDataCard();
		createOlder52DataCard();
		createAnnexedDataCard();
		createCampaignsDataCard();
		createInvestDataCard();
		createInterimCauseDataCard();
		createEntrepreneurSupportDataCard();
		createPromotionMeasuresDataCard();
		createQuoteReductionsDataCard();
		createBonusDataCard();
	}

	private void createCnoCard() {
		cnoCard = createCard("C\u00f3digo Nacional de Ocupaci\u00f3n", createCnoData());
		addCnoDataHandlers();
	}

	private Widget createCnoData() {
		FlowPanel table = createFlexColumnPanel();

		table.add(createRow(cnoSB, null));

		cnoSB.setValue(null);

		initializeCnoSuggest();

		CNO cno = cnoMap.get(contractSpecificData.getCno());
		if (null != cno)
			cnoSB.setValue(cno.getCode() + " - " + cno.getTitle());

		return table;
	}

	private void initializeCnoSuggest() {
		List<String> cnoEntry = new ArrayList<>();
		for (Entry<String, CNO> entry : cnoMap.entrySet())
			cnoEntry.add(entry.getKey() + " - " + entry.getValue().getTitle());

		List<String> cnoSuggest = new ArrayList<>();
		for (String cno : cnoEntry)
			cnoSuggest.add(cno);
		MultiWordSuggestOracle orclCnos = (MultiWordSuggestOracle) cnoSB.getSuggestBox().getSuggestOracle();
		orclCnos.addAll(cnoSuggest);
		cnoSB.setAutoSelectEnabled(true);
	}

	private void addCnoDataHandlers() {
		cnoSB.getSuggestBox().addSelectionHandler(e -> contractSpecificData.setCno(AonStringUtils.isBlank(cnoSB.getValue()) ? "" : cnoSB.getValue().split(" -")[0]));
	}

	private void createUpdateSepeInfo() {
		comunicationCard = new AonCustomCard("Comunicaci\u00f3n Sepe");
		comunicationCard.addStyleName(AON.CSS.aonContractSmallCard());
		comunicationCard.add(createComunicationData());
		addComunicationDataHadlers();
	}

	private Widget createComunicationData() {
		FlowPanel table = createFlexColumnPanel();

		table.add(createRow(ideTB, comunicationDateBx));

		ideTB.setValue("");
		comunicationDateBx.setValue(null);

		ideTB.setValue(contractSpecificData.getIde());
		comunicationDateBx.setValue(contractSpecificData.getComunicationDate());

		return table;
	}

	private void addComunicationDataHadlers() {
		ideTB.addValueChangeHandler(e -> contractSpecificData.setIde(ideTB.getValue()));
		comunicationDateBx.addValueChangeHandler(e -> contractSpecificData.setComunicationDate(comunicationDateBx.getValue()));
	}

	private void createUpdateTransformSepeInfo() {
		comunicationTransformCard = new AonCustomCard("Comunicaci\u00f3n Transformaci\u00f3n Sepe");
		comunicationTransformCard.addStyleName(AON.CSS.aonContractMediumCard());
		comunicationTransformCard.add(createComunicationTransformData());
		addComunicationTransformDataHadlers();

		add(comunicationTransformCard);
	}

	private Widget createComunicationTransformData() {
		FlowPanel table = createFlexColumnPanel();

		table.add(createRow(ideTransformTB, comunicationTransformDateBx));

		ideTransformTB.setValue("");
		comunicationTransformDateBx.setValue(null);

		ideTransformTB.setValue(contractSpecificData.getTransformIde());
		comunicationTransformDateBx.setValue(contractSpecificData.getComunicationTransformDate());

		return table;
	}

	private void addComunicationTransformDataHadlers() {
		ideTransformTB.addValueChangeHandler(e -> contractSpecificData.setTransformIde(ideTransformTB.getValue()));
		comunicationTransformDateBx.addValueChangeHandler(e -> contractSpecificData.setComunicationTransformDate(e.getValue()));
	}

	private void createUpdateExtensionSepeInfo() {
		comunicationExtensionCard = new AonCustomCard("Comunicaci\u00f3n Pr\u00f3rroga Sepe");
		comunicationExtensionCard.addStyleName(AON.CSS.aonContractMediumCard());
		comunicationExtensionCard.add(createComunicationExtensionData());
		addComunicationExtensionDataHadlers();

		add(comunicationExtensionCard);
	}

	private Widget createComunicationExtensionData() {
		FlowPanel table = createFlexColumnPanel();

		table.add(createRow(extensionsLB, null));
		table.add(createRow(ideExtensionTB, comunicationExtensionDateBx));

		extensionsLB.clearItems();
		ideExtensionTB.setValue("");
		comunicationExtensionDateBx.setValue(null);

		initializeExtensionLB();

		if (!contractSpecificData.getExtensions().isEmpty()) {
			Entry<String, Date> entry = (Entry<String, Date>) contractSpecificData.getExtensions().entrySet().toArray()[0];
			ideExtensionTB.setValue(entry.getKey());
			comunicationExtensionDateBx.setValue(entry.getValue());
		} else {
			ideTransformTB.setValue(contractSpecificData.getTransformIde());
			comunicationTransformDateBx.setValue(contractSpecificData.getComunicationTransformDate());
		}

		return table;
	}

	private void initializeExtensionLB() {
		int extension = 1;
		extensionsLB.clearItems();
		for (String extensionIde : contractSpecificData.getExtensions().keySet()) {
			extensionsLB.addItem("Pr\u00f3rroga " + extension, extensionIde);
			extension++;
		}
	}

	private void addComunicationExtensionDataHadlers() {
		ideExtensionTB.addValueChangeHandler(e -> {
			Date extensionDate = contractSpecificData.getExtensions().get(ideExtensionTB.getValue());
			if (null == extensionDate) {
				Date newExtensionDate = comunicationExtensionDateBx.getValue();
				contractSpecificData.addExtension(ideExtensionTB.getValue(),
						null == newExtensionDate ? new Date() : newExtensionDate);
			}
		});

		extensionsLB.addChangeHandler(e -> {
			Date extensionDate = contractSpecificData.getExtensions().get(extensionsLB.getValue());
			ideExtensionTB.setValue(extensionsLB.getValue());
			comunicationExtensionDateBx.setValue(extensionDate);
		});
	}

	private void createOtherDataCard() {
		otherDataCard = new AonCustomCard("Otros datos contrato");
		otherDataCard.addStyleName(AON.CSS.aonContractMediumCard());
		otherDataCard.add(createOtherData());
		addOtherDataHandlers();

		add(otherDataCard);
	}

	private Widget createOtherData() {
		FlowPanel table = createFlexColumnPanel();

		journeyDurationHoursTB.getTextBox().setMaxLength(4);
		journeyDurationMinutesTB.getTextBox().setMaxLength(2);
		
		formativeLevelLB.getElement().getStyle().setProperty("max-width", "32rem");
		academicTitulationLB.getElement().getStyle().setProperty("max-width", "32rem");

		table.add(createRow(calendarFormativeStartDate, calendarFormativeEndDate));
		table.add(createRow(formativeLevelLB, academicTitulationLB));
		table.add(createRow(profesionalityCB, legalRepresentativeTB));
		table.add(createRow(signBasicCopyLB, null));
		table.add(createRow(basicCopyTA, null));
		table.add(createRow(useEnterpriseFreeTB, null));
		table.add(createRow(agreementHoursTB, agreementMinutesTB));
		table.add(createRow(repeatFDCB, journeyTypeLB));
		table.add(createRow(journeyDurationHoursTB, journeyDurationMinutesTB));
		table.add(createRow(retirementPercentTB, teoricFormationLB));
		table.add(createRow(formationHoursTB, formationMinutesTB));
		table.add(createRow(discCB, discReasonLB));
		table.add(createRow(trueDateCB, planRecoveryCB));
		table.add(createRow(writenContractCB, null));

		calendarFormativeStartDate.setValue(null);
		calendarFormativeEndDate.setValue(null);
		formativeLevelLB.clearItems();
		academicTitulationLB.clearItems();
		profesionalityCB.setValue(false);
		legalRepresentativeTB.setValue(null);
		signBasicCopyLB.clearItems();
		basicCopyTA.setValue(null);
		useEnterpriseFreeTB.setValue(null);
		agreementHoursTB.setValue(null);
		agreementMinutesTB.setValue(null);
		repeatFDCB.setValue(false);
		journeyTypeLB.clearItems();
		journeyDurationHoursTB.setValue(null);
		journeyDurationMinutesTB.setValue(null);
		retirementPercentTB.setValue(null);
		teoricFormationLB.clearItems();
		formationHoursTB.setValue(null);
		formationMinutesTB.setValue(null);
		discCB.setValue(false);
		discReasonLB.clearItems();
		trueDateCB.setValue(false);
		planRecoveryCB.setValue(false);
		writenContractCB.setValue(false);

		formativeLevelLB.clearItems();
		formativeLevelLB.addItem("-", "");
		for (Entry<String, String> entry : formativeLevel.getFormativeLevelMap().entrySet())
			formativeLevelLB.addItem(entry.getValue(), entry.getKey());

		signBasicCopyLB.clearItems();
		signBasicCopyLB.addItem("-", "");
		signBasicCopyLB.addItem("FIRMADAS POR LOS REPRESENTANTES LEGALES", "1");
		signBasicCopyLB.addItem("NO EXISTE REPRESENTACION LEGAL", "2");
		signBasicCopyLB.addItem("NO SE HA FACILITADO COPIA", "3");
		signBasicCopyLB.addItem("REHUSA FIRMAR", "4");

		createJourneyType();

		teoricFormationLB.addItem("-", "");
		teoricFormationLB.addItem("Si", "true");
		teoricFormationLB.addItem("No", "false");

		discReasonLB.addItem("-", "");
		discReasonLB.addItem("INCAPACIDAD TRANSITORIA", "I");
		discReasonLB.addItem("PRORROGA TACITA", "P");

		calendarFormativeStartDate.setValue(contractSpecificData.getCalendarFormativeStartDate());
		calendarFormativeEndDate.setValue(contractSpecificData.getCalendarFormativeEndDate());
		formativeLevelLB.setValue(contractSpecificData.getFormativeLevel());
		Map<String, String> academicTitulations = AcademicTitulation.getAcademicTitulations(contractSpecificData.getFormativeLevel());
		createAcademicTitulationLB(academicTitulations);
		academicTitulationLB.setValue(contractSpecificData.getAcademicTitulation());
		profesionalityCB.setValue(contractSpecificData.getProfesionality() != null && contractSpecificData.getProfesionality());
		legalRepresentativeTB.setValue(contractSpecificData.getLegalRepresentative());
		signBasicCopyLB.setValue(contractSpecificData.getSignBasicCopy());
		basicCopyTA.setValue(contractSpecificData.getBasicCopy());
		useEnterpriseFreeTB.setValue(contractSpecificData.getUseEnterpriseFree());
		agreementHoursTB.setValue(contractSpecificData.getAgreementHours());
		agreementMinutesTB.setValue(contractSpecificData.getAgreementMinutes());
		repeatFDCB.setValue(contractSpecificData.getRepeatFD() != null && contractSpecificData.getRepeatFD());
		journeyTypeLB.setValue(contractSpecificData.getJourneyType());
		journeyDurationHoursTB.setValue(contractSpecificData.getJourneyDurationHours());
		journeyDurationMinutesTB.setValue(contractSpecificData.getJourneyDurationMinutes());
		retirementPercentTB.setValue(contractSpecificData.getRetirementPercent());
		teoricFormationLB.setValue(null != contractSpecificData.getTeoricFormation() && contractSpecificData.getTeoricFormation() ? "true" : "false");
		formationHoursTB.setValue(contractSpecificData.getFormationHours());
		formationMinutesTB.setValue(contractSpecificData.getFormationMinutes());
		discCB.setValue(contractSpecificData.getDisc() != null && contractSpecificData.getDisc());
		discReasonLB.setValue(contractSpecificData.getDiscReason());
		trueDateCB.setValue(contractSpecificData.getTrueDate() != null && contractSpecificData.getTrueDate());
		planRecoveryCB.setValue(contractSpecificData.getPlanRecovery() != null && contractSpecificData.getPlanRecovery());
		writenContractCB.setValue(contractSpecificData.getWritenContract() != null && contractSpecificData.getWritenContract());

		return table;
	}

	private void createJourneyType() {
		String contractType = contractEmployeeInfo.getContractInfo().getContractType();

		journeyTypeLB.addItem("-", "");

		if (AonStringUtils.equalsIgnoreCase(contractType, "300") || AonStringUtils.equalsIgnoreCase(contractType, "309")
				|| AonStringUtils.equalsIgnoreCase(contractType, "330")
				|| AonStringUtils.equalsIgnoreCase(contractType, "339")
				|| AonStringUtils.equalsIgnoreCase(contractType, "350")
				|| AonStringUtils.equalsIgnoreCase(contractType, "389"))
			journeyTypeLB.addItem("JORNADA ANUAL", "A");
		else {
			journeyTypeLB.addItem("JORNADA ANUAL", "A");
			journeyTypeLB.addItem("JORNADA MENSUAL", "M");
			journeyTypeLB.addItem("JORNADA SEMANAL", "S");
			journeyTypeLB.addItem("JORNADA DIARIA", "D");
		}
	}

	private void createAcademicTitulationLB(Map<String, String> academicTitulations) {
		academicTitulationLB.clearItems();
		for (Entry<String, String> entry : academicTitulations.entrySet()) {
			academicTitulationLB.addItem(entry.getValue(), entry.getKey());
		}
	}

	private void addOtherDataHandlers() {
		calendarFormativeStartDate.addValueChangeHandler(e -> contractSpecificData.setCalendarFormativeStartDate(calendarFormativeStartDate.getValue()));
		calendarFormativeEndDate.addValueChangeHandler(e -> contractSpecificData.setCalendarFormativeEndDate(calendarFormativeEndDate.getValue()));
		formativeLevelLB.addChangeHandler(e -> {
			String formativeLevelValue = formativeLevelLB.getValue();
			Map<String, String> academicTitulations = AcademicTitulation.getAcademicTitulations(formativeLevelValue);
			createAcademicTitulationLB(academicTitulations);

			String academicTitulationValue = academicTitulationLB.getValue();

			contractSpecificData.setFormativeLevel(formativeLevelValue);
			contractSpecificData.setAcademicTitulation(academicTitulationValue);
		});
		academicTitulationLB.addChangeHandler(e -> contractSpecificData.setAcademicTitulation(academicTitulationLB.getValue()));
		profesionalityCB.addValueChangeHandler(e -> contractSpecificData.setProfesionality(profesionalityCB.getValue()));
		legalRepresentativeTB.addValueChangeHandler(e -> contractSpecificData.setLegalRepresentative(legalRepresentativeTB.getValue()));
		signBasicCopyLB.addChangeHandler(e -> contractSpecificData.setSignBasicCopy(signBasicCopyLB.getValue()));
		basicCopyTA.addValueChangeHandler(e -> contractSpecificData.setBasicCopy(basicCopyTA.getValue()));
		useEnterpriseFreeTB.addValueChangeHandler(e -> contractSpecificData.setUseEnterpriseFree(useEnterpriseFreeTB.getValue()));
		agreementHoursTB.addValueChangeHandler(e -> contractSpecificData.setAgreementHours(agreementHoursTB.getValue()));
		agreementMinutesTB.addValueChangeHandler(e -> contractSpecificData.setAgreementMinutes(agreementMinutesTB.getValue()));
		repeatFDCB.addValueChangeHandler(e -> contractSpecificData.setRepeatFD(repeatFDCB.getValue()));
		journeyTypeLB.addChangeHandler(e -> contractSpecificData.setJourneyType(journeyTypeLB.getValue()));
		journeyDurationHoursTB.addValueChangeHandler(e -> contractSpecificData.setJourneyDurationHours(journeyDurationHoursTB.getValue()));
		journeyDurationMinutesTB.addValueChangeHandler(e -> contractSpecificData.setJourneyDurationMinutes(journeyDurationMinutesTB.getValue()));
		teoricFormationLB.addChangeHandler(e -> contractSpecificData.setTeoricFormation(AonStringUtils.isBlank(teoricFormationLB.getValue()) ? false : Boolean.parseBoolean(teoricFormationLB.getValue())));
		formationHoursTB.addValueChangeHandler(e -> contractSpecificData.setFormationHours(formationHoursTB.getValue()));
		formationMinutesTB.addValueChangeHandler(e -> contractSpecificData.setFormationMinutes(formationMinutesTB.getValue()));
		retirementPercentTB.addValueChangeHandler(e -> contractSpecificData.setRetirementPercent(retirementPercentTB.getValue()));
		discCB.addValueChangeHandler(e -> contractSpecificData.setDisc(discCB.getValue()));
		discReasonLB.addChangeHandler(e -> contractSpecificData.setDiscReason(discReasonLB.getValue()));
		trueDateCB.addValueChangeHandler(e -> contractSpecificData.setTrueDate(trueDateCB.getValue()));
		planRecoveryCB.addValueChangeHandler(e -> contractSpecificData.setPlanRecovery(planRecoveryCB.getValue()));
		writenContractCB.addValueChangeHandler(e -> contractSpecificData.setWritenContract(writenContractCB.getValue()));
	}

	private void createSpecificDataCard() {
		specificDataCard = new AonCustomCard("Datos especificos");
		specificDataCard.addStyleName(AON.CSS.aonContractMediumCard());
		specificDataCard.add(createSpecificData());
		addSpecificDataHandlers();

		add(specificDataCard);
	}

	private Widget createSpecificData() {
		FlowPanel table = createFlexColumnPanel();
		
		List<Widget> widgets = new ArrayList<>();
		widgets.add(workProgramDataCB);
		widgets.add(temporalWorkEnterpriseCB);
		widgets.add(disabilityCB);
		widgets.add(older52CB);
		widgets.add(annexedCB);
		widgets.add(contractReliefCB);
		widgets.add(offerWorkDataCB);
		widgets.add(workshopSchoolCB);
		widgets.add(campaignsCB);
		widgets.add(investCB);
		widgets.add(interimCauseCB);
		widgets.add(entrepreneurSupportCB);
		widgets.add(promotionMeasuresCB);
		widgets.add(quoteReductionsCB);
		widgets.add(bonusCB);
		
		FlowPanel wrapRow = createWrapRow(widgets);
		
		table.add(wrapRow);

		workProgramDataCB.setValue(false);
		temporalWorkEnterpriseCB.setValue(false);
		contractReliefCB.setValue(false);
		offerWorkDataCB.setValue(false);
		workshopSchoolCB.setValue(false);
		disabilityCB.setValue(false);
		older52CB.setValue(false);
		annexedCB.setValue(false);
		campaignsCB.setValue(false);
		investCB.setValue(false);
		interimCauseCB.setValue(false);
		entrepreneurSupportCB.setValue(false);
		promotionMeasuresCB.setValue(false);
		quoteReductionsCB.setValue(false);
		bonusCB.setValue(false);

		return table;
	}

	private void addSpecificDataHandlers() {
		workProgramDataCB.addValueChangeHandler(e -> {
			showHideElement(workProgramDataCard, Boolean.TRUE.equals(workProgramDataCB.getValue()));
			contractSpecificData.setWorkProgramData(workProgramDataCB.getValue());
			contractSpecificData.setWorkProgram(workProgramDataCB.getValue() ? workProgramLB.getValue() : null);
		});

		temporalWorkEnterpriseCB.addValueChangeHandler(e -> {
			showHideElement(temporalWorkEnterpriseDataCard, Boolean.TRUE.equals(temporalWorkEnterpriseCB.getValue()));
			contractSpecificData.setTemporalWorkEnterprise(temporalWorkEnterpriseCB.getValue());
		});

		contractReliefCB.addValueChangeHandler(e -> {
			showHideElement(contractReliefDataCard, Boolean.TRUE.equals(contractReliefCB.getValue()));
			contractSpecificData.setContractRelief(contractReliefCB.getValue());
			contractSpecificData.setReliefEmployee(contractReliefCB.getValue() ? reliefEmployeeLB.getValue() : null);
		});

		offerWorkDataCB.addValueChangeHandler(e -> {
			showHideElement(offerWorkDataCard, Boolean.TRUE.equals(offerWorkDataCB.getValue()));
			contractSpecificData.setOfferWorkData(offerWorkDataCB.getValue());
		});

		workshopSchoolCB.addValueChangeHandler(e -> {
			showHideElement(workProgramDataCard, Boolean.TRUE.equals(workshopSchoolCB.getValue()));
			contractSpecificData.setWorkshopSchoolB(workshopSchoolCB.getValue());
			contractSpecificData.setWorkshopSchool(workshopSchoolCB.getValue() ? workshopSchoolLB.getValue() : null);
		});

		disabilityCB.addValueChangeHandler(e -> {
			showHideElement(disabilityDataCard, Boolean.TRUE.equals(disabilityCB.getValue()));
			contractSpecificData.setDisabilityB(disabilityCB.getValue());
			contractSpecificData.setDisability(disabilityCB.getValue() ? disabilityLB.getValue() : null);
			contractSpecificData.setBonusColective(disabilityCB.getValue() ? bonusColectiveDisabilityLB.getValue() : null);
		});

		older52CB.addValueChangeHandler(e -> {
			showHideElement(older52DataCard, Boolean.TRUE.equals(older52CB.getValue()));
			contractSpecificData.setOlderThan52(older52CB.getValue());
		});

		annexedCB.addValueChangeHandler(e -> {
			showHideElement(annexedDataCard, Boolean.TRUE.equals(annexedCB.getValue()));
			contractSpecificData.setAnnexedB(annexedCB.getValue());
		});

		campaignsCB.addValueChangeHandler(e -> {
			showHideElement(campaignsDataCard, Boolean.TRUE.equals(campaignsCB.getValue()));
			contractSpecificData.setCampaigns(campaignsCB.getValue());
		});

		investCB.addValueChangeHandler(e -> {
			showHideElement(investDataCard, Boolean.TRUE.equals(investCB.getValue()));
			contractSpecificData.setInvest(investCB.getValue());
			contractSpecificData.setEmployer(investCB.getValue() ? employerLB.getValue() : null);
			contractSpecificData.setEmployee(investCB.getValue() ? employeeLB.getValue() : null);
		});

		interimCauseCB.addValueChangeHandler(e -> {
			showHideElement(interimCauseDataCard, Boolean.TRUE.equals(interimCauseCB.getValue()));
			contractSpecificData.setIsSustitucionCause(interimCauseCB.getValue());
			contractSpecificData.setSustitucionCause(interimCauseCB.getValue() ? interimCauseLB.getValue() : null);
		});

		entrepreneurSupportCB.addValueChangeHandler(e -> {
			showHideElement(entrepreneurSupportDataCard, Boolean.TRUE.equals(entrepreneurSupportCB.getValue()));
			contractSpecificData.setEntrepreneurSupport(entrepreneurSupportCB.getValue());
			contractSpecificData.setBonusColective(entrepreneurSupportCB.getValue() ? bonusColectiveLB.getValue() : null);
		});

		promotionMeasuresCB.addValueChangeHandler(e -> {
			showHideElement(promotionMeasuresDataCard, Boolean.TRUE.equals(promotionMeasuresCB.getValue()));
			contractSpecificData.setPromotionMeasures(promotionMeasuresCB.getValue());
		});

		quoteReductionsCB.addValueChangeHandler(e -> {
			showHideElement(quoteReductionsDataCard, Boolean.TRUE.equals(quoteReductionsCB.getValue()));
			contractSpecificData.setQuoteReductions(quoteReductionsCB.getValue());
			contractSpecificData.setReductionColective(quoteReductionsCB.getValue() ? reductionColectiveLB.getValue() : null);
			contractSpecificData.setQuoteReduction(quoteReductionsCB.getValue() ? Boolean.parseBoolean(quoteReductionLB.getValue()) : null);
			
		});

		bonusCB.addValueChangeHandler(e -> {
			showHideElement(bonusDataCard, Boolean.TRUE.equals(bonusCB.getValue()));
			contractSpecificData.setBonus(bonusCB.getValue());
		});
	}

	private void createWorkProgramDataCard() {
		workProgramDataCard = new AonCustomCard("Datos Programa de Empleo");
		workProgramDataCard.addStyleName(AON.CSS.aonContractMediumCard());
		workProgramDataCard.add(createWorkProgramData());
		addWorkProgramDataHandlers();

		add(workProgramDataCard);
	}

	private Widget createWorkProgramData() {
		FlowPanel table = createFlexColumnPanel();

		table.add(createRow(workProgramLB, null));

		workProgramLB.clearItems();

		workProgramLB.addItem("FOMENTO EMPLEO AGRARIO", "01");
		workProgramLB.addItem("INSERCION CORPORACION LOCAL", "02");
		workProgramLB.addItem("INSERCION (ORGANO ADMINISTRACION ESTADO)", "03");
		workProgramLB.addItem("INSERCION (COMUNIDAD AUTONOMA)", "04");
		workProgramLB.addItem("INSERCION (ENTIDAD SIN ANIMO DE LUCRO)", "05");
		workProgramLB.addItem("INSERCION (UNIVERSIDAD)", "06");
		workProgramLB.addItem("SUBSIDIO AGRARIO (ORGANISMO INVERSORES)", "07");
		workProgramLB.addItem("AGENTES DE EMPLEO Y DESARROLLO LOCAL", "08");
		workProgramLB.addItem("ESTUDIOS Y CAMPA\u00D1AS", "09");
		workProgramLB.addItem("PROGRAMA DE EMPLEO I+E", "10");
		workProgramLB.addItem("INTERES SOCIAL (CORPORACION LOCAL)", "12");
		workProgramLB.addItem("INTERES SOCIAL (ORGANOS AD. ESTADO O CCAA)", "13");
		workProgramLB.addItem("INTERES SOCIAL (COMUNIDAD AUTONOMA)", "14");
		workProgramLB.addItem("INTERES SOCIAL (ENTIDAD SIN ANIMO DE LUCRO)", "15");
		workProgramLB.addItem("INTERES SOCIAL (UNIVERSIDAD)", "16");

		if (Boolean.TRUE.equals(contractSpecificData.getWorkProgramData())) {
			workProgramDataCB.setValue(true);
			workProgramLB.setValue(contractSpecificData.getWorkProgram());
		} else showHideElement(workProgramDataCard, false);

		return table;
	}

	private void addWorkProgramDataHandlers() {
		workProgramLB.addChangeHandler(e -> contractSpecificData.setWorkProgram(workProgramLB.getValue()));
	}

	private void createTemporalWorkEnterpriseDataCard() {
		temporalWorkEnterpriseDataCard = new AonCustomCard("Empresa de Trabajo Temporal");
		temporalWorkEnterpriseDataCard.addStyleName(AON.CSS.aonContractMediumCard());
		temporalWorkEnterpriseDataCard.add(createTemporalWorkEnterpriseData());
		addTemporalWorkEnterpriseDataHandlers();

		add(temporalWorkEnterpriseDataCard);
	}

	private Widget createTemporalWorkEnterpriseData() {
		FlowPanel table = createFlexColumnPanel();

		table.add(createRow(nifTB, socialReasonTB));
		table.add(createRow(contractTemplateCB, foreignEnterpriseCB));

		nifTB.setValue(null);
		socialReasonTB.setValue(null);
		contractTemplateCB.setValue(false);
		foreignEnterpriseCB.setValue(false);

		// TemporalWorkEnterpriseDataTable
		if (Boolean.TRUE.equals(this.contractSpecificData.getTemporalWorkEnterprise())) {
			temporalWorkEnterpriseCB.setValue(true);
			nifTB.setValue(contractSpecificData.getNif());
			socialReasonTB.setValue(contractSpecificData.getSocialReason());
			contractTemplateCB.setValue(contractSpecificData.getContractTemplate());
			foreignEnterpriseCB.setValue(contractSpecificData.getForeignEnterprise());
		} else showHideElement(temporalWorkEnterpriseDataCard, false);

		return table;
	}

	private void addTemporalWorkEnterpriseDataHandlers() {
		nifTB.addValueChangeHandler(e -> contractSpecificData.setNif(nifTB.getValue()));
		socialReasonTB.addValueChangeHandler(e -> contractSpecificData.setSocialReason(socialReasonTB.getValue()));
		contractTemplateCB.addValueChangeHandler(e -> contractSpecificData.setContractTemplate(contractTemplateCB.getValue()));
		foreignEnterpriseCB.addValueChangeHandler(e -> contractSpecificData.setForeignEnterprise(foreignEnterpriseCB.getValue()));
	}

	private void createContractReliefDataCard() {
		contractReliefDataCard = new AonCustomCard("Contrato Relevo");
		contractReliefDataCard.addStyleName(AON.CSS.aonContractMediumCard());
		contractReliefDataCard.add(createContractReliefData());
		addContractReliefDataHandlers();

		add(contractReliefDataCard);
	}

	private Widget createContractReliefData() {
		FlowPanel table = createFlexColumnPanel();

		table.add(createRow(reliefEmployeeLB, retirementNameTB));
		table.add(createRow(retirementSurnameTB, retirementSurname2TB));

		reliefEmployeeLB.clearItems();
		retirementNameTB.setValue(null);
		retirementSurnameTB.setValue(null);
		retirementSurname2TB.setValue(null);

		reliefEmployeeLB.addItem("TRABAJADOR INSCRITO COMO DEMANDANTE", "1");
		reliefEmployeeLB.addItem("TRABAJADOR CON CONTRATO DURACION DETERMINADA", "2");

		if (Boolean.TRUE.equals(contractSpecificData.getContractRelief())) {
			contractReliefCB.setValue(true);
			reliefEmployeeLB.setValue(contractSpecificData.getReliefEmployee());
			retirementNameTB.setValue(contractSpecificData.getRetirementName());
			retirementSurnameTB.setValue(contractSpecificData.getRetirementSurname());
			retirementSurname2TB.setValue(contractSpecificData.getRetirementSurname2());
		} else showHideElement(contractReliefDataCard, false);

		return table;
	}

	private void addContractReliefDataHandlers() {
		reliefEmployeeLB.addChangeHandler(e -> contractSpecificData.setReliefEmployee(reliefEmployeeLB.getValue()));
		retirementNameTB.addValueChangeHandler(e -> contractSpecificData.setRetirementName(retirementNameTB.getValue()));
		retirementSurnameTB.addValueChangeHandler(e -> contractSpecificData.setRetirementSurname(retirementSurnameTB.getValue()));
		retirementSurname2TB.addValueChangeHandler(e -> contractSpecificData.setRetirementSurname2(retirementSurname2TB.getValue()));
	}

	private void createOfferWorkDataCard() {
		offerWorkDataCard = new AonCustomCard("Datos Oferta de Trabajo");
		offerWorkDataCard.addStyleName(AON.CSS.aonContractMediumCard());
		offerWorkDataCard.add(createOfferWorkData());
		addOfferWorkDataHandlers();

		add(offerWorkDataCard);
	}

	private Widget createOfferWorkData() {
		FlowPanel table = createFlexColumnPanel();

		table.add(createRow(offerTB, null));

		offerTB.setValue(null);

		if (Boolean.TRUE.equals(contractSpecificData.getOfferWorkData())) {
			offerWorkDataCB.setValue(true);
			offerTB.setValue(contractSpecificData.getOffer());
		} else showHideElement(offerWorkDataCard, false);

		return table;
	}

	private void addOfferWorkDataHandlers() {
		offerTB.addValueChangeHandler(e -> contractSpecificData.setOffer(offerTB.getValue()));
	}

	private void createWorkshopSchoolDataCard() {
		workshopSchoolDataCard = new AonCustomCard("Escuelas Taller");
		workshopSchoolDataCard.addStyleName(AON.CSS.aonContractMediumCard());
		workshopSchoolDataCard.add(createWorkshopSchoolData());
		addWorkshopSchoolDataHandlers();

		add(workshopSchoolDataCard);
	}

	private Widget createWorkshopSchoolData() {
		FlowPanel table = createFlexColumnPanel();

		table.add(createRow(workshopSchoolLB, null));

		workshopSchoolLB.clearItems();
		workshopSchoolLB.addItem("CONTRATO TALLERES EMPLEO ALUMNO/TRABAJADOR", "E01");
		workshopSchoolLB.addItem("CONTRATO TALLERES DE EMPLEO PERSONAL", "E02");
		workshopSchoolLB.addItem("CONTRATO FORMACION DUAL ALUMNO/TRABAJADOR", "F01");
		workshopSchoolLB.addItem("CONTRATO  FORMACION DUAL PERSONAL", "F02");
		workshopSchoolLB.addItem("CASA DE OFICIO ALUMNO/TRABAJADOR", "O01");
		workshopSchoolLB.addItem("CASA DE OFICIO PERSONAL", "O02");
		workshopSchoolLB.addItem("CONTRATO ESCUALA TALLER ALUMNO/TRABAJADOR", "T01");
		workshopSchoolLB.addItem("CONTRATO ESCUALA TALLER PERSONAL UPD", "T02");

		if (Boolean.TRUE.equals(contractSpecificData.getWorkshopSchoolB())) {
			workshopSchoolCB.setValue(true);
			workshopSchoolLB.setValue(contractSpecificData.getWorkshopSchool());
		} else showHideElement(workshopSchoolDataCard, false);

		return table;
	}

	private void addWorkshopSchoolDataHandlers() {
		workshopSchoolLB.addChangeHandler(e -> contractSpecificData.setWorkshopSchool(workshopSchoolLB.getValue()));
	}

	private void createDisabilityDataCard() {
		disabilityDataCard = new AonCustomCard("Discapacidad");
		disabilityDataCard.addStyleName(AON.CSS.aonContractMediumCard());
		disabilityDataCard.add(createDisabilityData());
		addDisabilityDataHandlers();

		add(disabilityDataCard);
	}

	private Widget createDisabilityData() {
		FlowPanel table = createFlexColumnPanel();

		table.add(createRow(disabilityLB, bonusColectiveDisabilityLB));

		disabilityLB.clearItems();
		disabilityLB.addItem("DISCAPACITADOS EN CENTROS ESPECIALES DE EMPLEO", "C");
		disabilityLB.addItem("ENCLAVES LABORALES DISC. INTELECT. >= 33%", "E");
		disabilityLB.addItem("ENCLAVES LABORALES DISC. FIS./SENS. >= 65%", "F");
		disabilityLB.addItem("ENCLAVES LABORALES MUJERES DISC. >= 33%", "G");
		disabilityLB.addItem("DISCAPACITADOS", "S");

		bonusColectiveDisabilityLB.clearItems();
		bonusColectiveDisabilityLB.addItem("HOMBRE SIN DISCAPACIDAD SEVERA < 45 A\u00D1OS CON CONTRATO INDEFINIDO", "070");
		bonusColectiveDisabilityLB.addItem("HOMBRE SIN DISCAPACIDAD SEVERA >=45 A\u00D1OS CON CONTRATO INDEFINIDO", "071");
		bonusColectiveDisabilityLB.addItem("HOMBRE CON DISCAPACIDAD SEVERA < 45 A\u00D1OS CON CONTRATO INDEFINIDO", "072");
		bonusColectiveDisabilityLB.addItem("HOMBRE CON DISCAPACIDAD SEVERA >=45 A\u00D1OS CON CONTRATO INDEFINIDO", "073");
		bonusColectiveDisabilityLB.addItem("MUJER SIN DISCAPACIDAD SEVERA < 45 A\u00D1OS CON CONTRATO INDEFINIDO", "074");
		bonusColectiveDisabilityLB.addItem("MUJER SIN DISCAPACIDAD SEVERA >=45 A\u00D1OS CON CONTRATO INDEFINIDO", "075");
		bonusColectiveDisabilityLB.addItem("MUJER CON DISCAPACIDAD SEVERA < 45 A\u00D1OS CON CONTRATO INDEFINIDO", "076");
		bonusColectiveDisabilityLB.addItem("MUJER CON DISCAPACIDAD SEVERA >=45 A\u00D1OS CON CONTRATO INDEFINIDO", "077");

		if (Boolean.TRUE.equals(contractSpecificData.getDisabilityB())) {
			disabilityCB.setValue(true);
			disabilityLB.setValue(contractSpecificData.getDisability());
			bonusColectiveDisabilityLB.setValue(contractSpecificData.getBonusColective());
		} else showHideElement(disabilityDataCard, false);

		return table;
	}

	private void addDisabilityDataHandlers() {
		disabilityLB.addChangeHandler(e -> contractSpecificData.setDisability(disabilityLB.getValue()));
		bonusColectiveDisabilityLB.addChangeHandler(e -> contractSpecificData.setBonusColective(bonusColectiveDisabilityLB.getValue()));
	}

	private void createOlder52DataCard() {
		older52DataCard = new AonCustomCard("Mayores de 52 a\u00f1os (Ley 45/2002)");
		older52DataCard.addStyleName(AON.CSS.aonContractMediumCard());
		older52DataCard.add(createOlder52Data());
		addOlder52DataHandlers();

		add(older52DataCard);
	}

	private Widget createOlder52Data() {
		FlowPanel table = createFlexColumnPanel();

		table.add(createRow(older52LB, null));

		older52LB.clearItems();
		older52LB.addItem("-", "");
		older52LB.addItem("LEY 45/2002 MAYORES DE 52 PERC.SUB.REASS", "001");
		older52LB.addItem("LEY 45/2002 MAYORES DE 52 PERC.RESTO SUB", "002");

		if (Boolean.TRUE.equals(this.contractSpecificData.getOlderThan52())) {
			older52CB.setValue(true);
			older52LB.setValue(contractSpecificData.getOtherLegislations());
		} else showHideElement(older52DataCard, false);

		return table;
	}

	private void addOlder52DataHandlers() {
		older52LB.addChangeHandler(e -> contractSpecificData.setOtherLegislations(older52LB.getValue()));
	}

	private void createAnnexedDataCard() {
		annexedDataCard = new AonCustomCard("Anexo gestion colect. de contrataciones en origen");
		annexedDataCard.addStyleName(AON.CSS.aonContractMediumCard());
		annexedDataCard.add(creatAnnexedData());
		addAnnexedDataHandlers();

		add(annexedDataCard);
	}

	private Widget creatAnnexedData() {
		FlowPanel table = createFlexColumnPanel();

		table.add(createRow(annexedLB, sourceYearTB));

		annexedLB.clearItems();
		annexedLB.addItem("Si", "true");
		annexedLB.addItem("No", "false");

		sourceYearTB.setValue(null);

		if (Boolean.TRUE.equals(contractSpecificData.getAnnexedB())) {
			annexedCB.setValue(true);
			annexedLB.setValue(contractSpecificData.getAnnexed() ? "true" : "false");
			sourceYearTB.setValue(contractSpecificData.getSourceYear());
		} else showHideElement(annexedDataCard, false);

		return table;
	}

	private void addAnnexedDataHandlers() {
		annexedLB.addChangeHandler(e -> contractSpecificData.setAnnexed(Boolean.parseBoolean(annexedLB.getValue())));
		sourceYearTB.addValueChangeHandler(e -> contractSpecificData.setSourceYear(sourceYearTB.getValue()));
	}

	private void createCampaignsDataCard() {
		campaignsDataCard = new AonCustomCard("Campa\u00f1as");
		campaignsDataCard.addStyleName(AON.CSS.aonContractMediumCard());
		campaignsDataCard.add(creatCampaignsData());
		addCampaignsDataHandlers();

		add(campaignsDataCard);
	}

	private Widget creatCampaignsData() {
		FlowPanel table = createFlexColumnPanel();

		table.add(createRow(cpCampaignTB, codeCampaignTB));
		table.add(createRow(yearCampaignTB, null));

		cpCampaignTB.setValue(null);
		codeCampaignTB.setValue(null);
		yearCampaignTB.setValue(null);

		if (Boolean.TRUE.equals(contractSpecificData.getCampaigns())) {
			campaignsCB.setValue(true);
			cpCampaignTB.setValue(contractSpecificData.getCpCampaign());
			codeCampaignTB.setValue(contractSpecificData.getCodeCampaign());
			yearCampaignTB.setValue(contractSpecificData.getYearCampaign());
		} else showHideElement(campaignsDataCard, false);

		return table;
	}

	private void addCampaignsDataHandlers() {
		cpCampaignTB.addValueChangeHandler(e -> contractSpecificData.setCpCampaign(cpCampaignTB.getValue()));
		codeCampaignTB.addValueChangeHandler(e -> contractSpecificData.setCodeCampaign(codeCampaignTB.getValue()));
		yearCampaignTB.addValueChangeHandler(e -> contractSpecificData.setYearCampaign(yearCampaignTB.getValue()));
	}

	private void createInvestDataCard() {
		investDataCard = new AonCustomCard("Investigaci\u00f3n");
		investDataCard.addStyleName(AON.CSS.aonContractMediumCard());
		investDataCard.add(createInvestData());
		addInvestDataHandlers();

		add(investDataCard);
	}

	private Widget createInvestData() {
		FlowPanel table = createFlexColumnPanel();

		table.add(createRow(employerLB, employeeLB));
		table.add(createRow(researcherCB, null));

		employerLB.clearItems();
		employerLB.addItem("ORGANISMO PUBLICO", "1");
		employerLB.addItem("INSTITUCION SIN ANIMO DE LUCRO", "2");
		employerLB.addItem("UNIVERSIDAD PUBLICA", "3");
		employerLB.addItem("ORGANISMO PUBLICO DE INVESTIGACION DE LA ADMINISTRACION GENERAL DEL ESTADO", "4");
		employerLB.addItem("ORGANISMO PUBLICO DE INVESTIGACION DE OTRAS ADMINISTRACIONES PUBLICAS", "5");
		employerLB.addItem("UNIVERSIDADES PRIVADAS Y UNIVERSIDADES DE LA IGLESIA CATOLICA QUE PERCIBAN FONDOS PARA CONTRATAR PERSONAL INVESTIGADOR", "6");
		employerLB.addItem("ENTIDADES PRIVADAS SIN ANIMO DE LUCRO QUE REALICEN ACTIVIDADES DE I+D", "7");
		employerLB.addItem("CONSORCIOS PUBLICOS Y FUNDACIONES DEL SECTOR PUBLICO SEGUN D.A. 1ª LEY 14/2011", "8");
		employerLB.addItem("OTROS ORGANISMOS DE INVESTIGACION DE LA AGE CUANDO REALICEN ACTIVIDAD DE INVESTIGACION", "9");

		employeeLB.clearItems();
		employeeLB.addItem("INVESTIGAODR", "1");
		employeeLB.addItem("CIENTIFICO O TECNICO", "2");

		researcherCB.setValue(false);

		if (Boolean.TRUE.equals(contractSpecificData.getInvest())) {
			investCB.setValue(true);
			employerLB.setValue(contractSpecificData.getEmployer());
			employeeLB.setValue(contractSpecificData.getEmployee());
			researcherCB.setValue(this.contractSpecificData.getResearcher());
		} else showHideElement(investDataCard, false);

		return table;
	}

	private void addInvestDataHandlers() {
		employerLB.addChangeHandler(e -> contractSpecificData.setEmployer(employerLB.getValue()));
		employeeLB.addChangeHandler(e -> contractSpecificData.setEmployee(employeeLB.getValue()));
		researcherCB.addValueChangeHandler(e -> contractSpecificData.setResearcher(researcherCB.getValue()));
	}

	private void createInterimCauseDataCard() {
		interimCauseDataCard = new AonCustomCard("Causa de interinidad");
		interimCauseDataCard.addStyleName(AON.CSS.aonContractMediumCard());
		interimCauseDataCard.add(createInterimCauseData());
		addInterimCauseDataHandlers();

		add(interimCauseDataCard);
	}

	private Widget createInterimCauseData() {
		FlowPanel table = createFlexColumnPanel();

		table.add(createRow(interimCauseLB, null));

		interimCauseLB.clearItems();
		interimCauseLB.addItem("ACOGIMIENTO, PERSONAS TRABAJADORAS AUTONOMAS, SOCIAS TRABAJADORAS O SOCIAS DE TRABAJO DE LAS SOCIEDADES COOPERATIVAS", "V");
		interimCauseLB.addItem("ADOPCION, PERSONAS TRABAJADORAS AUTONOMAS, SOCIAS TRABAJADORAS O SOCIAS DE TRABAJO DE LAS SOCIEDADES COOPERATIVAS", "U");
		interimCauseLB.addItem("CONTRATADOS POR AUTONOMOS, POR CONCILIACION", "O");
		interimCauseLB.addItem("EJERCICIO CORRESPONSABLE DEL CUIDADO DEL MENOR O DE LA MENOR LACTANTE, PERSONAS TRABAJADORAS AUTONOMAS, SOCIAS TRABAJADORAS O SOCIAS DE TRABAJO DE LAS SOCIEDADES COOPERATIVAS", "Y");
		interimCauseLB.addItem("MATERNIDAD CON BONIFICACION, PERSONAS TRABAJADORAS AUTONOMAS, SOCIAS TRABAJADORAS O SOCIAS DE TRABAJO DE LAS SOCIEDADES COOPERATIVAS", "S");
		interimCauseLB.addItem("PATERNIDAD CON BONIFICACION, PERSONAS TRABAJADORAS AUTONOMAS, SOCIAS TRABAJADORAS O SOCIAS DE TRABAJO DE LAS SOCIEDADES COOPERATIVAS", "T");
		interimCauseLB.addItem("PERSONAS CON DISCAPACIDAD DESEMPLEADAS EN SUSTITUCION DE TRABAJADOR CON DISCAPACIDAD EN I.T.", "K");
		interimCauseLB.addItem("RIESGO DURANTE EL EMBARAZO, PERSONAS TRABAJADORAS AUTONOMAS, SOCIAS TRABAJADORAS O SOCIAS DE TRABAJO DE LAS SOCIEDADES COOPERATIVAS", "X");
		interimCauseLB.addItem("RIESGO DURANTE LA LACTANCIA NATURAL, PERSONAS TRABAJADORAS AUTONOMAS, SOCIAS TRABAJADORAS O SOCIAS DE TRABAJO DE LAS SOCIEDADES COOPERATIVAS", "W");
		interimCauseLB.addItem("SUSTITUCION VICTIMAS VIOLENCIA DE GENERO", "M");
		interimCauseLB.addItem("SUSTITUCION VICTIMAS VIOLENCIA SEXUAL", "Q");
		interimCauseLB.addItem("TRABAJADOR CON DERECHO RESERVA DE PUESTO", "A");
		interimCauseLB.addItem("TRABAJADOR PROCESO DE SELECCION/PROMOCION", "E");
		interimCauseLB.addItem("TRABAJADORES/AS POR MATERNIDAD/PATERNIDAD SIN BONIFICACION DE CUOTAS", "B");
		interimCauseLB.addItem("TRABAJ.EN FORMACION POR PERCEPTOR PRESTA", "J");

		if (Boolean.TRUE.equals(contractSpecificData.getIsInterimCause())) {
			interimCauseCB.setValue(true);
			interimCauseLB.setValue(contractSpecificData.getSustitucionCause());
		} else showHideElement(interimCauseDataCard, false);

		return table;
	}

	private void addInterimCauseDataHandlers() {
		interimCauseLB.addChangeHandler(e -> contractSpecificData.setSustitucionCause(interimCauseLB.getValue()));
	}

	private void createEntrepreneurSupportDataCard() {
		entrepreneurSupportDataCard = new AonCustomCard("Apoyo emprendedores");
		entrepreneurSupportDataCard.addStyleName(AON.CSS.aonContractMediumCard());
		entrepreneurSupportDataCard.add(createEntrepreneurSupportData());
		addEntrepreneurSupportDataHandlers();

		add(entrepreneurSupportDataCard);
	}

	private Widget createEntrepreneurSupportData() {
		FlowPanel table = createFlexColumnPanel();

		table.add(createRow(bonusColectiveLB, freelanceEmployeerCB));

		bonusColectiveLB.setWidth("70%");
		bonusColectiveLB.clearItems();
		bonusColectiveLB.addItem("JOVENES MENORES DE 30 A\u00D1OS", "001");
		bonusColectiveLB.addItem("MAYORES DE 45 A\u00D1OS", "002");
		bonusColectiveLB.addItem("DESEMPLEADOS INSCRIT. O.E 12 O MAS MESES", "003");
		bonusColectiveLB.addItem("MUJERES SUBREPR.INSCRITAS 12 O MAS MESES", "004");
		bonusColectiveLB.addItem("PERSONAS CON DISCAPACIDAD", "005");
		bonusColectiveLB.addItem("MUJERES SUBREPRES. MAYORES.DE 45 A\u00D1OS", "006");
		bonusColectiveLB.addItem("MUJERES SUBREPRESENTADAS (RESTO)", "007");
		bonusColectiveLB.addItem("JOVEN MENOR DE 30 A\u00D1OS PERCEPTOR REASS", "008");
		bonusColectiveLB.addItem("MAYOR DE 45 A\u00D1OS PERCEPTOR DE REASS", "009");
		bonusColectiveLB.addItem("INSCRITOS 12 O MAS MESES PERCEPT. REASS", "010");
		bonusColectiveLB.addItem("MUJER SUBREPR. > 45 A\u00D1OS PERCEPTOR REASS", "011");
		bonusColectiveLB.addItem("MUJER SUBR. INSCR 12 O MAS MESES. REASS", "012");
		bonusColectiveLB.addItem("MUJER SUBREP. (RESTO) SUBSIDIO REASS", "013");
		bonusColectiveLB.addItem("MENOR DE 25 A\u00D1OS", "014");
		bonusColectiveLB.addItem("ENTRE 30 Y 44 A\u00D1OS", "015");
		bonusColectiveLB.addItem("PERCEPTOR DE RENTAS MINIMAS DE INSERCION", "016");
		bonusColectiveLB.addItem("PERSONAS NO PUEDEN ACCEDER A PRESTACION", "017");
		bonusColectiveLB.addItem("JOVENES PROCED. INSTITUC. PENITENCIARIAS", "018");
		bonusColectiveLB.addItem("PERSONAS PROBLEM DROGADICCION Y ALCOHOL", "019");
		bonusColectiveLB.addItem("INTERNOS DE CENTROS PENITENCIARIOS", "020");
		bonusColectiveLB.addItem("PENADOS EN INSTITUCIONES PENITENCIARIAS", "021");
		bonusColectiveLB.addItem("MUJERES DESEMPLEADAS ENTRE 16 Y 45 A\u00D1OS", "022");
		bonusColectiveLB.addItem("MUJERES SUBPRESENTADAS INSC. 6 O MAS MESES", "023");
		bonusColectiveLB.addItem("DESEMPLEADOS INSCRITOS OE 6 O MAS MESES", "024");
		bonusColectiveLB.addItem("DESEMPLEADOS MAYORES 45 Y HASTA 55 A\u00D1OS", "025");
		bonusColectiveLB.addItem("DESEMPLEADOS MAYORES 55 Y HASTA 65 A\u00D1OS", "026");
		bonusColectiveLB.addItem("DESEM.PERC. PREST.SUB.RESTA 1 A\u00D1O O M\u00C1S", "027");
		bonusColectiveLB.addItem("DESEMPL. PERCEP. RENTA ACTIVA INSERCION", "028");
		bonusColectiveLB.addItem("MUJ.INSC.OE.12 O MAS MES EN 24 SIG. ALUMBR", "029");
		bonusColectiveLB.addItem("DESEMPLEADOS PERCEPTORES SUBSIDIO REASS", "030");
		bonusColectiveLB.addItem("CONVERSION A INDEFINIDO ACOGIDO BONIFICA", "031");
		bonusColectiveLB.addItem("MATERNIDAD", "032");
		bonusColectiveLB.addItem("ADOPCION", "033");
		bonusColectiveLB.addItem("ACOGIMIENTO", "034");
		bonusColectiveLB.addItem("RIESGO DURANTE EMBARAZO", "035");
		bonusColectiveLB.addItem("PERCEPTORES DE RENTA ACTIVA DE INSERCI\u00D3N (MAYORES 45 HASTA 55 A\u00D1OS)", "036");
		bonusColectiveLB.addItem("PERCEPTORES DE RENTA ACTIVA DE INSERCI\u00D3N (MAYORES 55 HASTA 65 A\u00D1OS)", "037");
		bonusColectiveLB.addItem("PERCEP.RENTA ACTIVA INS.RESTO EDADES", "038");
		bonusColectiveLB.addItem("MUJ.CONTRATADAS 24 MESES SIGU.ALUMBRAM", "039");
		bonusColectiveLB.addItem("MENORES INTERNOS INCLUIDOS EN LEY ORG. 5", "040");
		bonusColectiveLB.addItem("V\u00CDCTIMAS DE VIOLENCIA DOM\u00C9STICA", "041");
		bonusColectiveLB.addItem("PERSONAS CON DISCAPACIDAD DESEMP.INTERINIDAD POR INC", "042");
		bonusColectiveLB.addItem("MAYORES DE 52 A\u00D1OS BENEFICIARIOS DE SUBS", "043");
		bonusColectiveLB.addItem("DESEMPLEADOS PERCEPTORES DE RENTA AGRARIA", "044");
		bonusColectiveLB.addItem("MATERNIDAD O EXCEDENCIA TRANSFORMADO ANTES DE 1 A\u00D1O", "045");
		bonusColectiveLB.addItem("EXCEDENCIA CUIDADO HIJO PERCEPTOR PRESTACIONES", "046");
		bonusColectiveLB.addItem("EXCEDENCIA CUIDADO FAMILIAR PERCEP.PRESTACIONES", "047");
		bonusColectiveLB.addItem("SUSTITUCI\u00D3N V\u00CDCTIMAS VIOLENCIA DE G\u00C9NERO", "048");
		bonusColectiveLB.addItem("MAYORES DE 60 A\u00D1OS", "049");
		bonusColectiveLB.addItem("MUJERES REINC.2 A\u00D1OS SIGUIENTES AL PARTO", "050");
		bonusColectiveLB.addItem("PERSONAS CON DISCAPACIDAD INTELECTUAL O PAR\u00C1LISIS CELEBRAL O ENFERMEDAD MENTAL, CON GRADO IGUAL O SUPERIOR AL 33 %", "051");
		bonusColectiveLB.addItem("PERSONAS CON DISCAPACIDAD F\u00CDSICA O SENSORIAL, CON GRADO IGUAL O SUPERIOR AL 65 %", "052");
		bonusColectiveLB.addItem("MUJERES CON DISCAPACIDAD, CON GRADO IGUAL O SUPERIOR AL 33 %", "053");
		bonusColectiveLB.addItem("PERSONAL INVESTIGADOR EN FORMACI\u00D3N", "054");
		bonusColectiveLB.addItem("MUJERES CONTRATADAS DESPU\u00C9S DE 5 A\u00D1OS DE INACTIVIDAD LABORAL, SI ANTERIORMENTE HAN TRABAJADO 3 A\u00D1OS", "055");
		bonusColectiveLB.addItem("J\u00D3VENES ENTRE 16 Y 30 A\u00D1OS (AMBOS INCLUSIVE)", "056");
		bonusColectiveLB.addItem("MAYORES DE 45 A\u00D1OS", "057");
		bonusColectiveLB.addItem("MUJERES", "058");
		bonusColectiveLB.addItem("MATERNIDAD O EXCEDENCIA TRANSFORMADO EN EL MOMENTO DE LA REINCORPORACI\u00D3N", "059");
		bonusColectiveLB.addItem("V\u00CDCTIMAS DE VIOLENCIA DE G\u00C9NERO", "060");
		bonusColectiveLB.addItem("TEXTIL. MUJERES MENORES DE 45 A\u00D1OS", "061");
		bonusColectiveLB.addItem("TEXTIL. TRABAJADORES CON DISCAPACIDAD", "062");
		bonusColectiveLB.addItem("TEXTIL. MAYORES DE 30 Y MENORES DE 45 NO ACOGIDOS A NING\u00DAN COLECTIVO DEL PROGRAMA DE FOMENTO DE EMPLEO", "063");
		bonusColectiveLB.addItem("TEXTIL. MAYORES DE 30 Y MENORES DE 45 ACOGIDOS A ALG\u00DAN COLECTIVO DEL PROGRAMA DE FOMENTO DE EMPLEO", "064");
		bonusColectiveLB.addItem("TEXTIL. HOMBRES MAYORES DE 45 Y MENORES DE 55 A\u00D1OS", "065");
		bonusColectiveLB.addItem("TEXTIL. MUJERES MAYORES DE 45 Y MENORES DE 55 A\u00D1OS", "066");
		bonusColectiveLB.addItem("TEXTIL. HOMBRES MAYORES DE 55 A\u00D1OS", "067");
		bonusColectiveLB.addItem("TEXTIL. MUJERES MAYORES DE 55 A\u00D1OS", "068");
		bonusColectiveLB.addItem("TEXTIL. TRABAJADOR MAYOR 55 A\u00D1OS PERCEPTOR PRESTACI\u00D3N CONTRIBUTIVA AL QUE RESTE AL MENOS 1 A\u00D1O DE PERCEPCI\u00D3N", "069");
		bonusColectiveLB.addItem("HOMBRE SIN DISCAPACIDAD SEVERA < 45 A\u00D1OS CON CONTRATO INDEFINIDO", "070");
		bonusColectiveLB.addItem("HOMBRE SIN DISCAPACIDAD SEVERA >=45 A\u00D1OS CON CONTRATO INDEFINIDO", "071");
		bonusColectiveLB.addItem("HOMBRE CON DISCAPACIDAD SEVERA < 45 A\u00D1OS CON CONTRATO INDEFINIDO", "072");
		bonusColectiveLB.addItem("HOMBRE CON DISCAPACIDAD SEVERA >=45 A\u00D1OS CON CONTRATO INDEFINIDO", "073");
		bonusColectiveLB.addItem("MUJER SIN DISCAPACIDAD SEVERA < 45 A\u00D1OS CON CONTRATO INDEFINIDO", "074");
		bonusColectiveLB.addItem("MUJER SIN DISCAPACIDAD SEVERA >=45 A\u00D1OS CON CONTRATO INDEFINIDO", "075");
		bonusColectiveLB.addItem("MUJER CON DISCAPACIDAD SEVERA < 45 A\u00D1OS CON CONTRATO INDEFINIDO", "076");
		bonusColectiveLB.addItem("MUJER CON DISCAPACIDAD SEVERA >=45 A\u00D1OS CON CONTRATO INDEFINIDO", "077");
		bonusColectiveLB.addItem("HOMBRE SIN DISCAPACIDAD SEVERA < 45 A\u00D1OS CON CONTRATO TEMPORAL", "078");
		bonusColectiveLB.addItem("HOMBRE SIN DISCAPACIDAD SEVERA >=45 A\u00D1OS CON CONTRATO TEMPORAL", "079");
		bonusColectiveLB.addItem("HOMBRE CON DISCAPACIDAD SEVERA < 45 A\u00D1OS CON CONTRATO TEMPORAL", "080");
		bonusColectiveLB.addItem("HOMBRE CON DISCAPACIDAD SEVERA >=45 A\u00D1OS CON CONTRATO TEMPORAL", "081");
		bonusColectiveLB.addItem("MUJER SIN DISCAPACIDAD SEVERA < 45 A\u00D1OS CON CONTRATO TEMPORAL", "082");
		bonusColectiveLB.addItem("MUJER SIN DISCAPACIDAD SEVERA >=45 A\u00D1OS CON CONTRATO TEMPORAL", "083");
		bonusColectiveLB.addItem("MUJER CON DISCAPACIDAD SEVERA < 45 A\u00D1OS CON CONTRATO TEMPORAL", "084");
		bonusColectiveLB.addItem("MUJER CON DISCAPACIDAD SEVERA >=45 A\u00D1OS CON CONTRATO TEMPORAL", "085");
		bonusColectiveLB.addItem("PATERNIDAD", "086");
		bonusColectiveLB.addItem("RIESGO DURANTE LA LACTANCIA NATURAL", "087");
		bonusColectiveLB.addItem("PERSONAS CENTROS ALOJAMIENTO ALTERNATIVO", "088");
		bonusColectiveLB.addItem("PERSONAS SERVICIOS PREVENCI\u00D3N/INSERCI\u00D3N", "089");
		bonusColectiveLB.addItem("MENORES INTERNOS QUE TRABAJEN EN EL PROPIO CENTRO", "090");
		bonusColectiveLB.addItem("DESEMPLEADOS CON CARGAS FAMILIARES", "091");
		bonusColectiveLB.addItem("CARGAS FAMILIARES. DESEMPLEADOS INSCRITOS 6 \u00D3 M\u00C1S MESES", "092");
		bonusColectiveLB.addItem("CARGAS FAMILIARES. MUJERES CONTRATADAS EN LOS 24 MESES SIGUIENTES AL PARTO", "093");
		bonusColectiveLB.addItem("CARGAS FAMILIARES. MUJERES REINCORPORADAS DESPU\u00C9S 5 A\u00D1OS INACTIVIDAD", "094");
		bonusColectiveLB.addItem("CARGAS FAMILIARES. J\u00D3VENES ENTRE 16 Y 30 A\u00D1OS", "095");
		bonusColectiveLB.addItem("CARGAS FAMILIARES. MAYORES DE 45 A\u00D1OS", "096");
		bonusColectiveLB.addItem("CARGAS FAMILIARES. MUJERES EN GENERAL", "097");
		bonusColectiveLB.addItem("CARGAS FAMILIARES. V\u00CDCTIMAS DE VIOLENCIA DOM\u00C9STICA", "098");
		bonusColectiveLB.addItem("CARGAS FAMILIARES. V\u00CDCTIMAS DE VIOLENCIA DE G\u00C9NERO", "099");
		bonusColectiveLB.addItem("CARGAS FAMILIARES. PERCEPTOR RENTA M\u00CDNIMA INSERCI\u00D3N", "100");
		bonusColectiveLB.addItem("CARGAS FAMILIARES. PERSONAS NO PUEDEN ACCEDER A PRESTACI\u00D3N", "101");
		bonusColectiveLB.addItem("CARGAS FAMILIARES. J\u00D3VENES PROCED.INSTITUCIONES PENITENCIARIAS", "102");
		bonusColectiveLB.addItem("CARGAS FAMILIARES. PERSONAS PROBLEMAS DROGADICCI\u00D3N Y ALCOHOL", "103");
		bonusColectiveLB.addItem("CARGAS FAMILIARES. INTERNOS DE CENTROS PENITENCIARIOS", "104");
		bonusColectiveLB.addItem("CARGAS FAMILIARES. MENORES INTERNOS INCLUIDOS EN LEY ORG.5", "105");
		bonusColectiveLB.addItem("CARGAS FAMILIARES. TEXTIL-MUJERES MENORES DE 45 A\u00D1OS", "106");
		bonusColectiveLB.addItem("CARGAS FAMILIARES. TEXTIL-TRABAJADORES CON DISCAPACIDAD", "107");
		bonusColectiveLB.addItem("CARGAS FAMILIARES. TEXTIL-MAYORES DE 30 Y MENORES DE 45 NO ACOGIDOS A NING\u00DAN COLECTIVO DEL PROGRAMA DE FOMENTO DE EMPLEO", "108");
		bonusColectiveLB.addItem("CARGAS FAMILIARES. TEXTIL-MAYORES DE 30 Y MENORES DE 45 ACOGIDOS A ALG\u00DAN COLECTIVO DEL PROGRAMA DE FOMENTO DE EMPLEO", "109");
		bonusColectiveLB.addItem("CARGAS FAMILIARES. TEXTIL-HOMBRES MAYORES DE 45 Y MENORES DE 55 A\u00D1OS", "110");
		bonusColectiveLB.addItem("CARGAS FAMILIARES. TEXTIL-MUJERES MAYORES DE 45 Y MENORES DE 55 A\u00D1OS", "111");
		bonusColectiveLB.addItem("CARGAS FAMILIARES. TEXTIL-HOMBRES MAYORES DE 55 A\u00D1OS", "112");
		bonusColectiveLB.addItem("CARGAS FAMILIARES. TEXTIL-MUJERES MAYORES DE 55 A\u00D1OS", "113");
		bonusColectiveLB.addItem("CARGAS FAMILIARES. TEXTIL-TRABAJADOR MAYOR 55 A\u00D1OS PERCEPTOR PRESTACI\u00D3N CONTRIBUTIVA AL QUE RESTE AL MENOS 1 A\u00D1O DE PERCEPCI\u00D3N", "114");
		bonusColectiveLB.addItem("CALZADO - MUJERES MENORES DE 45 A\u00D1OS", "115");
		bonusColectiveLB.addItem("CALZADO - TRABAJADORES CON DISCAPACIDAD", "116");
		bonusColectiveLB.addItem("CALZADO - MAYORES DE 30 Y MENORES DE 45 NO ACOGIDOS A NING\u00DAN COLECTIVO DEL PROGRAMA DE FOMENTO DE EMPLEO", "117");
		bonusColectiveLB.addItem("CALZADO - MAYORES DE 30 Y MENORES DE 45 ACOGIDOS A ALG\u00DAN COLECTIVO DEL PROGRAMA DE FOMENTO DE EMPLEO", "118");
		bonusColectiveLB.addItem("CALZADO - HOMBRES MAYORES DE 45 Y MENORES DE 55 A\u00D1OS", "119");
		bonusColectiveLB.addItem("CALZADO - MUJERES MAYORES DE 45 Y MENORES DE 55 A\u00D1OS", "120");
		bonusColectiveLB.addItem("CALZADO - HOMBRES MAYORES DE 55 A\u00D1OS", "121");
		bonusColectiveLB.addItem("CALZADO - MUJERES MAYORES DE 55 A\u00D1OS", "122");
		bonusColectiveLB.addItem("CALZADO - TRABAJADOR MAYOR 55 A\u00D1OS PERCEPTOR PRESTACI\u00D3N CONTRIBUTIVA AL QUE RESTE AL MENOS 1 A\u00D1O DE PERCEPCI\u00D3N", "123");
		bonusColectiveLB.addItem("BENEFICIARIO DE PRESTACI\u00D3N DURANTE AL MENOS 3 MESES", "124");
		bonusColectiveLB.addItem("BENEFICIARIO DE SUBSIDIO POR DESEMPLEO", "125");
		bonusColectiveLB.addItem("BENEFICIARIO DE RENTA ACTIVA DE INSERCI\u00D3N", "126");
		bonusColectiveLB.addItem("PRIMER ASALARIADO DE TRABAJADOR AUT\u00D3NOMO", "127");
		bonusColectiveLB.addItem("JUGUETE - MUJERES MENORES DE 45 A\u00D1OS", "128");
		bonusColectiveLB.addItem("JUGUETE - TRABAJADORES CON DISCAPACIDAD", "129");
		bonusColectiveLB.addItem("JUGUETE - MAYORES DE 30 Y MENORES DE 45 NO ACOGIDOS A NING\u00DAN COLECTIVO DEL PROGRAMA DE FOMENTO DE EMPLEO", "130");
		bonusColectiveLB.addItem("JUGUETE - MAYORES DE 30 Y MENORES DE 45 ACOGIDOS A ALG\u00DAN COLECTIVO DEL PROGRAMA DE FOMENTO DE EMPLEO", "131");
		bonusColectiveLB.addItem("JUGUETE - HOMBRES MAYORES DE 45 Y MENORES DE 55 A\u00D1OS", "132");
		bonusColectiveLB.addItem("JUGUETE - MUJERES MAYORES DE 45 Y MENORES DE 55 A\u00D1OS", "133");
		bonusColectiveLB.addItem("JUGUETE - HOMBRES MAYORES DE 55 A\u00D1OS", "134");
		bonusColectiveLB.addItem("JUGUETE - MUJERES MAYORES DE 55 A\u00D1OS", "135");
		bonusColectiveLB.addItem("JUGUETE - TRABAJADOR MAYOR 55 A\u00D1OS PERCEPTOR PRESTACI\u00D3N CONTRIBUTIVA AL QUE RESTE AL MENOS 1 A\u00D1O DE PERCEPCI\u00D3N", "136");
		bonusColectiveLB.addItem("MUEBLE - MUJERES MENORES DE 45 A\u00D1OS", "137");
		bonusColectiveLB.addItem("MUEBLE - TRABAJADORES CON DISCAPACIDAD", "138");
		bonusColectiveLB.addItem("MUEBLE - MAYORES DE 30 Y MENORES DE 45 NO ACOGIDOS A NING\u00DAN COLECTIVO DEL PROGRAMA DE FOMENTO DE EMPLEO", "139");
		bonusColectiveLB.addItem("MUEBLE - MAYORES DE 30 Y MENORES DE 45 ACOGIDOS A ALG\u00DAN COLECTIVO DEL PROGRAMA DE FOMENTO DE EMPLEO", "140");
		bonusColectiveLB.addItem("MUEBLE - HOMBRES MAYORES DE 45 Y MENORES DE 55 A\u00D1OS", "141");
		bonusColectiveLB.addItem("MUEBLE - MUJERES MAYORES DE 45 Y MENORES DE 55 A\u00D1OS", "142");
		bonusColectiveLB.addItem("MUEBLE - HOMBRES MAYORES DE 55 A\u00D1OS", "143");
		bonusColectiveLB.addItem("MUEBLE - MUJERES MAYORES DE 55 A\u00D1OS", "144");
		bonusColectiveLB.addItem("MUEBLE - TRABAJADOR MAYOR 55 A\u00D1OS PERCEPTOR PRESTACI\u00D3N CONTRIBUTIVA AL QUE RESTE AL MENOS 1 A\u00D1O DE PERCEPCI\u00D3N", "145");
		bonusColectiveLB.addItem("J\u00D3VENES 16-30 A\u00D1OS, HOMBRES DESEMP.1 A\u00D1O SIN TITULACI\u00D3N", "146");
		bonusColectiveLB.addItem("J\u00D3VENES 16-30 A\u00D1OS, MUJERES DESEMP.1 A\u00D1O SIN TITULACI\u00D3N", "147");
		bonusColectiveLB.addItem("MAYORES DE 45 A\u00D1OS, HOMBRES DESEMPLEADOS 1 A\u00D1O", "148");
		bonusColectiveLB.addItem("MAYORES DE 45 A\u00D1OS, MUJERES DESEMPLEADAS 1 A\u00D1O", "149");
		bonusColectiveLB.addItem("FORMACI\u00D3N", "150");
		bonusColectiveLB.addItem("CONVERSI\u00D3N A INDEFINIDO, HOMBRES", "151");
		bonusColectiveLB.addItem("CONVERSI\u00D3N A INDEFINIDO, MUJERES", "152");
		bonusColectiveLB.addItem("HOMBRES 16-30 A\u00D1OS, DESEMP.12 MESES EN 18 SIN TITULACION", "153");
		bonusColectiveLB.addItem("MUJERES 16-30 A\u00D1OS, DESEMP.12 MESES EN 18 SIN TITULACION", "154");
		bonusColectiveLB.addItem("HOMBRES MAY. 45 A\u00D1OS DESEMPLEADOS 12 MESES EN 18", "155");
		bonusColectiveLB.addItem("MUJERES MAY. 45 A\u00D1OS DESEMPLEADOS 12 MESES EN 18", "156");
		bonusColectiveLB.addItem("J\u00D3VENES 16-30 A\u00D1OS EN EMPRESAS MENOS DE 50 TRABAJ.", "157");
		bonusColectiveLB.addItem("MUJERES 16-30 A\u00D1OS OCUP. SUBREPR. EMPRESAS < 50", "158");
		bonusColectiveLB.addItem("MAYORES DE 45 A\u00D1OS EN EMPRESAS MENOS DE 50 TRABAJ.", "159");
		bonusColectiveLB.addItem("MUJERES MAY. 45 A\u00D1OS OCUP. SUBREPR. EMPRESAS < 50", "160");
		bonusColectiveLB.addItem("TRANSFORMACI\u00D3N EN INDEFINIDO, HOMBRES", "161");
		bonusColectiveLB.addItem("TRANSFORMACI\u00D3N EN INDEFINIDO, MUJERES", "162");
		bonusColectiveLB.addItem("V\u00CDCTIMAS DE TERRORISMO", "163");
		bonusColectiveLB.addItem("TRANSFORMACI\u00D3N INDEFINIDO, EXCLU\u00CDDO SOCIAL", "164");
		bonusColectiveLB.addItem("TRANSFORMACI\u00D3N INDEFINIDO, V. VIOLENCIA DE G\u00C9NERO", "165");
		bonusColectiveLB.addItem("TRANSFORMACI\u00D3N INDEFINIDO, V. VIOLENCIA DOM\u00C9STICA", "166");
		bonusColectiveLB.addItem("TRANSFORMACI\u00D3N INDEFINIDO, V\u00CDCTIMA TERRORISMO", "167");
		bonusColectiveLB.addItem("INCORPORACI\u00D3N COMO SOCIO DESEMPLEADO MENOR 30 A\u00D1OS", "168");
		bonusColectiveLB.addItem("CONVERSI\u00D3N EVENTUAL PRIMER EMPLEO JOVEN – HOMBRE", "169");
		bonusColectiveLB.addItem("CONVERSI\u00D3N EVENTUAL PRIMER EMPLEO JOVEN – MUJER", "170");
		bonusColectiveLB.addItem("INDEFINIDO PROCEDENTE PRIMER EMPLEO JOVEN DE ETT", "171");
		bonusColectiveLB.addItem("INDEFINIDO PROCEDENTE CONTRATO EN PR\u00C1CTICAS ETT", "172");
		bonusColectiveLB.addItem("BENEFICIARIO SISTEMA NACIONAL DE GARANT\u00CDA JUVENIL", "173");
		bonusColectiveLB.addItem("CREACI\u00D3N DE EMPLEO INDEFINIDO (SNGJ) RDL 1/2015", "174");
		bonusColectiveLB.addItem("V\u00CDCTIMAS DE TRATA DE SERES HUMANOS", "175");
		bonusColectiveLB.addItem("CONT. FORMACI\u00D3N SNGJ BONIF. 100% EMPRE MENOS 250 TRAB", "176");
		bonusColectiveLB.addItem("CONT. FORMACI\u00D3N SNGJ BONIF. 75% EMPRE 250 \u00D3 M\u00C1S TRAB.", "177");
		bonusColectiveLB.addItem("TRANSFORMACI\u00D3N CONTRATO DE FORMACI\u00D3N – HOMBRES SNGJ", "178");
		bonusColectiveLB.addItem("TRANSFORMACI\u00D3N CONTRATO DE FORMACI\u00D3N – MUJERES SNGJ", "179");
		bonusColectiveLB.addItem("FAMILIAR DE TRABAJADOR AUT\u00D3NOMO", "186");
		bonusColectiveLB.addItem("DESEMPLEADO, INSCRITO 12 MESES EN UN PERIODO DE 18, HOMBRES", "187");
		bonusColectiveLB.addItem("DESEMPLEADO, INSCRITO 12 MESES EN UN PERIODO DE 18, MUJERES", "188");
		bonusColectiveLB.addItem("TRANSFORMACI\u00D3N CONTRATO TEMPORAL AGRARIO, HOMBRES", "189");
		bonusColectiveLB.addItem("TRANSFORMACI\u00D3N CONTRATO TEMPORAL AGRARIO, MUJERES", "190");

		freelanceEmployeerCB.setValue(false);

		if (Boolean.TRUE.equals(contractSpecificData.getEntrepreneurSupport())) {
			entrepreneurSupportCB.setValue(true);
			bonusColectiveLB.setValue(contractSpecificData.getBonusColective());
			freelanceEmployeerCB.setValue(contractSpecificData.getFreelanceEmployeer());
		} else showHideElement(entrepreneurSupportDataCard, false);

		return table;
	}

	private void addEntrepreneurSupportDataHandlers() {
		bonusColectiveLB.addChangeHandler(e -> contractSpecificData.setBonusColective(bonusColectiveLB.getValue()));
		freelanceEmployeerCB.addValueChangeHandler(e -> contractSpecificData.setFreelanceEmployeer(freelanceEmployeerCB.getValue()));
	}

	private void createPromotionMeasuresDataCard() {
		promotionMeasuresDataCard = new AonCustomCard("Medidas fomento");
		promotionMeasuresDataCard.addStyleName(AON.CSS.aonContractMediumCard());
		promotionMeasuresDataCard.add(createPromotionMeasuresData());
		addPromotionMeasuresDataHandlers();

		add(promotionMeasuresDataCard);
	}

	private Widget createPromotionMeasuresData() {
		FlowPanel table = createFlexColumnPanel();

		table.add(createRow(promotionPermanentHiringCB, null));

		promotionPermanentHiringCB.setValue(false);

		if (Boolean.TRUE.equals(contractSpecificData.getPromotionMeasures())) {
			promotionMeasuresCB.setValue(true);
			promotionPermanentHiringCB.setValue(contractSpecificData.getPromotionPermanentHiring());
		} else showHideElement(promotionMeasuresDataCard, false);

		return table;
	}

	private void addPromotionMeasuresDataHandlers() {
		promotionPermanentHiringCB.addValueChangeHandler(e -> contractSpecificData.setPromotionPermanentHiring(promotionPermanentHiringCB.getValue()));
	}

	private void createQuoteReductionsDataCard() {
		quoteReductionsDataCard = new AonCustomCard("Reducci\u00f3n de cuotas");
		quoteReductionsDataCard.addStyleName(AON.CSS.aonContractMediumCard());
		quoteReductionsDataCard.add(createQuoteReductionsData());
		addQuoteReductionsDataHandlers();

		add(quoteReductionsDataCard);
	}

	private Widget createQuoteReductionsData() {
		FlowPanel table = createFlexColumnPanel();

		table.add(createRow(reductionColectiveLB, quoteReductionLB));
		table.add(createRow(journeyPercentTB, null));

		reductionColectiveLB.clearItems();
		reductionColectiveLB.addItem("DESEMPLEADOS CON EDAD IGUAL O INFERIOR A 30 ANOS", "01");
		reductionColectiveLB.addItem("DESEMPLEADOS AL MENOS 12 MESES EN 18 ANTERIORES", "02");
		reductionColectiveLB.addItem("DESEMPLEADOS MAYORES DE 20 ANOS INSCRITOS A 16/08/2011", "03");
		reductionColectiveLB.addItem("DESEMPLEADOS INSCRITOS EN OFICINA DE EMPLEO", "04");
		reductionColectiveLB.addItem("TRANSFORMACION DE CONTRATO DE FORMACION HOMBRE", "05");
		reductionColectiveLB.addItem("TRANSFORMACION DE CONTRATO DE FORMACION MUJER", "06");
		reductionColectiveLB.addItem("CONTRATO PREDOCTORAL", "07");
		reductionColectiveLB.addItem("DESEMPLEADO < 30 ANOS SIN EXPERIENCIA LABORAL", "08");
		reductionColectiveLB.addItem("DESEMPLEADO < 30 ANOS PROCED.OTRO SECTOR ACTIVIDAD", "09");
		reductionColectiveLB.addItem("DESEMPLEADO < 30 ANOS INSCRITOS 12 MESES EN 18", "10");
		reductionColectiveLB.addItem("DESEMPLEADO < 30 ANOS CON AUTONOMOS O MICROEMPRESA", "11");
		reductionColectiveLB.addItem("PRIMER CONTRATO CON >=45 ANOS INSCRITO 12 M. EN 18", "12");
		reductionColectiveLB.addItem("PRIMER CONTRATO CON >=45 ANOS RECUAL. PROFESIONAL", "13");
		reductionColectiveLB.addItem("CONTRATO EN PR¡CTICAS A < 30 ANOS", "14");
		reductionColectiveLB.addItem("CONTRATO EN PR¡CTICAS A < 30 ANOS, PRACT.NO LABOR.", "15");
		reductionColectiveLB.addItem("DESEMPLEADO < 30 ANOS SIN TITULACION", "16");
		reductionColectiveLB.addItem("INDEFINIDO PROCEDENTE CONTRATO DE FORMACION DE ETT", "17");
		reductionColectiveLB.addItem("CONTRATO INDEFINIDO TARIFA PLANA", "18");
		reductionColectiveLB.addItem("BENEFICIARIO SISTEMA NACIONAL DE GARANTÕA JUVENIL", "19");
		reductionColectiveLB.addItem("CREACION DE EMPLEO INDEFINIDO", "20");
		reductionColectiveLB.addItem("FORMACION, DISCAPACIDAD >= 33% DISPOSICION ADICIONAL 20 E.T.", "21");
		reductionColectiveLB.addItem("PRACTICAS, DISCAPACIDAD >= 33% DISPOSICION ADICIONAL 20 E.T.", "22");
		reductionColectiveLB.addItem("EN PRACTICAS < 35 ANOS, DISCAPACIDAD D.A. 20 E.T.", "23");
		reductionColectiveLB.addItem("PRACT. NO LABOR.< 35 ANOS, DISCAPACIDAD D.A. 20 E.T.", "24");

		quoteReductionLB.clearItems();
		quoteReductionLB.addItem("Si", "true");
		quoteReductionLB.addItem("No", "false");

		journeyPercentTB.setValue(null);

		if (Boolean.TRUE.equals(contractSpecificData.getQuoteReductions())) {
			quoteReductionsCB.setValue(true);
			reductionColectiveLB.setValue(contractSpecificData.getReductionColective());
			quoteReductionLB.setValue(
					null != contractSpecificData.getQuoteReduction() && contractSpecificData.getQuoteReduction()
							? "true"
							: "false");
			journeyPercentTB.setValue(contractSpecificData.getJourneyPercent());
		} else showHideElement(quoteReductionsDataCard, false);

		return table;
	}

	private void addQuoteReductionsDataHandlers() {
		reductionColectiveLB.addChangeHandler(e -> contractSpecificData.setReductionColective(reductionColectiveLB.getValue()));
		quoteReductionLB.addChangeHandler(e -> contractSpecificData.setQuoteReduction(Boolean.parseBoolean(quoteReductionLB.getValue())));
		journeyPercentTB.addValueChangeHandler(e -> contractSpecificData.setJourneyPercent(journeyPercentTB.getValue()));
	}

	private void createBonusDataCard() {
		bonusDataCard = new AonCustomCard("Bonificaci\u00f3n");
		bonusDataCard.addStyleName(AON.CSS.aonContractMediumCard());
		bonusDataCard.add(createBonusData());
		addBonusDataHandlers();

		add(bonusDataCard);
	}

	private Widget createBonusData() {
		FlowPanel table = createFlexColumnPanel();

		table.add(createRow(bonusLB, null));

		bonusLB.clearItems();
		bonusLB.addItem("-", "");
		bonusLB.addItem("PERCEPTOR DE RENTAS MINIMAS DE INSERCION", "016");
		bonusLB.addItem("PERSONAS NO PUEDEN ACCEDER A PRESTACION", "017");
		bonusLB.addItem("JOVENES PROCED. INSTITUC. PENITENCIARIAS", "018");
		bonusLB.addItem("PERSONAS PROBLEM DROGADICCION Y ALCOHOL", "019");
		bonusLB.addItem("INTERNOS DE CENTROS PENITENCIARIOS", "020");
		bonusLB.addItem("PENADOS EN INSTITUCIONES PENITENCIARIAS", "021");
		bonusLB.addItem("MENORES INTERNOS INCLUIDOS EN LEY ORG. 5", "040");
		bonusLB.addItem("V\u00CDCTIMAS DE VIOLENCIA DOM\u00C9STICA", "041");
		bonusLB.addItem("V\u00CDCTIMAS DE VIOLENCIA DE G\u00C9NERO", "060");
		bonusLB.addItem("PERSONAS CENTROS ALOJAMIENTO ALTERNATIVO", "088");
		bonusLB.addItem("PERSONAS SERVICIOS PREVENCI\u00D3N/INSERCI\u00D3N", "089");
		bonusLB.addItem("V\u00CDCTIMAS DE TERRORISMO", "163");
		bonusLB.addItem("INDEFINIDO PROCEDENTE CONTRATO EN PR\u00C1CTICAS ETT", "172");
		bonusLB.addItem("V\u00CDCTIMAS DE TRATA DE SERES HUMANOS", "175");
		bonusLB.addItem("FAMILIAR DE TRABAJADOR AUT\u00D3NOMO", "186");
		bonusLB.addItem("DESEMPLEADO, INSCRITO 12 MESES EN UN PERIODO DE 18, HOMBRES", "187");
		bonusLB.addItem("DESEMPLEADO, INSCRITO 12 MESES EN UN PERIODO DE 18, MUJERES", "188");
		bonusLB.addItem("PERSONA CON CAPACIDAD INTELECTUAL L\u00CDMITE", "193");

		// BonusDataTable
		if (Boolean.TRUE.equals(contractSpecificData.getBonus())) {
			bonusCB.setValue(true);
			bonusLB.setValue(contractSpecificData.getBonusType());
		} else showHideElement(bonusDataCard, false);

		return table;
	}

	private void addBonusDataHandlers() {
		bonusLB.addChangeHandler(e -> contractSpecificData.setBonusType(bonusLB.getValue()));
	}

	// ------------------------------------- Cards Methods

	private AonCustomCard createCard(String title, Widget content) {
		AonCustomCard card = new AonCustomCard(title);
		card.addStyleName(AON.CSS.aonContractSmallCard());
		card.add(content);
		return card;
	}

	private FlowPanel createRow(Widget widget1, Widget widget2) {
		FlowPanel row = createFlexPanel();
		row.add(widget1);
		if (widget2 != null) {
			row.add(widget2);
		}
		return row;
	}
	
	private FlowPanel createWrapRow(List<Widget> widgets) {
		FlowPanel row = createFlexPanel();
		row.getElement().getStyle().setProperty("flex-wrap", "wrap");
		
		widgets.forEach(widget -> {
			widget.setWidth("49%");
			row.add(widget);
		});
		
		return row;
	}

	private FlowPanel createFlexPanel() {
		FlowPanel panel = new FlowPanel();
		panel.addStyleName(AON.CSS.aonItemFlex());
		panel.setWidth("100%");
		return panel;
	}

	private FlowPanel createFlexColumnPanel() {
		FlowPanel panel = new FlowPanel();
		panel.addStyleName(AON.CSS.aonItemFlex());
		panel.addStyleName(AON.CSS.aonFlexColumn());
		panel.setWidth("100%");
		return panel;
	}

	private void showHideElement(Widget widget, boolean visible) {
		if (visible)
			widget.getElement().getStyle().clearDisplay();
		else
			widget.getElement().getStyle().setDisplay(Display.NONE);
	}

	// ------------------------------------------------------ Auxiliar Methods
	// ----------------------------------------------------

	private void initializeView() {
		String contractType = contractEmployeeInfo.getContractInfo().getContractType();
		switch (contractType) {
			case "130":
				set130View();
				break;
			case "150":
				set150View();
				break;
			case "200":
				set200View();
			case "209":
				set200View();
				break;
			case "230":
				set230and250View();
				break;
			case "250":
				set230and250View();
				break;
			case "289":
				set200View();
				break;
			case "300":
				set300View();
				break;
			case "330":
				set330and350View();
				break;
			case "350":
				set330and350View();
				break;
			case "389":
				set300View();
				break;
			case "401":
				set401View();
				break;
			case "402":
				set402View();
				break;
			case "403":
				set403View();
				break;
			case "407":
				set402View();
				break;
			case "410":
				set410View();
				break;
			case "418":
				set410View();
				break;
			case "420":
				set420View();
				break;
			case "421":
				set421View();
				break;
			case "430":
				set430View();
				break;
			case "441":
				set441View();
				break;
			case "450":
				set450View();
				break;
			case "452":
				set452View();
				break;
			case "501":
				set501View();
				break;
			case "502":
				set502View();
				break;
			case "503":
				set503View();
				break;
			case "507":
				set502View();
				break;
			case "510":
				set510View();
				break;
			case "518":
				set510View();
				break;
			case "520":
				set520View();
				break;
			case "530":
				set530View();
				break;
			case "540":
				set540View();
				break;
			case "541":
				set541View();
				break;
			case "550":
				set550View();
				break;
			case "552":
				set552View();
				break;
			case "970":
				set970and990View();
				break;
			case "980":
				set980View();
				break;
			case "990":
				set970and990View();
				break;
			default:
				setDefaultView();
				break;
		}

		// Transform
		showHideTransformRows();
	}

	private void setDefaultView() {
		showHideElement(calendarFormativeStartDate, false);
		showHideElement(calendarFormativeEndDate, false);
		showHideElement(calendarFormativeEndDate, false);
		showHideElement(academicTitulationLB, false);
		showHideElement(profesionalityCB, false);
		showHideElement(agreementHoursTB, false);
		showHideElement(agreementMinutesTB, false);
		showHideElement(repeatFDCB, false);
		showHideElement(journeyTypeLB, false);
		showHideElement(journeyDurationHoursTB, false);
		showHideElement(journeyDurationMinutesTB, false);
		showHideElement(teoricFormationLB, false);
		showHideElement(formationHoursTB, false);
		showHideElement(formationMinutesTB, false);
		showHideElement(retirementPercentTB, false);
		showHideElement(planRecoveryCB, false);
		showHideElement(writenContractCB, false);

		showHideElement(workProgramDataCB, false);
		showHideElement(investCB, false);
		showHideElement(interimCauseCB, false);
		showHideElement(entrepreneurSupportCB, true);
		showHideElement(quoteReductionsCB, false);
		showHideElement(bonusCB, false);
	}

	private void set130View() {
		showHideElement(calendarFormativeStartDate, false);
		showHideElement(calendarFormativeEndDate, false);
		showHideElement(academicTitulationLB, false);
		showHideElement(profesionalityCB, false);
		showHideElement(agreementHoursTB, false);
		showHideElement(agreementMinutesTB, false);
		showHideElement(repeatFDCB, false);
		showHideElement(journeyTypeLB, false);
		showHideElement(journeyDurationHoursTB, false);
		showHideElement(journeyDurationMinutesTB, false);
		showHideElement(teoricFormationLB, false);
		showHideElement(formationHoursTB, false);
		showHideElement(formationMinutesTB, false);
		showHideElement(retirementPercentTB, false);
		showHideElement(planRecoveryCB, false);
		showHideElement(writenContractCB, false);

		showHideElement(workProgramDataCB, false);
		showHideElement(annexedCB, false);
		showHideElement(investCB, false);
		showHideElement(interimCauseCB, false);
		showHideElement(entrepreneurSupportCB, false);
		showHideElement(quoteReductionsCB, false);
		showHideElement(bonusCB, true);
	}

	private void set150View() {
		showHideElement(calendarFormativeStartDate, false);
		showHideElement(calendarFormativeEndDate, false);
		showHideElement(academicTitulationLB, false);
		showHideElement(profesionalityCB, false);
		showHideElement(agreementHoursTB, false);
		showHideElement(agreementMinutesTB, false);
		showHideElement(repeatFDCB, false);
		showHideElement(journeyTypeLB, false);
		showHideElement(journeyDurationHoursTB, false);
		showHideElement(journeyDurationMinutesTB, false);
		showHideElement(teoricFormationLB, false);
		showHideElement(formationHoursTB, false);
		showHideElement(formationMinutesTB, false);
		showHideElement(retirementPercentTB, false);
		showHideElement(planRecoveryCB, false);
		showHideElement(writenContractCB, false);

		showHideElement(workProgramDataCB, false);
		showHideElement(annexedCB, false);
		showHideElement(investCB, false);
		showHideElement(interimCauseCB, false);
		showHideElement(quoteReductionsCB, false);
		showHideElement(bonusCB, true);
	}

	private void set200View() {
		showHideElement(calendarFormativeStartDate, false);
		showHideElement(calendarFormativeEndDate, false);
		showHideElement(academicTitulationLB, false);
		showHideElement(profesionalityCB, false);
		showHideElement(agreementHoursTB, true);
		showHideElement(agreementMinutesTB, true);
		showHideElement(repeatFDCB, true);
		showHideElement(journeyTypeLB, true);
		showHideElement(journeyDurationHoursTB, true);
		showHideElement(journeyDurationMinutesTB, true);
		showHideElement(teoricFormationLB, false);
		showHideElement(formationHoursTB, false);
		showHideElement(formationMinutesTB, false);
		showHideElement(retirementPercentTB, false);
		showHideElement(planRecoveryCB, false);
		showHideElement(writenContractCB, false);

		showHideElement(workProgramDataCB, false);
		showHideElement(investCB, false);
		showHideElement(interimCauseCB, false);
		showHideElement(entrepreneurSupportCB, true);
		showHideElement(bonusCB, false);
	}

	private void set230and250View() {
		showHideElement(calendarFormativeStartDate, false);
		showHideElement(calendarFormativeEndDate, false);
		showHideElement(academicTitulationLB, false);
		showHideElement(profesionalityCB, false);
		showHideElement(teoricFormationLB, false);
		showHideElement(formationHoursTB, false);
		showHideElement(formationMinutesTB, false);
		showHideElement(retirementPercentTB, false);
		showHideElement(planRecoveryCB, false);
		showHideElement(writenContractCB, false);
		
		showHideElement(agreementHoursTB, true);
		showHideElement(agreementMinutesTB, true);
		showHideElement(repeatFDCB, true);
		showHideElement(journeyTypeLB, true);
		showHideElement(journeyDurationHoursTB, true);
		showHideElement(journeyDurationMinutesTB, true);

		showHideElement(workProgramDataCB, false);
		showHideElement(annexedCB, false);
		showHideElement(investCB, false);
		showHideElement(interimCauseCB, false);
		showHideElement(entrepreneurSupportCB, false);
		showHideElement(bonusCB, true);
	}

	private void set300View() {
		showHideElement(calendarFormativeStartDate, false);
		showHideElement(calendarFormativeEndDate, false);
		showHideElement(academicTitulationLB, false);
		showHideElement(profesionalityCB, false);
		showHideElement(agreementHoursTB, true);
		showHideElement(agreementMinutesTB, true);
		showHideElement(repeatFDCB, true);
		showHideElement(journeyTypeLB, true);
		showHideElement(journeyDurationHoursTB, true);
		showHideElement(journeyDurationMinutesTB, true);
		showHideElement(teoricFormationLB, false);
		showHideElement(formationHoursTB, false);
		showHideElement(formationMinutesTB, false);
		showHideElement(retirementPercentTB, false);
		showHideElement(planRecoveryCB, false);
		showHideElement(writenContractCB, false);
		
		showHideElement(workProgramDataCB, false);
		showHideElement(annexedCB, false);
		showHideElement(investCB, false);
		showHideElement(interimCauseCB, false);
		showHideElement(entrepreneurSupportCB, true);
		showHideElement(quoteReductionsCB, false);
		showHideElement(bonusCB, false);
	}

	private void set330and350View() {
		showHideElement(calendarFormativeStartDate, false);
		showHideElement(calendarFormativeEndDate, false);
		showHideElement(academicTitulationLB, false);
		showHideElement(profesionalityCB, false);
		showHideElement(repeatFDCB, false);
		showHideElement(agreementHoursTB, true);
		showHideElement(agreementMinutesTB, true);
		showHideElement(repeatFDCB, true);
		showHideElement(journeyTypeLB, true);
		showHideElement(journeyDurationHoursTB, true);
		showHideElement(journeyDurationMinutesTB, true);
		showHideElement(teoricFormationLB, false);
		showHideElement(formationHoursTB, false);
		showHideElement(formationMinutesTB, false);
		showHideElement(retirementPercentTB, false);
		showHideElement(planRecoveryCB, false);
		showHideElement(writenContractCB, false);

		showHideElement(workProgramDataCB, false);
		showHideElement(annexedCB, false);
		showHideElement(investCB, false);
		showHideElement(interimCauseCB, false);
		showHideElement(entrepreneurSupportCB, false);
		showHideElement(bonusCB, true);
	}

	private void set401View() {
		showHideElement(calendarFormativeStartDate, false);
		showHideElement(calendarFormativeEndDate, false);
		showHideElement(academicTitulationLB, false);
		showHideElement(profesionalityCB, false);
		showHideElement(repeatFDCB, false);
		showHideElement(agreementHoursTB, true);
		showHideElement(agreementMinutesTB, true);
		showHideElement(repeatFDCB, true);
		showHideElement(journeyTypeLB, true);
		showHideElement(journeyDurationHoursTB, true);
		showHideElement(journeyDurationMinutesTB, true);
		showHideElement(teoricFormationLB, false);
		showHideElement(formationHoursTB, false);
		showHideElement(formationMinutesTB, false);
		showHideElement(retirementPercentTB, false);
		showHideElement(planRecoveryCB, false);
		showHideElement(writenContractCB, false);

		showHideElement(interimCauseCB, false);
		showHideElement(contractReliefCB, false);
		showHideElement(entrepreneurSupportCB, false);
		showHideElement(promotionMeasuresCB, false);
		showHideElement(quoteReductionsCB, false);
		showHideElement(bonusCB, false);
	}

	private void set402View() {
		set402And403view();
	}

	private void set403View() {
		set402And403view();
	}

	private void set402And403view() {
		showHideElement(calendarFormativeStartDate, false);
		showHideElement(calendarFormativeEndDate, false);
		showHideElement(academicTitulationLB, false);
		showHideElement(profesionalityCB, false);
		showHideElement(agreementHoursTB, true);
		showHideElement(agreementMinutesTB, true);
		showHideElement(repeatFDCB, true);
		showHideElement(journeyTypeLB, true);
		showHideElement(journeyDurationHoursTB, true);
		showHideElement(journeyDurationMinutesTB, true);
		showHideElement(teoricFormationLB, false);
		showHideElement(formationHoursTB, false);
		showHideElement(formationMinutesTB, false);
		showHideElement(retirementPercentTB, false);
		showHideElement(planRecoveryCB, false);

		showHideElement(interimCauseCB, false);
		showHideElement(contractReliefCB, false);
		showHideElement(workshopSchoolCB, false);
		showHideElement(investCB, false);
		showHideElement(interimCauseCB, false);
		showHideElement(entrepreneurSupportCB, false);
		showHideElement(promotionMeasuresCB, false);
		showHideElement(quoteReductionsCB, false);
		showHideElement(bonusCB, false);
	}

	private void set410View() {
		set410And510View();
	}

	private void set420View() {
		showHideElement(calendarFormativeStartDate, false);
		showHideElement(calendarFormativeEndDate, false);
		showHideElement(agreementHoursTB, true);
		showHideElement(agreementMinutesTB, true);
		showHideElement(repeatFDCB, true);
		showHideElement(journeyTypeLB, true);
		showHideElement(journeyDurationHoursTB, true);
		showHideElement(journeyDurationMinutesTB, true);
		showHideElement(teoricFormationLB, false);
		showHideElement(formationHoursTB, false);
		showHideElement(formationMinutesTB, false);
		showHideElement(retirementPercentTB, false);
		showHideElement(planRecoveryCB, true);
		showHideElement(writenContractCB, false);

		showHideElement(contractReliefCB, false);
		showHideElement(annexedCB, false);
		showHideElement(quoteReductionsCB, false);
		showHideElement(interimCauseCB, false);
		showHideElement(entrepreneurSupportCB, false);
		showHideElement(promotionMeasuresCB, false);
		showHideElement(bonusCB, false);
	}

	private void set421View() {
		showHideElement(calendarFormativeStartDate, false);
		showHideElement(calendarFormativeEndDate, false);
		showHideElement(academicTitulationLB, false);
		showHideElement(profesionalityCB, false);
		showHideElement(agreementHoursTB, true);
		showHideElement(agreementMinutesTB, true);
		showHideElement(repeatFDCB, true);
		showHideElement(journeyTypeLB, true);
		showHideElement(journeyDurationHoursTB, true);
		showHideElement(journeyDurationMinutesTB, true);
		showHideElement(teoricFormationLB, true);
		showHideElement(formationHoursTB, true);
		showHideElement(formationMinutesTB, true);
		showHideElement(retirementPercentTB, false);
		showHideElement(planRecoveryCB, false);
		showHideElement(writenContractCB, false);

		showHideElement(contractReliefCB, false);
		showHideElement(annexedCB, false);
		showHideElement(entrepreneurSupportCB, false);
		showHideElement(promotionMeasuresCB, false);
		showHideElement(investCB, false);
		showHideElement(interimCauseCB, false);
		showHideElement(quoteReductionsCB, false);
		showHideElement(bonusCB, false);
	}

	private void set430View() {
		showHideElement(calendarFormativeStartDate, false);
		showHideElement(calendarFormativeEndDate, false);
		showHideElement(academicTitulationLB, false);
		showHideElement(profesionalityCB, false);
		showHideElement(agreementHoursTB, true);
		showHideElement(agreementMinutesTB, true);
		showHideElement(repeatFDCB, true);
		showHideElement(journeyTypeLB, true);
		showHideElement(journeyDurationHoursTB, true);
		showHideElement(journeyDurationMinutesTB, true);
		showHideElement(teoricFormationLB, false);
		showHideElement(formationHoursTB, false);
		showHideElement(formationMinutesTB, false);
		showHideElement(retirementPercentTB, false);
		showHideElement(planRecoveryCB, false);
		showHideElement(writenContractCB, false);

		showHideElement(contractReliefCB, false);
		showHideElement(annexedCB, false);
		showHideElement(investCB, false);
		showHideElement(interimCauseCB, false);
		showHideElement(quoteReductionsCB, false);
		showHideElement(bonusCB, true);
	}

	private void set441View() {
		showHideElement(calendarFormativeStartDate, false);
		showHideElement(calendarFormativeEndDate, false);
		showHideElement(academicTitulationLB, false);
		showHideElement(profesionalityCB, false);
		showHideElement(agreementHoursTB, true);
		showHideElement(agreementMinutesTB, true);
		showHideElement(repeatFDCB, true);
		showHideElement(journeyTypeLB, true);
		showHideElement(journeyDurationHoursTB, true);
		showHideElement(journeyDurationMinutesTB, true);
		showHideElement(teoricFormationLB, false);
		showHideElement(formationHoursTB, false);
		showHideElement(formationMinutesTB, false);
		showHideElement(retirementPercentTB, false);
		showHideElement(planRecoveryCB, false);
		showHideElement(writenContractCB, false);

		showHideElement(workProgramDataCB, false);
		showHideElement(annexedCB, false);
		showHideElement(investCB, false);
		showHideElement(interimCauseCB, false);
		showHideElement(entrepreneurSupportCB, false);
		showHideElement(promotionMeasuresCB, false);
		showHideElement(quoteReductionsCB, false);
		showHideElement(bonusCB, false);
	}

	private void set450View() {
		showHideElement(calendarFormativeStartDate, false);
		showHideElement(calendarFormativeEndDate, false);
		showHideElement(academicTitulationLB, false);
		showHideElement(agreementHoursTB, true);
		showHideElement(agreementMinutesTB, true);
		showHideElement(repeatFDCB, true);
		showHideElement(journeyTypeLB, true);
		showHideElement(journeyDurationHoursTB, true);
		showHideElement(journeyDurationMinutesTB, true);
		showHideElement(teoricFormationLB, false);
		showHideElement(formationHoursTB, false);
		showHideElement(formationMinutesTB, false);
		showHideElement(retirementPercentTB, false);
		showHideElement(planRecoveryCB, false);

		showHideElement(contractReliefCB, false);
		showHideElement(annexedCB, false);
		showHideElement(investCB, false);
		showHideElement(entrepreneurSupportCB, false);
		showHideElement(promotionMeasuresCB, false);
		showHideElement(quoteReductionsCB, false);
		showHideElement(bonusCB, true);
	}

	private void set452View() {
		showHideElement(calendarFormativeStartDate, false);
		showHideElement(calendarFormativeEndDate, false);
		showHideElement(academicTitulationLB, false);
		showHideElement(profesionalityCB, false);
		showHideElement(agreementHoursTB, true);
		showHideElement(agreementMinutesTB, true);
		showHideElement(repeatFDCB, true);
		showHideElement(journeyTypeLB, true);
		showHideElement(journeyDurationHoursTB, true);
		showHideElement(journeyDurationMinutesTB, true);
		showHideElement(teoricFormationLB, false);
		showHideElement(formationHoursTB, false);
		showHideElement(formationMinutesTB, false);
		showHideElement(retirementPercentTB, false);
		showHideElement(planRecoveryCB, false);

		showHideElement(contractReliefCB, false);
		showHideElement(annexedCB, false);
		showHideElement(investCB, false);
		showHideElement(interimCauseCB, false);
		showHideElement(entrepreneurSupportCB, false);
		showHideElement(promotionMeasuresCB, false);
		showHideElement(quoteReductionsCB, false);
		showHideElement(bonusCB, false);
		showHideElement(bonusCB, true);
	}

	private void set501View() {
		showHideElement(calendarFormativeStartDate, false);
		showHideElement(calendarFormativeEndDate, false);
		showHideElement(academicTitulationLB, false);
		showHideElement(profesionalityCB, false);
		showHideElement(agreementHoursTB, true);
		showHideElement(agreementMinutesTB, true);
		showHideElement(repeatFDCB, true);
		showHideElement(journeyTypeLB, true);
		showHideElement(journeyDurationHoursTB, true);
		showHideElement(journeyDurationMinutesTB, true);
		showHideElement(teoricFormationLB, false);
		showHideElement(formationHoursTB, false);
		showHideElement(formationMinutesTB, false);
		showHideElement(retirementPercentTB, false);
		showHideElement(planRecoveryCB, false);
		showHideElement(writenContractCB, false);

		showHideElement(interimCauseCB, false);
		showHideElement(contractReliefCB, false);
		showHideElement(entrepreneurSupportCB, false);
		showHideElement(promotionMeasuresCB, false);
		showHideElement(bonusCB, false);
	}

	private void set502View() {
		showHideElement(calendarFormativeStartDate, false);
		showHideElement(calendarFormativeEndDate, false);
		showHideElement(academicTitulationLB, false);
		showHideElement(profesionalityCB, false);
		showHideElement(agreementHoursTB, true);
		showHideElement(agreementMinutesTB, true);
		showHideElement(repeatFDCB, true);
		showHideElement(journeyTypeLB, true);
		showHideElement(journeyDurationHoursTB, true);
		showHideElement(journeyDurationMinutesTB, true);
		showHideElement(teoricFormationLB, false);
		showHideElement(formationHoursTB, false);
		showHideElement(formationMinutesTB, false);
		showHideElement(retirementPercentTB, false);
		showHideElement(planRecoveryCB, false);
		showHideElement(writenContractCB, false);

		showHideElement(workProgramDataCB, false);
		showHideElement(interimCauseCB, false);
		showHideElement(contractReliefCB, false);
		showHideElement(entrepreneurSupportCB, false);
		showHideElement(promotionMeasuresCB, false);
		showHideElement(investCB, false);
		showHideElement(bonusCB, false);
	}

	private void set503View() {
		set503And552View();
	}

	private void set510View() {
		set410And510View();
	}

	private void set410And510View() {
		showHideElement(calendarFormativeStartDate, false);
		showHideElement(calendarFormativeEndDate, false);
		showHideElement(academicTitulationLB, false);
		showHideElement(profesionalityCB, false);
		showHideElement(agreementHoursTB, true);
		showHideElement(agreementMinutesTB, true);
		showHideElement(repeatFDCB, true);
		showHideElement(journeyTypeLB, true);
		showHideElement(journeyDurationHoursTB, true);
		showHideElement(journeyDurationMinutesTB, true);
		showHideElement(teoricFormationLB, false);
		showHideElement(formationHoursTB, false);
		showHideElement(formationMinutesTB, false);
		showHideElement(retirementPercentTB, false);
		showHideElement(planRecoveryCB, false);
		showHideElement(writenContractCB, false);

		showHideElement(contractReliefCB, false);
		showHideElement(annexedCB, false);
		showHideElement(investCB, false);
		showHideElement(interimCauseCB, true);
		showHideElement(entrepreneurSupportCB, false);
		showHideElement(promotionMeasuresCB, false);
		showHideElement(quoteReductionsCB, false);
		showHideElement(bonusCB, false);
	}

	private void set520View() {
		showHideElement(calendarFormativeStartDate, false);
		showHideElement(calendarFormativeEndDate, false);
		showHideElement(agreementHoursTB, true);
		showHideElement(agreementMinutesTB, true);
		showHideElement(repeatFDCB, true);
		showHideElement(journeyTypeLB, true);
		showHideElement(journeyDurationHoursTB, true);
		showHideElement(journeyDurationMinutesTB, true);
		showHideElement(teoricFormationLB, false);
		showHideElement(formationHoursTB, false);
		showHideElement(formationMinutesTB, false);
		showHideElement(retirementPercentTB, false);
		showHideElement(planRecoveryCB, false);
		showHideElement(writenContractCB, false);

		showHideElement(contractReliefCB, false);
		showHideElement(annexedCB, false);
		showHideElement(investCB, false);
		showHideElement(interimCauseCB, false);
		showHideElement(entrepreneurSupportCB, false);
		showHideElement(promotionMeasuresCB, false);
		showHideElement(bonusCB, false);
	}

	private void set530View() {
		showHideElement(calendarFormativeStartDate, false);
		showHideElement(calendarFormativeEndDate, false);
		showHideElement(academicTitulationLB, false);
		showHideElement(profesionalityCB, false);
		showHideElement(agreementHoursTB, true);
		showHideElement(agreementMinutesTB, true);
		showHideElement(repeatFDCB, true);
		showHideElement(journeyTypeLB, true);
		showHideElement(journeyDurationHoursTB, true);
		showHideElement(journeyDurationMinutesTB, true);
		showHideElement(teoricFormationLB, false);
		showHideElement(formationHoursTB, false);
		showHideElement(formationMinutesTB, false);
		showHideElement(retirementPercentTB, false);
		showHideElement(planRecoveryCB, false);
		showHideElement(writenContractCB, false);

		showHideElement(contractReliefCB, false);
		showHideElement(annexedCB, false);
		showHideElement(investCB, false);
		showHideElement(interimCauseCB, false);
		showHideElement(promotionMeasuresCB, false);
		showHideElement(bonusCB, true);
	}

	private void set540View() {
		showHideElement(calendarFormativeStartDate, false);
		showHideElement(calendarFormativeEndDate, false);
		showHideElement(academicTitulationLB, false);
		showHideElement(profesionalityCB, false);
		showHideElement(agreementHoursTB, true);
		showHideElement(agreementMinutesTB, true);
		showHideElement(repeatFDCB, true);
		showHideElement(journeyTypeLB, true);
		showHideElement(journeyDurationHoursTB, true);
		showHideElement(journeyDurationMinutesTB, true);
		showHideElement(teoricFormationLB, false);
		showHideElement(formationHoursTB, false);
		showHideElement(formationMinutesTB, false);
		showHideElement(planRecoveryCB, false);
		showHideElement(writenContractCB, false);

		showHideElement(workProgramDataCB, false);
		showHideElement(contractReliefCB, false);
		showHideElement(annexedCB, false);
		showHideElement(investCB, false);
		showHideElement(interimCauseCB, false);
		showHideElement(entrepreneurSupportCB, false);
		showHideElement(promotionMeasuresCB, false);
		showHideElement(quoteReductionsCB, false);
		showHideElement(bonusCB, false);
	}

	private void set541View() {
		showHideElement(calendarFormativeStartDate, false);
		showHideElement(calendarFormativeEndDate, false);
		showHideElement(academicTitulationLB, false);
		showHideElement(profesionalityCB, false);
		showHideElement(agreementHoursTB, true);
		showHideElement(agreementMinutesTB, true);
		showHideElement(repeatFDCB, true);
		showHideElement(journeyTypeLB, true);
		showHideElement(journeyDurationHoursTB, true);
		showHideElement(journeyDurationMinutesTB, true);
		showHideElement(teoricFormationLB, false);
		showHideElement(formationHoursTB, false);
		showHideElement(formationMinutesTB, false);
		showHideElement(retirementPercentTB, false);
		showHideElement(planRecoveryCB, false);
		showHideElement(writenContractCB, false);

		showHideElement(workProgramDataCB, false);
		showHideElement(promotionMeasuresCB, false);
		showHideElement(annexedCB, false);
		showHideElement(investCB, false);
		showHideElement(interimCauseCB, false);
		showHideElement(entrepreneurSupportCB, false);
		showHideElement(promotionMeasuresCB, false);
		showHideElement(quoteReductionsCB, false);
		showHideElement(bonusCB, false);
	}

	private void set550View() {
		showHideElement(calendarFormativeStartDate, false);
		showHideElement(calendarFormativeEndDate, false);
		showHideElement(academicTitulationLB, false);
		showHideElement(agreementHoursTB, true);
		showHideElement(agreementMinutesTB, true);
		showHideElement(repeatFDCB, true);
		showHideElement(journeyTypeLB, true);
		showHideElement(journeyDurationHoursTB, true);
		showHideElement(journeyDurationMinutesTB, true);
		showHideElement(teoricFormationLB, false);
		showHideElement(formationHoursTB, false);
		showHideElement(formationMinutesTB, false);
		showHideElement(retirementPercentTB, false);
		showHideElement(planRecoveryCB, false);

		showHideElement(contractReliefCB, false);
		showHideElement(annexedCB, false);
		showHideElement(investCB, false);
		showHideElement(entrepreneurSupportCB, false);
		showHideElement(promotionMeasuresCB, false);
		showHideElement(bonusCB, true);
	}

	private void set552View() {
		set503And552View();
	}

	private void set503And552View() {
		showHideElement(calendarFormativeStartDate, false);
		showHideElement(calendarFormativeEndDate, false);
		showHideElement(academicTitulationLB, false);
		showHideElement(profesionalityCB, false);
		showHideElement(agreementHoursTB, true);
		showHideElement(agreementMinutesTB, true);
		showHideElement(repeatFDCB, true);
		showHideElement(journeyTypeLB, true);
		showHideElement(journeyDurationHoursTB, true);
		showHideElement(journeyDurationMinutesTB, true);
		showHideElement(teoricFormationLB, false);
		showHideElement(formationHoursTB, false);
		showHideElement(formationMinutesTB, false);
		showHideElement(retirementPercentTB, false);
		showHideElement(planRecoveryCB, false);

		showHideElement(contractReliefCB, false);
		showHideElement(workshopSchoolCB, false);
		showHideElement(annexedCB, false);
		showHideElement(investCB, false);
		showHideElement(interimCauseCB, false);
		showHideElement(entrepreneurSupportCB, false);
		showHideElement(promotionMeasuresCB, false);
		showHideElement(quoteReductionsCB, false);
		showHideElement(bonusCB, true);
	}

	private void set970and990View() {
		showHideElement(calendarFormativeStartDate, false);
		showHideElement(calendarFormativeEndDate, false);
		showHideElement(academicTitulationLB, false);
		showHideElement(profesionalityCB, false);
		showHideElement(agreementHoursTB, true);
		showHideElement(agreementMinutesTB, true);
		showHideElement(repeatFDCB, true);
		showHideElement(journeyTypeLB, true);
		showHideElement(journeyDurationHoursTB, true);
		showHideElement(journeyDurationMinutesTB, true);
		showHideElement(teoricFormationLB, false);
		showHideElement(formationHoursTB, false);
		showHideElement(formationMinutesTB, false);
		showHideElement(retirementPercentTB, false);
		showHideElement(planRecoveryCB, false);

		showHideElement(contractReliefCB, false);
		showHideElement(investCB, false);
		showHideElement(interimCauseCB, false);
		showHideElement(entrepreneurSupportCB, false);
		showHideElement(promotionMeasuresCB, false);
		showHideElement(quoteReductionsCB, false);
		showHideElement(annexedCB, false);
		showHideElement(older52CB, false);
		showHideElement(bonusCB, false);
	}

	private void set980View() {
		showHideElement(calendarFormativeStartDate, false);
		showHideElement(calendarFormativeEndDate, false);

		showHideElement(academicTitulationLB, false);
		showHideElement(profesionalityCB, false);
		showHideElement(agreementHoursTB, true);
		showHideElement(agreementMinutesTB, true);
		showHideElement(repeatFDCB, true);
		showHideElement(journeyTypeLB, true);
		showHideElement(journeyDurationHoursTB, true);
		showHideElement(journeyDurationMinutesTB, true);
		showHideElement(teoricFormationLB, false);
		showHideElement(formationHoursTB, false);
		showHideElement(formationMinutesTB, false);
		showHideElement(retirementPercentTB, false);
		showHideElement(planRecoveryCB, false);
		showHideElement(writenContractCB, false);

		showHideElement(workProgramDataCB, false);
		showHideElement(contractReliefCB, false);
		showHideElement(investCB, false);
		showHideElement(interimCauseCB, false);
		showHideElement(entrepreneurSupportCB, false);
		showHideElement(promotionMeasuresCB, false);
		showHideElement(quoteReductionsCB, false);
		showHideElement(annexedCB, false);
		showHideElement(older52CB, false);
		showHideElement(bonusCB, false);
	}

	private void showHideTransformRows() {
		showHideElement(discCB, isTransform || isExtension);
		showHideElement(discReasonLB, isTransform || isExtension);
	}

	public com.esferalia.aon.gwt.payroll.shared.ContractSpecificData getContractSpecificData() {
		return this.contractSpecificData;
	}

	// ------------------------------------------------- Database Methods (Specific Data)

	private void getCnos(Consumer<Void> success) {
		impl.getCNOs(new AsyncCallback<Map<String, CNO>>() {

			@Override
			public void onFailure(Throwable caught) {
				// Nothing to do here
			}

			@Override
			public void onSuccess(Map<String, CNO> result) {
				cnoMap = result;
				success.accept(null);
			}
		});
	}

	public void getContractSpecificData(Consumer<com.esferalia.aon.gwt.payroll.shared.ContractSpecificData> success,
			Consumer<Throwable> failure) {
		Integer contractId = contractEmployeeInfo.getContractInfo().getContractId();
		impl.getContractSpecificData(contractId,
				new AsyncCallback<com.esferalia.aon.gwt.payroll.shared.ContractSpecificData>() {

					@Override
					public void onSuccess(com.esferalia.aon.gwt.payroll.shared.ContractSpecificData result) {
						contractEmployeeInfo.setContractSpecificData(result);
						success.accept(result);
					}

					@Override
					public void onFailure(Throwable caught) {
						failure.accept(caught);
					}
				});
	}

	public void saveSepe() {
		showLoadingMessage("Guardando datos Sepe del contrato...");
		setContractSpecificData(contractSpecificData, s -> {
			showSuccessMessage("Datos Sepe", "Datos Sepe del contrato guardados correctamente");
			Timer timer = new Timer() {
				@Override
				public void run() {
					reloadSepeData(s -> {
					});
				}
			};
			timer.schedule(2500);
		}, f -> showErrorMessage("Error obtenci\u00f3n Datos Sepe contrato", f.getMessage()));
	}

	public void syncComunicationsData() {
		showLoadingMessage("Sincronizando comunicaciones del Sepe...");
		implEmployee.getSepeComunicationData(
				contractEmployeeInfo.getContractInfo().getEnterpriseCIF(),
				contractEmployeeInfo.getEmployeeInfo().getDocument(),
				contractEmployeeInfo.getContractInfo().getStartDate(),
				contractEmployeeInfo.getContractInfo().getContractId(), new AsyncCallback<Map<String, String>>() {

					@Override
					public void onSuccess(Map<String, String> sepeData) {
						showSuccessMessage("Sincronizaci\u00f3n Sepe",
								"Sincronizaci\u00f3n con el Sepe realizada correctamente");

						Timer timer = new Timer() {
							@Override
							public void run() {
								reloadSepeData(s -> {
								});
							}
						};
						timer.schedule(2500);
					}

					@Override
					public void onFailure(Throwable caught) {
						showErrorMessage("Error sincronizaci\u00f3n Sepe", caught.getMessage());
					}
				});
	}

	public void setContractSpecificData(com.esferalia.aon.gwt.payroll.shared.ContractSpecificData contractSpecificData,
			Consumer<Void> success, Consumer<Throwable> failure) {
		contractEmployeeInfo.setContractSpecificData(contractSpecificData);
		impl.setContractSpecificData(contractEmployeeInfo, new AsyncCallback<Void>() {

			@Override
			public void onSuccess(Void result) {
				success.accept(result);
			}

			@Override
			public void onFailure(Throwable caught) {
				failure.accept(caught);
			}
		});
	}

	// --------------------------------------------------------- Abstract Methods

	protected abstract void showErrorMessage(String title, String message);

	protected abstract void showSuccessMessage(String title, String message);

	protected abstract void showLoadingMessage(String message);

	protected abstract void hideMessagePanel();

	protected abstract void downloadCtoDocument();

	protected abstract void downloadCtoTransformDocument();

	protected abstract void downloadCtoExtensionDocument();

}
