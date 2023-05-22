package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;

import com.esferalia.aon.gwt.common.client.widget.DateBoxEx;
import com.esferalia.aon.gwt.payroll.shared.AcademicTitulation;
import com.esferalia.aon.gwt.payroll.shared.CNO;
import com.esferalia.aon.gwt.payroll.shared.EmployeeContractInfo;
import com.esferalia.aon.gwt.payroll.shared.FormativeLevel;
import com.esferalia.aon.occam.api.model.type.ContractType;
import com.esferalia.aon.occam.api.model.type.ContractType.ContractTypeRecord;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.TableElement;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Timer;
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

public abstract class ContractSpecificData extends ResizeComposite {

	// -------------------------------------------------- UiBinder --------------------------------------------------

	private static ContractSpecificDataUiBinder uiBinder = GWT.create(ContractSpecificDataUiBinder.class);

	interface ContractSpecificDataUiBinder extends UiBinder<Widget, ContractSpecificData> {}

	// -------------------------------------------------- UiFields --------------------------------------------------

	@UiField
	MyStyle style;
	
	interface MyStyle extends CssResource {
		String maxWidthTB();
	}
	
	@UiField
	SuggestBox cnoSB;
	
	@UiField
	HTMLPanel idePanel;
	
	@UiField
	DateBoxEx comunicationDateBx;
	
	@UiField
	VerticalPanel ideTransformTable;
	
	@UiField
	HTMLPanel ideTransformPanel;
	
	@UiField
	DateBoxEx comunicationTransformDateBx;
	
	@UiField
	VerticalPanel ideExtensionTable;
	
	@UiField
	ListBox extensionsLB;
	
	@UiField
	HTMLPanel ideExtensionPanel;
	
	@UiField
	DateBoxEx comunicationExtensionDateBx;
	
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
	CheckBox discCB;
	
	@UiField
	ListBox discReasonLB;
	
	@UiField
	CheckBox trueDateCB;
	
	@UiField
	CheckBox planRecoveryCB;
	
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
	HTMLPanel bonusCBPanel;
	
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
	CheckBox bonusCB;
	
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
	ListBox bonusColectiveDisabilityLB;
	
	@UiField
	VerticalPanel older52DataTable;
	
	@UiField
	ListBox older52LB;
	
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
	
	@UiField
	VerticalPanel bonusDataTable;
	
	@UiField
	ListBox bonusLB;
	
	// ------------------------------------------------------ Constructor ---------------------------------------------------------

	private DomainEnterprisesServiceAsync impl = DomainEnterprisesServiceAsync.newInstance();
	private DomainEmployeesServiceAsync implEmployee = DomainEmployeesServiceAsync.newInstance();
	private com.esferalia.aon.gwt.payroll.shared.ContractSpecificData contractSpecificData;
	private EmployeeContractInfo contractEmployeeInfo;
	private Map<String, CNO> cnoMap;
	private TextBox ideTB;
	private TextBox ideTransformTB;
	private TextBox ideExtensionTB;
	private boolean isComunica;
	private boolean isTransform;
	private boolean isExtension;
	
	private FormativeLevel formativeLevel = new FormativeLevel();
	
	protected ContractSpecificData() {
		initWidget(uiBinder.createAndBindUi(this));
		cnoMap = new HashMap<>();
		initializeView();
	}
	
	public void setEmployeeContractInfo(EmployeeContractInfo contractEmployeeInfo, boolean isComunica, Consumer<Void> finish) {
		this.contractEmployeeInfo = contractEmployeeInfo;
		this.isComunica = isComunica;
		
		ContractTypeRecord contractTypeRecord = null;
		try {
			ContractType contractType = new ContractType();
			contractTypeRecord = contractType.getContractType(Integer.parseInt(contractEmployeeInfo.getContractInfo().getContractType()));
		} catch (Exception e) {
			// Nothing to do here
		}
		
		this.isTransform = null == contractTypeRecord ? false : contractTypeRecord.isTransform();
		this.isExtension = contractEmployeeInfo.getContractInfo().isHasExtension();
		
		resetView();
		
		setDefaultView(contractEmployeeInfo.getContractInfo().getContractType());
		reloadSepeData(finish);
	}
	
	private void reloadSepeData(Consumer<Void> finis) {
		showLoadingMessage("Cargando datos Sepe del contrato...");
		getContractSpecificData(contractSpecificDataIn -> {
			contractSpecificData = contractSpecificDataIn;
			createUpdateSepeInfo(
					isComunica, 
					contractEmployeeInfo.getEmployeeInfo().getDocument(),
					contractEmployeeInfo.getContractInfo().getStartDate(),
					contractEmployeeInfo.getContractInfo().getContractId());
			createUpdateTransformSepeInfo(
					isTransform,
					isComunica, 
					contractEmployeeInfo.getEmployeeInfo().getDocument(),
					contractEmployeeInfo.getContractInfo().getEnterpriseCIF(),
					contractEmployeeInfo.getContractInfo().getOriginalStartDate(),
					contractEmployeeInfo.getContractInfo().getSepeId(),
					contractEmployeeInfo.getContractInfo().getContractId());
			createUpdateExtensionSepeInfo(
					isExtension,
					isComunica, 
					contractEmployeeInfo.getEmployeeInfo().getDocument(),
					contractEmployeeInfo.getContractInfo().getEnterpriseCIF(),
					contractEmployeeInfo.getContractInfo().getOriginalStartDate(),
					contractEmployeeInfo.getContractInfo().getSepeExtensionId(),
					contractEmployeeInfo.getContractInfo().getContractId());
			fillSpecificData();
			hideMessagePanel();
			finis.accept(null);
		}, f -> showErrorMessage("Datos Sepe", f.getMessage()));
	}

	private void createUpdateSepeInfo(boolean isComunica, String document, Date fini, Integer contractId) {
		// Reiniciar el boton por que se estaban acumulando los click handler
		idePanel.clear();
		this.ideTB = new TextBox();
		this.ideTB.addStyleName(style.maxWidthTB());
		this.ideTB.addChangeHandler(e -> contractSpecificData.setIde(ideTB.getValue()));
		idePanel.add(ideTB);
	}
	
	private void createUpdateTransformSepeInfo(boolean isTransform, boolean isComunica, String document, String enterpriseCif, Date originalStartDate, String sepeId, Integer contractId) {
		if(Boolean.FALSE.equals(isTransform) && AonStringUtils.isBlank(contractSpecificData.getTransformIde())) {
			ideTransformTable.setVisible(false);
			return;
		}
		
		// Reiniciar el boton por que se estaban acumulando los click handler
		ideTransformTable.setVisible(true);
		ideTransformPanel.clear();
		this.ideTransformTB = new TextBox();
		this.ideTransformTB.addStyleName(style.maxWidthTB());
		this.ideTransformTB.addChangeHandler(e -> contractSpecificData.setTransformIde(ideTransformTB.getValue()));
		ideTransformPanel.add(ideTransformTB);
	}
	
	private void createUpdateExtensionSepeInfo(boolean isExtension, boolean isComunica, String document, String enterpriseCif, Date originalStartDate, String sepeId, Integer contractId) {
		if(Boolean.FALSE.equals(isExtension) && contractSpecificData.getExtensions().isEmpty()) {
			ideExtensionTable.setVisible(false);
			return;
		}
		
		// Reiniciar el boton por que se estaban acumulando los click handler
		ideExtensionTable.setVisible(true);
		ideExtensionPanel.clear();
		this.ideExtensionTB = new TextBox();
		this.ideExtensionTB.addStyleName(style.maxWidthTB());
		this.ideExtensionTB.addChangeHandler(e -> {
			Date extensionDate = contractSpecificData.getExtensions().get(ideExtensionTB.getValue());
			if(null == extensionDate)
				contractSpecificData.addExtension(ideExtensionTB.getValue(), new Date());
		});
		ideExtensionPanel.add(ideExtensionTB);
	}
	
