package com.esferalia.aon.gwt.fiscal.client.console;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.logging.Logger;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.AsyncCallbackWrapper;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCloseTab;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonConfirmDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomPopup;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayGrid;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonLayoutPanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessageDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMinimizePanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTabLayoutPanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbar;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.gwt.fiscal.client.console.ConsoleDomainTable.ConsoleDomainTableCallback;
import com.esferalia.aon.gwt.fiscal.client.console.ConsoleDomainTableRow.DeleteAsyncCallback;
import com.esferalia.aon.gwt.fiscal.client.finance.utilities.FinanceUtilitiesModuleOptions;
import com.esferalia.aon.gwt.fiscal.client.finance.utilities.FinanceUtilitiesModulePanel;
import com.esferalia.aon.gwt.fiscal.shared.IRequestParamsNames;
import com.esferalia.aon.gwt.fiscal.shared.JsonParams;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.DomainParams;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.console.ConsoleMessageType;
import com.esferalia.aon.occam.api.model.console.ConsoleSchema;
import com.esferalia.aon.occam.api.model.security.User;
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
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.logging.client.ConsoleLogHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.xhr.client.ReadyStateChangeHandler;
import com.google.gwt.xhr.client.XMLHttpRequest;
 
public class ConsoleDomainPanel extends AonLayoutPanel {


	private static final String AVISO = "AVISO";
	private static final String APPLICATION_X_WWW_FORM_URLENCODED = "application/x-www-form-urlencoded";
	private static final String CONTENT_TYPE = "Content-type";
	private static final String DOMAIN_STREAM_SERVLET = URL.encode(GWT.getModuleBaseURL() + "roms/ConsoleDomainFlatStreamServlet");
	private static final String CHECK_DOMAIN_INTEGRITY_SERVLET = URL.encode(GWT.getModuleBaseURL() + "roms/ConsoleDomainCheckIntegrityServlet");
	private static final String CHECK_DOMAIN_SCOPE_INTEGRITY_SERVLET = URL.encode(GWT.getModuleBaseURL() + "roms/ConsoleDomainCheckScopeIntegrityServlet");
	private static final String DOMAIN_DELETE_SERVLET = URL.encode(GWT.getModuleBaseURL() + "roms/ConsoleDomainDeleteServlet");
	private static final String DOMAIN_REPORT_EXCEL_PRINT = "/aon_gwt_fiscal/roms/ConsoleDomainReportExcelPrint";
	private static final String DOMAIN_INFO_REPORT_EXCEL_PRINT = "/aon_gwt_fiscal/roms/ConsoleDomainInfoReportExcelPrint";
	private static final String DOMAIN_ISOLATE_SERVLET = URL.encode(GWT.getModuleBaseURL() + "roms/ConsoleDomainIsolateServlet");
	private static final String UTILITIES_SERVLET = URL.encode(GWT.getModuleBaseURL() + "roms/ConsoleUtilities");

	private static final Logger LOGGER = Logger.getLogger(ConsoleDomainPanel.class.getName());
	private static final String UTILITIES = "Utilidades";
	static {
		LOGGER.addHandler( new ConsoleLogHandler() );
	}
	
	private SplitLayoutPanel splitLayoutPanel;
	private AonMinimizePanel footPanel;
	private AonTabLayoutPanel tabLayout;
	private ConsoleModuleOptions options;
	private LinkedHashMap<Integer,ConsoleDomainTableRow> checkedList = new LinkedHashMap<>();
	private AonTabLayoutPanel mainTabLayout;
	private SimpleLayoutPanel container = new SimpleLayoutPanel();
	private ConsoleDomainFilterPanel filterPanel;
	private InlineLabel runningLabel = new InlineLabel("Ejecutando");
	private AonToolbarButton deleteButton = new AonToolbarButton(AON.MSG.deleteAction(),AON.CSS.aonIconDelete());
	private AonToolbarButton utilitiesButton = new AonToolbarButton(UTILITIES,AON.CSS.aonIconWizard());
	private FormPanel diskForm;
	private Hidden domainParamsHidden;

	
	private int lastScrollPos = 0;
	// private final MutableInt offset = new MutableInt(0);
	private int[] schemasOffsets = new int[ConsoleSchema.values().length];
	private final MutableInt moreData = new MutableInt(0);
	private final MutableInt searchEnabled = new MutableInt( 0 );

	private int count = 0;
	private boolean processRunning;

