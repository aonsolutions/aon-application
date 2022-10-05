package com.esferalia.aon.gwt.fiscal.client.console;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.logging.Logger;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.AsyncCallbackWrapper;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCloseTab;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonConfirmDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonLayoutPanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessageDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMinimizePanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTabLayoutPanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbar;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.gwt.fiscal.client.console.ConsoleDomainTable.ConsoleDomainTableCallback;
import com.esferalia.aon.gwt.fiscal.client.console.ConsoleDomainTableRow.DeleteAsyncCallback;
import com.esferalia.aon.gwt.fiscal.shared.IRequestParamsNames;
import com.esferalia.aon.gwt.fiscal.shared.JsonParams;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.DomainParams;
import com.esferalia.aon.occam.api.model.console.ConsoleMessageType;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.mutable.MutableInt;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.http.client.URL;
import com.google.gwt.logging.client.ConsoleLogHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.xhr.client.ReadyStateChangeHandler;
import com.google.gwt.xhr.client.XMLHttpRequest;
 
public class ConsoleDomainModule extends AonLayoutPanel {


	private static final String AVISO = "AVISO";
	private static final String APPLICATION_X_WWW_FORM_URLENCODED = "application/x-www-form-urlencoded";
	private static final String CONTENT_TYPE = "Content-type";
	private static final String DOMAIN_STREAM_SERVLET = URL.encode(GWT.getModuleBaseURL() + "roms/ConsoleDomainFlatStreamServlet");
	private static final String CHECK_DOMAIN_INTEGRITY_SERVLET = URL.encode(GWT.getModuleBaseURL() + "roms/ConsoleDomainCheckIntegrityServlet");
	private static final String DOMAIN_DELETE_SERVLET = URL.encode(GWT.getModuleBaseURL() + "roms/ConsoleDomainDeleteServlet");
	private static final String DOMAIN_REPORT_EXCEL_PRINT = "/aon_gwt_fiscal/roms/ConsoleDomainReportExcelPrint";
	private static final String DOMAIN_INFO_REPORT_EXCEL_PRINT = "/aon_gwt_fiscal/roms/ConsoleDomainInfoReportExcelPrint";
	private static final String DOMAIN_ISOLATE_SERVLET = URL.encode(GWT.getModuleBaseURL() + "roms/ConsoleDomainIsolateServlet");

	private static final Logger LOGGER = Logger.getLogger(ConsoleDomainModule.class.getName());
	static {
		LOGGER.addHandler( new ConsoleLogHandler() );
	}
	
	private SplitLayoutPanel splitLayoutPanel;
	private AonMinimizePanel footPanel;
	private AonTabLayoutPanel tabLayout;
	private ConsoleModuleOptions options;
	private LinkedHashMap<Integer,ConsoleDomainTableRow> checkedList = new LinkedHashMap<>();	
	private SimpleLayoutPanel container = new SimpleLayoutPanel();
	private ConsoleDomainFilterPanel filterPanel;
	private InlineLabel runningLabel = new InlineLabel("Ejecutando");
	private AonToolbarButton deleteButton = new AonToolbarButton(AON.MSG.deleteAction(),AON.CSS.aonIconDelete());
	private AonToolbarButton extractButton = new AonToolbarButton("Duplicar",AON.CSS.aonIconCopy());
	private FormPanel diskForm;
	private Hidden domainParamsHidden;

	
	private int lastScrollPos = 0;
	private final MutableInt offset = new MutableInt(0);
	private final MutableInt moreData = new MutableInt(0);
	private final MutableInt searchEnabled = new MutableInt( 0 );

	private int count = 0;
	private boolean processRunning;

	class ConsoleDomainTableCallbackImpl implements ConsoleDomainTableCallback {
		@Override
		public String getSchema() {
			return ConsoleDomainModule.this.filterPanel.getSchema();			
		}
		@Override
		public String[] getSchemas() {
			return ConsoleDomainModule.this.filterPanel.getSchemas();			
		}
		@Override
		public int addCount() {
			return ++count;
		}
		@Override
		public boolean isRunning() {
			return processRunning;
		}
		@Override
		public void setRunning(boolean run) {
			processRunning = run;
			runningLabel.setVisible(processRunning);
		}
		@Override
		public void showError(String message) {
			ConsoleDomainModule.this.showErrorPanel(message);			
		}
		@Override
		public void showInfo(String message) {
			ConsoleDomainModule.this.showInfoPanel(message);			
		}
		
