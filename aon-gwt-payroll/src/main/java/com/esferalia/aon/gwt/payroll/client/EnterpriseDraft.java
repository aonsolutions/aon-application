package com.esferalia.aon.gwt.payroll.client;

import java.util.List;
import java.util.function.Consumer;

import com.esferalia.aon.gwt.payroll.shared.Agreement;
import com.esferalia.aon.gwt.payroll.shared.EnterpriseInfo;
import com.esferalia.aon.gwt.payroll.shared.WorkplaceInfo;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.event.dom.client.ContextMenuHandler;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;

public class EnterpriseDraft extends Composite implements ContextMenuHandler {
	
	private class EnterpriseImplementation extends Enterprise{

		@Override
		public void onEnterpriseNameChange() {
//			enterpriseDraftObject.setName(this.enterpriseName.getValue());
//			saving();
		}

		@Override
		public void onEnterpriseAliasChange() {
//			enterpriseDraftObject.setAlias(this.enterpriseAlias.getValue());
//			saving();
		}

		@Override
		public void onEnterpriseDocumentChange() {
//			enterpriseDraftObject.setDocument(this.document.getValue());
//			onDocumentChange();
//			saving();
			checkStylesDocument();
		}

		@Override
		public void onEnterpriseNationalityChange() {
			enterpriseDraftObject.setNationality(this.nationality.getValue());
			saving();
		}

		@Override
		public void onEnterpriseStreetTypeChange() {
			enterpriseDraftObject.setAddressStreetType(this.streetType.getSelectedItemText());
			saving();
		}

		@Override
		public void onEnterpriseAddressChange() {
//			enterpriseDraftObject.setAddress(this.address.getValue());
//			saving();
		}

		@Override
		public void onEnterpriseAddressNumChange() {
//			enterpriseDraftObject.setAddressNum(this.addressNum.getValue());
//			saving();
		}

		@Override
		public void onEnterpriseAddressZipChange() {
//			enterpriseDraftObject.setAddressZip(this.addressZip.getValue());
//			saving();
		}

		@Override
		public void onEnterpriseAddressCityChange() {
//			enterpriseDraftObject.setAddressCity(this.addressCity.getValue());
//			saving();
		}

		@Override
		public void onEnterpriseAddressProvinceChange() {
			enterpriseDraftObject.setAddressProvince(this.addressProvince.getSelectedItemText());
			saving();
		}

		@Override
		public void onEnterpriseMobileChange() {
//			enterpriseDraftObject.setMobile(this.mobile.getValue());
//			saving();
		}

		@Override
		public void onEnterprisePhoneChange() {
//			enterpriseDraftObject.setPhone(this.phone.getValue());
//			saving();
		}

		@Override
		public void onEnterpriseEmailChange() {
//			enterpriseDraftObject.setEmail(this.email.getValue());
//			saving();
		}

		@Override
		public void onEnterpriseWebChange() {
//			enterpriseDraftObject.setWeb(this.enterpriseWeb.getValue());
//			saving();
		}

		@Override
		public void onEnterprisePaysheetModelChange() {
			enterpriseDraftObject.setPaySheetModel(this.enterprisePaysheetModel.getSelectedIndex());
			saving();
		}

		@Override
		public void onEnterpriseCostModelChange() {
			enterpriseDraftObject.setCostModel(this.enterpriseCostModel.getSelectedIndex());
			saving();
		}

		@Override
		public void onEnterprisePaysheetSendTypeChange() {
			enterpriseDraftObject.setPaySheetSendType(this.enterprisePaysheetSendType.getSelectedIndex());
			checkPaysheetSendType();
			saving();
		}

		@Override
		public void onEnterprisePaysheetSendEmailChange() {
//			enterpriseDraftObject.setPaySheetSendEmail(this.enterprisePaysheetSendEmail.getValue());
//			saving();
		}
		
		@Override
		public void onEnterpriseAgreementChange() {
			enterpriseDraftObject.setAgreement(this.enterpriseAgreement.getSelectedItemText());
			saving();
		}
		
	}
	
	// -------------------------------------------------- UiBinder --------------------------------------------------

