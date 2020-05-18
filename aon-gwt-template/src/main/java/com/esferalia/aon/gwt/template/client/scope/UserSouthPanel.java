package com.esferalia.aon.gwt.template.client.scope;

import java.util.LinkedList;

import com.esferalia.aon.gwt.api.client.AonJsArray;
import com.esferalia.aon.gwt.api.client.JSON;
import com.esferalia.aon.gwt.api.client.common.JsCompany;
import com.esferalia.aon.gwt.api.client.incidence.JsUser;
import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.polymer.AonDialog;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.vaadin.polymer.iron.widget.IronIcon;
import com.vaadin.polymer.paper.widget.PaperIconButton;
import com.vaadin.polymer.paper.widget.PaperInput;
import com.vaadin.polymer.paper.widget.PaperItem;


public class UserSouthPanel extends SouthPanel {

	ScopePrincipal parent;
	
    public UserSouthPanel(ScopePrincipal parent, AonJsArray<JsUser> items, JsCompany o) {   
    	super();
    	this.parent = parent;
    	title.setText("Empresa: " + o.getName() +" - \u00c1mbito: " + o.getScope().getName());
    	title.getElement().getStyle().setFontWeight(FontWeight.BOLD);
        vertical.setWidth("100%");
        if(items.length() > 0){
        	items.stream().forEach(js -> {
            	users.add(js);
        		vertical.add(buildPaperItem(js, o));
        	});
        }
    	vertical.add(nuevoItem(o));
    }
   
    public PaperItem buildPaperItem(JsUser js, JsCompany o){
    	PaperItem pi = new PaperItem();
    	IronIcon ironIcon = new IronIcon();
    	ironIcon.setIcon("account-box");
    	ironIcon.addStyleName(AON.AON_CSS.aonMinWidth24());
    	pi.add(ironIcon);
    	pi.add(new Label(js.getLogin()));
    	pi.setStyle("min-height:24px;font-size:12px;padding:0px;");

    	IronIcon removeIcon = new IronIcon();
    	removeIcon.setTitle("Borrar Usuario");
    	removeIcon.setIcon("remove");
    	removeIcon.getElement().getStyle().setCursor(Cursor.POINTER);
    	removeIcon.addStyleName(AON.AON_CSS.aonMinWidth24());
    	removeIcon.getElement().getStyle().setPosition(Position.ABSOLUTE);
    	removeIcon.getElement().getStyle().setRight(28, Unit.PX);
    	removeIcon.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				AonDialog d= new AonDialog("Desvincular \u00c1mbito", new Label("Est\u00e1s seguro de Desvincular " + js.getLogin() +" de " + o.getScope().getName())) {
					
					@Override
					protected void onCancel() {
						hide();
					}
					
					@Override
					protected void onAccept() {
						JSONObject json = new JSONObject();
						json.put("scope", new JSONString(o.getScope().getId()+ ""));
						json.put("user", new JSONString(js.getId()+ ""));
						String requestData = JsonUtils.stringify(json.getJavaScriptObject());

						parent.getAPI().getCommon().removeUserScope(requestData, new AsyncCallback<JavaScriptObject>() {
							
							@Override
							public void onSuccess(JavaScriptObject result) {
								parent.companySelection(o);
								hide();						
							}
							
							@Override
							public void onFailure(Throwable caught) {
								hide();						
							}
						});
					}
				};
				d.getElement().getStyle().setWidth(255, Unit.PX);
				d.center();
			}
		});
    	pi.add(removeIcon);
    	return pi;
    }
    
    LinkedList<JsUser> users = new LinkedList<JsUser>();
    
    public PaperIconButton nuevoItem(JsCompany o) {
    	PaperIconButton newIcon = new PaperIconButton();
    	newIcon.setIcon("add");
    	newIcon.setTitle("A\u00f1adir Usuario");
    	newIcon.addStyleName(AON.AON_CSS.aonMinWidth24());
    	newIcon.getElement().getStyle().setPosition(Position.ABSOLUTE);
    	newIcon.getElement().getStyle().setRight(20, Unit.PX);
    	newIcon.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				
				
				parent.getAPI().getIncidence().getApplicationUsers(new AsyncCallback<JSON<JsUser>>() {
					
					@Override
					public void onSuccess(JSON<JsUser> result) {
						
						ScrollPanel sp = new ScrollPanel();
						sp.setWidth("100%");
						sp.setHeight("200px");
						VerticalPanel vp = new VerticalPanel();
						vp.setWidth("100%");

						PaperInput pi = new PaperInput();
						pi.setPlaceholder("Filtro");
						pi.addDomHandler(new KeyUpHandler() {
							
							@Override
							public void onKeyUp(KeyUpEvent event) {
								for (Integer i = 1; i < vp.getWidgetCount(); i++) {
									PaperItem pitem = (PaperItem) vp.getWidget(i);
									Label label = (Label) pitem.getWidget(1);
									pitem.setVisible(label.getText().contains(pi.getValue()));
								}
							}
						}, KeyUpEvent.getType());
						vp.add(pi);
						result.getData().stream().forEach(r-> {
							if(!contains(r)) vp.add(buildPaperItem2(r, o));
						});
						sp.add(vp);
						AonDialog d= new AonDialog("Vincular \u00c1mbito", sp) {
							
							@Override
							protected void onCancel() {
								hide();
							}
							
							@Override
							protected void onAccept() {
								JSONArray ar = new JSONArray();
								for(Integer i = 0; i < users.size(); i++) {
									ar.set(i, new JSONString(users.get(i).getId() + ""));
								}
								JSONObject json = new JSONObject();
								json.put("scope", new JSONString(o.getScope().getId()+ ""));
								json.put("users", ar);
								String requestData = JsonUtils.stringify(json.getJavaScriptObject());
								parent.getAPI().getCommon().updateUserScope(requestData, new AsyncCallback<JavaScriptObject>() {
									
									@Override
									public void onSuccess(JavaScriptObject result) {
										parent.companySelection(o);
										hide();						
									}
									
									@Override
									public void onFailure(Throwable caught) {
										hide();						
									}
								});
							}
						};
						d.getElement().getStyle().setWidth(255, Unit.PX);
						d.getElement().getStyle().setHeight(300, Unit.PX);	
						d.center();	
					}
					
					@Override
					public void onFailure(Throwable caught) {
						
					}
				});
				
				
						
			}
		});
    	return newIcon;
    }
    
	public PaperItem buildPaperItem2(JsUser js, JsCompany o) {
		PaperItem pi = new PaperItem();
		IronIcon ironIcon = new IronIcon();
		ironIcon.setIcon("account-box");
		ironIcon.addStyleName(AON.AON_CSS.aonMinWidth24());
		pi.add(ironIcon);
		pi.add(new Label(js.getLogin()));
		pi.setStyle("min-height:24px;font-size:12px;padding:0px;");

		pi.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				if (users.contains(js)) {
					users.remove(js);
					pi.remove(2);
				} else {
					users.add(js);
					IronIcon ii = new IronIcon();
					ii.setIcon("check");
					ii.addStyleName(AON.AON_CSS.aonMinWidth24());
					ii.getElement().getStyle().setPosition(Position.ABSOLUTE);
					ii.getElement().getStyle().setRight(15, Unit.PX);
					pi.add(ii);
				}
			}
		});

		return pi;
	}
    
    private Boolean contains(JsUser js) {
    	Boolean bool = false;
    	for (JsUser us : users) {
			if(!bool &&  js.getId() == us.getId()) {
				bool = true;
			}
		}
    	return bool;
	}
}
