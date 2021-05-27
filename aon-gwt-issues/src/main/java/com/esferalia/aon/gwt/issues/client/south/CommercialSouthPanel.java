package com.esferalia.aon.gwt.issues.client.south;

import com.esferalia.aon.gwt.api.client.AonJsArray;
import com.esferalia.aon.gwt.api.client.project.JsCommercialTracking;
import com.esferalia.aon.gwt.api.client.project.JsProject;
import com.esferalia.aon.gwt.common.client.AON;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.vaadin.polymer.iron.widget.IronIcon;
import com.vaadin.polymer.paper.widget.PaperItem;


public class CommercialSouthPanel extends SouthPanel {

    public CommercialSouthPanel(AonJsArray<JsProject> items) {   
    	super();
        vertical.setWidth("100%");
        if(items.length() > 0){
        	items.stream().forEach(js -> {
        		PaperItem pi = buildProject(js);
        		VerticalPanel vp = new VerticalPanel();
        		vp.setVisible(false);
        		
        		if(js.getCommercialTrackingList().length() > 0){
        			vp.getElement().getStyle().setPaddingLeft(24, Unit.PX);
        			js.getCommercialTrackingList().stream().forEach(js2 -> {
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
    
    public PaperItem buildProject(JsProject js){
    	PaperItem pi = new PaperItem();
    	IronIcon ironIcon = new IronIcon();
    	ironIcon.setIcon("work");
    	ironIcon.setTitle(js.getComment());
    	ironIcon.addStyleName(AON.AON_CSS.aonMinWidth24());
    	pi.add(ironIcon);
    	String str = js.getDate() + " - " + js.getName() + ", comercial: " + js.getSeller().getName() ;
    	pi.add(new Label(str));
    	pi.setStyle("min-height:24px;font-size:12px;padding:0px;");
    	return pi;
    }
   
    public PaperItem buildPaperItem(JsCommercialTracking js){
    	PaperItem pi = new PaperItem();
    	IronIcon ironIcon = new IronIcon();
    	ironIcon.setIcon("assignment");
    	ironIcon.setTitle(js.getComment());
    	ironIcon.addStyleName(AON.AON_CSS.aonMinWidth24());
    	pi.add(ironIcon);
    	String str = js.getDate() + " - comercial: " + js.getSeller().getName(); // + " - " + js.getComment();
    	pi.add(new Label(str));
    	pi.setStyle("min-height:24px;font-size:12px;padding:0px;");
    	return pi;
    }
}
