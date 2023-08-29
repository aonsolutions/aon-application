package com.esferalia.aon.gwt.fiscal.client.registry;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.TreeMap;
import java.util.function.Consumer;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.gwt.common.client.CommonServiceAsync;
import com.esferalia.aon.gwt.common.client.CommonServiceAsyncDecorator;
import com.esferalia.aon.gwt.common.client.RegistryService;
import com.esferalia.aon.gwt.common.client.RegistryServiceAsync;
import com.esferalia.aon.gwt.common.client.RegistryServiceAsyncDecorator;
import com.esferalia.aon.gwt.common.client.RootLayoutPanel;
import com.esferalia.aon.gwt.common.client.json.DomainCompanyJSON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonContextMenu;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDateBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog.AonAcceptDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDomainSelectionDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessagePanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbar;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarSmallButton;
import com.esferalia.aon.gwt.fiscal.client.MainEntryPoint;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.BookingCheck;
import com.esferalia.aon.occam.api.model.Customer;
import com.esferalia.aon.occam.api.model.DomainCompany;
import com.esferalia.aon.occam.api.model.fee.Fee;
import com.esferalia.aon.occam.api.model.product.OldItem;
import com.esferalia.aon.occam.api.model.registry.CustomerFeeParams;
import com.esferalia.aon.occam.api.model.registry.RegistryItemStatus;
import com.esferalia.aon.occam.api.model.registry.Segment;
import com.esferalia.aon.watson.mutable.MutableInt;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.NativeEvent;
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
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.Widget;

public class BookingPanel extends MainEntryPoint {
	
	// ------- FeeWithOutBooking
	
	class RemoveCustomerFeeCommand implements ScheduledCommand {

		@Override
		public void execute() {
			removeCustomerFee();
		}

	}
	
	class CreateBookingCommand implements ScheduledCommand {

		@Override
		public void execute() {
			createBooking();
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
	
	// ------- BookingWithOutFee
	
	class CreateCustomerFeeCommand implements ScheduledCommand {

		@Override
		public void execute() {
			createCustomerFee(bookingWithOutFeeMenu.getItem(), bookingWithOutFeeMenu.getCustomer());
		}

	}
	
	class UpdateBookingCommand implements ScheduledCommand {

		@Override
		public void execute() {
			updateBooking();
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

	}

	// Services
	private static RegistryServiceAsync SERVICE;
	private static CommonServiceAsync COMMON_SERVICE;

	// Options Config
	private RegistryModuleOptions options;
	
	// Context Menu
	private FeeWithoutbookintMenu feeWithoutbookintMenu;
	private BookingWithOutFeeMenu bookingWithOutFeeMenu;

	// Content
	private DockLayoutPanel dockLayoutPanel;

	private AonToolbar toolbar;
	private ListBox bookingCheckType;
	private AonToolbarButton syncDomains;
	private AonToolbarButton backBtn;
	
	private DeckPanel mainDeckPanel;
	private BookingCustomer bookingCustomer;
	
	// Filter
	private boolean expandFilter = false;
	
	private Integer minYear;
	private Integer maxYear;
	
	private HTMLPanel periodItemPanel;
	private ListBox monthListBox;
	private ListBox yearListBox;
	private SuggestBox customerSuggestBox;
	private ListBox customerStatusListBox;
	private ListBox segmentListBox;
	private SuggestBox conceptSuggestBox;
	private ListBox conceptStatusListBox;
	private ListBox startCompareLB;
	private AonDateBox startDateBox;
	private ListBox endCompareLB;
	private AonDateBox endDateBox;
	
	private HTMLPanel filterExpandPanel;
	private ListBox domainTypeListBox;
	private ListBox domainStatusListBox;
	
	// BookingCheck Table
	private HTMLPanel container;
	private HTMLPanel messagePanel;
	private DeckPanel deckPanel;
	private ScrollPanel scrollPanel;
	private Grid bookingCheckTable;
	
	// Variables
	private LinkedList<BookingCheck> bookingCheckList = new LinkedList<>();
	
	// Search Variables
	private CustomerFeeParams params;
	private Date billingDate;
	
	private Map<String, Customer> customerSuggestions = new TreeMap<>();
	
	private Map<String, OldItem> productSuggestions = new TreeMap<>();
	
	private int limit = 100;
	private MutableInt offset = new MutableInt(0);
	private MutableInt moreData = new MutableInt(0);
	private MutableInt searchEnabled = new MutableInt(0); 
	private int lastScrollPos = 0;
	
	private DateTimeFormat formatDate = DateTimeFormat.getFormat("dd/MM/yyyy");
	private DateTimeFormat formatFullDate = DateTimeFormat.getFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
	
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

		CommonServiceAsync commonServiceRaw = GWT.create(CommonService.class);
		COMMON_SERVICE = new CommonServiceAsyncDecorator(commonServiceRaw);
		
		feeWithoutbookintMenu = new FeeWithoutbookintMenu();
		bookingWithOutFeeMenu = new BookingWithOutFeeMenu();

		dockLayoutPanel = new DockLayoutPanel(Unit.PX);
		opt.getParentWidget().add(dockLayoutPanel);
		
		dockLayoutPanel.clear();
		
		mainDeckPanel = new DeckPanel();
		
		bookingCustomer = new BookingCustomer(SERVICE, opt);

		if (opt.getConfiguration() == null) {
			COMMON_SERVICE.getAonConfiguration(opt.getDomainName(), opt.getDomain(), opt.getUser(),
					new AsyncCallback<AonConfiguration>() {
						@Override
						public void onSuccess(AonConfiguration result) {
							opt.setConfiguration(result);
							loadModule(opt);
						}

						@Override
						public void onFailure(Throwable caught) {
							dockLayoutPanel.add(new Label(AON.MSG.loadError( " [Interno: " + caught.getMessage() + "]")));
						}
					});
		} else {
			loadModule(opt);
		}
	}

	private void loadModule(final RegistryModuleOptions opt) {
		createToolbar();
		dockLayoutPanel.addNorth(toolbar, 50);
		
		container = new HTMLPanel("");
		container.clear();
		container.addStyleName(AON.CSS.aonFlexColumn());
		
		mainDeckPanel.add(container);
		mainDeckPanel.add(bookingCustomer);
		dockLayoutPanel.add(mainDeckPanel);

		messagePanel = new HTMLPanel("");
		container.add(messagePanel);
		
		createFilterPanel(opt, new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {
				dockLayoutPanel.add(new Label(AON.MSG.loadError( " [Interno: " + caught.getMessage() + "]")));
			}

			@Override
			public void onSuccess(Void result) {
				deckPanel = new DeckPanel();
				deckPanel.getElement().getStyle().setProperty("margin", "0 1rem");
				container.add(deckPanel);
				
				scrollPanel = new ScrollPanel();
				scrollPanel.setHeight((Window.getClientHeight() - 230) + "px");
				scrollPanel.addScrollHandler(e -> {
					// ------------------------------------ Ignore scroll up.
					int oldScrollPos = lastScrollPos;
					lastScrollPos = scrollPanel.getVerticalScrollPosition();
					if (oldScrollPos >= lastScrollPos) {
						return;
					}
					// -----------------------------------------------------
					if (isSearchEnabled()) {
						int maxScrollTop = scrollPanel.getWidget().getOffsetHeight() - scrollPanel.getOffsetHeight();
						if (lastScrollPos >= maxScrollTop) {
							disableSearch();
							searchFees();
						}
					}
				});
				
				initializeDeckPanel();

				AonMessagePanel.showLoading(messagePanel, "Cargando panel de facturaci\u00f3n ...");
				resetFilter();
				onSearchFees();
				AonMessagePanel.hideMessage(messagePanel);
			}
			
		});
		
		showBookingList();
	}
	
	private void showBookingList() {
		bookingCheckType.setVisible(true);
		syncDomains.setVisible(true);
		backBtn.setVisible(false);
		
		toolbar.setTitle("Panel Contrataci\u00f3n");
		
		mainDeckPanel.showWidget(0);
	}
	
	private void showBookingCustomer(String customerName) {
		bookingCheckType.setVisible(false);
		syncDomains.setVisible(false);
		backBtn.setVisible(true);
		
		toolbar.setTitle(customerName);
		
		mainDeckPanel.showWidget(1);
	}