		// -----------------------------------------------------------------------
		// 												  				    [INFO]
		// -----------------------------------------------------------------------
		@Override
		public void onInfo(Integer domainId) {
			DomainParams params = filterPanel.getParams(options);
			info(params.getSchema(), domainId);
		}

		private void info(String schema,Integer domainId) {
			DomainParams params = new DomainParams()
				.setSchema(schema)
				.setId(domainId);
			if (!AonStringUtils.isBlank(params.getSchema())) {
				diskForm.setAction(GWT.getHostPageBaseURL() + DOMAIN_INFO_REPORT_EXCEL_PRINT);
				domainParamsHidden.setValue(JsonParams.convert(params));
				diskForm.submit();
			} else {
				AonMessageDialog.show(AVISO, "Seleccione un esquema");
			}
			
		}

		// -----------------------------------------------------------------------
		// 												  				  [DELETE]
		// -----------------------------------------------------------------------
		@Override
		public void onMultipleDelete() {
			String tabLabel = "Multiple";
			final MutableInt processes = new MutableInt(checkedList.size()); 
			setRunning(true);
			checkedList.values()
				.stream()
				.forEach( row -> row.doDelete( tabLabel, new AsyncCallback<Boolean>() {
					DeleteAsyncCallback cbk = new DeleteAsyncCallback(row,false);
					
					@Override
					public void onFailure(Throwable caught) {
						cbk.onFailure(caught);
						finish();
					}

					@Override
					public void onSuccess(Boolean result) {
						cbk.onSuccess(result);
						finish();
					}

					private void finish() {
						processes.add(-1);
						if (processes.getValue() == 0) {
							setRunning(false);				
						}
					}
					
					
					
				}));

		}
		
		@Override
		public void onDelete(Integer domainId, String tabLabel, AsyncCallback<Boolean> cbk) {
			DomainParams params = filterPanel.getParams(options);
			deleteDomain(params.getSchema(), domainId, tabLabel, cbk);
			
		}

		private void deleteDomain(String schema,Integer domainId, String tabLabel, AsyncCallback<Boolean> cbk) {
			try {
				AonConsoleProgress tabWidget = (AonConsoleProgress) tabLayout.getWidget(tabLabel);
				if (tabWidget == null) {
					tabWidget = new AonConsoleProgress();
					AonCloseTab closeTab = new AonCloseTab(tabLabel, true);
					closeTab.addCloseHandler(e -> {if (!isRunning()) tabLayout.remove(tabLabel);});
					tabLayout.add(tabWidget, closeTab, tabLabel);
				}
				final AonConsoleProgress aonConsole = tabWidget; 
				tabLayout.selectTab(aonConsole);
				openFootPanelIfNeeded();
				
				XMLHttpRequest xhreq = XMLHttpRequest.create();
				xhreq.open(FormPanel.METHOD_POST, DOMAIN_DELETE_SERVLET);
				xhreq.setRequestHeader(CONTENT_TYPE,APPLICATION_X_WWW_FORM_URLENCODED);
				xhreq.setOnReadyStateChange(  new ConsoleReadyStateChangeHandler( aonConsole, cbk));
				StringBuilder requestData = new StringBuilder();
				DomainParams params = filterPanel.getParams(options).setId(domainId);
				requestData.append("&"+IRequestParamsNames.DOMAIN_PARAMS +"=" + JsonParams.convert(params));
				xhreq.send(requestData.toString());
			} catch (Exception e){
				cbk.onFailure(e);
			}
		}

		// -----------------------------------------------------------------------
		// 												  		   [CHANGE ACTIVE]
		// -----------------------------------------------------------------------
		@Override
		public void onChangeActive(Integer domainId, boolean active, AsyncCallback<Domain> cbk) {
			DomainParams params = filterPanel.getParams(options);
			ConsoleModule.CONSOLE_SERVICE.changeActive(params,domainId,active,new AsyncCallbackWrapper<>( cbk ));
		}
		
