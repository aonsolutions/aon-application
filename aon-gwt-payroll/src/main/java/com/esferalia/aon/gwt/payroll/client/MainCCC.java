package com.esferalia.aon.gwt.payroll.client;

import static com.esferalia.aon.watson.util.AonStringUtils.endsWith;
import static com.esferalia.aon.watson.util.AonStringUtils.trim;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.css.AonGwtTemplateResources;
import com.esferalia.aon.gwt.common.client.widget.MonthListBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog.AonAcceptDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessagePanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTableButton;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbar;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.gwt.payroll.shared.HttpException;
import com.esferalia.aon.gwt.payroll.shared.IvlService;
import com.esferalia.aon.gwt.payroll.shared.ProvinceContract;
import com.esferalia.aon.gwt.payroll.shared.SistemaREDService;
import com.esferalia.aon.gwt.payroll.shared.SistemaREDService.JsSistemaREDCCC;
import com.esferalia.aon.occam.api.model.EnterpriseCCC;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.esferalia.aon.watson.util.Pair;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.SuggestOracle.Request;
import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.xhr.client.XMLHttpRequest;

import net.aonsolutions.gwt.pdfjs.client.FullViewer;

public class MainCCC extends MainEntryPoint{
	
	// ----------------------------------------------- CCC Implementation
	
	private class CCCWidgetImpl extends CCC {
	    
	    	Label loadingLabel;

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
				mainCCCObject.getCCCs().forEach(CCCWidgetImpl.this::insertRow);
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
		protected <T> void fireWarningMessage(Map<String, T> messages) {
			AonMessagePanel.showWarning(messagePanel, messages);
		}
		
		@Override
		protected <T> void fireInfoMessage(Map<String, T> messages) {
			AonMessagePanel.showInfo(messagePanel, messages);
		}

		@Override
		protected <T> void fireLoadingMessage(T message) {
			loadingLabel = AonMessagePanel.showLoading(messagePanel, message);
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
		
		protected void setLoadingMessage(String text) {
			loadingLabel.setText(text);
		}

		protected void setLoadingMessage(SafeHtml html) {
			((HTML) loadingLabel).setHTML(html);
		}
	}
	public class CCCAssignedWidgetImpl extends CCCWidgetImpl {
	    
	    private int limit = 100;
	    
	    List<EnterpriseCCC> enterpriseCCCs ;
	    private MultiWordSuggestOracle cccSuggestOracle;
	    private MultiWordSuggestOracle enterpriseNameSuggestOracle;
	    
	    
	    public CCCAssignedWidgetImpl() {
		super();
		customizeHead();
	    }
	    
	    @Override
	    protected Set<Entry<Integer, String>> getActivities() {
	        return Collections.emptySet();
	    }
	    
	    @Override
	    public void insertRow(EnterpriseCCC enterpriseCCC) {
	        this.insertRowUI(enterpriseCCC);
	        
	        enterpriseCCCs.add(enterpriseCCC);
	        cccSuggestOracle.add(enterpriseCCC.getCcc());
	        enterpriseNameSuggestOracle.add(enterpriseCCC.getEnterpriseName());
	        
	        MainCCC.log(enterpriseCCC.getEnterpriseName() + ", " + enterpriseCCC.getCcc() );
	        
	    }
	    
