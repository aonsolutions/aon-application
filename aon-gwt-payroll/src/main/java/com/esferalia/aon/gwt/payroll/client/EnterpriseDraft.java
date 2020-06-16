package com.esferalia.aon.gwt.payroll.client;

import java.util.List;
import java.util.Map.Entry;
import java.util.function.Consumer;

import com.esferalia.aon.gwt.common.shared.StringUtils;
import com.esferalia.aon.gwt.payroll.shared.Agreement;
import com.esferalia.aon.gwt.payroll.shared.EnterpriseInfo;
import com.esferalia.aon.gwt.payroll.shared.ProvinceContract;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;

public class EnterpriseDraft extends Composite {
	
	private class EnterpriseImplementation extends Enterprise{

		@Override
		public void onEnterpriseNameChange() {
			String value = enterprise.enterpriseName.getValue();
			
			if(StringUtils.isBlank(value)) {
				WarningDialog warningDialog = new WarningDialog("Aviso", "Hay que rellenar los campos azules obligatoriamente.");
				warningDialog.center();
				warningDialog.show();
				
				//Set last good value
				enterprise.enterpriseName.setValue(enterpriseDraftObject.getName());
			}
		}

		@Override
		public void onEnterpriseAliasChange() {}

		@Override
		public void onEnterpriseDocumentChange() {
			checkStylesDocument();
		}

		@Override
		public void onEnterpriseNationalityChange() {
			enterpriseDraftObject.setNationality(this.nationality.getValue());
			saving();
		}

		@Override
		public void onEnterpriseStreetTypeChange() {
			String streetType = String.valueOf(this.streetType.getSelectedValue());
			enterpriseDraftObject.setAddressStreetType(streetType);
			saving();
		}

		@Override
		public void onEnterpriseAddressChange() {}

		@Override
		public void onEnterpriseAddressNumChange() {}

		@Override
		public void onEnterpriseAddressZipChange() {
			if(addressZip.getValue().length() >= 2) {
				String zip = addressZip.getValue().substring(0, 2);
				addressProvince.setSelectedIndex(ProvinceContract.getProvinceIndex(ProvinceContract.getName(zip)));
				DomEvent.fireNativeEvent(Document.get().createChangeEvent(), this.addressProvince);
			}
		}

		@Override
		public void onEnterpriseAddressCityChange() {}

		@Override
		public void onEnterpriseAddressProvinceChange() {
			String geozoneProvinceCode = String.valueOf(this.addressProvince.getSelectedValue());
			enterpriseDraftObject.setAddressProvince(geozoneProvinceCode);
			saving();
		}

		@Override
		public void onEnterpriseMobileChange() {}

		@Override
		public void onEnterprisePhoneChange() {}

		@Override
		public void onEnterpriseEmailChange() {}

		@Override
		public void onEnterpriseWebChange() {}

		@Override
		public void onEnterprisePaysheetModelChange() {
			byte paysheetModel = Byte.valueOf(this.enterprisePaysheetModel.getSelectedValue()).byteValue();
			enterpriseDraftObject.setPaySheetModel(paysheetModel);
			saving();
		}

		@Override
		public void onEnterpriseCostModelChange() {
			byte costModel = Byte.valueOf(this.enterpriseCostModel.getSelectedValue()).byteValue();
			enterpriseDraftObject.setCostModel(costModel);
			saving();
		}

		@Override
		public void onEnterprisePaysheetSendTypeChange() {
			byte paysheetSendType = Byte.valueOf(this.enterprisePaysheetSendType.getSelectedValue()).byteValue();
			enterpriseDraftObject.setPaySheetSendType(paysheetSendType);
			checkPaysheetSendType();
			saving();
		}

		@Override
		public void onEnterprisePaysheetSendEmailChange() {}
		
		@Override
		public void onEnterpriseAgreementChange() {
			if (this.enterpriseAgreement.getSelectedIndex() == 0 ) {
				enterpriseDraftObject.setAgreement(null);
				saving();
				return;
			}
			
			Integer agreementId = Integer.valueOf(this.enterpriseAgreement.getSelectedValue()); 
			enterpriseDraftObject.setAgreement(agreementId);
			saving();
		}
		
	}
	
	// -------------------------------------------------- UiBinder --------------------------------------------------

