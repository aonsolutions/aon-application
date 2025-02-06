package com.esferalia.aon.gwt.payroll.client;

import java.util.Map;
import java.util.function.Consumer;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomCard;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomCheckBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomListBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomTextBox;
import com.esferalia.aon.gwt.payroll.shared.EmployeeContractInfo;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

public abstract class ContractOtherDataWidget extends FlowPanel {

	private AonCustomCard representativeTable; // Representante empresa
	
	private AonCustomTextBox enterpriseAgentNameTB = new AonCustomTextBox("Nombre");
	private AonCustomTextBox enterpriseAgentSurnameTB = new AonCustomTextBox("Apellidos");
	private AonCustomTextBox enterpriseAgentNIFTB = new AonCustomTextBox("NIF");
	private AonCustomTextBox enterpriseAgentPositionTB = new AonCustomTextBox("Cargo del representante");
	
	private AonCustomCard minorAgentTable; // Representante del menor
	
	private AonCustomTextBox minorAgentTB = new AonCustomTextBox("Nombre");
	private AonCustomTextBox minorAgentNIFTB = new AonCustomTextBox("NIF");
	private AonCustomTextBox minorAgentQualityOfTB = new AonCustomTextBox("Calidad de");
	
	// -------------------------------------------------- Indefinite Cards
	
	private AonCustomCard indefiniteTable; // Otros datos del contrato

	private AonCustomTextBox doingFunctionsTB = new AonCustomTextBox("Funciones a realizar");
	private AonCustomCheckBox distanceB = new AonCustomCheckBox("A distancia");
	private AonCustomTextBox distanceAddressTB = new AonCustomTextBox("En el domicilio ubicado en");
	private AonCustomTextBox discontinuousWorkTB = new AonCustomTextBox("Trabajos discontinuos consistentes en");
	private AonCustomTextBox intermittentCyclicalActivityTB = new AonCustomTextBox("Actividad c\u00edclica intermitente de");
	private AonCustomTextBox durationFDTB = new AonCustomTextBox("Duraci\u00f3n (FD)");
	private AonCustomTextBox activityStimationDurationFDTB = new AonCustomTextBox("Duraci\u00f3n estimada de la actividad (FD)");
	private AonCustomTextBox journeyHoursFDTB = new AonCustomTextBox("Horas jornada (FD)");
	private AonCustomTextBox journeyPeriodFDTB = new AonCustomTextBox("Periodo jornada (FD)");
	private AonCustomTextBox timeDistributionFDTB = new AonCustomTextBox("Distribuci\u00f3n horaria (FD)");
	private AonCustomCheckBox partialTimeB = new AonCustomCheckBox("Se acoge a modalidad tiempo parcial");
	private AonCustomTextBox journeyHoursTCTB = new AonCustomTextBox("Horas jornada (TC)");
	private AonCustomTextBox startJourneyTCTB = new AonCustomTextBox("Comienzo de la jornada (TC)");
	private AonCustomTextBox endJourneyTCTB = new AonCustomTextBox("T\u00e9rmino de la jornada (TC)");
	private AonCustomTextBox journeyHoursTPTB = new AonCustomTextBox("Horas jornada (TP)");
	private AonCustomTextBox agreementJourneyHoursTB = new AonCustomTextBox("Horas jornada por conv. colectivo");
	private AonCustomCheckBox complementaryHoursB = new AonCustomCheckBox("Horas complementarias");
	private AonCustomTextBox trialPeriodTB = new AonCustomTextBox("Per\u00edodo de prueba");
	private AonCustomTextBox salaryAmountTB = new AonCustomTextBox("Importe salario");
	private AonCustomTextBox salaryPeriodTB = new AonCustomTextBox("Periodo salario");
	private AonCustomTextBox salaryConceptTB = new AonCustomTextBox("Concepto salario");
	private AonCustomTextBox holidaysTB = new AonCustomTextBox("Vacaciones");
	private AonCustomTextBox sepeOfficeTB = new AonCustomTextBox("Municipio de la oficina SEPE");
	private AonCustomTextBox accreditedDisabilityTB = new AonCustomTextBox("Discapacidad acreditada por");
	private AonCustomListBox withoutDisabilitySevereLB = new AonCustomListBox("Sin discapacidad severa");
	private AonCustomListBox disabilitySevereLB = new AonCustomListBox("Discapacidad severa");
	private AonCustomTextBox subsidyTB = new AonCustomTextBox("Subvenci\u00f3n de");
	private AonCustomCheckBox fourthLawB = new AonCustomCheckBox("4 de la Ley 3/2012, de 6 de julio ( BOE de 7 de julio )");
	private AonCustomListBox unemploymentLB = new AonCustomListBox("Desempleados entre 16 y 30 a\u00f1os");
	private AonCustomListBox unemploymentOldLB = new AonCustomListBox("Desempleados mayores de 45 a\u00f1os");
	private AonCustomCheckBox benefitsPerceptorB = new AonCustomCheckBox("Percerptor de prestaciones durante al menos 3 meses");
	private AonCustomCheckBox firstEmployeeB = new AonCustomCheckBox("Primer trabajador y menor de 30 a\u00f1os");
	private AonCustomListBox employeeLB = new AonCustomListBox("El trabajador/a es");
	private AonCustomTextBox agreementLineOneTB = new AonCustomTextBox("Convenio colectivo (linea 1");
	private AonCustomTextBox agreementLineTwoTB = new AonCustomTextBox("Convenio colectivo (linea 2)");
	private AonCustomCheckBox contactHoursB = new AonCustomCheckBox("Acuerdo de horas presenciales");
	private AonCustomTextBox hoursTB = new AonCustomTextBox("Horas");
	private AonCustomListBox remunerationFormLB = new AonCustomListBox("Forma retribuci\u00f3n");
	private AonCustomCheckBox overnightAgreementB = new AonCustomCheckBox("Acuerdo de pernocta");
	private AonCustomTextBox overnightRegimeTB = new AonCustomTextBox("R\u00e9gimen de pernoctas");
	private AonCustomCheckBox quoteReductionTCB = new AonCustomCheckBox("Reducci\u00f3n de cuotas (T. Completo)");
	private AonCustomCheckBox quoteReductionFDB = new AonCustomCheckBox("Reducci\u00f3n de cuotas (Fijo/Discontinuo)");
	private AonCustomTextBox sepeOfficeCOTB = new AonCustomTextBox("Municipio de la oficina SEPE (contrato origen)");
	
	// -------------------------------------------------- Temporal Table
	
	private AonCustomCard temporalTable; //Otros datos del contrato
	
	private AonCustomTextBox doingFunctionsTempTB = new AonCustomTextBox("Funciones a realizar");
	private AonCustomCheckBox distanceTempB = new AonCustomCheckBox("A distancia");
	private AonCustomTextBox distanceAddressTempTB = new AonCustomTextBox("En el domicilio ubicado en");
	private AonCustomTextBox journeyHoursTCTempTB = new AonCustomTextBox("Horas jornada (TC)");
	private AonCustomTextBox startJourneyTCTempTB = new AonCustomTextBox("Comienzo de la jornada (TC)");
	private AonCustomTextBox endJourneyTCTempTB = new AonCustomTextBox("T\u00e9rmino de la jornada (TC)");
	private AonCustomTextBox lowJourneyTempTB = new AonCustomTextBox("Siendo esta jornada inferior a");
	private AonCustomTextBox timeDistributionTempTB = new AonCustomTextBox("Distribuci\u00f3n del tiempo de trabajo (TP)");
	private AonCustomCheckBox complementaryHoursTempB = new AonCustomCheckBox("Horas complementarias");
	private AonCustomTextBox endContractTempTB = new AonCustomTextBox("Texto fin de contrato");
	private AonCustomTextBox trialPeriodTempTB = new AonCustomTextBox("Per\u00edodo de prueba");
	private AonCustomCheckBox permitedHighDurationTempB = new AonCustomCheckBox("Duraci\u00f3n mayor permitida por conv. coletivo");
	private AonCustomTextBox salaryAmountTempTB = new AonCustomTextBox("Importe salario");
	private AonCustomTextBox salaryPeriodTempTB = new AonCustomTextBox("Per\u00edodo salario");
	private AonCustomTextBox salaryConceptTempTB = new AonCustomTextBox("Concepto salario");
	private AonCustomTextBox holidaysTempTB = new AonCustomTextBox("Vacaciones");
	private AonCustomTextBox sepeOfficeTempTB = new AonCustomTextBox("Municipio de la oficina SEPE");
	private AonCustomTextBox workTempTB = new AonCustomTextBox("Obra o servicio a realizar");
	private AonCustomTextBox workMoreTempTB = new AonCustomTextBox("Obra o servicio a realizar (m\u00e1s)");
	private AonCustomTextBox taskTempTB = new AonCustomTextBox("Tareas a realizar");
	private AonCustomTextBox taskMoreTempTB = new AonCustomTextBox("Tareas a realizar (m\u00e1s)");
	private AonCustomTextBox sustituteEmployeeTempTB = new AonCustomTextBox("Trabajador a sustituir");
	private AonCustomListBox requirementsTempLB = new AonCustomListBox("Requisitos a cumplir");
	private AonCustomListBox formationTempLB = new AonCustomListBox("La formaci\u00f3n se");
	private AonCustomListBox formationWillTempLB = new AonCustomListBox("La formaci\u00f3n ser\u00e1");
	private AonCustomTextBox officeSPEmployeeTempTB = new AonCustomTextBox("Oficina de Servicios P\u00fablicos de Empleo");
	private AonCustomTextBox lenguageFormationTempTB = new AonCustomTextBox("Formaci\u00f3n en idiomas o tecnolog\u00edas consistentes en");
	private AonCustomCheckBox hoursDealTempB = new AonCustomCheckBox("Acuerdo de horas de presencia del empleador");
	private AonCustomTextBox presentHoursTempTB = new AonCustomTextBox("Horas de presencia");
	private AonCustomTextBox distributionHoursTempTB = new AonCustomTextBox("Distribuci\u00f3n de las horas");
	private AonCustomListBox timeCompensationTempLB = new AonCustomListBox("Compensaci\u00f3n del tiempo de presencia");
	private AonCustomCheckBox dealOvernightB = new AonCustomCheckBox("Acuerdo para pernoctar en el servicio del empleador");
	private AonCustomTextBox overnightRegimeTempTB = new AonCustomTextBox("R\u00e9gimen de pernoctar");
	private AonCustomTextBox officialOrganismTempTB = new AonCustomTextBox("Organismo oficial que emite la certificaci\u00f3n");
	private AonCustomListBox withoutSevereDisTempLB = new AonCustomListBox("Trabajadores sin discapacidad severa");
	private AonCustomListBox severeDisTempLB = new AonCustomListBox("Trabajadores con discapacidad severa");
	private AonCustomTextBox adaptationPeriodTempTB = new AonCustomTextBox("Per\u00edodo de adaptaci\u00f3n al trabajo");
	private AonCustomTextBox adaptationConditionsTempTB = new AonCustomTextBox("Condiciones de adaptaci\u00f3n");
	private AonCustomListBox adaptationWorkTempLB = new AonCustomListBox("Adaptaciones al puesto de trabajo");
	private AonCustomTextBox socialPersonalAdjustTempTB = new AonCustomTextBox("Ajuste de personal y social");
	private AonCustomTextBox socialPersonalAdjustMoreTempTB = new AonCustomTextBox("Ajuste de personal y social (m\u00e1s)");
	private AonCustomTextBox colectiveAgreementTempTB = new AonCustomTextBox("Convenio colectivo");
	
