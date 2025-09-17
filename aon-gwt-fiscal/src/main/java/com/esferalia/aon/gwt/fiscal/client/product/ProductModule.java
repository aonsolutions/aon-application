package com.esferalia.aon.gwt.fiscal.client.product;

import java.util.logging.Logger;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.RootLayoutPanel;

import com.esferalia.aon.gwt.fiscal.client.registry.RegistryModuleOptions;
import com.esferalia.aon.gwt.fiscal.client.tariff.TariffCatalogue;
import com.esferalia.aon.occam.api.model.product.Product;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.dom.client.BodyElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.logging.client.ConsoleLogHandler;
import com.google.gwt.user.client.ui.DeckLayoutPanel;

import static com.esferalia.aon.gwt.fiscal.client.EntryPointUtils.*;
import com.google.gwt.core.client.EntryPoint;

public class ProductModule  implements EntryPoint {
	
	private static final Logger LOGGER = Logger.getLogger(ProductModule.class.getName());
	static { LOGGER.addHandler( new ConsoleLogHandler() ); }
	
	private RegistryModuleOptions options;

	private DeckLayoutPanel deckLayoutPanel;
	private ProductList productList;
	private ProductEntry productEntry;
	private TariffCatalogue tariffCatalogue;
	
	@Override
	public void onModuleLoad() {
		RootLayoutPanel root = RootLayoutPanel.get(getRootPanel() != null ? getRootPanel() : "rootPanel");
		
		options = new RegistryModuleOptions();
		options.setParentWidget(root);
		options.setDomainName(getCurrentDomainName());
		options.setDomain(getCurrentDomain());
		options.setUser(getCurrentUser());
		
		moduleLoad();
	}
	
	public void moduleLoad() {
		AON.ensureInjected();
		ensureGwtSelector();
		
		deckLayoutPanel = new DeckLayoutPanel();
		
		productEntry = new ProductEntry(options) {

			@Override
			protected void onBackClick() {
				showProductList();
			}
			
		};
		
		productList = new ProductList(options) {

			@Override
			protected void onProductSelect(Product product) {
				showSelectedProduct(product);
			}
			
			@Override
			protected void onCatalogueShow() {
				showTariffCatalogue();
			}
		
		};
		
		tariffCatalogue = new TariffCatalogue(options) {

			@Override
			protected void onBackClick() {
				showProductList();
			}
			
		};
			
			
		deckLayoutPanel.add(productList);
		deckLayoutPanel.add(productEntry);
		deckLayoutPanel.add(tariffCatalogue);
		deckLayoutPanel.showWidget(productList);
		
		options.getParentWidget().add(deckLayoutPanel);
	}
	
	private void showProductList() {
		deckLayoutPanel.showWidget(productList);
		productList.onSearch();
	}
	
	private void showSelectedProduct(Product product) {
		deckLayoutPanel.showWidget(productEntry);
		productEntry.setProduct(product.getId());
	}
	
	private void showTariffCatalogue() {
		deckLayoutPanel.showWidget(tariffCatalogue);
		tariffCatalogue.onSearch();
	}
	
	private void ensureGwtSelector() {
		BodyElement body = Document.get().getBody();
		String className = body.getClassName();
		if (AonStringUtils.isBlank(className)
				|| (className.indexOf("gwt-Selector") == -1))
			body.addClassName("gwt-Selector");

	}
	
}
