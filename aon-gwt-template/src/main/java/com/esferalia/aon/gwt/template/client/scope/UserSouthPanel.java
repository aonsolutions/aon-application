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
    	hpanel.setHeight("40px");
     	hpanel.setWidth("100%");
    	title.setText("Empresa: " + o.getName() +" - \u00c1mbito: " + o.getScope().getName());
    	title.getElement().getStyle().setFontWeight(FontWeight.BOLD);
    	title.getElement().getStyle().setPosition(Position.ABSOLUTE);
    	title.getElement().getStyle().setTop(15, Unit.PX);
    	
    	PaperIconButton removeGroups = new PaperIconButton();
    	removeGroups.getElement().getStyle().setPosition(Position.ABSOLUTE);
    	removeGroups.getElement().getStyle().setColor("#931A00");
    	removeGroups.getElement().getStyle().setRight(150, Unit.PX);
    	removeGroups.setTitle("Desvincular Grupos");
    	removeGroups.setIcon("social:group");
    	removeGroups.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				removeGroups(o);
			}
		});
        hpanel.add(removeGroups);
        
        PaperIconButton addGroups = new PaperIconButton();
        addGroups.getElement().getStyle().setPosition(Position.ABSOLUTE);
        addGroups.getElement().getStyle().setRight(110, Unit.PX);
        addGroups.setIcon("social:group-add");
        addGroups.setTitle("Vincular Grupos");
        addGroups.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				addGroups(o);
			}
		});
        hpanel.add(addGroups);
    	
        PaperIconButton removeUsers = new PaperIconButton();
        removeUsers.setIcon("social:person");
        removeUsers.getElement().getStyle().setPosition(Position.ABSOLUTE);
        removeUsers.getElement().getStyle().setColor("#931A00");
        removeUsers.getElement().getStyle().setRight(70, Unit.PX);
        removeUsers.setTitle("Desvincular Usuarios");
        removeUsers.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				removeUsers(o);
			}
		});
        hpanel.add(removeUsers);
    	
        PaperIconButton addUsers = new PaperIconButton();
        addUsers.setIcon("social:person-add");
        addUsers.getElement().getStyle().setPosition(Position.ABSOLUTE);
        addUsers.getElement().getStyle().setRight(30, Unit.PX);
        addUsers.setTitle("Vincular Usuarios");
        addUsers.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				addUsers(o);
			}
		});
        hpanel.add(addUsers);
        
        vertical.setWidth("100%");
        if(items.length() > 0){
        	items.stream().forEach(js -> {
        		users.add(js);
        		vertical.add(buildPaperItem(js, o));
        	});
        }
    }
   
    public PaperItem buildPaperItem(JsUser user, JsCompany o){
    	PaperItem pi = new PaperItem();
    	IronIcon ironIcon = new IronIcon();
    	ironIcon.setIcon("account-box");
    	ironIcon.addStyleName(AON.AON_CSS.aonMinWidth24());
    	pi.add(ironIcon);
    	pi.add(new Label((user.getName() != null ? user.getName() + " - " : "") + user.getLogin()));
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
				AonDialog d= new AonDialog("Desvincular \u00c1mbito", new Label("Est\u00e1s seguro de Desvincular " + user.getLogin() +" de " + o.getScope().getName())) {
					
					@Override
					protected void onCancel() {
						hide();
					}
					
					@Override
					protected void onAccept() {
						JSONObject json = new JSONObject();
						json.put("scope", new JSONString(o.getScope().getId()+ ""));
						json.put("user", new JSONString(user.getId()+ ""));
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
    LinkedList<JsUser> removedUsers = new LinkedList<JsUser>();
    
    LinkedList<JsUser> groups = new LinkedList<JsUser>();
    LinkedList<JsUser> removedGroups = new LinkedList<JsUser>();
    
    
    private void addGroups(JsCompany o) {
    	parent.getAPI().getIncidence().getWorkgroups(new AsyncCallback<JSON<JsUser>>() {
			
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
					if(!r.getDescription().equalsIgnoreCase("Sin Asignar"))
						vp.add(buildGroupPaperItem(r, o, true));
				});
				sp.add(vp);
				AonDialog d= new AonDialog("Vincular Grupos", sp) {
					
					@Override
					protected void onCancel() {
						hide();
					}
					
					@Override
					protected void onAccept() {
						JSONArray ar = new JSONArray();
						for(Integer i = 0; i < groups.size(); i++) {
							ar.set(i, new JSONString(groups.get(i).getId() + ""));
						}
						groups = new LinkedList<>();
						JSONObject json = new JSONObject();
						json.put("scope", new JSONString(o.getScope().getId()+ ""));
						json.put("groups", ar);
						String requestData = JsonUtils.stringify(json.getJavaScriptObject());
						parent.getAPI().getCommon().addGroupScope(requestData, new AsyncCallback<JavaScriptObject>() {
							
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
    
    private void removeGroups(JsCompany o) {
    	parent.getAPI().getIncidence().getWorkgroups(new AsyncCallback<JSON<JsUser>>() {
    		
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
					if(!r.getDescription().equalsIgnoreCase("Sin Asignar"))
						vp.add(buildGroupPaperItem(r, o, false));
				});
				sp.add(vp);
				AonDialog d= new AonDialog("Desvincular Usuarios", sp) {
					
					@Override
					protected void onCancel() {
						hide();
					}
					
					@Override
					protected void onAccept() {
						JSONArray ar = new JSONArray();
						for(Integer i = 0; i < removedUsers.size(); i++) {
							ar.set(i, new JSONString(removedUsers.get(i).getId() + ""));
						}
						removedUsers = new LinkedList<>();
						JSONObject json = new JSONObject();
						json.put("scope", new JSONString(o.getScope().getId()+ ""));
						json.put("groups", ar);
						String requestData = JsonUtils.stringify(json.getJavaScriptObject());
						parent.getAPI().getCommon().removeGroupScopes(requestData, new AsyncCallback<JavaScriptObject>() {
							
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
    
    private void addUsers(JsCompany o) {
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
					if(!contains(r)) vp.add(buildUserPaperItem(r, o, true));
				});
				sp.add(vp);
				AonDialog d= new AonDialog("Vincular Usuarios", sp) {
					
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
    
    private void removeUsers(JsCompany o) {
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
					if(contains(r)) vp.add(buildUserPaperItem(r, o, false));
				});
				sp.add(vp);
				AonDialog d= new AonDialog("Desvincular Usuarios", sp) {
					
					@Override
					protected void onCancel() {
						hide();
					}
					
					@Override
					protected void onAccept() {
						JSONArray ar = new JSONArray();
						for(Integer i = 0; i < removedUsers.size(); i++) {
							ar.set(i, new JSONString(removedUsers.get(i).getId() + ""));
						}
						removedUsers = new LinkedList<>();
						JSONObject json = new JSONObject();
						json.put("scope", new JSONString(o.getScope().getId()+ ""));
						json.put("users", ar);
						String requestData = JsonUtils.stringify(json.getJavaScriptObject());
						parent.getAPI().getCommon().removeUserScopes(requestData, new AsyncCallback<JavaScriptObject>() {
							
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
    
	public PaperItem buildUserPaperItem(JsUser js, JsCompany o, Boolean add) {
		PaperItem pi = new PaperItem();
		pi.getElement().getStyle().setCursor(Cursor.POINTER);
		IronIcon ironIcon = new IronIcon();
		ironIcon.setIcon("account-box");
		ironIcon.addStyleName(AON.AON_CSS.aonMinWidth24());
		pi.add(ironIcon);
		pi.add(new Label(js.getLogin()));
		pi.setStyle("min-height:24px;font-size:12px;padding:0px;");

		pi.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				if ((add && users.contains(js)) || (!add && removedUsers.contains(js))) {
					if(add) users.remove(js);
					else removedUsers.remove(js);
					pi.remove(2);
				} else {
					if(add) users.add(js);
					else removedUsers.add(js);
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
	
	public PaperItem buildGroupPaperItem(JsUser js, JsCompany o, Boolean add) {
		PaperItem pi = new PaperItem();
		pi.getElement().getStyle().setCursor(Cursor.POINTER);
		IronIcon ironIcon = new IronIcon();
		ironIcon.setIcon("group-work");
		ironIcon.addStyleName(AON.AON_CSS.aonMinWidth24());
		pi.add(ironIcon);
		pi.add(new Label(js.getDescription()));
		pi.setStyle("min-height:24px;font-size:12px;padding:0px;");

		pi.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				if ((add && groups.contains(js)) || (!add && removedGroups.contains(js))) {
					if(add) groups.remove(js);
					else removedGroups.remove(js);
					pi.remove(2);
				} else {
					if(add) groups.add(js);
					else removedGroups.add(js);
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
