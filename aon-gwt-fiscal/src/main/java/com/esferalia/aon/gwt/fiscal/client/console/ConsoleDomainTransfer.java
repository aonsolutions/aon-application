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
 
public class ConsoleDomainTransfer extends AonLayoutPanel {
	
	private static final String DOMAIN_TRANSFER_SERVLET = URL.encode(GWT.getModuleBaseURL() + "roms/ConsoleDomainTransferServlet");

	private static final Logger LOGGER = Logger.getLogger(ConsoleDomainTransfer.class.getName());
	static {
		LOGGER.addHandler( new ConsoleLogHandler() );
	}
	
	private ConsoleModuleOptions options;
	private SimpleLayoutPanel pageContainer;
	private boolean running;

	private String schema;
	private String domainName;
	private String newSchema;
	private String newDomainName;

	public ConsoleDomainTransfer(ConsoleModuleOptions options) {
		this.options = options;
		AON.ensureInjected();
		this.addNorth(getToolbarPanel(), AonToolbar.HEIGTH);
		this.addNorth(getDataPanel(), 135);
		
		pageContainer = new SimpleLayoutPanel();
		this.add(pageContainer);
	}

	private Widget getDataPanel() {
		String host = Window.Location.getHost();
		host = AonStringUtils.substringBefore(host, ":");
		String mainDomain =  "." + AonStringUtils.substringAfter(host, ".");
		
		ScrollPanel scroll = new ScrollPanel();
		scroll.setStyleName(AON.CSS.aonScrollArea());
		
		Label descriptionLabel = new Label("Duplica un dominio en el esquema indicado");
		descriptionLabel.setStyleName(AON.CSS.aonPadding());
		descriptionLabel.addStyleName(AON.CSS.aonMarginBottom());
		descriptionLabel.addStyleName(AON.CSS.aonBorder());
		descriptionLabel.addStyleName(AON.CSS.aonBold());
		descriptionLabel.addStyleName(AON.CSS.aonFontLarger());
		descriptionLabel.addStyleName(AON.CSS.aonColorBlue());
		descriptionLabel.addStyleName(AON.CSS.aonTextCenter());
		AonDisplayTable table = new AonDisplayTable();
		FlowPanel container = new FlowPanel();
		container.add(descriptionLabel);
		container.add(table);
		scroll.setWidget(container);
		table.addStyleName(AON.CSS.aonBlockCenter());
		
		AonTextBox schemaBox = new AonTextBox();
		schemaBox.setMaxLength(25);
		schemaBox.addValueChangeHandler( e -> schema = schemaBox.getValue());
		FlowPanel firstPanel = new FlowPanel(); 
		AonTextBox fullDomainName = new AonTextBox();
		fullDomainName.setVisible(false);
		fullDomainName.setVisibleLength(50);
		fullDomainName.addValueChangeHandler( e -> domainName = fullDomainName.getValue());
		AonTextBox firstDomainName = new AonTextBox();
		firstDomainName.setVisibleLength(30);
		firstDomainName.addValueChangeHandler( e -> domainName = firstDomainName.getValue() + mainDomain);
		
		AonTextBox newSchemaBox = new AonTextBox();
		newSchemaBox.setMaxLength(25);
		newSchemaBox.addValueChangeHandler( e -> newSchema = newSchemaBox.getValue());
		InlineLabel secondDomainName = new InlineLabel(mainDomain);
		secondDomainName.setStyleName(AON.CSS.aonMarginLeft());
		secondDomainName.addStyleName(AON.CSS.aonBold());
		firstPanel.add(fullDomainName);
		firstPanel.add(firstDomainName);
		firstPanel.add(secondDomainName);
		
		FlowPanel secondPanel = new FlowPanel();
		AonTextBox fullNewDomainName = new AonTextBox();
		fullNewDomainName.setVisible(false);
		fullNewDomainName.setVisibleLength(50);
		fullDomainName.addValueChangeHandler( e -> newDomainName = fullNewDomainName.getValue());
		AonTextBox firstNewDomainName = new AonTextBox();
		firstNewDomainName.setVisibleLength(30);
		firstNewDomainName.addValueChangeHandler( e -> newDomainName = firstNewDomainName.getValue() + mainDomain);
		
		InlineLabel secondNewDomainName = new InlineLabel(mainDomain);
		secondNewDomainName.setStyleName(AON.CSS.aonMarginLeft());
		secondNewDomainName.addStyleName(AON.CSS.aonBold());
		secondPanel.add(fullNewDomainName);
		secondPanel.add(firstNewDomainName);
		secondPanel.add(secondNewDomainName);
		
		table.addRow()
			.addCell(new Label("Esquema origen"))
			.addCell( schemaBox )
			.addCell(new Label("Nombre del dominio"))
			.addCell( firstPanel )	
		;
	
		table.addRow()
			.addCell(new Label("Esquema destino"))
			.addCell( newSchemaBox )
			.addCell(new Label("Nuevo nombre del dominio"))
			.addCell( secondPanel )	
		;
		
		CheckBox fullViewCheck = new CheckBox("Editar nombres enteros");
		fullViewCheck.setStyleName(AON.CSS.aonMarginTop() );
		fullViewCheck.addClickHandler(e -> {
			boolean visible = fullViewCheck.getValue().booleanValue();
			fullDomainName.setVisible(visible);
			firstDomainName.setVisible(!visible);
			secondDomainName.setVisible(!visible);
			fullNewDomainName.setVisible(visible);
			firstNewDomainName.setVisible(!visible);
			secondNewDomainName.setVisible(!visible);
		});
		table.addRow()
			.addCell(new Label())
			.addCell( fullViewCheck )	
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
	
	private void doIt() {
		if (!running && validate()) {
			running = true;
			try {
				AonConsoleWidget aonConsole = new AonConsoleWidget();
				pageContainer.setWidget(aonConsole);
				XMLHttpRequest xhreq = XMLHttpRequest.create();
				xhreq.open(FormPanel.METHOD_POST, DOMAIN_TRANSFER_SERVLET);
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
				requestData.append("&"+IRequestParamsNames.SCHEMA				+"=" + schema );
				requestData.append("&"+IRequestParamsNames.DOMAIN_NAME			+"=" + domainName );
				requestData.append("&"+IRequestParamsNames.NEW_SCHEMA			+"=" + newSchema );
				requestData.append("&"+IRequestParamsNames.NEW_DOMAIN_NAME		+"=" + newDomainName );
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
	
	private boolean validate() {
		this.hideErrorPanel();
		if (AonStringUtils.isBlank(domainName)) {
			this.showErrorPanel("El nombre del dominio debe tener valor");			
			return false;
		}
		if (AonStringUtils.isBlank(newDomainName)) {
			this.showErrorPanel("El nuevo nombre del dominio debe tener valor");			
			return false;
		}
		
		String msg = "Se va a traspasar el dominio ["+domainName+"] a el dominio ["+newDomainName+"]. Continuar?";
		return Window.confirm(msg);
	}

	
}
