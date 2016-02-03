package com.esferalia.aon.gwt.template.client.marketplace;


import java.util.ArrayList;
import java.util.List;

import com.esferalia.aon.gwt.common.client.widget.CustomDialogB;
import com.esferalia.aon.gwt.template.client.ITemplate;
import com.esferalia.aon.gwt.template.client.ITemplateAsync;
import com.esferalia.aon.gwt.template.client.JsTemplates;
import com.esferalia.aon.gwt.template.client.ProgressBarDialog;
import com.esferalia.aon.gwt.template.shared.EcommerceProduct;
import com.esferalia.aon.gwt.template.shared.EcommerceProduct.ProductData.Ecommerce;
import com.esferalia.aon.gwt.template.shared.Product;
import com.esferalia.aon.gwt.template.shared.Seller;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasAllMouseHandlers;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.cellview.client.DataGrid.Style;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DecoratedPopupPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;

public class ProductValuesDialog extends CustomDialogB {
	
	
	final IMarketplaceAsync marketImpl = GWT.create(IMarketplace.class);
	final ITemplateAsync templateImpl = GWT.create(ITemplate.class);
	
	interface ProductValuesDialogBinder extends UiBinder<Widget, ProductValuesDialog> {}
	private static final ProductValuesDialogBinder binder = GWT.create(ProductValuesDialogBinder.class);

	DataGridResources resources = GWT.create(DataGridResources.class);

	public interface DataGridResources extends DataGrid.Resources {
		@Source("com/esferalia/aon/gwt/template/client/datagrid.css")
		Style dataGridStyle();
	}
	
	@UiField Button acceptButton;
	@UiField Button cancelButton;
	@UiField(provided = true) FlexTable templatesGrid;
	@UiField(provided = true) FlexTable valuesGrid;
	
	private ListBox productTemplateList;
	private ListBox sellerList;
	private List<Widget> valuesWidgetList;
	
	private ProgressBarDialog pbd;
	private Attach ecommerceProductAttach;
	private EcommerceProduct ecommerceProduct;
	private EcommerceProduct ecommerceTemplate;
	private Product product;
	private String login;
	
	public ProductValuesDialog(Product product, String login){
		this.product = product;
		this.login = login;
		
		templatesGrid = new FlexTable();
		productTemplateList = new ListBox();
		sellerList = new ListBox();
		valuesGrid = new FlexTable();

		loadTemplatesGrid();
		
		this.setWidget(binder.createAndBindUi(this));
		
		this.setCaption("("+product.getCode() +") " + product.getName());
		this.setWidth("900px");
		this.center();
	}
	
	
	private void loadTemplatesGrid() {
		templatesGrid.setStyleName("aon-panelGrid");
		templatesGrid.setWidth("100%");
		templatesGrid.setBorderWidth(1);
		templatesGrid.setCellSpacing(0);

		templatesGrid.setHTML(0, 0, "Seleccion de plantilla");
		templatesGrid.getFlexCellFormatter().setColSpan(0, 0, 2);
		templatesGrid.getFlexCellFormatter().setHorizontalAlignment(0, 0, HasHorizontalAlignment.ALIGN_CENTER);
		
		templatesGrid.setHTML(1, 0, "Canal de venta");
		templatesGrid.setWidget(1, 1, sellerList);

		loadSellerList();
		
		templatesGrid.setHTML(2, 0, "Nombre");
		templatesGrid.setWidget(2, 1, productTemplateList);
		
		applyGridStyle(templatesGrid);
		templatesGrid.getCellFormatter().setStyleName(0, 0, "aon-panelGrid-even");
		templatesGrid.getCellFormatter().addStyleName(0, 0, "aon-bold");
	}

	private void loadSellerList() {
		sellerList.addItem("-", null, null);
		templateImpl.getSellerList(getDomain(), new AsyncCallback<List<Seller>>() {
			@Override
			public void onSuccess(List<Seller> result) {				
				for(Seller seller: result){
					sellerList.addItem(seller.getRegistryName(), seller.getId().toString());
				}
			}
			@Override
			public void onFailure(Throwable caught){
				Window.alert("Error loading seller list");
			}
		});
		sellerList.addChangeHandler(new ChangeHandler(){
			@Override
			public void onChange(ChangeEvent arg0){
				loadTemplateList();
			}
		});
	}
	
