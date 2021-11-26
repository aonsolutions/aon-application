package com.esferalia.aon.gwt.payroll.client;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.DateListBox;
import com.esferalia.aon.gwt.common.client.widget.MonthListBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonExpandButton;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessagePanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbar;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.gwt.payroll.shared.Agreement;
import com.esferalia.aon.gwt.payroll.shared.Agreement.Level;
import com.esferalia.aon.gwt.payroll.shared.ContractInfo;
import com.esferalia.aon.gwt.payroll.shared.EmployeeInfo;
import com.esferalia.aon.gwt.payroll.shared.JourneyDuration;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DeckLayoutPanel;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.Widget;

import net.aonsolutions.gwt.pdfjs.client.FullViewer;

public abstract class EmployeeDraft extends Composite {
	
	private class EmployeeImplementation extends Employee{
		
		// TABLA DATOS CONTRATO
		
		@Override
		public void onClearEmployeeClick() {
			// Not user in this case
		}

		@Override
		public void onEmployeeDocumentSuggestionChange(String document) {
			// Not user in this case
		}

		@Override
		public void onEmployeeDocumentChange(String document, String documentType) {
			employeeDraftObject.setEmployeeDocument(document);
			employeeDraftObject.setEmployeeDocumentType(documentType);
		}

		@Override
		public void onEmployeeNationalityChange(String countryIso2) {
			employeeDraftObject.setNationality(countryIso2);
		}

		@Override
		public void onEmployeeSSNumSuggestionChange(String ssNumber) {
			// Not user in this case
		}

		@Override
		public void onEmployeeSSNumChange(String ssNumber) {
			employeeDraftObject.setEmployeeSocialSecurityNum(ssNumber);
		}

		@Override
		public void onEmployeeNameSuggestionChange(String nameSurname) {
			// Not user in this case
		}

		@Override
		public void onEmployeeNameChange(String name) {
			employeeDraftObject.setEmployeeName(name);
		}

		@Override
		public void onEmployeeFirstSurnameSuggestionChange(String nameSurname) {
			// Not user in this case
		}

		@Override
		public void onEmployeeFirstSurnameChange(String surname) {
			employeeDraftObject.setEmployeeFirstSurname(surname);
		}

		@Override
		public void onEmployeeSecondSurnameChange(String secondSurname) {
			employeeDraftObject.setEmployeeSecondSurname(secondSurname);
		}

		@Override
		public void onContractSSRegimenChange(byte ssRegime) {
			employeeDraftObject.setSSRegime(ssRegime);
		}

		@Override
		public void onContractActiviesCCCChange(String activityCCC) {
			employeeDraftObject.setActivityInfo(activityCCC);
		}
		
		@Override
		public void onContractMdCTZhange(String mdCtz) {
			employeeDraftObject.setMdCtzInfo(mdCtz);
		}

		@Override
		public void onContractWorkplaceChange(Integer workplaceId) {
			employeeDraftObject.setContractWorkplaceId(workplaceId);
		}

		@Override
		public void onContractTypeChange(String contractType) {
			employeeDraftObject.setContractType(contractType);
			employeeDraftObject.setContractModel(null);
		}

		@Override
		public void onContractModalityChange(Integer contractModel) {
			employeeDraftObject.setContractModel(contractModel);
		}

		@Override
		public void onContractStartDateChange(Date startDate) {
			employeeDraftObject.setContractStartDate(startDate);
		}

		@Override
		public void onContractEndDateChange(Date endDate) {
			employeeDraftObject.setContractEndDate(endDate);
		}

		@Override
		public void onContractSeniorityDateChange(Date seniorityDate) {
			employeeDraftObject.setContractSeniorityDate(seniorityDate);
		}

		@Override
		public void onContractAgreementChange(Integer agreementId, String agreementSSNumber) {
			employeeDraftObject.setContractAgreementId(agreementId);
			employeeDraftObject.setContractAgreementLevelId(null);
			employeeDraftObject.setContractCategory(null);
			employeeDraftObject.setAgreementSSNumber(agreementSSNumber);
			
			if(null != agreementId)
				getAgreementLevels(agreementId, s -> {}, f -> {});
		}

		@Override
		public void onContractAgreementLevelChange(Integer agreementLevelId) {
			employeeDraftObject.setContractAgreementLevelId(agreementLevelId);
		}

		@Override
		public void onContractCategoryChange(String agreementCategory) {
			employeeDraftObject.setContractCategory(agreementCategory);
		}

