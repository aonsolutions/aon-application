package com.esferalia.aon.gwt.fiscal.client.tariff;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.gwt.common.client.CommonServiceAsync;
import com.esferalia.aon.gwt.common.client.CommonServiceAsyncDecorator;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomTable;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessagePanel;
import com.esferalia.aon.gwt.fiscal.client.registry.RegistryModuleOptions;
import com.esferalia.aon.occam.api.model.product.ItemTariff;
import com.esferalia.aon.occam.api.model.product.Product;
import com.esferalia.aon.occam.api.model.product.ProductParams;
import com.esferalia.aon.occam.api.model.tariff.Tariff;
import com.esferalia.aon.occam.api.model.type.DomainType;
import com.esferalia.aon.occam.api.model.type.ProductType;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.SimplePanel;

public class TariffCatalogueList extends HTMLPanel {
	
	private class TariffCatalogueColumn implements Serializable {

		private static final long serialVersionUID = 1L;
		
		private String description;
		private String width;
		private String style;
		
		public TariffCatalogueColumn(String description, String width, String style) {
			super();
			this.description = description;
			this.width = width;
			this.style = style;
		}
		public String getDescription() {
			return description;
		}
		public String getWidth() {
			return width;
		}
		public String getStyle() {
			return style;
		}
		
	}
	
	private static CommonServiceAsync COMMON_SERVICE;
	
	// TariffList UI

	private HTMLPanel messagePanel = new HTMLPanel("");
	
	private SimplePanel tableContainer;
	private ScrollPanel tableScrollPanel;
	private AonCustomTable tab;
	
	private List<TariffCatalogueColumn> initializeTariffCatalogueColumns = new ArrayList<>();
	
	private RegistryModuleOptions options;
	private List<Tariff> tariffs;
	private DomainType domainType;
	
	// Constructor
	public TariffCatalogueList(RegistryModuleOptions options, List<Tariff> tariffs) {
		super("");
		
		CommonServiceAsync commonServiceRaw = GWT.create(CommonService.class);
		COMMON_SERVICE = new CommonServiceAsyncDecorator(commonServiceRaw);
		
		this.options = options;
		this.tariffs = tariffs;
		
		addStyleName(AON.CSS.aonFlexColumn());
		
		add(messagePanel);
	
		tableContainer = new SimpleLayoutPanel();
		tableContainer.setHeight("100%");
		tableContainer.getElement().getStyle().setProperty("margin-left", "1rem");
		
		add(tableContainer);	
	}
	
	public void onSearch(DomainType domainType) {
		this.domainType = domainType;
		
		tableContainer.clear();
		tab = new AonCustomTable();
		tableScrollPanel = new ScrollPanel(tab);
		tableScrollPanel.getElement().getStyle().setProperty("margin", "0 1rem");
	
		paintHeader();
		tableContainer.add(tableScrollPanel);
		searchDataList();
	}
	
	private void paintHeader() {
		initializeTariffCatalogueColumns.clear();
		initializeTariffCatalogueColumns.add(new TariffCatalogueColumn(AON.MSG.code(), "15rem" , "white-space: nowrap; overflow: hidden; text-overflow: ellipsis;"));
		initializeTariffCatalogueColumns.add(new TariffCatalogueColumn(AON.MSG.description(), "-moz-available" , "min-width: 5rem; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;"));
		initializeTariffCatalogueColumns.add(new TariffCatalogueColumn("Tipo" , "5rem" , ""));
		initializeTariffCatalogueColumns.add(new TariffCatalogueColumn("Precio" , "5rem" , ""));
		tariffs.forEach(tariff -> initializeTariffCatalogueColumns.add(new TariffCatalogueColumn(tariff.getCode(), "5rem" , "")));
		
		tab.createHeader();
		for ( TariffCatalogueColumn col : initializeTariffCatalogueColumns) 
			tab.addHeader(new Label(col.getDescription()), col.getWidth(), col.getStyle());
	}
	
	private void searchDataList() {
		getList(products -> {
			
			if (products.isEmpty()) {
				FlowPanel line = new FlowPanel();
				InlineLabel label = new InlineLabel(AON.MSG.noData());
				line.add(label);
				tableContainer.clear();
				tableContainer.add(line);
			} else 
				paintRow(products, 0);
			
		});
	}
	
