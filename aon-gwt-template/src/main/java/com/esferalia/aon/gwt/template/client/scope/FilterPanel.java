package com.esferalia.aon.gwt.template.client.scope;

import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.TextBox;

public class FilterPanel extends Composite {
	
    interface Binder extends UiBinder<HTMLPanel, FilterPanel> {
    	
    }
    
    private static Binder binder = GWT.create(Binder.class);

    @UiField HorizontalPanel panel;
    @UiField InlineLabel label;
    
    private ScopePrincipal parent;
    
    public ScopePrincipal getParent() {
		return parent;
	}

	public void setParent(ScopePrincipal parent) {
		this.parent = parent;
	}

	public FilterPanel(ScopePrincipal parent) {
    	this.parent = parent;
    	initWidget(binder.createAndBindUi(this));       

		InlineLabel lb = new InlineLabel("Descripci\u00f3n");
		lb.setStyleName(AON.AON_CSS.aonInnerLabel());
		lb.setWidth("20px");
		panel.add(lb);
    	
    	TextBox tb = new TextBox();
		tb.setStyleName(AON.AON_CSS.aonInputText());
		tb.addKeyUpHandler(new KeyUpHandler() {
			
			@Override
			public void onKeyUp(KeyUpEvent event) {
				LinkedList<String> list = new LinkedList<>();
				list.add(tb.getValue());
				parent.getFilterMap().put("description", list);
				parent.gridContent();
			}
		});
		panel.add(tb);
    }
    
    public void setTitle(String title){
    	label.setText(title);
    }
  
 
}
