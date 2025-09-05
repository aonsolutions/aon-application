package com.esferalia.aon.gwt.payroll.client.customer;

import java.util.logging.Logger;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.RootLayoutPanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.gwt.payroll.client.ActivitySummary;
import com.esferalia.aon.gwt.payroll.client.MainEntryPoint;
import com.google.gwt.logging.client.ConsoleLogHandler;
import com.google.gwt.user.client.ui.DeckLayoutPanel;

public class CustomerActivityModule extends MainEntryPoint {

	private static final Logger LOGGER = Logger.getLogger(CustomerActivityModule.class.getName());
	static {
		LOGGER.addHandler(new ConsoleLogHandler());
	}

	private DeckLayoutPanel deckLayoutPanel;
	private CustomersLinkedPanel customersLinkedPanel;
	private ActivitySummary activitySummary;
	
	private RootLayoutPanel root;

	@Override
	public void onModuleLoad() {
		root = RootLayoutPanel.get(getRootPanel() != null ? getRootPanel() : "rootPanel");

//		Window.alert("domainName : " + Wnd.getCurrentDomainNameURL() + "\ndomainId : " + Wnd.getCurrentDomain() + "\nuser : " + Wnd.getCurrentUser());

		moduleLoad();
	}

	public void moduleLoad() {
		AON.ensureInjected();

		deckLayoutPanel = new DeckLayoutPanel();

		customersLinkedPanel = new CustomersLinkedPanel() {

			@Override
			protected void onCustomerSelect(Integer customerId) {
				showSelectedCustomer(customerId);
			}

		};
		
		activitySummary = new ActivitySummary();
		
		AonToolbarButton back = new AonToolbarButton(AON.MSG.backAction(), AON.CSS.aonIconBack());
		back.addClickHandler(e -> showCustomersLinkedPanel());
		
		activitySummary.addToolbarButtonStart(back);

		deckLayoutPanel.add(customersLinkedPanel);
		deckLayoutPanel.add(activitySummary);
		deckLayoutPanel.showWidget(customersLinkedPanel);

		root.add(deckLayoutPanel);
		
		// Remove customer from LS
		// removeCustomer();
	}

	private void showCustomersLinkedPanel() {
		deckLayoutPanel.showWidget(customersLinkedPanel);
		customersLinkedPanel.onSearch();
	}

	private void showSelectedCustomer(Integer customerId) {
		deckLayoutPanel.showWidget(activitySummary);
		activitySummary.showCustomerDomainInfo(customerId);
	}

}

