package net.aonsolutions.aon.gwt.udapa.client.quality;

import com.esferalia.aon.gwt.api.client.AonJsArray;
import com.esferalia.aon.gwt.api.client.documental.JsAttach;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.vaadin.polymer.iron.widget.IronList;


public class ImagePanel extends ResizeComposite implements RequiresResize{
	
    interface Binder extends UiBinder<HTMLPanel, ImagePanel> {
    	
    }
    
    private static Binder binder = GWT.create(Binder.class);

    @UiField IronList imgList;
    
    public ImagePanel(AonJsArray<JsAttach> photos) {
        initWidget(binder.createAndBindUi(this));
        imgList.setItems(photos);
    }
    
   
   public AonJsArray<JsAttach> getItems(){
	   return imgList.getItems().cast();
   }
   
   public static native int getPhysicalSize(IronList i) /*-{ 
   		return i._physicalSize;
 	}-*/;

}