		// -----------------------------------------------------------------------
		// 												  [CHANGE EXPIRATION DATE]
		// -----------------------------------------------------------------------
		@Override
		public void onChangeExpirationDate(Integer domainId, Date expireDate, AsyncCallback<Domain> cbk) {
			DomainParams params = filterPanel.getParams(options);
			ConsoleModule.CONSOLE_SERVICE.changeExpirationDate(params,domainId,expireDate,new AsyncCallbackWrapper<>( cbk ));
		}
		
		// -----------------------------------------------------------------------
		// 												  				[VALIDATE]
		// -----------------------------------------------------------------------
		@Override
		public void onValidate(Integer domainId, String name, String description, AsyncCallback<Boolean> cbk) {
			DomainParams params = filterPanel.getParams(options);
			validate(params.getSchema(), domainId, name, description,cbk);
		}

		private void validate(String schema,Integer domainId, String name, String description, AsyncCallback<Boolean> cbk) {
			try {
				AonConsoleWidget aonConsole = new AonConsoleWidget();
				String tabLabel = AonStringUtils.abbreviate(description, 30);
				AonCloseTab closeTab = new AonCloseTab(tabLabel, true);
				tabLayout.add(aonConsole, closeTab, tabLabel);
				closeTab.addCloseHandler(e -> {if (!isRunning()) tabLayout.remove(tabLabel);});
				tabLayout.selectTab(aonConsole);
				openFootPanelIfNeeded();
				
				XMLHttpRequest xhreq = XMLHttpRequest.create();
				xhreq.open(FormPanel.METHOD_POST, CHECK_DOMAIN_INTEGRITY_SERVLET);
				xhreq.setRequestHeader(CONTENT_TYPE,APPLICATION_X_WWW_FORM_URLENCODED);
				xhreq.setOnReadyStateChange( xhr -> {
					int state = xhr.getReadyState();
					if (state == XMLHttpRequest.LOADING || state == XMLHttpRequest.DONE) {
						String text = xhr.getResponseText();
						aonConsole.log(text);
					}
					if (state == XMLHttpRequest.DONE) {
						cbk.onSuccess( true );
					}
				});
				StringBuilder requestData = new StringBuilder();
				requestData.append("&"+IRequestParamsNames.SCHEMA  				+"=" + schema );
				requestData.append("&"+IRequestParamsNames.DOMAIN_NAME  		+"=" + name );
				requestData.append("&"+IRequestParamsNames.DOMAIN_ID  			+"=" + domainId );
				requestData.append("&"+IRequestParamsNames.USER					+"=" + options.getUser() );
				xhreq.send(requestData.toString());
			} catch (Exception e){
				cbk.onFailure(e);		
			}
		}
		
		// -----------------------------------------------------------------------
		// 												  				   [CHECK]
		// -----------------------------------------------------------------------
		@Override
		public void check(com.esferalia.aon.gwt.fiscal.client.console.ConsoleDomainTableRow row) {
			updateChecks(row);			
		}
		
		// -----------------------------------------------------------------------
		// 												  		   [REMOTE ACCESS]
		// -----------------------------------------------------------------------
		@Override
		public void onRemoteAccess(Integer domainId, AsyncCallback<String> cbk) {
			DomainParams params = filterPanel.getParams(options);
			ConsoleModule.CONSOLE_SERVICE.remoteAccess(params,domainId, new AsyncCallbackWrapper<>( cbk ));
		}
		
