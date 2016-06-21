package com.esferalia.aon.gwt.issues.client;

import com.esferalia.aon.gwt.api.client.AonUrlApi;
import com.esferalia.aon.gwt.api.client.JSON;
import com.esferalia.aon.gwt.api.client.incidence.Incidence;
import com.esferalia.aon.gwt.api.client.incidence.JsLabel;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.vaadin.polymer.iron.widget.IronCollapse;

public class TagPanel extends Composite {
	
	protected static String USER_NAME = "aibanez91";
	protected static String ORG_NAME = "aibanez91";
	protected static String REPO_NAME = "repoPrueba6";
	protected static String ACCESS_TOKEN = "d8aa641723e106b5d7c2d79d3cad963e0eb6e92d";
	protected static String DESCRIPTION = "Repositorio de prueba para metodos de TEST";

    interface Binder extends UiBinder<HTMLPanel, TagPanel> {
    	
    }
    
    @UiField Button priorityButton;
    @UiField IronCollapse priorityCollapse;
    
    @UiField Button typeButton;
    @UiField IronCollapse typeCollapse;
    
    @UiField Button tagButton;
    @UiField IronCollapse tagCollapse;
    
    private static Binder binder = GWT.create(Binder.class);
    
    public TagPanel() {
        initWidget(binder.createAndBindUi(this));
        
       getTagList();
    }
    
    protected void getTagList() {
		Incidence i = new Incidence(AonUrlApi.GITHUB, ACCESS_TOKEN, USER_NAME, ORG_NAME, REPO_NAME);
		i.getLabels(new AsyncCallback<JSON<JsLabel>>() {
			
			@Override
			public void onSuccess(JSON<JsLabel> result) {
				
			}
			
			@Override
			public void onFailure(Throwable caught) {}
		});
	}
    
    @UiHandler("priorityButton")
	void priorityButtonClick(ClickEvent event){
    	priorityCollapse.toggle();
    }
    
    @UiHandler("typeButton")
	void typeButtonClick(ClickEvent event){
    	typeCollapse.toggle();
	}
    
    @UiHandler("tagButton")
	void tagButtonClick(ClickEvent event){
    	tagCollapse.toggle();
	}
    
}
