package com.esferalia.aon.gwt.payroll.client;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Consumer;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.css.AonGwtTemplateResources;
import com.esferalia.aon.gwt.common.client.widget.MonthListBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDockLayout;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessagePanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.gwt.payroll.client.AonActivityPanel.AonActivityPanelCallback;
import com.esferalia.aon.gwt.payroll.client.AonCCCPanel.AonCCCPanelCallback;
import com.esferalia.aon.gwt.payroll.shared.HttpException;
import com.esferalia.aon.gwt.payroll.shared.IvlService;
import com.esferalia.aon.occam.api.model.EnterpriseCCC;
import com.esferalia.aon.occam.api.model.payroll.Activity;
import com.esferalia.aon.watson.util.Pair;
import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.xhr.client.XMLHttpRequest;

import net.aonsolutions.gwt.pdfjs.client.FullViewer;

public class MainCCC extends MainEntryPoint{
	
	// ----------------------------------------------- CCC Implementation
	
	private class CCCWidgetImpl extends CCCNew {

		@Override
		protected void onInsertRows() {
			if(mainCCCObject.getCCCs().isEmpty()) {
				initPreview();
				paintNoDataRow();
			} else {
				initPreview();
				mainCCCObject.getCCCs().forEach(CCCWidgetImpl.this::insertRow);
			}
		}

		@Override
		protected void onDeleteCCC(Integer cccId) {
			AonMessagePanel.showLoading(messagePanel, "Eliminando cuenta de cotizaci\u00f3n ...");
			mainCCCObject.deleteCCC(cccId, s -> {
				AonMessagePanel.showSuccess(messagePanel, "Cuenta de cotizaci\u00f3n eliminada correctamente");
				onModuleLoad(mainCCCObject);
			}, f -> AonMessagePanel.showError(messagePanel, f.getMessage()));
		}
		
		@Override
		protected void onCCCOpen(EnterpriseCCC ccc) {
			final AonCustomDialog dialog = new AonCustomDialog();
			dialog.setCaption( "Editar Cuenta Cotizaci\u00f3n" );
			AonCCCPanel cccDialog = new AonCCCPanel(mainCCCObject.getDomain(), mainCCCObject.getActivities(), ccc, new AonCCCPanelCallback() {
				
				@Override
				public void onCancel() {
					dialog.hide();
				}
				
				@Override
				public void onAccept(EnterpriseCCC ccc) {
					dialog.hide();
					AonMessagePanel.showLoading(messagePanel, "Guardando cuenta de cotizaci\u00f3n ...");
					mainCCCObject.saveCCC(ccc, s -> {
						AonMessagePanel.showSuccess(messagePanel, "Cuenta de cotizaci\u00f3n guardada correctamente");
						onModuleLoad(mainCCCObject);
					}, f -> AonMessagePanel.showError(messagePanel, f.getMessage()));
				}
			}) {

				@Override
				protected void onResize() {
					dialog.showLoaded();
				}};
			
			
			dialog.add( cccDialog );
			dialog.showLoaded();
		}

		@Override
		protected Set<Entry<Integer, String>> getActivities() {
			return mainCCCObject.getActivities();
		}

		@Override
		protected <T> void fireWarningMessage(Map<String, T> messages) {
			AonMessagePanel.showWarning(messagePanel, messages);
		}
		
		@Override
		protected <T> void fireInfoMessage(Map<String, T> messages) {
			AonMessagePanel.showInfo(messagePanel, messages);
		}

		@Override
		protected <T> void fireLoadingMessage(T message) {
			AonMessagePanel.showLoading(messagePanel, message);
		}

		@Override
		protected void hideMessage() {
			AonMessagePanel.hideMessage(messagePanel);
			AonMessagePanel.hideMessage(messagePanelPDF);
		}

		@Override
		protected void showPDF(String dataURI, String title, boolean isLaboralLife) {
			showPdf(isLaboralLife);
			pdfDockLayoutPanel.setToolbarTitle(title);
			fullViewer.open(dataURI);
		}
	}
	
	// ----------------------------------------------- UiBinder
	
	interface Binder extends UiBinder<Widget, MainCCC> {}

