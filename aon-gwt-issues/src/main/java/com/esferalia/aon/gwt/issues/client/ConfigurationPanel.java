package com.esferalia.aon.gwt.issues.client;

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
    
    public ConfigurationPanel() {
    	tabs = new PaperTabs();
        initWidget(binder.createAndBindUi(this));
        tabs.setNoBar(false);
        tabs.setNoink(true);
        tabs.setSize("350px", "48px");
        tabs.setSelected("0");
        tabs.addIronSelectHandler(new IronSelectEventHandler() {
			
			@Override
			public void onIronSelect(IronSelectEvent event) {
				tabs.setSize("350px", "48px");
				if(tabs.getSelected().equals(ZERO)
					|| tabs.getSelected() == ZERO){
					//Window.alert("zero");
					//tabContent.add(new TagPanel());
				} else if(tabs.getSelected().equals(ONE)
					|| tabs.getSelected() == ONE){
					//Window.alert("one");
				} else if(tabs.getSelected().equals(TWO)
						|| tabs.getSelected() == TWO){
					//Window.alert("two");
				}
			}
		});
        tabs.setVisible(false);
        
        tabContent.add(new TagPanel());
    }
    
    
}