	private void loadTemplateList() {
		productTemplateList.clear();
		productTemplateList.addItem("-", null, null);
		marketImpl.obtainEcommerceProductTemplates(getDomain(), sellerList.getSelectedValue(), new AsyncCallback<List<Attach>>(){
			@Override
			public void onSuccess(List<Attach> result){
				for(Attach attach: result){
					productTemplateList.addItem(attach.getDescription(), attach.getDescription());
				}
			}
			@Override
			public void onFailure(Throwable caught){
				Window.alert("Error loading template list");
			}
		});
		productTemplateList.addChangeHandler(new ChangeHandler(){
			@Override
			public void onChange(ChangeEvent arg0){
				loadValuesGrid();
			}
		});
	}
	
	private void loadValuesGrid(){
		valuesGrid.clear();
		valuesGrid.removeAllRows();
		valuesGrid.setStyleName("aon-panelGrid");
		valuesGrid.setWidth("100%");
		valuesGrid.setBorderWidth(1);
		valuesGrid.setCellSpacing(0);

		ecommerceProduct = null;
		ecommerceTemplate = null;
		ecommerceProductAttach = null;
		valuesWidgetList = null;
		
		if(productTemplateList!=null && productTemplateList.getItemCount()>0 && productTemplateList.getSelectedValue()!=null){
			loadEcommerceTemplateValues();
		}
	}
	
	private void loadEcommerceTemplateValues(){
		marketImpl.obtainEcommerceProductValues(getDomain(), product, productTemplateList.getSelectedItemText(), new AsyncCallback<EcommerceProduct>(){
			@Override
			public void onSuccess(EcommerceProduct result){
				ecommerceTemplate = result;
				loadEcommerceProductAttach();
			}
			@Override
			public void onFailure(Throwable caught){
				Window.alert("Error loading template values");
			}
		});
	}

	private void loadEcommerceProductAttach(){
		marketImpl.obtainEcommerceProductAttach(getDomain(), product, productTemplateList.getSelectedItemText(), new AsyncCallback<Attach>(){
			@Override
			public void onSuccess(Attach result){
				ecommerceProductAttach = result;
				if(ecommerceProductAttach != null){
					loadEcommerceProductValues();
				} else {
					ecommerceProduct = ecommerceTemplate;
					loadValuesGridContent();
				}
			}
			@Override
			public void onFailure(Throwable caught){
				Window.alert("Error loading product template");
			}
		});
	}
	
	private void loadEcommerceProductValues(){
		marketImpl.obtainEcommerceProductValues(getDomain(), ecommerceProductAttach, product, new AsyncCallback<EcommerceProduct>(){
			@Override
			public void onSuccess(EcommerceProduct result){
				ecommerceProduct = result;
				loadValuesGridContent();
			}
			@Override
			public void onFailure(Throwable caught){
				Window.alert("Error loading product values");
			}
		});
	}

	private void loadValuesGridContent(){
		if(ecommerceProduct!=null){
			HorizontalPanel panel = new HorizontalPanel();
			panel.add(new HTML("Valores de producto"));
			panel.addStyleName("aon-bold");
			if(ecommerceProductAttach==null){
				HTML newIcon = new HTML("");
				newIcon.setSize("20px","20px");
				newIcon.setStyleName("aon-margin-left aon-icon-new-text");
				panel.add(newIcon);
			}
			valuesGrid.setWidget(0, 0, panel);
			valuesGrid.getFlexCellFormatter().setColSpan(0, 0, 3);
			valuesGrid.getFlexCellFormatter().setHorizontalAlignment(0, 0, HasHorizontalAlignment.ALIGN_CENTER);
			valuesGrid.getCellFormatter().setStyleName(0, 0, "aon-panelGrid-even");
			
			valuesWidgetList = new ArrayList<>();
			boolean isNew;
			boolean isDeprecated;
			for(Ecommerce ecommerce: ecommerceProduct.getProductData().getEcommerce()){
				isDeprecated=false;
				if(ecommerceProductAttach!=null && ecommerceProductAttach.getId()!=null){
					isDeprecated=isDeprecated(ecommerce);
				}
				if(!isDeprecated){
					valuesWidgetList.add(obtainEditValueWidget(ecommerce));
				}
			}
			
			// editable and new values
			for(Ecommerce ecommerce: ecommerceTemplate.getProductData().getEcommerce()){
				isNew=false;
				if(ecommerceProductAttach!=null && ecommerceProductAttach.getId()!=null){
					isNew=isNew(ecommerce);
				}
				int currentRow = valuesGrid.getRowCount();
				if(isNew){
					valuesWidgetList.add(currentRow-1, obtainEditValueWidget(ecommerce));
					ecommerceProduct.getProductData().getEcommerce().add(currentRow-1, ecommerce);
				}
				
				addGridRow(currentRow, ecommerce, valuesWidgetList.get(currentRow-1), isNew, false );
			}
			
			// deprecated values
			for(Ecommerce ecommerce: ecommerceProduct.getProductData().getEcommerce()){
				isDeprecated=false;
				if(ecommerceProductAttach!=null && ecommerceProductAttach.getId()!=null){
					isDeprecated=isDeprecated(ecommerce);
				}
				if(isDeprecated){
					addGridRow(valuesGrid.getRowCount(), ecommerce, new HTML(ecommerce.getValue()), false, isDeprecated );
				}
			}
		}
	}
	
