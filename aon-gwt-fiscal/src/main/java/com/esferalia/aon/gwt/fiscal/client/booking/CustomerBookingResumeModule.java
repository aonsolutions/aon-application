package com.esferalia.aon.gwt.fiscal.client.booking;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.RegistryService;
import com.esferalia.aon.gwt.common.client.RegistryServiceAsync;
import com.esferalia.aon.gwt.common.client.RegistryServiceAsyncDecorator;
import com.esferalia.aon.gwt.common.client.RootLayoutPanel;
import com.esferalia.aon.gwt.common.client.json.DomainCompanyJSON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDateBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog.AonAcceptDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDomainSyncSelectionDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessagePanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTableButton;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbar;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarSmall;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarSmallButton;
import com.esferalia.aon.gwt.fiscal.client.MainEntryPoint;
import com.esferalia.aon.gwt.fiscal.client.registry.CustomerFeeDialog;
import com.esferalia.aon.gwt.fiscal.client.registry.RegistryModuleOptions;
import com.esferalia.aon.occam.api.model.Customer;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.DomainCompany;
import com.esferalia.aon.occam.api.model.RegistryParams;
import com.esferalia.aon.occam.api.model.fee.Fee;
import com.esferalia.aon.occam.api.model.product.OldItem;
import com.esferalia.aon.occam.api.model.registry.CustomerFeeParams;
import com.esferalia.aon.occam.api.model.security.Booking;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.type.AonStatus;
import com.esferalia.aon.occam.api.model.type.BillingPeriod;
import com.esferalia.aon.occam.api.model.type.RegistryStatus;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.http.client.Response;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.user.client.DOM;
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
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.ValueBoxBase.TextAlignment;
import com.google.gwt.user.client.ui.Widget;

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
	
	private AonToolbarSmallButton toolbarDomainChildsDiscBtn;
	private HTMLPanel domainChildsPanel;
	private Grid domainChildsTable;
	
	private AonToolbarSmallButton toolbarCustomerFeeDiscBtn;
	private AonToolbarSmallButton createCustomerFeeButton;
	private AonToolbarSmallButton toolbarCustomerFeeSaveBtn;
	private AonToolbarSmallButton toolbarCustomerFeeUndoBtn;
	private HTMLPanel customerFeePanel;
	private Grid customerFeeTable;
	private LinkedList<Fee> customerFeeList;
	
	private HTMLPanel messagePanel;

	private AonToolbar toolbar;
	private AonToolbarButton backBtn;
	private ListBox enterprisesView;
	private AonToolbarButton refreshBtn;
	private AonToolbarButton excelBtn;
	
	private Booking domainBooking;
	
	private Customer customer;
	private List<DomainCompany> customerDomains;
	
	private RegistryModuleOptions opt;
	
	private DateTimeFormat formatDate = DateTimeFormat.getFormat("dd/MM/yyyy");
	private DateTimeFormat formatBillingDate = DateTimeFormat.getFormat("MM/yyyy");
	
	private boolean isChildsOpen = true;
	private boolean isCustomerFeeOpen = true;
	
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
		
		customerFeePanel = new HTMLPanel("");
		customerFeePanel.addStyleName(AON.CSS.aonFlexColumn());
		
		container.add(customerPanel);
		container.add(domainsPanel);
		
		container.add(domainPanel);
		container.add(domainChildsPanel);
		
		container.add(customerFeePanel);
		
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

	// ---------- BookingResume
	
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
                        	
                        	// Init onLoad CustomerFee
                    		getCustomerFees();
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
	
	private AonToolbarSmall createDomainChildsToolbar() {
		AonToolbarSmall toolbar = new AonToolbarSmall("Empresas Hijas");
		
		toolbarDomainChildsDiscBtn = new AonToolbarSmallButton("Desplegar Empresas Hijas", AON.CSS.aonIconDown());
		toolbarDomainChildsDiscBtn.addClickHandler(e -> {
			isChildsOpen = !isChildsOpen;
			handleIcon(toolbarDomainChildsDiscBtn, isChildsOpen);
			if(isChildsOpen) domainChildsTable.getElement().getStyle().clearDisplay();
			else domainChildsTable.getElement().getStyle().setDisplay(Display.NONE);
		});
		
		toolbar.add(toolbarDomainChildsDiscBtn);
		
		return toolbar;
	}
	
	private void createDomainChildsBookingResume() {
		boolean allEnterprises = enterprisesView.getSelectedIndex() == 1;
		domainChildsPanel.clear();
		
		domainChildsPanel.add(createDomainChildsToolbar());
		
		domainChildsTable = new Grid(0, 10);
		domainChildsTable.clear();
		domainChildsTable.setWidth("100%");

		int row = domainChildsTable.insertRow(domainChildsTable.getRowCount());

		Label name = new Label("EMPRESA");
		Label booking = new Label("EXTENSIONES CONTRATADAS");
		Label extensions = new Label("EXTENSIONES HEREDADAS");
		Label bookingNum = new Label("N\u00b0 EXTEN.");
		Label status = new Label("ESTADO");
		Label expiration = new Label("F. EXPIRACI\u00f3N");
		Label users = new Label("USR.");
		Label portalUsers = new Label("USR. PORTAL");
		Label type = new Label("TIPO");
		Label action = new Label("");
		
		name.addStyleName(AON.CSS.aonHeaderTable());
		booking.addStyleName(AON.CSS.aonHeaderTable());
		extensions.addStyleName(AON.CSS.aonHeaderTable());
		bookingNum.addStyleName(AON.CSS.aonHeaderTable());
		status.addStyleName(AON.CSS.aonHeaderTable());
		expiration.addStyleName(AON.CSS.aonHeaderTable());
		users.addStyleName(AON.CSS.aonHeaderTable());
		portalUsers.addStyleName(AON.CSS.aonHeaderTable());
		type.addStyleName(AON.CSS.aonHeaderTable());
		action.addStyleName(AON.CSS.aonHeaderTable());

		domainChildsTable.setWidget(row, 0, name);
		domainChildsTable.setWidget(row, 1, booking);
		domainChildsTable.setWidget(row, 2, extensions);
		domainChildsTable.setWidget(row, 3, bookingNum);
		domainChildsTable.setWidget(row, 4, status);
		domainChildsTable.setWidget(row, 5, expiration);
		domainChildsTable.setWidget(row, 6, users);
		domainChildsTable.setWidget(row, 7, portalUsers);
		domainChildsTable.setWidget(row, 8, type);
		domainChildsTable.setWidget(row, 9, action);
		
		domainChildsTable.getCellFormatter().addStyleName(row, 0, AON.CSS.aonHeaderSticky());
		domainChildsTable.getCellFormatter().addStyleName(row, 1, AON.CSS.aonHeaderSticky());
		domainChildsTable.getCellFormatter().addStyleName(row, 2, AON.CSS.aonHeaderSticky());
		domainChildsTable.getCellFormatter().addStyleName(row, 3, AON.CSS.aonHeaderSticky());
		domainChildsTable.getCellFormatter().addStyleName(row, 4, AON.CSS.aonHeaderSticky());
		domainChildsTable.getCellFormatter().addStyleName(row, 5, AON.CSS.aonHeaderSticky());
		domainChildsTable.getCellFormatter().addStyleName(row, 6, AON.CSS.aonHeaderSticky());
		domainChildsTable.getCellFormatter().addStyleName(row, 7, AON.CSS.aonHeaderSticky());
		domainChildsTable.getCellFormatter().addStyleName(row, 8, AON.CSS.aonHeaderSticky());
		domainChildsTable.getCellFormatter().addStyleName(row, 9, AON.CSS.aonHeaderSticky());
		
		domainChildsTable.getColumnFormatter().getElement(0).getStyle().setWidth(300, Unit.PX);
		domainChildsTable.getColumnFormatter().getElement(3).getStyle().setWidth(70, Unit.PX);
		domainChildsTable.getColumnFormatter().getElement(4).getStyle().setWidth(70, Unit.PX);
		domainChildsTable.getColumnFormatter().getElement(5).getStyle().setWidth(100, Unit.PX);
		domainChildsTable.getColumnFormatter().getElement(6).getStyle().setWidth(50, Unit.PX);
		domainChildsTable.getColumnFormatter().getElement(7).getStyle().setWidth(80, Unit.PX);
		domainChildsTable.getColumnFormatter().getElement(8).getStyle().setWidth(80, Unit.PX);
		domainChildsTable.getColumnFormatter().getElement(9).getStyle().setWidth(25, Unit.PX);
		
		List<String> parentApps = domainBooking.getApps().stream().filter(aonApp -> AonStringUtils.isNotBlank(aonApp.getDescription())).map(aonApp -> aonApp.getDescription()).collect(Collectors.toList());
		List<Domain> childsDomain = domainBooking.getResume().getChilds().stream().filter(child -> AonStringUtils.isNotBlank(child.getDescription())).collect(Collectors.toList());
		childsDomain.sort((o1, o2) -> o1.getDescription().compareTo(o2.getDescription()));
		
		for(Domain domainChild : childsDomain) {
			
			// Get child and parent diff apps
			List<String> childApps = domainChild.getApps().stream().filter(domainApp -> null != domainApp.getApp() && AonStringUtils.isNotBlank(domainApp.getApp().getDescription())).map(domainApp -> domainApp.getApp().getDescription()).collect(Collectors.toList());
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
			
			Label bookingLabel = new Label((childAppsDiff.size() == 0 ? "Sin contrataciones" : String.join(", ", childAppsDiff)));
			
			Label extensionsLabel = new Label((parentAppsDiff.size() == 0 ? "Sin extensiones heredadas" : String.join(", ", parentAppsDiff)));
			
			Label bookingNumLabel = new Label(childAppsDiff.size() + " / " + parentAppsDiff.size() + " (" +  childApps.size() + ")");
			bookingNumLabel.setTitle(childAppsDiff.size() + " extensiones facturables / " + parentAppsDiff.size() + " extensiones heredadas del padre");
			
			String statusMessage = domainChild.getExpirationDate() != null && domainChild.getExpirationDate().before(new Date()) ? "Expirado" : (domainChild.isActive() ? "Activo" : "Inactivo");
			Label statusLabel = new Label(statusMessage);
			statusLabel.setTitle(domainChild.getExpirationDate() != null && domainChild.getExpirationDate().before(new Date()) ? ("F. expiraci\u00f3n : " + formatDate(domainChild.getExpirationDate())) : "");
			
			Label expirationLabel = new Label(formatDate(domainChild.getExpirationDate()));
			
			Label usersLabel = new Label(activeUsersDiff + " / " + domainChild.getMaxDefinedUsers());
			
			Label portalUsersLabel = new Label(portalUsersCount + "");
			
			Label typeLabel = new Label(null == domainChild.getDomainType() ? "" : domainChild.getDomainType().getName());
			
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
			domainChildsTable.setWidget(newRow, 2, extensionsLabel);
			domainChildsTable.setWidget(newRow, 3, bookingNumLabel);
			domainChildsTable.setWidget(newRow, 4, statusLabel);
			domainChildsTable.setWidget(newRow, 5, expirationLabel);
			domainChildsTable.setWidget(newRow, 6, usersLabel);
			domainChildsTable.setWidget(newRow, 7, portalUsersLabel);
			domainChildsTable.setWidget(newRow, 8, typeLabel);
			domainChildsTable.setWidget(newRow, 9, actionLabel);
			
			List<Label> rowLabels = new ArrayList<>();
			rowLabels.add(domainNameLabel);
			rowLabels.add(bookingLabel);
			rowLabels.add(extensionsLabel);
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
				extensionsLabel.addStyleName(AON.CSS.aonOddTableRow());
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
				domainChildsTable.getCellFormatter().addStyleName(newRow, 9, AON.CSS.aonOddTableRow());
			}
			
			domainChildsTable.getCellFormatter().getElement(newRow, 3).getStyle().setTextAlign(TextAlign.CENTER);
			domainChildsTable.getCellFormatter().getElement(newRow, 4).getStyle().setTextAlign(TextAlign.CENTER);
			domainChildsTable.getCellFormatter().getElement(newRow, 5).getStyle().setTextAlign(TextAlign.CENTER);
			domainChildsTable.getCellFormatter().getElement(newRow, 6).getStyle().setTextAlign(TextAlign.CENTER);
			domainChildsTable.getCellFormatter().getElement(newRow, 7).getStyle().setTextAlign(TextAlign.CENTER);
			
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
	
	// ---------- CustomerFees
	
	private AonToolbarSmall createCustomerFeeToolbar() {
		AonToolbarSmall toolbar = new AonToolbarSmall("Cuotas");
		
		toolbarCustomerFeeDiscBtn = new AonToolbarSmallButton("Desplegar Cuotas", AON.CSS.aonIconDown());
		toolbarCustomerFeeDiscBtn.addClickHandler(e -> {
			isCustomerFeeOpen = !isCustomerFeeOpen;
			handleIcon(toolbarCustomerFeeDiscBtn, isCustomerFeeOpen);
			if(isCustomerFeeOpen) customerFeeTable.getElement().getStyle().clearDisplay();
			else customerFeeTable.getElement().getStyle().setDisplay(Display.NONE);
		});
		
		toolbar.add(toolbarCustomerFeeDiscBtn);
		
		return toolbar;
	}
	
	private void getCustomerFees() {
		Integer customerId = getCustomer();
		
		CustomerFeeParams params = new CustomerFeeParams();
		params.setDomain(options.getDomain());
		params.setCustomer(customerId);
		
		params.setLimit(Integer.MAX_VALUE);
		params.setOffset(0);
		
		SERVICE.getCustomerFeeList(options.getDomainName(), options.getDomain(), options.getUser(), params,
				new AsyncCallback<LinkedList<Fee>>() {

					@Override
					public void onFailure(Throwable caught) {
						AonMessagePanel.showError(messagePanel, "Error cargando panel de facturaci\u00f3n: " + caught.getMessage());
					}

					@Override
					public void onSuccess(LinkedList<Fee> customerFeeListDB) {
						customerFeeList = customerFeeListDB;
						createCustomerFees();
					}
				});		
	}
	
	private void createCustomerFees() {
		customerFeePanel.clear();
		
		customerFeePanel.add(createCustomerFeeToolbar());
		
		customerFeeTable = new Grid(0, 10);
		customerFeeTable.clear();
		customerFeeTable.setWidth("100%");

		int row = customerFeeTable.insertRow(customerFeeTable.getRowCount());

		Label line = new Label("LINEA");
		Label concept = new Label("CONCEPTO");
		Label period = new Label("PERIODO");
		Label quantity = new Label("CANTIDAD");
		Label price = new Label("PRECIO");
		Label discount = new Label("DESCUENTO");
		Label startDate = new Label("F. DESDE");
		startDate.setTitle("F. DESDE FACTURACI\u00f3N");
		Label billingDate = new Label("F. FACTURACI\u00f3N");
		Label endDate = new Label("F. HASTA");
		endDate.setTitle("F. HASTA FACTURACI\u00f3N");
		
		HTMLPanel buttons = new HTMLPanel("");
		buttons.addStyleName(AON.CSS.aonItemFlex());
		buttons.getElement().getStyle().setProperty("justify-content", "right");
		
		createCustomerFeeButton = new AonToolbarSmallButton("Nueva Cuota", AON.CSS.aonIconAdd());
		createCustomerFeeButton.addClickHandler(e -> {
			new CustomerFeeDialog(options, customer) {
				
				@Override
				protected void onCreate(Fee fee) {
					SERVICE.createCustomerFeeList(options.getDomainName(), options.getDomain(), options.getUser(), fee, new AsyncCallback<Void>() {
						
						@Override
						public void onSuccess(Void result) {
							AonMessagePanel.showSuccess(messagePanel, "Se ha creado la cuota correctamente");
							getCustomerFees();
						}
						
						@Override
						public void onFailure(Throwable caught) {
							AonMessagePanel.showError(messagePanel, "Error creando cuota: " + caught.getMessage());
						}
					});
				}
				
				@Override
				protected void onAccept(Optional<OldItem> item, Optional<Double> price, Optional<String> discountExpr,
						Optional<Date> startDate, Optional<Date> endDate, Optional<Date> billingDate) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				protected void onAccept(Fee fee) {
					// TODO Auto-generated method stub
					
				}
			};
		});
		buttons.add(createCustomerFeeButton);
		
		toolbarCustomerFeeUndoBtn = new AonToolbarSmallButton(AON.MSG.undo(), AON.CSS.aonIconUndoAll());
		toolbarCustomerFeeUndoBtn.setEnabled(false);
		toolbarCustomerFeeUndoBtn.addClickHandler(e -> {
			AonDialog dialog = new AonDialog("Deshacer cambios cuotas",
					new HTML("Se va a proceder a deshacer los cambios, sin guardar, efectuados en las cuotas..<br>\u00bfRealmente quiere deshacer los cambios efectuados\u003f"));
			
			dialog.confirm(new AonAcceptDialogCallback() {

				@Override
				public void onCancel() {}

				@Override
				public void onAccept() {
					getCustomerFees();	
				}
			});
		});
		buttons.add(toolbarCustomerFeeUndoBtn);
		
		toolbarCustomerFeeSaveBtn = new AonToolbarSmallButton(AON.MSG.saveAction(), AON.CSS.aonIconSave());
		toolbarCustomerFeeSaveBtn.setEnabled(false);
		toolbarCustomerFeeSaveBtn.addClickHandler(e -> {
			AonMessagePanel.showLoading(messagePanel, "Guardando cuotas ...");
			SERVICE.saveCustomerFeeList(options.getDomainName(), options.getDomain(), options.getUser(), customerFeeList,
					new AsyncCallback<Integer>() {

						@Override
						public void onFailure(Throwable caught) {
							AonMessagePanel.showError(messagePanel, "Error guardando cuotas: " + caught.getMessage());
						}

						@Override
						public void onSuccess(Integer updates) {
							AonMessagePanel.showSuccess(messagePanel, "Se han actualizado " + updates + " cuotas correctamente");
							toolbarCustomerFeeUndoBtn.setEnabled(false);
							toolbarCustomerFeeSaveBtn.setEnabled(false);
							getCustomerFees();
						}
					});
		});
		buttons.add(toolbarCustomerFeeSaveBtn);
		
		line.addStyleName(AON.CSS.aonHeaderTable());
		concept.addStyleName(AON.CSS.aonHeaderTable());
		period.addStyleName(AON.CSS.aonHeaderTable());
		quantity.addStyleName(AON.CSS.aonHeaderTable());
		price.addStyleName(AON.CSS.aonHeaderTable());
		discount.addStyleName(AON.CSS.aonHeaderTable());
		startDate.addStyleName(AON.CSS.aonHeaderTable());
		billingDate.addStyleName(AON.CSS.aonHeaderTable());
		endDate.addStyleName(AON.CSS.aonHeaderTable());
		buttons.addStyleName(AON.CSS.aonHeaderTable());

		customerFeeTable.setWidget(row, 0, line);
		customerFeeTable.setWidget(row, 1, concept);
		customerFeeTable.setWidget(row, 2, period);
		customerFeeTable.setWidget(row, 3, quantity);
		customerFeeTable.setWidget(row, 4, price);
		customerFeeTable.setWidget(row, 5, discount);
		customerFeeTable.setWidget(row, 6, startDate);
		customerFeeTable.setWidget(row, 7, billingDate);
		customerFeeTable.setWidget(row, 8, endDate);
		customerFeeTable.setWidget(row, 9, buttons);
		
		customerFeeTable.getCellFormatter().addStyleName(row, 0, AON.CSS.aonHeaderSticky());
		customerFeeTable.getCellFormatter().addStyleName(row, 1, AON.CSS.aonHeaderSticky());
		customerFeeTable.getCellFormatter().addStyleName(row, 2, AON.CSS.aonHeaderSticky());
		customerFeeTable.getCellFormatter().addStyleName(row, 3, AON.CSS.aonHeaderSticky());
		customerFeeTable.getCellFormatter().addStyleName(row, 4, AON.CSS.aonHeaderSticky());
		customerFeeTable.getCellFormatter().addStyleName(row, 5, AON.CSS.aonHeaderSticky());
		customerFeeTable.getCellFormatter().addStyleName(row, 6, AON.CSS.aonHeaderSticky());
		customerFeeTable.getCellFormatter().addStyleName(row, 7, AON.CSS.aonHeaderSticky());
		customerFeeTable.getCellFormatter().addStyleName(row, 8, AON.CSS.aonHeaderSticky());
		customerFeeTable.getCellFormatter().addStyleName(row, 9, AON.CSS.aonHeaderSticky());
		customerFeeTable.getCellFormatter().getElement(row, 9).getStyle().setZIndex(1);
		
		customerFeeTable.getColumnFormatter().getElement(0).getStyle().setWidth(70, Unit.PX);
//		customerFeeTable.getColumnFormatter().getElement(1).getStyle().setWidth(38, Unit.PCT);
		customerFeeTable.getColumnFormatter().getElement(2).getStyle().setWidth(120, Unit.PX);
		customerFeeTable.getColumnFormatter().getElement(3).getStyle().setWidth(70, Unit.PX);
		customerFeeTable.getColumnFormatter().getElement(4).getStyle().setWidth(120, Unit.PX);
		customerFeeTable.getColumnFormatter().getElement(5).getStyle().setWidth(120, Unit.PX);
		customerFeeTable.getColumnFormatter().getElement(6).getStyle().setWidth(120, Unit.PX);
		customerFeeTable.getColumnFormatter().getElement(7).getStyle().setWidth(120, Unit.PX);
		customerFeeTable.getColumnFormatter().getElement(8).getStyle().setWidth(120, Unit.PX);
		customerFeeTable.getColumnFormatter().getElement(9).getStyle().setWidth(100, Unit.PX);
		
		for(Fee fee : customerFeeList) {

			int newRow = customerFeeTable.insertRow(customerFeeTable.getRowCount());
			
			TextBox lineTextBox = new TextBox();
			lineTextBox.setValue(fee.getLine().toString());
			lineTextBox.setWidth("50px");
			lineTextBox.addValueChangeHandler(e -> {
				try {
					Short newLine = Short.parseShort(lineTextBox.getValue());
					reorderAndSaveLine(fee, newLine, customerFeeList);
				} catch (NumberFormatException ex) {
					AonMessagePanel.showError(messagePanel, "El valor de la linea debe ser un entero");
				}
			});
			
			AutoResizeTextArea conceptTextArea = new AutoResizeTextArea(customerFeeTable, fee, newRow);
			conceptTextArea.setWidth("95%");
			conceptTextArea.setValue(fee.getDescription());

			ListBox periodListBox = createPeriodListBox(customerFeeTable, fee, newRow);
			periodListBox.setWidth("100px");
			setInputStyle(periodListBox);
			setSelectedValueLB(periodListBox, fee.getPeriod().getValue().toString());

			TextBox quantityTextBox = new TextBox();
			quantityTextBox.setWidth("50px");
			setInputStyle(quantityTextBox);
			quantityTextBox.setValue(null == fee.getQuantity() ? "" : fee.getQuantity().toString());
			quantityTextBox.addValueChangeHandler(e -> {
				fee.setQuantity(Double.parseDouble(e.getValue()));
				setModifyColor(customerFeeTable, fee, newRow);
			});

			TextBox priceTextBox = new TextBox();
			priceTextBox.setWidth("80px");
			setInputStyle(priceTextBox);
			priceTextBox.setValue(null == fee.getPrice() ? "" : fee.getPrice().toString());
			priceTextBox.addValueChangeHandler(e -> {
				fee.setPrice(Double.parseDouble(e.getValue()));
				setModifyColor(customerFeeTable, fee, newRow);
			});

			TextBox discountTextBox = new TextBox();
			discountTextBox.setWidth("80px");
			setInputStyle(discountTextBox);
			discountTextBox.setValue(null == fee.getDiscountExpr() ? "" : fee.getDiscountExpr());
			discountTextBox.addValueChangeHandler(e -> {
				String expression = e.getValue();
				String result = e.getValue();
				
				try {
					if(AonStringUtils.contains(expression, ",") && AonStringUtils.contains(expression, "."))
						expression= expression.replaceAll("\\.", "");
					expression= expression.replaceAll(",", ".");
					
					Double expressionValue = evalExpression(expression);
					result = null == expressionValue ? "" : expressionValue.toString();
					fee.setDiscount(Double.parseDouble(result));
				} catch (Exception ex) {
					AonMessagePanel.showWarning(messagePanel, new HTML("La expresi\u00f3n de <b>Descuento</b> que ha introducido no es correcta"));
				}
				
				fee.setDiscountExpr(expression);
				setModifyColor(customerFeeTable, fee, newRow);
				
			});
			
			AonDateBox startDateBox = new AonDateBox();
			startDateBox.getElement().getStyle().setTextAlign(TextAlign.CENTER);
			startDateBox.addStyleName("gwt-TextBox");
			setInputStyle(startDateBox);
			startDateBox.setValue(fee.getStartDate());
			startDateBox.addValueChangeHandler(e -> {
				fee.setStartDate(e.getValue());
				setModifyColor(customerFeeTable, fee, newRow);
			});

			HTMLPanel billingDatePanel = new HTMLPanel("");
			billingDatePanel.addStyleName(AON.CSS.aonItemFlex());
			
			ListBox monthLB = createMonthListBox();
			TextBox yearTB = createYearTextBox();
			
			monthLB.addChangeHandler(e -> {
				fee.setBillingDate(createBillingDate(monthLB.getSelectedValue(), yearTB.getValue()));
				setModifyColor(customerFeeTable, fee, newRow);
			});
			
			yearTB.addValueChangeHandler(e -> {
				fee.setBillingDate(createBillingDate(monthLB.getSelectedValue(), yearTB.getValue()));
				setModifyColor(customerFeeTable, fee, newRow);
			});
			
			setSelectedValueLB(monthLB, fee.getBillingDate().getMonth() + "");
			yearTB.setValue((fee.getBillingDate().getYear() + 1900) + "");
			
			billingDatePanel.add(monthLB);
			billingDatePanel.add(yearTB);

			AonDateBox endDateBox = new AonDateBox();
			endDateBox.getElement().getStyle().setTextAlign(TextAlign.CENTER);
			endDateBox.addStyleName("gwt-TextBox");
			setInputStyle(endDateBox);
			endDateBox.setValue(fee.getEndDate());
			endDateBox.addValueChangeHandler(e -> {
				fee.setEndDate(e.getValue());
				setModifyColor(customerFeeTable, fee, newRow);
			});
			
			HTMLPanel buttonsPanel = new HTMLPanel("");
			buttonsPanel.setStyleName(AON.CSS.aonItemFlex());
			buttonsPanel.getElement().getStyle().setProperty("justify-content", "right");
			
			AonToolbarSmallButton infoBtn = new AonToolbarSmallButton("", AON.CSS.aonIconInfo());
			infoBtn.setTitle(createFeeInfo(fee));
			buttonsPanel.add(infoBtn);
			
			AonToolbarSmallButton deleteBtn = new AonToolbarSmallButton("Eliminar", AON.CSS.aonIconDelete());
			deleteBtn.addClickHandler(e -> {
				AonDialog dialog = new AonDialog("Eliminaci\u00f3n Cuota",
						new HTML("Se va a proceder a eliminar la cuota.<br>\u00bfEsta seguro que desea proceder con la eliminaci\u00f3n\u003f<br>Este proceso ser\u00e5 irreversible"));
				
				dialog.confirm(new AonAcceptDialogCallback() {

					@Override
					public void onCancel() {
						// Nothing to do here
					}

					@Override
					public void onAccept() {
						LinkedList<Fee> deleteFees = new LinkedList<>();
						deleteFees.add(fee);
						
						AonMessagePanel.showLoading(messagePanel, "Elimando cuota ...");
						SERVICE.deleteCustomerFeeList(options.getDomainName(), options.getDomain(), options.getUser(), deleteFees,
								new AsyncCallback<Void>() {

									@Override
									public void onFailure(Throwable caught) {
										AonMessagePanel.showError(messagePanel, "Error eliminando cuotas: " + caught.getMessage());
									}

									@Override
									public void onSuccess(Void result) {
										AonMessagePanel.showSuccess(messagePanel, "Se han eliminado la cuota correctamente");
										loadModule();
									}
								});
					}
				});
			});
			buttonsPanel.add(deleteBtn);
			
			checkFeeStatus(startDateBox, endDateBox, fee);

			checkRowAndModify(customerFeeTable, newRow, fee, lineTextBox);
			checkRowAndModify(customerFeeTable, newRow, fee, conceptTextArea);
			checkRowAndModify(customerFeeTable, newRow, fee, periodListBox);
			checkRowAndModify(customerFeeTable, newRow, fee, quantityTextBox);
			checkRowAndModify(customerFeeTable, newRow, fee, priceTextBox);
			checkRowAndModify(customerFeeTable, newRow, fee, discountTextBox);
			checkRowAndModify(customerFeeTable, newRow, fee, startDateBox);
			checkRowAndModify(customerFeeTable, newRow, fee, billingDatePanel);
			checkRowAndModify(customerFeeTable, newRow, fee, endDateBox);
			checkRowAndModify(customerFeeTable, newRow, fee, buttonsPanel);

			customerFeeTable.setWidget(newRow, 0, lineTextBox);
			customerFeeTable.setWidget(newRow, 1, conceptTextArea);
			customerFeeTable.setWidget(newRow, 2, periodListBox);
			customerFeeTable.setWidget(newRow, 3, quantityTextBox);
			customerFeeTable.setWidget(newRow, 4, priceTextBox);
			customerFeeTable.setWidget(newRow, 5, discountTextBox);
			customerFeeTable.setWidget(newRow, 6, startDateBox);
			customerFeeTable.setWidget(newRow, 7, billingDatePanel);
			customerFeeTable.setWidget(newRow, 8, endDateBox);
			customerFeeTable.setWidget(newRow, 9, buttonsPanel);

			customerFeeTable.getCellFormatter().getElement(newRow, 0).getStyle().setTextAlign(TextAlign.CENTER);
			customerFeeTable.getCellFormatter().getElement(newRow, 2).getStyle().setTextAlign(TextAlign.CENTER);
			customerFeeTable.getCellFormatter().getElement(newRow, 3).getStyle().setTextAlign(TextAlign.CENTER);
			customerFeeTable.getCellFormatter().getElement(newRow, 4).getStyle().setTextAlign(TextAlign.CENTER);
			customerFeeTable.getCellFormatter().getElement(newRow, 5).getStyle().setTextAlign(TextAlign.CENTER);
			customerFeeTable.getCellFormatter().getElement(newRow, 6).getStyle().setTextAlign(TextAlign.CENTER);
			customerFeeTable.getCellFormatter().getElement(newRow, 7).getStyle().setTextAlign(TextAlign.CENTER);
			customerFeeTable.getCellFormatter().getElement(newRow, 8).getStyle().setTextAlign(TextAlign.CENTER);
			customerFeeTable.getCellFormatter().getElement(newRow, 9).getStyle().setTextAlign(TextAlign.CENTER);
			
			if (newRow % 2 == 0) {
				lineTextBox.addStyleName(AON.CSS.aonOddTableRow());
				conceptTextArea.addStyleName(AON.CSS.aonOddTableRow());
				periodListBox.addStyleName(AON.CSS.aonOddTableRow());
				quantityTextBox.addStyleName(AON.CSS.aonOddTableRow());
				priceTextBox.addStyleName(AON.CSS.aonOddTableRow());
				discountTextBox.addStyleName(AON.CSS.aonOddTableRow());
				startDateBox.addStyleName(AON.CSS.aonOddTableRow());
				billingDatePanel.addStyleName(AON.CSS.aonOddTableRow());
				endDateBox.addStyleName(AON.CSS.aonOddTableRow());
				infoBtn.addStyleName(AON.CSS.aonOddTableRow());
				
				customerFeeTable.getCellFormatter().addStyleName(newRow, 0, AON.CSS.aonOddTableRow());
				customerFeeTable.getCellFormatter().addStyleName(newRow, 1, AON.CSS.aonOddTableRow());
				customerFeeTable.getCellFormatter().addStyleName(newRow, 2, AON.CSS.aonOddTableRow());
				customerFeeTable.getCellFormatter().addStyleName(newRow, 3, AON.CSS.aonOddTableRow());
				customerFeeTable.getCellFormatter().addStyleName(newRow, 4, AON.CSS.aonOddTableRow());
				customerFeeTable.getCellFormatter().addStyleName(newRow, 5, AON.CSS.aonOddTableRow());
				customerFeeTable.getCellFormatter().addStyleName(newRow, 6, AON.CSS.aonOddTableRow());
				customerFeeTable.getCellFormatter().addStyleName(newRow, 7, AON.CSS.aonOddTableRow());
				customerFeeTable.getCellFormatter().addStyleName(newRow, 8, AON.CSS.aonOddTableRow());
				customerFeeTable.getCellFormatter().addStyleName(newRow, 9, AON.CSS.aonOddTableRow());
			}
			
			customerFeeTable.getRowFormatter().getElement(newRow).getStyle().setHeight(25.00, Unit.PX);
		}
		
		customerFeePanel.add(customerFeeTable);
	}
	
	private void reorderAndSaveLine(Fee fee, short newLine, LinkedList<Fee> customerFeeList) {
		
		customerFeeList.sort((o1, o2) -> o1.getLine().compareTo(o2.getLine()));
		
		HashSet<Short> existingNumber = new HashSet<>();
		existingNumber.add(newLine);
		
		for(int i=0; i<customerFeeList.size(); i++) {
			
			// Si es el que se esta actualizando se salta
			if(customerFeeList.get(i).equals(fee) || customerFeeList.get(i).getLine() < newLine) { 
				existingNumber.add(customerFeeList.get(i).getLine());
				
				if(customerFeeList.get(i).equals(fee)) existingNumber.add(newLine);
				
				continue;
			}
			
			// Si es la linea que se quiere insertar, o mayor se le suma una
			else if(customerFeeList.get(i).getLine() == newLine) {
				customerFeeList.get(i).setLine((short) (customerFeeList.get(i).getLine() + 1));
				customerFeeList.get(i).setModify(true);
				
				existingNumber.add(customerFeeList.get(i).getLine());
			}
			
			// Si es mayor se le suma una
			else if(customerFeeList.get(i).getLine() > newLine) {
				customerFeeList.get(i).setLine((short) (existingNumber.stream().collect(Collectors.toList()).get(existingNumber.size() - 1) + 1));
				customerFeeList.get(i).setModify(true);
				
				existingNumber.add(customerFeeList.get(i).getLine());
			}
			
		}
		
		fee.setLine(newLine);
		fee.setModify(true);
		
		// Guardar Cuotas
		AonMessagePanel.showLoading(messagePanel, "Guardando cuotas ...");
		SERVICE.saveCustomerFeeList(options.getDomainName(), options.getDomain(), options.getUser(), customerFeeList,
				new AsyncCallback<Integer>() {

					@Override
					public void onFailure(Throwable caught) {
						AonMessagePanel.showError(messagePanel, "Error guardando cuotas: " + caught.getMessage());
					}

					@Override
					public void onSuccess(Integer updates) {
						AonMessagePanel.showSuccess(messagePanel, "Se han actualizado " + updates + " cuotas correctamente");
						toolbarCustomerFeeUndoBtn.setEnabled(false);
						toolbarCustomerFeeSaveBtn.setEnabled(false);
						getCustomerFees();
					}
				});
	}

	private TextBox createYearTextBox() {
		TextBox tb = new TextBox();
		tb.setMaxLength(4);
		tb.setHeight("2em");
		tb.setWidth("4em");
		tb.setAlignment(TextAlignment.CENTER);
		tb.getElement().getStyle().setProperty("padding", "0 5px");
		tb.getElement().getStyle().setProperty("placeholder", "aaaa");
		
		return tb;
	}
	
	private ListBox createMonthListBox() {
		ListBox lb = new ListBox();
		lb.setHeight("2em");
		lb.getElement().getStyle().setProperty("padding", "0 5px");

		lb.addItem("-", "");
		lb.addItem("Ene.", "0");
		lb.addItem("Feb.", "1");
		lb.addItem("Mar.", "2");
		lb.addItem("Abr.", "3");
		lb.addItem("May.", "4");
		lb.addItem("Jun.", "5");
		lb.addItem("Jul.", "6");
		lb.addItem("Ago.", "7");
		lb.addItem("Sep.", "8");
		lb.addItem("Oct.", "9");
		lb.addItem("Nov.", "10");
		lb.addItem("Dic.", "11");

		return lb;
	}

	private Date createBillingDate(String monthStr, String yearStr) {
		if(AonStringUtils.isBlank(monthStr) || AonStringUtils.isBlank(yearStr))
			return null;
		
		return new Date(Integer.parseInt(yearStr) - 1900, Integer.parseInt(monthStr), 1);
	}

	private void checkFeeStatus(AonDateBox startDateBox, AonDateBox endDateBox, Fee fee) {
		if(null != fee.getEndDate() && fee.getEndDate().before(fee.getBillingDate())) {
			endDateBox.getElement().getStyle().setColor("red");
			endDateBox.getElement().getStyle().setFontWeight(FontWeight.BOLD);
			endDateBox.setTitle("La fecha fin es anterior a la fecha de facturaci\u00f3n");
		} else if(null != fee.getStartDate() && fee.getStartDate().after(fee.getBillingDate())) {
			startDateBox.getElement().getStyle().setColor("red");
			startDateBox.getElement().getStyle().setFontWeight(FontWeight.BOLD);
			startDateBox.setTitle("La fecha inicio es posterior a la fecha de facturaci\u00f3n");
		}
	}

	private ListBox createStatusListBox(Grid table, Fee fee, int row) {
		ListBox lb = new ListBox();
		lb.addItem("Activo", "Activo");
		lb.addItem("Inactivo", "Inactivo");
		lb.addItem("Bloqueado", "Bloqueado");
		lb.addChangeHandler(e -> {
			fee.getCustomer().setStatus(RegistryStatus.safeValueOf(lb.getSelectedValue()));
			setModifyColor(table, fee, row);
			
		});
		return lb;
	}

	private ListBox createPeriodListBox(Grid table, Fee fee, int row) {
		ListBox lb = new ListBox();
		lb.addItem("Sin periodo", "0");
		lb.addItem("Mensual", "1");
		lb.addItem("Bimensual", "2");
		lb.addItem("Trimestral", "3");
		lb.addItem("Cuatrimestral", "4");
		lb.addItem("Semestral", "5");
		lb.addItem("Anual", "6");
		lb.addChangeHandler(e -> {
			fee.setPeriod(BillingPeriod.safeValueOf(lb.getSelectedValue()));
			setModifyColor(table, fee, row);
		});
		return lb;
	}

	private String createFeeInfo(Fee fee) {
		String tooltip = "";
		
		tooltip += "Seguridad: " + fee.getSecurityLevel().getName();
		tooltip += "\nC. Trabajo: " + fee.getWorkplace().getDescription();
		
		if(null != fee.getSeller() && AonStringUtils.isNotBlank(fee.getSeller().getName())) tooltip += "\nComercial: " + fee.getSeller().getName();
		if(null != fee.getInvoicingGroup() && AonStringUtils.isNotBlank(fee.getInvoicingGroup().getDescription())) tooltip += "\nG. Facturac\u00f3n: " + fee.getInvoicingGroup().getDescription();
		if(null != fee.getProject() && AonStringUtils.isNotBlank(fee.getProject().getName())) tooltip += "\nProyecto: " + fee.getProject().getName();
		
		return tooltip;
	}
	
	private void setSelectedValueLB(ListBox lBox, String str) {
		String text = str;
		int indexToFind = 0;
		for (int i = 0; i < lBox.getItemCount(); i++) {
			if (lBox.getValue(i).equals(text)) {
				indexToFind = i;
				break;
			}
		}
		lBox.setSelectedIndex(indexToFind);
	}

	private void checkRowAndModify(Grid grid, int row, Fee fee, Widget widget) {
		if (row % 2 == 0)
			widget.addStyleName(AON.CSS.aonOddTableRow());

		if (fee.isModify()) {
			widget.addStyleName(AON.CSS.aonModifyTableRow());

			grid.getCellFormatter().addStyleName(row, 0, AON.CSS.aonModifyTableRow());
			grid.getCellFormatter().addStyleName(row, 1, AON.CSS.aonModifyTableRow());
			grid.getCellFormatter().addStyleName(row, 2, AON.CSS.aonModifyTableRow());
			grid.getCellFormatter().addStyleName(row, 3, AON.CSS.aonModifyTableRow());
			grid.getCellFormatter().addStyleName(row, 4, AON.CSS.aonModifyTableRow());
			grid.getCellFormatter().addStyleName(row, 5, AON.CSS.aonModifyTableRow());
			grid.getCellFormatter().addStyleName(row, 6, AON.CSS.aonModifyTableRow());
			grid.getCellFormatter().addStyleName(row, 7, AON.CSS.aonModifyTableRow());
			grid.getCellFormatter().addStyleName(row, 8, AON.CSS.aonModifyTableRow());
		} else {
			widget.removeStyleName(AON.CSS.aonModifyTableRow());

			grid.getCellFormatter().removeStyleName(row, 0, AON.CSS.aonModifyTableRow());
			grid.getCellFormatter().removeStyleName(row, 1, AON.CSS.aonModifyTableRow());
			grid.getCellFormatter().removeStyleName(row, 2, AON.CSS.aonModifyTableRow());
			grid.getCellFormatter().removeStyleName(row, 3, AON.CSS.aonModifyTableRow());
			grid.getCellFormatter().removeStyleName(row, 4, AON.CSS.aonModifyTableRow());
			grid.getCellFormatter().removeStyleName(row, 5, AON.CSS.aonModifyTableRow());
			grid.getCellFormatter().removeStyleName(row, 6, AON.CSS.aonModifyTableRow());
			grid.getCellFormatter().removeStyleName(row, 7, AON.CSS.aonModifyTableRow());
			grid.getCellFormatter().removeStyleName(row, 8, AON.CSS.aonModifyTableRow());
		}

	}
	
	private void setModifyColor(Grid table, Fee fee, int row) {
		table.getCellFormatter().addStyleName(row, 0, AON.CSS.aonModifyTableRow());
		table.getCellFormatter().addStyleName(row, 1, AON.CSS.aonModifyTableRow());
		table.getCellFormatter().addStyleName(row, 2, AON.CSS.aonModifyTableRow());
		table.getCellFormatter().addStyleName(row, 3, AON.CSS.aonModifyTableRow());
		table.getCellFormatter().addStyleName(row, 4, AON.CSS.aonModifyTableRow());
		table.getCellFormatter().addStyleName(row, 5, AON.CSS.aonModifyTableRow());
		table.getCellFormatter().addStyleName(row, 6, AON.CSS.aonModifyTableRow());
		table.getCellFormatter().addStyleName(row, 7, AON.CSS.aonModifyTableRow());
		table.getCellFormatter().addStyleName(row, 8, AON.CSS.aonModifyTableRow());
		
		table.getWidget(row, 0).addStyleName(AON.CSS.aonModifyTableRow());
		table.getWidget(row, 1).addStyleName(AON.CSS.aonModifyTableRow());
		table.getWidget(row, 2).addStyleName(AON.CSS.aonModifyTableRow());
		table.getWidget(row, 3).addStyleName(AON.CSS.aonModifyTableRow());
		table.getWidget(row, 4).addStyleName(AON.CSS.aonModifyTableRow());
		table.getWidget(row, 5).addStyleName(AON.CSS.aonModifyTableRow());
		table.getWidget(row, 6).addStyleName(AON.CSS.aonModifyTableRow());
		table.getWidget(row, 7).addStyleName(AON.CSS.aonModifyTableRow());
		table.getWidget(row, 8).addStyleName(AON.CSS.aonModifyTableRow());
		
		toolbarCustomerFeeUndoBtn.setEnabled(true);
		toolbarCustomerFeeSaveBtn.setEnabled(true);
		
		fee.setModify(true);
	}
	
	private void setInputStyle(Widget widget) {
		widget.setHeight("2em");
		widget.getElement().getStyle().setProperty("padding", "0 5px");
	}
	
	// ------------------------------------------ Eval Expression
	
	public double evalExpression(String expression) {
		return calculate(expression);
	}

	public final native double calculate(String expression) /*-{
		return eval(expression);
	}-*/;
	
	// ------------------------------------------ AutoResizeTextArea

	public class AutoResizeTextArea extends TextArea implements ValueChangeHandler<String>, KeyUpHandler {

		private final int MIN_HEIGHT = 20;
		private Fee fee;
		private int row;
		private Grid table;

		public AutoResizeTextArea(Grid table, Fee fee, int row) {
			super();
			this.table = table;
			this.fee = fee;
			this.row = row;
			this.getElement().getStyle().setProperty("resize", "none");
			addKeyUpHandler(this);
			addValueChangeHandler(this);
			Scheduler.get().scheduleDeferred(() -> adjustHeight());
		}

		@Override
		public void setText(String text) {
			super.setText(text);
			Scheduler.get().scheduleDeferred(() -> adjustHeight());
		}

		@Override
		public void setValue(String text) {
			super.setValue(text);
			Scheduler.get().scheduleDeferred(() -> adjustHeight());
		}

		@Override
		public void onKeyUp(KeyUpEvent event) {
			Scheduler.get().scheduleDeferred(() -> adjustHeight());
		}

		@Override
		public void onValueChange(ValueChangeEvent<String> event) {
			Scheduler.get().scheduleDeferred(() -> adjustHeight());
			this.fee.setDescription(event.getValue());
			setModifyColor(this.table, this.fee, this.row);
		}

		private void adjustHeight() {
			int scrollHeight = getElement().getScrollHeight();
			int offsetHeight = getElement().getOffsetHeight();
			int clientHeight = getElement().getClientHeight();
			int newHeight = Math.max(scrollHeight, MIN_HEIGHT);
			if (newHeight > offsetHeight || newHeight > clientHeight) {
				DOM.setStyleAttribute(getElement(), "height", newHeight + "px");
			}
		}
	}
	
	private void handleIcon(AonToolbarSmallButton button, boolean open) {
		if (open) {
			button.removeStyleName(AON.CSS.aonIconLeft());
			button.addStyleName(AON.CSS.aonIconDown());

			if (button.equals(toolbarDomainChildsDiscBtn))
				button.setTitle("Colapsar Empresas Hijas");
			if (button.equals(toolbarCustomerFeeDiscBtn))
				button.setTitle("Colapsar Cuotas");
		} else {
			button.removeStyleName(AON.CSS.aonIconDown());
			button.addStyleName(AON.CSS.aonIconLeft());

			if (button.equals(toolbarDomainChildsDiscBtn))
				button.setTitle("Desplegar Empresas Hijas");
			if (button.equals(toolbarCustomerFeeDiscBtn))
				button.setTitle("Desplegar Cuotas");
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
		
		excelBtn = new AonToolbarButton("Resumen Contrataci\u00f3n (XLS)", AON.CSS.aonIconExcel());
		excelBtn.setVisible(false);
		excelBtn.addClickHandler(e -> Window.alert("Export Excel"));
		
		toolbar.add(excelBtn);
	}

	public static native void back()
	/*-{
		$wnd.backCustomer();
	}-*/;
	
	private void onEnterprisesView() {
		createDomainChildsBookingResume();
	}
	
}
