package com.esferalia.aon.gwt.payroll.client;

import java.util.Date;
import java.util.function.Consumer;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.DateListBox;
import com.esferalia.aon.gwt.common.client.widget.MonthListBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonExpandButton;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbar;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.gwt.payroll.shared.Agreement;
import com.esferalia.aon.gwt.payroll.shared.Agreement.Level;
import com.esferalia.aon.gwt.payroll.shared.ContractInfo;
import com.esferalia.aon.gwt.payroll.shared.EmployeeInfo;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.Widget;

import net.aonsolutions.gwt.pdfjs.client.Viewer;

public class EmployeeDraft extends Composite {
	
	private class EmployeeImplementation extends Employee{
		
		// TABLA DATOS CONTRATO
		
		@Override
		public void onClearEmployeeClick() {}

		@Override
		public void onEmployeeDocumentSuggestionChange(String document) {}

		@Override
		public void onEmployeeDocumentChange(String document, String document_type) {
			employeeDraftObject.setEmployeeDocument(document);
			employeeDraftObject.setEmployeeDocumentType(document_type);
		}

		@Override
		public void onEmployeeNationalityChange(String countryIso2) {
			employeeDraftObject.setNationality(countryIso2);
		}

		@Override
		public void onEmployeeSSNumSuggestionChange(String ssNumber) {}

		@Override
		public void onEmployeeSSNumChange(String ssNumber) {
			employeeDraftObject.setEmployeeSocialSecurityNum(ssNumber);
		}

		@Override
		public void onEmployeeNameSuggestionChange(String nameSurname) {}

		@Override
		public void onEmployeeNameChange(String name) {
			employeeDraftObject.setEmployeeName(name);
		}

		@Override
		public void onEmployeeFirstSurnameSuggestionChange(String nameSurname) {}

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
		public void onContractJourneyTypeChange(Boolean journey_type) {
			 employeeDraftObject.setContractJourneyType(journey_type);
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
			SSBonusDraft dialog = new SSBonusDraft(employeeDraftObject.getContractId());
			dialog.setPopupPositionAndShow((x,y) -> dialog.center() );
		}
	}
	
	class NewContextMenu extends ContextMenu {
		
		private MenuItem ta;
		private MenuItem afi;
		private MenuItem idc;
		private MenuItem idcPlNss;		
		private MenuItem peculiarities = null;
		private MenuItem bonifications = null;
		
		public NewContextMenu() {
			
			afi = addItem("Cambios AFI", new AFICommand(), 
					AON.CSS.aonIconTgss(), AON.AON_ICON_CMD_BUTTON, style.cmd_btn());
			afi.ensureDebugId("afi");
			
			peculiarities = addItem("Peculiaridades de cotizaci" + String.valueOf("\u00F3") + "n", new PeculiaritiesCommand(), 
					AON.CSS.aonIconTgss(), AON.AON_ICON_CMD_BUTTON, style.cmd_btn());
			peculiarities.ensureDebugId("peculiarities");
			
			bonifications = addItem("Bonificaciones", new BonificationsCommand(), 
					AON.CSS.aonIconTgss(), AON.AON_ICON_CMD_BUTTON, style.cmd_btn());
			bonifications.ensureDebugId("bonifications");
			
			ta = addItem("Duplicados de Documentos TA", new TACommand(), 
					AON.CSS.aonIconTgss(), AON.AON_ICON_CMD_BUTTON, style.cmd_btn());
			ta.ensureDebugId("ta");
			
			idc = addItem("Informe de Cotizaci\u00F3n-Trab Cuenta Ajena", new IDCCommand(), 
					AON.CSS.aonIconTgss(), AON.AON_ICON_CMD_BUTTON, style.cmd_btn());
			idc.ensureDebugId("idc");
			
			idcPlNss = addItem("Informe de Cotizaci\u00F3n/Periodo iquidaci\u00F3n-NSS", new IDCPlNssCommand(), 
					AON.CSS.aonIconTgss(), AON.AON_ICON_CMD_BUTTON, style.cmd_btn());
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
			return peculiarities;
		}