	private static EnterpriseDraftUiBinder uiBinder = GWT.create(EnterpriseDraftUiBinder.class);

	interface EnterpriseDraftUiBinder extends UiBinder<Widget, EnterpriseDraft> {
	}

	// -------------------------------------------------- UiFields --------------------------------------------------

	@UiField
	MyStyle style;

	interface MyStyle extends CssResource {
		String hide();
		String paddingEnableDisable();
		String maxWidth();
		String fontDisableStyle();
		String fontEnableStyle();
		String warningColor();
		String maxWidthTextBox();
		String borderNone();
	}
	
	@UiField (provided = true)
	Enterprise enterprise;
	
	@UiField
	Label saveStatus;
	
//	@UiField
//	Button saveButton;
	
	@UiField
	Button newWorkplaceButton;
	
	@UiField
	Button newActivityButton;
	
	@UiField
	Button redoButton;

	@UiField
	Button undoButton;

	@UiField
	Button undoAllButton;

	// ------------------------------------------------------ VARIABLES DE LA CLASE --------------------------------------------------

	private EnterpriseDraftObject enterpriseDraftObject;
	
	private Timer saveTimer;
	
	private Consumer<EnterpriseInfo> onSaved ;

	// ------------------------------------------------ CONSTRUCTOR ------------------------------------------------------

	public EnterpriseDraft() {
		enterprise = new EnterpriseImplementation();
		
		// Inicializamos la vista del empleado
		initWidget(uiBinder.createAndBindUi(this));
		
		saveStatus.setTitle("Cada cambio que hagas se guarda autom\u00E1ticamente");
		onSaved = this::onSavedNoop;
	}

	// ------------------------------------------------- UiHandlers ------------------------------------------------------
	
	public EnterpriseDraft setOnSaved(Consumer<EnterpriseInfo> onSaved) {
		this.onSaved = onSaved;
		return this;
	}

	@UiHandler("undoButton")
	void onUndoButtonClick(ClickEvent event) {
		enterpriseDraftObject.undo();
		initilizeView();
		saving();
	}

	@UiHandler("undoAllButton")
	void onUndoAllButtonClick(ClickEvent event) {
		while ( enterpriseDraftObject.canUndo() )
			enterpriseDraftObject.undo();
		initilizeView();
		saving();
	}

	@UiHandler("redoButton")
	void onRedoButtonClick(ClickEvent event) {
		enterpriseDraftObject.redo();
		initilizeView();
		saving();
	}
	
	private void save() {
		saveStatus.setText("Guardando...");
		enterpriseDraftObject.updateEnterprise(
				r -> { 
					saved();
				}, 
				t -> {
					saveStatus.setText("Error, los cambios no se han guardado");
				}
		);
	}
	
//	@UiHandler("saveButton")
//	void onEnterpriseSaveClcik(ClickEvent event) {
//		if(canSave())
//			enterpriseDraftObject.updateEnterprise(
//					s -> {},
//					f -> {}
//			);
//		else {
//			WarningDialog warningDialog = new WarningDialog("Aviso", "Los campos azules se deben rellenar obligatoriamente.");
//			warningDialog.center();
//			warningDialog.show();
//		}
//	}

//	private boolean canSave() {
//		if("" == enterprise.enterpriseName.getValue() || "" == enterprise.document.getValue() || 
//		   "" == enterprise.address.getValue() || "" == enterprise.addressNum.getValue() || "" == enterprise.addressZip.getValue() ||
//		   "" == enterprise.addressCity.getValue() || 0 == enterprise.addressProvince.getSelectedIndex())
//			return false;
//		else
//			return true;
//	}
	
	@UiHandler("newWorkplaceButton")
	void onNewWorkplaceClcik(ClickEvent event) {
		EmployeeTree.showNewWorkplace();
	}
	
	@UiHandler("newActivityButton")
	void onNewActivityClcik(ClickEvent event) {
		EmployeeTree.showNewActivity();
	}

	// ------------------------------------------------------ METODOS DE LA CLASE --------------------------------------------------

