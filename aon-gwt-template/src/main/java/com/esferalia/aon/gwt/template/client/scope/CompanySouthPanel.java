package com.esferalia.aon.gwt.template.client.scope;

import com.esferalia.aon.gwt.api.client.AonJsArray;
import com.esferalia.aon.gwt.api.client.incidence.JsObject;
import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.polymer.AonDialog;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Label;
import com.vaadin.polymer.iron.widget.IronIcon;
import com.vaadin.polymer.paper.widget.PaperIconButton;
import com.vaadin.polymer.paper.widget.PaperItem;


public class CompanySouthPanel extends SouthPanel {

	ScopePrincipal parent;
	
    public CompanySouthPanel(ScopePrincipal parent, AonJsArray<JsObject> items, JsObject o) {   
    	super();
    	this.parent = parent;
    	title.setText("\u00c1mbito: " + o.getName());
    	title.getElement().getStyle().setFontWeight(FontWeight.BOLD);
        vertical.setWidth("100%");
        if(items.length() > 0){
        	items.stream().forEach(js -> {
        		vertical.add(buildPaperItem(js, o));
        	});
        }
       // vertical.add(nuevoItem(o));    
    }
   
    public PaperItem buildPaperItem(JsObject js, JsObject o){
    	PaperItem pi = new PaperItem();
    	IronIcon ironIcon = new IronIcon();
    	ironIcon.setIcon("work");
    	ironIcon.addStyleName(AON.AON_CSS.aonMinWidth24());
    	pi.add(ironIcon);
    	pi.add(new Label(js.getName()));
    	pi.setStyle("min-height:24px;font-size:12px;padding:0px;");
    	
    	IronIcon removeIcon = new IronIcon();
    	removeIcon.setIcon("remove");
    	removeIcon.addStyleName(AON.AON_CSS.aonMinWidth24());
    	removeIcon.getElement().getStyle().setPosition(Position.ABSOLUTE);
    	removeIcon.getElement().getStyle().setRight(28, Unit.PX);
    	removeIcon.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				
				AonDialog d= new AonDialog("Desvincular \u00c1mbito", new Label("Est\u00e1s seguro de Desvincular " + js.getName() +" de " + o.getName())) {
					
					@Override
					protected void onCancel() {
						hide();
					}
					
					@Override
					protected void onAccept() {
						JSONObject json = new JSONObject();
						json.put("company", new JSONString(js.getId()+ ""));
						String requestData = JsonUtils.stringify(json.getJavaScriptObject());

						parent.getAPI().getCommon().removeCompanyScope(requestData, new AsyncCallback<JavaScriptObject>() {
							
							@Override
							public void onSuccess(JavaScriptObject result) {
								//parent.scopeSelection(o);
								hide();						
							}
							
							@Override
							public void onFailure(Throwable caught) {
								hide();						
							}
						});
						hide();						
					}
				};
				d.getElement().getStyle().setWidth(255, Unit.PX);
				d.center();
			}
		});
    	pi.add(removeIcon);
    	return pi;
    }
    
    public PaperIconButton nuevoItem(JsObject o) {
    	PaperIconButton newIcon = new PaperIconButton();
    	newIcon.setIcon("add");
    	newIcon.addStyleName(AON.AON_CSS.aonMinWidth24());
    	newIcon.getElement().getStyle().setPosition(Position.ABSOLUTE);
    	newIcon.getElement().getStyle().setRight(20, Unit.PX);
    	newIcon.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				AonDialog d= new AonDialog("Vincular \u00c1mbito", new Label("")) {
					
					@Override
					protected void onCancel() {
						hide();
					}
					
					@Override
					protected void onAccept() {
						// borrar user_scope
						// volver a cargar lista!
						hide();						
					}
				};
				d.getElement().getStyle().setWidth(255, Unit.PX);
				d.center();
			}
		});
    	return newIcon;
    }

}