		@Override
		public void onContractQuoteGroupChange(String quoteGroup) {
			employeeDraftObject.setContractQuoteGroup(quoteGroup);
		}

		@Override
		public void onContractOccupationChange(String occupation) {
			employeeDraftObject.setContractOccupation(occupation);
		}
		
		@Override
		public void onContractRLCEChange(String rlce) {
			employeeDraftObject.setContractRlce(rlce);
		}

		@Override
		public void onContractJourneyTypeChange(Boolean journeyType) {
			 employeeDraftObject.setContractJourneyType(journeyType);
		}
		
		@Override
		public void onContractPartialityChange(Double partialityCoef) {
			employeeDraftObject.setPartialityCoef(partialityCoef);
		}
		
		@Override
		public void onContractJourneyDurationClick() {
			EmployeeTree.showEmployeeCalendar(employeeDraftObject.getEmployeeCalendar());	
		}
		
		//EMPLOYEE TABLE

		@Override
		public void onEmployeeBirthDateChange(Date birthDate) {
			employeeDraftObject.setEmployeeBirthDate(birthDate);
		}

		@Override
		public void onEmployeeGenderChange(byte gender) {
			employeeDraftObject.setEmployeeGender(gender);
		}

		@Override
		public void onEmployeeCivilStatusChange(byte civilStatus) {
			employeeDraftObject.setEmployeeCivilStatus(civilStatus);
		}

		@Override
		public void onEmployeeStreetTypeChange(String streetType) {
			employeeDraftObject.setEmployeeStreetType(streetType);
		}

		@Override
		public void onEmployeeAddressChange(String address) {
			employeeDraftObject.setEmployeeAddress(address);
		}

		@Override
		public void onEmployeeAddressNumChange(String addressNum) {
			employeeDraftObject.setEmployeeAddressNumber(addressNum);
		}
		
		@Override
		public void onEmployeeAddressInfoChange(String addressInfo) {
			employeeDraftObject.setEmployeeAddressInfo(addressInfo);
		}

		@Override
		public void onEmployeeAddressZipChange(String addressZip) {
			employeeDraftObject.setEmployeeAddressZip(addressZip);
		}
		
		@Override
		public void onEmployeeAddressProvinceChange(String addressProvinceCode) {
			employeeDraftObject.setEmployeeAddressProvince(addressProvinceCode);
			employeeDraftObject.setEmployeeAddressCity("-1");
		}

		@Override
		public void onEmployeeAddressMunicipalityChange(String addressMunicipality) {
			employeeDraftObject.setEmployeeAddressCity(addressMunicipality);
		}
		
		@Override
		public void onEmployeeMobileChange(String mobile) {
			employeeDraftObject.setEmployeeMobile(mobile);
		}

		@Override
		public void onEmployeePhoneChange(String phone) {
			employeeDraftObject.setEmployeePhone(phone);
		}

		@Override
		public void onEmployeeEmailChange(String email) {
			employeeDraftObject.setEmployeeEmail(email);
		}

		@Override
		public void onEmployeePayMethodChange(Integer payMethodId) {
			employeeDraftObject.setEmployeePayMethodId(payMethodId);
		}

		@Override
		public void onEmployeeBICChange(String bic) {
			employeeDraftObject.setEmployeeBIC(bic);
		}

		@Override
		public void onEmployeeAccountChange(String account, String bankAlias, String bankSwift) {
			employeeDraftObject.setEmployeeAccount(account);
			employeeDraftObject.setEmployeeBankAlias(bankAlias);
			employeeDraftObject.setEmployeeBIC(bankSwift);
		}
	}
	
	// ------------------------------------------------- UiBinder
	
	interface EmployeeDraftUiBinder extends UiBinder<Widget, EmployeeDraft> {}
	
	private static EmployeeDraftUiBinder uiBinder = GWT.create(EmployeeDraftUiBinder.class);
	
	// ------------------------------------------------- ScheduledCommand
	
	class AFICommand implements ScheduledCommand {

		@Override
		public void execute() {
			onAFIChanges();
		}
	}
	
	class IDCCommand implements ScheduledCommand {

		@Override
		public void execute() {
			showIdc();
		}
				
	}
	
	class IDCPlNssCommand implements ScheduledCommand {

		@Override
		public void execute() {
			showIdcPlNss();
		}
	}
	
