package com.esferalia.aon.gwt.payroll.client.customer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.logging.Logger;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomTable;
import com.esferalia.aon.gwt.payroll.client.DomainEnterprisesServiceAsync;
import com.esferalia.aon.occam.api.model.Customer;
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

	private String searchQuery;

	private static enum COLS {

		DOC(AON.MSG.document(), "6rem", "white-space: nowrap; overflow: hidden; text-overflow: ellipsis;"),
		DES(AON.MSG.name(), "-moz-available",
				"min-width: 5rem; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;"),
		ALI(AON.MSG.alias(), "20rem", "white-space: nowrap; overflow: hidden; text-overflow: ellipsis;");

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

	public CustomerPanel(String searchQuery) {
		this.searchQuery = searchQuery;

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

		Label alias = new Label(customer.getAlias());
		alias.setTitle(customer.getAlias());
		tab.addInlineStyle(alias, COLS.ALI.getStyles());
		tab.addRow(row, alias, COLS.ALI.getColWidth());

		rowCustomers.put(customer.getId(), customer);
	}

	public void resetSearchOffset() {
		offset.setValue(0);
	}

	private void getList(Consumer<List<Customer>> success) {

		if (getCurrentIsSig()) {

			LOGGER.info("Entrando en getSigCustomersLinked (campo de CustomerPanel)");

			service.getSigCustomersLinked(searchQuery, offset.intValue(), limit, new AsyncCallback<List<Customer>>() {

				@Override
				public void onFailure(Throwable caught) {
					onShowErrorMessage("Error obteniendo clientes : " + caught.getMessage());
				}

				@Override
				public void onSuccess(List<Customer> customers) {
					success.accept(customers);
				}
			});
		} else {
			LOGGER.info("Entrando en getCustomersLinked (campo de CustomerPanel)");
			
			service.getCustomersLinked(searchQuery, offset.intValue(), limit, new AsyncCallback<List<Customer>>() {

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

	protected abstract void onShowErrorMessage(String errorMessage);

	protected abstract void onCusotmerOpen(Integer customerId, String customerName);

	public static native boolean getCurrentIsSig()
	/*-{
		var value = $wnd.localStorage.getItem("isSig");
		return value === "true" || value === true;
	}-*/;

}
