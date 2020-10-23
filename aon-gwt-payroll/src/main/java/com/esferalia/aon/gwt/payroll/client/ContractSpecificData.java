package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.esferalia.aon.gwt.common.client.widget.DateBoxEx;
import com.esferalia.aon.gwt.common.shared.StringUtils;
import com.esferalia.aon.gwt.payroll.shared.AcademicTitulation;
import com.esferalia.aon.gwt.payroll.shared.CNO;
import com.esferalia.aon.gwt.payroll.shared.EmployeeContractInfo;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.TableElement;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class ContractSpecificData extends ResizeComposite {

	// -------------------------------------------------- UiBinder --------------------------------------------------

	private static ContractSpecificDataUiBinder uiBinder = GWT.create(ContractSpecificDataUiBinder.class);

	interface ContractSpecificDataUiBinder extends UiBinder<Widget, ContractSpecificData> {}

	// -------------------------------------------------- UiFields --------------------------------------------------

	@UiField
	MyStyle style;
	
	@UiField
	SuggestBox cnoSB;
	
	@UiField
	TableElement otherDataTableElement;
	
	@UiField
	DateBoxEx calendarFormativeStartDate;
	
	@UiField
	DateBoxEx calendarFormativeEndDate;
	
	@UiField
	ListBox formativeLevelLB;
	
	@UiField
	ListBox academicTitulationLB;
	
	@UiField
	CheckBox profesionalityCB;
	
	@UiField
	ListBox signBasicCopyLB;
	
	@UiField
	TextArea basicCopyTA;
	
	@UiField
	TextBox useEnterpriseFreeTB;
	
	@UiField
	TextBox agreementHoursTB;
	
	@UiField
	TextBox agreementMinutesTB;
	
	@UiField
	CheckBox repeatFDCB;
	
	@UiField
	ListBox journeyTypeLB;
	
	@UiField
	TextBox journeyDurationHoursTB;
	
	@UiField
	TextBox journeyDurationMinutesTB;
	
	@UiField
	RadioButton teoricFormationYesRB;
	
	@UiField
	RadioButton teoricFormationNoRB;
	
	@UiField
	TextBox formationHoursTB;
	
	@UiField
	TextBox formationMinutesTB;
	
	@UiField
	TextBox retirementPercentTB;
	
	@UiField
	HTMLPanel workProgramDataCBPanel;
	
	@UiField
	HTMLPanel temporalWorkEnterpriseCBPanel;
	
	@UiField
	HTMLPanel contractReliefCBPanel;
	
	@UiField
	HTMLPanel offerWorkDataCBPanel;
	
	@UiField
	HTMLPanel workshopSchoolCBPanel;
	
	@UiField
	HTMLPanel disabilityCBPanel;
	
	@UiField
	HTMLPanel older52CBPanel;
	
	@UiField
	HTMLPanel annexedCBPanel;
	
	@UiField
	HTMLPanel campaignsCBPanel;
	
	@UiField
	HTMLPanel investCBPanel;
	
	@UiField
	HTMLPanel interimCauseCBPanel;
	
	@UiField
	HTMLPanel entrepreneurSupportCBPanel;
	
	@UiField
	HTMLPanel promotionMeasuresCBPanel;
	
	@UiField
	HTMLPanel quoteReductionsCBPanel;
	
	@UiField
	CheckBox workProgramDataCB;
	
	@UiField
	CheckBox temporalWorkEnterpriseCB;
	
	@UiField
	CheckBox contractReliefCB;
	
	@UiField
	CheckBox offerWorkDataCB;
	
	@UiField
	CheckBox workshopSchoolCB;
	
	@UiField
	CheckBox disabilityCB;
	
	@UiField
	CheckBox older52CB;
	
	@UiField
	CheckBox annexedCB;
	
	@UiField
	CheckBox campaignsCB;
	
	@UiField
	CheckBox investCB;
	
	@UiField
	CheckBox interimCauseCB;
	
	@UiField
	CheckBox entrepreneurSupportCB;
	
	@UiField
	CheckBox promotionMeasuresCB;
	
	@UiField
	CheckBox quoteReductionsCB;
	
	@UiField
	VerticalPanel workProgramDataTable;
	
	@UiField
	ListBox workProgramLB;
	
	@UiField
	VerticalPanel temporalWorkEnterpriseDataTable;
	
	@UiField
	TextBox nifTB;
	
	@UiField
	TextBox socialReasonTB;
	
	@UiField
	CheckBox contractTemplateCB;
	
	@UiField
	CheckBox foreignEnterpriseCB;
	
	@UiField
	VerticalPanel contractReliefDataTable;
	
	@UiField
	ListBox reliefEmployeeLB;
	
	@UiField
	TextBox retirementNameTB;
	
	@UiField
	TextBox retirementSurnameTB;
	
	@UiField
	TextBox retirementSurname2TB;
	
	@UiField
	VerticalPanel offerWorkDataTable;
	
	@UiField
	TextBox offerTB;
	
	@UiField
	VerticalPanel workshopSchoolDataTable;
	
	@UiField
	ListBox workshopSchoolLB;
	
	@UiField
	VerticalPanel disabilityDataTable;
	
	@UiField
	ListBox disabilityLB;
	
	@UiField
	VerticalPanel annexedDataTable;
	
	@UiField
	RadioButton annexedRB;

	@UiField
	RadioButton annexed2RB;
	
	@UiField
	TextBox sourceYearTB;
	
	@UiField
	VerticalPanel campaignsDataTable;
	
	@UiField
	TextBox cpCampaignTB;
	
	@UiField
	TextBox codeCampaignTB;
	
	@UiField
	TextBox yearCampaignTB;
	
	@UiField
	VerticalPanel investDataTable;
	
	@UiField
	ListBox employerLB;
	
	@UiField
	ListBox employeeLB;
	
	@UiField
	CheckBox researcherCB;
	
	@UiField
	VerticalPanel interimCauseDataTable;
	
	@UiField
	ListBox interimCauseLB;
	
	@UiField
	VerticalPanel entrepreneurSupportDataTable;
	
	@UiField
	ListBox bonusColectiveLB;

	@UiField
	CheckBox freelanceEmployeerCB;
	
	@UiField
	VerticalPanel promotionMeasuresDataTable;

	@UiField
	CheckBox promotionPermanentHiringCB;
	
	@UiField
	VerticalPanel quoteReductionsDataTable;
	
	@UiField
	ListBox reductionColectiveLB;
	
	@UiField
	RadioButton quoteReductionRB;

	@UiField
	RadioButton quoteReduction2RB;
	
	@UiField
	TextBox journeyPercentTB;
	
	interface MyStyle extends CssResource {}
	
	// ------------------------------------------------------ Constructor ---------------------------------------------------------

	private DomainEnterprisesServiceAsync impl = DomainEnterprisesServiceAsync.newInstance();
	private EmployeeContractInfo employeeContractInfo;
	private Map<String, CNO> cnoMap;
	
	public ContractSpecificData() {
		initWidget(uiBinder.createAndBindUi(this));
		cnoMap = new HashMap<String, CNO>();
		initializeView();
	}
	
	public void setEmployeeContractInfo(EmployeeContractInfo employeeContractInfoIn) {
		this.employeeContractInfo = employeeContractInfoIn;
		setDefaultView(this.employeeContractInfo.getContractInfo().getContractType());
		fillSpecificData();
	}

	// --------------------------------------------------------- UiHandlers --------------------------------------------------------
	
	@UiHandler("cnoSB")
	void onCNOSBChange(SelectionEvent<Suggestion> event) {
		String cnoStr = cnoSB.getValue();
		String cno = "";
		if(!StringUtils.isBlank(cnoStr))
			cno = cnoStr.split(" -")[0];
		
		this.employeeContractInfo.getContractSpecificData().setCno(cno);	
	}
	
	@UiHandler("calendarFormativeStartDate")
	void onCalendarFormativeStartDateChange(ValueChangeEvent<Date> event) {
		this.employeeContractInfo.getContractSpecificData().setCalendarFormativeStartDate(event.getValue());	
	}
	
	@UiHandler("calendarFormativeEndDate")
	void onCalendarFormativeEndDateChange(ValueChangeEvent<Date> event) {
		this.employeeContractInfo.getContractSpecificData().setCalendarFormativeEndDate(event.getValue());	
	}
	
	@UiHandler("formativeLevelLB")
	void onFormativeLevelLBChange(ChangeEvent event) {
		String formativeLevelValue = formativeLevelLB.getSelectedValue();
		Map<String, String> academicTitulations = AcademicTitulation.getAcademicTitulations(formativeLevelValue);
		createAcademicTitulationLB(academicTitulations);
		
		String academicTitulationValue = academicTitulationLB.getSelectedValue();
		
		this.employeeContractInfo.getContractSpecificData().setFormativeLevel(formativeLevelValue);
		this.employeeContractInfo.getContractSpecificData().setAcademicTitulation(academicTitulationValue);
	}
	
	@UiHandler("academicTitulationLB")
	void onAcademicTitulationLBChange(ChangeEvent event) {
		String academicTitulationValue = academicTitulationLB.getSelectedValue();
		this.employeeContractInfo.getContractSpecificData().setAcademicTitulation(academicTitulationValue);
	}
	
	@UiHandler("profesionalityCB")
	void onProfesionalityCBChange(ValueChangeEvent<Boolean> event) {
		this.employeeContractInfo.getContractSpecificData().setProfesionality(event.getValue());
	}
	
	@UiHandler("signBasicCopyLB")
	void onSignBasicCopyLBChange(ChangeEvent event) {
		String signBasicCopyValue = signBasicCopyLB.getSelectedValue();
		this.employeeContractInfo.getContractSpecificData().setSignBasicCopy(signBasicCopyValue);
	}
	
	@UiHandler("basicCopyTA")
	void onBasicCopyTAChange(ValueChangeEvent<String> event) {
		this.employeeContractInfo.getContractSpecificData().setBasicCopy(event.getValue());
	}
	
	@UiHandler("useEnterpriseFreeTB")
	void onUseEnterpriseFreeTBChange(ValueChangeEvent<String> event) {
		this.employeeContractInfo.getContractSpecificData().setUseEnterpriseFree(event.getValue());
	}
	
	@UiHandler("agreementHoursTB")
	void onAgreementHoursTBChange(ValueChangeEvent<String> event) {
		this.employeeContractInfo.getContractSpecificData().setAgreementHours(event.getValue());
	}
	
	@UiHandler("agreementMinutesTB")
	void onAgreementMinutesTBChange(ValueChangeEvent<String> event) {
		this.employeeContractInfo.getContractSpecificData().setAgreementMinutes(event.getValue());
	}
	
	@UiHandler("repeatFDCB")
	void onRepeatFDCBChange(ValueChangeEvent<Boolean> event) {
		this.employeeContractInfo.getContractSpecificData().setRepeatFD(event.getValue());
	}
	
	@UiHandler("journeyTypeLB")
	void onJourneyTypeLBChange(ChangeEvent event) {
		String journeyTypeValue = journeyTypeLB.getSelectedValue();
		this.employeeContractInfo.getContractSpecificData().setJourneyType(journeyTypeValue);
	}
	
	@UiHandler("journeyDurationHoursTB")
	void onJourneyDurationHoursTBChange(ValueChangeEvent<String> event) {
		this.employeeContractInfo.getContractSpecificData().setJourneyDurationHours(event.getValue());
	}
	
	@UiHandler("journeyDurationMinutesTB")
	void onJourneyDurationMinutesTBChange(ValueChangeEvent<String> event) {
		this.employeeContractInfo.getContractSpecificData().setJourneyDurationMinutes(event.getValue());
	}
	
	@UiHandler("teoricFormationYesRB")
	void onTeoricFormationRBChange(ValueChangeEvent<Boolean> event) {
		this.employeeContractInfo.getContractSpecificData().setTeoricFormation(event.getValue());
	}
	
//	@UiHandler("teoricFormationYesRB")
//	void onTeoricFormationYesRBChange(ValueChangeEvent<Boolean> event) {
//		this.employeeContractInfo.getContractSpecificData().setTeoricFormationYes(event.getValue());
//	}
//	
//	@UiHandler("teoricFormationNoRB")
//	void onTeoricFormationNoRBChange(ValueChangeEvent<Boolean> event) {
//		this.employeeContractInfo.getContractSpecificData().setTeoricFormationNo(event.getValue());
//	}
	
	@UiHandler("formationHoursTB")
	void onFormationHoursTBChange(ValueChangeEvent<String> event) {
		this.employeeContractInfo.getContractSpecificData().setFormationHours(event.getValue());
	}
	
	@UiHandler("formationMinutesTB")
	void onFormationMinutesTBChange(ValueChangeEvent<String> event) {
		this.employeeContractInfo.getContractSpecificData().setFormationMinutes(event.getValue());
	}
	
	@UiHandler("retirementPercentTB")
	void onRetirementPercentTBChange(ValueChangeEvent<String> event) {
		this.employeeContractInfo.getContractSpecificData().setRetirementPercent(event.getValue());
	}
	
	@UiHandler("workProgramDataCB")
	void onWorkProgramDataCBChange(ValueChangeEvent<Boolean> event) {
		if(event.getValue())
			showWorkProgramDataTable();
		else {
			resetWorkProgramDataTable();
			hideWorkProgramDataTable();
		}
		
		this.employeeContractInfo.getContractSpecificData().setWorkProgramData(event.getValue());
	}
	
	@UiHandler("temporalWorkEnterpriseCB")
	void onTemporalWorkEnterpriseCBChange(ValueChangeEvent<Boolean> event) {
		if(event.getValue())
			showTemporalWorkEnterpriseDataTable();
		else {
			resetTemporalWorkEnterpriseDataTable();
			hideTemporalWorkEnterpriseDataTable();
		}
		
		this.employeeContractInfo.getContractSpecificData().setTemporalWorkEnterprise(event.getValue());
	}
	
	@UiHandler("contractReliefCB")
	void onContractReliefCBChange(ValueChangeEvent<Boolean> event) {
		if(event.getValue())
			showContractReliefDataTable();
		else {
			resetContractReliefDataTable();
			hideContractReliefDataTable();
		}
		
		this.employeeContractInfo.getContractSpecificData().setContractRelief(event.getValue());
	}
	
	@UiHandler("offerWorkDataCB")
	void onOfferWorkDataCBChange(ValueChangeEvent<Boolean> event) {
		if(event.getValue())
			showOfferWorkDataTable();
		else {
			resetOfferWorkDataTable();
			hideOfferWorkDataTable();
		}
		
		this.employeeContractInfo.getContractSpecificData().setOfferWorkData(event.getValue());
	}
	
	@UiHandler("workshopSchoolCB")
	void onWorkshopSchoolCBChange(ValueChangeEvent<Boolean> event) {
		if(event.getValue())
			showWorkshopSchoolDataTable();
		else {
			resetWorkshopSchoolDataTable();
			hideWorkshopSchoolDataTable();
		}
		
		this.employeeContractInfo.getContractSpecificData().setWorkshopSchoolB(event.getValue());
	}
	
	@UiHandler("disabilityCB")
	void onDisabilityCBChange(ValueChangeEvent<Boolean> event) {
		if(event.getValue())
			showDisabilityDataTable();
		else {
			resetDisabilityDataTable();
			hideDisabilityDataTable();
		}
		
		this.employeeContractInfo.getContractSpecificData().setDisabilityB(event.getValue());
	}
	
	@UiHandler("annexedCB")
	void onAnnexedCBChange(ValueChangeEvent<Boolean> event) {
		if(event.getValue())
			showAnnexedDataTable();
		else {
			resetAnnexedDataTable();
			hideAnnexedDataTable();
		}
		
		this.employeeContractInfo.getContractSpecificData().setAnnexedB(event.getValue());
	}
	
	@UiHandler("campaignsCB")
	void onCampaignsCBChange(ValueChangeEvent<Boolean> event) {
		if(event.getValue())
			showCampaignsDataTable();
		else {
			resetCampaignsDataTable();
			hideCampaignsDataTable();
		}
		
		this.employeeContractInfo.getContractSpecificData().setCampaigns(event.getValue());
	}
	
	@UiHandler("investCB")
	void onInvestCBChange(ValueChangeEvent<Boolean> event) {
		if(event.getValue())
			showInvestDataTable();
		else {
			resetInvestDataTable();
			hideInvestDataTable();
		}
		
		this.employeeContractInfo.getContractSpecificData().setInvest(event.getValue());
	}
	
	@UiHandler("interimCauseCB")
	void onInterimCauseCBChange(ValueChangeEvent<Boolean> event) {
		if(event.getValue())
			showInterimCauseDataTable();
		else {
			resetInterimCauseDataTable();
			hideInterimCauseDataTable();
		}
		
		this.employeeContractInfo.getContractSpecificData().setIsInterimCause(event.getValue());
	}
	
	@UiHandler("entrepreneurSupportCB")
	void onEntrepreneurSupportCBChange(ValueChangeEvent<Boolean> event) {
		if(event.getValue())
			showEntrepreneurSupportDataTable();
		else {
			resetEntrepreneurSupportDataTable();
			hideEntrepreneurSupportDataTable();
		}
		
		this.employeeContractInfo.getContractSpecificData().setEntrepreneurSupport(event.getValue());
	}
	
	@UiHandler("promotionMeasuresCB")
	void onPromotionMeasuresCBChange(ValueChangeEvent<Boolean> event) {
		if(event.getValue())
			showPromotionMeasuresDataTable();
		else {
			resetPromotionMeasuresDataTable();
			hidePromotionMeasuresDataTable();
		}
		
		this.employeeContractInfo.getContractSpecificData().setPromotionMeasures(event.getValue());
	}
	
	@UiHandler("quoteReductionsCB")
	void onQuoteReductionsCBChange(ValueChangeEvent<Boolean> event) {
		if(event.getValue())
			showQuoteReductionsDataTable();
		else {
			resetQuoteReductionsDataTable();
			hideQuoteReductionsDataTable();
		}
		
		this.employeeContractInfo.getContractSpecificData().setQuoteReductions(event.getValue());
	}
	
	@UiHandler("workProgramLB")
	void onWorkProgramLBChange(ChangeEvent event) {
		String workProgramValue = workProgramLB.getSelectedValue();
		this.employeeContractInfo.getContractSpecificData().setWorkProgram(workProgramValue);
	}
	
	@UiHandler("nifTB")
	void onNifTBChange(ValueChangeEvent<String> event) {
		this.employeeContractInfo.getContractSpecificData().setNif(event.getValue());
	}
	
	@UiHandler("socialReasonTB")
	void onSocialReasonTBChange(ValueChangeEvent<String> event) {
		this.employeeContractInfo.getContractSpecificData().setSocialReason(event.getValue());
	}
	
	@UiHandler("contractTemplateCB")
	void onContractTemplateCBChange(ValueChangeEvent<Boolean> event) {
		this.employeeContractInfo.getContractSpecificData().setContractTemplate(event.getValue());
	}
	
	@UiHandler("foreignEnterpriseCB")
	void onForeignEnterpriseCBChange(ValueChangeEvent<Boolean> event) {
		this.employeeContractInfo.getContractSpecificData().setForeignEnterprise(event.getValue());
	}
	
	@UiHandler("reliefEmployeeLB")
	void onReliefEmployeeLBChange(ChangeEvent event) {
		String reliefEmployeeValue = reliefEmployeeLB.getSelectedValue();
		this.employeeContractInfo.getContractSpecificData().setReliefEmployee(reliefEmployeeValue);
	}
	
	@UiHandler("retirementNameTB")
	void onRetirementNameTBChange(ValueChangeEvent<String> event) {
		this.employeeContractInfo.getContractSpecificData().setRetirementName(event.getValue());
	}
	
	@UiHandler("retirementSurnameTB")
	void onRetirementSurnameTBChange(ValueChangeEvent<String> event) {
		this.employeeContractInfo.getContractSpecificData().setRetirementSurname(event.getValue());
	}
	
	@UiHandler("retirementSurname2TB")
	void onRetirementSurname2TBChange(ValueChangeEvent<String> event) {
		this.employeeContractInfo.getContractSpecificData().setRetirementSurname2(event.getValue());
	}
	
	@UiHandler("offerTB")
	void onOfferTBChange(ValueChangeEvent<String> event) {
		this.employeeContractInfo.getContractSpecificData().setOffer(event.getValue());
	}
	
	@UiHandler("workshopSchoolLB")
	void onWorkshopSchoolLBChange(ChangeEvent event) {
		String workshopSchoolValue = workshopSchoolLB.getSelectedValue();
		this.employeeContractInfo.getContractSpecificData().setWorkshopSchool(workshopSchoolValue);
	}
	
	@UiHandler("disabilityLB")
	void onDisabilityLBChange(ChangeEvent event) {
		String disabilityValue = disabilityLB.getSelectedValue();
		this.employeeContractInfo.getContractSpecificData().setDisability(disabilityValue);
	}
	
	@UiHandler("annexedRB")
	void onAnnexedRBChange(ValueChangeEvent<Boolean> event) {
		if(event.getValue())
			this.employeeContractInfo.getContractSpecificData().setAnnexed(true);
	}
	
	@UiHandler("annexed2RB")
	void onAnnexed2RBChange(ValueChangeEvent<Boolean> event) {
		if(event.getValue())
			this.employeeContractInfo.getContractSpecificData().setAnnexed(false);
	}
	
	@UiHandler("sourceYearTB")
	void onSourceYearTBChange(ValueChangeEvent<String> event) {
		this.employeeContractInfo.getContractSpecificData().setSourceYear(event.getValue());
	}
	
	@UiHandler("cpCampaignTB")
	void onCpCampaignTBChange(ValueChangeEvent<String> event) {
		this.employeeContractInfo.getContractSpecificData().setCpCampaign(event.getValue());
	}
	
	@UiHandler("codeCampaignTB")
	void onCodeCampaignTBChange(ValueChangeEvent<String> event) {
		this.employeeContractInfo.getContractSpecificData().setCodeCampaign(event.getValue());
	}
	
	@UiHandler("yearCampaignTB")
	void onYearCampaignTBChange(ValueChangeEvent<String> event) {
		this.employeeContractInfo.getContractSpecificData().setYearCampaign(event.getValue());
	}
	
	@UiHandler("employerLB")
	void onEmployerLBChange(ChangeEvent event) {
		String employerValue = employerLB.getSelectedValue();
		this.employeeContractInfo.getContractSpecificData().setEmployer(employerValue);
	}
	
	@UiHandler("employeeLB")
	void onEmployeeLBChange(ChangeEvent event) {
		String employeeValue = employeeLB.getSelectedValue();
		this.employeeContractInfo.getContractSpecificData().setEmployee(employeeValue);
	}
	
	@UiHandler("researcherCB")
	void onResearcherCBChange(ValueChangeEvent<Boolean> event) {
		this.employeeContractInfo.getContractSpecificData().setResearcher(event.getValue());
	}
	
	@UiHandler("interimCauseLB")
	void onInterimCauseLBChange(ChangeEvent event) {
		String interimCauseValue = interimCauseLB.getSelectedValue();
		this.employeeContractInfo.getContractSpecificData().setInterimCause(interimCauseValue);
	}
	
	@UiHandler("bonusColectiveLB")
	void onBonusColectiveLBChange(ChangeEvent event) {
		String bonusColectiveValue = bonusColectiveLB.getSelectedValue();
		this.employeeContractInfo.getContractSpecificData().setBonusColective(bonusColectiveValue);
	}
	
	@UiHandler("freelanceEmployeerCB")
	void onFreelanceEmployeerCBChange(ValueChangeEvent<Boolean> event) {
		this.employeeContractInfo.getContractSpecificData().setFreelanceEmployeer(event.getValue());
	}
	
	@UiHandler("promotionPermanentHiringCB")
	void onPromotionPermanentHiringCBChange(ValueChangeEvent<Boolean> event) {
		this.employeeContractInfo.getContractSpecificData().setPromotionPermanentHiring(event.getValue());
	}
	
	@UiHandler("reductionColectiveLB")
	void onReductionColectiveLBChange(ChangeEvent event) {
		String reductionColectiveValue = reductionColectiveLB.getSelectedValue();
		this.employeeContractInfo.getContractSpecificData().setReductionColective(reductionColectiveValue);
	}
	
	@UiHandler("quoteReductionRB")
	void onQuoteReductionRBChange(ValueChangeEvent<Boolean> event) {
		if(event.getValue())
			this.employeeContractInfo.getContractSpecificData().setQuoteReduction(true);
	}
	
	@UiHandler("quoteReduction2RB")
	void onQuoteReduction2RBChange(ValueChangeEvent<Boolean> event) {
		if(event.getValue())
			this.employeeContractInfo.getContractSpecificData().setQuoteReduction(false);
	}
	
	@UiHandler("journeyPercentTB")
	void onJourneyPercentTBChange(ValueChangeEvent<String> event) {
		this.employeeContractInfo.getContractSpecificData().setJourneyPercent(event.getValue());
	}

	// --------------------------------------------------- UiHandlers (Aux Methods) -------------------------------------------------
	
	private void showWorkProgramDataTable() {
		workProgramDataTable.getElement().getStyle().clearDisplay();
	}
	
	private void hideWorkProgramDataTable() {
		workProgramDataTable.getElement().getStyle().setDisplay(Display.NONE);
	}

	private void resetWorkProgramDataTable() {
		workProgramLB.clear();
		workProgramLB.addItem("FOMENTO EMPLEO AGRARIO", "01");
		workProgramLB.addItem("INSERCION CORPORACION LOCAL", "02");
		workProgramLB.addItem("INSERCION (ORGANO ADMINISTRACION ESTADO)", "03");
		workProgramLB.addItem("INSERCION (COMUNIDAD AUTONOMA)", "04");
		workProgramLB.addItem("INSERCION (ENTIDAD SIN ANIMO DE LUCRO)", "05");
		workProgramLB.addItem("INSERCION (UNIVERSIDAD)", "06");
		workProgramLB.addItem("SUBSIDIO AGRARIO (ORGANISMO INVERSORES)", "07");
		workProgramLB.addItem("AGENTES DE EMPLEO Y DESARROLLO LOCAL", "08");
		workProgramLB.addItem("ESTUDIOS Y CAMPA" + String.valueOf("\u00D1") + "AS", "09");
		workProgramLB.addItem("PROGRAMA DE EMPLEO I+E", "10");
		workProgramLB.addItem("INTERES SOCIAL (CORPORACION LOCAL)", "12");
		workProgramLB.addItem("INTERES SOCIAL (ORGANOS AD. ESTADO O CCAA)", "13");
		workProgramLB.addItem("INTERES SOCIAL (COMUNIDAD AUTONOMA)", "14");
		workProgramLB.addItem("INTERES SOCIAL (ENTIDAD SIN ANIMO DE LUCRO)", "15");
		workProgramLB.addItem("INTERES SOCIAL (UNIVERSIDAD)", "16");
	}

	private void showTemporalWorkEnterpriseDataTable() {
		temporalWorkEnterpriseDataTable.getElement().getStyle().clearDisplay();
	}
	
	private void hideTemporalWorkEnterpriseDataTable() {
		temporalWorkEnterpriseDataTable.getElement().getStyle().setDisplay(Display.NONE);
	}

	private void resetTemporalWorkEnterpriseDataTable() {
		nifTB.setText("");
		socialReasonTB.setText("");
		contractTemplateCB.setValue(false);
		foreignEnterpriseCB.setValue(false);
	}
	
	private void showContractReliefDataTable() {
		contractReliefDataTable.getElement().getStyle().clearDisplay();
	}
	
	private void hideContractReliefDataTable() {
		contractReliefDataTable.getElement().getStyle().setDisplay(Display.NONE);
	}

	private void resetContractReliefDataTable() {
		reliefEmployeeLB.clear();
		reliefEmployeeLB.addItem("TRABAJADOR INSCRITO COMO DEMANDANTE", "1");
		reliefEmployeeLB.addItem("TRABAJADOR CON CONTRATO DURACION DETERMINADA", "2");
		
		retirementNameTB.setText("");
		retirementSurnameTB.setText("");
		retirementSurname2TB.setText("");
	}
	
	private void showOfferWorkDataTable() {
		offerWorkDataTable.getElement().getStyle().clearDisplay();
	}
	
	private void hideOfferWorkDataTable() {
		offerWorkDataTable.getElement().getStyle().setDisplay(Display.NONE);
	}

	private void resetOfferWorkDataTable() {
		offerTB.setText("");
	}
	
	private void showWorkshopSchoolDataTable() {
		workshopSchoolDataTable.getElement().getStyle().clearDisplay();
	}
	
	private void hideWorkshopSchoolDataTable() {
		workshopSchoolDataTable.getElement().getStyle().setDisplay(Display.NONE);
	}

	private void resetWorkshopSchoolDataTable() {
		workshopSchoolLB.clear();
		workshopSchoolLB.addItem("CONTRATO TALLERES EMPLEO ALUMNO/TRABAJADOR", "E01");
		workshopSchoolLB.addItem("CONTRATO TALLERES DE EMPLEO PERSONAL", "E02");
		workshopSchoolLB.addItem("CONTRATO FORMACION DUAL ALUMNO/TRABAJADOR", "F01");
		workshopSchoolLB.addItem("CONTRATO  FORMACION DUAL PERSONAL", "F02");
		workshopSchoolLB.addItem("CASA DE OFICIO ALUMNO/TRABAJADOR", "O01");
		workshopSchoolLB.addItem("CASA DE OFICIO PERSONAL", "O02");
		workshopSchoolLB.addItem("CONTRATO ESCUALA TALLER ALUMNO/TRABAJADOR", "T01");
		workshopSchoolLB.addItem("CONTRATO ESCUALA TALLER PERSONAL UPD", "T02");
	}
	
	private void showDisabilityDataTable() {
		disabilityDataTable.getElement().getStyle().clearDisplay();
	}
	
	private void hideDisabilityDataTable() {
		disabilityDataTable.getElement().getStyle().setDisplay(Display.NONE);
	}

	private void resetDisabilityDataTable() {
		disabilityLB.clear();
		disabilityLB.addItem("DISCAPACITADOS EN CENTROS ESPECIALES DE EMPLEO", "C");
		disabilityLB.addItem("ENCLAVES LABORALES DISC. INTELECT. >= 33%", "E");
		disabilityLB.addItem("ENCLAVES LABORALES DISC. FIS./SENS. >= 65%", "F");
		disabilityLB.addItem("ENCLAVES LABORALES MUJERES DISC. >= 33%", "G");
		disabilityLB.addItem("DISCAPACITADOS", "S");
	}
	
	private void showAnnexedDataTable() {
		annexedDataTable.getElement().getStyle().clearDisplay();
	}
	
	private void hideAnnexedDataTable() {
		annexedDataTable.getElement().getStyle().setDisplay(Display.NONE);
	}

	private void resetAnnexedDataTable() {
		annexedRB.setValue(false);
		annexed2RB.setValue(false);
		sourceYearTB.setText("");
	}
	
	private void showCampaignsDataTable() {
		campaignsDataTable.getElement().getStyle().clearDisplay();
	}
	
	private void hideCampaignsDataTable() {
		campaignsDataTable.getElement().getStyle().setDisplay(Display.NONE);
	}

	private void resetCampaignsDataTable() {
		cpCampaignTB.setText("");
		codeCampaignTB.setText("");
		yearCampaignTB.setText("");
	}

	private void showInvestDataTable() {
		investDataTable.getElement().getStyle().clearDisplay();
	}
	
	private void hideInvestDataTable() {
		investDataTable.getElement().getStyle().setDisplay(Display.NONE);
	}

	private void resetInvestDataTable() {
		employerLB.clear();
		employerLB.addItem("ORGANISMO PUBLICO", "1");
		employerLB.addItem("INSTITUCION SIN ANIMO DE LUCRO", "2");
		employerLB.addItem("UNIVERSIDAD PUBLICA", "3");
		employerLB.addItem("ORGANISMO PUBLICO DE INVESTIGACION DE LA ADMINISTRACION GENERAL DEL ESTADO", "4");
		employerLB.addItem("ORGANISMO PUBLICO DE INVESTIGACION DE OTRAS ADMINISTRACIONES PUBLICAS", "5");
		employerLB.addItem("UNIVERSIDADES PRIVADAS Y UNIVERSIDADES DE LA IGLESIA CATOLICA QUE PERCIBAN FONDOS PARA CONTRATAR PERSONAL INVESTIGADOR", "6");
		employerLB.addItem("ENTIDADES PRIVADAS SIN ANIMO DE LUCRO QUE REALICEN ACTIVIDADES DE I+D", "7");
		employerLB.addItem("CONSORCIOS PUBLICOS Y FUNDACIONES DEL SECTOR PUBLICO SEGUN D.A. 1ª LEY 14/2011", "8");
		employerLB.addItem("OTROS ORGANISMOS DE INVESTIGACION DE LA AGE CUANDO REALICEN ACTIVIDAD DE INVESTIGACION", "9");
		
		employeeLB.clear();
		employeeLB.addItem("INVESTIGAODR", "1");
		employeeLB.addItem("CIENTIFICO O TECNICO", "2");
		
		researcherCB.setValue(false);
	}
	
	private void showInterimCauseDataTable() {
		interimCauseDataTable.getElement().getStyle().clearDisplay();
	}
	
	private void hideInterimCauseDataTable() {
		interimCauseDataTable.getElement().getStyle().setDisplay(Display.NONE);
	}

	private void resetInterimCauseDataTable() {
		interimCauseLB.clear();
		interimCauseLB.addItem("TRABAJADOR CON DERECHO RESERVA DE PUESTO", "A");
		interimCauseLB.addItem("TRABAJADOR POR MATERNIDAD SIN BONIFICACION DE CUOTAS", "B");
		interimCauseLB.addItem("EXCEDENCIA CUIDADO HIJO PERCEPTOR PRESTA", "C");
		interimCauseLB.addItem("EXCEDENCIA CUIDADO HIJO NO PERCEP.PRESTA", "D");
		interimCauseLB.addItem("TRABAJADOR PROCESO DE SELECCION/PROMOCION", "E");
		interimCauseLB.addItem("MATERNIDAD CON BONIFICACION DE CUOTAS", "F");
		interimCauseLB.addItem("ADOPCION", "G");
		interimCauseLB.addItem("ACOGIMIENTO", "H");
		interimCauseLB.addItem("RIESGO DURANTE EMBARAZO", "I");
		interimCauseLB.addItem("TRABAJ.EN FORMACION POR PERCEPTOR PRESTA", "J");
		interimCauseLB.addItem("MINUSVALIDOS DESEMPLEADOS POR MINUSV.INCAP.TEMP", "K");
		interimCauseLB.addItem("EXCEDENCIA CUIDADO FAMILIAR PERCEP.PREST", "L");
		interimCauseLB.addItem("SUSTITUCION VICTIMAS VIOLENCIA DE GENERO", "M");
		interimCauseLB.addItem("PATERNIDAD", "N");
		interimCauseLB.addItem("RIESGO DURANTE LA LACTANCIA NATURAL", "O");
		interimCauseLB.addItem("CONTRATADOS POR AUTONOMOS, POR CONCILIACION", "P");
	}
	
	private void showEntrepreneurSupportDataTable() {
		entrepreneurSupportDataTable.getElement().getStyle().clearDisplay();
	}
	
	private void hideEntrepreneurSupportDataTable() {
		entrepreneurSupportDataTable.getElement().getStyle().setDisplay(Display.NONE);
	}

	private void resetEntrepreneurSupportDataTable() {
		bonusColectiveLB.clear();
		bonusColectiveLB.addItem("JOVENES MENORES DE 30 A" + String.valueOf("\u00D1") + "OS", "001");
		bonusColectiveLB.addItem("MAYORES DE 45 A" + String.valueOf("\u00D1") + "OS", "002");
		bonusColectiveLB.addItem("DESEMPLEADOS INSCRIT. O.E 12 O MAS MESES", "003");
		bonusColectiveLB.addItem("MUJERES SUBREPR.INSCRITAS 12 O MAS MESES", "004");
		bonusColectiveLB.addItem("PERSONAS CON DISCAPACIDAD", "005");
		bonusColectiveLB.addItem("MUJERES SUBREPRES. MAYORES.DE 45 A" + String.valueOf("\u00D1") + "OS", "006");
		bonusColectiveLB.addItem("MUJERES SUBREPRESENTADAS (RESTO)", "007");
		bonusColectiveLB.addItem("JOVEN MENOR DE 30 A" + String.valueOf("\u00D1") + "OS PERCEPTOR REASS", "008");
		bonusColectiveLB.addItem("MAYOR DE 45 A" + String.valueOf("\u00D1") + "OS PERCEPTOR DE REASS", "009");
		bonusColectiveLB.addItem("INSCRITOS 12 O MAS MESES PERCEPT. REASS", "010");
		bonusColectiveLB.addItem("MUJER SUBREPR. > 45 A" + String.valueOf("\u00D1") + "OS PERCEPTOR REASS", "011");
		bonusColectiveLB.addItem("MUJER SUBR. INSCR 12 O MAS MESES. REASS", "012");
		bonusColectiveLB.addItem("MUJER SUBREP. (RESTO) SUBSIDIO REASS", "013");
		bonusColectiveLB.addItem("MENOR DE 25 A" + String.valueOf("\u00D1") + "OS", "014");
		bonusColectiveLB.addItem("ENTRE 30 Y 44 A" + String.valueOf("\u00D1") + "OS", "015");
		bonusColectiveLB.addItem("PERCEPTOR DE RENTAS MINIMAS DE INSERCION", "016");
		bonusColectiveLB.addItem("PERSONAS NO PUEDEN ACCEDER A PRESTACION", "017");
		bonusColectiveLB.addItem("JOVENES PROCED. INSTITUC. PENITENCIARIAS", "018");
		bonusColectiveLB.addItem("PERSONAS PROBLEM DROGADICCION Y ALCOHOL", "019");
		bonusColectiveLB.addItem("INTERNOS DE CENTROS PENITENCIARIOS", "020");
		bonusColectiveLB.addItem("PENADOS EN INSTITUCIONES PENITENCIARIAS", "021");
		bonusColectiveLB.addItem("MUJERES DESEMPLEADAS ENTRE 16 Y 45 A" + String.valueOf("\u00D1") + "OS", "022");
		bonusColectiveLB.addItem("MUJERES SUBPRESENTADAS INSC. 6 O MAS MESES", "023");
		bonusColectiveLB.addItem("DESEMPLEADOS INSCRITOS OE 6 O MAS MESES", "024");
		bonusColectiveLB.addItem("DESEMPLEADOS MAYORES 45 Y HASTA 55 A" + String.valueOf("\u00D1") + "OS", "025");
		bonusColectiveLB.addItem("DESEMPLEADOS MAYORES 55 Y HASTA 65 A" + String.valueOf("\u00D1") + "OS", "026");
		bonusColectiveLB.addItem("DESEM.PERC. PREST.SUB.RESTA 1 A" + String.valueOf("\u00D1") + "O O MÁS", "027");
		bonusColectiveLB.addItem("DESEMPL. PERCEP. RENTA ACTIVA INSERCION", "028");
		bonusColectiveLB.addItem("MUJ.INSC.OE.12 O MAS MES EN 24 SIG. ALUMBR", "029");
		bonusColectiveLB.addItem("DESEMPLEADOS PERCEPTORES SUBSIDIO REASS", "030");
		bonusColectiveLB.addItem("CONVERSION A INDEFINIDO ACOGIDO BONIFICA", "031");
		bonusColectiveLB.addItem("MATERNIDAD", "032");
		bonusColectiveLB.addItem("ADOPCION", "033");
		bonusColectiveLB.addItem("ACOGIMIENTO", "034");
		bonusColectiveLB.addItem("RIESGO DURANTE EMBARAZO", "035");
		bonusColectiveLB.addItem("PERCEPTORES DE RENTA ACTIVA DE INSERCIÓN (MAYORES 45 HASTA 55 A" + String.valueOf("\u00D1") + "OS)", "036");
		bonusColectiveLB.addItem("PERCEPTORES DE RENTA ACTIVA DE INSERCIÓN (MAYORES 55 HASTA 65 A" + String.valueOf("\u00D1") + "OS)", "037");
		bonusColectiveLB.addItem("PERCEP.RENTA ACTIVA INS.RESTO EDADES", "038");
		bonusColectiveLB.addItem("MUJ.CONTRATADAS 24 MESES SIGU.ALUMBRAM", "039");
		bonusColectiveLB.addItem("MENORES INTERNOS INCLUIDOS EN LEY ORG. 5", "040");
		bonusColectiveLB.addItem("VÍCTIMAS DE VIOLENCIA DOMÉSTICA", "041");
		bonusColectiveLB.addItem("PERSONAS CON DISCAPACIDAD DESEMP.INTERINIDAD POR INC", "042");
		bonusColectiveLB.addItem("MAYORES DE 52 A" + String.valueOf("\u00D1") + "OS BENEFICIARIOS DE SUBS", "043");
		bonusColectiveLB.addItem("DESEMPLEADOS PERCEPTORES DE RENTA AGRARIA", "044");
		bonusColectiveLB.addItem("MATERNIDAD O EXCEDENCIA TRANSFORMADO ANTES DE 1 A" + String.valueOf("\u00D1") + "O", "045");
		bonusColectiveLB.addItem("EXCEDENCIA CUIDADO HIJO PERCEPTOR PRESTACIONES", "046");
		bonusColectiveLB.addItem("EXCEDENCIA CUIDADO FAMILIAR PERCEP.PRESTACIONES", "047");
		bonusColectiveLB.addItem("SUSTITUCIÓN VÍCTIMAS VIOLENCIA DE GÉNERO", "048");
		bonusColectiveLB.addItem("MAYORES DE 60 A" + String.valueOf("\u00D1") + "OS", "049");
		bonusColectiveLB.addItem("MUJERES REINC.2 A" + String.valueOf("\u00D1") + "OS SIGUIENTES AL PARTO", "050");
		bonusColectiveLB.addItem("PERSONAS CON DISCAPACIDAD INTELECTUAL O PARÁLISIS CELEBRAL O ENFERMEDAD MENTAL, CON GRADO IGUAL O SUPERIOR AL 33 %", "051");
		bonusColectiveLB.addItem("PERSONAS CON DISCAPACIDAD FÍSICA O SENSORIAL, CON GRADO IGUAL O SUPERIOR AL 65 %", "052");
		bonusColectiveLB.addItem("MUJERES CON DISCAPACIDAD, CON GRADO IGUAL O SUPERIOR AL 33 %", "053");
		bonusColectiveLB.addItem("PERSONAL INVESTIGADOR EN FORMACIÓN", "054");
		bonusColectiveLB.addItem("MUJERES CONTRATADAS DESPUÉS DE 5 A" + String.valueOf("\u00D1") + "OS DE INACTIVIDAD LABORAL, SI ANTERIORMENTE HAN TRABAJADO 3 A" + String.valueOf("\u00D1") + "OS", "055");
		bonusColectiveLB.addItem("JÓVENES ENTRE 16 Y 30 A" + String.valueOf("\u00D1") + "OS (AMBOS INCLUSIVE)", "056");
		bonusColectiveLB.addItem("MAYORES DE 45 A" + String.valueOf("\u00D1") + "OS", "057");
		bonusColectiveLB.addItem("MUJERES", "058");
		bonusColectiveLB.addItem("MATERNIDAD O EXCEDENCIA TRANSFORMADO EN EL MOMENTO DE LA REINCORPORACIÓN", "059");
		bonusColectiveLB.addItem("VÍCTIMAS DE VIOLENCIA DE GÉNERO", "060");
		bonusColectiveLB.addItem("TEXTIL. MUJERES MENORES DE 45 A" + String.valueOf("\u00D1") + "OS", "061");
		bonusColectiveLB.addItem("TEXTIL. TRABAJADORES CON DISCAPACIDAD", "062");
		bonusColectiveLB.addItem("TEXTIL. MAYORES DE 30 Y MENORES DE 45 NO ACOGIDOS A NINGÚN COLECTIVO DEL PROGRAMA DE FOMENTO DE EMPLEO", "063");
		bonusColectiveLB.addItem("TEXTIL. MAYORES DE 30 Y MENORES DE 45 ACOGIDOS A ALGÚN COLECTIVO DEL PROGRAMA DE FOMENTO DE EMPLEO", "064");
		bonusColectiveLB.addItem("TEXTIL. HOMBRES MAYORES DE 45 Y MENORES DE 55 A" + String.valueOf("\u00D1") + "OS", "065");
		bonusColectiveLB.addItem("TEXTIL. MUJERES MAYORES DE 45 Y MENORES DE 55 A" + String.valueOf("\u00D1") + "OS", "066");
		bonusColectiveLB.addItem("TEXTIL. HOMBRES MAYORES DE 55 A" + String.valueOf("\u00D1") + "OS", "067");
		bonusColectiveLB.addItem("TEXTIL. MUJERES MAYORES DE 55 A" + String.valueOf("\u00D1") + "OS", "068");
		bonusColectiveLB.addItem("TEXTIL. TRABAJADOR MAYOR 55 A" + String.valueOf("\u00D1") + "OS PERCEPTOR PRESTACIÓN CONTRIBUTIVA AL QUE RESTE AL MENOS 1 A" + String.valueOf("\u00D1") + "O DE PERCEPCIÓN", "069");
		bonusColectiveLB.addItem("HOMBRE SIN DISCAPACIDAD SEVERA < 45 A" + String.valueOf("\u00D1") + "OS CON CONTRATO INDEFINIDO", "070");
		bonusColectiveLB.addItem("HOMBRE SIN DISCAPACIDAD SEVERA >=45 A" + String.valueOf("\u00D1") + "OS CON CONTRATO INDEFINIDO", "071");
		bonusColectiveLB.addItem("HOMBRE CON DISCAPACIDAD SEVERA < 45 A" + String.valueOf("\u00D1") + "OS CON CONTRATO INDEFINIDO", "072");
		bonusColectiveLB.addItem("HOMBRE CON DISCAPACIDAD SEVERA >=45 A" + String.valueOf("\u00D1") + "OS CON CONTRATO INDEFINIDO", "073");
		bonusColectiveLB.addItem("MUJER SIN DISCAPACIDAD SEVERA < 45 A" + String.valueOf("\u00D1") + "OS CON CONTRATO INDEFINIDO", "074");
		bonusColectiveLB.addItem("MUJER SIN DISCAPACIDAD SEVERA >=45 A" + String.valueOf("\u00D1") + "OS CON CONTRATO INDEFINIDO", "075");
		bonusColectiveLB.addItem("MUJER CON DISCAPACIDAD SEVERA < 45 A" + String.valueOf("\u00D1") + "OS CON CONTRATO INDEFINIDO", "076");
		bonusColectiveLB.addItem("MUJER CON DISCAPACIDAD SEVERA >=45 A" + String.valueOf("\u00D1") + "OS CON CONTRATO INDEFINIDO", "077");
		bonusColectiveLB.addItem("HOMBRE SIN DISCAPACIDAD SEVERA < 45 A" + String.valueOf("\u00D1") + "OS CON CONTRATO TEMPORAL", "078");
		bonusColectiveLB.addItem("HOMBRE SIN DISCAPACIDAD SEVERA >=45 A" + String.valueOf("\u00D1") + "OS CON CONTRATO TEMPORAL", "079");
		bonusColectiveLB.addItem("HOMBRE CON DISCAPACIDAD SEVERA < 45 A" + String.valueOf("\u00D1") + "OS CON CONTRATO TEMPORAL", "080");
		bonusColectiveLB.addItem("HOMBRE CON DISCAPACIDAD SEVERA >=45 A" + String.valueOf("\u00D1") + "OS CON CONTRATO TEMPORAL", "081");
		bonusColectiveLB.addItem("MUJER SIN DISCAPACIDAD SEVERA < 45 A" + String.valueOf("\u00D1") + "OS CON CONTRATO TEMPORAL", "082");
		bonusColectiveLB.addItem("MUJER SIN DISCAPACIDAD SEVERA >=45 A" + String.valueOf("\u00D1") + "OS CON CONTRATO TEMPORAL", "083");
		bonusColectiveLB.addItem("MUJER CON DISCAPACIDAD SEVERA < 45 A" + String.valueOf("\u00D1") + "OS CON CONTRATO TEMPORAL", "084");
		bonusColectiveLB.addItem("MUJER CON DISCAPACIDAD SEVERA >=45 A" + String.valueOf("\u00D1") + "OS CON CONTRATO TEMPORAL", "085");
		bonusColectiveLB.addItem("PATERNIDAD", "086");
		bonusColectiveLB.addItem("RIESGO DURANTE LA LACTANCIA NATURAL", "087");
		bonusColectiveLB.addItem("PERSONAS CENTROS ALOJAMIENTO ALTERNATIVO", "088");
		bonusColectiveLB.addItem("PERSONAS SERVICIOS PREVENCIÓN/INSERCIÓN", "089");
		bonusColectiveLB.addItem("MENORES INTERNOS QUE TRABAJEN EN EL PROPIO CENTRO", "090");
		bonusColectiveLB.addItem("DESEMPLEADOS CON CARGAS FAMILIARES", "091");
		bonusColectiveLB.addItem("CARGAS FAMILIARES. DESEMPLEADOS INSCRITOS 6 Ó MÁS MESES", "092");
		bonusColectiveLB.addItem("CARGAS FAMILIARES. MUJERES CONTRATADAS EN LOS 24 MESES SIGUIENTES AL PARTO", "093");
		bonusColectiveLB.addItem("CARGAS FAMILIARES. MUJERES REINCORPORADAS DESPUÉS 5 A" + String.valueOf("\u00D1") + "OS INACTIVIDAD", "094");
		bonusColectiveLB.addItem("CARGAS FAMILIARES. JÓVENES ENTRE 16 Y 30 A" + String.valueOf("\u00D1") + "OS", "095");
		bonusColectiveLB.addItem("CARGAS FAMILIARES. MAYORES DE 45 A" + String.valueOf("\u00D1") + "OS", "096");
		bonusColectiveLB.addItem("CARGAS FAMILIARES. MUJERES EN GENERAL", "097");
		bonusColectiveLB.addItem("CARGAS FAMILIARES. VÍCTIMAS DE VIOLENCIA DOMÉSTICA", "098");
		bonusColectiveLB.addItem("CARGAS FAMILIARES. VÍCTIMAS DE VIOLENCIA DE GÉNERO", "099");
		bonusColectiveLB.addItem("CARGAS FAMILIARES. PERCEPTOR RENTA MÍNIMA INSERCIÓN", "100");
		bonusColectiveLB.addItem("CARGAS FAMILIARES. PERSONAS NO PUEDEN ACCEDER A PRESTACIÓN", "101");
		bonusColectiveLB.addItem("CARGAS FAMILIARES. JÓVENES PROCED.INSTITUCIONES PENITENCIARIAS", "102");
		bonusColectiveLB.addItem("CARGAS FAMILIARES. PERSONAS PROBLEMAS DROGADICCIÓN Y ALCOHOL", "103");
		bonusColectiveLB.addItem("CARGAS FAMILIARES. INTERNOS DE CENTROS PENITENCIARIOS", "104");
		bonusColectiveLB.addItem("CARGAS FAMILIARES. MENORES INTERNOS INCLUIDOS EN LEY ORG.5", "105");
		bonusColectiveLB.addItem("CARGAS FAMILIARES. TEXTIL-MUJERES MENORES DE 45 A" + String.valueOf("\u00D1") + "OS", "106");
		bonusColectiveLB.addItem("CARGAS FAMILIARES. TEXTIL-TRABAJADORES CON DISCAPACIDAD", "107");
		bonusColectiveLB.addItem("CARGAS FAMILIARES. TEXTIL-MAYORES DE 30 Y MENORES DE 45 NO ACOGIDOS A NINGÚN COLECTIVO DEL PROGRAMA DE FOMENTO DE EMPLEO", "108");
		bonusColectiveLB.addItem("CARGAS FAMILIARES. TEXTIL-MAYORES DE 30 Y MENORES DE 45 ACOGIDOS A ALGÚN COLECTIVO DEL PROGRAMA DE FOMENTO DE EMPLEO", "109");
		bonusColectiveLB.addItem("CARGAS FAMILIARES. TEXTIL-HOMBRES MAYORES DE 45 Y MENORES DE 55 A" + String.valueOf("\u00D1") + "OS", "110");
		bonusColectiveLB.addItem("CARGAS FAMILIARES. TEXTIL-MUJERES MAYORES DE 45 Y MENORES DE 55 A" + String.valueOf("\u00D1") + "OS", "111");
		bonusColectiveLB.addItem("CARGAS FAMILIARES. TEXTIL-HOMBRES MAYORES DE 55 A" + String.valueOf("\u00D1") + "OS", "112");
		bonusColectiveLB.addItem("CARGAS FAMILIARES. TEXTIL-MUJERES MAYORES DE 55 A" + String.valueOf("\u00D1") + "OS", "113");
		bonusColectiveLB.addItem("CARGAS FAMILIARES. TEXTIL-TRABAJADOR MAYOR 55 A" + String.valueOf("\u00D1") + "OS PERCEPTOR PRESTACIÓN CONTRIBUTIVA AL QUE RESTE AL MENOS 1 A" + String.valueOf("\u00D1") + "O DE PERCEPCIÓN", "114");
		bonusColectiveLB.addItem("CALZADO - MUJERES MENORES DE 45 A" + String.valueOf("\u00D1") + "OS", "115");
		bonusColectiveLB.addItem("CALZADO - TRABAJADORES CON DISCAPACIDAD", "116");
		bonusColectiveLB.addItem("CALZADO - MAYORES DE 30 Y MENORES DE 45 NO ACOGIDOS A NINGÚN COLECTIVO DEL PROGRAMA DE FOMENTO DE EMPLEO", "117");
		bonusColectiveLB.addItem("CALZADO - MAYORES DE 30 Y MENORES DE 45 ACOGIDOS A ALGÚN COLECTIVO DEL PROGRAMA DE FOMENTO DE EMPLEO", "118");
		bonusColectiveLB.addItem("CALZADO - HOMBRES MAYORES DE 45 Y MENORES DE 55 A" + String.valueOf("\u00D1") + "OS", "119");
		bonusColectiveLB.addItem("CALZADO - MUJERES MAYORES DE 45 Y MENORES DE 55 A" + String.valueOf("\u00D1") + "OS", "120");
		bonusColectiveLB.addItem("CALZADO - HOMBRES MAYORES DE 55 A" + String.valueOf("\u00D1") + "OS", "121");
		bonusColectiveLB.addItem("CALZADO - MUJERES MAYORES DE 55 A" + String.valueOf("\u00D1") + "OS", "122");
		bonusColectiveLB.addItem("CALZADO - TRABAJADOR MAYOR 55 A" + String.valueOf("\u00D1") + "OS PERCEPTOR PRESTACIÓN CONTRIBUTIVA AL QUE RESTE AL MENOS 1 A" + String.valueOf("\u00D1") + "O DE PERCEPCIÓN", "123");
		bonusColectiveLB.addItem("BENEFICIARIO DE PRESTACIÓN DURANTE AL MENOS 3 MESES", "124");
		bonusColectiveLB.addItem("BENEFICIARIO DE SUBSIDIO POR DESEMPLEO", "125");
		bonusColectiveLB.addItem("BENEFICIARIO DE RENTA ACTIVA DE INSERCIÓN", "126");
		bonusColectiveLB.addItem("PRIMER ASALARIADO DE TRABAJADOR AUTÓNOMO", "127");
		bonusColectiveLB.addItem("JUGUETE - MUJERES MENORES DE 45 A" + String.valueOf("\u00D1") + "OS", "128");
		bonusColectiveLB.addItem("JUGUETE - TRABAJADORES CON DISCAPACIDAD", "129");
		bonusColectiveLB.addItem("JUGUETE - MAYORES DE 30 Y MENORES DE 45 NO ACOGIDOS A NINGÚN COLECTIVO DEL PROGRAMA DE FOMENTO DE EMPLEO", "130");
		bonusColectiveLB.addItem("JUGUETE - MAYORES DE 30 Y MENORES DE 45 ACOGIDOS A ALGÚN COLECTIVO DEL PROGRAMA DE FOMENTO DE EMPLEO", "131");
		bonusColectiveLB.addItem("JUGUETE - HOMBRES MAYORES DE 45 Y MENORES DE 55 A" + String.valueOf("\u00D1") + "OS", "132");
		bonusColectiveLB.addItem("JUGUETE - MUJERES MAYORES DE 45 Y MENORES DE 55 A" + String.valueOf("\u00D1") + "OS", "133");
		bonusColectiveLB.addItem("JUGUETE - HOMBRES MAYORES DE 55 A" + String.valueOf("\u00D1") + "OS", "134");
		bonusColectiveLB.addItem("JUGUETE - MUJERES MAYORES DE 55 A" + String.valueOf("\u00D1") + "OS", "135");
		bonusColectiveLB.addItem("JUGUETE - TRABAJADOR MAYOR 55 A" + String.valueOf("\u00D1") + "OS PERCEPTOR PRESTACIÓN CONTRIBUTIVA AL QUE RESTE AL MENOS 1 A" + String.valueOf("\u00D1") + "O DE PERCEPCIÓN", "136");
		bonusColectiveLB.addItem("MUEBLE - MUJERES MENORES DE 45 A" + String.valueOf("\u00D1") + "OS", "137");
		bonusColectiveLB.addItem("MUEBLE - TRABAJADORES CON DISCAPACIDAD", "138");
		bonusColectiveLB.addItem("MUEBLE - MAYORES DE 30 Y MENORES DE 45 NO ACOGIDOS A NINGÚN COLECTIVO DEL PROGRAMA DE FOMENTO DE EMPLEO", "139");
		bonusColectiveLB.addItem("MUEBLE - MAYORES DE 30 Y MENORES DE 45 ACOGIDOS A ALGÚN COLECTIVO DEL PROGRAMA DE FOMENTO DE EMPLEO", "140");
		bonusColectiveLB.addItem("MUEBLE - HOMBRES MAYORES DE 45 Y MENORES DE 55 A" + String.valueOf("\u00D1") + "OS", "141");
		bonusColectiveLB.addItem("MUEBLE - MUJERES MAYORES DE 45 Y MENORES DE 55 A" + String.valueOf("\u00D1") + "OS", "142");
		bonusColectiveLB.addItem("MUEBLE - HOMBRES MAYORES DE 55 A" + String.valueOf("\u00D1") + "OS", "143");
		bonusColectiveLB.addItem("MUEBLE - MUJERES MAYORES DE 55 A" + String.valueOf("\u00D1") + "OS", "144");
		bonusColectiveLB.addItem("MUEBLE - TRABAJADOR MAYOR 55 A" + String.valueOf("\u00D1") + "OS PERCEPTOR PRESTACIÓN CONTRIBUTIVA AL QUE RESTE AL MENOS 1 A" + String.valueOf("\u00D1") + "O DE PERCEPCIÓN", "145");
		bonusColectiveLB.addItem("JÓVENES 16-30 A" + String.valueOf("\u00D1") + "OS, HOMBRES DESEMP.1 A" + String.valueOf("\u00D1") + "O SIN TITULACIÓN", "146");
		bonusColectiveLB.addItem("JÓVENES 16-30 A" + String.valueOf("\u00D1") + "OS, MUJERES DESEMP.1 A" + String.valueOf("\u00D1") + "O SIN TITULACIÓN", "147");
		bonusColectiveLB.addItem("MAYORES DE 45 A" + String.valueOf("\u00D1") + "OS, HOMBRES DESEMPLEADOS 1 A" + String.valueOf("\u00D1") + "O", "148");
		bonusColectiveLB.addItem("MAYORES DE 45 A" + String.valueOf("\u00D1") + "OS, MUJERES DESEMPLEADAS 1 A" + String.valueOf("\u00D1") + "O", "149");
		bonusColectiveLB.addItem("FORMACIÓN", "150");
		bonusColectiveLB.addItem("CONVERSIÓN A INDEFINIDO, HOMBRES", "151");
		bonusColectiveLB.addItem("CONVERSIÓN A INDEFINIDO, MUJERES", "152");
		bonusColectiveLB.addItem("HOMBRES 16-30 A" + String.valueOf("\u00D1") + "OS, DESEMP.12 MESES EN 18 SIN TITULACION", "153");
		bonusColectiveLB.addItem("MUJERES 16-30 A" + String.valueOf("\u00D1") + "OS, DESEMP.12 MESES EN 18 SIN TITULACION", "154");
		bonusColectiveLB.addItem("HOMBRES MAY. 45 A" + String.valueOf("\u00D1") + "OS DESEMPLEADOS 12 MESES EN 18", "155");
		bonusColectiveLB.addItem("MUJERES MAY. 45 A" + String.valueOf("\u00D1") + "OS DESEMPLEADOS 12 MESES EN 18", "156");
		bonusColectiveLB.addItem("JÓVENES 16-30 A" + String.valueOf("\u00D1") + "OS EN EMPRESAS MENOS DE 50 TRABAJ.", "157");
		bonusColectiveLB.addItem("MUJERES 16-30 A" + String.valueOf("\u00D1") + "OS OCUP. SUBREPR. EMPRESAS < 50", "158");
		bonusColectiveLB.addItem("MAYORES DE 45 A" + String.valueOf("\u00D1") + "OS EN EMPRESAS MENOS DE 50 TRABAJ.", "159");
		bonusColectiveLB.addItem("MUJERES MAY. 45 A" + String.valueOf("\u00D1") + "OS OCUP. SUBREPR. EMPRESAS < 50", "160");
		bonusColectiveLB.addItem("TRANSFORMACIÓN EN INDEFINIDO, HOMBRES", "161");
		bonusColectiveLB.addItem("TRANSFORMACIÓN EN INDEFINIDO, MUJERES", "162");
		bonusColectiveLB.addItem("VÍCTIMAS DE TERRORISMO", "163");
		bonusColectiveLB.addItem("TRANSFORMACIÓN INDEFINIDO, EXCLUÍDO SOCIAL", "164");
		bonusColectiveLB.addItem("TRANSFORMACIÓN INDEFINIDO, V. VIOLENCIA DE GÉNERO", "165");
		bonusColectiveLB.addItem("TRANSFORMACIÓN INDEFINIDO, V. VIOLENCIA DOMÉSTICA", "166");
		bonusColectiveLB.addItem("TRANSFORMACIÓN INDEFINIDO, VÍCTIMA TERRORISMO", "167");
		bonusColectiveLB.addItem("INCORPORACIÓN COMO SOCIO DESEMPLEADO MENOR 30 A" + String.valueOf("\u00D1") + "OS", "168");
		bonusColectiveLB.addItem("CONVERSIÓN EVENTUAL PRIMER EMPLEO JOVEN – HOMBRE", "169");
		bonusColectiveLB.addItem("CONVERSIÓN EVENTUAL PRIMER EMPLEO JOVEN – MUJER", "170");
		bonusColectiveLB.addItem("INDEFINIDO PROCEDENTE PRIMER EMPLEO JOVEN DE ETT", "171");
		bonusColectiveLB.addItem("INDEFINIDO PROCEDENTE CONTRATO EN PRÁCTICAS ETT", "172");
		bonusColectiveLB.addItem("BENEFICIARIO SISTEMA NACIONAL DE GARANTÍA JUVENIL", "173");
		bonusColectiveLB.addItem("CREACIÓN DE EMPLEO INDEFINIDO (SNGJ) RDL 1/2015", "174");
		bonusColectiveLB.addItem("VÍCTIMAS DE TRATA DE SERES HUMANOS", "175");
		bonusColectiveLB.addItem("CONT. FORMACIÓN SNGJ BONIF. 100% EMPRE MENOS 250 TRAB", "176");
		bonusColectiveLB.addItem("CONT. FORMACIÓN SNGJ BONIF. 75% EMPRE 250 Ó MÁS TRAB.", "177");
		bonusColectiveLB.addItem("TRANSFORMACIÓN CONTRATO DE FORMACIÓN – HOMBRES SNGJ", "178");
		bonusColectiveLB.addItem("TRANSFORMACIÓN CONTRATO DE FORMACIÓN – MUJERES SNGJ", "179");
		bonusColectiveLB.addItem("FAMILIAR DE TRABAJADOR AUTÓNOMO", "186");
		bonusColectiveLB.addItem("DESEMPLEADO, INSCRITO 12 MESES EN UN PERIODO DE 18, HOMBRES", "187");
		bonusColectiveLB.addItem("DESEMPLEADO, INSCRITO 12 MESES EN UN PERIODO DE 18, MUJERES", "188");
		bonusColectiveLB.addItem("TRANSFORMACIÓN CONTRATO TEMPORAL AGRARIO, HOMBRES", "189");
		bonusColectiveLB.addItem("TRANSFORMACIÓN CONTRATO TEMPORAL AGRARIO, MUJERES", "190");

		
		freelanceEmployeerCB.setValue(false);
	}
	
	private void showPromotionMeasuresDataTable() {
		promotionMeasuresDataTable.getElement().getStyle().clearDisplay();
	}
	
	private void hidePromotionMeasuresDataTable() {
		promotionMeasuresDataTable.getElement().getStyle().setDisplay(Display.NONE);
	}

	private void resetPromotionMeasuresDataTable() {
		promotionPermanentHiringCB.setValue(false);
	}
	
	private void showQuoteReductionsDataTable() {
		quoteReductionsDataTable.getElement().getStyle().clearDisplay();
	}
	
	private void hideQuoteReductionsDataTable() {
		quoteReductionsDataTable.getElement().getStyle().setDisplay(Display.NONE);
	}

	private void resetQuoteReductionsDataTable() {
		reductionColectiveLB.clear();
		reductionColectiveLB.addItem("DESEMPLEADOS CON EDAD IGUAL O INFERIOR A 30 ANOS", "01");
		reductionColectiveLB.addItem("DESEMPLEADOS AL MENOS 12 MESES EN 18 ANTERIORES", "02");
		reductionColectiveLB.addItem("DESEMPLEADOS MAYORES DE 20 ANOS INSCRITOS A 16/08/2011", "03");
		reductionColectiveLB.addItem("DESEMPLEADOS INSCRITOS EN OFICINA DE EMPLEO","04");
		reductionColectiveLB.addItem("TRANSFORMACION DE CONTRATO DE FORMACION HOMBRE","05");
		reductionColectiveLB.addItem("TRANSFORMACION DE CONTRATO DE FORMACION MUJER", "06");
		reductionColectiveLB.addItem("CONTRATO PREDOCTORAL","07");
		reductionColectiveLB.addItem("DESEMPLEADO < 30 ANOS SIN EXPERIENCIA LABORAL","08");
		reductionColectiveLB.addItem("DESEMPLEADO < 30 ANOS PROCED.OTRO SECTOR ACTIVIDAD","09");
		reductionColectiveLB.addItem("DESEMPLEADO < 30 ANOS INSCRITOS 12 MESES EN 18","10");
		reductionColectiveLB.addItem("DESEMPLEADO < 30 ANOS CON AUTONOMOS O MICROEMPRESA","11");
		reductionColectiveLB.addItem("PRIMER CONTRATO CON >=45 ANOS INSCRITO 12 M. EN 18","12");
		reductionColectiveLB.addItem("PRIMER CONTRATO CON >=45 ANOS RECUAL. PROFESIONAL","13");
		reductionColectiveLB.addItem("CONTRATO EN PR¡CTICAS A < 30 ANOS","14");
		reductionColectiveLB.addItem("CONTRATO EN PR¡CTICAS A < 30 ANOS, PRACT.NO LABOR.","15");
		reductionColectiveLB.addItem("DESEMPLEADO < 30 ANOS SIN TITULACION","16");
		reductionColectiveLB.addItem("INDEFINIDO PROCEDENTE CONTRATO DE FORMACION DE ETT","17");
		reductionColectiveLB.addItem("CONTRATO INDEFINIDO TARIFA PLANA","18");
		reductionColectiveLB.addItem("BENEFICIARIO SISTEMA NACIONAL DE GARANTÕA JUVENIL","19");
		reductionColectiveLB.addItem("CREACION DE EMPLEO INDEFINIDO","20");
		reductionColectiveLB.addItem("FORMACION, DISCAPACIDAD >= 33% DISPOSICION ADICIONAL 20 E.T.","21");
		reductionColectiveLB.addItem("PRACTICAS, DISCAPACIDAD >= 33% DISPOSICION ADICIONAL 20 E.T.","22");
		reductionColectiveLB.addItem("EN PRACTICAS < 35 ANOS, DISCAPACIDAD D.A. 20 E.T.","23");
		reductionColectiveLB.addItem("PRACT. NO LABOR.< 35 ANOS, DISCAPACIDAD D.A. 20 E.T.","24");

		quoteReductionRB.setValue(false);
		quoteReduction2RB.setValue(false);
		journeyPercentTB.setText("");
	}
	
	// ------------------------------------------------------ Auxiliar Methods ----------------------------------------------------
	
	private void initializeView() {
		hideTables();
		resetTables();
	}

	private void resetTables() {
		resetMainTable();
		resetWorkProgramDataTable();
		resetTemporalWorkEnterpriseDataTable();
		resetContractReliefDataTable();
		resetOfferWorkDataTable();
		resetWorkshopSchoolDataTable();
		resetDisabilityDataTable();
		resetAnnexedDataTable();
		resetCampaignsDataTable();
		resetInvestDataTable();
		resetInterimCauseDataTable();
		resetEntrepreneurSupportDataTable();
		resetPromotionMeasuresDataTable();
		resetQuoteReductionsDataTable();
	}

	private void resetMainTable() {
		// SuggestBox
		initalizeSuggestBox();
		
		// ListBox
		formativeLevelLB.clear();
		formativeLevelLB.addItem("-", "");
		formativeLevelLB.addItem("ESTUDIOS PRIMARIOS INCOMPLETOS", "11");
		formativeLevelLB.addItem("ESTUDIOS PRIMARIOS COMPLETO", "12");
		formativeLevelLB.addItem("PROGRAMAS PARA FORMACION E INSERCION LABORAL QUE NO PRECISAN DE UNA TITULACION", "21");
		formativeLevelLB.addItem("PRIMERA ETAPA DE EDUCACION SECUNDARIA SIN TITULO DE GRADUADO ESCOLAR O EQUIVALENTE", "22");
		formativeLevelLB.addItem("PRIMERA ETAPA DE EDUCACION SECUNDARIA CON TITULO DE GRADUADO ESCOLAR O EQUIVALENTE", "23");
		formativeLevelLB.addItem("PROGRAMAS PARA FORMACION E INSERCION LABORAL QUE PRECISAN DE UNA TITULACION DE ESTUDIOS SECUNDARIOS DE PRIMERA ETAPA", "31");
		formativeLevelLB.addItem("ENSE" + String.valueOf("\u00D1") + "ANZAS DE BACHILLERATO", "32");
		formativeLevelLB.addItem("ENSE" + String.valueOf("\u00D1") + "ANZAS DE GRADO MEDIO DE FORMACION ESPECIFICA, ARTES PLASTICAS...", "33");
		formativeLevelLB.addItem("ENSE" + String.valueOf("\u00D1") + "ANZAS DE GRADO MEDIO DE MUSICA Y DANZA", "34");
		formativeLevelLB.addItem("ENSE" + String.valueOf("\u00D1") + "ANZAS PARA LA FORMACION E INSERCION LABORAL QUE PRECISAN DE UNA TITULACION DE ESTUDIOS SECUNDARIOS DE SEGUNDA ETAPA", "41");
		formativeLevelLB.addItem("ENSE" + String.valueOf("\u00D1") + "ANZAS DE GRADO SUPERIOR DE FORMACION PROFESIONAL ESPECIFICA Y EQUIVALENTE", "51");
		formativeLevelLB.addItem("TITULOS PROPIOS DE LAS UNIVERSIDADES Y OTRAS ENSE" + String.valueOf("\u00D1") + "ANZAS QUE PRECISAN DEL TITULO", "52");
		formativeLevelLB.addItem("ENSE" + String.valueOf("\u00D1") + "ANZAS PARA LA FORMACION E INSERCION LABORAL QUE PRECISAN DE UNA FORMACION PROFESIONAL", "53");
		formativeLevelLB.addItem("ENSE" + String.valueOf("\u00D1") + "ANZAR UNIVERSITARIAS DE PRIMER CICLO Y EQUIVALENTES", "54");
		formativeLevelLB.addItem("ENSE" + String.valueOf("\u00D1") + "ANZAR UNIVERSITARIAS DE SEGUNDO CICLO Y EQUIVALENTES", "55");
		formativeLevelLB.addItem("ESTUDIOS OFICIALES DE ESPECIALIZACION PREFESIONAL", "56");
		formativeLevelLB.addItem("PROGRAMAS DE POSTGRADO IMPARTIDOS POR LAS UNIVERSIDADES U OTRAS INSTITUCIONES", "57");
		formativeLevelLB.addItem("PROGRAMAS DE FORMACION E INSERCION LABORAL QUE PRECISAN DE UNA TITULACION UNIVERSITARIA", "58");
		formativeLevelLB.addItem("ENSE" + String.valueOf("\u00D1") + "ANZAS UNIVERSITARIAS DE GRADO", "59");
		formativeLevelLB.addItem("ENSE" + String.valueOf("\u00D1") + "ANZAS UNIVERSITARIAS DE MASTER", "60");
		formativeLevelLB.addItem("DOCTORADO UNIVERSITARIO", "61");
		formativeLevelLB.addItem("SIN ESTUDIOS", "80");
		
		academicTitulationLB.clear();
		
		signBasicCopyLB.clear();
		signBasicCopyLB.addItem("FIRMADAS POR LOS REPRESENTANTES LEGALES", "1");
		signBasicCopyLB.addItem("NO EXISTE REPRESENTACION LEGAL", "2");
		signBasicCopyLB.addItem("NO SE HA FACILITADO COPIA", "3");
		signBasicCopyLB.addItem("REHUSA FIRMAR", "4");
		
		journeyTypeLB.clear();
		journeyTypeLB.addItem("JORNADA ANUAL","A");
		journeyTypeLB.addItem("JORNADA DIARIA","D");
		journeyTypeLB.addItem("JORNADA MENSUAL","M");
		journeyTypeLB.addItem("JORNADA SEMANAL","S");

		
		// CheckBox
		profesionalityCB.setValue(false);
		repeatFDCB.setValue(false);
		workProgramDataCB.setValue(false);
		temporalWorkEnterpriseCB.setValue(false);
		offerWorkDataCB.setValue(false);
		workshopSchoolCB.setValue(false);
		disabilityCB.setValue(false);
		older52CB.setValue(false);
		annexedCB.setValue(false);
		campaignsCB.setValue(false);
		investCB.setValue(false);
		
		// TexArea
		basicCopyTA.setText("");
		basicCopyTA.setHeight("100px");
		
		// TextBox
		useEnterpriseFreeTB.setText("");
		agreementHoursTB.setText("");
		agreementMinutesTB.setText("");
		journeyDurationHoursTB.setText("");
		journeyDurationMinutesTB.setText("");
		formationHoursTB.setText("");
		formationMinutesTB.setText("");
		retirementPercentTB.setText("");
		
		//DateBoxEs
		calendarFormativeStartDate.setValue(null);
		calendarFormativeEndDate.setValue(null);
		
		//RadioButton
		teoricFormationYesRB.setValue(false);
		teoricFormationNoRB.setValue(false);
	}

	private void initalizeSuggestBox() {
		impl.getCNOs(new AsyncCallback<Map<String, CNO>>() {

			@Override
			public void onFailure(Throwable caught) {}

			@Override
			public void onSuccess(Map<String, CNO> result) {
				cnoMap = result;
				
				List<String> cnoEntry = new ArrayList<String>();
				for(Entry<String, CNO> entry : cnoMap.entrySet())
					cnoEntry.add(entry.getKey() + " - " + entry.getValue().getTitle());
				List<String> cnoSuggest = new ArrayList<String>();
				for(String cno : cnoEntry)
					cnoSuggest.add(cno);
				MultiWordSuggestOracle orclIbans = (MultiWordSuggestOracle) cnoSB.getSuggestOracle();
				orclIbans.addAll(cnoSuggest);
				cnoSB.setAutoSelectEnabled(true);
			}});	
	}
	
	private void createAcademicTitulationLB(Map<String, String> academicTitulations) {
		academicTitulationLB.clear();
		for(Entry<String, String> entry : academicTitulations.entrySet()) {
			academicTitulationLB.addItem(entry.getValue(), entry.getKey());
		}
	}
	
	private void hideTables() {
		hideWorkProgramDataTable();
		hideTemporalWorkEnterpriseDataTable();
		hideContractReliefDataTable();
		hideOfferWorkDataTable();
		hideWorkshopSchoolDataTable();
		hideDisabilityDataTable();
		hideAnnexedDataTable();
		hideCampaignsDataTable();
		hideInvestDataTable();
		hideInterimCauseDataTable();
		hideEntrepreneurSupportDataTable();
		hidePromotionMeasuresDataTable();
		hideQuoteReductionsDataTable();
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
	
	private void setDefaultView(String contractType) {
		switch (contractType) {
		case "130":
			set130View();
			break;
		case "150":
			set150View();
			break;
		case "200":
			set200View();
			break;
		case "230":
			set230and250View();
			break;
		case "250":
			set230and250View();
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
		case "401":
			set401View();
			break;
		case "402":
			set402View();
			break;
		case "403":
			set403View();
			break;
		case "410":
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
		case "510":
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
	}

	private void setDefaultView() {
		otherDataTableElement.getRows().getItem(1).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(3).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(4).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(8).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(9).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(10).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(11).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(12).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(13).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(14).getStyle().setDisplay(Display.NONE);
		
		workProgramDataCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		investCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		interimCauseCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		entrepreneurSupportCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		quoteReductionsCBPanel.getElement().getStyle().setDisplay(Display.NONE);
	}
	
	private void set130View() {
		otherDataTableElement.getRows().getItem(1).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(3).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(4).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(8).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(9).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(10).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(11).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(12).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(13).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(14).getStyle().setDisplay(Display.NONE);
		
		workProgramDataCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		annexedCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		investCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		interimCauseCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		entrepreneurSupportCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		quoteReductionsCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		annexedCBPanel.getElement().getStyle().setDisplay(Display.NONE);
	}
	
	private void set150View() {
		otherDataTableElement.getRows().getItem(1).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(3).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(4).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(8).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(9).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(10).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(11).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(12).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(13).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(14).getStyle().setDisplay(Display.NONE);
		
		workProgramDataCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		annexedCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		investCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		interimCauseCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		quoteReductionsCBPanel.getElement().getStyle().setDisplay(Display.NONE);
	}
	
	private void set200View() {
		otherDataTableElement.getRows().getItem(1).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(3).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(4).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(10).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(11).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(12).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(13).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(14).getStyle().setDisplay(Display.NONE);
		
		workProgramDataCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		investCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		interimCauseCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		entrepreneurSupportCBPanel.getElement().getStyle().setDisplay(Display.NONE);
	}
	
	private void set230and250View() {
		otherDataTableElement.getRows().getItem(1).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(3).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(4).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(10).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(11).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(12).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(13).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(14).getStyle().setDisplay(Display.NONE);
		
		workProgramDataCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		annexedCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		investCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		interimCauseCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		entrepreneurSupportCBPanel.getElement().getStyle().setDisplay(Display.NONE);
	}
	
	private void set300View() {
		otherDataTableElement.getRows().getItem(1).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(3).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(4).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(9).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(10).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(11).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(12).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(13).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(14).getStyle().setDisplay(Display.NONE);
		
		workProgramDataCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		annexedCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		investCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		interimCauseCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		entrepreneurSupportCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		quoteReductionsCBPanel.getElement().getStyle().setDisplay(Display.NONE);
	}
	
	private void set330and350View() {
		otherDataTableElement.getRows().getItem(1).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(3).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(4).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(9).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(10).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(11).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(12).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(13).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(14).getStyle().setDisplay(Display.NONE);
		
		workProgramDataCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		annexedCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		investCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		interimCauseCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		entrepreneurSupportCBPanel.getElement().getStyle().setDisplay(Display.NONE);
	}
	
	private void set401View() {
		otherDataTableElement.getRows().getItem(1).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(3).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(4).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(8).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(9).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(10).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(11).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(12).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(13).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(14).getStyle().setDisplay(Display.NONE);
		
		interimCauseCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		contractReliefCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		entrepreneurSupportCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		promotionMeasuresCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		quoteReductionsCBPanel.getElement().getStyle().setDisplay(Display.NONE);
	}
	
	private void set402View() {
		otherDataTableElement.getRows().getItem(1).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(3).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(4).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(8).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(9).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(10).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(11).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(12).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(13).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(14).getStyle().setDisplay(Display.NONE);
		
		interimCauseCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		contractReliefCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		workshopSchoolCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		investCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		interimCauseCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		entrepreneurSupportCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		promotionMeasuresCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		quoteReductionsCBPanel.getElement().getStyle().setDisplay(Display.NONE);
	}
	
	private void set403View() {
		otherDataTableElement.getRows().getItem(1).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(3).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(4).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(8).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(9).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(10).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(11).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(12).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(13).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(14).getStyle().setDisplay(Display.NONE);
		
		interimCauseCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		contractReliefCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		workshopSchoolCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		investCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		interimCauseCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		entrepreneurSupportCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		promotionMeasuresCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		quoteReductionsCBPanel.getElement().getStyle().setDisplay(Display.NONE);
	}
	
	private void set410View() {
		otherDataTableElement.getRows().getItem(1).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(3).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(4).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(8).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(9).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(10).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(11).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(12).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(13).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(14).getStyle().setDisplay(Display.NONE);
		
		contractReliefCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		annexedCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		investCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		entrepreneurSupportCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		promotionMeasuresCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		quoteReductionsCBPanel.getElement().getStyle().setDisplay(Display.NONE);
	}
	
	private void set420View() {
		otherDataTableElement.getRows().getItem(1).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(8).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(9).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(10).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(11).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(12).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(13).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(14).getStyle().setDisplay(Display.NONE);
		
		contractReliefCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		annexedCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		quoteReductionsCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		interimCauseCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		entrepreneurSupportCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		promotionMeasuresCBPanel.getElement().getStyle().setDisplay(Display.NONE);
	}
	
	private void set421View() {
		otherDataTableElement.getRows().getItem(1).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(3).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(4).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(8).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(9).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(12).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(14).getStyle().setDisplay(Display.NONE);
		
		contractReliefCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		annexedCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		entrepreneurSupportCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		promotionMeasuresCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		investCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		interimCauseCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		quoteReductionsCB.getElement().getStyle().setDisplay(Display.NONE);
	}
	
	private void set430View() {
		otherDataTableElement.getRows().getItem(1).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(3).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(4).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(8).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(9).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(10).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(11).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(12).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(13).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(14).getStyle().setDisplay(Display.NONE);
		
		contractReliefCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		annexedCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		investCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		interimCauseCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		quoteReductionsCBPanel.getElement().getStyle().setDisplay(Display.NONE);
	}
	
	private void set441View() {
		otherDataTableElement.getRows().getItem(1).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(3).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(4).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(8).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(9).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(10).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(11).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(12).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(13).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(14).getStyle().setDisplay(Display.NONE);
		
		workProgramDataCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		annexedCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		investCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		interimCauseCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		entrepreneurSupportCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		promotionMeasuresCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		quoteReductionsCBPanel.getElement().getStyle().setDisplay(Display.NONE);
	}
	
	private void set450View() {
		otherDataTableElement.getRows().getItem(1).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(3).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(8).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(9).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(10).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(11).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(12).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(13).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(14).getStyle().setDisplay(Display.NONE);
		
		contractReliefCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		annexedCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		investCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		entrepreneurSupportCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		promotionMeasuresCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		quoteReductionsCB.getElement().getStyle().setDisplay(Display.NONE);
	}
	
	private void set452View() {
		otherDataTableElement.getRows().getItem(1).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(3).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(4).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(8).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(9).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(10).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(11).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(12).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(13).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(14).getStyle().setDisplay(Display.NONE);
		
		contractReliefCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		annexedCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		investCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		interimCauseCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		entrepreneurSupportCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		promotionMeasuresCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		quoteReductionsCBPanel.getElement().getStyle().setDisplay(Display.NONE);
	}
	
	private void set501View() {
		otherDataTableElement.getRows().getItem(1).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(3).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(4).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(8).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(9).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(10).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(11).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(12).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(13).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(14).getStyle().setDisplay(Display.NONE);
		
		interimCauseCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		contractReliefCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		entrepreneurSupportCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		promotionMeasuresCBPanel.getElement().getStyle().setDisplay(Display.NONE);
	}
	
	private void set502View() {
		otherDataTableElement.getRows().getItem(1).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(3).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(4).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(8).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(9).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(10).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(11).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(12).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(13).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(14).getStyle().setDisplay(Display.NONE);
		
		workProgramDataCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		interimCauseCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		contractReliefCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		entrepreneurSupportCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		promotionMeasuresCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		investCBPanel.getElement().getStyle().setDisplay(Display.NONE);
	}
	
	private void set503View() {
		otherDataTableElement.getRows().getItem(1).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(3).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(4).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(8).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(9).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(10).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(11).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(12).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(13).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(14).getStyle().setDisplay(Display.NONE);
		
		contractReliefCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		workshopSchoolCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		annexedCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		investCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		interimCauseCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		entrepreneurSupportCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		promotionMeasuresCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		quoteReductionsCBPanel.getElement().getStyle().setDisplay(Display.NONE);
	}
	
	private void set510View() {
		otherDataTableElement.getRows().getItem(1).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(3).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(4).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(8).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(9).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(10).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(11).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(12).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(13).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(14).getStyle().setDisplay(Display.NONE);
		
		contractReliefCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		annexedCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		investCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		entrepreneurSupportCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		promotionMeasuresCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		quoteReductionsCBPanel.getElement().getStyle().setDisplay(Display.NONE);
	}
	
	private void set520View() {
		otherDataTableElement.getRows().getItem(1).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(8).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(9).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(10).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(11).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(12).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(13).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(14).getStyle().setDisplay(Display.NONE);
		
		contractReliefCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		annexedCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		investCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		interimCauseCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		entrepreneurSupportCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		promotionMeasuresCBPanel.getElement().getStyle().setDisplay(Display.NONE);
	}

	private void set530View() {
		otherDataTableElement.getRows().getItem(1).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(3).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(4).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(8).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(9).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(10).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(11).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(12).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(13).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(14).getStyle().setDisplay(Display.NONE);
		
		contractReliefCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		annexedCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		investCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		interimCauseCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		promotionMeasuresCBPanel.getElement().getStyle().setDisplay(Display.NONE);
	}
	
	private void set540View() {
		otherDataTableElement.getRows().getItem(1).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(3).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(4).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(8).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(9).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(10).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(11).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(12).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(13).getStyle().setDisplay(Display.NONE);
		
		workProgramDataCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		contractReliefCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		annexedCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		investCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		interimCauseCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		entrepreneurSupportCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		promotionMeasuresCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		quoteReductionsCBPanel.getElement().getStyle().setDisplay(Display.NONE);
	}
	
	private void set541View() {
		otherDataTableElement.getRows().getItem(1).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(3).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(4).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(8).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(9).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(10).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(11).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(12).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(13).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(14).getStyle().setDisplay(Display.NONE);
		
		workProgramDataCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		promotionMeasuresCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		annexedCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		investCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		interimCauseCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		entrepreneurSupportCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		promotionMeasuresCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		quoteReductionsCBPanel.getElement().getStyle().setDisplay(Display.NONE);
	}
	
	private void set550View() {
		otherDataTableElement.getRows().getItem(1).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(3).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(8).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(9).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(10).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(11).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(12).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(13).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(14).getStyle().setDisplay(Display.NONE);
		
		contractReliefCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		annexedCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		investCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		entrepreneurSupportCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		promotionMeasuresCBPanel.getElement().getStyle().setDisplay(Display.NONE);
	}
	
	private void set552View() {
		otherDataTableElement.getRows().getItem(1).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(3).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(4).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(8).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(9).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(10).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(11).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(12).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(13).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(14).getStyle().setDisplay(Display.NONE);
		
		contractReliefCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		workshopSchoolCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		annexedCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		investCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		interimCauseCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		entrepreneurSupportCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		promotionMeasuresCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		quoteReductionsCBPanel.getElement().getStyle().setDisplay(Display.NONE);
	}
	
	private void set970and990View() {
		otherDataTableElement.getRows().getItem(1).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(3).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(4).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(8).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(9).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(10).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(11).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(12).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(13).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(14).getStyle().setDisplay(Display.NONE);
		
		contractReliefCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		investCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		interimCauseCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		entrepreneurSupportCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		promotionMeasuresCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		quoteReductionsCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		annexedCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		older52CBPanel.getElement().getStyle().setDisplay(Display.NONE);
	}
	
	private void set980View() {
		otherDataTableElement.getRows().getItem(1).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(3).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(4).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(8).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(9).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(10).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(11).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(12).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(13).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(14).getStyle().setDisplay(Display.NONE);
		
		workProgramDataCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		contractReliefCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		investCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		interimCauseCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		entrepreneurSupportCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		promotionMeasuresCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		quoteReductionsCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		annexedCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		older52CBPanel.getElement().getStyle().setDisplay(Display.NONE);
	}
	
	private void fillSpecificData() {
		String codeCNO = this.employeeContractInfo.getContractSpecificData().getCno();
		CNO cnoObj = cnoMap.get(codeCNO);
		if(null != cnoObj)
			cnoSB.setText(codeCNO + " - " + cnoObj.getTitle());
		
		calendarFormativeStartDate.setValue(this.employeeContractInfo.getContractSpecificData().getCalendarFormativeStartDate());
		calendarFormativeEndDate.setValue(this.employeeContractInfo.getContractSpecificData().getCalendarFormativeEndDate());
		setSelectedValueLB(formativeLevelLB, this.employeeContractInfo.getContractSpecificData().getFormativeLevel());
		Map<String, String> academicTitulations = AcademicTitulation.getAcademicTitulations(this.employeeContractInfo.getContractSpecificData().getFormativeLevel());
		createAcademicTitulationLB(academicTitulations);
		setSelectedValueLB(academicTitulationLB, this.employeeContractInfo.getContractSpecificData().getAcademicTitulation());
		profesionalityCB.setValue(this.employeeContractInfo.getContractSpecificData().getProfesionality());
		setSelectedValueLB(signBasicCopyLB, this.employeeContractInfo.getContractSpecificData().getSignBasicCopy());
		basicCopyTA.setValue(this.employeeContractInfo.getContractSpecificData().getBasicCopy());
		useEnterpriseFreeTB.setText(this.employeeContractInfo.getContractSpecificData().getUseEnterpriseFree());
		agreementHoursTB.setText(this.employeeContractInfo.getContractSpecificData().getAgreementHours());
		agreementMinutesTB.setText(this.employeeContractInfo.getContractSpecificData().getAgreementMinutes());
		repeatFDCB.setValue(this.employeeContractInfo.getContractSpecificData().getRepeatFD());
		setSelectedValueLB(journeyTypeLB, this.employeeContractInfo.getContractSpecificData().getJourneyType());
		journeyDurationHoursTB.setText(this.employeeContractInfo.getContractSpecificData().getJourneyDurationHours());
		journeyDurationMinutesTB.setText(this.employeeContractInfo.getContractSpecificData().getJourneyDurationMinutes());
		teoricFormationYesRB.setValue(this.employeeContractInfo.getContractSpecificData().getTeoricFormation());
//		teoricFormationYesRB.setValue(this.employeeContractInfo.getContractSpecificData().getTeoricFormationYes());
//		teoricFormationNoRB.setValue(this.employeeContractInfo.getContractSpecificData().getTeoricFormationNo());
		formationHoursTB.setText(this.employeeContractInfo.getContractSpecificData().getFormationHours());
		formationMinutesTB.setText(this.employeeContractInfo.getContractSpecificData().getFormationMinutes());
		retirementPercentTB.setText(this.employeeContractInfo.getContractSpecificData().getRetirementPercent());
		
		//WorkProgramDataTable
		if(this.employeeContractInfo.getContractSpecificData().getWorkProgramData()) {
			showWorkProgramDataTable();
			workProgramDataCB.setValue(true);
			setSelectedValueLB(workProgramLB, this.employeeContractInfo.getContractSpecificData().getWorkProgram());	
		}
		
		//TemporalWorkEnterpriseDataTable
		if(this.employeeContractInfo.getContractSpecificData().getTemporalWorkEnterprise()){
			showTemporalWorkEnterpriseDataTable();
			temporalWorkEnterpriseCB.setValue(true);
			nifTB.setText(this.employeeContractInfo.getContractSpecificData().getNif());
			socialReasonTB.setText(this.employeeContractInfo.getContractSpecificData().getSocialReason());
			contractTemplateCB.setValue(this.employeeContractInfo.getContractSpecificData().getContractTemplate());
			foreignEnterpriseCB.setValue(this.employeeContractInfo.getContractSpecificData().getForeignEnterprise());
		}
		
		//ContractReliefDataTable
		if(this.employeeContractInfo.getContractSpecificData().getContractRelief()){
			showContractReliefDataTable();
			contractReliefCB.setValue(true);
			setSelectedValueLB(reliefEmployeeLB, this.employeeContractInfo.getContractSpecificData().getReliefEmployee());	
			retirementNameTB.setText(this.employeeContractInfo.getContractSpecificData().getRetirementName());
			retirementSurnameTB.setText(this.employeeContractInfo.getContractSpecificData().getRetirementSurname());
			retirementSurname2TB.setText(this.employeeContractInfo.getContractSpecificData().getRetirementSurname2());
		}
		
		//OfferWorkDataTable
		if(this.employeeContractInfo.getContractSpecificData().getOfferWorkData()) {
			showOfferWorkDataTable();
			offerWorkDataCB.setValue(true);
			offerTB.setText(this.employeeContractInfo.getContractSpecificData().getOffer());
		}
		
		//WorkshopSchoolDataTable
		if(this.employeeContractInfo.getContractSpecificData().getWorkshopSchoolB()) {
			showWorkshopSchoolDataTable();
			workshopSchoolCB.setValue(true);
			setSelectedValueLB(workshopSchoolLB, this.employeeContractInfo.getContractSpecificData().getWorkshopSchool());	
		}
		
		//DisabilityDataTable
		if(this.employeeContractInfo.getContractSpecificData().getDisabilityB()) {
			showDisabilityDataTable();
			disabilityCB.setValue(true);
			setSelectedValueLB(disabilityLB, this.employeeContractInfo.getContractSpecificData().getDisability());	
		}
		
		//AnnexedDataTable
		if(this.employeeContractInfo.getContractSpecificData().getAnnexedB()) {
			showAnnexedDataTable();
			annexedCB.setValue(true);
			if(this.employeeContractInfo.getContractSpecificData().getAnnexed())
				annexedRB.setValue(true);
			else
				annexed2RB.setValue(true);
			sourceYearTB.setValue(this.employeeContractInfo.getContractSpecificData().getSourceYear());
		}

		//CampaignsDataTable
		if(this.employeeContractInfo.getContractSpecificData().getCampaigns()) {
			showCampaignsDataTable();
			campaignsCB.setValue(true);
			cpCampaignTB.setValue(this.employeeContractInfo.getContractSpecificData().getCpCampaign());
			codeCampaignTB.setValue(this.employeeContractInfo.getContractSpecificData().getCodeCampaign());
			yearCampaignTB.setValue(this.employeeContractInfo.getContractSpecificData().getYearCampaign());
		}
		
		//InvestDataTable
		if(this.employeeContractInfo.getContractSpecificData().getInvest()) {
			showInvestDataTable();
			investCB.setValue(true);
			setSelectedValueLB(employerLB, this.employeeContractInfo.getContractSpecificData().getEmployer());	
			setSelectedValueLB(employeeLB, this.employeeContractInfo.getContractSpecificData().getEmployee());	
			researcherCB.setValue(this.employeeContractInfo.getContractSpecificData().getResearcher());
		}

		//InterimCauseDataTable
		if(this.employeeContractInfo.getContractSpecificData().getIsInterimCause()) {
			showInterimCauseDataTable();
			interimCauseCB.setValue(true);
			setSelectedValueLB(interimCauseLB, this.employeeContractInfo.getContractSpecificData().getInterimCause());	
		}
		
		//EntrepreneurSupportDataTable
		if(this.employeeContractInfo.getContractSpecificData().getEntrepreneurSupport()) {
			showEntrepreneurSupportDataTable();
			entrepreneurSupportCB.setValue(true);
			setSelectedValueLB(bonusColectiveLB, this.employeeContractInfo.getContractSpecificData().getBonusColective());
			freelanceEmployeerCB.setValue(this.employeeContractInfo.getContractSpecificData().getFreelanceEmployeer());
		}

		//PromotionMeasuresDataTable
		if(this.employeeContractInfo.getContractSpecificData().getPromotionMeasures()) {
			showPromotionMeasuresDataTable();
			promotionMeasuresCB.setValue(true);
			promotionPermanentHiringCB.setValue(this.employeeContractInfo.getContractSpecificData().getPromotionPermanentHiring());
		}

		//QuoteReductionsDataTable
		if(this.employeeContractInfo.getContractSpecificData().getQuoteReductions()) {
			showQuoteReductionsDataTable();
			quoteReductionsCB.setValue(true);
			setSelectedValueLB(reductionColectiveLB, this.employeeContractInfo.getContractSpecificData().getReductionColective());
			if(null != this.employeeContractInfo.getContractSpecificData().getQuoteReduction() && this.employeeContractInfo.getContractSpecificData().getQuoteReduction())
				quoteReductionRB.setValue(true);
			else
				quoteReduction2RB.setValue(true);
			journeyPercentTB.setValue(this.employeeContractInfo.getContractSpecificData().getJourneyPercent());
		}	
	}

}