	private static EnterpriseDraftUiBinder uiBinder = GWT.create(EnterpriseDraftUiBinder.class);

	interface EnterpriseDraftUiBinder extends UiBinder<Widget, EnterpriseDraft> {}

	// -------------------------------------------------- UiFields --------------------------------------------------
	
	@UiField
	Button newWorkplaceButton;
	
	@UiField
	Button newActivityButton;
	
	@UiField
	Label saveStatus;
	
	@UiField
	Button redoButton;

	@UiField
	Button undoButton;

	@UiField
	Button undoAllButton;
	
	@UiField (provided = true)
	Enterprise enterprise;

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
					initializeUndoRedo();
					initializeScheduler();
					initilizeView();
				},
				f -> {}
		);
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
		initialiceHandlers();
		fillEnterpriseData();
		
		enterprise.enterpriseOthersTable.getRows().getItem(1).getStyle().setDisplay(Display.NONE);
	}
	
	private void initializeListBox() {
		//SCOPE
		initializeScopeCell();
		
		// CONVENIO
		initializeAgreementCell();
		
		//CALENDARIO
		initializeCalendarCell();		
	}

	private void initializeScopeCell() {
		Widget enterpriseScopeWidget;
		
		if(enterpriseDraftObject.getEnterprisecopes().values().size() == 0)
			enterpriseScopeWidget = createEmptyListLabel();
		else{
			ListBox scopeListBox = new ListBox();
			for(Entry<Integer, String> entry : enterpriseDraftObject.getEnterprisecopes().entrySet())
				scopeListBox.addItem(entry.getValue(), entry.getKey().toString());
			
			scopeListBox.setStyleName("aon-selectOneMenu");
			scopeListBox.getElement().getStyle().setWidth(100.00, Unit.PCT);
			
			scopeListBox.addChangeHandler(new ChangeHandler() {
				
				@Override
				public void onChange(ChangeEvent event) {
					Integer scopeId = Integer.valueOf(scopeListBox.getSelectedValue());
					enterpriseDraftObject.setScope(scopeId);
					saving();
				}
			});
			
			// If only one calendar, selected it and fire event
			// rtrepiana. Yes but saveTimer has not been initialize yet
//			if(scopeListBox.getItemCount() != 0 && scopeListBox.getItemCount() == 1){
//				scopeListBox.setSelectedIndex(0);
//				DomEvent.fireNativeEvent(Document.get().createChangeEvent(), scopeListBox);
//			}
			
			enterpriseScopeWidget = scopeListBox;
		}
		
		enterprise.enterpriseScopePanel.add(enterpriseScopeWidget);
	}
	
	private void initializeAgreementCell() {
		this.enterprise.enterpriseAgreement.addItem("-", "-1");
		List<Agreement> agreements = enterpriseDraftObject.getEnterpriseAgreements();
		for (Agreement agreement : agreements)
			this.enterprise.enterpriseAgreement.addItem(agreement.getDescription(), String.valueOf(agreement.getId()));
	}

	private void initializeCalendarCell() {
		Widget enterpriseCalendarWidget;
		
		if(enterpriseDraftObject.getEnterpriseCalendars().values().size() == 0)
			enterpriseCalendarWidget = createEmptyListLabel();
		else{
			ListBox calendarListBox = new ListBox();
			calendarListBox.addItem("-", "-1");
			for(Entry<Integer, String> entry : enterpriseDraftObject.getEnterpriseCalendars().entrySet())
				calendarListBox.addItem(entry.getValue(), entry.getKey().toString());
			
			calendarListBox.setStyleName("aon-selectOneMenu");
			calendarListBox.getElement().getStyle().setWidth(100.00, Unit.PCT);
			
			calendarListBox.addChangeHandler(new ChangeHandler() {
				
				@Override
				public void onChange(ChangeEvent event) {
					Integer calendarId = Integer.valueOf(calendarListBox.getSelectedValue());
					enterpriseDraftObject.setCalendar(calendarId);
					saving();
				}
			});
			
			// If only one calendar, selected it and fire event
//			if(calendarListBox.getItemCount() != 0 && calendarListBox.getItemCount() == 2){
//				calendarListBox.setSelectedIndex(1);
//				DomEvent.fireNativeEvent(Document.get().createChangeEvent(), calendarListBox);
//			}
			
			enterpriseCalendarWidget = calendarListBox;
		}
		
		enterprise.enterpriseCalendarPanel.add(enterpriseCalendarWidget);
	}

	private void initialiceHandlers() {
		enterprise.enterpriseName.addKeyUpHandler(e-> {
			
			String value = enterprise.enterpriseName.getValue();
			String saved = enterpriseDraftObject.getEnterpriseInfo().getName();
			if (AonStringUtils.equals(value, saved) || StringUtils.isBlank(value))
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
	
	private void fillEnterpriseData() {
		enterprise.enterpriseName.setValue(enterpriseDraftObject.getName());
		enterprise.enterpriseAlias.setValue(enterpriseDraftObject.getAlias());
		enterprise.document.setValue(enterpriseDraftObject.getDocument());
		checkStylesDocument();
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
		
		if(!enterpriseDraftObject.getEnterprisecopes().isEmpty()) {
			ListBox scopeListBox = (ListBox) enterprise.enterpriseScopePanel.getWidget(0);
			scopeListBox.setSelectedIndex(enterpriseDraftObject.getScopeIndex());
		}
		
		enterprise.enterprisePaysheetModel.setSelectedIndex(enterpriseDraftObject.getPaySheetModelIndex());
		enterprise.enterpriseCostModel.setSelectedIndex(enterpriseDraftObject.getCostsModelIndex());
		enterprise.enterprisePaysheetSendType.setSelectedIndex(enterpriseDraftObject.getPaysheetSendIndex());
		checkPaysheetSendType();
		
		if(!enterpriseDraftObject.getEnterpriseAgreements().isEmpty()) {
			enterprise.enterpriseAgreement.setSelectedIndex(enterpriseDraftObject.getEnterpriseAgreementIndex());
		}

		if(!enterpriseDraftObject.getEnterpriseCalendars().isEmpty()) {
			ListBox calendarListBox = (ListBox) enterprise.enterpriseCalendarPanel.getWidget(0);
			calendarListBox.setSelectedIndex(enterpriseDraftObject.getCalendarIndex());
		}
	}
	
	// ------------------------------------------------- AUX METHODS --------------------------------------------------

	private void checkPaysheetSendType() {
		if(enterpriseDraftObject.getPaysheetSendIndex() == 0) {
			enterprise.enterprisePaysheetSendPanel.removeStyleName(enterprise.style.hide());
			enterprise.enterprisePaysheetSendPanel.getElement().getStyle().setWidth(100.00, Unit.PCT);
			enterprise.enterprisePaysheetSendEmail.setValue(enterpriseDraftObject.getPaysheetSendEmail());
		}else {
			enterprise.enterprisePaysheetSendPanel.setStyleName(enterprise.style.hide());
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
	
	public Label createEmptyListLabel() {
		Label label = new Label();
		
		label.setText("No hay entradas disponibles");
		label.setStyleName("aon-inputText");
		label.addStyleName(enterprise.style.warningColor());
		label.getElement().getStyle().setWidth(99.7, Unit.PCT);	
		label.getElement().getStyle().setBorderStyle(BorderStyle.NONE);
		
		return label;
	}
	
	public Label createEmptyListLabel(String labelMessage) {
		Label label = new Label();
		
		label.setText(labelMessage);
		label.setStyleName("aon-inputText");
		label.addStyleName(enterprise.style.warningColor());
		label.getElement().getStyle().setWidth(99.7, Unit.PCT);	
		label.getElement().getStyle().setBorderStyle(BorderStyle.NONE);
		
		return label;
	}
	
	// ----------------------------------------------- CALLBACK TO SAVE ------------------------------------------------
	
	private void initializeScheduler() {
		saveStatus.setText("");
		saveTimer = new Timer() {
			@Override
			public void run() {
				save();
			}
		};
	}
	
	public EnterpriseDraft setOnSaved(Consumer<EnterpriseInfo> onSaved) {
		this.onSaved = onSaved;
		return this;
	}
	
	private void saving() {
		saveStatus.setText("Guardando...");
		saveTimer.schedule(2500);
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
	
	private void saved() {
		saveStatus.setText("Todos los cambios guardados");	
		onSaved.accept(enterpriseDraftObject.getEnterpriseInfo());
	}
	
	protected void onSavedNoop(EnterpriseInfo enterpriseInfo) {}

}