	private static final Binder binder = GWT.create(Binder.class);
	
	// ----------------------------------------------- UiFields
	
	@UiField
	DeckPanel deckPanel;
	
	@UiField(provided = true)
	AonCustomDockLayout dockLayoutPanel;
	
	@UiField(provided = true)
	AonCustomDockLayout pdfDockLayoutPanel;
	
	// --------------------------------------------- Import Form
	
	private FormPanel uploadForm;
	private FileUpload fileUpload;

	// ----------------------------------------------- Variables
	
	private MainCCCObject mainCCCObject;
	
	// ----------------------------------------------- Variables (dockLayoutPanel)
	
	private HTMLPanel container;
	private HTMLPanel messagePanel = new HTMLPanel("");
	private CCCWidgetImpl activityCCCWidget;
	private AonToolbarButton checkUpdateCert;
	
	// ----------------------------------------------- Variables (pdfDockLayoutPanel)
	
	private HTMLPanel containerPDF;
	private HTMLPanel messagePanelPDF = new HTMLPanel("");
	private FullViewer fullViewer;
	
	// ----------------------------------------------- Constructor

	public MainCCC() {	
		activityCCCWidget = new CCCWidgetImpl();
		
		this.dockLayoutPanel = new AonCustomDockLayout("C\u00f3digo Cuentas Cotizaci\u00f3n");
		this.pdfDockLayoutPanel = new AonCustomDockLayout("PDF");
		
		GWT.<AonGwtTemplateResources>create(AonGwtTemplateResources.class).css().ensureInjected();
		AON.ensureInjected();
	
		Widget ui = binder.createAndBindUi(this);
		RootLayoutPanel.get(getRootPanel() != null ? getRootPanel() : "rootPanel").add(ui);
		
		// dockLayoutPanel
		
		showCCCs();
		
		container = new HTMLPanel("");
		container.addStyleName(AON.CSS.aonFlexColumn());
		
		container.add(messagePanel);
		container.add(activityCCCWidget);
		
		this.dockLayoutPanel.add(container);
		
		addButtonsToolbar();
		
		// pdfDockLayoutPanel
		
		containerPDF = new HTMLPanel("");
		containerPDF.addStyleName(AON.CSS.aonFlexColumn());
		
		fullViewer = new FullViewer();
		
		containerPDF.add(messagePanelPDF);
		containerPDF.add(fullViewer);
		
		this.pdfDockLayoutPanel.add(containerPDF);
		
		addPDFButtonsToolbar();

		initUploadForm();
		
	}
	
	// ----------------------------------------------- onModuleLoad
	
	@Override
	public void onModuleLoad() {
		onModuleLoad( new MainCCCObject());
	}

	public void onModuleLoad(MainCCCObject mainCCCObject) {
		this.mainCCCObject = mainCCCObject;
		this.mainCCCObject.getMainCCCInfo(
				s -> {
					checkUpdateCert.setVisible(!mainCCCObject.getActivities().isEmpty());
					activityCCCWidget.onInsertRows();
				}, f -> {});
	}
	
	// ------------------------------------------ Upload Form

	private void initUploadForm() {
	    uploadForm = new FormPanel();
	    uploadForm.setVisible(false);
	    uploadForm.setAction(IvlService.IVL_URL);
	    uploadForm.setMethod(FormPanel.METHOD_POST);
	    uploadForm.setEncoding(FormPanel.ENCODING_MULTIPART);
	    uploadForm.addSubmitCompleteHandler(response ->  { 
		try {
		    activityCCCWidget.fireInfoMessage(Collections.singletonMap("IVL", toSafeHtml(response.getResults())));
		} catch ( IllegalArgumentException e ) {
		    activityCCCWidget.fireInfoMessage(Collections.singletonMap("IVL", response.getResults()));
		}
	    } );
	    
	    
	    FlowPanel formPanel = new FlowPanel();
	    
	    fileUpload = new FileUpload();
	    fileUpload.setName(IvlService.Parameter.FILE.name());
	    fileUpload.setVisible(false);
	    formPanel.add(fileUpload);
	    
	    formPanel.add(new Hidden(IvlService.Parameter.USER.name(), Wnd.getCurrentUser()));
	    formPanel.add(new Hidden(IvlService.Parameter.DOMAIN.name(), Wnd.getCurrentDomainNameURL()));
	    
	    
	    uploadForm.add(formPanel);
	    
	    this.dockLayoutPanel.addToolbarButton(uploadForm);
	}
	