	class TACommand implements ScheduledCommand {

		@Override
		public void execute() {
			showTa();
		}
		
		private void showTa() {
			employeeDraftObject.downloadTa(
			dataURI -> {
					showPdf();
					pdfViewer.open(dataURI);
			}, 
			trowable -> {});
		}
	}
	
	class PeculiaritiesCommand implements ScheduledCommand {

		@Override
		public void execute() {
			EmployeePeculiaritiesDialog dialog = new EmployeePeculiaritiesDialog(employeeDraftObject.getContractId(), employeeDraftObject.getContractStartDate());
			dialog.center();
			dialog.show();
		}
	}
	
	class BonificationsCommand implements ScheduledCommand {

		@Override
		public void execute() {
			SSPECDraft dialog = new SSPECDraft(employeeDraftObject.getContractId());
			dialog.setPopupPositionAndShow((x,y) -> dialog.center() );
		}
	}
	
	class NewContextMenu extends ContextMenu {
		
		private MenuItem ta;
		private MenuItem afi;
		private MenuItem idc;
		private MenuItem idcPlNss;		
		private MenuItem pecs = null;
		private MenuItem pecsSS = null;
		
		public NewContextMenu() {
			
			afi = addItem("Cambios AFI", new AFICommand(), 
					AON.CSS.aonIconTgss(), AON.AON_ICON_CMD_BUTTON, style.cmdBtn());
			afi.ensureDebugId("afi");
			
			pecs = addItem("Peculiaridades de Cotizaci\u00F3n (Manual)", new PeculiaritiesCommand(), 
					AON.CSS.aonIconTgss(), AON.AON_ICON_CMD_BUTTON, style.cmdBtn());
			pecs.ensureDebugId("peculiarities");
			
			pecsSS = addItem("Peculiaridades de Cotizaci\u00F3n (SISTEMA RED)", new BonificationsCommand(), 
					AON.CSS.aonIconTgss(), AON.AON_ICON_CMD_BUTTON, style.cmdBtn());
			pecsSS.ensureDebugId("bonifications");
			
			ta = addItem("Duplicados de Documentos TA", new TACommand(), 
					AON.CSS.aonIconTgss(), AON.AON_ICON_CMD_BUTTON, style.cmdBtn());
			ta.ensureDebugId("ta");
			
			idc = addItem("Informe de Cotizaci\u00F3n-Trab Cuenta Ajena", new IDCCommand(), 
					AON.CSS.aonIconTgss(), AON.AON_ICON_CMD_BUTTON, style.cmdBtn());
			idc.ensureDebugId("idc");
			
			idcPlNss = addItem("Informe de Cotizaci\u00F3n/Periodo iquidaci\u00F3n-NSS", new IDCPlNssCommand(), 
					AON.CSS.aonIconTgss(), AON.AON_ICON_CMD_BUTTON, style.cmdBtn());
			idcPlNss.ensureDebugId("idcPlNss");
			
		}

		public MenuItem getTa() {
			return ta;
		}

		public MenuItem getAfi() {
			return afi;
		}

		public MenuItem getIdc() {
			return idc;
		}

		public MenuItem getIdcPlNss() {
			return idcPlNss;
		}

		public MenuItem getPeculiarities() {
			return pecs;
		}

		public MenuItem getBonifications() {
			return pecsSS;
		}
		
	}
	
	// ------------------------------------------------- UiFields
	
	@UiField
	MyStyle style;

	interface MyStyle extends CssResource {
		String cmdBtn();
	}
	
	@UiField
	DockLayoutPanel dockLayoutPanel;
	
	@UiField (provided = true)
	AonToolbar toolbar;
	
	@UiField
	HTMLPanel messageContainer;
	
	@UiField (provided = true)
	Employee employee;
	
	@UiField
	DeckLayoutPanel deckPanel;

	@UiField
	FullViewer pdfViewer;
	
	private static final int EMPLOYEE_INDEX = 0;
	private static final int PDF_VIEWER_INDEX = 1;
	
	// ------------------------------------------------- Class variables
	
	private EmployeeDraftObject employeeDraftObject;
	
	private AonToolbarButton undoAll;
	private AonToolbarButton undo;
	private AonToolbarButton redo;
	private AonExpandButton tgss;
	private AonToolbarButton closePDF;
	private DateListBox idcDateListBox;
	private MonthListBox idcMonthListBox;
	
	private NewContextMenu contextMenu;
	
