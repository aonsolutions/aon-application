package com.esferalia.aon.gwt.fiscal.client.customer;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDockLayout;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomListBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomMultiSelectBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessagePanel;
import com.esferalia.aon.occam.api.model.customer.CustomersLinkedParams;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.SimplePanel;

/**
 * Pestania "Vincular": clientes SIG y su vinculacion con dominios.
 *
 * ResizeComposite y no Composite: dentro lleva un DockLayoutPanel, que necesita
 * recibir onResize del TabLayoutPanel para tener altura.
 */
public class SigCustomerLinkTab extends ResizeComposite {

	private final CustomersLinkedParams params;

	private AonCustomDockLayout docklayoutPanel;

	private HTMLPanel container;
	private final HTMLPanel messagePanel = new HTMLPanel("");

	private final AonCustomMultiSelectBox customerStatus = new AonCustomMultiSelectBox("Estado Cliente");
	private final AonCustomListBox sync = new AonCustomListBox("Vinculaci\u00f3n");

	private SimplePanel centerPanel;

	private SigCustomerDomainPanel sigCustomerDomainPanel;

	/** La primera busqueda no se lanza hasta que la pestania se muestra. */
	private boolean loaded = false;

	public SigCustomerLinkTab(CustomersLinkedParams params) {
		this.params = params;
		build();
		initWidget(docklayoutPanel);
	}
	
	private final Timer searchTimer = new Timer() {
	    @Override public void run() { onSearch(); }
	};

	private void build() {
		docklayoutPanel = new AonCustomDockLayout("Clientes vinculados (SIG)") {

			@Override
			protected void onClearFilter() {
				if (null != sigCustomerDomainPanel)
					sigCustomerDomainPanel.resetSearchOffset();

				getSearchTextBox().setValue(null, false);

				Set<String> selectedOptions = new LinkedHashSet<String>();
				selectedOptions.add("Activo");
				customerStatus.setSelectedOptions(selectedOptions);

				sync.setValue("");

				onSearch();
			}
		};

		docklayoutPanel.setSearchPlaceholder("Busqueda por documento/nombre...");

//		docklayoutPanel.addKeyUpHandler(e -> {
//			String value = docklayoutPanel.getSearchTextBox().getValue();
//			if (AonStringUtils.isNotBlank(value) && value.length() > 2) {
//				onSearch();
//			} else if (AonStringUtils.isBlank(value)) {
//				onSearch();
//			}
//		});
		
		docklayoutPanel.addKeyUpHandler(e -> {
		    String value = docklayoutPanel.getSearchTextBox().getValue();
		    if (AonStringUtils.isBlank(value) || value.length() > 2)
		        searchTimer.schedule(400);   // reprograma, no acumula
		});

		Set<String> customerStatusoptions = new LinkedHashSet<String>();
		customerStatusoptions.add("Activo");
		customerStatusoptions.add("Inactivo");
		customerStatusoptions.add("Bloqueado");
		customerStatus.setOptions(customerStatusoptions);

//		customerStatus.addBlurHandler(new BlurHandler() {
//			@Override
//			public void onBlur(BlurEvent event) {
//				if (customerStatus.getSelectedOptions().isEmpty()) {
//					Set<String> selectedOptions = new LinkedHashSet<String>();
//					selectedOptions.add("Activo");
//					customerStatus.setSelectedOptions(selectedOptions);
//				}
//				onSearch();
//			}
//		});

		Set<String> selectedOptions = new LinkedHashSet<String>();
		selectedOptions.add("Activo");
		customerStatus.setSelectedOptions(selectedOptions);

		sync.clearItems();
		sync.addItem("Todos", "");
		sync.addItem("No vinculados", "1");
		sync.addItem("No sincronizados", "2");
		sync.addItem("No vinculados ni sincronizados", "3");
		sync.setValue("");
		//sync.addChangeHandler(e -> onSearch());
		
		// Los filtros no buscan al cambiar: acumulan y se aplican al cerrar el panel.
		// Cada busqueda son 40+ peticiones HTTP, no tiene sentido lanzarla tres veces
		// mientras el usuario ajusta los filtros.
		docklayoutPanel.addOnSearchHandler(e -> onSearch());

		docklayoutPanel.addFilterWidget(customerStatus);
		docklayoutPanel.addFilterWidget(sync);

		container = new HTMLPanel("");
		container.addStyleName(AON.CSS.aonFlexColumn());
		container.add(messagePanel);

		centerPanel = new SimpleLayoutPanel();
		centerPanel.setHeight("100%");
		centerPanel.getElement().getStyle().setProperty("margin-left", "1rem");

		container.add(centerPanel);

		docklayoutPanel.add(container);
	}

	/** Invocado por el modulo al mostrarse la pestania. Solo busca la primera vez. */
	public void ensureLoaded() {
		if (loaded) return;
		loaded = true;
		onSearch();
	}

	/** Aplica una busqueda inicial venida de localStorage. */
	public void setInitialQuery(String query) {
		docklayoutPanel.getSearchTextBox().setValue(query);
	}

	public void onSearch() {
		centerPanel.clear();

		String searchQuery = docklayoutPanel.getSearchTextBox().getValue();

		params.setQuery(searchQuery);
		params.setCustomerStatus(mapStatus(customerStatus.getSelectedOptions()));

		if (AonStringUtils.isNotBlank(sync.getValue())) {
			params.setNoRaddInfo(AonStringUtils.equalsIgnoreCase(sync.getValue(), "1")
					|| AonStringUtils.equalsIgnoreCase(sync.getValue(), "3"));
			params.setNoAonCustomer(AonStringUtils.equalsIgnoreCase(sync.getValue(), "2")
					|| AonStringUtils.equalsIgnoreCase(sync.getValue(), "3"));
		} else {
			// "Todos" no limpiaba los flags de la busqueda anterior
			params.setNoRaddInfo(false);
			params.setNoAonCustomer(false);
		}

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

		if (selectedOptions == null || selectedOptions.isEmpty())
			return new Byte[] { 0, 1, 2 };

		if (selectedOptions.contains("Activo"))    result.add((byte) 0);
		if (selectedOptions.contains("Inactivo"))  result.add((byte) 1);
		if (selectedOptions.contains("Bloqueado")) result.add((byte) 2);

		return result.toArray(new Byte[0]);
	}
}