	// --------------------------------------------------------- Abstract Methods --------------------------------------------------

	protected abstract void showErrorMessage(String title, String message);
	protected abstract void showSuccessMessage(String title, String message);
	protected abstract void showLoadingMessage(String message);
	protected abstract void hideMessagePanel();
	protected abstract void downloadCtoDocument();
	protected abstract void downloadCtoTransformDocument();
	protected abstract void downloadCtoExtensionDocument();
	
	// --------------------------------------------------------- UiHandlers --------------------------------------------------------

	@UiHandler("cnoSB")
	void onCNOSBChange(SelectionEvent<Suggestion> event) {
		String cnoStr = cnoSB.getValue();
		String cno = "";
		if(!AonStringUtils.isBlank(cnoStr))
			cno = cnoStr.split(" -")[0];
		
		this.contractSpecificData.setCno(cno);	
	}
	
//	@UiHandler("ideTB")
//	void onIdeTBChange(ValueChangeEvent<String> event) {
//		this.contractSpecificData.setIde(event.getValue());
//	}
	
	@UiHandler("comunicationDateBx")
	void oncomunicationDateBxChange(ValueChangeEvent<Date> event) {
		this.contractSpecificData.setComunicationDate(event.getValue());
	}
	
	@UiHandler("calendarFormativeStartDate")
	void onCalendarFormativeStartDateChange(ValueChangeEvent<Date> event) {
		this.contractSpecificData.setCalendarFormativeStartDate(event.getValue());	
	}
	
	@UiHandler("calendarFormativeEndDate")
	void onCalendarFormativeEndDateChange(ValueChangeEvent<Date> event) {
		this.contractSpecificData.setCalendarFormativeEndDate(event.getValue());	
	}
	
	@UiHandler("formativeLevelLB")
	void onFormativeLevelLBChange(ChangeEvent event) {
		String formativeLevelValue = formativeLevelLB.getSelectedValue();
		Map<String, String> academicTitulations = AcademicTitulation.getAcademicTitulations(formativeLevelValue);
		createAcademicTitulationLB(academicTitulations);
		
		String academicTitulationValue = academicTitulationLB.getSelectedValue();
		
		this.contractSpecificData.setFormativeLevel(formativeLevelValue);
		this.contractSpecificData.setAcademicTitulation(academicTitulationValue);
	}
	
	@UiHandler("academicTitulationLB")
	void onAcademicTitulationLBChange(ChangeEvent event) {
		String academicTitulationValue = academicTitulationLB.getSelectedValue();
		this.contractSpecificData.setAcademicTitulation(academicTitulationValue);
	}
	
	@UiHandler("profesionalityCB")
	void onProfesionalityCBChange(ValueChangeEvent<Boolean> event) {
		this.contractSpecificData.setProfesionality(event.getValue());
	}
	
	@UiHandler("signBasicCopyLB")
	void onSignBasicCopyLBChange(ChangeEvent event) {
		String signBasicCopyValue = signBasicCopyLB.getSelectedValue();
		this.contractSpecificData.setSignBasicCopy(signBasicCopyValue);
	}
	
	@UiHandler("basicCopyTA")
	void onBasicCopyTAChange(ValueChangeEvent<String> event) {
		this.contractSpecificData.setBasicCopy(event.getValue());
	}
	
	@UiHandler("useEnterpriseFreeTB")
	void onUseEnterpriseFreeTBChange(ValueChangeEvent<String> event) {
		this.contractSpecificData.setUseEnterpriseFree(event.getValue());
	}
	
	@UiHandler("agreementHoursTB")
	void onAgreementHoursTBChange(ValueChangeEvent<String> event) {
		this.contractSpecificData.setAgreementHours(event.getValue());
	}
	
	@UiHandler("agreementMinutesTB")
	void onAgreementMinutesTBChange(ValueChangeEvent<String> event) {
		this.contractSpecificData.setAgreementMinutes(event.getValue());
	}
	
	@UiHandler("repeatFDCB")
	void onRepeatFDCBChange(ValueChangeEvent<Boolean> event) {
		this.contractSpecificData.setRepeatFD(event.getValue());
	}
	
	@UiHandler("journeyTypeLB")
	void onJourneyTypeLBChange(ChangeEvent event) {
		String journeyTypeValue = journeyTypeLB.getSelectedValue();
		this.contractSpecificData.setJourneyType(journeyTypeValue);
	}
	
	@UiHandler("journeyDurationHoursTB")
	void onJourneyDurationHoursTBChange(ValueChangeEvent<String> event) {
		this.contractSpecificData.setJourneyDurationHours(event.getValue());
	}
	
	@UiHandler("journeyDurationMinutesTB")
	void onJourneyDurationMinutesTBChange(ValueChangeEvent<String> event) {
		this.contractSpecificData.setJourneyDurationMinutes(event.getValue());
	}
	
	@UiHandler("teoricFormationYesRB")
	void onTeoricFormationRBChange(ValueChangeEvent<Boolean> event) {
		this.contractSpecificData.setTeoricFormation(event.getValue());
	}
	
	@UiHandler("formationHoursTB")
	void onFormationHoursTBChange(ValueChangeEvent<String> event) {
		this.contractSpecificData.setFormationHours(event.getValue());
	}
	
	@UiHandler("formationMinutesTB")
	void onFormationMinutesTBChange(ValueChangeEvent<String> event) {
		this.contractSpecificData.setFormationMinutes(event.getValue());
	}
	
	@UiHandler("retirementPercentTB")
	void onRetirementPercentTBChange(ValueChangeEvent<String> event) {
		this.contractSpecificData.setRetirementPercent(event.getValue());
	}
	
	@UiHandler("discCB")
	void onDiscCBChange(ValueChangeEvent<Boolean> event) {
		this.contractSpecificData.setDisc(event.getValue());
	}
	
	@UiHandler("discReasonLB")
	void onDiscReasonLBChange(ChangeEvent event) {
		this.contractSpecificData.setDiscReason(discReasonLB.getSelectedValue());
	}
	
	@UiHandler("trueDateCB")
	void onTrueDateCBChange(ValueChangeEvent<Boolean> event) {
		this.contractSpecificData.setTrueDate(event.getValue());
	}
	
	@UiHandler("planRecoveryCB")
	void onPlanRecoveryCBCBChange(ValueChangeEvent<Boolean> event) {
		this.contractSpecificData.setPlanRecovery(event.getValue());
	}
	
	@UiHandler("workProgramDataCB")
	void onWorkProgramDataCBChange(ValueChangeEvent<Boolean> event) {
		if(Boolean.TRUE.equals(event.getValue()))
			showWorkProgramDataTable();
		else {
			resetWorkProgramDataTable();
			hideWorkProgramDataTable();
		}
		
		this.contractSpecificData.setWorkProgramData(event.getValue());
	}
	
	@UiHandler("temporalWorkEnterpriseCB")
	void onTemporalWorkEnterpriseCBChange(ValueChangeEvent<Boolean> event) {
		if(Boolean.TRUE.equals(event.getValue()))
			showTemporalWorkEnterpriseDataTable();
		else {
			resetTemporalWorkEnterpriseDataTable();
			hideTemporalWorkEnterpriseDataTable();
		}
		
		this.contractSpecificData.setTemporalWorkEnterprise(event.getValue());
	}
	
