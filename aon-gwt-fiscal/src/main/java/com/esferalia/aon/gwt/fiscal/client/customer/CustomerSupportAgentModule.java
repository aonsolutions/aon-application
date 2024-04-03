package com.esferalia.aon.gwt.fiscal.client.customer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
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
import com.esferalia.aon.gwt.fiscal.client.booking.BookingApi;
import com.esferalia.aon.gwt.fiscal.client.registry.RegistryModuleOptions;
import com.esferalia.aon.occam.api.model.Customer;
import com.esferalia.aon.occam.api.model.DomainCompany;
import com.esferalia.aon.occam.api.model.RegistryParams;
import com.esferalia.aon.occam.api.model.registry.RegistryMedia;
import com.esferalia.aon.occam.api.model.registry.Seller;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
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
	
	// Log Sync Support Agent
	private HTMLPanel logPanel;
	private AonToolbarSmallButton toolbarLogBtn;
	private Grid logTable;
	private boolean isLogOpen = true;
	private boolean isDescCustomerSort = true;
	private boolean isDescTypeSort = true;
	private ArrayList<CustomerLog> customerLogs = new ArrayList<CustomerLog>();
	
	
	// Api
	private CustomerApi customerApi;
	private BookingApi bookingApi;
	private static String SESSION_API = "AONd95770f269e711eb94390242ac130002";
	private Integer customerCount = 0;
	private boolean isLocalDev = false;
	
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
		
		this.customerApi = new CustomerApi(SESSION_API);
		this.bookingApi = new BookingApi(SESSION_API);
		
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
		
		logPanel = new HTMLPanel("");
		logPanel.addStyleName(AON.CSS.aonFlexColumn());
		scrollLogContainer.add(logPanel);
		
		AonMessagePanel.showLoading(messagePanel, "Obteniendo los agentes de soporte...");
		
		initSupportAgents();
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
				
				AonMessagePanel.showLoading(messagePanel, "Obteniendo clientes sin agente de soporte asignado...");
				initCustomerWithoutAgent();
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
				
				AonMessagePanel.showLoading(messagePanel, "Obteniendo clientes sin dominio asociado...");
				initCustomerWithoutDomain();
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
		
		SERVICE.getCustomers(
				options.getDomainName(), 
				options.getDomain(), 
				options.getUser(), 
				new RegistryParams().setDomain(options.getDomain()).setActive(true), 
				0, 
				Integer.MAX_VALUE, 
				new AsyncCallback<LinkedList<Customer>>() {
					
					@Override
					public void onSuccess(LinkedList<Customer> customers) {
						customers.sort((o1, o2) -> o1.getName().compareTo(o2.getName()));
						
						customerCount = 0;
						Label messageLabel = AonMessagePanel.showLoading(messagePanel, "Comprobando cliente " + customers.get(customerCount).getName() + " ...");
						checkCustomerSync(customers, customers.size(), messageLabel);
						
					}
					
					private void checkCustomerSync(LinkedList<Customer> customers, int totalCustomers, Label messageLabel) {
						Customer customer = customers.get(customerCount);
						
						messageLabel.setText("Comprobando cliente " + customer.getName() + " ...");
						
						String host = isLocalDev ? "localhost:8080" : "aon.solutions";
						String endPoint = "/ms/api/customers-support-agent/" + customer.getId().toString();
						
						customerApi.checkCustomerDomianSync(host, endPoint, new AsyncCallback<String>() {
							
							@Override
							public void onSuccess(String message) {
								if(AonStringUtils.isBlank(message)) {
									Label code = new Label(customer.getId().toString());
				        			Label customerLabel = new Label(customer.getName());
				        			
				        			// Add row
									int newRow = customerWithoutDomainTable.insertRow(customerWithoutDomainTable.getRowCount());
									
									customerWithoutDomainTable.setWidget(newRow, 0, code);
									customerWithoutDomainTable.setWidget(newRow, 1, customerLabel);
									
									List<Label> rowLabels = new ArrayList<>();
									rowLabels.add(code);
									rowLabels.add(customerLabel);
									
									for(Label label : rowLabels) {
										label.addMouseOverHandler(e -> addHighlightRow(customerWithoutDomainTable, newRow));
										label.addMouseOutHandler(e -> removeHighlightRow(customerWithoutDomainTable, newRow));
									}
									
									if (newRow % 2 == 0) {
										code.addStyleName(AON.CSS.aonOddTableRow());
										customerLabel.addStyleName(AON.CSS.aonOddTableRow());
										
										customerWithoutDomainTable.getCellFormatter().addStyleName(newRow, 0, AON.CSS.aonOddTableRow());
										customerWithoutDomainTable.getCellFormatter().addStyleName(newRow, 1, AON.CSS.aonOddTableRow());
									}
									
									customerWithoutDomainTable.getRowFormatter().getElement(newRow).getStyle().setHeight(25.00, Unit.PX);
								}
								
								if(customerCount == (totalCustomers - 1))
									AonMessagePanel.hideMessage(messagePanel);
								else {
									customerCount++;
									checkCustomerSync(customers, totalCustomers, messageLabel);
								}
								
							}
							
							@Override
							public void onFailure(Throwable caught) {
								dockLayoutPanel.add(new Label(AON.MSG.loadError( " [Interno: " + caught.getMessage() + "]")));
								customerCount++;
								
							}
						});
					}

					@Override
					public void onFailure(Throwable caught) {
						dockLayoutPanel.add(new Label(AON.MSG.loadError( " [Interno: " + caught.getMessage() + "]")));
					}
				});
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
			if (button.equals(toolbarLogBtn))
				button.setTitle("Colapsar Log sincronizaci\u00f3n");
		} else {
			button.removeStyleName(AON.CSS.aonIconDown());
			button.addStyleName(AON.CSS.aonIconLeft());

			if (button.equals(toolbarSupportAgentsBtn))
				button.setTitle("Desplegar Agentes Soporte / Emails");
			if (button.equals(toolbarCustomerWithoutAgentBtn))
				button.setTitle("Desplegar Clientes con cuotas sin agente de soporte");
			if (button.equals(toolbarCustomerWithoutDomainBtn))
				button.setTitle("Desplegar Clientes sin dominio asociado");
			if (button.equals(toolbarLogBtn))
				button.setTitle("Desplegar Log sincronizaci\u00f3n");
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
		showLog();
		initLog();
	}
	
	// -------------------------------- SUPPORT AGENTS / EMAILS

	private void initLog() {
		logPanel.clear();
		logPanel.add(createLogToolbar());
		createLogTable();
	}

	private AonToolbarSmall createLogToolbar() {
		AonToolbarSmall toolbar = new AonToolbarSmall("Log sincronizaci\u00f3n");
		
		toolbarLogBtn = new AonToolbarSmallButton("Desplegar Log sincronizaci\u00f3n", AON.CSS.aonIconDown());
		toolbarLogBtn.addClickHandler(e -> {
			isLogOpen = !isLogOpen;
			handleIcon(toolbarLogBtn, isLogOpen);
			if(isLogOpen) logTable.getElement().getStyle().clearDisplay();
			else logTable.getElement().getStyle().setDisplay(Display.NONE);
		});
		
		toolbar.add(toolbarLogBtn);
		
		return toolbar;
	}
	
	private void createLogTable() {
		createLogTableHeader();
		createLogTableBody();
	}
	
	private void createLogTableHeader() {
		logTable = new Grid(0, 3);
		logTable.clear();
		logTable.setWidth("100%");

		int row = logTable.insertRow(logTable.getRowCount());

		Label customer = new Label("CLIENTE");
		Label type = new Label("TIPO");
		Label message = new Label("MENSAJE");
		
		customer.addStyleName(AON.CSS.aonHeaderTable());
		customer.getElement().getStyle().setProperty("cursor", "pointer");
		type.addStyleName(AON.CSS.aonHeaderTable());
		type.getElement().getStyle().setProperty("cursor", "pointer");
		message.addStyleName(AON.CSS.aonHeaderTable());
		
		customer.addClickHandler(e -> sortLogTableByName());
		type.addClickHandler(e -> sortLogTableByType());

		logTable.setWidget(row, 0, customer);
		logTable.setWidget(row, 1, type);
		logTable.setWidget(row, 2, message);
		
		logTable.getCellFormatter().addStyleName(row, 0, AON.CSS.aonHeaderSticky());
		logTable.getCellFormatter().addStyleName(row, 1, AON.CSS.aonHeaderSticky());
		logTable.getCellFormatter().addStyleName(row, 2, AON.CSS.aonHeaderSticky());
		
		logTable.getColumnFormatter().getElement(0).getStyle().setWidth(400, Unit.PX);
		logTable.getColumnFormatter().getElement(1).getStyle().setWidth(200, Unit.PX);
		
		logPanel.add(logTable);
	}

	private void sortLogTableByName() {
		logPanel.clear();
		
		isDescCustomerSort = !isDescCustomerSort;
		
		createLogTableHeader();
		
		if(isDescCustomerSort) customerLogs.sort((o1, o2) -> o1.getCustomerName().compareTo(o2.getCustomerName()));
		else customerLogs.sort((o1, o2) -> o2.getCustomerName().compareTo(o1.getCustomerName()));
		
		customerLogs.forEach(cusrtomerLog -> addLogRow(cusrtomerLog.getCustomerName(), cusrtomerLog.getType().getDescription(), cusrtomerLog.getMessage()));
	}

	private void sortLogTableByType() {
		logPanel.clear();
		
		isDescTypeSort = !isDescTypeSort;
		
		if(isDescTypeSort) customerLogs.sort((o1, o2) -> o1.getType().getDescription().compareTo(o2.getType().getDescription()));
		else customerLogs.sort((o1, o2) -> o2.getType().getDescription().compareTo(o1.getType().getDescription()));
		
        createLogTableHeader();
        
        customerLogs.forEach(cusrtomerLog -> addLogRow(cusrtomerLog.getCustomerName(), cusrtomerLog.getType().getDescription(), cusrtomerLog.getMessage()));
	}

	private void createLogTableBody() {
		customerLogs = new ArrayList<CustomerLog>();
		
		SERVICE.getCustomers(
				options.getDomainName(), 
				options.getDomain(), 
				options.getUser(), 
				new RegistryParams().setDomain(options.getDomain()).setActive(true), 
				0, 
				Integer.MAX_VALUE, 
				new AsyncCallback<LinkedList<Customer>>() {
					
					@Override
					public void onSuccess(LinkedList<Customer> customers) {
						customers.sort((o1, o2) -> o1.getName().compareTo(o2.getName()));
						
						customerCount = 0;
						Label messageLabel = AonMessagePanel.showLoading(messagePanel, "Asignando agente soporte al cliente " + customers.get(customerCount).getName() + " ...");
						
						try {
							checkCustomerSync(customers, customers.size(), messageLabel);
						} catch (Exception e) {
							Window.alert("Fail checkCustomerSync --> customerCount: " + customerCount + " / customersSize: " + customers.size());
						}
						
					}
					
					private void checkCustomerSync(LinkedList<Customer> customers, int totalCustomers, Label messageLabel) {
						Customer customer = customers.get(customerCount);
						
						messageLabel.setText("Asignando agente soporte al cliente " + customer.getName() + " ...");
						
						String host = isLocalDev ? "localhost:8080" : "aon.solutions";
						String endPoint = "/ms/api/domain/" + customer.getId().toString();
						
						bookingApi.getDomainCompanies(host, endPoint, new AsyncCallback<List<DomainCompany>>() {
							
							@Override
							public void onSuccess(List<DomainCompany> domainCompanies) {
								if(domainCompanies != null && !domainCompanies.isEmpty()) {
									
									// Check if customer has RSeller
									SERVICE.getCustomerSeller(options.getDomainName(), options.getDomain(), options.getUser(), customer.getId(), new AsyncCallback<Seller>() {
										
										@Override
										public void onSuccess(Seller seller) {
											if(seller.getId() == null) {
												addLogRow(customer.getName(), "Cliente / Agente Soporte", "El cliente " + customer.getName() + " no tiene agente de soporte asociado");
												customerLogs.add(new CustomerLog(customer.getName(), LogTypeEnum.CUSTOMER_SUPPORT_AGENT, "El cliente " + customer.getName() + " no tiene agente de soporte asociado"));
												
												if(customerCount == (totalCustomers - 1))
													AonMessagePanel.hideMessage(messagePanel);
							                	else {
							                		customerCount++;
							                		try {
														checkCustomerSync(customers, totalCustomers, messageLabel);
													} catch (Exception e) {
														Window.alert("Fail checkCustomerSync --> customerCount: " + customerCount + " / customersSize: " + customers.size());
													}
							                	}
							                	
											} else {
												
												SERVICE.getCustomerSellerEmail(options.getDomainName(), options.getDomain(), options.getUser(), customer.getId(), new AsyncCallback<String>() {
													
													@Override
													public void onSuccess(String sellerEmail) {
														if(AonStringUtils.isBlank(sellerEmail)) {
															addLogRow(customer.getName(), "Agente Soporte", "El agente de soporte " + seller.getName() + " no tiene email registrado en su ficha");
															customerLogs.add(new CustomerLog(customer.getName(), LogTypeEnum.SUPPORT_AGENT_EMAIL, "El agente de soporte " + seller.getName() + " no tiene email registrado en su ficha"));
															
															if(customerCount == (totalCustomers - 1))
																AonMessagePanel.hideMessage(messagePanel);
										                	else {
										                		customerCount++;
										                		try {
																	checkCustomerSync(customers, totalCustomers, messageLabel);
																} catch (Exception e) {
																	Window.alert("Fail checkCustomerSync --> customerCount: " + customerCount + " / customersSize: " + customers.size());
																}
										                	}
										                	
														} else {
															
															DomainCompany domainCompany = domainCompanies.get(0);
										            		
										            		String syncHost = isLocalDev ? "localhost:8080" : "aon.solutions";
															String syncEndPoint = "/ms/api/customers-support-agent/" + customer.getId();
															
															JSONObject body = new JSONObject();
											        		body.put("schema", new JSONString(domainCompany.getSchema()));
											        		body.put("domainName", new JSONString(domainCompany.getDomain().getName()));
											        		body.put("domainId", new JSONString(domainCompany.getDomain().getId().toString()));
											        		body.put("owner", new JSONString(sellerEmail));
															
															customerApi.syncSupportAgentCustomer(syncHost, syncEndPoint, body, new AsyncCallback<Void>() {
																
																@Override
																public void onSuccess(Void result) {
																	addLogRow(customer.getName(), "Sincronizac\u00f3n", "El agente de soporte " + seller.getName() + " ha sido asigando como gestor del dominio " + domainCompany.getDomain().getDescription());
																	customerLogs.add(new CustomerLog(customer.getName(), LogTypeEnum.SYNC, "El agente de soporte " + seller.getName() + " ha sido asigando como gestor del dominio " + domainCompany.getDomain().getDescription()));
																	
																	if(customerCount == (totalCustomers - 1))
																		AonMessagePanel.hideMessage(messagePanel);
																	else {
																		customerCount++;
																		try {
																			checkCustomerSync(customers, totalCustomers, messageLabel);
																		} catch (Exception e) {
																			Window.alert("Fail checkCustomerSync --> customerCount: " + customerCount + " / customersSize: " + customers.size());
																		}
																	}
																}
																
																@Override
																public void onFailure(Throwable caught) {
																	dockLayoutPanel.add(new Label(AON.MSG.loadError( " [Interno: " + caught.getMessage() + "]")));
																}
															});
															
														}
														
														
													}
													
													@Override
													public void onFailure(Throwable caught) {
														dockLayoutPanel.add(new Label(AON.MSG.loadError( " [Interno: " + caught.getMessage() + "]")));
													}
												});
												
											}
											
											customerCount++;
											try {
												checkCustomerSync(customers, totalCustomers, messageLabel);
											} catch (Exception e) {
												Window.alert("Fail checkCustomerSync --> customerCount: " + customerCount + " / customersSize: " + customers.size());
											}
										}
										
										@Override
										public void onFailure(Throwable caught) {
											dockLayoutPanel.add(new Label(AON.MSG.loadError( " [Interno: " + caught.getMessage() + "]")));
										}
									});
				
				                } else {
				                	addLogRow(customer.getName(), "Dominio Asociado", "El cliente " + customer.getName() + " no tiene dominio asociado");
				                	customerLogs.add(new CustomerLog(customer.getName(), LogTypeEnum.CUSTOMER_DOMAIN_SYNC, "El cliente " + customer.getName() + " no tiene dominio asociado"));
									
				                	if(customerCount == (totalCustomers - 1))
										AonMessagePanel.hideMessage(messagePanel);
				                	else {
				                		customerCount++;
				                		try {
											checkCustomerSync(customers, totalCustomers, messageLabel);
										} catch (Exception e) {
											Window.alert("Fail checkCustomerSync --> customerCount: " + customerCount + " / customersSize: " + customers.size());
										}
				                	}
				                	
				                }
							}

							@Override
							public void onFailure(Throwable caught) {
								dockLayoutPanel.add(new Label(AON.MSG.loadError( " [Interno: " + caught.getMessage() + "]")));
							}
						});
						
					}

					@Override
					public void onFailure(Throwable caught) {
						dockLayoutPanel.add(new Label(AON.MSG.loadError( " [Interno: " + caught.getMessage() + "]")));
					}
				});
	}
	
	private void addLogRow(String customer, String type, String message) {
		Label customerLabel = new Label(customer);
		Label typeLabel = new Label(type);
		Label messageLabel = new Label(message);
		
		if(AonStringUtils.equalsIgnoreCase(type, "Sincronizac\u00f3n")) {
			typeLabel.getElement().getStyle().setProperty("color", "green");
			typeLabel.getElement().getStyle().setProperty("font-weight", "bold");
		} else if(AonStringUtils.equalsIgnoreCase(type, "Cliente / Agente Soporte")) {
			typeLabel.getElement().getStyle().setProperty("color", "orange");
			typeLabel.getElement().getStyle().setProperty("font-weight", "bold");
		} else if(AonStringUtils.equalsIgnoreCase(type, "Agente Soporte")) {
			typeLabel.getElement().getStyle().setProperty("color", "red");
			typeLabel.getElement().getStyle().setProperty("font-weight", "bold");
		}
		
		// Add row
		int newRow = logTable.insertRow(logTable.getRowCount());
		
		logTable.setWidget(newRow, 0, customerLabel);
		logTable.setWidget(newRow, 1, typeLabel);
		logTable.setWidget(newRow, 2, messageLabel);
		
		List<Label> rowLabels = new ArrayList<>();
		rowLabels.add(customerLabel);
		rowLabels.add(typeLabel);
		rowLabels.add(messageLabel);
		
		for(Label label : rowLabels) {
			label.addMouseOverHandler(e -> addHighlightRow(logTable, newRow));
			label.addMouseOutHandler(e -> removeHighlightRow(logTable, newRow));
		}
		
		if (newRow % 2 == 0) {
			customerLabel.addStyleName(AON.CSS.aonOddTableRow());
			typeLabel.addStyleName(AON.CSS.aonOddTableRow());
			messageLabel.addStyleName(AON.CSS.aonOddTableRow());
			
			logTable.getCellFormatter().addStyleName(newRow, 0, AON.CSS.aonOddTableRow());
			logTable.getCellFormatter().addStyleName(newRow, 1, AON.CSS.aonOddTableRow());
			logTable.getCellFormatter().addStyleName(newRow, 2, AON.CSS.aonOddTableRow());
		}
		
		logTable.getRowFormatter().getElement(newRow).getStyle().setHeight(25.00, Unit.PX);	
	}
	
	// -------------------------------- CUSTOMER LOG
	
	private enum LogTypeEnum {
		CUSTOMER_DOMAIN_SYNC("Dominio Asociado"),
		CUSTOMER_SUPPORT_AGENT("Cliente / Agente Soporte"),
		SUPPORT_AGENT_EMAIL("Agente Soporte"),
		SYNC("Sincronizac\u00f3n")
		;
		
		private String description;
		
		LogTypeEnum(String description) {
			this.description = description;
		}
		
		public String getDescription() {
			return this.description;
		}
	}
	
	private class CustomerLog {
		
		private String customerName;
		private LogTypeEnum type;
		private String message;
		
		public CustomerLog(String customerName, LogTypeEnum type, String message) {
			super();
			this.customerName = customerName;
			this.type = type;
			this.message = message;
		}
		
		public String getCustomerName() {
			return customerName;
		}
		
		public LogTypeEnum getType() {
			return type;
		}
		
		public String getMessage() {
			return message;
		}
		
	}
	
}
