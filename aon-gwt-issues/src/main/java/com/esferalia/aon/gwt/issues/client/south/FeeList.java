package com.esferalia.aon.gwt.issues.client.south;

import com.esferalia.aon.gwt.api.client.AonJsArray;
import com.esferalia.aon.gwt.api.client.finance.JsFee;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.vaadin.polymer.iron.widget.IronList;


public class FeeList extends Composite {
	
    interface Binder extends UiBinder<HTMLPanel, FeeList> {
    	
    }
    
    private static Binder binder = GWT.create(Binder.class);

    @UiField IronList feeList;    

    public FeeList() {   
        initWidget(binder.createAndBindUi(this));
    }
    
    public FeeList(AonJsArray<JsFee> items) {   
        initWidget(binder.createAndBindUi(this));
        feeList.setItems(items);
    }
	
    public JsFee getSelectedItem(){
    	return feeList.getSelectedItem().cast();
    }
    
    public AonJsArray<JsFee> getItems(){
    	return feeList.getItems().cast();
    }


    public void setHeight(String height){
    	feeList.setHeight(height);
    }
    
    public void setWidth(String width){
    	feeList.setWidth(width);
    }
    
    public IronList getList(){
    	return feeList;
    }
    
}
