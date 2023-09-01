package com.esferalia.aon.gwt.fiscal.client.registry;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.RegistryServiceAsync;
import com.esferalia.aon.gwt.common.client.json.DomainCompanyJSON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonContextMenu;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomerTooltip;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog.AonAcceptDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessagePanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTableButton;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarSmall;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarSmallButton;
import com.esferalia.aon.occam.api.model.BookingCheck;
import com.esferalia.aon.occam.api.model.Customer;
import com.esferalia.aon.occam.api.model.DomainCompany;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.fee.Fee;
import com.esferalia.aon.occam.api.model.product.OldItem;
import com.esferalia.aon.occam.api.model.registry.CustomerFeeParams;
import com.esferalia.aon.occam.api.model.registry.RegistryItemStatus;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.type.AonStatus;
import com.esferalia.aon.occam.api.model.type.RegistryStatus;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.UrlBuilder;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.ScrollPanel;

public class BookingCustomer extends HTMLPanel {
	
	// ------- BookingWithOutFee
	
	class CreateCustomerFeeCommand implements ScheduledCommand {

		@Override
		public void execute() {
			createCustomerFee(bookingWithOutFeeMenu.getItem(), bookingWithOutFeeMenu.getCustomer());
		}
		
		private void createCustomerFee(OldItem oldItem, Customer customer) {
			new CustomerFeeDialog(options, oldItem, customer) {
				
				@Override
				protected void onAccept(Fee fee) {}
				
				@Override
				protected void onCreate(Fee fee) {
					SERVICE.createCustomerFeeList(options.getDomainName(), options.getDomain(), options.getUser(), fee, new AsyncCallback<Void>() {
						
						@Override
						public void onSuccess(Void result) {
							AonMessagePanel.showSuccess(messagePanel, "Se ha creado la cuota correctamente");
							setBookingCustomer(customer, customerDomains);
						}
						
						@Override
						public void onFailure(Throwable caught) {
							AonMessagePanel.showError(messagePanel, "Error creando cuota: " + caught.getMessage());
						}
					});
				}

				@Override
				protected void onAccept(Optional<OldItem> item, Optional<Double> price, Optional<String> discountExpr, Optional<Date> startDate, Optional<Date> endDate, Optional<Date> billingDate) {}
				
			};
		}

	}
	
	class UpdateBookingCommand implements ScheduledCommand {

		@Override
		public void execute() {
			updateBooking();
		}
		
		private void updateBooking() {
			new BookingCheckDialog(options, bookingWithOutFeeMenu.getBookingCheck()) {
				
				@Override
				protected void onCreate(BookingCheck bookingCheck) {}
				
				@Override
				protected void onUpdate(BookingCheck bookingCheck) {
					SERVICE.saveBookingCheck(options.getDomainName(), options.getDomain(), options.getUser(), bookingCheck, new AsyncCallback<Void>() {
						
						@Override
						public void onSuccess(Void result) {
							AonMessagePanel.showSuccess(messagePanel, "Se ha actualizado la contrataci\u00f3n correctamente");
							setBookingCustomer(customer, customerDomains);
						}
						
						@Override
						public void onFailure(Throwable caught) {
							AonMessagePanel.showError(messagePanel, "Error actualizando contrataci\u00f3n: " + caught.getMessage());
						}
					});
				}
			};
		}

	}
	
	class BookingWithOutFeeMenu extends AonContextMenu {
		
		private Integer id;
		private OldItem item;
		private Customer customer;
		private BookingCheck bookingCheck;
		
		private MenuItem createCustomerFee;
		private MenuItem updateBooking;

		public BookingWithOutFeeMenu() {
			createCustomerFee = addMenuItem("Crear Cuota", new CreateCustomerFeeCommand(), AON.CSS.aonIconAdd(), "createCustomerFee");
			updateBooking = addMenuItem("Actualizar Contrataci\u00f3n", new UpdateBookingCommand(), AON.CSS.aonIconEdit(), "updateBooking");
		}
		
		private MenuItem addMenuItem(String title, ScheduledCommand command, String iconStyle, String debugId) {
			MenuItem item = addItem(title, command, iconStyle, AON.AON_ICON_CMD_BUTTON, AON.CSS.aonCmdItem());
			item.ensureDebugId(debugId);
			return item;
		}
		
		public void setId(Integer id) {
			this.id = id;
		}
		
		public Integer getId() {
			return this.id;
		}

		public OldItem getItem() {
			return item;
		}

		public void setItem(OldItem item) {
			this.item = item;
		}

		public Customer getCustomer() {
			return customer;
		}

		public void setCustomer(Customer customer) {
			this.customer = customer;
		}
		
		public BookingCheck getBookingCheck() {
			return bookingCheck;
		}

		public void setBookingCheck(BookingCheck bookingCheck) {
			this.bookingCheck = bookingCheck;
		}
		
		public void setHasFee(boolean hasFee) {
			this.createCustomerFee.setVisible(!hasFee);
		}

	}
	
	// ------- FeeWithOutBooking
	
	class RemoveCustomerFeeCommand implements ScheduledCommand {

		@Override
		public void execute() {
			removeCustomerFee();
		}
		
