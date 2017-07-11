package net.aonsolutions.aon.gwt.sii.client.sii;

import com.esferalia.aon.gwt.api.client.API;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.VerticalPanel;


public class SouthPanel extends Composite {
	
    interface Binder extends UiBinder<HTMLPanel, SouthPanel> {
    	
    }
    
    private static Binder binder = GWT.create(Binder.class);
    protected static final String nothing = "NO HAY DATOS RELACIONADOS";  
    
	SiiPrincipal parent;
	
	protected API getAPI() {
		return parent.getAPI();
	}
	
    @UiField VerticalPanel vertical;
    
    public SouthPanel(SiiPrincipal parent) {
    	this.parent =parent;
        initWidget(binder.createAndBindUi(this));
    }
}
