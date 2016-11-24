package com.esferalia.aon.gwt.document.client.nuevo;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.vaadin.polymer.iron.widget.event.IronSelectEvent;
import com.vaadin.polymer.iron.widget.event.IronSelectEventHandler;
import com.vaadin.polymer.paper.widget.PaperTabs;

public class ConfigurationPanel extends Composite {
	
    interface Binder extends UiBinder<HTMLPanel, ConfigurationPanel> {
    	
    }
    
    private static Binder binder = GWT.create(Binder.class);
    
    @UiField HTMLPanel panel;
    @UiField HTMLPanel tabContent;
    @UiField PaperTabs tabs;

    private static final String ZERO = "0";
    private static final String ONE = "1";

    public ConfigurationPanel() {
    	initWidget(binder.createAndBindUi(this));
    	tabs.setSelected("0");
        tabs.addIronSelectHandler(new IronSelectEventHandler() {
			
			@Override
			public void onIronSelect(IronSelectEvent event) {
				for(Integer i = 0; i < tabContent.getWidgetCount(); i++)
					tabContent.remove(i);
				ScrollPanel sp = new ScrollPanel();
				Integer h = Window.getClientHeight() -190;
				sp.getElement().getStyle().setHeight(h, Unit.PX);
		    	Window.addResizeHandler(new ResizeHandler() {
					
					@Override
					public void onResize(ResizeEvent event) {
						Integer h = Window.getClientHeight() -190;
						sp.getElement().getStyle().setHeight(h, Unit.PX);
					}
				});
				if(tabs.getSelected().toString().equals(ZERO)
					|| tabs.getSelected().toString() == ZERO){
					sp.add(new DocumentsConfigurationPanel());
				} else if(tabs.getSelected().toString().equals(ONE)
					|| tabs.getSelected().toString() == ONE){
					sp.add(new DriveConfigurationPanel());					
				}
				tabContent.add(sp);
			}
		});
                
    }
    
    
}
