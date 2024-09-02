package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.css.AonGwtTemplateResources;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDockLayout;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomTable;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog.AonAcceptDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessagePanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTableButton;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
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
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

import net.aonsolutions.gwt.pdfjs.client.FullViewer;

public class MainCertificates extends MainEntryPoint{

	// ------------------------------------------------------ UiBinder
	
	interface Binder extends UiBinder<Widget, MainCertificates> {}

	private static final Binder binder = GWT.create(Binder.class);
	
	// ------------------------------------------------------ UiFields
	
	@UiField
	MyStyle style;

	interface MyStyle extends CssResource {
		String flex();
		String checkBox();
	}
	
	@UiField
	DeckPanel deckPanel;
	
	@UiField(provided = true)
	AonCustomDockLayout dockLayoutPanel;
	
	@UiField(provided = true)
	AonCustomDockLayout pdfDockLayoutPanel;
	
	private HTMLPanel container;
	private HTMLPanel messagePanel = new HTMLPanel("");
	
	private TabLayoutPanel tabLayoutPanel;
	
	private AonCustomTable userCertTable;
	private AonCustomTable enterpriseCertTable;
	
	private FullViewer fullViewer;
	
	private static enum COLS {
		  DES("Titular"						,"-moz-available")
		, BUD("Representaci\u00f3n"			,"60rem")
		, DOC("F. Expiraci\u00f3n"			,"30rem")
		, TYP(AON.MSG.alias()				,"45rem")
		, ACT(AonStringUtils.EMPTY			,"10rem")
		, TGS("TGSS"						,"10rem")
		, SEP("SEPE"						,"10rem")
		, AEA("AEAT"						,"10rem")
		, BUT(AonStringUtils.EMPTY			,"40rem")
		;

		String headerLabel;
		String colWidth;

		private COLS(String headerLabel,String colWidth) {
			this.headerLabel = headerLabel;
			this.colWidth = colWidth;
		}
		public String getColWidth() {
			return colWidth;
		}
		public String getHeaderLabel() {
			return headerLabel;
		}
	}
	
	// ------------------------------------------------------ Variables
	
	private MainCertificatesObject mainDigitalCertificatesObject;
	private DateTimeFormat formatFullDate = DateTimeFormat.getFormat("dd/MM/yyyy");
	
	private Integer domainId = null;
	
	// ------------------------------------------------------ Constructor

	public MainCertificates() {	
		GWT.<AonGwtTemplateResources>create(AonGwtTemplateResources.class).css().ensureInjected();
		AON.ensureInjected();
	
		this.dockLayoutPanel = new AonCustomDockLayout("Certificados Digitales");
		this.pdfDockLayoutPanel = new AonCustomDockLayout("Usuarios Secundarios");
		
		Widget ui = binder.createAndBindUi(this);
		RootLayoutPanel.get(getRootPanel() != null ? getRootPanel() : "rootPanel").add(ui);
		
		// dockLayoutPanel
		
		container = new HTMLPanel("");
		container.addStyleName(AON.CSS.aonFlexColumn());
		
		container.add(messagePanel);
	
		tabLayoutPanel = new TabLayoutPanel(3.00, Unit.EM);
		tabLayoutPanel.setHeight("100%");
		
		container.add(tabLayoutPanel);
		
		this.dockLayoutPanel.add(container);
		
		addButtonsToolbar();
		
		initUserCertDataTable();
		initEnterpriseCertDataTable();
		
		initTabLayoutPanel();
		
		// pdfDockLayoutPanel
		
		fullViewer = new FullViewer();
		this.pdfDockLayoutPanel.add(fullViewer);
		
		addPDFButtonsToolbar();
		
		deckPanel.showWidget(0);
	}
	
	// ------------------------------------------------------ Toolbar
	
	private void addButtonsToolbar() {
		AonToolbarButton newButton = new AonToolbarButton( "Nuevo certificado", AON.CSS.aonIconAdd());
		newButton.addClickHandler(e -> createNewCertificate());
		
		this.dockLayoutPanel.addToolbarButton(newButton);
		this.dockLayoutPanel.hideSearchWidget();
		this.dockLayoutPanel.hideFilterWidget();
	}
	
	private void addPDFButtonsToolbar() {
		AonToolbarButton backButton = new AonToolbarButton( "Volver", AON.CSS.aonIconBack());
		backButton.addClickHandler(e -> deckPanel.showWidget(0));
		
		this.pdfDockLayoutPanel.addToolbarButton(backButton);
		this.pdfDockLayoutPanel.hideSearchWidget();
		this.pdfDockLayoutPanel.hideFilterWidget();
	}
	
