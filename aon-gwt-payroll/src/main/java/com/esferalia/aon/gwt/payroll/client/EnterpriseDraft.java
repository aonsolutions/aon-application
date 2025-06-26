package com.esferalia.aon.gwt.payroll.client;

import java.util.function.Consumer;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDockLayout;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog.AonAcceptDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.gwt.payroll.shared.Municipalities;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.StreetType;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.MenuItem;

public abstract class EnterpriseDraft extends AonCustomDockLayout {
	
	private class EnterpriseImplementation extends Enterprise {

		@Override
		public void onEnterpriseNameChange(String name) {
			enterpriseDraftObject.setName(name);
			setToolbarTitle(name);
			setHasChange(true);
		}

		@Override
		public void onEnterpriseAliasChange(String alias) {
			enterpriseDraftObject.setAlias(alias);
			setHasChange(true);
		}

		@Override
		public void onEnterpriseDocumentChange(String document) {
			enterpriseDraftObject.setDocument(document);
			setHasChange(true);
		}

		@Override
		public void onEnterpriseNationalityChange(String nationality) {
			enterpriseDraftObject.setNationality(Country.getCountryByName(nationality));
			setHasChange(true);
		}

		@Override
		public void onEnterpriseStreetTypeChange(String streetType) {
			StreetType streetTypeObj = StreetType.valueOf(streetType);
			enterpriseDraftObject.setAddressStreetType(streetTypeObj);
			setHasChange(true);
		}

		@Override
		public void onEnterpriseAddressChange(String address) {
			enterpriseDraftObject.setAddress(address);
			setHasChange(true);
		}

		@Override
		public void onEnterpriseAddressNumChange(String number) {
			enterpriseDraftObject.setAddressNum(number);
			setHasChange(true);
		}

		@Override
		public void onEnterpriseAddressZipChange(String zip) {
			enterpriseDraftObject.setAddressZip(zip);
			setHasChange(true);
		}

		@Override
		public void onEnterpriseAddressCityChange(String municipalityCode) {
			enterpriseDraftObject.setAddressCity(municipalities.getMunicipalityByZip(municipalityCode));
			enterpriseDraftObject.setAddressMunicipalityCode(municipalityCode);
			setHasChange(true);
		}

		@Override
		public void onEnterpriseAddressProvinceChange(String province) {
			enterpriseDraftObject.setAddressProvince(province);
			setHasChange(true);
		}

		@Override
		public void onEnterpriseMobileChange(String mobile) {
			enterpriseDraftObject.setMobile(mobile);
			setHasChange(true);
		}

		@Override
		public void onEnterprisePhoneChange(String phone) {
			enterpriseDraftObject.setPhone(phone);
			setHasChange(true);
		}

		@Override
		public void onEnterpriseEmailChange(String email) {
			enterpriseDraftObject.setEmail(email);
			setHasChange(true);
		}

		@Override
		public void onEnterpriseWebChange(String web) {
			enterpriseDraftObject.setWeb(web);
			setHasChange(true);
		}

		@Override
		public void onEnterprisePaysheetModelChange(String paysheetModel) {
			enterpriseDraftObject.setPaySheetModel(paysheetModel);
			setHasChange(true);
		}

		@Override
		public void onEnterprisePaysheetSendTypeChange(String sendType) {
			enterpriseDraftObject.setPaySheetSendType(sendType);
			setHasChange(true);
		}

		@Override
		public void onEnterprisePaysheetSendEmailChange(String sendEmail) {
			enterpriseDraftObject.setPaySheetSendEmail(sendEmail);
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
		public void onEnterprisePaySsMutualChange(String paySSMutual) {
			enterpriseDraftObject.setPaySsMutual(paySSMutual);
			setHasChange(true);
		}

		@Override
		public void onnterprisePayAuthorizationKeyChange(String authKey) {
			enterpriseDraftObject.setPayAuthorizationKey(authKey);
			setHasChange(true);
		}
		
	}
	
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
			newWorkplace = addItem("Centro trabajo", new NewWorkplaceCommand(), AON.CSS.aonIconHome(), AON.CSS.aonCmdItem(), AON.AON_ICON_CMD_BUTTON);
			newWorkplace.ensureDebugId("newWorkplace");
			
			newActivity = addItem("Actividad", new NewActivityCommand(), AON.CSS.aonIconCopy(), AON.CSS.aonCmdItem(), AON.AON_ICON_CMD_BUTTON);
			newActivity.ensureDebugId("newActivity");
		}
	}
	
	// -------------------------------------------------- Variables

	private EnterpriseDraftObject enterpriseDraftObject;
	
	private Municipalities municipalities = new Municipalities();
	
	private Consumer<com.esferalia.aon.occam.api.model.payroll.Enterprise> onSaved ;
	
	private NewContextMenu contextMenu;
	
	private Enterprise enterprise;
	
	private AonToolbarButton acceptButton;
	private AonToolbarButton undoAllButton;
	
	private boolean hasChange;

	// -------------------------------------------------- Constructor

	protected EnterpriseDraft() {
		super("Empresa");
		
		enterprise = new EnterpriseImplementation();
		
		getToolbarPanel();
		hideSearchWidget();

		onSaved = this::onSavedNoop;
		contextMenu = new NewContextMenu();
		
		add(enterprise);
	}
	
	@Override
	protected void onClearFilter() {}

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
		setToolbarTitle(enterpriseDraftObject.getName());
		
		enterprise.initializeView();
		enterprise.setScopes(enterpriseDraftObject.getEnterprisecopes());
		enterprise.setAgreements(enterpriseDraftObject.getEnterpriseAgreements());
		
		enterprise.fillEnterprise(enterpriseDraftObject.getEnterpriseInfo());
	}
	
	public EnterpriseDraftObject getEnterpriseDraftObject() {
		return this.enterpriseDraftObject;
	}
	
	// -------------------------------------------------- Toolbar
	
	private void getToolbarPanel() {
		acceptButton = new AonToolbarButton( AON.MSG.saveAction(), AON.CSS.aonIconSave() );
		acceptButton.addClickHandler(e -> onAccept());
		addToolbarButton(acceptButton);
		
		undoAllButton = new AonToolbarButton( AON.MSG.undo() + " todo", AON.CSS.aonIconUndoAll() );
		undoAllButton.ensureDebugId("undoAllButton");
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
		addToolbarButton(undoAllButton);
		
		AonToolbarButton newButton = new AonToolbarButton( AON.MSG.newAction(), AON.CSS.aonIconAdd() );
		newButton.addClickHandler(this::onNew);
		addToolbarButton(newButton);
	}
	
	private void onAccept() {
		enterprise.showLoading("Guardando " + enterpriseDraftObject.getName() + " ...");
		enterpriseDraftObject.saveEnterprise(
				r -> {
					enterprise.showSuccess("La empresa " + enterpriseDraftObject.getEnterpriseInfo().getName() + " ha sido actualizada correctamente");
					onSaved.accept(enterpriseDraftObject.getEnterpriseInfo());
					setHasChange(false);
					
					setEnterpriseDraftObject(enterpriseDraftObject);
				}, 
				t -> enterprise.showError("Error : " + t.getMessage())
		);
	}
	
	private void onNew(ClickEvent event) {
		NativeEvent nativeEvent = event.getNativeEvent();
		contextMenu.setPopupPosition(nativeEvent.getClientX(), nativeEvent.getClientY());
		contextMenu.show();
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
