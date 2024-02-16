package com.esferalia.aon.gwt.fiscal.client.booking;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.RegistryService;
import com.esferalia.aon.gwt.common.client.RegistryServiceAsync;
import com.esferalia.aon.gwt.common.client.RegistryServiceAsyncDecorator;
import com.esferalia.aon.gwt.common.client.RootLayoutPanel;
import com.esferalia.aon.gwt.common.client.json.DomainCompanyJSON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog.AonAcceptDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDomainSyncSelectionDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessagePanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTableButton;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbar;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.gwt.fiscal.client.MainEntryPoint;
import com.esferalia.aon.gwt.fiscal.client.registry.RegistryModuleOptions;
import com.esferalia.aon.occam.api.model.Customer;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.DomainCompany;
import com.esferalia.aon.occam.api.model.RegistryParams;
import com.esferalia.aon.occam.api.model.security.Booking;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.type.AonStatus;
import com.esferalia.aon.occam.api.model.type.RegistryStatus;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.http.client.Response;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ScrollPanel;

public class CustomerBookingResumeModule extends MainEntryPoint {
	
	// Services
	private static RegistryServiceAsync SERVICE;

	// Options Config
	private RegistryModuleOptions options;

	// Content
	private DockLayoutPanel dockLayoutPanel;
	
	private ScrollPanel scrollPanel;
	private HTMLPanel container;
	
	private HTMLPanel customerPanel;
	private HTMLPanel domainsPanel;
	
	private HTMLPanel domainPanel;
	private HTMLPanel domainChildsPanel;
	
	private HTMLPanel messagePanel;

	private AonToolbar toolbar;
	private AonToolbarButton backBtn;
	private ListBox enterprisesView;
	private AonToolbarButton refreshBtn;
	
	private Booking domainBooking;
	
	private Customer customer;
	private List<DomainCompany> customerDomains;
	
	private RegistryModuleOptions opt;
	
	private DateTimeFormat formatDate = DateTimeFormat.getFormat("dd/MM/yyyy");
	private DateTimeFormat formatBillingDate = DateTimeFormat.getFormat("MM/yyyy");
	
	// Api
	private BookingApi bookingApi;
	private static String SESSION_API = "AONd95770f269e711eb94390242ac130002";
	private boolean isLocalDev = false;
	
	@Override
	public void onModuleLoad() {
		RootLayoutPanel root = RootLayoutPanel.get(getRootPanel() != null ? getRootPanel() : "rootPanel");
		options = new RegistryModuleOptions();
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
		
		this.opt = opt;
		this.bookingApi = new BookingApi(SESSION_API);
		
		dockLayoutPanel = new DockLayoutPanel(Unit.PX);
		this.opt.getParentWidget().add(dockLayoutPanel);
		
		dockLayoutPanel.clear();
		
		Integer customerId = getCustomer();
		RegistryParams params = new RegistryParams()
				.setId(customerId)
				.setDomain(options.getDomain());
		
		SERVICE.getCustomers(options.getDomainName(), options.getDomain(), options.getUser(), params, 0, 1, new AsyncCallback<LinkedList<Customer>>() {
			
			@Override
			public void onSuccess(LinkedList<Customer> customers) {
				if(customers != null && !customers.isEmpty()) {
					customer = customers.get(0);
				}
				loadModule();
			}
			
			@Override
			public void onFailure(Throwable caught) {
				dockLayoutPanel.add(new Label(AON.MSG.loadError( " [Interno: " + caught.getMessage() + "]")));
			}
		});
	}

	private void loadModule() {
		dockLayoutPanel.clear();
		
		createToolbar();
		dockLayoutPanel.addNorth(toolbar, 50);
		
		scrollPanel = new ScrollPanel();
		scrollPanel.setHeight((Window.getClientHeight() - 170) + "px");
		
		HTMLPanel mainContainer = new HTMLPanel("");
		mainContainer.addStyleName(AON.CSS.aonFlexColumn());
		
		container = new HTMLPanel("");
		container.addStyleName(AON.CSS.aonFlexColumn());
		container.getElement().getStyle().setProperty("margin", "0 1rem");
		
		messagePanel = new HTMLPanel("");
		
		customerPanel = new HTMLPanel("");
		customerPanel.addStyleName(AON.CSS.aonFlexColumn());
		
		domainsPanel = new HTMLPanel("");
		domainsPanel.addStyleName(AON.CSS.aonFlexColumn());
		
		domainPanel = new HTMLPanel("");
		domainPanel.addStyleName(AON.CSS.aonFlexColumn());
		
		domainChildsPanel = new HTMLPanel("");
		domainChildsPanel.addStyleName(AON.CSS.aonFlexColumn());
		
		container.add(customerPanel);
		container.add(domainsPanel);
		
		container.add(domainPanel);
		container.add(domainChildsPanel);
		
		mainContainer.add(messagePanel);
		mainContainer.add(container);
		
		scrollPanel.add(mainContainer);
		
		dockLayoutPanel.add(scrollPanel);
			
		checkCustomerDomains();
		getBookingResume();
	}
	
	private void checkCustomerDomains() {
		AonMessagePanel.showLoading(messagePanel, "Obteniendo dominios del cliente ...");
		
		String host = isLocalDev ? "localhost:8080" : "aon.solutions";
		String endPoint = "/ms/api/domain/" + customer.getId().toString();
		
		this.bookingApi.getDomainCompanies(host, endPoint, new AsyncCallback<List<DomainCompany>>() {
			
			@Override
			public void onSuccess(List<DomainCompany> domainCompanies) {
				AonMessagePanel.hideMessage(messagePanel);
                setBookingCustomer(customer, domainCompanies);
			}
			
			@Override
			public void onFailure(Throwable exception) {
				AonMessagePanel.showError(messagePanel, exception.getMessage());
			}
		});
	}
	
	private void setBookingCustomer(Customer customer, List<DomainCompany> customerDomains) {
		this.customer = customer;
		this.customerDomains = customerDomains;
		
		this.customerPanel.clear();
		this.domainsPanel.clear();
		
		initializeCustomer();
		initializeDomains();
	}
	
	// ---------- Cliente

