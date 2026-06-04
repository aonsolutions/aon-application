package com.esferalia.aon.gwt.common.client.widget.solutions;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.gwt.common.client.CommonServiceAsync;
import com.esferalia.aon.gwt.common.client.CommonServiceAsyncDecorator;
import com.esferalia.aon.occam.api.model.Certificate;
import com.esferalia.aon.occam.api.model.Certificate.CertificateOwner;
import com.esferalia.aon.occam.api.model.Certificate.CertificateSecurity;
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
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Hidden;

public class AonCertificateDialog extends AonCustomDialog {

	// ------------------------------------------------- CommonServiceAsync

	static CommonServiceAsync commonService;

	private static void initializeCommonService() {
		if (commonService == null) {
			CommonServiceAsync commonServiceRaw = GWT.create(CommonService.class);
			commonService = new CommonServiceAsyncDecorator(commonServiceRaw);
		}
	}
	
	public static interface AonCerticateDialogCallback {
		void onAccept();
	}

	// Container Panel
	private HTMLPanel container = new HTMLPanel("");
	
	// Message Panel
	private HTMLPanel messagePanel = new HTMLPanel("");
	
	// DeckPanel
	private DeckPanel deckPanel = new DeckPanel();
	
	// File Panel
	private HTMLPanel filePanel = new HTMLPanel("");
	private AonCustomTextBox certificate = new AonCustomTextBox("Certificado");
	private AonCustomTextBox password = new AonCustomTextBox("Contrase\u00F1a");
	
	// Config Panel
	private HTMLPanel configPanel = new HTMLPanel("");
	private HTMLPanel certificateInfoPanel = new HTMLPanel("");
	private AonCustomTextBox description = new AonCustomTextBox("Descripci\u00f3n");
	private AonCustomListBox use = new AonCustomListBox("Uso");
	private AonCustomListBox security = new AonCustomListBox("Seguridad");
	
	private HTMLPanel typePanel = new HTMLPanel("");
	private AonCustomCheckBox tgss = new AonCustomCheckBox("TGSS");
	private AonCustomCheckBox sepe = new AonCustomCheckBox("SEPE");
	private AonCustomCheckBox aeat = new AonCustomCheckBox("AEAT");
	
	// Buttons Panel
	private HTMLPanel buttonsPanel = new HTMLPanel("");
	private Button acceptBtnDialog;
	private Button verifyBtnDialog;
	
	// Variables
	
	private DateTimeFormat formatDate = DateTimeFormat.getFormat("dd/MM/yyyy");
	
	private DomainUserRoles userRoles;
	private CertificateInfo certificateInfo;
	private List<Certificate> certificates = Collections.emptyList();
	
	// Forms
	FormPanel form;
	FileUpload fileUpload;
	
	FormPanel formUpdate;
	
	Hidden userLoginHidden = new Hidden("currentUser", "");
	Hidden currentDomainHidden = new Hidden("currentDomain", "");
	//Hidden tokenHidden = new Hidden("token", "");
	Hidden rattachIdHidden = new Hidden("rattachId", "");
	Hidden raddinfoIdHidden = new Hidden("raddinfoId", "");
	Hidden extensionHidden = new Hidden("extension", "");
	Hidden fileNameHidden = new Hidden("filename", "");
	Hidden passwordHidden = new Hidden("password", "");
	Hidden ownerHidden = new Hidden("owner", "user");
	Hidden securityHidden = new Hidden("security", "public");
	Hidden tgssHidden = new Hidden("tgss", "");
	Hidden sepeHidden = new Hidden("sepe", "");
	Hidden aeatHidden = new Hidden("aeat", "");
	
	private String domainName;
	private Integer domainId;
	private String user;
	
	private AonCerticateDialogCallback callback;
	
	// Constructor
	public AonCertificateDialog(String domainName,  Integer domainId, String user, AonCerticateDialogCallback callback) {
		setCaption("Nuevo Certificado Digital");
		initializeCommonService();
		
		this.domainName = domainName;
		this.domainId = domainId;
		this.user = user;
		
		userLoginHidden = new Hidden("currentUser", user);
		currentDomainHidden = new Hidden("currentDomain", domainName);
		//tokenHidden = new Hidden("token", token);
		
		this.getElement().getStyle().setProperty("min-width", "30rem");
		this.callback = callback;
		showCreate(callback);
	}
	
