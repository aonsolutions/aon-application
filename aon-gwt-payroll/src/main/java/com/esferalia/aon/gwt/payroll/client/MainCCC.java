package com.esferalia.aon.gwt.payroll.client;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Consumer;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.css.AonGwtTemplateResources;
import com.esferalia.aon.gwt.common.client.widget.MonthListBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog.AonAcceptDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessagePanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbar;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.gwt.common.shared.Base64;
import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.gwt.payroll.shared.HttpException;
import com.esferalia.aon.gwt.payroll.shared.IvlService;
import com.esferalia.aon.occam.api.model.EnterpriseCCC;
import com.esferalia.aon.watson.util.Pair;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.xhr.client.ReadyStateChangeHandler;
import com.google.gwt.xhr.client.XMLHttpRequest;

import net.aonsolutions.gwt.pdfjs.client.FullViewer;

public class MainCCC extends MainEntryPoint{
	
	// ----------------------------------------------- CCC Implementation
	
	private class CCCWidgetImpl extends CCC {

		@Override
		protected void onInsertRow() {
			// Not use in this case
		}
		
		@Override
		protected void onInsertRows() {
			if(mainCCCObject.getCCCs().isEmpty())
				showCCCMessage();
			else {
				showCCCTable();
				mainCCCObject.getCCCs().forEach(ccc -> cccWidget.insertRow(ccc));
			}
		}

		@Override
		protected void onDeleteCCC(Integer cccId) {
			mainCCCObject.deleteCCC(cccId);
			setHasChange(true);
		}

		@Override
		protected void onInsertCCC(EnterpriseCCC ccc) {
			mainCCCObject.insertCCC(ccc);
			setHasChange(true);
		}

		@Override
		protected void onInsertActivity(com.esferalia.aon.occam.api.model.payroll.Activity activity) {
			mainCCCObject.insertActivity(activity);
			setHasChange(true);
		}

		@Override
		protected Set<Entry<Integer, String>> getActivities() {
			return mainCCCObject.getActivities();
		}
		
		@Override
		public List<EnterpriseCCC> getEnterpriseCCCs() {
			return mainCCCObject.getActiveCCCs();
		}

		@Override
		protected void fireWarningMessage(Map<String, String> messages) {
			AonMessagePanel.showWarning(messagePanel, messages);
		}
		
		@Override
		protected void fireInfoMessage(Map<String, String> messages) {
			AonMessagePanel.showInfo(messagePanel, messages);
		}

		@Override
		protected void fireLoadingMessage(String message) {
			AonMessagePanel.showLoading(messagePanel, message);
		}

		@Override
		protected void hideMessage() {
			AonMessagePanel.hideMessage(messagePanel);
		}

		@Override
		protected void showPDF(String dataURI, boolean isLaboralLife) {
			showPdf(isLaboralLife);
			pdfViewer.open(dataURI);
		}
		
	}

	// ----------------------------------------------- UiBinder
	
	interface Binder extends UiBinder<Widget, MainCCC> {}

	private static final Binder binder = GWT.create(Binder.class);
	
	// ----------------------------------------------- UiFields
	
	@UiField
	MyStyle style;

	interface MyStyle extends CssResource {
		String container();
		String widthAll();
	}
	
	@UiField
	DockLayoutPanel dockLayoutPanel;
	
	@UiField
	DeckPanel toolbarDeckPanel;
	
	@UiField(provided = true)
	AonToolbar toolbar;
	
	@UiField(provided = true)
	AonToolbar toolbarPDFViewer;
	
	@UiField 
	HTMLPanel messagePanel;
	
	@UiField
	DeckPanel mainDeckPanel;
	
	@UiField
	HTMLPanel mainPanel;
	
	@UiField
	SimpleLayoutPanel scrolledPDFPanel;

	@UiField
	FullViewer pdfViewer;
	
	// --------------------------------------------- Import Form
	
	private FormPanel uploadForm;
	private FileUpload fileUpload;

	// ----------------------------------------------- Variables
	
	private MainCCCObject mainCCCObject;
	
	private CCC cccWidget;
	
	private AonToolbarButton acceptButton;
	private AonToolbarButton undoAllButton;
	
	private boolean hasChange;
	
	// ----------------------------------------------- Constructor