	private void initializeCustomer() {
		Grid customerTable = new Grid(0, 11);
		customerTable.clear();
		customerTable.setWidth("100%");

		int row = customerTable.insertRow(customerTable.getRowCount());

		Label type = new Label("TIPO");
		Label id = new Label("ID");
		Label eschema = new Label("ESQUEMA");
		Label name = new Label("NOMBRE");
		Label document = new Label("DOCUMENTO");
		Label status = new Label("ESTADO");
		Label billable = new Label("FACT.");
		Label creation = new Label("CREACION");
		Label lastModif = new Label("ULT. MODIF.");
		Label alias = new Label("ALIAS");
		Label action = new Label("");
		
		type.addStyleName(AON.CSS.aonHeaderTable());
		id.addStyleName(AON.CSS.aonHeaderTable());
		eschema.addStyleName(AON.CSS.aonHeaderTable());
		name.addStyleName(AON.CSS.aonHeaderTable());
		document.addStyleName(AON.CSS.aonHeaderTable());
		status.addStyleName(AON.CSS.aonHeaderTable());
		billable.addStyleName(AON.CSS.aonHeaderTable());
		creation.addStyleName(AON.CSS.aonHeaderTable());
		lastModif.addStyleName(AON.CSS.aonHeaderTable());
		alias.addStyleName(AON.CSS.aonHeaderTable());
		action.addStyleName(AON.CSS.aonHeaderTable());

		customerTable.setWidget(row, 0, type);
		customerTable.setWidget(row, 1, id);
		customerTable.setWidget(row, 2, eschema);
		customerTable.setWidget(row, 3, name);
		customerTable.setWidget(row, 4, document);
		customerTable.setWidget(row, 5, status);
		customerTable.setWidget(row, 6, billable);
		customerTable.setWidget(row, 7, creation);
		customerTable.setWidget(row, 8, lastModif);
		customerTable.setWidget(row, 9, alias);
		customerTable.setWidget(row, 10, action);
		
		customerTable.getCellFormatter().addStyleName(row, 0, AON.CSS.aonHeaderSticky());
		customerTable.getCellFormatter().addStyleName(row, 1, AON.CSS.aonHeaderSticky());
		customerTable.getCellFormatter().addStyleName(row, 2, AON.CSS.aonHeaderSticky());
		customerTable.getCellFormatter().addStyleName(row, 3, AON.CSS.aonHeaderSticky());
		customerTable.getCellFormatter().addStyleName(row, 4, AON.CSS.aonHeaderSticky());
		customerTable.getCellFormatter().addStyleName(row, 5, AON.CSS.aonHeaderSticky());
		customerTable.getCellFormatter().addStyleName(row, 6, AON.CSS.aonHeaderSticky());
		customerTable.getCellFormatter().addStyleName(row, 7, AON.CSS.aonHeaderSticky());
		customerTable.getCellFormatter().addStyleName(row, 8, AON.CSS.aonHeaderSticky());
		customerTable.getCellFormatter().addStyleName(row, 9, AON.CSS.aonHeaderSticky());
		customerTable.getCellFormatter().addStyleName(row, 10, AON.CSS.aonHeaderSticky());
		
		customerTable.getColumnFormatter().getElement(0).getStyle().setWidth(65, Unit.PX);
		customerTable.getColumnFormatter().getElement(1).getStyle().setWidth(60, Unit.PX);
		customerTable.getColumnFormatter().getElement(2).getStyle().setWidth(190, Unit.PX);

		customerTable.getColumnFormatter().getElement(4).getStyle().setWidth(80, Unit.PX);
		customerTable.getColumnFormatter().getElement(5).getStyle().setWidth(60, Unit.PX);
		customerTable.getColumnFormatter().getElement(6).getStyle().setWidth(50, Unit.PX);
		customerTable.getColumnFormatter().getElement(7).getStyle().setWidth(80, Unit.PX);
		customerTable.getColumnFormatter().getElement(8).getStyle().setWidth(80, Unit.PX);
		customerTable.getColumnFormatter().getElement(9).getStyle().setWidth(215, Unit.PX);
		customerTable.getColumnFormatter().getElement(10).getStyle().setWidth(25, Unit.PX);
		
		int newRow = customerTable.insertRow(customerTable.getRowCount());
		
		Label domainTypeLabel  = new Label("Cliente");
		Label idLabel = new Label(this.customer.getId().toString());
		Label schemaLabel = new Label(this.customer.getDomain().getName());
		
		if(AonStringUtils.isNotBlank(this.customer.getName()))
			toolbar.setTitle("Resumen Contrataci\u00f3n (" + this.customer.getName() + ")");
		
		Label descriptionLabel = new Label(this.customer.getName());
		Label documentLabel = new Label(this.customer.getDocument());
		Label statusLabel = new Label(this.customer.getStatus().getDescription());
		Label billableLabel = new Label(this.customer.isBillable() ? "SI" : "NO");
		Label expirationLabel = new Label(formatDate(this.customer.getCreationDate()));
		Label lastAccessLabel = new Label(formatDate(this.customer.getModificationDate()));
		
		Label nameLabel = new Label(this.customer.getAlias());
		nameLabel.getElement().getStyle().setCursor(Cursor.POINTER);
		nameLabel.addClickHandler(e ->{
			enableRemoteDomain(customer.getDomain().getId(), customer.getAlias());
		});
		
		// Status
		if (this.customer.getStatus().equals(RegistryStatus.INACTIVE)) {
			statusLabel.getElement().getStyle().setFontWeight(FontWeight.BOLD);
			statusLabel.getElement().getStyle().setColor("red");
		} else if (this.customer.getStatus().equals(RegistryStatus.BLOCKED)) {
			statusLabel.getElement().getStyle().setFontWeight(FontWeight.BOLD);
			statusLabel.getElement().getStyle().setColor("orange");
		}
		
		if(!this.customer.isBillable()) {
			billableLabel.getElement().getStyle().setFontWeight(FontWeight.BOLD);
			billableLabel.getElement().getStyle().setColor("orange");
		}
		
		customerTable.setWidget(newRow, 0, domainTypeLabel);
		customerTable.setWidget(newRow, 1, idLabel);
		customerTable.setWidget(newRow, 2, schemaLabel);
		customerTable.setWidget(newRow, 3, descriptionLabel);
		customerTable.setWidget(newRow, 4, documentLabel);
		customerTable.setWidget(newRow, 5, statusLabel);
		customerTable.setWidget(newRow, 6, billableLabel);
		customerTable.setWidget(newRow, 7, expirationLabel);
		customerTable.setWidget(newRow, 8, lastAccessLabel);
		customerTable.setWidget(newRow, 9, nameLabel);
		
		HTMLPanel buttonPanel = new HTMLPanel("");
		buttonPanel.addStyleName(AON.CSS.aonItemFlex());
		buttonPanel.getElement().getStyle().setProperty("min-width", "50px");
		
		AonTableButton syncBtn = new AonTableButton("Sincronizar Contrataci\u00f3n", AON.CSS.aonIconSync());
		if (newRow % 2 == 0) syncBtn.addStyleName(AON.CSS.aonOddTableRow());
		syncBtn.addClickHandler(e -> {
			syncCustomerDomains();
		});
		
		AonTableButton unSyncBtn = new AonTableButton("Desincronizar Contrataci\u00f3n", AON.CSS.aonIconSyncDisabled());
		if (newRow % 2 == 0) unSyncBtn.addStyleName(AON.CSS.aonOddTableRow());
		unSyncBtn.addClickHandler(e -> {
			unSyncCustomerDomains();
		});
		
		buttonPanel.add(syncBtn);
		buttonPanel.add(unSyncBtn);
		
		customerTable.setWidget(newRow, 10, buttonPanel);
		
		if (newRow % 2 == 0) {
			domainTypeLabel.addStyleName(AON.CSS.aonOddTableRow());
			idLabel.addStyleName(AON.CSS.aonOddTableRow());
			schemaLabel.addStyleName(AON.CSS.aonOddTableRow());
			descriptionLabel.addStyleName(AON.CSS.aonOddTableRow());
			documentLabel.addStyleName(AON.CSS.aonOddTableRow());
			statusLabel.addStyleName(AON.CSS.aonOddTableRow());
			billableLabel.addStyleName(AON.CSS.aonOddTableRow());
			expirationLabel.addStyleName(AON.CSS.aonOddTableRow());
			lastAccessLabel.addStyleName(AON.CSS.aonOddTableRow());
			nameLabel.addStyleName(AON.CSS.aonOddTableRow());
			
			customerTable.getCellFormatter().addStyleName(newRow, 0, AON.CSS.aonOddTableRow());
			customerTable.getCellFormatter().addStyleName(newRow, 1, AON.CSS.aonOddTableRow());
			customerTable.getCellFormatter().addStyleName(newRow, 2, AON.CSS.aonOddTableRow());
			customerTable.getCellFormatter().addStyleName(newRow, 3, AON.CSS.aonOddTableRow());
			customerTable.getCellFormatter().addStyleName(newRow, 4, AON.CSS.aonOddTableRow());
			customerTable.getCellFormatter().addStyleName(newRow, 5, AON.CSS.aonOddTableRow());
			customerTable.getCellFormatter().addStyleName(newRow, 6, AON.CSS.aonOddTableRow());
			customerTable.getCellFormatter().addStyleName(newRow, 7, AON.CSS.aonOddTableRow());
			customerTable.getCellFormatter().addStyleName(newRow, 8, AON.CSS.aonOddTableRow());
			customerTable.getCellFormatter().addStyleName(newRow, 9, AON.CSS.aonOddTableRow());
			customerTable.getCellFormatter().addStyleName(newRow, 10, AON.CSS.aonOddTableRow());
		}
		
		customerTable.getCellFormatter().getElement(newRow, 0).getStyle().setTextAlign(TextAlign.CENTER);
		customerTable.getCellFormatter().getElement(newRow, 1).getStyle().setTextAlign(TextAlign.CENTER);
		customerTable.getCellFormatter().getElement(newRow, 4).getStyle().setTextAlign(TextAlign.CENTER);
		customerTable.getCellFormatter().getElement(newRow, 5).getStyle().setTextAlign(TextAlign.CENTER);
		customerTable.getCellFormatter().getElement(newRow, 6).getStyle().setTextAlign(TextAlign.CENTER);
		customerTable.getCellFormatter().getElement(newRow, 7).getStyle().setTextAlign(TextAlign.CENTER);
		customerTable.getCellFormatter().getElement(newRow, 8).getStyle().setTextAlign(TextAlign.CENTER);
		customerTable.getCellFormatter().getElement(newRow, 10).getStyle().setTextAlign(TextAlign.CENTER);
		
		customerTable.getRowFormatter().getElement(newRow).getStyle().setHeight(25.00, Unit.PX);
		
		customerPanel.add(customerTable);
		
	}
	
