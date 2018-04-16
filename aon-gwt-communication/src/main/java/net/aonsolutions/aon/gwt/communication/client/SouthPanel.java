package net.aonsolutions.aon.gwt.communication.client;

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
    protected static final String EMPTY_MESSAGE = "NO HAY DATOS";  
    
    CommunicationPrincipal parent;
	
	protected API getAPI() {
		return parent.getAPI();
	}
	
    @UiField VerticalPanel vertical;
    
    public SouthPanel(CommunicationPrincipal parent) {
    	this.parent =parent;
        initWidget(binder.createAndBindUi(this));
    }
}