	// -------------------------------------------------- Formation Table
	
	private AonCustomCard formationTable; //Otros datos del contrato
	
	private AonCustomCheckBox ssReductionFormB = new AonCustomCheckBox("Reducci\u00f3n de cuotas S.S.");
	private AonCustomListBox employeeFormLB = new AonCustomListBox("El trabajador/a es");
	private AonCustomTextBox workplaceFormTB = new AonCustomTextBox("Ubicaci\u00f3n centro de trabajo");
	private AonCustomTextBox tutorFormTB = new AonCustomTextBox("Tutor de la formaci\u00f3n");
	private AonCustomTextBox efectiveWorkHoursFormTB = new AonCustomTextBox("Horario del trabajo efectivo");
	private AonCustomTextBox activityHoursFormTB = new AonCustomTextBox("Horario de la actividad formativa");
	private AonCustomTextBox trialPeriodFormTB = new AonCustomTextBox("Per\u00edodo de prueba");
	private AonCustomCheckBox agreementTrialFormB = new AonCustomCheckBox("Per\u00edodo de prueba de mayor duraci\u00f3n por convenio");
	private AonCustomTextBox salaryAmountFormTB = new AonCustomTextBox("Importe salario");
	private AonCustomTextBox salaryPeriodFormTB = new AonCustomTextBox("Per\u00edodo salario");
	private AonCustomTextBox holidaysFormTB = new AonCustomTextBox("Vacaciones");
	private AonCustomCheckBox degreeExistFormB = new AonCustomCheckBox("Existe t\u00edtulo de F.P., cert. de prof. y centro disponible (Anexo I)");
	private AonCustomCheckBox degreeExist2FormB = new AonCustomCheckBox("Existe t\u00edtulo de F.P., cert. de prof. y centro disponible (Anexo II)");

	// -------------------------------------------------- Practice Table
	
	private AonCustomCard practiceTable; //Otros datos del contrato
	
	private AonCustomTextBox profesionalCertPracTB = new AonCustomTextBox("Cert. de profesional en posesi\u00f3n");
	private AonCustomTextBox obtainingDatePracTB = new AonCustomTextBox("O fecha de obtenci\u00f3n");
	private AonCustomTextBox disabilityCertPracTB = new AonCustomTextBox("Cert. de discapacidad expedida por");
	private AonCustomTextBox disabilityCertMorePracTB = new AonCustomTextBox("Cert. de discapacidad expedida por (m\u00e1s)");
	private AonCustomListBox firstContractPracLB = new AonCustomListBox("Primer contrato en pr\u00e1cticas");
	private AonCustomTextBox journeyHoursPracTB = new AonCustomTextBox("Horas de jornada (TC)");
	private AonCustomTextBox startJourneyPracTB = new AonCustomTextBox("Comienzo de la jornada (TC)");
	private AonCustomTextBox endJourneyPracTB = new AonCustomTextBox("T\u00e9rmino de la jornada (TC)");
	private AonCustomTextBox distributionJourneyPracTB = new AonCustomTextBox("Distribuci\u00f3n de la jornada");
	private AonCustomTextBox trialPeriodPracTB = new AonCustomTextBox("Per\u00edodo de prueba");
	private AonCustomTextBox salaryAmountPracTB = new AonCustomTextBox("Importe salario");
	private AonCustomTextBox salaryPeriodPracTB = new AonCustomTextBox("Per\u00edodo salario");
	private AonCustomTextBox salaryConceptPracTB = new AonCustomTextBox("Concepto salario");
	private AonCustomTextBox holidaysPracTB = new AonCustomTextBox("Vacaciones");
	private AonCustomTextBox sepeComunicationPracTB = new AonCustomTextBox("Comunicar al SEPE de");
	private AonCustomTextBox endSepeComunicationPracTB = new AonCustomTextBox("Comunicar fin dela relaci\u00f3n laboral al SEPE de");
	private AonCustomListBox unemploymentSubsidyPracLB = new AonCustomListBox("Beneficiario de subsidio por desempleo");
	private AonCustomTextBox adaptationPeriodPracTB = new AonCustomTextBox("Per\u00edodo de adaptaci\u00f3n al trabajo");
	private AonCustomTextBox adaptationConditionsPracTB = new AonCustomTextBox("Condiciones de adaptaci\u00f3n");
	private AonCustomTextBox adaptationWorkPracTB = new AonCustomTextBox("Adaptaciones al puesto de trabajo");
	private AonCustomTextBox personalSocialAdjustPracTB = new AonCustomTextBox("Ajustes de personal y social");
	private AonCustomTextBox personalSocialAdjustMorePracTB = new AonCustomTextBox("Ajustes de personal y social (m\u00e1s)");
	private AonCustomListBox motivationPracLB = new AonCustomListBox("Motivaci\u00f3n");
	private AonCustomListBox employerPracLB = new AonCustomListBox("Empleador");
	
	private DomainEnterprisesServiceAsync enterprisesService = DomainEnterprisesServiceAsync.newInstance();
	private EmployeeContractInfo contractEmployeeInfo;
	private Integer contractType;
	
	// ------------------------------------------------------ Constructor ---------------------------------------------------------

	protected ContractOtherDataWidget() {
		addStyleName(AON.CSS.aonItemFlex());
		addStyleName(AON.CSS.aonFlexColumn());
		getElement().getStyle().setProperty("gap", "1rem");
		getElement().getStyle().setProperty("margin", "1rem 0");
	}
	
	// -------------------------------------------------- setEmployeeContractInfo --------------------------------------------------
	
	public void setEmployeeContractInfo(EmployeeContractInfo contractEmployeeInfo) {
		this.contractEmployeeInfo = contractEmployeeInfo;
		this.contractType = getContractType(contractEmployeeInfo.getContractInfo().getContractType());
		initializeView();
	}
	
	private Integer getContractType(String contractTypeStr) {
		return AonStringUtils.isBlank(contractTypeStr) ? -1 : Integer.parseInt(contractTypeStr);
	}
	
	private void initializeView() {
		getContractOtherDataDB(
				s -> fillContractOtherData(), 
				f -> showErrorMessage("Error obtenci\u00f3n Otros Datos", f.getMessage()));	
	}
	
	private void fillContractOtherData() {
		clear();
		
        createRepresentativeTable();
        
        switch (contractType) {
            case 421: initializeFormationCards(); break;
            case 420: case 520: initializePracticeCards(); break;
            default:
                if (contractType >= 100 && contractType <= 400)  initializeIndefiniteCards();
                else initializeTemporalCards();
                break;
        }
	}
	
	private void createRepresentativeTable() {
		FlowPanel table = createFlexPanel();
		table.getElement().getStyle().setProperty("justify-content", "center");
		table.getElement().getStyle().setProperty("gap", "1rem");
		
        representativeTable = createCard("Representante empresa", createRepresentativeDataTable());
        addRepresentativeDataTableHadlers();
        
        minorAgentTable = createCard("Representante del menor", createMinorAgentDataTable());
        addMinorAgentDataTableHadlers();
       
        table.add(representativeTable);
        table.add(minorAgentTable);
        add(table);
	}

	private Widget createRepresentativeDataTable() {
		FlowPanel table = createFlexColumnPanel();
		
		table.add(createRow(enterpriseAgentNameTB, enterpriseAgentSurnameTB));
		table.add(createRow(enterpriseAgentNIFTB, enterpriseAgentPositionTB));
		
		enterpriseAgentNameTB.setValue("");
		enterpriseAgentSurnameTB.setValue("");
		enterpriseAgentNIFTB.setValue("");
		enterpriseAgentPositionTB.setValue("");
		
		if(contractType >= 100 && contractType <= 400) { // Indefinite
			enterpriseAgentNameTB.setValue(getEnterpriseAgentName(getContractOtherData("I_ENTERPRISE_DIR_STAFF_NAME")));
			enterpriseAgentSurnameTB.setValue(getEnterpriseAgentSurname(getContractOtherData("I_ENTERPRISE_DIR_STAFF_NAME")));
			enterpriseAgentNIFTB.setValue(getContractOtherData("I_ENTERPRISE_DIR_STAFF_NIF"));
			enterpriseAgentPositionTB.setValue(getContractOtherData("I_ENTERPRISE_DIR_STAFF_CHARGE"));
		} else if (contractType == 421) { // Formation
			enterpriseAgentNameTB.setValue(getEnterpriseAgentName(getContractOtherData("L_ENTERPRISE_DIR_STAFF_NAME")));
			enterpriseAgentSurnameTB.setValue(getEnterpriseAgentSurname(getContractOtherData("L_ENTERPRISE_DIR_STAFF_NAME")));
			enterpriseAgentNIFTB.setValue(getContractOtherData("L_ENTERPRISE_DIR_STAFF_NIF"));
			enterpriseAgentPositionTB.setValue(getContractOtherData("L_ENTERPRISE_DIR_STAFF_CHARGE"));
		} else if (contractType == 420 || contractType == 520) { // Practice
			enterpriseAgentNameTB.setValue(getEnterpriseAgentName(getContractOtherData("P_ENTERPRISE_DIR_STAFF_NAME")));
			enterpriseAgentSurnameTB.setValue(getEnterpriseAgentSurname(getContractOtherData("P_ENTERPRISE_DIR_STAFF_NAME")));
			enterpriseAgentNIFTB.setValue(getContractOtherData("P_ENTERPRISE_DIR_STAFF_NIF"));
			enterpriseAgentPositionTB.setValue(getContractOtherData("P_ENTERPRISE_DIR_STAFF_CHARGE"));
		} else { // Temporal
			enterpriseAgentNameTB.setValue(getEnterpriseAgentName(getContractOtherData("T_ENTERPRISE_DIR_STAFF_NAME")));
			enterpriseAgentSurnameTB.setValue(getEnterpriseAgentSurname(getContractOtherData("T_ENTERPRISE_DIR_STAFF_NAME")));
			enterpriseAgentNIFTB.setValue(getContractOtherData("T_ENTERPRISE_DIR_STAFF_NIF"));
			enterpriseAgentPositionTB.setValue(getContractOtherData("T_ENTERPRISE_DIR_STAFF_CHARGE"));
		}
		
		return table;
	}

