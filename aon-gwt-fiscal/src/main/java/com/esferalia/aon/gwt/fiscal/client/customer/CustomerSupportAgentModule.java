package com.esferalia.aon.gwt.fiscal.client.customer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.RegistryService;
import com.esferalia.aon.gwt.common.client.RegistryServiceAsync;
import com.esferalia.aon.gwt.common.client.RegistryServiceAsyncDecorator;
import com.esferalia.aon.gwt.common.client.RootLayoutPanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessagePanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbar;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarSmall;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarSmallButton;
import com.esferalia.aon.gwt.fiscal.client.MainEntryPoint;
import com.esferalia.aon.gwt.fiscal.client.registry.RegistryModuleOptions;
import com.esferalia.aon.occam.api.model.Customer;
import com.esferalia.aon.occam.api.model.registry.RegistryMedia;
import com.esferalia.aon.occam.api.model.registry.Seller;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.UrlBuilder;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;

public class CustomerSupportAgentModule extends MainEntryPoint {

	// Services
	private static RegistryServiceAsync SERVICE;
	
	// Options Config
	private RegistryModuleOptions options;

	// Content
	private DockLayoutPanel dockLayoutPanel;
	
	private AonToolbar toolbar;
	private AonToolbarButton backButton;
	private AonToolbarButton syncSupportAgent;
	
	private HTMLPanel container;
	
	private HTMLPanel messagePanel;
	
	private ScrollPanel scrollPanel;
	private DeckPanel deckPanel;
	private HTMLPanel scrollContainer;
	private HTMLPanel scrollLogContainer;
	
	// Support Agents
	private HTMLPanel supportAgentsPanel;
	private AonToolbarSmallButton toolbarSupportAgentsBtn;
	private Grid supportAgentsTable;
	private boolean isSupportAgentsOpen = true;
	
	// Customer Agents
	private HTMLPanel customerWithoutAgentPanel;
	private AonToolbarSmallButton toolbarCustomerWithoutAgentBtn;
	private Grid customerWithoutAgentTable;
	private boolean isCustomerWithoutAgentOpen = true;
	
	// Customer Domains
	private HTMLPanel customerWithoutDomainPanel;
	private AonToolbarSmallButton toolbarCustomerWithoutDomainBtn;
	private Grid customerWithoutDomainTable;
	private boolean isCustomerWithoutDomainOpen = true;
	
	// Api
	private static String SESSION_API = "AONd95770f269e711eb94390242ac130002";
	
	@Override
	public void onModuleLoad() {
		RootLayoutPanel root = RootLayoutPanel.get(getRootPanel() != null ? getRootPanel() : "rootPanel");
		RegistryModuleOptions options = new RegistryModuleOptions();
		options.setParentWidget(root);
		options.setDomainName(getCurrentDomainName());
		options.setDomain(getCurrentDomain());
		options.setUser(getCurrentUser());
		this.onModuleLoad(options);
	}

	public void onModuleLoad(final RegistryModuleOptions opt) {
		AON.ensureInjected();
		
		RegistryServiceAsync registryServiceRaw = GWT.create(RegistryService.class);
		SERVICE = new RegistryServiceAsyncDecorator(registryServiceRaw);
		
		this.options = opt;
		
		dockLayoutPanel = new DockLayoutPanel(Unit.PX);
		this.options.getParentWidget().add(dockLayoutPanel);
		
		dockLayoutPanel.clear();
		
		loadModule();
	}

