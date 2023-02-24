package com.esferalia.aon.gwt.payroll.client;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog.AonAcceptDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessagePanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbar;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.gwt.payroll.shared.Municipalities;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.StreetType;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.Widget;

public abstract class EnterpriseDraft extends Composite {
	
	private class EnterpriseImplementation extends Enterprise {

		@Override
		public void onEnterpriseNameChange() {
			String value = this.enterpriseName.getValue();
			enterpriseDraftObject.setName(value);
			setToolbaTitle(value);
			setHasChange(true);
		}

		@Override
		public void onEnterpriseAliasChange() {
			String value = this.enterpriseAlias.getValue();
			enterpriseDraftObject.setAlias(value);
			setHasChange(true);
		}

		@Override
		public void onEnterpriseDocumentChange() {
			String value = this.document.getValue();
			enterprise.checkDocument(true);
			enterpriseDraftObject.setDocument(value);
			setHasChange(true);
		}

		@Override
		public void onEnterpriseNationalityChange() {
			enterpriseDraftObject.setNationality(Country.getCountryByName(this.nationality.getValue()));
			setHasChange(true);
		}

		@Override
		public void onEnterpriseStreetTypeChange() {
			String streetType = String.valueOf(this.streetType.getSelectedValue());
			StreetType streetTypeObj = StreetType.valueOf(streetType);
			enterpriseDraftObject.setAddressStreetType(streetTypeObj);
			setHasChange(true);
		}

		@Override
		public void onEnterpriseAddressChange() {
			String value = this.address.getValue();
			enterpriseDraftObject.setAddress(value);
			setHasChange(true);
		}

		@Override
		public void onEnterpriseAddressNumChange() {
			String value = this.addressNum.getValue();
			enterpriseDraftObject.setAddressNum(value);
			setHasChange(true);
		}

		@Override
		public void onEnterpriseAddressZipChange() {
			String value = this.addressZip.getValue();
			enterpriseDraftObject.setAddressZip(value);
			updateProvince();
			updateMunicipalities();
			setHasChange(true);
		}

		@Override
		public void onEnterpriseAddressCityChange() {
			enterpriseDraftObject.setAddressCity(this.addressCity.getSelectedItemText());
			enterpriseDraftObject.setAddressMunicipalityCode(municipalities.getZipByMunicipalityName(this.addressCity.getSelectedItemText()));
			setHasChange(true);
		}

		@Override
		public void onEnterpriseAddressProvinceChange() {
			String addressProvinceCode = String.valueOf(this.addressProvince.getSelectedValue());
			enterpriseDraftObject.setAddressProvince(addressProvinceCode);
			updateMunicipalities();
			setHasChange(true);
		}

		@Override
		public void onEnterpriseMobileChange() {
			String value = this.mobile.getValue();
			enterpriseDraftObject.setMobile(value);
			setHasChange(true);
		}

		@Override
		public void onEnterprisePhoneChange() {
			String value = this.phone.getValue();
			enterpriseDraftObject.setPhone(value);
			setHasChange(true);
		}

		@Override
		public void onEnterpriseEmailChange() {
			String value = this.email.getValue();
			enterpriseDraftObject.setEmail(value);
			setHasChange(true);
		}

		@Override
		public void onEnterpriseWebChange() {
			String value = this.enterpriseWeb.getValue();
			enterpriseDraftObject.setWeb(value);
			setHasChange(true);
		}

		@Override
		public void onEnterprisePaysheetModelChange() {
			String paysheetModel = String.valueOf(this.enterprisePaysheetModel.getSelectedValue());
			enterpriseDraftObject.setPaySheetModel(paysheetModel);
			setHasChange(true);
		}

		@Override
		public void onEnterprisePaysheetSendTypeChange() {
			String paysheetSendType = String.valueOf(this.enterprisePaysheetSendType.getSelectedValue());
			enterpriseDraftObject.setPaySheetSendType(paysheetSendType);
			this.checkPaysheetSendType(enterpriseDraftObject.getPaysheetSendEmail());
			setHasChange(true);
		}

