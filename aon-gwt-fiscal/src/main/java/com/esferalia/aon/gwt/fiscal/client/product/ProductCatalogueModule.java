package com.esferalia.aon.gwt.fiscal.client.product;

import static com.esferalia.aon.gwt.fiscal.client.EntryPointUtils.getCurrentDomain;
import static com.esferalia.aon.gwt.fiscal.client.EntryPointUtils.getCurrentDomainName;
import static com.esferalia.aon.gwt.fiscal.client.EntryPointUtils.getCurrentUser;
import static com.esferalia.aon.gwt.fiscal.client.EntryPointUtils.getRootPanel;

import java.util.List;
import java.util.function.Consumer;
import java.util.logging.Logger;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.gwt.common.client.CommonServiceAsync;
import com.esferalia.aon.gwt.common.client.CommonServiceAsyncDecorator;
import com.esferalia.aon.gwt.common.client.RootLayoutPanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDockLayout;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomListBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessagePanel;
import com.esferalia.aon.gwt.fiscal.client.registry.RegistryModuleOptions;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.tariff.Tariff;
import com.esferalia.aon.occam.api.model.tariff.TariffParams;
import com.esferalia.aon.occam.api.model.type.DomainType;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.BodyElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.logging.client.ConsoleLogHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTMLPanel;

public class ProductCatalogueModule  implements EntryPoint {
	
	private static final Logger LOGGER = Logger.getLogger(ProductCatalogueModule.class.getName());
	static { LOGGER.addHandler( new ConsoleLogHandler() ); }
	
	// ------------------------------------------------- CommonServiceAsync

	static CommonServiceAsync commonService;
	
	private static void initializeCommonService() {
		if (commonService == null) {
			CommonServiceAsync commonServiceRaw = GWT.create(CommonService.class);
			commonService = new CommonServiceAsyncDecorator(commonServiceRaw);
		}
	}
	
	private RegistryModuleOptions currentDomainOptions;
	private RegistryModuleOptions officeDomainOptions;
	
	private AonCustomDockLayout aonCustomDockLayout;

	private final String EMPTY_STRING = "";

	private AonCustomListBox domainType = new AonCustomListBox("Tipo Dominio");
	private AonCustomListBox tariff = new AonCustomListBox("Tarifa");
	
	private HTMLPanel container;
	
	private HTMLPanel messagePanel = new HTMLPanel(EMPTY_STRING);
	
	private ProductCatalogueBooking productCatalogue;
	
	private List<Tariff> tariffs;
	
	@Override
	public void onModuleLoad() {
		RootLayoutPanel root = RootLayoutPanel.get(getRootPanel() != null ? getRootPanel() : "rootPanel");
		
		currentDomainOptions = new RegistryModuleOptions();
		currentDomainOptions.setParentWidget(root);
		currentDomainOptions.setDomainName(getCurrentDomainName());
		currentDomainOptions.setDomain(getCurrentDomain());
		currentDomainOptions.setUser(getCurrentUser());
		
		officeDomainOptions = new RegistryModuleOptions();
		
		moduleLoad();
	}
	
	public void moduleLoad() {
		AON.ensureInjected();
		ensureGwtSelector();
		
		aonCustomDockLayout = new AonCustomDockLayout("Cat\u00e1logo") {
			
			@Override
			protected void onClearFilter() {
				// TODO Auto-generated method stub
				
			}
		};
		
		initializeCommonService();
		
		getTariffs(tariffsDb -> {
			this.tariffs = tariffsDb;
			
			aonCustomDockLayout.hideSearchWidget();
			createToolbar();
			
			container = new HTMLPanel(EMPTY_STRING);
			container.addStyleName(AON.CSS.aonFlexColumn());
			container.addStyleName(AON.CSS.aonSelector());
			container.getElement().getStyle().setProperty("padding", "0 1rem");
			container.add(messagePanel);
			
			container.add(productCatalogue);
			
			aonCustomDockLayout.add(container);
			
			onSearch();
		});
		
		currentDomainOptions.getParentWidget().add(aonCustomDockLayout);
	}
	
	private void createToolbar() {
		domainType.clearItems();
		DomainType.getValues().forEach(domainTypeIt -> domainType.addItem(domainTypeIt.getName(), domainTypeIt.ordinal() + ""));
		domainType.addChangeHandler(e -> onSearch());
		domainType.setVisible(false);
		aonCustomDockLayout.addToolbarButton(domainType);
		
		tariff.clearItems();
		tariffs.forEach(tariffIt -> tariff.addItem(tariffIt.getName(), tariffIt.getId().toString()));
		tariff.addChangeHandler(e -> onSearch());
		tariff.setVisible(false);
		aonCustomDockLayout.addToolbarButton(tariff);
	}
	
	public void onSearch() {
		productCatalogue.onSearch(
				DomainType.safeValueOf(Byte.parseByte(domainType.getValue())), 
				tariffs.stream().filter(tariffIt -> tariffIt.getId().equals(Integer.parseInt(tariff.getValue()))).findFirst().get());
	}
	
	private void getTariffs(Consumer<List<Tariff>> success) {
		commonService.getAonConfiguration(currentDomainOptions.getDomainName(), currentDomainOptions.getDomain(), currentDomainOptions.getUser(), new AsyncCallback<AonConfiguration>() {
			
			@Override
			public void onSuccess(AonConfiguration config) {
				productCatalogue = new ProductCatalogueBooking(currentDomainOptions, officeDomainOptions, config.getCompany().getId(), config.getWorkplaces());
				productCatalogue.setHeight("100%");
				
				commonService.getOfficeSibling(currentDomainOptions.getDomainName(), currentDomainOptions.getDomain(), currentDomainOptions.getUser(), new AsyncCallback<Domain>() {

					@Override
					public void onFailure(Throwable error) {
						AonMessagePanel.showError(messagePanel, "Error obteniendo despacho: " + error.getMessage());
					}

					@Override
					public void onSuccess(Domain officeDomain) {
						officeDomainOptions.setDomainName(officeDomain.getName());
						officeDomainOptions.setDomain(officeDomain.getId());
						
						TariffParams params = new TariffParams()
								.setDomainName(officeDomainOptions.getDomainName())
								.setDomain(officeDomainOptions.getDomain())
								.setUser(officeDomainOptions.getUser())
								.setOffset(0)
								.setLimit(Integer.MAX_VALUE)
								;
						
						commonService.getTariffs(params, new AsyncCallback<List<Tariff>>() {
							
							@Override
							public void onSuccess(List<Tariff> products) {
								success.accept(products);
							}
							
							@Override
							public void onFailure(Throwable caught) {
								AonMessagePanel.showError(messagePanel, "Error tarifas: " + caught.getMessage());
							}
						});
						
					}});
			}
			
			@Override
			public void onFailure(Throwable caught) {
				AonMessagePanel.showError(messagePanel, "Error config: " + caught.getMessage());
			}
		});
	}
	
	private void ensureGwtSelector() {
		BodyElement body = Document.get().getBody();
		String className = body.getClassName();
		if (AonStringUtils.isBlank(className)
				|| (className.indexOf("gwt-Selector") == -1))
			body.addClassName("gwt-Selector");

	}
	
}