	private Consumer<EmployeeDraftObject> onSaved ;

	// ------------------------------------------------- Constructor

	protected EmployeeDraft() {

		employee = new EmployeeImplementation();
		toolbar = new AonToolbar("Contrato");
		
		initWidget(uiBinder.createAndBindUi(this));
		
		contextMenu = new NewContextMenu();
		
		fillToolbarPanel();
		
		employee.hideClearEmployee();
		
		onSaved = this::onSavedNoop;
				
	}
		
	// ------------------------------------------------- setEmployeeDraft

	public void setEmployeeDraftObject(EmployeeDraftObject employeeDraftObject) {
		showEmployee();
		this.employeeDraftObject = employeeDraftObject;
		this.employeeDraftObject.initializeEmployee(
			r -> {
					employee.initializeView();
					initializeIdcMonthListBox();
					initializeView();
					initializeUndoRedo();
					
					// Check SS only if not RETA
					Byte ssRegime = employeeDraftObject.getContractData().getSsRegimen();
					if (null == ssRegime || ssRegime != 3) 
						onCheckStatus(getEmployeeDraftObject());
			}, t -> {}
		);
	
	}
	
	protected abstract void onCheckStatus(EmployeeDraftObject employeeDraftObject2);

	public EmployeeDraftObject getEmployeeDraftObject() {
		return this.employeeDraftObject;
	}

	// ------------------------------------------------- Initialize view
	
	private void initializeIdcMonthListBox() {
		Date firstMonth = DateUtils.getFirstDayOfMonth(employeeDraftObject.getContractStartDate());
		Date lastMonth = DateUtils.getFirstDayOfMonth();
		idcMonthListBox.setFirstMonth(firstMonth);
		idcMonthListBox.setLastMonth(lastMonth);
		int months = DateUtils.getMonths(lastMonth, firstMonth);
		idcMonthListBox.setVisibleRange(0, months+1);
		idcMonthListBox.ensureDebugId("idcMonthListBox");
	}
	
	private void initializeIdcDateListBox() {
		employeeDraftObject.getIdcDates(
		dates -> {
			// filter out 'Baja' dates
			dates = filterEven(dates);
			int count = dates.size();
			idcDateListBox.setRowCount(count, true);
			idcDateListBox.setRowData(0, dates);
			idcDateListBox.setVisibleRange(0, count+1);
			idcDateListBox.setSelected(count-1, true);
			idcDateListBox.onResizeDropDownPopup();
		}, 
		error -> {} );
	}

	private void initializeUndoRedo() {
		employeeDraftObject.clearUndoMaganager();
		undo.setEnabled(employeeDraftObject.canUndo());
		undoAll.setEnabled(employeeDraftObject.canUndo());
		redo.setEnabled(employeeDraftObject.canRedo());

		employeeDraftObject.addUndoManagerListener( undoManager -> {
			undo.setEnabled(undoManager.canUndo());
			undoAll.setEnabled(undoManager.canUndo());
			redo.setEnabled(undoManager.canRedo());
		});
	}

	private void initializeView() {
		initActivitiesCCC();
		initWorkplaces();
		initContractType();
		initAgreements();
		initPayMethods();
		initIbans();
		initFocus();
		
		initExistingEmployee(employeeDraftObject.getContractData().hasPayroll());
	}

	private void initActivitiesCCC() {
		employee.initActivitiesCCC(
				employeeDraftObject.getEnterpriseContext().getActivitiesCCC().getActivities(), 
				employeeDraftObject.getEnterpriseContext().getActivitiesCCC().getCccs());
	}
	
	private void initWorkplaces() {
		employee.initWorkplaces(employeeDraftObject.getEnterpriseContext().getWorkplaces());
	}
	
	private void initContractType() {
		employee.initContractType();
	}
	
	private void initAgreements() {
		employee.initAgreements(employeeDraftObject.getActiveAgreements());
	}
	
	private void initPayMethods() {
		employee.initPayMethods(employeeDraftObject.getEnterpriseContext().getPayMethods());
	}
	
	private void initIbans() {
		employee.initIbans(employeeDraftObject.getExistingIban());
	}
	
	private void initFocus() {
		//FOCUS DOCUMENT
		Scheduler.get().scheduleDeferred(() -> employee.document.setFocus(true));
	}
	
	// ------------------------------------------------- Initialize existing employee
	