		@Override
		public void onEnterprisePaysheetSendEmailChange() {
			String value = this.enterprisePaysheetSendEmail.getValue();
			enterpriseDraftObject.setPaySheetSendEmail(value);
			setHasChange(true);
		}
		
		@Override
		public void onEnterpriseAgreementChange(Integer agreementId) {
			enterpriseDraftObject.setAgreement(null == agreementId ? null : agreementId.toString());
			setHasChange(true);
		}

		@Override
		public void onEnterpriseScopeChange(Integer scopeId) {
			enterpriseDraftObject.setScope(scopeId);
			setHasChange(true);
		}

		@Override
		public void fireErrorMessage(Map<String, String> errorMap) {
			AonMessagePanel.showError(messageContainer, errorMap);
		}
		
		@Override
		public void fireInfoMessage(Map<String, String> errorMap) {
			AonMessagePanel.showInfo(messageContainer, errorMap);
		}
		
	}
	
	// -------------------------------------------------- UiBinder

	private static EnterpriseDraftUiBinder uiBinder = GWT.create(EnterpriseDraftUiBinder.class);

	interface EnterpriseDraftUiBinder extends UiBinder<Widget, EnterpriseDraft> {}

	// -------------------------------------------------- NewContextMenu
	
	class NewWorkplaceCommand implements ScheduledCommand {

		@Override
		public void execute() {
			EmployeeTree.showNewWorkplace();
		}
	}
	
	class NewActivityCommand implements ScheduledCommand {

		@Override
		public void execute() {
			EmployeeTree.showNewActivity();
		}
	}
	
	class NewContextMenu extends ContextMenu {
				
		private MenuItem newWorkplace = null;
		private MenuItem newActivity = null;
		
		public NewContextMenu() {
			
			newWorkplace = addItem("Centro trabajo", new NewWorkplaceCommand(), 
					AON.CSS.aonIconHome(), AON.AON_ICON_CMD_BUTTON, style.cmdBtn());
			newWorkplace.ensureDebugId("newWorkplace");
			
			newActivity = addItem("Actividad", new NewActivityCommand(), 
					AON.CSS.aonIconCopy(), AON.AON_ICON_CMD_BUTTON, style.cmdBtn());
			newActivity.ensureDebugId("newActivity");
		}
	}
	
	// -------------------------------------------------- UiFields
	
	@UiField
	MyStyle style;

	interface MyStyle extends CssResource {
		String container();
		String cmdBtn();
	}
	
	@UiField
	DockLayoutPanel dockLayoutPanel;
	
	@UiField
	HTMLPanel messageContainer;
	
	@UiField
	HTMLPanel centerContainer;

	// -------------------------------------------------- Variables

	private EnterpriseDraftObject enterpriseDraftObject;
	
	private Municipalities municipalities = new Municipalities();
	
	private Consumer<com.esferalia.aon.occam.api.model.payroll.Enterprise> onSaved ;
	
	private NewContextMenu contextMenu;
	
	private Enterprise enterprise;
	
	private AonToolbar toolbar;
	private AonToolbarButton acceptButton;
	private AonToolbarButton undoAllButton;
	
	private boolean hasChange;

	// -------------------------------------------------- Constructor

	protected EnterpriseDraft() {
		enterprise = new EnterpriseImplementation();
		getToolbarPanel();

		// Inicializamos la vista del empleado
		initWidget(uiBinder.createAndBindUi(this));
		
		onSaved = this::onSavedNoop;
		contextMenu = new NewContextMenu();
		
		dockLayoutPanel.addNorth( toolbar , AonToolbar.HEIGTH );
		dockLayoutPanel.addStyleName(style.container());
		
		centerContainer.add(enterprise);
	}

