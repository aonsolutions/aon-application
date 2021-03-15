package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.function.Consumer;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonConfirmDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbar;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.gwt.payroll.shared.EnterpriseInfo;
import com.esferalia.aon.gwt.payroll.shared.Municipalities;
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

public class EnterpriseDraft extends Composite {
	
	private class EnterpriseImplementation extends Enterprise {

		@Override
		public void onEnterpriseNameChange() {
			String value = this.enterpriseName.getValue();
			
			if(AonStringUtils.isBlank(value)) {
				AonConfirmDialog dialog = new AonConfirmDialog();
				dialog.info("AVISO: Campos obligatorios", "Hay que rellenar los campos azules obligatoriamente.");
			}
			
			enterpriseDraftObject.setName(value);
		}

		@Override
		public void onEnterpriseAliasChange() {
			String value = this.enterpriseAlias.getValue();
			enterpriseDraftObject.setAlias(value);
		}

		@Override
		public void onEnterpriseDocumentChange() {
			String value = this.document.getValue();
			enterprise.checkDocument();
			enterpriseDraftObject.setDocument(value);
		}

		@Override
		public void onEnterpriseNationalityChange() {
			enterpriseDraftObject.setNationality(this.nationality.getValue());
		}

		@Override
		public void onEnterpriseStreetTypeChange() {
			String streetType = String.valueOf(this.streetType.getSelectedValue());
			enterpriseDraftObject.setAddressStreetType(streetType);
		}

		@Override
		public void onEnterpriseAddressChange() {
			String value = this.address.getValue();
			enterpriseDraftObject.setAddress(value);
		}

		@Override
		public void onEnterpriseAddressNumChange() {
			String value = this.addressNum.getValue();
			enterpriseDraftObject.setAddressNum(value);
		}

		@Override
		public void onEnterpriseAddressZipChange() {
			String value = this.addressZip.getValue();
			if(value.length() >= 2) {
				String zipCode = this.addressZip.getValue().substring(0, 2);
				setSelectedValueLB(addressProvince, zipCode); 
				enterpriseDraftObject.setAddressZip(value);
				DomEvent.fireNativeEvent(Document.get().createChangeEvent(), addressProvince);
			}
		}

		@Override
		public void onEnterpriseAddressCityChange() {
			enterpriseDraftObject.setAddressCity(municipalities.getZipByMunicipalityName(this.addressCity.getSelectedItemText()).toString());
		}

		@Override
		public void onEnterpriseAddressProvinceChange() {
			String addressProvinceCode = String.valueOf(this.addressProvince.getSelectedValue());
			enterpriseDraftObject.setAddressProvince(addressProvinceCode);
			updateMunicipalities();
		}

		@Override
		public void onEnterpriseMobileChange() {
			String value = this.mobile.getValue();
			enterpriseDraftObject.setMobile(value);
		}

		@Override
		public void onEnterprisePhoneChange() {
			String value = this.phone.getValue();
			enterpriseDraftObject.setPhone(value);
		}

		@Override
		public void onEnterpriseEmailChange() {
			String value = this.email.getValue();
			enterpriseDraftObject.setEmail(value);
		}

		@Override
		public void onEnterpriseWebChange() {
			String value = this.enterpriseWeb.getValue();
			enterpriseDraftObject.setWeb(value);
		}

		@Override
		public void onEnterprisePaysheetModelChange() {
			String paysheetModel = String.valueOf(this.enterprisePaysheetModel.getSelectedValue());
			enterpriseDraftObject.setPaySheetModel(paysheetModel);
		}

		@Override
		public void onEnterpriseCostModelChange() {
			String costModel = String.valueOf(this.enterpriseCostModel.getSelectedValue());
			enterpriseDraftObject.setCostModel(costModel);
		}

		@Override
		public void onEnterprisePaysheetSendTypeChange() {
			String paysheetSendType = String.valueOf(this.enterprisePaysheetSendType.getSelectedValue());
			enterpriseDraftObject.setPaySheetSendType(paysheetSendType);
			this.checkPaysheetSendType(enterpriseDraftObject.getPaysheetSendEmail());
		}

		@Override
		public void onEnterprisePaysheetSendEmailChange() {
			String value = this.enterprisePaysheetSendEmail.getValue();
			enterpriseDraftObject.setPaySheetSendEmail(value);
		}
		