	private void addGridRow(int currentRow, Ecommerce ecommerce, Widget editValueWidget, boolean isNew, boolean isDeprecated){
		Label labelName = new Label(ecommerce.getName());
		labelName.setStyleName(isNew?"aon-italic":(isDeprecated?"aon-line-through":"aon-bold"));
		HTML icon = new HTML("");
		icon.setWidth("20px");
		icon.setHeight("20px");
		icon.setStyleName("aon-margin-left " + (isNew?"aon-icon-new-text":(isDeprecated?"aon-icon-removed":"")));
		
		if(isNew || isDeprecated){
			addTitlePopup(labelName, isNew, isDeprecated);
			addTitlePopup(icon, isNew, isDeprecated);
		}
		
		valuesGrid.getFlexCellFormatter().setWidth(currentRow, 0, "40%");
		valuesGrid.getFlexCellFormatter().setWidth(currentRow, 1, "60%");
		
		HorizontalPanel labelPanel = new HorizontalPanel();
		labelPanel.add(labelName);
		labelPanel.add(icon);
		valuesGrid.setWidget(currentRow, 0, labelPanel);
		valuesGrid.setWidget(currentRow, 1, editValueWidget);
		
		valuesGrid.getCellFormatter().setStyleName(currentRow, 0, "aon-panelGrid-odd");
		valuesGrid.getCellFormatter().addStyleName(currentRow, 0, "aon-wrap");
		valuesGrid.getCellFormatter().setStyleName(currentRow, 1, "aon-panelGrid-even");
	}
	
	private void addTitlePopup(HasAllMouseHandlers mouseHandler, boolean isNew, boolean isDeprecated){
		final DecoratedPopupPanel simplePopup = new DecoratedPopupPanel(true);
		simplePopup.setWidget(new HTML((isNew?"Nuevo":(isDeprecated?"Obsoleto":""))));
		mouseHandler.addMouseOverHandler(new MouseOverHandler() {
			@Override
			public void onMouseOver(MouseOverEvent event) {
				Widget source = (Widget) event.getSource();
				simplePopup.setPopupPosition(source.getAbsoluteLeft() + 10, source.getAbsoluteTop() + 10);
				simplePopup.show();
			}
		});
		mouseHandler.addMouseOutHandler(new MouseOutHandler() {
			@Override
			public void onMouseOut(MouseOutEvent event) {
				simplePopup.hide();
			}
		});
	}

	private boolean isNew(Ecommerce ecommerce){
		return !contains(ecommerce, ecommerceProduct);
	}
	
	private boolean isDeprecated(Ecommerce ecommerce){
		return !contains(ecommerce, ecommerceTemplate);
	}

	private boolean contains(Ecommerce ecommerce, EcommerceProduct ecommerceProduct){
		boolean match=false;
		for(Ecommerce template: ecommerceProduct.getProductData().getEcommerce()){
			if (template.getCode().equals(ecommerce.getCode())) {
				match=true;
			}
		}
		return match;
	}

