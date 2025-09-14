package com.esferalia.aon.gwt.fiscal.client.customer;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.logging.Logger;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDockLayout;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomMultiSelectBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessagePanel;
import com.esferalia.aon.occam.api.model.customer.CustomersLinkedParams;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.logging.client.ConsoleLogHandler;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.SimplePanel;

public abstract class CustomersNotLinkedPanel extends AonCustomDockLayout {

	private static final Logger LOGGER = Logger.getLogger(CustomersNotLinkedPanel.class.getName());
	static {
		LOGGER.addHandler(new ConsoleLogHandler());
	}

	private HTMLPanel container;
	private HTMLPanel messagePanel = new HTMLPanel("");

	private AonCustomMultiSelectBox customerStatus = new AonCustomMultiSelectBox("Estado Cliente");

	private SimplePanel centerPanel;

	private CustomerNotLinkedPanel customerNotLinkedPanel;
	
	private CustomersLinkedParams params;

	public CustomersNotLinkedPanel(CustomersLinkedParams params) {
		super("Clientes sin vinculaci\u00f3n");

		this.params = params;
		
		setSearchPlaceholder("Busqueda por documento/nombre...");

		addKeyUpHandler(e -> {
			String value = getSearchTextBox().getValue();
			if (AonStringUtils.isNotBlank(value) && value.length() > 2) {
				onSearch();
			} else if (AonStringUtils.isBlank(value)) {
				onSearch();
			}
		});

		Set<String> customerStatusoptions = new LinkedHashSet<String>();
		customerStatusoptions.add("Activo");
		customerStatusoptions.add("Inactivo");
		customerStatusoptions.add("Bloqueado");
		customerStatus.setOptions(customerStatusoptions);

		customerStatus.addBlurHandler(new BlurHandler() {
			@Override
			public void onBlur(BlurEvent event) {
				if (customerStatus.getSelectedOptions().isEmpty()) {
					Set<String> selectedOptions = new LinkedHashSet<String>();
					selectedOptions.add("Activo");
					customerStatus.setSelectedOptions(selectedOptions);
				}

				onSearch();
			}
		});

		Set<String> selectedOptions = new LinkedHashSet<String>();
		selectedOptions.add("Activo");
		customerStatus.setSelectedOptions(selectedOptions);

		addFilterWidget(customerStatus);

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
		customerNotLinkedPanel.resetSearchOffset();
		getSearchTextBox().setValue(null, false);

		Set<String> selectedOptions = new LinkedHashSet<String>();
		selectedOptions.add("Activo");
		customerStatus.setSelectedOptions(selectedOptions);

		onSearch();
	}

	public void onSearch() {
		centerPanel.clear();

		String searchQuery = getSearchTextBox().getValue();
		Byte[] customerStatusSearch = mapStatus(customerStatus.getSelectedOptions());
		
		this.params.setQuery(searchQuery);
		this.params.setCustomerStatus(customerStatusSearch);

		customerNotLinkedPanel = new CustomerNotLinkedPanel(params) {

			@Override
			protected void onShowErrorMessage(String errorMessage) {
				AonMessagePanel.showError(messagePanel, errorMessage);
			}

			@Override
			protected void onCusotmerOpen(Integer customerId, String customerName) {
				onCustomerSelect(customerId, customerName);
			}
			
			@Override
			protected void onHideMessage() {
				AonMessagePanel.hideMessage(messagePanel);
			}

			@Override
			protected void onShowELoadingMessage(String loadingMessage) {
				AonMessagePanel.showLoading(messagePanel, loadingMessage);
			}

		};

		centerPanel.setWidget(customerNotLinkedPanel);
	}

	private static Byte[] mapStatus(Set<String> selectedOptions) {
		ArrayList<Byte> result = new ArrayList<Byte>();

		if (selectedOptions == null || selectedOptions.isEmpty()) {
			// Si está vacío, devolver todos
			return new Byte[] { 0, 1, 2 };
		}

		if (selectedOptions.contains("Activo")) {
			result.add((byte) 0);
		}
		if (selectedOptions.contains("Inactivo")) {
			result.add((byte) 1);
		}
		if (selectedOptions.contains("Bloqueado")) {
			result.add((byte) 2);
		}

		return result.toArray(new Byte[0]);
	}

	protected abstract void onCustomerSelect(Integer customerId, String customerName);

}
