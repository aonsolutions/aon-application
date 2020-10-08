package com.esferalia.aon.gwt.payroll.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public abstract class ContractOtherData extends ResizeComposite {

	// -------------------------------------------------- UiBinder --------------------------------------------------

	private static ContractOtherDataUiBinder uiBinder = GWT.create(ContractOtherDataUiBinder.class);

	interface ContractOtherDataUiBinder extends UiBinder<Widget, ContractOtherData> {}

	// -------------------------------------------------- UiFields --------------------------------------------------

	@UiField
	MyStyle style;

	interface MyStyle extends CssResource {}
	
	// -------------------------------------------------- Indefinite Table
	
	@UiField
	VerticalPanel indefiniteTable;

	@UiField
	TextBox enterpriseAgentTB;
	
	@UiField
	TextBox enterpriseAgentNIFTB;
	
	@UiField
	TextBox enterpriseAgentPositionTB;

	@UiField
	TextBox minorAgentTB;

	@UiField
	TextBox minorAgentNIFTB;

	@UiField
	TextBox minorAgentQualityOfTB;

	@UiField
	TextBox doingFunctionsTB;

	@UiField
	CheckBox distanceCB;

	@UiField
	TextBox distanceAddressTB;

	@UiField
	TextBox discontinuousWorkTB;

	@UiField
	TextBox intermittentCyclicalActivityTB;

	@UiField
	TextBox durationFDTB;

	@UiField
	TextBox activityStimationDurationFDTB;

	@UiField
	TextBox journeyHoursFDTB;

	@UiField
	TextBox journeyPeriodFDTB;

	@UiField
	TextBox timeDistributionFDTB;

	@UiField
	ListBox partialTimeLB;
	
	@UiField
	TextBox journeyHoursTCTB;

	@UiField
	TextBox startJourneyTCTB;

	@UiField
	TextBox endJourneyTCTB;

	@UiField
	TextBox journeyHoursTPTB;

	@UiField
	TextBox agreementJourneyHoursTB;

	@UiField
	ListBox complementaryHoursLB;
	
	@UiField
	TextBox trialPeriodTB;

	@UiField
	TextBox salaryAmountTB;

	@UiField
	TextBox salaryPeriodTB;

	@UiField
	TextBox salaryConceptTB;

	@UiField
	TextBox holidaysTB;

	@UiField
	TextBox sepeOfficeTB;
	
	@UiField
	TextBox accreditedDisabilityTB;
	
	@UiField
	ListBox withoutDisabilitySevereLB;
	
	@UiField
	ListBox disabilitySevereLB;
	
	@UiField
	TextBox subsidyTB;
	
	@UiField
	ListBox fourthLawLB;
	
	@UiField
	ListBox unemploymentLB;
	
	@UiField
	ListBox unemploymentOldLB;
	
	@UiField
	CheckBox benefitsPerceptorCB;
	
	@UiField
	CheckBox firstEmployeeCB;
	
	@UiField
	ListBox employeeLB;
	
	@UiField
	TextBox agreementLineOneTB;
	
	@UiField
	TextBox agreementLineTwoTB;
	
	@UiField
	ListBox contactHoursLB;
	
	@UiField
	TextBox hoursTB;
	
	@UiField
	ListBox remunerationFormLB;
	
	@UiField
	ListBox overnightAgreementLB;
	
	@UiField
	TextBox overnightRegimeTB;
	
	@UiField
	ListBox quoteReductionTCLB;
	
	@UiField
	ListBox quoteReductionFDLB;
	
	@UiField
	TextBox sepeOfficeCOTB;
	
	// -------------------------------------------------- Temporal Table
	
	@UiField
	VerticalPanel temporalTable;
	
	@UiField
	TextBox enterpriseAgentTempTB;
	
	@UiField
	TextBox enterpriseAgentNIFTempTB;
	
	@UiField
	TextBox enterpriseAgentPositionTempTB;

	@UiField
	TextBox minorAgentTempTB;

	@UiField
	TextBox minorAgentNIFTempTB;

	@UiField
	TextBox minorAgentQualityOfTempTB;

	@UiField
	TextBox doingFunctionsTempTB;

	@UiField
	CheckBox distanceTempCB;

	@UiField
	TextBox distanceAddressTempTB;
	
	@UiField
	TextBox journeyHoursTCTempTB;
	
	@UiField
	TextBox startJourneyTCTempTB;
	
	@UiField
	TextBox endJourneyTCTempTB;
	
	@UiField
	TextBox lowJourneyTempTB;
	
	@UiField
	TextBox timeDistributionTempTB;
	
	@UiField
	TextBox endContractTempTB;
	
	@UiField
	TextBox trialPeriodTempTB;
	
	@UiField
	CheckBox permitedHighDurationTempCB;
	
	@UiField
	TextBox salaryAmountTempTB;
	
	@UiField
	TextBox salaryPeriodTempTB;
	
	@UiField
	TextBox salaryConceptTempTB;
	
	@UiField
	TextBox holidaysTempTB;
	
	@UiField
	TextBox sepeOfficeTempTB;
	
	@UiField
	TextBox workTempTB;
	
	@UiField
	TextBox workMoreTempTB;
	
	@UiField
	TextBox taskTempTB;
	
	@UiField
	TextBox taskMoreTempTB;
	
	@UiField
	TextBox sustituteEmployeeTempTB;
	
	@UiField
	ListBox requirementsTempLB;
	
	@UiField
	ListBox formationTempLB;
	
	@UiField
	ListBox formationWillTempLB;
	
	@UiField
	TextBox officeSPEmployeeTempTB;
	
	@UiField
	TextBox lenguageFormationTempTB;
	
	@UiField
	ListBox hoursDealTempLB;
	
	@UiField
	TextBox presentHoursTempTB;
	
	@UiField
	TextBox distributionHoursTempTB;
	
	@UiField
	ListBox timeCompensationTempLB;
	
	@UiField
	ListBox dealOvernightLB;
	
	@UiField
	TextBox overnightRegimeTempTB;
	
	@UiField
	TextBox officialOrganismTempTB;
	
	@UiField
	ListBox withoutSevereDisTempLB;
	
	@UiField
	ListBox severeDisTempLB;
	
	@UiField
	TextBox adaptationPeriodTempTB;
	
	@UiField
	TextBox adaptationConditionsTempTB;
	
	@UiField
	ListBox adaptationWorkTempLB;
	
	@UiField
	TextBox socialPersonalAdjustTempTB;
	
	@UiField
	TextBox socialPersonalAdjustMoreTempTB;
	
	@UiField
	TextBox colectiveAgreementTempTB;
	
	// -------------------------------------------------- Formation Table
	
	@UiField
	VerticalPanel formationTable;
	
	@UiField
	TextBox enterpriseAgentFormTB;
	
	@UiField
	TextBox enterpriseAgentNIFFormTB;
	
	@UiField
	TextBox enterpriseAgentPositionFormTB;

	@UiField
	TextBox minorAgentFormTB;

	@UiField
	TextBox minorAgentNIFFormTB;

	@UiField
	TextBox minorAgentQualityOfFormTB;
	
	@UiField
	ListBox ssReductionFormLB;
	
	@UiField
	ListBox employeeFormLB;
	
	@UiField
	TextBox workplaceFormTB;
	
	@UiField
	TextBox tutorFormTB;
	
	@UiField
	TextBox efectiveWorkHoursFormTB;
	
	@UiField
	TextBox activityHoursFormTB;
	
	@UiField
	TextBox trialPeriodFormTB;
	
	@UiField
	CheckBox agreementTrialFormCB;
	
	@UiField
	TextBox salaryAmountFormTB;
	
	@UiField
	TextBox salaryPeriodFormTB;
	
	@UiField
	TextBox holidaysFormTB;
	
	@UiField
	CheckBox degreeExistFormCB;
	
	@UiField
	CheckBox degreeExist2FormCB;

	// -------------------------------------------------- Practice Table
	
	@UiField
	VerticalPanel practiceTable;
	
	@UiField
	TextBox enterpriseAgentPracTB;
	
	@UiField
	TextBox enterpriseAgentNIFPracTB;
	
	@UiField
	TextBox enterpriseAgentPositionPracTB;

	@UiField
	TextBox minorAgentPracTB;

	@UiField
	TextBox minorAgentNIFPracTB;

	@UiField
	TextBox minorAgentQualityOfPracTB;
	
	@UiField
	TextBox profesionalCertPracTB;
	
	@UiField
	TextBox obtainingDatePracTB;
	
	@UiField
	TextBox disabilityCertPracTB;
	
	@UiField
	TextBox disabilityCertMorePracTB;
	
	@UiField
	ListBox firstContractPracLB;
	
	@UiField
	TextBox journeyHoursPracTB;
	
	@UiField
	TextBox startJourneyPracTB;
	
	@UiField
	TextBox endJourneyPracTB;
	
	@UiField
	TextBox distributionJourneyPracTB;
	
	@UiField
	TextBox trialPeriodPracTB;
	
	@UiField
	TextBox salaryAmountPracTB;
	
	@UiField
	TextBox salaryPeriodPracTB;
	
	@UiField
	TextBox salaryConceptPracTB;
	
	@UiField
	TextBox holidaysPracTB;
	
	@UiField
	TextBox sepeComunicationPracTB;
	
	@UiField
	TextBox endSepeComunicationPracTB;
	
	@UiField
	ListBox unemploymentSubsidyPracLB;
	
	@UiField
	TextBox adaptationPeriodPracTB;
	
	@UiField
	TextBox adaptationConditionsPracTB;
	
	@UiField
	TextBox adaptationWorkPracTB;
	
	@UiField
	TextBox personalSocialAdjustPracTB;
	
	@UiField
	TextBox personalSocialAdjustMorePracTB;
	
	@UiField
	ListBox motivationPracLB;
	
	@UiField
	ListBox employerPracLB;
	
	// ------------------------------------------------------ Constructor ---------------------------------------------------------

	public ContractOtherData() {
		initWidget(uiBinder.createAndBindUi(this));
		initializeView();
	}
	
	// --------------------------------------------------------- UiHandlers --------------------------------------------------------
	
	// ------------------------------------------------------- Indefinite Table
	
	@UiHandler("enterpriseAgentTB")
	void onEnterpriseAgentTBChange(ValueChangeEvent<String> event) {
		onEnterpriseAgentTBChange();
	}
	
	@UiHandler("enterpriseAgentNIFTB")
	void onEnterpriseAgentNIFTBChange(ValueChangeEvent<String> event) {
		onEnterpriseAgentNIFTBChange();
	}
	
	@UiHandler("enterpriseAgentPositionTB")
	void onEnterpriseAgentPositionTBChange(ValueChangeEvent<String> event) {
		onEnterpriseAgentPositionTBChange();
	}
	
	@UiHandler("minorAgentTB")
	void onMinorAgentTBChange(ValueChangeEvent<String> event) {
		onMinorAgentTBChange();
	}
	
	@UiHandler("minorAgentNIFTB")
	void onMinorAgentNIFTBChange(ValueChangeEvent<String> event) {
		onMinorAgentNIFTBChange();
	}
	
	@UiHandler("minorAgentQualityOfTB")
	void onMinorAgentQualityOfTBChange(ValueChangeEvent<String> event) {
		onMinorAgentQualityOfTBChange();
	}
	
	@UiHandler("doingFunctionsTB")
	void onDoingFunctionsTBChange(ValueChangeEvent<String> event) {
		onDoingFunctionsTBChange();
	}
	
	@UiHandler("distanceCB")
	void onDistanceCBChange(ValueChangeEvent<Boolean> event) {
		onDistanceCBChange();
	}
	
	@UiHandler("distanceAddressTB")
	void onDistanceAddressTBChange(ValueChangeEvent<String> event) {
		onDistanceAddressTBChange();
	}
	
	@UiHandler("discontinuousWorkTB")
	void onDiscontinuousWorkTBChange(ValueChangeEvent<String> event) {
		onDiscontinuousWorkTBChange();
	}
	
	@UiHandler("intermittentCyclicalActivityTB")
	void onIntermittentCyclicalActivityTBChange(ValueChangeEvent<String> event) {
		onIntermittentCyclicalActivityTBChange();
	}
	
	@UiHandler("durationFDTB")
	void onDurationFDTBChange(ValueChangeEvent<String> event) {
		onDurationFDTBChange();
	}

	@UiHandler("activityStimationDurationFDTB")
	void onActivityStimationDurationFDTBChange(ValueChangeEvent<String> event) {
		onActivityStimationDurationFDTBChange();
	}
	
	@UiHandler("journeyHoursFDTB")
	void onJourneyHoursFDTBChange(ValueChangeEvent<String> event) {
		onJourneyHoursFDTBChange();
	}
	
	@UiHandler("journeyPeriodFDTB")
	void onJourneyPeriodFDTBChange(ValueChangeEvent<String> event) {
		onJourneyPeriodFDTBChange();
	}

	@UiHandler("timeDistributionFDTB")
	void onTimeDistributionFDTBChange(ValueChangeEvent<String> event) {
		onTimeDistributionFDTBChange();
	}
	
	@UiHandler("partialTimeLB")
	void onPartialTimeLBChange(ChangeEvent event) {
		onPartialTimeLBChange();
	}
	
	@UiHandler("journeyHoursTCTB")
	void onJourneyHoursTCTBChange(ValueChangeEvent<String> event) {
		onJourneyHoursTCTBChange();
	}
	
	@UiHandler("startJourneyTCTB")
	void onStartJourneyTCTBChange(ValueChangeEvent<String> event) {
		onStartJourneyTCTBChange();
	}
	
	@UiHandler("endJourneyTCTB")
	void onEndJourneyTCTBChange(ValueChangeEvent<String> event) {
		onEndJourneyTCTBChange();
	}
	
	@UiHandler("journeyHoursTPTB")
	void onJourneyHoursTPTBChange(ValueChangeEvent<String> event) {
		onJourneyHoursTPTBChange();
	}
	
	@UiHandler("agreementJourneyHoursTB")
	void onAgreementJourneyHoursTBChange(ValueChangeEvent<String> event) {
		onAgreementJourneyHoursTBChange();
	}
	
	@UiHandler("complementaryHoursLB")
	void onComplementaryHoursLBChange(ChangeEvent event) {
		onComplementaryHoursLBChange();
	}
	
	@UiHandler("trialPeriodTB")
	void onTrialPeriodTBChange(ValueChangeEvent<String> event) {
		onTrialPeriodTBChange();
	}
	
	@UiHandler("salaryAmountTB")
	void onSalaryAmountTBChange(ValueChangeEvent<String> event) {
		onSalaryAmountTBChange();
	}
	
	@UiHandler("salaryPeriodTB")
	void onSalaryPeriodTBChange(ValueChangeEvent<String> event) {
		onSalaryPeriodTBChange();
	}
	
	@UiHandler("salaryConceptTB")
	void onSalaryConceptTBChange(ValueChangeEvent<String> event) {
		onSalaryConceptTBChange();
	}
	
	@UiHandler("holidaysTB")
	void onHolidaysTBChange(ValueChangeEvent<String> event) {
		onHolidaysTBChange();
	}
	
	@UiHandler("sepeOfficeTB")
	void onSepeOfficeTBChange(ValueChangeEvent<String> event) {
		onSepeOfficeTBChange();
	}
	
	@UiHandler("accreditedDisabilityTB")
	void onAccreditedDisabilityTBChange(ValueChangeEvent<String> event) {
		onAccreditedDisabilityTBChange();
	}
	
	@UiHandler("withoutDisabilitySevereLB")
	void onWithoutDisabilitySevereLBChange(ChangeEvent event) {
		onWithoutDisabilitySevereLBChange();
	}

	@UiHandler("disabilitySevereLB")
	void onDisabilitySevereLBChange(ChangeEvent event) {
		onDisabilitySevereLBChange();
	}

	@UiHandler("subsidyTB")
	void onSubsidyTBChange(ValueChangeEvent<String> event) {
		onSubsidyTBChange();
	}
	
	@UiHandler("fourthLawLB")
	void onFourthLawLBChange(ChangeEvent event) {
		onFourthLawLBChange();
	}
	
	@UiHandler("unemploymentLB")
	void onUnemploymentLBChange(ChangeEvent event) {
		onUnemploymentLBChange();
	}

	@UiHandler("unemploymentOldLB")
	void onUnemploymentOldLBChange(ChangeEvent event) {
		onUnemploymentOldLBChange();
	}
	
	@UiHandler("benefitsPerceptorCB")
	void onBenefitsPerceptorCBChange(ValueChangeEvent<Boolean> event) {
		onBenefitsPerceptorCBChange();
	}
	
	@UiHandler("firstEmployeeCB")
	void onFirstEmployeeCBChange(ValueChangeEvent<Boolean> event) {
		onFirstEmployeeCBChange();
	}
	
	@UiHandler("employeeLB")
	void onEmployeeLBChange(ChangeEvent event) {
		onEmployeeLBChange();
	}
	
	@UiHandler("agreementLineOneTB")
	void onAgreementLineOneTBChange(ValueChangeEvent<String> event) {
		onAgreementLineOneTBChange();
	}
	
	@UiHandler("agreementLineTwoTB")
	void onAgreementLineTwoTBChange(ValueChangeEvent<String> event) {
		onAgreementLineTwoTBChange();
	}
	
	@UiHandler("contactHoursLB")
	void onContactHoursLBChange(ChangeEvent event) {
		onContactHoursLBChange();
	}

	@UiHandler("hoursTB")
	void onHoursTBChange(ValueChangeEvent<String> event) {
		onHoursTBChange();
	}

	@UiHandler("remunerationFormLB")
	void onRemunerationFormLBChange(ChangeEvent event) {
		onRemunerationFormLBChange();
	}
	
	@UiHandler("overnightAgreementLB")
	void onOvernightAgreementLBChange(ChangeEvent event) {
		onOvernightAgreementLBChange();
	}
	
	@UiHandler("overnightRegimeTB")
	void onOvernightRegimeTBChange(ValueChangeEvent<String> event) {
		onOvernightRegimeTBChange();
	}
	
	@UiHandler("quoteReductionTCLB")
	void onQuoteReductionTCLBChange(ChangeEvent event) {
		onQuoteReductionTCLBChange();
	}
	
	@UiHandler("quoteReductionFDLB")
	void onQuoteReductionFDLBChange(ChangeEvent event) {
		onQuoteReductionFDLBChange();
	}
	
	@UiHandler("sepeOfficeCOTB")
	void onSepeOfficeCOTBChange(ValueChangeEvent<String> event) {
		onSepeOfficeCOTBChange();
	}
	
	// ------------------------------------------------------- Temporal Table
	
	@UiHandler("enterpriseAgentTempTB")
	void onEnterpriseAgentTempTBChange(ValueChangeEvent<String> event) {
		onEnterpriseAgentTempTBChange();
	}
	
	@UiHandler("enterpriseAgentNIFTempTB")
	void onEnterpriseAgentNIFTempTBChange(ValueChangeEvent<String> event) {
		onEnterpriseAgentNIFTempTBChange();
	}
	
	@UiHandler("enterpriseAgentPositionTempTB")
	void onEnterpriseAgentPositionTempTBChange(ValueChangeEvent<String> event) {
		onEnterpriseAgentPositionTempTBChange();
	}
	
	@UiHandler("minorAgentTempTB")
	void onMinorAgentTempTBChange(ValueChangeEvent<String> event) {
		onMinorAgentTempTBChange();
	}
	
	@UiHandler("minorAgentNIFTempTB")
	void onMinorAgentNIFTempTBChange(ValueChangeEvent<String> event) {
		onMinorAgentNIFTempTBChange();
	}
	
	@UiHandler("minorAgentQualityOfTempTB")
	void onMinorAgentQualityOfTempTBChange(ValueChangeEvent<String> event) {
		onMinorAgentQualityOfTempTBChange();
	}
	
	@UiHandler("doingFunctionsTempTB")
	void onDoingFunctionsTempTBChange(ValueChangeEvent<String> event) {
		onDoingFunctionsTempTBChange();
	}
	
	@UiHandler("distanceTempCB")
	void onDistanceTempCBChange(ValueChangeEvent<Boolean> event) {
		onDistanceTempCBChange();
	}
	
	@UiHandler("distanceAddressTempTB")
	void onDistanceAddressTempTBChange(ValueChangeEvent<String> event) {
		onDistanceAddressTempTBChange();
	}
	
	@UiHandler("journeyHoursTCTempTB")
	void onJourneyHoursTCTempTBChange(ValueChangeEvent<String> event) {
		onJourneyHoursTCTempTBChange();
	}
	
	@UiHandler("startJourneyTCTempTB")
	void onStartJourneyTCTempTBChange(ValueChangeEvent<String> event) {
		onStartJourneyTCTempTBChange();
	}
	
	@UiHandler("endJourneyTCTempTB")
	void onEndJourneyTCTempTBChange(ValueChangeEvent<String> event) {
		onEndJourneyTCTempTBChange();
	}
	
	@UiHandler("lowJourneyTempTB")
	void onLowJourneyTempTBChange(ValueChangeEvent<String> event) {
		onLowJourneyTempTBChange();
	}
	
	@UiHandler("timeDistributionTempTB")
	void onTimeDistributionTempTBChange(ValueChangeEvent<String> event) {
		onTimeDistributionTempTBChange();
	}
	
	@UiHandler("endContractTempTB")
	void onEndContractTempTBChange(ValueChangeEvent<String> event) {
		onEndContractTempTBChange();
	}
	
	@UiHandler("trialPeriodTempTB")
	void onTrialPeriodTempTBChange(ValueChangeEvent<String> event) {
		onTrialPeriodTempTBChange();
	}
	
	@UiHandler("permitedHighDurationTempCB")
	void onPermitedHighDurationTempCBChange(ValueChangeEvent<Boolean> event) {
		onPermitedHighDurationTempCBChange();
	}
	
	@UiHandler("salaryAmountTempTB")
	void onSalaryAmountTempTBChange(ValueChangeEvent<String> event) {
		onSalaryAmountTempTBChange();
	}
	
	@UiHandler("salaryPeriodTempTB")
	void onSalaryPeriodTempTBChange(ValueChangeEvent<String> event) {
		onSalaryPeriodTempTBChange();
	}
	
	@UiHandler("salaryConceptTempTB")
	void onSalaryConceptTempTBChange(ValueChangeEvent<String> event) {
		onSalaryConceptTempTBChange();
	}
	
	@UiHandler("holidaysTempTB")
	void onHolidaysTempTBChange(ValueChangeEvent<String> event) {
		onHolidaysTempTBChange();
	}
	
	@UiHandler("sepeOfficeTempTB")
	void onSepeOfficeTempTBChange(ValueChangeEvent<String> event) {
		onSepeOfficeTempTBChange();
	}
	
	@UiHandler("workTempTB")
	void onWorkTempTBChange(ValueChangeEvent<String> event) {
		onWorkTempTBChange();
	}
	
	@UiHandler("workMoreTempTB")
	void onWorkMoreTempTBChange(ValueChangeEvent<String> event) {
		onWorkMoreTempTBChange();
	}
	
	@UiHandler("taskTempTB")
	void onTaskTempTBChange(ValueChangeEvent<String> event) {
		onTaskTempTBChange();
	}
	
	@UiHandler("taskMoreTempTB")
	void onTaskMoreTempTBChange(ValueChangeEvent<String> event) {
		onTaskMoreTempTBChange();
	}
	
	@UiHandler("sustituteEmployeeTempTB")
	void onSustituteEmployeeTempTBChange(ValueChangeEvent<String> event) {
		onSustituteEmployeeTempTBChange();
	}
	
	@UiHandler("requirementsTempLB")
	void onRequirementsTempLBChange(ChangeEvent event) {
		onRequirementsTempLBChange();
	}
	
	@UiHandler("formationTempLB")
	void onFormationTempLBChange(ChangeEvent event) {
		onFormationTempLBChange();
	}
	
	@UiHandler("formationWillTempLB")
	void onFormationWillTempLBChange(ChangeEvent event) {
		onFormationWillTempLBChange();
	}
	
	@UiHandler("officeSPEmployeeTempTB")
	void onOfficeSPEmployeeTempTBChange(ValueChangeEvent<String> event) {
		onOfficeSPEmployeeTempTBChange();
	}
	
	@UiHandler("lenguageFormationTempTB")
	void onLenguageFormationTempTBChange(ValueChangeEvent<String> event) {
		onLenguageFormationTempTBChange();
	}
	
	@UiHandler("hoursDealTempLB")
	void onHoursDealTempLBChange(ChangeEvent event) {
		onHoursDealTempLBChange();
	}
	
	@UiHandler("presentHoursTempTB")
	void onPresentHoursTempTBChange(ValueChangeEvent<String> event) {
		onPresentHoursTempTBChange();
	}
	
	@UiHandler("distributionHoursTempTB")
	void onDistributionHoursTempTBChange(ValueChangeEvent<String> event) {
		onDistributionHoursTempTBChange();
	}
	
	@UiHandler("timeCompensationTempLB")
	void onTimeCompensationTempLBChange(ChangeEvent event) {
		onTimeCompensationTempLBChange();
	}
	
	@UiHandler("dealOvernightLB")
	void onDealOvernightLBChange(ChangeEvent event) {
		onDealOvernightLBChange();
	}
	
	@UiHandler("overnightRegimeTempTB")
	void onOvernightRegimeTempTBChange(ValueChangeEvent<String> event) {
		onOvernightRegimeTempTBChange();
	}
	
	@UiHandler("officialOrganismTempTB")
	void onOfficialOrganismTempTBChange(ValueChangeEvent<String> event) {
		onOfficialOrganismTempTBChange();
	}
	
	@UiHandler("withoutSevereDisTempLB")
	void onWithoutSevereDisTempLBChange(ChangeEvent event) {
		onWithoutSevereDisTempLBChange();
	}
	
	@UiHandler("severeDisTempLB")
	void onSevereDisTempLBChange(ChangeEvent event) {
		onSevereDisTempLBChange();
	}
	
	@UiHandler("adaptationPeriodTempTB")
	void onAdaptationPeriodTempTBChange(ValueChangeEvent<String> event) {
		onAdaptationPeriodTempTBChange();
	}
	
	@UiHandler("adaptationConditionsTempTB")
	void onAdaptationConditionsTempTBChange(ValueChangeEvent<String> event) {
		onAdaptationConditionsTempTBChange();
	}
	
	@UiHandler("adaptationWorkTempLB")
	void onAdaptationWorkTempLBChange(ChangeEvent event) {
		onAdaptationWorkTempLBChange();
	}
	
	@UiHandler("socialPersonalAdjustTempTB")
	void onSocialPersonalAdjustTempTBChange(ValueChangeEvent<String> event) {
		onSocialPersonalAdjustTempTBChange();
	}
	
	@UiHandler("socialPersonalAdjustMoreTempTB")
	void onSocialPersonalAdjustMoreTempTBChange(ValueChangeEvent<String> event) {
		onSocialPersonalAdjustMoreTempTBChange();
	}
	
	@UiHandler("colectiveAgreementTempTB")
	void onColectiveAgreementTempTBChange(ValueChangeEvent<String> event) {
		onColectiveAgreementTempTBChange();
	}
	
	// ------------------------------------------------------- Formation Table
	
	@UiHandler("enterpriseAgentFormTB")
	void onEnterpriseAgentFormTBChange(ValueChangeEvent<String> event) {
		onEnterpriseAgentFormTBChange();
	}
	
	@UiHandler("enterpriseAgentNIFFormTB")
	void onEnterpriseAgentNIFFormTBChange(ValueChangeEvent<String> event) {
		onEnterpriseAgentNIFFormTBChange();
	}
	
	@UiHandler("enterpriseAgentPositionFormTB")
	void onEnterpriseAgentPositionFormTBChange(ValueChangeEvent<String> event) {
		onEnterpriseAgentPositionFormTBChange();
	}
	
	@UiHandler("minorAgentFormTB")
	void onMinorAgentFormTBChange(ValueChangeEvent<String> event) {
		onMinorAgentFormTBChange();
	}
	
	@UiHandler("minorAgentNIFFormTB")
	void onMinorAgentNIFFormTBChange(ValueChangeEvent<String> event) {
		onMinorAgentNIFFormTBChange();
	}
	
	@UiHandler("minorAgentQualityOfFormTB")
	void onMinorAgentQualityOfFormTBChange(ValueChangeEvent<String> event) {
		onMinorAgentQualityOfFormTBChange();
	}
	
	@UiHandler("ssReductionFormLB")
	void onSsReductionFormLBChange(ChangeEvent event) {
		onSsReductionFormLBChange();
	}
	
	@UiHandler("employeeFormLB")
	void onEmployeeFormLBChange(ChangeEvent event) {
		onEmployeeFormLBChange();
	}
	
	@UiHandler("workplaceFormTB")
	void onWorkplaceFormTBChange(ValueChangeEvent<String> event) {
		onWorkplaceFormTBChange();
	}
	
	@UiHandler("tutorFormTB")
	void onTutorFormTBChange(ValueChangeEvent<String> event) {
		onTutorFormTBChange();
	}
	
	@UiHandler("efectiveWorkHoursFormTB")
	void onEfectiveWorkHoursFormTBChange(ValueChangeEvent<String> event) {
		onEfectiveWorkHoursFormTBChange();
	}
	
	@UiHandler("activityHoursFormTB")
	void onActivityHoursFormTBChange(ValueChangeEvent<String> event) {
		onActivityHoursFormTBChange();
	}
	
	@UiHandler("trialPeriodFormTB")
	void onTrialPeriodFormTBChange(ValueChangeEvent<String> event) {
		onTrialPeriodFormTBChange();
	}
	
	@UiHandler("agreementTrialFormCB")
	void onAgreementTrialFormCBChange(ValueChangeEvent<Boolean> event) {
		onAgreementTrialFormCBChange();
	}
	
	@UiHandler("salaryAmountFormTB")
	void onSalaryAmountFormTBChange(ValueChangeEvent<String> event) {
		onSalaryAmountFormTBChange();
	}
	
	@UiHandler("salaryPeriodFormTB")
	void onSalaryPeriodFormTBChange(ValueChangeEvent<String> event) {
		onSalaryPeriodFormTBChange();
	}
	
	@UiHandler("holidaysFormTB")
	void onHolidaysFormTBChange(ValueChangeEvent<String> event) {
		onHolidaysFormTBChange();
	}
	
	@UiHandler("degreeExistFormCB")
	void onDegreeExistFormCBChange(ValueChangeEvent<Boolean> event) {
		onDegreeExistFormCBChange();
	}
	
	@UiHandler("degreeExist2FormCB")
	void onDegreeExist2FormCBChange(ValueChangeEvent<Boolean> event) {
		onDegreeExist2FormCBChange();
	}
	
	// ------------------------------------------------------- Practice Table
	
	@UiHandler("enterpriseAgentPracTB")
	void onEnterpriseAgentPracTBChange(ValueChangeEvent<String> event) {
		onEnterpriseAgentPracTBChange();
	}
	
	@UiHandler("enterpriseAgentNIFPracTB")
	void onEnterpriseAgentNIFPracTBChange(ValueChangeEvent<String> event) {
		onEnterpriseAgentNIFPracTBChange();
	}
	
	@UiHandler("enterpriseAgentPositionPracTB")
	void onEnterpriseAgentPositionPracTBChange(ValueChangeEvent<String> event) {
		onEnterpriseAgentPositionPracTBChange();
	}
	
	@UiHandler("minorAgentPracTB")
	void onMinorAgentPracTBChange(ValueChangeEvent<String> event) {
		onMinorAgentPracTBChange();
	}
	
	@UiHandler("minorAgentNIFPracTB")
	void onMinorAgentNIFPracTBChange(ValueChangeEvent<String> event) {
		onMinorAgentNIFPracTBChange();
	}
	
	@UiHandler("minorAgentQualityOfPracTB")
	void onMinorAgentQualityOfPracTBChange(ValueChangeEvent<String> event) {
		onMinorAgentQualityOfPracTBChange();
	}
	
	@UiHandler("profesionalCertPracTB")
	void onProfesionalCertPracTBChange(ValueChangeEvent<String> event) {
		onProfesionalCertPracTBChange();
	}
	
	@UiHandler("obtainingDatePracTB")
	void onObtainingDatePracTBChange(ValueChangeEvent<String> event) {
		onObtainingDatePracTBChange();
	}
	
	@UiHandler("disabilityCertPracTB")
	void onDisabilityCertPracTBChange(ValueChangeEvent<String> event) {
		onDisabilityCertPracTBChange();
	}
	
	@UiHandler("disabilityCertMorePracTB")
	void onDisabilityCertMorePracTBChange(ValueChangeEvent<String> event) {
		onDisabilityCertMorePracTBChange();
	}
	
	@UiHandler("firstContractPracLB")
	void onFirstContractPracLBChange(ChangeEvent event) {
		onFirstContractPracLBChange();
	}
	
	@UiHandler("journeyHoursPracTB")
	void onJourneyHoursPracTBChange(ValueChangeEvent<String> event) {
		onJourneyHoursPracTBChange();
	}
	
	@UiHandler("startJourneyPracTB")
	void onStartJourneyPracTBChange(ValueChangeEvent<String> event) {
		onStartJourneyPracTBChange();
	}
	
	@UiHandler("endJourneyPracTB")
	void onEndJourneyPracTBChange(ValueChangeEvent<String> event) {
		onEndJourneyPracTBChange();
	}
	
	@UiHandler("distributionJourneyPracTB")
	void onDistributionJourneyPracTBChange(ValueChangeEvent<String> event) {
		onDistributionJourneyPracTBChange();
	}
	
	@UiHandler("trialPeriodPracTB")
	void onTrialPeriodPracTBChange(ValueChangeEvent<String> event) {
		onTrialPeriodPracTBChange();
	}
	
	@UiHandler("salaryAmountPracTB")
	void onSalaryAmountPracTBChange(ValueChangeEvent<String> event) {
		onSalaryAmountPracTBChange();
	}
	
	@UiHandler("salaryPeriodPracTB")
	void onSalaryPeriodPracTBChange(ValueChangeEvent<String> event) {
		onSalaryPeriodPracTBChange();
	}
	
	@UiHandler("salaryConceptPracTB")
	void onSalaryConceptPracTBChange(ValueChangeEvent<String> event) {
		onSalaryConceptPracTBChange();
	}
	
	@UiHandler("holidaysPracTB")
	void onHolidaysPracTBChange(ValueChangeEvent<String> event) {
		onHolidaysPracTBChange();
	}
	
	@UiHandler("sepeComunicationPracTB")
	void onSepeComunicationPracTBChange(ValueChangeEvent<String> event) {
		onSepeComunicationPracTBChange();
	}
	
	@UiHandler("endSepeComunicationPracTB")
	void onEndSepeComunicationPracTBChange(ValueChangeEvent<String> event) {
		onEndSepeComunicationPracTBChange();
	}
	
	@UiHandler("unemploymentSubsidyPracLB")
	void onUnemploymentSubsidyPracLBChange(ChangeEvent event) {
		onUnemploymentSubsidyPracLBChange();
	}
	
	@UiHandler("adaptationPeriodPracTB")
	void onAdaptationPeriodPracTBChange(ValueChangeEvent<String> event) {
		onAdaptationPeriodPracTBChange();
	}
	
	@UiHandler("adaptationConditionsPracTB")
	void onAdaptationConditionsPracTBChange(ValueChangeEvent<String> event) {
		onAdaptationConditionsPracTBChange();
	}
	
	@UiHandler("adaptationWorkPracTB")
	void onAdaptationWorkPracTBChange(ValueChangeEvent<String> event) {
		onAdaptationWorkPracTBChange();
	}
	
	@UiHandler("personalSocialAdjustPracTB")
	void onPersonalSocialAdjustPracTBChange(ValueChangeEvent<String> event) {
		onPersonalSocialAdjustPracTBChange();
	}
	
	@UiHandler("personalSocialAdjustMorePracTB")
	void onPersonalSocialAdjustMorePracTBChange(ValueChangeEvent<String> event) {
		onPersonalSocialAdjustMorePracTBChange();
	}
	
	@UiHandler("motivationPracLB")
	void onMotivationPracLBChange(ChangeEvent event) {
		onMotivationPracLBChange();
	}
	
	@UiHandler("employerPracLB")
	void onEmployerPracLBChange(ChangeEvent event) {
		onEmployerPracLBChange();
	}

	// ------------------------------------------------------ Abstract Methods ---------------------------------------------------------
	
	// ------------------------------------------------------- Indefinite Table
	
	protected abstract void onEnterpriseAgentTBChange();
	protected abstract void onEnterpriseAgentNIFTBChange();
	protected abstract void onEnterpriseAgentPositionTBChange();
	protected abstract void onMinorAgentTBChange();
	protected abstract void onMinorAgentNIFTBChange();
	protected abstract void onMinorAgentQualityOfTBChange();
	protected abstract void onDoingFunctionsTBChange();
	protected abstract void onDistanceCBChange();
	protected abstract void onDistanceAddressTBChange();
	protected abstract void onDiscontinuousWorkTBChange();
	protected abstract void onIntermittentCyclicalActivityTBChange();
	protected abstract void onDurationFDTBChange();
	protected abstract void onActivityStimationDurationFDTBChange();
	protected abstract void onJourneyHoursFDTBChange();
	protected abstract void onJourneyPeriodFDTBChange();
	protected abstract void onTimeDistributionFDTBChange();
	protected abstract void onPartialTimeLBChange();
	protected abstract void onJourneyHoursTCTBChange();
	protected abstract void onStartJourneyTCTBChange();
	protected abstract void onEndJourneyTCTBChange();
	protected abstract void onJourneyHoursTPTBChange();
	protected abstract void onAgreementJourneyHoursTBChange();
	protected abstract void onComplementaryHoursLBChange();
	protected abstract void onTrialPeriodTBChange();
	protected abstract void onSalaryAmountTBChange();
	protected abstract void onSalaryPeriodTBChange();
	protected abstract void onSalaryConceptTBChange();
	protected abstract void onHolidaysTBChange();
	protected abstract void onSepeOfficeTBChange();
	protected abstract void onAccreditedDisabilityTBChange();
	protected abstract void onWithoutDisabilitySevereLBChange();
	protected abstract void onDisabilitySevereLBChange();
	protected abstract void onSubsidyTBChange();
	protected abstract void onFourthLawLBChange();
	protected abstract void onUnemploymentLBChange();
	protected abstract void onUnemploymentOldLBChange();
	protected abstract void onBenefitsPerceptorCBChange();
	protected abstract void onFirstEmployeeCBChange();
	protected abstract void onEmployeeLBChange();
	protected abstract void onAgreementLineOneTBChange();
	protected abstract void onAgreementLineTwoTBChange();
	protected abstract void onContactHoursLBChange();
	protected abstract void onHoursTBChange();
	protected abstract void onRemunerationFormLBChange();
	protected abstract void onOvernightAgreementLBChange();
	protected abstract void onOvernightRegimeTBChange();
	protected abstract void onQuoteReductionTCLBChange();
	protected abstract void onQuoteReductionFDLBChange();
	protected abstract void onSepeOfficeCOTBChange();
	
	// ------------------------------------------------------- Temporal Table
	
	protected abstract void onEnterpriseAgentTempTBChange();
	protected abstract void onEnterpriseAgentNIFTempTBChange();
	protected abstract void onEnterpriseAgentPositionTempTBChange();
	protected abstract void onMinorAgentTempTBChange();
	protected abstract void onMinorAgentNIFTempTBChange();
	protected abstract void onMinorAgentQualityOfTempTBChange();
	protected abstract void onDoingFunctionsTempTBChange();
	protected abstract void onDistanceTempCBChange();
	protected abstract void onDistanceAddressTempTBChange();
	protected abstract void onJourneyHoursTCTempTBChange();
	protected abstract void onStartJourneyTCTempTBChange();
	protected abstract void onEndJourneyTCTempTBChange();
	protected abstract void onLowJourneyTempTBChange();
	protected abstract void onTimeDistributionTempTBChange();
	protected abstract void onEndContractTempTBChange();
	protected abstract void onTrialPeriodTempTBChange();
	protected abstract void onPermitedHighDurationTempCBChange();
	protected abstract void onSalaryAmountTempTBChange();
	protected abstract void onSalaryPeriodTempTBChange();
	protected abstract void onSalaryConceptTempTBChange();
	protected abstract void onHolidaysTempTBChange();
	protected abstract void onSepeOfficeTempTBChange();
	protected abstract void onWorkTempTBChange();
	protected abstract void onWorkMoreTempTBChange();
	protected abstract void onTaskTempTBChange();
	protected abstract void onTaskMoreTempTBChange();
	protected abstract void onSustituteEmployeeTempTBChange();
	protected abstract void onRequirementsTempLBChange();
	protected abstract void onFormationTempLBChange();
	protected abstract void onFormationWillTempLBChange();
	protected abstract void onOfficeSPEmployeeTempTBChange();
	protected abstract void onLenguageFormationTempTBChange();
	protected abstract void onHoursDealTempLBChange();
	protected abstract void onPresentHoursTempTBChange();
	protected abstract void onDistributionHoursTempTBChange();
	protected abstract void onTimeCompensationTempLBChange();
	protected abstract void onDealOvernightLBChange();
	protected abstract void onOvernightRegimeTempTBChange();
	protected abstract void onOfficialOrganismTempTBChange();
	protected abstract void onWithoutSevereDisTempLBChange();
	protected abstract void onSevereDisTempLBChange();
	protected abstract void onAdaptationPeriodTempTBChange();
	protected abstract void onAdaptationConditionsTempTBChange();
	protected abstract void onAdaptationWorkTempLBChange();
	protected abstract void onSocialPersonalAdjustTempTBChange();
	protected abstract void onSocialPersonalAdjustMoreTempTBChange();
	protected abstract void onColectiveAgreementTempTBChange();
	
	// ------------------------------------------------------- Formation Table
	
	protected abstract void onEnterpriseAgentFormTBChange();
	protected abstract void onEnterpriseAgentNIFFormTBChange();
	protected abstract void onEnterpriseAgentPositionFormTBChange();
	protected abstract void onMinorAgentFormTBChange();
	protected abstract void onMinorAgentNIFFormTBChange();
	protected abstract void onMinorAgentQualityOfFormTBChange();
	protected abstract void onSsReductionFormLBChange();
	protected abstract void onEmployeeFormLBChange();
	protected abstract void onWorkplaceFormTBChange();
	protected abstract void onTutorFormTBChange();
	protected abstract void onEfectiveWorkHoursFormTBChange();
	protected abstract void onActivityHoursFormTBChange();
	protected abstract void onTrialPeriodFormTBChange();
	protected abstract void onAgreementTrialFormCBChange();
	protected abstract void onSalaryAmountFormTBChange();
	protected abstract void onSalaryPeriodFormTBChange();
	protected abstract void onHolidaysFormTBChange();
	protected abstract void onDegreeExistFormCBChange();
	protected abstract void onDegreeExist2FormCBChange();
	
	// ------------------------------------------------------- Practice Table
	
	protected abstract void onEnterpriseAgentPracTBChange();
	protected abstract void onEnterpriseAgentNIFPracTBChange();
	protected abstract void onEnterpriseAgentPositionPracTBChange();
	protected abstract void onMinorAgentPracTBChange();
	protected abstract void onMinorAgentNIFPracTBChange();
	protected abstract void onMinorAgentQualityOfPracTBChange();
	protected abstract void onProfesionalCertPracTBChange();
	protected abstract void onObtainingDatePracTBChange();
	protected abstract void onDisabilityCertPracTBChange();
	protected abstract void onDisabilityCertMorePracTBChange();
	protected abstract void onFirstContractPracLBChange();
	protected abstract void onJourneyHoursPracTBChange();
	protected abstract void onStartJourneyPracTBChange();
	protected abstract void onEndJourneyPracTBChange();
	protected abstract void onDistributionJourneyPracTBChange();
	protected abstract void onTrialPeriodPracTBChange();
	protected abstract void onSalaryAmountPracTBChange();
	protected abstract void onSalaryPeriodPracTBChange();
	protected abstract void onSalaryConceptPracTBChange();
	protected abstract void onHolidaysPracTBChange();
	protected abstract void onSepeComunicationPracTBChange();
	protected abstract void onEndSepeComunicationPracTBChange();
	protected abstract void onUnemploymentSubsidyPracLBChange();
	protected abstract void onAdaptationPeriodPracTBChange();
	protected abstract void onAdaptationConditionsPracTBChange();
	protected abstract void onAdaptationWorkPracTBChange();
	protected abstract void onPersonalSocialAdjustPracTBChange();
	protected abstract void onPersonalSocialAdjustMorePracTBChange();
	protected abstract void onMotivationPracLBChange();
	protected abstract void onEmployerPracLBChange();

	// ------------------------------------------------------------------------
	//							Class Methods
	// ------------------------------------------------------------------------

	private void initializeView() {
		resetElements();
		initializeListBox();
	}

	public void resetElements() {
		
		// ------------------------------------------------------- Indefinite Table
		
		this.enterpriseAgentTB.setValue("");
		this.enterpriseAgentNIFTB.setValue("");
		this.enterpriseAgentPositionTB.setValue("");
		this.minorAgentTB.setValue("");
		this.minorAgentNIFTB.setValue("");
		this.minorAgentQualityOfTB.setValue("");
		this.doingFunctionsTB.setValue("");
		this.distanceCB.setValue(false);
		this.distanceAddressTB.setValue("");
		this.discontinuousWorkTB.setValue("");
		this.intermittentCyclicalActivityTB.setValue("");
		this.durationFDTB.setValue("");
		this.activityStimationDurationFDTB.setValue("");
		this.journeyHoursFDTB.setValue("");
		this.journeyPeriodFDTB.setValue("");
		this.timeDistributionFDTB.setValue("");
		this.partialTimeLB.clear();
		this.journeyHoursTCTB.setValue("");
		this.startJourneyTCTB.setValue("");
		this.endJourneyTCTB.setValue("");
		this.journeyHoursTPTB.setValue("");
		this.agreementJourneyHoursTB.setValue("");
		this.complementaryHoursLB.clear();
		this.trialPeriodTB.setValue("");
		this.salaryAmountTB.setValue("");
		this.salaryPeriodTB.setValue("");
		this.salaryConceptTB.setValue("");
		this.holidaysTB.setValue("");
		this.sepeOfficeTB.setValue("");
		this.accreditedDisabilityTB.setValue("");
		this.withoutDisabilitySevereLB.clear();
		this.disabilitySevereLB.clear();
		this.subsidyTB.setValue("");
		this.fourthLawLB.clear();
		this.unemploymentLB.clear();
		this.unemploymentOldLB.clear();
		this.benefitsPerceptorCB.setValue(false);
		this.firstEmployeeCB.setValue(false);
		this.employeeLB.clear();
		this.agreementLineOneTB.setValue("");
		this.agreementLineTwoTB.setValue("");
		this.contactHoursLB.clear();
		this.hoursTB.setValue("");
		this.remunerationFormLB.clear();
		this.overnightAgreementLB.clear();
		this.overnightRegimeTB.setValue("");
		this.quoteReductionTCLB.clear();
		this.quoteReductionFDLB.clear();
		this.sepeOfficeCOTB.setValue("");
		
		// ------------------------------------------------------- Temporal Table
		
		this.enterpriseAgentTempTB.setValue("");
		this.enterpriseAgentNIFTempTB.setValue("");
		this.enterpriseAgentPositionTempTB.setValue("");
		this.minorAgentTempTB.setValue("");
		this.minorAgentNIFTempTB.setValue("");
		this.minorAgentQualityOfTempTB.setValue("");
		this.doingFunctionsTempTB.setValue("");
		this.distanceTempCB.setValue(false);
		this.distanceAddressTempTB.setValue("");
		this.journeyHoursTCTempTB.setValue("");
		this.startJourneyTCTempTB.setValue("");
		this.endJourneyTCTempTB.setValue("");
		this.lowJourneyTempTB.setValue("");
		this.timeDistributionTempTB.setValue("");
		this.endContractTempTB.setValue("");
		this.trialPeriodTempTB.setValue("");
		this.permitedHighDurationTempCB.setValue(false);
		this.salaryAmountTempTB.setValue("");
		this.salaryPeriodTempTB.setValue("");
		this.salaryConceptTempTB.setValue("");
		this.holidaysTempTB.setValue("");
		this.sepeOfficeTempTB.setValue("");
		this.workTempTB.setValue("");
		this.workMoreTempTB.setValue("");
		this.taskTempTB.setValue("");
		this.taskMoreTempTB.setValue("");
		this.sustituteEmployeeTempTB.setValue("");
		this.requirementsTempLB.clear();
		this.formationTempLB.clear();
		this.formationWillTempLB.clear();
		this.officeSPEmployeeTempTB.setValue("");
		this.lenguageFormationTempTB.setValue("");
		this.hoursDealTempLB.clear();
		this.presentHoursTempTB.setValue("");
		this.distributionHoursTempTB.setValue("");
		this.timeCompensationTempLB.clear();
		this.dealOvernightLB.clear();
		this.overnightRegimeTempTB.setValue("");
		this.officialOrganismTempTB.setValue("");
		this.withoutSevereDisTempLB.clear();
		this.severeDisTempLB.clear();
		this.adaptationPeriodTempTB.setValue("");
		this.adaptationConditionsTempTB.setValue("");
		this.adaptationWorkTempLB.clear();
		this.socialPersonalAdjustTempTB.setValue("");
		this.socialPersonalAdjustMoreTempTB.setValue("");
		this.colectiveAgreementTempTB.setValue("");
		
		// ------------------------------------------------------- Formation Table
		
		this.enterpriseAgentFormTB.setValue("");
		this.enterpriseAgentNIFFormTB.setValue("");
		this.enterpriseAgentPositionFormTB.setValue("");
		this.minorAgentFormTB.setValue("");
		this.minorAgentNIFFormTB.setValue("");
		this.minorAgentQualityOfFormTB.setValue("");
		this.ssReductionFormLB.clear();
		this.employeeFormLB.clear();
		this.workplaceFormTB.setValue("");
		this.tutorFormTB.setValue("");
		this.efectiveWorkHoursFormTB.setValue("");
		this.activityHoursFormTB.setValue("");
		this.trialPeriodFormTB.setValue("");
		this.agreementTrialFormCB.setValue(false);
		this.salaryAmountFormTB.setValue("");
		this.salaryPeriodFormTB.setValue("");
		this.holidaysFormTB.setValue("");
		this.degreeExistFormCB.setValue(false);
		this.degreeExist2FormCB.setValue(false);
		
		// ------------------------------------------------------- Practice Table
		
		this.enterpriseAgentPracTB.setValue("");
		this.enterpriseAgentNIFPracTB.setValue("");
		this.enterpriseAgentPositionPracTB.setValue("");
		this.minorAgentPracTB.setValue("");
		this.minorAgentNIFPracTB.setValue("");
		this.minorAgentQualityOfPracTB.setValue("");
		this.profesionalCertPracTB.setValue("");
		this.obtainingDatePracTB.setValue("");
		this.disabilityCertPracTB.setValue("");
		this.disabilityCertMorePracTB.setValue("");
		this.firstContractPracLB.clear();
		this.journeyHoursPracTB.setValue("");
		this.startJourneyPracTB.setValue("");
		this.endJourneyPracTB.setValue("");
		this.distributionJourneyPracTB.setValue("");
		this.trialPeriodPracTB.setValue("");
		this.salaryAmountPracTB.setValue("");
		this.salaryPeriodPracTB.setValue("");
		this.salaryConceptPracTB.setValue("");
		this.holidaysPracTB.setValue("");
		this.sepeComunicationPracTB.setValue("");
		this.endSepeComunicationPracTB.setValue("");
		this.unemploymentSubsidyPracLB.clear();
		this.adaptationPeriodPracTB.setValue("");
		this.adaptationConditionsPracTB.setValue("");
		this.adaptationWorkPracTB.setValue("");
		this.personalSocialAdjustPracTB.setValue("");
		this.personalSocialAdjustMorePracTB.setValue("");
		this.motivationPracLB.clear();
		this.employerPracLB.clear();
	}

	private void initializeListBox() {
		
		// ------------------------------------------------------- Indefinite Table
		
		this.partialTimeLB.addItem("-", "");
		this.partialTimeLB.addItem("NO", "DISC_AGREEMENT_COLLECTIVE_NO");
		this.partialTimeLB.addItem("SI", "DISC_AGREEMENT_COLLECTIVE_YES");
		
		this.complementaryHoursLB.addItem("-", "");
		this.complementaryHoursLB.addItem("NO", "COMPLEMENTARY_HOURS_NO");
		this.complementaryHoursLB.addItem("SI", "COMPLEMENTARY_HOURS_YES");
		
		this.withoutDisabilitySevereLB.addItem("-", "");
		this.withoutDisabilitySevereLB.addItem("Hombres menores de 45 a" + String.valueOf("\u00F1") + "os", "OPT2_DISABILITY_NO_SEVERE_MAN_LT_45");
		this.withoutDisabilitySevereLB.addItem("Hombres mayores de 45 a" + String.valueOf("\u00F1") + "os", "OPT2_DISABILITY_NO_SEVERE_MAN_GT_45");
		this.withoutDisabilitySevereLB.addItem("Mujeres menores de 45 a" + String.valueOf("\u00F1") + "os", "OPT2_DISABILITY_NO_SEVERE_WOMAN_LT_45");
		this.withoutDisabilitySevereLB.addItem("Mujeres mayores de 45 a" + String.valueOf("\u00F1") + "os", "OPT2_DISABILITY_NO_SEVERE_WOMAN_GT_45");
		
		this.disabilitySevereLB.addItem("-", "");
		this.disabilitySevereLB.addItem("Hombres menores de 45 a" + String.valueOf("\u00F1") + "os", "OPT2_DISABILITY_SEVERE_MAN_LT_45");
		this.disabilitySevereLB.addItem("Hombres mayores de 45 a" + String.valueOf("\u00F1") + "os", "OPT2_DISABILITY_SEVERE_MAN_GT_45");
		this.disabilitySevereLB.addItem("Mujeres menores de 45 a" + String.valueOf("\u00F1") + "os", "OPT2_DISABILITY_SEVERE_WOMAN_LT_45");
		this.disabilitySevereLB.addItem("Mujeres mayores de 45 a" + String.valueOf("\u00F1") + "os", "OPT2_DISABILITY_SEVERE_WOMAN_GT_45");
		
		this.fourthLawLB.addItem("-", "");
		this.fourthLawLB.addItem("NO", "OPT5_BONUS_ART4_RDL3_2012_NO");
		this.fourthLawLB.addItem("SI", "OPT5_BONUS_ART4_RDL3_2012_YES");
		
		this.unemploymentLB.addItem("-", "");
		this.unemploymentLB.addItem("J" + String.valueOf("\u00F3") + "venes", "OPT5_UNEMPLOYED_BT_16_30_JUNIOR");
		this.unemploymentLB.addItem("Mujeres en ocupaciones menos representadas", "OPT5_UNEMPLOYED_BT_16_30_FEMALE");
		
		this.unemploymentOldLB.addItem("-", "");
		this.unemploymentOldLB.addItem("Mayores de 45 a" + String.valueOf("\u00F1") + "os", "OPT5_UNEMPLOYED_GT_45_MALE");
		this.unemploymentOldLB.addItem("Mujeres en ocupaciones menos representadas", "OPT5_UNEMPLOYED_GT_45_FEMALE");
		
		this.employeeLB.addItem("-", "");
		this.employeeLB.addItem("Menor de 30 a" + String.valueOf("\u00F1") + "os", "OPT6_LT_30_EMPLOYEE");
		this.employeeLB.addItem("Menor de 35 a" + String.valueOf("\u00F1") + "os con discapacidad mayor o igual al 33%", "OPT6_LT_35_EMPLOYEE_AND_HANDICAP_GTE_33");
		
		this.contactHoursLB.addItem("-", "");
		this.contactHoursLB.addItem("NO", "OPT15_ONSITE_HOURS_NO");
		this.contactHoursLB.addItem("SI", "OPT15_ONSITE_HOURS_YES");
		
		this.remunerationFormLB.addItem("-", "");
		this.remunerationFormLB.addItem("Comp. con periodo equiv. de descanso", "OPT15_SALARY_OPT1");
		this.remunerationFormLB.addItem("Retrib. con salario no inferior a horas extras", "OPT15_SALARY_OPT2");
		this.remunerationFormLB.addItem("Cualquiera de las anteriores", "OPT15_SALARY_OPT3");
		
		this.overnightAgreementLB.addItem("-", "");
		this.overnightAgreementLB.addItem("NO", "OPT15_OVERNIGHT_NO");
		this.overnightAgreementLB.addItem("SI", "OPT15_OVERNIGHT_YES");
		
		this.quoteReductionTCLB.addItem("-", "");
		this.quoteReductionTCLB.addItem("NO", "OPT17_FULL_TIME_QUOTE_BONUS_NO");
		this.quoteReductionTCLB.addItem("SI", "OPT17_FULL_TIME_QUOTE_BONUS_YES");
		
		this.quoteReductionFDLB.addItem("-", "");
		this.quoteReductionFDLB.addItem("NO", "OPT17_DISCONT_TIME_QUOTE_BONUS_NO");
		this.quoteReductionFDLB.addItem("SI", "OPT17_DISCONT_TIME_QUOTE_BONUS_YES");
		
		// ------------------------------------------------------- Temporal Table
		
		this.requirementsTempLB.addItem("-", "");
		this.requirementsTempLB.addItem("No tener experiencia o que esta sea inferior a 3 meses", "OPT10_REQUIREMENTS_OPT1");
		this.requirementsTempLB.addItem("Proceder de otro sector de actividad en los t" + String.valueOf("\u00E9") + "rminos que se determine reglamentariamente", "OPT10_REQUIREMENTS_OPT2");
		this.requirementsTempLB.addItem("Ser desempleado inscrito ininterrumpidamente en la oficina de empleo al menos doce meses durante los los dieciocho meses anteriores a la contrataci" + String.valueOf("\u00F3") + "n", "OPT10_REQUIREMENTS_OPT3");
		this.requirementsTempLB.addItem("Carecer de t" + String.valueOf("\u00ED") + "tulo oficial de ense" + String.valueOf("\u00F1") + "anza obligatoria, de t" + String.valueOf("\u00ED") + "tulo de formaci" + String.valueOf("\u00F3") + "n profesional o certificado de profesionalidad", "OPT10_REQUIREMENTS_OPT4");
		
		this.formationTempLB.addItem("-", "");
		this.formationTempLB.addItem("Compatibilizar" + String.valueOf("\u00E1") + " el empleo con la formaci" + String.valueOf("\u00F3") + "n", "OPT10_FORMATION_OPT1");
		this.formationTempLB.addItem("Ha cursado la formaci" + String.valueOf("\u00F3") + "n en los 6 meses previos a la celebraci" + String.valueOf("\u00F3") + "n del contrato", "OPT10_FORMATION_OPT2");
		
		this.formationWillTempLB.addItem("-", "");
		this.formationWillTempLB.addItem("Formaci" + String.valueOf("\u00F3") + "n acreditable oficialmente o promovida por los Servicios P" + String.valueOf("\u00FA") + "blicos de Empleo", "OPT10_FORMATION_TYPE_OPT1");
		this.formationWillTempLB.addItem("Fromaci" + String.valueOf("\u00F3") + "n en idiomas o tecnolog" + String.valueOf("\u00ED") + "as de la informaci" + String.valueOf("\u00F3") + "n y la comunicaci" + String.valueOf("\u00F3") + "n de una duraci" + String.valueOf("\u00F3") + "n m" + String.valueOf("\u00ED") + "nima de 90 horas", "OPT10_FORMATION_TYPE_OPT2");
		
		this.hoursDealTempLB.addItem("-", "");
		this.hoursDealTempLB.addItem("NO", "OPT12_ONSITE_HOURS_NO");
		this.hoursDealTempLB.addItem("SI", "OPT12_ONSITE_HOURS_YES");
		
		this.timeCompensationTempLB.addItem("-", "");
		this.timeCompensationTempLB.addItem("Per" + String.valueOf("\u00ED") + "odos de descanso", "OPT12_SALARY_OPT1");
		this.timeCompensationTempLB.addItem("Retribuci" + String.valueOf("\u00F3") + "n con salario", "OPT12_SALARY_OPT2");
		this.timeCompensationTempLB.addItem("Cualquiera de la anteriores", "OPT12_SALARY_OPT3");
		
		this.dealOvernightLB.addItem("-", "");
		this.dealOvernightLB.addItem("NO", "OPT12_OVERNIGHT_NO");
		this.dealOvernightLB.addItem("SI", "OPT12_OVERNIGHT_YES");
		
		this.withoutSevereDisTempLB.addItem("-", "");
		this.withoutSevereDisTempLB.addItem("Hombres menores de 45 a" + String.valueOf("\u00F1") + "os", "OPT13_DISABILITY_MAN_LT_45");
		this.withoutSevereDisTempLB.addItem("Hombres mayores de 45 a" + String.valueOf("\u00F1") + "os", "OPT13_DISABILITY_MAN_GT_45");
		this.withoutSevereDisTempLB.addItem("Mujeres menores de 45 a" + String.valueOf("\u00F1") + "os", "OPT13_DISABILITY_WOMAN_LT_45");
		this.withoutSevereDisTempLB.addItem("Mujeres mayores de 45 a" + String.valueOf("\u00F1") + "os", "OPT13_DISABILITY_WOMAN_GT_45");
		
		this.severeDisTempLB.addItem("-", "");
		this.severeDisTempLB.addItem("Hombres menores de 45 a" + String.valueOf("\u00F1") + "os", "OPT13_SEVERE_DISABILITY_MAN_LT_45");
		this.severeDisTempLB.addItem("Hombres mayores de 45 a" + String.valueOf("\u00F1") + "os", "OPT13_SEVERE_DISABILITY_MAN_GT_45");
		this.severeDisTempLB.addItem("Mujeres menores de 45 a" + String.valueOf("\u00F1") + "os", "OPT13_SEVERE_DISABILITY_WOMAN_LT_45");
		this.severeDisTempLB.addItem("Mujeres mayores de 45 a" + String.valueOf("\u00F1") + "os", "OPT13_SEVERE_DISABILITY_WOMAN_GT_45");
		
		// ------------------------------------------------------- Formation Table
		
		this.ssReductionFormLB.addItem("-", "");
		this.ssReductionFormLB.addItem("NO", "QUOTE_BONUS_NO");
		this.ssReductionFormLB.addItem("SI", "QUOTE_BONUS_YES");
		
		this.employeeFormLB.addItem("-", "");
		this.employeeFormLB.addItem("Mayor de 16 y menor de 30 a" + String.valueOf("\u00F1") + "os", "EMPLOYEE_OPT1");
		this.employeeFormLB.addItem("Trabajador/a con discapacidad (sin l" + String.valueOf("\u00ED") + "mite de edad)", "EMPLOYEE_OPT2");
		this.employeeFormLB.addItem("Participantes en proyecto al amparo de lo previsto en el art. 25 1 d (ley 56/2003)", "EMPLOYEE_OPT3");
		this.employeeFormLB.addItem("Trabajador/a en situaci" + String.valueOf("\u00F3") + "n de exclusi" + String.valueOf("\u00F3") + "n social (sin l" + String.valueOf("\u00ED") + "mite de edad)", "EMPLOYEE_OPT4");
	
		// ------------------------------------------------------- Practice Table
		
		this.firstContractPracLB.addItem("-", "");
		this.firstContractPracLB.addItem("Menor de 30 a" + String.valueOf("\u00F1") + "os", "FIRST_CONTRACT_LT_30");
		this.firstContractPracLB.addItem("Menor de 35 y con grado de disc. igual o mayor a 33%", "FIRST_CONTRACT_LT_35");
		this.firstContractPracLB.addItem("Menor de 30 y realiza pr" + String.valueOf("\u00E1") + "cticas no laborables", "FIRST_CONTRACT_LT_30_RD1543_2011");
		
		this.unemploymentSubsidyPracLB.addItem("-", "");
		this.unemploymentSubsidyPracLB.addItem("Recogidos en el art. 215", "OPT3_UNEMPLOYMENT_ART_215");
		this.unemploymentSubsidyPracLB.addItem("Eventuales incl. en el R.E.A.", "OPT3_UNEMPLOYMENT_AGRARIAN_REGIME");
		
		this.motivationPracLB.addItem("-", "");
		this.motivationPracLB.addItem("Inter" + String.valueOf("\u00E9") + "s social", "OPT5_MOTIVATION_SOCIAL_INTEREST");
		this.motivationPracLB.addItem("Fomento empleo agrario", "OPT5_MOTIVATION_AGRARIAN_PROMOTION");
		
		this.employerPracLB.addItem("-", "");
		this.employerPracLB.addItem("Corporaci" + String.valueOf("\u00F3") + "n local", "OPT5_EMPLOYER_LOCAL_CORPORATION");
		this.employerPracLB.addItem(String.valueOf("\u00D3") + "rganos de la Admin. General del Estado", "OPT5_EMPLOYER_GENERAL_ADMINISTRATION");
		this.employerPracLB.addItem("Comunidad aut" + String.valueOf("\u00F3") + "noma", "OPT5_EMPLOYER_AUTON_COMMUNITY");
		this.employerPracLB.addItem("Entidad sin " + String.valueOf("\u00E1") + "nimo de lucro", "OPT5_EMPLOYER_NONPROFIT_ENTITY");
		this.employerPracLB.addItem("Universidad", "OPT5_EMPLOYER_UNIVERSITY");
		
	}

	public void showIndefiniteTable() {
		indefiniteTable.getElement().getStyle().clearDisplay();
		temporalTable.getElement().getStyle().setDisplay(Display.NONE);
		formationTable.getElement().getStyle().setDisplay(Display.NONE);
		practiceTable.getElement().getStyle().setDisplay(Display.NONE);
	}

	public void showTemporalTable() {
		temporalTable.getElement().getStyle().clearDisplay();
		indefiniteTable.getElement().getStyle().setDisplay(Display.NONE);
		formationTable.getElement().getStyle().setDisplay(Display.NONE);
		practiceTable.getElement().getStyle().setDisplay(Display.NONE);
	}

	public void showFormationTable() {
		formationTable.getElement().getStyle().clearDisplay();
		indefiniteTable.getElement().getStyle().setDisplay(Display.NONE);
		temporalTable.getElement().getStyle().setDisplay(Display.NONE);
		practiceTable.getElement().getStyle().setDisplay(Display.NONE);
	}

	public void showPracticeTable() {
		practiceTable.getElement().getStyle().clearDisplay();
		indefiniteTable.getElement().getStyle().setDisplay(Display.NONE);
		temporalTable.getElement().getStyle().setDisplay(Display.NONE);
		formationTable.getElement().getStyle().setDisplay(Display.NONE);
	}

}