	private void syncCustomerDomains() {
		AonDialog dialog = new AonDialog("Sincronizaci\u00f3n Dominios Cliente",
				new HTML("Se va a proceder a sincronizar los dominios del cliente <b>" + this.customer.getName() + "</b>.<br>\u00bfEsta seguro que desea proceder con la sincronizaci\u00f3n\u003f"));
		
		dialog.confirm(new AonAcceptDialogCallback() {

			@Override
			public void onCancel() {
				// Nothing to do here
			}

			@Override
			public void onAccept() {
				updateBookingRitems();
			}
		});
	}
	
	private void unSyncCustomerDomains() {
		AonDialog dialog = new AonDialog("Desincronizaci\u00f3n Dominios Cliente",
				new HTML("Se va a proceder a desincronizar los dominios del cliente <b>" + this.customer.getName() + "</b>.<br>\u00bfEsta seguro que desea proceder con la desincronizaci\u00f3n\u003f. Este proceso sera irreversible."));
		
		dialog.confirm(new AonAcceptDialogCallback() {

			@Override
			public void onCancel() {
				// Nothing to do here
			}

			@Override
			public void onAccept() {
				unSyncDomains();
			}
		});
	}
	
	// ---------- Dominios

	private void initializeDomains() {
		ScrollPanel scrollPanel = new ScrollPanel();
		scrollPanel.getElement().getStyle().setProperty("max-height", "130px");
		
		Grid domainTable = new Grid(0, 11);
		domainTable.clear();
		domainTable.setWidth("100%");

		int row = domainTable.insertRow(domainTable.getRowCount());

		Label type = new Label("TIPO");
		Label id = new Label("ID");
		Label eschema = new Label("ESQUEMA");
		Label name = new Label("NOMBRE");
		Label document = new Label("DOCUMENTO");
		Label status = new Label("ESTADO");
		Label billable = new Label("FACT.");
		Label expire = new Label("EXPIRA");
		Label lastAccess = new Label("ULT. ACCESO");
		Label description = new Label("URL");
		Label url = new Label("");
		
		type.addStyleName(AON.CSS.aonHeaderTable());
		id.addStyleName(AON.CSS.aonHeaderTable());
		eschema.addStyleName(AON.CSS.aonHeaderTable());
		name.addStyleName(AON.CSS.aonHeaderTable());
		document.addStyleName(AON.CSS.aonHeaderTable());
		status.addStyleName(AON.CSS.aonHeaderTable());
		billable.addStyleName(AON.CSS.aonHeaderTable());
		expire.addStyleName(AON.CSS.aonHeaderTable());
		lastAccess.addStyleName(AON.CSS.aonHeaderTable());
		description.addStyleName(AON.CSS.aonHeaderTable());
		url.addStyleName(AON.CSS.aonHeaderTable());

		domainTable.setWidget(row, 0, type);
		domainTable.setWidget(row, 1, id);
		domainTable.setWidget(row, 2, eschema);
		domainTable.setWidget(row, 3, name);
		domainTable.setWidget(row, 4, document);
		domainTable.setWidget(row, 5, status);
		domainTable.setWidget(row, 6, billable);
		domainTable.setWidget(row, 7, expire);
		domainTable.setWidget(row, 8, lastAccess);
		domainTable.setWidget(row, 9, description);
		domainTable.setWidget(row, 10, url);
		
		domainTable.getCellFormatter().addStyleName(row, 0, AON.CSS.aonHeaderSticky());
		domainTable.getCellFormatter().addStyleName(row, 1, AON.CSS.aonHeaderSticky());
		domainTable.getCellFormatter().addStyleName(row, 2, AON.CSS.aonHeaderSticky());
		domainTable.getCellFormatter().addStyleName(row, 3, AON.CSS.aonHeaderSticky());
		domainTable.getCellFormatter().addStyleName(row, 4, AON.CSS.aonHeaderSticky());
		domainTable.getCellFormatter().addStyleName(row, 5, AON.CSS.aonHeaderSticky());
		domainTable.getCellFormatter().addStyleName(row, 6, AON.CSS.aonHeaderSticky());
		domainTable.getCellFormatter().addStyleName(row, 7, AON.CSS.aonHeaderSticky());
		domainTable.getCellFormatter().addStyleName(row, 8, AON.CSS.aonHeaderSticky());
		domainTable.getCellFormatter().addStyleName(row, 9, AON.CSS.aonHeaderSticky());
		domainTable.getCellFormatter().addStyleName(row, 10, AON.CSS.aonHeaderSticky());
		
		domainTable.getColumnFormatter().getElement(0).getStyle().setWidth(65, Unit.PX);
		domainTable.getColumnFormatter().getElement(1).getStyle().setWidth(60, Unit.PX);
		domainTable.getColumnFormatter().getElement(2).getStyle().setWidth(190, Unit.PX);

		domainTable.getColumnFormatter().getElement(4).getStyle().setWidth(80, Unit.PX);
		domainTable.getColumnFormatter().getElement(5).getStyle().setWidth(60, Unit.PX);
		domainTable.getColumnFormatter().getElement(6).getStyle().setWidth(50, Unit.PX);
		domainTable.getColumnFormatter().getElement(7).getStyle().setWidth(80, Unit.PX);
		domainTable.getColumnFormatter().getElement(8).getStyle().setWidth(80, Unit.PX);
		domainTable.getColumnFormatter().getElement(9).getStyle().setWidth(215, Unit.PX);
		domainTable.getColumnFormatter().getElement(10).getStyle().setWidth(25, Unit.PX);
		
		scrollPanel.add(domainTable);
		
		// Create empty line for sync
		if(null == customerDomains || customerDomains.isEmpty()) {
			int newRow = domainTable.insertRow(domainTable.getRowCount());
			
			domainTable.setWidget(newRow, 0, new Label());
			domainTable.setWidget(newRow, 1, new Label());
			domainTable.setWidget(newRow, 2, new Label());
			domainTable.setWidget(newRow, 3, new Label());
			domainTable.setWidget(newRow, 4, new Label());
			domainTable.setWidget(newRow, 5, new Label());
			domainTable.setWidget(newRow, 6, new Label());
			domainTable.setWidget(newRow, 7, new Label());
			domainTable.setWidget(newRow, 8, new Label());
			domainTable.setWidget(newRow, 9, new Label());
			
			HTMLPanel buttonPanel = new HTMLPanel("");
			buttonPanel.addStyleName(AON.CSS.aonItemFlex());
			buttonPanel.getElement().getStyle().setProperty("min-width", "50px");
						
			AonTableButton syncDomainBtn = new AonTableButton("Vincular Dominio", AON.CSS.aonIconLink());
			syncDomainBtn.addClickHandler(e -> syncCustomerToDomain());
			
			buttonPanel.add(syncDomainBtn);
			
			domainTable.setWidget(newRow, 10, buttonPanel);
			
			domainTable.getCellFormatter().getElement(newRow, 10).getStyle().setTextAlign(TextAlign.CENTER);
			
			domainTable.getRowFormatter().getElement(newRow).getStyle().setHeight(25.00, Unit.PX);
			
		} else {
			for(DomainCompany domainCompany : customerDomains) {
				int newRow = domainTable.insertRow(domainTable.getRowCount());
				
				Label domainTypeLabel  = new Label(domainCompany.getDomain().getDomainType().getName());
				Label idLabel = new Label(domainCompany.getDomain().getId().toString());
				Label schemaLabel = new Label(domainCompany.getSchema());
				Label descriptionLabel = new Label(domainCompany.getDomain().getDescription());
				Label documentLabel = new Label(domainCompany.getCompany().getDocument());
				Label statusLabel = new Label(domainCompany.getDomain().isActive() ? "Activo" : "Inactivo");
				Label billableLabel = new Label(domainCompany.getDomain().getAonStatus().equals(AonStatus.BILLABLE) ? "SI" : "NO");
				Label expirationLabel = new Label(formatDate(domainCompany.getDomain().getExpirationDate()));
				Label lastAccessLabel = new Label(formatDate(domainCompany.getDomain().getLastAccessDate()));
				Label nameLabel = new Label(domainCompany.getDomain().getName());
				nameLabel.getElement().getStyle().setCursor(Cursor.POINTER);
				nameLabel.addClickHandler(e ->{
					enableRemoteDomain(domainCompany.getDomain().getId(), domainCompany.getDomain().getName());
				});
				
				// Status
				if (!domainCompany.getDomain().isActive()) {
					statusLabel.getElement().getStyle().setFontWeight(FontWeight.BOLD);
					statusLabel.getElement().getStyle().setColor("red");
				}
				
				if (domainCompany.getDomain().getAonStatus().equals(AonStatus.NOT_BILLABLE)) {
					billableLabel.getElement().getStyle().setFontWeight(FontWeight.BOLD);
					billableLabel.getElement().getStyle().setColor("orange");
				}
				
				if(null != domainCompany.getDomain().getExpirationDate()) {
					Date today = new Date();
					if(domainCompany.getDomain().getExpirationDate().after(today)) {
						expirationLabel.getElement().getStyle().setFontWeight(FontWeight.BOLD);
						expirationLabel.getElement().getStyle().setColor("orange");
					} else if(domainCompany.getDomain().getExpirationDate().before(today)) {
						expirationLabel.getElement().getStyle().setFontWeight(FontWeight.BOLD);
						expirationLabel.getElement().getStyle().setColor("red");
					}
				}
				
				domainTable.setWidget(newRow, 0, domainTypeLabel);
				domainTable.setWidget(newRow, 1, idLabel);
				domainTable.setWidget(newRow, 2, schemaLabel);
				domainTable.setWidget(newRow, 3, descriptionLabel);
				domainTable.setWidget(newRow, 4, documentLabel);
				domainTable.setWidget(newRow, 5, statusLabel);
				domainTable.setWidget(newRow, 6, billableLabel);
				domainTable.setWidget(newRow, 7, expirationLabel);
				domainTable.setWidget(newRow, 8, lastAccessLabel);
				domainTable.setWidget(newRow, 9, nameLabel);
				
				HTMLPanel buttonPanel = new HTMLPanel("");
				buttonPanel.addStyleName(AON.CSS.aonItemFlex());
				buttonPanel.getElement().getStyle().setProperty("min-width", "50px");
							
				AonTableButton usersBtn = new AonTableButton("Usuarios Dominio", AON.CSS.aonIconGroup());
				usersBtn.addClickHandler(e -> openUserTooltip(domainCompany.getDomain()));
				
				AonTableButton unsyncDomainBtn = new AonTableButton("Desvincular Dominio", AON.CSS.aonIconLinkOff());
				unsyncDomainBtn.addClickHandler(e -> unSyncDomain(domainCompany));
				
				buttonPanel.add(usersBtn);
				buttonPanel.add(unsyncDomainBtn);
				
				domainTable.setWidget(newRow, 10, buttonPanel);
				
				if (newRow % 2 == 0) {
					domainTypeLabel.addStyleName(AON.CSS.aonOddTableRow());
					idLabel.addStyleName(AON.CSS.aonOddTableRow());
					schemaLabel.addStyleName(AON.CSS.aonOddTableRow());
					descriptionLabel.addStyleName(AON.CSS.aonOddTableRow());
					documentLabel.addStyleName(AON.CSS.aonOddTableRow());
					statusLabel.addStyleName(AON.CSS.aonOddTableRow());
					billableLabel.addStyleName(AON.CSS.aonOddTableRow());
					expirationLabel.addStyleName(AON.CSS.aonOddTableRow());
					lastAccessLabel.addStyleName(AON.CSS.aonOddTableRow());
					nameLabel.addStyleName(AON.CSS.aonOddTableRow());
					usersBtn.addStyleName(AON.CSS.aonOddTableRow());
					
					domainTable.getCellFormatter().addStyleName(newRow, 0, AON.CSS.aonOddTableRow());
					domainTable.getCellFormatter().addStyleName(newRow, 1, AON.CSS.aonOddTableRow());
					domainTable.getCellFormatter().addStyleName(newRow, 2, AON.CSS.aonOddTableRow());
					domainTable.getCellFormatter().addStyleName(newRow, 3, AON.CSS.aonOddTableRow());
					domainTable.getCellFormatter().addStyleName(newRow, 4, AON.CSS.aonOddTableRow());
					domainTable.getCellFormatter().addStyleName(newRow, 5, AON.CSS.aonOddTableRow());
					domainTable.getCellFormatter().addStyleName(newRow, 6, AON.CSS.aonOddTableRow());
					domainTable.getCellFormatter().addStyleName(newRow, 7, AON.CSS.aonOddTableRow());
					domainTable.getCellFormatter().addStyleName(newRow, 8, AON.CSS.aonOddTableRow());
					domainTable.getCellFormatter().addStyleName(newRow, 9, AON.CSS.aonOddTableRow());
					domainTable.getCellFormatter().addStyleName(newRow, 10, AON.CSS.aonOddTableRow());
				}
				
				domainTable.getCellFormatter().getElement(newRow, 0).getStyle().setTextAlign(TextAlign.CENTER);
				domainTable.getCellFormatter().getElement(newRow, 1).getStyle().setTextAlign(TextAlign.CENTER);
				domainTable.getCellFormatter().getElement(newRow, 4).getStyle().setTextAlign(TextAlign.CENTER);
				domainTable.getCellFormatter().getElement(newRow, 5).getStyle().setTextAlign(TextAlign.CENTER);
				domainTable.getCellFormatter().getElement(newRow, 6).getStyle().setTextAlign(TextAlign.CENTER);
				domainTable.getCellFormatter().getElement(newRow, 7).getStyle().setTextAlign(TextAlign.CENTER);
				domainTable.getCellFormatter().getElement(newRow, 8).getStyle().setTextAlign(TextAlign.CENTER);
				domainTable.getCellFormatter().getElement(newRow, 10).getStyle().setTextAlign(TextAlign.CENTER);
				
				domainTable.getRowFormatter().getElement(newRow).getStyle().setHeight(25.00, Unit.PX);
			}
		}
		
		domainsPanel.add(scrollPanel);
		
	}

