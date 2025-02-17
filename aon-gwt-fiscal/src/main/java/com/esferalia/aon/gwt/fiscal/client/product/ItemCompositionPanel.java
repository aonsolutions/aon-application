package com.esferalia.aon.gwt.fiscal.client.product;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.gwt.common.client.CommonServiceAsync;
import com.esferalia.aon.gwt.common.client.CommonServiceAsyncDecorator;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomMultiSelectBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomNumberBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomTextBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessagePanel;
import com.esferalia.aon.gwt.fiscal.client.registry.RegistryModuleOptions;
import com.esferalia.aon.occam.api.model.product.Item;
import com.esferalia.aon.occam.api.model.product.ItemComposition;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

public class ItemCompositionPanel  extends HTMLPanel {
	// Callback
	
	public static interface ItemCompositionCallback {
		void onAccept(ItemComposition itemComposition);
	}

	// CommonService
	
	static CommonServiceAsync commonService;
	
	private static void initializeCommonService() {
		if (commonService == null) {
			CommonServiceAsync commonServiceRaw = GWT.create(CommonService.class);
			commonService = new CommonServiceAsyncDecorator(commonServiceRaw);
		}
	}
	
	// Variables
	
	private final static String EMPTY_STRING = "";
	
	private RegistryModuleOptions options;
	private Item item;
	private List<Item> itemList;
	private List<ItemComposition> itemCompositions;
	private ItemComposition itemComposition;
	
	private ItemCompositionCallback callback;
	
	private HTMLPanel messagePanel = new HTMLPanel(EMPTY_STRING);
	
	private AonCustomMultiSelectBox products = new AonCustomMultiSelectBox("Productos");
	private AonCustomTextBox product = new AonCustomTextBox("Producto");
	private AonCustomNumberBox order = new AonCustomNumberBox("Orden");
	private AonCustomNumberBox quantity = new AonCustomNumberBox("Cantidad");
	
	private Button okButton;
	
	// Constructor
	
	public ItemCompositionPanel(RegistryModuleOptions options, List<Item> itemList, List<ItemComposition> itemCompositions, Item item, ItemCompositionCallback callback) {
		super(EMPTY_STRING);
		
		this.options = options;
		this.item = item;
		this.itemList = itemList;
		this.itemCompositions = itemCompositions;
		this.callback = callback;
		
		this.itemComposition = new ItemComposition().setDomain(options.getDomain()).setItemId(item.getId());
		
		initializeView();
	}
	
	public ItemCompositionPanel(RegistryModuleOptions options, List<Item> itemList, List<ItemComposition> itemCompositions, Item item, ItemComposition itemComposition, ItemCompositionCallback callback) {
		super(EMPTY_STRING);
		
		this.options = options;
		this.item = item;
		this.itemList = itemList;
		this.itemCompositions = itemCompositions;
		this.callback = callback;
		
		this.itemComposition = itemComposition;
		
		initializeView();
	}

	private void initializeView() {
		initializeCommonService();
		show();
	}
	
