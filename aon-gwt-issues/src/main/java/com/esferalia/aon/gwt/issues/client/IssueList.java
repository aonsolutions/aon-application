package com.esferalia.aon.gwt.issues.client;

import com.esferalia.aon.gwt.api.client.incidence.JsIssue;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.vaadin.polymer.iron.widget.IronList;

public class IssueList extends Composite {
	
    interface Binder extends UiBinder<HTMLPanel, IssueList> {
    	
    }
    
    private static Binder binder = GWT.create(Binder.class);

    @UiField IronList issueList;

    public IssueList(JsArray<JsIssue> issues) {
        initWidget(binder.createAndBindUi(this));
        issueList.setItems(issues);
    }
    
   public void updateItems(JsArray<JsIssue> issues){
	   issueList.setItems(issues);
   }
}