	private void createFilterPanel(final RegistryModuleOptions opt, AsyncCallback<Void> endCallback) {
		HTMLPanel filterContentPanel = new HTMLPanel("");
		filterContentPanel.addStyleName(AON.CSS.aonFlexBetween());
		filterContentPanel.addStyleName(AON.CSS.aonFilterPanel());
		filterContentPanel.getElement().getStyle().setProperty("margin", "0 1rem");
		
		HTMLPanel filterLeftPanel = new HTMLPanel("");
		filterLeftPanel.addStyleName(AON.CSS.aonFlexColumn());
		
		HTMLPanel filterDefaultPanel = new HTMLPanel("");
		filterDefaultPanel.addStyleName(AON.CSS.aonFlexWrap());
		
		// Period
		periodItemPanel = new HTMLPanel("");
		periodItemPanel.addStyleName(AON.CSS.aonItemFlex());

		Label periodLabel = new Label("F. Facturaci\u00f3n");
		periodLabel.getElement().getStyle().setFontWeight(FontWeight.BOLD);
		monthListBox = createMonthListBox();
		monthListBox.addChangeHandler(e -> onSearchFees());
		
		createYearListBox(lb -> {
			yearListBox = lb;
			yearListBox.addChangeHandler(e -> onSearchFees());
			periodItemPanel.add(yearListBox);
			endCallback.onSuccess(null);
		});
		
		periodItemPanel.add(periodLabel);
		periodItemPanel.add(monthListBox);
		
		periodItemPanel.setVisible(false);

		filterDefaultPanel.add(periodItemPanel);

		// Customer
		HTMLPanel customerItemPanel = new HTMLPanel("");
		customerItemPanel.addStyleName(AON.CSS.aonItemFlex());

		Label customerLabel = new Label("Cliente");
		customerLabel.getElement().getStyle().setFontWeight(FontWeight.BOLD);
		createCustomerSuggestBox();
		createCustomerStatusListBox();

		customerItemPanel.add(customerLabel);
		customerItemPanel.add(customerSuggestBox);
		customerItemPanel.add(customerStatusListBox);

		filterDefaultPanel.add(customerItemPanel);
		
		// Segment
		if (opt.getConfiguration().hasSegments()) {
			HTMLPanel segmentItemPanel = new HTMLPanel("");
			segmentItemPanel.addStyleName(AON.CSS.aonItemFlex());

			Label segmentLabel = new Label("Segmento");
			segmentLabel.getElement().getStyle().setFontWeight(FontWeight.BOLD);
			
			segmentListBox = new ListBox();
			segmentListBox.setHeight("2em");
			segmentListBox.getElement().getStyle().setProperty("padding", "0 5px");
			segmentListBox.addItem("-", "");
			segmentListBox.addItem("SIN SEGMENTO", "-1");
			segmentListBox.setSelectedIndex(0);
			
			for (Segment ea : opt.getConfiguration().getSegments()) {
				segmentListBox.addItem(ea.getName(), AonNumberUtils.toString( ea.getId()));
			}
			segmentListBox.addChangeHandler(event -> onSearchFees());
			
			segmentItemPanel.add(segmentLabel);
			segmentItemPanel.add(segmentListBox);
			
			filterDefaultPanel.add(segmentItemPanel);
		}
		
		// Product
		HTMLPanel conceptItemPanel = new HTMLPanel("");
		conceptItemPanel.addStyleName(AON.CSS.aonItemFlex());

		Label conceptLabel = new Label("Producto");
		conceptLabel.getElement().getStyle().setFontWeight(FontWeight.BOLD);
		createConceptSuggestBox();
		createConceptStatusListBox();

		conceptItemPanel.add(conceptLabel);
		conceptItemPanel.add(conceptSuggestBox);
		
		if(isBookingWithOutFee())
			conceptItemPanel.add(conceptStatusListBox);

		filterDefaultPanel.add(conceptItemPanel);
		
		// StartDate
		HTMLPanel startDateItemPanel = new HTMLPanel("");
		startDateItemPanel.addStyleName(AON.CSS.aonItemFlex());

		Label startDateLabel = new Label("F. Inicio");
		startDateLabel.getElement().getStyle().setFontWeight(FontWeight.BOLD);
		
		startCompareLB = createCompareListBox();
		startCompareLB.addChangeHandler(e -> {
			if(null != startDateBox.getValue()) onSearchFees();
		});
		
		startDateBox = new AonDateBox();
		startDateBox.getElement().getStyle().setTextAlign(TextAlign.CENTER);
		startDateBox.addStyleName("gwt-TextBox");
		setInputStyle(startDateBox);
		startDateBox.addValueChangeHandler(e -> onSearchFees());

		startDateItemPanel.add(startDateLabel);
		startDateItemPanel.add(startCompareLB);
		startDateItemPanel.add(startDateBox);

		filterDefaultPanel.add(startDateItemPanel);
		
		// EndDate
		HTMLPanel endDateItemPanel = new HTMLPanel("");
		endDateItemPanel.addStyleName(AON.CSS.aonItemFlex());

		Label endDateLabel = new Label("F. Fin");
		endDateLabel.getElement().getStyle().setFontWeight(FontWeight.BOLD);
		
		endCompareLB = createCompareListBox();
		endCompareLB.addChangeHandler(e -> {
			if(null != endDateBox.getValue()) onSearchFees();
		});
		
		endDateBox = new AonDateBox();
		endDateBox.getElement().getStyle().setTextAlign(TextAlign.CENTER);
		endDateBox.addStyleName("gwt-TextBox");
		setInputStyle(endDateBox);
		endDateBox.addValueChangeHandler(e -> onSearchFees());

		endDateItemPanel.add(endDateLabel);
		endDateItemPanel.add(endCompareLB);
		endDateItemPanel.add(endDateBox);

		filterDefaultPanel.add(endDateItemPanel);
		
		// Filter Expand Panel
		filterExpandPanel = new HTMLPanel("");
		filterExpandPanel.addStyleName(AON.CSS.aonFlexWrap());
		filterExpandPanel.getElement().getStyle().setDisplay(Display.NONE);
		
		// Domain
		Label domainLabel = new Label("Dominio");
		domainLabel.getElement().getStyle().setFontWeight(FontWeight.BOLD);
		createDomainTypeListBox();
		createDomainStatusListBox();
		
		filterExpandPanel.add(domainLabel);
		filterExpandPanel.add(domainTypeListBox);
		filterExpandPanel.add(domainStatusListBox);
		
		// Right buttons
		HTMLPanel filterRightPanel = new HTMLPanel("");
		filterRightPanel.addStyleName(AON.CSS.aonFlexWrap());
		filterRightPanel.getElement().getStyle().setProperty("height", "100%");
		filterRightPanel.getElement().getStyle().setProperty("align-items", "flex-start");
		filterRightPanel.getElement().getStyle().setProperty("flex-wrap", "nowrap");
		
		AonToolbarSmallButton expandFilterBtn = new AonToolbarSmallButton("Mas filtros", AON.CSS.aonIconTune());
		expandFilterBtn.addClickHandler(e -> {
			expandFilter = !expandFilter;
			if(expandFilter) {
				filterExpandPanel.getElement().getStyle().clearDisplay();
			} else {
				filterExpandPanel.getElement().getStyle().setDisplay(Display.NONE);
			}
		});
		
		AonToolbarSmallButton resetBtn = new AonToolbarSmallButton("Borrar filtros", AON.CSS.aonIconClear());
		resetBtn.addClickHandler(e -> {
			resetFilter();
			bookingCheckList.clear();
			resetFeeTable();
			enableMoreData();
			offset.setValue(0);
			showInitialMessage();
			onSearchFees();
		});
		
		filterLeftPanel.add(filterDefaultPanel);
		filterLeftPanel.add(filterExpandPanel);
		
		filterRightPanel.add(expandFilterBtn);
		filterRightPanel.add(resetBtn);
		
		filterContentPanel.add(filterLeftPanel);
		filterContentPanel.add(filterRightPanel);

		container.add(filterContentPanel);
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

	private void createYearListBox(Consumer<ListBox> consumer) {
		SERVICE.getMinMaxCustomerFeeYear(options.getDomainName(), options.getDomain(), options.getUser(), new AsyncCallback<Map<Integer, Integer>>() {

			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onSuccess(Map<Integer, Integer> minMaxYear) {
				Optional<Entry<Integer, Integer>> firstEntry = minMaxYear.entrySet().stream().findFirst();
				
				ListBox lb = new ListBox();
				lb.setHeight("2em");
				lb.getElement().getStyle().setProperty("padding", "0 5px");
				lb.addItem("-", "");
				
				if(firstEntry.isPresent()) {
					Integer minYearEntry = firstEntry.get().getKey();
					Integer maxYearEntry = firstEntry.get().getValue();
					
					minYear = minYearEntry;
					maxYear = maxYearEntry;
					
					Integer itYear = maxYear;
					
					while(itYear >= minYear) {
						lb.addItem(itYear.toString(), (itYear - 1900) + "");
						itYear--;
					}
				} 
				
				consumer.accept(lb);
			}
			
		});
	}
	
	private ListBox createCompareListBox() {
		ListBox lb = new ListBox();
		lb.setHeight("2em");
		lb.getElement().getStyle().setProperty("padding", "0 5px");

		lb.addItem("=", "0");
		lb.addItem("\u2264", "1");
		lb.addItem("\u2265", "2");
		
		return lb;
	}
	
	// ------- CUSTOMER ---------
	
	private void createCustomerSuggestBox() {
		customerSuggestBox = new SuggestBox();
		customerSuggestBox.setWidth("225px");
		customerSuggestBox.setHeight("2em");
		customerSuggestBox.getElement().getStyle().setProperty("padding", "0 5px");
		customerSuggestBox.setAutoSelectEnabled(false);
		customerSuggestBox.getElement().setPropertyString("placeholder", "Clientes: busque por nombre, nif o alias");
		
		customerSuggestBox.addSelectionHandler(e -> {
			customerSuggestBox.hideSuggestionList();
			onSearchFees();
		});
		
		customerSuggestBox.addKeyUpHandler(e -> {
			String customerQuery = customerSuggestBox.getValue();
			if(e.isControlKeyDown() && e.getNativeKeyCode() == 32) {
				customerSuggestBox.setValue("");
				customerQuery = null;
				getCustomersSuggestion(customerQuery);
			} else if(AonStringUtils.isNotBlank(customerQuery) && customerQuery.length() > 3)
				getCustomersSuggestion(customerQuery);
		});
		
		customerSuggestBox.getTextBox().addBlurHandler(e -> {
			String customerQuery = customerSuggestBox.getValue();
			if(AonStringUtils.isBlank(customerQuery)) {
				onSearchFees();
			}
		});
		
	}
	
	private void createCustomerStatusListBox() {
		customerStatusListBox = new ListBox();
		customerStatusListBox.setHeight("2em");
		customerStatusListBox.getElement().getStyle().setProperty("padding", "0 5px");
		customerStatusListBox.addItem("-", "");
		customerStatusListBox.addItem("Activo", "0");
		customerStatusListBox.addItem("Inactivo", "1");
		customerStatusListBox.addItem("Bloqueado", "2");
		customerStatusListBox.addChangeHandler(e -> onSearchFees());
	}
	
	private void getCustomersSuggestion(String customerQuery) {
		SERVICE.getCustomersSuggestion(options.getDomainName(), options.getDomain(), options.getUser(), customerQuery, new AsyncCallback<Map<String, Customer>>() {
			
			@Override
			public void onSuccess(Map<String, Customer> customerSuggestionsDB) {
				customerSuggestions = customerSuggestionsDB;
				
				MultiWordSuggestOracle orclSb = (MultiWordSuggestOracle) customerSuggestBox.getSuggestOracle();
				orclSb.clear();
				orclSb.addAll(customerSuggestions.keySet());
				orclSb.setDefaultSuggestionsFromText(customerSuggestions.keySet());
				customerSuggestBox.showSuggestionList();
			}
			
			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub	
			}
			
		});
	}
	