		// -----------------------------------------------------------------------
		// 												  		   	   [DUPLICATE]
		// -----------------------------------------------------------------------
		public void onDuplicate(DomainParams origin, DomainParams target ) {
			try {
				setRunning(true);
				String tabLabel = AonStringUtils.abbreviate(origin.getDescription(), 30);
				AonConsoleProgress tabWidget = (AonConsoleProgress) tabLayout.getWidget(tabLabel);
				if (tabWidget == null) {
					tabWidget = new AonConsoleProgress();
					AonCloseTab closeTab = new AonCloseTab(tabLabel, true);
					closeTab.addCloseHandler(e -> {if (!isRunning()) tabLayout.remove(tabLabel);});
					tabLayout.add(tabWidget, closeTab, tabLabel);
				}
				final AonConsoleProgress aonConsole = tabWidget; 
				tabLayout.selectTab(aonConsole);
				openFootPanelIfNeeded();
				
				XMLHttpRequest xhreq = XMLHttpRequest.create();
				xhreq.open(FormPanel.METHOD_POST, DOMAIN_ISOLATE_SERVLET);
				xhreq.setRequestHeader(CONTENT_TYPE,APPLICATION_X_WWW_FORM_URLENCODED);
				xhreq.setOnReadyStateChange(  new ConsoleReadyStateChangeHandler( aonConsole , new AsyncCallback<Boolean>() {

					@Override
					public void onFailure(Throwable caught) {
						setRunning(false);
					}

					@Override
					public void onSuccess(Boolean result) {
						setRunning(false);
					}
					
				}));
				StringBuilder requestData = new StringBuilder();
				requestData.append("&"+IRequestParamsNames.SCHEMA  				+"=" + origin.getSchema() );
				requestData.append("&"+IRequestParamsNames.DOMAIN_NAME			+"=" + origin.getName() );
				requestData.append("&"+IRequestParamsNames.DOMAIN_ID  			+"=" + origin.getId() );
				
				requestData.append("&"+IRequestParamsNames.NEW_SCHEMA  			+"=" + target.getSchema() );
				requestData.append("&"+IRequestParamsNames.NEW_DOMAIN_NAME		+"=" + target.getName() );
				requestData.append("&"+IRequestParamsNames.USER					+"=" + options.getUser() );
				requestData.append("&"+IRequestParamsNames.VALIDATE				+"=" + Boolean.toString( origin.isValidate() ));
				requestData.append("&"+IRequestParamsNames.MUST_FLATTEN			+"=" + Boolean.toString( origin.mustFlatten() ));
				xhreq.send(requestData.toString());
			} catch (Exception e){
				setRunning(false);
				showError("No se pudo duplicar el dominio. " + e.getMessage());
			}
		}
	}

	public ConsoleDomainModule(ConsoleModuleOptions options) {
		this.options = options;
		AON.ensureInjected();
		this.addNorth(getToolbarPanel(options), AonToolbar.HEIGTH);
		filterPanel = new ConsoleDomainFilterPanel(options);
		filterPanel.addValueChangeHandler(e -> search(e.getValue()) );
		this.addNorth(filterPanel, ConsoleDomainFilterPanel.HEIGTH);

		splitLayoutPanel = new SplitLayoutPanel( 2 );
		splitLayoutPanel.addSouth(getMinimizePanel(), 30);
		this.add(splitLayoutPanel);
		
		splitLayoutPanel.add(container);
	}
	
	private void disableMoreData() {
		moreData.setValue(-1);
	}
	private void enableMoreData() {
		moreData.setValue(0);
	}
	private boolean isMoreData() {
		return AonNumberUtils.equals(moreData.getValue(),0);
	}
	private void enableSearch() {
		searchEnabled.setValue(0);
	}
	private boolean isSearchEnabled() {
		return AonNumberUtils.equals(searchEnabled.getValue(),0);
	}
	private void disableSearch() {
		searchEnabled.setValue(-1);
	}

	private AonMinimizePanel getMinimizePanel() {
		footPanel = new AonMinimizePanel();
		footPanel.addMinimizeHandler( event -> closeFootPanel() );
		footPanel.addMaximizeHandler( event -> maximizeFootPanel());
		footPanel.setStyleName(AON.CSS.aonSelector());
		tabLayout = new AonTabLayoutPanel(26, Unit.PX);
		tabLayout.setWidth("100%");
		footPanel.setWidget(tabLayout);
		
		tabLayout.setAnimationDuration(300);
		tabLayout.addSelectionHandler( event -> openFootPanelIfNeeded());
		return footPanel; 
	}
	
	private void closeFootPanel() {
		splitLayoutPanel.setWidgetSize(footPanel, 30);
		splitLayoutPanel.animate(500);
	}
	private void openFootPanel() {
		splitLayoutPanel.setWidgetSize(footPanel, Window.getClientHeight() / 4.0);
		splitLayoutPanel.animate(500);
	}
	private void openFootPanelIfNeeded() {
		if (splitLayoutPanel.getWidgetSize(footPanel) <= 50) {
			openFootPanel();
		}
	}
	private void maximizeFootPanel() {
		splitLayoutPanel.setWidgetSize(footPanel, Window.getClientHeight() / 2.0);
		splitLayoutPanel.animate(500);
	}
	
