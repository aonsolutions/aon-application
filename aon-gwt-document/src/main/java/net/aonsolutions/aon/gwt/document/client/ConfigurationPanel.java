package net.aonsolutions.aon.gwt.document.client;

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

public class ConfigurationPanel extends Composite {
	
    interface Binder extends UiBinder<HTMLPanel, ConfigurationPanel> {
    	
    }
    
    private static Binder binder = GWT.create(Binder.class);
    
    @UiField HTMLPanel panel;
    @UiField HTMLPanel tabContent;

    public ConfigurationPanel(Documental parent) {
    	initWidget(binder.createAndBindUi(this));
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
		sp.add(new DocumentsConfigurationPanel(parent));
		tabContent.add(sp);               
    }
    
    
}
