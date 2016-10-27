package com.esferalia.aon.gwt.issues.client;

import com.esferalia.aon.gwt.api.client.incidence.Incidence;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.vaadin.polymer.iron.widget.event.IronSelectEvent;
import com.vaadin.polymer.iron.widget.event.IronSelectEventHandler;
import com.vaadin.polymer.paper.widget.PaperTabs;

public class ConfigurationPanel extends Composite {
	
    interface Binder extends UiBinder<HTMLPanel, ConfigurationPanel> {
    	
    }
    
    private static Binder binder = GWT.create(Binder.class);
    
    @UiField HTMLPanel tabContent;
    @UiField PaperTabs tabs;

    private static final String ZERO = "0";
    private static final String ONE = "1";
    private static final String TWO = "2";
    
    Incidence incidence;
    Issues issues;
    public ConfigurationPanel(Issues issues, Incidence incidence) {
    	this.issues = issues;
    	this.incidence = incidence;
    	initWidget(binder.createAndBindUi(this));
        tabs.setSelected("0");
        tabs.addIronSelectHandler(new IronSelectEventHandler() {
			
			@Override
			public void onIronSelect(IronSelectEvent event) {
				if(tabs.getSelected().equals(ZERO)
					|| tabs.getSelected() == ZERO){
					if(tabContent.getWidgetCount()> 0){
						for(Integer i = 0; i < tabContent.getWidgetCount(); i++)
							tabContent.remove(i);
					}
					tabContent.add(new TagPanel(incidence));
				} else if(tabs.getSelected().equals(ONE)
					|| tabs.getSelected() == ONE){
					if(tabContent.getWidgetCount()> 0){
						for(Integer i = 0; i < tabContent.getWidgetCount(); i++)
							tabContent.remove(i);
					}
					tabContent.add(new NotificationPanel(incidence));
					
				} else if(tabs.getSelected().equals(TWO)
						|| tabs.getSelected() == TWO){
					if(tabContent.getWidgetCount()> 0){
						for(Integer i = 0; i < tabContent.getWidgetCount(); i++)
							tabContent.remove(i);
					}
					tabContent.add(new ConfPanel(issues, incidence));
				}
			}
		});
                
    }
    
    
}
