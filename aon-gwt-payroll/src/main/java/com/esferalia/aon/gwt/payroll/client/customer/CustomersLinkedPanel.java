package com.esferalia.aon.gwt.payroll.client.customer;

import java.util.logging.Logger;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDockLayout;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessagePanel;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.logging.client.ConsoleLogHandler;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.SimplePanel;


public abstract class CustomersLinkedPanel extends AonCustomDockLayout {
	
	private static final Logger LOGGER = Logger.getLogger(CustomersLinkedPanel.class.getName());
	static {
		LOGGER.addHandler(new ConsoleLogHandler());
	}
	
	private HTMLPanel container;
	private HTMLPanel messagePanel = new HTMLPanel("");
	
	private SimplePanel centerPanel;
	
	private CustomerPanel customerPanel;
	
	public CustomersLinkedPanel() {
		super("Clientes vinculados");
		
		hideFilterButton();
		
		setSearchPlaceholder("Busqueda por documento/nombre...");
		
		addKeyUpHandler(e -> {
			String value = getSearchTextBox().getValue();
			if(AonStringUtils.isNotBlank(value) && value.length() > 2) {
				onSearch();
			} else if(AonStringUtils.isBlank(value)) {
				onSearch();
			}
		});
		
		container = new HTMLPanel("");
		container.addStyleName(AON.CSS.aonFlexColumn());
		
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
		customerPanel.resetSearchOffset();
		getSearchTextBox().setValue(null, false);
		onSearch();
	}

	
	public void onSearch() {
		centerPanel.clear();
		
		String searchQuery = getSearchTextBox().getValue();
		
		customerPanel = new CustomerPanel(searchQuery) {

			@Override
			protected void onShowErrorMessage(String errorMessage) {
				AonMessagePanel.showError(messagePanel, errorMessage);
			}

			@Override
			protected void onCusotmerOpen(Integer customerId, String customerName) {
				onCustomerSelect(customerId, customerName);
			}
		
		};
		
		centerPanel.setWidget(customerPanel);
	}
	
	protected abstract void onCustomerSelect(Integer customerId, String customerName);

}