	public void initExistingEmployee( boolean hasPayroll){
		fillExistingEmployee();
		fillExistingContract();
		if(hasPayroll) {
		   employee.blockVariablesExistingContract();
		   employee.blockFieldsExistingPayroll();
		} else {
		   employee.unblockVariablesExistingContract();
		   employee.unblockFieldsExistingPayroll();
		}
	}
	
	private void fillExistingEmployee() {
		EmployeeInfo employeeData = employeeDraftObject.getEmployeeData();
		
		employee.document.setValue(employeeData.getDocument(), true);
		employee.nationality.setValue(employeeData.getNationality());
		employee.securitySocialNum.setValue(employeeData.getSsNumber(), true);
		
		employee.name.setValue(employeeData.getName());
		employee.firstSurname.setValue(employeeData.getSurName());
		employee.secondSurname.setValue(employeeData.getSecondSurName());
		
		employee.birthDate.setValue(employeeData.getBirthdate(), true);
		setSelectedValueLB(employee.gender, String.valueOf(employeeData.getGender()));
		setSelectedValueLB(employee.civilStatus, employeeData.getCivilStatus()+"");
		
		setSelectedValueLB(employee.streetType, employeeData.getStreetType());
		employee.address.setValue(employeeData.getAddress());
		employee.addressNum.setValue(employeeData.getAddresNum());
		employee.addressZip.setValue(employeeData.getAddressZip());
		
		setSelectedValueLB(employee.addressProvince, employeeData.getAddressProvinces());
		employee.updateMunicipalities();
		setSelectedValueLB(employee.addressMunicipality, employeeData.getAddressCity());
		
		employee.mobile.setValue(employeeData.getMobile());
		employee.phone.setValue(employeeData.getPhone());
		employee.email.setValue(employeeData.getEmail());
		
		setSelectedValueLB(employee.payMethod, employeeData.getPaymethodId()+"");
		employee.account.setValue(employeeData.getAccount());
		employee.bic.setValue(employeeData.getBic());
		employee.reformatAccount(employee.account);
	}
	
	private void fillExistingContract() {
		ContractInfo contractData = employeeDraftObject.getContractData();
		
		//RETA, había algo mas que determinaba si era o no RETA
		if (null != contractData.getSsRegimen() && contractData.getSsRegimen() == 3) { 
			employee.showElementsFreelancerTable();
			contextMenu.getAfi().setVisible(false);
			fillContractFreelancerTable(contractData);
		} else {
			employee.hideElementsFreelancerTable();
			contextMenu.getAfi().setVisible(true);
			fillContractTable(contractData);
		}
	}

	private void fillContractFreelancerTable(ContractInfo contractData) {
		// RETA
		setSelectedValueLB(employee.ssRegimeType, contractData.getSsRegimen()+"");
		setSelectedValueLB(employee.workplace, contractData.getWorkplaceId()+"");
		
		employee.startDate.setValue(contractData.getStartDate());
		employee.seniorityDate.setValue(contractData.getSeniorityDate());
		employee.endDate.setValue(contractData.getEndDate());
		
		Integer agreementId = contractData.getAgreementId();
		setSelectedValueLB(employee.agreement, agreementId+"/"+contractData.getAgreementColective());
		if(null != agreementId) {
			getAgreementLevels(agreementId, s -> {
				setSelectedValueLB(employee.level, contractData.getAgreementLevelId()+"");
				employee.category.setValue(contractData.getAgreementCategory());
			}, f -> {});
		}
		
		setSelectedValueLB(employee.journeyType, (null == contractData.getJourneyType() || contractData.getJourneyType() == 0) ? "false" : "true");
		DomEvent.fireNativeEvent(Document.get().createChangeEvent(), employee.journeyType); 
	}