	// ---------- Dominios (methods)
	
	private void syncCustomerToDomain() {
		AonMessagePanel.showLoading(messagePanel, "Obteniendo dominios ...");
		
		String host = isLocalDev ? "localhost:8080" : "aon.solutions";
		String endPoint = "/ms/api/domain/";
		
		this.bookingApi.getDomainCompanies(host, endPoint, new AsyncCallback<List<DomainCompany>>() {
			
			@Override
			public void onSuccess(List<DomainCompany> domainCompanies) {
				AonMessagePanel.hideMessage(messagePanel);
				showSelectSyncDomainsDialog(domainCompanies);
			}
			
			@Override
			public void onFailure(Throwable exception) {
				AonMessagePanel.showError(messagePanel, exception.getMessage());
			}
		});
	}
	
	private void showSelectSyncDomainsDialog(List<DomainCompany> companies) {
		new AonDomainSyncSelectionDialog("Viculaci\u00f3n Dominios", companies, customer) {
			
			@Override
			protected void onAccept(DomainCompany domainCompany) {
				// Unsync Domain aonCustomer
            	AonMessagePanel.showLoading(messagePanel, "Vinculando dominio del cliente " + customer.getName() + " ...");
            	
            	String host = isLocalDev ? "localhost:8080" : "aon.solutions";
        		String endPoint = "/ms/api/domain/";
        		
        		JSONObject body = new JSONObject();
        		body.put("customer", new JSONNumber(customer.getId()));
        		
        		JSONArray domains = new JSONArray();
        		domains.set(0, DomainCompanyJSON.domainCompanyToJSON(domainCompany));
        		body.put("domains", domains);
        		
        		bookingApi.syncDomainCustomer(host, endPoint, body, new AsyncCallback<Void>() {
        			
        			@Override
        			public void onSuccess(Void success) {
        				syncCustomerBooking();
        			}
        			
        			@Override
        			public void onFailure(Throwable exception) {
        				AonMessagePanel.showError(messagePanel, exception.getMessage());
        			}
        		});
        		
			}
		};
	}
	
	private void syncCustomerBooking() {
		AonMessagePanel.showLoading(messagePanel, "Sincronizando contrataci\u00f3n para el cliente " + customer.getName() + " ...");
		
		String host = Window.Location.getHost();
		String endPoint = "/ms/api/domain/booking-customer/";
		
		JSONObject body = new JSONObject();
		body.put("customer", new JSONNumber(customer.getId()));
		
		HashMap<String, String> headers = new HashMap<>();
		headers.put("domain_name", options.getDomainName());
		headers.put("domain_login", options.getUser());
		headers.put("domain_id", String.valueOf(options.getDomain()));
		
		bookingApi.syncCustomerBooking(host, endPoint, headers, body, new AsyncCallback<Response>() {
			
			@Override
			public void onSuccess(Response response) {
				AonMessagePanel.showSuccess(messagePanel, "La sincronizaci\u00f3n del cliente " + customer.getName() + " se ha realizado correctamente");
        		
            	Timer timer = new Timer() {
           		     @Override
           		     public void run() {
           		    	checkCustomerDomains();
           		     }
           		};
           		timer.schedule(2500);
			}
			
			@Override
			public void onFailure(Throwable exception) {
				AonMessagePanel.showError(messagePanel, exception.getMessage());
			}
		});
	}
	
