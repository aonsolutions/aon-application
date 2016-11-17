package com.esferalia.aon.gwt.issues.client;

import com.esferalia.aon.gwt.api.client.AonJsArray;
import com.esferalia.aon.gwt.api.client.incidence.Incidence;
import com.esferalia.aon.gwt.api.client.incidence.JsIssue;
import com.esferalia.aon.gwt.common.client.polymer.AonToolbar;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.event.dom.client.ScrollHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
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
        
    public IssueList(Issues parent, Incidence incidence, AonJsArray<JsIssue> issues) {    
        initWidget(binder.createAndBindUi(this));
        this.parent = parent;
        issueList.setItems(issues);

        autoHeight(issueList, 260);
        
        issueList.addDomHandler(new ScrollHandler() {
			@Override
			public void onScroll(ScrollEvent event) {
				Integer scrollTop = issueList.getElement().getScrollTop();
				Integer offsetHeight = issueList.getElement().getOffsetHeight();
				Integer physicalSize = issueList.getElement().getScrollHeight();
				Integer maxScrollPosition = physicalSize - offsetHeight;
				if(scrollTop > maxScrollPosition && parent.more){
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
				if(issue != null && !issue.isDeleted()){
					parent.contentDockLayoutPanel.removeFromParent();
					AonToolbar t = (AonToolbar)parent.toolbar.getWidget(0);
					t.setVisibleRefreshButton(false);
					parent.contentDockLayoutPanel = new DockLayoutPanel(Unit.PX);
					parent.contentDockLayoutPanel.add(new IssuePanel(parent, incidence, issue));
					parent.dockLayoutPanel.add(parent.contentDockLayoutPanel);				
				} else if(issue.isDeleted()){
					AonDialog2 d = new AonDialog2("Restaurar Tarea",new Label("Est\u00e1s seguro de restaurar la tarea #" + issue.getNumber()) ) {
						
						@Override protected void onCancel() {hide();}
						
						@Override
						protected void onAccept() {
							String request = "{\"state\":\""+ "restore" +"\"}";
							incidence.updateOrgIssue(issue, request, new AsyncCallback<JsIssue>() {
								
								@Override
								public void onSuccess(JsIssue result) {
									parent.updateIssueList(parent.issueFilter, false);
								}
								
								@Override public void onFailure(Throwable caught) {}
							});
							hide();
						}
					};
					d.getElement().getStyle().setWidth(255, Unit.PX);
					d.center();
				}
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
   

   public void autoHeight(Widget widget, Integer value){
	   widget.getElement().getStyle().setHeight(Window.getClientHeight() - value, Unit.PX);
	   Window.addResizeHandler(new ResizeHandler() {
			
			@Override
			public void onResize(ResizeEvent event) {
				widget.getElement().getStyle().setHeight(Window.getClientHeight() - value, Unit.PX);
			}
		});
   }
}