	private void loadModule() {
		dockLayoutPanel.clear();
		
		createToolbar();
		dockLayoutPanel.addNorth(toolbar, 50);
		
		container = new HTMLPanel("");
		container.addStyleName(AON.CSS.aonFlexColumn());
		
		messagePanel = new HTMLPanel("");
		container.add(messagePanel);
		
		scrollPanel = new ScrollPanel();
		scrollPanel.setHeight((Window.getClientHeight() - 170) + "px");
		scrollPanel.getElement().getStyle().setProperty("padding", "0 1rem");
		container.add(scrollPanel);
		
		deckPanel = new DeckPanel();
		deckPanel.setHeight((Window.getClientHeight() - 170) + "px");
		scrollPanel.add(deckPanel);
		
		scrollContainer = new HTMLPanel("");
		scrollContainer.addStyleName(AON.CSS.aonFlexColumn());
		deckPanel.add(scrollContainer);
		
		scrollLogContainer = new HTMLPanel("");
		scrollLogContainer.addStyleName(AON.CSS.aonFlexColumn());
		deckPanel.add(scrollLogContainer);
		
		showInfo();
		
		dockLayoutPanel.add(container);
		
		supportAgentsPanel = new HTMLPanel("");
		supportAgentsPanel.addStyleName(AON.CSS.aonFlexColumn());
		scrollContainer.add(supportAgentsPanel);
		
		customerWithoutAgentPanel = new HTMLPanel("");
		customerWithoutAgentPanel.addStyleName(AON.CSS.aonFlexColumn());
		scrollContainer.add(customerWithoutAgentPanel);
		
		customerWithoutDomainPanel = new HTMLPanel("");
		customerWithoutDomainPanel.addStyleName(AON.CSS.aonFlexColumn());
		scrollContainer.add(customerWithoutDomainPanel);
		
		initSupportAgents();
		initCustomerWithoutAgent();
		initCustomerWithoutDomain();
		
		AonMessagePanel.showLoading(messagePanel, "Obteniendo informaci\u00f3n de los clientes y de los agentes de soporte...");
	}

	// -------------------------------- SUPPORT AGENTS / EMAILS

	private void initSupportAgents() {
		supportAgentsPanel.clear();
		supportAgentsPanel.add(createSupportAgentsToolbar());
		createSupportAgentsTable();
		supportAgentsPanel.add(supportAgentsTable);
	}

	private AonToolbarSmall createSupportAgentsToolbar() {
		AonToolbarSmall toolbar = new AonToolbarSmall("Agentes Soporte / Emails");
		
		toolbarSupportAgentsBtn = new AonToolbarSmallButton("Desplegar Agentes Soporte / Emails", AON.CSS.aonIconDown());
		toolbarSupportAgentsBtn.addClickHandler(e -> {
			isSupportAgentsOpen = !isSupportAgentsOpen;
			handleIcon(toolbarSupportAgentsBtn, isSupportAgentsOpen);
			if(isSupportAgentsOpen) supportAgentsTable.getElement().getStyle().clearDisplay();
			else supportAgentsTable.getElement().getStyle().setDisplay(Display.NONE);
		});
		
		toolbar.add(toolbarSupportAgentsBtn);
		
		return toolbar;
	}
	
	private void createSupportAgentsTable() {
		createSupportAgentsTableHeader();
		createSupportAgentsTableBody();
	}
	
	private void createSupportAgentsTableHeader() {
		supportAgentsTable = new Grid(0, 2);
		supportAgentsTable.clear();
		supportAgentsTable.setWidth("100%");

		int row = supportAgentsTable.insertRow(supportAgentsTable.getRowCount());

		Label name = new Label("AGENTE DE SOPORTE");
		Label email = new Label("EMAIL");
		
		name.addStyleName(AON.CSS.aonHeaderTable());
		email.addStyleName(AON.CSS.aonHeaderTable());

		supportAgentsTable.setWidget(row, 0, name);
		supportAgentsTable.setWidget(row, 1, email);
		
		supportAgentsTable.getCellFormatter().addStyleName(row, 0, AON.CSS.aonHeaderSticky());
		supportAgentsTable.getCellFormatter().addStyleName(row, 1, AON.CSS.aonHeaderSticky());
		
		supportAgentsTable.getColumnFormatter().getElement(0).getStyle().setWidth(50, Unit.PCT);
		supportAgentsTable.getColumnFormatter().getElement(1).getStyle().setWidth(50, Unit.PCT);
	}

