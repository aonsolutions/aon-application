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
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTableButton;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog.AonAcceptDialogCallback;
import com.esferalia.aon.gwt.fiscal.client.booking.BookingApi;
import com.esferalia.aon.occam.api.model.Customer;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.DomainCompany;
import com.esferalia.aon.occam.api.model.customer.CustomersLinkedParams;
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
import com.google.gwt.user.client.ui.Widget;

public abstract class SigCustomerDomainPanel extends ScrollPanel {

	private static CommonServiceAsync COMMON_SERVICE;

	private static final Logger LOGGER = Logger.getLogger(SigCustomerDomainPanel.class.getName());
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
		RAD("RaddInfo", "5rem", "white-space: nowrap; overflow: hidden; text-overflow: ellipsis;"),
		ACU("Aon Customer", "7rem", "white-space: nowrap; overflow: hidden; text-overflow: ellipsis;"),
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

		List<DomainSigAddInfo> customerRaddInfo = customersRaddInfo.get(customer.getId());
		AonTableButton raddInfoBtn = new AonTableButton(customerRaddInfo.isEmpty() ? "No vinculado" : "Vinculado",
				customerRaddInfo.isEmpty() ? AON.CSS.aonIconBlock() : AON.CSS.aonIconCheckCircle());
		tab.addInlineStyle(raddInfoBtn, COLS.RAD.getStyles());
		tab.addRow(row, raddInfoBtn, COLS.RAD.getColWidth());

		List<DomainCompany> customerDomain = customersDomain.get(customer.getId());
		AonTableButton customerDomainBtn = new AonTableButton(
				customerDomain.isEmpty() ? "No vinculado" : "Vinculado",
				customerDomain.isEmpty() ? AON.CSS.aonIconBlock() : AON.CSS.aonIconCheckCircle());
		tab.addInlineStyle(customerDomainBtn, COLS.ACU.getStyles());
		tab.addRow(row, customerDomainBtn, COLS.ACU.getColWidth());

		Widget optionBtn;
		if ((customerDomain.isEmpty() && customerRaddInfo.isEmpty()) || (!customerDomain.isEmpty() && !customerRaddInfo.isEmpty()))
			optionBtn = new Label();
		else {
			optionBtn = new AonTableButton("Sincronizar", AON.CSS.aonIconSync());
			((AonTableButton) optionBtn).addClickHandler(e -> {
				e.stopPropagation();
				onCustomerClick(customer);
			});
		}
		tab.addInlineStyle(optionBtn, COLS.BTN.getStyles());
		tab.addRow(row, optionBtn, COLS.BTN.getColWidth());

		rowCustomers.put(customer.getId(), customer);
	}
	
	private void onCustomerClick(Customer customer) {
		AonDialog dialog = new AonDialog("Vinculaci\u00f3n Cliente / Dominio", new Label(
				"Desea sincronizar la vinculaci\u00f3n del cliente " + customer.getName() + " ?"));
		dialog.confirm(new AonAcceptDialogCallback() {

			@Override
			public void onCancel() {
			}

			@Override
			public void onAccept() {
				onSyncCustomerDomain(customer);
			}

		});
	}

	private void onSyncCustomerDomain(Customer customer) {
		onShowELoadingMessage("Vinculando cliente con dominio...");
		
		List<DomainCompany> customerDomainList = customersDomain.get(customer.getId());
		List<DomainSigAddInfo> customerRaddInfoList = customersRaddInfo.get(customer.getId());
		
		DomainCompany domainCompany = !customerDomainList.isEmpty() 
				? customerDomainList.get(0) 
				: new DomainCompany()
					.setSchema(customerRaddInfoList.get(0).getDomainSchema())
					.setDomain(
						new Domain()
							.setId(Integer.parseInt(customerRaddInfoList.get(0).getDomainId()))
							.setName(customerRaddInfoList.get(0).getDomainName())
							.setDomainType(DomainType.getValues().stream().filter(dt -> AonStringUtils.equalsIgnoreCase(dt.getName(), customerRaddInfoList.get(0).getDomainType())).findFirst().orElse(null))
					)
					;
		
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

	private void onCusotmerOpen(Customer customer) {
		HTMLPanel info = new HTMLPanel(AonStringUtils.EMPTY);
		info.addStyleName(AON.CSS.aonItemFlex());
		info.addStyleName(AON.CSS.aonFlexColumn());
		info.getElement().getStyle().setProperty("align-items", "start");
		
		List<DomainSigAddInfo> customerRaddInfo = customersRaddInfo.get(customer.getId());
		
		Label customerRaddInfoLabel = new Label("Otros Datos");
		customerRaddInfoLabel.addStyleName(AON.CSS.aonBold());
		info.add(customerRaddInfoLabel);
		
		Widget customerRaddInfoValue;
		if (customerRaddInfo.isEmpty()){
			customerRaddInfoValue = new Label("No existen datos");
			customerRaddInfoValue.getElement().getStyle().setProperty("margin-left", "1rem");
		} else {
			
			customerRaddInfoValue = new HTMLPanel(AonStringUtils.EMPTY);
			((HTMLPanel) customerRaddInfoValue).addStyleName(AON.CSS.aonItemFlex());
			((HTMLPanel) customerRaddInfoValue).addStyleName(AON.CSS.aonFlexColumn());
			((HTMLPanel) customerRaddInfoValue).getElement().getStyle().setProperty("margin-left", "1rem");
			
			customerRaddInfo.forEach(i -> ((HTMLPanel) customerRaddInfoValue).add(new Label(i.getDomainId() + " - " + i.getDomainName() + " (" + i.getDomainSchema() + ") [" + i.getDomainType() + "]")));
			
		}
		info.add(customerRaddInfoValue);
		
		List<DomainCompany> customerDomain = customersDomain.get(customer.getId());
		
		Label customerDomainoLabel = new Label("Aon Customer");
		customerDomainoLabel.addStyleName(AON.CSS.aonBold());
		info.add(customerDomainoLabel);
		
		Widget customerDomainValue;
		if (customerDomain.isEmpty()){
			customerDomainValue = new Label("No existen datos");
			customerDomainValue.getElement().getStyle().setProperty("margin-left", "1rem");
		} else {
			
			customerDomainValue = new HTMLPanel(AonStringUtils.EMPTY);
			((HTMLPanel) customerDomainValue).addStyleName(AON.CSS.aonItemFlex());
			((HTMLPanel) customerDomainValue).addStyleName(AON.CSS.aonFlexColumn());
			((HTMLPanel) customerDomainValue).getElement().getStyle().setProperty("margin-left", "1rem");
			
			customerDomain.forEach(i -> ((HTMLPanel) customerDomainValue).add(new Label(i.getDomain().getId() + " - " + i.getDomain().getName() + " (" + i.getSchema() + ")")));
			
		}
		info.add(customerDomainValue);
		
		AonDialog dialog = new AonDialog(customer.getName(), info);
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

	protected abstract void onShowErrorMessage(String errorMessage);
	protected abstract void onShowELoadingMessage(String loadingMessage);
	protected abstract void onHideMessage();
	protected abstract void onEndSync();

}