	private void addRepresentativeDataTableHadlers() {
		enterpriseAgentNameTB.addValueChangeHandler(e -> {
			if(contractType >= 100 && contractType <= 400) // Indefinite
				setContractOtherData("I_ENTERPRISE_DIR_STAFF_NAME", getFullName());
			else if (contractType == 421) // Formation
				setContractOtherData("L_ENTERPRISE_DIR_STAFF_NAME", getFullName());
			else if (contractType == 420 || contractType == 520) // Practice
				setContractOtherData("P_ENTERPRISE_DIR_STAFF_NAME", getFullName());
			else // Temporal
				setContractOtherData("T_ENTERPRISE_DIR_STAFF_NAME", getFullName());
		});
		
		enterpriseAgentSurnameTB.addValueChangeHandler(e -> {
			if(contractType >= 100 && contractType <= 400) // Indefinite
				setContractOtherData("I_ENTERPRISE_DIR_STAFF_NAME", getFullName());
			else if (contractType == 421) // Formation
				setContractOtherData("L_ENTERPRISE_DIR_STAFF_NAME", getFullName());
			else if (contractType == 420 || contractType == 520) // Practice
				setContractOtherData("P_ENTERPRISE_DIR_STAFF_NAME", getFullName());
			else // Temporal
				setContractOtherData("T_ENTERPRISE_DIR_STAFF_NAME", getFullName());	
		});
		
		enterpriseAgentNIFTB.addValueChangeHandler(e -> {
			if(contractType >= 100 && contractType <= 400) // Indefinite
				setContractOtherData("I_ENTERPRISE_DIR_STAFF_NIF", enterpriseAgentNIFTB.getValue());
			else if (contractType == 421) // Formation
				setContractOtherData("L_ENTERPRISE_DIR_STAFF_NIF", enterpriseAgentNIFTB.getValue());
			else if (contractType == 420 || contractType == 520) // Practice
				setContractOtherData("P_ENTERPRISE_DIR_STAFF_NIF", enterpriseAgentNIFTB.getValue());
			else // Temporal
				setContractOtherData("T_ENTERPRISE_DIR_STAFF_NIF", enterpriseAgentNIFTB.getValue());
		});
		
		enterpriseAgentPositionTB.addValueChangeHandler(e -> {
			if(contractType >= 100 && contractType <= 400) { // Indefinite
				setContractOtherData("I_ENTERPRISE_DIR_STAFF_CHARGE", enterpriseAgentPositionTB.getValue());
			} else if (contractType == 421) { // Formation
				setContractOtherData("L_ENTERPRISE_DIR_STAFF_CHARGE", enterpriseAgentPositionTB.getValue());
			} else if (contractType == 420 || contractType == 520) { // Practice
				setContractOtherData("P_ENTERPRISE_DIR_STAFF_CHARGE", enterpriseAgentPositionTB.getValue());
			} else { // Temporal
				setContractOtherData("T_ENTERPRISE_DIR_STAFF_CHARGE", enterpriseAgentPositionTB.getValue());
			}
		});
	}
	
	private Widget createMinorAgentDataTable() {
		FlowPanel table = createFlexColumnPanel();
		
		table.add(createRow(minorAgentTB, minorAgentNIFTB));
		table.add(createRow(minorAgentQualityOfTB, null));
		
		minorAgentTB.setValue("");
		minorAgentNIFTB.setValue("");
		minorAgentQualityOfTB.setValue("");
		
		if(contractType >= 100 && contractType <= 400) { // Indefinite
			minorAgentTB.setValue(getContractOtherData("I_LEGAL_REPRESENTATIVE_NAME"));
			minorAgentNIFTB.setValue(getContractOtherData("I_LEGAL_REPRESENTATIVE_NIF"));
			minorAgentQualityOfTB.setValue(getContractOtherData("I_LEGAL_REPRESENTATIVE_CHARGE"));
		} else if (contractType == 421) { // Formation
			minorAgentTB.setValue(getContractOtherData("L_LEGAL_REPRESENTATIVE_NAME"));
			minorAgentNIFTB.setValue(getContractOtherData("L_LEGAL_REPRESENTATIVE_NIF"));
			minorAgentQualityOfTB.setValue(getContractOtherData("L_LEGAL_REPRESENTATIVE_CHARGE"));
		} else if (contractType == 420 || contractType == 520) { // Practice
			minorAgentTB.setValue(getContractOtherData("P_LEGAL_REPRESENTATIVE_NAME"));
			minorAgentNIFTB.setValue(getContractOtherData("P_LEGAL_REPRESENTATIVE_NIF"));
			minorAgentQualityOfTB.setValue(getContractOtherData("P_LEGAL_REPRESENTATIVE_CHARGE"));
		} else { // Temporal
			minorAgentTB.setValue(getContractOtherData("T_LEGAL_REPRESENTATIVE_NAME"));
			minorAgentNIFTB.setValue(getContractOtherData("T_LEGAL_REPRESENTATIVE_NIF"));
			minorAgentQualityOfTB.setValue(getContractOtherData("T_LEGAL_REPRESENTATIVE_CHARGE"));
		}
		
		return table;
	}

	private void addMinorAgentDataTableHadlers() {
		minorAgentTB.addValueChangeHandler(e -> {
			if(contractType >= 100 && contractType <= 400) // Indefinite
				setContractOtherData("I_LEGAL_REPRESENTATIVE_NAME", minorAgentTB.getValue());
			else if (contractType == 421) // Formation
				setContractOtherData("L_LEGAL_REPRESENTATIVE_NAME", minorAgentTB.getValue());
			else if (contractType == 420 || contractType == 520) // Practice
				setContractOtherData("P_LEGAL_REPRESENTATIVE_NAME", minorAgentTB.getValue());
			else // Temporal
				setContractOtherData("T_LEGAL_REPRESENTATIVE_NAME", minorAgentTB.getValue());
		});
		
		minorAgentNIFTB.addValueChangeHandler(e -> {
			if(contractType >= 100 && contractType <= 400) // Indefinite
				setContractOtherData("I_LEGAL_REPRESENTATIVE_NIF", minorAgentNIFTB.getValue());
			else if (contractType == 421) // Formation
				setContractOtherData("L_LEGAL_REPRESENTATIVE_NIF", minorAgentNIFTB.getValue());
			else if (contractType == 420 || contractType == 520) // Practice
				setContractOtherData("P_LEGAL_REPRESENTATIVE_NIF", minorAgentNIFTB.getValue());
			else // Temporal
				setContractOtherData("T_LEGAL_REPRESENTATIVE_NIF", minorAgentNIFTB.getValue());
		});
		
		minorAgentQualityOfTB.addValueChangeHandler(e -> {
			if(contractType >= 100 && contractType <= 400) // Indefinite
				setContractOtherData("I_LEGAL_REPRESENTATIVE_CHARGE", minorAgentQualityOfTB.getValue());
			else if (contractType == 421) // Formation
				setContractOtherData("L_LEGAL_REPRESENTATIVE_CHARGE", minorAgentQualityOfTB.getValue());
			else if (contractType == 420 || contractType == 520) // Practice
				setContractOtherData("P_LEGAL_REPRESENTATIVE_CHARGE", minorAgentQualityOfTB.getValue());
			else // Temporal
				setContractOtherData("T_LEGAL_REPRESENTATIVE_CHARGE", minorAgentQualityOfTB.getValue());
		});
	}

	private void initializeIndefiniteCards() {
		indefiniteTable = new AonCustomCard("Otros datos del contrato");
		indefiniteTable.addStyleName(AON.CSS.aonContractMediumCard());
		indefiniteTable.add(createIndefiniteDataTable());
		addIndefiniteDataTableHadlers();
		
		add( indefiniteTable );
	}
	