	// -------------------------------------------------- setEnterpriseDraftObject

	public void setEnterpriseDraftObject(EnterpriseDraftObject enterpriseDraftObject) {
		this.enterpriseDraftObject = enterpriseDraftObject;
		this.enterpriseDraftObject.initializeEnterprise(
				s -> {
					initilizeView();
					onCheckStatus(getEnterpriseDraftObject());
					setHasChange(false);
				},
				f -> {}
		);
	}
	
	private void initilizeView() {
		setToolbaTitle(enterpriseDraftObject.getName());
		
		enterprise.initializeView();
		enterprise.initializeScopeCell(enterpriseDraftObject.getEnterprisecopes());
		enterprise.initializeAgreementCell(enterpriseDraftObject.getEnterpriseAgreements());
		
		fillEnterpriseData();
	}
	
	private void fillEnterpriseData() {
		enterprise.enterpriseName.setValue(enterpriseDraftObject.getName());
		enterprise.enterpriseAlias.setValue(enterpriseDraftObject.getAlias());
		enterprise.document.setValue(enterpriseDraftObject.getDocument());
		enterprise.checkDocument(false);
		enterprise.nationality.setValue(enterpriseDraftObject.getDocumentCountry());
		setSelectedValueLB(enterprise.streetType, enterpriseDraftObject.getSteetType());
		enterprise.address.setValue(enterpriseDraftObject.getAddress());
		enterprise.addressNum.setValue(enterpriseDraftObject.getAddressNum());
		enterprise.addressZip.setValue(enterpriseDraftObject.getAddressZip());
		
		updateProvince();
		setSelectedValueLB(enterprise.addressProvince, enterpriseDraftObject.getGeozoneCode());
		
		if(null != enterpriseDraftObject.getGeozoneCode()) {
			updateMunicipalities();
			setSelectedValueLB(enterprise.addressCity, enterpriseDraftObject.getMunicipalityCode());
		}
		
		enterprise.mobile.setValue(enterpriseDraftObject.getMobile());
		enterprise.phone.setValue(enterpriseDraftObject.getPhone());
		enterprise.email.setValue(enterpriseDraftObject.getEmail());
		enterprise.enterpriseWeb.setValue(enterpriseDraftObject.getWeb());
		
		if(!enterpriseDraftObject.getEnterprisecopes().isEmpty()) {
			ListBox scopeListBox = (ListBox) enterprise.enterpriseScopePanel.getWidget(0);
			setSelectedValueLB(scopeListBox, enterpriseDraftObject.getScope()+"");
		}
		
		setSelectedValueLB(enterprise.enterprisePaysheetModel, enterpriseDraftObject.getPaySheetModel());
		setSelectedValueLB(enterprise.enterprisePaysheetSendType, enterpriseDraftObject.getPaysheetSend());
		enterprise.checkPaysheetSendType(enterpriseDraftObject.getPaysheetSendEmail());
		
		enterprise.enterpriseAgreement.setValue(enterpriseDraftObject.getAgreementDescription());
	}
	
	public EnterpriseDraftObject getEnterpriseDraftObject() {
		return this.enterpriseDraftObject;
	}
	
	// -------------------------------------------------- Auxiliar Methods

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
	
	public void updateMunicipalities() {
		String provinceCode = enterprise.addressProvince.getSelectedValue();
		enterprise.addressCity.clear();
		enterprise.addressCity.addItem("-", "-1");
		HashMap<String, String> municipalitiesOfProvince = municipalities.getMunicipalitiesByProvinceCode(provinceCode);
		municipalitiesOfProvince.entrySet().forEach(e -> enterprise.addressCity.addItem(e.getValue(), e.getKey()));
	}
	
