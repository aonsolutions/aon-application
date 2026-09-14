package com.esferalia.aon.gwt.fiscal.client.registry;

import static com.esferalia.aon.gwt.fiscal.client.EntryPointUtils.getCurrentDomain;
import static com.esferalia.aon.gwt.fiscal.client.EntryPointUtils.getCurrentDomainName;
import static com.esferalia.aon.gwt.fiscal.client.EntryPointUtils.getCurrentUser;
import static com.esferalia.aon.gwt.fiscal.client.EntryPointUtils.getOfficeDomain;
import static com.esferalia.aon.gwt.fiscal.client.EntryPointUtils.getRootPanel;
import static com.esferalia.aon.gwt.fiscal.client.EntryPointUtils.removeCustomer;
import static com.esferalia.aon.gwt.fiscal.client.EntryPointUtils.removeOfficeDomain;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.TreeMap;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.gwt.common.client.CommonServiceAsync;
import com.esferalia.aon.gwt.common.client.CommonServiceAsyncDecorator;
import com.esferalia.aon.gwt.common.client.RegistryService;
import com.esferalia.aon.gwt.common.client.RegistryServiceAsync;
import com.esferalia.aon.gwt.common.client.RegistryServiceAsyncDecorator;
import com.esferalia.aon.gwt.common.client.RootLayoutPanel;
import com.esferalia.aon.gwt.common.client.widget.Upload;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDateBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDockLayout;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomListBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomSuggestBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomSuggestOracle;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomTable;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomTextBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDateBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog.AonAcceptDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessagePanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonProgressBarDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTableButton;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarSmallButton;
import com.esferalia.aon.gwt.fiscal.client.EntryPointUtils;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.Customer;
import com.esferalia.aon.occam.api.model.ImportError;
import com.esferalia.aon.occam.api.model.Workplace;
import com.esferalia.aon.occam.api.model.fee.Fee;
import com.esferalia.aon.occam.api.model.finance.InvoicingGroup;
import com.esferalia.aon.occam.api.model.product.OldItem;
import com.esferalia.aon.occam.api.model.registry.CustomerFeeParams;
import com.esferalia.aon.occam.api.model.registry.CustomerFull;
import com.esferalia.aon.occam.api.model.registry.Project;
import com.esferalia.aon.occam.api.model.registry.Segment;
import com.esferalia.aon.occam.api.model.registry.Seller;
import com.esferalia.aon.occam.api.model.type.BillingPeriod;
import com.esferalia.aon.watson.mutable.MutableInt;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.UrlBuilder;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DeckLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class CustomerFee  implements EntryPoint {
	
	// Services
	private static RegistryServiceAsync SERVICE;
	private static CommonServiceAsync COMMON_SERVICE;

	// Options Config
	private RegistryModuleOptions options;

	// Content
	private DeckLayoutPanel deckLayoutPanel;
	
	private AonCustomDockLayout feeDockLayout;

	private AonToolbarButton createButton;
	private AonToolbarButton duplicateValueButton;
	private AonToolbarButton addValueButton;
	private AonToolbarButton deleteFeeButton;
	private AonToolbarButton exportButton;
	private AonToolbarButton importButton;
	private AonToolbarButton customerButton;
	
	// Filter
	private AonCustomListBox monthListBox;
	private AonCustomListBox yearListBox;
	private AonCustomListBox periocityListBox;
	private AonCustomListBox customerStatusListBox;
	private AonCustomListBox segmentListBox;
	private AonCustomListBox startCompareLB;
	private AonCustomDateBox startDateBox;
	private AonCustomListBox endCompareLB;
	private AonCustomDateBox endDateBox;
	
	private AonCustomSuggestBox conceptSuggestBox;
	private AonCustomSuggestBox productCategorySuggestBox;
	private AonCustomSuggestBox productTagSuggestBox;
	private AonCustomTextBox quantityTextBox;
	private AonCustomTextBox priceTextBox;
	private AonCustomTextBox discountTextBox;

	private AonCustomSuggestBox sellerSuggestBox;
	private AonCustomSuggestBox workplaceSuggestBox;
	private AonCustomSuggestBox invoicingGroupSuggestBox;
	private AonCustomSuggestBox projectSuggestBox;
	
	private AonCustomListBox sort = new AonCustomListBox("Ordenar Por");
	private AonCustomListBox asc = new AonCustomListBox("Orden");
	
	// Fee Table
	private HTMLPanel container;
	private HTMLPanel messagePanel = new HTMLPanel("");
	
	private SimplePanel tableContainer;
	private ScrollPanel tableScrollPanel;
	private AonCustomTable tab;
	
	private int limit = 100;
	private final MutableInt offset = new MutableInt(0);
	private final MutableInt moreData = new MutableInt(0);
	private final MutableInt searchEnabled = new MutableInt( 0 );
	
	private int lastScrollPos = 0;

	private static enum COLS {
		  CHK(AonStringUtils.EMPTY					,"2rem"				,"max-width: 2rem; ") 
		, CUS("Cliente"								,"12rem"  			,"max-width: 12rem; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;")
		, STS("Estado"								,"5rem" 			,"max-width: 5rem; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;")
		, LIN("#"									,"2rem" 			,"max-width: 2rem; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;")
		, CON("Concepto"							,"-moz-available" 	,"white-space: nowrap; overflow: hidden; text-overflow: ellipsis;")
		, QUA("Cant."								,"4rem" 			,"max-width: 4rem; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;")
		, PRI("Precio"								,"4rem" 			,"max-width: 4rem; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;")
		, DIS("Dto."								,"3rem" 			,"max-width: 3rem; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;")
		, IMP("Importe"								,"6rem" 			,"max-width: 6rem; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;")
		, STA("F. Desde"							,"5rem" 			,"max-width: 5rem; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;")
		, BIL("F. Factur."							,"6rem" 			,"max-width: 6rem; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;")
		, END("F. Hasta"							,"5rem" 			,"max-width: 5rem; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;")
		, PER("Periodo"								,"6rem" 			,"max-width: 6rem; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;")
		, BUT(AonStringUtils.EMPTY					,"2.3rem" 			,"max-width: 2.3rem; ")
		;

		String headerLabel;
		String colWidth;
		String cellStyleClass;

		private COLS(String headerLabel,String colWidth,String cellStyleClass) {
			this.headerLabel = headerLabel;
			this.colWidth = colWidth;
			this.cellStyleClass = cellStyleClass;
		}
		public String getColWidth() {
			return colWidth;
		}
		public String getHeaderLabel() {
			return headerLabel;
		}
		public String getStyles() {
			return cellStyleClass;
		}
	}
	
	private static enum CUSTOMER_COLS {
		  CHK(AonStringUtils.EMPTY					,"2rem"				,"max-width: 2rem; ") 
		, LIN("#"									,"2rem" 			,"max-width: 2rem; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;")
		, CON("Concepto"							,"-moz-available" 	,"white-space: nowrap; overflow: hidden; text-overflow: ellipsis;")
		, QUA("Cant."								,"4rem" 			,"max-width: 4rem; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;")
		, PRI("Precio"								,"4rem" 			,"max-width: 4rem; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;")
		, DIS("Dto."								,"3rem" 			,"max-width: 3rem; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;")
		, IMP("Importe"								,"6rem" 			,"max-width: 6rem; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;")
		, STA("F. Desde"							,"5rem" 			,"max-width: 5rem; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;")
		, BIL("F. Factur."							,"6rem" 			,"max-width: 6rem; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;")
		, END("F. Hasta"							,"5rem" 			,"max-width: 5rem; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;")
		, PER("Periodo"								,"6rem" 			,"max-width: 6rem; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;")
		, PRO("Expediente"							,"7rem" 			,"max-width: 7rem; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;")
		, SEL("Agente"								,"7rem" 			,"max-width: 7rem; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;")
		, BUT(AonStringUtils.EMPTY					,"2.3rem" 			,"max-width: 3rem; ")
		;

		String headerLabel;
		String colWidth;
		String cellStyleClass;

		private CUSTOMER_COLS(String headerLabel,String colWidth,String cellStyleClass) {
			this.headerLabel = headerLabel;
			this.colWidth = colWidth;
			this.cellStyleClass = cellStyleClass;
		}
		public String getColWidth() {
			return colWidth;
		}
		public String getHeaderLabel() {
			return headerLabel;
		}
		public String getStyles() {
			return cellStyleClass;
		}
	}
	
	// Variables
	private CustomerFeeParams params;
	private Map<Integer, Fee> rowFees = new HashMap<>();
	private Map<Integer, AonTableButton> selectedItems = new HashMap<>();
	
	private boolean hasChange = false;
	
	private Date billingDate;
	
	private Map<String, OldItem> productSuggestions = new TreeMap<>();
	private Map<String, Integer> productCategorySuggestions = new TreeMap<>();
	private Map<String, Integer> productTagSuggestions = new TreeMap<>();
	
	private Map<String, Seller> sellerSuggestions = new TreeMap<>();
	private Map<String, Workplace> workplaceSuggestions = new TreeMap<>();
	private Map<String, InvoicingGroup> invoicingGroupSuggestions = new TreeMap<>();
	private Map<String, Project> projectSuggestions = new TreeMap<>();
	
	private DateTimeFormat formatDate = DateTimeFormat.getFormat("dd/MM/yyyy");
	private DateTimeFormat formatMonthDate = DateTimeFormat.getFormat("MMMM/yyyy");
	
	// CustomerWithoutFee
	CustomerWithoutFee customerWithoutFee;
	
	// Import fees
	private AonProgressBarDialog pbd;
	private LinkedList<String> verror = new LinkedList<>();
	private LinkedList<String> werror = new LinkedList<>();
	
	// Custome id from JS customer fee page (Portal)
	private Integer customerId = null;
	private Integer searchDomain = null;

	@Override
	public void onModuleLoad() {
		RootLayoutPanel root = RootLayoutPanel.get(getRootPanel() != null ? getRootPanel() : "rootPanel");
		options = new RegistryModuleOptions();
		options.setParentWidget(root);
		options.setDomainName(getCurrentDomainName());
		options.setDomain(getCurrentDomain());
		options.setUser(getCurrentUser());
		
		customerId = EntryPointUtils.getCustomer() > 0 ? EntryPointUtils.getCustomer() : null;
		searchDomain = getOfficeDomain() > 0 ? getOfficeDomain() : getCurrentDomain();
		
		removeCustomer();
		removeOfficeDomain();
		
		this.onModuleLoad(options);
	}

	public void onModuleLoad(final RegistryModuleOptions opt) {
		AON.ensureInjected();

		RegistryServiceAsync registryServiceRaw = GWT.create(RegistryService.class);
		SERVICE = new RegistryServiceAsyncDecorator(registryServiceRaw);

		CommonServiceAsync commonServiceRaw = GWT.create(CommonService.class);
		COMMON_SERVICE = new CommonServiceAsyncDecorator(commonServiceRaw);

		deckLayoutPanel = new DeckLayoutPanel();
		opt.getParentWidget().add(deckLayoutPanel);

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
							deckLayoutPanel.add(new Label(AON.MSG.loadError( " [Interno: " + caught.getMessage() + "]")));
						}
					});
		} else {
			loadModule(opt);
		}
	}

	private void loadModule(final RegistryModuleOptions opt) {
		initializeCustomerFeePanel(opt);
		initializeCustomerPanel(opt);
	}

	private void initializeCustomerPanel(final RegistryModuleOptions opt) {
		customerWithoutFee = new CustomerWithoutFee(opt) {
			@Override
			protected void showCustomerFee() {
				showCustomerFeeTable();
			}
		};
		deckLayoutPanel.add(customerWithoutFee);
	}

	private void initializeCustomerFeePanel(final RegistryModuleOptions opt) {
		feeDockLayout = new AonCustomDockLayout("Panel Facturaci\u00f3n de Cuotas") {
			
			@Override
			protected void onClearFilter() {
				if(!isCustomer()) 
					getSearchTextBox().setValue(null, false);
				
				monthListBox.setValue("");
				yearListBox.setValue("");
				periocityListBox.setValue("");
				
				if(!isCustomer()) {
					customerStatusListBox.setValue("");
					if(null != segmentListBox)
						segmentListBox.setValue("");
				}
				startCompareLB.setValue("0");
				startDateBox.setValue(null);
				endCompareLB.setValue("0");
				endDateBox.setValue(null);
				
				conceptSuggestBox.setValue(null);
				productCategorySuggestBox.setValue(null);
				productTagSuggestBox.setValue(null);
				quantityTextBox.setValue(null);
				priceTextBox.setValue(null);
				discountTextBox.setValue(null);
				sellerSuggestBox.setValue(null);
				workplaceSuggestBox.setValue(null);
				invoicingGroupSuggestBox.setValue(null);
				projectSuggestBox.setValue(null);
				
				resetSearchOffset();
				onSearch();
			}
		};
		
		feeDockLayout.hideToolbarFilterMessages();
		feeDockLayout.setSearchPlaceholder("Clientes: busque por nombre, nif o alias");
		if(isCustomer()) feeDockLayout.showSeachButton();
		feeDockLayout.addKeyUpHandler(e -> {
			String value = feeDockLayout.getSearchTextBox().getValue();
			if(AonStringUtils.isNotBlank(value) && value.length() > 2) {
				onSearch();
			} else if(AonStringUtils.isBlank(value)) {
				onSearch();
			}
		});
		
		createToolbar();
		
		createFilterPanel(opt, new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {
				container.add(new Label(AON.MSG.loadError( " [Interno: " + caught.getMessage() + "]")));
			}

			@Override
			public void onSuccess(Void result) {
				container = new HTMLPanel("");
				container.addStyleName(AON.CSS.aonFlexColumn());
				
				container.add(messagePanel);
			
				tableContainer = new SimpleLayoutPanel();
				tableContainer.setHeight("100%");
				tableContainer.getElement().getStyle().setProperty("margin-left", "1rem");
				
				container.add(tableContainer);
				
				feeDockLayout.add(container);
				
				showCustomerFeeTable();
			}
			
		});
		
		deckLayoutPanel.add(feeDockLayout);
	}

	private void createFilterPanel(final RegistryModuleOptions opt, AsyncCallback<Void> endCallback) {
		// Period
		HTMLPanel periodItemPanel = new HTMLPanel("");
		periodItemPanel.addStyleName(AON.CSS.aonItemFlex());

		monthListBox = createMonthListBox("F. Facturaci\u00f3n");
		monthListBox.addChangeHandler(e -> onSearch());
		
		createYearListBox(lb -> {
			yearListBox = lb;
			yearListBox.addChangeHandler(e -> onSearch());
			periodItemPanel.add(yearListBox);
			endCallback.onSuccess(null);
		});
		
		periodItemPanel.add(monthListBox);
		
		feeDockLayout.addFilterWidget(periodItemPanel);
		
		// Periodicity
		periocityListBox = createPeriodicityListBox("Periodicidad");
		periocityListBox.addChangeHandler(e -> onSearch());
		feeDockLayout.addFilterWidget(periocityListBox);

		// Customer
		if(!isCustomer()) {
			createCustomerStatusListBox();
			feeDockLayout.addFilterWidget(customerStatusListBox);
			
			// Segment
			if (opt.getConfiguration().hasSegments()) {
				segmentListBox = new AonCustomListBox("Segmento");
				segmentListBox.addItem("-", "");
				segmentListBox.addItem("SIN SEGMENTO", "-1");
				
				for (Segment ea : opt.getConfiguration().getSegments()) {
					segmentListBox.addItem(ea.getName(), AonNumberUtils.toString( ea.getId()));
				}
				segmentListBox.addChangeHandler(event -> onSearch());
				
				feeDockLayout.addFilterWidget(segmentListBox);
			}
		}
		
		// Dates
		HTMLPanel datesPanel = new HTMLPanel("");
		datesPanel.addStyleName(AON.CSS.aonItemFlex());
		
		startCompareLB = createCompareListBox("F. Ini");
		startCompareLB.setWidth("3rem");
		startCompareLB.addChangeHandler(e -> { if(null != startDateBox.getValue()) onSearch(); });
		datesPanel.add(startCompareLB);
		
		startDateBox = new AonCustomDateBox("");
		startDateBox.addValueChangeHandler(e -> onSearch());
		datesPanel.add(startDateBox);
		
		endCompareLB = createCompareListBox("F. Fin");
		endCompareLB.setWidth("3rem");
		endCompareLB.addChangeHandler(e -> { if(null != endDateBox.getValue()) onSearch(); });
		datesPanel.add(endCompareLB);
		
		endDateBox = new AonCustomDateBox("");
		endDateBox.addValueChangeHandler(e -> onSearch());
		datesPanel.add(endDateBox);
		
		feeDockLayout.addFilterWidget(datesPanel);
		
		
		// Product
		createConceptSuggestBox();
		feeDockLayout.addFilterWidget(conceptSuggestBox);
		
		// Product Category
		createProductCategorySuggestBox();
		feeDockLayout.addFilterWidget(productCategorySuggestBox);
		
		// Product Tag
		createProductTagSuggestBox();
		feeDockLayout.addFilterWidget(productTagSuggestBox);
		
		// Quantity
		quantityTextBox = new AonCustomTextBox("Cantidad");
		quantityTextBox.addValueChangeHandler(e -> onSearch());
		feeDockLayout.addFilterWidget(quantityTextBox);

		// Price
		priceTextBox = new AonCustomTextBox("Precio");
		priceTextBox.addValueChangeHandler(e -> onSearch());
		feeDockLayout.addFilterWidget(priceTextBox);
		
		// Discount
		discountTextBox = new AonCustomTextBox("Descuento");
		discountTextBox.addValueChangeHandler(e -> onSearch());
		feeDockLayout.addFilterWidget(discountTextBox);
		
		// Seller
		createSellerSuggestBox();
		feeDockLayout.addFilterWidget(sellerSuggestBox);
		
		// Workplace
		createWorkplaceSuggestBox();
		feeDockLayout.addFilterWidget(workplaceSuggestBox);
		
		// Invoicing Group
		createInvoicingGroupSuggestBox();
		feeDockLayout.addFilterWidget(invoicingGroupSuggestBox);
		
		// Project
		createProjectSuggestBox();
		feeDockLayout.addFilterWidget(projectSuggestBox);
		
		sort.addItem("Cliente", "name");
		sort.addItem("F. Facturaci\u00f3n", "date");
		sort.getListBox().addChangeHandler(event -> onSearch());
		
		asc.addItem("Ascendente", "true");
		asc.addItem("Descendete", "false");
		asc.getListBox().addChangeHandler(event -> onSearch());
		
		feeDockLayout.addSortWidget(sort);
		feeDockLayout.addSortWidget(asc);
	}
	
	private AonCustomListBox createMonthListBox(String title) {
		AonCustomListBox lb = new AonCustomListBox(title);
		
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

	private void createYearListBox(Consumer<AonCustomListBox> consumer) {
		SERVICE.getMinMaxCustomerFeeYear(options.getDomainName(), options.getDomain(), options.getUser(), searchDomain, new AsyncCallback<Map<Integer, Integer>>() {

			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onSuccess(Map<Integer, Integer> minMaxYear) {
				Optional<Entry<Integer, Integer>> firstEntry = minMaxYear.entrySet().stream().findFirst();
				
				AonCustomListBox lb = new AonCustomListBox("A\u00f1o");
				lb.addItem("-", "");
				
				if(firstEntry.isPresent()) {
					Integer minYearEntry = firstEntry.get().getKey();
					Integer maxYearEntry = firstEntry.get().getValue();
					
					Integer itYear = maxYearEntry;
					
					while(itYear >= minYearEntry) {
						lb.addItem(itYear.toString(), (itYear - 1900) + "");
						itYear--;
					}
				} 
				
				consumer.accept(lb);
			}
			
		});
	}
	
	private AonCustomListBox createPeriodicityListBox(String title) {
		AonCustomListBox lb = new AonCustomListBox(title);
		lb.addItem("-", "");
		lb.addItem("Sin periodo", "0");
		lb.addItem("Mensual", "1");
		lb.addItem("Bimensual", "2");
		lb.addItem("Trimestral", "3");
		lb.addItem("Cuatrimestral", "4");
		lb.addItem("Semestral", "5");
		lb.addItem("Anual", "6");
		
		return lb;
	}
	
	private AonCustomListBox createCompareListBox(String title) {
		AonCustomListBox lb = new AonCustomListBox(title);
		lb.addItem("=", "0");
		lb.addItem("\u2264", "1");
		lb.addItem("\u2265", "2");
		return lb;
	}
	
	// ------- CUSTOMER ---------
	
	private void createCustomerStatusListBox() {
		customerStatusListBox = new AonCustomListBox("Estado Cliente");
		customerStatusListBox.addItem("-", "");
		customerStatusListBox.addItem("Activo", "0");
		customerStatusListBox.addItem("Inactivo", "1");
		customerStatusListBox.addItem("Bloqueado", "2");
		customerStatusListBox.addChangeHandler(e -> onSearch());
	}
	
	// ------- PRODUCT ---------

	private void createConceptSuggestBox() {
		conceptSuggestBox = new AonCustomSuggestBox("Concepto", new AonCustomSuggestOracle());
		conceptSuggestBox.setAutoSelectEnabled(false);
		conceptSuggestBox.setLimit(50);
		conceptSuggestBox.setPlaceHolder("Producto: busque por c\u00f3digo o descripci\u00f3n (Ctrl + espacio para sugerencias)");

		conceptSuggestBox.addSelectionHandler(e -> {
			conceptSuggestBox.hideSuggestionList();
			onSearch();
		});

		conceptSuggestBox.addRemoteSuggestionsHandler(3, this::getProductsSuggestion);
	}
	
	private void getProductsSuggestion(String productQuery) {
		SERVICE.getProductsSuggestion(options.getDomainName(), options.getDomain(), options.getUser(), searchDomain, productQuery, new AsyncCallback<Map<String, OldItem>>() {

			@Override
			public void onSuccess(Map<String, OldItem> productSuggestionsDB) {
				productSuggestions = productSuggestionsDB;

				conceptSuggestBox.setSuggestions(productSuggestions.keySet());
				conceptSuggestBox.showSuggestionList();
			}

			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
			}

		});
	}
	
	private void createProductCategorySuggestBox() {
		productCategorySuggestBox = new AonCustomSuggestBox("Categoria", new AonCustomSuggestOracle());
		productCategorySuggestBox.setAutoSelectEnabled(false);
		productCategorySuggestBox.setLimit(50);
		productCategorySuggestBox.setPlaceHolder("Categoria: busque por descripci\u00f3n (Ctrl + espacio para sugerencias)");

		productCategorySuggestBox.addSelectionHandler(e -> {
			productCategorySuggestBox.hideSuggestionList();
			onSearch();
		});

		productCategorySuggestBox.addRemoteSuggestionsHandler(3, this::getProductCategoriesSuggestion);
	}

	private void getProductCategoriesSuggestion(String productCategoryQuery) {
		SERVICE.getProductCategoriesSuggestion(options.getDomainName(), options.getDomain(), options.getUser(), searchDomain, productCategoryQuery, new AsyncCallback<Map<String, Integer>>() {

			@Override
			public void onSuccess(Map<String, Integer> productCategorySuggestionsDB) {
				productCategorySuggestions = productCategorySuggestionsDB;

				productCategorySuggestBox.setSuggestions(productCategorySuggestions.keySet());
				productCategorySuggestBox.showSuggestionList();
			}

			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
			}

		});
	}
	
	private void createProductTagSuggestBox() {
		productTagSuggestBox = new AonCustomSuggestBox("Etiqueta", new AonCustomSuggestOracle());
		productTagSuggestBox.setAutoSelectEnabled(false);
		productTagSuggestBox.setLimit(50);
		productTagSuggestBox.setPlaceHolder("Etiqueta: busque por descripci\u00f3n (Ctrl + espacio para sugerencias)");

		productTagSuggestBox.addSelectionHandler(e -> {
			productTagSuggestBox.hideSuggestionList();
			onSearch();
		});

		productTagSuggestBox.addRemoteSuggestionsHandler(3, this::getProductTagsSuggestion);
	}

	private void getProductTagsSuggestion(String productTagQuery) {
		SERVICE.getProductTagsSuggestion(options.getDomainName(), options.getDomain(), options.getUser(), searchDomain, productTagQuery, new AsyncCallback<Map<String, Integer>>() {

			@Override
			public void onSuccess(Map<String, Integer> productTagSuggestionsDB) {
				productTagSuggestions = productTagSuggestionsDB;

				productTagSuggestBox.setSuggestions(productTagSuggestions.keySet());
				productTagSuggestBox.showSuggestionList();
			}

			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
			}

		});
	}
	
	// ------- SELLER ---------
	
	private void createSellerSuggestBox() {
		sellerSuggestBox = new AonCustomSuggestBox("Comercial", new AonCustomSuggestOracle());
		sellerSuggestBox.setAutoSelectEnabled(false);
		sellerSuggestBox.setLimit(50);
		sellerSuggestBox.setPlaceHolder("Comercial: busque descripci\u00f3n (Ctrl + espacio para sugerencias)");

		sellerSuggestBox.addSelectionHandler(e -> {
			sellerSuggestBox.hideSuggestionList();
			onSearch();
		});

		sellerSuggestBox.addRemoteSuggestionsHandler(3, this::getSellerSuggestion);
	}

	private void getSellerSuggestion(String sellerQuery) {
		SERVICE.getSellersSuggestion(options.getDomainName(), options.getDomain(), options.getUser(), searchDomain, sellerQuery, new AsyncCallback<Map<String, Seller>>() {

			@Override
			public void onSuccess(Map<String, Seller> sellerSuggestionsDB) {
				sellerSuggestions = sellerSuggestionsDB;

				sellerSuggestBox.setSuggestions(sellerSuggestions.keySet());
				sellerSuggestBox.showSuggestionList();
			}

			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
			}

		});
	}
	
	// ------- WORKPLACE ---------
	
	private void createWorkplaceSuggestBox() {
		workplaceSuggestBox = new AonCustomSuggestBox("C. Trabajo", new AonCustomSuggestOracle());
		workplaceSuggestBox.setAutoSelectEnabled(false);
		workplaceSuggestBox.setLimit(50);
		workplaceSuggestBox.setPlaceHolder("C. Trabajo: busque descripci\u00f3n (Ctrl + espacio para sugerencias)");

		workplaceSuggestBox.addSelectionHandler(e -> {
			workplaceSuggestBox.hideSuggestionList();
			onSearch();
		});

		workplaceSuggestBox.addRemoteSuggestionsHandler(3, this::getWorkplaceSuggestion);
	}

	private void getWorkplaceSuggestion(String workplaceQuery) {
		SERVICE.getWorkplacesSuggestion(options.getDomainName(), options.getDomain(), options.getUser(), searchDomain, workplaceQuery, new AsyncCallback<Map<String, Workplace>>() {

			@Override
			public void onSuccess(Map<String, Workplace> workplaceSuggestionsDB) {
				workplaceSuggestions = workplaceSuggestionsDB;

				workplaceSuggestBox.setSuggestions(workplaceSuggestions.keySet());
				workplaceSuggestBox.showSuggestionList();
			}

			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
			}

		});
	}
	
	// ------- INVOICING GROUP ---------
	
	private void createInvoicingGroupSuggestBox() {
		invoicingGroupSuggestBox = new AonCustomSuggestBox("G. Facturaci\u00f3n", new AonCustomSuggestOracle());
		invoicingGroupSuggestBox.setAutoSelectEnabled(false);
		invoicingGroupSuggestBox.setLimit(50);
		invoicingGroupSuggestBox.setPlaceHolder("G. Facturaci\u00f3n: busque descripci\u00f3n (Ctrl + espacio para sugerencias)");

		invoicingGroupSuggestBox.addSelectionHandler(e -> {
			invoicingGroupSuggestBox.hideSuggestionList();
			onSearch();
		});

		invoicingGroupSuggestBox.addRemoteSuggestionsHandler(3, this::getInvoicingGroupQuerySuggestion);
	}

	private void getInvoicingGroupQuerySuggestion(String invoicingGroupQuery) {
		SERVICE.getInvoicingGroupsSuggestion(options.getDomainName(), options.getDomain(), options.getUser(), searchDomain, invoicingGroupQuery, new AsyncCallback<Map<String, InvoicingGroup>>() {

			@Override
			public void onSuccess(Map<String, InvoicingGroup> invoicingGroupSuggestionsDB) {
				invoicingGroupSuggestions = invoicingGroupSuggestionsDB;

				invoicingGroupSuggestBox.setSuggestions(invoicingGroupSuggestions.keySet());
				invoicingGroupSuggestBox.showSuggestionList();
			}

			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
			}

		});
	}
	
	// ------- PROJECT ---------
	
	private void createProjectSuggestBox() {
		projectSuggestBox = new AonCustomSuggestBox("Proyecto", new AonCustomSuggestOracle());
		projectSuggestBox.setAutoSelectEnabled(false);
		projectSuggestBox.setLimit(50);
		projectSuggestBox.setPlaceHolder("Proyecto: busque descripci\u00f3n (Ctrl + espacio para sugerencias)");

		projectSuggestBox.addSelectionHandler(e -> {
			projectSuggestBox.hideSuggestionList();
			onSearch();
		});

		projectSuggestBox.addRemoteSuggestionsHandler(3, this::getProjectQuerySuggestion);
	}

	private void getProjectQuerySuggestion(String projectQuery) {
		SERVICE.getProjectsSuggestion(options.getDomainName(), options.getDomain(), options.getUser(), null, searchDomain, projectQuery, new AsyncCallback<Map<String, Project>>() {

			@Override
			public void onSuccess(Map<String, Project> projectSuggestionsDB) {
				projectSuggestions = projectSuggestionsDB;

				projectSuggestBox.setSuggestions(projectSuggestions.keySet());
				projectSuggestBox.showSuggestionList();
			}

			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
			}

		});
	}
	
	// ------- SEARCH ---------
	
	private void onSearch() {
		AonMessagePanel.showLoading(messagePanel, "Cargando panel de facturaci\u00f3n ...");
		getWidgetParams();
		resetSearchOffset();
		onSearchData();
		AonMessagePanel.hideMessage(messagePanel);
	}
	
	private void resetSearchOffset() {
		offset.setValue(0);
	}
	
	public boolean isSearchEnabled() {
		return (searchEnabled.getValue() == 0 );
	}
	public void disableSearch() {
		searchEnabled.setValue(-1);
	}
	public void enableSearch() {
		searchEnabled.setValue(0);
	}
	public boolean isMoreData() {
		return (moreData.getValue() == 0 );
	}
	public void disableMoreData() {
		moreData.setValue(-1);
	}
	public void enableMoreData() {
		moreData.setValue(0);
	}
	
	private void onSearchData() {
		enableMoreData();
		searchData();
	}
	
	private void searchData() {
		tableContainer.clear();
		tab = new AonCustomTable();
		tableScrollPanel = new ScrollPanel(tab);
		
		tableScrollPanel.addScrollHandler(e -> {
			// ------------------------------------ Ignore scroll up.
			int oldScrollPos = lastScrollPos;
			lastScrollPos = tableScrollPanel.getVerticalScrollPosition();
			if (oldScrollPos >= lastScrollPos) {
				return;
			}
			// -----------------------------------------------------
			if (isSearchEnabled()) {
				int maxScrollTop = tableScrollPanel.getWidget().getOffsetHeight() - tableScrollPanel.getOffsetHeight();
				if (lastScrollPos >= maxScrollTop) {
					disableSearch();
					searchDataList();
				}
			}
		});
		
		paintHeader();
		tableContainer.add(tableScrollPanel);
		searchDataList();
	}
	
	private void paintHeader() {
		tab.createHeader();
		if(!isCustomer()) {
			for ( COLS col : COLS.values()) 
				if(col == COLS.CHK) {
					AonTableButton checkAllButton = new AonTableButton(AON.MSG.selectAction(), AON.CSS.aonIconCheck());
					checkAllButton.addClickHandler(e -> {
						List<AonTableButton> selectedItemList = selectedItems.values().stream().filter(check -> AonStringUtils.containsIgnoreCase(check.getStyleName(), AON.CSS.aonIconChecked())).collect(Collectors.toList());
						if (selectedItemList.size() == rowFees.size() || AonStringUtils.containsIgnoreCase(checkAllButton.getStyleName(), AON.CSS.aonIconChecked())) {
							checkAllButton.addStyleName(AON.CSS.aonIconCheck());
							checkAllButton.removeStyleName(AON.CSS.aonIconChecked());
							selectedItems.values().forEach(check ->{
								check.addStyleName(AON.CSS.aonIconCheck());
								check.removeStyleName(AON.CSS.aonIconChecked());
							});
						} else {
							checkAllButton.addStyleName(AON.CSS.aonIconChecked());
							checkAllButton.removeStyleName(AON.CSS.aonIconCheck());
							selectedItems.values().forEach(check ->{
								check.addStyleName(AON.CSS.aonIconChecked());
								check.removeStyleName(AON.CSS.aonIconCheck());
							});
						}
						
						duplicateValueButton.setEnabled(selectedItemList.isEmpty());
						addValueButton.setEnabled(selectedItemList.isEmpty());
						deleteFeeButton.setEnabled(selectedItemList.isEmpty());
						exportButton.setEnabled(selectedItemList.isEmpty());
					});
					
					tab.addHeader(checkAllButton, col.getColWidth());
				} else tab.addHeader(new Label(col.getHeaderLabel()), col.getColWidth(), col.getStyles());
		} else {
			for ( CUSTOMER_COLS col : CUSTOMER_COLS.values()) 
				if(col == CUSTOMER_COLS.CHK) {
					AonTableButton checkAllButton = new AonTableButton(AON.MSG.selectAction(), AON.CSS.aonIconCheck());
					checkAllButton.addClickHandler(e -> {
						List<AonTableButton> selectedItemList = selectedItems.values().stream().filter(check -> AonStringUtils.containsIgnoreCase(check.getStyleName(), AON.CSS.aonIconChecked())).collect(Collectors.toList());
						if (selectedItemList.size() == rowFees.size() || AonStringUtils.containsIgnoreCase(checkAllButton.getStyleName(), AON.CSS.aonIconChecked())) {
							checkAllButton.addStyleName(AON.CSS.aonIconCheck());
							checkAllButton.removeStyleName(AON.CSS.aonIconChecked());
							selectedItems.values().forEach(check ->{
								check.addStyleName(AON.CSS.aonIconCheck());
								check.removeStyleName(AON.CSS.aonIconChecked());
							});
						} else {
							checkAllButton.addStyleName(AON.CSS.aonIconChecked());
							checkAllButton.removeStyleName(AON.CSS.aonIconCheck());
							selectedItems.values().forEach(check ->{
								check.addStyleName(AON.CSS.aonIconChecked());
								check.removeStyleName(AON.CSS.aonIconCheck());
							});
						}
						
						duplicateValueButton.setEnabled(selectedItemList.isEmpty());
						addValueButton.setEnabled(selectedItemList.isEmpty());
						deleteFeeButton.setEnabled(selectedItemList.isEmpty());
						exportButton.setEnabled(selectedItemList.isEmpty());
					});
					
					tab.addHeader(checkAllButton, col.getColWidth());
				} else tab.addHeader(new Label(col.getHeaderLabel()), col.getColWidth(), col.getStyles());
		}
	}
	
	private void searchDataList() {
		if (!isMoreData()) return;
		
		params.setOffset(offset.intValue());
		params.setLimit(limit);
		
		getList(fees -> {
			boolean something = false;
			
			for( Fee fee : fees) {
				something = true;
				paintRow(fee);
			}
			
			if (fees.size() < limit) {
				disableMoreData();
			} else {
				offset.setValue(offset.intValue() + fees.size() - 1);
				enableMoreData();
			}
			
			if (!something) {
				FlowPanel line = new FlowPanel();
				InlineLabel label = new InlineLabel(AON.MSG.noData());
				line.add(label);
				tableContainer.clear();
				tableContainer.add(line);
				disableMoreData();
			}
			enableSearch();
			
		});
	}

	private void getWidgetParams() {
		if(null == params) params = new CustomerFeeParams();
		params.setDomain(searchDomain);
		params.setSeller(1);
		
		params.setMonth(AonStringUtils.isBlank(monthListBox.getValue()) ? null : Integer.parseInt(monthListBox.getValue()));
		params.setYear(AonStringUtils.isBlank(yearListBox.getValue()) ? null : Integer.parseInt(yearListBox.getValue()));
		params.setPeriodicity(AonStringUtils.isBlank(periocityListBox.getValue()) ? null : Byte.parseByte(periocityListBox.getValue()));
		
		if(isCustomer()) params.setCustomer(customerId);
		else params.setDescription(feeDockLayout.getSearchTextBox().getValue());
		
		if(!isCustomer()) params.setCustomerStatus(AonStringUtils.isBlank(customerStatusListBox.getValue()) ? null : Byte.parseByte(customerStatusListBox.getValue()));
		if(!isCustomer()) params.setSegment(segmentListBox != null && !AonStringUtils.isBlank(segmentListBox.getValue()) ? AonNumberUtils.toInteger( segmentListBox.getValue()) : null);
		params.setStartCompare(Byte.parseByte(startCompareLB.getValue()));
		params.setStartDate(startDateBox.getValue());
		params.setEndCompare(Byte.parseByte(endCompareLB.getValue()));
		params.setEndDate(endDateBox.getValue());
		
		params.setProduct(null != productSuggestions.get(conceptSuggestBox.getValue()) ? productSuggestions.get(conceptSuggestBox.getValue()).getId() : null);
		params.setProductCategory(null != productCategorySuggestions.get(productCategorySuggestBox.getValue()) ? productCategorySuggestions.get(productCategorySuggestBox.getValue()) : null);
		params.setProductTag(null != productTagSuggestions.get(productTagSuggestBox.getValue()) ? productTagSuggestions.get(productTagSuggestBox.getValue()) : null);
		params.setQuantity(quantityTextBox.getValue());
		params.setPrice(priceTextBox.getValue());
		params.setDiscount(discountTextBox.getValue());
		
		params.setSeller(null != sellerSuggestions.get(sellerSuggestBox.getValue()) ? sellerSuggestions.get(sellerSuggestBox.getValue()).getId() : null);
		params.setWorkplace(null != workplaceSuggestions.get(workplaceSuggestBox.getValue()) ? workplaceSuggestions.get(workplaceSuggestBox.getValue()).getDescription() : null);
		params.setInvoicingGroup(null != invoicingGroupSuggestions.get(invoicingGroupSuggestBox.getValue()) ? invoicingGroupSuggestions.get(invoicingGroupSuggestBox.getValue()).getDescription() : null);
		params.setProject(null != projectSuggestions.get(projectSuggestBox.getValue()) ? projectSuggestions.get(projectSuggestBox.getValue()).getId() : null);
		
		params.setLimit(limit);
		params.setOffset(offset.getValue());
	}
	
	private void paintRow(Fee fee) {
		HTMLPanel row = tab.createRow();
		row.addDomHandler(e -> {
			e.stopPropagation();
			if(!isCustomer()) {
				
				Integer originalSeller = null == fee.getSeller() ? null : fee.getSeller().getId();
				
				new CustomerFeeDialog(fee, options, searchDomain) {
					
					@Override
					protected void onAccept(Fee fee) {
						LinkedList<Fee> fees = new LinkedList<Fee>();
						fee.setModify(true);
						fees.add(fee);
						
						SERVICE.saveCustomerFeeList(options.getDomainName(), options.getDomain(), options.getUser(), fees,
								new AsyncCallback<Integer>() {

									@Override
									public void onFailure(Throwable caught) {
										AonMessagePanel.showError(messagePanel, "Error guardando panel de facturaci\u00f3n: " + caught.getMessage());
									}

									@Override
									public void onSuccess(Integer updates) {
										AonMessagePanel.showSuccess(messagePanel, "Se han actualizado " + updates + " cuotas correctamente");
										duplicateValueButton.setEnabled(false);
										addValueButton.setEnabled(false);
										deleteFeeButton.setEnabled(false);
										exportButton.setEnabled(false);
										onSearch();
										
										if(null != originalSeller) {
											
											// Desasignar
											if(null == fee.getSeller() || null == fee.getSeller().getId() || !originalSeller.equals(fee.getSeller().getId()))
												sendFeeEmail(fee.getId(), false, originalSeller);
											
											if(null != fee.getSeller() && null != fee.getSeller().getId() && !originalSeller.equals(fee.getSeller().getId()))
												sendFeeEmail(fee.getId(), true, null);
											
										} else if(null != fee.getSeller() && null != fee.getSeller().getId())
											sendFeeEmail(fee.getId(), true, null);
									}
								});
					}

					@Override
					protected void onAccept(Optional<OldItem> item, Optional<Double> price, Optional<String> discountExpr, Optional<Date> startDate, Optional<Date> endDate, Optional<Date> billingDate) {}

					@Override
					protected void onCreate(Fee fee) {}

					@Override
					protected void onCreate(Fee fee, Integer ritem) {
						// TODO Auto-generated method stub
						
					}
					
				};
			} else {
				getCustomer(customer -> {
					Integer originalSeller = null == fee.getSeller() ? null : fee.getSeller().getId();
					
					new CustomerFeeDialog(fee, customer, options, searchDomain) {
						
						@Override
						protected void onAccept(Fee fee) {
							LinkedList<Fee> fees = new LinkedList<Fee>();
							fee.setModify(true);
							fees.add(fee);
							
							SERVICE.saveCustomerFeeList(options.getDomainName(), options.getDomain(), options.getUser(), fees,
									new AsyncCallback<Integer>() {

										@Override
										public void onFailure(Throwable caught) {
											AonMessagePanel.showError(messagePanel, "Error guardando panel de facturaci\u00f3n: " + caught.getMessage());
										}

										@Override
										public void onSuccess(Integer updates) {
											AonMessagePanel.showSuccess(messagePanel, "Se han actualizado " + updates + " cuotas correctamente");
											duplicateValueButton.setEnabled(false);
											addValueButton.setEnabled(false);
											deleteFeeButton.setEnabled(false);
											exportButton.setEnabled(false);
											onSearch();
											
											if(null != originalSeller) {
												
												// Desasignar
												if(null == fee.getSeller() || null == fee.getSeller().getId() || !originalSeller.equals(fee.getSeller().getId()))
													sendFeeEmail(fee.getId(), false, originalSeller);
												
												if(null != fee.getSeller() && null != fee.getSeller().getId() && !originalSeller.equals(fee.getSeller().getId()))
													sendFeeEmail(fee.getId(), true, null);
												
											} else if(null != fee.getSeller() && null != fee.getSeller().getId())
												sendFeeEmail(fee.getId(), true, null);
										}
									});
						}

						@Override
						protected void onAccept(Optional<OldItem> item, Optional<Double> price, Optional<String> discountExpr, Optional<Date> startDate, Optional<Date> endDate, Optional<Date> billingDate) {}

						@Override
						protected void onCreate(Fee fee) {}

						@Override
						protected void onCreate(Fee fee, Integer ritem) {
							// TODO Auto-generated method stub
							
						}
						
					};
				});
				
			}
			
		}, ClickEvent.getType());
		
		AonTableButton checkButton = new AonTableButton(AON.MSG.selectAction(), AON.CSS.aonIconCheck());
		checkButton.addClickHandler(e -> {
			e.stopPropagation();
			if (AonStringUtils.containsIgnoreCase(checkButton.getStyleName(), AON.CSS.aonIconChecked())) {
				checkButton.addStyleName(AON.CSS.aonIconCheck());
				checkButton.removeStyleName(AON.CSS.aonIconChecked());
			} else {
				checkButton.addStyleName(AON.CSS.aonIconChecked());
				checkButton.removeStyleName(AON.CSS.aonIconCheck());
			}
			List<AonTableButton> selectedItemList = selectedItems.values().stream().filter(check -> AonStringUtils.containsIgnoreCase(check.getStyleName(), AON.CSS.aonIconChecked())).collect(Collectors.toList());
			
			duplicateValueButton.setEnabled(!selectedItemList.isEmpty());
			addValueButton.setEnabled(!selectedItemList.isEmpty());
			deleteFeeButton.setEnabled(!selectedItemList.isEmpty());
			exportButton.setEnabled(!selectedItemList.isEmpty());
		});
		tab.addRow(row, checkButton, COLS.CHK.getColWidth());
		
		if(!isCustomer()) {
			Label name = new Label(fee.getCustomer().getName());
			name.setTitle(fee.getCustomer().getName());
			tab.addInlineStyle(name, COLS.CUS.getStyles());
			tab.addRow(row, name, COLS.CUS.getColWidth());
		}
		
		if(!isCustomer()) {
			String statusValue = null == fee.getCustomer().getStatus() ? "" : fee.getCustomer().getStatus().getDescription();
			Label status = new Label(statusValue);
			status.setTitle(statusValue);
			tab.addInlineStyle(status, COLS.STS.getStyles());
			tab.addRow(row, status, COLS.STS.getColWidth());
		}
		
		String lineValue = null == fee.getLine() ? "" : fee.getLine().toString();
		Label line = new Label(lineValue);
		line.setTitle(lineValue);
		tab.addInlineStyle(line, COLS.LIN.getStyles());
		tab.addRow(row, line, COLS.LIN.getColWidth());
		
		String conceptValue = fee.getDescription();
		Label concept = new Label(conceptValue);
		concept.setTitle(conceptValue);
		tab.addInlineStyle(concept, isCustomer() ?  CUSTOMER_COLS.CON.getStyles() : COLS.CON.getStyles());
		tab.addRow(row, concept, isCustomer() ?  CUSTOMER_COLS.CON.getColWidth() : COLS.CON.getColWidth());
		
		String quantityValue = null == fee.getQuantity() ? "" : fee.getQuantity().toString();
		Label quantity = new Label(quantityValue);
		quantity.setTitle(quantityValue);
		tab.addInlineStyle(quantity, COLS.QUA.getStyles());
		tab.addRow(row, quantity, COLS.QUA.getColWidth());
		
		String priceValue = null == fee.getPrice() ? "" : fee.getPrice().toString();
		Label price = new Label(priceValue);
		price.setTitle(priceValue);
		tab.addInlineStyle(price, COLS.PRI.getStyles());
		tab.addRow(row, price, COLS.PRI.getColWidth());
		
		String discountValue = null == fee.getDiscountExpr() ? "" : fee.getDiscountExpr();
		Label discount = new Label(discountValue);
		discount.setTitle(discountValue);
		tab.addInlineStyle(discount, COLS.DIS.getStyles());
		tab.addRow(row, discount, COLS.DIS.getColWidth());
		
		double totalValue = getTotalNetPrice(fee);
		Label total = new Label(totalValue + "");
		discount.setTitle(totalValue + "");
		tab.addInlineStyle(total, COLS.IMP.getStyles());
		tab.addRow(row, total, COLS.IMP.getColWidth());
		
		String startValue = null == fee.getStartDate() ? "" : formatDate.format(fee.getStartDate()) ;
		Label start = new Label(startValue);
		start.setTitle(startValue);
		tab.addInlineStyle(start, COLS.STA.getStyles());
		tab.addRow(row, start, COLS.STA.getColWidth());
		
		String billingValue = null == fee.getBillingDate() ? "" : formatMonthDate.format(fee.getBillingDate()) ;
		Label billing = new Label(billingValue);
		billing.setTitle(billingValue);
		tab.addInlineStyle(billing, COLS.BIL.getStyles());
		tab.addRow(row, billing, COLS.BIL.getColWidth());
		
		String endValue = null == fee.getEndDate() ? "" : formatDate.format(fee.getEndDate()) ;
		Label end = new Label(endValue);
		end.setTitle(endValue);
		tab.addInlineStyle(end, COLS.END.getStyles());
		tab.addRow(row, end, COLS.END.getColWidth());
		
		String periodValue = null == fee.getPeriod() ? "" : BillingPeriod.toString(fee.getPeriod());
		Label period = new Label(periodValue);
		period.setTitle(periodValue);
		tab.addInlineStyle(period, COLS.PER.getStyles());
		tab.addRow(row, period, COLS.PER.getColWidth());
		
		if(isCustomer()) {
			String projectValue = null == fee.getProject() ? "" : fee.getProject().getName();
			Label project = new Label(projectValue);
			project.setTitle(projectValue);
			tab.addInlineStyle(project, CUSTOMER_COLS.PRO.getStyles());
			tab.addRow(row, project, CUSTOMER_COLS.PRO.getColWidth());
			
			String sellerValue = null == fee.getSellerComercial() ? "" : fee.getSellerComercial().getName();
			Label seller = new Label(sellerValue);
			seller.setTitle(sellerValue);
			tab.addInlineStyle(seller, CUSTOMER_COLS.SEL.getStyles());
			tab.addRow(row, seller, CUSTOMER_COLS.SEL.getColWidth());
		}
		
		AonToolbarSmallButton infoBtn = new AonToolbarSmallButton("", AON.CSS.aonIconInfo());
		infoBtn.setTitle(createFeeInfo(fee));
		tab.addRow(row, infoBtn, COLS.BUT.getColWidth());
		
		rowFees.put(fee.getId(), fee);
		selectedItems.put(fee.getId(), checkButton);
	}
	
	private double getNetCost(Fee fee) {
		return fee.getPrice() * (1 - fee.getDiscount()/100);
	}
	 
	private double getTotalNetPrice(Fee fee) {
		return roundTwoDecimals( getNetCost(fee) * fee.getQuantity() );
	}
	
	private double roundTwoDecimals(double value) {
	    return Math.round(value * 100.0) / 100.0;
	}

	private void checkFeeStatus(Label label, AonDateBox startDateBox, AonDateBox endDateBox, Fee fee) {
		if(null != fee.getEndDate() && fee.getEndDate().before(fee.getBillingDate())) {
			label.getElement().getStyle().setFontWeight(FontWeight.BOLD);
			label.getElement().getStyle().setColor("red");
			label.setTitle("La fecha fin es anterior a la fecha de facturaci\u00f3n");
			
			endDateBox.getElement().getStyle().setColor("red");
			endDateBox.getElement().getStyle().setFontWeight(FontWeight.BOLD);
			endDateBox.setTitle("La fecha fin es anterior a la fecha de facturaci\u00f3n");
			
		} else if(null != fee.getStartDate() && fee.getStartDate().after(fee.getBillingDate())) {
			label.getElement().getStyle().setFontWeight(FontWeight.BOLD);
			label.getElement().getStyle().setColor("red");
			label.setTitle("La fecha inicion es posterior a la fecha de facturaci\u00f3n");
			
			startDateBox.getElement().getStyle().setColor("red");
			startDateBox.getElement().getStyle().setFontWeight(FontWeight.BOLD);
			startDateBox.setTitle("La fecha inicio es posterior a la fecha de facturaci\u00f3n");
			
		}
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

	// -------------------------------- TOOLBAR
	
	private void createToolbar() {
		createButton = new AonToolbarButton(AON.MSG.newAction(), AON.CSS.aonIconAdd());
		createButton.addClickHandler(e -> {
			
			if(isCustomer()) {
				
				getCustomer(customer -> {
					
					new CustomerFeeDialog(options, customer, searchDomain) {
						
						@Override
						protected void onCreate(Fee fee) {
							SERVICE.createCustomerFeeList(options.getDomainName(), options.getDomain(), options.getUser(), fee, new AsyncCallback<Fee>() {
								
								@Override
								public void onSuccess(Fee customerFee) {
									AonMessagePanel.showSuccess(messagePanel, "Se ha creado la cuota correctamente");
									duplicateValueButton.setEnabled(false);
									addValueButton.setEnabled(false);
									deleteFeeButton.setEnabled(false);
									exportButton.setEnabled(false);
									onSearch();
									
									if(null != fee.getPrice() && fee.getPrice() >= 0.00 && null != fee.getSeller() && null != fee.getSeller().getId())
										sendFeeEmail(customerFee.getId(), true, null);
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

						@Override
						protected void onCreate(Fee fee, Integer ritem) {
							// TODO Auto-generated method stub
							
						}
					};
					
				});
				
			} else {
			
				new CustomerFeeDialog(options, searchDomain) {
					
					@Override
					protected void onCreate(Fee fee) {
						SERVICE.createCustomerFeeList(options.getDomainName(), options.getDomain(), options.getUser(), fee, new AsyncCallback<Fee>() {
							
							@Override
							public void onSuccess(Fee customerFee) {
								AonMessagePanel.showSuccess(messagePanel, "Se ha creado la cuota correctamente");
								duplicateValueButton.setEnabled(false);
								addValueButton.setEnabled(false);
								deleteFeeButton.setEnabled(false);
								exportButton.setEnabled(false);
								onSearch();
								
								if(null != fee.getPrice() && fee.getPrice() >= 0.00 && null != fee.getSeller() && null != fee.getSeller().getId())
									sendFeeEmail(customerFee.getId(), true, null);
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

					@Override
					protected void onCreate(Fee fee, Integer ritem) {
						// TODO Auto-generated method stub
						
					}
				};
				
			}
			
		});
		feeDockLayout.addToolbarButton(createButton);
		
		duplicateValueButton = new AonToolbarButton("Duplicar Cuota", AON.CSS.aonIconCopy());
		duplicateValueButton.setEnabled(false);
		duplicateValueButton.addClickHandler(e -> {
			List<Entry<Integer, AonTableButton>> selectedItemList = selectedItems.entrySet().stream().filter(entry -> AonStringUtils.containsIgnoreCase(entry.getValue().getStyleName(), AON.CSS.aonIconChecked())).collect(Collectors.toList());
			if(selectedItemList.size() == 1) {
				Fee selectedFee = rowFees.get(selectedItemList.get(0).getKey());
				if(null != selectedFee) {
					Fee duplicateFee = new Fee();
					duplicateFee.duplicate(selectedFee);
					
					selectedItems.values().forEach(check ->{
						check.addStyleName(AON.CSS.aonIconCheck());
						check.removeStyleName(AON.CSS.aonIconChecked());
					});
					duplicateValueButton.setEnabled(false);
					addValueButton.setEnabled(false);
					deleteFeeButton.setEnabled(false);
					exportButton.setEnabled(false);
					
					CustomerFeeDialog feeDialog = new CustomerFeeDialog(duplicateFee, options, searchDomain) {
						
						@Override
						protected void onAccept(Fee fee) {
							SERVICE.createCustomerFeeList(options.getDomainName(), options.getDomain(), options.getUser(), fee, new AsyncCallback<Fee>() {
								
								@Override
								public void onSuccess(Fee customerFee) {
									AonMessagePanel.showSuccess(messagePanel, "Se ha creado la cuota correctamente");
									duplicateValueButton.setEnabled(false);
									addValueButton.setEnabled(false);
									deleteFeeButton.setEnabled(false);
									exportButton.setEnabled(false);
									onSearch();
									
									if(null != fee.getPrice() && fee.getPrice() >= 0.00 && null != fee.getSeller() && null != fee.getSeller().getId())
										sendFeeEmail(customerFee.getId(), true, null);
								}
								
								@Override
								public void onFailure(Throwable caught) {
									AonMessagePanel.showError(messagePanel, "Error creando cuota: " + caught.getMessage());
								}
							});
							
						}

						@Override
						protected void onAccept(Optional<OldItem> item, Optional<Double> price, Optional<String> discountExpr, Optional<Date> startDate, Optional<Date> endDate, Optional<Date> billingDate) {}

						@Override
						protected void onCreate(Fee fee) {}

						@Override
						protected void onCreate(Fee fee, Integer ritem) {
							// TODO Auto-generated method stub
							
						}
						
					};
					
					feeDialog.setCaption("Duplicar Cuota");
					
				}
			} else {
				new CustomerFeeDuplicateDialog() {
					
					@Override
					protected void onAccept(Double discount, Date startDate, Date endDate, Date billingDate, BillingPeriod period) {
						List<Entry<Integer, AonTableButton>> selectedItemList = selectedItems.entrySet().stream().filter(entry -> AonStringUtils.containsIgnoreCase(entry.getValue().getStyleName(), AON.CSS.aonIconChecked())).collect(Collectors.toList());
						
						if(selectedItemList.size() == rowFees.size()) {
							offset.setValue(0);
							limit = Integer.MAX_VALUE;
							getWidgetParams();
							
							getList(dbFees -> {
								LinkedList<Fee> updatedFees = new LinkedList<Fee>();
								
								dbFees.forEach(dbFee -> {
									Fee newFee = new Fee();
									newFee.duplicate(dbFee);
									
									newFee.setDiscount(discount);
									newFee.setStartDate(startDate);
									newFee.setEndDate(endDate);
									newFee.setBillingDate(billingDate);
									newFee.setPeriod(period);
									
									updatedFees.add(newFee);
								});
								
								createDuplicateFees(updatedFees, 0, 0);
							});
						} else {
							// Solo cuotas seleccionadas
							LinkedList<Integer> selectedFees = selectedItemList.stream()
									.filter(entry -> AonStringUtils.containsIgnoreCase(entry.getValue().getStyleName(), AON.CSS.aonIconChecked()))
									.map(entry -> entry.getKey())
									.collect(Collectors.toCollection(LinkedList::new));
							
							LinkedList<Fee> updatedFees = new LinkedList<Fee>();
							
							selectedFees.forEach(feeId -> {
								Fee feeIt = rowFees.get(feeId);
								
								Fee newFee = new Fee();
								newFee.duplicate(feeIt);
								
								newFee.setDiscount(discount);
								newFee.setStartDate(startDate);
								newFee.setEndDate(endDate);
								newFee.setBillingDate(billingDate);
								newFee.setPeriod(period);
								
								updatedFees.add(newFee);
							});
							
							createDuplicateFees(updatedFees, 0, 0);
						}
					}

					private void createDuplicateFees(LinkedList<Fee> updatedFees, int index, int accumulatedUpdates) {
						if (index >= updatedFees.size()) {
					       AonMessagePanel.showSuccess(messagePanel, "Se han actualizado " + accumulatedUpdates + " cuotas correctamente");
					       duplicateValueButton.setEnabled(false);
					       addValueButton.setEnabled(false);
					       deleteFeeButton.setEnabled(false);
					       exportButton.setEnabled(false);
					       onSearch();
					       return;
					    }

					    Fee currentFee = updatedFees.get(index);
					    
					    SERVICE.createCustomerFeeList(options.getDomainName(), options.getDomain(), options.getUser(), currentFee, new AsyncCallback<Fee>() {
							
							@Override
							public void onSuccess(Fee customerFee) {
								if(null != currentFee.getPrice() && currentFee.getPrice() >= 0.00 && null != currentFee.getSeller() && null != currentFee.getSeller().getId())
									sendFeeEmail(customerFee.getId(), true, null);
								
								createDuplicateFees(updatedFees, index + 1, accumulatedUpdates + 1);
							}
							
							@Override
							public void onFailure(Throwable caught) {
								AonMessagePanel.showError(messagePanel, "Error creando cuota: " + caught.getMessage());
								// Continuar aunque haya error
								createDuplicateFees(updatedFees, index + 1, accumulatedUpdates);
							}
						});
						
					    
					}
				};
			}
		});
		feeDockLayout.addToolbarButton(duplicateValueButton);
		
		addValueButton = new AonToolbarButton("Editar Cuota", AON.CSS.aonIconEdit());
		addValueButton.setEnabled(false);
		addValueButton.addClickHandler(e -> {
			List<Entry<Integer, AonTableButton>> selectedItemList = selectedItems.entrySet().stream().filter(entry -> AonStringUtils.containsIgnoreCase(entry.getValue().getStyleName(), AON.CSS.aonIconChecked())).collect(Collectors.toList());
			if(selectedItemList.size() == 1) {
				Fee selectedFee = rowFees.get(selectedItemList.get(0).getKey());
				if(null != selectedFee) {
					
					Integer originalSeller = null == selectedFee.getSeller() ? null : selectedFee.getSeller().getId();
					
					new CustomerFeeDialog(selectedFee, options, searchDomain) {
						
						@Override
						protected void onAccept(Fee fee) {
							LinkedList<Fee> fees = new LinkedList<Fee>();
							fee.setModify(true);
							fees.add(fee);
							
							SERVICE.saveCustomerFeeList(options.getDomainName(), options.getDomain(), options.getUser(), fees,
									new AsyncCallback<Integer>() {

										@Override
										public void onFailure(Throwable caught) {
											AonMessagePanel.showError(messagePanel, "Error guardando panel de facturaci\u00f3n: " + caught.getMessage());
										}

										@Override
										public void onSuccess(Integer updates) {
											AonMessagePanel.showSuccess(messagePanel, "Se han actualizado " + updates + " cuotas correctamente");
											addValueButton.setEnabled(false);
											deleteFeeButton.setEnabled(false);
											exportButton.setEnabled(false);
											onSearch();
											
											if(null != originalSeller) {
												
												// Desasignar
												if(null == fee.getSeller() || null == fee.getSeller().getId() || !originalSeller.equals(fee.getSeller().getId()))
													sendFeeEmail(fee.getId(), false, originalSeller);
												
												if(null != fee.getSeller() && null != fee.getSeller().getId() && !originalSeller.equals(fee.getSeller().getId()))
													sendFeeEmail(fee.getId(), true, null);
												
											} else if(null != fee.getSeller() && null != fee.getSeller().getId())
												sendFeeEmail(fee.getId(), true, null);		
										}
									});
						}

						@Override
						protected void onAccept(Optional<OldItem> item, Optional<Double> price, Optional<String> discountExpr, Optional<Date> startDate, Optional<Date> endDate, Optional<Date> billingDate) {}

						@Override
						protected void onCreate(Fee fee) {}

						@Override
						protected void onCreate(Fee fee, Integer ritem) {
							// TODO Auto-generated method stub
							
						}
						
					};
				}
			} else {
				new CustomerFeeDialog(productSuggestions.get(conceptSuggestBox.getValue()), options, searchDomain) {
					
					@Override
					protected void onAccept(Fee fee) {}
					
					@Override
					protected void onCreate(Fee fee) {}

					@Override
					protected void onAccept(Optional<OldItem> item, Optional<Double> price, Optional<String> discountExpr, Optional<Date> startDate, Optional<Date> endDate, Optional<Date> billingDate) {
						Fee fee = new Fee();
						
						if(item.isPresent()) fee.setItem(item.get());
						if(price.isPresent()) fee.setPrice(price.get());
						if(discountExpr.isPresent()) fee.setDiscountExpr(discountExpr.get());
						if(startDate.isPresent()) fee.setStartDate(startDate.get());
						if(endDate.isPresent()) fee.setEndDate(endDate.get());
						if(billingDate.isPresent()) fee.setBillingDate(billingDate.get());
						
						List<Entry<Integer, AonTableButton>> selectedItemList = selectedItems.entrySet().stream().filter(entry -> AonStringUtils.containsIgnoreCase(entry.getValue().getStyleName(), AON.CSS.aonIconChecked())).collect(Collectors.toList());
						
						if(selectedItemList.size() == rowFees.size()) {
							// Cambio masivo (todo seleccionado)
							offset.setValue(0);
							getWidgetParams();
							
							SERVICE.getCustomerProductsUpdates(options.getDomainName(), options.getDomain(), options.getUser(), searchDomain, params, new AsyncCallback<Map<Integer,Integer>>() {
								
								@Override
								public void onSuccess(Map<Integer, Integer> result) {
									Optional<Entry<Integer, Integer>> resultEntry = result.entrySet().stream().findFirst();
									AonDialog dialog = new AonDialog("Edici\u00f3n Cuotas",
											new HTML("El cambio afectara a <b>" + resultEntry.get().getKey() + " clientes</b> y <b>" + resultEntry.get().getValue() + " cuotas</b>.<br>\u00bfEsta seguro que desea proceder a la actualizacion\u003f"));
									
									dialog.confirm(new AonAcceptDialogCallback() {

										@Override
										public void onCancel() {
											// Nothing to do here
										}

										@Override
										public void onAccept() {
											AonMessagePanel.showLoading(messagePanel, "Actualizando datos de cuotas masivamente ...");
											SERVICE.saveMassiveCustomerFee(options.getDomainName(), options.getDomain(), options.getUser(), fee, params,
												new AsyncCallback<Integer>() {

													@Override
													public void onFailure(Throwable caught) {
														AonMessagePanel.showError(messagePanel, "Error guardando panel de facturaci\u00f3n: " + caught.getMessage());
													}

													@Override
													public void onSuccess(Integer updates) {
														AonMessagePanel.showSuccess(messagePanel, "Se han actualizado " + updates + " cuotas correctamente");
														addValueButton.setEnabled(false);
														deleteFeeButton.setEnabled(false);
														exportButton.setEnabled(false);
														onSearch();
													}
												});
										}
									});
								}
								
								@Override
								public void onFailure(Throwable caught) {
									// TODO Auto-generated method stub
									
								}
							});
						} else {
							// Solo cuotas seleccionadas
							LinkedList<Integer> selectedFees = selectedItemList.stream()
									.filter(entry -> AonStringUtils.containsIgnoreCase(entry.getValue().getStyleName(), AON.CSS.aonIconChecked()))
									.map(entry -> entry.getKey())
									.collect(Collectors.toCollection(LinkedList::new));
							
							AonDialog dialog = new AonDialog("Edici\u00f3n Cuotas",
									new HTML("El cambio afectara a <b>" + selectedFees.size() + " cuotas</b>.<br>\u00bfEsta seguro que desea proceder a la actualizacion\u003f"));
							
							dialog.confirm(new AonAcceptDialogCallback() {

								@Override
								public void onCancel() {
									// Nothing to do here
								}

								@Override
								public void onAccept() {
									AonMessagePanel.showLoading(messagePanel, "Actualizando datos de cuotas masivamente ...");
									selectedFees.forEach(feeId -> {
										Fee feeIt = rowFees.get(feeId);
										feeIt.setModify(true);
										
										if(null != fee.getPeriod()) feeIt.setPeriod(fee.getPeriod());
										if(null != fee.getQuantity()) feeIt.setQuantity(fee.getQuantity());
										if(null != fee.getPrice()) feeIt.setPrice(fee.getPrice());
										if(null != fee.getDiscountExpr()) feeIt.setDiscountExpr(fee.getDiscountExpr());
										
										if(null != fee.getBillingDate()) feeIt.setBillingDate(fee.getBillingDate());
										if(null != fee.getStartDate()) feeIt.setStartDate(fee.getStartDate());
										if(null != fee.getEndDate()) feeIt.setEndDate(fee.getEndDate());
										
										
									});
									
									LinkedList<Fee> updatedFees = rowFees.entrySet().stream()
											.filter(entry -> selectedFees.contains(entry.getKey()))
											.map(entry -> entry.getValue())
											.collect(Collectors.toCollection(LinkedList::new));
									
									SERVICE.saveCustomerFeeList(options.getDomainName(), options.getDomain(), options.getUser(), updatedFees,
											new AsyncCallback<Integer>() {

												@Override
												public void onFailure(Throwable caught) {
													AonMessagePanel.showError(messagePanel, "Error guardando panel de facturaci\u00f3n: " + caught.getMessage());
												}

												@Override
												public void onSuccess(Integer updates) {
													AonMessagePanel.showSuccess(messagePanel, "Se han actualizado " + updates + " cuotas correctamente");
													addValueButton.setEnabled(false);
													deleteFeeButton.setEnabled(false);
													exportButton.setEnabled(false);
													onSearch();
												}
											});
								}
							});
						} 
					}

					@Override
					protected void onCreate(Fee fee, Integer ritem) {
						// TODO Auto-generated method stub
						
					}
					
				};
			}
		});
		feeDockLayout.addToolbarButton(addValueButton);
		
		deleteFeeButton = new AonToolbarButton("Eliminar", AON.CSS.aonIconDelete());
		deleteFeeButton.setEnabled(false);
		deleteFeeButton.addClickHandler(event -> {
			
			List<Entry<Integer, AonTableButton>> selectedItemList = selectedItems.entrySet().stream().filter(entry -> AonStringUtils.containsIgnoreCase(entry.getValue().getStyleName(), AON.CSS.aonIconChecked())).collect(Collectors.toList());
			
			if(selectedItemList.size() == rowFees.size()) {
				// Cambio masivo (todo seleccionado)
				offset.setValue(0);
				getWidgetParams();
				
				SERVICE.getCustomerProductsUpdates(options.getDomainName(), options.getDomain(), options.getUser(), searchDomain, params, new AsyncCallback<Map<Integer,Integer>>() {
					
					@Override
					public void onSuccess(Map<Integer, Integer> result) {
						Optional<Entry<Integer, Integer>> resultEntry = result.entrySet().stream().findFirst();
						AonDialog dialog = new AonDialog("Eliminaci\u00f3n Cuotas",
								new HTML("Se va a proceder a eliminar <b>" + resultEntry.get().getValue() + " cuotas</b>.<br>\u00bfEsta seguro que desea proceder con la eliminaci\u00f3n\u003f. Este proceso ser\u00e1 irreversible"));
						
						dialog.confirm(new AonAcceptDialogCallback() {

							@Override
							public void onCancel() {
								// Nothing to do here
							}

							@Override
							public void onAccept() {
								offset.setValue(0);
								limit = Integer.MAX_VALUE;
								getWidgetParams();

								// Hay que eliminar las cuotas cuando se haya realizado el envío de email
								getList(deletedFees -> {
									if (deletedFees.isEmpty()) {
										deleteFees(resultEntry.get().getValue()); // Si no hay ninguna, se puede eliminar directamente
										return;
									}

									MutableInt pendingEmails = new MutableInt(deletedFees.size());

									deletedFees.forEach(deleteFee -> {
										Integer sellerId = deleteFee.getSeller() != null ? deleteFee.getSeller().getId() : null;

										sendFeeEmail(deleteFee.getId(), false, sellerId, () -> {
											// Cuando una llamada termina
											pendingEmails.decrement();
											if (pendingEmails.getValue() == 0) {
												deleteFees(resultEntry.get().getValue()); // Ejecutar eliminación cuando todas hayan terminado
											}
										});
									});
								});
								
//								offset.setValue(0);
//								limit = Integer.MAX_VALUE;
//								getWidgetParams();
//								
//								// Hay que eliminar las cuotas cuando se haya realizado el envio de email (lo que hay es un error)
//								getList(deletedFees -> {
//									deletedFees.forEach(deleteFee -> {
//										if(null != deleteFee.getSeller() || null != deleteFee.getSeller().getId())
//											sendFeeEmail(deleteFee.getId(), false, deleteFee.getSeller().getId());
//									});
//									
//									AonMessagePanel.showLoading(messagePanel, "Elimando cuotas seleccionadas ...");
//									SERVICE.deleteCustomerFeeList(options.getDomainName(), options.getDomain(), options.getUser(), params,
//											new AsyncCallback<Void>() {
//
//												@Override
//												public void onFailure(Throwable caught) {
//													AonMessagePanel.showError(messagePanel, "Error eliminando cuotas: " + caught.getMessage());
//												}
//
//												@Override
//												public void onSuccess(Void result) {
//													AonMessagePanel.showSuccess(messagePanel, "Se han eliminado " + resultEntry.get().getValue() + " cuotas correctamente");
//													duplicateValueButton.setEnabled(false);
//													addValueButton.setEnabled(false);
//													deleteFeeButton.setEnabled(false);
//													exportButton.setEnabled(false);
//													onSearch();
//												}
//											});
//								});
								
								
							}
						});
					}
					
					@Override
					public void onFailure(Throwable caught) {
						// TODO Auto-generated method stub
						
					}
				});
			} else {
				AonDialog dialog = new AonDialog("Eliminaci\u00f3n Cuotas",
						new HTML("Se va a proceder a eliminar <b>" + selectedItemList.size() + " cuotas</b>.<br>\u00bfEsta seguro que desea proceder con la eliminaci\u00f3n\u003f<br>Este proceso ser\u00e5 irreversible"));
				
				dialog.confirm(new AonAcceptDialogCallback() {

					@Override
					public void onCancel() {
						// Nothing to do here
					}

					@Override
					public void onAccept() {
						AonMessagePanel.showLoading(messagePanel, "Elimando cuotas seleccionadas ...");
						
						LinkedList<Integer> selectedFees = selectedItemList.stream()
								.filter(entry -> AonStringUtils.containsIgnoreCase(entry.getValue().getStyleName(), AON.CSS.aonIconChecked()))
								.map(entry -> entry.getKey())
								.collect(Collectors.toCollection(LinkedList::new));
						
						LinkedList<Fee> deletedFees = rowFees.entrySet().stream()
								.filter(entry -> selectedFees.contains(entry.getKey()))
								.map(entry -> entry.getValue())
								.collect(Collectors.toCollection(LinkedList::new));
						
						deletedFees.forEach(deleteFee -> {
							if(null != deleteFee.getSeller() || null != deleteFee.getSeller().getId())
								sendFeeEmail(deleteFee.getId(), false, deleteFee.getSeller().getId());
						});
						
						SERVICE.deleteCustomerFeeList(options.getDomainName(), options.getDomain(), options.getUser(), deletedFees,
								new AsyncCallback<Void>() {

									@Override
									public void onFailure(Throwable caught) {
										AonMessagePanel.showError(messagePanel, "Error eliminando cuotas: " + caught.getMessage());
									}

									@Override
									public void onSuccess(Void result) {
										AonMessagePanel.showSuccess(messagePanel, "Se han eliminado " + selectedFees.size() + " cuotas correctamente");
										duplicateValueButton.setEnabled(false);
										addValueButton.setEnabled(false);
										deleteFeeButton.setEnabled(false);
										exportButton.setEnabled(false);
										onSearch();
									}
								});
					}
				});
			}
		});
		feeDockLayout.addToolbarButton(deleteFeeButton);
		
		exportButton = new AonToolbarButton("Exportar Cuotas", AON.CSS.aonIconExcel());
		exportButton.setEnabled(false);
		exportButton.addClickHandler(e -> {
			List<Entry<Integer, AonTableButton>> selectedItemList = selectedItems.entrySet().stream().filter(entry -> AonStringUtils.containsIgnoreCase(entry.getValue().getStyleName(), AON.CSS.aonIconChecked())).collect(Collectors.toList());
			
			if(selectedItemList.size() == rowFees.size()) {
				// Exportacion masiva (todo seleccionado)
				exportFees(null);
			} else {
				List<Integer> feeIds = selectedItemList.stream().map(entry -> entry.getKey()).collect(Collectors.toList());
				exportFees(feeIds);
			}
		});
		feeDockLayout.addToolbarButton(exportButton);
		
		if(!isCustomer()) {
			importButton = new AonToolbarButton("Importar Cuotas", AON.CSS.aonIconUploadFile());
			importButton.addClickHandler(e -> {
				Upload upload = new Upload() {
					
					@Override
					protected void onUpload(String data, String type) {
						pbd = new AonProgressBarDialog("Procesando Excel...") {};
						pbd.addStyleName("gwt-PopupPanel-template");
						pbd.setGlassEnabled(true);
						pbd.center();
						pbd.show();
						
						SERVICE.parseFeeFile(options.getConfiguration().getDomain(), options.getConfiguration().getUser(), data, new AsyncCallback<List<Fee>>() {
							@Override
							public void onSuccess(List<Fee> result) {
								pbd.completed();
								pbd.hide();
								pbd = new AonProgressBarDialog("Importando Cuotas...") {};
								pbd.addStyleName("gwt-PopupPanel-template");
								pbd.setGlassEnabled(true);
								pbd.center();
								pbd.show();
								insertFee(result, 0);
							}
								
							@Override
							public void onFailure(Throwable caught) {}
						});
					}
				};
				upload.upload();
			});
			feeDockLayout.addToolbarButton(importButton);
		}
		
		if(!isCustomer()) {
			customerButton = new AonToolbarButton("Clientes sin cuotas", AON.CSS.aonIconGroupOff());
			customerButton.addClickHandler(e ->  showCustomersTable() );
			feeDockLayout.addToolbarButton(customerButton);
		}
	}
	
	private void deleteFees(Integer deleteCount) {
		AonMessagePanel.showLoading(messagePanel, "Eliminando cuotas seleccionadas ...");
		SERVICE.deleteCustomerFeeList(options.getDomainName(), options.getDomain(), options.getUser(), params,
				new AsyncCallback<Void>() {

					@Override
					public void onFailure(Throwable caught) {
						AonMessagePanel.showError(messagePanel, "Error eliminando cuotas: " + caught.getMessage());
					}

					@Override
					public void onSuccess(Void result) {
						AonMessagePanel.showSuccess(messagePanel, "Se han eliminado " + deleteCount + " cuotas correctamente");
						duplicateValueButton.setEnabled(false);
						addValueButton.setEnabled(false);
						deleteFeeButton.setEnabled(false);
						exportButton.setEnabled(false);
						onSearch();
					}
				});
	}
	
	private void sendFeeEmail(Integer feeId, Boolean add, Integer oldSellerId, Runnable onComplete) {
		String host = Window.Location.getHost();
		String endPoint = "/ms/api/fee-mail/";

		HashMap<String, String> headers = new HashMap<>();
		headers.put("domain_name", options.getDomainName());
		headers.put("domain_login", options.getUser());
		headers.put("domain_id", String.valueOf(options.getDomain()));

		JSONObject body = new JSONObject();
		body.put("feeId", new JSONString(feeId.toString()));

		if (add) {
			body.put("add", new JSONString(add.toString()));
		} else if (oldSellerId != null) {
			body.put("oldSellerId", new JSONString(oldSellerId.toString()));
		}

		UrlBuilder urlBuilder = new UrlBuilder();
		urlBuilder.setProtocol(Window.Location.getProtocol());
		urlBuilder.setHost(host);
		urlBuilder.setPath(endPoint);

		RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.POST, urlBuilder.buildString());

		headers.forEach(requestBuilder::setHeader);

		try {
			requestBuilder.sendRequest(body.toString(), new RequestCallback() {
				public void onResponseReceived(Request request, Response response) {
					String message = "Respuesta sin mensaje";
					if (response.getStatusCode() == 200 || response.getStatusCode() == 400) {
						try {
							JSONValue jsonValue = JSONParser.parseStrict(response.getText());
							if (jsonValue != null && jsonValue.isObject() != null) {
								JSONObject jsonObject = jsonValue.isObject();
								JSONValue msgVal = jsonObject.get("message");
								message = msgVal != null ? msgVal.isString().stringValue() : "Error desconocido";
							}
						} catch (Exception e) {
							message = "Error parseando respuesta";
						}
					}

					if (response.getStatusCode() == 400)
						AonMessagePanel.showError(messagePanel, message);
					else
						AonMessagePanel.showSuccess(messagePanel, message);

					onComplete.run();
				}

				public void onError(Request request, Throwable exception) {
					onComplete.run();
				}
			});
		} catch (RequestException exception) {
			onComplete.run();
		}
	}

	
	private void showCustomerFeeTable() {
		deckLayoutPanel.showWidget(0);	
		onSearch();
	}

	private void showCustomersTable() {
		deckLayoutPanel.showWidget(1);
		customerWithoutFee.onSearch();
	}

	private void insertFee(List<Fee> fee, Integer index) {
		Integer lines = fee.size();
		AsyncCallback<ImportError> callback = new AsyncCallback<ImportError>() {
			@Override
			public void onSuccess(ImportError result) {
				Double progress = (result.getLine().doubleValue() / lines.doubleValue()) * 100.0;
				if(!result.getError()) {
					verror.add(result.getTextError().getFirst());
				}
				if(result.getTextWarning() != null && result.getTextWarning().size() > 0) {
					werror.addAll(result.getTextWarning());
				}
				pbd.updateProgress(progress.intValue());
				if(result.getLine() < lines - 1) {
					insertFee(fee, result.getLine() + 1);
				} else {
					pbd.completed();
					pbd.hide();
					ImportError error = new ImportError();
					error.setError(verror.size() == 0);
					error.setTextError(verror);
					error.setTextWarning(werror);
					VerticalPanel vPanel = new VerticalPanel();
					error.getTextError().forEach(errorIt -> vPanel.add(new Label(errorIt)));
					error.getTextWarning().forEach(warnIt -> vPanel.add(new Label(warnIt)));
					AonDialog dialog = new AonDialog("Importar Cuotas", vPanel);
					dialog.info();
				}
			}
				
			@Override public void onFailure(Throwable caught) {
				pbd.completed();
				pbd.hide();
			}
		};	
		
		SERVICE.importFee(options.getConfiguration().getDomain(), options.getConfiguration().getUser(), fee.get(index), index, callback);
	}

	private void exportFees(List<Integer> feeIds) {
		JSONObject json = new JSONObject();
		
		if(null == feeIds || feeIds.size() == 0) {
			
			if(AonStringUtils.isNotBlank(monthListBox.getValue())) json.put("month", new JSONNumber(Integer.parseInt(monthListBox.getValue())));
			if(AonStringUtils.isNotBlank(yearListBox.getValue())) json.put("year", new JSONNumber(Integer.parseInt(yearListBox.getValue())));
			if(AonStringUtils.isNotBlank(periocityListBox.getValue())) json.put("periodicity", new JSONNumber(Integer.parseInt(periocityListBox.getValue())));
			
			if(isCustomer()) json.put("customer", new JSONNumber(customerId));
			else json.put("description", new JSONString(feeDockLayout.getSearchTextBox().getValue()));
			
			if(!isCustomer()) if(AonStringUtils.isNotBlank(customerStatusListBox.getValue())) json.put("status", new JSONNumber(Integer.parseInt(customerStatusListBox.getValue())));
			if(!isCustomer()) if(null != segmentListBox && AonStringUtils.isNotBlank(segmentListBox.getValue())) json.put("segmentId", new JSONNumber(Integer.parseInt(segmentListBox.getValue())));
			
			if(AonStringUtils.isNotBlank(startCompareLB.getValue())) json.put("startDateCompare", new JSONNumber(Integer.parseInt(startCompareLB.getValue())));
			if(null != startDateBox.getValue()) json.put("startDate", new JSONNumber(startDateBox.getValue().getTime()));
			if(AonStringUtils.isNotBlank(endCompareLB.getValue())) json.put("endDateCompare", new JSONNumber(Integer.parseInt(endCompareLB.getValue())));
			if(null != endDateBox.getValue()) json.put("endDate", new JSONNumber(endDateBox.getValue().getTime()));
			
			if(null != productSuggestions.get(conceptSuggestBox.getValue())) json.put("product", new JSONNumber(productSuggestions.get(conceptSuggestBox.getValue()).getId()));
			if(null != productCategorySuggestions.get(productCategorySuggestBox.getValue())) json.put("productCategory", new JSONNumber(productCategorySuggestions.get(productCategorySuggestBox.getValue())));
			if(null != productTagSuggestions.get(productTagSuggestBox.getValue())) json.put("productTag", new JSONNumber(productTagSuggestions.get(productTagSuggestBox.getValue())));
			if(AonStringUtils.isNotBlank(quantityTextBox.getValue()))  json.put("quantity", new JSONNumber(Double.parseDouble(quantityTextBox.getValue())));
			if(AonStringUtils.isNotBlank(priceTextBox.getValue()))  json.put("price", new JSONNumber(Double.parseDouble(priceTextBox.getValue())));
			if(AonStringUtils.isNotBlank(discountTextBox.getValue()))  json.put("discount", new JSONString(discountTextBox.getValue()));
			
			if(null != sellerSuggestions.get(sellerSuggestBox.getValue())) json.put("seller", new JSONNumber(sellerSuggestions.get(sellerSuggestBox.getValue()).getId()));
			if(null != workplaceSuggestions.get(workplaceSuggestBox.getValue())) json.put("workplace", new JSONString(workplaceSuggestBox.getValue()));
			if(null != invoicingGroupSuggestions.get(invoicingGroupSuggestBox.getValue())) json.put("invoicingGroup", new JSONString(invoicingGroupSuggestBox.getValue()));
			if(null != projectSuggestions.get(projectSuggestBox.getValue())) json.put("project", new JSONNumber(projectSuggestions.get(projectSuggestBox.getValue()).getId()));
			
		} else {
			JSONObject feeJson = new JSONObject();
			for(int i=0; i<feeIds.size(); i++) 
				feeJson.put("feeId"+i, new JSONString(feeIds.get(i).toString()));
			
			json.put("feeIds", feeJson);
		}
		
		json.put("isCustomerFee", new JSONString("true"));
		
		String fileDownloadURL = GWT.getModuleBaseURL()+ "ms/gwt_download_fee/"
            	+ "?filter=" + btoa(json.toString())
            	+ "&domain_name=" + options.getDomainName()
            	+ "&domain_id=" + options.getDomain()
				+ "&username="+ options.getUser();
		
		Window.open( fileDownloadURL, "_blank",null);
	}

	private native String btoa(String str) /*-{
	    return btoa(str);
	}-*/;
	
	private void sendFeeEmail(Integer feeId, Boolean add, Integer oldSellerId) {
		String host = Window.Location.getHost();
		String endPoint = "/ms/api/fee-mail/";

		HashMap<String, String> headers = new HashMap<>();
		headers.put("domain_name", options.getDomainName());
		headers.put("domain_login", options.getUser());
		headers.put("domain_id", String.valueOf(options.getDomain()));

		JSONObject body = new JSONObject();

		body.put("feeId", new JSONString(feeId.toString()));
		
		if(add)	body.put("add", new JSONString(add.toString()));
		else if(null != oldSellerId) body.put("oldSellerId", new JSONString(oldSellerId.toString()));
		
		// Create a URL builder and add query parameters
		UrlBuilder urlBuilder = new UrlBuilder();
		urlBuilder.setProtocol(Window.Location.getProtocol()); // Use the current protocol
		urlBuilder.setHost(host);
		urlBuilder.setPath(endPoint);

		// Create the request builder with the complete URL
		RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.POST, urlBuilder.buildString());

		headers.entrySet().forEach(entry -> requestBuilder.setHeader(entry.getKey(), entry.getValue()));

		try {
			// Send the request
			requestBuilder.sendRequest(body.toString(), new RequestCallback() {
				public void onResponseReceived(Request request, Response response) {
					JSONValue jsonValue = JSONParser.parseStrict(response.getText());
					String message = "";

					if (jsonValue != null && jsonValue.isObject() != null) {
						JSONObject jsonObject = jsonValue.isObject();

						JSONValue messageValue = jsonObject.get("message");
						message = null != messageValue ? messageValue.isString().stringValue()
								: "Error desconocido";
					}

					if (response.getStatusCode() == 400)
						AonMessagePanel.showError(messagePanel, message);
					else
						AonMessagePanel.showSuccess(messagePanel, message);
				}

				public void onError(Request request, Throwable exception) {
					Window.alert(exception.getMessage());
				}
			});
		} catch (RequestException exception) {
			Window.alert("Catch : " + exception.getMessage());
		}
	}
	
	// ------------------------------------------ Eval Expression
	
	public double evalExpression(String expression) {
		return calculate(expression);
	}

	public final native double calculate(String expression) /*-{
		return eval(expression);
	}-*/;
	
	public static native String getCustomerId()
	/*-{
		return $wnd.getCustomerId();
	}-*/;
	
	private boolean isCustomer() {
		return null != customerId && 0 != customerId;
	}
	
	private void getList(Consumer<LinkedList<Fee>> success) {
		SERVICE.getCustomerFeeList(options.getDomainName(), options.getDomain(), options.getUser(), params,
			new AsyncCallback<LinkedList<Fee>>() {

				@Override
				public void onFailure(Throwable caught) {
					AonMessagePanel.showError(messagePanel, "Error cargando panel de facturaci\u00f3n: " + caught.getMessage());
				}

				@Override
				public void onSuccess(LinkedList<Fee> feeList) {
					success.accept(feeList);
					
				}
			}
		);
	}
	
	private void getCustomer(Consumer<Customer> success) {
		COMMON_SERVICE.getCustomer(options.getDomainName(), options.getDomain(), options.getUser(), customerId, new AsyncCallback<CustomerFull>() {
			
			@Override
			public void onSuccess(CustomerFull customer) {
				success.accept(customer.getRegistry());
			}
			
			@Override
			public void onFailure(Throwable caught) {
				AonMessagePanel.showError(messagePanel, "Error obteniendo cliente: " + caught.getMessage());
				success.accept(null);
			}
		});
	}

}
