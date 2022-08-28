package com.esferalia.aon.gwt.fiscal.client.console;

import java.util.Arrays;
import java.util.logging.Logger;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayTable;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonLayoutPanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTextBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbar;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.gwt.fiscal.shared.IRequestParamsNames;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.URL;
import com.google.gwt.logging.client.ConsoleLogHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.xhr.client.XMLHttpRequest;
 
public class ConsoleDomainCheckIntegrity extends AonLayoutPanel {
	
	private static final String CHECK_DOMAIN_INTEGRITY_SERVLET = URL.encode(GWT.getModuleBaseURL() + "roms/ConsoleDomainCheckIntegrityServlet");

	private static final Logger LOGGER = Logger.getLogger(ConsoleDomainCheckIntegrity.class.getName());
	static {
		LOGGER.addHandler( new ConsoleLogHandler() );
	}
	
	private ConsoleModuleOptions options;
	private SimpleLayoutPanel pageContainer;
	private boolean running;
	private String domainName;

	public ConsoleDomainCheckIntegrity(ConsoleModuleOptions options) {
		this.options = options;
		AON.ensureInjected();

		this.addNorth(getToolbarPanel(), AonToolbar.HEIGTH);
		this.addNorth(getDataPanel(), 100);
		pageContainer = new SimpleLayoutPanel();
		this.add(pageContainer);
	}

	private AonToolbar getToolbarPanel() {
		AonToolbar toolbarPanel = new AonToolbar();
		toolbarPanel.setTitle("Chequeo integridad de dominios");
		
		AonToolbarButton runCommand = new AonToolbarButton( AON.MSG.execute(), AON.CSS.aonIconSend() );
		runCommand.addClickHandler(event -> doIt());
		toolbarPanel.add(runCommand);

		return toolbarPanel;
	}
	
	private Widget getDataPanel() {
		String host = Window.Location.getHost();
		host = AonStringUtils.substringBefore(host, ":");
		String mainDomain =  "." + AonStringUtils.substringAfter(host, ".");
		
		ScrollPanel scroll = new ScrollPanel();
		scroll.setStyleName(AON.CSS.aonScrollArea());
		AonDisplayTable table = new AonDisplayTable();
		scroll.setWidget(table);
		table.addStyleName(AON.CSS.aonBlockCenter());
		
		FlowPanel firstPanel = new FlowPanel(); 
		AonTextBox fullDomainName = new AonTextBox();
		fullDomainName.setVisible(false);
		fullDomainName.setVisibleLength(50);
		fullDomainName.addValueChangeHandler( e -> domainName = fullDomainName.getValue());
		AonTextBox firstDomainName = new AonTextBox();
		firstDomainName.setVisibleLength(30);
		firstDomainName.addValueChangeHandler( e -> domainName = firstDomainName.getValue() + mainDomain);
		
		InlineLabel secondDomainName = new InlineLabel(mainDomain);
		secondDomainName.setStyleName(AON.CSS.aonMarginLeft());
		secondDomainName.addStyleName(AON.CSS.aonBold());
		firstPanel.add(fullDomainName);
		firstPanel.add(firstDomainName);
		firstPanel.add(secondDomainName);
		
		table.addRow()
			.addCell(new Label("Nombre del dominio"))
			.addCell( firstPanel )	
		;
	
		CheckBox fullViewCheck = new CheckBox("Editar nombres enteros");
		fullViewCheck.setStyleName(AON.CSS.aonMarginTop() );
		fullViewCheck.addClickHandler(e -> {
			boolean visible = fullViewCheck.getValue().booleanValue();
			fullDomainName.setVisible(visible);
			firstDomainName.setVisible(!visible);
			secondDomainName.setVisible(!visible);
		});
		table.addRow()
			.addCell(new Label())
			.addCell( fullViewCheck )	
		;
		return scroll;
	}

	private void doIt() {
		if (!running) {
			running = true;
			try {
				AonConsoleWidget aonConsole = new AonConsoleWidget();
				pageContainer.setWidget(aonConsole);
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
				requestData.append("&"+IRequestParamsNames.DOMAIN_NAME			+"=" + domainName );
				requestData.append("&"+IRequestParamsNames.DOMAIN_ID  			+"=" + options.getDomain() );
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
