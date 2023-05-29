package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.css.AonGwtTemplateResources;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonConfirmDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonConfirmDialog.AonConfirmDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog.AonAcceptDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessagePanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTableButton;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbar;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.gwt.payroll.shared.SecondaryUserCertificate;
import com.esferalia.aon.occam.api.model.Certificate;
import com.esferalia.aon.occam.api.model.Certificate.CertificateOwner;
import com.esferalia.aon.occam.api.model.Certificate.CertificateSecurity;
import com.esferalia.aon.occam.api.model.security.CertificateType;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
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
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class MainCertificates extends MainEntryPoint{

	// ------------------------------------------------------ UiBinder
	
	interface Binder extends UiBinder<Widget, MainCertificates> {}

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
	
	private MainCertificatesObject mainDigitalCertificatesObject;
	private DateTimeFormat formatFullDate = DateTimeFormat.getFormat("dd/MM/yyyy");
	
	private Integer domainId = null;
	private boolean showInactives = false;
	
	// ------------------------------------------------------ Constructor

	public MainCertificates() {	
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
		
		userLoadingPanel.clear();
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
		
		enterpriseLoadingPanel.clear();
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
		loadingPanel.clear();
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
		dataTableHeader.resizeColumns(tabLayoutPanel.getSelectedIndex() == 0 ? 8 :9);
		
		dataTable.clear();
		dataTable.resize(0, 0);
		dataTable.resizeColumns(tabLayoutPanel.getSelectedIndex() == 0 ? 8 :9);
		
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
		
		Label certificateFor = new Label("TITULAR");
		Label representation = new Label("REPRESENTACI\u00d3N");
		Label type = new Label("F. EXPIRACI\u00d3N");
		Label alias = new Label("ALIAS");
		Label security = new Label("");
		Label tgss = new Label("TGSS");
		Label sepe = new Label("SEPE");
		Label aeat = new Label("AEAT");
		Label buttons = new Label("");
		
		certificateFor.addStyleName(style.headerStyle());
		representation.addStyleName(style.headerStyle());
		type.addStyleName(style.headerStyle());
		alias.addStyleName(style.headerStyle());
		tgss.addStyleName(style.headerStyle());
		sepe.addStyleName(style.headerStyle());
		aeat.addStyleName(style.headerStyle());
		
		dataTableHeader.setWidget(row, 0, certificateFor);
		dataTableHeader.setWidget(row, 1, representation);
		dataTableHeader.setWidget(row, 2, type);
		dataTableHeader.setWidget(row, 3, alias);
		if(tabLayoutPanel.getSelectedIndex() == 0) {
			dataTableHeader.setWidget(row, 4, tgss);
			dataTableHeader.setWidget(row, 5, sepe);
			dataTableHeader.setWidget(row, 6, aeat);
			dataTableHeader.setWidget(row, 7, buttons);
		} else {
			dataTableHeader.setWidget(row, 4, security);
			dataTableHeader.setWidget(row, 5, tgss);
			dataTableHeader.setWidget(row, 6, sepe);
			dataTableHeader.setWidget(row, 7, aeat);
			dataTableHeader.setWidget(row, 8, buttons);
		}
	}
	
	private void setColumnWidth(Grid dataTableHeader, Grid dataTable) {
		//MaxWidth 750px
		dataTableHeader.getColumnFormatter().getElement(0).getStyle().setWidth(240, Unit.PX);
		dataTable.getColumnFormatter().getElement(0).getStyle().setWidth(240, Unit.PX);
		
		dataTableHeader.getColumnFormatter().getElement(1).getStyle().setWidth(240, Unit.PX);
		dataTable.getColumnFormatter().getElement(1).getStyle().setWidth(240, Unit.PX);
		
		dataTableHeader.getColumnFormatter().getElement(2).getStyle().setWidth(100, Unit.PX);
		dataTableHeader.getCellFormatter().getElement(0, 2).getStyle().setTextAlign(TextAlign.CENTER);
		dataTable.getColumnFormatter().getElement(2).getStyle().setWidth(100, Unit.PX);
		
		dataTableHeader.getColumnFormatter().getElement(3).getStyle().setWidth(150, Unit.PX);
		dataTableHeader.getCellFormatter().getElement(0, 3).getStyle().setTextAlign(TextAlign.CENTER);
		dataTable.getColumnFormatter().getElement(3).getStyle().setWidth(150, Unit.PX);
		
		if(tabLayoutPanel.getSelectedIndex() == 0) {
			dataTableHeader.getColumnFormatter().getElement(4).getStyle().setWidth(50, Unit.PX);
			dataTableHeader.getCellFormatter().getElement(0, 4).getStyle().setTextAlign(TextAlign.CENTER);
			dataTable.getColumnFormatter().getElement(4).getStyle().setWidth(50, Unit.PX);
			
			dataTableHeader.getColumnFormatter().getElement(5).getStyle().setWidth(50, Unit.PX);
			dataTableHeader.getCellFormatter().getElement(0, 5).getStyle().setTextAlign(TextAlign.CENTER);
			dataTable.getColumnFormatter().getElement(5).getStyle().setWidth(50, Unit.PX);
			
			dataTableHeader.getColumnFormatter().getElement(6).getStyle().setWidth(50, Unit.PX);
			dataTableHeader.getCellFormatter().getElement(0, 6).getStyle().setTextAlign(TextAlign.CENTER);
			dataTable.getColumnFormatter().getElement(6).getStyle().setWidth(50, Unit.PX);
			
		} else {
			dataTableHeader.getColumnFormatter().getElement(4).getStyle().setWidth(50, Unit.PX);
			dataTableHeader.getCellFormatter().getElement(0, 4).getStyle().setTextAlign(TextAlign.CENTER);
			dataTable.getColumnFormatter().getElement(4).getStyle().setWidth(50, Unit.PX);
			
			dataTableHeader.getColumnFormatter().getElement(5).getStyle().setWidth(50, Unit.PX);
			dataTableHeader.getCellFormatter().getElement(0, 5).getStyle().setTextAlign(TextAlign.CENTER);
			dataTable.getColumnFormatter().getElement(5).getStyle().setWidth(50, Unit.PX);
			
			dataTableHeader.getColumnFormatter().getElement(6).getStyle().setWidth(50, Unit.PX);
			dataTableHeader.getCellFormatter().getElement(0, 6).getStyle().setTextAlign(TextAlign.CENTER);
			dataTable.getColumnFormatter().getElement(6).getStyle().setWidth(50, Unit.PX);
			
			dataTableHeader.getColumnFormatter().getElement(7).getStyle().setWidth(50, Unit.PX);
			dataTableHeader.getCellFormatter().getElement(0, 7).getStyle().setTextAlign(TextAlign.CENTER);
			dataTable.getColumnFormatter().getElement(7).getStyle().setWidth(50, Unit.PX);
		}
		
	}
	
	// ------------------------------------------------------ onModuleLoad
	
	@Override
	public void onModuleLoad() {
		onModuleLoad(new MainCertificatesObject());
	}
	
	public void onModuleLoad(MainCertificatesObject mainDigitalCertificatesObject) {
		this.mainDigitalCertificatesObject = mainDigitalCertificatesObject;
		this.mainDigitalCertificatesObject.getEnterpriseId(
				s -> {
					mainDigitalCertificatesObject.getDomainUserRoles(domainUserRole -> {
						if(!domainUserRole.isAdmin())
							hideEnterpriseTab();
					}, fa -> {});
					mainDigitalCertificatesObject.getDomainId(domainIdIn -> {
						domainId = domainIdIn;
						loadDigitalCertificates();
					}, f -> {});
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
		dataTableHeader.getColumnFormatter().getElement(0).getStyle().setWidth(300, Unit.PX);
		dataTable.getColumnFormatter().getElement(0).getStyle().setWidth(300, Unit.PX);
		
		dataTableHeader.getColumnFormatter().getElement(1).getStyle().setWidth(200, Unit.PX);
		dataTableHeader.getCellFormatter().getElement(0, 1).getStyle().setTextAlign(TextAlign.CENTER);
		dataTable.getColumnFormatter().getElement(1).getStyle().setWidth(200, Unit.PX);
		
		dataTableHeader.getColumnFormatter().getElement(2).getStyle().setWidth(200, Unit.PX);
		dataTableHeader.getCellFormatter().getElement(0, 2).getStyle().setTextAlign(TextAlign.CENTER);
		dataTable.getColumnFormatter().getElement(2).getStyle().setWidth(200, Unit.PX);
		
		dataTableHeader.getColumnFormatter().getElement(3).getStyle().setWidth(200, Unit.PX);
		dataTableHeader.getCellFormatter().getElement(0, 3).getStyle().setTextAlign(TextAlign.CENTER);
		dataTable.getColumnFormatter().getElement(3).getStyle().setWidth(200, Unit.PX);
		
		dataTableHeader.getColumnFormatter().getElement(4).getStyle().setWidth(90, Unit.PX);
		dataTable.getColumnFormatter().getElement(4).getStyle().setWidth(90, Unit.PX);
	}
	
	// ------------------------------------------------------ Create certificate tables

	private void createUserCertDataTable() {
		hideSecondaryUsers();
		initUserCertDataTable();
		createUserCertDataTableRows();
	}

	private void createEntepriseCertDataTable() {
		hideSecondaryUsers();
		initEnterpriseCertDataTable();
		createEnterpriseCertDataTableRows();
	}
	
	private void createUserCertDataTableRows() {
		List<Certificate> userCertificateList = mainDigitalCertificatesObject.getUserCertificateList();
		if(userCertificateList.isEmpty())
			userCertDataTableDeckPanel.showWidget(2);
		else {
			userCertDataTableDeckPanel.showWidget(1);
			for(Certificate certificate : userCertificateList)
				insertCertificateRow(certificate, userCertDataTable, false);
		}
	}
	
	private void createEnterpriseCertDataTableRows() {
		List<Certificate> enterpriseCertificateList = mainDigitalCertificatesObject.getEnterpriseCertificateList();
		if(enterpriseCertificateList.isEmpty())
			enterpriseCertDataTablDeckPanel.showWidget(2);
		else {
			enterpriseCertDataTablDeckPanel.showWidget(1);
			for(Certificate certificate : enterpriseCertificateList)
				insertCertificateRow(certificate, enterpriseCertDataTable, true);
		}
	}
	
	// ------------------------------------------------------ Insert Rows
	
	private void insertCertificateRow(Certificate certificate, Grid dataTable, boolean isEnterprise) {
		// Insert new row
		int row = dataTable.insertRow(dataTable.getRowCount());
		
		// Form Panel
		createFormPanel(dataTable, row, certificate, isEnterprise);
	}

	private void createFormPanel(Grid table, int row, Certificate certificate, boolean isEnterprise) {
		
		// Save Button
		AonTableButton saveButton = new AonTableButton("Guardar", AON.CSS.aonIconSave());
		
		// Create Form Panel
		FormPanel formPanel = new FormPanel();
		formPanel.setAction(GWT.getModuleBaseURL() + "certificate/create/");
		formPanel.setEncoding(FormPanel.ENCODING_MULTIPART);
		formPanel.setMethod(FormPanel.METHOD_POST);
		formPanel.addSubmitCompleteHandler(e -> {
			saveButton.setEnabled(true);
			showSuccess("Certitficado", "Los certificados han sido actualizados correctamente");
			loadDigitalCertificates();
		});
		
		Hidden rattachIdHidden = new Hidden("rattachId", certificate.getId().toString());
		Hidden raddinfoIdHidden = new Hidden("raddinfoId", "");
		Hidden extensionHidden = new Hidden("extension", "");
		Hidden fileNameHidden = new Hidden("filename", certificate.getDescription());
		Hidden passwordHidden = new Hidden("password", certificate.getPassword());
		Hidden userLoginHidden = new Hidden("currentUser", Wnd.getCurrentUser());
		Hidden currentDomainHidden = new Hidden("currentDomain", Wnd.getCurrentDomainNameURL());
		Hidden tokenHidden = new Hidden("token", Wnd.getToken());
		Hidden securityHidden = new Hidden("security", certificate.getConfidential() != null && certificate.getConfidential().equals(CertificateSecurity.PRIVATE) ? "private" : "public");
		Hidden tgssHidden = new Hidden("tgss", hasTGSSCertificate(certificate) ? "tgss" : "");
		Hidden sepeHidden = new Hidden("sepe", hasSEPECertificate(certificate) ? "sepe" : "");
		Hidden aeatHidden = new Hidden("aeat", hasAEATCertificate(certificate) ? "aeat" : "");
		Hidden ownerHidden = new Hidden("owner", certificate.getOwner().name());
		String certificateFor = "-";
		if(!certificate.getCertificateInfo().isEmpty()) {
			certificateFor = (AonStringUtils.isBlank(certificate.getCertificateInfo().getDocument()) ? "" : "(" + certificate.getCertificateInfo().getDocument() + ") ") + certificate.getCertificateInfo().getName() + " " + certificate.getCertificateInfo().getSurname();
		}

		Label certificateForL = new Label(parseStringLenght(certificateFor));
		certificateForL.setTitle(certificateFor);

		String representation = "-";
		if(!certificate.getCertificateInfo().isEmpty())
			representation = AonStringUtils.isBlank(certificate.getCertificateInfo().getEnterprise()) ? "PERSONA F\u00cdSICA" : 
				(AonStringUtils.isBlank(certificate.getCertificateInfo().getCif()) ? "" : "(" + certificate.getCertificateInfo().getCif() + ") ") + certificate.getCertificateInfo().getEnterprise();
		Label representationL = new Label(parseStringLenght(representation));
		representationL.setTitle(representation);
		
		String expirationDate = null == certificate.getCertificateInfo().getToDate() ? "" : formatFullDate.format(certificate.getCertificateInfo().getToDate());
		Label expirationDateL = new Label(expirationDate);
		
		TextBox alias = new TextBox();
		alias.setStyleName("aon-inputText");
		alias.setValue(certificate.getDescription());
		alias.addValueChangeHandler(e -> fileNameHidden.setValue(e.getValue()));
		
		String securityTitle = certificate.getConfidential() != null && certificate.getConfidential().equals(CertificateSecurity.PRIVATE) ? "Privado: S\u00f3lo visible para usuarios de la empresa" : "P\u00fablico: Visible para todos los usuarios";
		String securityIcon = certificate.getConfidential() != null && certificate.getConfidential().equals(CertificateSecurity.PRIVATE) ? AON.CSS.aonIconLock() : AON.CSS.aonIconUnLock();
		AonTableButton security = new AonTableButton(securityTitle, securityIcon);
		security.addClickHandler(e -> {
			Boolean oldValue = isActiveToggleButton(security);
			Boolean value = !oldValue;
			getEnableDisableButton(security, value);
			certificate.setConfidential(Boolean.TRUE.equals(value) ? CertificateSecurity.PRIVATE : CertificateSecurity.PUBLIC);
			securityHidden.setValue(Boolean.TRUE.equals(value) ? "private" : "public");
		});
		
		CertificateOwner owner = tabLayoutPanel.getSelectedIndex() == 0 ? CertificateOwner.USER : CertificateOwner.ENTERPRISE;
		
		CheckBox tgssCB = new CheckBox();
		tgssCB.addStyleName(style.checkBox());
		tgssCB.setValue(hasTGSSCertificate(certificate));
		tgssCB.addValueChangeHandler(e -> {
			if(Boolean.TRUE.equals(e.getValue())) {
				if(this.mainDigitalCertificatesObject.hasOtherHasType(CertificateType.TGSS, owner)) {
					showWarning("Tipo certificado", "El tipo de certificado " + CertificateType.TGSS.name() + " ya existe");
					tgssCB.setValue(false);
				} else {
					certificate.addTag(CertificateType.TGSS);
					tgssHidden.setValue("tgss");
				}
			} else {
				certificate.removeTag(CertificateType.TGSS);
				tgssHidden.setValue("");
			}
		});
		
		CheckBox sepeCB = new CheckBox();
		sepeCB.addStyleName(style.checkBox());
		sepeCB.setValue(hasSEPECertificate(certificate));
		sepeCB.addValueChangeHandler(e -> {
			if(Boolean.TRUE.equals(e.getValue())) {
				if(this.mainDigitalCertificatesObject.hasOtherHasType(CertificateType.SEPE, owner)) {
					showWarning("Tipo certificado", "El tipo de certificado " + CertificateType.SEPE.name() + " ya existe");
					sepeCB.setValue(false);
				} else{
					certificate.addTag(CertificateType.SEPE);
					sepeHidden.setValue("sepe");
				}
			} else {
				certificate.removeTag(CertificateType.SEPE);
				sepeHidden.setValue("");
			}
		});
		
		CheckBox aeatCB = new CheckBox();
		aeatCB.addStyleName(style.checkBox());
		aeatCB.setValue(hasAEATCertificate(certificate));
		aeatCB.addValueChangeHandler(e -> {
			if(Boolean.TRUE.equals(e.getValue())) {
				if(this.mainDigitalCertificatesObject.hasOtherHasType(CertificateType.AEAT, owner)) {
					showWarning("Tipo certificado", "El tipo de certificado " + CertificateType.AEAT.name() + " ya existe");
					aeatCB.setValue(false);
				} else {
					certificate.addTag(CertificateType.AEAT);
					aeatHidden.setValue("aeat");
				}
			} else {
				certificate.removeTag(CertificateType.AEAT);
				aeatHidden.setValue("");
			}
		});
		
		// Buttons Panel
		HTMLPanel buttonsPanel = new HTMLPanel("");
		buttonsPanel.addStyleName(style.flex());
		
		saveButton.addClickHandler(e -> {
			if(hasTGSSCertificate(certificate) || hasSEPECertificate(certificate) || hasAEATCertificate(certificate)) {
				saveButton.setEnabled(false);
				AonMessagePanel.showLoading(messagePanel, "Guardando certificado digital...");
				formPanel.submit();
			} else 
				showError("Certitficado", "Debe seleccionar un tipo de certificado para poder guardarlo");
		});
		
		AonTableButton verifyButton = new AonTableButton("Verificar Certificado", AON.CSS.aonIconVerify());
		verifyButton.addClickHandler(e ->  {
			showLoading("Validando certificado SEPE...");
			hideSecondaryUsers();
			ArrayList<CertificateType> tags = new ArrayList<CertificateType>();
			tags.add(CertificateType.SEPE);
			mainDigitalCertificatesObject.verifyCertificate(
				certificate.getId(), 
				tags, 
				s -> showSuccess("Certificado", "Certificado validado correctamente"), 
				f -> showWarning("Error verificaci\u00F3n", f.getMessage())
			);
		});
		verifyButton.setVisible(false);
		
		AonTableButton secondaryUsersButton = new AonTableButton("Usuarios Secundarios", AON.CSS.aonIconList());
		secondaryUsersButton.addClickHandler(e -> onSecondaryUser(certificate.getId()));
		
		secondaryUsersButton.setVisible(false);
		
		AonTableButton deleteButton = new AonTableButton("Borrar", AON.CSS.aonIconDelete());
		deleteButton.addClickHandler(e -> deleteCertificate(certificate));
		
		// Para descargar un certificado descomentar lineas 660, 661, 705. Y cambiar el path del metodo downloadCertificate(...)
		AonTableButton downloadButton = new AonTableButton("Borrar", AON.CSS.aonIconDownload());
		downloadButton.addClickHandler(e -> downloadCertificate(certificate));
		
		AonTableButton checkCertificateButton = new AonTableButton("Informaci\u00F3n", AON.CSS.aonIconInfo());
		checkCertificateButton.addClickHandler(e -> getCertificateInfo(certificate));
		
		// Buttons visibility
		if(Boolean.FALSE.equals(certificate.hasCertificate())) {
			verifyButton.setVisible(false);
			secondaryUsersButton.setVisible(false);
			checkCertificateButton.setVisible(false);
		} else {
			if(hasTGSSCertificate(certificate))
				secondaryUsersButton.setVisible(true);
			if(hasSEPECertificate(certificate))
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
		
		formPanel.add(flowPanel);
		
		buttonsPanel.add(formPanel);
		buttonsPanel.add(saveButton);
		buttonsPanel.add(verifyButton);
		buttonsPanel.add(secondaryUsersButton);
		buttonsPanel.add(checkCertificateButton);
		buttonsPanel.add(deleteButton);
		buttonsPanel.add(downloadButton);
		
		table.setWidget(row, 0, certificateForL);
		table.setWidget(row, 1, representationL);
		table.setWidget(row, 2, expirationDateL);
		table.getCellFormatter().getElement(row, 2).getStyle().setTextAlign(TextAlign.CENTER);
		table.setWidget(row, 3, alias);
		table.getCellFormatter().getElement(row, 3).getStyle().setTextAlign(TextAlign.CENTER);
		if(tabLayoutPanel.getSelectedIndex() == 0) {
			table.setWidget(row, 4, tgssCB);
			table.getCellFormatter().getElement(row, 4).getStyle().setTextAlign(TextAlign.CENTER);
			table.setWidget(row, 5, sepeCB);
			table.getCellFormatter().getElement(row, 5).getStyle().setTextAlign(TextAlign.CENTER);
			table.setWidget(row, 6, aeatCB);
			table.getCellFormatter().getElement(row, 6).getStyle().setTextAlign(TextAlign.CENTER);
			table.setWidget(row, 7, buttonsPanel);
			table.getCellFormatter().getElement(row, 7).getStyle().setTextAlign(TextAlign.RIGHT);
		} else {
			table.setWidget(row, 4, security);
			table.getCellFormatter().getElement(row, 4).getStyle().setTextAlign(TextAlign.CENTER);
			table.setWidget(row, 5, tgssCB);
			table.getCellFormatter().getElement(row, 5).getStyle().setTextAlign(TextAlign.CENTER);
			table.setWidget(row, 6, sepeCB);
			table.getCellFormatter().getElement(row, 6).getStyle().setTextAlign(TextAlign.CENTER);
			table.setWidget(row, 7, aeatCB);
			table.getCellFormatter().getElement(row, 7).getStyle().setTextAlign(TextAlign.CENTER);
			table.setWidget(row, 8, buttonsPanel);
			table.getCellFormatter().getElement(row, 8).getStyle().setTextAlign(TextAlign.RIGHT);
		}
		
		// Para poder visualizar certificados publicos del padre pero con edicion restringida
		if(Boolean.TRUE.equals(isEnterprise) && (certificate.getDomain() != null && !certificate.getDomain().equals(domainId))) {
			alias.setEnabled(false);
			alias.setTitle("Certificado p\u00fablico del dominio padre");
			security.setEnabled(false);
			tgssCB.setEnabled(false);
			tgssCB.setTitle("Certificado p\u00fablico del dominio padre");
			sepeCB.setEnabled(false);
			sepeCB.setTitle("Certificado p\u00fablico del dominio padre");
			aeatCB.setEnabled(false);
			aeatCB.setTitle("Certificado p\u00fablico del dominio padre");
			
			saveButton.setVisible(false);
			verifyButton.setVisible(false);
			secondaryUsersButton.setVisible(false);
			deleteButton.setVisible(false);
		}
	}
	
	private String parseStringLenght(String input) {
		return (AonStringUtils.isBlank(input) || input.length() < 35) ? input : AonStringUtils.substring(input, 0, 32) + "...";
	}

	private void getCertificateInfo(Certificate certificate) {
		mainDigitalCertificatesObject.getCertificateInfo(
				certificate.getId(), 
				certificateInfo -> {
					hideSecondaryUsers();
					String certificateInfoStr = certificateInfo.toString();
					certificateInfoStr += "<br>Validez desde : " + formatFullDate.format(certificateInfo.getFromDate()) + " hasta : " + formatFullDate.format(certificateInfo.getToDate());
					AonDialog dialog = new AonDialog("Informaci\u00F3n Certificado", new HTML(certificateInfoStr));
					dialog.info();
				}, 
				f -> showWarning("Error verificaci\u00F3n", f.getMessage()));
	}

	private void onSecondaryUser(Integer rattachId) {
		CertificateOwner owner = tabLayoutPanel.getSelectedIndex() == 0 ? CertificateOwner.USER : CertificateOwner.ENTERPRISE;
		
		if(CertificateOwner.USER.equals(owner)) {
			initUserSecondaryTable();
			userSecondayUsersPanel.setVisible(true);
		} else {
			initEnterpriseSecondaryTable();
			enterpriseSecondayUsersPanel.setVisible(true);
		}
		
		ArrayList<CertificateType> tags = new ArrayList<CertificateType>();
		tags.add(CertificateType.TGSS);
		
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
						
						showWarning("Error usuarios secundarios", f.getMessage());
					}
				);
			
			}, failure -> {
				if(CertificateOwner.USER.equals(owner))
					userSecondayUsersPanel.setVisible(false);
				else
					enterpriseSecondayUsersPanel.setVisible(false);
				
				showWarning("Error verificaci\u00F3n", failure.getMessage());
			}
		);	
	}

	private void loadDigitalCertificates() {
		updateLoadingPanelStatus();
		this.mainDigitalCertificatesObject.getCertificates(
				s -> {
					Integer index = tabLayoutPanel.getSelectedIndex();
					if(index == 0) 
						createUserCertDataTable();
					else if(index == 1)
						createEntepriseCertDataTable();
					updateTabTitle();
				}, 
				f -> {});
	}

	private void updateTabTitle() {
		tabLayoutPanel.setTabText(0, "Personales (" + this.mainDigitalCertificatesObject.getUserCertificateList().size() + ")");
		tabLayoutPanel.setTabText(1, "Compartidos (" + this.mainDigitalCertificatesObject.getEnterpriseCertificateList().size() + ")");
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

	// ------------------------------------------------------ Delete Certificate Methods

	private void deleteCertificate(Certificate certificate) {
		AonDialog deleteDialog = new AonDialog("Eliminar certificado", new HTML("\u00BFDesea eliminar este certificado\u003F"));
		deleteDialog.confirm(new AonAcceptDialogCallback() {
			
			@Override
			public void onCancel() {
				// Nothing to do here
			}
			
			@Override
			public void onAccept() {
				mainDigitalCertificatesObject.deleteCertificate(
						certificate, 
						s -> loadDigitalCertificates(), 
						f -> {});
			}
		});
	}
	
	// ------------------------------------------------------ Donwload Certificate Method
	
	private void downloadCertificate(Certificate certificate) {
		String filePath = "/Users/svaldepenas/Desktop/certificate.p12";
		mainDigitalCertificatesObject.downloadCertificate(
				certificate.getId(), 
				filePath,
				s -> showSuccess("Descarga", "Certificado descargado en la ruta " + filePath), 
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
						showSuccess("AVISO: Creado", "El usuario secundario ha sido creado correctamente.");
						
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
								showSuccess("AVISO: Borrado", "El usuario secundario ha sido borrado correctamente.");
								
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
		dataTable.getCellFormatter().getElement(row, 1).getStyle().setTextAlign(TextAlign.CENTER);
		dataTable.setWidget(row, 2, statusL);
		dataTable.getCellFormatter().getElement(row, 2).getStyle().setTextAlign(TextAlign.CENTER);
		dataTable.setWidget(row, 3, dateL);
		dataTable.getCellFormatter().getElement(row, 3).getStyle().setTextAlign(TextAlign.CENTER);
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
				showSuccess("AVISO: Creado", "El usuario secundario ha sido creado correctamente.");
				
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
	
	private void hideSecondaryUsers() {
		Integer index = tabLayoutPanel.getSelectedIndex();
		if(index == 0)
			userSecondayUsersPanel.setVisible(false);
		else if(index == 1)
			enterpriseSecondayUsersPanel.setVisible(false);
	}
	
	private Button getEnableDisableButton() {
		Button showInactiveUserBtn = new Button();
		showInactiveUserBtn.setStyleName(!showInactives ? AON.AON_ICON_DISABLE : AON.AON_ICON_ENABLE );
		showInactiveUserBtn.setStyleName(AON.AON_NO_MARGIN, true);
		showInactiveUserBtn.setStyleName(AON.AON_EDIT_DATA_TABLE_BUTTON, true);
		return showInactiveUserBtn;
	}

	// ------------------------------------------------------ Toolbar
	
	private void initToolbar() {
		this.toolbar = new AonToolbar("Gesti\u00F3n de certificados");
		
		AonToolbarButton newCertificateBtn = new AonToolbarButton("Nuevo certificado", AON.CSS.aonIconAdd());
		newCertificateBtn.addClickHandler(e -> createNewCertificate());
		toolbar.add(newCertificateBtn);
	}
	
	private void createNewCertificate() {
		new CertificateDialog() {
			
			@Override
			public void onAccept() {
				loadDigitalCertificates();
			}
		};
	}

	// ------------------------------------------------- Aon Messages panel

	private void showSuccess(String title, String message) {
		Map<String, String> successMap = new HashMap<>();
		successMap.put(title, message);
		AonMessagePanel.showSuccess(messagePanel, successMap);
	}
	
	private void showWarning(String title, String message) {
		Map<String, String> warningMap = new HashMap<>();
		warningMap.put(title, message);
		AonMessagePanel.showWarning(messagePanel, warningMap);
	}
	
	private void showError(String title, String message) {
		Map<String, String> errorMap = new HashMap<>();
		errorMap.put(title, message);
		AonMessagePanel.showError(messagePanel, errorMap);
	}
	
	private void showLoading(String message) {
		AonMessagePanel.showLoading(messagePanel, message);
	}
	
	// ------------------------------------------------- SecurityButton
	
	private void getEnableDisableButton(Button button, boolean disabled) {
		button.removeStyleName(disabled ? AON.CSS.aonIconUnLock() : AON.CSS.aonIconLock());
		button.addStyleName(!disabled ? AON.CSS.aonIconUnLock() : AON.CSS.aonIconLock() );
		button.setTitle(!disabled ? "P\u00fablico: Visible para todos los usuarios" : "Privado: S\u00f3lo visible para usuarios de la empresa");
	}
	
	private boolean isActiveToggleButton(Button button) {
		return AonStringUtils.containsIgnoreCase(button.getStyleName(), AON.CSS.aonIconLock());
	}
	
}