	private void createSupportAgentsTableBody() {
		SERVICE.getActiveSupportAgents(options.getDomainName(), options.getDomain(), options.getUser(), new AsyncCallback<HashMap<Seller, RegistryMedia>>() {
			
			@Override
			public void onSuccess(HashMap<Seller, RegistryMedia> supportAgents) {
				supportAgents.entrySet().forEach(entry -> {
					Seller seller = entry.getKey();
					RegistryMedia registryMedia = entry.getValue();
					
					Label name = new Label(seller.getName());
					Label email = new Label(null != registryMedia.getId() ? registryMedia.getValue() : "No existe email para este agente de soporte");
					if(null == registryMedia.getId()) {
						name.addStyleName(AON.CSS.aonColorRed());
						email.addStyleName(AON.CSS.aonColorRed());
					}
					
					// Add row
					int newRow = supportAgentsTable.insertRow(supportAgentsTable.getRowCount());
					
					supportAgentsTable.setWidget(newRow, 0, name);
					supportAgentsTable.setWidget(newRow, 1, email);
					
					List<Label> rowLabels = new ArrayList<>();
					rowLabels.add(name);
					rowLabels.add(email);
					
					for(Label label : rowLabels) {
						label.addMouseOverHandler(e -> addHighlightRow(supportAgentsTable, newRow));
						label.addMouseOutHandler(e -> removeHighlightRow(supportAgentsTable, newRow));
					}
					
					if (newRow % 2 == 0) {
						name.addStyleName(AON.CSS.aonOddTableRow());
						email.addStyleName(AON.CSS.aonOddTableRow());
						
						supportAgentsTable.getCellFormatter().addStyleName(newRow, 0, AON.CSS.aonOddTableRow());
						supportAgentsTable.getCellFormatter().addStyleName(newRow, 1, AON.CSS.aonOddTableRow());
					}
					
					supportAgentsTable.getRowFormatter().getElement(newRow).getStyle().setHeight(25.00, Unit.PX);	
				});
			}
			
			@Override
			public void onFailure(Throwable caught) {
				dockLayoutPanel.add(new Label(AON.MSG.loadError( " [Interno: " + caught.getMessage() + "]")));
			}
		});
	}
	
	// -------------------------------- CUSTOMER / SUPPORT AGENTS

	private void initCustomerWithoutAgent() {
		customerWithoutAgentPanel.clear();
		customerWithoutAgentPanel.add(createCustomerWithoutAgentToolbar());
		createCustomerWithoutAgentTable();
		customerWithoutAgentPanel.add(customerWithoutAgentTable);
	}

	private AonToolbarSmall createCustomerWithoutAgentToolbar() {
		AonToolbarSmall toolbar = new AonToolbarSmall("Clientes con cuotas sin agente de soporte");
		
		toolbarCustomerWithoutAgentBtn = new AonToolbarSmallButton("Desplegar Clientes con cuotas sin agente de soporte", AON.CSS.aonIconDown());
		toolbarCustomerWithoutAgentBtn.addClickHandler(e -> {
			isCustomerWithoutAgentOpen = !isCustomerWithoutAgentOpen;
			handleIcon(toolbarCustomerWithoutAgentBtn, isCustomerWithoutAgentOpen);
			if(isCustomerWithoutAgentOpen) customerWithoutAgentTable.getElement().getStyle().clearDisplay();
			else customerWithoutAgentTable.getElement().getStyle().setDisplay(Display.NONE);
		});
		
		toolbar.add(toolbarCustomerWithoutAgentBtn);
		
		return toolbar;
	}
	
	private void createCustomerWithoutAgentTable() {
		createCustomerWithoutAgentTableHeader();
		createCustomerWithoutAgentTableBody();
	}
	
	private void createCustomerWithoutAgentTableHeader() {
		customerWithoutAgentTable = new Grid(0, 2);
		customerWithoutAgentTable.clear();
		customerWithoutAgentTable.setWidth("100%");

		int row = customerWithoutAgentTable.insertRow(customerWithoutAgentTable.getRowCount());

		Label code = new Label("CODIGO");
		Label name = new Label("NOMBRE");
		
		code.addStyleName(AON.CSS.aonHeaderTable());
		name.addStyleName(AON.CSS.aonHeaderTable());

		customerWithoutAgentTable.setWidget(row, 0, code);
		customerWithoutAgentTable.setWidget(row, 1, name);
		
		customerWithoutAgentTable.getCellFormatter().addStyleName(row, 0, AON.CSS.aonHeaderSticky());
		customerWithoutAgentTable.getCellFormatter().addStyleName(row, 1, AON.CSS.aonHeaderSticky());
		
		customerWithoutAgentTable.getColumnFormatter().getElement(0).getStyle().setWidth(100, Unit.PX);
	}

