package com.esferalia.aon.gwt.payroll.client;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.AonDateUtils;
import com.esferalia.aon.gwt.common.client.widget.DateListBox;
import com.esferalia.aon.gwt.common.client.widget.MonthListBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDockLayout;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomTextBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog.AonAcceptDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonExpandButton;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessagePanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.gwt.payroll.shared.Agreement;
import com.esferalia.aon.gwt.payroll.shared.Agreement.Level;
import com.esferalia.aon.gwt.payroll.shared.CNO;
import com.esferalia.aon.gwt.payroll.shared.ContractInfo;
import com.esferalia.aon.gwt.payroll.shared.EmployeeInfo;
import com.esferalia.aon.gwt.payroll.shared.JourneyDuration;
import com.esferalia.aon.occam.api.model.type.ContractType;
import com.esferalia.aon.occam.api.model.type.ContractType.ContractTypeRecord;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.DeckLayoutPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.MenuItemSeparator;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.ScrollPanel;

import net.aonsolutions.gwt.pdfjs.client.FullViewer;

public abstract class EmployeeDraft extends AonCustomDockLayout {
	
	private class EmployeeImplementation extends EmployeeWidget{
		
		// TABLA DATOS CONTRATO
		
		@Override
		public void onClearEmployeeClick() {
			// Not use in this case
		}
		
		@Override
		public void onEmployeeDocumentSuggestionChange(String document) {
			// Not use in this case
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
			// Not use in this case
		}

		@Override
		public void onEmployeeSSNumChange(String ssNumber) {
			employeeDraftObject.setEmployeeSocialSecurityNum(ssNumber);
		}
		
		@Override
		public void onEmployeeNameSuggestionChange(String nameSurname) {
			// Not use in this case
		}
		
		@Override
		public void onEmployeeNameChange(String name) {
			employeeDraftObject.setEmployeeName(name);
		}

