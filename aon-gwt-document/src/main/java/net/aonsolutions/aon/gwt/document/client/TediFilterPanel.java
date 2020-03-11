package net.aonsolutions.aon.gwt.document.client;

import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.css.AonGwtIssuesCSS;
import com.esferalia.aon.gwt.common.client.css.AonGwtIssuesResources;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.TextBox;

public class TediFilterPanel extends Composite {
	
    interface Binder extends UiBinder<HTMLPanel, TediFilterPanel> {
    	
    }
    
    private static Binder binder = GWT.create(Binder.class);

	public static final AonGwtIssuesCSS ICSS = GWT.<AonGwtIssuesResources> create(AonGwtIssuesResources.class).css();

    @UiField TextBox titleFilter;
    
    Documental parent;
    
    public TediFilterPanel(Documental parent) {
    	this.parent = parent;
  
    	initWidget(binder.createAndBindUi(this));    
    	titleFilter.getElement().getStyle().setWidth(500, Unit.PX);
    	titleFilter.addKeyUpHandler(new KeyUpHandler() {
			
			@Override
			public void onKeyUp(KeyUpEvent event) {
				LinkedList<String> list = new LinkedList<>();
				list.add(titleFilter.getText());
				parent.getFilterMap().put("description", list);
				parent.createAttachListPanel();
			}
		});
    }
}