	public void show() {
		// Message Panel
		setStyleName(AON.CSS.aonFlexColumn());
		getElement().getStyle().setProperty("padding", "1rem 0");
		add(messagePanel);
		
		HTMLPanel container = new HTMLPanel(EMPTY_STRING);
		container.setStyleName(AON.CSS.aonFlexColumn());
		container.getElement().getStyle().setProperty("padding", "0 1rem");
		container.getElement().getStyle().setProperty("min-width", "35rem");
		
		// First Row
		HTMLPanel row = new HTMLPanel(EMPTY_STRING);
		row.setStyleName(AON.CSS.aonItemFlex());
		
		if(itemComposition.getId() == null) {
			
			Set<String> optionsSet = itemList.stream()
					.filter(itemIt -> { 
						return !itemIt.getId().equals(item.getId()) && 
								( itemCompositions.isEmpty() ||
								  itemCompositions.stream()
										.filter(itemComposition -> itemComposition.getComposition().getId() == itemIt.getId().intValue() || itemComposition.getCompositionItemId() == itemIt.getId().intValue())
										.collect(Collectors.toList()).size() == 0);
				})
				.map(item -> item.getProduct().getName())
				.collect(Collectors.toSet());
			
			products.setOptions(optionsSet);
			products.addBlurHandler(e -> {
				okButton.setVisible(true);
			});
			row.add(products);
			
		} else {
			product.setEnable(false);
			product.setValue(itemComposition.getDescription());
			row.add(product);
		}
		
		container.add(row);
		
		// Second Row
		HTMLPanel row2 = new HTMLPanel(EMPTY_STRING);
		row2.setStyleName(AON.CSS.aonItemFlex());
		
		order.hideNearBy();
		quantity.hideNearBy();
		
		order.setValue(itemComposition.getId() == null ?  ((double)itemCompositions.size() + 1) : (double) itemComposition.getSequence());
		quantity.setValue(itemComposition.getId() == null ?  1 : itemComposition.getQuantity());
		
		order.addValueChangeHandler(e -> okButton.setVisible(true));
		quantity.addValueChangeHandler(e -> okButton.setVisible(true));
		
		row2.add(order);
		row2.add(quantity);
		container.add(row2);
		
		// Buttons
		container.add(createButtonsPanel());
		add(container);	
	}
	
	private Widget createButtonsPanel() {
		HTMLPanel buttonsPanel = new HTMLPanel(EMPTY_STRING);
		buttonsPanel.setStyleName(AON.CSS.aonTextCenter());
    	
    	okButton = new Button();
    	okButton.setStyleName(AON.CSS.aonOkButton());
    	okButton.setText(itemComposition.getId() == null ? "Crear Composici\u00f3n" : "Actualizar Composici\u00f3n");
    	okButton.setVisible(false);
    	okButton.addClickHandler(e -> {
    		okButton.setEnabled(false);
    		
    		if(null == itemComposition.getId()) {
    			if(products.getSelectedOptions().isEmpty()) {
    				AonMessagePanel.showWarning(messagePanel, "Debe seleccionar al menos un producto");
    				okButton.setEnabled(true);
    			} else {
    				List<ItemComposition> itemCompositions = new ArrayList<ItemComposition>();
    				for(int i=0; i < products.getSelectedOptions().size(); i++) {
    					String productSelected = products.getSelectedOptions().stream().collect(Collectors.toList()).get(i);
    					Optional<Item> itemOpt = itemList.stream().filter(item -> AonStringUtils.equalsIgnoreCase(item.getProduct().getName(), productSelected)).findFirst();
    					itemCompositions.add(
    							new ItemComposition()
    								.setDomain(item.getDomain().getId())
    								.setItemId(item.getId())
    								.setComposition(itemOpt.get())
    								.setCompositionItemId(itemOpt.get().getId())
    								.setSequence(order.getValue().intValue() + i)
    								.setQuantity(quantity.getValue())
    								.setDescription(itemOpt.get().getProduct().getName())
    					);
    				}
    				
    				commonService.saveItemCompositions(options.getDomainName(), options.getDomain(), options.getUser(), itemCompositions, new AsyncCallback<List<ItemComposition>>() {
        				
        				@Override
        				public void onSuccess(List<ItemComposition> result) {
        					callback.onAccept(null);
        				}
        				
        				@Override
        				public void onFailure(Throwable error) {
        					AonMessagePanel.showError(messagePanel, error.getMessage());
        					okButton.setEnabled(true);
        				}
        				
        			});
    			}
    			
    		} else {
    			itemComposition
    				.setSequence(order.getValue().intValue())
    				.setQuantity(quantity.getValue())
    				;
    			
    			commonService.saveItemComposition(options.getDomainName(), options.getDomain(), options.getUser(), itemComposition, new AsyncCallback<ItemComposition>() {
    				
    				@Override
    				public void onSuccess(ItemComposition result) {
    					callback.onAccept(result);
    				}
    				
    				@Override
    				public void onFailure(Throwable error) {
    					AonMessagePanel.showError(messagePanel, error.getMessage());
    					okButton.setEnabled(true);
    				}
    				
    			});
    		}
			
    	});
    	buttonsPanel.add(okButton);
    	
    	return buttonsPanel;
	}
	
}
