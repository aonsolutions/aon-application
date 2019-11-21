package com.esferalia.aon.gwt.fiscal.client.tedi;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.css.AonGwtIssuesCSS;
import com.esferalia.aon.gwt.common.client.css.AonGwtIssuesResources;
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

public abstract class FilterPanel extends Composite {
	
    interface Binder extends UiBinder<HTMLPanel, FilterPanel> {
    	
    }
    
    private static Binder binder = GWT.create(Binder.class);

	public static final AonGwtIssuesCSS ICSS = GWT.<AonGwtIssuesResources> create(AonGwtIssuesResources.class).css();

    @UiField HorizontalPanel panel;

    
    
    protected abstract void onDocumentChange(String value);
    	
    protected abstract void onNameChange(String value);

    private String document = null;
	private String name = null;

    public FilterPanel() {
    	initWidget(binder.createAndBindUi(this));       
    
    	panel.addStyleName(AON.AON_CSS.aonMarginTop());  
    	
    	InlineLabel documentLabel = new InlineLabel("NIF");
    	documentLabel.setStyleName(AON.AON_CSS.aonInnerLabel());
    	documentLabel.setWidth("20px");
    	panel.add(documentLabel);
    	
		TextBox documentBox = new TextBox();
		documentBox.setStyleName(AON.AON_CSS.aonInputText());
		documentBox.addKeyUpHandler(new KeyUpHandler() {
			
			@Override
			public void onKeyUp(KeyUpEvent event) {
				setDocument(documentBox.getValue());
				onDocumentChange(documentBox.getValue());
			}
		});
		panel.add(documentBox);

    	InlineLabel nameLabel = new InlineLabel("Nombre");
    	nameLabel.setStyleName(AON.AON_CSS.aonInnerLabel());
    	nameLabel.setWidth("20px");
    	panel.add(nameLabel);
    	
    	TextBox nameBox = new TextBox();
		nameBox.setStyleName(AON.AON_CSS.aonInputText());
		nameBox.addKeyUpHandler(new KeyUpHandler() {
			
			@Override
			public void onKeyUp(KeyUpEvent event) {
				setName(nameBox.getValue());
				onNameChange(nameBox.getValue());
			}
		});
		panel.add(nameBox);
    }

	public String getDocument() {
		return document;
	}

	public void setDocument(String document) {
		this.document = document;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
    
 }