	public void setEnterpriseDraftObject(EnterpriseDraftObject enterpriseDraftObject) {
		this.enterpriseDraftObject = enterpriseDraftObject;
		this.enterpriseDraftObject.initializeEnterprise(
				s -> {
					initilizeView();
					initializeUndoRedo();
					initializeScheduler();
				},
				f -> {}
		);
	}
	
	private void initializeScheduler() {
		saveStatus.setText("");
		saveTimer = new Timer() {
			@Override
			public void run() {
				save();
			}
		};
	}
	
	private void initializeUndoRedo() {
		undoButton.setEnabled(enterpriseDraftObject.canUndo());
		undoAllButton.setEnabled(enterpriseDraftObject.canUndo());
		redoButton.setEnabled(enterpriseDraftObject.canRedo());

		enterpriseDraftObject.addUndoManagerListener( (undoManager) -> {
			undoButton.setEnabled(undoManager.canUndo());
			undoAllButton.setEnabled(undoManager.canUndo());
			redoButton.setEnabled(undoManager.canRedo());
		});
	}

	private void initilizeView() {
		enterprise.initializeView();
		initializeListBox();
		fillEnterpriseData();
		initialiceHandlers();
		
		enterprise.enterpriseOthersTable.getRows().getItem(1).getStyle().setDisplay(Display.NONE);
	}

	private void initialiceHandlers() {
		enterprise.enterpriseName.addKeyUpHandler(e-> {
			
			String value = enterprise.enterpriseName.getValue();
			String saved = enterpriseDraftObject.getEnterpriseInfo().getName();
			if ( AonStringUtils.equals(value, saved))
				return;
			
			enterpriseDraftObject.setName(value);
			saving();
		});
		
		enterprise.enterpriseAlias.addKeyUpHandler(e-> {
			
			String value = enterprise.enterpriseAlias.getValue();
			String saved = enterpriseDraftObject.getEnterpriseInfo().getAlias();
			if ( AonStringUtils.equals(value, saved))
				return;
			
			enterpriseDraftObject.setAlias(value);
			saving();
		});
		
		enterprise.document.addKeyUpHandler(e-> {
			
			String value = enterprise.document.getValue();
			String saved = enterpriseDraftObject.getEnterpriseInfo().getDocument();
			if ( AonStringUtils.equals(value, saved))
				return;
			
			enterpriseDraftObject.setDocument(value);
			saving();
		});
		
		enterprise.address.addKeyUpHandler(e-> {
			
			String value = enterprise.address.getValue();
			String saved = enterpriseDraftObject.getEnterpriseInfo().getAddress();
			if ( AonStringUtils.equals(value, saved))
				return;
			
			enterpriseDraftObject.setAddress(value);
			saving();
		});
		
		enterprise.addressNum.addKeyUpHandler(e-> {
			
			String value = enterprise.addressNum.getValue();
			String saved = enterpriseDraftObject.getEnterpriseInfo().getAddressNum();
			if ( AonStringUtils.equals(value, saved))
				return;
			
			enterpriseDraftObject.setAddressNum(value);
			saving();
		});
		
		enterprise.addressZip.addKeyUpHandler(e-> {
			
			String value = enterprise.addressZip.getValue();
			String saved = enterpriseDraftObject.getEnterpriseInfo().getAddressZip();
			if ( AonStringUtils.equals(value, saved))
				return;
			
			enterpriseDraftObject.setAddressZip(value);
			saving();
		});
		
		enterprise.addressCity.addKeyUpHandler(e-> {
			
			String value = enterprise.addressCity.getValue();
			String saved = enterpriseDraftObject.getEnterpriseInfo().getAddressCity();
			if ( AonStringUtils.equals(value, saved))
				return;
			
			enterpriseDraftObject.setAddressCity(value);
			saving();
		});
		
		enterprise.mobile.addKeyUpHandler(e-> {
			
			String value = enterprise.mobile.getValue();
			String saved = enterpriseDraftObject.getEnterpriseInfo().getMobile();
			if ( AonStringUtils.equals(value, saved))
				return;
			
			enterpriseDraftObject.setMobile(value);
			saving();
		});
		
		enterprise.phone.addKeyUpHandler(e-> {
			
			String value = enterprise.phone.getValue();
			String saved = enterpriseDraftObject.getEnterpriseInfo().getPhone();
			if ( AonStringUtils.equals(value, saved))
				return;
			
			enterpriseDraftObject.setPhone(value);
			saving();
		});
		
		enterprise.email.addKeyUpHandler(e-> {
			
			String value = enterprise.email.getValue();
			String saved = enterpriseDraftObject.getEnterpriseInfo().getEmail();
			if ( AonStringUtils.equals(value, saved))
				return;
			
			enterpriseDraftObject.setEmail(value);
			saving();
		});
		
		enterprise.enterpriseWeb.addKeyUpHandler(e-> {
			
			String value = enterprise.enterpriseWeb.getValue();
			String saved = enterpriseDraftObject.getEnterpriseInfo().getWeb();
			if ( AonStringUtils.equals(value, saved))
				return;
			
			enterpriseDraftObject.setWeb(value);
			saving();
		});
		
		enterprise.enterprisePaysheetSendEmail.addKeyUpHandler(e-> {
			
			String value = enterprise.enterprisePaysheetSendEmail.getValue();
			String saved = enterpriseDraftObject.getEnterpriseInfo().getPaysheetEmail();
			if ( AonStringUtils.equals(value, saved))
				return;
			
			enterpriseDraftObject.setPaySheetSendEmail(value);
			saving();
		});
	}