	private void fillContractTable(ContractInfo contractData) {
		setSelectedValueLB(employee.ssRegimeType, contractData.getSsRegimen()+"");
		
		setSelectedValueLB(employee.activityCCC, contractData.getActivityId()+"/"+contractData.getCccId()+"/"+contractData.getCccType());
		
		if(contractData.getCccType() == (byte)7) {
			employee.showMdCtzContract();
			setSelectedValueLB(employee.mdCTZLB, contractData.getMdctz());
		} else
			employee.hideMdCtzContract();
		
		setSelectedValueLB(employee.workplace, contractData.getWorkplaceId()+"");
		
		setSelectedValueLB(employee.contractTypeLB, contractData.getContractType());
		
		Integer contractTypeInt = null;
		try {
			contractTypeInt = Integer.parseInt(contractData.getContractType());
			if(AonNumberUtils.between(contractTypeInt, 200, 300) || AonNumberUtils.between(contractTypeInt, 500, 599) || AonNumberUtils.equals(contractTypeInt, 0)) {
				employee.showPartialTimeContract();
				if(employeeDraftObject.getContractData().getContractJourneyDuration().getContractJourneyDuration().entrySet().isEmpty()) {
					employee.createJourneyDurationWarning();
				} else {
					employee.createJourneyDurationInfo(employeeDraftObject.getContractData().getContractJourneyDuration().getJourneyText());
				}
			} else
				employee.showElementsFullTimeContract();
		} catch (NumberFormatException e) {
			// Not use here
		}
		
		employee.updateModality(contractTypeInt);
		setSelectedValueLB(employee.modality, contractData.getContractModel()+"");
		
		employee.startDate.setValue(contractData.getStartDate());
		employee.seniorityDate.setValue(contractData.getSeniorityDate());
		employee.endDate.setValue(contractData.getEndDate());
		
		Integer agreementId = contractData.getAgreementId();
		setSelectedValueLB(employee.agreement, agreementId+"/"+contractData.getAgreementColective());
		if(null != agreementId) {
			getAgreementLevels(agreementId, s -> {
				setSelectedValueLB(employee.level, contractData.getAgreementLevelId()+"");
				employee.category.setValue(contractData.getAgreementCategory());
			}, f -> {});
		}
		
		setSelectedValueLB(employee.quoteGroup, contractData.getQuoteGroup());
		setSelectedValueLB(employee.occupation, contractData.getOcupation());
		setSelectedValueLB(employee.rlce, contractData.getRlce());
		
		Double partialityCoef = contractData.getPartialityCoef();
		if( (null == partialityCoef || partialityCoef == 0.00) && 
				(null != contractTypeInt && (AonNumberUtils.between(contractTypeInt, 200, 300) || AonNumberUtils.between(contractTypeInt, 500, 599) || AonNumberUtils.equals(contractTypeInt, 0)))) {
			
			partialityCoef = calculatePartialityCoef();
			contractData.setPartialityCoef(partialityCoef);
		}
		
		if(null != contractData.getPartialityCoef())
			employee.partialityCoef.setValue(contractData.getPartialityCoef());	
	}
	
	private Double calculatePartialityCoef() {
		Double hours = 0.00;
		
		if(null != employeeDraftObject.getContractData().getContractJourneyDuration() && !employeeDraftObject.getContractData().getContractJourneyDuration().getContractJourneyDuration().isEmpty()) {
			for(JourneyDuration journeyDuration : employeeDraftObject.getContractData().getContractJourneyDuration().getContractJourneyDuration().descendingMap().entrySet().iterator().next().getValue()) {
				if(AonStringUtils.isNotBlank(journeyDuration.getExpression()) && !AonStringUtils.equals(journeyDuration.getExpression(), "NL")){
					String expression = journeyDuration.getExpression();
					expression = expression.replace(",", ".");
					hours += Double.parseDouble(expression);
				}
			}
			hours = hours / 40;
		}
				
		return Math.round(hours * 100.0) / 100.0;
	}
	
	// ------------------------------------------------- Auxiliar Methods

	private void getAgreementLevels(Integer agreementId, Consumer<Agreement> success, Consumer<Throwable> failure) {
		employee.level.clear();
		employee.level.addItem("-", "-1");
		
		employeeDraftObject.getAgreement(agreementId,  
		agreement -> {
			for (Level levelRecord : agreement.getLevels()) {
				employee.level.addItem(levelRecord.getDescription(), String.valueOf(levelRecord.getId()));
				for (String categoryRecord : agreement.getCategoriesMap().get(levelRecord.getId()))
					employee.level.addItem(levelRecord.getDescription() + " - " + categoryRecord, String.valueOf(levelRecord.getId()));
			}

			success.accept(agreement);
		},
		throwable -> {
			employeeDraftObject.setContractAgreementId(null);
			employeeDraftObject.setContractAgreementLevelId(null);
			employee.category.setEnabled(false);
			employee.category.setValue("");
			DomEvent.fireNativeEvent(Document.get().createChangeEvent(), employee.category);
			failure.accept(throwable);
		});
	}
	
