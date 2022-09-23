package com.esferalia.aon.gwt.fiscal.client.console;

import java.util.Date;
import java.util.HashSet;
import java.util.logging.Logger;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.AsyncCallbackWrapper;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCloseTab;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonLayoutPanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessageDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMinimizePanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTabLayoutPanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbar;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.gwt.fiscal.client.console.ConsoleDomainTable.ConsoleDomainTableCallback;
import com.esferalia.aon.gwt.fiscal.shared.IRequestParamsNames;
import com.esferalia.aon.gwt.fiscal.shared.JsonParams;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.DomainParams;
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
import com.google.gwt.xhr.client.XMLHttpRequest;
 
public class ConsoleDomainModule extends AonLayoutPanel {


	private static final String DOMAIN_STREAM_SERVLET = URL.encode(GWT.getModuleBaseURL() + "roms/ConsoleDomainFlatStreamServlet");
	private static final String CHECK_DOMAIN_INTEGRITY_SERVLET = URL.encode(GWT.getModuleBaseURL() + "roms/ConsoleDomainCheckIntegrityServlet");
	private static final String DOMAIN_REPORT_EXCEL_PRINT = "/aon_gwt_fiscal/roms/ConsoleDomainReportExcelPrint";

	private static final Logger LOGGER = Logger.getLogger(ConsoleDomainModule.class.getName());
	static {
		LOGGER.addHandler( new ConsoleLogHandler() );
	}
	
	private boolean running;
	private SplitLayoutPanel splitLayoutPanel;
	private AonMinimizePanel footPanel;
	private AonTabLayoutPanel tabLayout;
	private ConsoleModuleOptions options;
	private HashSet<Integer> checkedList = new HashSet<>();
	private SimpleLayoutPanel container = new SimpleLayoutPanel();
	private ConsoleDomainFilterPanel filterPanel;
	private AonToolbarButton deleteButton = new AonToolbarButton(AON.MSG.deleteAction(),AON.CSS.aonIconDelete());
	
	private int lastScrollPos = 0;
	private final MutableInt offset = new MutableInt(0);
	private final MutableInt moreData = new MutableInt(0);
	private final MutableInt searchEnabled = new MutableInt( 0 );