	private void createNewCertificate() {
		new CertificateDialog() {
			
			@Override
			public void onAccept() {
				loadDigitalCertificates();
			}
		};
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

	// ------------------------------------------------------ Init Preview (Tables TGSS & SEPE)
	
	private void initUserCertDataTable() {
		userCertTable = new AonCustomTable();
		userCertTable.setMaxHeight((Window.getClientHeight() - Window.getClientHeight()/3) + "px");
		ScrollPanel scrollPanel = new ScrollPanel(userCertTable);
		
		paintUserHeader();
		tabLayoutPanel.add(scrollPanel);
	}
	
	private void paintUserHeader() {
		userCertTable.createHeader();
		for ( COLS col : COLS.values()) 
			userCertTable.addHeader(new Label(col.getHeaderLabel()), col.getColWidth());
	}

	private void initEnterpriseCertDataTable() {
		enterpriseCertTable = new AonCustomTable();
		enterpriseCertTable.ensureDebugId("enterpriseCertTable");
		enterpriseCertTable.setMaxHeight((Window.getClientHeight() - Window.getClientHeight()/3) + "px");
		ScrollPanel scrollPanel = new ScrollPanel(enterpriseCertTable);
		
		paintEnterpriseHeader();
		tabLayoutPanel.add(scrollPanel);
	}
	
	private void paintEnterpriseHeader() {
		enterpriseCertTable.createHeader();
		for ( COLS col : COLS.values()) 
			enterpriseCertTable.addHeader(new Label(col.getHeaderLabel()), col.getColWidth());
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
	

	private void loadDigitalCertificates() {
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
	
	// ------------------------------------------------------ Create certificate tables

	private void createUserCertDataTable() {
		removeTableRows(userCertTable);
		paintUserHeader();
		createUserCertDataTableRows();
	}

	private void createEntepriseCertDataTable() {
		removeTableRows(enterpriseCertTable);
		paintEnterpriseHeader();
		createEnterpriseCertDataTableRows();
	}
	
	private void removeTableRows(AonCustomTable table) {
		int rows = table.getRowsCount();
		while(rows >= 0) {
			table.remove(rows);
			rows--;
		}
	}

	private void createUserCertDataTableRows() {
		List<Certificate> userCertificateList = mainDigitalCertificatesObject.getUserCertificateList();
		if(userCertificateList.isEmpty()) {
			paintNoDataRow(userCertTable);
		} else
			for(Certificate certificate : userCertificateList)
				paintRow(userCertTable, certificate, false);
			
	}

	private void createEnterpriseCertDataTableRows() {
		List<Certificate> enterpriseCertificateList = mainDigitalCertificatesObject.getEnterpriseCertificateList();
		if(enterpriseCertificateList.isEmpty()) {
			paintNoDataRow(enterpriseCertTable);
		} else 
			for(Certificate certificate : enterpriseCertificateList)
				paintRow(enterpriseCertTable, certificate, true);
	}
	

	
	private void paintNoDataRow(AonCustomTable table) {
		HTMLPanel row = table.createRow();
		Label noData = new Label("No existen certificados");
		noData.getElement().getStyle().setTextAlign(TextAlign.CENTER);
		table.addRow(row, noData, "100%");
	}
	
	private void paintRow(AonCustomTable table, Certificate certificate, boolean isEnterprise) {
		HTMLPanel row = table.createRow();
		createFormCells(table, row, certificate, false);
	}
	
	private void createFormCells(AonCustomTable table, HTMLPanel row, Certificate certificate, boolean isEnterprise) {
		// Save Button
		AonTableButton saveButton = new AonTableButton("Guardar", AON.CSS.aonIconSave());
		saveButton.addStyleName(AON.CSS.aonCustomRowButtom());
		
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
		textEllipsis(certificateForL);
		certificateForL.setTitle(certificateFor);

		String representation = "-";
		if(!certificate.getCertificateInfo().isEmpty())
			representation = AonStringUtils.isBlank(certificate.getCertificateInfo().getEnterprise()) ? "PERSONA F\u00cdSICA" : 
				(AonStringUtils.isBlank(certificate.getCertificateInfo().getCif()) ? "" : "(" + certificate.getCertificateInfo().getCif() + ") ") + certificate.getCertificateInfo().getEnterprise();
		Label representationL = new Label(parseStringLenght(representation));
		textEllipsis(representationL);
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
		buttonsPanel.getElement().getStyle().setProperty("justify-content", "right");
		
		saveButton.addClickHandler(e -> {
			if(hasTGSSCertificate(certificate) || hasSEPECertificate(certificate) || hasAEATCertificate(certificate)) {
				saveButton.setEnabled(false);
				AonMessagePanel.showLoading(messagePanel, "Guardando certificado digital...");
				formPanel.submit();
			} else 
				showError("Certitficado", "Debe seleccionar un tipo de certificado para poder guardarlo");
		});
		
		AonTableButton verifyButton = new AonTableButton("Verificar Certificado", AON.CSS.aonIconVerify());
		verifyButton.addStyleName(AON.CSS.aonCustomRowButtom());
		verifyButton.addClickHandler(e ->  {
			showLoading("Validando certificado SEPE...");
			
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
		secondaryUsersButton.addStyleName(AON.CSS.aonCustomRowButtom());
		secondaryUsersButton.addClickHandler(e -> onSecondaryUser(certificate.getId()));
		
		secondaryUsersButton.setVisible(false);
		
		AonTableButton deleteButton = new AonTableButton("Borrar", AON.CSS.aonIconDelete());
		deleteButton.addStyleName(AON.CSS.aonCustomRowButtom());
		deleteButton.addClickHandler(e -> deleteCertificate(certificate));
		
		AonTableButton downloadButton = new AonTableButton("Descargar", AON.CSS.aonIconDownload());
		downloadButton.addStyleName(AON.CSS.aonCustomRowButtom());
		downloadButton.addClickHandler(e -> downloadCertificate(certificate));
		
		AonTableButton checkCertificateButton = new AonTableButton("Informaci\u00F3n", AON.CSS.aonIconInfo());
		checkCertificateButton.addStyleName(AON.CSS.aonCustomRowButtom());
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
		flowPanel.getElement().getStyle().setProperty("justify-content", "right");
				
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
//		buttonsPanel.add(downloadButton);
		
		table.addRow(row, certificateForL, COLS.DES.getColWidth());
		table.addRow(row, representationL, COLS.BUD.getColWidth());
		table.addRow(row, expirationDateL, COLS.DOC.getColWidth());
		table.addRow(row, alias, COLS.TYP.getColWidth());
		table.addRow(row, tabLayoutPanel.getSelectedIndex() == 0 ? new Label() : security, tabLayoutPanel.getSelectedIndex() == 0 ? "10rem" : "3rem");
		table.addRow(row, tgssCB, COLS.TGS.getColWidth());
		table.addRow(row, sepeCB, COLS.SEP.getColWidth());
		table.addRow(row, aeatCB, COLS.AEA.getColWidth());
		table.addRow(row, buttonsPanel, COLS.BUT.getColWidth());
		
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
	
	// ------------------------------------------------------ Insert Rows
	
	private void textEllipsis(Widget widget) {
		widget.getElement().getStyle().setProperty("text-overflow", "ellipsis");
		widget.getElement().getStyle().setProperty("overflow", "hidden");
		widget.getElement().getStyle().setProperty("white-space", "nowrap");
	}
	
	private String parseStringLenght(String input) {
		return (AonStringUtils.isBlank(input) || input.length() < 35) ? input : AonStringUtils.substring(input, 0, 32) + "...";
	}

	private void getCertificateInfo(Certificate certificate) {
		mainDigitalCertificatesObject.getCertificateInfo(
				certificate.getId(), 
				certificateInfo -> {
					String certificateInfoStr = certificateInfo.toString();
					certificateInfoStr += "<br>Validez desde : " + formatFullDate.format(certificateInfo.getFromDate()) + " hasta : " + formatFullDate.format(certificateInfo.getToDate());
					AonDialog dialog = new AonDialog("Informaci\u00F3n Certificado", new HTML(certificateInfoStr));
					dialog.info();
				}, 
				f -> showWarning("Error verificaci\u00F3n", f.getMessage()));
	}

	private void onSecondaryUser(Integer rattachId) {
		ArrayList<CertificateType> tags = new ArrayList<CertificateType>();
		tags.add(CertificateType.TGSS);
		
		showLoading("Verificando certificado sistema RED...");
		
		mainDigitalCertificatesObject.verifyCertificate(
			rattachId, 
			tags,
			success -> {
				showLoading("Accediendo al sistema RED para consultar los usuarios secundarios...");
				
				mainDigitalCertificatesObject.getSecondaryUsersPDF(
					rattachId, 
					dataURI -> {
						deckPanel.showWidget(1);
						fullViewer.open(dataURI);
					}, 
					f -> {
						showWarning("Error usuarios secundarios", f.getMessage());
					}
				);
			
			}, failure -> {
				showWarning("Error verificaci\u00F3n", failure.getMessage());
			}
		);	
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
				showLoading("Eliminando certificado ...");
				mainDigitalCertificatesObject.deleteCertificate(
						certificate, 
						s -> {
							showSuccess("Certificado eliminado", "Certificado eliminado correctamente");
							loadDigitalCertificates();
						}, 
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