	@UiHandler("contractReliefCB")
	void onContractReliefCBChange(ValueChangeEvent<Boolean> event) {
		if(Boolean.TRUE.equals(event.getValue()))
			showContractReliefDataTable();
		else {
			resetContractReliefDataTable();
			hideContractReliefDataTable();
		}
		
		this.contractSpecificData.setContractRelief(event.getValue());
	}
	
	@UiHandler("offerWorkDataCB")
	void onOfferWorkDataCBChange(ValueChangeEvent<Boolean> event) {
		if(Boolean.TRUE.equals(event.getValue()))
			showOfferWorkDataTable();
		else {
			resetOfferWorkDataTable();
			hideOfferWorkDataTable();
		}
		
		this.contractSpecificData.setOfferWorkData(event.getValue());
	}
	
	@UiHandler("workshopSchoolCB")
	void onWorkshopSchoolCBChange(ValueChangeEvent<Boolean> event) {
		if(Boolean.TRUE.equals(event.getValue()))
			showWorkshopSchoolDataTable();
		else {
			resetWorkshopSchoolDataTable();
			hideWorkshopSchoolDataTable();
		}
		
		this.contractSpecificData.setWorkshopSchoolB(event.getValue());
	}
	
	@UiHandler("disabilityCB")
	void onDisabilityCBChange(ValueChangeEvent<Boolean> event) {
		if(Boolean.TRUE.equals(event.getValue()))
			showDisabilityDataTable();
		else {
			resetDisabilityDataTable();
			hideDisabilityDataTable();
		}
		
		this.contractSpecificData.setDisabilityB(event.getValue());
	}
	
	@UiHandler("older52CB")
	void onOlder52CBChange(ValueChangeEvent<Boolean> event) {
		if(Boolean.TRUE.equals(event.getValue()))
			showOlder52DataTable();
		else {
			resetOlder52DataTable();
			hideOlder52DataTable();
		}
		
		this.contractSpecificData.setOlderThan52(event.getValue());
	}
	
	@UiHandler("annexedCB")
	void onAnnexedCBChange(ValueChangeEvent<Boolean> event) {
		if(Boolean.TRUE.equals(event.getValue()))
			showAnnexedDataTable();
		else {
			resetAnnexedDataTable();
			hideAnnexedDataTable();
		}
		
		this.contractSpecificData.setAnnexedB(event.getValue());
	}
	
	@UiHandler("campaignsCB")
	void onCampaignsCBChange(ValueChangeEvent<Boolean> event) {
		if(Boolean.TRUE.equals(event.getValue()))
			showCampaignsDataTable();
		else {
			resetCampaignsDataTable();
			hideCampaignsDataTable();
		}
		
		this.contractSpecificData.setCampaigns(event.getValue());
	}
	
	@UiHandler("investCB")
	void onInvestCBChange(ValueChangeEvent<Boolean> event) {
		if(Boolean.TRUE.equals(event.getValue()))
			showInvestDataTable();
		else {
			resetInvestDataTable();
			hideInvestDataTable();
		}
		
		this.contractSpecificData.setInvest(event.getValue());
	}
	
	@UiHandler("interimCauseCB")
	void onInterimCauseCBChange(ValueChangeEvent<Boolean> event) {
		if(Boolean.TRUE.equals(event.getValue())) {
			showInterimCauseDataTable();
			DomEvent.fireNativeEvent(Document.get().createChangeEvent(), interimCauseLB);
		} else {
			resetInterimCauseDataTable();
			hideInterimCauseDataTable();
			this.contractSpecificData.setInterimCause(null);
		}
		
		this.contractSpecificData.setIsInterimCause(event.getValue());
	}
	
	@UiHandler("entrepreneurSupportCB")
	void onEntrepreneurSupportCBChange(ValueChangeEvent<Boolean> event) {
		if(Boolean.TRUE.equals(event.getValue()))
			showEntrepreneurSupportDataTable();
		else {
			resetEntrepreneurSupportDataTable();
			hideEntrepreneurSupportDataTable();
		}
		
		this.contractSpecificData.setEntrepreneurSupport(event.getValue());
	}
	
	@UiHandler("promotionMeasuresCB")
	void onPromotionMeasuresCBChange(ValueChangeEvent<Boolean> event) {
		if(Boolean.TRUE.equals(event.getValue()))
			showPromotionMeasuresDataTable();
		else {
			resetPromotionMeasuresDataTable();
			hidePromotionMeasuresDataTable();
		}
		
		this.contractSpecificData.setPromotionMeasures(event.getValue());
	}
	
	@UiHandler("quoteReductionsCB")
	void onQuoteReductionsCBChange(ValueChangeEvent<Boolean> event) {
		if(Boolean.TRUE.equals(event.getValue()))
			showQuoteReductionsDataTable();
		else {
			resetQuoteReductionsDataTable();
			hideQuoteReductionsDataTable();
		}
		
		this.contractSpecificData.setQuoteReductions(event.getValue());
	}
	
	@UiHandler("bonusCB")
	void onBonusCBChange(ValueChangeEvent<Boolean> event) {
		if(Boolean.TRUE.equals(event.getValue()))
			showBonusDataTable();
		else {
			resetBonusDataTable();
			hideBonusDataTable();
		}
		
		this.contractSpecificData.setBonus(event.getValue());
	}
	
	@UiHandler("bonusLB")
	void onBonusLBChange(ChangeEvent event) {
		String bonusColectiveValue = bonusLB.getSelectedValue();
		this.contractSpecificData.setBonusType(bonusColectiveValue);
	}
	
	@UiHandler("workProgramLB")
	void onWorkProgramLBChange(ChangeEvent event) {
		String workProgramValue = workProgramLB.getSelectedValue();
		this.contractSpecificData.setWorkProgram(workProgramValue);
	}
	
	@UiHandler("nifTB")
	void onNifTBChange(ValueChangeEvent<String> event) {
		this.contractSpecificData.setNif(event.getValue());
	}
	
	@UiHandler("socialReasonTB")
	void onSocialReasonTBChange(ValueChangeEvent<String> event) {
		this.contractSpecificData.setSocialReason(event.getValue());
	}
	
	@UiHandler("contractTemplateCB")
	void onContractTemplateCBChange(ValueChangeEvent<Boolean> event) {
		this.contractSpecificData.setContractTemplate(event.getValue());
	}
	
	@UiHandler("foreignEnterpriseCB")
	void onForeignEnterpriseCBChange(ValueChangeEvent<Boolean> event) {
		this.contractSpecificData.setForeignEnterprise(event.getValue());
	}
	
	@UiHandler("reliefEmployeeLB")
	void onReliefEmployeeLBChange(ChangeEvent event) {
		String reliefEmployeeValue = reliefEmployeeLB.getSelectedValue();
		this.contractSpecificData.setReliefEmployee(reliefEmployeeValue);
	}
	
	@UiHandler("retirementNameTB")
	void onRetirementNameTBChange(ValueChangeEvent<String> event) {
		this.contractSpecificData.setRetirementName(event.getValue());
	}
	
	@UiHandler("retirementSurnameTB")
	void onRetirementSurnameTBChange(ValueChangeEvent<String> event) {
		this.contractSpecificData.setRetirementSurname(event.getValue());
	}
	