	private AonToolbar getToolbarPanel( ConsoleModuleOptions options) {
		AonToolbar toolbar = new AonToolbar();
		toolbar.setTitle("Gesti\u00F3n de dominios");
		
		diskForm = new FormPanel("_blank");
		diskForm.setMethod(FormPanel.METHOD_POST);
		domainParamsHidden = new Hidden(IRequestParamsNames.DOMAIN_PARAMS);
		FlowPanel formFlowPanel = new FlowPanel();
		diskForm.add(formFlowPanel);
		formFlowPanel.add(domainParamsHidden);
		toolbar.add(diskForm);

		AonToolbarButton exportButton = new AonToolbarButton( AON.MSG.export(), AON.CSS.aonIconExcel() );
		exportButton.addClickHandler(event -> {
			DomainParams params = filterPanel.getParams(options);
			if (!AonStringUtils.isBlank(params.getSchema())) {
				diskForm.setAction(GWT.getHostPageBaseURL() + DOMAIN_REPORT_EXCEL_PRINT);
				domainParamsHidden.setValue(JsonParams.convert(params));
				diskForm.submit();
			} else {
				AonMessageDialog.show(AVISO, "Seleccione un esquema");
			}
		});
		toolbar.add(exportButton);
		
		deleteButton.setEnabled(false);
		deleteButton.addClickHandler(e -> {
			ConsoleDomainTableCallbackImpl innerCallback = new ConsoleDomainTableCallbackImpl();
			if (canRunElseNotify()) {
				AonConfirmDialog.showConfirm("\u00A1\u00A1ESTE PROCESO ES IRREVERSIBLE!!"
					,"Se van a borrar " + checkedList.size() + " dominios!"
					, innerCallback::onMultipleDelete);
			}
		});
		
		extractButton.setEnabled(false);
		extractButton.addClickHandler(e -> duplicateDomain());
		
		toolbar.add(deleteButton);
		toolbar.add(extractButton);
		runningLabel.setVisible(false);
		runningLabel.setStyleName(AON.CSS.aonMarginLeft());
		runningLabel.addStyleName(AON.CSS.aonColorWhite());
		runningLabel.addStyleName(AON.CSS.aonBizkaiaBackgroundColor());
		runningLabel.addStyleName(AON.CSS.aonPadding());
		runningLabel.addStyleName(AON.CSS.aonBold());
		toolbar.add(runningLabel);
		return toolbar;
	}
	
	private boolean canRun() {
		return !processRunning;
	}
	private boolean canRunElseNotify() {
		if (!canRun()) {
			AonMessageDialog.show("AVISO","Hay una proceso ejecut\u00E1ndose. Un momento, por favor.");
			return false;
		}
		return true;
	}
	
	
	private void search(DomainParams params) {
		container.clear();
		ScrollPanel scrollPanel = new ScrollPanel();
		scrollPanel.setStyleName(AON.CSS.aonScrollArea());
		container.setWidget(scrollPanel);
		ConsoleDomainTable grid = getTable();
		scrollPanel.setWidget(grid);
		
		scrollPanel.addScrollHandler(event -> {
			// ------------------------------------ Ignore scroll up.
			int oldScrollPos = lastScrollPos;
			lastScrollPos = scrollPanel.getVerticalScrollPosition();
			if (oldScrollPos >= lastScrollPos) {
				return;
			}
			// -----------------------------------------------------
			LOGGER.info("Scroll event: ("+ isSearchEnabled() +")");
			if (isSearchEnabled()) {
				int maxScrollTop = scrollPanel.getWidget().getOffsetHeight() - getOffsetHeight();
				if (lastScrollPos >= maxScrollTop) {
					LOGGER.info("Scroll event: ( search )");
					disableSearch();
					search(params,grid);
				}
			}
		});
		enableMoreData();
		offset.setValue(0);
		disableSearch();
		search(params, grid);
	}

