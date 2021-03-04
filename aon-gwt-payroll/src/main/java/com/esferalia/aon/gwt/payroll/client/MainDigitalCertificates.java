package com.esferalia.aon.gwt.payroll.client;

import java.util.List;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.css.AonGwtTemplateResources;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonConfirmDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonConfirmDialog.AonConfirmDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTableButton;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbar;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.gwt.common.shared.StringUtils;
import com.esferalia.aon.gwt.payroll.shared.DigitalCertificate;
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

	// -------------------------------------------------- UiBinder --------------------------------------------------
	
	interface Binder extends UiBinder<Widget, MainDigitalCertificates> {}

	private static final Binder binder = GWT.create(Binder.class);
	
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
	
	// -------------------------------------------- Variables de la clase---------------------------------------------
	
	private MainDigitalCertificatesObject mainDigitalCertificatesObject;
	private DateTimeFormat formatFullDate = DateTimeFormat.getFormat("dd/MM/yyyy");
	
	private AonToolbar toolbar;
	private AonToolbarButton accept;
	private AonToolbarButton showSecondaryUsers;
	
	private boolean showInactives = false;
	
	// ------------------------------------------------- CONSTRUCTOR --------------------------------------------------

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
	
	// -------------------------------------------------- UiHandlers --------------------------------------------------

	// ----------------------------------------------- METODOS DE LA CLASE ------------------------------------------------

	public void onModuleLoad(MainDigitalCertificatesObject mainDigitalCertificatesObject) {
		this.mainDigitalCertificatesObject = mainDigitalCertificatesObject;
		secondayUsersPanel.setVisible(false);
		this.mainDigitalCertificatesObject.getDigitalCertificates(
				s -> {
					this.accept.setVisible(false);
					initPreview();
					insertRows();
					fillCertificatesRows(s);
				}, f -> {});
		
		checkStatus(this.mainDigitalCertificatesObject);
	}
	
	private void checkStatus(MainDigitalCertificatesObject mainDigitalCertificatesObject) {
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
	}

	private void setSistemaREDVisible(boolean visible) {
		this.showSecondaryUsers.setVisible(visible);
	}

	private void initPreview() {
		digitalCertificatesDataTableHeader.clear();
		digitalCertificatesDataTableHeader.resize(0, 0);
		digitalCertificatesDataTable.clear();
		digitalCertificatesDataTable.resize(0, 0);
		digitalCertificatesDataTableHeader.resizeColumns(5);
		digitalCertificatesDataTable.resizeColumns(5);
		
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
		Label date = new Label("F. ACTUALIZACI" + String.valueOf("\u00D3") + "N");
		
		type.addStyleName(style.headerStyle());
		password.addStyleName(style.headerStyle());
		certificate.addStyleName(style.headerStyle());
		date.addStyleName(style.headerStyle());
		
		digitalCertificatesDataTableHeader.setWidget(row, 0, type);
		digitalCertificatesDataTableHeader.setWidget(row, 1, confidential);
		digitalCertificatesDataTableHeader.setWidget(row, 2, password);
		digitalCertificatesDataTableHeader.setWidget(row, 3, certificate);
		digitalCertificatesDataTableHeader.setWidget(row, 4, date);
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
		Integer clientHeight = Window.getClientHeight();
		scrollPanel.setHeight((clientHeight - 900) + "px");
		secondaryUserScrollPanel.setHeight((clientHeight - 900) + "px");
	}
	
	private void setColumnWidth() {
		//MaxWidth 950px
		digitalCertificatesDataTableHeader.getCellFormatter().getElement(0, 0).getStyle().setWidth(200, Unit.PX);
		digitalCertificatesDataTableHeader.getCellFormatter().getElement(0, 1).getStyle().setWidth(50, Unit.PX);
		digitalCertificatesDataTableHeader.getCellFormatter().getElement(0, 2).getStyle().setWidth(175, Unit.PX);
		digitalCertificatesDataTableHeader.getCellFormatter().getElement(0, 3).getStyle().setWidth(375, Unit.PX);
		digitalCertificatesDataTableHeader.getCellFormatter().getElement(0, 4).getStyle().setWidth(150, Unit.PX);
		digitalCertificatesDataTableHeader.getCellFormatter().getElement(0, 4).getStyle().setTextAlign(TextAlign.CENTER);
		
		digitalCertificatesDataTable.getColumnFormatter().getElement(0).getStyle().setWidth(200, Unit.PX);
		digitalCertificatesDataTable.getColumnFormatter().getElement(1).getStyle().setWidth(50, Unit.PX);
		digitalCertificatesDataTable.getColumnFormatter().getElement(2).getStyle().setWidth(175, Unit.PX);
		digitalCertificatesDataTable.getColumnFormatter().getElement(3).getStyle().setWidth(375, Unit.PX);
		digitalCertificatesDataTable.getColumnFormatter().getElement(4).getStyle().setWidth(150, Unit.PX);
		digitalCertificatesDataTable.getColumnFormatter().getElement(4).getStyle().setTextAlign(TextAlign.CENTER);
		
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
	
	private void insertRows() {
		if(!mainDigitalCertificatesObject.hasMoraThanOneSEPECertificates())
			insertSEPECertificateRow();
		insertTGSSCertificateRow();
	}

	private void insertSEPECertificateRow() {
		// Insert new row
		int row = digitalCertificatesDataTable.insertRow(digitalCertificatesDataTable.getRowCount());
		
		// Type Label
		Label typeL = new Label("Certificado Empresa (SEPE)");
		
		// Confidential CheckBox
		CheckBox confidentialCB = new CheckBox();
		confidentialCB.addValueChangeHandler((e) -> {
			mainDigitalCertificatesObject.setConfidential((byte)0, e.getValue());
			this.accept.setVisible(true);
		});
		
		// Password TextBox
		HorizontalPanel hPanel = new HorizontalPanel();
		
		PasswordTextBox passwordTB = new PasswordTextBox();
		passwordTB.addValueChangeHandler(e -> {
			mainDigitalCertificatesObject.setPassword((byte)0, e.getValue());
			this.accept.setVisible(true);
		});
		hPanel.add(passwordTB);
		
		if(!mainDigitalCertificatesObject.hasData(Byte.parseByte("0"))) {
			AonTableButton showPassBtn = new AonTableButton("Mostrar", AON.CSS.aonIconShowPass());
			showPassBtn.addClickHandler(e -> {
				String type = passwordTB.getElement().getAttribute("type");
				if(StringUtils.isBlank(type) || (type != "text" || !type.equals("text")))
					passwordTB.getElement().setAttribute("type", "text");
				else
					passwordTB.getElement().setAttribute("type", "password");
			});
			
			showPassBtn.getElement().getStyle().setMarginLeft(5, Unit.PX);
			showPassBtn.getElement().getStyle().setMarginTop(4, Unit.PX);
			hPanel.add(showPassBtn);
		}
		
		// Upload & Download FormPanel
		HorizontalPanel hFormPanel = new HorizontalPanel();
		
		Widget formPanel = createFormPanel("0");
		hFormPanel.add(formPanel);
		
		if(mainDigitalCertificatesObject.hasData(Byte.parseByte("0"))) {
			AonTableButton deleteCertificate = new AonTableButton("Eliminar Cert", AON.CSS.aonIconDelete());
			deleteCertificate.getElement().getStyle().setMarginTop(5, Unit.PX);
			deleteCertificate.addClickHandler(e -> {
				mainDigitalCertificatesObject.deleteDigitalCertificate("0",
	    				s -> {
	    					this.accept.setVisible(false);
	    					initPreview();
	    					insertRows();
	    					fillCertificatesRows(mainDigitalCertificatesObject.getDigitalCertificateList());
	    				}, f -> {});
			});
			
			
			hFormPanel.add(deleteCertificate);
		}
		
		
		// Attach DateBoxEx
		Label dateL = new Label();
		dateL.getElement().getStyle().setTextAlign(TextAlign.CENTER);
		
		//Add to table
		digitalCertificatesDataTable.setWidget(row, 0, typeL);
		digitalCertificatesDataTable.setWidget(row, 1, confidentialCB);
		digitalCertificatesDataTable.setWidget(row, 2, hPanel);
		digitalCertificatesDataTable.setWidget(row, 3, hFormPanel);
		digitalCertificatesDataTable.setWidget(row, 4, dateL);
	}
	
	private void insertTGSSCertificateRow() {
		// Insert new row
		int row = digitalCertificatesDataTable.insertRow(digitalCertificatesDataTable.getRowCount());
		
		// Type Label
		Label typeL = new Label("Certificado Personal (TGSS)");
		
		// Confidential CheckBox
		CheckBox confidentialCB = new CheckBox();
		confidentialCB.addValueChangeHandler((e) -> {
			mainDigitalCertificatesObject.setConfidential((byte)1, e.getValue());
			this.accept.setVisible(true);
		});
		
		// Password TextBox
		HorizontalPanel hPanel = new HorizontalPanel();
		
		PasswordTextBox passwordTB = new PasswordTextBox();
		passwordTB.addValueChangeHandler(e -> {
			mainDigitalCertificatesObject.setPassword((byte)1, e.getValue());
			this.accept.setVisible(true);
		});
		hPanel.add(passwordTB);
		
		if(!mainDigitalCertificatesObject.hasData(Byte.parseByte("1"))) {
			AonTableButton showPassBtn = new AonTableButton("Mostrar", AON.CSS.aonIconShowPass());
			showPassBtn.addClickHandler(e -> {
				String type = passwordTB.getElement().getAttribute("type");
				if(StringUtils.isBlank(type) || (type != "text" || !type.equals("text")))
					passwordTB.getElement().setAttribute("type", "text");
				else
					passwordTB.getElement().setAttribute("type", "password");
			});
			
			showPassBtn.getElement().getStyle().setMarginLeft(5, Unit.PX);
			showPassBtn.getElement().getStyle().setMarginTop(4, Unit.PX);
			
			hPanel.add(showPassBtn);
		}
		
		// Upload & Download FormPanel
		HorizontalPanel hFormPanel = new HorizontalPanel();
		Widget formPanel = createFormPanel("1");
		hFormPanel.add(formPanel);
		
		if(mainDigitalCertificatesObject.hasData(Byte.parseByte("1"))) {
			AonTableButton deleteCertificate = new AonTableButton("Eliminar Cert", AON.CSS.aonIconDelete());
			deleteCertificate.getElement().getStyle().setMarginTop(5, Unit.PX);
			deleteCertificate.addClickHandler(e -> {
				mainDigitalCertificatesObject.deleteDigitalCertificate("1",
	    				s -> {
	    					this.accept.setVisible(false);
	    					secondaryUserDeckPanel.showWidget(0);
	    					initPreview();
	    					insertRows();
	    					fillCertificatesRows(mainDigitalCertificatesObject.getDigitalCertificateList());
	    					
	    					this.showSecondaryUsers.setVisible(true);
							this.secondayUsersPanel.setVisible(false);
							
							checkStatus(this.mainDigitalCertificatesObject);
	    				}, f -> {});
			});
			
			
			hFormPanel.add(deleteCertificate);
		}
		
		// Attach DateBoxEx
		Label dateL = new Label();
		dateL.getElement().getStyle().setTextAlign(TextAlign.CENTER);
		
		//Add to table
		digitalCertificatesDataTable.setWidget(row, 0, typeL);
		digitalCertificatesDataTable.setWidget(row, 1, confidentialCB);
		digitalCertificatesDataTable.setWidget(row, 2, hPanel);
		digitalCertificatesDataTable.setWidget(row, 3, hFormPanel);
		digitalCertificatesDataTable.setWidget(row, 4, dateL);
	}
	
	private Widget createFormPanel(String certificateTypeStr) {
		HorizontalPanel mainFlowPanel = new HorizontalPanel();
		mainFlowPanel.getElement().getStyle().setPaddingLeft(10, Unit.PX);
		mainFlowPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		mainFlowPanel.setWidth("350px");
		
		TextBox fileNameTB = new TextBox();
		if(mainDigitalCertificatesObject.hasMoraThanOneSEPECertificates() && AonStringUtils.equals(certificateTypeStr, "0"))
			fileNameTB.setEnabled(false);
		
		fileNameTB.getElement().getStyle().setWidth(305, Unit.PX);
		if(mainDigitalCertificatesObject.hasData(Byte.parseByte(certificateTypeStr)))
			fileNameTB.setValue(mainDigitalCertificatesObject.getDescription(Byte.parseByte(certificateTypeStr)));
		
		fileNameTB.addValueChangeHandler(e -> {
			mainDigitalCertificatesObject.setDescription(Byte.parseByte(certificateTypeStr), e.getValue());
			this.accept.setVisible(true);
		});
		
		//Create formPanel to UploadFiles
		FlowPanel flowPanel = new FlowPanel();
		
		FormPanel formPanel = new FormPanel();
		formPanel.setAction(GWT.getModuleBaseURL()+ "certificate/");
		formPanel.setEncoding(FormPanel.ENCODING_MULTIPART);
		formPanel.setMethod(FormPanel.METHOD_POST);
		
		Hidden extension = new Hidden("extension", "");
		Hidden fileName = new Hidden("filename", "");
		Hidden certificateType = new Hidden("certificatetype", certificateTypeStr);
		Hidden userLogin = new Hidden("currentUser", Wnd.getCurrentUser());
		Hidden currentDomain = new Hidden("currentDomain", Wnd.getCurrentDomainNameURL());
		Hidden token = new Hidden("token", Wnd.getToken());
		
		FileUpload fileU = new FileUpload();
		if(mainDigitalCertificatesObject.hasMoraThanOneSEPECertificates() && AonStringUtils.equals(certificateTypeStr, "0"))
			fileU.setEnabled(false);
		
		fileU.setName("uploader");
		fileU.getElement().setPropertyString("multiple", "multiple");
		fileU.getElement().setPropertyString("accept", ".p12");
		fileU.getElement().getStyle().setDisplay(Display.NONE);
		fileU.addChangeHandler((e) -> {
			String filename = getFileName(fileU.getFilename().toString());
			String fileExt = getFileExtension(fileU.getFilename());

            if(filename.length() == 0) {
            	// TODO : El archivo seleccionado no se ha podido subir
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
        					this.accept.setVisible(false);
        					secondaryUserDeckPanel.showWidget(0);
        					initPreview();
        					insertRows();
        					fillCertificatesRows(s);
        					
        					this.showSecondaryUsers.setVisible(true);
    						this.secondayUsersPanel.setVisible(false);
    						
    						checkStatus(this.mainDigitalCertificatesObject);
        				}, f -> {});
            }
	    });
		
		AonTableButton fileButton = new AonTableButton("Subir Cert", AON.CSS.aonIconAttach());
		fileButton.addClickHandler(e -> {
			fileU.click();
		});
		
	
		flowPanel.add(extension);
		flowPanel.add(fileName);
		flowPanel.add(certificateType);
		flowPanel.add(userLogin);
		flowPanel.add(currentDomain);
		flowPanel.add(token);
		flowPanel.add(fileButton);
		flowPanel.add(fileU);
		
		formPanel.add(flowPanel);
			
		mainFlowPanel.add(fileNameTB);
		
		if(!mainDigitalCertificatesObject.hasData(Byte.parseByte(certificateTypeStr)))
			mainFlowPanel.add(formPanel);
		
		return mainFlowPanel;
	}

	private void fillCertificatesRows(List<DigitalCertificate> digitalCertificates) {
		for(DigitalCertificate digitalCertificate : digitalCertificates) {
			if(digitalCertificate.getType() == (byte) 0) {
				if(mainDigitalCertificatesObject.hasMoraThanOneSEPECertificates())
					createAndfillSEPECertificateRow(digitalCertificate);
				else
					fillSEPECertificateRow(digitalCertificate);
			}
			if(digitalCertificate.getType() == (byte) 1)
				fillTGSSCertificateRow(digitalCertificate);	
		}
	}

	private void createAndfillSEPECertificateRow(DigitalCertificate digitalCertificate) {
		// Insert new row
		int row = digitalCertificatesDataTable.insertRow(digitalCertificatesDataTable.getRowCount());
		
		// Type Label
		Label typeL = new Label("Certificado Empresa (SEPE)");
		
		// Confidential CheckBox
		CheckBox confidentialCB = new CheckBox();
		confidentialCB.addValueChangeHandler((e) -> {
			mainDigitalCertificatesObject.setConfidential((byte)0, e.getValue());
			this.accept.setVisible(true);
		});
		
		// Password TextBox
		HorizontalPanel hPanel = new HorizontalPanel();
		
		PasswordTextBox passwordTB = new PasswordTextBox();
		passwordTB.addValueChangeHandler(e -> {
			mainDigitalCertificatesObject.setPassword((byte)0, e.getValue());
			this.accept.setVisible(true);
		});
		hPanel.add(passwordTB);
		
		if(!mainDigitalCertificatesObject.hasData(Byte.parseByte("0"))) {
			AonTableButton showPassBtn = new AonTableButton("Mostrar", AON.CSS.aonIconShowPass());
			showPassBtn.addClickHandler(e -> {
				String type = passwordTB.getElement().getAttribute("type");
				if(StringUtils.isBlank(type) || (type != "text" || !type.equals("text")))
					passwordTB.getElement().setAttribute("type", "text");
				else
					passwordTB.getElement().setAttribute("type", "password");
			});
			
			showPassBtn.getElement().getStyle().setMarginLeft(5, Unit.PX);
			showPassBtn.getElement().getStyle().setMarginTop(4, Unit.PX);
			hPanel.add(showPassBtn);
		}
		
		// Upload & Download FormPanel
		HorizontalPanel hFormPanel = new HorizontalPanel();
		
		Widget formPanel = createFormPanel("0");
		hFormPanel.add(formPanel);
		
		AonTableButton deleteCertificate = new AonTableButton("");
		if(mainDigitalCertificatesObject.hasData(Byte.parseByte("0")) && !mainDigitalCertificatesObject.hasMoraThanOneSEPECertificates()) {
			deleteCertificate = new AonTableButton("Eliminar Cert", AON.CSS.aonIconDelete());
			deleteCertificate.getElement().getStyle().setMarginTop(5, Unit.PX);
			deleteCertificate.addClickHandler(e -> {
				mainDigitalCertificatesObject.deleteDigitalCertificate("0",
	    				s -> {
	    					this.accept.setVisible(false);
	    					initPreview();
	    					insertRows();
	    					fillCertificatesRows(mainDigitalCertificatesObject.getDigitalCertificateList());
	    				}, f -> {});
			});
			
			
			hFormPanel.add(deleteCertificate);
		}
		
		
		// Attach DateBoxEx
		Label dateL = new Label();
		dateL.getElement().getStyle().setTextAlign(TextAlign.CENTER);
		
		//Add to table
		digitalCertificatesDataTable.setWidget(row, 0, typeL);
		digitalCertificatesDataTable.setWidget(row, 1, confidentialCB);
		digitalCertificatesDataTable.setWidget(row, 2, hPanel);
		digitalCertificatesDataTable.setWidget(row, 3, hFormPanel);
		digitalCertificatesDataTable.setWidget(row, 4, dateL);
		
		confidentialCB.setValue(digitalCertificate.getConfidential());
		passwordTB.setValue(digitalCertificate.getPassword());
		dateL.setText(formatFullDate.format(digitalCertificate.getCreationDate()));
		
		if(mainDigitalCertificatesObject.hasMoraThanOneSEPECertificates()) {
			confidentialCB.setEnabled(false);
			passwordTB.setEnabled(false);
			deleteCertificate.setVisible(false);
		}
	}

	private void fillSEPECertificateRow(DigitalCertificate digitalCertificate) {
		((CheckBox) digitalCertificatesDataTable.getWidget(0, 1)).setValue(digitalCertificate.getConfidential());
		((PasswordTextBox)((HorizontalPanel) digitalCertificatesDataTable.getWidget(0, 2)).getWidget(0)).setValue(digitalCertificate.getPassword());
		((Label) digitalCertificatesDataTable.getWidget(0, 4)).setText(formatFullDate.format(digitalCertificate.getCreationDate()));
	}
	
	private void fillTGSSCertificateRow(DigitalCertificate digitalCertificate) {
		if(mainDigitalCertificatesObject.hasMoraThanOneSEPECertificates()) {
			((CheckBox) digitalCertificatesDataTable.getWidget(0, 1)).setValue(digitalCertificate.getConfidential());
			((PasswordTextBox)((HorizontalPanel) digitalCertificatesDataTable.getWidget(0, 2)).getWidget(0)).setValue(digitalCertificate.getPassword());
			((Label) digitalCertificatesDataTable.getWidget(0, 4)).setText(formatFullDate.format(digitalCertificate.getCreationDate()));
		}else {
			((CheckBox) digitalCertificatesDataTable.getWidget(1, 1)).setValue(digitalCertificate.getConfidential());
			((PasswordTextBox)((HorizontalPanel) digitalCertificatesDataTable.getWidget(1, 2)).getWidget(0)).setValue(digitalCertificate.getPassword());
			((Label) digitalCertificatesDataTable.getWidget(1, 4)).setText(formatFullDate.format(digitalCertificate.getCreationDate()));
		}
	}
	
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
	
	private void onShowSecondaryUsers(ClickEvent event) {
		secondayUsersPanel.setVisible(true);
		mainDigitalCertificatesObject.getSecondaryUsers(t -> {
			this.showSecondaryUsers.setVisible(false);
			insertSecondaryUsersRows();
		}, e -> {});
	}
	
	private void onAccept(ClickEvent event) {
		mainDigitalCertificatesObject.setDigitalCertificates(s -> {
			mainDigitalCertificatesObject.getDigitalCertificates(
					t -> {
						this.accept.setVisible(false);
						secondaryUserDeckPanel.showWidget(0);
						initPreview();
						insertRows();
						fillCertificatesRows(t);
						this.showSecondaryUsers.setVisible(true);
						this.secondayUsersPanel.setVisible(false);
						
						checkStatus(this.mainDigitalCertificatesObject);
					}, f -> {});
		}, f -> {});
	}
	
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

}
