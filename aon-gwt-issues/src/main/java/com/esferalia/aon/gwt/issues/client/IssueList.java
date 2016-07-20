package com.esferalia.aon.gwt.issues.client;

import com.esferalia.aon.gwt.api.client.AonJsArray;
import com.esferalia.aon.gwt.api.client.incidence.JsIssue;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.event.dom.client.ScrollHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.vaadin.polymer.iron.widget.IronList;
import com.vaadin.polymer.paper.widget.PaperButton;


public class IssueList extends Composite {
	
    interface Binder extends UiBinder<HTMLPanel, IssueList> {
    	
    }
    
    private static Binder binder = GWT.create(Binder.class);

    @UiField IronList issueList;
     PaperButton issueButton;
    
    Integer top = 0;
    Issues parent;
    AonJsArray<JsIssue> issues;
    
    public IssueList(Issues parent, AonJsArray<JsIssue> issues) {
        initWidget(binder.createAndBindUi(this));
        this.parent = parent;
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
        
        
        issueList.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				JsIssue issue = issueList.getSelectedItem().cast();
			   	parent.contentDockLayoutPanel.removeFromParent();
			   	AonToolbar t = (AonToolbar)parent.toolbar.getWidget(0);
			   	t.setVisibleRefreshButton(false);
			   	parent.contentDockLayoutPanel = new DockLayoutPanel(Unit.PX);
				parent.contentDockLayoutPanel.add(new IssuePanel(parent,issue));
				parent.dockLayoutPanel.add(parent.contentDockLayoutPanel);				
			}
		});
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
