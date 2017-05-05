package com.esferalia.aon.gwt.document.client.nuevo;


import com.esferalia.aon.gwt.api.client.AonJsArray;
import com.esferalia.aon.gwt.api.client.documental.JsAttach;
import com.esferalia.aon.gwt.api.client.incidence.JsIssue;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.event.dom.client.ScrollHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.polymer.iron.widget.IronList;


public class AttachListPanel2 extends Composite {
	
    interface Binder extends UiBinder<HTMLPanel, AttachListPanel2> {
    	
    }
    
    private static Binder binder = GWT.create(Binder.class);

    @UiField IronList grid;
    Documental parent;
    Integer top = 0;
    
    public AttachListPanel2(Documental parent, AonJsArray<JsAttach> items) {    
    	this.parent = parent;
        initWidget(binder.createAndBindUi(this));
        
        grid.setItems(items);
      
        autoHeight(grid, 260);
        
        grid.addDomHandler(new ScrollHandler() {
			@Override
			public void onScroll(ScrollEvent event) {
				Integer scrollTop = grid.getElement().getScrollTop();
				Integer offsetHeight = grid.getElement().getOffsetHeight();
				Integer physicalSize = grid.getElement().getScrollHeight();
				Integer maxScrollPosition = physicalSize - offsetHeight;
				/*if(scrollTop > maxScrollPosition && parent.more){
					parent.issueFilter.setPage(parent.issueFilter.getPage()+1);
					parent.updateIssueList(parent.issueFilter, true);		
					top = scrollTop;
				}*/
			}
		}, ScrollEvent.getType());

   
    }
    
   public void updateItems(AonJsArray<JsIssue> issues){
	   grid.setItems(issues);
	   grid.getElement().setScrollTop(top);
	   top = 0;
   }
   
   public AonJsArray<JsIssue> getItems(){
	   return grid.getItems().cast();
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
