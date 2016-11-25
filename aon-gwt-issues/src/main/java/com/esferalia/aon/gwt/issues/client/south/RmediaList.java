package com.esferalia.aon.gwt.issues.client.south;

import com.esferalia.aon.gwt.api.client.AonJsArray;
import com.esferalia.aon.gwt.api.client.registry.JsRmedia;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.vaadin.polymer.iron.widget.IronList;


public class RmediaList extends Composite {
	
    interface Binder extends UiBinder<HTMLPanel, RmediaList> {
    	
    }
    
    private static Binder binder = GWT.create(Binder.class);

    @UiField IronList rmediaList;    
    @UiField InlineLabel dir;
    @UiField InlineLabel comercial;
    @UiField InlineLabel seg;
    
    public RmediaList() {   
        initWidget(binder.createAndBindUi(this));
    }
    
    public RmediaList(AonJsArray<JsRmedia> items) {   
        initWidget(binder.createAndBindUi(this));
        rmediaList.setItems(items);
    }
	
    public JsRmedia getSelectedItem(){
    	return rmediaList.getSelectedItem().cast();
    }
    
    public AonJsArray<JsRmedia> getItems(){
    	return rmediaList.getItems().cast();
    }


    public void setHeight(String height){
    	rmediaList.setHeight(height);
    }
    
    public void setWidth(String width){
    	rmediaList.setWidth(width);
    }
    
    public IronList getList(){
    	return rmediaList;
    }
    
    public void setDirection(String direction){
    	dir.setText(direction);
    }
    
    public void setCommercial(String commercial){
    	comercial.setText(commercial);
    }
    
    public void setSegmentation(String segmentation){
    	seg.setText(segmentation);
    }
    
}