	private void unSyncDomain(DomainCompany domainCompany) {
		AonMessagePanel.showLoading(messagePanel, "Desincronizando contrataci\u00f3n para el cliente " + customer.getName() + " ...");
		
		String host = Window.Location.getHost();
		String endPoint = "/ms/api/domain/booking/";
		
		JSONObject body = new JSONObject();
		body.put("customer", new JSONNumber(customer.getId()));
		
		bookingApi.unsyncCustomerBooking(host, endPoint, body, new AsyncCallback<Void>() {
			
			@Override
			public void onSuccess(Void success) {
				AonMessagePanel.showLoading(messagePanel, "Desvinculando dominio del cliente " + customer.getName() + " ...");
				
				String host = isLocalDev ? "localhost:8080" : "aon.solutions";
        		String endPoint = "/ms/api/domain/";
        		
        		JSONObject body = new JSONObject();
        		
        		JSONArray domains = new JSONArray();
        		domains.set(0, DomainCompanyJSON.domainCompanyToJSON(domainCompany));
        		body.put("domains", domains);
        		
        		bookingApi.unsyncDomainCustomer(host, endPoint, body, new AsyncCallback<Void>() {
        			
        			@Override
        			public void onSuccess(Void success) {
        				AonMessagePanel.showSuccess(messagePanel, "Dominio desvinculado del cliente correctamente");
		            	
		            	Timer timer = new Timer() {
   		           		     @Override
   		           		     public void run() {
   		           		    	// Remove unsync domain from customerDomains
   		           		    	customerDomains = customerDomains.stream().filter(domainCompanyIt -> !domainCompanyIt.getDomain().getId().equals(domainCompany.getDomain().getId())).collect(Collectors.toList());
   		           		    	setBookingCustomer(customer, customerDomains);
   		           		     }
   		           		};	
   		           		timer.schedule(2500);
        			}
        			
        			@Override
        			public void onFailure(Throwable exception) {
        				AonMessagePanel.showError(messagePanel, exception.getMessage());
        			}
        		});
			}
			
			@Override
			public void onFailure(Throwable exception) {
				AonMessagePanel.showError(messagePanel, exception.getMessage());
			}
		});
	}

	private String formatDate(Date date) {
		if(null == date) return "";
		return formatDate.format(date);
	}

	private void getBookingResume() {
		AonMessagePanel.showLoading(messagePanel, "Obteniendo dominios del cliente ...");
		
		Integer customerId = getCustomer();
		
		String host = isLocalDev ? "localhost:8080" : "aon.solutions";
		String endPoint = "/ms/api/domain/" + customerId.toString();
		
		this.bookingApi.getDomainCompanies(host, endPoint, new AsyncCallback<List<DomainCompany>>() {
			
			@Override
			public void onSuccess(List<DomainCompany> domainCompanies) {
				AonMessagePanel.hideMessage(messagePanel);
                
				if(domainCompanies != null && !domainCompanies.isEmpty()) {
	                
                	AonMessagePanel.showLoading(messagePanel, "Obteniendo contrataci\u00f3n de " + domainCompanies.get(0).getDomain().getName());
                	
                	String host = isLocalDev ? "localhost:8080" : "aon.solutions";
            		String endPoint =  "/ms/api/booking/";
            		
            		HashMap<String, String> header = new HashMap<>();
            		header.put("domain_name", domainCompanies.get(0).getDomain().getName());
            		header.put("domain_id", domainCompanies.get(0).getDomain().getId().toString());
            		
            		bookingApi.getBooking(host, endPoint, header, new AsyncCallback<Booking>() {
            			
            			@Override
            			public void onSuccess(Booking booking) {
            				AonMessagePanel.hideMessage(messagePanel);
            				domainBooking = booking;
            				createDomainBookingResume();
                        	createDomainChildsBookingResume();
            			}
            			
            			@Override
            			public void onFailure(Throwable exception) {
            				AonMessagePanel.showError(messagePanel, exception.getMessage());
            			}
            		});
                }
			}
			
			@Override
			public void onFailure(Throwable exception) {
				AonMessagePanel.showError(messagePanel, exception.getMessage());
			}
		});
		
	}

