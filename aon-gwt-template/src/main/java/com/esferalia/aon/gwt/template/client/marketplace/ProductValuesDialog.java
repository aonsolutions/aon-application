package com.esferalia.aon.gwt.template.client.marketplace;


import java.util.LinkedList;
import java.util.List;

import com.esferalia.aon.gwt.common.client.widget.CustomDialogB;
import com.esferalia.aon.gwt.template.client.JsTemplates;
import com.esferalia.aon.gwt.template.client.ProgressBarDialog;
import com.esferalia.aon.gwt.template.shared.EcommerceProduct;
import com.esferalia.aon.gwt.template.shared.EcommerceProduct.ProductData.Ecommerce;
import com.esferalia.aon.gwt.template.shared.Product;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.cellview.client.DataGrid.Style;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class ProductValuesDialog extends CustomDialogB {
	
	
	final IMarketplaceAsync marketImpl = GWT.create(IMarketplace.class);
	
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
	private List<Widget> valuesWidgetList;
	
	private ProgressBarDialog pbd;
	private Attach ecommerceAttach;
	private EcommerceProduct ecommerceProduct;
	private Product product;
	private String login;
	
	public ProductValuesDialog(Product product, String login) {
		this.product = product;
		this.login = login;
		
		templatesGrid = new FlexTable();
		productTemplateList = new ListBox();
		valuesGrid = new FlexTable();

		loadTemplatesGrid();
		
		this.setWidget(binder.createAndBindUi(this));
		
		this.setCaption(product.getName());
		this.setWidth("500px");
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
		
		templatesGrid.setHTML(1, 0, "Nombre");
		templatesGrid.setWidget(1, 1, productTemplateList);
		
		productTemplateList.addItem("", "");
		marketImpl.obtainEcommerceProductTemplates(getDomain(), product, new AsyncCallback<List<Attach>>() {
			@Override
			public void onSuccess(List<Attach> result) {
				for(Attach attach: result){
					productTemplateList.addItem(attach.getDescription(), attach.getId().toString());
				}
			}
			@Override
			public void onFailure(Throwable caught) {}
		});
		productTemplateList.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent arg0) {
				loadValuesGrid();
			}
		});
		
		applyGridStyle(templatesGrid);
		templatesGrid.getCellFormatter().setStyleName(0, 0, "aon-panelGrid-even");
	}
	
	private void loadValuesGrid() {
		valuesGrid.clear();
		valuesGrid.removeAllRows();
		valuesGrid.setStyleName("aon-panelGrid");
		valuesGrid.setWidth("100%");
		valuesGrid.setBorderWidth(1);
		valuesGrid.setCellSpacing(0);

		ecommerceProduct = null;
		ecommerceAttach = null;
		valuesWidgetList = null;
		if(productTemplateList!=null && productTemplateList.getItemCount()>0 && productTemplateList.getSelectedItemText()!=null){
			
			loadEcommerceProductAttach();
			
			if(ecommerceAttach != null){
				loadEcommerceProductValues(ecommerceAttach);
			} else {
				loadEcommerceProductValues();
			}
			
		}
		
	}

	private void loadEcommerceProductAttach() {
		marketImpl.obtainEcommerceProductAttach(getDomain(), product, productTemplateList.getSelectedItemText(), new AsyncCallback<Attach>() {
			@Override
			public void onSuccess(Attach result) {
				ecommerceAttach = result;
			}
			@Override
			public void onFailure(Throwable caught) {}
		});
	}
	
	private void loadEcommerceProductValues(Attach ecommerceAttach2) {
		marketImpl.obtainEcommerceProductValues(getDomain(), ecommerceAttach, product, new AsyncCallback<EcommerceProduct>() {
			@Override
			public void onSuccess(EcommerceProduct result) {
				ecommerceProduct = result;
				loadValuesGridContent();
			}
			@Override
			public void onFailure(Throwable caught) {}
		});
	}
	
	private void loadEcommerceProductValues() {
		marketImpl.obtainEcommerceProductValues(getDomain(), product, productTemplateList.getSelectedItemText(), new AsyncCallback<EcommerceProduct>() {
			@Override
			public void onSuccess(EcommerceProduct result) {
				ecommerceProduct = result;
				loadValuesGridContent();
			}
			@Override
			public void onFailure(Throwable caught) {}
		});
	}

	private void loadValuesGridContent() {
		if(ecommerceProduct!=null){
			valuesGrid.setHTML(0, 0, "Valores de producto" + (ecommerceAttach==null?" - NUEVO":""));
			valuesGrid.getFlexCellFormatter().setColSpan(0, 0, 2);
			valuesGrid.getFlexCellFormatter().setHorizontalAlignment(0, 0, HasHorizontalAlignment.ALIGN_CENTER);
			valuesGrid.getCellFormatter().setStyleName(0, 0, "aon-panelGrid-even");
			
			valuesWidgetList = new LinkedList<>();
			for(Ecommerce ecommerce: ecommerceProduct.getProductData().getEcommerce()){
				valuesWidgetList.add(getEditValueWidget(ecommerce));
			}
			for(Ecommerce ecommerce: ecommerceProduct.getProductData().getEcommerce()){
				int currentRow = valuesGrid.getRowCount();
				
				valuesGrid.setWidget(currentRow, 0, new Label(ecommerce.getName()));
				valuesGrid.setWidget(currentRow, 1, valuesWidgetList.get(currentRow-1));
				
				valuesGrid.getCellFormatter().setStyleName(currentRow, 0, "aon-panelGrid-odd");
				valuesGrid.getCellFormatter().setStyleName(currentRow, 1, "aon-panelGrid-even");
			}
		}
	}
	
	private Widget getEditValueWidget(Ecommerce ecommerce){
		Widget valueWidget = null;
		if(ecommerce.getPresetValues()!=null 
				&& ecommerce.getPresetValues().getPresetValue()!=null
				&& ecommerce.getPresetValues().getPresetValue().size()>0){
			ListBox listValue = new ListBox();
			for (String value : ecommerce.getPresetValues().getPresetValue()) {
				if(listValue!=null && !listValue.equals("")){
					listValue.addItem(value);
					if(value.equals(ecommerce.getValue())){
						listValue.setSelectedIndex(listValue.getItemCount()-1);
					}
				}
			}
			valueWidget = listValue;
		} else {
			TextBox textValue = new TextBox();
			textValue.setStyleName("aon-inputText");
			textValue.setText(ecommerce.getValue());
			valueWidget = textValue;
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
			for(int i=0; i<valuesWidgetList.size(); i++){
				Widget w = valuesWidgetList.get(i);
				String value = null;
				if(w instanceof ListBox){
					value = ((ListBox)w).getSelectedValue();
				} else if(w instanceof TextBox){
					value = ((TextBox)w).getValue();
				}
				ecommerceProduct.getProductData().getEcommerce().get(i).setValue(value);
			}
			marketImpl.insertEcommerceProductValues(getDomain(), login, productTemplateList.getSelectedItemText(), ecommerceProduct, ecommerceAttach, new AsyncCallback<Boolean>() {
				@Override
				public void onSuccess(Boolean result) {
					pbd.hide();
				}
				@Override
				public void onFailure(Throwable caught) {
					pbd.hide();
				}
			});
		}
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
	
}
