package com.esferalia.aon.gwt.fiscal.client.registry;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.logging.Logger;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.RegistryService;
import com.esferalia.aon.gwt.common.client.RegistryServiceAsync;
import com.esferalia.aon.gwt.common.client.RegistryServiceAsyncDecorator;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomTable;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTableButton;
import com.esferalia.aon.occam.api.model.Customer;
import com.esferalia.aon.occam.api.model.RegistryParams;
import com.esferalia.aon.watson.mutable.MutableInt;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.event.dom.client.ScrollHandler;
import com.google.gwt.logging.client.ConsoleLogHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;

public abstract class CustomerPanel extends ScrollPanel {

	private static RegistryServiceAsync REGISTRY_SERVICE;
	
	private static final Logger LOGGER = Logger.getLogger(CustomerPanel.class.getName());
	static { LOGGER.addHandler( new ConsoleLogHandler() ); }
	
	private final int limit = 100;
	private final MutableInt offset = new MutableInt(0);
	private final MutableInt moreData = new MutableInt(0);
	private final MutableInt searchEnabled = new MutableInt( 0 );
	
	private AonCustomTable tab;
	private int lastScrollPos = 0;
	
	private RegistryParams params;
	private Map<Integer, Customer> rowCustomers = new HashMap<>();
	
	private SimplePanel parentPanel;
	
	private static enum COLS {
		  DOC(AON.MSG.document()					,"6rem"				,"white-space: nowrap; overflow: hidden; text-overflow: ellipsis;")
		, DES(AON.MSG.name()						,"-moz-available"	,"min-width: 5rem; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;")
		, BUD(AON.MSG.alias()						,"15rem"			,"white-space: nowrap; overflow: hidden; text-overflow: ellipsis;")
		, ACT("Estado"								,"6rem"				,"white-space: nowrap; overflow: hidden; text-overflow: ellipsis;")
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

	public CustomerPanel(RegistryParams params, SimplePanel centerPanel) {
		RegistryServiceAsync registryServiceRaw = GWT.create(RegistryService.class);
		REGISTRY_SERVICE = new RegistryServiceAsyncDecorator(registryServiceRaw);
		
		this.parentPanel = centerPanel;
		
		this.params = params;
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
		for ( COLS col : COLS.values()) 
			tab.addHeader(new Label(col.getHeaderLabel()), col.getColWidth(), col.getStyles());
		
		if(params.getDomainName().contains("aonsolutions.org")) {
			tab.addHeader(new Label(""), "3rem", "white-space: nowrap; overflow: hidden; text-overflow: ellipsis;");
		}
	}
	
	private void searchData() {
		if (!isMoreData()) return;
		
		getList(customers -> {
			boolean something = false;
			
			for(Customer customer : customers) {
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
				FlowPanel line = new FlowPanel();
				InlineLabel label = new InlineLabel(AON.MSG.noData());
				line.add(label);
				parentPanel.clear();
				parentPanel.add(line);
				disableMoreData();
			}
			
			enableSearch();
			
		});
	}
	
	private void paintRow(Customer customer) {
		HTMLPanel row = tab.createRow();
		row.addDomHandler(e -> onCustomerOpen(customer), ClickEvent.getType());
		
		Label document = new Label(customer.getDocument());
		document.setTitle(customer.getDocument());
		tab.addInlineStyle(document, COLS.DOC.getStyles());
		tab.addRow(row, document, COLS.DOC.getColWidth());
		
		Label name = new Label(customer.getName());
		name.setTitle(customer.getName());
		tab.addInlineStyle(name, COLS.DES.getStyles());
		tab.addRow(row, name, COLS.DES.getColWidth());
		
		Label alias = new Label(customer.getAlias());
		alias.setTitle(customer.getAlias());
		tab.addInlineStyle(alias, COLS.BUD.getStyles());
		tab.addRow(row, alias, COLS.BUD.getColWidth());
		
		String statusValue = customer.getStatus().getDescription();
		Label status = new Label(statusValue);
		status.setTitle(statusValue);
		tab.addInlineStyle(status, COLS.ACT.getStyles());
		tab.addRow(row, status, COLS.ACT.getColWidth());
		
		if(params.getDomainName().contains("aonsolutions.org")) {
			AonTableButton showCustomer = new AonTableButton("Ver nuevo", AON.CSS.aonIconInfo());
			showCustomer.addClickHandler(e -> {
				e.stopPropagation();
				onCustomerOpenNew(customer);
			});
			tab.addInlineStyle(showCustomer,"white-space: nowrap; overflow: hidden; text-overflow: ellipsis;");
			tab.addRow(row, showCustomer, "3rem");
		}
		
		rowCustomers.put(customer.getId(), customer);
	}
	
	private void getList(Consumer<List<Customer>> success) {
		REGISTRY_SERVICE.getCustomers(
				params.getDomainName(),
				params.getDomain(),
				params.getUser(), 
				params, 
				offset.intValue(), 
				limit,
				new AsyncCallback<LinkedList<Customer>>() {
					@Override
					public void onSuccess(LinkedList<Customer> result) {
						success.accept(result);
					}
							
					@Override
					public void onFailure(Throwable caught) {
						onShowErrorMessage(caught.getMessage());
					}
				});	
	}
	
	public void resetSearchOffset() {
		offset.setValue(0);
		rowCustomers.clear();
	}

	protected abstract void onCustomerOpen(Customer customer);
	protected abstract void onCustomerOpenNew(Customer customer);
	protected abstract void onShowErrorMessage(String message);
	
}

