package com.esferalia.aon.gwt.issues.client.south;

import com.esferalia.aon.gwt.api.client.AonJsArray;
import com.esferalia.aon.gwt.api.client.finance.JsBoughtProduct;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.vaadin.polymer.iron.widget.IronIcon;
import com.vaadin.polymer.paper.widget.PaperItem;


public class SalesSouthPanel extends SouthPanel {

    public SalesSouthPanel(AonJsArray<JsBoughtProduct> items) {   
    	super();
        vertical.setWidth("100%");
        if(items.length() > 0){
        	items.stream().forEach(js -> {
        		PaperItem pi = buildProduct(js);
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
        } else {
        	vertical.add(new Label(nothing));
        }
    }
    
    public PaperItem buildProduct(JsBoughtProduct js){
    	PaperItem pi = new PaperItem();
    	IronIcon ironIcon = new IronIcon();
    	ironIcon.setIcon("receipt");
    	pi.add(ironIcon);
    	String str = js.getCode() + " - " + js.getName() + ", Total: " + js.getArray().length() + " - Ultima venta el " + js.getDate();
    	pi.add(new Label(str));
    	pi.setStyle("min-height:24px;font-size:12px;padding:0px;");
    	return pi;
    }
   
    public PaperItem buildPaperItem(JsBoughtProduct js){
    	PaperItem pi = new PaperItem();
    	IronIcon ironIcon = new IronIcon();
    	ironIcon.setIcon("receipt");
    	pi.add(ironIcon);
    	String str = "El " + js.getDate() + " - " + js.getQuantity() + (js.getQuantity() == 1 ? "unidad" : " unidades") + " en Fra.: " + js.getReferenceCode() + " - " 
    			+ js.getDescription();
    	pi.add(new Label(str));
    	pi.setStyle("min-height:24px;font-size:12px;padding:0px;");
    	return pi;
    }
}
