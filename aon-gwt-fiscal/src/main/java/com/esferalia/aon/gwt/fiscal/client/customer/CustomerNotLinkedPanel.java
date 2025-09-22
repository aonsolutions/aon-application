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
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomTable;
import com.esferalia.aon.gwt.fiscal.client.booking.BookingApi;
import com.esferalia.aon.occam.api.model.Customer;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.DomainCompany;
import com.esferalia.aon.occam.api.model.customer.CustomersLinkedParams;
import com.esferalia.aon.watson.mutable.MutableInt;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.event.dom.client.ScrollHandler;
import com.google.gwt.logging.client.ConsoleLogHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;

public abstract class CustomerNotLinkedPanel extends ScrollPanel {

	private static CommonServiceAsync COMMON_SERVICE;

	private static final Logger LOGGER = Logger.getLogger(CustomerNotLinkedPanel.class.getName());
	static {
		LOGGER.addHandler(new ConsoleLogHandler());
	}

	private final int limit = 100;
	private final MutableInt offset = new MutableInt(0);
	private final MutableInt moreData = new MutableInt(0);
	private final MutableInt searchEnabled = new MutableInt(0);

	private AonCustomTable tab;
	private int lastScrollPos = 0;

	private Map<Integer, Customer> rowCustomers = new HashMap<>();
	private Map<Integer, Domain> customersDomain = new HashMap<>();

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

	public CustomerNotLinkedPanel(CustomersLinkedParams params) {
		this.params = params;

		CommonServiceAsync commonServiceRaw = GWT.create(CommonService.class);
		COMMON_SERVICE = new CommonServiceAsyncDecorator(commonServiceRaw);
		
		this.customerApi = new CustomerApi(SESSION_API);
		this.bookingApi = new BookingApi(SESSION_API);

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
		
		onShowELoadingMessage("Cargando informaci\u00f3n clientes/dominios. Este proceso puede llevar unos segundos...");

		getList(customers -> {
			boolean something = false;

			for (Customer customer : customers) {
				something = true;
				paintRow(customer);
			}
			
			if(!getCurrentIsSig()) {
				if (customers.size() < limit) {
					disableMoreData();
				} else {
					offset.setValue(offset.intValue() + customers.size() - 1);
					enableMoreData();
				}
			} else {
				offset.setValue(offset.intValue() + customers.size() - 1);
				enableMoreData();
			}

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

		Label name = new Label("No existen clientes sin viculaci\u00f3n");
		name.setTitle("No existen clientes sin viculaci\u00f3n");
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

		rowCustomers.put(customer.getId(), customer);
	}

	public void resetSearchOffset() {
		offset.setValue(0);
	}

	private void getList(Consumer<List<Customer>> success) {

		params.setSig(getCurrentIsSig());
		
		params.setOffset(offset.intValue());
		params.setLimit(limit);
		
		if(getCurrentIsSig()) {
			COMMON_SERVICE.getCustomers(params, new AsyncCallback<List<Customer>>() {

				@Override
				public void onFailure(Throwable caught) {
					onShowErrorMessage("Error obteniendo clientes : " + caught.getMessage());
				}

				@Override
				public void onSuccess(List<Customer> customers) {
					getCustomersDomain(customers, 
							parsedCustomers -> {
								success.accept(parsedCustomers);
							});
				}
			});
		} else {
			COMMON_SERVICE.getCustomersNotLinked(params, new AsyncCallback<List<Customer>>() {

				@Override
				public void onFailure(Throwable caught) {
					onShowErrorMessage("Error obteniendo clientes : " + caught.getMessage());
				}

				@Override
				public void onSuccess(List<Customer> customers) {
					success.accept(customers);
				}
			});
		}
	}
	
	private void getCustomersDomain(List<Customer> customers, Consumer<List<Customer>> success) {
		checkCustomerDomains(customers.stream().map(customer -> customer.getId()).collect(Collectors.toCollection(ArrayList::new)), customerDomains -> {
			customersDomain.putAll(customerDomains);
			success.accept(customers.stream().filter(c -> customerDomains.keySet().contains(c.getId())).collect(Collectors.toList()));
		});
	}
	
	private void checkCustomerDomains(ArrayList<Integer> customerIds, Consumer<HashMap<Integer, Domain>> success) {
	    HashMap<Integer, Domain> result = new HashMap<>();
	    
	    int total = customerIds.size();
	    int[] pending = { total }; 

	    customerIds.forEach(customerId -> {
	        String host = isLocalDev ? "localhost:8080" : "aon.solutions";
	        String endPoint = "/ms/api/domain/" + customerId;

	        this.bookingApi.getDomainCompanies(host, endPoint, new AsyncCallback<List<DomainCompany>>() {
	            @Override
	            public void onSuccess(List<DomainCompany> domainCompanies) {
	                if (null == domainCompanies || domainCompanies.isEmpty()) {
	                    result.put(customerId, null);
	                }
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
	                	 success.accept(result);
	                }
	            }
	        });
	    });

	    if (total == 0) {
	    	 success.accept(result);
	    }
	}

	protected abstract void onShowErrorMessage(String errorMessage);
	protected abstract void onShowELoadingMessage(String loadingMessage);
	protected abstract void onHideMessage();

	protected abstract void onCusotmerOpen(Customer customer);

	public static native boolean getCurrentIsSig()
	/*-{
		var value = $wnd.localStorage.getItem("isSig");
		return value === "true" || value === true;
	}-*/;

}
