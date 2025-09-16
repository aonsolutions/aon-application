package com.esferalia.aon.gwt.fiscal.client.customer;

import java.util.logging.Logger;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDockLayout;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessagePanel;
import com.esferalia.aon.occam.api.model.Customer;
import com.esferalia.aon.occam.api.model.customer.CustomersDomainSyncParams;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.logging.client.ConsoleLogHandler;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.SimplePanel;

public abstract class DomainSyncPanel extends AonCustomDockLayout {

	private static final Logger LOGGER = Logger.getLogger(DomainSyncPanel.class.getName());
	static {
		LOGGER.addHandler(new ConsoleLogHandler());
	}

	private HTMLPanel container;
	private HTMLPanel messagePanel = new HTMLPanel("");

	private SimplePanel centerPanel;

	private DomainSyncTable domainSyncTable;

	private Customer customer;
	
	private CustomersDomainSyncParams paramsDomains;
	
	public DomainSyncPanel(Customer customer, CustomersDomainSyncParams paramsDomains) {
		super("Dominios");
		
		this.customer = customer;
		this.paramsDomains = paramsDomains;
		
		hideFilterButton();
		
		setSearchPlaceholder("Busqueda por documento/nombre...");
		
		getSearchTextBox().setValue(this.customer.getDocument());

		addKeyUpHandler(e -> {
			String value = getSearchTextBox().getValue();
			if (AonStringUtils.isNotBlank(value) && value.length() > 2) {
				onSearch();
			} else if (AonStringUtils.isBlank(value)) {
				onSearch();
			}
		});

		container = new HTMLPanel("");
		container.addStyleName(AON.CSS.aonFlexColumn());
		container.getElement().getStyle().setProperty("padding", "0 1rem");

		container.add(messagePanel);
		
		centerPanel = new SimpleLayoutPanel();
		centerPanel.setHeight("100%");
		centerPanel.getElement().getStyle().setProperty("margin-left", "1rem");

		container.add(centerPanel);

		add(container);
		
		onSearch();
	}

	@Override
	protected void onClearFilter() {
		domainSyncTable.resetSearchOffset();
		getSearchTextBox().setValue(null, false);

		onSearch();
	}

	public void onSearch() {
		centerPanel.clear();

		String searchQuery = getSearchTextBox().getValue();
		this.paramsDomains.setQuery(searchQuery);
		
		domainSyncTable = new DomainSyncTable(this.customer, this.paramsDomains) {

			@Override
			protected void onShowErrorMessage(String errorMessage) {
				AonMessagePanel.showError(messagePanel, errorMessage);
			}
			
			@Override
			protected void onHideMessage() {
				AonMessagePanel.hideMessage(messagePanel);
			}

			@Override
			protected void onShowELoadingMessage(String loadingMessage) {
				AonMessagePanel.showLoading(messagePanel, loadingMessage);
			}
			
			@Override
			protected void onEndSync() {
				onEndSuccessSync();
			}

		};

		centerPanel.setWidget(domainSyncTable);
	}
	
	protected abstract void onEndSuccessSync();

	public static native boolean getCurrentIsSig()
	/*-{
		var value = $wnd.localStorage.getItem("isSig");
		return value === "true" || value === true;
	}-*/;

}