	private Widget createIndefiniteDataTable() {
		FlowPanel table = createFlexColumnPanel();
		
		table.add(createRow(doingFunctionsTB, distanceB));
		table.add(createRow(distanceAddressTB, activityStimationDurationFDTB));
		table.add(createRow(journeyHoursFDTB, journeyPeriodFDTB));
		table.add(createRow(timeDistributionFDTB, partialTimeB));
		table.add(createRow(journeyHoursTCTB, startJourneyTCTB));
		table.add(createRow(endJourneyTCTB, journeyHoursTPTB));
		table.add(createRow(agreementJourneyHoursTB, complementaryHoursB));
		table.add(createRow(trialPeriodTB, salaryAmountTB));
		table.add(createRow(salaryPeriodTB, salaryConceptTB));
		table.add(createRow(holidaysTB, sepeOfficeTB));
		table.add(createRow(accreditedDisabilityTB, withoutDisabilitySevereLB));
		table.add(createRow(disabilitySevereLB, subsidyTB));
		table.add(createRow(fourthLawB, unemploymentLB));
		table.add(createRow(unemploymentOldLB, benefitsPerceptorB));
		table.add(createRow(firstEmployeeB, employeeLB));
		table.add(createRow(agreementLineOneTB, agreementLineTwoTB));
		table.add(createRow(hoursTB, contactHoursB));
		table.add(createRow(remunerationFormLB, overnightAgreementB));
		table.add(createRow(overnightRegimeTB, quoteReductionTCB));
		table.add(createRow(sepeOfficeCOTB, quoteReductionFDB));
		
		minorAgentTB.setValue("");
		minorAgentNIFTB.setValue("");
		minorAgentQualityOfTB.setValue("");
		doingFunctionsTB.setValue("");
		distanceB.setValue(false);
		distanceAddressTB.setValue("");
		discontinuousWorkTB.setValue("");
		intermittentCyclicalActivityTB.setValue("");
		durationFDTB.setValue("");
		activityStimationDurationFDTB.setValue("");
		journeyHoursFDTB.setValue("");
		journeyPeriodFDTB.setValue("");
		timeDistributionFDTB.setValue("");
		partialTimeB.setValue(false);
		journeyHoursTCTB.setValue("");
		startJourneyTCTB.setValue("");
		endJourneyTCTB.setValue("");
		journeyHoursTPTB.setValue("");
		agreementJourneyHoursTB.setValue("");
		complementaryHoursB.setValue(false);
		trialPeriodTB.setValue("");
		salaryAmountTB.setValue("");
		salaryPeriodTB.setValue("");
		salaryConceptTB.setValue("");
		holidaysTB.setValue("");
		sepeOfficeTB.setValue("");
		accreditedDisabilityTB.setValue("");
		withoutDisabilitySevereLB.clearItems();
		disabilitySevereLB.clearItems();
		subsidyTB.setValue("");
		fourthLawB.setValue(false);
		unemploymentLB.clearItems();
		unemploymentOldLB.clearItems();
		benefitsPerceptorB.setValue(false);
		firstEmployeeB.setValue(false);
		employeeLB.clearItems();
		agreementLineOneTB.setValue("");
		agreementLineTwoTB.setValue("");
		contactHoursB.setValue(false);
		hoursTB.setValue("");
		remunerationFormLB.clearItems();
		overnightAgreementB.setValue(false);
		overnightRegimeTB.setValue("");
		quoteReductionTCB.setValue(false);
		quoteReductionFDB.setValue(false);
		sepeOfficeCOTB.setValue("");
		
		withoutDisabilitySevereLB.addItem("-", "");
		withoutDisabilitySevereLB.addItem("Hombres menores de 45 a\u00F1os", "OPT2_DISABILITY_NO_SEVERE_MAN_LT_45");
		withoutDisabilitySevereLB.addItem("Hombres mayores de 45 a\u00F1os", "OPT2_DISABILITY_NO_SEVERE_MAN_GT_45");
		withoutDisabilitySevereLB.addItem("Mujeres menores de 45 a\u00F1os", "OPT2_DISABILITY_NO_SEVERE_WOMAN_LT_45");
		withoutDisabilitySevereLB.addItem("Mujeres mayores de 45 a\u00F1os", "OPT2_DISABILITY_NO_SEVERE_WOMAN_GT_45");
		
		disabilitySevereLB.addItem("-", "");
		disabilitySevereLB.addItem("Hombres menores de 45 a\u00F1os", "OPT2_DISABILITY_SEVERE_MAN_LT_45");
		disabilitySevereLB.addItem("Hombres mayores de 45 a\u00F1os", "OPT2_DISABILITY_SEVERE_MAN_GT_45");
		disabilitySevereLB.addItem("Mujeres menores de 45 a\u00F1os", "OPT2_DISABILITY_SEVERE_WOMAN_LT_45");
		disabilitySevereLB.addItem("Mujeres mayores de 45 a\u00F1os", "OPT2_DISABILITY_SEVERE_WOMAN_GT_45");
		
		unemploymentLB.addItem("-", "");
		unemploymentLB.addItem("J\u00F3venes", "OPT5_UNEMPLOYED_BT_16_30_JUNIOR");
		unemploymentLB.addItem("Mujeres en ocupaciones menos representadas", "OPT5_UNEMPLOYED_BT_16_30_FEMALE");
		
		unemploymentOldLB.addItem("-", "");
		unemploymentOldLB.addItem("Mayores de 45 a\u00F1os", "OPT5_UNEMPLOYED_GT_45_MALE");
		unemploymentOldLB.addItem("Mujeres en ocupaciones menos representadas", "OPT5_UNEMPLOYED_GT_45_FEMALE");
		
		employeeLB.addItem("-", "");
		employeeLB.addItem("Menor de 30 a\u00F1os", "OPT6_LT_30_EMPLOYEE");
		employeeLB.addItem("Menor de 35 a\u00F1os con discapacidad mayor o igual al 33%", "OPT6_LT_35_EMPLOYEE_AND_HANDICAP_GTE_33");
		
		remunerationFormLB.addItem("-", "");
		remunerationFormLB.addItem("Comp. con periodo equiv. de descanso", "OPT15_SALARY_OPT1");
		remunerationFormLB.addItem("Retrib. con salario no inferior a horas extras", "OPT15_SALARY_OPT2");
		remunerationFormLB.addItem("Cualquiera de las anteriores", "OPT15_SALARY_OPT3");
		
		doingFunctionsTB.setValue(getContractOtherData("I_FUNCTIONS"));
		distanceB.setValue(getContractOtherDataCB("I_EMPLOYEE_CONTRACT_DISTANCE"));
		distanceAddressTB.setValue(getContractOtherData("I_EMPLOYEE_CONTRACT_DIST_ADDR"));
		discontinuousWorkTB.setValue(getContractOtherData("I_DISC_WORK_DESCRIPTION"));
		intermittentCyclicalActivityTB.setValue(getContractOtherData("I_DISC_WORK_ACTIVITY"));
		durationFDTB.setValue(getContractOtherData("I_DISC_WORK_DURATION"));
		activityStimationDurationFDTB.setValue(getContractOtherData("I_DISC_WORK_ESTIMATED_DURATION"));
		journeyHoursFDTB.setValue(getContractOtherData("I_DISC_WORK_ESTIM_JOURNAL_HOURS"));
		journeyPeriodFDTB.setValue(getContractOtherData("I_DISC_WORK_ESTIM_JOURNAL_PERIOD"));
		timeDistributionFDTB.setValue(getContractOtherData("I_DISC_WORK_ESTIM_SCHEDULE"));
		partialTimeB.setValue(AonStringUtils.containsIgnoreCase(getContractOtherData("I_DISC_AGREEMENT_COLLECTIVE"), "YES"));
		journeyHoursTCTB.setValue(getContractOtherData("I_FULL_TIME_WEEK_HOURS"));
		startJourneyTCTB.setValue(getContractOtherData("I_FULL_TIME_START_TIME"));
		endJourneyTCTB.setValue(getContractOtherData("I_FULL_TIME_END_TIME"));
		journeyHoursTPTB.setValue(getContractOtherData("I_PARTIALLY_TIME_HOURS"));
		agreementJourneyHoursTB.setValue(getContractOtherData("I_DEFAULT_JOURNAL_HOURS"));
		complementaryHoursB.setValue(AonStringUtils.containsIgnoreCase(getContractOtherData("I_COMPLEMENTARY_HOURS"), "YES"));
		trialPeriodTB.setValue(getContractOtherData("I_TRIAL_DURATION"));
		salaryAmountTB.setValue(getContractOtherData("I_SALARY_AMOUNT"));
		salaryPeriodTB.setValue(getContractOtherData("I_SALARY_PERIOD"));
		salaryConceptTB.setValue(getContractOtherData("I_SALARY_CONCEPT"));
		holidaysTB.setValue(getContractOtherData("I_HOLIDAYS"));
		sepeOfficeTB.setValue(getContractOtherData("I_SEPE_MUNICIPALITY"));
		accreditedDisabilityTB.setValue(getContractOtherData("I_OPT2_SEPE_MUNICIPALITY"));
		withoutDisabilitySevereLB.setValue(getContractOtherData("I_OPT2_DISABILITY_NO_SEVERE"));
		disabilitySevereLB.setValue(getContractOtherData("I_OPT2_DISABILITY_SEVERE"));
		subsidyTB.setValue(getContractOtherData("I_OPT2_REDUCTION"));
		fourthLawB.setValue(AonStringUtils.containsIgnoreCase(getContractOtherData("I_OPT5_BONUS_ART4_RDL3_2012"), "YES"));
		unemploymentLB.setValue(getContractOtherData("I_OPT5_UNEMPLOYED_BT_16_30"));
		unemploymentOldLB.setValue(getContractOtherData("I_OPT5_UNEMPLOYED_GT_45"));
		benefitsPerceptorB.setValue(getContractOtherDataCB("I_OPT5_UNEMPL_3_MONTH_BENEFIT"));
		firstEmployeeB.setValue(getContractOtherDataCB("I_OPT5_FIRST_EMPLOYEE_AND_LT_30"));
		employeeLB.setValue(getContractOtherData("I_OPT6_AGE"));
		agreementLineOneTB.setValue(getContractOtherData("I_OPT6_AGREEMENT_COLLECTIVE1"));
		agreementLineTwoTB.setValue(getContractOtherData("I_OPT6_AGREEMENT_COLLECTIVE2"));
		contactHoursB.setValue(AonStringUtils.containsIgnoreCase(getContractOtherData("I_OPT15_ONSITE_HOURS"), "YES"));
		hoursTB.setValue(getContractOtherData("I_OPT15_ONSITE_WEEK_HOURS"));
		remunerationFormLB.setValue(getContractOtherData("I_OPT15_SALARY"));
		overnightAgreementB.setValue(AonStringUtils.containsIgnoreCase(getContractOtherData("I_OPT15_OVERNIGHT"), "YES"));
		overnightRegimeTB.setValue(getContractOtherData("I_OPT15_OVERNIGHT_WEEK_DAYS"));
		quoteReductionTCB.setValue(AonStringUtils.containsIgnoreCase(getContractOtherData("I_OPT17_FULL_TIME_QUOTE_BONUS"), "YES"));
		quoteReductionFDB.setValue(AonStringUtils.containsIgnoreCase(getContractOtherData("I_OPT17_DISCONT_TIME_QUOTE_BONUS"), "YES"));
		sepeOfficeCOTB.setValue(getContractOtherData("I_OPT17_SRC_CONTRACT_SEPE_MUNIC"));
		
		return table;
	}

