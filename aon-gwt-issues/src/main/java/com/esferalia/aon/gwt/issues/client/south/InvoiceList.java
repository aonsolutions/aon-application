package com.esferalia.aon.gwt.issues.client.south;

import com.esferalia.aon.gwt.api.client.AonJsArray;
import com.esferalia.aon.gwt.api.client.finance.JsInvoice;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.vaadin.polymer.iron.widget.IronList;


public class InvoiceList extends Composite {
	
    interface Binder extends UiBinder<HTMLPanel, InvoiceList> {
    	
    }
    
    private static Binder binder = GWT.create(Binder.class);

    @UiField IronList invoiceList;    

    public InvoiceList() {   
        initWidget(binder.createAndBindUi(this));
    }
    
    public InvoiceList(AonJsArray<JsInvoice> items) {   
        initWidget(binder.createAndBindUi(this));
        invoiceList.setItems(items);
    }
	
    public JsInvoice getSelectedItem(){
    	return invoiceList.getSelectedItem().cast();
    }
    
    public AonJsArray<JsInvoice> getItems(){
    	return invoiceList.getItems().cast();
    }


    public void setHeight(String height){
    	invoiceList.setHeight(height);
    }
    
    public void setWidth(String width){
    	invoiceList.setWidth(width);
    }
    
    public IronList getList(){
    	return invoiceList;
    }
    
}