		private void removeCustomerFee() {
			AonDialog dialog = new AonDialog("Eliminaci\u00f3n Cuota",
					new HTML("Se va a proceder a eliminar la cuota.<br>\u00bfEsta seguro que desea proceder con la eliminaci\u00f3n\u003f. Este proceso ser\u00e1 irreversible"));
			
			dialog.confirm(new AonAcceptDialogCallback() {

				@Override
				public void onCancel() {
					// Nothing to do here
				}

				@Override
				public void onAccept() {
					AonMessagePanel.showLoading(messagePanel, "Elimando cuota seleccionada ...");
					LinkedList<Fee> selectedFees = new LinkedList<>();
					selectedFees.add(new Fee().setId(feeWithoutbookintMenu.getId()));
					
					SERVICE.deleteCustomerFeeList(options.getDomainName(), options.getDomain(), options.getUser(), selectedFees,
							new AsyncCallback<Void>() {

								@Override
								public void onFailure(Throwable caught) {
									AonMessagePanel.showError(messagePanel, "Error eliminando cuota: " + caught.getMessage());
								}

								@Override
								public void onSuccess(Void result) {
									AonMessagePanel.showSuccess(messagePanel, "Se han eliminado la cuota correctamente");
									setBookingCustomer(customer, customerDomains);
								}
							});
				}
			});
		}

	}
	
	class CreateBookingCommand implements ScheduledCommand {

		@Override
		public void execute() {
			createBooking();
		}
		
		private void createBooking() {
			new BookingCheckDialog(options, feeWithoutbookintMenu.getItem(), feeWithoutbookintMenu.getCustomer()) {
				
				@Override
				protected void onCreate(BookingCheck bookingCheck) {
					SERVICE.saveBookingCheck(options.getDomainName(), options.getDomain(), options.getUser(), bookingCheck, new AsyncCallback<Void>() {
						
						@Override
						public void onSuccess(Void result) {
							AonMessagePanel.showSuccess(messagePanel, "Se ha creado la contrataci\u00f3n correctamente");
							setBookingCustomer(customer, customerDomains);
						}
						
						@Override
						public void onFailure(Throwable caught) {
							AonMessagePanel.showError(messagePanel, "Error creando contrataci\u00f3n: " + caught.getMessage());
						}
					});
				}
				
				@Override
				protected void onUpdate(BookingCheck bookingCheck) {}
			};
		}

	}
	
	class FeeWithoutbookintMenu extends AonContextMenu {
		
		private Integer id;
		private OldItem item;
		private Customer customer;

		private MenuItem removeCustomerFee;
		private MenuItem createBooking;

		public FeeWithoutbookintMenu() {
			removeCustomerFee = addMenuItem("Eliminar Cuota", new RemoveCustomerFeeCommand(), AON.CSS.aonIconDelete(), "removeCustomerFee");
			createBooking = addMenuItem("Crear Contrataci\u00f3n", new CreateBookingCommand(), AON.CSS.aonIconAdd(), "createBooking");
		}
		
		private MenuItem addMenuItem(String title, ScheduledCommand command, String iconStyle, String debugId) {
			MenuItem item = addItem(title, command, iconStyle, AON.AON_ICON_CMD_BUTTON, AON.CSS.aonCmdItem());
			item.ensureDebugId(debugId);
			return item;
		}
		
		public void setId(Integer id) {
			this.id = id;
		}
		
		public Integer getId() {
			return this.id;
		}

		public OldItem getItem() {
			return item;
		}

		public void setItem(OldItem item) {
			this.item = item;
		}

		public Customer getCustomer() {
			return customer;
		}

		public void setCustomer(Customer customer) {
			this.customer = customer;
		}

	}
	
	// ------- Variables
	
	private BookingWithOutFeeMenu bookingWithOutFeeMenu;
	private FeeWithoutbookintMenu feeWithoutbookintMenu;
	
	private FocusPanel focusPanel;
	private ScrollPanel scrollPanel;
	private HTMLPanel container;
	
	private HTMLPanel messagePanel;
	private HTMLPanel customerPanel;
	private HTMLPanel domainsPanel;
	
	private HTMLPanel bookingPanel;
	private AonToolbarSmallButton toolbarBookingDiscBtn;
	private DeckPanel bookingDeckPanel;
	private Grid bookingGrid;
	private HTMLPanel bookingMessagePanel;
	
	private HTMLPanel feeWithoutBookingPanel;
	private AonToolbarSmallButton toolbarFeeWithoutBookingDiscBtn;
	private DeckPanel feeWithoutBookingDeckPanel;
	private Grid feeWithoutBookingGrid;
	private HTMLPanel feeWithoutBookingMessagePanel;
	
	private Customer customer;
	private List<DomainCompany> customerDomains;
	
	private boolean isBookingOpen = true;
	private boolean isFeeWithoutBookingOpen = true;
	
	private RegistryServiceAsync SERVICE; 
	private RegistryModuleOptions options;
	
	private DateTimeFormat formatDate = DateTimeFormat.getFormat("dd/MM/yyyy");
	
	private AonCustomerTooltip aonCustomerTooltip;
	
	// ------- Constructor
	
	public BookingCustomer(RegistryServiceAsync service, RegistryModuleOptions opt) {
		super("");
		
		this.SERVICE = service;
		this.options = opt;
		
		aonCustomerTooltip = new AonCustomerTooltip();
		
		bookingWithOutFeeMenu = new BookingWithOutFeeMenu();
		feeWithoutbookintMenu = new FeeWithoutbookintMenu();
		
		focusPanel = new FocusPanel();
		
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
		
		bookingPanel = new HTMLPanel("");
		bookingPanel.addStyleName(AON.CSS.aonFlexColumn());
		
		feeWithoutBookingPanel = new HTMLPanel("");
		feeWithoutBookingPanel.addStyleName(AON.CSS.aonFlexColumn());
		
		container.add(customerPanel);
		container.add(domainsPanel);
		
		container.add(bookingPanel);
		container.add(feeWithoutBookingPanel);
		
		mainContainer.add(messagePanel);
		mainContainer.add(container);
		
		scrollPanel.add(mainContainer);
		
		focusPanel.add(scrollPanel);
		
		this.add(focusPanel);
	}
	