	private void addIndefiniteDataTableHadlers() {
		doingFunctionsTB.addValueChangeHandler(e -> setContractOtherData("I_FUNCTIONS", doingFunctionsTB.getValue()));
		distanceB.addValueChangeHandler(e -> setContractOtherData("I_EMPLOYEE_CONTRACT_DISTANCE", e.getValue() ? "true" : ""));
		distanceAddressTB.addValueChangeHandler(e -> setContractOtherData("I_EMPLOYEE_CONTRACT_DIST_ADDR", distanceAddressTB.getValue()));
		discontinuousWorkTB.addValueChangeHandler(e -> setContractOtherData("I_DISC_WORK_DESCRIPTION", discontinuousWorkTB.getValue()));
		intermittentCyclicalActivityTB.addValueChangeHandler(e -> setContractOtherData("I_DISC_WORK_ACTIVITY", intermittentCyclicalActivityTB.getValue()));
		durationFDTB.addValueChangeHandler(e -> setContractOtherData("I_DISC_WORK_DURATION", durationFDTB.getValue()));
		activityStimationDurationFDTB.addValueChangeHandler(e -> setContractOtherData("I_DISC_WORK_ESTIMATED_DURATION", activityStimationDurationFDTB.getValue()));
		journeyHoursFDTB.addValueChangeHandler(e -> setContractOtherData("I_DISC_WORK_ESTIM_JOURNAL_HOURS", journeyHoursFDTB.getValue()));
		journeyPeriodFDTB.addValueChangeHandler(e -> setContractOtherData("I_DISC_WORK_ESTIM_JOURNAL_PERIOD", journeyPeriodFDTB.getValue()));
		timeDistributionFDTB.addValueChangeHandler(e -> setContractOtherData("I_DISC_WORK_ESTIM_SCHEDULE", timeDistributionFDTB.getValue()));
		partialTimeB.addValueChangeHandler(e -> setContractOtherData("I_DISC_AGREEMENT_COLLECTIVE", e.getValue() ? "DISC_AGREEMENT_COLLECTIVE_YES" : "DISC_AGREEMENT_COLLECTIVE_NO"));
		journeyHoursTCTB.addValueChangeHandler(e -> setContractOtherData("I_FULL_TIME_WEEK_HOURS", journeyHoursTCTB.getValue()));
		startJourneyTCTB.addValueChangeHandler(e -> setContractOtherData("I_FULL_TIME_START_TIME", startJourneyTCTB.getValue()));
		endJourneyTCTB.addValueChangeHandler(e -> setContractOtherData("I_FULL_TIME_END_TIME", endJourneyTCTB.getValue()));
		journeyHoursTPTB.addValueChangeHandler(e -> setContractOtherData("I_PARTIALLY_TIME_HOURS", journeyHoursTPTB.getValue()));
		agreementJourneyHoursTB.addValueChangeHandler(e -> setContractOtherData("I_DEFAULT_JOURNAL_HOURS", agreementJourneyHoursTB.getValue()));
		complementaryHoursB.addValueChangeHandler(e -> setContractOtherData("I_COMPLEMENTARY_HOURS", e.getValue() ? "COMPLEMENTARY_HOURS_YES" : "COMPLEMENTARY_HOURS_NO"));
		trialPeriodTB.addValueChangeHandler(e -> setContractOtherData("I_TRIAL_DURATION", trialPeriodTB.getValue()));
		salaryAmountTB.addValueChangeHandler(e -> setContractOtherData("I_SALARY_AMOUNT", salaryAmountTB.getValue()));
		salaryPeriodTB.addValueChangeHandler(e -> setContractOtherData("I_SALARY_PERIOD", salaryPeriodTB.getValue()));
		salaryConceptTB.addValueChangeHandler(e -> setContractOtherData("I_SALARY_CONCEPT", salaryConceptTB.getValue()));
		holidaysTB.addValueChangeHandler(e -> setContractOtherData("I_HOLIDAYS", holidaysTB.getValue()));
		sepeOfficeTB.addValueChangeHandler(e -> setContractOtherData("I_SEPE_MUNICIPALITY", sepeOfficeTB.getValue()));
		accreditedDisabilityTB.addValueChangeHandler(e -> setContractOtherData("I_OPT2_SEPE_MUNICIPALITY", accreditedDisabilityTB.getValue()));
		withoutDisabilitySevereLB.addChangeHandler(e -> setContractOtherData("I_OPT2_DISABILITY_NO_SEVERE", withoutDisabilitySevereLB.getValue()));
		disabilitySevereLB.addChangeHandler(e -> setContractOtherData("I_OPT2_DISABILITY_SEVERE", disabilitySevereLB.getValue()));
		subsidyTB.addValueChangeHandler(e -> setContractOtherData("I_OPT2_REDUCTION", subsidyTB.getValue()));
		fourthLawB.addValueChangeHandler(e -> setContractOtherData("I_OPT5_BONUS_ART4_RDL3_2012", e.getValue() ? "OPT5_BONUS_ART4_RDL3_2012_YES" : "OPT5_BONUS_ART4_RDL3_2012_NO"));
		unemploymentLB.addChangeHandler(e -> setContractOtherData("I_OPT5_UNEMPLOYED_BT_16_30", unemploymentLB.getValue()));
		unemploymentOldLB.addChangeHandler(e -> setContractOtherData("I_OPT5_UNEMPLOYED_GT_45", unemploymentOldLB.getValue()));
		benefitsPerceptorB.addValueChangeHandler(e -> setContractOtherData("I_OPT5_UNEMPL_3_MONTH_BENEFIT", e.getValue() ? "true" : ""));
		firstEmployeeB.addValueChangeHandler(e -> setContractOtherData("I_OPT5_FIRST_EMPLOYEE_AND_LT_30", e.getValue() ? "true" : ""));
		employeeLB.addChangeHandler(e -> setContractOtherData("I_OPT6_AGE", employeeLB.getValue()));
		agreementLineOneTB.addValueChangeHandler(e -> setContractOtherData("I_OPT6_AGREEMENT_COLLECTIVE1", agreementLineOneTB.getValue()));
		agreementLineTwoTB.addValueChangeHandler(e -> setContractOtherData("I_OPT6_AGREEMENT_COLLECTIVE2", agreementLineTwoTB.getValue()));
		contactHoursB.addValueChangeHandler(e -> setContractOtherData("I_OPT15_ONSITE_HOURS", e.getValue() ? "OPT15_ONSITE_HOURS_YES" : "OPT15_ONSITE_HOURS_NO"));
		hoursTB.addValueChangeHandler(e -> setContractOtherData("I_OPT15_ONSITE_WEEK_HOURS", hoursTB.getValue()));
		remunerationFormLB.addChangeHandler(e -> setContractOtherData("I_OPT15_SALARY", remunerationFormLB.getValue()));
		overnightAgreementB.addValueChangeHandler(e -> setContractOtherData("I_OPT15_OVERNIGHT", e.getValue() ? "OPT15_OVERNIGHT_YES" : "OPT15_OVERNIGHT_NO"));
		overnightRegimeTB.addValueChangeHandler(e -> setContractOtherData("I_OPT15_OVERNIGHT_WEEK_DAYS", overnightRegimeTB.getValue()));
		quoteReductionTCB.addValueChangeHandler(e -> setContractOtherData("I_OPT17_FULL_TIME_QUOTE_BONUS", e.getValue() ? "OPT17_FULL_TIME_QUOTE_BONUS_YES" : "OPT17_FULL_TIME_QUOTE_BONUS_NO"));
		quoteReductionFDB.addValueChangeHandler(e -> setContractOtherData("I_OPT17_DISCONT_TIME_QUOTE_BONUS", e.getValue() ? "OPT17_DISCONT_TIME_QUOTE_BONUS_YES" : "OPT17_DISCONT_TIME_QUOTE_BONUS_NO"));
		sepeOfficeCOTB.addValueChangeHandler(e -> setContractOtherData("I_OPT17_SRC_CONTRACT_SEPE_MUNIC", sepeOfficeCOTB.getValue()));
	}
	
	private void initializeFormationCards() {
		formationTable = new AonCustomCard("Otros datos del contrato");
		formationTable.addStyleName(AON.CSS.aonContractMediumCard());
		formationTable.add(createFormationDataTable());
		addFormationDataTableHadlers();
		
		add( formationTable );
	}

	private Widget createFormationDataTable() {
		FlowPanel table = createFlexColumnPanel();
		
		table.add(createRow(employeeFormLB, ssReductionFormB));
		table.add(createRow(workplaceFormTB, tutorFormTB));
		table.add(createRow(efectiveWorkHoursFormTB, activityHoursFormTB));
		table.add(createRow(trialPeriodFormTB, agreementTrialFormB));
		table.add(createRow(salaryAmountFormTB, salaryPeriodFormTB));
		table.add(createRow(holidaysFormTB, null));
		table.add(createRow(degreeExistFormB, degreeExist2FormB));
		
		ssReductionFormB.setValue(false);
		employeeFormLB.clearItems();
		workplaceFormTB.setValue("");
		tutorFormTB.setValue("");
		efectiveWorkHoursFormTB.setValue("");
		activityHoursFormTB.setValue("");
		trialPeriodFormTB.setValue("");
		agreementTrialFormB.setValue(false);
		salaryAmountFormTB.setValue("");
		salaryPeriodFormTB.setValue("");
		holidaysFormTB.setValue("");
		degreeExistFormB.setValue(false);
		degreeExist2FormB.setValue(false);
		
		employeeFormLB.addItem("-", "");
		employeeFormLB.addItem("Mayor de 16 y menor de 30 a\u00F1os", "EMPLOYEE_OPT1");
		employeeFormLB.addItem("Trabajador/a con discapacidad (sin l\u00EDmite de edad)", "EMPLOYEE_OPT2");
		employeeFormLB.addItem("Participantes en proyecto al amparo de lo previsto en el art. 25 1 d (ley 56/2003)", "EMPLOYEE_OPT3");
		employeeFormLB.addItem("Trabajador/a en situaci\u00F3n de exclusi\u00F3n social (sin l\u00EDmite de edad)", "EMPLOYEE_OPT4");
		
		ssReductionFormB.setValue(AonStringUtils.equalsIgnoreCase(getContractOtherData("L_QUOTE_BONUS"), "YES"));
		employeeFormLB.setValue(getContractOtherData("L_EMPLOYEE_OPT"));
		workplaceFormTB.setValue(getContractOtherData("L_CONTRACT_WORKPLACE_ADDRESS"));
		tutorFormTB.setValue(getContractOtherData("L_FORMATION_TEACHER"));
		efectiveWorkHoursFormTB.setValue(getContractOtherData("L_HORARIO_LABORAL"));
		activityHoursFormTB.setValue(getContractOtherData("L_HORARIO_LECTIVO"));
		trialPeriodFormTB.setValue(getContractOtherData("L_TRIAL_DURATION"));
		agreementTrialFormB.setValue(getContractOtherDataCB("L_TRIAL_DURATION_INCREASE"));
		salaryAmountFormTB.setValue(getContractOtherData("L_SALARY_AMOUNT"));
		salaryPeriodFormTB.setValue(getContractOtherData("L_SALARY_PERIOD"));
		holidaysFormTB.setValue(getContractOtherData("L_HOLIDAYS"));
		degreeExistFormB.setValue(getContractOtherDataCB("L_ANNEX_I_CHECK"));
		degreeExist2FormB.setValue(getContractOtherDataCB("L_ANNEX_II_CHECK"));
		
		return table;
	}

	private void addFormationDataTableHadlers() {
		ssReductionFormB.addValueChangeHandler(e -> setContractOtherData("L_QUOTE_BONUS", e.getValue() ? "QUOTE_BONUS_YES" : "QUOTE_BONUS_NO"));
		employeeFormLB.addChangeHandler(e -> setContractOtherData("L_EMPLOYEE_OPT", employeeFormLB.getValue()));
		workplaceFormTB.addValueChangeHandler(e -> setContractOtherData("L_CONTRACT_WORKPLACE_ADDRESS", workplaceFormTB.getValue()));
		tutorFormTB.addValueChangeHandler(e -> setContractOtherData("L_FORMATION_TEACHER", tutorFormTB.getValue()));
		efectiveWorkHoursFormTB.addValueChangeHandler(e -> setContractOtherData("L_HORARIO_LABORAL", efectiveWorkHoursFormTB.getValue()));
		activityHoursFormTB.addValueChangeHandler(e -> setContractOtherData("L_HORARIO_LECTIVO", activityHoursFormTB.getValue()));
		trialPeriodFormTB.addValueChangeHandler(e -> setContractOtherData("L_TRIAL_DURATION", trialPeriodFormTB.getValue()));
		agreementTrialFormB.addValueChangeHandler(e -> setContractOtherData("L_TRIAL_DURATION_INCREASE", e.getValue() ? "true" : ""));
		salaryAmountFormTB.addValueChangeHandler(e -> setContractOtherData("L_SALARY_AMOUNT", salaryAmountFormTB.getValue()));
		salaryPeriodFormTB.addValueChangeHandler(e -> setContractOtherData("L_SALARY_PERIOD", salaryPeriodFormTB.getValue()));
		holidaysFormTB.addValueChangeHandler(e -> setContractOtherData("L_HOLIDAYS", holidaysFormTB.getValue()));
		degreeExistFormB.addValueChangeHandler(e -> setContractOtherData("L_ANNEX_I_CHECK", e.getValue() ? "true" : ""));
		degreeExist2FormB.addValueChangeHandler(e -> setContractOtherData("L_ANNEX_II_CHECK", e.getValue() ? "true" : ""));
	}

	private void initializePracticeCards() {
		practiceTable = new AonCustomCard("Otros datos del contrato");
		practiceTable.addStyleName(AON.CSS.aonContractMediumCard());
		practiceTable.add(createPracticeDataTable());
		addPracticeDataTableHadlers();
		
		add( practiceTable );
	}

