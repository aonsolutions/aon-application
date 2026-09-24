package com.esferalia.aon.gwt.fiscal.client.customer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.gwt.common.client.CommonServiceAsync;
import com.esferalia.aon.gwt.common.client.CommonServiceAsyncDecorator;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomTable;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog.AonAcceptDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTableButton;
import com.esferalia.aon.gwt.fiscal.client.booking.BookingApi;
import com.esferalia.aon.occam.api.model.Customer;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.DomainCompany;
import com.esferalia.aon.occam.api.model.customer.CustomersDomainSyncParams;
import com.esferalia.aon.occam.api.model.customer.CustomersLinkedParams;
import com.esferalia.aon.occam.api.model.registry.DomainCustomerSync;
import com.esferalia.aon.occam.api.model.registry.DomainSigAddInfo;
import com.esferalia.aon.occam.api.model.type.DomainType;
import com.esferalia.aon.occam.api.model.type.RegistryStatus;
import com.esferalia.aon.watson.mutable.MutableInt;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.event.dom.client.ScrollHandler;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.logging.client.ConsoleLogHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public abstract class SigCustomerDomainPanel extends ScrollPanel {

	private static CommonServiceAsync COMMON_SERVICE;

	private static final Logger LOGGER = Logger.getLogger(SigCustomerDomainPanel.class.getName());
	static {
		LOGGER.addHandler(new ConsoleLogHandler());
	}

	private final int limit = 40;
	private final MutableInt offset = new MutableInt(0);
	private final MutableInt moreData = new MutableInt(0);
	private final MutableInt searchEnabled = new MutableInt(0);

	private AonCustomTable tab;
	private int lastScrollPos = 0;

	private Map<Integer, Customer> rowCustomers = new HashMap<>();
	private Map<Integer, List<DomainCompany>> customersDomain = new HashMap<>();
	private Map<Integer, List<DomainSigAddInfo>> customersRaddInfo = new HashMap<>();

	private CustomersLinkedParams params;

	private BookingApi bookingApi;
	private CustomerApi customerApi;
	private static String SESSION_API = "AONd95770f269e711eb94390242ac130002";
	private boolean isLocalDev = false;

	private static enum COLS {

		IDC("Cliente",           "5rem",          "min-width: 3rem;   white-space: nowrap; overflow: hidden; text-overflow: ellipsis;"),
		DOC(AON.MSG.document(),  "6rem",          "min-width: 4rem;   white-space: nowrap; overflow: hidden; text-overflow: ellipsis;"),
		DES(AON.MSG.name(),      "-moz-available","min-width: 6rem;   white-space: nowrap; overflow: hidden; text-overflow: ellipsis;"),
		STA(AON.MSG.status(),    "6rem",          "min-width: 4rem;   white-space: nowrap; overflow: hidden; text-overflow: ellipsis;"),
		RAD("RaddInfo",          "5rem",          "min-width: 2.5rem; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; justify-content: center;"),
		ACU("Aon Customer",      "7rem",          "min-width: 2.5rem; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; justify-content: center;"),
		BTN(AonStringUtils.EMPTY,"4rem",          "min-width: 4rem;   white-space: nowrap; overflow: hidden; text-overflow: ellipsis;")
		;

		String headerLabel;
		String colWidth;
		String styles;

		private COLS(String headerLabel, String colWidth, String styles) {
			this.headerLabel = headerLabel;
			this.colWidth = colWidth;
			this.styles = styles;
		}

		public String getColWidth() {
			return colWidth;
		}

		public String getHeaderLabel() {
			return headerLabel;
		}

		public String getStyles() {
			return styles;
		}
	}

	public SigCustomerDomainPanel(CustomersLinkedParams params) {
		this.params = params;

		CommonServiceAsync commonServiceRaw = GWT.create(CommonService.class);
		COMMON_SERVICE = new CommonServiceAsyncDecorator(commonServiceRaw);

		this.bookingApi = new BookingApi(SESSION_API);
		this.customerApi = new CustomerApi(SESSION_API);

		this.rowCustomers.clear();

		addScrollHandler(new ScrollHandler() {

			public void onScroll(ScrollEvent event) {
				// ------------------------------------ Ignore scroll up.
				int oldScrollPos = lastScrollPos;
				lastScrollPos = getVerticalScrollPosition();
				if (oldScrollPos >= lastScrollPos) {
					return;
				}
				// -----------------------------------------------------
				if (isSearchEnabled()) {
					int maxScrollTop = getWidget().getOffsetHeight() - getOffsetHeight();
					if (lastScrollPos >= maxScrollTop) {
						disableSearch();
						searchData();
					}
				}
			}
		});

		onSearch();

	}

	public boolean isSearchEnabled() {
		return (searchEnabled.getValue() == 0);
	}

	public void disableSearch() {
		searchEnabled.setValue(-1);
	}

	public void enableSearch() {
		searchEnabled.setValue(0);
	}

	public boolean isMoreData() {
		return (moreData.getValue() == 0);
	}

	public void disableMoreData() {
		moreData.setValue(-1);
	}

	public void enableMoreData() {
		moreData.setValue(0);
	}

	private void onSearch() {
		enableMoreData();
		search();
	}

	private void search() {
		tab = new AonCustomTable();
		setWidget(tab);

		paintHeader();
		searchData();
	}

	private void paintHeader() {
		tab.createHeader();
		for (COLS col : COLS.values())
			tab.addHeader(new Label(col.getHeaderLabel()), col.getColWidth(), col.getStyles());
	}
	
	private void searchData() {
	    if (!isMoreData()) return;

	    onShowELoadingMessage("Cargando informaci\u00f3n clientes/dominios. Este proceso puede llevar unos segundos...");

	    getList(customers -> {
	        boolean firstPage = (0 == offset.intValue());

	        customers.forEach(this::paintRow);

	        if (params.isNoRaddInfo() || params.isNoAonCustomer()) {
	            // limit = MAX_VALUE: carga unica, sin scroll infinito
	            disableMoreData();
	            disableSearch();
	        } else {
	            offset.setValue(offset.intValue() + customers.size());
	            if (customers.size() < limit) disableMoreData();
	            enableSearch();
	        }

	        if (firstPage && customers.isEmpty()) paintEmptyRow();

	        onHideMessage();
	    });
	}

	private void paintEmptyRow() {
		HTMLPanel row = tab.createRow();

		Label name = new Label("No existen clientes para la busqueda");
		name.setTitle("No existen clientes para la busqueda");
		tab.addInlineStyle(name, COLS.DES.getStyles());
		tab.addRow(row, name, COLS.DES.getColWidth());
	}

	private void paintRow(Customer customer) {
		HTMLPanel row = tab.createRow();
		row.addDomHandler(e -> onCusotmerOpen(customer), ClickEvent.getType());

		Label id = new Label(customer.getId().toString());
		tab.addRow(row, id, COLS.IDC.getColWidth(), COLS.IDC.getStyles());
		
		Label document = new Label(customer.getDocument());
		tab.addRow(row, document, COLS.DOC.getColWidth(), COLS.DOC.getStyles());

		Label name = new Label(customer.getName());
		name.setTitle(customer.getName());
		tab.addRow(row, name, COLS.DES.getColWidth(), COLS.DES.getStyles());

		Label customerStatus = new Label(customer.getStatus().getDescription());
		customerStatus.setTitle(customer.getStatus().getDescription());
		tab.addRow(row, customerStatus, COLS.STA.getColWidth(), COLS.STA.getStyles());

		HTMLPanel customerRaddInfoPanel = new HTMLPanel(AonStringUtils.EMPTY);
		customerRaddInfoPanel.addStyleName(AON.CSS.aonItemFlex());
		List<DomainSigAddInfo> customerRaddInfo = safeList(customersRaddInfo, customer.getId());
		if(customerRaddInfo.size() > 1) {
			
			customerRaddInfo.forEach(ds -> {
				AonTableButton raddInfoBtn = new AonTableButton("RaddInfo (" + ds.getDomainId() + " -- " + ds.getDomainName() + ")", AON.CSS.aonIconCheckCircle());
				customerRaddInfoPanel.add(raddInfoBtn);
			});
			
		} else {
			AonTableButton raddInfoBtn = new AonTableButton(
					customerRaddInfo.isEmpty() ? "No existe RaddInfo" : "RaddInfo (" + customerRaddInfo.get(0).getDomainId() + " -- " + customerRaddInfo.get(0).getDomainName() + ")",
					customerRaddInfo.isEmpty() ? AON.CSS.aonIconBlock() : AON.CSS.aonIconCheckCircle());

			customerRaddInfoPanel.add(raddInfoBtn);
		}
		tab.addRow(row, customerRaddInfoPanel, COLS.RAD.getColWidth(), COLS.RAD.getStyles());

		HTMLPanel customerDomainPanel = new HTMLPanel(AonStringUtils.EMPTY);
		customerDomainPanel.addStyleName(AON.CSS.aonItemFlex());
		List<DomainCompany>    customerDomain   = safeList(customersDomain,   customer.getId());
		if(customerDomain.size() > 1) {
			
			customerDomain.forEach(ds -> {
				AonTableButton raddInfoBtn = new AonTableButton("AonCustomer (" + ds.getDomain().getAonCustomer() + ") -- " +  ds.getDomain().getName() + " [" + ds.getDomain().getId() + "]", AON.CSS.aonIconCheckCircle());
				customerDomainPanel.add(raddInfoBtn);
			});
			
		} else {
			AonTableButton customerDomainBtn = new AonTableButton(
					customerDomain.isEmpty() ? "No existe AonCustomer" : "AonCustomer (" + customerDomain.get(0).getDomain().getAonCustomer() + ") -- " +  customerDomain.get(0).getDomain().getName() + " [" + customerDomain.get(0).getDomain().getId() + "]",
					customerDomain.isEmpty() ? AON.CSS.aonIconBlock() : AON.CSS.aonIconCheckCircle());
			
			customerDomainPanel.add(customerDomainBtn);
		}
		tab.addRow(row, customerDomainPanel, COLS.ACU.getColWidth(), COLS.ACU.getStyles());

		HTMLPanel optionsPanel = new HTMLPanel(AonStringUtils.EMPTY);
		optionsPanel.addStyleName(AON.CSS.aonItemFlex());
		
		Widget optionBtn;
		if (!customerDomain.isEmpty() && !customerRaddInfo.isEmpty())
			optionBtn = new Label();
		else if (customerDomain.isEmpty() && customerRaddInfo.isEmpty()) {
			optionBtn = new AonTableButton("Vincular", AON.CSS.aonIconLink());
			((AonTableButton) optionBtn).addClickHandler(e -> {
				e.stopPropagation();
				showSelectedCustomer(customer);
			});
		} else {
			optionBtn = new AonTableButton("Sincronizar", AON.CSS.aonIconSync());
			((AonTableButton) optionBtn).addClickHandler(e -> {
				e.stopPropagation();
				onCustomerClick(customer, !customerRaddInfo.isEmpty() ? customerRaddInfo.get(0).getDomainName() : (!customerDomain.isEmpty() ? customerDomain.get(0).getDomain().getName() : null));
			});
		}
		optionsPanel.add(optionBtn);
		
		if(!customerDomain.isEmpty() && needInfoShow(customer, customerDomain.get(0))) {
			AonTableButton messages = new AonTableButton(
					getInfoMessage(customer, customerDomain.get(0)),
					AON.CSS.aonIconInfo()
			);
			messages.getElement().getStyle().setProperty("background-size", "22px");
			optionsPanel.add(messages);
		}
		
		tab.addRow(row, optionsPanel, COLS.BTN.getColWidth(), COLS.BTN.getStyles());

		rowCustomers.put(customer.getId(), customer);
	}
	
	private void onCustomerClick(Customer customer, String domainName) {
		AonDialog dialog = new AonDialog("Vinculaci\u00f3n Cliente / Dominio", new Label(
				"Desea sincronizar la vinculaci\u00f3n del cliente " + domainName + " ?"));
		dialog.confirm(new AonAcceptDialogCallback() {

			@Override
			public void onCancel() {}

			@Override
			public void onAccept() {
				onSyncCustomerDomain(customer, domainName);
				dialog.hide();
			}

		});
	}

	private void onCusotmerOpen(Customer customer) {
		SimplePanel centerPanel = new SimpleLayoutPanel();
		centerPanel.setHeight("25rem");
		centerPanel.setWidth("100%");

		List<DomainSigAddInfo> customerRaddInfo = safeList(customersRaddInfo, customer.getId());
		List<DomainCompany>    customerDomain   = safeList(customersDomain,   customer.getId());
		
		SyncSigMultipleDomainsTable table = new SyncSigMultipleDomainsTable(customer, customerRaddInfo, customerDomain) {
			
			@Override
			protected void onClickRow(DomainCustomerSync domainCustomerSync) {
				onCustomerClick(customer, domainCustomerSync.getDomainName());
			}
			
		};
		
		centerPanel.setWidget(table);
		
		AonDialog dialog = new AonDialog(customer.getName() + " (" + customer.getId() + ") " + customerRaddInfo.size() + "/" + customerDomain.size(), centerPanel);
		dialog.removeMaxWidth();
		dialog.showCloseButton(true);
		dialog.setWidth("55rem");
		dialog.info();
	}

	public void resetSearchOffset() {
		offset.setValue(0);
	}

	private void getList(Consumer<List<Customer>> success) {

		params.setSig(true);
		params.setOffset(offset.intValue());
		params.setLimit(params.isNoRaddInfo() || params.isNoAonCustomer() ? Integer.MAX_VALUE : limit);
		
		COMMON_SERVICE.getCustomers(params, new AsyncCallback<List<Customer>>() {

			@Override
			public void onFailure(Throwable caught) {
				onShowErrorMessage("Error obteniendo clientes : " + caught.getMessage());
			}

			@Override
			public void onSuccess(List<Customer> customers) {
				List<Integer> customerIds = customers.stream().map(customer -> customer.getId()).collect(Collectors.toCollection(ArrayList::new));
				checkCustomerRaddInfos(customerIds, 
					end -> {
						checkCustomerDomains(customerIds, 
							parsedCustomers -> {
								
								
								
								if(params.isNoRaddInfo() && !params.isNoAonCustomer())
									success.accept(customers.stream().filter(c -> safeList(customersRaddInfo, c.getId()).isEmpty()).collect(Collectors.toList()));
								else if(!params.isNoRaddInfo() && params.isNoAonCustomer())
									success.accept(customers.stream().filter(c -> safeList(customersDomain, c.getId()).isEmpty()).collect(Collectors.toList()));
								else if(params.isNoRaddInfo() && params.isNoAonCustomer())
									success.accept(customers.stream().filter(c -> safeList(customersDomain, c.getId()).isEmpty() && safeList(customersRaddInfo, c.getId()).isEmpty()).collect(Collectors.toList()));
								else
									success.accept(customers);
							});
							
				});
				
			}
		});
	}
	
	private void checkCustomerDomains(List<Integer> customerIds, Consumer<Void> success) {
	    int total = customerIds.size();
	    int[] pending = { total };
	    List<String> errors = new ArrayList<>();

	    customerIds.forEach(customerId -> {
	        String host = isLocalDev ? "localhost:8080" : "aon.solutions";
	        String endPoint = "/ms/api/domain/" + customerId;

	        this.bookingApi.getDomainCompanies(host, endPoint, new AsyncCallback<List<DomainCompany>>() {
	            @Override
	            public void onSuccess(List<DomainCompany> domainCompanies) {
	                customersDomain.put(customerId, domainCompanies);
	                checkFinish();
	            }

	            @Override
	            public void onFailure(Throwable exception) {
	                // sin entrada en el mapa, paintRow reventaba con NPE
	                customersDomain.put(customerId, new ArrayList<DomainCompany>());
	                errors.add("cliente " + customerId + ": " + exception.getMessage());
	                checkFinish();
	            }

	            private void checkFinish() {
	                pending[0]--;
	                if (0 == pending[0]) {
	                    if (!errors.isEmpty())
	                        onShowErrorMessage("No se pudo obtener el dominio de " + errors.size()
	                                + " cliente(s). Primero -> " + errors.get(0));
	                    success.accept(null);
	                }
	            }
	        });
	    });

	    if (0 == total) success.accept(null);
	}
	
	private void checkCustomerRaddInfos(List<Integer> customerIds, Consumer<Void> success) {
	    if (customerIds.isEmpty()) {
	        success.accept(null);
	        return;
	    }

	    COMMON_SERVICE.getDomainSigAddInfo(params.getDomainName(), params.getDomainId(), params.getUser(),
	            new ArrayList<Integer>(customerIds),
	            new AsyncCallback<HashMap<Integer, List<DomainSigAddInfo>>>() {

	        @Override
	        public void onSuccess(HashMap<Integer, List<DomainSigAddInfo>> raddInfos) {
	            customersRaddInfo.putAll(raddInfos);
	            // el servidor ya devuelve entrada para todos, pero no confiamos en ello
	            customerIds.forEach(id -> customersRaddInfo.putIfAbsent(id, new ArrayList<DomainSigAddInfo>()));
	            success.accept(null);
	        }

	        @Override
	        public void onFailure(Throwable caught) {
	            customerIds.forEach(id -> customersRaddInfo.put(id, new ArrayList<DomainSigAddInfo>()));
	            onShowErrorMessage("Error obteniendo la vinculaci\u00f3n (raddinfo) de los clientes : " + caught.getMessage());
	            success.accept(null);
	        }
	    });
	}
	
	private void showSelectedCustomer(Customer customer) {
		SimpleLayoutPanel centerPanel = new SimpleLayoutPanel();
		centerPanel.setHeight("20rem");
		centerPanel.getElement().getStyle().setProperty("margin", "1rem");
		
		AonCustomDialog dialog = new AonCustomDialog();
		dialog.showCloseButton(true);
		dialog.setCaption(customer.getName());
		dialog.setHeight("25rem");
		dialog.setWidth("55rem");
		
		CustomersDomainSyncParams paramsDomains = new CustomersDomainSyncParams()
				.setDomainName(params.getDomainName())
				.setDomainId(params.getDomainId())
				.setUser(params.getUser())
				.setQuery(customer.getAlias())
				.setSig(true)
				.setOffset(params.getOffset())
				.setLimit(params.getLimit());

		DomainSyncPanel domainSyncPanel = new DomainSyncPanel(customer, paramsDomains) {
			@Override protected void onEndSuccessSync() { 
				dialog.hide(); 
				onEndSync();
			}
		};
		
		centerPanel.setWidget(domainSyncPanel);
		
		dialog.add(centerPanel);
		dialog.showLoaded();
	}

	private void onSyncCustomerDomain(Customer customer, String domainName) {
		onShowELoadingMessage("Vinculando cliente con dominio...");
		
		List<DomainCompany>    customerDomainList   = safeList(customersDomain,   customer.getId());
		List<DomainSigAddInfo> customerRaddInfoList = safeList(customersRaddInfo, customer.getId());
		
		Optional<DomainCompany> domainCompanyTmp = Optional.empty();
		if(!customerDomainList.isEmpty())
			domainCompanyTmp = customerDomainList.stream().filter(d -> AonStringUtils.equalsIgnoreCase(d.getDomain().getName(), domainName)).findFirst();
		
		if(domainCompanyTmp.isEmpty()) {
			Optional<DomainSigAddInfo> domainSigAddInfo = customerRaddInfoList.stream().filter(d -> AonStringUtils.equalsIgnoreCase(d.getDomainName(), domainName)).findFirst();
			if(!domainSigAddInfo.isEmpty()) 
				domainCompanyTmp = Optional.of(
						new DomainCompany()
							.setSchema(domainSigAddInfo.get().getDomainSchema())
							.setDomain(
								new Domain()
									.setId(Integer.parseInt(domainSigAddInfo.get().getDomainId()))
									.setName(domainSigAddInfo.get().getDomainName())
									.setDomainType(DomainType.getValues().stream().filter(dt -> AonStringUtils.equalsIgnoreCase(dt.getName(), domainSigAddInfo.get().getDomainType())).findFirst().orElse(null))
							)
							);
		}
		
		if(domainCompanyTmp.isEmpty() && !customerDomainList.isEmpty()) domainCompanyTmp = Optional.of(customerDomainList.get(0));
		if(domainCompanyTmp.isEmpty() && !customerRaddInfoList.isEmpty()) domainCompanyTmp = Optional.of(
				new DomainCompany()
				.setSchema(customerRaddInfoList.get(0).getDomainSchema())
				.setDomain(
					new Domain()
						.setId(Integer.parseInt(customerRaddInfoList.get(0).getDomainId()))
						.setName(customerRaddInfoList.get(0).getDomainName())
						.setDomainType(DomainType.getValues().stream().filter(dt -> AonStringUtils.equalsIgnoreCase(dt.getName(), customerRaddInfoList.get(0).getDomainType())).findFirst().orElse(null))
				)
				);
		
		if (domainCompanyTmp.isEmpty()) {
		    onShowErrorMessage("No se puede resolver el dominio del cliente " + customer.getId());
		    return;
		}
		DomainCompany domainCompany = domainCompanyTmp.get();
		
		COMMON_SERVICE.syncCustomer(params.getDomainName(), params.getDomainId(), params.getUser(), customer.getId(), domainCompany, true, new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {
				onShowErrorMessage("Error vinculando cliente : " + caught.getMessage());
			}

			@Override
			public void onSuccess(Void result) {
				String host = isLocalDev ? "localhost:8080" : "aon.solutions";
				String endPoint = "/ms/api/domain/sync-aon-customer";
				
				JSONObject body = new JSONObject();
				body.put("customer", new JSONString(customer.getId().toString()));
				body.put("domain_name", new JSONString(domainCompany.getDomain().getName()));
				body.put("domain_id", new JSONNumber(domainCompany.getDomain().getId()));
				body.put("user", new JSONString(""));
				
				customerApi.syncAonCustomerDomain(host, endPoint, body, new AsyncCallback<Void>() {
					
					@Override
					public void onSuccess(Void result) {
						onHideMessage();
						onEndSync();
					}
					
					@Override
					public void onFailure(Throwable caught) {
						onShowErrorMessage("Error actualizando aonCustomer del dominio vinculado : " + caught.getMessage());
					}
				});
			}
		});
	}
	
	private boolean needInfoShow(Customer customer, DomainCompany domainCompany) {
		return (null == domainCompany || !domainCompany.getDomain().getAonCustomer().equals(customer.getId()))
			|| (customer.getStatus().equals(RegistryStatus.BLOCKED) && null == domainCompany.getDomain().getExpirationDate())
			|| (customer.getStatus().equals(RegistryStatus.INACTIVE) && (null == domainCompany.getDomain().getExpirationDate() || domainCompany.getDomain().isActive()))
			
			;
	}
	
	private String getInfoMessage(Customer customer, DomainCompany domainCompany) {
		if(null == domainCompany || !domainCompany.getDomain().getAonCustomer().equals(customer.getId()))
			return "El cliente del dominio no coincide con el seleccionado";
		else if(customer.getStatus().equals(RegistryStatus.BLOCKED) && null == domainCompany.getDomain().getExpirationDate())
			return "El cliente esta bloqueado pero el dominio no tiene fecha de expiraci\u00f3n";
		else if(customer.getStatus().equals(RegistryStatus.INACTIVE) && (null == domainCompany.getDomain().getExpirationDate() || domainCompany.getDomain().isActive()))
			return "El cliente esta inactivo pero el dominio esta activo o no tiene fecha de expiraci\u00f3n";
		
		return null;
	}
	
	private static <T> List<T> safeList(Map<Integer, List<T>> map, Integer key) {
	    List<T> list = map.get(key);
	    return null != list ? list : new ArrayList<>();
	}

	protected abstract void onShowErrorMessage(String errorMessage);
	protected abstract void onShowELoadingMessage(String loadingMessage);
	protected abstract void onHideMessage();
	protected abstract void onEndSync();

}
