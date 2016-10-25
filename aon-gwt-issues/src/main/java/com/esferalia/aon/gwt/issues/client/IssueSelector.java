package com.esferalia.aon.gwt.issues.client;

import com.esferalia.aon.gwt.api.client.AonJsArray;
import com.esferalia.aon.gwt.api.client.incidence.JsIssue;
import com.esferalia.aon.gwt.common.client.AON;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.vaadin.polymer.iron.widget.IronList;
import com.vaadin.polymer.paper.widget.PaperButton;


public class IssueSelector extends Composite {
	
    interface Binder extends UiBinder<HTMLPanel, IssueSelector> {
    	
    }
    
    private static Binder binder = GWT.create(Binder.class);

    @UiField IronList issueSelector;
    @UiField Label selectLabel;
    PaperButton issueButton;
    
    
    Integer top = 0;
    AonJsArray<JsIssue> issues;
        
    public IssueSelector() {   
        initWidget(binder.createAndBindUi(this));
    }
    
    public IssueSelector(AonJsArray<JsIssue> issues) {   
        initWidget(binder.createAndBindUi(this));
        issueSelector.setItems(issues);
        issueSelector.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				JsIssue iss = issueSelector.getSelectedItem().cast();
				selectLabel.setText(iss.getTitle().length()>20 ? 
						iss.getTitle().substring(0, 20)+"... #" + iss.getNumber()
						: iss.getTitle() + " #" + iss.getNumber());
				selectLabel.setStyleName(AON.AON_BOLD);
			}
		});
    }
    
    public JsIssue getSelectedItem(){
    	return issueSelector.getSelectedItem().cast();
    }
    
    public AonJsArray<JsIssue> getItems(){
    	return issueSelector.getItems().cast();
    }


    public void setHeight(String height){
    	issueSelector.setHeight(height);
    }
    
    public void setWidth(String width){
    	issueSelector.setWidth(width);
    }
    
    public IronList getList(){
    	return issueSelector;
    }
}