	private Widget createPracticeDataTable() {
		FlowPanel table = createFlexColumnPanel();
		
		table.add(createRow(obtainingDatePracTB, disabilityCertPracTB));
		table.add(createRow(disabilityCertMorePracTB, firstContractPracLB));
		table.add(createRow(journeyHoursPracTB, startJourneyPracTB));
		table.add(createRow(endJourneyPracTB, distributionJourneyPracTB));
		table.add(createRow(trialPeriodPracTB, salaryAmountPracTB));
		table.add(createRow(salaryPeriodPracTB, salaryConceptPracTB));
		table.add(createRow(holidaysPracTB, sepeComunicationPracTB));
		table.add(createRow(endSepeComunicationPracTB, unemploymentSubsidyPracLB));
		table.add(createRow(adaptationPeriodPracTB, adaptationConditionsPracTB));
		table.add(createRow(adaptationWorkPracTB, personalSocialAdjustPracTB));
		table.add(createRow(personalSocialAdjustMorePracTB, motivationPracLB));
		table.add(createRow(employerPracLB, null));
		
		profesionalCertPracTB.setValue("");
		obtainingDatePracTB.setValue("");
		disabilityCertPracTB.setValue("");
		disabilityCertMorePracTB.setValue("");
		firstContractPracLB.clear();
		journeyHoursPracTB.setValue("");
		startJourneyPracTB.setValue("");
		endJourneyPracTB.setValue("");
		distributionJourneyPracTB.setValue("");
		trialPeriodPracTB.setValue("");
		salaryAmountPracTB.setValue("");
		salaryPeriodPracTB.setValue("");
		salaryConceptPracTB.setValue("");
		holidaysPracTB.setValue("");
		sepeComunicationPracTB.setValue("");
		endSepeComunicationPracTB.setValue("");
		unemploymentSubsidyPracLB.clearItems();
		adaptationPeriodPracTB.setValue("");
		adaptationConditionsPracTB.setValue("");
		adaptationWorkPracTB.setValue("");
		personalSocialAdjustPracTB.setValue("");
		personalSocialAdjustMorePracTB.setValue("");
		motivationPracLB.clearItems();
		employerPracLB.clearItems();
		
		firstContractPracLB.addItem("-", "");
		firstContractPracLB.addItem("Menor de 30 a\u00F1os", "FIRST_CONTRACT_LT_30");
		firstContractPracLB.addItem("Menor de 35 y con grado de disc. igual o mayor a 33%", "FIRST_CONTRACT_LT_35");
		firstContractPracLB.addItem("Menor de 30 y realiza pr\u00E1cticas no laborables", "FIRST_CONTRACT_LT_30_RD1543_2011");
		
		unemploymentSubsidyPracLB.addItem("-", "");
		unemploymentSubsidyPracLB.addItem("Recogidos en el art. 215", "OPT3_UNEMPLOYMENT_ART_215");
		unemploymentSubsidyPracLB.addItem("Eventuales incl. en el R.E.A.", "OPT3_UNEMPLOYMENT_AGRARIAN_REGIME");
		
		motivationPracLB.addItem("-", "");
		motivationPracLB.addItem("Inter\u00E9s social", "OPT5_MOTIVATION_SOCIAL_INTEREST");
		motivationPracLB.addItem("Fomento empleo agrario", "OPT5_MOTIVATION_AGRARIAN_PROMOTION");
		
		employerPracLB.addItem("-", "");
		employerPracLB.addItem("Corporaci\u00F3n local", "OPT5_EMPLOYER_LOCAL_CORPORATION");
		employerPracLB.addItem("\u00D3rganos de la Admin. General del Estado", "OPT5_EMPLOYER_GENERAL_ADMINISTRATION");
		employerPracLB.addItem("Comunidad aut\u00F3noma", "OPT5_EMPLOYER_AUTON_COMMUNITY");
		employerPracLB.addItem("Entidad sin \u00E1nimo de lucro", "OPT5_EMPLOYER_NONPROFIT_ENTITY");
		employerPracLB.addItem("Universidad", "OPT5_EMPLOYER_UNIVERSITY");
		
		profesionalCertPracTB.setValue(getContractOtherData("P_PROFESSIONAL_CERT"));
		obtainingDatePracTB.setValue(getContractOtherData("P_PROFESSIONAL_CERT_OBTAIN_DATE"));
		disabilityCertPracTB.setValue(getContractOtherData("P_DISABILITY_ISSUE_ENTITY"));
		disabilityCertMorePracTB.setValue(getContractOtherData("P_DISABILITY_ISSUE_ENTITY_MORE"));
		firstContractPracLB.setValue( getContractOtherData("P_FIRST_CONTRACT"));
		journeyHoursPracTB.setValue(getContractOtherData("P_FULL_TIME_WEEK_HOURS"));
		startJourneyPracTB.setValue(getContractOtherData("P_FULL_TIME_START_TIME"));
		endJourneyPracTB.setValue(getContractOtherData("P_FULL_TIME_END_TIME"));
		distributionJourneyPracTB.setValue(getContractOtherData("P_JOB_TIME_DISTRIBUTION2"));
		trialPeriodPracTB.setValue(getContractOtherData("P_TRIAL_DURATION"));
		salaryAmountPracTB.setValue(getContractOtherData("P_SALARY_AMOUNT"));
		salaryPeriodPracTB.setValue(getContractOtherData("P_SALARY_PERIOD"));
		salaryConceptPracTB.setValue(getContractOtherData("P_SALARY_CONCEPT"));
		holidaysPracTB.setValue(getContractOtherData("P_HOLIDAYS"));
		sepeComunicationPracTB.setValue(getContractOtherData("P_SEPE_START_COMMUNICATION"));
		endSepeComunicationPracTB.setValue(getContractOtherData("P_SEPE_END_COMMUNICATION"));
		unemploymentSubsidyPracLB.setValue(getContractOtherData("P_OPT3_UNEMPLOYMENT"));
		adaptationPeriodPracTB.setValue(getContractOtherData("P_OPT4_TRIAL_DURATION"));
		adaptationConditionsPracTB.setValue(getContractOtherData("P_OPT4_TRIAL_DURATION_CONDITIONS"));
		adaptationWorkPracTB.setValue(getContractOtherData("P_OPT4_WORK_PLACE_ADAPTATIONS"));
		personalSocialAdjustPracTB.setValue(getContractOtherData("P_OPT4_STAFF_ADJUSTMENT"));
		personalSocialAdjustMorePracTB.setValue(getContractOtherData("P_OPT4_STAFF_ADJUSTMENT_MORE"));
		motivationPracLB.setValue(getContractOtherData("P_OPT5_MOTIVATION"));
		employerPracLB.setValue(getContractOtherData("P_OPT5_EMPLOYER"));
		
		return table;
	}

	private void addPracticeDataTableHadlers() {
		profesionalCertPracTB.addValueChangeHandler(e -> setContractOtherData("P_PROFESSIONAL_CERT", profesionalCertPracTB.getValue()));
		obtainingDatePracTB.addValueChangeHandler(e -> setContractOtherData("P_PROFESSIONAL_CERT_OBTAIN_DATE", obtainingDatePracTB.getValue()));
		disabilityCertPracTB.addValueChangeHandler(e -> setContractOtherData("P_DISABILITY_ISSUE_ENTITY", disabilityCertPracTB.getValue()));
		disabilityCertMorePracTB.addValueChangeHandler(e -> setContractOtherData("P_DISABILITY_ISSUE_ENTITY_MORE", disabilityCertMorePracTB.getValue()));
		firstContractPracLB.addChangeHandler(e -> setContractOtherData("P_FIRST_CONTRACT", firstContractPracLB.getValue()));
		journeyHoursPracTB.addValueChangeHandler(e -> setContractOtherData("P_FULL_TIME_WEEK_HOURS", journeyHoursPracTB.getValue()));
		startJourneyPracTB.addValueChangeHandler(e -> setContractOtherData("P_FULL_TIME_START_TIME", startJourneyPracTB.getValue()));
		endJourneyPracTB.addValueChangeHandler(e -> setContractOtherData("P_FULL_TIME_END_TIME", endJourneyPracTB.getValue()));
		distributionJourneyPracTB.addValueChangeHandler(e -> setContractOtherData("P_JOB_TIME_DISTRIBUTION2", distributionJourneyPracTB.getValue()));
		trialPeriodPracTB.addValueChangeHandler(e -> setContractOtherData("P_TRIAL_DURATION", trialPeriodPracTB.getValue()));
		salaryAmountPracTB.addValueChangeHandler(e -> setContractOtherData("P_SALARY_AMOUNT",  salaryAmountPracTB.getValue()));
		salaryPeriodPracTB.addValueChangeHandler(e -> setContractOtherData("P_SALARY_PERIOD", salaryPeriodPracTB.getValue()));
		salaryConceptPracTB.addValueChangeHandler(e -> setContractOtherData("P_SALARY_CONCEPT", salaryConceptPracTB.getValue()));
		holidaysPracTB.addValueChangeHandler(e -> setContractOtherData("P_HOLIDAYS",  holidaysPracTB.getValue()));
		sepeComunicationPracTB.addValueChangeHandler(e -> setContractOtherData("P_SEPE_START_COMMUNICATION", sepeComunicationPracTB.getValue()));
		endSepeComunicationPracTB.addValueChangeHandler(e -> setContractOtherData("P_SEPE_END_COMMUNICATION", endSepeComunicationPracTB.getValue()));
		unemploymentSubsidyPracLB.addChangeHandler(e -> setContractOtherData("P_OPT3_UNEMPLOYMENT", unemploymentSubsidyPracLB.getValue()));
		adaptationPeriodPracTB.addValueChangeHandler(e -> setContractOtherData("P_OPT4_TRIAL_DURATION", adaptationPeriodPracTB.getValue()));
		adaptationConditionsPracTB.addValueChangeHandler(e -> setContractOtherData("P_OPT4_TRIAL_DURATION_CONDITIONS",  adaptationConditionsPracTB.getValue()));
		adaptationWorkPracTB.addValueChangeHandler(e -> setContractOtherData("P_OPT4_WORK_PLACE_ADAPTATIONS", adaptationWorkPracTB.getValue()));
		personalSocialAdjustPracTB.addValueChangeHandler(e -> setContractOtherData("P_OPT4_STAFF_ADJUSTMENT", personalSocialAdjustPracTB.getValue()));
		personalSocialAdjustMorePracTB.addValueChangeHandler(e -> setContractOtherData("P_OPT4_STAFF_ADJUSTMENT_MORE", personalSocialAdjustPracTB.getValue()));
		motivationPracLB.addChangeHandler(e -> setContractOtherData("P_OPT5_MOTIVATION", motivationPracLB.getValue()));
		employerPracLB.addChangeHandler(e -> setContractOtherData("P_OPT5_EMPLOYER", employerPracLB.getValue()));		
	}

	private void initializeTemporalCards() {
		temporalTable = new AonCustomCard("Otros datos del contrato");
		temporalTable.addStyleName(AON.CSS.aonContractMediumCard());
		temporalTable.add(createTemporalDataTable());
		addTemporalDataTableHadlers();
		
		add( temporalTable );
	}
	
