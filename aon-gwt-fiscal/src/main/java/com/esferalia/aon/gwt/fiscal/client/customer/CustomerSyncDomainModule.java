package com.esferalia.aon.gwt.fiscal.client.customer;

import java.util.logging.Logger;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.RootLayoutPanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDialog;

import com.esferalia.aon.occam.api.model.Customer;
import com.esferalia.aon.occam.api.model.customer.CustomersDomainSyncParams;
import com.esferalia.aon.occam.api.model.customer.CustomersLinkedParams;
import com.google.gwt.logging.client.ConsoleLogHandler;
import com.google.gwt.user.client.ui.DeckLayoutPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;

import static com.esferalia.aon.gwt.fiscal.client.EntryPointUtils.*;
import com.google.gwt.core.client.EntryPoint;

public class CustomerSyncDomainModule  implements EntryPoint {

	private static final Logger LOGGER = Logger.getLogger(CustomerSyncDomainModule.class.getName());
	static {
		LOGGER.addHandler(new ConsoleLogHandler());
	}

	private DeckLayoutPanel deckLayoutPanel;
	private CustomersNotLinkedPanel customersNotLinkedPanel;
	
	private RootLayoutPanel root;
	
	private CustomersLinkedParams params;
	private CustomersDomainSyncParams paramsDomains;
	
	@Override
	public void onModuleLoad() {
		root = RootLayoutPanel.get(getRootPanel() != null ? getRootPanel() : "rootPanel");
		
		params = new CustomersLinkedParams();
		params.setDomainName(getCurrentDomainName());
		params.setDomainId(getCurrentDomain());
		params.setUser(getCurrentUser());
		
		paramsDomains = new CustomersDomainSyncParams();
		paramsDomains.setDomainName(getCurrentDomainName());
		paramsDomains.setDomainId(getCurrentDomain());
		paramsDomains.setUser(getCurrentUser());
		
		moduleLoad();
	}

	public void moduleLoad() {
		AON.ensureInjected();

		deckLayoutPanel = new DeckLayoutPanel();

		customersNotLinkedPanel = new CustomersNotLinkedPanel(params) {

			@Override
			protected void onCustomerSelect(Customer customer) {
				showSelectedCustomer(customer);
			}

		};

		deckLayoutPanel.add(customersNotLinkedPanel);
		deckLayoutPanel.showWidget(customersNotLinkedPanel);

		root.add(deckLayoutPanel);
	}

	private void showSelectedCustomer(Customer customer) {
		SimpleLayoutPanel centerPanel = new SimpleLayoutPanel();
		centerPanel.setHeight("20rem");
		centerPanel.getElement().getStyle().setProperty("margin", "1rem");
		
		AonCustomDialog dialog = new AonCustomDialog();
		dialog.showCloseButton(true);
		dialog.setCaption(customer.getName());
		dialog.setHeight("25rem");
		dialog.setWidth("65rem");

		DomainSyncPanel domainSyncPanel = new DomainSyncPanel(customer, paramsDomains) {
			@Override protected void onEndSuccessSync() { dialog.hide(); }
		};
		
		centerPanel.setWidget(domainSyncPanel);
		
		dialog.add(centerPanel);
		dialog.showLoaded();
	}

}

