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
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.xhr.client.XMLHttpRequest;
 
public class ConsoleDomainIsolate extends AonLayoutPanel {
	
	private static final String DOMAIN_ISOLATE_SERVLET = URL.encode(GWT.getModuleBaseURL() + "roms/ConsoleDomainIsolateServlet");

	private static final Logger LOGGER = Logger.getLogger(ConsoleDomainIsolate.class.getName());
	static {
		LOGGER.addHandler( new ConsoleLogHandler() );
	}
	
	private AonToolbarButton runCommand = new AonToolbarButton( AON.MSG.execute(), AON.CSS.aonIconSend() );
	
	private ConsoleModuleOptions options;
	private SimpleLayoutPanel pageContainer;
	private boolean running;

	private ListBox schemaBox = new ListBox();
	private String schema;
	private String domainName;
	
	private ListBox newSchemaBox = new ListBox();
	private String newSchema;
	private String newDomainName;

	public ConsoleDomainIsolate(ConsoleModuleOptions options) {
		this.options = options;
		AON.ensureInjected();
		this.addNorth(getToolbarPanel(), AonToolbar.HEIGTH);
		this.addNorth(getDataPanel(), 135);
		
		pageContainer = new SimpleLayoutPanel();
		this.add(pageContainer);
		
		runCommand.setVisible(false);
		ConsoleModule.CONSOLE_SERVICE.getSchemas(options.getOccam(), new AsyncCallback<String[]>() {
			
			@Override
			public void onSuccess(String[] schemas) {
				schemaBox.clear();
				schemaBox.addItem(AonStringUtils.EMPTY);
				newSchemaBox.clear();
				newSchemaBox.addItem(AonStringUtils.EMPTY);
				for (String sch : schemas) {
					schemaBox.addItem(sch);
					newSchemaBox.addItem(sch);
				}
				runCommand.setVisible(true);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				Window.alert("No se pueden leer los escquemas de la BD");
			}
		});
	}

	private Widget getDataPanel() {
		ScrollPanel scroll = new ScrollPanel();
		scroll.setStyleName(AON.CSS.aonScrollArea());
		
		Label descriptionLabel = new Label("Duplica y aplana en su caso un dominio en el mismo esquema");
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
		
		Label originLabel = new Label("ORIGEN");
		originLabel.setStyleName(AON.CSS.aonBold());
		
		AonDomainBox domainBox = new AonDomainBox(options.getOccam());
		domainBox.setEnabled(false);
		domainBox.addSelectionHandler( e -> domainName = (e.getSelectedItem() == null)?null:e.getSelectedItem().getName());
		schemaBox.addChangeHandler( e -> {
			schema = schemaBox.getSelectedValue();
			newSchema = schemaBox.getSelectedValue();
			domainBox.setEnabled(AonStringUtils.isNotBlank(schema));		
			domainBox.setSchema(schema);
		});
		Label schemaLabel = new Label("Esquema");
		schemaLabel.setStyleName(AON.CSS.aonInnerLabel());
		Label domainLabel = new Label("Dominio");
		domainLabel.setStyleName(AON.CSS.aonInnerLabel());
		
		table.addRow()
			.addCell(originLabel)
			.addCell(schemaLabel)
			.addCell(schemaBox)
			.addCell(domainLabel)
			.addCell( domainBox )	
		;
	
		Label targetLabel = new Label("DESTINO");
		targetLabel.setStyleName(AON.CSS.aonBold());
		newSchemaBox.addChangeHandler( e -> newSchema = newSchemaBox.getSelectedValue());
		AonTextBox newDomainBox = new AonTextBox();
		newDomainBox.addValueChangeHandler(e -> newDomainName = newDomainBox.getValue());
		Label newSchemaLabel = new Label("Esquema");
		newSchemaLabel.setStyleName(AON.CSS.aonInnerLabel());
		Label newDomainLabel = new Label("Dominio");
		newDomainLabel.setStyleName(AON.CSS.aonInnerLabel());
		table.addRow()
			.addCell(targetLabel)
			.addCell(newSchemaLabel)
			.addCell(newSchemaBox)
			.addCell(newDomainLabel)
			.addCell(newDomainBox)	
		;
		
		return scroll;
	}

	private AonToolbar getToolbarPanel() {
		AonToolbar toolbarPanel = new AonToolbar();
		toolbarPanel.setTitle("Extracci\u00F3n de dominios");
		
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
				requestData.append("&"+IRequestParamsNames.SCHEMA  				+"=" + schema );
				requestData.append("&"+IRequestParamsNames.DOMAIN_NAME			+"=" + domainName );
				requestData.append("&"+IRequestParamsNames.NEW_SCHEMA  			+"=" + newSchema );
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