	private Widget obtainEditValueWidget(Ecommerce ecommerce){
		Widget valueWidget = null;
		if(ecommerce.getPresetValues()!=null 
				&& ecommerce.getPresetValues().getPresetValue()!=null
				&& ecommerce.getPresetValues().getPresetValue().size()>0){
			ListBox listValue = new ListBox();
			for (String value : ecommerce.getPresetValues().getPresetValue()){
				if(listValue!=null && !listValue.equals("")){
					listValue.addItem(value);
					if(value.equals(ecommerce.getValue())){
						listValue.setSelectedIndex(listValue.getItemCount()-1);
					}
				}
			}
			valueWidget = listValue;
		} else {
			CustomTextArea textAreaValue = new CustomTextArea();
			textAreaValue.setText(ecommerce.getValue());
			if(ecommerce.getValue()!=null && ecommerce.getValue().length()>30){
				textAreaValue.extendLines();
			} else {
				textAreaValue.reduceLines();
			}
			valueWidget = textAreaValue;
		}
		return valueWidget;
	}
	
	
	@UiHandler("cancelButton")
	void cancelButton(ClickEvent event){
		this.hide();
	}
	
	@UiHandler("acceptButton")
	void acceptButton(ClickEvent event){
		accept();
	}
	
	private void accept(){
		pbd = new ProgressBarDialog(2.0, 1.0) {};
		pbd.addStyleName("gwt-PopupPanel-template");
		pbd.setGlassEnabled(true);
		pbd.show();
		
		if(productTemplateList!=null && productTemplateList.getItemCount()>0 && productTemplateList.getSelectedItemText()!=null){
			cleanDeprecatedValues(ecommerceProduct);
			for(int i=0; i<valuesWidgetList.size(); i++){
				Ecommerce ecommerce = ecommerceProduct.getProductData().getEcommerce().get(i);
				Widget w = valuesWidgetList.get(i);
				String value = null;
				if(w instanceof ListBox){
					value = ((ListBox)w).getSelectedValue();
				} else if(w instanceof CustomTextArea){
					value = ((CustomTextArea)w).getText();
				}
				ecommerce.setValue(value);
			}
			marketImpl.insertEcommerceProductValues(getDomain(), login, productTemplateList.getSelectedItemText(), ecommerceProduct, ecommerceProductAttach, new AsyncCallback<Boolean>() {
				@Override
				public void onSuccess(Boolean result) {
					pbd.hide();
					loadValuesGrid();
				}
				@Override
				public void onFailure(Throwable caught) {
					pbd.hide();
					Window.alert(caught.getMessage());
				}
			});
		}
	}
	
	private void cleanDeprecatedValues(EcommerceProduct ecommerceProduct){
		List<Ecommerce> list = new ArrayList<>();
		for(Ecommerce ecommerce: ecommerceProduct.getProductData().getEcommerce()){
			if(ecommerce.getCode()!=null){
				list.add(ecommerce);
			}
		}
		ecommerceProduct.getProductData().setEcommerce(list);
	}
	
	private void applyGridStyle(FlexTable grid){
		for (int i = 0; i < grid.getRowCount(); i++) {
			for (int j = 0; j < grid.getCellCount(i); j++) {
				if ((j % 2) == 0) {
					grid.getCellFormatter().setStyleName(i, j, "aon-panelGrid-odd");
				} else {
					grid.getCellFormatter().setStyleName(i, j, "aon-panelGrid-even");
				}
			}
		}	
	}
	
	private Domain getDomain() {
		return new Domain().setId(JsTemplates.getCurrentDomain()).setName(JsTemplates.getCurrentDomainName());
	}
	
	public class CustomTextArea extends HorizontalPanel {
		
		private TextArea textArea;
		private Button extendButton;
		private boolean extended;
		
		public CustomTextArea() {
			super();
			
			this.textArea = new TextArea();
			this.textArea.setStyleName("aon-inputTextarea");
			
			this.extendButton = new Button();
			loadLinesChangeButton();
			
			this.add(textArea);
			this.add(extendButton);
			
			reduceLines();
		}
		
		public String getText(){
			return this.textArea.getText();
		}
		
		public void setText(String text){
			this.textArea.setText(text);
		}
		
		private void loadLinesChangeButton() {
			extendButton.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					if(extended){
						reduceLines();
					} else {
						extendLines();
					}
				}
			});
		}

		public void reduceLines() {
			this.textArea.setVisibleLines(1);
			this.textArea.setCharacterWidth(30);
			this.extended = false;
			extendButton.setSize("20px", "20px");
			extendButton.setStyleName("aon-icon-edit-add");
		}
		
		public void extendLines() {
			this.textArea.setVisibleLines(4);
			this.textArea.setCharacterWidth(90);
			this.extended = true;
			extendButton.setSize("20px", "20px");
			extendButton.setStyleName("aon-icon-edit-end");
		}
		
		
	}
	
}