	public void updateProvince() {
		String zip = enterprise.addressZip.getValue();
		if(AonStringUtils.isNotBlank(zip)) {
			String zipCode = zip.substring(0, 2);
			setSelectedValueLB(enterprise.addressProvince, AonStringUtils.leftPad(zipCode, 2, '0'));
			DomEvent.fireNativeEvent(Document.get().createChangeEvent(), enterprise.addressProvince);
		}
		
	}
	
	// -------------------------------------------------- Toolbar
	
	private void getToolbarPanel() {
		toolbar = new AonToolbar("Empresa");
		
		acceptButton = new AonToolbarButton( AON.MSG.saveAction(), AON.CSS.aonIconSave() );
		acceptButton.addClickHandler(e -> onAccept());
		toolbar.add(acceptButton);
		
		undoAllButton = new AonToolbarButton( AON.MSG.undo() + " todo", AON.CSS.aonIconUndoAll() );
		undoAllButton.addClickHandler(e -> {
			AonDialog confirmDialog =  new AonDialog("Restaurar empresa", new HTMLPanel("\u00bfDesea realmente deshacer los cambios realizados en la empresa <b>" + enterpriseDraftObject.getName() + "</b> \u003f <br>Este proceso es irreversible."));
			confirmDialog.confirm(new AonAcceptDialogCallback() {
				
				@Override
				public void onCancel() {
					// Nothing to do
				}
				
				@Override
				public void onAccept() {
					enterpriseDraftObject.initializeEnterprise(
							s -> {
								initilizeView();
								setHasChange(false);
							},
							f -> {}
					);
				}
			});
		});
		toolbar.add(undoAllButton);
		
		AonToolbarButton newButton = new AonToolbarButton( AON.MSG.newAction(), AON.CSS.aonIconAdd() );
		newButton.addClickHandler(this::onNew);
		toolbar.add(newButton);
	}
	
	private void setToolbaTitle(String title) {
		toolbar.setTitle(title);
	}
	
	private void onAccept() {
		showLoading("Guardando " + enterpriseDraftObject.getName() + " ...");
		enterpriseDraftObject.saveEnterprise(
				r -> {
					showSuccess("Guardado", "La empresa " + enterpriseDraftObject.getEnterpriseInfo().getName() + " ha sido actualizada correctamente");
					onSaved.accept(enterpriseDraftObject.getEnterpriseInfo());
					setHasChange(false);
				}, 
				t -> showError("Guardado", "La empresa " + enterpriseDraftObject.getEnterpriseInfo().getName() + " ha sido actualizada correctamente")
		);
	}
	
	private void onNew(ClickEvent event) {
		NativeEvent nativeEvent = event.getNativeEvent();
		contextMenu.setPopupPosition(nativeEvent.getClientX(), nativeEvent.getClientY());
		contextMenu.show();
	}
	
	private void showError(String title, String message) {
		Map<String, String> messageMap = new HashMap<>();
		messageMap.put(title,message);
		AonMessagePanel.showSuccess(messageContainer, messageMap);
	}
	
	private void showSuccess(String title, String message) {
		Map<String, String> messageMap = new HashMap<>();
		messageMap.put(title,message);
		AonMessagePanel.showSuccess(messageContainer, messageMap);
	}
	
	private void showLoading(String message) {
		AonMessagePanel.showLoading(messageContainer, message);
	}
	
	private void setHasChange(boolean hasChange) {
		this.hasChange = hasChange;
		acceptButton.setEnabled(this.hasChange);
		undoAllButton.setEnabled(this.hasChange);
	}
	
	// -------------------------------------------------- Saved Methods
	
	public EnterpriseDraft setOnSaved(Consumer<com.esferalia.aon.occam.api.model.payroll.Enterprise> onSaved) {
		this.onSaved = onSaved;
		return this;
	}
	
	protected void onSavedNoop(com.esferalia.aon.occam.api.model.payroll.Enterprise enterprise) {}
	
	// -------------------------------------------------- Abstract Methods
	
	protected abstract void onCheckStatus(EnterpriseDraftObject enterpriseDraftObject);

}
