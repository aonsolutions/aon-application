package com.esferalia.aon.gwt.fiscal.client.customer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.logging.Logger;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.gwt.common.client.CommonServiceAsync;
import com.esferalia.aon.gwt.common.client.CommonServiceAsyncDecorator;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomTable;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog.AonAcceptDialogCallback;
import com.esferalia.aon.gwt.fiscal.client.booking.BookingApi;
import com.esferalia.aon.occam.api.model.Customer;
import com.esferalia.aon.occam.api.model.DomainCompany;
import com.esferalia.aon.occam.api.model.customer.CustomersDomainSyncParams;
import com.esferalia.aon.watson.mutable.MutableInt;
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

public abstract class DomainSyncTable extends ScrollPanel {

	private static CommonServiceAsync COMMON_SERVICE;

	private static final Logger LOGGER = Logger.getLogger(DomainSyncTable.class.getName());
	static {
		LOGGER.addHandler(new ConsoleLogHandler());
	}

	private final int limit = 100;
	private final MutableInt offset = new MutableInt(0);
	private final MutableInt moreData = new MutableInt(0);
	private final MutableInt searchEnabled = new MutableInt(0);

	private AonCustomTable tab;
	private int lastScrollPos = 0;

	private Map<Integer, DomainCompany> rowDomains = new HashMap<>();

	private BookingApi bookingApi;
	private CustomerApi customerApi;
	private static String SESSION_API = "AONd95770f269e711eb94390242ac130002";
	private boolean isLocalDev = false;

	private CustomersDomainSyncParams paramsDomains;
	private Customer customer;

	private static enum COLS {

		SCH("Esquema", "13em", "white-space: nowrap; overflow: hidden; text-overflow: ellipsis;"),
		DOC(AON.MSG.description(), "20rem", "white-space: nowrap; overflow: hidden; text-overflow: ellipsis;"),
		DES(AON.MSG.name(), "-moz-available",
				"min-width: 5rem; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;"),;

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

	public DomainSyncTable(Customer customer, CustomersDomainSyncParams paramsDomains) {
		CommonServiceAsync commonServiceRaw = GWT.create(CommonService.class);
		COMMON_SERVICE = new CommonServiceAsyncDecorator(commonServiceRaw);

		this.customer = customer;
		this.paramsDomains = paramsDomains;

		this.customerApi = new CustomerApi(SESSION_API);
		this.bookingApi = new BookingApi(SESSION_API);

		this.rowDomains.clear();

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
		for (COLS col : COLS.values()) {
			if (!getCurrentIsSig() && col.equals(COLS.SCH))
				continue;

			tab.addHeader(new Label(col.getHeaderLabel()), col.getColWidth(), col.getStyles());
		}
	}

	private void searchData() {
		if (!isMoreData())
			return;

		onShowELoadingMessage("Cargando informaci\u00f3n dominios...");

		getList(domainCompanies -> {
			boolean something = false;

			for (DomainCompany domainCompany : domainCompanies) {
				something = true;
				paintRow(domainCompany);
			}

			if (domainCompanies.size() < limit) {
				disableMoreData();
			} else {
				offset.setValue(offset.intValue() + domainCompanies.size() - 1);
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

		Label name = new Label("No existen dominios para la busqueda realizada");
		name.setTitle("No existen dominios para la busqueda realizada");
		tab.addInlineStyle(name, COLS.DES.getStyles());
		tab.addRow(row, name, COLS.DES.getColWidth());
	}

	private void paintRow(DomainCompany domainCompany) {
		HTMLPanel row = tab.createRow();
		row.setTitle("Vincular dominio");
		row.addDomHandler(e -> onDomainClick(domainCompany), ClickEvent.getType());

		if (getCurrentIsSig()) {
			Label schema = new Label(domainCompany.getSchema());
			tab.addInlineStyle(schema, COLS.SCH.getStyles());
			tab.addRow(row, schema, COLS.SCH.getColWidth());
		}

		Label description = new Label(domainCompany.getDomain().getDescription());
		tab.addInlineStyle(description, COLS.DOC.getStyles());
		tab.addRow(row, description, COLS.DOC.getColWidth());

		Label name = new Label(domainCompany.getDomain().getName());
		name.setTitle(domainCompany.getDomain().getName());
		tab.addInlineStyle(name, COLS.DES.getStyles());
		tab.addRow(row, name, COLS.DES.getColWidth());

		rowDomains.put(domainCompany.getDomain().getId(), domainCompany);
	}

	private void onDomainClick(DomainCompany domainCompany) {
		AonDialog dialog = new AonDialog("Vinculaci\u00f3n Cliente / Dominio", new Label(
				"Desea vincular el cliente con el dominio " + domainCompany.getDomain().getDescription() + " ?"));
		dialog.confirm(new AonAcceptDialogCallback() {

			@Override
			public void onCancel() {
			}

			@Override
			public void onAccept() {
				onSyncDomain(domainCompany);
			}

		});
	}

	public void resetSearchOffset() {
		offset.setValue(0);
	}

	private void getList(Consumer<List<DomainCompany>> success) {
		if (getCurrentIsSig())
			getSigDomains(success);
		else {
			COMMON_SERVICE.getAviableSyncDomains(paramsDomains, new AsyncCallback<List<DomainCompany>>() {

				@Override
				public void onFailure(Throwable caught) {
					onShowErrorMessage("Error obteniendo dominios para sincronizar : " + caught.getMessage());
				}

				@Override
				public void onSuccess(List<DomainCompany> domainCompanies) {
					success.accept(domainCompanies);
				}
			});
		}
	}

	private void getSigDomains(Consumer<List<DomainCompany>> success) {
		String host = isLocalDev ? "localhost:8080" : "aon.solutions";
		String endPoint = "/ms/api/domain/sync-aon-customer";

		JSONObject body = new JSONObject();
		body.put("document", new JSONString(paramsDomains.getQuery()));

		this.bookingApi.getDomainCompanies(host, endPoint, body, new AsyncCallback<List<DomainCompany>>() {

			@Override
			public void onSuccess(List<DomainCompany> domainCompanies) {
				success.accept(domainCompanies);
			}

			@Override
			public void onFailure(Throwable exception) {
				onShowErrorMessage("Error obteniendo clientes : " + exception.getMessage());
			}
		});
	}

	private void onSyncDomain(DomainCompany domainCompany) {
		onShowELoadingMessage("Vinculando cliente con dominio...");
		
		COMMON_SERVICE.syncCustomer(paramsDomains.getDomainName(), paramsDomains.getDomainId(), paramsDomains.getUser(), this.customer.getId(), domainCompany, getCurrentIsSig(), new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {
				onShowErrorMessage("Error vinculando cliente : " + caught.getMessage());
			}

			@Override
			public void onSuccess(Void result) {
				if(getCurrentIsSig()) {
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
					
				} else onHideMessage();
			}
		});
	}

	protected abstract void onShowErrorMessage(String errorMessage);

	protected abstract void onShowELoadingMessage(String loadingMessage);

	protected abstract void onHideMessage();

	protected abstract void onEndSync();

	public static native boolean getCurrentIsSig()
	/*-{
		var value = $wnd.localStorage.getItem("isSig");
		return value === "true" || value === true;
	}-*/;

}