	class ConsoleDomainTableCallbackImpl implements ConsoleDomainTableCallback {
		@Override
		public ConsoleModuleOptions getOptions() {
			return ConsoleDomainPanel.this.options;
		}
		@Override
		public boolean isAdvancedMode() {
			return ConsoleDomainPanel.this.filterPanel.isAdvancedMode();
		}
		@Override
		public String getSchema() {
			return ConsoleDomainPanel.this.filterPanel.getSchema();			
		}
//		@Override
//		public String[] getSchemas() {
//			return ConsoleDomainPanel.this.filterPanel.getSchemas();			
//		}
		@Override
		public int addCount() {
			return ++count;
		}
		@Override
		public boolean isRunning() {
			return ConsoleDomainPanel.this.isRunning();
		}
		@Override
		public void setRunning(boolean run) {
			ConsoleDomainPanel.this.setRunning(run);
		}
		@Override
		public void showError(String message) {
			ConsoleDomainPanel.this.showErrorPanel(message);			
		}
		@Override
		public void showInfo(String message) {
			ConsoleDomainPanel.this.showInfoPanel(message);			
		}
		
		// -----------------------------------------------------------------------
		// 												  				    [INFO]
		// -----------------------------------------------------------------------
		@Override
		public void onInfo(JsConsoleDomain domain) {
			DomainParams params = new DomainParams()
				.setDbSchema(domain.getSchema())
				.setId(AonNumberUtils.toInteger("" + domain.getId()));
			if (!AonStringUtils.isBlank(params.getDbSchema())) {
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
			hideErrorPanel();
			checkedList.values()
				.stream()
				.forEach( row -> row.doDelete(tabLabel, ConsoleDomainTableCallbackImpl.this, new AsyncCallback<Boolean>() {
					DeleteAsyncCallback cbk = new DeleteAsyncCallback(null, row, ConsoleDomainTableCallbackImpl.this,false);
					
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
		public void onDelete(String domainSchema, Integer domainId, String tabLabel, AsyncCallback<Boolean> cbk) {
			hideErrorPanel();
			deleteDomain(domainSchema, domainId, tabLabel, cbk);
		}

		private void deleteDomain(String domainSchema, Integer domainId, String tabLabel, AsyncCallback<Boolean> cbk) {
			try {
				final AonConsoleProgress aonConsole = getAonConsoleProgress(tabLabel);
				XMLHttpRequest xhreq = XMLHttpRequest.create();
				xhreq.open(FormPanel.METHOD_POST, DOMAIN_DELETE_SERVLET);
				xhreq.setRequestHeader(CONTENT_TYPE,APPLICATION_X_WWW_FORM_URLENCODED);
				xhreq.setOnReadyStateChange(  new ConsoleReadyStateChangeHandler( aonConsole, cbk));
				StringBuilder requestData = new StringBuilder();
				DomainParams params = filterPanel
					.getParams(options)
					.setId(domainId)
					.setDbSchema( domainSchema );
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
		public void onChangeActive(String schema, Integer domainId, boolean active, AsyncCallback<Domain> cbk) {
			ConsoleModule.CONSOLE_SERVICE.changeActive(schema,domainId,active,new AsyncCallbackWrapper<>( cbk ));
		}
		
		// -----------------------------------------------------------------------
		// 												  [CHANGE EXPIRATION DATE]
		// -----------------------------------------------------------------------
		@Override
		public void onChangeExpirationDate(String schema,Integer domainId, Date expireDate, AsyncCallback<Domain> cbk) {
			ConsoleModule.CONSOLE_SERVICE.changeExpirationDate(schema,domainId,expireDate,new AsyncCallbackWrapper<>( cbk ));
		}
		
		// -----------------------------------------------------------------------
		// 												  				[VALIDATE]
		// -----------------------------------------------------------------------
		@Override
		public void onValidate(Integer domainId, String name, String description, AsyncCallback<Boolean> cbk) {
			String tabLabel = AonStringUtils.abbreviate(description, 30);
			validate(domainId, tabLabel, cbk);
		}

		private void validate(Integer domainId, String tabLabel, AsyncCallback<Boolean> cbk) {
			try {
				hideErrorPanel();
				final AonConsoleProgress aonConsole = getAonConsoleProgress(tabLabel); 
				XMLHttpRequest xhreq = XMLHttpRequest.create();
				xhreq.open(FormPanel.METHOD_POST, CHECK_DOMAIN_INTEGRITY_SERVLET);
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
		// 												  		  [SCOPE VALIDATE]
		// -----------------------------------------------------------------------
		@Override
		public void onScopeValidate(Integer domainId, String name, String description, AsyncCallback<Boolean> cbk) {
			String tabLabel = AonStringUtils.abbreviate(description, 30);
			scopeValidate(domainId, tabLabel, cbk);
		}

		private void scopeValidate(Integer domainId, String tabLabel, AsyncCallback<Boolean> cbk) {
			try {
				hideErrorPanel();
				final AonConsoleProgress aonConsole = getAonConsoleProgress(tabLabel); 
				XMLHttpRequest xhreq = XMLHttpRequest.create();
				xhreq.open(FormPanel.METHOD_POST, CHECK_DOMAIN_SCOPE_INTEGRITY_SERVLET);
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
		public void onAvailableUsers(JsConsoleDomain domain, AsyncCallback<LinkedList<User>> cbk) {
			Occam occam = new Occam()
				.setDomainName(domain.getName())
				.setDomain(domain.getId())
				.setUser( null );
			ConsoleModule.CONSOLE_SERVICE.availableUsers(occam, domain.getId(), new AsyncCallbackWrapper<>( cbk ));
		}
		@Override
		public void onSwitchRemoteAccess(String schema, Integer domainId, AsyncCallback<Boolean> cbk) {
			ConsoleModule.CONSOLE_SERVICE.switchRemoteAccess(schema, domainId, new AsyncCallbackWrapper<>( cbk ));
		}
		
		// -----------------------------------------------------------------------
		// 												  		     [EDIT DOMAIN]
		// -----------------------------------------------------------------------
		@Override
		public void onEditDomain(JsConsoleDomain domain) {
			final String tabLabel = "Edit " + AonStringUtils.abbreviate(domain.getDescription(), 20);
			Widget w = mainTabLayout.getWidget(tabLabel);
			if (w == null) {
				AonCloseTab closeTab = new AonCloseTab(tabLabel, true);
				closeTab.addCloseHandler(e -> mainTabLayout.remove(tabLabel));
				DomainParams params = filterPanel.getParams(options)
					.setDbSchema(domain.getSchema())
					.setDescription( domain.getDescription() )
					.setId(domain.getId());
				mainTabLayout.add( new ConsoleRowQuery( params , () -> mainTabLayout.remove(tabLabel) )
					, closeTab, tabLabel);
			} 
			mainTabLayout.selectTab(tabLabel);
		}

		// ----------------------------------------------------------------------
		// 												  		     [UTILIDADES]
		// ----------------------------------------------------------------------
		@Override
		public void onUtilitiesDomain(JsConsoleDomain domain) {
			onAvailableUsers(domain, new AsyncCallback<LinkedList<User>>() {

				@Override
				public void onFailure(Throwable t) {
					showError( "No se pudo mostrar los usuarios. ("+ t.getMessage() +")");
				}

				@Override
				public void onSuccess(LinkedList<User> users) {
					AonCustomPopup popup = new AonCustomPopup(true); 
					popup.setWidth("600px");
					popup.setHeight("600px");
					FlowPanel cont = new FlowPanel();
					Hidden userHidden = new Hidden("j_username");
					Hidden passwordHidden = new Hidden("j_password");
					FormPanel locForm = new FormPanel("_blank");
					locForm.setMethod(FormPanel.METHOD_POST);
					FlowPanel locFormPanel = new FlowPanel();
					locFormPanel.add(userHidden);
					locFormPanel.add(passwordHidden);
					locForm.setWidget(locFormPanel);
					cont.add(locForm);
					
					AonDisplayGrid grid = new AonDisplayGrid();
					grid.addStyleName(AON.CSS.aonMarginTop());
					grid.addStyleName(AON.CSS.aonWidthAlmostAll());
					grid.addStyleName(AON.CSS.aonBlockCenter());
					grid.addHeaderRow()
						.addCell(new Label(""), AON.CSS.aonWidth30())
						.addCell(new Label("Usuario"), AON.CSS.aonWidth150())
						.addCell(new Label("Nombre"), AON.CSS.aonFlexGrow1());
					users.stream()	
						.forEach( u -> {
							Label topLevel = new Label();
							if (AonNumberUtils.notEquals(domain.getId(),u.getDomain().getId())) {
								topLevel.setStyleName(AON.CSS.aonTabIcon());
								topLevel.addStyleName(AON.CSS.aonIconLevelTop());
							}
							grid.addRow()
								.addCell( topLevel )
								.addCell(new Label(u.getLogin()))
								.addCell(new Label(u.getName()))
								.addClickHandler( e -> {
									final String tabLabel = "Tool " + AonStringUtils.abbreviate(domain.getDescription(), 20);
									Widget w = mainTabLayout.getWidget(tabLabel);
									if (w == null) {
										AonCloseTab closeTab = new AonCloseTab(tabLabel, true);
										closeTab.addCloseHandler(e1 -> mainTabLayout.remove(tabLabel));
										FinanceUtilitiesModuleOptions opts = new FinanceUtilitiesModuleOptions()
											.setDomainName(domain.getName())
											.setDomain(domain.getId())
											.setUser(u.getLogin())
											.setAdvancedMode( true );
										Domain d = new Domain()
											.setId( domain.getId() )
											.setName(domain.getName())
											.setDescription(domain.getDescription())
											.setParentId(domain.getParentId())
											.setDomainType( domain.getDomainType())
											.setEnableHeredity( domain.isEnableHeredity() )
											.setDomainManagement(domain.isDomainManagement())
											.setActive( domain.isActive() )
											.setOwner( domain.getOwner() )
											.setScope( domain.getScope() )
											.setMaxDefinedUsers(domain.getMaxDefinedUsers())
											.setDefinedUsers(domain.getDefinedUsers())
											.setMaxDocumentSize(domain.getMaxDocumentSize())
											.setMaxTotalDocumentSize(domain.getMaxTotalDocumentSize())
											.setLastAccessUser(domain.getLastAccessUser())
											.setLastAccessDate(domain.getLastAccessDate())
											.setExpirationDate((domain.getExpirationDate() ))
											.setCreationUser( domain.getCreationUser() )
											.setCreationDate( domain.getCreationDate() )  
											.setModificationUser( domain.getModificationUser() )
											.setModificationDate(domain.getModificationDate())
										;
										
										FinanceUtilitiesModulePanel panel = new FinanceUtilitiesModulePanel( opts, d );						
										mainTabLayout.add( panel, closeTab, tabLabel);
										popup.hide();
								} 
								mainTabLayout.selectTab(tabLabel);
							});
						});
					ScrollPanel scroll = new ScrollPanel();
					cont.add(grid);
					scroll.add(cont);
					popup.add(scroll);
					popup.center();
					popup.show();
				}
			});
		}
		// -----------------------------------------------------------------------
		// 												  		   	   [DUPLICATE]
		// -----------------------------------------------------------------------
		public void onDuplicate(DomainParams origin, DomainParams target ) {
			try {
				setRunning(true);
				hideErrorPanel();
				String tabLabel = AonStringUtils.abbreviate(origin.getDescription(), 30);
				final AonConsoleProgress aonConsole = getAonConsoleProgress(tabLabel);
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
				requestData.append("&"+IRequestParamsNames.DOMAIN_PARAMS +"=" + JsonParams.convert(origin));				
				requestData.append("&"+IRequestParamsNames.NEW_SCHEMA  			+"=" + target.getDbSchema() );
				requestData.append("&"+IRequestParamsNames.NEW_DOMAIN_NAME		+"=" + target.getName() );
				xhreq.send(requestData.toString());
			} catch (Exception e){
				setRunning(false);
				showError("No se pudo duplicar el dominio. " + e.getMessage());
			}
			
		}
		
		private AonConsoleProgress getAonConsoleProgress(String tabLabel) {
			AonConsoleProgress tabWidget = (AonConsoleProgress) tabLayout.getWidget(tabLabel);
			if (tabWidget != null) {
				tabWidget.reset();
			} else {
				tabWidget = new AonConsoleProgress( filterPanel.isAdvancedMode() );
				AonCloseTab closeTab = new AonCloseTab(tabLabel, true);
				closeTab.addCloseHandler(e -> {if (!isRunning()) {
					tabLayout.remove(tabLabel);
					if ( tabLayout.getWidgetCount() == 0) {
						closeFootPanel();
					}
				}});
				tabLayout.add(tabWidget, closeTab, tabLabel);
			}
			tabLayout.selectTab(tabWidget);
			openFootPanelIfNeeded();
			return tabWidget; 
		}
		
		// -----------------------------------------------------------------------
		// 												  		   [UTILITIES RUN]
		// -----------------------------------------------------------------------
		@Override
		public void runUtility(ConsoleUtilities cu, AonConsoleProgress aonConsole) {
			try {
				setRunning(true);
				hideErrorPanel();
				XMLHttpRequest xhreq = XMLHttpRequest.create();
				xhreq.open(FormPanel.METHOD_POST, UTILITIES_SERVLET);
				xhreq.setRequestHeader(CONTENT_TYPE,APPLICATION_X_WWW_FORM_URLENCODED);
				xhreq.setOnReadyStateChange(  new ConsoleReadyStateChangeHandler( aonConsole, new AsyncCallback<Boolean>() {
					
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
				DomainParams params = filterPanel.getParams(options);
				requestData.append("&"+IRequestParamsNames.DOMAIN_PARAMS +"=" + JsonParams.convert(params));
				requestData.append("&"+IRequestParamsNames.CONSOLE_UTILITY +"=" + cu.name());
				
				xhreq.send(requestData.toString());
			} catch (Throwable e){
				setRunning(false);
				showError("No se pudo ejecutar la utilidad. " + e.getMessage());
			}
		}
	}
	

	public ConsoleDomainPanel(ConsoleModuleOptions options) {
		this.options = options;
		AON.ensureInjected();
		this.addNorth(getToolbarPanel(options), AonToolbar.HEIGTH);
		filterPanel = new ConsoleDomainFilterPanel(options);
		filterPanel.addValueChangeHandler(e -> search(e.getValue()) );
		this.addNorth(filterPanel, ConsoleDomainFilterPanel.HEIGTH);

		splitLayoutPanel = new SplitLayoutPanel( 2 );
		splitLayoutPanel.setStyleName(AON.CSS.aonSelector());
		splitLayoutPanel.addSouth(getMinimizePanel(), 30);
		this.add(splitLayoutPanel);
		
		mainTabLayout = new AonTabLayoutPanel(30, Unit.PX);
		mainTabLayout.add(container,new AonCloseTab("Dominios", false),"Dominios");
		splitLayoutPanel.add(mainTabLayout);
	}
	
	public boolean isRunning() {
		return processRunning;
	}
	public void setRunning(boolean run) {
		processRunning = run;
		runningLabel.setVisible(processRunning);
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
		splitLayoutPanel.setWidgetSize(footPanel, Window.getClientHeight() / 3.0);
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
			diskForm.setAction(GWT.getHostPageBaseURL() + DOMAIN_REPORT_EXCEL_PRINT);
			domainParamsHidden.setValue(JsonParams.convert(params));
			diskForm.submit();
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
		toolbar.add(deleteButton);
		
		utilitiesButton.addClickHandler(e -> utilities( new ConsoleDomainTableCallbackImpl() ));
		toolbar.add(utilitiesButton);

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
			AonMessageDialog.show(AVISO,"Hay una proceso ejecut\u00E1ndose. Un momento, por favor.");
			return false;
		}
		return true;
	}
	
	
	private void search(DomainParams params) {
		container.clear();
		count = 0;
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
		initializeOffsets( );
		disableSearch();
		search(params, grid);
	}

	private void search(DomainParams params, ConsoleDomainTable grid) {
		if (!isMoreData() || isRunning()) return;
		setRunning( true );
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
					ConsoleSchema.safeValueOf( domain.getSchema() )
						.ifPresent( this::addOffset );
				}
				if (x > 0) {
//					offset.setValue( params.getOffset() + x);
					enableMoreData();
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
				setRunning( false );
				LOGGER.info("onReadyStateChange (enableSearch)");
			}
		});
		params.setSchemasOffsets( schemasOffsets );
		JSONObject r = new JSONObject();
		r.put(IRequestParamsNames.DOMAIN_PARAMS, JsonParams.convert2Object( params ));
		xhr.send(r.toString());
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
		}
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
					if (cbk != null) cbk.onSuccess(true);
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
	
	private void initializeOffsets( ) {
		this.schemasOffsets = new int[ConsoleSchema.values().length];
		for ( int i = 0; i < schemasOffsets.length; i++ ) schemasOffsets[i] = 0;
	}
	
	private int addOffset( ConsoleSchema cs ) {
		return addOffset(cs, 1);
	}
	private int addOffset( ConsoleSchema cs, int increment ) {
		if (cs == null) throw new IllegalArgumentException("ConsoleSchema is mandatory");
		schemasOffsets[ cs.ordinal() ] = schemasOffsets[ cs.ordinal() ] + increment;
		return schemasOffsets[ cs.ordinal() ]; 
	}
	
	private void utilities(ConsoleDomainTableCallback callback) {
		ConsoleUtilitiesPanel panel = new ConsoleUtilitiesPanel(callback);
		Widget w = mainTabLayout.getWidget( UTILITIES );
		if (w == null) {
			AonCloseTab closeTab = new AonCloseTab(UTILITIES, true);
			closeTab.addCloseHandler(event -> mainTabLayout.remove(panel));
			mainTabLayout.add( panel, closeTab, UTILITIES);
		} 
		mainTabLayout.selectTab(UTILITIES);
	}

}

