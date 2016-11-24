package com.esferalia.aon.gwt.document.client.nuevo;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.TextBox;

public class FilterPanel extends Composite {
	
    interface Binder extends UiBinder<HTMLPanel, FilterPanel> {
    	
    }
    
    private static Binder binder = GWT.create(Binder.class);

    @UiField Button advanceSearch;
    @UiField Button cleanFilterButton;
    @UiField TextBox searchBox;
    @UiField SuggestBox enterpriseSearchBox;
    @UiField Button reset;
    @UiField Button searchButton;
    @UiField Button eSearchButton;
    
    
    
    public FilterPanel() {
    	initWidget(binder.createAndBindUi(this));       
    	
    }
}
