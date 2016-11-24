package com.esferalia.aon.gwt.document.client.nuevo;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.vaadin.polymer.iron.widget.IronCollapse;
import com.vaadin.polymer.iron.widget.IronSelector;
import com.vaadin.polymer.paper.widget.PaperItem;

public class DocumentsConfigurationPanel extends Composite {

    interface Binder extends UiBinder<HTMLPanel , DocumentsConfigurationPanel> {
    	
    }
    
    @UiField HTMLPanel panel;
    
    @UiField IronSelector menuSelector;
    @UiField PaperItem allFilesPaper;
    @UiField PaperItem systemPaper;
    @UiField PaperItem lotePaper;
    
    @UiField Button categoryButton;
    @UiField IronCollapse categoryCollapse;
    @UiField IronSelector categorySelector;
    
    @UiField Button tagButton;
    @UiField IronCollapse tagCollapse;
    @UiField IronSelector tagSelector;
        
    private static Binder binder = GWT.create(Binder.class);
		
    public DocumentsConfigurationPanel() {
    	initWidget(binder.createAndBindUi(this));
   			

        createCategory();
        createTag();
    }
    
  
    
    private void createCategory() {
    	
    }
   
    private void createTag() {
    	
    }
   
}