	private void search(DomainParams params, ConsoleDomainTable grid) {
		if (!isMoreData()) return;
		ConsoleDomainTableCallbackImpl innerCallback = new ConsoleDomainTableCallbackImpl();
		XMLHttpRequest xhr = XMLHttpRequest.create();
		xhr.open(FormPanel.METHOD_POST, DOMAIN_STREAM_SERVLET);
		xhr.setRequestHeader(CONTENT_TYPE,APPLICATION_X_WWW_FORM_URLENCODED);
		xhr.setOnReadyStateChange(xhreq -> {
			int state = xhreq.getReadyState();
			LOGGER.info("START onReadyStateChange: ("+ state +")");
			boolean something = false;
			if (state == XMLHttpRequest.DONE) {
				String text = xhreq.getResponseText();
				int x = 0;
				if (!JsonUtils.safeToEval(text)) {
					Window.alert("ERROR de evaluación");
				}
				JavaScriptObject unk = JsonUtils.safeEval(text);
				JsArray<JsConsoleDomain> array = unk.cast();
				for (; x < array.length(); x++ ) {
					JsConsoleDomain domain = array.get(x);
					grid.addRow(innerCallback,domain);
					something = true;
				}
				if (x > 0) {
					offset.setValue( params.getOffset() + x);
					enableMoreData();
					LOGGER.info("onReadyStateChange (" + x + ") : offset " + offset.getValue() + " enableMoreData");
				}
			
				if (!something) {
					FlowPanel line = new FlowPanel();
					InlineLabel label = new InlineLabel(AON.MSG.noData());
					line.add(label);
					grid.add(line);
					disableMoreData();
					LOGGER.info("onReadyStateChange (disableMoreData)");
				}
				enableSearch();
				LOGGER.info("onReadyStateChange (enableSearch)");
			}
		});
		count = 0;
		StringBuilder requestData = new StringBuilder();
		params.setOffset(offset.getValue());
		requestData.append("&"+IRequestParamsNames.DOMAIN_PARAMS +"=" + JsonParams.convert( params ) );
		xhr.send(requestData.toString());
	}
	
	private ConsoleDomainTable getTable() {
		checkedList.clear();
		ConsoleDomainTable table = new ConsoleDomainTable( );
		table.addSelectionHandler(e -> updateChecks( e.getSelectedItem() ));
		return table;
	}
	
	private void updateChecks(ConsoleDomainTableRow row) {
		if (row != null) {
			if (checkedList.containsKey(row.getId())) {
				checkedList.remove(row.getId());
			} else {
				checkedList.put(row.getId(), row);
			}
			deleteButton.setEnabled( !checkedList.isEmpty() );
			extractButton.setEnabled( checkedList.size() == 1 );
		}
	}

	private Object duplicateDomain() {
		// TODO Auto-generated method stub
		return null;
	}

	private class ConsoleReadyStateChangeHandler implements ReadyStateChangeHandler {
		
		private AonConsoleProgress aonConsole;
		private AsyncCallback<Boolean> cbk;
		private int loaded = 0;
		private boolean hasError = false;
		private String errorMessage = null;
		
		private ConsoleReadyStateChangeHandler( AonConsoleProgress aonConsole, AsyncCallback<Boolean> cbk ) {
			this.aonConsole = aonConsole;
			this.cbk = cbk;
		}
		
		@Override
		public void onReadyStateChange(XMLHttpRequest xhr) {
			int state = xhr.getReadyState();
			if (state == XMLHttpRequest.LOADING || state == XMLHttpRequest.DONE) {
				String text = xhr.getResponseText();
				try {
					for (JsConsoleMessage msg = read(text); text != null; msg = read(text)) {
						aonConsole.log(msg);
						if (!hasError && msg.getType() == ConsoleMessageType.ERROR ) {
							errorMessage = msg.getMessage();
							hasError = true; 
						}
					}
					
				} catch (IndexOutOfBoundsException e) {
				}
			}
			if (state == XMLHttpRequest.DONE) {
				if (cbk != null && hasError) {
					cbk.onFailure(new AonCoreException(errorMessage));
				} else {
					cbk.onSuccess(true);
				}
			}
		}

		private JsConsoleMessage read(String text) {
			for (int begin = loaded; begin < text.length(); begin++) {
				if (text.charAt(begin) == '{') {
					loaded = findEnd(text, begin + 1) + 1;
					String json = text.substring(begin, loaded);
					return JsonUtils.safeEval(json);
				}
			}
			throw new IndexOutOfBoundsException();
		}

		private int findEnd(String text, int start) {
			for (int end = start; end < text.length(); end++) {
				switch (text.charAt(end)) {
				case '}':
					return end;
				case '{':
					end = findEnd(text, end + 1);
				}
			}
			throw new IndexOutOfBoundsException();
		}
	}
	
}