	public MainCCC() {	
		cccWidget = new CCCWidgetImpl();
		
		this.toolbar = new AonToolbar("C\u00F3digo Cuentas Cotizaci\u00F3n");
		this.toolbarPDFViewer = new AonToolbar("C\u00F3digo Cuentas Cotizaci\u00F3n");
		
		GWT.<AonGwtTemplateResources>create(AonGwtTemplateResources.class).css().ensureInjected();
		AON.ensureInjected();
	
		Widget ui = binder.createAndBindUi(this);
		RootLayoutPanel.get(getRootPanel() != null ? getRootPanel() : "rootPanel").add(ui);
		
		showCCCs();
		
		getToolbarPanel();
		getToolbarPDFViewerPanel();
		
		dockLayoutPanel.addStyleName(style.container());
		
		scrolledPDFPanel.getElement().getStyle().setHeight(Window.getClientHeight() - 170.00, Unit.PX);
		
		mainPanel.add(cccWidget);
		mainPanel.addStyleName(style.widthAll());
		
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
					cccWidget.setDomain(mainCCCObject.getDomain());
					cccWidget.onInsertRows();
					cccWidget.calculateScrollPanelHeightMainCCC();
					setHasChange(false);
				}, f -> {});
	}
	
	// ------------------------------------------ Upload Form

	private void initUploadForm() {
	    uploadForm = new FormPanel();
	    uploadForm.setVisible(false);
	    uploadForm.setAction(IvlService.IVL_URL);
	    uploadForm.setMethod(FormPanel.METHOD_POST);
	    uploadForm.setEncoding(FormPanel.ENCODING_MULTIPART);
	    uploadForm.addSubmitCompleteHandler(e ->  cccWidget.fireInfoMessage(Collections.singletonMap("IVL", e.getResults())) );
	    
	    
	    FlowPanel formPanel = new FlowPanel();
	    
	    fileUpload = new FileUpload();
	    fileUpload.setName(IvlService.Parameter.FILE.name());
	    fileUpload.setVisible(false);
	    formPanel.add(fileUpload);
	    
	    formPanel.add(new Hidden(IvlService.Parameter.USER.name(), Wnd.getCurrentUser()));
	    formPanel.add(new Hidden(IvlService.Parameter.DOMAIN.name(), Wnd.getCurrentDomainNameURL()));
	    
	    
	    uploadForm.add(formPanel);
	    mainPanel.add(uploadForm);
	}
	
	private void uploadFile() {
	    fileUpload.click();
	    fileUpload.addChangeHandler( e -> {
		    cccWidget.fireLoadingMessage("Importando vida laboral ...");
		    uploadForm.submit();
	    });
	}
	
	// ----------------------------------------------- Toolbar

	private void getToolbarPanel() {
		
		acceptButton = new AonToolbarButton( AON.MSG.saveAction(), AON.CSS.aonIconSave() );
		acceptButton.addClickHandler(e -> onAccept());
		toolbar.add(acceptButton);
		
		undoAllButton = new AonToolbarButton( AON.MSG.undo() + " todo", AON.CSS.aonIconUndoAll() );
		undoAllButton.addClickHandler(e -> {
			AonDialog confirmDialog =  new AonDialog("Restaurar CCCs", new HTMLPanel("\u00bfDesea realmente deshacer los cambios realizados sobre las cuentas de cotizaci\u00f3n\u003f <br>Este proceso es irreversible."));
			confirmDialog.confirm(new AonAcceptDialogCallback() {
				
				@Override
				public void onCancel() {
					// Nothing to do
				}
				
				@Override
				public void onAccept() {
					mainCCCObject.getMainCCCInfo(
							s -> {
								cccWidget.setDomain(mainCCCObject.getDomain());
								cccWidget.resetPreview();
								cccWidget.onInsertRows();
								cccWidget.calculateScrollPanelHeightMainCCC();
								setHasChange(false);
							}, f -> {});
				}
			});
		});
		toolbar.add(undoAllButton);
		
		AonToolbarButton checkUpdateCert = new AonToolbarButton("Cert. de estar al corriente con TGSS", AON.CSS.aonIconTgss() );
		checkUpdateCert.addClickHandler(e -> onCheckUpdateCert());
		toolbar.add(checkUpdateCert);
		
		AonToolbarButton createCCCBtn = new AonToolbarButton(AON.MSG.newAction() + " CCC", AON.CSS.aonIconAdd() );
		createCCCBtn.addClickHandler(e -> cccWidget.onAddNewCCC());
		toolbar.add(createCCCBtn);
		
		AonToolbarButton importBtn = new AonToolbarButton(AON.MSG.importAction() , AON.CSS.aonIconUploadFile() );
		importBtn.addClickHandler(e -> uploadFile());
		toolbar.add(importBtn);
		
	}
	
	// ------------------------------------------------- Toolbar PDFViewer panel
	
	private AonToolbar getToolbarPDFViewerPanel() {

		AonToolbarButton closePDF = new AonToolbarButton(AON.MSG.closed(), AON.CSS.aonIconBack());
		closePDF.addClickHandler(e -> onClosePDF());
		toolbarPDFViewer.add(closePDF);
		
		return toolbarPDFViewer;
	}
	
	// ----------------------------------------------- Toolbar.Methods TGSS
	
	private void onAccept() {
		AonMessagePanel.showLoading(messagePanel, "Guardando CCCs ...");
		this.mainCCCObject.setMainCCCInfo(s -> {
			Map<String, String> successMap = new HashMap<>();
			successMap.put("Guardado", "CCCs guardados correctamente");
			AonMessagePanel.showSuccess(messagePanel, successMap);
			
			mainCCCObject.getMainCCCInfo(
					su -> {
						cccWidget.setDomain(mainCCCObject.getDomain());
						cccWidget.resetPreview();
						cccWidget.onInsertRows();
						cccWidget.calculateScrollPanelHeightMainCCC();
						setHasChange(false);
					}, f -> {});
		}, f -> {
			Map<String, String> errorMap = new HashMap<>();
			errorMap.put("Error Guardado", f.getMessage());
			AonMessagePanel.showError(messagePanel, errorMap);
		});
	}
	
	private void onCheckUpdateCert() {
		AonMessagePanel.showLoading(messagePanel, "Obteniendo Cert. de estar al corriente con TGSS ...");
		Pair<String, String> completeCCC = mainCCCObject.getPrincipalAccount();
		
		mainCCCObject.getUpdateCert(completeCCC.getKey(), completeCCC.getValue(),
				dataURI -> {
					showPdf(false);
					pdfViewer.open(dataURI);
					AonMessagePanel.hideMessage(messagePanel);
				}, f -> {
					Map<String, String> warningMap = new HashMap<>();
					warningMap.put("Error obtenci\u00f3n TGSS", f.getMessage());
					AonMessagePanel.showWarning(messagePanel, warningMap);
				});
	}
	
	// ------------------------------------------------- Show/Hide PDF

	private void showCCCs() {
		toolbarDeckPanel.showWidget(0);
		mainDeckPanel.showWidget(0);
	}

	private void showPdf(boolean isLaboralLife) {
		toolbarDeckPanel.showWidget(1);
		mainDeckPanel.showWidget(1);
		
		checkPDFToolbar(isLaboralLife);
	}
	
	private void checkPDFToolbar(boolean isLaboralLife) {
		if(isLaboralLife && toolbarPDFViewer.getButtonContainer().getWidgetCount() == 1) {
			MonthListBox monthListBox = new MonthListBox();
			Date lastMonth = DateUtils.getFirstDayOfMonth(); 
			Date firstMonth = DateUtils.addYears2Date(DateUtils.getFirstDayOfMonth(), -4);
			monthListBox.setFirstMonth(firstMonth);
			monthListBox.setLastMonth(lastMonth);
			monthListBox.setPageSize(52);
			monthListBox.setVisibleRange(0, 52);
			monthListBox.addChangeHandler(e -> this.cccWidget.onLaboralLife(monthListBox.getSelected()));
			monthListBox.setSelected(DateUtils.getFirstDayOfMonth(), true);
			monthListBox.setWidth("200px");
			toolbarPDFViewer.add(monthListBox);
			
			AonToolbarButton importPDF = new AonToolbarButton(AON.MSG.importAction(), AON.CSS.aonIconImport());
			importPDF.addClickHandler(e -> onImportIvl());
			toolbarPDFViewer.add(importPDF);

		} else if(!isLaboralLife && toolbarPDFViewer.getButtonContainer().getWidgetCount() > 1)
			toolbarPDFViewer.getButtonContainer().remove(toolbarPDFViewer.getButtonContainer().getWidgetCount()-1);
		
	}
	
	private void onClosePDF() {
		showCCCs();
	}
	
	
	private void onImportIvl() {
	    pdfViewer.getData(this::importIvl);
	}
	
	private void importIvl(String data) {
	    cccWidget.fireLoadingMessage("Importando Informe de Vida Laboral ...");
	    upload(data, 
		    message -> cccWidget.fireInfoMessage(Collections.singletonMap("IVL", message)), 
		    exception -> cccWidget.fireWarningMessage(Collections.singletonMap("IVL", exception.getMessage())) );
	}

	private void setHasChange(boolean hasChange) {
		this.hasChange = hasChange;
		acceptButton.setEnabled(this.hasChange);
		undoAllButton.setEnabled(this.hasChange);
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
	
    }