	private void setSelectedValueLB(ListBox lBox, String str) {
	    String text = str;
	    int indexToFind = 0;
	    for (int i = 0; i < lBox.getItemCount(); i++) {
	        if (AonStringUtils.equalsIgnoreCase(lBox.getValue(i), text)) {
	            indexToFind = i;
	            break;
	        }
	    }
	    lBox.setSelectedIndex(indexToFind);
	}
	
	// ------------------------------------------------- Toolbar panel
	
	private void fillToolbarPanel() {
		
		AonToolbarButton saveContract = new AonToolbarButton( AON.MSG.saveAction(), AON.CSS.aonIconSave() );
		saveContract.addClickHandler(e -> onSaveContract());
		toolbar.add(saveContract);
		
		undoAll = new AonToolbarButton( "Deshacer todo", AON.CSS.aonIconUndoAll() );
		undoAll.addClickHandler(e -> onUndoAll());
		toolbar.add(undoAll);
		
		undo = new AonToolbarButton( AON.MSG.undo(), AON.CSS.aonIconUndo() );
		undo.addClickHandler(e -> onUndo());
		toolbar.add(undo);
		
		redo = new AonToolbarButton( "Rehacer", AON.CSS.aonIconRedo() );
		redo.addClickHandler(e -> onRedo());
		toolbar.add(redo);
		
		tgss = new AonExpandButton("TGSS", AON.CSS.aonIconTgss()) {
			
			@Override
			public void onExpandClick(ClickEvent event) {
				NativeEvent nativeEvent = event.getNativeEvent();
				contextMenu.setPopupPosition(nativeEvent.getClientX(), nativeEvent.getClientY());
				contextMenu.show();
			}
			
			@Override
			public void onDefaultClick(ClickEvent evet) {
				onAFIChanges();
			}

		};
		
		toolbar.add(tgss);
		
		closePDF = new AonToolbarButton( AON.MSG.closed(), AON.CSS.aonIconClose() );
		closePDF.addClickHandler(e -> onClosePDF());
		toolbar.add(closePDF);

		idcMonthListBox = new MonthListBox();
		idcMonthListBox.addChangeHandler(e -> showIdcPlNss(idcMonthListBox.getSelectedMonth()));
		toolbar.add(idcMonthListBox);
		
		idcDateListBox = new DateListBox();
		idcDateListBox.addChangeHandler(e -> showIdc(idcDateListBox.getSelectedDate()));
		toolbar.add(idcDateListBox);

	}

	private void onSaveContract() {
		Map<String, String> messageMap = employee.checkSaveAndGetErrors();
		if(messageMap.isEmpty())
			employeeDraftObject.updateEmployee(
					r -> saved(), 
					t -> {}
			);
		else
			AonMessagePanel.showError(messageContainer, messageMap);
	}

	private void onUndoAll() {
		while ( employeeDraftObject.canUndo() )
			employeeDraftObject.undo();
		initializeView();
	}
	
	private void onUndo() {
		employeeDraftObject.undo();
		initializeView();
	}
	
	private void onRedo() {
		employeeDraftObject.redo();
		initializeView();
	}
	
	private void onAFIChanges() {
		EmployeeAFIDialog dialog = new EmployeeAFIDialog(
				employee.startDate.getValue(),
				employee.endDate.getValue(),
				employee.contractTypeLB.getSelectedValue(),
				employee.quoteGroup.getSelectedValue(),
				employee.occupation.getSelectedValue(),
				employee.partialityCoef.getValue(),
				employeeDraftObject.getContractId(),
				employeeDraftObject.getDomainId(),
				employeeDraftObject.getWorkplaceId(),
				false){

					@Override
					protected void onAcceptCB() {
						// Not use in this case
					}

					@Override
					protected void onPartialityCoefContract(String partialityCoef, Date date) {
						// Not use in this case
					}

					@Override
					protected void onOcupationContract(String ocupation, Date date) {
						// Not use in this case
					}

					@Override
					protected void onQuoteContract(String quoteGroup, Date date) {
						// Not use in this case
					}

					@Override
					protected void onChangeContract(String contract, Date date) {
						// Not use in this case
					}

					@Override
					protected void onEndContract(String settleReason) {
						// Not use in this case
					}

					@Override
					protected void onStartContract() {
						// Not use in this case
					}
		
		};
			
		dialog.center();
		dialog.show();
	}
	
	private void showIdcPlNss() {
		showIdcPlNss(DateUtils.getFirstDayOfMonth());
	}
	
