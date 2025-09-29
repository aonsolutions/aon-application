package com.esferalia.aon.gwt.fiscal.client.customer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

	private final int limit = 30;
//	private final int limit = 3;
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

		DOC(AON.MSG.document(), "6rem", "white-space: nowrap; overflow: hidden; text-overflow: ellipsis;"),
		DES(AON.MSG.name(), "-moz-available",
				"min-width: 5rem; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;"),
		STA(AON.MSG.status(), "6rem", "white-space: nowrap; overflow: hidden; text-overflow: ellipsis;"),
		RAD("RaddInfo", "5rem", "white-space: nowrap; overflow: hidden; text-overflow: ellipsis; justify-content: center;"),
		ACU("Aon Customer", "7rem", "white-space: nowrap; overflow: hidden; text-overflow: ellipsis; justify-content: center;"),
		BTN(AonStringUtils.EMPTY, "5rem", "white-space: nowrap; overflow: hidden; text-overflow: ellipsis;"),;

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
		getElement().getStyle().setProperty("margin", "0 1rem");

		paintHeader();
		searchData();
	}

	private void paintHeader() {
		tab.createHeader();
		for (COLS col : COLS.values())
			tab.addHeader(new Label(col.getHeaderLabel()), col.getColWidth(), col.getStyles());
	}

	private void searchData() {
		if (!isMoreData())
			return;

		onShowELoadingMessage(
				"Cargando informaci\u00f3n clientes/dominios. Este proceso puede llevar unos segundos...");

		getList(customers -> {
			boolean something = false;

			for (Customer customer : customers) {
				something = true;
				paintRow(customer);
			}

			offset.setValue(offset.intValue() + customers.size() - 1);
			enableMoreData();

			if (!something) {
				paintEmptyRow();
				disableMoreData();
			}
			enableSearch();

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

		Label document = new Label(customer.getDocument());
		tab.addInlineStyle(document, COLS.DOC.getStyles());
		tab.addRow(row, document, COLS.DOC.getColWidth());

		Label name = new Label(customer.getName());
		name.setTitle(customer.getName());
		tab.addInlineStyle(name, COLS.DES.getStyles());
		tab.addRow(row, name, COLS.DES.getColWidth());

		Label customerStatus = new Label(customer.getStatus().getDescription());
		customerStatus.setTitle(customer.getStatus().getDescription());
		tab.addInlineStyle(customerStatus, COLS.STA.getStyles());
		tab.addRow(row, customerStatus, COLS.STA.getColWidth());

		HTMLPanel customerRaddInfoPanel = new HTMLPanel(AonStringUtils.EMPTY);
		customerRaddInfoPanel.addStyleName(AON.CSS.aonItemFlex());
		List<DomainSigAddInfo> customerRaddInfo = customersRaddInfo.get(customer.getId());
		if(customerRaddInfo.size() > 1) {
			
			customerRaddInfo.forEach(ds -> {
				AonTableButton raddInfoBtn = new AonTableButton("Vinculado", AON.CSS.aonIconCheckCircle());
				customerRaddInfoPanel.add(raddInfoBtn);
			});
			
		} else {
			AonTableButton raddInfoBtn = new AonTableButton(customerRaddInfo.isEmpty() ? "No vinculado" : "Vinculado",
					customerRaddInfo.isEmpty() ? AON.CSS.aonIconBlock() : AON.CSS.aonIconCheckCircle());

			customerRaddInfoPanel.add(raddInfoBtn);
		}
		tab.addInlineStyle(customerRaddInfoPanel, COLS.RAD.getStyles());
		tab.addRow(row, customerRaddInfoPanel, COLS.RAD.getColWidth());

		HTMLPanel customerDomainPanel = new HTMLPanel(AonStringUtils.EMPTY);
		customerDomainPanel.addStyleName(AON.CSS.aonItemFlex());
		List<DomainCompany> customerDomain = customersDomain.get(customer.getId());
		if(customerDomain.size() > 1) {
			
			customerRaddInfo.forEach(ds -> {
				AonTableButton raddInfoBtn = new AonTableButton("Vinculado", AON.CSS.aonIconCheckCircle());
				customerDomainPanel.add(raddInfoBtn);
			});
			
		} else {
			AonTableButton customerDomainBtn = new AonTableButton(
					customerDomain.isEmpty() ? "No vinculado" : "Vinculado",
					customerDomain.isEmpty() ? AON.CSS.aonIconBlock() : AON.CSS.aonIconCheckCircle());
			

			customerDomainPanel.add(customerDomainBtn);
		}
		tab.addInlineStyle(customerDomainPanel, COLS.ACU.getStyles());
		tab.addRow(row, customerDomainPanel, COLS.ACU.getColWidth());

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
				onCustomerClick(customer, null);
			});
		}
		tab.addInlineStyle(optionBtn, COLS.BTN.getStyles());
		tab.addRow(row, optionBtn, COLS.BTN.getColWidth());

		rowCustomers.put(customer.getId(), customer);
	}
	
	private void onCustomerClick(Customer customer, String domainName) {
		AonDialog dialog = new AonDialog("Vinculaci\u00f3n Cliente / Dominio", new Label(
				"Desea sincronizar la vinculaci\u00f3n del cliente " + customer.getName() + " ?"));
		dialog.confirm(new AonAcceptDialogCallback() {

			@Override
			public void onCancel() {}

			@Override
			public void onAccept() {
				onSyncCustomerDomain(customer, domainName);
			}

		});
	}

	private void onCusotmerOpen(Customer customer) {
		SimplePanel centerPanel = new SimpleLayoutPanel();
		centerPanel.setHeight("25rem");
		centerPanel.setWidth("100%");
		
		List<DomainSigAddInfo> customerRaddInfo = customersRaddInfo.get(customer.getId());
		List<DomainCompany> customerDomain = customersDomain.get(customer.getId());
		
		SyncSigMultipleDomainsTable table = new SyncSigMultipleDomainsTable(customer.getId(), customerRaddInfo, customerDomain) {
			
			@Override
			protected void onClickRow(DomainCustomerSync domainCustomerSync) {
				onCustomerClick(customer, domainCustomerSync.getDomainName());
			}
			
		};
		
		centerPanel.setWidget(table);
		
		AonDialog dialog = new AonDialog(customer.getName() + " (" + customer.getId() + ")", centerPanel);
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
		params.setLimit(limit);
		
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
								success.accept(customers);
							});
							
				});
				
			}
		});
	}

	private void checkCustomerDomains(List<Integer> customerIds, Consumer<Void> success) {
		int total = customerIds.size();
		int[] pending = { total };

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
					onShowErrorMessage(exception.getMessage());
					checkFinish();
				}

				private void checkFinish() {
					pending[0]--;
					if (pending[0] == 0) {
						success.accept(null);
					}
				}
			});
		});

		if (total == 0) {
			success.accept(null);
		}
	}

	private void checkCustomerRaddInfos(List<Integer> customerIds, Consumer<Void> success) {
		int total = customerIds.size();
		int[] pending = { total };

		customerIds.forEach(customerId -> {
			COMMON_SERVICE.getDomainSigAddInfo(params.getDomainName(), params.getDomainId(), params.getUser(),
					customerId, new AsyncCallback<List<DomainSigAddInfo>>() {

						@Override
						public void onFailure(Throwable caught) {
							onShowErrorMessage(caught.getMessage());
							checkFinish();
						}

						@Override
						public void onSuccess(List<DomainSigAddInfo> customerDomainSigAddInfo) {
							customersRaddInfo.put(customerId, customerDomainSigAddInfo);
							checkFinish();
						}

						private void checkFinish() {
							pending[0]--;
							if (pending[0] == 0) {
								success.accept(null);
							}
						}

					});
		});

		if (total == 0) {
			success.accept(null);
		}
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
				onSearch();
			}
		};
		
		centerPanel.setWidget(domainSyncPanel);
		
		dialog.add(centerPanel);
		dialog.showLoaded();
	}

	private void onSyncCustomerDomain(Customer customer, String domainName) {
		onShowELoadingMessage("Vinculando cliente con dominio...");
		
		List<DomainCompany> customerDomainList = customersDomain.get(customer.getId());
		List<DomainSigAddInfo> customerRaddInfoList = customersRaddInfo.get(customer.getId());
		
		DomainCompany domainCompanyTmp = new DomainCompany();
		if(!customerDomainList.isEmpty())
			domainCompanyTmp = AonStringUtils.isBlank(domainName) ? customerDomainList.get(0) : customerDomainList.stream().filter(d -> AonStringUtils.equalsIgnoreCase(d.getDomain().getName(), domainName)).findFirst().orElse(customerDomainList.get(0));
		else {
			 DomainSigAddInfo domainSigAddInfo = AonStringUtils.isBlank(domainName) ? customerRaddInfoList.get(0) : customerRaddInfoList.stream().filter(d -> AonStringUtils.equalsIgnoreCase(d.getDomainName(), domainName)).findFirst().orElse(customerRaddInfoList.get(0));
			 domainCompanyTmp = new DomainCompany()
						.setSchema(domainSigAddInfo.getDomainSchema())
						.setDomain(
							new Domain()
								.setId(Integer.parseInt(domainSigAddInfo.getDomainId()))
								.setName(domainSigAddInfo.getDomainName())
								.setDomainType(DomainType.getValues().stream().filter(dt -> AonStringUtils.equalsIgnoreCase(dt.getName(), domainSigAddInfo.getDomainType())).findFirst().orElse(null))
						)
						;
		}
		
		DomainCompany domainCompany = domainCompanyTmp;
		
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

	protected abstract void onShowErrorMessage(String errorMessage);
	protected abstract void onShowELoadingMessage(String loadingMessage);
	protected abstract void onHideMessage();
	protected abstract void onEndSync();

}