	// ------- PRODUCT ---------

	private void createConceptSuggestBox() {
		conceptSuggestBox = new SuggestBox();
		conceptSuggestBox.setWidth("240px");
		conceptSuggestBox.setHeight("2em");
		conceptSuggestBox.getElement().getStyle().setProperty("padding", "0 5px");
		conceptSuggestBox.setAutoSelectEnabled(false);
		conceptSuggestBox.getElement().setPropertyString("placeholder", "Producto: busque por c\u00f3digo o descripci\u00f3n");
		
		conceptSuggestBox.addSelectionHandler(e -> {
			conceptSuggestBox.hideSuggestionList();
			onSearchFees();
		});
		
		conceptSuggestBox.addKeyUpHandler(e -> {
			String productQuery = conceptSuggestBox.getValue();
			if(e.isControlKeyDown() && e.getNativeKeyCode() == 32) {
				conceptSuggestBox.setValue("");
				productQuery = null;
				getProductsSuggestion(productQuery);
			} else if(AonStringUtils.isNotBlank(productQuery) && productQuery.length() > 3)
				getProductsSuggestion(productQuery);
		});
		
		conceptSuggestBox.getTextBox().addBlurHandler(e -> {
			String productQuery = conceptSuggestBox.getValue();
			if(AonStringUtils.isBlank(productQuery)) {
				onSearchFees();
			}
		});
	}
	
	private void getProductsSuggestion(String productQuery) {
		SERVICE.getProductsSuggestion(options.getDomainName(), options.getDomain(), options.getUser(), productQuery, new AsyncCallback<Map<String, OldItem>>() {
			
			@Override
			public void onSuccess(Map<String, OldItem> productSuggestionsDB) {
				productSuggestions = productSuggestionsDB;
				
				MultiWordSuggestOracle orclSb = (MultiWordSuggestOracle) conceptSuggestBox.getSuggestOracle();
				orclSb.clear();
				orclSb.addAll(productSuggestions.keySet());
				orclSb.setDefaultSuggestionsFromText(productSuggestions.keySet());
				conceptSuggestBox.showSuggestionList();
			}
			
			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub	
			}
			
		});
	}
	
	private void createConceptStatusListBox() {
		conceptStatusListBox = new ListBox();
		conceptStatusListBox.setHeight("2em");
		conceptStatusListBox.getElement().getStyle().setProperty("padding", "0 5px");
		conceptStatusListBox.addItem("-", "");
		conceptStatusListBox.addItem("Facturable", "0");
		conceptStatusListBox.addItem("No Facturable", "1");
		conceptStatusListBox.addItem("No Contratado", "2");
		conceptStatusListBox.addItem("Inactivo", "3");
		conceptStatusListBox.setSelectedIndex(1);
		conceptStatusListBox.addChangeHandler(e -> onSearchFees());
	}
	
	// ------- DOMAIN ---------
	
	private void createDomainTypeListBox() {
		domainTypeListBox = new ListBox();
		domainTypeListBox.setHeight("2em");
		domainTypeListBox.getElement().getStyle().setProperty("padding", "0 5px");
		domainTypeListBox.addItem("-", "");
		domainTypeListBox.addItem("Facturable", "0");
		domainTypeListBox.addItem("No Facturable", "1");
		domainTypeListBox.addChangeHandler(e -> onSearchFees());
	}

	private void createDomainStatusListBox() {
		domainStatusListBox = new ListBox();
		domainStatusListBox.setHeight("2em");
		domainStatusListBox.getElement().getStyle().setProperty("padding", "0 5px");
		domainStatusListBox.addItem("-", "");
		domainStatusListBox.addItem("Inactivo", "0");
		domainStatusListBox.addItem("Activo", "1");
		domainStatusListBox.addChangeHandler(e -> onSearchFees());
	}
	
	// ------- RESET FILTER ---------

	private void resetFilter() {
		monthListBox.setSelectedIndex(0);
		if (null != yearListBox) yearListBox.setSelectedIndex(0);
		customerSuggestBox.setValue("");
		customerStatusListBox.setSelectedIndex(0);
		if (null != segmentListBox) segmentListBox.setSelectedIndex(0);
		conceptSuggestBox.setValue("");
		conceptStatusListBox.setSelectedIndex(0);
		startCompareLB.setSelectedIndex(0);
		startDateBox.setValue(null);
		endCompareLB.setSelectedIndex(0);
		endDateBox.setValue(null);
		
		domainTypeListBox.setSelectedIndex(0);
		domainStatusListBox.setSelectedIndex(0);
		
		if(null != params) {
			params.setMonth(null);
			params.setYear(null);
			params.setCustomer(null);
			params.setCustomerStatus(null);
			params.setSegment(null);
			params.setProduct(null);
			params.setProductStatus(null);
			params.setStartCompare((byte)0);
			params.setStartDate(null);
			params.setEndCompare((byte)0);
			params.setEndDate(null);
			
			params.setDomainType(null);
			params.setDomainStatus(null);
		}
	}
	
	// ------- SEARCH ---------
	
	private void onSearchFees() {
		enableMoreData();
		offset.setValue(0);
		searchFees();
		bookingCheckList.clear();
		resetFeeTable();
	}

	private void searchFees() {
		if (!isMoreData()) return;
		
		createParams();
		
		switch (Integer.parseInt(bookingCheckType.getSelectedValue())) {
		case 0:
			SERVICE.getBookingWithoutFeeList(options.getDomainName(), options.getDomain(), options.getUser(), params,
					new AsyncCallback<LinkedList<BookingCheck>>() {
	
						@Override
						public void onFailure(Throwable caught) {
							AonMessagePanel.showError(messagePanel, "Error cargando panel de facturaci\u00f3n: " + caught.getMessage());
						}
	
						@Override
						public void onSuccess(LinkedList<BookingCheck> bookingCheckListDB) {
							loadData(bookingCheckListDB);
						}
					});
			break;
		case 1:
			SERVICE.getFeeWithoutBookingList(options.getDomainName(), options.getDomain(), options.getUser(), params,
					new AsyncCallback<LinkedList<BookingCheck>>() {
	
						@Override
						public void onFailure(Throwable caught) {
							AonMessagePanel.showError(messagePanel, "Error cargando panel de facturaci\u00f3n: " + caught.getMessage());
						}
	
						@Override
						public void onSuccess(LinkedList<BookingCheck> bookingCheckListDB) {
							loadData(bookingCheckListDB);
						}
					});
			break;
		default:
			SERVICE.getBookingCheckList(options.getDomainName(), options.getDomain(), options.getUser(), params,
					new AsyncCallback<LinkedList<BookingCheck>>() {
	
						@Override
						public void onFailure(Throwable caught) {
							AonMessagePanel.showError(messagePanel, "Error cargando panel de facturaci\u00f3n: " + caught.getMessage());
						}
	
						@Override
						public void onSuccess(LinkedList<BookingCheck> bookingCheckListDB) {
							loadData(bookingCheckListDB);
						}
					});
			break;
		}
	}
	
	private void loadData(LinkedList<BookingCheck> feeListDB) {
		if(params.getOffset() == 0 && (feeListDB == null || feeListDB.isEmpty()))
			showEmptyFeeMessage();
		else if(feeListDB == null || feeListDB.isEmpty()) {
			disableMoreData();
			if(bookingCheckList.isEmpty()) showEmptyFeeMessage();
		} else {
			showFeeTable();
			feeListDB.forEach( bookingCheck -> paintRow(bookingCheck));
			bookingCheckList.addAll(feeListDB);
			enableMoreData();
			offset.setValue(offset.getValue() + feeListDB.size());
		}
		
		enableSearch();
	}

