package com.esferalia.aon.gwt.payroll.client;

import java.util.Map;
import java.util.function.Consumer;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.payroll.shared.EmployeeContractInfo;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
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
	VerticalPanel indefiniteRepresentativeTable;
	
	@UiField
	VerticalPanel indefiniteTable;

	@UiField
	TextBox enterpriseAgentNameTB;
	
	@UiField
	TextBox enterpriseAgentSurnameTB;
	
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
	Button distanceB;

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
	Button partialTimeB;
	
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
	Button complementaryHoursB;
	
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
	Button fourthLawB;
	
	@UiField
	ListBox unemploymentLB;
	
	@UiField
	ListBox unemploymentOldLB;
	
	@UiField
	Button benefitsPerceptorB;
	
	@UiField
	Button firstEmployeeB;
	
	@UiField
	ListBox employeeLB;
	
	@UiField
	TextBox agreementLineOneTB;
	
	@UiField
	TextBox agreementLineTwoTB;
	
	@UiField
	Button contactHoursB;
	
	@UiField
	TextBox hoursTB;
	
	@UiField
	ListBox remunerationFormLB;
	
	@UiField
	Button overnightAgreementB;
	
	@UiField
	TextBox overnightRegimeTB;
	
	@UiField
	Button quoteReductionTCB;
	
	@UiField
	Button quoteReductionFDB;
	
	@UiField
	TextBox sepeOfficeCOTB;
	
	// -------------------------------------------------- Temporal Table
	
	@UiField
	VerticalPanel temporalRepresentativeTable;
	
	@UiField
	VerticalPanel temporalTable;
	
	@UiField
	TextBox enterpriseAgentNameTempTB;
	
	@UiField
	TextBox enterpriseAgentSurnameTempTB;
	
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
	Button distanceTempB;

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
	Button complementaryHoursTempB;
	
	@UiField
	TextBox endContractTempTB;
	
	@UiField
	TextBox trialPeriodTempTB;
	
	@UiField
	Button permitedHighDurationTempB;
	
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
	Button hoursDealTempB;
	
	@UiField
	TextBox presentHoursTempTB;
	
	@UiField
	TextBox distributionHoursTempTB;
	
	@UiField
	ListBox timeCompensationTempLB;
	
	@UiField
	Button dealOvernightB;
	
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
	VerticalPanel formationRepresentativeTable;
	
	@UiField
	VerticalPanel formationTable;
	
	@UiField
	TextBox enterpriseAgentNameFormTB;
	
	@UiField
	TextBox enterpriseAgentSurnameFormTB;
	
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
	Button ssReductionFormB;
	
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
	Button agreementTrialFormB;
	
	@UiField
	TextBox salaryAmountFormTB;
	
	@UiField
	TextBox salaryPeriodFormTB;
	
	@UiField
	TextBox holidaysFormTB;
	
	@UiField
	Button degreeExistFormB;
	
	@UiField
	Button degreeExist2FormB;

	// -------------------------------------------------- Practice Table
	
	@UiField
	VerticalPanel practiceRepresentativeTable;
	
	@UiField
	VerticalPanel practiceTable;
	
	@UiField
	TextBox enterpriseAgentNamePracTB;
	
	@UiField
	TextBox enterpriseAgentSurnamePracTB;
	
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
	
	private DomainEnterprisesServiceAsync enterprisesService = DomainEnterprisesServiceAsync.newInstance();
	private EmployeeContractInfo contractEmployeeInfo;
	private Integer contractType;
	
	// ------------------------------------------------------ Constructor ---------------------------------------------------------

	protected ContractOtherData() {
		initWidget(uiBinder.createAndBindUi(this));
		initializeView();
	}
	
	// --------------------------------------------------------- UiHandlers --------------------------------------------------------
	
	// ------------------------------------------------------- Indefinite Table
	
	@UiHandler("enterpriseAgentNameTB")
	void onEnterpriseAgentNameTBChange(ValueChangeEvent<String> event) {
		setContractOtherData("I_ENTERPRISE_DIR_STAFF_NAME", getFullName());
	}
	
	@UiHandler("enterpriseAgentSurnameTB")
	void onEnterpriseAgentSurnameTBChange(ValueChangeEvent<String> event) {
		setContractOtherData("I_ENTERPRISE_DIR_STAFF_NAME", getFullName());
	}
	
	private String getFullName() {
		String name = enterpriseAgentNameTB.getValue();
		String surname = enterpriseAgentSurnameTB.getValue();
		
		String fullName = AonStringUtils.isBlank(surname) ? null : surname;
		return AonStringUtils.isNotBlank(fullName) ? fullName + ", " + name : name;
	}

	@UiHandler("enterpriseAgentNIFTB")
	void onEnterpriseAgentNIFTBChange(ValueChangeEvent<String> event) {
		String value = enterpriseAgentNIFTB.getValue();
		setContractOtherData("I_ENTERPRISE_DIR_STAFF_NIF", value);
	}
	
	@UiHandler("enterpriseAgentPositionTB")
	void onEnterpriseAgentPositionTBChange(ValueChangeEvent<String> event) {
		String value = enterpriseAgentPositionTB.getValue();
		setContractOtherData("I_ENTERPRISE_DIR_STAFF_CHARGE", value);
	}
	
	@UiHandler("minorAgentTB")
	void onMinorAgentTBChange(ValueChangeEvent<String> event) {
		String value = minorAgentTB.getValue();
		setContractOtherData("I_LEGAL_REPRESENTATIVE_NAME", value);
	}
	
	@UiHandler("minorAgentNIFTB")
	void onMinorAgentNIFTBChange(ValueChangeEvent<String> event) {
		String value = minorAgentNIFTB.getValue();
		setContractOtherData("I_LEGAL_REPRESENTATIVE_NIF", value);
	}
	
	@UiHandler("minorAgentQualityOfTB")
	void onMinorAgentQualityOfTBChange(ValueChangeEvent<String> event) {
		String value = minorAgentQualityOfTB.getValue();
		setContractOtherData("I_LEGAL_REPRESENTATIVE_CHARGE", value);
	}
	
	@UiHandler("doingFunctionsTB")
	void onDoingFunctionsTBChange(ValueChangeEvent<String> event) {
		String value = doingFunctionsTB.getValue();
		setContractOtherData("I_FUNCTIONS", value);
	}
	
	@UiHandler("distanceB")
	void onDistanceCBChange(ClickEvent event) {
		Boolean oldValue = isActiveToggleButton(distanceB);
		Boolean value = !oldValue;
		getEnableDisableButton(distanceB, value);
		if(Boolean.TRUE.equals(value))
			setContractOtherData("I_EMPLOYEE_CONTRACT_DISTANCE", "true");
		else
			setContractOtherData("I_EMPLOYEE_CONTRACT_DISTANCE", "");
	}
	
	@UiHandler("distanceAddressTB")
	void onDistanceAddressTBChange(ValueChangeEvent<String> event) {
		String value = distanceAddressTB.getValue();
		setContractOtherData("I_EMPLOYEE_CONTRACT_DIST_ADDR", value);
	}
	
	@UiHandler("discontinuousWorkTB")
	void onDiscontinuousWorkTBChange(ValueChangeEvent<String> event) {
		String value = discontinuousWorkTB.getValue();
		setContractOtherData("I_DISC_WORK_DESCRIPTION", value);
	}
	
	@UiHandler("intermittentCyclicalActivityTB")
	void onIntermittentCyclicalActivityTBChange(ValueChangeEvent<String> event) {
		String value = intermittentCyclicalActivityTB.getValue();
		setContractOtherData("I_DISC_WORK_ACTIVITY", value);
	}
	
	@UiHandler("durationFDTB")
	void onDurationFDTBChange(ValueChangeEvent<String> event) {
		String value = durationFDTB.getValue();
		setContractOtherData("I_DISC_WORK_DURATION", value);
	}

	@UiHandler("activityStimationDurationFDTB")
	void onActivityStimationDurationFDTBChange(ValueChangeEvent<String> event) {
		String value = activityStimationDurationFDTB.getValue();
		setContractOtherData("I_DISC_WORK_ESTIMATED_DURATION", value);
	}
	
	@UiHandler("journeyHoursFDTB")
	void onJourneyHoursFDTBChange(ValueChangeEvent<String> event) {
		String value = journeyHoursFDTB.getValue();
		setContractOtherData("I_DISC_WORK_ESTIM_JOURNAL_HOURS", value);
	}
	
	@UiHandler("journeyPeriodFDTB")
	void onJourneyPeriodFDTBChange(ValueChangeEvent<String> event) {
		String value = journeyPeriodFDTB.getValue();
		setContractOtherData("I_DISC_WORK_ESTIM_JOURNAL_PERIOD", value);
	}

	@UiHandler("timeDistributionFDTB")
	void onTimeDistributionFDTBChange(ValueChangeEvent<String> event) {
		String value = timeDistributionFDTB.getValue();
		setContractOtherData("I_DISC_WORK_ESTIM_SCHEDULE", value);
	}
	
	@UiHandler("partialTimeB")
	void onPartialTimeLBChange(ClickEvent event) {
		Boolean oldValue = isActiveToggleButton(partialTimeB);
		Boolean value = !oldValue;
		getEnableDisableButton(partialTimeB, value);
		if(Boolean.TRUE.equals(value))
			setContractOtherData("I_DISC_AGREEMENT_COLLECTIVE", "DISC_AGREEMENT_COLLECTIVE_YES");
		else
			setContractOtherData("I_DISC_AGREEMENT_COLLECTIVE", "DISC_AGREEMENT_COLLECTIVE_NO");
	}
	
	@UiHandler("journeyHoursTCTB")
	void onJourneyHoursTCTBChange(ValueChangeEvent<String> event) {
		String value = journeyHoursTCTB.getValue();
		setContractOtherData("I_FULL_TIME_WEEK_HOURS", value);
	}
	
	@UiHandler("startJourneyTCTB")
	void onStartJourneyTCTBChange(ValueChangeEvent<String> event) {
		String value = startJourneyTCTB.getValue();
		setContractOtherData("I_FULL_TIME_START_TIME", value);
	}
	
	@UiHandler("endJourneyTCTB")
	void onEndJourneyTCTBChange(ValueChangeEvent<String> event) {
		String value = endJourneyTCTB.getValue();
		setContractOtherData("I_FULL_TIME_END_TIME", value);
	}
	
	@UiHandler("journeyHoursTPTB")
	void onJourneyHoursTPTBChange(ValueChangeEvent<String> event) {
		String value = journeyHoursTPTB.getValue();
		setContractOtherData("I_PARTIALLY_TIME_HOURS", value);
	}
	
	@UiHandler("agreementJourneyHoursTB")
	void onAgreementJourneyHoursTBChange(ValueChangeEvent<String> event) {
		String value = agreementJourneyHoursTB.getValue();
		setContractOtherData("I_DEFAULT_JOURNAL_HOURS", value);
	}
	
	@UiHandler("complementaryHoursB")
	void onComplementaryHoursLBChange(ClickEvent event) {
		Boolean oldValue = isActiveToggleButton(complementaryHoursB);
		Boolean value = !oldValue;
		getEnableDisableButton(complementaryHoursB, value);
		if(Boolean.TRUE.equals(value))
			setContractOtherData("I_COMPLEMENTARY_HOURS", "COMPLEMENTARY_HOURS_YES");
		else
			setContractOtherData("I_COMPLEMENTARY_HOURS", "COMPLEMENTARY_HOURS_NO");
	}
	
	@UiHandler("trialPeriodTB")
	void onTrialPeriodTBChange(ValueChangeEvent<String> event) {
		String value = trialPeriodTB.getValue();
		setContractOtherData("I_TRIAL_DURATION", value);
	}
	
	@UiHandler("salaryAmountTB")
	void onSalaryAmountTBChange(ValueChangeEvent<String> event) {
		String value = salaryAmountTB.getValue();
		setContractOtherData("I_SALARY_AMOUNT", value);
	}
	
	@UiHandler("salaryPeriodTB")
	void onSalaryPeriodTBChange(ValueChangeEvent<String> event) {
		String value = salaryPeriodTB.getValue();
		setContractOtherData("I_SALARY_PERIOD", value);
	}
	
	@UiHandler("salaryConceptTB")
	void onSalaryConceptTBChange(ValueChangeEvent<String> event) {
		String value = salaryConceptTB.getValue();
		setContractOtherData("I_SALARY_CONCEPT", value);
	}
	
	@UiHandler("holidaysTB")
	void onHolidaysTBChange(ValueChangeEvent<String> event) {
		String value = holidaysTB.getValue();
		setContractOtherData("I_HOLIDAYS", value);
	}
	
	@UiHandler("sepeOfficeTB")
	void onSepeOfficeTBChange(ValueChangeEvent<String> event) {
		String value = sepeOfficeTB.getValue();
		setContractOtherData("I_SEPE_MUNICIPALITY", value);
	}
	
	@UiHandler("accreditedDisabilityTB")
	void onAccreditedDisabilityTBChange(ValueChangeEvent<String> event) {
		String value = accreditedDisabilityTB.getValue();
		setContractOtherData("I_OPT2_SEPE_MUNICIPALITY", value);
	}
	
	@UiHandler("withoutDisabilitySevereLB")
	void onWithoutDisabilitySevereLBChange(ChangeEvent event) {
		String selectedValue = withoutDisabilitySevereLB.getSelectedValue();
		setContractOtherData("I_OPT2_DISABILITY_NO_SEVERE", selectedValue);
	}

	@UiHandler("disabilitySevereLB")
	void onDisabilitySevereLBChange(ChangeEvent event) {
		String selectedValue = disabilitySevereLB.getSelectedValue();
		setContractOtherData("I_OPT2_DISABILITY_SEVERE", selectedValue);
	}

	@UiHandler("subsidyTB")
	void onSubsidyTBChange(ValueChangeEvent<String> event) {
		String value = subsidyTB.getValue();
		setContractOtherData("I_OPT2_REDUCTION", value);
	}
	
	@UiHandler("fourthLawB")
	void onFourthLawLBChange(ClickEvent event) {
		Boolean oldValue = isActiveToggleButton(fourthLawB);
		Boolean value = !oldValue;
		getEnableDisableButton(fourthLawB, value);
		if(Boolean.TRUE.equals(value))
			setContractOtherData("I_OPT5_BONUS_ART4_RDL3_2012", "OPT5_BONUS_ART4_RDL3_2012_YES");
		else
			setContractOtherData("I_OPT5_BONUS_ART4_RDL3_2012", "OPT5_BONUS_ART4_RDL3_2012_NO");
	}
	
	@UiHandler("unemploymentLB")
	void onUnemploymentLBChange(ChangeEvent event) {
		String selectedValue = unemploymentLB.getSelectedValue();
		setContractOtherData("I_OPT5_UNEMPLOYED_BT_16_30", selectedValue);
	}

	@UiHandler("unemploymentOldLB")
	void onUnemploymentOldLBChange(ChangeEvent event) {
		String selectedValue = unemploymentOldLB.getSelectedValue();
		setContractOtherData("I_OPT5_UNEMPLOYED_GT_45", selectedValue);
	}
	
	@UiHandler("benefitsPerceptorB")
	void onBenefitsPerceptorCBChange(ClickEvent event) {
		Boolean oldValue = isActiveToggleButton(benefitsPerceptorB);
		Boolean value = !oldValue;
		getEnableDisableButton(benefitsPerceptorB, value);
		if(Boolean.TRUE.equals(value))
			setContractOtherData("I_OPT5_UNEMPL_3_MONTH_BENEFIT", "true");
		else
			setContractOtherData("I_OPT5_UNEMPL_3_MONTH_BENEFIT", "");
	}
	
	@UiHandler("firstEmployeeB")
	void onFirstEmployeeCBChange(ClickEvent event) {
		Boolean oldValue = isActiveToggleButton(firstEmployeeB);
		Boolean value = !oldValue;
		getEnableDisableButton(firstEmployeeB, value);
		if(Boolean.TRUE.equals(value))
			setContractOtherData("I_OPT5_FIRST_EMPLOYEE_AND_LT_30", "true");
		else
			setContractOtherData("I_OPT5_FIRST_EMPLOYEE_AND_LT_30", "");
	}
	
	@UiHandler("employeeLB")
	void onEmployeeLBChange(ChangeEvent event) {
		String selectedValue = employeeLB.getSelectedValue();
		setContractOtherData("I_OPT6_AGE", selectedValue);
	}
	
	@UiHandler("agreementLineOneTB")
	void onAgreementLineOneTBChange(ValueChangeEvent<String> event) {
		String value = agreementLineOneTB.getValue();
		setContractOtherData("I_OPT6_AGREEMENT_COLLECTIVE1", value);
	}
	
	@UiHandler("agreementLineTwoTB")
	void onAgreementLineTwoTBChange(ValueChangeEvent<String> event) {
		String value = agreementLineTwoTB.getValue();
		setContractOtherData("I_OPT6_AGREEMENT_COLLECTIVE2", value);
	}
	
	@UiHandler("contactHoursB")
	void onContactHoursLBChange(ClickEvent event) {
		Boolean oldValue = isActiveToggleButton(contactHoursB);
		Boolean value = !oldValue;
		getEnableDisableButton(contactHoursB, value);
		if(Boolean.TRUE.equals(value))
			setContractOtherData("I_OPT15_ONSITE_HOURS", "OPT15_ONSITE_HOURS_YES");
		else
			setContractOtherData("I_OPT15_ONSITE_HOURS", "OPT15_ONSITE_HOURS_NO");
	}

	@UiHandler("hoursTB")
	void onHoursTBChange(ValueChangeEvent<String> event) {
		String value = hoursTB.getValue();
		setContractOtherData("I_OPT15_ONSITE_WEEK_HOURS", value);
	}

	@UiHandler("remunerationFormLB")
	void onRemunerationFormLBChange(ChangeEvent event) {
		String selectedValue = remunerationFormLB.getSelectedValue();
		setContractOtherData("I_OPT15_SALARY", selectedValue);
	}
	
	@UiHandler("overnightAgreementB")
	void onOvernightAgreementLBChange(ClickEvent event) {
		Boolean oldValue = isActiveToggleButton(overnightAgreementB);
		Boolean value = !oldValue;
		getEnableDisableButton(overnightAgreementB, value);
		if(Boolean.TRUE.equals(value))
			setContractOtherData("I_OPT15_OVERNIGHT", "OPT15_OVERNIGHT_YES");
		else
			setContractOtherData("I_OPT15_OVERNIGHT", "OPT15_OVERNIGHT_NO");
	}
	
	@UiHandler("overnightRegimeTB")
	void onOvernightRegimeTBChange(ValueChangeEvent<String> event) {
		String value = overnightRegimeTB.getValue();
		setContractOtherData("I_OPT15_OVERNIGHT_WEEK_DAYS", value);
	}
	
	@UiHandler("quoteReductionTCB")
	void onQuoteReductionTCLBChange(ClickEvent event) {
		Boolean oldValue = isActiveToggleButton(quoteReductionTCB);
		Boolean value = !oldValue;
		getEnableDisableButton(quoteReductionTCB, value);
		if(Boolean.TRUE.equals(value))
			setContractOtherData("I_OPT17_FULL_TIME_QUOTE_BONUS", "OPT17_FULL_TIME_QUOTE_BONUS_YES");
		else
			setContractOtherData("I_OPT17_FULL_TIME_QUOTE_BONUS", "OPT17_FULL_TIME_QUOTE_BONUS_NO");
	}
	
	@UiHandler("quoteReductionFDB")
	void onQuoteReductionFDLBChange(ClickEvent event) {
		Boolean oldValue = isActiveToggleButton(quoteReductionFDB);
		Boolean value = !oldValue;
		getEnableDisableButton(quoteReductionFDB, value);
		if(Boolean.TRUE.equals(value))
			setContractOtherData("I_OPT17_DISCONT_TIME_QUOTE_BONUS", "OPT17_DISCONT_TIME_QUOTE_BONUS_YES");
		else
			setContractOtherData("I_OPT17_DISCONT_TIME_QUOTE_BONUS", "OPT17_DISCONT_TIME_QUOTE_BONUS_NO");
	}
	
	@UiHandler("sepeOfficeCOTB")
	void onSepeOfficeCOTBChange(ValueChangeEvent<String> event) {
		String value = sepeOfficeCOTB.getValue();
		setContractOtherData("I_OPT17_SRC_CONTRACT_SEPE_MUNIC", value);
	}
	
	// ------------------------------------------------------- Temporal Table
	
	@UiHandler("enterpriseAgentNameTempTB")
	void onEnterpriseAgentNameTempTBChange(ValueChangeEvent<String> event) {
		setContractOtherData("T_ENTERPRISE_DIR_STAFF_NAME", getFullNameTmp());
	}
	
	@UiHandler("enterpriseAgentSurnameTempTB")
	void onEnterpriseAgentSurnameTempTBChange(ValueChangeEvent<String> event) {
		setContractOtherData("T_ENTERPRISE_DIR_STAFF_NAME", getFullNameTmp());
	}
	
	private String getFullNameTmp() {
		String name = enterpriseAgentNameTempTB.getValue();
		String surname = enterpriseAgentSurnameTempTB.getValue();
		
		String fullName = AonStringUtils.isBlank(surname) ? null : surname;
		return AonStringUtils.isNotBlank(fullName) ? fullName + ", " + name : name;
	}
	
	@UiHandler("enterpriseAgentNIFTempTB")
	void onEnterpriseAgentNIFTempTBChange(ValueChangeEvent<String> event) {
		String value = enterpriseAgentNIFTempTB.getValue();
		setContractOtherData("T_ENTERPRISE_DIR_STAFF_NIF", value);
	}
	
	@UiHandler("enterpriseAgentPositionTempTB")
	void onEnterpriseAgentPositionTempTBChange(ValueChangeEvent<String> event) {
		String value = enterpriseAgentPositionTempTB.getValue();
		setContractOtherData("T_ENTERPRISE_DIR_STAFF_CHARGE", value);
	}
	
	@UiHandler("minorAgentTempTB")
	void onMinorAgentTempTBChange(ValueChangeEvent<String> event) {
		String value = minorAgentTempTB.getValue();
		setContractOtherData("T_LEGAL_REPRESENTATIVE_NAME", value);
	}
	
	@UiHandler("minorAgentNIFTempTB")
	void onMinorAgentNIFTempTBChange(ValueChangeEvent<String> event) {
		String value = minorAgentNIFTempTB.getValue();
		setContractOtherData("T_LEGAL_REPRESENTATIVE_NIF", value);
	}
	
	@UiHandler("minorAgentQualityOfTempTB")
	void onMinorAgentQualityOfTempTBChange(ValueChangeEvent<String> event) {
		String value = minorAgentQualityOfTempTB.getValue();
		setContractOtherData("T_LEGAL_REPRESENTATIVE_CHARGE", value);
	}
	
	@UiHandler("doingFunctionsTempTB")
	void onDoingFunctionsTempTBChange(ValueChangeEvent<String> event) {
		String value = doingFunctionsTempTB.getValue();
		setContractOtherData("T_FUNCTIONS", value);
	}
	
	@UiHandler("distanceTempB")
	void onDistanceTempCBChange(ClickEvent event) {
		Boolean oldValue = isActiveToggleButton(distanceTempB);
		Boolean value = !oldValue;
		getEnableDisableButton(distanceTempB, value);
		if(Boolean.TRUE.equals(value))
			setContractOtherData("T_EMPLOYEE_CONTRACT_DISTANCE", "true");
		else
			setContractOtherData("T_EMPLOYEE_CONTRACT_DISTANCE", "");
	}
	
	@UiHandler("distanceAddressTempTB")
	void onDistanceAddressTempTBChange(ValueChangeEvent<String> event) {
		String value = distanceAddressTempTB.getValue();
		setContractOtherData("T_EMPLOYEE_CONTRACT_DIST_ADDR", value);
	}
	
	@UiHandler("journeyHoursTCTempTB")
	void onJourneyHoursTCTempTBChange(ValueChangeEvent<String> event) {
		String value = journeyHoursTCTempTB.getValue();
		setContractOtherData("T_FULL_TIME_WEEK_HOURS", value);
	}
	
	@UiHandler("startJourneyTCTempTB")
	void onStartJourneyTCTempTBChange(ValueChangeEvent<String> event) {
		String value = startJourneyTCTempTB.getValue();
		setContractOtherData("T_FULL_TIME_START_TIME", value);
	}
	
	@UiHandler("endJourneyTCTempTB")
	void onEndJourneyTCTempTBChange(ValueChangeEvent<String> event) {
		String value = endJourneyTCTempTB.getValue();
		setContractOtherData("T_FULL_TIME_END_TIME", value);
	}
	
	@UiHandler("lowJourneyTempTB")
	void onLowJourneyTempTBChange(ValueChangeEvent<String> event) {
		String value = lowJourneyTempTB.getValue();
		setContractOtherData("T_PARTIALLY_TIME_JOB_LOWER_THAN", value);
	}
	
	@UiHandler("timeDistributionTempTB")
	void onTimeDistributionTempTBChange(ValueChangeEvent<String> event) {
		String value = timeDistributionTempTB.getValue();
		setContractOtherData("T_PARTIALLY_TIME_JOB_DISTRIB", value);
	}
	
	@UiHandler("complementaryHoursTempB")
	void onComplementaryHoursTempBClick(ClickEvent event) {
		Boolean oldValue = isActiveToggleButton(complementaryHoursTempB);
		Boolean value = !oldValue;
		getEnableDisableButton(complementaryHoursTempB, value);
		if(Boolean.TRUE.equals(value))
			setContractOtherData("T_COMPLEMENTARY_HOURS", "COMPLEMENTARY_HOURS_YES");
		else
			setContractOtherData("T_COMPLEMENTARY_HOURS", "COMPLEMENTARY_HOURS_NO");
	}
	
	@UiHandler("endContractTempTB")
	void onEndContractTempTBChange(ValueChangeEvent<String> event) {
		String value = endContractTempTB.getValue();
		setContractOtherData("T_END_DATE_TEXT", value);
	}
	
	@UiHandler("trialPeriodTempTB")
	void onTrialPeriodTempTBChange(ValueChangeEvent<String> event) {
		String value = trialPeriodTempTB.getValue();
		setContractOtherData("T_TRIAL_DURATION", value);
	}
	
	@UiHandler("permitedHighDurationTempB")
	void onPermitedHighDurationTempCBChange(ClickEvent event) {
		Boolean oldValue = isActiveToggleButton(permitedHighDurationTempB);
		Boolean value = !oldValue;
		getEnableDisableButton(permitedHighDurationTempB, value);
		if(Boolean.TRUE.equals(value))
			setContractOtherData("T_GREATER_DURATION_AGREEMENT_COL", "true");
		else
			setContractOtherData("T_GREATER_DURATION_AGREEMENT_COL", "");
	}
	
	@UiHandler("salaryAmountTempTB")
	void onSalaryAmountTempTBChange(ValueChangeEvent<String> event) {
		String value = salaryAmountTempTB.getValue();
		setContractOtherData("T_SALARY_AMOUNT", value);
	}
	
	@UiHandler("salaryPeriodTempTB")
	void onSalaryPeriodTempTBChange(ValueChangeEvent<String> event) {
		String value = salaryPeriodTempTB.getValue();
		setContractOtherData("T_SALARY_PERIOD", value);
	}
	
	@UiHandler("salaryConceptTempTB")
	void onSalaryConceptTempTBChange(ValueChangeEvent<String> event) {
		String value = salaryConceptTempTB.getValue();
		setContractOtherData("T_SALARY_CONCEPT", value);
	}
	
	@UiHandler("holidaysTempTB")
	void onHolidaysTempTBChange(ValueChangeEvent<String> event) {
		String value = holidaysTempTB.getValue();
		setContractOtherData("T_HOLIDAYS", value);
	}
	
	@UiHandler("sepeOfficeTempTB")
	void onSepeOfficeTempTBChange(ValueChangeEvent<String> event) {
		String value = sepeOfficeTempTB.getValue();
		setContractOtherData("T_SEPE_MUNICIPALITY", value);
	}
	
	@UiHandler("workTempTB")
	void onWorkTempTBChange(ValueChangeEvent<String> event) {
		String value = workTempTB.getValue();
		setContractOtherData("T_OPT1_WORK_DESCRIPTION1", value);
	}
	
	@UiHandler("workMoreTempTB")
	void onWorkMoreTempTBChange(ValueChangeEvent<String> event) {
		String value = workMoreTempTB.getValue();
		setContractOtherData("T_OPT1_WORK_DESCRIPTION2", value);
	}
	
	@UiHandler("taskTempTB")
	void onTaskTempTBChange(ValueChangeEvent<String> event) {
		String value = taskTempTB.getValue();
		setContractOtherData("T_OPT2_WORK_DESCRIPTION1", value);
	}
	
	@UiHandler("taskMoreTempTB")
	void onTaskMoreTempTBChange(ValueChangeEvent<String> event) {
		String value = taskMoreTempTB.getValue();
		setContractOtherData("T_OPT2_WORK_DESCRIPTION2", value);
	}
	
	@UiHandler("sustituteEmployeeTempTB")
	void onSustituteEmployeeTempTBChange(ValueChangeEvent<String> event) {
		String value = sustituteEmployeeTempTB.getValue();
		setContractOtherData("T_OPT3_REPLACED_WORKER_NAME", value);
	}
	
	@UiHandler("requirementsTempLB")
	void onRequirementsTempLBChange(ChangeEvent event) {
		String selectedValue = requirementsTempLB.getSelectedValue();
		setContractOtherData("T_OPT10_REQUIREMENTS_OPT", selectedValue);
	}
	
	@UiHandler("formationTempLB")
	void onFormationTempLBChange(ChangeEvent event) {
		String selectedValue = formationTempLB.getSelectedValue();
		setContractOtherData("T_OPT10_FORMATION_OPT", selectedValue);
	}
	
	@UiHandler("formationWillTempLB")
	void onFormationWillTempLBChange(ChangeEvent event) {
		String selectedValue = formationWillTempLB.getSelectedValue();
		setContractOtherData("T_OPT10_FORMATION_TYPE_OPT", selectedValue);
	}
	
	@UiHandler("officeSPEmployeeTempTB")
	void onOfficeSPEmployeeTempTBChange(ValueChangeEvent<String> event) {
		String value = officeSPEmployeeTempTB.getValue();
		setContractOtherData("T_OPT10_FORMATION_TYPE_OPT1_TEXT", value);
	}
	
	@UiHandler("lenguageFormationTempTB")
	void onLenguageFormationTempTBChange(ValueChangeEvent<String> event) {
		String value = lenguageFormationTempTB.getValue();
		setContractOtherData("T_OPT10_FORMATION_TYPE_OPT2_TEXT", value);
	}
	
	@UiHandler("hoursDealTempB")
	void onHoursDealTempLBChange(ClickEvent event) {
		Boolean oldValue = isActiveToggleButton(hoursDealTempB);
		Boolean value = !oldValue;
		getEnableDisableButton(hoursDealTempB, value);
		if(Boolean.TRUE.equals(value))
			setContractOtherData("T_OPT12_ONSITE_HOURS", "OPT12_ONSITE_HOURS_YES");
		else
			setContractOtherData("T_OPT12_ONSITE_HOURS", "OPT12_ONSITE_HOURS_NO");
	}
	
	@UiHandler("presentHoursTempTB")
	void onPresentHoursTempTBChange(ValueChangeEvent<String> event) {
		String value = presentHoursTempTB.getValue();
		setContractOtherData("T_OPT12_ONSITE_WEEK_HOURS", value);
	}
	
	@UiHandler("distributionHoursTempTB")
	void onDistributionHoursTempTBChange(ValueChangeEvent<String> event) {
		String value = distributionHoursTempTB.getValue();
		setContractOtherData("T_OPT12_ONSITE_HOURS_DISTRIB", value);
	}
	
	@UiHandler("timeCompensationTempLB")
	void onTimeCompensationTempLBChange(ChangeEvent event) {
		String selectedValue = timeCompensationTempLB.getSelectedValue();
		setContractOtherData("T_OPT12_SALARY_OPT", selectedValue);
	}
	
	@UiHandler("dealOvernightB")
	void onDealOvernightLBChange(ClickEvent event) {
		Boolean oldValue = isActiveToggleButton(dealOvernightB);
		Boolean value = !oldValue;
		getEnableDisableButton(dealOvernightB, value);
		if(Boolean.TRUE.equals(value))
			setContractOtherData("T_OPT12_OVERNIGHT", "OPT12_OVERNIGHT_YES");
		else
			setContractOtherData("T_OPT12_OVERNIGHT", "OPT12_OVERNIGHT_NO");
	}
	
	@UiHandler("overnightRegimeTempTB")
	void onOvernightRegimeTempTBChange(ValueChangeEvent<String> event) {
		String value = overnightRegimeTempTB.getValue();
		setContractOtherData("T_OPT12_OVERNIGHT_WEEK_DAYS", value);
	}
	
	@UiHandler("officialOrganismTempTB")
	void onOfficialOrganismTempTBChange(ValueChangeEvent<String> event) {
		String value = officialOrganismTempTB.getValue();
		setContractOtherData("T_OPT13_DISABILITY_ISSUED_BY", value);
	}
	
	@UiHandler("withoutSevereDisTempLB")
	void onWithoutSevereDisTempLBChange(ChangeEvent event) {
		String selectedValue = withoutSevereDisTempLB.getSelectedValue();
		setContractOtherData("T_OPT13_DISABILITY", selectedValue);
	}
	
	@UiHandler("severeDisTempLB")
	void onSevereDisTempLBChange(ChangeEvent event) {
		String selectedValue = severeDisTempLB.getSelectedValue();
		setContractOtherData("T_OPT13_SEVERE_DISABILITY", selectedValue);
	}
	
	@UiHandler("adaptationPeriodTempTB")
	void onAdaptationPeriodTempTBChange(ValueChangeEvent<String> event) {
		String value = adaptationPeriodTempTB.getValue();
		setContractOtherData("T_OPT14_TRIAL_PERIOD", value);
	}
	
	@UiHandler("adaptationConditionsTempTB")
	void onAdaptationConditionsTempTBChange(ValueChangeEvent<String> event) {
		String value = adaptationConditionsTempTB.getValue();
		setContractOtherData("T_OPT14_TRIAL_TERMS", value);
	}
	
	@UiHandler("adaptationWorkTempLB")
	void onAdaptationWorkTempLBChange(ChangeEvent event) {
		String selectedValue = adaptationWorkTempLB.getSelectedValue();
		setContractOtherData("T_OPT14_PROFESSION", selectedValue);
	}
	
	@UiHandler("socialPersonalAdjustTempTB")
	void onSocialPersonalAdjustTempTBChange(ValueChangeEvent<String> event) {
		String value = socialPersonalAdjustTempTB.getValue();
		setContractOtherData("T_OPT14_DISTANCE_ADJUSTMENT", value);
	}
	
	@UiHandler("socialPersonalAdjustMoreTempTB")
	void onSocialPersonalAdjustMoreTempTBChange(ValueChangeEvent<String> event) {
		String value = socialPersonalAdjustMoreTempTB.getValue();
		setContractOtherData("T_OPT14_DISTANCE_ADJUSTMENT_MORE", value);
	}
	
	@UiHandler("colectiveAgreementTempTB")
	void onColectiveAgreementTempTBChange(ValueChangeEvent<String> event) {
		String value = colectiveAgreementTempTB.getValue();
		setContractOtherData("T_OPT14_COLLECTIVE_AGREEMENT", value);
	}
	
	// ------------------------------------------------------- Formation Table
	
	@UiHandler("enterpriseAgentNameFormTB")
	void onEnterpriseAgentNameFormTBChange(ValueChangeEvent<String> event) {
		setContractOtherData("L_ENTERPRISE_DIR_STAFF_NAME", getFullNameForm());
	}
	
	@UiHandler("enterpriseAgentSurnameFormTB")
	void onEnterpriseAgentSurnameFormTBChange(ValueChangeEvent<String> event) {
		setContractOtherData("L_ENTERPRISE_DIR_STAFF_NAME", getFullNameForm());
	}
	
	private String getFullNameForm() {
		String name = enterpriseAgentNameFormTB.getValue();
		String surname = enterpriseAgentSurnameFormTB.getValue();
		
		String fullName = AonStringUtils.isBlank(surname) ? null : surname;
		return AonStringUtils.isNotBlank(fullName) ? fullName + ", " + name : name;
	}
	
	@UiHandler("enterpriseAgentNIFFormTB")
	void onEnterpriseAgentNIFFormTBChange(ValueChangeEvent<String> event) {
		String value = enterpriseAgentNIFFormTB.getValue();
		setContractOtherData("L_ENTERPRISE_DIR_STAFF_NIF", value);
	}
	
	@UiHandler("enterpriseAgentPositionFormTB")
	void onEnterpriseAgentPositionFormTBChange(ValueChangeEvent<String> event) {
		String value = enterpriseAgentPositionFormTB.getValue();
		setContractOtherData("L_ENTERPRISE_DIR_STAFF_CHARGE", value);
	}
	
	@UiHandler("minorAgentFormTB")
	void onMinorAgentFormTBChange(ValueChangeEvent<String> event) {
		String value = minorAgentFormTB.getValue();
		setContractOtherData("L_LEGAL_REPRESENTATIVE_NAME", value);
	}
	
	@UiHandler("minorAgentNIFFormTB")
	void onMinorAgentNIFFormTBChange(ValueChangeEvent<String> event) {
		String value = minorAgentNIFFormTB.getValue();
		setContractOtherData("L_LEGAL_REPRESENTATIVE_NIF", value);
	}
	
	@UiHandler("minorAgentQualityOfFormTB")
	void onMinorAgentQualityOfFormTBChange(ValueChangeEvent<String> event) {
		String value = minorAgentQualityOfFormTB.getValue();
		setContractOtherData("L_LEGAL_REPRESENTATIVE_CHARGE", value);
	}
	
	@UiHandler("ssReductionFormB")
	void onSsReductionFormLBChange(ClickEvent event) {
		Boolean oldValue = isActiveToggleButton(ssReductionFormB);
		Boolean value = !oldValue;
		getEnableDisableButton(ssReductionFormB, value);
		if(Boolean.TRUE.equals(value))
			setContractOtherData("L_QUOTE_BONUS", "QUOTE_BONUS_YES");
		else
			setContractOtherData("L_QUOTE_BONUS", "QUOTE_BONUS_NO");
	}
	
	@UiHandler("employeeFormLB")
	void onEmployeeFormLBChange(ChangeEvent event) {
		String selectedValue = employeeFormLB.getSelectedValue();
		setContractOtherData("L_EMPLOYEE_OPT", selectedValue);
	}
	
	@UiHandler("workplaceFormTB")
	void onWorkplaceFormTBChange(ValueChangeEvent<String> event) {
		String value = workplaceFormTB.getValue();
		setContractOtherData("L_CONTRACT_WORKPLACE_ADDRESS", value);
	}
	
	@UiHandler("tutorFormTB")
	void onTutorFormTBChange(ValueChangeEvent<String> event) {
		String value = tutorFormTB.getValue();
		setContractOtherData("L_FORMATION_TEACHER", value);
	}
	
	@UiHandler("efectiveWorkHoursFormTB")
	void onEfectiveWorkHoursFormTBChange(ValueChangeEvent<String> event) {
		String value = efectiveWorkHoursFormTB.getValue();
		setContractOtherData("L_HORARIO_LABORAL", value);
	}
	
	@UiHandler("activityHoursFormTB")
	void onActivityHoursFormTBChange(ValueChangeEvent<String> event) {
		String value = activityHoursFormTB.getValue();
		setContractOtherData("L_HORARIO_LECTIVO", value);
	}
	
	@UiHandler("trialPeriodFormTB")
	void onTrialPeriodFormTBChange(ValueChangeEvent<String> event) {
		String value = trialPeriodFormTB.getValue();
		setContractOtherData("L_TRIAL_DURATION", value);
	}
	
	@UiHandler("agreementTrialFormB")
	void onAgreementTrialFormCBChange(ClickEvent event) {
		Boolean oldValue = isActiveToggleButton(agreementTrialFormB);
		Boolean value = !oldValue;
		getEnableDisableButton(agreementTrialFormB, value);
		if(Boolean.TRUE.equals(value))
			setContractOtherData("L_TRIAL_DURATION_INCREASE", "true");
		else
			setContractOtherData("L_TRIAL_DURATION_INCREASE", "");
	}
	
	@UiHandler("salaryAmountFormTB")
	void onSalaryAmountFormTBChange(ValueChangeEvent<String> event) {
		String value = salaryAmountFormTB.getValue();
		setContractOtherData("L_SALARY_AMOUNT", value);
	}
	
	@UiHandler("salaryPeriodFormTB")
	void onSalaryPeriodFormTBChange(ValueChangeEvent<String> event) {
		String value = salaryPeriodFormTB.getValue();
		setContractOtherData("L_SALARY_PERIOD", value);
	}
	
	@UiHandler("holidaysFormTB")
	void onHolidaysFormTBChange(ValueChangeEvent<String> event) {
		String value = holidaysFormTB.getValue();
		setContractOtherData("L_HOLIDAYS", value);
	}
	
	@UiHandler("degreeExistFormB")
	void onDegreeExistFormCBChange(ClickEvent event) {
		Boolean oldValue = isActiveToggleButton(degreeExistFormB);
		Boolean value = !oldValue;
		getEnableDisableButton(degreeExistFormB, value);
		if(Boolean.TRUE.equals(value))
			setContractOtherData("L_ANNEX_I_CHECK", "true");
		else
			setContractOtherData("L_ANNEX_I_CHECK", "");
	}
	
	@UiHandler("degreeExist2FormB")
	void onDegreeExist2FormCBChange(ClickEvent event) {
		Boolean oldValue = isActiveToggleButton(degreeExist2FormB);
		Boolean value = !oldValue;
		getEnableDisableButton(degreeExist2FormB, value);
		if(Boolean.TRUE.equals(value))
			setContractOtherData("L_ANNEX_II_CHECK", "true");
		else
			setContractOtherData("L_ANNEX_II_CHECK", "");
	}
	
	// ------------------------------------------------------- Practice Table
	
	@UiHandler("enterpriseAgentNamePracTB")
	void onEnterpriseAgentNamePracTBChange(ValueChangeEvent<String> event) {
		setContractOtherData("P_ENTERPRISE_DIR_STAFF_NAME", getFullNamePrac());
	}
	
	@UiHandler("enterpriseAgentSurnamePracTB")
	void onEnterpriseAgentSurnamePracTBChange(ValueChangeEvent<String> event) {
		setContractOtherData("P_ENTERPRISE_DIR_STAFF_NAME", getFullNamePrac());
	}
	
	private String getFullNamePrac() {
		String name = enterpriseAgentNamePracTB.getValue();
		String surname = enterpriseAgentSurnamePracTB.getValue();
		
		String fullName = AonStringUtils.isBlank(surname) ? null : surname;
		return AonStringUtils.isNotBlank(fullName) ? fullName + ", " + name : name;
	}
	
	@UiHandler("enterpriseAgentNIFPracTB")
	void onEnterpriseAgentNIFPracTBChange(ValueChangeEvent<String> event) {
		String value = enterpriseAgentNIFPracTB.getValue();
		setContractOtherData("P_ENTERPRISE_DIR_STAFF_NIF", value);
	}
	
	@UiHandler("enterpriseAgentPositionPracTB")
	void onEnterpriseAgentPositionPracTBChange(ValueChangeEvent<String> event) {
		String value = enterpriseAgentPositionPracTB.getValue();
		setContractOtherData("P_ENTERPRISE_DIR_STAFF_CHARGE", value);
	}
	
	@UiHandler("minorAgentPracTB")
	void onMinorAgentPracTBChange(ValueChangeEvent<String> event) {
		String value = minorAgentPracTB.getValue();
		setContractOtherData("P_LEGAL_REPRESENTATIVE_NAME", value);
	}
	
	@UiHandler("minorAgentNIFPracTB")
	void onMinorAgentNIFPracTBChange(ValueChangeEvent<String> event) {
		String value = minorAgentNIFPracTB.getValue();
		setContractOtherData("P_LEGAL_REPRESENTATIVE_NIF", value);
	}
	
	@UiHandler("minorAgentQualityOfPracTB")
	void onMinorAgentQualityOfPracTBChange(ValueChangeEvent<String> event) {
		String value = minorAgentQualityOfPracTB.getValue();
		setContractOtherData("P_LEGAL_REPRESENTATIVE_CHARGE", value);
	}
	
	@UiHandler("profesionalCertPracTB")
	void onProfesionalCertPracTBChange(ValueChangeEvent<String> event) {
		String value = profesionalCertPracTB.getValue();
		setContractOtherData("P_PROFESSIONAL_CERT", value);
	}
	
	@UiHandler("obtainingDatePracTB")
	void onObtainingDatePracTBChange(ValueChangeEvent<String> event) {
		String value = obtainingDatePracTB.getValue();
		setContractOtherData("P_PROFESSIONAL_CERT_OBTAIN_DATE", value);
	}
	
	@UiHandler("disabilityCertPracTB")
	void onDisabilityCertPracTBChange(ValueChangeEvent<String> event) {
		String value = disabilityCertPracTB.getValue();
		setContractOtherData("P_DISABILITY_ISSUE_ENTITY", value);
	}
	
	@UiHandler("disabilityCertMorePracTB")
	void onDisabilityCertMorePracTBChange(ValueChangeEvent<String> event) {
		String value = disabilityCertMorePracTB.getValue();
		setContractOtherData("P_DISABILITY_ISSUE_ENTITY_MORE", value);
	}
	
	@UiHandler("firstContractPracLB")
	void onFirstContractPracLBChange(ChangeEvent event) {
		String selectedValue = firstContractPracLB.getSelectedValue();
		setContractOtherData("P_FIRST_CONTRACT", selectedValue);
	}
	
	@UiHandler("journeyHoursPracTB")
	void onJourneyHoursPracTBChange(ValueChangeEvent<String> event) {
		String value = journeyHoursPracTB.getValue();
		setContractOtherData("P_FULL_TIME_WEEK_HOURS", value);
	}
	
	@UiHandler("startJourneyPracTB")
	void onStartJourneyPracTBChange(ValueChangeEvent<String> event) {
		String value = startJourneyPracTB.getValue();
		setContractOtherData("P_FULL_TIME_START_TIME", value);
	}
	
	@UiHandler("endJourneyPracTB")
	void onEndJourneyPracTBChange(ValueChangeEvent<String> event) {
		String value = endJourneyPracTB.getValue();
		setContractOtherData("P_FULL_TIME_END_TIME", value);
	}
	
	@UiHandler("distributionJourneyPracTB")
	void onDistributionJourneyPracTBChange(ValueChangeEvent<String> event) {
		String value = distributionJourneyPracTB.getValue();
		setContractOtherData("P_JOB_TIME_DISTRIBUTION2", value);
	}
	
	@UiHandler("trialPeriodPracTB")
	void onTrialPeriodPracTBChange(ValueChangeEvent<String> event) {
		String value = trialPeriodPracTB.getValue();
		setContractOtherData("P_TRIAL_DURATION", value);
	}
	
	@UiHandler("salaryAmountPracTB")
	void onSalaryAmountPracTBChange(ValueChangeEvent<String> event) {
		String value = salaryAmountPracTB.getValue();
		setContractOtherData("P_SALARY_AMOUNT", value);
	}
	
	@UiHandler("salaryPeriodPracTB")
	void onSalaryPeriodPracTBChange(ValueChangeEvent<String> event) {
		String value = salaryPeriodPracTB.getValue();
		setContractOtherData("P_SALARY_PERIOD", value);
	}
	
	@UiHandler("salaryConceptPracTB")
	void onSalaryConceptPracTBChange(ValueChangeEvent<String> event) {
		String value = salaryConceptPracTB.getValue();
		setContractOtherData("P_SALARY_CONCEPT", value);
	}
	
	@UiHandler("holidaysPracTB")
	void onHolidaysPracTBChange(ValueChangeEvent<String> event) {
		String value = holidaysPracTB.getValue();
		setContractOtherData("P_HOLIDAYS", value);
	}
	
	@UiHandler("sepeComunicationPracTB")
	void onSepeComunicationPracTBChange(ValueChangeEvent<String> event) {
		String value = sepeComunicationPracTB.getValue();
		setContractOtherData("P_SEPE_START_COMMUNICATION", value);
	}
	
	@UiHandler("endSepeComunicationPracTB")
	void onEndSepeComunicationPracTBChange(ValueChangeEvent<String> event) {
		String value = endSepeComunicationPracTB.getValue();
		setContractOtherData("P_SEPE_END_COMMUNICATION", value);
	}
	
	@UiHandler("unemploymentSubsidyPracLB")
	void onUnemploymentSubsidyPracLBChange(ChangeEvent event) {
		String selectedValue = unemploymentSubsidyPracLB.getSelectedValue();
		setContractOtherData("P_OPT3_UNEMPLOYMENT", selectedValue);
	}
	
	@UiHandler("adaptationPeriodPracTB")
	void onAdaptationPeriodPracTBChange(ValueChangeEvent<String> event) {
		String value = adaptationPeriodPracTB.getValue();
		setContractOtherData("P_OPT4_TRIAL_DURATION", value);
	}
	
	@UiHandler("adaptationConditionsPracTB")
	void onAdaptationConditionsPracTBChange(ValueChangeEvent<String> event) {
		String value = adaptationConditionsPracTB.getValue();
		setContractOtherData("P_OPT4_TRIAL_DURATION_CONDITIONS", value);
	}
	
	@UiHandler("adaptationWorkPracTB")
	void onAdaptationWorkPracTBChange(ValueChangeEvent<String> event) {
		String value = adaptationWorkPracTB.getValue();
		setContractOtherData("P_OPT4_WORK_PLACE_ADAPTATIONS", value);
	}
	
	@UiHandler("personalSocialAdjustPracTB")
	void onPersonalSocialAdjustPracTBChange(ValueChangeEvent<String> event) {
		String value = personalSocialAdjustPracTB.getValue();
		setContractOtherData("P_OPT4_STAFF_ADJUSTMENT", value);
	}
	
	@UiHandler("personalSocialAdjustMorePracTB")
	void onPersonalSocialAdjustMorePracTBChange(ValueChangeEvent<String> event) {
		String value = personalSocialAdjustMorePracTB.getValue();
		setContractOtherData("P_OPT4_STAFF_ADJUSTMENT_MORE", value);
	}
	
	@UiHandler("motivationPracLB")
	void onMotivationPracLBChange(ChangeEvent event) {
		String selectedValue = motivationPracLB.getSelectedValue();
		setContractOtherData("P_OPT5_MOTIVATION", selectedValue);
	}
	
	@UiHandler("employerPracLB")
	void onEmployerPracLBChange(ChangeEvent event) {
		String selectedValue = employerPracLB.getSelectedValue();
		setContractOtherData("P_OPT5_EMPLOYER", selectedValue);
	}

	// ------------------------------------------------------------------------
	//							Class Methods
	// ------------------------------------------------------------------------

	private void initializeView() {
		resetElements();
		initializeListBox();
	}

	public void resetElements() {
		
		// ------------------------------------------------------- Indefinite Table
		
		this.enterpriseAgentNameTB.setValue("");
		this.enterpriseAgentSurnameTB.setValue("");
		this.enterpriseAgentNIFTB.setValue("");
		this.enterpriseAgentPositionTB.setValue("");
		this.minorAgentTB.setValue("");
		this.minorAgentNIFTB.setValue("");
		this.minorAgentQualityOfTB.setValue("");
		this.doingFunctionsTB.setValue("");
		getEnableDisableButton(this.distanceB, false);
		this.distanceAddressTB.setValue("");
		this.discontinuousWorkTB.setValue("");
		this.intermittentCyclicalActivityTB.setValue("");
		this.durationFDTB.setValue("");
		this.activityStimationDurationFDTB.setValue("");
		this.journeyHoursFDTB.setValue("");
		this.journeyPeriodFDTB.setValue("");
		this.timeDistributionFDTB.setValue("");
		getEnableDisableButton(this.partialTimeB, false);
		this.journeyHoursTCTB.setValue("");
		this.startJourneyTCTB.setValue("");
		this.endJourneyTCTB.setValue("");
		this.journeyHoursTPTB.setValue("");
		this.agreementJourneyHoursTB.setValue("");
		getEnableDisableButton(this.complementaryHoursB, false);
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
		getEnableDisableButton(this.fourthLawB, false);
		this.unemploymentLB.clear();
		this.unemploymentOldLB.clear();
		getEnableDisableButton(this.benefitsPerceptorB, false);
		getEnableDisableButton(this.firstEmployeeB, false);
		this.employeeLB.clear();
		this.agreementLineOneTB.setValue("");
		this.agreementLineTwoTB.setValue("");
		getEnableDisableButton(this.contactHoursB, false);
		this.hoursTB.setValue("");
		this.remunerationFormLB.clear();
		getEnableDisableButton(this.overnightAgreementB, false);
		this.overnightRegimeTB.setValue("");
		getEnableDisableButton(this.quoteReductionTCB, false);
		getEnableDisableButton(this.quoteReductionFDB, false);
		this.sepeOfficeCOTB.setValue("");
		
		// ------------------------------------------------------- Temporal Table
		
		this.enterpriseAgentNameTempTB.setValue("");
		this.enterpriseAgentSurnameTempTB.setValue("");
		this.enterpriseAgentNIFTempTB.setValue("");
		this.enterpriseAgentPositionTempTB.setValue("");
		this.minorAgentTempTB.setValue("");
		this.minorAgentNIFTempTB.setValue("");
		this.minorAgentQualityOfTempTB.setValue("");
		this.doingFunctionsTempTB.setValue("");
		getEnableDisableButton(this.distanceTempB, false);
		this.distanceAddressTempTB.setValue("");
		this.journeyHoursTCTempTB.setValue("");
		this.startJourneyTCTempTB.setValue("");
		this.endJourneyTCTempTB.setValue("");
		this.lowJourneyTempTB.setValue("");
		this.timeDistributionTempTB.setValue("");
		getEnableDisableButton(this.complementaryHoursTempB, false);
		this.endContractTempTB.setValue("");
		this.trialPeriodTempTB.setValue("");
		getEnableDisableButton(this.permitedHighDurationTempB, false);
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
		getEnableDisableButton(this.hoursDealTempB, false);
		this.presentHoursTempTB.setValue("");
		this.distributionHoursTempTB.setValue("");
		this.timeCompensationTempLB.clear();
		getEnableDisableButton(this.dealOvernightB, false);
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
		
		this.enterpriseAgentNameFormTB.setValue("");
		this.enterpriseAgentSurnameFormTB.setValue("");
		this.enterpriseAgentNIFFormTB.setValue("");
		this.enterpriseAgentPositionFormTB.setValue("");
		this.minorAgentFormTB.setValue("");
		this.minorAgentNIFFormTB.setValue("");
		this.minorAgentQualityOfFormTB.setValue("");
		getEnableDisableButton(this.ssReductionFormB, false);
		this.employeeFormLB.clear();
		this.workplaceFormTB.setValue("");
		this.tutorFormTB.setValue("");
		this.efectiveWorkHoursFormTB.setValue("");
		this.activityHoursFormTB.setValue("");
		this.trialPeriodFormTB.setValue("");
		getEnableDisableButton(this.agreementTrialFormB, false);
		this.salaryAmountFormTB.setValue("");
		this.salaryPeriodFormTB.setValue("");
		this.holidaysFormTB.setValue("");
		getEnableDisableButton(this.degreeExistFormB, false);
		getEnableDisableButton(this.degreeExist2FormB, false);
		
		// ------------------------------------------------------- Practice Table
		
		this.enterpriseAgentNamePracTB.setValue("");
		this.enterpriseAgentSurnamePracTB.setValue("");
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
		
		this.withoutDisabilitySevereLB.addItem("-", "");
		this.withoutDisabilitySevereLB.addItem("Hombres menores de 45 a\u00F1os", "OPT2_DISABILITY_NO_SEVERE_MAN_LT_45");
		this.withoutDisabilitySevereLB.addItem("Hombres mayores de 45 a\u00F1os", "OPT2_DISABILITY_NO_SEVERE_MAN_GT_45");
		this.withoutDisabilitySevereLB.addItem("Mujeres menores de 45 a\u00F1os", "OPT2_DISABILITY_NO_SEVERE_WOMAN_LT_45");
		this.withoutDisabilitySevereLB.addItem("Mujeres mayores de 45 a\u00F1os", "OPT2_DISABILITY_NO_SEVERE_WOMAN_GT_45");
		
		this.disabilitySevereLB.addItem("-", "");
		this.disabilitySevereLB.addItem("Hombres menores de 45 a\u00F1os", "OPT2_DISABILITY_SEVERE_MAN_LT_45");
		this.disabilitySevereLB.addItem("Hombres mayores de 45 a\u00F1os", "OPT2_DISABILITY_SEVERE_MAN_GT_45");
		this.disabilitySevereLB.addItem("Mujeres menores de 45 a\u00F1os", "OPT2_DISABILITY_SEVERE_WOMAN_LT_45");
		this.disabilitySevereLB.addItem("Mujeres mayores de 45 a\u00F1os", "OPT2_DISABILITY_SEVERE_WOMAN_GT_45");
		
		this.unemploymentLB.addItem("-", "");
		this.unemploymentLB.addItem("J\u00F3venes", "OPT5_UNEMPLOYED_BT_16_30_JUNIOR");
		this.unemploymentLB.addItem("Mujeres en ocupaciones menos representadas", "OPT5_UNEMPLOYED_BT_16_30_FEMALE");
		
		this.unemploymentOldLB.addItem("-", "");
		this.unemploymentOldLB.addItem("Mayores de 45 a\u00F1os", "OPT5_UNEMPLOYED_GT_45_MALE");
		this.unemploymentOldLB.addItem("Mujeres en ocupaciones menos representadas", "OPT5_UNEMPLOYED_GT_45_FEMALE");
		
		this.employeeLB.addItem("-", "");
		this.employeeLB.addItem("Menor de 30 a\u00F1os", "OPT6_LT_30_EMPLOYEE");
		this.employeeLB.addItem("Menor de 35 a\u00F1os con discapacidad mayor o igual al 33%", "OPT6_LT_35_EMPLOYEE_AND_HANDICAP_GTE_33");
		
		this.remunerationFormLB.addItem("-", "");
		this.remunerationFormLB.addItem("Comp. con periodo equiv. de descanso", "OPT15_SALARY_OPT1");
		this.remunerationFormLB.addItem("Retrib. con salario no inferior a horas extras", "OPT15_SALARY_OPT2");
		this.remunerationFormLB.addItem("Cualquiera de las anteriores", "OPT15_SALARY_OPT3");
		
		// ------------------------------------------------------- Temporal Table
		
		this.requirementsTempLB.addItem("-", "");
		this.requirementsTempLB.addItem("No tener experiencia o que esta sea inferior a 3 meses", "OPT10_REQUIREMENTS_OPT1");
		this.requirementsTempLB.addItem("Proceder de otro sector de actividad en los t\u00E9rminos que se determine reglamentariamente", "OPT10_REQUIREMENTS_OPT2");
		this.requirementsTempLB.addItem("Ser desempleado inscrito ininterrumpidamente en la oficina de empleo al menos doce meses durante los los dieciocho meses anteriores a la contrataci\u00F3n", "OPT10_REQUIREMENTS_OPT3");
		this.requirementsTempLB.addItem("Carecer de t\u00EDtulo oficial de ense\u00F1anza obligatoria, de t\u00EDtulo de formaci\u00F3n profesional o certificado de profesionalidad", "OPT10_REQUIREMENTS_OPT4");
		
		this.formationTempLB.addItem("-", "");
		this.formationTempLB.addItem("Compatibilizar\u00E1 el empleo con la formaci\u00F3n", "OPT10_FORMATION_OPT1");
		this.formationTempLB.addItem("Ha cursado la formaci\u00F3n en los 6 meses previos a la celebraci\u00F3n del contrato", "OPT10_FORMATION_OPT2");
		
		this.formationWillTempLB.addItem("-", "");
		this.formationWillTempLB.addItem("Formaci\u00F3n acreditable oficialmente o promovida por los Servicios P\u00FAblicos de Empleo", "OPT10_FORMATION_TYPE_OPT1");
		this.formationWillTempLB.addItem("Fromaci\u00F3n en idiomas o tecnolog\u00EDas de la informaci\u00F3n y la comunicaci\u00F3n de una duraci\u00F3n m\u00EDnima de 90 horas", "OPT10_FORMATION_TYPE_OPT2");
		
		this.timeCompensationTempLB.addItem("-", "");
		this.timeCompensationTempLB.addItem("Per\u00EDodos de descanso", "OPT12_SALARY_OPT1");
		this.timeCompensationTempLB.addItem("Retribuci\u00F3n con salario", "OPT12_SALARY_OPT2");
		this.timeCompensationTempLB.addItem("Cualquiera de la anteriores", "OPT12_SALARY_OPT3");
		
		this.withoutSevereDisTempLB.addItem("-", "");
		this.withoutSevereDisTempLB.addItem("Hombres menores de 45 a\u00F1os", "OPT13_DISABILITY_MAN_LT_45");
		this.withoutSevereDisTempLB.addItem("Hombres mayores de 45 a\u00F1os", "OPT13_DISABILITY_MAN_GT_45");
		this.withoutSevereDisTempLB.addItem("Mujeres menores de 45 a\u00F1os", "OPT13_DISABILITY_WOMAN_LT_45");
		this.withoutSevereDisTempLB.addItem("Mujeres mayores de 45 a\u00F1os", "OPT13_DISABILITY_WOMAN_GT_45");
		
		this.severeDisTempLB.addItem("-", "");
		this.severeDisTempLB.addItem("Hombres menores de 45 a\u00F1os", "OPT13_SEVERE_DISABILITY_MAN_LT_45");
		this.severeDisTempLB.addItem("Hombres mayores de 45 a\u00F1os", "OPT13_SEVERE_DISABILITY_MAN_GT_45");
		this.severeDisTempLB.addItem("Mujeres menores de 45 a\u00F1os", "OPT13_SEVERE_DISABILITY_WOMAN_LT_45");
		this.severeDisTempLB.addItem("Mujeres mayores de 45 a\u00F1os", "OPT13_SEVERE_DISABILITY_WOMAN_GT_45");
		
		// ------------------------------------------------------- Formation Table
		
		this.employeeFormLB.addItem("-", "");
		this.employeeFormLB.addItem("Mayor de 16 y menor de 30 a\u00F1os", "EMPLOYEE_OPT1");
		this.employeeFormLB.addItem("Trabajador/a con discapacidad (sin l\u00EDmite de edad)", "EMPLOYEE_OPT2");
		this.employeeFormLB.addItem("Participantes en proyecto al amparo de lo previsto en el art. 25 1 d (ley 56/2003)", "EMPLOYEE_OPT3");
		this.employeeFormLB.addItem("Trabajador/a en situaci\u00F3n de exclusi\u00F3n social (sin l\u00EDmite de edad)", "EMPLOYEE_OPT4");
	
		// ------------------------------------------------------- Practice Table
		
		this.firstContractPracLB.addItem("-", "");
		this.firstContractPracLB.addItem("Menor de 30 a\u00F1os", "FIRST_CONTRACT_LT_30");
		this.firstContractPracLB.addItem("Menor de 35 y con grado de disc. igual o mayor a 33%", "FIRST_CONTRACT_LT_35");
		this.firstContractPracLB.addItem("Menor de 30 y realiza pr\u00E1cticas no laborables", "FIRST_CONTRACT_LT_30_RD1543_2011");
		
		this.unemploymentSubsidyPracLB.addItem("-", "");
		this.unemploymentSubsidyPracLB.addItem("Recogidos en el art. 215", "OPT3_UNEMPLOYMENT_ART_215");
		this.unemploymentSubsidyPracLB.addItem("Eventuales incl. en el R.E.A.", "OPT3_UNEMPLOYMENT_AGRARIAN_REGIME");
		
		this.motivationPracLB.addItem("-", "");
		this.motivationPracLB.addItem("Inter\u00E9s social", "OPT5_MOTIVATION_SOCIAL_INTEREST");
		this.motivationPracLB.addItem("Fomento empleo agrario", "OPT5_MOTIVATION_AGRARIAN_PROMOTION");
		
		this.employerPracLB.addItem("-", "");
		this.employerPracLB.addItem("Corporaci\u00F3n local", "OPT5_EMPLOYER_LOCAL_CORPORATION");
		this.employerPracLB.addItem("\u00D3rganos de la Admin. General del Estado", "OPT5_EMPLOYER_GENERAL_ADMINISTRATION");
		this.employerPracLB.addItem("Comunidad aut\u00F3noma", "OPT5_EMPLOYER_AUTON_COMMUNITY");
		this.employerPracLB.addItem("Entidad sin \u00E1nimo de lucro", "OPT5_EMPLOYER_NONPROFIT_ENTITY");
		this.employerPracLB.addItem("Universidad", "OPT5_EMPLOYER_UNIVERSITY");
		
	}

	public void showIndefiniteTable() {
		indefiniteRepresentativeTable.getElement().getStyle().clearDisplay();
		indefiniteTable.getElement().getStyle().clearDisplay();
		temporalRepresentativeTable.getElement().getStyle().setDisplay(Display.NONE);
		temporalTable.getElement().getStyle().setDisplay(Display.NONE);
		formationRepresentativeTable.getElement().getStyle().setDisplay(Display.NONE);
		formationTable.getElement().getStyle().setDisplay(Display.NONE);
		practiceRepresentativeTable.getElement().getStyle().setDisplay(Display.NONE);
		practiceTable.getElement().getStyle().setDisplay(Display.NONE);
	}

	public void showTemporalTable() {
		temporalRepresentativeTable.getElement().getStyle().clearDisplay();
		temporalTable.getElement().getStyle().clearDisplay();
		indefiniteRepresentativeTable.getElement().getStyle().setDisplay(Display.NONE);
		indefiniteTable.getElement().getStyle().setDisplay(Display.NONE);
		formationRepresentativeTable.getElement().getStyle().setDisplay(Display.NONE);
		formationTable.getElement().getStyle().setDisplay(Display.NONE);
		practiceRepresentativeTable.getElement().getStyle().setDisplay(Display.NONE);
		practiceTable.getElement().getStyle().setDisplay(Display.NONE);
	}

	public void showFormationTable() {
		formationRepresentativeTable.getElement().getStyle().clearDisplay();
		formationTable.getElement().getStyle().clearDisplay();
		indefiniteRepresentativeTable.getElement().getStyle().setDisplay(Display.NONE);
		indefiniteTable.getElement().getStyle().setDisplay(Display.NONE);
		temporalRepresentativeTable.getElement().getStyle().setDisplay(Display.NONE);
		temporalTable.getElement().getStyle().setDisplay(Display.NONE);
		practiceRepresentativeTable.getElement().getStyle().setDisplay(Display.NONE);
		practiceTable.getElement().getStyle().setDisplay(Display.NONE);
	}

	public void showPracticeTable() {
		practiceRepresentativeTable.getElement().getStyle().clearDisplay();
		practiceTable.getElement().getStyle().clearDisplay();
		indefiniteRepresentativeTable.getElement().getStyle().setDisplay(Display.NONE);
		indefiniteTable.getElement().getStyle().setDisplay(Display.NONE);
		temporalRepresentativeTable.getElement().getStyle().setDisplay(Display.NONE);
		temporalTable.getElement().getStyle().setDisplay(Display.NONE);
		formationRepresentativeTable.getElement().getStyle().setDisplay(Display.NONE);
		formationTable.getElement().getStyle().setDisplay(Display.NONE);
	}
	
	private void setContractOtherData(String name, String value) {
		this.contractEmployeeInfo.addContractOtherData(name, value);
	}
	
	public String getContractOtherData(String name) {
		return this.contractEmployeeInfo.getContractOtherData().get(name);
	}
	
	public Boolean getContractOtherDataCB(String name) {
		String value = this.contractEmployeeInfo.getContractOtherData().get(name);
		return null != value;
	}

	public void setEmployeeContractInfo(EmployeeContractInfo contractEmployeeInfo) {
		this.contractEmployeeInfo = contractEmployeeInfo;
		this.contractType = getContractType(contractEmployeeInfo.getContractInfo().getContractType());
		reloadOtherData();
	}
	
	private void reloadOtherData() {
		getContractOtherDataDB(s -> fillContractOtherData(this.contractType), f -> showErrorMessage("Error obtenci\u00f3n Otros Datos", f.getMessage()));
	}
	
	public Integer getContractType(String contractTypeStr) {
		if(null == contractTypeStr)
			return -1;
		else
			return Integer.parseInt(contractTypeStr);
	}
	
	private void fillContractOtherData(Integer contractType) {
		if(contractType >= 100 && contractType <= 400) {
			showIndefiniteTable();
			fillContractOtherData();
		} else if (contractType == 421) {
			showFormationTable();
			fillContractOtherDataFormation();
		} else if (contractType == 420 || contractType == 520) {
			showPracticeTable();
			fillContractOtherDataPractice();
		} else {
			showTemporalTable();
			fillContractOtherDataTemp();
		}
	}
	
	private String getEnterpriseAgentName(String fullName) {
		return AonStringUtils.isNotBlank(fullName) && AonStringUtils.containsIgnoreCase(fullName, ",") ? fullName.split(",")[1].trim() : null;
	}
	
	private String getEnterpriseAgentSurname(String fullName) {
		return AonStringUtils.isNotBlank(fullName) && AonStringUtils.containsIgnoreCase(fullName, ",") ? fullName.split(",")[0].trim() : fullName;
	}

	private void fillContractOtherData() {
		enterpriseAgentNameTB.setValue(getEnterpriseAgentName(getContractOtherData("I_ENTERPRISE_DIR_STAFF_NAME")));
		enterpriseAgentSurnameTB.setValue(getEnterpriseAgentSurname(getContractOtherData("I_ENTERPRISE_DIR_STAFF_NAME")));
		enterpriseAgentNIFTB.setValue(getContractOtherData("I_ENTERPRISE_DIR_STAFF_NIF"));
		enterpriseAgentPositionTB.setValue(getContractOtherData("I_ENTERPRISE_DIR_STAFF_CHARGE"));
		minorAgentTB.setValue(getContractOtherData("I_LEGAL_REPRESENTATIVE_NAME"));
		minorAgentNIFTB.setValue(getContractOtherData("I_LEGAL_REPRESENTATIVE_NIF"));
		minorAgentQualityOfTB.setValue(getContractOtherData("I_LEGAL_REPRESENTATIVE_CHARGE"));
		doingFunctionsTB.setValue(getContractOtherData("I_FUNCTIONS"));
		getEnableDisableButton(distanceB, getContractOtherDataCB("I_EMPLOYEE_CONTRACT_DISTANCE"));
		distanceAddressTB.setValue(getContractOtherData("I_EMPLOYEE_CONTRACT_DIST_ADDR"));
		discontinuousWorkTB.setValue(getContractOtherData("I_DISC_WORK_DESCRIPTION"));
		intermittentCyclicalActivityTB.setValue(getContractOtherData("I_DISC_WORK_ACTIVITY"));
		durationFDTB.setValue(getContractOtherData("I_DISC_WORK_DURATION"));
		activityStimationDurationFDTB.setValue(getContractOtherData("I_DISC_WORK_ESTIMATED_DURATION"));
		journeyHoursFDTB.setValue(getContractOtherData("I_DISC_WORK_ESTIM_JOURNAL_HOURS"));
		journeyPeriodFDTB.setValue(getContractOtherData("I_DISC_WORK_ESTIM_JOURNAL_PERIOD"));
		timeDistributionFDTB.setValue(getContractOtherData("I_DISC_WORK_ESTIM_SCHEDULE"));
		getEnableDisableButton(partialTimeB, AonStringUtils.containsIgnoreCase(getContractOtherData("I_DISC_AGREEMENT_COLLECTIVE"), "YES"));
		journeyHoursTCTB.setValue(getContractOtherData("I_FULL_TIME_WEEK_HOURS"));
		startJourneyTCTB.setValue(getContractOtherData("I_FULL_TIME_START_TIME"));
		endJourneyTCTB.setValue(getContractOtherData("I_FULL_TIME_END_TIME"));
		journeyHoursTPTB.setValue(getContractOtherData("I_PARTIALLY_TIME_HOURS"));
		agreementJourneyHoursTB.setValue(getContractOtherData("I_DEFAULT_JOURNAL_HOURS"));
		getEnableDisableButton(complementaryHoursB, AonStringUtils.containsIgnoreCase(getContractOtherData("I_COMPLEMENTARY_HOURS"), "YES"));
		trialPeriodTB.setValue(getContractOtherData("I_TRIAL_DURATION"));
		salaryAmountTB.setValue(getContractOtherData("I_SALARY_AMOUNT"));
		salaryPeriodTB.setValue(getContractOtherData("I_SALARY_PERIOD"));
		salaryConceptTB.setValue(getContractOtherData("I_SALARY_CONCEPT"));
		holidaysTB.setValue(getContractOtherData("I_HOLIDAYS"));
		sepeOfficeTB.setValue(getContractOtherData("I_SEPE_MUNICIPALITY"));
		accreditedDisabilityTB.setValue(getContractOtherData("I_OPT2_SEPE_MUNICIPALITY"));
		setSelectedValueLB(withoutDisabilitySevereLB, getContractOtherData("I_OPT2_DISABILITY_NO_SEVERE"));
		setSelectedValueLB(disabilitySevereLB, getContractOtherData("I_OPT2_DISABILITY_SEVERE"));
		subsidyTB.setValue(getContractOtherData("I_OPT2_REDUCTION"));
		getEnableDisableButton(fourthLawB, AonStringUtils.containsIgnoreCase(getContractOtherData("I_OPT5_BONUS_ART4_RDL3_2012"), "YES"));
		setSelectedValueLB(unemploymentLB, getContractOtherData("I_OPT5_UNEMPLOYED_BT_16_30"));
		setSelectedValueLB(unemploymentOldLB, getContractOtherData("I_OPT5_UNEMPLOYED_GT_45"));
		getEnableDisableButton(benefitsPerceptorB, getContractOtherDataCB("I_OPT5_UNEMPL_3_MONTH_BENEFIT"));
		getEnableDisableButton(firstEmployeeB, getContractOtherDataCB("I_OPT5_FIRST_EMPLOYEE_AND_LT_30"));
		setSelectedValueLB(employeeLB, getContractOtherData("I_OPT6_AGE"));
		agreementLineOneTB.setValue(getContractOtherData("I_OPT6_AGREEMENT_COLLECTIVE1"));
		agreementLineTwoTB.setValue(getContractOtherData("I_OPT6_AGREEMENT_COLLECTIVE2"));
		getEnableDisableButton(contactHoursB, AonStringUtils.containsIgnoreCase(getContractOtherData("I_OPT15_ONSITE_HOURS"), "YES"));
		hoursTB.setValue(getContractOtherData("I_OPT15_ONSITE_WEEK_HOURS"));
		setSelectedValueLB(remunerationFormLB, getContractOtherData("I_OPT15_SALARY"));
		getEnableDisableButton(overnightAgreementB, AonStringUtils.containsIgnoreCase(getContractOtherData("I_OPT15_OVERNIGHT"), "YES"));
		overnightRegimeTB.setValue(getContractOtherData("I_OPT15_OVERNIGHT_WEEK_DAYS"));
		getEnableDisableButton(quoteReductionTCB, AonStringUtils.containsIgnoreCase(getContractOtherData("I_OPT17_FULL_TIME_QUOTE_BONUS"), "YES"));
		getEnableDisableButton(quoteReductionFDB, AonStringUtils.containsIgnoreCase(getContractOtherData("I_OPT17_DISCONT_TIME_QUOTE_BONUS"), "YES"));
		sepeOfficeCOTB.setValue(getContractOtherData("I_OPT17_SRC_CONTRACT_SEPE_MUNIC"));
	}
	
	private void fillContractOtherDataTemp() {
		enterpriseAgentNameTempTB.setValue(getEnterpriseAgentName(getContractOtherData("T_ENTERPRISE_DIR_STAFF_NAME")));
		enterpriseAgentSurnameTempTB.setValue(getEnterpriseAgentSurname(getContractOtherData("T_ENTERPRISE_DIR_STAFF_NAME")));
		enterpriseAgentNIFTempTB.setValue(getContractOtherData("T_ENTERPRISE_DIR_STAFF_NIF"));
		enterpriseAgentPositionTempTB.setValue(getContractOtherData("T_ENTERPRISE_DIR_STAFF_CHARGE"));
		minorAgentTempTB.setValue(getContractOtherData("T_LEGAL_REPRESENTATIVE_NAME"));
		minorAgentNIFTempTB.setValue(getContractOtherData("T_LEGAL_REPRESENTATIVE_NIF"));
		minorAgentQualityOfTempTB.setValue(getContractOtherData("T_LEGAL_REPRESENTATIVE_CHARGE"));
		doingFunctionsTempTB.setValue(getContractOtherData("T_FUNCTIONS"));
		getEnableDisableButton(distanceTempB, getContractOtherDataCB("T_EMPLOYEE_CONTRACT_DISTANCE"));
		distanceAddressTempTB.setValue(getContractOtherData("T_EMPLOYEE_CONTRACT_DIST_ADDR"));
		journeyHoursTCTempTB.setValue(getContractOtherData("T_FULL_TIME_WEEK_HOURS"));
		startJourneyTCTempTB.setValue(getContractOtherData("T_FULL_TIME_START_TIME"));
		endJourneyTCTempTB.setValue(getContractOtherData("T_FULL_TIME_END_TIME"));
		lowJourneyTempTB.setValue(getContractOtherData("T_PARTIALLY_TIME_JOB_LOWER_THAN"));
		timeDistributionTempTB.setValue(getContractOtherData("T_PARTIALLY_TIME_JOB_DISTRIB"));
		getEnableDisableButton(complementaryHoursTempB, getContractOtherDataCB("T_COMPLEMENTARY_HOURS"));
		endContractTempTB.setValue(getContractOtherData("T_END_DATE_TEXT"));
		trialPeriodTempTB.setValue(getContractOtherData("T_TRIAL_DURATION"));
		getEnableDisableButton(permitedHighDurationTempB, getContractOtherDataCB("T_GREATER_DURATION_AGREEMENT_COL"));
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
		setSelectedValueLB(requirementsTempLB, getContractOtherData("T_OPT10_REQUIREMENTS_OPT"));
		setSelectedValueLB(formationTempLB, getContractOtherData("T_OPT10_FORMATION_OPT"));
		setSelectedValueLB(formationWillTempLB, getContractOtherData("T_OPT10_FORMATION_TYPE_OPT"));
		officeSPEmployeeTempTB.setValue(getContractOtherData("T_OPT10_FORMATION_TYPE_OPT1_TEXT"));
		lenguageFormationTempTB.setValue(getContractOtherData("T_OPT10_FORMATION_TYPE_OPT2_TEXT"));
		getEnableDisableButton(hoursDealTempB, AonStringUtils.containsIgnoreCase(getContractOtherData("T_OPT12_ONSITE_HOURS"), "YES"));
		presentHoursTempTB.setValue(getContractOtherData("T_OPT12_ONSITE_WEEK_HOURS"));
		distributionHoursTempTB.setValue(getContractOtherData("T_OPT12_ONSITE_HOURS_DISTRIB"));
		setSelectedValueLB(timeCompensationTempLB, getContractOtherData("T_OPT12_SALARY_OPT"));
		getEnableDisableButton(dealOvernightB, AonStringUtils.containsIgnoreCase(getContractOtherData("T_OPT12_OVERNIGHT"), "YES"));
		overnightRegimeTempTB.setValue(getContractOtherData("T_OPT12_OVERNIGHT_WEEK_DAYS"));
		officialOrganismTempTB.setValue(getContractOtherData("T_OPT13_DISABILITY_ISSUED_BY"));
		setSelectedValueLB(withoutSevereDisTempLB, getContractOtherData("T_OPT13_DISABILITY"));
		setSelectedValueLB(severeDisTempLB, getContractOtherData("T_OPT13_SEVERE_DISABILITY"));
		adaptationPeriodTempTB.setValue(getContractOtherData("T_OPT14_TRIAL_PERIOD"));
		adaptationConditionsTempTB.setValue(getContractOtherData("T_OPT14_TRIAL_TERMS"));
		setSelectedValueLB(adaptationWorkTempLB, getContractOtherData("T_OPT14_PROFESSION"));
		socialPersonalAdjustTempTB.setValue(getContractOtherData("T_OPT14_DISTANCE_ADJUSTMENT"));
		socialPersonalAdjustMoreTempTB.setValue(getContractOtherData("T_OPT14_DISTANCE_ADJUSTMENT_MORE"));
		colectiveAgreementTempTB.setValue(getContractOtherData("T_OPT14_COLLECTIVE_AGREEMENT"));
	}
	
	private void fillContractOtherDataFormation() {
		enterpriseAgentNameFormTB.setValue(getEnterpriseAgentName(getContractOtherData("L_ENTERPRISE_DIR_STAFF_NAME")));
		enterpriseAgentSurnameFormTB.setValue(getEnterpriseAgentSurname(getContractOtherData("L_ENTERPRISE_DIR_STAFF_NAME")));
		enterpriseAgentNIFFormTB.setValue(getContractOtherData("L_ENTERPRISE_DIR_STAFF_NIF"));
		enterpriseAgentPositionFormTB.setValue(getContractOtherData("L_ENTERPRISE_DIR_STAFF_CHARGE"));
		minorAgentFormTB.setValue(getContractOtherData("L_LEGAL_REPRESENTATIVE_NAME"));
		minorAgentNIFFormTB.setValue(getContractOtherData("L_LEGAL_REPRESENTATIVE_NIF"));
		minorAgentQualityOfFormTB.setValue(getContractOtherData("L_LEGAL_REPRESENTATIVE_CHARGE"));
		getEnableDisableButton(ssReductionFormB, AonStringUtils.containsIgnoreCase(getContractOtherData("L_QUOTE_BONUS"), "YES"));
		setSelectedValueLB(employeeFormLB, getContractOtherData("L_EMPLOYEE_OPT"));
		workplaceFormTB.setValue(getContractOtherData("L_CONTRACT_WORKPLACE_ADDRESS"));
		tutorFormTB.setValue(getContractOtherData("L_FORMATION_TEACHER"));
		efectiveWorkHoursFormTB.setValue(getContractOtherData("L_HORARIO_LABORAL"));
		activityHoursFormTB.setValue(getContractOtherData("L_HORARIO_LECTIVO"));
		trialPeriodFormTB.setValue(getContractOtherData("L_TRIAL_DURATION"));
		getEnableDisableButton(agreementTrialFormB, getContractOtherDataCB("L_TRIAL_DURATION_INCREASE"));
		salaryAmountFormTB.setValue(getContractOtherData("L_SALARY_AMOUNT"));
		salaryPeriodFormTB.setValue(getContractOtherData("L_SALARY_PERIOD"));
		holidaysFormTB.setValue(getContractOtherData("L_HOLIDAYS"));
		getEnableDisableButton(degreeExistFormB, getContractOtherDataCB("L_ANNEX_I_CHECK"));
		getEnableDisableButton(degreeExist2FormB, getContractOtherDataCB("L_ANNEX_II_CHECK"));
	}
	
	private void fillContractOtherDataPractice() {
		enterpriseAgentNamePracTB.setValue(getEnterpriseAgentName(getContractOtherData("P_ENTERPRISE_DIR_STAFF_NAME")));
		enterpriseAgentSurnamePracTB.setValue(getEnterpriseAgentSurname(getContractOtherData("P_ENTERPRISE_DIR_STAFF_NAME")));
		enterpriseAgentNIFPracTB.setValue(getContractOtherData("P_ENTERPRISE_DIR_STAFF_NIF"));
		enterpriseAgentPositionPracTB.setValue(getContractOtherData("P_ENTERPRISE_DIR_STAFF_CHARGE"));
		minorAgentPracTB.setValue(getContractOtherData("P_LEGAL_REPRESENTATIVE_NAME"));
		minorAgentNIFPracTB.setValue(getContractOtherData("P_LEGAL_REPRESENTATIVE_NIF"));
		minorAgentQualityOfPracTB.setValue(getContractOtherData("P_LEGAL_REPRESENTATIVE_CHARGE"));
		profesionalCertPracTB.setValue(getContractOtherData("P_PROFESSIONAL_CERT"));
		obtainingDatePracTB.setValue(getContractOtherData("P_PROFESSIONAL_CERT_OBTAIN_DATE"));
		disabilityCertPracTB.setValue(getContractOtherData("P_DISABILITY_ISSUE_ENTITY"));
		disabilityCertMorePracTB.setValue(getContractOtherData("P_DISABILITY_ISSUE_ENTITY_MORE"));
		setSelectedValueLB(firstContractPracLB, getContractOtherData("P_FIRST_CONTRACT"));
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
		setSelectedValueLB(unemploymentSubsidyPracLB, getContractOtherData("P_OPT3_UNEMPLOYMENT"));
		adaptationPeriodPracTB.setValue(getContractOtherData("P_OPT4_TRIAL_DURATION"));
		adaptationConditionsPracTB.setValue(getContractOtherData("P_OPT4_TRIAL_DURATION_CONDITIONS"));
		adaptationWorkPracTB.setValue(getContractOtherData("P_OPT4_WORK_PLACE_ADAPTATIONS"));
		personalSocialAdjustPracTB.setValue(getContractOtherData("P_OPT4_STAFF_ADJUSTMENT"));
		personalSocialAdjustMorePracTB.setValue(getContractOtherData("P_OPT4_STAFF_ADJUSTMENT_MORE"));
		setSelectedValueLB(motivationPracLB, getContractOtherData("P_OPT5_MOTIVATION"));
		setSelectedValueLB(employerPracLB, getContractOtherData("P_OPT5_EMPLOYER"));
	}
	
	private void setSelectedValueLB(ListBox lBox, String str) {
	    String text = str;
	    int indexToFind = 0;
	    for (int i = 0; i < lBox.getItemCount(); i++) {
	        if (lBox.getValue(i).equals(text)) {
	            indexToFind = i;
	            break;
	        }
	    }
	    lBox.setSelectedIndex(indexToFind);
	}

	public  Map<String, String> getContractOtherData() {
		return contractEmployeeInfo.getContractOtherData();
	}
	
	private void getEnableDisableButton(Button button, boolean disabled) {
		button.removeStyleName(disabled ? AON.AON_ICON_DISABLE : AON.AON_ICON_ENABLE);
		button.removeStyleName(AON.AON_NO_MARGIN);
		button.removeStyleName(AON.AON_EDIT_DATA_TABLE_BUTTON);
		
		button.setStyleName(!disabled ? AON.AON_ICON_DISABLE : AON.AON_ICON_ENABLE );
		button.setStyleName(AON.AON_NO_MARGIN, true);
		button.setStyleName(AON.AON_EDIT_DATA_TABLE_BUTTON, true);
	}
	
	private boolean isActiveToggleButton(Button button) {
		return AonStringUtils.containsIgnoreCase(button.getStyleName(), AON.AON_ICON_ENABLE);
	}
	
	public  Map<String, String> getContractInfo() {
		return this.contractEmployeeInfo.getContractOtherData();
	}
	
	// ------------------------------------------------------ Abstract Methods

	protected abstract void showErrorMessage(String title, String message);

	protected abstract void showSuccessMessage(String title, String message);
	
	protected abstract void showLoadingMessage(String message);
	
	protected abstract void onContractPDF();
	
	// ------------------------------------------------------ Toolbar Methods

	public void saveOhterData() {
		showLoadingMessage("Guardando otros datos de contrato...");
		saveOhterDataDB(s -> {
				showSuccessMessage("Otros Datos", "Otros datos de contrato guardados correctamente");
				reloadOtherData();
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
	
}
