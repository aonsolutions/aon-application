package com.esferalia.aon.gwt.issues.client;

import com.esferalia.aon.gwt.api.client.incidence.IssueFilter;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.vaadin.polymer.paper.widget.PaperButton;

public class FilterPanel extends Composite {
	
    interface Binder extends UiBinder<HTMLPanel, FilterPanel> {
    	
    }
    
    private static Binder binder = GWT.create(Binder.class);

    Issues issues;

    @UiField PaperButton openButton;
    @UiField PaperButton closeButton;
    @UiField TextBox titleFilter;
    @UiField Button cleanButton;
    
    public FilterPanel(Issues issues) {
    	this.issues = issues;
    	
    	initWidget(binder.createAndBindUi(this));       
    	
    	titleFilter.addKeyUpHandler(new KeyUpHandler() {
			
			@Override
			public void onKeyUp(KeyUpEvent event) {
				if(event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ENTER){
					getIssues().issueFilter.setTitle(titleFilter.getText());
					getIssues().updateIssueList(getIssues().issueFilter, false);
				}
			}
		});
    }
    
	@UiHandler("openButton")
	void openButtonClick(ClickEvent event){
		getIssues().issueFilter.setState("open");
		getIssues().updateIssueList(issues.issueFilter, false);
	}
	
	@UiHandler("closeButton")
	void closeButtonClick(ClickEvent event){
		getIssues().issueFilter.setState("closed");
		getIssues().updateIssueList(issues.issueFilter, false);
	}
	
	@UiHandler("cleanButton")
	void cleanButtonClick(ClickEvent event){
		getIssues().issueFilter = new IssueFilter();
		getIssues().updateIssueList(issues.issueFilter, false);
	}

	public Issues getIssues() {
		return issues;
	}

}
