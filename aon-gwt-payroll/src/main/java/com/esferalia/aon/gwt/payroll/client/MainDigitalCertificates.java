package com.esferalia.aon.gwt.payroll.client;

import java.util.List;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.css.AonGwtTemplateResources;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbar;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.gwt.common.shared.StringUtils;
import com.esferalia.aon.gwt.payroll.shared.DigitalCertificate;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Display;
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
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class MainDigitalCertificates extends MainEntryPoint{

	// -------------------------------------------------- UiBinder --------------------------------------------------
	
	interface Binder extends UiBinder<Widget, MainDigitalCertificates> {}

	private static final Binder binder = GWT.create(Binder.class);
	
	@UiField
	MyStyle style;

	interface MyStyle extends CssResource {
		String headerStyle();
	}
	
	@UiField
	DockLayoutPanel dockLayoutPanel;
	
	@UiField
	Grid digitalCertificatesDataTableHeader;
	
	@UiField
	ScrollPanel scrollPanel;
	
	@UiField
	Grid digitalCertificatesDataTable;
	
	// -------------------------------------------- Variables de la clase---------------------------------------------
	
	private MainDigitalCertificatesObject mainDigitalCertificatesObject;
	private DateTimeFormat formatFullDate = DateTimeFormat.getFormat("dd/MM/yyyy");
	
	private AonToolbar toolbar;
	private AonToolbarButton accept;
	
	// ------------------------------------------------- CONSTRUCTOR --------------------------------------------------

	public MainDigitalCertificates() {	
		GWT.<AonGwtTemplateResources>create(AonGwtTemplateResources.class).css().ensureInjected();
		AON.ensureInjected();
	
		Widget ui = binder.createAndBindUi(this);
		RootLayoutPanel.get(getRootPanel() != null ? getRootPanel() : "rootPanel").add(ui);
		
		toolbar = getToolbarPanel();
		dockLayoutPanel.addNorth( toolbar , AonToolbar.HEIGTH );
	}
	
	// -------------------------------------------------- UiHandlers --------------------------------------------------

	// ----------------------------------------------- METODOS DE LA CLASE ------------------------------------------------

	public void onModuleLoad(MainDigitalCertificatesObject mainDigitalCertificatesObject) {
		this.mainDigitalCertificatesObject = mainDigitalCertificatesObject;
		this.mainDigitalCertificatesObject.getDigitalCertificates(
				s -> {
					initPreview();
					insertRows();
					fillCertificatesRows(s);
				}, f -> {});
	}

	private void initPreview() {
		digitalCertificatesDataTableHeader.clear();
		digitalCertificatesDataTableHeader.resize(0, 0);
		digitalCertificatesDataTable.clear();
		digitalCertificatesDataTable.resize(0, 0);
		digitalCertificatesDataTableHeader.resizeColumns(5);
		digitalCertificatesDataTable.resizeColumns(5);
		
		paintHeader();
		calculateScrollPanelHeight();
		setColumnWidth();
	}
	
	private void paintHeader() {
		int row = digitalCertificatesDataTableHeader.insertRow(digitalCertificatesDataTableHeader.getRowCount());
		Label type = new Label("TIPO");
		Label confidential = new Label();
		confidential.setStyleName("aon-editDataTable-button aon-icon-confidential");
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
	
	private void calculateScrollPanelHeight() {
		Integer clientHeight = Window.getClientHeight();
		scrollPanel.setHeight((clientHeight - 900) + "px");
	}
	
	private void setColumnWidth() {
		//MaxWidth 950px
		digitalCertificatesDataTableHeader.getCellFormatter().getElement(0, 0).getStyle().setWidth(200, Unit.PX);
		digitalCertificatesDataTableHeader.getCellFormatter().getElement(0, 1).getStyle().setWidth(50, Unit.PX);
		digitalCertificatesDataTableHeader.getCellFormatter().getElement(0, 1).getStyle().setPaddingLeft(10, Unit.PX);
		digitalCertificatesDataTableHeader.getCellFormatter().getElement(0, 2).getStyle().setWidth(175, Unit.PX);
		digitalCertificatesDataTableHeader.getCellFormatter().getElement(0, 3).getStyle().setWidth(375, Unit.PX);
		digitalCertificatesDataTableHeader.getCellFormatter().getElement(0, 4).getStyle().setWidth(150, Unit.PX);
		
		digitalCertificatesDataTable.getColumnFormatter().getElement(0).getStyle().setWidth(200, Unit.PX);
		digitalCertificatesDataTable.getColumnFormatter().getElement(1).getStyle().setWidth(50, Unit.PX);
		digitalCertificatesDataTable.getColumnFormatter().getElement(2).getStyle().setWidth(175, Unit.PX);
		digitalCertificatesDataTable.getColumnFormatter().getElement(3).getStyle().setWidth(375, Unit.PX);
		digitalCertificatesDataTable.getColumnFormatter().getElement(4).getStyle().setWidth(150, Unit.PX);
	}
	
	private void insertRows() {
		insertSEPECertificateRow();
		insertTGSSCertificateRow();
	}

	private void insertSEPECertificateRow() {
		// Insert new row
		int row = digitalCertificatesDataTable.insertRow(digitalCertificatesDataTable.getRowCount());
		
		// Type Label
		Label typeL = new Label("Certificado Empresa");
		
		// Confidential CheckBox
		CheckBox confidentialCB = new CheckBox();
		confidentialCB.addValueChangeHandler((e) -> {
			mainDigitalCertificatesObject.setConfidential((byte)0, e.getValue());
		});
		
		// Password TextBox
		HorizontalPanel hPanel = new HorizontalPanel();
		PasswordTextBox passwordTB = new PasswordTextBox();
		passwordTB.addValueChangeHandler(e -> {
			mainDigitalCertificatesObject.setPassword((byte)0, e.getValue());
		});
		Button showPassBtn = new Button();
		showPassBtn.setStyleName("aon-editDataTable-button aon-icon-audit");
		showPassBtn.addClickHandler(e -> {
			String type = passwordTB.getElement().getAttribute("type");
			if(StringUtils.isBlank(type) || (type != "text" || !type.equals("text")))
				passwordTB.getElement().setAttribute("type", "text");
			else
				passwordTB.getElement().setAttribute("type", "password");
		});
		hPanel.add(passwordTB);
		hPanel.add(showPassBtn);
		showPassBtn.getElement().getStyle().setMarginLeft(5, Unit.PX);
		showPassBtn.getElement().getStyle().setMarginTop(4, Unit.PX);
		
		// Attach DateBoxEx
		Label dateL = new Label();
		dateL.getElement().getStyle().setPaddingLeft(5, Unit.PX);
		
		// Upload & Download FormPanel
		Widget formPanel = createFormPanel("0");
		
		//Add to table
		digitalCertificatesDataTable.setWidget(row, 0, typeL);
		digitalCertificatesDataTable.setWidget(row, 1, confidentialCB);
		digitalCertificatesDataTable.setWidget(row, 2, hPanel);
		digitalCertificatesDataTable.setWidget(row, 3, formPanel);
		digitalCertificatesDataTable.setWidget(row, 4, dateL);
	}
	
	private void insertTGSSCertificateRow() {
		// Insert new row
		int row = digitalCertificatesDataTable.insertRow(digitalCertificatesDataTable.getRowCount());
		
		// Type Label
		Label typeL = new Label("Certificado Personal");
		
		// Confidential CheckBox
		CheckBox confidentialCB = new CheckBox();
		confidentialCB.addValueChangeHandler((e) -> {
			mainDigitalCertificatesObject.setConfidential((byte)1, e.getValue());
		});
		
		// Password TextBox
		HorizontalPanel hPanel = new HorizontalPanel();
		PasswordTextBox passwordTB = new PasswordTextBox();
		passwordTB.addValueChangeHandler(e -> {
			mainDigitalCertificatesObject.setPassword((byte)1, e.getValue());
		});
		Button showPassBtn = new Button();
		showPassBtn.setStyleName("aon-editDataTable-button aon-icon-audit");
		showPassBtn.addClickHandler(e -> {
			String type = passwordTB.getElement().getAttribute("type");
			if(StringUtils.isBlank(type) || (type != "text" || !type.equals("text")))
				passwordTB.getElement().setAttribute("type", "text");
			else
				passwordTB.getElement().setAttribute("type", "password");
		});
		hPanel.add(passwordTB);
		hPanel.add(showPassBtn);
		showPassBtn.getElement().getStyle().setMarginLeft(5, Unit.PX);
		showPassBtn.getElement().getStyle().setMarginTop(4, Unit.PX);
		
		// Attach DateBoxEx
		Label dateL = new Label();
		dateL.getElement().getStyle().setPaddingLeft(5, Unit.PX);
		
		// Upload & Download FormPanel
		Widget formPanel = createFormPanel("1");
		
		//Add to table
		digitalCertificatesDataTable.setWidget(row, 0, typeL);
		digitalCertificatesDataTable.setWidget(row, 1, confidentialCB);
		digitalCertificatesDataTable.setWidget(row, 2, hPanel);
		digitalCertificatesDataTable.setWidget(row, 3, formPanel);
		digitalCertificatesDataTable.setWidget(row, 4, dateL);
	}
	
	private Widget createFormPanel(String certificateTypeStr) {
		HorizontalPanel mainFlowPanel = new HorizontalPanel();
		mainFlowPanel.getElement().getStyle().setPaddingLeft(10, Unit.PX);
		mainFlowPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		mainFlowPanel.setWidth("350px");
		
		TextBox fileNameTB = new TextBox();
		fileNameTB.getElement().getStyle().setWidth(325, Unit.PX);
		if(mainDigitalCertificatesObject.hasData(Byte.parseByte(certificateTypeStr)))
			fileNameTB.setValue(mainDigitalCertificatesObject.getDescription(Byte.parseByte(certificateTypeStr)));
		
		fileNameTB.addValueChangeHandler(e -> {
			mainDigitalCertificatesObject.setDescription(Byte.parseByte(certificateTypeStr), e.getValue());
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
		
		FileUpload fileU = new FileUpload();
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
            	formPanel.submit();
            }
		});
		
		formPanel.addSubmitCompleteHandler((e) -> {
	        if(e.getResults().length() == 0) {
                Window.alert("Something went wrong - Try again");
            } else {
            	// TODO : refresh table
            	mainDigitalCertificatesObject.getDigitalCertificates(
        				s -> {
        					initPreview();
        					insertRows();
        					fillCertificatesRows(s);
        				}, f -> {});
            }
	    });
		
		Button fileButton = new Button();
		fileButton.setStyleName("aon-editDataTable-button aon-icon-attach-file");
		fileButton.addClickHandler(e -> {
			fileU.click();
		});
		
	
		flowPanel.add(extension);
		flowPanel.add(fileName);
		flowPanel.add(certificateType);
		flowPanel.add(userLogin);
		flowPanel.add(currentDomain);
		flowPanel.add(fileButton);
		flowPanel.add(fileU);
		
		formPanel.add(flowPanel);
			
		mainFlowPanel.add(formPanel);
		mainFlowPanel.add(fileNameTB);
		return mainFlowPanel;
	}

	private void fillCertificatesRows(List<DigitalCertificate> digitalCertificates) {
		for(DigitalCertificate digitalCertificate : digitalCertificates) {
			if(digitalCertificate.getType() == (byte) 0)
				fillSEPECertificateRow(digitalCertificate);	
			if(digitalCertificate.getType() == (byte) 1)
				fillTGSSCertificateRow(digitalCertificate);	
		}
	}

	private void fillSEPECertificateRow(DigitalCertificate digitalCertificate) {
		((CheckBox) digitalCertificatesDataTable.getWidget(0, 1)).setValue(digitalCertificate.getConfidential());
		((PasswordTextBox)((HorizontalPanel) digitalCertificatesDataTable.getWidget(0, 2)).getWidget(0)).setValue(digitalCertificate.getPassword());
		((Label) digitalCertificatesDataTable.getWidget(0, 4)).setText(formatFullDate.format(digitalCertificate.getCreationDate()));
	}
	
	private void fillTGSSCertificateRow(DigitalCertificate digitalCertificate) {
		((CheckBox) digitalCertificatesDataTable.getWidget(1, 1)).setValue(digitalCertificate.getConfidential());
		((PasswordTextBox)((HorizontalPanel) digitalCertificatesDataTable.getWidget(1, 2)).getWidget(0)).setValue(digitalCertificate.getPassword());
		((Label) digitalCertificatesDataTable.getWidget(1, 4)).setText(formatFullDate.format(digitalCertificate.getCreationDate()));
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
		
		AonToolbar toolbar = new AonToolbar("Certificados digitales");

		accept = new AonToolbarButton( AON.MSG.saveAction(), AON.CSS.aonIconSave() );
		accept.setAccessKey('G');
		accept.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				onAccept(event);
			}
		});
		toolbar.add(accept);

		return toolbar;

	}
	
	private void onAccept(ClickEvent event) {
		mainDigitalCertificatesObject.setDigitalCertificates(s -> {
			mainDigitalCertificatesObject.getDigitalCertificates(
					t -> {
						initPreview();
						insertRows();
						fillCertificatesRows(t);
					}, f -> {});
		}, f -> {});
	}

}