	private void uploadFile() {
	    fileUpload.click();
	    fileUpload.addChangeHandler( e -> {
		    activityCCCWidget.fireLoadingMessage(toSafeHtml("Importando <b>Informe Vida Laboral de un C&oacute;digo de Cuenta de Cotizaci&oacute;n</b>..."));
		    uploadForm.submit();
	    });
	}

	private SafeHtml toSafeHtml(String html) {
	    return new SafeHtmlBuilder().appendHtmlConstant(html).toSafeHtml();
	}
	
	// ----------------------------------------------- Toolbar

	private void addButtonsToolbar() {
		
		AonToolbarButton createCCCBtn = new AonToolbarButton(AON.MSG.newAction() + " CCC", AON.CSS.aonIconAdd() );
		createCCCBtn.addClickHandler(e -> {
			
			if(mainCCCObject.getActivities().isEmpty()) {
				
				final AonCustomDialog dialog = new AonCustomDialog();
				dialog.setCaption( "Nueva Actividad" );
				AonActivityPanel cccDialog = new AonActivityPanel(mainCCCObject.getDomain(), new AonActivityPanelCallback() {
					
					@Override
					public void onCancel() {
						dialog.hide();
					}
					
					@Override
					public void onAccept(Activity activity) {
						dialog.hide();
						AonMessagePanel.showLoading(messagePanel, "Creando actividad " + activity.getDescription() + " ...");
						mainCCCObject.saveActivity(activity, s -> {
							AonMessagePanel.showSuccess(messagePanel, "Actividad creada correctamente");
							mainCCCObject.getMainCCCInfo(
									su -> {
										activityCCCWidget.onInsertRows();
										
										final AonCustomDialog dialog = new AonCustomDialog();
										dialog.setCaption( "Nueva Cuenta Cotizaci\u00f3n" );
										AonCCCPanel cccDialog = new AonCCCPanel(mainCCCObject.getDomain(), mainCCCObject.getActivities(), new AonCCCPanelCallback() {
											
											@Override
											public void onCancel() {
												dialog.hide();
											}
											
											@Override
											public void onAccept(EnterpriseCCC ccc) {
												dialog.hide();
												AonMessagePanel.showLoading(messagePanel, "Guardando cuenta de cotizaci\u00f3n ...");
												mainCCCObject.saveCCC(ccc, s -> {
													AonMessagePanel.showSuccess(messagePanel, "Cuenta de cotizaci\u00f3n guardada correctamente");
													onModuleLoad(mainCCCObject);
												}, f -> AonMessagePanel.showError(messagePanel, f.getMessage()));
											}
										}) {

											@Override
											protected void onResize() {
												dialog.showLoaded();
											}};
										
										
										dialog.add( cccDialog );
										dialog.showLoaded();
									}, f -> {});
						}, f -> AonMessagePanel.showError(messagePanel, f.getMessage()));
					}
				}) {

					@Override
					protected void onResize() {
						dialog.showLoaded();
					}};
				
				
				dialog.add( cccDialog );
				dialog.showLoaded();
				
			} else {
				final AonCustomDialog dialog = new AonCustomDialog();
				dialog.setCaption( "Nueva Cuenta Cotizaci\u00f3n" );
				AonCCCPanel cccDialog = new AonCCCPanel(mainCCCObject.getDomain(), mainCCCObject.getActivities(), new AonCCCPanelCallback() {
					
					@Override
					public void onCancel() {
						dialog.hide();
					}
					
					@Override
					public void onAccept(EnterpriseCCC ccc) {
						dialog.hide();
						AonMessagePanel.showLoading(messagePanel, "Guardando cuenta de cotizaci\u00f3n ...");
						mainCCCObject.saveCCC(ccc, s -> {
							AonMessagePanel.showSuccess(messagePanel, "Cuenta de cotizaci\u00f3n guardada correctamente");
							onModuleLoad(mainCCCObject);
						}, f -> AonMessagePanel.showError(messagePanel, f.getMessage()));
					}
				}) {

					@Override
					protected void onResize() {
						dialog.showLoaded();
					}};
				
				
				dialog.add( cccDialog );
				dialog.showLoaded();
			}
		});
		
		checkUpdateCert = new AonToolbarButton("Cert. de estar al corriente con TGSS", AON.CSS.aonIconTgss() );
		checkUpdateCert.addClickHandler(e -> onCheckUpdateCert());
		
		AonToolbarButton importBtn = new AonToolbarButton(AON.MSG.importAction() + " vida laboral" , AON.CSS.aonIconUploadFile() );
		importBtn.addClickHandler(e -> uploadFile());
		
		this.dockLayoutPanel.addToolbarButton(createCCCBtn);
		this.dockLayoutPanel.addToolbarButton(checkUpdateCert);
		this.dockLayoutPanel.addToolbarButton(importBtn);
		
		this.dockLayoutPanel.hideSearchWidget();
		this.dockLayoutPanel.hideFilterWidget();
		
	}
	
