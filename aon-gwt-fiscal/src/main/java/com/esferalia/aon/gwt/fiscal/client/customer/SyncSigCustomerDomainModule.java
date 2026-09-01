package com.esferalia.aon.gwt.fiscal.client.customer;

import static com.esferalia.aon.gwt.fiscal.client.EntryPointUtils.getCurrentDomain;
import static com.esferalia.aon.gwt.fiscal.client.EntryPointUtils.getCurrentDomainName;
import static com.esferalia.aon.gwt.fiscal.client.EntryPointUtils.getCurrentUser;
import static com.esferalia.aon.gwt.fiscal.client.EntryPointUtils.getRootPanel;

import java.util.logging.Logger;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.RootLayoutPanel;
import com.esferalia.aon.occam.api.model.customer.CustomersLinkedParams;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.logging.client.ConsoleLogHandler;
import com.google.gwt.user.client.ui.TabLayoutPanel;

public class SyncSigCustomerDomainModule implements EntryPoint {

	private static final Logger LOGGER = Logger.getLogger(SyncSigCustomerDomainModule.class.getName());
	static {
		LOGGER.addHandler(new ConsoleLogHandler());
	}

	private static final int TAB_LINK = 0;
	private static final int TAB_INTEGRITY = 1;

	private CustomersLinkedParams params;

	private TabLayoutPanel tabPanel;
	private SigCustomerLinkTab linkTab;

	@Override
	public void onModuleLoad() {
		RootLayoutPanel root = RootLayoutPanel.get(getRootPanel() != null ? getRootPanel() : "rootPanel");

		params = new CustomersLinkedParams();
		params.setDomainName(getCurrentDomainName());
		params.setDomainId(getCurrentDomain());
		params.setUser(getCurrentUser());

		AON.ensureInjected();

		linkTab = new SigCustomerLinkTab(params);

		// Busqueda por defecto guardada por otra pantalla
		String searchQuery = getSearchQuery();
		if (AonStringUtils.isNotBlank(searchQuery)) {
			linkTab.setInitialQuery(searchQuery);
			removeSearchQuery();
		}

		tabPanel = new TabLayoutPanel(2.5, Unit.EM);
		tabPanel.add(linkTab, "Vincular");
		// tabPanel.add(integrityTab, "Integridad Estados");   // paso 4

		tabPanel.addSelectionHandler(event -> onTabSelected(event.getSelectedItem()));

		root.add(tabPanel);

		// selectTab no dispara el evento si ya es la pestania activa
		tabPanel.selectTab(TAB_LINK);
		onTabSelected(TAB_LINK);
	}

	/**
	 * Carga perezosa: cada pestania hace su primera busqueda al mostrarse.
	 * Estas consultas son lentas y no tiene sentido pagarlas por duplicado.
	 */
	private void onTabSelected(int index) {
		if (TAB_LINK == index)
			linkTab.ensureLoaded();
		else if (TAB_INTEGRITY == index) {
			// integrityTab.ensureLoaded();                    // paso 4
		}
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