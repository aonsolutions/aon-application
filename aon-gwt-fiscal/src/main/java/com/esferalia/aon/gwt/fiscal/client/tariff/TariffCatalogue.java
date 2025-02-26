package com.esferalia.aon.gwt.fiscal.client.tariff;

import java.util.List;
import java.util.function.Consumer;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.gwt.common.client.CommonServiceAsync;
import com.esferalia.aon.gwt.common.client.CommonServiceAsyncDecorator;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDockLayout;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomListBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessagePanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.gwt.fiscal.client.product.ProductCatalogue;
import com.esferalia.aon.gwt.fiscal.client.registry.RegistryModuleOptions;
import com.esferalia.aon.occam.api.model.tariff.Tariff;
import com.esferalia.aon.occam.api.model.tariff.TariffParams;
import com.esferalia.aon.occam.api.model.type.DomainType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;

public abstract class TariffCatalogue extends AonCustomDockLayout {
	// ------------------------------------------------- CommonServiceAsync
	
		static CommonServiceAsync commonService;
		
		private static void initializeCommonService() {
			if (commonService == null) {
				CommonServiceAsync commonServiceRaw = GWT.create(CommonService.class);
				commonService = new CommonServiceAsyncDecorator(commonServiceRaw);
			}
		}
		
		// ------------------------------------------------- Variables
		
		private final String EMPTY_STRING = "";

		private AonCustomListBox domainType = new AonCustomListBox("Tipo Dominio");
		private AonCustomListBox tariff = new AonCustomListBox("Tarifa");
		
		private HTMLPanel container;
		
		private HTMLPanel messagePanel = new HTMLPanel(EMPTY_STRING);
		
		private TabLayoutPanel tablayoutPanel;
		
		private TariffCatalogueList tariffCatalogueList;
		private ProductCatalogue productCatalogue;

		private RegistryModuleOptions options;
		private List<Tariff> tariffs;
		
		public TariffCatalogue(RegistryModuleOptions options) {
			super("Cat\u00e1logo");
			
			this.options = options;
			initializeCommonService();
			
			getTariffs(tariffsDb -> {
				this.tariffs = tariffsDb;
				
				hideSearchWidget();
				createToolbar();
				
				tablayoutPanel = new TabLayoutPanel(25.00, Unit.PX);
				tablayoutPanel.setHeight((Window.getClientHeight() - 130) + "px");
				tablayoutPanel.getElement().getStyle().setProperty("margin", "0 1rem");
			
				container = new HTMLPanel(EMPTY_STRING);
				container.addStyleName(AON.CSS.aonFlexColumn());
				container.addStyleName(AON.CSS.aonSelector());
				container.add(messagePanel);
				
				HTMLPanel rootPanel = new HTMLPanel(EMPTY_STRING);
				rootPanel.addStyleName(AON.CSS.aonFlexColumn());
				
				tariffCatalogueList = new TariffCatalogueList(options, this.tariffs);
				tablayoutPanel.add(tariffCatalogueList, "Tarifas");
				tablayoutPanel.selectTab(0);
				
				productCatalogue = new ProductCatalogue(options);
				tablayoutPanel.add(productCatalogue, "Cat\u00e1logo");
				
				tablayoutPanel.addSelectionHandler(e -> {
					tariff.setVisible(tablayoutPanel.getSelectedIndex() != 0);
					onSearch();
				});
				
				container.add(tablayoutPanel);
				
				add(container);
			});
		}
		
		@Override
		protected void onClearFilter() {}
		
		private void createToolbar() {
			AonToolbarButton backBtn = new AonToolbarButton("Volver", AON.CSS.aonIconBack());
			backBtn.addClickHandler(e -> onBackClick());
			addToolbarButton(backBtn);
			
			domainType.clearItems();
			DomainType.getValues().forEach(domainTypeIt -> domainType.addItem(domainTypeIt.getName(), domainTypeIt.ordinal() + ""));
			domainType.addChangeHandler(e -> onSearch());
			addToolbarButton(domainType);
			
			tariff.clearItems();
			tariffs.forEach(tariffIt -> tariff.addItem(tariffIt.getName(), tariffIt.getId().toString()));
			tariff.addChangeHandler(e -> onSearch());
			tariff.setVisible(false);
			addToolbarButton(tariff);
		}
		
		public void onSearch() {
			if(tablayoutPanel.getSelectedIndex() == 0) {
				tariffCatalogueList.onSearch(DomainType.safeValueOf(Byte.parseByte(domainType.getValue())));
			} else if(tablayoutPanel.getSelectedIndex() == 1) {
				productCatalogue.onSearch(DomainType.safeValueOf(Byte.parseByte(domainType.getValue())), tariffs.stream().filter(tariffIt -> tariffIt.getId().equals(Integer.parseInt(tariff.getValue()))).findFirst().get());
			} 
		}
		
		private void getTariffs(Consumer<List<Tariff>> success) {
			TariffParams params = new TariffParams()
					.setDomainName(options.getDomainName())
					.setDomain(options.getDomain())
					.setUser(options.getUser())
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
		}


		protected abstract void onBackClick();

}