	private void showIdcPlNss(Date month) {
		employeeDraftObject.downloadIdcPlNss( 
		month,
		dataURI -> {
				showPdf();
				idcMonthListBox.setVisible(true);
				idcMonthListBox.setSelected(month, true);
				pdfViewer.open(dataURI);
		}, trowable -> {});
	}
	
	private void showIdc() {
		showIdc(idcDateListBox.getSelected());
	}
	
	private void showIdc(Date date) {
		employeeDraftObject.downloadIdc( 
		date,
		dataURI -> {
				showPdf();
				idcDateListBox.setVisible(true);
				idcDateListBox.setSelected(date, true);
				pdfViewer.open(dataURI);
		}, trowable -> {});
	}

	private void onClosePDF() {
		showEmployee();
	}
	
	// ------------------------------------------------- Toolbar panel auxiliar methods
	
	private void showPdf() {
		contextMenu.getAfi().setVisible(false);
		tgss.setVisible(false);
		contextMenu.getTa().setVisible(false);
		contextMenu.getIdc().setVisible(false);
		contextMenu.getIdcPlNss().setVisible(false);
		undo.setVisible(false);
		redo.setVisible(false);
		undoAll.setVisible(false);

		closePDF.setVisible(true);

		deckPanel.showWidget(PDF_VIEWER_INDEX);		
	}
	
	private void showEmployee() {
		contextMenu.getAfi().setVisible(true);
		tgss.setVisible(true);
		undo.setVisible(true);
		redo.setVisible(true);
		undoAll.setVisible(true);
		contextMenu.getTa().setVisible(true);
		contextMenu.getIdcPlNss().setVisible(true);
		contextMenu.getIdc().setVisible(contextMenu.getIdc().isEnabled());
		
		closePDF.setVisible(false);
		
		idcDateListBox.setVisible(false);
		idcMonthListBox.setVisible(false);
		
		deckPanel.showWidget(EMPLOYEE_INDEX);		
	}
	
	// ------------------------------------------------- Callback saved for check status
	
	private void saved() {
		Map<String, String> messageSuccessMap = new HashMap<>();
		messageSuccessMap.put("Guardado", "El contrato " + employeeDraftObject.getEmployeeFullName() + " ha sido actualizado correctamente");
		AonMessagePanel.showSuccess(messageContainer, messageSuccessMap);
		
		onSaved.accept(employeeDraftObject);
	}
	
	public EmployeeDraft setOnSaved(Consumer<EmployeeDraftObject> onSaved) {
		this.onSaved = onSaved;
		return this;
	}
	
	protected void onSavedNoop(EmployeeDraftObject employeeDraftObject) {}
	
	// ------------------------------------------------- CheckStatus(EmployeeDraftObject) - EmployeeTree
	
	public void setEndDate(Date endDate) {
		employee.setEndDate(endDate);
	}
	
	public void setStartDate(Date endDate) {
		employee.setStartDate(endDate);
	}

	public void setOcupation(String occupation) {
		switch (occupation) {
		case "a":
			employee.occupation.setSelectedIndex(1);
			break;
		case "b":
			employee.occupation.setSelectedIndex(2);
			break;
		case "d":
			employee.occupation.setSelectedIndex(3);
			break;
		case "e":
			employee.occupation.setSelectedIndex(4);
			break;
		case "f":
			employee.occupation.setSelectedIndex(5);
			break;
		case "g":
			employee.occupation.setSelectedIndex(6);
			break;
		case "h":
			employee.occupation.setSelectedIndex(7);
			break;
		default:
			employee.occupation.setSelectedIndex(0);
			break;
		}
		
		DomEvent.fireNativeEvent(Document.get().createChangeEvent(), employee.occupation);
	}
	
	public void setTaVisible(boolean visible ) {
		contextMenu.getTa().setVisible(visible);
	}

	public void setIdcVisible(boolean visible ) {
		initializeIdcDateListBox();
		contextMenu.getIdcPlNss().setVisible(visible);
	}

	private static <T> List<T> filterEven( List<T> list ){
		return filter(list, i -> i % 2 == 0);
	}
	
	private static <T> List<T> filter( List<T> list , Function<Integer, Boolean> filter){
		return IntStream.range(0, list.size()).filter( i -> filter.apply(i)).mapToObj(i -> list.get(i) ).collect(Collectors.toList());
	}
	
}