	private void initializeListBox() {
		//SCOPE
		initializeScopeCell();
		
		// CONVENIO
		enterprise.enterpriseAgreement.addItem("-");
		List<Agreement> agreements = enterpriseDraftObject.getActiveAgreements();
		for (Agreement a : agreements) {
			enterprise.enterpriseAgreement.addItem(a.getDescription());
		}
		
		//CALENDARIO
		initializeCalendarCell();		
	}

	private void initializeScopeCell() {
		Widget enterpriseScopeWidget;
		if(enterpriseDraftObject.getEnterprisecopes().values().size() == 1){
			Integer scopeId = (Integer) enterpriseDraftObject.getEnterprisecopes().keySet().toArray()[0];
			enterpriseScopeWidget = new Label((null == scopeId) ? "" : enterpriseDraftObject.getEnterprisecopes().get(scopeId));
			enterpriseScopeWidget.setStyleName("aon-inputText");
			enterpriseScopeWidget.addStyleName(style.maxWidthTextBox());
			enterprise.enterpriseScopePanel.add(enterpriseScopeWidget);
			enterpriseDraftObject.setScopeId(scopeId); //AutoSeleccion
		}else{
			enterpriseScopeWidget = new ListBox();
			for(String scope : enterpriseDraftObject.getEnterprisecopes().values()){
				((ListBox) enterpriseScopeWidget).addItem(scope);
			}
			enterpriseScopeWidget.setStyleName("aon-selectOneMenu");
			enterpriseScopeWidget.addStyleName(style.maxWidth());
			((ListBox) enterpriseScopeWidget).addChangeHandler(new ChangeHandler() {
				
				@Override
				public void onChange(ChangeEvent event) {
					enterpriseDraftObject.setScope(((ListBox) enterpriseScopeWidget).getSelectedItemText());
					saving();
				}
			});
			if(((ListBox) enterpriseScopeWidget).getItemCount() != 0){
				((ListBox) enterpriseScopeWidget).setSelectedIndex(enterpriseDraftObject.getScopeIndex());
			}
			enterprise.enterpriseScopePanel.add(enterpriseScopeWidget);
		}
	}