	private void createParams() {
		if(null == params) params = new CustomerFeeParams();
		params.setDomain(options.getDomain());
				
		params.setMonth(AonStringUtils.isBlank(monthListBox.getSelectedValue()) ? null : Integer.parseInt(monthListBox.getSelectedValue()));
		params.setYear(AonStringUtils.isBlank(yearListBox.getSelectedValue()) ? null : Integer.parseInt(yearListBox.getSelectedValue()));
		params.setCustomer(null != customerSuggestions.get(customerSuggestBox.getValue()) ? customerSuggestions.get(customerSuggestBox.getValue()).getName() : null);
		params.setCustomerStatus(AonStringUtils.isBlank(customerStatusListBox.getSelectedValue()) ? null : Byte.parseByte(customerStatusListBox.getSelectedValue()));
		params.setSegment(segmentListBox != null && segmentListBox.getSelectedIndex() > 0 ? AonNumberUtils.toInteger( segmentListBox.getSelectedValue()) : null);
		params.setProduct(null != productSuggestions.get(conceptSuggestBox.getValue()) ? productSuggestions.get(conceptSuggestBox.getValue()).getId() : null);
		params.setProductStatus(AonStringUtils.isBlank(conceptStatusListBox.getSelectedValue()) ? null : Byte.parseByte(conceptStatusListBox.getSelectedValue()));
		params.setStartCompare(Byte.parseByte(startCompareLB.getSelectedValue()));
		params.setStartDate(startDateBox.getValue());
		params.setEndCompare(Byte.parseByte(endCompareLB.getSelectedValue()));
		params.setEndDate(endDateBox.getValue());
		
		params.setDomainType(AonStringUtils.isBlank(domainTypeListBox.getSelectedValue()) ? null : Byte.parseByte(domainTypeListBox.getSelectedValue()));
		params.setDomainStatus(AonStringUtils.isBlank(domainStatusListBox.getSelectedValue()) ? null : Byte.parseByte(domainStatusListBox.getSelectedValue()));
		
		params.setLimit(limit);
		params.setOffset(offset.getValue());
	}
	
	// ------- MAIN PAGE ---------

	private void initializeDeckPanel() {
		deckPanel.clear();
		createInitialMessage();
		createEmptyFeeMessage();
		createFeeTable();
		showInitialMessage();
	}
	
	private void showInitialMessage() {
		deckPanel.showWidget(0);
	}
	
	private void showEmptyFeeMessage() {
		deckPanel.showWidget(1);
	}
	
	private void showFeeTable() {
		deckPanel.showWidget(2);
	}	
	
	private void createInitialMessage() {
		HTMLPanel emptyPanel = new HTMLPanel("");
		emptyPanel.addStyleName(AON.CSS.aonDisplayFlexCenter());
		Label emptyMessage = new Label("RELLENE LA BUSQUEDA PARA CARGAR EL PANEL DE CONTRATACION");
		emptyMessage.getElement().getStyle().setFontWeight(FontWeight.BOLD);
		emptyMessage.getElement().getStyle().setMarginTop(1, Unit.EM);
		emptyPanel.add(emptyMessage);
		deckPanel.add(emptyPanel);
	}

	private void createEmptyFeeMessage() {
		HTMLPanel emptyFeePanel = new HTMLPanel("");
		emptyFeePanel.addStyleName(AON.CSS.aonDisplayFlexCenter());
		Label emptyMessage = new Label("NO EXISTEN DATOS PARA ESTE PERIODO/FILTRO");
		emptyMessage.getElement().getStyle().setFontWeight(FontWeight.BOLD);
		emptyMessage.getElement().getStyle().setMarginTop(1, Unit.EM);
		emptyFeePanel.add(emptyMessage);
		deckPanel.add(emptyFeePanel);
	}
	
	private void createFeeTable() {
		resetFeeTable();
		deckPanel.add(scrollPanel);
	}
	
	private void resetFeeTable() {
		scrollPanel.clear();
		createFeeHeader();
		setColumnWidth();
	}

	private void createFeeHeader() {
		bookingCheckTable = new Grid(0, isBookingWithOutFee() ? 8 : 10);
		bookingCheckTable.clear();
		bookingCheckTable.setWidth("100%");

		int row = bookingCheckTable.insertRow(bookingCheckTable.getRowCount());

		Label type = new Label("TIPO");
		Label customer = new Label("CLIENTE");
		Label customerStatus = new Label("ESTADO");
		Label concept = new Label("PRODUCTO");
		Label conceptStatus = new Label("ESTADO");
		Label quantity = new Label("CANTIDAD");
		Label startDate = new Label("F. DESDE");
		Label endDate = new Label("F. HASTA");
		Label url = new Label("");
		Label action = new Label("");
		
		type.addStyleName(AON.CSS.aonHeaderTable());
		customer.addStyleName(AON.CSS.aonHeaderTable());
		customerStatus.addStyleName(AON.CSS.aonHeaderTable());
		concept.addStyleName(AON.CSS.aonHeaderTable());
		conceptStatus.addStyleName(AON.CSS.aonHeaderTable());
		quantity.addStyleName(AON.CSS.aonHeaderTable());
		startDate.addStyleName(AON.CSS.aonHeaderTable());
		endDate.addStyleName(AON.CSS.aonHeaderTable());
		url.addStyleName(AON.CSS.aonHeaderTable());
		action.addStyleName(AON.CSS.aonHeaderTable());

		bookingCheckTable.setWidget(row, 0, type);
		bookingCheckTable.setWidget(row, 1, customer);
		bookingCheckTable.setWidget(row, 2, customerStatus);
		bookingCheckTable.setWidget(row, 3, concept);
		bookingCheckTable.setWidget(row, 4, conceptStatus);
		bookingCheckTable.setWidget(row, 5, quantity);
		if(isBookingWithOutFee()) {
			bookingCheckTable.setWidget(row, 6, url);
			bookingCheckTable.setWidget(row, 7, action);
		} else {
			bookingCheckTable.setWidget(row, 6, startDate);
			bookingCheckTable.setWidget(row, 7, endDate);
			bookingCheckTable.setWidget(row, 8, url);
			bookingCheckTable.setWidget(row, 9, action);
		}
		
		bookingCheckTable.getCellFormatter().addStyleName(row, 0, AON.CSS.aonHeaderSticky());
		bookingCheckTable.getCellFormatter().addStyleName(row, 1, AON.CSS.aonHeaderSticky());
		bookingCheckTable.getCellFormatter().addStyleName(row, 2, AON.CSS.aonHeaderSticky());
		bookingCheckTable.getCellFormatter().addStyleName(row, 3, AON.CSS.aonHeaderSticky());
		bookingCheckTable.getCellFormatter().addStyleName(row, 4, AON.CSS.aonHeaderSticky());
		bookingCheckTable.getCellFormatter().addStyleName(row, 5, AON.CSS.aonHeaderSticky());
		if(isBookingWithOutFee()) {
			bookingCheckTable.getCellFormatter().addStyleName(row, 6, AON.CSS.aonHeaderSticky());
			bookingCheckTable.getCellFormatter().addStyleName(row, 7, AON.CSS.aonHeaderSticky());
		} else {
			bookingCheckTable.getCellFormatter().addStyleName(row, 6, AON.CSS.aonHeaderSticky());
			bookingCheckTable.getCellFormatter().addStyleName(row, 7, AON.CSS.aonHeaderSticky());
			bookingCheckTable.getCellFormatter().addStyleName(row, 8, AON.CSS.aonHeaderSticky());
			bookingCheckTable.getCellFormatter().addStyleName(row, 9, AON.CSS.aonHeaderSticky());
		}
		
		scrollPanel.add(bookingCheckTable);
	}
	
	private void setColumnWidth() {
		bookingCheckTable.getColumnFormatter().getElement(0).getStyle().setWidth(80, Unit.PX);
		bookingCheckTable.getColumnFormatter().getElement(2).getStyle().setWidth(80, Unit.PX);
		bookingCheckTable.getColumnFormatter().getElement(3).getStyle().setWidth(30, Unit.PCT);
		bookingCheckTable.getColumnFormatter().getElement(4).getStyle().setWidth(80, Unit.PX);
		if(isBookingWithOutFee()) {
			bookingCheckTable.getColumnFormatter().getElement(5).getStyle().setWidth(160, Unit.PX);
			bookingCheckTable.getColumnFormatter().getElement(6).getStyle().setWidth(25, Unit.PX);
			bookingCheckTable.getColumnFormatter().getElement(7).getStyle().setWidth(25, Unit.PX);
		} else {
			bookingCheckTable.getColumnFormatter().getElement(5).getStyle().setWidth(80, Unit.PX);
			bookingCheckTable.getColumnFormatter().getElement(6).getStyle().setWidth(80, Unit.PX);
			bookingCheckTable.getColumnFormatter().getElement(7).getStyle().setWidth(80, Unit.PX);
			bookingCheckTable.getColumnFormatter().getElement(8).getStyle().setWidth(25, Unit.PX);
			bookingCheckTable.getColumnFormatter().getElement(9).getStyle().setWidth(25, Unit.PX);
		}
	}
	
	private boolean isBookingWithOutFee() {
		return Integer.parseInt(bookingCheckType.getSelectedValue()) == 0;
	}
	
	private void disableMoreData() {
		moreData.setValue(-1);
	}
	
	private void enableMoreData() {
		moreData.setValue(0);
	}
	
	private boolean isMoreData() {
		return (moreData.getValue() == 0 );
	}
	
	private void enableSearch() {
		searchEnabled.setValue(0);
	}
	
	private boolean isSearchEnabled() {
		return (searchEnabled.getValue() == 0 );
	}
	
	private void disableSearch() {
		searchEnabled.setValue(-1);
	}