	private void createCustomerWithoutAgentTableBody() {
		SERVICE.getCustomerWithoutAgent(options.getDomainName(), options.getDomain(), options.getUser(), new AsyncCallback<List<Customer>>() {
			
			@Override
			public void onSuccess(List<Customer> customers) {
				customers.forEach(customer -> {
					Label code = new Label(customer.getId().toString());
					Label name = new Label(customer.getName());
					
					// Add row
					int newRow = customerWithoutAgentTable.insertRow(customerWithoutAgentTable.getRowCount());
					
					customerWithoutAgentTable.setWidget(newRow, 0, code);
					customerWithoutAgentTable.setWidget(newRow, 1, name);
					
					List<Label> rowLabels = new ArrayList<>();
					rowLabels.add(code);
					rowLabels.add(name);
					
					for(Label label : rowLabels) {
						label.addMouseOverHandler(e -> addHighlightRow(customerWithoutAgentTable, newRow));
						label.addMouseOutHandler(e -> removeHighlightRow(customerWithoutAgentTable, newRow));
					}
					
					if (newRow % 2 == 0) {
						code.addStyleName(AON.CSS.aonOddTableRow());
						name.addStyleName(AON.CSS.aonOddTableRow());
						
						customerWithoutAgentTable.getCellFormatter().addStyleName(newRow, 0, AON.CSS.aonOddTableRow());
						customerWithoutAgentTable.getCellFormatter().addStyleName(newRow, 1, AON.CSS.aonOddTableRow());
					}
					
					customerWithoutAgentTable.getRowFormatter().getElement(newRow).getStyle().setHeight(25.00, Unit.PX);	
				});
			}
			
			@Override
			public void onFailure(Throwable caught) {
				dockLayoutPanel.add(new Label(AON.MSG.loadError( " [Interno: " + caught.getMessage() + "]")));
			}
		});
	}
	
	// -------------------------------- CUSTOMER / DOMAIN

	private void initCustomerWithoutDomain() {
		customerWithoutDomainPanel.clear();
		customerWithoutDomainPanel.add(createCustomerWithoutDomainToolbar());
		createCustomerWithoutDomainTable();
		customerWithoutDomainPanel.add(customerWithoutDomainTable);
	}

	private AonToolbarSmall createCustomerWithoutDomainToolbar() {
		AonToolbarSmall toolbar = new AonToolbarSmall("Clientes sin dominio asociado");
		
		toolbarCustomerWithoutDomainBtn = new AonToolbarSmallButton("Desplegar Clientes sin dominio asociado", AON.CSS.aonIconDown());
		toolbarCustomerWithoutDomainBtn.addClickHandler(e -> {
			isCustomerWithoutDomainOpen = !isCustomerWithoutDomainOpen;
			handleIcon(toolbarCustomerWithoutDomainBtn, isCustomerWithoutDomainOpen);
			if(isCustomerWithoutDomainOpen) customerWithoutDomainTable.getElement().getStyle().clearDisplay();
			else customerWithoutDomainTable.getElement().getStyle().setDisplay(Display.NONE);
		});
		
		toolbar.add(toolbarCustomerWithoutDomainBtn);
		
		return toolbar;
	}
	
	private void createCustomerWithoutDomainTable() {
		createCustomerWithoutDomainTableHeader();
		createCustomerWithoutDomainTableBody();
	}
	
	private void createCustomerWithoutDomainTableHeader() {
		customerWithoutDomainTable = new Grid(0, 2);
		customerWithoutDomainTable.clear();
		customerWithoutDomainTable.setWidth("100%");

		int row = customerWithoutDomainTable.insertRow(customerWithoutDomainTable.getRowCount());

		Label code = new Label("CODIGO");
		Label name = new Label("NOMBRE");
		
		code.addStyleName(AON.CSS.aonHeaderTable());
		name.addStyleName(AON.CSS.aonHeaderTable());

		customerWithoutDomainTable.setWidget(row, 0, code);
		customerWithoutDomainTable.setWidget(row, 1, name);
		
		customerWithoutDomainTable.getCellFormatter().addStyleName(row, 0, AON.CSS.aonHeaderSticky());
		customerWithoutDomainTable.getCellFormatter().addStyleName(row, 1, AON.CSS.aonHeaderSticky());
		
		customerWithoutDomainTable.getColumnFormatter().getElement(0).getStyle().setWidth(100, Unit.PX);
	}

