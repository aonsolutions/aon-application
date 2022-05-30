package com.esferalia.aon.gwt.fiscal.client.console;

import java.util.Arrays;
import java.util.logging.Logger;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.gwt.common.client.CommonServiceAsync;
import com.esferalia.aon.gwt.common.client.CommonServiceAsyncDecorator;
import com.esferalia.aon.gwt.common.client.RootLayoutPanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayTable;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonLayoutPanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTextBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbar;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.gwt.fiscal.client.MainEntryPoint;
import com.esferalia.aon.gwt.fiscal.shared.IRequestParamsNames;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.http.client.URL;
import com.google.gwt.logging.client.ConsoleLogHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.xhr.client.XMLHttpRequest;
 
public class DomainIsolate extends MainEntryPoint {
	
	private static final String DOMAIN_ISOLATE_SERVLET = URL.encode(GWT.getModuleBaseURL() + "roms/DomainIsolateServlet");

	private static final Logger LOGGER = Logger.getLogger(DomainIsolate.class.getName());
	static {
		LOGGER.addHandler( new ConsoleLogHandler() );
	}
	
	private static final CommonServiceAsync COMMON_SERVICE;
	static {
		CommonServiceAsync commonServiceRaw = GWT.create(CommonService.class);
		COMMON_SERVICE = new CommonServiceAsyncDecorator(commonServiceRaw); 
	}

	private ConsoleModuleOptions options;
	private AonLayoutPanel aonLayout;
	private SimpleLayoutPanel pageContainer;
	private boolean running;
	private AonTextBox domainName;
	private AonTextBox newDomainName;

	private ConsoleModuleOptions getOptions() {
		if (this.options == null) {
			this.options = new ConsoleModuleOptions();
		}
		return this.options;
	}

	@Override
	public void onModuleLoad() {
		COMMON_SERVICE.getAonConfiguration(getCurrentDomainName(), getCurrentDomain(), getCurrentUser()
				, new AsyncCallback<AonConfiguration>() {
				
				@Override
				public void onSuccess(AonConfiguration config) {
					RootLayoutPanel root = RootLayoutPanel.get(getRootPanel() != null ? getRootPanel() : "rootPanel");
					ConsoleModuleOptions opts = new ConsoleModuleOptions();
					opts.setParentWidget(root);
					opts.setDomainName(getCurrentDomainName());
					opts.setDomain(getCurrentDomain());
					opts.setUser(getCurrentUser());
					opts.setConfiguration(config);
					onModuleLoad( opts );
				}
				
				@Override public void onFailure(Throwable caught) {
					Window.alert( "Error al cargar el module" );
				}
			});
	}
	
	public void onModuleLoad(ConsoleModuleOptions options) {
		this.options = options;
		AON.ensureInjected();

		aonLayout = new AonLayoutPanel();
		aonLayout.addNorth(getToolbarPanel(), AonToolbar.HEIGTH);
		aonLayout.addNorth(getDataPanel(), 200);
		
		pageContainer = new SimpleLayoutPanel();
		aonLayout.add(pageContainer);
		getOptions().getParentWidget().add(aonLayout);

	}

	private Widget getDataPanel() {
		ScrollPanel scroll = new ScrollPanel();
		scroll.setStyleName(AON.CSS.aonScrollArea());
		AonDisplayTable table = new AonDisplayTable();
		scroll.setWidget(table);
		table.addStyleName(AON.CSS.aonBlockCenter());
		
		domainName = new AonTextBox();
		domainName.setVisibleLength(50);
		
		newDomainName = new AonTextBox();
		newDomainName.setVisibleLength(50);
		
		table.addRow()
			.addCell(new Label("Nombre del dominio"))
			.addCell(domainName )	
		;
		
		table.addRow()
			.addCell(new Label("Nuevo nombre del dominio"))
			.addCell(newDomainName )	
		;
		return scroll;
	}

	private AonToolbar getToolbarPanel() {
		AonToolbar toolbarPanel = new AonToolbar();
		toolbarPanel.setTitle("Extracci\u00F3n de dominios");
		
		AonToolbarButton runCommand = new AonToolbarButton( AON.MSG.execute(), AON.CSS.aonIconSend() );
		runCommand.addClickHandler(event -> doIt());
		toolbarPanel.add(runCommand);

		return toolbarPanel;
	}
	
	public static void run() {
		GWT.runAsync(DomainIsolate.class, new RunAsyncCallback() {
			
			@Override
			public void onFailure(Throwable reason) {
				Window.alert(AON.MSG.loadError("DomainIntegrityCheck"));
			}

			@Override
			public void onSuccess() {
				DomainIsolate domainIntegrityCheck = new DomainIsolate();
				domainIntegrityCheck.onModuleLoad();
			}
			
		});
	}
	
	private void doIt() {
		if (!running && validate()) {
			running = true;
			try {
				AonConsoleWidget aonConsole = new AonConsoleWidget();
				pageContainer.setWidget(aonConsole);
				XMLHttpRequest xhreq = XMLHttpRequest.create();
				xhreq.open(FormPanel.METHOD_POST, DOMAIN_ISOLATE_SERVLET);
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
				requestData.append("&"+IRequestParamsNames.DOMAIN_NAME			+"=" + domainName.getValue()  );
				requestData.append("&"+IRequestParamsNames.NEW_DOMAIN_NAME		+"=" + newDomainName.getValue() );
				requestData.append("&"+IRequestParamsNames.DOMAIN_ID  			+"=" + options.getDomain() );
				requestData.append("&"+IRequestParamsNames.USER					+"=" + options.getUser() );
				xhreq.send(requestData.toString());
			} catch (Exception e){
				running = false;
			} finally {
			}
		} 
	}
	
	private boolean validate() {
		return true;
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
