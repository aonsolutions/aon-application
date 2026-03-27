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
import com.esferalia.aon.occam.api.model.RegistryParams;
import com.esferalia.aon.occam.api.model.registry.Supplier;
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

public abstract class SupplierPanel extends ScrollPanel {

	private static RegistryServiceAsync REGISTRY_SERVICE;
	
	private static final Logger LOGGER = Logger.getLogger(SupplierPanel.class.getName());
	static { LOGGER.addHandler( new ConsoleLogHandler() ); }
	
	private final int limit = 100;
	private final MutableInt offset = new MutableInt(0);
	private final MutableInt moreData = new MutableInt(0);
	private final MutableInt searchEnabled = new MutableInt( 0 );
	
	private AonCustomTable tab;
	private int lastScrollPos = 0;
	
	private RegistryParams params;
	private Map<Integer, Supplier> rowSuppliers = new HashMap<>();
	
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

	public SupplierPanel(RegistryParams params, SimplePanel centerPanel) {
		RegistryServiceAsync registryServiceRaw = GWT.create(RegistryService.class);
		REGISTRY_SERVICE = new RegistryServiceAsyncDecorator(registryServiceRaw);
		
		this.parentPanel = centerPanel;
		
		this.params = params;
		this.rowSuppliers.clear();

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
	}
	
	private void searchData() {
		if (!isMoreData()) return;
		
		getList(suppliers -> {
			boolean something = false;
			
			for(Supplier supplier : suppliers) {
				something = true;
				paintRow(supplier);
			}
			
			if (suppliers.size() < limit) {
				disableMoreData();
			} else {
				offset.setValue(offset.intValue() + suppliers.size() - 1);
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
	
	private void paintRow(Supplier supplier) {
		HTMLPanel row = tab.createRow();
		row.addDomHandler(e -> onSupplierOpen(supplier), ClickEvent.getType());
		
		Label document = new Label(supplier.getDocument());
		document.setTitle(supplier.getDocument());
		tab.addInlineStyle(document, COLS.DOC.getStyles());
		tab.addRow(row, document, COLS.DOC.getColWidth());
		
		Label name = new Label(supplier.getName());
		name.setTitle(supplier.getName());
		tab.addInlineStyle(name, COLS.DES.getStyles());
		tab.addRow(row, name, COLS.DES.getColWidth());
		
		Label alias = new Label(supplier.getAlias());
		alias.setTitle(supplier.getAlias());
		tab.addInlineStyle(alias, COLS.BUD.getStyles());
		tab.addRow(row, alias, COLS.BUD.getColWidth());
		
		String statusValue = supplier.getStatus().getDescription();
		Label status = new Label(statusValue);
		status.setTitle(statusValue);
		tab.addInlineStyle(status, COLS.ACT.getStyles());
		tab.addRow(row, status, COLS.ACT.getColWidth());
		
		rowSuppliers.put(supplier.getId(), supplier);
	}
	
	private void getList(Consumer<List<Supplier>> success) {
		REGISTRY_SERVICE.getSuppliers(
				params.getDomainName(),
				params.getDomain(),
				params.getUser(), 
				params, 
				offset.intValue(), 
				limit,
				new AsyncCallback<LinkedList<Supplier>>() {
					@Override
					public void onSuccess(LinkedList<Supplier> result) {
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
		rowSuppliers.clear();
	}

	protected abstract void onSupplierOpen(Supplier supplier);
	protected abstract void onShowErrorMessage(String message);
	
}