	private void paintRow(List<Product> products, int index) {
		Product product = products.get(index);
		
		getItemTariff(product.getItem().getId(), itemTariffs -> {
			HTMLPanel row = tab.createRow();
			
			for(TariffCatalogueColumn col : initializeTariffCatalogueColumns) {
				if(AonStringUtils.equalsIgnoreCase(col.getDescription(), AON.MSG.code())) {
					Label code = new Label(product.getCode());
					code.setTitle(product.getCode());
					tab.addInlineStyle(code, col.getStyle());
					tab.addRow(row, code, col.getWidth());
				} else if(AonStringUtils.equalsIgnoreCase(col.getDescription(), AON.MSG.description())) {
					Label description = new Label(product.getName());
					description.setTitle(product.getName());
					tab.addInlineStyle(description, col.getStyle());
					tab.addRow(row, description, col.getWidth());
				} else if(AonStringUtils.equalsIgnoreCase(col.getDescription(), "Precio")) {
					tab.addRow(row, new Label(formaDouble(product.getItem().getPrice()) + " \u20ac"), col.getWidth());
				} else if(AonStringUtils.equalsIgnoreCase(col.getDescription(), "Tipo")){
					tab.addRow(row, new Label(null == product.getComposition() ? "" : (product.getComposition() ? "Pack" : "Servicio")), col.getWidth());
				}else {
					Optional<ItemTariff> itemTariffOpt = itemTariffs.stream().filter(itemTariff -> itemTariff.getItem().equals(product.getItem().getId()) && AonStringUtils.equalsIgnoreCase(itemTariff.getTariff().getCode(), col.getDescription())).findFirst();
					Tariff tariffObj = tariffs.stream().filter(tariffIt -> AonStringUtils.equalsIgnoreCase(tariffIt.getCode(), col.getDescription())).findFirst().get();
					tab.addRow(row, new Label(formaDouble(getNeto(itemTariffOpt.isEmpty() ? tariffObj.getDiscount() : itemTariffOpt.get().getProfitPercent(), product.getItem().getPrice())) + " \u20ac"), col.getWidth());
				}
			}
			
			paintRow(products, index + 1);
		});
	}
	
	private double getNeto(double percent, double price) {
		return price - (price * percent / 100);
	}

	private static String formaDouble(double value) {
        // Round to two decimal places
        long scaledValue = Math.round(value * 100); // Scale to avoid floating-point precision issues
        long integerPart = scaledValue / 100;      // Extract integer part
        long decimalPart = scaledValue % 100;      // Extract decimal part

        // Format the result
        return integerPart + "." + (decimalPart < 10 ? "0" : "") + decimalPart /*+ " \u20ac"*/;
    }
	
	private void getList(Consumer<List<Product>> success) {
		ProductParams params = new ProductParams()
				.setDomainName(options.getDomainName())
				.setDomain(options.getDomain())
				.setUser(options.getUser())
				.setType(ProductType.AUXILIARY)
				.setDomainType(domainType)
				.setOffset(0)
				.setLimit(Integer.MAX_VALUE)
				;
		
		COMMON_SERVICE.getProducts(params, new AsyncCallback<List<Product>>() {
			
			@Override
			public void onSuccess(List<Product> products) {
				products.sort(Comparator.comparing(Product::getComposition).reversed());
				success.accept(products);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				AonMessagePanel.showError(messagePanel, "Error productos: " + caught.getMessage());
			}
		});
	}
	

	
	private void getItemTariff(Integer itemId, Consumer<List<ItemTariff>> success) {
		COMMON_SERVICE.getItemTariffs(options.getDomainName(), options.getDomain(), options.getUser(), itemId, new AsyncCallback<List<ItemTariff>>() {
			
			@Override
			public void onSuccess(List<ItemTariff> itemTariffsDb) {
				success.accept(itemTariffsDb);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				AonMessagePanel.showError(messagePanel, "Error obteniendo tarifas producto: " + caught.getMessage());
			}
		});
	}
	
}