	private Widget createTemporalDataTable() {
		FlowPanel table = createFlexColumnPanel();
		
		requirementsTempLB.getElement().getStyle().setProperty("max-width", "50%");
		formationWillTempLB.getElement().getStyle().setProperty("max-width", "50%");
		
		table.add(createRow(doingFunctionsTempTB, distanceTempB));
		table.add(createRow(distanceAddressTempTB, journeyHoursTCTempTB));
		table.add(createRow(startJourneyTCTempTB, endJourneyTCTempTB));
		table.add(createRow(lowJourneyTempTB, timeDistributionTempTB));
		table.add(createRow(complementaryHoursTempB, endContractTempTB));
		table.add(createRow(trialPeriodTempTB, permitedHighDurationTempB));
		table.add(createRow(salaryAmountTempTB, salaryPeriodTempTB));
		table.add(createRow(salaryConceptTempTB, holidaysTempTB));
		table.add(createRow(sepeOfficeTempTB, workTempTB));
		table.add(createRow(workMoreTempTB, taskTempTB));
		table.add(createRow(taskMoreTempTB, sustituteEmployeeTempTB));
		table.add(createRow(requirementsTempLB, formationTempLB));
		table.add(createRow(formationWillTempLB, officeSPEmployeeTempTB));
		table.add(createRow(lenguageFormationTempTB, hoursDealTempB));
		table.add(createRow(presentHoursTempTB, distributionHoursTempTB));
		table.add(createRow(timeCompensationTempLB, dealOvernightB));
		table.add(createRow(overnightRegimeTempTB, officialOrganismTempTB));
		table.add(createRow(withoutSevereDisTempLB, severeDisTempLB));
		table.add(createRow(adaptationPeriodTempTB, adaptationConditionsTempTB));
		table.add(createRow(adaptationWorkTempLB, socialPersonalAdjustTempTB));
		table.add(createRow(socialPersonalAdjustMoreTempTB, colectiveAgreementTempTB));
		
		doingFunctionsTempTB.setValue("");
		distanceTempB.setValue(false);
		distanceAddressTempTB.setValue("");
		journeyHoursTCTempTB.setValue("");
		startJourneyTCTempTB.setValue("");
		endJourneyTCTempTB.setValue("");
		lowJourneyTempTB.setValue("");
		timeDistributionTempTB.setValue("");
		complementaryHoursTempB.setValue(false);
		endContractTempTB.setValue("");
		trialPeriodTempTB.setValue("");
		permitedHighDurationTempB.setValue(false);
		salaryAmountTempTB.setValue("");
		salaryPeriodTempTB.setValue("");
		salaryConceptTempTB.setValue("");
		holidaysTempTB.setValue("");
		sepeOfficeTempTB.setValue("");
		workTempTB.setValue("");
		workMoreTempTB.setValue("");
		taskTempTB.setValue("");
		taskMoreTempTB.setValue("");
		sustituteEmployeeTempTB.setValue("");
		requirementsTempLB.clearItems();
		formationTempLB.clearItems();
		formationWillTempLB.clearItems();
		officeSPEmployeeTempTB.setValue("");
		lenguageFormationTempTB.setValue("");
		hoursDealTempB.setValue(false);
		presentHoursTempTB.setValue("");
		distributionHoursTempTB.setValue("");
		timeCompensationTempLB.clearItems();
		dealOvernightB.setValue(false);
		overnightRegimeTempTB.setValue("");
		officialOrganismTempTB.setValue("");
		withoutSevereDisTempLB.clearItems();
		severeDisTempLB.clearItems();
		adaptationPeriodTempTB.setValue("");
		adaptationConditionsTempTB.setValue("");
		adaptationWorkTempLB.clearItems();
		socialPersonalAdjustTempTB.setValue("");
		socialPersonalAdjustMoreTempTB.setValue("");
		colectiveAgreementTempTB.setValue("");
		
		requirementsTempLB.addItem("-", "");
		requirementsTempLB.addItem("No tener experiencia o que esta sea inferior a 3 meses", "OPT10_REQUIREMENTS_OPT1");
		requirementsTempLB.addItem("Proceder de otro sector de actividad en los t\u00E9rminos que se determine reglamentariamente", "OPT10_REQUIREMENTS_OPT2");
		requirementsTempLB.addItem("Ser desempleado inscrito ininterrumpidamente en la oficina de empleo al menos doce meses durante los los dieciocho meses anteriores a la contrataci\u00F3n", "OPT10_REQUIREMENTS_OPT3");
		requirementsTempLB.addItem("Carecer de t\u00EDtulo oficial de ense\u00F1anza obligatoria, de t\u00EDtulo de formaci\u00F3n profesional o certificado de profesionalidad", "OPT10_REQUIREMENTS_OPT4");
		
		formationTempLB.addItem("-", "");
		formationTempLB.addItem("Compatibilizar\u00E1 el empleo con la formaci\u00F3n", "OPT10_FORMATION_OPT1");
		formationTempLB.addItem("Ha cursado la formaci\u00F3n en los 6 meses previos a la celebraci\u00F3n del contrato", "OPT10_FORMATION_OPT2");
		
		formationWillTempLB.addItem("-", "");
		formationWillTempLB.addItem("Formaci\u00F3n acreditable oficialmente o promovida por los Servicios P\u00FAblicos de Empleo", "OPT10_FORMATION_TYPE_OPT1");
		formationWillTempLB.addItem("Fromaci\u00F3n en idiomas o tecnolog\u00EDas de la informaci\u00F3n y la comunicaci\u00F3n de una duraci\u00F3n m\u00EDnima de 90 horas", "OPT10_FORMATION_TYPE_OPT2");
		
		timeCompensationTempLB.addItem("-", "");
		timeCompensationTempLB.addItem("Per\u00EDodos de descanso", "OPT12_SALARY_OPT1");
		timeCompensationTempLB.addItem("Retribuci\u00F3n con salario", "OPT12_SALARY_OPT2");
		timeCompensationTempLB.addItem("Cualquiera de la anteriores", "OPT12_SALARY_OPT3");
		
		withoutSevereDisTempLB.addItem("-", "");
		withoutSevereDisTempLB.addItem("Hombres menores de 45 a\u00F1os", "OPT13_DISABILITY_MAN_LT_45");
		withoutSevereDisTempLB.addItem("Hombres mayores de 45 a\u00F1os", "OPT13_DISABILITY_MAN_GT_45");
		withoutSevereDisTempLB.addItem("Mujeres menores de 45 a\u00F1os", "OPT13_DISABILITY_WOMAN_LT_45");
		withoutSevereDisTempLB.addItem("Mujeres mayores de 45 a\u00F1os", "OPT13_DISABILITY_WOMAN_GT_45");
		
		severeDisTempLB.addItem("-", "");
		severeDisTempLB.addItem("Hombres menores de 45 a\u00F1os", "OPT13_SEVERE_DISABILITY_MAN_LT_45");
		severeDisTempLB.addItem("Hombres mayores de 45 a\u00F1os", "OPT13_SEVERE_DISABILITY_MAN_GT_45");
		severeDisTempLB.addItem("Mujeres menores de 45 a\u00F1os", "OPT13_SEVERE_DISABILITY_WOMAN_LT_45");
		severeDisTempLB.addItem("Mujeres mayores de 45 a\u00F1os", "OPT13_SEVERE_DISABILITY_WOMAN_GT_45");
		
		doingFunctionsTempTB.setValue(getContractOtherData("T_FUNCTIONS"));
		distanceTempB.setValue(getContractOtherDataCB("T_EMPLOYEE_CONTRACT_DISTANCE"));
		distanceAddressTempTB.setValue(getContractOtherData("T_EMPLOYEE_CONTRACT_DIST_ADDR"));
		journeyHoursTCTempTB.setValue(getContractOtherData("T_FULL_TIME_WEEK_HOURS"));
		startJourneyTCTempTB.setValue(getContractOtherData("T_FULL_TIME_START_TIME"));
		endJourneyTCTempTB.setValue(getContractOtherData("T_FULL_TIME_END_TIME"));
		lowJourneyTempTB.setValue(getContractOtherData("T_PARTIALLY_TIME_JOB_LOWER_THAN"));
		timeDistributionTempTB.setValue(getContractOtherData("T_PARTIALLY_TIME_JOB_DISTRIB"));
		complementaryHoursTempB.setValue(getContractOtherDataCB("T_COMPLEMENTARY_HOURS"));
		endContractTempTB.setValue(getContractOtherData("T_END_DATE_TEXT"));
		trialPeriodTempTB.setValue(getContractOtherData("T_TRIAL_DURATION"));
		permitedHighDurationTempB.setValue(getContractOtherDataCB("T_GREATER_DURATION_AGREEMENT_COL"));
		salaryAmountTempTB.setValue(getContractOtherData("T_SALARY_AMOUNT"));
		salaryPeriodTempTB.setValue(getContractOtherData("T_SALARY_PERIOD"));
		salaryConceptTempTB.setValue(getContractOtherData("T_SALARY_CONCEPT"));
		holidaysTempTB.setValue(getContractOtherData("T_HOLIDAYS"));
		sepeOfficeTempTB.setValue(getContractOtherData("T_SEPE_MUNICIPALITY"));
		workTempTB.setValue(getContractOtherData("T_OPT1_WORK_DESCRIPTION1"));
		workMoreTempTB.setValue(getContractOtherData("T_OPT1_WORK_DESCRIPTION2"));
		taskTempTB.setValue(getContractOtherData("T_OPT2_WORK_DESCRIPTION1"));
		taskMoreTempTB.setValue(getContractOtherData("T_OPT2_WORK_DESCRIPTION2"));
		sustituteEmployeeTempTB.setValue(getContractOtherData("T_OPT3_REPLACED_WORKER_NAME"));
		requirementsTempLB.setValue(getContractOtherData("T_OPT10_REQUIREMENTS_OPT"));
		formationTempLB.setValue(getContractOtherData("T_OPT10_FORMATION_OPT"));
		formationWillTempLB.setValue(getContractOtherData("T_OPT10_FORMATION_TYPE_OPT"));
		officeSPEmployeeTempTB.setValue(getContractOtherData("T_OPT10_FORMATION_TYPE_OPT1_TEXT"));
		lenguageFormationTempTB.setValue(getContractOtherData("T_OPT10_FORMATION_TYPE_OPT2_TEXT"));
		hoursDealTempB.setValue(AonStringUtils.containsIgnoreCase(getContractOtherData("T_OPT12_ONSITE_HOURS"), "YES"));
		presentHoursTempTB.setValue(getContractOtherData("T_OPT12_ONSITE_WEEK_HOURS"));
		distributionHoursTempTB.setValue(getContractOtherData("T_OPT12_ONSITE_HOURS_DISTRIB"));
		timeCompensationTempLB.setValue(getContractOtherData("T_OPT12_SALARY_OPT"));
		dealOvernightB.setValue(AonStringUtils.containsIgnoreCase(getContractOtherData("T_OPT12_OVERNIGHT"), "YES"));
		overnightRegimeTempTB.setValue(getContractOtherData("T_OPT12_OVERNIGHT_WEEK_DAYS"));
		officialOrganismTempTB.setValue(getContractOtherData("T_OPT13_DISABILITY_ISSUED_BY"));
		withoutSevereDisTempLB.setValue(getContractOtherData("T_OPT13_DISABILITY"));
		severeDisTempLB.setValue(getContractOtherData("T_OPT13_SEVERE_DISABILITY"));
		adaptationPeriodTempTB.setValue(getContractOtherData("T_OPT14_TRIAL_PERIOD"));
		adaptationConditionsTempTB.setValue(getContractOtherData("T_OPT14_TRIAL_TERMS"));
		adaptationWorkTempLB.setValue(getContractOtherData("T_OPT14_PROFESSION"));
		socialPersonalAdjustTempTB.setValue(getContractOtherData("T_OPT14_DISTANCE_ADJUSTMENT"));
		socialPersonalAdjustMoreTempTB.setValue(getContractOtherData("T_OPT14_DISTANCE_ADJUSTMENT_MORE"));
		colectiveAgreementTempTB.setValue(getContractOtherData("T_OPT14_COLLECTIVE_AGREEMENT"));
		
		return table;
	}