	@UiHandler("retirementSurname2TB")
	void onRetirementSurname2TBChange(ValueChangeEvent<String> event) {
		this.contractSpecificData.setRetirementSurname2(event.getValue());
	}
	
	@UiHandler("offerTB")
	void onOfferTBChange(ValueChangeEvent<String> event) {
		this.contractSpecificData.setOffer(event.getValue());
	}
	
	@UiHandler("workshopSchoolLB")
	void onWorkshopSchoolLBChange(ChangeEvent event) {
		String workshopSchoolValue = workshopSchoolLB.getSelectedValue();
		this.contractSpecificData.setWorkshopSchool(workshopSchoolValue);
	}
	
	@UiHandler("disabilityLB")
	void onDisabilityLBChange(ChangeEvent event) {
		String disabilityValue = disabilityLB.getSelectedValue();
		this.contractSpecificData.setDisability(disabilityValue);
	}
	
	@UiHandler("bonusColectiveDisabilityLB")
	void onBonusColectiveDisabilityLBChange(ChangeEvent event) {
		String bonusColectiveValue = bonusColectiveDisabilityLB.getSelectedValue();
		this.contractSpecificData.setBonusColective(bonusColectiveValue);
	}
	
	@UiHandler("older52LB")
	void onOlder52LBChange(ChangeEvent event) {
		String older52 = older52LB.getSelectedValue();
		this.contractSpecificData.setOtherLegislations(older52);
	}
	
	@UiHandler("annexedRB")
	void onAnnexedRBChange(ValueChangeEvent<Boolean> event) {
		if(Boolean.TRUE.equals(event.getValue()))
			this.contractSpecificData.setAnnexed(true);
	}
	
	@UiHandler("annexed2RB")
	void onAnnexed2RBChange(ValueChangeEvent<Boolean> event) {
		if(Boolean.TRUE.equals(event.getValue()))
			this.contractSpecificData.setAnnexed(false);
	}
	
	@UiHandler("sourceYearTB")
	void onSourceYearTBChange(ValueChangeEvent<String> event) {
		this.contractSpecificData.setSourceYear(event.getValue());
	}
	
	@UiHandler("cpCampaignTB")
	void onCpCampaignTBChange(ValueChangeEvent<String> event) {
		this.contractSpecificData.setCpCampaign(event.getValue());
	}
	
	@UiHandler("codeCampaignTB")
	void onCodeCampaignTBChange(ValueChangeEvent<String> event) {
		this.contractSpecificData.setCodeCampaign(event.getValue());
	}
	
	@UiHandler("yearCampaignTB")
	void onYearCampaignTBChange(ValueChangeEvent<String> event) {
		this.contractSpecificData.setYearCampaign(event.getValue());
	}
	
	@UiHandler("employerLB")
	void onEmployerLBChange(ChangeEvent event) {
		String employerValue = employerLB.getSelectedValue();
		this.contractSpecificData.setEmployer(employerValue);
	}
	
	@UiHandler("employeeLB")
	void onEmployeeLBChange(ChangeEvent event) {
		String employeeValue = employeeLB.getSelectedValue();
		this.contractSpecificData.setEmployee(employeeValue);
	}
	
	@UiHandler("researcherCB")
	void onResearcherCBChange(ValueChangeEvent<Boolean> event) {
		this.contractSpecificData.setResearcher(event.getValue());
	}
	
	@UiHandler("interimCauseLB")
	void onInterimCauseLBChange(ChangeEvent event) {
		String interimCauseValue = interimCauseLB.getSelectedValue();
		this.contractSpecificData.setInterimCause(interimCauseValue);
	}
	
	@UiHandler("bonusColectiveLB")
	void onBonusColectiveLBChange(ChangeEvent event) {
		String bonusColectiveValue = bonusColectiveLB.getSelectedValue();
		this.contractSpecificData.setBonusColective(bonusColectiveValue);
	}
	
	@UiHandler("freelanceEmployeerCB")
	void onFreelanceEmployeerCBChange(ValueChangeEvent<Boolean> event) {
		this.contractSpecificData.setFreelanceEmployeer(event.getValue());
	}
	
	@UiHandler("promotionPermanentHiringCB")
	void onPromotionPermanentHiringCBChange(ValueChangeEvent<Boolean> event) {
		this.contractSpecificData.setPromotionPermanentHiring(event.getValue());
	}
	
	@UiHandler("reductionColectiveLB")
	void onReductionColectiveLBChange(ChangeEvent event) {
		String reductionColectiveValue = reductionColectiveLB.getSelectedValue();
		this.contractSpecificData.setReductionColective(reductionColectiveValue);
	}
	
	@UiHandler("quoteReductionRB")
	void onQuoteReductionRBChange(ValueChangeEvent<Boolean> event) {
		if(Boolean.TRUE.equals(event.getValue()))
			this.contractSpecificData.setQuoteReduction(true);
	}
	
	@UiHandler("quoteReduction2RB")
	void onQuoteReduction2RBChange(ValueChangeEvent<Boolean> event) {
		if(Boolean.TRUE.equals(event.getValue()))
			this.contractSpecificData.setQuoteReduction(false);
	}
	
	@UiHandler("journeyPercentTB")
	void onJourneyPercentTBChange(ValueChangeEvent<String> event) {
		this.contractSpecificData.setJourneyPercent(event.getValue());
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
		workProgramLB.addItem("ESTUDIOS Y CAMPA\u00D1AS", "09");
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
		createBonusColectiveDisability();
	}
	
	private void createBonusColectiveDisability() {
		bonusColectiveDisabilityLB.clear();
		bonusColectiveDisabilityLB.addItem("HOMBRE SIN DISCAPACIDAD SEVERA < 45 A\u00D1OS CON CONTRATO INDEFINIDO", "070");
		bonusColectiveDisabilityLB.addItem("HOMBRE SIN DISCAPACIDAD SEVERA >=45 A\u00D1OS CON CONTRATO INDEFINIDO", "071");
		bonusColectiveDisabilityLB.addItem("HOMBRE CON DISCAPACIDAD SEVERA < 45 A\u00D1OS CON CONTRATO INDEFINIDO", "072");
		bonusColectiveDisabilityLB.addItem("HOMBRE CON DISCAPACIDAD SEVERA >=45 A\u00D1OS CON CONTRATO INDEFINIDO", "073");
		bonusColectiveDisabilityLB.addItem("MUJER SIN DISCAPACIDAD SEVERA < 45 A\u00D1OS CON CONTRATO INDEFINIDO", "074");
		bonusColectiveDisabilityLB.addItem("MUJER SIN DISCAPACIDAD SEVERA >=45 A\u00D1OS CON CONTRATO INDEFINIDO", "075");
		bonusColectiveDisabilityLB.addItem("MUJER CON DISCAPACIDAD SEVERA < 45 A\u00D1OS CON CONTRATO INDEFINIDO", "076");
		bonusColectiveDisabilityLB.addItem("MUJER CON DISCAPACIDAD SEVERA >=45 A\u00D1OS CON CONTRATO INDEFINIDO", "077");
	}
	
	private void showOlder52DataTable() {
		older52DataTable.getElement().getStyle().clearDisplay();
	}
	
	private void hideOlder52DataTable() {
		older52DataTable.getElement().getStyle().setDisplay(Display.NONE);
	}