	private void createDomainBookingResume() {
		domainPanel.clear();
		
		Grid domainTable = new Grid(0, 10);
		domainTable.clear();
		domainTable.setWidth("100%");

		int row = domainTable.insertRow(domainTable.getRowCount());

		Label name = new Label("DOMINIO");
		Label booking = new Label("EXTENSIONES CONTRATADAS");
		Label enterprises = new Label("EMPRESAS");
		Label bookingNum = new Label("N\u00b0 EXTEN.");
		Label status = new Label("ESTADO");
		Label expiration = new Label("F. EXPIRACI\u00f3N");
		Label users = new Label("USR.");
		Label portalUsers = new Label("USR. PORTAL");
		Label type = new Label("TIPO");
		Label action = new Label("");
		
		name.addStyleName(AON.CSS.aonHeaderTable());
		booking.addStyleName(AON.CSS.aonHeaderTable());
		enterprises.addStyleName(AON.CSS.aonHeaderTable());
		bookingNum.addStyleName(AON.CSS.aonHeaderTable());
		status.addStyleName(AON.CSS.aonHeaderTable());
		expiration.addStyleName(AON.CSS.aonHeaderTable());
		users.addStyleName(AON.CSS.aonHeaderTable());
		portalUsers.addStyleName(AON.CSS.aonHeaderTable());
		type.addStyleName(AON.CSS.aonHeaderTable());
		action.addStyleName(AON.CSS.aonHeaderTable());

		domainTable.setWidget(row, 0, name);
		domainTable.setWidget(row, 1, booking);
		domainTable.setWidget(row, 2, enterprises);
		domainTable.setWidget(row, 3, bookingNum);
		domainTable.setWidget(row, 4, status);
		domainTable.setWidget(row, 5, expiration);
		domainTable.setWidget(row, 6, users);
		domainTable.setWidget(row, 7, portalUsers);
		domainTable.setWidget(row, 8, type);
		domainTable.setWidget(row, 9, action);
		
		domainTable.getCellFormatter().addStyleName(row, 0, AON.CSS.aonHeaderSticky());
		domainTable.getCellFormatter().addStyleName(row, 1, AON.CSS.aonHeaderSticky());
		domainTable.getCellFormatter().addStyleName(row, 2, AON.CSS.aonHeaderSticky());
		domainTable.getCellFormatter().addStyleName(row, 3, AON.CSS.aonHeaderSticky());
		domainTable.getCellFormatter().addStyleName(row, 4, AON.CSS.aonHeaderSticky());
		domainTable.getCellFormatter().addStyleName(row, 5, AON.CSS.aonHeaderSticky());
		domainTable.getCellFormatter().addStyleName(row, 6, AON.CSS.aonHeaderSticky());
		domainTable.getCellFormatter().addStyleName(row, 7, AON.CSS.aonHeaderSticky());
		domainTable.getCellFormatter().addStyleName(row, 8, AON.CSS.aonHeaderSticky());
		domainTable.getCellFormatter().addStyleName(row, 9, AON.CSS.aonHeaderSticky());
		
		domainTable.getColumnFormatter().getElement(0).getStyle().setWidth(300, Unit.PX);
		domainTable.getColumnFormatter().getElement(2).getStyle().setWidth(80, Unit.PX);
		domainTable.getColumnFormatter().getElement(3).getStyle().setWidth(70, Unit.PX);
		domainTable.getColumnFormatter().getElement(4).getStyle().setWidth(70, Unit.PX);
		domainTable.getColumnFormatter().getElement(5).getStyle().setWidth(100, Unit.PX);
		domainTable.getColumnFormatter().getElement(6).getStyle().setWidth(50, Unit.PX);
		domainTable.getColumnFormatter().getElement(7).getStyle().setWidth(80, Unit.PX);
		domainTable.getColumnFormatter().getElement(8).getStyle().setWidth(80, Unit.PX);
		domainTable.getColumnFormatter().getElement(9).getStyle().setWidth(25, Unit.PX);
		
		int newRow = domainTable.insertRow(domainTable.getRowCount());
		
		Label domainNameLabel  = new Label(domainBooking.getDomain().getDescription());
		
		List<String> apps = domainBooking.getApps().stream().map(aonApp -> aonApp.getDescription()).collect(Collectors.toList());
		apps.sort((o1, o2) -> o1.compareTo(o2));
		
		Label bookingLabel = new Label(String.join(", ", apps));
		Label bookingNumLabel = new Label(domainBooking.getApps().size() + "");
		
		List<Domain> childs = domainBooking.getResume().getChilds();
		List<Domain> activeChilds = domainBooking.getResume().getChilds().stream().filter(domain -> domain.isActive()).collect(Collectors.toList());
		Label enterprisesLabel = new Label(activeChilds.size() + " / " + childs.size());
		enterprisesLabel.setTitle(activeChilds.size() + " empresas activas / " + childs.size() + " empresas");
		
		String statusMessage = domainBooking.getDomain().getExpirationDate() != null && domainBooking.getDomain().getExpirationDate().before(new Date()) ? "Expirado" : (domainBooking.getDomain().isActive() ? "Activo" : "Inactivo");
		Label statusLabel = new Label(statusMessage);
		statusLabel.setTitle(AonStringUtils.equalsIgnoreCase(statusMessage, "Expirado") ? ("F. expiraci\u00f3n : " + formatDate(domainBooking.getDomain().getExpirationDate())) : "");
		
		Label expirationLabel = new Label(formatDate(domainBooking.getDomain().getExpirationDate()));
		
		Label usersLabel = new Label(domainBooking.getNumberOfUsers() + " / " + domainBooking.getDomain().getMaxDefinedUsers());
		
		Integer portalUsersCount = null == domainBooking.getDomain().getUsers() ? 0 : (int) domainBooking.getDomain().getUsers().stream().filter(user -> user.isActive() && user.isPortal()).count();			
		Label portalUsersLabel = new Label(portalUsersCount + "");
		
		Label typeLabel = new Label(domainBooking.getType().getName());
		
		HTMLPanel buttonPanel = new HTMLPanel("");
		buttonPanel.addStyleName(AON.CSS.aonItemFlex());
		buttonPanel.getElement().getStyle().setProperty("min-width", "25px");
		
		AonTableButton bookingInfoBtn = new AonTableButton("Ver contrataciones", AON.CSS.aonIconInfo());
		bookingInfoBtn.addClickHandler(e -> {
			new CustomerBookingDialog(options, getCustomer());
		});
		buttonPanel.add(bookingInfoBtn);
				
		domainTable.setWidget(newRow, 0, domainNameLabel);
		domainTable.setWidget(newRow, 1, bookingLabel);
		domainTable.setWidget(newRow, 2, enterprisesLabel);
		domainTable.setWidget(newRow, 3, bookingNumLabel);
		domainTable.setWidget(newRow, 4, statusLabel);
		domainTable.setWidget(newRow, 5, expirationLabel);
		domainTable.setWidget(newRow, 6, usersLabel);
		domainTable.setWidget(newRow, 7, portalUsersLabel);
		domainTable.setWidget(newRow, 8, typeLabel);
		domainTable.setWidget(newRow, 9, buttonPanel);
		
		if (newRow % 2 == 0) {
			domainNameLabel.addStyleName(AON.CSS.aonOddTableRow());
			bookingLabel.addStyleName(AON.CSS.aonOddTableRow());
			bookingNumLabel.addStyleName(AON.CSS.aonOddTableRow());
			statusLabel.addStyleName(AON.CSS.aonOddTableRow());
			expirationLabel.addStyleName(AON.CSS.aonOddTableRow());
			enterprisesLabel.addStyleName(AON.CSS.aonOddTableRow());
			usersLabel.addStyleName(AON.CSS.aonOddTableRow());
			portalUsersLabel.addStyleName(AON.CSS.aonOddTableRow());
			typeLabel.addStyleName(AON.CSS.aonOddTableRow());
			
			buttonPanel.addStyleName(AON.CSS.aonOddTableRow());
			bookingInfoBtn.addStyleName(AON.CSS.aonOddTableRow());
			
			domainTable.getCellFormatter().addStyleName(newRow, 0, AON.CSS.aonOddTableRow());
			domainTable.getCellFormatter().addStyleName(newRow, 1, AON.CSS.aonOddTableRow());
			domainTable.getCellFormatter().addStyleName(newRow, 2, AON.CSS.aonOddTableRow());
			domainTable.getCellFormatter().addStyleName(newRow, 3, AON.CSS.aonOddTableRow());
			domainTable.getCellFormatter().addStyleName(newRow, 4, AON.CSS.aonOddTableRow());
			domainTable.getCellFormatter().addStyleName(newRow, 5, AON.CSS.aonOddTableRow());
			domainTable.getCellFormatter().addStyleName(newRow, 6, AON.CSS.aonOddTableRow());
			domainTable.getCellFormatter().addStyleName(newRow, 7, AON.CSS.aonOddTableRow());
			domainTable.getCellFormatter().addStyleName(newRow, 8, AON.CSS.aonOddTableRow());
			domainTable.getCellFormatter().addStyleName(newRow, 9, AON.CSS.aonOddTableRow());
		}
		
		domainTable.getCellFormatter().getElement(newRow, 2).getStyle().setTextAlign(TextAlign.CENTER);
		domainTable.getCellFormatter().getElement(newRow, 3).getStyle().setTextAlign(TextAlign.CENTER);
		domainTable.getCellFormatter().getElement(newRow, 4).getStyle().setTextAlign(TextAlign.CENTER);
		domainTable.getCellFormatter().getElement(newRow, 5).getStyle().setTextAlign(TextAlign.CENTER);
		domainTable.getCellFormatter().getElement(newRow, 6).getStyle().setTextAlign(TextAlign.CENTER);
		domainTable.getCellFormatter().getElement(newRow, 7).getStyle().setTextAlign(TextAlign.CENTER);
		
		domainTable.getRowFormatter().getElement(newRow).getStyle().setHeight(25.00, Unit.PX);
		
		domainPanel.add(domainTable);
	}
	
