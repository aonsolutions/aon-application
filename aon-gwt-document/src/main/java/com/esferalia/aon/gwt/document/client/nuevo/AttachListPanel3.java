package com.esferalia.aon.gwt.document.client.nuevo;


import com.esferalia.aon.gwt.api.client.JSON;
import com.esferalia.aon.gwt.api.client.documental.JsAttach;
import com.esferalia.aon.gwt.api.client.incidence.Incidence;
import com.esferalia.aon.gwt.api.client.incidence.JsObject;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;


public class AttachListPanel3 extends Composite {
	
    interface Binder extends UiBinder<HTMLPanel, AttachListPanel3> {
    	
    }
    
    private static Binder binder = GWT.create(Binder.class);

    @UiField DataGrid<JsAttach> grid;
    
    public AttachListPanel3() {    
        initWidget(binder.createAndBindUi(this));
        Incidence i = new Incidence("http://zuremoto.aibanez.net/", "amigo", "aaa", "zuremoto.aibanez.net", "zuremoto.aibanez.net");
        i.getOrderOptions(new AsyncCallback<JSON<JsObject>>() {
			
			@Override
			public void onSuccess(JSON<JsObject> result) {
				
				// INSERT DATAS....
				
			}
			
			@Override
			public void onFailure(Throwable arg0) {
					
			}
		});      
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