	private void initializeCalendarCell() {
		Widget enterpriseCalendarWidget;
		if(enterpriseDraftObject.getEnterpriseCalendars().values().size() == 0){
			enterpriseCalendarWidget = new Label("No hay calendarios disponibles");
			enterpriseCalendarWidget.setStyleName("aon-inputText");
			enterpriseCalendarWidget.addStyleName(style.maxWidthTextBox());
			enterpriseCalendarWidget.addStyleName(style.warningColor());
			enterpriseCalendarWidget.addStyleName(style.borderNone());
			enterprise.enterpriseCalendarPanel.add(enterpriseCalendarWidget);
		}else if(enterpriseDraftObject.getEnterpriseCalendars().values().size() == 1){
			Integer calendarId = enterpriseDraftObject.getCalendar();
			enterpriseCalendarWidget = new Label(enterpriseDraftObject.getEnterpriseCalendars().get(calendarId));
			enterpriseCalendarWidget.setStyleName("aon-inputText");
			enterpriseCalendarWidget.addStyleName(style.maxWidthTextBox());
			enterprise.enterpriseCalendarPanel.add(enterpriseCalendarWidget);
			enterpriseDraftObject.setCalendarId(calendarId); //AutoSeleccion
		}else{
			enterpriseCalendarWidget = new ListBox();
			for(String calendar : enterpriseDraftObject.getEnterpriseCalendars().values()){
				if(null != calendar)
					((ListBox) enterpriseCalendarWidget).addItem(calendar);
			}
			enterpriseCalendarWidget.setStyleName("aon-selectOneMenu");
			enterpriseCalendarWidget.addStyleName(style.maxWidth());
			((ListBox) enterpriseCalendarWidget).addChangeHandler(new ChangeHandler() {
				
				@Override
				public void onChange(ChangeEvent event) {
					enterpriseDraftObject.setCalendar(((ListBox) enterpriseCalendarWidget).getSelectedItemText());
					saving();
				}
			});
			enterprise.enterpriseCalendarPanel.add(enterpriseCalendarWidget);
			if(((ListBox) enterpriseCalendarWidget).getItemCount() != 0){
				((ListBox) enterpriseCalendarWidget).setSelectedIndex(enterpriseDraftObject.getCalendarIndex());
			}
		}	
	}
	
	private void fillEnterpriseData() {
		enterprise.enterpriseName.setValue(enterpriseDraftObject.getName());
		enterprise.enterpriseAlias.setValue(enterpriseDraftObject.getAlias());
		enterprise.document.setValue(enterpriseDraftObject.getDocument());
		checkStylesDocument();
//		onDocumentChange();
		enterprise.nationality.setValue(enterpriseDraftObject.getDocumentCountry());
		enterprise.streetType.setSelectedIndex(enterpriseDraftObject.getAddressStreetTypeIndex());
		enterprise.address.setValue(enterpriseDraftObject.getAddress());
		enterprise.addressNum.setValue(enterpriseDraftObject.getAddressNum());
		enterprise.addressZip.setValue(enterpriseDraftObject.getAddressZip());
		enterprise.addressCity.setValue(enterpriseDraftObject.getAddressCity());
		enterprise.addressProvince.setSelectedIndex(enterpriseDraftObject.getAddressProvinceIndex());
		enterprise.mobile.setValue(enterpriseDraftObject.getMobile());
		enterprise.phone.setValue(enterpriseDraftObject.getPhone());
		enterprise.email.setValue(enterpriseDraftObject.getEmail());
		enterprise.enterpriseWeb.setValue(enterpriseDraftObject.getWeb());
		
		//Crear Imagen
//		FileUpload fileUpload = new FileUpload(); 
//		enterprise.enterpriseLogoPanel.add(fileUpload);
//
//		Image img = new Image(enterpriseDraftObject.getSignature());
//		img.setPixelSize(100, 100);
//		enterprise.enterpriseSignPanel.add(img);
		
		enterprise.enterprisePaysheetModel.setSelectedIndex(enterpriseDraftObject.getPaySheetModelIndex());
		enterprise.enterpriseCostModel.setSelectedIndex(enterpriseDraftObject.getCostsModelIndex());
		enterprise.enterprisePaysheetSendType.setSelectedIndex(enterpriseDraftObject.getPaysheetSendIndex());
		checkPaysheetSendType();
		if(enterprise.enterpriseAgreement.getItemCount() != 0) {
			enterprise.enterpriseAgreement.setSelectedIndex(enterpriseDraftObject.getAgreementIndex() + 1);
		}
	}