	private void paintRow(BookingCheck bookingCheck) {
		int row = bookingCheckTable.insertRow(bookingCheckTable.getRowCount());

		Label typeLabel = new Label(getType());
		typeLabel.setTitle(getTypeTitle());
		
		Label customerLabel = new Label(bookingCheck.getCustomer().getName());
		Label customerStatusLabel = new Label(bookingCheck.getCustomer().getStatus().getDescription());
		Label productLabel = new Label(getProductDescription(bookingCheck));
		Label productStatusLabel = new Label(Integer.parseInt(bookingCheckType.getSelectedValue()) == 1 ? getFeeStatus(bookingCheck) : getProductStatus(bookingCheck));
		Label quantityLabel = new Label(AonStringUtils.isBlank(bookingCheck.getQuantity()) ? "1.0" : bookingCheck.getQuantity());
		Label startDateLabel = new Label(formatDate(bookingCheck.getStartDate()));
		Label endDateLabel = new Label(formatDate(bookingCheck.getEndDate()));
		
		AonToolbarSmallButton actionBtn = new AonToolbarSmallButton(getActionTitle(), AON.CSS.aonIconMoreVertical());
		actionBtn.addClickHandler(e -> {
			NativeEvent nativeEvent = e.getNativeEvent();
			switch (Integer.parseInt(bookingCheckType.getSelectedValue())) {
				case 0:
					bookingWithOutFeeMenu.setPopupPosition(nativeEvent.getClientX() - 150, nativeEvent.getClientY());
					bookingWithOutFeeMenu.show();
					bookingWithOutFeeMenu.setId(bookingCheck.getId());
					bookingWithOutFeeMenu.setItem(bookingCheck.getItem());
					bookingWithOutFeeMenu.setCustomer(bookingCheck.getCustomer());
					bookingWithOutFeeMenu.setBookingCheck(bookingCheck);
					break;
				case 1:
					feeWithoutbookintMenu.setPopupPosition(nativeEvent.getClientX() - 150, nativeEvent.getClientY());
					feeWithoutbookintMenu.show();
					feeWithoutbookintMenu.setId(bookingCheck.getId());
					feeWithoutbookintMenu.setItem(bookingCheck.getItem());
					feeWithoutbookintMenu.setCustomer(bookingCheck.getCustomer());
					break;
				default:
					break;
			}
		});
		
		switch (Integer.parseInt(bookingCheckType.getSelectedValue())) {
			case 0:
				actionBtn.setVisible(true);
				break;
			case 1:
				actionBtn.setVisible(true);
				break;
			default:
				if(!bookingCheck.getStatus().equals(RegistryItemStatus.ACTIVE)) {
					actionBtn = new AonToolbarSmallButton(getBookingStatus(bookingCheck.getStatus()), AON.CSS.aonIconInfo());
				} else actionBtn.setVisible(false);
				break;
		}
		
		AonToolbarSmallButton urlBtn = new AonToolbarSmallButton("URL", AON.CSS.aonIconInfo());
		urlBtn.addClickHandler(e -> {checkCustomerDomains(bookingCheck);});
		
		bookingCheckTable.setWidget(row, 0, typeLabel);
		bookingCheckTable.setWidget(row, 1, customerLabel);
		bookingCheckTable.setWidget(row, 2, customerStatusLabel);
		bookingCheckTable.setWidget(row, 3, productLabel);
		bookingCheckTable.setWidget(row, 4, productStatusLabel);
		bookingCheckTable.setWidget(row, 5, quantityLabel);
		if(isBookingWithOutFee()) {
			bookingCheckTable.setWidget(row, 6, urlBtn);
			bookingCheckTable.setWidget(row, 7, actionBtn);
		} else {
			bookingCheckTable.setWidget(row, 6, startDateLabel);
			bookingCheckTable.setWidget(row, 7, endDateLabel);
			bookingCheckTable.setWidget(row, 8, urlBtn);
			bookingCheckTable.setWidget(row, 9, actionBtn);
		}
		
		if(Integer.parseInt(bookingCheckType.getSelectedValue()) == 0) {
			productLabel.getElement().getStyle().setFontWeight(FontWeight.BOLD);
			productLabel.getElement().getStyle().setColor("red");
			productLabel.setTitle("Existe contrataci\u00f3n, pero no cuota");
		} else if(Integer.parseInt(bookingCheckType.getSelectedValue()) == 1) {
			productLabel.getElement().getStyle().setFontWeight(FontWeight.BOLD);
			productLabel.getElement().getStyle().setColor("orange");
			productLabel.setTitle("Existe una cuota sin contrataci\u00f3n");
		} else if(Integer.parseInt(bookingCheckType.getSelectedValue()) == 2) {
			if(!bookingCheck.getStatus().equals(RegistryItemStatus.ACTIVE)) {
				productLabel.getElement().getStyle().setFontWeight(FontWeight.BOLD);
				productLabel.getElement().getStyle().setColor("orange");
				productLabel.setTitle("Existe contrataci\u00f3n asociada, pero no es tipo 'Facturable'");
			}
		}
		
		if (row % 2 == 0) {
			typeLabel.addStyleName(AON.CSS.aonOddTableRow());
			customerLabel.addStyleName(AON.CSS.aonOddTableRow());
			customerStatusLabel.addStyleName(AON.CSS.aonOddTableRow());
			productLabel.addStyleName(AON.CSS.aonOddTableRow());
			productStatusLabel.addStyleName(AON.CSS.aonOddTableRow());
			quantityLabel.addStyleName(AON.CSS.aonOddTableRow());
			startDateLabel.addStyleName(AON.CSS.aonOddTableRow());
			endDateLabel.addStyleName(AON.CSS.aonOddTableRow());
			urlBtn.addStyleName(AON.CSS.aonOddTableRow());
			actionBtn.addStyleName(AON.CSS.aonOddTableRow());
			
			bookingCheckTable.getCellFormatter().addStyleName(row, 0, AON.CSS.aonOddTableRow());
			bookingCheckTable.getCellFormatter().addStyleName(row, 1, AON.CSS.aonOddTableRow());
			bookingCheckTable.getCellFormatter().addStyleName(row, 2, AON.CSS.aonOddTableRow());
			bookingCheckTable.getCellFormatter().addStyleName(row, 3, AON.CSS.aonOddTableRow());
			bookingCheckTable.getCellFormatter().addStyleName(row, 4, AON.CSS.aonOddTableRow());
			bookingCheckTable.getCellFormatter().addStyleName(row, 5, AON.CSS.aonOddTableRow());
			if(isBookingWithOutFee()) {
				bookingCheckTable.getCellFormatter().addStyleName(row, 6, AON.CSS.aonOddTableRow());
				bookingCheckTable.getCellFormatter().addStyleName(row, 7, AON.CSS.aonOddTableRow());
			} else {
				bookingCheckTable.getCellFormatter().addStyleName(row, 6, AON.CSS.aonOddTableRow());
				bookingCheckTable.getCellFormatter().addStyleName(row, 7, AON.CSS.aonOddTableRow());
				bookingCheckTable.getCellFormatter().addStyleName(row, 8, AON.CSS.aonOddTableRow());
				bookingCheckTable.getCellFormatter().addStyleName(row, 9, AON.CSS.aonOddTableRow());
			}
		}
		
		bookingCheckTable.getCellFormatter().getElement(row, 0).getStyle().setTextAlign(TextAlign.CENTER);
		bookingCheckTable.getCellFormatter().getElement(row, 2).getStyle().setTextAlign(TextAlign.CENTER);
		bookingCheckTable.getCellFormatter().getElement(row, 4).getStyle().setTextAlign(TextAlign.CENTER);
		bookingCheckTable.getCellFormatter().getElement(row, 5).getStyle().setTextAlign(TextAlign.CENTER);
		if(isBookingWithOutFee()) {
			bookingCheckTable.getCellFormatter().getElement(row, 6).getStyle().setTextAlign(TextAlign.CENTER);
			bookingCheckTable.getCellFormatter().getElement(row, 7).getStyle().setTextAlign(TextAlign.CENTER);
		} else {
			bookingCheckTable.getCellFormatter().getElement(row, 6).getStyle().setTextAlign(TextAlign.CENTER);
			bookingCheckTable.getCellFormatter().getElement(row, 7).getStyle().setTextAlign(TextAlign.CENTER);
			bookingCheckTable.getCellFormatter().getElement(row, 8).getStyle().setTextAlign(TextAlign.CENTER);
			bookingCheckTable.getCellFormatter().getElement(row, 9).getStyle().setTextAlign(TextAlign.CENTER);	
		}

		bookingCheckTable.getRowFormatter().getElement(row).getStyle().setHeight(25.00, Unit.PX);
	}

	private void checkCustomerDomains(BookingCheck bookingCheck) {
		AonMessagePanel.showLoading(messagePanel, "Obteniendo dominios del cliente ...");
		
//		String responseBody = getResponseCustomerDomainsFake();
//      List<DomainCompany> companies = DomainCompanyJSON.parseDomainCompanyJSONArr(responseBody);
//      AonMessagePanel.hideMessage(messagePanel);
//        
//      showBookingCustomer(bookingCheck.getCustomer().getName());
//      bookingCustomer.setBookingCustomer(bookingCheck.getCustomer(), companies);
		
		// Create the base URL
		String baseUrl = "/ms/api/domain/" + bookingCheck.getCustomer().getId().toString();

		// Create a URL builder and add query parameters
		UrlBuilder urlBuilder = new UrlBuilder();
		urlBuilder.setProtocol(Window.Location.getProtocol()); // Use the current protocol
		urlBuilder.setHost("aon.solutions"); 
		urlBuilder.setPath(baseUrl);
		
		// Create the request builder with the complete URL
		RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.GET, urlBuilder.buildString());
		requestBuilder.setHeader("session_id", "AONd95770f269e711eb94390242ac130002");
		
