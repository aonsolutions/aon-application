package com.esferalia.aon.gwt.template.client.scope;

import java.util.LinkedList;

import com.esferalia.aon.gwt.api.client.AonJsArray;
import com.esferalia.aon.gwt.api.client.JSON;
import com.esferalia.aon.gwt.api.client.common.JsCompany;
import com.esferalia.aon.gwt.api.client.incidence.JsObject;
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


public class CompanySouthPanel extends SouthPanel {

	ScopePrincipal parent;
	
	Integer user;
    LinkedList<JsUser> users = new LinkedList<JsUser>();
    LinkedList<JsUser> removedUsers = new LinkedList<JsUser>();
    
    LinkedList<JsUser> groups = new LinkedList<JsUser>();
    LinkedList<JsUser> removedGroups = new LinkedList<JsUser>();
    
    LinkedList<JsUser> companies = new LinkedList<JsUser>();
    LinkedList<JsUser> removedCompanies = new LinkedList<JsUser>();
	
    public CompanySouthPanel(ScopePrincipal parent, AonJsArray<JsCompany> items, JsUser o) {   
    	super();
    	this.parent = parent;
    	hpanel.setHeight("40px");
     	hpanel.setWidth("100%");
    	title.setText("Usuario: " + o.getLogin());
    	title.getElement().getStyle().setFontWeight(FontWeight.BOLD);
    	title.getElement().getStyle().setPosition(Position.ABSOLUTE);
    	title.getElement().getStyle().setTop(15, Unit.PX);

    	PaperIconButton removeGroups = new PaperIconButton();
    	removeGroups.setDisabled(true);
    	removeGroups.getElement().getStyle().setPosition(Position.ABSOLUTE);
    	//removeGroups.getElement().getStyle().setColor("#931A00");
    	removeGroups.getElement().getStyle().setRight(230, Unit.PX);
    	removeGroups.setTitle("Desvincular \u00c1mbitos de Grupos");
    	removeGroups.setIcon("social:group");
    	removeGroups.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				removeGroups(o);
			}
		});
        hpanel.add(removeGroups);
    	
        PaperIconButton addGroups = new PaperIconButton();
        addGroups.setDisabled(true);
        addGroups.getElement().getStyle().setPosition(Position.ABSOLUTE);
        addGroups.getElement().getStyle().setRight(190, Unit.PX);
        addGroups.setIcon("social:group-add");
        addGroups.setTitle("Vincular \u00c1mbitos de Grupos");
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
        removeUsers.getElement().getStyle().setRight(150, Unit.PX);
        removeUsers.setTitle("Desvincular \u00c1mbitos de Usuarios");
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
        addUsers.getElement().getStyle().setRight(110, Unit.PX);
        addUsers.setTitle("Vincular \u00c1mbitos de Usuarios");
        addUsers.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				addUsers(o);
			}
		});
        hpanel.add(addUsers);
        
        PaperIconButton removeCompanies = new PaperIconButton();
        removeCompanies.setDisabled(true);
        removeCompanies.setIcon("aon-communication:domain-disabled");
        removeCompanies.getElement().getStyle().setPosition(Position.ABSOLUTE);
        //removeCompanies.getElement().getStyle().setColor("#931A00");
        removeCompanies.getElement().getStyle().setRight(70, Unit.PX);
        removeCompanies.setTitle("Desvincular Empresas");
        removeCompanies.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				removeCompanies(o);
			}
		});
        hpanel.add(removeCompanies);
        
        PaperIconButton addCompanies = new PaperIconButton();
        addCompanies.setDisabled(true);
        addCompanies.setIcon("aon-communication:business");
        addCompanies.getElement().getStyle().setPosition(Position.ABSOLUTE);
        addCompanies.getElement().getStyle().setRight(30, Unit.PX);
        addCompanies.setTitle("Vincular Empresas");
        addCompanies.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				addCompanies(o);
			}
		});
        hpanel.add(addCompanies);
        
    	vertical.setWidth("100%");
        if(items.length() > 0){
        	items.stream().forEach(js -> {
        		vertical.add(buildPaperItem(js, o));
        	});
        }
    }
   
    public PaperItem buildPaperItem(JsCompany js, JsUser o){
    	PaperItem pi = new PaperItem();
    	IronIcon ironIcon = new IronIcon();
    	ironIcon.setIcon("work");
    	ironIcon.addStyleName(AON.AON_CSS.aonMinWidth24());
    	pi.add(ironIcon);
    	pi.add(new Label(js.getName()));
    	pi.setStyle("min-height:24px;font-size:12px;padding:0px;");
    	
    	IronIcon removeIcon = new IronIcon();
    	removeIcon.setTitle("Borrar");
    	removeIcon.setIcon("remove");
    	removeIcon.getElement().getStyle().setCursor(Cursor.POINTER);
    	removeIcon.addStyleName(AON.AON_CSS.aonMinWidth24());
    	removeIcon.getElement().getStyle().setPosition(Position.ABSOLUTE);
    	removeIcon.getElement().getStyle().setRight(28, Unit.PX);
    	removeIcon.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				
				AonDialog d= new AonDialog("Desvincular \u00c1mbito", new Label("Est\u00e1s seguro de Desvincular " + js.getName() +" de " + o.getLogin())) {
					
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
    	//pi.add(removeIcon);
    	return pi;
    }
    
    public PaperIconButton nuevoItem(JsObject o) {
    	PaperIconButton newIcon = new PaperIconButton();
    	newIcon.setIcon("add");
    	newIcon.setTitle("A\u00f1adir");
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
    
    
    private void addGroups(JsUser user) {

	}
    
    private void removeGroups(JsUser user) {

	}
    
    private void addUsers(JsUser user) {
    	action(user, true);
	}
    
    private void removeUsers(JsUser user) {	
    	action(user, false);
	}

	private void action(JsUser object, Boolean isCopy) {	
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
					PaperItem pi2 = new PaperItem();
					IronIcon ironIcon = new IronIcon();
					ironIcon.setIcon("account-box");
					ironIcon.addStyleName(AON.AON_CSS.aonMinWidth24());
					pi2.add(ironIcon);
					pi2.add(new Label(r.getLogin()));
					pi2.setStyle("min-height:24px;font-size:12px;padding:0px;");

					pi2.addClickHandler(new ClickHandler() {

						@Override
						public void onClick(ClickEvent event) {
							for(Integer k = 1; k < vp.getWidgetCount(); k++ ) {
								PaperItem pi3 = (PaperItem) vp.getWidget(k);
								if(pi3.getWidgetCount() > 2) pi3.remove(2);
							}
							user = r.getId();
							IronIcon ii = new IronIcon();
							ii.setIcon("check");
							ii.addStyleName(AON.AON_CSS.aonMinWidth24());
							ii.getElement().getStyle().setPosition(Position.ABSOLUTE);
							ii.getElement().getStyle().setRight(15, Unit.PX);
							pi2.add(ii);
						}
					});
					vp.add(pi2);
				});
				sp.add(vp);
				AonDialog d= new AonDialog(isCopy ? "Copiar \u00c1mbitos de" : "Desvincular \u00c1mbitos de" , sp) {
					
					@Override
					protected void onCancel() {
						hide();
					}
					
					@Override
					protected void onAccept() {
						hide();
						if(isCopy) {
							parent.getAPI().getCommon().copyUserScope(object.getId(), user,  new AsyncCallback<JSON<JsUser>>() {
								@Override public void onSuccess(JSON<JsUser> result) {
									parent.userSelection(object);
								}
								@Override public void onFailure(Throwable caught) {
									parent.userSelection(object);
								}
							});
						} else {
							parent.getAPI().getCommon().deleteUserScope(object.getId(), user,  new AsyncCallback<JSON<JsUser>>() {
								@Override public void onSuccess(JSON<JsUser> result) {
									parent.userSelection(object);
								}
								@Override public void onFailure(Throwable caught) {
									parent.userSelection(object);
								}
							});
						}
						
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
    
    private void addCompanies(JsUser user) {

	}
    
    private void removeCompanies(JsUser user) {

	}

}
