package com.esferalia.aon.gwt.payroll.client;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.css.AonGwtTemplateResources;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonConfirmDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonConfirmDialog.AonConfirmDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessagePanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTableButton;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbar;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarSmallButton;
import com.esferalia.aon.gwt.payroll.shared.DigitalCertificateNew;
import com.esferalia.aon.gwt.payroll.shared.DigitalCertificateNew.CertificateOwner;
import com.esferalia.aon.gwt.payroll.shared.DigitalCertificateNew.CertificateSecurity;
import com.esferalia.aon.gwt.payroll.shared.DigitalCertificateNew.CertificateType;
import com.esferalia.aon.gwt.payroll.shared.SecondaryUserCertificate;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class MainDigitalCertificatesNew extends MainEntryPoint{

	// ------------------------------------------------------ UiBinder
	
	interface Binder extends UiBinder<Widget, MainDigitalCertificatesNew> {}

	private static final Binder binder = GWT.create(Binder.class);
	
	// ------------------------------------------------------ UiFields
	
	@UiField
	MyStyle style;

	interface MyStyle extends CssResource {
		String headerStyle();
		String loadingPanel();
		String warningTB();
		String flexGrow();
		String flex();
		String checkBox();
	}
	
	@UiField
	DockLayoutPanel dockLayoutPanel;
	
	@UiField (provided = true)
	AonToolbar toolbar;
	
	@UiField
	HTMLPanel messagePanel;
	
	@UiField
	TabLayoutPanel tabLayoutPanel;
	
	@UiField
	Grid userCertDataTableHeader;
	
	@UiField
	DeckPanel userCertDataTableDeckPanel;
	
	@UiField
	HTMLPanel  userCertLoadingPanel;
	
	@UiField
	ScrollPanel  userCertTableScrollPanel;
	
	@UiField
	Grid  userCertDataTable;
	
	@UiField
	HTMLPanel userSecondayUsersPanel;
	
	@UiField
	Grid userSecondaryUserDataTableHeader;
	
	@UiField
	ScrollPanel userSecondaryUserScrollPanel;
	
	@UiField
	DeckPanel userSecondaryUserDeckPanel;
	
	@UiField
	HTMLPanel userLoadingPanel;
	
	@UiField
	Grid userSecondaryUserDataTable;
	
	@UiField
	HTMLPanel userSecondaryUserToolbar;
	
	@UiField
	HTMLPanel addUserSecondaryUserToolbar;
	
	@UiField
	HTMLPanel showUserSecondaryUserToolbar;
	
	@UiField
	Grid enterpriseCertDataTableHeader;
	
	@UiField
	DeckPanel enterpriseCertDataTablDeckPanel;
	
	@UiField
	HTMLPanel  enterpriseCertLoadingPanel;
	
	@UiField
	ScrollPanel  enterpriseCertTableScrollPanel;
	
	@UiField
	Grid  enterpriseCertDataTable;
	
	@UiField
	HTMLPanel enterpriseSecondayUsersPanel;
	
	@UiField
	Grid enterpriseSecondaryUserDataTableHeader;
	
	@UiField
	ScrollPanel enterpriseSecondaryUserScrollPanel;
	
	@UiField
	DeckPanel enterpriseSecondaryUserDeckPanel;
	
	@UiField
	HTMLPanel enterpriseLoadingPanel;
	
	@UiField
	Grid enterpriseSecondaryUserDataTable;
	
	@UiField
	HTMLPanel enterpriseSecondaryUserToolbar;
	
	@UiField
	HTMLPanel addEnterpriseSecondaryUserToolbar;
	
	@UiField
	HTMLPanel showEnterpriseSecondaryUserToolbar;
	
	// ------------------------------------------------------ Variables
	
	private MainDigitalCertificatesObjectNew mainDigitalCertificatesObject;
	private DateTimeFormat formatFullDate = DateTimeFormat.getFormat("dd/MM/yyyy");
	
	private boolean showInactives = false;
	
	// ------------------------------------------------------ Constructor

	public MainDigitalCertificatesNew() {	
		GWT.<AonGwtTemplateResources>create(AonGwtTemplateResources.class).css().ensureInjected();
		AON.ensureInjected();
		
		initToolbar();
	
		Widget ui = binder.createAndBindUi(this);
		RootLayoutPanel.get(getRootPanel() != null ? getRootPanel() : "rootPanel").add(ui);
		
		initTabLayoutPanel();
		
		initUserCertDataTable();
		initEnterpriseCertDataTable();
		
		initUserSecondaryTable();
		initEnterpriseSecondaryTable();
	}

	// ------------------------------------------------------ Constructor.Methods
	
	private void initTabLayoutPanel() {
		tabLayoutPanel.getElement().getStyle().setMarginLeft(10, Unit.PX);
		tabLayoutPanel.getElement().getStyle().setMarginRight(10, Unit.PX);
		tabLayoutPanel.getElement().getStyle().setHeight(Window.getClientHeight() - 200.00, Unit.PX);
		
		tabLayoutPanel.addSelectionHandler(event -> {
			Integer index = event.getSelectedItem();
			if(index == 0)
				createUserCertDataTable();
			else if(index == 1)
				createEntepriseCertDataTable();
		});
		
		tabLayoutPanel.selectTab(0, false);
	}

	private void initUserSecondaryTable() {
		userSecondayUsersPanel.setVisible(false);
		
		userSecondaryUserDeckPanel.showWidget(0);
		
		AonTableButton loadingBtn = new AonTableButton("", AON.CSS.aonIconRenew());
		loadingBtn.addStyleName(style.loadingPanel());
		
		Label loadingL = new Label("Verificando certificado sistema RED...");
		loadingL.getElement().getStyle().setMarginLeft(5, Unit.PX);
		
		userLoadingPanel.add(loadingBtn);
		userLoadingPanel.add(loadingL);
		
		resetPreviewSecondaryUser(userSecondaryUserDataTableHeader, userSecondaryUserDataTable);
		paintHeaderSecondaryUser(userSecondaryUserDataTableHeader);
		setSecondaryUserColumnWidth(userSecondaryUserDataTableHeader, userSecondaryUserDataTable);
	}
	
	private void initEnterpriseSecondaryTable() {
		enterpriseSecondayUsersPanel.setVisible(false);
		
		enterpriseSecondaryUserDeckPanel.showWidget(0);
		
		AonTableButton loadingBtn = new AonTableButton("", AON.CSS.aonIconRenew());
		loadingBtn.addStyleName(style.loadingPanel());
		
		Label loadingL = new Label("Verificando certificado sistema RED...");
		loadingL.getElement().getStyle().setMarginLeft(5, Unit.PX);
		
		enterpriseLoadingPanel.add(loadingBtn);
		enterpriseLoadingPanel.add(loadingL);
		
		resetPreviewSecondaryUser(enterpriseSecondaryUserDataTableHeader, enterpriseSecondaryUserDataTable);
		paintHeaderSecondaryUser(enterpriseSecondaryUserDataTableHeader);
		setSecondaryUserColumnWidth(enterpriseSecondaryUserDataTableHeader, enterpriseSecondaryUserDataTable);
	}

	// ------------------------------------------------------ Init Preview (Tables TGSS & SEPE)
	
	private void initUserCertDataTable() {
		resetPreview(userCertDataTableHeader, userCertDataTable);
		initLoadingPanel(userCertLoadingPanel);
		userCertDataTableDeckPanel.showWidget(0);
	}

	private void initEnterpriseCertDataTable() {
		resetPreview(enterpriseCertDataTableHeader, enterpriseCertDataTable);
		initLoadingPanel(enterpriseCertLoadingPanel);
		enterpriseCertDataTablDeckPanel.showWidget(0);
	}
	
	private void initLoadingPanel(HTMLPanel loadingPanel) {
		AonTableButton loadingBtn = new AonTableButton("", AON.CSS.aonIconRenew());
		loadingBtn.addStyleName(style.loadingPanel());
		
		Label loadingL = new Label("Obteniendo certificados...");
		loadingL.getElement().getStyle().setMarginLeft(5, Unit.PX);
		
		loadingPanel.add(loadingBtn);
		loadingPanel.add(loadingL);
	}

	private void resetPreview(Grid dataTableHeader, Grid dataTable) {
		dataTableHeader.clear();
		dataTableHeader.resize(0, 0);
		dataTableHeader.resizeColumns(7);
		
		dataTable.clear();
		dataTable.resize(0, 0);
		dataTable.resizeColumns(7);
		
		paintHeader(dataTableHeader);
		setColumnWidth(dataTableHeader, dataTable);
	}

	private void resetPreviewSecondaryUser(Grid dataTableHeader, Grid dataTable) {
		dataTableHeader.clear();
		dataTableHeader.resize(0, 0);
		dataTable.clear();
		dataTable.resize(0, 0);
		dataTableHeader.resizeColumns(5);
		dataTable.resizeColumns(5);
	}
	
	private void paintHeader(Grid dataTableHeader) {
		int row = dataTableHeader.insertRow(dataTableHeader.getRowCount());
		
		Label password = new Label("CONTRASE\u00D1A");
		Label certificate = new Label("CERTIFICADO");
		AonToolbarSmallButton security = new AonToolbarSmallButton("", AON.CSS.aonIconLock());
		Label tgss = new Label("TGSS");
		Label sepe = new Label("SEPE");
		Label aeat = new Label("AEAT");
		Label buttons = new Label("");
		
		password.addStyleName(style.headerStyle());
		certificate.addStyleName(style.headerStyle());
		tgss.addStyleName(style.headerStyle());
		sepe.addStyleName(style.headerStyle());
		aeat.addStyleName(style.headerStyle());
		
		dataTableHeader.setWidget(row, 0, password);
		dataTableHeader.setWidget(row, 1, certificate);
		dataTableHeader.setWidget(row, 2, security);
		dataTableHeader.setWidget(row, 3, tgss);
		dataTableHeader.setWidget(row, 4, sepe);
		dataTableHeader.setWidget(row, 5, aeat);
		dataTableHeader.setWidget(row, 6, buttons);
	}
	
	private void setColumnWidth(Grid dataTableHeader, Grid dataTable) {
		//MaxWidth 750px
		dataTableHeader.getColumnFormatter().getElement(0).getStyle().setWidth(180, Unit.PX);
		dataTable.getColumnFormatter().getElement(0).getStyle().setWidth(180, Unit.PX);
		
		dataTableHeader.getColumnFormatter().getElement(1).getStyle().setWidth(230, Unit.PX);
		dataTable.getColumnFormatter().getElement(1).getStyle().setWidth(230, Unit.PX);
		
		dataTableHeader.getColumnFormatter().getElement(2).getStyle().setWidth(50, Unit.PX);
		dataTableHeader.getCellFormatter().getElement(0, 2).getStyle().setTextAlign(TextAlign.CENTER);
		dataTable.getColumnFormatter().getElement(2).getStyle().setWidth(50, Unit.PX);
		
		dataTableHeader.getColumnFormatter().getElement(3).getStyle().setWidth(50, Unit.PX);
		dataTableHeader.getCellFormatter().getElement(0, 3).getStyle().setTextAlign(TextAlign.CENTER);
		dataTable.getColumnFormatter().getElement(3).getStyle().setWidth(50, Unit.PX);
		
		dataTableHeader.getColumnFormatter().getElement(4).getStyle().setWidth(50, Unit.PX);
		dataTableHeader.getCellFormatter().getElement(0, 4).getStyle().setTextAlign(TextAlign.CENTER);
		dataTable.getColumnFormatter().getElement(4).getStyle().setWidth(50, Unit.PX);
		
		dataTableHeader.getColumnFormatter().getElement(5).getStyle().setWidth(50, Unit.PX);
		dataTableHeader.getCellFormatter().getElement(0, 5).getStyle().setTextAlign(TextAlign.CENTER);
		dataTable.getColumnFormatter().getElement(5).getStyle().setWidth(50, Unit.PX);
	}
	
	// ------------------------------------------------------ onModuleLoad
	
	public void onModuleLoad(MainDigitalCertificatesObjectNew mainDigitalCertificatesObject) {
		this.mainDigitalCertificatesObject = mainDigitalCertificatesObject;
		this.mainDigitalCertificatesObject.getEnterpriseId(
				s -> {
					mainDigitalCertificatesObject.getDomainUserRoles(domainUserRole -> {
						if(null == domainUserRole.isAdmin() || !domainUserRole.isAdmin())
							hideEnterpriseTab();
					}, fa -> {});
					loadDigitalCertificates();
				}, f -> {});
	}
	
	private void hideEnterpriseTab() {
		tabLayoutPanel.remove(1);
	}

	// ------------------------------------------------------ Init Preview (Secondary Users)
	
	private void paintHeaderSecondaryUser(Grid dataTableHeader) {
		int row = dataTableHeader.insertRow(dataTableHeader.getRowCount());
		
		Label name = new Label("NOMBRE");
		Label naf = new Label("NAF");
		Label status = new Label("ESTADO");
		Label date = new Label("FECHA ESTADO");
		Label action = new Label("");
		
		name.addStyleName(style.headerStyle());
		naf.addStyleName(style.headerStyle());
		status.addStyleName(style.headerStyle());
		date.addStyleName(style.headerStyle());
		action.addStyleName(style.headerStyle());
		
		dataTableHeader.setWidget(row, 0, name);
		dataTableHeader.setWidget(row, 1, naf);
		dataTableHeader.setWidget(row, 2, status);
		dataTableHeader.setWidget(row, 3, date);
		dataTableHeader.setWidget(row, 4, action);
	}
	
	private void setSecondaryUserColumnWidth(Grid dataTableHeader, Grid dataTable) {
		//MaxWidth 750px
		dataTableHeader.getColumnFormatter().getElement(0).getStyle().setWidth(230, Unit.PX);
		dataTable.getColumnFormatter().getElement(0).getStyle().setWidth(230, Unit.PX);
		
		dataTableHeader.getColumnFormatter().getElement(1).getStyle().setWidth(150, Unit.PX);
		dataTable.getColumnFormatter().getElement(1).getStyle().setWidth(150, Unit.PX);
		
		dataTableHeader.getColumnFormatter().getElement(2).getStyle().setWidth(150, Unit.PX);
		dataTable.getColumnFormatter().getElement(2).getStyle().setWidth(150, Unit.PX);
		
		dataTableHeader.getColumnFormatter().getElement(3).getStyle().setWidth(150, Unit.PX);
		dataTable.getColumnFormatter().getElement(3).getStyle().setWidth(150, Unit.PX);
	}
	
	// ------------------------------------------------------ Create certificate tables

	private void createUserCertDataTable() {
		initUserCertDataTable();
		createUserCertDataTableRows();
	}

	private void createEntepriseCertDataTable() {
		initEnterpriseCertDataTable();
		createEnterpriseCertDataTableRows();
	}
	
	private void createUserCertDataTableRows() {
		List<DigitalCertificateNew> userCertificateList = mainDigitalCertificatesObject.getUserCertificateList();
		if(userCertificateList.isEmpty())
			userCertDataTableDeckPanel.showWidget(2);
		else {
			userCertDataTableDeckPanel.showWidget(1);
			for(DigitalCertificateNew digitalCertificate : userCertificateList)
				insertCertificateRow(digitalCertificate, userCertDataTable);
		}
	}
	
	private void createEnterpriseCertDataTableRows() {
		List<DigitalCertificateNew> enterpriseCertificateList = mainDigitalCertificatesObject.getEnterpriseCertificateList();
		if(enterpriseCertificateList.isEmpty())
			enterpriseCertDataTablDeckPanel.showWidget(2);
		else {
			enterpriseCertDataTablDeckPanel.showWidget(1);
			for(DigitalCertificateNew digitalCertificate : enterpriseCertificateList)
				insertCertificateRow(digitalCertificate, enterpriseCertDataTable);
		}
	}
	
	// ------------------------------------------------------ Insert Rows
	
	private void insertCertificateRow(DigitalCertificateNew digitalCertificate, Grid dataTable) {
		// Insert new row
		int row = dataTable.insertRow(dataTable.getRowCount());
		
		// Form Panel
		createFormPanel(dataTable, row, digitalCertificate);
	}

	private void createFormPanel(Grid table, int row, DigitalCertificateNew digitalCertificate) {
		
		// Create Form Panel
		FormPanel formPanel = new FormPanel();
		formPanel.setAction(GWT.getModuleBaseURL()+ "certificate_new/");
		formPanel.setEncoding(FormPanel.ENCODING_MULTIPART);
		formPanel.setMethod(FormPanel.METHOD_POST);
		formPanel.addSubmitCompleteHandler(e -> {
			Map<String, String> successMap = new HashMap<>();
			successMap.put("Certitficado", "Los certidicados han sido actualizados correctamente");
			AonMessagePanel.showSuccess(messagePanel, successMap);
			loadDigitalCertificates();
		});
		
		Hidden rattachIdHidden = new Hidden("rattachId", "");
		Hidden raddinfoIdHidden = new Hidden("raddinfoId", "");
		Hidden extensionHidden = new Hidden("extension", "");
		Hidden fileNameHidden = new Hidden("filename", "");
		Hidden passwordHidden = new Hidden("password", "");
		Hidden userLoginHidden = new Hidden("currentUser", Wnd.getCurrentUser());
		Hidden currentDomainHidden = new Hidden("currentDomain", Wnd.getCurrentDomainNameURL());
		Hidden tokenHidden = new Hidden("token", Wnd.getToken());
		Hidden securityHidden = new Hidden("security", digitalCertificate.getConfidential() != null && digitalCertificate.getConfidential().equals(CertificateSecurity.PRIVATE) ? "private" : "public");
		Hidden tgssHidden = new Hidden("tgss", hasTGSSCertificate(digitalCertificate) ? "tgss" : "");
		Hidden sepeHidden = new Hidden("sepe", hasSEPECertificate(digitalCertificate) ? "sepe" : "");
		Hidden aeatHidden = new Hidden("aeat", hasAEATCertificate(digitalCertificate) ? "aeat" : "");
		Hidden ownerHidden = new Hidden("owner", digitalCertificate.getOwner().name());
		
		// Save Button
		AonTableButton saveButton = new AonTableButton("Guardar", AON.CSS.aonIconSave());
		
		// Password Panel
		HTMLPanel passwordPanel = new HTMLPanel("");
		passwordPanel.addStyleName(style.flex());
		
		PasswordTextBox passwordTB = new PasswordTextBox();
		passwordTB.setStyleName("aon-inputText");
		passwordTB.addValueChangeHandler(e -> {
			passwordHidden.setValue(e.getValue());
			
			if(AonStringUtils.isBlank(passwordTB.getValue())) {
				addWarningIcon(passwordPanel, passwordTB, null);
				saveButton.setEnabled(false);
			} else {
				if(hasWarningIcon(passwordPanel))
					removeWarningIcon(passwordPanel, passwordTB);
				saveButton.setEnabled(true);
			}
		});
		
		passwordPanel.add(passwordTB);
		
		if(Boolean.FALSE.equals(digitalCertificate.getHasCertificate())) {
			AonTableButton showPassBtn = createShowPassButton(passwordTB);
			passwordPanel.add(showPassBtn);
		} else if(digitalCertificate.getOwner() == CertificateOwner.ENTERPRISE){
			passwordTB.setEnabled(false);
		}
		
		// File Panel
		TextBox fileNameTB = new TextBox();
		fileNameTB.setStyleName("aon-inputText");
		fileNameTB.getElement().getStyle().setWidth(200, Unit.PX);
		fileNameTB.addValueChangeHandler(e -> fileNameHidden.setValue(e.getValue()));
		
		FileUpload fileU = new FileUpload();
		fileU.setName("uploader");
		fileU.getElement().setPropertyString("multiple", "multiple");
		fileU.getElement().setPropertyString("accept", ".p12");
		fileU.getElement().getStyle().setDisplay(Display.NONE);
		
		fileU.addChangeHandler(e -> {
			String filename = getFileName(fileU.getFilename());
			String fileExt = getFileExtension(fileU.getFilename());

            if(filename.length() == 0)
            	 Window.alert("Cant upload file - Try again");
            else {
            	extensionHidden.setValue(fileExt);
            	fileNameHidden.setValue(filename);
            	fileNameTB.setValue(filename);
            }
		});
		
		AonTableButton fileButton = new AonTableButton("Subir Cert", AON.CSS.aonIconAttach());
		fileButton.addClickHandler(e -> fileU.click());
		
		CertificateOwner owner = tabLayoutPanel.getSelectedIndex() == 0 ? CertificateOwner.USER : CertificateOwner.ENTERPRISE;
		
		CheckBox securityCB = new CheckBox();
		securityCB.addStyleName(style.checkBox());
		securityCB.setValue(digitalCertificate.getConfidential() != null && digitalCertificate.getConfidential().equals(CertificateSecurity.PRIVATE));
		securityCB.addValueChangeHandler(e -> {
			if(Boolean.TRUE.equals(e.getValue())) {
				digitalCertificate.setConfidential(CertificateSecurity.PRIVATE);
				securityHidden.setValue("private");
			} else {
				digitalCertificate.setConfidential(CertificateSecurity.PUBLIC);
				securityHidden.setValue("public");
			}
		});
		
		CheckBox tgssCB = new CheckBox();
		tgssCB.addStyleName(style.checkBox());
		tgssCB.setValue(hasTGSSCertificate(digitalCertificate));
		tgssCB.addValueChangeHandler(e -> {
			if(Boolean.TRUE.equals(e.getValue())) {
				if(this.mainDigitalCertificatesObject.hasOtherHasType(CertificateType.TGSS, owner)) {
					Map<String, String> warningMap = new HashMap<>();
					warningMap.put("Tipo certificado", "El tipo de certificado " + CertificateType.TGSS.name() + " ya existe");
					AonMessagePanel.showWarning(messagePanel, warningMap);
					tgssCB.setValue(false);
				} else {
					digitalCertificate.addTag(CertificateType.TGSS);
					tgssHidden.setValue("tgss");
				}
			} else {
				digitalCertificate.removeTag(CertificateType.TGSS);
				tgssHidden.setValue("");
			}
		});
		
		CheckBox sepeCB = new CheckBox();
		sepeCB.addStyleName(style.checkBox());
		sepeCB.setValue(hasSEPECertificate(digitalCertificate));
		sepeCB.addValueChangeHandler(e -> {
			if(Boolean.TRUE.equals(e.getValue())) {
				if(this.mainDigitalCertificatesObject.hasOtherHasType(CertificateType.SEPE, owner)) {
					Map<String, String> warningMap = new HashMap<>();
					warningMap.put("Tipo certificado", "El tipo de certificado " + CertificateType.SEPE.name() + " ya existe");
					AonMessagePanel.showWarning(messagePanel, warningMap);
					sepeCB.setValue(false);
				} else{
					digitalCertificate.addTag(CertificateType.SEPE);
					sepeHidden.setValue("sepe");
				}
			} else {
				digitalCertificate.removeTag(CertificateType.SEPE);
				sepeHidden.setValue("");
			}
		});
		
		CheckBox aeatCB = new CheckBox();
		aeatCB.addStyleName(style.checkBox());
		aeatCB.setValue(hasAEATCertificate(digitalCertificate));
		aeatCB.addValueChangeHandler(e -> {
			if(Boolean.TRUE.equals(e.getValue())) {
				if(this.mainDigitalCertificatesObject.hasOtherHasType(CertificateType.AEAT, owner)) {
					Map<String, String> warningMap = new HashMap<>();
					warningMap.put("Tipo certificado", "El tipo de certificado " + CertificateType.AEAT.name() + " ya existe");
					AonMessagePanel.showWarning(messagePanel, warningMap);
					aeatCB.setValue(false);
				} else {
					digitalCertificate.addTag(CertificateType.AEAT);
					aeatHidden.setValue("aeat");
				}
			} else {
				digitalCertificate.removeTag(CertificateType.AEAT);
				aeatHidden.setValue("");
			}
		});
		
		// Buttons Panel
		HTMLPanel buttonsPanel = new HTMLPanel("");
		buttonsPanel.addStyleName(style.flex());
		
		saveButton.addClickHandler(e -> {
			if(hasTGSSCertificate(digitalCertificate) || hasSEPECertificate(digitalCertificate) || hasAEATCertificate(digitalCertificate)) {
				formPanel.submit();
			} else {
				Map<String, String> errorMap = new HashMap<>();
				errorMap.put("Certitficado", "Debe seleccionar un tipo de certificado para poder guardarlo");
				AonMessagePanel.showError(messagePanel, errorMap);
			}
		});
		
		AonTableButton verifyButton = new AonTableButton("Verificar Certificado", AON.CSS.aonIconVerify());
		verifyButton.addClickHandler(e -> 
			mainDigitalCertificatesObject.verifyCertificate(
				digitalCertificate.getRattachId(), 
				digitalCertificate.getTags(), 
				s -> {
					Map<String, String> successMap = new HashMap<>();
					successMap.put("Certificado", "Certificado validado correctamente");
					AonMessagePanel.showSuccess(messagePanel, successMap);
				}, f -> {
					Map<String, String> warningMap = new HashMap<>();
					warningMap.put("Error verificaci\u00F3n", f.getMessage());
					AonMessagePanel.showWarning(messagePanel, warningMap);
				}
			)
		);
		verifyButton.setVisible(false);
		
		AonTableButton secondaryUsersButton = new AonTableButton("Usuarios Secundarios", AON.CSS.aonIconList());
		secondaryUsersButton.addClickHandler(e -> onSecondaryUser(digitalCertificate.getRattachId(), digitalCertificate.getTags()));
		
		secondaryUsersButton.setVisible(false);
		
		AonTableButton deleteButton = new AonTableButton("Borrar", AON.CSS.aonIconDelete());
		deleteButton.addClickHandler(e -> deleteCertificate(digitalCertificate));
		
		AonTableButton checkCertificateButton = new AonTableButton("Informaci\u00F3n", AON.CSS.aonIconInfo());
		checkCertificateButton.addClickHandler(e -> getCertificateInfo(digitalCertificate));
		
		// Buttons visibility
		if(Boolean.FALSE.equals(digitalCertificate.getHasCertificate())) {
			fileButton.setVisible(true);
			deleteButton.setVisible(false);
			verifyButton.setVisible(false);
			secondaryUsersButton.setVisible(false);
			checkCertificateButton.setVisible(false);
		} else {
			fileButton.setVisible(false);
			deleteButton.setVisible(true);
			if(hasTGSSCertificate(digitalCertificate))
				secondaryUsersButton.setVisible(true);
			if(hasSEPECertificate(digitalCertificate))
				verifyButton.setVisible(true);
			checkCertificateButton.setVisible(true);
		}
		
		//Add all to FlowPanel to add to FormPanel
		HTMLPanel flowPanel = new HTMLPanel("");
		flowPanel.addStyleName(style.flex());
				
		flowPanel.add(rattachIdHidden);
		flowPanel.add(raddinfoIdHidden);
		flowPanel.add(extensionHidden);
		flowPanel.add(fileNameHidden);
		flowPanel.add(passwordHidden);
		flowPanel.add(userLoginHidden);
		flowPanel.add(currentDomainHidden);
		flowPanel.add(tokenHidden);
		flowPanel.add(securityHidden);
		flowPanel.add(tgssHidden);
		flowPanel.add(sepeHidden);
		flowPanel.add(aeatHidden);
		flowPanel.add(ownerHidden);
		flowPanel.add(fileU);
		flowPanel.add(fileButton);
		
		formPanel.add(flowPanel);
		
		buttonsPanel.add(formPanel);
		buttonsPanel.add(saveButton);
		buttonsPanel.add(verifyButton);
		buttonsPanel.add(secondaryUsersButton);
		buttonsPanel.add(checkCertificateButton);
		buttonsPanel.add(deleteButton);
			
		// Fill fields
		rattachIdHidden.setValue(digitalCertificate.getRattachId()+"");
		raddinfoIdHidden.setValue(digitalCertificate.getRaddinfoId()+"");
		
		String password = digitalCertificate.getPassword();
		
		if(AonStringUtils.isBlank(password))
			passwordTB.setEnabled(true);
		
		passwordTB.setValue(digitalCertificate.getPassword());
		passwordHidden.setValue(passwordTB.getValue());
		
		String description = "No existe certficado";
		if(Boolean.TRUE.equals(digitalCertificate.getHasCertificate()))
			description = AonStringUtils.isBlank(digitalCertificate.getDescription()) ? "Certficado sin nombre" : digitalCertificate.getDescription();
		fileNameTB.setValue(description);
		fileNameHidden.setValue(description);
		
		table.setWidget(row, 0, passwordPanel);
		table.setWidget(row, 1, fileNameTB);
		table.setWidget(row, 2, securityCB);
		table.getCellFormatter().getElement(row, 2).getStyle().setTextAlign(TextAlign.CENTER);
		table.setWidget(row, 3, tgssCB);
		table.getCellFormatter().getElement(row, 3).getStyle().setTextAlign(TextAlign.CENTER);
		table.setWidget(row, 4, sepeCB);
		table.getCellFormatter().getElement(row, 4).getStyle().setTextAlign(TextAlign.CENTER);
		table.setWidget(row, 5, aeatCB);
		table.getCellFormatter().getElement(row, 5).getStyle().setTextAlign(TextAlign.CENTER);
		table.setWidget(row, 6, buttonsPanel);
	}
	
	private void getCertificateInfo(DigitalCertificateNew digitalCertificate) {
		mainDigitalCertificatesObject.validateCertJava(
				digitalCertificate.getRattachId(), 
				certificateInfo -> {
					String certificateInfoStr = certificateInfo.toString();
					certificateInfoStr += "<br>Validez desde : " + formatFullDate.format(certificateInfo.getFromDate()) + " hasta : " + formatFullDate.format(certificateInfo.getToDate());
					AonDialog dialog = new AonDialog("Informaci\u00F3n Certificado", new HTML(certificateInfoStr));
					dialog.info();
				}, 
				f -> {});
	}

	private void onSecondaryUser(Integer rattachId, List<CertificateType> tags) {
		CertificateOwner owner = tabLayoutPanel.getSelectedIndex() == 0 ? CertificateOwner.USER : CertificateOwner.ENTERPRISE;
		
		if(CertificateOwner.USER.equals(owner))
			userSecondayUsersPanel.setVisible(true);
		else
			enterpriseSecondayUsersPanel.setVisible(true);
		
		mainDigitalCertificatesObject.verifyCertificate(
			rattachId, 
			tags,
			success -> {
				Label loadingL =  null;
				if(CertificateOwner.USER.equals(owner))
					loadingL = (Label) userLoadingPanel.getWidget(userLoadingPanel.getWidgetCount()-1);
				else
					loadingL = (Label) enterpriseLoadingPanel.getWidget(enterpriseLoadingPanel.getWidgetCount()-1);
				
				loadingL.setText("Accediendo al sistema RED para consultar los usuarios secundarios...");
				
				mainDigitalCertificatesObject.getSecondaryUsers(
					rattachId, 
					s -> {
						if(CertificateOwner.USER.equals(owner))
							insertSecondaryUsersRows(userSecondaryUserDataTableHeader, userSecondaryUserDataTable, userSecondaryUserDeckPanel);
						else
							insertSecondaryUsersRows(enterpriseSecondaryUserDataTableHeader, enterpriseSecondaryUserDataTable, enterpriseSecondaryUserDeckPanel);
					}, 
					f -> {
						if(CertificateOwner.USER.equals(owner))
							userSecondayUsersPanel.setVisible(false);
						else
							enterpriseSecondayUsersPanel.setVisible(false);
						
						Map<String, String> warningMap = new HashMap<>();
						warningMap.put("Error usuarios secundarios", f.getMessage());
						AonMessagePanel.showWarning(messagePanel, warningMap);
					}
				);
			
			}, failure -> {
				if(CertificateOwner.USER.equals(owner))
					userSecondayUsersPanel.setVisible(false);
				else
					enterpriseSecondayUsersPanel.setVisible(false);
				
				Map<String, String> warningMap = new HashMap<>();
				warningMap.put("Error verificaci\u00F3n", failure.getMessage());
				AonMessagePanel.showWarning(messagePanel, warningMap);
			}
		);	
	}

	private void loadDigitalCertificates() {
		updateLoadingPanelStatus();
		this.mainDigitalCertificatesObject.getDigitalCertificates(
				s -> {
					Integer index = tabLayoutPanel.getSelectedIndex();
					if(index == 0) 
						createUserCertDataTable();
					else if(index == 1)
						createEntepriseCertDataTable();
				}, 
				f -> {});
	}

	private void updateLoadingPanelStatus() {
		CertificateOwner owner = tabLayoutPanel.getSelectedIndex() == 0 ? CertificateOwner.USER : CertificateOwner.ENTERPRISE;
		Label loadingL =  null;
		
		if(CertificateOwner.USER.equals(owner)) {
			userCertDataTableDeckPanel.showWidget(0);
			loadingL = (Label) userCertLoadingPanel.getWidget(userCertLoadingPanel.getWidgetCount()-1);
		} else {
			enterpriseCertDataTablDeckPanel.showWidget(0);
			loadingL = (Label) enterpriseCertLoadingPanel.getWidget(enterpriseCertLoadingPanel.getWidgetCount()-1);
		}
		
		loadingL.setText("Cargando certificados...");
	}

	private boolean hasTGSSCertificate(DigitalCertificateNew digitalCertificate) {
		if(null == digitalCertificate.getTags())
			return false;
		
		for(CertificateType tag : digitalCertificate.getTags())
			if(tag.equals(CertificateType.TGSS))
				return true;
		
		return false;
	}
	
	private boolean hasSEPECertificate(DigitalCertificateNew digitalCertificate) {
		if(null == digitalCertificate.getTags())
			return false;
		
		for(CertificateType tag : digitalCertificate.getTags())
			if(tag.equals(CertificateType.SEPE))
				return true;
		
		return false;
	}
	
	private boolean hasAEATCertificate(DigitalCertificateNew digitalCertificate) {
		if(null == digitalCertificate.getTags())
			return false;
		
		for(CertificateType tag : digitalCertificate.getTags())
			if(tag.equals(CertificateType.AEAT))
				return true;
		
		return false;
	}

	// ------------------------------------------------------ Insert Rows.Auxiliar Methods
	
	private AonTableButton createShowPassButton(PasswordTextBox passwordTB) {
		AonTableButton showPassBtn = new AonTableButton("Mostrar", AON.CSS.aonIconShowPass());
		showPassBtn.addClickHandler(e -> {
			String type = passwordTB.getElement().getAttribute("type");
			if(AonStringUtils.isBlank(type) || !type.equals("text"))
				passwordTB.getElement().setAttribute("type", "text");
			else
				passwordTB.getElement().setAttribute("type", "password");
		});
		
		showPassBtn.getElement().getStyle().setMarginLeft(5, Unit.PX);
		showPassBtn.getElement().getStyle().setMarginTop(4, Unit.PX);
		
		return showPassBtn;
	}

	// ------------------------------------------------------ Delete Certificate Methods

	private void deleteCertificate(DigitalCertificateNew digitalCertificate) {
		this.mainDigitalCertificatesObject.deleteDigitalCertificate(
				digitalCertificate, 
				s -> loadDigitalCertificates(), 
				f -> {});
	}

	// ------------------------------------------------------ Insert Secondary Users
	
	private void insertSecondaryUsersRows(Grid dataTableHeader, Grid dataTable, DeckPanel deckPanel) {
		dataTable.clear();
		dataTable.resize(0, 0);
		dataTable.resizeColumns(5);
		
		setSecondaryUserColumnWidth(dataTableHeader, dataTable);
		
		CertificateOwner owner = tabLayoutPanel.getSelectedIndex() == 0 ? CertificateOwner.USER : CertificateOwner.ENTERPRISE;
		
		if(CertificateOwner.USER.equals(owner))
			initSeconaryUserToolBar(addUserSecondaryUserToolbar, showUserSecondaryUserToolbar);
		else
			initSeconaryUserToolBar(addEnterpriseSecondaryUserToolbar, showEnterpriseSecondaryUserToolbar);
		
		if(mainDigitalCertificatesObject.getSecondaryUsers(showInactives).isEmpty())
			deckPanel.showWidget(1);
		else {
			deckPanel.showWidget(2);
			for(SecondaryUserCertificate secondaryUser : mainDigitalCertificatesObject.getSecondaryUsers(showInactives)) {
				if(CertificateOwner.USER.equals(owner))
					fillSecondaryUserRow(userSecondaryUserDataTable, secondaryUser);
				else
					fillSecondaryUserRow(enterpriseSecondaryUserDataTable, secondaryUser);
			}
		}
	}
	
	private void fillSecondaryUserRow(Grid dataTable, SecondaryUserCertificate secondaryUser) {
		// Insert new row
		int row = dataTable.insertRow(dataTable.getRowCount());
		
		// Name Label
		Label nameL = new Label(secondaryUser.getName());
		
		// NAF Label
		Label nafL = new Label(secondaryUser.getNaf());
		
		// NAF Label
		Label statusL = new Label(secondaryUser.getSituation());
		
		// NAF Label
		Label dateL = new Label(formatFullDate.format(secondaryUser.getSituation_date()));
		
		// Delete Button
		AonTableButton comunicateBtn;
		if(AonStringUtils.equalsIgnoreCase(secondaryUser.getSituation(), "Baja"))
			comunicateBtn =  new AonTableButton("Activar Certificado", AON.CSS.aonIconSend());
		else
			comunicateBtn =  new AonTableButton("Anular Certificado", AON.CSS.aonIconSendCancel());
		
		CertificateOwner owner = tabLayoutPanel.getSelectedIndex() == 0 ? CertificateOwner.USER : CertificateOwner.ENTERPRISE;
		
		comunicateBtn.addClickHandler(e -> {
			if(AonStringUtils.equalsIgnoreCase(secondaryUser.getSituation(), "Baja")) {
				SecondaryUserDialog dialog = new SecondaryUserDialog(mainDigitalCertificatesObject.getCertificateTGSSId(owner), secondaryUser.getNaf()) {
					
					@Override
					protected void onAccept() {
						Map<String, String> successMap = new HashMap<>();
						successMap.put("AVISO: Creado", "El usuario secundario ha sido creado correctamente.");
						AonMessagePanel.showSuccess(messagePanel, successMap);
						
						mainDigitalCertificatesObject.getSecondaryUsers(
								mainDigitalCertificatesObject.getCertificateTGSSId(owner), 
								t -> {
									if(CertificateOwner.USER.equals(owner))
										insertSecondaryUsersRows(userSecondaryUserDataTableHeader, userSecondaryUserDataTable, userSecondaryUserDeckPanel);
									else
										insertSecondaryUsersRows(enterpriseSecondaryUserDataTableHeader, enterpriseSecondaryUserDataTable, enterpriseSecondaryUserDeckPanel);
								}, 
								e -> {});
					}
				};
				dialog.center();
				dialog.show();
			} else {
				// Delete User Dialog Confirm
				AonConfirmDialog confirmDialog = new AonConfirmDialog();
				confirmDialog.confirm(
					"BORRADO", 
					"\u00BFRealmente desea eliminar a este usuario?",
					new AonConfirmDialogCallback() {

						@Override
						public void onAccept() {
							mainDigitalCertificatesObject.deleteSecondaryUser(mainDigitalCertificatesObject.getCertificateTGSSId(owner), secondaryUser, s -> {
								Map<String, String> successMap = new HashMap<>();
								successMap.put("AVISO: Borrado", "El usuario secundario ha sido borrado correctamente.");
								AonMessagePanel.showSuccess(messagePanel, successMap);
								
								mainDigitalCertificatesObject.getSecondaryUsers(
										mainDigitalCertificatesObject.getCertificateTGSSId(owner), 
										t -> {
											if(CertificateOwner.USER.equals(owner))
												insertSecondaryUsersRows(userSecondaryUserDataTableHeader, userSecondaryUserDataTable, userSecondaryUserDeckPanel);
											else
												insertSecondaryUsersRows(enterpriseSecondaryUserDataTableHeader, enterpriseSecondaryUserDataTable, enterpriseSecondaryUserDeckPanel);
										}, e -> {});
							}, f -> {});
						}

						@Override
						public void onCancel() {
							// Not use in this case
						}
					}
				);
			}
			
		});
		
		//Add to table
		dataTable.setWidget(row, 0, nameL);
		dataTable.setWidget(row, 1, nafL);
		dataTable.setWidget(row, 2, statusL);
		dataTable.setWidget(row, 3, dateL);
		dataTable.setWidget(row, 4, comunicateBtn);
		dataTable.getCellFormatter().getElement(row, 4).getStyle().setTextAlign(TextAlign.CENTER);
	}
	
	// ------------------------------------------------------ Insert Secondary Users.Toolbar
	
	private void initSeconaryUserToolBar(HTMLPanel addSecondaryUserToolbar, HTMLPanel showSecondaryUserToolbar) {
		addSecondaryUserToolbar.clear();
		showSecondaryUserToolbar.clear();
		
		AonTableButton addSecondaryUser = new AonTableButton("Nuevo Usuario Secundario",  AON.CSS.aonIconAdd());
		addSecondaryUser.addClickHandler(e -> onAddSecondaryUser());
		
		Label addSecondaryUserL = new Label("A\u00F1adir Autorizado");
		
		addSecondaryUserToolbar.add(addSecondaryUser);
		addSecondaryUserToolbar.add(addSecondaryUserL);
		
		Button showInactiveUserBtn = getEnableDisableButton();
		showInactiveUserBtn.addClickHandler(e -> {
			showInactives = !showInactives;
			CertificateOwner owner = tabLayoutPanel.getSelectedIndex() == 0 ? CertificateOwner.USER : CertificateOwner.ENTERPRISE;
			if(CertificateOwner.USER.equals(owner))
				insertSecondaryUsersRows(userSecondaryUserDataTableHeader, userSecondaryUserDataTable, userSecondaryUserDeckPanel);
			else
				insertSecondaryUsersRows(enterpriseSecondaryUserDataTableHeader, enterpriseSecondaryUserDataTable, enterpriseSecondaryUserDeckPanel);
		});
		
		Label inactiveL = new Label("Ver inactivos");
		
		showSecondaryUserToolbar.add(inactiveL);
		showSecondaryUserToolbar.add(showInactiveUserBtn);	
	}
	
	// ------------------------------------------------------ Insert Secondary Users.Toolbar Methods
	
	private void onAddSecondaryUser() {
		CertificateOwner owner = tabLayoutPanel.getSelectedIndex() == 0 ? CertificateOwner.USER : CertificateOwner.ENTERPRISE;
		SecondaryUserDialog dialog = new SecondaryUserDialog(mainDigitalCertificatesObject.getCertificateTGSSId(owner)) {
			
			@Override
			protected void onAccept() {
				Map<String, String> successMap = new HashMap<>();
				successMap.put("AVISO: Creado", "El usuario secundario ha sido creado correctamente.");
				AonMessagePanel.showSuccess(messagePanel, successMap);
				
				mainDigitalCertificatesObject.getSecondaryUsers(
						mainDigitalCertificatesObject.getCertificateTGSSId(owner), 
						t -> {
							CertificateOwner owner = tabLayoutPanel.getSelectedIndex() == 0 ? CertificateOwner.USER : CertificateOwner.ENTERPRISE;
							if(CertificateOwner.USER.equals(owner))
								insertSecondaryUsersRows(userSecondaryUserDataTableHeader, userSecondaryUserDataTable, userSecondaryUserDeckPanel);
							else
								insertSecondaryUsersRows(enterpriseSecondaryUserDataTableHeader, enterpriseSecondaryUserDataTable, enterpriseSecondaryUserDeckPanel);
						}, 
						e -> {});
			}
		};
		
		dialog.center();
		dialog.show();
	}
	
	// ------------------------------------------------------ Auxiliar Methods
	
	private Button getEnableDisableButton() {
		Button showInactiveUserBtn = new Button();
		showInactiveUserBtn.setStyleName(!showInactives ? AON.AON_ICON_DISABLE : AON.AON_ICON_ENABLE );
		showInactiveUserBtn.setStyleName(AON.AON_NO_MARGIN, true);
		showInactiveUserBtn.setStyleName(AON.AON_EDIT_DATA_TABLE_BUTTON, true);
		return showInactiveUserBtn;
	}

	private String getFileName(String filename) {
		String[] splits = filename.split("\\\\");
		return splits[splits.length-1].contains("\\.") ? splits[splits.length-1].split("\\.")[0] : splits[splits.length-1];
	}
	
	private String getFileExtension(String filename) {
		String[] splits = filename.split("\\.");
		return splits[splits.length-1];
	}
	
	// ------------------------------------------------------ Toolbar
	
	private void initToolbar() {
		this.toolbar = new AonToolbar("Gesti\u00F3n de certificados");
		
		AonToolbarButton newCertificateBtn = new AonToolbarButton("Nuevo certificado", AON.CSS.aonIconAdd());
		newCertificateBtn.addClickHandler(e -> createNewCertificate());
		toolbar.add(newCertificateBtn);
	}
	
	private void createNewCertificate() {
		CertificateOwner owner = tabLayoutPanel.getSelectedIndex() == 0 ? CertificateOwner.USER : CertificateOwner.ENTERPRISE;
		this.mainDigitalCertificatesObject.createNewCertificate(owner);
		Integer tab = tabLayoutPanel.getSelectedIndex();
		if(tab == 0)
			createUserCertDataTable();
		else if(tab == 1)
			createEntepriseCertDataTable();
	}

	// ------------------------------------------------------ Toolbar.Methods
	
	private void addWarningIcon(HTMLPanel panel, Widget widget, String message) {
		message = AonStringUtils.isBlank(message) ? "Este campo es obligatorio" : message;
		panel.add(new AonToolbarSmallButton(message, AON.CSS.aonIconWarning()));
		widget.addStyleName(style.warningTB());
		widget.addStyleName(style.flexGrow());
	}
	
	private boolean hasWarningIcon(HTMLPanel panel) {
		Widget widget = panel.getWidget(panel.getWidgetCount()-1);
		return widget instanceof AonToolbarSmallButton;
	}
	
	private void removeWarningIcon(HTMLPanel panel, Widget widget) {
		if(panel.getWidgetCount() > 2)
			panel.remove(panel.getWidgetCount() - 1);
		
		widget.removeStyleName(style.warningTB());
	}
	
}