	abstract class AbsConsoleDomainTableCallback implements ConsoleDomainTableCallback {
		public void showError(String message) {
			ConsoleDomainModule.this.showErrorPanel(message);			
		}
		public void showInfo(String message) {
			ConsoleDomainModule.this.showInfoPanel(message);			
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
		
		FormPanel diskForm = new FormPanel("_blank");
		diskForm.setMethod(FormPanel.METHOD_POST);
		Hidden domainParamsHidden = new Hidden(IRequestParamsNames.DOMAIN_PARAMS);
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
				AonMessageDialog.show("AVISO", "Seleccione un esquema");
			}
		});
		toolbar.add(exportButton);
		
		deleteButton.setEnabled(false);
		deleteButton.setVisible(false);
		toolbar.add(deleteButton);
		
		return toolbar;
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
		XMLHttpRequest xhr = XMLHttpRequest.create();
		xhr.open(FormPanel.METHOD_POST, DOMAIN_STREAM_SERVLET);
		xhr.setRequestHeader("Content-type","application/x-www-form-urlencoded");
		xhr.setOnReadyStateChange(xhreq -> {
			int state = xhreq.getReadyState();
			LOGGER.info("START onReadyStateChange: ("+ state +")");
			boolean something = false;
			if (state == XMLHttpRequest.DONE) {
				String text = xhreq.getResponseText();
				int count = 0;
				if (!JsonUtils.safeToEval(text)) {
					Window.alert("ERROR de evaluación");
				}
				JavaScriptObject unk = JsonUtils.safeEval(text);
				JsArray<JsDomain> array = unk.cast();
				for (; count < array.length(); count++ ) {
					JsDomain domain = array.get(count);
					grid.addRow(domain);
					something = true;
				}
				if (count > 0) {
					offset.setValue( params.getOffset() + count);
					enableMoreData();
					LOGGER.info("onReadyStateChange (" + count + ") : offset " + offset.getValue() + " enableMoreData");
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
		StringBuilder requestData = new StringBuilder();
		params.setOffset(offset.getValue());
		String jsonParams = JsonParams.convert( params );
		LOGGER.info("jsonParams --> " + jsonParams);
		requestData.append("&"+IRequestParamsNames.DOMAIN_PARAMS +"=" + jsonParams );
		xhr.send(requestData.toString());
	}
	
	private ConsoleDomainTable getTable() {
		checkedList.clear();
		ConsoleDomainTable table = new ConsoleDomainTable(new AbsConsoleDomainTableCallback() {
			
			@Override
			public void onDelete(Integer domainId, AsyncCallback<Boolean> cbk) {
				DomainParams params = filterPanel.getParams(options);
				ConsoleModule.CONSOLE_SERVICE.deleteDomain(params,domainId,new AsyncCallbackWrapper<>( cbk ));
			}

			@Override
			public void onChangeActive(Integer domainId, boolean active, AsyncCallback<Domain> cbk) {
				DomainParams params = filterPanel.getParams(options);
				ConsoleModule.CONSOLE_SERVICE.changeActive(params,domainId,active,new AsyncCallbackWrapper<>( cbk ));
			}
			
			@Override
			public void onChangeExpirationDate(Integer domainId, Date expireDate, AsyncCallback<Domain> cbk) {
				DomainParams params = filterPanel.getParams(options);
				ConsoleModule.CONSOLE_SERVICE.changeExpirationDate(params,domainId,expireDate,new AsyncCallbackWrapper<>( cbk ));
			}
			
			@Override
			public void onValidate(Integer domainId, String name, String description, AsyncCallback<Boolean> cbk) {
				DomainParams params = filterPanel.getParams(options);
				validate(params.getSchema(), domainId, name, description);
			}

		});
		table.addSelectionHandler(e -> check( e.getSelectedItem() ));
		return table;
	}
	
	private void check(JsDomain domain) {
		if (domain != null) {
			if (checkedList.contains(domain.getId())) {
				checkedList.remove(domain.getId());
			} else {
				checkedList.add(domain.getId());
			}
			deleteButton.setEnabled( !checkedList.isEmpty() );
		}
	}

	private void validate(String schema,Integer domainId, String name, String description) {
		if (!running) {
			running = true;
			try {
				AonConsoleWidget aonConsole = new AonConsoleWidget();
				String tabLabel = AonStringUtils.abbreviate(description, 30);
				AonCloseTab closeTab = new AonCloseTab(tabLabel, true);
				tabLayout.add(aonConsole, closeTab);
				closeTab.addCloseHandler(e -> {Window.alert("click!!");tabLayout.remove(tabLabel);});
				tabLayout.selectTab(aonConsole);
				openFootPanelIfNeeded();
				
				XMLHttpRequest xhreq = XMLHttpRequest.create();
				xhreq.open(FormPanel.METHOD_POST, CHECK_DOMAIN_INTEGRITY_SERVLET);
				xhreq.setRequestHeader("Content-type","application/x-www-form-urlencoded");
				xhreq.setOnReadyStateChange( xhr -> {
					int state = xhr.getReadyState();
					if (state == XMLHttpRequest.LOADING || state == XMLHttpRequest.DONE) {
						String text = xhr.getResponseText();
						aonConsole.log(text);
					}
					if (state == XMLHttpRequest.DONE) {
						running = false;
					}
				});
				StringBuilder requestData = new StringBuilder();
				requestData.append("&"+IRequestParamsNames.SCHEMA  				+"=" + schema );
				requestData.append("&"+IRequestParamsNames.DOMAIN_NAME  		+"=" + name );
				requestData.append("&"+IRequestParamsNames.DOMAIN_ID  			+"=" + domainId );
				requestData.append("&"+IRequestParamsNames.USER					+"=" + options.getUser() );
				xhreq.send(requestData.toString());
			} catch (Exception e){
				running = false;
			}
		} 
	}
	
}