	// ------------------------------------------------- Toolbar PDFViewer panel
	
	private void addPDFButtonsToolbar() {

		AonToolbarButton backButton = new AonToolbarButton(AON.MSG.closed(), AON.CSS.aonIconBack());
		backButton.addClickHandler(e -> showCCCs());
		
		this.pdfDockLayoutPanel.addToolbarButton(backButton);
		this.pdfDockLayoutPanel.hideSearchWidget();
		this.pdfDockLayoutPanel.hideFilterWidget();
	}
	
	// ----------------------------------------------- Toolbar.Methods TGSS
	
	private void onCheckUpdateCert() {
		AonMessagePanel.showLoading(messagePanel, "Obteniendo Cert. de estar al corriente con TGSS ...");
		Pair<String, String> completeCCC = mainCCCObject.getPrincipalAccount();
		
		mainCCCObject.getUpdateCert(completeCCC.getKey(), completeCCC.getValue(),
				dataURI -> {
					showPdf(false);
					pdfDockLayoutPanel.setToolbarTitle("Cert. de estar al corriente con TGSS");
					fullViewer.open(dataURI);
					AonMessagePanel.hideMessage(messagePanel);
				}, f -> {
					Map<String, String> warningMap = new HashMap<>();
					warningMap.put("Error obtenci\u00f3n TGSS", f.getMessage());
					AonMessagePanel.showWarning(messagePanel, warningMap);
				});
	}
	
	// ------------------------------------------------- Show/Hide PDF

	private void showCCCs() {
		deckPanel.showWidget(0);
	}

	private void showPdf(boolean isLaboralLife) {
		deckPanel.showWidget(1);
		checkPDFToolbar(isLaboralLife);
	}
	
	private void checkPDFToolbar(boolean isLaboralLife) {
		if(isLaboralLife && this.pdfDockLayoutPanel.getToolbarButtonCount() == 1) {
			MonthListBox monthListBox = new MonthListBox();
			Date lastMonth = DateUtils.getFirstDayOfMonth(); 
			Date firstMonth = DateUtils.addYears2Date(DateUtils.getFirstDayOfMonth(), -4);
			monthListBox.setFirstMonth(firstMonth);
			monthListBox.setLastMonth(lastMonth);
			monthListBox.setPageSize(52);
			monthListBox.setVisibleRange(0, 52);
			monthListBox.addChangeHandler(e -> {
				AonMessagePanel.showLoading(messagePanelPDF, "Obteniendo Informe de Vida Laboral ...");
				this.activityCCCWidget.onLaboralLife(monthListBox.getSelected());
			});
			monthListBox.setSelected(DateUtils.getFirstDayOfMonth(), true);
			monthListBox.setWidth("200px");
			this.pdfDockLayoutPanel.addToolbarButton(monthListBox);
			
			AonToolbarButton importPDF = new AonToolbarButton(AON.MSG.importAction(), AON.CSS.aonIconImport());
			importPDF.addClickHandler(e -> onImportIvl());
			this.pdfDockLayoutPanel.addToolbarButton(importPDF);

		} else if(!isLaboralLife && this.pdfDockLayoutPanel.getToolbarButtonCount() > 1)
			this.pdfDockLayoutPanel.getToolbarButtonPanel().remove(this.pdfDockLayoutPanel.getToolbarButtonCount()-1);
		
	}
	
	
	private void onImportIvl() {
	    fullViewer.getData(this::importIvl);
	}
	