	private void checkStylesDocument() {
		String document = enterpriseDraftObject.getDocument();
		String document_type = checkDocumentType(document);
		
		enterprise.documentType.setText(document_type);
		if(enterpriseDraftObject.checkDocumentValidation(document_type, document)) {
			enterprise.documentStatus.removeStyleName("aon-finding-toolbar-item aon-icon-exception aon-finding-toolbar-item-no-border");
			enterprise.documentStatus.setStyleName("aon-finding-toolbar-item aon-icon-predetermine aon-finding-toolbar-item-no-border");
		}else {
			enterprise.documentStatus.removeStyleName("aon-finding-toolbar-item aon-icon-predetermine aon-finding-toolbar-item-no-border");
			enterprise.documentStatus.setStyleName("aon-finding-toolbar-item aon-icon-exception aon-finding-toolbar-item-no-border");
			
		}

		showNationality(document_type);
	}

	private void checkPaysheetSendType() {
		if(enterpriseDraftObject.getPaysheetSendIndex() == 0) {
			enterprise.enterprisePaysheetSendPanel.removeStyleName(style.hide());
			enterprise.enterprisePaysheetSendPanel.setStyleName(style.maxWidth());
			enterprise.enterprisePaysheetSendEmail.setValue(enterpriseDraftObject.getPaysheetSendEmail());
		}else {
			enterprise.enterprisePaysheetSendPanel.setStyleName(style.hide());
		}
		
	}

	private void onDocumentChange() {
		String document = enterpriseDraftObject.getDocument();
		String document_type = checkDocumentType(document);
		
		enterprise.documentType.setText(document_type);
		if(enterpriseDraftObject.checkDocumentValidation(document_type, document)) {
			enterpriseDraftObject.setDocument(document);
			enterpriseDraftObject.setDocumentType(document_type);
			enterprise.documentStatus.removeStyleName("aon-finding-toolbar-item aon-icon-exception aon-finding-toolbar-item-no-border");
			enterprise.documentStatus.setStyleName("aon-finding-toolbar-item aon-icon-predetermine aon-finding-toolbar-item-no-border");
		}else {
			enterprise.documentStatus.removeStyleName("aon-finding-toolbar-item aon-icon-predetermine aon-finding-toolbar-item-no-border");
			enterprise.documentStatus.setStyleName("aon-finding-toolbar-item aon-icon-exception aon-finding-toolbar-item-no-border");
			
		}

		showNationality(document_type);
	}
	
	public String checkDocumentType(String document) {

		RegExp dniPattern = RegExp.compile("\\d{8}\\-?[A-HJ-NP-TV-Z]");
		RegExp niePattern = RegExp.compile("[A-Z]{1}\\d{7}[A-Z]{1}");
		RegExp cifPattern = RegExp.compile("[A-Z]{1}\\d{8}");

		if (dniPattern.test(document.toUpperCase()))
			return "DNI";
		else if (niePattern.test(document.toUpperCase()))
			return "NIE";
		else if (cifPattern.test(document.toUpperCase()))
			return "CIF";
		else
			return "Pasaporte";
	}
	
	private void showNationality(String document_type_str) {
		if (document_type_str == "CIF" || document_type_str == "Pasaporte" || document_type_str == "NIE") {
			enterprise.nationalityLabelCell.getStyle().clearDisplay();
			enterprise.nationalityCell.getStyle().clearDisplay();
		} else {
			enterprise.nationalityLabelCell.getStyle().setDisplay(Display.NONE);
			enterprise.nationalityCell.getStyle().setDisplay(Display.NONE);
			enterprise.nationality.setValue("ESPA\u00D1A");
		}
	}

	@Override
	public void onContextMenu(ContextMenuEvent event) {
		// TODO Auto-generated method stub
	}
	
	private void saving() {
		saveStatus.setText("Guardando...");
		saveTimer.schedule(2500);
	}
	
	private void saved() {
		saveStatus.setText("Todos los cambios guardados");	
		onSaved.accept(enterpriseDraftObject.getEnterpriseInfo());
	}
	
	protected void onSavedNoop(EnterpriseInfo enterpriseInfo) {
		
	}


}