		try {
		    // Send the request
		    requestBuilder.sendRequest(null, new RequestCallback() {
		        public void onResponseReceived(Request request, Response response) {
		            if (response.getStatusCode() == 200) {
		            	
		                String responseBody = response.getText();
		                List<DomainCompany> companies = DomainCompanyJSON.parseDomainCompanyJSONArr(responseBody);
		                AonMessagePanel.hideMessage(messagePanel);
		                
		                showBookingCustomer(bookingCheck.getCustomer().getName());
		                bookingCustomer.setBookingCustomer(bookingCheck.getCustomer(), companies);
		                
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
	
	private String getFeeStatus(BookingCheck bookingCheck) {
		return 	bookingCheck.getEndDate() == null || 
				(new Date().before(bookingCheck.getEndDate()) && 
				bookingCheck.getEndDate().after(bookingCheck.getStartDate())) 
				? "Facturable" : "Expirado";
	}

	private String getProductStatus(BookingCheck bookingCheck) {
		if(bookingCheck.getStatus().equals(RegistryItemStatus.ACTIVE)) return "Facturable";
		else if(bookingCheck.getStatus().equals(RegistryItemStatus.INTERESTED)) return "No Facturable";
		else if(bookingCheck.getStatus().equals(RegistryItemStatus.REFUSED)) return " No Contratado";
		else if(bookingCheck.getStatus().equals(RegistryItemStatus.INACTIVE)) return "Inactivo";
		else return "";
	}

	private String getTypeTitle() {
		switch (Integer.parseInt(bookingCheckType.getSelectedValue())) {
			case 0:
				return "Contrataci\u00f3n";
			case 1:
				return "Cuota";
			default:
				return "Cuota";
		}
	}
	
	private String getType() {
		switch (Integer.parseInt(bookingCheckType.getSelectedValue())) {
			case 0:
				return "Contr.";
			case 1:
				return "Cuota";
			default:
				return "Cuota";
		}
	}

	private String getBookingStatus(RegistryItemStatus status) {
		switch (status) {
			case INTERESTED:
				return "La contrataci\u00f3n asociada es No Facturable";
			case REFUSED:
				return "La contrataci\u00f3n asociada es No Contratado";
			case INACTIVE:
				return "La contrataci\u00f3n asociada es Inactiva";
			default:
				return "";
		}
	}

	private String getActionTitle() {
		switch (Integer.parseInt(bookingCheckType.getSelectedValue())) {
			case 0:
				return "Crear cuota para la contrataci\u00f3n";
			case 1:
				return "Saneador";
			default:
				return "";
		}
	}

	private String getProductDescription(BookingCheck bookingCheck) {
		if(null == bookingCheck.getItem()) return null;
		
		String productDescription = bookingCheck.getItem().getProduct().getName();
		String productCode = bookingCheck.getItem().getProduct().getCode();
		
		return AonStringUtils.isBlank(productDescription) ? productCode : productDescription + " - (" + productCode + ")";
	}

	private void setInputStyle(Widget widget) {
		widget.setHeight("2em");
		widget.getElement().getStyle().setProperty("padding", "0 5px");
	}
	
	private String formatDate(Date date) {
		if(null == date) return null;
		return formatDate.format(date);
	}

	// -------------------------------- TOOLBAR
	
	private void createToolbar() {
		toolbar = new AonToolbar("Panel Contrataci\u00f3n");
		
		bookingCheckType = new ListBox();
		bookingCheckType.addItem("Contrataci\u00f3n Sin Cuotas", "0");
		bookingCheckType.addItem("Cuotas Sin Contrataci\u00f3n", "1");
		bookingCheckType.addItem("Cuotas Correctas", "2");
		
		bookingCheckType.addChangeHandler(e -> {
			checkPeriodVisibility();
			resetFilter();
			bookingCheckList.clear();
			resetFeeTable();
			enableMoreData();
			offset.setValue(0);
			showInitialMessage();
			onSearchFees();
		});
		
		syncDomains = new AonToolbarButton("Sincronizar dominio", AON.CSS.aonIconCloudSync());
		syncDomains.addClickHandler(e -> {
			syncDomain();
		});
		
		backBtn = new AonToolbarButton("Lista Contrataci\u00f3n", AON.CSS.aonIconBack());
		backBtn.setVisible(false);
		backBtn.addClickHandler(e -> showBookingList());
		
		toolbar.add(bookingCheckType);
		toolbar.add(syncDomains);
		toolbar.add(backBtn);
	}

	private void syncDomain() {
		AonMessagePanel.showLoading(messagePanel, "Obteniendo dominios ...");
		
		// Create the base URL
		String baseUrl = "/ms/api/domain/";

		// Create a URL builder and add query parameters
		UrlBuilder urlBuilder = new UrlBuilder();
		urlBuilder.setProtocol(Window.Location.getProtocol()); // Use the current protocol
		urlBuilder.setHost("aon.solutions"); 
		urlBuilder.setPath(baseUrl);
		
		 urlBuilder.setParameter("linked", "true");
		
		// Create the request builder with the complete URL
		RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.GET, urlBuilder.buildString());
		requestBuilder.setHeader("session_id", "AONd95770f269e711eb94390242ac130002");
		
		try {
		    // Send the request
		    requestBuilder.sendRequest(null, new RequestCallback() {
		        public void onResponseReceived(Request request, Response response) {
		            if (response.getStatusCode() == 200) {

		                String responseBody = response.getText();
		                List<DomainCompany> companies = DomainCompanyJSON.parseDomainCompanyJSONArr(responseBody);
		                AonMessagePanel.hideMessage(messagePanel);
		                showSelectDomainsDialog(companies);
		                
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
	
	private void showSelectDomainsDialog(List<DomainCompany> companies) {
		new AonDomainSelectionDialog("Dominios", companies) {
			
			@Override
			protected void onAccept(DomainCompany domainCompany) {
				AonMessagePanel.showLoading(messagePanel, "Obteniendo contrataci\u00f3n para el dominio seleccionado ...");
				
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
		
		};
	}
	
	private void getDomain(DomainCompany domainCompany, String bookingJSON) {
		AonMessagePanel.showLoading(messagePanel, "Obteniendo informaci\u00f3n del dominio seleccionado ...");
		
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
		                updateBookingRitems(domainJSON, bookingJSON);
		                
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

	private void updateBookingRitems(String domainJSON, String bookingJSON) {
		AonMessagePanel.showLoading(messagePanel, "Sincronizando contrataci\u00f3n para el dominio seleccionado ...");
		
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
		            	AonMessagePanel.showSuccess(messagePanel, "La sincronizaci\u00f3n se ha realizado correctamente");
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
	
	private void checkPeriodVisibility() {
		switch (Integer.parseInt(bookingCheckType.getSelectedValue())) {
			case 0:
				periodItemPanel.setVisible(false);
				break;
			case 1:
				periodItemPanel.setVisible(true);
				break;
			default:
				periodItemPanel.setVisible(true);
				break;
		}
	}
	
	// -------------------------------- FIXING METHODS

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
								bookingCheckList.clear();
								resetFeeTable();
								enableMoreData();
								offset.setValue(0);
								showInitialMessage();
								onSearchFees();
							}
						});
			}
		});
	}

	private void createBooking() {
		new BookingCheckDialog(options, feeWithoutbookintMenu.getItem(), feeWithoutbookintMenu.getCustomer()) {
			
			@Override
			protected void onCreate(BookingCheck bookingCheck) {
				SERVICE.saveBookingCheck(options.getDomainName(), options.getDomain(), options.getUser(), bookingCheck, new AsyncCallback<Void>() {
					
					@Override
					public void onSuccess(Void result) {
						AonMessagePanel.showSuccess(messagePanel, "Se ha creado la contrataci\u00f3n correctamente");
						resetFeeTable();
						enableMoreData();
						offset.setValue(0);
						showInitialMessage();
						onSearchFees();
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
						resetFeeTable();
						enableMoreData();
						offset.setValue(0);
						showInitialMessage();
						onSearchFees();
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
						resetFeeTable();
						enableMoreData();
						offset.setValue(0);
						showInitialMessage();
						onSearchFees();
					}
					
					@Override
					public void onFailure(Throwable caught) {
						AonMessagePanel.showError(messagePanel, "Error actualizando contrataci\u00f3n: " + caught.getMessage());
					}
				});
			}
		};
	}
	
//	private String getResponseCustomerDomainsFake() {
//		return "[{\"domain\":{\"owner\":\"info@aonsolutions.es\",\"lastAccessUser\":\"inigo\",\"modification_user\":\"jgarcia\",\"aonStatus\":\"BILLABLE\",\"disableDomainManagement\":false,\"domainType\":\"CONSULTANCY\",\"creation_user\":\"admin\",\"enableHeredity\":false,\"description\":\"Entorno Zarate\",\"active\":true,\"creation_date\":\"2015-02-20T12:08:02Z\",\"maxTotalDocumentSize\":100,\"lastAccessDate\":\"2023-08-25T12:51:30Z\",\"modification_date\":\"2022-05-29T21:44:05Z\",\"name\":\"entorno-zarate.aonsolutions.net\",\"id\":497,\"domainManagement\":true,\"maxDocumentSize\":1,\"maxDefinedUsers\":8,\"aonCustomer\":4289},\"company\":{\"surcharge\":false,\"withholding\":false,\"documentType\":\"CIF\",\"document\":\"B01192707\",\"active\":false,\"eInvoice\":false,\"documentCountry\":\"ES\",\"nationality\":\"ES\",\"vatAccrualPayment\":false,\"domain\":{\"owner\":\"info@aonsolutions.es\",\"lastAccessUser\":\"inigo\",\"modification_user\":\"jgarcia\",\"aonStatus\":\"BILLABLE\",\"disableDomainManagement\":false,\"domainType\":\"CONSULTANCY\",\"creation_user\":\"admin\",\"enableHeredity\":false,\"description\":\"Entorno Zarate\",\"active\":true,\"creation_date\":\"2015-02-20T12:08:02Z\",\"maxTotalDocumentSize\":100,\"lastAccessDate\":\"2023-08-25T12:51:30Z\",\"modification_date\":\"2022-05-29T21:44:05Z\",\"name\":\"entorno-zarate.aonsolutions.net\",\"id\":497,\"domainManagement\":true,\"maxDocumentSize\":1,\"maxDefinedUsers\":8,\"aonCustomer\":4289},\"legalPerson\":false,\"name\":\"ZARATE ASESORES S.L.\",\"alias\":\"ENTORNO-ZARATE\",\"id\":20720,\"confidential\":false}},{\"domain\":{\"owner\":\"administracion@aonSolutions.es\",\"lastAccessUser\":\"cparra\",\"modification_user\":\"jgarcia\",\"aonStatus\":\"BILLABLE\",\"disableDomainManagement\":false,\"domainType\":\"OFFICE\",\"enableHeredity\":false,\"description\":\"ZARATE ASESORES,S.L.\",\"active\":true,\"maxTotalDocumentSize\":100,\"lastAccessDate\":\"2023-08-25T08:13:06Z\",\"modification_date\":\"2019-02-02T11:46:18Z\",\"name\":\"zarateasesores.aonsolutions.net\",\"id\":611,\"domainManagement\":false,\"maxDocumentSize\":1,\"maxDefinedUsers\":5,\"aonCustomer\":4289},\"company\":{\"surcharge\":false,\"withholding\":false,\"documentType\":\"CIF\",\"document\":\"B01192707\",\"active\":false,\"eInvoice\":false,\"documentCountry\":\"ES\",\"nationality\":\"ES\",\"vatAccrualPayment\":false,\"domain\":{\"owner\":\"administracion@aonSolutions.es\",\"lastAccessUser\":\"cparra\",\"modification_user\":\"jgarcia\",\"aonStatus\":\"BILLABLE\",\"disableDomainManagement\":false,\"domainType\":\"OFFICE\",\"enableHeredity\":false,\"description\":\"ZARATE ASESORES,S.L.\",\"active\":true,\"maxTotalDocumentSize\":100,\"lastAccessDate\":\"2023-08-25T08:13:06Z\",\"modification_date\":\"2019-02-02T11:46:18Z\",\"name\":\"zarateasesores.aonsolutions.net\",\"id\":611,\"domainManagement\":false,\"maxDocumentSize\":1,\"maxDefinedUsers\":5,\"aonCustomer\":4289},\"legalPerson\":true,\"name\":\"ZARATE ASESORES,S.L.\",\"alias\":\"\",\"id\":72952,\"confidential\":false}}]";
//	}
	
//	private String getResponseDomainsFake() {
//		return "[{\"schema\":\"zar-aonsolutions-net\",\"domain\":{\"owner\":\"info@aonsolutions.es\",\"lastAccessUser\":\"carlos\",\"modification_user\":\"admin\",\"aonStatus\":\"BILLABLE\",\"disableDomainManagement\":false,\"domainType\":\"CONSULTANCY\",\"creation_user\":\"admin\",\"enableHeredity\":false,\"description\":\"Entorno LyZ\",\"active\":true,\"creation_date\":\"2015-02-19T18:53:04Z\",\"maxTotalDocumentSize\":100,\"lastAccessDate\":\"2023-08-09T12:24:48Z\",\"modification_date\":\"2015-02-20T02:48:29Z\",\"name\":\"entorno-lyz.aonsolutions.net\",\"id\":359,\"domainManagement\":true,\"maxDocumentSize\":1,\"maxDefinedUsers\":4,\"aonCustomer\":4290},\"company\":{\"surcharge\":false,\"withholding\":false,\"documentType\":\"CIF\",\"document\":\"B01390830\",\"active\":false,\"eInvoice\":false,\"documentCountry\":\"ES\",\"nationality\":\"ES\",\"vatAccrualPayment\":false,\"domain\":{\"owner\":\"info@aonsolutions.es\",\"lastAccessUser\":\"carlos\",\"modification_user\":\"admin\",\"aonStatus\":\"BILLABLE\",\"disableDomainManagement\":false,\"domainType\":\"CONSULTANCY\",\"creation_user\":\"admin\",\"enableHeredity\":false,\"description\":\"Entorno LyZ\",\"active\":true,\"creation_date\":\"2015-02-19T18:53:04Z\",\"maxTotalDocumentSize\":100,\"lastAccessDate\":\"2023-08-09T12:24:48Z\",\"modification_date\":\"2015-02-20T02:48:29Z\",\"name\":\"entorno-lyz.aonsolutions.net\",\"id\":359,\"domainManagement\":true,\"maxDocumentSize\":1,\"maxDefinedUsers\":4,\"aonCustomer\":4290},\"legalPerson\":false,\"name\":\"Zarate & Larreina Asesores\",\"alias\":\"\",\"id\":7578,\"confidential\":false}},{\"schema\":\"zar-aonsolutions-net\",\"domain\":{\"owner\":\"info@aonsolutions.es\",\"lastAccessUser\":\"inigo\",\"modification_user\":\"jgarcia\",\"aonStatus\":\"BILLABLE\",\"disableDomainManagement\":false,\"domainType\":\"CONSULTANCY\",\"creation_user\":\"admin\",\"enableHeredity\":false,\"description\":\"Entorno Zarate\",\"active\":true,\"creation_date\":\"2015-02-20T12:08:02Z\",\"maxTotalDocumentSize\":100,\"lastAccessDate\":\"2023-08-25T12:51:30Z\",\"modification_date\":\"2022-05-29T21:44:05Z\",\"name\":\"entorno-zarate.aonsolutions.net\",\"id\":497,\"domainManagement\":true,\"maxDocumentSize\":1,\"maxDefinedUsers\":8,\"aonCustomer\":4289},\"company\":{\"surcharge\":false,\"withholding\":false,\"documentType\":\"CIF\",\"document\":\"B01192707\",\"active\":false,\"eInvoice\":false,\"documentCountry\":\"ES\",\"nationality\":\"ES\",\"vatAccrualPayment\":false,\"domain\":{\"owner\":\"info@aonsolutions.es\",\"lastAccessUser\":\"inigo\",\"modification_user\":\"jgarcia\",\"aonStatus\":\"BILLABLE\",\"disableDomainManagement\":false,\"domainType\":\"CONSULTANCY\",\"creation_user\":\"admin\",\"enableHeredity\":false,\"description\":\"Entorno Zarate\",\"active\":true,\"creation_date\":\"2015-02-20T12:08:02Z\",\"maxTotalDocumentSize\":100,\"lastAccessDate\":\"2023-08-25T12:51:30Z\",\"modification_date\":\"2022-05-29T21:44:05Z\",\"name\":\"entorno-zarate.aonsolutions.net\",\"id\":497,\"domainManagement\":true,\"maxDocumentSize\":1,\"maxDefinedUsers\":8,\"aonCustomer\":4289},\"legalPerson\":false,\"name\":\"ZARATE ASESORES S.L.\",\"alias\":\"ENTORNO-ZARATE\",\"id\":20720,\"confidential\":false}},{\"schema\":\"zar-aonsolutions-net\",\"domain\":{\"owner\":\"jcomas@aonsolutions.es\",\"lastAccessUser\":\"admin\",\"modification_user\":\"jgarcia\",\"aonStatus\":\"BILLABLE\",\"disableDomainManagement\":false,\"domainType\":\"ENTERPRISE\",\"enableHeredity\":false,\"description\":\"HABILITAS EDUCACIÓN NORTE, S.L.\",\"active\":true,\"parentId\":497,\"maxTotalDocumentSize\":100,\"lastAccessDate\":\"2023-08-24T13:13:44Z\",\"modification_date\":\"2021-12-02T08:40:01Z\",\"name\":\"serinpack-zar.aonsolutions.net\",\"id\":639,\"domainManagement\":false,\"maxDocumentSize\":1,\"maxDefinedUsers\":1,\"aonCustomer\":665928},\"company\":{\"surcharge\":false,\"withholding\":false,\"documentType\":\"CIF\",\"document\":\"B01524487\",\"active\":false,\"eInvoice\":false,\"documentCountry\":\"ES\",\"nationality\":\"ES\",\"vatAccrualPayment\":false,\"domain\":{\"owner\":\"jcomas@aonsolutions.es\",\"lastAccessUser\":\"admin\",\"modification_user\":\"jgarcia\",\"aonStatus\":\"BILLABLE\",\"disableDomainManagement\":false,\"domainType\":\"ENTERPRISE\",\"enableHeredity\":false,\"description\":\"HABILITAS EDUCACIÓN NORTE, S.L.\",\"active\":true,\"parentId\":497,\"maxTotalDocumentSize\":100,\"lastAccessDate\":\"2023-08-24T13:13:44Z\",\"modification_date\":\"2021-12-02T08:40:01Z\",\"name\":\"serinpack-zar.aonsolutions.net\",\"id\":639,\"domainManagement\":false,\"maxDocumentSize\":1,\"maxDefinedUsers\":1,\"aonCustomer\":665928},\"legalPerson\":true,\"name\":\"HABILITAS EDUCACION NORTE, S.L.U.\",\"alias\":\"Habilitas Creative Play\",\"id\":29033,\"confidential\":false}},{\"schema\":\"zar-aonsolutions-net\",\"domain\":{\"owner\":\"jcomas@aonsolutions.es\",\"lastAccessUser\":\"arantza\",\"modification_user\":\"jgarcia\",\"aonStatus\":\"BILLABLE\",\"disableDomainManagement\":false,\"domainType\":\"ENTERPRISE\",\"creation_user\":\"inma\",\"enableHeredity\":true,\"description\":\"HABILITAS EDUCACION SL.\",\"active\":false,\"creation_date\":\"2016-07-20T12:21:21Z\",\"parentId\":497,\"maxTotalDocumentSize\":100,\"lastAccessDate\":\"2020-03-11T11:04:48Z\",\"scope\":421,\"modification_date\":\"2019-12-30T13:53:27Z\",\"name\":\"habilitas-zar.aonsolutions.net\",\"id\":718,\"domainManagement\":false,\"maxDocumentSize\":1,\"maxDefinedUsers\":0,\"aonCustomer\":665928},\"company\":{\"surcharge\":false,\"withholding\":false,\"documentType\":\"CIF\",\"document\":\"B01524487\",\"active\":true,\"eInvoice\":true,\"documentCountry\":\"ES\",\"nationality\":\"ES\",\"vatAccrualPayment\":false,\"domain\":{\"owner\":\"jcomas@aonsolutions.es\",\"lastAccessUser\":\"arantza\",\"modification_user\":\"jgarcia\",\"aonStatus\":\"BILLABLE\",\"disableDomainManagement\":false,\"domainType\":\"ENTERPRISE\",\"creation_user\":\"inma\",\"enableHeredity\":true,\"description\":\"HABILITAS EDUCACION SL.\",\"active\":false,\"creation_date\":\"2016-07-20T12:21:21Z\",\"parentId\":497,\"maxTotalDocumentSize\":100,\"lastAccessDate\":\"2020-03-11T11:04:48Z\",\"scope\":421,\"modification_date\":\"2019-12-30T13:53:27Z\",\"name\":\"habilitas-zar.aonsolutions.net\",\"id\":718,\"domainManagement\":false,\"maxDocumentSize\":1,\"maxDefinedUsers\":0,\"aonCustomer\":665928},\"legalPerson\":false,\"name\":\"HABILITAS EDUCACIÓN NORTE,S.L.U.\",\"alias\":\"HABILITAS\",\"id\":37231,\"confidential\":false}},{\"schema\":\"zar-aonsolutions-net\",\"domain\":{\"owner\":\"jcomas@aonSolutions.es\",\"lastAccessUser\":\"admin\",\"modification_user\":\"jgarcia\",\"aonStatus\":\"BILLABLE\",\"disableDomainManagement\":false,\"domainType\":\"ENTERPRISE\",\"creation_user\":\"inma\",\"enableHeredity\":true,\"description\":\"ESTIBALIZ VERA ORTIZ DE ZARATE\",\"active\":true,\"creation_date\":\"2021-02-25T11:57:17Z\",\"parentId\":497,\"maxTotalDocumentSize\":100,\"lastAccessDate\":\"2023-08-08T21:46:44Z\",\"scope\":421,\"modification_date\":\"2021-02-25T16:21:04Z\",\"name\":\"tallerdearte-zar.aonsolutions.net\",\"id\":858,\"domainManagement\":false,\"maxDocumentSize\":1,\"maxDefinedUsers\":1,\"aonCustomer\":681545},\"company\":{\"surcharge\":false,\"withholding\":false,\"documentType\":\"NIF\",\"document\":\"72753274L\",\"active\":true,\"eInvoice\":false,\"documentCountry\":\"ES\",\"nationality\":\"ES\",\"vatAccrualPayment\":false,\"domain\":{\"owner\":\"jcomas@aonSolutions.es\",\"lastAccessUser\":\"admin\",\"modification_user\":\"jgarcia\",\"aonStatus\":\"BILLABLE\",\"disableDomainManagement\":false,\"domainType\":\"ENTERPRISE\",\"creation_user\":\"inma\",\"enableHeredity\":true,\"description\":\"ESTIBALIZ VERA ORTIZ DE ZARATE\",\"active\":true,\"creation_date\":\"2021-02-25T11:57:17Z\",\"parentId\":497,\"maxTotalDocumentSize\":100,\"lastAccessDate\":\"2023-08-08T21:46:44Z\",\"scope\":421,\"modification_date\":\"2021-02-25T16:21:04Z\",\"name\":\"tallerdearte-zar.aonsolutions.net\",\"id\":858,\"domainManagement\":false,\"maxDocumentSize\":1,\"maxDefinedUsers\":1,\"aonCustomer\":681545},\"legalPerson\":false,\"name\":\"ESTIBALIZ VERA ORTIZ DE ZARATE\",\"alias\":\"TALLER DE ARTE\",\"id\":235017,\"confidential\":false}},{\"schema\":\"etl-aonsolutions-net\",\"domain\":{\"owner\":\"jgarcia@aonsolutions.es\",\"lastAccessUser\":\"joan\",\"modification_user\":\"admin\",\"aonStatus\":\"BILLABLE\",\"disableDomainManagement\":false,\"domainType\":\"CONSULTANCY\",\"creation_user\":\"jgarcia\",\"enableHeredity\":false,\"description\":\"Villagra-Noriega\",\"active\":true,\"creation_date\":\"2021-10-02T11:08:34Z\",\"maxTotalDocumentSize\":100,\"lastAccessDate\":\"2023-08-08T12:09:38Z\",\"modification_date\":\"2022-02-07T10:39:39Z\",\"name\":\"cvnoriega.aonsolutions.net\",\"id\":2,\"domainManagement\":true,\"maxDocumentSize\":1,\"maxDefinedUsers\":7,\"aonCustomer\":693843},\"company\":{\"surcharge\":false,\"withholding\":false,\"documentType\":\"CIF\",\"document\":\"\",\"active\":true,\"eInvoice\":false,\"documentCountry\":\"ES\",\"nationality\":\"ES\",\"vatAccrualPayment\":false,\"domain\":{\"owner\":\"jgarcia@aonsolutions.es\",\"lastAccessUser\":\"joan\",\"modification_user\":\"admin\",\"aonStatus\":\"BILLABLE\",\"disableDomainManagement\":false,\"domainType\":\"CONSULTANCY\",\"creation_user\":\"jgarcia\",\"enableHeredity\":false,\"description\":\"Villagra-Noriega\",\"active\":true,\"creation_date\":\"2021-10-02T11:08:34Z\",\"maxTotalDocumentSize\":100,\"lastAccessDate\":\"2023-08-08T12:09:38Z\",\"modification_date\":\"2022-02-07T10:39:39Z\",\"name\":\"cvnoriega.aonsolutions.net\",\"id\":2,\"domainManagement\":true,\"maxDocumentSize\":1,\"maxDefinedUsers\":7,\"aonCustomer\":693843},\"legalPerson\":false,\"name\":\"Villagra-Noriega\",\"alias\":\"CVN\",\"id\":216083,\"confidential\":false}}]";
//	}
//	
//	private String getResponseBookingFake() {
//		return "{\"resume\":{\"domain\":{\"ENTERPRISE\":{\"number\":2,\"childs\":[{\"name\":\"c01122-lyz.aonsolutions.net\",\"description\":\"LIMPIEZAS HAUS NET, S.L.\",\"id\":740,\"maxDefinedUsers\":1,\"apps\":[\"COMMERCIAL\",\"TREASURY\",\"STANDAR_MANAGEMENT\",\"INVOICE\"]},{\"name\":\"c01114-lyz.aonsolutions.net\",\"description\":\"AIURRI MONTESSORI, S.L.\",\"id\":713,\"maxDefinedUsers\":1,\"apps\":[\"STANDAR_MANAGEMENT\",\"INVOICE\",\"COMMERCIAL\",\"TREASURY\"]}],\"apps\":{}}},\"user\":{\"SHARED\":1,\"childDefinedUsers\":2,\"childBillingUsers\":0}},\"numberOfUsers\":4,\"domain\":{\"owner\":\"info@aonsolutions.es\",\"lastAccessUser\":\"carlos\",\"modification_user\":\"admin\",\"aonStatus\":\"BILLABLE\",\"disableDomainManagement\":false,\"domainType\":\"CONSULTANCY\",\"creation_user\":\"admin\",\"enableHeredity\":false,\"description\":\"Entorno LyZ\",\"active\":true,\"creation_date\":\"2015-02-19T18:53:04Z\",\"maxTotalDocumentSize\":100,\"lastAccessDate\":\"2023-08-09T12:24:48Z\",\"modification_date\":\"2015-02-20T02:48:29Z\",\"name\":\"entorno-lyz.aonsolutions.net\",\"id\":359,\"domainManagement\":true,\"maxDocumentSize\":1,\"maxDefinedUsers\":4,\"aonCustomer\":4290},\"company\":{\"surcharge\":false,\"withholding\":false,\"documentType\":\"CIF\",\"document\":\"B01390830\",\"active\":false,\"eInvoice\":false,\"documentCountry\":\"ES\",\"nationality\":\"ES\",\"vatAccrualPayment\":false,\"domain\":{\"owner\":\"info@aonsolutions.es\",\"lastAccessUser\":\"carlos\",\"modification_user\":\"admin\",\"aonStatus\":\"BILLABLE\",\"disableDomainManagement\":false,\"domainType\":\"CONSULTANCY\",\"creation_user\":\"admin\",\"enableHeredity\":false,\"description\":\"Entorno LyZ\",\"active\":true,\"creation_date\":\"2015-02-19T18:53:04Z\",\"maxTotalDocumentSize\":100,\"lastAccessDate\":\"2023-08-09T12:24:48Z\",\"modification_date\":\"2015-02-20T02:48:29Z\",\"name\":\"entorno-lyz.aonsolutions.net\",\"id\":359,\"domainManagement\":true,\"maxDocumentSize\":1,\"maxDefinedUsers\":4,\"aonCustomer\":4290},\"legalPerson\":false,\"name\":\"Zarate & Larreina Asesores\",\"alias\":\"\",\"id\":7578,\"confidential\":false},\"parentApps\":[],\"payer\":\"\",\"apps\":[\"STANDAR_MANAGEMENT\",\"INVOICE\",\"COMMERCIAL\",\"TREASURY\"]}";
//	}

}