	private void importIvl(String data) {
		AonMessagePanel.showLoading(messagePanelPDF, toSafeHtml("Importando <b>Informe Vida Laboral de un C&oacute;digo de Cuenta de Cotizaci&oacute;n</b>..."));
		
		upload(data, 
		    message -> AonMessagePanel.showInfo(messagePanelPDF, Collections.singletonMap("IVL", toSafeHtml(message))), 
		    exception -> AonMessagePanel.showWarning(messagePanelPDF, Collections.singletonMap("IVL", exception.getMessage())) );
	}

	protected static void upload(String data, Consumer<String> onSuccess, Consumer<HttpException> onFailure ) {

	    XMLHttpRequest xmlHttpRequest = XMLHttpRequest.create();

	    xmlHttpRequest.setOnReadyStateChange(xhr -> {
		int state = xhr.getReadyState();
		
		if (state != XMLHttpRequest.DONE)
			return;
		
		int status = xhr.getStatus();
		// Successful 2xx
		if (status >= 200 && status < 300)
			onSuccess.accept(xhr.getResponseText());
		else
			onFailure.accept(new HttpException(status, xhr.getResponseText()));
		
		
	    });

	    xmlHttpRequest.open("POST", IvlService.IVL_URL);

	    /* enctype is multipart/form-data */
	    String boundary = "---------------------------" + Long.toHexString(System.currentTimeMillis());
	    xmlHttpRequest.setRequestHeader("Content-Type", "multipart/form-data; boundary=" + boundary);

	    StringBuilder requestBuffer = new StringBuilder();
	    
	    Map<String, String> datas = new HashMap<>();
	    datas.put(IvlService.Parameter.USER.name(), Wnd.getCurrentUser());
	    datas.put(IvlService.Parameter.DOMAIN.name(), Wnd.getCurrentDomainNameURL());
	    
	    for (Map.Entry<String, String> entry : datas.entrySet()) {
		    // We start a new part in our body's request
		    requestBuffer.append("--" + boundary + "\r\n");
		    // We said it's form data (it could be something else)
		    requestBuffer.append("Content-Disposition: form-data; "
			    // We define the name of the form data
			    + "name=\"" + entry.getKey() + "\"\r\n");
		    // There is always a blank line between the meta-data and the
		    // data
		    requestBuffer.append("\r\n");

		    requestBuffer.append(entry.getValue());

		    requestBuffer.append("\r\n");
	    }

	    // We start a new part in our body's request
	    requestBuffer.append("--" + boundary + "\r\n");
	    // We said it's form data (it could be something else)
	    requestBuffer.append("Content-Disposition: form-data; "
		    // We define the name of the form data
		    + "name=\""+IvlService.Parameter.FILE.name() +"\"; "
		    // We provide the 'real' name of the file
		    + "filename=\"Informe de Vida Laboral.pdf" + "\"\r\n");
	    requestBuffer.append("Content-Transfer-Encoding: base64\r\n");
		    // We provide the mime type of the file
	    requestBuffer.append("Content-Type: application/pdf\r\n");
	    // There is always a blank line between the meta-data and the data
	    requestBuffer.append("\r\n");

	    requestBuffer.append(data);

	    requestBuffer.append("\r\n");

	    // Once we are done, we "close" the body's request
	    requestBuffer.append("--" + boundary + "--\r\n");

	    xmlHttpRequest.send(requestBuffer.toString());

	}
	
	
	static native String btoa(byte[] data) /*-{
	    return btoa(data);
	}-*/;
	
	public static native void log (String message ) /*-{
		console.log(message);
	}-*/;

    public static native void error (String message ) /*-{
    	console.error(message);
    }-*/;
		
}