	    private void insertRowUI(EnterpriseCCC enterpriseCCC) {
	        if ( cccDataTable.getRowCount() >= limit ) 
	            return;
	        super.insertRow(enterpriseCCC);
	        int row = cccDataTable.getRowCount() - 1;
	        Label enterpriseNamelabel = new Label(enterpriseCCC.getEnterpriseName());
	        cccDataTable.setWidget(row, 0, enterpriseNamelabel );
	    }
	    
	    
	    private void customizeHead() {
		enterpriseCCCs = new ArrayList<>();
		
		int row = cccDataTableHeader.getRowCount() -1;
		TextBox enterpriseNameTextBox = new TextBox();
		cccDataTableHeader.setWidget(row, 0, enterpriseNameTextBox);
		enterpriseNameSuggestOracle = new MultiWordSuggestOracle();
		enterpriseNameTextBox.getElement().getStyle().setWidth(95, Unit.PCT);
		
		HTMLPanel hPanel = new HTMLPanel("");
		hPanel.setStyleName(style.flexEvenly());
		hPanel.addStyleName(style.widthAll());
		Label typeLabel = new Label("0111");
		typeLabel.setVisible(false);
		AonTableButton cccButton = new AonTableButton("", AON.CSS.aonIconValid());
		cccButton.setVisible(false);
		TextBox cccTextBox = new TextBox();
		cccTextBox.addStyleName("aon-inputText");
		cccTextBox.addStyleName(style.inputTextHeight());
		cccTextBox.getElement().getStyle().setProperty("width", "65%");
		
		hPanel.add(typeLabel);
		hPanel.add(cccTextBox);
		hPanel.add(cccButton);

		cccDataTableHeader.setWidget(row, 2, hPanel);
		cccSuggestOracle = new MultiWordSuggestOracle();

		Timer cccTimer = newTimer(() -> filterByCCC(cccTextBox.getText()) );
		cccTextBox.addKeyUpHandler(e -> cccTimer.schedule(2000));
		Timer enterpriseNameTimer = newTimer(() -> filterByEnterpriseName(enterpriseNameTextBox.getText()));
		enterpriseNameTextBox.addKeyUpHandler(e -> enterpriseNameTimer.schedule(2000));
		
	    }
	    
	    
	    private void filterByCCC(String query ) {
		if (AonStringUtils.isBlank(query)) {
		    clearRows();
		    enterpriseCCCs.stream().limit(limit).forEach(this::insertRowUI);
		    return;
		}
		Request request = new Request(query, limit);
		cccSuggestOracle.requestSuggestions(request, (req, res) -> {
		    Set<String> cccs = res.getSuggestions().stream().map(Suggestion::getReplacementString)
			    .collect(Collectors.toSet());
		    clearRows();
		    cccs.stream()
		    .flatMap(this::getCCCByCCC)
		    .forEach(this::insertRowUI);
		});
	    }

	    private void filterByEnterpriseName(String query ) {
		if (AonStringUtils.isBlank(query)) {
		    clearRows();
		    enterpriseCCCs.stream().limit(limit).forEach(this::insertRowUI);
		    return;
		}
		Request request = new Request(query, limit);
		enterpriseNameSuggestOracle.requestSuggestions(request, (req, res) -> {
		    Set<String> enterprisesNames = res.getSuggestions().stream().map(Suggestion::getReplacementString)
			    .collect(Collectors.toSet());
		    clearRows();
		    
		    enterprisesNames.forEach( MainCCC::log);
		    enterprisesNames.stream().flatMap(this::getCCCByName).forEach(ccc -> MainCCC.log("  *" + ccc.getEnterpriseName() ));
		    
		    enterprisesNames.stream()
		    .flatMap(this::getCCCByName)
		    .forEach(this::insertRowUI);
		});
	    }
	    
	    private void clearRows() {
		while ( cccDataTable.getRowCount() > 0 )
		    cccDataTable.removeRow(0);
	    }
	    
	    private Timer newTimer(Runnable runnable) {
		return new Timer() {
		  @Override
		    public void run() {
		      runnable.run();
		    }  
		};
	    }

	    private Stream<EnterpriseCCC> getCCCByName(String enterpriseName) {
		return enterpriseCCCs.stream()
		.filter(ccc -> AonStringUtils.equals(ccc.getEnterpriseName(), enterpriseName))
		.limit(1);
	    }
	    
	    private Stream<EnterpriseCCC> getCCCByCCC(String ccc) {
		return enterpriseCCCs.stream()
		.filter(enterpriseCCC -> AonStringUtils.equalsIgnoreCase(enterpriseCCC.getCcc(), ccc))
		.limit(1);
	    }
	    