	public void setBookingCustomer(Customer customer, List<DomainCompany> customerDomains) {
		this.customer = customer;
		this.customerDomains = customerDomains;
		
		this.customerPanel.clear();
		this.domainsPanel.clear();
		
		initializeCustomer();
		initializeDomains();
		
		this.bookingPanel.clear();
		this.feeWithoutBookingPanel.clear();
		
		initializeBooking();
		initializeFeeWithoutBooking();
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
		
		AonTableButton syncBtn = new AonTableButton("Sincronizar", AON.CSS.aonIconCloudSync());
		syncBtn.addClickHandler(e -> {
			syncCustomerDomains();
		});
		
		customerTable.setWidget(newRow, 10, syncBtn);
		
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
			syncBtn.addStyleName(AON.CSS.aonOddTableRow());
			
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
				syncDomains();
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
		
		for(DomainCompany domainCompany : customerDomains) {
			int newRow = domainTable.insertRow(domainTable.getRowCount());
			
			Label domainTypeLabel  = new Label(domainCompany.getDomain().getDomainType().getName());
			Label idLabel = new Label(domainCompany.getDomain().getId().toString());
			Label schemaLabel = new Label(domainCompany.getSchema());
			Label descriptionLabel = new Label(domainCompany.getDomain().getDescription());
			Label documentLabel = new Label(domainCompany.getCompany().getDocument());
			Label statusLabel = new Label(domainCompany.getDomain().isActive() ? "Activo" : "Inactivo");
			Label billableLabel = new Label(domainCompany.getDomain().getAonStatus().equals(AonStatus.BILLABLE) ? "SI" : "NO");
			Label expirationLabel = new Label(formatDate(domainCompany.getDomain().getCreationDate()));
			Label lastAccessLabel = new Label(formatDate(domainCompany.getDomain().getModificationDate()));
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
			
			AonTableButton urlBtn = new AonTableButton("Ir a", AON.CSS.aonIconGroup());
			urlBtn.addMouseOverHandler(e -> {
				openUserTooltip(domainCompany.getDomain().getName(), domainCompany.getDomain().getId(), e.getClientX(), e.getClientY());
			});
			focusPanel.addMouseOverHandler(e -> {
				aonCustomerTooltip.hide();
			});
			
			domainTable.setWidget(newRow, 10, urlBtn);
			
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
				urlBtn.addStyleName(AON.CSS.aonOddTableRow());
				
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
		
		domainsPanel.add(scrollPanel);
		
	}

	private String formatDate(Date date) {
		if(null == date) return "";
		return formatDate.format(date);
	}
	
	// -------- Booking
	
	private void initializeBooking() {
		bookingDeckPanel = new DeckPanel();
		createBookingGrid();
		createBookingWithOutFeeMessage();
		
		bookingDeckPanel.add(bookingGrid);
		bookingDeckPanel.add(bookingMessagePanel);
		
		bookingPanel.add(createBookingWithoutFeeToolbar());
		bookingPanel.add(bookingDeckPanel);
	}
	
	private void showBookingGrid() {
		bookingDeckPanel.showWidget(0);
	}
	
	private void showBookingMessage() {
		bookingDeckPanel.showWidget(1);
	}
	
	private void createBookingGrid() {
		bookingGrid = new Grid(0, 6);
		bookingGrid.clear();
		bookingGrid.setWidth("100%");

		int row = bookingGrid.insertRow(bookingGrid.getRowCount());

		Label concept = new Label("PRODUCTO");
		Label code = new Label("CODIGO");
		Label fee = new Label("CUOTA");
		Label conceptStatus = new Label("ESTADO");
		Label quantity = new Label("CANTIDAD");
		Label action = new Label("");
		
		concept.addStyleName(AON.CSS.aonHeaderTable());
		code.addStyleName(AON.CSS.aonHeaderTable());
		fee.addStyleName(AON.CSS.aonHeaderTable());
		conceptStatus.addStyleName(AON.CSS.aonHeaderTable());
		conceptStatus.addStyleName(AON.CSS.aonHeaderTable());
		quantity.addStyleName(AON.CSS.aonHeaderTable());
		action.addStyleName(AON.CSS.aonHeaderTable());

		bookingGrid.setWidget(row, 0, concept);
		bookingGrid.setWidget(row, 1, code);
		bookingGrid.setWidget(row, 2, fee);
		bookingGrid.setWidget(row, 3, conceptStatus);
		bookingGrid.setWidget(row, 4, quantity);
		bookingGrid.setWidget(row, 5, action);
		
		bookingGrid.getCellFormatter().addStyleName(row, 0, AON.CSS.aonHeaderSticky());
		bookingGrid.getCellFormatter().addStyleName(row, 1, AON.CSS.aonHeaderSticky());
		bookingGrid.getCellFormatter().addStyleName(row, 2, AON.CSS.aonHeaderSticky());
		bookingGrid.getCellFormatter().addStyleName(row, 3, AON.CSS.aonHeaderSticky());
		bookingGrid.getCellFormatter().addStyleName(row, 4, AON.CSS.aonHeaderSticky());
		bookingGrid.getCellFormatter().addStyleName(row, 5, AON.CSS.aonHeaderSticky());
		
		setColumnWidthBooking();
		fillBooking();
	}

	private void setColumnWidthBooking() {
		bookingGrid.getColumnFormatter().getElement(1).getStyle().setWidth(160, Unit.PX);
		bookingGrid.getColumnFormatter().getElement(2).getStyle().setWidth(50, Unit.PX);
		bookingGrid.getColumnFormatter().getElement(3).getStyle().setWidth(85, Unit.PX);
		bookingGrid.getColumnFormatter().getElement(4).getStyle().setWidth(160, Unit.PX);
		bookingGrid.getColumnFormatter().getElement(5).getStyle().setWidth(25, Unit.PX);
	}

	private void fillBooking() {
		CustomerFeeParams params = new CustomerFeeParams()
				.setDomain(options.getDomain())
				.setCustomer(this.customer.getName())
				.setOffset(0)
				.setLimit(100);
		
		SERVICE.getCustomerBookingCheckList(options.getDomainName(), options.getDomain(), options.getUser(), params,
				new AsyncCallback<LinkedList<BookingCheck>>() {

					@Override
					public void onFailure(Throwable caught) {
						AonMessagePanel.showError(messagePanel, "Error cargando panel de facturaci\u00f3n: " + caught.getMessage());
					}

					@Override
					public void onSuccess(LinkedList<BookingCheck> bookingCheckListDB) {
						if(bookingCheckListDB.isEmpty()) showBookingMessage();
						else fillBookingGrid(bookingCheckListDB);
					}
				});
	}

	private void fillBookingGrid(LinkedList<BookingCheck> bookingCheckListDB) {
		showBookingGrid();
		
		for(BookingCheck bookingCheck : bookingCheckListDB) {
			int row = bookingGrid.insertRow(bookingGrid.getRowCount());
			
			Label productLabel = new Label(bookingCheck.getItem().getProduct().getName());
			
			Label codeLabel = new Label(bookingCheck.getItem().getProduct().getCode());
			Label feeLabel = new Label(bookingCheck.hasFee() ? "SI" : "NO");
			if(!bookingCheck.hasFee() && !bookingCheck.getStatus().equals(RegistryItemStatus.INTERESTED)) {
				feeLabel.getElement().getStyle().setFontWeight(FontWeight.BOLD);
				feeLabel.getElement().getStyle().setColor("red");
			}
			
			Label productStatusLabel = new Label(getProductStatus(bookingCheck));
			Label quantityLabel = new Label(AonStringUtils.isBlank(bookingCheck.getQuantity()) ? "1.0" : bookingCheck.getQuantity());
			
			AonToolbarSmallButton actionBtn = new AonToolbarSmallButton("Acciones", AON.CSS.aonIconMoreVertical());
			actionBtn.addClickHandler(e -> {
				NativeEvent nativeEvent = e.getNativeEvent();
				bookingWithOutFeeMenu.setPopupPosition(nativeEvent.getClientX() - 150, nativeEvent.getClientY());
				bookingWithOutFeeMenu.show();
				bookingWithOutFeeMenu.setId(bookingCheck.getId());
				bookingWithOutFeeMenu.setItem(bookingCheck.getItem());
				bookingWithOutFeeMenu.setCustomer(bookingCheck.getCustomer());
				bookingWithOutFeeMenu.setBookingCheck(bookingCheck);
				bookingWithOutFeeMenu.setHasFee(bookingCheck.hasFee());
			});
			
			bookingGrid.setWidget(row, 0, productLabel);
			bookingGrid.setWidget(row, 1, codeLabel);
			bookingGrid.setWidget(row, 2, feeLabel);
			bookingGrid.setWidget(row, 3, productStatusLabel);
			bookingGrid.setWidget(row, 4, quantityLabel);
			bookingGrid.setWidget(row, 5, actionBtn);
			
			if (row % 2 == 0) {
				productLabel.addStyleName(AON.CSS.aonOddTableRow());
				codeLabel.addStyleName(AON.CSS.aonOddTableRow());
				feeLabel.addStyleName(AON.CSS.aonOddTableRow());
				productStatusLabel.addStyleName(AON.CSS.aonOddTableRow());
				quantityLabel.addStyleName(AON.CSS.aonOddTableRow());
				actionBtn.addStyleName(AON.CSS.aonOddTableRow());
				
				bookingGrid.getCellFormatter().addStyleName(row, 0, AON.CSS.aonOddTableRow());
				bookingGrid.getCellFormatter().addStyleName(row, 1, AON.CSS.aonOddTableRow());
				bookingGrid.getCellFormatter().addStyleName(row, 2, AON.CSS.aonOddTableRow());
				bookingGrid.getCellFormatter().addStyleName(row, 3, AON.CSS.aonOddTableRow());
				bookingGrid.getCellFormatter().addStyleName(row, 4, AON.CSS.aonOddTableRow());
				bookingGrid.getCellFormatter().addStyleName(row, 5, AON.CSS.aonOddTableRow());
			}
			
			bookingGrid.getCellFormatter().getElement(row, 2).getStyle().setTextAlign(TextAlign.CENTER);
			bookingGrid.getCellFormatter().getElement(row, 3).getStyle().setTextAlign(TextAlign.CENTER);
			bookingGrid.getCellFormatter().getElement(row, 5).getStyle().setTextAlign(TextAlign.CENTER);
				
			bookingGrid.getRowFormatter().getElement(row).getStyle().setHeight(25.00, Unit.PX);
		}

	}
	
	private void createBookingWithOutFeeMessage() {
		bookingMessagePanel = new HTMLPanel("");
		bookingMessagePanel.addStyleName(AON.CSS.aonDisplayFlexCenter());
		bookingMessagePanel.add(new Label("No existen contrataciones sin cuotas para este cliente"));
	}

	private AonToolbarSmall createBookingWithoutFeeToolbar() {
		AonToolbarSmall toolbar = new AonToolbarSmall("Contrataciones");
		
		toolbarBookingDiscBtn = new AonToolbarSmallButton("Desplegar Contrataciones", AON.CSS.aonIconDown());
		toolbarBookingDiscBtn.addClickHandler(e -> {
			isBookingOpen = !isBookingOpen;
			handleIcon(toolbarBookingDiscBtn, isBookingOpen);
			if(isBookingOpen) bookingDeckPanel.getElement().getStyle().clearDisplay();
			else bookingDeckPanel.getElement().getStyle().setDisplay(Display.NONE);
		});
		
		toolbar.add(toolbarBookingDiscBtn);
		
		return toolbar;
		
	}
	
	// -------- Fee Without Booking
	
	private void initializeFeeWithoutBooking() {
		feeWithoutBookingDeckPanel = new DeckPanel();
		createFeeWithoutBookingGrid();
		createFeeWithoutBookingMessage();
		
		feeWithoutBookingDeckPanel.add(feeWithoutBookingGrid);
		feeWithoutBookingDeckPanel.add(feeWithoutBookingMessagePanel);
		
		feeWithoutBookingPanel.add(createFeeWithoutBookingToolbar());
		feeWithoutBookingPanel.add(feeWithoutBookingDeckPanel);
	}
	
	private void showFeeWithoutBookingGrid() {
		feeWithoutBookingDeckPanel.showWidget(0);
	}
	
	private void showFeeWithoutBookingMessage() {
		feeWithoutBookingDeckPanel.showWidget(1);
	}
	
	private void createFeeWithoutBookingGrid() {
		feeWithoutBookingGrid = new Grid(0, 7);
		feeWithoutBookingGrid.clear();
		feeWithoutBookingGrid.setWidth("100%");

		int row = feeWithoutBookingGrid.insertRow(feeWithoutBookingGrid.getRowCount());

		Label concept = new Label("PRODUCTO");
		Label code = new Label("CODIGO");
		Label conceptStatus = new Label("ESTADO");
		Label quantity = new Label("CANTIDAD");
		Label startDate = new Label("F. DESDE");
		Label endDate = new Label("F. HASTA");
		Label action = new Label("");
		
		concept.addStyleName(AON.CSS.aonHeaderTable());
		code.addStyleName(AON.CSS.aonHeaderTable());
		conceptStatus.addStyleName(AON.CSS.aonHeaderTable());
		quantity.addStyleName(AON.CSS.aonHeaderTable());
		startDate.addStyleName(AON.CSS.aonHeaderTable());
		endDate.addStyleName(AON.CSS.aonHeaderTable());
		action.addStyleName(AON.CSS.aonHeaderTable());

		
		feeWithoutBookingGrid.setWidget(row, 0, concept);
		feeWithoutBookingGrid.setWidget(row, 1, code);
		feeWithoutBookingGrid.setWidget(row, 2, conceptStatus);
		feeWithoutBookingGrid.setWidget(row, 3, quantity);
		feeWithoutBookingGrid.setWidget(row, 4, startDate);
		feeWithoutBookingGrid.setWidget(row, 5, endDate);
		feeWithoutBookingGrid.setWidget(row, 6, action);
		
		feeWithoutBookingGrid.getCellFormatter().addStyleName(row, 0, AON.CSS.aonHeaderSticky());
		feeWithoutBookingGrid.getCellFormatter().addStyleName(row, 1, AON.CSS.aonHeaderSticky());
		feeWithoutBookingGrid.getCellFormatter().addStyleName(row, 2, AON.CSS.aonHeaderSticky());
		feeWithoutBookingGrid.getCellFormatter().addStyleName(row, 3, AON.CSS.aonHeaderSticky());
		feeWithoutBookingGrid.getCellFormatter().addStyleName(row, 4, AON.CSS.aonHeaderSticky());
		feeWithoutBookingGrid.getCellFormatter().addStyleName(row, 5, AON.CSS.aonHeaderSticky());
		feeWithoutBookingGrid.getCellFormatter().addStyleName(row, 6, AON.CSS.aonHeaderSticky());
		
		setColumnWidthFeeWithoutBooking();
		fillFeeWithoutBooking();
	}

	private void setColumnWidthFeeWithoutBooking() {
		feeWithoutBookingGrid.getColumnFormatter().getElement(1).getStyle().setWidth(160, Unit.PX);
		feeWithoutBookingGrid.getColumnFormatter().getElement(2).getStyle().setWidth(85, Unit.PX);
		feeWithoutBookingGrid.getColumnFormatter().getElement(3).getStyle().setWidth(160, Unit.PX);
		feeWithoutBookingGrid.getColumnFormatter().getElement(4).getStyle().setWidth(80, Unit.PX);
		feeWithoutBookingGrid.getColumnFormatter().getElement(5).getStyle().setWidth(80, Unit.PX);
		feeWithoutBookingGrid.getColumnFormatter().getElement(6).getStyle().setWidth(25, Unit.PX);
	}

	private void fillFeeWithoutBooking() {
		CustomerFeeParams params = new CustomerFeeParams()
				.setDomain(options.getDomain())
				.setCustomer(this.customer.getName())
				.setOffset(0)
				.setLimit(100);
		
		SERVICE.getFeeWithoutBookingList(options.getDomainName(), options.getDomain(), options.getUser(), params,
				new AsyncCallback<LinkedList<BookingCheck>>() {

					@Override
					public void onFailure(Throwable caught) {
						AonMessagePanel.showError(messagePanel, "Error cargando panel de facturaci\u00f3n: " + caught.getMessage());
					}

					@Override
					public void onSuccess(LinkedList<BookingCheck> bookingCheckListDB) {
						if(bookingCheckListDB.isEmpty()) showFeeWithoutBookingMessage();
						else fillFeeWithoutBookingGrid(bookingCheckListDB);
					}
				});
	}

	private void fillFeeWithoutBookingGrid(LinkedList<BookingCheck> bookingCheckListDB) {
		showFeeWithoutBookingGrid();
		
		for(BookingCheck bookingCheck : bookingCheckListDB) {
			int row = feeWithoutBookingGrid.insertRow(feeWithoutBookingGrid.getRowCount());

			Label productLabel = new Label(bookingCheck.getItem().getProduct().getName());
			Label codeLabel = new Label(bookingCheck.getItem().getProduct().getCode());
			Label productStatusLabel = new Label(getFeeStatus(bookingCheck));
			Label quantityLabel = new Label(AonStringUtils.isBlank(bookingCheck.getQuantity()) ? "1.0" : bookingCheck.getQuantity());
			Label startDateLabel = new Label(formatDate(bookingCheck.getStartDate()));
			Label endDateLabel = new Label(formatDate(bookingCheck.getEndDate()));
			
			AonToolbarSmallButton actionBtn = new AonToolbarSmallButton("Acciones", AON.CSS.aonIconMoreVertical());
			actionBtn.addClickHandler(e -> {
				NativeEvent nativeEvent = e.getNativeEvent();
				feeWithoutbookintMenu.setPopupPosition(nativeEvent.getClientX() - 150, nativeEvent.getClientY());
				feeWithoutbookintMenu.show();
				feeWithoutbookintMenu.setId(bookingCheck.getId());
				feeWithoutbookintMenu.setItem(bookingCheck.getItem());
				feeWithoutbookintMenu.setCustomer(bookingCheck.getCustomer());
			});
			
			feeWithoutBookingGrid.setWidget(row, 0, productLabel);
			feeWithoutBookingGrid.setWidget(row, 1, codeLabel);
			feeWithoutBookingGrid.setWidget(row, 2, productStatusLabel);
			feeWithoutBookingGrid.setWidget(row, 3, quantityLabel);
			feeWithoutBookingGrid.setWidget(row, 4, startDateLabel);
			feeWithoutBookingGrid.setWidget(row, 5, endDateLabel);
			feeWithoutBookingGrid.setWidget(row, 6, actionBtn);
			
			if (row % 2 == 0) {
				productLabel.addStyleName(AON.CSS.aonOddTableRow());
				codeLabel.addStyleName(AON.CSS.aonOddTableRow());
				productStatusLabel.addStyleName(AON.CSS.aonOddTableRow());
				quantityLabel.addStyleName(AON.CSS.aonOddTableRow());
				startDateLabel.addStyleName(AON.CSS.aonOddTableRow());
				endDateLabel.addStyleName(AON.CSS.aonOddTableRow());
				actionBtn.addStyleName(AON.CSS.aonOddTableRow());
				
				feeWithoutBookingGrid.getCellFormatter().addStyleName(row, 0, AON.CSS.aonOddTableRow());
				feeWithoutBookingGrid.getCellFormatter().addStyleName(row, 1, AON.CSS.aonOddTableRow());
				feeWithoutBookingGrid.getCellFormatter().addStyleName(row, 2, AON.CSS.aonOddTableRow());
				feeWithoutBookingGrid.getCellFormatter().addStyleName(row, 3, AON.CSS.aonOddTableRow());
				feeWithoutBookingGrid.getCellFormatter().addStyleName(row, 4, AON.CSS.aonOddTableRow());
				feeWithoutBookingGrid.getCellFormatter().addStyleName(row, 5, AON.CSS.aonOddTableRow());
				feeWithoutBookingGrid.getCellFormatter().addStyleName(row, 6, AON.CSS.aonOddTableRow());
			}
			
			feeWithoutBookingGrid.getCellFormatter().getElement(row, 2).getStyle().setTextAlign(TextAlign.CENTER);
			feeWithoutBookingGrid.getCellFormatter().getElement(row, 4).getStyle().setTextAlign(TextAlign.CENTER);
			feeWithoutBookingGrid.getCellFormatter().getElement(row, 5).getStyle().setTextAlign(TextAlign.CENTER);
			feeWithoutBookingGrid.getCellFormatter().getElement(row, 6).getStyle().setTextAlign(TextAlign.CENTER);
				
			feeWithoutBookingGrid.getRowFormatter().getElement(row).getStyle().setHeight(25.00, Unit.PX);
		}

	}
	
	private void createFeeWithoutBookingMessage() {
		feeWithoutBookingMessagePanel = new HTMLPanel("");
		feeWithoutBookingMessagePanel.addStyleName(AON.CSS.aonDisplayFlexCenter());
		feeWithoutBookingMessagePanel.add(new Label("No existen cuotas sin contrataciones para este cliente"));
	}

	private AonToolbarSmall createFeeWithoutBookingToolbar() {
		AonToolbarSmall toolbar = new AonToolbarSmall("Cuotas sin Contrataciones");
		
		toolbarFeeWithoutBookingDiscBtn = new AonToolbarSmallButton("Desplegar Cuotas sin Contrataciones", AON.CSS.aonIconDown());
		toolbarFeeWithoutBookingDiscBtn.addClickHandler(e -> {
			isFeeWithoutBookingOpen = !isFeeWithoutBookingOpen;
			handleIcon(toolbarFeeWithoutBookingDiscBtn, isFeeWithoutBookingOpen);
			if(isFeeWithoutBookingOpen) feeWithoutBookingDeckPanel.getElement().getStyle().clearDisplay();
			else feeWithoutBookingDeckPanel.getElement().getStyle().setDisplay(Display.NONE);
		});
		
		toolbar.add(toolbarFeeWithoutBookingDiscBtn);
		
		return toolbar;
		
	}
	
	// -------- Auxiliar Methods
	
	private String getProductStatus(BookingCheck bookingCheck) {
		if(bookingCheck.getStatus().equals(RegistryItemStatus.ACTIVE)) return "Facturable";
		else if(bookingCheck.getStatus().equals(RegistryItemStatus.INTERESTED)) return "No Facturable";
		else if(bookingCheck.getStatus().equals(RegistryItemStatus.REFUSED)) return " No Contratado";
		else if(bookingCheck.getStatus().equals(RegistryItemStatus.INACTIVE)) return "Inactivo";
		else return "";
	}
	
	private String getFeeStatus(BookingCheck bookingCheck) {
		return 	bookingCheck.getEndDate() == null || 
				(new Date().before(bookingCheck.getEndDate()) && 
				bookingCheck.getEndDate().after(bookingCheck.getStartDate())) 
				? "Facturable" : "Expirado";
	}
	
	private void handleIcon(AonToolbarSmallButton button, boolean open) {
		if (open) {
			button.removeStyleName(AON.CSS.aonIconLeft());
			button.addStyleName(AON.CSS.aonIconDown());

			if (button.equals(toolbarBookingDiscBtn))
				button.setTitle("Colapsar Contrataciones");
			if (button.equals(toolbarFeeWithoutBookingDiscBtn))
				button.setTitle("Colapsar Cuotas sin Contrataci\u00f3n");
		} else {
			button.removeStyleName(AON.CSS.aonIconDown());
			button.addStyleName(AON.CSS.aonIconLeft());

			if (button.equals(toolbarBookingDiscBtn))
				button.setTitle("Desplegar Contrataciones");
			if (button.equals(toolbarFeeWithoutBookingDiscBtn))
				button.setTitle("Desplegar Cuotas sin Contrataci\u00f3n");
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
				// Create the base URL
				String baseUrl = "/ms/api/domain/remote/";

				// Create a URL builder and add query parameters
				UrlBuilder urlBuilder = new UrlBuilder();
				urlBuilder.setProtocol(Window.Location.getProtocol()); // Use the current protocol
				urlBuilder.setHost("aon.solutions"); 
				urlBuilder.setPath(baseUrl);
				
				// Create the request builder with the complete URL
				RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.PUT, urlBuilder.buildString());
				requestBuilder.setHeader("session_id", "AONd95770f269e711eb94390242ac130002");
				
				requestBuilder.setHeader("domain_name", url);
				requestBuilder.setHeader("domain_id", domainId.toString());
				
				try {
				    // Send the request
				    requestBuilder.sendRequest(null, new RequestCallback() {
				        public void onResponseReceived(Request request, Response response) {
				            if (response.getStatusCode() == 200) {
				            	Window.open("https://" + url, "_blank", "");
				            } else {
				            	AonMessagePanel.showError(messagePanel, response.getText());
				            }
				        }

						public void onError(Request request, Throwable exception) {
							AonMessagePanel.showError(messagePanel, exception.getMessage());
				        }
				    });
				} catch (RequestException e) {
					AonMessagePanel.showError(messagePanel, e.getMessage());
				}	
			}
		});
	}
	
	private void openUserTooltip(String domainName, Integer domainId, int clientX, int clientY) {
		// Create the base URL
		String baseUrl = "/ms/api/user/";

		// Create a URL builder and add query parameters
		UrlBuilder urlBuilder = new UrlBuilder();
		urlBuilder.setProtocol(Window.Location.getProtocol()); // Use the current protocol
		urlBuilder.setHost("aon.solutions"); 
		urlBuilder.setPath(baseUrl);
		
		// Create the request builder with the complete URL
		RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.GET, urlBuilder.buildString());
		requestBuilder.setHeader("session_id", "AONd95770f269e711eb94390242ac130002");
		
		requestBuilder.setHeader("domain_name", domainName);
		requestBuilder.setHeader("domain_id", domainId.toString());
		
		try {
		    // Send the request
		    requestBuilder.sendRequest(null, new RequestCallback() {
		        public void onResponseReceived(Request request, Response response) {
		            if (response.getStatusCode() == 200) {
		            	String responseBody = response.getText();
		            	List<User> users = DomainCompanyJSON.parseUsersJSONArr(responseBody);
		            	aonCustomerTooltip.setUsers(users);
						aonCustomerTooltip.showTooltip(clientX, clientY);
		            } else {
		            	AonMessagePanel.showError(messagePanel, response.getText());
		            }
		        }

				public void onError(Request request, Throwable exception) {
					AonMessagePanel.showError(messagePanel, exception.getMessage());
		        }
		    });
		} catch (RequestException e) {
			AonMessagePanel.showError(messagePanel, e.getMessage());
		}
	}
	
	private void syncDomains() {
		for(DomainCompany domainCompany : this.customerDomains) {
			AonMessagePanel.showLoading(messagePanel, "Obteniendo contrataci\u00f3n para el dominio " + domainCompany.getDomain().getDescription() + " ...");
			
			// Create the base URL
			String baseUrl = "/ms/api/booking/";

			// Create a URL builder and add query parameters
			UrlBuilder urlBuilder = new UrlBuilder();
			urlBuilder.setProtocol(Window.Location.getProtocol()); // Use the current protocol
			urlBuilder.setHost("aon.solutions"); 
			urlBuilder.setPath(baseUrl);
			
			urlBuilder.setParameter("domainName", domainCompany.getDomain().getName());
			urlBuilder.setParameter("domainId", domainCompany.getDomain().getId().toString());
			
			// Create the request builder with the complete URL
			RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.GET, urlBuilder.buildString());
			requestBuilder.setHeader("session_id", "AONd95770f269e711eb94390242ac130002");
			
			try {
			    // Send the request
			    requestBuilder.sendRequest(null, new RequestCallback() {
			        public void onResponseReceived(Request request, Response response) {
			            if (response.getStatusCode() == 200) {

			                String bookingJSON = response.getText();
			                AonMessagePanel.hideMessage(messagePanel);
			                getDomain(domainCompany, bookingJSON);
			                
			            } else {
			            	AonMessagePanel.showError(messagePanel, response.getText());
			            }
			        }

					public void onError(Request request, Throwable exception) {
						AonMessagePanel.showError(messagePanel, exception.getMessage());
			        }
			    });
			} catch (RequestException e) {
				AonMessagePanel.showError(messagePanel, e.getMessage());
			}
		}
	}
	
	private void getDomain(DomainCompany domainCompany, String bookingJSON) {
		AonMessagePanel.showLoading(messagePanel, "Obteniendo informaci\u00f3n del dominio " + domainCompany.getDomain().getDescription() + " ...");
		
		// Create the base URL
		String baseUrl = "/ms/api/domain/";

		// Create a URL builder and add query parameters
		UrlBuilder urlBuilder = new UrlBuilder();
		urlBuilder.setProtocol(Window.Location.getProtocol()); // Use the current protocol
		urlBuilder.setHost("aon.solutions"); 
		urlBuilder.setPath(baseUrl);
		
		urlBuilder.setParameter("domainId", domainCompany.getDomain().getId().toString());
		
		// Create the request builder with the complete URL
		RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.GET, urlBuilder.buildString());
		requestBuilder.setHeader("session_id", "AONd95770f269e711eb94390242ac130002");
		
		try {
		    // Send the request
		    requestBuilder.sendRequest(null, new RequestCallback() {
		        public void onResponseReceived(Request request, Response response) {
		            if (response.getStatusCode() == 200) {

		                String domainJSON = response.getText();
		                AonMessagePanel.hideMessage(messagePanel);
		                updateBookingRitems(domainCompany, domainJSON, bookingJSON);
		                
		            } else {
		            	AonMessagePanel.showError(messagePanel, response.getText());
		            }
		        }

				public void onError(Request request, Throwable exception) {
					AonMessagePanel.showError(messagePanel, exception.getMessage());
		        }
		    });
		} catch (RequestException e) {
			AonMessagePanel.showError(messagePanel, e.getMessage());
		}
	}
	
	private void updateBookingRitems(DomainCompany domainCompany, String domainJSON, String bookingJSON) {
		AonMessagePanel.showLoading(messagePanel, "Sincronizando contrataci\u00f3n para el dominio " + domainCompany.getDomain().getDescription() + " ...");
		
		// Create the base URL
		String baseUrl = "/ms/api/domain/booking/";

		// Create a URL builder and add query parameters
		UrlBuilder urlBuilder = new UrlBuilder();
		urlBuilder.setProtocol(Window.Location.getProtocol()); // Use the current protocol
		urlBuilder.setHost(Window.Location.getHost()); 
		urlBuilder.setPath(baseUrl);
		
		urlBuilder.setParameter("domain", domainJSON);
		urlBuilder.setParameter("booking", bookingJSON);
		
		// Create the request builder with the complete URL
		RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.PUT, urlBuilder.buildString());
		requestBuilder.setHeader("session_id", "AONd95770f269e711eb94390242ac130002");
		
		try {
		    // Send the request
		    requestBuilder.sendRequest(null, new RequestCallback() {
		        public void onResponseReceived(Request request, Response response) {
		            if (response.getStatusCode() == 200) {
		            	AonMessagePanel.showSuccess(messagePanel, "La sincronizaci\u00f3n del dominio " + domainCompany.getDomain().getDescription() + " se ha realizado correctamente");
		            } else {
		            	AonMessagePanel.showError(messagePanel, response.getText());
		            }
		        }

				public void onError(Request request, Throwable exception) {
					AonMessagePanel.showError(messagePanel, exception.getMessage());
		        }
		    });
		} catch (RequestException e) {
			AonMessagePanel.showError(messagePanel, e.getMessage());
		}
	}
	
}