	public AonCertificateDialog(String domainName, Integer domainId, String user, Certificate certificate, AonCerticateDialogCallback callback) {
		setCaption("Editar Certificado Digital");
		initializeCommonService();
		
		this.domainName = domainName;
		this.domainId = domainId;
		this.user = user;
		
		userLoginHidden = new Hidden("currentUser", user);
		currentDomainHidden = new Hidden("currentDomain", domainName);
		//tokenHidden = new Hidden("token", token);
		
		this.getElement().getStyle().setProperty("min-width", "30rem");
		this.callback = callback;
		showUpdate(certificate, callback);
	}
	
	public void showCreate(AonCerticateDialogCallback callback) {
		initView();
		commonService.getDomainUserRoles(domainName, domainId, user, new AsyncCallback<DomainUserRoles>() {
			
			@Override
			public void onSuccess(DomainUserRoles userRolesDB) {
				userRoles = userRolesDB;
				
				if(Boolean.FALSE.equals(userRoles.isAdmin())) disableEnterprise();
				
				commonService.getCertificates(domainName, domainId, user, false, new AsyncCallback<List<Certificate>>() {
					
					@Override
					public void onSuccess(List<Certificate> certificatesDB) {
						certificates = certificatesDB;
						initCertificateTypes(null);
						deckPanel.showWidget(0);
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
	
	public void showUpdate(Certificate certificate, AonCerticateDialogCallback callback) {
		initUpdateView(certificate);
		commonService.getDomainUserRoles(domainName, domainId, user, new AsyncCallback<DomainUserRoles>() {
			
			@Override
			public void onSuccess(DomainUserRoles userRolesDB) {
				userRoles = userRolesDB;
				
				if(Boolean.FALSE.equals(userRoles.isAdmin())) disableEnterprise();
				
				commonService.getCertificates(domainName, domainId, user, false, new AsyncCallback<List<Certificate>>() {
					
					@Override
					public void onSuccess(List<Certificate> certificatesDB) {
						certificates = certificatesDB;
						initCertificateTypes(certificate);
						deckPanel.showWidget(0);
						fillCertificate(certificate);
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
	
	private void initView() {
		container.addStyleName(AON.CSS.aonFlexColumn2());
		container.getElement().getStyle().setProperty("margin", "1rem");
		
		// Init FilePanel
		filePanel.addStyleName(AON.CSS.aonFlexColumn2());
		
		AonTableButton fileBtn = new AonTableButton("Importar certificado", AON.CSS.aonIconAttach());
		fileBtn.addClickHandler(e -> fileUpload.click());
		certificate.addButton(fileBtn);
		certificate.getTextBox().addClickHandler(e -> fileUpload.click());
		certificate.getTextBox().addValueChangeHandler(e -> fileNameHidden.setValue(e.getValue()));
		
		AonTableButton visibilityBtn = new AonTableButton("Mostrar", AON.CSS.aonIconVisibility());
		visibilityBtn.addClickHandler(e -> {
			String type = password.getTextBox().getElement().getAttribute("type");
			if(AonStringUtils.isBlank(type) || !type.equals("text")) {
				password.getTextBox().getElement().setAttribute("type", "text");
				visibilityBtn.setTitle("Ocultar");
			} else {
				password.getTextBox().getElement().setAttribute("type", "password");
				visibilityBtn.setTitle("Mostrar");
			}
		});
		password.getTextBox().getElement().setAttribute("type", "password");
		password.addButton(visibilityBtn);
		password.getTextBox().addValueChangeHandler(e -> {
			passwordHidden.setValue(e.getValue());
			//checkCertificate();
		});
		
		filePanel.add(certificate);
		filePanel.add(password);
		deckPanel.add(filePanel);
		
		// Init ConfigPanel
		use.addItem("Personal", "0");
		use.addItem("Compartido", "1");
		use.getListBox().addChangeHandler(e -> {
			ownerHidden.setValue(AonStringUtils.equalsIgnoreCase(use.getValue(), "0") ? "user" : "enterprise");
			security.setVisible(AonStringUtils.equalsIgnoreCase(use.getValue(), "1"));
			initCertificateTypes(null);
		});
		
		security.addItem("Publico: Todos los usuarios", "0");
		security.addItem("Privado: Solo ususarios de la empresa", "1");
		security.setVisible(false);
		security.getListBox().addChangeHandler(e -> securityHidden.setValue(AonStringUtils.equalsIgnoreCase(use.getValue(), "0") ? "public" : "private"));
		
		// CheckBoxes
		tgss.getCheckBox().addValueChangeHandler(e -> tgssHidden.setValue(Boolean.TRUE.equals(e.getValue()) ? "tgss" : ""));
		sepe.getCheckBox().addValueChangeHandler(e -> sepeHidden.setValue(Boolean.TRUE.equals(e.getValue()) ? "sepe" : ""));
		aeat.getCheckBox().addValueChangeHandler(e -> aeatHidden.setValue(Boolean.TRUE.equals(e.getValue()) ? "aeat" : ""));
		
		typePanel.addStyleName(AON.CSS.aonItemFlex());
		typePanel.add(tgss);
		typePanel.add(sepe);
		typePanel.add(aeat);
		
		configPanel.addStyleName(AON.CSS.aonFlexColumn2());
		configPanel.add(certificateInfoPanel);
		configPanel.add(use);
		configPanel.add(security);
		configPanel.add(typePanel);
		deckPanel.add(configPanel);
		
		// Init Container
		deckPanel.getElement().getStyle().setProperty("margin", "0 1rem");
		
		container.add(messagePanel);
		container.add(deckPanel);
		container.add(buttonsPanel);
		
		// Init Form
		initializeCheckCertificateForm();
		container.add(form);
		
		// Init Buttons
		getButtonsPanel();
		
		this.add(container);
	}
	
	private void initUpdateView(Certificate certificate) {
		container.addStyleName(AON.CSS.aonFlexColumn2());
		container.getElement().getStyle().setProperty("margin", "1rem 0");
		
		// Init ConfigPanel
		description.getTextBox().addValueChangeHandler(e -> fileNameHidden.setValue(e.getValue()));
		
		use.addItem("Personal", "0");
		use.addItem("Compartido", "1");
		use.getListBox().setEnabled(false);
		
		security.addItem("Publico: Todos los usuarios", "0");
		security.addItem("Privado: Solo ususarios de la empresa", "1");
		security.setVisible(false);
		security.getListBox().addChangeHandler(e -> securityHidden.setValue(AonStringUtils.equalsIgnoreCase(security.getValue(), "0") ? "public" : "private"));
		
		// CheckBoxes
		tgss.getCheckBox().addValueChangeHandler(e -> {
			tgssHidden.setValue(Boolean.TRUE.equals(e.getValue()) ? "tgss" : "");
			
			if(Boolean.TRUE.equals(e.getValue())) certificate.addTag(CertificateType.TGSS);
			else certificate.removeTag(CertificateType.TGSS);
		});
		sepe.getCheckBox().addValueChangeHandler(e -> {
			sepeHidden.setValue(Boolean.TRUE.equals(e.getValue()) ? "sepe" : "");
			
			if(Boolean.TRUE.equals(e.getValue())) certificate.addTag(CertificateType.SEPE);
			else certificate.removeTag(CertificateType.SEPE);
		});
		aeat.getCheckBox().addValueChangeHandler(e -> {
			aeatHidden.setValue(Boolean.TRUE.equals(e.getValue()) ? "aeat" : "");

			if(Boolean.TRUE.equals(e.getValue())) certificate.addTag(CertificateType.AEAT);
			else certificate.removeTag(CertificateType.AEAT);	
		});
		
		
		typePanel.addStyleName(AON.CSS.aonItemFlex());
		typePanel.add(tgss);
		typePanel.add(sepe);
		typePanel.add(aeat);
		
		configPanel.addStyleName(AON.CSS.aonFlexColumn2());
		updateCertificateInfoPanel(certificate);
		configPanel.add(certificateInfoPanel);
		configPanel.add(description);
		configPanel.add(use);
		configPanel.add(security);
		configPanel.add(typePanel);
		deckPanel.add(configPanel);
		
		// Init Container
		deckPanel.getElement().getStyle().setProperty("margin", "0 1rem");
		
		container.add(messagePanel);
		container.add(deckPanel);
		container.add(buttonsPanel);
		
		// Init Form
		container.add(initializeUpdateCertificateForm(certificate));
		
		// Init Buttons
		getUpdateButtonsPanel(certificate);
		
		this.add(container);
	}
	
	private void fillCertificate(Certificate certificate) {
		description.setValue(certificate.getDescription());
		use.setValue(certificate.getOwner() == CertificateOwner.USER ? "0" : "1");
		if(certificate.getOwner() == CertificateOwner.ENTERPRISE) {
			security.setVisible(true);
			security.setValue(certificate.getConfidential() == CertificateSecurity.PUBLIC ? "0" : "1");
		}
		tgss.setValue(hasTGSSCertificate(certificate));
		sepe.setValue(hasSEPECertificate(certificate));
		aeat.setValue(hasAEATCertificate(certificate));
	}
	
	private void initializeCheckCertificateForm() {
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
				
				JSONValue type = json.isObject().get("type");
				if(AonStringUtils.equalsIgnoreCase(form.getAction(), GWT.getModuleBaseURL() + "certificate/check/") &&  (null == type || (null != type && !AonStringUtils.equalsIgnoreCase(type.toString().replaceAll("(^\")|(\"$)", ""), "error")) ) ) {
					accept();
				}
				
			} catch (NullPointerException | IllegalArgumentException err){
				acceptBtnDialog.setVisible(true);
				verifyBtnDialog.setVisible(false);
				// showError("Formato", "Error formateando la informaci\u00f3n");
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
            	certificate.getTextBox().setValue(filename);
            	//checkCertificate();
            }
		});
		
		//Add all to FlowPanel to add to FormPanel
		HTMLPanel flowFormPanel = new HTMLPanel("");
		flowFormPanel.add(userLoginHidden);
		flowFormPanel.add(currentDomainHidden);
		//flowFormPanel.add(tokenHidden);
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
	}
	
	private FormPanel initializeUpdateCertificateForm(Certificate certificate) {
		// Create Form Panel
		formUpdate = new FormPanel();
		formUpdate.setAction(GWT.getModuleBaseURL() + "certificate/create/");
		formUpdate.setEncoding(FormPanel.ENCODING_MULTIPART);
		formUpdate.setMethod(FormPanel.METHOD_POST);
		formUpdate.addSubmitCompleteHandler(e -> {
			callback.onAccept();
			hide();
		});
		
		rattachIdHidden = new Hidden("rattachId", certificate.getId().toString());
		fileNameHidden = new Hidden("filename", certificate.getDescription());
		passwordHidden = new Hidden("password", certificate.getPassword());
		securityHidden = new Hidden("security", certificate.getConfidential() != null && certificate.getConfidential().equals(CertificateSecurity.PRIVATE) ? "private" : "public");
		tgssHidden = new Hidden("tgss", hasTGSSCertificate(certificate) ? "tgss" : "");
		sepeHidden = new Hidden("sepe", hasSEPECertificate(certificate) ? "sepe" : "");
		aeatHidden = new Hidden("aeat", hasAEATCertificate(certificate) ? "aeat" : "");
		ownerHidden = new Hidden("owner", certificate.getOwner().name());
		
		HTMLPanel flowFormPanel = new HTMLPanel("");
		flowFormPanel.add(userLoginHidden);
		flowFormPanel.add(currentDomainHidden);
		//flowFormPanel.add(tokenHidden);
		flowFormPanel.add(rattachIdHidden);
		flowFormPanel.add(raddinfoIdHidden);
		flowFormPanel.add(extensionHidden);
		flowFormPanel.add(fileNameHidden);
		flowFormPanel.add(passwordHidden);
		
		flowFormPanel.add(ownerHidden);
		flowFormPanel.add(securityHidden);
		flowFormPanel.add(tgssHidden);
		flowFormPanel.add(sepeHidden);
		flowFormPanel.add(aeatHidden);
		formUpdate.add(flowFormPanel);
		
		return formUpdate;
	}
	
	private boolean hasTGSSCertificate(Certificate certificate) {
		if(null == certificate.getTags())
			return false;
		
		for(CertificateType tag : certificate.getTags())
			if(tag.equals(CertificateType.TGSS))
				return true;
		
		return false;
	}
	
	private boolean hasSEPECertificate(Certificate certificate) {
		if(null == certificate.getTags())
			return false;
		
		for(CertificateType tag : certificate.getTags())
			if(tag.equals(CertificateType.SEPE))
				return true;
		
		return false;
	}
	
	private boolean hasAEATCertificate(Certificate certificate) {
		if(null == certificate.getTags())
			return false;
		
		for(CertificateType tag : certificate.getTags())
			if(tag.equals(CertificateType.AEAT))
				return true;
		
		return false;
	}
	
	private void checkCertificate() {
    	if(AonStringUtils.isNotBlank(fileUpload.getFilename()) && AonStringUtils.isNotBlank(password.getValue())) {
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
			verifyBtnDialog.setVisible(false);
		} else {
			if(AonStringUtils.containsIgnoreCase(type.toString(), "create")) {
				hide();
				callback.onAccept();
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
	
	private void updateCertificateInfoPanel(Certificate certificate) {
		certificateInfoPanel.clear();
		certificateInfoPanel.addStyleName(AON.CSS.aonFlexColumn2());
		
		certificateInfo = certificate.getCertificateInfo();
		
		if(!AonStringUtils.isBlank(certificateInfo.getDocument())) {
			AonCustomTextBox emit = new AonCustomTextBox("Emitido para");
			emit.setEnable(false);
			emit.setValue("(" + certificateInfo.getDocument() + ") " + certificateInfo.getName() + " " + certificateInfo.getSurname());
			certificateInfoPanel.add(emit);
		}
		
		if(!AonStringUtils.isBlank(certificate.getCertificateInfo().getCif())) {
			AonCustomTextBox representative = new AonCustomTextBox("Representado");
			representative.setEnable(false);
			representative.setValue(
					AonStringUtils.isBlank(certificateInfo.getOcupation())
					? (certificateInfo.getEnterprise() + " (" + certificateInfo.getCif() +") " + certificateInfo.getEnterprise())
					: (certificateInfo.getOcupation() + " " + certificateInfo.getEnterprise() + " (" + certificateInfo.getCif() +") " + certificateInfo.getEnterprise())
			);
			certificateInfoPanel.add(representative);
		}
		
		if(null != certificateInfo.getToDate()) {
			AonCustomTextBox expirationDate = new AonCustomTextBox("Fecha Expiraci\u00f3n");
			expirationDate.setEnable(false);
			expirationDate.setValue(formatDate.format(certificateInfo.getToDate()));
			certificateInfoPanel.add(expirationDate);
		}
	}
	
	private void createCertificateInfoPanel() {
		certificateInfoPanel.clear();
		certificateInfoPanel.addStyleName(AON.CSS.aonFlexColumn2());
		
		if(!AonStringUtils.isBlank(certificateInfo.getDocument())) {
			AonCustomTextBox emit = new AonCustomTextBox("Emitido para");
			emit.setEnable(false);
			emit.setValue("(" + certificateInfo.getDocument() + ") " + certificateInfo.getName() + " " + certificateInfo.getSurname());
			certificateInfoPanel.add(emit);
		}
		
		if(!AonStringUtils.isBlank(certificateInfo.getCif())) {
			AonCustomTextBox representative = new AonCustomTextBox("Representado");
			representative.setEnable(false);
			representative.setValue(
					AonStringUtils.isBlank(certificateInfo.getOcupation())
					? (certificateInfo.getEnterprise() + " (" + certificateInfo.getCif() +") " + certificateInfo.getEnterprise())
					: (certificateInfo.getOcupation() + " " + certificateInfo.getEnterprise() + " (" + certificateInfo.getCif() +") " + certificateInfo.getEnterprise())
			);
			certificateInfoPanel.add(representative);
		}
		
		if(null != certificateInfo.getToDate()) {
			AonCustomTextBox expirationDate = new AonCustomTextBox("Fecha Expiraci\u00f3n");
			expirationDate.setEnable(false);
			expirationDate.setValue(formatDate.format(certificateInfo.getToDate()));
			certificateInfoPanel.add(expirationDate);
		}
	}
	
	private void disableEnterprise() {
		use.getListBox().getElement().getElementsByTagName("option").getItem(1).setAttribute("disabled", "disabled");
		use.setTitle("Opci\u00f3n para usuarios administradores");
		security.getElement().getStyle().setDisplay(Display.NONE);
	}
	
	private void initCertificateTypes(Certificate certificate) {
		tgss.setEnable(true);
		sepe.setEnable(true);
		aeat.setEnable(true);
		tgss.setValue(false, true);
		sepe.setValue(false, true);
		aeat.setValue(false, true);
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
		buttonsPanel.getElement().getStyle().setProperty("display", "flex");
		buttonsPanel.getElement().getStyle().setProperty("gap", "1rem");
		buttonsPanel.getElement().getStyle().setProperty("justify-content", "center");
		
		acceptBtnDialog = new Button();
		acceptBtnDialog.setStyleName(AON.CSS.aonOkButtonSmall());
		acceptBtnDialog.setText("Grabar");
		acceptBtnDialog.setVisible(false);
		acceptBtnDialog.addClickHandler(e -> accept());
		
		buttonsPanel.add(acceptBtnDialog);
		
		verifyBtnDialog = new Button();
		verifyBtnDialog.setStyleName(AON.CSS.aonOkButtonSmall());
		verifyBtnDialog.setText("Verificar");
		verifyBtnDialog.addClickHandler(e -> checkCertificate());
		
		buttonsPanel.add(verifyBtnDialog);
		
		Button closeBtnDialog = new Button();
		closeBtnDialog.setStyleName(AON.CSS.aonCancelButtonSmall());
		closeBtnDialog.setText("Cerrar");
		closeBtnDialog.addClickHandler(e -> hide());
		
		buttonsPanel.add(closeBtnDialog);
	}
	
	private void getUpdateButtonsPanel(Certificate certificate) {
		buttonsPanel.getElement().getStyle().setProperty("display", "flex");
		buttonsPanel.getElement().getStyle().setProperty("gap", "1rem");
		buttonsPanel.getElement().getStyle().setProperty("justify-content", "center");
		
		acceptBtnDialog = new Button();
		acceptBtnDialog.setStyleName(AON.CSS.aonOkButtonSmall());
		acceptBtnDialog.setText("Grabar");
		acceptBtnDialog.addClickHandler(e -> {
			if(hasTGSSCertificate(certificate) || hasSEPECertificate(certificate) || hasAEATCertificate(certificate)) {
				if(!existCertificateTypeInOtherCertificate(certificate))
					formUpdate.submit();
				else 
					showError("Certitficado Tipo", "No puede existir un tipo de certiticado repetido");
			} else 
				showError("Certitficado", "Debe seleccionar un tipo de certificado para poder guardarlo");
		});
		
		buttonsPanel.add(acceptBtnDialog);
		
		Button closeBtnDialog = new Button();
		closeBtnDialog.setStyleName(AON.CSS.aonCancelButtonSmall());
		closeBtnDialog.setText("Cerrar");
		closeBtnDialog.addClickHandler(e -> hide());
		
		buttonsPanel.add(closeBtnDialog);
	}

	private boolean existCertificateTypeInOtherCertificate(Certificate certificate) {
		Boolean exist = false;
		
		String owner = ownerHidden.getValue();
		for(Certificate certificateIt: certificates)
			if(AonStringUtils.equalsIgnoreCase(certificateIt.getOwner().name(), owner) && (null == certificate || !certificate.getId().equals(certificateIt.getId())))
				for(CertificateType tag : certificateIt.getTags())
					if(certificate.getTags().contains(tag))
						return true;
		
		return exist;
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
