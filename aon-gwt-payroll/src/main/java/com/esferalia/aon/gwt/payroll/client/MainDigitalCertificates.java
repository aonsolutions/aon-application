package com.esferalia.aon.gwt.payroll.client;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.css.AonGwtTemplateResources;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonConfirmDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonConfirmDialog.AonConfirmDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTableButton;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbar;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.gwt.payroll.shared.DigitalCertificate;
import com.esferalia.aon.gwt.payroll.shared.DigitalCertificate.CertificateType;
import com.esferalia.aon.gwt.payroll.shared.EnterpriseStatus;
import com.esferalia.aon.gwt.payroll.shared.SecondaryUserCertificate;
import com.esferalia.aon.gwt.payroll.shared.SistemaREDService.JsSistemaREDResults;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArray;
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
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
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
	}
	
	@UiField
	DockLayoutPanel dockLayoutPanel;
	
	@UiField
	Grid digitalCertificatesDataTableHeader;
	
	@UiField
	ScrollPanel scrollPanel;
	
	@UiField
	Grid digitalCertificatesDataTable;
	
	@UiField
	VerticalPanel secondayUsersPanel;
	
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
	
	// ------------------------------------------------------ Variables
	
	private MainDigitalCertificatesObject mainDigitalCertificatesObject;
	private DateTimeFormat formatFullDate = DateTimeFormat.getFormat("dd/MM/yyyy");
	
	private AonToolbar toolbar;
	private AonToolbarButton accept;
	private AonToolbarButton showSecondaryUsers;
	
	private boolean showInactives = false;
	
	// ------------------------------------------------------ Constructor

	public MainDigitalCertificates() {	
		GWT.<AonGwtTemplateResources>create(AonGwtTemplateResources.class).css().ensureInjected();
		AON.ensureInjected();
	
		Widget ui = binder.createAndBindUi(this);
		RootLayoutPanel.get(getRootPanel() != null ? getRootPanel() : "rootPanel").add(ui);
		
		toolbar = getToolbarPanel();
		toolbar.getElement().getStyle().setHeight(50, Unit.PX);
		dockLayoutPanel.addNorth( toolbar , AonToolbar.HEIGTH );
		
		AonTableButton loadingBtn = new AonTableButton("", AON.CSS.aonIconRenew());
		loadingBtn.addStyleName(style.loadingPanel());
		
		Label loadingL = new Label("Accediendo al sistema RED para consultar los usuarios secundarios...");
		loadingL.getElement().getStyle().setMarginLeft(5, Unit.PX);
		
		loadingPanel.add(loadingBtn);
		loadingPanel.add(loadingL);
		
		secondaryUserDeckPanel.showWidget(0);
	}
	
	// ------------------------------------------------------ onModuleLoad
	
	public void onModuleLoad(MainDigitalCertificatesObject mainDigitalCertificatesObject) {
		this.mainDigitalCertificatesObject = mainDigitalCertificatesObject;
		
		this.mainDigitalCertificatesObject.getDigitalCertificates(
				s -> {
					secondayUsersPanel.setVisible(false);
					
					initPreview();
					insertRows();
				
				}, f -> {});
		
		checkStatus(this.mainDigitalCertificatesObject);
	}
	
	// ------------------------------------------------------ Check Status
	
	private void checkStatus(MainDigitalCertificatesObject mainDigitalCertificatesObject) {
		try {
			mainDigitalCertificatesObject.checkStatus(enterpriseStatus -> {
				SistemaREDResults sistemaREDResults = new SistemaREDResults() {
					
					@Override
					public void up2Date() {}
	
					@Override
					public void run() {}
					
					@Override
					protected void newAffiliated(JsSistemaREDResults jsSaltraResults) {}
					
					@Override
					protected void newAffiliated(JsArray<JsSistemaREDResults> jsSaltraResults ) {}
	
					@Override
					protected void newAffiliated(JsArray<JsSistemaREDResults> jsResults, int total ) {}
					
					@Override
					protected void saltraCredentialsFound() {}
				};
	
				enterpriseStatus.visit(sistemaREDResults);
				EnterpriseStatus.ifSistemaREDEnabled(enterpriseStatus, () -> {
					MainDigitalCertificates.this.setSistemaREDVisible(true);
				}, () -> {
					MainDigitalCertificates.this.setSistemaREDVisible(false);
				});
	
			}, throwable -> {
				MainDigitalCertificates.this.setSistemaREDVisible(false);
			});
		} catch (Exception e) {
			MainDigitalCertificates.this.setSistemaREDVisible(false);
		}
	}

	private void setSistemaREDVisible(boolean visible) {
		this.showSecondaryUsers.setVisible(visible);
	}

	// ------------------------------------------------------ Init Preview (Tables)
	
	private void initPreview() {
		digitalCertificatesDataTableHeader.clear();
		digitalCertificatesDataTableHeader.resize(0, 0);
		digitalCertificatesDataTable.clear();
		digitalCertificatesDataTable.resize(0, 0);
		digitalCertificatesDataTableHeader.resizeColumns(4);
		digitalCertificatesDataTable.resizeColumns(4);
		
		secondaryUserDataTableHeader.clear();
		secondaryUserDataTableHeader.resize(0, 0);
		secondaryUserDataTable.clear();
		secondaryUserDataTable.resize(0, 0);
		secondaryUserDataTableHeader.resizeColumns(5);
		secondaryUserDataTable.resizeColumns(5);
		
		paintHeader();
		paintHeaderSecondaryUser();
		calculateScrollPanelHeight();
		setColumnWidth();
	}
	
	private void paintHeader() {
		int row = digitalCertificatesDataTableHeader.insertRow(digitalCertificatesDataTableHeader.getRowCount());
		
		Label type = new Label("TIPO");
		AonTableButton confidential = new AonTableButton("Confidencial", AON.CSS.aonIconLock());
		Label password = new Label("CONTRASE" + String.valueOf("\u00D1") + "A");
		Label certificate = new Label("CERTIFICADO");
		
		type.addStyleName(style.headerStyle());
		password.addStyleName(style.headerStyle());
		certificate.addStyleName(style.headerStyle());
		
		digitalCertificatesDataTableHeader.setWidget(row, 0, type);
		digitalCertificatesDataTableHeader.setWidget(row, 1, confidential);
		digitalCertificatesDataTableHeader.setWidget(row, 2, password);
		digitalCertificatesDataTableHeader.setWidget(row, 3, certificate);
	}
	
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
	
	private void calculateScrollPanelHeight() {
		//Integer clientHeight = Window.getClientHeight();
		//scrollPanel.setHeight((clientHeight - 900) + "px");
		//secondaryUserScrollPanel.setHeight((clientHeight - 900) + "px");
	}
	
	private void setColumnWidth() {
		//MaxWidth 950px
		digitalCertificatesDataTableHeader.getCellFormatter().getElement(0, 0).getStyle().setWidth(200, Unit.PX);
		digitalCertificatesDataTableHeader.getCellFormatter().getElement(0, 1).getStyle().setWidth(50, Unit.PX);
		digitalCertificatesDataTableHeader.getCellFormatter().getElement(0, 2).getStyle().setWidth(175, Unit.PX);
		digitalCertificatesDataTableHeader.getCellFormatter().getElement(0, 3).getStyle().setWidth(375, Unit.PX);
		
		digitalCertificatesDataTable.getColumnFormatter().getElement(0).getStyle().setWidth(200, Unit.PX);
		digitalCertificatesDataTable.getColumnFormatter().getElement(1).getStyle().setWidth(50, Unit.PX);
		digitalCertificatesDataTable.getColumnFormatter().getElement(2).getStyle().setWidth(175, Unit.PX);
		digitalCertificatesDataTable.getColumnFormatter().getElement(3).getStyle().setWidth(375, Unit.PX);
		
		secondaryUserDataTableHeader.getCellFormatter().getElement(0, 0).getStyle().setWidth(350, Unit.PX);
		secondaryUserDataTableHeader.getCellFormatter().getElement(0, 1).getStyle().setWidth(200, Unit.PX);
		secondaryUserDataTableHeader.getCellFormatter().getElement(0, 2).getStyle().setWidth(200, Unit.PX);
		secondaryUserDataTableHeader.getCellFormatter().getElement(0, 3).getStyle().setWidth(150, Unit.PX);
		secondaryUserDataTableHeader.getCellFormatter().getElement(0, 4).getStyle().setWidth(50, Unit.PX);
		
		secondaryUserDataTable.getColumnFormatter().getElement(0).getStyle().setWidth(350, Unit.PX);
		secondaryUserDataTable.getColumnFormatter().getElement(1).getStyle().setWidth(200, Unit.PX);
		secondaryUserDataTable.getColumnFormatter().getElement(2).getStyle().setWidth(200, Unit.PX);
		secondaryUserDataTable.getColumnFormatter().getElement(3).getStyle().setWidth(150, Unit.PX);
		secondaryUserDataTable.getColumnFormatter().getElement(4).getStyle().setWidth(50, Unit.PX);
	}
	
	// ------------------------------------------------------ Insert Rows
	
	private void insertRows() {
		for(DigitalCertificate digitalCertificate :mainDigitalCertificatesObject.getDigitalCertificateList()) {
			if(digitalCertificate.getType() == CertificateType.SEPE)
				insertSEPECertificateRow(digitalCertificate);
			if(digitalCertificate.getType() == CertificateType.TGSS)
				insertTGSSCertificateRow(digitalCertificate);
		}
	}

	// ------------------------------------------------------ Insert Rows (SEPE)
	
	private void insertSEPECertificateRow(DigitalCertificate digitalCertificate) {
		// Insert new row
		int row = digitalCertificatesDataTable.insertRow(digitalCertificatesDataTable.getRowCount());
		
		// Type Label
		Label typeL = new Label("Certificado Empresa (SEPE)");
		
		// Confidential CheckBox
		CheckBox confidentialCB = new CheckBox();
		confidentialCB.addValueChangeHandler((e) -> {
			digitalCertificate.setConfidential(e.getValue());
		});
		confidentialCB.setValue(digitalCertificate.getConfidential());
		
		// Password TextBox
		HorizontalPanel hPanel = new HorizontalPanel();
		
		PasswordTextBox passwordTB = new PasswordTextBox();
		passwordTB.addValueChangeHandler(e -> {
			digitalCertificate.setPassword(e.getValue());
		});
		passwordTB.setValue(digitalCertificate.getPassword());
		hPanel.add(passwordTB);
		
		if(!digitalCertificate.getHasCertificate()) {
			AonTableButton showPassBtn = createShowPassButton(passwordTB);
			hPanel.add(showPassBtn);
		}
		
		// Upload & Download FormPanel
		HorizontalPanel hFormPanel = new HorizontalPanel();
		
		Widget formPanel = createFormPanel(digitalCertificate);
		
		hFormPanel.add(formPanel);
		
		if(digitalCertificate.getHasCertificate()) {
			
			AonTableButton deleteCertificate = new AonTableButton("Eliminar Cert", AON.CSS.aonIconDelete());
			deleteCertificate.getElement().getStyle().setMarginTop(5, Unit.PX);
			deleteCertificate.addClickHandler(e -> {
				mainDigitalCertificatesObject.deleteDigitalCertificate(CertificateType.SEPE,
	    				s -> {
	    					reloadView();
	    				}, f -> {});
			});
			
			hFormPanel.add(deleteCertificate);
		}
		
		//Add to table
		digitalCertificatesDataTable.setWidget(row, 0, typeL);
		digitalCertificatesDataTable.setWidget(row, 1, confidentialCB);
		digitalCertificatesDataTable.setWidget(row, 2, hPanel);
		digitalCertificatesDataTable.setWidget(row, 3, hFormPanel);
	}
	
	// ------------------------------------------------------ Insert Rows (TGSS)
	
	private void insertTGSSCertificateRow(DigitalCertificate digitalCertificate) {
		// Insert new row
		int row = digitalCertificatesDataTable.insertRow(digitalCertificatesDataTable.getRowCount());
		
		// Type Label
		Label typeL = new Label("Certificado Personal (TGSS)");
		
		// Confidential CheckBox
		CheckBox confidentialCB = new CheckBox();
		confidentialCB.addValueChangeHandler((e) -> {
			digitalCertificate.setConfidential(e.getValue());
		});
		confidentialCB.setValue(digitalCertificate.getConfidential());
		
		// Password TextBox
		HorizontalPanel hPanel = new HorizontalPanel();
		
		PasswordTextBox passwordTB = new PasswordTextBox();
		passwordTB.addValueChangeHandler(e -> {
			digitalCertificate.setPassword(e.getValue());
		});
		passwordTB.setValue(digitalCertificate.getPassword());
		hPanel.add(passwordTB);
		
		if(!digitalCertificate.getHasCertificate()) {
			AonTableButton showPassBtn = createShowPassButton(passwordTB);
			hPanel.add(showPassBtn);
		}
		
		// Upload & Download FormPanel
		HorizontalPanel hFormPanel = new HorizontalPanel();
		Widget formPanel = createFormPanel(digitalCertificate);
		hFormPanel.add(formPanel);
		
		if(digitalCertificate.getHasCertificate()) {
			
			setSistemaREDVisible(true);
			
			AonTableButton deleteCertificate = new AonTableButton("Eliminar Cert", AON.CSS.aonIconDelete());
			deleteCertificate.getElement().getStyle().setMarginTop(5, Unit.PX);
			deleteCertificate.addClickHandler(e -> {
				mainDigitalCertificatesObject.deleteDigitalCertificate(CertificateType.TGSS,
	    				s -> {
	    					reloadView();
	    					setSistemaREDVisible(false);
	    				}, f -> {});
			});
			
			hFormPanel.add(deleteCertificate);
		}
		
		//Add to table
		digitalCertificatesDataTable.setWidget(row, 0, typeL);
		digitalCertificatesDataTable.setWidget(row, 1, confidentialCB);
		digitalCertificatesDataTable.setWidget(row, 2, hPanel);
		digitalCertificatesDataTable.setWidget(row, 3, hFormPanel);
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

	private Widget createFormPanel(DigitalCertificate digitalCertificate) {
		
		CertificateType certificateType = digitalCertificate.getType();
		String certificateTypeStr = certificateType == CertificateType.SEPE ? "0" : "1";
		
		HorizontalPanel mainFlowPanel = new HorizontalPanel();
		mainFlowPanel.getElement().getStyle().setPaddingLeft(10, Unit.PX);
		mainFlowPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		mainFlowPanel.setWidth("350px");
		
		TextBox fileNameTB = new TextBox();
		fileNameTB.getElement().getStyle().setWidth(305, Unit.PX);
		String description = "No existe certficado";
		if(digitalCertificate.getHasCertificate())
			description = AonStringUtils.isBlank(digitalCertificate.getDescription()) ? "Certficado sin nombre" : digitalCertificate.getDescription();
		fileNameTB.setValue(description);
		
		if(mainDigitalCertificatesObject.hasMoraThanOneSEPECertificates() && certificateType == CertificateType.SEPE)
			fileNameTB.setEnabled(false);
		
		fileNameTB.addValueChangeHandler(e -> {
			digitalCertificate.setDescription(e.getValue());
		});
		
		//Create formPanel to UploadFiles
		FlowPanel flowPanel = new FlowPanel();
		
		FormPanel formPanel = new FormPanel();
		formPanel.setAction(GWT.getModuleBaseURL()+ "certificate/");
		formPanel.setEncoding(FormPanel.ENCODING_MULTIPART);
		formPanel.setMethod(FormPanel.METHOD_POST);
		
		Hidden rattachId = new Hidden("rattachId", digitalCertificate.getRattachId()+"");
		Hidden raddinfoId = new Hidden("raddinfoId", digitalCertificate.getRaddinfoId().toString()+"");
		Hidden extension = new Hidden("extension", "");
		Hidden fileName = new Hidden("filename", "");
		Hidden certificateTypeH = new Hidden("certificatetype", certificateTypeStr);
		Hidden userLogin = new Hidden("currentUser", Wnd.getCurrentUser());
		Hidden currentDomain = new Hidden("currentDomain", Wnd.getCurrentDomainNameURL());
		Hidden token = new Hidden("token", Wnd.getToken());
		
		FileUpload fileU = new FileUpload();
		fileU.setName("uploader");
		fileU.getElement().setPropertyString("multiple", "multiple");
		fileU.getElement().setPropertyString("accept", ".p12");
		fileU.getElement().getStyle().setDisplay(Display.NONE);
		
		if(mainDigitalCertificatesObject.hasMoraThanOneSEPECertificates() && certificateType == CertificateType.SEPE)
			fileU.setEnabled(false);
		
		fileU.addChangeHandler((e) -> {
			String filename = getFileName(fileU.getFilename().toString());
			String fileExt = getFileExtension(fileU.getFilename());

            if(filename.length() == 0) {
            	 Window.alert("Cant upload file - Try again : ");
            } else {
            	extension.setValue(fileExt);
            	fileName.setValue(filename);
            	fileNameTB.setValue(filename);
            	mainDigitalCertificatesObject.setDigitalCertificates(s -> {
            		formPanel.submit();
        		}, f -> {});
            }
		});
		
		formPanel.addSubmitCompleteHandler((e) -> {
	        if(e.getResults().length() == 0) {
                Window.alert("Something went wrong - Try again");
            } else {
            	mainDigitalCertificatesObject.getDigitalCertificates(
        				s -> {
        					reloadView();
        				}, f -> {});
            }
	    });
		
		AonTableButton fileButton = new AonTableButton("Subir Cert", AON.CSS.aonIconAttach());
		fileButton.addClickHandler(e -> {
			fileU.click();
		});
		
		flowPanel.add(rattachId);
		flowPanel.add(raddinfoId);
		flowPanel.add(extension);
		flowPanel.add(fileName);
		flowPanel.add(certificateTypeH);
		flowPanel.add(userLogin);
		flowPanel.add(currentDomain);
		flowPanel.add(token);
		flowPanel.add(fileButton);
		flowPanel.add(fileU);
		
		formPanel.add(flowPanel);
			
		mainFlowPanel.add(fileNameTB);
		
		if(!digitalCertificate.getHasCertificate())
			mainFlowPanel.add(formPanel);
		
		return mainFlowPanel;
	}
	
	// ------------------------------------------------------ Insert Secondary Users
	
	private void insertSecondaryUsersRows() {
		secondaryUserDataTable.clear();
		secondaryUserDataTable.resize(0, 0);
		secondaryUserDataTable.resizeColumns(5);
		setColumnWidth();
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
	
	private void reloadView() {
		mainDigitalCertificatesObject.getDigitalCertificates(
				t -> {
					secondaryUserDeckPanel.showWidget(0);
					
					initPreview();
					insertRows();
					
					checkStatus(this.mainDigitalCertificatesObject);
				}, f -> {});
	}
	
	// ------------------------------------------------------ Toolbar
	
	private AonToolbar getToolbarPanel() {
		
		AonToolbar toolbar = new AonToolbar("Gesti" + String.valueOf("\u00F3") + "n de Autorizados");
		
		accept = new AonToolbarButton( AON.MSG.saveAction(), AON.CSS.aonIconSave() );
		accept.setAccessKey('G');
		accept.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				onAccept(event);
			}
		});
		toolbar.add(accept);

		showSecondaryUsers = new AonToolbarButton( "Segundos Autorizados", AON.CSS.aonIconList() );
		showSecondaryUsers.setAccessKey('G');
		showSecondaryUsers.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				onShowSecondaryUsers(event);
			}
		});
		toolbar.add(showSecondaryUsers);

		return toolbar;

	}
	
	// ------------------------------------------------------ Toolbar.Methods
	
	private void onAccept(ClickEvent event) {
		mainDigitalCertificatesObject.setDigitalCertificates(s -> {
			mainDigitalCertificatesObject.getDigitalCertificates(
					t -> {
						reloadView();
					}, f -> {});
		}, f -> {});
	}
	
	private void onShowSecondaryUsers(ClickEvent event) {
		secondayUsersPanel.setVisible(true);
		mainDigitalCertificatesObject.getSecondaryUsers(t -> {
			this.showSecondaryUsers.setVisible(false);
			insertSecondaryUsersRows();
		}, e -> {
			secondayUsersPanel.setVisible(false);
		});
	}
	
}
