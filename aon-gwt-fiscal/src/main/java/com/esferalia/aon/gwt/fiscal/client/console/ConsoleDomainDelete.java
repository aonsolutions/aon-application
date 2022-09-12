package com.esferalia.aon.gwt.fiscal.client.console;

import java.util.Arrays;
import java.util.logging.Logger;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonConfirmDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonConfirmDialog.AonConfirmDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayGrid;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayTable;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonLayoutPanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbar;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.gwt.fiscal.shared.IRequestParamsNames;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.watson.util.AonNumberUtils;
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
 
public class ConsoleDomainDelete extends AonLayoutPanel {
	
	private static final String DOMAIN_DELETE_SERVLET = URL.encode(GWT.getModuleBaseURL() + "roms/ConsoleDomainDeleteServlet");

	private static final Logger LOGGER = Logger.getLogger(ConsoleDomainDelete.class.getName());
	static {
		LOGGER.addHandler( new ConsoleLogHandler() );
	}
	
	private AonToolbarButton runCommand = new AonToolbarButton( AON.MSG.execute(), AON.CSS.aonIconSend() );
	
	private ConsoleModuleOptions options;
	private SimpleLayoutPanel pageContainer;
	private boolean running;

	private FlowPanel domainContainer = new FlowPanel();
	
	private ListBox schemaBox = new ListBox();
	private String schema;
	private Domain privDomain;

	public ConsoleDomainDelete(ConsoleModuleOptions options) {
		this.options = options;
		AON.ensureInjected();
		this.addNorth(getToolbarPanel(), AonToolbar.HEIGTH);
		this.addNorth(getDataPanel(), 300);
		
		pageContainer = new SimpleLayoutPanel();
		this.add(pageContainer);
		
		runCommand.setVisible(false);
		ConsoleModule.CONSOLE_SERVICE.getSchemas(options.getOccam(), new AsyncCallback<String[]>() {
			
			@Override
			public void onSuccess(String[] schemas) {
				schemaBox.clear();
				schemaBox.addItem(AonStringUtils.EMPTY);
				for (String sch : schemas) {
					schemaBox.addItem(sch);
				}
				runCommand.setVisible(true);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				Window.alert("No se pueden leer los escquemas de la BD");
			}
		});
	}
	
	private Domain getDomain() {
		return privDomain;
	}
	private void setDomain(Domain domain) {
		this.privDomain = domain;
		paintDomain();
	}
	
	private void paintDomain() {
		domainContainer.clear();
		if (getDomain() != null) {
			AonDisplayGrid table = new AonDisplayGrid();
			table.addStyleName(AON.CSS.aonBlockCenter());
			domainContainer.add(table);
			table.addHeaderRow()
				.addCell(new Label("Id"))
				.addCell(new Label("Padre"))
				.addCell(new Label("Nombre"))
				.addCell(new Label("Descripci\u00F3n"))
				.addCell(new Label("\u00FAltimo acceso"))
				.addCell(new Label("Expira"))
			;
			table.addRow()
				.addCell(new Label(AonNumberUtils.toString(getDomain().getId())), AON.CSS.aonTextCenter())
				.addCell(new Label(AonNumberUtils.toString(getDomain().getParentId())), AON.CSS.aonTextCenter())
				.addCell(new Label(getDomain().getName()))
				.addCell(new Label(getDomain().getDescription()))
				.addCell(new Label(Boolean.toString( getDomain().isEnableHeredity())), AON.CSS.aonTextCenter())
				.addCell(new Label(Boolean.toString( getDomain().isDomainManagement())), AON.CSS.aonTextCenter())
				.addCell(new Label(getDomain().getLastAccessDate() == null ?"":AON.TIME_FORMAT.format(getDomain().getLastAccessDate())), AON.CSS.aonTextCenter())
				.addCell(new Label(getDomain().getExpirationDate() == null ?"":AON.TIME_FORMAT.format(getDomain().getExpirationDate())), AON.CSS.aonTextCenter())
			;	
		}
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
		
		domainContainer.setStyleName(AON.CSS.aonTextCenter());
		domainContainer.addStyleName(AON.CSS.aonMarginTop());		
		domainContainer.addStyleName(AON.CSS.aonPaddingTop());
		domainContainer.addStyleName(AON.CSS.aonPaddingBottom());
		domainContainer.addStyleName(AON.CSS.aonBlockCenter());
		domainContainer.addStyleName(AON.CSS.aonBorder());
		
		FlowPanel container = new FlowPanel();
		container.add(descriptionLabel);
		container.add(table);
		container.add(domainContainer);
		scroll.setWidget(container);
		table.addStyleName(AON.CSS.aonBlockCenter());
		
		Label originLabel = new Label("ORIGEN");
		originLabel.setStyleName(AON.CSS.aonBold());
		
		AonDomainBox domainBox = new AonDomainBox(options.getOccam());
		domainBox.setEnabled(false);
		domainBox.addSelectionHandler( e -> setDomain(e.getSelectedItem()));
		schemaBox.addChangeHandler( e -> {
			schema = schemaBox.getSelectedValue();
			domainBox.setEnabled(AonStringUtils.isNotBlank(schema));		
			domainBox.setSchema(schema);
			setDomain(null);
			domainBox.setDomain(getDomain());
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
			String msg = "Se va a borrar el dominio "
					+ "["+getDomain().getName()+"] "
					+ "\u00BFContinuar?";
			AonConfirmDialog acd = new AonConfirmDialog();
			acd.confirm(msg, new AonConfirmDialogCallback() {
				
				@Override
				public void onCancel() {
					running = false;
				}
				
				@Override
				public void onAccept() {
					running = true;
					run();
				}
			});
		} 
	}
	
	private void run() {
		try {
			AonConsoleWidget aonConsole = new AonConsoleWidget();
			pageContainer.setWidget(aonConsole);
			XMLHttpRequest xhreq = XMLHttpRequest.create();
			xhreq.open(FormPanel.METHOD_POST, DOMAIN_DELETE_SERVLET);
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
			requestData.append("&"+IRequestParamsNames.DOMAIN_NAME			+"=" + getDomain().getName() );
			requestData.append("&"+IRequestParamsNames.DOMAIN_ID  			+"=" + getDomain().getId() );
			requestData.append("&"+IRequestParamsNames.USER					+"=" + options.getUser() );
			xhreq.send(requestData.toString());
		} catch (Exception e){
			running = false;
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
		if (getDomain() == null || AonStringUtils.isBlank(getDomain().getName())) {
			this.showErrorPanel("El nombre del dominio debe tener valor");			
			return false;
		}
		return true;
	}

	
}