		@Override
		public void onEmployeeFirstSurnameSuggestionChange(String nameSurname) {
			// Not use in this case
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
		public void onContractMdTBThange(String tbtType) {
			employeeDraftObject.setMdTBT(tbtType);
		}
		
		@Override
		public void onContractActiviesCCCChange(String activityCCC) {
			employeeDraftObject.setActivityInfo(activityCCC);
		}
		
		@Override
		public void onContractMdCTZhange(String mdCtz) {
			employeeDraftObject.setContractMdCtz(mdCtz);
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
			employeeDraftObject.setContractModel(contractModel); // GET String of enum in JooqEmployee.java
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
//			Windo
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
		public void onContractQuoteGroupIdx(boolean quoteGroupMonth) {
			employeeDraftObject.setContractQuoteIdxMonth(quoteGroupMonth);
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
		public void onEmployeeCnoSuggestionChange(String cno) {
			employeeDraftObject.setContractCno(cno);
		}
		
		@Override
		public void onContractEmployeesColectiveChange(String employeesColective) {
			employeeDraftObject.setContractEmployeesColective(employeesColective);
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
		
		// TABLA DATOS EMPLEADO

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
		public void onEmployeeAddressProvinceChange(Integer geozoneId) {
			employeeDraftObject.setEmployeeAddressProvince(geozoneId);
			employeeDraftObject.setEmployeeAddressCity(null);
		}
		
		@Override
		public void onEmployeeAddressMunicipalityChange(String cityName, String cityCode) {
			employeeDraftObject.setEmployeeAddressCity(cityName, cityCode);
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

		@Override
		public void fireError(String title, String message) {
			showError(title, message);
		}

		@Override
		public void onUploadDni() {}
		
		@Override
		public void onLoadEnd() {}
		
	}
	
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
	}
	
	class TAEndCommand implements ScheduledCommand {

		@Override
		public void execute() {
			showTaEnd();
		}
	}
	
	class LaboralLifeCommand implements ScheduledCommand {

		@Override
		public void execute() {
			showLaboralLife();
		}
				
	}
	
	class PeculiaritiesCommand implements ScheduledCommand {

		@Override
		public void execute() {
			new EmployeePeculiaritiesDialog(employeeDraftObject.getContractId(), employeeDraftObject.getContractStartDate()) {

				@Override
				protected void onAccept() {
					// Nothing to refresh
				}};
		}
	}
	
	class AltaConsolidadaDeleteCommand implements ScheduledCommand {

		@Override
		public void execute() {
			AonDialog dialog = new AonDialog("Alta Consolidada", new HTML("\u00bfDesea realmente eliminar el alta consolidada\u003f"));
			dialog.confirm(new AonAcceptDialogCallback() {
				
				@Override
				public void onCancel() {
					// Nothing to do here
				}
				
				@Override
				public void onAccept() {
					altaConsolidadaDelete();
				}
			});
		}
	}

	class ComunicateAFICommand implements ScheduledCommand {

		@Override
		public void execute() {
			onComunicateAFI();
		}
	}
	
	class BonificationsCommand implements ScheduledCommand {

		@Override
		public void execute() {
			new SSPECDialog(
					employeeDraftObject.getContractId(), 
					employeeDraftObject.getContractStartDate(),
					employeeDraftObject.getContractEndDate());
		}
	}
	
	class NewContextMenu extends ContextMenu {
		
		private MenuItem ta;
		private MenuItem taEnd;
		private MenuItem afi;
		private MenuItem idc;
		private MenuItem idcPlNss;		
		private MenuItem laboralLife;		
		private MenuItem pecs = null;
		private MenuItem pecsSS = null;

//		private MenuItem movPrevDelete = null;
		MenuItemSeparator separator;
		private MenuItem altaConsolidadaDelete = null;
		private MenuItem comunicateAFI = null;
		
		public NewContextMenu() {
			
			afi = addItem("Cambios AFI", new AFICommand(), 
					AON.CSS.aonIconTgss(), AON.AON_ICON_CMD_BUTTON, AON.CSS.aonCmdItem());
			afi.ensureDebugId("afi");
			
			pecs = addItem("Peculiaridades de Cotizaci\u00F3n (Manual)", new PeculiaritiesCommand(), 
					AON.CSS.aonIconTgss(), AON.AON_ICON_CMD_BUTTON, AON.CSS.aonCmdItem());
			pecs.ensureDebugId("peculiarities");
			
			pecsSS = addItem("Peculiaridades de Cotizaci\u00F3n (SISTEMA RED)", new BonificationsCommand(), 
					AON.CSS.aonIconTgss(), AON.AON_ICON_CMD_BUTTON, AON.CSS.aonCmdItem());
			pecsSS.ensureDebugId("bonifications");
			
			addSeparator();
			
			ta = addItem("Duplicados de Documentos TA", new TACommand(), 
					AON.CSS.aonIconPdf(), AON.AON_ICON_CMD_BUTTON, AON.CSS.aonCmdItem());
			ta.ensureDebugId("ta");
			
			taEnd = addItem("Duplicados de Documentos TA (Baja)", new TAEndCommand(), 
					AON.CSS.aonIconPdf(), AON.AON_ICON_CMD_BUTTON, AON.CSS.aonCmdItem());
			taEnd.ensureDebugId("taEnd");
			
			idc = addItem("IDC-Trab Cuenta Ajena", new IDCCommand(), 
					AON.CSS.aonIconPdf(), AON.AON_ICON_CMD_BUTTON, AON.CSS.aonCmdItem());
			idc.ensureDebugId("idc");
			
			idcPlNss = addItem("IDC/Periodo Liquidaci\u00F3n-NSS", new IDCPlNssCommand(), 
					AON.CSS.aonIconPdf(), AON.AON_ICON_CMD_BUTTON, AON.CSS.aonCmdItem());
			idcPlNss.ensureDebugId("idcPlNss");
			
			laboralLife = addItem("Vida Laboral", new LaboralLifeCommand(), 
					AON.CSS.aonIconPdf(), AON.AON_ICON_CMD_BUTTON, AON.CSS.aonCmdItem());
			laboralLife.ensureDebugId("laboralLife");
			
			addSeparator();

//			movPrevDelete = addItem("Eliminar movimiento previo", new MovPrevDeleteCommand(), 
//					AON.CSS.aonIconTgss(), AON.AON_ICON_CMD_BUTTON, AON.CSS.aonCmdItem());
//			movPrevDelete.ensureDebugId("movPrevDelete");
			
			altaConsolidadaDelete = addItem("Eliminar alta consolidada", new AltaConsolidadaDeleteCommand(),
					AON.CSS.aonIconSend(), AON.AON_ICON_CMD_BUTTON, AON.CSS.aonCmdItem());
			altaConsolidadaDelete.ensureDebugId("altaConsolidadaDelete");

			comunicateAFI = addItem("Notificaci\u00f3n AFI (TGSS)", new ComunicateAFICommand(), AON.CSS.aonIconSend(),
					AON.AON_ICON_CMD_BUTTON, AON.CSS.aonCmdItem());
			comunicateAFI.ensureDebugId("comunicateAFI");
			
		}

		public MenuItem getTa() {
			return ta;
		}
		
		public MenuItem getTaEnd() {
			return taEnd;
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
		
		public MenuItem getLaboralLife() {
			return laboralLife;
		}

		public MenuItem getPeculiarities() {
			return pecs;
		}

		public MenuItem getSSPeculiarities() {
			return pecsSS;
		}
		
//		public MenuItem getMovPrevDelete() {
//			return movPrevDelete;
//		}
		
		public MenuItem getAltaConsolidadaDelete() {
			return altaConsolidadaDelete;
		}

		public MenuItem getComunicateAFI() {
			return comunicateAFI;
		}
		
	}
	
	// ------------------------------------------------- UiFields

	private DeckLayoutPanel deckPanel;
	private HTMLPanel content;
	private HTMLPanel messageContainer;
	private ScrollPanel scrolledPanel;
	private EmployeeWidget employee;
	
	private FullViewer pdfViewer;
	
	
	private static final int EMPLOYEE_INDEX = 0;
	private static final int PDF_VIEWER_INDEX = 1;
	
	// ------------------------------------------------- Class variables
	
	private EmployeeDraftObject employeeDraftObject;
	
	private AonToolbarButton saveContract;
	private AonToolbarButton undoAll;
	private AonToolbarButton undo;
	private AonToolbarButton redo;
	private AonExpandButton tgss;
	private AonToolbarButton closePDF;
	private AonToolbarButton openPDF;
	private DateListBox idcDateListBox;
	private MonthListBox idcMonthListBox;
	
	private Label checkIDCLabel;
	private Label unCheckIDCLabel;
	private AonToolbarButton checkIDCButton;
	private AonToolbarButton unCheckIDCButton;
	
	private NewContextMenu contextMenu;
	
	private Consumer<EmployeeDraftObject> onSaved ;

	// ------------------------------------------------- Constructor

	protected EmployeeDraft() {
		super("Trabajador/a");
		
		fillToolbarPanel();
		hideSearchWidget();
		
		onSaved = this::onSavedNoop;
		
		contextMenu = new NewContextMenu();
		messageContainer = new HTMLPanel(AonStringUtils.EMPTY);
		
		employee = new EmployeeImplementation();
		scrolledPanel = new ScrollPanel();
		scrolledPanel.setWidth("100%");	
		scrolledPanel.setWidget(employee);
		
		content = new HTMLPanel(AonStringUtils.EMPTY);
		content.addStyleName(AON.CSS.aonFlexColumn());
		
		content.add(messageContainer);
		content.add(scrolledPanel);
		
		pdfViewer = new FullViewer();
		
		deckPanel = new DeckLayoutPanel();
		
		deckPanel.add(content);
		deckPanel.add(pdfViewer);
		
		add(deckPanel);
		
	}
	
	@Override
	protected void onClearFilter() {}

	// ------------------------------------------------- setEmployeeDraft

	public void setEmployeeDraftObject(EmployeeDraftObject employeeDraftObject) {
		
		disableSistemaRED();

		showEmployee();
		this.employeeDraftObject = employeeDraftObject;
		this.employeeDraftObject.initializeEmployee(
			r -> {
					employee.initializeView();
					initializeIdcMonthListBox();
					initializeView();
					initializeUndoRedo();
					setVisible(contextMenu.getTaEnd().getElement(), null != employeeDraftObject.getContractData().getEndDate());
					
					
					// Check SS only if not RETA
					Byte ssRegime = employeeDraftObject.getContractData().getSsRegimen();
					if (null == ssRegime || ssRegime != 3) 
						onCheckStatus(getEmployeeDraftObject());
					
					checkTGSSStatus();
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
		if(lastMonth.after(firstMonth)) {
			idcMonthListBox.setFirstMonth(firstMonth);
			idcMonthListBox.setLastMonth(lastMonth);
			int months = DateUtils.getMonths(lastMonth, firstMonth);
			idcMonthListBox.setVisibleRange(0, months+1);
		}
		idcMonthListBox.ensureDebugId("idcMonthListBox");
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
		
		employee.document.setValue(employeeData.getDocument());
		
		if(!employee.checkDocumentValidation(employeeData.getDocument()))
				employee.document.addError();
		else employee.document.removeError();
		
		employee.nationality.setValue(employeeData.getNationality());
		employee.securitySocialNum.setValue(employeeData.getSsNumber(), true);
		
		employee.name.setValue(employeeData.getName());
		employee.firstSurname.setValue(employeeData.getSurName());
		employee.secondSurname.setValue(employeeData.getSecondSurName());
		
		employee.birthDate.setValue(employeeData.getBirthdate(), true);
		employee.gender.setValue(String.valueOf(employeeData.getGender()));
		employee.civilStatus.setValue(employeeData.getCivilStatus()+"");
		
		employee.streetType.setValue(employeeData.getStreetType());
		employee.address.setValue(employeeData.getAddress());
		employee.addressNum.setValue(employeeData.getAddresNum());
		employee.addressInfo.setValue(employeeData.getAddressInfo());
		employee.addressZip.setValue(employeeData.getAddressZip());
		
		employee.selectProvince(employeeData.getAddressProvinces());
		employee.updateMunicipalities();
		employee.addressMunicipality.setValue(employeeData.getAddressCityDescription());

		employee.mobile.setValue(employeeData.getMobile());
		employee.phone.setValue(employeeData.getPhone());
		employee.email.setValue(employeeData.getEmail());
		
		employee.payMethod.setValue(employeeData.getPaymethodId()+"");
		employee.account.setValue(employeeData.getAccount());
		employee.bic.setValue(employeeData.getBic());
		employee.reformatAccount(employee.account);
	}
	
	private void fillExistingContract() {
		ContractInfo contractData = employeeDraftObject.getContractData();
		
		//RETA, habi­a algo mas que determinaba si era o no RETA
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
		employee.ssRegimeType.setValue(contractData.getSsRegimen()+"");
		employee.mdTBTLB.setValue(contractData.getMdTBT()+"");
		employee.workplace.setValue(contractData.getWorkplaceId()+"");
		
		employee.startDate.setValue(contractData.getStartDate());
		employee.seniorityDate.setValue(contractData.getSeniorityDate());
		employee.endDate.setValue(contractData.getEndDate());
		
		Integer agreementId = contractData.getAgreementId();
		employee.agreement.setValue(employeeDraftObject.getAgreementDescription());
		if(null != agreementId) {
			getAgreementLevels(agreementId, s-> {
				employee.level.setValue(contractData.getAgreementLevelId()+"");
				employee.category.setValue(contractData.getAgreementCategory());
			}, f -> {});
		}
		
		employee.rlce.setValue(contractData.getRlce());
		
		employee.journeyType.setValue((null == contractData.getJourneyType() || contractData.getJourneyType() == 0) ? "false" : "true");
//				DomEvent.fireNativeEvent(Document.get().createChangeEvent(), employee.journeyType);
		
		Double partialityCoef = contractData.getPartialityCoef();
		if( (null == partialityCoef || partialityCoef == 0.00)) {
			partialityCoef = calculatePartialityCoef();
			contractData.setPartialityCoef(partialityCoef);
		}
		employee.partialityCoef.setValue(contractData.getPartialityCoef());
		
		Boolean journeyTypeStr = Boolean.valueOf(employee.journeyType.getValue());
		
		if (Boolean.TRUE.equals(journeyTypeStr))
			employee.showElementsFullTimeJourneyTypeContract();
		else {
			if(null != contractData.getContractJourneyDuration() && null != contractData.getContractJourneyDuration().getContractJourneyDuration() && 
			!contractData.getContractJourneyDuration().getContractJourneyDuration().isEmpty()) {
				employee.journeyDuration.clear();
				AonCustomTextBox journeyDur = new AonCustomTextBox("Duraci\u00f3n de la jornada");
				journeyDur.setValue(contractData.getContractJourneyDuration().getJourneyText());
				journeyDur.setEnable(false);
				employee.journeyDuration.add(journeyDur);
				employee.showElementsPartialTimeContract();
			} else
				employee.showPartialTimeContract();
		}
		
	}

	private void fillContractTable(ContractInfo contractData) {
		if(contractData.getCccType() != null) {
			employee.checkCCCType(contractData.getCccType());
			employee.activityCCC.setValue(contractData.getActivityId()+"/"+contractData.getCccId()+"/"+contractData.getCccType());
		
			if(contractData.getCccType() == (byte)7) {
				employee.showMdCtzContract();
				employee.mdCTZLB.setValue(contractData.getMdctz());
			} else
				employee.hideMdCtzContract();
		}
		
		employee.ssRegimeType.setValue(contractData.getSsRegimen()+"");
		employee.mdTBTLB.setValue(contractData.getMdTBT()+"");
		
		employee.workplace.setValue(contractData.getWorkplaceId()+"");
		
		employee.contractTypeLB.setValue(contractData.getContractType());
		employee.contractFireEventsWithOutValue();
		
		if(!isCompleteJourneyContract(contractData.getContractType())) {
			employee.showPartialTimeContract();
			if(employeeDraftObject.getContractData().getContractJourneyDuration().getContractJourneyDuration().entrySet().isEmpty())
				employee.createJourneyDurationWarning();
			else
				employee.createJourneyDurationInfo(employeeDraftObject.getContractData().getContractJourneyDuration().getJourneyText());
			
			if(null != contractData.getJourneyType()) {
				employee.journeyType.setValue(contractData.getJourneyType() == 0 ? "false" : "true");
				DomEvent.fireNativeEvent(Document.get().createChangeEvent(), employee.journeyType.getListBox()); 
			}
			
			if(null != contractData.getContractJourneyDuration() && null != contractData.getContractJourneyDuration().getContractJourneyDuration() && 
					!contractData.getContractJourneyDuration().getContractJourneyDuration().isEmpty()) {
				employee.journeyDuration.clear();
				AonCustomTextBox journeyDur = new AonCustomTextBox("Duraci\u00f3n de la jornada");
				journeyDur.setValue(contractData.getContractJourneyDuration().getJourneyText());
				journeyDur.setEnable(false);
				employee.journeyDuration.add(journeyDur);
			}
				
			Double partialityCoef = contractData.getPartialityCoef();
			if(null == partialityCoef) partialityCoef = calculatePartialityCoef();
			contractData.setPartialityCoef(partialityCoef);
			employee.partialityCoef.setValue(contractData.getPartialityCoef());	
			
		} else
			employee.showElementsFullTimeContract();
		
		try {
			Integer contractTypeInt = Integer.parseInt(contractData.getContractType());
			if(AonNumberUtils.equals(contractTypeInt, 402) || AonNumberUtils.equals(contractTypeInt, 502)) {
				employee.showEmployeesColective();
				employee.employeesColective.setValue(contractData.getEmployeesColective());
			} else {
				employee.hideEmployeesColective();
				contractData.setEmployeesColective(null);
			}
		} catch (Exception e) {
			// Nothing to do here
		}
		
		employee.modality.setValue(contractData.getContractModel()+"");
		
		employee.startDate.setValue(contractData.getStartDate());
		employee.seniorityDate.setValue(contractData.getSeniorityDate());
		employee.endDate.setValue(contractData.getEndDate());
		
		Integer agreementId = contractData.getAgreementId();
		employee.agreement.setValue(employeeDraftObject.getAgreementDescription());
		if(null != agreementId) {
			getAgreementLevels(agreementId, s -> {
				employee.level.setValue(contractData.getAgreementLevelId()+"");
				employee.category.setValue(contractData.getAgreementCategory());
			}, f -> {});
		}
		
		employee.quoteGroup.setValue(contractData.getQuoteGroup());
		DomEvent.fireNativeEvent(Document.get().createChangeEvent(), employee.quoteGroup.getListBox());
		employee.quoteGroupCotizB.setValue(contractData.getQuoteGroupIdxMonth());
		employee.occupation.setValue(contractData.getOcupation());
		employee.rlce.setValue(contractData.getRlce());
		
		CNO cno = employee.getCNOByCode(contractData.getCno());
		if(cno != null) employee.cnoSB.setValue(cno.getCode() + " - " + cno.getTitle());
		if(!employee.isCnoSelected()) showWarning("CNO", "El CNO es obligatorio para todas las altas a partir del 01/01/2023");
	}
	
	private static boolean isCompleteJourneyContract(String contractTypeCodeStr) {
		if(AonStringUtils.isBlank(contractTypeCodeStr)) return false;
		
		ContractType contractTypeObj = new ContractType();
		ContractTypeRecord contractType = contractTypeObj.getContractType(Integer.parseInt(contractTypeCodeStr));
		return AonStringUtils.equalsIgnoreCase(contractType.getJourneyType(), "C");
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
		employee.level.clearItems();
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
			employee.category.setEnable(false);
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
		
		saveContract = new AonToolbarButton( AON.MSG.saveAction(), AON.CSS.aonIconSave() );
		saveContract.addClickHandler(e -> onSaveContract());
		addToolbarButton(saveContract);
		
		undoAll = new AonToolbarButton( "Deshacer todo", AON.CSS.aonIconUndoAll() );
		undoAll.addClickHandler(e -> onUndoAll());
		addToolbarButton(undoAll);
		
		undo = new AonToolbarButton( AON.MSG.undo(), AON.CSS.aonIconUndo() );
		undo.addClickHandler(e -> onUndo());
		addToolbarButton(undo);
		
		redo = new AonToolbarButton( "Rehacer", AON.CSS.aonIconRedo() );
		redo.addClickHandler(e -> onRedo());
		addToolbarButton(redo);
		
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
		
		addToolbarButton(tgss);
		
		closePDF = new AonToolbarButton( AON.MSG.closed(), AON.CSS.aonIconClose() );
		closePDF.addClickHandler(e -> onClosePDF());
		addToolbarButton(closePDF);

		openPDF = new AonToolbarButton( AON.MSG.reopen(), AON.CSS.aonIconPdf() );
		openPDF.addClickHandler(e -> onOpenPDF());
		addToolbarButton(openPDF);

		idcMonthListBox = new MonthListBox();
		idcMonthListBox.addChangeHandler(e -> showIdcPlNss(idcMonthListBox.getSelectedMonth()));
		addToolbarButton(idcMonthListBox);
		
		idcDateListBox = new DateListBox();
		idcDateListBox.addChangeHandler(e -> showIdc(idcDateListBox.getSelectedDate()));
		addToolbarButton(idcDateListBox);

		
		checkIDCLabel = new Label("Comparar con AON", false);
		checkIDCButton = new AonToolbarButton("", AON.AON_ICON_DISABLE);
		unCheckIDCLabel = new Label("Comparar con AON", false);
		unCheckIDCButton = new AonToolbarButton("", AON.AON_ICON_ENABLE);
		addToolbarButton(checkIDCButton);
		addToolbarButton(checkIDCLabel);
		addToolbarButton(unCheckIDCButton);
		addToolbarButton(unCheckIDCLabel);
		checkIDCButton.addClickHandler( e -> checkIdc());
		unCheckIDCButton.addClickHandler( e -> unCheckIdc());
		checkIDCLabel.setVisible(false);
		checkIDCButton.setVisible(false);
		unCheckIDCLabel.setVisible(false);
		unCheckIDCButton.setVisible(false);
		

	}

	private void onSaveContract() {
		Map<String, String> messageMap = employee.checkSaveAndGetErrors();
		if(messageMap.isEmpty())
			employeeDraftObject.updateEmployee(
					r -> {
						saved();
						setEmployeeDraftObject(employeeDraftObject);
					}, 
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
		ContractTypeRecord contractTypeRecord = null;
		try {
			ContractType contractType = new ContractType();
			contractTypeRecord = contractType.getContractType(Integer.parseInt(employeeDraftObject.getContractData().getContractType()));
		} catch (Exception e) {
			// Nothing to do here
		}
		
		EmployeeAFIDialog dialog = new EmployeeAFIDialog(
				employee.startDate.getValue(),
				employee.endDate.getValue(),
				employee.contractTypeLB.getValue(),
				employee.quoteGroup.getValue(),
				employee.occupation.getValue(),
				employee.partialityCoef.getValue(),
				this.employeeDraftObject.getContractData().getCno(),
				employeeDraftObject.getContractId(),
				employeeDraftObject.getDomainId(),
				employeeDraftObject.getWorkplaceId(),
				this.employeeDraftObject.getContractData().getHolidaysDate(),
				this.employeeDraftObject.getContractData().getSAA(),
				this.employeeDraftObject.getContractData().hasSettle(),
				null == contractTypeRecord ? false : contractTypeRecord.isTransform(),
				false){

					@Override
					protected void onAcceptCB() {
						setEmployeeDraftObject(getEmployeeDraftObject());
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
					protected void onChangeContract(String contract, String partialityCoef, Date date) {
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

					@Override
					protected void onCnoContract(String cno, Date date) {
						// TODO Auto-generated method stub
						
					}
		
		};
			
		dialog.center();
		dialog.show();
	}
	
	private void showTa() {
		showLoading("Obteniendo TA...");
		employeeDraftObject.downloadTa(dataURI -> {
				hideMessage();
				showPdf();
				pdfViewer.open(dataURI);
		}, f -> showError("Error TA", f.getMessage()));
	}
	
	private void showTaEnd() {
		showLoading("Obteniendo TA (Baja)...");
		employeeDraftObject.downloadTaEnd(dataURI -> {
				hideMessage();
				showPdf();
				pdfViewer.open(dataURI);
		}, f -> showError("Error TA (Baja)", f.getMessage()));
	}
	
	private void showIdcPlNss() {
		Date firstDayOfMonth = DateUtils.getFirstDayOfMonth(); 
		Date endDate = employeeDraftObject.getEmployee().getEndDate();
		Date idcPlNssDate = AonDateUtils.min(endDate, firstDayOfMonth); 
		showIdcPlNss(idcPlNssDate);
	}
	
	private void showIdcPlNss(Date month) {
		showLoading("Obteniendo IDC PL NSS...");
		employeeDraftObject.downloadIdcPlNss( 
		month,
		dataURI -> {
				hideMessage();
				showPdf();
				idcMonthListBox.setVisible(true);
				idcMonthListBox.getElement().getStyle().setWidth(100, Unit.PCT);
				idcMonthListBox.setSelected(month, true);
				pdfViewer.open(dataURI);
		}, trowable -> {
			showError("IdcPlNss", trowable.getMessage());
		});
	}
	
	private void showIdc() {
		showIdc(idcDateListBox.getSelected());
	}
	
	private void checkIdc() {
		employeeDraftObject.checkIdc(
			null, 
			pdfViewer.getDataURI(), 
			dataURI -> {
				checkIDCLabel.setVisible(false);
				checkIDCButton.setVisible(false);
				unCheckIDCLabel.setVisible(true);
				unCheckIDCButton.setVisible(true);
				
				pdfViewer.open(dataURI);
			}, 
			trowable -> {
				showError("Idc", trowable.getMessage());
			});
	}
	
	private void unCheckIdc() {
		unCheckIDCLabel.setVisible(false);
		unCheckIDCButton.setVisible(false);
		checkIDCLabel.setVisible(true);
		checkIDCButton.setVisible(true);
	}

	private void showIdc(Date date) {
		showLoading("Obteniendo IDC...");
		employeeDraftObject.downloadIdc( 
		date,
		dataURI -> {
				hideMessage();
				showPdf();
				idcDateListBox.setVisible(true);
				checkIDCLabel.setVisible(true);
				checkIDCButton.setVisible(true);
				
				idcDateListBox.getElement().getStyle().setWidth(100, Unit.PCT);
				idcDateListBox.setSelected(date, true);
				pdfViewer.open(dataURI);
		}, trowable -> {
			showError("Idc", trowable.getMessage());
			Date idcDate = idcDateListBox.getSelected();
			if ( !Objects.equals(date, idcDate ) )
				showIdc();
		});
	}
	
	private void showLaboralLife() {
		showLoading("Obteniendo vida laboral...");
		employeeDraftObject.downloadLaboralLife(dataURI -> {
				hideMessage();
				showPdf();
				pdfViewer.open(dataURI);
		}, f -> showError("Error Vida Laboral", f.getMessage()));
	}
	
	private void movPrevDelete() {
		showLoading("Borrando movimiento previo...");
		employeeDraftObject.movPrevDelete(s -> {
			hideMessage();
			setEmployeeDraftObject(getEmployeeDraftObject());
		}, f -> showError("Error borrado movimiento previo", f.getMessage()));
	}

	private void altaConsolidadaDelete() {
		showLoading("Borrando alta consolidad...");
		employeeDraftObject.altaConsolidadaDelete(s -> {
			hideMessage();
			setEmployeeDraftObject(getEmployeeDraftObject());
		}, f -> showError("Error borrado alta consolidada", f.getMessage()));
	}

	private void onComunicateAFI() {
		ContractTypeRecord contractTypeRecord = null;
		try {
			ContractType contractType = new ContractType();
			contractTypeRecord = contractType.getContractType(Integer.parseInt(employeeDraftObject.getContractData().getContractType()));
		} catch (Exception e) {
			// Nothing to do here
		}
		
		new EmployeeAFIDialog(employee.startDate.getValue(), employee.endDate.getValue(), 
				employee.contractTypeLB.getValue(),
				employee.quoteGroup.getValue(), 
				employee.occupation.getValue(),
				employee.partialityCoef.getValue(), 
				this.employeeDraftObject.getContractData().getCno(),
				this.employeeDraftObject.getContractData().getContractId(),
				this.employeeDraftObject.getEmployeeData().getDomain(),
				this.employeeDraftObject.getContractData().getWorkplaceId(), 
				this.employeeDraftObject.getContractData().getHolidaysDate(),
				this.employeeDraftObject.getContractData().getSAA(),
				this.employeeDraftObject.getContractData().hasSettle(),
				null == contractTypeRecord ? false : contractTypeRecord.isTransform(),
				true) {

			@Override
			protected void onAcceptCB() {
				// Nothing to do here
			}

			@Override
			protected void onPartialityCoefContract(String partialityCoef, Date date) {
				showLoading("Comunicando coeficiente parcialidad (TGSS) ...");
				employeeDraftObject.cambioCoef(partialityCoef, date,
						s -> showSuccess("AVISO: Parcialidad",
								"El coeficiente de parcialidad ha sido notificado a la Seguridad Social."),
						f -> showError("Error comunicaci\u00F3n", f.getMessage()));
			}
			
			@Override
			protected void onCnoContract(String cno, Date date) {
				showLoading("Comunicando CNO (TGSS) ...");
				employeeDraftObject.cambioCno(cno, date,
						s -> showSuccess("AVISO: CNO",
								"El cambio de CNO ha sido notificado a la Seguridad Social."),
						f -> showError("Error comunicaci\u00F3n", f.getMessage()));
			}

			@Override
			protected void onOcupationContract(String ocupation, Date date) {
				showLoading("Comunicando ocupaci\u00f3n (TGSS) ...");
				employeeDraftObject.cambioOcupacion(ocupation, date,
						s -> showSuccess("AVISO: Ocupaci\u00F3n",
								"El cambio de ocupaci\u00F3n ha sido notificado a la Seguridad Social."),
						f -> showError("Error comunicaci\u00F3n", f.getMessage()));
			}

			@Override
			protected void onQuoteContract(String quoteGroup, Date date) {
				showLoading("Comunicando grupo cotizaci\u00f3n (TGSS) ...");
				employeeDraftObject.cambioGrupCtz(quoteGroup, date,
						s -> showSuccess("AVISO: Grupo cotizaci\u00F3n",
								"El cambio de grupo de cotizaci\u00F3n ha sido notificado a la Seguridad Social."),
						f -> showError("Error comunicaci\u00F3n", f.getMessage()));
			}

			@Override
			protected void onChangeContract(String contract, String partialityCoef, Date date) {
				showLoading("Comunicando cambio TC2 (TGSS) ...");
				employeeDraftObject.cambioContrato(contract, partialityCoef, date,
						s -> showSuccess("AVISO: Tipo contrato",
								"El cambio de tipo de contrato ha sido notificado a la Seguridad Social."),
						f -> showError("Error comunicaci\u00F3n", f.getMessage()));
			}

			@Override
			protected void onEndContract(String settleReason) {
				showLoading("Comunicando baja (TGSS) ...");
				employeeDraftObject.sendEmployeeBaja(settleReason, s -> {
					showSuccess("AVISO: Baja", "La baja de este trabajador ha sido notificada a la Seguridad Social.");
					downloadTAEnd();
				}, f -> showError("Error comunicaci\u00F3n", f.getMessage()));
			}

			@Override
			protected void onStartContract() {
				showLoading("Comunicando alta (TGSS) ...");
				employeeDraftObject.sendEmployeeAlta(s -> {
					showSuccess("AVISO: Alta", "El alta de este trabajador ha sido notificado a la Seguridad Social.");
					downloadStartDocuments();
				}, f -> showError("Error comunicaci\u00F3n", f.getMessage()));
			}
		};
	}
	
	private void downloadStartDocuments() {
		showLoading("Descargando TA (Alta) ....");
		employeeDraftObject.downloadTa(s -> {
			showSuccess("TA (Alta)",
					"Se ha descargado el TA (Alta) del trabajador. El documento se encuentran en el apartado de Documentos");
			downloadStartIdc();
		}, f -> {
			showError("Error obtenci\u00F3n TA (Alta)", f.getMessage());
			downloadStartIdc();
		}, "ALTA");
	}

	private void downloadStartIdc() {
		Timer timer = new Timer() {
			@Override
			public void run() {
				showLoading("Descargando IDC....");
				employeeDraftObject.downloadIdc(null, s -> showSuccess("IDC",
						"Se ha descargado el IDC del trabajador. El documento se encuentran en el apartado de Documentos"),
						f -> showError("Error obtenci\u00F3n IDC", f.getMessage()));
			}
		};
		timer.schedule(2500);
	}
	
	private void downloadTAEnd() {
		showLoading("Descargando TA...");
		employeeDraftObject.downloadTa(s -> showSuccess("TA (Baja)",
				"Se han descargado el TA (Baja) del trabajador. El documento se encuentran en el apartado de Documentos"),
				f -> showError("Error obtenci\u00F3n TA (Baja)", f.getMessage()), "BAJA");
	}
	
	private void onClosePDF() {
		showEmployee();
	}
	
	private void onOpenPDF() {
		open(pdfViewer.getDataURI(), "_blank");
	}

	// ------------------------------------------------- Toolbar panel auxiliar methods
	
	private void showPdf() {
		saveContract.setVisible(false);
		undoAll.setEnabled(true);
		undoAll.setVisible(false);
		undo.setEnabled(true);
		undo.setVisible(false);
		redo.setEnabled(true);
		redo.setVisible(false);
		tgss.setVisible(false);
		
		openPDF.setVisible(true);
		closePDF.setVisible(true);
		
		idcDateListBox.setVisible(false);
		idcMonthListBox.setVisible(false);
		checkIDCLabel.setVisible(false);
		checkIDCButton.setVisible(false);
		unCheckIDCLabel.setVisible(false);
		unCheckIDCButton.setVisible(false);

		deckPanel.showWidget(PDF_VIEWER_INDEX);		
	}
	
	private void showEmployee() {
		saveContract.setVisible(true);
		undoAll.setVisible(true);
		undoAll.setEnabled(null != employeeDraftObject && employeeDraftObject.canUndo());
		undo.setVisible(true);
		undo.setEnabled(null != employeeDraftObject && employeeDraftObject.canUndo());
		redo.setVisible(true);
		redo.setEnabled(null != employeeDraftObject && employeeDraftObject.canRedo());
		tgss.setVisible(true);
		
		openPDF.setVisible(false);
		closePDF.setVisible(false);
		
		idcDateListBox.setVisible(false);
		idcMonthListBox.setVisible(false);
		checkIDCLabel.setVisible(false);
		checkIDCButton.setVisible(false);
		unCheckIDCLabel.setVisible(false);
		unCheckIDCButton.setVisible(false);
		
		deckPanel.showWidget(EMPLOYEE_INDEX);		
	}
	
	// ------------------------------------------------- Callback saved for check status
	
	private void saved() {
		showSuccess("Guardado", "El contrato " + employeeDraftObject.getEmployeeFullName() + " ha sido actualizado correctamente");
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
		employee.occupation.setValue(occupation);
		DomEvent.fireNativeEvent(Document.get().createChangeEvent(), employee.occupation);
	}
	
	
	public void disableSistemaRED() {
		contextMenu.getTa().setEnabled(false);
		contextMenu.getTaEnd().setEnabled(false);
		contextMenu.getIdc().setEnabled(false);
		contextMenu.getIdcPlNss().setEnabled(false);
		contextMenu.getLaboralLife().setEnabled(false);
		contextMenu.getSSPeculiarities().setEnabled(false);
		contextMenu.getAltaConsolidadaDelete().setEnabled(false);
		contextMenu.getComunicateAFI().setEnabled(false);
	}

	public void enableSistemaRED() {
		contextMenu.getTa().setEnabled(true);
		contextMenu.getTaEnd().setEnabled(true);
		contextMenu.getIdc().setEnabled(true);
		contextMenu.getIdcPlNss().setEnabled(true);
		contextMenu.getLaboralLife().setEnabled(true);
		contextMenu.getSSPeculiarities().setEnabled(true);
		contextMenu.getAltaConsolidadaDelete().setEnabled(true);
		contextMenu.getComunicateAFI().setEnabled(true);
	}

	public void initializeIdcDateListBox(List<Date> dates) {
		idcDateListBox.setRowCount(1, true);
		Date startDate = employeeDraftObject.getEmployee().getStartDate();
		idcDateListBox.setRowData(0, Collections.singletonList(startDate));
		idcDateListBox.setSelected(startDate, true);

		// filter out 'Baja' dates
		//dates = filterEven(dates);
		Collections.sort(dates);
		int count = dates.size();
		idcDateListBox.setRowCount(count, true);
		idcDateListBox.setRowData(0, dates);

		Date selectedDate =
		dates.stream()
		.filter(d -> Objects.equals(d,startDate))
		.findAny().orElse(dates.get(count-1));
		
		idcDateListBox.setSelected(selectedDate, true);
	}

	private static <T> List<T> filterEven( List<T> list ){
		return filter(list, i -> i % 2 == 0);
	}
	
	private static <T> List<T> filter( List<T> list , Function<Integer, Boolean> filter){
		return IntStream.range(0, list.size()).filter( i -> filter.apply(i)).mapToObj(i -> list.get(i) ).collect(Collectors.toList());
	}
	
	// ------------------------------------------------- TGSS status

	private void checkTGSSStatus() {
		Date startDate = employeeDraftObject.getContractData().getStartDate();
		Date endDate = employeeDraftObject.getContractData().getEndDate();

		setVisible(contextMenu.getTaEnd().getElement(), null != endDate);
		setVisible(contextMenu.getAltaConsolidadaDelete().getElement(),
				DateUtils.isAfterOrEquals(new Date(), startDate));
		setVisible(contextMenu.getComunicateAFI().getElement(), employeeDraftObject.isComunica());
	}
	
	// ------------------------------------------------- Aon Messages panel
	
	protected Panel getMessagePanel() {
		return messageContainer;
	}
	
	protected void hideMessage() {
		AonMessagePanel.hideMessage(getMessagePanel());
	}

	protected void showLoading(String message) {
		AonMessagePanel.showLoading(getMessagePanel(), message);
	}

	protected void showError(String title, String message) {
		Map<String, String> errorMap = new HashMap<>();
		errorMap.put(title, message);
		AonMessagePanel.showError(getMessagePanel(), errorMap);
	}
	
	protected void showWarning(String title, String message) {
		Map<String, String> messages = new HashMap<>();
		messages.put(title, message);
		AonMessagePanel.showWarning(getMessagePanel(), messages);
	}
	
	protected void showSuccess(String title, String message) {
		Map<String, String> successMap = new HashMap<>();
		successMap.put(title, message);
		AonMessagePanel.showSuccess(getMessagePanel(), successMap);
	}
	
	private static native void open(String datauristring, String name) /*-{

		//var string = doc.output(datauristring);
        var iframe = "<iframe src='" + datauristring + "' frameborder='0' style='border:0; top:0px; left:0px; bottom:0px; right:0px; width:100%; height:100%;' allowfullscreen></iframe>";
        var x = window.open("", name);
        x.document.open();
        x.document.write(iframe);
        x.document.close();

  	}-*/;

}
