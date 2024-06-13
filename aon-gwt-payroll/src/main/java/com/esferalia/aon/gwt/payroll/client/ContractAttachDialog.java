package com.esferalia.aon.gwt.payroll.client;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDateBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessagePanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTableButton;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public abstract class ContractAttachDialog extends AonCustomDialog {
	
	// ------------------------------------------------- UIBinder
	
	interface ContractAttachDialogUIBinder extends UiBinder<Widget, ContractAttachDialog> {}

	private static final ContractAttachDialogUIBinder binder = GWT.create(ContractAttachDialogUIBinder.class);
	
	// ------------------------------------------------- UIFileds
	
	@UiField
	MyStyle style;

	interface MyStyle extends CssResource {
		String cursorDefualt();
		String footerButton();
	}
	
	@UiField
	HTMLPanel messagePanel;
	
	@UiField
	HTMLPanel formPanel;
	
	@UiField
	HTMLPanel filePanel;
	
	@UiField
	TextBox descriptionTB;
	
	@UiField
	ListBox typeLB;
	
	@UiField
	HTMLPanel visibilityPanel;
	
	@UiField
	AonDateBox dateBox;
	
	@UiField
	ListBox scopeLB;
	
	@UiField
	HTMLPanel buttonsPanel;
	
	// ------------------------------------------------- Variables
	
	final DomainEnterprisesServiceAsync enterprisesService = DomainEnterprisesServiceAsync.newInstance();
	private DateTimeFormat formatDate = DateTimeFormat.getFormat("dd/MM/yyyy");
	
	private static final String CREATEURL = GWT.getModuleBaseURL() + "attach/create/";
	private static final String UPDATEURL = GWT.getModuleBaseURL() + "attach/update/";
	
	private Attach attach;
	private Map<String, String> scopes;
	
	// Form
	FormPanel form;
	FileUpload fileUpload;
	Hidden userLoginHidden = new Hidden("login", Wnd.getCurrentUser());
	Hidden currentDomainHidden = new Hidden("domain", Wnd.getCurrentDomainNameURL());
	Hidden attachIdHidden = new Hidden("attachId", "");
	Hidden contractIdHidden = new Hidden("attachModule", "");
	Hidden descriptionHidden = new Hidden("description", "");
	Hidden typeHidden = new Hidden("type", "");
	Hidden securityHidden = new Hidden("security", "");
	Hidden dateHidden = new Hidden("date", "");
	Hidden scopeHidden = new Hidden("scope", "");
	Hidden mimeTypeHidden = new Hidden("mimeType", "");
	
	// Button
	Button acceptBtnDialog;
	
	// ------------------------------------------------- Constructor
	
	protected ContractAttachDialog(Attach attach, Map<String, String> scopes) {
		
		setCaption(attach.getId() == null ? "Nuevo documento" : "Edici\u00f3n documento");
		setWidget(binder.createAndBindUi(this));
		
		showCloseButton(true);
		
		this.attach = attach;
		this.scopes = scopes;
		
		initializeFormData();
		initializeForm();
		
		getButtonsPanel();
		showDialog();
	}
	
	private void initializeFormData() {
		dateHidden.setValue(null == this.attach.getDate() ? "" : formatDate.format(this.attach.getDate()));
		contractIdHidden.setValue(this.attach.getAttachModule() + "");
		
		if(null == this.attach || null == this.attach.getId()) return;
		
		attachIdHidden.setValue(this.attach.getId() + "");
		descriptionHidden.setValue(this.attach.getDescription());
		typeHidden.setValue(this.attach.getType() + "");
		securityHidden.setValue(this.attach.getConfidential() + "");
		scopeHidden.setValue(this.attach.getScope() + "");
		mimeTypeHidden.setValue(null == this.attach.getMimeType() ? "" : this.attach.getMimeType().getExtension());
	}
	
	private void initializeForm() {
		// Create Form Panel
		form = new FormPanel();
		form.setAction(null == this.attach.getId() ? CREATEURL : UPDATEURL);
		form.setEncoding(FormPanel.ENCODING_MULTIPART);
		form.setMethod(FormPanel.METHOD_POST);
		form.addSubmitCompleteHandler(e -> {
			try {
				String jsonStr = e.getResults().split(">")[1].split("<")[0];
				JSONValue json = JSONParser.parseStrict(jsonStr);
				parseJSON(json.isObject());
			} catch (NullPointerException | IllegalArgumentException err){
				showError("Formato", "Error formateando la informaci\u00f3n");
			}
		});

		// Description TextBox
		descriptionTB.setValue(this.attach.getDescription());
		descriptionTB.addValueChangeHandler(e -> descriptionHidden.setValue(e.getValue()));

		// Type ListBox
		initTypeListBox();
		setSelectedValueLB(typeLB, this.attach.getType() + "");
		typeLB.addChangeHandler(e -> typeHidden.setValue(typeLB.getSelectedValue()));
		typeLB.setEnabled(null == attach.getType() || !isComunicationCreated(attach.getType()));
		
		// Confidential CheckBox
		visibilityPanel.clear();
		String confidentialTitle = attach.isConfidential() ? "Privado: S\u00f3lo visible para usuarios de la empresa" : "P\u00fablico: Visible para todos los usuarios";
		Button toggleConfidential = new Button();
		toggleConfidential.setTitle(confidentialTitle);
		getEnableDisableToggleButton(toggleConfidential, attach.getConfidential());
		toggleConfidential.addClickHandler(e -> {
			Boolean oldValue = isActiveToggleButton(toggleConfidential);
			Boolean value = !oldValue;
			getEnableDisableToggleButton(toggleConfidential, value);
			securityHidden.setValue(Boolean.TRUE.equals(value) ? "true" : "false");
		});
		visibilityPanel.add(toggleConfidential);

		// Attach DateBoxEx
		dateBox.setValue(attach.getDate());
		dateBox.addValueChangeHandler(e -> dateHidden.setValue(null == e.getValue() ? "" : formatDate.format(e.getValue())));
		
		// Scope ListBox
		initScopeListBox();
		scopeLB.addChangeHandler(e -> scopeHidden.setValue(scopeLB.getSelectedValue()));

		// FileUpload
		FileUpload fileUpload = new FileUpload();
		fileUpload.setName("uploader");
		fileUpload.getElement().setPropertyString("multiple", "multiple");
		fileUpload.getElement().getStyle().setDisplay(Display.NONE);

		fileUpload.addChangeHandler(e -> {
			String filename = getFileName(fileUpload.getFilename());
			String fileExt = getFileExtension(fileUpload.getFilename());

			if (filename.length() == 0)
				Window.alert("Cant upload file - Try again");
			else {
				mimeTypeHidden.setValue(fileExt);
				descriptionHidden.setValue(filename);
				descriptionTB.setValue(filename);

				final int size = getFileSize(fileUpload.getElement());
				if (size > 15000000)
					showError("Tama\u00F1o fichero", "El archivo adjunto no puede ser superior a 10 MB");

			}
		});
		
		// Attach File Button
		AonTableButton fileAttach = new AonTableButton("Subir Documento", AON.CSS.aonIconAttach());
		fileAttach.addClickHandler(e -> fileUpload.click());
		fileAttach.setVisible(null == attach.getId());
		
		filePanel.add(fileAttach);

		// Add all to FlowPanel to add to FormPanel
		HTMLPanel flowFormPanel = new HTMLPanel("");
		flowFormPanel.add(userLoginHidden);
		flowFormPanel.add(currentDomainHidden);
		flowFormPanel.add(contractIdHidden);
		flowFormPanel.add(attachIdHidden);
		flowFormPanel.add(descriptionHidden);
		flowFormPanel.add(typeHidden);
		flowFormPanel.add(mimeTypeHidden);
		flowFormPanel.add(securityHidden);
		flowFormPanel.add(dateHidden);
		flowFormPanel.add(scopeHidden);
		flowFormPanel.add(fileUpload);
		form.add(flowFormPanel);
		formPanel.add(form);
	}
	
	// ------------------------------------------------------ FileUpload.Methods

	private native int getFileSize(final Element data) /*-{
		return data.files[0].size;
	}-*/;
	
	// ------------------------------------------------------ ToogleButton.Methods

	private void initScopeListBox() {
		this.scopeLB.clear();
		this.scopeLB.addItem("-", "-1");
		for (Entry<String, String> entry : scopes.entrySet())
			this.scopeLB.addItem(entry.getKey(), entry.getValue());
	}
	
	private boolean isComunicationCreated(byte attachType) {
		return attachType == ((byte)98) || attachType == ((byte)99) || attachType == ((byte)101) || attachType == ((byte)102) || attachType == ((byte)103) || attachType == ((byte)108);
	}
	
	private void initTypeListBox() {
		typeLB.clear();
		typeLB.addItem("Borrador del contrato", "0");
		typeLB.addItem("Copia Contrato laboral", "1");
		typeLB.addItem("Domiciliacion bancaria", "7");
		typeLB.addItem("Anexo I", "8");
		typeLB.addItem("Anexo II", "9");
		typeLB.addItem("Borrador prorroga", "10");
		typeLB.addItem("Prorroga", "11");
		typeLB.addItem("Borrador del certificado de empresa", "22");
		typeLB.addItem("TA (Alta)", "98");
		typeLB.addItem("TA (Baja)", "99");
		typeLB.addItem("Contrato (Comunicaci\u00f3n SEPE)", "101");
		typeLB.addItem("Copia basica (Comunicaci\u00f3n SEPE)", "102");
		typeLB.addItem("Certific\u00402 (Pdf)", "103");
		typeLB.addItem("IDC", "104");
		typeLB.addItem("IDCPlNss", "105");
		typeLB.addItem("Notificaci\u00f3n Laboral", "107");
		typeLB.addItem("Transformaci\u00f3n (Comunicaci\u00f3n SEPE)", "108");
		typeLB.addItem("Borrador transformaci\u00f3n contrato", "109");
		typeLB.addItem("Borrador pr\u00f3rroga contrato", "110");
		typeLB.addItem("Borrador propuesta recolocaci\u00f3n", "111");
		typeLB.addItem("Otros", "106");
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

	// ------------------------------------------------- Parse JSON
	
	private void parseJSON(JSONObject json) {
		JSONValue type = json.get("type");
		if(null != type && AonStringUtils.isNotBlank(type.toString())) {
			if(AonStringUtils.containsIgnoreCase(type.toString(), "create")) {
				onSuccess("El documento se ha generado correctamente");
			} else if(AonStringUtils.containsIgnoreCase(type.toString(), "update")) {
				onSuccess("El documento se ha actualizado correctamente");
			} else {
				JSONValue message = json.get("message");
				onError(message.toString());
			}
		}
		
		hide();
	}
	

	protected abstract void onSuccess(String message);
	protected abstract void onError(String message);

	// ------------------------------------------------- Auxiliar Methods
	
	private String getFileName(String filename) {
		String[] splits = filename.split("\\\\");
		return splits[splits.length-1].contains("\\.") ? splits[splits.length-1].split("\\.")[0] : splits[splits.length-1];
	}
	
	private String getFileExtension(String filename) {
		String[] splits = filename.split("\\.");
		return splits[splits.length-1];
	}
	
	public void showDialog() {
		// Show center
		Scheduler.get().scheduleDeferred(() -> {
			center();
			show();
		});
	}
	
	private void getEnableDisableToggleButton(Button button, boolean disabled) {
		button.removeStyleName(disabled ? AON.AON_ICON_DISABLE : AON.AON_ICON_ENABLE);
		button.removeStyleName(AON.AON_NO_MARGIN);
		button.removeStyleName(AON.AON_EDIT_DATA_TABLE_BUTTON);
		
		button.setStyleName(!disabled ? AON.AON_ICON_DISABLE : AON.AON_ICON_ENABLE );
		button.setStyleName(AON.AON_NO_MARGIN, true);
		button.setStyleName(AON.AON_EDIT_DATA_TABLE_BUTTON, true);
	}
	
	private boolean isActiveToggleButton(Button button) {
		return AonStringUtils.containsIgnoreCase(button.getStyleName(), AON.AON_ICON_ENABLE);
	}

	// ------------------------------------------------- ButtonsPanel
	
	private void getButtonsPanel() {
		acceptBtnDialog = new Button();
		acceptBtnDialog.setStyleName(AON.CSS.aonIconSave());
		acceptBtnDialog.addStyleName(style.footerButton());
		acceptBtnDialog.setText(this.attach.getId() == null ? "Guardar documento" : "Guardar");
		acceptBtnDialog.addClickHandler(e -> form.submit());
		
		buttonsPanel.add(acceptBtnDialog);
	}
	
	// ------------------------------------------------- Aon Messages panel
	
	private void showError(String title, String message) {
		Map<String, String> errorMap = new HashMap<>();
		errorMap.put(title, message);
		AonMessagePanel.showError(messagePanel, errorMap);
	}
	
	private void showSuccess(String title, String message) {
		Map<String, String> successMap = new HashMap<>();
		successMap.put(title, message);
		AonMessagePanel.showSuccess(messagePanel, successMap);
	}
	
	private void showLoading(String message) {
		AonMessagePanel.showLoading(messagePanel, message);
	}
	
}
