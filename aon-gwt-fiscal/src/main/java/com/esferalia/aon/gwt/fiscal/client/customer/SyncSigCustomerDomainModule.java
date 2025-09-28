package com.esferalia.aon.gwt.fiscal.client.customer;

import static com.esferalia.aon.gwt.fiscal.client.EntryPointUtils.getCurrentDomain;
import static com.esferalia.aon.gwt.fiscal.client.EntryPointUtils.getCurrentDomainName;
import static com.esferalia.aon.gwt.fiscal.client.EntryPointUtils.getCurrentUser;
import static com.esferalia.aon.gwt.fiscal.client.EntryPointUtils.getRootPanel;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.logging.Logger;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.RootLayoutPanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDockLayout;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomMultiSelectBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessagePanel;
import com.esferalia.aon.occam.api.model.customer.CustomersLinkedParams;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.logging.client.ConsoleLogHandler;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.SimplePanel;

public class SyncSigCustomerDomainModule  implements EntryPoint {

	private static final Logger LOGGER = Logger.getLogger(SyncSigCustomerDomainModule.class.getName());
	static {
		LOGGER.addHandler(new ConsoleLogHandler());
	}
	
	private AonCustomDockLayout docklayoutPanel;
	
	private HTMLPanel container;
	private HTMLPanel messagePanel = new HTMLPanel("");

	private AonCustomMultiSelectBox customerStatus = new AonCustomMultiSelectBox("Estado Cliente");

	private SimplePanel centerPanel;

	private SigCustomerDomainPanel sigCustomerDomainPanel;
	
	private CustomersLinkedParams params;

	private RootLayoutPanel root;
	
	@Override
	public void onModuleLoad() {
		root = RootLayoutPanel.get(getRootPanel() != null ? getRootPanel() : "rootPanel");
		
		params = new CustomersLinkedParams();
		params.setDomainName(getCurrentDomainName());
		params.setDomainId(getCurrentDomain());
		params.setUser(getCurrentUser());
		
		moduleLoad();
	}

	public void moduleLoad() {
		AON.ensureInjected();
		
		docklayoutPanel = new AonCustomDockLayout("Clientes vinculados (SIG)") {

			@Override
			protected void onClearFilter() {
				sigCustomerDomainPanel.resetSearchOffset();
				getSearchTextBox().setValue(null, false);

				Set<String> selectedOptions = new LinkedHashSet<String>();
				selectedOptions.add("Activo");
				customerStatus.setSelectedOptions(selectedOptions);

				onSearch();
			}
		
		};
		
		docklayoutPanel.setSearchPlaceholder("Busqueda por documento/nombre...");
		
		docklayoutPanel.addKeyUpHandler(e -> {
			String value = docklayoutPanel.getSearchTextBox().getValue();
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
		
		// Set default search if exists
		if(AonStringUtils.isNotBlank(getSearchQuery())) {
			docklayoutPanel.getSearchTextBox().setValue(getSearchQuery());
			removeSearchQuery();
		}

		docklayoutPanel.addFilterWidget(customerStatus);

		container = new HTMLPanel("");
		container.addStyleName(AON.CSS.aonFlexColumn());

		container.add(messagePanel);

		centerPanel = new SimpleLayoutPanel();
		centerPanel.setHeight("100%");
		centerPanel.getElement().getStyle().setProperty("margin-left", "1rem");

		container.add(centerPanel);

		docklayoutPanel.add(container);
		
		root.add(docklayoutPanel);
		
		onSearch();
	}
	
	public void onSearch() {
		centerPanel.clear();

		String searchQuery = docklayoutPanel.getSearchTextBox().getValue();
		Byte[] customerStatusSearch = mapStatus(customerStatus.getSelectedOptions());
		
		this.params.setQuery(searchQuery);
		this.params.setCustomerStatus(customerStatusSearch);

		sigCustomerDomainPanel = new SigCustomerDomainPanel(params) {

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
				sigCustomerDomainPanel.resetSearchOffset();
				onSearch();
			}

		};

		centerPanel.setWidget(sigCustomerDomainPanel);
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
	
	public static native String getSearchQuery()
	/*-{
		var value = $wnd.localStorage.getItem("searchQuery");
		return value;
	}-*/;
	
	public static native void removeSearchQuery()
	/*-{
		$wnd.localStorage.removeItem("searchQuery");
	}-*/;

}