	private void resetOlder52DataTable() {
		older52LB.clear();
		older52LB.addItem("-", "");
		older52LB.addItem("LEY 45/2002 MAYORES DE 52 PERC.SUB.REASS", "001");
		older52LB.addItem("LEY 45/2002 MAYORES DE 52 PERC.RESTO SUB", "002");
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
	
	private void showBonusDataTable() {
		bonusDataTable.getElement().getStyle().clearDisplay();
	}
	
	private void hideBonusDataTable() {
		bonusDataTable.getElement().getStyle().setDisplay(Display.NONE);
	}

	private void resetBonusDataTable() {
		bonusLB.clear();
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
		for(Entry<String, String> entry: formativeLevel.getFormativeLevelMap().entrySet())
			formativeLevelLB.addItem(entry.getValue(), entry.getKey());
		
		academicTitulationLB.clear();
		
		signBasicCopyLB.clear();
		signBasicCopyLB.addItem("-","");
		signBasicCopyLB.addItem("FIRMADAS POR LOS REPRESENTANTES LEGALES", "1");
		signBasicCopyLB.addItem("NO EXISTE REPRESENTACION LEGAL", "2");
		signBasicCopyLB.addItem("NO SE HA FACILITADO COPIA", "3");
		signBasicCopyLB.addItem("REHUSA FIRMAR", "4");
		
		journeyTypeLB.clear();
		journeyTypeLB.addItem("-","");
		journeyTypeLB.addItem("JORNADA ANUAL","A");
		journeyTypeLB.addItem("JORNADA DIARIA","D");
		journeyTypeLB.addItem("JORNADA MENSUAL","M");
		journeyTypeLB.addItem("JORNADA SEMANAL","S");
		
		discReasonLB.clear();
		discReasonLB.addItem("-", "");
		discReasonLB.addItem("INCAPACIDAD TRANSITORIA", "I");
		discReasonLB.addItem("PRORROGA TACITA", "P");

		
		// CheckBox
		discCB.setValue(false);
		trueDateCB.setValue(false);
		planRecoveryCB.setValue(false);
		profesionalityCB.setValue(false);
		repeatFDCB.setValue(false);
		workProgramDataCB.setValue(false);
		temporalWorkEnterpriseCB.setValue(false);
		offerWorkDataCB.setValue(false);
		workshopSchoolCB.setValue(false);
		disabilityCB.setValue(false);
		older52CB.setValue(false);
		annexedCB.setValue(false);
		older52CB.setValue(false);
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
		journeyDurationHoursTB.setMaxLength(4);
		journeyDurationMinutesTB.setText("");
		journeyDurationMinutesTB.setMaxLength(2);
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
			public void onFailure(Throwable caught) {
				// Nothing to do here
			}

			@Override
			public void onSuccess(Map<String, CNO> result) {
				cnoMap = result;
				
				List<String> cnoEntry = new ArrayList<>();
				for(Entry<String, CNO> entry : cnoMap.entrySet())
					cnoEntry.add(entry.getKey() + " - " + entry.getValue().getTitle());
				List<String> cnoSuggest = new ArrayList<>();
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
		this.cnoSB.setValue("");
		this.comunicationDateBx.setValue(null);
		this.formativeLevelLB.setSelectedIndex(0);
		this.signBasicCopyLB.setSelectedIndex(0);
		this.basicCopyTA.setValue("");
		this.useEnterpriseFreeTB.setValue("");
		
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
		
		// Set journeyType for contracts 300
		createJourneyType(contractType);
		
		// Transform
		showHideTransformRows();
	}

	private void createJourneyType(String contractType) {
		journeyTypeLB.clear();
		journeyTypeLB.addItem("-","");
		
		if(AonStringUtils.equalsIgnoreCase(contractType, "300") || AonStringUtils.equalsIgnoreCase(contractType, "309") ||
				AonStringUtils.equalsIgnoreCase(contractType, "330") || AonStringUtils.equalsIgnoreCase(contractType, "339") ||
						AonStringUtils.equalsIgnoreCase(contractType, "350") || AonStringUtils.equalsIgnoreCase(contractType, "389"))
			journeyTypeLB.addItem("JORNADA ANUAL","A");
		else {
			journeyTypeLB.addItem("JORNADA ANUAL","A");
			journeyTypeLB.addItem("JORNADA MENSUAL","M");
			journeyTypeLB.addItem("JORNADA SEMANAL","S");
			journeyTypeLB.addItem("JORNADA DIARIA","D");
		}
	}
	
	private void resetView() {
		otherDataTableElement.getRows().getItem(1).getStyle().clearDisplay();
		otherDataTableElement.getRows().getItem(2).getStyle().clearDisplay();
		otherDataTableElement.getRows().getItem(3).getStyle().clearDisplay();
		otherDataTableElement.getRows().getItem(4).getStyle().clearDisplay();
		otherDataTableElement.getRows().getItem(5).getStyle().clearDisplay();
		otherDataTableElement.getRows().getItem(6).getStyle().clearDisplay();
		otherDataTableElement.getRows().getItem(7).getStyle().clearDisplay();
		otherDataTableElement.getRows().getItem(8).getStyle().clearDisplay();
		otherDataTableElement.getRows().getItem(9).getStyle().clearDisplay();
		otherDataTableElement.getRows().getItem(10).getStyle().clearDisplay();
		otherDataTableElement.getRows().getItem(11).getStyle().clearDisplay();
		otherDataTableElement.getRows().getItem(12).getStyle().clearDisplay();
		otherDataTableElement.getRows().getItem(13).getStyle().clearDisplay();
		otherDataTableElement.getRows().getItem(14).getStyle().clearDisplay();
		otherDataTableElement.getRows().getItem(15).getStyle().clearDisplay();
		otherDataTableElement.getRows().getItem(16).getStyle().clearDisplay();
		otherDataTableElement.getRows().getItem(17).getStyle().clearDisplay();
		otherDataTableElement.getRows().getItem(18).getStyle().clearDisplay();
		
		workProgramDataCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		investCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		interimCauseCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		entrepreneurSupportCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		quoteReductionsCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		bonusCBPanel.getElement().getStyle().setDisplay(Display.NONE);
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
		otherDataTableElement.getRows().getItem(18).getStyle().setDisplay(Display.NONE);
		
		workProgramDataCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		investCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		interimCauseCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		entrepreneurSupportCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		quoteReductionsCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		bonusCBPanel.getElement().getStyle().setDisplay(Display.NONE);
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
		otherDataTableElement.getRows().getItem(18).getStyle().setDisplay(Display.NONE);
		
		workProgramDataCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		annexedCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		investCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		interimCauseCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		entrepreneurSupportCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		quoteReductionsCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		annexedCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		bonusCBPanel.getElement().getStyle().clearDisplay();
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
		otherDataTableElement.getRows().getItem(18).getStyle().setDisplay(Display.NONE);
		
		workProgramDataCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		annexedCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		investCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		interimCauseCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		quoteReductionsCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		bonusCBPanel.getElement().getStyle().clearDisplay();
	}
	
	private void set200View() {
		otherDataTableElement.getRows().getItem(1).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(3).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(4).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(9).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(12).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(13).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(14).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(18).getStyle().setDisplay(Display.NONE);
		
		workProgramDataCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		investCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		interimCauseCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		entrepreneurSupportCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		bonusCBPanel.getElement().getStyle().setDisplay(Display.NONE);
	}
	
	private void set230and250View() {
		otherDataTableElement.getRows().getItem(1).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(3).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(4).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(12).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(13).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(14).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(18).getStyle().setDisplay(Display.NONE);
		
		workProgramDataCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		annexedCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		investCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		interimCauseCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		entrepreneurSupportCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		bonusCBPanel.getElement().getStyle().clearDisplay();
	}
	
	private void set300View() {
		otherDataTableElement.getRows().getItem(1).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(3).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(4).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(9).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(12).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(13).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(14).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(18).getStyle().setDisplay(Display.NONE);
		
		workProgramDataCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		annexedCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		investCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		interimCauseCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		entrepreneurSupportCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		quoteReductionsCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		bonusCBPanel.getElement().getStyle().setDisplay(Display.NONE);
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
		otherDataTableElement.getRows().getItem(18).getStyle().setDisplay(Display.NONE);
		
		workProgramDataCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		annexedCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		investCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		interimCauseCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		entrepreneurSupportCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		bonusCBPanel.getElement().getStyle().clearDisplay();
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
		otherDataTableElement.getRows().getItem(18).getStyle().setDisplay(Display.NONE);
		
		interimCauseCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		contractReliefCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		entrepreneurSupportCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		promotionMeasuresCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		quoteReductionsCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		bonusCBPanel.getElement().getStyle().setDisplay(Display.NONE);
	}
	
	private void set402View() {
		set402And403view();
	}
	
	private void set403View() {
		set402And403view();
	}
	
	private void set402And403view() {
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
		otherDataTableElement.getRows().getItem(18).getStyle().setDisplay(Display.NONE);
		
		interimCauseCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		contractReliefCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		workshopSchoolCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		investCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		interimCauseCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		entrepreneurSupportCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		promotionMeasuresCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		quoteReductionsCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		bonusCBPanel.getElement().getStyle().setDisplay(Display.NONE);
	}
	
	private void set410View() {
		set410And510View();
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
		otherDataTableElement.getRows().getItem(18).getStyle().clearDisplay();
		
		contractReliefCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		annexedCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		quoteReductionsCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		interimCauseCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		entrepreneurSupportCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		promotionMeasuresCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		bonusCBPanel.getElement().getStyle().setDisplay(Display.NONE);
	}
	
	private void set421View() {
		otherDataTableElement.getRows().getItem(1).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(3).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(4).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(8).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(9).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(12).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(14).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(18).getStyle().setDisplay(Display.NONE);
		
		contractReliefCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		annexedCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		entrepreneurSupportCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		promotionMeasuresCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		investCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		interimCauseCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		quoteReductionsCB.getElement().getStyle().setDisplay(Display.NONE);
		bonusCBPanel.getElement().getStyle().setDisplay(Display.NONE);
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
		otherDataTableElement.getRows().getItem(18).getStyle().setDisplay(Display.NONE);
		
		contractReliefCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		annexedCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		investCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		interimCauseCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		quoteReductionsCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		bonusCBPanel.getElement().getStyle().clearDisplay();
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
		otherDataTableElement.getRows().getItem(18).getStyle().setDisplay(Display.NONE);
		
		workProgramDataCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		annexedCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		investCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		interimCauseCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		entrepreneurSupportCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		promotionMeasuresCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		quoteReductionsCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		bonusCBPanel.getElement().getStyle().setDisplay(Display.NONE);
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
		otherDataTableElement.getRows().getItem(18).getStyle().setDisplay(Display.NONE);
		
		contractReliefCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		annexedCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		investCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		entrepreneurSupportCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		promotionMeasuresCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		quoteReductionsCB.getElement().getStyle().setDisplay(Display.NONE);
		bonusCBPanel.getElement().getStyle().clearDisplay();
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
		otherDataTableElement.getRows().getItem(18).getStyle().setDisplay(Display.NONE);
		
		contractReliefCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		annexedCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		investCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		interimCauseCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		entrepreneurSupportCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		promotionMeasuresCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		quoteReductionsCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		bonusCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		bonusCBPanel.getElement().getStyle().clearDisplay();
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
		otherDataTableElement.getRows().getItem(18).getStyle().setDisplay(Display.NONE);
		
		interimCauseCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		contractReliefCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		entrepreneurSupportCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		promotionMeasuresCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		bonusCBPanel.getElement().getStyle().setDisplay(Display.NONE);
	}
	
	private void set502View() {
		otherDataTableElement.getRows().getItem(1).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(3).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(4).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(8).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(9).getStyle().setDisplay(Display.NONE);
//		otherDataTableElement.getRows().getItem(10).getStyle().setDisplay(Display.NONE);
//		otherDataTableElement.getRows().getItem(11).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(12).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(13).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(14).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(18).getStyle().setDisplay(Display.NONE);
		
		workProgramDataCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		interimCauseCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		contractReliefCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		entrepreneurSupportCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		promotionMeasuresCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		investCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		bonusCBPanel.getElement().getStyle().setDisplay(Display.NONE);
	}
	
	private void set503View() {
		set503And552View();
	}
	
	private void set510View() {
		set410And510View();
	}
	
	private void set410And510View() {
		otherDataTableElement.getRows().getItem(1).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(3).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(4).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(8).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(9).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(12).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(13).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(14).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(18).getStyle().setDisplay(Display.NONE);
		
		contractReliefCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		annexedCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		investCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		interimCauseCBPanel.getElement().getStyle().clearDisplay();
		entrepreneurSupportCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		promotionMeasuresCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		quoteReductionsCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		bonusCBPanel.getElement().getStyle().setDisplay(Display.NONE);
	}
	
	private void set520View() {
		otherDataTableElement.getRows().getItem(1).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(8).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(9).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(12).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(13).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(14).getStyle().setDisplay(Display.NONE);
		otherDataTableElement.getRows().getItem(18).getStyle().setDisplay(Display.NONE);
		
		contractReliefCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		annexedCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		investCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		interimCauseCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		entrepreneurSupportCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		promotionMeasuresCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		bonusCBPanel.getElement().getStyle().setDisplay(Display.NONE);
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
		otherDataTableElement.getRows().getItem(18).getStyle().setDisplay(Display.NONE);
		
		contractReliefCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		annexedCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		investCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		interimCauseCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		promotionMeasuresCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		bonusCBPanel.getElement().getStyle().clearDisplay();
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
		otherDataTableElement.getRows().getItem(18).getStyle().setDisplay(Display.NONE);
		
		workProgramDataCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		contractReliefCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		annexedCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		investCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		interimCauseCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		entrepreneurSupportCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		promotionMeasuresCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		quoteReductionsCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		bonusCBPanel.getElement().getStyle().setDisplay(Display.NONE);
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
		otherDataTableElement.getRows().getItem(18).getStyle().setDisplay(Display.NONE);
		
		workProgramDataCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		promotionMeasuresCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		annexedCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		investCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		interimCauseCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		entrepreneurSupportCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		promotionMeasuresCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		quoteReductionsCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		bonusCBPanel.getElement().getStyle().setDisplay(Display.NONE);
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
		otherDataTableElement.getRows().getItem(18).getStyle().setDisplay(Display.NONE);
		
		contractReliefCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		annexedCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		investCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		entrepreneurSupportCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		promotionMeasuresCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		bonusCBPanel.getElement().getStyle().clearDisplay();
	}
	
	private void set552View() {
		set503And552View();
	}
	
	private void set503And552View() {
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
		otherDataTableElement.getRows().getItem(18).getStyle().setDisplay(Display.NONE);
		
		contractReliefCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		workshopSchoolCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		annexedCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		investCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		interimCauseCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		entrepreneurSupportCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		promotionMeasuresCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		quoteReductionsCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		bonusCBPanel.getElement().getStyle().clearDisplay();
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
		otherDataTableElement.getRows().getItem(18).getStyle().setDisplay(Display.NONE);
		
		contractReliefCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		investCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		interimCauseCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		entrepreneurSupportCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		promotionMeasuresCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		quoteReductionsCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		annexedCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		older52CBPanel.getElement().getStyle().setDisplay(Display.NONE);
		bonusCBPanel.getElement().getStyle().setDisplay(Display.NONE);
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
		otherDataTableElement.getRows().getItem(18).getStyle().setDisplay(Display.NONE);
		
		workProgramDataCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		contractReliefCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		investCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		interimCauseCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		entrepreneurSupportCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		promotionMeasuresCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		quoteReductionsCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		annexedCBPanel.getElement().getStyle().setDisplay(Display.NONE);
		older52CBPanel.getElement().getStyle().setDisplay(Display.NONE);
		bonusCBPanel.getElement().getStyle().setDisplay(Display.NONE);
	}
	
	private void showHideTransformRows() {
		if(isTransform || isExtension) {
			otherDataTableElement.getRows().getItem(15).getStyle().clearDisplay();
			otherDataTableElement.getRows().getItem(16).getStyle().clearDisplay();
		} else {
			otherDataTableElement.getRows().getItem(15).getStyle().setDisplay(Display.NONE);
			otherDataTableElement.getRows().getItem(16).getStyle().setDisplay(Display.NONE);
		}
	}
	
	private void fillSpecificData() {
		String codeCNO = this.contractSpecificData.getCno();
		CNO cnoObj = cnoMap.get(codeCNO);
		if(null != cnoObj)
			cnoSB.setText(codeCNO + " - " + cnoObj.getTitle());
		
		ideTB.setValue(this.contractSpecificData.getIde());
		comunicationDateBx.setValue(this.contractSpecificData.getComunicationDate());
		
		if(Boolean.TRUE.equals(isTransform) || AonStringUtils.isNotBlank(contractSpecificData.getTransformIde())) {
			ideTransformTB.setValue(this.contractSpecificData.getTransformIde());
			comunicationTransformDateBx.setValue(this.contractSpecificData.getComunicationTransformDate());
		}
		
		if(Boolean.TRUE.equals(isExtension) || !contractSpecificData.getExtensions().isEmpty()) {
			int extension = 1;
			extensionsLB.clear();
			for(String extensionIde : contractSpecificData.getExtensions().keySet()){
				extensionsLB.addItem("Pr\u00f3rroga " + extension, extensionIde);
				extension++;
			}
			
			extensionsLB.addChangeHandler(e -> {
				Date extensionDate = contractSpecificData.getExtensions().get(extensionsLB.getSelectedValue());
				ideExtensionTB.setValue(extensionsLB.getSelectedValue());
				comunicationExtensionDateBx.setValue(extensionDate);
			});
			
			if(!contractSpecificData.getExtensions().isEmpty()) {
				Entry<String, Date> entry = (Entry<String, Date>) contractSpecificData.getExtensions().entrySet().toArray()[0];
				ideExtensionTB.setValue(entry.getKey());
				comunicationExtensionDateBx.setValue(entry.getValue());
			}
			
		}
		
		calendarFormativeStartDate.setValue(this.contractSpecificData.getCalendarFormativeStartDate());
		calendarFormativeEndDate.setValue(this.contractSpecificData.getCalendarFormativeEndDate());
		setSelectedValueLB(formativeLevelLB, this.contractSpecificData.getFormativeLevel());
		Map<String, String> academicTitulations = AcademicTitulation.getAcademicTitulations(this.contractSpecificData.getFormativeLevel());
		createAcademicTitulationLB(academicTitulations);
		setSelectedValueLB(academicTitulationLB, this.contractSpecificData.getAcademicTitulation());
		profesionalityCB.setValue(this.contractSpecificData.getProfesionality());
		setSelectedValueLB(signBasicCopyLB, this.contractSpecificData.getSignBasicCopy());
		basicCopyTA.setValue(this.contractSpecificData.getBasicCopy());
		useEnterpriseFreeTB.setText(this.contractSpecificData.getUseEnterpriseFree());
		agreementHoursTB.setText(this.contractSpecificData.getAgreementHours());
		agreementMinutesTB.setText(this.contractSpecificData.getAgreementMinutes());
		repeatFDCB.setValue(this.contractSpecificData.getRepeatFD());
		setSelectedValueLB(journeyTypeLB, this.contractSpecificData.getJourneyType());
		journeyDurationHoursTB.setText(this.contractSpecificData.getJourneyDurationHours());
		journeyDurationMinutesTB.setText(this.contractSpecificData.getJourneyDurationMinutes());
		teoricFormationYesRB.setValue(this.contractSpecificData.getTeoricFormation());
		formationHoursTB.setText(this.contractSpecificData.getFormationHours());
		formationMinutesTB.setText(this.contractSpecificData.getFormationMinutes());
		retirementPercentTB.setText(this.contractSpecificData.getRetirementPercent());
		discCB.setValue(this.contractSpecificData.getDisc());
		setSelectedValueLB(discReasonLB, this.contractSpecificData.getDiscReason());
		trueDateCB.setValue(this.contractSpecificData.getTrueDate());
		planRecoveryCB.setValue(this.contractSpecificData.getPlanRecovery());
		
		//WorkProgramDataTable
		if(Boolean.TRUE.equals(this.contractSpecificData.getWorkProgramData())) {
			showWorkProgramDataTable();
			workProgramDataCB.setValue(true);
			setSelectedValueLB(workProgramLB, this.contractSpecificData.getWorkProgram());	
		} else {
			workProgramDataCB.setValue(false);
			resetWorkProgramDataTable();
			hideWorkProgramDataTable();
		}
		
		//TemporalWorkEnterpriseDataTable
		if(Boolean.TRUE.equals(this.contractSpecificData.getTemporalWorkEnterprise())){
			showTemporalWorkEnterpriseDataTable();
			temporalWorkEnterpriseCB.setValue(true);
			nifTB.setText(this.contractSpecificData.getNif());
			socialReasonTB.setText(this.contractSpecificData.getSocialReason());
			contractTemplateCB.setValue(this.contractSpecificData.getContractTemplate());
			foreignEnterpriseCB.setValue(this.contractSpecificData.getForeignEnterprise());
		} else {
			temporalWorkEnterpriseCB.setValue(false);
			resetTemporalWorkEnterpriseDataTable();
			hideTemporalWorkEnterpriseDataTable();
		}
		
		//ContractReliefDataTable
		if(Boolean.TRUE.equals(this.contractSpecificData.getContractRelief())){
			showContractReliefDataTable();
			contractReliefCB.setValue(true);
			setSelectedValueLB(reliefEmployeeLB, this.contractSpecificData.getReliefEmployee());	
			retirementNameTB.setText(this.contractSpecificData.getRetirementName());
			retirementSurnameTB.setText(this.contractSpecificData.getRetirementSurname());
			retirementSurname2TB.setText(this.contractSpecificData.getRetirementSurname2());
		} else {
			contractReliefCB.setValue(false);
			resetContractReliefDataTable();
			hideContractReliefDataTable();
		}
		
		//OfferWorkDataTable
		if(Boolean.TRUE.equals(this.contractSpecificData.getOfferWorkData())) {
			showOfferWorkDataTable();
			offerWorkDataCB.setValue(true);
			offerTB.setText(this.contractSpecificData.getOffer());
		} else {
			offerWorkDataCB.setValue(false);
			resetOfferWorkDataTable();
			hideOfferWorkDataTable();
		}
		
		//WorkshopSchoolDataTable
		if(Boolean.TRUE.equals(this.contractSpecificData.getWorkshopSchoolB())) {
			showWorkshopSchoolDataTable();
			workshopSchoolCB.setValue(true);
			setSelectedValueLB(workshopSchoolLB, this.contractSpecificData.getWorkshopSchool());	
		} else {
			workshopSchoolCB.setValue(false);
			resetWorkshopSchoolDataTable();
			hideWorkshopSchoolDataTable();
		}
		
		//DisabilityDataTable
		if(Boolean.TRUE.equals(this.contractSpecificData.getDisabilityB())) {
			showDisabilityDataTable();
			disabilityCB.setValue(true);
			setSelectedValueLB(disabilityLB, this.contractSpecificData.getDisability());
			setSelectedValueLB(bonusColectiveDisabilityLB, this.contractSpecificData.getBonusColective());
		} else {
			disabilityCB.setValue(false);
			resetDisabilityDataTable();
			hideDisabilityDataTable();
		}
		
		//Older52
		if(Boolean.TRUE.equals(this.contractSpecificData.getOlderThan52())) {
			showOlder52DataTable();
			older52CB.setValue(true);
			setSelectedValueLB(older52LB, this.contractSpecificData.getOtherLegislations());
		} else {
			older52CB.setValue(false);
			resetOlder52DataTable();
			hideOlder52DataTable();
		}
		
		//AnnexedDataTable
		if(Boolean.TRUE.equals(this.contractSpecificData.getAnnexedB())) {
			showAnnexedDataTable();
			annexedCB.setValue(true);
			if(Boolean.TRUE.equals(this.contractSpecificData.getAnnexed()))
				annexedRB.setValue(true);
			else
				annexed2RB.setValue(true);
			sourceYearTB.setValue(this.contractSpecificData.getSourceYear());
		} else {
			annexedCB.setValue(false);
			resetAnnexedDataTable();
			hideAnnexedDataTable();
		}

		//CampaignsDataTable
		if(Boolean.TRUE.equals(this.contractSpecificData.getCampaigns())) {
			showCampaignsDataTable();
			campaignsCB.setValue(true);
			cpCampaignTB.setValue(this.contractSpecificData.getCpCampaign());
			codeCampaignTB.setValue(this.contractSpecificData.getCodeCampaign());
			yearCampaignTB.setValue(this.contractSpecificData.getYearCampaign());
		} else {
			campaignsCB.setValue(false);
			resetCampaignsDataTable();
			hideCampaignsDataTable();
		}
		
		//InvestDataTable
		if(Boolean.TRUE.equals(this.contractSpecificData.getInvest())) {
			showInvestDataTable();
			investCB.setValue(true);
			setSelectedValueLB(employerLB, this.contractSpecificData.getEmployer());	
			setSelectedValueLB(employeeLB, this.contractSpecificData.getEmployee());	
			researcherCB.setValue(this.contractSpecificData.getResearcher());
		} else {
			investCB.setValue(false);
			resetInvestDataTable();
			hideInvestDataTable();
		}

		//InterimCauseDataTable
		if(Boolean.TRUE.equals(this.contractSpecificData.getIsInterimCause())) {
			showInterimCauseDataTable();
			interimCauseCB.setValue(true);
			setSelectedValueLB(interimCauseLB, this.contractSpecificData.getInterimCause());	
		} else {
			interimCauseCB.setValue(false);
			resetInterimCauseDataTable();
			hideInterimCauseDataTable();
		}
		
		//EntrepreneurSupportDataTable
		if(Boolean.TRUE.equals(this.contractSpecificData.getEntrepreneurSupport())) {
			showEntrepreneurSupportDataTable();
			entrepreneurSupportCB.setValue(true);
			setSelectedValueLB(bonusColectiveLB, this.contractSpecificData.getBonusColective());
			freelanceEmployeerCB.setValue(this.contractSpecificData.getFreelanceEmployeer());
		} else {
			entrepreneurSupportCB.setValue(false);
			resetEntrepreneurSupportDataTable();
			hideEntrepreneurSupportDataTable();
		}

		//PromotionMeasuresDataTable
		if(Boolean.TRUE.equals(this.contractSpecificData.getPromotionMeasures())) {
			showPromotionMeasuresDataTable();
			promotionMeasuresCB.setValue(true);
			promotionPermanentHiringCB.setValue(this.contractSpecificData.getPromotionPermanentHiring());
		} else {
			promotionMeasuresCB.setValue(false);
			resetPromotionMeasuresDataTable();
			hidePromotionMeasuresDataTable();
		}

		//QuoteReductionsDataTable
		if(Boolean.TRUE.equals(this.contractSpecificData.getQuoteReductions())) {
			showQuoteReductionsDataTable();
			quoteReductionsCB.setValue(true);
			setSelectedValueLB(reductionColectiveLB, this.contractSpecificData.getReductionColective());
			if(null != this.contractSpecificData.getQuoteReduction() && this.contractSpecificData.getQuoteReduction())
				quoteReductionRB.setValue(true);
			else
				quoteReduction2RB.setValue(true);
			journeyPercentTB.setValue(this.contractSpecificData.getJourneyPercent());
		} else {
			quoteReductionsCB.setValue(false);
			resetQuoteReductionsDataTable();
			hideQuoteReductionsDataTable();
		}
		
		//BonusDataTable
		if(Boolean.TRUE.equals(this.contractSpecificData.getBonus())) {
			showBonusDataTable();
			bonusCB.setValue(true);
			setSelectedValueLB(bonusLB, this.contractSpecificData.getBonusType());
		} else {
			bonusCB.setValue(false);
			resetBonusDataTable();
			hideBonusDataTable();
		}
	}
	
	public com.esferalia.aon.gwt.payroll.shared.ContractSpecificData getContractSpecificData() {
		return this.contractSpecificData;
	}
	
	// ------------------------------------------------- Database Methods (Specific Data)
	
	public void getContractSpecificData(Consumer<com.esferalia.aon.gwt.payroll.shared.ContractSpecificData> success, Consumer<Throwable> failure) {
		Integer contractId = contractEmployeeInfo.getContractInfo().getContractId();
		impl.getContractSpecificData(contractId, new AsyncCallback<com.esferalia.aon.gwt.payroll.shared.ContractSpecificData>() {
			
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
						reloadSepeData(s -> {});
					}
				};
				timer.schedule(2500);
			}, f -> showErrorMessage("Error obtenci\u00f3n Datos Sepe contrato", f.getMessage()));
	}
	
	public void syncComunicationsData() {
		showLoadingMessage("Sincronizando comunicaciones del Sepe...");
		implEmployee.getSepeComunicationData(
				contractEmployeeInfo.getEmployeeInfo().getDocument(),
				contractEmployeeInfo.getContractInfo().getStartDate(),
				contractEmployeeInfo.getContractInfo().getContractId(), 
				new AsyncCallback<Map<String,String>>() {
			
					@Override
					public void onSuccess(Map<String, String> sepeData) {
						showSuccessMessage("Sincronizaci\u00f3n Sepe", "Sincronizaci\u00f3n con el Sepe realizada correctamente");
						
						Timer timer = new Timer() {
							@Override
							public void run() {
								reloadSepeData(s -> {});
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
	
	public void setContractSpecificData(com.esferalia.aon.gwt.payroll.shared.ContractSpecificData contractSpecificData, Consumer<Void> success, Consumer<Throwable> failure) {
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

}
