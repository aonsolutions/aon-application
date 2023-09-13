package com.esferalia.aon.gwt.payroll.client;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessagePanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarSmallButton;
import com.esferalia.aon.occam.api.model.Certificate;
import com.esferalia.aon.occam.api.model.CertificateInfo;
import com.esferalia.aon.occam.api.model.aonsolutions.DomainUserRoles;
import com.esferalia.aon.occam.api.model.security.CertificateType;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public abstract class CertificateDialog extends AonCustomDialog {
	
	// ------------------------------------------------- UIBinder
	
	interface CertificateDialogUIBinder extends UiBinder<Widget, CertificateDialog> {}

	private static final CertificateDialogUIBinder binder = GWT.create(CertificateDialogUIBinder.class);
	
	// ------------------------------------------------- UIFileds
	
	@UiField
	MyStyle style;

	interface MyStyle extends CssResource {
		String security();
	}
	
	@UiField
	HTMLPanel mainPanel;
	
	@UiField
	HTMLPanel messagePanel;
	
	@UiField
	HTMLPanel formPanel;
	
	@UiField
	DeckPanel deckPanel;
	
	@UiField
	HTMLPanel filePanel;
	
	@UiField
	TextBox fileNameTB;
	
	@UiField
	PasswordTextBox passwordTB;
	
	@UiField (provided = true)
	AonToolbarSmallButton visibilityBtn;
	
	@UiField
	HTMLPanel certificateInfoPanel;
	
	@UiField
	RadioButton userRB;
	
	@UiField
	RadioButton enterpriseRB;
	
	@UiField
	RadioButton publicRB;
	
	@UiField
	RadioButton privateRB;
	
	@UiField
	CheckBox tgssCB;
	
	@UiField
	CheckBox sepeCB;
	
	@UiField
	CheckBox aeatCB;
	
	@UiField
	HTMLPanel buttonsPanel;
	
	// ------------------------------------------------- Variables
	
	final DomainEnterprisesServiceAsync enterprisesService = DomainEnterprisesServiceAsync.newInstance();
	private DateTimeFormat formatDate = DateTimeFormat.getFormat("dd/MM/yyyy");
	
	private DomainUserRoles userRoles;
	private CertificateInfo certificateInfo;
	private List<Certificate> certificates = Collections.emptyList();
	
	// Forms
	FormPanel form;
	FileUpload fileUpload;
	Hidden userLoginHidden = new Hidden("currentUser", Wnd.getCurrentUser());
	Hidden currentDomainHidden = new Hidden("currentDomain", Wnd.getCurrentDomainNameURL());
	Hidden tokenHidden = new Hidden("token", Wnd.getToken());
	Hidden extensionHidden = new Hidden("extension", "");
	Hidden fileNameHidden = new Hidden("filename", "");
	Hidden passwordHidden = new Hidden("password", "");
	Hidden ownerHidden = new Hidden("owner", "user");
	Hidden securityHidden = new Hidden("security", "public");
	Hidden tgssHidden = new Hidden("tgss", "");
	Hidden sepeHidden = new Hidden("sepe", "");
	Hidden aeatHidden = new Hidden("aeat", "");
	
	// Button
	Button acceptBtnDialog;
	
	// ------------------------------------------------- Constructor
	
	protected CertificateDialog() {
		
		setCaption("Certificado Digital");
		
		initializeProviedElements();
		
		setWidget(binder.createAndBindUi(this));
		
		initializeForm();
		
		initElementHandlers();
		getButtonsPanel();
		
		deckPanel.showWidget(0);
		acceptBtnDialog.setVisible(false);
		
		enterprisesService.getDomainUserRoles(new AsyncCallback<DomainUserRoles>() {
			
			@Override
			public void onSuccess(DomainUserRoles userRolesDB) {
				userRoles = userRolesDB;
				
				if(Boolean.FALSE.equals(userRoles.isAdmin())) disableEnterprise();
				
				enterprisesService.getCertificates(false, new AsyncCallback<List<Certificate>>() {
					
					@Override
					public void onSuccess(List<Certificate> certificatesDB) {
						certificates = certificatesDB;
						initCertificateTypes();
						showDialog();
					}
					
					@Override
					public void onFailure(Throwable error) {
						showError("Ontenci\uf003n certificados :", error.getMessage());
					}
				});
				
			}

			@Override
			public void onFailure(Throwable error) {
				showError("Usuarios secundarios :", error.getMessage());
			}
			
		});
	}
	
	// ------------------------------------------------- Constructor methods

	private void initializeProviedElements() {
		// Visibility Btn
		visibilityBtn = createShowPassButton();
	}
	
	private void initElementHandlers() {
		// File TB
		fileNameTB.addValueChangeHandler(e -> fileNameHidden.setValue(e.getValue()));
		fileNameTB.addClickHandler(e -> {
			fileUpload.click();
			e.stopPropagation();
		});
		
		// Password TB
		passwordTB.addValueChangeHandler(e -> {
			passwordHidden.setValue(e.getValue());
			checkCertificate();
		});
		
		// Owner
		userRB.addValueChangeHandler(e -> {
			ownerHidden.setValue(Boolean.TRUE.equals(userRB.getValue()) ? "user" : "enterprise");
			initCertificateTypes();
		});
		userRB.setValue(true);
		enterpriseRB.addValueChangeHandler(e -> {
			ownerHidden.setValue(Boolean.TRUE.equals(enterpriseRB.getValue()) ? "enterprise" : "user");
			initCertificateTypes();
		});
		publicRB.addValueChangeHandler(e -> securityHidden.setValue(Boolean.TRUE.equals(publicRB.getValue()) ? "public" : "private"));
		privateRB.addValueChangeHandler(e -> securityHidden.setValue(Boolean.TRUE.equals(privateRB.getValue()) ? "private" : "public"));
		
		// CheckBoxes
		tgssCB.addValueChangeHandler(e -> tgssHidden.setValue(Boolean.TRUE.equals(e.getValue()) ? "tgss" : ""));
		sepeCB.addValueChangeHandler(e -> sepeHidden.setValue(Boolean.TRUE.equals(e.getValue()) ? "sepe" : ""));
		aeatCB.addValueChangeHandler(e -> aeatHidden.setValue(Boolean.TRUE.equals(e.getValue()) ? "aeat" : ""));
	}

	private void initializeForm() {
		// Hiddens
		Hidden rattachIdHidden = new Hidden("rattachId", "");
		Hidden raddinfoIdHidden = new Hidden("raddinfoId", "");
		
		// Create Form Panel
		form = new FormPanel();
		form.setAction(GWT.getModuleBaseURL() + "certificate/check/");
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
		
		// FileUpload
		fileUpload = new FileUpload();
		fileUpload.setName("uploader");
		fileUpload.getElement().setPropertyString("multiple", "multiple");
		fileUpload.getElement().setPropertyString("accept", ".p12,.pfx");
		fileUpload.getElement().getStyle().setDisplay(Display.NONE);
		
		fileUpload.addChangeHandler(e -> {
			String filename = getFileName(fileUpload.getFilename());
			String fileExt = getFileExtension(fileUpload.getFilename());

            if(filename.length() == 0)
            	 Window.alert("Cant upload file - Try again");
            else {
            	extensionHidden.setValue(fileExt);
            	fileNameHidden.setValue(filename);
            	fileNameTB.setValue(filename);
            	checkCertificate();
            }
		});
		
		// Attach Btn
		AonToolbarSmallButton attachBtn = new AonToolbarSmallButton("Adjuntar archivo", AON.CSS.aonIconAttach());
		attachBtn.addClickHandler(e -> fileUpload.click());
		filePanel.add(attachBtn);
		
		// Visibility Btn
		visibilityBtn = createShowPassButton();
		
		//Add all to FlowPanel to add to FormPanel
		HTMLPanel flowFormPanel = new HTMLPanel("");
		flowFormPanel.add(userLoginHidden);
		flowFormPanel.add(currentDomainHidden);
		flowFormPanel.add(tokenHidden);
		flowFormPanel.add(rattachIdHidden);
		flowFormPanel.add(raddinfoIdHidden);
		flowFormPanel.add(extensionHidden);
		flowFormPanel.add(fileNameHidden);
		flowFormPanel.add(passwordHidden);
		flowFormPanel.add(fileUpload);
		flowFormPanel.add(ownerHidden);
		flowFormPanel.add(securityHidden);
		flowFormPanel.add(tgssHidden);
		flowFormPanel.add(sepeHidden);
		flowFormPanel.add(aeatHidden);
		form.add(flowFormPanel);
		formPanel.add(form);
	}
	
	private void checkCertificate() {
    	if(AonStringUtils.isNotBlank(fileUpload.getFilename()) && AonStringUtils.isNotBlank(passwordTB.getValue())) {
			showLoading("Verificando certificado");
			form.submit();
    	}
	}

	// ------------------------------------------------- Parse JSON
	
	private void parseJSON(JSONObject json) {
		JSONValue type = json.get("type");
		if(null == type || AonStringUtils.isBlank(type.toString())) {
			// CertificateInfo
			parseCertificateInfo(json);
			createCertificateInfoPanel();
			showSuccess("Validaci\u00f3n", "Certificado validado correctamente");
			acceptBtnDialog.setVisible(true);
		} else {
			if(AonStringUtils.containsIgnoreCase(type.toString(), "create")) {
				hide();
				onAccept();
			}else {
				// Error
				JSONValue message = json.get("message");
				showError("Certificado", message.toString().replace("ñ", "\u00F1"));
			}
		}
	}

	private void parseCertificateInfo(JSONObject json) {
		certificateInfo = new CertificateInfo();
		
		JSONValue enterprise = json.get("enterprise");
		if(null != enterprise) certificateInfo.setEnterprise(enterprise.toString().replaceAll("(^\")|(\"$)", ""));
		
		JSONValue cif = json.get("cif");
		if(null != cif) certificateInfo.setCif(cif.toString().replaceAll("(^\")|(\"$)", ""));
		
		JSONValue name = json.get("name");
		if(null != name) certificateInfo.setName(name.toString().replaceAll("(^\")|(\"$)", ""));
		
		JSONValue surname = json.get("surname");
		if(null != surname) certificateInfo.setSurname(surname.toString().replaceAll("(^\")|(\"$)", ""));
			
		JSONValue document = json.get("document");
		if(null != document) certificateInfo.setDocument(document.toString().replaceAll("(^\")|(\"$)", ""));
		
		JSONValue type = json.get("typeCert");
		if(null != type) certificateInfo.setType(type.toString().replaceAll("(^\")|(\"$)", ""));
		
		JSONValue ocupation = json.get("ocupation");
		if(null != ocupation) certificateInfo.setOcupation(ocupation.toString().replaceAll("(^\")|(\"$)", ""));
		
		try {
			JSONValue fromDate = json.get("fromDate");
			if(null != fromDate) certificateInfo.setFromDate(formatDate.parse(fromDate.toString().replaceAll("(^\")|(\"$)", "")));
		} catch (Exception e) {
			// Nothing to do here
		}
		
		try {
			JSONValue toDate = json.get("toDate");
			if(null != toDate) certificateInfo.setToDate(formatDate.parse(toDate.toString().replaceAll("(^\")|(\"$)", "")));
		} catch (Exception e) {
			// Nothing to do here
		}
	}
	
	private void createCertificateInfoPanel() {
		String html = "<b>Emitido para: </b>" + (AonStringUtils.isBlank(certificateInfo.getDocument()) ? "" : " (" + certificateInfo.getDocument() + ") ") + certificateInfo.getName() + " " + certificateInfo.getSurname();
		html += AonStringUtils.isBlank(certificateInfo.getCif()) ? "<br>" : "<br><b>Representando: </b>"; 
		html += AonStringUtils.isBlank(certificateInfo.getOcupation()) ? "" : certificateInfo.getOcupation();
		html += AonStringUtils.isBlank(certificateInfo.getEnterprise()) ? "" :
			(AonStringUtils.isBlank(certificateInfo.getCif()) ? certificateInfo.getEnterprise() + "<br>" : "(" + certificateInfo.getCif() +") " + certificateInfo.getEnterprise() + "<br>");
		html += "<b>Fecha expiraci\u00f3n: </b> " + formatDate.format(certificateInfo.getToDate());
		certificateInfoPanel.add(new HTMLPanel(html));
	}

	// ------------------------------------------------- View Methods
	
	private AonToolbarSmallButton createShowPassButton() {
		AonToolbarSmallButton showPassBtn = new AonToolbarSmallButton("Mostrar", AON.CSS.aonIconShowPass());
		showPassBtn.addClickHandler(e -> {
			String type = passwordTB.getElement().getAttribute("type");
			if(AonStringUtils.isBlank(type) || !type.equals("text"))
				passwordTB.getElement().setAttribute("type", "text");
			else
				passwordTB.getElement().setAttribute("type", "password");
		});
		
		return showPassBtn;
	}
	
	private void disableEnterprise() {
		enterpriseRB.setEnabled(false);
		enterpriseRB.setTitle("Opci\u00f3n para usuarios administradores");
		publicRB.getElement().getStyle().setDisplay(Display.NONE);
		privateRB.getElement().getStyle().setDisplay(Display.NONE);
	}
	
	private void initCertificateTypes() {
		tgssCB.setEnabled(true);
		sepeCB.setEnabled(true);
		aeatCB.setEnabled(true);
		tgssCB.setValue(false, true);
		sepeCB.setValue(false, true);
		aeatCB.setValue(false, true);
		String owner = ownerHidden.getValue();
		for(Certificate certificate: certificates)
			if(AonStringUtils.equalsIgnoreCase(certificate.getOwner().name(), owner))
				for(CertificateType tag : certificate.getTags())
					checkTagEnable(tag);
	}

	private void checkTagEnable(CertificateType tag) {
		if(tag.equals(CertificateType.TGSS)) tgssCB.setEnabled(false);
		if(tag.equals(CertificateType.SEPE)) sepeCB.setEnabled(false);
		if(tag.equals(CertificateType.AEAT)) aeatCB.setEnabled(false);
	}

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

	// ------------------------------------------------- ButtonsPanel
	
	private void getButtonsPanel() {
		acceptBtnDialog = new Button();
		acceptBtnDialog.setStyleName(AON.CSS.aonOkButtonSmall());
		acceptBtnDialog.setText("Continuar");
		acceptBtnDialog.addClickHandler(e -> accept());
		
		buttonsPanel.add(acceptBtnDialog);
		
		Button closeBtnDialog = new Button();
		closeBtnDialog.setStyleName(AON.CSS.aonCancelButtonSmall());
		closeBtnDialog.setText("Cerrar");
		closeBtnDialog.addClickHandler(e -> hide());
		
		buttonsPanel.add(closeBtnDialog);
	}

	private void accept() {
		if(deckPanel.getVisibleWidget() == 0) {
			deckPanel.showWidget(1);
			acceptBtnDialog.setText("Grabar");
		} else {
			form.setAction(GWT.getModuleBaseURL() + "certificate/create/");
			form.submit();
		}
	}
	
	// ------------------------------------------------- Abstract methods
	
	public abstract void onAccept();
	
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
