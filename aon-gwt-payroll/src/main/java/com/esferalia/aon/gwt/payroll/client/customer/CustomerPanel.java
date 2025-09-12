package com.esferalia.aon.gwt.payroll.client.customer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.AonDateUtils;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomTable;
import com.esferalia.aon.gwt.payroll.client.DomainEnterprisesServiceAsync;
import com.esferalia.aon.occam.api.model.Customer;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.watson.mutable.MutableInt;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.event.dom.client.ScrollHandler;
import com.google.gwt.logging.client.ConsoleLogHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;

public abstract class CustomerPanel extends ScrollPanel {

	private final DomainEnterprisesServiceAsync service = DomainEnterprisesServiceAsync.newInstance();

	private static final Logger LOGGER = Logger.getLogger(CustomerPanel.class.getName());
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

	private String searchQuery;
	private Byte[] customerStatusSearch;

	private static enum COLS {

		DOC(AON.MSG.document(), "6rem", "white-space: nowrap; overflow: hidden; text-overflow: ellipsis;"),
		DES(AON.MSG.name(), "-moz-available",
				"min-width: 5rem; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;"),
		STA(AON.MSG.status(), "6rem", "white-space: nowrap; overflow: hidden; text-overflow: ellipsis;"),
		DOM("Dominio", "16rem", "white-space: nowrap; overflow: hidden; text-overflow: ellipsis;"),
		STD(AON.MSG.status(), "6rem", "white-space: nowrap; overflow: hidden; text-overflow: ellipsis;"),
		EXP("F. Expiraci\u00f3n", "7rem", "white-space: nowrap; overflow: hidden; text-overflow: ellipsis;"),
		LST("F. Ult. Acceso", "7rem", "white-space: nowrap; overflow: hidden; text-overflow: ellipsis;"),;

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

	public CustomerPanel(String searchQuery, Byte[] customerStatusSearch) {
		this.searchQuery = searchQuery;
		this.customerStatusSearch = customerStatusSearch;

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

		getList(customers -> {
			boolean something = false;

			for (Customer customer : customers) {
				something = true;
				paintRow(customer);
			}

			if (customers.size() < limit) {
				disableMoreData();
			} else {
				offset.setValue(offset.intValue() + customers.size() - 1);
				enableMoreData();
			}

			if (!something) {
				paintEmptyRow();
				disableMoreData();
			}
			enableSearch();

		});

	}

	private void paintEmptyRow() {
		HTMLPanel row = tab.createRow();

		Label name = new Label("No existen clientes viculados");
		name.setTitle("No existen clientes viculados");
		tab.addInlineStyle(name, COLS.DES.getStyles());
		tab.addRow(row, name, COLS.DES.getColWidth());
	}

	private void paintRow(Customer customer) {
		HTMLPanel row = tab.createRow();
		row.addDomHandler(e -> onCusotmerOpen(customer.getId(), customer.getName()), ClickEvent.getType());

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
		
		Domain customerDomain = customersDomain.get(customer.getId());

		Label domainName = new Label(null == customerDomain || null == customerDomain.getId() ? "" : customerDomain.getName());
		domainName.setTitle(null == customerDomain || null == customerDomain.getId() ? "" : customerDomain.getName());
		tab.addInlineStyle(domainName, COLS.DOM.getStyles());
		tab.addRow(row, domainName, COLS.DOM.getColWidth());

		Label domainStatus = new Label(null == customerDomain || null == customerDomain.getId() ? "" : customerDomain.isActive() ? "Activo" : "Inactivo");
		domainStatus.setTitle(null == customerDomain || null == customerDomain.getId() ? "" : customerDomain.getAonStatus().getName());
		tab.addInlineStyle(domainStatus, COLS.STD.getStyles());
		tab.addRow(row, domainStatus, COLS.STD.getColWidth());

		Label domainExpirationDate = new Label(null == customerDomain || null == customerDomain.getId() ? "" : AonDateUtils.formatDate(customerDomain.getExpirationDate()));
		tab.addInlineStyle(domainExpirationDate, COLS.EXP.getStyles());
		tab.addRow(row, domainExpirationDate, COLS.EXP.getColWidth());

		Label domainLastAccessDate = new Label(null == customerDomain || null == customerDomain.getId() ? "" : AonDateUtils.formatDate(customerDomain.getLastAccessDate()));
		tab.addInlineStyle(domainLastAccessDate, COLS.LST.getStyles());
		tab.addRow(row, domainLastAccessDate, COLS.LST.getColWidth());

		rowCustomers.put(customer.getId(), customer);
	}

	public void resetSearchOffset() {
		offset.setValue(0);
	}

	private void getList(Consumer<List<Customer>> success) {

		if (getCurrentIsSig()) {

			LOGGER.info("Entrando en getSigCustomersLinked (campo de CustomerPanel)");

			service.getSigCustomersLinked(searchQuery, customerStatusSearch, offset.intValue(), limit, new AsyncCallback<List<Customer>>() {

				@Override
				public void onFailure(Throwable caught) {
					onShowErrorMessage("Error obteniendo clientes : " + caught.getMessage());
				}

				@Override
				public void onSuccess(List<Customer> customers) {

					getCustomersDomain(customers.stream().map(customer -> customer.getId())
							.collect(Collectors.toCollection(ArrayList::new)), end -> {
								success.accept(customers);
							});
				}
			});
		} else {
			LOGGER.info("Entrando en getCustomersLinked (campo de CustomerPanel)");

			service.getCustomersLinked(searchQuery, customerStatusSearch, offset.intValue(), limit, new AsyncCallback<List<Customer>>() {

				@Override
				public void onFailure(Throwable caught) {
					onShowErrorMessage("Error obteniendo clientes : " + caught.getMessage());
				}

				@Override
				public void onSuccess(List<Customer> customers) {
					getCustomersDomain(customers.stream().map(customer -> customer.getId())
							.collect(Collectors.toCollection(ArrayList::new)), end -> {
								success.accept(customers);
							});
				}
			});
		}
	}

	private void getCustomerDomain(Integer customerId, Consumer<Domain> success) {
		service.getCustomerDomain(customerId, getCurrentIsSig(), new AsyncCallback<Domain>() {

			@Override
			public void onFailure(Throwable caught) {
				onShowErrorMessage("Error obteniendo dominio cliente : " + caught.getMessage());
			}

			@Override
			public void onSuccess(Domain customerDomain) {
				success.accept(customerDomain);
			}
		});
	}

	private void getCustomersDomain(ArrayList<Integer> customerIds, Consumer<HashMap<Integer, Domain>> success) {
		service.getCustomersDomain(customerIds, getCurrentIsSig(), new AsyncCallback<HashMap<Integer, Domain>>() {

			@Override
			public void onFailure(Throwable caught) {
				onShowErrorMessage("Error obteniendo dominio cliente : " + caught.getMessage());
			}

			@Override
			public void onSuccess(HashMap<Integer, Domain> result) {
				customersDomain.putAll(result);
				success.accept(result);
			}
		});
	}

	protected abstract void onShowErrorMessage(String errorMessage);

	protected abstract void onCusotmerOpen(Integer customerId, String customerName);

	public static native boolean getCurrentIsSig()
	/*-{
		var value = $wnd.localStorage.getItem("isSig");
		return value === "true" || value === true;
	}-*/;

}
