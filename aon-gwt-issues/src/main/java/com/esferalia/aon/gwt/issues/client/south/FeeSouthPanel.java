package com.esferalia.aon.gwt.issues.client.south;

import com.esferalia.aon.gwt.api.client.AonJsArray;
import com.esferalia.aon.gwt.api.client.finance.JsFee;
import com.esferalia.aon.gwt.common.client.AON;
import com.google.gwt.user.client.ui.Label;
import com.vaadin.polymer.iron.widget.IronIcon;
import com.vaadin.polymer.paper.widget.PaperItem;


public class FeeSouthPanel extends SouthPanel {

    public FeeSouthPanel(AonJsArray<JsFee> items) {   
    	super();
        vertical.setWidth("100%");
        if(items.length() > 0){
        	items.stream().forEach(js -> {
        		vertical.add(buildPaperItem(js));
        	});
        } else {
        	vertical.add(new Label(nothing));
        }
    }
   
    public PaperItem buildPaperItem(JsFee js){
    	PaperItem pi = new PaperItem();
    	IronIcon ironIcon = new IronIcon();
    	ironIcon.setIcon("event");
    	ironIcon.addStyleName(AON.AON_CSS.aonMinWidth24());
    	pi.add(ironIcon);
    	String str =js.getDescription() +  " - Proxima cuota " + js.getPeriod() + " en " + js.getBillingMonth() + "/" + js.getBillingYear() 
    		+ ", activa desde el " + js.getStartDate() + (!js.getEndDate().equals("") ? " y finaliza el " + js.getEndDate() : ""); 
    	pi.add(new Label(str));
    	pi.setStyle("min-height:24px;font-size:12px;padding:0px;");
    	return pi;
    }

}
