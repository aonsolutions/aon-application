package com.esferalia.aon.gwt.issues.client.south;

import com.esferalia.aon.gwt.api.client.incidence.JsGeneral;
import com.esferalia.aon.gwt.api.client.registry.JsRmedia;
import com.esferalia.aon.gwt.common.client.AON;
import com.google.gwt.user.client.ui.Label;
import com.vaadin.polymer.iron.widget.IronIcon;
import com.vaadin.polymer.paper.widget.PaperItem;


public class GeneralInfoSouthPanel extends SouthPanel {

    public GeneralInfoSouthPanel(JsGeneral general) {   
    	super();
        vertical.setWidth("100%");
  
        // OBSERVACION
        if(general.getStatus() != null && !general.getStatus().equals(""))
        	vertical.add(buildGeneral("report-problem", general.getStatus()));
  
        // OBSERVACION
        if(general.getObservation() != null && !general.getObservation().equals(""))
        	vertical.add(buildGeneral("visibility", "Observacion: " + general.getObservation()));
        
        // DIRECCION
        if(general.getDirection() != null && !general.getDirection().equals(""))
        	vertical.add(buildGeneral("room", "Direccion: " + general.getDirection()));
        
        // COMERCIAL
        if(general.getCommercial() != null && !general.getCommercial().equals(""))
        	vertical.add(buildGeneral("work", "Comercial: " + general.getCommercial()));
        
        // SEGMENTACION
        if(general.getSegmentation() != null && !general.getSegmentation().equals(""))
        	vertical.add(buildGeneral("view-module", "Segmentacion: " + general.getSegmentation()));
        
        // RMEDIA
        general.getRmedia().stream().forEach(js -> {
        	vertical.add(buildRmedia(js));
        });
    }
   
    public PaperItem buildGeneral(String icon, String str){
    	PaperItem pi = new PaperItem();
    	IronIcon ironIcon = new IronIcon();
    	ironIcon.setIcon(icon);
    	ironIcon.addStyleName(AON.AON_CSS.aonMinWidth24());
    	pi.add(ironIcon);
    	pi.add(new Label(str));
    	pi.setStyle("min-height:24px;font-size:12px;padding:0px;");
    	return pi;
    }
    
    public PaperItem buildRmedia(JsRmedia js){
    	PaperItem pi = new PaperItem();
    	IronIcon ironIcon = new IronIcon();
    	ironIcon.setIcon(js.getIcon());
    	ironIcon.addStyleName(AON.AON_CSS.aonMinWidth24());
    	pi.add(ironIcon);
    	String str = js.getValue() + " - " + js.getComment();
    	pi.add(new Label(str));
    	pi.setStyle("min-height:24px;font-size:12px;padding:0px;");
    	return pi;
    }
}
