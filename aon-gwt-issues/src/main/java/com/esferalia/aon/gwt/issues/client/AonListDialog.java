package com.esferalia.aon.gwt.issues.client;

import com.esferalia.aon.gwt.api.client.AonJsArray;
import com.esferalia.aon.gwt.api.client.incidence.JsLabel;
import com.esferalia.aon.gwt.api.client.incidence.JsUser;
import com.esferalia.aon.gwt.common.client.css.AonResources;
import com.esferalia.aon.gwt.issues.client.css.AonGwtIssuesCSS;
import com.esferalia.aon.gwt.issues.client.css.AonGwtIssuesResources;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.vaadin.polymer.vaadin.widget.VaadinComboBox;
import com.vaadin.polymer.vaadin.widget.event.ValueChangedEvent;
import com.vaadin.polymer.vaadin.widget.event.ValueChangedEventHandler;

public abstract class AonListDialog   extends PopupPanel {
	
    interface Binder extends UiBinder<HTMLPanel, AonListDialog> {}
  
    private static Binder binder = GWT.create(Binder.class);
    

    @UiField HTMLPanel panel;
    
    Integer top = 0;

	public static final AonGwtIssuesCSS CSS = GWT.<AonGwtIssuesResources> create(AonResources.class).css();

	protected abstract void onSelect(JavaScriptObject item);

	public AonListDialog(AonJsArray<JsLabel> labels
			, AonJsArray<JsUser> users, String label) {
		setWidget(binder.createAndBindUi(this));
		String arr= "[";
		if(labels != null)
			for(Integer i = 0; i < labels.length(); i++){
				if(i > 0) arr = arr + " , ";
				arr = arr + "\""+ labels.get(i).getName()+"\"";
			}
		else if(users != null)
			for(Integer i = 0; i < users.length(); i++){
				if(i > 0) arr = arr + " , ";
				arr = arr + "\""+ users.get(i).getLogin()+"\"";
			}
		arr = arr + "]";
		VaadinComboBox vcb = new VaadinComboBox();
		vcb.setItems(arr);
		vcb.setLabel(label);
		vcb.addValueChangedHandler(new ValueChangedEventHandler() {
			
			@Override
			public void onValueChanged(ValueChangedEvent event) {
				if(labels != null)
					for(Integer i = 0; i < labels.length(); i++)
						if(labels.get(i).getName().equals(vcb.getValue())) 
							onSelect(labels.get(i));
					
				if(users != null)
					for(Integer j = 0; j < users.length(); j++)
						if(users.get(j).getLogin().equals(vcb.getValue())) 
							onSelect(users.get(j));
					
			}
		});
		panel.add(vcb);
		addAutoHidePartner(vcb.getElement());	
	}
	
	@Override
	public void show() {
		super.show();
		VaadinComboBox vcb = (VaadinComboBox) panel.getWidget(0);
		vcb.toggle();
	}

}