	    private  int getEnterpriseCCCCount() {
		return enterpriseCCCs.size();
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
	HTMLPanel mainHtmlPanel;
	
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
	HTMLPanel activityCCCPanel;
	
	@UiField
	HTMLPanel assignedCCCPanel;

	@UiField
	SimpleLayoutPanel scrolledPDFPanel;

	@UiField
	FullViewer pdfViewer;
	
	@UiField
	TabLayoutPanel cccTabLayoutPanel;
	
	@UiField
	SimpleLayoutPanel mainCCCPanel;
	
	// --------------------------------------------- Import Form
	
	private FormPanel uploadForm;
	private FileUpload fileUpload;

	// ----------------------------------------------- Variables
	
	private MainCCCObject mainCCCObject;
	
	private CCCWidgetImpl activityCCCWidget;
	private CCCAssignedWidgetImpl assignedCCCWidget;
	
	private AonToolbarButton acceptButton;
	private AonToolbarButton undoAllButton;
	
	private boolean hasChange;
	
	// ----------------------------------------------- Constructor

	public MainCCC() {	
		activityCCCWidget = new CCCWidgetImpl();
		assignedCCCWidget = new CCCAssignedWidgetImpl();
		
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
		
		mainCCCPanel.getElement().getStyle().setHeight(Window.getClientHeight() - 170.00, Unit.PX);
		scrolledPDFPanel.getElement().getStyle().setHeight(Window.getClientHeight() - 170.00, Unit.PX);
		
		activityCCCPanel.add(activityCCCWidget);
		activityCCCPanel.addStyleName(style.widthAll());
		
		assignedCCCPanel.add(assignedCCCWidget);
		assignedCCCPanel.addStyleName(style.widthAll());
		
		List<HandlerRegistration> tabLayoutHadlers = new ArrayList<>();
		tabLayoutHadlers.add( 
		cccTabLayoutPanel.addSelectionHandler( e -> {
		    if ( e.getSelectedItem() == 0 ) {
			return;
		    }
		    if ( assignedCCCWidget.getRowCount() > 0 ) { 
			return;
		    }
		    assignedCCCWidget.calculateScrollPanelHeightMainCCC();
		    assignedCCCWidget.fireLoadingMessage(toSafeHtml("Consultando CCCs asignados."));
		    getAssignedCCCs(this::onAssignedCCCSuccess,this::onAssignedCCCFailure, this::onAssignedCCCDone);
		    tabLayoutHadlers.forEach(HandlerRegistration::removeHandler);
		}));

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
					activityCCCWidget.setDomain(mainCCCObject.getDomain());
					activityCCCWidget.onInsertRows();
					activityCCCWidget.calculateScrollPanelHeightMainCCC();
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
	    mainHtmlPanel.add(uploadForm);
	}
	
	private void uploadFile() {
	    fileUpload.click();
	    fileUpload.addChangeHandler( e -> {
		    activityCCCWidget.fireLoadingMessage(toSafeHtml("Importando <b>Informe Vida Laboral de un C&oacute;digo de Cuenta de Cotizaci&oacute;n</b>..."));
		    uploadForm.submit();
	    });
	}

	/**
	 * @return
	 */
	private SafeHtml toSafeHtml(String html) {
	    return new SafeHtmlBuilder().appendHtmlConstant(html).toSafeHtml();
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
								activityCCCWidget.setDomain(mainCCCObject.getDomain());
								activityCCCWidget.resetPreview();
								activityCCCWidget.onInsertRows();
								activityCCCWidget.calculateScrollPanelHeightMainCCC();
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
		createCCCBtn.addClickHandler(e -> activityCCCWidget.onAddNewCCC());
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
						activityCCCWidget.setDomain(mainCCCObject.getDomain());
						activityCCCWidget.resetPreview();
						activityCCCWidget.onInsertRows();
						activityCCCWidget.calculateScrollPanelHeightMainCCC();
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
			monthListBox.addChangeHandler(e -> this.activityCCCWidget.onLaboralLife(monthListBox.getSelected()));
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
	    activityCCCWidget.fireLoadingMessage(toSafeHtml("Importando <b>Informe Vida Laboral de un C&oacute;digo de Cuenta de Cotizaci&oacute;n</b>..."));
	    upload(data, 
		    message -> activityCCCWidget.fireInfoMessage(Collections.singletonMap("IVL", toSafeHtml(message))), 
		    exception -> activityCCCWidget.fireWarningMessage(Collections.singletonMap("IVL", exception.getMessage())) );
	}

	private void setHasChange(boolean hasChange) {
		this.hasChange = hasChange;
		acceptButton.setEnabled(this.hasChange);
		undoAllButton.setEnabled(this.hasChange);
	}
	
	private void onAssignedCCCDone() {
	    assignedCCCWidget.hideMessage();
	}
	
	private void onAssignedCCCFailure(HttpException exception) {
	    assignedCCCWidget.fireWarningMessage(Collections.singletonMap(SistemaREDService.ASSIGNED_CCCS, exception.getMessage()));
	}

	private void onAssignedCCCSuccess(EnterpriseCCC enterpriseCCC) {
	    assignedCCCWidget.insertRow(enterpriseCCC);
	    assignedCCCWidget.setLoadingMessage(toSafeHtml(enterpriseCCC.getEnterpriseName() + " <b>" + enterpriseCCC.getCcc() + "</b> (" + assignedCCCWidget.getEnterpriseCCCCount() + " CCCs )" ));
	}

	protected static void getAssignedCCCs(Consumer<EnterpriseCCC> onSuccess, Consumer<HttpException> onFailure,  Runnable onDone) {
	    
	    Set<String> assignedCCCs = new HashSet<>();
	    
	    XMLHttpRequest xmlHttpRequest = XMLHttpRequest.create();
	    
	    xmlHttpRequest.setOnReadyStateChange(xhr -> {

		int state = xhr.getReadyState();
		
		String text = xhr.getResponseText();
		if ( AonStringUtils.isNotEmpty(text)) {
		    String json ; 
		    if ( endsWith(trim(text), "]") ) {
			json = text;
		    } else {
			json = text + "]";
		    }
		    JsArray<JsSistemaREDCCC> jsCCCs = JsonUtils.safeEval(json);
		    for (int i = 0; i < jsCCCs.length(); i++) {
			JsSistemaREDCCC jsCCC = jsCCCs.get(i);
			EnterpriseCCC enterpriseCCC = new EnterpriseCCC();
			enterpriseCCC.setType((byte)0);
			enterpriseCCC.setCcc(jsCCC.getCCC());
			enterpriseCCC.setGeozoneCode(jsCCC.getProvince());
			enterpriseCCC.setEnterpriseName(jsCCC.getEnterpriseName());
			enterpriseCCC.setGeozoneDescription(ProvinceContract.getName(jsCCC.getProvince()));
			if ( assignedCCCs.add(jsCCC.getHashCode()) ) {
			    onSuccess.accept(enterpriseCCC);
			}
		    }
		}

		if (state != XMLHttpRequest.DONE )
			return;
		
		int status = xhr.getStatus();
		// Failure 1xx, 3xx, 4xx, 5xxx 
		if (status < 200 && status >= 300)
			onFailure.accept(new HttpException(status, xhr.getResponseText()));
		
		onDone.run();
		
	    });


	    Map<String, String> datas = new HashMap<>();
	    datas.put(SistemaREDService.Parameter.USER.name(), Wnd.getCurrentUser());
	    datas.put(SistemaREDService.Parameter.DOMAIN.name(), Wnd.getCurrentDomainNameURL());
	    
	    String queryString = datas.entrySet().stream().map(e -> e.getKey() + "=" + e.getValue()).collect(Collectors.joining("&"));
	    
	    xmlHttpRequest.open("GET", SistemaREDService.SISTEMA_RED_URL + "/" + SistemaREDService.ASSIGNED_CCCS + "?" + queryString);
	    xmlHttpRequest.send();

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
