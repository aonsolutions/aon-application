package com.esferalia.aon.gwt.issues.client.south;

import com.esferalia.aon.gwt.api.client.AonJsArray;
import com.esferalia.aon.gwt.api.client.incidence.JsIssue;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Label;
import com.vaadin.polymer.iron.widget.IronIcon;
import com.vaadin.polymer.paper.widget.PaperItem;


public abstract class TaskSouthPanel extends SouthPanel {

    public TaskSouthPanel(JsIssue issue, AonJsArray<JsIssue> items) {   
    	super();
        vertical.setWidth("100%");
        if(items.length() > 0){
        	items.stream().forEach(js -> {
        		PaperItem pi = buildPaperItem(issue, js);
        		pi.addClickHandler(new ClickHandler() {
        			
        			@Override
        			public void onClick(ClickEvent arg0) {
        				onIssueClick(js);
        			}
        		});
        		vertical.add(pi);
        	});
        } else {
        	vertical.add(new Label(nothing));
        }
    }
   
    public PaperItem buildPaperItem(JsIssue issue, JsIssue js){
    	PaperItem pi = new PaperItem();
    	IronIcon ironIcon = new IronIcon();
    	ironIcon.setIcon("error-outline");
    	ironIcon.setStyle("color:" + js.getColor());
    	ironIcon.setTitle(js.getBody());
    	pi.add(ironIcon);
    	String str = js.getTitle() + "#" + js.getNumber() +" creado por "+ js.getUser().getLogin() 
    			+ " el "+ js.getCreatedAtDate() + " a las "+ js.getCreatedAtHour() + js.getDays();
    	pi.add(new Label(str));
    	if(issue.getId() == js.getId()) 
    		pi.setStyle("min-height:24px;font-size:12px;padding:0px;font-weight:bold;");
    	else pi.setStyle("min-height:24px;font-size:12px;padding:0px;");
    	return pi;
    }
    
    protected abstract void onIssueClick(JsIssue issue);
}