	private void createCustomerWithoutDomainTableBody() {
		String host =  Window.Location.getHost();
		String endPoint = "/ms/api/customer-support-agent/check-customer-sync-domains/";
		
		// Create a URL builder and add query parameters
		UrlBuilder urlBuilder = new UrlBuilder();
		urlBuilder.setProtocol(Window.Location.getProtocol()); // Use the current protocol
		urlBuilder.setHost(host);
		urlBuilder.setPath(endPoint);
		
		// Create the request builder with the complete URL
		RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.GET, urlBuilder.buildString());
		requestBuilder.setHeader("session_id", SESSION_API);
		
		try {
		    // Send the request
		    requestBuilder.sendRequest(null, new RequestCallback() {
		        public void onResponseReceived(Request request, Response response) {
		            if (response.getStatusCode() == 200) {
		                String responseBody = response.getText();
		                JSONValue json = JSONParser.parseStrict(responseBody);
		                JSONArray customersArr = json.isObject().get("customers").isArray();
		                
		                AonMessagePanel.hideMessage(messagePanel);
		                
		                for(int i=0; i < customersArr.size(); i++) {
		        			Label code = new Label(customersArr.get(i).isObject().get("id").isString().stringValue());
		        			Label customer = new Label(customersArr.get(i).isObject().get("name").isString().stringValue());
		        			
		        			// Add row
							int newRow = customerWithoutDomainTable.insertRow(customerWithoutDomainTable.getRowCount());
							
							customerWithoutDomainTable.setWidget(newRow, 0, code);
							customerWithoutDomainTable.setWidget(newRow, 1, customer);
							
							List<Label> rowLabels = new ArrayList<>();
							rowLabels.add(code);
							rowLabels.add(customer);
							
							for(Label label : rowLabels) {
								label.addMouseOverHandler(e -> addHighlightRow(customerWithoutDomainTable, newRow));
								label.addMouseOutHandler(e -> removeHighlightRow(customerWithoutDomainTable, newRow));
							}
							
							if (newRow % 2 == 0) {
								code.addStyleName(AON.CSS.aonOddTableRow());
								customer.addStyleName(AON.CSS.aonOddTableRow());
								
								customerWithoutDomainTable.getCellFormatter().addStyleName(newRow, 0, AON.CSS.aonOddTableRow());
								customerWithoutDomainTable.getCellFormatter().addStyleName(newRow, 1, AON.CSS.aonOddTableRow());
							}
							
							customerWithoutDomainTable.getRowFormatter().getElement(newRow).getStyle().setHeight(25.00, Unit.PX);	
		        		}
		            }
		        }

				public void onError(Request request, Throwable exception) {
					AonMessagePanel.showError(messagePanel, exception.getMessage());
		        }
		    });
		} catch (RequestException exception) {
			AonMessagePanel.showError(messagePanel, exception.getMessage());
		}
	}

	// -------------------------------- AUXILIAR METHDomain
	private void handleIcon(AonToolbarSmallButton button, boolean open) {
		if (open) {
			button.removeStyleName(AON.CSS.aonIconLeft());
			button.addStyleName(AON.CSS.aonIconDown());

			if (button.equals(toolbarSupportAgentsBtn))
				button.setTitle("Colapsar Agentes Soporte / Emails");
			if (button.equals(toolbarCustomerWithoutAgentBtn))
				button.setTitle("Colapsar Clientes con cuotas sin agente de soporte");
			if (button.equals(toolbarCustomerWithoutDomainBtn))
				button.setTitle("Colapsar Clientes sin dominio asociado");
		} else {
			button.removeStyleName(AON.CSS.aonIconDown());
			button.addStyleName(AON.CSS.aonIconLeft());

			if (button.equals(toolbarSupportAgentsBtn))
				button.setTitle("Desplegar Agentes Soporte / Emails");
			if (button.equals(toolbarCustomerWithoutAgentBtn))
				button.setTitle("Desplegar Clientes con cuotas sin agente de soporte");
			if (button.equals(toolbarCustomerWithoutDomainBtn))
				button.setTitle("Desplegar Clientes sin dominio asociado");
		}
	}
	
	private void addHighlightRow(Grid grid, int row) {
		grid.getRowFormatter().addStyleName(row, AON.CSS.aonRowHighlight());
		for(int column=0; column < grid.getColumnCount(); column++) {
			grid.getWidget(row, column).addStyleName(AON.CSS.aonRowHighlight());
			grid.getCellFormatter().addStyleName(row, column, AON.CSS.aonRowHighlight());
		}
	}

	private void removeHighlightRow(Grid grid, int row) {
		grid.getRowFormatter().removeStyleName(row, AON.CSS.aonRowHighlight());
		for(int column=0; column < grid.getColumnCount(); column++) {
			grid.getWidget(row, column).removeStyleName(AON.CSS.aonRowHighlight());
			grid.getCellFormatter().removeStyleName(row, column, AON.CSS.aonRowHighlight());
		}
	}

	// -------------------------------- TOOLBAR
	
	private void createToolbar() {
		toolbar = new AonToolbar("Agentes Soporte / Clientes");
		
		backButton = new AonToolbarButton("Volver", AON.CSS.aonIconBack());
		backButton.addClickHandler(e -> showInfo());
		
		toolbar.add(backButton);
		
		syncSupportAgent = new AonToolbarButton("Sincronizar Agentes Soporte", AON.CSS.aonIconSync());
		syncSupportAgent.addClickHandler(e -> syncCustomerSupportAgent());
		
		toolbar.add(syncSupportAgent);
	}

	private void showInfo() {
		backButton.setVisible(false);
		deckPanel.showWidget(0);
	}
	
	private void showLog() {
		backButton.setVisible(true);
		deckPanel.showWidget(1);
	}

	private void syncCustomerSupportAgent() {
		AonMessagePanel.showLoading(messagePanel, "Sincronizando agentes de soporte con dominio del cliente...");
		
		String host = Window.Location.getHost();
		String endPoint = "/ms/api/customer-support-agent/";
		
		// Create a URL builder and add query parameters
		UrlBuilder urlBuilder = new UrlBuilder();
		urlBuilder.setProtocol(Window.Location.getProtocol()); // Use the current protocol
		urlBuilder.setHost(host);
		urlBuilder.setPath(endPoint);
		
		// Create the request builder with the complete URL
		RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.PUT, urlBuilder.buildString());
		requestBuilder.setHeader("session_id", SESSION_API);
		
		try {
		    // Send the request
		    requestBuilder.sendRequest(null, new RequestCallback() {
		        public void onResponseReceived(Request request, Response response) {
		            if (response.getStatusCode() == 200) {
		            	showLog();
		                String responseBody = response.getText();
		                AonMessagePanel.showSuccess(messagePanel, "Sincronizaci\u00f3n finalizada correctamente. Compruebe el log generado.");
		                JSONValue json = JSONParser.parseStrict(responseBody);
		                printSuccessMessages(json.isObject().get("success").isArray());
		                printErrorsMessages(json.isObject().get("error").isArray());
		            }
		        }

				public void onError(Request request, Throwable exception) {
					AonMessagePanel.showError(messagePanel, exception.getMessage());
		        }
		    });
		} catch (RequestException exception) {
			AonMessagePanel.showError(messagePanel, exception.getMessage());
		}
		
	}

	private void printSuccessMessages(JSONArray successArr) {
		scrollLogContainer.clear();
		
		Label message = new Label("Sincronizaciones realizadas");
		message.getElement().getStyle().setProperty("font-size", ".9rem");
		message.getElement().getStyle().setProperty("font-weight", "bold");
		
		scrollLogContainer.add(message);
		
		for(int i=0; i < successArr.size(); i++) {
			HTMLPanel success = new HTMLPanel(successArr.get(i).isObject().get("success").isString().stringValue());
			success.getElement().getStyle().setProperty("padding-left", "1rem");
			scrollLogContainer.add(success);
		}
	}

	private void printErrorsMessages(JSONArray errorArr) {
		Label message = new Label("Errores encontrados");
		message.getElement().getStyle().setProperty("font-size", ".9rem");
		message.getElement().getStyle().setProperty("font-weight", "bold");
		
		scrollLogContainer.add(message);
		
		for(int i=0; i < errorArr.size(); i++) {
			HTMLPanel error = new HTMLPanel(errorArr.get(i).isObject().get("error").isString().stringValue());
			error.getElement().getStyle().setProperty("padding-left", "1rem");
			scrollLogContainer.add(error);
		}
	}
	
}
