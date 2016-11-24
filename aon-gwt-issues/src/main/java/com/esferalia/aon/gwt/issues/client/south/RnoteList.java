package com.esferalia.aon.gwt.issues.client.south;

import com.esferalia.aon.gwt.api.client.AonJsArray;
import com.esferalia.aon.gwt.api.client.JSON;
import com.esferalia.aon.gwt.api.client.incidence.Incidence;
import com.esferalia.aon.gwt.api.client.incidence.IssueFilter;
import com.esferalia.aon.gwt.api.client.incidence.JsIssue;
import com.esferalia.aon.gwt.common.client.AON;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.event.dom.client.ScrollHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.vaadin.polymer.iron.widget.IronList;
import com.vaadin.polymer.paper.widget.PaperButton;


public class RnoteList extends Composite {
	
    interface Binder extends UiBinder<HTMLPanel, RnoteList> {
    	
    }
    
    private static Binder binder = GWT.create(Binder.class);

    @UiField IronList issueSelector;
    @UiField Label selectLabel;
    PaperButton issueButton;
    
    
    Integer top = 0;
    AonJsArray<JsIssue> issues;
    
    Incidence incidence;
    JsIssue issue;
    Boolean more;
        
    public RnoteList() {   
        initWidget(binder.createAndBindUi(this));
    }
    
    public RnoteList(Incidence incidence, JsIssue issue, IssueFilter iff,
    		String type, AonJsArray<JsIssue> issues) {   
        this.incidence = incidence;
        this.issue = issue;
        more = issues.length()==30;
        initWidget(binder.createAndBindUi(this));
        issueSelector.setItems(issues);
        
        issueSelector.addDomHandler(new ScrollHandler() {
			@Override
			public void onScroll(ScrollEvent event) {
				Integer scrollTop = issueSelector.getElement().getScrollTop();
				Integer offsetHeight = issueSelector.getElement().getOffsetHeight();
				Integer physicalSize = issueSelector.getElement().getScrollHeight();
				Integer maxScrollPosition = physicalSize - offsetHeight;
				if(scrollTop >= maxScrollPosition && more){
					iff.setPage(iff.getPage()+1);
					if(type.equals("dup")) updatelIssueSelectorDup(iff, true);
					else if(type.equals("faq")) updatelIssueSelectorFaq(iff, true);
					top = scrollTop;
				}
			}
		}, ScrollEvent.getType());
        
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
    
    
	public void updatelIssueSelectorDup(IssueFilter filter, Boolean showMore){
		if(!showMore) filter.setPage(1);
		incidence.getLightIssues(issue.getId(), filter, new AsyncCallback<JSON<JsIssue>>() {
			
			@Override
			public void onSuccess(JSON<JsIssue> result) {
				more = result.getData().length()==30;				
				if(showMore) updateItems(getItems().concat(result.getData()).cast());
				else updateItems(result.getData());	
			}
			
			@Override public void onFailure(Throwable arg0) {	}
		});
	}
	
	public void updatelIssueSelectorFaq(IssueFilter filter, Boolean showMore){
		if(!showMore) filter.setPage(1);
		incidence.getFaqIssues(filter, new AsyncCallback<JSON<JsIssue>>() {
			
			@Override
			public void onSuccess(JSON<JsIssue> result) {
				more = result.getData().length()==30;				
				if(showMore) updateItems(getItems().concat(result.getData()).cast());
				else updateItems(result.getData());	
			}
			
			@Override public void onFailure(Throwable arg0) {	}
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
    
    public void updateItems(AonJsArray<JsIssue> issues){
 	   issueSelector.setItems(issues);
 	   issueSelector.getElement().setScrollTop(top);
 	   top = 0;
    }
}