		@Override
		public void onEnterpriseAgreementChange() {
			if (this.enterpriseAgreement.getSelectedIndex() == 0 ) {
				enterpriseDraftObject.setAgreement(null);
			} else {
				String agreementId = String.valueOf(this.enterpriseAgreement.getSelectedValue()); 
				enterpriseDraftObject.setAgreement(agreementId);
			}
		}

		@Override
		public void onEnterpriseScopeChange(Integer scopeId) {
			enterpriseDraftObject.setScope(scopeId);
		}
		
	}
	
	// -------------------------------------------------- UiBinder --------------------------------------------------

	private static EnterpriseDraftUiBinder uiBinder = GWT.create(EnterpriseDraftUiBinder.class);

	interface EnterpriseDraftUiBinder extends UiBinder<Widget, EnterpriseDraft> {}

	// ----------------------------------------------- ScheduledCommand ---------------------------------------------
	
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
					AON.CSS.aonIconHome(), AON.AON_ICON_CMD_BUTTON, style.cmd_btn());
			newWorkplace.ensureDebugId("newWorkplace");
			
			newActivity = addItem("Actividad", new NewActivityCommand(), 
					AON.CSS.aonIconCopy(), AON.AON_ICON_CMD_BUTTON, style.cmd_btn());
			newActivity.ensureDebugId("newActivity");
		}
	}
	
	// -------------------------------------------------- UiFields --------------------------------------------------
	
	@UiField
	MyStyle style;

	interface MyStyle extends CssResource {
		String container();
		String cmd_btn();
	}
	
	@UiField
	DockLayoutPanel dockLayoutPanel;
	
	@UiField
	HTMLPanel centerContainer;

	// ------------------------------------------------------ VARIABLES DE LA CLASE --------------------------------------------------

	private EnterpriseDraftObject enterpriseDraftObject;
	
	private Municipalities municipalities = new Municipalities();
	
	private Consumer<EnterpriseInfo> onSaved ;
	
	private NewContextMenu contextMenu;
	
	private Enterprise enterprise;
	
	private AonToolbar toolbar;
	private AonToolbarButton acceptButton;
	private AonToolbarButton newButton;
	private AonToolbarButton undoAllButton;
	private AonToolbarButton undoButton;
	private AonToolbarButton redoButton;

	// ------------------------------------------------ CONSTRUCTOR ------------------------------------------------------

	public EnterpriseDraft() {
		enterprise = new EnterpriseImplementation();
		toolbar = getToolbarPanel();
		
		// Inicializamos la vista del empleado
		initWidget(uiBinder.createAndBindUi(this));
		
		onSaved = this::onSavedNoop;
		contextMenu = new NewContextMenu();
		
		dockLayoutPanel.addNorth( toolbar , AonToolbar.HEIGTH );
		dockLayoutPanel.addStyleName(style.container());
		
		centerContainer.add(enterprise);
	}

	// ------------------------------------------------------ METODOS DE LA CLASE --------------------------------------------------

	public void setEnterpriseDraftObject(EnterpriseDraftObject enterpriseDraftObject) {
		this.enterpriseDraftObject = enterpriseDraftObject;
		this.enterpriseDraftObject.initializeEnterprise(
				s -> {
					initializeUndoRedo();
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
		enterprise.initializeScopeCell(enterpriseDraftObject.getEnterprisecopes());
		enterprise.initializeAgreementCell(enterpriseDraftObject.getEnterpriseAgreements());
		
		fillEnterpriseData();
	}
	
	private void fillEnterpriseData() {
		enterprise.enterpriseName.setValue(enterpriseDraftObject.getName());
		enterprise.enterpriseAlias.setValue(enterpriseDraftObject.getAlias());
		enterprise.document.setValue(enterpriseDraftObject.getDocument());
		enterprise.checkDocument();
		enterprise.nationality.setValue(enterpriseDraftObject.getDocumentCountry());
		enterprise.streetType.setSelectedIndex(enterpriseDraftObject.getAddressStreetTypeIndex());
		enterprise.address.setValue(enterpriseDraftObject.getAddress());
		enterprise.addressNum.setValue(enterpriseDraftObject.getAddressNum());
		enterprise.addressZip.setValue(enterpriseDraftObject.getAddressZip());
		enterprise.addressProvince.setSelectedIndex(enterpriseDraftObject.getAddressProvinceIndex());
		if(null != enterpriseDraftObject.getAddressProvinceIndex()) {
			updateMunicipalities();
			enterprise.addressCity.setSelectedIndex(getMunicipalityIndex(enterpriseDraftObject.getAddressProvince(), enterpriseDraftObject.getAddressCity()));
		}
		enterprise.mobile.setValue(enterpriseDraftObject.getMobile());
		enterprise.phone.setValue(enterpriseDraftObject.getPhone());
		enterprise.email.setValue(enterpriseDraftObject.getEmail());
		enterprise.enterpriseWeb.setValue(enterpriseDraftObject.getWeb());
		
		if(!enterpriseDraftObject.getEnterprisecopes().isEmpty()) {
			ListBox scopeListBox = (ListBox) enterprise.enterpriseScopePanel.getWidget(0);
			scopeListBox.setSelectedIndex(enterpriseDraftObject.getScopeIndex());
		}
		
		setSelectedValueLB(enterprise.enterprisePaysheetModel, enterpriseDraftObject.getPaySheetModel());
		setSelectedValueLB(enterprise.enterpriseCostModel, enterpriseDraftObject.getCostsModel());
		setSelectedValueLB(enterprise.enterprisePaysheetSendType, enterpriseDraftObject.getPaysheetSend());
		enterprise.checkPaysheetSendType(enterpriseDraftObject.getPaysheetSendEmail());
		
		setSelectedValueLB(enterprise.enterpriseAgreement, enterpriseDraftObject.getAgreement());

	}
	
	// ------------------------------------------------- AUX METHODS --------------------------------------------------

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
	
	public void updateMunicipalities() {
		String provinceCode = enterprise.addressProvince.getSelectedValue();
		enterprise.addressCity.clear();
		enterprise.addressCity.addItem("-");;
		ArrayList<String> municipalitiesOfProvince = municipalities.getMunicipalitiesByProvinceCodeArr(provinceCode);
		municipalitiesOfProvince.forEach(m -> {enterprise.addressCity.addItem(m);});
	}
	
	private int getMunicipalityIndex(String province, String city) {
		return (AonStringUtils.isBlank(province) || AonStringUtils.isBlank(city)) ? -1 : municipalities.getMunicipalityIndex(province, city) + 1;
	}
	
	// ----------------------------------------------- TOOLBAR ------------------------------------------------
	
	private AonToolbar getToolbarPanel() {
		
		AonToolbar toolbar = new AonToolbar("Empresa");
		
		acceptButton = new AonToolbarButton( AON.MSG.saveAction(), AON.CSS.aonIconSave() );
		acceptButton.addClickHandler(e -> {
			onAccept(e);
		});
		toolbar.add(acceptButton);
		
		newButton = new AonToolbarButton( AON.MSG.newAction(), AON.CSS.aonIconAdd() );
		newButton.addClickHandler(e -> {
			onNew(e);
		});
		toolbar.add(newButton);
		
		undoAllButton = new AonToolbarButton( "Deshacer todo", AON.CSS.aonIconUndoAll() );
		undoAllButton.addClickHandler(e -> {
			onUndoAll(e);
		});
		toolbar.add(undoAllButton);
		
		undoButton = new AonToolbarButton( AON.MSG.undo(), AON.CSS.aonIconUndo() );
		undoButton.addClickHandler(e -> {
			onUndo(e);
		});
		toolbar.add(undoButton);
		
		redoButton = new AonToolbarButton( "Rehacer", AON.CSS.aonIconRedo() );
		redoButton.addClickHandler(e -> {
			onRedo(e);
		});
		toolbar.add(redoButton);
		
		return toolbar;

	}
	
	private void onAccept(ClickEvent event) {
		enterpriseDraftObject.updateEnterprise(
				r -> {
					onSaved.accept(enterpriseDraftObject.getEnterpriseInfo());
				}, 
				t -> {}
		);
	}
	
	private void onUndoAll(ClickEvent e) {
		while ( enterpriseDraftObject.canUndo() )
			enterpriseDraftObject.undo();
		initilizeView();
	}
	
	private void onUndo(ClickEvent e) {
		enterpriseDraftObject.undo();
		initilizeView();
	}

	private void onRedo(ClickEvent e) {
		enterpriseDraftObject.redo();
		initilizeView();
	}
	
	private void onNew(ClickEvent event) {
		NativeEvent nativeEvent = event.getNativeEvent();
		contextMenu.setPopupPosition(nativeEvent.getClientX(), nativeEvent.getClientY());
		contextMenu.show();
	}
	
	public EnterpriseDraft setOnSaved(Consumer<EnterpriseInfo> onSaved) {
		this.onSaved = onSaved;
		return this;
	}
	
	protected void onSavedNoop(EnterpriseInfo enterpriseInfo) {}

}