	private void addTemporalDataTableHadlers() {
		doingFunctionsTempTB.addValueChangeHandler(e -> setContractOtherData("T_FUNCTIONS", doingFunctionsTempTB.getValue()));
		distanceTempB.addValueChangeHandler(e -> setContractOtherData("T_EMPLOYEE_CONTRACT_DISTANCE", e.getValue() ? "true" : ""));
		distanceAddressTempTB.addValueChangeHandler(e -> setContractOtherData("T_EMPLOYEE_CONTRACT_DIST_ADDR", distanceAddressTempTB.getValue()));
		journeyHoursTCTempTB.addValueChangeHandler(e -> setContractOtherData("T_FULL_TIME_WEEK_HOURS", journeyHoursTCTempTB.getValue()));
		startJourneyTCTempTB.addValueChangeHandler(e -> setContractOtherData("T_FULL_TIME_START_TIME", startJourneyTCTempTB.getValue()));
		endJourneyTCTempTB.addValueChangeHandler(e -> setContractOtherData("T_FULL_TIME_END_TIME", endJourneyTCTempTB.getValue()));	
		lowJourneyTempTB.addValueChangeHandler(e -> setContractOtherData("T_PARTIALLY_TIME_JOB_LOWER_THAN", lowJourneyTempTB.getValue()));
		timeDistributionTempTB.addValueChangeHandler(e -> setContractOtherData("T_PARTIALLY_TIME_JOB_DISTRIB", timeDistributionTempTB.getValue()));
		complementaryHoursTempB.addValueChangeHandler(e -> setContractOtherData("T_COMPLEMENTARY_HOURS", e.getValue() ? "COMPLEMENTARY_HOURS_YES" : "COMPLEMENTARY_HOURS_NO"));
		endContractTempTB.addValueChangeHandler(e -> setContractOtherData("T_END_DATE_TEXT", endContractTempTB.getValue()));
		trialPeriodTempTB.addValueChangeHandler(e -> setContractOtherData("T_TRIAL_DURATION", trialPeriodTempTB.getValue()));
		permitedHighDurationTempB.addValueChangeHandler(e -> setContractOtherData("T_GREATER_DURATION_AGREEMENT_COL", e.getValue() ? "true" : ""));
		salaryAmountTempTB.addValueChangeHandler(e -> setContractOtherData("T_SALARY_AMOUNT", salaryAmountTempTB.getValue()));
		salaryPeriodTempTB.addValueChangeHandler(e -> setContractOtherData("T_SALARY_PERIOD", salaryPeriodTempTB.getValue()));
		salaryConceptTempTB.addValueChangeHandler(e -> setContractOtherData("T_SALARY_CONCEPT", salaryConceptTempTB.getValue()));
		holidaysTempTB.addValueChangeHandler(e -> setContractOtherData("T_HOLIDAYS", holidaysTempTB.getValue()));
		sepeOfficeTempTB.addValueChangeHandler(e -> setContractOtherData("T_SEPE_MUNICIPALITY", sepeOfficeTempTB.getValue()));
		workTempTB.addValueChangeHandler(e -> setContractOtherData("T_OPT1_WORK_DESCRIPTION1", workTempTB.getValue()));
		workMoreTempTB.addValueChangeHandler(e -> setContractOtherData("T_OPT1_WORK_DESCRIPTION2", workMoreTempTB.getValue()));
		taskTempTB.addValueChangeHandler(e -> setContractOtherData("T_OPT2_WORK_DESCRIPTION1", taskTempTB.getValue()));
		taskMoreTempTB.addValueChangeHandler(e -> setContractOtherData("T_OPT2_WORK_DESCRIPTION2", taskMoreTempTB.getValue()));
		sustituteEmployeeTempTB.addValueChangeHandler(e -> setContractOtherData("T_OPT3_REPLACED_WORKER_NAME", sustituteEmployeeTempTB.getValue()));
		requirementsTempLB.addChangeHandler(e -> setContractOtherData("T_OPT10_REQUIREMENTS_OPT", requirementsTempLB.getValue()));
		formationTempLB.addChangeHandler(e -> setContractOtherData("T_OPT10_FORMATION_OPT", formationTempLB.getValue()));
		formationWillTempLB.addChangeHandler(e -> setContractOtherData("T_OPT10_FORMATION_TYPE_OPT", formationWillTempLB.getValue()));
		officeSPEmployeeTempTB.addValueChangeHandler(e -> setContractOtherData("T_OPT10_FORMATION_TYPE_OPT1_TEXT", officeSPEmployeeTempTB.getValue()));
		lenguageFormationTempTB.addValueChangeHandler(e -> setContractOtherData("T_OPT10_FORMATION_TYPE_OPT2_TEXT", lenguageFormationTempTB.getValue()));
		hoursDealTempB.addValueChangeHandler(e -> setContractOtherData("T_OPT12_ONSITE_HOURS", e.getValue() ? "OPT12_ONSITE_HOURS_YES" : "OPT12_ONSITE_HOURS_NO"));
		presentHoursTempTB.addValueChangeHandler(e -> setContractOtherData("T_OPT12_ONSITE_WEEK_HOURS", presentHoursTempTB.getValue()));
		distributionHoursTempTB.addValueChangeHandler(e -> setContractOtherData("T_OPT12_ONSITE_HOURS_DISTRIB", distributionHoursTempTB.getValue()));
		timeCompensationTempLB.addChangeHandler(e -> setContractOtherData("T_OPT12_SALARY_OPT", timeCompensationTempLB.getValue()));
		dealOvernightB.addValueChangeHandler(e -> setContractOtherData("T_OPT12_OVERNIGHT", e.getValue() ? "OPT12_OVERNIGHT_YES" : "OPT12_OVERNIGHT_NO"));
		overnightRegimeTempTB.addValueChangeHandler(e -> setContractOtherData("T_OPT12_OVERNIGHT_WEEK_DAYS", overnightRegimeTempTB.getValue()));
		officialOrganismTempTB.addValueChangeHandler(e -> setContractOtherData("T_OPT13_DISABILITY_ISSUED_BY", officialOrganismTempTB.getValue()));
		withoutSevereDisTempLB.addChangeHandler(e -> setContractOtherData("T_OPT13_DISABILITY", withoutSevereDisTempLB.getValue()));
		severeDisTempLB.addChangeHandler(e -> setContractOtherData("T_OPT13_SEVERE_DISABILITY", severeDisTempLB.getValue()));
		adaptationPeriodTempTB.addValueChangeHandler(e -> setContractOtherData("T_OPT14_TRIAL_PERIOD", adaptationPeriodTempTB.getValue()));
		adaptationConditionsTempTB.addValueChangeHandler(e -> setContractOtherData("T_OPT14_TRIAL_TERMS", adaptationConditionsTempTB.getValue()));
		adaptationWorkTempLB.addChangeHandler(e -> setContractOtherData("T_OPT14_PROFESSION", adaptationWorkTempLB.getValue()));
		socialPersonalAdjustTempTB.addValueChangeHandler(e -> setContractOtherData("T_OPT14_DISTANCE_ADJUSTMENT", socialPersonalAdjustTempTB.getValue()));
		socialPersonalAdjustMoreTempTB.addValueChangeHandler(e -> setContractOtherData("T_OPT14_DISTANCE_ADJUSTMENT_MORE", socialPersonalAdjustMoreTempTB.getValue()));
		colectiveAgreementTempTB.addValueChangeHandler(e -> setContractOtherData("T_OPT14_COLLECTIVE_AGREEMENT", colectiveAgreementTempTB.getValue()));
	}

	// ------------------------------------------------------------------------
	//							Class Methods
	// ------------------------------------------------------------------------
	
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
		
	private String getFullName() {
		String name = enterpriseAgentNameTB.getValue();
		String surname = enterpriseAgentSurnameTB.getValue();
		
		String fullName = AonStringUtils.isBlank(surname) ? null : surname;
		return AonStringUtils.isNotBlank(fullName) ? fullName + ", " + name : name;
	}

	private void setContractOtherData(String name, String value) {
		this.contractEmployeeInfo.addContractOtherData(name, value);
	}
	
	private String getContractOtherData(String name) {
		return this.contractEmployeeInfo.getContractOtherData().get(name);
	}
	
	private Boolean getContractOtherDataCB(String name) {
		String value = this.contractEmployeeInfo.getContractOtherData().get(name);
		return null != value;
	}
	
	private String getEnterpriseAgentName(String fullName) {
		return AonStringUtils.isNotBlank(fullName) && AonStringUtils.containsIgnoreCase(fullName, ",") ? fullName.split(",")[1].trim() : null;
	}
	
	private String getEnterpriseAgentSurname(String fullName) {
		return AonStringUtils.isNotBlank(fullName) && AonStringUtils.containsIgnoreCase(fullName, ",") ? fullName.split(",")[0].trim() : fullName;
	}

	public  Map<String, String> getContractOtherData() {
		return contractEmployeeInfo.getContractOtherData();
	}
	
	// ------------------------------------------------------ Toolbar Methods

	public void saveOhterData() {
		showLoadingMessage("Guardando otros datos de contrato...");
		saveOhterDataDB(s -> {
				showSuccessMessage("Otros Datos", "Otros datos de contrato guardados correctamente");
				initializeView();
			}, f -> showErrorMessage("Error obtenci\u00f3n Otro Datos contrato", f.getMessage()));
	}
	
	private void saveOhterDataDB(Consumer<Map<String, String>> success, Consumer<Throwable> failure) {
		Integer contractId = contractEmployeeInfo.getContractInfo().getContractId();
		String contractType = contractEmployeeInfo.getContractInfo().getContractType();
		Map<String, String> contractOtherData = contractEmployeeInfo.getContractOtherData();
		
		enterprisesService.setContractOtherInfo(contractId, contractType, contractOtherData, new AsyncCallback<Map<String, String>>() {
			
			@Override
			public void onSuccess(Map<String, String> contractOtherData) {
				success.accept(contractOtherData);
			}

			@Override
			public void onFailure(Throwable caught) {
				failure.accept(caught);
			}
			
		});
	}
	
	public void getContractOtherDataDB(Consumer<Map<String, String>> success, Consumer<Throwable> failure) {
		Integer contractId = contractEmployeeInfo.getContractInfo().getContractId();
		
		enterprisesService.getContractOtherInfo(contractId, contractType, new AsyncCallback<Map<String, String>>() {
			
			@Override
			public void onSuccess(Map<String, String> contractOtherData) {
				contractEmployeeInfo.setContractOtherData(contractOtherData);
				success.accept(contractOtherData);
			}

			@Override
			public void onFailure(Throwable caught) {
				failure.accept(caught);
			}
			
		});
	}
	
	// ------------------------------------------------------ Abstract Methods

	protected abstract void showErrorMessage(String title, String message);

	protected abstract void showSuccessMessage(String title, String message);
	
	protected abstract void showLoadingMessage(String message);
	
	protected abstract void onContractPDF();	
	
}
