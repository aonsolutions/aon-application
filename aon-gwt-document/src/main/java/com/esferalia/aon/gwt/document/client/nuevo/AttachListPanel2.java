package com.esferalia.aon.gwt.document.client.nuevo;


import com.esferalia.aon.gwt.api.client.AonJsArray;
import com.esferalia.aon.gwt.api.client.documental.JsAttach;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
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
    
    public AttachListPanel2(AonJsArray<JsAttach> items) {    
        initWidget(binder.createAndBindUi(this));
        
        grid.setItems(items);
      
    }
    


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