		public MenuItem getBonifications() {
			return bonifications;
		}
		
	}
	
	// ------------------------------------------------- UiFields
	
	@UiField
	MyStyle style;

	interface MyStyle extends CssResource {
		String cmd_btn();
	}
	
	@UiField
	DockLayoutPanel dockLayoutPanel;
	
	@UiField (provided = true)
	Employee employee;
	
	@UiField
	DeckPanel deckPanel;

	@UiField
	Viewer pdfViewer;
	
	// ------------------------------------------------- Class variables
	
	private EmployeeDraftObject employeeDraftObject;
	
	private AonToolbar toolbar;
	private AonToolbarButton saveContract;
	private AonToolbarButton undoAll;
	private AonToolbarButton undo;
	private AonToolbarButton redo;
	private AonExpandButton tgss;
	private AonToolbarButton closePDF;
	private AonToolbarButton downloadPDF;
	private ListBox zoomListBox;
	private DateListBox idcDateListBox;
	private MonthListBox idcMonthListBox;
	private int zoom;
	
	private NewContextMenu contextMenu;
	
	private Consumer<EmployeeDraftObject> onSaved ;

	// ------------------------------------------------- Constructor

	public EmployeeDraft() {
		this.zoom = Constants.DEFAULT_ZOOM;
		
		employee = new EmployeeImplementation();
		
		initWidget(uiBinder.createAndBindUi(this));
		
		contextMenu = new NewContextMenu();
		
		toolbar = getToolbarPanel();
		dockLayoutPanel.addNorth( toolbar , AonToolbar.HEIGTH );
		
		employee.hideClearEmployee();
		
		onSaved = this::onSavedNoop;
				
		initZoomList();
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
			}, t -> {}
		);
	
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
		contextMenu.getIdc().setEnabled(false);
		contextMenu.getIdc().setVisible(false);
		employeeDraftObject.getIdcDates(
		(dates) -> {
			int count = dates.size();
			idcDateListBox.setRowCount(count, true);
			idcDateListBox.setRowData(0, dates);
			idcDateListBox.setVisibleRange(0, count+1);
			idcDateListBox.setSelected(count-1, true);
			idcDateListBox.onResizeDropDownPopup();
			contextMenu.getIdc().setEnabled(true);
			contextMenu.getIdc().setVisible(true);
		}, 
		(error) -> {
		} );
	}

	private void initializeUndoRedo() {
		employeeDraftObject.clearUndoMaganager();
		undo.setEnabled(employeeDraftObject.canUndo());
		undoAll.setEnabled(employeeDraftObject.canUndo());
		redo.setEnabled(employeeDraftObject.canRedo());

		employeeDraftObject.addUndoManagerListener( (undoManager) -> {
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
		employee.initActivitiesCCC(employeeDraftObject.getActivities(), employeeDraftObject.getCCCs());
	}
	
	private void initWorkplaces() {
		employee.initWorkplaces(employeeDraftObject.getWorkplaces());
	}
	
	private void initContractType() {
		employee.initContractType();
	}
	
	private void initAgreements() {
		employee.initAgreements(employeeDraftObject.getActiveAgreements());
	}
	
	private void initPayMethods() {
		employee.initPayMethods(employeeDraftObject.getPayMethods());
	}
	
	private void initIbans() {
		employee.initIbans(employeeDraftObject.getExistingIban());
	}
	
	private void initFocus() {
		//FOCUS DOCUMENT
		Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand () {
	        public void execute () {
	        	employee.document.setFocus(true);
	        }
		});
	}
	
	// ------------------------------------------------- Initialize existing employee
	
	public void initExistingEmployee( boolean hasPayroll){
		fillExistingEmployee();
		fillExistingContract();
		if(hasPayroll)
		   employee.blockVariablesExistingContract();
		else
		   employee.unblockVariablesExistingContract();
	}
	
	private void fillExistingEmployee() {
		EmployeeInfo employeeData = employeeDraftObject.getEmployeeData();
		
		employee.document.setValue(employeeData.getDocument(), true);
		employee.nationality.setValue(employeeData.getNationality());
		employee.security_social_num.setValue(employeeData.getSsNumber(), true);
		
		employee.name.setValue(employeeData.getName());
		employee.first_surname.setValue(employeeData.getSurName());
		employee.second_surname.setValue(employeeData.getSecondSurName());
		
		employee.birth_date.setValue(employeeData.getBirthdate(), true);
		setSelectedValueLB(employee.gender, String.valueOf(employeeData.getGender()));
		setSelectedValueLB(employee.civilStatus, employeeData.getCivilStatus()+"");
		
		setSelectedValueLB(employee.street_type, employeeData.getStreetType());
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
		
		employee.start_date.setValue(contractData.getStartDate());
		employee.seniority_date.setValue(contractData.getSeniorityDate());
		employee.end_date.setValue(contractData.getEndDate());
		
		Integer agreementId = contractData.getAgreementId();
		setSelectedValueLB(employee.agreement, agreementId+"/"+contractData.getAgreementColective());
		if(null != agreementId) {
			getAgreementLevels(agreementId, s -> {
				setSelectedValueLB(employee.level, contractData.getAgreementLevelId()+"");
				employee.category.setValue(contractData.getAgreementCategory());
			}, f -> {});
		}
		
		setSelectedValueLB(employee.journeyType, contractData.getJourneyType()+"");
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
		
		Integer contractTypeInt = Integer.parseInt(contractData.getContractType());
		if(AonNumberUtils.between(contractTypeInt, 200, 300) || AonNumberUtils.between(contractTypeInt, 500, 599) || AonNumberUtils.equals(contractTypeInt, 0)) {
			employee.showPartialTimeContract();
			if(employeeDraftObject.getContractData().getContractJourneyDuration().getContractJourneyDuration().entrySet().size() == 0) {
				employee.createJourneyDurationWarning();
			} else {
				employee.createJourneyDurationInfo(employeeDraftObject.getContractData().getContractJourneyDuration().getJourneyText());
			}
		} else
			employee.showElementsFullTimeContract();
		
		employee.updateModality(contractTypeInt);
		setSelectedValueLB(employee.modality, contractData.getContractModel()+"");
		
		employee.start_date.setValue(contractData.getStartDate());
		employee.seniority_date.setValue(contractData.getSeniorityDate());
		employee.end_date.setValue(contractData.getEndDate());
		
		Integer agreementId = contractData.getAgreementId();
		setSelectedValueLB(employee.agreement, agreementId+"/"+contractData.getAgreementColective());
		if(null != agreementId) {
			getAgreementLevels(agreementId, s -> {
				setSelectedValueLB(employee.level, contractData.getAgreementLevelId()+"");
				employee.category.setValue(contractData.getAgreementCategory());
			}, f -> {});
		}
		
		setSelectedValueLB(employee.quote_group, contractData.getQuoteGroup());
		setSelectedValueLB(employee.occupation, contractData.getOcupation());
		employee.partiality_coef.setValue(contractData.getPartialityCoef());	
	}
	
	// ------------------------------------------------- Auxiliar Methods

	private void getAgreementLevels(Integer agreementId, Consumer<Agreement> success, Consumer<Throwable> failure) {
		employee.level.clear();
		employee.level.addItem("-", "-1");
		
		employeeDraftObject.getAgreement(agreementId,  
		(agreement) -> {
			for (Level levelRecord : agreement.getLevels())
				for (String categoryRecord : agreement.getCategoriesMap().get(levelRecord.getId()))
					employee.level.addItem(levelRecord.getDescription() + " - " + categoryRecord, String.valueOf(levelRecord.getId()));

			success.accept(agreement);
		},
		(throwable) -> {
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
	
	private void initZoomList() {
		zoomListBox = new ListBox();
		for (int zoom = Constants.MIN_ZOOM; zoom < Constants.DEFAULT_ZOOM; zoom += Constants.ZOOM_STEP)
			zoomListBox.addItem(Constants.PERCENT_FORMAT.format((double) zoom / 100));
		int selectedIndex = zoomListBox.getItemCount();
		for (int zoom = Constants.DEFAULT_ZOOM; zoom < Constants.MAX_ZOOM; zoom += Constants.ZOOM_STEP)
			zoomListBox.addItem(Constants.PERCENT_FORMAT.format((double) zoom / 100));
		zoomListBox.addItem(Constants.PERCENT_FORMAT.format((double) Constants.MAX_ZOOM / 100));
		zoomListBox.setSelectedIndex(selectedIndex);
		
	}
	
	// ------------------------------------------------- Toolbar panel
	
	private AonToolbar getToolbarPanel() {
		
		AonToolbar toolbar = new AonToolbar("Contrato");
		
		saveContract = new AonToolbarButton( AON.MSG.saveAction(), AON.CSS.aonIconSave() );
		saveContract.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				onSaveContract(event);
			}
		});
		toolbar.add(saveContract);
		
		undoAll = new AonToolbarButton( "Deshacer todo", AON.CSS.aonIconUndoAll() );
		undoAll.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				onUndoAll(event);
			}
		});
		toolbar.add(undoAll);
		
		undo = new AonToolbarButton( AON.MSG.undo(), AON.CSS.aonIconUndo() );
		undo.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				onUndo(event);
			}
		});
		toolbar.add(undo);
		
		redo = new AonToolbarButton( "Rehacer", AON.CSS.aonIconRedo() );
		redo.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				onRedo(event);
			}
		});
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
		closePDF.setAccessKey('I');
		closePDF.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				onClosePDF(event);
			}
		});
		toolbar.add(closePDF);
		
		initZoomList();
		zoomListBox.addChangeHandler(e -> {
			int index =zoomListBox.getSelectedIndex();
			String text = zoomListBox.getItemText(index);
			zoom = (int) (Constants.PERCENT_FORMAT.parse(text));
			pdfViewer.scale(zoom / 100.00);
		});
		toolbar.add(zoomListBox);
		zoomListBox.setVisible(false);
		
		idcMonthListBox = new MonthListBox();
		idcMonthListBox.addChangeHandler(e -> {
			showIdcPlNss(idcMonthListBox.getSelectedMonth());
		});
		toolbar.add(idcMonthListBox);
		
		idcDateListBox = new DateListBox();
		idcDateListBox.addChangeHandler(e -> {
			showIdc(idcDateListBox.getSelectedDate());
		});
		toolbar.add(idcDateListBox);

		downloadPDF = new AonToolbarButton( AON.MSG.download(), AON.CSS.aonIconPdf() );
		downloadPDF.setAccessKey('D');
		downloadPDF.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				onDownloadPDF(event);
			}
		});
		toolbar.add(downloadPDF);
		
		return toolbar;
	}

	private void onSaveContract(ClickEvent event) {
		if(employee.checkIfSaveEmployeeIsPossible())
			employeeDraftObject.updateEmployee(
					r -> { 
						saved();
					}, 
					t -> {}
			);
	}

	private void onUndoAll(ClickEvent event) {
		while ( employeeDraftObject.canUndo() )
			employeeDraftObject.undo();
		initializeView();
	}
	
	private void onUndo(ClickEvent event) {
		employeeDraftObject.undo();
		initializeView();
	}
	
	private void onRedo(ClickEvent event) {
		employeeDraftObject.redo();
		initializeView();
	}
	
	private void onAFIChanges() {
		EmployeeAFIDialog dialog = new EmployeeAFIDialog(
				employee.start_date.getValue(),
				employee.end_date.getValue(),
				employee.contractTypeLB.getSelectedValue(),
				employee.quote_group.getSelectedValue(),
				employee.occupation.getSelectedValue(),
				employee.partiality_coef.getValue(),
				employeeDraftObject.getPayrollDate(),
				employeeDraftObject.getContractId(),
				employeeDraftObject.getDomainId(),
				employeeDraftObject.getWorkplaceId()
				){

					@Override
					protected void onAcceptCB() {}

					@Override
					protected void onPartialityCoefContract(String partialityCoef, Date date) {}

					@Override
					protected void onOcupationContract(String ocupation, Date date) {}

					@Override
					protected void onQuoteContract(String quoteGroup, Date date) {}

					@Override
					protected void onChangeContract(String contract, Date date) {}

					@Override
					protected void onEndContract() {}

					@Override
					protected void onStartContract() {}
		
		};
			
		dialog.center();
		dialog.show();
	}

	private void onClosePDF(ClickEvent event) {
		showEmployee();
	}
	
	private void onDownloadPDF(ClickEvent event) {
		String fileName = employeeDraftObject.getEmployeeFullName() + " IDC.pdf";
		pdfViewer.download(fileName);
	}
	
	// ------------------------------------------------- Toolbar panel auxiliar methods
	
	private void showTa() {
		employeeDraftObject.downloadTa(
		(dataURI) -> {
				showPdf();
				pdfViewer.setDocument(dataURI, zoom / 100.00);
		}, 
		(trowable)-> {
			
		}
		);
	}
	
	private void showIdc() {
		showIdc(idcDateListBox.getSelected());
	}

	private void showIdcPlNss() {
		showIdcPlNss(DateUtils.getFirstDayOfMonth());
	}

	private void showIdc( Date date) {
		employeeDraftObject.downloadIdc( 
		date,
		(dataURI) -> {
				showPdf();
				idcDateListBox.setVisible(true);
				idcDateListBox.setSelected(date, true);
				pdfViewer.setDocument(dataURI, zoom / 100.00);
		}, 
		(trowable) -> {}
		);
	}

	private void showIdcPlNss( Date month) {
		employeeDraftObject.downloadIdcPlNss( 
		month,
		(dataURI) -> {
				showPdf();
				idcMonthListBox.setVisible(true);
				idcMonthListBox.setSelected(month, true);
				pdfViewer.setDocument(dataURI, zoom / 100.00);
		}, 
		(trowable) -> {}
		);
	}

	private void showPdf() {
		contextMenu.getAfi().setVisible(false);
		tgss.setVisible(false);
		contextMenu.getTa().setVisible(false);
		contextMenu.getIdc().setVisible(false);
		contextMenu.getIdcPlNss().setVisible(false);
		undo.setVisible(false);
		redo.setVisible(false);
		undoAll.setVisible(false);

		zoomListBox.setVisible(true);
		closePDF.setVisible(true);
		downloadPDF.setVisible(true);
		
		showWidget(pdfViewer);
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
		
		zoomListBox.setVisible(false);
		closePDF.setVisible(false);
		downloadPDF.setVisible(false);
		idcDateListBox.setVisible(false);
		idcMonthListBox.setVisible(false);
		
		showWidget(employee);
	}
	
	private void showWidget(Widget widget) {
		deckPanel.showWidget(deckPanel.getWidgetIndex(widget));
	}
	
	// ------------------------------------------------- Callback saved for check status
	
	private void saved() {
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
		case "b":
			employee.occupation.setSelectedIndex(2);
		case "d":
			employee.occupation.setSelectedIndex(3);
		case "e":
			employee.occupation.setSelectedIndex(4);
		case "f":
			employee.occupation.setSelectedIndex(5);
		case "g":
			employee.occupation.setSelectedIndex(6);
		case "h":
			employee.occupation.setSelectedIndex(7);
		default:
			employee.occupation.setSelectedIndex(0);
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
}
