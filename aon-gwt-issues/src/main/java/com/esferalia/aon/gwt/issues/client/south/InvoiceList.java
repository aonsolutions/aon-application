package com.esferalia.aon.gwt.issues.client.south;

import com.esferalia.aon.gwt.api.client.AonJsArray;
import com.esferalia.aon.gwt.api.client.finance.JsBoughtProduct;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.vaadin.polymer.iron.widget.IronIcon;
import com.vaadin.polymer.paper.widget.PaperItem;


public class InvoiceList extends Composite {
	
    interface Binder extends UiBinder<HTMLPanel, InvoiceList> {
    	
    }
    
    private static Binder binder = GWT.create(Binder.class);

    @UiField VerticalPanel vertical;
    public InvoiceList() {   
 
        initWidget(binder.createAndBindUi(this));
    }
    
    public InvoiceList(AonJsArray<JsBoughtProduct> items) {   
        initWidget(binder.createAndBindUi(this));
        vertical.setWidth("100%");
        items.stream().forEach(js -> {
        	PaperItem pi = buildPaperItem(js);
        	VerticalPanel vp = new VerticalPanel();
        	vp.setVisible(false);
        	if(js.getArray().length() > 0){
        		vp.getElement().getStyle().setPaddingLeft(24, Unit.PX);
        		js.getArray().stream().forEach(js2 -> {
        			PaperItem pi2 = buildPaperItem(js2);
        			vp.add(pi2);
        		});
        	}
        	
        	pi.addClickHandler(new ClickHandler() {
    			
    			@Override
    			public void onClick(ClickEvent arg0) {
    				if(vp.isVisible()) vp.setVisible(false);
    				else vp.setVisible(true);
    			}
    		});
        	vertical.add(pi);
        	vertical.add(vp);
        });
      	
    }
   
    public PaperItem buildPaperItem(JsBoughtProduct js){
    	PaperItem pi = new PaperItem();
    	IronIcon ironIcon = new IronIcon();
    	ironIcon.setIcon("receipt");
    	pi.add(ironIcon);
    	String str = js.getDate() + " - " + js.getCode() + " - " 
    			+ js.getDescription() + " - " + js.getTotal();
    	pi.add(new Label(str));
    	pi.setStyle("min-height:24px;font-size:12px;padding:0px;");
    	return pi;
    }
}