	private void createDomainChildsBookingResume() {
		boolean allEnterprises = enterprisesView.getSelectedIndex() == 1;
		domainChildsPanel.clear();
		
		Grid domainChildsTable = new Grid(0, 9);
		domainChildsTable.clear();
		domainChildsTable.setWidth("100%");

		int row = domainChildsTable.insertRow(domainChildsTable.getRowCount());

		Label name = new Label("EMPRESA");
		Label booking = new Label("EXTENSIONES CONTRATADAS");
		Label bookingNum = new Label("N\u00b0 EXTEN.");
		Label status = new Label("ESTADO");
		Label expiration = new Label("F. EXPIRACI\u00f3N");
		Label users = new Label("USR.");
		Label portalUsers = new Label("USR. PORTAL");
		Label type = new Label("TIPO");
		Label action = new Label("");
		
		name.addStyleName(AON.CSS.aonHeaderTable());
		booking.addStyleName(AON.CSS.aonHeaderTable());
		bookingNum.addStyleName(AON.CSS.aonHeaderTable());
		status.addStyleName(AON.CSS.aonHeaderTable());
		expiration.addStyleName(AON.CSS.aonHeaderTable());
		users.addStyleName(AON.CSS.aonHeaderTable());
		portalUsers.addStyleName(AON.CSS.aonHeaderTable());
		type.addStyleName(AON.CSS.aonHeaderTable());
		action.addStyleName(AON.CSS.aonHeaderTable());

		domainChildsTable.setWidget(row, 0, name);
		domainChildsTable.setWidget(row, 1, booking);
		domainChildsTable.setWidget(row, 2, bookingNum);
		domainChildsTable.setWidget(row, 3, status);
		domainChildsTable.setWidget(row, 4, expiration);
		domainChildsTable.setWidget(row, 5, users);
		domainChildsTable.setWidget(row, 6, portalUsers);
		domainChildsTable.setWidget(row, 7, type);
		domainChildsTable.setWidget(row, 8, action);
		
		domainChildsTable.getCellFormatter().addStyleName(row, 0, AON.CSS.aonHeaderSticky());
		domainChildsTable.getCellFormatter().addStyleName(row, 1, AON.CSS.aonHeaderSticky());
		domainChildsTable.getCellFormatter().addStyleName(row, 2, AON.CSS.aonHeaderSticky());
		domainChildsTable.getCellFormatter().addStyleName(row, 3, AON.CSS.aonHeaderSticky());
		domainChildsTable.getCellFormatter().addStyleName(row, 4, AON.CSS.aonHeaderSticky());
		domainChildsTable.getCellFormatter().addStyleName(row, 5, AON.CSS.aonHeaderSticky());
		domainChildsTable.getCellFormatter().addStyleName(row, 6, AON.CSS.aonHeaderSticky());
		domainChildsTable.getCellFormatter().addStyleName(row, 7, AON.CSS.aonHeaderSticky());
		domainChildsTable.getCellFormatter().addStyleName(row, 8, AON.CSS.aonHeaderSticky());
		
		domainChildsTable.getColumnFormatter().getElement(0).getStyle().setWidth(300, Unit.PX);
		domainChildsTable.getColumnFormatter().getElement(2).getStyle().setWidth(70, Unit.PX);
		domainChildsTable.getColumnFormatter().getElement(3).getStyle().setWidth(70, Unit.PX);
		domainChildsTable.getColumnFormatter().getElement(4).getStyle().setWidth(100, Unit.PX);
		domainChildsTable.getColumnFormatter().getElement(5).getStyle().setWidth(50, Unit.PX);
		domainChildsTable.getColumnFormatter().getElement(6).getStyle().setWidth(80, Unit.PX);
		domainChildsTable.getColumnFormatter().getElement(7).getStyle().setWidth(80, Unit.PX);
		domainChildsTable.getColumnFormatter().getElement(8).getStyle().setWidth(25, Unit.PX);
		
		List<String> parentApps = domainBooking.getApps().stream().map(aonApp -> aonApp.getDescription()).collect(Collectors.toList());
		List<Domain> childsDomain = domainBooking.getResume().getChilds();
		childsDomain.sort((o1, o2) -> o1.getDescription().compareTo(o2.getDescription()));
		
		for(Domain domainChild : childsDomain) {
			
			// Get child and parent diff apps
			List<String> childApps = domainChild.getApps().stream().map(domainApp -> domainApp.getApp().getDescription()).collect(Collectors.toList());
			List<String> parentAppsDiff = childApps.stream().filter(app -> parentApps.contains(app)).collect(Collectors.toList());
			parentAppsDiff.sort((o1, o2) -> o1.compareTo(o2));
			
			List<String> childAppsDiff = childApps.stream().filter(app -> !parentApps.contains(app)).collect(Collectors.toList());
			childAppsDiff.sort((o1, o2) -> o1.compareTo(o2));
			
			Integer portalUsersCount = null == domainChild.getUsers() ? 0 : (int) domainChild.getUsers().stream().filter(user -> user.isActive() && user.isPortal()).count();			
			List<User> activeUsers = domainChild.getUsers().stream().filter(user -> user.isActive()).collect(Collectors.toList());
			Integer activeUsersDiff = null == activeUsers ? 0 : (activeUsers.size() - portalUsersCount);

			// If empresasfacturables has no child app skip
			if(!allEnterprises && childAppsDiff.size() == 0 && 0 == domainChild.getMaxDefinedUsers()) continue;
			
			// Grid widgets columns
			Label domainNameLabel  = new Label(domainChild.getDescription());
			
			Label bookingLabel = new Label(
					(childAppsDiff.size() == 0 ? "Sin contrataciones" : String.join(", ", childAppsDiff)) + 
					" / " + 
					(parentAppsDiff.size() == 0 ? "Sin extensiones heredadas" : String.join(", ", parentAppsDiff)));
			
			Label bookingNumLabel = new Label(childAppsDiff.size() + " / " + parentAppsDiff.size() + " (" +  childApps.size() + ")");
			bookingNumLabel.setTitle(childAppsDiff.size() + " extensiones facturables / " + parentAppsDiff.size() + " extensiones heredadas del padre");
			
			String statusMessage = domainChild.getExpirationDate() != null && domainChild.getExpirationDate().before(new Date()) ? "Expirado" : (domainChild.isActive() ? "Activo" : "Inactivo");
			Label statusLabel = new Label(statusMessage);
			statusLabel.setTitle(AonStringUtils.equalsIgnoreCase(statusMessage, "Expirado") ? ("F. expiraci\u00f3n : " + formatDate(domainChild.getExpirationDate())) : "");
			
			Label expirationLabel = new Label(formatDate(domainChild.getExpirationDate()));
			
			Label usersLabel = new Label(activeUsersDiff + " / " + domainChild.getMaxDefinedUsers());
			
			Label portalUsersLabel = new Label(portalUsersCount + "");
			
			Label typeLabel = new Label(domainChild.getDomainType().getName());
			
			// Check user diffs
			if(activeUsersDiff < domainChild.getMaxDefinedUsers()) {
				usersLabel.getElement().getStyle().setColor("orange");
				usersLabel.setTitle("Existe mas usuarios contratados que activos");
			} else if(activeUsersDiff > domainChild.getMaxDefinedUsers()) {
				usersLabel.getElement().getStyle().setColor("red");
				usersLabel.setTitle("Existe mas usuarios activos que contratados");
			}
			
			Label actionLabel = new Label("");
			
			// Add row
			int newRow = domainChildsTable.insertRow(domainChildsTable.getRowCount());
			
			domainChildsTable.setWidget(newRow, 0, domainNameLabel);
			domainChildsTable.setWidget(newRow, 1, bookingLabel);
			domainChildsTable.setWidget(newRow, 2, bookingNumLabel);
			domainChildsTable.setWidget(newRow, 3, statusLabel);
			domainChildsTable.setWidget(newRow, 4, expirationLabel);
			domainChildsTable.setWidget(newRow, 5, usersLabel);
			domainChildsTable.setWidget(newRow, 6, portalUsersLabel);
			domainChildsTable.setWidget(newRow, 7, typeLabel);
			domainChildsTable.setWidget(newRow, 8, actionLabel);
			
			List<Label> rowLabels = new ArrayList<>();
			rowLabels.add(domainNameLabel);
			rowLabels.add(bookingLabel);
			rowLabels.add(bookingNumLabel);
			rowLabels.add(statusLabel);
			rowLabels.add(expirationLabel);
			rowLabels.add(usersLabel);
			rowLabels.add(portalUsersLabel);
			rowLabels.add(typeLabel);
			rowLabels.add(actionLabel);
			
			for(Label label : rowLabels) {
				label.addMouseOverHandler(e -> addHighlightRow(domainChildsTable, newRow));
				label.addMouseOutHandler(e -> removeHighlightRow(domainChildsTable, newRow));
			}
			
			if (newRow % 2 == 0) {
				domainNameLabel.addStyleName(AON.CSS.aonOddTableRow());
				bookingLabel.addStyleName(AON.CSS.aonOddTableRow());
				bookingNumLabel.addStyleName(AON.CSS.aonOddTableRow());
				statusLabel.addStyleName(AON.CSS.aonOddTableRow());
				expirationLabel.addStyleName(AON.CSS.aonOddTableRow());
				usersLabel.addStyleName(AON.CSS.aonOddTableRow());
				portalUsersLabel.addStyleName(AON.CSS.aonOddTableRow());
				typeLabel.addStyleName(AON.CSS.aonOddTableRow());
				actionLabel.addStyleName(AON.CSS.aonOddTableRow());
				
				domainChildsTable.getCellFormatter().addStyleName(newRow, 0, AON.CSS.aonOddTableRow());
				domainChildsTable.getCellFormatter().addStyleName(newRow, 1, AON.CSS.aonOddTableRow());
				domainChildsTable.getCellFormatter().addStyleName(newRow, 2, AON.CSS.aonOddTableRow());
				domainChildsTable.getCellFormatter().addStyleName(newRow, 3, AON.CSS.aonOddTableRow());
				domainChildsTable.getCellFormatter().addStyleName(newRow, 4, AON.CSS.aonOddTableRow());
				domainChildsTable.getCellFormatter().addStyleName(newRow, 5, AON.CSS.aonOddTableRow());
				domainChildsTable.getCellFormatter().addStyleName(newRow, 6, AON.CSS.aonOddTableRow());
				domainChildsTable.getCellFormatter().addStyleName(newRow, 7, AON.CSS.aonOddTableRow());
				domainChildsTable.getCellFormatter().addStyleName(newRow, 8, AON.CSS.aonOddTableRow());
			}
			
			domainChildsTable.getCellFormatter().getElement(newRow, 2).getStyle().setTextAlign(TextAlign.CENTER);
			domainChildsTable.getCellFormatter().getElement(newRow, 3).getStyle().setTextAlign(TextAlign.CENTER);
			domainChildsTable.getCellFormatter().getElement(newRow, 4).getStyle().setTextAlign(TextAlign.CENTER);
			domainChildsTable.getCellFormatter().getElement(newRow, 5).getStyle().setTextAlign(TextAlign.CENTER);
			domainChildsTable.getCellFormatter().getElement(newRow, 6).getStyle().setTextAlign(TextAlign.CENTER);
			
			domainChildsTable.getRowFormatter().getElement(newRow).getStyle().setHeight(25.00, Unit.PX);
		}
		
		domainChildsPanel.add(domainChildsTable);
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
	
	// -------- Servlets Methods
	
	private void enableRemoteDomain(Integer domainId, String url) {
		AonDialog dialog = new AonDialog("Acceso remoto",
				new HTML("Se va a acceder al dominio <b>" + url + "</b>.<br>\u00bfQuiere activar el acceso remoto para este dominio\u003f"));
		
		dialog.confirm(new AonAcceptDialogCallback() {

			@Override
			public void onCancel() {
				Window.open("https://" + url, "_blank", "");
			}

			@Override
			public void onAccept() {
				String host = isLocalDev ? "localhost:8080" : "aon.solutions";
				String endPoint = "/ms/api/domain/remote/";
				
				HashMap<String, String> header = new HashMap<>();
				header.put("domain_name", url);
				header.put("domain_id", domainId.toString());
				
				bookingApi.enableDomainRemote(host, endPoint, header, new AsyncCallback<Void>() {
					
					@Override
					public void onSuccess(Void success) {
						Window.open("https://" + url, "_blank", "");
					}
					
					@Override
					public void onFailure(Throwable exception) {
						AonMessagePanel.showError(messagePanel, exception.getMessage());
					}
				});	
			}
		});
	}
	
	private void updateBookingRitems() {
		AonMessagePanel.showLoading(messagePanel, "Sincronizando contrataci\u00f3n para el cliente " + customer.getName() + " ...");
		
		String host = Window.Location.getHost();
		String endPoint = "/ms/api/domain/booking-customer/";
		
		JSONObject body = new JSONObject();
		body.put("customer", new JSONNumber(customer.getId()));
		
		HashMap<String, String> headers = new HashMap<>();
		headers.put("domain_name", options.getDomainName());
		headers.put("domain_login", options.getUser());
		headers.put("domain_id", String.valueOf(options.getDomain()));
		
		bookingApi.syncCustomerBooking(host, endPoint, headers, body, new AsyncCallback<Response>() {
			
			@Override
			public void onSuccess(Response response) {
				AonMessagePanel.showSuccess(messagePanel, "La sincronizaci\u00f3n del cliente " + customer.getName() + " se ha realizado correctamente");
        		
            	Timer timer = new Timer() {
           		     @Override
           		     public void run() {
           		    	setBookingCustomer(customer, customerDomains);
           		     }
           		};
           		timer.schedule(2500);
           		
           		Timer logTimer = new Timer() {
           		     @Override
           		     public void run() {
           		    	 showSyncLogs(response.getText());
           		     }
           		};
           		logTimer.schedule(4500);
			}
			
			@Override
			public void onFailure(Throwable exception) {
				AonMessagePanel.showError(messagePanel, exception.getMessage());
			}
		});
	}
	
	private void unSyncDomains() {
		AonMessagePanel.showLoading(messagePanel, "Desincronizando contrataci\u00f3n para el cliente " + customer.getName() + " ...");
		
		String host = Window.Location.getHost();
		String endPoint = "/ms/api/domain/booking/";
		
		JSONObject body = new JSONObject();
		body.put("customer", new JSONNumber(customer.getId()));
		
		bookingApi.unsyncCustomerBooking(host, endPoint, body, new AsyncCallback<Void>() {
			
			@Override
			public void onSuccess(Void success) {
				AonMessagePanel.showSuccess(messagePanel, "La desincronizaci\u00f3n del cliente " + customer.getName() + " se ha realizado correctamente");
        		
            	Timer timer = new Timer() {
           		     @Override
           		     public void run() {
           		    	setBookingCustomer(customer, customerDomains);;
           		     }
           		};
           		timer.schedule(2500);
			}
			
			@Override
			public void onFailure(Throwable exception) {
				AonMessagePanel.showError(messagePanel, exception.getMessage());
			}
		});
	}
	
	private void showSyncLogs(String response) {
		if(AonStringUtils.isNotBlank(response) && AonStringUtils.containsIgnoreCase(response, "logs"))
    		AonMessagePanel.showWarning(messagePanel, parseErrors(response));
	}

	private HTMLPanel parseErrors(String jsonErrors) {
		String htmlBody = "<ul>";
		
		JSONObject jsonObj = JSONParser.parseStrict(jsonErrors).isObject();
		JSONArray arr = jsonObj.get("logs").isArray();
		
		for(Integer i = 0; i < arr.size(); i++)
			htmlBody += "<li>" + arr.get(i).isObject().get("log").isString().stringValue() + "</li>";

		htmlBody += "</ul>";
		
		HTMLPanel html = new HTMLPanel(htmlBody);
		html.getElement().getStyle().setPaddingLeft(2, Unit.EM);
		
		return html;
	}
	
	private void openUserTooltip(Domain domain) {
		AonMessagePanel.showLoading(messagePanel, "Obteniendo usuarios para el dominio " + domain.getDescription() + " ...");
		
		String host = isLocalDev ? "localhost:8080" : "aon.solutions";
		String endPoint = "/ms/api/user/";
		
		HashMap<String, String> header = new HashMap<>();
		header.put("domain_name", domain.getName());
		header.put("domain_id", domain.getId().toString());
		
		bookingApi.getDomainUsers(host, endPoint, header, new AsyncCallback<List<User>>() {
			
			@Override
			public void onSuccess(List<User> users) {
				HTMLPanel container = new HTMLPanel("");
        		container.addStyleName(AON.CSS.aonFlexColumn());
        		container.getElement().getStyle().setProperty("margin", "0 1rem");
        		
        		users.forEach(user -> {
        			HTMLPanel row = new HTMLPanel("");
        			row.addStyleName(AON.CSS.aonItemFlex());
        			
        			Label name = new Label("(" + user.getLogin() + ") " + user.getName());
        			
        			AonTableButton copy = new AonTableButton("Copiar login", AON.CSS.aonIconCopy());
        			copy.addClickHandler(e -> copyToClipboard(user.getLogin()));
        			
        			row.add(copy);
        			row.add(name);
        			
        			container.add(row);
        		});
            	
        		AonDialog dialog = new AonDialog("Usuarios " + domain.getDescription(), container);
    			dialog.info();
    			
				AonMessagePanel.hideMessage(messagePanel);
			}
			
			@Override
			public void onFailure(Throwable exception) {
				AonMessagePanel.showError(messagePanel, exception.getMessage());
			}
		});
	}
	
	private final native void copyToClipboard(String text) /*-{
		var textField = $doc.createElement('textarea');
	    textField.value = text;
	    $doc.body.appendChild(textField);
	    textField.select();
	    $doc.execCommand('copy');
	    $doc.body.removeChild(textField);
	}-*/;

	// -------------------------------- TOOLBAR
	
	private void createToolbar() {
		toolbar = new AonToolbar("Resumen Contrataci\u00f3n");
		
		backBtn = new AonToolbarButton("Volver inicio", AON.CSS.aonIconBack());
		backBtn.addClickHandler(e -> back());
		
		toolbar.add(backBtn);
		
		Label showLabel = new Label("Ver:");
		showLabel.getElement().getStyle().setFontWeight(FontWeight.BOLD);
		toolbar.add(showLabel);
		
		enterprisesView = new ListBox();
		enterprisesView.addItem("Empresas con contrataciones");
		enterprisesView.addItem("Todas las empresas");
		enterprisesView.addChangeHandler(e -> onEnterprisesView());
		
		toolbar.add(enterprisesView);
		
		refreshBtn = new AonToolbarButton("Recargar", AON.CSS.aonIconRefresh());
		refreshBtn.addClickHandler(e -> loadModule());
		
		toolbar.add(refreshBtn);
	}

	public static native void back()
	/*-{
		$wnd.backCustomer();
	}-*/;
	
	private void onEnterprisesView() {
		createDomainChildsBookingResume();
	}
	
}
