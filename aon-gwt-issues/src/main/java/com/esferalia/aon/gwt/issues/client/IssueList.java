package com.esferalia.aon.gwt.issues.client;

import com.esferalia.aon.gwt.api.client.AonJsArray;
import com.esferalia.aon.gwt.api.client.incidence.JsIssue;
import com.esferalia.aon.gwt.issues.client.css.AonGwtIssuesResources;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.event.dom.client.ScrollHandler;
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
    
    Integer top = 0;
    
    public IssueList(Issues parent, AonJsArray<JsIssue> issues) {
        initWidget(binder.createAndBindUi(this));
        issueList.setItems(issues);
        issueList.addDomHandler(new ScrollHandler() {
			@Override
			public void onScroll(ScrollEvent event) {
				Integer scrollTop = issueList.getElement().getScrollTop();
				Integer offsetHeight = issueList.getElement().getOffsetHeight();
				Integer physicalSize = issueList.getElement().getScrollHeight();
				Integer maxScrollPosition = physicalSize - offsetHeight;
				if(scrollTop > maxScrollPosition){
					parent.issueFilter.setPage(parent.issueFilter.getPage()+1);
					parent.updateIssueList(parent.issueFilter, true);		
					top = scrollTop;
				}
			}
		}, ScrollEvent.getType());
    }
    
   public void updateItems(AonJsArray<JsIssue> issues){
	   issueList.setItems(issues);
	   issueList.getElement().setScrollTop(top);
	   top = 0;
   }
   
   public AonJsArray<JsIssue> getItems(){
	   return issueList.getItems().cast();
   }
   
   public static native int getPhysicalSize(IronList i) /*-{ 
   		return i._physicalSize;
 	}-*/;
}
