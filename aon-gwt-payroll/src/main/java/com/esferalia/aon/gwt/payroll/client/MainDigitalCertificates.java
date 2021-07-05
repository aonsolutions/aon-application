package com.esferalia.aon.gwt.payroll.client;

import java.util.List;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.css.AonGwtTemplateResources;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonConfirmDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonConfirmDialog.AonConfirmDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTableButton;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbar;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarSmallButton;
import com.esferalia.aon.gwt.payroll.shared.DigitalCertificate;
import com.esferalia.aon.gwt.payroll.shared.DigitalCertificate.CertificateType;
import com.esferalia.aon.gwt.payroll.shared.SecondaryUserCertificate;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class MainDigitalCertificates extends MainEntryPoint{

	// ------------------------------------------------------ UiBinder
	
	interface Binder extends UiBinder<Widget, MainDigitalCertificates> {}

	private static final Binder binder = GWT.create(Binder.class);
	
	// ------------------------------------------------------ UiFields
	
	@UiField
	MyStyle style;

	interface MyStyle extends CssResource {
		String headerStyle();
		String loadingPanel();
		String warningTB();
		String flexGrow();
	}
	
	@UiField
	DockLayoutPanel dockLayoutPanel;
	
	@UiField
	DeckPanel deckPanel;
	
	@UiField
	Grid certificateTGSSDataTableHeader;
	
	@UiField
	ScrollPanel  certificateTGSSScrollPanel;
	
	@UiField
	Grid  certificateTGSSDataTable;
	
	@UiField
	HTMLPanel secondayUsersPanel;
	
	@UiField
	Grid secondaryUserDataTableHeader;
	
	@UiField
	ScrollPanel secondaryUserScrollPanel;
	
	@UiField
	DeckPanel secondaryUserDeckPanel;
	
	@UiField
	HTMLPanel loadingPanel;
	
	@UiField
	Grid secondaryUserDataTable;
	
	@UiField
	HTMLPanel secondaryUserToolbar;
	
	@UiField
	HTMLPanel addSecondaryUserToolbar;
	
	@UiField
	HTMLPanel showSecondaryUserToolbar;
	
	@UiField
	Grid certificateSEPEDataTableHeader;
	
	@UiField
	ScrollPanel  certificateSEPEScrollPanel;
	
	@UiField
	Grid  certificateSEPEDataTable;
	
	// ------------------------------------------------------ Variables
	
	private MainDigitalCertificatesObject mainDigitalCertificatesObject;
	private DateTimeFormat formatFullDate = DateTimeFormat.getFormat("dd/MM/yyyy");
	
	private AonToolbar toolbar;
	private AonToolbarButton certificateTGSS;
	private AonToolbarButton certificateSEPE;
	
	private boolean showInactives = false;
	
	// ------------------------------------------------------ Constructor

	public MainDigitalCertificates() {	
		GWT.<AonGwtTemplateResources>create(AonGwtTemplateResources.class).css().ensureInjected();
		AON.ensureInjected();
	
		Widget ui = binder.createAndBindUi(this);
		RootLayoutPanel.get(getRootPanel() != null ? getRootPanel() : "rootPanel").add(ui);
		
		initToolbar();
		
		initTGSSPreview();
		initSEPEPreview();
		
		initSecondaryTable();
		
		deckPanel.showWidget(0);
	}

	// ------------------------------------------------------ Constructor.Methods
	
	private void initToolbar() {
		toolbar = getToolbarPanel();
		toolbar.getElement().getStyle().setHeight(50, Unit.PX);
		dockLayoutPanel.addNorth( toolbar , AonToolbar.HEIGTH );
	}

	private void initSecondaryTable() {
		secondayUsersPanel.setVisible(false);
		
		secondaryUserDeckPanel.showWidget(0);
		
		AonTableButton loadingBtn = new AonTableButton("", AON.CSS.aonIconRenew());
		loadingBtn.addStyleName(style.loadingPanel());
		
		Label loadingL = new Label("Verificando certificado sistema RED...");
		loadingL.getElement().getStyle().setMarginLeft(5, Unit.PX);
		
		loadingPanel.add(loadingBtn);
		loadingPanel.add(loadingL);
	}
	
	// ------------------------------------------------------ onModuleLoad
	
	public void onModuleLoad(MainDigitalCertificatesObject mainDigitalCertificatesObject) {
		this.mainDigitalCertificatesObject = mainDigitalCertificatesObject;
		this.mainDigitalCertificatesObject.getEnterpriseId(s -> {
			showTGSSCertificate();
		}, f -> {});
	}

	// ------------------------------------------------------ Init Preview (Tables TGSS & SEPE)
	
	private void initTGSSPreview() {
		initPreview(certificateTGSSDataTableHeader, certificateTGSSDataTable);
		
		secondaryUserDataTableHeader.clear();
		secondaryUserDataTableHeader.resize(0, 0);
		secondaryUserDataTable.clear();
		secondaryUserDataTable.resize(0, 0);
		secondaryUserDataTableHeader.resizeColumns(5);
		secondaryUserDataTable.resizeColumns(5);
		
		paintHeaderSecondaryUser();
		setSecondaryUserColumnWidth();
	}

	private void initSEPEPreview() {
		initPreview(certificateSEPEDataTableHeader, certificateSEPEDataTable);
	}
	
	private void initPreview(Grid dataTableHeader, Grid dataTable) {
		dataTableHeader.clear();
		dataTableHeader.resize(0, 0);
		dataTableHeader.resizeColumns(2);
		
		dataTable.clear();
		dataTable.resize(0, 0);
		dataTable.resizeColumns(2);
		
		paintHeader(dataTableHeader);
		setColumnWidth(dataTableHeader, dataTable);
	}
	
	private void paintHeader(Grid dataTableHeader) {
		int row = dataTableHeader.insertRow(dataTableHeader.getRowCount());
		
		Label type = new Label("TIPO");
		HorizontalPanel hPanel = new HorizontalPanel();
		Label password = new Label("CONTRASE" + String.valueOf("\u00D1") + "A");
		Label certificate = new Label("CERTIFICADO");
		Label buttons = new Label("");
		
		type.addStyleName(style.headerStyle());
		password.addStyleName(style.headerStyle());
		certificate.addStyleName(style.headerStyle());
		
		password.setWidth("175px");
		certificate.setWidth("375px");
		buttons.setWidth("50px");
		
		hPanel.add(password);
		hPanel.add(certificate);
		hPanel.add(buttons);
		
		dataTableHeader.setWidget(row, 0, type);
		dataTableHeader.setWidget(row, 1, hPanel);
		
	}
	
	private void setColumnWidth(Grid dataTableHeader, Grid dataTable) {
		//MaxWidth 950px
		dataTableHeader.getCellFormatter().getElement(0, 0).getStyle().setWidth(200, Unit.PX);
		dataTableHeader.getCellFormatter().getElement(0, 1).getStyle().setWidth(600, Unit.PX);
		
		dataTable.getColumnFormatter().getElement(0).getStyle().setWidth(190, Unit.PX);
		dataTable.getColumnFormatter().getElement(1).getStyle().setWidth(610, Unit.PX);
	}
	
	// ------------------------------------------------------ Init Preview (Secondary Users)
	
	private void paintHeaderSecondaryUser() {
		int row = secondaryUserDataTableHeader.insertRow(secondaryUserDataTableHeader.getRowCount());
		
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
		
		secondaryUserDataTableHeader.setWidget(row, 0, name);
		secondaryUserDataTableHeader.setWidget(row, 1, naf);
		secondaryUserDataTableHeader.setWidget(row, 2, status);
		secondaryUserDataTableHeader.setWidget(row, 3, date);
		secondaryUserDataTableHeader.setWidget(row, 4, action);
	}
	
	private void setSecondaryUserColumnWidth() {
		secondaryUserDataTableHeader.getCellFormatter().getElement(0, 0).getStyle().setWidth(300, Unit.PX);
		secondaryUserDataTableHeader.getCellFormatter().getElement(0, 1).getStyle().setWidth(125, Unit.PX);
		secondaryUserDataTableHeader.getCellFormatter().getElement(0, 2).getStyle().setWidth(125, Unit.PX);
		secondaryUserDataTableHeader.getCellFormatter().getElement(0, 3).getStyle().setWidth(125, Unit.PX);
		secondaryUserDataTableHeader.getCellFormatter().getElement(0, 4).getStyle().setWidth(50, Unit.PX);
		
		secondaryUserDataTable.getColumnFormatter().getElement(0).getStyle().setWidth(300, Unit.PX);
		secondaryUserDataTable.getColumnFormatter().getElement(1).getStyle().setWidth(125, Unit.PX);
		secondaryUserDataTable.getColumnFormatter().getElement(2).getStyle().setWidth(125, Unit.PX);
		secondaryUserDataTable.getColumnFormatter().getElement(3).getStyle().setWidth(125, Unit.PX);
		secondaryUserDataTable.getColumnFormatter().getElement(4).getStyle().setWidth(50, Unit.PX);
	}
	
	// ------------------------------------------------------ Insert TGSS Row
	
	private void insertTGSSRow() {
		DigitalCertificate digitalTGSSCertificate = mainDigitalCertificatesObject.getDigitalCertificateTGSS();
		certificateTGSSDataTable.clear();
		certificateTGSSDataTable.resize(0, 0);
		certificateTGSSDataTable.resizeColumns(2);
		
		insertCertificateRow(digitalTGSSCertificate, CertificateType.TGSS, certificateTGSSDataTable);
	}
	
	// ------------------------------------------------------ Insert SEPE Rows
	
	private void insertSEPERows() {
		List<DigitalCertificate> digitalSEPECertificates = mainDigitalCertificatesObject.getDigitalCertificateSEPEList();
		certificateSEPEDataTable.clear();
		certificateSEPEDataTable.resize(0, 0);
		certificateSEPEDataTable.resizeColumns(2);
		
		if(digitalSEPECertificates.isEmpty())
			insertCertificateRow(null, CertificateType.SEPE, certificateSEPEDataTable);
		else
			for(DigitalCertificate digitalSEPECertificate : digitalSEPECertificates)
				insertCertificateRow(digitalSEPECertificate, CertificateType.SEPE, certificateSEPEDataTable);
	}
	
	// ------------------------------------------------------ Insert Rows
	
	private void insertCertificateRow(DigitalCertificate digitalCertificate, CertificateType certificateType, Grid dataTable) {
		// Insert new row
		int row = dataTable.insertRow(dataTable.getRowCount());
		
		// Type Label
		Label certificateTypeL = new Label(certificateType == CertificateType.TGSS ? "Certificado Usuario (TGSS)" : "Certificado Empresa (SEPE)");
		
		// Form Panel
		Widget formPanel = createFormPanel(digitalCertificate, certificateType);
		
		//Add to table
		dataTable.setWidget(row, 0, certificateTypeL);
		dataTable.setWidget(row, 1, formPanel);
	}

	private Widget createFormPanel(DigitalCertificate digitalCertificate, CertificateType certificateType) {
		
		// Create Form Panel
		FormPanel formPanel = new FormPanel();
		formPanel.setAction(GWT.getModuleBaseURL()+ "certificate/");
		formPanel.setEncoding(FormPanel.ENCODING_MULTIPART);
		formPanel.setMethod(FormPanel.METHOD_POST);
		
		Hidden rattachIdHidden = new Hidden("rattachId", "");
		Hidden raddinfoIdHidden = new Hidden("raddinfoId", "");
		Hidden extensionHidden = new Hidden("extension", "");
		Hidden fileNameHidden = new Hidden("filename", "");
		Hidden certificateTypeHidden = new Hidden("certificateType", "");
		Hidden passwordHidden = new Hidden("password", "");
		Hidden userLoginHidden = new Hidden("currentUser", Wnd.getCurrentUser());
		Hidden currentDomainHidden = new Hidden("currentDomain", Wnd.getCurrentDomainNameURL());
		Hidden tokenHidden = new Hidden("token", Wnd.getToken());
		
		// MainFlowPanel
		HorizontalPanel mainFlowPanel = new HorizontalPanel();
		mainFlowPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		mainFlowPanel.setWidth("600px");
		
		// CertificateType
		String certificateTypeStr = certificateType == CertificateType.SEPE ? "0" : "1";
		certificateTypeHidden.setValue(certificateTypeStr);
		
		// Save Button
		AonTableButton saveButton = new AonTableButton("Guardar", AON.CSS.aonIconSave());
		
		// Password Panel
		HorizontalPanel passwordPanel = new HorizontalPanel();
		
		PasswordTextBox passwordTB = new PasswordTextBox();
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
		
		if(null == digitalCertificate) {
			AonTableButton showPassBtn = createShowPassButton(passwordTB);
			passwordPanel.add(showPassBtn);
		} else if(!digitalCertificate.getHasCertificate()) {
			AonTableButton showPassBtn = createShowPassButton(passwordTB);
			passwordPanel.add(showPassBtn);
		} else if(certificateType == CertificateType.SEPE){
			passwordTB.setEnabled(false);
		}
		
		// File Panel
		TextBox fileNameTB = new TextBox();
		fileNameTB.getElement().getStyle().setWidth(305, Unit.PX);
		fileNameTB.addValueChangeHandler(e -> {
			fileNameHidden.setValue(e.getValue());
		});
		
		FileUpload fileU = new FileUpload();
		fileU.setName("uploader");
		fileU.getElement().setPropertyString("multiple", "multiple");
		fileU.getElement().setPropertyString("accept", ".p12");
		fileU.getElement().getStyle().setDisplay(Display.NONE);
		
		fileU.addChangeHandler((e) -> {
			String filename = getFileName(fileU.getFilename().toString());
			String fileExt = getFileExtension(fileU.getFilename());

            if(filename.length() == 0) {
            	 Window.alert("Cant upload file - Try again");
            } else {
            	extensionHidden.setValue(fileExt);
            	fileNameHidden.setValue(filename);
            	fileNameTB.setValue(filename);
            }
		});
		
		formPanel.addSubmitCompleteHandler((e) -> {
	        if(certificateType == CertificateType.TGSS)
        		showTGSSCertificate();
        	else
        		showSEPECertificates();
	    });
		
		AonTableButton fileButton = new AonTableButton("Subir Cert", AON.CSS.aonIconAttach());
		fileButton.addClickHandler(e -> {
			fileU.click();
		});
		
		// Buttons Panel
		HorizontalPanel buttonsPanel = new HorizontalPanel();
		
		saveButton.addClickHandler(e -> {
			formPanel.submit();
		});
		
		AonTableButton verifyButton = new AonTableButton("Verificar Certificado", AON.CSS.aonIconVerify());
		verifyButton.addClickHandler(e -> {
			mainDigitalCertificatesObject.verifyCertificate(certificateType, s -> {
				AonDialog dialog = new AonDialog("Certificado", new HTML("Certificado validado correctamente"));
				dialog.warning();
			}, f -> {
				AonDialog dialog = new AonDialog("Error", new HTML(f.getMessage()));
				dialog.warning();
			});
		});
		verifyButton.setVisible(false);
		
		AonTableButton secondaryUsersButton = new AonTableButton("Usuarios Secundarios", AON.CSS.aonIconList());
		secondaryUsersButton.addClickHandler(e -> {
			secondayUsersPanel.setVisible(true);
			
			mainDigitalCertificatesObject.verifyCertificate(CertificateType.TGSS, success -> {
				
				Label loadingL = (Label) loadingPanel.getWidget(loadingPanel.getWidgetCount()-1);
				loadingL.setText("Accediendo al sistema RED para consultar los usuarios secundarios...");
				
				mainDigitalCertificatesObject.getSecondaryUsers(s -> {
					insertSecondaryUsersRows();
				}, f -> {
					secondayUsersPanel.setVisible(false);
					AonDialog dialog = new AonDialog("Error", new HTML(f.getMessage()));
					dialog.warning();
				});
				
			}, failure -> {
				secondayUsersPanel.setVisible(false);
				AonDialog dialog = new AonDialog("Error", new HTML(failure.getMessage()));
				dialog.warning();
			});
			
		});
		secondaryUsersButton.setVisible(false);
		
		AonTableButton deleteButton = new AonTableButton("Borrar", AON.CSS.aonIconDelete());
		deleteButton.addClickHandler(e -> {
			deleteCertificate(digitalCertificate);
		});
		
		// Buttons visibility
		if(null == digitalCertificate || !digitalCertificate.getHasCertificate()) {
			fileButton.setVisible(true);
			deleteButton.setVisible(false);
			verifyButton.setVisible(false);
			secondaryUsersButton.setVisible(false);
		} else {
			fileButton.setVisible(false);
			deleteButton.setVisible(true);
			if(certificateType == CertificateType.TGSS)
				secondaryUsersButton.setVisible(true);
			else
				verifyButton.setVisible(true);
		}
		
		buttonsPanel.add(fileButton);
		buttonsPanel.add(saveButton);
		buttonsPanel.add(verifyButton);
		buttonsPanel.add(secondaryUsersButton);
		buttonsPanel.add(deleteButton);
		
		//Add all to FlowPanel to add to FormPanel
		FlowPanel flowPanel = new FlowPanel();
				
		flowPanel.add(rattachIdHidden);
		flowPanel.add(raddinfoIdHidden);
		flowPanel.add(extensionHidden);
		flowPanel.add(fileNameHidden);
		flowPanel.add(certificateTypeHidden);
		flowPanel.add(passwordHidden);
		flowPanel.add(userLoginHidden);
		flowPanel.add(currentDomainHidden);
		flowPanel.add(tokenHidden);
		flowPanel.add(fileU);
		flowPanel.add(buttonsPanel);
		
		formPanel.add(flowPanel);
		
		mainFlowPanel.add(passwordPanel);
		mainFlowPanel.add(fileNameTB);
		mainFlowPanel.add(formPanel);
			
		// Fill fields
		if(null != digitalCertificate) {
			rattachIdHidden.setValue(digitalCertificate.getRattachId()+"");
			raddinfoIdHidden.setValue(digitalCertificate.getRaddinfoId()+"");
			
			String password = digitalCertificate.getPassword();
			
			if(AonStringUtils.isBlank(password))
				passwordTB.setEnabled(true);
			
			passwordTB.setValue(digitalCertificate.getPassword());
			passwordHidden.setValue(passwordTB.getValue());
			
			String description = "No existe certficado";
			if(digitalCertificate.getHasCertificate())
				description = AonStringUtils.isBlank(digitalCertificate.getDescription()) ? "Certficado sin nombre" : digitalCertificate.getDescription();
			fileNameTB.setValue(description);
			fileNameHidden.setValue(description);
		}
		
		// Check row whene more than one SEPE certificate
		if(mainDigitalCertificatesObject.hasMoraThanOneSEPECertificates() && certificateType == CertificateType.SEPE)
			fileNameTB.setEnabled(false);
		
		
		return mainFlowPanel;
	}

	// ------------------------------------------------------ Delete Certificate Methods

	private void deleteCertificate(DigitalCertificate digitalCertificate) {
		this.mainDigitalCertificatesObject.deleteDigitalCertificate(digitalCertificate, s -> {
			if(digitalCertificate.getType() == CertificateType.TGSS)
				showTGSSCertificate();
			else
				showSEPECertificates();
		}, f -> {});
	}

	// ------------------------------------------------------ Insert Rows.Auxiliar Methods
	
	private AonTableButton createShowPassButton(PasswordTextBox passwordTB) {
		AonTableButton showPassBtn = new AonTableButton("Mostrar", AON.CSS.aonIconShowPass());
		showPassBtn.addClickHandler(e -> {
			String type = passwordTB.getElement().getAttribute("type");
			if(AonStringUtils.isBlank(type) || (type != "text" || !type.equals("text")))
				passwordTB.getElement().setAttribute("type", "text");
			else
				passwordTB.getElement().setAttribute("type", "password");
		});
		
		showPassBtn.getElement().getStyle().setMarginLeft(5, Unit.PX);
		showPassBtn.getElement().getStyle().setMarginTop(4, Unit.PX);
		
		return showPassBtn;
	}
	
	// ------------------------------------------------------ Insert Secondary Users
	
	private void insertSecondaryUsersRows() {
		secondaryUserDataTable.clear();
		secondaryUserDataTable.resize(0, 0);
		secondaryUserDataTable.resizeColumns(5);
		setSecondaryUserColumnWidth();
		initSeconaryUserToolBar();
		
		if(mainDigitalCertificatesObject.getSecondaryUsers(showInactives).isEmpty())
			secondaryUserDeckPanel.showWidget(1);
		else {
			secondaryUserDeckPanel.showWidget(2);
			for(SecondaryUserCertificate secondaryUser : mainDigitalCertificatesObject.getSecondaryUsers(showInactives)) {
				fillSecondaryUserRow(secondaryUser);
			}
		}
	}
	
	private void fillSecondaryUserRow(SecondaryUserCertificate secondaryUser) {
		// Insert new row
		int row = secondaryUserDataTable.insertRow(secondaryUserDataTable.getRowCount());
		
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
		
		comunicateBtn.addClickHandler(e -> {
			if(AonStringUtils.equalsIgnoreCase(secondaryUser.getSituation(), "Baja")) {
				SecondaryUserDialog dialog = new SecondaryUserDialog(secondaryUser.getNaf()) {
					
					@Override
					protected void onAccept() {
						AonConfirmDialog dialog = new AonConfirmDialog();
						dialog.info("AVISO: Creado", "El usuario secundario ha sido creado correctamente.");
						
						mainDigitalCertificatesObject.getSecondaryUsers(t -> {
							insertSecondaryUsersRows();
						}, e -> {});
					}
				};
				dialog.center();
				dialog.show();
			} else {
				// Delete User Dialog Confirm
				AonConfirmDialog confirmDialog = new AonConfirmDialog();
				confirmDialog.confirm(
						"BORRADO", 
						String.valueOf("\u00BF") + "Realmente desea eliminar a este usuario?",
						new AonConfirmDialogCallback() {

							@Override
							public void onAccept() {
								mainDigitalCertificatesObject.deleteSecondaryUser(secondaryUser, s -> {
									AonConfirmDialog dialog = new AonConfirmDialog();
									dialog.info("AVISO: Borrado", "El usuario secundario ha sido borrado correctamente.");
									
									mainDigitalCertificatesObject.getSecondaryUsers(t -> {
										insertSecondaryUsersRows();
									}, e -> {});
								}, f -> {});
							}

							@Override
							public void onCancel() {
								// TODO Auto-generated method stub
							}});
			}
			
		});
		
		//Add to table
		secondaryUserDataTable.setWidget(row, 0, nameL);
		secondaryUserDataTable.setWidget(row, 1, nafL);
		secondaryUserDataTable.setWidget(row, 2, statusL);
		secondaryUserDataTable.setWidget(row, 3, dateL);
		secondaryUserDataTable.setWidget(row, 4, comunicateBtn);
		secondaryUserDataTable.getCellFormatter().getElement(row, 4).getStyle().setTextAlign(TextAlign.CENTER);
	}
	
	// ------------------------------------------------------ Insert Secondary Users.Toolbar
	
	private void initSeconaryUserToolBar() {
		addSecondaryUserToolbar.clear();
		showSecondaryUserToolbar.clear();
		
		AonTableButton addSecondaryUser = new AonTableButton("Nuevo Usuario Secundario",  AON.CSS.aonIconAdd());
		addSecondaryUser.addClickHandler(e -> {
			onAddSecondaryUser(e);
		});
		
		Label addSecondaryUserL = new Label("A" + String.valueOf("\u00F1") + "adir Autorizado");
		
		addSecondaryUserToolbar.add(addSecondaryUser);
		addSecondaryUserToolbar.add(addSecondaryUserL);
		
		Button showInactiveUserBtn = getEnableDisableButton();
		showInactiveUserBtn.addClickHandler(e -> {
			showInactives = !showInactives;
			insertSecondaryUsersRows();
		});
		
		Label inactiveL = new Label("Ver inactivos");
		
		showSecondaryUserToolbar.add(inactiveL);
		showSecondaryUserToolbar.add(showInactiveUserBtn);	
	}
	
	// ------------------------------------------------------ Insert Secondary Users.Toolbar Methods
	
	private void onAddSecondaryUser(ClickEvent event) {
		SecondaryUserDialog dialog = new SecondaryUserDialog() {
			
			@Override
			protected void onAccept() {
				AonConfirmDialog dialog = new AonConfirmDialog();
				dialog.info("AVISO: Creado", "El usuario secundario ha sido creado correctamente.");
				
				mainDigitalCertificatesObject.getSecondaryUsers(t -> {
					insertSecondaryUsersRows();
				}, e -> {});
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
	
	private AonToolbar getToolbarPanel() {
		
		AonToolbar toolbar = new AonToolbar("Gesti" + String.valueOf("\u00F3") + "n de Autorizados");
		
		certificateTGSS = new AonToolbarButton("Cert. TGSS", AON.CSS.aonIconTgss() );
		certificateTGSS.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				showTGSSCertificate();
			}
		});
		toolbar.add(certificateTGSS);

		certificateSEPE = new AonToolbarButton("Cert. SEPE", AON.CSS.aonIconSepe());
		certificateSEPE.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				showSEPECertificates();
			}
		});
		toolbar.add(certificateSEPE);
		
		return toolbar;
	}
	
	// ------------------------------------------------------ Toolbar.Methods
	
	private void showTGSSCertificate() {
		this.mainDigitalCertificatesObject.getDigitalCertificateTGSS(
				s -> {
					deckPanel.showWidget(0);
					insertTGSSRow();
				
				}, f -> {});
	}
	
	private void showSEPECertificates() {
		this.mainDigitalCertificatesObject.getDigitalCertificatesSEPE(
				s -> {
					deckPanel.showWidget(1);
					insertSEPERows();
				}, f -> {});
	}
	
	private void addWarningIcon(HorizontalPanel panel, Widget widget, String message) {
		message = AonStringUtils.isBlank(message) ? "Este campo es obligatorio" : message;
		panel.add(new AonToolbarSmallButton(message, AON.CSS.aonIconWarning()));
		widget.addStyleName(style.warningTB());
		widget.addStyleName(style.flexGrow());
	}
	
	private boolean hasWarningIcon(HorizontalPanel panel) {
		Widget widget = panel.getWidget(panel.getWidgetCount()-1);
		return widget instanceof AonToolbarSmallButton;
	}
	
	private void removeWarningIcon(HorizontalPanel panel, Widget widget) {
		if(panel.getWidgetCount() > 2)
			panel.remove(panel.getWidgetCount() - 1);
		
		widget.removeStyleName(style.warningTB());
	}
	
}
