package com.esferalia.aon.gwt.fiscal.client.console;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.logging.Logger;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.AsyncCallbackWrapper;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCloseTab;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonLayoutPanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessageDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMinimizePanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTabLayoutPanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToast;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbar;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.gwt.fiscal.client.console.ConsoleDomainTable.ConsoleDomainTableCallback;
import com.esferalia.aon.gwt.fiscal.shared.IRequestParamsNames;
import com.esferalia.aon.gwt.fiscal.shared.JsonParams;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.DomainParams;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.http.client.URL;
import com.google.gwt.logging.client.ConsoleLogHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.xhr.client.XMLHttpRequest;
 
public class ConsoleDomainModule extends AonLayoutPanel {

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
		filterPanel.addValueChangeHandler(e -> search(options, e.getValue()) );
		this.addNorth(filterPanel, ConsoleDomainFilterPanel.HEIGTH);

		splitLayoutPanel = new SplitLayoutPanel( 2 );
		splitLayoutPanel.addSouth(getMinimizePanel(), 30);
		this.add(splitLayoutPanel);
		
		splitLayoutPanel.add(container);
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
	
	private void search(ConsoleModuleOptions options, DomainParams params) {
		final AonToast toast = new AonToast();
		toast.show("Cargando ...", new InlineLabel("Un momento, por favor ..."));
		ConsoleModule.CONSOLE_SERVICE.getDomains(params,new AsyncCallback<LinkedList<Domain>>() {
			
			public void onFailure(Throwable caught) {
				toast.hide();
				showErrorPanel(caught.getMessage());
			}

			public void onSuccess(LinkedList<Domain> domains) {
				toast.hide();
				container.setWidget(getTable( domains ) );
			}

		});
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
	
	private Widget getTable(LinkedList<Domain> domains) {
		checkedList.clear();
		ConsoleDomainTable table = new ConsoleDomainTable(domains, new AbsConsoleDomainTableCallback() {
			
			@Override
			public void onDelete(Domain domain, AsyncCallback<Boolean> cbk) {
				DomainParams params = filterPanel.getParams(options);
				ConsoleModule.CONSOLE_SERVICE.deleteDomain(params, 
					domain.getId(),new AsyncCallbackWrapper<>( cbk ));
			}

			@Override
			public void onChangeActive(Domain domain, AsyncCallback<Domain> cbk) {
				DomainParams params = filterPanel.getParams(options);
				ConsoleModule.CONSOLE_SERVICE.changeActive(params,domain,new AsyncCallbackWrapper<>( cbk ));
			}
			
			@Override
			public void onChangeExpirationDate(Domain domain, AsyncCallback<Domain> cbk) {
				DomainParams params = filterPanel.getParams(options);
				ConsoleModule.CONSOLE_SERVICE.changeExpirationDate(params,domain,new AsyncCallbackWrapper<>( cbk ));
			}
			
			@Override
			public void onValidate(Domain domain, AsyncCallback<Boolean> cbk) {
				DomainParams params = filterPanel.getParams(options);
				validate(params.getSchema(), domain);
			}

		});
		table.addSelectionHandler(e -> check( e.getSelectedItem() ));
		return table;
	}

	private void check(Domain domain) {
		if (domain != null) {
			if (checkedList.contains(domain.getId())) {
				checkedList.remove(domain.getId());
			} else {
				checkedList.add(domain.getId());
			}
			deleteButton.setEnabled( !checkedList.isEmpty() );
		}
	}

	private void validate(String schema,Domain domain) {
		if (!running) {
			running = true;
			try {
				AonConsoleWidget aonConsole = new AonConsoleWidget();
				String tabLabel = AonStringUtils.abbreviate(domain.getDescription(), 30);
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
				requestData.append("&"+IRequestParamsNames.DOMAIN_NAME			+"=" + domain.getName() );
				requestData.append("&"+IRequestParamsNames.DOMAIN_ID  			+"=" + domain.getId() );
				requestData.append("&"+IRequestParamsNames.USER					+"=" + options.getUser() );
				xhreq.send(requestData.toString());
			} catch (Exception e){
				running = false;
			}
		} 
	}
	
	private static class AonConsoleWidget extends ScrollPanel {
		private final HTMLPanel consoleWidget;
		private int lastIndex = 0;
		
		public AonConsoleWidget() {
			setStyleName(AON.CSS.aonScrollArea());
			consoleWidget = new HTMLPanel("pre","");
			consoleWidget.setStyleName(AON.CSS.aonPadding());
			consoleWidget.getElement().getStyle().setBackgroundColor("black");
			consoleWidget.getElement().getStyle().setColor("white");
			setWidget(consoleWidget);
		}

		public void log(String text) {
			int newLastIndex = AonStringUtils.lastIndexOf(text, '\n');
			String text2 = AonStringUtils.substring(text, lastIndex, newLastIndex);
			String[] array = AonStringUtils.split(text2, '\n');
			if (array != null) {
				Arrays.stream(array)
					.forEach(line ->  consoleWidget.add(new Label(line)));
			}
			lastIndex = newLastIndex;
			scrollToBottom();
		}
		
	}
	
}
